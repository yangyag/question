package com.gsc.kixxhub.module.pumpm.pump.util;

import org.apache.ibatis.session.SqlSession;

import com.gsc.kixxhub.common.data.ICode;
import com.gsc.kixxhub.common.data.IConstant;
import com.gsc.kixxhub.common.data.ICustConstant;
import com.gsc.kixxhub.common.data.IUPOSConstant;
import com.gsc.kixxhub.common.data.posdata.POS_DT;
import com.gsc.kixxhub.common.data.posdata.POS_DU;
import com.gsc.kixxhub.common.data.posdata.POS_DV;
import com.gsc.kixxhub.common.data.posdata.POS_DW;
import com.gsc.kixxhub.common.data.pump.CB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.GB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HD_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PG_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S9_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.data.upos.CreateUPOSMessage;
import com.gsc.kixxhub.common.data.upos.UPOSMessage;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_AffilateInfo;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_CampInfo;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_ItemInfo;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_ItemInfo_Item;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_TradeCondition;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_TradeCondition_Item;
import com.gsc.kixxhub.common.dbadapter.common.SqlSessionFactoryManager;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_CHARGING_PERSONHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_CODEMASTERHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_PRODUCTHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_VIOLATIONHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_CHARGING_PERSONData;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_PRODUCTData;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_VIOLATIONData;
import com.gsc.kixxhub.common.dbadapter.pump.handler.T_NZ_NOZZLEHandler;
import com.gsc.kixxhub.common.dbadapter.pump.vo.T_NZ_NOZZLEData;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.module.pumpm.pump.data.cust.CustReturnValue;
import com.gsc.kixxhub.module.pumpm.pump.data.cust.LimitAmount;
import com.gsc.kixxhub.module.pumpm.pump.manager.PumpMODTSaleManager;
import com.gsc.kixxhub.module.pumpm.pump.manager.PumpMTransactionManager;

public class S9Util {

	/**
	 * �������� ���θ� �Ǵ��Ͽ� PO������ POS�� ����
	 * @param dwMsg
	 * @return
	 */
	public static boolean checkControlStatus(POS_DW dwMsg){
		boolean chkControl = false;
		if (	!"00".equals(dwMsg.getControl_status()) || 
				"1".equals(dwMsg.getControl_yn()))
			chkControl = true;
		
		return chkControl ;
	}
	
	/**
	 * ����ŷ�ó������ �����㰡�� �ŷ�ó�� ���������� ���ش�.
	 * @param dwMsg
	 * @return
	 */
	public static boolean checkControlStatus2(POS_DW dwMsg){
		boolean chkControl = false;
		if ("1".equals(dwMsg.getControl_yn()))
			chkControl = true;
		
		return chkControl ;
	}

	/**
	 * POS���� ���� DW������ �̿��Ͽ� �پ��� �������� ��û�� �ŷ�ó ���� ��û�� ���� ��������(CB)�� �����.
	 * @
	 */
	public static CB_WorkingMessage convertCBWorkMsgFromPOSDWMsg (POS_DW dwMsg) {
		
		CB_WorkingMessage cbWorkingMsg = new CB_WorkingMessage();
		
		cbWorkingMsg.setNozzleNo(PumpMUtil.getODTNumberFromNozzleNo(dwMsg.getDeviceID()));
		cbWorkingMsg.setConnectNozzleNo(dwMsg.getDeviceID());
		cbWorkingMsg.setCustomerType("");
		cbWorkingMsg.setMessage("");
		
		cbWorkingMsg = getCustomer_type(dwMsg, cbWorkingMsg);
		
		cbWorkingMsg.print();
		
		return cbWorkingMsg;
	}
	
