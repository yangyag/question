package com.gsc.kixxhub.device.pumpa.driver;

import gnu.io.CommPortOwnershipListener;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.util.Calendar;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.Vector;

import com.gsc.kixxhub.common.data.pump.HC_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HD_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P2_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P5_1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P5_NozzleInfo;
import com.gsc.kixxhub.common.data.pump.P6_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P7_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S3_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S4_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S8_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SE_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.TR_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.Change;
import com.gsc.kixxhub.device.pumpa.common.SerialConnectException;
import com.gsc.kixxhub.device.pumpa.common.Sleep;
import com.gsc.kixxhub.device.pumpa.define.CCommDriver;
import com.gsc.kixxhub.device.pumpa.translation.TransSomoSelf;

public class CommPrimeSelfODT extends CCommDriver implements SerialPortEventListener, CommPortOwnershipListener, 
	Runnable {

    protected int		buffSize = 1024;
	
	protected int 	dispLevel=0;
	protected boolean firstRequest=true;
	protected HC_WorkingMessage HC_wm;
	protected boolean	issueLineErr=true;
	protected byte[] 	lastPumpingData=new byte[buffSize];
	protected int		lineCommCheckCnt=0;
	protected int		lineErrCnt=0;
	protected String 	m_realBasePrice="000000"; // 실 주유단가
	protected String 	m_realLiter="0000000";    // 실 주유량
	protected String 	m_realPrice="00000000";   // 실 주유금액
	protected int		m_statusCode=601;
	
    protected int		minErrCnt=8, maxSkipCnt=100;
    protected byte		nozID;
    protected short		nozState=0;
    protected String 	nozStr = "";
    protected boolean P1_sent=false;
    protected P1_WorkingMessage P1_wm;
    protected boolean P2_sent=false;
    
	protected P2_WorkingMessage P2_wm; 
	protected boolean P5_1_recved=false; 
	protected P5_1_WorkingMessage P5_1_wm; 
	protected boolean P6_sent=false;
	
	protected P6_WorkingMessage P6_wm;
	protected PB_WorkingMessage PB_wm;
    //progressStep : 0=초기화전, 1=초기화후, 2=상태정보송신후, 3=주유중, 4=주유완료, 
    //               5=토털게이지수신/주유완료자료 송신
    protected int 	progressStep=3;
	protected byte[] 	rcvInitBuf = new byte[6];
	protected int		readBuffInterval   = 200;
	protected int		readStartInterval  = 100;
	protected byte[] 	RxBuf = new byte[buffSize];
	protected S3_WorkingMessage S3_wm;
	protected S4_WorkingMessage S4_wm;
	protected S8_WorkingMessage S8_wm;
	protected SE_WorkingMessage SE_wm;
	protected TR_WorkingMessage TR_wm;

	//protected WorkingMessage		wmUp;
	protected TransSomoSelf trans = new TransSomoSelf();
	protected byte[] 	TxBuf = new byte[buffSize];
	protected int		writeStartInterval = 100;
    
    public CommPrimeSelfODT (int nozNum, String romVerStr) {
    	
    	byte	bcc;
		Formatter form = new Formatter ();
		byte[] 	nozByt = new byte[2];
		
		nozNo = nozNum;
		romVer = romVerStr;
		
		form.format("%02d", nozNo);
		nozStr = form.toString();
		nozByt = nozStr.getBytes();
		nozID = (byte) (nozNum - 1 + 0x40);
		
    }
    
	protected byte[] generateByteStream (WorkingMessage wm) throws Exception {			
		return trans.generateByteStream(wm);
	}
	
	protected WorkingMessage generateWorkingMessage (byte[] buf, String command) throws Exception {
		return trans.generateWorkingMessage(buf, command);
	}
	
	protected boolean isSkipWorkingMessage (WorkingMessage wm) throws Exception {
		
		boolean skip=false;
				
		if (wm.getCommand().equals("P1")) { // 셀프 환경설정(사업장 정보)
			
			P1_wm = (P1_WorkingMessage) wm.clone();
//			LogUtility.getPumpALogger().info("ODT P1 수신 >>> ODT_No=" + nozStr);
			
			if (P5_1_recved==true) {
				send_P1_workingMessage();
			}
			
		}
		else if (wm.getCommand().equals("P2")) { // 셀프 환경설정(머리말/꼬리말)
			
			P2_wm = (P2_WorkingMessage) wm.clone();
//			LogUtility.getPumpALogger().info("ODT P2 수신 >>> ODT_No=" + nozStr);

			if (P5_1_recved==true) {
				send_P2_workingMessage();
			}
		}
//		else if (wm.getCommand().equals("P6")) { // 셀프 환경설정(시스템시각)
//			
//			P6_wm = (P6_WorkingMessage) wm.clone();
//			LogUtility.getPumpALogger().info("111_ODT>>> P6 수신 nozNo=" + nozStr);
//
//			if (P5_1_recved==true) {
//				send_P6_workingMessage();
//			}
//		}
		else if (wm.getCommand().equals("P5_1")) { // 셀프 환경설정(노즐정보)
			
			P5_1_wm = (P5_1_WorkingMessage) wm;
			Vector<P5_NozzleInfo> nozInfoVec = P5_1_wm.getNozzleInfo();
			int nozCnt = nozInfoVec.size();

//			LogUtility.getPumpALogger().info("ODT P5_1 수신 >>> ODT_No=" + P5_1_wm.getNozzleNo() + 
//					" mode=" + P5_1_wm.getMode());
			
			for (int i=0; i<nozCnt; i++) {
				
				P5_1_WorkingMessage P5_1_wm2 = (P5_1_WorkingMessage) P5_1_wm.clone();
				P5_NozzleInfo P5_nozWm = nozInfoVec.get(i);
				P5_1_wm2.setNozzleNo(P5_1_wm.getNozzleNo());
				P5_1_wm2.setTargetNozzleNo(P5_nozWm.getNozzleNumber());
				P5_1_wm2.setPassThrough(false);

//				LogUtility.getPumpALogger().info("ODT P5_1 송신 >>> ODT_No=" + P5_1_wm2.getNozzleNo() + 
//						" -> tarNoz=" + P5_1_wm2.getTargetNozzleNo());

				insertRecvQueue(P5_1_wm2); 
			}

			if (P1_sent==false && P1_wm != null) {
				send_P1_workingMessage();
			}
			if (P2_sent==false && P2_wm != null) {
				send_P2_workingMessage();
			}
//			if (P6_sent==false && P6_wm != null) {
//				send_P6_workingMessage();
//			}
			
			P5_1_recved = true;
		}
		else if (wm.getCommand().equals("HC")) { // 외상외 승인응답

			HC_WorkingMessage HC_wm = (HC_WorkingMessage) wm;
			
			HC_wm.setNozzleNo(HC_wm.getNozzleNo());
			HC_wm.setTargetNozzleNo(HC_wm.getConnectNozzleNo());
			HC_wm.setPassThrough(false);

			insertRecvQueue(HC_wm); 
			
			LogUtility.getPumpALogger().info(new StringBuffer("\n동화셀프ODT 수신전문 : " ).append( HC_wm.getCommand() )
										 .append("\n ODT_No=" ).append( HC_wm.getNozzleNo() )
										 .append("\n nozzle=" ).append( HC_wm.getConnectNozzleNo() )
										 .append("\n mode  =[" ).append(HC_wm.getMode() ).append( "]\n").toString());
			//HC_wm.print();
										 
		}
		else if (wm.getCommand().equals("HD")) { // 외상 승인응답

			HD_WorkingMessage HD_wm = (HD_WorkingMessage) wm;
			
			HD_wm.setNozzleNo(HD_wm.getNozzleNo());
			HD_wm.setTargetNozzleNo(HD_wm.getConnectNozzleNo());
			HD_wm.setPassThrough(false);

			insertRecvQueue(HD_wm); 

			LogUtility.getPumpALogger().info(new StringBuffer("\n동화셀프ODT 수신전문 : " ).append( HD_wm.getCommand() )
										 .append("\n ODT_No=" ).append( HD_wm.getNozzleNo() )
										 .append("\n nozzle=" ).append( HD_wm.getConnectNozzleNo() ).append( "\n").toString());
			//HD_wm.print();
			
		}
		else if (wm.getCommand().equals("P7")) { // 주유기 파라미터 설정

			P7_WorkingMessage P7_wm = (P7_WorkingMessage) wm;
			readBuffInterval   = readBuffInterval + Change.toValue(P7_wm.getReadBuffInterval());
			readStartInterval  = readStartInterval + Change.toValue(P7_wm.getReadStartInterval());
			writeStartInterval = writeStartInterval + Change.toValue(P7_wm.getWriteStartInterval());
			minErrCnt 		   = minErrCnt + Change.toValue(P7_wm.getLineErrorCount());
			//maxSkipCnt 		   = maxSkipCnt + Change.toValue(P7_wm.getLineErrorSkipCount());
			int lineErrSkipCnt = Change.toValue(P7_wm.getLineErrorSkipCount());
			if (lineErrSkipCnt >= 9000 && lineErrSkipCnt <= 9003)
				dispLevel = lineErrSkipCnt - 9000; // dispLevel : 0 ~ 3
			else
				maxSkipCnt = maxSkipCnt + lineErrSkipCnt;
						
		}
		
		skip=true;
		
		return skip;
	}
	
	protected void makeLineError () throws Exception {
		
		SE_wm = new SE_WorkingMessage();
		
		SE_wm.setNozzleNo(Change.toString("%02d", nozNo));
		SE_wm.setDeviceType(Change.toString("%02d", nozType));
		SE_wm.setStatus("1");
		SE_wm.setStatusCode("601"); // 회선불량
		SE_wm.setErrMsg("셀프ODT 회선불량");
		
		m_statusCode=601; // appended

		Calendar cal = new GregorianCalendar();
		Formatter form = new Formatter();
		String year = Change.toString("%04d", cal.get(Calendar.YEAR));
		form.format("%s%02d%02d%02d%02d%02d", year.substring(2, 4),
				cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH), 
				cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), 
				cal.get(Calendar.SECOND));
		String timeStr = form.toString();		
		SE_wm.setDetectTime(timeStr);
		
		insertRecvQueue(SE_wm);
	}

	protected void makeStatusInfo(int nozState) throws Exception {

		S8_wm = new S8_WorkingMessage();
		int	statusCode=0;
		String errorMsg="";
		
		switch (nozState) {
			case 650:
				statusCode = 650; // 정상
				errorMsg = "셀프ODT 정상";
				break;
		}
		m_statusCode = statusCode;
		
		S8_wm.setNozzleNo(Change.toString("%02d", nozNo));
		S8_wm.setDeviceType(Change.toString("%02d", nozType));
		S8_wm.setStatus("1");
		S8_wm.setStatusCode(Change.toString("%03d", statusCode)); 
		S8_wm.setErrMsg(errorMsg);
		
		Calendar cal = new GregorianCalendar();
		Formatter form = new Formatter();
		String year = Change.toString("%04d", cal.get(Calendar.YEAR));
		form.format("%s%02d%02d%02d%02d%02d", year.substring(2, 4),
				cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH), 
				cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), 
				cal.get(Calendar.SECOND));
		String timeStr = form.toString();
		S8_wm.setDetectTime(timeStr);

		insertRecvQueue(S8_wm);
	}
	
	public void ownershipChange(int arg0) {
		// TODO Auto-generated method stub
	}
	
