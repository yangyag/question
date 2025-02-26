package com.gsc.kixxhub.device.pumpa.driverVersion;

import gnu.io.SerialPortEvent;

import java.io.IOException;
import java.util.Calendar;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.Vector;

import com.gsc.kixxhub.common.data.pump.HA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HC_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HD_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P5_1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P5_NozzleInfo;
import com.gsc.kixxhub.common.data.pump.P7_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S4_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S8_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SE_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.TR_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.Base64Util;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.BytesQue2;
import com.gsc.kixxhub.device.pumpa.common.Change;
import com.gsc.kixxhub.device.pumpa.common.DataStruct;
import com.gsc.kixxhub.device.pumpa.common.Log;
import com.gsc.kixxhub.device.pumpa.common.SerialConnectException;
import com.gsc.kixxhub.device.pumpa.common.Sleep;
import com.gsc.kixxhub.device.pumpa.driver.CommSomoSelfN;
import com.gsc.kixxhub.device.pumpa.translation.TransSomoSelfN;

public class CommSomoSelfN_401 extends CommSomoSelfN {

    protected static boolean m_isSaveSTX=false;
	protected byte  	ACK  = 0x10;
    protected byte  	SEL  = 0x41;
    protected byte  	STX  = 0x02;
	protected byte[] 	ACK0 = new byte[3];
	protected byte[] 	ACK1 = new byte[3];
	protected int		baseMinErrCnt=5, baseMaxSkipCnt=100; // <- 200
	protected int		baseReadBuffInterval   	= 100+10; // ����Ʈ������ PJT ����
	// ODT ������� �������� �ð��� ���� (2011.05.25)
	protected int		baseReadStartInterval  	= 20+4; // ����Ʈ������ PJT ����
	protected int		baseWriteStartInterval 	= 10+3; // ����Ʈ������ PJT ����
	protected int 	dispLevel=0;
	protected byte  	ENQ  = 0x05;

	protected byte[] 	enq_Buf = new byte[4];
	
    protected byte[] 	ENQb = new byte[4];
    protected byte  	EOT  = 0x04;
    protected byte[]  	EOTb = {EOT, 0};
    
    protected byte  	ETX  = 0x03;
	protected boolean firstRequest=true;
	protected HA_WorkingMessage HA_wm;
	protected HB_WorkingMessage HB_wm;
	
	protected HC_WorkingMessage HC_wm;
	protected HD_WorkingMessage HD_wm;
    protected boolean	issueLineErr	=true;
	protected int		lineCommCheckCnt=0;
	protected int		lineErrCnt		=0;
	protected boolean	m_bSendSuccess=false;
	protected boolean	m_changeBasePrice=false;
	protected boolean	m_isCompleteBasePrice=false;
	protected int		m_nODTState=0;
	protected int		m_nRequestNerailNo=0;
	protected String 	m_realBasePrice	="000000"; 		// �� �����ܰ�
	protected String 	m_realLiter			="0000000";    	// �� ������
	protected String 	m_realPrice		="00000000";   // �� �����ݾ�
	protected boolean	m_setEnvDataOK=false;
	protected int		m_statusCode=601;
	protected byte  	mode = SEL;

    protected byte  	NAK  = 0x15;

	protected byte[]  	NAKb = {NAK, 0};
	protected byte		nozID;
	protected short		nozState			=0;
    
    //protected byte[] 	lastPumpingData=new byte[buffSize];
	protected String 	nozStr = "";
    protected PB_WorkingMessage PB_wm;
    protected byte  	POL  = 0x51;
    protected byte  	PREFIX_ENQ = 0x05;
    //progressStep : 0=�ʱ�ȭ��, 1=�ʱ�ȭ��, 2=���������۽���, 3=������, 4=�����Ϸ�, 
    //                         5=���а���������/�����Ϸ��ڷ� �۽�
    protected int 	progressStep=3;
    protected byte[] 	rcvInitBuf = new byte[6];
    protected byte[] 	RxBuf = new byte[1024];
    protected S4_WorkingMessage S4_wm;
    protected S8_WorkingMessage S8_wm;
    protected SE_WorkingMessage SE_wm;


    protected byte[] 	t0201_Buf;
    protected DataStruct t0201_ds 	= new DataStruct();
    protected byte[] 	t0202_Buf;
    
    protected DataStruct t0202_ds 	= new DataStruct();
    protected byte[] 	t0205_Buf;
    protected DataStruct t0205_ds 	= new DataStruct();
    protected byte[] 	t0206_Buf;
    protected DataStruct t0206_ds 	= new DataStruct();
    protected byte[] 	t02eb_Buf;
    protected DataStruct t02eb_ds 	= new DataStruct();
    protected byte[] 	t02sb_Buf;
    protected DataStruct t02sb_ds 	= new DataStruct();
    protected byte[] 	t40_Buf;
    protected DataStruct t40_ds = new DataStruct();
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

    protected byte[] 	t59_Buf;
    protected DataStruct t59_ds = new DataStruct();
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
	