	/**
	 * pi2, cwi, 2016-01-14 
	 * �� ���� ��ȸ ���� ������ GSC������ ������ ���ؼ� uPOS �������� ���� �� ���������� GB �������� �����Ѵ�.
	 * 
	 * @param dwMsg		: POS Protocol DW ����
	 * @return
	 */
	public static GB_WorkingMessage convertGBWorkMsgFromPOSDWMsg(POS_DW dwMsg) {
		
		PG_WorkingMessage pgWorkMsg = S9Util.convertPGWorkMsgFromPOSDWMsg(dwMsg) ;
		//�����Ⱑ ����-������ ���� ���ؼ� ���������� ���� ���� ����-�������� ���� �Ǳ� ������
		//GSC������ ��� ����-������ ���� �����Ѵٸ� �̸� �ٽ� ���� ������ ������ ���� ���� �� 
		//GSC���� �����п� ����-�������� ���ٸ� �Ʒ��� �ڵ� ����
		
		String custcard_no = null; 	// ������(01: ������, 02: ������, 03: ����, 04: 1ȸ����)
		String cardadj_ind = dwMsg.getCardadj_ind() ;
		
		if (cardadj_ind.equals("00")) {
			custcard_no = "01" ;
		} else if (cardadj_ind.equals("01")) {
			custcard_no = "05" ;				
		} else if (cardadj_ind.equals("02")) {
			custcard_no = "02" ;
		} else if (cardadj_ind.equals("03")) {
			custcard_no = "03" ;
		} else if (cardadj_ind.equals("04")) {
			custcard_no = "04" ;
		} else if (cardadj_ind.equals("05")) {
			custcard_no = "06" ;
		}
		pgWorkMsg.setCusType(custcard_no);
		
		//����ŷ�ó üũ
		//����ŷ�ó�� ��� ������ �������� �ʴ´�.
		if (checkControlStatus2(dwMsg))
			pgWorkMsg.setTransStatus("2");
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		SqlSession session = null;
		String nozzle_no = pgWorkMsg.getConnectNozzleNo();;
		String goodsCode = dwMsg.getGoods_code();
		String bonGoodsCode = "";
		String unitPrice_before_discount = "0";
		String unitPrice_after_discount = "0";
		String unitDiscount_ind = "00"; 	
		
		// With Out Pos�� ��� �ŷ����� ��ϵ� ��ǰ�ڵ�� ���� ġ �� �� goodsCode�� 0000���� ������ NullPointerException �� �߻���
		// ��ǰ�ڵ尡 ����ġ �� ��� bonGoodsCode ���� ���� �� ������ �ܰ��� ��ȸ���� �ʵ��� ���� �ϸ�,
		// �Ʒ� Filler1 ���� �� �ŷ�ó ������ ������ 02�� �����ϵ��� �Ѵ�.
		if(!goodsCode.equals("0000") && goodsCode != null){
			try {
				session = SqlSessionFactoryManager.openSqlSession();
				
				T_NZ_NOZZLEData nzNozzleData = T_NZ_NOZZLEHandler.getHandler().getT_NZ_NOZZLEDataByNozzleNo(session ,nozzle_no)[0] ;
				T_KH_PRODUCTData productData = T_KH_PRODUCTHandler.getHandler().getT_KH_PRODUCTData(session, nzNozzleData.getGoods_code())[0] ;
				bonGoodsCode = productData.getGoods_code_bonus();
		
				double basePriceDou = Double.parseDouble(PumpMUtil.getBasePrice( productData, nzNozzleData));
				unitPrice_before_discount = GlobalUtility.getMultipleWith1000(basePriceDou);
				
			} catch (Exception e) {
				LogUtility.getLogger().error(e.getMessage(), e);
			} finally {
				SqlSessionFactoryManager.closeSqlSession(session);
			}
			
			// with out pos�� ��� �ŷ�ó ������ ��Ȯ���� ���� �� BasePrice ���� �������� ������
			// with pos�� ��� �ŷ�ó ������ ��Ȯ���� ���� �� BasePrice ���� 0���� ������
			if(dwMsg.getBasePrice() == null || dwMsg.getBasePrice().equals("")){
				dwMsg.setBasePrice("0");
			}
			
			Integer up = Integer.parseInt(dwMsg.getBasePrice()); // ���� �� �ܰ�(�ŷ�ó ��ǰ �ܰ�)
			if(up < 1){
				unitPrice_after_discount = unitPrice_before_discount;
				//dwMsg.setBasePrice(unitPrice_before_discount);
			}else{
//				unitPrice_after_discount = GlobalUtility.appending0End(dwMsg.getBasePrice(), 7);
				// pi2, cwi, 2016.06.30, �ŷ�ó ���δܰ� ���� �� 1000�� ���� ���Ϸ� ������ ��� �ڿ� 0�� �ϳ� �� ���� ����
				unitPrice_after_discount = GlobalUtility.appending0Pre(GlobalUtility.getMultipleWith1000(dwMsg.getBasePrice()), 7);
				}
		}
	
		
		// ���ο��� (00: ������, 01: ����)
		if((Integer.parseInt(unitPrice_before_discount) - Integer.parseInt(unitPrice_after_discount)) < (Integer.parseInt(unitPrice_before_discount))){
			unitDiscount_ind = "01"; // ����
		}
		
		// ��ǰ���� ���ڵ�Ǽ�(�����ȣ(1) + US + ��ǰ�ڵ�(1) + US + ���ʽ���ǰ�ڵ� (1)+ US + ��ǰ����(0) + US + �������ܰ�(1) + US + ����(0) + US + ������ �ܰ�(1) + US + ���鼼����(0) + US + ���ް���(0)
		// 				   + US + ����(0) + US + �������ݾ�(0) + US + �����ıݾ�(0) + US + ��ǥ��ȣ(0) + US + �ܻ����Ÿ��(0) + US + ���ο��� (1)+ US + ��������ȣ(0) + US + ��������������(0) + US + filler)
		UPOSMessage_ItemInfo_Item itemInfo = CreateUPOSMessage.createUPOSMessage_ItemInfo_Item(nozzle_no, goodsCode, bonGoodsCode, "",
																						  unitPrice_before_discount, "", unitPrice_after_discount,
																						  "", "", "", "", "", "", "", unitDiscount_ind, "", "");
		UPOSMessage_ItemInfo item_info = null;
		item_info = UPOSMessage_ItemInfo.createUPOSMessage_ItemInfo(itemInfo);
		
		String custCard_No = pgWorkMsg.getSerialNumber(); // �ŷ�óī�� ��ȣ
		String ss_crStNum = dwMsg.getCust_code();  		  // �ŷ�ó��ȣ(�ڵ�)
		String ss_carNum = pgWorkMsg.getCarNumber();      // �ŷ�ó������ȣ
		String ss_crStNm = pgWorkMsg.getDriverName();     // �ŷ�ó��(������ �� ���� ���)		
		
		String limit_type = "0";
		String limit_amt =  "0";
		String saveLimit = "0";
		
		if(dwMsg.getLimit_type() != null) limit_type = GlobalUtility.appending0Pre(pgWorkMsg.getLimitType(),2); //�ѵ�����(01: ����, 02: �ݾ�)
		if(pgWorkMsg.getMonthLimit() != null) limit_amt = pgWorkMsg.getMonthLimit();    // �ѵ���,�ѵ��ݾ�
		if(pgWorkMsg.getSaveLimit() != null) saveLimit = pgWorkMsg.getSaveLimit();  	// ��뷮
		
		//////	 �ܷ� ���(�ѵ���,�ѵ��ݾ� - ��뷮)
		Integer limit = Integer.parseInt(limit_amt) - Integer.parseInt(saveLimit);
		String limit_remain = limit.toString();           // �ܷ�
		
		String led_code = pgWorkMsg.getTransStatus();     // LED�ڵ�(�ŷ�����->�ܻ�ŷ� ����(1: �ŷ���, 2: �ŷ�����, 3:�ŷ�����))
		String unitPrint_yn = pgWorkMsg.getPrintBase();   // �ŷ��ܰ� �ܰ���¿���(0: ��¾���, 1: �����)
		
		String carNoPrint_yn = "0"; //������ȣ ��� ����(0: ��¾���, 1: �����)
		if(!ss_carNum.equals("") || ss_carNum != null) carNoPrint_yn = "1";
			
		String FixedQty = "";
		if(dwMsg.getSliplimit_amt_stdcapa() != null) FixedQty = dwMsg.getSliplimit_amt_stdcapa(); // ������ 
		
		
		String custcar_no = GlobalUtility.appending0Pre(pgWorkMsg.getTransType(),2);	  // �ŷ�ó ����(00: ����, 01: �ܻ�, 02: �̵��ī��)
		// PI2, CWI, 2016-02-17
		// �ŷ�ó ������ ��� �� ��ǰ�� ����ڰ� �Է��� ��ǰ(���ٷ� �Ǵ�)�� �ٸ� ���
		// �ŷ������� ������ 2�� �����Ѵ�.(������ �ٸ���� ���� �ܰ��� ������� �ʵ��� �ϱ� ����)
		String ConnectGoodsCode = "";
		try {
			ConnectGoodsCode = T_NZ_NOZZLEHandler.getHandler().getGoods_codeBYNozzle_no(nozzle_no);
		} catch (Exception e) {
			LogUtility.getLogger().error(e.getMessage(), e);
		}
		
		if (!goodsCode.equals(ConnectGoodsCode)){
			LogUtility.getLogger().info("������ ��ǰ�ڵ�� ��ϵ� �ŷ�ó�� ��ǰ������ �ٸ��ϴ�.");
			custcar_no = "02";
		}else if(goodsCode.equals("0000") && goodsCode != null){
			LogUtility.getLogger().info("[With Out Pos] ������ ��ǰ�ڵ�� ��ϵ� �ŷ�ó�� ��ǰ������ �ٸ��ϴ�.");
			custcar_no = "02";
		}else if(dwMsg.getBasePrice().equals("0") || dwMsg.getBasePrice() == null){
			LogUtility.getLogger().info("�ŷ�ó �ܰ��� 0�Դϴ�. �ŷ� �Ұ���");
			custcar_no = "02";
		}
		
		String cust_nm = GlobalUtility.appending0Pre(pgWorkMsg.getReceiptType(), 2);  // ��꼭�ŷ�����(01: ��������, 02:���ݺ�����, 03:�������)
		//String cust_code =     GlobalUtility.appending0Pre(dwMsg.getRcptsheetissue_code_amtsale(), 2); 	 // �Ҽ���ó�����(01: ����, 02:�ݿø�, 03:����)
		String cust_code = "";
		if(!pgWorkMsg.getFloatTR().equals("") && pgWorkMsg.getFloatTR() != null){
			cust_code =     GlobalUtility.appending0Pre(pgWorkMsg.getFloatTR(), 2); 	 // �Ҽ���ó�����(01: ����, 02:�ݿø�, 03:����)
		}
		
		// tradeCondition = �ŷ����� + US + ������ + US + ��꼭�ŷ����� + US + �Ҽ���ó�����  -> �ŷ� �������� ��
		UPOSMessage_TradeCondition_Item tradeCondition = new UPOSMessage_TradeCondition_Item();
		tradeCondition.setCustcar_no(custcar_no);
		tradeCondition.setCustcard_no(custcard_no);
		tradeCondition.setCust_nm(cust_nm);
		tradeCondition.setCust_code(cust_code);
		
		UPOSMessage_TradeCondition tradeCondition2  = null;
		tradeCondition2 =  UPOSMessage_TradeCondition.createUPOSMessage_TradeCondition(tradeCondition);
		
		
		// PI2, cwi,2016-01-18, 4208 ������ ���� 4202 ������ ������ ������ ���� �ϱ� ���� ������� �ʴ� ������ �����Ѵ�.
		String custCar_limit_type = ""; // �ŷ�ó���������ѵ����뱸��
		String bonRSCard_no = "";  
		String bonRSCard_ID = ""; // ���ʽ���ID
		String local_point = ""; // ������(����)����
		String local_occurPoint = ""; // �߻���������
		UPOSMessage_CampInfo camp_info = null; // ķ�������� Structure
		UPOSMessage_AffilateInfo affilate_info = null; // ����ī�� ��ȸ����
		
		String download_flag = ""; // �ٿ�ε� Flag
		String pos_saleDate = UPOSUtil.getPosSaleDate(); // POS��������
		String term_ID = ""; // �ܸ��� ��ȣ
		String loanCustBonus_yn = "1"; // �ܻ� �ŷ�ó ���ʽ� ���� ����
		
		// �ܻ� �ŷ�ó�� ��츸 �ܻ�ŷ�ó ���ʽ� �������θ� �� �� �Ҽ� �ֵ��� ����
		if(custcar_no.equals("01")){
		String strData = "";
		try {
			session = SqlSessionFactoryManager.openSqlSession();
			// �ŷ�ó ���ʽ� ���� ���� �Ǵ�
			strData = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(session, IConstant.POSPORTOCOL_CODEMASTER_0256);
			if ("0".equals(strData)) {
				LogUtility.getPumpMLogger().debug("�ܻ�ŷ�ó ���ʽ� ���� �ź� ��."); 
				loanCustBonus_yn = "0";	//�ܻ� �ŷ�ó : ���ʽ� ����(X)
			} else {
				LogUtility.getPumpMLogger().debug("�ܻ�ŷ�ó ���ʽ� ���� ����."); 
				loanCustBonus_yn = "1";	//�ܻ� �ŷ�ó : ���ʽ� ����(O)
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);;
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);;
		}
		}
		
