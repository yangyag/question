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
	protected byte[] 	AP_Buf = new byte[8];  	// �����㰡(���)
	protected DataStruct AP_ds = new DataStruct();
	protected int	baseMinErrCnt=8, baseMaxSkipCnt=100;
	protected int	baseReadBuffInterval   = 30+5; // ����Ʈ������ PJT ����
	// 1=������ �޽��� + ��������� STX + ������� STX
	 							 // 2=������ �޽��� + ��������� STX + ����ۼ��� STX
	 							 // 3=��� �޽��� + ��� �ۼ��� STX
	protected int	baseReadStartInterval  = 20+4; // ����Ʈ������ PJT ����
	protected int	baseWriteStartInterval = 20+4; // ����Ʈ������ PJT ����
	
	protected boolean beforeNozzleUp=false; // ���� ���������
	protected int		buffSize = 40;
	protected byte[] 	CQ_Buf = new byte[8];  	// ���а����� ��û
	protected DataStruct CQ_ds = new DataStruct();
	protected byte DC1 = 0x11;
	
    protected byte DC2 = 0x12;
    protected byte DC3 = 0x13;
    protected byte DC4 = 0x14;
    protected int	dispLevel=0; // 0=������ �޽��� + ��������� STX
    protected byte ENQ = 0x05;
    protected byte[] 	enq_Buf = new byte[3];
    protected byte EOB = 0x17;
    protected byte EOT = 0x04;
    protected byte ETX = 0x03;
    protected boolean firstPumpingData=true;
    protected boolean firstRequest=true;
    protected byte FS  = 0x1c;
    protected HF_WorkingMessage HF_wm;
    
    protected byte[] 	IN_Buf = new byte[8];  		// ������ ��⵿
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
	protected int	MAX_StateReq_Cnt=30; // �������� ��û�ֱ�(0=nothing)
	protected int	MAX_WAIT_PDATA=5;
	protected byte NAK = 0x15;
	protected boolean newPumpingData=false; // ���� ������ ����
	protected byte[] 	nozID = new byte[2];
	protected short		nozState=0;
	protected String 	nozStr = "";
	protected int		p0_cnt=0;
	protected byte[] 	PC_Buf = new byte[12]; 	// �ܰ� ����
	protected DataStruct PC_ds = new DataStruct();
	protected String presetBasePrice="000000";
	protected boolean presetDataFlag=false;
	protected String presetLiter="0000000";
	protected String presetPrice="000000";
	// Preset data keeping - for Quick-win
	protected String presetType="2";
	
	//progressStep : 0=�ʱ�ȭ��, 1=�ʱ�ȭ��, 2=���������۽���, 3=������, 4=�����Ϸ�, 
    //               5=���а���������/�����Ϸ��ڷ� �۽�
    protected int 	progressStep=3;
	protected int		progressStep4Cnt=0;

    protected boolean pumpingStart=false;
    
	protected int	readCmdInterval    = 20; // readBuffInterval when read command(ACK/EOT etc.)
	protected boolean realPumpingStart=false; // ��������
	protected byte[] 	RT_Buf = new byte[8];  	// ������ �ʱ�ȭ
	protected DataStruct RT_ds = new DataStruct();
	protected boolean rtn;
	protected byte[] 	RxBuf = new byte[buffSize];
	protected S3_WorkingMessage S3_wm;
	
	protected S4_WorkingMessage S4_wm;
	protected S5_WorkingMessage S5_wm;
	protected S8_WorkingMessage S8_wm;
	protected byte[] 	SC_Buf = new byte[8];  	// ��������
	protected DataStruct SC_ds = new DataStruct();
	protected SE_WorkingMessage SE_wm;
    
	protected boolean sentNozDownInfo=false;
	protected boolean sentPumpingEndInfo=false; // �߰�(09/09/08)
	protected boolean sentPumpingStartInfo=false;
	protected String	SJ_TotalGauge="0000000000";
	protected SJ_WorkingMessage SJ_wm;
	protected byte SOH = 0x01;
	protected byte[] 	ST_Buf = new byte[16]; 	// �����㰡(������)
	protected DataStruct ST_ds = new DataStruct();
	
    protected byte STX = 0x02;
    
	protected byte[] 	TQ_Buf = new byte[8];  	// �����Ϸ� �ڷ��û
	protected DataStruct TQ_ds = new DataStruct();
	protected TransSk trans = new TransSk();
	protected byte[] 	TxBuf = new byte[buffSize];
	protected int		waitGaugeForSJ_Cnt=0;
	protected int		waitLastPData_Cnt=0;
	protected boolean waitLastPumpData=false; // ���� �����Ϸᰪ ���Ŵ��
	protected boolean waitTotalGuageFor_SJ=false; // �������� ������ ���Ŵ��
    
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
	    	
			//--- AP (�����㰡-���)---//
        	AP_ds.addByte("SOH", SOH);
			AP_ds.addString("nozNo", nozStr, 2);
			AP_ds.addByte("STX", STX);
			AP_ds.addString("command", "AP", 2);
			AP_ds.addByte("ETX", ETX);
			AP_ds.addByte("bcc", (byte) ' ');
			AP_Buf = AP_ds.getByteStream();
	    	
			//--- CQ (���а����� ��û)---//
        	CQ_ds.addByte("SOH", SOH);
        	CQ_ds.addString("nozNo", nozStr, 2);
        	CQ_ds.addByte("STX", STX);
        	CQ_ds.addString("command", "CQ", 2);
        	CQ_ds.addByte("ETX", ETX);
        	CQ_ds.addByte("bcc", (byte) ' ');
        	CQ_Buf = CQ_ds.getByteStream();
        	
			//--- IN (������ ��⵿)---//
        	IN_ds.addByte("SOH", SOH);
        	IN_ds.addString("nozNo", nozStr, 2);
        	IN_ds.addByte("STX", STX);
        	IN_ds.addString("command", "IN", 2);
        	IN_ds.addByte("ETX", ETX);
        	IN_ds.addByte("bcc", (byte) ' ');
        	IN_Buf = IN_ds.getByteStream();
	    	
			//--- PC (�ܰ�����)---//
        	PC_ds.addByte("SOH", SOH);
        	PC_ds.addString("nozNo", nozStr, 2);
        	PC_ds.addByte("STX", STX);
        	PC_ds.addString("command", "PC", 2);
        	PC_ds.addString("basePrice", "0000", 4);
        	PC_ds.addByte("ETX", ETX);
        	PC_ds.addByte("bcc", (byte) ' ');
        	PC_Buf = PC_ds.getByteStream();
        	
			//--- RT (������ �ʱ�ȭ)---//
        	RT_ds.addByte("SOH", SOH);
        	RT_ds.addString("nozNo", nozStr, 2);
        	RT_ds.addByte("STX", STX);
        	RT_ds.addString("command", "RT", 2);
        	RT_ds.addByte("ETX", ETX);
        	RT_ds.addByte("bcc", (byte) ' ');
        	RT_Buf = RT_ds.getByteStream();
        	
			//--- SC (��������)---//
        	SC_ds.addByte("SOH", SOH);
        	SC_ds.addString("nozNo", nozStr, 2);
        	SC_ds.addByte("STX", STX);
        	SC_ds.addString("command", "SC", 2);
        	SC_ds.addByte("ETX", ETX);
        	SC_ds.addByte("bcc", (byte) ' ');
        	SC_Buf = SC_ds.getByteStream();
        	
			//--- ST (�����㰡-������)---//
        	ST_ds.addByte("SOH", SOH);
        	ST_ds.addString("nozNo", nozStr, 2);
        	ST_ds.addByte("STX", STX);
        	ST_ds.addString("command", "ST", 2);
        	ST_ds.addByte("mode", (byte) ' '); // Q=����, A=�ݾ�, F=Full
        	ST_ds.addString("preset", "0000000", 7); // ����=4.3, �ݾ�=7.0
        	ST_ds.addByte("ETX", ETX);
        	ST_ds.addByte("bcc", (byte) ' ');
        	ST_Buf = ST_ds.getByteStream();

			//--- TQ (�����Ϸ� �ڷ��û)---//
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
			
			if(sendText(TxBuf) != true) { // �ܰ����� ��û(PC)
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
				// ID, BCC, Data format �� ���Ŵܰ� Ȯ��
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
		
		if (compareNozID(byData) == false) { // NAK �Ⱥ���
			LogUtility.getPumpALogger().info("1.Recv STX NozID mismatch-1.0! (Noz=" + nozNo + ")");
			Log.datas(byData, 40, 20);

			trimInputStream("routine : 1");
			return 1; // �ݺ�
		}
		
		if (compareBCC(byData) == false) {
			sendText(NAK);
			LogUtility.getPumpALogger().info("1.Recv STX BCC fail! (Noz=" + nozNo + ")");
			Log.datas(byData, 40, 20);
			return 1; // �ݺ�
		}
		
		if (verifyData(byData) == false) {
			LogUtility.getPumpALogger().info("1.Recv STX Data verify fail! (Noz=" + nozNo + ")");
			Log.datas(byData, 40, 20);
			return 2; // ����
		}
		
		if (byData[4]=='P' && byData[5]=='C') { // �ܰ�����
			if (compareBasePrice(byData) == false) {
				LogUtility.getPumpALogger().info("1.Recv basePrice verify fail! (Noz=" + nozNo + ")");
				Log.datas(byData, 40, 20);
				lineErrCnt++;
				return 2; // ����
			}
		}
		
		return 0; // ����
	}
	
	protected boolean compareBasePrice (byte[] buf) throws Exception {
		
		try {
			if (m_basePrice.equals("000000")) // �ܰ����� �̼��� ����
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
				
		if (wm.getCommand().equals("QF")) { // ������ �ڷ� ��û
			
			HF_wm = new HF_WorkingMessage();
			HF_wm.setNozzleNo(Change.toString("%02d", nozNo));
			HF_wm.setType(presetType);
			HF_wm.setLiter(presetLiter);
			HF_wm.setBasePrice(presetBasePrice);
			HF_wm.setPrice(presetPrice);
		
			insertRecvQueue(HF_wm);
			
			skip = true;
		}	
		else if (wm.getCommand().equals("PB")) { // �������� ����
			
			// �ܰ��� e0_ds�� �����Ѵ�.
			PB_WorkingMessage PB_wm = (PB_WorkingMessage) wm;
			bPrice = PB_wm.getBasePrice();
			m_downBasePrice = bPrice;
			
			// �ܰ�����
			PC_ds.editString("basePrice", bPrice.substring(0, 4) , 4);
			
			LogUtility.getPumpALogger().info("[Pump A][����-�����㰡]" + " nozzle=" + 
					PB_wm.getNozzleNo() + "("+PB_wm.getCommand()+")" +
					" liter =" + PB_wm.getLiter() +
					" | bPrice=" + PB_wm.getBasePrice() +
					" | price =" + PB_wm.getPrice());
										
			if (nozState==0 || nozState==2) { // �߰�(08/10/16)
				
				setNozzleBasePrice();
				changeBasePrice ();
				
				LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> PB ����, ����ٿ����, �����㰡(AP) : progStep=" + 
						progressStep + " nozState=" + nozState + " m_basePrice=" + m_basePrice);

				skip = false;
			} else 
				skip = true; // ����� �Ǵ� �������̸� �ܰ����� �ʴ´�.
		}
		else if (wm.getCommand().equals("P3_1")) { // ������ ȯ�漳��

			// �ܰ��� e0_ds�� �����Ѵ�.
			P3_1_WorkingMessage P3_wm = (P3_1_WorkingMessage) wm;
			bPrice = P3_wm.getBasePrice();
			m_downBasePrice = bPrice;

			PC_ds.editString("basePrice", bPrice.substring(0, 4) , 4);

			if (nozType==2 || nozState==0 || nozState==2) { // �߰�(08/10/16)
				setNozzleBasePrice();
				LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> P3_1 ����, ����ٿ����, �ܰ�����(PC) : progStep=" + 
						progressStep + " nozState=" + nozState + " m_basePrice=" + m_basePrice);

				skip = false;
			} else 
				skip = true; // ����� �Ǵ� �������̸� �ܰ����� �ʴ´�.
		}
		else if (wm.getCommand().equals("PA")) { // �������� ��û(�������/����)

			PA_WorkingMessage PA_wm = (PA_WorkingMessage) wm;
			int nNozState = Change.toValue(PA_wm.getNozzleState());

			m_nozLock = (nNozState==0 ? true : false);

			if (m_nozLock==true) {
				m_recvedNozLock=true;
				makeStatusInfo(656);
			}
			else {
				if (nozType != 2) // �߰�(08/12/02)
					TxQue.enQueue(AP_Buf);
				makeStatusInfo(657);
			}

			skip = false;
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
		SE_wm.setStatusCode("601"); // ȸ���ҷ�
		SE_wm.setErrMsg("������ ȸ���ҷ�");

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
					
		insertRecvQueue(SJ_wm); // �������� �ڷ� �۽�
		
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
				statusCode = 651; // ����ٿ�
				errorMsg = "����ٿ�";
				break;
			case 1:
			case 3:
				statusCode = 652; // �����
				errorMsg = "�����";
				break;
			case 4:
				statusCode = 653; // ������
				errorMsg = "������";
				break;
			case 5:
				statusCode = 654; // �����Ϸ�
				errorMsg = "�����Ϸ�";
				break;
			case 655:
				statusCode = 655; // ������ ����(ENE �����ڵ�(E) ����)
				errorMsg = "������ ����";
				break;
			case 656:
				statusCode = 656; // �������(��������)
				errorMsg = "�������";
				break;
			case 657:
				statusCode = 657; // �����������
				errorMsg = "�������";
				break;
			case 650:
				statusCode = 650; // ����
				errorMsg = "����";
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
		if (RxBuf[4]=='L' && RxBuf[5]=='K') { // �㰡�� ����ٿ�
			nozState = (short) (nozState==4 ? 5 : 0);
		} 
		else if (RxBuf[4]=='A' && RxBuf[5]=='Q') { // �㰡�� �����
			nozState = 1;
		}
		else if (RxBuf[4]=='U' && RxBuf[5]=='L') { // �㰡�� ����ٿ�
			nozState = 2;
		}
		else if (RxBuf[4]=='P' && RxBuf[5]=='P') { // �㰡�� �����(��������, ������)
			nozState = 4;
		}
		else if (RxBuf[4]=='T' && RxBuf[5]=='R') { 
			nozState = 5;
		} 
		else if (RxBuf[4]=='C' && RxBuf[5]=='T') { // ���а����� ����(�������� ��)
		
			if (nozState==3) { // ����� ���¿��� ����
				nozState = 4;
			}
			else if (progressStep==4) { // �����Ϸ��� ����
				progressStep = 5;
			} 
			else 
				insertRecvQueue(generateWorkingMessage(RxBuf, "S5")); // ���а����� �ڷ� �۽�
		}

//		LogUtility.getPumpALogger().debug("#### STX Start : nozzle=" + nozStr + " ProgStep=" +
//				progressStep + " nozState=" + nozState + " pumpingStart=" + pumpingStart);
		
		//############# STX Process ##############//
		switch (progressStep) {
		case 3 :
			if (nozState==0 || nozState==2) { // ����ٿ�

				flushBuffer(lastPumpingData);
				sentPumpingStartInfo=false;
				waitTotalGuageFor_SJ=false; 
				waitGaugeForSJ_Cnt=0; 
				
				if (sentNozDownInfo==false) { 
					makeStatusInfo(nozState); // �������� ����
					sentNozDownInfo=true;
				}
				
				setNozzleBasePrice();

				// �������/���� �������� ó��
				if (m_nozLock==true)
					makeStatusInfo(656); // �������
				else if (m_recvedNozLock==true) {
					makeStatusInfo(657); // �������
					m_recvedNozLock=false;
				}

				// ������ٿ�� ��=0 �����Ϸ� �ڷ� ���� -> POS���� ������ ������ ���ó����
				// POS ������ ���δ� ��⿡�� �Ǵ�
				if (beforeNozzleUp==true && realPumpingStart==false && // ���� �����Ϸ��ڷᰡ �ƴϸ�
						sentPumpingEndInfo==false) {
					
					S4_wm = new S4_WorkingMessage();
					S4_wm.setNozzleNo(Change.toString("%02d",nozNo));
					S4_wm.setLiter("0000000");
					S4_wm.setBasePrice(m_basePrice);
					S4_wm.setPrice("00000000");
					S4_wm.setTotalGauge("0000000000"); 
					
					if (nozType==2) { // �����������̸�
						if (nozState==2) {
							insertRecvQueue(S4_wm); // �����Ϸ� �ڷ�
							LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> ������ٿ�, ������ ������ҿ� �����Ϸ�����(S4) ����");
						}
					} 
					else {
						insertRecvQueue(S4_wm); // �����Ϸ� �ڷ�
						LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> ������ٿ�, ������ ������ҿ� �����Ϸ�����(S4) ����");
					}
					
					sentPumpingEndInfo=true;
					waitTotalGuageFor_SJ=false; 
				}
				
				beforeNozzleUp=false;
			}
			else if (nozState==1 || nozState==3) { // �����

				flushBuffer(lastPumpingData);
				
				if (nozState==1) {
					changeBasePrice();
					
					if (m_nozLock==false && sentPumpingStartInfo==false) {
						SJ_TotalGauge = recvTotalGauge(); // --- ���۰����� ����ó�� ---//
						makePumpingStartInfo(SJ_TotalGauge); // --- ��������(SJ)���� ���� ---//
					}
	
					if (m_nozLock == false && nozType == 1 && pumpingStart == false) {
						if (Change.toValue(m_basePrice) > 0) {
							// ��� �����㰡(AP)
							if (pumpingEnable() == false)
								return;
							else {
								nozState = 3;
								LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> �����, �����㰡(AP) : progStep="
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
			else if (nozState == 4) { // ������
				
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

					// ���� ������ ���Ž� Skip �� ���� ó��
					if (nPrice > 0 && nLiter > 0) {	
						if (newPumpingData==false && nLiter==m_nLiter) {
							LogUtility.getPumpALogger().info("nozzle=" + nozNo + 
									" -> ������(PP)�� ���� �������� �����մϴ�. : ������(x.3)=" + 
									nLiter + " ���� ������(x.3)=" + m_nLiter);
						}
						else {
							lastPumpingData = RxBuf.clone(); // ���� �������� ����
							realPumpingStart=true;
							newPumpingData=true;
						}
					}

					if (presetDataFlag==true) 
						presetDataFlag=false; // Preset data �̸� skip
					else {
						if (nPrice > 0 && nLiter > 0) {
							if (firstPumpingData==true) {
								makeStatusInfo(nozState); // �������� ����(������)
								firstPumpingData=false;
							}
							if (newPumpingData==true) 
								insertRecvQueue(S3_wm); // ������ �ڷ� ���� �۽�
						}
					}
				}
				
				sentPumpingEndInfo=false; 	
				sentNozDownInfo=false;
			}
			else if (nozState == 5) { // �����Ϸ�

				if (pumpingStart==false) {
					flushBuffer(lastPumpingData);
					pumpingStart=true;
				}
				
				TxQue.enQueueNewer(TQ_Buf); // �����Ϸ� �ڷ� ��û
				
				progressStep=4;
			}
			break;

		case 4 :
			
			// ���� �����ڷ� ���� ó��			
			if ((RxBuf[4]=='T' && RxBuf[5]=='R') || waitLastPData_Cnt>=MAX_LAST_PDATA) {
				
				if ((RxBuf[4]=='T' && RxBuf[5]=='R')) {
					
					byte[][] tBuf = new byte [2][buffSize];
					tBuf[0] = RxBuf.clone(); // ���� ��������
					//tBuf[1] = RxBuf.clone(); // Total guage
					
					S4_wm = (S4_WorkingMessage) generateWorkingMessage(tBuf, "S4");
					int nPrice=0, nLiter=0;
					if (S4_wm != null) {
						nPrice = Change.toValue(S4_wm.getPrice());
						nLiter = Change.toValue(S4_wm.getLiter());
					}
					if (nPrice > 0 || nLiter > 0)  
						lastPumpingData = RxBuf.clone(); // ���� �������� ����

					LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> �����Ϸ�, ���������� ����(TR) : progStep=" + 
								progressStep + " waitLastPData_Cnt=" + waitLastPData_Cnt + 
								" liter=" + S4_wm.getLiter() + 
								" price=" + S4_wm.getPrice());
				} else
					LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> �����Ϸ�, ���������� �̼���(TR), ���������� ���� : progStep=" + 
							progressStep + " waitLastPData_Cnt=" + waitLastPData_Cnt);
				
				TxQue.enQueueNewer(CQ_Buf); // ���а����� ��û
				
				LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> �����Ϸ�, ���а����� ��û(CQ) : progStep=" + 
						progressStep + " nozState=" + nozState + " pumpingStart=" + pumpingStart);
				
				makeStatusInfo(nozState); // �������� ����(�����Ϸ�)
				
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
				
				LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> �����Ϸ�, ���������� ���Ŵ�� : nozState=" +
						nozState + " waitLastPData_Cnt=" + waitLastPData_Cnt);
			}
				
			break;

		case 5 : // �����Ϸ��� ���а����� ����

			byte[][] tBuf = new byte [2][buffSize];

			tBuf[0] = lastPumpingData.clone(); // ���� ��������
			flushBuffer(lastPumpingData);
			tBuf[1] = RxBuf.clone(); // Total guage
			
			progressStep = 3;
			nozState = 0;
			progressStep4Cnt=0;

			pumpingStart=false;
			sentPumpingStartInfo=false;
			firstPumpingData=true;
			newPumpingData=false;
			
			LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> �����Ϸ�, ���а����� ����(CT) : progStep=" + 
					progressStep + " nozState=" + nozState + " pumpingStart=" + pumpingStart);

			setNozzleBasePrice();
			/*
			if (m_nozLock==false && nozType==1) { // �Ϲ�������
				if (Change.toValue(m_basePrice) > 0) {
					TxQue.enQueueNewer(AP_Buf); // �����㰡(����/���� ����)				
					LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> ���а����� ����, �����㰡(AP) : progStep=" + 
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
				LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> ���а��������� 0 �̰ų�, �̼����Ͽ� 0 ���� ����");
			
			 // ����(08/09/10)
			if (nLiter <= 0 || nPrice <= 0) {
				S4_wm.setBasePrice(m_basePrice);
				LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> �����Ϸ�(S4) �ڷᰪ=0 : Liter=" + 
						nLiter + " Price=" + nPrice);
			}
			if (sentPumpingEndInfo==false) {
				insertRecvQueue(S4_wm); // �����Ϸ� �ڷ�
				sentPumpingEndInfo=true;
				waitTotalGuageFor_SJ=false;
			}
			
			//changeBasePrice();
			makeStatusInfo(0); // ��������(����ٿ�) ����
			
			break;
		}
	}
	
	protected boolean pumpingEnable () throws Exception, SerialConnectException {
		
		TxBuf = AP_ds.getByteStream();
		TxBuf[1] = nozID[0];
		TxBuf[2] = nozID[1];
		setBCC (TxBuf); // write BCC
		
		for (int i=0; i<3; i++) {
			
			if(sendText(TxBuf) != true) { // ��� �����㰡(AP)
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
			
			if(sendText(TxBuf) != true) { // �������� ��û(CQ)
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
				if (checkRecvData(RxBuf)==0) { // ID, BCC, Data format Ȯ�� -> 0=����
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
		
		// ȸ���ҷ� ó��
		boolean	occurLineErr = (lineErrCnt >= minErrCnt);
			
		if (occurLineErr) { // Check line error

			if (issueLineErr==true) {
				makeLineError(); // ȸ���ҷ�
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
				
		// ȸ���ҷ� ������ ó��
		if (firstRequest==true) {
			firstRequest=false;
			TxQue.enQueueNewer(CQ_Buf); // ����������û
		}
		if (m_statusCode==601) { // ȸ���ҷ�
			if (lineCommCheckCnt%(minErrCnt+5)==0) {
				lineCommCheckCnt=0;
				TxQue.enQueueNewer(CQ_Buf); // ����������û
			}
			lineCommCheckCnt++;
		}
						
		// �����Ϸ�(S4) ���������� �̼��� ó��
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
		
		// �����Ϸ�(S4) ���а����� �̼��� ó��
		if (progressStep==4) {
			if (progressStep4Cnt >= MAX_PROG4STEP) {
				
				progressStep4Cnt = 0;
				progressStep = 5;
				try {
					processRecvSTX();
					flushBuffer(lastPumpingData);
					
					LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> �����Ϸ�, ���а�����(CT) �̼���, processSTX() ȣ��.");
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
						
						short result = checkRecvData(RxBuf); // ID, BCC, Data format Ȯ��
						
						if (result==1) {
							lineErrCnt++;
							continue;
						} else if (result==2) {
							lineErrCnt++;
							return;
						} else if (result==0) { //  STX �������
							
							if (dispLevel==1 || dispLevel==2) {
								LogUtility.getPumpALogger().info("1.Recv STX with normal (Noz="+nozNo+")");
								Log.datas(RxBuf, 40, 20);
							}
							
							if (m_isSaveSTX==true) 
								LogUtility.getPumpALogger().info("Recv STX("+nozStr+") : ["+pack(RxBuf, ETX)+"]");
							else
								LogUtility.getPumpALogger().debug("Recv STX("+nozStr+") : ["+pack(RxBuf, ETX)+"]");
							
							recvTail_proc(ACK);						
							processRecvSTX(); //--- ���� ���ŵ����� ó�� ---//
					
							lineErrCnt=0; // Normal terminated
							//return;
						}
					} 
					else if (RxBuf[0] == ACK) { // recv : ACK
	
						if (dispLevel>=3)
							LogUtility.getPumpALogger().info("2.Recv ACK (Noz="+nozNo+")");
					}

					if (TxQue.isEmpty()==false) { // �۽� ������ ������
						
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
								
								// --- �۽ſϷ��� �۽ŵ����� ���� ---//
								TxQue.deQueue();
								lineErrCnt = 0; // Normal terminated
								return;
							}
							if (RxBuf[0] == SOH) { // recv reply data : SOH
								
								if (dispLevel>=3) {
									LogUtility.getPumpALogger().info ("2.Recv STX (Noz="+nozNo+")");
									Log.datas(RxBuf, 40, 20);
								}
								
								short result = checkRecvData(RxBuf); // ID, BCC, Data format Ȯ��
								
								if (result==1) {
									lineErrCnt++;
									continue;
								} else if (result==2) {
									lineErrCnt++;
									return;
								} else if (result==0) { //  STX �������
									
									if (dispLevel==1 || dispLevel==2) {
										LogUtility.getPumpALogger().info ("2.Recv STX with normal (Noz="+nozNo+")");
										Log.datas(RxBuf, 40, 20);
									}
									
									if (m_isSaveSTX==true) 
										LogUtility.getPumpALogger().info("Recv STX("+nozStr+") : ["+pack(RxBuf, ETX)+"]");
									else
										LogUtility.getPumpALogger().debug("Recv STX("+nozStr+") : ["+pack(RxBuf, ETX)+"]");

									recvTail_proc(ACK);
									processRecvSTX(); //--- ���� ���ŵ����� ó�� ---//
								}
								
								// --- �۽ſϷ��� �۽ŵ����� ���� ---//
								TxQue.deQueue();
								lineErrCnt = 0; // Normal terminated
								return;
							}
							else if (RxBuf[0] == NAK) {	// recv : NAK
								
								sendText(TxBuf); // ������
									
								LogUtility.getPumpALogger().info("2.Send STX fail!(Returned NAK)->retry send (Noz="+nozNo+")");
								Log.datas(TxBuf, TxBuf.length, 20);
								continue;
							}							
						} // end of for (int j=0; j<3; j++)
					} 
					else { //--- �۽� ������ ������ ---//
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
//			LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> ��������(s0) ���û");
//		}
//		else if (buf[4]=='p' && buf[5]=='0') {
//			TxQue.enQueueNewer(p0_Buf);
//			LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> �����ڷ�(p0) ���û");
//		}
//		else if (buf[4]=='t' && buf[5]=='0') {
//			TxQue.enQueueNewer(t0_Buf);
//			LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> ���а�����(t0) ���û");
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

		if (buf[4]=='P' && buf[5]=='C') { // �ܰ�����
			size = 12;
			cmd = "PC";
			startIdx=6;
		}
		else if (buf[4]=='P' && buf[5]=='P') { // ������ �ڷ�
			size = 22;
			cmd = "PP";
			startIdx=6;
		}
		else if (buf[4]=='T' && buf[5]=='R') { // �����Ϸ� �ڷ�
			size = 26;
			cmd = "TR";
			startIdx=6;
		}
		else if (buf[4]=='C' && buf[5]=='T') { // ���а�����
			size = 18;
			cmd = "CT";
			startIdx=6;
		}
		else {
			return true;
		}
		
		//--- ������ ����(������ ���̿� ETX Ȯ��) ---//
		if (size != bufLen || buf[size-2] != ETX) {
			LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> �̻����� ����("+cmd+"), �����ͱ���=" + 
					size + ", ���ŵ����ͱ���=" + bufLen + ", ETX=" + buf[size-1]);
			return false;
		}

		//--- ������ ����(���ڿ���) ---//
		for (int i=startIdx; i<size-2; i++) {
			if (buf[i] < 0x30 || buf[i] > 0x39) { // 0 ~ 9 �� �ƴϸ�
				LogUtility.getPumpALogger().debug("nozzle=" + nozNo + 
						" -> �̻����� ����("+cmd+"), ���ڰ� �ƴ�, ���ŵ�����=" + buf[i]);
				return false;
			}
		}
			
		return true;
	}
}
