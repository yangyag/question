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
import com.gsc.kixxhub.device.pumpa.translation.TransTatsunoN;

public class CommGSLubr extends CCommDriver implements SerialPortEventListener, CommPortOwnershipListener, 
	Runnable {

	protected static int	MAX_WAIT_PDATA=5;
	protected byte  	ACK  = 0x10;
	protected byte[] 	ACK0 = new byte[3];
	protected byte[] 	ACK1 = new byte[3];
	protected boolean beforeNozzleUp=false; // 이전 노즐업여부
	protected int		buffSize = 500;
	protected int	dispLevel=0;
	protected byte  	ENQ  = 0x05;

	protected byte[] 	enq_Buf = new byte[4];
	
    protected byte[] 	ENQb = new byte[4];
    protected byte  	EOT  = 0x04;
    protected byte[]  	EOTb = {EOT, 0};
    protected byte  	ESC  = 0x1B;
    protected byte  	ETX  = 0x03;
    protected boolean firstPumpingData=true;
    protected boolean firstRequest=true;
    protected HF_WorkingMessage HF_wm;
    protected boolean	issueLineErr=true;
    protected byte[] 	lastPumpingData=new byte[buffSize];
    protected int		lineCommCheckCnt=0;

	protected int		lineErrCnt=0;
	protected String	m_basePrice="000000";
	//protected String	last_SJ_TotalGauge="0000000000"; 
    protected boolean m_nozLock=false;
	protected boolean m_recvedNozLock=false;
	
	protected int		m_statusCode=601;
	protected int		minErrCnt=8, maxSkipCnt=100;
    protected byte  SEN  = 0x40; // '@'
    protected byte  mode = SEN;
	protected byte  	NAK  = 0x15;
	protected byte[]  	NAKb = {NAK, 0};
	protected byte		nozID;
	protected short		nozState=0;
	protected String 	nozStr = "";
	protected byte  POL  = 0x3F; // '?'
	//progressStep : 0=초기화전, 1=초기화후, 2=상태정보송신후, 3=주유중, 4=주유완료, 
    //               5=토털게이지수신/주유완료자료 송신
    protected int 	progressStep=0;
	protected boolean pumpingStart=false;
	protected byte[] 	rcvInitBuf = new byte[6];
	protected int		readBuffInterval   = 30;
	protected int		readStartInterval  = 10;
	protected boolean realPumpingStart=false; // 오일토출
	protected byte[] 	RxBuf = new byte[buffSize];
	
	protected S3_WorkingMessage S3_wm;
	protected S4_WorkingMessage S4_wm;
    
    protected S5_WorkingMessage S5_wm;
    protected S8_WorkingMessage S8_wm;
    protected SE_WorkingMessage SE_wm;
    protected boolean sentPumpingStartInfo=false;
    protected SJ_WorkingMessage SJ_wm;
    protected byte  	STX  = 0x02;
    protected byte[] 	t10_Buf;
    protected DataStruct t10_ds  = new DataStruct(); //--- pumping grant(Non MPP) ---//
    protected byte[] 	t11_Buf;
    protected DataStruct t11_ds  = new DataStruct(); //--- pumping grant(Only MPP) ---//
    protected byte[] 	t12_Buf;

    protected DataStruct t12_ds  = new DataStruct(); //--- pumping grant cancel(Non MPP) ---//
    protected byte[] 	t13_Buf;
    protected DataStruct t13_ds  = new DataStruct(); //--- pump locking ---//
    
    protected byte[] 	t14_Buf;
    protected DataStruct t14_ds  = new DataStruct(); //--- pump locking cancel---//
    protected byte[] 	t15_Buf;
    protected DataStruct t15_ds  = new DataStruct(); //--- request pump status ---//
    protected byte[] 	t20_Buf;
    protected DataStruct t20_ds  = new DataStruct(); //--- request total gauge ---//
    protected byte[] 	t60_Buf;
    protected DataStruct t60_ds  = new DataStruct(); //--- receive pump status ---//
    protected byte[] 	t61_Buf;
    protected DataStruct t61_ds  = new DataStruct(); //--- receive pumping status ---//
    protected byte[] 	t62_Buf;
    protected DataStruct t62_ds  = new DataStruct(); //--- receive error message ---//
    protected byte[] 	t65a_Buf;
    protected DataStruct t65a_ds = new DataStruct(); //--- receive total gauage - 1 Nozzle type--//
	
    protected byte[] 	t65b_Buf;
    protected DataStruct t65b_ds = new DataStruct(); //--- receive total gauage - 2 Nozzle type--//
    protected byte[] 	t65c_Buf;
    protected DataStruct t65c_ds = new DataStruct(); //--- receive total gauage - 3 Nozzle type--//
    protected TransTatsunoN trans;
    protected byte[] 	TxBuf = new byte[buffSize];
    protected int		waitGaugeForS4_Cnt=0;
    protected int		waitGaugeForSJ_Cnt=0;
    protected boolean waitPumpingData=false; // 주유자료 수신여부
    protected int		waitPumpingData_Cnt=0;
    protected boolean waitTotalGuageFor_S4=false; // 주유완료 게이지 수신대기
    protected boolean waitTotalGuageFor_SJ=false; // 주유시작 게이지 수신대기
    protected int		writeStartInterval = 10;
    
    public CommGSLubr (int nozNum, String romVerStr) {
    	
    	byte	bcc;
		Formatter form = new Formatter ();
		byte[] 	nozByt = new byte[2];
		
		nozNo = nozNum;
		romVer = romVerStr;

		trans = new TransTatsunoN (romVer);
		
		form.format("%02d", nozNo);
		nozStr = form.toString();
		nozByt = nozStr.getBytes();
		nozID = (byte) (nozNo - 1 + 0x40);
		
		//--- prefix ACK-buffer ---//
	    ACK0[0] = ACK;
	    ACK0[1] = nozID;
	    ACK0[2] = 0x30;
	    ACK1[0] = ACK;
	    ACK1[1] = nozID;
	    ACK1[2] = 0x31;
		
		//--- prefix ENQ-buffer ---//
	    ENQb[0] = EOT;
	    ENQb[1] = nozID;
	    ENQb[2] = SEN;
	    ENQb[3] = ENQ;
	    
		//--- prefix NAK-buffer---//
	    NAKb[1] = nozID;

		//--- prefix EOT-buffer---//
	    EOTb[1] = nozID;
		
		try {
        	flushBuffer(lastPumpingData);
        	
			//--- pumping grant(Non MPP) ---//
			t10_ds.addByte  ("STX", STX);
			t10_ds.addByte  ("SA", nozID);
			t10_ds.addByte  ("UA", SEN);
			t10_ds.addString("command", "10", 2);
			//t10_ds.addByte  ("pumpingType", (byte) '2');
			t10_ds.addByte  ("pumpingType", (byte) '0');
			t10_ds.addByte  ("presetType", (byte) '1');
			t10_ds.addString("price_liter", "000000", 6);
			t10_ds.addByte  ("bPriceFlag", (byte) '2');
			t10_ds.addString("basePrice", "0000", 4);
			t10_ds.addByte  ("ETX", ETX);
			t10_ds.addByte  ("BCC", (byte) ' ');
			t10_Buf = t10_ds.getByteStream();
	
			//--- pumping grant(Only MPP) ---//
			t11_ds.addByte  ("STX", STX);
			t11_ds.addByte  ("SA", nozID);
			t11_ds.addByte  ("UA", SEN);
			t11_ds.addString("command", "11", 2);
			t11_ds.addByte  ("pumpingType", (byte) '0');
			t11_ds.addByte  ("presetType", (byte) '2');
			t11_ds.addString("price_liter", "000000", 6);
			t11_ds.addByte  ("bPriceFlag1", (byte) '2');
			t11_ds.addString("basePrice1", "0000", 4);
			t11_ds.addByte  ("bPriceFlag2", (byte) '2');
			t11_ds.addString("basePrice2", "0000", 4);
			t11_ds.addByte  ("bPriceFlag3", (byte) '2');
			t11_ds.addString("basePrice3", "0000", 4);
			t11_ds.addByte  ("bPriceFlag4", (byte) '2');
			t11_ds.addString("basePrice4", "0000", 4);
			t11_ds.addByte  ("bPriceFlag5", (byte) '2');
			t11_ds.addString("basePrice5", "0000", 4);
			t11_ds.addByte  ("bPriceFlag6", (byte) '2');
			t11_ds.addString("basePrice6", "0000", 4);
			t11_ds.addByte  ("ETX", ETX);
			t11_ds.addByte  ("BCC", (byte) ' ');
			t11_Buf = t11_ds.getByteStream();
	
			//--- pumping grant cancel(Non MPP) ---//
			t12_ds.addByte  ("STX", STX);
			t12_ds.addByte  ("SA", nozID);
			t12_ds.addByte  ("UA", SEN);
			t12_ds.addString("command", "12", 2);
			t12_ds.addByte  ("ETX", ETX);
			t12_ds.addByte  ("BCC", (byte) ' ');
			t12_Buf = t12_ds.getByteStream();
	
			//--- pump locking ---//
			t13_ds.addByte  ("STX", STX);
			t13_ds.addByte  ("SA", nozID);
			t13_ds.addByte  ("UA", SEN);
			t13_ds.addString("command", "13", 2);
			t13_ds.addByte  ("ETX", ETX);
			t13_ds.addByte  ("BCC", (byte) ' ');
			t13_Buf = t13_ds.getByteStream();
	
			//--- pump locking cancel---//
			t14_ds.addByte  ("STX", STX);
			t14_ds.addByte  ("SA", nozID);
			t14_ds.addByte  ("UA", SEN);
			t14_ds.addString("command", "14", 2);
			t14_ds.addByte  ("ETX", ETX);
			t14_ds.addByte  ("BCC", (byte) ' ');
			t14_Buf = t14_ds.getByteStream();
	
			//--- request pump status ---//
			t15_ds.addByte  ("STX", STX);
			t15_ds.addByte  ("SA", nozID);
			t15_ds.addByte  ("UA", SEN);
			t15_ds.addString("command", "15", 2);
			t15_ds.addByte  ("ETX", ETX);
			t15_ds.addByte  ("BCC", (byte) ' ');
			t15_Buf = t15_ds.getByteStream();
	
			//--- request total gauge ---//
			t20_ds.addByte  ("STX", STX);
			t20_ds.addByte  ("SA", nozID);
			t20_ds.addByte  ("UA", SEN);
			t20_ds.addString("command", "20", 2);
			t20_ds.addByte  ("ETX", ETX);
			t20_ds.addByte  ("BCC", (byte) ' ');
			t20_Buf = t20_ds.getByteStream();
			
			//--- receive pump status ---//
			t60_ds.setByte  ("STX");
			t60_ds.setByte  ("SA");
			t60_ds.setByte  ("UA");
			t60_ds.setString("command", 2);
			t60_ds.setByte  ("state");
			t60_ds.setByte  ("contents");
			t60_ds.setByte  ("ETX");
			t60_ds.setByte  ("BCC");
			t60_Buf = t60_ds.getByteStream();
	
			//--- receive pumping status ---//
			t61_ds.setByte  ("STX");
			t61_ds.setByte  ("SA");
			t61_ds.setByte  ("UA");
			t61_ds.setString("command", 2);
			t61_ds.setByte  ("state");
			t61_ds.setString("liter", 6);
			t61_ds.setByte  ("bPriceFlag");
			t61_ds.setString("basePrice", 4);
			t61_ds.setString("price", 6);
			t61_ds.setByte  ("nozNo");
			t61_ds.setByte  ("oilFlag");
			t61_ds.setByte  ("dispType");
			t61_ds.setByte  ("ETX");
			t61_ds.setByte  ("BCC");
			t61_Buf = t61_ds.getByteStream();
			
			//--- receive error message ---//
			t62_ds.setByte  ("STX");
			t62_ds.setByte  ("SA");
			t62_ds.setByte  ("UA");
			t62_ds.setString("command", 2);
			t62_ds.setString("code", 2);
			t62_ds.setByte  ("errState");
			t62_ds.setByte  ("ETX");
			t62_ds.setByte  ("BCC");
			t62_Buf = t62_ds.getByteStream();
			
			//--- receive total gauage ---//
			// 1 Nozzle type
			t65a_ds.setByte  ("STX");
			t65a_ds.setByte  ("SA");
			t65a_ds.setByte  ("UA");
			t65a_ds.setString("command", 2);
			t65a_ds.setByte  ("noz1_state");
			t65a_ds.setString("reserved", 5);
			t65a_ds.setString("noz1_totalLiter", 10);
			t65a_ds.setString("noz1_totalPrice", 10);
			t65a_ds.setByte  ("ETX");
			t65a_ds.setByte  ("BCC");
			t65a_Buf = t65a_ds.getByteStream();
			
			// 2 Nozzle type
			t65b_ds.setByte  ("STX");
			t65b_ds.setByte  ("SA");
			t65b_ds.setByte  ("UA");
			t65b_ds.setString("command", 2);
			t65b_ds.setByte  ("noz1_state");
			t65b_ds.setByte  ("noz2_state");
			t65b_ds.setString("reserved", 4);
			t65b_ds.setString("noz1_totalLiter", 10);
			t65b_ds.setString("noz1_totalPrice", 10);
			t65b_ds.setString("noz2_totalLiter", 10);
			t65b_ds.setString("noz2_totalPrice", 10);
			t65b_ds.setByte  ("ETX");
			t65b_ds.setByte  ("BCC");
			t65b_Buf = t65b_ds.getByteStream();
			
			// 3 Nozzle type
			t65c_ds.setByte  ("STX");
			t65c_ds.setByte  ("SA");
			t65c_ds.setByte  ("UA");
			t65c_ds.setString("command", 2);
			t65c_ds.setByte  ("noz1_state");
			t65c_ds.setByte  ("noz2_state");
			t65c_ds.setByte  ("noz3_state");
			t65c_ds.setString("reserved", 3);
			t65c_ds.setString("noz1_totalLiter", 10);
			t65c_ds.setString("noz1_totalPrice", 10);
			t65c_ds.setString("noz2_totalLiter", 10);
			t65c_ds.setString("noz2_totalPrice", 10);
			t65c_ds.setString("noz3_totalLiter", 10);
			t65c_ds.setString("noz3_totalPrice", 10);
			t65c_ds.setByte  ("ETX");
			t65c_ds.setByte  ("BCC");
			t65c_Buf = t65c_ds.getByteStream();
			
		} catch (Exception e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
		
    }
    
	protected boolean compareBCC(byte[] buf) throws Exception {
		int	i;
	
		for (i=0; i<buf.length-2; i++) {
			if (buf[i]== ETX) 
				break;
		}
		
		if (getBCC (buf) == buf[i+1])		
			return true;
		else
			return false;
	}
	
	protected boolean compareNozID(byte[] buf) throws Exception {

		if (RxBuf[1]==nozID)		
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
		
		//LogUtility.getPumpALogger().debug("length="+dat.length+", stxIdx="+stxIdx+", etxIdx="+etxIdx);
		//Show.datas(dat, dat.length, 20);

		if (stxIdx<=0 || etxIdx<=0) // No exist STX, ETX
			return -1;
		
		//--- Calculate BCC
		for (i=stxIdx; i<=etxIdx; i++) {
			bcc = (byte) (bcc ^ dat[i]); // XOR
		}
		
		return bcc;
	}

	protected byte[] getInitData(byte[] dat) throws Exception {
		
		//--- Calculate InitData
		int uCRC = 0;
		for (int i=0; i<6 ;i++)
			uCRC = makeCCITT (dat[i], uCRC);

		byte byt = (byte) (((uCRC>>8) & 0x00ff) + (uCRC & 0x00ff));
		Formatter form = new Formatter();
		form.format("00%02X", byt & 0x0ff);
		String str = form.toString();
		byte initData[] = str.getBytes();

		return initData;
	}
	
	protected boolean isSkipWorkingMessage (WorkingMessage wm) throws Exception {
		
		boolean skip=false;
		String bPrice;
				
		if (wm.getCommand().equals("PB")) { // 정액정량 설정
			
			// 단가를 t10_ds에 저장한다.
			PB_WorkingMessage PB_wm = (PB_WorkingMessage) wm;
			bPrice = PB_wm.getBasePrice().substring(0, 4);	
			t10_ds.editString("basePrice", bPrice, 4);
			t10_Buf = t10_ds.getByteStream();

			m_basePrice = PB_wm.getBasePrice();
			
			skip = false;
		}
		else if (wm.getCommand().equals("P3_1")) { // 주유기 환경설정
			
			// 단가를 t10_ds에 저장한다.
			P3_1_WorkingMessage P3_wm = (P3_1_WorkingMessage) wm;
			bPrice = P3_wm.getBasePrice().substring(0, 4);	
			t10_ds.editString("basePrice", bPrice, 4);
			t10_Buf = t10_ds.getByteStream();

			m_basePrice = P3_wm.getBasePrice();
			
			skip = false;
		}
		else if (wm.getCommand().equals("PA")) { // 노즐제어 요청

			PA_WorkingMessage PA_wm = (PA_WorkingMessage) wm;
			int nNozState = Change.toValue(PA_wm.getNozzleState());
			
			m_nozLock = (nNozState==0 ? true : false);

			if (m_nozLock==true) {
				m_recvedNozLock=true;
				TxQue.enQueue(t15_Buf); // 상태정보요청
			}
			else
				TxQue.enQueue(t10_Buf); // 주유허가(정액/정량 설정)
			
			skip = false;
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
			
			skip = true;
		}
		
		return skip;
	}
	
	protected int makeCCITT (int wData, int wCRC) throws Exception {
		wData <<= 1;
		for (int i = 8; i > 0 ;i--)
		{
			wData >>= 1;			
			if (((wData ^ wCRC) & 0x0001) != 0)
				wCRC = (wCRC>>1) ^ 0x8408;
			else
				wCRC >>= 1;
		}	// end for (int i=8; i>0 ;i--)

		return wCRC;
	}
	
	protected void makeLineError () throws Exception {
		
		SE_wm = new SE_WorkingMessage();
		
		SE_wm.setNozzleNo(Change.toString("%02d", nozNo));
		SE_wm.setDeviceType(Change.toString("%02d", nozType));
		SE_wm.setStatus("1");
		SE_wm.setStatusCode("601"); // 회선불량
		SE_wm.setErrMsg("주유기 회선불량");

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
				statusCode = 651; // 노즐다운
				errorMsg = "노즐다운";
				break;
			case 1:
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
			case 650:
				statusCode = 650; // 정상
				errorMsg = "정상";
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

	public void processRecvSTX () throws SerialConnectException, Exception {
		
		//############# State check ##############//
		if (RxBuf[3]=='0' && RxBuf[4]=='0') { // 주유기 초기화
						
			rcvInitBuf[0] = RxBuf[3];
			rcvInitBuf[1] = RxBuf[4];
			rcvInitBuf[2] = RxBuf[5];
			rcvInitBuf[3] = RxBuf[6];
			rcvInitBuf[4] = RxBuf[7];
			rcvInitBuf[5] = RxBuf[8];

			//LogUtility.getPumpALogger().debug ("Recv InitData =");
			//Show.datas(rcvInitBuf, rcvInitBuf.length, 20);
			LogUtility.getPumpALogger().debug("####### Completed Initialization : nozNo="+nozNo+"#######");
			
			byte[] sndInitBufTmp = getInitData (rcvInitBuf);
			byte[] sndInitBuf = new byte[9];
			sndInitBuf[0] = STX;
			sndInitBuf[1] = nozID;
			sndInitBuf[2] = SEN;
			sndInitBuf[3] = sndInitBufTmp[0];
			sndInitBuf[4] = sndInitBufTmp[1];
			sndInitBuf[5] = sndInitBufTmp[2];
			sndInitBuf[6] = sndInitBufTmp[3];
			sndInitBuf[7] = ETX;
			sndInitBuf[8] = getBCC (sndInitBuf);

			TxQue.enQueue(sndInitBuf);
			progressStep=1;
		}
		else if (RxBuf[3]=='6' && RxBuf[4]=='0') { // 주유기 상황보고

			progressStep = 2;
		} 
		else if (RxBuf[3]=='6' && RxBuf[4]=='1') { // 주유중 정보보고

			// 주유중 자료 누락처리용 추가(08/10/10)
			waitPumpingData=false;
			waitPumpingData_Cnt=0;
			
			progressStep = 3;
			
			short state = (short) (RxBuf[5] - 0x30);
			nozState = state;
			
			switch (state) {
			case 3 :  // 주유중
				nozState = 4;
				break;
			case 4 :  //주유완료
				nozState = 5;
				break;
			}
			
			pumpingStart = (nozState==4 ? true : pumpingStart); // 주유시작
			nozState = (pumpingStart==true && nozState==0 ? 5 : nozState); //주유완료자료 미수신 처리
			
			//System.out.printf ("1.====> nozState=%s progState=%d\n", nozState, progressStep);
		}
		else if (RxBuf[3]=='6' && RxBuf[4]=='5') { // 주유기 적산치 보고

			if (nozState==1) { // 노즐업상태에서 수신
				
				SJ_wm = (SJ_WorkingMessage) generateWorkingMessage(RxBuf, "SJ"); 
				//last_SJ_TotalGauge = SJ_wm.getTotalGauge();
				insertRecvQueue(SJ_wm); // 주유시작 자료 송신
				
				sentPumpingStartInfo=true;
				waitTotalGuageFor_SJ=false;
				waitGaugeForSJ_Cnt = 0;
			}
			else if (progressStep==3 && nozState==4) { // 주유중 수신
				// Skip
			}
			else if (progressStep==3 && nozState==0) { // 주유완료후 수신
				progressStep = 4;
				nozState = 5;
			} 
			else 
				insertRecvQueue(generateWorkingMessage(RxBuf, "S5")); // 토털게이지 자료 송신
		}


		//############# STX Process ##############//
		switch (progressStep) {
		case 3 :

			if (nozState==0) { // 노즐다운

				pumpingStart=false;
				sentPumpingStartInfo=false;
				waitTotalGuageFor_SJ=false; // 추가->주유시작(SJ) 반복생성 문제해결(08/08/11)
				
				makeStatusInfo(nozState); // 상태정보 전송
				
				if (m_nozLock==false && nozType==1) { // 일반주유기
					if (Change.toValue(m_basePrice) > 0)
						TxQue.enQueue(t10_Buf); // 주유허가(통상주유)
				}

				// 비상정지/해제 상태정보 처리
				if (m_nozLock==true)
					makeStatusInfo(656); // 비상정지
				else if (m_recvedNozLock==true) {
					makeStatusInfo(657); // 비상해제
					m_recvedNozLock=false;
				}
				
				// POS에서 프리셋 설정후 취소처리(주유값이 0 인 주유완료 자료)
				if (beforeNozzleUp==true && realPumpingStart==false) { // 정상 주유완료자료가 아니면

					makeStatusInfo(5); // 상태정보 전송
					
					S4_wm = new S4_WorkingMessage();
					S4_wm.setNozzleNo(Change.toString("%02d",nozNo));
					S4_wm.setLiter("0000000");
					S4_wm.setBasePrice(m_basePrice);
					S4_wm.setLiter("00000000");
					//S4_wm.setTotalGauge(last_SJ_TotalGauge);
					S4_wm.setTotalGauge("0000000000"); // 수정(2008/11/05)
					insertRecvQueue(S4_wm); // 주유완료 자료
				}
				beforeNozzleUp=false;
				
			}
			else if (nozState==1) { // 노즐업 & 일반주유기

				makeStatusInfo(nozState); // 상태정보 전송

				beforeNozzleUp=true;
				realPumpingStart=false;
				
				if (m_nozLock==false && nozType==1) { // 일반주유기
					if (Change.toValue(m_basePrice) > 0)
						TxQue.enQueue(t10_Buf); // 주유허가(통상주유)
				}
				
				if (sentPumpingStartInfo==false) {
					TxQue.enQueue(t20_Buf); // 토털게이지 요청(주유시작 정보 수신용)
					
					sentPumpingStartInfo=true;
					waitTotalGuageFor_SJ=true;
				}

			}
			else if (nozState == 4) { // 주유중
				
				// 수정(2008/09/05)
				S3_wm = (S3_WorkingMessage) generateWorkingMessage(RxBuf, "S3");
				if (S3_wm != null) {
					int nPrice = Change.toValue(S3_wm.getPrice());
					int nLiter = Change.toValue(S3_wm.getLiter());

					if (nPrice > 0 && nLiter > 0) {			
						lastPumpingData = RxBuf.clone(); // 최종 주유정보 저장
						realPumpingStart=true;
	
						if (firstPumpingData==true) {
							makeStatusInfo(nozState); // 상태정보 전송
							firstPumpingData=false;
						}
					
						insertRecvQueue(S3_wm);
					}
				}

				TxQue.enQueue(t15_Buf); // 주유기 Status(주유자료) 요청
				waitPumpingData=true; // 주유중 자료 누락처리용 추가(08/10/10)
			}
			else if (nozState == 5) { // 주유완료
				
				// 수정(2008/09/05)
				S3_wm = (S3_WorkingMessage) generateWorkingMessage(RxBuf, "S3");
				if (S3_wm != null) {
					int nPrice = Change.toValue(S3_wm.getPrice());
					int nLiter = Change.toValue(S3_wm.getLiter());
					if (nPrice > 0 || nLiter > 0) // 수정(08/11/24) : && -> ||				
						lastPumpingData = RxBuf.clone(); // 최종 주유정보 저장
				}
					
				TxQue.enQueue(t20_Buf); // 토털게이지 요청
				waitTotalGuageFor_S4=true;

				progressStep = 4;
				pumpingStart=false;
				sentPumpingStartInfo=false;
				firstPumpingData=true;
				makeStatusInfo(nozState); // 상태정보 전송
			}
			break;
			
		case 4 :
			if (nozState == 5) { // 주유완료후 토털게이지 수신
				byte[][] tBuf = new byte [2][80];
				tBuf[0] = lastPumpingData.clone(); // 최종 주유정보
				flushBuffer(lastPumpingData);
				tBuf[1] = RxBuf.clone(); // Total guage

				progressStep = 3;
				nozState = 0;
				
				S4_wm = (S4_WorkingMessage) generateWorkingMessage(tBuf, "S4"); 
				S4_wm.setNozzleNo(Change.toString("%02d",nozNo));
				insertRecvQueue(S4_wm); // 주유완료 자료
				
				makeStatusInfo(nozState); // 상태정보 전송
				
				waitTotalGuageFor_S4=false;
				waitGaugeForS4_Cnt = 0;
			}
			break;
		}
	}

	protected boolean recvTail_proc () throws Exception, SerialConnectException {
		
		int numBytes;

		//--- send : ACK ---//
		if (sendText (ACK1)==false) {
			if (dispLevel>=3)
				LogUtility.getPumpALogger().debug("5.Send ACK1 fail! (Noz="+nozNo+")");
			return false;
		}
		
		if (dispLevel>=3)
			LogUtility.getPumpALogger().debug("5.Send ACK1 \n");
		
		//--- recv : EOT ---//
		numBytes = recvText(RxBuf);
		if (numBytes < 1) {
			if (dispLevel>=3)
				LogUtility.getPumpALogger().debug("5.Recv EOT fail (Noz="+nozNo+")");
			return false;
		}
	
		if (RxBuf[0] == EOT) {	//--- recv : EOT ---//
			if (dispLevel>=3)
				LogUtility.getPumpALogger().debug("5.Recv EOT (Noz="+nozNo+")");
			return true;
		}
		else {
			if (dispLevel>=3)
				LogUtility.getPumpALogger().debug("5.Recv EOT fail (Noz="+nozNo+")");
			return false;
		}
	}
	
	protected int recvText(byte[] RxBuf) throws Exception, SerialConnectException {

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
					if (RxBuf[i]==EOT || RxBuf[i]==ACK || RxBuf[i]==NAK) { // recv EOT/ACK/NAK
						
						//read NozID 추가(09/01/05)
						RxByt[0] = 0x00;
						is.read(RxByt, 0, 1);
						RxBuf[i+1] = RxByt[0];
						
						numBytes=i+2;
						break;
					}
				
				if (RxBuf[i]==ETX) { // recv ETX next STX
					if (STXFlag==true) {
						Sleep.sleep(1);
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
			LogUtility.getPumpALogger().error(e.getMessage(), e);
			return 0;
		}
	}
	
	@Override
	public void requestData() throws Exception, SerialConnectException {

		int 	loopCnt=0;
		byte[]  byt = new byte[1];
		boolean	occurLineErr = (lineErrCnt >= minErrCnt);
		
		if (occurLineErr) { // Check line communication error

			if (issueLineErr==true) {
				makeLineError();
				TxQue.flushQueue();
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
		if (firstRequest==true) {
			firstRequest=false;
			TxQue.enQueue(t15_Buf); // 상태정보요청
		}
		if (m_statusCode==601) { // 회선불량
			if (lineCommCheckCnt%(minErrCnt+5)==0) {
				lineCommCheckCnt=0;
				TxQue.enQueueNewer(t15_Buf); // 상태정보요청
			}
			lineCommCheckCnt++;
		}
		
		// 주유시작(SJ) 토털게이지 미수신 처리
		if (waitTotalGuageFor_SJ==true) {
			if (waitGaugeForSJ_Cnt > 15) {
				SJ_wm = new SJ_WorkingMessage();
				SJ_wm.setNozzleNo(Change.toString("%02d",nozNo));
				//SJ_wm.setTotalGauge(last_S4_TotalGauge);
				SJ_wm.setTotalGauge("0000000000");
				insertRecvQueue(SJ_wm); // 주유시작 자료 송신

				waitGaugeForSJ_Cnt=0;
				sentPumpingStartInfo=true;
				waitTotalGuageFor_SJ=false; // 수정(08/08/11)

				LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> 주유시작 토털게이지(t0) 미수신, 주유시작(SJ)전문 생성");
			}
			else
				waitGaugeForSJ_Cnt++;
			
			LogUtility.getPumpALogger().debug("waitGaugeForSJ_Cnt="+waitGaugeForSJ_Cnt);
		}
		
		// 주유중 자료(S3) 미수신 처리
		if (nozState==4 && waitPumpingData==true) {
			//LogUtility.getPumpALogger().debug("waitPumpingData_Cnt=" + waitPumpingData_Cnt);
			if (waitPumpingData_Cnt >= MAX_WAIT_PDATA) {
				TxQue.enQueueNewer(t15_Buf);				
				
				LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> 주유중 자료(S3)값 미수신, 주유자료 요청(p0), waitPumpingData_Cnt="+waitPumpingData_Cnt);
				waitPumpingData_Cnt=0;
			}
			else
				waitPumpingData_Cnt++;
		}

		// 주유완료(S4) 토털게이지 미수신 처리(2008/08/13)
		if (waitTotalGuageFor_S4==true) {
			if (waitGaugeForS4_Cnt > 15) {				

				byte[][] tBuf = new byte [2][80];
				tBuf[0] = lastPumpingData.clone(); // 최종 주유정보
				flushBuffer(lastPumpingData);
				tBuf[1] = RxBuf.clone(); // Total guage

				S4_wm = (S4_WorkingMessage) generateWorkingMessage(tBuf, "S4"); 
				S4_wm.setNozzleNo(Change.toString("%02d",nozNo));
				//S4_wm.setTotalGauge(last_SJ_TotalGauge);
				S4_wm.setTotalGauge("0000000000");
				insertRecvQueue(S4_wm); // 주유완료 자료
				
				progressStep = 3;
				nozState = 0;

				sentPumpingStartInfo=true;
				waitTotalGuageFor_S4=false;
				waitGaugeForS4_Cnt=0;

				LogUtility.getPumpALogger().debug("주유완료 토털게이지(65) 미수신, 주유완료(S4)전문 생성");
			}
			else
				waitGaugeForS4_Cnt++;
			
			LogUtility.getPumpALogger().debug("waitGaugeForS4_Cnt="+waitGaugeForS4_Cnt);
		}

		flushBuffer(RxBuf);
		
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
			if (wm.getCommand().equals("P3_1"))
				Show.datas(wmByte, wmByte.length, 20);
			*/
			wm=null;
		}
		
		if (dispLevel>=3) 
			LogUtility.getPumpALogger().debug("\nStart request ========> TatsunoN (Noz="+nozNo+")");

		mode = POL;
		while (true) {

			flushBuffer(RxBuf);
			
			if (loopCnt >= 2) break;
			loopCnt++;
			
			try {
				//##### Send ENQ #####//
				mode = (mode==POL? SEN : POL);
				ENQb[2] = mode;
				
				if (mode==SEN) {
					if (progressStep != 1 && /*TxQue.isEmpty()==true*/ TxQue.getItemCount()==0) // 송신자료 없으면
						continue;
				}
				else { // mode=POL
					if (progressStep != 1 && /*TxQue.isEmpty()==false*/ TxQue.getItemCount() > 0) // 송신자료 있으면
						continue;
				}

				if (dispLevel>=3) {
					if (mode==POL)
						LogUtility.getPumpALogger().debug("----POLLING MODE---- (progStep=" + progressStep + " , nozState=" + nozState + ")");
					else
						LogUtility.getPumpALogger().debug("----SENDING MODE---- (progStep=" + progressStep + " , nozState=" + nozState + ")");
				}
				
				//--- Send ENQ ---//
				if(sendText (ENQb) != true) {
					lineErrCnt++;
					continue;
				}
				
				if (dispLevel>=3) {
					LogUtility.getPumpALogger().debug("0.Send ENQ (Noz="+nozNo+")");
					Log.datas(ENQb, 4, 20);
				}

				//--- recv ACK/STX ---//	
				if (recvText(RxBuf) < 1) {
					lineErrCnt++;
					if (dispLevel>=2)
						LogUtility.getPumpALogger().debug("0.Recv ACK/STX fail!(No response) lineErrCnt="+lineErrCnt+"/"+minErrCnt+" (Noz="+nozNo+")");
					else if (lineErrCnt >= minErrCnt)
						LogUtility.getPumpALogger().debug("0.Recv ACK/STX fail!(No response) lineErrCnt="+lineErrCnt+"/"+minErrCnt+" (Noz="+nozNo+")");

					continue;
				}
				
				//###### Recv data(Polling mode) : STX ######//
				if (mode==POL) { // 0x51

					if (RxBuf[0] == STX) {

						if (dispLevel>=3) {
							LogUtility.getPumpALogger().debug("1.Recv STX(Data) (Noz="+nozNo+")");
							Log.datas(RxBuf, 80, 20);
						}

						if (compareNozID(RxBuf)==false) { 
							LogUtility.getPumpALogger().debug ("1.Recv STX NozID mismatch-1.0! (Noz="+nozNo+")");
							Log.datas(RxBuf, 40, 20);
							lineErrCnt++;
							
							is.read(); // flush inputStream
							continue;
						}
						if (compareBCC(RxBuf)==false) {
							LogUtility.getPumpALogger().debug ("1.Recv STX BCC fail! (Noz="+nozNo+")");
							sendText (NAKb);
							continue;
						}
						else {
							if (dispLevel==1 || dispLevel==2) {
								LogUtility.getPumpALogger().debug ("1.Recv STX with normal (Noz="+nozNo+")");
								Log.datas(RxBuf, 80, 20);
							}
							
							processRecvSTX(); // 수신 노즐 데이터 처리
							
							if (recvTail_proc()==false) 
								lineErrCnt++;
							else
								lineErrCnt=0; // Normal terminated
						}
					}
					else if (RxBuf[0] == EOT) {
						
						if (compareNozID(RxBuf)==false) { 
							LogUtility.getPumpALogger().debug ("1.Recv EOT NozID mismatch-1.0! (Noz="+nozNo+")");
							Log.datas(RxBuf, 40, 20);
							lineErrCnt++;
							
							is.read(); // flush inputStream
							continue;
						}
						
						if (dispLevel>=3)
							LogUtility.getPumpALogger().debug("1.Recv EOT (Noz="+nozNo+")");
						
						lineErrCnt=0; // Normal terminated
						continue;
					} else {
						if (dispLevel>=3)
							LogUtility.getPumpALogger().debug("1.Recv fail! (Noz="+nozNo+")");
						
						lineErrCnt++;
						continue;
					}
				}
				//###### Send data(Sending mode) ######//
				else if (mode==SEN) { // 0x41

					if (RxBuf[0]==ACK) { // ACK + SA
						
						if (compareNozID(RxBuf)==false) { 
							LogUtility.getPumpALogger().debug ("2.Recv ACK NozID mismatch-1.0! (Noz="+nozNo+")");
							Log.datas(RxBuf, 40, 20);
							lineErrCnt++;
							
							is.read(); // flush inputStream
							continue;
						}
						
						is.read (byt,0,1);
						
						if (byt[0]==0x30) { // recv : ACK0

							if (dispLevel>=3)
								LogUtility.getPumpALogger().debug("2.Recv ACK0 (Noz="+nozNo+")");
		
							if (TxQue.isEmpty()==false) { // 송신 데이터 있으면
	
								//###### Send data : send working-data ######//
								TxBuf = TxQue.getFirstItem();
								TxBuf[1] = nozID;
								TxBuf[2] = mode;
								setBCC (TxBuf); // write BCC
								if(sendText(TxBuf) != true) {
									if (dispLevel>=2) {
										LogUtility.getPumpALogger().debug("2.Send STX fail! (Noz="+nozNo+")");
										Log.datas(TxBuf, TxBuf.length, 20);
									}
									lineErrCnt++;
									continue;
								}
																
								if (dispLevel>=3) {
									LogUtility.getPumpALogger().debug("2.Send STX(TxBuf) (Noz="+nozNo+")");
									Log.datas(TxBuf, TxBuf.length, 20);
								}
								
								if (TxBuf.length > 500) { // 추가(09/01/07)
									Sleep.sleep(TxBuf.length + 100);
									LogUtility.getPumpALogger().debug("추가지연 발생. TxBuf.length=" + TxBuf.length + "(ODT="+nozNo+")");
								}						
				
								if (recvText(RxBuf) < 1) {
									if (dispLevel>=3)
										LogUtility.getPumpALogger().debug("2.Recv ACK1 fail! (Noz="+nozNo+")");
									lineErrCnt++;
									continue;
								}
								
								if (RxBuf[0]==ACK) {
									
									if (compareNozID(RxBuf)==false) { 
										LogUtility.getPumpALogger().debug ("2.Recv ACK1 NozID mismatch! (Noz="+nozNo+")");
										Log.datas(RxBuf, 40, 20);
										lineErrCnt++;
										
										is.read(); // flush inputStream
										continue;
									}
									
									is.read (byt,0,1);
									if (byt[0]==0x31) { // recv : ACK1
										
										TxQue.deQueue(); // remove item
										
										if (dispLevel>=3)
											LogUtility.getPumpALogger().debug("2.Recv ACK1 (Noz="+nozNo+")");
										
										if (dispLevel==2) {
											LogUtility.getPumpALogger().debug("2.Send STX with normal (Noz="+nozNo+")");
											Log.datas(TxBuf, TxBuf.length, 20);
										}
										lineErrCnt=0; // Normal terminated
									}
								}
								else if (dispLevel>=3) {
									LogUtility.getPumpALogger().debug("2.Recv ACK1 fail! (Noz="+nozNo+")");
									lineErrCnt++;
								}
							}
						}
						else { // recv mismatched data
							LogUtility.getPumpALogger().debug("2.recv mismatched data! (Noz="+nozNo+")");
							is.read(); // flush inputStream
						}
					}
					else if (RxBuf[0] == EOT) {
						
						if (compareNozID(RxBuf)==false) { 
							LogUtility.getPumpALogger().debug ("1.Recv EOT NozID mismatch-1.1! (Noz="+nozNo+")");
							Log.datas(RxBuf, 40, 20);
							lineErrCnt++;
							
							is.read(); // flush inputStream
							continue;
						}
						
						if (dispLevel>=3) 
							LogUtility.getPumpALogger().debug("2.Recv EOT (Noz="+nozNo+")");
						
						lineErrCnt=0; // Normal terminated
						continue;
					} else {
						if (dispLevel>=3) 
							LogUtility.getPumpALogger().debug("2.Recv fail! (Noz="+nozNo+")  itemCount=" + TxQue.getItemCount());
						lineErrCnt++;
						continue;
					}
				} 
			} catch (Exception e) {
				LogUtility.getPumpALogger().debug("Exception occurr! (Noz="+nozNo+")");
				lineErrCnt++;
				LogUtility.getPumpALogger().error(e.getMessage(), e);
			}
		}
	}
	
	protected void retryRecvData (byte[] buf) throws Exception {
		
		if (buf[3]=='6' && buf[4]=='1') {
			TxQue.enQueueNewer(t15_Buf);
		}
		else if (buf[3]=='6' && buf[4]=='5') {
			TxQue.enQueueNewer(t20_Buf);
		}
	}
	
	public void run() {

	}
	
	protected boolean sendTail_proc () throws Exception, SerialConnectException {

		return true;
	}

	public boolean sendText(byte buf) throws SerialConnectException {

		Sleep.sleep(writeStartInterval);
		
	    try {
			os.write(buf);
		} catch (IOException e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	
		return true;
	}
	
	public boolean sendText(byte[] buf) throws SerialConnectException {

		Sleep.sleep(writeStartInterval);
		
	    try {
			os.write(buf);
			//LogUtility.getPumpALogger().debug ("Send Text : [0]:" + buf[0] + " [1]:" + buf[1]);
			//LogUtility.getPumpALogger().debug ("------>" + buf);
		} catch (IOException e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	
		return true;
	}
	
	public boolean sendText(String txt) throws SerialConnectException {

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

		int	numBytes;
		
		switch (event.getEventType()) {
	
			case SerialPortEvent.BI:
			case SerialPortEvent.OE:
			case SerialPortEvent.FE:
			case SerialPortEvent.PE:
			case SerialPortEvent.CD:
			case SerialPortEvent.CTS:
			case SerialPortEvent.DSR:
			case SerialPortEvent.RI:
			case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
				break;
			case SerialPortEvent.DATA_AVAILABLE:
				byte[] RxBuf = new byte[200];

		    try {
		    	while (is.available() > 0)
		    		numBytes = is.read(RxBuf);

		    	System.out.print("Received Text by event : " + new String(RxBuf) + "\n");
		    } catch (IOException e) {}
	
		    break;
		}
	}

	protected void setBCC(byte[] buf) throws Exception {
		int	i;
		
		for (i=0; i<buf.length-2; i++) {
			if (buf[i]==ETX) 
				break;
		}
		
		buf[i+1] = getBCC(buf);
	}

	protected boolean verifyData (byte[] buf) throws Exception {
		
		int size=0, bufLen;
		String cmd="";
		
		for (bufLen=0; bufLen<buf.length; bufLen++)
			if (buf[bufLen]==ETX) break;
		bufLen+=2;
			
		if (buf[3]=='0' && buf[4]=='0') { // 초기화
			//size = 11;
			//cmd = "00";
			return true;
		}
		else if (buf[3]=='6' && buf[4]=='0') { // 주유기 상황보고
			size = 9;
			cmd = "60";
		}
		else if (buf[3]=='6' && buf[4]=='2') { // 수신전문 에러 메시지
			size = 10;
			cmd = "62";
		}
		else if (buf[3]=='6' && buf[4]=='1') { // 주유자료 수신
			size = 28;
			cmd = "61";
		}
		else if (buf[3]=='6' && buf[4]=='5') { // 토털게이지 자료 수신
			size = 33;
			cmd = "65";
		}
		else 
			return false;

		//--- 데이터 검증(데이터 길이와 ETX 확인) ---//
		if (size != bufLen || buf[size-2] != ETX) {
			LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> 이상데이터 수신("+cmd+"), 실데이터길이=" + 
					size + ", 수신데이터길이=" + bufLen + ", ETX=" + buf[size-1]);
			return false;
		}

		//--- 데이터 검증(숫자여부) ---//
		for (int i=5; i<size-2; i++) {
			if (buf[i] < 0x30 || buf[i] > 0x39)
				if (buf[i] != 0x20) { // 0 ~ 9 및 공백이 아니면
					LogUtility.getPumpALogger().debug("nozzle=" + nozNo + 
						" -> 이상데이터 수신("+cmd+"), 숫자나 공백이 아님, 수신데이터=" + buf[i]);
					return false;
				}
		}
		
		return true;
	}
}