		String taxFreeCust_type = ""; // �鼼 ����
		String fixedQty_yn = ""; 	  // ���� �Է� ����
		String keepDoc_header = "";
		String keepDoc_tail1 = "";
		String keepDoc_tail2 = "";
		String keepDoc_limitDate = "";
		
		// uPOS����
		UPOSMessage upos = CreateUPOSMessage.createUPOSMessage_4208( IUPOSConstant.DEVICE_TYPE_3S,
																	   nozzle_no,     // ���ٹ�ȣ
																	   item_info,     // ��ǰ����
																	   custCard_No,   // �ŷ�óī�� ��ȣ
																	   ss_crStNum,    // �ŷ�ó��ȣ
																	   ss_carNum,     // �ŷ�ó������ȣ
																	   ss_crStNm,     // �ŷ�ó��
																	   custCar_limit_type ,
																	   limit_type,    // �ѵ��������
																	   limit_amt,     // �ѵ���,�ѵ��ݾ�
																	   limit_remain,  // �ܷ�,�ܾ�  
																	   bonRSCard_no ,
																	   bonRSCard_ID ,
																	   local_point ,
																	   local_occurPoint ,
																	   camp_info ,
																	   affilate_info ,
																	   download_flag,
																	   pos_saleDate ,
																	   term_ID,
																	   led_code,	  // LED�ڵ�
																	   unitPrint_yn,  // �ŷ��ܰ� ��¿���
																	   carNoPrint_yn, // ������ȣ ��¿���
																	   loanCustBonus_yn ,
																	   taxFreeCust_type ,
																	   fixedQty_yn ,
																	   FixedQty,	  // ������
																	   keepDoc_header ,
																	   keepDoc_tail1,
																	   keepDoc_tail2,
																	   keepDoc_limitDate,
																	   tradeCondition2
		);
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		
		// ���� GB�� ����
		GB_WorkingMessage gbWmsg = new GB_WorkingMessage();
		gbWmsg.setMessageType(upos.getMessageType());
		gbWmsg.setNozzleNo(pgWorkMsg.getNozzleNo());
		gbWmsg.setConnectNozzleNo(pgWorkMsg.getConnectNozzleNo());
		gbWmsg.setUnityMessage(upos);
		gbWmsg.setBasePrice(unitPrice_after_discount);
		
