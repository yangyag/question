package com.gsc.kixxhub.module.pumpm.pump.util;

import java.util.ArrayList;
import java.util.Vector;

import com.gsc.kixxhub.common.data.IConstant;
import com.gsc.kixxhub.common.data.IUPOSConstant;
import com.gsc.kixxhub.common.data.posdata.POSHeader;
import com.gsc.kixxhub.common.data.posdata.POS_DW;
import com.gsc.kixxhub.common.data.pump.HA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HC_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PB_ST_TrInfo;
import com.gsc.kixxhub.common.data.pump.ST_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.data.pump.format.PumpMessageFormat;
import com.gsc.kixxhub.common.data.upos.CreateUPOSMessage;
import com.gsc.kixxhub.common.data.upos.UPOSMessage;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_CampInfo;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_ItemInfo;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_CODEMASTERHandler;
import com.gsc.kixxhub.common.dbadapter.pump.handler.T_NZ_NOZZLEHandler;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.module.pumpm.pump.manager.PumpMODTSaleManager;

public class ODTUtility_SoMo {

	public static byte DELIMITER_0X1E = 0x1e ;	// RS
	public static String DELIMITER_RS_STRING = Character.toString((char)DELIMITER_0X1E) ;	
	
	/**
	 * �Ҹ� ������ ���� �Ǹ� �Ϸ� ���� (ST) ���뿡�� ������ ������ �ִ� ��� POS �� 0001 ������ �������� �ʴ´�.
	 * 2012.06.07 ksm ���������� ��� �����Ͽ� ���а� �ִ��� Ȯ���ϱ� ���� �����.
	 * @param workMsg	: �ǸſϷ� ����. ST_WorkingMessage
	 * @return
	 */
	public static boolean containFailContent(WorkingMessage workMsg) {
		boolean rlt = false ;
		try {
			ST_WorkingMessage stWorkMsg = (ST_WorkingMessage) workMsg ;
			Vector<PB_ST_TrInfo> trInfoVector = stWorkMsg.getTrInfoVector() ;
			
			int size = 0 ;
			if (trInfoVector != null) {
				size = trInfoVector.size() ;
			}
			
			for (int i = 0 ; i < size ; i++) {
				PB_ST_TrInfo trInfo = trInfoVector.get(i) ;
				String mode = trInfo.getMode() ;	// 4 : ����� ����
																			// 5 : ����� ����
				// ���ο� ���� ��� Ȥ�� ����� ���� ������ �ִ� ���
    			if ("4".equals(mode) || "5".equals(mode)) {
    				rlt = true ;
    				return rlt ;
    			}
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
		}
		return rlt ;
	}
		
	/**
	 * �Ҹ����� ��� ���� ������ �ִ�. �̴� CAT M �� ���ؼ� ���� ������ �޴� ���� �ƴϸ�, Pump M ���� ��ü������ ����
	 * POS �� Pump A �� �����Ѵ�. 
	 * 
	 * @param workingMsg	: HA_WorkingMessage
	 * @param khproc_no		: KH ó����ȣ
	 * @return
	 */
	public static UPOSMessage create0012UPOSMessageFromCashRequest_SoMo(WorkingMessage workingMsg, String khproc_no) {
		HA_WorkingMessage haWorkingMsg = (HA_WorkingMessage) workingMsg ;
		String nozzleNo = haWorkingMsg.getConnectNozzleNo();
		String liter = PumpMUtil.convertNumberFormatFromPumpToStandardFormat(haWorkingMsg.getLiter(),3) ;
		String basePrice = PumpMUtil.convertNumberFormatFromPumpToStandardFormat(haWorkingMsg.getBasePrice(),2) ;
		String payPrice = GlobalUtility.getStringValue(haWorkingMsg.getPrice()) ;
		
		UPOSMessage_ItemInfo itemInfo = UPOSUtil.getUPOSMessage_ItemInfo(nozzleNo, 
														khproc_no,
														liter,
														basePrice,
														payPrice) ;
		
		UPOSMessage uPosMsg = CreateUPOSMessage.createUPOSMessage_0012(	IUPOSConstant.DEVICE_TYPE_3S, 
														khproc_no, 
														nozzleNo,
														null,
														itemInfo, 
														null, 					
														null,
														null,
														null, 
														payPrice, 
														null, 
														"1") ;
		
		return uPosMsg ;
	}
	
	/**
	 * BL üũ ���� ������ ��û�Ѵ�.
	 * 
	 * @param workingMsg	: WorkingMessage
	 * @return
	 */
	public static UPOSMessage create0071UPOSMessageFromWorkingMessage_SoMo(WorkingMessage workingMsg) {
		HA_WorkingMessage haWorkingMsg = (HA_WorkingMessage) workingMsg ;
		
		String creditCard_no = haWorkingMsg.getCardNumber() ; 
		String nozzle_no 		= haWorkingMsg.getConnectNozzleNo() ;
		String payment_amt = haWorkingMsg.getPrice() ; ; 
		String pos_ip 				= UPOSUtil.getPOSIP() ; 
		String pos_port 			= UPOSUtil.getPOSPort() ; 
		String pos_saleDate 	= UPOSUtil.getPosSaleDate() ; 
		String authType 			= haWorkingMsg.getAuthType();	// 2016.03.30 WooChul Jung. 
		//	Explicitly express stage of request. Please reference to HA_WorkingMessage Class
		//�������濡 ���� �׸� �߰�(2015.11.17) 
		String creditCardReading_type = IConstant.CreditCardReading_Type_MAGNETIC ;
		String chipData = "" ; 
		String certification_id = "";
		String signImage_Info = "";
		String signImage_Data = "";
		String term_id = ODTUtility_Common.getTermId() ;
		String store_cd = ODTUtility_Common.getStoreCode();
		String encryptCredit_no = "";
		String creditPassCode = "";
		String selfPayment_type = authType ;	// 2016.03.30 WooChul Jung. New Spec moves to selfPayment_type from filler1
		String payment_tax = GlobalUtility.getTaxPrice(payment_amt) ;
		String charge = "";
		String credit_Round = "";
		String catTracking_no = ODTUtility_Common.getTrackingNo() ;
		String trx_No = ODTUtility_Common.getTrxNo();
		String trx_Seq = ODTUtility_Common.getTrxSeq();
		String term_Ver = ODTUtility_Common.getRomVer();
		String rTrade_Yn = "=";
		String coupon_Trade_Type = "0";
		String coupon_Acquier_Type = "";
		String term_Res_Code = "00";
		String txt_Direction = "0000";
		String fallback_Trx_Reason = "00";
		
		return CreateUPOSMessage.createUPOSMessage_0071(IUPOSConstant.DEVICE_TYPE_3S, 
				nozzle_no, 
				PumpMessageFormat.getPrintFormatCardNumber(creditCard_no, true).replaceAll("-", ""),
				payment_amt,
				UPOSUtil.getPOSIP(),
				UPOSUtil.getPOSPort(),
				UPOSUtil.getPosSaleDate(), 
				null, 
				//�������濡 ���� �׸� �߰� (2015.11.17)
				creditCardReading_type ,
				chipData,
				certification_id ,
				signImage_Info ,
				signImage_Data ,
				term_id ,
				store_cd ,
				creditCard_no,
				creditPassCode ,
				selfPayment_type ,
				payment_tax ,
				charge ,
				credit_Round ,
				catTracking_no,
				trx_No ,
				trx_Seq ,
				term_Ver ,
				rTrade_Yn ,
				coupon_Trade_Type ,
				coupon_Acquier_Type ,
				term_Res_Code ,
				txt_Direction ,
				fallback_Trx_Reason) ;	
	}
	/**
     * ST ������ �Ҹ��� ODT �� ���� ���� ���� �ܻ� �ŷ�ó�� ���� ������ ���� 0082 (�ܻ� �ŷ� ���� ����) �� 
     * ���� POS �� �����ϵ��� �Ѵ�.
     * 
     * @param dwPumpM			: POS Protocol DW ����
     * @param PB_ST_TrInfo		: �Ҹ��� ODT �� ������ ST ����
	 * @param khproc_no			: KH ó����ȣ
     * @return
     */
	public static ArrayList<UPOSMessage> createUPOSMessage(String nozzle_no, POS_DW dwPumpM, PB_ST_TrInfo trInfo, String khproc_no) {
		LogUtility.getPumpMLogger().info("[Pump M] Create 0082 UPOSMessage for SoMo Self") ;
		
		if (dwPumpM != null) 
			LogUtility.getPumpMLogger().info(dwPumpM.toString());
		if (trInfo != null) trInfo.print("") ;
		
		ArrayList<UPOSMessage> uPosMsgArray = new ArrayList<UPOSMessage>() ;
		UPOSMessage uPosMsg = null ;
		
		try {
			// TR ������ �ʵ�
			String tr_liter 		= PumpMUtil.convertTotalLiterFromPumpTOPOS(trInfo.getLiter());		// �Ǹŷ�
			String tr_price 		= trInfo.getPrice();																										// �Ǹ� �ݾ�
			String tr_basePrice 	= PumpMUtil.convertBasePriceFromPumpToPOS(trInfo.getBasePrice()); 	// �Ǹ� �ܰ� 
			
			// �Ʒ� �������� �ܻ����� ���� ������ ������ ä�� �ʿ䰡 ���� ���� ����. ���� Default ���� �����.
			String bonRSCard_no = "" ;
			String bonRSCard_authNo = "" ;
			String trdate_bonRSCard = "" ;
			String bonRSCard_ID = "" ;
			String bonRSCRSt_nm  = "" ;
			String gs_point1 = "" ;
			String gs_point2 = "" ;
			String gs_point3 = "" ;
			String gs_point4 = "" ;
			String local_point = "" ;
			String local_occurPoint = "" ;
			String loyaltyReqCode = "" ;
			String title_msg = "" ;										
			UPOSMessage_CampInfo camp_info = null ;
			String receipt_type = "" ;
			String loyality_id = "" ;
			String taxFreeCust_type = "" ;
			String supply_type = "" ;
			String deal_type = "" ;
			String loan_date = "" ;
			// �ǹ� ���� ���� - ��
			
			String emp_no = "" ;									// ������ ID (���� ODT �� ����)
			String custCard_No = "" ;								// �ŷ�óī���ȣ
			String ss_crStNum = "" ;								// �ŷ�ó��ȣ
			String ss_carNum = "" ;									// �ŷ�ó������ȣ
			String term_id = "         " ;							// �ܸ��� ��ȣ
			String lastPayment_yn = "0" ;							// ������ ��������
			String led_code = "1" ;									// LED �ڵ�
			String keepDoc_limitDate = "" ;							// ������ ������
			
			if (dwPumpM != null) {
				custCard_No 	= dwPumpM.getCust_card_no() ;		// �ŷ�óī���ȣ
				ss_crStNum 		= dwPumpM.getCust_code() ;			// �ŷ�ó��ȣ
				ss_carNum 		= dwPumpM.getCar_no() ;				// �ŷ�ó������ȣ
			}
			
			UPOSMessage_ItemInfo itemInfo = null ;
			
			itemInfo = UPOSUtil.createCustomerItemInfo(	nozzle_no, 
														khproc_no,
														tr_liter, 
														tr_basePrice, 
														tr_price, 
														tr_price) ;
																					
			String payment_amt = tr_price ;
			
			uPosMsg = CreateUPOSMessage.createUPOSMessage_0082(	IUPOSConstant.DEVICE_TYPE_3S,
																	khproc_no,
																	nozzle_no,
																	emp_no, 
																	itemInfo, 
																	custCard_No, 
																	ss_crStNum, 
																	ss_carNum, 
																	bonRSCard_no,
																	bonRSCard_authNo, 
																	trdate_bonRSCard, 
																	bonRSCard_ID, 
																	bonRSCRSt_nm, 
																	gs_point1, 
																	gs_point2,
																	gs_point3, 
																	gs_point4, 
																	local_point,
																	local_occurPoint,
																	loyaltyReqCode,
																	title_msg,
																	camp_info, 
																	receipt_type,
																	loyality_id, 
																	payment_amt, 
																	term_id, 
																	lastPayment_yn, 
																	led_code,
																	taxFreeCust_type,
																	supply_type,
																	deal_type, 
																	loan_date, 
																	keepDoc_limitDate) ;
			
			uPosMsgArray.add(uPosMsg) ;
			
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		LogUtility.getPumpMLogger().info("[Pump M] Complete creating 0082 UPOSMessage for SoMo Self") ;
		if (uPosMsg != null) uPosMsg.print() ;
		
		return uPosMsgArray ;
	}	
	

	/**
	 * �Ҹ��� ī�� ���� ��û ������ UPOSMessage Class �� ��ȯ�Ѵ�. 
	 * �Ҹ����κ����� ���� ��û ������ HA_WorkingMessage Class �� ���޵Ǹ�, Card_Type �� BonusCard �ʵ�
	 * ���ο� ���� �׿� �����ϴ� UPOSMessage Class �� ��ȯ�Ѵ�.
	 * 	Card_Type													���ʽ� ī�� O		���ʽ� ī�� X
	 * 		1	�ſ�ī�� (+���ʽ�) ���� ��û								0033			0031
	 * 		2	�ſ�ī�� (+���ʽ�) ���� ��� ��û						8033			8031
	 * 		3	���ʽ� ī�� ���� ��û											0003			
	 * 		4	���ʽ� ī�� ���� ��� ��û									8003
	 * 		5	���� ���� ��û (���� �ŷ��� ���)											0011
	 * 		6	���� + ���ʽ� ���� ���� ��û								0013
	 * 		A	GS , GS& ���ʽ� ī�� �̿� ���� ��û										0061
	 * 		B	GS , GS& ���ʽ� ī�� �̿� ��� ��û										8061
	 * 		C	myLG  ���ʽ� ī�� �̿� ���� ��û											0051
	 * 		D	myLG ���ʽ� ī�� �̿� ��� ��û											8051
	 * 		E	BL üũ																0071
	 * 		F	����û ���� ������ ���� ��û								0015
	 * 		G	����û ���� ������ ���� ��� ��û						8015
	 * 		H	��Ÿ (GS, myLG ����) ���ʽ� ī�� �̿� ���� ��û		0033 (�ſ�ī�� ������ ����, �Һΰ����� 61�� ����->VAN �翡�� ���ʽ��̿����� �ν�)
	 * 		I	��Ÿ (GS, myLG ����) ���ʽ� ī�� �̿� ���� ��� ��û	8033 (�ſ�ī�� ������ ����, �Һΰ����� 61�� ����->VAN �翡�� ���ʽ��̿����� �ν�)
	 * 
	 * @param workingMsg
	 * @param khproc_no		: KH ó����ȣ
	 * @return
	 */	
	public static UPOSMessage createUPOSMessageFromWorkingMessage_SoMo(
			WorkingMessage workingMsg, String khproc_no) {
		LogUtility.getPumpMLogger().info("[Pump M] Convert WorkingMessage to UPOSMessage for SoMo Self ODT.");
		
		UPOSMessage uPosMsg = null;
		HA_WorkingMessage haWorkingMsg = (HA_WorkingMessage) workingMsg;

		String nozzleNo 		= haWorkingMsg.getConnectNozzleNo(); // (ODT .)
		String cardType 			= haWorkingMsg.getTrType();
		String card_number 	= haWorkingMsg.getCardNumber();
				
		// ksm 2012.06.07 F879 ����ǽɺκ�  PumpMUtil 726 - [Pump M] BnsCrdNm NoMatch '='
		String bonus_card 				= PumpMUtil.getRealBonusCardNumber(haWorkingMsg.getBonusCard());
		String liter 							= PumpMUtil.convertNumberFormatFromPumpToStandardFormat(haWorkingMsg.getLiter(), 3);
		String basePrice 				= PumpMUtil.convertNumberFormatFromPumpToStandardFormat(haWorkingMsg	.getBasePrice(), 2);
		String payPrice 					= GlobalUtility.getStringValue(haWorkingMsg.getPrice());
		String loyalty_password 	= haWorkingMsg.getPin().trim(); 
		String authType 					= haWorkingMsg.getAuthType();

		String creditAuthNo 	= haWorkingMsg.getCNumber();
		String bonusAuthNo = haWorkingMsg.getBNumber();

		//�������濡 ���� �׸� �߰�(2015.11.17) 
		String creditCardReading_type = IConstant.CreditCardReading_Type_MAGNETIC ;
		String chipData = "" ; 
		String certification_id = "";
		String signImage_Info = "";
		String signImage_Data = "";
		String term_id = ODTUtility_Common.getTermId() ;
		String store_cd = ODTUtility_Common.getStoreCode();
		String encryptCredit_no = "";
		String creditPassCode = "";
		String selfPayment_type = authType ;	// 2016.03.30 WooChul Jung. New Spec moves to selfPayment_type from filler1
		String payment_tax = GlobalUtility.getTaxPrice(payPrice) ;
		String charge = "";
		String credit_Round = "";
		String catTracking_no = ODTUtility_Common.getTrackingNo() ;
		String trx_No = ODTUtility_Common.getTrxNo();
		String trx_Seq = ODTUtility_Common.getTrxSeq();
		String term_Ver = ODTUtility_Common.getRomVer();
		String rTrade_Yn = "=";
		String coupon_Trade_Type = "0";
		String coupon_Acquier_Type = "";
		String term_Res_Code = "00";
		String txt_Direction = "0000";
		String fallback_Trx_Reason = "00";
				
		
		if (card_number != null) {
			card_number.trim();
		}

		if (cardType == null) {
			LogUtility.getPumpMLogger().warn("[Pump M] card Type is null.");
		} else if ("1".equals(cardType)) {
			
			String maskingCardNo = PumpMessageFormat.getPrintFormatCardNumber(card_number, true).replaceAll("-", "") ;
			
			// (+)
			boolean rlt = GlobalUtility.isNullOrEmptyString(bonus_card);
			if (rlt) {
				LogUtility.getPumpMLogger().info("[Pump M] Credit Request -> UPOSMessage(0031)");
				
				UPOSMessage_ItemInfo itemInfo = UPOSUtil.getUPOSMessage_ItemInfo(nozzleNo, khproc_no, liter, basePrice, payPrice);
				
				uPosMsg = CreateUPOSMessage.createUPOSMessage_0031(	IUPOSConstant.DEVICE_TYPE_3S, 
														khproc_no, 
														nozzleNo,
														null, 
														itemInfo, 
														maskingCardNo, 
														null, 
														payPrice, 
														UPOSUtil.getPOSIP(), 
														UPOSUtil.getPOSPort(), 
														UPOSUtil.getPosSaleDate(), 
														"0", 
														null, 
														null, 
														null,
														null,
														null, 
														null, 
														//�������濡 ���� �׸� �߰� (2015.11.17)
														creditCardReading_type ,
														chipData,
														certification_id ,
														signImage_Info ,
														signImage_Data ,
														term_id ,
														store_cd ,
														card_number,
														creditPassCode ,
														selfPayment_type ,
														payment_tax ,
														charge ,
														credit_Round ,
														catTracking_no,
														trx_No ,
														trx_Seq ,
														term_Ver ,
														rTrade_Yn ,
														coupon_Trade_Type ,
														coupon_Acquier_Type ,
														term_Res_Code ,
														txt_Direction ,
														fallback_Trx_Reason) ;	
			} else {
				LogUtility.getPumpMLogger().info(
						"[Pump M] Credit+Bonus Request -> UPOSMessage(0033)");
				UPOSMessage_ItemInfo itemInfo = UPOSUtil.getUPOSMessage_ItemInfo(nozzleNo, khproc_no, liter, basePrice, payPrice);
				uPosMsg = CreateUPOSMessage.createUPOSMessage_0033(	IUPOSConstant.DEVICE_TYPE_3S, 
														khproc_no, 
														nozzleNo,
														null, 
														itemInfo, 
														maskingCardNo, 
														bonus_card, 
														null,
														payPrice, 
														"", 
														"", 
														"", 
														"", 
														UPOSUtil.getPOSIP(), 
														UPOSUtil.getPOSPort(), 
														UPOSUtil.getPosSaleDate(), 
														"0",
														null, 
														null, 
														null, 
														null, 
														null, 
														null, 
														//�������濡 ���� �׸� �߰� (2015.11.17)
														creditCardReading_type ,
														chipData ,
														certification_id ,
														signImage_Info ,
														signImage_Data ,
														term_id ,
														store_cd ,
														card_number,
														creditPassCode ,
														selfPayment_type ,
														payment_tax ,
														charge ,
														credit_Round ,
														catTracking_no,
														trx_No ,
														trx_Seq ,
														term_Ver ,
														rTrade_Yn ,
														coupon_Trade_Type ,
														coupon_Acquier_Type ,
														term_Res_Code ,
														txt_Direction ,
														fallback_Trx_Reason,
														"1") ;
				
				/**
				 * 2016.03.30 WooChul Jung.
				 * 	Previously, the double point accumulation for bonus_card and card_number is checked in KixxHUB,
				 * 	but this logic is moved to VAN.
				 */
				/*boolean isDiscountCargo = CustUtil.isDiscountCargo(bonus_card, card_number);
				
				if (isDiscountCargo)
					uPosMsg.setFiller2("U");*/
			}
		} else if ("2".equals(cardType)) {
			// (+)
			boolean rlt = GlobalUtility.isNullOrEmptyString(bonus_card);
			UPOSMessage preUPOSMsg = null;
			if (rlt) {
				// 
				LogUtility.getPumpMLogger().info("[Pump M] Credit Cancel Request -> UPOSMessage(8031)");
				
				preUPOSMsg = PumpMODTSaleManager.getPreAcceptedUPOSMessageForCancel(nozzleNo,
																		khproc_no, 
																		IUPOSConstant.MESSAGETYPE_8031,
																		null, 
																		null, 
																		creditAuthNo, 
																		bonusAuthNo);
				String maskingCardNo = preUPOSMsg.getCreditCard_no().replaceAll("-", "") ;
				
				uPosMsg = CreateUPOSMessage.createUPOSMessage_8031(	IUPOSConstant.DEVICE_TYPE_3S, 
																		preUPOSMsg.getPosReceipt_no(), 
																		preUPOSMsg.getNozzle_no(),
																		preUPOSMsg.getItem_info(), 
																		preUPOSMsg.getEmp_no(),
																		maskingCardNo,
																		preUPOSMsg.getTrdate_creditCard(), 
																		"0", 
																		preUPOSMsg.getCredit_auth_no(), 
																		payPrice, 
																		UPOSUtil.getPOSIP(),
																		UPOSUtil.getPOSPort(), 
																		UPOSUtil.getPosSaleDate(),
																		null,
																		null,
																		preUPOSMsg.getCreditCardReading_type() ,
																		preUPOSMsg.getChipData() ,
																		preUPOSMsg.getCertification_id() ,
																		preUPOSMsg.getSignImage_Info() ,
																		preUPOSMsg.getSignImage_Data() ,
																		preUPOSMsg.getTerm_id() ,
																		preUPOSMsg.getStore_cd() ,
																		card_number.getBytes() ,
																		preUPOSMsg.getCreditPassCode() ,
																		selfPayment_type ,
																		preUPOSMsg.getPayment_tax() ,
																		preUPOSMsg.getCharge() ,
																		preUPOSMsg.getCredit_Round() ,
																		preUPOSMsg.getCat_tracking_number(),
																		preUPOSMsg.getTrx_No() ,
																		preUPOSMsg.getTrx_Seq() ,
																		preUPOSMsg.getTerm_Ver() ,
																		preUPOSMsg.getRTrade_Yn() ,
																		preUPOSMsg.getCoupon_Trade_Type() ,
																		preUPOSMsg.getCoupon_Acquier_Type() ,
																		preUPOSMsg.getTerm_Res_Code(),
																		preUPOSMsg.getTxt_Direction(),
																		preUPOSMsg.getFallback_Trx_Reason()) ;
			} else {
				// +
				LogUtility.getPumpMLogger().info("[Pump M] Credit+Bonus Cancel Request -> UPOSMessage(8033)");
				
				preUPOSMsg = PumpMODTSaleManager	.getPreAcceptedUPOSMessageForCancel(	nozzleNo,
																		khproc_no, 
																		IUPOSConstant.MESSAGETYPE_8033,
																		null, 
																		bonus_card, 
																		creditAuthNo,
																		bonusAuthNo);
				
				String maskingCardNo = preUPOSMsg.getCreditCard_no().replaceAll("-", "") ;
				
				uPosMsg = CreateUPOSMessage.createUPOSMessage_8033(	IUPOSConstant.DEVICE_TYPE_3S, 
																		preUPOSMsg.getPosReceipt_no(), 
																		preUPOSMsg.getNozzle_no(),
																		preUPOSMsg.getItem_info(), 
																		preUPOSMsg.getEmp_no(),
																		maskingCardNo, 
																		preUPOSMsg.getTrdate_creditCard(), 
																		preUPOSMsg.getCredit_auth_no(), 
																		preUPOSMsg.getBonRSCard_no(), 
																		preUPOSMsg.getTrdate_bonRSCard(), 
																		preUPOSMsg.getBonRSCard_ID(), 
																		preUPOSMsg.getBonRSCard_authNo(), 
																		payPrice, 
																		UPOSUtil.getPOSIP(),
																		UPOSUtil.getPOSPort(), 
																		UPOSUtil.getPosSaleDate(), 
																		null,
																		null,
																		preUPOSMsg.getCreditCardReading_type() ,
																		preUPOSMsg.getChipData() ,
																		preUPOSMsg.getCertification_id() ,
																		preUPOSMsg.getSignImage_Info() ,
																		preUPOSMsg.getSignImage_Data() ,
																		preUPOSMsg.getTerm_id() ,
																		preUPOSMsg.getStore_cd() ,
																		card_number.getBytes() ,
																		preUPOSMsg.getCreditPassCode() ,
																		selfPayment_type ,
																		preUPOSMsg.getPayment_tax() ,
																		preUPOSMsg.getCharge() ,
																		preUPOSMsg.getCredit_Round() ,
																		preUPOSMsg.getCat_tracking_number(),
																		preUPOSMsg.getTrx_No() ,
																		preUPOSMsg.getTrx_Seq() ,
																		preUPOSMsg.getTerm_Ver() ,
																		preUPOSMsg.getRTrade_Yn() ,
																		preUPOSMsg.getCoupon_Trade_Type() ,
																		preUPOSMsg.getCoupon_Acquier_Type() ,
																		preUPOSMsg.getTerm_Res_Code(),
																		preUPOSMsg.getTxt_Direction(),
																		preUPOSMsg.getFallback_Trx_Reason()) ;
			}
		} else if ("3".equals(cardType)) {
			// 
			LogUtility.getPumpMLogger().info("[Pump M] Bonus Accumulation Request -> UPOSMessage(0003)");
			
			UPOSMessage_ItemInfo itemInfo = UPOSUtil.getUPOSMessage_ItemInfo(	nozzleNo, 
																		khproc_no, 
																		liter, 
																		basePrice, 
																		payPrice);
			
			String BonusSave_Type = loyalty_password.trim();
			
			uPosMsg = CreateUPOSMessage.createUPOSMessage_0003(	IUPOSConstant.DEVICE_TYPE_3S, 
																		khproc_no, 
																		nozzleNo, 
																		null,
																		itemInfo, 
																		bonus_card, 
																		null, 
																		payPrice, 
																		null, 
																		null, 
																		UPOSUtil.getPOSIP(), 
																		UPOSUtil.getPOSPort(), 
																		null, 
																		null,
																		null, 
																		UPOSUtil.getPosSaleDate(), 
																		BonusSave_Type, 
																		null,
																		null, 
																		null,
																		null,
																		null,
																		null,
//																		�������濡 ���� �׸� �߰� (2015.11.17)
																		creditCardReading_type ,
																		chipData,
																		certification_id ,
																		signImage_Info ,
																		signImage_Data ,
																		term_id ,
																		store_cd ,
																		"",
																		creditPassCode ,
																		selfPayment_type ,
																		payment_tax ,
																		charge ,
																		credit_Round ,
																		catTracking_no,
																		trx_No ,
																		trx_Seq ,
																		term_Ver ,
																		rTrade_Yn ,
																		coupon_Trade_Type ,
																		coupon_Acquier_Type ,
																		term_Res_Code ,
																		txt_Direction ,
																		fallback_Trx_Reason	) ;	
		} else if ("4".equals(cardType)) {
			// 
			LogUtility.getPumpMLogger().info("[Pump M] Bonus Accumulation Cancel Request -> UPOSMessage(8003)");
			
			UPOSMessage preUPOSMsg = null;
			preUPOSMsg = PumpMODTSaleManager	.getPreAcceptedUPOSMessageForCancel(	nozzleNo, 
																	khproc_no,
																	IUPOSConstant.MESSAGETYPE_8003, 
																	null, 
																	bonus_card,
																	creditAuthNo, 
																	bonusAuthNo);

			uPosMsg = CreateUPOSMessage.createUPOSMessage_8003(	IUPOSConstant.DEVICE_TYPE_3S,
																	preUPOSMsg.getPosReceipt_no(), 
																	preUPOSMsg.getNozzle_no(), 
																	preUPOSMsg.getItem_info(),
																	preUPOSMsg.getEmp_no(), 
																	preUPOSMsg.getBonRSCard_no(), 
																	preUPOSMsg.getBonRSCard_authNo(), 
																	preUPOSMsg.getTrdate_bonRSCard(), 
																	preUPOSMsg.getBonRSCard_ID(), 
																	preUPOSMsg.getBonRSCRSt_nm(), 
																	payPrice, 
																	UPOSUtil.getPOSIP(),
																	UPOSUtil.getPOSPort(), 
																	UPOSUtil.getPosSaleDate(),
																	preUPOSMsg.getUnitPrint_yn(), 
																	preUPOSMsg.getCarNoPrint_yn(), 
																	// 2016.03.30 WooChul Jung. Add field.
																	preUPOSMsg.getCreditCardReading_type() ,
																	preUPOSMsg.getChipData() ,
																	preUPOSMsg.getCertification_id() ,
																	preUPOSMsg.getSignImage_Info() ,
																	preUPOSMsg.getSignImage_Data() ,
																	preUPOSMsg.getTerm_id() ,
																	preUPOSMsg.getStore_cd() ,
																	card_number.getBytes() ,
																	preUPOSMsg.getCreditPassCode() ,
																	selfPayment_type ,
																	preUPOSMsg.getPayment_tax() ,
																	preUPOSMsg.getCharge() ,
																	preUPOSMsg.getCredit_Round() ,
																	preUPOSMsg.getCat_tracking_number(),
																	preUPOSMsg.getTrx_No() ,
																	preUPOSMsg.getTrx_Seq() ,
																	preUPOSMsg.getTerm_Ver() ,
																	preUPOSMsg.getRTrade_Yn() ,
																	preUPOSMsg.getCoupon_Trade_Type() ,
																	preUPOSMsg.getCoupon_Acquier_Type() ,
																	preUPOSMsg.getTerm_Res_Code(),
																	preUPOSMsg.getTxt_Direction(),
																	preUPOSMsg.getFallback_Trx_Reason()) ;
		// ���ݰ����� ���
		} else if ("5".equals(cardType)) { 
			// 

		} else if ("6".equals(cardType)) {
			// +
			LogUtility.getPumpMLogger().info(	"[Pump M] Money+Bonus Request -> UPOSMessage(0013)" );
			
			UPOSMessage_ItemInfo itemInfo = UPOSUtil.getUPOSMessage_ItemInfo(	nozzleNo, 
																	khproc_no, 
																	liter, 
																	basePrice, 
																	payPrice);
			
			uPosMsg = CreateUPOSMessage.createUPOSMessage_0013(	IUPOSConstant.DEVICE_TYPE_3S, 
																	khproc_no, 
																	nozzleNo, 
																	null,
																	itemInfo, 
																	bonus_card, 
																	null, 
																	payPrice, 
																	loyalty_password,
																	null, 
																	UPOSUtil.getPOSIP(), 
																	UPOSUtil.getPOSPort(), 
																	null,
																	null, 
																	null, 
																	UPOSUtil.getPosSaleDate(), 
																	null, 
																	null, 
																	null,
																	null, 
																	null, 
																	null,
																	//�������濡 ���� �׸� �߰� (2015.11.17)
																	creditCardReading_type ,
																	chipData ,
																	certification_id ,
																	signImage_Info ,
																	signImage_Data ,
																	term_id ,
																	store_cd ,
																	card_number,
																	creditPassCode ,
																	selfPayment_type ,
																	payment_tax ,
																	charge ,
																	credit_Round ,
																	catTracking_no,
																	trx_No ,
																	trx_Seq ,
																	term_Ver ,
																	rTrade_Yn ,
																	coupon_Trade_Type ,
																	coupon_Acquier_Type ,
																	term_Res_Code ,
																	txt_Direction ,
																	fallback_Trx_Reason) ;

		} else if ("A".equals(cardType)) {
			// GS
			LogUtility.getPumpMLogger().info("[Pump M] GS Bonus Card Request -> UPOSMessage(0061)");
			
			UPOSMessage_ItemInfo itemInfo = UPOSUtil.getUPOSMessage_ItemInfo(	nozzleNo, 
																	khproc_no, 
																	liter, 
																	basePrice, 
																	payPrice);
			
			uPosMsg = CreateUPOSMessage.createUPOSMessage_0061(	IUPOSConstant.DEVICE_TYPE_3S, 
																	khproc_no, 
																	nozzleNo, 
																	null,
																	itemInfo, 
																	bonus_card, 
																	null, 
																	payPrice, 
																	ODTUtility_Common.getChangeLoyaltyPassword(loyalty_password),
																	UPOSUtil.getPOSIP(), 
																	UPOSUtil.getPOSPort(), 
																	UPOSUtil.getPosSaleDate(), 
																	null,
																	null,
																	null,
																	null,
																	null,
																	null, 
																	//	�������濡 ���� �׸� �߰� (2015.11.17)
																	creditCardReading_type ,
																	chipData ,
																	certification_id ,
																	signImage_Info ,
																	signImage_Data ,
																	term_id ,
																	store_cd ,
																	"",
																	"" ,
																	selfPayment_type ,
																	payment_tax ,
																	charge ,
																	credit_Round ,
																	catTracking_no,
																	trx_No ,
																	trx_Seq ,
																	term_Ver ,
																	rTrade_Yn ,
																	coupon_Trade_Type ,
																	coupon_Acquier_Type ,
																	term_Res_Code ,
																	txt_Direction ,
																	fallback_Trx_Reason) ;
		} else if ("B".equals(cardType)) {
			// GS
			LogUtility.getPumpMLogger().info("[Pump M] GS BonusCard Cancel Request -> UPOSMessage(8061)");
			//���ʽ� ������ ��� ���ι�ȣ�� 'creditAuthNo' ���⿡�� ���� �־��ֱ� ������ ���� uPos���� ��ȸ �� �Ȱ��� 
			// 'creditAuthNo' �̰��� ���� �־� ��ȸ�� �ǰ��ؾ� �ȴ�.
			creditAuthNo = bonusAuthNo;
			
			UPOSMessage preUPOSMsg = null;
			preUPOSMsg = PumpMODTSaleManager	.getPreAcceptedUPOSMessageForCancel(	nozzleNo, 
																			khproc_no,
																			IUPOSConstant.MESSAGETYPE_8061, 
																			null, 
																			bonus_card,
																			creditAuthNo, 
																			bonusAuthNo);
			
			uPosMsg = CreateUPOSMessage.createUPOSMessage_8061(	IUPOSConstant.DEVICE_TYPE_3S,
																		preUPOSMsg.getPosReceipt_no(), 
																		preUPOSMsg.getNozzle_no(), 
																		preUPOSMsg.getItem_info(),
																		preUPOSMsg.getEmp_no(), 
																		preUPOSMsg.getBonRSCard_no(), 
																		preUPOSMsg.getBonRSCard_authNo(), 
																		preUPOSMsg.getTrdate_bonRSCard(), 
																		preUPOSMsg.getBonRSCard_ID(), 
																		preUPOSMsg.getBonRSCRSt_nm(), 
																		payPrice, 
																		UPOSUtil.getPOSIP(),
																		UPOSUtil.getPOSPort(), 
																		UPOSUtil.getPosSaleDate(),
																		preUPOSMsg.getUnitPrint_yn(), 
																		preUPOSMsg.getCarNoPrint_yn(), 
//																		 2016.03.30 WooChul Jung. Add field.
																		preUPOSMsg.getCreditCardReading_type() ,
																		preUPOSMsg.getChipData() ,
																		preUPOSMsg.getCertification_id() ,
																		preUPOSMsg.getSignImage_Info() ,
																		preUPOSMsg.getSignImage_Data() ,
																		preUPOSMsg.getTerm_id() ,
																		preUPOSMsg.getStore_cd() ,
																		card_number.getBytes() ,
																		preUPOSMsg.getCreditPassCode() ,
																		selfPayment_type ,
																		preUPOSMsg.getPayment_tax() ,
																		preUPOSMsg.getCharge() ,
																		preUPOSMsg.getCredit_Round() ,
																		preUPOSMsg.getCat_tracking_number(),
																		preUPOSMsg.getTrx_No() ,
																		preUPOSMsg.getTrx_Seq() ,
																		preUPOSMsg.getTerm_Ver() ,
																		preUPOSMsg.getRTrade_Yn() ,
																		preUPOSMsg.getCoupon_Trade_Type() ,
																		preUPOSMsg.getCoupon_Acquier_Type() ,
																		preUPOSMsg.getTerm_Res_Code(),
																		preUPOSMsg.getTxt_Direction(),
																		preUPOSMsg.getFallback_Trx_Reason()) ;
			
			//	2012.07.09 ksm  my���� ����Ʈ ��� ����
			// ������ ������ ���ؿ� ��� CSR ��û ( 2012-07-06 )
		/*} else if ("C".equals(cardType)) {
			// myLG
			LogUtility.getPumpMLogger().info("[Pump M] myLG Card Use Request -> UPOSMessage(0051)");
			
			UPOSMessage_ItemInfo itemInfo = UPOSUtil.getUPOSMessage_ItemInfo(	nozzleNo, 
																																			khproc_no, 
																																			liter, 
																																			basePrice, 
																																			payPrice);
			
			uPosMsg = CreateUPOSMessage.createUPOSMessage_0051(	IUPOSConstant.DEVICE_TYPE_3S, 
																														khproc_no,
																														nozzleNo,
																														PumpMODTSaleManager.getChargingPersonID(nozzleNo),
																														itemInfo,
																														card_number,
																														null, 
																														payPrice, 
																														UPOSUtil.getPOSIP(),
																														UPOSUtil.getPOSPort(),
																														UPOSUtil.getPosSaleDate(), 
																														null, 
																														null,
																														null, 
																														null, 
																														null,
																														null); 
																														
		} else if ("D".equals(cardType)) {
			// myLG
			LogUtility.getPumpMLogger().info("[Pump M] myLG Card Use Cancel Request -> UPOSMessage(8051)");
			
			UPOSMessage preUPOSMsg = null;
			
			preUPOSMsg = PumpMODTSaleManager	.getPreAcceptedUPOSMessageForCancel(	nozzleNo, 
																																							khproc_no,
																																							IUPOSConstant.MESSAGETYPE_8051, 
																																							bonus_card,
																																							null,
																																							bonusAuthNo,
																																							creditAuthNo);
			
			uPosMsg = CreateUPOSMessage.createUPOSMessage_8051(	IUPOSConstant.DEVICE_TYPE_3S,
																														preUPOSMsg.getPosReceipt_no(), 
																														preUPOSMsg.getNozzle_no(),
																														preUPOSMsg.getItem_info(), 
																														preUPOSMsg.getEmp_no(),
																														card_number,
																														preUPOSMsg.getTrdate_creditCard(), 
																														preUPOSMsg.getCredit_month(), 
																														preUPOSMsg.getCredit_auth_no(),
																														payPrice, 
																														UPOSUtil.getPOSIP(), 
																														UPOSUtil.getPOSPort(), 
																														UPOSUtil.getPosSaleDate(), 
																														null,
																														null);*/
		} else if ("E".equals(cardType)) {
			// BL
			LogUtility.getPumpMLogger().info("[Pump M] BL Check Request -> UPOSMessage(0071)");
			
			String maskingCardNo = PumpMessageFormat.getPrintFormatCardNumber(card_number, true).replaceAll("-", "") ;
			uPosMsg = CreateUPOSMessage.createUPOSMessage_0071(	IUPOSConstant.DEVICE_TYPE_3S, 
																	nozzleNo, 
																	maskingCardNo,
																	payPrice,
																	UPOSUtil.getPOSIP(),
																	UPOSUtil.getPOSPort(),
																	UPOSUtil.getPosSaleDate(), 
																	null, 
																	//�������濡 ���� �׸� �߰� (2015.11.17)
																	creditCardReading_type ,
																	chipData,
																	certification_id ,
																	signImage_Info ,
																	signImage_Data ,
																	term_id ,
																	store_cd ,
																	card_number,
																	creditPassCode ,
																	selfPayment_type ,
																	payment_tax ,
																	charge ,
																	credit_Round ,
																	catTracking_no,
																	trx_No ,
																	trx_Seq ,
																	term_Ver ,
																	rTrade_Yn ,
																	coupon_Trade_Type ,
																	coupon_Acquier_Type ,
																	term_Res_Code ,
																	txt_Direction ,
																	fallback_Trx_Reason) ;
		} else if ("F".equals(cardType)) {
			// 
			LogUtility.getPumpMLogger().info("[Pump M] Cash Receipt Request -> UPOSMessage(0015)");
			
			UPOSMessage_ItemInfo itemInfo = UPOSUtil.getUPOSMessage_ItemInfo(	nozzleNo, 
																		khproc_no, 
																		liter,
																		basePrice,
																		payPrice);
				
			String loyality_type = null;
			String certiDest = "01"; // (01:GSC , 02:)
			String certiSrcType = PumpMUtil	.getSubString(loyalty_password, 0, 1); // (0:, 1:)
			String certiNumberType = PumpMUtil.getSubString(loyalty_password,	1, 2); // 
			
			/*	PI2 20160330 twlee ���� AS-IS �ҽ�
			if ( "4".equals(certiNumberType))
				certiNumberType = "9";
			*/
			
			/*
			 * if ("4".equals(loyalty_password)) { // certiDest = "01" ; } else {
			 * certiDest = "02" ; }
			 */
			// (2byte)+{(1byte)+ (1byte)}
			
			// PI2 20160330 twlee ��ִ��� �����Ͽ�  ���ݿ����� ��û �ڵ� ����
			//  ����û�۽���ü(01:GSC, 02:����Ʈ��) + �ŷ��ڱ���(0:�Һ���, 1:�����) + Ȯ���ڱ���(0:�ſ�ī���ȣ 1:�ֹε�Ϲ�ȣ 2:����ڵ�Ϲ�ȣ 3:��Ÿ 9:���ʽ�ī���ȣ)
			if ("1".equals(certiNumberType)) {	// 20160323 twlee PI2  �ֹι�ȣ�� ���ݿ����� ó���� ����
				creditCardReading_type = IConstant.CreditCardReading_Type_Keyin;
			} else if ("2".equals(certiNumberType)) {	// ����� ��ȣ
				creditCardReading_type = IConstant.CreditCardReading_Type_Keyin;
			} else if("3".equals(certiNumberType)) {	// �ڵ��� ��ȣ
				creditCardReading_type = IConstant.CreditCardReading_Type_Keyin;
			} else if ("4".equals(certiNumberType)) {	// ���ʽ� ī���ȣ
				certiNumberType = "9" ;
				creditCardReading_type = IConstant.CreditCardReading_Type_MAGNETIC;
			} 
			
			loyality_type = certiDest + certiSrcType + certiNumberType;
			String maskingCashReceiptNo = PumpMessageFormat.getPrintFormatCardNumberForPI2(card_number, false);
			String cashReceiptNo = ODTUtility_Common.getChangeCashReceiptNumber(card_number);
			uPosMsg = CreateUPOSMessage.createUPOSMessage_0015(	IUPOSConstant.DEVICE_TYPE_3S, 
																		khproc_no, 
																		nozzleNo,
																		null,
																		itemInfo,
																		maskingCashReceiptNo, 
																		null,
																		payPrice,
																		loyality_type,
																		UPOSUtil.getPOSIP(), 
																		UPOSUtil.getPOSPort(),
																		UPOSUtil.getPosSaleDate(),
																		"0", 
																		null,
																		null, 
																		null,
																		null,
																		null, 
																		null, 
																		// �������濡 ���� �׸� �߰� (2015.11.17)
																		creditCardReading_type ,
																		chipData,
																		certification_id ,
																		signImage_Info ,
																		signImage_Data ,
																		term_id ,
																		store_cd ,
																		cashReceiptNo,
																		creditPassCode,
																		selfPayment_type ,
																		payment_tax ,
																		charge ,
																		credit_Round ,
																		catTracking_no,
																		trx_No ,
																		trx_Seq ,
																		term_Ver ,
																		rTrade_Yn ,
																		coupon_Trade_Type ,
																		coupon_Acquier_Type ,
																		term_Res_Code ,
																		txt_Direction ,
																		fallback_Trx_Reason) ;
		} else if ("G".equals(cardType)) {
			// 
			LogUtility.getPumpMLogger().info("[Pump M] Cash Receipt Cancel Request -> UPOSMessage(8015)");
			
			UPOSMessage preUPOSMsg = null;
			
			preUPOSMsg = PumpMODTSaleManager	.getPreAcceptedUPOSMessageForCancel(	nozzleNo, 
																				khproc_no,
																				IUPOSConstant.MESSAGETYPE_8015, 
																				null,
																				null,
																				creditAuthNo, 
																				bonusAuthNo);
			
			String maskingCardNo = preUPOSMsg.getCreditCard_no().replaceAll("-", "") ;
			uPosMsg = CreateUPOSMessage.createUPOSMessage_8015(	IUPOSConstant.DEVICE_TYPE_3S,
																				preUPOSMsg.getPosReceipt_no(), 
																				preUPOSMsg.getNozzle_no(), 
																				preUPOSMsg.getItem_info(),
																				preUPOSMsg.getEmp_no(), 
																				maskingCardNo, 
																				preUPOSMsg.getTrdate_creditCard(), 
																				preUPOSMsg.getCredit_auth_no(), 
																				preUPOSMsg.getPayment_amt(), 
																				preUPOSMsg.getLoyality_type(), 
																				UPOSUtil.getPOSIP(),
																				UPOSUtil.getPOSPort(), 
																				UPOSUtil.getPosSaleDate(),
																				preUPOSMsg.getUnitPrint_yn(), 
																				preUPOSMsg.getCarNoPrint_yn(), 
																				// 2016.03.30 WooChul Jung. add field
																				preUPOSMsg.getCreditCardReading_type() ,
																				null ,
																				preUPOSMsg.getCertification_id() ,
																				preUPOSMsg.getSignImage_Info() ,
																				null ,
																				preUPOSMsg.getTerm_id() ,
																				preUPOSMsg.getStore_cd() ,
																				card_number ,
																				null ,
																				selfPayment_type ,
																				preUPOSMsg.getPayment_tax() ,
																				preUPOSMsg.getCharge() ,
																				preUPOSMsg.getCredit_Round() ,
																				preUPOSMsg.getCat_tracking_number(),
																				preUPOSMsg.getTrx_No() ,
																				preUPOSMsg.getTrx_Seq() ,
																				preUPOSMsg.getTerm_Ver() ,
																				preUPOSMsg.getRTrade_Yn() ,
																				preUPOSMsg.getCoupon_Trade_Type() ,
																				preUPOSMsg.getCoupon_Acquier_Type() ,
																				preUPOSMsg.getTerm_Res_Code(),
																				preUPOSMsg.getTxt_Direction(),
																				preUPOSMsg.getFallback_Trx_Reason()) ;
		} else if ("H".equals(cardType)) {
			// (GS, myLG �̿��� POINT ���)
			LogUtility.getPumpMLogger().info("[Pump M] Other Bonus Card Request -> UPOSMessage(0031)");
			
			UPOSMessage_ItemInfo itemInfo = UPOSUtil.getUPOSMessage_ItemInfo(	nozzleNo, 
																				khproc_no, 
																				liter, 
																				basePrice, 
																				payPrice);
			String maskingCardNo = PumpMessageFormat.getPrintFormatCardNumber(card_number, true).replaceAll("-", "") ;
			
			uPosMsg = CreateUPOSMessage.createUPOSMessage_0031(	IUPOSConstant.DEVICE_TYPE_3S, 
																				khproc_no, 
																				nozzleNo,
																				null,
																				itemInfo,
																				maskingCardNo,
																				null,
																				payPrice, 
																				UPOSUtil.getPOSIP(),
																				UPOSUtil.getPOSPort(),
																				UPOSUtil.getPosSaleDate(),
																				IConstant.OTHER_POINT_USE_CARD_MONTH, 
																				null,
																				null,
																				null,
																				null, 
																				null, 
																				null,
																				//�������濡 ���� �׸� �߰� (2015.11.17)
																				creditCardReading_type ,
																				chipData,
																				certification_id ,
																				signImage_Info ,
																				signImage_Data ,
																				term_id ,
																				store_cd ,
																				card_number,
																				creditPassCode ,
																				selfPayment_type ,
																				payment_tax ,
																				charge ,
																				credit_Round ,
																				catTracking_no,
																				trx_No ,
																				trx_Seq ,
																				term_Ver ,
																				rTrade_Yn ,
																				coupon_Trade_Type ,
																				coupon_Acquier_Type ,
																				term_Res_Code ,
																				txt_Direction ,
																				fallback_Trx_Reason) ;	
		} else if ("I".equals(cardType)) {
			// (GS, myLG �̿��� POINT ���)
			LogUtility.getPumpMLogger().info("[Pump M] Other Bonus Card Cancel Request -> UPOSMessage(8031) " + khproc_no+ " "+ card_number+ " "+ creditAuthNo+ " "+ bonusAuthNo+ " ");

			UPOSMessage preUPOSMsg = null;
			
			preUPOSMsg = PumpMODTSaleManager	.getPreAcceptedUPOSMessageForCancel(	nozzleNo,
																				khproc_no,
																				IUPOSConstant.MESSAGETYPE_8031, 
																				null, 
																				null,
																				creditAuthNo,
																				bonusAuthNo);
			String maskingCardNo = preUPOSMsg.getCreditCard_no().replaceAll("-", "") ;
			
			uPosMsg = CreateUPOSMessage.createUPOSMessage_8031(	IUPOSConstant.DEVICE_TYPE_3S,
																				preUPOSMsg.getPosReceipt_no(), 
																				preUPOSMsg.getNozzle_no(),
																				preUPOSMsg.getItem_info(), 
																				preUPOSMsg.getEmp_no(),
																				maskingCardNo,
																				preUPOSMsg.getTrdate_creditCard(),
																				IConstant.OTHER_POINT_USE_CARD_MONTH, 
																				preUPOSMsg.getCredit_auth_no(), 
																				payPrice,
																				UPOSUtil.getPOSIP(), 
																				UPOSUtil.getPOSPort(), 
																				UPOSUtil.getPosSaleDate(), 
																				null, 
																				null,
																				preUPOSMsg.getCreditCardReading_type() ,
																				preUPOSMsg.getChipData() ,
																				preUPOSMsg.getCertification_id() ,
																				preUPOSMsg.getSignImage_Info() ,
																				preUPOSMsg.getSignImage_Data() ,
																				preUPOSMsg.getTerm_id() ,
																				preUPOSMsg.getStore_cd() ,
																				card_number.getBytes() ,
																				preUPOSMsg.getCreditPassCode() ,
																				selfPayment_type ,
																				preUPOSMsg.getPayment_tax() ,
																				preUPOSMsg.getCharge() ,
																				preUPOSMsg.getCredit_Round() ,
																				preUPOSMsg.getCat_tracking_number(),
																				preUPOSMsg.getTrx_No() ,
																				preUPOSMsg.getTrx_Seq() ,
																				preUPOSMsg.getTerm_Ver() ,
																				preUPOSMsg.getRTrade_Yn() ,
																				preUPOSMsg.getCoupon_Trade_Type() ,
																				preUPOSMsg.getCoupon_Acquier_Type() ,
																				preUPOSMsg.getTerm_Res_Code(),
																				preUPOSMsg.getTxt_Direction(),
																				preUPOSMsg.getFallback_Trx_Reason()) ;
		} else if ("J".equals(cardType)) {
			// (���� M ����Ʈ)
			LogUtility.getPumpMLogger().info(	"[Pump M] Other Bonus Card Request -> UPOSMessage(0031)");
			
			UPOSMessage_ItemInfo itemInfo = UPOSUtil.getUPOSMessage_ItemInfo(	nozzleNo, 
																				khproc_no, 
																				liter,
																				basePrice, 
																				payPrice);
			
			String maskingCardNo = PumpMessageFormat.getPrintFormatCardNumber(card_number, true).replaceAll("-", "") ;
			uPosMsg = CreateUPOSMessage.createUPOSMessage_0031(	IUPOSConstant.DEVICE_TYPE_3S, 
																			khproc_no, 
																			nozzleNo, 
																			null,
																			itemInfo, 
																			maskingCardNo, 
																			null, 
																			payPrice, 
																			UPOSUtil.getPOSIP(),
																			UPOSUtil.getPOSPort(), 
																			UPOSUtil.getPosSaleDate(),
																			IConstant.OTHER_POINT_USE_CARD_MONTH,
																			null,
																			null,
																			null,
																			null,
																			null, 
																			null,
																			//�������濡 ���� �׸� �߰� (2015.11.17)
																			creditCardReading_type ,
																			chipData,
																			certification_id ,
																			signImage_Info ,
																			signImage_Data ,
																			term_id ,
																			store_cd ,
																			card_number,
																			creditPassCode ,
																			selfPayment_type ,
																			payment_tax ,
																			charge ,
																			credit_Round ,
																			catTracking_no,
																			trx_No ,
																			trx_Seq ,
																			term_Ver ,
																			rTrade_Yn ,
																			coupon_Trade_Type ,
																			coupon_Acquier_Type ,
																			term_Res_Code ,
																			txt_Direction ,
																			fallback_Trx_Reason) ;	
		} else if ("K".equals(cardType)) {
			// (���� M ����Ʈ )
			LogUtility.getPumpMLogger().info(	"[Pump M] Other Bonus Card Cancel Request -> UPOSMessage(8031) " + khproc_no+ " "+ card_number+ " "+ creditAuthNo+ " "+ bonusAuthNo+ " ");
			
			UPOSMessage preUPOSMsg = null;
			
			preUPOSMsg = PumpMODTSaleManager	.getPreAcceptedUPOSMessageForCancel(	nozzleNo, 
																			khproc_no,
																			IUPOSConstant.MESSAGETYPE_8031, 
																			null, 
																			null,
																			creditAuthNo, 
																			bonusAuthNo);
			
			String maskingCardNo = preUPOSMsg.getCreditCard_no().replaceAll("-", "") ;
			uPosMsg = CreateUPOSMessage.createUPOSMessage_8031(	IUPOSConstant.DEVICE_TYPE_3S,
																			preUPOSMsg.getPosReceipt_no(), 
																			preUPOSMsg.getNozzle_no(),
																			preUPOSMsg.getItem_info(), 
																			preUPOSMsg.getEmp_no(),
																			maskingCardNo, 
																			preUPOSMsg.getTrdate_creditCard(),
																			IConstant.OTHER_POINT_USE_CARD_MONTH, 
																			preUPOSMsg.getCredit_auth_no(), 
																			payPrice,
																			UPOSUtil.getPOSIP(), 
																			UPOSUtil.getPOSPort(), 
																			UPOSUtil.getPosSaleDate(), 
																			null, 
																			null, 
																			preUPOSMsg.getCreditCardReading_type() ,
																			preUPOSMsg.getChipData() ,
																			preUPOSMsg.getCertification_id() ,
																			preUPOSMsg.getSignImage_Info() ,
																			preUPOSMsg.getSignImage_Data() ,
																			preUPOSMsg.getTerm_id() ,
																			preUPOSMsg.getStore_cd() ,
																			card_number.getBytes() ,
																			preUPOSMsg.getCreditPassCode() ,
																			selfPayment_type ,
																			preUPOSMsg.getPayment_tax() ,
																			preUPOSMsg.getCharge() ,
																			preUPOSMsg.getCredit_Round() ,
																			preUPOSMsg.getCat_tracking_number(),
																			preUPOSMsg.getTrx_No() ,
																			preUPOSMsg.getTrx_Seq() ,
																			preUPOSMsg.getTerm_Ver() ,
																			preUPOSMsg.getRTrade_Yn() ,
																			preUPOSMsg.getCoupon_Trade_Type() ,
																			preUPOSMsg.getCoupon_Acquier_Type() ,
																			preUPOSMsg.getTerm_Res_Code(),
																			preUPOSMsg.getTxt_Direction(),
																			preUPOSMsg.getFallback_Trx_Reason()) ;
		} else if ("M".equals(cardType)) {
			// (GS���� - ��Ÿ����Ʈ ��� ����  ��û )
			LogUtility.getPumpMLogger().info(	"[Pump M] Other Bonus Card Request -> UPOSMessage(0031)");
			
			UPOSMessage_ItemInfo itemInfo = UPOSUtil.getUPOSMessage_ItemInfo(	nozzleNo, 
																			khproc_no,
																			liter, 
																			basePrice,
																			payPrice);
			
			String maskingCardNo = PumpMessageFormat.getPrintFormatCardNumber(card_number, true).replaceAll("-", "") ;
			uPosMsg = CreateUPOSMessage.createUPOSMessage_0031( 	IUPOSConstant.DEVICE_TYPE_3S, 
																			khproc_no,
																			nozzleNo,
																			null,
																			itemInfo,
																			maskingCardNo,
																			null, 
																			payPrice, 
																			UPOSUtil.getPOSIP(),
																			UPOSUtil.getPOSPort(), 
																			UPOSUtil.getPosSaleDate(),
																			IConstant.OTHER_POINT_USE_CARD_MONTH,
																			null, 
																			null,
																			null,
																			null,
																			null,
																			null,
																			//�������濡 ���� �׸� �߰� (2015.11.17)
																			creditCardReading_type ,
																			chipData,
																			certification_id ,
																			signImage_Info ,
																			signImage_Data ,
																			term_id ,
																			store_cd ,
																			card_number,
																			creditPassCode ,
																			selfPayment_type ,
																			payment_tax ,
																			charge ,
																			credit_Round ,
																			catTracking_no,
																			trx_No ,
																			trx_Seq ,
																			term_Ver ,
																			rTrade_Yn ,
																			coupon_Trade_Type ,
																			coupon_Acquier_Type ,
																			term_Res_Code ,
																			txt_Direction ,
																			fallback_Trx_Reason) ;
		} else if ("N".equals(cardType)) {
			// (GS���� - ��Ÿ����Ʈ ��� ��� ��û )
			LogUtility.getPumpMLogger().info(	"[Pump M] Other Bonus Card Cancel Request -> UPOSMessage(8031) " + khproc_no+ " "+ card_number+ " "+ creditAuthNo+ " "+ bonusAuthNo+ " ");
			
			UPOSMessage preUPOSMsg = null;
			
			preUPOSMsg = PumpMODTSaleManager	.getPreAcceptedUPOSMessageForCancel(	nozzleNo, 
																				khproc_no,
																				IUPOSConstant.MESSAGETYPE_8031,
																				null,
																				null,
																				creditAuthNo,
																				bonusAuthNo);
			String maskingCardNo = preUPOSMsg.getCreditCard_no().replaceAll("-", "") ;
			uPosMsg = CreateUPOSMessage.createUPOSMessage_8031(	IUPOSConstant.DEVICE_TYPE_3S,
																				preUPOSMsg.getPosReceipt_no(), 
																				preUPOSMsg.getNozzle_no(),
																				preUPOSMsg.getItem_info(), 
																				preUPOSMsg.getEmp_no(),
																				maskingCardNo, 
																				preUPOSMsg.getTrdate_creditCard(),
																				IConstant.OTHER_POINT_USE_CARD_MONTH, 
																				preUPOSMsg.getCredit_auth_no(), 
																				payPrice,
																				UPOSUtil.getPOSIP(), 
																				UPOSUtil.getPOSPort(), 
																				UPOSUtil.getPosSaleDate(), 
																				null, 
																				null,
																				preUPOSMsg.getCreditCardReading_type() ,
																				preUPOSMsg.getChipData() ,
																				preUPOSMsg.getCertification_id() ,
																				preUPOSMsg.getSignImage_Info() ,
																				preUPOSMsg.getSignImage_Data() ,
																				preUPOSMsg.getTerm_id() ,
																				preUPOSMsg.getStore_cd() ,
																				card_number.getBytes() ,
																				preUPOSMsg.getCreditPassCode() ,
																				selfPayment_type ,
																				preUPOSMsg.getPayment_tax() ,
																				preUPOSMsg.getCharge() ,
																				preUPOSMsg.getCredit_Round() ,
																				preUPOSMsg.getCat_tracking_number(),
																				preUPOSMsg.getTrx_No() ,
																				preUPOSMsg.getTrx_Seq() ,
																				preUPOSMsg.getTerm_Ver() ,
																				preUPOSMsg.getRTrade_Yn() ,
																				preUPOSMsg.getCoupon_Trade_Type() ,
																				preUPOSMsg.getCoupon_Acquier_Type() ,
																				preUPOSMsg.getTerm_Res_Code(),
																				preUPOSMsg.getTxt_Direction(),
																				preUPOSMsg.getFallback_Trx_Reason()) ;
		} else {
			LogUtility.getPumpMLogger().warn("[Pump M] Strange CardType = " + cardType);
		}

		/**
		 * [2008.12.14] , , , 
		 * . , 
		 * Pump M . POS , .
		 */
		//uPosMsg.setFiller1(authType);
		/**
		 * 2016.03.31 WooChul Jung.
		 * 	filler1 moves to selfpayment_type on new spec.
		 */
		uPosMsg.setSelfPayment_type(selfPayment_type) ;


		return uPosMsg;
	}

	/**
	 * ���� ��û�� ���� ���� ���� (UPOSMessage Object) �� �޾Ƽ� �̸� �Ҹ� ���� ODT Spec �� �´� HC_WorkingMessage Class
	 * �� ��ȯ�Ѵ�.
	 * �Ʒ� ������ UPOSMessage Object �� Message Type �� ���� ���� �����̴�.
	 * 														trType
	 * 		0034		�ſ�ī�� + ���ʽ� ���� ���� ����			1			
	 * 		0032		�ſ� ī�� ���� ����					1	or H (�Һΰ����� 61�� ���)
	 * 		8034		�ſ�ī�� + ���ʽ� ���� ��� ���� ����		2
	 * 		8032		�ſ� ī�� ��� ���� ����				2	or I (�Һΰ����� 61�� ���)
	 * 		0004		���ʽ� ī�� ���� ���� ����				3
	 * 		8004		���ʽ� ī�� ���� ��� ���� ����			4
	 * 		0012		���� ���� ����							5
	 * 		0014		���� + ���ʽ� ���� ���� ����				6
	 * 		0062		GS, GS& ���ʽ� ī�� �̿� ���� ����		A
	 * 		8062		GS, GS& ���ʽ� ī�� �̿� ��� ���� ����	B
	 * 		0052		myLG ���ʽ� ī�� �̿� ���� ���� ����		C
	 * 		8052		myLG ���ʽ� ī�� �̿� ��� ���� ����		D
	 * 		0072		BL üũ ���� ����						E
	 * 		0016		����û ���� ������ ���� ���� ����			F
	 * 		8016		����û ���� ������ ���� ��� ���� ����	G
	 * 
	 * [2008.11.27] ���� by ������ �����.
	 * ����Ʈ�� ������ ����Բ��� ������ ���� ������ ��û�Ͽ����ϴ�.
	 * 	1. ����
	 * 		GS ����Ʈ �̿� -> GS ���ʽ� ����
	 * 		��Ÿ ����Ʈ �̿� (myLG ����) -> VAN ��� �Ϲ� �ſ�ī�� ����ó�� ������ �����ϵ�, �Һΰ����� '61' �� ����
	 * 	2. ���� ����
	 * 		GS ����Ʈ �̿� -> GS ���ʽ� ����
	 * 		myLG ����Ʈ �̿� -> VAN ��� ����Ʈ ��� L3 ������ �����ϵ��� �Ѵ�.
	 * 		��Ÿ ����Ʈ �̿� (GS , myLG ����) -> VAN ��� �Ϲ� �ſ�ī�� ����ó�� ������ �����ϵ�, �Һΰ����� '61' �� ����
	 * 
	 * [2012.06.01] �����߰� - ���ϼ��� ��û���� ���� �������ڵ� ���
	 * 
	 * @param uPosMsg
	 * @return
	 */
	public static ArrayList<WorkingMessage> createWorkingMessageFromUPOSMessage_SoMo(UPOSMessage uPosMsg) {
		LogUtility.getPumpMLogger().info("[Pump M] Convert UPOSMessage[" + uPosMsg.getMessageType() + "] to WorkingMessage for SoMo.") ;
		
		ArrayList<WorkingMessage> workMsgArray = new ArrayList<WorkingMessage>() ;
		String trType = null ;
		
		// �Ҹ� ������ ��� ���� ������ �����ϴ�. (�ſ� ���� ���� ���ʽ� ������ �� �� ����.)
		uPosMsg.setLastPayment_yn("0") ;
		int messageType = Integer.parseInt(uPosMsg.getMessageType()) ;
		
		/*//
		String mode = "" ;
		switch (messageType) {
			case IUPOSConstant.MESSAGETYPE_INT_0034 :
			case IUPOSConstant.MESSAGETYPE_INT_0032 :
			case IUPOSConstant.MESSAGETYPE_INT_0004 :
			case IUPOSConstant.MESSAGETYPE_INT_0012 :
			case IUPOSConstant.MESSAGETYPE_INT_0014 :
			case IUPOSConstant.MESSAGETYPE_INT_0062 :
			case IUPOSConstant.MESSAGETYPE_INT_0052 :
			case IUPOSConstant.MESSAGETYPE_INT_0072 :
			case IUPOSConstant.MESSAGETYPE_INT_0016 :
			{
				mode = uPosMsg.getLed_code() ;
				break;
			}
			case IUPOSConstant.MESSAGETYPE_INT_8034 :
			case IUPOSConstant.MESSAGETYPE_INT_8032 :
			case IUPOSConstant.MESSAGETYPE_INT_8004 :
			case IUPOSConstant.MESSAGETYPE_INT_8062 :
			case IUPOSConstant.MESSAGETYPE_INT_8052 :
			case IUPOSConstant.MESSAGETYPE_INT_8016 :
			{
				String ledCode = uPosMsg.getLed_code() ;
				if (ledCode.equals("1")) {
					mode = "4" ;
				} else if (ledCode.equals("2")) {
					mode = "5" ;
				} else if (ledCode.equals("3")) {
					mode = "6" ;
				}
				break;
			}
				
		}
		if (mode.equals("2") || mode.equals("3") || mode.equals("5") || mode.equals("6"))
		{
			//PumpMTransactionManager.getInstance().setNozzleState(uPosMsg.getNozzle_no(), IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED) ;
		}*/
		
		switch (messageType) {
			case IUPOSConstant.MESSAGETYPE_INT_0034 : { 
				// �ſ�ī�� + ���ʽ� ���� ����
				LogUtility.getPumpMLogger().info("[Pump M] Credit+Bonus Response -> WorkingMessage") ;
				trType = "1" ;
				HC_WorkingMessage hcWorkingMsg = getHC_WorkingMessage_SoMo(uPosMsg, trType, true) ;
				workMsgArray.add(hcWorkingMsg) ;
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_0032 : { 
				// �ſ�ī�� ���� ���� (or ����Ʈ ��� ���� ����) -> �Һΰ����� 61�� ��� ����Ʈ ���(GS , myLG ����)
				LogUtility.getPumpMLogger().info("[Pump M] Credit Response -> WorkingMessage") ;
				if (UPOSUtil.isPointUseResponseWithoutGSCard_MyLGCard(messageType, uPosMsg)) {
					trType = "H" ;
				} else {
					trType = "1" ;
				}
				HC_WorkingMessage hcWorkingMsg = getHC_WorkingMessage_SoMo(uPosMsg, trType, true) ;
				workMsgArray.add(hcWorkingMsg) ;
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_8034 : { 
				// �ſ�ī�� + ���ʽ� ��� ����
				LogUtility.getPumpMLogger().info("[Pump M] Credit+Bonus Cancel Response -> WorkingMessage") ;
				trType = "2" ;
				HC_WorkingMessage hcWorkingMsg = getHC_WorkingMessage_SoMo(uPosMsg, trType, false) ;
				workMsgArray.add(hcWorkingMsg) ;
				
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_8032 : { 
				// �ſ�ī�� ���� ��� ���� (or ����Ʈ ��� ��� ����) -> �Һΰ����� 61�� ��� ����Ʈ ���(GS , myLG ����)
				LogUtility.getPumpMLogger().info("[Pump M] Credit Cancel Response -> WorkingMessage") ;
				if (UPOSUtil.isPointUseResponseWithoutGSCard_MyLGCard(messageType, uPosMsg)) {
					trType = "I" ;
				} else {
					trType = "2" ;
				}
				HC_WorkingMessage hcWorkingMsg = getHC_WorkingMessage_SoMo(uPosMsg, trType, false) ;
				workMsgArray.add(hcWorkingMsg) ;
				break ;
			}			
			case IUPOSConstant.MESSAGETYPE_INT_0004 : { 
				// ���ʽ� ī�� ���� ����
				LogUtility.getPumpMLogger().info("[Pump M] Bonus Accumulation Response -> WorkingMessage") ;
				trType = "3" ;
				HC_WorkingMessage hcWorkingMsg = getHC_WorkingMessage_SoMo(uPosMsg, trType, true) ;
				workMsgArray.add(hcWorkingMsg) ;
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_8004 : { 
				// ���ʽ� ī�� ���� ��� ����
				LogUtility.getPumpMLogger().info("[Pump M] Bonus Accumulation Cancel Response -> WorkingMessage") ;
				trType = "4" ;
				HC_WorkingMessage hcWorkingMsg = getHC_WorkingMessage_SoMo(uPosMsg, trType, false) ;
				workMsgArray.add(hcWorkingMsg) ;				
				break ;
			}	
			case IUPOSConstant.MESSAGETYPE_INT_0012 : { 
				// ���� ����
				LogUtility.getPumpMLogger().info("[Pump M] Money Response -> WorkingMessage") ;
				trType = "5" ;
				HC_WorkingMessage hcWorkingMsg = getHC_WorkingMessage_SoMo(uPosMsg, trType, true) ;
				workMsgArray.add(hcWorkingMsg) ;				
				break ;
			}	
			case IUPOSConstant.MESSAGETYPE_INT_0014 : { 
				// ���� + ���ʽ� ���� ���� ����
				LogUtility.getPumpMLogger().info("[Pump M] Money+Bonus Response -> WorkingMessage") ;
				trType = "6" ;
				HC_WorkingMessage hcWorkingMsg = getHC_WorkingMessage_SoMo(uPosMsg, trType, true) ;
				workMsgArray.add(hcWorkingMsg) ;
				
				break ;
			}	
			case IUPOSConstant.MESSAGETYPE_INT_0062 : { 
				// GS ���ʽ� ī�� �̿� ����
				LogUtility.getPumpMLogger().info("[Pump M] GS Bonus Card use Response -> WorkingMessage") ;
				trType = "A" ;
				HC_WorkingMessage hcWorkingMsg = getHC_WorkingMessage_SoMo(uPosMsg, trType, true) ;
				workMsgArray.add(hcWorkingMsg) ;	
				break ;
			}			
			case IUPOSConstant.MESSAGETYPE_INT_8062 : { 
				// GS ���ʽ� ī�� �̿� ��� ���� ����
				LogUtility.getPumpMLogger().info("[Pump M] GS Bonus Card use Cancel Response -> WorkingMessage") ;
				trType = "B" ;
				HC_WorkingMessage hcWorkingMsg = getHC_WorkingMessage_SoMo(uPosMsg, trType, false) ;
				workMsgArray.add(hcWorkingMsg) ;
				break ;
			}
			
			/*
			 //	2012.07.09 ksm  my���� ����Ʈ ��� ����
			// ������ ������ ���ؿ� ��� CSR ��û ( 2012-07-06 )
			case IUPOSConstant.MESSAGETYPE_INT_0052 : { 
				// myLG ���ʽ� ī�� �̿� ���� ���� ����
				LogUtility.getPumpMLogger().info("[Pump M] myLG Bonus Card use Response -> WorkingMessage") ;
				trType = "C" ;
				HC_WorkingMessage hcWorkingMsg = getHC_WorkingMessage_SoMo(uPosMsg, trType, true) ;
				workMsgArray.add(hcWorkingMsg) ;	
				break ;
			}				
			case IUPOSConstant.MESSAGETYPE_INT_8052 : { 
				// myLG ���ʽ� ī�� �̿� ��� ���� ����
				LogUtility.getPumpMLogger().info("[Pump M] myLG Bonus Card use Cancel Response -> WorkingMessage") ;
				trType = "D" ;
				HC_WorkingMessage hcWorkingMsg = getHC_WorkingMessage_SoMo(uPosMsg, trType, false) ;
				workMsgArray.add(hcWorkingMsg) ;
				break ;
			} */
			case IUPOSConstant.MESSAGETYPE_INT_0072 : { 
				// BL üũ ���� ����
				LogUtility.getPumpMLogger().info("[Pump M] BL Response -> WorkingMessage") ;
				trType = "E" ;
				HC_WorkingMessage hcWorkingMsg = getHC_WorkingMessage_SoMo(uPosMsg, trType, true) ;
				workMsgArray.add(hcWorkingMsg) ;
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_0016 : { 
				// ����û ���� ������ ���� ���� ����
				LogUtility.getPumpMLogger().info("[Pump M] Money Receipt Response -> WorkingMessage") ;
				trType = "F" ;
				HC_WorkingMessage hcWorkingMsg = getHC_WorkingMessage_SoMo(uPosMsg, trType, true) ;
				workMsgArray.add(hcWorkingMsg) ;
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_8016 : { 
				// ����û ���� ������ ���� ��� ���� ����
				LogUtility.getPumpMLogger().info("[Pump M] Money Receipt Cancel Response -> WorkingMessage") ;
				trType = "G" ;
				HC_WorkingMessage hcWorkingMsg = getHC_WorkingMessage_SoMo(uPosMsg, trType, false) ;
				workMsgArray.add(hcWorkingMsg) ;
				break ;
			}
		}		
		return workMsgArray ;
	}


	/**
	 * UPOSMessage Object �� HC_WorkingMessage Object �� ��ȯ�Ѵ�.
	 * 
	 *
	 * @param uPosMsg : 	UPOSMessage
	 * @param trType		�������� ����	
	 * @param isAccept
	 * 			true : 		���� ���� ����
	 * 			false : 	��� ���� ����
	 * @return
	 * 			HC_WorkingMessage
	 * 

	 */
	private static HC_WorkingMessage getHC_WorkingMessage_SoMo(UPOSMessage uPosMsg , String trType, boolean isAccept) {
		HC_WorkingMessage hcWorkingMsg = null ;
		
		String odtNo = null ;
		String nozzle_no = uPosMsg.getNozzle_no() ;
		String mode = "" ;								// ���� ���� Ÿ��
//		String bonusCardType = "" ;						// ���ʽ�ī�� ����	(x)
		String bonusAuthCode = "" ;						// ���ʽ������ڵ�
		String authInfo = "" ;							// ��������
//		String liter = "" ;								// ���� (4.3)
		String basePrice = "" ;							// �ܰ� (4.2)
		String price = "" ;								// �ݾ�
		String authTime = "" ;							// ���� �ð�
		String authNo = "" ;							// ���� ��ȣ
		String cardNo = "" ;							// ī�� ��ȣ
//		String cardCorpNumber = "" ;					// ī����ȣ (x)
		String cardCorpName = "" ;						// ī��� ��
		String noteCorpCode = "" ;						// ��ǥ ���Ի� �ڵ�
		String noteCorpName = "" ;						// ��ǥ ���Ի� ��
		String noteNumber = "" ;						// ��ǥ ��ȣ
		String bonusCardNumber = "" ;					// ���ʽ� ī�� ��ȣ
		String bonusAuthTime = "" ;						// ���ʽ� ���� �ð�
		String bonusAuthNumber = "" ;					// ���ʽ� ���� ��ȣ
		String dAuthNumber = "" ;						// ���ݿ����� ���� ��ȣ
		String generateScore = "" ;						// �߻�����
		String score = "" ;								// ��������
		String totalScore = "" ;						// �Ѵ�������
		String publicMsg = "" ;							// �޽���
		String vanMsg = "" ;							// ���޽���
		String bnsMsg = "" ;							// ���ʽ��޽���
		String storePoint = "" ;						// ���� ����
		String barCode = "";							// ���� ���ڵ�
		String khprocsNo = "";							// K/H ó����ȣ
		
		if (isAccept) {
			mode = uPosMsg.getLed_code() ;
		} else {
			String ledCode = uPosMsg.getLed_code() ;
			if (ledCode.equals("1")) {
				mode = "4" ;
			} else if (ledCode.equals("2")) {
				mode = "5" ;
			} else if (ledCode.equals("3")) {
				mode = "6" ;
			} 
		}		
		
		// myLG ���ʽ� ����Ʈ ��� ���� / ��� ��� ������ ��� ī�� ��ȣ �� ���� ��ȣ�� �ſ�ī�� ���� ����.
		if (("C".equals(trType)) || ("D".equals(trType))) {
			bonusAuthTime = uPosMsg.getTrdate_creditCard() ;					// GS,GS&�� ������ ī���� ����Ʈ ��� ���� �ð�
			bonusAuthNumber = uPosMsg.getCredit_auth_no() ;						// GS,GS&�� ������ ī���� ����Ʈ ��� ���� ��ȣ
			bonusCardNumber = PumpMUtil.getCardNumberPre16Length(uPosMsg.getCreditCard_no()) ;	// GS,GS&�� ������ ī����  ����Ʈ ��� ī�� ��ȣ
		} else {
			authTime = uPosMsg.getTrdate_creditCard() ;									// ���� �ð�
			authNo = uPosMsg.getCredit_auth_no() ;										// ���� ��ȣ
			// 2016.03.30 WooChul Jung. ��ִ����ý������� ���� ������� ī���ȣ�� ��� "-" �� ���Ե� ��찡 �߻���.
			cardNo = PumpMUtil.getCardNumberPre16Length(uPosMsg.getCreditCard_no().replaceAll("-", "")) ;	// ī�� ��ȣ
			bonusCardNumber = PumpMUtil.getCardNumberPre16Length(uPosMsg.getBonRSCard_no()) ;	// ���ʽ� ī�� ��ȣ
			bonusAuthTime = uPosMsg.getTrdate_bonRSCard() ;					// ���ʽ� ���� �ð�
			bonusAuthNumber = uPosMsg.getBonRSCard_authNo() ;				// ���ʽ� ���� ��ȣ
		}
		
		// BL üũ�� ���
		if ("E".equals(trType)) {
			odtNo = PumpMUtil.getODTNumberFromNozzleNo(nozzle_no) ;		
			POSHeader posMsg = PumpMODTSaleManager.getCustPOSPumpM(nozzle_no);
			
			POS_DW dwMsg = (POS_DW)posMsg;
			if (dwMsg == null)
				basePrice = UPOSUtil.getBasePriceForPumpA(uPosMsg) ;		// �ܰ� (4.2)
			else
				basePrice = PumpMUtil.convertNumberFormatFromPOSToPump(dwMsg.getBasePrice(),4,2) ;		// �ܰ� (4.2)
						
			cardCorpName 	= uPosMsg.getIssuer_name() ;						// ī��� ��
			noteCorpCode 	= uPosMsg.getAcquier_code() ;						// ��ǥ ���Ի� �ڵ�
			noteCorpName 	= uPosMsg.getAcquier_name() ;						// ��ǥ ���Ի� ��		
			noteNumber 		= getNoteNumber(uPosMsg.getMessageType(), uPosMsg.getCredit_authInfo(), isAccept) ;		// ��ǥ ��ȣ
			publicMsg			= uPosMsg.getTitle_msg() ;								// �޽���
			vanMsg 				= uPosMsg.getVan_msg() ;								// ���޽���
		} else {
			if (("F".equals(trType)) || ("G".equals(trType))) {
				dAuthNumber = authNo ;
			}
	
			odtNo = PumpMUtil.getODTNumberFromNozzleNo(nozzle_no) ;
			bonusAuthCode = uPosMsg.getLoyaltyReqCode() ;				// ���ʽ� ���� �ڵ�
			authInfo = parsingCredit_AuthInfo(uPosMsg.getMessageType(), uPosMsg.getLed_code(), uPosMsg.getCredit_authInfo(),isAccept) ;	// ��������
//			liter = UPOSUtil.getLiterForPumpA(uPosMsg) ;					// ���� (4.3)
			basePrice = UPOSUtil.getBasePriceForPumpA(uPosMsg) ;			// �ܰ� (4.2)
			price = UPOSUtil.getPriceForPumpA(uPosMsg) ;					// �ݾ�
			cardCorpName = uPosMsg.getIssuer_name() ;						// ī��� ��
			noteCorpCode = uPosMsg.getAcquier_code() ;						// ��ǥ ���Ի� �ڵ�
			noteCorpName = uPosMsg.getAcquier_name() ;						// ��ǥ ���Ի� ��		
			noteNumber = getNoteNumber(uPosMsg.getMessageType(), uPosMsg.getCredit_authInfo(), isAccept) ;					// ��ǥ ��ȣ		
			generateScore = uPosMsg.getGs_point1() ;						// �߻�����
			score = uPosMsg.getGs_point2() ;								// ��������
			totalScore = uPosMsg.getGs_point3() ;							// �Ѵ�������
			publicMsg = uPosMsg.getTitle_msg() ;							// �޽���
			vanMsg = uPosMsg.getVan_msg() ;									// ���޽���
			bnsMsg = uPosMsg.getBonRS_msg() ;								// ���ʽ��޽���				
			storePoint = uPosMsg.getLocal_point() ;							// ��������			
			
			String messageType = uPosMsg.getMessageType();
			
			//LogUtility.getPumpMLogger().info("[CarWash BarCode] MessageType : " + messageType);
			
			// ksm 2012.06.01 ���� ���� ���ڵ� ����̸� ���� �ݾ����� ���� ���ڵ� ����
			try{
				// (����) ���� ���ڵ� ��� ����
				String printBarCode = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0440);
				
				
				if("1".equals(printBarCode))
				{
					/*
					// ������� ������ ��� ���ڵ带 ���� ����.
					if( !"8".equals(messageType.substring(0,1)) ){                         //&& "1".equals(mode) ){
						khprocsNo = uPosMsg.getPosReceipt_no() ;							// K/Hó����ȣ
						barCode = getODTBarcodeNumber(price, khprocsNo);					// �������ڵ�
					}else{
						LogUtility.getPumpMLogger().info("[CarWash BarCode] MessageType : " + messageType + ", mode = " + mode);
						//LogUtility.getPumpMLogger().info("[CarWash BarCode] ���� ������ �ƴ� ��� ���ڵ� ���� null ��.");
						barCode = "";
					}
					*/
					khprocsNo = uPosMsg.getPosReceipt_no() ;								// K/Hó����ȣ
					// twsongkis 2015-01-28 ���ο� Barcode Ŭ������ barcode�������� ����
					barCode = Barcode.getBarcodeNumber("3", price, uPosMsg.getNozzle_no(), khprocsNo, messageType, uPosMsg.getLed_code(), null);
				}
			}catch(Exception e){
				LogUtility.getPumpMLogger().error("[BarCode]���� ���ڵ� ��� ���� ��ȸ�� ���� �߻�");
				LogUtility.getPumpMLogger().error(e.getMessage(), e);
			}
			//////////////////////////////////////////////////////////////////////////////////////
		}
		
		if (!uPosMsg.getLed_code().equals("1")) {
			// PI2 20160325 twlee ��ִ������� odtScreenMsg�� �������� �ʾ� display_msg�� ����			
			vanMsg = uPosMsg.getDisplay_msg() ;	
		}
		hcWorkingMsg = new HC_WorkingMessage(	odtNo,
									nozzle_no		,
									trType 				,
									mode				,
									authInfo 			,
									price 				,
									authTime 		,
									authNo 			,
									cardNo 			,
									//				cardCorpNumber 	,
									cardCorpName 		,
									noteCorpCode 		,
									noteCorpName	 	,
									noteNumber 			,
									bonusAuthCode 		,
									bonusCardNumber	,
									bonusAuthTime 		,
									bonusAuthNumber	,
									dAuthNumber 	,
									generateScore 	,
									score 					,
									totalScore 			,
									storePoint 			,
									publicMsg 			,
									vanMsg 				,
									bnsMsg				,
									basePrice			,
									barCode) ;

		return hcWorkingMsg ;
	}
	
	/**
	 * AuthInfo ���� ���� ��ǥ ��ȣ�� �����´�. 
	 * 
	 * @param credit_authInfo		: ���� ������ Auth Info
	 * @param isAccepted			: ���� ���� ����
	 * @return
	 */
	private static String getNoteNumber(String messageType, String credit_authInfo, boolean isAccepted) {
		String noteNumber = "" ;
		try {
			if (!isValidCreditAuthInfo(messageType)) {
				noteNumber = GlobalUtility.appendingSPACEEnd("", 12) ;
			} else {
				String[] strArray = GlobalUtility.splitByteArrayToStringArray(credit_authInfo.getBytes(), DELIMITER_0X1E) ;
				if ((strArray != null) && (strArray.length > 1)) {				
					noteNumber = GlobalUtility.appendingSPACEEnd(strArray[1], 12) ;		
				} else if(strArray.length == 1) {
					// ���� ���� (2009.01.07 �߿��� ����)
					noteNumber = GlobalUtility.appendingSPACEEnd(strArray[0], 12);
				}
			} 
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
			noteNumber = GlobalUtility.appendingSPACEEnd("", 12) ;
		}
		
		if ("".equals(noteNumber)) {
			noteNumber = GlobalUtility.appendingSPACEEnd("", 12) ;
		}
		return noteNumber ;
	}
	
	/**
	 * u-POS ������ credit_authInfo �ʵ�� ��� ���� �������� ���Ǵ� ���� �ƴϴ�.
	 * ���� �ǹ� �ִ� Message Type ���� �����Ѵ�.
	 * 
	 * @param messageType	: Message Type
	 * @return
	 */
	private static boolean isValidCreditAuthInfo(String messageType) {
		boolean rlt = true ;
		try {
			int messageTypeInt = Integer.parseInt(messageType) ;
			switch (messageTypeInt) {
				case IUPOSConstant.MESSAGETYPE_INT_0016 :
				case IUPOSConstant.MESSAGETYPE_INT_0032 : 
				case IUPOSConstant.MESSAGETYPE_INT_0034 : 
				case IUPOSConstant.MESSAGETYPE_INT_0054 :
				case IUPOSConstant.MESSAGETYPE_INT_8016 :
				case IUPOSConstant.MESSAGETYPE_INT_8032 : 
				case IUPOSConstant.MESSAGETYPE_INT_8034 : 
				case IUPOSConstant.MESSAGETYPE_INT_8054 :
				{
					rlt = true ;
					break ;
				}
				default :
					rlt = false ;
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
		}
		return rlt ;
	}

	/**
	 * ���������� ���� ������ �Ҹ��� ODT �� �䱸�ϴ� ���������� ��ȯ.
	 * 
	 * @param messageType		
	 * @param ledCode		
	 * @param credit_authInfo	: ���������� ���� ����
	 * 		���ν�: �ŷ��Ϸù�ȣ(V4byte)+RS+��ǥ��ȣ (V12byte)+RS+ddc�ڵ�(V1byte)+RS+��������ȣ (V15byte)
	 * 		������: ����Ʈ�� �����ڵ�(V2byte)
	 * @param isAccepted		: 
	 * 		true : ���� ��û�� ���� ����
	 * 		false : ���� ��� ��û�� ���� ���� 
	 * @return
	 */
	private static String parsingCredit_AuthInfo(String messageType , String ledCode ,String credit_authInfo, boolean isAccepted) {
		String retValue = "" ;
		try {
			if (!"1".equals(ledCode)) {
				retValue = GlobalUtility.appendingSPACEEnd(credit_authInfo, 35) ;
			} else {
				String[] strArray = GlobalUtility.splitByteArrayToStringArray(credit_authInfo.getBytes(), DELIMITER_0X1E) ;
				
				if ((strArray != null) && (strArray.length > 1)) {
					retValue = GlobalUtility.appendingSPACEEnd(strArray[0], 4) +
						GlobalUtility.appendingSPACEEnd(strArray[1], 12) +
						GlobalUtility.appendingSPACEEnd(strArray[2], 1) +
						GlobalUtility.appendingSPACEEnd(strArray[3], 15) ;				
				} else if(strArray.length == 1) {
					// ���� ���� (2009.01.21 �߿��� ����)
					retValue = strArray[0];
				}
				retValue = GlobalUtility.appendingSPACEEnd(retValue, 35) ;
			} 
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
			retValue = GlobalUtility.appendingSPACEEnd("", 35) ;
		}
		if ((retValue == null) || ("".equals(retValue))) {
			retValue = GlobalUtility.appendingSPACEEnd("", 35) ;
		}
		return retValue ;
	}
	
	/**
	 * 0001 ���� ���� ���θ� �����Ѵ�.
	 * 
	 * @param nozzle_no		: ���� ��ȣ
	 * @param khproc_no		: KH ó����ȣ
	 * @param workMsg			: �Ǹ� �Ϸ� ���� (TR_WorkingMessage)
	 * @return
	 */
	public static boolean shouldSend0001UPOSMessageToSaleM(String nozzle_no, String khproc_no, WorkingMessage workMsg) {
		boolean rlt = false ;
		Vector<PB_ST_TrInfo> stTrInfoVector = null ;
		
		if (workMsg instanceof ST_WorkingMessage) {
			stTrInfoVector = ((ST_WorkingMessage) workMsg).getTrInfoVector();
		}
		
		if (stTrInfoVector == null) {
			LogUtility.getPumpMLogger().info("[Pump M] stTrInfoVector is null, so don't send 0001 to POS") ;
			rlt = false ;
		} else {
			int size = stTrInfoVector.size() ;
			if (size >= 1) {
				String trType = stTrInfoVector.get(0).getTrType() ;
				if ("Z".equals(trType)) {
					// Z : ������������(POS �κ��� Preset �� ���� ����)
					LogUtility.getPumpMLogger().info("[Pump M] This Pumping was caused by Preset from POS. Don't send 0001 to POS.") ;
					rlt = false ;
/*				} else if (containFailContent(workMsg)) {
					// ����� Ȥ�� ����� �� ������ ������ �ִ� ���
					LogUtility.getPumpMLogger().info("[Pump M] There is fail content. Don't send 0001 to POS.") ;
					rlt = false ;*/
				} else {
					rlt = true ;
				}
			} else {
				LogUtility.getPumpMLogger().warn("[Pump M] There is no pay content. Don't send 0001 to POS.") ;
				rlt = false ;
			}
		}
		return rlt ;
	}

    /**
	 * UPOSMessage ���� ������ Sale M ���� �������� �Ǵ��Ѵ�.
	 * 
	 * @param uPosMsg	: UPOSMessage ���� ����
	 * @return
	 */
    public static boolean shouldSendToSaleM(UPOSMessage uPosMsg) {    	
    	boolean rlt = true ;
    	
    	// 2013.07.18 ksm ���ݿ����� ���ν����� ��� POS�� ���� ����.
    	if("2".equals(uPosMsg.getLed_code()) && "0016".equals(uPosMsg.getMessageType()) ){
    		LogUtility.getPumpMLogger().info("[���ݿ����� ���ν���] POS�� ���۾���. LED_CODE=2, MESSAGE_TYPE=0016" ) ;
    		rlt = false;
    	}

    	return rlt ;	
    }

	/**
	 * �Ҹ���ODT �� �ŷ�ó�� ���� �ڵ�� ������ ���� �ڵ尡 ��ġ�� ��츸 ������ ����Ѵ�.
	 * ���� ���� �ŷ�ó�� ���� �������� �ƴϸ� �Ϲݰ��� �������� ������ �ʿ䰡 �ְ�, �̿� ���� ������
	 * ���� �ŷ�ó ���� ������ ��ǰ �ڵ�� ������ ��ǰ �ڵ带 ���Ͽ� �����Ѵ�.
	 * POS �� ���� ���� ������ ���۽�, �ŷ�ó�� ���� ������ ���� �ŷ�ó ������ �� �����ϵ��� Spec �� ���ǵǾ� �ִ�.
	 * 
	 * @param deviceID
	 * @param pumpM_DW
	 * @return
	 */
	public static boolean validatePOSPumpM_DW(String deviceID, POS_DW pumpM_DW) {
		boolean rlt = false ;
		try {
			String dw_goodCode 	= pumpM_DW.getGoods_code() ;
			String nzGoodCode 		= T_NZ_NOZZLEHandler.getHandler().getGoods_codeBYNozzle_no(deviceID) ;
			
			if (nzGoodCode.equals(dw_goodCode)) rlt = true ;
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		LogUtility.getPumpMLogger().info("[Pump M] SoMo.validate Cust GoodCode with nzGoodCode=" + rlt) ;
		return rlt;
	}
	
}
