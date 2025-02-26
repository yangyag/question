/*
 * �����پ��뼿��(����)-ODT, �ҹ��� = 372
 * (�߰� 2012.08, dhp) 
 */ 
package com.gsc.kixxhub.device.pumpa.driverVersion;

import gnu.io.SerialPortEvent;

import java.util.Calendar;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Vector;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.BI_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.CA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.CB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HE_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P3_1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P5_1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P5_NozzleInfo;
import com.gsc.kixxhub.common.data.pump.P7_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.QL_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.QM_WoringMessage;
import com.gsc.kixxhub.common.data.pump.S4_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S8_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SE_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.TR_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.Base64Util;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.Change;
import com.gsc.kixxhub.device.pumpa.common.DataStruct;
import com.gsc.kixxhub.device.pumpa.common.Log;
import com.gsc.kixxhub.device.pumpa.common.SerialConnectException;
import com.gsc.kixxhub.device.pumpa.common.Sleep;
import com.gsc.kixxhub.device.pumpa.devDatas.TsnSelfHSDS;
import com.gsc.kixxhub.device.pumpa.driver.CommTsnSelfHS;
import com.gsc.kixxhub.device.pumpa.translation.TransTsnSelfHS;

public class CommTsnSelfHS_odt extends CommTsnSelfHS {

	protected byte ACK = 0x06;
	protected BI_WorkingMessage BI_wm;
	
	protected int		buffSize = 120;
	protected CB_WorkingMessage CB_wm;
	protected byte DC1 = 0x11;
	protected byte DC2 = 0x12;
	protected byte DC3 = 0x13;
	protected byte DC4 = 0x14;
	protected int	dispLevel=0; // 0=������ �޽��� + ��������� STX
    							 // 1=������ �޽��� + ��������� STX + ������� STX
	 							 // 2=������ �޽��� + ��������� STX + ����ۼ��� STX
	 							 // 3=��� �޽��� + ��� �ۼ��� STX
	
	protected byte ENQ = 0x05;
	protected byte EOB = 0x17;
	protected byte EOT = 0x04;
	protected byte ETX = 0x03;
	protected DataStruct FC_ds  = TsnSelfHSDS.getDS ("FC");  // �������� ����(�ż�����)
	
    protected boolean firstRequest=true;
    protected byte FS  = 0x1c;
    
    protected DataStruct HE_ds = new DataStruct();
    protected boolean	issueLineErr=true;

	protected byte[] 	JP_Buf = null;  // ������ �׷��� ����
	protected DataStruct JP_ds  = TsnSelfHSDS.getDS ("JP", 6);  // ������ �׷��� ����
    protected byte[] 	KP_Buf = null;  // ODT �ɼ�	
	protected byte[] 	lastPumpingData=new byte[buffSize];
	protected int		lineCommCheckCnt=0;
	protected int		lineErrCnt=0;
	protected String	m_basePrice="000000";
	protected Hashtable<String, String> m_basePriceTbl = new Hashtable<String, String>();
	protected String 	m_bonusCardNo = ""; 	// ���ʽ�ī��
	protected String 	m_bonusCardNo2 = ""; 	// ���ʽ�ī�� (�ܻ��û-2 ��)
	protected String 	m_cashTotalCount = "0"; // ��ü �����Աݾ�
	protected String 	m_custCardNo = ""; 		// ��ī��
	protected String 	m_custCardNo2 = ""; 	// ��ī�� (�ܻ��û-2 ��)
	protected String 	m_custType2 = ""; 	    // ��Ÿ�� (�ܻ��û-2 ��)
	protected String	m_downBasePrice="000000";
	//protected boolean 	m_changeBasePrice=false;
	protected boolean	m_isFullPumping=false;
	protected boolean m_isSaveSTX=false;
	protected String	m_liter="";
	protected int		m_nCustType=1; // 1=�Ϲ�
	protected boolean 	m_nozLock=false;
	protected Hashtable<String, String> m_nozStateTbl = new Hashtable<String, String>();
	protected String 	m_nozzleNo = "";

	protected Vector<String> m_nozzleVec = new Vector<String>();
	protected String	m_presetMode ="";
	
	protected String	m_price="";
	protected String 	m_realBasePrice="000000"; // �� �����ܰ�
	protected String 	m_realLiter="0000000";    // �� ������	
	protected String 	m_realPrice="00000000";   // �� �����ݾ�
	protected boolean 	m_recvedNozLock=false;
	protected String 	m_selectedPayType = ""; // ODT���� ������ ��������
	protected boolean m_sendEnvDataOK=true;
	protected boolean	m_setEnvDataOK=false;
	protected String 	m_sSaveBonus="0"; 	
	protected int		m_statusCode=0;
	protected String 	m_uploadedCommand = ""; // ODT�� ��û�� ����
	protected int	MAX_GAUGE_SJ  =5;

	protected int	MAX_LAST_PDATA=3;
	protected int	MAX_PROG4STEP =15;
	protected int	MAX_StateReq_Cnt=30; // �������� ��û�ֱ�(0=nothing)

    protected int	MAX_WAIT_PDATA=5;

	protected byte NAK = 0x15;
    
	protected short		nozState=0;
	protected byte[] 	OM_Buf = null;
	protected int		p0_cnt=0;
	protected P5_1_WorkingMessage P5_1_wm = null;
	protected byte[] 	PL_Buf = null;
	//progressStep : 0=�ʱ�ȭ��, 1=�ʱ�ȭ��, 2=���������۽���, 3=������, 4=�����Ϸ�, 
    //               5=���а���������/�����Ϸ��ڷ� �۽�
    protected int 	progressStep=3;
	protected int		progressStep4Cnt=0;
	
	protected QM_WoringMessage  QM_wm;
	protected byte[] 	RxBuf = new byte[buffSize];
	protected S4_WorkingMessage S4_wm;
	protected S8_WorkingMessage S8_wm;
	protected SE_WorkingMessage SE_wm;
	protected boolean sentNozDownInfo=false;

/*	protected DataStruct CQ_ds = new DataStruct();
	
	protected byte[] 	CQ_buf = new byte[6];*/
	
	protected String	SJ_TotalGauge="0000000000";
	protected byte[] 	SM_Buf = null;
	//    protected DataStruct KP_ds  = new DataStruct();  // ODT �ɼ�
//    protected DataStruct PL_ds  = new DataStruct();
//    protected DataStruct OM_ds  = new DataStruct();
    protected DataStruct SM_ds = new DataStruct();
	protected byte SOH = 0x01;
    protected byte STX = 0x02;
    protected TransTsnSelfHS trans = new TransTsnSelfHS(m_basePriceTbl);
    
protected byte[] 	TxBuf;
	protected int		waitGaugeForSJ_Cnt=0;
	    
	protected int		waitLastPData_Cnt=0;
	protected byte[][]  YL_Buf = new byte[4][];  // ���� ��������
	
	// �߰� 12��08��
	protected DataStruct YL_ds1 = TsnSelfHSDS.getDS ("YL", 3); // ���� ��������(Kixx)
	protected DataStruct YL_ds2 = TsnSelfHSDS.getDS ("YL", 3); // ���� ��������(KixxPrime) 
	protected DataStruct YL_ds3 = TsnSelfHSDS.getDS ("YL", 3); // ���� ��������(Diesel) 
	protected DataStruct YL_ds4 = TsnSelfHSDS.getDS ("YL", 3); // ���� ��������(AdvDiesel) 
    
    public CommTsnSelfHS_odt (int nozNum, String romVerStr) throws Exception {

		super(nozNum, romVerStr);
		
		Formatter form = new Formatter();
		nozNo = nozNum;
		romVer = romVerStr;

		form.format("%02d", nozNo);
		nozStr = form.toString();
		//nozByt = nozStr.getBytes();
		nozStr = TsnSelfHSDS.getOdtNo_forODT(nozStr);
    	nozID = nozStr.getBytes();
    	
		//--- prefix-ENQ ---//
		enq_Buf[0] = ENQ;
		enq_Buf[1] = nozID[0];
		enq_Buf[2] = nozID[1];
    			
    	try {
        	flushBuffer(lastPumpingData);
	    	       	
        	/*
        	 *  ���� ����ODT
        	 */
			//---  YL (��������)---//    		
        	YL_Buf[0] = YL_ds1.getByteStream();
        	YL_Buf[1] = YL_ds1.getByteStream();
        	YL_Buf[2] = YL_ds1.getByteStream();
        	YL_Buf[3] = YL_ds1.getByteStream();
    		
        	//--- JP (������ �׷��� ����)
        	JP_ds.addByte("SOH", SOH);
        	JP_ds.addString("nozNo", nozStr, 2);
        	JP_ds.addByte("STX", STX);
        	JP_ds.addString("command", "JP", 2);
        	JP_ds.addString("nozzleNo0", "00", 2);
        	JP_ds.addString("nozzleNo1", "00", 2);
        	JP_ds.addString("nozzleNo2", "00", 2);
        	JP_ds.addString("nozzleNo3", "00", 2);
        	JP_ds.addString("nozzleNo4", "00", 2);
        	JP_ds.addString("nozzleNo5", "00", 2);
        	JP_ds.addByte("ETX", ETX);
        	JP_ds.addByte("bcc", (byte) ' ');
        	JP_Buf = JP_ds.getByteStream();      

        	//--- KP (ODT �ɼ� ����)
        	//KP_Buf = KP_ds.getByteStream();

    	} catch (Exception e) {
    		LogUtility.getPumpALogger().error(e.getMessage(), e);
    	}

    }
 
