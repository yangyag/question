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
	 * 소모 셀프에 오는 판매 완료 전문 (ST) 내용에서 실패한 내역이 있는 경우 POS 로 0001 전문을 전송하지 않는다.
	 * 2012.06.07 ksm 셀프세차권 출력 관련하여 실패가 있는지 확인하기 위해 사용함.
	 * @param workMsg	: 판매완료 전문. ST_WorkingMessage
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
				String mode = trInfo.getMode() ;	// 4 : 선취소 실패
																			// 5 : 재승인 실패
				// 승인에 대한 취소 혹은 재승인 실패 전문이 있는 경우
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
	 * 소모셀프의 경우 현금 전문이 있다. 이는 CAT M 을 통해서 응답 전문을 받는 것은 아니며, Pump M 에서 자체적으로 만들어서
	 * POS 및 Pump A 에 전송한다. 
	 * 
	 * @param workingMsg	: HA_WorkingMessage
	 * @param khproc_no		: KH 처리번호
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
	 * BL 체크 전문 생성을 요청한다.
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
		//전문변경에 따른 항목 추가(2015.11.17) 
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
				//전문변경에 따른 항목 추가 (2015.11.17)
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
     * ST 전문을 소모셀프 ODT 로 부터 수신 이후 외상 거래처로 인한 결제인 경우는 0082 (외상 거래 응답 전문) 을 
     * 만들어서 POS 로 전송하도록 한다.
     * 
     * @param dwPumpM			: POS Protocol DW 전문
     * @param PB_ST_TrInfo		: 소모셀프 ODT 로 부터의 ST 전문
	 * @param khproc_no			: KH 처리번호
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
			// TR 전문의 필드
			String tr_liter 		= PumpMUtil.convertTotalLiterFromPumpTOPOS(trInfo.getLiter());		// 판매량
			String tr_price 		= trInfo.getPrice();																										// 판매 금액
			String tr_basePrice 	= PumpMUtil.convertBasePriceFromPumpToPOS(trInfo.getBasePrice()); 	// 판매 단가 
			
			// 아래 변수들은 외상응답 전문 구성시 내용을 채울 필요가 없는 정보 들임. 따라서 Default 값을 사용함.
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
			// 의미 없는 변수 - 끝
			
			String emp_no = "" ;									// 충전원 ID (셀프 ODT 는 없음)
			String custCard_No = "" ;								// 거래처카드번호
			String ss_crStNum = "" ;								// 거래처번호
			String ss_carNum = "" ;									// 거래처차량번호
			String term_id = "         " ;							// 단말기 번호
			String lastPayment_yn = "0" ;							// 마지막 결제여부
			String led_code = "1" ;									// LED 코드
			String keepDoc_limitDate = "" ;							// 보관증 만료일
			
			if (dwPumpM != null) {
				custCard_No 	= dwPumpM.getCust_card_no() ;		// 거래처카드번호
				ss_crStNum 		= dwPumpM.getCust_code() ;			// 거래처번호
				ss_carNum 		= dwPumpM.getCar_no() ;				// 거래처차량번호
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
	 * 소모셀프 카드 결제 요청 전문을 UPOSMessage Class 로 변환한다. 
	 * 소모셀프로부터의 승인 요청 전문은 HA_WorkingMessage Class 로 전달되며, Card_Type 및 BonusCard 필드
	 * 여부에 따라서 그에 상응하는 UPOSMessage Class 로 변환한다.
	 * 	Card_Type													보너스 카드 O		보너스 카드 X
	 * 		1	신용카드 (+보너스) 승인 요청								0033			0031
	 * 		2	신용카드 (+보너스) 승인 취소 요청						8033			8031
	 * 		3	보너스 카드 누적 요청											0003			
	 * 		4	보너스 카드 누적 취소 요청									8003
	 * 		5	현금 승인 요청 (현금 거래일 경우)											0011
	 * 		6	현금 + 보너스 누적 승인 요청								0013
	 * 		A	GS , GS& 보너스 카드 이용 승인 요청										0061
	 * 		B	GS , GS& 보너스 카드 이용 취소 요청										8061
	 * 		C	myLG  보너스 카드 이용 승인 요청											0051
	 * 		D	myLG 보너스 카드 이용 취소 요청											8051
	 * 		E	BL 체크																0071
	 * 		F	국세청 현금 영수증 승인 요청								0015
	 * 		G	국세청 현금 영수증 승인 취소 요청						8015
	 * 		H	기타 (GS, myLG 제외) 보너스 카드 이용 승인 요청		0033 (신용카드 결제와 동일, 할부개월은 61로 설정->VAN 사에서 보너스이용으로 인식)
	 * 		I	기타 (GS, myLG 제외) 보너스 카드 이용 승인 취소 요청	8033 (신용카드 결제와 동일, 할부개월은 61로 설정->VAN 사에서 보너스이용으로 인식)
	 * 
	 * @param workingMsg
	 * @param khproc_no		: KH 처리번호
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
				
		// ksm 2012.06.07 F879 장애의심부분  PumpMUtil 726 - [Pump M] BnsCrdNm NoMatch '='
		String bonus_card 				= PumpMUtil.getRealBonusCardNumber(haWorkingMsg.getBonusCard());
		String liter 							= PumpMUtil.convertNumberFormatFromPumpToStandardFormat(haWorkingMsg.getLiter(), 3);
		String basePrice 				= PumpMUtil.convertNumberFormatFromPumpToStandardFormat(haWorkingMsg	.getBasePrice(), 2);
		String payPrice 					= GlobalUtility.getStringValue(haWorkingMsg.getPrice());
		String loyalty_password 	= haWorkingMsg.getPin().trim(); 
		String authType 					= haWorkingMsg.getAuthType();

		String creditAuthNo 	= haWorkingMsg.getCNumber();
		String bonusAuthNo = haWorkingMsg.getBNumber();

		//전문변경에 따른 항목 추가(2015.11.17) 
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
														//전문변경에 따른 항목 추가 (2015.11.17)
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
														//전문변경에 따른 항목 추가 (2015.11.17)
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
//																		전문변경에 따른 항목 추가 (2015.11.17)
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
		// 현금결제일 경우
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
																	//전문변경에 따른 항목 추가 (2015.11.17)
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
																	//	전문변경에 따른 항목 추가 (2015.11.17)
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
			//보너스 승인인 경우 승인번호를 'creditAuthNo' 여기에도 같이 넣어주기 때문에 이전 uPos전문 조회 시 똑같이 
			// 'creditAuthNo' 이곳에 값을 넣어 조회가 되게해야 된다.
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
			
			//	2012.07.09 ksm  my신한 포인트 사용 중지
			// 마케팅 전략팀 김준완 사원 CSR 요청 ( 2012-07-06 )
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
																	//전문변경에 따른 항목 추가 (2015.11.17)
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
			
			/*	PI2 20160330 twlee 기존 AS-IS 소스
			if ( "4".equals(certiNumberType))
				certiNumberType = "9";
			*/
			
			/*
			 * if ("4".equals(loyalty_password)) { // certiDest = "01" ; } else {
			 * certiDest = "02" ; }
			 */
			// (2byte)+{(1byte)+ (1byte)}
			
			// PI2 20160330 twlee 장애대응 관련하여  현금영수증 요청 코드 변경
			//  국세청송신주체(01:GSC, 02:스마트로) + 거래자구분(0:소비자, 1:사업자) + 확인자구분(0:신용카드번호 1:주민등록번호 2:사업자등록번호 3:기타 9:보너스카드번호)
			if ("1".equals(certiNumberType)) {	// 20160323 twlee PI2  주민번호로 현금영수증 처리를 막음
				creditCardReading_type = IConstant.CreditCardReading_Type_Keyin;
			} else if ("2".equals(certiNumberType)) {	// 사업자 번호
				creditCardReading_type = IConstant.CreditCardReading_Type_Keyin;
			} else if("3".equals(certiNumberType)) {	// 핸드폰 번호
				creditCardReading_type = IConstant.CreditCardReading_Type_Keyin;
			} else if ("4".equals(certiNumberType)) {	// 보너스 카드번호
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
																		// 전문변경에 따른 항목 추가 (2015.11.17)
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
			// (GS, myLG 이외의 POINT 사용)
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
																				//전문변경에 따른 항목 추가 (2015.11.17)
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
			// (GS, myLG 이외의 POINT 사용)
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
			// (현대 M 포인트)
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
																			//전문변경에 따른 항목 추가 (2015.11.17)
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
			// (현대 M 포인트 )
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
			// (GS제외 - 기타포인트 사용 승인  요청 )
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
																			//전문변경에 따른 항목 추가 (2015.11.17)
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
			// (GS제외 - 기타포인트 사용 취소 요청 )
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
	 * 승인 요청에 대한 응답 전문 (UPOSMessage Object) 를 받아서 이를 소모 셀프 ODT Spec 에 맞는 HC_WorkingMessage Class
	 * 로 변환한다.
	 * 아래 내용은 UPOSMessage Object 의 Message Type 에 따른 전문 내용이다.
	 * 														trType
	 * 		0034		신용카드 + 보너스 누적 응답 전문			1			
	 * 		0032		신용 카드 응답 전문					1	or H (할부개월이 61인 경우)
	 * 		8034		신용카드 + 보너스 누적 취소 응답 전문		2
	 * 		8032		신용 카드 취소 응답 전문				2	or I (할부개월이 61인 경우)
	 * 		0004		보너스 카드 누적 응답 전문				3
	 * 		8004		보너스 카드 누적 취소 응답 전문			4
	 * 		0012		현금 승인 응답							5
	 * 		0014		현금 + 보너스 누적 응답 전문				6
	 * 		0062		GS, GS& 보너스 카드 이용 응답 전문		A
	 * 		8062		GS, GS& 보너스 카드 이용 취소 응답 전문	B
	 * 		0052		myLG 보너스 카드 이용 승인 응답 전문		C
	 * 		8052		myLG 보너스 카드 이용 취소 응답 전문		D
	 * 		0072		BL 체크 응답 전문						E
	 * 		0016		국세청 현금 영수증 승인 응답 전문			F
	 * 		8016		국세청 현금 영수증 승인 취소 응답 전문	G
	 * 
	 * [2008.11.27] 변경 by 정순덕 차장님.
	 * 스마트로 정순덕 차장님께서 다음과 같이 변경을 요청하였습니다.
	 * 	1. 기존
	 * 		GS 포인트 이용 -> GS 보너스 서버
	 * 		기타 포인트 이용 (myLG 포함) -> VAN 사로 일반 신용카드 결제처럼 전문을 구성하되, 할부개월을 '61' 로 설정
	 * 	2. 변경 사항
	 * 		GS 포인트 이용 -> GS 보너스 서버
	 * 		myLG 포인트 이용 -> VAN 사로 포인트 사용 L3 전문을 전송하도록 한다.
	 * 		기타 포인트 이용 (GS , myLG 제외) -> VAN 사로 일반 신용카드 결제처럼 전문을 구성하되, 할부개월을 '61' 로 설정
	 * 
	 * [2012.06.01] 전문추가 - 서일석유 요청으로 셀프 세차바코드 출력
	 * 
	 * @param uPosMsg
	 * @return
	 */
	public static ArrayList<WorkingMessage> createWorkingMessageFromUPOSMessage_SoMo(UPOSMessage uPosMsg) {
		LogUtility.getPumpMLogger().info("[Pump M] Convert UPOSMessage[" + uPosMsg.getMessageType() + "] to WorkingMessage for SoMo.") ;
		
		ArrayList<WorkingMessage> workMsgArray = new ArrayList<WorkingMessage>() ;
		String trType = null ;
		
		// 소모 셀프의 경우 복합 결제가 가능하다. (신용 승인 이후 보너스 누적이 올 수 있음.)
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
				// 신용카드 + 보너스 승인 응답
				LogUtility.getPumpMLogger().info("[Pump M] Credit+Bonus Response -> WorkingMessage") ;
				trType = "1" ;
				HC_WorkingMessage hcWorkingMsg = getHC_WorkingMessage_SoMo(uPosMsg, trType, true) ;
				workMsgArray.add(hcWorkingMsg) ;
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_0032 : { 
				// 신용카드 승인 응답 (or 포인트 사용 승인 응답) -> 할부개월이 61인 경우 포인트 사용(GS , myLG 제외)
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
				// 신용카드 + 보너스 취소 응답
				LogUtility.getPumpMLogger().info("[Pump M] Credit+Bonus Cancel Response -> WorkingMessage") ;
				trType = "2" ;
				HC_WorkingMessage hcWorkingMsg = getHC_WorkingMessage_SoMo(uPosMsg, trType, false) ;
				workMsgArray.add(hcWorkingMsg) ;
				
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_8032 : { 
				// 신용카드 승인 취소 응답 (or 포인트 사용 취소 응답) -> 할부개월이 61인 경우 포인트 사용(GS , myLG 제외)
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
				// 보너스 카드 누적 응답
				LogUtility.getPumpMLogger().info("[Pump M] Bonus Accumulation Response -> WorkingMessage") ;
				trType = "3" ;
				HC_WorkingMessage hcWorkingMsg = getHC_WorkingMessage_SoMo(uPosMsg, trType, true) ;
				workMsgArray.add(hcWorkingMsg) ;
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_8004 : { 
				// 보너스 카드 누적 취소 응답
				LogUtility.getPumpMLogger().info("[Pump M] Bonus Accumulation Cancel Response -> WorkingMessage") ;
				trType = "4" ;
				HC_WorkingMessage hcWorkingMsg = getHC_WorkingMessage_SoMo(uPosMsg, trType, false) ;
				workMsgArray.add(hcWorkingMsg) ;				
				break ;
			}	
			case IUPOSConstant.MESSAGETYPE_INT_0012 : { 
				// 현금 응답
				LogUtility.getPumpMLogger().info("[Pump M] Money Response -> WorkingMessage") ;
				trType = "5" ;
				HC_WorkingMessage hcWorkingMsg = getHC_WorkingMessage_SoMo(uPosMsg, trType, true) ;
				workMsgArray.add(hcWorkingMsg) ;				
				break ;
			}	
			case IUPOSConstant.MESSAGETYPE_INT_0014 : { 
				// 현금 + 보너스 누적 응답 전문
				LogUtility.getPumpMLogger().info("[Pump M] Money+Bonus Response -> WorkingMessage") ;
				trType = "6" ;
				HC_WorkingMessage hcWorkingMsg = getHC_WorkingMessage_SoMo(uPosMsg, trType, true) ;
				workMsgArray.add(hcWorkingMsg) ;
				
				break ;
			}	
			case IUPOSConstant.MESSAGETYPE_INT_0062 : { 
				// GS 보너스 카드 이용 응답
				LogUtility.getPumpMLogger().info("[Pump M] GS Bonus Card use Response -> WorkingMessage") ;
				trType = "A" ;
				HC_WorkingMessage hcWorkingMsg = getHC_WorkingMessage_SoMo(uPosMsg, trType, true) ;
				workMsgArray.add(hcWorkingMsg) ;	
				break ;
			}			
			case IUPOSConstant.MESSAGETYPE_INT_8062 : { 
				// GS 보너스 카드 이용 취소 응답 전문
				LogUtility.getPumpMLogger().info("[Pump M] GS Bonus Card use Cancel Response -> WorkingMessage") ;
				trType = "B" ;
				HC_WorkingMessage hcWorkingMsg = getHC_WorkingMessage_SoMo(uPosMsg, trType, false) ;
				workMsgArray.add(hcWorkingMsg) ;
				break ;
			}
			
			/*
			 //	2012.07.09 ksm  my신한 포인트 사용 중지
			// 마케팅 전략팀 김준완 사원 CSR 요청 ( 2012-07-06 )
			case IUPOSConstant.MESSAGETYPE_INT_0052 : { 
				// myLG 보너스 카드 이용 승인 응답 전문
				LogUtility.getPumpMLogger().info("[Pump M] myLG Bonus Card use Response -> WorkingMessage") ;
				trType = "C" ;
				HC_WorkingMessage hcWorkingMsg = getHC_WorkingMessage_SoMo(uPosMsg, trType, true) ;
				workMsgArray.add(hcWorkingMsg) ;	
				break ;
			}				
			case IUPOSConstant.MESSAGETYPE_INT_8052 : { 
				// myLG 보너스 카드 이용 취소 응답 전문
				LogUtility.getPumpMLogger().info("[Pump M] myLG Bonus Card use Cancel Response -> WorkingMessage") ;
				trType = "D" ;
				HC_WorkingMessage hcWorkingMsg = getHC_WorkingMessage_SoMo(uPosMsg, trType, false) ;
				workMsgArray.add(hcWorkingMsg) ;
				break ;
			} */
			case IUPOSConstant.MESSAGETYPE_INT_0072 : { 
				// BL 체크 응답 전문
				LogUtility.getPumpMLogger().info("[Pump M] BL Response -> WorkingMessage") ;
				trType = "E" ;
				HC_WorkingMessage hcWorkingMsg = getHC_WorkingMessage_SoMo(uPosMsg, trType, true) ;
				workMsgArray.add(hcWorkingMsg) ;
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_0016 : { 
				// 국세청 현금 영수증 승인 응답 전문
				LogUtility.getPumpMLogger().info("[Pump M] Money Receipt Response -> WorkingMessage") ;
				trType = "F" ;
				HC_WorkingMessage hcWorkingMsg = getHC_WorkingMessage_SoMo(uPosMsg, trType, true) ;
				workMsgArray.add(hcWorkingMsg) ;
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_8016 : { 
				// 국세청 현금 영수증 승인 취소 응답 전문
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
	 * UPOSMessage Object 를 HC_WorkingMessage Object 로 변환한다.
	 * 
	 *
	 * @param uPosMsg : 	UPOSMessage
	 * @param trType		결제유형 구분	
	 * @param isAccept
	 * 			true : 		승인 응답 전문
	 * 			false : 	취소 응답 전문
	 * @return
	 * 			HC_WorkingMessage
	 * 

	 */
	private static HC_WorkingMessage getHC_WorkingMessage_SoMo(UPOSMessage uPosMsg , String trType, boolean isAccept) {
		HC_WorkingMessage hcWorkingMsg = null ;
		
		String odtNo = null ;
		String nozzle_no = uPosMsg.getNozzle_no() ;
		String mode = "" ;								// 승인 응답 타입
//		String bonusCardType = "" ;						// 보너스카드 종류	(x)
		String bonusAuthCode = "" ;						// 보너스승인코드
		String authInfo = "" ;							// 승인정보
//		String liter = "" ;								// 수량 (4.3)
		String basePrice = "" ;							// 단가 (4.2)
		String price = "" ;								// 금액
		String authTime = "" ;							// 승인 시각
		String authNo = "" ;							// 승인 번호
		String cardNo = "" ;							// 카드 번호
//		String cardCorpNumber = "" ;					// 카드사번호 (x)
		String cardCorpName = "" ;						// 카드사 명
		String noteCorpCode = "" ;						// 전표 매입사 코드
		String noteCorpName = "" ;						// 전표 매입사 명
		String noteNumber = "" ;						// 전표 번호
		String bonusCardNumber = "" ;					// 보너스 카드 번호
		String bonusAuthTime = "" ;						// 보너스 승인 시각
		String bonusAuthNumber = "" ;					// 보너스 승인 번호
		String dAuthNumber = "" ;						// 현금영수증 승인 번호
		String generateScore = "" ;						// 발생점수
		String score = "" ;								// 가용점수
		String totalScore = "" ;						// 총누적점수
		String publicMsg = "" ;							// 메시지
		String vanMsg = "" ;							// 밴사메시지
		String bnsMsg = "" ;							// 보너스메시지
		String storePoint = "" ;						// 매장 점수
		String barCode = "";							// 세차 바코드
		String khprocsNo = "";							// K/H 처리번호
		
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
		
		// myLG 보너스 포인트 사용 응답 / 사용 취소 응답일 경우 카드 번호 및 승인 번호는 신용카드 란에 있음.
		if (("C".equals(trType)) || ("D".equals(trType))) {
			bonusAuthTime = uPosMsg.getTrdate_creditCard() ;					// GS,GS&을 제외한 카드의 포인트 사용 승인 시각
			bonusAuthNumber = uPosMsg.getCredit_auth_no() ;						// GS,GS&을 제외한 카드의 포인트 사용 승인 번호
			bonusCardNumber = PumpMUtil.getCardNumberPre16Length(uPosMsg.getCreditCard_no()) ;	// GS,GS&을 제외한 카드의  포인트 사용 카드 번호
		} else {
			authTime = uPosMsg.getTrdate_creditCard() ;									// 승인 시각
			authNo = uPosMsg.getCredit_auth_no() ;										// 승인 번호
			// 2016.03.30 WooChul Jung. 장애대응시스템으로 부터 응답받은 카드번호의 경우 "-" 가 포함된 경우가 발생함.
			cardNo = PumpMUtil.getCardNumberPre16Length(uPosMsg.getCreditCard_no().replaceAll("-", "")) ;	// 카드 번호
			bonusCardNumber = PumpMUtil.getCardNumberPre16Length(uPosMsg.getBonRSCard_no()) ;	// 보너스 카드 번호
			bonusAuthTime = uPosMsg.getTrdate_bonRSCard() ;					// 보너스 승인 시각
			bonusAuthNumber = uPosMsg.getBonRSCard_authNo() ;				// 보너스 승인 번호
		}
		
		// BL 체크인 경우
		if ("E".equals(trType)) {
			odtNo = PumpMUtil.getODTNumberFromNozzleNo(nozzle_no) ;		
			POSHeader posMsg = PumpMODTSaleManager.getCustPOSPumpM(nozzle_no);
			
			POS_DW dwMsg = (POS_DW)posMsg;
			if (dwMsg == null)
				basePrice = UPOSUtil.getBasePriceForPumpA(uPosMsg) ;		// 단가 (4.2)
			else
				basePrice = PumpMUtil.convertNumberFormatFromPOSToPump(dwMsg.getBasePrice(),4,2) ;		// 단가 (4.2)
						
			cardCorpName 	= uPosMsg.getIssuer_name() ;						// 카드사 명
			noteCorpCode 	= uPosMsg.getAcquier_code() ;						// 전표 매입사 코드
			noteCorpName 	= uPosMsg.getAcquier_name() ;						// 전표 매입사 명		
			noteNumber 		= getNoteNumber(uPosMsg.getMessageType(), uPosMsg.getCredit_authInfo(), isAccept) ;		// 전표 번호
			publicMsg			= uPosMsg.getTitle_msg() ;								// 메시지
			vanMsg 				= uPosMsg.getVan_msg() ;								// 밴사메시지
		} else {
			if (("F".equals(trType)) || ("G".equals(trType))) {
				dAuthNumber = authNo ;
			}
	
			odtNo = PumpMUtil.getODTNumberFromNozzleNo(nozzle_no) ;
			bonusAuthCode = uPosMsg.getLoyaltyReqCode() ;				// 보너스 응답 코드
			authInfo = parsingCredit_AuthInfo(uPosMsg.getMessageType(), uPosMsg.getLed_code(), uPosMsg.getCredit_authInfo(),isAccept) ;	// 승인정보
//			liter = UPOSUtil.getLiterForPumpA(uPosMsg) ;					// 수량 (4.3)
			basePrice = UPOSUtil.getBasePriceForPumpA(uPosMsg) ;			// 단가 (4.2)
			price = UPOSUtil.getPriceForPumpA(uPosMsg) ;					// 금액
			cardCorpName = uPosMsg.getIssuer_name() ;						// 카드사 명
			noteCorpCode = uPosMsg.getAcquier_code() ;						// 전표 매입사 코드
			noteCorpName = uPosMsg.getAcquier_name() ;						// 전표 매입사 명		
			noteNumber = getNoteNumber(uPosMsg.getMessageType(), uPosMsg.getCredit_authInfo(), isAccept) ;					// 전표 번호		
			generateScore = uPosMsg.getGs_point1() ;						// 발생점수
			score = uPosMsg.getGs_point2() ;								// 가용점수
			totalScore = uPosMsg.getGs_point3() ;							// 총누적점수
			publicMsg = uPosMsg.getTitle_msg() ;							// 메시지
			vanMsg = uPosMsg.getVan_msg() ;									// 밴사메시지
			bnsMsg = uPosMsg.getBonRS_msg() ;								// 보너스메시지				
			storePoint = uPosMsg.getLocal_point() ;							// 매장점수			
			
			String messageType = uPosMsg.getMessageType();
			
			//LogUtility.getPumpMLogger().info("[CarWash BarCode] MessageType : " + messageType);
			
			// ksm 2012.06.01 매장 세차 바코드 사용이면 결제 금액으로 세차 바코드 생성
			try{
				// (셀프) 세차 바코드 출력 여부
				String printBarCode = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0440);
				
				
				if("1".equals(printBarCode))
				{
					/*
					// 승인취소 응답의 경우 바코드를 넣지 않음.
					if( !"8".equals(messageType.substring(0,1)) ){                         //&& "1".equals(mode) ){
						khprocsNo = uPosMsg.getPosReceipt_no() ;							// K/H처리번호
						barCode = getODTBarcodeNumber(price, khprocsNo);					// 세차바코드
					}else{
						LogUtility.getPumpMLogger().info("[CarWash BarCode] MessageType : " + messageType + ", mode = " + mode);
						//LogUtility.getPumpMLogger().info("[CarWash BarCode] 정상 승인이 아닌 경우 바코드 값은 null 임.");
						barCode = "";
					}
					*/
					khprocsNo = uPosMsg.getPosReceipt_no() ;								// K/H처리번호
					// twsongkis 2015-01-28 새로운 Barcode 클래스의 barcode로직으로 변경
					barCode = Barcode.getBarcodeNumber("3", price, uPosMsg.getNozzle_no(), khprocsNo, messageType, uPosMsg.getLed_code(), null);
				}
			}catch(Exception e){
				LogUtility.getPumpMLogger().error("[BarCode]세차 바코드 출력 여부 조회시 에러 발생");
				LogUtility.getPumpMLogger().error(e.getMessage(), e);
			}
			//////////////////////////////////////////////////////////////////////////////////////
		}
		
		if (!uPosMsg.getLed_code().equals("1")) {
			// PI2 20160325 twlee 장애대응에서 odtScreenMsg를 내려주지 않아 display_msg로 변경			
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
	 * AuthInfo 내용 에서 전표 번호를 가져온다. 
	 * 
	 * @param credit_authInfo		: 응답 전문의 Auth Info
	 * @param isAccepted			: 승인 거절 여부
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
					// 승인 실패 (2009.01.07 추영대 수정)
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
	 * u-POS 전문의 credit_authInfo 필드는 모든 응답 전문에서 사용되는 것이 아니다.
	 * 따라서 의미 있는 Message Type 인지 조사한다.
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
	 * 응답전문의 승인 정보를 소모셀프 ODT 가 요구하는 승인정보로 변환.
	 * 
	 * @param messageType		
	 * @param ledCode		
	 * @param credit_authInfo	: 응답전문의 승인 정보
	 * 		승인시: 거래일련번호(V4byte)+RS+전표번호 (V12byte)+RS+ddc코드(V1byte)+RS+가맹점번호 (V15byte)
	 * 		거절시: 스마트로 응답코드(V2byte)
	 * @param isAccepted		: 
	 * 		true : 승인 요청에 대한 응답
	 * 		false : 승인 취소 요청에 대한 응답 
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
					// 승인 실패 (2009.01.21 추영대 수정)
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
	 * 0001 전문 전송 여부를 설정한다.
	 * 
	 * @param nozzle_no		: 노즐 번호
	 * @param khproc_no		: KH 처리번호
	 * @param workMsg			: 판매 완료 전문 (TR_WorkingMessage)
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
					// Z : 결제내역없음(POS 로부터 Preset 에 의한 주유)
					LogUtility.getPumpMLogger().info("[Pump M] This Pumping was caused by Preset from POS. Don't send 0001 to POS.") ;
					rlt = false ;
/*				} else if (containFailContent(workMsg)) {
					// 선취소 혹은 재승인 시 실패한 내역이 있는 경우
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
	 * UPOSMessage 응답 전문을 Sale M 으로 보낼지를 판단한다.
	 * 
	 * @param uPosMsg	: UPOSMessage 응답 전문
	 * @return
	 */
    public static boolean shouldSendToSaleM(UPOSMessage uPosMsg) {    	
    	boolean rlt = true ;
    	
    	// 2013.07.18 ksm 현금영수증 승인실패인 경우 POS로 전송 안함.
    	if("2".equals(uPosMsg.getLed_code()) && "0016".equals(uPosMsg.getMessageType()) ){
    		LogUtility.getPumpMLogger().info("[현금영수증 승인실패] POS로 전송안함. LED_CODE=2, MESSAGE_TYPE=0016" ) ;
    		rlt = false;
    	}

    	return rlt ;	
    }

	/**
	 * 소모셀프ODT 는 거래처의 유종 코드와 노즐의 유종 코드가 일치할 경우만 주유를 허용한다.
	 * 따라서 현금 거래처에 의한 결제인지 아니면 일반고객의 결제인지 구분할 필요가 있고, 이에 대한 구분은
	 * 기존 거래처 응답 전문의 상품 코드와 노즐의 상품 코드를 비교하여 구분한다.
	 * POS 로 결제 응답 전문을 전송시, 거래처에 의한 결제인 경우는 거래처 정보를 꼭 포함하도록 Spec 에 정의되어 있다.
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
