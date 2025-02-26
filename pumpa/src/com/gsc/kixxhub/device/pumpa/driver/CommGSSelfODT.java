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

import com.gsc.kixxhub.common.data.pump.BC_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.BI_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.CA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.CB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HE_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HF_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P5_1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P5_NozzleInfo;
import com.gsc.kixxhub.common.data.pump.P7_WorkingMessage;
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
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.BytesQue2;
import com.gsc.kixxhub.device.pumpa.common.Change;
import com.gsc.kixxhub.device.pumpa.common.DataStruct;
import com.gsc.kixxhub.device.pumpa.common.Log;
import com.gsc.kixxhub.device.pumpa.common.SerialConnectException;
import com.gsc.kixxhub.device.pumpa.common.Sleep;
import com.gsc.kixxhub.device.pumpa.datas.ReceiptUtil;
import com.gsc.kixxhub.device.pumpa.define.CCommDriver;
import com.gsc.kixxhub.device.pumpa.translation.TransGSSelf;

public class CommGSSelfODT extends CCommDriver implements SerialPortEventListener, 
				CommPortOwnershipListener, Runnable {

	private class SelectedItemInfo {
		String id="01";
		String voice="";
	}
	
	protected static boolean m_isSaveSTX=false;
	protected short 	ADIESEL=12;
	protected int	baseMinErrCnt=5, baseMaxSkipCnt=200;
	protected int	baseReadBuffInterval   = 30;
	//protected boolean waitEndProcess=false;
    //	protected int	baseReadStartInterval  = 3;
//	protected int	baseWriteStartInterval = 0;
//    protected int	baseReadBuffInterval   = 30;
	protected int	baseReadStartInterval  = 10;
	protected int	baseWriteStartInterval = 10;
	protected BC_WorkingMessage BC_wm;
	protected BI_WorkingMessage BI_wm;
	protected CB_WorkingMessage CB_wm;
	protected short 	DIESEL=11;
	protected int 	dispLevel=0;
	protected byte  	DLE  = 0x10;
	protected byte[] 	DLE0 = new byte[3];
	protected byte[] 	DLE1 = new byte[3];
	
	protected byte  	PRESET_LITER=1;
	protected byte  	PRESET_NONE=2;
	protected byte  	PRESET_PRICE=0; 
	
	protected byte  	ENQ  = 0x05;
	
    protected byte[] 	enq_Buf = new byte[4];
    protected byte[] 	ENQb = new byte[4];
    protected byte  	EOT  = 0x04;

protected byte[]  	EOTb = {EOT, 0};
	protected byte  	ESC  = 0x1B;
    protected byte  	ETX  = 0x03;
	protected boolean firstRequest=true;
	
	protected short 	GASOLINE=20;
	protected HE_WorkingMessage HE_wm;
    protected HF_WorkingMessage HF_wm;
    protected boolean	issueLineErr=true;
    
    protected int		RxBuffSize = 256;
    protected byte[] 	RxBuf = new byte[RxBuffSize];
	protected byte[] 	lastPumpingData=new byte[RxBuffSize];
	protected int		lineCommCheckCnt=0;
	protected int		lineErrCnt=0;
	protected String 	m_basePrice="000000"; // ���� ��û�ܰ�
	protected Hashtable<String,String> m_basePriceTbl = new Hashtable<String,String>();
	protected String  	m_bonusNo="";
	// for HE_WorkingMessage
	protected String  	m_cashCount="";
	protected String  	m_cashReceiptNo="";
	//protected byte[]  	m_byCreditNo = new byte[40];
	//protected byte[]  	m_byBonusNo = new byte[40];
	protected String  	m_creditNo="";

    protected String  	m_custCardNo="";
    protected String 	m_dispPriceLiter="";
    protected boolean 	m_isCreditMode, m_isBonusMode, m_isApprove;
    protected boolean	m_isCustCheck=false;
    protected boolean 	m_isFirstPumping = true;
    protected boolean 	m_isFirstPumpingEnd=true;
    protected boolean	m_isFromODT=false;
    
    protected boolean 	m_isFullPumping = false;
	protected boolean	m_isInsertedCash=false;
	protected boolean 	m_isProcessNoInvoke=false;
	protected boolean 	m_isProcessRepeat=false;
	protected boolean 	m_isProcessSkip=false;
	protected String 	m_liter="0000000";    // ���� ��û��
	protected int		m_nCashReceiptState=0;
	protected int		m_nCashReceiptStep=1;
	protected int		m_nCashReceiptType=0; // 1=�Һ��ڿ�, 2= ����ڿ�
	protected int		m_nCreditErrCnt=0;
	protected int		m_nCustType=1; // 1=�Ϲ�
	protected int	 	m_nInitODTCount=0;
	//protected short 	m_nODTState=0;
	protected short 	m_nODTState=1;
	// Nozzle info tables (P5_1)
	protected Vector<String> m_nozNoVec = new Vector<String>();
	protected int		m_nPayType; // �������
	protected short 	m_nPresetMode = 0; // 0=����, 1=����
	protected int		m_nSetPrice;
	protected int	 	m_numMPPs;
	protected Hashtable<String,String> m_oilCodeTbl = new Hashtable<String,String>();
	protected short 	m_oilKind=0;
	protected Hashtable<String,String> m_oilNameTbl = new Hashtable<String,String>();
	protected byte		m_preset=PRESET_NONE;
	protected String 	m_price="00000000";   // ���� ��û�ݾ�
	protected String 	m_realBasePrice="000000"; // �� �����ܰ�
	protected String 	m_realLiter="0000000";    // �� ������
	protected String 	m_realPrice="00000000";   // �� �����ݾ�
	protected String[][] m_sDispPriceArr=new String[2][6];
	
	protected boolean	m_setEnvDataOK=false;
	protected String[][] m_sMentPriceArr=new String[2][6];
	protected String[][] m_sPriceButtArr=new String[2][6];
	protected String 	m_sSaveBonus="0";
	protected int 		m_statusCode=601;
	protected String	m_targetNozzle;
	
    protected byte  SEN  = 0x40; // '@'
	protected byte  POL  = 0x3F; // '?'
	protected byte  mode = SEN;
	
	protected byte  	NAK  = 0x15;
	protected byte[]  	NAKb = {NAK, 0};
	protected byte		nozID;
	protected short		nozState=0;
	protected String 	nozStr = "";
	protected PB_WorkingMessage PB_wm;
	protected short 	PGASOLINE=21;

	protected Vector<String> processList = new Vector<String>();
	
//    protected byte  PREFIX_ENQ = 0x05;
//    protected byte  STX  = 0x02;
//    protected byte  ETX  = 0x03;
//    protected byte  DLE  = 0x10;
//    protected byte[] DLE0 = new byte[2];
//    protected byte[] DLE1 = new byte[2];
//    protected byte  NAK  = 0x15;
//    protected byte  ENQ  = 0x05;
//    protected byte  EOT  = 0x04;
//    protected byte  ESC  = 0x1B;

    //progressStep : 0=�ʱ�ȭ��, 1=�ʱ�ȭ��, 2=���������۽���, 3=������, 4=�����Ϸ�, 
    //               5=���а���������/�����Ϸ��ڷ� �۽�
    protected int 	progressStep=3;

    protected QM_WoringMessage  QM_wm;
    protected byte[] 	rcvInitBuf = new byte[6];


    protected S3_WorkingMessage S3_wm;
    protected S4_WorkingMessage S4_wm;
    protected S5_WorkingMessage S5_wm;
    protected S8_WorkingMessage S8_wm;
    protected SE_WorkingMessage SE_wm;

    protected SJ_WorkingMessage SJ_wm;
    protected byte  	STX  = 0x02;

    protected byte[] 	tC0_Buf;
    protected DataStruct tC0_ds = new DataStruct(); // ������1
    protected byte[] 	tC1_Buf;
    
    protected DataStruct tC1_ds = new DataStruct(); // ������2
protected byte[] 	tC2_Buf;
    protected DataStruct tC2_ds = new DataStruct(); // ������3
    protected byte[] 	tD0_Buf;
    protected DataStruct tD0_ds = new DataStruct(); // ����̽�����
    protected byte[] 	tE0_01_Buf; // ���� ����
    protected byte[] 	tE0_02_Buf; // ������� ����
    protected byte[] 	tE0_04_Buf; // ��������
    protected byte[] 	tE0_06_Buf; // ���ʽ�ī�� ����
    protected byte[] 	tE0_07_Buf; // ���ݿ����� ����
    
    protected byte[] 	tE0_08_Buf; // ķ���� ����
    protected byte[] 	tE0_31_Buf; // ������ ����
    protected byte[] 	tE0_32_Buf; // �����丮 �� ����
    //    protected byte[] 	tsb_Buf; // Start block
//    protected byte[] 	teb_Buf; // End block
    protected byte[] 	tE0_Buf;
    protected DataStruct tE0_ds = new DataStruct(); // ȯ�漳�� ���� 
    protected DataStruct teb_ds = new DataStruct(); // End block 

	protected DataStruct ti0_ds = new DataStruct(); // ����̽� �Է�����
    protected byte[] 	tM0_Buf;
    protected DataStruct tm0_ds = new DataStruct(); // ����̽� ���۸��
    protected DataStruct tM0_ds = new DataStruct(); // �������� ��û
    protected TR_WorkingMessage TR_wm;
    TransGSSelf trans = new TransGSSelf();
    protected byte[] 	tS0_Buf;
    //����
    protected DataStruct ts0_ds = new DataStruct(); // ��������
    protected DataStruct tS0_ds = new DataStruct(); // �������� ��û
	
	//��û
    protected DataStruct tsb_ds = new DataStruct(); // Start block
    protected int		TxBuffSize = 2600;
    protected byte[] 	TxBuf = new byte[TxBuffSize];
		
	protected BytesQue2 TxQue = new BytesQue2(30);
	
	public CommGSSelfODT (int nozNum, String romVerStr, int nMPPCount) {
		
		Formatter form = new Formatter ();
		byte[] 	nozByt = new byte[2];
		
		nozNo = nozNum;
		romVer = romVerStr;
		
		form.format("%02d", nozNo);
		nozStr = form.toString();
		nozByt = nozStr.getBytes();
		nozID = (byte) (nozNo + 0x40); // 01 = 'A'
		
		m_numMPPs = nMPPCount;
		
		//--- prefix DLE-buffer ---//
	    DLE0[0] = DLE;
	    DLE0[1] = nozID;
	    DLE0[2] = 0x30;
	    DLE1[0] = DLE;
	    DLE1[1] = nozID;
	    DLE1[2] = 0x31;
		
		//--- prefix ENQ-buffer ---//
	    ENQb[0] = EOT;
	    ENQb[1] = nozID;
	    ENQb[2] = SEN;
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
			
//			//--- Start block ---//
//			tsb_ds.addByte  ("STX", (byte) STX);
//			tsb_ds.addByte  ("SA", nozID);
//			tsb_ds.addByte  ("UA", (byte) SEN);
//			tsb_ds.addString("command", "C0", 2);
//			tsb_ds.addByte  ("sequence", (byte) '1'); //sequence
//			teb_ds.addString("device", "11", 2);
//			tsb_ds.addString("block", "S", 1);
//			tsb_ds.addString("blockName", "01-PRINT", 8);
//			tsb_ds.addByte  ("ETX", (byte) ETX);
//			tsb_ds.addByte  ("BCC", (byte) ' ');
//			tsb_Buf = tsb_ds.getByteStream();
//						
//			//--- End block ---//
//			teb_ds.addByte  ("STX", (byte) STX);
//			teb_ds.addByte  ("SA", nozID);
//			teb_ds.addByte  ("UA", (byte) SEN);
//			teb_ds.addString("command", "C0", 2);
//			teb_ds.addByte  ("sequence", (byte) '1'); //sequence
//			teb_ds.addString("device", "11", 2);
//			teb_ds.addString("block", "E", 1);
//			teb_ds.addString("blockName", "02-PRINT", 8);
//			teb_ds.addByte  ("ETX", (byte) ETX);
//			teb_ds.addByte  ("BCC", (byte) ' ');
//			teb_Buf = teb_ds.getByteStream();
			
			//--- ����̽� �Է����� ---//
			tD0_ds.addByte  ("STX", STX);
			tD0_ds.addByte  ("SA", nozID);
			tD0_ds.addByte  ("UA", SEN);
			tD0_ds.addString("command", "D0", 2);
			tD0_ds.addByte  ("sequence", (byte) '1'); //sequence
			tD0_ds.addByte  ("devCtrl1", (byte) 0x00);
			tD0_ds.addByte  ("devCtrl2", (byte) 0x00);
			tD0_ds.addByte  ("ETX", ETX);
			tD0_ds.addByte  ("BCC", (byte) ' ');
			tD0_Buf = tD0_ds.getByteStream();
			
			//--- ������1 ---//
			tC0_ds.addByte  ("STX", STX);
			tC0_ds.addByte  ("SA", nozID);
			tC0_ds.addByte  ("UA", SEN);
			tC0_ds.addString("command", "C0", 2);
			tC0_ds.addByte  ("sequence", (byte) '1'); //sequence
			tC0_ds.addString("deviceCode", "", 2);
			tC0_ds.addString("datas", "", 2504); // �ִ�ũ��(������ ����)
			tC0_ds.addByte  ("ETX", ETX);
			tC0_ds.addByte  ("BCC", (byte) ' ');
			tC0_Buf = tC0_ds.getByteStream();
					
			//--- ������2 - ȭ��, ī�帮��, BNA ---//
			tC1_ds.addByte  ("STX", STX);
			tC1_ds.addByte  ("SA", nozID);
			tC1_ds.addByte  ("UA", SEN);
			tC1_ds.addString("command", "C1", 2);
			tC1_ds.addByte  ("sequence", (byte) '1'); //sequence
			tC1_ds.addString("group", "00", 2);
			tC1_ds.addString("id", "00", 2);
			tC1_ds.addByte  ("devCtrl1", (byte) 0xFF);
			tC1_ds.addByte  ("devCtrl2", (byte) 0xFF);
			tC1_ds.addString("voiceDataCnt", "00", 2);
			tC1_ds.addString("voiceData", "000", 3);
			tC1_ds.addByte  ("ETX", ETX);
			tC1_ds.addByte  ("BCC", (byte) ' ');
			tC1_Buf = tC1_ds.getByteStream();

			//--- ������3 - �Է°�� ������ ---//
			tC2_ds.addByte  ("STX", STX);
			tC2_ds.addByte  ("SA", nozID);
			tC2_ds.addByte  ("UA", SEN);
			tC2_ds.addString("command", "C2", 2);
			tC2_ds.addByte  ("sequence", (byte) '1'); //sequence
			tC2_ds.addString("group", "00", 2);
			tC2_ds.addString("id", "00", 2);
			tC2_ds.addByte  ("devCtrl1", (byte) 0xFF);
			tC2_ds.addByte  ("devCtrl2", (byte) 0xFF);
			tC2_ds.addString("voiceDataCnt", "00", 2);
			tC2_ds.addString("voiceData", "000", 3);
			tC2_ds.addString("rtnGroup", "00", 2);
			tC2_ds.addString("rtnID", "00", 2);
			tC2_ds.addString("rtnDataSize", "0000", 4);
			tC2_ds.addByte  ("ETX", ETX);
			tC2_ds.addByte  ("BCC", (byte) ' ');
			tC2_Buf = tC2_ds.getByteStream();

			//--- �������� ��û ---//
			tS0_ds.addByte  ("STX", STX);
			tS0_ds.addByte  ("SA", nozID);
			tS0_ds.addByte  ("UA", SEN);
			tS0_ds.addString("command", "S0", 2);
			tS0_ds.addByte  ("sequence", (byte) '1'); //sequence
			tS0_ds.addByte  ("ETX", ETX);
			tS0_ds.addByte  ("BCC", (byte) ' ');
			tS0_Buf = tS0_ds.getByteStream();
			
			//--- ������� ��û ---//
			tM0_ds.addByte  ("STX", STX);
			tM0_ds.addByte  ("SA", nozID);
			tM0_ds.addByte  ("UA", SEN);
			tM0_ds.addString("command", "M0", 2);
			tM0_ds.addByte  ("sequence", (byte) '1'); //sequence
			tM0_ds.addByte  ("ETX", ETX);
			tM0_ds.addByte  ("BCC", (byte) ' ');
			tM0_Buf = tM0_ds.getByteStream();
			
		} catch (Exception e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	}
	
	protected boolean chkEnvDataAndSaving (byte[] dat) throws Exception {
		
		boolean envDataFlag=false;
		
		if (dat[3]=='E' && dat[4]=='0') {
			
			if (dat[6]=='0' && dat[7]=='1') { // ���� ����
				tE0_01_Buf = dat.clone();
				
				//envDataFlag = true; // ODT ��û�� ����
				envDataFlag = false; // ���� ��� ODT�� ���Ҽ�

				LogUtility.getPumpALogger().info("ȯ�漳������(����� ����) ���� > Buf ����. (Noz="+nozNo+")");
				LogUtility.getPumpALogger().info(new String(tE0_01_Buf));				
			}
			else if (dat[6]=='0' && dat[7]=='2') { // ������� ����
				tE0_02_Buf = dat.clone();
				
				//envDataFlag = true; // ODT ��û�� ����
				envDataFlag = false; // ���� ��� ODT�� ���Ҽ�

				LogUtility.getPumpALogger().info("������� ���� ���� > Buf ����. (Noz="+nozNo+")");
				LogUtility.getPumpALogger().info(new String(tE0_02_Buf));
			}
			else if (dat[6]=='0' && dat[7]=='4') { // ��������
				tE0_04_Buf = dat.clone();
				
				//envDataFlag = true; // ODT ��û�� ����
				envDataFlag = false; // ���� ��� ODT�� ���Ҽ�

				LogUtility.getPumpALogger().info("�������� ���� > Buf ����. (Noz="+nozNo+")");
				LogUtility.getPumpALogger().info(new String(tE0_04_Buf));
			}
			else if (dat[6]=='0' && dat[7]=='6') { // ���ʽ�ī�� ����
				tE0_06_Buf = dat.clone();
				
				//envDataFlag = true; // ODT ��û�� ����
				envDataFlag = false; // ���� ��� ODT�� ���Ҽ�

				LogUtility.getPumpALogger().info("���ݿ����� ���� ���� > Buf ����. (Noz="+nozNo+")");
				LogUtility.getPumpALogger().info(new String(tE0_06_Buf));
			}
			else if (dat[6]=='0' && dat[7]=='7') { // ���ʽ�ī�� ����
				tE0_07_Buf = dat.clone();
				
				//envDataFlag = true; // ODT ��û�� ����
				envDataFlag = false; // ���� ��� ODT�� ���Ҽ�

				LogUtility.getPumpALogger().info("���ݿ����� ���� ���� > Buf ����. (Noz="+nozNo+")");
				LogUtility.getPumpALogger().info(new String(tE0_07_Buf));
			}
			else if (dat[6]=='0' && dat[7]=='8') { // ķ���� ����
				tE0_08_Buf = dat.clone();
				
				//envDataFlag = true; // ODT ��û�� ����
				envDataFlag = false; // ���� ��� ODT�� ���Ҽ�

				LogUtility.getPumpALogger().info("ķ���� ���� ���� > Buf ����. (Noz="+nozNo+")");
				LogUtility.getPumpALogger().info(new String(tE0_08_Buf));
			}
			else if (dat[6]=='3' && dat[7]=='1') { // ������ ����
				tE0_31_Buf = dat.clone();
				
				//envDataFlag = true; // ODT ��û�� ����
				envDataFlag = false; // ���� ��� ODT�� ���Ҽ�

				LogUtility.getPumpALogger().info("������ ���� ���� > Buf ����. (Noz="+nozNo+")");
				LogUtility.getPumpALogger().info(new String(tE0_31_Buf));
			}
			else if (dat[6]=='3' && dat[7]=='2') { // �����丮�� ����
				tE0_32_Buf = dat.clone();
				
				//envDataFlag = true; // ODT ��û�� ����
				envDataFlag = false; // ���� ��� ODT�� ���Ҽ�

				LogUtility.getPumpALogger().info("�����丮�� ���� ���� > Buf ����. (Noz="+nozNo+")");
				LogUtility.getPumpALogger().info(new String(tE0_32_Buf));
			}
		}
		
		//envDataFlag = true; // temp
		
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

		if (buf[1]==nozID)		
			return true;
		else
			return false;
	}

	//--- ���ϴ� ũ���� ��ĭ�� �����
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
	
	protected String getInputValue (byte[] buf) throws  Exception {

		byte[] byLen = new byte[4];
		byte[] byValue = null;
		
		System.arraycopy(buf, 13, byLen, 0, 4);
		int len = Change.toValue(new String(byLen));
		byValue = new byte[len];
		System.arraycopy(buf, 17, byValue, 0, len);
		
		return new String (byValue);
	}
	
	protected String getMSCardNumber (byte[] dat) throws Exception {
		
		byte[] byCardNo = new byte[40];
		byte[] byLen=new byte[4];
		System.arraycopy(dat, 5, byLen, 0, 4);
		String lenStr = new String(byLen);
		int	len = Change.toValue (lenStr) - 4;
						
		//flushBuffer(m_byCreditNo);
		System.arraycopy(dat, 13, byCardNo, 0, len);
		int k;
		for (k=0; k<byCardNo.length; k++)
			if (byCardNo[k]==0x00)
				break;
		
		String sCardNo = new String(byCardNo);
		
		return sCardNo.substring(0,k);
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
	
	protected byte[] getPureData(byte[] buf) throws Exception {

		byte[] dat=null;
		int i, size;

//		LogUtility.getPumpALogger().debug("before ===========");
//		Log.datas(buf, buf.length, 20);
		
		for (i=3; i<buf.length; i++) {
			if (buf[i]==ETX)
				break;
		}
				
		size = i-3;
		if (i>3) {
			dat = new byte[size];
			System.arraycopy(buf, 3, dat, 0, size);
		}

		LogUtility.getPumpALogger().debug("RecvData ::::: pureDataSize " + size + " bytes");
		Log.datas(buf, size+3, 20);
		
		return dat;
	}
	
	protected void initVariables () throws  Exception {
		
		m_isCreditMode=false;
		m_isBonusMode=false;
		m_isFirstPumping=true;
		m_isFirstPumpingEnd=true;
		m_oilKind= 0;
		m_liter= "0000000";
		m_price= "00000000";
		m_creditNo = "";
		m_bonusNo = "";
		m_custCardNo = "";
		m_cashCount = "";
		m_cashReceiptNo = "";
		m_nCustType = 1;
		
		m_nPresetMode=PRESET_PRICE;
		m_isFullPumping=false;
		m_nCashReceiptStep=1;
		m_isProcessSkip=false;
		m_isProcessRepeat=false;
		m_isProcessNoInvoke=false;
		
		m_isInsertedCash=false;
		
//		insertDevCtrlOrder_42((byte)'0', "00", "02", "000"); // Ÿ�̸� ����
//		m_returnInitMode = true;
	}
	
	//----- ������1(ȭ��, ����̽� �Է�, ���� ��������)-----//
	protected void insertDeviceCtrl_D0 (byte sequence, byte devCtrl1, byte devCtrl2) throws Exception {

		byte[] sndBuf;
		
		try {
			tD0_ds.editByte  ("sequence", sequence);
			tD0_ds.editByte  ("devCtrl1", devCtrl1);
			tD0_ds.editByte  ("devCtrl2", devCtrl2);

			sndBuf = tD0_ds.getByteStream();
			TxQue.enQueue(sndBuf);

			LogUtility.getPumpALogger().debug(">>>>Insert command D0. itemCount=" + TxQue.getItemCount() + " (ODT="+nozNo+")");
			Log.datas(sndBuf, sndBuf.length, 20);
		
		} catch (Exception e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	}
		
	protected void insertEnvDataToTxQue () throws Exception {
		
		TxQue.enQueue(tE0_01_Buf);
		TxQue.enQueue(tE0_02_Buf);
		TxQue.enQueue(tE0_04_Buf);
		TxQue.enQueue(tE0_06_Buf);
		TxQue.enQueue(tE0_07_Buf);
		TxQue.enQueue(tE0_08_Buf);
		TxQue.enQueue(tE0_31_Buf);
		TxQue.enQueue(tE0_32_Buf);
	}
	
	protected void insertPrintData (byte[] byPrtData, int nStart, int nLen, int sequence) throws Exception {

		byte[] bySndBuf = new byte[nLen];
		
		System.arraycopy(byPrtData, nStart, bySndBuf, 0, nLen);

		ODTCtrl_C0 ((byte) sequence, "11", "D" + new String(bySndBuf));
		
		LogUtility.getPumpALogger().debug(">>>>Insert command C0(Print). itemCount=" + TxQue.getItemCount() + " (ODT="+nozNo+")");
		Log.datas(bySndBuf, bySndBuf.length, 20);
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
	
	protected void invokeNextPage (String keyGroup, SelectedItemInfo selItem) throws Exception {
		
		//boolean isSkipProcess = false;
		
		if (keyGroup.equals("01")) { //--- ���ȭ��
			
			m_nODTState = 1;
			ODTCtrl_C1((byte) '1', "01", selItem.id, (byte) 0x00, (byte) 0x00, selItem.voice + "");

			makeStatusInfo(231); // �������� ���(�����)
		} 
		else if (keyGroup.equals("02")) { //--- ������� ����ȭ��
			
			m_nODTState = 2;
			ODTCtrl_C1((byte) '1', "02", selItem.id, (byte) 0x00, (byte) 0x00, selItem.voice + "");

			makeStatusInfo(231); // �������� ���(�����)
		} 
		else if (keyGroup.equals("03")) { //--- �����Է� ȭ��
			
			m_nODTState = 3;
			ODTCtrl_C1((byte) '1', "03", selItem.id, (byte) 0x00, (byte) 0x00, selItem.voice + "");

			makeStatusInfo(233); // �����Է� ���
		} 
		else if (keyGroup.equals("04")) { //--- �������� ȭ��

			m_nODTState = 4;
			ODTCtrl_C1((byte) '1', "04", selItem.id, (byte) 0x00, (byte) 0x00, selItem.voice + "02");

			makeStatusInfo(232); // �ݾ׼��� ���
		} 
		else if (keyGroup.equals("05")) { //--- �ݾ�/���� �Է�ȭ��
			
			m_nODTState = 5;
			
			if (m_nPayType == 1) { // ��������� ������ ���
				m_isProcessSkip = true;
			}
			else if (m_nPayType == 2) { // ��������� �ſ�ī���� ���
				ODTCtrl_C2((byte) '1', "05", selItem.id, (byte) 0x00, (byte) 0x00, selItem.voice + "12", "05", "01", 9);
			}
			else if (m_nPayType == 3) { // ��������� �ŷ�óī���� ���
				requestCheckCustmerType (m_custCardNo);
			}

			makeStatusInfo(232); // �ݾ׼��� ���
		} 
		else if (keyGroup.equals("06")) { //--- ���ʽ� �Է�ȭ��
			
			m_nODTState = 6;
			ODTCtrl_C1((byte) '1', "06", selItem.id, (byte) 0x00, (byte) 0x00, selItem.voice + "5153");

			makeStatusInfo(234); // 
		}
		else if (keyGroup.equals("07")) { // ���ݿ����� �Է�ȭ��
			
			m_nODTState = 7;
			
			LogUtility.getPumpALogger().debug("\n>>>(invokeNext-3) m_nCashReceiptStep="+m_nCashReceiptStep);
			
			if (m_nPayType != 1) { // ��������� ������ �ƴҰ��
				m_isProcessSkip = true;
			}
			else { // ��������� ������ ���
				if (m_nCashReceiptStep==1) {
					// ���� ������ ��������
					ODTCtrl_C1((byte) '1', "07", selItem.id, (byte) 0x00, (byte) 0x00, selItem.voice + "5254");
					m_nCashReceiptStep=2;
				}
				else if (m_nCashReceiptStep==2) { 
					// ���ݿ����� �Է� �Ǵ� ī�帮�� ȭ��
					ODTCtrl_C2((byte) '1', "07", "02", (byte) 0x00, (byte) 0x00, selItem.voice + "5254", "07", "11", 20);
					m_nCashReceiptStep=3;
				}
			}
			
			makeStatusInfo(234); // 
		} 
		else if (keyGroup.equals("08")) { // ķ���� �Է�ȭ��
			
			m_nODTState = 8;
			ODTCtrl_C1((byte) '1', "08", selItem.id, (byte) 0x00, (byte) 0x00, selItem.voice + "69");

			makeStatusInfo(234); // 
		}
		else if (keyGroup.equals("09")) { // ���ο�û ��
			
			m_nODTState = 9;
			//ODTCtrl_C1((byte) '1', "09", selItem.id, (byte) 0x00, (byte) 0x00, selItem.voice + "51");
			ODTCtrl_C1((byte) '1', "03", "15", (byte) 0x00, (byte) 0x00, selItem.voice + "5556");

			//----- ī����ο�û -----//
			boolean bApprove = requestCardApprove ();
			
			makeStatusInfo(236); // ���ο�û ��
		}
		
	}
	
	protected boolean isSkipWorkingMessage (WorkingMessage wm) throws Exception {
		
		boolean skip=false;

		if (wm.getCommand().equals("P5_1")) { // ���� ȯ�漳��
			
			P5_1_WorkingMessage P5_1_wm = (P5_1_WorkingMessage) wm;
			Vector<P5_NozzleInfo> nozInfoVec = P5_1_wm.getNozzleInfo();
			int nozCnt = nozInfoVec.size();

			LogUtility.getPumpALogger().debug("GS����ODT P5 ��������. ODT_No=" + P5_1_wm.getOdtID()+
					" nozCnt=" + nozCnt + " mode=" + P5_1_wm.getMode());
			
			if (P5_1_wm.getMode().equals("0")) { // �ʱ�ȭ
				
				for (int i=0; i<nozCnt; i++) {
					
					P5_NozzleInfo P5_nozWm = nozInfoVec.get(i);
					String szNozNo = P5_nozWm.getNozzleNumber();
					LogUtility.getPumpALogger().debug("nozzle="+ P5_nozWm.getNozzleNumber());
					LogUtility.getPumpALogger().debug("bPrice="+ P5_nozWm.getBasePrice());
					LogUtility.getPumpALogger().debug("oilType="+ P5_nozWm.getGoodsCode());
					LogUtility.getPumpALogger().debug("oilCode="+ P5_nozWm.getGoodsType() + "\n");
					
					m_nozNoVec.add(szNozNo);
					m_basePriceTbl.put(szNozNo, P5_nozWm.getBasePrice());
					m_oilCodeTbl.put(szNozNo, P5_nozWm.getGoodsCode());
					m_oilNameTbl.put(szNozNo, P5_nozWm.getGoodsType());
				}
	
				m_setEnvDataOK=true;
			}
			else if (P5_1_wm.getMode().equals("1")) { // �ܰ�����
				
				for (int i=0; i<nozCnt; i++) {
					P5_NozzleInfo P5_nozWm = nozInfoVec.get(i);
					String szNozNo = P5_nozWm.getNozzleNumber();
					String beforeBasePrice_ = m_basePriceTbl.get(szNozNo).substring(0,4);

					m_basePriceTbl.remove(szNozNo);
					m_basePriceTbl.put(szNozNo, P5_nozWm.getBasePrice());
					
					/*
					// �߰�(09/03/05)
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

					LogUtility.getPumpALogger().debug("�ܰ������ P5����, �ܰ�����. nozzle="+szNozNo+" : "+beforeBasePrice_+
							" -> " +P5_nozWm.getBasePrice().substring(0,4)+" (TableSize="+m_basePriceTbl.size()+")");
				}
			}
			
			skip = true;
		}
		else if (wm.getCommand().equals("PB")) { // ������ �Ǵ� ������������
			PB_wm = (PB_WorkingMessage) wm;

			m_targetNozzle=PB_wm.getConnectNozzleNo();
			m_oilKind = getOilKind(PB_wm.getConnectNozzleNo());
			m_liter = PB_wm.getLiter();
			m_basePrice = PB_wm.getBasePrice();
			m_price = PB_wm.getPrice();
			int mode = Change.toValue(PB_wm.getCommandSet());
			
			if (mode==0) { // ���׼���
				m_nSetPrice = Change.toValue(m_price);
				m_dispPriceLiter = Change.toString("W%d",m_nSetPrice);
			}
			else { // ��������
				m_nSetPrice = (Change.toValue(m_liter)/1000) * Change.toValue(m_basePrice.substring(0,4)) - 1;
				m_dispPriceLiter = m_liter.substring(0,4) + "." + m_liter.substring(4,6) + "L";
			}
			
			String oilName = m_oilNameTbl.get(PB_wm.getConnectNozzleNo());
			
			m_nODTState = 10;

			// ���εǾ����ϴ�. OO�� �����̰� 000�Դϴ�. �����⸦ ��� ������ �ּ���.
			if (m_oilKind == DIESEL) {
				ODTCtrl_C1 ((byte) '1', "09", "11", (byte) 0x00, (byte) 0x00, "1005760757563");
			}
			else if (m_oilKind == ADIESEL) {
				ODTCtrl_C1 ((byte) '1', "09", "11", (byte) 0x00, (byte) 0x00, "1005762757563");
			}
			else if (m_oilKind == GASOLINE) {
				ODTCtrl_C1 ((byte) '1', "09", "11", (byte) 0x00, (byte) 0x00, "1005759757563");
			}
			else if (m_oilKind == PGASOLINE) {
				ODTCtrl_C1 ((byte) '1', "09", "11", (byte) 0x00, (byte) 0x00, "1005761757563");
			}

			makeStatusInfo (235); // �������

			LogUtility.getPumpALogger().debug("������/������������(PB) ����, �����㰡(PB) �߻� : ODT_No=" + PB_wm.getNozzleNo()+ 
				" nozzle=" + PB_wm.getConnectNozzleNo() + " mode=" + PB_wm.getCommandSet()+ 
				" liter=" + m_liter + " bPrice=" + m_basePrice + " price=" + m_price);
			
			skip = true;
		}
		else if (wm.getCommand().equals("CB")) { // ������ Ȯ������
			
			CB_wm = (CB_WorkingMessage) wm;
				
			LogUtility.getPumpALogger().info(new StringBuffer("\n�پ��뼿��ODT �������� >>> ������ Ȯ������(CB)" )
										 .append("\n ODT_No=" ).append( CB_wm.getNozzleNo() )
										 .append("\n nozzle=" ).append( CB_wm.getConnectNozzleNo() ).append( "(CB)" ) 
										 .append("\n custType =[" ).append( CB_wm.getCustomerType() ).append( "]" ) 
										 .append("\n saveBonus=" ).append( CB_wm.getSaveBonus() ).append( "\n").toString());
				
			m_nCustType = Change.toValue(CB_wm.getCustomerType());
			m_sSaveBonus  = CB_wm.getSaveBonus();
			
			//m_nCustType = 3; // temp
							
			// ������
			if (m_nCustType == 0) { // ����

				//������ ������ �����ϴ�. �繫�ǿ��� Ȯ���Ͽ� �ּ���.
				ODTCtrl_C1 ((byte) '1', "01", "01", (byte) 0x00, (byte) 0x00,"100587575676900");

				// �����ð� �� �ʱ����(��������)�� ��������
				//insertDevCtrlOrder_42((byte)'0', "00", "01", "010"); // Ÿ�̸� ����
				//m_nODTState=8;
				m_nODTState=1;

				LogUtility.getPumpALogger().info("###### 15 : ������ Ȯ��-���ΰ���. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");

			}
			else if (m_nCustType == 1) { // �Ϲ�
				m_nODTState = 5;
					
				m_creditNo = m_custCardNo;
				m_custCardNo = "";
				
				//�����ݾ��� ������ �ּ���.
				ODTCtrl_C1 ((byte) '1', "05", "01", (byte) 0x00, (byte) 0x00, "1001200");
					
				LogUtility.getPumpALogger().info("###### 15 : �Ϲ�(�ſ�ī��) ���Դϴ�. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");

			}
			else if (m_nCustType == 2) { // ���ݰŷ�ó
				m_nODTState = 2;

				//���ݰŷ�ó ���Դϴ�. ��������� ������ �ּ���.
				ODTCtrl_C1 ((byte) '1', "02", "11", (byte) 0x00, (byte) 0x00, "10011");
					
				LogUtility.getPumpALogger().info("###### 15 : ���ݰŷ�ó ���Դϴ�. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");

			}
			else if (m_nCustType == 3) { // �ܻ�ŷ�ó
				m_nODTState = 5;

				//�ܻ�ŷ�ó ���Դϴ�. �����ݾ��� ������ �ּ���.
				ODTCtrl_C1 ((byte) '1', "05", "01", (byte) 0x00, (byte) 0x00, "100107575751200");
					
				LogUtility.getPumpALogger().info("###### 15 : �ܻ�ŷ�ó ���Դϴ�. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");

			}
			else if (m_nCustType == 4) { // �ܻ�ŷ�ó-������
				m_nODTState = 5;
					
				//�����ݾ��� ������ �ּ���.
				ODTCtrl_C1 ((byte) '1', "05", "01", (byte) 0x00, (byte) 0x00, "100107575751200");
					
				LogUtility.getPumpALogger().info("###### 15 : �ܻ�ŷ�ó-���� ���Դϴ�. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
					
			}
			else { // Fail
										
				m_nODTState = 4;
				m_nCustType = 1; // �Ϲݰ�
				m_sSaveBonus = "1";

				//�����ݾ��� ������ �ּ���.
				ODTCtrl_C1 ((byte) '1', "05", "01", (byte) 0x00, (byte) 0x00, "1001200");
					
				LogUtility.getPumpALogger().info("###### 15 : ������ Ȯ�� ���� -> �Ϲ�(�ſ�ī��)���� �����մϴ٤�. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
			}
		}
		else if (wm.getCommand().equals("QM")) { // ī�� ��������
			QM_wm = (QM_WoringMessage) wm;
			
			LogUtility.getPumpALogger().debug(new StringBuffer("\nGS����ODT �������� >>> ��������(QM) : " ).append( QM_wm.getCommand() )
										 .append("\n ODT_No=" ).append( QM_wm.getNozzleNo() )
										 .append("\n nozzle=" ).append( QM_wm.getConnectNozzleNo() )
										 .append("\n mode  =[" ).append( QM_wm.getMode() ).append( "]" )
										 .append("\n liter =" ).append( QM_wm.getLiter() )
										 .append("\n bPrice=" ).append( QM_wm.getBasePrice() )
										 .append("\n price =" ).append( QM_wm.getPrice() ).append( "\n").toString());
			
			//QM_wm.setMode("1"); // for test
			
			short mode = (short) Change.toValue(QM_wm.getMode());
			
			if (mode == 1) { // ����OK
				
				m_nODTState = 10;
				
				PB_wm = new PB_WorkingMessage();
				//PB_wm.setDirection(IPumpConstant.DIRECTION_FROM_ODT);
				PB_wm.setPassThrough(false);
				PB_wm.setNozzleNo(wm.getNozzleNo()); // ODT No
				PB_wm.setConnectNozzleNo(wm.getConnectNozzleNo()); // Nozzle No
				PB_wm.setTargetNozzleNo(wm.getConnectNozzleNo());  // Nozzle No
				PB_wm.setCommandSet(Change.toString(m_nPresetMode));
//				PB_wm.setLiter(m_liter);
//				PB_wm.setBasePrice(m_basePrice);
//				PB_wm.setPrice(m_price);
				PB_wm.setLiter(QM_wm.getLiter());
				PB_wm.setBasePrice(QM_wm.getBasePrice());
				PB_wm.setPrice(QM_wm.getPrice());
				
				insertRecvQueue(PB_wm);
				
				//--- �����ݾ� ����
				m_nSetPrice = Change.toValue(m_price);
				m_dispPriceLiter = Change.toString("W%d",m_nSetPrice);
												
				// ���εǾ����ϴ�. OO�� �����̰� 000�Դϴ�. �����⸦ ��� ������ �ּ���.
				if (m_oilKind == DIESEL) {
					ODTCtrl_C1 ((byte) '1', "09", "11", (byte) 0x00, (byte) 0x00, "1005760757563");
				}
				else if (m_oilKind == ADIESEL) {
					ODTCtrl_C1 ((byte) '1', "09", "11", (byte) 0x00, (byte) 0x00, "1005762757563");
				}
				else if (m_oilKind == GASOLINE) {
					ODTCtrl_C1 ((byte) '1', "09", "11", (byte) 0x00, (byte) 0x00, "1005759757563");
				}
				else if (m_oilKind == PGASOLINE) {
					ODTCtrl_C1 ((byte) '1', "09", "11", (byte) 0x00, (byte) 0x00, "1005761757563");
				}
				
				makeStatusInfo (235); // �������
				
				LogUtility.getPumpALogger().debug("����OK(QM), �����㰡(PB) �߻� : ODT_No=" + QM_wm.getNozzleNo()+ 
						" nozzle=" + QM_wm.getConnectNozzleNo() + " liter=" + m_liter + 
						" bPrice=" + m_basePrice + " price=" + m_price);

			}
			else if (mode==2 || // �������
					 mode==3) { // ������
				
				if (m_nODTState==9) {

					m_nODTState = 1;
					initVariables();
					
					//������ ������ �����ϴ�. �繫�ǿ��� Ȯ���Ͽ� �ּ���.
					ODTCtrl_C1 ((byte) '1', "82", "16", (byte) 0x00, (byte) 0x00, "10058756769");
					

					ODTCtrl_C1 ((byte) '1', "12", "12", (byte) 0x00, (byte) 0x00, "10075");
					ODTCtrl_C1 ((byte) '1', "12", "12", (byte) 0x00, (byte) 0x00, "10075");
					ODTCtrl_C1 ((byte) '1', "12", "12", (byte) 0x00, (byte) 0x00, "10075");
					ODTCtrl_C1 ((byte) '1', "12", "12", (byte) 0x00, (byte) 0x00, "10075");
					ODTCtrl_C1 ((byte) '1', "12", "12", (byte) 0x00, (byte) 0x00, "10075");
					ODTCtrl_C1 ((byte) '1', "12", "12", (byte) 0x00, (byte) 0x00, "10075");
					ODTCtrl_C1 ((byte) '1', "12", "12", (byte) 0x00, (byte) 0x00, "10075");
					ODTCtrl_C1 ((byte) '1', "12", "12", (byte) 0x00, (byte) 0x00, "10075");

					ODTCtrl_C1 ((byte) '1', "01", "01", (byte) 0x00, (byte) 0x00, "10001");
					
					// �ʱ����(��������)�� ��������
					//insertDevCtrlOrder_42((byte)'0', "00", "01", "015"); // Ÿ�̸� ����
					//ODTCtrl_C0 ((byte) '1', "12", "10005");

					LogUtility.getPumpALogger().debug("���ΰ���(QM) : ODT_No=" + QM_wm.getNozzleNo()+ 
							" nozzle=" + QM_wm.getConnectNozzleNo() + " liter=" + m_liter + 
							" bPrice=" + m_basePrice + " price=" + m_price + " m_nODTState="+m_nODTState);

				}
			}
			
			

			// for test
//			if (mode==2 || mode==3) { // ������
//				String prtData = GSSelfODTReceiptMaker.getTestReceipt();
//				LogUtility.getPumpALogger().debug("##### print out : \n"+ prtData);
//				byte[] byCut = {0x0D, 0x0A, 0x0D, 0x0A, 0x0D, 0x0A, 0x0D, 0x0A, 0x1D, 0x56, 0x31}; // ������ ����
//				printReceipt (prtData  + new String (byCut), 1);
//			}
			
			

			skip = true;
		}
		else if (wm.getCommand().equals("S3")) { // ������
			S3_wm = (S3_WorkingMessage) wm;
			
			if (m_isFirstPumping==true) {
				
				m_isFirstPumping = false;
				//insertInitCmd();
				
				//�����Ͻŷ����� ���� ������ ���׹����� ������ õ������ ������ �˴ϴ�.
				ODTCtrl_C1 ((byte) '1', "10", "01", (byte) 0x00, (byte) 0x00, "100707172");
			}

			String szLiter1 = S3_wm.getLiter().substring(0, 4);
			String szLiter2 = S3_wm.getLiter().substring(4, 7);
			
			//insertDisplayData_23 ("01", "01", "Liter:"+szLiter1+"."+szLiter2+ 
								//"      Price:"+S3_wm.getPrice());
			
			if (Change.toValue(S3_wm.getPrice()) >= m_nSetPrice) { 

				if (m_isFirstPumpingEnd==true) {
					m_isFirstPumpingEnd=false;
					
					// ������ �Ϸ� �Ǿ����ϴ�.
					//ODTCtrl_C1 ((byte) '1', "12", "12", (byte) 0x00, (byte) 0x00, "10074756465");

					LogUtility.getPumpALogger().debug("������ ��ŭ ������ �Ǿ����ϴ�. ODT_No=" + S3_wm.getNozzleNo()+ 
							" nozzle=" + S3_wm.getConnectNozzleNo() + 
							" ������=" + m_nSetPrice+ " ������="+ Change.toValue(S3_wm.getPrice()));
				}
			}

			skip = true;
		}
		else if (wm.getCommand().equals("S4")) { // �����Ϸ�
			S4_wm = (S4_WorkingMessage) wm;

			m_isFromODT = false;
			m_isFirstPumping = true;
			m_isFirstPumpingEnd = true;
			
			LogUtility.getPumpALogger().debug(new StringBuffer("\nGS����ODT �������� >>> �����Ϸ�(S4) : " ).append( S4_wm.getCommand() )
										.append("\n ODT_No=" ).append( S4_wm.getNozzleNo() )
										.append("\n nozzle=" ).append( S4_wm.getConnectNozzleNo() )
										.append("\n liter =" ).append( S4_wm.getLiter() )
										.append("\n bPrice=" ).append( S4_wm.getBasePrice() )
										.append("\n price =" ).append( S4_wm.getPrice()).toString());
						
			// ���ᱸ�� �ݾ��ֽð� �������� �޾��ּ���.
			ODTCtrl_C1 ((byte) '1', "12", "12", (byte) 0x00, (byte) 0x00, "10074756465");
			
			//ODTCtrl_C0 ((byte) '1', "10", "01"); // ȭ��
		
			// ������ ����� �ʱ����(��������)�� ��������
			//insertDevCtrlOrder_42((byte)'0', "00", "01", "010"); // Ÿ�̸� ����
			//ODTCtrl_C0 ((byte) '1', "12", "10010"); // Ÿ�̸� ����
			
			m_nODTState=11;
			
			String szLiter1 = S4_wm.getLiter().substring(0, 4);
			String szLiter2 = S4_wm.getLiter().substring(4, 7);
			
			// TR ����������
			m_realPrice     = S4_wm.getPrice();
			m_realLiter     = S4_wm.getLiter();
			m_realBasePrice = S4_wm.getBasePrice();
						
			skip = true;
		}
		else if (wm.getCommand().equals("QL")) { // ���������
			QL_WorkingMessage QL_wm = (QL_WorkingMessage) wm;

			LogUtility.getPumpALogger().debug(new StringBuffer("\nGS����ODT �������� >>> ���������(QL) : " ).append( QL_wm.getCommand() )
										 .append("\n ODT_No=" ).append( QL_wm.getNozzleNo() )
										 .append("\n nozzle=" ).append( QL_wm.getConnectNozzleNo() )
										 .append("\n mode  =" ).append( QL_wm.getMode() )
										 .append("\n length=" ).append( QL_wm.getContent().length() ).append("\n").toString());
			
			String prtData = QL_wm.getContent();

			// for test
			//prtData = GSSelfODTReceiptMaker.getTestReceipt();

			LogUtility.getPumpALogger().debug("##### print out : \n"+ prtData);
			
			byte[] byCut = {0x0D, 0x0A, 0x0D, 0x0A, 0x0D, 0x0A, 0x0D, 0x0A, 0x1D, 0x56, 0x31}; // ������ ����
			
			printReceipt (prtData  + new String (byCut), 1); // ������
						
			

			ODTCtrl_C1 ((byte) '1', "12", "12", (byte) 0x00, (byte) 0x00, "10075");
			ODTCtrl_C1 ((byte) '1', "12", "12", (byte) 0x00, (byte) 0x00, "10075");
			ODTCtrl_C1 ((byte) '1', "12", "12", (byte) 0x00, (byte) 0x00, "10075");
			ODTCtrl_C1 ((byte) '1', "12", "12", (byte) 0x00, (byte) 0x00, "10075");
			ODTCtrl_C1 ((byte) '1', "12", "12", (byte) 0x00, (byte) 0x00, "10075");
			ODTCtrl_C1 ((byte) '1', "12", "12", (byte) 0x00, (byte) 0x00, "10075");
			ODTCtrl_C1 ((byte) '1', "12", "12", (byte) 0x00, (byte) 0x00, "10075");
			ODTCtrl_C1 ((byte) '1', "12", "12", (byte) 0x00, (byte) 0x00, "10075");

			ODTCtrl_C1 ((byte) '1', "01", "01", (byte) 0x00, (byte) 0x00, "10001");

			m_nODTState = 1;
			initVariables();
						
			if (QL_wm.getMode().equals("1")) { // ������ ������
				
				//----- TR ���� ���� -----//
				TR_wm = new TR_WorkingMessage();
				TR_wm.setNozzleNo(QL_wm.getNozzleNo());
				TR_wm.setConnectNozzleNo(QL_wm.getConnectNozzleNo());
				TR_wm.setLiter(m_realLiter);
				TR_wm.setBasePrice(m_realBasePrice);
				TR_wm.setPrice(m_realPrice);
				
				insertRecvQueue(TR_wm);
				
				LogUtility.getPumpALogger().debug(new StringBuffer("\nGS����ODT �۽����� >>> TR ���� ���� : " )
											.append("\n ODT_No=" ).append( TR_wm.getNozzleNo() )
											.append("\n nozzle=" ).append( TR_wm.getConnectNozzleNo() )
											.append("\n liter =" ).append( TR_wm.getLiter() )
											.append("\n bPrice=" ).append( TR_wm.getBasePrice() )
											.append("\n price =" ).append( TR_wm.getPrice() ).append( "\n").toString());
			}
			
			skip = true;
		}
		else if (wm.getCommand().equals("P7")) { // ������ �Ķ���� ����

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
	
	protected void makeStatusInfo(int nozState) throws Exception {

		S8_wm = new S8_WorkingMessage();
		int		statusCode=0;
		int		thisNozType=nozType;
		String 	errorMsg="";
		boolean isNozStatus=false;
		
		switch (nozState) {
			case 231:
				statusCode = 231;
				errorMsg = "�������� ���";
				thisNozType=2;
				isNozStatus=true;
				break;

			case 232:
				statusCode = 232;
				errorMsg = "�ݾ׼��� ���";
				thisNozType=2;
				isNozStatus=true;
				break;

			case 233:
				statusCode = 233;
				errorMsg = "�����Է� ���";
				thisNozType=2;
				isNozStatus=true;
				break;

			case 234:
				statusCode = 234;
				errorMsg = "���ʽ�ī���Է� ���";
				thisNozType=2;
				isNozStatus=true;
				break;

			case 235:
				statusCode = 235;
				errorMsg = "���� ���";
				thisNozType=2;
				isNozStatus=true;
				break;
				
			case 236:
				statusCode = 236;
				errorMsg = "���ο�û ��";
				thisNozType=2;
				isNozStatus=true;
				break;
				
			case 650:
				statusCode = 650; // ����
				errorMsg = "����ODT ����";
				break;
		}
		m_statusCode = statusCode;
		
		if (isNozStatus==true) {
			if (m_targetNozzle!=null)
				S8_wm.setNozzleNo(m_targetNozzle);
		} else
			S8_wm.setNozzleNo(Change.toString("%02d", nozNo));
		
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
	
	//----- ������1(Bytes type) -----//
	protected void ODTCtrl_C0 (byte sequence, String deviceCode, byte[] pbDatas) throws Exception {

		Formatter form = new Formatter();
		byte[] sndBuf = new byte[pbDatas.length + 10];
		
		try {			
			sndBuf[0] = STX;
			sndBuf[1] = nozID;
			sndBuf[2] = '@';
			sndBuf[3] = 'C';
			sndBuf[4] = '0';
			sndBuf[5] = sequence;
			
			byte[] pbDevCode = deviceCode.getBytes();
			System.arraycopy(pbDevCode, 0, sndBuf, 6, 2);
			System.arraycopy(pbDatas, 0, sndBuf, 8, pbDatas.length);
			sndBuf[pbDatas.length + 10 - 2] = ETX;
						
			TxQue.enQueue(sndBuf);

//			LogUtility.getPumpALogger().debug("++++pbDatas.length=" + pbDatas.length + " sndBuf.length=" + sndBuf.length);
			LogUtility.getPumpALogger().debug(">>>>Insert command C0. itemCount=" + TxQue.getItemCount() + " (ODT="+nozNo+")");
			Log.datas(sndBuf, sndBuf.length, 20);
		
		} catch (Exception e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	}
	
	//----- ������1(String type) -----//
	protected void ODTCtrl_C0 (byte sequence, String deviceCode, String datas) throws Exception {

		byte[] sndBuf;
		
		try {
			tC0_ds.editByte  ("sequence", sequence);
			tC0_ds.editString("deviceCode", deviceCode, 2);
			tC0_ds.editString("datas", datas, datas.length());
			
//			LogUtility.getPumpALogger().debug("+++++++++++++ datas.length()=" + datas.length());
			
			sndBuf = tC0_ds.getByteStream();
			TxQue.enQueue(sndBuf);

			LogUtility.getPumpALogger().debug(">>>>Insert command C0. itemCount=" + TxQue.getItemCount() + " (ODT="+nozNo+")");
			Log.datas(sndBuf, sndBuf.length, 20);
		
		} catch (Exception e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	}

	//----- ������2 (ȭ��, ����̽� �Է�, ���� ��������)-----//
	protected void ODTCtrl_C1 (byte sequence, String group, String id, byte devCtrl1, 
			byte devCtrl2, String voiceData) throws Exception {

		byte[] sndBuf;
		
		try {
			tC1_ds.editByte  ("sequence", sequence);
			tC1_ds.editString("group", group, 2);
			tC1_ds.editString("id", id, 2);
			tC1_ds.editByte  ("devCtrl1", devCtrl1);
			tC1_ds.editByte  ("devCtrl2", devCtrl2);
			tC1_ds.editString("voiceDataCnt", Change.toString("%02d", voiceData.length()), 2);
			tC1_ds.editString("voiceData", voiceData, voiceData.length());

			sndBuf = tC1_ds.getByteStream();
			TxQue.enQueue(sndBuf);

			LogUtility.getPumpALogger().debug(">>>>Insert command C1. itemCount=" + TxQue.getItemCount() + " (ODT="+nozNo+")");
			Log.datas(sndBuf, sndBuf.length, 20);
		
		} catch (Exception e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	}

	//----- ������3 (ȭ��, ����̽� �Է�, ���� ��������)-----//
	protected void ODTCtrl_C2 (byte sequence, String group, String id, byte devCtrl1, 
			byte devCtrl2, String voiceData, String rtnGroup, String rtnID, int rtnDataSize) throws Exception {

		byte[] sndBuf;
		
		try {
			tC2_ds.editByte  ("sequence", sequence);
			tC2_ds.editString("group", group, 2);
			tC2_ds.editString("id", id, 2);
			tC2_ds.editByte  ("devCtrl1", devCtrl1);
			tC2_ds.editByte  ("devCtrl2", devCtrl2);
			tC2_ds.editString("voiceDataCnt", Change.toString("%02d", voiceData.length()), 2);
			tC2_ds.editString("voiceData", voiceData, voiceData.length());
			tC2_ds.editString("rtnGroup", rtnGroup, 2);
			tC2_ds.editString("rtnID", rtnID, 2);
			tC2_ds.editString("rtnDataSize", Change.toString("%04d", rtnDataSize), 4);

			sndBuf = tC2_ds.getByteStream();
			
			TxQue.enQueue(sndBuf);

			LogUtility.getPumpALogger().debug(">>>>Insert command C2. itemCount=" + TxQue.getItemCount() + " (ODT="+nozNo+")");
			Log.datas(sndBuf, sndBuf.length, 20);
		
		} catch (Exception e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	}
	
	public void ownershipChange(int arg0) {
		// TODO Auto-generated method stub
		
	}

	protected void printReceipt(String sData, int startSequence) throws Exception {

		byte[] byData = sData.getBytes();
		int nRestLen, nSendLen;
		int nStartPos = 0, nBlockCnt = 0;
		byte bSequence = '1';

		nRestLen = byData.length;
		
		// send start-block
		bSequence = (byte) (startSequence + 0x30);
		ODTCtrl_C0 (bSequence, "11", "S" + "01-PRINT");

	/*
		do {
			if (nRestLen >= 300) {
				nSendLen = 300;
				nRestLen -= nSendLen;
			}
			else {
				nSendLen = nRestLen;
				nRestLen = 0;
			}
			nBlockCnt++;
			
			String sLen = Change.toString("%04d", nSendLen);
			byte[] bySndBuf = new byte[nSendLen + 5];
			
			bySndBuf[0] = 'D';
			System.arraycopy(sLen.getBytes(), 0, bySndBuf, 1, 4); // Length
			System.arraycopy(byData, nStartPos, bySndBuf, 5, nSendLen); // Print data
			
//			LogUtility.getPumpALogger().debug("33+++++++++++++ sLen=" + sLen + " bySndBuf.length=" + bySndBuf.length);
//			Log.datas(bySndBuf, bySndBuf.length, 20);
			
			bSequence = (byte) (startSequence + nBlockCnt + 0x30);
			ODTCtrl_C0 (bSequence, "11", bySndBuf);
			
			nStartPos += nSendLen;
			
		} while (nRestLen > 0);
		*/
		
		Vector receiptVec = ReceiptUtil.splitBs(byData, 300);
		
		for (int i=0; i<receiptVec.size(); i++) {
			
			nBlockCnt++;
			
			byte[] tmpBuf = (byte[]) receiptVec.get(i);
			nSendLen = tmpBuf.length;
			
			String sLen = Change.toString("%04d", nSendLen);
			byte[] bySndBuf = new byte[nSendLen + 5];
			
			bySndBuf[0] = 'D';
			System.arraycopy(sLen.getBytes(), 0, bySndBuf, 1, 4); // Length
			//System.arraycopy(byData, nStartPos, bySndBuf, 5, nSendLen); // Print data
			System.arraycopy(tmpBuf, 0, bySndBuf, 5, nSendLen); // Print data
			
//			LogUtility.getPumpALogger().debug("33+++++++++++++ sLen=" + sLen + " bySndBuf.length=" + bySndBuf.length);
			Log.datas(bySndBuf, bySndBuf.length, 20);
			
			bSequence = (byte) (startSequence + nBlockCnt + 0x30);
			ODTCtrl_C0 (bSequence, "11", bySndBuf);
		}
		
		bSequence++;
		
		// send end-block
		ODTCtrl_C0 (bSequence, "11", "E" + "02-PRINT");
	}

	protected void processDeviceErr() throws Exception {
		
		insertRecvQueue(generateWorkingMessage(RxBuf, null));	
	}
	
	// ------ �Է¿��� ����(i0) ó�� ------//
	protected void processODT (byte[] RxDat) throws SerialConnectException, Exception {
		
		if (m_setEnvDataOK == false)
			return;

		byte[] byt = new byte[2];
		SelectedItemInfo selItem = new SelectedItemInfo();
		String keyGroup="", keyID="", devType="";

		byt[0]=RxDat[3];
		byt[1]=RxDat[4];
		devType = new String (byt);
						
		byt[0]=RxDat[9];
		byt[1]=RxDat[10];
		keyGroup=new String (byt); // Input group
		
		byt[0]=RxDat[11];
		byt[1]=RxDat[12];
		keyID=new String (byt); // Input ID(��ư)

		LogUtility.getPumpALogger().debug("\n<<<< Input ODT=" + nozNo + " keyGroup=" + keyGroup
										+ " keyID=" + keyID + " m_nODTState=" + m_nODTState);

		if (keyGroup.equals("81") && (keyID.equals("05") || // ���
									  keyID.equals("07"))) { // ó������

			initVariables();
			invokeNextPage("01", selItem);
			
			return;
		}
		
		for (int i=0; i<processList.size(); i++) {
			
			String sGroup = processList.get(i);
			int	nGroup = Change.toValue(sGroup);

			if (nGroup == m_nODTState) {
				
				int idx;

				if (keyGroup.equals(sGroup)) { // ���� ȭ������
					
					// �Էµ� �� �м� -> ���õ� ID�� �ش������� return ����
					selItem = processSelectedItem(RxDat, devType, keyGroup, keyID);
										
					idx = i+1 >= processList.size() ? 0 : i+1;
					String sNextGroup = processList.get(idx);
					
					if (m_isProcessRepeat == true) { // �ݺ���
						m_isProcessRepeat = false;
						
						if (m_isProcessNoInvoke==false) {
							invokeNextPage(keyGroup, selItem); //  ����ȭ�� ��ȣ��
							//LogUtility.getPumpALogger().debug("$$$$$���� invokeNextPage(). keyGroup="+keyGroup+" selItem="+selItem.id);
						}
						else // true �̸�
							m_isProcessNoInvoke=false;
					}
					else { // ����ȭ�� ȣ���� ó��
						invokeNextPage(sNextGroup, selItem);
						//LogUtility.getPumpALogger().debug("$$$$$���� invokeNextPage(). keyGroup="+keyGroup+" selItem="+selItem.id);
					}
					
					if (m_isProcessSkip == true) { // �ش� ���μ����� Skip �̸�
						
						m_isProcessSkip = false;
						idx = i+2 >= processList.size() ? 0 : i+2;
						sNextGroup = processList.get(idx);
						
						// Skip�� ȭ���� ����ȭ�� ȣ��
						invokeNextPage(sNextGroup, selItem);
					}
				} 
				else if (keyGroup.equals("81") && keyID.equals("05")) { // ���� ȭ������

					idx = i-1 <= 1 ? 0 : i-1;
					String sBeforeGroup = processList.get(idx);
					
					LogUtility.getPumpALogger().debug("��Ҽ��� >>>> sBeforeGroup=" + sBeforeGroup +
							" idx=" + idx + " id=" + selItem.id);
							
					invokeNextPage(sBeforeGroup, selItem);
				}
			}
		}
		
		/*
		//--- ODT ���μ��� ���� (Hard coding ���)
		if (m_nODTState == 1) { // ���ȭ��

			if (keyGroup.equals("01")) {
				initVariables();

				selItem = processSelectedItem(RxDat, keyGroup, keyID);
				invokeNextPage("04", selItem);
			}
		}
		else if (m_nODTState == 4) { // ������� ����ȭ��

			if (keyGroup.equals("04")) {
				selItem = processSelectedItem(RxDat, keyGroup, keyID);
				invokeNextPage("02", selItem);
			} 
			else if (keyGroup.equals("81") && keyID.equals("06")) { // ��������
				invokeNextPage("01", selItem);
			}
		} 
		else if (m_nODTState == 2) { // �����Է� ȭ��

			if (keyGroup.equals("02")) {
				selItem = processSelectedItem(RxDat, keyGroup, keyID);
				invokeNextPage("03", selItem);
			} 
			else if (keyGroup.equals("81") && keyID.equals("06")) { // ��������
				invokeNextPage("01", selItem);
			}
		} 
		else if (m_nODTState == 3) { // ���� ����ȭ��

			if (keyGroup.equals("03")) {
				selItem = processSelectedItem(RxDat, keyGroup, keyID);
				invokeNextPage("05", selItem);
			} 
			else if (keyGroup.equals("81") && keyID.equals("06")) { // ��������
				invokeNextPage("02", selItem);
			}
		} 
		else if (m_nODTState == 5) { // ���� ����ȭ��

			if (keyGroup.equals("05")) { 
				selItem = processSelectedItem(RxDat, keyGroup, keyID);
				invokeNextPage("06", selItem);
			} 
			else if (keyGroup.equals("81") && keyID.equals("06")) { // ��������
				invokeNextPage("04", selItem);
			}
		} 
		else if (m_nODTState == 6) { // ���ʽ� �Է�ȭ��

			if (keyGroup.equals("06")) {
				selItem = processSelectedItem(RxDat, keyGroup, keyID);
				invokeNextPage("09", selItem);
			} 
			else if (keyGroup.equals("81") && keyID.equals("06")) { // ��������
				invokeNextPage("05", selItem);
			}
		}
		*/
	}

	protected void processRecvSTX (byte[] RxDat) throws SerialConnectException, Exception {
	
		//System.out.printf ("==========[3]=%c [4]=%c\n", RxDat[3], RxDat[4]);
		
		//############# State check ##############//
		if (RxDat[0]=='m' && RxDat[1]=='0') { // OPT ���� ����

			makeStatusInfo (650); // ������������(����)
			
			switch (RxDat[3]) { // ���۸�� ('0'=Initial mode, '1'=POS mode)
			
			case '0': // Initial mode (ȯ�漳��)

				insertEnvDataToTxQue (); 
				LogUtility.getPumpALogger().debug("\n######### Completed GS Self ODT Initializtion : ODT=" +	nozStr + "#########\n");
				break;
				
			case '1': // POS mode (���� ����)

				m_nODTState = 1;
				

				processList.add("01"); // ���ȭ��

				processList.add("04"); // ��������
				processList.add("02"); // ������� ����
				processList.add("03"); // ���� �Է�
				processList.add("05"); // �ݾ�/���� �Է�
				processList.add("06"); // ���ʽ� �Է�
				processList.add("07"); // ���ݿ����� �Է�
				processList.add("08"); // ķ���� �Է�
				
				processList.add("09"); // ���� ��û
				
				
				// ���ȭ��+�λ縻
				ODTCtrl_C1 ((byte) '1', "01", "01", (byte) 0x00, (byte) 0x00, "10001");

				LogUtility.getPumpALogger().debug("###### 0 : �ʱ���� �Ͽ����ϴ�. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
				
				break;
			}
		}
		else if (RxDat[0]=='i' && RxDat[1]=='0') { // ODT �Է����� ����

			processODT (RxDat); //---- ODT �Էµ����� ó�� ----//
		}
		else if (RxDat[0]=='s' && RxDat[1]=='0') { // ODT �������� ����

			if (RxDat[3]=='0' && RxDat[4]=='0') // ����
				makeStatusInfo(650);
		}

	}
	
	protected SelectedItemInfo processSelectedItem (byte[] RxDat, String devType, String keyGroup, String keyID) throws Exception {

		SelectedItemInfo selItem = new SelectedItemInfo();
		
		//LogUtility.getPumpALogger().debug("\n>>>>>> Input keyGroup=" + keyGroup + " keyID=" + keyID + "\n");

		if (keyGroup.equals("01")) {

			m_nODTState = 1;
			selItem.id = "01";
			selItem.voice = "10001";
			LogUtility.getPumpALogger().debug("###### 1 : ODT �۵����� �Ͽ����ϴ�. m_nODTState=" + 
					m_nODTState	+ " (ODT=" + nozNo + ")");
		}
		 //--- ������� ���� ---//
		else if (keyGroup.equals("02")) {

			switch (Change.toValue(keyID)) {
			case 1:
				// ���� �Է�ȭ��(�ſ�ī��)���� ����
				m_nPayType = 1; // ����
				selItem.id = "01";
				selItem.voice = "1007348";
				LogUtility.getPumpALogger().debug("###### 1 : �������-���� �����Ͽ����ϴ�. m_nODTState="
								+ m_nODTState + " (ODT=" + nozNo + ")");
				break;

			case 2:
				// ���� �Է�ȭ��(����)���� ����
				m_nPayType = 2; // �ſ�ī��
				selItem.id = "02";
				selItem.voice = "10008";
				LogUtility.getPumpALogger().debug("###### 1 : �������-�ſ�ī�� �����Ͽ����ϴ�. m_nODTState="
								+ m_nODTState + " (ODT=" + nozNo + ")");
				break;

			case 3:
				// ���� �Է�ȭ��(�ŷ�óī��)���� ����
				m_nPayType = 3; // �ŷ�óī��
				selItem.id = "03";
				selItem.voice = "10009";
				LogUtility.getPumpALogger().debug("###### 1 : �������-�ŷ�óī�� �����Ͽ����ϴ�. m_nODTState="
								+ m_nODTState + " (ODT=" + nozNo + ")");
				break;
			}
		} 
		//--- ���� �Է� ---//
		else if (keyGroup.equals("03")) { // �����Է�(����/�ſ�ī��/�ŷ�óī�� �Է�)

			LogUtility.getPumpALogger().debug("m_nPayType========>"+m_nPayType);
			
			if (devType.equals("01")) { // ī���Է�
				
				if (m_nPayType==2) { // �ſ�ī��
					
					m_isCreditMode = true;
				
					m_creditNo = getMSCardNumber(RxDat);
					
					if(m_creditNo.length() < 10) {
						m_isProcessRepeat = true;
						selItem.id = keyID;
					} 
					else 
						selItem.id = "01";
					
					selItem.voice = "100";
							
					LogUtility.getPumpALogger().debug("###### 7 : �ſ�ī�� �Է��Ͽ����ϴ�. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
					LogUtility.getPumpALogger().debug("m_byCreditNo="+m_creditNo+" len="+m_creditNo.length());
				}
				else if (m_nPayType==3) { // �ŷ�ó ī��
					
					m_custCardNo = getMSCardNumber(RxDat);
					
					if(m_custCardNo.length() < 10) {
						m_isProcessRepeat = true;
						selItem.id = keyID;
					} 
					else 
						selItem.id = "01";
					
					selItem.voice = "100";
								
					LogUtility.getPumpALogger().debug("###### 7 : �ŷ�óī�� �Է��Ͽ����ϴ�. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
					LogUtility.getPumpALogger().debug("m_custCardNo="+m_custCardNo+" len="+m_custCardNo.length());
				}
			}
			else if (devType.equals("03")) { // �Ϸ��ư �Է�
				
				if (m_nPayType==1) { // ���ݰ���
						
					selItem.id = "01";
					selItem.voice = "100";
					
					LogUtility.getPumpALogger().debug("###### 7 : ���� �Է��Ͽ����ϴ�. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");		
				}
			}
			else if (devType.equals("04")) { // �����Է�(BNA �Էµ�����)
				
				String str   = new String(RxBuf);
				String sCash = str.substring(34, 34+8); // �̹� �Է±ݾ�
				m_cashCount  = str.substring(42, 42+8); // ���� �Է±ݾ�
				m_price = m_cashCount;
				//int	   nCash	= Change.toValue(sCash);
											
				BI_wm = new BI_WorkingMessage();
				BI_wm.setNozzleNo(nozStr); // ODTNo
				BI_wm.setConnectNozzleNo(m_targetNozzle); // nozNo
				BI_wm.setCash(sCash);
				BI_wm.setCashCount(m_cashCount);

				insertRecvQueue(BI_wm);

				m_isInsertedCash = true;
				
				m_isProcessRepeat = true; // �ݺ�
				m_isProcessNoInvoke = true; // �ݺ������� ȭ��ȣ���� ���� ����
			}		
		}
		//--- �������� ---//
		else if (keyGroup.equals("04")) { 

			if (keyID.equals("01"))
				m_targetNozzle = m_nozNoVec.get(0);
			else if (keyID.equals("02"))
				m_targetNozzle = m_nozNoVec.get(1);
			else if (keyID.equals("03"))
				m_targetNozzle = m_nozNoVec.get(2);

			m_oilKind = getOilKind(m_targetNozzle);
			m_basePrice = m_basePriceTbl.get(m_targetNozzle);

			// ���� ���÷���
			String oilName = m_oilNameTbl.get(m_targetNozzle);
			String oilVoice = "";

			if (m_oilKind == DIESEL) { // ����
				oilVoice = "06";
			} else if (m_oilKind == ADIESEL) { // ��ް��� �����Ӱ���
				oilVoice = "05";
			} else if (m_oilKind == GASOLINE) { // ���� �ֹ���
				oilVoice = "04";
			} else if (m_oilKind == PGASOLINE) { // ����ֹ��� ű��������
				oilVoice = "03";
			}

			selItem.id = "01";
			selItem.voice = "100" + oilVoice;

			LogUtility.getPumpALogger().debug("###### 1 : ���������Ͽ����ϴ�. m_nODTState=" + m_nODTState
							+ " Oil name=" + oilName + " (ODT=" + nozNo + ")");
		}
		//--- �ݾ�/���� ���� ---//
		else if (keyGroup.equals("05")) {

			if (RxDat[17] == 'F') { // ��������
				m_price = "00149900";
				m_isFullPumping = true;
			} else if (RxDat[17] == 'P') { // ��������
				String val = getInputValue(RxDat);
				m_price = val.substring(1, val.length());
				m_nPresetMode = PRESET_PRICE;
			} else if (RxDat[17] == 'L') { // ��������
				String val = getInputValue(RxDat);
				m_liter = val.substring(2, val.length());
				m_nPresetMode = PRESET_LITER;
			}

			selItem.id = "01";
			selItem.voice = "100";

			LogUtility.getPumpALogger().debug("###### 1 : �ݾ�/���� �����Ͽ����ϴ�. m_nODTState=" + m_nODTState
							+ " (ODT=" + nozNo + ")");
			LogUtility.getPumpALogger().debug("m_nPresetMode=" + m_nPresetMode + " m_price=" + m_price
							+ " m_liter=" + m_liter);
		}
		//--- ���ʽ�ī�� �Է� ---//
		else if (keyGroup.equals("06")) { 

			m_isBonusMode = true;
			
			if (devType.equals("01")) {
				m_bonusNo = getMSCardNumber(RxDat);
				
				if(m_bonusNo.length() < 10) {
					m_isProcessRepeat = true;
					selItem.id = keyID;
				} 
				else 
					selItem.id = "01";
			}
			else if (devType.equals("03")) {
				selItem.id = "01";
			}

			selItem.voice = "100";

			LogUtility.getPumpALogger().debug("###### 10 : ���ʽ�ī�� �Է��Ͽ����ϴ�. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
			LogUtility.getPumpALogger().debug("���ʽ�ī�� ��ȣ="+m_bonusNo+" len="+m_bonusNo.length());

//			//----- ī����ο�û -----//
//			boolean bApprove = requestCardApprove ();
//						
//			LogUtility.getPumpALogger().debug("###### 11 : ���� ��û�Ͽ����ϴ�. : state="+m_nODTState+ " approve="+bApprove+
//					" m_nODTState="+m_nODTState + " (ODT="+nozNo+")");
			
		}
		//--- ���ݿ����� �Է� ---//
		else if (keyGroup.equals("07")) {

			//LogUtility.getPumpALogger().debug("\n>>>(selectedItem-1) m_nCashReceiptStep=" + m_nCashReceiptStep);

			if (m_nCashReceiptStep == 2) { // ���ݿ����� ��������
				
				if (keyID.equals("01")) {
					m_nCashReceiptType=1;
					m_isProcessRepeat=true;
					LogUtility.getPumpALogger().debug("###### 10 : ���ݿ����� ����=�Һ��� �����Ͽ����ϴ�. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
				} 
				else if (keyID.equals("02")) {
					m_nCashReceiptType=2;
					m_isProcessRepeat=true;
					LogUtility.getPumpALogger().debug(")###### 10 : ���ݿ����� ����=����� �����Ͽ����ϴ�. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
				}
				else if (keyID.equals("03")) {
					m_nCashReceiptType=3;
					m_isProcessRepeat=false;
					LogUtility.getPumpALogger().debug("###### 10 : ���ݿ����� �߱޾����� �����Ͽ����ϴ�. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
				}
			}
			else if (m_nCashReceiptStep == 3) { // ���ݿ����� �Է�(ī��/��ư)
				
				if (devType.equals("01")) { // ī�� �Է�
					
					m_isProcessRepeat=false;
					m_cashReceiptNo = getMSCardNumber(RxDat);
					
					LogUtility.getPumpALogger().debug("###### 10 : ���ݿ����� ī���Է��Ͽ����ϴ�. m_cashReceiptNo=" + m_cashReceiptNo + " (ODT="+nozNo+")");
					LogUtility.getPumpALogger().debug("���ݿ����� ��ȣ="+m_cashReceiptNo+" len="+m_cashReceiptNo.length());
				}
				else if (devType.equals("06")) { // �Է°��(��ư)
					
					m_isProcessRepeat=false;
					String val = getInputValue(RxDat);
					m_cashReceiptNo = val.substring(0, val.length());
					
					LogUtility.getPumpALogger().debug("###### 10 : ���ݿ����� ��ư�Է��Ͽ����ϴ�. m_cashReceiptNo=" + m_cashReceiptNo + " (ODT="+nozNo+")");
					LogUtility.getPumpALogger().debug("���ݿ����� ��ȣ="+m_cashReceiptNo+" len="+m_cashReceiptNo.length());
				}
				else 
					m_isProcessRepeat=true;
				
				//LogUtility.getPumpALogger().debug("\n>>>(selectedItem-2)###### 10 : ���ݿ����� �Է·�ƾ. m_nODTState=" + m_nODTState + " devType=" + devType + " (ODT="+nozNo+")");
				//LogUtility.getPumpALogger().debug("���ݿ����� ��ȣ="+m_bonusNo+" len="+m_bonusNo.length());
			}
			
			//LogUtility.getPumpALogger().debug("\n>>>(selectedItem-3)m_isProcessRepeat=" + m_isProcessRepeat +"\n");

			selItem.id = "01";
			selItem.voice = "100";
		}
		//--- ķ���� �Է� ---//
		else if (keyGroup.equals("08")) { 

			selItem.id = "01";
			selItem.voice = "100";

			LogUtility.getPumpALogger().debug("###### 11 : ķ���� �Է��Ͽ����ϴ�. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
			//LogUtility.getPumpALogger().debug("���ݿ����� ��ȣ="+m_bonusNo+" len="+m_bonusNo.length());

		}
		//--- ���ο�û ---//
		else if (keyGroup.equals("09")) { 

			selItem.voice = "100";
						
			LogUtility.getPumpALogger().debug("###### 12 : ���� ��û�Ͽ����ϴ�. : state="+m_nODTState+ " approve="+m_isApprove+
					" m_nODTState="+m_nODTState + " (ODT="+nozNo+")");
		}
//		else if (keyGroup.equals("81")) { 
//
//			if (keyID.equals("02") || keyID.equals("03") || keyID.equals("04")) {
//				selItem.id = "01";
//				selItem.voice = "100";
//			}
//		}
		
		return selItem;
	}
	
	
	protected boolean recvTail_proc () throws Exception, SerialConnectException {
		
		//--- send : DLE ---//
		if (sendText (DLE1)==false) {
			if (dispLevel>=3)
				LogUtility.getPumpALogger().debug("S5.Send DLE1 fail! (ODT="+nozNo+")");
			return false;
		}
		
		if (dispLevel>=3)
			LogUtility.getPumpALogger().debug("S5.Send DLE1 (ODT="+nozNo+")");

		//--- recv : EOT ---//
		if (recvText(RxBuf) < 1) {
			if (dispLevel>=3)
				LogUtility.getPumpALogger().debug("S5.Recv EOT fail! (ODT="+nozNo+")");
			return false;
		}
	
		if (RxBuf[0] == EOT) {	//--- recv : EOT ---//
			if (dispLevel>=3)
				LogUtility.getPumpALogger().debug("S5.Recv EOT (ODT="+nozNo+")");
			//return true;
		}
		else {
			if (dispLevel>=3)
				LogUtility.getPumpALogger().debug("S5.Recv EOT fail (ODT="+nozNo+")");
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
					if (RxBuf[i]==EOT || RxBuf[i]==DLE || RxBuf[i]==NAK) { // recv EOT/DLE/NAK
						
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
	
	protected boolean requestCardApprove () throws Exception {
		
		HE_wm = new HE_WorkingMessage();
		HE_wm.setNozzleNo(nozStr); // ODTNo
		HE_wm.setConnectNozzleNo(m_targetNozzle); // nozNo
		HE_wm.setCardType("1");
		HE_wm.setCardNumber(m_creditNo);
		HE_wm.setBonusCard(m_bonusNo);
		HE_wm.setCustCardNo(m_custCardNo);
		HE_wm.setCashCount(m_cashCount);
		HE_wm.setCashReceiptNo(m_cashReceiptNo);
		HE_wm.setLiter(m_liter);
		HE_wm.setBasePrice(m_basePrice);
		HE_wm.setPrice(m_price);
		HE_wm.setCustomerType(Change.toString("%d", m_nCustType));
		
		if (m_isFullPumping==true)
			HE_wm.setIsFullPumping("1"); // ��������
		else
			HE_wm.setIsFullPumping("0");
		
//		if (Change.toValue(m_price) > 0)
//			m_preset = PRESET_PRICE;
//		else if (Change.toValue(m_liter) > 0)
//			m_preset = PRESET_LITER;
//		else
//			m_preset = PRESET_NONE;
       
		LogUtility.getPumpALogger().info(new StringBuffer("\nGS����ODT ���ο�û ����(HE)" )
									.append("\n ODT_No=" ).append( HE_wm.getNozzleNo() )
									.append("\n nozzle=" ).append( HE_wm.getConnectNozzleNo() ).append( "(HE)" )
									.append("\n type     =" ).append( HE_wm.getCardType() )
									.append("\n liter    =" ).append( HE_wm.getLiter() )
									.append("\n basePrice=" ).append( HE_wm.getBasePrice() )
									.append("\n price    =" ).append( HE_wm.getPrice() )
//									.append("\n creditNo =" ) Base64Util.encode(getCardTrack1Data(HE_wm.getCardNumber())) )
//									.append("\n bonusNo  =" ) Base64Util.encode(getCardTrack1Data(HE_wm.getBonusCard())) )
//									.append("\n custNo   =" ) Base64Util.encode(getCardTrack1Data(HE_wm.getCustCardNo())) )
									.append("\n creditNo =" ).append( "[" ).append( getCardTrack1Data(HE_wm.getCardNumber()) ).append( "]" )
									.append("\n bonusNo  =" ).append( "[" ).append( getCardTrack1Data(HE_wm.getBonusCard()) ).append( "]" )
									.append("\n custNo   =" ).append( "[" ).append( getCardTrack1Data(HE_wm.getCustCardNo()) ).append( "]" )
									.append("\n cashCount=" ).append( "[" ).append( HE_wm.getCashCount() ).append( "]" )
//									.append("\n cashRctNo=" ) Base64Util.encode(getCardTrack1Data(HE_wm.getCashReceiptNo())) )
									.append("\n cashRctNo=" ).append( "[" ).append( getCardTrack1Data(HE_wm.getCashReceiptNo()) ).append( "]" )
									.append("\n isFullPmp=" ).append( HE_wm.getIsFullPumping() )
									.append("\n custType =" ).append( HE_wm.getCustomerType() ).append("\n").toString());

		insertRecvQueue(HE_wm);
		
		//insertDevCtrlOrder_42((byte)'1', "02", "01", "00000000"+"0000000000000000");
		//ODTCtrl_C1 ((byte) '1', "00", "01", (byte) 0x00, (byte) 0x00, "100232323");
		//insertInitCmd();

		//makeStatusInfo (236); // ���ο�û ��
		
		m_isFromODT=true;
		
		return true;
	}

	protected boolean requestCheckCustmerType (String cardNo) throws Exception {
		
		CA_WorkingMessage CA_wm = new CA_WorkingMessage();
		CA_wm.setNozzleNo(nozStr); // ODTNo
		CA_wm.setConnectNozzleNo(m_targetNozzle);
		CA_wm.setCardNo(cardNo);
		                  
		LogUtility.getPumpALogger().info(new StringBuffer("\nǥ�ؼ���ODT ������ Ȯ�ο�û(CA)" )
									 .append("\n ODT_No=" ).append( CA_wm.getNozzleNo() ) 
									 .append("\n nozzle=" ).append( CA_wm.getConnectNozzleNo() ).append( "(CA)" )
									 .append("\n cardNo  =" ).append( "[" ).append( getCardTrack1Data(CA_wm.getCardNo()) ).append( "]" ).append( "\n").toString());
	
		insertRecvQueue(CA_wm);
		
		//insertDevCtrlOrder_42((byte)'0', "00", "02", "000"); // Ÿ�̸� ���� 
//		insertDevCtrlOrder_42((byte)'1', "02", "01", "00000000"+"0000000000000000");
//		insertInitCmd();
		
		return true;
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
		
		// ȸ���ҷ� ���� ó��
		if (firstRequest==true) {
			firstRequest=false;
			TxQue.enQueue(tS0_Buf); // ����������û
			TxQue.enQueue(tM0_Buf); // ���������û
			ODTCtrl_C1 ((byte) '1', "01", "01", (byte) 0x00, (byte) 0x00, "10001");
		}
		if (m_statusCode==601) { // ȸ���ҷ�
			if (lineCommCheckCnt >= (minErrCnt+5)) {
				lineCommCheckCnt=0;
				TxQue.enQueueNewer(tS0_Buf); // ����������û
			}
			lineCommCheckCnt++;
		}
		
		flushBuffer(RxBuf);

		//--- Transfer from WorkingMessage to BytesStream
		while (sndQue.getItemCount() > 0) {
			WorkingMessage wm = (WorkingMessage)sndQue.deQueue();
			
			LogUtility.getPumpALogger().debug("\n>>>>> Received  Down data : " +
								"ODT=" + nozNo + " command=" + wm.getCommand());
			
			if (isSkipWorkingMessage(wm)==true) {
				wm=null;
				continue;
			}
			
			byte[] wmByte = generateByteStream(wm);
			
			if (wmByte != null) {
				if (chkEnvDataAndSaving(wmByte) == false) // If EnvData then saving to dataStruct
					TxQue.enQueue(wmByte); // ȯ�漳�������� �ƴϸ�
			}
			/*
			if (wm.getCommand().equals("PB")) 
				Show.datas(wmByte, wmByte.length, 20);
			*/			
			wm=null;
		}
		if (dispLevel>=3)
			LogUtility.getPumpALogger().debug("\nStart request ===========> TatsunoSelfODT (Noz="+nozNo+")");

		mode = SEN;
		while (true) {

			flushBuffer(RxBuf);
			
			if (loopCnt >= 2) break;
			loopCnt++;
			
			try {
				//##### Send ENQ #####//
				mode = (mode==POL? SEN : POL);
				ENQb[2] = mode;
				
				if (mode==SEN) {
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

				//--- recv DLE/STX ---//	
				if (recvText(RxBuf) < 1) {
					lineErrCnt++;
					if (dispLevel>=2)
						LogUtility.getPumpALogger().debug("0.Recv DLE/STX fail!(No response) lineErrCnt="+lineErrCnt+"/"+minErrCnt+" (Noz="+nozNo+")");
					else if (lineErrCnt >= minErrCnt)
						LogUtility.getPumpALogger().debug("0.Recv DLE/STX fail!(No response) lineErrCnt="+lineErrCnt+"/"+minErrCnt+" (Noz="+nozNo+")");

					continue;
				}
				
				//###### Recv data(Polling mode) : STX ######//
				if (mode==POL) { // 0x3F, '?'

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

							if (m_isSaveSTX==true) 
								LogUtility.getPumpALogger().info("Recv STX("+Change.toString("%02d", nozNo)+
										") : ["+pack(RxBuf, ETX)+"]");
							else
								LogUtility.getPumpALogger().debug("Recv STX("+Change.toString("%02d", nozNo)+
										") : ["+pack(RxBuf, ETX)+"]");
							
							processRecvSTX(getPureData(RxBuf)); // ���� ���� ������ ó��
							
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
				else if (mode==SEN) { // 0x40, '@'

					if (RxBuf[0]==DLE) { // DLE + SA
						
						if (compareNozID(RxBuf)==false) { 
							LogUtility.getPumpALogger().debug ("2.Recv DLE NozID mismatch-1.0! (Noz="+nozNo+")");
							Log.datas(RxBuf, 40, 20);
							lineErrCnt++;
							
							is.read(); // flush inputStream
							continue;
						}
						
						is.read (byt,0,1);
						
						if (byt[0]==0x30) { // recv : DLE0

							if (dispLevel>=3)
								LogUtility.getPumpALogger().debug("2.Recv DLE0 (Noz="+nozNo+")");
		
							if (TxQue.isEmpty()==false) { // �۽� ������ ������
	
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
								
								if (TxBuf.length > 50) { // �߰�(09/01/07)
									Sleep.sleep(TxBuf.length);
									LogUtility.getPumpALogger().debug("�߰����� �߻�. TxBuf.length=" + TxBuf.length + "(ODT="+nozNo+")");
								}
				
								if (recvText(RxBuf) < 1) {
									if (dispLevel>=3)
										LogUtility.getPumpALogger().debug("2.Recv DLE1 fail! (Noz="+nozNo+")");
									lineErrCnt++;
									continue;
								}
								
								if (RxBuf[0]==DLE) {
									
									if (compareNozID(RxBuf)==false) { 
										LogUtility.getPumpALogger().debug ("2.Recv DLE1 NozID mismatch! (Noz="+nozNo+")");
										Log.datas(RxBuf, 40, 20);
										lineErrCnt++;
										
										is.read(); // flush inputStream
										continue;
									}
									
									is.read (byt,0,1);
									if (byt[0]==0x31) { // recv : DLE1
										
										if (m_isSaveSTX==true) 
											LogUtility.getPumpALogger().info("Send STX("+Change.toString("%02d", nozNo)+
													") : ["+new String(TxBuf)+"]");
										else
											LogUtility.getPumpALogger().debug("Send STX("+Change.toString("%02d", nozNo)+
													") : ["+new String(TxBuf)+"]");
										
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
								else if (RxBuf[0]==NAK) {	// recv : NAK
									is.read (byt,0,1);
									//sendText(TxBuf); // ������
									
									if (dispLevel>=3) 
										LogUtility.getPumpALogger().debug("2.Recv NAK! (Noz="+nozNo+")");
									lineErrCnt++;
								}
								else {	
									if (dispLevel>=3) 
										LogUtility.getPumpALogger().debug("2.Recv DLE fail! (Noz="+nozNo+")");
									lineErrCnt++;
								}
							}
						}
						else { // recv mismatched data
							LogUtility.getPumpALogger().debug("2.recv mismatched data! (Noz="+nozNo+")");
							Log.datas(RxBuf, 40, 20);
							lineErrCnt++;
							
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