	protected short checkRecvData (byte[] byData) throws Exception, SerialConnectException {
		
		if (compareNozID(byData) == false) { // NAK �Ⱥ���
			LogUtility.getPumpALogger().info("1.Recv STX NozID mismatch-1.0! (Noz=" + nozNo + ")");
			Log.datas(protectCardNumber(byData), byData.length, 20);

			trimInputStream("routine : 1");
			return 1; // �ݺ�
		}
		
		if (compareBCC(byData) == false) {
			sendText(NAK);
			LogUtility.getPumpALogger().info("1.Recv STX BCC fail! (Noz=" + nozNo + ")");
			Log.datas(protectCardNumber(byData), byData.length, 20);
			return 1; // �ݺ�
		}
		
		if (verifyData(byData) == false) {
			sendText(NAK);
			LogUtility.getPumpALogger().info("1.Recv STX Data verify fail! (Noz=" + nozNo + ")");
			Log.datas(protectCardNumber(byData), byData.length, 20);
			return 2; // ����
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
	
	protected byte[] convertReceiptForm (byte[] dat) {
		
		byte[] temp = new byte[dat.length+100];
		
		int i=0;
		int j=0;
		for (; i<dat.length-2; i++) {

			//-- carriageReturn (OD + 0A)
			if (dat[i]==0x0D)
				if (dat[i+1]==0x0A) {
					temp[j++] = 0x0A;
					//LogUtility.getPumpALogger().info("++++ carriageReturn");
					i+=1;
					continue;
				}
			
			//-- normalFontSize (1B + '!' + 00)
			if (dat[i]==0x1B)
				if (dat[i+1]==0x21)
					if (dat[i+2]==0x00) {
						temp[j++] = 0x1B;
						temp[j++] = 'y';
						temp[j++] = '0';
						//LogUtility.getPumpALogger().info("++++ normalFontSize i=" + i);	
						i+=2;
						continue;
					}
			
			//-- largeFontSize (1B + '!' + 10)
			if (dat[i]==0x1B)
				if (dat[i+1]==0x21)
					if (dat[i+2]==0x10) {
						temp[j++] = 0x1B;
						temp[j++] = 'y';
						temp[j++] = '1';
						//LogUtility.getPumpALogger().info("++++ largeFontSize i=" + i);	
						i+=2;
						continue;
					}
			
			//--------------------------------	

			//-- koreanLargeFontOff 
			if (dat[i]==0x1C)
				if (dat[i+1]==0x57)
					if (dat[i+2]==0x00) {
						temp[j++] = 0x1B;
						temp[j++] = 'y';
						temp[j++] = '0';
						//LogUtility.getPumpALogger().info("++++ koreanLargeFontOff i=" + i);	
						i+=2;
						continue;
					}

			//-- koreanLargeFontOn 
			if (dat[i]==0x1C)
				if (dat[i+1]==0x57)
					if (dat[i+2]==0x01) {
						temp[j++] = 0x1B;
						temp[j++] = 'y';
						temp[j++] = '1';
						//LogUtility.getPumpALogger().info("++++ koreanLargeFontOn i=" + i);	
						i+=2;
						continue;
					}

			//-- normalAlign 
			if (dat[i]==0x1B)
				if (dat[i+1]==0x61)
					if (dat[i+2]==0x00) {
//						temp[j++] = 0x20;
//						temp[j++] = 0x20;
//						temp[j++] = 0x20;
						//LogUtility.getPumpALogger().info("++++ normalAlign i=" + i);	
						i+=2;
						continue;
					}

			//-- centerAlign 
			if (dat[i]==0x1B)
				if (dat[i+1]==0x61)
					if (dat[i+2]==0x01) {
						temp[j++] = 0x20;
						temp[j++] = 0x20;
						temp[j++] = 0x20;
						temp[j++] = 0x20;
						temp[j++] = 0x20;
						//LogUtility.getPumpALogger().info("++++ centerAlign i=" + i);	
						i+=2;
						continue;
					}

			//-- rightAlign 
			if (dat[i]==0x1B)
				if (dat[i+1]==0x61)
					if (dat[i+2]==0x02) {
						temp[j++] = 0x20;
						temp[j++] = 0x20;
						temp[j++] = 0x20;
						temp[j++] = 0x20;
						temp[j++] = 0x20;
						temp[j++] = 0x20;
						temp[j++] = 0x20;
						temp[j++] = 0x20;
						temp[j++] = 0x20;
						temp[j++] = 0x20;
						temp[j++] = 0x20;
						temp[j++] = 0x20;
						temp[j++] = 0x20;
						//LogUtility.getPumpALogger().info("++++ rightAlign i=" + i);	
						i+=2;
						continue;
					}
						
			temp[j] = dat[i];
			j++;			
		}

		byte[] rtnBuf=new byte[j];
		System.arraycopy(temp, 0, rtnBuf, 0, j);
		
		return rtnBuf;
	}

	protected byte[] convertReceiptForm_org (byte[] dat) {
		
				
		for (int i=4; i<dat.length-2; i++) {
			if (dat[i]==0x1B || dat[i]==0x21 || dat[i]==0x00 || dat[i]==0x1B || dat[i]==0x10
							|| dat[i]==0x1C || dat[i]==0x57 || dat[i]==0x61 || dat[i]==0x01) { 
				//LogUtility.getPumpALogger().info("dat[" + i + "]=" + dat[i]);
				dat[i] = 0x20;
			}
		}
				
		return dat;
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
		
		
	protected String getCardTrack1Data (String cardNo) throws Exception {
		
		if (cardNo.contains("=")) 
			return cardNo.substring(0, cardNo.indexOf("="));
		else
			return cardNo;
	}
	
	protected String getCurrentDateTime () throws Exception, SerialConnectException {
		
		Calendar cal = new GregorianCalendar();
		Formatter form = new Formatter();
		form.format("%04d%02d%02d%02d%02d", cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH), 
				cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
		
		return form.toString();
	}
	
	// get from Prime
	protected void insertEnvDataToTxQue (byte[] envDat, String fileNo) throws Exception {


	}
	
	protected boolean isSkipWorkingMessage (WorkingMessage wm) throws Exception {
		
		boolean skip=false;
		
		/*
		 * ���� ����ODT
		 */
		if (wm.getCommand().equals("P5_1")) { // ���� ȯ�漳��(��������)
			
			P5_1_wm = (P5_1_WorkingMessage) wm;
			//skip=false;
			
			LogUtility.getPumpALogger().info("[Pump A][����-�پ��뼿��(����)ODT ����/�ܰ� ����]" + " nozzle=" + 
					P5_1_wm.getNozzleNo() + "("+P5_1_wm.getCommand()+")" +
					" | mode=" + P5_1_wm.getMode() + 
					" | useFullPumping=" + P5_1_wm.getUseFullPumping());
			
			if (P5_1_wm.getMode().equals("0")) {// �ʱ�ȭ

				skip = true;
				m_setEnvDataOK=true;
				
				String odtNo = TsnSelfHSDS.getOdtNo_forODT (P5_1_wm.getNozzleNo());

				//-- ������ �׷������� ����
				Vector<P5_NozzleInfo> nozzleInfoVector = P5_1_wm.getNozzleInfo();

				DataStruct JP_ds = new DataStruct();
				JP_ds.addByte("soh", SOH);
				JP_ds.addString("odtNo", odtNo, 2);
				JP_ds.addByte("stx", STX);				
				JP_ds.addString("command", "JP", 2);
				for (int i=0; i < nozzleInfoVector.size(); i++) {
					P5_NozzleInfo nozzleInfo = nozzleInfoVector.get(i);	
					JP_ds.addString("nozzleNo" + i, nozzleInfo.getNozzleNumber(), 2);
					m_nozzleVec.add (nozzleInfo.getNozzleNumber());
					m_nozStateTbl.put(nozzleInfo.getNozzleNumber(), "651"); //����ٿ�
				}
				JP_ds.addByte("etx", ETX);
				JP_ds.addByte("bcc", (byte) 0x20);

				JP_Buf = JP_ds.getByteStream();
				
				//-- �������� ó��
				byte[][] wmByte = generateByteStreams(P5_1_wm); // ODT �� 4���� ������ ��������					
				if (wmByte != null) {
					saveOilInfo_YL(wmByte);  // �������� ���� -> �ʱ�ȭ�� ����
				}
				
				//-- SM����
				DataStruct SM_ds = new DataStruct();
				SM_ds.addByte("soh", SOH);
				SM_ds.addString("odtNo", odtNo, 2);
				SM_ds.addByte("stx", STX);
				SM_ds.addString("command", "SM", 2);
				SM_ds.addString("date", getCurrentDateTime(), 12); // ???		
				SM_ds.addByte("etx", ETX);
				SM_ds.addByte("bcc", (byte) 0x20);

				SM_Buf = SM_ds.getByteStream();	
				
				if (m_sendEnvDataOK == true) { // ����Ʈ������ PJT �߰� -> ���� �ѹ� �ʱ�ȭ ���� ����					
					sendInitData();					
					m_sendEnvDataOK = false;
				}
			} 
			else if (P5_1_wm.getMode().equals("1")) { // �ܰ�����

				skip = true;
				//m_changeBasePrice=true;
	
				Vector<P5_NozzleInfo> nozzleInfoVector = P5_1_wm.getNozzleInfo();

				for (int i=0; i < nozzleInfoVector.size(); i++) {
					P5_NozzleInfo nozzleInfo = nozzleInfoVector.get(i);
			
					String nozzleNo  = nozzleInfo.getNozzleNumber();
					m_basePriceTbl.put(nozzleNo, nozzleInfo.getBasePrice());
					LogUtility.getPumpALogger().info("�ܰ������ P5���� �� �ܰ�����, nozzleNo=" + nozzleNo + ", base_price=" + nozzleInfo.getBasePrice());
		
				}
				
				send_P3_1_workingMessage (); // ���� P3 ��������

				//LogUtility.getPumpALogger().info("�ܰ������ P5����, �ܰ�����. ODT_No="+P5_1_wm.getNozzleNo());
			}
			else if (P5_1_wm.getMode().equals("2")) { // �������� �ɼǺ���

				skip = true;
				//m_useFullPumping = P5_1_wm.getUseFullPumping().equals("1")? true : false;
				FC_ds.editString("useFullPumping", P5_1_wm.getUseFullPumping(), 1);
				
				byte[] fc_buf = FC_ds.getByteStream();
				TxQue.enQueue(fc_buf);
				
				LogUtility.getPumpALogger().info("�������� �ɼǺ���� P5����, �ɼǺ���. ODT_No="+P5_1_wm.getNozzleNo() +
						" useFullPumping=" + P5_1_wm.getUseFullPumping());
								
			}
		}
		else if (wm.getCommand().equals("CB")) { // ������ Ȯ������
			
			CB_wm = (CB_WorkingMessage) wm;
				
			LogUtility.getPumpALogger().info("[Pump A][����-�پ��뼿��(����)ODT ������ Ȯ������]" + " nozzle=" + 
									CB_wm.getConnectNozzleNo() + "("+CB_wm.getCommand()+")" +
									" | ODT_No=" + CB_wm.getNozzleNo() +
									" | custType =[" + CB_wm.getCustomerType() + "]" + 
									" | saveBonus=" + CB_wm.getSaveBonus());
				
			m_custType2 = CB_wm.getCustomerType();
			m_nCustType = Change.toValue(CB_wm.getCustomerType());
			m_sSaveBonus  = CB_wm.getSaveBonus();
			
			String odtNo = TsnSelfHSDS.getOdtNo_forPOS((String) HE_ds.getValue("odtNo"));
			String targetNozzleNo = (String) HE_ds.getValue("nozzleNo");
			
			switch (m_nCustType) {
			
				case 0 : LogUtility.getPumpALogger().info("### ������ Ȯ�� ����. (ODT="+nozNo+")");
				
						// ���ν��� ��������
						if (m_uploadedCommand.equals("KK"))							
							sendFailMessageToODT(CB_wm.getMessage(), odtNo);
						else		
							sendFailMessageToODT("ī������ ����ġ!", odtNo);
				
						break;
						
				case 1 : LogUtility.getPumpALogger().info("### �Ϲ�(�ſ�ī��) ���Դϴ�. (ODT="+nozNo+")");
				
						if (m_uploadedCommand.equals("KD")) 
							requestCardApprove("1", "1", ""); // ���ο�û						
						else 	// ��û�� �ܻ��� ��� �̵�� ī�尡 �ſ����� ������ ���
							sendFailMessageToODT("��ī�尡 �ƴմϴ�!", odtNo);
						
						break;
						
				case 2 : LogUtility.getPumpALogger().info("### ���ݰŷ�ó ���Դϴ�. (ODT="+nozNo+")");
								
						if (m_uploadedCommand.equals("KK")) {
							// ����ODT �ܻ��ÿ�û�� ���� �ܻ������Ա� �㰡���� ����
							DataStruct KI_ds = new DataStruct();
							KI_ds.addByte("soh", SOH);
							KI_ds.addString("odtNo", odtNo, 2);
							KI_ds.addByte("stx", STX);
	
							KI_ds.addString("command", "KI", 2);
							KI_ds.addString("trNo", "", 4);		// �ŷ���ȣ
							KI_ds.addString("wcc", "A", 1);		// 'A'
							String basePrice = m_basePriceTbl.get(targetNozzleNo).substring(0,4);
							KI_ds.addString("basePrice1", basePrice, 4); // ������ �ܰ�
							KI_ds.addString("basePrice2", basePrice, 4); // ������ �ܰ�
							KI_ds.addString("liter", m_liter, 7);		 // ����(�Ҽ��� 3�ڸ�)
							KI_ds.addString("price", m_price, 7);		 // �ݾ�
							
							KI_ds.addByte("etx", ETX);
							KI_ds.addByte("bcc", (byte) 0x20);
							
							byte[] KI_Buf = KI_ds.getByteStream();							
							TxQue.enQueue(KI_Buf);	
						}
						else
							sendFailMessageToODT("�ſ�ī�尡 �ƴմϴ�!", odtNo);
							
						break;
						
				case 3 : LogUtility.getPumpALogger().info("### �ܻ�ŷ�ó ���Դϴ�. (ODT="+nozNo+")");

						if (m_uploadedCommand.equals("KK")) 
							requestCardApprove("1", "3", ""); // ���ο�û
						else
							sendFailMessageToODT("�ſ�ī�尡 �ƴմϴ�!", odtNo);
								
						break;
						
				case 4 : LogUtility.getPumpALogger().info("### �ܻ�ŷ�ó-���� ���Դϴ�. (ODT="+nozNo+")");

						if (m_uploadedCommand.equals("KK")) 
							requestCardApprove("1", "4", ""); // ���ο�û
						else
							sendFailMessageToODT("�ſ�ī�尡 �ƴմϴ�!", odtNo);
						
						break;
			}
			
			skip = true;

		}
		else if (wm.getCommand().equals("QM")) { // ī�� ��������
			QM_wm = (QM_WoringMessage) wm;
			
			LogUtility.getPumpALogger().info("[Pump A][����-�پ��뼿��(����)ODT ��������]" + " nozzle=" + 
					QM_wm.getConnectNozzleNo() + "("+QM_wm.getCommand()+")" +
					" | ODT_No=" + QM_wm.getNozzleNo() +
					" | mode=" + QM_wm.getMode());
			
			LogUtility.getPumpALogger().info(new StringBuffer("\n����-�پ��뼿��(����)ODT �������� ����(QM)" )
					.append("\n ODT_No    =" ).append( QM_wm.getNozzleNo() )
					.append("\n nozzle    =" ).append( QM_wm.getConnectNozzleNo() )
					.append("\n ���ΰ��  =" ).append( ( QM_wm.getMode().equals("1") ? "����" : "����") )
					.append("\n liter     =" ).append( QM_wm.getLiter() )
					.append("\n basePrice =" ).append( QM_wm.getBasePrice() )
					.append("\n price     =" ).append( QM_wm.getPrice() )
					.append("\n driveName =" ).append( QM_wm.getDriveName() )
					.append("\n carNo     =" ).append( QM_wm.getCarNo() )
					.append("\n cardAdjInd=" ).append( QM_wm.getCardAdjInd() ).append("(01:������, 02:������, 03:�ŷ�ó��, 04:1ȸ����, 05:�����Է�)" )
					.append("\n limitBase =" ).append( QM_wm.getLimitBase() ).append( "(01:����, 02:�ݾ�)" )
					.append("\n Limit     =" ).append( QM_wm.getLimit() )
					.append("\n accLimit  =" ).append( QM_wm.getAccLimit() )
					.append("\n message   =" ).append( QM_wm.getMessage() ).append("\n").toString());	
			
			QM_wm.print();
						
			if(QM_wm.getMode().equals("1") && !m_uploadedCommand.equals(IPumpConstant.TH_COMMAND_KK)) { // ������ �����㰡
				PB_WorkingMessage PB_wm = new PB_WorkingMessage();
				
				PB_wm.setNozzleNo(QM_wm.getNozzleNo());
				PB_wm.setConnectNozzleNo(wm.getConnectNozzleNo()); // Nozzle No
				PB_wm.setTargetNozzleNo(QM_wm.getConnectNozzleNo());
				PB_wm.setPassThrough(false);
				
				if (/*m_uploadedCommand.equals(IPumpConstant.TH_COMMAND_KK) ||  //-- �ܻ��û 
*/					m_uploadedCommand.equals(IPumpConstant.TH_COMMAND_KY) ||  //-- ���ݰŷ�ó(����)��û
					m_uploadedCommand.equals(IPumpConstant.TH_COMMAND_KV)) {  //-- ���ݰŷ�ó(�ſ�)��û
					// �������㰡�� ���񿡼� �ܰ�����(PC) �ʵ��� �ϱ�����
					PB_wm.setDirection(IPumpConstant.DIRECTION_FROM_ODT); 
				}
				
				PB_wm.setPrice(QM_wm.getPrice());
				PB_wm.setBasePrice(QM_wm.getBasePrice());
				PB_wm.setLiter(QM_wm.getLiter());
				
				if(Change.toValue(QM_wm.getPrice()) > 0)
					m_presetMode = "0"; // ���׼���
				else
					m_presetMode = "1"; // ��������
				
				PB_wm.setCommandSet(m_presetMode);
	
				insertRecvQueue(PB_wm); 
			}		
			
			DataStruct QM_ds = new DataStruct();
				
			boolean isApprove = QM_wm.getMode().equals("1") ? true : false;
			String basePrice = m_basePriceTbl.get(QM_wm.getConnectNozzleNo()); // �ܰ�(���ΰ�)
				
			if (isApprove == true) { //-- ���� ���� --//

				if (m_uploadedCommand.equals(IPumpConstant.TH_COMMAND_KD) ||  //-- �ſ��û
					m_uploadedCommand.equals(IPumpConstant.TH_COMMAND_KE) ||  //-- ���ݿ�û
					m_uploadedCommand.equals(IPumpConstant.TH_COMMAND_KX) ||  //-- ���ݿ�û(���ݿ�����)
					m_uploadedCommand.equals(IPumpConstant.TH_COMMAND_LD) ||  //-- GS����Ʈ ����
					m_uploadedCommand.equals(IPumpConstant.TH_COMMAND_LA)) {  //-- �ܻ��û-2
					
					if(basePrice.equals(QM_wm.getBasePrice())) { //--- ������
						QM_ds.addString("command", "KA", 2);	// Command
						QM_ds.addString("trNo", "", 4);			// �ŷ���ȣ
						if(m_presetMode.equals("0")) {
							QM_ds.addString("wcc", "A", 1);		// WCC
							QM_ds.addString("value", QM_wm.getPrice().substring(1,8), 7); // �ݾ�	
						}
						else {
							QM_ds.addString("wcc", "Q", 1);		// WCC
							QM_ds.addString("value", QM_wm.getLiter(), 7); // ����							
						}
					}
					else { //--- ����
						QM_ds.addString("command", "KB", 2);	// Command
						QM_ds.addString("trNo", "", 4);			// �ŷ���ȣ
						if(m_presetMode.equals("0")) 
							QM_ds.addString("wcc", "A", 1);		// WCC
						else
							QM_ds.addString("wcc", "Q", 1);		// WCC

						QM_ds.addString("basePrice1", basePrice.substring(0,4), 4);		// ������ �ܰ�
						QM_ds.addString("basePrice2", QM_wm.getBasePrice().substring(0,4), 4);	// ������ �ܰ�
						QM_ds.addString("liter", QM_wm.getLiter(), 7);					// ����(�Ҽ��� 3�ڸ�)
						QM_ds.addString("price", QM_wm.getPrice().substring(1,8), 7); 	// �ݾ�	
						QM_ds.addString("message", QM_wm.getMessage(), 48); 			// �޽���	
					}
				}		
				else if (m_uploadedCommand.equals(IPumpConstant.TH_COMMAND_KK)) { //-- �ܻ��û - 1
					
					//String cmd = m_uploadedCommand.equals(IPumpConstant.TH_COMMAND_KK) ? "KJ" : "KB";
					String cmd = "LS"; // �ŷ�ó����(�ѵ�����)

					// temp data
//					QM_wm.setLimitBase("02");
//					QM_wm.setLimit("1000000");
//					QM_wm.setAccLimit("600000");					
		
					QM_ds.addString("command", cmd, 2);		// Command
					QM_ds.addString("trNo", "", 4);			// �ŷ���ȣ
					if(m_presetMode.equals("0")) 
						QM_ds.addString("wcc", "A", 1);		// WCC
					else
						QM_ds.addString("wcc", "Q", 1);		// WCC

					QM_ds.addString("basePrice1", basePrice.substring(0,4), 4);		// ������ �ܰ�
					QM_ds.addString("basePrice2", QM_wm.getBasePrice().substring(0,4), 4);	// ������ �ܰ�
					QM_ds.addString("liter", QM_wm.getLiter(), 7);					// ����(�Ҽ��� 3�ڸ�)
					QM_ds.addString("price", QM_wm.getPrice().substring(1,8), 7); 	// �ݾ�	
					QM_ds.addString("driveName", QM_wm.getDriveName(), 20); 		// �����ڸ�(�ŷ�ó��)
					QM_ds.addString("carNo", QM_wm.getCarNo(), 18); 				// ������ȣ
					QM_ds.addString("cardAdjInd", QM_wm.getCardAdjInd(), 2);		// ������(01:������, ��Ÿ:����)
					QM_ds.addString("limitBase", QM_wm.getLimitBase(), 2); 			// �ѵ�����(01:����, 02:�ݾ�)
									
					String limit = "";
					String accLimit = "";
					if(QM_wm.getLimitBase().equals("01")) { // ����
						Float flo = new Float(QM_wm.getLimit());		
						float f_val = flo.floatValue();	
						int val = (int) (f_val * 1000);
						Formatter form = new Formatter();
						limit = (form.format("%018d", val)).toString();						

						Float flo2 = new Float(QM_wm.getAccLimit());		
						float f_val2 = flo2.floatValue();	
						int val2 = (int) (f_val2 * 1000);
						Formatter form2 = new Formatter();
						accLimit = (form2.format("%018d", val2)).toString();				
					} 
					else { // �ݾ�
						Formatter form = new Formatter();
						limit = (form.format("%018d", Change.toValue(QM_wm.getLimit()))).toString();
						
						Formatter form2 = new Formatter();
						accLimit = (form2.format("%018d", Change.toValue(QM_wm.getAccLimit()))).toString();							
					}
					QM_ds.addString("limit", limit, 18); 					// ���ѵ�
					QM_ds.addString("accLimit", accLimit, 18);				// ������뷮

					LogUtility.getPumpALogger().info("cardAdjInd==" + QM_ds.getValue("cardAdjInd"));
					LogUtility.getPumpALogger().info("limitBase===" + QM_ds.getValue("limitBase"));
					LogUtility.getPumpALogger().info("limit=======" + QM_ds.getValue("limit"));
					LogUtility.getPumpALogger().info("accLimit====" + QM_ds.getValue("accLimit"));
					
					QM_ds.addString("message", QM_wm.getMessage(), 48); 			// �޽���	
				}
				else if (m_uploadedCommand.equals(IPumpConstant.TH_COMMAND_KY) || 	//-- ���ݰŷ�ó(����)��û
						 m_uploadedCommand.equals(IPumpConstant.TH_COMMAND_KV)) { 	//-- ���ݰŷ�ó(�ſ�)��û
						
					//String cmd = m_uploadedCommand.equals(IPumpConstant.TH_COMMAND_KK) ? "KJ" : "KB";
					String cmd = "KB"; // �����㰡
		
					QM_ds.addString("command", cmd, 2);		// Command
					QM_ds.addString("trNo", "", 4);			// �ŷ���ȣ
					if(m_presetMode.equals("0")) 
						QM_ds.addString("wcc", "A", 1);		// WCC
					else
						QM_ds.addString("wcc", "Q", 1);		// WCC

					QM_ds.addString("basePrice1", basePrice.substring(0,4), 4);	// ������ �ܰ�
					QM_ds.addString("basePrice2", QM_wm.getBasePrice().substring(0,4), 4);	// ������ �ܰ�
					QM_ds.addString("liter", QM_wm.getLiter(), 7);					// ����(�Ҽ��� 3�ڸ�)
					QM_ds.addString("price", QM_wm.getPrice().substring(1,8), 7); 	// �ݾ�	
					QM_ds.addString("message", QM_wm.getMessage(), 48); // �޽���	
				}


				makeStatusInfo(235, m_nozzleNo); //���� �����
			}
			else { //-- ���� ���� --//
				QM_ds.addString("command", "KG", 2);				// Command
				QM_ds.addString("errorCode", "", 4); 				// ������ȣ 
				QM_ds.addString("message", QM_wm.getMessage(), 48); // �޽���(����, �ִ� 48 byte)
			}
				
			byte[] tempArray = QM_ds.getByteStream();	
			byte[] QM_buf = trans.makeProtocol(tempArray, QM_wm.getNozzleNo());
			
			TxQue.enQueue(QM_buf);

			// �������� �ʱ�ȭ
			m_uploadedCommand = ""; 
			m_price ="";
			m_liter = "";
			m_isFullPumping = false;
			m_presetMode = "";

			skip=true;
		}
		else if (wm.getCommand().equals("S4")) { // �����Ϸ�
			S4_wm = (S4_WorkingMessage) wm;
			
			LogUtility.getPumpALogger().info("[Pump A][����-�پ��뼿��(����)ODT �����Ϸ�]" + " nozzle=" + 
					S4_wm.getNozzleNo() + "("+S4_wm.getCommand()+")" +
					" | ODT_No=" + S4_wm.getConnectNozzleNo() +
					" | price=" + S4_wm.getPrice() + 
					" | basePrice=" + S4_wm.getBasePrice() +
					" | Liter=" + S4_wm.getLiter());
			
			S4_wm.print();
			
			// TR ����������
			m_realPrice     = S4_wm.getPrice();
			m_realLiter     = S4_wm.getLiter();
			m_realBasePrice = S4_wm.getBasePrice();

			skip=true;
		}
		else if (wm.getCommand().equals("QL")) { // ���������
			QL_WorkingMessage QL_wm = (QL_WorkingMessage) wm;

			LogUtility.getPumpALogger().info("[Pump A][����-�پ��뼿��(����)ODT ���������" + " nozzle=" + 
										 QL_wm.getConnectNozzleNo() + "("+QL_wm.getCommand()+")" +
										 " | ODT_No=" + QL_wm.getNozzleNo() +
										 " | mode=" + QL_wm.getMode() +
										 " | alarmState=" + QL_wm.getAlarmState() +
										 " | length=" + QL_wm.getContent().length());
			
			//QL_wm.print();
									
			String odtNo = TsnSelfHSDS.getOdtNo_forODT (P5_1_wm.getNozzleNo());
			
			//-- ������ ���˺�ȯ
			byte[] byContent = convertReceiptForm(QL_wm.getContent().getBytes());
			
			int maxLen1Page = 950;		
			int n_totalPage = (byContent.length / maxLen1Page) + 2;
			String totalPage = Change.toString(n_totalPage);
						
			int i=0;
			int offset=0;
			
			for (; i<n_totalPage - 1; i++) {
				
				//DataStruct PS_ds = TsnSelfHSDS.getDS(IPumpConstant.TH_COMMAND_PS); // �ʵ�
				DataStruct PS_ds = new DataStruct();
				PS_ds.addByte("soh", SOH);
				PS_ds.addString("odtNo", odtNo, 2);
				PS_ds.addByte("stx", STX);
	
				PS_ds.addString("command", IPumpConstant.TH_COMMAND_PS, 2);
				PS_ds.addString("trNo", "" ,4);							// �ŷ���ȣ
				PS_ds.addString("currPage", Change.toString(i + 1), 1);	// ����������
				PS_ds.addString("totPage", totalPage, 1);				// ��ü������
				int startIdx = Math.min((i * maxLen1Page) + offset, byContent.length);
				int endIdx   = Math.min((i+1) * maxLen1Page, byContent.length);
								
				// �������κ��� CR(0x0A) �ƴϸ� �ִ� 100 �ڸ����� �Ѿ
				for(int k=0; k<100 && endIdx < byContent.length; k++) {
					if(byContent[endIdx]==0x0A) {
						endIdx++;
						offset++;
						break;
					} else {
						endIdx++;
						offset++;
					}
				}
								
				int len = endIdx - startIdx;
				byte[] byt = new byte[len];
				System.arraycopy(byContent, startIdx, byt, 0, len);
				String str = new String(byt);
				PS_ds.addString("content", str, str.getBytes().length);	// ������ ������			
				
				PS_ds.addByte("etx", ETX);
				PS_ds.addByte("bcc", (byte) 0x20);

				TxQue.enQueue(PS_ds.getByteStream());
			}			
			
			//--- ���ڵ� �� ������ ����ó��(Feed and Cutting)
			DataStruct PS_ds = new DataStruct();
			PS_ds.addByte("soh", SOH);
			PS_ds.addString("odtNo", odtNo, 2);
			PS_ds.addByte("stx", STX);

			PS_ds.addString("command", IPumpConstant.TH_COMMAND_PS, 2);
			PS_ds.addString("trNo", "" ,4);							// �ŷ���ȣ
			PS_ds.addString("currPage", Change.toString(i + 1), 1);	// ����������
			PS_ds.addString("totPage", totalPage, 1);				// ��ü������

			String barcode ="";
			if (QL_wm.getBarCode().length() > 0) {
				byte[] byCmd = {0x0A, 0x0A, 0x1B, 'b'}; 
				String cmd = new String(byCmd) + Change.toString("%02d", QL_wm.getBarCode().length());
				barcode = cmd + QL_wm.getBarCode();
			}
													
			byte[] cut = {0x0A, 0x0A, 0x0A, 0x0A, 0x1B, 'm'}; // LineFeed + PartialCut
			String tailStr = barcode + new String(cut);
			PS_ds.addString("content", tailStr, tailStr.getBytes().length);	// ������ ������	

			PS_ds.addByte("etx", ETX);
			PS_ds.addByte("bcc", (byte) 0x20);

			TxQue.enQueue(PS_ds.getByteStream());

			//----- TR ���� ���� -----//
			TR_WorkingMessage TR_wm = new TR_WorkingMessage();
			TR_wm.setNozzleNo(QL_wm.getNozzleNo());
			TR_wm.setConnectNozzleNo(QL_wm.getConnectNozzleNo());
			TR_wm.setLiter(m_realLiter);
			TR_wm.setBasePrice(m_realBasePrice);
			TR_wm.setPrice(m_realPrice);
			
			insertRecvQueue(TR_wm); //���� ����
			
			makeStatusInfo(231, m_nozzleNo); // ��������(�ʱ�)

			skip=true;
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
		else if (wm.getCommand().equals("SE")) { //--- ������ �̻�����
			SE_WorkingMessage SE_wm = (SE_WorkingMessage) wm;		
			
			if(SE_wm.getStatusCode().substring(0,1).equals("6")) { // 6xx = ��������
				m_nozStateTbl.put(SE_wm.getConnectNozzleNo(), SE_wm.getStatusCode()); // ȸ���ҷ�	
				LogUtility.getPumpALogger().debug("Recv nozState. noz=" + SE_wm.getConnectNozzleNo() + " nozState=" + m_nozStateTbl.get(SE_wm.getConnectNozzleNo()));

			}
		}
		else if (wm.getCommand().equals("S8")) { //--- ������ ��������
			S8_WorkingMessage S8_wm = (S8_WorkingMessage) wm;		
			
			if(S8_wm.getStatusCode().substring(0,1).equals("6")) { // 6xx = ��������
				m_nozStateTbl.put(S8_wm.getConnectNozzleNo(), S8_wm.getStatusCode()); // ��������	
				LogUtility.getPumpALogger().debug("Recv nozState. noz=" + S8_wm.getConnectNozzleNo() + " nozState=" + m_nozStateTbl.get(S8_wm.getConnectNozzleNo()));

			}
		}

/*		Enumeration<String> enum1 = m_nozStateTbl.elements();
		while (enum1.hasMoreElements()) {
			String str = enum1.nextElement();
			LogUtility.getPumpALogger().debug("m_nozStateTbl value============" + str);
		}*/
		
		return skip;
	}
	
	protected void makeLineError () throws Exception {
		
		SE_wm = new SE_WorkingMessage();
		
		SE_wm.setNozzleNo(Change.toString("%02d", nozNo));
		SE_wm.setDeviceType(Change.toString("%02d", nozType));
		SE_wm.setStatus("1");
		SE_wm.setStatusCode("601"); // ȸ���ҷ�
		SE_wm.setErrMsg("����ODT ȸ���ҷ�");

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
			
	
	protected void makeStatusCode(byte[] buf) throws Exception {

		int	statusCode=0;
		int len = getBufferLength(buf);
		byte[] byt = new byte[2];
		
		//Log.datas(buf, buf.length, 20);
		//LogUtility.getPumpALogger().info("len======>>>>" + len);
		
		if(len == 10) {
			
			if (buf[6] == 'S') {
				statusCode = 231; //�������� ���
				for(int i=0; i<m_nozzleVec.size(); i++) {
					m_nozzleNo = m_nozzleVec.get(i);
					makeStatusInfo(statusCode, m_nozzleNo);
				}
			}
			else if (buf[6] == 'D') {
				byte[] tmp = new byte[1];
				tmp[0] = buf[7];
				m_selectedPayType = new String(tmp);
				statusCode = 232; // �ݾ׼��ô��
				makeStatusInfo(statusCode, m_nozzleNo);
			}
			else {
				statusCode = 232; // �ݾ׼��ô��
				byt[0]=buf[6]; byt[1]=buf[7];
				m_nozzleNo = new String(byt);
				makeStatusInfo(statusCode, m_nozzleNo);
			}
		}
		else if(len == 11) {
			
			if (buf[8] == 'B') {
				statusCode = 234; // ���ʽ��Է´��
				byt[0]=buf[6]; byt[1]=buf[7];
				m_nozzleNo = new String(byt);
				makeStatusInfo(statusCode, m_nozzleNo);
			}
			else if (buf[8] == 'C') {
				statusCode = 233; // �����Է´��
				byt[0]=buf[6]; byt[1]=buf[7];
				m_nozzleNo = new String(byt);
				makeStatusInfo(statusCode, m_nozzleNo);
			}
		}
		else {
			statusCode = 233; // �����Է� ���
			byt[0]=buf[6]; byt[1]=buf[7];
			m_nozzleNo = new String(byt);
			makeStatusInfo(statusCode, m_nozzleNo);
		}
	}
	
	protected void makeStatusInfo(int nozState, String noz_no) throws Exception {
		
		S8_wm = new S8_WorkingMessage();
		int	statusCode=0;
		String errorMsg="";
		
		switch (nozState) {
			case 650:
				statusCode = 650; // ����
				errorMsg = "����";
				break;
			case 231:
				statusCode = 231;
				errorMsg = "�������� ���";
				break;
			case 232:
				statusCode = 232;
				errorMsg = "�ݾ׼��� ���";
				break;
			case 233:
				statusCode = 233;
				errorMsg = "�����Է� ���";
				break;
			case 234:
				statusCode = 234;
				errorMsg = "���ʽ�ī���Է� ���";
				break;
			case 235:
				statusCode = 235;
				errorMsg = "���� ���";
				break;
			case 236:
				statusCode = 236;
				errorMsg = "���ο�û ��";
				break;
		}

/*		if (m_statusCode==statusCode)
			return;*/
		
		m_statusCode = statusCode;	
				
		S8_wm.setNozzleNo(noz_no);
		S8_wm.setDeviceType(Change.toString("%02d", 2));
		S8_wm.setStatus("1");
		S8_wm.setStatusCode(Change.toString("%03d", statusCode)); 
		S8_wm.setNozzleState(Change.toString("%01d", 0)); 
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

	protected void processODT () throws SerialConnectException, Exception {
		
		if (m_setEnvDataOK == false)
			return;
		
		String recvCmd = trans.getCommand(RxBuf);
		
		if (recvCmd.equals(IPumpConstant.TH_COMMAND_GD)) { // ������ ������ ���ſϷ�
			//insertRecvQueue(generateWorkingMessage(RxBuf, "GD")); 
		} 
		// POS�� ���� ȯ������ ������ ���� 1�� Ȥ�� ODT �ʱⵥ���� ��û�� ODT�� ������ ���� ����
		else if (recvCmd.equals(IPumpConstant.TH_COMMAND_IQ)) { 
			
			LogUtility.getPumpALogger().info("�پ��뼿��ODT(����) �ʱ�ȭ ���� ����....");
			
			// ���� �ʱ�ȭ
			HE_ds = null;
			m_cashTotalCount = "0";
			m_custCardNo = "";
			m_bonusCardNo = "";
			m_uploadedCommand = ""; 
			m_price ="";
			m_liter = "";
			m_isFullPumping = false;
			
			sendInitData(); // ����Ʈ������ PJT ����

			/*//-- ������ �׷��� ����(JP) // ����Ʈ������ PJT ���� -> sendInitData() �� ����ó��
			TxQue.enQueue (JP_Buf); //			

			LogUtility.getPumpALogger().info("$$$$$$$$$$$$ TxQue.enQueue (JP_Buf)=\n");
			Log.datas(JP_Buf, JP_Buf.length, 20);
			
			//-- ��������(YL)
			if(YL_Buf[0].length > 22) TxQue.enQueue (YL_Buf[0]); // kixx
			if(YL_Buf[1].length > 22) TxQue.enQueue (YL_Buf[1]); // kixxPrime
			if(YL_Buf[2].length > 22) TxQue.enQueue (YL_Buf[2]); // diesel
			if(YL_Buf[3].length > 22) TxQue.enQueue (YL_Buf[3]); // advDiesel
			        	
			
			//-- ������ �ܰ����� ó��(����������� P3_1 ������ ���ŵ��� �����Ƿ�  P3_1 ������ �����⿡ ����)
			if(P5_1_wm != null)
				send_P3_1_workingMessage (); // ���� P3 ��������
			else
				LogUtility.getPumpALogger().info("P5 ���� �̼������� send_P3_1_workingMessage() �̽���!");
			
			//-- ODT �ɼ�����(KP)
			//TxQue.enQueue (KP_Buf); // kixx			
			
			//-- ��Ÿ����
			//TxQue.enQueue (PL_Buf); // 
			//TxQue.enQueue (OM_Buf); // 
			TxQue.enQueue (SM_Buf); // 
			*/
		} 
		else if (recvCmd.equals(IPumpConstant.TH_COMMAND_KF)) { // �����Ա� ����
			
			// ODT����  : �����پ���(����) �����Ա� ����
			// ������� :  WorkingMessage
			DataStruct KF_ds = TsnSelfHSDS.getDS("KF");
			
			// ODT ���� ������ ����
			KF_ds.setByteStream(RxBuf);

			String odtNo			= TsnSelfHSDS.getOdtNo_forPOS((String) KF_ds.getValue("odtNo"));
			String targetNozzleNo 	= (String) KF_ds.getValue("nozzleNo");		
						
			BI_WorkingMessage BI_wm = new BI_WorkingMessage();
				
			BI_wm.setNozzleNo(odtNo); 					// ODT
			BI_wm.setConnectNozzleNo(targetNozzleNo); 	// ����

			int cashTotalCount = Change.toValue((String) KF_ds.getValue("value"));
			int cashCount = cashTotalCount - Change.toValue(m_cashTotalCount); //  �̹��� - ������
			BI_wm.setCash(Change.toString("%08d", cashCount)); // ���Աݾ�
			
			m_cashTotalCount = "0" + (String) KF_ds.getValue("value");
			BI_wm.setCashCount(m_cashTotalCount); // �� ���Աݾ�

			insertRecvQueue(BI_wm);
			BI_wm.print();		
			
		} 
		//--- ���� ���ο�û ---//
		else if (recvCmd.equals(IPumpConstant.TH_COMMAND_KD) ||
				 recvCmd.equals(IPumpConstant.TH_COMMAND_KE) ||
				 recvCmd.equals(IPumpConstant.TH_COMMAND_KK) ||
				 recvCmd.equals(IPumpConstant.TH_COMMAND_KY) ||
				 recvCmd.equals(IPumpConstant.TH_COMMAND_KV) ||
				 recvCmd.equals(IPumpConstant.TH_COMMAND_KX) ||
				 recvCmd.equals(IPumpConstant.TH_COMMAND_LD) ||
				 recvCmd.equals(IPumpConstant.TH_COMMAND_LA)) { 
			
			// ODT����  : �����پ���(����) ����/�ſ�/���ʽ�/�ܻ� ����/��� ��û 
			// ������� : HE WorkingMessage
			//DataStruct HE_ds = null;
			
			m_uploadedCommand = recvCmd;
			
			if(recvCmd.equals(IPumpConstant.TH_COMMAND_KD)) // �ſ���ο�û
				HE_ds = TsnSelfHSDS.getDS("KD");
			else if(recvCmd.equals(IPumpConstant.TH_COMMAND_KE)) // ���ݽ��ο�û
				HE_ds = TsnSelfHSDS.getDS("KE");
			else if(recvCmd.equals(IPumpConstant.TH_COMMAND_KK)) // �ܻ���ο�û
				HE_ds = TsnSelfHSDS.getDS("KK");
			else if(recvCmd.equals(IPumpConstant.TH_COMMAND_KY)) // ���ݰŷ�ó(����)
				HE_ds = TsnSelfHSDS.getDS("KY");
			else if(recvCmd.equals(IPumpConstant.TH_COMMAND_KV)) // ���ݰŷ�ó(�ſ�)
				HE_ds = TsnSelfHSDS.getDS("KV");
			else if(recvCmd.equals(IPumpConstant.TH_COMMAND_KX)) // ���ݽ��ο�û-���ݿ����� ����
				HE_ds = TsnSelfHSDS.getDS("KX");
			else if(recvCmd.equals(IPumpConstant.TH_COMMAND_LD)) // GS����Ʈ ���ο�û
				HE_ds = TsnSelfHSDS.getDS("LD");
			else if(recvCmd.equals(IPumpConstant.TH_COMMAND_LA)) // �ܻ���ο�û-2
				HE_ds = TsnSelfHSDS.getDS("LA");
			
			//LogUtility.getPumpALogger().info("��������===" + recvCmd);
			
			// ODT ���� ������ ���� (KD�� KV �� �Һΰ����� �ڸ����� �����̹Ƿ� �� ó�� �ʿ�)
			if(recvCmd.equals(IPumpConstant.TH_COMMAND_KD)) {	

				int bufLen=getBufferLength(RxBuf);

				if(bufLen==106) // �Һΰ����� ����
					HE_ds.setByteStream(RxBuf);
				else { // �Һΰ����� ���� (length==104)
					
					byte[] buf = new byte[106];
					System.arraycopy(RxBuf, 0, buf, 0, 60);
					buf[60] = '0'; buf[61] = '0'; // �Һΰ����� 00 �� ����
					System.arraycopy(RxBuf, 60, buf, 62, 44);
					
					HE_ds.setByteStream(buf);
				}
			} 
			if(recvCmd.equals(IPumpConstant.TH_COMMAND_KV)) {	

				int bufLen=getBufferLength(RxBuf);

				if(bufLen==78) // �Һΰ����� ����
					HE_ds.setByteStream(RxBuf);
				else { // �Һΰ����� ���� (length==76)
					
					byte[] buf = new byte[78];
					System.arraycopy(RxBuf, 0, buf, 0, 74);
					buf[74] = '0'; buf[75] = '0'; // �Һΰ����� 00 �� ����
					System.arraycopy(RxBuf, 74, buf, 76, 2);
					
					HE_ds.setByteStream(buf);
				}
			} 
			else
				HE_ds.setByteStream(RxBuf);
			
			String odtNo = TsnSelfHSDS.getOdtNo_forPOS((String) HE_ds.getValue("odtNo"));
			String targetNozzleNo = (String) HE_ds.getValue("nozzleNo");			
			
			//--- ���� ���ο�û�� ������ Ȯ�ξ��� ���ο�û ��
			if (recvCmd.equals(IPumpConstant.TH_COMMAND_KE) || 		// ����
					recvCmd.equals(IPumpConstant.TH_COMMAND_KX)) { 	// ����(���ݿ�����)
				requestCardApprove("1", "0", recvCmd); // ���ο�û
			}
			//--- GS����Ʈ ���ο�û�� ������ Ȯ�ξ��� ���ο�û ��
			else if (recvCmd.equals(IPumpConstant.TH_COMMAND_LD)) { // GS����Ʈ
				requestCardApprove("3", "0", recvCmd); // ���ο�û
			}
			//--- ���ݰŷ�ó�� �̹� KK �������Ž� ������ ��ȸ �Ϸ��Ͽ����Ƿ� ���ο�û ��
			else if (recvCmd.equals(IPumpConstant.TH_COMMAND_KY) || // ���ݰŷ�ó(����)
					 recvCmd.equals(IPumpConstant.TH_COMMAND_KV)) { // ���ݰŷ�ó(�ſ�)
				requestCardApprove("1", "2", recvCmd); // ���ο�û
			}			
			//--- �ܻ��û-2(LA����)�� ���ο�û ��
			else if (recvCmd.equals(IPumpConstant.TH_COMMAND_LA)) { // �ܻ��û-2
				m_custCardNo  = m_custCardNo2;
				m_bonusCardNo = m_bonusCardNo2;				
				
				requestCardApprove("1", m_custType2, recvCmd); // ���ο�û

				m_custCardNo  = "";
				m_bonusCardNo = "";
				m_custCardNo2  = "";
				m_bonusCardNo2 = "";
				m_custType2 = "";
			}
			else { //--- ��Ÿ(KD:�ſ�, KK:�ܻ�)�� ������ Ȯ��
				
				if(recvCmd.equals(IPumpConstant.TH_COMMAND_KK)) { //-- ��������
					
					m_custCardNo  = (String) HE_ds.getValue("cardNo1");
					m_bonusCardNo = (String) HE_ds.getValue("cardNo2");
					
//test
//					LogUtility.getPumpALogger().info("[����] m_custCardNo : " + m_custCardNo);
//					LogUtility.getPumpALogger().info("[����] m_bonusCardNo : " + m_bonusCardNo);

					// �ܻ��û-2 ��
					m_custCardNo2  = m_custCardNo;
					m_bonusCardNo2 = m_bonusCardNo;
					
					if (HE_ds.getValue("saleType").equals("A")) {	// �ݾ�
						m_price = (String) HE_ds.getValue("value");
						m_isFullPumping = false;
						//m_presetMode = "0";
					} 
					else if (HE_ds.getValue("saleType").equals("Q")) {	// ����
						m_liter = (String) HE_ds.getValue("value");
						m_isFullPumping = false;
						//m_presetMode = "1";
					} 
					else if (HE_ds.getValue("saleType").equals("F")) {	// ��������
						m_price = (String) HE_ds.getValue("value");
						m_isFullPumping = true;
						//m_presetMode = "0";
					}
				}
				
				// ������ Ȯ�ο�û
				CA_WorkingMessage CA_wm = new CA_WorkingMessage();
				CA_wm.setNozzleNo(odtNo); // ODTNo
				CA_wm.setConnectNozzleNo(targetNozzleNo);
				String cardNo = (String) HE_ds.getValue("cardNo1");
				CA_wm.setCardNo(cardNo);

				LogUtility.getPumpALogger().info(new StringBuffer( "\n�پ��뼿��ODT(����) ������ Ȯ�ο�û(CA)" )
											 .append("\n ODT_No=" ).append( CA_wm.getNozzleNo() ) 
											 .append("\n nozzle=" ).append( CA_wm.getConnectNozzleNo() ).append("(CA)" )
											 //.append("\n cardNo=" ) Base64Util.encode(getCardTrack1Data(cardNo)) ) "\n");
											 .append("\n cardNo=" ).append( Base64Util.encode(getCardTrack1Data(cardNo)) ).append( "\n").toString());
	
				insertRecvQueue(CA_wm);
				CA_wm.print();	
			
			}				
		}
		//--- ODT �������� ---//
		else if (recvCmd.equals(IPumpConstant.TH_COMMAND_OT)) {

			DataStruct OT_ds = TsnSelfHSDS.getDS(IPumpConstant.TH_COMMAND_OT);	
			OT_ds.setByteStream(RxBuf); // ODT ���� ������ ����			
			//makeStatusCode((String) OT_ds.getValue("datas"));		
			makeStatusCode(RxBuf);
		}
/*		//--- ODT �̻����� ---//
		else if (recvCmd.equals(IPumpConstant.TH_COMMAND_ES)) {

			DataStruct ES_ds = TsnSelfHSDS.getDS(IPumpConstant.TH_COMMAND_ES);	
			ES_ds.setByteStream(RxBuf); // ODT ���� ������ ����			
			makeStatusInfo(Change.toValue((String) ES_ds.getValue("status")));
		}*/
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
		else { // Self ODT ���� ���� �� ó��
			if(RxBuf[1]=='S')
				processODT();
		}

//		LogUtility.getPumpALogger().debug("#### STX Start : nozzle=" + nozStr + " ProgStep=" +
//				progressStep + " nozState=" + nozState + " pumpingStart=" + pumpingStart);
		
	}
	
	// get from Prime
	protected byte[] protectCardNumber (byte[] buf) throws Exception, SerialConnectException {
		
		byte[] dat = buf.clone();
		
		if ((dat[4]=='K' && dat[5]=='D') ||
			(dat[4]=='K' && dat[5]=='K') ||
			(dat[4]=='K' && dat[5]=='E') ||
			(dat[4]=='K' && dat[5]=='X')) { // ���ο�û
			
			for (int i=20; i<20+40; i++) // ī��1
				dat[i] = '*';

			for (int i=20+40+2; i<20+40+2+40; i++) // ī��2
				dat[i] = '*';
		}
		else if ((dat[4]=='K' && dat[5]=='V')) {
			for (int i=36; i<36+38; i++) // ī��1
				dat[i] = '*';
		}
		else if ((dat[4]=='L' && dat[5]=='D')) {
			for (int i=20; i<20+40+17; i++) // ī��1
				dat[i] = '*';
		}
		
		return dat;
	}

	protected boolean requestCardApprove (String cardType, String custType, String recvCmd) throws Exception {
						
		String odtNo = TsnSelfHSDS.getOdtNo_forPOS((String) HE_ds.getValue("odtNo"));
		String targetNozzleNo = (String) HE_ds.getValue("nozzleNo");
		int nCardType = Change.toValue(cardType);
		m_nCustType = Change.toValue(custType);
		
		// ������ ȸ���ҷ��̸� ���ν��� ó��
		if(m_nozStateTbl.size() > 0 && m_nozStateTbl.get(targetNozzleNo).equals("601")) {
			sendFailMessageToODT("������ ȸ���ҷ�!", odtNo);
			return false;
		}
		
		HE_WorkingMessage HE_wm = new HE_WorkingMessage();
		
		HE_wm.setNozzleNo(odtNo); 					// ODT
		HE_wm.setConnectNozzleNo(targetNozzleNo); 	// ����
		HE_wm.setCardType(cardType); 				// 1 : ���ο�û

//test
		
//		LogUtility.getPumpALogger().info("[����] nCardType : " + nCardType);
//		LogUtility.getPumpALogger().info("[����] m_nCustType : " + m_nCustType);
//		LogUtility.getPumpALogger().info("[����] m_custCardNo : " + m_custCardNo);
//		LogUtility.getPumpALogger().info("[����]  HE_ds.getValue(cardNo1) : " +  HE_ds.getValue("cardNo1"));
//		LogUtility.getPumpALogger().info("[����] m_selectedPayType : " + m_selectedPayType);
		switch (nCardType) {
		
			case 1 : // �ſ�/����/��ī��
				switch (m_nCustType) {
				
					//-- �Ϲݰ�(����)
					case 0 : 
						HE_wm.setCustomerType("");
						
						if(recvCmd.equals("KE")) { 		// ���ݿ�û
							HE_wm.setBonusCard((String) HE_ds.getValue("cardNo1"));     // ���ʽ�ī��
							HE_wm.setCashCount(m_cashTotalCount); 						// ���Աݾ�
						}
						else if(recvCmd.equals("KX")) { // ���ݿ�û(���ݿ�����)
							
							String target = (String) HE_ds.getValue("target");
							String loyaltyType = "";
							if(target.equals("1"))
								loyaltyType = "0"; // �Һ���
							else if(target.equals("2"))
								loyaltyType = "1"; // �����
							
							HE_wm.setLoyaltyType(loyaltyType);
							HE_wm.setBonusCard((String) HE_ds.getValue("cardNo1"));     // ���ʽ�ī��
							HE_wm.setCashReceiptNo((String) HE_ds.getValue("cardNo2")); // ���ݿ�����ī��
							HE_wm.setCashCount(m_cashTotalCount); 						// ���Աݾ�
						} 
						break;
				
					//-- �Ϲݰ�(�ſ�ī��)
					case 1 : 
						HE_wm.setCustomerType("1");
						HE_wm.setCardNumber((String) HE_ds.getValue("cardNo1")); 	// �ſ�ī��
						HE_wm.setBonusCard((String) HE_ds.getValue("cardNo2"));	 	// ���ʽ�ī��
						HE_wm.setMonthCount((String) HE_ds.getValue("monthCnt"));	// �Һΰ�����
						break;
		
					//-- ���ݰŷ�ó��
					case 2 : 
						HE_wm.setCustomerType("2"); 	
						HE_wm.setCustCardNo(m_custCardNo);	 // ��ī��
						HE_wm.setBonusCard(m_bonusCardNo);	 // ���ʽ�ī��
						
						if(recvCmd.equals("KY")) { 	// ���ݰŷ�ó(���ݿ�û)
							
							String target = (String) HE_ds.getValue("target");
							String loyaltyType = "";
							if(target.equals("1"))
								loyaltyType = "0"; // �Һ���
							else if(target.equals("2"))
								loyaltyType = "1"; // �����

							HE_wm.setLoyaltyType(loyaltyType);
							HE_wm.setCashCount(m_cashTotalCount); 						// �����Աݾ�
							HE_wm.setCashReceiptNo((String) HE_ds.getValue("cardNo1")); // ���ݿ�������ȣ
						}
						else if(recvCmd.equals("KV")) { // ���ݰŷ�ó(�ſ��û)
							HE_wm.setCardNumber((String) HE_ds.getValue("cardNo1")); 	// �ſ�ī��
							HE_wm.setMonthCount((String) HE_ds.getValue("monthCnt"));	// �Һΰ�����
						}
						break;
		
					//-- �ܻ�ŷ�ó��
						
					// ksm 2014.02.10 �����پ��� ���ο�û�� �������� ���ÿ� ���� �������� �ȳѾ��.
					// ���� Ȯ���ʿ�.
						
					case 3 : 						
						//if(m_selectedPayType.equals("3")) { // ��ī�弱��
							HE_wm.setCustomerType("3");
							HE_wm.setCustCardNo(m_custCardNo); 		// �ܻ�ī��
							HE_wm.setBonusCard(m_bonusCardNo);	 	// ���ʽ�
						//}
						break;
						
					case 4 : 		
						//if(m_selectedPayType.equals("3")) { // ��ī�弱��
							HE_wm.setCustomerType("4"); // ������
							HE_wm.setCustCardNo(m_custCardNo); 		// �ܻ�ī��
							HE_wm.setBonusCard(m_bonusCardNo);	 	// ���ʽ�
						//}
						break;
		
					default :
						LogUtility.getPumpALogger().info("\n�پ��뼿��ODT(����) ���ο�û ����(HE) ��������!");
						return false;
				}
				break;
				
			case 3 : // GS����Ʈ ����(LD���� ����) 12.12.26 dhp �߰�
				LogUtility.getPumpALogger().info("\nGS����Ʈ ����(LD���� ����)");
				LogUtility.getPumpALogger().info("cardNo1=" + HE_ds.getValue("cardNo1"));
				LogUtility.getPumpALogger().info("pin=" + HE_ds.getValue("pin"));
				
				HE_wm.setCustomerType("");
				HE_wm.setCardNumber((String) HE_ds.getValue("cardNo1"));// GS ����Ʈī��
				HE_wm.setBonusPin((String) HE_ds.getValue("pin"));			// ��й�ȣ
				break;
		}

		// �ݾ�/���� ó��
		if(recvCmd.equals("KY") || recvCmd.equals("KV") ) { // ���ݰŷ�ó
			HE_wm.setPrice((String) HE_ds.getValue("price"));
			HE_wm.setLiter((String) HE_ds.getValue("liter"));
			/* ODT ��û������ ���������� 'F'�� �;������� 'A'�� ���ŵǹǷ� �ǹ̾���.
			String isFullPumping = m_isFullPumping ? "1" : "0";
			HE_wm.setIsFullPumping(isFullPumping); */
		}
		else {
			if (HE_ds.getValue("saleType").equals("A")) {	// �ݾ�
				HE_wm.setPrice((String) HE_ds.getValue("value"));
				HE_wm.setIsFullPumping("0");
			} 
			else if (HE_ds.getValue("saleType").equals("Q")) {	// ����
				HE_wm.setLiter((String) HE_ds.getValue("value"));
				HE_wm.setIsFullPumping("0");
			} 
			else if (HE_ds.getValue("saleType").equals("F")) {	// ��������
				HE_wm.setPrice((String) HE_ds.getValue("value"));
				HE_wm.setIsFullPumping("1");
			}
		}
		
		if(Change.toValue(HE_wm.getPrice()) >= 149900) {
			HE_wm.setIsFullPumping("1");
			m_isFullPumping = true;
		}

		HE_wm.setBasePrice(m_basePriceTbl.get(HE_wm.getConnectNozzleNo())); // �ܰ�

		//HE_wm.print();	
		
		//�̰�ȣ2014-07-15 
		//���ʽ����� �α׻� ��й�ȣ ����ŷó����. 
		////////////////////////////////////////////////////////////////////
		String tmpBonusPin = HE_wm.getBonusPin();
		if( !"".equals(tmpBonusPin) )
			tmpBonusPin = "****";
		////////////////////////////////////////////////////////////////////
		LogUtility.getPumpALogger().info(new StringBuffer("\n�پ��뼿��ODT ���ο�û ����(HE)" )
				.append("\n ODT_No   =" ).append( HE_wm.getNozzleNo() )
				.append("\n nozzle   =" ).append( HE_wm.getConnectNozzleNo() ).append( "(HE)" )
				.append("\n cardType =" ).append( HE_wm.getCardType() )
				.append("\n liter    =" ).append( HE_wm.getLiter() )
				.append("\n basePrice=" ).append( HE_wm.getBasePrice() )
				.append("\n price    =" ).append( HE_wm.getPrice() )
				.append("\n creditNo =" ).append(Base64Util.encode(getCardTrack1Data(HE_wm.getCardNumber())) )
				.append("\n bonusNo  =" ).append(Base64Util.encode(getCardTrack1Data(HE_wm.getBonusCard())) )
				.append("\n custNo   =" ).append(Base64Util.encode(getCardTrack1Data(HE_wm.getCustCardNo())) )
				.append("\n cashCount=" ).append( HE_wm.getCashCount() )
				.append("\n cashRctNo=" ).append( Base64Util.encode(getCardTrack1Data(HE_wm.getCashReceiptNo())) )
				.append("\n isFullPmp=" ).append( HE_wm.getIsFullPumping() )
				.append("\n custType =" ).append( HE_wm.getCustomerType() )
				.append("\n loyaltyTy=" ).append( HE_wm.getLoyaltyType() ).append( "(0:�Һ���, 1:�����)" ) // ���ݿ����� �ŷ�����
				.append("\n monthCnt =" ).append( HE_wm.getMonthCount() )
				.append("\n bonusPin =" ).append( tmpBonusPin ).append("(GS Point ���)\n").toString());	
		
		insertRecvQueue(HE_wm);

		makeStatusInfo(236, m_nozzleNo); // ���ο�û ��
		
		HE_ds = null;
		m_cashTotalCount = "0";
		m_custCardNo = "";
		m_bonusCardNo = "";
		
		return true;
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
					TxQue.enQueue(wmByte); // ȯ�漳�������� �ƴϸ�
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
								Log.datas(protectCardNumber(RxBuf), buffSize, 20);
								//LogUtility.getPumpALogger().debug("0.Recv STX("+nozStr+") : ["+pack(RxBuf, ETX)+"]");
								//Log.datas(RxBuf, buffSize, 20);
							}
							
							if (m_isSaveSTX==true) 
								LogUtility.getPumpALogger().info("Recv STX("+nozStr+") : ["+pack(protectCardNumber(RxBuf), ETX)+"]");
							else
								LogUtility.getPumpALogger().debug("Recv STX("+nozStr+") : ["+pack(protectCardNumber(RxBuf), ETX)+"]");
							
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
						
						if (TxBuf.length > 200) { 
							int delayTime = (int) ((TxBuf.length - 200) * 1.2);
							Sleep.sleep(delayTime);
							LogUtility.getPumpALogger().debug("���Ŵ��ð� �߰�. TxBuf.length=" + TxBuf.length + 
									" delayTime=" + delayTime + " (ODT="+nozNo+")");
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
											Log.datas(protectCardNumber(RxBuf), buffSize, 20);
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
												Log.datas(protectCardNumber(RxBuf), buffSize, 20);
											}
											
											if (m_isSaveSTX==true) 
												LogUtility.getPumpALogger().info("Recv STX("+Change.toString("%02d", nozNo)+
														") : ["+pack(protectCardNumber(RxBuf), ETX)+"]");
											else
												LogUtility.getPumpALogger().debug("Recv STX("+Change.toString("%02d", nozNo)+
														") : ["+pack(protectCardNumber(RxBuf), ETX)+"]");

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


	@Override
	public void run() {

	}

	protected boolean saveOilInfo_YL (byte[][] byOilInfo) throws Exception {
		
		boolean envDataFlag=false;
		
		for(int i=0; i<4; i++) {
			YL_Buf[i] = byOilInfo[i].clone();
		}
		
		return envDataFlag;	
	}

	protected void send_P3_1_workingMessage () throws Exception {
			
			Vector<P5_NozzleInfo> nozInfoVec = P5_1_wm.getNozzleInfo();
			//P5_NozzleInfo P5_nozWm = nozInfoVec.get(0);			

			for (int i=0; i < nozInfoVec.size(); i++) {
				P5_NozzleInfo nozzleInfo = nozInfoVec.get(i);
				
				P3_1_WorkingMessage P3_wm = new P3_1_WorkingMessage ();
				P3_wm.setCommand(IPumpConstant.COMMANDID_P3_1);
				P3_wm.setNozzleNo(P5_1_wm.getNozzleNo()); 				// ODT��ȣ
				P3_wm.setTargetNozzleNo(nozzleInfo.getNozzleNumber());  // �����ȣ
				P3_wm.setConnectNozzleNo(P5_1_wm.getNozzleNo());
				P3_wm.setBasePrice(nozzleInfo.getBasePrice());  		// �ܰ�
				P3_wm.setPassThrough(false);

				insertRecvQueue(P3_wm);					
				
			}				
	}


	protected void sendFailMessageToODT (String msg, String odtNo) throws Exception {
		
		// ���ν��� ��������
		DataStruct QM_ds = new DataStruct();
		QM_ds.addString("command", "KG", 2);	// Command(���ν���)	
		QM_ds.addString("errorCode", "", 4); 	// ������ȣ 
		QM_ds.addString("message", msg, 48); 	// �޽���(����, �ִ� 48 byte)
		byte[] tempArray = QM_ds.getByteStream();	
		byte[] QM_buf = trans.makeProtocol(tempArray, odtNo);						
		TxQue.enQueue(QM_buf);	
	}
	
	
	protected void sendInitData () throws Exception { // ����Ʈ������ PJT �߰�
		
		//-- ������ �׷��� ����(JP)
		TxQue.enQueue (JP_Buf); //			
		
		//-- ��������(YL)
		if(YL_Buf[0].length > 22) TxQue.enQueue (YL_Buf[0]); // kixx
		if(YL_Buf[1].length > 22) TxQue.enQueue (YL_Buf[1]); // kixxPrime
		if(YL_Buf[2].length > 22) TxQue.enQueue (YL_Buf[2]); // diesel
		if(YL_Buf[3].length > 22) TxQue.enQueue (YL_Buf[3]); // advDiesel		        	
		
		//-- ������ �ܰ����� ó��(����������� P3_1 ������ ���ŵ��� �����Ƿ�  P3_1 ������ �����⿡ ����)
		if(P5_1_wm != null)
			send_P3_1_workingMessage (); // ���� P3 ��������
		else
			LogUtility.getPumpALogger().info("P5 ���� �̼������� send_P3_1_workingMessage() �̽���!");
		
		//-- ODT �ɼ�����(KP)
		//TxQue.enQueue (KP_Buf); // kixx			
		
		//-- ��Ÿ����
		//TxQue.enQueue (PL_Buf); // 
		//TxQue.enQueue (OM_Buf); // 
		TxQue.enQueue (SM_Buf); // 
	}

	// To be invoked when InputStream(is) has a receiving data
	@Override
	public void serialEvent(SerialPortEvent event) {

	}
		
	// get from Prime
	protected void setCurrentTime () throws Exception, SerialConnectException {
		
		Calendar cal = new GregorianCalendar();
		Formatter form = new Formatter();
		form.format("%04d%02d%02d%02d%02d%02d", cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH), 
				cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), 
				cal.get(Calendar.SECOND));
		String timeStr = form.toString();
		
/*		t0206_ds.editString("systemTime", timeStr, 14);
		t0206_Buf = t0206_ds.getByteStream();*/
	}

	protected void trimInputStream(String msg) throws Exception {
		
		Sleep.sleep(readStartInterval);
		byte[] trimBuf = new byte[40];
				
		int len = is.read(trimBuf); // trim inputStream
		//LogUtility.getPumpALogger().debug("Trim inputStream (" + msg + ") :" + " len=" + len + " (Noz="+nozNo+")");
		//Log.datas(trimBuf, 40, 20);
	}

	protected boolean verifyData (byte[] buf) throws Exception {
		
		int valStart=0, valEnd=0, size=0;
		
		byte[] byt = {buf[4], buf[5]};
		String cmd= new String (byt);
						
		int bufLen = getBufferLength(buf);
		
		//LogUtility.getPumpALogger().debug("++++ verifyData() cmd=" + cmd + " length=" + bufLen);

		if (cmd.equals("KD")) { 			
			valStart=12; valEnd=18;
			
			//--- ������ ����(������ ���̿� ETX Ȯ��) ---//
			if (!(bufLen==104 || bufLen==106) || buf[bufLen-2] != ETX) {
				LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> �̻����� ����("+cmd+"), �����ͱ���=104 �Ǵ� 106(�Һν�)" + 
						", ���ŵ����ͱ���=" + bufLen);
				//Log.datas(buf, buf.length, 20);
				return false;
			}
		}
		else if (cmd.equals("KF")) {
			size=21; valStart=12; valEnd=18;
		}
		else if (cmd.equals("KE")) {
			size=70; valStart=12; valEnd=18;
		}
		else if (cmd.equals("KX")) {
			size=112; valStart=12; valEnd=18;
		}
		else if (cmd.equals("KK")) {
			size=104; valStart=12; valEnd=18;
		}
/*		else if (cmd.equals("KU")) {
			size=42; valStart=11; valEnd=39; // KY�� ����
		}*/
		else if (cmd.equals("KY")) {
			size=84; valStart=11; valEnd=19; 
		}
		else if (cmd.equals("LD")) {
			size=79; valStart=12; valEnd=18;
		}
		else if (cmd.equals("LA")) {
			size=41; valStart=12; valEnd=38;
		}
		else if (cmd.equals("KV")) {
			valStart=11; valEnd=32;

			//--- ������ ����(������ ���̿� ETX Ȯ��) ---//
			if (!(bufLen==76 || bufLen==78) || buf[bufLen-2] != ETX) {
				LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> �̻����� ����("+cmd+"), �����ͱ���=104 �Ǵ� 106(�Һν�)" + 
						", ���ŵ����ͱ���=" + bufLen);
				//Log.datas(buf, buf.length, 20);
				return false;
			}
		}
		else {
			return true;
		}
		
		//--- KD�̿��� ������ ����(������ ���̿� ETX Ȯ��) ---//
		if (!cmd.equals("KD") && !cmd.equals("KV")) {
			if (size != bufLen || buf[size-2] != ETX) {
				LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> �̻����� ����("+cmd+"), �����ͱ���=" + 
						size + ", ���ŵ����ͱ���=" + bufLen);
				//Log.datas(buf, buf.length, 20);
				return false;
			}
		}

		//--- Value ����(���ڿ���) ---//
		byte tmp;
		for (int i=valStart; i<=valEnd; i++) {
			tmp = buf[i]==0x20 ? 0x30 : buf[i]; // ' ' �� '0'����
			if (tmp < 0x30 || tmp > 0x39) { // 0 ~ 9 �� �ƴϸ�
				LogUtility.getPumpALogger().debug("nozzle=" + nozNo + 
						" -> �̻����� ����("+cmd+"), ���ڰ� �ƴ�, ���ŵ�����=" + buf[i]);
				return false;
			}
		}

		return true;
	}
}
