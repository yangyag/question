/*
 * �����پ��뼿��(����)-������, �ҹ��� = 371
 * (�߰� 2012.08, dhp)
 */ 
package com.gsc.kixxhub.device.pumpa.driverVersion;

import gnu.io.SerialPortEvent;

import java.util.Calendar;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.Hashtable;

import com.gsc.kixxhub.common.data.IPumpConstant;
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
import com.gsc.kixxhub.device.pumpa.driver.CommTsnSelfHS;
import com.gsc.kixxhub.device.pumpa.translation.TransTsnSelfHS;

public class CommTsnSelfHS_noz extends CommTsnSelfHS {

	protected static boolean m_isSaveSTX=false;
	protected byte[] 	AP_Buf = new byte[8];  // �����㰡(���)
	protected DataStruct AP_ds = new DataStruct();
	
	protected boolean beforeNozzleUp=false; // ���� ���������
	protected int		buffSize = 40;
	protected byte[] 	CQ_Buf = new byte[8];  // ���а����� ��û
	protected DataStruct CQ_ds = new DataStruct();
	protected int	dispLevel=0; // 0=������ �޽��� + ��������� STX
	protected boolean firstPumpingData=true;
	protected boolean firstRequest=true;
	
	protected DataStruct HE_ds = new DataStruct();
	protected HF_WorkingMessage HF_wm;
	protected byte[] 	IN_Buf = new byte[8];  // ������ ��⵿
	protected DataStruct IN_ds = new DataStruct();
	protected boolean	issueLineErr=true;
	
    protected byte[] 	lastPumpingData=new byte[buffSize];
    protected int		lineCommCheckCnt=0;
    protected int		lineErrCnt=0;
    //protected String	m_downBasePrice="000000";
	protected String	m_basePrice="000000";
    protected Hashtable<String, String> m_basePriceTbl = new Hashtable<String, String>();
    protected boolean m_changeBasePrice=false;
    protected int		m_nLiter=0;
    protected String  m_nozdownCmd="";
    protected boolean m_nozLock=false;
    protected int		m_nPrice=0;
    protected boolean m_recvedNozLock=false;
    protected boolean	m_setEnvDataOK=false;
    
    protected int		m_statusCode=0;
    protected int	MAX_GAUGE_SJ  =5;
    
    protected int	MAX_LAST_PDATA=3;
    protected int	MAX_PROG4STEP =15;

	protected int	MAX_StateReq_Cnt=30; // �������� ��û�ֱ�(0=nothing)
    							 protected int	MAX_WAIT_PDATA=5;
	protected boolean newPumpingData=false; // ���� ������ ����
	protected short		nozState=0;

	protected int		p0_cnt=0;
	protected byte[] 	PC_Buf = new byte[12]; // �ܰ� ����
    protected DataStruct PC_ds = new DataStruct();
	protected String presetBasePrice="000000";
	protected boolean presetDataFlag=false;
	protected String presetLiter="0000000";
	protected String presetPrice="000000";
	// 1=������ �޽��� + ��������� STX + ������� STX
	 							 // 2=������ �޽��� + ��������� STX + ����ۼ��� STX
	 							 // 3=��� �޽��� + ��� �ۼ��� STX
	// Preset data keeping - for Quick-win
	protected String presetType="2";
	//progressStep : 0=�ʱ�ȭ��, 1=�ʱ�ȭ��, 2=���������۽���, 3=������, 4=�����Ϸ�, 
    //               5=���а���������/�����Ϸ��ڷ� �۽�
    protected int 	progressStep=3;
	protected int		progressStep4Cnt=0;
	protected boolean pumpingEnable=false;
	protected boolean pumpingStart=false;
	protected boolean realPumpingStart=false; // ��������
	protected byte[] 	RT_Buf = new byte[8];  // ������ �ʱ�ȭ
	protected DataStruct RT_ds = new DataStruct();
	protected byte[] 	RxBuf = new byte[buffSize];
	protected S3_WorkingMessage S3_wm;
	
