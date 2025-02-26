package com.gsc.kixxhub.device.pumpa.driver;

import gnu.io.CommPortOwnershipListener;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.util.Calendar;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Vector;

import com.gsc.kixxhub.common.data.pump.CB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.CL_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HE_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HF_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P5_1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P5_NozzleInfo;
import com.gsc.kixxhub.common.data.pump.P7_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.QL_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.QM_WoringMessage;
import com.gsc.kixxhub.common.data.pump.S3_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S4_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S5_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S8_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SE_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SJ_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.TR_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.data.pump.format.PumpMessageFormat;
import com.gsc.kixxhub.common.utility.Base64Util;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.BytesQue2;
import com.gsc.kixxhub.device.pumpa.common.Change;
import com.gsc.kixxhub.device.pumpa.common.DataStruct;
import com.gsc.kixxhub.device.pumpa.common.Log;
import com.gsc.kixxhub.device.pumpa.common.SerialConnectException;
import com.gsc.kixxhub.device.pumpa.common.Sleep;
import com.gsc.kixxhub.device.pumpa.define.CCommDriver;
import com.gsc.kixxhub.device.pumpa.devDatas.TatsunoSelfDS;
import com.gsc.kixxhub.device.pumpa.translation.TransTatsunoSelf_MPP6;

