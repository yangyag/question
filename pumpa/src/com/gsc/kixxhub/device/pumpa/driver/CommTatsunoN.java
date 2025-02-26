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

public class CommTatsunoN extends CCommDriver implements SerialPortEventListener, CommPortOwnershipListener, 
	Runnable {

	protected static boolean m_isSaveSTX=false;
	protected static int	MAX_WAIT_PDATA=5;
	protected byte  ACK  = 0x10;
	protected byte[] ACK0 = new byte[2];
	protected byte[] ACK1 = new byte[2];
	protected int		baseMinErrCnt=8, baseMaxSkipCnt=100;
	protected int		baseReadBuffInterval   = 30+6; // ����Ʈ������ PJT ����
	protected int		baseReadStartInterval  = 15+4; // ����Ʈ������ PJT ����

	protected int		baseWriteStartInterval = 15+4; // ����Ʈ������ PJT ����
	
    protected boolean beforeNozzleUp=false; // ���� ���������
    protected int		buffSize = 500;
    protected int	dispLevel=0;
    protected byte  ENQ  = 0x05;
    protected byte[] 	enq_Buf = new byte[4];
    protected byte  EOT  = 0x04;
    protected byte  ETX  = 0x03;
    protected boolean firstPumpingData=true;
    protected boolean firstRequest=true;
    protected HF_WorkingMessage HF_wm;
    protected boolean	issueLineErr=true;

	protected byte[] 	lastPumpingData=new byte[buffSize];
	protected int		lineCommCheckCnt=0;
    protected int		lineErrCnt=0;
	protected String	m_basePrice="000000";
	
	protected long		m_nNozDownTime=0;
	//protected String	last_SJ_TotalGauge="0000000000"; 
	protected long		m_nNozUpTime=0;
    protected boolean m_nozLock=false;
	protected boolean m_recvedNozLock=false;
	protected int		m_statusCode=601;
    protected byte  SEL  = 0x41;
	protected byte  mode = SEL;
	protected byte  NAK  = 0x15;
	protected byte		nozID;
	protected short		nozState=0;
	protected String 	nozStr = "";
	protected byte  POL  = 0x51;
	protected byte  PREFIX_ENQ = 0x05;
	//progressStep : 0=�ʱ�ȭ��, 1=�ʱ�ȭ��, 2=���������۽���, 3=������, 4=�����Ϸ�, 
    //               5=���а���������/�����Ϸ��ڷ� �۽�
    protected int 	progressStep=0;
	protected boolean pumpingStart=false;
	protected byte[] 	rcvInitBuf = new byte[6];
	protected boolean realPumpingStart=false; // ��������
	protected byte[] 	RxBuf = new byte[buffSize];
	protected S3_WorkingMessage S3_wm;

    protected S4_WorkingMessage S4_wm;
	
	protected S5_WorkingMessage S5_wm;
	protected S8_WorkingMessage S8_wm;
    
    protected SE_WorkingMessage SE_wm;

    protected boolean sentPumpingStartInfo=false;
    protected SJ_WorkingMessage SJ_wm;
    protected byte  STX  = 0x02;
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
    protected boolean waitPumpingData=false; // �����ڷ� ���ſ���
    protected int		waitPumpingData_Cnt=0;
    protected boolean waitTotalGuageFor_S4=false; // �����Ϸ� ������ ���Ŵ��
    protected boolean waitTotalGuageFor_SJ=false; // �������� ������ ���Ŵ��
    
    public CommTatsunoN (int nozNum, String romVerStr) {
    	
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
		
	    ACK0[0] = ACK;
	    ACK0[1] = 0x30;
	    ACK1[0] = ACK;
	    ACK1[1] = 0x31;
		
		//--- prefix-ENQ ---//
		enq_Buf[0] = EOT;
		enq_Buf[1] = nozID;
		enq_Buf[2] = SEL;
		enq_Buf[3] = ENQ;
		
		readBuffInterval   	= baseReadBuffInterval;
		readStartInterval  	= baseReadStartInterval;
		writeStartInterval 	= baseWriteStartInterval;
		minErrCnt 		   	  	= baseMinErrCnt;
		maxSkipCnt 		   	= baseMaxSkipCnt;
		
		try {
        	flushBuffer(lastPumpingData);
        	
			//--- pumping grant(Non MPP) ---//
			t10_ds.addByte  ("STX", STX);
			t10_ds.addByte  ("SA", nozID);
			t10_ds.addByte  ("UA", SEL);
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
			t11_ds.addByte  ("UA", SEL);
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
				
		if (wm.getCommand().equals("PB")) { // �������� ����
			
			// �ܰ��� t10_ds�� �����Ѵ�.
			PB_WorkingMessage PB_wm = (PB_WorkingMessage) wm;
			bPrice = PB_wm.getBasePrice().substring(0, 4);	
			t10_ds.editString("basePrice", bPrice, 4);
			t10_Buf = t10_ds.getByteStream();

			m_basePrice = PB_wm.getBasePrice();

			LogUtility.getPumpALogger().info("[Pump A][����-�����㰡]" + " nozzle=" + 
					PB_wm.getNozzleNo() + "("+PB_wm.getCommand()+")" +
					" liter =" + PB_wm.getLiter() +
					" | bPrice=" + PB_wm.getBasePrice() +
					" | price =" + PB_wm.getPrice());
			
			skip = false;
		}
		else if (wm.getCommand().equals("P3_1")) { // ������ ȯ�漳��
			
			// �ܰ��� t10_ds�� �����Ѵ�.
			P3_1_WorkingMessage P3_wm = (P3_1_WorkingMessage) wm;
			bPrice = P3_wm.getBasePrice().substring(0, 4);	
			t10_ds.editString("basePrice", bPrice, 4);
			t10_Buf = t10_ds.getByteStream();

			m_basePrice = P3_wm.getBasePrice();
			
			skip = false;
		}
		else if (wm.getCommand().equals("PA")) { // �������� ��û

			PA_WorkingMessage PA_wm = (PA_WorkingMessage) wm;
			int nNozState = Change.toValue(PA_wm.getNozzleState());
			
			m_nozLock = (nNozState==0 ? true : false);

			if (m_nozLock==true) {
				m_recvedNozLock=true;
				//TxQue.enQueue(t15_Buf); // ����������û
				makeStatusInfo(656);
			}
			else {
				TxQue.enQueue(t10_Buf); // �����㰡(����/���� ����)
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

			if (lineErrSkipCnt >= 9005)
				m_isSaveSTX = true;
			
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
		SE_wm.setStatusCode("601"); // ȸ���ҷ�
		SE_wm.setErrMsg("������ ȸ���ҷ�");

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

			//LogUtility.getPumpALogger().debug ("Recv InitData =");
			//Show.datas(rcvInitBuf, rcvInitBuf.length, 20);
			LogUtility.getPumpALogger().info("####### Completed Initialization : nozNo="+nozNo+"#######");
			
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
		else if (RxBuf[3]=='6' && RxBuf[4]=='1') { // ������ ��������

			// ������ �ڷ� ����ó���� �߰�(08/10/10)
			waitPumpingData=false;
			waitPumpingData_Cnt=0;
			
			progressStep = 3;
			
			short state = (short) (RxBuf[5] - 0x30);
			
			if (romVer.equals("152") || romVer.equals("153")) { // 152=���� �پ������1(�ȵ�+�㰡����) (09/02/02)
																									// 153=��ǳ �پ������2(�ȵ�) (09/10/07)
				long currTime = cal.getTimeInMillis();
							
				if (nozState>=5 && state==1) {
					LogUtility.getPumpALogger().debug("nozzle="+nozNo+" -> Nozzle Up ������. nozState="+nozState+" state="+state);
					return; // ����
				}
							
				switch (state) {
					case 0 :  // ����ٿ�
						if ((currTime - m_nNozUpTime) > 2000) { // 2 sec
							m_nNozDownTime=currTime;
							//LogUtility.getPumpALogger().debug("nozzle="+nozNo+" -> Nozzle Down���� ������. currTime=" + currTime + " nozUpTime="+m_nNozUpTime);
							nozState=0;
						} else { // ������
							Sleep.sleep(500);
							TxQue.enQueueNewer(t15_Buf); // ����������û
							nozState=1;
							LogUtility.getPumpALogger().debug("nozzle="+nozNo+" -> Nozzle Down���� ��������. currTime=" + currTime + " nozUpTime="+m_nNozUpTime);
							return;
						}
						break;
					case 1 :  // �����
						if ((currTime - m_nNozDownTime) > 2000) { // 2 sec
							m_nNozUpTime=currTime;
							//LogUtility.getPumpALogger().debug("nozzle="+nozNo+" -> Nozzle Up���� ������. currTime=" + currTime + " nozDownTime="+m_nNozDownTime);
							nozState=1;
						} else { // ������
							Sleep.sleep(500);
							TxQue.enQueueNewer(t15_Buf); // ����������û
							LogUtility.getPumpALogger().debug("nozzle="+nozNo+" -> Nozzle Up���� ��������. currTime=" + currTime + " nozDownTime="+m_nNozDownTime);
							nozState=0;
							return;
						}
						break;
					case 3 :  // ������
						nozState = 4;
						break;
					case 4 :  //�����Ϸ�
						nozState = 5;
						break;
					default :
						nozState = state;
						break;
				}
			}
			else { // ǥ�ع���
				
				switch (state) {
					case 3 :  // ������
						nozState = 4;
						break;
					case 4 :  //�����Ϸ�
						nozState = 5;
						break;
					default :
						nozState = state;
						break;
				}
			}
			
			pumpingStart = (nozState==4 ? true : pumpingStart); // ��������
			nozState = (pumpingStart==true && nozState==0 ? 5 : nozState); //�����Ϸ��ڷ� �̼��� ó��
			
			//System.out.printf ("1.====> nozState=%s progState=%d\n", nozState, progressStep);
		}
		else if (RxBuf[3]=='6' && RxBuf[4]=='5') { // ������ ����ġ ����

			if (nozState==1) { // ��������¿��� ����
				
				SJ_wm = (SJ_WorkingMessage) generateWorkingMessage(RxBuf, "SJ"); 
				//last_SJ_TotalGauge = SJ_wm.getTotalGauge();
				insertRecvQueue(SJ_wm); // �������� �ڷ� �۽�
				
				sentPumpingStartInfo=true;
				waitTotalGuageFor_SJ=false;
				waitGaugeForSJ_Cnt = 0;
				
				return; // ���� �߰� (09/09/02)
			}
			else if (progressStep==3 && nozState==4) { // ������ ����
				// Skip
			}
			else if (progressStep==3 && nozState==0) { // �����Ϸ��� ����
				progressStep = 4;
				nozState = 5;
			} 
			else 
				insertRecvQueue(generateWorkingMessage(RxBuf, "S5")); // ���а����� �ڷ� �۽�
			
		}
		else 
			return; // ������ũ �߰� (09/08/25)

		
		//############# STX Process ##############//
		switch (progressStep) {
		case 3 :

			if (nozState==0) { // ����ٿ�

				pumpingStart=false;
				sentPumpingStartInfo=false;
				waitTotalGuageFor_SJ=false; // �߰�->��������(SJ) �ݺ����� �����ذ�(08/08/11)
				
				makeStatusInfo(nozState); // �������� ����
				
				// ���ι�ȭ(ǥ�ع���) ������ ��ȸ�Ұ��� ����->ǥ�ع����� �㰡����(09/09/16)
				// ����(�پ������1) POS�������� ������������ ����(09/09/23)
				if (romVer.equals("152") && m_nozLock==false && nozType==1) { // �Ϲ�������
					if (Change.toValue(m_basePrice) > 0) {
						TxQue.enQueue(t10_Buf); // �����㰡(�������)
						LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> ����ٿ�, �����㰡(e0) : progStep=" + progressStep + 
								" nozState=" + nozState + " m_basePrice=" + m_basePrice);
					}
				}

				// �������/���� �������� ó��
				if (m_nozLock==true)
					makeStatusInfo(656); // �������
				else if (m_recvedNozLock==true) {
					makeStatusInfo(657); // �������
					m_recvedNozLock=false;
				}
				
				// POS���� ������ ������ ���ó��(�������� 0 �� �����Ϸ� �ڷ�)
				if (beforeNozzleUp==true && realPumpingStart==false) { // ���� �����Ϸ��ڷᰡ �ƴϸ�

					makeStatusInfo(5); // �������� ����
					
					S4_wm = new S4_WorkingMessage();
					S4_wm.setNozzleNo(Change.toString("%02d",nozNo));
					S4_wm.setLiter("0000000");
					S4_wm.setBasePrice(m_basePrice);
					S4_wm.setPrice("00000000");
					//S4_wm.setTotalGauge(last_SJ_TotalGauge);
					S4_wm.setTotalGauge("0000000000"); // ����(2008/11/05)
					insertRecvQueue(S4_wm); // �����Ϸ� �ڷ�
				}
				beforeNozzleUp=false;
				
			}
			else if (nozState==1) { // ����� & �Ϲ�������

				makeStatusInfo(nozState); // �������� ����

				beforeNozzleUp=true;
				realPumpingStart=false;
				
				if (romVer.equals("152")==false) { // ����(�پ������1) ������� ���������� ������ �㰡����(09/09/07)
					if (m_nozLock==false && nozType==1) { // �Ϲ�������
						if (Change.toValue(m_basePrice) > 0) {
							TxQue.enQueue(t10_Buf); // �����㰡(�������)
							LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> �����, �����㰡(e0) : progStep=" + progressStep + 
									" nozState=" + nozState + " m_basePrice=" + m_basePrice);

						}
					}
				}
				
				if (sentPumpingStartInfo==false) {
					TxQue.enQueue(t20_Buf); // ���а����� ��û(�������� ���� ���ſ�)
					
					sentPumpingStartInfo=true;
					waitTotalGuageFor_SJ=true;
				}

			}
			else if (nozState == 4) { // ������
				
				// ����(2008/09/05)
				S3_wm = (S3_WorkingMessage) generateWorkingMessage(RxBuf, "S3");
				if (S3_wm != null) {
					int nPrice = Change.toValue(S3_wm.getPrice());
					int nLiter = Change.toValue(S3_wm.getLiter());

					if (nPrice > 0 && nLiter > 0) {			
						lastPumpingData = RxBuf.clone(); // ���� �������� ����
						realPumpingStart=true;
	
						if (firstPumpingData==true) {
							makeStatusInfo(nozState); // �������� ����
							firstPumpingData=false;
						}
					
						insertRecvQueue(S3_wm);
					}
				}

				TxQue.enQueue(t15_Buf); // ������ Status(�����ڷ�) ��û
				waitPumpingData=true; // ������ �ڷ� ����ó���� �߰�(08/10/10)
			}
			else if (nozState == 5) { // �����Ϸ�
				
				// ����(2008/09/05)
				S3_wm = (S3_WorkingMessage) generateWorkingMessage(RxBuf, "S3");
				if (S3_wm != null) {
					int nPrice = Change.toValue(S3_wm.getPrice());
					int nLiter = Change.toValue(S3_wm.getLiter());
					if (nPrice > 0 || nLiter > 0) // ����(08/11/24) : && -> ||				
						lastPumpingData = RxBuf.clone(); // ���� �������� ����
				}
				
				TxQue.enQueue(t20_Buf); // ���а����� ��û
				waitTotalGuageFor_S4=true;

				progressStep = 4;
				pumpingStart=false;
				sentPumpingStartInfo=false;
				firstPumpingData=true;
				makeStatusInfo(nozState); // �������� ����
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
				
				S4_wm = (S4_WorkingMessage) generateWorkingMessage(tBuf, "S4"); 
				S4_wm.setNozzleNo(Change.toString("%02d",nozNo));
				insertRecvQueue(S4_wm); // �����Ϸ� �ڷ�
				
				makeStatusInfo(nozState); // �������� ����
				
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
				LogUtility.getPumpALogger().info("5.Send ACK1 fail! (Noz="+nozNo+")");
			return false;
		}
		
		if (dispLevel>=3)
			LogUtility.getPumpALogger().info("5.Send ACK1 \n");
		
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
			LogUtility.getPumpALogger().error(e.getMessage(), e);
			return 0;
		}
	}
	
	@Override
	public void requestData() throws Exception, SerialConnectException {

		int 	loopCnt=0;
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
		
		// ȸ���ҷ� ������ ó��
		if (firstRequest==true) {
			firstRequest=false;
			TxQue.enQueue(t15_Buf); // ����������û
		}
		if (m_statusCode==601) { // ȸ���ҷ�
			if (lineCommCheckCnt%(minErrCnt+5)==0) {
				lineCommCheckCnt=0;
				TxQue.enQueueNewer(t15_Buf); // ����������û
			}
			lineCommCheckCnt++;
		}
		
		// ��������(SJ) ���а����� �̼��� ó��
		if (waitTotalGuageFor_SJ==true) {
			if (waitGaugeForSJ_Cnt > 15) {
				SJ_wm = new SJ_WorkingMessage();
				SJ_wm.setNozzleNo(Change.toString("%02d",nozNo));
				//SJ_wm.setTotalGauge(last_S4_TotalGauge);
				SJ_wm.setTotalGauge("0000000000");
				insertRecvQueue(SJ_wm); // �������� �ڷ� �۽�

				waitGaugeForSJ_Cnt=0;
				sentPumpingStartInfo=true;
				waitTotalGuageFor_SJ=false; // ����(08/08/11)

				LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> �������� ���а�����(t0) �̼���, ��������(SJ)���� ����");
			}
			else
				waitGaugeForSJ_Cnt++;
			
			LogUtility.getPumpALogger().debug("waitGaugeForSJ_Cnt="+waitGaugeForSJ_Cnt);
		}
		
		// ������ �ڷ�(S3) �̼��� ó��
		if (nozState==4 && waitPumpingData==true) {
			//LogUtility.getPumpALogger().debug("waitPumpingData_Cnt=" + waitPumpingData_Cnt);
			if (waitPumpingData_Cnt >= MAX_WAIT_PDATA) {
				TxQue.enQueueNewer(t15_Buf);				
				
				LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> ������ �ڷ�(S3)�� �̼���, �����ڷ� ��û(p0), waitPumpingData_Cnt="+waitPumpingData_Cnt);
				waitPumpingData_Cnt=0;
			}
			else
				waitPumpingData_Cnt++;
		}

		// �����Ϸ�(S4) ���а����� �̼��� ó��(2008/08/13)
		if (waitTotalGuageFor_S4==true) {
			if (waitGaugeForS4_Cnt > 15) {				

				byte[][] tBuf = new byte [2][80];
				tBuf[0] = lastPumpingData.clone(); // ���� ��������
				flushBuffer(lastPumpingData);
				tBuf[1] = RxBuf.clone(); // Total guage

				S4_wm = (S4_WorkingMessage) generateWorkingMessage(tBuf, "S4"); 
				S4_wm.setNozzleNo(Change.toString("%02d",nozNo));
				//S4_wm.setTotalGauge(last_SJ_TotalGauge);
				S4_wm.setTotalGauge("0000000000");
				insertRecvQueue(S4_wm); // �����Ϸ� �ڷ�
				
				progressStep = 3;
				nozState = 0;

				sentPumpingStartInfo=true;
				waitTotalGuageFor_S4=false;
				waitGaugeForS4_Cnt=0;

				LogUtility.getPumpALogger().info("�����Ϸ� ���а�����(65) �̼���, �����Ϸ�(S4)���� ����");
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
				mode = (mode==POL? SEL : POL);
				enq_Buf[2] = mode;
				
				if (mode==SEL) {
					if (progressStep != 1 && TxQue.isEmpty()==true)
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
					LogUtility.getPumpALogger().info("0.Send ENQ (Noz="+nozNo+")");
					Log.datas(enq_Buf, 4, 20);
				}

				//--- recv data---//	
				if (recvText(RxBuf) < 1) {
					lineErrCnt++;
					if (dispLevel>=2)
						LogUtility.getPumpALogger().info("0.Recv ACK/STX fail!(No response) lineErrCnt="+lineErrCnt+"/"+minErrCnt+" (Noz="+nozNo+")");
					else if (lineErrCnt >= minErrCnt) {
						if (m_nNoResponseCnt%5==0) {
							m_nNoResponseCnt=0;
							LogUtility.getPumpALogger().debug("0.Recv ACK/STX fail!(No response) lineErrCnt="+lineErrCnt+"/"+minErrCnt+" (Noz="+nozNo+")");
						}
						m_nNoResponseCnt++;
					}
					continue;
				}

				//###### Recv data(Polling mode) : STX ######//
				if (mode==POL) { // 0x51

					if (RxBuf[0] == STX) {

						if (dispLevel>=3) {
							LogUtility.getPumpALogger().info("1.Recv STX(Data) :");
							Log.datas(RxBuf, 80, 20);
						}
						
						if (compareBCC(RxBuf)==false) {
							retryRecvData(RxBuf);
							LogUtility.getPumpALogger().info ("1.Recv STX BCC fail! (Noz=" + nozNo + ")");
							Log.datas(RxBuf, 40, 20);
							//sendText (NAK);
							continue;
						}
						if (compareNozID(RxBuf)==false) { 
							LogUtility.getPumpALogger().info ("1.Recv STX NozID mismatch! (Noz="+nozNo+")");
							Log.datas(RxBuf, 40, 20);
							continue;
						}
						if (verifyData(RxBuf)==false) { 
							retryRecvData(RxBuf);
							LogUtility.getPumpALogger().info ("1.Recv STX Data verify fail! (Noz="+nozNo+")");
							Log.datas(RxBuf, 40, 20);
							continue;
						}
						else {
							if (dispLevel==1 || dispLevel==2) {
								LogUtility.getPumpALogger().info ("1.Recv STX with normal (Noz="+nozNo+")");
								Log.datas(RxBuf, 80, 20);
							}

							if (m_isSaveSTX==true) 
								LogUtility.getPumpALogger().info("Recv STX("+Change.toString("%02d", nozNo)+
										") : ["+pack(RxBuf, ETX)+"]");
							else
								LogUtility.getPumpALogger().debug("Recv STX("+Change.toString("%02d", nozNo)+
										") : ["+pack(RxBuf, ETX)+"]");
							
							processRecvSTX(); // ���� ���� ������ ó��
							
							if (recvTail_proc()==false) 
								lineErrCnt++;
							else
								lineErrCnt=0; // Normal terminated
						}
					}
					else if (RxBuf[0] == EOT) {
						if (dispLevel>=3)
							LogUtility.getPumpALogger().info("1.Recv EOT :");
						lineErrCnt=0; // Normal terminated
						continue;
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
						
						is.read (RxBuf,0,1);
						if (RxBuf[0]==0x30) { // recv : ACK0

							if (dispLevel>=3)
								LogUtility.getPumpALogger().info("2.Recv ACK0 (Noz="+nozNo+")");
		
							if (TxQue.isEmpty()==false) { // �۽� ������ ������
								
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
									LogUtility.getPumpALogger().info("2.Send STX(TxBuf) :\n");
									Log.datas(TxBuf, TxBuf.length, 20);
								}
								
								if (recvText(RxBuf) < 1) {
									if (dispLevel>=3)
										LogUtility.getPumpALogger().info("2.Recv ACK1 fail! (Noz="+nozNo+")");
									lineErrCnt++;
									continue;
								}

								if (RxBuf[0]==ACK) {
									is.read (RxBuf,0,1);
									if (RxBuf[0]==0x31) { // recv : ACK1
										
										if (m_isSaveSTX==true) 
											LogUtility.getPumpALogger().info("Send STX("+Change.toString("%02d", nozNo)+
													") : ["+new String(TxBuf)+"]");
										else
											LogUtility.getPumpALogger().debug("Send STX("+Change.toString("%02d", nozNo)+
													") : ["+new String(TxBuf)+"]");
										
										TxQue.deQueue(); // remove item
										
										if (dispLevel>=3)
											LogUtility.getPumpALogger().info("2.Recv ACK1");

										if (dispLevel==2) {
											LogUtility.getPumpALogger().info("2.Send STX with normal (Noz="+nozNo+")");
											Log.datas(TxBuf, TxBuf.length, 20);
										}
										lineErrCnt=0; // Normal terminated
									}
								}
								else if (dispLevel>=3) {
									LogUtility.getPumpALogger().info("2.Recv ACK1 fail! (Noz="+nozNo+")");
									lineErrCnt++;
								}
							}
						}
					}
					else if (RxBuf[0] == EOT) {
						if (dispLevel>=3) {
							LogUtility.getPumpALogger().info("2.Recv EOT");
							//Show.datas(RxBuf, 26, 20);
						}
						lineErrCnt=0; // Normal terminated
						continue;
					} else {
						if (dispLevel>=3)
							LogUtility.getPumpALogger().info("2.Recv fail! (Noz="+nozNo+")");
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
			
		if (buf[3]=='0' && buf[4]=='0') { // �ʱ�ȭ
			//size = 11;
			//cmd = "00";
			return true;
		}
		else if (buf[3]=='6' && buf[4]=='0') { // ������ ��Ȳ����
			size = 9;
			cmd = "60";
		}
		else if (buf[3]=='6' && buf[4]=='2') { // �������� ���� �޽���
			size = 10;
			cmd = "62";
		}
		else if (buf[3]=='6' && buf[4]=='1') { // �����ڷ� ����
			size = 28;
			cmd = "61";
		}
		else if (buf[3]=='6' && buf[4]=='5') { // ���а����� �ڷ� ����
			size = 33;
			cmd = "65";
		}
		else 
			return false;

		//--- ������ ����(������ ���̿� ETX Ȯ��) ---//
		if (size != bufLen || buf[size-2] != ETX) {
			LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> �̻����� ����("+cmd+"), �ǵ����ͱ���=" + 
					size + ", ���ŵ����ͱ���=" + bufLen + ", ETX=" + buf[size-1]);
			return false;
		}

		//--- ������ ����(���ڿ���) ---//
		for (int i=5; i<size-2; i++) {
			if (buf[i] < 0x30 || buf[i] > 0x39)
				if (buf[i] != 0x20) { // 0 ~ 9 �� ������ �ƴϸ�
					LogUtility.getPumpALogger().info("nozzle=" + nozNo + 
						" -> �̻����� ����("+cmd+"), ���ڳ� ������ �ƴ�, ���ŵ�����=" + buf[i]);
					return false;
				}
		}
		
		return true;
	}
}
