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
import com.gsc.kixxhub.device.pumpa.define.CCommDriver;
import com.gsc.kixxhub.device.pumpa.translation.TransTatsunoSelf_MPP6;

public class CommGSSelf extends CCommDriver implements SerialPortEventListener, 
				CommPortOwnershipListener, Runnable {

	protected byte  	ACK  = 0x06;
	protected byte[] 	ACK0 = new byte[3];
	protected byte[] 	ACK1 = new byte[3];
	protected short 	ADIESEL=12;
	protected int		buffSize = 1024;
	protected short 	DIESEL=11;
	protected int 	dispLevel=0;
	protected byte  	ENQ  = 0x05;
	protected byte[] 	enq_Buf = new byte[4];
	protected byte[] 	ENQb = new byte[4];
	protected byte  	EOT  = 0x04;
	protected byte[]  	EOTb = {EOT, 0};
	
	protected byte  	ESC  = 0x1B;
    protected byte  SEN  = 0x40; // '@'
	protected byte  POL  = 0x3F; // '?'
    protected byte  	ETX  = 0x03;
    protected boolean firstRequest=true;
    //protected boolean waitEndProcess=false;
    protected short 	GASOLINE=20;

	protected HE_WorkingMessage HE_wm;
	protected HF_WorkingMessage HF_wm;
    protected boolean	issueLineErr=true;
	protected byte[] 	lastPumpingData=new byte[buffSize];
	
	protected int		lineCommCheckCnt=0;
	protected int		lineErrCnt=0;
    protected String 	m_basePrice="000000"; // ���� ��û�ܰ�
    
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
    protected String 	m_liter="0000000";    // ���� ��û��
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
	protected String 	m_price="00000000";   // ���� ��û�ݾ�
	protected String 	m_realBasePrice="000000"; // �� �����ܰ�
	protected String 	m_realLiter="0000000";    // �� ������
	protected String 	m_realPrice="00000000";   // �� �����ݾ�
	protected String[][] m_sDispPriceArr=new String[2][6];
	protected boolean	m_setEnvDataOK=false;
	protected String[][] m_sMentPriceArr=new String[2][6];
	protected String[][] m_sPriceButtArr=new String[2][6];
	protected int 		m_statusCode=601;
	protected String	m_targetNozzle;
	protected int	minErrCnt=8, maxSkipCnt=100;

	protected byte  mode = SEN;
	protected byte  	NAK  = 0x15;
	protected byte[]  	NAKb = {NAK, 0};
	protected byte		nozID;
	protected short		nozState=0;
	protected String 	nozStr = "";
	protected PB_WorkingMessage PB_wm;
	protected short 	PGASOLINE=21;

	protected byte  	PRESET_LITER=1;
	protected byte  	PRESET_NONE=2;
	protected byte		m_preset=PRESET_NONE;
	
	protected byte  	PRESET_PRICE=0;
	//progressStep : 0=�ʱ�ȭ��, 1=�ʱ�ȭ��, 2=���������۽���, 3=������, 4=�����Ϸ�, 
    //               5=���а���������/�����Ϸ��ڷ� �۽�
    protected int 	progressStep=3;
	protected QM_WoringMessage  QM_wm; 
	protected byte[] 	rcvInitBuf = new byte[6];
	
//    protected byte  PREFIX_ENQ = 0x05;
//    protected byte  STX  = 0x02;
//    protected byte  ETX  = 0x03;
//    protected byte  ACK  = 0x10;
//    protected byte[] ACK0 = new byte[2];
//    protected byte[] ACK1 = new byte[2];
//    protected byte  NAK  = 0x15;
//    protected byte  ENQ  = 0x05;
//    protected byte  EOT  = 0x04;
//    protected byte  ESC  = 0x1B;

    protected int	readBuffInterval   = 60;
    protected int	readStartInterval  = 5;
    protected byte[] 	RxBuf = new byte[buffSize];
    protected S3_WorkingMessage S3_wm;
    protected S4_WorkingMessage S4_wm;
    protected S5_WorkingMessage S5_wm;
    protected S8_WorkingMessage S8_wm;
    protected SE_WorkingMessage SE_wm;
    protected SJ_WorkingMessage SJ_wm;
    protected byte  	STX  = 0x02;
    protected byte[] 	t02eb_Buf; // End block

    protected DataStruct t02eb_ds = new DataStruct(); // End block
    protected byte[] 	t02sb_Buf; // Start block
    //��û
    protected DataStruct t02sb_ds = new DataStruct(); // Start block
    
    protected byte[] 	tC0_Buf;
    protected DataStruct tC0_ds = new DataStruct(); // ������1
    protected byte[] 	tC1_Buf;
    protected DataStruct tC1_ds = new DataStruct(); // ������2
    protected byte[] 	tD0_Buf;
    protected DataStruct tD0_ds = new DataStruct(); // ����̽�����
    protected byte[] 	tE0_Buf;
    protected DataStruct tE0_ds = new DataStruct(); // ȯ�漳�� ���� 

	protected DataStruct ti0_ds = new DataStruct(); // ����̽� �Է�����
    protected DataStruct tm0_ds = new DataStruct(); // ����̽� ���۸��
    protected TR_WorkingMessage TR_wm;
    TransTatsunoSelf_MPP6 trans = new TransTatsunoSelf_MPP6();
    protected byte[] 	tS0_Buf;
    //����
    protected DataStruct ts0_ds = new DataStruct(); // ��������
    protected DataStruct tS0_ds = new DataStruct(); // �������� ��û
	
	protected byte[] 	TxBuf = new byte[buffSize];
    protected BytesQue2 TxQue = new BytesQue2(30);
    protected int	writeStartInterval = 5;
		
	public CommGSSelf (int nozNum, String romVerStr, int nMPPCount) 
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
		enq_Buf[2] = SEN;
		enq_Buf[3] = ENQ;
			
		try {
			
			//--- Start block ---//
			t02sb_ds.addByte  ("STX", STX);
			t02sb_ds.addByte  ("SA", nozID);
			t02sb_ds.addByte  ("UA", SEN);
			t02sb_ds.addString("command", "02", 2);
			t02sb_ds.addByte  ("sequence", (byte) '1'); //sequence
			t02sb_ds.addString("block", "1", 1);
			t02sb_ds.addString("fileNo", "01", 2);
			t02sb_ds.addString("fileName", "OPT01000", 8);
			t02sb_ds.addString("fileExt", "000", 3);
			t02sb_ds.addString("outputDevice", "00", 2);
			t02sb_ds.addString("reserved", "", 20);
			t02sb_ds.addByte  ("ETX", ETX);
			t02sb_ds.addByte  ("BCC", (byte) ' ');
						
			//--- End block ---//
			t02eb_ds.addByte  ("STX", STX);
			t02eb_ds.addByte  ("SA", nozID);
			t02eb_ds.addByte  ("UA", SEN);
			t02eb_ds.addString("command", "02", 2);
			t02eb_ds.addByte  ("sequence", (byte) '1'); //sequence
			t02eb_ds.addString("block", "3", 1);
			t02eb_ds.addString("fileNo", "01", 2);
			t02eb_ds.addString("blockCnt", "001", 3);
			t02eb_ds.addString("reserved", "", 30);
			t02eb_ds.addByte  ("ETX", ETX);
			t02eb_ds.addByte  ("BCC", (byte) ' ');
			
			//--- ����̽� �Է����� ---//
			tD0_ds.addByte  ("STX", STX);
			tD0_ds.addByte  ("SA", nozID);
			tD0_ds.addByte  ("UA", SEN);
			tD0_ds.addString("command", "D0", 2);
			tD0_ds.addByte  ("sequence", (byte) '1'); //sequence
			tD0_ds.addByte  ("devCtrl1", (byte) 0x80);
			tD0_ds.addByte  ("devCtrl2", (byte) 0x00);
			tD0_ds.addByte  ("ETX", ETX);
			tD0_ds.addByte  ("BCC", (byte) ' ');
			tD0_Buf = tD0_ds.getByteStream();
			
			//--- ������1 ---//
			tC0_ds.addByte  ("STX", STX);
			tC0_ds.addByte  ("SA", nozID);
			tC0_ds.addByte  ("UA", SEN);
			tC0_ds.addString("command", "C1", 2);
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
			tC1_ds.addString("command", "C0", 2);
			tC1_ds.addByte  ("sequence", (byte) '1'); //sequence
			tC1_ds.addByte  ("pageNo", (byte) '2');
			tC1_ds.addByte  ("devCtrl1", (byte) 0xFF);
			tC1_ds.addByte  ("devCtrl2", (byte) 0xFF);
			tC1_ds.addString("voiceDataCnt", "00", 2);
			tC1_ds.addString("voiceData", "00", 43);
			tC1_ds.addByte  ("ETX", ETX);
			tC1_ds.addByte  ("BCC", (byte) ' ');
			tC1_Buf = tC1_ds.getByteStream();

			//--- �������� ��û ---//
			tS0_ds.addByte  ("STX", STX);
			tS0_ds.addByte  ("SA", nozID);
			tS0_ds.addByte  ("UA", SEN);
			tS0_ds.addString("command", "S0", 2);
			tS0_ds.addByte  ("sequence", (byte) '1'); //sequence
			tS0_ds.addByte  ("ETX", ETX);
			tS0_ds.addByte  ("BCC", (byte) ' ');
			tS0_Buf = tS0_ds.getByteStream();
			
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
	
	protected boolean compareNozID(byte[] buf) throws Exception {

		if (RxBuf[1]==nozID)		
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
	
	//----- ������1 -----//
	protected void insertDeviceCtrl_C0 (byte sequence, String deviceCode, String datas) throws Exception {

		byte[] sndBuf;
		
		try {
			tC0_ds.editByte  ("sequence", sequence);
			tC0_ds.editString("deviceCode", deviceCode, 2);
			tC0_ds.editString("datas", datas, datas.length());
			
			sndBuf = tC0_ds.getByteStream();
			TxQue.enQueue(sndBuf);

			//LogUtility.getPumpALogger().debug(">>>>Insert command 40. itemCount=" + TxQue.getItemCount() + " (ODT="+nozNo+")");
			//Log.datas(sndBuf, sndBuf.length, 20);
		
		} catch (Exception e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	}

	//----- ������2 (ȭ��, ����̽� �Է�, ���� ��������)-----//
	protected void insertDeviceCtrl_C1 (byte sequence, String pageNo, byte devCtrl1, 
			byte devCtrl2, String voiceDataCnt, String voiceData) throws Exception {

		byte[] sndBuf;
		
		try {
			tC1_ds.editByte  ("sequence", sequence);
			tC1_ds.editString("pageNo", pageNo, 2);
			tC1_ds.editByte  ("devCtrl1", devCtrl1);
			tC1_ds.editByte  ("devCtrl2", devCtrl2);
			tC1_ds.editString("voiceDataCnt", voiceDataCnt, 2);
			tC1_ds.editString("voiceData", voiceData, 43);

			sndBuf = tC1_ds.getByteStream();
			TxQue.enQueue(sndBuf);

			//LogUtility.getPumpALogger().debug(">>>>Insert command 40. itemCount=" + TxQue.getItemCount() + " (ODT="+nozNo+")");
			//Log.datas(sndBuf, sndBuf.length, 20);
		
		} catch (Exception e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
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

			//LogUtility.getPumpALogger().debug(">>>>Insert command 40. itemCount=" + TxQue.getItemCount() + " (ODT="+nozNo+")");
			//Log.datas(sndBuf, sndBuf.length, 20);
		
		} catch (Exception e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	}
	
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
	
	protected boolean isSkipWorkingMessage (WorkingMessage wm) throws Exception {
		
		boolean skip=false;

		if (wm.getCommand().equals("P5_1")) { // ���� ȯ�漳��
			
			P5_1_WorkingMessage P5_1_wm = (P5_1_WorkingMessage) wm;
			Vector<P5_NozzleInfo> nozInfoVec = P5_1_wm.getNozzleInfo();
			int nozCnt = nozInfoVec.size();

			LogUtility.getPumpALogger().debug("�پ��뼿��ODT P5 ��������. ODT_No=" + P5_1_wm.getOdtID()+
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
			
			m_nODTState = 7;

			// ���εǾ����ϴ�. OO�� �����̰� 000�Դϴ�. �����⸦ ��� ������ �ּ���.
			//insertDevCtrlOrder_42((byte)'0', "02", "01", "00000010"+"0000000000000000");
			insertDeviceCtrl_C1 ((byte) '1', "07", (byte) 0x80, (byte) 0x01, "02", "000102");
	
			if (DIESEL == m_oilKind) {
				//insertDevCtrlOrder_42((byte)'0', "11", "01", "1003133344500");
				insertDeviceCtrl_C1 ((byte) '1', "07", (byte) 0x80, (byte) 0x01, "02", "000102");
			}
			else if (ADIESEL == m_oilKind) {
				//insertDevCtrlOrder_42((byte)'0', "11", "01", "1003147344500");
				insertDeviceCtrl_C1 ((byte) '1', "07", (byte) 0x80, (byte) 0x01, "02", "000102");
			}
			else if (GASOLINE == m_oilKind) {
				//insertDevCtrlOrder_42((byte)'0', "11", "01", "1003132344500");
				insertDeviceCtrl_C1 ((byte) '1', "07", (byte) 0x80, (byte) 0x01, "02", "000102");
			}
			else if (PGASOLINE == m_oilKind) {
				//insertDevCtrlOrder_42((byte)'0', "11", "01", "1003146344500");
				insertDeviceCtrl_C1 ((byte) '1', "07", (byte) 0x80, (byte) 0x01, "02", "000102");
			}

			LogUtility.getPumpALogger().debug("������/������������(PB) ����, �����㰡(PB) �߻� : ODT_No=" + PB_wm.getNozzleNo()+ 
				" nozzle=" + PB_wm.getConnectNozzleNo() + " mode=" + PB_wm.getCommandSet()+ 
				" liter=" + m_liter + " bPrice=" + m_basePrice + " price=" + m_price);

			makeStatusInfo (235); // �������
			
			skip = true;
		}
		else if (wm.getCommand().equals("QM")) { // ī�� ��������
			QM_wm = (QM_WoringMessage) wm;
			
			LogUtility.getPumpALogger().debug(new StringBuffer("\n�پ��뼿��ODT �������� >>> ��������(QM) : " ).append( QM_wm.getCommand() )
										 .append("\n ODT_No=" ).append( QM_wm.getNozzleNo() )
										 .append("\n nozzle=" ).append( QM_wm.getConnectNozzleNo() )
										 .append("\n mode  =[" ).append( QM_wm.getMode() ).append( "]\n").toString());
			
			short mode = (short) Change.toValue(QM_wm.getMode());
			
			if (mode == 1) { // ����OK
				
				m_nODTState = 7;
				
				PB_wm = new PB_WorkingMessage();
				//PB_wm.setDirection(IPumpConstant.DIRECTION_FROM_ODT);
				PB_wm.setPassThrough(false);
				PB_wm.setNozzleNo(wm.getNozzleNo()); // ODT No
				PB_wm.setConnectNozzleNo(wm.getConnectNozzleNo()); // Nozzle No
				PB_wm.setTargetNozzleNo(wm.getConnectNozzleNo());  // Nozzle No
				PB_wm.setCommandSet(Change.toString(m_preset));
				PB_wm.setLiter(m_liter);
				PB_wm.setBasePrice(m_basePrice);
				PB_wm.setPrice(m_price);
				
				insertRecvQueue(PB_wm);
				
				//--- �����ݾ� ����
				m_nSetPrice = Change.toValue(m_price);
				m_dispPriceLiter = Change.toString("W%d",m_nSetPrice);
				
				LogUtility.getPumpALogger().debug("����OK(QM), �����㰡(PB) �߻� : ODT_No=" + QM_wm.getNozzleNo()+ 
						" nozzle=" + QM_wm.getConnectNozzleNo() + " liter=" + m_liter + 
						" bPrice=" + m_basePrice + " price=" + m_price);
				
				makeStatusInfo (235); // �������
				
				//insertDevCtrlOrder_42((byte)'0', "02", "01", "00000010"+"0000000000000000");
				insertDeviceCtrl_C1 ((byte) '1', "07", (byte) 0x80, (byte) 0x01, "02", "000102");
				
				// ���εǾ����ϴ�. OO�� �����̰� 000�Դϴ�. �����⸦ ��� ������ �ּ���.
				if (DIESEL == m_oilKind) {
					//insertDevCtrlOrder_42((byte)'0', "11", "01", "1003133344500");
					insertDeviceCtrl_C1 ((byte) '1', "07", (byte) 0x80, (byte) 0x01, "02", "000102");
				}
				else if (ADIESEL == m_oilKind) {
					//insertDevCtrlOrder_42((byte)'0', "11", "01", "1003147344500");
					insertDeviceCtrl_C1 ((byte) '1', "07", (byte) 0x80, (byte) 0x01, "02", "000102");
				}
				else if (GASOLINE == m_oilKind) {
					//insertDevCtrlOrder_42((byte)'0', "11", "01", "1003132344500");
					insertDeviceCtrl_C1 ((byte) '1', "07", (byte) 0x80, (byte) 0x01, "02", "000102");
				}
				else if (PGASOLINE == m_oilKind) {
					//insertDevCtrlOrder_42((byte)'0', "11", "01", "1003146344500");
					insertDeviceCtrl_C1 ((byte) '1', "07", (byte) 0x80, (byte) 0x01, "02", "000102");
				}

				//insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x80); //�Է¿䱸
			}
			else if (mode==2 || // �������
					 mode==3) { // ������
				
				if (m_nODTState==6) {
								
					//������ ������ �����ϴ�. �繫�ǿ��� Ȯ���Ͽ� �ּ���.
					//insertDevCtrlOrder_42((byte)'0', "02", "01", "00000000"+"0000000000000000");
					insertDeviceCtrl_C1 ((byte) '1', "07", (byte) 0x80, (byte) 0x01, "02", "000102");
					
					// �ʱ����(��������)�� ��������
					//insertDevCtrlOrder_42((byte)'0', "00", "01", "015"); // Ÿ�̸� ����
					insertDeviceCtrl_C0 ((byte) '1', "12", "10005");

					LogUtility.getPumpALogger().debug("�������(QM) : ODT_No=" + QM_wm.getNozzleNo()+ 
							" nozzle=" + QM_wm.getConnectNozzleNo() + " liter=" + m_liter + 
							" bPrice=" + m_basePrice + " price=" + m_price + " m_nODTState="+m_nODTState);

					//waitEndProcess=true;
					m_nODTState=8;
				}
			}

			skip = true;
		}
		else if (wm.getCommand().equals("S3")) { // ������
			S3_wm = (S3_WorkingMessage) wm;
			
			if (m_bFirstPumping==true) {
				
				m_bFirstPumping = false;
				//insertInitCmd();
				
				//�����Ͻŷ����� ���� ������ ���׹����� ������ õ������ ������ �˴ϴ�.
				insertDeviceCtrl_C1 ((byte) '1', "07", (byte) 0x80, (byte) 0x01, "02", "000102");
				
			}

			String szLiter1 = S3_wm.getLiter().substring(0, 4);
			String szLiter2 = S3_wm.getLiter().substring(4, 7);
			
			//insertDisplayData_23 ("01", "01", "Liter:"+szLiter1+"."+szLiter2+ 
								//"      Price:"+S3_wm.getPrice());
			
			if (Change.toValue(S3_wm.getPrice()) >= m_nSetPrice) { 

				if (m_bFirstPumpingEnd==true) {
					m_bFirstPumpingEnd=false;
					
					// ������ �Ϸ� �Ǿ����ϴ�.
					insertDeviceCtrl_C1 ((byte) '1', "07", (byte) 0x80, (byte) 0x01, "02", "000102");

					LogUtility.getPumpALogger().debug("������ ��ŭ ������ �Ǿ����ϴ�. ODT_No=" + S3_wm.getNozzleNo()+ 
							" nozzle=" + S3_wm.getConnectNozzleNo() + 
							" ������=" + m_nSetPrice+ " ������="+ Change.toValue(S3_wm.getPrice()));
					
				}
			}

			skip = true;
		}
		else if (wm.getCommand().equals("S4")) { // �����Ϸ�
			S4_wm = (S4_WorkingMessage) wm;

			m_bFromODT = false;
			m_bFirstPumping = true;
			m_bFirstPumpingEnd = true;
			
			LogUtility.getPumpALogger().debug(new StringBuffer("\n�پ��뼿��ODT �������� >>> �����Ϸ�(S4) : ").append( S4_wm.getCommand() )
										.append("\n ODT_No=" ).append( S4_wm.getNozzleNo() )
										.append("\n nozzle=" ).append( S4_wm.getConnectNozzleNo() )
										.append("\n liter =" ).append( S4_wm.getLiter() )
										.append("\n bPrice=" ).append( S4_wm.getBasePrice() )
										.append("\n price =" ).append( S4_wm.getPrice()).toString());
						
			if (m_bCreditMode==true) { // �ſ�ī�� ���ΰ�
				// ���ᱸ�� �ݾ��ֽð� �������� �޾��ּ���.
				insertDeviceCtrl_C1 ((byte) '1', "07", (byte) 0x80, (byte) 0x01, "02", "000102");
			}
			else { // ���� ��
				// ���ᱸ�� �ݾ��ּ���.�����մϴ�.�ȳ���������.
				insertDeviceCtrl_C1 ((byte) '1', "07", (byte) 0x80, (byte) 0x01, "02", "000102");
			}
			
			insertDeviceCtrl_C0 ((byte) '1', "10", "01"); // ȭ��
		
			// ������ ����� �ʱ����(��������)�� ��������
			//insertDevCtrlOrder_42((byte)'0', "00", "01", "010"); // Ÿ�̸� ����
			insertDeviceCtrl_C0 ((byte) '1', "12", "10010"); // Ÿ�̸� ����
			
			m_nODTState=8;
			
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

			LogUtility.getPumpALogger().debug(new StringBuffer("\n�پ��뼿��ODT �������� >>> ���������(QL) : " ).append( QL_wm.getCommand() )
										 .append("\n ODT_No=" ).append( QL_wm.getNozzleNo() )
										 .append("\n nozzle=" ).append( QL_wm.getConnectNozzleNo() )
										 .append("\n mode  =" ).append( QL_wm.getMode() )
										 .append("\n length=" ).append( QL_wm.getContent().length() ).append("\n").toString());
			
			String prtData = QL_wm.getContent();
			byte[] byCut = {0x1D, 0x56}; // ������ ����
			String sLen = Change.toString("%04d", prtData.length());

			insertDeviceCtrl_C0((byte) '1', "11", sLen + prtData);
			
//			byte[] byCut = {0x1B, 0x69}; // ������ ����
//							
//			//printReceipt (prtData.getBytes(), 0); // ������ ������
//			//printReceipt(PumpMessageFormat.getBarcodeFormat("123456"), 0);
//
//			//printReceipt (prtData.getBytes(), 0); // ������ ������
//			
//			int len = prtData.length();
//			if (len > 1610) {
//				printReceipt (prtData.substring(0,1610).getBytes(), 0); // ������ ������ 1
//				printReceipt (prtData.substring(1610,len).getBytes(), 0); // ������ ������ 2
//			} else
//				printReceipt (prtData.getBytes(), 0); // ������ ������
//			
//			if (QL_wm.getBarCode().length() > 0)
//				printReceipt (PumpMessageFormat.getBarcodeFormat(QL_wm.getBarCode()), 0); // Barcode byteStrem
//			
//			//printReceipt (QL_wm.getDate().getBytes(), 0); // �μ�ð�
//			printReceipt (byCut, 0);
			

			
			//LogUtility.getPumpALogger().debug("##### print out("+len+" bytes) : \n"+ QL_wm.getContent());
			
			if (QL_wm.getMode().equals("1")) { // ������ ������
				
				//----- TR ���� ���� -----//
				TR_wm = new TR_WorkingMessage();
				TR_wm.setNozzleNo(QL_wm.getNozzleNo());
				TR_wm.setConnectNozzleNo(QL_wm.getConnectNozzleNo());
				TR_wm.setLiter(m_realLiter);
				TR_wm.setBasePrice(m_realBasePrice);
				TR_wm.setPrice(m_realPrice);
				
				insertRecvQueue(TR_wm);
				
				LogUtility.getPumpALogger().debug(new StringBuffer("\n�پ��뼿��ODT �۽����� >>> TR ���� ���� : " )
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
	
	/*
	protected void insertCommand_40 (byte sequence, String orderNo, byte orderCond, 
			byte inputCond) { // inputCond->unsigned byte ó���ʿ�)
		
		Formatter form = new Formatter();
		byte[] sndBuf=null;

		System.out.printf ("\n111111 seq=%c ord=%s ordCon=%c inCon=%c\n", sequence, 
				orderNo, orderCond, (byte) inputCond);
		
		inputCond = (byte) (inputCond==0x00 ? ' ' : inputCond);
		form.format("%c%c%c%c%c%c%s    %c%c%c  00 0000%c%c", STX, nozID, 0x43, '4', '0', sequence, 
				orderNo, orderCond, 0x4B, (byte)inputCond, ETX, ' ');

		sndBuf = form.toString().getBytes();
		
		System.out.printf ("\n=======>>>>Insert command 40 :\n");
		Show.datas(sndBuf, sndBuf.length, 20);
		
		TxQue.enQueue(sndBuf);
	}
	*/
	
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
		
	//------ �Է¿��� ����(80) ó�� ------//
	protected void processOPT () throws SerialConnectException, Exception {
		
		if (m_setEnvDataOK == false)
			return;
				
		switch (RxBuf[10]) { //������ Ÿ��	
		case '0': //�Է�����
			break;
			
		case '1': //Ű �Է�
			byte[] byt = new byte[2];
			byt[0]=RxBuf[11];
			byt[1]=RxBuf[12];
			String function=new String (byt);

			LogUtility.getPumpALogger().debug("\n<<<<Input function. keyNo="+function+" m_nODTState="+m_nODTState+"\n");

			if ((function.equals("00")==true || function.equals("01")==true || function.equals("02")==true || 
					function.equals("0D")==true) && 
					(m_nODTState == 0 || m_nODTState == 1 || m_nODTState == 2)) {

				if (function.equals("0D")==true) { // ���ó��

					m_nODTState = 1;
					m_bCreditMode = false;
					m_bBonusMode = false;
					m_bFirstPumping=true;
					m_bFirstPumpingEnd=true;
					m_oilKind= 0;
					m_liter= "0";
					//m_basePrice= "0";
					m_price= "0";
					m_creditNo = "";
					m_bonusNo = "";

					// ���Ͻô� ������ ������ �ּ���
					//insertDevCtrlOrder_42((byte)'0', "02", "01", "10000000"+"0000000000000000");
					insertDeviceCtrl_C1 ((byte) '1', "01", (byte) 0x80, (byte) 0x01, "02", "000102");

					LogUtility.getPumpALogger().debug("###### 1 : ����Ͽ����ϴ�. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
					
					makeStatusInfo (231); // �������� ���(�����)
				}
				else {
					m_nODTState = 3;
					m_bCreditMode = false;
					m_bBonusMode = false;
					m_bFirstPumping=true;
					m_bFirstPumpingEnd=true;
					
					m_creditNo = "";
					m_bonusNo = "";
					
					if (function.equals("00"))
						m_targetNozzle = m_nozNoVec.get(0);
					else if (function.equals("01"))
						m_targetNozzle = m_nozNoVec.get(1);
					else if (function.equals("02"))
						m_targetNozzle = m_nozNoVec.get(2);

					m_oilKind = getOilKind(m_targetNozzle);
					m_basePrice = m_basePriceTbl.get(m_targetNozzle);

					// ���� ���÷���
					String oilName = m_oilNameTbl.get(m_targetNozzle);
					Formatter form = new Formatter();
					form.format("%-14s", oilName);
					String sndStr = form.toString();
					//insertDisplayData_23 ("01", "01", sndStr);
					
					//LogUtility.getPumpALogger().debug("Oil name : "+ oilName);

					//insertDevCtrlOrder_42((byte)'0', "02", "01", "01000000"+"0000000000000000");
					insertDeviceCtrl_C1 ((byte) '1', "01", (byte) 0x80, (byte) 0x01, "02", "000102");

					if (m_oilKind==DIESEL) { // ����
						insertDeviceCtrl_C1 ((byte) '1', "01", (byte) 0x80, (byte) 0x01, "02", "000102");
					}
					else if (m_oilKind==ADIESEL) { // ��ް��� �����Ӱ���
						insertDeviceCtrl_C1 ((byte) '1', "01", (byte) 0x80, (byte) 0x01, "02", "000102");
					}
					else if (m_oilKind==GASOLINE) { // ���� �ֹ���
						insertDeviceCtrl_C1 ((byte) '1', "01", (byte) 0x80, (byte) 0x01, "02", "000102");
					}
					else if (m_oilKind==PGASOLINE) { // ����ֹ��� ű��������
						insertDeviceCtrl_C1 ((byte) '1', "01", (byte) 0x80, (byte) 0x01, "02", "000102");
					}

					//insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x82); //�Է¿䱸
					insertDeviceCtrl_C1 ((byte) '1', "01", (byte) 0x80, (byte) 0x01, "02", "000102");
					
					//insertDevCtrlOrder_42((byte)'0', "00", "01", "120"); // $$$$$$$$					
					LogUtility.getPumpALogger().debug("###### 2 : ���������Ͽ����ϴ�. m_nODTState=" + m_nODTState + 
							"Oil name="+ oilName + " (ODT="+nozNo+")");

					makeStatusInfo (232); // �ݾ׼��� ���
				}
			}
			else if (3 == m_nODTState && 
					(function.equals("03") || function.equals("04") || function.equals("05") || 
					 function.equals("06") || function.equals("07") || function.equals("13") || 
					 function.equals("14") || function.equals("15") || function.equals("16") || 
					 function.equals("17") || function.equals("0A") || function.equals("0B") || 
					 function.equals("0D")))
			{
				if (function.equals("0D")) { // ���
					m_nODTState = 1;
					// ������ �����Ǽ� �����ϴ�. ���Ͻô� ������ ������ �ּ���
					//insertDevCtrlOrder_42((byte)'0', "02", "01", "10000000"+"0000000000000000");
					insertDeviceCtrl_C1 ((byte) '1', "01", (byte) 0x80, (byte) 0x01, "01", "000102");

					LogUtility.getPumpALogger().debug("###### 3 : ����Ͽ����ϴ�. . m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");

					makeStatusInfo (231); // �������� ���(�����)
				}
				else {
					
					m_nODTState = 4;
					String szVoice="";
					String szDisplay="";
					m_liter="0000000";

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
						case 'B': 
							szVoice=m_sMentPriceArr[1][5]; szDisplay=m_sDispPriceArr[1][5]; 
							m_price=m_sPriceButtArr[1][5]; m_preset = PRESET_PRICE;
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
					// �ſ�ī�带 �־��� ���ּ���.
					//insertDevCtrlOrder_42((byte)'0', "02", "01", "00001000"+"0000000000000000");
					insertDeviceCtrl_C1 ((byte) '1', "05", (byte) 0x80, (byte) 0x01, "02", "000102");

					LogUtility.getPumpALogger().debug("###### 4 : �ݾ׼����Ͽ����ϴ�. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
					
					makeStatusInfo (233); // �����Է� ���
				}
			}
			else if (4 == m_nODTState && function.equals("0D")) {
				
				if (function.equals("0D")) { // ���
					m_nODTState = 1;

					// ������ �����Ǽ� �����ϴ�. ���Ͻô� ������ ������ �ּ���
					//insertDevCtrlOrder_42((byte)'0', "02", "01", "10000000"+"0000000000000000");
					insertDeviceCtrl_C1 ((byte) '1', "01", (byte) 0x80, (byte) 0x01, "02", "000102");

					LogUtility.getPumpALogger().debug("###### 5 : ����Ͽ����ϴ�. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");

					makeStatusInfo (231); // �������� ���(�����)
				}
			}
			else if (5 == m_nODTState && 
					(function.equals("0C") || function.equals("0D"))) {
				
				if (function.equals("0D")) { // ���
					m_nODTState = 1;

					// ������ �����Ǽ� �����ϴ�. ���Ͻô� ������ ������ �ּ���
					//insertDevCtrlOrder_42((byte)'0', "02", "01", "10000000"+"0000000000000000");
					insertDeviceCtrl_C1 ((byte) '1', "01", (byte) 0x80, (byte) 0x01, "02", "000102");
					
					LogUtility.getPumpALogger().debug("###### 6 : ����Ͽ����ϴ�. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");

					makeStatusInfo (231); // �������� ���(�����)
				}
				else { // ���ʽ�ī�� ����
					m_nODTState = 6;
					
					insertDeviceCtrl_C1 ((byte) '1', "04", (byte) 0x80, (byte) 0x01, "02", "000102");

					//----- ī����ο�û -----//
					boolean bApprove = requestCardApprove (); 
					
					LogUtility.getPumpALogger().debug("###### 7 : ���� ��û�Ͽ����ϴ�. : state="+m_nODTState+
							" approve="+bApprove+" m_nODTState="+m_nODTState + " (ODT="+nozNo+")");
				}
			}
			else {

				switch(m_nODTState) {
					case 1 :
						//insertDevCtrlOrder_42((byte)'0', "02", "01", "10000000"+"0000000000000000");
						insertDeviceCtrl_C1 ((byte) '1', "01", (byte) 0x80, (byte) 0x01, "02", "000102");
						break;
					case 3 :
						//insertDevCtrlOrder_42((byte)'0', "02", "01", "01000000"+"0000000000000000");
						insertDeviceCtrl_C1 ((byte) '1', "02", (byte) 0x80, (byte) 0x01, "02", "000102");
						break;
					case 4 :
						//insertDevCtrlOrder_42((byte)'0', "02", "01", "00001000"+"0000000000000000");
						insertDeviceCtrl_C1 ((byte) '1', "05", (byte) 0x80, (byte) 0x01, "02", "000102");
						break;
					case 5 :
						//insertDevCtrlOrder_42((byte)'0', "02", "01", "00000100"+"0000000000000000");
						insertDeviceCtrl_C1 ((byte) '1', "06", (byte) 0x80, (byte) 0x01, "02", "000102");
						break;
				}
							
				LogUtility.getPumpALogger().debug("###### 0 : ����ó�� ��ƾ(1)-----m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
			}
			
			break;
				
		case '2': // ī�� �Է�
			if (4 == m_nODTState)
			{
				if (RxBuf[91]=='0' && RxBuf[92]=='0') {
					
					m_nODTState = 5;
					m_bCreditMode = true;
				
					byte[] byLen = {RxBuf[93], RxBuf[94]};
					String lenStr = new String(byLen);
					int	len = Change.toValue (lenStr);
					
					System.arraycopy(RxBuf, 95, m_byCreditNo, 0, len);
					int k;
					for (k=0; k<m_byCreditNo.length; k++)
						if (m_byCreditNo[k]==0x00)
							break;
					
					String szCardNo = new String(m_byCreditNo);
					m_creditNo = szCardNo.substring(0,k);
					
					//���ʽ�ī�带 �־��� ���ּ���.
					//insertDevCtrlOrder_42((byte)'0', "02", "01", "00000100"+"0000000000000000");
					insertDeviceCtrl_C1 ((byte) '1', "06", (byte) 0x80, (byte) 0x01, "02", "000102");
					
					LogUtility.getPumpALogger().debug("###### 7 : �ſ�ī�� �Է��Ͽ����ϴ�. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
					LogUtility.getPumpALogger().debug("m_byCreditNo="+m_creditNo+" len="+m_creditNo.length());

					makeStatusInfo (234); // ���ʽ�ī���Է� ���
				}
				else {
					/*
					m_nCreditErrCnt++;
					if (3 == m_nCreditErrCnt) {
						
						m_nODTState = 1;		
						//m_lubr = none;
						insertDevCtrlOrder_42((byte)'0', "00", "02", "000"); // Ÿ�̸� ���� 
						insertDevCtrlOrder_42((byte)'0', "11", "02", "000"); // ���� ����
						insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x80);
						insertDevCtrlOrder_42((byte)'0', "02", "01", "10000000"+"0000000000000000");
						
						LogUtility.getPumpALogger().debug("###### 8 : �ſ�ī�� �Է��Ͽ����ϴ�->�̻�ó��->�ʱ�� ����. m_nODTState=" + m_nODTState);
					}
					else {

						insertDevCtrlOrder_42((byte)'0', "00", "02", "000"); // Ÿ�̸� ���� 
						insertDevCtrlOrder_42((byte)'0', "11", "02", "000"); // ���� ����
						insertDevCtrlOrder_42((byte)'0', "11", "01", "1004900");
						insertDevCtrlOrder_42((byte)'0', "11", "01", "0054900");
						insertDisplayData_23_new("01", "01", (byte)'3', (byte)'3', false, "Card Error!");
						
						LogUtility.getPumpALogger().debug("###### 9 : �ſ�ī�� �Է��Ͽ����ϴ�->�̻�ó��. m_nODTState=" + m_nODTState);
					}*/
					
					m_nODTState = 1;

					// ������ �����Ǽ� �����ϴ�. ���Ͻô� ������ ������ �ּ���
					//insertDevCtrlOrder_42((byte)'0', "02", "01", "10000000"+"0000000000000000");
					insertDeviceCtrl_C1 ((byte) '1', "01", (byte) 0x80, (byte) 0x01, "02", "000102");

					LogUtility.getPumpALogger().debug("###### 9 : �ſ�ī�� �Է��Ͽ����ϴ�. ī���̻����� ���ο�û ���. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");

				}
			}
			else if (5 == m_nODTState) {
				
				if (RxBuf[91]=='0' && RxBuf[92]=='0') { // ī�帮�� ����
					
					m_nODTState = 6;
					m_bBonusMode = true;

					byte[] byLen = {RxBuf[93], RxBuf[94]};
					String lenStr = new String(byLen);
					int	len = Change.toValue (lenStr);
					System.arraycopy(RxBuf, 95, m_byBonusNo, 0, len);
					m_bonusNo = new String(m_byBonusNo);
					
					insertDeviceCtrl_C1 ((byte) '1', "  ", (byte) 0x80, (byte) 0x01, "02", "000102");

					LogUtility.getPumpALogger().debug("###### 10 : ���ʽ�ī�� �Է��Ͽ����ϴ�. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
					LogUtility.getPumpALogger().debug("���ʽ�ī�� ��ȣ="+m_bonusNo+" len="+m_bonusNo.length());

					//----- ī����ο�û -----//
					boolean bApprove = requestCardApprove ();
					
					LogUtility.getPumpALogger().debug("###### 11 : ���� ��û�Ͽ����ϴ�. : state="+m_nODTState+ " approve="+bApprove+
							" m_nODTState="+m_nODTState + " (ODT="+nozNo+")");
	
				}
				else {
					/*
					m_nCreditErrCnt++;
					
					if (3 == m_nCreditErrCnt) {
						m_nODTState = 1;	
						//m_lubr = none;

						insertDevCtrlOrder_42((byte)'0', "00", "02", "000"); // Ÿ�̸� ���� 
						insertDevCtrlOrder_42((byte)'0', "11", "02", "000"); // ���� ����
						insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x80);
						insertDevCtrlOrder_42((byte)'0', "02", "01", "10000000"+"0000000000000000");
						
						LogUtility.getPumpALogger().debug("###### 12 : ����ġ �Ͽ����ϴ�. m_nODTState=" + m_nODTState);
					}
					else {

						insertDevCtrlOrder_42((byte)'0', "00", "02", "000"); // Ÿ�̸� ���� 
						insertDevCtrlOrder_42((byte)'0', "11", "02", "000"); // ���� ����
						insertDevCtrlOrder_42((byte)'0', "11", "01", "1004900");
						insertDevCtrlOrder_42((byte)'0', "11", "01", "0054900");
						insertDisplayData_23_new("01", "01", (byte)'3', (byte)'3', true, "Card Error!");		

						LogUtility.getPumpALogger().debug("###### 13 : ����ġ �Ͽ����ϴ�. m_nODTState=" + m_nODTState);
					}
					*/
					m_nODTState = 1;

					// ������ �����Ǽ� �����ϴ�. ���Ͻô� ������ ������ �ּ���
					//insertDevCtrlOrder_42((byte)'0', "02", "01", "10000000"+"0000000000000000");
					insertDeviceCtrl_C1 ((byte) '1', "  ", (byte) 0x80, (byte) 0x01, "02", "000102");
					
					LogUtility.getPumpALogger().debug("###### 12 : ���ʽ�ī�� �Է��Ͽ����ϴ�->ī���̻����� ��û���. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");

					makeStatusInfo (231); // �������� ���(�����)
				}
			}
			else {
				
				switch(m_nODTState) {
					case 1 :
						//insertDevCtrlOrder_42((byte)'0', "02", "01", "10000000"+"0000000000000000");
						insertDeviceCtrl_C1 ((byte) '1', "01", (byte) 0x80, (byte) 0x01, "02", "000102");
						//makeStatusInfo (231); // �������� ���(�����)
						break;
					case 3 :
						//insertDevCtrlOrder_42((byte)'0', "02", "01", "01000000"+"0000000000000000");
						insertDeviceCtrl_C1 ((byte) '1', "01", (byte) 0x80, (byte) 0x01, "02", "000102");
						//makeStatusInfo (232); // �ݾ׼��� ���
						break;
					case 4 :
						//insertDevCtrlOrder_42((byte)'0', "02", "01", "00001000"+"0000000000000000");
						insertDeviceCtrl_C1 ((byte) '1', "01", (byte) 0x80, (byte) 0x01, "02", "000102");
						//makeStatusInfo (233); // �����Է� ���
						break;
					case 5 :
						//insertDevCtrlOrder_42((byte)'0', "02", "01", "00000100"+"0000000000000000");
						insertDeviceCtrl_C1 ((byte) '1', "01", (byte) 0x80, (byte) 0x01, "02", "000102");
						//makeStatusInfo (234); // ���ʽ�ī�� �Է´��
						break;
				}
								
				LogUtility.getPumpALogger().debug("###### 0 : ����ó�� ��ƾ(2)-----m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
			}
			
			break;
		}
	}
	
	protected void processRecvSTX () throws SerialConnectException, Exception {
	
		//System.out.printf ("==========[3]=%c [4]=%c\n", RxBuf[3], RxBuf[4]);
		
		//############# State check ##############//
		if (RxBuf[3]=='6' && RxBuf[4]=='0') { // ����/������ ��������

			//recvFileStatus();
		}
		else if (RxBuf[3]=='6' && RxBuf[4]=='1') { // OPT ���� ����

			makeStatusInfo (650); // ������������(����)
			
			switch (RxBuf[6]) // ���۸�� ('1'=Initial mode, '2'=POS mode)
			{
			case '1': // Initial mode (ȯ�漳��)

				//insertInitialFileDemand();
				//insertOPTFile();
				
				LogUtility.getPumpALogger().debug("\n######### Completed Tatsuno Self ODT Initializtion : ODT=" +
												nozStr + "(MPP" + m_numMPPs + ") #########\n");

				break;
			case '2': // POS mode (���� ����)

				m_nODTState = 1;
				
				//insertOPTStatus ((byte)'0');
				insertDeviceCtrl_C1 ((byte) '1', "01", (byte) 0x80, (byte) 0x01, "02", "000102");

				LogUtility.getPumpALogger().debug("###### 0 : �ʱ���� �Ͽ����ϴ�. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
				
				break;
			}
		}
		else if (RxBuf[3]=='8' && RxBuf[4]=='0') { // �Է¿��� ����

			processOPT (); // �Է¿��� ������ ó��
		}
		else if (RxBuf[3]=='8' && RxBuf[4]=='1') { // OPT ���� ����

			processDeviceErr (); 
		}
		else if (RxBuf[3]=='8' && RxBuf[4]=='2') { // ����̽� ��������

			if (RxBuf[6]=='1' && RxBuf[7]=='5') { // �����������

				if (RxBuf[8]=='0' && RxBuf[9]=='1') { // ��������
					
				   if (0 == m_nODTState || 1 == m_nODTState || 2 == m_nODTState) {
					
					   m_nODTState = 1;

					  // insertInitCmd();
					   
					   //insertDevCtrlOrder_42((byte)'0', "02", "01", "10000000"+"0000000000000000");
					   insertDeviceCtrl_C1 ((byte) '1', "01", (byte) 0x80, (byte) 0x01, "02", "000102");
				   	}
				   	LogUtility.getPumpALogger().debug("###### 0 : ��� �����Ͽ����ϴ�. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
				}
				else {
					LogUtility.getPumpALogger().debug("###### 0 : ����������� �۵��Ͽ����ϴ�. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
				}
			}
			else if (RxBuf[6]=='0' && RxBuf[7]=='0') // Ÿ�̸�
			{
				//insertInitCmd();
				
				//if (waitEndProcess==true) {
				if (8 == m_nODTState) {
					
					m_nODTState = 1; // �߰�    
					//insertDevCtrlOrder_42((byte)'0', "00", "02", "000"); // Ÿ�̸� ����
					insertDeviceCtrl_C0 ((byte) '1', "12", "00000");

					//waitEndProcess=false;
				} 
				
				if (7 != m_nODTState) { // 7 = ������ ������
					
					byte[] byt1 = new byte[2];										
					byt1[0] = RxBuf[10];
					byt1[1] = RxBuf[11];
					LogUtility.getPumpALogger().debug("###### 0 : ����̽� Ÿ�̸� ���� : state=" + new String(byt1) + 
							" m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
												
					m_nODTState = 1; // ������ Ÿ�ӵ��� �Է��� ������� ó��(��������)���� ����
	
					//insertDevCtrlOrder_42((byte)'0', "02", "01", "10000000" + "0000000000000000");
					insertDeviceCtrl_C1 ((byte) '1', "01", (byte) 0x80, (byte) 0x01, "02", "000102");

					makeStatusInfo (231); // �������� ���(�����)
				}

				//insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x81);
				insertDeviceCtrl_C0 ((byte) '1', "10", "01");
				
			}
			else if (RxBuf[6]=='0' && RxBuf[7]=='2') // ����
			{
				byte[] byt1 = new byte[2];
				byt1[0] = RxBuf[10]; byt1[1] = RxBuf[11];
				LogUtility.getPumpALogger().debug("###### 0 : ����̽� ���� ���� : state=" + new String(byt1) +
						" m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
			}
		}
	}

	protected boolean recvTail_proc () throws Exception, SerialConnectException {
		
		//--- send : ACK ---//
		if (sendText (ACK1)==false) {
			if (dispLevel>=3)
				LogUtility.getPumpALogger().debug("S5.Send ACK1 fail! (ODT="+nozNo+")");
			return false;
		}
		
		if (dispLevel>=3)
			LogUtility.getPumpALogger().debug("S5.Send ACK1 (ODT="+nozNo+")");

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
	
	protected boolean requestCardApprove () throws Exception {
		
		HE_wm = new HE_WorkingMessage();
		HE_wm.setNozzleNo(nozStr); // ODTNo
		HE_wm.setConnectNozzleNo(m_targetNozzle);
		HE_wm.setCardType("1");
		HE_wm.setCardNumber(m_creditNo);
		HE_wm.setBonusCard(m_bonusNo);
		HE_wm.setLiter(m_liter);
		HE_wm.setBasePrice(m_basePrice);
		HE_wm.setPrice(m_price);
		                  
		LogUtility.getPumpALogger().debug(">>>>���ο�û(HE) : ODT_No=" + nozNo);
		LogUtility.getPumpALogger().debug(">>>>���ο�û(HE) : nozzle=" + m_targetNozzle);
		LogUtility.getPumpALogger().debug(">>>>���ο�û(HE) : oll   =" + m_oilKind);
		LogUtility.getPumpALogger().debug(">>>>���ο�û(HE) : liter =" + m_liter);
		LogUtility.getPumpALogger().debug(">>>>���ο�û(HE) : bPrice=" + m_basePrice);
		LogUtility.getPumpALogger().debug(">>>>���ο�û(HE) : price =" + m_price);
		LogUtility.getPumpALogger().debug(">>>>���ο�û(HE) : credit=" + m_creditNo);
		LogUtility.getPumpALogger().debug(">>>>���ο�û(HE) : bonus =" + m_bonusNo);
		LogUtility.getPumpALogger().debug(">>>>���ο�û(HE) : ODTState=" + m_nODTState);
	
		insertRecvQueue(HE_wm);
		
		//insertDevCtrlOrder_42((byte)'1', "02", "01", "00000000"+"0000000000000000");
		insertDeviceCtrl_C1 ((byte) '1', "00", (byte) 0x80, (byte) 0x01, "02", "000102");
		//insertInitCmd();

		makeStatusInfo (236); // ���ο�û ��
		
		m_bFromODT=true;
		
		return true;
	}

	@Override
	public void requestData() throws Exception, SerialConnectException {

		int 	numBytes=0, loopCnt=0;
		boolean	occurLineErr = (lineErrCnt >= minErrCnt);
		byte[]  byt = new byte[1];
		
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
		
		// ȸ���ҷ� ���� ó��
		if (firstRequest==true) {
			firstRequest=false;
			TxQue.enQueue(tS0_Buf); // ����������û
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
					if (progressStep != 1 && /*TxQue.isEmpty()==true*/ TxQue.getItemCount()==0) // �۽��ڷ� ������
						continue;
				}
				else { // mode=POL
					if (progressStep != 1 && /*TxQue.isEmpty()==false*/ TxQue.getItemCount() > 0) // �۽��ڷ� ������
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
					LogUtility.getPumpALogger().debug("0.Send ENQ (Noz="+nozNo+")");
					Log.datas(ENQb, 4, 20);
				}

				//--- recv data---//	
				if (recvText(RxBuf) < 1) {
					if (dispLevel>=3) {
						LogUtility.getPumpALogger().debug("0.Recv DAT fail! (Noz="+nozNo+")");
						Log.datas(RxBuf, 80, 20);
					}
					lineErrCnt++;
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
							
							processRecvSTX(); // ���� ���� ������ ó��
							
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
				//###### Send data(Selecting mode) ######//
				else if (mode==SEN) { // 0x41

					if (RxBuf[0]==ACK) {
						
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
		
							if (TxQue.isEmpty()==false) { // �۽� ������ ������
	
								//###### Send data : send working-data ######//
								TxBuf = TxQue.getFirstItem();
								TxBuf[1] = nozID;
								TxBuf[2] = mode;
								setBCC (TxBuf); // write BCC
								if(sendText(TxBuf) != true) {
									if (dispLevel>=3) {
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
								
								if (TxBuf.length > 500) { // �߰�(09/01/07)
									Sleep.sleep(TxBuf.length + 100);
									LogUtility.getPumpALogger().debug("�߰����� �߻�. TxBuf.length=" + TxBuf.length + "(ODT="+nozNo+")");
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
