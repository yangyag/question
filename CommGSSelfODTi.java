/*
 * GS ǥ�ؼ��� ODT ��������(TCP/IP ��� ���) - �񵿱� ���
 * PI2-�ڵ�ȭ, 2016-01-07 �ۼ�
 * 
 * ODT �� �����⸦ �����ϴ� ������� ��� ������ ODT�� ���� �ۼ����Ѵ�.
 * GSocketServer�� ODT ���� �񵿱� ������� ����� �Ѵ�.(Polling �� ���� �ʴ´�)
 * ���� ������ �����Ͽ� ��⿡�� ���ŵǴ� ������ ByPass ó���Ѵ�.
 */
package com.gsc.kixxhub.device.pumpa.driver;

import java.io.IOException;
import java.util.Calendar;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Vector;

import com.gsc.kixxhub.common.data.pump.BC_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.BI_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.BR_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.BS_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.GA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.GB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.GT_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P2_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P5_1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P5_NozzleInfo;
import com.gsc.kixxhub.common.data.pump.P6_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P7_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PC_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PU_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PV_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S3_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S4_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S8_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SE_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SJ_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.ClearUtil;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.BytesQue2;
import com.gsc.kixxhub.device.pumpa.common.Change;
import com.gsc.kixxhub.device.pumpa.common.DataStruct;
import com.gsc.kixxhub.device.pumpa.common.SerialConnectException;
import com.gsc.kixxhub.device.pumpa.common.Sleep;
import com.gsc.kixxhub.device.pumpa.common.Time;
import com.gsc.kixxhub.device.pumpa.controller.GServerService;
import com.gsc.kixxhub.device.pumpa.controller.GSocketServerSelf;
import com.gsc.kixxhub.device.pumpa.controller.PacketUtil;
import com.gsc.kixxhub.device.pumpa.define.CCommDriver;
import com.gsc.kixxhub.device.pumpa.translation.TransGSSelfOdt;


public class CommGSSelfODTi extends CCommDriver implements GServerService {

	TransGSSelfOdt trans = new TransGSSelfOdt();
		
	protected BytesQue2 TxQue = new BytesQue2(30);

	protected int HEADER_SIZE = 20;
	protected int BUFFER_SIZE = 4096;

	protected byte[] 	TxBuf = new byte[BUFFER_SIZE];
	protected byte[] 	RxBuf = new byte[BUFFER_SIZE];
		
	// TCP/IP ó�� ����
	private String 		host = ""; // SC IP address
	private int 		port = 0;  // SC Port = 55000 + ODT ��ȣ
	private GSocketServerSelf m_sockServer = new GSocketServerSelf(HEADER_SIZE, BUFFER_SIZE);

	protected byte		nozID;
	protected String 	nozStr = "";
    //protected int 	dispLevel=0;
    protected int 		dispLevel=2;
	protected int		lineErrCnt=0;
	protected int		minErrCnt=50;
	
	protected boolean	issueLineErr=true;
	protected boolean	issueLineOK=true;
	protected boolean	okSend_SJ=true;
	//protected boolean	okSend_S4=true;
	protected boolean	isCompleteBasePrice=false;
	protected boolean	isChangeEnvData=false;
	protected boolean	isODTInitCompleted=false;
	protected boolean	useFullPumping=true;
	protected boolean	rcvEnvDataOK=false;
	protected int		rcvEnvDataCnt=0;
	
	protected String 	lastGARcvdMessageType=""; 	// �������� GA���� �޽�������
	protected String 	lastGARcvdODTNo=""; 		// �������� GA���� ODT No
	protected String 	lastGARcvdNozNo=""; 		// �������� GA���� Noz No
	protected String 	lastGARcvdCreatedTime=""; 	// �������� GA���� �����ð�
	
	protected String 	lastGTRcvdODTNo=""; 		// �������� GT���� ODT No
	protected String 	lastGTRcvdNozNo=""; 		// �������� GT���� Noz No
	protected String 	lastGTRcvdCreatedTime=""; 	// �������� GT���� �����ð�
	
	protected String 	lastS4RcvdODTNo=""; 		// �������� S4���� ODT No
	protected String 	lastS4RcvdNozNo=""; 		// �������� S4���� Noz No
	protected String 	lastS4RcvdSystemTime=""; 	// �������� S4���� �ý��� �ð� 
	protected String 	lastS4RcvdTotalGauge=""; 	// �������� S4���� Total gauge

	protected String 	lastBRRcvdODTNo=""; 		// �������� BR���� ODT No
	protected String 	lastBRRcvdNozNo=""; 		// �������� BR���� Noz No
	protected String 	lastBRPosReceiptNo=""; 		// �������� BR���� ��ǥ��ȣ

	protected int 		m_statusCode=0;
	protected int 		m_nozState=0;
	protected String 	m_basePrice="000000"; // ���� ��û�ܰ�
	protected String 	m_pumpingLiter="0";
	protected String 	m_pumpingPrice="0";
	protected String 	m_customerType="1"; // �Ϲ�
	
	protected long 		m_lastConnectedTime=0;

	protected Vector<String> m_nozNoVec = new Vector<String>();
	protected Hashtable<String,String> m_basePriceTbl = new Hashtable<String,String>();
	protected Hashtable<String,String> m_oilCodeTbl = new Hashtable<String,String>(); 
	protected Hashtable<String,String> m_oilNameTbl = new Hashtable<String,String>();
			
    protected byte  	ENQ  = 0x05;
    protected byte  	ACK  = 0x10;
    protected byte  	NAK  = 0x15;
    protected byte  	STX  = 0x02;
    protected byte  	ETX  = 0x03;
    protected byte  	EOT  = 0x04;
    protected byte  	ESC  = 0x1B;

    protected DataStruct P1_ds = new DataStruct();
    protected DataStruct P2_ds = new DataStruct();
    protected DataStruct P5_ds = new DataStruct();
    protected DataStruct P6_ds = new DataStruct();
    protected DataStruct PM_ds = new DataStruct();
    protected DataStruct PU_ds = new DataStruct();
    protected DataStruct PV_ds = new DataStruct();
    protected DataStruct GA_ds = new DataStruct();
    protected DataStruct GB_ds = new DataStruct();
    protected DataStruct GT_ds = new DataStruct();
    protected DataStruct BI_ds = new DataStruct();
    protected DataStruct BC_ds = new DataStruct();
    protected DataStruct FC_ds = new DataStruct();
    