//	protected void send_P6_workingMessage () throws Exception {
//		
//		Vector<P5_NozzleInfo> nozInfoVec = P5_1_wm.getNozzleInfo();
//		P5_NozzleInfo P5_nozWm = nozInfoVec.get(0);
//		
//		P6_wm.setNozzleNo(P5_1_wm.getNozzleNo());
//		P6_wm.setTargetNozzleNo(P5_nozWm.getNozzleNumber());
//		P6_wm.setPassThrough(false);
//
//		insertRecvQueue(P6_wm);
//		
//		LogUtility.getPumpALogger().info("111_ODT>>> P6 송신 to nozNo=" + P6_wm.getNozzleNo() +
//				" tarNozNo=" + P6_wm.getTargetNozzleNo());
//		
//		P6_sent=true;
//	}
	
	@Override
	public void requestData() throws Exception, SerialConnectException {

		if (firstRequest==true) {
			firstRequest=false;
			makeStatusInfo(650);
		}
		
		Sleep.sleep(10);
		
		try {
			
			//--- Transfer from WorkingMessage to BytesStream
			while (sndQue.getItemCount() > 0) {
				WorkingMessage wm = (WorkingMessage)sndQue.deQueue();
				
				LogUtility.getPumpALogger().debug("\n>>>>> Received  Down data : " +
									"ODT_No=" + nozNo + " command=" + wm.getCommand());
	
				if (isSkipWorkingMessage(wm)==true) {
					wm=null;
					continue;
				}
			}
			if (dispLevel>=2)
				LogUtility.getPumpALogger().debug("\nStart request ===========> (ODT="+nozNo+")");

		} catch (Exception e) {
			LogUtility.getPumpALogger().error("Exception occurr! (ODT="+nozNo+")");
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	}
	
	public void run() {

	}
	
	protected void send_P1_workingMessage () throws Exception {

		Vector<P5_NozzleInfo> nozInfoVec = P5_1_wm.getNozzleInfo();
		P5_NozzleInfo P5_nozWm = nozInfoVec.get(0);
		
		P1_wm.setNozzleNo(P5_1_wm.getNozzleNo());
		P1_wm.setTargetNozzleNo(P5_nozWm.getNozzleNumber());
		P1_wm.setPassThrough(false);

		insertRecvQueue(P1_wm);
		
//		LogUtility.getPumpALogger().info("ODT P1 송신 >>> ODT_No=" + P1_wm.getNozzleNo() +
//				" -> tarNozNo=" + P1_wm.getTargetNozzleNo());

		P1_sent=true;
	}

	protected void send_P2_workingMessage () throws Exception {
		
		Vector<P5_NozzleInfo> nozInfoVec = P5_1_wm.getNozzleInfo();
		P5_NozzleInfo P5_nozWm = nozInfoVec.get(0);
		
		P2_wm.setNozzleNo(P5_1_wm.getNozzleNo());
		P2_wm.setTargetNozzleNo(P5_nozWm.getNozzleNumber());
		P2_wm.setPassThrough(false);

		insertRecvQueue(P2_wm);
		
//		LogUtility.getPumpALogger().info("ODT P2 송신 >>> ODT_No=" + P2_wm.getNozzleNo() +
//				" -> tarNozNo=" + P2_wm.getTargetNozzleNo());
		
		P2_sent=true;
	}

	// To be invoked when InputStream(is) has a receiving data
	public void serialEvent(SerialPortEvent event) {

	}
}

