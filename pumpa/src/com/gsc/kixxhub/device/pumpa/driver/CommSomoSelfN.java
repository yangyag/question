package com.gsc.kixxhub.device.pumpa.driver;

import gnu.io.CommPortOwnershipListener;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.util.Calendar;
import java.util.Formatter;
import java.util.GregorianCalendar;

import com.gsc.kixxhub.common.data.pump.HC_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HD_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P5_1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P7_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S4_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S8_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SE_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.TR_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.BytesQue2;
import com.gsc.kixxhub.device.pumpa.common.Change;
import com.gsc.kixxhub.device.pumpa.common.DataStruct;
import com.gsc.kixxhub.device.pumpa.common.Log;
import com.gsc.kixxhub.device.pumpa.common.SerialConnectException;
import com.gsc.kixxhub.device.pumpa.common.Sleep;
import com.gsc.kixxhub.device.pumpa.define.CCommDriver;
import com.gsc.kixxhub.device.pumpa.translation.TransSomoSelfN;

public class CommSomoSelfN extends CCommDriver implements SerialPortEventListener, CommPortOwnershipListener, 
	Runnable {

    protected byte  	ACK  = 0x10;
	protected byte[] 	ACK0 = new byte[2];
	protected byte[] 	ACK1 = new byte[2];
	protected int		baseMinErrCnt=8, baseMaxSkipCnt=100;
	protected int		baseReadBuffInterval   = 120+10; // 스마트주유소 PJT 보정
	protected int		baseReadStartInterval  = 10+3; // 스마트주유소 PJT 보정
	protected int		baseWriteStartInterval = 10+3; // 스마트주유소 PJT 보정
	protected int 	dispLevel=0;

	protected byte  	ENQ  = 0x05;
	
    protected byte[] 	enq_Buf = new byte[4];
    protected byte  	EOT  = 0x04;
    protected byte  	ETX  = 0x03;
    protected byte  	SEL  = 0x41;
    protected byte  	STX  = 0x02;
    
	protected boolean firstRequest=true;
	protected HC_WorkingMessage HC_wm;
	protected HD_WorkingMessage HD_wm;
	protected boolean	issueLineErr=true;
	
	protected int		lineCommCheckCnt=0;
	protected int		lineErrCnt=0;
    protected boolean	m_changeBasePrice=false;
	protected String 	m_realBasePrice	="000000"; 		// 실 주유단가
	protected String 	m_realLiter			="0000000";    	// 실 주유량
	protected String 	m_realPrice		="00000000";   // 실 주유금액
	protected int		m_statusCode=601;
	protected byte  	mode = SEL;
	protected byte  	NAK  = 0x15;
	protected byte[] 	NAK0 = new byte[2];
	protected byte[] 	NAK1 = new byte[2];

	protected byte		nozID;
	protected short		nozState=0;
	//protected byte[] 	lastPumpingData=new byte[buffSize];
	protected String 	nozStr = "";
    
    protected PB_WorkingMessage PB_wm;
    protected byte  	POL  = 0x51;
    protected byte  	PREFIX_ENQ = 0x05;
    //progressStep : 0=초기화전, 1=초기화후, 2=상태정보송신후, 3=주유중, 4=주유완료, 
    //                         5=토털게이지수신/주유완료자료 송신
    protected int 	progressStep=3;
    protected byte[] 	rcvInitBuf = new byte[6];
    protected byte[] 	RxBuf = new byte[1024];
    protected S4_WorkingMessage S4_wm;
    protected S8_WorkingMessage S8_wm;
    protected SE_WorkingMessage SE_wm;


    protected byte[] 	t0201_Buf;
    protected DataStruct t0201_ds = new DataStruct();
    protected byte[] 	t0202_Buf;
    
    protected DataStruct t0202_ds = new DataStruct();
    protected byte[] 	t0205_Buf;
    protected DataStruct t0205_ds = new DataStruct();
    protected byte[] 	t0206_Buf;
    protected DataStruct t0206_ds = new DataStruct();
    protected byte[] 	t02eb_Buf;
    protected DataStruct t02eb_ds = new DataStruct();
    protected byte[] 	t02sb_Buf;
    protected DataStruct t02sb_ds = new DataStruct();
    //protected byte[] 	t22_Buf;
    protected byte[] 	t50_Buf;
    //protected DataStruct t22_ds = new DataStruct();
    protected DataStruct t50_ds = new DataStruct();
    protected byte[] 	t51_Buf;
    protected DataStruct t51_ds = new DataStruct();
    protected byte[] 	t52_Buf;
    protected DataStruct t52_ds = new DataStruct();
    protected byte[] 	t54_Buf;
    protected DataStruct t54_ds = new DataStruct();
    protected byte[] 	t55_Buf;
    protected DataStruct t55_ds = new DataStruct();

    protected byte[] 	t60_Buf;
    protected DataStruct t60_ds = new DataStruct();
    protected byte[] 	t61_Buf;
    protected DataStruct t61_ds = new DataStruct();
    protected byte[] 	t80_Buf;
    protected DataStruct t80_ds = new DataStruct();
    protected byte[] 	t81_Buf;
    protected byte[] 	t90_Buf;
    protected byte[] 	t91_Buf;
    protected byte[] 	t92_Buf;
    protected TR_WorkingMessage TR_wm;
    //protected WorkingMessage		wmUp;
	protected TransSomoSelfN trans = new TransSomoSelfN();
    //protected int		buffSize = 1024;
	protected byte[] 	TxBuf = new byte[2048];
    protected BytesQue2 TxQue = new BytesQue2(30);
	
    public CommSomoSelfN (int nozNum, String romVerStr) {
    	
    	byte	bcc;
		Formatter form = new Formatter ();
		byte[] 	nozByt = new byte[2];
		
		nozNo = nozNum;
		romVer = romVerStr;
		
		form.format("%02d", nozNo);
		nozStr = form.toString();
		nozByt = nozStr.getBytes();
		nozID = (byte) (nozNum - 1 + 0x40);
		
	    ACK0[0] = ACK;
	    ACK0[1] = 0x30;
	    ACK1[0] = ACK;
	    ACK1[1] = 0x31;
	    NAK0[0] = NAK;
	    NAK0[1] = 0x30;
	    NAK1[0] = NAK;
	    NAK1[1] = 0x31;
		
		//--- prefix-ENQ ---//
		enq_Buf[0] = EOT;
		enq_Buf[1] = nozID;
		enq_Buf[2] = SEL;
		enq_Buf[3] = ENQ;

		readBuffInterval   	= baseReadBuffInterval;
		readStartInterval  	= baseReadStartInterval;
		writeStartInterval 	= baseWriteStartInterval;
		minErrCnt				= baseMinErrCnt;
		maxSkipCnt 		   	= baseMaxSkipCnt;
		
		try {
			//--- 환경설정 정보(Start block) ---//
			t02sb_ds.addByte  ("STX", STX);
			t02sb_ds.addByte  ("SA", nozID);
			t02sb_ds.addByte  ("UA", SEL);
			t02sb_ds.addString("command", "02", 2);
			t02sb_ds.addString("order", "0", 1);
			t02sb_ds.addString("block", "1", 1);
			t02sb_ds.addString("fileNo", "  ", 2);
			t02sb_ds.addString("fileName", "Self0001", 8);
			t02sb_ds.addString("fileExt", "env", 3);
			t02sb_ds.addString("outputDevice", "00", 2);
			t02sb_ds.addString("reserved", "", 20);
			t02sb_ds.addByte  ("ETX", ETX);
			t02sb_ds.addByte  ("BCC", (byte) ' ');
			t02sb_Buf = t02sb_ds.getByteStream();
			
			//--- 환경설정 정보(사업자 정보) ---//
			t0201_ds.addByte  ("STX", STX);
			t0201_ds.addByte  ("SA", nozID);
			t0201_ds.addByte  ("UA", SEL);
			t0201_ds.addString("command", "02", 2);
			t0201_ds.addString("order", "0", 1);
			t0201_ds.addString("block", "2", 1);
			t0201_ds.addString("fileNo", "01", 2);
			t0201_ds.addString("fileLength", "235", 3);
			t0201_ds.addString("storeCode", "", 10);
			t0201_ds.addString("businessCode", "", 12);
			t0201_ds.addString("storeName", "", 40);
			t0201_ds.addString("president", "", 30);
			t0201_ds.addString("postNo", "", 7);
			t0201_ds.addString("address1", "", 50);
			t0201_ds.addString("address2", "", 50);
			t0201_ds.addString("telephone", "", 16);
			t0201_ds.addString("reApprovePrice", "", 10);
			t0201_ds.addString("bonusStoreCode", "", 10);
			t0201_ds.addByte  ("ETX", ETX);
			t0201_ds.addByte  ("BCC", (byte) ' ');
			t0201_Buf = t0201_ds.getByteStream();

			//--- 환경설정 정보(머리말/꼬리말) ---//
			t0202_ds.addByte  ("STX", STX);
			t0202_ds.addByte  ("SA", nozID);
			t0202_ds.addByte  ("UA", SEL);
			t0202_ds.addString("command", "02", 2);
			t0202_ds.addString("order", "0", 1);
			t0202_ds.addString("block", "2", 1);
			t0202_ds.addString("fileNo", "02", 2);
			t0202_ds.addString("fileLength", "150", 3);
			t0202_ds.addString("baseHeadMsg", "", 50);
			t0202_ds.addString("baseTailMsg1", "", 50);
			t0202_ds.addString("baseTailMsg2", "", 50);
			t0202_ds.addByte  ("ETX", ETX);
			t0202_ds.addByte  ("BCC", (byte) ' ');
			t0202_Buf = t0202_ds.getByteStream();

			//--- 환경설정 정보(주유기 정보) ---//
			t0205_ds.addByte  ("STX", STX);
			t0205_ds.addByte  ("SA", nozID);
			t0205_ds.addByte  ("UA", SEL);
			t0205_ds.addString("command", "02", 2);
			t0205_ds.addString("order", "0", 1);
			t0205_ds.addString("block", "2", 1);
			t0205_ds.addString("fileNo", "05", 2);
			t0205_ds.addString("fileLength", "096", 3);
			
			t0205_ds.addString("nozNo", "", 2);
			t0205_ds.addString("basePrice", "", 4);
			t0205_ds.addString("oilCode", "", 4);
			t0205_ds.addString("oilName", "", 14);
			
			t0205_ds.addString("nozNo", "", 2);
			t0205_ds.addString("basePrice", "", 4);
			t0205_ds.addString("oilCode", "", 4);
			t0205_ds.addString("oilName", "", 14);
			
			t0205_ds.addString("nozNo", "", 2);
			t0205_ds.addString("basePrice", "", 4);
			t0205_ds.addString("oilCode", "", 4);
			t0205_ds.addString("oilName", "", 14);
			
			t0205_ds.addString("nozNo", "", 2);
			t0205_ds.addString("basePrice", "", 4);
			t0205_ds.addString("oilCode", "", 4);
			t0205_ds.addString("oilName", "", 14);
			
			t0205_ds.addByte  ("ETX", ETX);
			t0205_ds.addByte  ("BCC", (byte) ' ');
			t0205_Buf = t0205_ds.getByteStream();
			
			//--- 환경설정 정보(시스템 시각) ---//
			t0206_ds.addByte  ("STX", STX);
			t0206_ds.addByte  ("SA", nozID);
			t0206_ds.addByte  ("UA", SEL);
			t0206_ds.addString("command", "02", 2);
			t0206_ds.addString("order", "1", 1);
			t0206_ds.addString("block", "2", 1);
			t0206_ds.addString("fileNo", "06", 2);
			t0206_ds.addString("fileLength", "014", 3);
			t0206_ds.addString("systemTime", "", 14);
			t0206_ds.addByte  ("ETX", ETX);
			t0206_ds.addByte  ("BCC", (byte) ' ');
			t0206_Buf = t0206_ds.getByteStream();
			
			//--- 환경설정 정보(End block) ---//
			t02eb_ds.addByte  ("STX", STX);
			t02eb_ds.addByte  ("SA", nozID);
			t02eb_ds.addByte  ("UA", SEL);
			t02eb_ds.addString("command", "02", 2);
			t02eb_ds.addString("order", "0", 1);
			t02eb_ds.addString("block", "3", 1);
			t02eb_ds.addString("fileNo", "  ", 2);
			t02eb_ds.addString("blockCnt", "   ", 3);
			t02eb_ds.addString("fileExt", "env", 3);
			t02eb_ds.addString("reserved", "", 20);
			t02eb_ds.addByte  ("ETX", ETX);
			t02eb_ds.addByte  ("BCC", (byte) ' ');
			t02eb_Buf = t02eb_ds.getByteStream();
			/*
			t02_ds.addString("terminalSN", "", 2); // 사이즈 확인요망
			t02_ds.addString("terminalID", "", 2); // 사이즈 확인요망
			t02_ds.addByte  ("ETX", (byte) ETX);
			t02_ds.addByte  ("BCC", (byte) ' ');
			t02_Buf = t02_ds.getByteStream();
			*/
			
			/*
			//--- 시각설정 지시 ---//
			t22_ds.addByte  ("STX", (byte) STX);
			t22_ds.addByte  ("SA", nozID);
			t22_ds.addByte  ("UA", (byte) SEL);
			t22_ds.addString("command", "22", 2);
			t22_ds.addString("systemTime", "", 14);
			t22_ds.addByte  ("ETX", (byte) ETX);
			t22_ds.addByte  ("BCC", (byte) ' ');
			t22_Buf = t22_ds.getByteStream();
			*/
			//--- 주유기 상태 ---//
			t50_ds.addByte  ("STX", STX);
			t50_ds.addByte  ("SA", nozID);
			t50_ds.addByte  ("UA", SEL);
			t50_ds.addString("command", "50", 2);
			t50_ds.addString("nozNo", "", 2);
			t50_ds.addString("nozState", "", 1);
			t50_ds.addByte  ("ETX", ETX);
			t50_ds.addByte  ("BCC", (byte) ' ');
			t50_Buf = t50_ds.getByteStream();

			//--- 주유중/주유완료 ---//
			t51_ds.addByte  ("STX", STX);
			t51_ds.addByte  ("SA", nozID);
			t51_ds.addByte  ("UA", SEL);
			t51_ds.addString("command", "51", 2);
			t51_ds.addString("nozNo", "", 2);
			t51_ds.addString("nozState", "", 1);
			t51_ds.addString("liter", "", 7);
			t51_ds.addString("basePrice", "", 4);
			t51_ds.addString("price", "", 6);
			t51_ds.addByte  ("ETX", ETX);
			t51_ds.addByte  ("BCC", (byte) ' ');
			t51_Buf = t51_ds.getByteStream();
			
			//--- VAN, TMS 승인응답정보 ---//
			t52_ds.addByte  ("STX", STX);
			t52_ds.addByte  ("SA", nozID);
			t52_ds.addByte  ("UA", SEL);
			t52_ds.addString("command", "52", 2);
			t52_ds.addString("flag", "", 1);
			t52_ds.addString("dataLength", "", 4);
			t52_ds.addString("nozNo", "", 2);
			t52_ds.addString("cardNo", "", 18);
			t52_ds.addString("carInfo", "", 18);
			t52_ds.addString("driverName", "", 30);
			t52_ds.addString("saleLiter", "", 7);
			t52_ds.addString("saleBasePrice", "", 6);
			t52_ds.addString("receiptLiter", "", 7); // 전표량
			t52_ds.addString("saleType", "", 1);
			t52_ds.addString("customerType", "", 1);
			t52_ds.addString("SaleState", "", 1);
			t52_ds.addString("basePricePrint", "", 1); // 단가 출력여부
			t52_ds.addString("depositReceipt", "", 1); // 보관증 발행여부
			t52_ds.addString("underData", "", 1); // 소수점 처리방식
			t52_ds.addString("receiptType", "", 1); // 계산서 거래 종류
			t52_ds.addString("maxLiter", "", 10); // 한도수량
			t52_ds.addString("totalLiter", "", 10); // 누적 수량
			t52_ds.addString("maxType", "", 1); // 한도기준
			t52_ds.addByte  ("ETX", ETX);
			t52_ds.addByte  ("BCC", (byte) ' ');
			t52_Buf = t52_ds.getByteStream();
			
			//--- 완료/에러 여부 확인 ---//
			t54_ds.addByte  ("STX", STX);
			t54_ds.addByte  ("SA", nozID);
			t54_ds.addByte  ("UA", SEL);
			t54_ds.addString("command", "54", 2);
			t54_ds.addString("state", "", 1);
			t54_ds.addByte  ("ETX", ETX);
			t54_ds.addByte  ("BCC", (byte) ' ');
			t54_Buf = t54_ds.getByteStream();
			
			//--- 선 결제 지시 ---//
			t55_ds.addByte  ("STX", STX);
			t55_ds.addByte  ("SA", nozID);
			t55_ds.addByte  ("UA", SEL);
			t55_ds.addString("command", "55", 2);
			t55_ds.addString("nozNo", "", 2);
			t55_ds.addString("type", "", 1);
			t55_ds.addString("liter", "", 7);
			t55_ds.addString("basePrice", "", 4);
			t55_ds.addString("price", "", 6);
			t55_ds.addString("barcode", "", 16);		// 2012.07.19 ksm 
			t55_ds.addByte  ("ETX", ETX);
			t55_ds.addByte  ("BCC", (byte) ' ');
			t55_Buf = t55_ds.getByteStream();
			
			//--- 파일, 데이터 수신응답 ---//
			t60_ds.addByte  ("STX", STX);
			t60_ds.addByte  ("SA", nozID);
			t60_ds.addByte  ("UA", SEL);
			t60_ds.addString("command", "60", 2);
			t60_ds.addString("state", "", 2);
			t60_ds.addString("rcvDataBlockCnt", "", 3);
			t60_ds.addString("fileNo", "", 2);
			t60_ds.addString("fileName", "", 11);
			t60_ds.addByte  ("ETX", ETX);
			t60_ds.addByte  ("BCC", (byte) ' ');
			t60_Buf = t60_ds.getByteStream();

			//--- 셀프 동작 모드 ---//
			t61_ds.addByte  ("STX", STX);
			t61_ds.addByte  ("SA", nozID);
			t61_ds.addByte  ("UA", SEL);
			t61_ds.addString("command", "61", 2);
			t61_ds.addString("mode", "", 1);
			t61_ds.addString("fileState", "", 20);
			t61_ds.addByte  ("ETX", ETX);
			t61_ds.addByte  ("BCC", (byte) ' ');
			t61_Buf = t61_ds.getByteStream();

			//--- 입력 오더 응답 ---//
			t80_ds.addByte  ("STX", STX);
			t80_ds.addByte  ("SA", nozID);
			t80_ds.addByte  ("UA", SEL);
			t80_ds.addString("command", "80", 2);
			t80_ds.addString("orderNo", "", 2);
			t80_ds.addString("displayNo", "", 2);
			t80_ds.addString("dataType", "", 1);
			t80_ds.addString("funtion", "", 2);
			t80_ds.addString("type", "", 2);
			t80_ds.addString("nozNo", "", 7);
			t80_ds.addString("price_liter", "", 2);
			t80_ds.addByte  ("ETX", ETX);
			t80_ds.addByte  ("BCC", (byte) ' ');
			t80_Buf = t80_ds.getByteStream();
			
		} catch (Exception e) {}
		
    }
    
	protected boolean chkEnvDataAndSaving (byte[] dat) throws Exception {
		
		boolean envDataFlag=false;
		
		if (dat[3]=='0' && dat[4]=='2') {
			
			switch (dat[8]) {
			case '1' :
				t0201_Buf = dat.clone(); // 사업자 정보
				envDataFlag = true;

				LogUtility.getPumpALogger().info("환경설정정보(사업자 정보) 수신 > 저장. (Noz="+nozNo+")");
				//Log.datas(t0201_Buf, t0201_Buf.length, 20);
				
				break;
			case '2' :
				t0202_Buf = dat.clone(); // 머리말/꼬리말
				envDataFlag = true;

				LogUtility.getPumpALogger().info("환경설정정보(머리말/꼬리말) 수신 > 저장. (Noz="+nozNo+")");
				//Log.datas(t0202_Buf, t0202_Buf.length, 20);
				
				break;
			case '5' :
				t0205_Buf = dat.clone(); // 주유기 정보
				if (m_changeBasePrice==true) {
					envDataFlag = false; // 단가변경 설정을 위하여 주유기에 전송
					m_changeBasePrice = false;
				} else
					envDataFlag = true; // 주유기에 전송않음

				LogUtility.getPumpALogger().info("환경설정정보(주유기 정보) > 저장. (Noz="+nozNo+")");
				//Log.datas(t0205_Buf, t0205_Buf.length, 20);
				
				break;
			}
		}
		
		return envDataFlag;
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

	protected byte[] generateByteStream (WorkingMessage wm) throws Exception {	
		
		//if (wm.getCommand().equals("S4")) {
			//S4_WorkingMessage rwm = (S4_WorkingMessage) wm;
			//System.out.printf ("SSSSSSS------> cmd=%s noz=%s conNoz=%s\n\n", 
					//wm.getCommand(), wm.getNozzleNo(), wm.getConnectNozzleNo());
		//}
		
		return trans.generateByteStream(wm);
	}

	protected WorkingMessage generateWorkingMessage (byte[] buf, String command) throws Exception {
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

	protected String getCommand(byte[] buf) throws Exception {
		
		byte[] cmd = new byte[2];
		cmd[0] = buf[0];
		cmd[1] = buf[1];
		
		return cmd.toString();
	}
	
	protected byte[] getInitData(byte[] dat) throws Exception { // No use
		
		//--- Calculate InitData
		int uCRC = 0;
		for (int i=0; i<6; i++)
			uCRC = makeCCITT (dat[i], uCRC);

		byte byt = (byte) (((uCRC>>8) & 0x00ff) + (uCRC & 0x00ff));
		Formatter form = new Formatter();
		form.format("00%02X", byt & 0x0ff);
		String str = form.toString();
		byte initData[] = str.getBytes();

		return initData;
	}
	
	protected void insertEnvDataToTxQue (byte[] envDat, String fileNo) throws Exception {
		
		t02sb_ds.editString("fileNo", fileNo, 2); // Start block
		t02sb_Buf = t02sb_ds.getByteStream();

		//LogUtility.getPumpALogger().debug("##### insert EnvData Start block : fileNo=" + fileNo + "(Noz="+nozNo+")");
		//Log.datas (t02sb_Buf, t02sb_Buf.length, 20);
		
		t02eb_ds.editString("fileNo", fileNo, 2); // End block
		t02eb_Buf = t02eb_ds.getByteStream();
		
		//LogUtility.getPumpALogger().debug("##### insert EnvData Data block" + "(Noz="+nozNo+")");
		//Log.datas (envDat, envDat.length, 20);

		//LogUtility.getPumpALogger().debug("##### insert EnvData End block" + "(Noz="+nozNo+")");
		//Log.datas (t02eb_Buf, t02eb_Buf.length, 20);
		
		TxQue.enQueue(t02sb_Buf); // Start block
		TxQue.enQueue(envDat); 	  // Data block
		TxQue.enQueue(t02eb_Buf); // End block

		//LogUtility.getPumpALogger().debug("##### insert EnvData End block : itemCount=" + TxQue.getItemCount()+ "(Noz="+nozNo+")");
	}
	
	protected boolean isSkipWorkingMessage (WorkingMessage wm) throws Exception {
		
		boolean skip=false;
		String bPrice;
				

		if (wm.getCommand().equals("HC")) { // 외상외 승인응답
			HC_wm = (HC_WorkingMessage) wm;
			
			LogUtility.getPumpALogger().info(new StringBuffer("\n소모셀프ODT 수신전문 : " ).append( HC_wm.getCommand() )
										 .append("\n ODT_No=" ).append( HC_wm.getNozzleNo() )
										 .append("\n nozzle=" ).append( HC_wm.getConnectNozzleNo() )
										 .append("\n mode  =[" ).append( HC_wm.getMode() ).append( "]\n").toString());
			
			skip = false;
		}
		else if (wm.getCommand().equals("HD")) { // 외상 승인응답
			HD_wm = (HD_WorkingMessage) wm;
			
			LogUtility.getPumpALogger().info(new StringBuffer("\n소모셀프ODT 수신전문 : " ).append( HD_wm.getCommand() )
										 .append("\n ODT_No=" ).append( HD_wm.getNozzleNo() )
										 .append("\n nozzle=" ).append( HD_wm.getConnectNozzleNo() ).append( "\n").toString());
			
			skip = false;
		}
		else if (wm.getCommand().equals("S4")) { // 주유완료

			S4_wm = (S4_WorkingMessage) wm;

			m_realPrice     = S4_wm.getPrice();
			m_realLiter     = S4_wm.getLiter();
			m_realBasePrice = S4_wm.getBasePrice();

			LogUtility.getPumpALogger().info(new StringBuffer( "\n소모셀프ODT 수신전문 : " ).append( S4_wm.getCommand() )
										 .append("\n ODT_No=" ).append( S4_wm.getNozzleNo() )
										 .append("\n nozzle=" ).append( S4_wm.getConnectNozzleNo() ) 
										 .append("\n liter =" ).append( S4_wm.getLiter() )
										 .append("\n bPrice=" ).append( S4_wm.getBasePrice() )
										 .append("\n price =" ).append( S4_wm.getPrice() ).append( "\n").toString());
			
			skip = false;
		}
		else if (wm.getCommand().equals("P7")) { // 주유기 파라미터 설정

			P7_WorkingMessage P7_wm = (P7_WorkingMessage) wm;
			readBuffInterval   = baseReadBuffInterval + Change.toValue(P7_wm.getReadBuffInterval());
			readStartInterval  = baseReadStartInterval + Change.toValue(P7_wm.getReadStartInterval());
			writeStartInterval = baseWriteStartInterval + Change.toValue(P7_wm.getWriteStartInterval());
			minErrCnt 		   = baseMinErrCnt + Change.toValue(P7_wm.getLineErrorCount());
			
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
		else if (wm.getCommand().equals("P5_1")) { // 환경설정정보(노즐정보)
			P5_1_WorkingMessage P5_wm = (P5_1_WorkingMessage) wm;
			//P5_wm.print();
			
			if (P5_wm.getMode().equals("0")) // 초기화
				m_changeBasePrice=false;
			else
				m_changeBasePrice=true; // 단가변경

			LogUtility.getPumpALogger().info(new StringBuffer("\n소모셀프ODT 수신전문 : " ).append( P5_wm.getCommand() )
					 .append("\n ODT_No=" ).append( P5_wm.getNozzleNo() )
					 .append("\n mode  =" ).append( P5_wm.getMode() ).append( "\n").toString());
		}
		/*
		else if (wm.getCommand().equals("P1")) { // 환경설정정보(사업장정보)
			P1_WorkingMessage P1_wm = (P1_WorkingMessage) wm;
			P1_wm.print();
		}
		else if (wm.getCommand().equals("P2")) { // 환경설정정보(머리말/꼬리말 정보)
			P2_WorkingMessage P2_wm = (P2_WorkingMessage) wm;
			P2_wm.print();
		}*/
		
		return skip;
	}
	
	protected int makeCCITT (int wData, int wCRC) throws Exception { // No use
		wData <<= 1;
		for (int i = 8; i > 0 ;i--)
		{
			wData >>= 1;			
			if (((wData ^ wCRC) & 0x0001) != 0)
				wCRC = (wCRC>>1) ^ 0x8408;
			else
				wCRC >>= 1;
		}

		return wCRC;
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
	
	protected void ProcessDeviceError () throws Exception {

	}
	
	protected void processODT () throws SerialConnectException, Exception {

		try {
		
			switch (RxBuf[3])
			{
			case '8':
				switch (RxBuf[4])
				{
				case '0': //--- "80" = 입력오더응답
					insertRecvQueue(generateWorkingMessage(RxBuf, null));
					//System.out.printf ("insertRecvQueue =========\"%c%c\"\n", RxBuf[3], RxBuf[4]);
					break;
	
				case '1': //--- "81" = 디바이스 에러 통지
					if (RxBuf[6]=='1') { // 발생
						insertRecvQueue(generateWorkingMessage(RxBuf, null));
						//System.out.printf ("insertRecvQueue =========\"%c%c\"\n", RxBuf[3], RxBuf[4]);
					}
					else if (RxBuf[6]=='0') { // 회복
						insertRecvQueue(generateWorkingMessage(RxBuf, null));
					}
					break;
				}
	
				break;
			case '9':
				switch (RxBuf[4])
				{
				case '0': //--- "90" = 정액/정량 마감요청(정수정지)
					insertRecvQueue(generateWorkingMessage(RxBuf, null));
					//System.out.printf ("insertRecvQueue =========\"%c%c\"\n", RxBuf[3], RxBuf[4]);
					break;
	
				case '1': //--- "91" = 셀프상태 통지
					makeStatusInfo(650);
					insertRecvQueue(generateWorkingMessage(RxBuf, null));
					//System.out.printf ("insertRecvQueue =========\"%c%c\"\n", RxBuf[3], RxBuf[4]);
					break;
	
				case '2': //--- "92" = 승인완료(주유허가) 또는 판매완료
					WorkingMessage wm = generateWorkingMessage(RxBuf, null);
					insertRecvQueue(wm);
					//LogUtility.getPumpALogger().debug("ODT 수신데이터 : ===== 92");
					//LogUtility.getPumpALogger().debug("command=" + wm.getCommand() + " nozzle=" + wm.getNozzleNo() + 
							//" targetNoz=" + wm.getTargetNozzleNo());
					//wm.print(" "); //wm.print(" "); 박종호 변경
					break;
	
				case '3': //--- "93" = 주유기 비상정지/해제
					insertRecvQueue(generateWorkingMessage(RxBuf, null));
					//System.out.printf ("insertRecvQueue =========\"%c%c\"\n", RxBuf[3], RxBuf[4]);
					break;
	
				case '4': //--- "94" = 최종 주유값 요청
					insertRecvQueue(generateWorkingMessage(RxBuf, null));
					//System.out.printf ("insertRecvQueue =========\"%c%c\"\n", RxBuf[3], RxBuf[4]);
					break;
	
				case '5': //--- "95" = 주유기 오류상태 요청
					insertRecvQueue(generateWorkingMessage(RxBuf, null));
					//System.out.printf ("insertRecvQueue =========\"%c%c\"\n", RxBuf[3], RxBuf[4]);
					break;
	
				default:
					break;
				}
				
				break;
				
			default:	
				break;
			}
		
		} catch (IOException e) {
			LogUtility.getPumpALogger().error("Exception occurr! (Noz="+nozNo+")");
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	}

	protected void ProcessOKResult () throws Exception {

	}

	protected void processRecvSTX () throws SerialConnectException, Exception {
		
		byte[] byt = new byte[2];
		byt[0] = RxBuf[3];
		byt[1] = RxBuf[4];
		//LogUtility.getPumpALogger().debug("\n...Recv data from ODT >>>>>>> " + new String(byt));
		//Log.datas(RxBuf, 120, 20);
		
		if (RxBuf[3]=='6' && RxBuf[4]=='1') {
			
			switch (RxBuf[6]) // 동작모드 ('1'=Initial mode, '2'=POS mode)
			{
			case '1': // Initial mode (환경설정)
				switch (RxBuf[5]) // 파일No.
				{
				case '1':
					insertEnvDataToTxQue (t0201_Buf, "01"); // 사업자정보(P1)
					break;
				case '2':
					insertEnvDataToTxQue (t0202_Buf, "02"); // 머리말/꼬리말(P2)
					break;
				case '6':
					setCurrentTime(); // 현재시각을 설정
					insertEnvDataToTxQue (t0206_Buf, "06"); // 시스템시각(P6)
					//LogUtility.getPumpALogger().debug("\n시각요청==============>>>>>");
					//Log.datas(t0206_Buf, t0206_Buf.length, 20);
					break;
				case '5':
					insertEnvDataToTxQue (t0205_Buf, "05"); // 노즐정보(P5)
					break;
				default:
					break;
				}

				break;
			case '2': // POS mode
				
				break;
			}
		}

		switch (RxBuf[3]) // Command
		{
		case '8':
		case '9':
			processODT ();
			break;
		}
	}

	protected boolean recvTail_proc (byte[] ACK_NAK) throws Exception, SerialConnectException {
		
		//--- send : ACK ---//
		if (sendText (ACK_NAK)==false) {
			if (dispLevel>=3)
				LogUtility.getPumpALogger().info("S5.Send ACK1 fail! (ODT="+nozNo+")");
			return false;
		}
		
		if (dispLevel>=3)
			LogUtility.getPumpALogger().info("S5.Send ACK1 (ODT="+nozNo+")");
		
		//--- recv : EOT ---//
		if (recvText(RxBuf) < 1) {
			if (dispLevel>=3)
				LogUtility.getPumpALogger().info("S5.Recv EOT fail (ODT="+nozNo+")");
			return false;
		}
	
		if (RxBuf[0] == EOT) {	//--- recv : EOT ---//
			if (dispLevel>=3)
				LogUtility.getPumpALogger().info("S5.Recv EOT (ODT="+nozNo+")");
			return true;
		}
		else {
			if (dispLevel>=3)
				LogUtility.getPumpALogger().info("S5.Recv EOT fail (ODT="+nozNo+")");
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

	@Override
	public void requestData() throws Exception, SerialConnectException {

		int 	numBytes=0, loopCnt=0;
		boolean	occurLineErr = (lineErrCnt >= minErrCnt);
			
		if (occurLineErr) { // Check line communication error

			if (issueLineErr==true) {
				makeLineError();
				TxQue.flushQueue();
				//LogUtility.getPumpALogger().debug("Flush buffer(TxQue) lineErrCnt="+lineErrCnt+ " (ODT="+nozNo+")");
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
			if (m_statusCode!=601)
				makeStatusInfo(650);
		}
		if (m_statusCode==601) { // 회선불량
			if (lineCommCheckCnt%(minErrCnt+5)==0) {
				lineCommCheckCnt=0;
				if (m_statusCode!=601)
					makeStatusInfo(650);
			} else
				lineCommCheckCnt++;
		}
		
		flushBuffer(RxBuf);
		
		//--- Transfer from WorkingMessage to BytesStream
		while (sndQue.getItemCount() > 0) {
			WorkingMessage wm = (WorkingMessage)sndQue.deQueue();
			
			LogUtility.getPumpALogger().debug("\n>>>>> Received Down data : " +
								"ODT=" + nozNo + " command=" + wm.getCommand());

			if (isSkipWorkingMessage(wm)==true) {
				wm=null;
				continue;
			}
			
			byte[] wmByte = generateByteStream(wm);
			
			if (wmByte != null) {
				if (chkEnvDataAndSaving(wmByte) == false) // If EnvData then saving to dataStruct
					TxQue.enQueue(wmByte); // 환경설정보가 아니면
			}
			/*
			if (wm.getCommand().equals("PB")) {
				LogUtility.getPumpALogger().debug(":::::::::>>> ByteStrems of workingMessage ["+ wm.getCommand() + "] in requestData().");
				Log.datas(wmByte, wmByte.length, 20);
			}
			*/
			wm=null;
		}
		if (dispLevel>=3)
			LogUtility.getPumpALogger().debug("\nStart request ===========> (ODT="+nozNo+")");

		mode = POL;
		while (true) {

			flushBuffer(RxBuf);
			
			if (loopCnt >= 4) break;
			loopCnt++;
			
			try {
				//##### Send ENQ #####//
				mode = (mode==POL? SEL : POL);
				enq_Buf[2] = mode;
				
				if (mode==SEL) {
					if (progressStep != 1 && TxQue.isEmpty()==true) // 송신자료 없으면
						continue;
				}
				else { // mode=POL
					if (progressStep != 1 && TxQue.isEmpty()==false) // 송신자료 있으면
						continue;
				}

				if (dispLevel>=3) {
					if (mode==POL)
						LogUtility.getPumpALogger().debug("----POLLING MODE---- (progStep=" + progressStep + " , nozState=" + nozState + ")");
					else
						LogUtility.getPumpALogger().debug("---SELECTING MODE--- (progStep=" + progressStep + " , nozState=" + nozState + ")");
				}
				
				//--- Send ENQ ---//
				if(sendText (enq_Buf) != true) {
					lineErrCnt++;
					continue;
				}
				
				if (dispLevel>=3) {
					LogUtility.getPumpALogger().info("0.Send ENQ (ODT="+nozNo+")");
					Log.datas(enq_Buf, 4, 20);
				}

				//--- recv data---//	
				if (recvText(RxBuf) < 1) {
					if (dispLevel>=3)
						LogUtility.getPumpALogger().info("0.Recv DAT fail! (ODT="+nozNo+")");
					lineErrCnt++;
					continue;
				}

				//###### Recv data(Polling mode) : STX ######//
				if (mode==POL) { // 0x51

					if (RxBuf[0] == STX) {

						if (dispLevel>=3) {
							LogUtility.getPumpALogger().info("1.Recv STX(Data) (ODT="+nozNo+")");
							Log.datas(RxBuf, 80, 20);
						}
						
						if (compareNozID(RxBuf)==false) { 
							is.read(); // flush inputStream
							LogUtility.getPumpALogger().info ("1.Recv STX NozID mismatch-1.0! (ODT="+nozNo+")");
							Log.datas(RxBuf, 40, 20);
						}
						if (compareBCC(RxBuf)==false) {
							sendText (NAK);
							LogUtility.getPumpALogger().info ("1.Recv STX BCC fail! (ODT="+nozNo+")");
							Log.datas (RxBuf, 40, 20);
							continue;
						}
						else {
							if (dispLevel==1 || dispLevel==2) { 
								LogUtility.getPumpALogger().info ("1.Recv STX with normal (ODT="+nozNo+")");
								Log.datas(RxBuf, 80, 20);
							}
							
							LogUtility.getPumpALogger().debug("Recv STX("+Change.toString("%02d", nozNo)+
									") : ["+new String(RxBuf)+"]");
							
							processRecvSTX(); // 수신 데이터 처리
							
							if (recvTail_proc(ACK1)==false) 
								lineErrCnt++;
							else {
								lineErrCnt=0; // Normal terminated
								break;
							}
						}
					}
					else if (RxBuf[0] == EOT) {
						if (dispLevel>=3)
							LogUtility.getPumpALogger().info("1.Recv EOT (ODT="+nozNo+")");
						lineErrCnt=0; // Normal terminated
						//continue;
						break;
					} else {
						is.read(); // flush inputStream
						
						if (dispLevel>=3)
							LogUtility.getPumpALogger().info("1.Recv fail! (ODT="+nozNo+")");
						lineErrCnt++;
						continue;
					}
				}
				//###### Send data(Selecting mode) ######//
				else if (mode==SEL) { // 0x41

					if (RxBuf[0]==ACK) {
						
						is.read (RxBuf,0,1);
						if (RxBuf[0]==0x30) { // recv : DLE0

							if (dispLevel>=3)
								LogUtility.getPumpALogger().info("2.Recv ACK0 (ODT="+nozNo+")");

							//LogUtility.getPumpALogger().debug("2.Send STX before (ODT="+nozNo+") itemCount="+TxQue.getItemCount());
							if (TxQue.isEmpty()==false) { // 송신 데이터 있으면

								//###### Send data : send working-data ######//
								TxBuf = TxQue.getFirstItem();
								TxBuf[1] = nozID;
								TxBuf[2] = mode;
								setBCC (TxBuf); // write BCC
								if(sendText(TxBuf) != true) {
									if (dispLevel>=3) {
										LogUtility.getPumpALogger().info("2.Send STX fail! (ODT="+nozNo+")");
										Log.datas(TxBuf, TxBuf.length, 20);
									}
									lineErrCnt++;
									continue;
								}

								if (dispLevel>=3) {
									LogUtility.getPumpALogger().info("2.Send STX(TxBuf) (ODT="+nozNo+")");
									Log.datas(TxBuf, TxBuf.length, 20);
								}
								
								if (TxBuf.length > 200) { // 추가(08/12/08) for SomoSelf
									//Sleep.sleep(TxBuf.length + 500);
									Sleep.sleep(TxBuf.length);
									LogUtility.getPumpALogger().info("추가지연 발생. TxBuf.length=" +(TxBuf.length)+ "(ODT="+nozNo+")");
								}
																
								if (recvText(RxBuf) < 1) {
									if (dispLevel>=3)
										LogUtility.getPumpALogger().info("2.Recv ACK1 fail! (ODT="+nozNo+")");
									lineErrCnt++;
									continue;
								}

								if (RxBuf[0]==ACK) {
									
									is.read (RxBuf,0,1);
									if (RxBuf[0]==0x31) { // recv : DLE1
										TxQue.deQueue(); // remove item
										
										if (dispLevel>=3)
											LogUtility.getPumpALogger().info("2.Recv ACK1 (ODT="+nozNo+")");
										
										if (dispLevel==2) {
											LogUtility.getPumpALogger().info("2.Send STX with normal (ODT="+nozNo+") itemCount="+TxQue.getItemCount());
											Log.datas(TxBuf, TxBuf.length, 20);
										}

										byte[] bbb = new byte[2]; // Temp...
										bbb[0]=TxBuf[3];
										bbb[1]=TxBuf[4];
										LogUtility.getPumpALogger().info("<<<<Sent command "+new String(bbb)+". itemCount=" + 
												TxQue.getItemCount() + " (ODT="+nozNo+")");
										
										sendTail_proc();
										lineErrCnt=0; // Normal terminated
										break;
									}
									else { // recv mismatched data
										is.read(); // flush inputStream
									}
								}
								else if (dispLevel>=3) {
									LogUtility.getPumpALogger().info("2.Recv ACK1 fail! (ODT="+nozNo+")");
									lineErrCnt++;
								}
							}
						}
						else { // recv mismatched data
							is.read(); // flush inputStream
						}
						
					}
					else if (RxBuf[0] == EOT) {
						if (dispLevel>=3) {
							LogUtility.getPumpALogger().info("2.Recv EOT (ODT="+nozNo+")");
						}
						lineErrCnt=0; // Normal terminated
						//continue;
						break;
					} else {
						if (dispLevel>=3)
							LogUtility.getPumpALogger().info("2.Recv fail! (ODT="+nozNo+")");
						lineErrCnt++;

						sendTail_proc(); // $$$$$$
						continue;
					}
				} 				
				
			} catch (Exception e) {
				LogUtility.getPumpALogger().error("Exception occurr! (ODT="+nozNo+")");
				lineErrCnt++;
				LogUtility.getPumpALogger().error(e.getMessage(), e);
			}
		}
	}
	
	public void run() {

	}
	
	protected boolean sendTail_proc () throws Exception, SerialConnectException {

		return true;
	}

	protected boolean sendText(byte buf) throws Exception, SerialConnectException {

		Sleep.sleep(writeStartInterval);
		
	    try {
			os.write(buf);
		} catch (IOException e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	
		return true;
	}
	
	protected boolean sendText(byte[] buf) throws Exception, SerialConnectException {

		Sleep.sleep(writeStartInterval);
		
	    try {
			os.write(buf);
		} catch (IOException e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	
		return true;
	}
	
	protected boolean sendText(String txt) throws Exception, SerialConnectException {

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

	protected void setCurrentTime () throws Exception, SerialConnectException {
		
		Calendar cal = new GregorianCalendar();
		Formatter form = new Formatter();
		form.format("%04d%02d%02d%02d%02d%02d", cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH), 
				cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), 
				cal.get(Calendar.SECOND));
		String timeStr = form.toString();
		
		t0206_ds.editString("systemTime", timeStr, 14);
		t0206_Buf = t0206_ds.getByteStream();
	}
}


