package com.gsc.kixxhub.module.pumpm.pump.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.ibatis.session.SqlSession;

import com.gsc.kixxhub.common.data.IConstant;
import com.gsc.kixxhub.common.data.IUPOSConstant;
import com.gsc.kixxhub.common.data.posdata.POSHeader;
import com.gsc.kixxhub.common.data.posdata.POS_DW;
import com.gsc.kixxhub.common.data.pump.HE_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.QL_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.data.pump.format.GSSelfODTReceiptData;
import com.gsc.kixxhub.common.data.pump.format.GSSelfODTReceiptMaker;
import com.gsc.kixxhub.common.data.pump.format.PumpMessageFormat;
import com.gsc.kixxhub.common.data.upos.UPOSMessage;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_ItemInfo;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_ItemInfo_Item;
import com.gsc.kixxhub.common.dbadapter.common.SqlSessionFactoryManager;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_PRODUCTHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_STOREHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_PRODUCTData;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_STOREData;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.module.pumpm.pump.data.odt.DasNoODTNozzleInfo;
import com.gsc.kixxhub.module.pumpm.pump.manager.PumpMODTSaleManager;
import com.gsc.kixxhub.module.pumpm.pump.manager.dasno.DasNoSelfPumpingManager;

public class ODTUtility_GSC_Self {

	/**
	 * 현금 + 보너스 응답 영수증 출력
	 * @param itemInfo
	 * @param uPosMsg
	 * @param diffPrice
	 * @param isAccepted
	 * @return
	 */
	@SuppressWarnings("unused")
	private static String createCachWithBonusTatsunoFormat(UPOSMessage_ItemInfo itemInfo, UPOSMessage uPosMsg, int diffPrice, boolean isAccepted) {
		String formatMsg = "응답 정보는 받았지만, 응답 정보에 에러가 생겨서 Print 정보가 없습니다." ;	
		
		String productName1			= "";	// 상품명
		String storeCode			= "";	// 매장코드 (사용하지 않음)
		String date					= "";	// 날짜 (YYYYMMDDHHMMSS)
		String tel					= "";	// 매장전화번호
		String manager				= "";	// 담당자
		String wDate				= "";	// 영업일자
		String odtNo				= "";	// ODT 번호
		String transactionNo		= "";	// 거래번호
		String storeName			= "";	// 매장명
		String representative		= "";	// 대표자
		String represent			= "";	// 사업자번호
		String address				= "";	// 매장 주소
		String sequence				= "";	// 순번구분
		String productName2			= "";	// 제품명
		String nozzleNo				= "";	// 노즐 번호
		String liter				= "";	// 주유량
		String basePrice			= "";	// 단가
		String price				= "";	// 주유금액
		String productPrice			= "";	// 과세 물품가액
		String taxPrice				= "";	// 부가세
		String totalPrice			= "";	// 합계
		String receivePrice			= "";	// 받은 금액
	 	String refund				= "";	// 환불 금액 
		String bonusCardNo			= "";	// 보너스카드 번호
		String authNo				= "";	// 보너스카드 승인 번호
		String createPoint			= "";	// 발생 포인트
		String usePoint				= "";	// 가용 포인트
		String totalPoint			= "";	// 주유소 포인트
		String message				= "";	// 보너스카드 메세지

		
		SqlSession session = null;		
		try {
			session = SqlSessionFactoryManager.openSqlSession();

			if ((itemInfo.getItemInfoList() != null) && (itemInfo.getItemInfoList().size() != 0)) {
				UPOSMessage_ItemInfo_Item itemInfo_item = itemInfo.getItemInfoList().get(0) ;
				String goods_code = itemInfo_item.getGoodsCode() ;
				
				T_KH_PRODUCTData[] productDataArray = 
					T_KH_PRODUCTHandler.getHandler().getT_KH_PRODUCTData(session, goods_code) ;

				if (productDataArray != null) {
					T_KH_PRODUCTData productData = productDataArray[0] ;
					storeCode = T_KH_STOREHandler.getHandler().getStoreCode();		//매장코드
					T_KH_STOREData storeData = 
						T_KH_STOREHandler.getHandler().getT_KH_STOREDataByStoreCode(storeCode) ;
					
					productName1 = productData.getGoods_name() ;			// 상품명
					date = getWDateFormat() ;								// 날짜
					tel = "(" + storeData.getTel_nbr() + ")" ;				// 매장전화번호
					
					manager = storeData.getRep_name() ;						// 담당자
					wDate = getWorkingDateFormat(storeData.getBizhour_date()) ;			// 영업일자
					odtNo = PumpMUtil.getODTNumberFromNozzleNo(uPosMsg.getNozzle_no()) ;	//odt번호					
					transactionNo = uPosMsg.getPosReceipt_no() ;	// 거래번호
					storeName = storeData.getCust_name_disp() ;		// 매장명
					representative = storeData.getRep_name() ;		// 대표자
					represent = storeData.getBizregno_nbr() ;		// 사업자번호
					address = storeData.getBizstoreaddr_txt1()  + " " + storeData.getBizstoreaddr_txt2();	// 매장주소					
					
					POSHeader posMsg = PumpMODTSaleManager.getCustPOSPumpM(itemInfo_item.getNozzleNo()) ;
					POS_DW dwPumpM = null ;
					if ((posMsg != null) && (posMsg instanceof POS_DW))
						dwPumpM = (POS_DW) posMsg ;
					
					
					sequence = "01" ;		// 구분
					productName2 = productData.getGoods_name() ;	// 상품명
					nozzleNo = uPosMsg.getNozzle_no() ;				// 노즐번호
					liter = PumpMUtil.convertNumberFormatFromPumpToPOS(itemInfo.getTotalOilAmount(),3) ;	// 주유량
					
					if (posMsg == null) {
						basePrice = PumpMUtil.convertNumberFormatFromPumpToPOS(itemInfo.getUnitPrice(),3) ;	// 단가
					} else {
						if ("1".equals(dwPumpM.getRcptunitprc_ind_prt()) )	//단가 출력 여부
							basePrice = PumpMUtil.convertNumberFormatFromPumpToPOS(itemInfo.getUnitPrice(),3) ;	// 단가
					}
					price = itemInfo.getTotalOilPrice_after_discount() ;				// 주유금액
					productPrice = itemInfo.getTotalPrice_tax() ;						// 공급가액
					taxPrice = itemInfo.getTotalTaxPrice() ;							// 새액
					totalPrice = itemInfo.getTotalOilPrice_after_discount() ;			// 주유 금액
					
					HE_WorkingMessage heWorkingMsg = DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzleNo).getHeWorkingMsg();
					
					receivePrice		= heWorkingMsg.getPrice();	// 받은 금액
					if (!PumpMUtil.shouldSendingRejectAndReApproval(receivePrice, totalPrice, false)) 
						refund				= String.valueOf(Double.parseDouble(receivePrice) - Double.parseDouble(totalPrice));	// 환불 금액
					else
						refund				= "0" ;	// 환불 금액
					
					bonusCardNo			= uPosMsg.getBonRSCard_no();	// 보너스카드 번호
					authNo				= uPosMsg.getBonRSCard_authNo();	// 보너스카드 승인 번호
					createPoint			= uPosMsg.getGs_point1();	// 발생 포인트
					usePoint			= uPosMsg.getGs_point2();	// 가용 포인트
					totalPoint			= "0";	// 주유소 포인트
					message				= uPosMsg.getBonRS_msg();	// 보너스카드 메세지
					

				}
			}

		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}
		
		GSSelfODTReceiptData receipt = new GSSelfODTReceiptData(GSSelfODTReceiptData.GS_SELF);
		
		receipt.makeDefaultInfo(
				PumpMessageFormat.getHeadPrint(), 
				productName1, 
				manager, 
				wDate, 
				transactionNo, 
				storeName, 
				tel, 
				representative, 
				represent, 
				address, 
				nozzleNo,
				liter, 
				basePrice, 
				price, 
				productPrice, 
				taxPrice, 
				totalPrice, 
				receivePrice, 
				refund,
				PumpMessageFormat.getFootPrint1(), 
				PumpMessageFormat.getFootPrint2(), 
				date,
				"");
		
		receipt.makeBonusInfo(
				authNo, 
				bonusCardNo, 
				createPoint, 
				message, 
				totalPoint, 
				usePoint);
		
