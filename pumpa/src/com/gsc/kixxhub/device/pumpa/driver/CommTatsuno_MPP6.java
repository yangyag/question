package com.gsc.kixxhub.device.pumpa.driver;

import gnu.io.CommPortOwnershipListener;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.Vector;

import com.gsc.kixxhub.common.data.pump.EX_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HF_WorkingMessage;
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
import com.gsc.kixxhub.device.pumpa.controller.TatsunoMPPNoz;
import com.gsc.kixxhub.device.pumpa.define.CCommDriver;
import com.gsc.kixxhub.device.pumpa.translation.TransTatsuno_MPP6;

public class CommTatsuno_MPP6 extends CCommDriver implements SerialPortEventListener, 
					CommPortOwnershipListener, Runnable {

	protected static boolean m_isSaveSTX=false;
	protected static int	MAX_PROG4STEP =5;
	protected static int	MAX_WAIT_PDATA=5;
	public static byte[][] t11_staBuf = new byte[100][];
	protected byte  	ACK  = 0x10;
	protected byte  	SEL  = 0x41;
	protected byte[] 	ACK0 = new byte[2];
	protected byte[] 	ACK1 = new byte[2];
	protected int		baseMinErrCnt=8, baseMaxSkipCnt=100;

	protected int		baseNozNo=0;
	protected int		baseReadBuffInterval   = 30+7; // ����Ʈ������ PJT ����
	
    protected int		baseReadStartInterval  = 20+5; // ����Ʈ������ PJT ����
    protected int		baseWriteStartInterval = 20+5; // ����Ʈ������ PJT ����
    protected boolean beforeNozzleUp=false; // ���� ���������
    protected int		buffSize = 100;
    protected int	dispLevel=0;
    protected byte  	ENQ  = 0x05;
    protected byte[] 	enq_Buf = new byte[4];
    protected byte  	EOT  = 0x04;
    protected byte  	ETX  = 0x03;
    protected boolean firstPumpingData=true;
    protected boolean firstRequest=true;
    protected HF_WorkingMessage HF_wm;
    protected boolean	issueLineErr=true;
    protected byte[] 	lastPumpingData=new byte[buffSize];
    
	protected int		lineCommCheckCnt=0;
	protected int		lineErrCnt=0;
    protected String	m_basePrice="000000";
	protected boolean 	m_isLogPumpingSTX=false;
	
	protected long		m_nNozDownTime=0;
	protected long		m_nNozUpTime=0;
    protected boolean m_nozLock=true;
	protected boolean[] m_nozLocks={true, true, true};
	protected int		m_nS3_cnt=0;
	protected boolean m_recvedNozLock=false;
	protected boolean m_recvedSubNozID=false; // 61�� ������ nozzle status �� 1~3 ���Ž� true
	protected int 		m_statusCode=0;
	protected byte  	mode = SEL;
	protected byte  	NAK  = 0x15;
	protected byte		nozID;
	protected short		nozState=0;
	protected String 	nozStr = "";
	protected byte  	POL  = 0x51;
	protected byte  	PREFIX_ENQ = 0x05;
	//progressStep : 0=�ʱ�ȭ��, 1=�ʱ�ȭ��, 2=���������۽���, 3=������, 4=�����Ϸ�, 
    //               5=���а���������/�����Ϸ��ڷ� �۽�
    protected int 	progressStep=0;
	protected boolean pumpingStart=false;
	protected byte[] 	rcvInitBuf = new byte[6];
	protected boolean realPumpingStart=false; // ��������
	protected boolean recvedPumpingEnd=false;
	protected boolean resentPumpEnable=false; // �����㰡 ������ ����
	protected byte[] 	RxBuf = new byte[buffSize];

    protected S3_WorkingMessage S3_wm;
	
	// �پ��� 6����(����)�� ��������ȣ ó����
	
	protected S4_WorkingMessage S4_wm;
	protected S5_WorkingMessage S5_wm;
	protected S8_WorkingMessage S8_wm;
	protected SE_WorkingMessage SE_wm;

	protected int		sendPumpingData_Cnt=0;
	protected boolean sentChkNozState=false;
	protected boolean sentPumpingStartInfo=false;
    
    protected SJ_WorkingMessage SJ_wm;
    protected int		stateReq_Cnt=0;
    protected byte  	STX  = 0x02;
    protected int 		subNozCount=0;
    protected int 		subNozNo=1, fixedSubNozNo;
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
    protected Vector<TatsunoMPPNoz> tatsunoMPPNozVec;
    protected TransTatsuno_MPP6 trans;
    protected byte[] 	TxBuf = new byte[buffSize];
    protected int		waitGaugeForS4_Cnt=0;
    protected int		waitGaugeForSJ_Cnt=0;
    protected boolean waitPumpingData=false; // �����ڷ� ���ſ���
    protected int		waitPumpingData_Cnt=0;
    protected boolean waitTotalGuageFor_S4=false; // �����Ϸ� ������ ���Ŵ��
    protected boolean waitTotalGuageFor_SJ=false; // �������� ������ ���Ŵ��
    
    public CommTatsuno_MPP6 (int nozNum, String romVerStr, Vector<TatsunoMPPNoz>tatsunoMPPNozVec) {
    	
		Formatter form = new Formatter ();
		byte[] 	nozByt = new byte[2];
		
		trans = new TransTatsuno_MPP6(tatsunoMPPNozVec);
		this.tatsunoMPPNozVec = tatsunoMPPNozVec;
		subNozCount = tatsunoMPPNozVec.size();
		
		nozNo = nozNum;
		romVer = romVerStr;
		
		form.format("%02d", nozNo);
		nozStr = form.toString();
		nozByt = nozStr.getBytes();
		//nozID = (byte) (nozNo - 1 + 0x40);
		
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
		
		try {
        	flushBuffer(lastPumpingData);
        	
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
	
			//--- pumping grant(Only MPP) ---//
			t11_ds.addByte  ("STX", STX);
			t11_ds.addByte  ("SA", nozID);
			t11_ds.addByte  ("UA", SEL);
			t11_ds.addString("command", "11", 2);
			t11_ds.addByte  ("pumpingType", (byte) '2');
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
			t12_ds.addByte  ("UA", SEL);
			t12_ds.addString("command", "12", 2);
			t12_ds.addByte  ("ETX", ETX);
			t12_ds.addByte  ("BCC", (byte) ' ');
			t12_Buf = t12_ds.getByteStream();
	
			//--- pump locking ---//
			t13_ds.addByte  ("STX", STX);
			t13_ds.addByte  ("SA", nozID);
			t13_ds.addByte  ("UA", SEL);
			t13_ds.addString("command", "13", 2);
			t13_ds.addByte  ("ETX", ETX);
			t13_ds.addByte  ("BCC", (byte) ' ');
			t13_Buf = t13_ds.getByteStream();
	
			//--- pump locking cancel---//
			t14_ds.addByte  ("STX", STX);
			t14_ds.addByte  ("SA", nozID);
			t14_ds.addByte  ("UA", SEL);
			t14_ds.addString("command", "14", 2);
			t14_ds.addByte  ("ETX", ETX);
			t14_ds.addByte  ("BCC", (byte) ' ');
			t14_Buf = t14_ds.getByteStream();
	
			//--- request pump status ---//
			t15_ds.addByte  ("STX", STX);
			t15_ds.addByte  ("SA", nozID);
			t15_ds.addByte  ("UA", SEL);
			t15_ds.addString("command", "15", 2);
			t15_ds.addByte  ("ETX", ETX);
			t15_ds.addByte  ("BCC", (byte) ' ');
			t15_Buf = t15_ds.getByteStream();
	
			//--- request total gauge ---//
			t20_ds.addByte  ("STX", STX);
			t20_ds.addByte  ("SA", nozID);
			t20_ds.addByte  ("UA", SEL);
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
			
		} catch (Exception e) {}
		
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
    
	public byte[] generateByteStream (WorkingMessage wm) throws Exception {		
		return trans.generateByteStream(wm);
	}
	
	public WorkingMessage generateWorkingMessage (byte[] buf, String command) throws Exception {
		return trans.generateWorkingMessage(buf, command);
	}
	
	public WorkingMessage generateWorkingMessage (byte[] buf, String command, int subNozNo) throws Exception {
		return trans.generateWorkingMessage(buf, command, subNozNo);
	}
	
	public WorkingMessage generateWorkingMessage (byte[][] buf, String command, int subNozNo) throws Exception {
		return trans.generateWorkingMessage(buf, command, subNozNo);
	}
	
	public byte getBCC(byte[] dat) throws Exception {
		
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

	public byte[] getInitData(byte[] dat) throws Exception {
		
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
	
	protected boolean isSkipWorkingMessage (WorkingMessage wm) throws Exception {
		
		boolean skip=false;
				
		if (wm.getCommand().equals("PB")) { // �������� ����
			
			// �ܰ��� t11_ds�� �����Ѵ�.
			PB_WorkingMessage PB_wm = (PB_WorkingMessage) wm;
			m_basePrice = PB_wm.getBasePrice();	
									
			if (nozNo != baseNozNo) {
				EX_WorkingMessage EX_wm = new EX_WorkingMessage();
				EX_wm.setNozzleNo(Change.toString("%02d", nozNo));
				EX_wm.setTargetNozzleNo(Change.toString("%02d", baseNozNo));
				EX_wm.setSubCommand("PB"); // �����㰡
				EX_wm.setSubNozNo(fixedSubNozNo); // ������ ������ subNozNo�� ����
				EX_wm.setBasePrice(PB_wm.getBasePrice());
				insertRecvQueue(EX_wm);
			}
			//else
				m_nozLocks[fixedSubNozNo-1] = false;
			
			t11_staBuf[baseNozNo-1] = generateByteStream(wm); // �߰� 2010/06/16(�ﺸ����)
			
			LogUtility.getPumpALogger().info("[Pump A][����-�����㰡]" + " nozzle=" + 
					PB_wm.getNozzleNo() + "("+PB_wm.getCommand()+")" +
					" ODT_No=" + PB_wm.getConnectNozzleNo()+ " | baseNoz=" + baseNozNo + 
					" | fixedSubNozNo=" + fixedSubNozNo + " | liter=" + PB_wm.getLiter() + 
					" | bPrice=" + PB_wm.getBasePrice() + " | price=" + PB_wm.getPrice());
			//PB_wm.print();

			Log.datas(t11_staBuf[baseNozNo-1], t11_staBuf[baseNozNo-1].length, 20);
			
			skip = false;
		}
		else if (wm.getCommand().equals("EX")) { // ������ȯ ����

			EX_WorkingMessage EX_wm = (EX_WorkingMessage) wm;
			
			if (EX_wm.getSubCommand().equals("PB")) {
				m_nozLocks[EX_wm.getSubNozNo()-1] = false;
				m_basePrice = EX_wm.getBasePrice();
			}
	
//			LogUtility.getPumpALogger().debug("EX ���� �����Ͽ����ϴ�. nozNo=" + nozNo+
//					" baseNoz=" + baseNozNo+" wm.subNoz="+EX_wm.getSubNozNo());
			LogUtility.getPumpALogger().debug("EX ���� ����->baseNozNo=" + baseNozNo + " fixedSubNozNo=" + fixedSubNozNo + 
					" nozLocks[0]=" + m_nozLocks[0] + " nozLocks[1]=" + m_nozLocks[1] +
					" nozLocks[2]=" + m_nozLocks[2]);
			
			skip = true;
		}
		else if (wm.getCommand().equals("PA")) { // �������� ��û(�������/����)

			PA_WorkingMessage PA_wm = (PA_WorkingMessage) wm;
			int nNozState = Change.toValue(PA_wm.getNozzleState());

			m_nozLock = (nNozState==0 ? true : false);
			
			if (m_nozLock==true) { // �������
				//m_recvedNozLock=true;
				
				// �߰� 2010.11.12 (������ ���ó��)
				PA_wm.setPassThrough(false);
				insertRecvQueue(PA_wm);
				LogUtility.getPumpALogger().info("PA ���� ODT �۽�. nozzle=" + PA_wm.getNozzleNo());

				makeStatusInfo_direct(656);
				skip = false;
			}
			else { // �������
				makeStatusInfo_direct(657);
				skip = true;
			}

			LogUtility.getPumpALogger().info("PA ���� �����Ͽ����ϴ�. nozzle=" + PA_wm.getNozzleNo() + 
					" mode=" + PA_wm.getNozzleState());
					
		}
		else if (wm.getCommand().equals("P3_1")) { // ������ ȯ�漳��
			/*
			// �ܰ��� t11_ds�� �����Ѵ�.
			P3_1_WorkingMessage P3_wm = (P3_1_WorkingMessage) wm;
			bPrice = P3_wm.getBasePrice().substring(0, 4);	
			t11_ds.editString("basePrice1", bPrice, 4);
			t11_ds.editString("basePrice2", bPrice, 4);
			t11_ds.editString("basePrice3", bPrice, 4);
			t11_ds.editString("basePrice4", bPrice, 4);
			t11_ds.editString("basePrice5", bPrice, 4);
			t11_ds.editString("basePrice6", bPrice, 4);
			t11_Buf = t11_ds.getByteStream();

			LogUtility.getPumpALogger().info("P3 ���� �����Ͽ����ϴ�.");
			LogUtility.getPumpALogger().info("nozzle   =" + P3_wm.getNozzleNo());
			LogUtility.getPumpALogger().info("basePrice=" + P3_wm.getBasePrice());
			
			skip = false;
			*/
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

	public int makeCCITT (int wData, int wCRC) throws Exception {
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
		
		for (int i=0; i<subNozCount; i++) {
		
			SE_wm = new SE_WorkingMessage();
			SE_wm.setNozzleNo(Change.toString("%02d", baseNozNo+i));
			SE_wm.setDeviceType(Change.toString("%02d", nozType));
			SE_wm.setStatus("1");
			SE_wm.setStatusCode("601"); // ȸ���ҷ�
			SE_wm.setErrMsg("������ ȸ���ҷ�");
	
			m_statusCode=601; // appended
			//LogUtility.getPumpALogger().debug("1.m_statusCode="+m_statusCode+" lineCommCheckCnt="+lineCommCheckCnt);
			
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
	}
	
	protected void makeStatusInfo(int nozState) throws Exception {

		int	statusCode=0;
		String errorMsg="";
		
		switch (nozState) {
			case 0:
				statusCode = 651; // ����ٿ�
				errorMsg = "����ٿ�";
				break;
			case 1:
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
		// �߰�(09/01/13)
		if (m_statusCode==statusCode)
			return;
		
		m_statusCode = statusCode;
		
		//LogUtility.getPumpALogger().debug("+++++ �������� ����. nozNo="+nozNo+" subNozNo="+subNozNo);
		
		int cnt=1;
		if (sentChkNozState==false) {
			cnt = subNozCount;
		}
		
		for (int i=0; i<cnt; i++) {

			S8_wm = new S8_WorkingMessage();
			int thisNozNo = baseNozNo + subNozNo - 1;
			
			if (sentChkNozState==false)
				thisNozNo = baseNozNo + i;
			
			S8_wm.setNozzleNo(Change.toString("%02d", thisNozNo));
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

		sentChkNozState=true;
	}
	
	protected void makeStatusInfo_direct(int nozState) throws Exception {

		S8_wm = new S8_WorkingMessage();
		int	statusCode=0;
		String errorMsg="";
		
		switch (nozState) {
			case 656:
				statusCode = 656; // �������(��������)
				errorMsg = "�������";
				break;
			case 657:
				statusCode = 657; // �����������
				errorMsg = "�������";
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

		Calendar cal = new GregorianCalendar();
		
		//############# State check ##############//
		if (RxBuf[3]=='0' && RxBuf[4]=='0') { // ������ �ʱ�ȭ
						
			rcvInitBuf[0] = RxBuf[3];
			rcvInitBuf[1] = RxBuf[4];
			rcvInitBuf[2] = RxBuf[5];
			rcvInitBuf[3] = RxBuf[6];
			rcvInitBuf[4] = RxBuf[7];
			rcvInitBuf[5] = RxBuf[8];

			LogUtility.getPumpALogger().info("####### Completed Initialization : baseNozNo="+baseNozNo+"#######");
			
			byte[] sndInitBufTmp = getInitData (rcvInitBuf);
			byte[] sndInitBuf = new byte[9];
			sndInitBuf[0] = STX;
			sndInitBuf[1] = nozID;
			sndInitBuf[2] = SEL;
			sndInitBuf[3] = sndInitBufTmp[0];
			sndInitBuf[4] = sndInitBufTmp[1];
			sndInitBuf[5] = sndInitBufTmp[2];
			sndInitBuf[6] = sndInitBufTmp[3];
			sndInitBuf[7] = ETX;
			sndInitBuf[8] = getBCC (sndInitBuf);

			TxQue.enQueue(sndInitBuf);
			progressStep=1;
		}
		else if (RxBuf[3]=='6' && RxBuf[4]=='0') { // ������ ��Ȳ����

			progressStep = 2;		
		} 
		else if (RxBuf[3]=='6' && RxBuf[4]=='1') { // ������ Status ����(������/�Ϸ� ����)

			// ������ �ڷ� ����ó���� �߰�(08/10/10)
			waitPumpingData=false;
			waitPumpingData_Cnt=0;
			progressStep = 3;
			
			short state = (short) (RxBuf[5] - 0x30);			
			long currTime = cal.getTimeInMillis();
									
			// ���� (09/01/16)
			if ((nozState>=5 || (nozState==0 && realPumpingStart==true)) && state==1)
				return; // ����
						
			switch (state) {
				
				case 0 :  // ����ٿ�
					if ((currTime - m_nNozUpTime) > 3000) { // 3 sec
						m_nNozDownTime=currTime;
						//LogUtility.getPumpALogger().debug("Nozzle-Down Time=====>>>>" + m_nNozDownTime + " (Noz="+(baseNozNo+subNozNo-1)+")");
						nozState=0;
					} else { // ������
						Sleep.sleep(500);
						TxQue.enQueueNewer(t15_Buf); // ����������û
						nozState=1;
						return;
					}
					break;
				case 1 :  // �����
					if ((currTime - m_nNozDownTime) > 3000) { // 3 sec
						m_nNozUpTime=currTime;
						//LogUtility.getPumpALogger().debug("Nozzle-Up Time=====>>>>" + m_nNozUpTime + " (Noz="+(baseNozNo+subNozNo-1)+")");
						nozState=1;
					} else { // ������
						Sleep.sleep(500);
						TxQue.enQueueNewer(t15_Buf); // ����������û
						nozState=0;
						return;
					}
					break;
					
				case 3 :  // ������
					nozState = 4;
					break;
					
				case 4 :  //�����Ϸ�
					nozState = 5;
					recvedPumpingEnd=true;
					break;
					
				default :
					nozState = state;
					break;
			}
			
			nozState = (pumpingStart==true && nozState==0 && 
					    recvedPumpingEnd==false ? 5 : nozState); //�����Ϸ��ڷ� �̼��� ó�� -> ����(09/01/08)                    
			
			// �پ���6����(����) ��������ȣ ó��(������ ������ subNozNo ���� ����)
			int subNoz = RxBuf[23] - 0x30;
			if (subNoz >= 1) {
				subNozNo = subNoz;
				//subNozNo = (subNoz==5? 3 : subNoz);
				m_recvedSubNozID=true; // 61�� ������ nozzle status �� 1~3 ���Ž� true
			} 
			
			//System.out.printf ("\n===========> subNoz=%s subNozNo=%d m_recvedSubNozID=%s\n", 
					//subNoz, subNozNo, m_recvedSubNozID);			
		}
		else if (RxBuf[3]=='6' && RxBuf[4]=='2') { // �߰� 2010/06/16(�ﺸ����) - �������� �� "�������� Error Message"
			
			LogUtility.getPumpALogger().info("baseNoz=" + baseNozNo + " -> ������ ���ſ��� : subNozNo=" + fixedSubNozNo + 
					" nozLocks[0]=" + m_nozLocks[0] + " nozLocks[1]=" + m_nozLocks[1] + 
					" nozLocks[2]=" + m_nozLocks[2] + " resentPumpEnable=" + resentPumpEnable);
			Log.datas(RxBuf, 40, 20);
			
			//if (m_nozLocks[fixedSubNozNo-1]==false) { // �����㰡 ����
				if (RxBuf[5]=='1' && RxBuf[6]=='1' && resentPumpEnable==false)	{ // �����㰡 ������ �ѹ��� �ǽ�
					resentPumpEnable=true;
					TxQue.enQueueNewer(t11_staBuf[baseNozNo-1]);
					LogUtility.getPumpALogger().info("baseNoz=" + baseNozNo + " -> �����㰡 ������ : subNozNo=" + fixedSubNozNo + 
							" nozLocks[0]=" + m_nozLocks[0] + " nozLocks[1]=" + m_nozLocks[1] + " nozLocks[2]=" + m_nozLocks[2]);
					Log.datas(t11_staBuf[baseNozNo-1], t11_staBuf[baseNozNo-1].length, 20);
				}
			//}
		} 
		else if (RxBuf[3]=='6' && RxBuf[4]=='5') { // ������ ����ġ ����

			switch (progressStep) {
				
				case 3  : 
					if (nozState==1 && m_nozLocks[subNozNo-1]==false) { // ��������¿��� ����				
						SJ_wm = (SJ_WorkingMessage) generateWorkingMessage(RxBuf, "SJ", subNozNo);
						insertRecvQueue(SJ_wm); // �������� �ڷ� �۽�
						sentPumpingStartInfo=true;
						m_nS3_cnt=0;
						return; // �߰�(09/02/02)
					}
					else if (nozState==4) { // ������ ����
						// Skip
					}
					else if (nozState==5 || (nozState==0 && realPumpingStart==true)) { // �����Ϸ��� ����
						progressStep = 4;
						nozState = 5;
					} 
					/*else if (progressStep==3 && (nozState==0 && realPumpingStart==false)) {
						//System.out.printf ("(2-1) : nozNo=%d subNozNo=%d\n", nozNo, subNozNo);	
						insertRecvQueue(generateWorkingMessage(RxBuf, "S5", subNozNo)); // ���а����� �ڷ� �۽�
					}*/
					
					break;
				
				case 4 :
					break; // skip
			}
		}


		//############# STX Process ##############//
		switch (progressStep) {
		case 3 :

			if (nozState == 0) { // ����ٿ�

				pumpingStart=false; // �ʱ�ȭ
				sentPumpingStartInfo=false;

				if (m_nozLocks[subNozNo-1]==false && nozType==1) { // �Ϲ�������
					TxQue.enQueue(t11_Buf); // �����㰡(����/���� ����)
				}
				/*
				// 2010/06/15 ����
				// POS���� ������ ������ ���ó��(�������� 0 �� �����Ϸ� �ڷ�)
				if (m_nozLocks[subNozNo-1]==false && beforeNozzleUp==true && realPumpingStart==false) { 

					TxQue.enQueue(t13_Buf); // �㰡���
					makeStatusInfo(5); // �������� ����(�����Ϸ�)
					
					S4_wm = new S4_WorkingMessage();
					S4_wm.setNozzleNo(Change.toString("%02d", baseNozNo + subNozNo -1));
					S4_wm.setLiter("0000000");
					S4_wm.setBasePrice(m_basePrice);
					S4_wm.setLiter("000000");
					S4_wm.setTotalGauge("0000000000"); // ����(2008/11/05)
					
					insertRecvQueue(S4_wm); // �����Ϸ� �ڷ�

					m_nozLocks[0] = true;
					m_nozLocks[1] = true;
					m_nozLocks[2] = true;
					LogUtility.getPumpALogger().debug("nozzle=" + (baseNozNo + subNozNo -1) + 
							" -> ������ٿ�, ������ ������ҿ� �����Ϸ�����(S4) ����");
				}
				*/
				
				beforeNozzleUp=false;
//				resentPumpEnable=false;
				makeStatusInfo(nozState); // �������� ����

				LogUtility.getPumpALogger().info("nozzle=" + (baseNozNo+subNozNo-1) + " -> ����ٿ� : subNozNo=" + subNozNo + 
						" nozLocks[0]=" + m_nozLocks[0] + " nozLocks[1]=" + m_nozLocks[1] + " nozLocks[2]=" + m_nozLocks[2]);

			}
			else if (nozState == 1) { // ����� & �Ϲ�������	

				flushBuffer(lastPumpingData);
				pumpingStart=true; // Apppend 2008.04.19 (��ź������)
				beforeNozzleUp=true;
				realPumpingStart=false; // �ʱ�ȭ
				recvedPumpingEnd=false;
				
				makeStatusInfo(nozState); // �������� ����
				
				if (sentPumpingStartInfo==false) {
					TxQue.enQueue(t20_Buf); // ���а����� ��û(�������� ���� ���ſ�)
					sentPumpingStartInfo=true;
				}	
				
				LogUtility.getPumpALogger().info("nozzle=" + (baseNozNo+subNozNo-1) + " -> ����� : subNozNo=" + subNozNo + 
						" nozLocks[0]=" + m_nozLocks[0] + " nozLocks[1]=" + m_nozLocks[1] + " nozLocks[2]=" + m_nozLocks[2]);

			}
			else if (nozState == 4) { // ������
					
				lastPumpingData = RxBuf.clone(); // ���� �������� ����
				pumpingStart=true;
				realPumpingStart=true;
				
				if (firstPumpingData==true) {
					makeStatusInfo(nozState); // �������� ����
					firstPumpingData=false;
				}
					
				TxQue.enQueueNewer(t15_Buf);
				waitPumpingData=true; // ������ �ڷ� ����ó���� �߰�(08/10/10)

				S3_wm = (S3_WorkingMessage) generateWorkingMessage(RxBuf, "S3");					
				insertRecvQueue(S3_wm);
			}
			else if (nozState == 5) { // �����Ϸ�
							
				lastPumpingData = RxBuf.clone(); // ���� �������� ����
	
				TxQue.enQueue(t20_Buf); // ���а����� ��û
				TxQue.enQueue(t13_Buf); // �㰡���
				
				waitTotalGuageFor_S4=true;
				progressStep = 4;
				
				if (m_nozLocks[subNozNo-1]==false)
					makeStatusInfo(nozState); // �������� ����(�����Ϸ�)
				
				LogUtility.getPumpALogger().info("nozzle=" + (baseNozNo+subNozNo-1) + " -> �����Ϸ�, ���а����� ��û : subNozNo=" + subNozNo + 
						" nozLocks[0]=" + m_nozLocks[0] + " nozLocks[1]=" + m_nozLocks[1] + " nozLocks[2]=" + m_nozLocks[2]);
			}
			break;

		case 4 :
			if (nozState == 5) { // �����Ϸ��� ���а����� ����
				
				byte[][] tBuf = new byte [2][80];
				tBuf[0] = lastPumpingData.clone(); // ���� ��������
				flushBuffer(lastPumpingData);
				tBuf[1] = RxBuf.clone(); // Total guage

				progressStep = 3;
				nozState = 0;

				TxQue.enQueue(t13_Buf); // �㰡���

				if (m_nozLocks[subNozNo-1]==false) {
					
					if (m_recvedSubNozID==true && tBuf[0][23] > 0) { // �߰�(09/01/08) - tBuf[0][23]=subNoz number
						S4_wm = (S4_WorkingMessage) generateWorkingMessage(tBuf, "S4", subNozNo);

						int nPrice=0, nLiter=0; 
						if (S4_wm != null) {
							nPrice = Change.toValue(S4_wm.getPrice());
							nLiter = Change.toValue(S4_wm.getLiter());
						}
						
						if (realPumpingStart==true) {
							if (nPrice>0 && nLiter>0)
								insertRecvQueue(S4_wm); // �����Ϸ� �ڷ�
						} 
						else if (realPumpingStart==false) {
							S4_wm.setLiter("0000000");
							S4_wm.setPrice("00000000");
							insertRecvQueue(S4_wm); // �����Ϸ� �ڷ�
						}
						
						LogUtility.getPumpALogger().info("nozzle=" + (baseNozNo+subNozNo-1) + " -> �����Ϸ�, ���а����� ����. �Ϸ�����(S4) ���� ��ƾ: m_nozLocks=" +m_nozLocks[subNozNo-1]);
						LogUtility.getPumpALogger().info("nozzle=" + (baseNozNo+subNozNo-1) + " -> realPumpingStart="+realPumpingStart+" nPrice="+nPrice+" nLiter="+nLiter);
					}
					
					m_recvedSubNozID=false;
				}
				else {
					LogUtility.getPumpALogger().info("nozzle=" + (baseNozNo+subNozNo-1) + " -> �����Ϸ�, ���а����� ����. �Ϸ�����(S4) ���۾��� : m_nozLocks=" + m_nozLocks[subNozNo-1]);
				}
				
				makeStatusInfo(nozState); // �������� ����(����ٿ�)

				//LogUtility.getPumpALogger().debug("####>>>> ����ٿ�. m_nozLocks= " + m_nozLocks[subNozNo-1] +
						//" beforeNozzleUp="+beforeNozzleUp+" realPumpingStart="+realPumpingStart);
						
				// ����(09/01/13)
				pumpingStart=false; 
				sentPumpingStartInfo=false;
				firstPumpingData=true;

				realPumpingStart=false;
				waitTotalGuageFor_S4=false;
				waitGaugeForS4_Cnt=0;
				sendPumpingData_Cnt=0;
				m_nS3_cnt=0;
				resentPumpEnable=false;
				
				m_nozLocks[subNozNo-1] = true; // ����(09/02/02)
			}
			break;
		}
	}
	
	protected boolean recvTail_proc () throws Exception, SerialConnectException {
		
		int numBytes;

		//--- send : ACK ---//
		if (sendText (ACK1)==false) {
			if (dispLevel>=3)
				LogUtility.getPumpALogger().info("5.Send ACK1 fail! (Noz="+nozNo+")");
			return false;
		}
		
		if (dispLevel>=3)
			LogUtility.getPumpALogger().info("5.Send ACK1 (Noz="+nozNo+")");
		
		//--- recv : EOT ---//
		numBytes = recvText(RxBuf);
		if (numBytes < 1) {
			if (dispLevel>=3)
				LogUtility.getPumpALogger().info("5.Recv EOT fail (Noz="+nozNo+")");
			return false;
		}
	
		if (RxBuf[0] == EOT) {	//--- recv : EOT ---//
			if (dispLevel>=3)
				LogUtility.getPumpALogger().info("5.Recv EOT (Noz="+nozNo+")");
			return true;
		}
		else {
			if (dispLevel>=3)
				LogUtility.getPumpALogger().info("5.Recv EOT fail (Noz="+nozNo+")");
			return false;
		}
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
					if (RxBuf[i]==EOT || RxBuf[i]==ACK || RxBuf[i]==NAK) {  // recv EOT/ACK/NAK
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
		
		// �پ���6����(����) ��������ȣ ó��
		if (baseNozNo==0 && tatsunoMPPNozVec.size() > 0) {
			setSubNozzleNo();
		}
		
		// ȸ���ҷ� ���� ó��
		if (firstRequest==true) {
			firstRequest=false;
			//LogUtility.getPumpALogger().debug("++++++ �������� ��û(t15) : nozNo="+nozNo+" subNozNo="+subNozNo);
			TxQue.enQueue(t15_Buf); // ����������û
		}
		if (m_statusCode==601) { // ȸ���ҷ�
			if (lineCommCheckCnt >= (minErrCnt+5)) {
				lineCommCheckCnt=0;
				sentChkNozState=false;
				TxQue.enQueueNewer(t15_Buf); // ����������û
			}
			lineCommCheckCnt++;
		}
		//LogUtility.getPumpALogger().debug("2.m_statusCode="+m_statusCode+" lineCommCheckCnt="+lineCommCheckCnt);
		
		// ������ �ڷ�(S3) �̼��� ó��
		if (nozState==4 && waitPumpingData==true) {
			//LogUtility.getPumpALogger().debug("waitPumpingData_Cnt=" + waitPumpingData_Cnt);
			if (waitPumpingData_Cnt >= MAX_WAIT_PDATA) {
				TxQue.enQueueNewer(t15_Buf);				
				
				LogUtility.getPumpALogger().info("nozzle=" + (baseNozNo+subNozNo-1) + " -> ������ �ڷ�(61)�� �̼���, �����ڷ� ��û(15)" +
						" waitPumpingData_Cnt="+waitPumpingData_Cnt);
				waitPumpingData_Cnt=0;
			}
			else
				waitPumpingData_Cnt++;
		}

		// �����Ϸ�(S4) ���а����� �̼��� ó��(2008/08/13)
		if (waitTotalGuageFor_S4==true) {
			if (waitGaugeForS4_Cnt > MAX_PROG4STEP) {	
				try {
					// �߰�-������Ƽ(09/01/12)
					progressStep = 4;
					nozState = 5;

					processRecvSTX();
					
					LogUtility.getPumpALogger().info("nozzle=" + (baseNozNo+subNozNo-1) + " -> �����Ϸ�, ���а�����(65) �̼���, processSTX() ȣ��.");
				} catch (Exception e) {
					LogUtility.getPumpALogger().error(e.getMessage(), e);
				}
			}
			else
				waitGaugeForS4_Cnt++;
			
			LogUtility.getPumpALogger().debug("waitGaugeForS4_Cnt="+waitGaugeForS4_Cnt);
		}
		
		while (sndQue.getItemCount() > 0) {
			
			WorkingMessage wm = (WorkingMessage)sndQue.deQueue();

			LogUtility.getPumpALogger().debug("\n>>>>> Received  Down data : " +
								"nozNo=" + (baseNozNo+subNozNo-1) + " command=" + wm.getCommand());
			
			if (isSkipWorkingMessage(wm)==true) {
				wm=null;
				continue;
			}
			
			byte[] wmByte = generateByteStream(wm);
			if (wmByte != null) 
				TxQue.enQueue(wmByte);
			/*
			if (wm.getCommand().equals("PB")) {
				LogUtility.getPumpALogger().debug(":::::::::>>> ByteStrems of workingMessage ["+ wm.getCommand() + "] in requestData().");
				Log.datas(wmByte, wmByte.length, 20);
			}
			*/
			wm=null;
		}
		
		if (dispLevel>=3) 
			LogUtility.getPumpALogger().debug("\nStart request ========> TatsunoN_MPP (Noz="+nozNo+")");

		mode = POL;
		while (true) {

			flushBuffer(RxBuf);
			
			if (loopCnt >= 2) break;
			loopCnt++;
			
			try {
				//##### Send ENQ #####//
				mode = (mode==POL? SEL : POL);
				enq_Buf[1] = nozID;
				enq_Buf[2] = mode;
				
				if (mode==SEL) {
					if (progressStep != 1 && TxQue.isEmpty()==true)
						continue;
				}
				if (mode==POL) { // �پ���6����(������) ��������ȣ ó����-Base Nozzle�� ������ ������
					if (nozNo != baseNozNo)
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
					LogUtility.getPumpALogger().info("0.Send ENQ (Noz="+(baseNozNo+subNozNo-1)+")");
					Log.datas(enq_Buf, 4, 20);
				}

				//--- recv data---//				
				if (recvText(RxBuf) < 1) {
					lineErrCnt++;
					if (dispLevel>=2)
						LogUtility.getPumpALogger().info("0.Recv ACK/STX fail!(No response) lineErrCnt="+lineErrCnt+"/"+minErrCnt+" (Noz="+(baseNozNo+subNozNo-1)+")");
					else if (lineErrCnt >= minErrCnt) {
						if (m_nNoResponseCnt%5==0) {
							m_nNoResponseCnt=0;
							LogUtility.getPumpALogger().debug("0.Recv ACK/STX fail!(No response) lineErrCnt="+lineErrCnt+"/"+minErrCnt+" (Noz="+(baseNozNo+subNozNo-1)+")");
						}
						m_nNoResponseCnt++;
					}
					continue;
				}

				//###### Recv data(Polling mode) : STX ######//
				if (mode==POL) { // 0x51

					if (RxBuf[0] == STX) {

						if (dispLevel>=3) {
							LogUtility.getPumpALogger().info("1.Recv STX(Data) (Noz="+(baseNozNo+subNozNo-1)+")");
							Log.datas(RxBuf, 80, 20);
						}
						
						if (compareBCC(RxBuf)==false) {
							if (dispLevel>=0)
								LogUtility.getPumpALogger().info ("1.Recv STX BCC fail! (Noz="+(baseNozNo+subNozNo-1)+")");
							sendText (NAK);
							continue;
						}
						else {
							if (dispLevel==1 || dispLevel==2) {
								LogUtility.getPumpALogger().info ("1.Recv STX with normal (Noz="+(baseNozNo+subNozNo-1)+")");
								Log.datas(RxBuf, 80, 20);
							}
							/*
							if (RxBuf[3]=='6' && (RxBuf[4]=='1' || RxBuf[4]=='5')) {
								int subNoz=1;
								if (RxBuf[4]=='1' && (RxBuf[23]-0x30) >= 1) 
									subNoz = RxBuf[23] - 0x30;
								else
									subNoz = subNozNo;

								if (m_isSaveSTX==true) 
									LogUtility.getPumpALogger().info("Recv STX("+Change.toString("%02d", baseNozNo+subNoz-1)+
											") : ["+pack(RxBuf, ETX)+"]");
								else
									LogUtility.getPumpALogger().debug("Recv STX("+Change.toString("%02d", baseNozNo+subNoz-1)+
											") : ["+pack(RxBuf, ETX)+"]");
							}
							*/
							if (RxBuf[3]=='6' && (RxBuf[4]=='1' || RxBuf[4]=='5')) {
								int subNoz=1;
								if (RxBuf[4]=='1' && (RxBuf[23]-0x30) >= 1) 
									subNoz = RxBuf[23] - 0x30;
								else
									subNoz = subNozNo;

								if (RxBuf[4]=='5' || (RxBuf[4]=='1' && m_isLogPumpingSTX==true)) {
									
									if (m_isSaveSTX==true) 
										LogUtility.getPumpALogger().info("Recv STX("+Change.toString("%02d", baseNozNo+subNoz-1)+
												") : ["+pack(RxBuf, ETX)+"]");
									else
										LogUtility.getPumpALogger().debug("Recv STX("+Change.toString("%02d", baseNozNo+subNoz-1)+
												") : ["+pack(RxBuf, ETX)+"]");
									
									if (RxBuf[4]=='1') m_isLogPumpingSTX=false;
								}
								
							}
							
							//---���� ���� ������ ó��---//
							processRecvSTX(); 
							
							if (recvTail_proc()==false) 
								lineErrCnt++;
							else
								lineErrCnt=0; // Normal terminated
						}
					}
					else if (RxBuf[0] == EOT) {
						if (dispLevel>=3)
							LogUtility.getPumpALogger().info("1.Recv EOT (Noz="+(baseNozNo+subNozNo-1)+")");
						lineErrCnt=0; // Normal terminated
						continue;
						
					} else {
						if (dispLevel>=3)
							LogUtility.getPumpALogger().info("1.Recv fail! (Noz="+(baseNozNo+subNozNo-1)+")");
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
								LogUtility.getPumpALogger().info("2.Recv ACK0 (Noz="+(baseNozNo+subNozNo-1)+")");
		
							if (TxQue.isEmpty()==false) { // �۽� ������ ������
	
								//###### Send data : send working-data ######//
								TxBuf = TxQue.getFirstItem();
								TxBuf[1] = nozID;
								TxBuf[2] = mode;
								setBCC (TxBuf); // write BCC
								if(sendText(TxBuf) != true) {
									if (dispLevel>=3) {
										LogUtility.getPumpALogger().info("2.Send STX fail! (Noz="+(baseNozNo+subNozNo-1)+")");
										Log.datas(TxBuf, TxBuf.length, 20);
									}
									lineErrCnt++;
									continue;
								}

								if (dispLevel>=3) {
									LogUtility.getPumpALogger().info("2.Send STX(TxBuf) (Noz="+(baseNozNo+subNozNo-1)+")");
									Log.datas(TxBuf, TxBuf.length, 20);
								}
																
								numBytes = recvText(RxBuf);
								if (numBytes < 1) {
									if (dispLevel>=3)
										LogUtility.getPumpALogger().info("2.Recv ACK1 fail! (Noz="+(baseNozNo+subNozNo-1)+")");
									lineErrCnt++;
									continue;
								}

								if (RxBuf[0]==ACK) {
									is.read (RxBuf,0,1);
									if (RxBuf[0]==0x31) { // recv : ACK1
										
										if (!(TxBuf[3]=='1' && TxBuf[4]=='5') || 
												(TxBuf[3]=='1' && TxBuf[4]=='5' && m_nS3_cnt%5==0)) {
											
											if (m_isSaveSTX==true) 
												LogUtility.getPumpALogger().info("Send STX("+Change.toString("%02d", baseNozNo+subNozNo-1)+
														") : ["+new String(TxBuf)+"]");
											else
												LogUtility.getPumpALogger().debug("Send STX("+Change.toString("%02d", baseNozNo+subNozNo-1)+
														") : ["+new String(TxBuf)+"]");
											
											if (TxBuf[3]=='1' && TxBuf[4]=='5') {
												m_nS3_cnt=0;
												m_isLogPumpingSTX=true;
											}
										}
										
										if (TxBuf[3]=='1' && TxBuf[4]=='5') m_nS3_cnt++;
										
										TxQue.deQueue(); //--- remove item ---//
										
										/*
										// ������ ���ſ��� ����(62) ó�� �׽�Ʈ��
										if (TxBuf[3]=='1' && TxBuf[4]=='1') {
											RxBuf[3] = '6';	RxBuf[4] = '2';
											RxBuf[5] = '1';	RxBuf[6] = '1'; RxBuf[7] = '0';
											processRecvSTX();
										}*/				
										
										if (dispLevel>=3)
											LogUtility.getPumpALogger().info("2.Recv ACK1 (Noz="+(baseNozNo+subNozNo-1)+")");
										
										if (dispLevel==2) {
											LogUtility.getPumpALogger().info("2.Send STX with normal (Noz="+(baseNozNo+subNozNo-1)+")");
											Log.datas(TxBuf, TxBuf.length, 20);
										}
										lineErrCnt=0; // Normal terminated
									}
								}
								else if (dispLevel>=3) {
									LogUtility.getPumpALogger().info("2.Recv ACK1 fail! (Noz="+(baseNozNo+subNozNo-1)+")");
									lineErrCnt++;
								}
							}								
						}
					}
					else if (RxBuf[0] == EOT) {
						if (dispLevel>=3) {
							LogUtility.getPumpALogger().info("2.Recv EOT (Noz="+(baseNozNo+subNozNo-1)+")");
							Log.datas(RxBuf, 26, 20);
						}
						lineErrCnt=0; // Normal terminated
						continue;
					} else {
						if (dispLevel>=3)
							LogUtility.getPumpALogger().info("2.Recv fail! (Noz="+(baseNozNo+subNozNo-1)+")");
						lineErrCnt++;
						continue;
					}
				} 
			} catch (Exception e) {
				LogUtility.getPumpALogger().error("Exception occurr! (Noz="+(baseNozNo+subNozNo-1)+")");
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

	public boolean sendText(byte buf) throws Exception, SerialConnectException {

		Sleep.sleep(writeStartInterval);
		
	    try {
			os.write(buf);
		} catch (IOException e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	
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

	protected void setSubNozzleNo () throws Exception { // �پ��� 6����(����)�� ��������ȣ ó����
	
		String baseNozStr=null;
			
		if (tatsunoMPPNozVec.size() > 0) {
			Enumeration<TatsunoMPPNoz> odtEnum = tatsunoMPPNozVec.elements();		
			
			while (odtEnum.hasMoreElements()) {				
				TatsunoMPPNoz mppOdt = odtEnum.nextElement();	

				if (Change.toValue(mppOdt.getODTNo()) == connectDeviceNo) {		
					Vector nozVec = mppOdt.getConnectNozNoVec();
					subNozCount = nozVec.size();
											
					if (subNozCount > 0)
						baseNozStr = (String) nozVec.get(0);
				}
			}
			if (baseNozStr != null) {
				baseNozNo = Change.toValue(baseNozStr);
				subNozNo = nozNo - baseNozNo + 1;
				fixedSubNozNo = subNozNo;
			}
		}

		nozID = (byte) (baseNozNo - 1 + 0x40);
				
		//System.out.printf("@@@@@Tatsuno MPP(2) nozNo=%d baseNozNo=%d subNozNo=%d nozId=%c\n",
				//nozNo, baseNozNo, subNozNo, nozID);
    }
}