	protected S4_WorkingMessage S4_wm;
	protected S5_WorkingMessage S5_wm;
	protected S8_WorkingMessage S8_wm;
	protected byte[] 	SC_Buf = new byte[8];  // ��������

    protected DataStruct SC_ds = new DataStruct();
        
	protected SE_WorkingMessage SE_wm;
	protected boolean sentNozDownInfo=false;
	protected boolean sentPumpingEndInfo=false; // �߰�(09/09/08)
	protected boolean sentPumpingStartInfo=false;
	protected String	SJ_TotalGauge="0000000000";
	protected SJ_WorkingMessage SJ_wm;
	protected byte[] 	ST_Buf = new byte[16]; // �����㰡(������)
	protected DataStruct ST_ds = new DataStruct();
	    
	protected byte[] 	TQ_Buf = new byte[8];  // �����Ϸ� �ڷ��û	
	protected DataStruct TQ_ds = new DataStruct();
	protected TransTsnSelfHS trans = new TransTsnSelfHS(/*HE_ds,*/ m_basePriceTbl);
	protected byte[] 	TxBuf = new byte[buffSize];
	protected int		waitGaugeForSJ_Cnt=0;
	protected int		waitLastPData_Cnt=0;
	protected boolean waitLastPumpData=false; // ���� �����Ϸᰪ ���Ŵ��
	protected boolean waitTotalGuageFor_SJ=false; // �������� ������ ���Ŵ��
    