    protected DataStruct PA_ds = new DataStruct();
    protected DataStruct S3_ds = new DataStruct();
    protected DataStruct S4_ds = new DataStruct();
    protected DataStruct PB_ds = new DataStruct();
    protected DataStruct P3_ds = new DataStruct();
    protected DataStruct SE_ds = new DataStruct();
    protected DataStruct S8_ds = new DataStruct();

    protected byte[] 	P1_Buf;
    protected byte[] 	P2_Buf;
    protected byte[] 	P5_Buf;
    protected byte[] 	P6_Buf;
    protected byte[] 	PM_Buf;
    protected byte[]    PU_Buf; // ODT ��ġ �˸���û �߰�
    protected byte[] 	PV_Buf;
    protected byte[] 	GA_Buf;
    protected byte[] 	GB_Buf;
    protected byte[] 	GT_Buf;
    protected byte[] 	BI_Buf;
    protected byte[] 	BC_Buf;
    protected byte[] 	FC_Buf;
    
    protected byte[] 	PA_Buf;
    protected byte[] 	S3_Buf;
    protected byte[] 	S4_Buf;
    protected byte[] 	PB_Buf;
    protected byte[] 	P3_Buf;
    protected byte[] 	SE_Buf;
    protected byte[] 	S8_Buf;
    protected byte[] 	PC_Buf;
		
	public CommGSSelfODTi (int ODTNum, String romVerStr, String ipAddress) {
				
		super.nozNo = ODTNum;
		super.romVer = romVerStr;
		super.ipAddress = ipAddress;
		
		nozStr = Change.toString("%02d", nozNo);
		byte[] nozByt = nozStr.getBytes();
		nozID = (byte) (nozNo + 0x40);

		try {						
			
			//----------- ���� ���� -----------//
			//-- P6 : �ð����� --//
			P6_ds.addString("Command", "P6", 2);
			P6_ds.addString("DeviceNo", "", 2);
			P6_ds.addString("ConnectDevNo", "", 2);
			P6_ds.addString("SystemTime", "", 12);
			P6_Buf = P6_ds.getByteStream();

			//-- PM : ODT ������� --//
			PM_ds.addString("Command", "PM", 2);
			PM_ds.addString("DeviceNo", "", 2);
			PM_ds.addString("ConnectDevNo", "", 2);
			PM_ds.addString("Mode", "", 1);
			PM_Buf = PM_ds.getByteStream();

			//-- PU : ODT ��ġ �˶� ��û --//
			// 2019.08.01 ODT��û�������� �߰�   
			// by SoonKwan Kwon
			PU_ds.addString("Command", "PU", 2);
			PU_ds.addString("DeviceNo", "", 2);
			PU_ds.addString("ConnectDevNo", "", 2);
			PU_ds.addString("Mode", "", 1);
			PU_Buf = PU_ds.getByteStream();
			
			//-- PV : ODT �������� --//
			PV_ds.addString("Command", "PV", 2);
			PV_ds.addString("DeviceNo", "", 2);
			PV_ds.addString("ConnectDevNo", "", 2);
			PV_ds.addString("SystemTime", "", 12);
			PV_Buf = PV_ds.getByteStream();

			//-- GA : ���ο�û --//
			GA_ds.addString("Command", "GA", 2);
			GA_ds.addString("ODTNo", "", 2);
			GA_ds.addString("NozzleNo", "", 2);
			GA_ds.addVString("MessageType", "", 4);
			GA_ds.addVString("CardNumber", "", 100);
			GA_ds.addVString("BonusCard", "", 100);
			GA_ds.addVString("CustCardNo", "", 100);
			GA_ds.addVString("CashReceiptNo", "", 100);
			GA_ds.addVString("Liter", "", 7);
			GA_ds.addVString("BasePrice", "", 6);
			GA_ds.addVString("Price", "", 8);
			GA_ds.addVString("LEDCode", "", 1);
			GA_ds.addVString("BonusPin", "", 15);			
			GA_ds.addVString("CreditedTime", "", 14);
			GA_ds.addVString("UnityMessage", "", 4000);
			GA_Buf = GA_ds.getByteStream();

			//-- GB : �������� --//
			GB_ds.addString("Command", "GB", 2);
			GB_ds.addString("ODTNo", "", 2);
			GB_ds.addString("NozzleNo", "", 2);
			GB_ds.addVString("MessageType", "", 4);
			GB_ds.addVString("UnityMessage", "", 4000);
			GB_Buf = GB_ds.getByteStream();

			//-- GT : ODT �ǸſϷ� --//
			GT_ds.addString("Command", "GT", 2);
			GT_ds.addString("DeviceNo", "", 2);
			GT_ds.addString("ConnectDevNo", "", 2);
			GT_ds.addString("Liter", "", 7);
			GT_ds.addString("BasePrice", "", 6);
			GT_ds.addString("Price", "", 8);
			GT_Buf = GT_ds.getByteStream();
			
			//-- BI : �������� ���� --//
			BI_ds.addString("Command", "BI", 2);
			BI_ds.addString("DeviceNo", "", 2);
			BI_ds.addString("ConnectDevNo", "", 2);
			BI_ds.addString("Cash", "", 8);
			BI_ds.addString("CashCount", "", 8);
			BI_ds.addString("Time", "", 14);
			BI_Buf = BI_ds.getByteStream();
			
			//-- BC : �������� ������� --//
			BC_ds.addString("Command", "BC", 2);
			BC_ds.addString("DeviceNo", "", 2);
			BC_ds.addString("ConnectDevNo", "", 2);
			BC_ds.addString("CashCount", "", 8);
			BC_ds.addString("Time", "", 14);
			BC_Buf = BC_ds.getByteStream();

			//-- FC : ODT �������� ���� --//
			FC_ds.addString("Command", "FC", 2);
			FC_ds.addString("DeviceNo", "", 2);
			FC_ds.addString("ConnectDevNo", "", 2);
			FC_ds.addString("ReCertiOrder", "", 1);
			FC_ds.addString("UseFullPumping", "", 1);
			FC_ds.addString("CertiType", "", 1);
			FC_ds.addString("SettingPrice", "", 8);
			FC_Buf = FC_ds.getByteStream();
			
			//-- P3 : ������ �ܰ����� ���� --//
			P3_ds.addString("Command", "P3", 2);
			P3_ds.addString("DeviceNo", "", 2);
			P3_ds.addString("ConnectDevNo", "", 2);
			P3_ds.addString("NozzleNo", "", 2);
			P3_ds.addString("GoodsCode", "", 18);
			P3_ds.addString("BasePrice", "", 6);
			P3_Buf = P3_ds.getByteStream();

			//-- PB : �����㰡 --//
			PB_ds.addString("Command", "PB", 2);
			PB_ds.addString("DeviceNo", "", 2);
			PB_ds.addString("ConnectDevNo", "", 2);
			PB_ds.addString("Mode", "", 1);
			PB_ds.addString("Liter", "", 7);
			PB_ds.addString("BasePrice", "", 6);
			PB_ds.addString("Price", "", 8);
			PB_Buf = PB_ds.getByteStream();
			
			//-- SE : ������/ODT �̻����� --//
			SE_ds.addString("Command", "SE", 2);
			SE_ds.addString("DeviceNo", "", 2);
			SE_ds.addString("ConnectDevNo", "", 2);
			SE_ds.addString("DiviceType", "", 1);
			SE_ds.addString("Status", "", 4);
			SE_ds.addString("StatusCode", "", 3);
			SE_ds.addString("ErrorMsg", "", 20);
			SE_ds.addString("DetectTime", "", 12);
			SE_Buf = SE_ds.getByteStream();

			//-- S8 : ������/ODT �������� --//
			S8_ds.addString("Command", "S8", 2);
			S8_ds.addString("DeviceNo", "", 2);
			S8_ds.addString("ConnectDevNo", "", 2);
			S8_ds.addString("DiviceType", "", 1);
			S8_ds.addString("Status", "", 1);
			S8_Buf = S8_ds.getByteStream();
			
			
			super.setInterface_method(1); // ��Ź�� 1=TCP/IP			
			INetStart(); // non-blocking socket channel ����
			
		} catch (Exception e) {
			LogUtility.getLogger().error(e.getMessage(), e);
		}
	}
	
