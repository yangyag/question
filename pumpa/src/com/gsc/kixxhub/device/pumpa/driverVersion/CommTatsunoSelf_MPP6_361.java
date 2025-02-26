// 다쓰노셀프 
package com.gsc.kixxhub.device.pumpa.driverVersion;

import java.util.Formatter;
import java.util.Vector;

import com.gsc.kixxhub.common.data.pump.BC_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.BI_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.CA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.CB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HE_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P5_1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P5_NozzleInfo;
import com.gsc.kixxhub.common.data.pump.P7_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.QL_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.QM_WoringMessage;
import com.gsc.kixxhub.common.data.pump.S3_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S4_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.TR_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.data.pump.format.PumpMessageFormat;
import com.gsc.kixxhub.common.utility.Base64Util;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.Change;
import com.gsc.kixxhub.device.pumpa.common.SerialConnectException;
import com.gsc.kixxhub.device.pumpa.driver.CommTatsunoSelf_MPP6;

public class CommTatsunoSelf_MPP6_361 extends CommTatsunoSelf_MPP6  {

	protected BC_WorkingMessage BC_wm;
	protected BI_WorkingMessage BI_wm;
	
	protected boolean	m_bCustCheck=false;
	protected boolean	m_bInsertedCash=false;
	// for romVer=361(BNA)
	protected String  	m_cashCount="";
	protected String  	m_cashReceiptNo="";
	protected String  	m_custCardNo="";
	protected boolean	m_isBNANormal=true;
	protected int		m_nCashReceiptState=0;
	protected int		m_nCustType=1; // 1=일반
	protected String 	m_sSaveBonus="0";
	
	protected boolean	m_useJustStop=false;
	