    public CommTsnSelfHS_noz (int nozNum, String romVerStr) throws Exception {

		super(nozNum, romVerStr);
    	
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
        	
			//--- KIXXHUB (��������)---//
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
				if(RxBuf[0]==ACK) {	

					flushBuffer(RxBuf);
					if (recvText(RxBuf) < 1) { 
						if (dispLevel>=3)
							LogUtility.getPumpALogger().info("1.Recv reply STX(PC) fail! (Noz="+nozNo+")");
						continue;
					}
					
					// ID, BCC, Data format �� ���Ŵܰ� Ȯ��
					if (checkRecvData(RxBuf)==0 && compareBasePrice(RxBuf)==true) {
						lineErrCnt=0;
						if (dispLevel>=3)
							LogUtility.getPumpALogger().info("1.Send STX(PC) success <- Recv reply. (Noz="+nozNo+")");
						//Log.datas(RxBuf, 20, 20);
						sendText(ACK);
						return true;
					} else {
						continue;
					}
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
			sendText(NAK);
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
	
	// get from Prime
	protected boolean chkEnvDataAndSaving (byte[] dat) throws Exception {		
		boolean envDataFlag=false;
				
		return envDataFlag;
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
		
	@Override
	protected boolean compareNozID(byte[] buf) throws Exception {
		
		if (buf[1]==nozID[0] && buf[2]==nozID[1])		
			return true;
		else 
			return false;
	}

	public byte[] generateByteStream (WorkingMessage wm) throws Exception {	
		return trans.generateByteStream(wm);
	}
	
	public byte[][] generateByteStreams (WorkingMessage wm) throws Exception {
		return trans.generateByteStreams(wm);
	}

	public WorkingMessage generateWorkingMessage (byte[] buf, String command) throws Exception {
		return trans.generateWorkingMessage(buf, command);
	}
	
	public WorkingMessage generateWorkingMessage (byte[][] buf, String command) throws Exception {
		return trans.generateWorkingMessage(buf, command);
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
			//m_downBasePrice = bPrice;
			pumpingEnable = true;
			
			// �ܰ�����
			PC_ds.editString("basePrice", bPrice.substring(0, 4) , 4);
			
			LogUtility.getPumpALogger().info("[Pump A][����-�����㰡]" + " nozzle=" + 
					PB_wm.getNozzleNo() + "("+PB_wm.getCommand()+")" +
					" liter =" + PB_wm.getLiter() +
					" | bPrice=" + PB_wm.getBasePrice() +
					" | price=" + PB_wm.getPrice() +
					" | commansSet =" + PB_wm.getCommandSet());

			if (nozState==0 || nozState==2) { // �߰�(08/10/16)
				
				// ODT���� ���ŵǴ� ������ �����㰡�ô� �ܰ����� �ʴ´�.
				if(PB_wm.getDirection().equals(IPumpConstant.DIRECTION_FROM_ODT)==false)
					changeBasePrice (); // ���� PC���� ����
				
				LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> PB ����, ����ٿ����, �����㰡(AP) : progStep=" + 
						progressStep + " nozState=" + nozState + " m_basePrice=" + m_basePrice + 
						" direction=" + PB_wm.getDirection());

			} 
		}
		else if (wm.getCommand().equals("P3_1")) { // ������ ȯ�漳��

			// �ܰ��� e0_ds�� �����Ѵ�.
			P3_1_WorkingMessage P3_wm = (P3_1_WorkingMessage) wm;
			bPrice = P3_wm.getBasePrice();
			//m_downBasePrice = bPrice;
			
//			LogUtility.getPumpALogger().info("++++ P3_1 ���� P3_wm.getNozzleNo()=" + P3_wm.getNozzleNo());
//			LogUtility.getPumpALogger().info("++++ P3_1 ���� P3_wm.getNozzleNumber()=" + P3_wm.getNozzleNumber());
//			LogUtility.getPumpALogger().info("++++ P3_1 ���� P3_wm.getConnectNozzleNo()=" + P3_wm.getConnectNozzleNo());
//			LogUtility.getPumpALogger().info("++++ P3_1 ���� P3_wm.getTargetNozzleNo()=" + P3_wm.getTargetNozzleNo());
//			LogUtility.getPumpALogger().info("++++ P3_1 ���� P3_wm.getBasePrice()=" + P3_wm.getBasePrice());
//			LogUtility.getPumpALogger().info("++++ P3_1 ���� P3_wm.getDirection()=" + P3_wm.getDirection());

			if (nozState==0 || nozState==2) { 				
				setNozzleBasePrice(bPrice); // �ܰ����� ���ΰ� ����
				changeBasePrice(); // PC ���� ����
				
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

//			LogUtility.getPumpALogger().info ("++++ nNozState=" + nNozState);
//			LogUtility.getPumpALogger().info ("++++ m_nozLock=" + m_nozLock);

			if (m_nozLock==true) {
				m_recvedNozLock=true;
				TxQue.enQueue(SC_Buf); // ����
				makeStatusInfo(656);
			}
			else {
				TxQue.enQueue(AP_Buf); // �㰡
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
		SE_wm.setErrMsg("���������� ȸ���ҷ�");

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

	@Override
	public void ownershipChange(int arg0) {
		// TODO Auto-generated method stub
		
	}

	public void processRecvSTX () throws SerialConnectException, Exception {
	
		//############# State check ##############//
		if (RxBuf[4]=='L' && RxBuf[5]=='K') { // ����ٿ�(���㰡 ����)
			nozState = (short) (nozState==4 ? 5 : 0);
			m_nozdownCmd = "LK";
			realPumpingStart=false;
		} 
		else if (RxBuf[4]=='A' && RxBuf[5]=='Q') { // �㰡�� �����
			nozState = 1;
		}
		else if (RxBuf[4]=='U' && RxBuf[5]=='L') { // ����ٿ�(�㰡 ����)
			nozState = 2;
			m_nozdownCmd = "UL";
			realPumpingStart=false;
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
				
				//setNozzleBasePrice(); // �ܰ����� ���ΰ� ����
				//changeBasePrice(); // PC ���� ����

				// �������/���� �������� ó��
				if (m_nozLock==true)
					makeStatusInfo(656); // �������
				else if (m_recvedNozLock==true) {
					makeStatusInfo(657); // �������
					m_recvedNozLock=false;
				}
				
//				LogUtility.getPumpALogger().info("++++ 0�� 0���� ó���ϱ� ��. m_nozdownCmd=" + m_nozdownCmd + 
//						" m_nozLock=" + m_nozLock + " realPumpingStart=" + realPumpingStart);

				
				// ������ٿ�� ��=0 �����Ϸ� �ڷ� ���� -> POS���� ������ ������ ���ó����
				// POS ������ ���δ� ��⿡�� �Ǵ�				
				if (m_nozdownCmd.equals("LK") && m_nozLock==false && realPumpingStart==false) {
					
					S4_wm = new S4_WorkingMessage();
					S4_wm.setNozzleNo(Change.toString("%02d",nozNo));
					S4_wm.setLiter("0000000");
					S4_wm.setBasePrice(m_basePrice);
					S4_wm.setPrice("00000000");
					S4_wm.setTotalGauge("0000000000"); 
					
					insertRecvQueue(S4_wm); // �����Ϸ� �ڷ�
					LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> ������ٿ�, ������ ������ҿ� �����Ϸ�����(S4) ����");
					
					sentPumpingEndInfo=true;
					waitTotalGuageFor_SJ=false;
					pumpingEnable = false;
				}
				
				beforeNozzleUp=false;
			}
			else if (nozState==1 || nozState==3) { // �����
				
				//LogUtility.getPumpALogger().info("++++ ����� nozzle=" + nozNo + " nozState=" + nozState);

				flushBuffer(lastPumpingData);
				
/*				if (nozState==1) {
					changeBasePrice();					
				}*/
				
				if (m_nozLock == false && pumpingStart == false) {
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
					
				beforeNozzleUp=true;
				
				realPumpingStart=false;
				sentPumpingEndInfo=false;
				sentNozDownInfo=false;
				
				//setNozzleBasePrice(); // �ܰ����� ���ΰ� ����
				//changeBasePrice(); // PC ���� ����
				makeStatusInfo(nozState); 
					
			}
			else if (nozState == 4) { // ������
				
				if (pumpingStart==false) {
					flushBuffer(lastPumpingData);
					pumpingStart=true;
				}
				
				if (m_nozLock==false && sentPumpingStartInfo==false) {
					SJ_TotalGauge = recvTotalGauge(); // --- ���۰����� ����ó�� ---//
					makePumpingStartInfo(SJ_TotalGauge); // --- ��������(SJ)���� ���� ---//
					//LogUtility.getPumpALogger().info("++++ �����(�������� ��������) nozzle=" + nozNo + " nozState=" + nozState);
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
				pumpingEnable = false;
				m_nozdownCmd = "";
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
			pumpingEnable = false;
			
			LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> �����Ϸ�, ���а����� ����(CT) : progStep=" + 
					progressStep + " nozState=" + nozState + " pumpingStart=" + pumpingStart);

			//setNozzleBasePrice();
			//changeBasePrice(); // PC ���� ����
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
				insertRecvQueue(S4_wm); // �����Ϸ��ڷ� ����
				sentPumpingEndInfo=true;
				waitTotalGuageFor_SJ=false;
			}
			
			changeBasePrice();
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
					LogUtility.getPumpALogger().info("1.Recv STX(CT) ACK fail! (Noz="+nozNo+")");
				continue;
			}
			else { // success
				if(RxBuf[0]==ACK) {	

					flushBuffer(RxBuf);
					if (recvText(RxBuf) < 1) { // recv gauge(CT) fail
						if (dispLevel>=3)
							LogUtility.getPumpALogger().info("1.Recv reply STX(CT) SOH fail! (Noz="+nozNo+")");
						continue;
					}
					
					if (checkRecvData(RxBuf)==0) { // ID, BCC, Data format Ȯ�� -> 0=����
						System.arraycopy(RxBuf, 6, byGauge, 0, 10);
						gauge = new String(byGauge);
						lineErrCnt=0;
						sendText(ACK);
						return gauge;
					}
					else {
						continue;
					}
				}
			}
		}
		
		lineErrCnt++;
		return gauge;
	}
	
	@Override
	public void requestData() throws SerialConnectException, Exception {

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

//			LogUtility.getPumpALogger().debug("\n>>>>> Received  Down data : " +
//								"nozNo=" + nozStr + " command=" + wm.getCommand());

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
			if (wm.getCommand().equals("PB")) {
				LogUtility.getPumpALogger().debug(":::::::::>>> ByteStreams of workingMessage ["+ wm.getCommand() + "] in requestData().");
				Log.datas(wmByte, wmByte.length, 20);
			}*/
			
			wm=null;
		}
		if (dispLevel>=3)
			LogUtility.getPumpALogger().debug("\nStart request ============> TatsunoHS, nozNo=" + nozNo);

		try {
			//##### Send ENQ and Recv ACK/STX #####//
			if (dispLevel>=3)
				LogUtility.getPumpALogger().debug("----POLLING----(progStep="+progressStep+", nozState="+nozState+")");
			
			//--- Send ENQ ---//
			if(sendText (enq_Buf) != true) { // fail
				lineErrCnt++;
				return;
			}

			if (dispLevel>=3) {
				LogUtility.getPumpALogger().info("0.Send ENQ (Noz="+nozNo+")");
				Log.datas(enq_Buf, enq_Buf.length, 20);
			}
			
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
							Log.datas(RxBuf, buffSize, 20);
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
								Log.datas(RxBuf, buffSize, 20);
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
	
						if (dispLevel>=3) {
							LogUtility.getPumpALogger().info("2.Recv ACK (Noz="+nozNo+")");
							Log.datas(RxBuf, 10, 20);
						}
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

								TxQue.deQueue(); //-- �۽ſϷ��� �۽ŵ����� ����							

								//--- ���䵥���� Ȯ��(ACK + SOH + Datas) ---//								
								flushBuffer(RxBuf);								
								if (recvText(RxBuf) > 0) {
									if (RxBuf[0] == SOH) { // recv reply data : SOH
										
										if (dispLevel>=3) {
											LogUtility.getPumpALogger().info ("2.Recv STX (Noz="+nozNo+")");
											Log.datas(RxBuf, buffSize, 20);
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
												Log.datas(RxBuf, buffSize, 20);
											}
											
											if (m_isSaveSTX==true) 
												LogUtility.getPumpALogger().info("Recv STX("+Change.toString("%02d", nozNo)+
														") : ["+pack(RxBuf, ETX)+"]");
											else
												LogUtility.getPumpALogger().debug("Recv STX("+Change.toString("%02d", nozNo)+
														") : ["+pack(RxBuf, ETX)+"]");
		/*									
											if (m_isSaveSTX==true) 
												LogUtility.getPumpALogger().info("Recv STX("+nozStr+") : ["+pack(RxBuf, ETX)+"]");
											else
												LogUtility.getPumpALogger().debug("Recv STX("+nozStr+") : ["+pack(RxBuf, ETX)+"]");
		*/
											recvTail_proc(ACK);
											processRecvSTX(); //--- ���� ���ŵ����� ó�� ---//
										}										
									}
								}
								
								

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
						Log.datas(RxBuf, buffSize, 20);
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
	
	
	@Override
	public void run() {

	}


	// To be invoked when InputStream(is) has a receiving data
	@Override
	public void serialEvent(SerialPortEvent event) {

	}
		
	protected void setNozzleBasePrice(String bPrice) throws Exception { // �ܰ����� ���ΰ� ����

		PC_ds.editString("basePrice", bPrice.substring(0, 4) , 4);
		PC_Buf = PC_ds.getByteStream();
		
		m_basePrice = bPrice;
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