	public void INetStart() {		
		
		try {
			host = super.ipAddress;
			port = 55000 + nozNo;

			m_sockServer.service=this;
			m_sockServer.start(host, port, nozStr);
			m_sockServer.dispLevel=dispLevel;
						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
 
	public void INetStop() {
		
		//m_sockServer.removeListener(this);
		m_sockServer.stop();
	}
	
	public byte[] makePacket(String src, String dest, String packGrp, byte[] bodyBytes) {
		DataStruct head_ds = new DataStruct();
		DataStruct tail_ds = new DataStruct();
		
		try {
			String length = Change.toString("%08d", bodyBytes.length + 2);
			
			head_ds.addByte  ("SOH", (byte) 0x01);
			head_ds.addString("Source", src, 4);
			head_ds.addString("Destination", dest, 4);
			head_ds.addString("PacketGroup", packGrp, 2);
			head_ds.addString("Length", length, 8); // Length = STX ~ ETX	
			head_ds.addByte  ("STX", (byte) 0x02);
			byte[] byHead = head_ds.getByteStream();
			
			tail_ds.addByte  ("ETX", (byte) 0x03);
			tail_ds.addString("CRC", "", 4); // 4 bytes
			byte[] byTail = tail_ds.getByteStream();
			
			return (new String(byHead) + new String(bodyBytes) + new String(byTail)).getBytes();
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
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
		
		//LogUtility.getLogger().debug("length="+dat.length+", stxIdx="+stxIdx+", etxIdx="+etxIdx);
		//Show.datas(dat, dat.length, 20);

		if (stxIdx<=0 || etxIdx<=0) // No exist STX, ETX
			return -1;
		
		//--- Calculate BCC
		for (i=stxIdx; i<=etxIdx; i++) {
			bcc = (byte) (bcc ^ dat[i]); // XOR
		}
		
		return bcc;
	}
	
	protected void setBCC(byte[] buf) throws Exception {
		int	i;
		
		for (i=0; i<buf.length-2; i++) {
			if (buf[i]==ETX)
				break;
		}
		
		buf[i+1] = getBCC(buf);
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
	
	public boolean verifyRecvData (byte[] buf) {

//		byte[] by = new byte[4];		
//		int len = PacketUtil.getPacketLength(buf);
//		String odt = PacketUtil.getBodyODTNo(buf);
//
//		try {
//			System.arraycopy(buf, 1, by, 0, 4);
//			String src = new String(by);
//			System.arraycopy(buf, 5, by, 0, 4);
//			String dst = new String(by);
//			
//			if(odt.equals(nozStr) && buf[0]==0x01 && src.equals("OD01") && dst.equals("SC01") && buf[19]==0x02 && buf[len+18]==0x03)		
//				return true;
//			else {
//				if (buf[0]!=0x01 || buf[19]!=0x02 || buf[len+18]!=0x03)
//					LogUtility.getLogger().info("ODTNo=" + nozStr + ", ���ŵ����� ���� - Data format mismatched");
//				
//				if (!odt.equals(nozStr))
//					LogUtility.getLogger().info("ODTNo=" + nozStr + ", ���ŵ����� ���� - ODTNo mismatched : received ODTNo=" + odt);
//				
//				if (!src.equals("OD01") || !dst.equals("SC01"))
//					LogUtility.getLogger().info("ODTNo=" + nozStr + ", ���ŵ����� ���� - Data address mismatched : received src=" + src + ", dst=" + dst);
//				
//				Log.datas(buf, PacketUtil.pack(buf).length, 20);
//				return false;
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
		
		return true;
	}
	
	protected void makeLineError () throws Exception { // ȸ���ҷ� �߻�

		m_statusCode=601; // appended
		
		Calendar cal = new GregorianCalendar();
		Formatter form = new Formatter();
		String year = Change.toString("%04d", cal.get(Calendar.YEAR));
		form.format("%s%02d%02d%02d%02d%02d", year.substring(2, 4),
				cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH), 
				cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), 
				cal.get(Calendar.SECOND));
		String timeStr = form.toString();	
		
		//LogUtility.getLogger().info("m_nozNoVec.size()======" + m_nozNoVec.size());
		
		for (String nozStr : m_nozNoVec) { //-- ������ ȸ���ҷ�
			SE_WorkingMessage SE_wm = new SE_WorkingMessage();
			SE_wm.setNozzleNo(nozStr);
			SE_wm.setDeviceType("02");
			SE_wm.setStatus("1");
			SE_wm.setStatusCode("601"); // ȸ���ҷ�
			SE_wm.setErrMsg("���������� ȸ���ҷ�");		
			SE_wm.setDetectTime(timeStr);
			insertRecvQueue(SE_wm);
		}		

		//-- ODT ȸ���ҷ�
		SE_WorkingMessage SE_wm = new SE_WorkingMessage();
		SE_wm.setNozzleNo(Change.toString("%02d", nozNo));
		SE_wm.setDeviceType("05");
		SE_wm.setStatus("1");
		SE_wm.setStatusCode("601"); // ȸ���ҷ�
		SE_wm.setErrMsg("����ODT ȸ���ҷ�");
		SE_wm.setDetectTime(timeStr);
		insertRecvQueue(SE_wm);		
		
	}
	
	protected void makeStatusInfo(int nozState, int nozNo) throws Exception {

		S8_WorkingMessage S8_wm = new S8_WorkingMessage();
		int		statusCode=0;
		int		thisNozType=0;
		String 	errorMsg="";
		boolean isNozStatus=false;
		
		switch (nozState) {

			//-- ������
			case 2:
				statusCode = 651; // ����ٿ�
				errorMsg = "����ٿ�";
				thisNozType=2;
				break;
				
			case 1:
			case 3:
				statusCode = 652; // �����
				errorMsg = "�����";
				thisNozType=2;
				break;
				
			case 4:
				statusCode = 653; // ������
				errorMsg = "������";
				thisNozType=2;
				break;
				
			case 5:
				statusCode = 654; // �����Ϸ�
				errorMsg = "�����Ϸ�";
				thisNozType=2;
				break;
			case 655:
				statusCode = 655; // ������ ����(ENE �����ڵ�(E) ����)
				errorMsg = "������ ����";
				thisNozType=2;
				break;
				
			case 656:
				statusCode = 656; // �������(��������)
				errorMsg = "�������";
				thisNozType=2;
				break;
				
			case 657:
				statusCode = 657; // �����������
				errorMsg = "�������";
				thisNozType=2;
				break;
			
			//-- ODT	
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
				thisNozType=5;
				break;
				
			case 651:
				statusCode = 651; // ����
				errorMsg = "����ٿ�";
				thisNozType=5;
				break;
				
			case 699:
				statusCode = 699; // ����
				errorMsg = "����ȭ�� �̵�";
				thisNozType=5;
				break;
		}
		m_statusCode = statusCode;
		
		S8_wm.setNozzleNo(Change.toString("%02d", nozNo));		
		S8_wm.setDeviceType(Change.toString("%02d", thisNozType));
		S8_wm.setStatus("0");
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
	
	protected void makeStatusInfoAll (int stateCode) throws Exception { // ��� ���� ���� �������� ó��(����)
		
		for (String nozStr : m_nozNoVec) {
			makeStatusInfo(stateCode, Change.toValue(nozStr)); // ����ٿ�
		}
		
//		makeStatusInfo(651, nozNo); // ODT����
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
	

	public void recvMessage(byte[] rcvData) {
				
		try {
			//LogUtility.getLogger().info("ODTNo=" + nozStr + ", Recv <-- ���� : " + new String(PacketUtil.pack(rcvData)));
			//Log.datas(rcvData, PacketUtil.getPacketLength(rcvData), 20);

			RxBuf = PacketUtil.getBodyBytes(rcvData);
			
			processRecvDeviceData(RxBuf);
			
		} catch (Exception e) {
			LogUtility.getLogger().error(e.getMessage(), e);
		}
		
	}

	public void sendMessage(byte[] sndData) {

		try {
			byte[] sndPacket = makePacket("SC01", "OD01", "04", sndData);
//			if (PacketUtil.getDataCommand(sndPacket).equals("GB"))
//				ClearUtil.setClearString(sndData);
			
			m_sockServer.TxQue.enQueue(sndPacket);

			//LogUtility.getLogger().info("ODTNo=" + nozStr + ", Send --> ���� : " + new String(sndPacket));
			//Log.datas(sndPacket, sndPacket.length, 20);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	protected boolean processRecvWorkingMessage (WorkingMessage wm) throws Exception {
		
		boolean skip=false;

		if (wm.getCommand().equals("P1")) { // ��������

			P1_WorkingMessage P1_wm = (P1_WorkingMessage) wm;
			
			LogUtility.getLogger().info("[Pump A][GS����ODT �������� >>> ��������(P1) : " + " ODT_No=" + 
					P1_wm.getNozzleNo() + "("+P1_wm.getCommand() + ")");
			
			// ������/�ð� ����
			P1_Buf = generateByteStream(P1_wm);
			rcvEnvDataCnt++;
			
			skip = true;
		}		
		else if (wm.getCommand().equals("P2")) { // ������ ����

			P2_WorkingMessage P2_wm = (P2_WorkingMessage) wm;
			
			LogUtility.getLogger().info("[Pump A][GS����ODT �������� >>> ������ ����(P2) : " + " ODT_No=" + 
					P2_wm.getNozzleNo() + "("+P2_wm.getCommand() + ")");
			
			// ������/�ð� ����
			P2_Buf = generateByteStream(P2_wm);
			rcvEnvDataCnt++;
			
			skip = true;
		}
		else if (wm.getCommand().equals("P6")) { // ���� ������/�ð� ȯ�漳��

			P6_WorkingMessage P6_wm = (P6_WorkingMessage) wm;
			
			LogUtility.getLogger().info("[Pump A][GS����ODT �������� >>> ������/�ð� ����(P6) : " + " ODT_No=" + 
					P6_wm.getNozzleNo() + "("+P6_wm.getCommand()+")" +
					" | systemTime=" + P6_wm.getSystemTime());
			
			// ������/�ð� ����
			P6_Buf = generateByteStream(P6_wm); 
			//rcvEnvDataCnt++;
			
			skip = true;
		}
		else if (wm.getCommand().equals("P5_1")) { // ����ODT ����/�ܰ� ȯ�漳��

			P5_1_WorkingMessage P5_1_wm = (P5_1_WorkingMessage) wm;
			Vector<P5_NozzleInfo> nozInfoVec = P5_1_wm.getNozzleInfo();
			int nozCnt = nozInfoVec.size();
			
			LogUtility.getLogger().info("[Pump A][GS����ODT �������� >>> ����/�ܰ� ����(P5) : " + " ODT_No=" + 
					P5_1_wm.getNozzleNo() + "("+P5_1_wm.getCommand()+")" +
					" | mode=" + P5_1_wm.getMode() + 
					" | useFullPumping=" + P5_1_wm.getUseFullPumping());

			// ����/�ܰ����� ����
			P5_Buf = generateByteStream(P5_1_wm); 

//			LogUtility.getLogger().info(">>>>>>>>>>>>> P5_1 ������=" + new String(P5_Buf));
//			Log.datas(P5_Buf, P5_Buf.length, 20);

			// �ܰ� ������� Ȯ��
			if (P5_1_wm.getMode().equals("0") || P5_1_wm.getMode().equals("1")) {
				if (isCompleteBasePrice(P5_1_wm) == false) 
					LogUtility.getLogger().info("������ �ܰ����� ����(P5_1), ODTNo=" + P5_1_wm.getNozzleNo());
			}

			if (P5_1_wm.getMode().equals("0")) { // �ʱ�ȭ
				LogUtility.getLogger().info("����ODT �������� ����(P5_1), ODTNo=" + P5_1_wm.getNozzleNo());
				
				for (int i=0; i<nozCnt; i++) {
					P5_NozzleInfo P5_nozWm = nozInfoVec.get(i);
					String szNozNo = P5_nozWm.getNozzleNumber();
					LogUtility.getLogger().info(" -nozzleNo ="+ P5_nozWm.getNozzleNumber());
					LogUtility.getLogger().info(" -basePrice="+ P5_nozWm.getBasePrice());
					LogUtility.getLogger().info(" -oilType  ="+ P5_nozWm.getGoodsCode());
					LogUtility.getLogger().info(" -oilCode  ="+ P5_nozWm.getGoodsType() + "\n");
					
					m_nozNoVec.add(szNozNo);
					m_basePriceTbl.put(szNozNo, P5_nozWm.getBasePrice());
					m_oilCodeTbl.put(szNozNo, P5_nozWm.getGoodsCode());
					m_oilNameTbl.put(szNozNo, P5_nozWm.getGoodsType());
				}
				// �������� ��뿩��
				useFullPumping = P5_1_wm.getUseFullPumping().equals("1")? true : false;
				
				isChangeEnvData=false;
				//setEnvDataOK=true;
				rcvEnvDataCnt++;

				skip = true; // ODT�ʱ�ȭ�� ������
			}
			else if (P5_1_wm.getMode().equals("1")) { // �ܰ�����
				isChangeEnvData=true; 
				
				LogUtility.getLogger().info("�ܰ������ P5����, �ܰ�����. ODT_No=" + P5_1_wm.getNozzleNo());
				
				skip = true; // ODT�ʱ�ȭ�� ������
			}
			else if (P5_1_wm.getMode().equals("2")) { // �������� �ɼǺ���
				isChangeEnvData=true; 
								
				LogUtility.getLogger().info("�������� �ɼǺ���� P5����, �ɼǺ���. ODT_No=" + P5_1_wm.getNozzleNo() +
						" useFullPumping=" + P5_1_wm.getUseFullPumping());
				
				skip = true; // ODT�ʱ�ȭ�� ������
			}
			
		}	
		else if(wm.getCommand().equals("PC")) { // ����ȯ�漳�� ����
			PC_WorkingMessage pc_wm = (PC_WorkingMessage) wm;
			
			LogUtility.getLogger().info("[Pump A][GS����ODT �������� >>> ����ȯ�ἳ������(PC) : " + " ODT_No=" + 
					pc_wm.getNozzleNo() + "("+pc_wm.getCommand() + ")");
			
			PC_Buf = generateByteStream(pc_wm);
			rcvEnvDataCnt++;
			
			skip = true;
		}
		else if (wm.getCommand().equals("PB")) { // ������ �Ǵ� ������������
			PB_WorkingMessage PB_wm = (PB_WorkingMessage) wm;
			
			LogUtility.getLogger().info("������/������������(PB) ����, �����㰡(PB) �߻� : ODT_No=" + PB_wm.getNozzleNo()+ 
				" nozzle=" + PB_wm.getConnectNozzleNo() + " mode=" + PB_wm.getCommandSet()+ 
				" liter=" + PB_wm.getLiter() + " basePrice=" + PB_wm.getBasePrice() + " price=" + PB_wm.getPrice());

			//m_basePrice = PB_wm.getBasePrice();
			//makeStatusInfo (235, nozNo); // �������

			skip = false;
		}
		else if (wm.getCommand().equals("PA")) { // ��������(�������/����)
			PA_WorkingMessage PA_wm = (PA_WorkingMessage) wm;

//			int nNozState = Change.toValue(PA_wm.getNozzleState());
//			boolean m_nozLock = (nNozState==0 ? true : false);
//			if (m_nozLock==true) {
//				//m_recvedNozLock=true;
//				makeStatusInfo(656, Change.toValue(PA_wm.getConnectNozzleNo()));
//			} else {
//				makeStatusInfo(657, Change.toValue(PA_wm.getConnectNozzleNo()));
//			}
			
			LogUtility.getLogger().info("�������/����(PA) ���� : ODT_No=" + PA_wm.getNozzleNo()+ 
				" nozzle=" + PA_wm.getConnectNozzleNo() + " state=" + PA_wm.getNozzleState() + " (0:����, 1:����)");

			if (m_nozNoVec.contains(PA_wm.getConnectNozzleNo()))			
				skip = false; // ������ ��� PA ����
			else
				skip = true; // ODT�� ��� PA ���� ����
		}
/*		else if (wm.getCommand().equals("CB")) { // ������ Ȯ�� ����
			CB_WorkingMessage CB_wm = (CB_WorkingMessage) wm;
			
			LogUtility.getLogger().info("������ Ȯ�� ����(CB) ����. ODT_No=" + CB_wm.getNozzleNo()+ 
				" nozzle=" + CB_wm.getConnectNozzleNo() + " customerType=" + CB_wm.getCustomerType()+ 
				" saveBonus=" + CB_wm.getSaveBonus() + " message=" + CB_wm.getMessage());

			m_customerType = CB_wm.getCustomerType();
			//makeStatusInfo (235, nozNo); // �������

			skip = false;
		}*/
		else if (wm.getCommand().equals("GB")) { // ī�� ��������
			GB_WorkingMessage GB_wm = (GB_WorkingMessage) wm;
			
			LogUtility.getLogger().info(new StringBuffer("\nGS����ODT �������� >>> ��������(GB) : " ).append( GB_wm.getCommand() )
					 					.append(" MessageType=" ).append( GB_wm.getMessageType() )
										.append("\n ODT_No     =" ).append( GB_wm.getNozzleNo() )
										.append(" nozzle     =" ).append( GB_wm.getConnectNozzleNo() ));

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

			m_sockServer.dispLevel = dispLevel;
			
			skip = true;
		}	
		else if (wm.getCommand().equals("BS")) { // ������ ���ڵ� ����
			BS_WorkingMessage BS_wm = (BS_WorkingMessage) wm;
			
			LogUtility.getLogger().info("������ ���ڵ� ����(BS) ����. ODT_No=" + BS_wm.getNozzleNo()+ 
				" nozzle=" + BS_wm.getConnectNozzleNo() + " barcode=" + BS_wm.getBarcode());

			skip = false;
		}
		else if(wm.getCommand().equals("PU")) { // ODT Version ��û
			PU_WorkingMessage PU_wm = (PU_WorkingMessage) wm;
			
			LogUtility.getLogger().info("SelfODT Version(PU) ��û. ODT_No=" + PU_wm.getNozzleNo());
			
			skip = false;
		}
		return skip;
	}


	protected void processRecvDeviceData (byte[] rcvBytes) throws SerialConnectException, Exception {
		
		byte[] byCmd = {rcvBytes[0], rcvBytes[1]};
		String command = new String(byCmd);
		
		if (command.equals("PM")) { //-- ODT �������
			
			if (rcvBytes[6] == '0') { // ODT �ʱ�ȭ	���

				if (rcvEnvDataOK == false) {
				//if (rcvEnvDataCnt < 4) {
					LogUtility.getLogger().info("ODT �ʱ�ȭ ��û�Ͽ����� ���� ȯ�漳�� ���� �̼������� �ʱ�ȭ ���� ������!!");
					return;
				}

				//-- P6 : �ð����� --//
				DataStruct ds = new DataStruct();
				ds.addString("Command", "P6", 2);
				ds.addString("DeviceNo", nozStr, 2);
				ds.addString("ConnectDevNo", "", 2);
				ds.addString("SystemTime", Time.currentTime(), 12);
				P6_Buf = ds.getByteStream();

				TxQue.enQueue(P1_Buf);
				TxQue.enQueue(P2_Buf);
				TxQue.enQueue(P5_Buf);
				TxQue.enQueue(PC_Buf);
				TxQue.enQueue(P6_Buf);
				LogUtility.getLogger().info("ODT �ʱ�ȭ ��� ó�� (PM) ...........");
				
				//Log.datas(P5_Buf, P5_Buf.length, 20);
			}
		}
/*		else if (command.equals("CA")) { //-- ������ Ȯ�ο�û

			//LogUtility.getLogger().info("1.########### GA���� ũ�� ==========" + rcvBytes.length);
			
			CA_WorkingMessage wm = (CA_WorkingMessage) generateWorkingMessage(rcvBytes, "CA");					
			insertRecvQueue(wm);

			LogUtility.getLogger().info("������ Ȯ�� ó�� (CA) ...........");
			LogUtility.getLogger().info(" -NozzleNo=" + wm.getNozzleNo());
			LogUtility.getLogger().info(" -ConnectNozzleNo=" + wm.getConnectNozzleNo());
			LogUtility.getLogger().info(" -CardNumber=" + wm.getCardNo());
			
			//makeStatusInfo (236, nozNo); // ���ο�û ��
		}*/
		else if (command.equals("GA")) { //-- ���� ���ο�û

			GA_WorkingMessage wm = (GA_WorkingMessage) generateWorkingMessage(rcvBytes, "GA");	
			
			if (wm.getMessageType().equals(lastGARcvdMessageType) && wm.getNozzleNo().equals(lastGARcvdODTNo) && 
					wm.getConnectNozzleNo().equals(lastGARcvdNozNo) && wm.getCreatedTime().equals(lastGARcvdCreatedTime)) {
				
				LogUtility.getLogger().info("���� GA ���� ����! -> ������� ����...");
				LogUtility.getLogger().info(" 1.�������� ���� ���� : ");
				LogUtility.getLogger().info("  - MessageType=" + lastGARcvdMessageType);
				LogUtility.getLogger().info("  - ODTNo      =" + lastGARcvdODTNo);
				LogUtility.getLogger().info("  - NozNo      =" + lastGARcvdNozNo);
				LogUtility.getLogger().info("  - CreatedTime=" + lastGARcvdCreatedTime);
				LogUtility.getLogger().info(" 2.������� ���� ���� : ");
				LogUtility.getLogger().info("  - MessageType=" + wm.getMessageType());
				LogUtility.getLogger().info("  - ODTNo      =" + wm.getNozzleNo());
				LogUtility.getLogger().info("  - NozNo      =" + wm.getConnectNozzleNo());
				LogUtility.getLogger().info("  - CreatedTime=" + wm.getCreatedTime() + "\n");

				ClearUtil.setClearString(rcvBytes);
				rcvBytes=null;
				wm=null;
				
				return; // ������ ������� ����
			}
			else {
				lastGARcvdMessageType = wm.getMessageType();
				lastGARcvdODTNo = wm.getNozzleNo();
				lastGARcvdNozNo = wm.getConnectNozzleNo();
				lastGARcvdCreatedTime = wm.getCreatedTime();

				insertRecvQueue(wm);
			}
			
			//m_customerType="1"; // �Ϲ�			

			LogUtility.getLogger().info("���ο�û ���� ó�� (GA) MessageType = " + wm.getMessageType());
			LogUtility.getLogger().info(" - NozzleNo    =" + wm.getNozzleNo() + " ConnectNozNo=" + wm.getConnectNozzleNo());
			LogUtility.getLogger().info(" - CardNumber  =" + wm.getCardNumber());
			LogUtility.getLogger().info(" - BonusCard   =" + wm.getBonusCard());
			LogUtility.getLogger().info(" - Liter       =" + wm.getLiter());
			LogUtility.getLogger().info(" - Price       =" + wm.getPrice());
			LogUtility.getLogger().info(" - BasePrice   =" + wm.getBasePrice());
			LogUtility.getLogger().info(" - LEDCode     =" + wm.getLedCode() + "\n");
			//LogUtility.getLogger().info(" -CustomerType=" + wm.getCustomerType());
						
//			makeStatusInfo (236, nozNo); // ���ο�û ��
			ClearUtil.setClearString(rcvBytes);
			rcvBytes=null;
		}
		else if (command.equals("BI")) { //-- ������������
			BI_WorkingMessage wm = (BI_WorkingMessage) generateWorkingMessage(rcvBytes, "BI");
			insertRecvQueue(wm);
		}
		else if (command.equals("BC")) { //-- �������� ���
			BC_WorkingMessage wm = (BC_WorkingMessage) generateWorkingMessage(rcvBytes, "BC");
			insertRecvQueue(wm);
		}
		else if (command.equals("S3")) { //-- ������ �ڷ�
			S3_WorkingMessage wm = (S3_WorkingMessage) generateWorkingMessage(rcvBytes, "S3");
			insertRecvQueue(wm);
			
			m_pumpingLiter = wm.getLiter();
			m_pumpingPrice = wm.getPrice();
			//okSend_S4 = true; // S4 �۽Ű���
		}
		else if (command.equals("S4")) { //-- �����Ϸ� �ڷ�
			//if (okSend_S4==true) {
			S4_WorkingMessage wm = (S4_WorkingMessage) generateWorkingMessage(rcvBytes, "S4");
			
			if(wm.getNozzleNo().equals(lastS4RcvdODTNo) && wm.getConnectNozzleNo().equals(lastS4RcvdNozNo) &&
			   wm.getSystemTime().equals(lastS4RcvdSystemTime) && wm.getTotalGauge().equals(lastS4RcvdTotalGauge))
			{
				LogUtility.getLogger().info("���� S4 ���� ����! -> ������� ����...");
				LogUtility.getLogger().info(" 1.�������� ���� ���� : ");
				LogUtility.getLogger().info("  - ODTNo      =" + lastS4RcvdODTNo);
				LogUtility.getLogger().info("  - NozNo      =" + lastS4RcvdNozNo);
				LogUtility.getLogger().info("  - SystemTime =" + lastS4RcvdSystemTime);
				LogUtility.getLogger().info("  - TotalGauge =" + lastS4RcvdTotalGauge);
				LogUtility.getLogger().info(" 2.������� ���� ���� : ");
				LogUtility.getLogger().info("  - ODTNo      =" + wm.getNozzleNo());
				LogUtility.getLogger().info("  - NozNo      =" + wm.getConnectNozzleNo());
				LogUtility.getLogger().info("  - SystemTime =" + wm.getSystemTime());
				LogUtility.getLogger().info("  - TotalGauge =" + wm.getTotalGauge() + "\n");
				
			}
			else
			{
				lastS4RcvdODTNo = wm.getNozzleNo();
				lastS4RcvdNozNo = wm.getConnectNozzleNo();
				lastS4RcvdSystemTime = wm.getSystemTime();
				lastS4RcvdTotalGauge = wm.getTotalGauge();
				
				insertRecvQueue(wm);
			}
			
			//okSend_S4 = false;
			//}
			
			//okSend_SJ = true;
			m_pumpingLiter = "0";
			m_pumpingPrice = "0";

			LogUtility.getLogger().info("�����Ϸ� ���� ó�� (S4) ...........");
//			makeStatusInfo (5, nozNo); // �����Ϸ�
		}
		else if (command.equals("S8")) { //-- ��������
			S8_WorkingMessage wm = (S8_WorkingMessage) generateWorkingMessage(rcvBytes, "S8");
			insertRecvQueue(wm);
			
			if (wm.getDeviceType().equals("01") || wm.getDeviceType().equals("02")) { // ������ ���������� ���(01:�Ϲ�, 02:����)
//			if (nozType==1 || nozType==2) { // ������ ���������� ���(1:�Ϲ�, 2:����)
				m_nozState = Change.toValue(wm.getNozzleState());
				
//				if(m_nozState==1 || m_nozState==3) { // ����� ��
//					okSend_S4 = true; // S4 �۽Ű���
//				}
/*
				if(m_nozState==3 && okSend_SJ==true) { // �㰡�� �����
					SJ_WorkingMessage SJ_wm = new SJ_WorkingMessage();
					SJ_wm.setNozzleNo(wm.getNozzleNo());
					SJ_wm.setTotalGauge("0000000000");
					insertRecvQueue(SJ_wm); // �������� �ڷ� �۽�
					
					okSend_SJ = false; // SJ �۽źҰ���
				}
				*/
				
				if (m_nozState==231) { // ���� ���� ����ϋ� �ܰ� ������ �����Ѵ�.
										
					/*if (Change.toValue(m_pumpingLiter) <= 0 && Change.toValue(m_pumpingPrice) <= 0 && okSend_S4==true) { // 0�� 0����
						S4_WorkingMessage S4_wm = new S4_WorkingMessage();
						S4_wm.setNozzleNo(wm.getNozzleNo());
						S4_wm.setLiter("0000000");
						S4_wm.setBasePrice(m_basePrice);
						S4_wm.setPrice("00000000");
						S4_wm.setTotalGauge("0000000000");
						insertRecvQueue(S4_wm); // �����Ϸ� �ڷ� �۽�

						okSend_SJ = true;
						okSend_S4 = false;
						m_pumpingLiter = "0";
						m_pumpingPrice = "0";
					}*/
//					����ܰ� ó�� 
					if (isChangeEnvData == true) {
						
						TxQue.enQueue(P5_Buf);
						LogUtility.getLogger().info(wm.getConnectNozzleNo()+ " ODT �ܰ� ����");
						isChangeEnvData = false;
					}
				}
//				else if (m_nozState==231) {
//					makeStatusInfoAll(2); // ����ٿ�
//				}
			}
			
		}
		else if (command.equals("GT")) { //-- �ǸſϷ�
			GT_WorkingMessage wm = (GT_WorkingMessage) generateWorkingMessage(rcvBytes, "GT");			

			if (wm.getNozzleNo().equals(lastGTRcvdODTNo) && wm.getConnectNozzleNo().equals(lastGTRcvdNozNo) && 
					wm.getCreatedTime().equals(lastGTRcvdCreatedTime)) {
				
				LogUtility.getLogger().info("���� GT ���� ����! -> ������� ����...");
				LogUtility.getLogger().info(" 1.�������� ���� ���� : ");
				LogUtility.getLogger().info("  - ODTNo      =" + lastGTRcvdODTNo);
				LogUtility.getLogger().info("  - NozNo      =" + lastGTRcvdNozNo);
				LogUtility.getLogger().info("  - CreatedTime=" + lastGTRcvdCreatedTime);
				LogUtility.getLogger().info(" 2.������� ���� ���� : ");
				LogUtility.getLogger().info("  - ODTNo      =" + wm.getNozzleNo());
				LogUtility.getLogger().info("  - NozNo      =" + wm.getConnectNozzleNo());
				LogUtility.getLogger().info("  - CreatedTime=" + wm.getCreatedTime() + "\n");

				ClearUtil.setClearString(rcvBytes);
				rcvBytes=null;
				wm=null;
				
				return; // ������ ������� ����
			}
			else {
				lastGTRcvdODTNo = wm.getNozzleNo();
				lastGTRcvdNozNo = wm.getConnectNozzleNo();
				lastGTRcvdCreatedTime = wm.getCreatedTime();

				insertRecvQueue(wm);
			}
						
			LogUtility.getLogger().info("�ǸſϷ� ���� ó�� (GT) ...........");
			//makeStatusInfoAll(2); // �������� ����...
		}
		else if (command.equals("SE")) { // �̻�����
			SE_WorkingMessage wm = (SE_WorkingMessage) generateWorkingMessage(rcvBytes, "SE");
			insertRecvQueue(wm);
		}	
		else if (command.equals("BR")) { //-- ������ ���ڵ� ��û
			BR_WorkingMessage wm = (BR_WorkingMessage) generateWorkingMessage(rcvBytes, "BR");			
			
			if (wm.getNozzleNo().equals(lastBRRcvdODTNo) && wm.getConnectNozzleNo().equals(lastBRRcvdNozNo) && 
					wm.getPosReceiptNo().equals(lastBRPosReceiptNo)) {
				
				LogUtility.getLogger().info("���� BR ���� ����! -> ������� ����...");
				LogUtility.getLogger().info(" 1.�������� ���� ���� : ");
				LogUtility.getLogger().info("  - ODTNo       =" + lastBRRcvdODTNo);
				LogUtility.getLogger().info("  - NozNo       =" + lastBRRcvdNozNo);
				LogUtility.getLogger().info("  - PosReceiptNo=" + lastBRPosReceiptNo);
				LogUtility.getLogger().info(" 2.������� ���� ���� : ");
				LogUtility.getLogger().info("  - ODTNo       =" + wm.getNozzleNo());
				LogUtility.getLogger().info("  - NozNo       =" + wm.getConnectNozzleNo());
				LogUtility.getLogger().info("  - PosReceiptNo=" + wm.getPosReceiptNo() + "\n");

				ClearUtil.setClearString(rcvBytes);
				rcvBytes=null;
				wm=null;
				
				return; // ������ ������� ����
			}
			else {
				lastBRRcvdODTNo = wm.getNozzleNo();
				lastBRRcvdNozNo = wm.getConnectNozzleNo();
				lastBRPosReceiptNo = wm.getPosReceiptNo();

				insertRecvQueue(wm);
			}
		}
		else if (command.equals("SJ")) { //  ������ ��Ż ������ 
			SJ_WorkingMessage wm = (SJ_WorkingMessage) generateWorkingMessage(rcvBytes, "SJ");
			insertRecvQueue(wm);
		}
		else if(command.equals("PV")) {
			// SelfODT Version ����
			PV_WorkingMessage wm = (PV_WorkingMessage) generateWorkingMessage(rcvBytes, "PV");
			insertRecvQueue(wm);
		}		
	} 

	
	//--- ������ Polling ȣ�� (������ �۽� �� ����) ---//
	public void requestData() {

		Calendar cal = new GregorianCalendar();
		long currTime = cal.getTimeInMillis();
		
		try {		
//			m_sockServer.iStartInitCompletedFlag() : kixxhub �⵿�� odt �ʱ�ȭ �ϷῩ��(true:�Ϸ�, false:�̿Ϸ�)
			if (m_sockServer.isConnected() == false && m_sockServer.isStartInitCompletedFlag() == true) { // ȸ���ҷ� ó��
				if (issueLineErr==true) {
					makeLineError();
					TxQue.flushQueue();
					issueLineErr=false;
				}
			}
			
			if (m_sockServer.isConnected() == true) { // ȸ���ҷ� ���� ó��
				m_lastConnectedTime = currTime;
				if (issueLineErr==false) {
//					makeStatusInfoAll(2);
					issueLineErr=true;
				}
			}

			// ����Ȯ�� ó����
			long diff = currTime - m_lastConnectedTime;
			if (diff > 10000) { 				
				String nozNoList = "";
				for (String noz : m_nozNoVec) { //-- ������ ȸ���ҷ�
					nozNoList = nozNoList + noz + ", ";
				}
				LogUtility.getLogger().info("## Device disconnected! ODTNo=" + nozStr + " nozNo=" + nozNoList);
				m_lastConnectedTime = cal.getTimeInMillis();
			}
			
			// ȯ������ �ٿ�ε� Ȯ��(P1, P2, P5, P6, PC)
			rcvEnvDataOK = (rcvEnvDataCnt >= 4 ? true : false);	//������ⰳ�� ������Ʈ ȯ�漳�� �߰�
			m_sockServer.setRcvEnvDataOK(rcvEnvDataOK);
					
			// �������� ó��
			while (sndQue.getItemCount() > 0) {
				WorkingMessage wm = (WorkingMessage)sndQue.deQueue();
				LogUtility.getLogger().debug("## Received Down data : " + "ODTNo=" + nozStr + " command=" + wm.getCommand());
	
				if (processRecvWorkingMessage(wm)==true) { // true = skip �ϴ� ����
					wm = null;
					continue;
				}

				byte[] wmByte = generateByteStream(wm);
				if (wmByte != null) {
					TxQue.enQueue(wmByte); // ȯ�漳�������� �ƴϸ� ���� ť�� �߰�
				}
		
				wm = null;
			}

			//-- GSOcketServer ���� �� ODT �ʱ�ȭ ���� Ȯ��	
			if (m_sockServer != null && m_sockServer.isOdtInitCompleted() == true) { // ODT �ʱ�ȭ �Ϸ�
				
				//=== ������ �۽� ó���� ===//
				if (TxQue.isEmpty() == false) { //-- ���� ���� ������

					for (int i=0; i<TxQue.getItemCount(); i++) {
						TxBuf = TxQue.deQueue();					
						sendMessage(TxBuf);
					}
				}				
			}

			//Thread.sleep(10);

		} catch (Exception e) {
			LogUtility.getLogger().error("Exception occurr! (ODT=" + nozNo + ")");
			LogUtility.getLogger().error(e.getMessage(), e);
		}
				
	}
	

	public static void main(String[] args) throws IOException, Exception {

		//String 	ip = "211.232.30.188"; // ����
		String 		ip = "192.168.1.66"; // EMC

		int ODTNo = 71;
		//int ODTNo = 91;
		
		CommGSSelfODTi driver =  new CommGSSelfODTi(ODTNo, "000", ip);
		
		//driver.start();
		while(true) {
			Sleep.sleep(1000);
			driver.requestData();
		}
	}

//	public synchronized boolean isRcvEnvDataOK() {
//		return rcvEnvDataOK;
//	}
//
//	public synchronized void setRcvEnvDataOK(boolean rcvEnvDataOK) {
//		this.rcvEnvDataOK = rcvEnvDataOK;
//	}
	
}
