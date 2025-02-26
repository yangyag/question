/*
 * 신형다쓰노셀프(현성)-ODT, 롬버전 = 372
 * (추가 2012.08, dhp) 
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
	protected int	dispLevel=0; // 0=비정상 메시지 + 비정상수신 STX
    							 // 1=비정상 메시지 + 비정상수신 STX + 정상수신 STX
	 							 // 2=비정상 메시지 + 비정상수신 STX + 정상송수신 STX
	 							 // 3=모든 메시지 + 모든 송수신 STX
	
	protected byte ENQ = 0x05;
	protected byte EOB = 0x17;
	protected byte EOT = 0x04;
	protected byte ETX = 0x03;
	protected DataStruct FC_ds  = TsnSelfHSDS.getDS ("FC");  // 가득주유 통제(신설전문)
	
    protected boolean firstRequest=true;
    protected byte FS  = 0x1c;
    
    protected DataStruct HE_ds = new DataStruct();
    protected boolean	issueLineErr=true;

	protected byte[] 	JP_Buf = null;  // 주유기 그룹핑 정보
	protected DataStruct JP_ds  = TsnSelfHSDS.getDS ("JP", 6);  // 주유기 그룹핑 정보
    protected byte[] 	KP_Buf = null;  // ODT 옵션	
	protected byte[] 	lastPumpingData=new byte[buffSize];
	protected int		lineCommCheckCnt=0;
	protected int		lineErrCnt=0;
	protected String	m_basePrice="000000";
	protected Hashtable<String, String> m_basePriceTbl = new Hashtable<String, String>();
	protected String 	m_bonusCardNo = ""; 	// 보너스카드
	protected String 	m_bonusCardNo2 = ""; 	// 보너스카드 (외상요청-2 용)
	protected String 	m_cashTotalCount = "0"; // 전체 현금입금액
	protected String 	m_custCardNo = ""; 		// 고객카드
	protected String 	m_custCardNo2 = ""; 	// 고객카드 (외상요청-2 용)
	protected String 	m_custType2 = ""; 	    // 고객타입 (외상요청-2 용)
	protected String	m_downBasePrice="000000";
	//protected boolean 	m_changeBasePrice=false;
	protected boolean	m_isFullPumping=false;
	protected boolean m_isSaveSTX=false;
	protected String	m_liter="";
	protected int		m_nCustType=1; // 1=일반
	protected boolean 	m_nozLock=false;
	protected Hashtable<String, String> m_nozStateTbl = new Hashtable<String, String>();
	protected String 	m_nozzleNo = "";

	protected Vector<String> m_nozzleVec = new Vector<String>();
	protected String	m_presetMode ="";
	
	protected String	m_price="";
	protected String 	m_realBasePrice="000000"; // 실 주유단가
	protected String 	m_realLiter="0000000";    // 실 주유량	
	protected String 	m_realPrice="00000000";   // 실 주유금액
	protected boolean 	m_recvedNozLock=false;
	protected String 	m_selectedPayType = ""; // ODT에서 선택한 결제수단
	protected boolean m_sendEnvDataOK=true;
	protected boolean	m_setEnvDataOK=false;
	protected String 	m_sSaveBonus="0"; 	
	protected int		m_statusCode=0;
	protected String 	m_uploadedCommand = ""; // ODT가 요청한 전문
	protected int	MAX_GAUGE_SJ  =5;

	protected int	MAX_LAST_PDATA=3;
	protected int	MAX_PROG4STEP =15;
	protected int	MAX_StateReq_Cnt=30; // 상태정보 요청주기(0=nothing)

    protected int	MAX_WAIT_PDATA=5;

	protected byte NAK = 0x15;
    
	protected short		nozState=0;
	protected byte[] 	OM_Buf = null;
	protected int		p0_cnt=0;
	protected P5_1_WorkingMessage P5_1_wm = null;
	protected byte[] 	PL_Buf = null;
	//progressStep : 0=초기화전, 1=초기화후, 2=상태정보송신후, 3=주유중, 4=주유완료, 
    //               5=토털게이지수신/주유완료자료 송신
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
	//    protected DataStruct KP_ds  = new DataStruct();  // ODT 옵션
//    protected DataStruct PL_ds  = new DataStruct();
//    protected DataStruct OM_ds  = new DataStruct();
    protected DataStruct SM_ds = new DataStruct();
	protected byte SOH = 0x01;
    protected byte STX = 0x02;
    protected TransTsnSelfHS trans = new TransTsnSelfHS(m_basePriceTbl);
    
protected byte[] 	TxBuf;
	protected int		waitGaugeForSJ_Cnt=0;
	    
	protected int		waitLastPData_Cnt=0;
	protected byte[][]  YL_Buf = new byte[4][];  // 셀프 유종정보
	
	// 추가 12년08월
	protected DataStruct YL_ds1 = TsnSelfHSDS.getDS ("YL", 3); // 셀프 유종정보(Kixx)
	protected DataStruct YL_ds2 = TsnSelfHSDS.getDS ("YL", 3); // 셀프 유종정보(KixxPrime) 
	protected DataStruct YL_ds3 = TsnSelfHSDS.getDS ("YL", 3); // 셀프 유종정보(Diesel) 
	protected DataStruct YL_ds4 = TsnSelfHSDS.getDS ("YL", 3); // 셀프 유종정보(AdvDiesel) 
    
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
        	 *  이하 셀프ODT
        	 */
			//---  YL (유종정보)---//    		
        	YL_Buf[0] = YL_ds1.getByteStream();
        	YL_Buf[1] = YL_ds1.getByteStream();
        	YL_Buf[2] = YL_ds1.getByteStream();
        	YL_Buf[3] = YL_ds1.getByteStream();
    		
        	//--- JP (주유기 그룹핑 정보)
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

        	//--- KP (ODT 옵션 정보)
        	//KP_Buf = KP_ds.getByteStream();

    	} catch (Exception e) {
    		LogUtility.getPumpALogger().error(e.getMessage(), e);
    	}

    }
 
	protected short checkRecvData (byte[] byData) throws Exception, SerialConnectException {
		
		if (compareNozID(byData) == false) { // NAK 안보냄
			LogUtility.getPumpALogger().info("1.Recv STX NozID mismatch-1.0! (Noz=" + nozNo + ")");
			Log.datas(protectCardNumber(byData), byData.length, 20);

			trimInputStream("routine : 1");
			return 1; // 반복
		}
		
		if (compareBCC(byData) == false) {
			sendText(NAK);
			LogUtility.getPumpALogger().info("1.Recv STX BCC fail! (Noz=" + nozNo + ")");
			Log.datas(protectCardNumber(byData), byData.length, 20);
			return 1; // 반복
		}
		
		if (verifyData(byData) == false) {
			sendText(NAK);
			LogUtility.getPumpALogger().info("1.Recv STX Data verify fail! (Noz=" + nozNo + ")");
			Log.datas(protectCardNumber(byData), byData.length, 20);
			return 2; // 종료
		}

		return 0; // 정상
	}
	
	// get from Prime
	protected boolean chkEnvDataAndSaving (byte[] dat) throws Exception {
		
		boolean envDataFlag=false;
		
		return envDataFlag;
	}
	
	protected boolean compareBasePrice (byte[] buf) throws Exception {
		
		try {
			if (m_basePrice.equals("000000")) // 단가정보 미수신 상태
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
		 * 이하 셀프ODT
		 */
		if (wm.getCommand().equals("P5_1")) { // 셀프 환경설정(노즐정보)
			
			P5_1_wm = (P5_1_WorkingMessage) wm;
			//skip=false;
			
			LogUtility.getPumpALogger().info("[Pump A][수신-다쓰노셀프(현성)ODT 노즐/단가 정보]" + " nozzle=" + 
					P5_1_wm.getNozzleNo() + "("+P5_1_wm.getCommand()+")" +
					" | mode=" + P5_1_wm.getMode() + 
					" | useFullPumping=" + P5_1_wm.getUseFullPumping());
			
			if (P5_1_wm.getMode().equals("0")) {// 초기화

				skip = true;
				m_setEnvDataOK=true;
				
				String odtNo = TsnSelfHSDS.getOdtNo_forODT (P5_1_wm.getNozzleNo());

				//-- 주유기 그룹핑정보 생성
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
					m_nozStateTbl.put(nozzleInfo.getNozzleNumber(), "651"); //노즐다운
				}
				JP_ds.addByte("etx", ETX);
				JP_ds.addByte("bcc", (byte) 0x20);

				JP_Buf = JP_ds.getByteStream();
				
				//-- 유종정보 처리
				byte[][] wmByte = generateByteStreams(P5_1_wm); // ODT 당 4개의 유종별 노즐정보					
				if (wmByte != null) {
					saveOilInfo_YL(wmByte);  // 유종정보 저장 -> 초기화시 전송
				}
				
				//-- SM전문
				DataStruct SM_ds = new DataStruct();
				SM_ds.addByte("soh", SOH);
				SM_ds.addString("odtNo", odtNo, 2);
				SM_ds.addByte("stx", STX);
				SM_ds.addString("command", "SM", 2);
				SM_ds.addString("date", getCurrentDateTime(), 12); // ???		
				SM_ds.addByte("etx", ETX);
				SM_ds.addByte("bcc", (byte) 0x20);

				SM_Buf = SM_ds.getByteStream();	
				
				if (m_sendEnvDataOK == true) { // 스마트주유소 PJT 추가 -> 최초 한번 초기화 정보 전송					
					sendInitData();					
					m_sendEnvDataOK = false;
				}
			} 
			else if (P5_1_wm.getMode().equals("1")) { // 단가변경

				skip = true;
				//m_changeBasePrice=true;
	
				Vector<P5_NozzleInfo> nozzleInfoVector = P5_1_wm.getNozzleInfo();

				for (int i=0; i < nozzleInfoVector.size(); i++) {
					P5_NozzleInfo nozzleInfo = nozzleInfoVector.get(i);
			
					String nozzleNo  = nozzleInfo.getNozzleNumber();
					m_basePriceTbl.put(nozzleNo, nozzleInfo.getBasePrice());
					LogUtility.getPumpALogger().info("단가변경용 P5수신 및 단가변경, nozzleNo=" + nozzleNo + ", base_price=" + nozzleInfo.getBasePrice());
		
				}
				
				send_P3_1_workingMessage (); // 노즐에 P3 전문전송

				//LogUtility.getPumpALogger().info("단가변경용 P5수신, 단가변경. ODT_No="+P5_1_wm.getNozzleNo());
			}
			else if (P5_1_wm.getMode().equals("2")) { // 가득주유 옵션변경

				skip = true;
				//m_useFullPumping = P5_1_wm.getUseFullPumping().equals("1")? true : false;
				FC_ds.editString("useFullPumping", P5_1_wm.getUseFullPumping(), 1);
				
				byte[] fc_buf = FC_ds.getByteStream();
				TxQue.enQueue(fc_buf);
				
				LogUtility.getPumpALogger().info("가득주유 옵션변경용 P5수신, 옵션변경. ODT_No="+P5_1_wm.getNozzleNo() +
						" useFullPumping=" + P5_1_wm.getUseFullPumping());
								
			}
		}
		else if (wm.getCommand().equals("CB")) { // 고객유형 확인응답
			
			CB_wm = (CB_WorkingMessage) wm;
				
			LogUtility.getPumpALogger().info("[Pump A][수신-다쓰노셀프(현성)ODT 고객유형 확인응답]" + " nozzle=" + 
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
			
				case 0 : LogUtility.getPumpALogger().info("### 고객유형 확인 실패. (ODT="+nozNo+")");
				
						// 승인실패 전문전송
						if (m_uploadedCommand.equals("KK"))							
							sendFailMessageToODT(CB_wm.getMessage(), odtNo);
						else		
							sendFailMessageToODT("카드종류 불일치!", odtNo);
				
						break;
						
				case 1 : LogUtility.getPumpALogger().info("### 일반(신용카드) 고객입니다. (ODT="+nozNo+")");
				
						if (m_uploadedCommand.equals("KD")) 
							requestCardApprove("1", "1", ""); // 승인요청						
						else 	// 요청이 외상의 경우 미등록 카드가 신용으로 내려올 경우
							sendFailMessageToODT("고객카드가 아닙니다!", odtNo);
						
						break;
						
				case 2 : LogUtility.getPumpALogger().info("### 현금거래처 고객입니다. (ODT="+nozNo+")");
								
						if (m_uploadedCommand.equals("KK")) {
							// 셀프ODT 외상세팅요청에 대한 외상유대입금 허가전문 전송
							DataStruct KI_ds = new DataStruct();
							KI_ds.addByte("soh", SOH);
							KI_ds.addString("odtNo", odtNo, 2);
							KI_ds.addByte("stx", STX);
	
							KI_ds.addString("command", "KI", 2);
							KI_ds.addString("trNo", "", 4);		// 거래번호
							KI_ds.addString("wcc", "A", 1);		// 'A'
							String basePrice = m_basePriceTbl.get(targetNozzleNo).substring(0,4);
							KI_ds.addString("basePrice1", basePrice, 4); // 할인전 단가
							KI_ds.addString("basePrice2", basePrice, 4); // 할인후 단가
							KI_ds.addString("liter", m_liter, 7);		 // 수량(소수점 3자리)
							KI_ds.addString("price", m_price, 7);		 // 금액
							
							KI_ds.addByte("etx", ETX);
							KI_ds.addByte("bcc", (byte) 0x20);
							
							byte[] KI_Buf = KI_ds.getByteStream();							
							TxQue.enQueue(KI_Buf);	
						}
						else
							sendFailMessageToODT("신용카드가 아닙니다!", odtNo);
							
						break;
						
				case 3 : LogUtility.getPumpALogger().info("### 외상거래처 고객입니다. (ODT="+nozNo+")");

						if (m_uploadedCommand.equals("KK")) 
							requestCardApprove("1", "3", ""); // 승인요청
						else
							sendFailMessageToODT("신용카드가 아닙니다!", odtNo);
								
						break;
						
				case 4 : LogUtility.getPumpALogger().info("### 외상거래처-정량 고객입니다. (ODT="+nozNo+")");

						if (m_uploadedCommand.equals("KK")) 
							requestCardApprove("1", "4", ""); // 승인요청
						else
							sendFailMessageToODT("신용카드가 아닙니다!", odtNo);
						
						break;
			}
			
			skip = true;

		}
		else if (wm.getCommand().equals("QM")) { // 카드 승인응답
			QM_wm = (QM_WoringMessage) wm;
			
			LogUtility.getPumpALogger().info("[Pump A][수신-다쓰노셀프(현성)ODT 승인응답]" + " nozzle=" + 
					QM_wm.getConnectNozzleNo() + "("+QM_wm.getCommand()+")" +
					" | ODT_No=" + QM_wm.getNozzleNo() +
					" | mode=" + QM_wm.getMode());
			
			LogUtility.getPumpALogger().info(new StringBuffer("\n수신-다쓰노셀프(현성)ODT 승인응답 전문(QM)" )
					.append("\n ODT_No    =" ).append( QM_wm.getNozzleNo() )
					.append("\n nozzle    =" ).append( QM_wm.getConnectNozzleNo() )
					.append("\n 승인결과  =" ).append( ( QM_wm.getMode().equals("1") ? "성공" : "실패") )
					.append("\n liter     =" ).append( QM_wm.getLiter() )
					.append("\n basePrice =" ).append( QM_wm.getBasePrice() )
					.append("\n price     =" ).append( QM_wm.getPrice() )
					.append("\n driveName =" ).append( QM_wm.getDriveName() )
					.append("\n carNo     =" ).append( QM_wm.getCarNo() )
					.append("\n cardAdjInd=" ).append( QM_wm.getCardAdjInd() ).append("(01:무제한, 02:차량별, 03:거래처별, 04:1회정량, 05:정량입력)" )
					.append("\n limitBase =" ).append( QM_wm.getLimitBase() ).append( "(01:수량, 02:금액)" )
					.append("\n Limit     =" ).append( QM_wm.getLimit() )
					.append("\n accLimit  =" ).append( QM_wm.getAccLimit() )
					.append("\n message   =" ).append( QM_wm.getMessage() ).append("\n").toString());	
			
			QM_wm.print();
						
			if(QM_wm.getMode().equals("1") && !m_uploadedCommand.equals(IPumpConstant.TH_COMMAND_KK)) { // 주유기 주유허가
				PB_WorkingMessage PB_wm = new PB_WorkingMessage();
				
				PB_wm.setNozzleNo(QM_wm.getNozzleNo());
				PB_wm.setConnectNozzleNo(wm.getConnectNozzleNo()); // Nozzle No
				PB_wm.setTargetNozzleNo(QM_wm.getConnectNozzleNo());
				PB_wm.setPassThrough(false);
				
				if (/*m_uploadedCommand.equals(IPumpConstant.TH_COMMAND_KK) ||  //-- 외상요청 
*/					m_uploadedCommand.equals(IPumpConstant.TH_COMMAND_KY) ||  //-- 현금거래처(현금)요청
					m_uploadedCommand.equals(IPumpConstant.TH_COMMAND_KV)) {  //-- 현금거래처(신용)요청
					// 비할인허가시 노즐에서 단가변경(PC) 않도록 하기위함
					PB_wm.setDirection(IPumpConstant.DIRECTION_FROM_ODT); 
				}
				
				PB_wm.setPrice(QM_wm.getPrice());
				PB_wm.setBasePrice(QM_wm.getBasePrice());
				PB_wm.setLiter(QM_wm.getLiter());
				
				if(Change.toValue(QM_wm.getPrice()) > 0)
					m_presetMode = "0"; // 정액설정
				else
					m_presetMode = "1"; // 정량설정
				
				PB_wm.setCommandSet(m_presetMode);
	
				insertRecvQueue(PB_wm); 
			}		
			
			DataStruct QM_ds = new DataStruct();
				
			boolean isApprove = QM_wm.getMode().equals("1") ? true : false;
			String basePrice = m_basePriceTbl.get(QM_wm.getConnectNozzleNo()); // 단가(점두가)
				
			if (isApprove == true) { //-- 승인 성공 --//

				if (m_uploadedCommand.equals(IPumpConstant.TH_COMMAND_KD) ||  //-- 신용요청
					m_uploadedCommand.equals(IPumpConstant.TH_COMMAND_KE) ||  //-- 현금요청
					m_uploadedCommand.equals(IPumpConstant.TH_COMMAND_KX) ||  //-- 현금요청(현금영수증)
					m_uploadedCommand.equals(IPumpConstant.TH_COMMAND_LD) ||  //-- GS포인트 결제
					m_uploadedCommand.equals(IPumpConstant.TH_COMMAND_LA)) {  //-- 외상요청-2
					
					if(basePrice.equals(QM_wm.getBasePrice())) { //--- 비할인
						QM_ds.addString("command", "KA", 2);	// Command
						QM_ds.addString("trNo", "", 4);			// 거래번호
						if(m_presetMode.equals("0")) {
							QM_ds.addString("wcc", "A", 1);		// WCC
							QM_ds.addString("value", QM_wm.getPrice().substring(1,8), 7); // 금액	
						}
						else {
							QM_ds.addString("wcc", "Q", 1);		// WCC
							QM_ds.addString("value", QM_wm.getLiter(), 7); // 수량							
						}
					}
					else { //--- 할인
						QM_ds.addString("command", "KB", 2);	// Command
						QM_ds.addString("trNo", "", 4);			// 거래번호
						if(m_presetMode.equals("0")) 
							QM_ds.addString("wcc", "A", 1);		// WCC
						else
							QM_ds.addString("wcc", "Q", 1);		// WCC

						QM_ds.addString("basePrice1", basePrice.substring(0,4), 4);		// 할인전 단가
						QM_ds.addString("basePrice2", QM_wm.getBasePrice().substring(0,4), 4);	// 할인후 단가
						QM_ds.addString("liter", QM_wm.getLiter(), 7);					// 수량(소수점 3자리)
						QM_ds.addString("price", QM_wm.getPrice().substring(1,8), 7); 	// 금액	
						QM_ds.addString("message", QM_wm.getMessage(), 48); 			// 메시지	
					}
				}		
				else if (m_uploadedCommand.equals(IPumpConstant.TH_COMMAND_KK)) { //-- 외상요청 - 1
					
					//String cmd = m_uploadedCommand.equals(IPumpConstant.TH_COMMAND_KK) ? "KJ" : "KB";
					String cmd = "LS"; // 거래처정보(한도포함)

					// temp data
//					QM_wm.setLimitBase("02");
//					QM_wm.setLimit("1000000");
//					QM_wm.setAccLimit("600000");					
		
					QM_ds.addString("command", cmd, 2);		// Command
					QM_ds.addString("trNo", "", 4);			// 거래번호
					if(m_presetMode.equals("0")) 
						QM_ds.addString("wcc", "A", 1);		// WCC
					else
						QM_ds.addString("wcc", "Q", 1);		// WCC

					QM_ds.addString("basePrice1", basePrice.substring(0,4), 4);		// 할인전 단가
					QM_ds.addString("basePrice2", QM_wm.getBasePrice().substring(0,4), 4);	// 할인후 단가
					QM_ds.addString("liter", QM_wm.getLiter(), 7);					// 수량(소수점 3자리)
					QM_ds.addString("price", QM_wm.getPrice().substring(1,8), 7); 	// 금액	
					QM_ds.addString("driveName", QM_wm.getDriveName(), 20); 		// 운전자면(거래처명)
					QM_ds.addString("carNo", QM_wm.getCarNo(), 18); 				// 차량번호
					QM_ds.addString("cardAdjInd", QM_wm.getCardAdjInd(), 2);		// 고객구분(01:무제한, 기타:제한)
					QM_ds.addString("limitBase", QM_wm.getLimitBase(), 2); 			// 한도기준(01:수량, 02:금액)
									
					String limit = "";
					String accLimit = "";
					if(QM_wm.getLimitBase().equals("01")) { // 수량
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
					else { // 금액
						Formatter form = new Formatter();
						limit = (form.format("%018d", Change.toValue(QM_wm.getLimit()))).toString();
						
						Formatter form2 = new Formatter();
						accLimit = (form2.format("%018d", Change.toValue(QM_wm.getAccLimit()))).toString();							
					}
					QM_ds.addString("limit", limit, 18); 					// 총한도
					QM_ds.addString("accLimit", accLimit, 18);				// 누적사용량

					LogUtility.getPumpALogger().info("cardAdjInd==" + QM_ds.getValue("cardAdjInd"));
					LogUtility.getPumpALogger().info("limitBase===" + QM_ds.getValue("limitBase"));
					LogUtility.getPumpALogger().info("limit=======" + QM_ds.getValue("limit"));
					LogUtility.getPumpALogger().info("accLimit====" + QM_ds.getValue("accLimit"));
					
					QM_ds.addString("message", QM_wm.getMessage(), 48); 			// 메시지	
				}
				else if (m_uploadedCommand.equals(IPumpConstant.TH_COMMAND_KY) || 	//-- 현금거래처(현금)요청
						 m_uploadedCommand.equals(IPumpConstant.TH_COMMAND_KV)) { 	//-- 현금거래처(신용)요청
						
					//String cmd = m_uploadedCommand.equals(IPumpConstant.TH_COMMAND_KK) ? "KJ" : "KB";
					String cmd = "KB"; // 할인허가
		
					QM_ds.addString("command", cmd, 2);		// Command
					QM_ds.addString("trNo", "", 4);			// 거래번호
					if(m_presetMode.equals("0")) 
						QM_ds.addString("wcc", "A", 1);		// WCC
					else
						QM_ds.addString("wcc", "Q", 1);		// WCC

					QM_ds.addString("basePrice1", basePrice.substring(0,4), 4);	// 할인전 단가
					QM_ds.addString("basePrice2", QM_wm.getBasePrice().substring(0,4), 4);	// 할인후 단가
					QM_ds.addString("liter", QM_wm.getLiter(), 7);					// 수량(소수점 3자리)
					QM_ds.addString("price", QM_wm.getPrice().substring(1,8), 7); 	// 금액	
					QM_ds.addString("message", QM_wm.getMessage(), 48); // 메시지	
				}


				makeStatusInfo(235, m_nozzleNo); //주유 대기중
			}
			else { //-- 승인 실패 --//
				QM_ds.addString("command", "KG", 2);				// Command
				QM_ds.addString("errorCode", "", 4); 				// 오류번호 
				QM_ds.addString("message", QM_wm.getMessage(), 48); // 메시지(가변, 최대 48 byte)
			}
				
			byte[] tempArray = QM_ds.getByteStream();	
			byte[] QM_buf = trans.makeProtocol(tempArray, QM_wm.getNozzleNo());
			
			TxQue.enQueue(QM_buf);

			// 전문정보 초기화
			m_uploadedCommand = ""; 
			m_price ="";
			m_liter = "";
			m_isFullPumping = false;
			m_presetMode = "";

			skip=true;
		}
		else if (wm.getCommand().equals("S4")) { // 주유완료
			S4_wm = (S4_WorkingMessage) wm;
			
			LogUtility.getPumpALogger().info("[Pump A][수신-다쓰노셀프(현성)ODT 주유완료]" + " nozzle=" + 
					S4_wm.getNozzleNo() + "("+S4_wm.getCommand()+")" +
					" | ODT_No=" + S4_wm.getConnectNozzleNo() +
					" | price=" + S4_wm.getPrice() + 
					" | basePrice=" + S4_wm.getBasePrice() +
					" | Liter=" + S4_wm.getLiter());
			
			S4_wm.print();
			
			// TR 전문생성용
			m_realPrice     = S4_wm.getPrice();
			m_realLiter     = S4_wm.getLiter();
			m_realBasePrice = S4_wm.getBasePrice();

			skip=true;
		}
		else if (wm.getCommand().equals("QL")) { // 영수증출력
			QL_WorkingMessage QL_wm = (QL_WorkingMessage) wm;

			LogUtility.getPumpALogger().info("[Pump A][수신-다쓰노셀프(현성)ODT 영수증출력" + " nozzle=" + 
										 QL_wm.getConnectNozzleNo() + "("+QL_wm.getCommand()+")" +
										 " | ODT_No=" + QL_wm.getNozzleNo() +
										 " | mode=" + QL_wm.getMode() +
										 " | alarmState=" + QL_wm.getAlarmState() +
										 " | length=" + QL_wm.getContent().length());
			
			//QL_wm.print();
									
			String odtNo = TsnSelfHSDS.getOdtNo_forODT (P5_1_wm.getNozzleNo());
			
			//-- 영수증 포맷변환
			byte[] byContent = convertReceiptForm(QL_wm.getContent().getBytes());
			
			int maxLen1Page = 950;		
			int n_totalPage = (byContent.length / maxLen1Page) + 2;
			String totalPage = Change.toString(n_totalPage);
						
			int i=0;
			int offset=0;
			
			for (; i<n_totalPage - 1; i++) {
				
				//DataStruct PS_ds = TsnSelfHSDS.getDS(IPumpConstant.TH_COMMAND_PS); // 않됨
				DataStruct PS_ds = new DataStruct();
				PS_ds.addByte("soh", SOH);
				PS_ds.addString("odtNo", odtNo, 2);
				PS_ds.addByte("stx", STX);
	
				PS_ds.addString("command", IPumpConstant.TH_COMMAND_PS, 2);
				PS_ds.addString("trNo", "" ,4);							// 거래번호
				PS_ds.addString("currPage", Change.toString(i + 1), 1);	// 현재페이지
				PS_ds.addString("totPage", totalPage, 1);				// 전체페이지
				int startIdx = Math.min((i * maxLen1Page) + offset, byContent.length);
				int endIdx   = Math.min((i+1) * maxLen1Page, byContent.length);
								
				// 마지막부분이 CR(0x0A) 아니면 최대 100 자리까지 넘어감
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
				PS_ds.addString("content", str, str.getBytes().length);	// 영수증 데이터			
				
				PS_ds.addByte("etx", ETX);
				PS_ds.addByte("bcc", (byte) 0x20);

				TxQue.enQueue(PS_ds.getByteStream());
			}			
			
			//--- 바코드 및 영수증 마감처리(Feed and Cutting)
			DataStruct PS_ds = new DataStruct();
			PS_ds.addByte("soh", SOH);
			PS_ds.addString("odtNo", odtNo, 2);
			PS_ds.addByte("stx", STX);

			PS_ds.addString("command", IPumpConstant.TH_COMMAND_PS, 2);
			PS_ds.addString("trNo", "" ,4);							// 거래번호
			PS_ds.addString("currPage", Change.toString(i + 1), 1);	// 현재페이지
			PS_ds.addString("totPage", totalPage, 1);				// 전체페이지

			String barcode ="";
			if (QL_wm.getBarCode().length() > 0) {
				byte[] byCmd = {0x0A, 0x0A, 0x1B, 'b'}; 
				String cmd = new String(byCmd) + Change.toString("%02d", QL_wm.getBarCode().length());
				barcode = cmd + QL_wm.getBarCode();
			}
													
			byte[] cut = {0x0A, 0x0A, 0x0A, 0x0A, 0x1B, 'm'}; // LineFeed + PartialCut
			String tailStr = barcode + new String(cut);
			PS_ds.addString("content", tailStr, tailStr.getBytes().length);	// 영수증 데이터	

			PS_ds.addByte("etx", ETX);
			PS_ds.addByte("bcc", (byte) 0x20);

			TxQue.enQueue(PS_ds.getByteStream());

			//----- TR 전문 생성 -----//
			TR_WorkingMessage TR_wm = new TR_WorkingMessage();
			TR_wm.setNozzleNo(QL_wm.getNozzleNo());
			TR_wm.setConnectNozzleNo(QL_wm.getConnectNozzleNo());
			TR_wm.setLiter(m_realLiter);
			TR_wm.setBasePrice(m_realBasePrice);
			TR_wm.setPrice(m_realPrice);
			
			insertRecvQueue(TR_wm); //모듈로 전송
			
			makeStatusInfo(231, m_nozzleNo); // 유종선택(초기)

			skip=true;
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

			LogUtility.getPumpALogger().info("Received nozzle parameters(P7). nozzle=" + nozStr);
			LogUtility.getPumpALogger().info(" -readStartInterval ="+P7_wm.getReadStartInterval());
			LogUtility.getPumpALogger().info(" -writeStartInterval="+P7_wm.getWriteStartInterval());
			LogUtility.getPumpALogger().info(" -readBuffInterval  ="+P7_wm.getReadBuffInterval());
			LogUtility.getPumpALogger().info(" -minLineErrCount   ="+P7_wm.getLineErrorCount());
			LogUtility.getPumpALogger().info(" -maxLineErrSkipCnt ="+P7_wm.getLineErrorSkipCount());
			
			skip = true;
		}
		else if (wm.getCommand().equals("SE")) { //--- 주유기 이상정보
			SE_WorkingMessage SE_wm = (SE_WorkingMessage) wm;		
			
			if(SE_wm.getStatusCode().substring(0,1).equals("6")) { // 6xx = 상태정보
				m_nozStateTbl.put(SE_wm.getConnectNozzleNo(), SE_wm.getStatusCode()); // 회선불량	
				LogUtility.getPumpALogger().debug("Recv nozState. noz=" + SE_wm.getConnectNozzleNo() + " nozState=" + m_nozStateTbl.get(SE_wm.getConnectNozzleNo()));

			}
		}
		else if (wm.getCommand().equals("S8")) { //--- 주유기 상태정보
			S8_WorkingMessage S8_wm = (S8_WorkingMessage) wm;		
			
			if(S8_wm.getStatusCode().substring(0,1).equals("6")) { // 6xx = 상태정보
				m_nozStateTbl.put(S8_wm.getConnectNozzleNo(), S8_wm.getStatusCode()); // 상태정보	
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
		SE_wm.setStatusCode("601"); // 회선불량
		SE_wm.setErrMsg("셀프ODT 회선불량");

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
				statusCode = 231; //유종선택 대기
				for(int i=0; i<m_nozzleVec.size(); i++) {
					m_nozzleNo = m_nozzleVec.get(i);
					makeStatusInfo(statusCode, m_nozzleNo);
				}
			}
			else if (buf[6] == 'D') {
				byte[] tmp = new byte[1];
				tmp[0] = buf[7];
				m_selectedPayType = new String(tmp);
				statusCode = 232; // 금액선택대기
				makeStatusInfo(statusCode, m_nozzleNo);
			}
			else {
				statusCode = 232; // 금액선택대기
				byt[0]=buf[6]; byt[1]=buf[7];
				m_nozzleNo = new String(byt);
				makeStatusInfo(statusCode, m_nozzleNo);
			}
		}
		else if(len == 11) {
			
			if (buf[8] == 'B') {
				statusCode = 234; // 보너스입력대기
				byt[0]=buf[6]; byt[1]=buf[7];
				m_nozzleNo = new String(byt);
				makeStatusInfo(statusCode, m_nozzleNo);
			}
			else if (buf[8] == 'C') {
				statusCode = 233; // 결제입력대기
				byt[0]=buf[6]; byt[1]=buf[7];
				m_nozzleNo = new String(byt);
				makeStatusInfo(statusCode, m_nozzleNo);
			}
		}
		else {
			statusCode = 233; // 결제입력 대기
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
				statusCode = 650; // 정상
				errorMsg = "정상";
				break;
			case 231:
				statusCode = 231;
				errorMsg = "유종선택 대기";
				break;
			case 232:
				statusCode = 232;
				errorMsg = "금액선택 대기";
				break;
			case 233:
				statusCode = 233;
				errorMsg = "결제입력 대기";
				break;
			case 234:
				statusCode = 234;
				errorMsg = "보너스카드입력 대기";
				break;
			case 235:
				statusCode = 235;
				errorMsg = "주유 대기";
				break;
			case 236:
				statusCode = 236;
				errorMsg = "승인요청 중";
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
		
		if (recvCmd.equals(IPumpConstant.TH_COMMAND_GD)) { // 영수증 데이터 수신완료
			//insertRecvQueue(generateWorkingMessage(RxBuf, "GD")); 
		} 
		// POS로 부터 환경정보 수신후 최초 1번 혹은 ODT 초기데이터 요청시 ODT에 주유기 정보 전송
		else if (recvCmd.equals(IPumpConstant.TH_COMMAND_IQ)) { 
			
			LogUtility.getPumpALogger().info("다쓰노셀프ODT(현성) 초기화 정보 전송....");
			
			// 변수 초기화
			HE_ds = null;
			m_cashTotalCount = "0";
			m_custCardNo = "";
			m_bonusCardNo = "";
			m_uploadedCommand = ""; 
			m_price ="";
			m_liter = "";
			m_isFullPumping = false;
			
			sendInitData(); // 스마트주유소 PJT 수정

			/*//-- 주유기 그룹핑 정보(JP) // 스마트주유소 PJT 수정 -> sendInitData() 로 변경처리
			TxQue.enQueue (JP_Buf); //			

			LogUtility.getPumpALogger().info("$$$$$$$$$$$$ TxQue.enQueue (JP_Buf)=\n");
			Log.datas(JP_Buf, JP_Buf.length, 20);
			
			//-- 유종정보(YL)
			if(YL_Buf[0].length > 22) TxQue.enQueue (YL_Buf[0]); // kixx
			if(YL_Buf[1].length > 22) TxQue.enQueue (YL_Buf[1]); // kixxPrime
			if(YL_Buf[2].length > 22) TxQue.enQueue (YL_Buf[2]); // diesel
			if(YL_Buf[3].length > 22) TxQue.enQueue (YL_Buf[3]); // advDiesel
			        	
			
			//-- 주유기 단가정보 처리(셀프주유기는 P3_1 전문이 수신되지 않으므로  P3_1 전문을 주유기에 전송)
			if(P5_1_wm != null)
				send_P3_1_workingMessage (); // 노즐에 P3 전문전송
			else
				LogUtility.getPumpALogger().info("P5 전문 미수신으로 send_P3_1_workingMessage() 미실행!");
			
			//-- ODT 옵션정보(KP)
			//TxQue.enQueue (KP_Buf); // kixx			
			
			//-- 기타전문
			//TxQue.enQueue (PL_Buf); // 
			//TxQue.enQueue (OM_Buf); // 
			TxQue.enQueue (SM_Buf); // 
			*/
		} 
		else if (recvCmd.equals(IPumpConstant.TH_COMMAND_KF)) { // 현금입금 정보
			
			// ODT수신  : 신형다쓰노(현성) 현금입금 정보
			// 모듈전송 :  WorkingMessage
			DataStruct KF_ds = TsnSelfHSDS.getDS("KF");
			
			// ODT 수신 데이터 세팅
			KF_ds.setByteStream(RxBuf);

			String odtNo			= TsnSelfHSDS.getOdtNo_forPOS((String) KF_ds.getValue("odtNo"));
			String targetNozzleNo 	= (String) KF_ds.getValue("nozzleNo");		
						
			BI_WorkingMessage BI_wm = new BI_WorkingMessage();
				
			BI_wm.setNozzleNo(odtNo); 					// ODT
			BI_wm.setConnectNozzleNo(targetNozzleNo); 	// 노즐

			int cashTotalCount = Change.toValue((String) KF_ds.getValue("value"));
			int cashCount = cashTotalCount - Change.toValue(m_cashTotalCount); //  이번값 - 이전값
			BI_wm.setCash(Change.toString("%08d", cashCount)); // 투입금액
			
			m_cashTotalCount = "0" + (String) KF_ds.getValue("value");
			BI_wm.setCashCount(m_cashTotalCount); // 총 투입금액

			insertRecvQueue(BI_wm);
			BI_wm.print();		
			
		} 
		//--- 결제 승인요청 ---//
		else if (recvCmd.equals(IPumpConstant.TH_COMMAND_KD) ||
				 recvCmd.equals(IPumpConstant.TH_COMMAND_KE) ||
				 recvCmd.equals(IPumpConstant.TH_COMMAND_KK) ||
				 recvCmd.equals(IPumpConstant.TH_COMMAND_KY) ||
				 recvCmd.equals(IPumpConstant.TH_COMMAND_KV) ||
				 recvCmd.equals(IPumpConstant.TH_COMMAND_KX) ||
				 recvCmd.equals(IPumpConstant.TH_COMMAND_LD) ||
				 recvCmd.equals(IPumpConstant.TH_COMMAND_LA)) { 
			
			// ODT수신  : 신형다쓰노(현성) 현금/신용/보너스/외상 승인/취소 요청 
			// 모듈전송 : HE WorkingMessage
			//DataStruct HE_ds = null;
			
			m_uploadedCommand = recvCmd;
			
			if(recvCmd.equals(IPumpConstant.TH_COMMAND_KD)) // 신용승인요청
				HE_ds = TsnSelfHSDS.getDS("KD");
			else if(recvCmd.equals(IPumpConstant.TH_COMMAND_KE)) // 현금승인요청
				HE_ds = TsnSelfHSDS.getDS("KE");
			else if(recvCmd.equals(IPumpConstant.TH_COMMAND_KK)) // 외상승인요청
				HE_ds = TsnSelfHSDS.getDS("KK");
			else if(recvCmd.equals(IPumpConstant.TH_COMMAND_KY)) // 현금거래처(현금)
				HE_ds = TsnSelfHSDS.getDS("KY");
			else if(recvCmd.equals(IPumpConstant.TH_COMMAND_KV)) // 현금거래처(신용)
				HE_ds = TsnSelfHSDS.getDS("KV");
			else if(recvCmd.equals(IPumpConstant.TH_COMMAND_KX)) // 현금승인요청-현금영수증 포함
				HE_ds = TsnSelfHSDS.getDS("KX");
			else if(recvCmd.equals(IPumpConstant.TH_COMMAND_LD)) // GS포인트 승인요청
				HE_ds = TsnSelfHSDS.getDS("LD");
			else if(recvCmd.equals(IPumpConstant.TH_COMMAND_LA)) // 외상승인요청-2
				HE_ds = TsnSelfHSDS.getDS("LA");
			
			//LogUtility.getPumpALogger().info("수신전문===" + recvCmd);
			
			// ODT 수신 데이터 세팅 (KD와 KV 는 할부개월수 자리수가 가변이므로 전 처리 필요)
			if(recvCmd.equals(IPumpConstant.TH_COMMAND_KD)) {	

				int bufLen=getBufferLength(RxBuf);

				if(bufLen==106) // 할부개월수 있음
					HE_ds.setByteStream(RxBuf);
				else { // 할부개월수 없음 (length==104)
					
					byte[] buf = new byte[106];
					System.arraycopy(RxBuf, 0, buf, 0, 60);
					buf[60] = '0'; buf[61] = '0'; // 할부개월수 00 을 세팅
					System.arraycopy(RxBuf, 60, buf, 62, 44);
					
					HE_ds.setByteStream(buf);
				}
			} 
			if(recvCmd.equals(IPumpConstant.TH_COMMAND_KV)) {	

				int bufLen=getBufferLength(RxBuf);

				if(bufLen==78) // 할부개월수 있음
					HE_ds.setByteStream(RxBuf);
				else { // 할부개월수 없음 (length==76)
					
					byte[] buf = new byte[78];
					System.arraycopy(RxBuf, 0, buf, 0, 74);
					buf[74] = '0'; buf[75] = '0'; // 할부개월수 00 을 세팅
					System.arraycopy(RxBuf, 74, buf, 76, 2);
					
					HE_ds.setByteStream(buf);
				}
			} 
			else
				HE_ds.setByteStream(RxBuf);
			
			String odtNo = TsnSelfHSDS.getOdtNo_forPOS((String) HE_ds.getValue("odtNo"));
			String targetNozzleNo = (String) HE_ds.getValue("nozzleNo");			
			
			//--- 현금 승인요청은 고객유형 확인없이 승인요청 함
			if (recvCmd.equals(IPumpConstant.TH_COMMAND_KE) || 		// 현금
					recvCmd.equals(IPumpConstant.TH_COMMAND_KX)) { 	// 현금(현금영수증)
				requestCardApprove("1", "0", recvCmd); // 승인요청
			}
			//--- GS포인트 승인요청은 고객유형 확인없이 승인요청 함
			else if (recvCmd.equals(IPumpConstant.TH_COMMAND_LD)) { // GS포인트
				requestCardApprove("3", "0", recvCmd); // 승인요청
			}
			//--- 현금거래처는 이미 KK 전문수신시 고객정보 조회 완료하였으므로 승인요청 함
			else if (recvCmd.equals(IPumpConstant.TH_COMMAND_KY) || // 현금거래처(현금)
					 recvCmd.equals(IPumpConstant.TH_COMMAND_KV)) { // 현금거래처(신용)
				requestCardApprove("1", "2", recvCmd); // 승인요청
			}			
			//--- 외상요청-2(LA전문)은 승인요청 함
			else if (recvCmd.equals(IPumpConstant.TH_COMMAND_LA)) { // 외상요청-2
				m_custCardNo  = m_custCardNo2;
				m_bonusCardNo = m_bonusCardNo2;				
				
				requestCardApprove("1", m_custType2, recvCmd); // 승인요청

				m_custCardNo  = "";
				m_bonusCardNo = "";
				m_custCardNo2  = "";
				m_bonusCardNo2 = "";
				m_custType2 = "";
			}
			else { //--- 기타(KD:신용, KK:외상)는 고객유형 확인
				
				if(recvCmd.equals(IPumpConstant.TH_COMMAND_KK)) { //-- 정보보관
					
					m_custCardNo  = (String) HE_ds.getValue("cardNo1");
					m_bonusCardNo = (String) HE_ds.getValue("cardNo2");
					
//test
//					LogUtility.getPumpALogger().info("[현성] m_custCardNo : " + m_custCardNo);
//					LogUtility.getPumpALogger().info("[현성] m_bonusCardNo : " + m_bonusCardNo);

					// 외상요청-2 용
					m_custCardNo2  = m_custCardNo;
					m_bonusCardNo2 = m_bonusCardNo;
					
					if (HE_ds.getValue("saleType").equals("A")) {	// 금액
						m_price = (String) HE_ds.getValue("value");
						m_isFullPumping = false;
						//m_presetMode = "0";
					} 
					else if (HE_ds.getValue("saleType").equals("Q")) {	// 수량
						m_liter = (String) HE_ds.getValue("value");
						m_isFullPumping = false;
						//m_presetMode = "1";
					} 
					else if (HE_ds.getValue("saleType").equals("F")) {	// 가득주유
						m_price = (String) HE_ds.getValue("value");
						m_isFullPumping = true;
						//m_presetMode = "0";
					}
				}
				
				// 고객유형 확인요청
				CA_WorkingMessage CA_wm = new CA_WorkingMessage();
				CA_wm.setNozzleNo(odtNo); // ODTNo
				CA_wm.setConnectNozzleNo(targetNozzleNo);
				String cardNo = (String) HE_ds.getValue("cardNo1");
				CA_wm.setCardNo(cardNo);

				LogUtility.getPumpALogger().info(new StringBuffer( "\n다쓰노셀프ODT(현성) 고객유형 확인요청(CA)" )
											 .append("\n ODT_No=" ).append( CA_wm.getNozzleNo() ) 
											 .append("\n nozzle=" ).append( CA_wm.getConnectNozzleNo() ).append("(CA)" )
											 //.append("\n cardNo=" ) Base64Util.encode(getCardTrack1Data(cardNo)) ) "\n");
											 .append("\n cardNo=" ).append( Base64Util.encode(getCardTrack1Data(cardNo)) ).append( "\n").toString());
	
				insertRecvQueue(CA_wm);
				CA_wm.print();	
			
			}				
		}
		//--- ODT 상태정보 ---//
		else if (recvCmd.equals(IPumpConstant.TH_COMMAND_OT)) {

			DataStruct OT_ds = TsnSelfHSDS.getDS(IPumpConstant.TH_COMMAND_OT);	
			OT_ds.setByteStream(RxBuf); // ODT 수신 데이터 세팅			
			//makeStatusCode((String) OT_ds.getValue("datas"));		
			makeStatusCode(RxBuf);
		}
/*		//--- ODT 이상정보 ---//
		else if (recvCmd.equals(IPumpConstant.TH_COMMAND_ES)) {

			DataStruct ES_ds = TsnSelfHSDS.getDS(IPumpConstant.TH_COMMAND_ES);	
			ES_ds.setByteStream(RxBuf); // ODT 수신 데이터 세팅			
			makeStatusInfo(Change.toValue((String) ES_ds.getValue("status")));
		}*/
	}
	
	public void processRecvSTX () throws SerialConnectException, Exception {
	
		//############# State check ##############//
		if (RxBuf[4]=='L' && RxBuf[5]=='K') { // 허가전 노즐다운
			nozState = (short) (nozState==4 ? 5 : 0);
		} 
		else if (RxBuf[4]=='A' && RxBuf[5]=='Q') { // 허가전 노즐업
			nozState = 1;
		}
		else if (RxBuf[4]=='U' && RxBuf[5]=='L') { // 허가후 노즐다운
			nozState = 2;
		}
		else if (RxBuf[4]=='P' && RxBuf[5]=='P') { // 허가후 노즐업(주유시작, 주유중)
			nozState = 4;
		}
		else if (RxBuf[4]=='T' && RxBuf[5]=='R') { 
			nozState = 5;
		} 
		else if (RxBuf[4]=='C' && RxBuf[5]=='T') { // 토털게이지 수신(주유시작 용)
		
			if (nozState==3) { // 노즐업 상태에서 수신
				nozState = 4;
			}
			else if (progressStep==4) { // 주유완료후 수신
				progressStep = 5;
			} 
			else 
				insertRecvQueue(generateWorkingMessage(RxBuf, "S5")); // 토털게이지 자료 송신
		}		
		else { // Self ODT 전문 수신 및 처리
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
			(dat[4]=='K' && dat[5]=='X')) { // 승인요청
			
			for (int i=20; i<20+40; i++) // 카드1
				dat[i] = '*';

			for (int i=20+40+2; i<20+40+2+40; i++) // 카드2
				dat[i] = '*';
		}
		else if ((dat[4]=='K' && dat[5]=='V')) {
			for (int i=36; i<36+38; i++) // 카드1
				dat[i] = '*';
		}
		else if ((dat[4]=='L' && dat[5]=='D')) {
			for (int i=20; i<20+40+17; i++) // 카드1
				dat[i] = '*';
		}
		
		return dat;
	}

	protected boolean requestCardApprove (String cardType, String custType, String recvCmd) throws Exception {
						
		String odtNo = TsnSelfHSDS.getOdtNo_forPOS((String) HE_ds.getValue("odtNo"));
		String targetNozzleNo = (String) HE_ds.getValue("nozzleNo");
		int nCardType = Change.toValue(cardType);
		m_nCustType = Change.toValue(custType);
		
		// 주유기 회선불량이면 승인실패 처리
		if(m_nozStateTbl.size() > 0 && m_nozStateTbl.get(targetNozzleNo).equals("601")) {
			sendFailMessageToODT("주유기 회선불량!", odtNo);
			return false;
		}
		
		HE_WorkingMessage HE_wm = new HE_WorkingMessage();
		
		HE_wm.setNozzleNo(odtNo); 					// ODT
		HE_wm.setConnectNozzleNo(targetNozzleNo); 	// 노즐
		HE_wm.setCardType(cardType); 				// 1 : 승인요청

//test
		
//		LogUtility.getPumpALogger().info("[현성] nCardType : " + nCardType);
//		LogUtility.getPumpALogger().info("[현성] m_nCustType : " + m_nCustType);
//		LogUtility.getPumpALogger().info("[현성] m_custCardNo : " + m_custCardNo);
//		LogUtility.getPumpALogger().info("[현성]  HE_ds.getValue(cardNo1) : " +  HE_ds.getValue("cardNo1"));
//		LogUtility.getPumpALogger().info("[현성] m_selectedPayType : " + m_selectedPayType);
		switch (nCardType) {
		
			case 1 : // 신용/현금/고객카드
				switch (m_nCustType) {
				
					//-- 일반고객(현금)
					case 0 : 
						HE_wm.setCustomerType("");
						
						if(recvCmd.equals("KE")) { 		// 현금요청
							HE_wm.setBonusCard((String) HE_ds.getValue("cardNo1"));     // 보너스카드
							HE_wm.setCashCount(m_cashTotalCount); 						// 투입금액
						}
						else if(recvCmd.equals("KX")) { // 현금요청(현금영수증)
							
							String target = (String) HE_ds.getValue("target");
							String loyaltyType = "";
							if(target.equals("1"))
								loyaltyType = "0"; // 소비자
							else if(target.equals("2"))
								loyaltyType = "1"; // 사업자
							
							HE_wm.setLoyaltyType(loyaltyType);
							HE_wm.setBonusCard((String) HE_ds.getValue("cardNo1"));     // 보너스카드
							HE_wm.setCashReceiptNo((String) HE_ds.getValue("cardNo2")); // 현금영수증카드
							HE_wm.setCashCount(m_cashTotalCount); 						// 투입금액
						} 
						break;
				
					//-- 일반고객(신용카드)
					case 1 : 
						HE_wm.setCustomerType("1");
						HE_wm.setCardNumber((String) HE_ds.getValue("cardNo1")); 	// 신용카드
						HE_wm.setBonusCard((String) HE_ds.getValue("cardNo2"));	 	// 보너스카드
						HE_wm.setMonthCount((String) HE_ds.getValue("monthCnt"));	// 할부개월수
						break;
		
					//-- 현금거래처고객
					case 2 : 
						HE_wm.setCustomerType("2"); 	
						HE_wm.setCustCardNo(m_custCardNo);	 // 고객카드
						HE_wm.setBonusCard(m_bonusCardNo);	 // 보너스카드
						
						if(recvCmd.equals("KY")) { 	// 현금거래처(현금요청)
							
							String target = (String) HE_ds.getValue("target");
							String loyaltyType = "";
							if(target.equals("1"))
								loyaltyType = "0"; // 소비자
							else if(target.equals("2"))
								loyaltyType = "1"; // 사업자

							HE_wm.setLoyaltyType(loyaltyType);
							HE_wm.setCashCount(m_cashTotalCount); 						// 현금입금액
							HE_wm.setCashReceiptNo((String) HE_ds.getValue("cardNo1")); // 현금영수증번호
						}
						else if(recvCmd.equals("KV")) { // 현금거래처(신용요청)
							HE_wm.setCardNumber((String) HE_ds.getValue("cardNo1")); 	// 신용카드
							HE_wm.setMonthCount((String) HE_ds.getValue("monthCnt"));	// 할부개월수
						}
						break;
		
					//-- 외상거래처고객
						
					// ksm 2014.02.10 신형다쓰노 승인요청시 결제수단 선택에 대한 상태정보 안넘어옴.
					// 추후 확인필요.
						
					case 3 : 						
						//if(m_selectedPayType.equals("3")) { // 고객카드선택
							HE_wm.setCustomerType("3");
							HE_wm.setCustCardNo(m_custCardNo); 		// 외상카드
							HE_wm.setBonusCard(m_bonusCardNo);	 	// 보너스
						//}
						break;
						
					case 4 : 		
						//if(m_selectedPayType.equals("3")) { // 고객카드선택
							HE_wm.setCustomerType("4"); // 정량고객
							HE_wm.setCustCardNo(m_custCardNo); 		// 외상카드
							HE_wm.setBonusCard(m_bonusCardNo);	 	// 보너스
						//}
						break;
		
					default :
						LogUtility.getPumpALogger().info("\n다쓰노셀프ODT(현성) 승인요청 전문(HE) 생성실패!");
						return false;
				}
				break;
				
			case 3 : // GS포인트 결제(LD전문 수신) 12.12.26 dhp 추가
				LogUtility.getPumpALogger().info("\nGS포인트 결제(LD전문 수신)");
				LogUtility.getPumpALogger().info("cardNo1=" + HE_ds.getValue("cardNo1"));
				LogUtility.getPumpALogger().info("pin=" + HE_ds.getValue("pin"));
				
				HE_wm.setCustomerType("");
				HE_wm.setCardNumber((String) HE_ds.getValue("cardNo1"));// GS 포인트카드
				HE_wm.setBonusPin((String) HE_ds.getValue("pin"));			// 비밀번호
				break;
		}

		// 금액/수량 처리
		if(recvCmd.equals("KY") || recvCmd.equals("KV") ) { // 현금거래처
			HE_wm.setPrice((String) HE_ds.getValue("price"));
			HE_wm.setLiter((String) HE_ds.getValue("liter"));
			/* ODT 요청전문에 가득주유시 'F'가 와야하지만 'A'가 수신되므로 의미없음.
			String isFullPumping = m_isFullPumping ? "1" : "0";
			HE_wm.setIsFullPumping(isFullPumping); */
		}
		else {
			if (HE_ds.getValue("saleType").equals("A")) {	// 금액
				HE_wm.setPrice((String) HE_ds.getValue("value"));
				HE_wm.setIsFullPumping("0");
			} 
			else if (HE_ds.getValue("saleType").equals("Q")) {	// 수량
				HE_wm.setLiter((String) HE_ds.getValue("value"));
				HE_wm.setIsFullPumping("0");
			} 
			else if (HE_ds.getValue("saleType").equals("F")) {	// 가득주유
				HE_wm.setPrice((String) HE_ds.getValue("value"));
				HE_wm.setIsFullPumping("1");
			}
		}
		
		if(Change.toValue(HE_wm.getPrice()) >= 149900) {
			HE_wm.setIsFullPumping("1");
			m_isFullPumping = true;
		}

		HE_wm.setBasePrice(m_basePriceTbl.get(HE_wm.getConnectNozzleNo())); // 단가

		//HE_wm.print();	
		
		//이강호2014-07-15 
		//보너스사용시 로그상 비밀번호 마스킹처리함. 
		////////////////////////////////////////////////////////////////////
		String tmpBonusPin = HE_wm.getBonusPin();
		if( !"".equals(tmpBonusPin) )
			tmpBonusPin = "****";
		////////////////////////////////////////////////////////////////////
		LogUtility.getPumpALogger().info(new StringBuffer("\n다쓰노셀프ODT 승인요청 전문(HE)" )
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
				.append("\n loyaltyTy=" ).append( HE_wm.getLoyaltyType() ).append( "(0:소비자, 1:사업자)" ) // 현금영수증 거래구분
				.append("\n monthCnt =" ).append( HE_wm.getMonthCount() )
				.append("\n bonusPin =" ).append( tmpBonusPin ).append("(GS Point 사용)\n").toString());	
		
		insertRecvQueue(HE_wm);

		makeStatusInfo(236, m_nozzleNo); // 승인요청 중
		
		HE_ds = null;
		m_cashTotalCount = "0";
		m_custCardNo = "";
		m_bonusCardNo = "";
		
		return true;
	}

	
	@Override
	public void requestData() throws SerialConnectException, Exception {

		// 회선불량 처리
		boolean	occurLineErr = (lineErrCnt >= minErrCnt);

		if (occurLineErr) { // Check line error

			if (issueLineErr==true) {
				makeLineError(); // 회선불량
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
					TxQue.enQueue(wmByte); // 환경설정정보가 아니면
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
						
						short result = checkRecvData(RxBuf); // ID, BCC, Data format 확인
						
						if (result==1) {
							lineErrCnt++;
							continue;
						} else if (result==2) {
							lineErrCnt++;
							return;
						} else if (result==0) { //  STX 정상수신
							
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
							processRecvSTX(); //--- 노즐 수신데이터 처리 ---//
					
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

					if (TxQue.isEmpty()==false) { // 송신 데이터 있으면
						
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
							LogUtility.getPumpALogger().debug("수신대기시간 추가. TxBuf.length=" + TxBuf.length + 
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

								TxQue.deQueue(); //-- 송신완료후 송신데이터 제거							

								//--- 응답데이터 확인(ACK + SOH + Datas) ---//								
								flushBuffer(RxBuf);								
								if (recvText(RxBuf) > 0) {
									if (RxBuf[0] == SOH) { // recv reply data : SOH
										
										if (dispLevel>=3) {
											LogUtility.getPumpALogger().info ("2.Recv STX (Noz="+nozNo+")");
											Log.datas(protectCardNumber(RxBuf), buffSize, 20);
										}
										
										short result = checkRecvData(RxBuf); // ID, BCC, Data format 확인
										
										if (result==1) {
											lineErrCnt++;
											continue;
										} else if (result==2) {
											lineErrCnt++;
											return;
										} else if (result==0) { //  STX 정상수신
											
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
											processRecvSTX(); //--- 노즐 수신데이터 처리 ---//
										}
									}
								}

								lineErrCnt = 0; // Normal terminated
								return;
							}
							else if (RxBuf[0] == NAK) {	// recv : NAK
								
								sendText(TxBuf); // 재전송
									
								LogUtility.getPumpALogger().info("2.Send STX fail!(Returned NAK)->retry send (Noz="+nozNo+")");
								Log.datas(TxBuf, TxBuf.length, 20);
								continue;
							}							
						} // end of for (int j=0; j<3; j++)
					}
					else { //--- 송신 데이터 없으면 ---//
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
				P3_wm.setNozzleNo(P5_1_wm.getNozzleNo()); 				// ODT번호
				P3_wm.setTargetNozzleNo(nozzleInfo.getNozzleNumber());  // 노즐번호
				P3_wm.setConnectNozzleNo(P5_1_wm.getNozzleNo());
				P3_wm.setBasePrice(nozzleInfo.getBasePrice());  		// 단가
				P3_wm.setPassThrough(false);

				insertRecvQueue(P3_wm);					
				
			}				
	}


	protected void sendFailMessageToODT (String msg, String odtNo) throws Exception {
		
		// 승인실패 전문전송
		DataStruct QM_ds = new DataStruct();
		QM_ds.addString("command", "KG", 2);	// Command(승인실패)	
		QM_ds.addString("errorCode", "", 4); 	// 오류번호 
		QM_ds.addString("message", msg, 48); 	// 메시지(가변, 최대 48 byte)
		byte[] tempArray = QM_ds.getByteStream();	
		byte[] QM_buf = trans.makeProtocol(tempArray, odtNo);						
		TxQue.enQueue(QM_buf);	
	}
	
	
	protected void sendInitData () throws Exception { // 스마트주유소 PJT 추가
		
		//-- 주유기 그룹핑 정보(JP)
		TxQue.enQueue (JP_Buf); //			
		
		//-- 유종정보(YL)
		if(YL_Buf[0].length > 22) TxQue.enQueue (YL_Buf[0]); // kixx
		if(YL_Buf[1].length > 22) TxQue.enQueue (YL_Buf[1]); // kixxPrime
		if(YL_Buf[2].length > 22) TxQue.enQueue (YL_Buf[2]); // diesel
		if(YL_Buf[3].length > 22) TxQue.enQueue (YL_Buf[3]); // advDiesel		        	
		
		//-- 주유기 단가정보 처리(셀프주유기는 P3_1 전문이 수신되지 않으므로  P3_1 전문을 주유기에 전송)
		if(P5_1_wm != null)
			send_P3_1_workingMessage (); // 노즐에 P3 전문전송
		else
			LogUtility.getPumpALogger().info("P5 전문 미수신으로 send_P3_1_workingMessage() 미실행!");
		
		//-- ODT 옵션정보(KP)
		//TxQue.enQueue (KP_Buf); // kixx			
		
		//-- 기타전문
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
			
			//--- 데이터 검증(데이터 길이와 ETX 확인) ---//
			if (!(bufLen==104 || bufLen==106) || buf[bufLen-2] != ETX) {
				LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> 이상데이터 수신("+cmd+"), 데이터길이=104 또는 106(할부시)" + 
						", 수신데이터길이=" + bufLen);
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
			size=42; valStart=11; valEnd=39; // KY로 변경
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

			//--- 데이터 검증(데이터 길이와 ETX 확인) ---//
			if (!(bufLen==76 || bufLen==78) || buf[bufLen-2] != ETX) {
				LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> 이상데이터 수신("+cmd+"), 데이터길이=104 또는 106(할부시)" + 
						", 수신데이터길이=" + bufLen);
				//Log.datas(buf, buf.length, 20);
				return false;
			}
		}
		else {
			return true;
		}
		
		//--- KD이외의 데이터 검증(데이터 길이와 ETX 확인) ---//
		if (!cmd.equals("KD") && !cmd.equals("KV")) {
			if (size != bufLen || buf[size-2] != ETX) {
				LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> 이상데이터 수신("+cmd+"), 데이터길이=" + 
						size + ", 수신데이터길이=" + bufLen);
				//Log.datas(buf, buf.length, 20);
				return false;
			}
		}

		//--- Value 검증(숫자여부) ---//
		byte tmp;
		for (int i=valStart; i<=valEnd; i++) {
			tmp = buf[i]==0x20 ? 0x30 : buf[i]; // ' ' 는 '0'으로
			if (tmp < 0x30 || tmp > 0x39) { // 0 ~ 9 가 아니면
				LogUtility.getPumpALogger().debug("nozzle=" + nozNo + 
						" -> 이상데이터 수신("+cmd+"), 숫자가 아님, 수신데이터=" + buf[i]);
				return false;
			}
		}

		return true;
	}
}
