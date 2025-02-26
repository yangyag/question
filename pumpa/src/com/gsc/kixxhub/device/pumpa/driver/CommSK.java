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
import com.gsc.kixxhub.device.pumpa.translation.TransSk;

public class CommSK extends CCommDriver implements SerialPortEventListener, CommPortOwnershipListener, 
	Runnable {

	protected static boolean m_isSaveSTX=false;
	protected byte ACK = 0x06;
	protected byte[] 	AP_Buf = new byte[8];  	// 주유허가(통상)
	protected DataStruct AP_ds = new DataStruct();
	protected int	baseMinErrCnt=8, baseMaxSkipCnt=100;
	protected int	baseReadBuffInterval   = 30+5; // 스마트주유소 PJT 보정
	// 1=비정상 메시지 + 비정상수신 STX + 정상수신 STX
	 							 // 2=비정상 메시지 + 비정상수신 STX + 정상송수신 STX
	 							 // 3=모든 메시지 + 모든 송수신 STX
	protected int	baseReadStartInterval  = 20+4; // 스마트주유소 PJT 보정
	protected int	baseWriteStartInterval = 20+4; // 스마트주유소 PJT 보정
	
	protected boolean beforeNozzleUp=false; // 이전 노즐업여부
	protected int		buffSize = 40;
	protected byte[] 	CQ_Buf = new byte[8];  	// 토털게이지 요청
	protected DataStruct CQ_ds = new DataStruct();
	protected byte DC1 = 0x11;
	
    protected byte DC2 = 0x12;
    protected byte DC3 = 0x13;
    protected byte DC4 = 0x14;
    protected int	dispLevel=0; // 0=비정상 메시지 + 비정상수신 STX
    protected byte ENQ = 0x05;
    protected byte[] 	enq_Buf = new byte[3];
    protected byte EOB = 0x17;
    protected byte EOT = 0x04;
    protected byte ETX = 0x03;
    protected boolean firstPumpingData=true;
    protected boolean firstRequest=true;
    protected byte FS  = 0x1c;
    protected HF_WorkingMessage HF_wm;
    
    protected byte[] 	IN_Buf = new byte[8];  		// 주유기 재기동
    protected DataStruct IN_ds = new DataStruct();
    							 protected boolean	issueLineErr=true;
	protected byte[] 	lastPumpingData=new byte[buffSize];
	protected int		lineCommCheckCnt=0;
	protected int		lineErrCnt=0;

	protected String	m_basePrice="000000";
	
	protected String	m_downBasePrice="000000";
	protected int		m_nLiter=0;
	protected boolean m_nozLock=false;
	protected int		m_nPrice=0;

	protected boolean m_recvedNozLock=false;
	protected int		m_statusCode=0;
    protected int	MAX_GAUGE_SJ  =5;
	protected int	MAX_LAST_PDATA=3;
	protected int	MAX_PROG4STEP =15;
	protected int	MAX_StateReq_Cnt=30; // 상태정보 요청주기(0=nothing)
	protected int	MAX_WAIT_PDATA=5;
	protected byte NAK = 0x15;
	protected boolean newPumpingData=false; // 이전 주유값 여부
	protected byte[] 	nozID = new byte[2];
	protected short		nozState=0;
	protected String 	nozStr = "";
	protected int		p0_cnt=0;
	protected byte[] 	PC_Buf = new byte[12]; 	// 단가 변경
	protected DataStruct PC_ds = new DataStruct();
	protected String presetBasePrice="000000";
	protected boolean presetDataFlag=false;
	protected String presetLiter="0000000";
	protected String presetPrice="000000";
	// Preset data keeping - for Quick-win
	protected String presetType="2";
	
	//progressStep : 0=초기화전, 1=초기화후, 2=상태정보송신후, 3=주유중, 4=주유완료, 
    //               5=토털게이지수신/주유완료자료 송신
    protected int 	progressStep=3;
	protected int		progressStep4Cnt=0;

    protected boolean pumpingStart=false;
    
	protected int	readCmdInterval    = 20; // readBuffInterval when read command(ACK/EOT etc.)
	protected boolean realPumpingStart=false; // 오일토출
	protected byte[] 	RT_Buf = new byte[8];  	// 주유기 초기화
	protected DataStruct RT_ds = new DataStruct();
	protected boolean rtn;
	protected byte[] 	RxBuf = new byte[buffSize];
	protected S3_WorkingMessage S3_wm;
	
	protected S4_WorkingMessage S4_wm;
	protected S5_WorkingMessage S5_wm;
	protected S8_WorkingMessage S8_wm;
	protected byte[] 	SC_Buf = new byte[8];  	// 주유금지
	protected DataStruct SC_ds = new DataStruct();
	protected SE_WorkingMessage SE_wm;
    
	protected boolean sentNozDownInfo=false;
	protected boolean sentPumpingEndInfo=false; // 추가(09/09/08)
	protected boolean sentPumpingStartInfo=false;
	protected String	SJ_TotalGauge="0000000000";
	protected SJ_WorkingMessage SJ_wm;
	protected byte SOH = 0x01;
	protected byte[] 	ST_Buf = new byte[16]; 	// 주유허가(프리셋)
	protected DataStruct ST_ds = new DataStruct();
	
    protected byte STX = 0x02;
    
	protected byte[] 	TQ_Buf = new byte[8];  	// 주유완료 자료요청
	protected DataStruct TQ_ds = new DataStruct();
	protected TransSk trans = new TransSk();
	protected byte[] 	TxBuf = new byte[buffSize];
	protected int		waitGaugeForSJ_Cnt=0;
	protected int		waitLastPData_Cnt=0;
	protected boolean waitLastPumpData=false; // 최종 주유완료값 수신대기
	protected boolean waitTotalGuageFor_SJ=false; // 주유시작 게이지 수신대기
    
    public CommSK (int nozNum, String romVerStr) {
    	
    	byte	bcc;
		Formatter form = new Formatter();

		nozNo = nozNum;
		romVer = romVerStr;

		form.format("%02d", nozNo);
		nozStr = form.toString();
		//nozByt = nozStr.getBytes();
    	nozID = nozStr.getBytes();
    	
		//--- prefix-ENQ ---//
		enq_Buf[0] = ENQ;
		enq_Buf[1] = nozID[0];
		enq_Buf[2] = nozID[1];
    	
		readBuffInterval   	= baseReadBuffInterval;
		readStartInterval  	= baseReadStartInterval;
		writeStartInterval 	= baseWriteStartInterval;
		minErrCnt 		   		= baseMinErrCnt;
		maxSkipCnt 		   	= baseMaxSkipCnt;
		
    	try {
        	flushBuffer(lastPumpingData);
	    	
			//--- AP (주유허가-통상)---//
        	AP_ds.addByte("SOH", SOH);
			AP_ds.addString("nozNo", nozStr, 2);
			AP_ds.addByte("STX", STX);
			AP_ds.addString("command", "AP", 2);
			AP_ds.addByte("ETX", ETX);
			AP_ds.addByte("bcc", (byte) ' ');
			AP_Buf = AP_ds.getByteStream();
	    	
			//--- CQ (토털게이지 요청)---//
        	CQ_ds.addByte("SOH", SOH);
        	CQ_ds.addString("nozNo", nozStr, 2);
        	CQ_ds.addByte("STX", STX);
        	CQ_ds.addString("command", "CQ", 2);
        	CQ_ds.addByte("ETX", ETX);
        	CQ_ds.addByte("bcc", (byte) ' ');
        	CQ_Buf = CQ_ds.getByteStream();
        	
			//--- IN (주유기 재기동)---//
        	IN_ds.addByte("SOH", SOH);
        	IN_ds.addString("nozNo", nozStr, 2);
        	IN_ds.addByte("STX", STX);
        	IN_ds.addString("command", "IN", 2);
        	IN_ds.addByte("ETX", ETX);
        	IN_ds.addByte("bcc", (byte) ' ');
        	IN_Buf = IN_ds.getByteStream();
	    	
			//--- PC (단가변경)---//
        	PC_ds.addByte("SOH", SOH);
        	PC_ds.addString("nozNo", nozStr, 2);
        	PC_ds.addByte("STX", STX);
        	PC_ds.addString("command", "PC", 2);
        	PC_ds.addString("basePrice", "0000", 4);
        	PC_ds.addByte("ETX", ETX);
        	PC_ds.addByte("bcc", (byte) ' ');
        	PC_Buf = PC_ds.getByteStream();
        	
			//--- RT (주유기 초기화)---//
        	RT_ds.addByte("SOH", SOH);
        	RT_ds.addString("nozNo", nozStr, 2);
        	RT_ds.addByte("STX", STX);
        	RT_ds.addString("command", "RT", 2);
        	RT_ds.addByte("ETX", ETX);
        	RT_ds.addByte("bcc", (byte) ' ');
        	RT_Buf = RT_ds.getByteStream();
        	
			//--- SC (주유금지)---//
        	SC_ds.addByte("SOH", SOH);
        	SC_ds.addString("nozNo", nozStr, 2);
        	SC_ds.addByte("STX", STX);
        	SC_ds.addString("command", "SC", 2);
        	SC_ds.addByte("ETX", ETX);
        	SC_ds.addByte("bcc", (byte) ' ');
        	SC_Buf = SC_ds.getByteStream();
        	
			//--- ST (주유허가-프리셋)---//
        	ST_ds.addByte("SOH", SOH);
        	ST_ds.addString("nozNo", nozStr, 2);
        	ST_ds.addByte("STX", STX);
        	ST_ds.addString("command", "ST", 2);
        	ST_ds.addByte("mode", (byte) ' '); // Q=수량, A=금액, F=Full
        	ST_ds.addString("preset", "0000000", 7); // 수량=4.3, 금액=7.0
        	ST_ds.addByte("ETX", ETX);
        	ST_ds.addByte("bcc", (byte) ' ');
        	ST_Buf = ST_ds.getByteStream();

			//--- TQ (주유완료 자료요청)---//
        	TQ_ds.addByte("SOH", SOH);
        	TQ_ds.addString("nozNo", nozStr, 2);
        	TQ_ds.addByte("STX", STX);
        	TQ_ds.addString("command", "TQ", 2);
        	TQ_ds.addByte("ETX", ETX);
        	TQ_ds.addByte("bcc", (byte) ' ');
        	TQ_Buf = TQ_ds.getByteStream();
        	
    	} catch (Exception e) {
    		LogUtility.getPumpALogger().error(e.getMessage(), e);
    	}

    }
 
	protected boolean changeBasePrice () throws Exception, SerialConnectException {
		
		TxBuf = PC_ds.getByteStream();
		TxBuf[1] = nozID[0];
		TxBuf[2] = nozID[1];
		setBCC (TxBuf); // write BCC

		for (int i=0; i<3; i++) {
			
			if(sendText(TxBuf) != true) { // 단가변경 요청(PC)
				if (dispLevel>=3) {
					LogUtility.getPumpALogger().info("1.Send STX(PC) fail! (Noz="+nozNo+")");
					Log.datas(TxBuf, TxBuf.length, 20);
				}
				continue;
			}
			
			flushBuffer(RxBuf);
			if (recvText(RxBuf) < 1) { // recv basePrice(PC) fail
				if (dispLevel>=3)
					LogUtility.getPumpALogger().info("1.Recv STX(PC) fail! (Noz="+nozNo+")");
				continue;
			}
			else { // success
				// ID, BCC, Data format 및 수신단가 확인
				if (checkRecvData(RxBuf)==0 && compareBasePrice(RxBuf)==true) {
					lineErrCnt=0;
					return true;
				} else {
					continue;
				}
			}
		}
		
		lineErrCnt++;
		return false;
	}
	
	protected short checkRecvData (byte[] byData) throws Exception, SerialConnectException {
		
		if (compareNozID(byData) == false) { // NAK 안보냄
			LogUtility.getPumpALogger().info("1.Recv STX NozID mismatch-1.0! (Noz=" + nozNo + ")");
			Log.datas(byData, 40, 20);

			trimInputStream("routine : 1");
			return 1; // 반복
		}
		
		if (compareBCC(byData) == false) {
			sendText(NAK);
			LogUtility.getPumpALogger().info("1.Recv STX BCC fail! (Noz=" + nozNo + ")");
			Log.datas(byData, 40, 20);
			return 1; // 반복
		}
		
		if (verifyData(byData) == false) {
			LogUtility.getPumpALogger().info("1.Recv STX Data verify fail! (Noz=" + nozNo + ")");
			Log.datas(byData, 40, 20);
			return 2; // 종료
		}
		
		if (byData[4]=='P' && byData[5]=='C') { // 단가정보
			if (compareBasePrice(byData) == false) {
				LogUtility.getPumpALogger().info("1.Recv basePrice verify fail! (Noz=" + nozNo + ")");
				Log.datas(byData, 40, 20);
				lineErrCnt++;
				return 2; // 종료
			}
		}
		
		return 0; // 정상
	}
	
	protected boolean compareBasePrice (byte[] buf) throws Exception {
		
		try {
			if (m_basePrice.equals("000000")) // 단가정보 미수신 상태
				return true;
			else {
				byte[] byt = new byte[4];
				System.arraycopy(buf, 6, byt, 0, 4);
				String bPriceStr = new String(byt) + "00";
				//LogUtility.getPumpALogger().debug(.append("\n bPriceStr===" + bPriceStr + " m_basePrice===" + m_basePrice);
				
				return bPriceStr.equals(m_basePrice);
			}
		}
		catch (Exception e) {
			return false;
		}
	}
	
	protected boolean compareBCC(byte[] buf) throws Exception {
		int	i;
	
		for (i=0; i<buf.length-2; i++) {
			if (buf[i]==ETX) 
				break;
		}
		
		if (getBCC (buf) == buf[i+1])		
			return true;
		else
			return false;
	}

	protected boolean compareNozID(byte[] buf) throws Exception {
		
		if (buf[1]==nozID[0] && buf[2]==nozID[1])		
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
	
	protected byte getBCC(byte[] dat) throws Exception {
		
		byte 	bcc=0;
		int 	stxIdx=0, etxIdx=0, i, j;
		
		for (i=0; i<dat.length-1; i++) {
			if (dat[i]==SOH) 
				break;
		}
		stxIdx = i + 1; // start from STX + 3
		
		for(j=i; j<dat.length-1; j++) {
			if (dat[j]==ETX)
				break;
		}
		etxIdx = j;
		
		if (stxIdx<=0 || etxIdx<=0) // No exist STX, ETX
			return -1;
		
		//--- Calculate BCC
		for (i=stxIdx; i<=etxIdx; i++) {
			bcc = (byte) (bcc ^ dat[i]); // XOR
		}
		
		return bcc;
	}
	
	protected boolean isSkipWorkingMessage (WorkingMessage wm) throws Exception {
		
		boolean skip=false;
		String bPrice;
				
		if (wm.getCommand().equals("QF")) { // 프리셋 자료 요청
			
			HF_wm = new HF_WorkingMessage();
			HF_wm.setNozzleNo(Change.toString("%02d", nozNo));
			HF_wm.setType(presetType);
			HF_wm.setLiter(presetLiter);
			HF_wm.setBasePrice(presetBasePrice);
			HF_wm.setPrice(presetPrice);
		
			insertRecvQueue(HF_wm);
			
			skip = true;
		}	
		else if (wm.getCommand().equals("PB")) { // 정액정량 설정
			
			// 단가를 e0_ds에 저장한다.
			PB_WorkingMessage PB_wm = (PB_WorkingMessage) wm;
			bPrice = PB_wm.getBasePrice();
			m_downBasePrice = bPrice;
			
			// 단가변경
			PC_ds.editString("basePrice", bPrice.substring(0, 4) , 4);
			
			LogUtility.getPumpALogger().info("[Pump A][수신-주유허가]" + " nozzle=" + 
					PB_wm.getNozzleNo() + "("+PB_wm.getCommand()+")" +
					" liter =" + PB_wm.getLiter() +
					" | bPrice=" + PB_wm.getBasePrice() +
					" | price =" + PB_wm.getPrice());
										
			if (nozState==0 || nozState==2) { // 추가(08/10/16)
				
				setNozzleBasePrice();
				changeBasePrice ();
				
				LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> PB 수신, 노즐다운상태, 주유허가(AP) : progStep=" + 
						progressStep + " nozState=" + nozState + " m_basePrice=" + m_basePrice);

				skip = false;
			} else 
				skip = true; // 노즐업 또는 주유중이면 단가변경 않는다.
		}
		else if (wm.getCommand().equals("P3_1")) { // 주유기 환경설정

			// 단가를 e0_ds에 저장한다.
			P3_1_WorkingMessage P3_wm = (P3_1_WorkingMessage) wm;
			bPrice = P3_wm.getBasePrice();
			m_downBasePrice = bPrice;

			PC_ds.editString("basePrice", bPrice.substring(0, 4) , 4);

			if (nozType==2 || nozState==0 || nozState==2) { // 추가(08/10/16)
				setNozzleBasePrice();
				LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> P3_1 수신, 노즐다운상태, 단가변경(PC) : progStep=" + 
						progressStep + " nozState=" + nozState + " m_basePrice=" + m_basePrice);

				skip = false;
			} else 
				skip = true; // 노즐업 또는 주유중이면 단가변경 않는다.
		}
		else if (wm.getCommand().equals("PA")) { // 노즐제어 요청(비상정지/해제)

			PA_WorkingMessage PA_wm = (PA_WorkingMessage) wm;
			int nNozState = Change.toValue(PA_wm.getNozzleState());

			m_nozLock = (nNozState==0 ? true : false);

			if (m_nozLock==true) {
				m_recvedNozLock=true;
				makeStatusInfo(656);
			}
			else {
				if (nozType != 2) // 추가(08/12/02)
					TxQue.enQueue(AP_Buf);
				makeStatusInfo(657);
			}

			skip = false;
		}
		else if (wm.getCommand().equals("P7")) { // 주유기 파라미터 설정

			P7_WorkingMessage P7_wm = (P7_WorkingMessage) wm;
			readBuffInterval   = baseReadBuffInterval + Change.toValue(P7_wm.getReadBuffInterval());
			readStartInterval  = baseReadStartInterval + Change.toValue(P7_wm.getReadStartInterval());
			writeStartInterval = baseWriteStartInterval + Change.toValue(P7_wm.getWriteStartInterval());
			minErrCnt 		   = baseMinErrCnt + Change.toValue(P7_wm.getLineErrorCount());
			
			// init values
			dispLevel=0;
			m_isSaveSTX=false;
			
			int lineErrSkipCnt = Change.toValue(P7_wm.getLineErrorSkipCount());
			if (lineErrSkipCnt >= 9000 && lineErrSkipCnt <= 9003)
				dispLevel = lineErrSkipCnt - 9000; // dispLevel : 0 ~ 3
			else
				maxSkipCnt = baseMaxSkipCnt + lineErrSkipCnt;
						
			LogUtility.getPumpALogger().info("Received nozzle parameters(P7). nozzle=" + nozStr);
			LogUtility.getPumpALogger().info(" -readStartInterval ="+P7_wm.getReadStartInterval());
			LogUtility.getPumpALogger().info(" -writeStartInterval="+P7_wm.getWriteStartInterval());
			LogUtility.getPumpALogger().info(" -readBuffInterval  ="+P7_wm.getReadBuffInterval());
			LogUtility.getPumpALogger().info(" -minLineErrCount   ="+P7_wm.getLineErrorCount());
			LogUtility.getPumpALogger().info(" -maxLineErrSkipCnt ="+P7_wm.getLineErrorSkipCount());
			
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

		//if (m_statusCode==601)
			//return;

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
	
	protected void makePumpingStartInfo (String gauge) throws Exception, SerialConnectException {
		
		SJ_wm = new SJ_WorkingMessage();
		SJ_wm.setNozzleNo(Change.toString("%02d",nozNo));
		SJ_wm.setTotalGauge(gauge);
					
		insertRecvQueue(SJ_wm); // 주유시작 자료 송신
		
		sentPumpingStartInfo=true;
		waitTotalGuageFor_SJ=false;
		waitGaugeForSJ_Cnt=0;
		
		//LogUtility.getPumpALogger().info("makePumpingStartInfo ()" + gauge);
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
			case 655:
				statusCode = 655; // 주유기 점검(ENE 에러코드(E) 수신)
				errorMsg = "주유기 점검";
				break;
			case 656:
				statusCode = 656; // 비상정지(주유금지)
				errorMsg = "비상정지";
				break;
			case 657:
				statusCode = 657; // 비상정지해제
				errorMsg = "비상해제";
				break;
			case 650:
				statusCode = 650; // 정상
				errorMsg = "정상";
				break;
		}

		if (m_statusCode==statusCode)
			return;
		
		m_statusCode = statusCode;
				
		S8_wm.setNozzleNo(Change.toString("%02d", nozNo));
		S8_wm.setDeviceType(Change.toString("%02d", nozType));
		S8_wm.setStatus("1");
		S8_wm.setStatusCode(Change.toString("%03d", statusCode)); 
		S8_wm.setNozzleState(Change.toString("%01d", nozState)); 
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

	public void processRecvSTX () throws SerialConnectException, Exception {
	
		//############# State check ##############//
		if (RxBuf[4]=='L' && RxBuf[5]=='K') { // 허가전 노즐다운
			nozState = (short) (nozState==4 ? 5 : 0);
		} 
		else if (RxBuf[4]=='A' && RxBuf[5]=='Q') { // 허가전 노즐업
			nozState = 1;
		}
		else if (RxBuf[4]=='U' && RxBuf[5]=='L') { // 허가후 노즐다운
			nozState = 2;
		}
		else if (RxBuf[4]=='P' && RxBuf[5]=='P') { // 허가후 노즐업(주유시작, 주유중)
			nozState = 4;
		}
		else if (RxBuf[4]=='T' && RxBuf[5]=='R') { 
			nozState = 5;
		} 
		else if (RxBuf[4]=='C' && RxBuf[5]=='T') { // 토털게이지 수신(주유시작 용)
		
			if (nozState==3) { // 노즐업 상태에서 수신
				nozState = 4;
			}
			else if (progressStep==4) { // 주유완료후 수신
				progressStep = 5;
			} 
			else 
				insertRecvQueue(generateWorkingMessage(RxBuf, "S5")); // 토털게이지 자료 송신
		}

//		LogUtility.getPumpALogger().debug("#### STX Start : nozzle=" + nozStr + " ProgStep=" +
//				progressStep + " nozState=" + nozState + " pumpingStart=" + pumpingStart);
		
		//############# STX Process ##############//
		switch (progressStep) {
		case 3 :
			if (nozState==0 || nozState==2) { // 노즐다운

				flushBuffer(lastPumpingData);
				sentPumpingStartInfo=false;
				waitTotalGuageFor_SJ=false; 
				waitGaugeForSJ_Cnt=0; 
				
				if (sentNozDownInfo==false) { 
					makeStatusInfo(nozState); // 상태정보 전송
					sentNozDownInfo=true;
				}
				
				setNozzleBasePrice();

				// 비상정지/해제 상태정보 처리
				if (m_nozLock==true)
					makeStatusInfo(656); // 비상정지
				else if (m_recvedNozLock==true) {
					makeStatusInfo(657); // 비상해제
					m_recvedNozLock=false;
				}

				// 노즐업다운시 값=0 주유완료 자료 전송 -> POS에서 프리셋 설정후 취소처리용
				// POS 프리셋 여부는 모듈에서 판단
				if (beforeNozzleUp==true && realPumpingStart==false && // 정상 주유완료자료가 아니면
						sentPumpingEndInfo==false) {
					
					S4_wm = new S4_WorkingMessage();
					S4_wm.setNozzleNo(Change.toString("%02d",nozNo));
					S4_wm.setLiter("0000000");
					S4_wm.setBasePrice(m_basePrice);
					S4_wm.setPrice("00000000");
					S4_wm.setTotalGauge("0000000000"); 
					
					if (nozType==2) { // 셀프주유기이면
						if (nozState==2) {
							insertRecvQueue(S4_wm); // 주유완료 자료
							LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 노즐업다운, 선결제 주유취소용 주유완료전문(S4) 생성");
						}
					} 
					else {
						insertRecvQueue(S4_wm); // 주유완료 자료
						LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 노즐업다운, 선결제 주유취소용 주유완료전문(S4) 생성");
					}
					
					sentPumpingEndInfo=true;
					waitTotalGuageFor_SJ=false; 
				}
				
				beforeNozzleUp=false;
			}
			else if (nozState==1 || nozState==3) { // 노즐업

				flushBuffer(lastPumpingData);
				
				if (nozState==1) {
					changeBasePrice();
					
					if (m_nozLock==false && sentPumpingStartInfo==false) {
						SJ_TotalGauge = recvTotalGauge(); // --- 시작게이지 수신처리 ---//
						makePumpingStartInfo(SJ_TotalGauge); // --- 주유시작(SJ)전문 생성 ---//
					}
	
					if (m_nozLock == false && nozType == 1 && pumpingStart == false) {
						if (Change.toValue(m_basePrice) > 0) {
							// 통상 주유허가(AP)
							if (pumpingEnable() == false)
								return;
							else {
								nozState = 3;
								LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 노즐업, 주유허가(AP) : progStep="
												+ progressStep + " nozState=" + nozState + " m_basePrice="
												+ m_basePrice);
							}
						}
					}
				}
				
				if (nozType==2) { 
					if (nozState==3)
						beforeNozzleUp=true;
				} else
					beforeNozzleUp=true;
				
				realPumpingStart=false;
				sentPumpingEndInfo=false;
				sentNozDownInfo=false;
				
				setNozzleBasePrice();		
				makeStatusInfo(nozState); 
					
			}
			else if (nozState == 4) { // 주유중
				
				if (pumpingStart==false) {
					flushBuffer(lastPumpingData);
					pumpingStart=true;
				}
				
				if (RxBuf[4]=='P' && RxBuf[5]=='P') {

					S3_wm = (S3_WorkingMessage) generateWorkingMessage(RxBuf, "S3");
					S3_wm.setBasePrice(m_basePrice);
					
					int nPrice=0, nLiter=0;
					if (S3_wm != null) {
						nPrice = Change.toValue(S3_wm.getPrice());
						nLiter = Change.toValue(S3_wm.getLiter());
					}

					// 이전 주유량 수신시 Skip 을 위한 처리
					if (nPrice > 0 && nLiter > 0) {	
						if (newPumpingData==false && nLiter==m_nLiter) {
							LogUtility.getPumpALogger().info("nozzle=" + nozNo + 
									" -> 주유량(PP)이 이전 주유량과 동일합니다. : 주유량(x.3)=" + 
									nLiter + " 이전 주유량(x.3)=" + m_nLiter);
						}
						else {
							lastPumpingData = RxBuf.clone(); // 최종 주유정보 저장
							realPumpingStart=true;
							newPumpingData=true;
						}
					}

					if (presetDataFlag==true) 
						presetDataFlag=false; // Preset data 이면 skip
					else {
						if (nPrice > 0 && nLiter > 0) {
							if (firstPumpingData==true) {
								makeStatusInfo(nozState); // 상태정보 전송(주유중)
								firstPumpingData=false;
							}
							if (newPumpingData==true) 
								insertRecvQueue(S3_wm); // 주유중 자료 모듈로 송신
						}
					}
				}
				
				sentPumpingEndInfo=false; 	
				sentNozDownInfo=false;
			}
			else if (nozState == 5) { // 주유완료

				if (pumpingStart==false) {
					flushBuffer(lastPumpingData);
					pumpingStart=true;
				}
				
				TxQue.enQueueNewer(TQ_Buf); // 주유완료 자료 요청
				
				progressStep=4;
			}
			break;

		case 4 :
			
			// 최종 주유자료 수신 처리			
			if ((RxBuf[4]=='T' && RxBuf[5]=='R') || waitLastPData_Cnt>=MAX_LAST_PDATA) {
				
				if ((RxBuf[4]=='T' && RxBuf[5]=='R')) {
					
					byte[][] tBuf = new byte [2][buffSize];
					tBuf[0] = RxBuf.clone(); // 최종 주유정보
					//tBuf[1] = RxBuf.clone(); // Total guage
					
					S4_wm = (S4_WorkingMessage) generateWorkingMessage(tBuf, "S4");
					int nPrice=0, nLiter=0;
					if (S4_wm != null) {
						nPrice = Change.toValue(S4_wm.getPrice());
						nLiter = Change.toValue(S4_wm.getLiter());
					}
					if (nPrice > 0 || nLiter > 0)  
						lastPumpingData = RxBuf.clone(); // 최종 주유정보 저장

					LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 주유완료, 최종주유값 수신(TR) : progStep=" + 
								progressStep + " waitLastPData_Cnt=" + waitLastPData_Cnt + 
								" liter=" + S4_wm.getLiter() + 
								" price=" + S4_wm.getPrice());
				} else
					LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 주유완료, 최종주유값 미수신(TR), 이전주유값 적용 : progStep=" + 
							progressStep + " waitLastPData_Cnt=" + waitLastPData_Cnt);
				
				TxQue.enQueueNewer(CQ_Buf); // 토털게이지 요청
				
				LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 주유완료, 토털게이지 요청(CQ) : progStep=" + 
						progressStep + " nozState=" + nozState + " pumpingStart=" + pumpingStart);
				
				makeStatusInfo(nozState); // 상태정보 전송(주유완료)
				
				// Clear preset-data
				presetType="2";
				presetLiter="0000000";
				presetBasePrice="000000";
				presetPrice="000000";

				waitLastPumpData=false;
				waitLastPData_Cnt=0;
				sentNozDownInfo=false;
			}
			else {
				waitLastPumpData=true;
				
				LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 주유완료, 최종주유값 수신대기 : nozState=" +
						nozState + " waitLastPData_Cnt=" + waitLastPData_Cnt);
			}
				
			break;

		case 5 : // 주유완료후 토털게이지 수신

			byte[][] tBuf = new byte [2][buffSize];

			tBuf[0] = lastPumpingData.clone(); // 최종 주유정보
			flushBuffer(lastPumpingData);
			tBuf[1] = RxBuf.clone(); // Total guage
			
			progressStep = 3;
			nozState = 0;
			progressStep4Cnt=0;

			pumpingStart=false;
			sentPumpingStartInfo=false;
			firstPumpingData=true;
			newPumpingData=false;
			
			LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 주유완료, 토털게이지 수신(CT) : progStep=" + 
					progressStep + " nozState=" + nozState + " pumpingStart=" + pumpingStart);

			setNozzleBasePrice();
			/*
			if (m_nozLock==false && nozType==1) { // 일반주유기
				if (Change.toValue(m_basePrice) > 0) {
					TxQue.enQueueNewer(AP_Buf); // 주유허가(정액/정량 설정)				
					LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 토털게이지 수신, 주유허가(AP) : progStep=" + 
							progressStep + " nozState=" + nozState + " m_basePrice=" + m_basePrice);
				}
			}
			*/

			S4_wm = (S4_WorkingMessage) generateWorkingMessage(tBuf, "S4");
			S4_wm.setNozzleNo(Change.toString("%02d",nozNo));
			
			int nPrice=0, nLiter=0; long nGauge=0;
			if (S4_wm != null) {
				nPrice = Change.toValue(S4_wm.getPrice());
				nLiter = Change.toValue(S4_wm.getLiter());
				nGauge = Change.toLongValue(S4_wm.getTotalGauge());
			}
			m_nPrice = nPrice;
			m_nLiter = nLiter;
			
			if (nGauge==0)
				LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 토털게이지값이 0 이거나, 미수신하여 0 으로 생성");
			
			 // 수정(08/09/10)
			if (nLiter <= 0 || nPrice <= 0) {
				S4_wm.setBasePrice(m_basePrice);
				LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 주유완료(S4) 자료값=0 : Liter=" + 
						nLiter + " Price=" + nPrice);
			}
			if (sentPumpingEndInfo==false) {
				insertRecvQueue(S4_wm); // 주유완료 자료
				sentPumpingEndInfo=true;
				waitTotalGuageFor_SJ=false;
			}
			
			//changeBasePrice();
			makeStatusInfo(0); // 상태정보(노즐다운) 전송
			
			break;
		}
	}
	
	protected boolean pumpingEnable () throws Exception, SerialConnectException {
		
		TxBuf = AP_ds.getByteStream();
		TxBuf[1] = nozID[0];
		TxBuf[2] = nozID[1];
		setBCC (TxBuf); // write BCC
		
		for (int i=0; i<3; i++) {
			
			if(sendText(TxBuf) != true) { // 통상 주유허가(AP)
				if (dispLevel>=3) {
					LogUtility.getPumpALogger().info("1.Send STX fail! (Noz="+nozNo+")");
					Log.datas(TxBuf, TxBuf.length, 20);
				}
				continue;
			}
			
			if (recvText(RxBuf) < 1) { // recv ACK
				if (dispLevel>=3)
					LogUtility.getPumpALogger().info("1.Recv ACK fail! (Noz="+nozNo+")");
				continue;
			} else {
				if (RxBuf[0]==ACK) {
					lineErrCnt=0;
					return true;
				}
				else {
					continue;
				}
			}
		}
		
		lineErrCnt++;
		return false;
	}
	
	public int recvCmd(byte[] RxBuf) throws Exception, SerialConnectException {

		int		c=0, i=0, loop=0;
		byte[]	RxByt = new byte[1];
		int		recvLoopCnt=readCmdInterval/10;
		int		len=2;
		
		flushBuffer (RxBuf);
		Sleep.sleep(readStartInterval);

		try {
			
			for (c=0; c<RxBuf.length; c++) {
				
				RxByt[0] = 0x00;
				
				if (is.read(RxByt, 0, 1) < 1) { // no recv data
					//---check timeout
					if (loop >= recvLoopCnt) break;
					else loop++;
					
					c--;
					Sleep.sleep(2);
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
			LogUtility.getPumpALogger().error(e.getMessage(), e);
			return 0;
		}
	}
	
	protected boolean recvTail_proc (byte ACK_NAK) throws Exception, SerialConnectException {

		sendText(ACK_NAK);
		
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
					if (loop >= recvLoopCnt) break;
					else loop++;

					c--;
					Sleep.sleep(2); // wait
					continue;
				} else
					loop=0;

				RxBuf[i] = RxByt[0];
				
				if (RxBuf[i]==STX)
					STXFlag=true;

				if (STXFlag==false)
					if (RxBuf[i]==EOT || RxBuf[i]==ACK || RxBuf[i]==NAK) { // recv EOT/ACK/NAK
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
					else { // recv ETX none STX
						flushBuffer(RxBuf);
						numBytes=0;
						break;
					}
				}
								
				i++;
			}
			return numBytes;
			
		} catch (IOException e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
			return 0;
		}
	}
	
	
	protected String recvTotalGauge () throws Exception, SerialConnectException {

		byte[] byGauge = new byte[10];
		String gauge="0000000000";
		TxBuf = CQ_ds.getByteStream();
		TxBuf[1] = nozID[0];
		TxBuf[2] = nozID[1];
		setBCC (TxBuf); // write BCC
				
		for (int i=0; i<3; i++) {
			
			if(sendText(TxBuf) != true) { // 게이지값 요청(CQ)
				if (dispLevel>=3) {
					LogUtility.getPumpALogger().info("1.Send STX(CQ) fail! (Noz="+nozNo+")");
					Log.datas(TxBuf, TxBuf.length, 20);
				}
				continue;
			}
			
			flushBuffer(RxBuf);
			if (recvText(RxBuf) < 1) { // recv gauge(CT) fail
				if (dispLevel>=3)
					LogUtility.getPumpALogger().info("1.Recv STX(CT) fail! (Noz="+nozNo+")");
				continue;
			}
			else { // success
				if (checkRecvData(RxBuf)==0) { // ID, BCC, Data format 확인 -> 0=정상
					System.arraycopy(RxBuf, 6, byGauge, 0, 10);
					gauge = new String(byGauge);
					lineErrCnt=0;
					return gauge;
				}
				else {
					continue;
				}
			}
		}
		
		lineErrCnt++;
		return gauge;
	}

	@Override
	public void requestData() throws SerialConnectException, Exception {
			
		byte[] RxCmd = new byte[2];
		
		// 회선불량 처리
		boolean	occurLineErr = (lineErrCnt >= minErrCnt);
			
		if (occurLineErr) { // Check line error

			if (issueLineErr==true) {
				makeLineError(); // 회선불량
				TxQue.flushQueue();
				issueLineErr=false;
				sentNozDownInfo=false;
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
		if (firstRequest==true) {
			firstRequest=false;
			TxQue.enQueueNewer(CQ_Buf); // 상태정보요청
		}
		if (m_statusCode==601) { // 회선불량
			if (lineCommCheckCnt%(minErrCnt+5)==0) {
				lineCommCheckCnt=0;
				TxQue.enQueueNewer(CQ_Buf); // 상태정보요청
			}
			lineCommCheckCnt++;
		}
						
		// 주유완료(S4) 최종주유값 미수신 처리
		if (waitLastPumpData==true) {
			if (waitLastPData_Cnt >= MAX_LAST_PDATA) {
				try {
					processRecvSTX();		
				} catch (SerialConnectException e) {
					LogUtility.getPumpALogger().error(e.getMessage(), e);
				} catch (Exception e) {
					LogUtility.getPumpALogger().error(e.getMessage(), e);
				}
			}
			else
				waitLastPData_Cnt++;
			
			LogUtility.getPumpALogger().debug("waitLastPData_Cnt="+waitLastPData_Cnt+" progStep="+
					progressStep+" nozState="+nozState);
		}
		
		// 주유완료(S4) 토털게이지 미수신 처리
		if (progressStep==4) {
			if (progressStep4Cnt >= MAX_PROG4STEP) {
				
				progressStep4Cnt = 0;
				progressStep = 5;
				try {
					processRecvSTX();
					flushBuffer(lastPumpingData);
					
					LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 주유완료, 토털게이지(CT) 미수신, processSTX() 호출.");
				} catch (SerialConnectException e) {
					LogUtility.getPumpALogger().error(e.getMessage(), e);
				} catch (Exception e) {
					LogUtility.getPumpALogger().error(e.getMessage(), e);
				}
			}
			else
				progressStep4Cnt++;
			
			LogUtility.getPumpALogger().debug("progressStep4Cnt="+progressStep4Cnt+" progStep="+
					progressStep+" nozState="+nozState);
		}

		//--- Transfer from WorkingMessage to BytesStream
		while (sndQue.getItemCount() > 0) {
			WorkingMessage wm = (WorkingMessage)sndQue.deQueue();

			LogUtility.getPumpALogger().debug("\n>>>>> Received  Down data : " +
								"nozNo=" + nozStr + " command=" + wm.getCommand());

			if (isSkipWorkingMessage(wm)==true) {
				wm=null;
				continue;
			}
			
			byte[] wmByte = generateByteStream(wm);
			if (wmByte != null) 
				TxQue.enQueue(wmByte);
			/*
			if (wm.getCommand().equals("PB")) {
				LogUtility.getPumpALogger().debug(":::::::::>>> ByteStreams of workingMessage ["+ wm.getCommand() + "] in requestData().");
				Log.datas(wmByte, wmByte.length, 20);
			}*/
			
			wm=null;
		}
		if (dispLevel>=3)
			LogUtility.getPumpALogger().debug("\nStart request ============> SK, nozNo=" + nozNo);

		try {
			//##### Send ENQ and Recv ACK/STX #####//
			if (dispLevel>=3)
				LogUtility.getPumpALogger().debug("----POLLING----(progStep="+progressStep+", nozState="+nozState+")");
			
			//--- Send ENQ ---//
			if(sendText (enq_Buf) != true) { // fail
				lineErrCnt++;
				return;
			}

			if (dispLevel>=3)
				LogUtility.getPumpALogger().info("0.Send ENQ (Noz="+nozNo+")");
			
			//--- Loop for recv ACK/STX ---//
			for (int i=0; i<3; i++) {
				
				flushBuffer(RxBuf);

				//--- recv ACK/STX
				if (recvText(RxBuf) < 1) {
					lineErrCnt++;
					if (dispLevel>=2)
						LogUtility.getPumpALogger().info("0.Recv ACK/STX fail!(No response) lineErrCnt="+lineErrCnt+"/"+minErrCnt+" (Noz="+nozNo+")");
					else if (lineErrCnt >= minErrCnt)
						LogUtility.getPumpALogger().debug("0.Recv ACK/STX fail!(No response) lineErrCnt="+lineErrCnt+"/"+minErrCnt+" (Noz="+nozNo+")");

					return;
				}
			
				//###### Recv data : STX ######//
				if (RxBuf[0] == SOH || RxBuf[0] == ACK) {
					
					if (RxBuf[0] == SOH) {
						
						if (dispLevel>=3) {
							LogUtility.getPumpALogger().info("1.Recv STX (Noz="+nozNo+")");
							Log.datas(RxBuf, 40, 20);
						}
						
						short result = checkRecvData(RxBuf); // ID, BCC, Data format 확인
						
						if (result==1) {
							lineErrCnt++;
							continue;
						} else if (result==2) {
							lineErrCnt++;
							return;
						} else if (result==0) { //  STX 정상수신
							
							if (dispLevel==1 || dispLevel==2) {
								LogUtility.getPumpALogger().info("1.Recv STX with normal (Noz="+nozNo+")");
								Log.datas(RxBuf, 40, 20);
							}
							
							if (m_isSaveSTX==true) 
								LogUtility.getPumpALogger().info("Recv STX("+nozStr+") : ["+pack(RxBuf, ETX)+"]");
							else
								LogUtility.getPumpALogger().debug("Recv STX("+nozStr+") : ["+pack(RxBuf, ETX)+"]");
							
							recvTail_proc(ACK);						
							processRecvSTX(); //--- 노즐 수신데이터 처리 ---//
					
							lineErrCnt=0; // Normal terminated
							//return;
						}
					} 
					else if (RxBuf[0] == ACK) { // recv : ACK
	
						if (dispLevel>=3)
							LogUtility.getPumpALogger().info("2.Recv ACK (Noz="+nozNo+")");
					}

					if (TxQue.isEmpty()==false) { // 송신 데이터 있으면
						
						//###### Send data : send working-data ######//
						TxBuf = TxQue.getFirstItem();
						TxBuf[1] = nozID[0];
						TxBuf[2] = nozID[1];
						setBCC (TxBuf); // write BCC
					
						if(sendText(TxBuf) != true) {
							if (dispLevel>=3) {
								LogUtility.getPumpALogger().info("2.Send STX fail! (Noz="+nozNo+")");
								Log.datas(TxBuf, TxBuf.length, 20);
							}
							lineErrCnt++;
							return;
						}
	
						if (dispLevel>=3) {
							LogUtility.getPumpALogger().info("2.Send STX(TxBuf) (Noz="+nozNo+")");
							Log.datas(TxBuf, TxBuf.length, 20);
						}

						//--- Loop for recv ACK/NAK ---//
						for (int j=0; j<3; j++) {
							
							flushBuffer(RxBuf);
							if (recvText(RxBuf) < 1) {
								if (dispLevel>=3)
									LogUtility.getPumpALogger().info("2.Recv ACK fail! (Noz="+nozNo+")");
								lineErrCnt++;
								return;
							}
														
							if (RxBuf[0] == ACK) { // recv : ACK
								if (dispLevel >= 3)
									LogUtility.getPumpALogger().info("2.Recv ACK (Noz=" + nozNo + ")");

								if (dispLevel == 2) {
									LogUtility.getPumpALogger().info("2.Send STX with normal (Noz=" + nozNo + ")");
									Log.datas(TxBuf, TxBuf.length, 20);
								}

								// LogUtility.getPumpALogger().debug("Send STX : ["+new String(TxBuf)+"]");
								//sendTail_proc();
								
								// --- 송신완료후 송신데이터 제거 ---//
								TxQue.deQueue();
								lineErrCnt = 0; // Normal terminated
								return;
							}
							if (RxBuf[0] == SOH) { // recv reply data : SOH
								
								if (dispLevel>=3) {
									LogUtility.getPumpALogger().info ("2.Recv STX (Noz="+nozNo+")");
									Log.datas(RxBuf, 40, 20);
								}
								
								short result = checkRecvData(RxBuf); // ID, BCC, Data format 확인
								
								if (result==1) {
									lineErrCnt++;
									continue;
								} else if (result==2) {
									lineErrCnt++;
									return;
								} else if (result==0) { //  STX 정상수신
									
									if (dispLevel==1 || dispLevel==2) {
										LogUtility.getPumpALogger().info ("2.Recv STX with normal (Noz="+nozNo+")");
										Log.datas(RxBuf, 40, 20);
									}
									
									if (m_isSaveSTX==true) 
										LogUtility.getPumpALogger().info("Recv STX("+nozStr+") : ["+pack(RxBuf, ETX)+"]");
									else
										LogUtility.getPumpALogger().debug("Recv STX("+nozStr+") : ["+pack(RxBuf, ETX)+"]");

									recvTail_proc(ACK);
									processRecvSTX(); //--- 노즐 수신데이터 처리 ---//
								}
								
								// --- 송신완료후 송신데이터 제거 ---//
								TxQue.deQueue();
								lineErrCnt = 0; // Normal terminated
								return;
							}
							else if (RxBuf[0] == NAK) {	// recv : NAK
								
								sendText(TxBuf); // 재전송
									
								LogUtility.getPumpALogger().info("2.Send STX fail!(Returned NAK)->retry send (Noz="+nozNo+")");
								Log.datas(TxBuf, TxBuf.length, 20);
								continue;
							}							
						} // end of for (int j=0; j<3; j++)
					} 
					else { //--- 송신 데이터 없으면 ---//
						//sendTail_proc();		
						lineErrCnt=0; // Normal terminated
						return;
					}
				}
				else {
					if (dispLevel>=2) {
						LogUtility.getPumpALogger().info("0.Recv ACK/STX fail!(Unknown Data)! (Noz="+nozNo+")");
						Log.datas(RxBuf, 40, 20);
					}
					lineErrCnt++;
					continue;
				}
			} // end of for (int i=0; i<3; i++)
				
		} catch (Exception e) {
			LogUtility.getPumpALogger().error(":::::::Exception occurr! (Noz="+nozNo+")");
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	}
	
	protected void retryRecvData (byte[] buf) throws Exception {
		
//		if (buf[4]=='s' && buf[5]=='0') {
//			TxQue.enQueueNewer(s0_Buf);
//			LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> 상태정보(s0) 재요청");
//		}
//		else if (buf[4]=='p' && buf[5]=='0') {
//			TxQue.enQueueNewer(p0_Buf);
//			LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> 주유자료(p0) 재요청");
//		}
//		else if (buf[4]=='t' && buf[5]=='0') {
//			TxQue.enQueueNewer(t0_Buf);
//			LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> 토털게이지(t0) 재요청");
//		}
		
	}

	public void run() {

	}

	protected boolean sendTail_proc () throws Exception, SerialConnectException {
				
		return true;
	}
	
	public boolean sendText(byte cmd) throws Exception, SerialConnectException {

		byte[]	sndBuf = new byte[1];
		boolean	rtn;

		Sleep.sleep(writeStartInterval);
		
		//--- send : SA + ENQ ---//
		sndBuf[0] = cmd;
		rtn = sendText(sndBuf);
		if(rtn==false) {
			System.out.printf ("\n0.Send ENQ fail! (Noz=%02d)\n", nozNo);
			return false;
		}
		//System.out.printf ("Send Text : 0x%02X 0x%02X\n", sndBuf[0], sndBuf[1]);
	
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

	protected void setBCC(byte[] buf) throws Exception {
		int	i;
		
		for (i=0; i<buf.length-2; i++) {
			if (buf[i]==ETX) 
				break;
		}
		
		buf[i+1] = getBCC(buf);
	}
		
	protected void setNozzleBasePrice() throws Exception {

		PC_Buf = PC_ds.getByteStream();
		m_basePrice = m_downBasePrice;
	}

	protected void trimInputStream(String msg) throws Exception {
		
		Sleep.sleep(readStartInterval);
		byte[] trimBuf = new byte[40];
				
		int len = is.read(trimBuf); // trim inputStream
		//LogUtility.getPumpALogger().debug("Trim inputStream (" + msg + ") :" + " len=" + len + " (Noz="+nozNo+")");
		//Log.datas(trimBuf, 40, 20);
	}

	protected boolean verifyData (byte[] buf) throws Exception {
		
		int startIdx=0, size=0, bufLen;
		String cmd="";
				
		for (bufLen=0; bufLen<buf.length; bufLen++)
			if (buf[bufLen]==ETX) break;
		bufLen+=2;

		if (buf[4]=='P' && buf[5]=='C') { // 단가정보
			size = 12;
			cmd = "PC";
			startIdx=6;
		}
		else if (buf[4]=='P' && buf[5]=='P') { // 주유중 자료
			size = 22;
			cmd = "PP";
			startIdx=6;
		}
		else if (buf[4]=='T' && buf[5]=='R') { // 주유완료 자료
			size = 26;
			cmd = "TR";
			startIdx=6;
		}
		else if (buf[4]=='C' && buf[5]=='T') { // 토털게이지
			size = 18;
			cmd = "CT";
			startIdx=6;
		}
		else {
			return true;
		}
		
		//--- 데이터 검증(데이터 길이와 ETX 확인) ---//
		if (size != bufLen || buf[size-2] != ETX) {
			LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> 이상데이터 수신("+cmd+"), 데이터길이=" + 
					size + ", 수신데이터길이=" + bufLen + ", ETX=" + buf[size-1]);
			return false;
		}

		//--- 데이터 검증(숫자여부) ---//
		for (int i=startIdx; i<size-2; i++) {
			if (buf[i] < 0x30 || buf[i] > 0x39) { // 0 ~ 9 가 아니면
				LogUtility.getPumpALogger().debug("nozzle=" + nozNo + 
						" -> 이상데이터 수신("+cmd+"), 숫자가 아님, 수신데이터=" + buf[i]);
				return false;
			}
		}
			
		return true;
	}
}
