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
import com.gsc.kixxhub.device.pumpa.common.Command;
import com.gsc.kixxhub.device.pumpa.common.DataStruct;
import com.gsc.kixxhub.device.pumpa.common.Log;
import com.gsc.kixxhub.device.pumpa.common.SerialConnectException;
import com.gsc.kixxhub.device.pumpa.common.Sleep;
import com.gsc.kixxhub.device.pumpa.define.CCommDriver;
import com.gsc.kixxhub.device.pumpa.translation.TransWoojoo;

public class CommWooJoo extends CCommDriver implements SerialPortEventListener, CommPortOwnershipListener, 
	Runnable {

	protected static boolean m_isSaveSTX=false;
	protected byte ACK = 0x06;
	protected int	baseMinErrCnt=8, baseMaxSkipCnt=50;
	protected int	baseReadBuffInterval   = 20+4; // 스마트주유소 PJT 보정
	protected int	baseReadCmdInterval   = 20; // readBuffInterval when read command(ACK/EOT etc.)	
	// 1=비정상 메시지 + 비정상수신 STX + 정상수신 STX
						 			// 2=비정상 메시지 + 비정상수신 STX + 정상송수신 STX
						 			// 3=모든 메시지 + 모든 송수신 STX
	protected int	baseReadStartInterval  = 5+2; // 스마트주유소 PJT 보정
	protected int	baseWriteStartInterval = 5+2; // 스마트주유소 PJT 보정
	protected boolean beforeNozzleUp=false; // 이전 노즐업여부
	
	protected int		buffSize = 40;
	protected byte[] 	c0_Buf = new byte[8];
	protected int	dispLevel=0; 	// 0=비정상 메시지 + 비정상수신 STX
	protected byte[] 	e0_Buf = new byte[26];
	protected DataStruct e0_ds = new DataStruct();
	
    protected byte ENQ = 0x05;
    protected byte EOT = 0x04;
    protected byte ETX = 0x03;
    protected boolean firstPumpingData=true;
    protected boolean firstRequest=true;
    protected HF_WorkingMessage HF_wm;
    protected boolean	issueLineErr=true;
    protected byte[] 	lastPumpingData=new byte[buffSize];
    protected int		lineCommCheckCnt=0;
    protected int		lineErrCnt=0;
    protected String	m_basePrice="000000";
    protected byte 	m_byLastState='0';
    protected String	m_downBasePrice="000000";
    protected boolean 	m_isLogPumpingSTX=false;
    protected int		m_nLiter=0;
        
    protected boolean m_nozLock=false;
    protected int		m_nPrice=0;
					    			protected int		m_nS3_cnt=0;
	protected boolean m_recvedNozLock=false;
	protected int		m_statusCode=0;
	protected int	MAX_GAUGE_SJ  =5;

	protected int	MAX_LAST_PDATA=3;
	protected int	MAX_PROG4STEP =15;
	
	protected int	MAX_StateReq_Cnt=30; // 상태정보 요청주기(0=nothing)
protected int	MAX_WAIT_PDATA=5;
	protected byte NAK = 0x15;
	protected boolean newPumpingData=false; // 이전 주유값 여부

	protected byte		nozID;
	protected short		nozState=0;
    protected String 	nozStr = "";
	protected byte[] 	p0_Buf = new byte[8];
	//protected String	last_SJ_TotalGauge="0000000000";
	//protected String	last_S4_TotalGauge="0000000000";
	protected int		p0_cnt=0;
	protected byte[] 	p0r_Buf = new byte[26];
	protected DataStruct p0r_ds = new DataStruct();
	protected String presetBasePrice="000000";
	protected boolean presetDataFlag=false;
	protected String presetLiter="0000000";
	protected String presetPrice="000000";
	/*
	protected int	baseReadStartInterval =10;
	protected int	baseWriteStartInterval=10;
	protected int	baseReadBuffInterval  =30;
	protected int	baseMinErrCnt 		  =8;
	protected int	baseMaxSkipCnt		  =100;
*/
	// Preset data keeping - for Quick-win
	protected String presetType="2";
	//progressStep : 0=초기화전, 1=초기화후, 2=상태정보송신후, 3=주유중, 4=주유완료, 
    //               5=토털게이지수신/주유완료자료 송신
    protected int 	progressStep=3;
	protected int		progressStep4Cnt=0;
	protected boolean pumpingStart=false;
	protected int	readCmdInterval = 0;
	protected boolean realPumpingStart=false; // 오일토출
	protected byte REQ = 0x05;
	protected int		retryRcvData_Cnt=0;
	protected boolean rtn;
	protected byte[] 	RxBuf = new byte[buffSize];
	protected byte[] 	s0_Buf = new byte[8];
	protected S3_WorkingMessage S3_wm;
	protected S4_WorkingMessage S4_wm;
	protected S5_WorkingMessage S5_wm;
	protected S8_WorkingMessage S8_wm;
	
	protected SE_WorkingMessage SE_wm;
	protected int		send_S0_Cnt=0;
	
    protected boolean sentNozDownInfo=false;

	protected boolean sentPresetData=false;
	protected boolean sentPumpingEndInfo=false; // 추가(09/09/08)
	protected boolean sentPumpingStartInfo=false;
	protected SJ_WorkingMessage SJ_wm;
	protected int		stateReq_Cnt=0;
	protected byte STX = 0x02;
	protected byte[] 	t0_Buf = new byte[8];

	protected byte[] 	t0r_Buf = new byte[18];
	protected DataStruct t0r_ds = new DataStruct();
	protected TransWoojoo trans = new TransWoojoo();
	protected byte[] 	TxBuf = new byte[buffSize];
	protected int		waitGaugeForSJ_Cnt=0;
	protected int		waitLastPData_Cnt=0;
	protected boolean waitLastPumpData=false; // 최종 주유완료값 수신대기
    
	protected boolean waitPumpingData=false; // 주유자료 수신여부
	protected int		waitPumpingData_Cnt=0;
	protected boolean waitTotalGuageFor_SJ=false; // 주유시작 게이지 수신대기
    
    public CommWooJoo (int nozNum, String romVerStr) {
    	
    	byte	bcc;
		Formatter form = new Formatter();
		byte[] 	nozByt = new byte[2];

		nozNo = nozNum;
		romVer = romVerStr;

		form.format("%02d", nozNo);
		nozStr = form.toString();
		nozByt = nozStr.getBytes();
    	nozID = (byte) (nozNo + 0x40);
    	
		readBuffInterval   	= baseReadBuffInterval;
		readStartInterval  	= baseReadStartInterval;
		writeStartInterval 	= baseWriteStartInterval;
		minErrCnt 		   	= baseMinErrCnt;
		maxSkipCnt 		  	= baseMaxSkipCnt;
		
    	try {
        	flushBuffer(lastPumpingData);
        	
	    	//--- p0 (Request pumping data) ---//
	    	p0_Buf[0] = nozID;
	    	p0_Buf[1] = STX;
	    	p0_Buf[2] = nozByt[0];
	    	p0_Buf[3] = nozByt[1];
	    	p0_Buf[4] = 'p';
	    	p0_Buf[5] = '0';
	    	p0_Buf[6] = ETX;
	    	bcc = getBCC (p0_Buf);
	    	p0_Buf[7]= bcc; // BCC	
	    	
			//--- e0 (주유허가, 정액/정량설정)---//
			e0_ds.addByte("nozNo", nozID);
			e0_ds.addByte("STX", STX);
			e0_ds.addString("nozStr", nozStr, 2);
			e0_ds.addString("command", "e0", 2);
			e0_ds.addByte("mode", (byte) '0');
			e0_ds.addString("liter", "0000000", 7);
			e0_ds.addString("basePrice", "0000", 4);
			e0_ds.addString("price", "000000", 6);
			e0_ds.addByte("ETX", ETX);
			e0_ds.addByte("bcc", (byte) ' ');
			e0_Buf = e0_ds.getByteStream();
	    	bcc = getBCC (e0_Buf);
	    	e0_Buf[25]= bcc; // BCC
	    	
	    	//--- s0 (Request pump state) ---//
	    	s0_Buf[0] = nozID;
	    	s0_Buf[1] = STX;
	    	s0_Buf[2] = nozByt[0];
	    	s0_Buf[3] = nozByt[1];
	    	s0_Buf[4] = 's';
	    	s0_Buf[5] = '0';
	    	s0_Buf[6] = ETX;
	    	bcc = getBCC (s0_Buf);
	    	s0_Buf[7]= bcc; // Pump state
	    	
	    	//--- p0r (Recieve pumping complete) ---//
			p0r_ds.addByte("nozNo", nozID);
			p0r_ds.addByte("STX", STX);
			p0r_ds.addString("nozStr", nozStr, 2);
			p0r_ds.addString("command", "p0", 2);
			p0r_ds.addString("type", "2", 1);
			p0r_ds.addString("liter", "0000000", 7);
			p0r_ds.addString("basePrice", "0000", 4);
			p0r_ds.addString("price", "000000", 6);
			p0r_ds.addByte("ETX", ETX);
			p0r_ds.addByte("bcc", (byte) ' ');
			p0r_Buf = p0r_ds.getByteStream();
	    	bcc = getBCC (p0r_Buf);
	    	p0r_Buf[25]= bcc; // BCC
	    	
	    	//--- c0 (Request nozzle lock) ---//
	    	c0_Buf[0] = nozID;
	    	c0_Buf[1] = Command.STX;
	    	c0_Buf[2] = nozByt[0];
	    	c0_Buf[3] = nozByt[1];
	    	c0_Buf[4] = 'c';
	    	c0_Buf[5] = '0';
	    	c0_Buf[6]= Command.ETX;
	    	bcc = getBCC (c0_Buf);
	    	c0_Buf[7]= bcc; // BCC
	  	
	    	//--- t0 (Request total-gauge) ---//
	    	t0_Buf[0] = nozID;
	    	t0_Buf[1] = Command.STX;
	    	t0_Buf[2] = nozByt[0];
	    	t0_Buf[3] = nozByt[1];
	    	t0_Buf[4] = 't';
	    	t0_Buf[5] = '0';
	    	t0_Buf[6] = ETX;
	    	bcc = getBCC (t0_Buf);
	    	t0_Buf[7]= bcc; // BCC
	
	    	//--- t0r (Recieve total-gauge) ---//
			t0r_ds.setByte("nozNo");
			t0r_ds.setByte("STX");
			t0r_ds.setString("nozStr",  2);
			t0r_ds.setString("command",  2);
			t0r_ds.setString("totalGauge", 10);
			t0r_ds.setByte("ETX");
			t0r_ds.setByte("bcc");
			t0r_Buf = t0r_ds.getByteStream();
    	} catch (Exception e) {
    		LogUtility.getPumpALogger().error(e.getMessage(), e);
    	}

    }
 
	protected boolean compareBasePrice (byte[] buf) throws Exception {
		
		try {
			if (m_basePrice.equals("000000")) // 단가정보 미수신 상태
				return true;
			else {
				byte[] byt = new byte[4];
				System.arraycopy(buf, 14, byt, 0, 4);
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
		
		if (buf[0]==nozID)		
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
			if (dat[i]==STX) 
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
			e0_ds.editString("liter", "0000000" , 7);
			bPrice = PB_wm.getBasePrice();
			e0_ds.editString("basePrice", bPrice.substring(0, 4), 4);
			e0_ds.editString("price", "000000" , 6);
			
			m_downBasePrice = bPrice;
			
			LogUtility.getPumpALogger().info("[Pump A][수신-주유허가]" + " nozzle=" + 
					PB_wm.getNozzleNo() + "("+PB_wm.getCommand()+")" +
					" command = " +PB_wm.getCommandSet() +
					" | liter =" + PB_wm.getLiter() +
					" | bPrice=" + PB_wm.getBasePrice() +
					" | price =" + PB_wm.getPrice());
			
			if (nozType==2 || nozState==0 || nozState==2) { // 추가(08/10/16)
				setNozzleBasePrice(e0_Buf);
				LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> PB 수신, 노즐다운상태, 주유허가(e0) : progStep=" + 
						progressStep + " nozState=" + nozState + " m_basePrice=" + m_basePrice);
				//Log.datas(e0_Buf, e0_Buf.length, 20);
				//LogUtility.getPumpALogger().info("Recv STX("+PB_wm.getNozzleNo()+") : ["+pack(e0_Buf, ETX)+"]");

				skip = false;
			} else 
				skip = true; // 노즐업 또는 주유중이면 단가변경 않는다.
		}
		else if (wm.getCommand().equals("P3_1")) { // 주유기 환경설정

			// 단가를 e0_ds에 저장한다.
			P3_1_WorkingMessage P3_wm = (P3_1_WorkingMessage) wm;
			e0_ds.editString("liter", "0000000" , 7);
			bPrice = P3_wm.getBasePrice();
			e0_ds.editString("basePrice", bPrice.substring(0, 4), 4);
			e0_ds.editString("price", "000000" , 6);

			m_downBasePrice = bPrice;

			if (nozType==2 || nozState==0 || nozState==2) { // 추가(08/10/16)
				setNozzleBasePrice(e0_Buf);
				LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> P3_1 수신, 노즐다운상태, 주유허가(e0) : progStep=" + 
						progressStep + " nozState=" + nozState + " m_basePrice=" + m_basePrice);
				//Log.datas(e0_Buf, e0_Buf.length, 20);

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
			} else {
				if (nozType != 2) // 추가(08/12/02)
					TxQue.enQueue(e0_Buf);
				makeStatusInfo(657);
			}

			skip = false;
		}
		else if (wm.getCommand().equals("P7")) { // 주유기 파라미터 설정

			P7_WorkingMessage P7_wm = (P7_WorkingMessage) wm;
			
			if (nozType==2) { // 추가(08/12/03 for SomoSelf)
				baseReadStartInterval  = 0+2; // 스마트주유소 PJT 보정
				baseWriteStartInterval = 0+2; // 스마트주유소 PJT 보정
			}
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
			else	if (lineErrSkipCnt >= 8000 && lineErrSkipCnt <= 8999)
				MAX_StateReq_Cnt = lineErrSkipCnt - 8000; 
			else
				maxSkipCnt = baseMaxSkipCnt + lineErrSkipCnt;
			
			if (nozType==1) { // 추가(08/12/09)
				if (romVer.equals("101") || romVer.equals("102") || romVer.equals("103"))
					readCmdInterval = baseReadCmdInterval + Change.toValue(P7_wm.getReadBuffInterval());
			}

			if (lineErrSkipCnt >= 9005)
				m_isSaveSTX = true;
			
//			LogUtility.getPumpALogger().info("Received nozzle parameters(P7). nozzle=" + nozStr);
//			LogUtility.getPumpALogger().info(" -readStartInterval ="+P7_wm.getReadStartInterval());
//			LogUtility.getPumpALogger().info(" -writeStartInterval="+P7_wm.getWriteStartInterval());
//			LogUtility.getPumpALogger().info(" -readBuffInterval  ="+P7_wm.getReadBuffInterval());
//			LogUtility.getPumpALogger().info(" -minLineErrCount   ="+P7_wm.getLineErrorCount());
//			LogUtility.getPumpALogger().info(" -maxLineErrSkipCnt ="+P7_wm.getLineErrorSkipCount());
			
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
		if (RxBuf[4]=='s' && RxBuf[5]=='0') { // 주유기 상태정보 수신

			short state = (short) (RxBuf[6] - 0x30);
			
			switch (state) {
				case 0 : // 노즐다운(허가전)
				case 1 : // 노즐업(허가전)
				case 2 : // 노즐다운(허가후)
					if (nozState==4 || (nozState==5 && pumpingStart==true))
						nozState=5;
					else
						nozState=state;
					
					break;		
				case 3 : // 노즐업(허가후) - 토출중지시 state=3으로 가는것을 막기 위함
					if (nozState==4 && pumpingStart==true)
						nozState=4;
					else
						nozState=state;
					
					break;
				default :
					nozState=state;
			}
			
			if (m_nozLock==true && state>=2) { // 추가 (09/06/19) - 초이주유소(소모)
				TxQue.enQueue(c0_Buf);
				LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 비상정지 상태 주유허가 -> 허가취소 함(c0) : m_nozLock=" +
													m_nozLock + " state=" + state);
			}
			
		} 
		else if (RxBuf[4]=='p' && RxBuf[5]=='0') { // 주유중 정보 수신
			
			String type; 
			String szLiter = "", szPrice="", szBasePrice="";

			// 주유중 자료 누락처리용 추가(08/10/10)
			waitPumpingData=false;
			waitPumpingData_Cnt=0;
			
			//--- Preset-data processing of quick-win nozzle ---//
			p0r_ds.setByteStream(RxBuf);
			szLiter 	= (String) p0r_ds.getValue("liter");
			szBasePrice = (String) p0r_ds.getValue("basePrice");
			szPrice 	= (String) p0r_ds.getValue("price");
			int liter=Change.toValue (szLiter);
			int price=Change.toValue (szPrice);
			
			if (liter==0 && price>0) {
				type = "0"; // 정액설정
				szLiter = "0000000";
			} else 
			if (liter>0 && price==0 && liter!=4000000) {
				type = "1"; // 정량설정
				szPrice = "000000";
			} else {
				type = "2"; // 설정없음
				szLiter = "0000000";
				szPrice = "000000";
			}

			if (type.equals("0") || type.equals("1")) { // Preset Data(정액/정량 설정상태)

				if (sentPresetData==false) {
					HF_wm = new HF_WorkingMessage();
					HF_wm.setNozzleNo(Change.toString("%02d", nozNo));
					HF_wm.setType(type);
					HF_wm.setLiter(szLiter);
					szBasePrice = Change.toValue(szBasePrice) == 0 ? 
							(String) e0_ds.getValue("basePrice") : szBasePrice;
					HF_wm.setBasePrice(szBasePrice + "00");
					HF_wm.setPrice(szPrice);
				
					insertRecvQueue(HF_wm);
					
					presetDataFlag=true;
					sentPresetData=true;
					presetType=type;
					presetLiter=szLiter;
					presetBasePrice=szBasePrice;
					presetPrice=szPrice;
				}
			}
		}
		else if (RxBuf[4]=='t' && RxBuf[5]=='0') { // 토털게이지 정보수신(주유시작 용)
		
			if (nozState==1 || nozState==3 || nozState==4) { // 노즐업/주유중 상태에서 수신

				SJ_wm = (SJ_WorkingMessage) generateWorkingMessage(RxBuf, "SJ");
				
				if (romVer.equals("063")) // 추가-초고속주유기(09/03/10)
					SJ_wm.setTotalGauge(SJ_wm.getTotalGauge().substring(1,10) + "0");
				
				if(sentPumpingStartInfo==false) // AAAAA-추가(2008/09/06)
					insertRecvQueue(SJ_wm); // 주유시작 자료 송신
				
				//---- 이전 주유자료 수신문제 발생시 삭제검토 ---//
				if(romVer.equals("051") || romVer.equals("061") || romVer.equals("063")) // 추가 (09/01/03)
					TxQue.enQueueNewer(p0_Buf); // 주유자료 요청-ENE 프리셋자료수신용(08/08/12)
				
				sentPumpingStartInfo=true;
				waitTotalGuageFor_SJ=false;
				waitGaugeForSJ_Cnt = 0;
			}
			else if (progressStep==4) { // 주유완료후 수신
				progressStep = 5;
			} 
			else 
				insertRecvQueue(generateWorkingMessage(RxBuf, "S5")); // 토털게이지 자료 송신
		}
		else if (RxBuf[4]=='q') { // 주유기이상정보 추가(08/12/07 for SomoSelf)
			insertRecvQueue(generateWorkingMessage(RxBuf, "Q0")); 
		}
		else if (RxBuf[4]=='E') { // ENE 에러코드 수신 추가(09/03/19)
			makeStatusInfo(655); 
		}

		//LogUtility.getPumpALogger().debug("@STX Start : nozzle=" + nozStr + " ProgStep=" +
				//progressStep + " nozState=" + nozState + " pumpingStart=" + pumpingStart);
		
		
		//############# STX Process ##############//
		switch (progressStep) {
		case 3 :
			if (nozState==0 || nozState==2) { // 노즐다운

				flushBuffer(lastPumpingData);
				sentPumpingStartInfo=false;
				waitTotalGuageFor_SJ=false; // 추가->주유시작(SJ) 반복생성 문제해결(08/08/11)
				waitGaugeForSJ_Cnt=0; // 추가(2008/09/06)
				
				if (sentNozDownInfo==false) { // 추가 (08/12/19)
					makeStatusInfo(nozState); // 상태정보 전송
					sentNozDownInfo=true;
				}
				
				setNozzleBasePrice(e0_Buf);
				
				if (m_nozLock==false && nozType==1 && nozState==0 && pumpingStart==false) {
					
					if (Change.toValue(m_basePrice) > 0) {
						TxQue.enQueueNewer(e0_Buf); // 주유허가(정액/정량 설정)
						LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 노즐다운, 주유허가(e0) : progStep=" + progressStep + 
								" nozState=" + nozState + " m_basePrice=" + m_basePrice);
						//Log.datas(e0_Buf, e0_Buf.length, 20);
					}
				}
				
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
					//S4_wm.setTotalGauge(last_SJ_TotalGauge);
					S4_wm.setTotalGauge("0000000000"); // 수정(2008/09/06)
					
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

					sentPresetData=false;
					sentPumpingEndInfo=true;
					waitTotalGuageFor_SJ=false; // 추가->동화 노즐업다운시 SJ가 S4 다음도착 처리(09/01/07)
				}
				
				beforeNozzleUp=false;
			}
			else if (nozState==1 || nozState==3) { // 노즐업

				flushBuffer(lastPumpingData);
				//makeStatusInfo(nozState); // 상태정보 전송->아래로 이동(2008/07/22)
				if (nozType==2) { // 추가(09/02/17)
					if (nozState==3)
						beforeNozzleUp=true;
				} else
					beforeNozzleUp=true;
				
				realPumpingStart=false;
				sentPumpingEndInfo=false;
				sentNozDownInfo=false;

				if (nozState==3 && sentPumpingStartInfo==false) {
					TxQue.enQueueNewer(t0_Buf); // 토털게이지 요청(주유시작 정보 수신용)					
					makeStatusInfo(nozState); // 상태정보 전송, 위치이동(08/07/22)됨
					
					waitTotalGuageFor_SJ=true;
				}
				// 추가(08/11/29 for SomoSelf)
				else if (nozType==2)
					makeStatusInfo(nozState); // 상태정보 전송(노즐업)

				if (nozType!=2) { // 추가(08/11/29 for SomoSelf) - 셀프주유기 아니면
					TxQue.enQueueNewer(s0_Buf); // 소모구형 s04와 주유자료 누락처리, 테스트룸 #6 (2008/07/22)
				}
			}
			else if (nozState == 4) { // 주유중
				
				if (pumpingStart==false) {
					flushBuffer(lastPumpingData);
					pumpingStart=true;
				}
				
				if (RxBuf[4]=='p' && RxBuf[5]=='0') {

					S3_wm = (S3_WorkingMessage) generateWorkingMessage(RxBuf, "S3");
					int nPrice=0, nLiter=0;
					if (S3_wm != null) {
						nPrice = Change.toValue(S3_wm.getPrice());
						nLiter = Change.toValue(S3_wm.getLiter());
					}
					
					if (romVer.equals("201")) { // 길바꼬 단가보정 추가(09/08/18)
						S3_wm.setBasePrice(m_basePrice);
					}

					// 이전 주유량 수신시 Skip을 위한 추가(08/08/29)
					if (nPrice > 0 && nLiter > 0) {	
						if (newPumpingData==false && nLiter==m_nLiter) {
							LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 주유량(p0)이 이전 주유량과 동일합니다. : 주유량(x.3)=" + 
									nLiter + " 이전 주유량(x.3)=" + m_nLiter);
						} else {
							lastPumpingData = RxBuf.clone(); // 최종 주유정보 저장
							realPumpingStart=true;
							newPumpingData=true;
						}
					}

					if (presetDataFlag==true){ 
						presetDataFlag=false; // Preset data 이면 skip
					} else {
						if (nPrice > 0 && nLiter > 0) {
							if (firstPumpingData==true) {
								makeStatusInfo(nozState); // 상태정보 전송(주유중)
								firstPumpingData=false;
							}
							if (newPumpingData==true) // 추가(08/08/29)
								insertRecvQueue(S3_wm); // 주유중 자료 모듈로 송신
						}
					}
				}
				sentPumpingEndInfo=false; 
				
				if (romVer.equals("002")) { // 소모 페트로비즈
					
					if (p0_cnt%2==0) {
						TxQue.enQueueNewer(p0_Buf); // 주유자료 요청
						p0_cnt=0;
					}
					else {
						TxQue.enQueueNewer(s0_Buf); // 상태정보 요청
					}
						
					p0_cnt++;
				} 
				else {
					TxQue.enQueueNewer(p0_Buf); // 주유자료 요청
				}
				
				waitPumpingData=true; // AAA (08/10/18)
				sentNozDownInfo=false;
				
			}
			else if (nozState == 5) { // 주유완료

				if (pumpingStart==false) {
					flushBuffer(lastPumpingData);
					pumpingStart=true;
				}
				
				// 최종 주유자료 수신 처리			
				if ((RxBuf[4]=='p' && RxBuf[5]=='0') || waitLastPData_Cnt>=MAX_LAST_PDATA) {
					
					if ((RxBuf[4]=='p' && RxBuf[5]=='0')) {
						S3_wm = (S3_WorkingMessage) generateWorkingMessage(RxBuf, "S3");
						int nPrice=0, nLiter=0;
						if (S3_wm != null) {
							nPrice = Change.toValue(S3_wm.getPrice());
							nLiter = Change.toValue(S3_wm.getLiter());
						}
						if (nPrice > 0 || nLiter > 0) { // 수정(08/11/24) : && -> ||
							lastPumpingData = RxBuf.clone(); // 최종 주유정보 저장
							if (nozType==2) // <-- 추후 삭제검토
								TxQue.flushQueue(); // 추가(09/03/30) for SomoSelf -> p0 요청않음
						}

						LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 주유완료, 최종주유값 수신(p0) : progStep=" + 
									progressStep + " waitLastPData_Cnt=" + waitLastPData_Cnt + 
									" liter=" + S3_wm.getLiter() + " price=" + S3_wm.getPrice());
					} else
						LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 주유완료, 최종주유값 미수신(p0), 이전주유값 적용 : progStep=" + 
								progressStep + " waitLastPData_Cnt=" + waitLastPData_Cnt);
					
					TxQue.enQueueNewer(t0_Buf); // 토털게이지 요청
					
					LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 주유완료, 토털게이지 요청(t0) : progStep=" + 
							progressStep + " nozState=" + nozState + " pumpingStart=" + pumpingStart);
					
					progressStep=4;
					makeStatusInfo(nozState); // 상태정보 전송(주유완료)
					
					// Clear preset-data
					presetType="2";
					presetLiter="0000000";
					presetBasePrice="000000";
					presetPrice="000000";
					sentPresetData=false;

					waitLastPumpData=false;
					waitLastPData_Cnt=0;
					sentNozDownInfo=false;
				} else {
					waitLastPumpData=true;
					TxQue.enQueueNewer(p0_Buf); // 추가(09/02/03)
					
					LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 주유완료, 최종주유값 수신대기 : nozState=" +
							nozState + " waitLastPData_Cnt=" + waitLastPData_Cnt);
				}
			}
			break;

		case 4 :
			// skip
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
			
			LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 주유완료, 토털게이지 수신(t0) : progStep=" + 
					progressStep + " nozState=" + nozState + " pumpingStart=" + pumpingStart);

			setNozzleBasePrice(e0_Buf);
			
			if (m_nozLock==false && nozType==1) { // 1:일반주유기, 2:Self, 3:SemiSelf, 4:충전기
				if (Change.toValue(m_basePrice) > 0) {
					TxQue.enQueueNewer(e0_Buf); // 주유허가(정액/정량 설정)				
					LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 토털게이지 수신, 주유허가(e0) : progStep=" + 
							progressStep + " nozState=" + nozState + " m_basePrice=" + m_basePrice);
					//Log.datas(e0_Buf, e0_Buf.length, 20);
				}
			}

			S4_wm = (S4_WorkingMessage) generateWorkingMessage(tBuf, "S4");
			S4_wm.setNozzleNo(Change.toString("%02d",nozNo));

			if (romVer.equals("063")) // ENE 초고속주유기 추가(09/03/10)
				S4_wm.setTotalGauge(S4_wm.getTotalGauge().substring(1,10) + "0");
			
			if (romVer.equals("201")) { // 길바꼬(신설동) 단가보정 추가(09/08/18)
				S4_wm.setBasePrice(m_basePrice);
			}
			
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
			
			/* ---보정처리 적용여부 미결정 -> 보류
			int nGauge = Change.toValue(S4_wm.getTotalGauge());
			if (nGauge==0) {
				S4_wm.setTotalGauge(addTotalGauge(last_SJ_TotalGauge, S4_wm.getLiter()));
				LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> 주유완료(S4) 토털게이지값=0, 게이지값 보정처리");
			}
			else
				last_S4_TotalGauge = S4_wm.getTotalGauge();
			*/
			/*
			// 주유금액 차이시 보정처리(2009/07/20)
			int nRespectPrice = (int) (Change.toValue(m_basePrice.substring(0,4)) * (nLiter/1000.0));
			if (nozType==1 && Math.abs(nRespectPrice - nPrice) >= (nRespectPrice / 1000.0)) { // 0.1% 이상 차이시 보정함
				String sRespectPrice = Change.toString("%08d", nRespectPrice);
				m_nPrice = Change.toValue(sRespectPrice);
				S4_wm.setPrice(sRespectPrice);
				LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> 주유완료(S4) 금액보정. 수신값=" + 
						nPrice + " 보정값=" + m_nPrice);
			}
			*/
			 // 수정(08/09/10)
			if (nLiter <= 0 || nPrice <= 0) {
				S4_wm.setBasePrice(m_basePrice);
				LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 주유완료(S4) 자료값=0 : Liter=" + 
						nLiter + " Price=" + nPrice);
			}
			if (sentPumpingEndInfo==false) {
				insertRecvQueue(S4_wm); // 주유완료 자료
				sentPumpingEndInfo=true;
				waitTotalGuageFor_SJ=false; // 추가->동화 노즐업다운시 SJ가 S4 다음도착 처리(09/01/07)
			}
			
			if (nozType==2) // 추가(2008/11/19 for SomoSelf)
				TxQue.enQueueNewer(s0_Buf);
			
			break;
		}
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
		
		//--- send : SA + ACK/NAK ---//
		rtn = sendText (ACK_NAK);
		if(rtn==false) {
			//lineErrCnt++;
			return false;
		}

		if (dispLevel>=3) 
			LogUtility.getPumpALogger().info("1.Send ACK (Noz="+nozNo+")");
				
		//--- recv : SA + EOT ---//
		if (recvCmd(RxBuf) < 1) {
			//lineErrCnt++;
			if (dispLevel>=2) 
				LogUtility.getPumpALogger().info("1.Recv EOT fail! (Noz="+nozNo+")");
			return false;
		}
	
		if (RxBuf[1] == EOT) {	//--- recv : SA + EOT ---//
			if (dispLevel>=3) 
				LogUtility.getPumpALogger().info("1.Recv EOT (Noz="+nozNo+")");
		}
		/*
		//08.05.16 삭제 - 미요청시 응답않음	
		//--- send : SA + EOT ---//
		rtn = sendText (EOT);
		if(rtn==false) {
			//lineErrCnt++;
			return false;
		}

		if (dispLevel>=2) 
			LogUtility.getPumpALogger().debug("1.Send EOT (Noz="+nozNo+")");
		
		//--- recv : SA + EOT ---//
		if (recvCmd(RxBuf) < 1) {
			//lineErrCnt++;
			return false;
		}
	
		if (RxBuf[1] == EOT) {	//--- recv : SA + EOT ---//
			if (dispLevel>=2) 
				LogUtility.getPumpALogger().debug("1.Recv EOT2 (Noz="+nozNo+")");
		}*/

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
				} else {
					loop=0;
				}
				
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
	
	@Override
	public void requestData() throws SerialConnectException, Exception {
			
		//byte[] RxCmd = new byte[2];
		
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
		
		//flushBuffer(RxBuf);
		
		// 회선불량 복구시 처리
		if (firstRequest==true) {
			firstRequest=false;
			TxQue.enQueueNewer(s0_Buf); // 상태정보요청
		}
		if (m_statusCode==601) { // 회선불량
			if (lineCommCheckCnt%(minErrCnt+5)==0) {
				lineCommCheckCnt=0;
				TxQue.enQueueNewer(s0_Buf); // 상태정보요청
			}
			lineCommCheckCnt++;
		}
		
		// 주유시작(SJ) 토털게이지 미수신 처리
		if (waitTotalGuageFor_SJ==true) {
			if (waitGaugeForSJ_Cnt >= MAX_GAUGE_SJ) {
				SJ_wm = new SJ_WorkingMessage();
				SJ_wm.setNozzleNo(Change.toString("%02d",nozNo));
				//SJ_wm.setTotalGauge(last_S4_TotalGauge);
				SJ_wm.setTotalGauge("0000000000");
				insertRecvQueue(SJ_wm); // 주유시작 자료 송신

				waitGaugeForSJ_Cnt=0;
				sentPumpingStartInfo=true;
				waitTotalGuageFor_SJ=false; // 수정(08/08/11)

				LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 주유시작 토털게이지(t0) 미수신, 주유시작(SJ) 전문 생성");
			}
			else
				waitGaugeForSJ_Cnt++;
			
			LogUtility.getPumpALogger().debug("waitGaugeForSJ_Cnt="+waitGaugeForSJ_Cnt);
		}
		
		// 주유중 자료(S3) 미수신 처리
		if (nozState==4 && waitPumpingData==true) {
			//LogUtility.getPumpALogger().debug("waitPumpingData_Cnt=" + waitPumpingData_Cnt);
			if (waitPumpingData_Cnt >= MAX_WAIT_PDATA) {
				TxQue.enQueueNewer(p0_Buf);				
				
				LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 주유중 자료(S3)값 미수신, 주유자료 요청(p0), waitPumpingData_Cnt="+waitPumpingData_Cnt);
				waitPumpingData_Cnt=0;
			}
			else
				waitPumpingData_Cnt++;
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
					
					LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 주유완료, 토털게이지(t0) 미수신, processSTX() 호출.");
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
			
//			if (wm.getCommand().equals("PB")) {
//				LogUtility.getPumpALogger().debug(":::::::::>>> ByteStreams of workingMessage ["+ wm.getCommand() + "] in requestData().");
//				Log.datas(wmByte, wmByte.length, 20);
//			}
			
			wm=null;
		}
		if (dispLevel>=3)
			LogUtility.getPumpALogger().debug("\nStart request ============> WooJoo, nozNo=" + nozNo);

		try {
			//##### Send ENQ and Recv ACK/STX #####//
			if (dispLevel>=3)
				LogUtility.getPumpALogger().debug("----POLLING----(progStep="+progressStep+", nozState="+nozState+")");
			
			//--- Send ENQ ---//
			if(sendText (ENQ) != true) { // fail
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
					else if (lineErrCnt >= minErrCnt) {
						if (m_nNoResponseCnt%3==0) {
							m_nNoResponseCnt=0;
							LogUtility.getPumpALogger().debug("0.Recv ACK/STX fail!(No response) lineErrCnt="+lineErrCnt+"/"+minErrCnt+" (Noz="+nozNo+")");
						}
						m_nNoResponseCnt++;
					}
					return;
				}
			
				//###### Recv data : STX ######//
				if (RxBuf[1] == STX) {
					
					if (dispLevel>=3) {
						LogUtility.getPumpALogger().info ("1.Recv STX (Noz="+nozNo+")");
						Log.datas(RxBuf, 40, 20);
					}
					
					if (compareNozID(RxBuf)==false) { 
						LogUtility.getPumpALogger().info ("1.Recv STX NozID mismatch-1.0! (Noz="+nozNo+")");
						Log.datas(RxBuf, 40, 20);
						lineErrCnt++;
	
						trimInputStream("routine : 1");
						//return;
						continue;
					}
					if (compareBCC(RxBuf)==false) {
						sendText(NAK);
						LogUtility.getPumpALogger().info ("1.Recv STX BCC fail! (Noz="+nozNo+")");
						Log.datas(RxBuf, 40, 20);
						lineErrCnt++;
						continue;
					}
					if (verifyData(RxBuf)==false) { 
						sendText(NAK);
						LogUtility.getPumpALogger().info ("1.Recv STX Data verify fail! (Noz="+nozNo+")");
						Log.datas(RxBuf, 40, 20);
						lineErrCnt++;
						return;
					}
					else { //  STX 정상수신
						if (dispLevel==1 || dispLevel==2) {
							LogUtility.getPumpALogger().info ("1.Recv STX with normal (Noz="+nozNo+")");
							Log.datas(RxBuf, 40, 20);
						}
						
						if (nozType==2) { // self
							if (RxBuf[4]=='p' && m_isLogPumpingSTX==true) {
								if (m_isSaveSTX==true) 
									LogUtility.getPumpALogger().info("Recv STX("+nozStr+") : ["+pack(RxBuf, ETX)+"]");
								else
									LogUtility.getPumpALogger().debug("Recv STX("+nozStr+") : ["+pack(RxBuf, ETX)+"]");
								
								m_isLogPumpingSTX=false;
							}
						}
						else {
							if (RxBuf[4]=='p') {
								if (m_isSaveSTX==true) 
									LogUtility.getPumpALogger().info("Recv STX("+nozStr+") : ["+pack(RxBuf, ETX)+"]");
								else
									LogUtility.getPumpALogger().debug("Recv STX("+nozStr+") : ["+pack(RxBuf, ETX)+"]");
							}
						}

						if (RxBuf[4]=='t')
							if (m_isSaveSTX==true) 
								LogUtility.getPumpALogger().info("Recv STX("+nozStr+") : ["+pack(RxBuf, ETX)+"]");
							else
								LogUtility.getPumpALogger().debug("Recv STX("+nozStr+") : ["+pack(RxBuf, ETX)+"]");
						
						if (RxBuf[4]=='s' && RxBuf[6] != m_byLastState) {
							LogUtility.getPumpALogger().info("Recv STX("+nozStr+") : ["+pack(RxBuf, ETX)+"]");
							m_byLastState = RxBuf[6];
						}
						
						processRecvSTX(); // 노즐 수신데이터 처리
						recvTail_proc(ACK);

						lineErrCnt=0; // Normal terminated
						return;
					}
				} 
				else if (RxBuf[1] == ACK) { // recv : ACK
	
					if (dispLevel>=3)
						LogUtility.getPumpALogger().info("2.Recv ACK (Noz="+nozNo+")");
	
					if (compareNozID(RxBuf)==false) { // 추가(08/12/03) for SomoSelf
						LogUtility.getPumpALogger().info ("2.Recv ACK NozID mismatch-2.0! (Noz="+nozNo+")");
						Log.datas(RxBuf, 20, 20);
						lineErrCnt++;
						
						trimInputStream("routine : 2");
						//return;
						continue;
					}
	
					if (TxQue.isEmpty()==false) { // 송신 데이터 있으면
	
						//###### Send data : send working-data ######//
						TxBuf = TxQue.getFirstItem();
						TxBuf[0] = nozID;
						TxBuf[1] = STX;
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
														
							if (RxBuf[1] == ACK) {	// recv : ACK
								if (dispLevel>=3)
									LogUtility.getPumpALogger().info("2.Recv ACK (Noz="+nozNo+")");
		
								if (compareNozID(RxBuf)==false) { // 추가(08/12/03 for SomoSelf)
									LogUtility.getPumpALogger().info("2.Recv ACK NozID mismatch-2.1! (Noz="+nozNo+")");
									Log.datas(RxBuf, 20, 20);
									lineErrCnt++;
									
									trimInputStream("routine : 3");
									return;
								}
								
								sendTail_proc();

								if (nozType==2) { // self
									if (TxBuf[4]=='t' || (TxBuf[4]=='p' && m_nS3_cnt%5==0)) {
										if (m_isSaveSTX==true) 
											LogUtility.getPumpALogger().info("Send STX("+nozStr+") : ["+new String(TxBuf)+"]");
										else 
											LogUtility.getPumpALogger().debug("Send STX("+nozStr+") : ["+new String(TxBuf)+"]");

										if (TxBuf[4]=='p') {
											m_nS3_cnt=0;
											m_isLogPumpingSTX=true;
										}
									}
									if (TxBuf[4]=='p') m_nS3_cnt++;
								} else {
									if (TxBuf[4]!='s') {
										if (m_isSaveSTX==true) 
											LogUtility.getPumpALogger().info("Send STX("+nozStr+") : ["+new String(TxBuf)+"]");
										else 
											LogUtility.getPumpALogger().debug("Send STX("+nozStr+") : ["+new String(TxBuf)+"]");
									}
								}

								if (dispLevel==2) {
									LogUtility.getPumpALogger().info("2.Send STX with normal (Noz="+nozNo+")");
									Log.datas(TxBuf, TxBuf.length, 20);
								}

								//LogUtility.getPumpALogger().debug("Send STX : ["+new String(TxBuf)+"]");
									
								//--- 송신완료후 송신데이터 제거 ---//
								TxQue.deQueue();
									
								lineErrCnt=0; // Normal terminated
								return;
							}
							else if (RxBuf[1] == NAK) {	// recv : NAK
								
								sendText(TxBuf); // 재전송
									
								LogUtility.getPumpALogger().info("2.Send STX fail!(Returned NAK)->retry send (Noz="+nozNo+")");
								Log.datas(TxBuf, TxBuf.length, 20);
								continue;
							}							
						} // end of for (int j=0; j<3; j++)
					} 
					else { //--- 송신 데이터 없으면 ---//
						sendTail_proc();
						
						// 상태정보 요청 : 추가(08/12/19)
						if (MAX_StateReq_Cnt > 0) {
							if(stateReq_Cnt >= MAX_StateReq_Cnt-1) {
								TxQue.enQueueNewer(s0_Buf);
								stateReq_Cnt=0;
							}
							else
								stateReq_Cnt++;
						}
						
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
		
		//lineErrCnt=0; // Normal terminated
	}
	
	protected void retryRecvData (byte[] buf) throws Exception {
		
		if (buf[4]=='s' && buf[5]=='0') {
			TxQue.enQueueNewer(s0_Buf);
			LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 상태정보(s0) 재요청");
		}
		else if (buf[4]=='p' && buf[5]=='0') {
			TxQue.enQueueNewer(p0_Buf);
			LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 주유자료(p0) 재요청");
		}
		else if (buf[4]=='t' && buf[5]=='0') {
			TxQue.enQueueNewer(t0_Buf);
			LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 토털게이지(t0) 재요청");
		}
	}
	
	public void run() {

	}
	
	protected boolean sendTail_proc () throws Exception, SerialConnectException {
		boolean rtn;
		
		//--- send : SA + EOT ---//
		rtn = sendText (EOT);
		if(rtn==false) {
			//lineErrCnt++;
			return false;
		}

		if (dispLevel>=3) 
			LogUtility.getPumpALogger().info("2.Send EOT (Noz="+nozNo+")");

		if (recvCmd(RxBuf) < 1) {
			if (dispLevel>=2) 
				LogUtility.getPumpALogger().info("2.Recv EOT fail! (Noz="+nozNo+")");
			//lineErrCnt++;
			return false;
		}
	
		//--- recv : SA + EOT ---//
		if (RxBuf[1] == EOT) {	
			if (dispLevel>=3) 
				LogUtility.getPumpALogger().info("2.Recv EOT (Noz="+nozNo+")");
		}
		
		return true;
	}
	
	public boolean sendText(byte cmd) throws Exception, SerialConnectException {

		byte[]	sndBuf = new byte[2];
		boolean	rtn;

		Sleep.sleep(writeStartInterval);
		
		//--- send : SA + ENQ ---//
		sndBuf[0] = (byte) (nozNo + 0x40);
		sndBuf[1] = cmd;
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
		
	protected void setNozzleBasePrice(byte[] buf) throws Exception {

		e0_Buf = e0_ds.getByteStream();
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
		
		int startIdx=6, size=0, bufLen;
		String cmd="";
				
		for (bufLen=0; bufLen<buf.length; bufLen++)
			if (buf[bufLen]==ETX) break;
		bufLen+=2;
				
		if (buf[4]=='s' && buf[5]=='0') { // 주유기 상태정보 수신
			size = 9;
			cmd = "s0";
			
			//--- 데이터 검증(데이터 길이와 ETX 확인) ---//
			if (size != bufLen || buf[size-2] != ETX) {
				LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 이상데이터 수신("+cmd+"), 실데이터길이=" + 
						size + ", 수신데이터길이=" + bufLen + ", ETX=" + buf[size-1]);
				return false;
			}

			//--- 데이터 검증(숫자여부) ---//
			for (int i=startIdx; i<size-2; i++) {
				if (buf[i] < 0x30 || buf[i] > 0x39) { // 0 ~ 9 가 아니면
					LogUtility.getPumpALogger().info("nozzle=" + nozNo + 
							" -> 이상데이터 수신("+cmd+"), 숫자가 아님, 수신데이터=" + buf[i]);
					return false;
				}
			}
		}
		else if (buf[4]=='q' /*&& buf[5]=='0'*/) { // 주유기 상태정보 수신
			size = 9;
			cmd = "q0";
			
			//--- 데이터 검증(데이터 길이와 ETX 확인) ---//
			if (size != bufLen || buf[size-2] != ETX) {
				LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 이상데이터 수신("+cmd+"), 실데이터길이=" + 
						size + ", 수신데이터길이=" + bufLen + ", ETX=" + buf[size-1]);
				return false;
			}

			//--- 데이터 검증(숫자여부) ---//
			for (int i=startIdx-1; i<size-2; i++) {
				if (buf[i] < 0x30 || buf[i] > 0x39) { // 0 ~ 9 가 아니면
					LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 이상데이터 수신("+cmd+"), 숫자가 아님, 수신데이터=" + buf[i]);
					return false;
				}
			}
		}
		else if (buf[4]=='p' && buf[5]=='0') { // 주유자료 수신
			startIdx = 7; // 추가(2008/09/22)-ENE는 null 이 오는 경우 있음
			size = 26;
			cmd = "p0";
			
			//--- 데이터 검증(데이터 길이와 ETX 확인) ---//
			if (size != bufLen || buf[size-2] != ETX) {
				LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 이상데이터 수신("+cmd+"), 실데이터길이=" + 
						size + ", 수신데이터길이=" + bufLen + ", ETX=" + buf[size-1]);
				return false;
			}
			
			// 추가 (08/10/16) 
			if (compareBasePrice(buf)==false) {
				if (nozState==4 || nozState==5) {
					if (romVer.equals("061") || romVer.equals("063")) // 마이컴 주유기 -> 수정 (08/10/28)
						TxQue.enQueueNewer(e0_Buf); // 단가변경
				}
			}
			
			//--- 데이터 검증(숫자여부) ---//
			for (int i=startIdx; i<size-2; i++) {
				if (buf[i] < 0x30 || buf[i] > 0x39) { // 0 ~ 9 가 아니면
					
					if (i>=14 && i<=17) {
						buf[i] = '0'; // 단가는 검증않음
					}
					else if (i==7) {
						buf[i] = '0'; // ENE기종 10,000리터 초과시 발생버그 처리(값 = ?999.999) (09/01/02)
					} 
					else if (i==18) {
						buf[i] = '0'; // ENE기종 100만원 초과시 발생버그 처리(값 = ?99999) (08/10/16)
					} 
					else {
						LogUtility.getPumpALogger().info("nozzle=" + nozNo + 
								" -> 이상데이터 수신("+cmd+"), 숫자가 아님, 수신데이터=" + buf[i]);
						return false;
					}
				}
			}
		}
		else if (buf[4]=='t' && buf[5]=='0') { // 토털게이지 자료 수신
			size = 18;
			cmd = "t0";
			
			//--- 데이터 검증(데이터 길이와 ETX 확인) ---//
			if (size != bufLen || buf[size-2] != ETX) {
				LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 이상데이터 수신("+cmd+"), 실데이터길이=" + 
						size + ", 수신데이터길이=" + bufLen + ", ETX=" + buf[size-1]);
				return false;
			}

			//--- 데이터 검증(숫자여부) ---//
			for (int i=startIdx; i<size-2; i++) {
				if (buf[i] < 0x30 || buf[i] > 0x39) { // 0 ~ 9 가 아니면
					LogUtility.getPumpALogger().info("nozzle=" + nozNo + 
							" -> 이상데이터 수신("+cmd+"), 숫자가 아님, 수신데이터=" + buf[i]);
					return false;
				}
			}
		}
		else if (buf[4]=='E') { // 추가(09/03/10)
			LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> ENE 노즐 에러코드 수신->사용않음");
			Log.datas(buf, 20, 20);
		}
		else {
			LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 이상데이터 수신(STX), 알수없는 코멘드!");
			return false;
		}
				
		return true;
	}
}
