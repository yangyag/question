package com.gsc.kixxhub.device.pumpa.driver;

import gnu.io.CommPortOwnershipListener;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.util.Calendar;
import java.util.Formatter;
import java.util.GregorianCalendar;

import com.gsc.kixxhub.common.data.pump.BI_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HC_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HD_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P2_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P5_1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P7_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S3_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S4_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S8_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SE_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SJ_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.ST_WorkingMessage;
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
import com.gsc.kixxhub.device.pumpa.translation.TransPrimeSelf;

public class CommPrimeSelf extends CCommDriver implements SerialPortEventListener, CommPortOwnershipListener, 
	Runnable { 

	protected static boolean m_isSaveSTX=false;
	protected byte  	ACK  = 0x10;
	protected byte[] 	ACK0 = new byte[3];
	protected byte[] 	ACK1 = new byte[3];
	protected int		baseMinErrCnt=8, baseMaxSkipCnt=100;
	protected int		baseReadBuffInterval   = 100+10; // 스마트주유소 PJT 보정
	protected int		baseReadStartInterval  = 10+3; // 스마트주유소 PJT 보정
	protected int		baseWriteStartInterval = 10+3; // 스마트주유소 PJT 보정
	protected BI_WorkingMessage BI_wm;
	protected int		buffSize = 1024;
	protected int 	dispLevel=0;
	protected byte  	ENQ  = 0x05;
	
    protected byte[] 	ENQb = new byte[4];
    protected byte  	EOT  = 0x04;
    protected byte[]  	EOTb = {EOT, 0};
    protected byte  	SEL  = 0x41;
    protected byte  	STX  = 0x02;
	protected byte  	ETX  = 0x03;
	protected boolean firstRequest=true;
	protected HC_WorkingMessage HC_wm;
	protected HD_WorkingMessage HD_wm;
	
	protected boolean	issueLineErr=true;
	protected byte[] 	lastPumpingData=new byte[buffSize];
    protected int		lineCommCheckCnt=0;
	protected int		lineErrCnt=0;
	protected String	m_basePrice="000000";
	protected boolean	m_changeBasePrice=false;
	protected boolean	m_isPOSMode=false;
	protected String 	m_realBasePrice="000000"; // 실 주유단가
	protected String 	m_realLiter="0000000";    // 실 주유량
	protected String 	m_realPrice="00000000";   // 실 주유금액
	protected boolean	m_setEnvDataOK=false;
	protected int		m_statusCode=601;
	protected boolean	m_useFullPumping=false;
	protected byte  	mode = SEL;
	protected byte  	NAK  = 0x15;
	protected byte[]  	NAKb = {NAK, 0};
	protected byte		nozID;

	protected short		nozState=0;
	protected String 	nozStr = "";
	protected PB_WorkingMessage PB_wm;

    protected byte  	POL  = 0x51;
    
    protected byte  	PREFIX_ENQ = 0x05;
    //progressStep : 0=초기화전, 1=초기화후, 2=상태정보송신후, 3=주유중, 4=주유완료, 
    //               5=토털게이지수신/주유완료자료 송신
    protected int 	progressStep=3;
    protected byte[] 	rcvInitBuf = new byte[6];
    protected byte[] 	RxBuf = new byte[buffSize];
    protected S3_WorkingMessage S3_wm;
    protected S4_WorkingMessage S4_wm;
    protected S8_WorkingMessage S8_wm;
    protected SE_WorkingMessage SE_wm;

    protected SJ_WorkingMessage SJ_wm;
    protected ST_WorkingMessage ST_wm;


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
    protected byte[] 	t22_Buf;
    protected DataStruct t22_ds = new DataStruct();
    protected byte[] 	t40_Buf;
    protected DataStruct t40_ds = new DataStruct();
    protected byte[] 	t50_Buf;
    protected DataStruct t50_ds = new DataStruct();
    protected byte[] 	t51_Buf;
    protected DataStruct t51_ds = new DataStruct();
    protected byte[] 	t52_Buf;
    protected DataStruct t52_ds = new DataStruct();

    protected byte[] 	t54_Buf;
    protected DataStruct t54_ds = new DataStruct();
    protected byte[] 	t55_Buf;
    protected DataStruct t55_ds = new DataStruct();
    protected byte[] 	t59_Buf;
    protected DataStruct t59_ds = new DataStruct();
    protected byte[] 	t60_Buf;
    protected DataStruct t60_ds = new DataStruct();
    protected byte[] 	t61_Buf;
    protected DataStruct t61_ds = new DataStruct();
    protected byte[] 	t63_Buf;
    protected DataStruct t63_ds = new DataStruct();
    protected byte[] 	t80_Buf;
    protected DataStruct t80_ds = new DataStruct();
    protected TR_WorkingMessage TR_wm;
    protected TransPrimeSelf trans = new TransPrimeSelf();
    protected byte[] 	TxBuf = new byte[buffSize];
    protected BytesQue2 TxQue = new BytesQue2(30);
	
    public CommPrimeSelf (int nozNum, String romVerStr) {
    	
    	byte	bcc;
		Formatter form = new Formatter ();
		byte[] 	nozByt = new byte[2];
		
		nozNo = nozNum;
		romVer = romVerStr;
		
		form.format("%02d", nozNo);
		nozStr = form.toString();
		nozByt = nozStr.getBytes();
		nozID = (byte) (nozNum - 1 + 0x40);

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
	    ENQb[2] = SEL;
	    ENQb[3] = ENQ;
	    
		//--- prefix NAK-buffer---//
	    NAKb[1] = nozID;

		//--- prefix EOT-buffer---//
	    EOTb[1] = nozID;

		readBuffInterval   = baseReadBuffInterval;
		readStartInterval  = baseReadStartInterval;
		writeStartInterval = baseWriteStartInterval;
		minErrCnt 		   = baseMinErrCnt;
		maxSkipCnt 		   = baseMaxSkipCnt;
		
		try {

			//--- 상태정보요청 ---//
			t40_ds.addByte  ("STX", STX);
			t40_ds.addByte  ("SA", nozID);
			t40_ds.addByte  ("UA", SEL);
			t40_ds.addString("command", "40", 2);
			t40_ds.addByte  ("ETX", ETX);
			t40_ds.addByte  ("BCC", (byte) ' ');
			t40_Buf = t40_ds.getByteStream();
			
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
					
			//--- 주유기 상태 ---//
			t50_ds.addByte  ("STX", STX);
			t50_ds.addByte  ("SA", nozID);
			t50_ds.addByte  ("UA", SEL);
			t50_ds.addString("command", "50", 2);
			t50_ds.addString("nozNo", "", 2);
			t50_ds.addString("nozState", "", 1);
			t22_ds.addByte  ("ETX", ETX);
			t22_ds.addByte  ("BCC", (byte) ' ');
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

			//--- 가득주유 통제전문 ---//
			t59_ds.addByte  ("STX", STX);
			t59_ds.addByte  ("SA", nozID);
			t59_ds.addByte  ("UA", SEL);
			t59_ds.addString("command", "59", 2);
			t59_ds.addString("sequence", "1", 1);
			t59_ds.addString("reAuthType", "9", 1); // 1:승인-승인-취소(구현안됨), 2: 승인-취소-승인, 9: 변경안함
			t59_ds.addString("useFullPumping", "9", 1); // 0: 차단, 1: 허용, 9: 변경안함
			t59_ds.addString("fullPumpingAuthType", "9", 1); // 1:B/L체크방식, 2:선승인방식, 9: 변경안함
			t59_ds.addString("preAuthPrice", "99999999", 8); // 가득주유 선승인 금액 -> 99999999: 변경안함
			t59_ds.addByte  ("ETX", ETX);
			t59_ds.addByte  ("BCC", (byte) ' ');
			t59_Buf = t59_ds.getByteStream();

			//--- 응답전문 ---//
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
				//envDataFlag = true;
				envDataFlag = false;

				LogUtility.getPumpALogger().info("환경설정정보(사업자 정보) 수신 > Buf 저장. (Noz="+nozNo+")");
				LogUtility.getPumpALogger().info("P1=" + new String(t0201_Buf));
				//Log.datas(t0201_Buf, t0201_Buf.length, 20);
				
				break;
			case '2' :
				t0202_Buf = dat.clone(); // 머리말/꼬리말
				//envDataFlag = true;
				envDataFlag = false;
				
				LogUtility.getPumpALogger().info("환경설정정보(머리말/꼬리말) 수신 > Buf 저장. (Noz="+nozNo+")");
				LogUtility.getPumpALogger().info("P2=" + new String(t0202_Buf));
				//Log.datas(t0202_Buf, t0202_Buf.length, 20);
				
				break;
			case '5' :
				t0205_Buf = dat.clone(); // 주유기 정보
				if (m_changeBasePrice==true) {
					envDataFlag = false; // 단가변경 설정을 위하여 주유기에 전송
					m_changeBasePrice = false;
				} else
					envDataFlag = true; // 주유기에 전송않음

				LogUtility.getPumpALogger().info("환경설정정보(주유기 정보) 수신 > Buf 저장. (Noz="+nozNo+")");
				LogUtility.getPumpALogger().info("P5=" + new String(t0205_Buf));
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
		
		t02eb_ds.editString("fileNo", fileNo, 2); // End block
		t02eb_Buf = t02eb_ds.getByteStream();
				
		TxQue.enQueue(t02sb_Buf); // Start block
		TxQue.enQueue(envDat); 	  // Data block
		TxQue.enQueue(t02eb_Buf); // End block
		
		//LogUtility.getPumpALogger().debug(">>>>> Insert 환경설정정보. fileNo="+ fileNo + " (Noz="+nozNo+")");
		//Log.datas(envDat, envDat.length, 20);
	}
	
	protected boolean isSkipWorkingMessage (WorkingMessage wm) throws Exception {
		
		boolean skip=false;
		String modeMsg = "";
				
		if (wm.getCommand().equals("P1")) { // 셀프 환경설정(사업장 정보)
			
			P1_WorkingMessage P1_wm = (P1_WorkingMessage) wm;
//			LogUtility.getPumpALogger().info("333_SLF>>> P1 수신 nozzle=" + nozStr);
			P1_wm.print();
			
			skip=false;
		}
		else if (wm.getCommand().equals("P2")) { // 셀프 환경설정(머리말/꼬리말)
			
			P2_WorkingMessage P2_wm = (P2_WorkingMessage) wm;
//			LogUtility.getPumpALogger().info("333_SLF>>> P2 수신 nozzle=" + nozStr);
			P2_wm.print();
			
			skip=false;
		}
		else if (wm.getCommand().equals("P5_1")) { // 셀프 환경설정(노즐정보)
			
			P5_1_WorkingMessage P5_1_wm = (P5_1_WorkingMessage) wm;
			skip=false;
			
			LogUtility.getPumpALogger().info("[Pump A][수신-동화셀프ODT 노즐/단가 정보]" + " nozzle=" + 
					P5_1_wm.getNozzleNo() + "("+P5_1_wm.getCommand()+")" +
					" | mode=" + P5_1_wm.getMode() + 
					" | useFullPumping=" + P5_1_wm.getUseFullPumping());
			
			if (P5_1_wm.getMode().equals("0")) {// 초기화
				m_setEnvDataOK=true;
				m_changeBasePrice=false;
				//m_useFullPumping = P5_1_wm.getUseFullPumping().equals("1")? true : false;
			} 
			else if (P5_1_wm.getMode().equals("1")) { // 단가변경
				m_changeBasePrice=true; 
				
				LogUtility.getPumpALogger().info("단가변경용 P5수신, 단가변경. ODT_No="+P5_1_wm.getNozzleNo());
			}
			else if (P5_1_wm.getMode().equals("2")) { // 가득주유 옵션변경
				//m_useFullPumping = P5_1_wm.getUseFullPumping().equals("1")? true : false;
				
				t59_ds.editString("useFullPumping", P5_1_wm.getUseFullPumping(), 1);
				TxQue.enQueue(t59_ds.getByteStream());
				
				LogUtility.getPumpALogger().info("가득주유 옵션변경용 P5수신, 옵션변경. ODT_No="+P5_1_wm.getNozzleNo() +
						" useFullPumping=" + P5_1_wm.getUseFullPumping());
				
				Log.datas(t59_ds.getByteStream(), t59_ds.getByteStream().length, 20);
				
				/*
				// for test
				byte[] byt = new byte[19];
				byt[0] = 0x02;
				byt[1] = nozID;
				byt[2] = 'A';
				byt[3] = '5';
				byt[4] = '9';
				byt[5] = '1';
				byt[17] = 0x03;
			    BufferedReader in = new BufferedReader(new FileReader("/root/aaa.dat"));
			    String str="";
			    
			    str = in.readLine();
			    in.close();
		    	
			    byte[] byt2 = new byte[11];
			    byt2 = str.getBytes();
			    System.arraycopy(byt2, 0, byt, 6, 11);
		    	LogUtility.getPumpALogger().debug("$$$$$$$$$$$ Byt ===" + str);
		    	TxQue.enQueue(byt);

				LogUtility.getPumpALogger().info("가득주유 옵션변경용 P5수신, 옵션변경. ODT_No="+P5_1_wm.getNozzleNo() +
						" useFullPumping=" + P5_1_wm.getUseFullPumping());
				Log.datas(byt, byt.length, 20);
				*/
				
				
				skip=true;
			}

		}
		else if (wm.getCommand().equals("HC")) { // 카드 승인응답
			HC_wm = (HC_WorkingMessage) wm;
			//2014-12-01 이강호 로그 mode 로그메시지 변경
			//  1 : 승인(주유허가 발생)
			//	2 : 거부
			//	3 : 통신 실패 (타임아웃도 포함)
			//	4 : 취소 성공
			//	5 : 취소 거부
			//	6 : 취소 실패 (타임아웃도 포함)
//			LogUtility.getPumpALogger().info("[Pump A][수신-동화셀프ODT 승인응답]" + " nozzle=" + 
//					HC_wm.getNozzleNo() + "("+HC_wm.getCommand()+")" +
//					" | ODT_No=" + HC_wm.getConnectNozzleNo() +
//					" | mode=" + HC_wm.getMode() +
//					.append("\n 승인결과 ===> [" + (HC_wm.getMode().equals("1") ? "성공" : "실패") + "]\n");
			
			if(HC_wm.getMode().equals("1")  )
				modeMsg = "승인(주유허가 발생)";
			else if(HC_wm.getMode().equals("2")  )
				modeMsg = "거부";
			else if(HC_wm.getMode().equals("3")  )
				modeMsg = "통신 실패 (타임아웃도 포함)";
			else if(HC_wm.getMode().equals("4")  )
				modeMsg = "취소 성공";
			else if(HC_wm.getMode().equals("5")  )
				modeMsg = "취소 거부";
			else if(HC_wm.getMode().equals("6")  )
				modeMsg = "취소 실패 (타임아웃도 포함)";
				
			LogUtility.getPumpALogger().info(new StringBuffer("[Pump A][수신-동화셀프ODT 승인응답]" ).append(" nozzle=" ) 
					.append(HC_wm.getNozzleNo() ).append("(").append(HC_wm.getCommand()).append(")" )
					.append(" | ODT_No=" ).append( HC_wm.getConnectNozzleNo() )
					.append(" | mode=" ).append( HC_wm.getMode() )
					.append("\n 승인결과 ===> [" ).append( modeMsg ).append("]\n").toString());
			
			HC_wm.print();

			skip=false;
		}
		else if (wm.getCommand().equals("HD")) { // 외상 승인응답
			HD_wm = (HD_WorkingMessage) wm;
			
			LogUtility.getPumpALogger().info("[Pump A][수신-동화셀프ODT 승인응답]" + " nozzle=" + 
				    HD_wm.getNozzleNo() + "("+HD_wm.getCommand()+")" +
					" | ODT_No=" + HD_wm.getConnectNozzleNo() + "\n");
			
			HD_wm.print();

			skip=false;
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
			case 651:
				statusCode = 651; // 노즐다운
				errorMsg = "노즐다운";
				break;
			case 652:
				statusCode = 652; // 노즐업지
				errorMsg = "노즐업";
				break;
			case 653:
				statusCode = 653; // 주유중
				errorMsg = "주유중";
				break;
			case 654:
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
			case 231:
				statusCode = 231;
				errorMsg = "유종선택 대기";
				break;
			case 232:
				statusCode = 232;
				errorMsg = "금액선택 대기";
				break;
			case 233:
				statusCode = 233;
				errorMsg = "결제입력 대기";
				break;
			case 234:
				statusCode = 234;
				errorMsg = "보너스카드입력 대기";
				break;
			case 235:
				statusCode = 235;
				errorMsg = "주유 대기";
				break;
			case 236:
				statusCode = 236;
				errorMsg = "승인요청 중";
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
	
	protected void processODT () throws SerialConnectException, Exception {

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

			case '2': //--- "82" = 지폐투입
				BI_wm = (BI_WorkingMessage) generateWorkingMessage(RxBuf, "BI");
				insertRecvQueue(BI_wm);
				break;
			}			
		}
	}
	
	protected void processRecvSTX () throws SerialConnectException, Exception {
		
		//System.out.printf("STX Data 수신 >>>>> nozN0=%s cmd=%c%c\n", nozStr, RxBuf[3], RxBuf[4]); 
		if (m_setEnvDataOK == false)
			return;
		
		if (RxBuf[3]=='6' && RxBuf[4]=='1') {
			
			switch (RxBuf[6]) // 동작모드 ('1'=Initial mode, '2'=POS mode)
			{
			case '1': // Initial mode (환경설정)
				m_isPOSMode=false;
				//makeLineError();
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
					//LogUtility.getPumpALogger().info("\n시각요청==============>>>>>");
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
				m_isPOSMode=true;
				break;
			}
		}
		else if (RxBuf[3]=='S' && RxBuf[4]=='J') {
			
			//LogUtility.getPumpALogger().info(">>>>> 주유자료 수신");
			//Log.datas(RxBuf, 40, 20);
			
			SJ_wm = (SJ_WorkingMessage) generateWorkingMessage(RxBuf, "SJ");
			//S3_wm.setBasePrice(m_basePrice);
			
			insertRecvQueue(SJ_wm);
		}
		else if (RxBuf[3]=='S' && RxBuf[4]=='3') {
			
			//LogUtility.getPumpALogger().info(">>>>> 주유자료 수신");
			//Log.datas(RxBuf, 40, 20);
			
			S3_wm = (S3_WorkingMessage) generateWorkingMessage(RxBuf, "S3");
			//S3_wm.setBasePrice(m_basePrice);
			
			insertRecvQueue(S3_wm);
		}
		else if (RxBuf[3]=='S' && RxBuf[4]=='4') {
			
			//LogUtility.getPumpALogger().info(">>>>> 주유완료자료 수신");
			//Log.datas(RxBuf, 40, 20);
			
			S4_wm = (S4_WorkingMessage) generateWorkingMessage(RxBuf, "S4");
			//S4_wm.setBasePrice(m_basePrice);
			
			insertRecvQueue(S4_wm);
		}
		else if (RxBuf[3]=='9' && RxBuf[4]=='2') {
			
			//LogUtility.getPumpALogger().info(">>>>> TR자료 수신");
			//Log.datas(RxBuf, 40, 20);

			ST_wm = (ST_WorkingMessage) generateWorkingMessage(RxBuf, "ST");
			
			insertRecvQueue(ST_wm);
		}
		else if (RxBuf[3]=='S' && RxBuf[4]=='8') {
			
			byte[] byt = {RxBuf[11], RxBuf[12], RxBuf[13]};
			int  state = Change.toValue(new String(byt));
			
//			LogUtility.getPumpALogger().info("############ S8 수신");
//			Log.datas(RxBuf, 20, 20);
			
			makeStatusInfo(state);
		}

		switch (RxBuf[3]) // Command
		{
		case '8':
		//case '9':
			processODT(); // 승인요청 처리
			break;
		}
	}

	protected byte[] protectCardNumber (byte[] buf) throws Exception, SerialConnectException {
		
		byte[] dat = buf.clone();
		
		if (dat[3]=='8' && dat[4]=='0') { // 승인요청
			
			for (int i=13; i<13+40; i++) // 신용카드
				dat[i] = '*';

			for (int i=13+40; i<13+40+40; i++) // 보너스카드
				dat[i] = '*';
		}
		else if (dat[3]=='9' && dat[4]=='2') { // 주유허가/판매완료
			
			for (int i=12; i<12+40; i++)
				dat[i] = '*';
			
			for (int i=12+40; i<12+40+40; i++)
				dat[i] = '*';
		}
		else if (dat[3]=='5' && dat[4]=='2') {
			
			if (dat[11] != '7') { // 신용/현금 승인응답
				for (int i=90; i<90+40; i++)
					dat[i] = '*';
				
				for (int i=90+85+40; i<90+85+40+40; i++)
					dat[i] = '*';
			}
			else if (dat[11] == '7') { // 외상 승인응답
				for (int i=12; i<12+18; i++)
					dat[i] = '*';
			}
		}
		
		return dat;
	}

	protected boolean recvTail_proc () throws Exception, SerialConnectException {
		
		//--- send : ACK ---//
		if (sendText (ACK1)==false) {
			if (dispLevel>=1)
				LogUtility.getPumpALogger().info("S5.Send ACK1 fail! (Noz="+nozNo+")");
			return false;
		}
		
		if (dispLevel>=2)
			LogUtility.getPumpALogger().info("S5.Send ACK1 (Noz="+nozNo+")");
		
		//--- recv : EOT ---//
		if (recvText(RxBuf) < 1) {
			if (dispLevel>=1)
				LogUtility.getPumpALogger().info("S5.Recv EOT fail-0 (Noz="+nozNo+")");
			return false;
		}
	
		if (RxBuf[0] == EOT) {	//--- recv : EOT ---//
			if (dispLevel>=2)
				LogUtility.getPumpALogger().info("S5.Recv EOT (Noz="+nozNo+")");
			return true;
		}
		else {
			if (dispLevel>=1)
				LogUtility.getPumpALogger().info("S5.Recv EOT fail-1 (Noz="+nozNo+")");
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
			// TODO Auto-generated catch block
			LogUtility.getPumpALogger().error(e.getMessage(), e);
			return 0;
		}
	}
	
	@Override
	public void requestData() throws Exception, SerialConnectException {

		int 	loopCnt=0;
		boolean	occurLineErr = (lineErrCnt >= minErrCnt);
		byte[]  byt = new byte[1];
		
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
		//if (firstRequest==true) {
		if (firstRequest==true && m_isPOSMode==true) {
			firstRequest=false;
			TxQue.enQueueNewer(t40_Buf); // 상태정보요청
		}
		if (m_statusCode==601 && m_isPOSMode==true) { // 회선불량
			if (lineCommCheckCnt%(minErrCnt+5)==0) {
				lineCommCheckCnt=0;
				TxQue.enQueueNewer(t40_Buf); // 상태정보요청
			} else
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
			
			if (wmByte != null) {
				if (chkEnvDataAndSaving(wmByte) == false) // If EnvData then saving to dataStruct
					TxQue.enQueue(wmByte); // 환경설정보가 아니면
			}
			/*
			if (wm.getCommand().equals("HC")) {
				LogUtility.getPumpALogger().debug(":::::::::>>> ByteStrems of workingMessage ["+ wm.getCommand() + "] in requestData().");
				Log.datas(wmByte, wmByte.length, 20);
			}
			*/
			wm=null;
		}
		if (dispLevel>=3)
			LogUtility.getPumpALogger().debug("\nStart request ===========> (Noz="+nozNo+")");

		mode = POL;
		while (true) {

			flushBuffer(RxBuf);
			
			if (loopCnt >= 4) break; // 스마트주유소 PJT 수정 -> 송수신 실패시 1번 더 호출
			loopCnt++;
			
			try {
				//##### Send ENQ #####//
				mode = (mode==POL? SEL : POL);
				ENQb[2] = mode;
				
				if (mode==SEL) {
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
						LogUtility.getPumpALogger().debug("---SELECTING MODE--- (progStep=" + progressStep + " , nozState=" + nozState + ")");
				}
				
				//--- Send ENQ ---//
				if(sendText (ENQb) != true) {
					lineErrCnt++;
					continue;
				}
				
				if (dispLevel>=3) {
					LogUtility.getPumpALogger().info("0.Send ENQ (Noz="+nozNo+")");
					Log.datas(ENQb, 4, 20);
				}

				//--- recv data---//	
				if (recvText(RxBuf) < 1) {
					lineErrCnt++;
					if (dispLevel>=2)
						LogUtility.getPumpALogger().info("0.Recv ACK/STX fail!(No response) lineErrCnt="+lineErrCnt+"/"+minErrCnt+" (ODT="+nozNo+")");
					else if (lineErrCnt >= minErrCnt) {
						if (m_nNoResponseCnt%1==0) {
							m_nNoResponseCnt=0;
							LogUtility.getPumpALogger().debug("0.Recv ACK/STX fail!(No response) lineErrCnt="+lineErrCnt+"/"+minErrCnt+" (ODT="+nozNo+")");
						}
						m_nNoResponseCnt++;
					}
					continue;
				}
				
				//###### Recv data(Polling mode) : STX ######//
				if (mode==POL) { // 0x51

					if (RxBuf[0] == STX) {

						if (dispLevel>=3) {
							LogUtility.getPumpALogger().info("1.Recv STX(Data) (Noz="+nozNo+")");
							Log.datas(RxBuf, 80, 20);
						}

						if (compareNozID(RxBuf)==false) { 
							LogUtility.getPumpALogger().info ("1.Recv STX NozID mismatch-1.0! (Noz="+nozNo+")");
							Log.datas(RxBuf, 40, 20);
							lineErrCnt++;
							
							is.read(); // flush inputStream
							continue;
						}
						if (compareBCC(RxBuf)==false) {
							LogUtility.getPumpALogger().info ("1.Recv STX BCC fail! (Noz="+nozNo+")");
							sendText (NAKb);
							continue;
						}
						else {
							if (dispLevel==1 || dispLevel==2) {
								LogUtility.getPumpALogger().info ("1.Recv STX with normal (Noz="+nozNo+")");
								Log.datas(RxBuf, 80, 20);
							}
							
							if (m_isSaveSTX==true) 
								LogUtility.getPumpALogger().info("Recv STX("+Change.toString("%02d", nozNo)+
										") : ["+pack(protectCardNumber(RxBuf), ETX)+"]");
							else
								LogUtility.getPumpALogger().debug("Recv STX("+Change.toString("%02d", nozNo)+
										") : ["+pack(protectCardNumber(RxBuf), ETX)+"]");
							
							processRecvSTX(); // 수신 노즐 데이터 처리
							
							if (recvTail_proc()==false) 
								lineErrCnt++;
							else {
								lineErrCnt=0; // Normal terminated
								break; // 스마트주유소 PJT 추가
							}
						}
					}
					else if (RxBuf[0] == EOT) {
						
						if (compareNozID(RxBuf)==false) { 
							LogUtility.getPumpALogger().info ("1.Recv EOT NozID mismatch-1.0! (Noz="+nozNo+")");
							Log.datas(RxBuf, 40, 20);
							lineErrCnt++;
							
							is.read(); // flush inputStream
							continue;
						}
						
						if (dispLevel>=3)
							LogUtility.getPumpALogger().info("1.Recv EOT (Noz="+nozNo+")");
						lineErrCnt=0; // Normal terminated
						//continue;
						break; // 스마트주유소 PJT 수정
					} else {
						if (dispLevel>=3)
							LogUtility.getPumpALogger().info("1.Recv fail! (Noz="+nozNo+")");
						lineErrCnt++;
						continue;
					}
				}
				//###### Send data(Selecting mode) ######//
				else if (mode==SEL) { // 0x41

					if (RxBuf[0]==ACK) {
						
						if (compareNozID(RxBuf)==false) { 
							LogUtility.getPumpALogger().info ("2.Recv ACK NozID mismatch-1.0! (Noz="+nozNo+")");
							Log.datas(RxBuf, 40, 20);
							lineErrCnt++;
							
							is.read(); // flush inputStream
							continue;
						}
						
						is.read (byt,0,1);
						if (byt[0]==0x30) { // recv : ACK0

							if (dispLevel>=3)
								LogUtility.getPumpALogger().info("2.Recv ACK0 (Noz="+nozNo+")");
		
							if (TxQue.isEmpty()==false) { // 송신 데이터 있으면
	
								//###### Send data : send working-data ######//
								TxBuf = TxQue.getFirstItem();
								TxBuf[1] = nozID;
								TxBuf[2] = mode;
								setBCC (TxBuf); // write BCC
								if(sendText(TxBuf) != true) {
									if (dispLevel>=3) {
										LogUtility.getPumpALogger().info("2.Send STX fail! (Noz="+nozNo+")");
										Log.datas(TxBuf, TxBuf.length, 20);
									}
									lineErrCnt++;
									continue;
								}
																
								if (dispLevel>=3) {
									LogUtility.getPumpALogger().info("2.Send STX(TxBuf) (Noz="+nozNo+")");
									Log.datas(TxBuf, TxBuf.length, 20);
								}
								
								if (TxBuf.length > 500) { // 추가(09/01/07)
									Sleep.sleep(TxBuf.length + 100);
									LogUtility.getPumpALogger().info("추가지연 발생. TxBuf.length=" + TxBuf.length + "(ODT="+nozNo+")");
								}						
				
								if (recvText(RxBuf) < 1) {
									if (dispLevel>=3)
										LogUtility.getPumpALogger().info("2.Recv ACK1 fail! (Noz="+nozNo+")");
									lineErrCnt++;
									continue;
								}
								
								if (RxBuf[0]==ACK) {
									
									if (compareNozID(RxBuf)==false) { 
										LogUtility.getPumpALogger().info ("2.Recv ACK1 NozID mismatch! (Noz="+nozNo+")");
										Log.datas(RxBuf, 40, 20);
										lineErrCnt++;
										
										is.read(); // flush inputStream
										continue;
									}
									
									is.read (byt,0,1);
									if (byt[0]==0x31) { // recv : ACK1

										if (m_isSaveSTX==true) 
											LogUtility.getPumpALogger().info("Send STX("+Change.toString("%02d", nozNo)+
													") : ["+new String(protectCardNumber(TxBuf))+"]");
										else
											LogUtility.getPumpALogger().debug("Send STX("+Change.toString("%02d", nozNo)+
													") : ["+new String(protectCardNumber(TxBuf))+"]");
										
										TxQue.deQueue(); // remove item
										
										if (dispLevel>=3)
											LogUtility.getPumpALogger().info("2.Recv ACK1 (Noz="+nozNo+")");
										
										if (dispLevel==2) {
											LogUtility.getPumpALogger().info("2.Send STX with normal (Noz="+nozNo+")");
											Log.datas(TxBuf, TxBuf.length, 20);
										}
										lineErrCnt=0; // Normal terminated
										break; // 스마트주유소 PJT 수정
									}
								}
								else if (dispLevel>=3) {
									LogUtility.getPumpALogger().info("2.Recv ACK1 fail! (Noz="+nozNo+")");
									lineErrCnt++;
								}
							}
						}
						else { // recv mismatched data
							LogUtility.getPumpALogger().info("2.recv mismatched data! (Noz="+nozNo+")");
							is.read(); // flush inputStream
						}
					}
					else if (RxBuf[0] == EOT) {
						
						if (compareNozID(RxBuf)==false) { 
							LogUtility.getPumpALogger().info ("1.Recv EOT NozID mismatch-1.1! (Noz="+nozNo+")");
							Log.datas(RxBuf, 40, 20);
							lineErrCnt++;
							
							is.read(); // flush inputStream
							continue;
						}
						
						if (dispLevel>=3) 
							LogUtility.getPumpALogger().info("2.Recv EOT (Noz="+nozNo+")");
						
						lineErrCnt=0; // Normal terminated
						//continue;
						break; // 스마트주유소 PJT 수정
					} else {
						if (dispLevel>=3) 
							LogUtility.getPumpALogger().info("2.Recv fail! (Noz="+nozNo+")  itemCount=" + TxQue.getItemCount());
						lineErrCnt++;
						continue;
					}
				} 
			} catch (Exception e) {
				LogUtility.getPumpALogger().error("Exception occurr! (Noz="+nozNo+")");
				lineErrCnt++;
				LogUtility.getPumpALogger().error(e.getMessage(), e);
			}
		}
	}
	
	public void run() {
		/*
		while (true) {
			try {
				//Thread.sleep(recvWaitTime);
				Thread.sleep(10000000);
			} catch (InterruptedException e) {
				LogUtility.getPumpALogger().debug ("Thread running error");
			}
		}*/
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