	public CommTatsunoSelf_MPP6_361 (int nozNum, String romVerStr, int nMPPCount) {
		
		super(nozNum, romVerStr, nMPPCount);
						
		try {
			// 금액버튼 설정
			m_sPriceButtArr[0][0] = "00005000";
			m_sPriceButtArr[0][1] = "00010000";
			m_sPriceButtArr[0][2] = "00020000";
			m_sPriceButtArr[0][3] = "00030000";
			m_sPriceButtArr[0][4] = "00040000";
			m_sPriceButtArr[0][5] = "00050000";
			m_sPriceButtArr[1][0] = "00060000";
			m_sPriceButtArr[1][1] = "00070000";
			m_sPriceButtArr[1][2] = "00080000";
			m_sPriceButtArr[1][3] = "00090000";
			m_sPriceButtArr[1][4] = "00100000";
			m_sPriceButtArr[1][5] = "00149900";
			
			m_sDispPriceArr[0][0] = " W5,000";
			m_sDispPriceArr[0][1] = "W10,000";
			m_sDispPriceArr[0][2] = "W20,000";
			m_sDispPriceArr[0][3] = "W30,000";
			m_sDispPriceArr[0][4] = "W40,000";
			m_sDispPriceArr[0][5] = "W50,000";
			m_sDispPriceArr[1][0] = "W60,000";
			m_sDispPriceArr[1][1] = "W70,000";
			m_sDispPriceArr[1][2] = "W80,000";
			m_sDispPriceArr[1][3] = "W90,000";
			m_sDispPriceArr[1][4] = "100,000";
			m_sDispPriceArr[1][5] = "Full   ";

			m_sMentPriceArr[0][0] = "28";
			m_sMentPriceArr[0][1] = "1323";
			m_sMentPriceArr[0][2] = "1423";
			m_sMentPriceArr[0][3] = "1523";
			m_sMentPriceArr[0][4] = "1623";
			m_sMentPriceArr[0][5] = "1723";
			m_sMentPriceArr[1][0] = "1823";
			m_sMentPriceArr[1][1] = "1923";
			m_sMentPriceArr[1][2] = "2023";
			m_sMentPriceArr[1][3] = "2123";
			m_sMentPriceArr[1][4] = "2223";
			m_sMentPriceArr[1][5] = "44";
			
			//t40_ds.editByte  ("inputCond1", (byte) 0x47); // BNA 활성화
			
		} catch (Exception e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	}
	
	protected void cancelInsertedCash() throws Exception {
		
		if (m_bInsertedCash==true) { // 지폐투입 후 취소시 취소처리(환불)
			
			BC_wm = new BC_WorkingMessage();
			BC_wm.setNozzleNo(nozStr); // ODTNo
			BC_wm.setConnectNozzleNo(m_targetNozzle); // nozNo
			BC_wm.setCashCount(m_cashCount);

			insertRecvQueue(BC_wm);
		}
	}

	protected void cashInsertEnd_proc(String sTotCash) throws Exception {
		
		int	nTotCash = Change.toValue(sTotCash);
	
		LogUtility.getPumpALogger().info("###### 13 : 지폐투입 완료하였습니다. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
		LogUtility.getPumpALogger().info("            투입 합계금액=" + m_price + "원\n");
	
		// Reset BNA Cash-count
		insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x43, (byte)0x81); //입력요구
		TxQue.enQueue(t43_Buf);
		
		//insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x43, (byte)0xA0); //입력요구
		insertDisplayData_23_new("01", "13", (byte)'3', (byte)'3', false, "        ");
		insertDisplayData_23_new("02", "01", (byte)'3', (byte)'3', false, "W" + Change.toMoneyString("#,##0", nTotCash));
	
	}
	
	protected String getWonVoice(String sCash) throws Exception {

		String wonVoice="";
		
		switch (Change.toValue(sCash)) {
		
		case 1000 :
			wonVoice = "24";
			break;

		case 5000 :
			wonVoice = "28";
			break;

		case 10000 :
			wonVoice = "1323";
			break;
		}		
				
		return wonVoice;
	}
	
	@Override
	protected void initVariables () throws  Exception {
		
		m_bCreditMode=false;
		m_bBonusMode=false;
		m_bFirstPumping=true;
		m_bFirstPumpingEnd=true;
		m_oilKind= 0;
		m_liter= "0";
		m_price= "0";
		m_creditNo = "";
		m_bonusNo = "";
		
		m_custCardNo = "";
		m_cashCount = "";
		m_cashReceiptNo = "";
		m_bInsertedCash=false;
		m_nCustType=1;
		m_bCustCheck=false;
		m_nCashReceiptState=0;
		m_sSaveBonus="0";
		
		insertDevCtrlOrder_42((byte)'0', "00", "02", "000"); // 타이머 중지
		insertInitCmd();
		m_useJustStop = false;
//		m_rcvEnableFromPOS = false;
//		m_rcvCancelFromPOS = false;
	}
	
	protected void inputException_proc () throws Exception {
		
		switch(m_nODTState) {
		case 1 :
			insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x43, (byte)0x81);
			break;
		case 3 :
			if (m_bInsertedCash==false && m_isBNANormal==true)
				insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x47, (byte)0x82);
			else
				insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x43, (byte)0x82);
			break;
		case 4 :
			insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x43, (byte)0x84);
			break;
		case 5 :
			insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x43, (byte)0xA0);
			break;
		}
	}
		
	//----- BNA용 입력오더 요구(40) -----//
	protected void insertInputOrder_40 (byte sequence, String orderNo, byte orderCond, 
			byte inputCond1, byte inputCond2) throws Exception {

		byte[] sndBuf;
		
		try {

			t40_ds.editByte("sequence", sequence);
			t40_ds.editString("orderNo", orderNo, 2);
			t40_ds.editByte("orderCond", orderCond);

			if (inputCond1==0x00) { // 디바이스 활성화 제어
				t40_ds.editByte("inputCond1", (byte) ' ');
				t40_ds.editByte("inputCond3", (byte) ' '); 
			}
			else {
				t40_ds.editByte("inputCond1", inputCond1);
				t40_ds.editByte("inputCond3", (byte) 0x45); // 고정
			}
			
			if (inputCond2==0x00) { // 램프 제어
				t40_ds.editByte("inputCond2", (byte) ' ');
				t40_ds.editByte("inputCond4", (byte) ' ');
			}
			else {
				t40_ds.editByte("inputCond2", inputCond2);
				if (inputCond1==0x47)
					//t40_ds.editByte("inputCond4", (byte) 0x86);
					t40_ds.editByte("inputCond4", (byte) 0x82);
				else
					t40_ds.editByte("inputCond4", (byte) ' ');
			}
			
			sndBuf = t40_ds.getByteStream();
				
			TxQue.enQueue(sndBuf);

//			LogUtility.getPumpALogger().debug(">>>>Insert command 40. itemCount=" + TxQue.getItemCount() + " (ODT="+nozNo+")");
//			Log.datas(sndBuf, sndBuf.length, 20);
		
		} catch (Exception e){
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	}

	@Override
	protected void insertOPTFile() throws Exception {

		char x0D = 0x0D;
		char x0A = 0x0A;
		
		String fileData = 	"000" +
							"2" +
							"1" + // BNA
							this.generateBlank(214) +
							"11" +
							"010" +
							"05" +
							this.generateBlank(14) + x0D + x0A;
		
		//--- Start block ---//
		t02sb_ds.editString("command", "02", 2);
		t02sb_ds.editByte  ("sequence", (byte) '0'); //sequence
		t02sb_ds.editString("block", "1", 1);
		t02sb_ds.editString("fileNo", "01", 2);
		t02sb_ds.editString("fileName", "OPT01000", 8);
		t02sb_ds.editString("fileExt", "000", 3);
		t02sb_ds.editString("outputDevice", "00", 2);
		t02sb_ds.editString("reserved", "", 20);
		t02sb_Buf = t02sb_ds.getByteStream();
		
		TxQue.enQueue(t02sb_Buf);
		
		//--- Data block ---//
		t02_ds.editString("command", "02", 2);
		t02_ds.editByte  ("sequence", (byte) '0'); //sequence
		t02_ds.editString("block", "2", 1);
		t02_ds.editString("fileNo", "01", 2);
		t02_ds.editString("dataSize", "242", 3);
		t02_ds.editString("fileData", fileData, fileData.length());	
		t02_Buf = t02_ds.getByteStream();

//		LogUtility.getPumpALogger().debug(">>>>>>>>>>>>>>>>>>>>>");
//		Log.datas(t02_Buf, t02_Buf.length, 20);
		
		TxQue.enQueue(t02_Buf);
		
		//--- End block ---//
		t02eb_ds.editString("command", "02", 2);
		t02eb_ds.editByte  ("sequence", (byte) '0'); //sequence
		t02eb_ds.editString("block", "3", 1);
		t02eb_ds.editString("fileNo", "01", 2);
		t02eb_ds.editString("blockCnt", "001", 3);
		t02eb_ds.editString("reserved", "", 30);
		t02eb_Buf = t02eb_ds.getByteStream();
		
		TxQue.enQueue(t02eb_Buf);
		
	}

	@Override
	protected boolean isSkipWorkingMessage (WorkingMessage wm) throws Exception {
		
		boolean skip=false;

		if (wm.getCommand().equals("P5_1")) { // 셀프 환경설정
			
			P5_1_WorkingMessage P5_1_wm = (P5_1_WorkingMessage) wm;
			Vector<P5_NozzleInfo> nozInfoVec = P5_1_wm.getNozzleInfo();
			int nozCnt = nozInfoVec.size();

			LogUtility.getPumpALogger().info("[Pump A][수신-다쓰노셀프ODT 노즐/단가 정보]" + 
					" ODT_No=" + P5_1_wm.getNozzleNo() + "("+P5_1_wm.getCommand()+")" +
					" | mode=" + P5_1_wm.getMode() + 
					" | useFullPumping=" + P5_1_wm.getUseFullPumping());
			
			// 단가 정상수신 확인(추가 2010/2/22)
			if (P5_1_wm.getMode().equals("0") || P5_1_wm.getMode().equals("1"))
				m_isCompleteBasePrice = isCompleteBasePrice(P5_1_wm);
			
			if (P5_1_wm.getMode().equals("0")) { // 초기화
				
				for (int i=0; i<nozCnt; i++) {
					
					P5_NozzleInfo P5_nozWm = nozInfoVec.get(i);
					String szNozNo = P5_nozWm.getNozzleNumber();
					LogUtility.getPumpALogger().info("nozzle="+ P5_nozWm.getNozzleNumber() + "(P5_1)");
					LogUtility.getPumpALogger().info("bPrice="+ P5_nozWm.getBasePrice());
					LogUtility.getPumpALogger().info("oilType="+ P5_nozWm.getGoodsCode());
					LogUtility.getPumpALogger().info("oilCode="+ P5_nozWm.getGoodsType() + "\n");
					
					m_nozNoVec.add(szNozNo);
					m_basePriceTbl.put(szNozNo, P5_nozWm.getBasePrice());
					m_oilCodeTbl.put(szNozNo, P5_nozWm.getGoodsCode());
					m_oilNameTbl.put(szNozNo, P5_nozWm.getGoodsType());
				}
				// 가득주유 사용여부
				m_useFullPumping = P5_1_wm.getUseFullPumping().equals("1")? true : false;

				m_setEnvDataOK=true;
			}
			else if (P5_1_wm.getMode().equals("1")) { // 단가변경
				
				for (int i=0; i<nozCnt; i++) {
					P5_NozzleInfo P5_nozWm = nozInfoVec.get(i);
					String szNozNo = P5_nozWm.getNozzleNumber();
					String beforeBasePrice_ = m_basePriceTbl.get(szNozNo).substring(0,4);

					m_basePriceTbl.remove(szNozNo);
					m_basePriceTbl.put(szNozNo, P5_nozWm.getBasePrice());
					
					/*
					// 추가(09/03/05) -> 주유기 단가 즉시변경 필요시
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

					LogUtility.getPumpALogger().info("단가변경용 P5수신, 단가변경. nozzle="+szNozNo+" : "+beforeBasePrice_+
							" -> " +P5_nozWm.getBasePrice().substring(0,4)+" (TableSize="+m_basePriceTbl.size()+")");
				}
			}
			else if (P5_1_wm.getMode().equals("2")) { // 가득주유 옵션변경
				
				m_useFullPumping = P5_1_wm.getUseFullPumping().equals("1")? true : false;

				LogUtility.getPumpALogger().info("가득주유 옵션변경용 P5수신, 옵션변경. ODT_No="+P5_1_wm.getNozzleNo() +
						" useFullPumping=" + P5_1_wm.getUseFullPumping());
			}
			
			skip = true;
		}
		else if (wm.getCommand().equals("PB")) { // 선결제 또는 정액정량설정
			PB_wm = (PB_WorkingMessage) wm;
			m_rcvEnableFromPOS = true;
			m_rcvCancelFromPOS = false;
			
			LogUtility.getPumpALogger().info("[Pump A][수신-다쓰노셀프ODT 선결제]" + " nozzle=" + 
					 PB_wm.getConnectNozzleNo() + "("+PB_wm.getCommand()+")" +
					 " | ODT_No=" + PB_wm.getNozzleNo() +
					 " | mode=" + PB_wm.getCommandSet() + 
					 " | liter=" + PB_wm.getLiter() + 
					 " | basePrice=" + PB_wm.getBasePrice() + 
					 " | price=" + PB_wm.getPrice());

			m_targetNozzle=PB_wm.getConnectNozzleNo();
			m_oilKind = getOilKind(PB_wm.getConnectNozzleNo());
			m_liter = PB_wm.getLiter();
			m_basePrice = PB_wm.getBasePrice();
			m_price = PB_wm.getPrice();
			int mode = Change.toValue(PB_wm.getCommandSet());
			
			if (mode==0) { // 정액설정
				m_nSetPrice = Change.toValue(m_price);
				m_dispPriceLiter = "W" + Change.toMoneyString("#,##0", m_nSetPrice);
			}
			else { // 정량설정
				m_nSetPrice = (Change.toValue(m_liter)/1000) * Change.toValue(m_basePrice.substring(0,4)) - 1;
				m_dispPriceLiter = Change.toString(Change.toValue(m_liter.substring(0,4))) + "." + 
							m_liter.substring(4,6) + "L";
			}
			
			String oilName = m_oilNameTbl.get(PB_wm.getConnectNozzleNo());
			
			m_nODTState = 7;
			insertInitCmd();
							
			// 승인되었습니다. OO색 손잡이가 000입니다. 주유기를 들고 주유해 주세요.
			insertDevCtrlOrder_42((byte)'0', "00", "02", "000"); // 타이머 중지
			insertDevCtrlOrder_42((byte)'0', "11", "02", "000"); // 음성 중지
			insertDevCtrlOrder_42((byte)'0', "02", "01", "00000010"+"0000000000000000");
			insertDisplayData_23_new("01", "01", (byte)'3', (byte)'3', true, oilName);
			insertDisplayData_23_new("02", "01", (byte)'3', (byte)'3', false, m_dispPriceLiter);
			insertDisplayData_23_new("02", "10", (byte)'3', (byte)'3', false, "OK!       ");
	
			if (DIESEL == m_oilKind) {
				insertDevCtrlOrder_42((byte)'0', "11", "01", "1005760756300");
			}
			else if (ADIESEL == m_oilKind) {
				insertDevCtrlOrder_42((byte)'0', "11", "01", "1005762756300");
			}
			else if (GASOLINE == m_oilKind) {
				insertDevCtrlOrder_42((byte)'0', "11", "01", "1005759756300");
			}
			else if (PGASOLINE == m_oilKind) {
				insertDevCtrlOrder_42((byte)'0', "11", "01", "1005761756300");
			}

			makeStatusInfo (235); // 주유대기
			
			skip = true;
		}
		else if (wm.getCommand().equals("PA")) { // 선결제 취소. 추가(2010/05/03)
			PA_wm = (PA_WorkingMessage) wm;
			
			if (m_rcvEnableFromPOS==true && PA_wm.getNozzleState().equals("0")) {
				
				m_rcvCancelFromPOS = true; // PB 수신후 PA(mode=0) 수신
				insertDisplayData_23_new("02", "01", (byte)'1', (byte)'1', true, "Preset canceling..."); // 추가 (2010/11/18)
				insertDevCtrlOrder_42((byte)'0', "11", "01", "1005800");
				
				LogUtility.getPumpALogger().info("[Pump A][수신-다쓰노셀프ODT 선결제 취소]" + " nozzle=" + 
						PA_wm.getConnectNozzleNo() + "("+PA_wm.getCommand()+")" +
						 " | ODT_No=" + PA_wm.getNozzleNo() +
						 " | mode=" + PA_wm.getNozzleState() + "\n");
			}
			
			skip = true;
		}
		else if (wm.getCommand().equals("QM")) { // 카드 승인응답
			QM_wm = (QM_WoringMessage) wm;
			
			LogUtility.getPumpALogger().info("[Pump A][수신-다쓰노셀프ODT 승인응답]" + " nozzle=" + 
					QM_wm.getConnectNozzleNo() + "("+QM_wm.getCommand()+")" +
					" | ODT_No=" + QM_wm.getNozzleNo() +
					" | mode=" + QM_wm.getMode() +  "(1:승인, 2:거부, 3:미응답)" +
					" | liter=" + QM_wm.getLiter() +
					" | basePrice=" + QM_wm.getBasePrice() +
					" | price=" + QM_wm.getPrice() +
					" | certiInfo=" + QM_wm.getCertiInfo() +
					" | certiTime=" + QM_wm.getCertiTime());
			
			short mode = (short) Change.toValue(QM_wm.getMode());
			mode = m_isCompleteBasePrice==false? 0 : mode; // 단가확인
			
			if (mode == 1) { // 승인OK
				
				m_nODTState = 7;
				
				PB_wm = new PB_WorkingMessage();
				PB_wm.setPassThrough(false);
				PB_wm.setNozzleNo(wm.getNozzleNo()); // ODT No
				PB_wm.setConnectNozzleNo(wm.getConnectNozzleNo()); // Nozzle No
				PB_wm.setTargetNozzleNo(wm.getConnectNozzleNo());  // Nozzle No
				PB_wm.setCommandSet(Change.toString(m_preset));
				
//				PB_wm.setLiter(m_liter);
//				PB_wm.setBasePrice(m_basePrice);
//				PB_wm.setPrice(m_price);
				
				// 수정 for BNA 버전
				PB_wm.setLiter(QM_wm.getLiter());
				PB_wm.setBasePrice(QM_wm.getBasePrice());
				PB_wm.setPrice(QM_wm.getPrice());
				
				insertRecvQueue(PB_wm);
				
				//--- 주유금액 설정
				m_nSetPrice = Change.toValue(m_price);
				m_dispPriceLiter = "W" + Change.toMoneyString("#,##0", m_nSetPrice);
				
				LogUtility.getPumpALogger().info("승인OK(QM), 주유허가(PB) 발생 : ODT_No=" + QM_wm.getNozzleNo()+ 
						" nozzle=" + QM_wm.getConnectNozzleNo() + " liter=" + m_liter + 
						" bPrice=" + m_basePrice + " price=" + m_price);
				
				makeStatusInfo (235); // 주유대기
				
				insertDevCtrlOrder_42((byte)'0', "00", "02", "000"); // 타이머 중지
				insertDevCtrlOrder_42((byte)'0', "11", "02", "000"); // 음성 중지
				insertDevCtrlOrder_42((byte)'0', "02", "01", "00000010"+"0000000000000000");
				insertDisplayData_23_new("02", "10", (byte)'3', (byte)'3', false, "OK!       ");
				
				// 승인되었습니다. OO색 손잡이가 000입니다. 주유기를 들고 주유해 주세요.
				if (DIESEL == m_oilKind) {
					insertDevCtrlOrder_42((byte)'0', "11", "01", "1005760756300");
				}
				else if (ADIESEL == m_oilKind) {
					insertDevCtrlOrder_42((byte)'0', "11", "01", "1005762756300");
				}
				else if (GASOLINE == m_oilKind) {
					insertDevCtrlOrder_42((byte)'0', "11", "01", "1005759756300");
				}
				else if (PGASOLINE == m_oilKind) {
					insertDevCtrlOrder_42((byte)'0', "11", "01", "1005761756300");
				}

			}
			else if (mode==2 || // 승인거절
					 mode==3) { // 미응답
				
				if (m_nODTState==6) {
								
					//승인을 받을수 없습니다. 사무실에서 확인하여 주세요.
					insertDevCtrlOrder_42((byte)'0', "00", "02", "000"); // 타이머 중지
					insertDevCtrlOrder_42((byte)'0', "11", "02", "000"); // 음성 중지
					insertDevCtrlOrder_42((byte)'0', "11", "01", "100587575676900");
					insertDisplayData_23_new("02", "07", (byte)'3', (byte)'3', true, "Cancel!       ");
					insertDevCtrlOrder_42((byte)'0', "02", "01", "00000000"+"0000000000000000");
					
					// 초기상태(유종선택)로 가기위함
					insertDevCtrlOrder_42((byte)'0', "00", "01", "010"); // 타이머 시작

					LogUtility.getPumpALogger().info("승인거절(QM) : ODT_No=" + QM_wm.getNozzleNo()+ 
							" nozzle=" + QM_wm.getConnectNozzleNo() + " liter=" + m_liter + 
							" bPrice=" + m_basePrice + " price=" + m_price + " m_nODTState="+m_nODTState);

					m_nODTState=8;
				}
			}
			else if (mode == 0) { // 단가이상시 주유취소 
				S4_WorkingMessage S4_wm = new S4_WorkingMessage ();
				S4_wm.setNozzleNo(QM_wm.getConnectNozzleNo()); // NozzleNo
				S4_wm.setConnectNozzleNo(QM_wm.getNozzleNo()); // ODTNo
				insertRecvQueue(S4_wm);
				
				LogUtility.getPumpALogger().debug("단가(P5)이상으로 주유취소 ODT_No=" + S4_wm.getNozzleNo());
			}

			skip = true;
		}
		else if (wm.getCommand().equals("S3")) { // 주유중
			S3_wm = (S3_WorkingMessage) wm;
			
			if (m_bFirstPumping==true) {
				
				m_bFirstPumping = false;
				insertInitCmd();
				
				//선택하신량보다 적게 주유시 정액버턴을 누르면 천원단위 주유가 됩니다.
				insertDevCtrlOrder_42((byte)'0', "00", "02", "000"); // 타이머 중지
				insertDevCtrlOrder_42((byte)'0', "11", "02", "000"); // 음성 중지
				insertDevCtrlOrder_42((byte)'0', "11", "01", "10070717200");

				insertDisplayData_23_new("01", "01", (byte)'3', (byte)'3', true, m_dispPriceLiter);
				insertDisplayData_23_new("02", "01", (byte)'3', (byte)'3', false, "Starting...");
				
			}

			//String szLiter1 = S3_wm.getLiter().substring(0, 4);
			//String szLiter2 = S3_wm.getLiter().substring(4, 7);
						
			if (Change.toValue(S3_wm.getPrice()) >= m_nSetPrice) { 

				if (m_bFirstPumpingEnd==true) {
					m_bFirstPumpingEnd=false;
					
					// 주유가 완료 되었습니다.
					insertDevCtrlOrder_42((byte)'0', "00", "02", "000"); // 타이머 중지
					insertDevCtrlOrder_42((byte)'0', "11", "02", "000"); // 음성 중지
					insertDevCtrlOrder_42((byte)'0', "11", "01", "0007400");
					
					insertDisplayData_23_new("02", "01", (byte)'1', (byte)'1', true, "End!");

					LogUtility.getPumpALogger().info("설정액 만큼 주유가 되었습니다. ODT_No=" + S3_wm.getNozzleNo()+ 
							" nozzle=" + S3_wm.getConnectNozzleNo() + 
							" 설정액=" + m_nSetPrice+ " 주유액="+ Change.toValue(S3_wm.getPrice()));
					
				}
			}
			
			/*
			// 단위 멈춤(정액/정량 멈춤) 테스트
			
			if (m_useJustStop==false && Change.toValue(S3_wm.getPrice()) >= 6000) { 

				m_useJustStop = true;
				
				PA_wm = new PA_WorkingMessage();
				PA_wm.setPassThrough(false);
				PA_wm.setNozzleNo(S3_wm.getNozzleNo()); // ODT No
				PA_wm.setConnectNozzleNo(S3_wm.getConnectNozzleNo()); // Nozzle No
				PA_wm.setTargetNozzleNo(S3_wm.getConnectNozzleNo());  // Nozzle No
				PA_wm.setNozzleState("0");
				insertRecvQueue(PA_wm);
				
//				PA_wm = new PA_WorkingMessage();
//				PA_wm.setPassThrough(false);
//				PA_wm.setNozzleNo(S3_wm.getNozzleNo()); // ODT No
//				PA_wm.setConnectNozzleNo(S3_wm.getConnectNozzleNo()); // Nozzle No
//				PA_wm.setTargetNozzleNo(S3_wm.getConnectNozzleNo());  // Nozzle No
//				PA_wm.setNozzleState("1");
//				insertRecvQueue(PA_wm);
				
				PB_wm = new PB_WorkingMessage();
				PB_wm.setPassThrough(false);
				PB_wm.setNozzleNo(S3_wm.getNozzleNo()); // ODT No
				PB_wm.setConnectNozzleNo(S3_wm.getConnectNozzleNo()); // Nozzle No
				PB_wm.setTargetNozzleNo(S3_wm.getConnectNozzleNo());  // Nozzle No
				PB_wm.setCommandSet("0");
				PB_wm.setLiter("0000000");
				PB_wm.setBasePrice(S3_wm.getBasePrice());
				PB_wm.setPrice("00010000");
				insertRecvQueue(PB_wm);
				
				LogUtility.getPumpALogger().info("=====> 정액/정량 멈춤 전문 전송.... ODT_No=" + S3_wm.getNozzleNo());
			}
			*/
			

			skip = true;
		}
		else if (wm.getCommand().equals("S4")) { // 주유완료
			S4_wm = (S4_WorkingMessage) wm;

			m_bFromODT = false;
			m_bFirstPumping = true;
			m_bFirstPumpingEnd = true;
			
			LogUtility.getPumpALogger().info("[Pump A][수신-다쓰노셀프ODT 주유완료]" + " nozzle=" + 
							S4_wm.getConnectNozzleNo() + "("+S4_wm.getCommand()+")" +
							" | ODT_No=" + S4_wm.getNozzleNo() +
							" | liter=" + S4_wm.getLiter() +
							" | bPrice=" + S4_wm.getBasePrice() +
							" | price=" + S4_wm.getPrice() + "\n");
						
			insertDevCtrlOrder_42((byte)'0', "00", "02", "000"); // 타이머 중지
			insertDevCtrlOrder_42((byte)'0', "11", "02", "000"); // 음성 중지
			
			if (m_bInsertedCash==true && Change.toValue(S4_wm.getPrice()) < m_nSetPrice) {
				// 연료구를 닫아주시고 영수증을 받아주세요. 설정한 금액보다 적게 주유시 사무실에 문의해 주세요.
				insertDevCtrlOrder_42((byte)'0', "11", "01", "10064657575706900");
			}
			else {
				if (!(m_rcvEnableFromPOS==true && m_rcvCancelFromPOS==true)) { // 추가(2010/11/18)
					// 연료구를 닫아주시고 영수증을 받아주세요. 감사합니다. 안녕히 가세요.
					insertDevCtrlOrder_42((byte)'0', "11", "01", "100646575756600");
				}
			}
			
			insertDevCtrlOrder_42((byte)'0', "02", "01", "00000000"+"0000000000000000");
		
			// 영수증 출력후 초기상태(유종선택)로 가기위함
			insertDevCtrlOrder_42((byte)'0', "00", "01", "015"); // 타이머 시작
			insertDisplayData_23_new("02", "01", (byte)'1', (byte)'1', true, "Printing...");

			m_nODTState=8;
			
			// TR 전문생성용
			m_realPrice     = S4_wm.getPrice();
			m_realLiter     = S4_wm.getLiter();
			m_realBasePrice = S4_wm.getBasePrice();
			
			skip = true;
		}
		else if (wm.getCommand().equals("QL")) { // 영수증출력
			QL_WorkingMessage QL_wm = (QL_WorkingMessage) wm;

			LogUtility.getPumpALogger().info("[Pump A][수신-다쓰노셀프ODT 영수증출력]" + " nozzle=" + 
					 				QL_wm.getConnectNozzleNo() + "("+QL_wm.getCommand()+")" +
					 				" | ODT_No=" + QL_wm.getNozzleNo() +
					 				" | mode=" + QL_wm.getMode() +
									" | alarmState=" + QL_wm.getAlarmState() +
					 				" | length=" + QL_wm.getContent().length());
			
			LogUtility.getPumpALogger().debug ("m_rcvEnableFromPOS=" + m_rcvEnableFromPOS +
					" | m_rcvCancelFromPOS=" + m_rcvCancelFromPOS);
			
			if (m_rcvEnableFromPOS==true && m_rcvCancelFromPOS==true) { // 수정 및 추가 (2010/11/18)

				insertDisplayData_23_new("01", "01", (byte)'1', (byte)'1', true, "Preset canceled!");
				insertDisplayData_23_new("02", "01", (byte)'1', (byte)'1', false, "Waiting...");

				LogUtility.getPumpALogger().debug ("POS선결제 취소! 영수증 출력안함. nozzle=" + 
						QL_wm.getConnectNozzleNo() + " | ODTNo=" + QL_wm.getNozzleNo());
			}
			else {
				//byte[] byCut = {0x1B, 0x69}; // 영수증 컷팅(full cut)
				byte[] byCut = {0x1B, 0x6D}; // 영수증 컷팅(partial cut)
				byte[] byFeed = {0x1B, 'd', 0x01}; // 영수증 전진(라인)
				String prtData = QL_wm.getContent();
											
				int len = prtData.length();
				if (len > 1610) {
					printReceipt (prtData.substring(0,1610).getBytes(), 0); // 영수증 프린팅 1
					printReceipt (prtData.substring(1610,len).getBytes(), 0); // 영수증 프린팅 2
				} else
					printReceipt (prtData.getBytes(), 0); // 영수증 프린팅
				
				if (QL_wm.getBarCode().length() > 0)
					printReceipt (PumpMessageFormat.getBarcodeFormat(QL_wm.getBarCode()), 0); // Barcode byteStrem
				
				printReceipt (byCut, 0); // Paper cutting
				printReceipt (byFeed, 0); // Paper feeding
			}
			m_rcvEnableFromPOS=false;
			m_rcvCancelFromPOS=false;

			//--- 재승인 실패시 음성안내 기능 추가 (2010/03/16) ---//
			// 관련 수정사항 : 
			//   - QL_WorkingMessage(alarmState 추가)
			//   - Pump module의 ODTUtility_DaSNo(alarmState 값 설정추가)
			if (QL_wm.getAlarmState().equals("1")) { // 재승인 실패 -> 경고안내 출력
				
				insertDevCtrlOrder_42((byte)'0', "00", "02", "000"); // 타이머 중지
				insertDevCtrlOrder_42((byte)'0', "11", "02", "000"); // 음성 중지
				insertDevCtrlOrder_42((byte)'0', "11", "01", "1002324232423242324232423242324232423242324232423242324232423242324232423242324232400");
				insertDevCtrlOrder_42((byte)'0', "11", "01", "1005869586958695869586958695869586958695869586958695869586958695869586958695869586900");
				insertDisplayData_23_new("01", "01", (byte)'3', (byte)'3', true, ">>  Credit fail!  <<");
				insertDisplayData_23_new("02", "01", (byte)'3', (byte)'3', false,">> Help to Office.<<");
				insertDevCtrlOrder_42((byte)'0', "00", "01", "060"); // 타이머 시작
				
				LogUtility.getPumpALogger().debug ("재승인 실패안내 음성출력! nozzle=" + QL_wm.getConnectNozzleNo() + 
												" | ODTNo=" + QL_wm.getNozzleNo());
			}

			//LogUtility.getPumpALogger().debug("##### print out("+len+" bytes) : \n"+ QL_wm.getContent());
			
			if (QL_wm.getMode().equals("1")) { // 마지막 영수증(주유완료)
				
				//----- TR 전문 생성 -----//
				TR_wm = new TR_WorkingMessage();
				TR_wm.setNozzleNo(QL_wm.getNozzleNo());
				TR_wm.setConnectNozzleNo(QL_wm.getConnectNozzleNo());
				TR_wm.setLiter(m_realLiter);
				TR_wm.setBasePrice(m_realBasePrice);
				TR_wm.setPrice(m_realPrice);
				
				insertRecvQueue(TR_wm);
				
				// 주유기 단가 복원
				//if (m_realBasePrice.equals(m_basePrice)) {
//					PB_wm = new PB_WorkingMessage();
//					PB_wm.setPassThrough(false);
//					PB_wm.setNozzleNo(wm.getNozzleNo()); // ODT No
//					PB_wm.setConnectNozzleNo(wm.getConnectNozzleNo()); // Nozzle No
//					PB_wm.setTargetNozzleNo(wm.getConnectNozzleNo());  // Nozzle No
//					PB_wm.setCommandSet("0");
//					PB_wm.setLiter("0000000");
//					PB_wm.setBasePrice("155500");
//					PB_wm.setPrice("00000000");
//					insertRecvQueue(PB_wm);
//					
//					PA_wm = new PA_WorkingMessage();
//					PA_wm.setPassThrough(false);
//					PA_wm.setNozzleNo(wm.getNozzleNo()); // ODT No
//					PA_wm.setConnectNozzleNo(wm.getConnectNozzleNo()); // Nozzle No
//					PA_wm.setTargetNozzleNo(wm.getConnectNozzleNo());  // Nozzle No
//					PA_wm.setNozzleState("0");
//					insertRecvQueue(PA_wm);
				//}
				
				LogUtility.getPumpALogger().info(new StringBuffer("\n다쓰노셀프ODT 판매완료 전문(TR) 전송 : " )
											.append("\n ODT_No=" ).append( TR_wm.getNozzleNo() )
											.append("\n nozzle=" ).append( TR_wm.getConnectNozzleNo() ).append("(TR)" )
											.append("\n liter    =" ).append( TR_wm.getLiter() )
											.append("\n basePrice=" ).append( TR_wm.getBasePrice() )
											.append("\n price    =" ).append( TR_wm.getPrice()).toString());
			}
			
			skip = true;
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
			
			skip = true;
		}
		else if (wm.getCommand().equals("CB")) { // 고객유형 확인응답
			
			CB_wm = (CB_WorkingMessage) wm;
				
			LogUtility.getPumpALogger().info("[Pump A][수신-다쓰노셀프ODT 고객유형 확인응답]" + " nozzle=" + 
									CB_wm.getConnectNozzleNo() + "("+CB_wm.getCommand()+")" +
									" | ODT_No=" + CB_wm.getNozzleNo() +
									" | custType =[" + CB_wm.getCustomerType() + "]" + 
									" | saveBonus=" + CB_wm.getSaveBonus());
				
			m_nCustType = Change.toValue(CB_wm.getCustomerType());
			m_sSaveBonus  = CB_wm.getSaveBonus();
							
			// 고객유형
			if (m_nCustType == 0) { // 거절

				//승인을 받을수 없습니다. 사무실에서 확인하여 주세요.
				insertDevCtrlOrder_42((byte)'0', "00", "02", "000"); // 타이머 중지
				insertDevCtrlOrder_42((byte)'0', "11", "02", "000"); // 음성 중지
				insertDevCtrlOrder_42((byte)'0', "11", "01", "100587575676900");
				insertDevCtrlOrder_42((byte)'0', "02", "01", "00000000"+"0000000000000000");
				insertDisplayData_23_new("02", "07", (byte)'3', (byte)'3', true, "Cancel!");
					
				// 설정시간 후 초기상태(유종선택)로 가기위함
				insertDevCtrlOrder_42((byte)'0', "00", "01", "010"); // 타이머 시작
				m_nODTState=8;
					
				LogUtility.getPumpALogger().info("###### 15 : 고객유형 확인-승인거절. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");

			}
			else if (m_nCustType == 1) { // 일반
				m_nODTState = 4;
					
				//주유금액을 선택해 주세요.
				insertDevCtrlOrder_42((byte)'0', "11", "01", "1001200");
				insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x43, (byte)0x84); //입력요구
					
				LogUtility.getPumpALogger().info("###### 15 : 일반(신용카드) 고객입니다. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");

			}
			else if (m_nCustType == 2) { // 현금거래처
				m_nODTState = 3;
					
				m_custCardNo = m_creditNo;
				m_creditNo = "";

				//현금거래처 고객입니다. 신용카드 또는 지폐를 삽입해 주세요
				if (m_isBNANormal==true) {
					insertDevCtrlOrder_42((byte)'0', "11", "01", "10011757575070800");
					insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x47, (byte)0x82); //입력요구
				}
				else { // BNA 고장시는 신용카드만 처리 (2010/11/05)
					insertDevCtrlOrder_42((byte)'0', "11", "01", "100117575750800");
					insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x43, (byte)0x82); //입력요구				
				}
				
				LogUtility.getPumpALogger().info("###### 15 : 현금거래처 고객입니다. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");

			}
			else if (m_nCustType == 3) { // 외상거래처
				m_nODTState = 4;
					
				m_custCardNo = m_creditNo;
				m_creditNo = "";

				//외상거래처 고객입니다. 주유금액을 선택해 주세요.
				insertDevCtrlOrder_42((byte)'0', "11", "01", "100107575751200");
				insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x43, (byte)0x84); //입력요구
					
				LogUtility.getPumpALogger().info("###### 15 : 외상거래처 고객입니다. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");

			}
			else if (m_nCustType == 4) { // 외상거래처-정량고객
				m_nODTState = 4;
					
				m_custCardNo = m_creditNo;
				m_creditNo = "";

				//주유금액을 선택해 주세요.
				insertDevCtrlOrder_42((byte)'0', "11", "01", "100107575751200");
				insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x43, (byte)0x84); //입력요구
					
				LogUtility.getPumpALogger().info("###### 15 : 외상거래처-정량 고객입니다. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
					
			}
			else { // Fail
										
				m_nODTState = 4;
				m_nCustType = 1; // 일반고객
				m_sSaveBonus = "1";

				//주유금액을 선택해 주세요.
				insertDevCtrlOrder_42((byte)'0', "11", "01", "1001200");
				insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x43, (byte)0x84); //입력요구
					
				LogUtility.getPumpALogger().info("###### 15 : 고객유형 확인 실패 -> 일반(신용카드)으로 진행합니다ㅏ. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
			}
		}
				
		return skip;
	}
	
	@Override
	protected String makeKeyGrpFile() throws Exception {

		char x0D = 0x0D;
		char x0A = 0x0A;
		
		String fileData="";
		
		if (m_numMPPs==6) { // 6복식
			fileData = "01    " +
			  "999999999101102103" +
			  "999999999999999206" +
			  "999999999999999999" +
			  "301302303304305306" +
			  "401402403404405406" +
			  "999999999999999506" +
			  "999999999999999606" + x0D + x0A;
		}
		else if (m_numMPPs==4) { // 4복식
			fileData = "01    " +
			  "999999101101102102" +
			  "999999999999999206" +
			  "999999999999999999" +
			  "301302303304305306" +
			  "401402403404405406" +
			  "999999999999999506" +
			  "999999999999999606" + x0D + x0A;
		}
		
		return fileData;
	}
	
	@Override
	protected void processDeviceErr() throws Exception {
		
		if (RxBuf[7]=='1' && RxBuf[8]=='3') { // BNA
			if (RxBuf[6]=='1') // 에러 발생
				m_isBNANormal = false;
			else if (RxBuf[6]=='0') // 에러 회복
				m_isBNANormal = true;
		}
		
		insertRecvQueue(generateWorkingMessage(RxBuf, null));	
	}
	
	//------ 입력오더 응답(80) 처리 ------//
	@Override
	protected void processOPT () throws SerialConnectException, Exception {

		byte[]	byt = new byte[2];
		String	function;
		
		//--- m_nODTState 값 ---//
		// 1 : 유종선택 대기
		// 3 : 지폐/신용카드/거래처카드 삽입 대기
		// 4 : 주유금액선택 대기
		// 5 : 보너스카드/현금영수증카드 삽입 대기
		// 6 : 승인요청중
		// 7 : 승인완료(주유중)
		// 8 : 주유완료(영수증 출력중)
		
		if (m_setEnvDataOK == false)
			return;
				
		switch (RxBuf[10]) { //데이터 타입	
		case '0': //입력중지
			break;
			
		case '1': //키 입력
			byt[0]=RxBuf[11];
			byt[1]=RxBuf[12];
			function=new String (byt);

			LogUtility.getPumpALogger().info("\n<<<<Input function(1) keyNo="+function+" m_nODTState="+m_nODTState+"\n");

			
			if ((function.equals("00")==true || function.equals("01")==true || function.equals("02")==true || 
					function.equals("0D")==true) && 
					(m_nODTState == 0 || m_nODTState == 1 || m_nODTState == 2)) {

				if (function.equals("0D")==true) { // 취소처리

					m_nODTState = 1;
					cancelInsertedCash();
					initVariables();

					// 원하시는 유종을 선택해 주세요
					insertDevCtrlOrder_42((byte)'0', "11", "01", "100020202020400");
					insertDisplayData_23_new("01", "01", (byte)'3', (byte)'3', true, "Welcome! GS Caltex.");
					insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x43, (byte)0x81); //입력요구
					
					LogUtility.getPumpALogger().info("###### 1 : 취소하였습니다. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
					
					makeStatusInfo (231); // 유종선택 대기(대기모드)
				}
				else { //---- 유종선택 ----//
					
					m_nODTState = 3;				
					initVariables();

					// 위치변경 from initVariables() - 10/11/18 (선결제 취소 영수증 처리)
					m_rcvEnableFromPOS = false;
					m_rcvCancelFromPOS = false;
					
					if (function.equals("00"))
						m_targetNozzle = m_nozNoVec.get(0);
					else if (function.equals("01"))
						m_targetNozzle = m_nozNoVec.get(1);
					else if (function.equals("02"))
						m_targetNozzle = m_nozNoVec.get(2);

					m_oilKind = getOilKind(m_targetNozzle);
					m_basePrice = m_basePriceTbl.get(m_targetNozzle);

					// 유종 디스플레이
					String oilName = m_oilNameTbl.get(m_targetNozzle);
					Formatter form = new Formatter();
					form.format("%-14s", oilName);
					String sndStr = form.toString();
					insertDisplayData_23 ("01", "01", sndStr);
					
					if (m_oilKind==DIESEL) { // 경유
						insertDevCtrlOrder_42((byte)'0', "11", "01", "1000600");
					}
					else if (m_oilKind==ADIESEL) { // 고급경유 프라임경유
						insertDevCtrlOrder_42((byte)'0', "11", "01", "1000500");
					}
					else if (m_oilKind==GASOLINE) { // 무연 휘발유
						insertDevCtrlOrder_42((byte)'0', "11", "01", "1000400");
					}
					else if (m_oilKind==PGASOLINE) { // 고급휘발유 킥스프라임
						insertDevCtrlOrder_42((byte)'0', "11", "01", "1000300");
					}
					
					if (m_isBNANormal==true) {
						insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x47, (byte)0x82); //입력요구
						insertDevCtrlOrder_42((byte)'0', "11", "01", "000757507080900");// 지폐/카드를 삽입해 주세요
					}
					else { // 추가 (2010/11/05)
						insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x43, (byte)0x82); //입력요구
						insertDevCtrlOrder_42((byte)'0', "11", "01", "0007575080900");// 카드를 삽입해 주세요	
					}
					
					// 사람검지 및 타임아웃시 초기화면 진행제어
					insertDevCtrlOrder_42((byte)'0', "00", "02", "000"); // 타이머 중지
					insertDevCtrlOrder_42((byte)'0', "00", "01", "120"); // 타이머 시작
								
					LogUtility.getPumpALogger().info("###### 2 : 유종선택하였습니다. m_nODTState=" + m_nODTState + 
							" Oil name="+ oilName + " (ODT="+nozNo+")");

					makeStatusInfo (233); // 결제입력 대기
				}
			}
			else if (m_nODTState == 3 && (function.equals("0D") || function.equals("0E"))) {
				
				if (function.equals("0D")) { // 취소
					
					m_nODTState = 1;
					cancelInsertedCash();
					initVariables();
					//makeCancelInfo();

					// 승인을 받으실수 없습니다. 원하시는 유종을 선택해 주세요
					insertDevCtrlOrder_42((byte)'0', "11", "01", "100580200");
					insertDisplayData_23_new("01", "01", (byte)'3', (byte)'3', true, "Welcome! GS Caltex.");
					insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x43, (byte)0x81); //입력요구
					
					LogUtility.getPumpALogger().info("###### 5 : 취소하였습니다. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");

					makeStatusInfo (231); // 유종선택 대기(대기모드)
				}
				else if (function.equals("0E")) { // 다른키 입력후 완료버튼 누름

					String cashCount = m_cashCount.equals("") ? "0" : m_cashCount;

					if (Change.toValue(cashCount) > 0) {
						
						cashInsertEnd_proc (cashCount);
						
						if (m_nCustType==1 || (m_nCustType!=1 && m_sSaveBonus.equals("1"))) { // 보너스 적립함
							
							m_nODTState = 5;
							// 표시된 금액으로 주유됩니다. 보너스카드를 넣었다 빼주세요.
							insertDevCtrlOrder_42((byte)'0', "11", "01", "100497575515300");
							insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x43, (byte)0xA0); //입력요구
													
							makeStatusInfo (234); // 보너스카드 입력대기
						}
						else { // 보너스 적립안함
							// 표시된 금액으로 주유됩니다. 
							insertDevCtrlOrder_42((byte)'0', "11", "01", "10049757500");
							
							skipSaveBonus_proc();
						}
						
						LogUtility.getPumpALogger().info("###### 14 : 지폐투입이 " + m_cashCount + "원 입니다. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
					}
					else {
						m_nODTState = 3;
						
						//주유금액을 선택해 주세요.
						if (m_isBNANormal==true) {
							insertDevCtrlOrder_42((byte)'0', "11", "01", "000757507080900");// 지폐/카드를 삽입해 주세요
							insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x47, (byte)0x82); //입력요구
						} 
						else { // BNA 고장시 (2010/11/05)
							insertDevCtrlOrder_42((byte)'0', "11", "01", "0007575080900");// 지폐/카드를 삽입해 주세요
							insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x43, (byte)0x82); //입력요구							
						}
						
						makeStatusInfo (233); // 결제입력 대기
						
						LogUtility.getPumpALogger().info("###### 14 : 지폐투입이 0원 이므로 다시 입력하세요. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
					}
				}
			}
			else if (m_nODTState == 4 && 
					(function.equals("03") || function.equals("04") || function.equals("05") || 
					 function.equals("06") || function.equals("07") || function.equals("13") || 
					 function.equals("14") || function.equals("15") || function.equals("16") || 
					 function.equals("17") || function.equals("0A") || function.equals("0B") || 
					 function.equals("0D")))
			{
				if (function.equals("0D")) { // 취소
					
					m_nODTState = 1;
					cancelInsertedCash();
					initVariables();
					makeCancelInfo();

					// 승인을 받으실수 없습니다. 원하시는 유종을 선택해 주세요
					insertDevCtrlOrder_42((byte)'0', "11", "01", "100580200");
					insertDisplayData_23_new("01", "01", (byte)'3', (byte)'3', true, "Welcome! GS Caltex.");
					insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x43, (byte)0x81); //입력요구
					
					LogUtility.getPumpALogger().info("###### 3 : 취소하였습니다. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");

					makeStatusInfo (231); // 유종선택 대기(대기모드)
				}
				else { //---- 금액선택 ----//
					
					//m_nODTState = 5;
					String szVoice="";
					String szDisplay="";
					m_liter="0000000";
					boolean selFullPumping=false;

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
						case 'B': // 가득주유 버턴 입력
							selFullPumping = true;
							if (m_useFullPumping == true) {
								szVoice=m_sMentPriceArr[1][5]; szDisplay=m_sDispPriceArr[1][5]; 
								m_price=m_sPriceButtArr[1][5]; m_preset = PRESET_PRICE;
							}
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
					
					if (selFullPumping==true && m_useFullPumping==false) { // 가득주유 미사용
						m_nODTState = 4;
						
						// 승인을 받으실 수 없습니다. 주유금액을 선택해 주세요.
						insertDevCtrlOrder_42((byte)'0', "11", "01", "10044581200");
						insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x43, (byte)0x84); //입력요구
						
						makeStatusInfo (232); // 금액입력 대기
						LogUtility.getPumpALogger().info("###### 4 : 가득주유 미사용 상태입니다. m_useFullPumping=" + m_useFullPumping + " (ODT="+nozNo+")");
					}
					else {
						
						if (m_price.equals("00149900")) {
							// 가득주유를 선택하셨습니다. 
							insertDevCtrlOrder_42((byte)'0', "11", "01", "10044757500");
						}
						else {
							// OO원을 선택하셨습니다. 
							insertDevCtrlOrder_42((byte)'0', "11", "01", "100" + szVoice + "46757500");
						}
	
						insertDisplayData_23_new("02", "01", (byte)'3', (byte)'3', false, szDisplay);
						LogUtility.getPumpALogger().info("###### 4 : 금액선택하였습니다. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
	
						if (m_nCustType==1 || (m_nCustType!=1 && m_sSaveBonus.equals("1"))) { // 보너스 적립함
							
							m_nODTState = 5;
							// 보너스카드를 넣었다 빼주세요.
							insertDevCtrlOrder_42((byte)'0', "11", "01", "000515300");
							insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x43, (byte)0xA0); //입력요구
							
							makeStatusInfo (234); // 보너스 입력대기
						}
						else  // 보너스 적립 안함
							skipSaveBonus_proc();
					}
				}
			}
			else if (m_nODTState == 5 && 
					(function.equals("0C") || function.equals("0D"))) {
				
				if (function.equals("0D")) { // 취소
					
					m_nODTState = 1;
					cancelInsertedCash();
					initVariables();
					makeCancelInfo();

					// 승인을 받으실수 없습니다. 원하시는 유종을 선택해 주세요
					insertDevCtrlOrder_42((byte)'0', "11", "01", "100580200");
					insertDisplayData_23_new("01", "01", (byte)'3', (byte)'3', true, "Welcome! GS Caltex.");
					insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x43, (byte)0x81); //입력요구
					
					LogUtility.getPumpALogger().info("###### 6 : 취소하였습니다. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");

					makeStatusInfo (231); // 유종선택 대기(대기모드)
				}
				else { // 보너스카드 없음
					
					if (m_nCashReceiptState==1) {
						
						m_nODTState = 5;
						m_nCashReceiptState=2;
						
						//현금영수증카드를 넣었다 빼주세요.
						insertDevCtrlOrder_42((byte)'0', "11", "01", "100525300");
						insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x43, (byte)0xA0); //입력요구
						
						LogUtility.getPumpALogger().info("###### 8 : 현금영수증카드 입력하세요. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");

						makeStatusInfo (234); // 보너스카드입력 대기
					}
					else {
						
						m_nODTState = 6;
	
						// 주유승인중입니다. 잠시만 기다리세요.
						insertDevCtrlOrder_42((byte)'0', "11", "01", "10055757556757500");
						insertDevCtrlOrder_42((byte)'0', "11", "01", "0005575755600");
						insertDisplayData_23_new("02", "10", (byte)'3', (byte)'3', false, "Waiting...");
						
						//----- 카드승인요청 -----//
						boolean bApprove = requestCardApprove (); 
						
						LogUtility.getPumpALogger().info("###### 7 : 승인 요청하였습니다. : state="+m_nODTState+
								" approve="+bApprove+" m_nODTState="+m_nODTState + " (ODT="+nozNo+")");

						makeStatusInfo (236); // 승인요청 중
					}
				}
			}
			else {
				inputException_proc();
							
				LogUtility.getPumpALogger().info("###### : 예외처리 루틴(1)-----m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
			}
			
			break;
				
		case '2': // 카드 입력
			
			if (m_nODTState==3 && m_bInsertedCash==false) { // 결제대기

				byte[] byLen = {RxBuf[93], RxBuf[94]};
				String lenStr = new String(byLen);
				int	len = Change.toValue (lenStr);

				flushBuffer(m_byCreditNo);
				System.arraycopy(RxBuf, 95, m_byCreditNo, 0, len);

				//LogUtility.getPumpALogger().debug("******카드입력 확인 : m_byCreditNo="+new String(m_byCreditNo)+" len="+m_byCreditNo.length);

				int k;
				for (k=0; k<m_byCreditNo.length; k++)
					if (m_byCreditNo[k]==0x00)
						break;
				
				String szCardNo = new String(m_byCreditNo);
				m_creditNo = szCardNo.substring(0,k);
				
				if (m_bCustCheck == false) {
	
					//----- 고객유형 확인요청 -----//
					boolean bApprove = requestCheckCustmerType (szCardNo);
					m_bCustCheck = true;
					
					LogUtility.getPumpALogger().info("###### 7 : 고객유형 확인요청 하였습니다. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
				}
				else {
					m_nODTState = 4;
					
					//주유금액을 선택해 주세요.
					insertDevCtrlOrder_42((byte)'0', "11", "01", "1001200");
					insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x43, (byte)0x84); //입력요구
					
					LogUtility.getPumpALogger().info("###### 8 : 신용카드 입력하였습니다. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");

					makeStatusInfo (232); // 금액선택 대기
				}
			}
			else if (m_nODTState == 5) {
				
				if (RxBuf[91]=='0' && RxBuf[92]=='0') { // 카드리딩 성공
					
					if (m_nCashReceiptState==1) { // 지폐투입

						m_nODTState = 5;
						m_nCashReceiptState=2;

						m_bBonusMode = true;
	
						byte[] byLen = {RxBuf[93], RxBuf[94]};
						String lenStr = new String(byLen);
						int	len = Change.toValue (lenStr);
						System.arraycopy(RxBuf, 95, m_byBonusNo, 0, len);
						m_bonusNo = new String(m_byBonusNo);
						
						//현금영수증카드를 넣었다 빼주세요.
						insertDevCtrlOrder_42((byte)'0', "11", "01", "100525300");
						insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x43, (byte)0xA0); //입력요구
						
						LogUtility.getPumpALogger().info("###### 8 : 현금영수증카드 입력하세요. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");

						makeStatusInfo (234); // 보너스카드(현금영수증)입력 대기
					}
					else if (m_nCashReceiptState==0 || // 지폐투입 아님
							 m_nCashReceiptState==2) { // 지폐투입후 현금영수증 카드 삽입완료
						
						m_nODTState = 6;
						m_bBonusMode = true;
	
						byte[] byLen = {RxBuf[93], RxBuf[94]};
						String lenStr = new String(byLen);
						int	len = Change.toValue (lenStr);
						System.arraycopy(RxBuf, 95, m_byBonusNo, 0, len);
						
						if (m_nCashReceiptState==2) {
							m_cashReceiptNo = new String(m_byBonusNo);
							m_nCashReceiptState = 0;
						}
						else {
							m_bonusNo = new String(m_byBonusNo);
						}
	
						// 주유승인중입니다. 잠시만 기다려 주십시요.
						insertDevCtrlOrder_42((byte)'0', "11", "01", "10055757556757500");
						insertDevCtrlOrder_42((byte)'0', "11", "01", "0005575755600");
						insertDisplayData_23_new("02", "10", (byte)'3', (byte)'3', false, "Waiting...");
	
						LogUtility.getPumpALogger().info("###### 10 : 보너스카드 입력하였습니다. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
						//LogUtility.getPumpALogger().info("보너스카드 번호="+m_bonusNo+" len="+m_bonusNo.length());
	
						//----- 카드승인요청 -----//
						boolean bApprove = requestCardApprove ();
						
						LogUtility.getPumpALogger().info("###### 11 : 승인 요청하였습니다. : state="+m_nODTState+ " approve="+bApprove+
								" m_nODTState="+m_nODTState + " (ODT="+nozNo+")");

						makeStatusInfo (236); // 승인요청 중
					}
				}
				else {

					m_nODTState = 1;
					cancelInsertedCash();
					initVariables();

					// 승인을 받으실수 없습니다. 원하시는 유종을 선택해 주세요
					insertDevCtrlOrder_42((byte)'0', "11", "01", "100580200");
					insertDisplayData_23_new("01", "01", (byte)'3', (byte)'3', true, "Welcome! GS Caltex.");
					insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x43, (byte)0x81); //입력요구
					
					LogUtility.getPumpALogger().info("###### 12 : 보너스카드 입력하였습니다->카드이상으로 요청취소. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");

					makeStatusInfo (231); // 유종선택 대기(대기모드)
				}
			}
			else {
				inputException_proc();
								
				LogUtility.getPumpALogger().info("###### : 예외처리 루틴(2)-----m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
			}
			
			break;
			
		case '3': // BNA 입력 데이터(키 입력하여 지폐투입 완료 "80" 수신)

			byt[0]=RxBuf[45];
			byt[1]=RxBuf[46];
			function=new String (byt);
			
			LogUtility.getPumpALogger().info("\n<<<<Input function(2) keyNo="+function+" m_nODTState="+m_nODTState+"\n");
			
			String str = new String(RxBuf);
			String sTotCash = str.substring(37, 45);
			
			m_price = sTotCash;
			m_cashCount = sTotCash;
			
			if (m_nODTState == 3 && function.equals("0E")) { // 완료버튼 누름
				
				if (Change.toValue(m_cashCount) > 0) {
					
					cashInsertEnd_proc (sTotCash);
					
					if (m_nCustType==1 || (m_nCustType!=1 && m_sSaveBonus.equals("1"))) { // 보너스 적립함
						
						m_nODTState = 5;
						// 표시된 금액으로 주유됩니다. 보너스카드를 넣었다 빼주세요.
						insertDevCtrlOrder_42((byte)'0', "11", "01", "100497575515300");
						insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x43, (byte)0xA0); //입력요구
						
						makeStatusInfo (234); // 보너스카드 입력대기
					}
					else { // 보너스 적립안함
						// 표시된 금액으로 주유됩니다. 
						insertDevCtrlOrder_42((byte)'0', "11", "01", "10049757500");
						
						skipSaveBonus_proc();
					}
					
					LogUtility.getPumpALogger().info("###### 13 : 지폐투입이 " + m_cashCount + "원 입니다. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
				}
				else {
					m_nODTState = 3;
					
					if (m_isBNANormal==true) {
						insertDevCtrlOrder_42((byte)'0', "11", "01", "000757507080900");// 지폐/카드를 삽입해 주세요
						insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x47, (byte)0x82); //입력요구
					}
					else { // 추가 (2010/11/05)
						insertDevCtrlOrder_42((byte)'0', "11", "01", "0007575080900");// 카드를 삽입해 주세요
						insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x43, (byte)0x82); //입력요구
					}
					
					makeStatusInfo (233); // 결제입력 대기
					
					LogUtility.getPumpALogger().info("###### 13 : 지폐투입이 0원 이므로 다시 입력하세요. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
				}
			}
			else { // 완료버턴 이외의 키 누름
				insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x41, (byte)0x82); //입력요구
				insertDevCtrlOrder_42((byte)'0', "11", "01", "1004800");
				
				LogUtility.getPumpALogger().info("###### 13 : 완료버튼을 누르세요. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
			}

			break;
		}
	}
	
	@Override
	protected void processRecvSTX () throws SerialConnectException, Exception {
	
		//############# State check ##############//
		if (RxBuf[3]=='6' && RxBuf[4]=='0') { // 파일/데이터 수신응답

			recvFileStatus();
		}
		else if (RxBuf[3]=='6' && RxBuf[4]=='1') { // OPT 상태 통지

			makeStatusInfo (650); // 상태정보전송(정상)
			
			switch (RxBuf[6]) // 동작모드 ('1'=Initial mode, '2'=POS mode)
			{
			case '1': // Initial mode (환경설정)

				insertInitialFileDemand();
				insertOPTFile();
				
				LogUtility.getPumpALogger().info("\n######### Completed Tatsuno Self ODT Initializtion : ODT=" +
												nozStr + "(MPP" + m_numMPPs + "-BNA) #########\n");

				break;
			case '2': // POS mode (최초 실행)

				if (m_nODTState >= 3 && m_nODTState < 7)
					cancelInsertedCash(); 

				m_nODTState = 1;
				initVariables();
				insertInitCmd(); // 반복
				m_isBNANormal = true; // 추가 2010/12/16
				
				insertOPTStatus ((byte)'0');
				
				insertDevCtrlOrder_42((byte)'0', "11", "01", "100010200"); // 어서오세요. 원하시는 유종을 선택해 주세요			
				insertDisplayData_23_new("01", "01", (byte)'3', (byte)'3', true, "Welcome! GS Caltex.");
				insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x43, (byte)0x81); // 램프(유종선택)
				
				LogUtility.getPumpALogger().info("###### 0 : 초기실행 하였습니다. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
				
				break;
			}
		}
		else if (RxBuf[3]=='6' && RxBuf[4]=='2') { // BNA 투입

			String str = new String(RxBuf);
			String sCash 	= str.substring(24, 32);
			String sTotCash = str.substring(32, 40);
			int	   nCash	= Change.toValue(sCash);
			
			if (m_bInsertedCash==false) { // 최초 지폐투입시
				//지폐를 다 넣으셨으면 완료 버튼을 누르세요.
				insertDevCtrlOrder_42((byte)'0', "11", "01", "1004875757575757575754800");
			}
			
			m_bInsertedCash = true;
			m_nCashReceiptState = 1;
			
			BI_wm = new BI_WorkingMessage();
			BI_wm.setNozzleNo(nozStr); // ODTNo
			BI_wm.setConnectNozzleNo(m_targetNozzle); // nozNo
			BI_wm.setCash(sCash);
			BI_wm.setCashCount(sTotCash);

			insertRecvQueue(BI_wm);
			
			insertDisplayData_23_new("01", "14", (byte)'3', (byte)'3', false, "Cash>>");
			//insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x43, (byte)0x82); //검토 for 금천(2010.08.16)
			
			LogUtility.getPumpALogger().info("###### 0 : 지폐 투입되었습니다. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
			LogUtility.getPumpALogger().info("           투입금액=" + sCash + "원, 합계금액=" + sTotCash + "원\n");

		}
		else if (RxBuf[3]=='8' && RxBuf[4]=='0') { // 입력오더 응답

			processOPT (); // 입력오더 데이터 처리
		}
		else if (RxBuf[3]=='8' && RxBuf[4]=='1') { // OPT 에러 통지

			processDeviceErr(); 
		}
		else if (RxBuf[3]=='8' && RxBuf[4]=='2') { // 디바이스 제어응답

			if (RxBuf[6]=='1' && RxBuf[7]=='5') { // 사람검지센서

				if (RxBuf[8]=='0' && RxBuf[9]=='1') { // 검지상태
					
					if (m_nODTState == 1) {  // 수정(09/04/09), 승인요청 이전

						insertInitCmd();
					   
						insertDevCtrlOrder_42((byte)'0', "00", "02", "000"); // 타이머 중지
						insertDevCtrlOrder_42((byte)'0', "11", "01", "100010200");
						insertDisplayData_23_new("01", "01", (byte)'3', (byte)'3', true, "Welcome! GS Caltex.");
						insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x43, (byte)0x81); //입력요구

						makeStatusInfo (231); // 유종선택 대기(대기모드)
				   	}
				   	LogUtility.getPumpALogger().info("###### 0 : 사람 검지하였습니다. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
				}
				else {
					LogUtility.getPumpALogger().info("###### 0 : 사람검지센서 작동하였습니다. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
				}
			}
			else if (RxBuf[6]=='0' && RxBuf[7]=='0') // 타이머
			{
//				LogUtility.getPumpALogger().info("타이머 작동 >>>>>>>> m_nODTState="+m_nODTState+" m_bInsertedCash="+m_bInsertedCash);
//				Log.datas(RxBuf, 40, 20);
				
				if (RxBuf[10]=='0' && RxBuf[11]=='0') { // 카운트 종료
					
					if (m_nODTState == 8) { // 8 = 주유완료(프린터 출력중)
						m_nODTState = 1; 
						initVariables();
					} 
					
					if (m_nODTState <= 5 && m_bInsertedCash==false) { // 5 = 보너스카드/현금영수증카드 삽입 대기
																	
						m_nODTState = 1; // 설정한 타임동안 입력이 없을경우 처음(유종선택)으로 복귀
						initVariables();

						insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x43, (byte)0x81);
						insertDevCtrlOrder_42((byte)'0', "11", "01", "100010200");
						insertDisplayData_23_new("01", "01", (byte)'3', (byte)'3', true, "Welcome! GS Caltex.");
	
						makeStatusInfo (231); // 유종선택 대기(대기모드)
						
						byte[] byt1 = new byte[2];										
						byt1[0] = RxBuf[10]; 
						byt1[1] = RxBuf[11];
						LogUtility.getPumpALogger().info("###### 0 : 디바이스 타이머 응답 : state=" + new String(byt1) + 
								" m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
					}
				}
			}
			else if (RxBuf[6]=='0' && RxBuf[7]=='2') // 램프
			{
				byte[] byt1 = new byte[2];
				byt1[0] = RxBuf[10]; byt1[1] = RxBuf[11];
				LogUtility.getPumpALogger().info("###### 0 : 디바이스 램프 응답 : state=" + new String(byt1) +
						" m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
			}
		}
	}
	
	@Override
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
		
		if (m_price.equals("00149900"))
			HE_wm.setIsFullPumping("1"); // 가득주유
		else
			HE_wm.setIsFullPumping("0");
		                  
		LogUtility.getPumpALogger().info(new StringBuffer("\n다쓰노셀프ODT 승인요청 전문(HE)" )
									.append("\n ODT_No=" ).append( HE_wm.getNozzleNo() )
									.append("\n nozzle=" ).append( HE_wm.getConnectNozzleNo() ).append( "(HE)" )
									.append("\n type     =" ).append( HE_wm.getCardType() )
									.append("\n liter    =" ).append( HE_wm.getLiter() )
									.append("\n basePrice=" ).append( HE_wm.getBasePrice() )
									.append("\n price    =" ).append( HE_wm.getPrice() )
									.append("\n creditNo =" ).append( Base64Util.encode(getCardTrack1Data(HE_wm.getCardNumber())) )
									.append("\n bonusNo  =" ).append( Base64Util.encode(getCardTrack1Data(HE_wm.getBonusCard())) )
									.append("\n custNo   =" ).append( Base64Util.encode(getCardTrack1Data(HE_wm.getCustCardNo())) )
									.append("\n cashCount=" ).append( HE_wm.getCashCount() )
									.append("\n cashRctNo=" ).append( Base64Util.encode(getCardTrack1Data(HE_wm.getCashReceiptNo())) )
									.append("\n isFullPmp=" ).append( HE_wm.getIsFullPumping() )
									.append("\n custType =" ).append( HE_wm.getCustomerType() ).append("\n").toString());	
		
	
		insertRecvQueue(HE_wm);
		
		insertDevCtrlOrder_42((byte)'0', "00", "02", "000"); // 타이머 중지 
		insertDevCtrlOrder_42((byte)'1', "02", "01", "00000000"+"0000000000000000");
		insertInitCmd();
		
		m_bFromODT=true;
		
		return true;
	}
	
	protected boolean requestCheckCustmerType (String cardNo) throws Exception {
		
		CA_WorkingMessage CA_wm = new CA_WorkingMessage();
		CA_wm.setNozzleNo(nozStr); // ODTNo
		CA_wm.setConnectNozzleNo(m_targetNozzle);
		CA_wm.setCardNo(cardNo);
		                  
		LogUtility.getPumpALogger().info(new StringBuffer("\n다쓰노셀프ODT 고객유형 확인요청(CA)" )
									 .append("\n ODT_No=" ).append( CA_wm.getNozzleNo() ) 
									 .append("\n nozzle=" ).append( CA_wm.getConnectNozzleNo() ).append( "(CA)" )
									 .append("\n cardNo=" ).append( Base64Util.encode(getCardTrack1Data(cardNo)) ).append( "\n").toString());
	
		insertRecvQueue(CA_wm);
		
		//insertDevCtrlOrder_42((byte)'0', "00", "02", "000"); // 타이머 중지 
		insertDevCtrlOrder_42((byte)'1', "02", "01", "00000000"+"0000000000000000");
		insertInitCmd();
		
		return true;
	}
	
	protected void skipSaveBonus_proc () throws  Exception {
		
		if (m_nCashReceiptState==1) { // 지폐투입
			
			m_nODTState = 5;
			m_nCashReceiptState=2;
	
			//현금영수증카드를 넣었다 빼주세요.
			insertDevCtrlOrder_42((byte)'0', "11", "01", "000525300");
			insertInputOrder_40((byte)'0', "01", (byte)'1', (byte)0x43, (byte)0xA0); //입력요구
			
			LogUtility.getPumpALogger().info("###### 8 : 현금영수증카드 입력하세요. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
	
			makeStatusInfo (234); // 보너스카드입력 대기
		}
		else {
	
			m_nODTState = 6;
			
			// 주유승인중입니다. 잠시만 기다려 주십시요.
			insertDevCtrlOrder_42((byte)'0', "11", "01", "10055757556757500");
			insertDevCtrlOrder_42((byte)'0', "11", "01", "0005575755600");
			insertDisplayData_23_new("02", "10", (byte)'3', (byte)'3', false, "Waiting...");
	
			LogUtility.getPumpALogger().info("###### 10 : 금액선택 하였습니다. m_nODTState=" + m_nODTState + " (ODT="+nozNo+")");
			//LogUtility.getPumpALogger().info("보너스카드 번호="+m_bonusNo+" len="+m_bonusNo.length());
	
			//----- 카드승인요청 -----//
			boolean bApprove = requestCardApprove();
			
			LogUtility.getPumpALogger().info("###### 11 : 보너스 누적없이 승인요청하였습니다. : state="+m_nODTState+ " approve="+bApprove+
					" m_nODTState="+m_nODTState + " (ODT="+nozNo+")");
	
			makeStatusInfo (236); // 승인요청 중
		}
	}
}