    public CommSomoSelfN_401 (int nozNum, String romVerStr) {
    	
    	super(nozNum, romVerStr); // ���߿� �̰͸� ������ 
    	
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

		readBuffInterval   	= baseReadBuffInterval;
		readStartInterval  	= baseReadStartInterval;
		writeStartInterval 	= baseWriteStartInterval;
		minErrCnt 		  		= baseMinErrCnt;
		maxSkipCnt 		   	= baseMaxSkipCnt;
		
		try {
			//--- ȯ�漳�� ����(Start block) ---//
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
			
			//--- ȯ�漳�� ����(����� ����) ---//
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

			//--- ȯ�漳�� ����(�Ӹ���/������) ---//
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

			//--- ȯ�漳�� ����(������ ����) ---//
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
			
			//--- ȯ�漳�� ����(�ý��� �ð�) ---//
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
			
			//--- ȯ�漳�� ����(End block) ---//
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
			t02_ds.addString("terminalSN", "", 2); // ������ Ȯ�ο��
			t02_ds.addString("terminalID", "", 2); // ������ Ȯ�ο��
			t02_ds.addByte  ("ETX", (byte) ETX);
			t02_ds.addByte  ("BCC", (byte) ' ');
			t02_Buf = t02_ds.getByteStream();
			*/
			
			/*
			//--- �ð����� ���� ---//
			t22_ds.addByte  ("STX", (byte) STX);
			t22_ds.addByte  ("SA", nozID);
			t22_ds.addByte  ("UA", (byte) SEL);
			t22_ds.addString("command", "22", 2);
			t22_ds.addString("systemTime", "", 14);
			t22_ds.addByte  ("ETX", (byte) ETX);
			t22_ds.addByte  ("BCC", (byte) ' ');
			t22_Buf = t22_ds.getByteStream();
			*/
			
			//--- ����������û ---//
			t40_ds.addByte  ("STX", STX);
			t40_ds.addByte  ("SA", nozID);
			t40_ds.addByte  ("UA", SEL);
			t40_ds.addString("command", "40", 2);
			t40_ds.addByte  ("ETX", ETX);
			t40_ds.addByte  ("BCC", (byte) ' ');
			t40_Buf = t40_ds.getByteStream();
			
			//--- ������ ���� ---//
			t50_ds.addByte  ("STX", STX);
			t50_ds.addByte  ("SA", nozID);
			t50_ds.addByte  ("UA", SEL);
			t50_ds.addString("command", "50", 2);
			t50_ds.addString("nozNo", "", 2);
			t50_ds.addString("nozState", "", 2);
			t50_ds.addByte  ("ETX", ETX);
			t50_ds.addByte  ("BCC", (byte) ' ');
			t50_Buf = t50_ds.getByteStream();

			//--- ������/�����Ϸ� ---//
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
			
			//--- VAN, TMS ������������ ---//
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
			t52_ds.addString("receiptLiter", "", 7); // ��ǥ��
			t52_ds.addString("saleType", "", 1);
			t52_ds.addString("customerType", "", 1);
			t52_ds.addString("SaleState", "", 1);
			t52_ds.addString("basePricePrint", "", 1); // �ܰ� ��¿���
			t52_ds.addString("depositReceipt", "", 1); // ������ ���࿩��
			t52_ds.addString("underData", "", 1); // �Ҽ��� ó�����
			t52_ds.addString("receiptType", "", 1); // ��꼭 �ŷ� ����
			t52_ds.addString("maxLiter", "", 10); // �ѵ�����
			t52_ds.addString("totalLiter", "", 10); // ���� ����
			t52_ds.addString("maxType", "", 1); // �ѵ�����
			t52_ds.addByte  ("ETX", ETX);
			t52_ds.addByte  ("BCC", (byte) ' ');
			t52_Buf = t52_ds.getByteStream();
			
			//--- �Ϸ�/���� ���� Ȯ�� ---//
			t54_ds.addByte  ("STX", STX);
			t54_ds.addByte  ("SA", nozID);
			t54_ds.addByte  ("UA", SEL);
			t54_ds.addString("command", "54", 2);
			t54_ds.addString("state", "", 1);
			t54_ds.addByte  ("ETX", ETX);
			t54_ds.addByte  ("BCC", (byte) ' ');
			t54_Buf = t54_ds.getByteStream();
			
			//--- �� ���� ���� ---//
			t55_ds.addByte  ("STX", STX);
			t55_ds.addByte  ("SA", nozID);
			t55_ds.addByte  ("UA", SEL);
			t55_ds.addString("command", "55", 2);
			t55_ds.addString("nozNo", "", 2);
			t55_ds.addString("type", "", 1);
			t55_ds.addString("liter", "", 7);
			t55_ds.addString("basePrice", "", 4);
			t55_ds.addString("price", "", 6);
			t55_ds.addString("barcode", "", 16);			// 2012.07.19 ksm
			t55_ds.addByte  ("ETX", ETX);
			t55_ds.addByte  ("BCC", (byte) ' ');
			t55_Buf = t55_ds.getByteStream();
			
			//--- ����, ������ �������� ---//
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

			//--- ���� ���� ��� ---//
			t61_ds.addByte  ("STX", STX);
			t61_ds.addByte  ("SA", nozID);
			t61_ds.addByte  ("UA", SEL);
			t61_ds.addString("command", "61", 2);
			t61_ds.addString("mode", "", 1);
			t61_ds.addString("fileState", "", 20);
			t61_ds.addByte  ("ETX", ETX);
			t61_ds.addByte  ("BCC", (byte) ' ');
			t61_Buf = t61_ds.getByteStream();
			
			//--- �������� �������� ---//
			t59_ds.addByte  ("STX", STX);
			t59_ds.addByte  ("SA", nozID);
			t59_ds.addByte  ("UA", SEL);
			t59_ds.addString("command", "59", 2);
			t59_ds.addString("sequence", "1", 1);
			t59_ds.addString("reAuthType", "9", 1); // 1:����-����-���, 2: ����-���-����, 9: �������
			t59_ds.addString("useFullPumping", "9", 1); // 0: ����, 1: ���, 9: �������
			t59_ds.addString("fullPumpingAuthType", "9", 1); // 1:B/Lüũ���, 2:�����ι��, 9: �������
			t59_ds.addString("preAuthPrice", "99999999", 8); // �������� ������ �ݾ� -> 99999999: �������
			t59_ds.addByte  ("ETX", ETX);
			t59_ds.addByte  ("BCC", (byte) ' ');
			t59_Buf = t59_ds.getByteStream();

			//--- �������� ---//
			//--- �Է� ���� ���� ---//
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
    
	@Override
	protected boolean chkEnvDataAndSaving (byte[] dat) throws Exception {
		
		boolean envDataFlag=false;
		
		if (dat[3]=='0' && dat[4]=='2') {
			
			switch (dat[8]) {
			case '1' :
				t0201_Buf = dat.clone(); // ����� ����
				envDataFlag = true;

				LogUtility.getPumpALogger().info("ȯ�漳������(����� ����) ���� > ����. (ODT="+nozNo+")");
				//Log.datas(t0201_Buf, t0201_Buf.length, 20);
				
				break;
			case '2' :
				t0202_Buf = dat.clone(); // �Ӹ���/������
				envDataFlag = true;

				LogUtility.getPumpALogger().info("ȯ�漳������(�Ӹ���/������) ���� > ����. (ODT="+nozNo+")");
				//Log.datas(t0202_Buf, t0202_Buf.length, 20);
				
				break;
			case '5' :
				t0205_Buf = dat.clone(); // ������ ����
				if (m_changeBasePrice==true) {
					envDataFlag = false; // �ܰ����� ������ ���Ͽ� �����⿡ ����
					m_changeBasePrice = false;
				} else
					envDataFlag = true; // �����⿡ ���۾���

				LogUtility.getPumpALogger().info("ȯ�漳������(������ ����) > ����. (ODT="+nozNo+")");
				//Log.datas(t0205_Buf, t0205_Buf.length, 20);
				
				break;
			}
		}
		
		return envDataFlag;
	}
	
	@Override
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
	
	@Override
	protected boolean compareNozID(byte[] buf) throws Exception {

		if (RxBuf[1]==nozID) {
			//LogUtility.getPumpALogger().debug("compareNozID -> true, RxBuf[1]="+RxBuf[1]+" nozID="+nozID);
			return true;
		} else {
			//LogUtility.getPumpALogger().debug("compareNozID -> false, RxBuf[1]="+RxBuf[1]+" nozID="+nozID);
			return false;
		}
	}

	@Override
	protected byte[] generateByteStream (WorkingMessage wm) throws Exception {	
		
		//if (wm.getCommand().equals("S4")) {
			//S4_WorkingMessage rwm = (S4_WorkingMessage) wm;
			//System.out.printf ("SSSSSSS------> cmd=%s noz=%s conNoz=%s\n\n", 
					//wm.getCommand(), wm.getNozzleNo(), wm.getConnectNozzleNo());
		//}
		
		return trans.generateByteStream(wm);
	}

	@Override
	protected WorkingMessage generateWorkingMessage (byte[] buf, String command) throws Exception {
		return trans.generateWorkingMessage(buf, command);
	}
	
	@Override
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
	
	@Override
	protected String getCommand(byte[] buf) throws Exception {
		
		byte[] cmd = new byte[2];
		cmd[0] = buf[0];
		cmd[1] = buf[1];
		
		return cmd.toString();
	}
	
	@Override
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
	
	@Override
	protected void insertEnvDataToTxQue (byte[] envDat, String fileNo) throws Exception {
		
		t02sb_ds.editString("fileNo", fileNo, 2); // Start block
		t02sb_Buf = t02sb_ds.getByteStream();

		//LogUtility.getPumpALogger().debug("##### insert EnvData Start block : fileNo=" + fileNo + "(ODT="+nozNo+")");
		//Log.datas (t02sb_Buf, t02sb_Buf.length, 20);
		
		t02eb_ds.editString("fileNo", fileNo, 2); // End block
		t02eb_Buf = t02eb_ds.getByteStream();
		
		//LogUtility.getPumpALogger().debug("##### insert EnvData Data block" + "(ODT="+nozNo+")");
		//Log.datas (envDat, envDat.length, 20);

		//LogUtility.getPumpALogger().debug("##### insert EnvData End block" + "(ODT="+nozNo+")");
		//Log.datas (t02eb_Buf, t02eb_Buf.length, 20);
		
		TxQue.enQueue(t02sb_Buf); // Start block
		TxQue.enQueue(envDat); 	  // Data block
		TxQue.enQueue(t02eb_Buf); // End block

		//LogUtility.getPumpALogger().debug("##### insert EnvData End block : itemCount=" + TxQue.getItemCount()+ "(ODT="+nozNo+")");
	}
	
	protected boolean isCompleteBasePrice (P5_1_WorkingMessage P5_1_wm) throws Exception {
		
		Vector<P5_NozzleInfo> nozInfoVec = P5_1_wm.getNozzleInfo();
		int nozCnt = nozInfoVec.size();
		
		for (int i=0; i<nozCnt; i++) {
			P5_NozzleInfo P5_nozWm = nozInfoVec.get(i);
			if (Change.toValue(P5_nozWm.getBasePrice()) <= 0) // �ܰ�=0 �� 1���� ������
				return false;
		}
		
		return true;
	}
	
	protected boolean isExistCertiInfo () throws Exception {
		
		for (int i=0; i<TxQue.getItemCount(); i++) {

			TxBuf = TxQue.getFirstItem();
			if (TxBuf[3]=='5' && TxBuf[3]=='2') // ���������� ������
				return true;
		}
		
		return false;
	}
	
	@Override
	protected boolean isSkipWorkingMessage (WorkingMessage wm) throws Exception {
		
		boolean skip=false;
		String bPrice;
		String strWMmessage = wm.getCommand();
		
		//if (wm.getCommand().equals("HC")) { // �ܻ�� ��������
		   if ("HC".equals(strWMmessage)) { // �ܻ�� ��������	
			HC_wm = (HC_WorkingMessage) wm;
			
			LogUtility.getPumpALogger().info("[Pump A][����-�Ҹ���ODT ��������]" + " nozzle=" + 
										HC_wm.getConnectNozzleNo() + "("+HC_wm.getCommand()+")" +
										" | ODT_No=" + HC_wm.getNozzleNo() +
										" | mode=" + HC_wm.getMode() + "(1:����, 2:�ź�, 3:������, 4:���, 5:��Ұź�, 6:��ҽ���)");
			
			HC_wm.print();
			skip = false;
		}
		else if ("HD".equals(strWMmessage)) { // �ܻ� ��������
			HD_wm = (HD_WorkingMessage) wm;
			
			LogUtility.getPumpALogger().info("[Pump A][����-�Ҹ���ODT ��������]" + " nozzle=" + 
									    HD_wm.getConnectNozzleNo() + "("+HD_wm.getCommand()+")" +
										" | ODT_No=" + HD_wm.getNozzleNo());
			
			HD_wm.print();
			skip = false;
		}
		else if ("S4".equals(strWMmessage)) { // �����Ϸ�

			S4_wm = (S4_WorkingMessage) wm;

			m_realPrice     = S4_wm.getPrice();
			m_realLiter     = S4_wm.getLiter();
			m_realBasePrice = S4_wm.getBasePrice();

			LogUtility.getPumpALogger().info("[Pump A][����-�Ҹ���ODT �����Ϸ�]" + " nozzle=" + 
										S4_wm.getConnectNozzleNo() + "("+S4_wm.getCommand()+")" +
										" | ODT_No=" + S4_wm.getNozzleNo() +
										" | liter=" + S4_wm.getLiter() +
										" | bPrice=" + S4_wm.getBasePrice() +
										" | price=" + S4_wm.getPrice());
			
			skip = false;
		}
		else if ("P7".equals(strWMmessage)) { // ������ �Ķ���� ����

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
			if (lineErrSkipCnt >= 7000 && lineErrSkipCnt <= 7999) { // �������� ��������(59) ����
				String str = P7_wm.getLineErrorSkipCount();
				t59_ds.editString("reAuthType", str.substring(1, 2), 1); // 1:����-����-���(�߰�), 2: ����-���-����, 9: �������
				t59_ds.editString("useFullPumping", str.substring(2, 3), 1); // 0: ����, 1: ���, 9: �������
				t59_ds.editString("fullPumpingAuthType", str.substring(3, 4), 1); // 1:B/Lüũ���, 2:�����ι��, 9: �������
				TxQue.enQueue(t59_ds.getByteStream());
				
				LogUtility.getPumpALogger().info("�������� �ɼǺ���� P7(8xxx) ����, �ɼǺ���. ODT_No="+P7_wm.getNozzleNo() +
						" P7 lineErrSkipCnt=" + P7_wm.getLineErrorSkipCount());
				Log.datas(t59_ds.getByteStream(), t59_ds.getByteStream().length, 20);
			}
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
		else if ("P5_1".equals(strWMmessage)) { // ȯ�漳������(��������)
			P5_1_WorkingMessage P5_1_wm = (P5_1_WorkingMessage) wm;
			
			LogUtility.getPumpALogger().info("[Pump A][����-�Ҹ���ODT ����/�ܰ� ����]" + " ODT_No=" + 
					P5_1_wm.getNozzleNo() + "("+P5_1_wm.getCommand()+")" +
					" | mode=" + P5_1_wm.getMode() + 
					" | useFullPumping=" + P5_1_wm.getUseFullPumping());
			
			// �ܰ� ������� Ȯ��(�߰� 2010/2/22)
			if (P5_1_wm.getMode().equals("0") || P5_1_wm.getMode().equals("1"))
				m_isCompleteBasePrice = isCompleteBasePrice(P5_1_wm);
			
			//LogUtility.getPumpALogger().info(">>>>>>>>>> m_isCompleteBasePrice=" + m_isCompleteBasePrice);
				
			if (P5_1_wm.getMode().equals("0")) { // �ʱ�ȭ
				m_changeBasePrice=false;
				m_setEnvDataOK=true;
				//m_useFullPumping = P5_1_wm.getUseFullPumping().equals("1")? true : false;
			}
			else if (P5_1_wm.getMode().equals("1")) { // �ܰ�����
				m_changeBasePrice=true; 
				//m_setEnvDataOK=true; 
				
				LogUtility.getPumpALogger().info("�ܰ������ P5����, �ܰ�����. ODT_No="+P5_1_wm.getNozzleNo());
			}
			else if (P5_1_wm.getMode().equals("2")) { // �������� �ɼǺ���
				//m_useFullPumping = P5_1_wm.getUseFullPumping().equals("1")? true : false;
				
				t59_ds.editString("useFullPumping", P5_1_wm.getUseFullPumping(), 1);
				TxQue.enQueue(t59_ds.getByteStream());
				
				LogUtility.getPumpALogger().info("�������� �ɼǺ���� P5����, �ɼǺ���. ODT_No="+P5_1_wm.getNozzleNo() +
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
		    	TxQue.enQueue(byt);

				LogUtility.getPumpALogger().info("�������� �ɼǺ���� P5����, �ɼǺ���. ODT_No="+P5_1_wm.getNozzleNo() +
						" useFullPumping=" + P5_1_wm.getUseFullPumping());
				Log.datas(byt, byt.length, 20);
				*/
				
				
				skip=true;
			}
			
		}
	
		return skip;
	}
	
	@Override
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
	
	protected void makeCertiCancel (byte[] buf) throws Exception, SerialConnectException {

		if (buf[11]=='7') { // �ܻ�
			HD_wm = (HD_WorkingMessage) trans.generateWorkingMessage(buf, "HD");
			
			HB_wm = new HB_WorkingMessage();
			HB_wm.setNozzleNo(nozStr);
			HB_wm.setConnectNozzleNo(HD_wm.getConnectNozzleNo());
			HB_wm.setCommandIndex("3"); // ���

			HB_wm.setAuthType("11"); // �Ϲ� ���� ������ ��� ��û
			HB_wm.setCardNumber(HD_wm.getSerialNo());
			
			//insertRecvQueue(HB_wm);

			LogUtility.getPumpALogger().info(new StringBuffer( "\n�Ҹ���ODT �ܻ���� ��ҿ�û ����(HB)" )
										.append("\n ODT_No=" ).append( HB_wm.getNozzleNo() )
										.append("\n nozzle=" ).append( HB_wm.getConnectNozzleNo() ).append( "(HB)" )
										.append("\n AuthType=" ).append( HB_wm.getAuthType() )
										.append("\n cardNo  =" ).append( HB_wm.getCardNumber() ).append("\n").toString());
			
		} else { // �ܻ� ��

			HC_wm = (HC_WorkingMessage) trans.generateWorkingMessage(buf, "HC");
			
			HA_wm = new HA_WorkingMessage();
			HA_wm.setNozzleNo(nozStr);
			HA_wm.setConnectNozzleNo(HC_wm.getConnectNozzleNo());
			HA_wm.setCommandIndex("3"); // ���
			HA_wm.setTrType("2"); // �ſ���� ���

			HA_wm.setAuthType("11"); // �Ϲ� ���� ������ ��� ��û
			HA_wm.setCardNumber(HC_wm.getCardNo());
			
			//insertRecvQueue(HA_wm);
			
			LogUtility.getPumpALogger().info(new StringBuffer("\n�Ҹ���ODT �ſ���� ��ҿ�û ����(HB)" )
					.append("\n ODT_No=" ).append( HB_wm.getNozzleNo() )
					.append("\n nozzle=" ).append( HB_wm.getConnectNozzleNo() ).append( "(HB)" )
					.append("\n AuthType=" ).append( HB_wm.getAuthType() )
					.append("\n cardNo  =" ).append( Base64Util.encode(getCardTrack1Data(HB_wm.getCardNumber())) ).append("\n").toString());
		}
		
		LogUtility.getPumpALogger().debug("\n\n���� ��ҿ�û ȣ���... -> ��ҿ�û ���� ����.");
	}
	
	@Override
	protected void makeLineError () throws Exception {
		
		SE_wm = new SE_WorkingMessage();
		
		SE_wm.setNozzleNo(Change.toString("%02d", nozNo));
		SE_wm.setDeviceType(Change.toString("%02d", nozType));
		SE_wm.setStatus("1");
		SE_wm.setStatusCode("601"); // ȸ���ҷ�
		SE_wm.setErrMsg("����ODT ȸ���ҷ�");
		
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
	
	@Override
	protected void makeStatusInfo(int nozState) throws Exception {

		S8_wm = new S8_WorkingMessage();
		int	statusCode=0;
		String errorMsg="";
		
		switch (nozState) {
			case 650:
				statusCode = 650; // ����
				errorMsg = "����ODT ����";
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
	
	@Override
	public void ownershipChange(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void ProcessDeviceError () throws Exception {

	}
	
	@Override
	protected void processODT () throws SerialConnectException, Exception {

		try {
		
			switch (RxBuf[3])
			{
			case '8':
				switch (RxBuf[4])
				{
				case '0': //--- "80" = �Է¿�������
					
					int len;
					for (len=0; len<1024; len++) 
						if (RxBuf[len]==ETX) break;
					
					LogUtility.getPumpALogger().debug("80 �������� = " + len);
					
					if (len >= 186) {
						byte[] byt = new byte[4];
						System.arraycopy(RxBuf, 182, byt, 0, 4);
						int serialNo = Change.toValue(new String(byt));

						LogUtility.getPumpALogger().debug("<<< 80 ���� �ߺ����� ������� ODT. >>>");
						LogUtility.getPumpALogger().debug("���� Serial No = " + m_nRequestNerailNo);
						LogUtility.getPumpALogger().debug("���� Serial No = " + serialNo);
						
						if (m_nRequestNerailNo != serialNo) {
							m_nRequestNerailNo = serialNo;
							insertRecvQueue(generateWorkingMessage(RxBuf, null));
						}
						else
							LogUtility.getPumpALogger().debug("�ߺ������̹Ƿ� ����!!!");
					}
					else {
						LogUtility.getPumpALogger().debug("<<< 80 ���� �ߺ����� ������� ODT �ƴ�! >>>");
						insertRecvQueue(generateWorkingMessage(RxBuf, null));
					}
					
					break;
	
				case '1': //--- "81" = ����̽� ���� ����
					if (RxBuf[6]=='1') { // �߻�
						insertRecvQueue(generateWorkingMessage(RxBuf, null));
					}
					else if (RxBuf[6]=='0') { // ȸ��
						insertRecvQueue(generateWorkingMessage(RxBuf, null));
					}
					break;
					
				case '2': //--- "82" = ���� ��������
					insertRecvQueue(generateWorkingMessage(RxBuf, null));
					break;
				}
	
				break;
			case '9':
				switch (RxBuf[4])
				{
				case '0': //--- "90" = ����/���� ������û(��������)
					insertRecvQueue(generateWorkingMessage(RxBuf, null));
					break;
	
				case '1': //--- "91" = �������� ����
					if (RxBuf[8] == 0x11) // 2009/08/04 ����
						makeStatusInfo(650); // ����ODT ����
					
					insertRecvQueue(generateWorkingMessage(RxBuf, null));
					break;
	
				case '2': //--- "92" = ���οϷ�(�����㰡) �Ǵ� �ǸſϷ�
					WorkingMessage wm = generateWorkingMessage(RxBuf, null);
					
					//-- �ܰ� ������� ������� ó�� (�߰� 2010/02/22)
					if (!wm.getCommand().equals("PB") || wm.getCommand().equals("PB") && m_isCompleteBasePrice) {
						insertRecvQueue(wm);
					}
					else { // �������(0�� 0���� �����Ϸ�)
						S4_WorkingMessage S4_wm = new S4_WorkingMessage ();
						S4_wm.setNozzleNo(wm.getNozzleNo()); // ODTNo
						S4_wm.setConnectNozzleNo(wm.getConnectNozzleNo()); // NozzleNo
						byte[] byt = generateByteStream(S4_wm);
						TxQue.enQueue(byt);
						
						LogUtility.getPumpALogger().debug("�ܰ�(P5)�̻����� �������. ODT_No=" + S4_wm.getNozzleNo());
					}
					//LogUtility.getPumpALogger().debug("ODT ���ŵ����� : ===== 92");
					//LogUtility.getPumpALogger().debug("command=" + wm.getCommand() + " nozzle=" + wm.getNozzleNo() + 
							//" targetNoz=" + wm.getTargetNozzleNo());
					//wm.print(" "); //wm.print(" ");
					break;
	
				case '3': //--- "93" = ������ �������/����
					insertRecvQueue(generateWorkingMessage(RxBuf, null));
					break;
	
				case '4': //--- "94" = ���� ������ ��û
					insertRecvQueue(generateWorkingMessage(RxBuf, null));
					break;
	
				case '5': //--- "95" = ������ �������� ��û
					insertRecvQueue(generateWorkingMessage(RxBuf, null));
					break;
	
				default:
					break;
				}
				
				break;
				
			default:	
				break;
			}
		
		} catch (IOException e) {
			LogUtility.getPumpALogger().error("Exception occurr! (ODT="+nozNo+")");
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	}
	
	@Override
	protected void ProcessOKResult () throws Exception {

	}
	
	@Override
	protected void processRecvSTX () throws SerialConnectException, Exception {
		
		byte[] byt = new byte[2];
		byt[0] = RxBuf[3];
		byt[1] = RxBuf[4];
			
		if (RxBuf[3]=='6' && RxBuf[4]=='1') {

			//Log.datas(RxBuf, 40, 20);
			
			// ODT Version üũ(2011.06.28)			
			byte[] byVer = new byte[4];
			byVer[0] = (byte) (RxBuf[7] ==0x20 ? '0' : RxBuf[7]);
			byVer[1] = (byte) (RxBuf[8] ==0x20 ? '0' : RxBuf[8]);
			byVer[2] = (byte) (RxBuf[9] ==0x20 ? '0' : RxBuf[9]);
			byVer[3] = (byte) (RxBuf[10]==0x20 ? '0' : RxBuf[10]);
			trans.setODTVersion(Change.toValue(new String(byVer)));	
			LogUtility.getPumpALogger().info("SomoSelf ODT Version : " + trans.getODTVersion());		
							
			switch (RxBuf[6]) // ���۸�� ('1'=Initial mode, '2'=POS mode)
			{
			case '1': // Initial mode (ȯ�漳��)
				
				if (m_setEnvDataOK==true) { // ȯ�漳������ �̼��� ó�� (2010/03/26) 
						
					switch (RxBuf[5]) // ����No.
					{
					case '1':
						insertEnvDataToTxQue (t0201_Buf, "01"); // ���������(P1)
						break;
					case '2':
						insertEnvDataToTxQue (t0202_Buf, "02"); // �Ӹ���/������(P2)
						break;
					case '6':
						setCurrentTime(); // ����ð��� ����
						insertEnvDataToTxQue (t0206_Buf, "06"); // �ý��۽ð�(P6)
						break;
					case '5':
						insertEnvDataToTxQue (t0205_Buf, "05"); // ��������(P5)
						break;
					default:
						break;
					}
					break;

				}
				else { // ȯ�漳������ �̼��� ó�� (2010/03/26) 
					//TxQue.enQueue(NAKb); // ODT�� "61"�� ������ �ϵ��� �� -> �����߻����� ���������� ����
					LogUtility.getPumpALogger().info("PumpAdaptor �ʱ�ȭ �̿Ϸ�. ������. ODT_No="+nozStr);
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

	protected byte[] protectCardNumber (byte[] buf) throws Exception, SerialConnectException {
		
		byte[] dat = buf.clone();
		
		if (dat[3]=='8' && dat[4]=='0') { // ���ο�û
			
			for (int i=13+4; i<13+12; i++) // �ſ�ī��
				dat[i] = '*';
			for (int i=13+17; i<13+33; i++) // �ſ�ī��
				dat[i] = '*';

			for (int i=53+4; i<53+12; i++) // ���ʽ�ī��
				dat[i] = '*';
			for (int i=53+17; i<53+33; i++) // ���ʽ�ī��
				dat[i] = '*';
		}
		else if (dat[3]=='9' && dat[4]=='2') { // �����㰡/�ǸſϷ�
			
			for (int i=12; i<12+40; i++)
				dat[i] = '*';
			
			for (int i=52; i<52+40; i++)
				dat[i] = '*';
		}
		else if (dat[3]=='5' && dat[4]=='2') {
			
			if (dat[11] != '7') { // �ſ�/���� ��������
				
				if (trans.getODTVersion() >=1 ) { // new verion
					int i=0, cnt=0;
					for (; i<dat.length; i++) {
						if (dat[i]==0x1C) cnt++;
						if (cnt >= 4) {i++; break;}
					}
					i += 6;
					for (int j=0; j<6; j++, i++)
						dat[i] = '*';
				}
				else { // old verion
					for (int i=90; i<90+40; i++)
						dat[i] = '*';
					
					for (int i=90+85+40; i<90+85+40+40; i++)
						dat[i] = '*';
				}
			}
			else if (dat[11] == '7') { // �ܻ� ��������
				for (int i=12; i<12+18; i++)
					dat[i] = '*';
			}
		}
		
		return dat;
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

	@Override
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
						
						//read NozID �߰�(09/01/05)
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

		int 	numBytes=0, loopCnt=0;
		boolean	occurLineErr = (lineErrCnt >= minErrCnt);
		byte[]  byt = new byte[1];
			
		if (occurLineErr) { // Check line communication error

			if (issueLineErr==true) {
				makeLineError();
				if (isExistCertiInfo()==true) {
					//makeCertiCancel(TxBuf); // �����
					LogUtility.getPumpALogger().info(">>>>>>>>>>>> 1.����� ��Ȳ�߻�!!! ������� ����.");
				}
				TxQue.flushQueue();
				//LogUtility.getPumpALogger().debug("Flush buffer(TxQue) lineErrCnt="+lineErrCnt+ " (ODT="+nozNo+")");
				issueLineErr=false;
			}
			
			if (lineErrCnt < maxSkipCnt) {
				lineErrCnt++;
				return; // Skip
			} else {
				lineErrCnt=0;
				issueLineErr=true;
			}
		}
		
		// ȸ���ҷ� ������ ó��
		if (firstRequest==true) {
			firstRequest=false;
			if (m_statusCode!=601)
				makeStatusInfo(650);
		}
		if (m_statusCode==601) { // ȸ���ҷ�
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
			
//			LogUtility.getPumpALogger().debug("\n>>>>> Received Down data : " +
//								"ODT=" + nozNo + " command=" + wm.getCommand());

			if (isSkipWorkingMessage(wm)==true) {
				wm=null;
				continue;
			}
			
			byte[] wmByte = generateByteStream(wm);
			
			if (wmByte != null) {
				if (chkEnvDataAndSaving(wmByte) == false) // If EnvData then saving to dataStruct
					TxQue.enQueue(wmByte); // ȯ�漳������ �ƴϸ�
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
			LogUtility.getPumpALogger().debug("\nStart request ===========> (ODT="+nozNo+")");

		mode = POL;
		while (true) {

			flushBuffer(RxBuf);
			
			if (loopCnt >= 4) break; // ����Ʈ������ PJT ���� -> �ۼ��� ���н� 1�� �� ȣ��
			loopCnt++;
			
			try {
				//##### Send ENQ #####//
				mode = (mode==POL? SEL : POL);
				ENQb[2] = mode;
				
				if (mode==SEL) {
					if (progressStep != 1 && TxQue.isEmpty()==true) // �۽��ڷ� ������
						continue;
				}
				else { // mode=POL
					if (progressStep != 1 && TxQue.isEmpty()==false) // �۽��ڷ� ������
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
					LogUtility.getPumpALogger().info("0.Send ENQ (ODT="+nozNo+")");
					Log.datas(ENQb, 4, 20);
				}

				//--- Loop for recv ACK/STX ---//
				for (int i=0; i<3; i++) {

					flushBuffer(RxBuf);
					
					//--- recv data---//	
					if (recvText(RxBuf) < 1) {
						lineErrCnt++;
						if (dispLevel>=2)
							LogUtility.getPumpALogger().info("0.Recv ACK/STX fail!(No response) lineErrCnt="+lineErrCnt+"/"+minErrCnt+" (ODT="+nozNo+")");
						else if (lineErrCnt >= minErrCnt) {
							if (m_nNoResponseCnt%3==0) {
								m_nNoResponseCnt=0;
								LogUtility.getPumpALogger().debug("0.Recv ACK/STX fail!(No response) lineErrCnt="+lineErrCnt+"/"+minErrCnt+" (ODT="+nozNo+")");
							}
							m_nNoResponseCnt++;
						}
						
						break;
					}
					
					//###### Recv data(Polling mode) : STX ######//
					if (mode==POL) { // 0x51
	
						if (RxBuf[0] == STX) {
	
							if (dispLevel>=3) {
								LogUtility.getPumpALogger().info("1.Recv STX(Data) (ODT="+nozNo+")");
								Log.datas(RxBuf, 200, 20);
							}
	
							if (compareNozID(RxBuf)==false) { 
								LogUtility.getPumpALogger().info ("1.Recv STX NozID mismatch-1.0! (ODT="+nozNo+")");
								Log.datas(RxBuf, 200, 20);
								lineErrCnt++;
								
								//is.read(); // flush inputStream
								trimInputStream("");
								continue;
							}
							
							if (compareBCC(RxBuf)==false) {
								LogUtility.getPumpALogger().info ("1.Recv STX BCC fail! (ODT="+nozNo+")");
								Log.datas(RxBuf, 200, 20);
								lineErrCnt++;
								//sendText (NAKb);
								continue;
							}					
							if (verifyData(RxBuf)==false) { 
								LogUtility.getPumpALogger().info ("�Ҹ�ODT�� NAKb ���� (Noz="+nozNo+")");
								sendText(NAKb);
								LogUtility.getPumpALogger().info ("1.Recv STX Data verify fail! (Noz="+nozNo+")");
								Log.datas(RxBuf, 200, 20);
								lineErrCnt++;
								return;
							} else {
								if (dispLevel==1 || dispLevel==2) {
									LogUtility.getPumpALogger().info ("1.Recv STX with normal (ODT="+nozNo+")");
									Log.datas(RxBuf, 200, 20);
								}
	
								if (m_isSaveSTX==true)
									LogUtility.getPumpALogger().info("Recv STX("+Change.toString("%02d", nozNo)+
											") : ["+pack(protectCardNumber(RxBuf), ETX)+"]");
								else
									LogUtility.getPumpALogger().debug("Recv STX("+Change.toString("%02d", nozNo)+
											") : ["+pack(protectCardNumber(RxBuf), ETX)+"]");
	
								processRecvSTX(); // ���� ���� ������ ó��
								
								if (recvTail_proc()==false) 
									lineErrCnt++;
								else
									lineErrCnt=0; // Normal terminated
								
								break;
							}
						}
						else if (RxBuf[0] == EOT) {
							
							if (compareNozID(RxBuf)==false) { 
								LogUtility.getPumpALogger().info ("1.Recv EOT NozID mismatch-1.0! (ODT="+nozNo+")");
								Log.datas(RxBuf, 40, 20);
								lineErrCnt++;
								trimInputStream("");
								continue;
							}
							
							if (dispLevel>=3)
								LogUtility.getPumpALogger().info("1.Recv EOT (ODT="+nozNo+")");
							lineErrCnt=0; // Normal terminated
							break;
						} 
						else {
							if (dispLevel>=3)
								LogUtility.getPumpALogger().info("1.Recv fail! (ODT="+nozNo+")");
							lineErrCnt++;
							continue;
						}
					}
					//###### Send data(Selecting mode) ######//
					else if (mode==SEL) { // 0x41
	
						if (RxBuf[0]==ACK) {
							
							if (compareNozID(RxBuf)==false) { 
								LogUtility.getPumpALogger().info ("2.Recv ACK NozID mismatch-1.0! (ODT="+nozNo+")");
								Log.datas(RxBuf, 40, 20);
								lineErrCnt++;
								trimInputStream("");
								continue;
							}
							
							is.read (byt,0,1);
							if (byt[0]==0x30) { // recv : ACK0
	
								if (dispLevel>=3)
									LogUtility.getPumpALogger().info("2.Recv ACK0 (ODT="+nozNo+")");
			
								if (TxQue.isEmpty()==false) { // �۽� ������ ������
		
									//###### Send data : send working-data ######//
									TxBuf = TxQue.getFirstItem();
									TxBuf[1] = nozID;
									TxBuf[2] = mode;
									setBCC (TxBuf); // write BCC

									if (TxBuf[3]=='5' && TxBuf[4]=='2') { // �������� ����
										LogUtility.getPumpALogger().info("TxBuf("+Change.toString("%02d", nozNo)+
													") : ["+new String(protectCardNumber(TxBuf))+"]"); // append 2011.06.27
										//Log.datas(TxBuf, TxBuf.length, 20);
									}
									
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
									
	//								if (TxBuf.length > 500) { // ����(09/04/23)
	//									Sleep.sleep((int) ((TxBuf.length - 500) * 0.9));
	//									LogUtility.getPumpALogger().debug("�߰����� �߻�. TxBuf.length=" + TxBuf.length + "(ODT="+nozNo+")");
	//								}						

									m_bSendSuccess=false;
									
									//--- Loop for recv ACK/NAK ---//
									for (int j=0; j<5; j++) {
										
										flushBuffer(RxBuf);
										if (recvText(RxBuf) < 1) {
											//if (dispLevel>=3)
												LogUtility.getPumpALogger().info("2.Recv ACK1 fail! (ODT="+nozNo+")");
											//lineErrCnt++;
											continue;
										}
										
										if (RxBuf[0]==ACK) {
											
											if (compareNozID(RxBuf)==false) { 
												LogUtility.getPumpALogger().info ("2.Recv ACK1 NozID mismatch! (ODT="+nozNo+")");
												Log.datas(RxBuf, 40, 20);
												//lineErrCnt++;
												trimInputStream("");
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
													LogUtility.getPumpALogger().info("2.Recv ACK1 (ODT="+nozNo+")");
												
												if (dispLevel==2) {
													LogUtility.getPumpALogger().info("2.Send STX with normal (ODT="+nozNo+")");
													Log.datas(TxBuf, TxBuf.length, 20);
												}
												
												if (TxBuf[3]=='5' && TxBuf[4]=='1' && TxBuf[8]=='5') { // �����ϷḸ
													byte[] bbb = new byte[2]; // Temp...
													bbb[0]=TxBuf[3];
													bbb[1]=TxBuf[4];
													LogUtility.getPumpALogger().info("\n\n<<<<Sent command "+new String(bbb)+"(�����Ϸ�). itemCount=" + 
															TxQue.getItemCount() + " (ODT="+nozNo+")" + "\n");
												}
												
												lineErrCnt=0; // Normal terminated
												m_bSendSuccess=true;
												break;
											}
										} else {
											//if (dispLevel>=3)
												LogUtility.getPumpALogger().info("2.Recv ACK1 fail! (ODT="+nozNo+")");
											//lineErrCnt++;
											continue;
										}
										
									} // end of for();
									
									if (m_bSendSuccess==false) {
										lineErrCnt++;
										LogUtility.getPumpALogger().info("Fail Send STX("+Change.toString("%02d", nozNo)+
															") : ["+new String(protectCardNumber(TxBuf))+"]");
									}
																		
									if (m_bSendSuccess==false && m_nODTState < 4) { // ��������
										if (TxBuf[3]=='5' && TxBuf[4]=='2') {
											//TxQue.deQueue(); // remove item
											//makeCertiCancel(TxBuf); // �����
											LogUtility.getPumpALogger().info("2.������������ ���� ����(���� ��)!!!!");
										}
									} 
									
								}
							} else { // recv mismatched data
								LogUtility.getPumpALogger().info("2.recv ACK0 NozID mismatch! (ODT="+nozNo+")");
								lineErrCnt++;
								trimInputStream("");
								continue;
							}
						} else if (RxBuf[0] == EOT) {
							
							if (compareNozID(RxBuf)==false) { 
								LogUtility.getPumpALogger().info ("1.Recv EOT NozID mismatch-1.1! (ODT="+nozNo+")");
								Log.datas(RxBuf, 40, 20);
								lineErrCnt++;
								trimInputStream("");
								continue;
							}
							
							if (dispLevel>=3) 
								LogUtility.getPumpALogger().info("2.Recv EOT (ODT="+nozNo+")");
							
							lineErrCnt=0; // Normal terminated
							break;
						} 
						else {
							if (dispLevel>=3) 
								LogUtility.getPumpALogger().info("2.Recv fail! (ODT="+nozNo+")  itemCount=" + TxQue.getItemCount());
							lineErrCnt++;
							continue;
						}
					} 
					
					break;
				}
			} catch (Exception e) {
				LogUtility.getPumpALogger().error("Exception occurr! (ODT="+nozNo+")");
				lineErrCnt++;
				LogUtility.getPumpALogger().error(e.getMessage(), e);
			}
		}
	}

	@Override
	public void run() {

	}
	
	@Override
	protected boolean sendTail_proc () throws Exception, SerialConnectException {

		return true;
	}
	
	@Override
	protected boolean sendText(byte buf) throws Exception, SerialConnectException {

		Sleep.sleep(writeStartInterval);
		
	    try {
			os.write(buf);
		} catch (IOException e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	
		return true;
	}
	
	@Override
	protected boolean sendText(byte[] buf) throws Exception, SerialConnectException {

		Sleep.sleep(writeStartInterval);
		
	    try {
			os.write(buf);
		} catch (IOException e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	
		return true;
	}

	@Override
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
	@Override
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
	
	@Override
	protected void setBCC(byte[] buf) throws Exception {
		int	i;
		
		for (i=0; i<buf.length-2; i++) {
			if (buf[i]==ETX) 
				break;
		}
		
		buf[i+1] = getBCC(buf);
	}
	
	@Override
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

	protected void trimInputStream(String msg) throws Exception {
		
		Sleep.sleep(readStartInterval);
		//byte[] trimBuf = new byte[40];
				
		int len = is.read(); // trim inputStream
		//LogUtility.getPumpALogger().debug("Trim inputStream (" + msg + ") :" + " len=" + len + " (ODT="+nozNo+")");
		//Log.datas(trimBuf, 40, 20);
	}

	protected boolean verifyData (byte[] buf) throws Exception {
		
		String cmd="";
				
		int len;
		for (len=0; len<1024; len++) 
			if (RxBuf[len]==ETX) break;
				
		if (buf[3]=='8' && buf[4]=='0') { // ���ο�û			
			cmd = "80";
			
			//--- ������ ����(������ ���̿� ETX Ȯ��) ---// 
			if (len == 182 || len == 186) {
				
				//--- ������ ����(���� �ܰ� �ݾ� ���ڿ���) ---//
				for (int i=93; i<113; i++) {
					if (buf[i] < 0x30 || buf[i] > 0x39) { // 0 ~ 9 �� �ƴϸ�
						LogUtility.getPumpALogger().info("nozzle=" + nozNo + 
								" -> �̻����� ����("+cmd+"), ���ڰ� �ƴ�, ���ŵ�����=" + buf[i]);
						return false;
					}
				}
				
//				--- ������ ����( �ݾ� ���ڿ���) ---// �輺�� 2012.09.12
				for (int i=6; i<10; i++) {
					if (buf[i] < 0x30 || buf[i] > 0x39) { // 0 ~ 9 �� �ƴϸ�
						LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> �����ȣ �� �̻����� ����("+cmd+"), ���ڰ� �ƴ�, ���ŵ�����=" + buf[i]);
						return false;
					}
				}
				
				return true;
			}			
			else {
				LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> �̻����� ����("+cmd+"), ���ŵ����ͱ���=" + len);
				return false;
			}
		}

		return true;
	}
}