		formatMsg = GSSelfODTReceiptMaker.makeReceipt(receipt);
		
		
		return formatMsg ;
	}
	
	
	/**
	 * 현금 거래처 + 보너스 응답 영수증 출력
	 * @param itemInfo
	 * @param uPosMsg
	 * @param diffPrice
	 * @param isAccepted
	 * @return
	 */
	@SuppressWarnings("unused")
	private static String createCustomerTatsunoFormat(UPOSMessage_ItemInfo itemInfo, UPOSMessage uPosMsg, int diffPrice, boolean isAccepted) {
		String formatMsg = "응답 정보는 받았지만, 응답 정보에 에러가 생겨서 Print 정보가 없습니다." ;	
		
		String productName1			= "";	// 상품명
		String storeCode			= "";	// 매장코드 (사용하지 않음)
		String date					= "";	// 날짜 (YYYYMMDDHHMMSS)
		String tel					= "";	// 매장전화번호
		String manager				= "";	// 담당자
		String wDate				= "";	// 영업일자
		String odtNo				= "";	// ODT 번호
		String transactionNo		= "";	// 거래번호
		String storeName			= "";	// 매장명
		String representative		= "";	// 대표자
		String represent			= "";	// 사업자번호
		String address				= "";	// 매장 주소
		String customerCode			= "";	// 거래처 코드
		String customerName 		= "";	// 거래처명
		String customerCardNo 		= "";	// 거래처 카드번호
		String customerCarNo 		= "";	// 거래처 차량 번호 
		String limit 				= "";	// 한도량
		String saveLimit 			= "";	// 누적 사용량
		String remainLimit 			= "";	// 한도 잔량
		String cardNumber   		= "";	// 신용카드번호
		String acceptNo     		= "";	// 신용카드 승인번호
		String cardCorpName 		= "";	// 신용카드 승인회사
		String sequence				= "";	// 순번구분
		String productName2			= "";	// 제품명
		String nozzleNo				= "";	// 노즐 번호
		String liter				= "";	// 주유량
		String basePrice			= "";	// 단가
		String price				= "";	// 주유금액
		String productPrice			= "";	// 과세 물품가액
		String taxPrice				= "";	// 부가세
		String totalPrice			= "";	// 합계
		String receivePrice			= "";	// 받은 금액
	 	String refund				= "";	// 환불 금액 
		String bonusCardNo			= "";	// 보너스카드 번호
		String authNo				= "";	// 보너스카드 승인 번호
		String createPoint			= "";	// 발생 포인트
		String usePoint				= "";	// 가용 포인트
		String totalPoint			= "";	// 주유소 포인트
		String message				= "";	// 보너스카드 메세지
		String cachReceiptNo	 	= "";	// 현금영수증 인증번호
		String cachReceiptAuthNo 	= "";	// 현금영수증 승인번호
		String cachReceiptMessage 	= "";	// 현금영수증 메세지

		boolean isCreditCard = false;		// 신용카드 여부
		
		SqlSession session = null;		
		try {
			session = SqlSessionFactoryManager.openSqlSession();

			if ((itemInfo.getItemInfoList() != null) && (itemInfo.getItemInfoList().size() != 0)) {
				UPOSMessage_ItemInfo_Item itemInfo_item = itemInfo.getItemInfoList().get(0) ;
				String goods_code = itemInfo_item.getGoodsCode() ;
				
				T_KH_PRODUCTData[] productDataArray = 
					T_KH_PRODUCTHandler.getHandler().getT_KH_PRODUCTData(session, goods_code) ;

				if (productDataArray != null) {
					T_KH_PRODUCTData productData = productDataArray[0] ;
					storeCode = T_KH_STOREHandler.getHandler().getStoreCode();		//매장코드
					T_KH_STOREData storeData = 
						T_KH_STOREHandler.getHandler().getT_KH_STOREDataByStoreCode(storeCode) ;
					
					productName1 = productData.getGoods_name() ;			// 상품명
					date = getWDateFormat() ;								// 날짜
					tel = "(" + storeData.getTel_nbr() + ")" ;				// 매장전화번호
					
					manager = storeData.getRep_name() ;						// 담당자
					wDate = getWorkingDateFormat(storeData.getBizhour_date()) ;			// 영업일자
					odtNo = PumpMUtil.getODTNumberFromNozzleNo(uPosMsg.getNozzle_no()) ;	//odt번호					
					transactionNo = uPosMsg.getPosReceipt_no() ;	// 거래번호
					storeName = storeData.getCust_name_disp() ;		// 매장명
					representative = storeData.getRep_name() ;		// 대표자
					represent = storeData.getBizregno_nbr() ;		// 사업자번호
					address = storeData.getBizstoreaddr_txt1()  + " " + storeData.getBizstoreaddr_txt2();	// 매장주소					
					
					POSHeader posMsg = PumpMODTSaleManager.getCustPOSPumpM(itemInfo_item.getNozzleNo()) ;
					POS_DW dwPumpM = null ;
					if ((posMsg != null) && (posMsg instanceof POS_DW))
						dwPumpM = (POS_DW) posMsg ;
					
					if (posMsg == null) {
						
						customerCode = "";			// 거래처코드
						customerName = "";			// 거래처 명
						customerCardNo = "";		// 거래처 카드번호
						customerCarNo = "";			// 거래처 차량 번호
					} else {
						customerCode = dwPumpM.getCust_code();			// 거래처코드
						customerName = dwPumpM.getDrive_name();			// 거래처 명
						customerCardNo = dwPumpM.getCust_card_no();		// 거래처 카드번호
						customerCarNo = dwPumpM.getCar_no();			// 거래처 차량 번호
					}
					
					limit = "0";									// 한도량
					saveLimit = "0";								// 누적 사용량
					remainLimit = "0";								// 한도 잔량
					
					if (IUPOSConstant.MESSAGETYPE_0032.equals(uPosMsg.getMessageType()) ||
							IUPOSConstant.MESSAGETYPE_0034.equals(uPosMsg.getMessageType())){
						cardNumber = uPosMsg.getCreditCard_no() ;	// 신용카드 카드번호 
						acceptNo = uPosMsg.getCredit_auth_no() ;	// 신용카드 승인번호
						cardCorpName = uPosMsg.getAcquier_code() + " " + uPosMsg.getAcquier_name() ;	//신용카드 승인회사 	
						isCreditCard = true;						//신용카드 여부
						
					}
					
					sequence = "01" ;		// 구분
					productName2 = productData.getGoods_name() ;	// 상품명
					nozzleNo = uPosMsg.getNozzle_no() ;				// 노즐번호
					liter = PumpMUtil.convertNumberFormatFromPumpToPOS(itemInfo.getTotalOilAmount(),3) ;	// 주유량
					
					if (posMsg == null) {
						basePrice = PumpMUtil.convertNumberFormatFromPumpToPOS(itemInfo.getUnitPrice(),3) ;	// 단가
					} else {
						if ("1".equals(dwPumpM.getRcptunitprc_ind_prt()) )	//단가 출력 여부
							basePrice = PumpMUtil.convertNumberFormatFromPumpToPOS(itemInfo.getUnitPrice(),3) ;	// 단가
						else
							basePrice = "0";
					}
					price = itemInfo.getTotalOilPrice_after_discount() ;				// 주유금액
					productPrice = itemInfo.getTotalPrice_tax() ;						// 공급가액
					taxPrice = itemInfo.getTotalTaxPrice() ;							// 새액
					totalPrice = itemInfo.getTotalOilPrice_after_discount() ;			// 주유 금액
					
					receivePrice		= uPosMsg.getItem_info().getTotalOilPrice_after_discount();	// 받은 금액
					
					if (!PumpMUtil.shouldSendingRejectAndReApproval(receivePrice, totalPrice, isCreditCard) )
						refund	= String.valueOf(diffPrice);	// 환불 금액
					else
						refund = "0";
					
					if (IUPOSConstant.MESSAGETYPE_0054.equals(uPosMsg.getMessageType()) ||
							IUPOSConstant.MESSAGETYPE_0012.equals(uPosMsg.getMessageType()) ||
							IUPOSConstant.MESSAGETYPE_0004.equals(uPosMsg.getMessageType()) ||
							IUPOSConstant.MESSAGETYPE_0016.equals(uPosMsg.getMessageType())) {
						
						HE_WorkingMessage heWrkMsg = DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzleNo).getHeWorkingMsg();
						
						receivePrice = String.valueOf(Double.parseDouble(heWrkMsg.getPrice()));
						
						refund = String.valueOf(Double.parseDouble(receivePrice) - Double.parseDouble(totalPrice));
					}

					
					bonusCardNo			= uPosMsg.getBonRSCard_no();	// 보너스카드 번호
					authNo				= uPosMsg.getBonRSCard_authNo();	// 보너스카드 승인 번호
					createPoint			= uPosMsg.getGs_point1();	// 발생 포인트
					usePoint			= uPosMsg.getGs_point2();	// 가용 포인트
					totalPoint			= "0";	// 주유소 포인트
					message				= uPosMsg.getBonRS_msg();	// 보너스카드 메세지
					
					if (IUPOSConstant.MESSAGETYPE_0016.equals(uPosMsg.getMessageType()) ||
							IUPOSConstant.MESSAGETYPE_0054.equals(uPosMsg.getMessageType())){
						cachReceiptNo	 	= uPosMsg.getCreditCard_no();	// 현금영수증 인증번호
						cachReceiptAuthNo 	= uPosMsg.getCredit_auth_no();	// 현금영수증 승인번호
						cachReceiptMessage 	= uPosMsg.getVan_msg();	// 현금영수증 메세지
					} 

				}
			}

		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}
		
		GSSelfODTReceiptData receipt = new GSSelfODTReceiptData(GSSelfODTReceiptData.GS_SELF);
		
		receipt.makeDefaultInfo(
				PumpMessageFormat.getHeadPrint(), 
				productName1, 
				manager, 
				wDate, 
				transactionNo, 
				storeName, 
				tel, 
				representative, 
				represent, 
				address, 
				nozzleNo,
				liter, 
				basePrice, 
				price, 
				productPrice, 
				taxPrice, 
				totalPrice, 
				receivePrice, 
				refund,
				PumpMessageFormat.getFootPrint1(), 
				PumpMessageFormat.getFootPrint2(), 
				date,
				"");
		
		receipt.makeBonusInfo(
				authNo, 
				bonusCardNo, 
				createPoint, 
				message, 
				totalPoint, 
				usePoint);
		
		receipt.makeCustomerInfo(
				customerCardNo, 
				customerCarNo, 
				customerCode, 
				limit, 
				customerName, 
				remainLimit, 
				saveLimit);
		
		String mode;
		if (GlobalUtility.isNullOrEmptyString(uPosMsg.getLoyality_type())){
			mode = "";
		} else {
			mode = uPosMsg.getLoyality_type().substring(2, 3);
		}
			
		receipt.makeCachReceiptInfo(
				mode, 
				cachReceiptAuthNo, 
				cachReceiptMessage, 
				cachReceiptNo);
		
		formatMsg = GSSelfODTReceiptMaker.makeReceipt(receipt);
		
		return formatMsg ;
	}
	
	/**
	 * 승인 실패 영수증 출력
	 * @param option
	 * @param nozzleNo
	 * @param creditCard_no
	 * @param trdate_creditCard
	 * @param van_msg
	 * @param payment_amt
	 * @param date
	 * @return
	 */
	public static String createErrorPrintFormat( int option, String nozzleNo, String creditCard_no, String trdate_creditCard, String van_msg, String payment_amt, String date ) {

		GSSelfODTReceiptData receiptData = new GSSelfODTReceiptData(GSSelfODTReceiptData.GS_SELF);
		String errorName = "";
		
		switch (option) {
		case 0:
			errorName = "선승인체크 성공";
			break;
		case 1:
			errorName = "신용승인 성공";
			break;
		case 2:
		case 5:
			errorName = "신용승인 에러";
			break;
		case 3:
			errorName = "승인취소 성공";
			break;
		case 4:
			errorName = "승인취소 실패 ";
			break;
		case 6:
			errorName = "거래처승인 실패 ";
			break;
		}
		
		receiptData.makeErrorInfo(
										nozzleNo, 
										errorName, 
										creditCard_no, 
										trdate_creditCard, 
										van_msg, 
										payment_amt, 
										date);
		String formatMsg = GSSelfODTReceiptMaker.makeReceipt(receiptData);
		
		return formatMsg;
	}
	
	/**
	 *  
	 * VAN 사로 부터 거절시
	 * 
	 * @return
	 */
	public static QL_WorkingMessage createFailWhenReject(String nozzleNo, UPOSMessage uPosMsg) {
		LogUtility.getPumpMLogger().info("[Pump M] Create Fail Receipt Message for DasNo") ;
		String receipt = "" ;
		String length = "0" ;
		String odtNo = PumpMUtil.getODTNumberFromNozzleNo(nozzleNo) ;	
		String qlMode = "";
		
		try {
			DasNoODTNozzleInfo nozInfo = DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzleNo) ;
			int nozInfoOptionInt = nozInfo.getOption() ;
			int optionState = nozInfo.getOptionState() ;
			int option = 0;
			
			String date = "" ;
			date = getWDateFormat();
			LogUtility.getPumpMLogger().debug("[Pump M] nozInfoOptionInt : " + nozInfoOptionInt); // 테스트 로그
			LogUtility.getPumpMLogger().debug("[Pump M] optionState : " + optionState); // 테스트 로그
			switch (nozInfoOptionInt) {
				case IConstant.FULL_PUMPING_OPTION_0  : {
					switch (optionState) {					
						case IConstant.SELF_FULLPUMPING_OPTION_0_STATE_1 : {
							
							option = 5;
							qlMode = "0";
							break ;
						}
						case IConstant.SELF_FULLPUMPING_OPTION_0_STATE_2 : {
							option = 2;
							
							qlMode = "1";
							break ;
						}
					}
					break ;
				}
				case IConstant.FULL_PUMPING_OPTION_1 : {
					switch (optionState) {					
						case IConstant.SELF_FULLPUMPING_OPTION_1_STATE_1 : {
							option = 5;
							qlMode = "0";
							break ;
						}
						case IConstant.SELF_FULLPUMPING_OPTION_1_STATE_2 : {
							option = 2;
							qlMode = "1";
							break ;
						}
						case IConstant.SELF_FULLPUMPING_OPTION_1_STATE_3 : {
							option = 4;
							qlMode = "1";
							break ;
						}
					}
					break ;
				}
				case IConstant.FULL_PUMPING_OPTION_2 : {
					switch (optionState) {					
						case IConstant.SELF_FULLPUMPING_OPTION_2_STATE_1 : {
							option = 5;
							qlMode = "0";
							break ;
						}
						case IConstant.SELF_FULLPUMPING_OPTION_2_STATE_2 : {
							option = 4;
							qlMode = "1";
							break ;
						}
						case IConstant.SELF_FULLPUMPING_OPTION_2_STATE_3 : {
							option = 2;
							qlMode = "1";
							break ;
						}
					}
					break ;
				}	
				case IConstant.FULL_PUMPING_OPTION_5 : {
					switch (optionState) {					
						case IConstant.SELF_FULLPUMPING_OPTION_5_STATE_1 : {
							option = 5;
							qlMode = "0";
							break ;
						}
						case IConstant.SELF_FULLPUMPING_OPTION_5_STATE_2 : {
							option = 4;
							qlMode = "1";
							break ;
						}
					}
					break ;
				}
				case IConstant.FULL_PUMPING_OPTION_6 : {
					switch (optionState) {					
						case IConstant.SELF_FULLPUMPING_OPTION_6_STATE_1 : {
							option = 5;
							qlMode = "0";
							break ;
						}
						case IConstant.SELF_FULLPUMPING_OPTION_6_STATE_2 : {
							option = 4;
							qlMode = "1";
							break ;
						}
						case IConstant.SELF_FULLPUMPING_OPTION_6_STATE_3 : {
							option = 2;
							qlMode = "1";
							break ;
						}
					}
					break ;
				}
				case IConstant.FULL_PUMPING_OPTION_7 : {
					switch (optionState) {					
						case IConstant.SELF_FULLPUMPING_OPTION_7_STATE_1 : {
							option = 5;
							qlMode = "0";
							break ;
						}
						case IConstant.SELF_FULLPUMPING_OPTION_7_STATE_2 : {
							option = 4;
							qlMode = "1";
							break ;
						}
					}
					break ;
				}
				case IConstant.FULL_PUMPING_OPTION_8 : {
					switch (optionState) {					
						case IConstant.SELF_FULLPUMPING_OPTION_8_STATE_1 : {
							option = 5;
							qlMode = "0";
							break ;
						}
						case IConstant.SELF_FULLPUMPING_OPTION_8_STATE_2 : {
							
							break ;
						}
					}
						break ;
				}
				default : {
					LogUtility.getPumpMLogger().error("[Pump M] Can't proceed to make Receipt") ;
					break ;
				}
			}
			
			receipt = createErrorPrintFormat(
					option,
					nozzleNo,
					uPosMsg.getCreditCard_no(), 
					uPosMsg.getTrdate_creditCard(), 
					uPosMsg.getVan_msg(), 
					GlobalUtility.getStringValue((uPosMsg.getItem_info()).getTotalOilPrice_after_discount())	, 
					date);
			
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
		}
		if (receipt != null) {
			length = Integer.toString(receipt.length()) ;
		}
		return new QL_WorkingMessage(odtNo, nozzleNo, length, receipt, qlMode, "", "") ;
	}

	/**
	 * VAN 사로 부터 TimeOut 발생시
	 * @param nozzleNo			: 노즐 번호
	 * @param uPosMsg			: 결재 요청에 대한 응답 전문 UPOSMessage
	 * @return
	 */
	public static QL_WorkingMessage createFailWhenTimeOut(String nozzleNo, UPOSMessage uPosMsg) {
		LogUtility.getPumpMLogger().info("[Pump M] Create TimeOut Receipt Message for DasNo") ;
		String receipt = "" ;
		String length = "0" ;
		String odtNo = PumpMUtil.getODTNumberFromNozzleNo(nozzleNo) ;
		
		//return new QL_WorkingMessage(odtNo, nozzleNo, length, receipt, "1") ;
		return new QL_WorkingMessage(odtNo, nozzleNo, length, receipt, "1", "", "") ;
	}
	
	/**
	 * 현금 결재 이후 GSC 표준 SELF ODT 출력 메시지 생성을 요청한다.
	 * 
	 * @param nozzleNo			: 노즐 번호
	 * @param khproc_no			: KH 처리번호
	 * @param pumpingPrice		: 주유 금액
	 * @param pumpingLiter		: 주유 량
	 * @param payedPrice		: 결재 금액
	 * @param pumpingBasePrice	: 주유 단가
	 * @return
	 */
	@SuppressWarnings("unused")
	public static String createReceiptByCash(String nozzleNo, String khproc_no, 
			String pumpingPrice, String pumpingLiter, String payedPrice, String pumpingBasePrice) {
		String formatMsg = "응답 정보는 받았지만, 응답 정보에 에러가 생겨서 Print 정보가 없습니다." ;		

		String productName1 = "" ; 	// 상품명
		String storeCode = "" ; 	// 매장코드
		String date = "" ; 			// 날짜 (YYYYMMDDHHMMSS)
		String tel = "" ; 			// 매장전화번호
		String manager = "" ; 		// 담당자
		String wDate = "" ; 		// 영업일자
		String odtNo = "" ; 		// ODT 번호
		String transactionNo = "" ;	// 거래번호
		String storeName = "" ; 	// 매장명
		String representative = "" ;// 대표자
		String represent = "" ; 	// 사업자번호
		String address = "" ; 		// 매장 주소
		/*String cardNumber = "" ; 	// 카드번호
		String acceptNo = "" ; 		// 승인번호
		String cardCorpName = "" ; 	// 승인회사
*/		String sequence = "" ; 		// 구분
		String productName2 = "" ; 	// 상품명
		String liter = "" ; 		// 주유량
		String basePrice = "" ; 	// 단가
		String price = "" ; 		// 주유금액
		String productPrice = "" ; 	// 공급가액
		String taxPrice = "" ; 		// 새액
		String totalPrice = "" ; 	// 주유 금액
		String receivePrice = payedPrice ; 	// 결제 금액
		int refund = 0 ;
		
		SqlSession session = null;		
		try {
			session = SqlSessionFactoryManager.openSqlSession();
					
			UPOSMessage_ItemInfo itemInfo = UPOSUtil.getUPOSMessage_ItemInfo(nozzleNo, khproc_no,
					pumpingLiter, pumpingBasePrice, pumpingPrice) ;
			
			if ((itemInfo.getItemInfoList() != null) && (itemInfo.getItemInfoList().size() != 0)) {
				UPOSMessage_ItemInfo_Item itemInfo_item = itemInfo.getItemInfoList().get(0) ;
				String goods_code = itemInfo_item.getGoodsCode() ;
				
				T_KH_PRODUCTData[] productDataArray = 
					T_KH_PRODUCTHandler.getHandler().getT_KH_PRODUCTData(session, goods_code) ;

				if (productDataArray != null) {
					T_KH_PRODUCTData productData = productDataArray[0] ;
					storeCode = T_KH_STOREHandler.getHandler().getStoreCode();
					T_KH_STOREData storeData = 
						T_KH_STOREHandler.getHandler().getT_KH_STOREDataByStoreCode(storeCode) ;
					
					productName1 = productData.getGoods_name() ;
					date = getWDateFormat() ;
					tel = "(" + storeData.getTel_nbr() + ")" ;
					
					manager = storeData.getRep_name() ;			// 담당자
					wDate = getWorkingDateFormat(storeData.getBizhour_date()) ;			// 영업일자
					odtNo = PumpMUtil.getODTNumberFromNozzleNo(nozzleNo) ;					
					transactionNo = khproc_no ;	// 거래번호
					storeName = storeData.getCust_name_disp() ;		// 매장명
					representative = storeData.getRep_name() ;	// 대표자
					represent = storeData.getBizregno_nbr() ;		// 사업자번호
					address = storeData.getBizstoreaddr_txt1()  + " " + storeData.getBizstoreaddr_txt2();	// 매장주소					
					sequence = "01" ;		// 구분
					productName2 = productData.getGoods_name() ;	// 상품명
					liter = itemInfo.getTotalOilAmount() ;			// 주유량
					basePrice = itemInfo.getUnitPrice() ;		// 단가
					price = itemInfo.getTotalOilPrice_after_discount() ;			// 주유금액
					productPrice = itemInfo.getTotalPrice_tax() ;	// 공급가액
					taxPrice = itemInfo.getTotalTaxPrice() ;		// 새액
					totalPrice = itemInfo.getTotalOilPrice_after_discount() ;		// 주유금액
					
					refund = Integer.parseInt(payedPrice) - Integer.parseInt(pumpingPrice) ;
				}
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}
		
		
		GSSelfODTReceiptData receipt = new GSSelfODTReceiptData(GSSelfODTReceiptData.GS_SELF);
		
		receipt.makeDefaultInfo(
				PumpMessageFormat.getHeadPrint(), 
				productName1, 
				manager, 
				wDate, 
				transactionNo, 
				storeName, 
				tel, 
				representative, 
				represent, 
				address, 
				nozzleNo,
				GlobalUtility.getDividedWith1000(liter), 
				GlobalUtility.getDividedWith1000(basePrice), 
				GlobalUtility.getStringValue(price), 
				GlobalUtility.getStringValue(productPrice), 
				GlobalUtility.getStringValue(taxPrice), 
				GlobalUtility.getStringValue(totalPrice), 
				GlobalUtility.getStringValue(receivePrice), 
				GlobalUtility.getStringValue(refund),
				PumpMessageFormat.getFootPrint1(), 
				PumpMessageFormat.getFootPrint2(), 
				date,
				"");
		
		formatMsg = GSSelfODTReceiptMaker.makeReceipt(receipt);
		
		return formatMsg ;
	}

	/**
	 * 신용 승인 요청에 대한 응답 전문을 이용하여 다쓰노 ODT 영수증 출력을 위한 메시지 생성을 요청한다.
	 * 
	 * @param pumpingItemInfo	: 주유 정보
	 * @param uPosMsg			: 신용 승인 응답 전문
	 * @param refund			: POS 에서 현금으로 제공해야할 금액
	 * @param isAccepted		: 승인이 난 경우
	 * @return
	 */
	@SuppressWarnings("unused")
	private static String createReceiptByCash(UPOSMessage_ItemInfo pumpingItemInfo, 
			UPOSMessage uPosMsg, int refund, boolean isAccepted) {
		String formatMsg = "응답 정보는 받았지만, 응답 정보에 에러가 생겨서 Print 정보가 없습니다." ;		
		
		String productName1 = "" ;	// 상품명
		String storeCode = "" ;		// 매장코드
		String date = "" ;			// 날짜(YYYYMMDDHHMMSS)
		String tel = "" ;			// 매장전화번호
		String manager = "" ;		// 담당자
		String wDate = "" ;			// 영업일자
		String odtNo = "" ;			// ODT 번호
		String transactionNo = "" ;	// 거래번호
		String storeName = "" ;		// 매장명
		String representative = "" ;	// 대표자
		String represent = "" ;		// 사업자번호
		String address = "" ;		// 매장주소
		String cardNumber = "" ;	// 카드번호 
		String acceptNo = "" ;		// 승인번호 
		String cardCorpName = "" ;	// 승인회사 
		String sequence = "" ;		// 구분
		String productName2 = "" ;	// 상품명
		String nozzleNo = "" ;		// 노즐번호
		String liter = "" ;			// 주유량
		String basePrice = "" ;		// 단가
		String price = "" ;			// 주유금액
		String productPrice = "" ;	// 공급가액
		String taxPrice = "" ;		// 새액
		String totalPrice = "" ;	// 주유 금액
		String receivePrice = "" ;	// 결제금액
		
		SqlSession session = null;		
		try {
			session = SqlSessionFactoryManager.openSqlSession();

			uPosMsg.print() ;
			
			UPOSMessage_ItemInfo itemInfo = uPosMsg.getItem_info() ;
			if ((itemInfo.getItemInfoList() != null) && (itemInfo.getItemInfoList().size() != 0)) {
				UPOSMessage_ItemInfo_Item itemInfo_item = itemInfo.getItemInfoList().get(0) ;
				String goods_code = itemInfo_item.getGoodsCode() ;
				
				T_KH_PRODUCTData[] productDataArray = 
					T_KH_PRODUCTHandler.getHandler().getT_KH_PRODUCTData(session, goods_code) ;

				if (productDataArray != null) {
					T_KH_PRODUCTData productData = productDataArray[0] ;
					storeCode = T_KH_STOREHandler.getHandler().getStoreCode();
					T_KH_STOREData storeData = 
						T_KH_STOREHandler.getHandler().getT_KH_STOREDataByStoreCode(storeCode) ;
					
					productName1 = productData.getGoods_name() ;
					date = getWDateFormat() ;
					tel = "(" + storeData.getTel_nbr() + ")" ;
					
					manager = storeData.getRep_name() ;				// 담당자
					wDate = getWorkingDateFormat(storeData.getBizhour_date()) ;			// 영업일자
					odtNo = PumpMUtil.getODTNumberFromNozzleNo(uPosMsg.getNozzle_no()) ;					
					transactionNo = uPosMsg.getPosReceipt_no() ;	// 거래번호
					storeName = storeData.getCust_name_disp() ;		// 매장명
					representative = storeData.getRep_name() ;		// 대표자
					represent = storeData.getBizregno_nbr() ;		// 사업자번호
					address = storeData.getBizstoreaddr_txt1()  + " " + storeData.getBizstoreaddr_txt2();	// 매장주소					
					cardNumber = uPosMsg.getCreditCard_no() ;	// 카드번호
					acceptNo = uPosMsg.getCredit_auth_no() ;	// 승인번호
					cardCorpName = uPosMsg.getAcquier_code() + " " + uPosMsg.getAcquier_name() ;	// 승인회사 					
					sequence = "01" ;														// 구분
					productName2 = productData.getGoods_name() ;							// 상품명
					nozzleNo = uPosMsg.getNozzle_no() ;										// 노즐번호
					liter = pumpingItemInfo.getTotalOilAmount() ;							// 주유량
					basePrice = pumpingItemInfo.getUnitPrice() ;							// 단가
					price = pumpingItemInfo.getTotalOilPrice_after_discount() ;				// 주유금액
					productPrice = pumpingItemInfo.getTotalPrice_tax() ;					// 공급가액
					taxPrice = pumpingItemInfo.getTotalTaxPrice() ;							// 새액
					totalPrice = pumpingItemInfo.getTotalOilPrice_after_discount() ;		// 주유금액
					
					if (isAccepted) {
						if (Integer.parseInt(liter) == 0)
							receivePrice = String.valueOf(refund);
						else
							receivePrice = String.valueOf(Integer.parseInt(itemInfo.getTotalOilPrice_after_discount()) 
										+ refund);			// 결제금액
					} else {
						receivePrice = "0" ;
					}
				}
			}

		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}
		
		
		GSSelfODTReceiptData receipt = new GSSelfODTReceiptData(GSSelfODTReceiptData.GS_SELF);
		
		receipt.makeDefaultInfo(
				PumpMessageFormat.getHeadPrint(), 
				productName1, 
				manager, 
				wDate, 
				transactionNo, 
				storeName, 
				tel, 
				representative, 
				represent, 
				address, 
				nozzleNo,
				GlobalUtility.getDividedWith1000(liter), 
				GlobalUtility.getDividedWith1000(basePrice), 
				GlobalUtility.getStringValue(price), 
				GlobalUtility.getStringValue(productPrice), 
				GlobalUtility.getStringValue(taxPrice), 
				GlobalUtility.getStringValue(totalPrice), 
				GlobalUtility.getStringValue(receivePrice), 
				GlobalUtility.getStringValue(refund),
				PumpMessageFormat.getFootPrint1(), 
				PumpMessageFormat.getFootPrint2(), 
				date,
				"");

		receipt.makeCreditInfo(
				acceptNo, 
				cardNumber, 
				cardCorpName);

		formatMsg = GSSelfODTReceiptMaker.makeReceipt(receipt);
		
		return formatMsg ;
	}

	/**
	 * 신용 승인 요청에 대한 응답 전문을 이용하여 다쓰노 ODT 영수증 출력을 위한 메시지 생성을 요청한다.
	 * 
	 * @param pumpingItemInfo	: 주유 정보
	 * @param uPosMsg			: 신용 승인 응답 전문
	 * @param refund			: POS 에서 현금으로 제공해야할 금액
	 * @param isAccepted		: 승인이 난 경우
	 * @return
	 */
	@SuppressWarnings("unused")
	private static String createReceiptByCredit(UPOSMessage_ItemInfo pumpingItemInfo, 
			UPOSMessage uPosMsg, int refund, boolean isAccepted) {
		String formatMsg = "응답 정보는 받았지만, 응답 정보에 에러가 생겨서 Print 정보가 없습니다." ;		
		
		String productName1 = "" ;	// 상품명
		String storeCode = "" ;		// 매장코드
		String date = "" ;			// 날짜(YYYYMMDDHHMMSS)
		String tel = "" ;			// 매장전화번호
		String manager = "" ;		// 담당자
		String wDate = "" ;			// 영업일자
		String odtNo = "" ;			// ODT 번호
		String transactionNo = "" ;	// 거래번호
		String storeName = "" ;		// 매장명
		String representative = "" ;	// 대표자
		String represent = "" ;		// 사업자번호
		String address = "" ;		// 매장주소
		String cardNumber = "" ;	// 카드번호 
		String acceptNo = "" ;		// 승인번호 
		String cardCorpName = "" ;	// 승인회사 
		String sequence = "" ;		// 구분
		String productName2 = "" ;	// 상품명
		String nozzleNo = "" ;		// 노즐번호
		String liter = "" ;			// 주유량
		String basePrice = "" ;		// 단가
		String price = "" ;			// 주유금액
		String productPrice = "" ;	// 공급가액
		String taxPrice = "" ;		// 새액
		String totalPrice = "" ;	// 주유 금액
		String receivePrice = "" ;	// 결제금액
		
		SqlSession session = null;		
		try {
			session = SqlSessionFactoryManager.openSqlSession();

			uPosMsg.print() ;
			
			UPOSMessage_ItemInfo itemInfo = uPosMsg.getItem_info() ;
			if ((itemInfo.getItemInfoList() != null) && (itemInfo.getItemInfoList().size() != 0)) {
				UPOSMessage_ItemInfo_Item itemInfo_item = itemInfo.getItemInfoList().get(0) ;
				String goods_code = itemInfo_item.getGoodsCode() ;
				
				T_KH_PRODUCTData[] productDataArray = 
					T_KH_PRODUCTHandler.getHandler().getT_KH_PRODUCTData(session, goods_code) ;

				if (productDataArray != null) {
					T_KH_PRODUCTData productData = productDataArray[0] ;
					storeCode = T_KH_STOREHandler.getHandler().getStoreCode();
					T_KH_STOREData storeData = 
						T_KH_STOREHandler.getHandler().getT_KH_STOREDataByStoreCode(storeCode) ;
					
					productName1 = productData.getGoods_name() ;
					date = getWDateFormat() ;
					tel = "(" + storeData.getTel_nbr() + ")" ;
					
					manager = storeData.getRep_name() ;				// 담당자
					wDate = getWorkingDateFormat(storeData.getBizhour_date()) ;			// 영업일자
					odtNo = PumpMUtil.getODTNumberFromNozzleNo(uPosMsg.getNozzle_no()) ;					
					transactionNo = uPosMsg.getPosReceipt_no() ;	// 거래번호
					storeName = storeData.getCust_name_disp() ;		// 매장명
					representative = storeData.getRep_name() ;		// 대표자
					represent = storeData.getBizregno_nbr() ;		// 사업자번호
					address = storeData.getBizstoreaddr_txt1()  + " " + storeData.getBizstoreaddr_txt2();	// 매장주소					
					cardNumber = uPosMsg.getCreditCard_no() ;	// 카드번호
					acceptNo = uPosMsg.getCredit_auth_no() ;	// 승인번호
					cardCorpName = uPosMsg.getAcquier_code() + " " + uPosMsg.getAcquier_name() ;	// 승인회사 					
					sequence = "01" ;														// 구분
					productName2 = productData.getGoods_name() ;							// 상품명
					nozzleNo = uPosMsg.getNozzle_no() ;										// 노즐번호
					liter = pumpingItemInfo.getTotalOilAmount() ;							// 주유량
					basePrice = pumpingItemInfo.getUnitPrice() ;							// 단가
					price = pumpingItemInfo.getTotalOilPrice_after_discount() ;				// 주유금액
					productPrice = pumpingItemInfo.getTotalPrice_tax() ;					// 공급가액
					taxPrice = pumpingItemInfo.getTotalTaxPrice() ;							// 새액
					totalPrice = pumpingItemInfo.getTotalOilPrice_after_discount() ;		// 주유금액
					
					if (isAccepted) {
						receivePrice = itemInfo.getTotalOilPrice_after_discount() ;			// 결제금액
					} else {
						receivePrice = "0" ;
					}
				}
			}

		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}
		
		
		GSSelfODTReceiptData receipt = new GSSelfODTReceiptData(GSSelfODTReceiptData.GS_SELF);
		
		receipt.makeDefaultInfo(
				PumpMessageFormat.getHeadPrint(), 
				productName1, 
				manager, 
				wDate, 
				transactionNo, 
				storeName, 
				tel, 
				representative, 
				represent, 
				address, 
				nozzleNo,
				GlobalUtility.getDividedWith1000(liter), 
				GlobalUtility.getDividedWith1000(basePrice), 
				GlobalUtility.getStringValue(price), 
				GlobalUtility.getStringValue(productPrice), 
				GlobalUtility.getStringValue(taxPrice), 
				GlobalUtility.getStringValue(totalPrice), 
				GlobalUtility.getStringValue(receivePrice), 
				GlobalUtility.getStringValue(refund),
				PumpMessageFormat.getFootPrint1(), 
				PumpMessageFormat.getFootPrint2(), 
				date,
				"");
		
		receipt.makeCreditInfo(
				acceptNo, 
				cardNumber, 
				cardCorpName);

		formatMsg = GSSelfODTReceiptMaker.makeReceipt(receipt);
		
		return formatMsg ;
	}
	
	/**
	 * 신용 + 보너스 승인 요청에 대한 응답 전문을 이용하여 다쓰노 ODT 출력 메시지를 생성 요청한다.
	 * 
	 * @param pumpingItemInfo	: 주유 정보
	 * @param uPosMsg			: 승인 응답 전문
	 * @param refund			: POS 에서 현금으로 돌려줘야 할 금액
	 * @param isAccepted		: 승인이 난 경우
	 * @return
	 */
	@SuppressWarnings("unused")
	private static String createReceiptByCreditBonus(UPOSMessage_ItemInfo pumpingItemInfo, 
			UPOSMessage uPosMsg, int refund, boolean isAccepted) {
		String formatMsg = "응답 정보는 받았지만, 응답 정보에 에러가 생겨서 Print 정보가 없습니다." ;		
		
		String productName1 = "" ; 	// 상품명
		String storeCode = "" ; 	// 매장코드
		String date = "" ; 			// 날짜 (YYYYMMDDHHMMSS)
		String tel = "" ; 			// 매장전화번호
		String manager = "" ; 		// 담당자
		String wDate = "" ; 		// 영업일자
		String odtNo = "" ; 		// ODT 번호
		String transactionNo = "" ; // 거래번호
		String storeName = "" ; 	// 매장명
		String representative = "" ;// 대표자
		String represent = "" ; 	// 사업자번호
		String address = "" ; 		// 매장 주소
		String cardNumber = "" ; 	// 카드번호
		String acceptNo = "" ; 		// 승인번호
		String cardCorpName = "" ; 	// 승인회사
		String sequence = "" ; 		// 구분
		String productName2 = "" ; 	// 상품명
		String nozzleNo = "" ; 		// 노즐 번호
		String liter = "" ; 		// 주유량
		String basePrice = "" ; 	// 단가
		String price = "" ; 		// 주유금액
		String productPrice = "" ; 	// 공급가액
		String taxPrice = "" ; 		// 새액
		String totalPrice = "" ; 	// 주유 금액
		String receivePrice = "" ; 	// 결제 금액
		String bonusCardNo = "" ; 	// 카드No
		String authNo = "" ; 		// 승인No
		String createPoint = "" ; 	// 발생
		String usePoint = "" ; 		// 가용
		String totalPoint = "" ; 	// 총누적
		String message = "" ; 		// 메세지
		
		SqlSession session = null;		
		try {
			session = SqlSessionFactoryManager.openSqlSession();

			UPOSMessage_ItemInfo itemInfo = uPosMsg.getItem_info() ;
			if ((itemInfo.getItemInfoList() != null) && (itemInfo.getItemInfoList().size() != 0)) {
				UPOSMessage_ItemInfo_Item itemInfo_item = itemInfo.getItemInfoList().get(0) ;
				String goods_code = itemInfo_item.getGoodsCode() ;
				
				T_KH_PRODUCTData[] productDataArray = 
					T_KH_PRODUCTHandler.getHandler().getT_KH_PRODUCTData(session, goods_code) ;

				if (productDataArray != null) {
					T_KH_PRODUCTData productData = productDataArray[0] ;
					storeCode = T_KH_STOREHandler.getHandler().getStoreCode();
					T_KH_STOREData storeData = 
						T_KH_STOREHandler.getHandler().getT_KH_STOREDataByStoreCode(storeCode) ;
					
					productName1 = productData.getGoods_name() ;
					date = getWDateFormat() ;
					tel = "(" + storeData.getTel_nbr() + ")" ;
					
					manager = storeData.getRep_name() ;				// 담당자
					wDate = getWorkingDateFormat(storeData.getBizhour_date()) ;			// 영업일자
					odtNo = PumpMUtil.getODTNumberFromNozzleNo(uPosMsg.getNozzle_no()) ;					
					transactionNo = uPosMsg.getPosReceipt_no() ;	// 거래번호
					storeName = storeData.getCust_name_disp() ;		// 매장명
					representative = storeData.getRep_name() ;		// 대표자
					represent = storeData.getBizregno_nbr() ;		// 사업자번호
					address = storeData.getBizstoreaddr_txt1()  + " " + storeData.getBizstoreaddr_txt2();	// 매장주소					
					cardNumber = uPosMsg.getCreditCard_no() ;	// 카드번호 
					acceptNo = uPosMsg.getCredit_auth_no() ;	// 승인번호
					cardCorpName = uPosMsg.getAcquier_code() + " " + uPosMsg.getAcquier_name() ;	// 승인회사 					
					sequence = "01" ;		// 구분
					productName2 = productData.getGoods_name() ;	// 상품명
					nozzleNo = uPosMsg.getNozzle_no() ;				// 노즐번호
					liter = pumpingItemInfo.getTotalOilAmount() ;	// 주유량
					basePrice = pumpingItemInfo.getUnitPrice() ;	// 단가
					price = pumpingItemInfo.getTotalOilPrice_after_discount() ;				// 주유금액
					productPrice = pumpingItemInfo.getTotalPrice_tax() ;					// 공급가액
					taxPrice = pumpingItemInfo.getTotalTaxPrice() ;							// 새액
					totalPrice = pumpingItemInfo.getTotalOilPrice_after_discount() ;		// 주유 금액
					if (isAccepted) {
						receivePrice = itemInfo.getTotalOilPrice_after_discount() ;			// 결제금액
					} else {
						receivePrice = "0" ;
					}
					bonusCardNo = uPosMsg.getBonRSCard_no() ;
					authNo = uPosMsg.getBonRSCard_authNo() ;
					createPoint = uPosMsg.getGs_point1() ;
					usePoint =  uPosMsg.getGs_point2() ;
					totalPoint =  uPosMsg.getGs_point3() ;
					message =  uPosMsg.getBonRS_msg() ;
				}
			}

		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}
		
		
		GSSelfODTReceiptData receipt = new GSSelfODTReceiptData(GSSelfODTReceiptData.GS_SELF);
		
		receipt.makeDefaultInfo(
				PumpMessageFormat.getHeadPrint(), 
				productName1, 
				manager, 
				wDate, 
				transactionNo, 
				storeName, 
				tel, 
				representative, 
				represent, 
				address, 
				nozzleNo,
				GlobalUtility.getDividedWith1000(liter), 
				GlobalUtility.getDividedWith1000(basePrice), 
				GlobalUtility.getStringValue(price), 
				GlobalUtility.getStringValue(productPrice), 
				GlobalUtility.getStringValue(taxPrice), 
				GlobalUtility.getStringValue(totalPrice), 
				GlobalUtility.getStringValue(receivePrice), 
				GlobalUtility.getStringValue(refund),
				PumpMessageFormat.getFootPrint1(), 
				PumpMessageFormat.getFootPrint2(), 
				date,
				"");
		
		receipt.makeBonusInfo(
				authNo, 
				bonusCardNo, 
				createPoint, 
				message, 
				totalPoint, 
				usePoint);
		
		receipt.makeCreditInfo(
				acceptNo, 
				cardNumber, 
				cardCorpName);
		
		formatMsg = GSSelfODTReceiptMaker.makeReceipt(receipt);
		
		return formatMsg ;
	}

	/**
	 * 주유완료 금액이 0 원이어서 선승인 취소 이후 영수증 Format
	 * 
	 * @param itemInfo		: 주유완료 자료
	 * @param posMsg		: 선승인 취소 응답 전문
	 * @param refund		: 환불 금액				
	 * @param isAccepted	: 승인 or 거절 여부
	 * @return
	 */
	@SuppressWarnings("unused")
	private static String createReceiptWhenPumping0(UPOSMessage_ItemInfo pumpingItemInfo, 
			UPOSMessage uPosMsg, int refund, boolean isAccepted) {

		String formatMsg = "응답 정보는 받았지만, 응답 정보에 에러가 생겨서 Print 정보가 없습니다." ;		
		
		String productName1 = "" ;		// 상품명
		String storeCode = "" ;			// 매장코드
		String date = "" ;				// 날짜(YYYYMMDDHHMMSS)
		String tel = "" ;				// 매장전화번호
		String manager = "" ;			// 담당자
		String wDate = "" ;				// 영업일자
		String odtNo = "" ;				// ODT 번호
		String transactionNo = "" ;		// 거래번호
		String storeName = "" ;			// 매장명
		String representative = "" ;	// 대표자
		String represent = "" ;			// 사업자번호
		String address = "" ;			// 매장주소
		String cardNumber = "" ;		// 카드번호 
		String acceptNo = "" ;			// 승인번호 
		String cardCorpName = "" ;		// 승인회사 
		String sequence = "" ;			// 구분
		String productName2 = "" ;		// 상품명
		String nozzleNo = "" ;			// 노즐번호
		String liter = "0" ;			// 주유량
		String basePrice = "" ;			// 단가
		String price = "0" ;			// 주유금액
		String productPrice = "0" ;		// 공급가액
		String taxPrice = "0" ;			// 새액
		String totalPrice = "0" ;		// 주유 금액
		String receivePrice = "0" ;		// 결제금액
		
		SqlSession session = null;		
		try {
			session = SqlSessionFactoryManager.openSqlSession();

			uPosMsg.print() ;
			
			UPOSMessage_ItemInfo itemInfo = uPosMsg.getItem_info() ;
			if ((itemInfo.getItemInfoList() != null) && (itemInfo.getItemInfoList().size() != 0)) {
				UPOSMessage_ItemInfo_Item itemInfo_item = itemInfo.getItemInfoList().get(0) ;
				String goods_code = itemInfo_item.getGoodsCode() ;
				
				T_KH_PRODUCTData[] productDataArray = 
					T_KH_PRODUCTHandler.getHandler().getT_KH_PRODUCTData(session, goods_code) ;

				if (productDataArray != null) {
					T_KH_PRODUCTData productData = productDataArray[0] ;
					storeCode = T_KH_STOREHandler.getHandler().getStoreCode();
					T_KH_STOREData storeData = 
						T_KH_STOREHandler.getHandler().getT_KH_STOREDataByStoreCode(storeCode) ;
					
					productName1 = productData.getGoods_name() ;
					date = getWDateFormat() ;
					tel = "(" + storeData.getTel_nbr() + ")" ;
					
					manager = storeData.getRep_name() ;				// 담당자
					wDate = getWorkingDateFormat(storeData.getBizhour_date()) ;			// 영업일자
					odtNo = PumpMUtil.getODTNumberFromNozzleNo(uPosMsg.getNozzle_no()) ;					
					transactionNo = uPosMsg.getPosReceipt_no() ;	// 거래번호
					storeName = storeData.getCust_name_disp() ;		// 매장명
					representative = storeData.getRep_name() ;		// 대표자
					represent = storeData.getBizregno_nbr() ;		// 사업자번호
					address = storeData.getBizstoreaddr_txt1() + " " + storeData.getBizstoreaddr_txt2();	// 매장주소					
					cardNumber = uPosMsg.getCreditCard_no() ;		// 카드번호
//					acceptNo = uPosMsg.getCredit_auth_no() ;		// 승인번호
//					cardCorpName = uPosMsg.getAcquier_code() + " " + uPosMsg.getAcquier_name() ;	// 승인회사 					
//					sequence = "01" ;														// 구분
					productName2 = productData.getGoods_name() ;							// 상품명
					nozzleNo = uPosMsg.getNozzle_no() ;										// 노즐번호
//					liter = pumpingItemInfo.getTotalOilAmount() ;							// 주유량
					basePrice = pumpingItemInfo.getUnitPrice() ;							// 단가
//					price = pumpingItemInfo.getTotalOilPrice_after_discount() ;				// 주유금액
//					productPrice = pumpingItemInfo.getTotalPrice_tax() ;					// 공급가액
//					taxPrice = pumpingItemInfo.getTotalTaxPrice() ;							// 새액
//					totalPrice = pumpingItemInfo.getTotalOilPrice_after_discount() ;		// 주유금액
					
//					receivePrice = "0" ;
				}
			}

		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}
				
		GSSelfODTReceiptData receipt = new GSSelfODTReceiptData(GSSelfODTReceiptData.GS_SELF);
		
		receipt.makeDefaultInfo(
				PumpMessageFormat.getHeadPrint(), 
				productName1, 
				manager, 
				wDate, 
				transactionNo, 
				storeName, 
				tel, 
				representative, 
				represent, 
				address, 
				nozzleNo,
				GlobalUtility.getDividedWith1000(liter), 
				GlobalUtility.getDividedWith1000(basePrice), 
				GlobalUtility.getStringValue(price), 
				GlobalUtility.getStringValue(productPrice), 
				GlobalUtility.getStringValue(taxPrice), 
				GlobalUtility.getStringValue(totalPrice), 
				GlobalUtility.getStringValue(receivePrice), 
				GlobalUtility.getStringValue(refund),
				PumpMessageFormat.getFootPrint1(), 
				PumpMessageFormat.getFootPrint2(), 
				date,
				"");

		formatMsg = GSSelfODTReceiptMaker.makeReceipt(receipt);
		
		return formatMsg ;
	
	}
	
	
	/**
	 * 외상 거래처 응답 영수증 출력
	 * @param itemInfo
	 * @param uPosMsg
	 * @param diffPrice
	 * @param isAccepted
	 * @return
	 */
	@SuppressWarnings("unused")
	private static String createTrustTatsunoFormat(UPOSMessage_ItemInfo itemInfo, UPOSMessage uPosMsg, int diffPrice, boolean isAccepted) {
		String formatMsg = "응답 정보는 받았지만, 응답 정보에 에러가 생겨서 Print 정보가 없습니다." ;	
		
		String productName1		= "";	// 상품명
		String storeCode		= "";	// 매장코드 (사용하지 않음)
		String date				= "";	// 날짜 (YYYYMMDDHHMMSS)
		String tel				= "";	// 매장전화번호
		String manager			= "";	// 담당자
		String wDate			= "";	// 영업일자
		String odtNo			= "";	// ODT 번호
		String transactionNo	= "";	// 거래번호
		String storeName		= "";	// 매장명
		String representative	= "";	// 대표자
		String represent		= "";	// 사업자번호
		String address			= "";	// 매장 주소
		int    limitType		= 0;	// 한도 기준(금액 or 수량)
		String customerCode		= "";	// 거래처 코드
		String customerName 	= "";	// 거래처명
		String customerCardNo 	= "";	// 거래처 카드번호
		String customerCarNo 	= "";	// 거래처 차량 번호 
		String limit 			= "";	// 한도량
		String saveLimit 		= "";	// 누적 사용량
		String remainLimit 		= "";	// 한도 잔량
		String sequence			= "";	// 순번구분
		String productName2		= "";	// 제품명
		String nozzleNo			= "";	// 노즐 번호
		String liter			= "";	// 주유량
		String basePrice		= "0";	// 단가
		String price			= "";	// 주유금액
		String productPrice		= "";	// 과세 물품가액
		String taxPrice			= "";	// 부가세
		String totalPrice		= "";	// 합계
//		String receivePrice		= "0";	// 받은 금액

		
		SqlSession session = null;		
		try {
			session = SqlSessionFactoryManager.openSqlSession();

			if ((itemInfo.getItemInfoList() != null) && (itemInfo.getItemInfoList().size() != 0)) {
				UPOSMessage_ItemInfo_Item itemInfo_item = itemInfo.getItemInfoList().get(0) ;
				String goods_code = itemInfo_item.getGoodsCode() ;
				
				T_KH_PRODUCTData[] productDataArray = 
					T_KH_PRODUCTHandler.getHandler().getT_KH_PRODUCTData(session, goods_code) ;

				if (productDataArray != null) {
					T_KH_PRODUCTData productData = productDataArray[0] ;
					storeCode = T_KH_STOREHandler.getHandler().getStoreCode();		//매장코드
					T_KH_STOREData storeData = 
						T_KH_STOREHandler.getHandler().getT_KH_STOREDataByStoreCode(storeCode) ;
					
					productName1 = productData.getGoods_name() ;			// 상품명
					date = getWDateFormat() ;								// 날짜
					tel = "(" + storeData.getTel_nbr() + ")" ;				// 매장전화번호
					
					manager = storeData.getRep_name() ;						// 담당자
					wDate = getWorkingDateFormat(storeData.getBizhour_date()) ;			// 영업일자
					odtNo = PumpMUtil.getODTNumberFromNozzleNo(uPosMsg.getNozzle_no()) ;	//odt번호					
					transactionNo = uPosMsg.getPosReceipt_no() ;	// 거래번호
					storeName = storeData.getCust_name_disp() ;		// 매장명
					representative = storeData.getRep_name() ;		// 대표자
					represent = storeData.getBizregno_nbr() ;		// 사업자번호
					address = storeData.getBizstoreaddr_txt1()  + " " + storeData.getBizstoreaddr_txt2();	// 매장주소					
					
					POSHeader posMsg = PumpMODTSaleManager.getCustPOSPumpM(itemInfo_item.getNozzleNo()) ;
					POS_DW dwPumpM = null ;
					if ((posMsg != null) && (posMsg instanceof POS_DW))
						dwPumpM = (POS_DW) posMsg ;
					
					if ("01".equals(dwPumpM.getAdjbase_code_limit()))
						limitType = PumpMessageFormat.LITER_LIMIT_TYPE;
					else
						limitType = PumpMessageFormat.PRICE_LIMIT_TYPE;		// 한도적용 기준
					
					customerCode = dwPumpM.getCust_code();			// 거래처코드
					customerName = dwPumpM.getDrive_name();			// 거래처 명
					customerCardNo = dwPumpM.getCust_card_no();		// 거래처 카드번호
					customerCarNo = dwPumpM.getCar_no();			// 거래처 차량 번호
					limit = dwPumpM.getLimit();						// 한도량
					if ( "01".equals(dwPumpM.getCardadj_ind())){
						saveLimit = "0";
						remainLimit = "0";
					} else {
						if (limitType == PumpMessageFormat.LITER_LIMIT_TYPE) {
							saveLimit = String.valueOf(Double.parseDouble(dwPumpM.getAccLimit()) 
										+ Double.parseDouble(PumpMUtil.convertNumberFormatFromPumpToPOS(itemInfo_item.getOilAmount(),3)));				// 누적 사용량 
							remainLimit = String.valueOf(Double.parseDouble(limit) - Double.parseDouble(saveLimit));			// 한도 잔량
						} else {
							saveLimit = String.valueOf(Double.parseDouble(dwPumpM.getAccLimit()) 
										+ Double.parseDouble(itemInfo_item.getOilPrice_after_discount()));				// 누적 사용량 
							remainLimit = String.valueOf(Double.parseDouble(limit) - Double.parseDouble(saveLimit));			// 한도 잔량
							
						}
					}
					
					
					saveLimit = GlobalUtility.getValueByCertainDecimal(saveLimit, 3);
					remainLimit = GlobalUtility.getValueByCertainDecimal(remainLimit, 3);
					sequence = "01" ;		// 구분
					productName2 = productData.getGoods_name() ;	// 상품명
					nozzleNo = uPosMsg.getNozzle_no() ;				// 노즐번호
					liter = PumpMUtil.convertNumberFormatFromPumpToPOS(itemInfo.getTotalOilAmount(),3) ;	// 주유량
					
					if ("1".equals(dwPumpM.getRcptunitprc_ind_prt()) )
						basePrice = PumpMUtil.convertNumberFormatFromPumpToPOS(itemInfo.getUnitPrice(),3) ;	// 단가
					price = itemInfo.getTotalOilPrice_after_discount() ;				// 주유금액
					productPrice = itemInfo.getTotalPrice_tax() ;					// 공급가액
					taxPrice = itemInfo.getTotalTaxPrice() ;							// 새액
					totalPrice = itemInfo.getTotalOilPrice_after_discount() ;		// 주유 금액

				}
			}

		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}
		
		
		GSSelfODTReceiptData receipt = new GSSelfODTReceiptData(GSSelfODTReceiptData.GS_SELF);
		
		receipt.makeDefaultInfo(
				PumpMessageFormat.getHeadPrint(), 
				productName1, 
				manager, 
				wDate, 
				transactionNo, 
				storeName, 
				tel, 
				representative, 
				represent, 
				address, 
				nozzleNo,
				liter, 
				basePrice, 
				price, 
				productPrice, 
				taxPrice, 
				totalPrice, 
				price, 
				"0",
				PumpMessageFormat.getFootPrint1(), 
				PumpMessageFormat.getFootPrint2(), 
				date,
				"");
		
		receipt.makeCustomerInfo(
				customerCardNo, 
				customerCarNo, 
				customerCode, 
				limit, 
				customerName, 
				remainLimit, 
				saveLimit);

		formatMsg = GSSelfODTReceiptMaker.makeReceipt(receipt);
		
		return formatMsg ;
		
	}
	
	/**
	 * 실패 한 경우의 Format 을 만들어야 한다.
	 * 현재는 거스름돈 마이너스를 이용하여 적는다.
	 * 
	 * @param nozzleNo			: 노즐 번호
	 * @param khproc_no			: KH 처리번호
	 * @param pumpingPrice		: 주유 금액
	 * @param pumpingLiter		: 주유량
	 * @param payedPrice		: 결재 금액
	 * @param pumpingBasePrice	: 주유 단가
	 * @param uPosMsg			: 결재 요청에 대한 응답 전문 UPOSMessage
	 * @return
	 */
	public static WorkingMessage getQL_WorkingMessage_GSCSELFFromODT(String nozzleNo, String khproc_no, 
			String pumpingPrice, String pumpingLiter, String payedPrice, String pumpingBasePrice, UPOSMessage uPosMsg) {
		QL_WorkingMessage qlWorkingMsg = null ;
		
		String odtNo = null ;
		String nozzle_no = uPosMsg.getNozzle_no() ;
		String receipt = null ;
		String length = "0" ;
		String mode = "1";		//pumpadapter ql전문에 넣을 주유완료 여부 1: 주유완료, 0: 취소
		
		int payedPriceInt = 0 ;
		boolean isAccepted = true ;
		
		String ledCode = uPosMsg.getLed_code() ;

		// 결재 금액은 승인이 난 경우에만 의미가 있도록 한다.
		if ("1".equals(ledCode)) {
			// 승인이 난 경우
			payedPriceInt = Integer.parseInt(payedPrice) ;
			isAccepted = true ;
		} else {
			// 승인이 나지 않은 경우
			payedPriceInt = 0 ;
			isAccepted = false ;
		}

		int pumpingPriceInt = Integer.parseInt(pumpingPrice) ;
		
		UPOSMessage_ItemInfo itemInfo = UPOSUtil.getUPOSMessage_ItemInfo(nozzleNo, khproc_no,
				pumpingLiter, pumpingBasePrice, pumpingPrice) ;
		
		int option = DasNoSelfPumpingManager.getInstance().getCurrentOption(nozzle_no) ;
		
		/**
		 * 주유완료가 0원이고, 선승인에 대한 취소가 제대로 된 경우 createReceiptWhenPumping0 
		 * 함수를 호출하여 영수증 전문을 생성한다. 
		 * 따라서 영수증에 나오는 전문은 주유금액 0 원, 결재 금액 0으로 나오도록 한다.
		 */
		if ((option == IConstant.FULL_PUMPING_OPTION_5) || (option == IConstant.FULL_PUMPING_OPTION_7)) {
		// 주유완료 금액이 0 원인 경우 
			receipt = createReceiptWhenPumping0(itemInfo, uPosMsg, 0, isAccepted) ;
			length = Integer.toString(receipt.length()) ;
		} else {
		// diffPrice 가 양수인 경우는 사용자가 받아야 할 금액이고, 음수인 경우는 주유소가 사용자로 부터 받아야 할 금액이다.
		// 음수가 나온다라는 자체는 문제가 있으며, 음수가 안나오도록 코드 구현을 개선해야 한다.
			int diffPrice = payedPriceInt - pumpingPriceInt ;
			
			int messageType = Integer.parseInt(uPosMsg.getMessageType()) ;
			switch (messageType) {
				case IUPOSConstant.MESSAGETYPE_INT_0004 :
					//보너스 누적응답
				{
					POSHeader posMsg = PumpMODTSaleManager.getCustPOSPumpM(nozzleNo) ;
					POS_DW dwPumpM = null ;
					if ((posMsg != null) && (posMsg instanceof POS_DW))
						dwPumpM = (POS_DW) posMsg ;
					
					if (dwPumpM == null)
						receipt = createCachWithBonusTatsunoFormat(itemInfo, uPosMsg, diffPrice, isAccepted) ;
					else
						receipt = createCustomerTatsunoFormat(itemInfo, uPosMsg, diffPrice, isAccepted) ;
					
					length = Integer.toString(receipt.length()) ;
					break;
				}
				case IUPOSConstant.MESSAGETYPE_INT_0012 :
					//현금 응답
				{
					if ( Integer.parseInt(pumpingPrice) == 0)
						mode = "0";
					
					POSHeader posMsg = PumpMODTSaleManager.getCustPOSPumpM(nozzleNo) ;
					POS_DW dwPumpM = null ;
					if ((posMsg != null) && (posMsg instanceof POS_DW))
						dwPumpM = (POS_DW) posMsg ;
					
					if (dwPumpM == null)
						receipt = createReceiptByCash(itemInfo, uPosMsg, diffPrice, isAccepted) ;
					else
						receipt = createCustomerTatsunoFormat(itemInfo, uPosMsg, diffPrice, isAccepted) ;
					
					length = Integer.toString(receipt.length()) ;
					break;
				}
				case IUPOSConstant.MESSAGETYPE_INT_0014 :
					//현금 + 보너스 응답
				{
					POSHeader posMsg = PumpMODTSaleManager.getCustPOSPumpM(nozzleNo) ;
					POS_DW dwPumpM = null ;
					if ((posMsg != null) && (posMsg instanceof POS_DW))
						dwPumpM = (POS_DW) posMsg ;
					
					if (dwPumpM == null)
						receipt = createCachWithBonusTatsunoFormat(itemInfo, uPosMsg, diffPrice, isAccepted) ;
					else
						receipt = createCustomerTatsunoFormat(itemInfo, uPosMsg, diffPrice, isAccepted) ;
					
					length = Integer.toString(receipt.length()) ;
					break;
				}
				case IUPOSConstant.MESSAGETYPE_INT_0016 :
					//국세청현금영수증 응답
				case IUPOSConstant.MESSAGETYPE_INT_0054 : 
					//국세청 현금 영수증 + 보너스 응답
				{
					
					receipt = createCustomerTatsunoFormat(itemInfo, uPosMsg, diffPrice, isAccepted) ;
					
					length = Integer.toString(receipt.length()) ;
					break;
				}
				case IUPOSConstant.MESSAGETYPE_INT_0046 :
				case IUPOSConstant.MESSAGETYPE_INT_0032 : { 
					// 신용카드 승인 응답
					POSHeader posMsg = PumpMODTSaleManager.getCustPOSPumpM(nozzleNo) ;
					POS_DW dwPumpM = null ;
					if ((posMsg != null) && (posMsg instanceof POS_DW))
						dwPumpM = (POS_DW) posMsg ;
					
					if (dwPumpM == null)
						receipt = createReceiptByCredit(itemInfo, uPosMsg, diffPrice, isAccepted) ;
					else
						receipt = createCustomerTatsunoFormat(itemInfo, uPosMsg, diffPrice, isAccepted) ;
					length = Integer.toString(receipt.length()) ;
					break ;
				}
				case IUPOSConstant.MESSAGETYPE_INT_0048 :	
				case IUPOSConstant.MESSAGETYPE_INT_0034 : { 
					// 신용카드 + 보너스 승인 응답
					POSHeader posMsg = PumpMODTSaleManager.getCustPOSPumpM(nozzleNo) ;
					POS_DW dwPumpM = null ;
					if ((posMsg != null) && (posMsg instanceof POS_DW))
						dwPumpM = (POS_DW) posMsg ;
					
					if (dwPumpM == null)
						receipt = createReceiptByCreditBonus(itemInfo, uPosMsg, diffPrice, isAccepted) ;
					else
						receipt = createCustomerTatsunoFormat(itemInfo, uPosMsg, diffPrice, isAccepted) ;
					
					length = Integer.toString(receipt.length()) ;
					break ;
				}
				case IUPOSConstant.MESSAGETYPE_INT_0072 : { 
					// 신용카드 확인 응답 --> 셀프용 -> 발생할 일이 없음. 발생하면 코드의 문제임.
					LogUtility.getPumpMLogger().warn("[Pump M] This Log shouldn't happen. Please check code again.") ;
					receipt = createReceiptByCredit(itemInfo, uPosMsg, diffPrice, isAccepted) ;
					length = Integer.toString(receipt.length()) ;
					break ;
				}
				case IUPOSConstant.MESSAGETYPE_INT_0082 : { 
					// 외상거래처 응답. --> 셀프용 
					receipt = createTrustTatsunoFormat(itemInfo, uPosMsg, diffPrice, isAccepted) ;
					length = Integer.toString(receipt.length()) ;
					break ;
				}
			}		
		}
		
		odtNo = PumpMUtil.getODTNumberFromNozzleNo(nozzle_no) ;	
		//qlWorkingMsg = new QL_WorkingMessage(odtNo, nozzle_no, length, receipt, "1") ; //ORG
		qlWorkingMsg = new QL_WorkingMessage(odtNo, nozzle_no, length, receipt, mode, "", "") ;
		
		//주유후 영수증에 세차기OPT 세차할인권 인쇄 
		
		qlWorkingMsg.setBarCode(Barcode.getBarcodeNumber("3", pumpingPrice, nozzleNo, khproc_no, uPosMsg.getMessageType(), ledCode, null));
		
		return qlWorkingMsg ;
	}
	
	/**
	 * 다쓰노 ODT 에게 영수증 출력을 위한 WorkingMessage 생성을 요청한다. 이 Method 는 POS 로 부터의 현금 결재로 인한
	 * 주유시 사용된다.
	 * 
	 * @param nozzleNo			: 노즐 번호
	 * @param khproc_no			: KH 처리번호
	 * @param pumpingPrice		: 주유 금액
	 * @param pumpingLiter		: 주유 량
	 * @param payedPrice		: 결재 금액
	 * @param pumpingBasePrice	: 주유 단가
	 * @return
	 */
	public static WorkingMessage getQL_WorkingMessage_GSCSELFFromPOS(String nozzleNo, String khproc_no, 
			String pumpingPrice, String pumpingLiter, String payedPrice, String pumpingBasePrice) {
		QL_WorkingMessage qlWorkingMsg = null ;
		
		String odtNo = null ;
		String nozzle_no = nozzleNo ;
		String receipt = null ;
		String length = "0" ;

		receipt = createReceiptByCash(nozzleNo, khproc_no, pumpingPrice, pumpingLiter, payedPrice, pumpingBasePrice) ;
		length = Integer.toString(receipt.length()) ;
		
		odtNo = PumpMUtil.getODTNumberFromNozzleNo(nozzle_no) ;
		//qlWorkingMsg = new QL_WorkingMessage(odtNo, nozzle_no, length, receipt, "1") ; //ORG
		qlWorkingMsg = new QL_WorkingMessage(odtNo, nozzle_no, length, receipt, "1", "", "") ;
				
		//주유후 영수증에 세차기OPT 세차할인권 인쇄 
		qlWorkingMsg.setBarCode(Barcode.getBarcodeNumber("3", pumpingPrice, nozzle_no, khproc_no, null, null, null));
		
		return qlWorkingMsg ;
	}
	
	/**
	 * 영수증에 출력하기 위한 날짜
	 * 
	 * @return
	 */
	private static String getWDateFormat() {
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss") ;
		return formater.format(new Date()) ;	
	}
	
	/**
	 * 영수증에 출력하기 위한 영업일
	 * 
	 * @param workDate
	 * @return
	 */
	private static String getWorkingDateFormat(String workDate) {
		String year =  "" ;
		String month = "" ;
		String day = "" ;
		try {
			year = workDate.substring(0,4) ;
			month = workDate.substring(4,6) ;
			day = workDate.substring(6,8) ;
		} catch (Exception e) {
			SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd") ;
			return formater.format(new Date()) ;	
		}
		return year + "-" + month + "-" + day ;
	}
	
	public static void main(String[] args) {
		System.out.println(getWDateFormat()) ;
	}
	
	/**
	 * UPOSMessage 응답 전문을 Sale M 으로 보낼지를 판단한다.
	 * 
	 * @param uPosMsg	: UPOSMessage 응답 전문
	 * @return
	 */
    public static boolean shouldSendToSaleM(UPOSMessage uPosMsg) {
    	boolean rlt = true ;

    	return rlt ;	
    }
}
