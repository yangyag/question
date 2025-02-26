package com.gsc.kixxhub.device.pumpa.driver;

import gnu.io.CommPortOwnershipListener;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.util.Calendar;
import java.util.Formatter;
import java.util.GregorianCalendar;

import com.gsc.kixxhub.common.data.pump.HF_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P3_1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P7_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S3_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S4_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S5_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S8_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SE_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SJ_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.Change;
import com.gsc.kixxhub.device.pumpa.common.DataStruct;
import com.gsc.kixxhub.device.pumpa.common.Log;
import com.gsc.kixxhub.device.pumpa.common.SerialConnectException;
import com.gsc.kixxhub.device.pumpa.common.Sleep;
import com.gsc.kixxhub.device.pumpa.define.CCommDriver;
import com.gsc.kixxhub.device.pumpa.translation.TransTatsuno;

public class CommTatsuno extends CCommDriver implements SerialPortEventListener, CommPortOwnershipListener, 
	Runnable {

	protected static byte[]  g_byPumpLockState = new byte[2];
	protected byte ACK = 0x06;
	protected byte[] 	ack_Buf  = new byte[5];
	protected int		baseMinErrCnt=8, baseMaxSkipCnt=100;
	protected int		baseNozNo=0;
	protected int		baseReadBuffInterval   = 30+5; // 스마트주유소 PJT 보정
	protected int		baseReadStartInterval  = 20+3; // 스마트주유소 PJT 보정
	protected int		baseWriteStartInterval = 20+3; // 스마트주유소 PJT 보정
	
    protected boolean beforeNozzleUp=false; // 이전 노즐업여부
    protected int		buffSize = 500;
    protected byte CMD_PUMP_DATA = 0x00;
    protected byte CMD_TOTAL_GAUGE = 0x20;
    protected int	dispLevel=0;
    protected byte ENQ = 0x05;
    protected byte[] 	enq_Buf  = new byte[6];
    protected byte EOT = 0x04;
    protected byte ETX = 0x03;
    protected boolean firstPumpingData=true;
    protected HF_WorkingMessage HF_wm;
    protected boolean	issueLineErr=true;

	protected String	last_SJ_TotalGauge="0000000000";
	protected byte[] 	lastGaugeData  =new byte[15];
    protected byte[] 	lastPumpingData=new byte[19];
	protected int		lineCommCheckCnt=0;
	
	protected int		lineErrCnt=0;
	protected String 	m_basePrice="111100";
    protected boolean 	m_bOnLine;
	protected boolean 	m_bPreset=false;
	protected boolean 	m_bTotalGauge=false;
	protected boolean	m_lineCommState=true;
	protected int		m_nLiter=0;
	protected int		m_nNozzleState = 0;
	protected boolean 	m_nozLock=false;
	protected int		m_nPrice=0;
    protected boolean 	m_recvedNozLock=false;
    protected int		m_statusCode=601;
	protected byte NAK = 0x15;
	protected boolean newPumpingData=false; // 이전 주유값 여부
	
	protected byte		nozID;
	protected int		nozNo1_16;
	protected short		nozState=0;
	protected String 	nozStr = "";
	protected byte PACK= 0x02;
	protected boolean presetDataFlag=false;
	//progressStep : 0=초기화전, 1=초기화후, 2=상태정보송신후, 3=주유중, 4=주유완료, 
    //               5=토털게이지수신/주유완료자료 송신
    protected int 	progressStep=3;
	protected boolean pumpingStart=false;
	protected byte QACK= 0x01;
	protected boolean realPumpingStart=false; // 오일토출
	protected boolean rtn;

	protected byte[] 	RxBuf = new byte[buffSize];
	
	protected S3_WorkingMessage S3_wm;
	protected S4_WorkingMessage S4_wm; 
	protected S5_WorkingMessage S5_wm;
	protected S8_WorkingMessage S8_wm;
	protected SE_WorkingMessage SE_wm;
	
    protected boolean sentNozDownStateInfo=false;
    protected boolean sentNozUpStateInfo=false;
    protected boolean sentPumpingStartInfo=false;
    
	protected SJ_WorkingMessage SJ_wm;
	protected byte STX = (byte)0x82;
	
	protected TransTatsuno trans = new TransTatsuno();
	protected byte[] 	TxBuf = new byte[buffSize];
	protected byte[] 	x00r_Buf = new byte[19];
	protected DataStruct x00r_ds = new DataStruct(); // responded pumping data
	protected byte[] 	x00s_Buf = new byte[4];
	protected DataStruct x00s_ds = new DataStruct(); // request pumping data
	protected byte[] 	x01s_Buf = new byte[4];
	protected DataStruct x01s_ds = new DataStruct(); // QACK
	protected byte[] 	x02r_Buf = new byte[4];
	protected DataStruct x02r_ds = new DataStruct(); // PACK
	protected byte[] 	x02s_Buf = new byte[19];
	protected DataStruct x02s_ds = new DataStruct(); // set preset data(통상주유허가)
	protected byte[] 	x02s2_Buf= new byte[19];
	protected DataStruct x02s2_ds= new DataStruct(); // set preset data(프리셋주유허가)
	protected byte[] 	x05r_Buf = new byte[6];
	protected byte[] 	x05s_Buf = new byte[6];

	protected DataStruct x05s_ds = new DataStruct(); // ENQ
	protected byte[] 	x10r_Buf = new byte[19];
	protected DataStruct x10r_ds = new DataStruct(); // responded 
	protected byte[] 	x10s_Buf = new byte[9];
	protected DataStruct x10s_ds = new DataStruct(); // set
	protected byte[] 	x20r_Buf = new byte[15];
	protected DataStruct x20r_ds = new DataStruct(); // responded 
	protected byte[] 	x20s_Buf = new byte[5];
	protected DataStruct x20s_ds = new DataStruct(); // request total-gauge
	protected byte[] 	x24r_Buf = new byte[15];
	protected DataStruct x24r_ds = new DataStruct(); // responded 
	protected byte[] 	x24s_Buf = new byte[15];
	protected DataStruct x24s_ds = new DataStruct(); // set
    
    public CommTatsuno (int nozNum, String romVerStr) {
    	
		String 	nozStr = new String();
		Formatter form = new Formatter ();

		nozNo = nozNum;
		romVer = romVerStr;
		
		baseNozNo = (nozNo - 1) / 16;
		nozNo1_16 = nozNo - (baseNozNo * 16);
		LogUtility.getPumpALogger().info ("######## Tatusno nozNo=" + nozNo + 
				            " nozNo1_16=" + nozNo1_16 + " baseNozNo=" + baseNozNo + 1); // 초이 수정(09/09/02)
		
		form.format("%02d", nozNo);
		nozStr = form.toString();

		readBuffInterval   = baseReadBuffInterval;
		readStartInterval  = baseReadStartInterval;
		writeStartInterval = baseWriteStartInterval;
		minErrCnt 		   = baseMinErrCnt;
		maxSkipCnt 		   = baseMaxSkipCnt;
		
		try {

        	flushBuffer(lastPumpingData);
        	flushBuffer(lastGaugeData);
        	
			//---ENQ ---//
			x05s_ds.addByte("ENQ", ENQ);
			x05s_ds.addByte("UA", (byte) (nozNo1_16 - 1));
			x05s_ds.addByte("mode", (byte) 0x00);
			x05s_ds.addByte("PLK1_8", (byte) 0x00);
			x05s_ds.addByte("PLK9_16", (byte) 0x00);
			x05s_ds.addByte("BCC", (byte) 0x00);
			x05s_Buf = x05s_ds.getByteStream();	
			
			//--- Request pumping data ---//
			x00s_ds.addByte("STX", STX);
			x00s_ds.addByte("UA", (byte) (nozNo1_16 - 1));
			x00s_ds.addByte("command", (byte) 0x00);
			x00s_ds.addByte("BCC", (byte) 0x00);
			x00s_Buf = x00s_ds.getByteStream();	
	
			//---- Recv pumping data ---//
			x00r_ds.setByte("STX");
			x00r_ds.setByte("UA");
			x00r_ds.setByte("command");
			x00r_ds.setString("liter", 5);
			x00r_ds.setString("basePrice", 5);
			x00r_ds.setString("price", 5);
			x00r_ds.setByte("BCC");
			x00r_Buf = x00r_ds.getByteStream();	
			
			//--- QACK (reset pumping-ended flag) ---//
			x01s_ds.addByte("STX", STX);
			x01s_ds.addByte("UA", (byte) (nozNo1_16 - 1));
			x01s_ds.addByte("command", QACK);
			x01s_ds.addByte("BCC", (byte) 0x00);
			x01s_Buf = x01s_ds.getByteStream();	
	
			 //--- Set preset data (통상 주유허가)---//
			x02s_ds.addByte("STX", STX);
			x02s_ds.addByte("UA", (byte) (nozNo1_16 - 1));
			x02s_ds.addByte("command", (byte) 0x02);
			x02s_ds.addString("liter", "00990", 5);
			//x02s_ds.addString("basePrice", "01111", 5);
			x02s_ds.addString("basePrice", "00000", 5);
			x02s_ds.addString("price", "00000", 5);
			x02s_ds.addByte("BCC", (byte) 0x00);
			x02s_Buf = x02s_ds.getByteStream();	
			
			 //--- Set preset data (프리셋 주유허가)---//
			x02s2_ds.addByte("STX", STX);
			x02s2_ds.addByte("UA", (byte) (nozNo1_16 - 1));
			x02s2_ds.addByte("command", (byte) 0x02);
			x02s2_ds.addString("liter", "00000", 5);
			//x02s2_ds.addString("basePrice", "01111", 5);
			x02s2_ds.addString("basePrice", "00000", 5);
			x02s2_ds.addString("price", "00000", 5);
			x02s2_ds.addByte("BCC", (byte) 0x00);
			x02s2_Buf = x02s2_ds.getByteStream();	
			
			x02r_ds.setByte("STX");
			x02r_ds.setByte("UA");
			x02r_ds.setByte("command");
			x02r_ds.setByte("BCC");
			x02r_Buf = x02r_ds.getByteStream();	
			
			//--- set display basePrice & reset pumping-end ---//
			x10s_ds.addByte("STX", STX);
			x10s_ds.addByte("UA", (byte) (nozNo1_16 - 1));
			x10s_ds.addByte("command", (byte) 0x10);
			//x10s_ds.addString("basePrice", "01111", 5);
			x10s_ds.addString("basePrice", "00000", 5);
			x10s_ds.addByte("BCC", (byte) 0x00);
			x10s_Buf = x10s_ds.getByteStream();	
			
			x10r_ds.setByte("STX");
			x10r_ds.setByte("UA");
			x10r_ds.setByte("command");
			x10r_ds.setString("liter", 5);
			x10r_ds.setString("basePrice", 5);
			x10r_ds.setString("price", 5);
			x10r_ds.setByte("BCC");
			x10r_Buf = x10r_ds.getByteStream();	
			
			//--- request total-gauge ---//
			x20s_ds.addByte("STX", STX);
			x20s_ds.addByte("UA", (byte) (nozNo1_16 - 1));
			x20s_ds.addByte("command", (byte) 0x20);
			x20s_ds.addByte("octane", (byte) 0x00);
			x20s_ds.addByte("BCC", (byte) 0x00);
			x20s_Buf = x20s_ds.getByteStream();	
			
			x20r_ds.setByte("STX");
			x20r_ds.setByte("UA");
			x20r_ds.setByte("command");
			x20r_ds.setString("totalLiter", 5);
			x20r_ds.setString("totalPrice", 5);
			x20r_ds.setByte("BCC");
			x20r_Buf = x20r_ds.getByteStream();	
			
			 //--- set total-gauge ---//
			x24s_ds.addByte("STX", STX);
			x24s_ds.addByte("UA", (byte) (nozNo1_16 - 1));
			x24s_ds.addByte("command", (byte) 0x24);
			x24s_ds.addByte("octane", (byte) 0x00);
			x24s_ds.addString("totalLiter", "00000", 5);
			x24s_ds.addString("totalPrice", "00000", 5);
			x24s_ds.addByte("BCC", (byte) 0x00);
			x24s_Buf = x24s_ds.getByteStream();	
	
			x24r_ds.setByte("STX");
			x24r_ds.setByte("UA");
			x24r_ds.setByte("command");
			x24r_ds.addByte("octane", (byte) 0x00);
			x24r_ds.setString("totalLiter", 5);
			x24r_ds.setString("totalPrice", 5);
			x24r_ds.setByte("BCC");
			x24r_Buf = x24r_ds.getByteStream();	
			
			setPumpLockState(1); // 주유금지해지(Pump unlock)
		}
		catch (Exception e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
    }
	
	protected boolean compareBCC(byte[] buf, int len) throws Exception { // len = total_length - 1
		
		if (getBCC (buf, len) == buf[len])		
			return true;
		else
			return false;
	}
	
	public byte[] generateByteStream (WorkingMessage wm) throws Exception {		
		return trans.generateByteStream(wm);
	}
	
	public WorkingMessage generateWorkingMessage (byte[] buf, String command) throws Exception {
		return trans.generateWorkingMessage(buf, command);
	}
	
	public WorkingMessage generateWorkingMessage (byte[][] buf, String command) throws Exception {
		return trans.generateWorkingMessage(buf, command);
	}

	protected byte getBCC(byte[] dat, int len) throws Exception { // len = total_length - 1
		
		byte 	bcc=0;

		for (int i=0; i < len; i++) {
			bcc = (byte) (bcc ^ dat[i]); // XOR
		}
		
		return bcc;
	}
	
	protected boolean isSkipWorkingMessage (WorkingMessage wm) throws Exception {
		
		boolean skip=false;
		
		if (wm.getCommand().equals("PB")) { // 정액정량 설정

			PB_WorkingMessage PB_wm = (PB_WorkingMessage) wm;
			x02s2_Buf = generateByteStream(wm); // Preset Data 저장

			m_basePrice = PB_wm.getBasePrice(); // 단가저장
			m_bPreset = true;
			
			LogUtility.getPumpALogger().info("[Pump A][수신-주유허가]" + " nozzle=" + 
					PB_wm.getNozzleNo() + "("+PB_wm.getCommand()+")" +
					" liter =" + PB_wm.getLiter() +
					" | bPrice=" + PB_wm.getBasePrice() +
					" | price =" + PB_wm.getPrice());
						
			skip = true;
		}
		else if (wm.getCommand().equals("P3_1")) { // 주유기 환경설정(단가설정)

			P3_1_WorkingMessage P3_wm = (P3_1_WorkingMessage) wm;			
			m_basePrice = P3_wm.getBasePrice(); // 단가저장
									
			skip = true;
		}
		else if (wm.getCommand().equals("P8")) { // 토털게이지 요청

			m_bTotalGauge=true;
						
			skip = true;
		}
		else if (wm.getCommand().equals("PA")) { // 노즐제어 요청

			PA_WorkingMessage PA_wm = (PA_WorkingMessage) wm;
			int nNozState = Change.toValue(PA_wm.getNozzleState());
			setPumpLockState(nNozState);
			
			m_nozLock = (nNozState==0 ? true : false);

			if (m_nozLock==true)
				m_recvedNozLock=true;
			else {
				try {
					setBCC (x02s_Buf, x02s_Buf.length-1);
					sendText (x02s_Buf); // Preset 데이터 전송(통상주유허가)
				} catch (SerialConnectException e) {
					LogUtility.getPumpALogger().error(e.getMessage(), e);
				} 
			}
			
			skip = true;
		}
		else if (wm.getCommand().equals("P7")) { // 주유기 파라미터 설정

			P7_WorkingMessage P7_wm = (P7_WorkingMessage) wm;
			readBuffInterval   = baseReadBuffInterval + Change.toValue(P7_wm.getReadBuffInterval());
			readStartInterval  = baseReadStartInterval + Change.toValue(P7_wm.getReadStartInterval());
			writeStartInterval = baseWriteStartInterval + Change.toValue(P7_wm.getWriteStartInterval());
			minErrCnt 		   = baseMinErrCnt + Change.toValue(P7_wm.getLineErrorCount());
			
			// init values
			dispLevel=0;
			//m_isSaveSTX=false;
			
			int lineErrSkipCnt = Change.toValue(P7_wm.getLineErrorSkipCount());
			if (lineErrSkipCnt >= 9000 && lineErrSkipCnt <= 9003)
				dispLevel = lineErrSkipCnt - 9000; // dispLevel : 0 ~ 3
			else
				maxSkipCnt = baseMaxSkipCnt + lineErrSkipCnt;
			
			skip = true;
		}
			
		return skip;
	}	

	protected void makeLineError () throws Exception {
		
		SE_wm = new SE_WorkingMessage();
		
		SE_wm.setNozzleNo(Change.toString("%02d", nozNo));
		SE_wm.setDeviceType(Change.toString("%02d", nozType));
		SE_wm.setStatus("1");
		SE_wm.setStatusCode("601"); // 회선불량
		SE_wm.setErrMsg("주유기 회선불량");

		m_statusCode=601; // appended
		m_lineCommState=false;

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
			case 0:
			case 2:
				statusCode = 651; // 노즐다운
				errorMsg = "노즐다운";
				break;
			case 1:
			case 3:
				statusCode = 652; // 노즐업
				errorMsg = "노즐업";
				break;
			case 4:
				statusCode = 653; // 주유중
				errorMsg = "주유중";
				break;
			case 5:
				statusCode = 654; // 주유완료
				errorMsg = "주유완료";
				break;
			case 656:
				statusCode = 656; // 비상정지(주유금지)
				errorMsg = "비상정지";
				break;
			case 657:
				statusCode = 657; // 비상정지해제
				errorMsg = "비상해제";
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

	}
	
	protected boolean processRecvDST(byte byDST) throws Exception, SerialConnectException {
		
		boolean bRet = true;
		//byte[] byGaugeData= new byte[15];
		byte[] byGaugeData= {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		byte[] byPumpData = new byte[19];	
				
		m_bOnLine = ((byDST & 0x01)==0 ? true : false); 

		boolean bNozzleUp = ((byDST & 0x02) > 0 ? true : false); // 노즐업
		m_nNozzleState = (bNozzleUp ? 2 : 1);
		
		boolean bPumping = ((byDST & 0x04) > 0 ? true : false); // 주유중
		m_nNozzleState = (bPumping ? 3 : m_nNozzleState);
		
		boolean bPumpCP = ((byDST & 0x08) > 0 ? true : false); // 주유완료
		m_nNozzleState = (bPumpCP ? 4 : m_nNozzleState);
		
		switch (m_nNozzleState) {
			case 1 :
				m_statusCode=651; // 노즐다운
				break;
			case 2 :
				m_statusCode=652; // 노즐업
				break;
			case 3 :
				m_statusCode=653; // 주유중
				break;
			case 4 :
				m_statusCode=654; // 주유완료
				break;
		}

		//LogUtility.getPumpALogger().debug("m_nNozzleState="+m_nNozzleState + " m_statusCode=" +
				//m_statusCode + " nozNo="+nozNo);
		
		try {
			
			if (bNozzleUp==true) { //---- 노즐업->주유시작
				
//				bRet = recvTotalGauge (byGaugeData); //이동 (1)로, 2010/03/05
//				if (bRet==false)
//					byGaugeData = lastGaugeData.clone();
				
				Sleep.sleep(100); // 추가 2010/03/05
				
			    if (sentNozUpStateInfo==false) {
			    	makeStatusInfo(1); // 상태정보 전송
			    	
					beforeNozzleUp=true;
					realPumpingStart=false;
					
			    	sentNozUpStateInfo=true;
			    }
				
				if (sentPumpingStartInfo==false && m_nozLock==false) {
									
					bRet = recvTotalGauge (byGaugeData); //토털게이지 요청/수신 - (1), 이동 2010/03/05
					
					SJ_wm = (SJ_WorkingMessage) generateWorkingMessage(byGaugeData, "SJ");
					SJ_wm.setNozzleNo(Change.toString("%02d", nozNo));
					
					if (bRet==true) {
						last_SJ_TotalGauge = SJ_wm.getTotalGauge();
						LogUtility.getPumpALogger().info("주유시작전문(SJ)전송, TotalGauge="+SJ_wm.getTotalGauge());
					}
					else {
						//SJ_wm.setTotalGauge(last_SJ_TotalGauge);
						SJ_wm.setTotalGauge("0000000000");
						LogUtility.getPumpALogger().info("주유시작전문(SJ)생성, TotalGauge="+SJ_wm.getTotalGauge());
					}
					
					insertRecvQueue(SJ_wm); // 주유시작 전송
					
					sentPumpingStartInfo=true;
				}
				sentNozDownStateInfo=false;
			}
			else { // 노즐다운
				
				if (sentNozDownStateInfo==false && m_nNozzleState==1) {
					makeStatusInfo(0); // 상태정보 전송
					sentNozDownStateInfo=true;
				}
		    	sentNozUpStateInfo=false;

				// 비상정지/해제 상태정보 처리
				if (m_nozLock==true)
					makeStatusInfo(656); // 비상정지
				else if (m_recvedNozLock==true) {
					makeStatusInfo(657); // 비상해제
					m_recvedNozLock=false;
				}
				
				// POS에서 프리셋 설정후 취소처리(주유값이 0 인 주유완료 자료)
				if (beforeNozzleUp==true && realPumpingStart==false) { // 정상 주유완료자료가 아니면
					S4_wm = new S4_WorkingMessage();
					S4_wm.setNozzleNo(Change.toString("%02d",nozNo));
					S4_wm.setLiter("0000000");
					S4_wm.setBasePrice(m_basePrice);
					S4_wm.setPrice("00000000");
					S4_wm.setTotalGauge(last_SJ_TotalGauge);
					insertRecvQueue(S4_wm); // 주유완료 자료

					//sentPumpingStartInfo=false; // 이동(08/09/01)
					//firstPumpingData=true;
					sentNozDownStateInfo=false;
					sentNozUpStateInfo=false;
				}
				beforeNozzleUp=false;
				
				// 이동(08/09/01)
				sentPumpingStartInfo=false;
				firstPumpingData=true;
			}
			
			//----- 주유자료 처리 ------//
			if (bPumping==true || bPumpCP==true) {
				
				bRet = recvPumpData (byPumpData); // 주유자료 요청/수신
				
				if (bPumpCP==true && bRet==true) { //---- 주유완료
					
					setBCC (x01s_Buf, x01s_Buf.length-1);
					sendText (x01s_Buf); // QACK 송신
						
					byte[][] tBuf = new byte[2][20];
					
					// 값확인을 위한 임시전문 생성
					S3_wm = (S3_WorkingMessage) generateWorkingMessage(byPumpData, "S3");
					int nPrice = Change.toValue(S3_wm.getPrice());
					int nLiter = Change.toValue(S3_wm.getLiter());
					
					if (nPrice > 0 && nLiter > 0)
						tBuf[0] = byPumpData.clone(); // 최종 주유정보 저장
					else 
						tBuf[0] = lastPumpingData.clone(); // 이전 주유정보

					flushBuffer(lastPumpingData);
					
					Sleep.sleep(readBuffInterval + 150); // 추가 2010/03/05
					bRet = recvTotalGauge (byGaugeData); //토털게이지 요청/수신
										
//					if (bRet==true)
//						tBuf[1] = byGaugeData.clone(); // Total guage
//					else
//						tBuf[1] = lastGaugeData.clone(); // 삭제 2010/03/05
					
					tBuf[1] = byGaugeData.clone(); // Total guage
					
					makeStatusInfo(5); // 상태정보 전송
					
					S4_wm = (S4_WorkingMessage) generateWorkingMessage(tBuf, "S4");
					S4_wm.setNozzleNo(Change.toString("%02d", nozNo));
					nPrice = Change.toValue(S4_wm.getPrice());
					m_nPrice = nPrice;
					m_nLiter = nLiter;
					
					if (bRet==true) {  // 토털게이지 수신
						last_SJ_TotalGauge = S4_wm.getTotalGauge();
					}
					else { // 미수신시 보정처리
						/*
						long n_lastGauge=Change.toValue(last_SJ_TotalGauge) / 1000; // 이전값
						int  n_liter    =Change.toValue(S4_wm.getLiter()) / 1000; // 주유량
						S4_wm.setTotalGauge(Change.toString("%04d", n_lastGauge + n_liter)
								+ "000"); */
						S4_wm.setTotalGauge("0000000000");
					}
					
					insertRecvQueue(S4_wm); // 주유완료 자료 전송
					
					sentPumpingStartInfo=false;
					firstPumpingData=true;
					sentNozDownStateInfo=false;
					sentNozUpStateInfo=false;
					newPumpingData=false;
				}

				if (m_nNozzleState==3 && bRet==true) { //---- 주유중

					//realPumpingStart=true;
					
					if (firstPumpingData==true) {
						makeStatusInfo(4); // 상태정보 전송
						firstPumpingData=false;
					}

					S3_wm = (S3_WorkingMessage) generateWorkingMessage(byPumpData, "S3");
					S3_wm.setNozzleNo(Change.toString("%02d", nozNo));
					int nPrice = Change.toValue(S3_wm.getPrice());
					int nLiter = Change.toValue(S3_wm.getLiter());
					
					if (nPrice > 0 && nLiter > 0) {				
						if (newPumpingData==false && nLiter==m_nLiter) {
							LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 주유량(p0)이 이전 주유량과 동일합니다. : 주유량=" + 
									nLiter + " 이전 주유량=" + m_nLiter);
						}
						else {
							lastPumpingData = byPumpData.clone(); // 최종 주유정보 저장
							realPumpingStart=true;
							newPumpingData=true;
							
							insertRecvQueue(S3_wm); // 주유중 자료 전송
						}
					}
				}
			} 
			//--- End of 주유자료 처리 ---//

			boolean bReqPreset = ((byDST & 0x40) > 0 ? true : false);
			
			//----- 정액정량 설정 처리 ------//
			if (bReqPreset==true) {
								
				if (m_bPreset==true) { // preset 주유("PB"수신후)
					x02s2_Buf[1] = (byte) (nozNo1_16 - 1);
					
					setBCC (x02s2_Buf, x02s2_Buf.length-1);
					sendText (x02s2_Buf); // Preset 데이터 전송(정액정량 설정)

					LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 정액정량설정(1)");
					//Log.datas(x02s2_Buf, x02s2_Buf.length, 20);
				} 
				else {
					PB_WorkingMessage PB_wm = new PB_WorkingMessage();
					PB_wm.setCommandSet("1");
					PB_wm.setLiter("9999000");
					PB_wm.setBasePrice(m_basePrice);
					PB_wm.setPrice("00000000");
					x02s_Buf = generateByteStream(PB_wm);
					x02s_Buf[1] = (byte) (nozNo1_16 - 1);
					
					setBCC (x02s_Buf, x02s_Buf.length-1);
					sendText (x02s_Buf); // Preset 데이터 전송(통상주유허가)

					LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 정액정량설정(통상주유허가)");
				}
								
				bRet = recvPACK ();
				
				//if (bRet==true && m_bPreset==true) // 강남그린 삭제(08/08/15)
					m_bPreset = false;
			}
			//-- End of 정액정량 설정 처리 ---//

			//----- 토털게이지 요청 처리 ------//
			if (bReqPreset==false && m_bTotalGauge==true) {

				bRet = recvTotalGauge (byGaugeData); // 토털게이지 요청/수신

				if (bRet==true && m_bTotalGauge==true)
					m_bTotalGauge = false;

				S5_wm = (S5_WorkingMessage) generateWorkingMessage(byGaugeData, "S5");
				S5_wm.setNozzleNo(nozStr);
				
				insertRecvQueue(S5_wm); // 토털게이지 전송
				m_bTotalGauge = false;
			}	
			//-- End of 토털게이지 요청 처리 ---//

		} catch (Exception e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
		
		return bRet;
	}
	
	protected boolean recvPACK() throws SerialConnectException, Exception{
		
		short	len=4;
		byte byPACK[] = new byte[len];
		
		//Sleep.sleep(50);
		if (recvText (byPACK, len) < 1) {
			LogUtility.getPumpALogger().info ("1.Recv PACK fail! (Noz=" + nozNo + ")");
			Log.datas(byPACK, byPACK.length, 20);
			return false;
		}

		if (compareBCC (byPACK, len-1)==false) {
			LogUtility.getPumpALogger().info ("1.Recv PACK BCC fail! (Noz=" + nozNo + ")");
			Log.datas(byPACK, byPACK.length, 20);
			return false;
		}	

		if (byPACK[0] != STX) { // append(08.08.28)
			LogUtility.getPumpALogger().info ("1.Recv PACK data verify fail! (Noz=" + nozNo + ")");
			Log.datas(byPACK, byPACK.length, 20);
			return false;
		}	

		//if (byPACK[1] != nozNo1_16) {
		if (byPACK[1] != nozNo1_16 - 1)	{ // $$$$$$$$$$
			LogUtility.getPumpALogger().info ("1.Recv PACK NozId mismatch! (Noz=" + nozNo + ")");
			Log.datas(byPACK, byPACK.length, 20);
			return false;
		}	

		if (dispLevel==1 || dispLevel==2) {
			LogUtility.getPumpALogger().info ("1.Recv PACK with normal (Noz="+nozNo+")");
			Log.datas(byPACK, byPACK.length, 20);
		}

		return (byPACK[2]==PACK);
	}

	protected boolean recvPumpData (byte byPumpData[])	throws SerialConnectException, Exception {

		int	len=byPumpData.length;

		setBCC (x00s_Buf, x00s_Buf.length-1);
		sendText (x00s_Buf); // 주유자료 요청
		
		if (recvText(byPumpData, len) < 1) {
			return false;
		}

		if (compareBCC (byPumpData, len-1)==false) {
			LogUtility.getPumpALogger().info ("1.Recv STX BCC fail! (Noz=" + nozNo + ")");
			Log.datas(byPumpData, byPumpData.length, 20);
			return false;
		}

		if (byPumpData[1] != nozNo1_16 - 1)	{
			LogUtility.getPumpALogger().info ("1.Recv STX NozID mismatch! (Noz="+nozNo+")");
			Log.datas(byPumpData, byPumpData.length, 20);
			return false;
		}	

		if (byPumpData[2] != CMD_PUMP_DATA) {
			Log.datas(byPumpData, byPumpData.length, 20);
			return false;
		}

		/*if (verifyData(byPumpData)==false) { // append(08.08.28)
			LogUtility.getPumpALogger().debug ("1.Recv STX Data verify fail! (Noz="+nozNo+")");
			return false;
		}*/
		if (dispLevel==1 || dispLevel==2) {
			LogUtility.getPumpALogger().info ("1.Recv STX with normal (Noz="+nozNo+")");
			Log.datas(byPumpData, byPumpData.length, 20);
		}

		return true;
	}
	
	protected boolean recvTail_proc () throws Exception, SerialConnectException {
		
		return true;
	}
	
	
	public int recvText(byte[] RxBuf) throws Exception, SerialConnectException {

		int		numBytes=0, c=0, i=0, loop=0;
		boolean	STXFlag=false;
		byte[]	RxByt = new byte[1];
		int		recvLoopCnt=readBuffInterval/10;
		
		flushBuffer (RxBuf);
		Sleep.sleep(readStartInterval);

		try {
			
			for (c=0; c<RxBuf.length-1; c++) {
			
				RxByt[0] = 0x00;
				
				if (is.read(RxByt, 0, 1) < 1) { // no recv data
					//---check timeout
					if (loop > recvLoopCnt) break;
					else loop++;
					
					c--;
					Sleep.sleep(10);
					continue;
				} else
					loop=0;

				RxBuf[i] = RxByt[0];
				
				if (RxBuf[i]==STX)
					STXFlag=true;

				if (STXFlag==false)
					if (RxBuf[i]==EOT || RxBuf[i]==ACK || RxBuf[i]==NAK) {  // recv EOT/ACK/NAK
						numBytes=i+1;
						break;
					}
				
				if (RxBuf[i]==ETX) { // recv ETX next STX
					if (STXFlag==true) {
						RxByt[0] = 0x00;
						is.read(RxByt, 0, 1); // read BCC
						RxBuf[i+1] = RxByt[0];
						numBytes=i+2;
						break;
					}
					else {  // recv ETX none STX
						flushBuffer (RxBuf);
						numBytes=0;
						break;
					}
				}
				i++;
			}
			return numBytes;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogUtility.getPumpALogger().error(e.getMessage(), e);
			return 0;
		}
	}
	
	public int recvText(byte[] RxBuf, int len) throws Exception, SerialConnectException {

		int		c=0, i=0, loop=0;
		byte[]	RxByt = new byte[1];
		//int		recvLoopCnt=10;
		int		recvLoopCnt=readBuffInterval/10;
		
		flushBuffer (RxBuf);
		Sleep.sleep(readStartInterval);

		try {
			
			for (c=0; c<RxBuf.length; c++) {
				
				RxByt[0] = 0x00;
				
				if (is.read(RxByt, 0, 1) < 1) { // no recv data
					//---check timeout
					if (loop > recvLoopCnt) break;
					else loop++;
					
					c--;
					Sleep.sleep(10);
					continue;
				} else
					loop=0;

				RxBuf[i] = RxByt[0];
				
				i++;			
				if (i >= len)
					break;
			}

			return i;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogUtility.getPumpALogger().error(e.getMessage(), e);
			return 0;
		}
	}

	protected boolean recvTotalGauge (byte byGaugeData[]) throws SerialConnectException, Exception  {

		int	len=byGaugeData.length;

		setBCC (x20s_Buf, x20s_Buf.length-1);
		sendText (x20s_Buf); // 토털게이지 요청

		Sleep.sleep(50); // 강남그린 추가(08/08/15)
		
		if (recvText(byGaugeData, len) < 1) {
			flushBuffer(byGaugeData);
			return false;
		}

		if (compareBCC (byGaugeData, len-1)==false) {
			LogUtility.getPumpALogger().info ("1.Recv STX BCC fail! (Noz=" + nozNo + ")");
			Log.datas(byGaugeData, byGaugeData.length, 20);
			flushBuffer(byGaugeData);
			return false;
		}

		if (byGaugeData[1] != nozNo1_16 - 1) {
			LogUtility.getPumpALogger().info ("1.Recv STX NozID mismatch! (Noz="+nozNo+")");
			Log.datas(byGaugeData, byGaugeData.length, 20);
			flushBuffer(byGaugeData);
			return false;
		}	

		if (CMD_TOTAL_GAUGE != byGaugeData[2]) {
			Log.datas(byGaugeData, byGaugeData.length, 20);
			flushBuffer(byGaugeData);
			return false;
		}

		/*if (verifyData(byGaugeData)==false) { // append(08.08.28)
			flushBuffer(byGaugeData);
			LogUtility.getPumpALogger().debug ("1.Recv STX Data verify fail! (Noz="+nozNo+")");
			Log.datas(byGaugeData, byGaugeData.length, 20);
			return false;
		}*/
		
		lastGaugeData = byGaugeData.clone();
		
		if (dispLevel==1 || dispLevel==2) {
			LogUtility.getPumpALogger().info ("1.Recv STX with normal (Noz="+nozNo+")");
			Log.datas(byGaugeData, byGaugeData.length, 20);
		}

		return true;
	}
	
	@Override
	public void requestData() throws Exception, SerialConnectException {
		
		boolean	occurLineErr = (lineErrCnt >= minErrCnt);
		
		if (occurLineErr) { // Check line error

			if (issueLineErr==true) {
				makeLineError();
				issueLineErr=false;
			}
			
			if (lineErrCnt < maxSkipCnt) {
				lineErrCnt++;
				return; // Skip
			} 
			else {
				lineErrCnt=0;
				issueLineErr=true;
			}
		}
		
		// 회선불량 복구시 처리
		if (m_lineCommState==false) { // 회선불량
			//LogUtility.getPumpALogger().debug("회선불량상태  m_statusCode=" + m_statusCode);
			if (lineCommCheckCnt >= minErrCnt+5) {
				lineCommCheckCnt=0;
				if (m_statusCode!=601) {
					makeStatusInfo(0); // 상태정보(노즐다운) 전송
					m_lineCommState=true;
				}
			}
			lineCommCheckCnt++;
		}
		
		flushBuffer(RxBuf);
		
		//--- Transfer from WorkingMessage to BytesStream
		while (sndQue.getItemCount() > 0) {
			WorkingMessage wm = (WorkingMessage)sndQue.deQueue();

			LogUtility.getPumpALogger().debug("\n>>>>> Received  Down data : " +
								"nozNo=" + nozNo + " command=" + wm.getCommand());
			
			if (isSkipWorkingMessage(wm)==true) {
				wm=null;
				continue;
			}
			
			byte[] wmByte = generateByteStream(wm);
			if (wmByte != null) 
				TxQue.enQueue(wmByte);
			/*
			if (wm.getCommand().equals("PB"))
				Log.datas(wmByte, wmByte.length, 20);
			*/
			wm=null;
		}	
		if (dispLevel>=3)
			LogUtility.getPumpALogger().debug ("\nStart request ============> WooJoo, (noz="+nozNo+")");
			
		try {
			//##### Send ENQ and Recv ACK/STX #####//
			if (dispLevel>=3)
				LogUtility.getPumpALogger().debug ("----POLLING----(progStep="+progressStep+", noz="+nozNo+")");

			x05s_Buf[3] = g_byPumpLockState[0];
			x05s_Buf[4] = g_byPumpLockState[1];
			
			//--- Send ENQ ---//
			setBCC (x05s_Buf, x05s_Buf.length - 1);
			if(sendText (x05s_Buf) != true) { // fail
				lineErrCnt++;
				return;
			}

			if (dispLevel>=3)
				LogUtility.getPumpALogger().info ("0.Send ENQ (noz="+nozNo+")");
						
			for (int i=0; i<3; i++) { // 초이 추가(09/09/02)
				
				//--- recv data---//
				short ackLen=5;
				byte byACK[] = new byte[ackLen];
				if (recvText(byACK, ackLen) < 1) {
					if (dispLevel>=3)
						LogUtility.getPumpALogger().info ("0.Recv DAT fail! (noz="+nozNo+")");
					lineErrCnt++;

					is.read();
					sendText (x05s_Buf);
					continue;
				}
				
				if (byACK[0] == ACK) {
	
					if (dispLevel>=3) {
						LogUtility.getPumpALogger().info ("1.Recv ACK (noz="+nozNo+")");
						Log.datas(byACK, ackLen, 20);
					}
					
					if (compareBCC(byACK, ackLen-1)==false) {
						LogUtility.getPumpALogger().info ("1.Recv ACK BCC fail! (noz="+nozNo+")");
						Log.datas(byACK, byACK.length, 20);
						
						//초이 추가(09/09/02)
						lineErrCnt++;
						is.read();
						sendText (x05s_Buf);
						continue;
					}

					if (byACK[1] != nozNo1_16 - 1) {
						LogUtility.getPumpALogger().info ("\n1.Recv ACK NozNo mismatch! (noz="+nozNo+")");
						Log.datas(byACK, byACK.length, 20);

						//초이 추가(09/09/02)
						lineErrCnt++;
						is.read();
						sendText (x05s_Buf);
						continue;
					}
					
					processRecvDST(byACK[2]); // 노즐 DST 처리	
					lineErrCnt=0;
					break;
				} 
				else {
					if (dispLevel>=3)
						LogUtility.getPumpALogger().info ("2.Recv ACK fail! (noz="+nozNo+")");

					//초이 추가(09/09/02)
					lineErrCnt++;
					is.read();
					sendText (x05s_Buf);
					continue;
				}
			}
				
		} catch (Exception e) {
			LogUtility.getPumpALogger().error("Exception occurr! (noz="+nozNo+")");
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	}
	
	public void run() {

		
	}
	
	protected boolean sendTail_proc () throws Exception, SerialConnectException {
		boolean rtn;
			
		return true;
	}
	
	
	public boolean sendText(byte[] buf) throws Exception, SerialConnectException {

		Sleep.sleep(writeStartInterval);
		
	    try {	    	
			os.write(buf);
		} catch (IOException e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
		
		return true;
	}
	
	
	public boolean sendText(String txt) throws Exception, SerialConnectException {

		Sleep.sleep(writeStartInterval);
		
	    try {
			os.write(txt.getBytes());
		} catch (IOException e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	
		return true;
	}

	// To be invoked when InputStream(is) has a receiving data
	public void serialEvent(SerialPortEvent event) {


	}
	
	protected void setBCC(byte[] buf, int len) throws Exception { // len = total_length - 1
		
		buf[len] = getBCC(buf, len);
	}

	protected synchronized void setPumpLockState(int mode) throws Exception {
		//mode 0=주유금지 1=주유금지해제
		
		int	iNozzle = nozNo1_16-1;
		int idx = iNozzle/8;
		
		if (mode==0) { // 주유금지(Pump Lock)
			g_byPumpLockState[idx] &= ~(1 << (iNozzle%8));
		}
		else if (mode==1) { // 금지해제(Pump UnLock)
			g_byPumpLockState[idx] |= (1 << (iNozzle%8));
		}

		x05s_ds.editByte("PLK1_8",  g_byPumpLockState[0]);
		x05s_ds.editByte("PLK9_16", g_byPumpLockState[1]);
		x05s_Buf = x05s_ds.getByteStream();
	}

	protected boolean verifyData (byte[] buf) throws Exception {
		
		int size=0;
		String cmd="";
		
		if (buf[2]==CMD_PUMP_DATA) { // 주유자료 수신
			size = 19;
			cmd = "x00";
		}
		else if (buf[2]==CMD_TOTAL_GAUGE) { // 토털게이지 자료 수신
			size = 15;
			cmd = "x20";
		}
		else 
			return false;

		//--- 데이터 검증(숫자여부) ---//
		for (int i=4; i<size-1; i++) {
			
			// 0x00 ~ 0x99 및 공백이 아니면
			if (Change.getUnsignedByte(buf[i]) < (byte)0x00 || 
					Change.getUnsignedByte(buf[i]) > (byte)0x99) {
				
				if (Change.getUnsignedByte(buf[i]) != (byte)0x20) { 
					LogUtility.getPumpALogger().info("nozzle=" + nozNo + 
						" -> 이상데이터 수신("+cmd+"), 숫자나 공백이 아님, 수신데이터=" + 
						Change.getUnsignedByte(buf[i]));
					return false;
				}
			}
		}
	
		return true;
	}
}