public class CommTatsunoSelf_MPP6 extends CCommDriver implements SerialPortEventListener, 
				CommPortOwnershipListener, Runnable {

	protected static boolean m_isSaveSTX=false;
	protected byte  ACK  = 0x10;
	protected byte[] ACK0 = new byte[2];
	protected byte[] ACK1 = new byte[2];
	protected short 	ADIESEL=12;
	protected int	baseMinErrCnt=5, baseMaxSkipCnt=200;
	protected int	baseReadBuffInterval   = 50+6; // 스마트주유소 PJT 보정
	//protected int	readStartInterval  = 3;
	protected int	baseReadStartInterval  = 0+2; // 스마트주유소 PJT 보정
	protected int	baseWriteStartInterval = 6+2; // 스마트주유소 PJT 보정
	protected int		buffSize = 1024;
	protected CB_WorkingMessage CB_wm;
	protected CL_WorkingMessage CL_wm;
	protected short 	DIESEL=11;
	protected int 	dispLevel=0;
	protected byte  ENQ  = 0x05;
	
	protected byte[] 	enq_Buf = new byte[4];
	
    protected byte  EOT  = 0x04;
    protected byte  ESC  = 0x1B;
    protected byte  ETX  = 0x03;

    protected byte  	PRESET_LITER=1;
		
	protected byte  	PRESET_NONE=2;
	protected byte  	PRESET_PRICE=0;
	
	protected boolean firstRequest=true;
	protected short 	GASOLINE=20;
    protected HE_WorkingMessage HE_wm;
	protected HF_WorkingMessage HF_wm;
	
	protected boolean	issueLineErr=true;
	protected byte[] 	lastPumpingData=new byte[buffSize];
    protected int		lineCommCheckCnt=0;
    
	protected int		lineErrCnt=0;
	protected String 	m_basePrice="000000"; // 승인 요청단가
	protected Hashtable<String,String> m_basePriceTbl = new Hashtable<String,String>();
	protected boolean 	m_bCreditMode, m_bBonusMode, bApprove;
	protected boolean 	m_bFirstPumping = true;
	protected boolean 	m_bFirstPumpingEnd=true;
	protected boolean	m_bFromODT=false;
	protected String  	m_bonusNo="";
	protected byte[]  	m_byBonusNo = new byte[40];

    protected byte[]  	m_byCreditNo = new byte[40];
    protected String  	m_creditNo="";
    protected String 	m_dispPriceLiter="";
    protected boolean	m_isCompleteBasePrice=false;
    protected String 	m_liter="0000000";    // 승인 요청량
    protected int		m_nCreditErrCnt=0;
    protected int	 	m_nInitODTCount=0;
    
    protected short 	m_nODTState=0;
	// Nozzle info tables (P5_1)
	protected Vector<String> m_nozNoVec = new Vector<String>();
	protected int		m_nSetPrice;
	protected int	 	m_numMPPs;
	protected Hashtable<String,String> m_oilCodeTbl = new Hashtable<String,String>();
	protected short 	m_oilKind=0;
	protected Hashtable<String,String> m_oilNameTbl = new Hashtable<String,String>();
	protected byte		m_preset=PRESET_NONE;
	protected String 	m_price="00000000";   // 승인 요청금액
	protected boolean	m_rcvCancelFromPOS=false;
	protected boolean	m_rcvEnableFromPOS=false; // POS 선결제
	protected String 	m_realBasePrice="000000"; // 실 주유단가
	protected String 	m_realLiter="0000000";    // 실 주유량
	protected String 	m_realPrice="00000000";   // 실 주유금액
	
	protected String[][] m_sDispPriceArr=new String[2][6];
	protected boolean	m_setEnvDataOK=false;
	protected String[][] m_sMentPriceArr=new String[2][6];
	protected String[][] m_sPriceButtArr=new String[2][6];
	protected int 		m_statusCode=601;
	protected String	m_targetNozzle;
	protected boolean	m_useFullPumping=true;
    protected byte  SEL  = 'C';
	protected byte  mode = SEL;
	protected byte  NAK  = 0x15;
	protected byte		nozID;
	protected short		nozState=0;
	protected String 	nozStr = "";
	protected PA_WorkingMessage PA_wm;
	protected PB_WorkingMessage PB_wm;
	protected short 	PGASOLINE=21;
	protected byte  POL  = 'S';
	protected byte  PREFIX_ENQ = 0x05;

	//progressStep : 0=초기화전, 1=초기화후, 2=상태정보송신후, 3=주유중, 4=주유완료, 
    //               5=토털게이지수신/주유완료자료 송신
    protected int 	progressStep=3; 
	protected QM_WoringMessage  QM_wm;
	
    protected byte[] 	rcvInitBuf = new byte[6];
    protected byte[] 	RxBuf = new byte[buffSize];
    protected S3_WorkingMessage S3_wm;
    protected S4_WorkingMessage S4_wm;
    protected S5_WorkingMessage S5_wm;
    protected S8_WorkingMessage S8_wm;
    protected SE_WorkingMessage SE_wm;

    protected SJ_WorkingMessage SJ_wm;
    protected byte  STX  = 0x02;

    protected byte[] 	t01_Buf; //파일초기화
    //요청
    protected DataStruct t01_ds = new DataStruct(); //파일초기화
    protected byte[] 	t02_Buf; //파일등록
    
    protected DataStruct t02_ds = new DataStruct(); //파일등록
    protected byte[] 	t02eb_Buf; // End block
    protected DataStruct t02eb_ds = new DataStruct(); // End block
    protected byte[] 	t02sb_Buf; // Start block
    protected DataStruct t02sb_ds = new DataStruct(); // Start block
    protected byte[] 	t10_Buf;
    protected DataStruct t10_ds = new DataStruct(); //다쓰노신형 10 전문
    protected byte[] 	t20_Buf; //POS 상태 통지
    protected DataStruct t20_ds = new DataStruct(); //POS 상태 통지 
    protected byte[] 	t22_Buf; //내부달력 설정
    protected DataStruct t22_ds = new DataStruct(); //내부달력 설정
    protected byte[] 	t23_Buf;
    protected DataStruct t23_ds = new DataStruct();

	protected byte[] 	t40_Buf; //입력오더 요구
    protected DataStruct t40_ds = new DataStruct(); //입력오더 요구
    protected byte[] 	t41_Buf; //상태통지 요구
    protected DataStruct t41_ds = new DataStruct(); //상태통지 요구
    protected byte[] 	t42_Buf; //디바이스제어 요구
    protected DataStruct t42_ds = new DataStruct(); //디바이스제어 요구
    protected byte[] 	t43_Buf; //입력중지 요구
    protected DataStruct t43_ds = new DataStruct(); //입력중지 요구 
    //응답
    protected DataStruct t60_ds = new DataStruct(); //
    protected DataStruct t61_ds = new DataStruct(); //
    protected DataStruct t80_ds = new DataStruct(); //
    protected DataStruct t81_ds = new DataStruct(); //
	
	protected DataStruct t82_ds = new DataStruct(); //상태응답
    protected TR_WorkingMessage TR_wm;
    TransTatsunoSelf_MPP6 trans = new TransTatsunoSelf_MPP6();
    protected DataStruct ts60 = TatsunoSelfDS.getDS("60");
    protected DataStruct ts61 = TatsunoSelfDS.getDS("61");
	
    protected DataStruct ts80 = TatsunoSelfDS.getDS("80");
    protected DataStruct ts81 = TatsunoSelfDS.getDS("81");
    protected DataStruct ts82 = TatsunoSelfDS.getDS("82");
    protected byte[] 	TxBuf = new byte[buffSize];
    protected BytesQue2 TxQue = new BytesQue2(30);
	
	public CommTatsunoSelf_MPP6 (int nozNum, String romVerStr, int nMPPCount) 
	{
    	byte	bcc;
		Formatter form = new Formatter ();
		byte[] 	nozByt = new byte[2];
		
		nozNo = nozNum;
		romVer = romVerStr;
		
		form.format("%02d", nozNo);
		nozStr = form.toString();
		nozByt = nozStr.getBytes();
		nozID = (byte) (nozNo - 1 + 0x40);
		
		m_numMPPs = nMPPCount;
		
	    ACK0[0] = ACK;
	    ACK0[1] = 0x30;
	    ACK1[0] = ACK;
	    ACK1[1] = 0x31;
		
		//--- prefix-ENQ ---//
		enq_Buf[0] = EOT;
		enq_Buf[1] = nozID;
		enq_Buf[2] = SEL;
		enq_Buf[3] = ENQ;

		readBuffInterval   = baseReadBuffInterval;
		readStartInterval  = baseReadStartInterval;
		writeStartInterval = baseWriteStartInterval;
		minErrCnt 		   = baseMinErrCnt;
		maxSkipCnt 		   = baseMaxSkipCnt;
		
		// 금액버튼 설정
		if (romVer.equals("000")) {
			m_sPriceButtArr[0][0] = "00005000";
			m_sPriceButtArr[0][1] = "00010000";
			m_sPriceButtArr[0][2] = "00020000";
			m_sPriceButtArr[0][3] = "00030000";
			m_sPriceButtArr[0][4] = "00040000";
			m_sPriceButtArr[0][5] = "00050000";
			m_sPriceButtArr[1][0] = "00060000";
			m_sPriceButtArr[1][1] = "00070000";
			m_sPriceButtArr[1][2] = "00080000";
			m_sPriceButtArr[1][3] = "00090000";
			m_sPriceButtArr[1][4] = "00100000";
			m_sPriceButtArr[1][5] = "00149900";
			
			m_sDispPriceArr[0][0] = " W5,000";
			m_sDispPriceArr[0][1] = "W10,000";
			m_sDispPriceArr[0][2] = "W20,000";
			m_sDispPriceArr[0][3] = "W30,000";
			m_sDispPriceArr[0][4] = "W40,000";
			m_sDispPriceArr[0][5] = "W50,000";
			m_sDispPriceArr[1][0] = "W60,000";
			m_sDispPriceArr[1][1] = "W70,000";
			m_sDispPriceArr[1][2] = "W80,000";
			m_sDispPriceArr[1][3] = "W90,000";
			m_sDispPriceArr[1][4] = "100,000";
			m_sDispPriceArr[1][5] = "Full   ";

			m_sMentPriceArr[0][0] = "25";
			m_sMentPriceArr[0][1] = "10";
			m_sMentPriceArr[0][2] = "11";
			m_sMentPriceArr[0][3] = "12";
			m_sMentPriceArr[0][4] = "71";
			m_sMentPriceArr[0][5] = "13";
			m_sMentPriceArr[1][0] = "72";
			m_sMentPriceArr[1][1] = "14";
			m_sMentPriceArr[1][2] = "73";
			m_sMentPriceArr[1][3] = "74";
			m_sMentPriceArr[1][4] = "75";
			m_sMentPriceArr[1][5] = "09";
		}
		else if (romVer.equals("351")) {
			m_sPriceButtArr[0][0] = "00010000";
			m_sPriceButtArr[0][1] = "00020000";
			m_sPriceButtArr[0][2] = "00030000";
			m_sPriceButtArr[0][3] = "00040000";
			m_sPriceButtArr[0][4] = "00050000";
			m_sPriceButtArr[0][5] = "00149900";
			m_sPriceButtArr[1][0] = "00060000";
			m_sPriceButtArr[1][1] = "00070000";
			m_sPriceButtArr[1][2] = "00080000";
			m_sPriceButtArr[1][3] = "00090000";
			m_sPriceButtArr[1][4] = "00100000";
			m_sPriceButtArr[1][5] = "00149900";

			m_sDispPriceArr[0][0] = "W10,000";
			m_sDispPriceArr[0][1] = "W20,000";
			m_sDispPriceArr[0][2] = "W30,000";
			m_sDispPriceArr[0][3] = "W40,000";
			m_sDispPriceArr[0][4] = "W50,000";
			m_sDispPriceArr[0][5] = "Full   ";
			m_sDispPriceArr[1][0] = "W60,000";
			m_sDispPriceArr[1][1] = "W70,000";
			m_sDispPriceArr[1][2] = "W80,000";
			m_sDispPriceArr[1][3] = "W90,000";
			m_sDispPriceArr[1][4] = "100,000";
			m_sDispPriceArr[1][5] = "Full   ";

			m_sMentPriceArr[0][0] = "10";
			m_sMentPriceArr[0][1] = "11";
			m_sMentPriceArr[0][2] = "12";
			m_sMentPriceArr[0][3] = "71";
			m_sMentPriceArr[0][4] = "13";
			m_sMentPriceArr[0][5] = "09";
			m_sMentPriceArr[1][0] = "72";
			m_sMentPriceArr[1][1] = "14";
			m_sMentPriceArr[1][2] = "73";
			m_sMentPriceArr[1][3] = "74";
			m_sMentPriceArr[1][4] = "75";
			m_sMentPriceArr[1][5] = "09";
		}
		
		try {
			//--- 파일초기화 ---//
			t01_ds.addByte  ("STX", STX);
			t01_ds.addByte  ("SA", nozID);
			t01_ds.addByte  ("UA", SEL);
			t01_ds.addString("command", "01", 2);
			t01_ds.addByte  ("sequence", (byte) '0');
			t01_ds.addString("fileNo01", "01", 2); //01 파일 초기화
			t01_ds.addString("fileNo02", "02", 2); //02 파일 초기화
			t01_ds.addString("fileNo03", "00", 2); //다쓰노 셀프에서는 유효하지 않은 필드, 언제나 00
			t01_ds.addString("fileNo04", "00", 2); //다쓰노 셀프에서는 유효하지 않은 필드, 언제나 00
			t01_ds.addString("fileNo05", "05", 2); //05 파일 초기화
			t01_ds.addString("fileNo06", "06", 2); //06 파일 초기화
			t01_ds.addString("fileNo07", "00", 2); //다쓰노 셀프에서는 유효하지 않은 필드, 언제나 00
			t01_ds.addString("fileNo08", "08", 2); //08 파일 초기화
			t01_ds.addString("fileNo09", "00", 2); //다쓰노 셀프에서는 유효하지 않은 필드, 언제나 00
			t01_ds.addString("fileNo10", "00", 2); //다쓰노 셀프에서는 유효하지 않은 필드, 언제나 00
			t01_ds.addByte  ("ETX", ETX);
			t01_ds.addByte  ("BCC", (byte) ' ');
			t01_Buf = t01_ds.getByteStream();
			
			//--- Start block ---//
			t02sb_ds.addByte  ("STX", STX);
			t02sb_ds.addByte  ("SA", nozID);
			t02sb_ds.addByte  ("UA", SEL);
			t02sb_ds.addString("command", "02", 2);
			t02sb_ds.addByte  ("sequence", (byte) '0'); //sequence
			t02sb_ds.addString("block", "1", 1);
			t02sb_ds.addString("fileNo", "01", 2);
			t02sb_ds.addString("fileName", "OPT01000", 8);
			t02sb_ds.addString("fileExt", "000", 3);
			t02sb_ds.addString("outputDevice", "00", 2);
			t02sb_ds.addString("reserved", "", 20);
			t02sb_ds.addByte  ("ETX", ETX);
			t02sb_ds.addByte  ("BCC", (byte) ' ');
						
			//--- Data block ---//
			t02_ds.addByte  ("STX", STX);
			t02_ds.addByte  ("SA", nozID); 
			t02_ds.addByte  ("UA", SEL); 
			t02_ds.addString("command", "02", 2);
			t02_ds.addByte  ("sequence", (byte) '0'); //sequence
			t02_ds.addString("block", "2", 1);
			t02_ds.addString("fileNo", "01", 2);
			t02_ds.addString("dataSize", "242", 3); // 블록당 최대 4개의 레코드
			t02_ds.addString("fileData", "", 242);
			t02_ds.addByte  ("ETX", ETX);
			t02_ds.addByte  ("BCC", (byte) ' ');
			
			//--- End block ---//
			t02eb_ds.addByte  ("STX", STX);
			t02eb_ds.addByte  ("SA", nozID);
			t02eb_ds.addByte  ("UA", SEL);
			t02eb_ds.addString("command", "02", 2);
			t02eb_ds.addByte  ("sequence", (byte) '0'); //sequence
			t02eb_ds.addString("block", "3", 1);
			t02eb_ds.addString("fileNo", "01", 2);
			t02eb_ds.addString("blockCnt", "001", 3);
			t02eb_ds.addString("reserved", "", 30);
			t02eb_ds.addByte  ("ETX", ETX);
			t02eb_ds.addByte  ("BCC", (byte) ' ');
			
			//--- pumping grant(Non MPP) ---//
			t10_ds.addByte  ("STX", STX);
			t10_ds.addByte  ("SA", nozID);
			t10_ds.addByte  ("UA", SEL);
			t10_ds.addString("command", "10", 2);
			t10_ds.addByte  ("pumpingType", (byte) '2');
			t10_ds.addByte  ("presetType", (byte) '1');
			t10_ds.addString("price_liter", "000000", 6);
			t10_ds.addByte  ("bPriceFlag", (byte) '2');
			t10_ds.addString("basePrice", "0000", 4);
			t10_ds.addByte  ("ETX", ETX);
			t10_ds.addByte  ("BCC", (byte) ' ');
			t10_Buf = t10_ds.getByteStream();
			
			//POS 상태 통지
			t20_ds.addByte("STX", STX);
			t20_ds.addByte("SA", nozID); //셀프ODT 번호
			t20_ds.addByte("UA", SEL); //다쓰노 셀프에서는 고정값(0x43)
			t20_ds.addString("command", "20", 2);
			t20_ds.addByte("sequence", (byte) '0'); //sequence
			t20_ds.addByte("odtStatus", (byte) '0'); //0 제어가능, 1 제어불가능
			t20_ds.addByte("ETX", ETX);
			t20_ds.addByte("BCC", (byte) '8');
			t20_Buf = t20_ds.getByteStream();
			
			//내부 달력 설정
			t22_ds.addByte("STX", STX);
			t22_ds.addByte("SA", nozID); //셀프ODT 번호
			t22_ds.addByte("UA", SEL); //다쓰노 셀프에서는 고정값(0x43)
			t22_ds.addString("command", "22", 2);
			t22_ds.addByte("sequence", (byte) '0'); //sequence			
			t22_ds.addByte("ETX", ETX);
			t22_ds.addByte("BCC", (byte) ' ');
			t22_Buf = t22_ds.getByteStream();
			
			//표시데이터 지시
			t23_ds.addByte("STX", STX);
			t23_ds.addByte("SA", nozID); //셀프ODT 번호
			t23_ds.addByte("UA", SEL); //다쓰노 셀프에서는 고정값(0x43)
			t23_ds.addString("command", "23", 2);
			t23_ds.addByte("sequence", (byte) '0'); //sequence
			t23_ds.addString("display", "30", 2);
			t23_ds.addByte("write", (byte) '3');
			t23_ds.addByte("ctrl1", (byte) ' ');
			t23_ds.addByte("ctrl2", (byte) ' ');
			t23_ds.addByte("ctrl3", (byte) ' ');
			t23_ds.addByte("ctrl4", (byte) ' ');
			t23_ds.addString("col1", "  ", 2);
			t23_ds.addString("raw1", "  ", 2);
			t23_ds.addString("displayMsg", "", 30);	
			t23_ds.addByte("ETX", ETX);
			t23_ds.addByte("BCC", (byte) ' ');
			t23_Buf = t23_ds.getByteStream();
			
			//CMD 40, 입력 오더 요구
			t40_ds.addByte  ("STX", STX);
			t40_ds.addByte  ("SA", nozID);
			t40_ds.addByte  ("UA", SEL);
			t40_ds.addString("command", "40", 2);
			t40_ds.addByte  ("sequence", (byte) '0'); //sequence
			t40_ds.addString("orderNo", "01", 2);
			t40_ds.addString("keyGroupID", "  ", 2); //키그룹아이디
			t40_ds.addString("reserve1", "  ", 2); //리저브
			t40_ds.addByte  ("orderCond", (byte) '1');
			
			t40_ds.addByte  ("inputCond1", (byte) 0x4B);
			t40_ds.addByte  ("inputCond2", (byte) ' '); 
			t40_ds.addByte  ("inputCond3", (byte) ' ');
			t40_ds.addByte  ("inputCond4", (byte) ' ');
			t40_ds.addString("keyInputLen", "00", 2); //키 입력 자릿수
			t40_ds.addByte  ("reserve2", (byte) ' '); //리저브
			t40_ds.addString("orderLen", "00", 2); //오더 길이
			t40_ds.addString("defaultLen", "00", 2); //디폴트 길이
			t40_ds.addByte  ("ETX", ETX);
			t40_ds.addByte  ("BCC", (byte) ' ');
			t40_Buf = t40_ds.getByteStream();
			
			//CMD 41, 상태 통지 요구
			t41_ds.addByte("STX", STX);
			t41_ds.addByte("SA", nozID); //셀프ODT 번호
			t41_ds.addByte("UA",  SEL); //다쓰노 셀프에서는 고정값(0x43)
			t41_ds.addString("command", "41", 2);
			t41_ds.addByte("sequence", (byte) '0'); //sequence
			t41_ds.addByte("ETX", ETX);
			t41_ds.addByte("BCC", (byte) ' ');
			t41_Buf = t41_ds.getByteStream();
			
			//CMD 42, 디바이스 제어 요구
			t42_ds.addByte("STX", STX);
			t42_ds.addByte("SA", nozID); //셀프ODT 번호
			t42_ds.addByte("UA", SEL); //다쓰노 셀프에서는 고정값(0x43)
			t42_ds.addString("command", "42", 2);
			t42_ds.addByte("sequence", (byte) '0'); //sequence
			t42_ds.addString("deviceNo", "11", 2); //00=타이머,02=램프,11=음성합성,15=사람검지센서,16=카드시큐리티SW
			t42_ds.addString("controlCommand", "01", 2); //디바이스 컴맨드
			t42_ds.addString("condition", "1", 1);
			t42_ds.addString("delaySec", "02", 2);
			t42_ds.addString("dataCode", "384000", 10); //연료구 닫기 안내
			t42_ds.addByte("ETX", ETX);
			t42_ds.addByte("BCC", (byte) ' ');
			t42_Buf = t42_ds.getByteStream();

			//CMD 43, 입력 중지 요구
			t43_ds.addByte("STX", STX);
			t43_ds.addByte("SA", nozID); //셀프ODT 번호
			t43_ds.addByte("UA", SEL); //다쓰노 셀프에서는 고정값(0x43)
			t43_ds.addString("command", "43", 2);
			t43_ds.addByte("sequence", (byte) '0'); //sequence
			t43_ds.addByte("ETX", ETX);
			t43_ds.addByte("BCC", (byte) ' ');
			t43_Buf = t43_ds.getByteStream();
		
		} catch (Exception e) {}
	}
	
	protected boolean chkEnvDataAndSaving (byte[] dat) throws Exception {
		
		boolean envDataFlag=false;
		
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
	
	//--- 원하는 크기의 빈칸을 만든다
	public String generateBlank(int length) throws Exception {
		
		StringBuffer buffer = new StringBuffer();
		
		for (int i = 0; i < length; i++) 
			buffer.append(" ");
		
		return buffer.toString();
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

	protected String getCardTrack1Data (String cardNo) throws Exception {
				
		if (cardNo.contains("=")) 
			return cardNo.substring(0, cardNo.indexOf("="));
		else
			return cardNo;
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
		for (int i=0; i<6 ;i++)
			uCRC = makeCCITT (dat[i], uCRC);

		byte byt = (byte) (((uCRC>>8) & 0x00ff) + (uCRC & 0x00ff));
		Formatter form = new Formatter();
		form.format("00%02X", byt & 0x00ff);
		String str = form.toString();
		byte initData[] = str.getBytes();

		return initData;
	}
	
	protected short getOilKind(String targetNoz) throws Exception {
		
		String oilCode = m_oilCodeTbl.get(targetNoz);
		
		//System.out.printf ("#### noz=%s oliCode=%s\n", targetNoz, oilCode);
		
		if (oilCode.equals("1206"))      // Diesel
			return DIESEL;
		else if (oilCode.equals("1207")) // Adv Diesel
			return ADIESEL;
		else if (oilCode.equals("0660")) // Kixx
			return GASOLINE;
		else if (oilCode.equals("0610")) // Kixx Prime
			return PGASOLINE;
		
		return 0;
	}

	protected void initVariables () throws  Exception {
		
		m_bCreditMode=false;
		m_bBonusMode=false;
		m_bFirstPumping=true;
		m_bFirstPumpingEnd=true;
		m_oilKind= 0;
		m_liter= "0";
		m_price= "0";
		m_creditNo = "";
		m_bonusNo = "";
		
		insertDevCtrlOrder_42((byte)'0', "00", "02", "000"); // 타이머 중지
		insertInitCmd();
//		m_rcvEnableFromPOS = false;
//		m_rcvCancelFromPOS = false;
	}
	
	protected void insertCommFile() throws Exception {
		
		//--- Start block ---//
		t02sb_ds.editString("command", "02", 2);
		t02sb_ds.editByte  ("sequence", (byte) '0'); //sequence
		t02sb_ds.editString("block", "1", 1);
		t02sb_ds.editString("fileNo", "08", 2);
		t02sb_ds.editString("fileName", "OPT08000", 8);
		t02sb_ds.editString("fileExt", "000", 3);
		t02sb_ds.editString("outputDevice", "00", 2);
		t02sb_ds.editString("reserved", this.generateBlank(20), 20);
		t02sb_Buf = t02sb_ds.getByteStream();
		
		TxQue.enQueue(t02sb_Buf);
		
		//--- Data block ---//
		t02_ds.editString("command", "02", 2);
		t02_ds.editByte  ("sequence", (byte) '0'); //sequence
		t02_ds.editString("block", "2", 1);
		t02_ds.editString("fileNo", "08", 2);
		t02_ds.editString("dataSize", "025", 3);
		t02_ds.editString("fileData", "115RS-485 TIME OUT       ", 25);	
		t02_Buf = t02_ds.getByteStream();
		
		TxQue.enQueue(t02_Buf);
		
		//--- End block ---//
		t02eb_ds.editString("command", "02", 2);
		t02eb_ds.editByte  ("sequence", (byte) '0'); //sequence
		t02eb_ds.editString("block", "3", 1);
		t02eb_ds.editString("fileNo", "08", 2);
		t02eb_ds.editString("blockCnt", "001", 3);
		t02eb_ds.editString("reserved", this.generateBlank(30), 30);
		t02eb_Buf = t02eb_ds.getByteStream();
		
		TxQue.enQueue(t02eb_Buf);
		
	}	// end insertCommFile
	
	//----- 디바이스 제어요구(42) -----//
	protected void insertDevCtrlOrder_42 (byte sequence, String deviceNo, String controlCmd, 
			String tailData) throws Exception {
		
		Formatter form = new Formatter();
		byte[] sndBuf;

		form.format("%c%c%c%c%c%c%s%s%s%c%c", STX, nozID, 0x43, '4', '2', sequence, deviceNo, 
				controlCmd, tailData, ETX, ' ');

		sndBuf = form.toString().getBytes();
		
		TxQue.enQueue(sndBuf);

		//LogUtility.getPumpALogger().debug(">>>>Insert command 42. itemCount=" + TxQue.getItemCount() + " (ODT="+nozNo+")");
		//Log.datas(sndBuf, sndBuf.length, 20);
	}
	
	//----- 표시데이터 지시(23) -----//
	protected void insertDisplayData_23(String col, String raw, String dispMsg) throws Exception {
		
		try {
			
			t23_ds.editString("command", "23", 2);
			t23_ds.editByte  ("sequence", (byte) '0'); //sequence
			t23_ds.editString("display", "30", 2);
			t23_ds.editByte  ("write", (byte) '3');
			t23_ds.editByte  ("ctrl1", ESC);
			t23_ds.editByte  ("ctrl2", (byte) 'E');
			t23_ds.editByte  ("ctrl3", ESC);
			t23_ds.editByte  ("ctrl4", (byte) 'L');
			t23_ds.editString("col1", col, 2);
			t23_ds.editString("raw1", raw, 2);
			t23_ds.editString("displayMsg", dispMsg, dispMsg.length());
			
			t23_Buf = t23_ds.getByteStream();
			
			TxQue.enQueue(t23_Buf);
		
		} catch (Exception e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
		
		//System.out.printf (">>>>Insert command 23:\n");
	}
	
	protected void insertDisplayData_23_new (String col, String raw, byte byDisp, byte byWrite, 
			boolean clear, String display) throws Exception {
		
		Formatter form = new Formatter();
		byte[] sndBuf;
		String dispMsg;

		form.format("%c%c%c%c%c%c%c%c%c", STX, nozID, 0x43, '2', '3', '0', byDisp, '0', byWrite); //$$$$$$
		String headStr = form.toString();
		byte[] tail = {ETX, ' '};
		String tailStr = new String (tail);

		if (clear==true) {
			byte[] tmp = {ESC, 'E', ESC, 'L'};
			dispMsg = headStr + new String(tmp) + col + raw + display + tailStr;
		}
		else {
			byte[] tmp = {ESC, 'L'};
			dispMsg = headStr + new String(tmp) + col + raw + display + tailStr;
		}

		sndBuf = dispMsg.getBytes();
		TxQue.enQueue(sndBuf);

		//LogUtility.getPumpALogger().debug(">>>>Insert command 23. itemCount=" + TxQue.getItemCount() + " (ODT="+nozNo+")");
		//Log.datas(sndBuf, sndBuf.length, 20);
	}
	
	
	protected void insertInitCmd() throws Exception {
		t43_ds.editString("command", "43", 2);
		t43_ds.editByte("sequence", (byte) '0');
		
		t43_Buf = t43_ds.getByteStream();
		
		TxQue.enQueue(t43_Buf);
		TxQue.enQueue(t43_Buf);
//		TxQue.enQueue(t43_Buf);
		
		//Log.datas(t43_Buf, t43_Buf.length, 20);
	}	// end insertInitCmd
	
	protected void insertInitialFileDemand() throws Exception {

		t01_ds.editString ("command", "01", 2);
		t01_ds.editByte  ("sequence", (byte) '0');
		t01_ds.editString("fileNo01", "01", 2); //01 파일 초기화
		t01_ds.editString("fileNo02", "02", 2); //02 파일 초기화
		//t01_ds.editString("fileNo01", "00", 2); //01 파일 초기화
		//t01_ds.editString("fileNo02", "00", 2); //02 파일 초기화
		t01_ds.editString("fileNo03", "00", 2); //다쓰노 셀프에서는 유효하지 않은 필드, 언제나 00
		t01_ds.editString("fileNo04", "00", 2); //다쓰노 셀프에서는 유효하지 않은 필드, 언제나 00
		t01_ds.editString("fileNo05", "05", 2); //05 파일 초기화
		t01_ds.editString("fileNo06", "06", 2); //06 파일 초기화
		//t01_ds.editString("fileNo05", "00", 2); //05 파일 초기화
		//t01_ds.editString("fileNo06", "00", 2); //06 파일 초기화
		t01_ds.editString("fileNo07", "00", 2); //다쓰노 셀프에서는 유효하지 않은 필드, 언제나 00
		t01_ds.editString("fileNo08", "08", 2); //08 파일 초기화
		//t01_ds.editString("fileNo08", "00", 2); //08 파일 초기화
		t01_ds.editString("fileNo09", "00", 2); //다쓰노 셀프에서는 유효하지 않은 필드, 언제나 00
		t01_ds.editString("fileNo10", "00", 2); //다쓰노 셀프에서는 유효하지 않은 필드, 언제나 00
		t01_Buf = t01_ds.getByteStream();

		TxQue.enQueue(t01_Buf);
	}
	
	//----- 입력오더 요구(40) -----//
	protected void insertInputOrder_40 (byte sequence, String orderNo, byte orderCond, 
			byte inputCond) throws Exception {

		byte[] sndBuf;
		
		try {

			t40_ds.editByte("sequence", sequence);
			t40_ds.editString("orderNo", orderNo, 2);
			t40_ds.editByte("orderCond", orderCond);
			
			if (inputCond==0x00)
				t40_ds.editByte("inputCond2", (byte) ' ');
			else
				t40_ds.editByte("inputCond2", inputCond);
			
			sndBuf = t40_ds.getByteStream();
				
			TxQue.enQueue(sndBuf);

			//LogUtility.getPumpALogger().debug(">>>>Insert command 40. itemCount=" + TxQue.getItemCount() + " (ODT="+nozNo+")");
			//Log.datas(sndBuf, sndBuf.length, 20);
		
		} catch (Exception e){
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	}
	
	protected void insertKeyFile() throws Exception {	
		
		char x0D = 0x0D;
		char x0A = 0x0A;
		char xETX = (char) ETX;
		int	 blockCnt=5;
		
		String keyDataFile1 = 
				// OilName 처리 
				"101" +	" " + "2" +	"00" + this.generateBlank(31) +
				"04" + "oil1" +	this.generateBlank(10) + x0D + x0A +

				// OilName 처리 
				"102" +	" " + "2" +	"01" + this.generateBlank(31) +
				"04" + "oil2" + this.generateBlank(10) + x0D + x0A +
				
				"301" +	" " + "2" + "03" + this.generateBlank(31) +
				"07" + "manwon1" + this.generateBlank(7) + x0D + x0A +
				
				"302" +	" " + "2" + "04" + this.generateBlank(31) +
				"07" + "manwon2" + this.generateBlank(7) + x0D + x0A;
		
		String keyDataFile1_1 = //---> 6복식만 필요
				// OilName 처리 
				"103" + " " + "2" + "02" + this.generateBlank(31) +
				"04" + "oil3" + this.generateBlank(10) + x0D + x0A;
		
		String keyDataFile1_2 = //---> ROMVer=361(BNA)만 필요
				// 지폐삽입 완료버튼 
				"206" + " " + "2" + "0E" + this.generateBlank(31) +
				"08" + "complete" + this.generateBlank(6) + x0D + x0A;
		
		String keyDataFile2 = 
				"303" +	" " + "2" + "05" + this.generateBlank(31) +
				"07" + "manwon3" + this.generateBlank(7) + x0D + x0A +
				
				"304" + " " + "2" + "06" + this.generateBlank(31) +
				"07" + "manwon4" + this.generateBlank(7) + x0D + x0A +
				
				"305" + " " + "2" + "07" + this.generateBlank(31) +
				"07" + "manwon5" + this.generateBlank(7) + x0D + x0A +
				
				"306" + " " + "2" + "13" + this.generateBlank(31) +
				"04" + "Full" + this.generateBlank(10) + x0D + x0A;
		
		String keyDataFile3 = 
				"401" + " " + "2" + "14" + this.generateBlank(31) +
				"07" + "manwon6" + this.generateBlank(7) + x0D + x0A +
				
				"402" +	" " + "2" + "15" + this.generateBlank(31) +
				"07" + "manwon7" + this.generateBlank(7) + x0D + x0A +
				
				"403" + " " + "2" + "16" + this.generateBlank(31) +
				"07" + "manwon8" + this.generateBlank(7) + x0D + x0A +
				
				"404" +	" " + "2" + "17" + this.generateBlank(31) +
				"07" + "manwon9" + this.generateBlank(7) + x0D + x0A;
		
		String keyDataFile4 = 
				"405" + " " + "2" + "0A" + this.generateBlank(31) +
				"07" + "manwo10" + this.generateBlank(7) + x0D + x0A +
				
				"406" +	" " + "2" + "0B" + this.generateBlank(31) +
				"04" + "Cash" + this.generateBlank(10) + x0D + x0A +
				
				"506" + " " + "2" + "0C" + this.generateBlank(31) +
				"03" + "Not" + this.generateBlank(11) + x0D + x0A +
				
				"606" + " " + "2" + "0D" + this.generateBlank(31) +
				"06" + "Cancel" + this.generateBlank(8) + x0D + x0A;
		
		String finalData = 
				"999" + " " + "2" + "FF" + this.generateBlank(31) +
				"00" + this.generateBlank(14) + x0D + x0A;
		
		// start, data, data2(4개), finalData, end 전송
		//--- Start block ---//
		t02sb_ds.editString("command", "02", 2);
		t02sb_ds.editByte  ("sequence", (byte) '0'); //sequence
		t02sb_ds.editString("block", "1", 1);
		t02sb_ds.editString("fileNo", "05", 2);
		t02sb_ds.editString("fileName", "OPT05000", 8);
		t02sb_ds.editString("fileExt", "000", 3);
		t02sb_ds.editString("outputDevice", "00", 2);
		t02sb_ds.editString("reserved", this.generateBlank(20), 20);
		t02sb_Buf = t02sb_ds.getByteStream();

		TxQue.enQueue(t02sb_Buf);
	    //LogUtility.getPumpALogger().debug("startBlock.length()=" + t02sb_Buf.length);
	    //Show.datas(t02sb_Buf, t02sb_Buf.length, 20);
				
		//--- Data block (keyDataFile1) ---//
		t02_ds.editString("command", "02", 2);
		t02_ds.editByte  ("sequence", (byte) '0'); //sequence
		t02_ds.editString("block", "2", 1);
		t02_ds.editString("fileNo", "05", 2);
		t02_ds.editString("dataSize", "224", 3);
		t02_ds.editString("fileData", keyDataFile1, keyDataFile1.length());	
		t02_Buf = t02_ds.getByteStream();

		TxQue.enQueue(t02_Buf);
	    //LogUtility.getPumpALogger().debug("\nkeyDataFile1.length()=" + t02_Buf.length);
	    //Show.datas(t02_Buf, t02_Buf.length, 20);
				
		if (m_numMPPs==6) {
		//--- Data2 block (keyDataFile1_1) ---> 6복식만 필요---//
			t02_ds.editString("command", "02", 2);
			t02_ds.editByte  ("sequence", (byte) '0'); //sequence
			t02_ds.editString("block", "2", 1);
			t02_ds.editString("fileNo", "05", 2);
			t02_ds.editString("dataSize", "056", 3);
			t02_ds.editString("fileData", keyDataFile1_1, keyDataFile1_1.length());	
			t02_Buf = t02_ds.getByteStream();
			
			blockCnt++;
			TxQue.enQueue(t02_Buf);
		    //LogUtility.getPumpALogger().debug("\nkeyDataFile1_1.length()=" + t02_Buf.length);
		    //Show.datas(t02_Buf, t02_Buf.length, 20);
		}
		
		if (romVer.equals("361")) {
			//--- Data2 block (keyDataFile1_2) ---> BNA 만 필요---//
			t02_ds.editString("command", "02", 2);
			t02_ds.editByte  ("sequence", (byte) '0'); //sequence
			t02_ds.editString("block", "2", 1);
			t02_ds.editString("fileNo", "05", 2);
			t02_ds.editString("dataSize", "056", 3);
			t02_ds.editString("fileData", keyDataFile1_2, keyDataFile1_2.length());	
			t02_Buf = t02_ds.getByteStream();

			blockCnt++;
			TxQue.enQueue(t02_Buf);
		    //LogUtility.getPumpALogger().debug("\nkeyDataFile1_2.length()=" + t02_Buf.length);
		    //Show.datas(t02_Buf, t02_Buf.length, 20);
		}
				
		//--- Data2 block (keyDataFile2) ---//
		t02_ds.editString("command", "02", 2);
		t02_ds.editByte  ("sequence", (byte) '0'); //sequence
		t02_ds.editString("block", "2", 1);
		t02_ds.editString("fileNo", "05", 2);
		t02_ds.editString("dataSize", "224", 3);
		t02_ds.editString("fileData", keyDataFile2, keyDataFile2.length());	
		t02_Buf = t02_ds.getByteStream();

		TxQue.enQueue(t02_Buf);
	    //LogUtility.getPumpALogger().debug("\nkeyDataFile2.length()=" + t02_Buf.length);
	    //Show.datas(t02_Buf, t02_Buf.length, 20);
				
		//--- Data3 block (keyDataFile3) ---//
		t02_ds.editString("command", "02", 2);
		t02_ds.editByte  ("sequence", (byte) '0'); //sequence
		t02_ds.editString("block", "2", 1);
		t02_ds.editString("fileNo", "05", 2);
		t02_ds.editString("dataSize", "224", 3);
		t02_ds.editString("fileData", keyDataFile3, keyDataFile3.length());	
		t02_Buf = t02_ds.getByteStream();

		TxQue.enQueue(t02_Buf);
	    //LogUtility.getPumpALogger().debug("\nkeyDataFile3.length()=" + t02_Buf.length);
	    //Show.datas(t02_Buf, t02_Buf.length, 20);
				
		//--- Data4 block (keyDataFile4) ---//
		t02_ds.editString("command", "02", 2);
		t02_ds.editByte  ("sequence", (byte) '0'); //sequence
		t02_ds.editString("block", "2", 1);
		t02_ds.editString("fileNo", "05", 2);
		t02_ds.editString("dataSize", "224", 3);
		t02_ds.editString("fileData", keyDataFile4, keyDataFile4.length());	
		t02_Buf = t02_ds.getByteStream();

		TxQue.enQueue(t02_Buf);
	    //LogUtility.getPumpALogger().debug("\nkeyDataFile4.length()=" + t02_Buf.length);
	    //Show.datas(t02_Buf, t02_Buf.length, 20);
				
		//--- Final Data block ---//
		t02_ds.editString("command", "02", 2);
		t02_ds.editByte  ("sequence", (byte) '0'); //sequence
		t02_ds.editString("block", "2", 1);
		t02_ds.editString("fileNo", "05", 2);
		t02_ds.editString("dataSize", "056", 3);
		t02_ds.editString("fileData", finalData, finalData.length());	
		t02_Buf = t02_ds.getByteStream();

		TxQue.enQueue(t02_Buf);
	    //LogUtility.getPumpALogger().debug("\nfinalData.length()=" + t02_Buf.length);
	    //Show.datas(t02_Buf, t02_Buf.length, 20);
				
		//--- End block ---//
		String sBlockCnt = Change.toString("%03d", blockCnt);
		t02eb_ds.editString("command", "02", 2);
		t02eb_ds.editByte  ("sequence", (byte) '0'); //sequence
		t02eb_ds.editString("block", "3", 1);
		t02eb_ds.editString("fileNo", "05", 2);
		t02eb_ds.editString("blockCnt", sBlockCnt, 3);
		t02eb_ds.editString("reserved", this.generateBlank(30), 30);
		t02eb_Buf = t02eb_ds.getByteStream();

		TxQue.enQueue(t02eb_Buf);
	    //LogUtility.getPumpALogger().debug("\nendBlock.length()=" + t02eb_Buf.length);
	    //Show.datas(t02eb_Buf, t02eb_Buf.length, 20);
				
	}	// end insertKeyFile


	protected void insertKeyGrpFile() throws Exception {
		
		String fileData = makeKeyGrpFile();
		
		//--- Start block ---//
		t02sb_ds.editString("command", "02", 2);
		t02sb_ds.editByte  ("sequence", (byte) '0'); //sequence
		t02sb_ds.editString("block", "1", 1);
		t02sb_ds.editString("fileNo", "02", 2);
		t02sb_ds.editString("fileName", "OPT02000", 8);
		t02sb_ds.editString("fileExt", "000", 3);
		t02sb_ds.editString("outputDevice", "00", 2);
		t02sb_ds.editString("reserved", this.generateBlank(20), 20);
		t02sb_Buf = t02sb_ds.getByteStream();
		
		TxQue.enQueue(t02sb_Buf);
		
		//--- Data block ---//
		t02_ds.editString("command", "02", 2);
		t02_ds.editByte  ("sequence", (byte) '0'); //sequence
		t02_ds.editString("block", "2", 1);
		t02_ds.editString("fileNo", "02", 2);
		t02_ds.editString("dataSize", "134", 3);
		t02_ds.editString("fileData", fileData, fileData.length());	
		t02_Buf = t02_ds.getByteStream();
		
		TxQue.enQueue(t02_Buf);
		
		//--- End block ---//
		t02eb_ds.editString("command", "02", 2);
		t02eb_ds.editByte  ("sequence", (byte) '0'); //sequence
		t02eb_ds.editString("block", "3", 1);
		t02eb_ds.editString("fileNo", "02", 2);
		t02eb_ds.editString("blockCnt", "001", 3);
		t02eb_ds.editString("reserved", this.generateBlank(30), 30);
		t02eb_Buf = t02eb_ds.getByteStream();
		
		TxQue.enQueue(t02eb_Buf);
		
	}	// end insertKeyGrpFile

	protected void insertOPTFile() throws Exception {

		char x0D = 0x0D;
		char x0A = 0x0A;
		
		String fileData = 	"000" +
							"2" +
							this.generateBlank(215) +
							"11" +
							"010" +
							"05" +
							this.generateBlank(14) + x0D + x0A;
		
		//--- Start block ---//
		t02sb_ds.editString("command", "02", 2);
		t02sb_ds.editByte  ("sequence", (byte) '0'); //sequence
		t02sb_ds.editString("block", "1", 1);
		t02sb_ds.editString("fileNo", "01", 2);
		t02sb_ds.editString("fileName", "OPT01000", 8);
		t02sb_ds.editString("fileExt", "000", 3);
		t02sb_ds.editString("outputDevice", "00", 2);
		t02sb_ds.editString("reserved", "", 20);
		t02sb_Buf = t02sb_ds.getByteStream();
		
		TxQue.enQueue(t02sb_Buf);
		
		//--- Data block ---//
		t02_ds.editString("command", "02", 2);
		t02_ds.editByte  ("sequence", (byte) '0'); //sequence
		t02_ds.editString("block", "2", 1);
		t02_ds.editString("fileNo", "01", 2);
		t02_ds.editString("dataSize", "242", 3);
		t02_ds.editString("fileData", fileData, fileData.length());	
		t02_Buf = t02_ds.getByteStream();
		
		TxQue.enQueue(t02_Buf);
		
		//--- End block ---//
		t02eb_ds.editString("command", "02", 2);
		t02eb_ds.editByte  ("sequence", (byte) '0'); //sequence
		t02eb_ds.editString("block", "3", 1);
		t02eb_ds.editString("fileNo", "01", 2);
		t02eb_ds.editString("blockCnt", "001", 3);
		t02eb_ds.editString("reserved", "", 30);
		t02eb_Buf = t02eb_ds.getByteStream();
		
		TxQue.enQueue(t02eb_Buf);
		
	}
	
	protected void insertOPTStatus(byte status) throws Exception {
		
		t20_ds.editString("command", "20", 2);
		t20_ds.editByte("sequence", (byte) '0');
		t20_ds.editByte("odtStatus", status);
		
		t20_Buf = t20_ds.getByteStream();
		
		TxQue.enQueue(t20_Buf);
		
	}	// end insertOPTStatus

	// 주유기로 주유허가 - 사용않음
	protected void insertPermitCmd(byte pumpingType, byte presetType,
		  	String price_liter, byte bPriceFlag, String basePrice)  throws Exception {
		
		t10_ds.editString("command", "10", 2);
		t10_ds.editByte("pumpingType", pumpingType);
		t10_ds.editByte("presetType", presetType);
		t10_ds.editString("price_liter", price_liter, 6);
		t10_ds.editByte("bPriceFlag", bPriceFlag);
		t10_ds.editString("basePrice", basePrice, 4);
		
		t10_Buf = t10_ds.getByteStream();
		
		TxQue.enQueue(t10_Buf);

	}	// end insertPermitCmd
	
	protected void insertPrintData (byte[] pbySrcData, int nStart, int nLen, int sequence) throws Exception {

		Formatter form = new Formatter();
		byte[] sndBody = new byte[nLen];
		byte[] sndBuf = new byte[nLen+14];
		sequence = (sequence > 9 ? 9 : sequence);

		form.format("%c%c%c%c%c%d%c%c%c", STX, nozID, 0x43, '0', '2', sequence, '2', '9', '0');
		String headStr = form.toString();
		byte[] tail = {ETX, ' '};
		String tailStr = new String (tail);
		String lenStr = Change.toString("%03d", nLen);
		byte[] byHeadStr = headStr.getBytes(); // 9EA
		byte[] bylenStr = lenStr.getBytes(); // 3EA
		byte[] byTailStr = tailStr.getBytes(); // 2EA
		
		System.arraycopy(pbySrcData, nStart, sndBody, 0, nLen);
		
		//String sndStr = headStr + lenStr + new String(sndBody) + tailStr;
		//sndBuf = sndStr.getBytes();
		System.arraycopy(byHeadStr, 0, sndBuf, 0, 9);
		System.arraycopy(bylenStr, 0, sndBuf, 9, 3);
		System.arraycopy(sndBody, 0, sndBuf, 12, nLen);
		System.arraycopy(byTailStr, 0, sndBuf, 12+nLen, 2);

		TxQue.enQueue(sndBuf);
		
		//LogUtility.getPumpALogger().debug(">>>>Insert command 02. itemCount=" + TxQue.getItemCount() + " (ODT="+nozNo+")");
		//Log.datas(sndBuf, sndBuf.length, 20);
	}
	
	protected void insertPrintEnd (int nBlockCnt, int sequence) throws Exception {

		Formatter form = new Formatter();
		byte[] sndBuf;
		sequence = (sequence > 9 ? 9 : sequence);
		
		form.format("%c%c%c%c%c%d%c%c%c", STX, nozID, 0x43, '0', '2', sequence, '3', '9', '0');
		String headStr = form.toString();
		byte[] tail = {ETX, ' '};
		String tailStr = new String (tail);
		String blkCntStr = Change.toString("%03d", nBlockCnt);
		
		String sndStr = headStr + blkCntStr + "                              " + tailStr;
		sndBuf = sndStr.getBytes();

		TxQue.enQueue(sndBuf);

		//LogUtility.getPumpALogger().debug(">>>>Insert command 02. itemCount=" + TxQue.getItemCount() + " (ODT="+nozNo+")");
		//Log.datas(sndBuf, sndBuf.length, 20);
	}

	protected void insertPrintStart(int sequence) throws Exception {
		
		Formatter form = new Formatter();
		byte[] sndBuf;
		sequence = (sequence > 9 ? 9 : sequence);

		form.format("%c%c%c%c%c%d%c%c%c", STX, nozID, 0x43, '0', '2', sequence, '1', '9', '0');
		String headStr = form.toString();
		byte[] tail = {ETX, ' '};
		String tailStr = new String (tail);
		
		String sndStr = headStr + "OPT09000" + "000" + "05" + "                    " + tailStr;
		sndBuf = sndStr.getBytes();

		TxQue.enQueue(sndBuf);
		
		//LogUtility.getPumpALogger().debug(">>>>Insert command 02. itemCount=" + TxQue.getItemCount() + " (ODT="+nozNo+")");
		//Log.datas(sndBuf, sndBuf.length, 20);
	}
	
	protected void insertStampFile() throws Exception {
		
		  //--- Start block ---//
		  t02sb_ds.editString("command", "02", 2);
		  t02sb_ds.editByte  ("sequence", (byte) '0'); //sequence
		  t02sb_ds.editString("block", "1", 1);
		  t02sb_ds.editString("fileNo", "06", 2);
		  t02sb_ds.editString("fileName", "OPT06000", 8);
		  t02sb_ds.editString("fileExt", "000", 3);
		  t02sb_ds.editString("outputDevice", "00", 2);
		  t02sb_ds.editString("reserved", this.generateBlank(20), 20);
		  t02sb_Buf = t02sb_ds.getByteStream();
		  
		  TxQue.enQueue(t02sb_Buf);
		  
		  //--- Data block ---//
		  t02_ds.editString("command", "02", 2);
		  t02_ds.editByte  ("sequence", (byte) '0'); //sequence
		  t02_ds.editString("block", "2", 1);
		  t02_ds.editString("fileNo", "06", 2);
		  t02_ds.editString("dataSize", "016", 3);
		  t02_ds.editString("fileData", "HSoBAf//////////", 16); 
		  t02_Buf = t02_ds.getByteStream();
		  
		  TxQue.enQueue(t02_Buf);
		  
		  //--- End block ---//
		  t02eb_ds.editString("command", "02", 2);
		  t02eb_ds.editByte  ("sequence", (byte) '0'); //sequence
		  t02eb_ds.editString("block", "3", 1);
		  t02eb_ds.editString("fileNo", "06", 2);
		  t02eb_ds.editString("blockCnt", "001", 3);
		  t02eb_ds.editString("reserved", this.generateBlank(30), 30);
		  t02eb_Buf = t02eb_ds.getByteStream();
		  
		  TxQue.enQueue(t02eb_Buf);
		  
		 } // end insertStampFile
	
	// 주유기로 lock/unlock - 사용않음
	protected void insertStopCmd (boolean bStop) throws Exception {

	}
	
	protected boolean isCompleteBasePrice (P5_1_WorkingMessage P5_1_wm) throws Exception {
		
		Vector<P5_NozzleInfo> nozInfoVec = P5_1_wm.getNozzleInfo();
		int nozCnt = nozInfoVec.size();

		for (int i=0; i<nozCnt; i++) {
			P5_NozzleInfo P5_nozWm = nozInfoVec.get(i);
			if (Change.toValue(P5_nozWm.getBasePrice()) <= 0) // 단가=0 이 1개라도 있으면
				return false;
		}
		
		return true;
	}
	
	protected boolean isSkipWorkingMessage (WorkingMessage wm) throws Exception {
		
		boolean skip=false;

		if (wm.getCommand().equals("P5_1")) { // 셀프 환경설정
			
			P5_1_WorkingMessage P5_1_wm = (P5_1_WorkingMessage) wm;
			Vector<P5_NozzleInfo> nozInfoVec = P5_1_wm.getNozzleInfo();
			int nozCnt = nozInfoVec.size();

			LogUtility.getPumpALogger().info("[Pump A][수신-다쓰노셀프ODT 노즐/단가 정보]" + 
					" ODT_No=" + P5_1_wm.getNozzleNo() + "("+P5_1_wm.getCommand()+")" +
					" | mode=" + P5_1_wm.getMode() + 
					" | useFullPumping=" + P5_1_wm.getUseFullPumping());
			
			// 단가 정상수신 확인(추가 2010/2/22)
			if (P5_1_wm.getMode().equals("0") || P5_1_wm.getMode().equals("1"))
				m_isCompleteBasePrice = isCompleteBasePrice(P5_1_wm);
			
			if (P5_1_wm.getMode().equals("0")) { // 초기화
				
				for (int i=0; i<nozCnt; i++) {
					
					P5_NozzleInfo P5_nozWm = nozInfoVec.get(i);
					String szNozNo = P5_nozWm.getNozzleNumber();
					LogUtility.getPumpALogger().info("nozzle="+ P5_nozWm.getNozzleNumber() + "(P5_1)");
					LogUtility.getPumpALogger().info("bPrice="+ P5_nozWm.getBasePrice());
					LogUtility.getPumpALogger().info("oilType="+ P5_nozWm.getGoodsCode());
					LogUtility.getPumpALogger().info("oilCode="+ P5_nozWm.getGoodsType() + "\n");
					
					m_nozNoVec.add(szNozNo);
					m_basePriceTbl.put(szNozNo, P5_nozWm.getBasePrice());
					m_oilCodeTbl.put(szNozNo, P5_nozWm.getGoodsCode());
					m_oilNameTbl.put(szNozNo, P5_nozWm.getGoodsType());
				}
				// 가득주유 사용여부
				m_useFullPumping = P5_1_wm.getUseFullPumping().equals("1")? true : false;

				m_setEnvDataOK=true;
			}
			else if (P5_1_wm.getMode().equals("1")) { // 단가변경
				
				for (int i=0; i<nozCnt; i++) {
					P5_NozzleInfo P5_nozWm = nozInfoVec.get(i);
					String szNozNo = P5_nozWm.getNozzleNumber();
					String beforeBasePrice_ = m_basePriceTbl.get(szNozNo).substring(0,4);

					m_basePriceTbl.remove(szNozNo);
					m_basePriceTbl.put(szNozNo, P5_nozWm.getBasePrice());
					
					/*
					// 추가(09/03/05) -> 주유기 단가 즉시변경 필요시
					PB_wm = new PB_WorkingMessage();
					PB_wm.setPassThrough(false);
					PB_wm.setNozzleNo(wm.getNozzleNo()); // ODT No
					PB_wm.setConnectNozzleNo(wm.getConnectNozzleNo()); // Nozzle No
					PB_wm.setTargetNozzleNo(wm.getConnectNozzleNo());  // Nozzle No
					PB_wm.setCommandSet(Change.toString(m_preset));
					PB_wm.setLiter("0000000");
					PB_wm.setBasePrice(P5_nozWm.getBasePrice());
					PB_wm.setPrice("00000000");
					insertRecvQueue(PB_wm);
					*/

					LogUtility.getPumpALogger().info("단가변경용 P5수신, 단가변경. nozzle="+szNozNo+" : "+beforeBasePrice_+
							" -> " +P5_nozWm.getBasePrice().substring(0,4)+" (TableSize="+m_basePriceTbl.size()+")");
				}
			}
			else if (P5_1_wm.getMode().equals("2")) { // 가득주유 옵션변경
				
				m_useFullPumping = P5_1_wm.getUseFullPumping().equals("1")? true : false;

				LogUtility.getPumpALogger().info("가득주유 옵션변경용 P5수신, 옵션변경. ODT_No="+P5_1_wm.getNozzleNo() +
						" useFullPumping=" + P5_1_wm.getUseFullPumping());
			}
			
			skip = true;
		}
		else if (wm.getCommand().equals("PB")) { // 선결제 또는 정액정량설정
			PB_wm = (PB_WorkingMessage) wm;
			m_rcvEnableFromPOS = true;
			m_rcvCancelFromPOS = false;
			
			LogUtility.getPumpALogger().info("[Pump A][수신-다쓰노셀프ODT 선결제]" + " nozzle=" + 
					 PB_wm.getConnectNozzleNo() + "("+PB_wm.getCommand()+")" +
					 " | ODT_No=" + PB_wm.getNozzleNo() +
					 " | mode=" + PB_wm.getCommandSet() + 
					 " | liter=" + PB_wm.getLiter() + 
					 " | basePrice=" + PB_wm.getBasePrice() + 
					 " | price=" + PB_wm.getPrice() + "\n");

			m_targetNozzle=PB_wm.getConnectNozzleNo();
			m_oilKind = getOilKind(PB_wm.getConnectNozzleNo());
			m_liter = PB_wm.getLiter();
			m_basePrice = PB_wm.getBasePrice();
			m_price = PB_wm.getPrice();
			int mode = Change.toValue(PB_wm.getCommandSet());
			
			if (mode==0) { // 정액설정
				m_nSetPrice = Change.toValue(m_price);
				m_dispPriceLiter = Change.toString("W%d",m_nSetPrice);
			}
			else { // 정량설정
				m_nSetPrice = (Change.toValue(m_liter)/1000) * Change.toValue(m_basePrice.substring(0,4)) - 1;
				m_dispPriceLiter = m_liter.substring(0,4) + "." + m_liter.substring(4,6) + "L";
			}
			
			String oilName = m_oilNameTbl.get(PB_wm.getConnectNozzleNo());
			
			m_nODTState = 7;
			insertInitCmd();
							
			// 승인되었습니다. OO색 손잡이가 000입니다. 주유기를 들고 주유해 주세요.
			insertDevCtrlOrder_42((byte)'0', "00", "02", "000"); // 타이머 중지
			insertDevCtrlOrder_42((byte)'0', "11", "02", "000"); // 음성 중지
			insertDevCtrlOrder_42((byte)'0', "02", "01", "00000010"+"0000000000000000");
			insertDisplayData_23_new("01", "01", (byte)'3', (byte)'3', true, oilName);
			insertDisplayData_23_new("02", "01", (byte)'3', (byte)'3', false, m_dispPriceLiter);
			insertDisplayData_23_new("02", "10", (byte)'3', (byte)'3', false, "OK!       ");
	
			if (DIESEL == m_oilKind) {
				insertDevCtrlOrder_42((byte)'0', "11", "01", "1003133344500");
				insertDevCtrlOrder_42((byte)'0', "11", "01", "0103133344500");
			}
			else if (ADIESEL == m_oilKind) {
				insertDevCtrlOrder_42((byte)'0', "11", "01", "1003147344500");
				insertDevCtrlOrder_42((byte)'0', "11", "01", "0103147344500");
			}
			else if (GASOLINE == m_oilKind) {
				insertDevCtrlOrder_42((byte)'0', "11", "01", "1003132344500");
				insertDevCtrlOrder_42((byte)'0', "11", "01", "0103132344500");
			}
			else if (PGASOLINE == m_oilKind) {
				insertDevCtrlOrder_42((byte)'0', "11", "01", "1003146344500");
				insertDevCtrlOrder_42((byte)'0', "11", "01", "0103146344500");
			}

			makeStatusInfo (235); // 주유대기
			
			skip = true;
		}
		else if (wm.getCommand().equals("PA")) { // 선결제 취소. 추가(2010/05/03)
			PA_wm = (PA_WorkingMessage) wm;
			
			if (m_rcvEnableFromPOS==true && PA_wm.getNozzleState().equals("0")) {
				
				m_rcvCancelFromPOS = true; // PB 수신후 PA(mode=0) 수신
				insertDisplayData_23_new("02", "01", (byte)'1', (byte)'1', true, "Preset canceling..."); // 추가 (2010/11/18)
				insertDevCtrlOrder_42((byte)'0', "11", "01", "1002300");
				
				LogUtility.getPumpALogger().info("[Pump A][수신-다쓰노셀프ODT 선결제 취소]" + " nozzle=" + 
						PA_wm.getConnectNozzleNo() + "("+PA_wm.getCommand()+")" +
						 " | ODT_No=" + PA_wm.getNozzleNo() +
						 " | mode=" + PA_wm.getNozzleState() + "\n");
			}
			
			skip = true;
		}
		else if (wm.getCommand().equals("QM")) { // 카드 승인응답
			QM_wm = (QM_WoringMessage) wm;
			
			LogUtility.getPumpALogger().info("[Pump A][수신-다쓰노셀프ODT 승인응답]" + " nozzle=" + 
							QM_wm.getConnectNozzleNo() + "("+QM_wm.getCommand()+")" +
							" | ODT_No=" + QM_wm.getNozzleNo() +
							" | mode=" + QM_wm.getMode() + "(1:승인, 2:거부, 3:미응답)"); 
			
			short mode = (short) Change.toValue(QM_wm.getMode());
			mode = m_isCompleteBasePrice==false? 0 : mode; // 단가확인
			
			if (mode == 1) { // 승인OK
				
				m_nODTState = 7;
				
				PB_wm = new PB_WorkingMessage();
				PB_wm.setPassThrough(false);
				PB_wm.setNozzleNo(wm.getNozzleNo()); // ODT No
				PB_wm.setConnectNozzleNo(wm.getConnectNozzleNo()); // Nozzle No
				PB_wm.setTargetNozzleNo(wm.getConnectNozzleNo());  // Nozzle No
				PB_wm.setCommandSet(Change.toString(m_preset));
				
				PB_wm.setLiter(m_liter);
				PB_wm.setBasePrice(m_basePrice);
				PB_wm.setPrice(m_price);
								
				insertRecvQueue(PB_wm);
				
				//--- 주유금액 설정
				m_nSetPrice = Change.toValue(m_price);
				m_dispPriceLiter = Change.toString("W%d",m_nSetPrice);
				
				LogUtility.getPumpALogger().info("승인OK(QM), 주유허가(PB) 발생 : ODT_No=" + QM_wm.getNozzleNo()+ 
						" nozzle=" + QM_wm.getConnectNozzleNo() + " liter=" + m_liter + 
						" bPrice=" + m_basePrice + " price=" + m_price);
				
				makeStatusInfo (235); // 주유대기
				
				insertDevCtrlOrder_42((byte)'0', "00", "02", "000"); // 타이머 중지
				insertDevCtrlOrder_42((byte)'0', "11", "02", "000"); // 음성 중지
				insertDevCtrlOrder_42((byte)'0', "02", "01", "00000010"+"0000000000000000");
				insertDisplayData_23_new("02", "10", (byte)'3', (byte)'3', false, "OK!       ");
				
				// 승인되었습니다. OO색 손잡이가 000입니다. 주유기를 들고 주유해 주세요.
				if (DIESEL == m_oilKind) {
					insertDevCtrlOrder_42((byte)'0', "11", "01", "1003133344500");
					insertDevCtrlOrder_42((byte)'0', "11", "01", "0003133344500");
				}
				else if (ADIESEL == m_oilKind) {
					insertDevCtrlOrder_42((byte)'0', "11", "01", "1003147344500");
					insertDevCtrlOrder_42((byte)'0', "11", "01", "0003147344500");
				}
				else if (GASOLINE == m_oilKind) {
					insertDevCtrlOrder_42((byte)'0', "11", "01", "1003132344500");
					insertDevCtrlOrder_42((byte)'0', "11", "01", "0003132344500");
				}
				else if (PGASOLINE == m_oilKind) {
					insertDevCtrlOrder_42((byte)'0', "11", "01", "1003146344500");
					insertDevCtrlOrder_42((byte)'0', "11", "01", "0003146344500");
				}

			}
			else if (mode==2 || // 승인거절
					 mode==3) { // 미응답
				
				if (m_nODTState==6) {
								
					//승인을 받을수 없습니다. 사무실에서 확인하여 주세요.
					insertDevCtrlOrder_42((byte)'0', "00", "02", "000"); // 타이머 중지
					insertDevCtrlOrder_42((byte)'0', "11", "02", "000"); // 음성 중지
					insertDevCtrlOrder_42((byte)'0', "11", "01", "10023022400");
					insertDevCtrlOrder_42((byte)'0', "11", "01", "00523022400");
					insertDisplayData_23_new("02", "07", (byte)'3', (byte)'3', true, "Cancel!       ");
					insertDevCtrlOrder_42((byte)'0', "02", "01", "00000000"+"0000000000000000");
					
					// 초기상태(유종선택)로 가기위함
					insertDevCtrlOrder_42((byte)'0', "00", "01", "010"); // 타이머 시작

					LogUtility.getPumpALogger().info("승인거절(QM) : ODT_No=" + QM_wm.getNozzleNo()+ 
							" nozzle=" + QM_wm.getConnectNozzleNo() + " liter=" + m_liter + 
							" bPrice=" + m_basePrice + " price=" + m_price + " m_nODTState="+m_nODTState);

					m_nODTState=8;
				}
			}
			else if (mode == 0) { // 단가이상시 주유취소 
				S4_WorkingMessage S4_wm = new S4_WorkingMessage ();
				S4_wm.setNozzleNo(QM_wm.getConnectNozzleNo()); // NozzleNo
				S4_wm.setConnectNozzleNo(QM_wm.getNozzleNo()); // ODTNo
				insertRecvQueue(S4_wm);
				
				LogUtility.getPumpALogger().debug("단가(P5)이상으로 주유취소 ODT_No=" + S4_wm.getNozzleNo());
			}

			skip = true;
		}
		else if (wm.getCommand().equals("S3")) { // 주유중
			S3_wm = (S3_WorkingMessage) wm;
			
			if (m_bFirstPumping==true) {
				
				m_bFirstPumping = false;
				insertInitCmd();
				
				//선택하신량보다 적게 주유시 정액버턴을 누르면 천원단위 주유가 됩니다.
				insertDevCtrlOrder_42((byte)'0', "00", "02", "000"); // 타이머 중지
				insertDevCtrlOrder_42((byte)'0', "11", "02", "000"); // 음성 중지
				insertDevCtrlOrder_42((byte)'0', "11", "01", "10037525000");
				insertDevCtrlOrder_42((byte)'0', "11", "01", "00237525000");

				insertDisplayData_23_new("01", "01", (byte)'3', (byte)'3', true, m_dispPriceLiter);
				insertDisplayData_23_new("02", "01", (byte)'3', (byte)'3', false, "Starting...");
				
			}

//			String szLiter1 = S3_wm.getLiter().substring(0, 4);
//			String szLiter2 = S3_wm.getLiter().substring(4, 7);
						
			if (Change.toValue(S3_wm.getPrice()) >= m_nSetPrice) { 

				if (m_bFirstPumpingEnd==true) {
					m_bFirstPumpingEnd=false;
					
					// 주유가 완료 되었습니다.
					insertDevCtrlOrder_42((byte)'0', "00", "02", "000"); // 타이머 중지
					insertDevCtrlOrder_42((byte)'0', "11", "02", "000"); // 음성 중지
					insertDevCtrlOrder_42((byte)'0', "11", "01", "0005700");
					
					insertDisplayData_23_new("02", "01", (byte)'1', (byte)'1', true, "End!");

					LogUtility.getPumpALogger().info("설정액 만큼 주유가 되었습니다. ODT_No=" + S3_wm.getNozzleNo()+ 
							" nozzle=" + S3_wm.getConnectNozzleNo() + 
							" 설정액=" + m_nSetPrice+ " 주유액="+ Change.toValue(S3_wm.getPrice()));
					
				}
			}

			skip = true;
		}
		else if (wm.getCommand().equals("S4")) { // 주유완료
			S4_wm = (S4_WorkingMessage) wm;

			m_bFromODT = false;
			m_bFirstPumping = true;
			m_bFirstPumpingEnd = true;
			
			LogUtility.getPumpALogger().info("[Pump A][수신-다쓰노셀프ODT 주유완료]" + " nozzle=" + 
					S4_wm.getConnectNozzleNo() + "("+S4_wm.getCommand()+")" +
					" | ODT_No=" + S4_wm.getNozzleNo() +
					" | liter=" + S4_wm.getLiter() +
					" | bPrice=" + S4_wm.getBasePrice() +
					" | price=" + S4_wm.getPrice());
			
			insertDevCtrlOrder_42((byte)'0', "00", "02", "000"); // 타이머 중지
			insertDevCtrlOrder_42((byte)'0', "11", "02", "000"); // 음성 중지
			
			if (m_bCreditMode==true) { // 신용카드 승인고객
				// 연료구를 닫아주시고 영수증을 받아주세요.
				insertDevCtrlOrder_42((byte)'0', "11", "01", "1003840024800");
			}
			else { // 현금 고객
				// 연료구를 닫아주세요.감사합니다.안녕히가세요.
				insertDevCtrlOrder_42((byte)'0', "11", "01", "10056024800");
			}
			insertDevCtrlOrder_42((byte)'0', "02", "01", "00000000"+"0000000000000000");
		
			// 영수증 출력후 초기상태(유종선택)로 가기위함
			insertDevCtrlOrder_42((byte)'0', "00", "01", "015"); // 타이머 시작

			m_nODTState=8;
			
			String szLiter1 = S4_wm.getLiter().substring(0, 4);
			String szLiter2 = S4_wm.getLiter().substring(4, 7);

			insertDisplayData_23_new("02", "01", (byte)'1', (byte)'1', true, "Printing...");
			
			// TR 전문생성용
			m_realPrice     = S4_wm.getPrice();
			m_realLiter     = S4_wm.getLiter();
			m_realBasePrice = S4_wm.getBasePrice();
			
			skip = true;
		}
		else if (wm.getCommand().equals("QL")) { // 영수증출력
			QL_WorkingMessage QL_wm = (QL_WorkingMessage) wm;

			LogUtility.getPumpALogger().info("[Pump A][수신-다쓰노셀프ODT 영수증출력" + " nozzle=" + 
										 QL_wm.getConnectNozzleNo() + "("+QL_wm.getCommand()+")" +
										 " | ODT_No=" + QL_wm.getNozzleNo() +
										 " | mode=" + QL_wm.getMode() +
										 " | alarmState=" + QL_wm.getAlarmState() +
										 " | length=" + QL_wm.getContent().length());
			
			LogUtility.getPumpALogger().debug ("m_rcvEnableFromPOS=" + m_rcvEnableFromPOS +
					" | m_rcvCancelFromPOS=" + m_rcvCancelFromPOS);
			
			if (m_rcvEnableFromPOS==true && m_rcvCancelFromPOS==true) { // 수정 및 추가 (2010/11/18)

				insertDisplayData_23_new("01", "01", (byte)'1', (byte)'1', true, "Preset canceled!");
				insertDisplayData_23_new("02", "01", (byte)'1', (byte)'1', false, "Waiting...");
				
				LogUtility.getPumpALogger().debug ("POS선결제 취소! 영수증 출력안함. nozzle=" + 
						QL_wm.getConnectNozzleNo() + " | ODTNo=" + QL_wm.getNozzleNo());
			}
			else {
				//byte[] byCut = {0x1B, 0x69}; // 영수증 컷팅(full cut)
				byte[] byCut = {0x1B, 0x6D}; // 영수증 컷팅(partial cut)
				String prtData = QL_wm.getContent();
											
				int len = prtData.length();
				if (len > 1610) {
					printReceipt (prtData.substring(0,1610).getBytes(), 0); // 영수증 프린팅 1
					printReceipt (prtData.substring(1610,len).getBytes(), 0); // 영수증 프린팅 2
				} else
					printReceipt (prtData.getBytes(), 0); // 영수증 프린팅
				
				if (QL_wm.getBarCode().length() > 0)
					printReceipt (PumpMessageFormat.getBarcodeFormat(QL_wm.getBarCode()), 0); // Barcode byteStrem
				
				printReceipt (byCut, 0); // Paper cutting
			}
			m_rcvEnableFromPOS=false;
			m_rcvCancelFromPOS=false;
			
			//--- 재승인 실패시 음성안내 기능 추가 (2010/03/16) ---//
			// 관련 수정사항 : 
			//   - QL_WorkingMessage(alarmState 추가)
			//   - Pump module의 ODTUtility_DaSNo(alarmState 값 설정추가)
			if (QL_wm.getAlarmState().equals("1")) { // 재승인 실패 -> 경고안내 출력
				
				insertDevCtrlOrder_42((byte)'0', "00", "02", "000"); // 타이머 중지
				insertDevCtrlOrder_42((byte)'0', "11", "02", "000"); // 음성 중지
				insertDevCtrlOrder_42((byte)'0', "11", "01", "1002324232423242324232423242324232423242324232423242324232423242324232423242324232400");
				insertDisplayData_23_new("01", "01", (byte)'3', (byte)'3', true, ">>  Credit fail!  <<");
				insertDisplayData_23_new("02", "01", (byte)'3', (byte)'3', false,">> Help to Office.<<");
				insertDevCtrlOrder_42((byte)'0', "00", "01", "060"); // 타이머 시작
				
				LogUtility.getPumpALogger().debug ("재승인 실패안내 음성출력! nozzle=" + 
						QL_wm.getConnectNozzleNo() + " | ODTNo=" + QL_wm.getNozzleNo());
			}

			//LogUtility.getPumpALogger().debug("##### print out("+len+" bytes) : \n"+ QL_wm.getContent());
			
			if (QL_wm.getMode().equals("1")) { // 마지막 영수증
				
				//----- TR 전문 생성 -----//
				TR_wm = new TR_WorkingMessage();
				TR_wm.setNozzleNo(QL_wm.getNozzleNo());
				TR_wm.setConnectNozzleNo(QL_wm.getConnectNozzleNo());
				TR_wm.setLiter(m_realLiter);
				TR_wm.setBasePrice(m_realBasePrice);
				TR_wm.setPrice(m_realPrice);
				
				insertRecvQueue(TR_wm);
				
				LogUtility.getPumpALogger().info(new StringBuffer("\n다쓰노셀프ODT 판매완료 전문(TR) 전송 : " )
											.append("\n ODT_No=" ).append( TR_wm.getNozzleNo() )
											.append("\n nozzle=" ).append( TR_wm.getConnectNozzleNo() ).append( "(TR)" )
											.append("\n liter    =" ).append( TR_wm.getLiter() )
											.append("\n basePrice=" ).append( TR_wm.getBasePrice() )
											.append("\n price    =" ).append( TR_wm.getPrice()).toString());
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
			m_isSaveSTX=false;
			
			int lineErrSkipCnt = Change.toValue(P7_wm.getLineErrorSkipCount());
			if (lineErrSkipCnt >= 9000 && lineErrSkipCnt <= 9003)
				dispLevel = lineErrSkipCnt - 9000; // dispLevel : 0 ~ 3
			else
				maxSkipCnt = baseMaxSkipCnt + lineErrSkipCnt;
			
			if (lineErrSkipCnt >= 9005)
				m_isSaveSTX = true;
			
			skip = true;
		}
		
		return skip;
	}
	
	protected void makeCancelInfo() throws Exception {

		CL_wm = new CL_WorkingMessage();

		if (m_targetNozzle!=null)
			CL_wm.setNozzleNo(m_targetNozzle);
		
		insertRecvQueue(CL_wm);
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
		
	protected String makeKeyGrpFile() throws Exception {

		char x0D = 0x0D;
		char x0A = 0x0A;
		
		String fileData="";
		
		if (m_numMPPs==6) { // 6복식
			fileData = "01    " +
			  "999999999101102103" +
			  "999999999999999999" +
			  "301302303304305306" +
			  "401402403404405406" +
			  "999999999999999999" +
			  "999999999999999506" +
			  "999999999999999606" + x0D + x0A;
		}
		else if (m_numMPPs==4) { // 4복식
			fileData = "01    " +
			  "999999101101102102" +
			  "999999999999999999" +
			  "301302303304305306" +
			  "401402403404405406" +
			  "999999999999999999" +
			  "999999999999999506" +
			  "999999999999999606" + x0D + x0A;
		}
		
		return fileData;
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
		int		statusCode=0;
		int		thisNozType=nozType;
		String 	errorMsg="";
		boolean isNozStatus=false;
		
		switch (nozState) {
			case 231:
				statusCode = 231;
				errorMsg = "유종선택 대기";
				thisNozType=2;
				isNozStatus=true;
				break;

			case 232:
				statusCode = 232;
				errorMsg = "금액선택 대기";
				thisNozType=2;
				isNozStatus=true;
				break;

			case 233:
				statusCode = 233;
				errorMsg = "결제입력 대기";
				thisNozType=2;
				isNozStatus=true;
				break;

			case 234:
				statusCode = 234;
				errorMsg = "보너스카드입력 대기";
				thisNozType=2;
				isNozStatus=true;
				break;

			case 235:
				statusCode = 235;
				errorMsg = "주유 대기";
				thisNozType=2;
				isNozStatus=true;
				break;
				
			case 236:
				statusCode = 236;
				errorMsg = "승인요청 중";
				thisNozType=2;
				isNozStatus=true;
				break;
				
			case 650:
				statusCode = 650; // 정상
				errorMsg = "셀프ODT 정상";
				break;
		}
		m_statusCode = statusCode;
		
		if (isNozStatus==true) {
			if (m_targetNozzle!=null)
				S8_wm.setNozzleNo(m_targetNozzle);
		} else
			S8_wm.setNozzleNo(Change.toString("%02d", nozNo));
		
		// 스마트주유소 노즐번호 00 확인용 임시로그
		if (S8_wm.getNozzleNo().equals("00")) {
			LogUtility.getPumpALogger().info("$$$$ nozzle=00 확인");
			LogUtility.getPumpALogger().info("$$$$ isNozStatus   =" + isNozStatus);
			LogUtility.getPumpALogger().info("$$$$ m_targetNozzle=" + m_targetNozzle);
			LogUtility.getPumpALogger().info("$$$$ nozNo         =" + nozNo);
			for (int i=0; i<m_nozNoVec.size(); i++) {
				String noz = m_nozNoVec.get(i);
				LogUtility.getPumpALogger().info("$$$$ m_nozNoVec[" + i + "], noz=" + noz);
			}
		}
		
		S8_wm.setDeviceType(Change.toString("%02d", thisNozType));
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
	
	protected void printReceipt(byte[] pData, int startSequence) throws Exception {

		int nRestLen, nSendLen;
		int nStartPos = 0, nBlockCnt = 0;

		nRestLen = pData.length;

		insertPrintStart (startSequence);

		do {
			if (nRestLen >= 230) {
				nSendLen = 230;
				nRestLen -= nSendLen;
			}
			else {
				nSendLen = nRestLen;
				nRestLen = 0;
			}
			nBlockCnt++;
			insertPrintData (pData, nStartPos, nSendLen, startSequence/* + nBlockCnt*/);
			nStartPos += nSendLen;
		} while (nRestLen > 0);

		insertPrintEnd (nBlockCnt, startSequence/* + nBlockCnt + 1*/);
	}

	protected void processDeviceErr() throws Exception {
		
		insertRecvQueue(generateWorkingMessage(RxBuf, null));	
	}
	
	//------ 입력오더 응답(80) 처리 ------//
	protected void processOPT () throws SerialConnectException, Exception {
		
		if (m_setEnvDataOK == false)
			return;
				
		switch (RxBuf[10]) { //데이터 타입	
		case '0': //입력중지
			break;
			
		case '1': //키 입력
			byte[] byt = new byte[2];
			byt[0]=RxBuf[11];
			byt[1]=RxBuf[12];
			String function=new String (byt);
			
			LogUtility.getPumpALogger().info("\n<<<<Input function. keyNo="+function+" m_nODTState="+m_nODTState+"\n");

			if ((function.equals("00")==true || function.equals("01")==true || function.equals("02")==true || 
					function.equals("0D")==true) && 
					(m_nODTState == 0 || m_nODTState == 1 || m_nODTState == 2)) {

				if (function.equals("0D")==true) { // 취소처리

					m_nODTState = 1;
					initVariables();
					
					insertDisplayData_23_new("01", "01", (byte)'3', (byte)'3', true, "Welcome! GS Caltex.");

					// 원하시는 유종을 선택해 주세요
					insertDevCtrlOrder_42((byte)'0', "11", "01", "100020202020400");
					insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x81); //입력요구
					
					LogUtility.getPumpALogger().info("###### 1 : 취소하였습니다. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
					
					makeStatusInfo (231); // 유종선택 대기(대기모드)
				}
				else {
					m_nODTState = 3;
					initVariables();
					
					// 위치변경 from initVariables() - 10/11/18 (선결제 취소 영수증 처리)
					m_rcvEnableFromPOS = false;
					m_rcvCancelFromPOS = false;
										
					if (function.equals("00"))
						m_targetNozzle = m_nozNoVec.get(0);
					else if (function.equals("01"))
						m_targetNozzle = m_nozNoVec.get(1);
					else if (function.equals("02"))
						m_targetNozzle = m_nozNoVec.get(2);

					m_oilKind = getOilKind(m_targetNozzle);
					m_basePrice = m_basePriceTbl.get(m_targetNozzle);

					// 유종 디스플레이
					String oilName = m_oilNameTbl.get(m_targetNozzle);
					Formatter form = new Formatter();
					form.format("%-14s", oilName);
					String sndStr = form.toString();
					insertDisplayData_23 ("01", "01", sndStr);
					
					if (m_oilKind==DIESEL) { // 경유
						insertDevCtrlOrder_42((byte)'0', "11", "01", "10006070800");
						insertDevCtrlOrder_42((byte)'0', "11", "01", "00506070800");
					}
					else if (m_oilKind==ADIESEL) { // 고급경유 프라임경유
						insertDevCtrlOrder_42((byte)'0', "11", "01", "10036070800");
						insertDevCtrlOrder_42((byte)'0', "11", "01", "00536070800");
					}
					else if (m_oilKind==GASOLINE) { // 무연 휘발유
						insertDevCtrlOrder_42((byte)'0', "11", "01", "10005070800");
						insertDevCtrlOrder_42((byte)'0', "11", "01", "00505070800");
					}
					else if (m_oilKind==PGASOLINE) { // 고급휘발유 킥스프라임
						insertDevCtrlOrder_42((byte)'0', "11", "01", "10035070800");
						insertDevCtrlOrder_42((byte)'0', "11", "01", "00535070800");
					}
					
					// 사람검지 및 타임아웃시 초기화면 진행제어
					insertDevCtrlOrder_42((byte)'0', "00", "02", "000"); // 타이머 중지
					insertDevCtrlOrder_42((byte)'0', "00", "01", "120"); // 타이머 시작
					
					insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x82); //입력요구
									
					LogUtility.getPumpALogger().info("###### 2 : 유종선택하였습니다. m_nODTState=" + m_nODTState + 
							"Oil name="+ oilName + " (ODT="+nozNo+")");

					makeStatusInfo (232); // 금액선택 대기
				}
			}
			else if (m_nODTState == 3 && 
					(function.equals("03") || function.equals("04") || function.equals("05") || 
					 function.equals("06") || function.equals("07") || function.equals("13") || 
					 function.equals("14") || function.equals("15") || function.equals("16") || 
					 function.equals("17") || function.equals("0A") || function.equals("0B") || 
					 function.equals("0D")))
			{
				if (function.equals("0D")) { // 취소
					
					m_nODTState = 1;
					initVariables();

					insertDisplayData_23_new("01", "01", (byte)'3', (byte)'3', true, "Welcome! GS Caltex.");

					// 승인을 받으실수 없습니다. 원하시는 유종을 선택해 주세요
					insertDevCtrlOrder_42((byte)'0', "11", "01", "1002300");
					insertDevCtrlOrder_42((byte)'0', "11", "01", "000020202020400");

					insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x81); //입력요구
					LogUtility.getPumpALogger().info("###### 3 : 취소하였습니다. . m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");

					makeStatusInfo (231); // 유종선택 대기(대기모드)
				}
				else {
					
					m_nODTState = 4;
					String szVoice="";
					String szDisplay="";
					m_liter="0000000";
					boolean selFullPumping=false;

					switch (RxBuf[11]) {
					case '0':
						
						switch (RxBuf[12]) {
						case '3':
							szVoice=m_sMentPriceArr[0][0]; szDisplay=m_sDispPriceArr[0][0]; 
							m_price=m_sPriceButtArr[0][0]; m_preset = PRESET_PRICE;
							break;
						case '4':
							szVoice=m_sMentPriceArr[0][1]; szDisplay=m_sDispPriceArr[0][1]; 
							m_price=m_sPriceButtArr[0][1]; m_preset = PRESET_PRICE;
							break;
						case '5':
							szVoice=m_sMentPriceArr[0][2]; szDisplay=m_sDispPriceArr[0][2]; 
							m_price=m_sPriceButtArr[0][2]; m_preset = PRESET_PRICE;
							break;
						case '6':
							szVoice=m_sMentPriceArr[0][3]; szDisplay=m_sDispPriceArr[0][3]; 
							m_price=m_sPriceButtArr[0][3]; m_preset = PRESET_PRICE;
							break;
						case '7':
							szVoice=m_sMentPriceArr[0][4]; szDisplay=m_sDispPriceArr[0][4]; 
							m_price=m_sPriceButtArr[0][4]; m_preset = PRESET_PRICE;
							break;
						case 'A': 
							szVoice=m_sMentPriceArr[1][4]; szDisplay=m_sDispPriceArr[1][4]; 
							m_price=m_sPriceButtArr[1][4]; m_preset = PRESET_PRICE;
							break;
						case 'B': // 가득주유 버턴 입력
							selFullPumping = true;
							if (m_useFullPumping == true) {
								szVoice=m_sMentPriceArr[1][5]; szDisplay=m_sDispPriceArr[1][5]; 
								m_price=m_sPriceButtArr[1][5]; m_preset = PRESET_PRICE;
							}
							break;
						}
						break;
					case '1':
						switch (RxBuf[12])
						{
						case '3':
							szVoice=m_sMentPriceArr[0][5]; szDisplay=m_sDispPriceArr[0][5]; 
							m_price=m_sPriceButtArr[0][5]; m_preset = PRESET_PRICE;
							break;
						case '4':
							szVoice=m_sMentPriceArr[1][0]; szDisplay=m_sDispPriceArr[1][0]; 
							m_price=m_sPriceButtArr[1][0]; m_preset = PRESET_PRICE;
							break;
						case '5':
							szVoice=m_sMentPriceArr[1][1]; szDisplay=m_sDispPriceArr[1][1]; 
							m_price=m_sPriceButtArr[1][1]; m_preset = PRESET_PRICE;
							break;
						case '6':
							szVoice=m_sMentPriceArr[1][2]; szDisplay=m_sDispPriceArr[1][2]; 
							m_price=m_sPriceButtArr[1][2]; m_preset = PRESET_PRICE;
							break;
						case '7':
							szVoice=m_sMentPriceArr[1][3]; szDisplay=m_sDispPriceArr[1][3]; 
							m_price=m_sPriceButtArr[1][3]; m_preset = PRESET_PRICE;
							break;
						}
						break;
					}
					
					if (selFullPumping==true && m_useFullPumping==false) { // 가득주유 미사용
						m_nODTState = 3;
						
						// 승인을 받으실 수 없습니다. 주유금액을 선택해 주세요.
						insertDevCtrlOrder_42((byte)'0', "11", "01", "10009022300");
						insertDevCtrlOrder_42((byte)'0', "11", "01", "0000700");
						insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x82); //입력요구
						
						makeStatusInfo (232); // 금액입력 대기
						LogUtility.getPumpALogger().info("###### 4 : 가득주유 미사용 상태입니다. m_useFullPumping=" + m_useFullPumping + " (ODT="+nozNo+")");
					}
					else {
						// 신용카드를 넣었다 빼주세요.
						insertDevCtrlOrder_42((byte)'0', "11", "01", "100" + szVoice + "2000");
						insertDevCtrlOrder_42((byte)'0', "11", "01", "005" + szVoice + "2000");
						insertDisplayData_23_new("02", "01", (byte)'3', (byte)'3', false, szDisplay);
	
						insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x90); //입력요구
						LogUtility.getPumpALogger().info("###### 4 : 금액선택하였습니다. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
						
						makeStatusInfo (233); // 결제입력 대기
					}
				}
			}
			else if (m_nODTState == 4 && function.equals("0D")) {
				
				if (function.equals("0D")) { // 취소
					
					m_nODTState = 1;
					initVariables();

					insertDisplayData_23_new("01", "01", (byte)'3', (byte)'3', true, "Welcome! GS Caltex.");

					// 승인을 받으실수 없습니다. 원하시는 유종을 선택해 주세요.
					insertDevCtrlOrder_42((byte)'0', "11", "01", "1002300");
					insertDevCtrlOrder_42((byte)'0', "11", "01", "000020202020400");

					insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x81); //입력요구
					LogUtility.getPumpALogger().info("###### 5 : 취소하였습니다. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");

					makeStatusInfo (231); // 유종선택 대기(대기모드)
				}
			}
			else if (m_nODTState == 5 && 
					(function.equals("0C") || function.equals("0D"))) {
				
				if (function.equals("0D")) { // 취소
					
					m_nODTState = 1;
					initVariables();

					insertDisplayData_23_new("01", "01", (byte)'3', (byte)'3', true, "Welcome! GS Caltex.");

					// 승인을 받으실수 없습니다. 원하시는 유종을 선택해 주세요
					insertDevCtrlOrder_42((byte)'0', "11", "01", "1002300");
					insertDevCtrlOrder_42((byte)'0', "11", "01", "000020202020400");

					insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x81); //입력요구
					LogUtility.getPumpALogger().info("###### 6 : 취소하였습니다. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");

					makeStatusInfo (231); // 유종선택 대기(대기모드)
				}
				else { // 보너스카드 없음
					
					m_nODTState = 6;

					insertDisplayData_23_new("02", "10", (byte)'3', (byte)'3', false, "Waiting...");
					insertDevCtrlOrder_42((byte)'0', "11", "01", "100293000");
					insertDevCtrlOrder_42((byte)'0', "11", "01", "005293000");
					
					//----- 카드승인요청 -----//
					boolean bApprove = requestCardApprove (); 
					
					LogUtility.getPumpALogger().info("###### 7 : 승인 요청하였습니다. : state="+m_nODTState+
							" approve="+bApprove+" m_nODTState="+m_nODTState + " (ODT="+nozNo+")");

					makeStatusInfo (236); // 승인요청 중
				}
			}
			else {

				switch(m_nODTState) {
					case 1 :
						insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x81);
						break;
					case 3 :
						insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x82);
						break;
					case 4 :
						insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x90);
						break;
					case 5 :
						insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0xA0);
						break;
				}
							
				LogUtility.getPumpALogger().info("###### 0 : 예외처리 루틴(1)-----m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
			}
			
			break;
				
		case '2': // 카드 입력
						
			if (m_nODTState == 4)
			{
				if (RxBuf[91]=='0' && RxBuf[92]=='0') {
					
					m_nODTState = 5;
					m_bCreditMode = true;
				
					byte[] byLen = {RxBuf[93], RxBuf[94]};
					String lenStr = new String(byLen);
					int	len = Change.toValue (lenStr);
					
					flushBuffer(m_byCreditNo);
					System.arraycopy(RxBuf, 95, m_byCreditNo, 0, len);
					
					//LogUtility.getPumpALogger().debug("******카드입력 확인 : m_byCreditNo="+new String(m_byCreditNo)+" len="+m_byCreditNo.length);

					int k;
					for (k=0; k<m_byCreditNo.length; k++)
						if (m_byCreditNo[k]==0x00)
							break;
					
					String szCardNo = new String(m_byCreditNo);
					m_creditNo = szCardNo.substring(0,k);
					
					//보너스카드를 넣었다 빼주세요.
					insertDevCtrlOrder_42((byte)'0', "11", "01", "100542600");
					insertDevCtrlOrder_42((byte)'0', "11", "01", "005542600");

					insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0xA0); //입력요구
					
					LogUtility.getPumpALogger().info("###### 7 : 신용카드 입력하였습니다. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
					//LogUtility.getPumpALogger().info("m_byCreditNo="+m_creditNo+" len="+m_creditNo.length());

					makeStatusInfo (234); // 보너스카드입력 대기
				}
				else {
					
					m_nODTState = 1;
					initVariables();

					insertDisplayData_23_new("01", "01", (byte)'3', (byte)'3', true, "Welcome! GS Caltex.");

					// 승인을 받으실수 없습니다. 원하시는 유종을 선택해 주세요
					insertDevCtrlOrder_42((byte)'0', "11", "01", "1002300");
					insertDevCtrlOrder_42((byte)'0', "11", "01", "000020202020400");

					insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x81); //입력요구
					LogUtility.getPumpALogger().info("###### 9 : 신용카드 입력하였습니다. 카드이상으로 승인요청 취소. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");

					makeStatusInfo (231); // 유종선택 대기(대기모드)
				}
			}
			else if (m_nODTState == 5) {
				
				if (RxBuf[91]=='0' && RxBuf[92]=='0') { // 보너스카드 입력
					
					m_nODTState = 6;
					m_bBonusMode = true;

					byte[] byLen = {RxBuf[93], RxBuf[94]};
					String lenStr = new String(byLen);
					int	len = Change.toValue (lenStr);
					flushBuffer(m_byBonusNo);
					System.arraycopy(RxBuf, 95, m_byBonusNo, 0, len);
					m_bonusNo = new String(m_byBonusNo);

					insertDevCtrlOrder_42((byte)'0', "11", "01", "100293000");
					insertDevCtrlOrder_42((byte)'0', "11", "01", "005293000");
					insertDisplayData_23_new("02", "10", (byte)'3', (byte)'3', false, "Waiting...");

					LogUtility.getPumpALogger().info("###### 10 : 보너스카드 입력하였습니다. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
					//LogUtility.getPumpALogger().info("보너스카드 번호="+m_bonusNo+" len="+m_bonusNo.length());

					//----- 카드승인요청 -----//
					boolean bApprove = requestCardApprove ();
					
					LogUtility.getPumpALogger().info("###### 11 : 승인 요청하였습니다. : state="+m_nODTState+ " approve="+bApprove+
							" m_nODTState="+m_nODTState + " (ODT="+nozNo+")");

					makeStatusInfo (236); // 승인요청 중
				}
				else {

					m_nODTState = 1;
					initVariables();

					insertDisplayData_23_new("01", "01", (byte)'3', (byte)'3', true, "Welcome! GS Caltex.");

					// 승인을 받으실수 없습니다. 원하시는 유종을 선택해 주세요
					insertDevCtrlOrder_42((byte)'0', "11", "01", "1002300");
					insertDevCtrlOrder_42((byte)'0', "11", "01", "000020202020400");

					insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x81); //입력요구
					LogUtility.getPumpALogger().info("###### 12 : 보너스카드 입력하였습니다->카드이상으로 요청취소. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");

					makeStatusInfo (231); // 유종선택 대기(대기모드)
				}
			}
			else {
				
				switch(m_nODTState) {
					case 1 :
						insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x81);
						break;
					case 3 :
						insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x82);
						break;
					case 4 :
						insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x90);
						break;
					case 5 :
						insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0xA0);
						break;
				}
								
				LogUtility.getPumpALogger().info("###### 0 : 예외처리 루틴(2)-----m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
			}
			
			break;
		}
	}

	protected void processRecvSTX () throws SerialConnectException, Exception {
			
		//############# State check ##############//
		if (RxBuf[3]=='6' && RxBuf[4]=='0') { // 파일/데이터 수신응답

			recvFileStatus();
		}
		else if (RxBuf[3]=='6' && RxBuf[4]=='1') { // OPT 상태 통지

			makeStatusInfo (650); // 상태정보전송(정상)
			
			switch (RxBuf[6]) // 동작모드 ('1'=Initial mode, '2'=POS mode)
			{
			case '1': // Initial mode (환경설정)

				insertInitialFileDemand();
				insertOPTFile();
				
				LogUtility.getPumpALogger().info("\n######### Completed Tatsuno Self ODT Initializtion : ODT=" +
												nozStr + "(MPP" + m_numMPPs + ") #########\n");

				break;
			case '2': // POS mode (최초 실행)

				m_nODTState = 1;
				initVariables();
				insertInitCmd(); // 반복
				//insertInitCmd();
				
				insertOPTStatus ((byte)'0');
				
				insertDevCtrlOrder_42((byte)'0', "11", "01", "100020202020400"); // 원하시는 유종을 선택해 주세요			
				insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x81); // 램프(유종선택)
				insertDisplayData_23_new("01", "01", (byte)'3', (byte)'3', true, "Welcome! GS Caltex.");
				
				LogUtility.getPumpALogger().info("###### 0 : 초기실행 하였습니다. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
				
				break;
			}
		}
		else if (RxBuf[3]=='8' && RxBuf[4]=='0') { // 입력오더 응답

			processOPT (); // 입력오더 데이터 처리
		}
		else if (RxBuf[3]=='8' && RxBuf[4]=='1') { // OPT 에러 통지

			processDeviceErr (); 
		}
		else if (RxBuf[3]=='8' && RxBuf[4]=='2') { // 디바이스 제어응답

			if (RxBuf[6]=='1' && RxBuf[7]=='5') { // 사람검지센서

				if (RxBuf[8]=='0' && RxBuf[9]=='1') { // 검지상태
					
					if (m_nODTState == 1) {  // 수정(09/04/09), 승인요청 이전

					   insertInitCmd();
					   
					   insertDisplayData_23_new("01", "01", (byte)'3', (byte)'3', true, "Welcome! GS Caltex.");
					   insertDevCtrlOrder_42((byte)'0', "11", "01", "1000203533900");
					   insertDevCtrlOrder_42((byte)'0', "11", "01", "000020202020400");

					   makeStatusInfo (231); // 유종선택 대기(대기모드)
					   insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x81); //입력요구
				   	}
				   	LogUtility.getPumpALogger().info("###### 0 : 사람 검지하였습니다. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
				}
				else {
					LogUtility.getPumpALogger().info("###### 0 : 사람검지센서 작동하였습니다. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
				}
			}
			else if (RxBuf[6]=='0' && RxBuf[7]=='0') // 타이머
			{
//				LogUtility.getPumpALogger().info("타이머 작동 >>>>>>>>>>>>>>>>>>>>>");
//				Log.datas(RxBuf, 40, 20);
				
				if (RxBuf[10]=='0' && RxBuf[11]=='0') { // 카운트 종료
					
					insertInitCmd();
					
					if (m_nODTState == 8) { // 8 = 주유완료(프린터 출력중)
						
						m_nODTState = 1;  
						insertDevCtrlOrder_42((byte)'0', "00", "02", "000"); // 타이머 중지																
					} 
					
					if (m_nODTState <= 5) { // 5 = 보너스카드/현금영수증카드 삽입 대기
	
						m_nODTState = 1; // 설정한 타임동안 입력이 없을경우 처음(유종선택)으로 복귀
						initVariables();

						insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x81);
						insertDevCtrlOrder_42((byte)'0', "11", "01", "10002030400");
						insertDisplayData_23_new("01", "01", (byte)'3', (byte)'3', true, "Welcome! GS Caltex.");
						insertDevCtrlOrder_42((byte)'0', "11", "01", "005030400");
	
						makeStatusInfo (231); // 유종선택 대기(대기모드)
						
						byte[] byt1 = new byte[2];										
						byt1[0] = RxBuf[10];
						byt1[1] = RxBuf[11];
						LogUtility.getPumpALogger().info("###### 0 : 디바이스 타이머 응답 : state=" + new String(byt1) + 
								" m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
					}
				}
			}
			else if (RxBuf[6]=='0' && RxBuf[7]=='2') // 램프
			{
				byte[] byt1 = new byte[2];
				byt1[0] = RxBuf[10]; byt1[1] = RxBuf[11];
				LogUtility.getPumpALogger().info("###### 0 : 디바이스 램프 응답 : state=" + new String(byt1) +
						" m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
			}
		}
	}
	

	protected byte[] protectCardNumber (byte[] buf) throws Exception, SerialConnectException {
		
		byte[] dat = buf.clone();
		
		if (dat[3]=='8' && dat[4]=='0') {
			
			if (dat[10] == '2') { // 카드입력
				for (int i=95+4; i<95+12; i++)
					dat[i] = '*';
				
				for (int i=95+17; i<95+33; i++)
					dat[i] = '*';
			}
			
			//Log.datas(dat, dat.length, 20);
		}
				
		return dat;
	}

	protected void recvFileStatus() throws Exception {

		boolean bStatus = (RxBuf[6]=='0' && RxBuf[7]=='0');

		if (bStatus==false)
		{
			int nStat1 = RxBuf[11] - 0x30;
			int nStat2 = RxBuf[12] - 0x30;
			
			if (nStat1 > 9 || nStat2 > 9) {
				insertInitialFileDemand ();
				insertOPTFile ();
			}

			switch (RxBuf[7])
			{
			case '1':
			case '2':
			case '3':
			case '4':
				if (RxBuf[11]=='0' && RxBuf[12]=='1')
					insertOPTFile ();
				else if (RxBuf[11]=='0' && RxBuf[12]=='2')
					insertKeyGrpFile ();
				else if (RxBuf[11]=='0' && RxBuf[12]=='5')
					insertKeyFile ();
				else if (RxBuf[11]=='0' && RxBuf[12]=='6')
					insertStampFile ();
				else if (RxBuf[11]=='0' && RxBuf[12]=='8')
					insertCommFile ();
				break;
			}
			return;
		}
		
		if (RxBuf[11]=='0' && RxBuf[12]=='1')
			insertKeyGrpFile ();
		else if (RxBuf[11]=='0' && RxBuf[12]=='2')
			insertKeyFile ();
		else if (RxBuf[11]=='0' && RxBuf[12]=='5')
			insertStampFile ();
		else if (RxBuf[11]=='0' && RxBuf[12]=='6')
			insertCommFile ();
		//else if (RxBuf[13]=='0' && RxBuf[14]=='8')
			//0;	//??
	}

	protected boolean recvTail_proc () throws Exception, SerialConnectException {
		
		//--- send : ACK ---//
		if (sendText (ACK1)==false) {
			if (dispLevel>=3)
				LogUtility.getPumpALogger().info("S5.Send ACK1 fail! (ODT="+nozNo+")");
			return false;
		}
		
		if (dispLevel>=3)
			LogUtility.getPumpALogger().info("S5.Send ACK1 (ODT="+nozNo+")");

		//--- recv : EOT ---//
		if (recvText(RxBuf) < 1) {
			if (dispLevel>=3)
				LogUtility.getPumpALogger().info("S5.Recv EOT fail! (ODT="+nozNo+")");
			return false;
		}
	
		if (RxBuf[0] == EOT) {	//--- recv : EOT ---//
			if (dispLevel>=3)
				LogUtility.getPumpALogger().info("S5.Recv EOT (ODT="+nozNo+")");
			//return true;
		}
		else {
			if (dispLevel>=3)
				LogUtility.getPumpALogger().info("S5.Recv EOT fail (ODT="+nozNo+")");
			return false;
		}
		
		return true;
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

	protected boolean requestCardApprove () throws Exception {
		
		HE_wm = new HE_WorkingMessage();
		HE_wm.setNozzleNo(nozStr); // ODTNo
		HE_wm.setConnectNozzleNo(m_targetNozzle); // nozNo
		HE_wm.setCardType("1");
		HE_wm.setCardNumber(m_creditNo);
		HE_wm.setBonusCard(m_bonusNo);
		HE_wm.setLiter(m_liter);
		HE_wm.setBasePrice(m_basePrice);
		HE_wm.setPrice(m_price);
		HE_wm.setCustomerType("1"); // 1=일반
		
		if (m_price.equals("00149900"))
			HE_wm.setIsFullPumping("1"); // 가득주유
		else
			HE_wm.setIsFullPumping("0");
		                  
		LogUtility.getPumpALogger().info(new StringBuffer("\n다쓰노셀프ODT 승인요청 전문(HE)" )
									.append("\n ODT_No=" ).append( HE_wm.getNozzleNo() )
									.append("\n nozzle=" ).append( HE_wm.getConnectNozzleNo() ).append( "(HE)" )
									.append("\n type     =" ).append( HE_wm.getCardType() )
									.append("\n liter    =" ).append( HE_wm.getLiter() )
									.append("\n basePrice=" ).append( HE_wm.getBasePrice() )
									.append("\n price    =" ).append( HE_wm.getPrice() )
									.append("\n creditNo =" ).append( Base64Util.encode(getCardTrack1Data(HE_wm.getCardNumber())) )
									.append("\n bonusNo  =" ).append(  Base64Util.encode(getCardTrack1Data(HE_wm.getBonusCard())) ) 
									.append("\n isFullPmp=" ).append( HE_wm.getIsFullPumping() )
									.append("\n custType =" ).append( HE_wm.getCustomerType() ).append("\n").toString());	
		
		insertRecvQueue(HE_wm);
		
		insertDevCtrlOrder_42((byte)'0', "00", "02", "000"); // 타이머 중지 
		insertDevCtrlOrder_42((byte)'1', "02", "01", "00000000"+"0000000000000000");
		insertInitCmd();
		
		m_bFromODT=true;
				
		return true;
	}
	
	@Override
	public void requestData() throws Exception, SerialConnectException {

		int 	numBytes=0, loopCnt=0;
		boolean	occurLineErr = (lineErrCnt >= minErrCnt);
		
		if (occurLineErr) { // Check line communication error

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
		
		// 회선불량 복구 처리
		if (firstRequest==true) {
			firstRequest=false;
			TxQue.enQueue(t41_Buf); // 상태정보요청
		}
		if (m_statusCode==601) { // 회선불량
			if (lineCommCheckCnt >= (minErrCnt+5)) {
				lineCommCheckCnt=0;
				TxQue.enQueueNewer(t41_Buf); // 상태정보요청
			}
			lineCommCheckCnt++;
		}
		
		flushBuffer(RxBuf);
		
		//--- Transfer from WorkingMessage to BytesStream
		while (sndQue.getItemCount() > 0) {
			WorkingMessage wm = (WorkingMessage)sndQue.deQueue();
			
//			LogUtility.getPumpALogger().debug("\n>>>>> Received  Down data : " +
//								"ODT=" + nozNo + " command=" + wm.getCommand());
			
			if (isSkipWorkingMessage(wm)==true) {
				wm=null;
				continue;
			}
			
			byte[] wmByte = generateByteStream(wm);
			
			if (wmByte != null) {
				if (chkEnvDataAndSaving(wmByte) == false) // If EnvData then saving to dataStruct
					TxQue.enQueue(wmByte); // 환경설정정보가 아니면
			}
			/*
			if (wm.getCommand().equals("PB")) 
				Show.datas(wmByte, wmByte.length, 20);
			*/			
			wm=null;
		}
		if (dispLevel>=3)
			LogUtility.getPumpALogger().debug("\nStart request ===========> TatsunoSelfODT (Noz="+nozNo+")");

		mode = POL;
		while (true) {

			flushBuffer(RxBuf);
			
			if (loopCnt >= 2) break;
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
					lineErrCnt++;
					if (dispLevel>=2)
						LogUtility.getPumpALogger().info("0.Recv ACK/STX fail!(No response) lineErrCnt="+lineErrCnt+"/"+minErrCnt+" (ODT="+nozNo+")");
					else if (lineErrCnt >= minErrCnt) {
						if (m_nNoResponseCnt%4==0) {
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
							LogUtility.getPumpALogger().info("1.Recv STX(Data) (ODT="+nozNo+")");
							Log.datas(RxBuf, 80, 20);
						}
						
						if (compareBCC(RxBuf)==false) {
							sendText (NAK);
							LogUtility.getPumpALogger().info ("1.Recv STX BCC fail! (ODT="+nozNo+")");
							Log.datas (RxBuf, 80, 20);
							continue;
						}
						else {
							if (dispLevel==1 || dispLevel==2) { 
								LogUtility.getPumpALogger().info ("1.Recv STX with normal (ODT="+nozNo+")");
								Log.datas(RxBuf, 80, 20);
							}

							if (m_isSaveSTX==true) 
								LogUtility.getPumpALogger().info("Recv STX("+Change.toString("%02d", nozNo)+
										") : ["+pack(protectCardNumber(RxBuf), ETX)+"]");
							else
								LogUtility.getPumpALogger().debug("Recv STX("+Change.toString("%02d", nozNo)+
										") : ["+pack(protectCardNumber(RxBuf), ETX)+"]");
							
							processRecvSTX(); // 수신 데이터 처리
							
							if (recvTail_proc()==false) 
								lineErrCnt++;
							else
								lineErrCnt=0; // Normal terminated
						}
					}
					else if (RxBuf[0] == EOT) {
						if (dispLevel>=3)
							LogUtility.getPumpALogger().info("1.Recv EOT (ODT="+nozNo+")");
						lineErrCnt=0; // Normal terminated
						continue;
					} else {
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
						if (RxBuf[0]==0x30) { // recv : ACK0

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

								if (recvText(RxBuf) < 1) {
									if (dispLevel>=3)
										LogUtility.getPumpALogger().info("2.Recv ACK1 fail! (ODT="+nozNo+")");
									lineErrCnt++;
									continue;
								}

								if (RxBuf[0]==ACK) {
									is.read (RxBuf,0,1);
									if (RxBuf[0]==0x31) { // recv : ACK1
										
										if (m_isSaveSTX==true) 
											LogUtility.getPumpALogger().info("Send STX("+Change.toString("%02d", nozNo)+
													") : ["+new String(protectCardNumber(TxBuf))+"]");
										else
											LogUtility.getPumpALogger().debug("Send STX("+Change.toString("%02d", nozNo)+
													") : ["+new String(protectCardNumber(TxBuf))+"]");
										
										TxQue.deQueue(); // remove item
										
										if (dispLevel>=3)
											LogUtility.getPumpALogger().info("2.Recv ACK1 (ODT="+nozNo+")");
										
										if (dispLevel==2) {
											LogUtility.getPumpALogger().info("2.Send STX with normal (ODT="+nozNo+") itemCount="+TxQue.getItemCount());
											Log.datas(TxBuf, TxBuf.length, 20);
										}

//										byte[] bbb = new byte[2]; // Temp...
//										bbb[0]=TxBuf[3];
//										bbb[1]=TxBuf[4];
//										LogUtility.getPumpALogger().debug("<<<<Sent command "+new String(bbb)+". itemCount=" + 
//												TxQue.getItemCount() + " (ODT="+nozNo+")");
										
										sendTail_proc(); 
										lineErrCnt=0; // Normal terminated
									}
								}
								else if (dispLevel>=3) {
									LogUtility.getPumpALogger().info("2.Recv ACK1 fail! (ODT="+nozNo+")");
									lineErrCnt++;
								}
							}
						}
					}
					else if (RxBuf[0] == EOT) {
						if (dispLevel>=3) {
							LogUtility.getPumpALogger().info("2.Recv EOT (ODT="+nozNo+")");
						}
						lineErrCnt=0; // Normal terminated
						continue;
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
}
