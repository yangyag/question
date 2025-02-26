/*
 * GS 표준셀프 가상 주유기 프로토콜
 * PI2-박동화, 2015-11-18 작성
 * 
 * ODT 가 주유기를 제어하는 방식으로 모든 전문은 ODT를 통해 송수신한다.
 * 데이터 송수신은 동기화를 위해 ODT를 순차적으로 Polling 한다.(노즐은 Polling 않음)
 * 가상 노즐을 생성하여 모듈에서 수신되는 전문을 ByPass 처리한다.
 */
package com.gsc.kixxhub.device.pumpa.driver;

import java.util.Formatter;

import com.gsc.kixxhub.common.data.pump.S8_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.SerialConnectException;
import com.gsc.kixxhub.device.pumpa.common.Sleep;
import com.gsc.kixxhub.device.pumpa.define.CCommDriver;
import com.gsc.kixxhub.device.pumpa.translation.TransGSSelfOdt;


public class CommGSSelfVNoz extends CCommDriver {

    protected int		buffSize = 1024;
	
	protected int 	dispLevel=0;
	
    protected boolean firstRequest=true;
    protected boolean	issueLineErr=true;
    protected byte[] 	lastPumpingData=new byte[buffSize];
    
	protected int		lineCommCheckCnt=0; 
	protected int		lineErrCnt=0; 
	protected int		m_statusCode=601; 
	protected int		minErrCnt=8, maxSkipCnt=100;
	
	protected byte		nozID;
	protected short		nozState=0;
    protected String 	nozStr = "";
	//progressStep : 0=초기화전, 1=초기화후, 2=상태정보송신후, 3=주유중, 4=주유완료, 
    //               5=토털게이지수신/주유완료자료 송신
    protected int 	progressStep=3;
	protected byte[] 	rcvInitBuf = new byte[6];
	protected int		readBuffInterval   = 200;
	protected int		readStartInterval  = 100;
	protected byte[] 	RxBuf = new byte[buffSize];
	protected S8_WorkingMessage S8_wm;
	//protected WorkingMessage		wmUp;
	protected TransGSSelfOdt trans = new TransGSSelfOdt();
	protected byte[] 	TxBuf = new byte[buffSize];
	protected int		writeStartInterval = 100;
    
    public CommGSSelfVNoz (int nozNum, String romVerStr) {
    	
    	byte	bcc;
		Formatter form = new Formatter ();
		byte[] 	nozByt = new byte[2];
		
		nozNo = nozNum;
		romVer = romVerStr;
		
		form.format("%02d", nozNo);
		nozStr = form.toString();
		nozByt = nozStr.getBytes();
		nozID = (byte) (nozNum - 1 + 0x40);

		super.setInterface_method(1); // 통신방식 1=TCP/IP (DrvierScheduler 인식용, 실제 통신은 하지않음)
    }
    
	protected byte[] generateByteStream (WorkingMessage wm) throws Exception {			
		return trans.generateByteStream(wm);
	}
	
	protected WorkingMessage generateWorkingMessage (byte[] buf, String command) throws Exception {
		return trans.generateWorkingMessage(buf, command);
	}
	
//	protected void makeStatusInfo(int nozState) throws Exception {
//
//		S8_wm = new S8_WorkingMessage();
//		int	statusCode=0;
//		String errorMsg="";
//		
//		switch (nozState) {
//			case 650:
//				statusCode = 651; // 노즐다운
//				errorMsg = "노즐다운";
//				break;
//		}
//		m_statusCode = statusCode;
//		
//		S8_wm.setNozzleNo(Change.toString("%02d", nozNo));
//		S8_wm.setDeviceType(Change.toString("%02d", nozType));
//		S8_wm.setStatus("1");
//		S8_wm.setStatusCode(Change.toString("%03d", statusCode)); 
//		S8_wm.setErrMsg(errorMsg);
//		
//		Calendar cal = new GregorianCalendar();
//		Formatter form = new Formatter();
//		String year = Change.toString("%04d", cal.get(Calendar.YEAR));
//		form.format("%s%02d%02d%02d%02d%02d", year.substring(2, 4),
//				cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH), 
//				cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), 
//				cal.get(Calendar.SECOND));
//		String timeStr = form.toString();
//		S8_wm.setDetectTime(timeStr);
//
//		insertRecvQueue(S8_wm);
//	}

	
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
//		LogUtility.getLogger().info("111_ODT>>> P6 송신 to nozNo=" + P6_wm.getNozzleNo() +
//				" tarNozNo=" + P6_wm.getTargetNozzleNo());
//		
//		P6_sent=true;
//	}
	
	protected boolean isSkipWorkingMessage (WorkingMessage wm) throws Exception {
		
		boolean skip=false;
				
/*		if (wm.getCommand().equals("PB")) { // 주유허가

			PB_WorkingMessage PB_wm = (PB_WorkingMessage) wm;
			
			PB_wm.setNozzleNo(PB_wm.getNozzleNo());
			PB_wm.setTargetNozzleNo(PB_wm.getConnectNozzleNo());
			PB_wm.setPassThrough(false);

			insertRecvQueue(PB_wm); 
			
			LogUtility.getLogger().info("주유허가(PB) 수신 후 ODT로 전송함...");
			PB_wm.print();
			
		}
		else if (wm.getCommand().equals("PA")) { // 노즐제어 요청

			PA_WorkingMessage PA_wm = (PA_WorkingMessage) wm;
			
			PA_wm.setNozzleNo(PA_wm.getNozzleNo());
			PA_wm.setTargetNozzleNo(PA_wm.getConnectNozzleNo());
			PA_wm.setPassThrough(false);

			insertRecvQueue(PA_wm); 
			
			LogUtility.getLogger().info("노즐제어 요청(PA) 수신 후 ODT로 전송함...");
			PA_wm.print();
			
		}
		else if (wm.getCommand().equals("P3_1")) { // 주유기 환경설정

			P3_1_WorkingMessage P3_1_wm = (P3_1_WorkingMessage) wm;
			
			P3_1_wm.setNozzleNo(P3_1_wm.getNozzleNo());
			P3_1_wm.setTargetNozzleNo(P3_1_wm.getConnectNozzleNo());
			P3_1_wm.setPassThrough(false);

			insertRecvQueue(P3_1_wm); 
			
			LogUtility.getLogger().info("주유기 환경설정(P3_1) 수신 후 ODT로 전송함...");
			P3_1_wm.print("");
			
		}*/
		
		skip=true;
		
		return skip;
	}
	
	@Override
	public void requestData() throws Exception, SerialConnectException {

//		if (firstRequest==true) {
//			firstRequest=false;
//			makeStatusInfo(650);
//		}
		
		Sleep.sleep(10);
		
		try {
			
			//--- Transfer from WorkingMessage to BytesStream
			while (sndQue.getItemCount() > 0) {
				WorkingMessage wm = (WorkingMessage)sndQue.deQueue();
				
				//LogUtility.getLogger().debug("## Received  Down data : " +
				//					"nozNo=" + nozNo + " command=" + wm.getCommand());
	
				if (isSkipWorkingMessage(wm)==true) {
					wm=null;
					continue;
				}
			}
			if (dispLevel>=2)
				LogUtility.getLogger().debug("\nStart request ===========> (ODT="+nozNo+")");

		} catch (Exception e) {
			LogUtility.getLogger().error("Exception occurr! (ODT="+nozNo+")");
			LogUtility.getLogger().error(e.getMessage(), e);
		}
	}

	// To be invoked when InputStream(is) has a receiving data
//	public void serialEvent(SerialPortEvent event) {
//
//	}


}