		return gbWmsg;
	}
	
	
	/**
	 * �� ���� ��ȸ ���� ������ �Ҹ����� ������ ���ؼ� HD �������� �����Ѵ�.
	 * 
	 * @param dwMsg		: POS Protocol DW ����
	 * @return
	 */
	public static HD_WorkingMessage convertHDWorkMsgFromPOSDWMsg(POS_DW dwMsg) {
		
		PG_WorkingMessage pgWorkMsg = S9Util.convertPGWorkMsgFromPOSDWMsg(dwMsg) ;
		//�����Ⱑ ����-������ ���� ���ؼ� ���������� ���� ���� ����-�������� ���� �Ǳ� ������
		//�Ҹ����� ��� ����-������ ���� �����Ѵٸ� �̸� �ٽ� ���� ������ ������ ���� ���� �� 
		//�Ҹ��� �����п� ����-�������� ���ٸ� �Ʒ��� �ڵ� ����
		
		String cusType = null; 	// ������(1: ������, 2: ������, 3: ����, 4:1ȸ����)
		String cardadj_ind = dwMsg.getCardadj_ind() ;
		if (cardadj_ind.equals(ICode.DW_CARDADJ_IND_00)) {	// Default
			cusType = "1" ;	// ��� ����
		} else if (cardadj_ind.equals(ICode.DW_CARDADJ_IND_01)) {	// ������
			cusType = "5" ;	// ������			
		} else if (cardadj_ind.equals(ICode.DW_CARDADJ_IND_02)) {	// ������
			cusType = "2" ;	// ������
		} else if (cardadj_ind.equals(ICode.DW_CARDADJ_IND_03)) {	// �ŷ�ó��
			cusType = "3" ;	// �ŷ�ó��
		} else if (cardadj_ind.equals(ICode.DW_CARDADJ_IND_04)) {	// 1ȸ ����
			cusType = "4" ;	// 1ȸ ����
		} else if (cardadj_ind.equals(ICode.DW_CARDADJ_IND_05)) {	// ���� �Է�
			cusType = "6" ;	// ���� �Է�
		}
		//	 ksm 2012.03.22 cusType - 8 : ���⺸�� ���� ó��.
		//else if (cardadj_ind.equals("06")) {
		//	cusType = "8" ;
		//}
		
		pgWorkMsg.setCusType(cusType);
		
		//����ŷ�ó üũ
		//����ŷ�ó�� ��� ������ �������� �ʴ´�.
		if (checkControlStatus2(dwMsg))
			pgWorkMsg.setTransStatus("2");
		
		
		// 2012.07.12 ksm �������ڵ� ���� ���� ����
		// twsongkis 2015-01-28 ���ο� ���ڵ� ���� ���� - ���� �Ǹűݾ��� �������� �ʾ� ""���� ó�� / �� ������ȸ ������
		String barCode = "";
		try {
			String printBarCode = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0440);
			
			if("1".equals(printBarCode))
				barCode = "";
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error("[CarWash BarCode]���� ���ڵ� ��� ���� ��ȸ�� ���� �߻�");
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		}
		
		return new HD_WorkingMessage(pgWorkMsg,dwMsg.getGoods_code(), barCode) ;
	}
	
	/**
	 * POS �� ���� ���� ��ī�� ��ȸ�� ���� ���� ������ ������� �����ϱ� ���ؼ� PG ������ �����Ѵ�.
	 * 
	 * @param dwMsg	: POS �� ���� ���� DW ����
	 * @return
	 */
	public static PG_WorkingMessage convertPGWorkMsgFromPOSDWMsg(POS_DW dwMsg) {
		
		PG_WorkingMessage pgWorkMsg = null ;
		
		String messageID = dwMsg.getMessageID() ;			// Message ID
		String odtNo = null ;								// ODT ID
		String nozzleID = dwMsg.getDeviceID() ;				// Nozzle ID
		String serialNumber = null ;						// ī���ȣ
		String cusNumber = "";		                      	// �ŷ�ó��ȣ �߰� ������ 2016.04.21
		String carNumber = null;							// ������ȣ (or ��������ȣ)
		String driverName = null;							// �����ڸ� (or ��������)
		String totalLiter = null; 							// �Ǹż��� (�Ҹ���-������ space,������-�����)
		String up = null;									// �ǸŴܰ�
		String jpLiter = null;								// ��ǥ��
		String transType = null;							// �ŷ�����(0: ����, 1: �ܻ�, 2: �̵��ī��)
		String cusType = null; 								// ������(1: ������, 2: ������, 3: ����, 4:1ȸ����
															//        			5: ������, 6: �����Է�, 7: ������ī��)
		String transStatus = null; 							// �ŷ�����->�ܻ�ŷ� ����(1: �ŷ���, 2: �ŷ�����, 3:�ŷ�����)
		String printBase = null; 							// �ܰ���¿���(0: ��¾���, 1:�ǸŴܰ�, 2:���ΰ�)
		String depositST = null; 							// ���������࿩��(1: �������, 2:����)
		String floatTR = null; 								// �Ҽ���ó�����(1: ����, 2:�ݿø�, 3:����)
		String receiptType = null; 							// ��꼭�ŷ�����(1: ��������, 2:���ݺ�����, 3:�������)
		String monthLimit = "0"; 							// �ѵ�����
		String saveLimit = "0"; 							// ��뷮
		String limitType = "1"; 							// �ѵ�����(1: ����, 2: �ݾ�)
		
		try {
			odtNo = PumpMUtil.getODTNumberFromNozzleNo(nozzleID) ;
			serialNumber = dwMsg.getCust_card_no() ;	
			cusNumber = dwMsg.getCust_code();
			carNumber = dwMsg.getCar_no();					
			driverName = dwMsg.getDrive_name();		
			up = PumpMUtil.convertNumberFormatFromPOSToPump(dwMsg.getBasePrice() , 4, 2) ;	
			jpLiter = PumpMUtil.convertNumberFormatFromPOSToPump(dwMsg.getSliplimit_amt_stdcapa() , 4, 3) ;
			
			/*
			 * card_code_base			transType (0:���� , 1:�ܻ�, 2:�̵��ī��)
			 * 		01 : ���� �ŷ�ó		0
			 * 		02 : �ܻ� �ŷ�ó		1
			 * 		03 : �뿪 ����		0
			 * 		04 : ���� ����		0
			 * 		06 : �̵�� ī��		2
			 */
			String card_code_base = dwMsg.getCard_code_base() ;

			if (card_code_base.equals(ICode.CARD_CODE_BASE_01)) {
				transType = "0" ;
			} else if (card_code_base.equals(ICode.CARD_CODE_BASE_02)) {
				transType = "1" ;
			} else if (card_code_base.equals(ICode.CARD_CODE_BASE_03)) {
				transType = "0" ;
			} else if (card_code_base.equals(ICode.CARD_CODE_BASE_04)) {
				transType = "0" ;
			} else if (card_code_base.equals(ICode.CARD_CODE_BASE_06)) {
				transType = "2" ;
			} 

			/*
			 * cardadj_ind			cus_type
			 * 		00 : Default	1 : ������
			 * 		01 : ������		5 : ������
			 * 		02 : ������		2 : ������
			 * 		03 : �ŷ�ó��		3 : ����
			 * 		04 : 1ȸ ����		4 : 1ȸ ����
			 * 		05 : �����Է�		6 : �����Է�
			 */
			String cardadj_ind = dwMsg.getCardadj_ind() ;
			if (cardadj_ind.equals(ICode.DW_CARDADJ_IND_00)) {			// ��� ����
				cusType = "1" ;	// ��� ����
			} else if (cardadj_ind.equals(ICode.DW_CARDADJ_IND_01)) {	// ������
				if (card_code_base.equals(ICode.CARD_CODE_BASE_01)) { 
				// �������� ���ݰŷ�ó-������ �� ��� ODT�� ����-�������� ���� ������ ���� ������ �� ������ �ش�. 
					cusType = "2" ;	// ������
				} else {
					cusType = "5" ;	// ������
				}
			} else if (cardadj_ind.equals(ICode.DW_CARDADJ_IND_02)) {	// ������
				cusType = "2" ;	// ������
			} else if (cardadj_ind.equals(ICode.DW_CARDADJ_IND_03)) {	// �ŷ�ó��
				cusType = "3" ;	// �ŷ�ó��
			} else if (cardadj_ind.equals(ICode.DW_CARDADJ_IND_04)) {	// 1ȸ ����
				cusType = "4" ;	// 1ȸ ����
			} else if (cardadj_ind.equals(ICode.DW_CARDADJ_IND_05)) {	// ���� �Է�
				cusType = "6" ;	// ���� �Է�
			} 

			/*
			 * status_code_card		transStatus
			 * 		01 : ����		1 : �ŷ���
			 * 		02 : ����		2 : �ŷ�����
			 * 		03 : ����		3 : �ŷ�����
			 */
			String status_code_card = dwMsg.getStatus_code_card() ;
			if (status_code_card.equals(ICode.STATUS_CODE_CARD_01)) {
				transStatus = "1" ;
			} else if (status_code_card.equals(ICode.STATUS_CODE_CARD_02)) {
				transStatus = "2" ;
			} else if (status_code_card.equals(ICode.STATUS_CODE_CARD_03)) {
				transStatus = "3" ;
			}
		
			/*
			 * �ܰ� ��� ����
			 * rcptunitprc_ind_prt		printBase
			 * 		0 : ��� ����			
			 * 		1 : �ǸŴܰ�		
			 *		2 : ���ΰ�			
			 */
			printBase = dwMsg.getRcptunitprc_ind_prt() ;
			
			/* 2011.02.16 ksm �ܰ���¿��� �� �״�� ���.
			if (rcptunitprc_ind_prt.equals("0")) {
				printBase = "0" ;
			} else if (rcptunitprc_ind_prt.equals("1")) {
				printBase = "1" ;
			} else if (rcptunitprc_ind_prt.equals("2")) {
				printBase = "2" ;
			}
			*/

						
			/*
			 * ������ ���� ����
			 * 		keepissue_ind		depositST
			 * 		0 : �ƴϿ�				1
			 * 		1 : ��					2
			 */
			String keepissue_ind = dwMsg.getKeepissue_ind() ;
			if (ICode.KEEPISSUE_IND_0.equals(keepissue_ind)) {
				depositST = "1" ;
			} else if (ICode.KEEPISSUE_IND_1.equals(keepissue_ind)) {
				depositST = "2" ;
			} 
			
			/*
			 * �Ҽ��� ó�� ���
			 * 		rcptsheetissue_code_amtsale			floatTR
			 * 			01 : ����							1
			 * 			02 : �ݿø�							2
			 * 			03 : ����							3
			 */
			String rcptsheetissue_code_amtsale = dwMsg.getRcptsheetissue_code_amtsale() ;
			if (rcptsheetissue_code_amtsale.equals(ICode.RCPTSHEETISSUE_CODE_AMTSALE_01)) {
				floatTR = "1" ;
			} else if (rcptsheetissue_code_amtsale.equals(ICode.RCPTSHEETISSUE_CODE_AMTSALE_02)) {
				floatTR = "2" ;
			} else if (rcptsheetissue_code_amtsale.equals(ICode.RCPTSHEETISSUE_CODE_AMTSALE_03)) {
				floatTR = "3" ;
			} 
			
			/*
			 * ��꼭 �ŷ� ����
			 */
			receiptType = "1" ; 		// default

			/*
			 * �ѵ� ����(�ݾ�) / ���� ��뷮 (�ݾ�)( monthLimit / saveLimit )
			 * 
			 * adjbase_code_limit		limitType
			 * 		01 : ����				1
			 * 		02 : �ݾ�				2
			 */
			String adjbase_code_limit = dwMsg.getAdjbase_code_limit() ;
			if (adjbase_code_limit.equals(ICode.ADJBASE_CODE_LIMIT_01)) {
				limitType = "1" ;
				monthLimit = 
					PumpMUtil.convertNumberFormatFromPOSToPump(dwMsg.getLimit() , 15, 3) ; 	// �ѵ����� 
				saveLimit = 
					PumpMUtil.convertNumberFormatFromPOSToPump(dwMsg.getAccLimit() , 15, 3);	// ��뷮	
			} else if (adjbase_code_limit.equals(ICode.ADJBASE_CODE_LIMIT_02)) {
				limitType = "2" ;	
				monthLimit = GlobalUtility.getStringValue(dwMsg.getLimit()) ; 					// �ѵ��ݾ�
				saveLimit = GlobalUtility.getStringValue(dwMsg.getAccLimit()) ;				// ���ݾ�
			} 
			
					
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
//		LogUtility.getPumpMLogger().debug("[TEST] Ȥ�� ������ ó�� �ȵǰ� ���� ���Գ�??? nozzleID="+nozzleID);

		//����ŷ�ó üũ
		//����ŷ�ó�� ��� ������ �������� �ʴ´�.
		if (checkControlStatus2(dwMsg))
			transStatus = "2";
		
//		�� ī�� ������ TransactionData Class �� �����Ѵ�.
//		LogUtility.getPumpMLogger().debug("[TEST] �� ī�� ������ TransactionData Class �� �����Ѵ� serialNumber=" + serialNumber + ", nozzleID="+nozzleID);
		if (!transType.equals("2") && !transStatus.equals("2") && !transStatus.equals("3")) {
			// transType �� �̵��ī�尡 �ƴ� ��
			// transStatus �� ������ �ƴ� ��
			// transStatus �� ���Ұ� �ƴ� �� �� ������
			PumpMTransactionManager.getInstance().setCustCardNumber(nozzleID, serialNumber);	
		}
		
		pgWorkMsg = new PG_WorkingMessage(messageID,
									odtNo,
									nozzleID,
									serialNumber,
									cusNumber, 
									carNumber,
									driverName,
									totalLiter,
									up,
									jpLiter,
									transType,
									cusType,
									transStatus,
									printBase,
									depositST,
									floatTR,
									receiptType,
									monthLimit,
									saveLimit,
									limitType) ;
		
		return pgWorkMsg ;
	}
	

	/**
	 * DU ������ �̿��Ͽ� ������ S9 ������ �����Ѵ�. �̴� ������ȸ�� ������ 1���� ��� �� ��ȸ�� �ٽ� ��û�ϵ��� �Ѵ�.
	 * 
	 * @param duMsg	: POS Protocol DU ���� (�� ���� ��ȸ ����)
	 * @return
	 */
	public static S9_WorkingMessage createS9WorkingMessage(POS_DU duMsg) {
		LogUtility.getPumpMLogger().info("[Pump M] Convert DU to S9.duMsg.getDup()=" + duMsg.getDup()) ;
		S9_WorkingMessage s9WorkMsg = null ;
		
		
		if (duMsg.getDup() == 1) {
			String messageID = duMsg.getMessageID() ;
			String odtID = duMsg.getDeviceID() ;
			String nozzleID = duMsg.getDeviceID() ;
			String mode = "0" ;
			String serialNumber = duMsg.getCarInfoArray()[0].getCust_card_no() ;
			
			s9WorkMsg = new S9_WorkingMessage(messageID,
								odtID,
								nozzleID,
								mode,
								serialNumber) ;
		}
		LogUtility.getPumpMLogger().debug("[Pump M] Changed S9 is like below.") ;
		if (s9WorkMsg != null) s9WorkMsg.print() ;
		
		return s9WorkMsg;
	}

	private static CB_WorkingMessage getCustomer_type( POS_DW dwMsg, CB_WorkingMessage cbWorkingMsg){
		
		if ((ICode.STATUS_CODE_CARD_02.equals(dwMsg.getStatus_code_card()) || ICode.STATUS_CODE_CARD_03.equals(dwMsg.getStatus_code_card())) ) {
			if (GlobalUtility.isNullOrEmptyString(dwMsg.getCust_code()))
				// �Ϲݰ�
				cbWorkingMsg.setCustomerType(ICustConstant.DASNO_CUST_TYPE_1);
			else {
				// �ŷ� ���� �ܻ� �ŷ�ó
				cbWorkingMsg.setCustomerType(ICustConstant.DASNO_CUST_TYPE_0);
				cbWorkingMsg.setMessage("�ŷ�����");
			}
			return cbWorkingMsg;
		}
		
		String saveBonusYn = "0";
		
		if (ICode.CARD_CODE_BASE_01.equals(dwMsg.getCard_code_base())) {
			//���ݰŷ�ó
			cbWorkingMsg.setCustomerType(ICustConstant.DASNO_CUST_TYPE_2);
			saveBonusYn = "1";
		}
		else if (ICode.CARD_CODE_BASE_02.equals(dwMsg.getCard_code_base())) {
			//�ܻ�ŷ�ó
			try {
				saveBonusYn = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0256);
			} catch (Exception e) {
				 LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
			} 
			
			if (ICode.CARDADJ_IND_05.equals(dwMsg.getCardadj_ind())) {	// ���� �Է�
				cbWorkingMsg.setCustomerType(ICustConstant.DASNO_CUST_TYPE_4);
			}else {
				//�Ϲ� �ܻ� �ŷ�ó
				cbWorkingMsg.setCustomerType(ICustConstant.DASNO_CUST_TYPE_3);
			}

		} else if (ICode.CARD_CODE_BASE_06.equals(dwMsg.getCard_code_base())) {
			//�̵��ī��� �Ϲݰŷ�
			cbWorkingMsg.setCustomerType(ICustConstant.DASNO_CUST_TYPE_2);
			cbWorkingMsg.setMessage("�̵�� ī���Դϴ�.");
			saveBonusYn = "1";
		}


		if ( Double.parseDouble(dwMsg.getBasePrice()) == 0) {
			cbWorkingMsg.setCustomerType(ICustConstant.DASNO_CUST_TYPE_0);
			cbWorkingMsg.setMessage("�ŷ����� ����ġ");
		}
		
		cbWorkingMsg.setSaveBonus(saveBonusYn);
		
		//����ŷ�ó üũ
		//����ŷ�ó�� ��� ������ �������� �ʴ´�.
		if (checkControlStatus(dwMsg))
			cbWorkingMsg.setCustomerType(ICustConstant.DASNO_CUST_TYPE_0);
		
		return cbWorkingMsg;
	}
	
	/**
	 * ������κ��� '�� ī�� ���ο�û'(S9����) ���� ������ ��ȸ�� ��� �������� ������ �����մϴ�.
	 * 
	 * @param s9WorkMsg	: ������κ��� �ö�� S9 ����
	 * @return
	 */
	public static WorkingMessage getPGWorkingMessageForChargingPerson(S9_WorkingMessage s9WorkMsg) {
		  WorkingMessage workMsg = null ;
		  try {
			T_KH_CHARGING_PERSONData chargingPersonData = 
				  T_KH_CHARGING_PERSONHandler.getHandler().getT_KH_CHARGING_PERSONData(s9WorkMsg.getSerialNumber()) ;
			if (chargingPersonData != null) {
				workMsg = new PG_WorkingMessage() ;
				((PG_WorkingMessage)workMsg).setSerialNumber(chargingPersonData.getCardno_nbr()) ;
				((PG_WorkingMessage)workMsg).setCarNumber(chargingPersonData.getEmpl_no()) ; ;
				((PG_WorkingMessage)workMsg).setDriverName(chargingPersonData.getEmpl_name()) ;
				((PG_WorkingMessage)workMsg).setNozzleNo(s9WorkMsg.getNozzleNo()) ;
				((PG_WorkingMessage)workMsg).setCusType("7") ;				// ������ ī�� ����
				((PG_WorkingMessage)workMsg).setTransType("0") ;			// ���� ����
				((PG_WorkingMessage)workMsg).setTransStatus("1") ;			// �ŷ��� ����		
				LogUtility.getPumpMLogger().debug("[Pump M] Charging Card") ;
				
				PumpMODTSaleManager.setChargingPerson(s9WorkMsg.getNozzleNo(), chargingPersonData.getEmpl_no()) ;
			} 
		  } catch (Exception e) {
			  LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		  } finally {
/*			  if ((workMsg == null) && (s9WorkMsg.getSerialNumber().startsWith("0190610000229879"))) {
				  LogUtility.getPumpMLogger().info("[Pump M] �������� ������ ����մϴ�.");
					workMsg = new PG_WorkingMessage() ;
					((PG_WorkingMessage)workMsg).setSerialNumber("0190610000229879") ;
					((PG_WorkingMessage)workMsg).setCarNumber("0010") ; ;
					((PG_WorkingMessage)workMsg).setDriverName("����ȣ") ;
					((PG_WorkingMessage)workMsg).setNozzleNo(s9WorkMsg.getNozzleNo()) ;
					((PG_WorkingMessage)workMsg).setCusType("7") ;			// ������ ī�� ����
					((PG_WorkingMessage)workMsg).setTransType("0") ;			// ���� ����
					((PG_WorkingMessage)workMsg).setTransStatus("1") ;		// �ŷ��� ����		
					LogUtility.getPumpMLogger().debug("[Pump M] Charging Card") ;
					
					PumpMODTSaleManager.setChargingPerson(s9WorkMsg.getNozzleNo(), "0010") ;
			  } else */
				  if (workMsg == null) {
					workMsg = new PG_WorkingMessage() ;
					((PG_WorkingMessage)workMsg).setNozzleNo(s9WorkMsg.getNozzleNo()) ;
					((PG_WorkingMessage)workMsg).setCusType("7") ;		// ������ ī�� ����
					((PG_WorkingMessage)workMsg).setTransType("2") ;		// �̵�� ī�� ����
					((PG_WorkingMessage)workMsg).setTransStatus("2") ;	// �ŷ� ���� ����	
					LogUtility.getPumpMLogger().debug("[Pump M] No Charging Card") ;
			  }
		  }
		  workMsg.setMessageID(s9WorkMsg.getMessageID()) ;
		  return workMsg ;
	}
	
	/**
	 * �� ���� ��ȸ�� POS �� ��û�ϱ� ���ؼ� DT ������ �����Ѵ�.
	 * 
	 * @param messageID		: Message ID
	 * @param nozzleNo		: ���� ��ȣ
	 * @param car_short_no	: ���� ���� ��ȣ
	 * @return
	 */
	public static POS_DT getPOSPumpM_DT(String messageID, String nozzleNo, String car_short_no) {
		return new POS_DT(messageID, nozzleNo, car_short_no);
	}

	/**
	 * POS �� ��ī�� ��ȸ ��û�� �ϱ� ���ؼ� DV ������ �����Ѵ�.
	 * 
	 * @param messageID			: Message ID
	 * @param nozzle_no			: ���� ��ȣ
	 * @param khTransactionID	: KH ó����ȣ
	 * @param cust_card_no		: �ŷ�ó ī�� ��ȣ
	 * @return
	 */
	public static POS_DV getPOSPumpM_DV(String messageID, String nozzle_no, String khTransactionID, String cust_card_no) {
		POS_DV dvPumpMData = null ;
		SqlSession session = null;
		
		String goods_code = "" ;	// ��ǰ �ڵ�
		String basePrice = "" ;		// ���ΰ�
		try {
			session = SqlSessionFactoryManager.openSqlSession();
			
			T_NZ_NOZZLEData nzData = T_NZ_NOZZLEHandler.getHandler().getT_NZ_NOZZLEDataByNozzleNo(session, nozzle_no)[0] ;
			goods_code = nzData.getGoods_code() ;
			basePrice = T_KH_PRODUCTHandler.getHandler().getBasePrice(session, goods_code, nzData.getSelf_ind_exist()) ;
		
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}
		
		dvPumpMData = new POS_DV(messageID, nozzle_no, khTransactionID, cust_card_no, goods_code, basePrice);
		return dvPumpMData ;
	}
	
	/**
	 * POS �� ���� ������ ��� DV ���� ��û�� ���� DW ���� ������ KixxHub ��ü������ �����Ѵ�.
	 * 
	 * @param dvPosMsg	: POS �� �����ߴ� DV ����
	 * @return			: ��ü ������ DW ����
	 */
	public static POS_DW processPOSPumpM_DV(POS_DV dvPosMsg) {
		LogUtility.getPumpMLogger().info("[Pump M] ��ü������ DV ������ �̿��Ͽ� DW ������ �����Ѵ�.") ;		
		// Debug Purpose
		LogUtility.getPumpMLogger().info(dvPosMsg.toString());
		String messageID 							= dvPosMsg.getMessageID() ;				
		String deviceID 							= dvPosMsg.getDeviceID() ;			// ���� ��ȣ
		String khTransactionID 						= dvPosMsg.getKhTransactionID() ;	// KH ó����ȣ
		String cust_card_no 						= dvPosMsg.getCust_card_no() ;		// �ŷ�ó ī�� ��ȣ
		String car_no 								= "" ;								// ���� ��ȣ
		String drive_name 							= "" ;								// ������ ��
		String cust_code 							= "" ;
		String goods_code 							= dvPosMsg.getGoods_code() ;		// ��ǰ �ڵ� (Default �� ������ ��ǰ �ڵ�)
		String basePrice 							= "" ;								// �Ǹ� �ܰ�
		String cust_cd_item 						= "" ;								// �ŷ�ó ����
		String card_code_base 						= "" ;								// ī�� ����
		String rentlimit_proc_ind_overlimit			= ICode.PROC_IND_OVERLIMIT_01 ;		// �ܻ����Ÿ��(2b)
		String cardadj_ind 							= ICode.CARDADJ_IND_00 ;			// ī�� ���� ����
		String status_code_card						= ICode.STATUS_CODE_CARD_02 ;		// �ŷ� ���� (Default : ����)
		String sliplimit_amt_stdcapa 				= "" ;								// ��ǥ�ѵ� 1ȸ ����
		String rcptunitprc_ind_prt 					= "" ;								// �������ܰ�ǥ�⿩��
		String keepissue_ind 						= "" ;								// ���������࿩��
		String rcptsheetissue_code_amtsale			= "" ;								// ����ݾ�ó������
		String receipt_type	 						= "" ;								// ��꼭 �ŷ� ����
		String limit_type 							= "" ;								// �ѵ�����
		String adjbase_code_limit 					= "" ;								// �ѵ��������
		String limit 								= "0" ;								// �ѵ�����
		String accLimit 							= "0" ;								// ������뷮
		
		POS_DW dwPosPumpMsg = null ;
		CustReturnValue custReturnValue = CustUtil.processCustWithCustCardNo(dvPosMsg.getDeviceID() ,
				dvPosMsg.getCust_card_no() ,
				ICode.DX_TAXFREECUST_TYPE_0) ;

		int state = ICustConstant.STATE_0 ;
		
		if (custReturnValue != null) {
			state = custReturnValue.getState() ;
			
			car_no = custReturnValue.getCarno_nbr() ;						// ���� ��ȣ
			drive_name = custReturnValue.getCust_name() ;					// ������ ��
			cust_code = custReturnValue.getCust_code() ;					// �ŷ�ó ��ȣ
			cust_cd_item = custReturnValue.getCust_cd_item() ;				// �ŷ�ó ����
			rentlimit_proc_ind_overlimit = custReturnValue.getProc_ind_overlimit() ;	// �ѵ��ʰ��Ǹ�ó������

			// ī�� ���� ���� : card_code_base
			switch (state) {			
				case ICustConstant.STATE_0 : 
				case ICustConstant.STATE_1 :{
					card_code_base = ICode.CARD_CODE_BASE_06 ;
					break ;
				}
				case ICustConstant.STATE_10 : 
				case ICustConstant.STATE_11 : 
				case ICustConstant.STATE_100 : 
				case ICustConstant.STATE_110 : 
				case ICustConstant.STATE_12 : 
				case ICustConstant.STATE_13 :{
					// ����  �� 
					card_code_base = ICode.CARD_CODE_BASE_01 ;
					break ;
				}
				case ICustConstant.STATE_30 : 
				case ICustConstant.STATE_31 : 
				case ICustConstant.STATE_32 :
				case ICustConstant.STATE_33 :
				case ICustConstant.STATE_40 : 
				case ICustConstant.STATE_41 : 
				case ICustConstant.STATE_42 :
				case ICustConstant.STATE_43 :
				case ICustConstant.STATE_50 : 
				case ICustConstant.STATE_51 : 
				case ICustConstant.STATE_52 :
				case ICustConstant.STATE_53 :
				case ICustConstant.STATE_60 : 
				case ICustConstant.STATE_61 : 
				case ICustConstant.STATE_62 : 
				case ICustConstant.STATE_63 : 
				case ICustConstant.STATE_90 :
				case ICustConstant.STATE_91 :
				case ICustConstant.STATE_92 :
				case ICustConstant.STATE_93 :{
					// �ܻ� ��
					card_code_base = ICode.CARD_CODE_BASE_02 ;
					break ;
				}
				case ICustConstant.STATE_70 : {
					// �뿪 ���� �� - ���ΰ�
					card_code_base = ICode.CARD_CODE_BASE_03 ;
					break ;
				}
				case ICustConstant.STATE_80 : {
					// ���� ���� �� - ���ΰ�
					card_code_base = ICode.CARD_CODE_BASE_04 ;
					break ;
				}
				default : {
					card_code_base = ICode.CARD_CODE_BASE_06 ;
					break ;
				}				
			}

			// ī�� ���� ���� ���� : cardadj_ind
			switch (state) {			
				case ICustConstant.STATE_0 : 
				case ICustConstant.STATE_1 :
				case ICustConstant.STATE_10 : 
				case ICustConstant.STATE_11 : 
				case ICustConstant.STATE_12 : 
				case ICustConstant.STATE_13 : 
				case ICustConstant.STATE_70 : 
				case ICustConstant.STATE_80 : 
				case ICustConstant.STATE_100 : 
				case ICustConstant.STATE_110 : {
					cardadj_ind = ICode.DW_CARDADJ_IND_00 ;	// ��� ����
					break ;
				}
				case ICustConstant.STATE_30 : 
				case ICustConstant.STATE_31 : 
				case ICustConstant.STATE_32 : 
				case ICustConstant.STATE_33 : {
					cardadj_ind = ICode.DW_CARDADJ_IND_01 ;	// ������
					break ;
				}
				case ICustConstant.STATE_40 : // �ܻ� �ŷ�ó - �������ѵ� - ����
				case ICustConstant.STATE_41 : 
				case ICustConstant.STATE_42 : 
				case ICustConstant.STATE_43 : {
					cardadj_ind = ICode.DW_CARDADJ_IND_02 ;	// ������
					break ;
				}
				case ICustConstant.STATE_50 : // �ܻ� �ŷ�ó - �ŷ�ó���ѵ� - ����
				case ICustConstant.STATE_51 : 
				case ICustConstant.STATE_52 : 
				case ICustConstant.STATE_53 : {
					cardadj_ind = ICode.DW_CARDADJ_IND_03 ;	// �ŷ�ó��
					break ;
				}
				case ICustConstant.STATE_60 : 	// �ܻ� �ŷ�ó - 1ȸ ����  - ����
				case ICustConstant.STATE_61 : 
				case ICustConstant.STATE_62 : 
				case ICustConstant.STATE_63 : {
					cardadj_ind = ICode.DW_CARDADJ_IND_04 ;	// 1ȸ ����
					break ;
				}
				case ICustConstant.STATE_90 : 	// �ܻ� �ŷ�ó - �����Է�  - ����
				case ICustConstant.STATE_91 : 
				case ICustConstant.STATE_92 : 
				case ICustConstant.STATE_93 : {
					cardadj_ind = ICode.DW_CARDADJ_IND_05 ;	// ���� �Է�
					break ;
				}
				default : {
					cardadj_ind = ICode.DW_CARDADJ_IND_00 ;				
					break ;
				}				
			}		
			
			// ��ǰ �ڵ� : goods_code
			switch (state) {			
				case ICustConstant.STATE_11 : 
				case ICustConstant.STATE_13 :
				case ICustConstant.STATE_31 : 
				case ICustConstant.STATE_33 :
				case ICustConstant.STATE_41 : 
				case ICustConstant.STATE_43 :
				case ICustConstant.STATE_51 : 
				case ICustConstant.STATE_53 :
				case ICustConstant.STATE_61 : 
				case ICustConstant.STATE_63 :
				case ICustConstant.STATE_91 : 
				case ICustConstant.STATE_93 : {
					goods_code = ICode.DW_GOODS_NOT_MATCHING ;
					break ;
				}
				default : {
					goods_code = dvPosMsg.getGoods_code() ;				
					break ;
				}				
			}	
			
			status_code_card = custReturnValue.getTrans_code_status() ;			// �ŷ� ����			
			
			// PL�� ���� ��� HOS�� �������� > �������� > ȯ�漳�� ȭ���� ����⺻���� ���� �����ڵ� 0251, 
			// ���� �̵�� ��ǰ ����ó�� ���ΰ� '1'�� ��� ���ΰ��� �ܰ��� �����Ѵ�.
			if (Integer.parseInt(custReturnValue.getDiscountBasePrice()) == 0) {
				String custsale_ind = null ;
				
				try {
					custsale_ind = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0251);
				} catch (Exception e) {
					LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
				}
				
				if ((custsale_ind != null) && (custsale_ind.equals("1"))) {
					LogUtility.getPumpMLogger().debug("[Pump M] ��ǰ ����ġ ������, '�ŷ�ó���Ǹſ���'(���帶����) �� �Ǹ��̱� " +
							"������ ���ΰ��� �ǸŸ� �Ѵ�. custsale_ind=" + custsale_ind) ;
					basePrice = custReturnValue.getBasePrice();
				} else {
					/**
					 * 2016. 4. 14. ���� 15:48:31, PI2, ���ÿ� Bug Fix.
					 * 	PL ���ſ� �̵�ϵǾ� �ְ�, ����⺻���� ���� �Ǹ� �Ұ��� ���
					 * 	ī�带 �̵�� ī��� ������ �ϰ�, ���¸� ������ ������ ������.
					 */
					basePrice = custReturnValue.getBasePrice(); 
					card_code_base = ICode.CARD_CODE_BASE_06 ;
					status_code_card = ICode.STATUS_CODE_CARD_02 ;
				}
			} else {
				basePrice = custReturnValue.getDiscountBasePrice() ;			// �Ǹ� �ܰ�				
			}
			
			sliplimit_amt_stdcapa = custReturnValue.getAmount1() ;				// ��ǥ�ѵ� 1ȸ ����
			rcptunitprc_ind_prt = custReturnValue.getRcptunitprc_ind_prt() ;	// �������ܰ�ǥ�⿩��
			keepissue_ind = custReturnValue.getKeepissue_ind() ;				// ���������࿩��
			rcptsheetissue_code_amtsale = custReturnValue.getRcptsheetissue_code_amtsale() ;	// ����ݾ�ó������
			receipt_type = ICode.DW_RECEIPT_TYPE_1 ;					// ��꼭 �ŷ� ����

			/**
			 * rcptunitprc_ind_prt (POS , KH)		ODT
			 * 		0	�μ� ����						0 (������ �ܰ� ǥ�� - X)
			 * 		1	�ŷ� �ܰ�						1 (������ �ܰ� ǥ�� - O)
			 * 		2	���ΰ�						1 (������ �ܰ� ǥ�� - O)
			 */
			/*  2011.02.16 ksm ������ �ܰ� ��¿��ΰ� �״�� ���.
			if ("0".equals(rcptunitprc_ind_prt)) {
				rcptunitprc_ind_prt = "0" ;
			} else if ("1".equals(rcptunitprc_ind_prt)) {
				rcptunitprc_ind_prt = "1" ;
			} else if ("2".equals(rcptunitprc_ind_prt)) {
				rcptunitprc_ind_prt = "2" ;
			}
			*/
			
			// �ѵ����� : limit_type
			switch (state) {			
				case ICustConstant.STATE_40 : {
					limit_type = ICode.DY_LIMIT_TYPE_01 ;
					break ;
				}
				case ICustConstant.STATE_50 : {
					limit_type = ICode.DY_LIMIT_TYPE_02 ;
					break ;
				}	
				default : {
					limit_type = ICode.DY_LIMIT_TYPE_00 ;
					break ;	
				}
			}
			
			// �ѵ����� ���� : adjbase_code_limit
			switch (state) {			
				case ICustConstant.STATE_40 : 
				case ICustConstant.STATE_50 : {
					LimitAmount limitAmount = custReturnValue.getLimitAmount() ;

					limit = limitAmount.getLimit() ;
					accLimit = limitAmount.getUsedAmount() ;
					int priceOrLiter = limitAmount.getPricePrLiter() ;	
					
					if (priceOrLiter == ICustConstant.LIMIT_AMOUNT_LITER) {
						adjbase_code_limit = ICode.ADJBASE_CODE_LIMIT_01 ;
					} else {
						adjbase_code_limit = ICode.ADJBASE_CODE_LIMIT_02 ;						
					}
					
					break ;
				}	
			}
			
		} else {
			LogUtility.getPumpMLogger().debug("[Pump M] �ŷ�ó ���� ���� ���� ���� ��� Default ���� �����Ѵ�.") ;
			card_code_base = ICode.CARD_CODE_BASE_06 ;
			cardadj_ind = ICode.CARDADJ_IND_00 ;
			status_code_card = ICode.STATUS_CODE_CARD_02 ;
		}
		


		/**
		 * CC�Ϻ� �ŷ�ó ����ŷ�ó üũ
		 */
		SqlSession session = null;

		String control_status = "00";
		String control_yn = "0";
		
		try {
			session = SqlSessionFactoryManager.openSqlSession();
			T_KH_VIOLATIONData data = T_KH_VIOLATIONHandler.getHandler().getT_KH_VIOLATIONDataByCustCode(session, cust_code);
			
			if (data != null){
				control_status = data.getControl_status();
				control_yn = data.getControl_yn();
			} 
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}		
		
		dwPosPumpMsg = new 	POS_DW(messageID, 
				deviceID, 
				khTransactionID,
				cust_card_no,
				car_no,
				drive_name,
				cust_code,
				goods_code,
				basePrice,
				cust_cd_item,
				card_code_base,
				cardadj_ind,
				status_code_card,
				rentlimit_proc_ind_overlimit,
				sliplimit_amt_stdcapa,
				rcptunitprc_ind_prt,
				keepissue_ind,
				rcptsheetissue_code_amtsale,
				receipt_type,
				limit_type,
				adjbase_code_limit,
				limit,
				accLimit,
				control_status,
				control_yn)  ;

		LogUtility.getPumpMLogger().debug("[Pump M] DW Content is like below.") ;
		LogUtility.getPumpMLogger().info(dwPosPumpMsg.toString());
		return dwPosPumpMsg ;
	}
	
}
