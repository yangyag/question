package com.gsc.kixxhub.module.pumpm.pump.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.ibatis.session.SqlSession;

import com.gsc.kixxhub.common.data.ICode;
import com.gsc.kixxhub.common.data.IConstant;
import com.gsc.kixxhub.common.data.IUPOSConstant;
import com.gsc.kixxhub.common.data.posdata.POSHeader;
import com.gsc.kixxhub.common.data.posdata.POS_DW;
import com.gsc.kixxhub.common.data.pump.HE_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.QL_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.QM_WoringMessage;
import com.gsc.kixxhub.common.data.pump.TR_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.data.pump.format.PumpMessageFormat;
import com.gsc.kixxhub.common.data.upos.CreateUPOSMessage;
import com.gsc.kixxhub.common.data.upos.UPOSMessage;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_CampInfo;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_ItemInfo;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_ItemInfo_Item;
import com.gsc.kixxhub.common.dbadapter.common.SqlSessionFactoryManager;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_CODEMASTERHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_PRODUCTHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_PUMP_TRHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_STOREHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_PRODUCTData;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_PUMP_TRData;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_STOREData;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.module.pumpm.pump.data.odt.DasNoODTNozzleInfo;
import com.gsc.kixxhub.module.pumpm.pump.manager.PumpMODTSaleManager;
import com.gsc.kixxhub.module.pumpm.pump.manager.PumpMTransactionManager;
import com.gsc.kixxhub.module.pumpm.pump.manager.dasno.DasNoSelfPumpingManager;

/**
 * 
 * @author WooChul Jung (2016.04.04)
 * 	다쓰노 방식의 결제 프로세스는 다음과 같이 처리된다.
 * 		0) 전제 사항
 * 			(1) 최초 승인시점에서, HE_WorkingMessage 와 그에 대응하는 UPOSMessage 응답 전문을 저장함.
 * 			(2) 주유완료 이후 미만 주유 (0원 주유 및 가득주유의 BL 포함) 에 따라서
 * 				재승인 받아야 할 정보는, 주유완료 (금액, Liter) 와 최초 HE 전문을 이용하여 구성
 * 				취소해야 할 정보는, 응답 UPOSMessage 를 이용하여 구성
 * 			(3) 재승인 혹은 취소 전문 요청 이후, 다른 전문은 pending 변수에 저장 (0원 주유, 가득 주유의 BL 체크시에는 pending 변수에 저장하지 않음)
 * 			(4) CAT M 으로 부터 응답 수신 이후, pending 변수 존재시, 그 전문을 그대로 CAT M 으로 재전송하고, pending 변수를 null 로 처리함.
 * 			(5) 모든 결제 완료시, QL (영수증) 전문을 생성하여 Pump A 로 전송
 * 	
 * 		1) 선결제
 * 			(1) HE_WorkingMessage -> uPOS Message 로 변환
 * 			(2) HE_WorkingMessage (heOrg) 를 저장 (향후 재승인을 위해서 임시로 보관)
 * 			(3) uPOS (respondUPOS) 응답 전문 수신 및 저장
 * 			(4) uPOS 응답 전문을 QM_WorkingMessage 로 변환하여 Pump A 로 전
 * 		2) 주유완료 이후
 * 			(1) 0원 주유인 경우 : respondUPOS 를 이용하여 취소 uPOS Message 를 생성하여 CAT M 으로 전송
 * 			(2) 미만 주유인 경우 : 
 * 				a. heOrg 를 이용하여 승인 UPOSMessage 를 생성
 * 				b. respondUPOS 를 이용하여 취소 UPOSMessage 를 생성하여 pendingUPOS 변수에 설정
 * 				c. 승인 UPOSMessage 를 CAT M 로 전송
 * 				d. CAT M 으로 부터 응답 UPOSMessage 를 수신하면, pendingUPOS 변수가 있는지 확인하고, 있으면 CAT M 로 재전송
 * 			(3) 일반 주유인 경우 : 영수증 (QL) 전문 생성하여 Pump A 로 전송
 * 
 *
 */
public class ODTUtility_DaSNo {


	/**
	 * BL 체크 전문을 생성한다.
	 * 
	 * @param workingMsg	: 주유기 Adapter 로 부터 올라온 HE 전문
	 * @return
	 */
	public static UPOSMessage create0071UPOSMessageFromWorkingMessage_DaSNo(WorkingMessage workingMsg) {
		HE_WorkingMessage heWorkingMsg = (HE_WorkingMessage) workingMsg ;
		
		String creditCard_no = heWorkingMsg.getCardNumber() ; 
		String nozzle_no = heWorkingMsg.getConnectNozzleNo() ;
		String payment_amt = heWorkingMsg.getPrice();
		String pos_ip = UPOSUtil.getPOSIP() ; 
		String pos_port = UPOSUtil.getPOSPort() ; 
		String pos_saleDate = UPOSUtil.getPosSaleDate() ; 

//		여전법 대응에 따른 UposMessage 추가, 2015.11.19 - cwi 
		String creditCardReading_type = IConstant.CreditCardReading_Type_MAGNETIC ;
		String chipData = "";
		String certification_id = "";
		String signImage_Info = "";
		String signImage_Data = "";
		String term_ID = ODTUtility_Common.getTermId() ;
		String store_CD = ODTUtility_Common.getStoreCode();
		String encryptCredit_no = "";
		String creditPassCode = "";
		String selfPayment_type = "" ; // 2016.03.31 WooChul Jung. 확인 필요. 어느 시점에서 Setting 할 것인지.
		String payment_Tax = GlobalUtility.getTaxPrice(payment_amt) ;
		String charge = "";
		String credit_Round = "";
		String trx_No = ODTUtility_Common.getTrxNo();
		String trx_Seq = ODTUtility_Common.getTrxSeq();
		String term_Ver = ODTUtility_Common.getRomVer();
		String rTrade_Yn = "=";
		String coupon_Trade_Type = "0";
		String coupon_Acquier_Type = "";
		String term_Res_Code = "00";
		String txt_Direction = "0000";
		String fallback_Trx_Reason = "00";
		String maskingCardNo = PumpMessageFormat.getPrintFormatCardNumber(creditCard_no, true).replaceAll("-", "") ;
		
		return 	CreateUPOSMessage.createUPOSMessage_0071(	IUPOSConstant.DEVICE_TYPE_3S, 
															nozzle_no,
															maskingCardNo, 
															payment_amt, 
															pos_ip, 
															pos_port, 
															pos_saleDate,
															"",
															creditCardReading_type ,
															chipData ,
															certification_id ,
															signImage_Info ,
															signImage_Data ,
															term_ID ,
															store_CD ,
															creditCard_no ,
															creditPassCode ,
															selfPayment_type ,
															payment_Tax ,
															charge ,
															credit_Round ,
															"",
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
	 * 현금 + 보너스
	 * @param itemInfo
	 * @param uPosMsg
	 * @param diffPrice
	 * @param isAccepted
	 * @return
	 */
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
		int option 					= 0;	// 가득주유 타입
		
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
					
					bonusCardNo		= uPosMsg.getBonRSCard_no();	// 보너스카드 번호
					authNo				= uPosMsg.getBonRSCard_authNo();	// 보너스카드 승인 번호
					createPoint			= uPosMsg.getGs_point1();	// 발생 포인트
					usePoint			= uPosMsg.getGs_point2();	// 가용 포인트
					totalPoint			= "0";	// 주유소 포인트
					message			= uPosMsg.getBonRS_msg();	// 보너스카드 메세지
					option 				= DasNoSelfPumpingManager.getInstance().getCurrentOption(nozzleNo) ;
				}
			}

		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}	
		
		formatMsg = PumpMessageFormat.createCachWithBonusTatsunoFormat(	productName1		
												,date					
												,tel					
												,manager				
												,wDate				
												,odtNo				
												,transactionNo		
												,storeName			
												,representative		
												,represent			
												,address	 		
												,sequence				
												,productName2			
												,nozzleNo				
												,liter				
												,basePrice			
												,price				
												,productPrice			
												,taxPrice				
												,totalPrice			
												,receivePrice			
												,refund				
												,bonusCardNo			
												,authNo				
												,createPoint			
												,usePoint				
												,totalPoint			
												,message	
												,option) ;
		return formatMsg ;
	}
	
	/**
	 * 
	 * 취소 UPOSMessage 를 생성한다. 이는 가장 최근의 승인 전문을 이용하여 취소 전문을 생성한다.
	 * 
	 * @param heWorkingMsg		: HE 전문 (Pump A 로 부터의 최초 요청전문)
	 * @param khproc_no			: KH 처리번호
	 * @return
	 */
	public static UPOSMessage createCancelUPOSMessageFromWorkingMessage_DaSNo(HE_WorkingMessage heWorkingMsg, 
			String khproc_no) {
		LogUtility.getLogger().info("[Pump M] Create Cancel UPOSMessage for DasNo.khproc_no=" + khproc_no) ;
				
		UPOSMessage uPosMsg = null ;
		try {
			UPOSMessage preUPOSMsg = null ;
			DasNoODTNozzleInfo dasNoInfo = DasNoSelfPumpingManager.getInstance().
				getDasNoODTNozzleInfo(heWorkingMsg.getConnectNozzleNo()) ;
						
			if (dasNoInfo != null) {
				preUPOSMsg = dasNoInfo.getFirstRespondUPOSMsg() ;
				String messageType = preUPOSMsg.getMessageType() ;
				int messageTypeInt = Integer.parseInt(messageType) ;

/*				LogUtility.getLogger().info("\n\n#### createCancelUPOSMessageFromWorkingMessage_DaSNo()");
				LogUtility.getLogger().info("messageType=====" + messageType);*/
				
				String card_number = heWorkingMsg.getCardNumber();
				String maskingCardNo = preUPOSMsg.getCreditCard_no().replaceAll("-", "") ;
				
				switch (messageTypeInt) {
					case IUPOSConstant.MESSAGETYPE_INT_0032 : {
						LogUtility.getLogger().info("[Pump M] Create Cancel Credit -> UPOSMessage(8031)");
						uPosMsg = CreateUPOSMessage.createUPOSMessage_8031(	IUPOSConstant.DEVICE_TYPE_3S, 
																			preUPOSMsg.getPosReceipt_no() ,
																			preUPOSMsg.getNozzle_no() ,
																			preUPOSMsg.getItem_info(),
																			null,
																			maskingCardNo,
																			preUPOSMsg.getTrdate_creditCard() ,
																			"0",
																			preUPOSMsg.getCredit_auth_no() ,
																			preUPOSMsg.getPayment_amt() ,
																			UPOSUtil.getPOSIP(),
																			UPOSUtil.getPOSPort(),
																			UPOSUtil.getPosSaleDate(), 
																			null,
																			null,
																			preUPOSMsg.getCreditCardReading_type(),
																			preUPOSMsg.getChipData(),
																			preUPOSMsg.getCertification_id(),
																			preUPOSMsg.getSignImage_Info(),
																			preUPOSMsg.getSignImage_Data(),
																			preUPOSMsg.getTerm_id(),
																			preUPOSMsg.getStore_cd(),
																			card_number.getBytes(),
																			preUPOSMsg.getCreditPassCode(),
																			preUPOSMsg.getSelfPayment_type(),
																			preUPOSMsg.getPayment_tax(),
																			preUPOSMsg.getCharge(),
																			preUPOSMsg.getCredit_Round(),
																			"",
																			preUPOSMsg.getTrx_No(),
																			preUPOSMsg.getTrx_Seq(),
																			preUPOSMsg.getTerm_Ver(),
																			preUPOSMsg.getRTrade_Yn(),
																			preUPOSMsg.getCoupon_Trade_Type() ,
																			preUPOSMsg.getCoupon_Acquier_Type() ,
																			preUPOSMsg.getTerm_Res_Code(),
																			preUPOSMsg.getTxt_Direction(),
																			preUPOSMsg.getFallback_Trx_Reason()) ;
						break ;
					}
					case IUPOSConstant.MESSAGETYPE_INT_0034 : {		
						LogUtility.getLogger().info("[Pump M] Create Cancel Credit + Bonus -> UPOSMessage(8033)");
						uPosMsg = CreateUPOSMessage.createUPOSMessage_8033(	IUPOSConstant.DEVICE_TYPE_3S, 
																			preUPOSMsg.getPosReceipt_no(),
																			preUPOSMsg.getNozzle_no() ,
																			preUPOSMsg.getItem_info(),
																			null,
																			maskingCardNo,
																			preUPOSMsg.getTrdate_creditCard() ,
																			preUPOSMsg.getCredit_auth_no() ,
																			preUPOSMsg.getBonRSCard_no(),
																			preUPOSMsg.getTrdate_bonRSCard(),
																			preUPOSMsg.getBonRSCard_ID(),
																			preUPOSMsg.getBonRSCard_authNo(),
																			preUPOSMsg.getPayment_amt() ,
																			UPOSUtil.getPOSIP(),
																			UPOSUtil.getPOSPort(),
																			UPOSUtil.getPosSaleDate(), 
																			null,
																			null,
																			preUPOSMsg.getCreditCardReading_type(),
																			preUPOSMsg.getChipData(),
																			preUPOSMsg.getCertification_id(),
																			preUPOSMsg.getSignImage_Info(),
																			preUPOSMsg.getSignImage_Data(),
																			preUPOSMsg.getTerm_id(),
																			preUPOSMsg.getStore_cd(),
																			card_number.getBytes(),
																			preUPOSMsg.getCreditPassCode(),
																			preUPOSMsg.getSelfPayment_type(),
																			preUPOSMsg.getPayment_tax(),
																			preUPOSMsg.getCharge(),
																			preUPOSMsg.getCredit_Round(),
																			"",
																			preUPOSMsg.getTrx_No(),
																			preUPOSMsg.getTrx_Seq(),
																			preUPOSMsg.getTerm_Ver(),
																			preUPOSMsg.getRTrade_Yn(),
																			preUPOSMsg.getCoupon_Trade_Type() ,
																			preUPOSMsg.getCoupon_Acquier_Type() ,
																			preUPOSMsg.getTerm_Res_Code(),
																			preUPOSMsg.getTxt_Direction(),
																			preUPOSMsg.getFallback_Trx_Reason()) ;	
						break ;
					}
//					 tatsuno_hs okdhp7 (2012.12) - GS포인트 결제 취소 
					case IUPOSConstant.MESSAGETYPE_INT_0062 : {	
						
						String payPrice = GlobalUtility.getPositiveValue(heWorkingMsg.getPrice()); 
						
						//---- (GS포인트 사용 취소 요청)
						LogUtility.getLogger().info("[Pump M] Create Cancel GS Bonus Card -> UPOSMessage(8061).payPrice=" + payPrice);
//						LogUtility.getLogger().info("payPrice====" + payPrice);
												
						uPosMsg = CreateUPOSMessage.createUPOSMessage_8061 (IUPOSConstant.DEVICE_TYPE_3S,
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
																			preUPOSMsg.getCreditCardReading_type(),
																			preUPOSMsg.getChipData(),
																			preUPOSMsg.getCertification_id(),
																			preUPOSMsg.getSignImage_Info(),
																			preUPOSMsg.getSignImage_Data(),
																			preUPOSMsg.getTerm_id(),
																			preUPOSMsg.getStore_cd(),
																			card_number.getBytes(),
																			preUPOSMsg.getCreditPassCode(),
																			preUPOSMsg.getSelfPayment_type(),
																			preUPOSMsg.getPayment_tax(),
																			preUPOSMsg.getCharge(),
																			preUPOSMsg.getCredit_Round(),
																			"",
																			preUPOSMsg.getTrx_No(),
																			preUPOSMsg.getTrx_Seq(),
																			preUPOSMsg.getTerm_Ver(),
																			preUPOSMsg.getRTrade_Yn(),
																			"",
																			"",
																			"",
																			"",
																			"");
						break ;
					}
				}
			}
		} catch (Exception e) {
			LogUtility.getLogger().error(e.getMessage(), e) ;
		}
		return uPosMsg ;
	}
		
	/**
	 * 현금거래처 결제 or 대표거래처 현금영수증
	 */
	private static String createCustomerTatsunoFormat(UPOSMessage_ItemInfo itemInfo, UPOSMessage uPosMsg, int diffPrice, boolean isAccepted) {
		String formatMsg = "응답 정보는 받았지만, 응답 정보에 에러가 생겨서 Print 정보가 없습니다." ;	
		
		String productName1					= "";	// 상품명
		String storeCode					= "";	// 매장코드 (사용하지 않음)
		String date							= "";	// 날짜 (YYYYMMDDHHMMSS)
		String tel							= "";	// 매장전화번호
		String manager						= "";	// 담당자
		String wDate						= "";	// 영업일자
		String odtNo						= "";	// ODT 번호
		String transactionNo				= "";	// 거래번호
		String storeName					= "";	// 매장명
		String representative				= "";	// 대표자
		String represent					= "";	// 사업자번호
		String address						= "";	// 매장 주소
		String customerCode					= "";	// 거래처 코드
		String customerName 				= "";	// 거래처명
		String customerCardNo 				= "";	// 거래처 카드번호
		String customerCarNo 				= "";	// 거래처 차량 번호 
		String limit 						= "";	// 한도량
		String saveLimit 					= "";	// 누적 사용량
		String remainLimit 					= "";	// 한도 잔량
		String cardNumber   				= "";	// 신용카드번호
		String acceptNo     				= "";	// 신용카드 승인번호
		String cardCorpName 				= "";	// 신용카드 승인회사
		String sequence						= "";	// 순번구분
		String productName2					= "";	// 제품명
		String nozzleNo						= "";	// 노즐 번호
		String liter						= "";	// 주유량
		String basePrice					= "";	// 단가
		String price						= "";	// 주유금액
		String productPrice					= "";	// 과세 물품가액
		String taxPrice						= "";	// 부가세
		String totalPrice					= "";	// 합계
		String receivePrice					= "";	// 받은 금액
	 	String refund						= "";	// 환불 금액 
		String bonusCardNo					= "";	// 보너스카드 번호
		String authNo						= "";	// 보너스카드 승인 번호
		String createPoint					= "";	// 발생 포인트
		String usePoint						= "";	// 가용 포인트
		String totalPoint					= "";	// 주유소 포인트
		String message						= "";	// 보너스카드 메세지
		String titleMessage	 				= "";	// 현금영수증 거래구분 // tatsuno_hs okdhp7 (2013.03) 추가
		String cachReceiptNo	 			= "";	// 현금영수증 인증번호
		String cachReceiptAuthNo 			= "";	// 현금영수증 승인번호
		String cachReceiptMessage 			= "";	// 현금영수증 메세지
		int option = 0; 	// 가득주유 타입

		boolean isCreditCard = false;		// 신용카드 여부

		// 2013.05.20 ksm 할부개월수
		String monthCnt = "00";
//		 2011.09.07 ksm 선승인 금액 저장 추가
		String preReceivePrice = "0";	// 선승인금액
		
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
					date = getWDateFormat() ;													// 날짜
					tel = "(" + storeData.getTel_nbr() + ")" ;								// 매장전화번호
					
					manager = storeData.getRep_name() ;								// 담당자
					wDate = getWorkingDateFormat(storeData.getBizhour_date()) ;			// 영업일자
					odtNo = PumpMUtil.getODTNumberFromNozzleNo(uPosMsg.getNozzle_no()) ;	//odt번호					
					transactionNo = uPosMsg.getPosReceipt_no() ;				// 거래번호
					storeName = storeData.getCust_name_disp() ;					// 매장명
					representative = storeData.getRep_name() ;						// 대표자
					represent = storeData.getBizregno_nbr() ;							// 사업자번호
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
						customerCode 		= dwPumpM.getCust_code();			// 거래처코드
						customerName 		= dwPumpM.getDrive_name();		// 거래처 명
						customerCardNo 	= dwPumpM.getCust_card_no();	// 거래처 카드번호
						customerCarNo 		= dwPumpM.getCar_no();				// 거래처 차량 번호
					}
					
					limit = "0";									// 한도량
					saveLimit = "0";							// 누적 사용량
					remainLimit = "0";						// 한도 잔량
					
					if (IUPOSConstant.MESSAGETYPE_0032.equals(uPosMsg.getMessageType()) ||
							IUPOSConstant.MESSAGETYPE_0034.equals(uPosMsg.getMessageType())){
						cardNumber = uPosMsg.getCreditCard_no() ;	// 신용카드 카드번호 
						acceptNo = uPosMsg.getCredit_auth_no() ;	// 신용카드 승인번호
						cardCorpName = uPosMsg.getAcquier_code() + " " + uPosMsg.getAcquier_name() ;	//신용카드 승인회사 	
						isCreditCard = true;						//신용카드 여부
						monthCnt = uPosMsg.getCredit_month();
					}
					
					sequence = "01" ;		// 구분
					productName2 	= productData.getGoods_name() ;		// 상품명
					nozzleNo 			= uPosMsg.getNozzle_no() ;				// 노즐번호
					liter = PumpMUtil.convertNumberFormatFromPumpToPOS(itemInfo.getTotalOilAmount(),3) ;	// 주유량
					
					if (posMsg == null) {
						basePrice = PumpMUtil.convertNumberFormatFromPumpToPOS(itemInfo.getUnitPrice(),3) ;	// 단가
					} else {
						if ("1".equals(dwPumpM.getRcptunitprc_ind_prt()) )	//단가 출력 여부
							basePrice = PumpMUtil.convertNumberFormatFromPumpToPOS(itemInfo.getUnitPrice(),3) ;	// 단가
						else
							basePrice = "0";
					}
					
					price 				= itemInfo.getTotalOilPrice_after_discount() ;	// 주유금액
					productPrice 	= itemInfo.getTotalPrice_tax() ;							// 공급가액
					taxPrice 			= itemInfo.getTotalTaxPrice() ;							// 새액
					totalPrice 		= itemInfo.getTotalOilPrice_after_discount() ;	// 주유 금액
					
					receivePrice = uPosMsg.getItem_info().getTotalOilPrice_after_discount();	// 받은 금액
					
					if (!PumpMUtil.shouldSendingRejectAndReApproval(receivePrice, totalPrice, isCreditCard) )
						refund	= String.valueOf(diffPrice);	// 환불 금액
					else
						refund = "0";
					
					if (IUPOSConstant.MESSAGETYPE_0054.equals(uPosMsg.getMessageType()) ||
							IUPOSConstant.MESSAGETYPE_0012.equals(uPosMsg.getMessageType()) ||
		 
							//2012.10.09 ksm 현금거래처 보너스 적립시 금액표시 이상
							IUPOSConstant.MESSAGETYPE_0014.equals(uPosMsg.getMessageType()) ||	
							IUPOSConstant.MESSAGETYPE_0004.equals(uPosMsg.getMessageType()) ||
							IUPOSConstant.MESSAGETYPE_0016.equals(uPosMsg.getMessageType())) {
						
						HE_WorkingMessage heWrkMsg = DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzleNo).getHeWorkingMsg();
						
						// 2012.10.26 ksm 현금거래처 거스름돈 처리
						//receivePrice = String.valueOf(Double.parseDouble(heWrkMsg.getPrice()));
						receivePrice = String.valueOf(Double.parseDouble(heWrkMsg.getCashCount()));
						
						refund = String.valueOf(Double.parseDouble(receivePrice) - Double.parseDouble(totalPrice));
					}
					
					bonusCardNo		= uPosMsg.getBonRSCard_no();				// 보너스카드 번호
					authNo				= uPosMsg.getBonRSCard_authNo();		// 보너스카드 승인 번호
					createPoint			= uPosMsg.getGs_point1();						// 발생 포인트
					usePoint				= uPosMsg.getGs_point2();						// 가용 포인트
					totalPoint			= "0";															// 주유소 포인트
					message				= uPosMsg.getBonRS_msg();					// 보너스카드 메세지
					option 				= DasNoSelfPumpingManager.getInstance().getCurrentOption(nozzleNo) ;

					//2011.09.08 ksm 선승인금액 얻어옴.
					if(isCreditCard){
						preReceivePrice = getPreReceivePrice(nozzleNo, transactionNo, price);
					}
										
					if (IUPOSConstant.MESSAGETYPE_0016.equals(uPosMsg.getMessageType()) ||
							IUPOSConstant.MESSAGETYPE_0054.equals(uPosMsg.getMessageType())){
						titleMessage	 			= uPosMsg.getTitle_msg();		// 현금영수증 거래구분 // tatsuno_hs okdhp7 (2012.12) 수정
						cachReceiptNo	 			= uPosMsg.getCreditCard_no();	// 현금영수증 인증번호
						cachReceiptAuthNo 		= uPosMsg.getCredit_auth_no();	// 현금영수증 승인번호
						cachReceiptMessage 	= uPosMsg.getVan_msg();				// 현금영수증 메세지
					} 
				}
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}		
		
		formatMsg = PumpMessageFormat.createCustomerTatsunoFormat(productName1			
											,storeCode			
											,date					
											,tel					
											,manager				
											,wDate				
											,odtNo				
											,transactionNo		
											,storeName			
											,representative		
											,represent			
											,address			
											,customerCode			
											,customerName 		
											,customerCardNo 		
											,customerCarNo 		
											,limit 				
											,saveLimit 			
											,remainLimit 			
											,cardNumber   		
											,acceptNo     		
											,cardCorpName 		
											,sequence				
											,productName2			
											,nozzleNo				
											,liter				
											,basePrice			
											,price				
											,productPrice			
											,taxPrice				
											,totalPrice			
											,receivePrice			
											,refund				
											,bonusCardNo			
											,authNo				
											,createPoint			
											,usePoint				
											,totalPoint			
											,message				
											,titleMessage // tatsuno_hs okdhp7 (2012.12) 			
											,cachReceiptNo	 
											,cachReceiptAuthNo 
											,cachReceiptMessage
											,preReceivePrice
											,option
											,monthCnt) ;
		return formatMsg ;
	}	
	
	/**
	 * VAN 사로 부터 거절시 
	 * 
	 * by 박종호
	 * 
	 * @return
	 */
	public static QL_WorkingMessage createFailWhenReject(String nozzleNo, UPOSMessage uPosMsg) {
		LogUtility.getPumpMLogger().info("[Pump M] Create Fail Receipt Message for DasNo") ;
		String receipt = "" ;
		String length = "0" ;
		String odtNo = PumpMUtil.getODTNumberFromNozzleNo(nozzleNo) ;	
		String qlMode = "";
		String alarmState = "0"; 	// 셀프ODT에서 재승인 실패시 음성메시지 출력을 위해 추가 - 최순구 2010.03.17
		
		try {
			DasNoODTNozzleInfo nozInfo = DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzleNo) ;
			int nozInfoOptionInt = nozInfo.getOption() ;
			int optionState = nozInfo.getOptionState() ;
			String date = "" ;
			date = getWDateFormat();
			
			LogUtility.getPumpMLogger().debug("[Pump M] nozInfoOptionInt : " + nozInfoOptionInt); // 테스트 로그
			LogUtility.getPumpMLogger().debug("[Pump M] optionState : " + optionState); // 테스트 로그
			
			switch (nozInfoOptionInt) {
				case IConstant.FULL_PUMPING_OPTION_0  : {
					switch (optionState) {					
					case IConstant.SELF_FULLPUMPING_OPTION_0_STATE_1 : {
						receipt = PumpMessageFormat.createErrorPrintFormat(	5, 
																			10, 
																			nozzleNo, 
																			uPosMsg.getCreditCard_no(), 
																			uPosMsg.getIssuer_name(), 
																			uPosMsg.getCredit_auth_no(), 
																			uPosMsg.getTrdate_creditCard(), 
																			uPosMsg.getBonRSCard_no(), 
																			uPosMsg.getBonRSCard_authNo(), 
																			//	uPosMsg.getVan_msg(), 
																			//	uPosMsg.getBonRS_msg(),
																			//PI2 20160325 twlee uPosMsg.getOdtScreenMsg()	=> uPosMsg.getDisplay_msg()변경 
																			uPosMsg.getDisplay_msg(),
																			uPosMsg.getVan_msg(), 
																			uPosMsg.getPayment_amt(), 
																			date);
						qlMode = "0";
						break ;
					}
						case IConstant.SELF_FULLPUMPING_OPTION_0_STATE_2 : {
							// 2. 5
							receipt = PumpMessageFormat.createErrorPrintFormat(	2, 
									5, 
									nozzleNo, 
									uPosMsg.getCreditCard_no(), 
									uPosMsg.getIssuer_name(), 
									uPosMsg.getCredit_auth_no(), 
									uPosMsg.getTrdate_creditCard(), 
									uPosMsg.getBonRSCard_no(), 
									uPosMsg.getBonRSCard_authNo(), 
									//09.02.09 실패시 getOdtScreenMsg 로 메세지가 오는 것으로 인해 수정, getBonRS_msg 위치에 getVan_msg 임시로 출력
									//	uPosMsg.getVan_msg(), 
									//	uPosMsg.getBonRS_msg(),
									//PI2 20160325 twlee 장애대응에서 odtScreenMsg를 내려주지 않아 display_msg로 변경
									uPosMsg.getDisplay_msg(),
									uPosMsg.getVan_msg(), 
									uPosMsg.getPayment_amt(), 
									date);
							qlMode = "1";
							alarmState = "1";
							break ;
						}
					}
					break ;
				}
				case IConstant.FULL_PUMPING_OPTION_1 : 
				case IConstant.FULL_PUMPING_OPTION_9 :{ //2016-04 이강호
					switch (optionState) {					
						case IConstant.SELF_FULLPUMPING_OPTION_1_STATE_1 : {
							receipt = PumpMessageFormat.createErrorPrintFormat(	5, 
									10, 
									nozzleNo, 
									uPosMsg.getCreditCard_no(), 
									uPosMsg.getIssuer_name(), 
									uPosMsg.getCredit_auth_no(), 
									uPosMsg.getTrdate_creditCard(), 
									uPosMsg.getBonRSCard_no(), 
									uPosMsg.getBonRSCard_authNo(), 
									//	uPosMsg.getVan_msg(), 
									//	uPosMsg.getBonRS_msg(),
									//PI2 20160325 twlee uPosMsg.getOdtScreenMsg()	=> uPosMsg.getDisplay_msg()변경 
									uPosMsg.getDisplay_msg(),
									uPosMsg.getVan_msg(), 
									uPosMsg.getPayment_amt(), 
									date);
							qlMode = "0";
							break ;
						}
						case IConstant.SELF_FULLPUMPING_OPTION_1_STATE_2 : {
							// 2.5
							receipt = PumpMessageFormat.createErrorPrintFormat(2, 
									5, 
									nozzleNo, 
									uPosMsg.getCreditCard_no(), 
									uPosMsg.getIssuer_name(), 
									uPosMsg.getCredit_auth_no(), 
									uPosMsg.getTrdate_creditCard(), 
									uPosMsg.getBonRSCard_no(), 
									uPosMsg.getBonRSCard_authNo(), 
									//	09.02.09 실패시 getOdtScreenMsg 로 메세지가 오는 것으로 인해 수정, getBonRS_msg 위치에 getVan_msg 임시로 출력
									//	uPosMsg.getVan_msg(), 
									//	uPosMsg.getBonRS_msg(),
									//PI2 20160325 twlee uPosMsg.getOdtScreenMsg()	=> uPosMsg.getDisplay_msg()변경 
									uPosMsg.getDisplay_msg(),
									uPosMsg.getVan_msg(), 
									uPosMsg.getPayment_amt(), 
									date);
							qlMode = "1";
							alarmState = "1";
							break ;
						}
						case IConstant.SELF_FULLPUMPING_OPTION_1_STATE_3 : {
							// 4.4
							receipt = PumpMessageFormat.createErrorPrintFormat(	4, 
									4, 
									nozzleNo, 
									uPosMsg.getCreditCard_no(), 
									uPosMsg.getIssuer_name(), 
									uPosMsg.getCredit_auth_no(), 
									uPosMsg.getTrdate_creditCard(), 
									uPosMsg.getBonRSCard_no(), 
									uPosMsg.getBonRSCard_authNo(), 
									//	09.02.09 실패시 getOdtScreenMsg 로 메세지가 오는 것으로 인해 수정, getBonRS_msg 위치에 getVan_msg 임시로 출력
									//	uPosMsg.getVan_msg(), 
									//	uPosMsg.getBonRS_msg(),
									//PI2 20160325 twlee uPosMsg.getOdtScreenMsg()	=> uPosMsg.getDisplay_msg()변경 
									uPosMsg.getDisplay_msg(),
									uPosMsg.getVan_msg(),  
									uPosMsg.getPayment_amt(), 
									date);
							qlMode = "1";
							break ;
						}
					}
					break ;
				}
				case IConstant.FULL_PUMPING_OPTION_2 : {
					switch (optionState) {					
						case IConstant.SELF_FULLPUMPING_OPTION_2_STATE_1 : {
							receipt = PumpMessageFormat.createErrorPrintFormat(		5, 
									10, 
									nozzleNo, 
									uPosMsg.getCreditCard_no(), 
									uPosMsg.getIssuer_name(), 
									uPosMsg.getCredit_auth_no(), 
									uPosMsg.getTrdate_creditCard(), 
									uPosMsg.getBonRSCard_no(), 
									uPosMsg.getBonRSCard_authNo(), 
									//	uPosMsg.getVan_msg(), 
									//	uPosMsg.getBonRS_msg(),
									//PI2 20160325 twlee uPosMsg.getOdtScreenMsg()	=> uPosMsg.getDisplay_msg()변경 
									uPosMsg.getDisplay_msg(),
									uPosMsg.getVan_msg(), 
									uPosMsg.getPayment_amt(), 
									date);
							qlMode = "0";
							break ;
						}
						case IConstant.SELF_FULLPUMPING_OPTION_2_STATE_2 : {
							// 4.4
							receipt = PumpMessageFormat.createErrorPrintFormat(	4,
									4, 
									nozzleNo, 
									uPosMsg.getCreditCard_no(), 
									uPosMsg.getIssuer_name(), 
									uPosMsg.getCredit_auth_no(), 
									uPosMsg.getTrdate_creditCard(), 
									uPosMsg.getBonRSCard_no(), 
									uPosMsg.getBonRSCard_authNo(), 
									//	09.02.09 실패시 getOdtScreenMsg 로 메세지가 오는 것으로 인해 수정, getBonRS_msg 위치에 getVan_msg 임시로 출력
									//	uPosMsg.getVan_msg(), 
									//	uPosMsg.getBonRS_msg(),
									//PI2 20160325 twlee uPosMsg.getOdtScreenMsg()	=> uPosMsg.getDisplay_msg()변경 
									uPosMsg.getDisplay_msg(),
									uPosMsg.getVan_msg(), 
									uPosMsg.getPayment_amt(), 
									date);
							qlMode = "1";
							alarmState = "1";
							break ;
						}
						case IConstant.SELF_FULLPUMPING_OPTION_2_STATE_3 : {
							// 2.5
							receipt = PumpMessageFormat.createErrorPrintFormat(2, 
									5, 
									nozzleNo, 
									uPosMsg.getCreditCard_no(), 
									uPosMsg.getIssuer_name(), 
									uPosMsg.getCredit_auth_no(), 
									uPosMsg.getTrdate_creditCard(), 
									uPosMsg.getBonRSCard_no(), 
									uPosMsg.getBonRSCard_authNo(), 
									//	09.02.09 실패시 getOdtScreenMsg 로 메세지가 오는 것으로 인해 수정, getBonRS_msg 위치에 getVan_msg 임시로 출력
									//	uPosMsg.getVan_msg(), 
									//	uPosMsg.getBonRS_msg(),
									//PI2 20160325 twlee uPosMsg.getOdtScreenMsg()	=> uPosMsg.getDisplay_msg()변경 
									uPosMsg.getDisplay_msg(),
									uPosMsg.getVan_msg(), 
									uPosMsg.getPayment_amt(), 
									date);
							qlMode = "1";
							alarmState = "1";
							break ;
						}
					}
					break ;
				}	
				case IConstant.FULL_PUMPING_OPTION_5 : {
					switch (optionState) {					
						case IConstant.SELF_FULLPUMPING_OPTION_5_STATE_1 : {
							receipt = PumpMessageFormat.createErrorPrintFormat(		5, 
									10, 
									nozzleNo, 
									uPosMsg.getCreditCard_no(), 
									uPosMsg.getIssuer_name(), 
									uPosMsg.getCredit_auth_no(), 
									uPosMsg.getTrdate_creditCard(), 
									uPosMsg.getBonRSCard_no(), 
									uPosMsg.getBonRSCard_authNo(), 
									//	uPosMsg.getVan_msg(), 
									//	uPosMsg.getBonRS_msg(),
									//PI2 20160325 twlee uPosMsg.getOdtScreenMsg()	=> uPosMsg.getDisplay_msg()변경 
									uPosMsg.getDisplay_msg(),
									uPosMsg.getVan_msg(), 
									uPosMsg.getPayment_amt(), 
									date);
							qlMode = "0";
							break ;
						}
						case IConstant.SELF_FULLPUMPING_OPTION_5_STATE_2 : {
							//  4, 8
							receipt = PumpMessageFormat.createErrorPrintFormat(	4, 
									8, 
									nozzleNo, 
									uPosMsg.getCreditCard_no(), 
									uPosMsg.getIssuer_name(), 
									uPosMsg.getCredit_auth_no(), 
									uPosMsg.getTrdate_creditCard(), 
									uPosMsg.getBonRSCard_no(), 
									uPosMsg.getBonRSCard_authNo(), 
									//	09.02.09 실패시 getOdtScreenMsg 로 메세지가 오는 것으로 인해 수정, getBonRS_msg 위치에 getVan_msg 임시로 출력
									//	uPosMsg.getVan_msg(), 
									//	uPosMsg.getBonRS_msg(),
									//PI2 20160325 twlee uPosMsg.getOdtScreenMsg()	=> uPosMsg.getDisplay_msg()변경 
									uPosMsg.getDisplay_msg(),
									uPosMsg.getVan_msg(), 
									uPosMsg.getPayment_amt(), 
									date);
							qlMode = "1";
							break ;
						}
					}
					break ;
				}
				case IConstant.FULL_PUMPING_OPTION_6 : {
					switch (optionState) {					
						case IConstant.SELF_FULLPUMPING_OPTION_6_STATE_1 : {
							receipt = PumpMessageFormat.createErrorPrintFormat(	5, 
									10, 
									nozzleNo, 
									uPosMsg.getCreditCard_no(), 
									uPosMsg.getIssuer_name(), 
									uPosMsg.getCredit_auth_no(), 
									uPosMsg.getTrdate_creditCard(), 
									uPosMsg.getBonRSCard_no(), 
									uPosMsg.getBonRSCard_authNo(), 
									//	uPosMsg.getVan_msg(), 
									//	uPosMsg.getBonRS_msg(),
									//PI2 20160325 twlee uPosMsg.getOdtScreenMsg()	=> uPosMsg.getDisplay_msg()변경 
									uPosMsg.getDisplay_msg(),
									uPosMsg.getVan_msg(), 
									uPosMsg.getPayment_amt(), 
									date);
							qlMode = "0";
							break ;
						}
						case IConstant.SELF_FULLPUMPING_OPTION_6_STATE_2 : {
							// 4. 8
							receipt = PumpMessageFormat.createErrorPrintFormat(4,
									8, 
									nozzleNo, 
									uPosMsg.getCreditCard_no(), 
									uPosMsg.getIssuer_name(), 
									uPosMsg.getCredit_auth_no(), 
									uPosMsg.getTrdate_creditCard(), 
									uPosMsg.getBonRSCard_no(), 
									uPosMsg.getBonRSCard_authNo(), 
									//	09.02.09 실패시 getOdtScreenMsg 로 메세지가 오는 것으로 인해 수정, getBonRS_msg 위치에 getVan_msg 임시로 출력
									//	uPosMsg.getVan_msg(), 
									//	uPosMsg.getBonRS_msg(),
									//PI2 20160325 twlee uPosMsg.getOdtScreenMsg()	=> uPosMsg.getDisplay_msg()변경 
									uPosMsg.getDisplay_msg(),
									uPosMsg.getVan_msg(), 
									uPosMsg.getPayment_amt(), 
									date);
							qlMode = "1";
							alarmState = "1";
							break ;
						}
						case IConstant.SELF_FULLPUMPING_OPTION_6_STATE_3 : {
							// 2. 9
							receipt = PumpMessageFormat.createErrorPrintFormat(	2,
									9, 
									nozzleNo, 
									uPosMsg.getCreditCard_no(), 
									uPosMsg.getIssuer_name(), 
									uPosMsg.getCredit_auth_no(), 
									uPosMsg.getTrdate_creditCard(), 
									uPosMsg.getBonRSCard_no(), 
									uPosMsg.getBonRSCard_authNo(), 
									//	09.02.09 실패시 getOdtScreenMsg 로 메세지가 오는 것으로 인해 수정, getBonRS_msg 위치에 getVan_msg 임시로 출력
									//	uPosMsg.getVan_msg(), 
									//	uPosMsg.getBonRS_msg(),
									//PI2 20160325 twlee uPosMsg.getOdtScreenMsg()	=> uPosMsg.getDisplay_msg()변경 
									uPosMsg.getDisplay_msg(),
									uPosMsg.getVan_msg(), 
									uPosMsg.getPayment_amt(), 
									date);
							qlMode = "1";
							alarmState = "1";
							break ;
						}
					}
					break ;
				}
				case IConstant.FULL_PUMPING_OPTION_7 : {
					switch (optionState) {					
						case IConstant.SELF_FULLPUMPING_OPTION_7_STATE_1 : {
							receipt = PumpMessageFormat.createErrorPrintFormat(	5, 
									10, 
									nozzleNo, 
									uPosMsg.getCreditCard_no(), 
									uPosMsg.getIssuer_name(), 
									uPosMsg.getCredit_auth_no(), 
									uPosMsg.getTrdate_creditCard(), 
									uPosMsg.getBonRSCard_no(), 
									uPosMsg.getBonRSCard_authNo(), 
									//	uPosMsg.getVan_msg(), 
									//	uPosMsg.getBonRS_msg(),
									//PI2 20160325 twlee uPosMsg.getOdtScreenMsg()	=> uPosMsg.getDisplay_msg()변경 
									uPosMsg.getDisplay_msg(),
									uPosMsg.getVan_msg(), 
									uPosMsg.getPayment_amt(), 
									date);
							qlMode = "0";
							break ;
						}
						case IConstant.SELF_FULLPUMPING_OPTION_7_STATE_2 : {
							// 4.4
							receipt = PumpMessageFormat.createErrorPrintFormat(	4,
									4, 
									nozzleNo, 
									uPosMsg.getCreditCard_no(), 
									uPosMsg.getIssuer_name(), 
									uPosMsg.getCredit_auth_no(), 
									uPosMsg.getTrdate_creditCard(), 
									uPosMsg.getBonRSCard_no(), 
									uPosMsg.getBonRSCard_authNo(), 
									//	09.02.09 실패시 getOdtScreenMsg 로 메세지가 오는 것으로 인해 수정, getBonRS_msg 위치에 getVan_msg 임시로 출력
									//	uPosMsg.getVan_msg(), 
									//	uPosMsg.getBonRS_msg(),
									//PI2 20160325 twlee uPosMsg.getOdtScreenMsg()	=> uPosMsg.getDisplay_msg()변경 
									uPosMsg.getDisplay_msg(),
									uPosMsg.getVan_msg(), 
									uPosMsg.getPayment_amt(), 
									date);
							qlMode = "1";
							break ;
						}
					}
					break ;
				}
				case IConstant.FULL_PUMPING_OPTION_8 : {
					switch (optionState) {					
						case IConstant.SELF_FULLPUMPING_OPTION_8_STATE_1 : {
							receipt = PumpMessageFormat.createErrorPrintFormat(	5, 
									10, 
									nozzleNo, 
									uPosMsg.getCreditCard_no(), 
									uPosMsg.getIssuer_name(), 
									uPosMsg.getCredit_auth_no(), 
									uPosMsg.getTrdate_creditCard(), 
									uPosMsg.getBonRSCard_no(), 
									uPosMsg.getBonRSCard_authNo(), 
									//	uPosMsg.getVan_msg(), 
									//	uPosMsg.getBonRS_msg(),
									//PI2 20160325 twlee uPosMsg.getOdtScreenMsg()	=> uPosMsg.getDisplay_msg()변경 
									uPosMsg.getDisplay_msg(),
									uPosMsg.getVan_msg(), 
									uPosMsg.getPayment_amt(), 
									date);
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
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
		}
		if (receipt != null) {
			length = Integer.toString(receipt.length()) ;
		}
		//return new QL_WorkingMessage(odtNo, nozzleNo, length, receipt, qlMode) ; ORG
		return new QL_WorkingMessage(odtNo, nozzleNo, length, receipt, qlMode, "", "", alarmState) ;
	}
	
	/**
	 * VAN 사로 부터 TimeOut 발생시
	 * 
	 * by 박종호
	 * @return
	 */
	public static QL_WorkingMessage createFailWhenTimeOut(String nozzleNo, UPOSMessage uPosMsg) {
		LogUtility.getPumpMLogger().info("[Pump M] Create TimeOut Receipt Message for DasNo") ;
		String receipt = "" ;
		String length = "0" ;
		String odtNo = PumpMUtil.getODTNumberFromNozzleNo(nozzleNo) ;
		
		try {
			DasNoODTNozzleInfo nozInfo = DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzleNo) ;
			int nozInfoOptionInt = nozInfo.getOption() ;
			int optionState = nozInfo.getOptionState() ;
			switch (nozInfoOptionInt) {
				case IConstant.FULL_PUMPING_OPTION_0  : {
					switch (optionState) {					
						case IConstant.SELF_FULLPUMPING_OPTION_0_STATE_1 : {
							
							break ;
						}
						case IConstant.SELF_FULLPUMPING_OPTION_0_STATE_2 : {
							
							break ;
						}
					}
					break ;
				}
				case IConstant.FULL_PUMPING_OPTION_1 : {
					switch (optionState) {					
						case IConstant.SELF_FULLPUMPING_OPTION_1_STATE_1 : {
							
							break ;
						}
						case IConstant.SELF_FULLPUMPING_OPTION_1_STATE_2 : {
							
							break ;
						}
						case IConstant.SELF_FULLPUMPING_OPTION_1_STATE_3 : {
							
							break ;
						}
					}
					break ;
				}
				case IConstant.FULL_PUMPING_OPTION_2 : {
					switch (optionState) {					
						case IConstant.SELF_FULLPUMPING_OPTION_2_STATE_1 : {
							
							break ;
						}
						case IConstant.SELF_FULLPUMPING_OPTION_2_STATE_2 : {
							
							break ;
						}
						case IConstant.SELF_FULLPUMPING_OPTION_2_STATE_3 : {
							
							break ;
						}
					}
					break ;
				}	
				case IConstant.FULL_PUMPING_OPTION_5 : {
					switch (optionState) {					
						case IConstant.SELF_FULLPUMPING_OPTION_5_STATE_1 : {
							
							break ;
						}
						case IConstant.SELF_FULLPUMPING_OPTION_5_STATE_2 : {
							
							break ;
						}
					}
					break ;
				}
				case IConstant.FULL_PUMPING_OPTION_6 : {
					switch (optionState) {					
						case IConstant.SELF_FULLPUMPING_OPTION_6_STATE_1 : {
							
							break ;
						}
						case IConstant.SELF_FULLPUMPING_OPTION_6_STATE_2 : {
							
							break ;
						}
						case IConstant.SELF_FULLPUMPING_OPTION_6_STATE_3 : {
							
							break ;
						}
					}
					break ;
				}
				case IConstant.FULL_PUMPING_OPTION_7 : {
					switch (optionState) {					
						case IConstant.SELF_FULLPUMPING_OPTION_7_STATE_1 : {
							
							break ;
						}
						case IConstant.SELF_FULLPUMPING_OPTION_7_STATE_2 : {
							
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
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
		}
		
		if (receipt != null) {
			length = Integer.toString(receipt.length()) ;
		}
		//return new QL_WorkingMessage(odtNo, nozzleNo, length, receipt, "1") ;
		return new QL_WorkingMessage(odtNo, nozzleNo, length, receipt, "1", "", "") ;
	}

	// tatsuno_hs okdhp7 (2012.12) 추가
	/**
	 * 주유완료 금액이 0 원이어서 선승인 취소 이후 영수증 Format
	 * 
	 * @param itemInfo		: 주유완료 자료
	 * @param posMsg		: 선승인 취소 응답 전문
	 * @param refund		: 환불 금액				
	 * @param isAccepted	: 승인 or 거절 여부
	 * @return
	 */
	private static String createReceiptBonusPointWhenPumping0(UPOSMessage_ItemInfo pumpingItemInfo, 
			UPOSMessage uPosMsg, int refund, boolean isAccepted) {

		String formatMsg = "응답 정보는 받았지만, 응답 정보에 에러가 생겨서 Print 정보가 없습니다." ;		
		
		String productName1 		= "" ;		// 상품명
		String storeCode 			= "" ;		// 매장코드
		String date 				= "" ;		// 날짜(YYYYMMDDHHMMSS)
		String tel 					= "" ;		// 매장전화번호
		String manager 				= "" ;		// 담당자
		String wDate				= "" ;		// 영업일자
		String odtNo 				= "" ;		// ODT 번호
		String transactionNo 		= "" ;		// 거래번호
		String storeName 			= "" ;		// 매장명
		String representative 		= "" ;		// 대표자
		String represent 			= "" ;		// 사업자번호
		String address 				= "" ;		// 매장주소
		String cardNumber 			= "" ;		// 카드번호 
		String acceptNo 			= "" ;		// 승인번호 
		String cardCorpName 		= "" ;		// 승인회사 
		String sequence 			= "" ;		// 구분
		String productName2 		= "" ;		// 상품명
		String nozzleNo 			= "" ;		// 노즐번호
		String liter 				= "0" ;		// 주유량
		String basePrice 			= "" ;		// 단가
		String price 				= "0" ;		// 주유금액
		String productPrice 		= "0" ;		// 공급가액
		String taxPrice 			= "0" ;		// 새액
		String totalPrice 			= "0" ;		// 주유 금액
		String receivePrice 		= "0" ;		// 결제금액
		int option 					= 0; 		// 가득주유타입 
		
		// 2011.09.07 ksm 선승인 금액 저장 추가
		String preReceivePrice = "0";	// 선승인금액
		
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
					
					manager = storeData.getRep_name() ;																				// 담당자
					wDate = getWorkingDateFormat(storeData.getBizhour_date()) ;									// 영업일자
					odtNo = PumpMUtil.getODTNumberFromNozzleNo(uPosMsg.getNozzle_no()) ;					
					transactionNo = uPosMsg.getPosReceipt_no() ;																// 거래번호
					storeName = storeData.getCust_name_disp() ;																	// 매장명
					representative = storeData.getRep_name() ;																		// 대표자
					represent = storeData.getBizregno_nbr() ;																			// 사업자번호
					address = storeData.getBizstoreaddr_txt1() + " " + storeData.getBizstoreaddr_txt2();	// 매장주소					
					cardNumber = uPosMsg.getCreditCard_no() ;																	// 카드번호
//					acceptNo = uPosMsg.getCredit_auth_no() ;		// 승인번호
//					cardCorpName = uPosMsg.getAcquier_code() + " " + uPosMsg.getAcquier_name() ;	// 승인회사 					
//					sequence = "01" ;														// 구분
					productName2 = productData.getGoods_name() ;															// 상품명
					nozzleNo = uPosMsg.getNozzle_no() ;																				// 노즐번호
//					liter = pumpingItemInfo.getTotalOilAmount() ;																// 주유량
					basePrice = pumpingItemInfo.getUnitPrice() ;																	// 단가
//					price = pumpingItemInfo.getTotalOilPrice_after_discount() ;											// 주유금액
//					productPrice = pumpingItemInfo.getTotalPrice_tax() ;														// 공급가액
//					taxPrice = pumpingItemInfo.getTotalTaxPrice() ;																// 세액
//					totalPrice = pumpingItemInfo.getTotalOilPrice_after_discount() ;									// 주유금액
//					receivePrice = "0" ;
					option = DasNoSelfPumpingManager.getInstance().getCurrentOption(nozzleNo) ;
					
					//2011.09.08 ksm 선승인금액 얻어옴.
					preReceivePrice = getPreReceivePrice(nozzleNo, transactionNo, price);
				}
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}
		
		if (refund != 0) {
			formatMsg = PumpMessageFormat.createBonusPointRefundWhenPumping0_TatsunoFormat( productName1, 
												"(" + storeCode + ")", 
												date,
												tel, 
												manager, 
												wDate, 
												odtNo, 
												transactionNo, 
												storeName, 
												representative,
												represent, 
												address,
												cardNumber,
												acceptNo, 
												cardCorpName, 
												sequence, 
												productName2, 
												nozzleNo, 
												GlobalUtility.getDividedWith1000(liter), 
												GlobalUtility.getDividedWith1000(basePrice), 
												GlobalUtility.getStringValue(price),
												GlobalUtility.getStringValue(productPrice), 
												GlobalUtility.getStringValue(taxPrice),
												GlobalUtility.getStringValue(totalPrice),
												GlobalUtility.getStringValue(receivePrice), 
												Integer.toString(refund),
												option) ;
		} else {
			formatMsg = PumpMessageFormat.createBonusPointAcceptWhenPumping0_TatsunoFormat(	productName1, 
												"(" + storeCode + ")", 
												date, 
												tel,
												manager, 
												wDate, 
												odtNo, 
												transactionNo, 
												storeName,
												representative, 
												represent,
												address,
												cardNumber,
												acceptNo,
												cardCorpName,
												sequence, 
												productName2, 
												nozzleNo,
												GlobalUtility.getDividedWith1000(liter), 
												GlobalUtility.getDividedWith1000(basePrice), 
												GlobalUtility.getStringValue(price),
												GlobalUtility.getStringValue(productPrice), 
												GlobalUtility.getStringValue(taxPrice),
												GlobalUtility.getStringValue(totalPrice),
												GlobalUtility.getStringValue(receivePrice),
												GlobalUtility.getStringValue(preReceivePrice),
												option) ;
		}
		return formatMsg ;
	
	}

	// tatsuno_hs okdhp7 (2012.12) 추가
	/**
	 * GS 보너스 포인트 결제 요청에 대한 응답 전문을 이용하여 다쓰노 ODT 영수증 출력을 위한 메시지 생성을 요청한다.
	 * 
	 * @param pumpingItemInfo	: 주유 정보
	 * @param uPosMsg			: 신용 승인 응답 전문
	 * @param refund			: POS 에서 현금으로 제공해야할 금액
	 * @param isAccepted		: 승인이 난 경우
	 * @return
	 */
	private static String createReceiptByBonusPoint(UPOSMessage_ItemInfo pumpingItemInfo, 
			UPOSMessage uPosMsg, int refund, boolean isAccepted) {
		String formatMsg = "응답 정보는 받았지만, 응답 정보에 에러가 생겨서 Print 정보가 없습니다." ;		
		
		String productName1 		= "" ;		// 상품명
		String storeCode 			= "" ;		// 매장코드
		String date 				= "" ;		// 날짜(YYYYMMDDHHMMSS)
		String tel 					= "" ;		// 매장전화번호
		String manager 				= "" ;		// 담당자
		String wDate 				= "" ;		// 영업일자
		String odtNo 				= "" ;		// ODT 번호
		String transactionNo 		= "" ;		// 거래번호
		String storeName 			= "" ;		// 매장명
		String representative 		= "" ;		// 대표자
		String represent 			= "" ;		// 사업자번호
		String address 				= "" ;		// 매장주소
		String cardNumber 			= "" ;		// 카드번호 
		String acceptNo 			= "" ;		// 승인번호 
		String cardCorpName 		= "" ;		// 승인회사 
		String sequence 			= "" ;		// 구분
		String productName2 		= "" ;		// 상품명
		String nozzleNo 			= "" ;		// 노즐번호
		String liter 				= "" ;		// 주유량
		String basePrice 			= "" ;		// 단가
		String price 				= "" ;		// 주유금액
		String productPrice 		= "" ;		// 공급가액
		String taxPrice 			= "" ;		// 새액
		String totalPrice 			= "" ;		// 주유 금액
		String receivePrice 		= "" ;		// 결제금액
		int option 					= 0; 		// 가득주유 타입
		
//		 2011.09.07 ksm 선승인 금액 저장 추가
		String preReceivePrice = "0";	// 선승인금액
		
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
					sequence = "01" ;														// 구분
					productName2 = productData.getGoods_name() ;							// 상품명
					nozzleNo = uPosMsg.getNozzle_no() ;										// 노즐번호
					liter = pumpingItemInfo.getTotalOilAmount() ;							// 주유량
					basePrice = pumpingItemInfo.getUnitPrice() ;							// 단가
					price = pumpingItemInfo.getTotalOilPrice_after_discount() ;				// 주유금액
					productPrice = pumpingItemInfo.getTotalPrice_tax() ;					// 공급가액
					taxPrice = pumpingItemInfo.getTotalTaxPrice() ;							// 새액
					totalPrice = pumpingItemInfo.getTotalOilPrice_after_discount() ;		// 주유금액
					option = DasNoSelfPumpingManager.getInstance().getCurrentOption(nozzleNo) ;
										
					//2011.09.08 ksm 선승인금액 얻어옴.
					preReceivePrice = getPreReceivePrice(nozzleNo, transactionNo, price);

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
		
		if (refund != 0) { // 차액이 있으면(실제로 사용하지 않음)
			formatMsg = PumpMessageFormat.createBonuPointRefundTatsunoFormat(	productName1, 
																			"(" + storeCode + ")", 
																			date,
																			tel, 
																			manager, 
																			wDate, 
																			odtNo, 
																			transactionNo, 
																			storeName, 
																			representative,
																			represent, 
																			address,
																			cardNumber,
																			acceptNo, 
																			cardCorpName, 
																			sequence, 
																			productName2, 
																			nozzleNo, 
																			GlobalUtility.getDividedWith1000(liter), 
																			GlobalUtility.getDividedWith1000(basePrice), 
																			GlobalUtility.getStringValue(price),
																			GlobalUtility.getStringValue(productPrice), 
																			GlobalUtility.getStringValue(taxPrice),
																			GlobalUtility.getStringValue(totalPrice),
																			GlobalUtility.getStringValue(receivePrice), 
																			Integer.toString(refund),
																			option) ;
		} else { // 차액이 없으면(정상주유 및 미만주유 재승인 해당)
			formatMsg = PumpMessageFormat.createBonusPointAcceptTatsunoFormat(	productName1, 
																			"(" + storeCode + ")", 
																			date, 
																			tel,
																			manager, 
																			wDate, 
																			odtNo, 
																			transactionNo, 
																			storeName,
																			representative, 
																			represent,
																			address,
																			cardNumber,
																			acceptNo,
																			cardCorpName,
																			sequence, 
																			productName2, 
																			nozzleNo,
																			GlobalUtility.getDividedWith1000(liter), 
																			GlobalUtility.getDividedWith1000(basePrice), 
																			GlobalUtility.getStringValue(price),
																			GlobalUtility.getStringValue(productPrice), 
																			GlobalUtility.getStringValue(taxPrice),
																			GlobalUtility.getStringValue(totalPrice),
																			GlobalUtility.getStringValue(receivePrice),
																			GlobalUtility.getStringValue(preReceivePrice),
																			option) ;
		}
		return formatMsg ;
	}
	
	/**
	 * 현금 결제 이후 다쓰노 ODT 출력 메시지 생성을 요청한다.
	 * 
	 * @param nozzleNo			: 노즐 번호
	 * @param khproc_no			: KH 처리번호
	 * @param pumpingPrice		: 주유 금액
	 * @param pumpingLiter		: 주유 량
	 * @param payedPrice		: 결제 금액
	 * @param pumpingBasePrice	: 주유 단가
	 * @return
	 */
	public static String createReceiptByCash(String nozzleNo, String khproc_no, 
			String pumpingPrice, String pumpingLiter, String payedPrice, String pumpingBasePrice) {
		String formatMsg = "응답 정보는 받았지만, 응답 정보에 에러가 생겨서 Print 정보가 없습니다." ;		

		String productName1 			= "" ; 	// 상품명
		String storeCode 				= "" ; 	// 매장코드
		String date 					= "" ; 	// 날짜 (YYYYMMDDHHMMSS)
		String tel 						= "" ; 	// 매장전화번호
		String manager 					= "" ; 	// 담당자
		String wDate 					= "" ; 	// 영업일자
		String odtNo 					= "" ; 	// ODT 번호
		String transactionNo 			= "" ;	// 거래번호
		String storeName 				= "" ; 	// 매장명
		String representative 			= "" ;	// 대표자
		String represent 				= "" ; 	// 사업자번호
		String address 					= "" ; 	// 매장 주소
		/*String cardNumber				= "" ;	// 카드번호
		String acceptNo 				= "" ;	// 승인번호
		String cardCorpName 			= "" ; 	// 승인회사
*/		String sequence 				= "" ;	// 구분
		String productName2 			= "" ; 	// 상품명
		String liter 					= "" ;	// 주유량
		String basePrice 				= "" ; 	// 단가
		String price 					= "" ;	// 주유금액
		String productPrice 			= "" ; 	// 공급가액
		String taxPrice 				= "" ;	// 새액
		String totalPrice 				= "" ; 	// 주유 금액
		String receivePrice 			= payedPrice ; 	// 결제 금액
		int refund 						= 0 ;
		
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
					T_KH_STOREData storeData = T_KH_STOREHandler.getHandler().getT_KH_STOREDataByStoreCode(storeCode) ;
					
					productName1 = productData.getGoods_name() ;
					date = getWDateFormat() ;
					tel = "(" + storeData.getTel_nbr() + ")" ;
					
					manager = storeData.getRep_name() ;														// 담당자
					wDate = getWorkingDateFormat(storeData.getBizhour_date()) ;			// 영업일자
					odtNo = PumpMUtil.getODTNumberFromNozzleNo(nozzleNo) ;			// ODT번호		
					transactionNo = khproc_no ;																		// 거래번호
					storeName = storeData.getCust_name_disp() ;											// 매장명
					representative = storeData.getRep_name() ;												// 대표자
					represent = storeData.getBizregno_nbr() ;													// 사업자번호
					address = storeData.getBizstoreaddr_txt1()  + " " + storeData.getBizstoreaddr_txt2();	// 매장주소					
					sequence = "01" ;																							// 구분
					productName2 = productData.getGoods_name() ;									// 상품명
					liter = itemInfo.getTotalOilAmount() ;														// 주유량
					basePrice = itemInfo.getUnitPrice() ;															// 단가
					price = itemInfo.getTotalOilPrice_after_discount() ;									// 주유금액
					productPrice = itemInfo.getTotalPrice_tax() ;											// 공급가액
					taxPrice = itemInfo.getTotalTaxPrice() ;														// 새액
					totalPrice = itemInfo.getTotalOilPrice_after_discount() ;							// 주유금액
					
					DasNoODTNozzleInfo nozInfo = DasNoSelfPumpingManager.getInstance().nozzleHash.get(nozzleNo);
					
					payedPrice = nozInfo.getHeWorkingMsg().getCashCount();

					receivePrice = payedPrice;
										
					refund = Integer.parseInt(payedPrice) - Integer.parseInt(pumpingPrice) ;
					
				}
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}
		
		if (refund == 0) {
			formatMsg = PumpMessageFormat.createCachAcceptTatsunoFormat(productName1, 
												"(" + storeCode + ")",
												date,
												tel,
												manager,
												wDate, 
												odtNo, 
												transactionNo,
												storeName,
												representative,
												represent,
												address, 
												sequence, 
												productName2,
												nozzleNo,
												GlobalUtility.getDividedWith1000(liter), 
												GlobalUtility.getDividedWith1000(basePrice), 
												GlobalUtility.getStringValue(price),
												GlobalUtility.getStringValue(productPrice), 
												GlobalUtility.getStringValue(taxPrice),
												GlobalUtility.getStringValue(totalPrice),
												GlobalUtility.getStringValue(receivePrice),
												0) ;
		} else {
			formatMsg = PumpMessageFormat.createCachRefundTatsunoFormat(	productName1, 
												"(" + storeCode + ")",
												date, 
												tel, 
												manager,
												wDate, 
												odtNo, 
												transactionNo, 
												storeName,
												representative, 
												represent, 
												address, 
												sequence, 
												productName2,
												nozzleNo, 
												GlobalUtility.getDividedWith1000(liter), 
												GlobalUtility.getDividedWith1000(basePrice), 
												GlobalUtility.getStringValue(price),
												GlobalUtility.getStringValue(productPrice), 
												GlobalUtility.getStringValue(taxPrice),
												GlobalUtility.getStringValue(totalPrice),
												GlobalUtility.getStringValue(receivePrice),
												Integer.toString(refund),
												0) ;
		}
		return formatMsg ;
	}
	
	/**
	 * 현금 승인 요청에 대한 응답 전문을 이용하여 다쓰노 ODT 영수증 출력을 위한 메시지 생성을 요청한다.
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
		String formatMsg 			= "응답 정보는 받았지만, 응답 정보에 에러가 생겨서 Print 정보가 없습니다." ;		
		String productName1 		= "" ;		// 상품명
		String storeCode 			= "" ;		// 매장코드
		String date 				= "" ;		// 날짜(YYYYMMDDHHMMSS)
		String tel 					= "" ;		// 매장전화번호
		String manager 				= "" ;		// 담당자
		String wDate 				= "" ;		// 영업일자
		String odtNo 				= "" ;		// ODT 번호
		String transactionNo		= "" ;		// 거래번호
		String storeName 			= "" ;		// 매장명
		String representative 		= "" ;		// 대표자
		String represent 			= "" ;		// 사업자번호
		String address 				= "" ;		// 매장주소
		String cardNumber 			= "" ;		// 카드번호 
		String acceptNo 			= "" ;		// 승인번호 
		String cardCorpName 		= "" ;		// 승인회사 
		String sequence 			= "" ;		// 구분
		String productName2 		= "" ;		// 상품명
		String nozzleNo 			= "" ;		// 노즐번호
		String liter 				= "" ;		// 주유량
		String basePrice 			= "" ;		// 단가
		String price 				= "" ;		// 주유금액
		String productPrice 		= "" ;		// 공급가액
		String taxPrice 			= "" ;		// 새액
		String totalPrice 			= "" ;		// 주유 금액
		String receivePrice 		= "" ;		// 결제금액
		int option 					= 0; 		// 가득주유 타입
		
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
					sequence = "01" ;														// 구분
					productName2 = productData.getGoods_name() ;							// 상품명
					nozzleNo = uPosMsg.getNozzle_no() ;										// 노즐번호
					liter = pumpingItemInfo.getTotalOilAmount() ;							// 주유량
					basePrice = pumpingItemInfo.getUnitPrice() ;							// 단가
					price = pumpingItemInfo.getTotalOilPrice_after_discount() ;				// 주유금액
					productPrice = pumpingItemInfo.getTotalPrice_tax() ;					// 공급가액
					taxPrice = pumpingItemInfo.getTotalTaxPrice() ;							// 새액
					totalPrice = pumpingItemInfo.getTotalOilPrice_after_discount() ;		// 주유금액
					option = DasNoSelfPumpingManager.getInstance().getCurrentOption(nozzleNo) ;
					
					if (isAccepted) {
						if (Integer.parseInt(liter) == 0)
							receivePrice = String.valueOf(refund);
						else
							receivePrice = String.valueOf(Integer.parseInt(itemInfo.getTotalOilPrice_after_discount()) + refund);			// 결제금액
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
		
		if (refund != 0) {
			formatMsg = PumpMessageFormat.createCachRefundTatsunoFormat(productName1, 
												"(" + storeCode + ")", 	
												date,
												tel, 
												manager, 
												wDate, 
												odtNo, 
												transactionNo, 
												storeName, 
												representative,
												represent, 
												address, 
												sequence,
												productName1,
												nozzleNo, 
												GlobalUtility.getDividedWith1000(liter), 
												GlobalUtility.getDividedWith1000(basePrice), 
												GlobalUtility.getStringValue(price),
												GlobalUtility.getStringValue(productPrice), 
												GlobalUtility.getStringValue(taxPrice),
												GlobalUtility.getStringValue(totalPrice),
												GlobalUtility.getStringValue(receivePrice), 
												Integer.toString(refund),
												option) ;
		} else {
			formatMsg = PumpMessageFormat.createCachAcceptTatsunoFormat(	productName1, 
												"(" + storeCode + ")", 
												date,
												tel, 
												manager, 
												wDate, 
												odtNo, 
												transactionNo, 
												storeName, 
												representative,
												represent, 
												address, 
												sequence,
												productName1,
												nozzleNo, 
												GlobalUtility.getDividedWith1000(liter), 
												GlobalUtility.getDividedWith1000(basePrice), 
												GlobalUtility.getStringValue(price),
												GlobalUtility.getStringValue(productPrice), 
												GlobalUtility.getStringValue(taxPrice),
												GlobalUtility.getStringValue(totalPrice),
												GlobalUtility.getStringValue(receivePrice),
												option) ;
		}
		return formatMsg ;
	}
	
	private static String createReceiptByCashFromPos(String nozzleNo, 
													String khproc_no, 
													String pumpingPrice, 
													String pumpingLiter, 
													String payedPrice, 
													String pumpingBasePrice) {
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
		
		if (refund == 0) {
			formatMsg = PumpMessageFormat.createCachAcceptTatsunoFormatFromPOS(	productName1, 
												"(" + storeCode + ")",
												date,
												tel,
												manager,
												wDate, 
												odtNo, 
												transactionNo,
												storeName,
												representative,
												represent,
												address, 
												sequence, 
												productName2,
												nozzleNo,
												GlobalUtility.getDividedWith1000(liter), 
												GlobalUtility.getDividedWith1000(basePrice), 
												GlobalUtility.getStringValue(price),
												GlobalUtility.getStringValue(productPrice), 
												GlobalUtility.getStringValue(taxPrice),
												GlobalUtility.getStringValue(totalPrice),
												GlobalUtility.getStringValue(receivePrice),
												0) ;
		} else {
			formatMsg = PumpMessageFormat.createCachRefundTatsunoFormatFromPOS(	productName1, 
												"(" + storeCode + ")",
												date, 
												tel, 
												manager,
												wDate, 
												odtNo, 
												transactionNo, 
												storeName,
												representative, 
												represent, 
												address, 
												sequence, 
												productName2,
												nozzleNo, 
												GlobalUtility.getDividedWith1000(liter), 
												GlobalUtility.getDividedWith1000(basePrice), 
												GlobalUtility.getStringValue(price),
												GlobalUtility.getStringValue(productPrice), 
												GlobalUtility.getStringValue(taxPrice),
												GlobalUtility.getStringValue(totalPrice),
												GlobalUtility.getStringValue(receivePrice),
												Integer.toString(refund),
												0) ;
		}
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
	private static String createReceiptByCredit(UPOSMessage_ItemInfo pumpingItemInfo, 
			UPOSMessage uPosMsg, int refund, boolean isAccepted) {
		String formatMsg = "응답 정보는 받았지만, 응답 정보에 에러가 생겨서 Print 정보가 없습니다." ;		
		
		String productName1 		= "" ;		// 상품명
		String storeCode 			= "" ;		// 매장코드
		String date 				= "" ;		// 날짜(YYYYMMDDHHMMSS)
		String tel 					= "" ;		// 매장전화번호
		String manager 				= "" ;		// 담당자
		String wDate 				= "" ;		// 영업일자
		String odtNo 				= "" ;		// ODT 번호
		String transactionNo 		= "" ;		// 거래번호
		String storeName 			= "" ;		// 매장명
		String representative 		= "" ;		// 대표자
		String represent 			= "" ;		// 사업자번호
		String address 				= "" ;		// 매장주소
		String cardNumber 			= "" ;		// 카드번호 
		String acceptNo 			= "" ;		// 승인번호 
		String cardCorpName 		= "" ;		// 승인회사 
		String sequence 			= "" ;		// 구분
		String productName2 		= "" ;		// 상품명
		String nozzleNo 			= "" ;		// 노즐번호
		String liter 				= "" ;		// 주유량
		String basePrice 			= "" ;		// 단가
		String price 				= "" ;		// 주유금액
		String productPrice 		= "" ;		// 공급가액
		String taxPrice 			= "" ;		// 새액
		String totalPrice 			= "" ;		// 주유 금액
		String receivePrice 		= "" ;		// 결제금액
		int option 					= 0; 		// 가득주유 타입
		String monthCnt			= "00";	// 할부개월수
		
//		 2011.09.07 ksm 선승인 금액 저장 추가
		String preReceivePrice = "0";	// 선승인금액
		
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
					sequence = "01" ;														// 구분
					productName2 = productData.getGoods_name() ;							// 상품명
					nozzleNo = uPosMsg.getNozzle_no() ;										// 노즐번호
					liter = pumpingItemInfo.getTotalOilAmount() ;							// 주유량
					basePrice = pumpingItemInfo.getUnitPrice() ;							// 단가
					price = pumpingItemInfo.getTotalOilPrice_after_discount() ;				// 주유금액
					productPrice = pumpingItemInfo.getTotalPrice_tax() ;					// 공급가액
					taxPrice = pumpingItemInfo.getTotalTaxPrice() ;							// 새액
					totalPrice = pumpingItemInfo.getTotalOilPrice_after_discount() ;		// 주유금액
					option = DasNoSelfPumpingManager.getInstance().getCurrentOption(nozzleNo) ;
					
					//2013.05.20 ksm 할부개월수 추가
					monthCnt = uPosMsg.getCredit_month();
					//2011.09.08 ksm 선승인금액 얻어옴.
					preReceivePrice = getPreReceivePrice(nozzleNo, transactionNo, price);

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
		
		if (refund != 0) {
			formatMsg = PumpMessageFormat.createCreditRefundTatsunoFormat(	productName1, 
												"(" + storeCode + ")", 
												date,
												tel, 
												manager, 
												wDate, 
												odtNo, 
												transactionNo, 
												storeName, 
												representative,
												represent, 
												address,
												cardNumber,
												acceptNo, 
												cardCorpName, 
												sequence, 
												productName2, 
												nozzleNo, 
												GlobalUtility.getDividedWith1000(liter), 
												GlobalUtility.getDividedWith1000(basePrice), 
												GlobalUtility.getStringValue(price),
												GlobalUtility.getStringValue(productPrice), 
												GlobalUtility.getStringValue(taxPrice),
												GlobalUtility.getStringValue(totalPrice),
												GlobalUtility.getStringValue(receivePrice), 
												Integer.toString(refund),
												option,
												monthCnt) ;
		} else {
			formatMsg = PumpMessageFormat.createCreditAcceptTatsunoFormat(	productName1, 
												"(" + storeCode + ")", 
												date, 
												tel,
												manager, 
												wDate, 
												odtNo, 
												transactionNo, 
												storeName,
												representative, 
												represent,
												address,
												cardNumber,
												acceptNo,
												cardCorpName,
												sequence, 
												productName2, 
												nozzleNo,
												GlobalUtility.getDividedWith1000(liter), 
												GlobalUtility.getDividedWith1000(basePrice), 
												GlobalUtility.getStringValue(price),
												GlobalUtility.getStringValue(productPrice), 
												GlobalUtility.getStringValue(taxPrice),
												GlobalUtility.getStringValue(totalPrice),
												GlobalUtility.getStringValue(receivePrice),
												GlobalUtility.getStringValue(preReceivePrice),
												option,
												monthCnt) ;
		}
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
	private static String createReceiptByCreditBonus(UPOSMessage_ItemInfo pumpingItemInfo, 
			UPOSMessage uPosMsg, int refund, boolean isAccepted) {
		String formatMsg = "응답 정보는 받았지만, 응답 정보에 에러가 생겨서 Print 정보가 없습니다." ;		
		
		String productName1 		= "" ; 	// 상품명
		String storeCode 			= "" ; 	// 매장코드
		String date 				= "" ; 	// 날짜 (YYYYMMDDHHMMSS)
		String tel 					= "" ; 	// 매장전화번호
		String manager				= "" ; 	// 담당자
		String wDate 				= "" ; 	// 영업일자
		String odtNo 				= "" ; 	// ODT 번호
		String transactionNo 		= "" ; 	// 거래번호
		String storeName 			= "" ; 	// 매장명
		String representative 		= "" ;	// 대표자
		String represent 			= "" ; 	// 사업자번호
		String address 				= "" ; 	// 매장 주소
		String cardNumber 			= "" ; 	// 카드번호
		String acceptNo 			= "" ; 	// 승인번호
		String cardCorpName 		= "" ; 	// 승인회사
		String sequence 			= "" ; 	// 구분
		String productName2 		= "" ; 	// 상품명
		String nozzleNo 			= "" ; 	// 노즐 번호
		String liter 				= "" ; 	// 주유량
		String basePrice 			= "" ; 	// 단가
		String price 				= "" ; 	// 주유금액
		String productPrice 		= "" ; 	// 공급가액
		String taxPrice 			= "" ; 	// 새액
		String totalPrice 			= "" ; 	// 주유 금액
		String receivePrice 		= "" ; 	// 결제 금액
		String bonusCardNo 			= "" ; 	// 카드No
		String authNo 				= "" ; 	// 승인No
		String createPoint 			= "" ; 	// 발생
		String usePoint 			= "" ; 	// 가용
		String totalPoint 			= "" ; 	// 총누적
		String message 				= "" ; 	// 메세지
		int option 					= 0; 	// 가득주유 타입
		
		// 2013.05.20 ksm 할부개월수
		String monthCnt 			= "00";
		
//		 2011.09.07 ksm 선승인 금액 저장 추가
		String preReceivePrice = "0";	// 선승인금액
		
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
					
					bonusCardNo 	= uPosMsg.getBonRSCard_no() ;
					authNo 				= uPosMsg.getBonRSCard_authNo() ;
					createPoint 		= uPosMsg.getGs_point1() ;
					usePoint 				= uPosMsg.getGs_point2() ;
					totalPoint 			= uPosMsg.getGs_point3() ;
					message 				= uPosMsg.getBonRS_msg() ;
					option 				= DasNoSelfPumpingManager.getInstance().getCurrentOption(nozzleNo) ;
					
					monthCnt			= uPosMsg.getCredit_month();
					//2011.09.08 ksm 선승인금액 얻어옴.
					preReceivePrice = getPreReceivePrice(nozzleNo, transactionNo, price);					
				}
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}
		
		if (refund != 0) {
			formatMsg = PumpMessageFormat.createBonusRefundTatsunoFormat(	productName1, 
												"(" + storeCode + ")", 
												date, 
												tel, 
												manager, 
												wDate, 
												odtNo, 
												transactionNo, 
												storeName, 
												representative, 
												represent, 
												address, 
												cardNumber, 
												acceptNo, 
												cardCorpName, 
												sequence, 
												productName2, 
												nozzleNo, 
												GlobalUtility.getDividedWith1000(liter), 
												GlobalUtility.getDividedWith1000(basePrice), 
												GlobalUtility.getStringValue(price),
												GlobalUtility.getStringValue(productPrice), 
												GlobalUtility.getStringValue(taxPrice),
												GlobalUtility.getStringValue(totalPrice),
												GlobalUtility.getStringValue(receivePrice),
												Integer.toString(refund), 
												bonusCardNo, 
												authNo, 
												createPoint, 
												usePoint, 
												totalPoint, 
												message,
												option,
												monthCnt) ;
		} else {
			formatMsg = PumpMessageFormat.createBonusAcceptTatsunoFormat(	productName1,  
												"(" + storeCode + ")", 
												date, 
												tel, 
												manager,
												wDate, 
												odtNo, 
												transactionNo, 
												storeName, 
												representative, 
												represent, 
												address, 
												cardNumber, 
												acceptNo, 
												cardCorpName, 
												sequence, 
												productName2, 
												nozzleNo, 
												GlobalUtility.getDividedWith1000(liter), 
												GlobalUtility.getDividedWith1000(basePrice), 
												GlobalUtility.getStringValue(price),
												GlobalUtility.getStringValue(productPrice), 
												GlobalUtility.getStringValue(taxPrice),
												GlobalUtility.getStringValue(totalPrice),
												GlobalUtility.getStringValue(receivePrice),
												bonusCardNo, 
												authNo, 
												createPoint, 
												usePoint, 
												totalPoint, 
												message,
												preReceivePrice,
												option,
												monthCnt) ;
		}
		return formatMsg ;
	}
	
	// 2012.10.19 ksm 조달청 제휴 카드 ODT사용시 거절 영수증 생성.
	public static String createReceiptByFail(String nozzleNo, String outputMessage){
		
		String storeCode="";
		T_KH_STOREData storeData = null;
		try{
			storeCode	= T_KH_STOREHandler.getHandler().getStoreCode();
			storeData 	= T_KH_STOREHandler.getHandler().getT_KH_STOREDataByStoreCode(storeCode) ;
		}catch(Exception e){
			
		}
		
		String date = getWDateFormat() ;
		String tel = "(" + storeData.getTel_nbr() + ")" ;
		
		String manager 			= storeData.getRep_name() ;															// 담당자
		String wDate 				= getWorkingDateFormat(storeData.getBizhour_date()) ;			// 영업일자
		String odtNo 				= nozzleNo;																						// 노즐번호
		String transactionNo 	= "0000000000" ;																				// 거래번호
		String storeName 		= storeData.getCust_name_disp() ;													// 매장명
		String representative 	= storeData.getRep_name() ;															// 대표자
		String represent 			= storeData.getBizregno_nbr() ;														// 사업자번호
		String address 			= storeData.getBizstoreaddr_txt1()  + " " + storeData.getBizstoreaddr_txt2();	// 매장주소		
			
		return PumpMessageFormat.createFailTatsunoFormat(tel, date, manager, wDate, odtNo, transactionNo, storeName, representative, represent, address, outputMessage);
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
	private static String createReceiptWhenPumping0(UPOSMessage_ItemInfo pumpingItemInfo, UPOSMessage uPosMsg, int refund, boolean isAccepted) {

		String formatMsg = "응답 정보는 받았지만, 응답 정보에 에러가 생겨서 Print 정보가 없습니다." ;		
		
		String productName1 		= "" ;		// 상품명
		String storeCode 			= "" ;		// 매장코드
		String date 				= "" ;		// 날짜(YYYYMMDDHHMMSS)
		String tel 					= "" ;		// 매장전화번호
		String manager 				= "" ;		// 담당자
		String wDate				= "" ;		// 영업일자
		String odtNo 				= "" ;		// ODT 번호
		String transactionNo 		= "" ;		// 거래번호
		String storeName 			= "" ;		// 매장명
		String representative 		= "" ;		// 대표자
		String represent 			= "" ;		// 사업자번호
		String address 				= "" ;		// 매장주소
		String cardNumber 			= "" ;		// 카드번호 
		String acceptNo 			= "" ;		// 승인번호 
		String cardCorpName 		= "" ;		// 승인회사 
		String sequence 			= "" ;		// 구분
		String productName2 		= "" ;		// 상품명
		String nozzleNo 			= "" ;		// 노즐번호
		String liter 				= "0" ;		// 주유량
		String basePrice 			= "" ;		// 단가
		String price 				= "0" ;		// 주유금액
		String productPrice 		= "0" ;		// 공급가액
		String taxPrice 			= "0" ;		// 새액
		String totalPrice 			= "0" ;		// 주유 금액
		String receivePrice 		= "0" ;		// 결제금액
		int option 					= 0; 		// 가득주유타입 
		String monthCnt = "00";
		
		// 2011.09.07 ksm 선승인 금액 저장 추가
		String preReceivePrice = "0";	// 선승인금액
		
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
					
					manager = storeData.getRep_name() ;																				// 담당자
					wDate = getWorkingDateFormat(storeData.getBizhour_date()) ;									// 영업일자
					odtNo = PumpMUtil.getODTNumberFromNozzleNo(uPosMsg.getNozzle_no()) ;					
					transactionNo = uPosMsg.getPosReceipt_no() ;																// 거래번호
					storeName = storeData.getCust_name_disp() ;																	// 매장명
					representative = storeData.getRep_name() ;																		// 대표자
					represent = storeData.getBizregno_nbr() ;																			// 사업자번호
					address = storeData.getBizstoreaddr_txt1() + " " + storeData.getBizstoreaddr_txt2();	// 매장주소					
					cardNumber = uPosMsg.getCreditCard_no() ;																	// 카드번호
//					acceptNo = uPosMsg.getCredit_auth_no() ;		// 승인번호
//					cardCorpName = uPosMsg.getAcquier_code() + " " + uPosMsg.getAcquier_name() ;	// 승인회사 					
//					sequence = "01" ;														// 구분
					productName2 = productData.getGoods_name() ;															// 상품명
					nozzleNo = uPosMsg.getNozzle_no() ;																				// 노즐번호
//					liter = pumpingItemInfo.getTotalOilAmount() ;																// 주유량
					basePrice = pumpingItemInfo.getUnitPrice() ;																	// 단가
//					price = pumpingItemInfo.getTotalOilPrice_after_discount() ;											// 주유금액
//					productPrice = pumpingItemInfo.getTotalPrice_tax() ;														// 공급가액
//					taxPrice = pumpingItemInfo.getTotalTaxPrice() ;																// 세액
//					totalPrice = pumpingItemInfo.getTotalOilPrice_after_discount() ;									// 주유금액
//					receivePrice = "0" ;
					option = DasNoSelfPumpingManager.getInstance().getCurrentOption(nozzleNo) ;
					
					//2013.05.20 ksm 
					monthCnt = uPosMsg.getCredit_month();
					//2011.09.08 ksm 선승인금액 얻어옴.
					preReceivePrice = getPreReceivePrice(nozzleNo, transactionNo, price);
				}
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}
		
		if (refund != 0) {
			formatMsg = PumpMessageFormat.createCreditRefundTatsunoFormat( productName1, 
												"(" + storeCode + ")", 
												date,
												tel, 
												manager, 
												wDate, 
												odtNo, 
												transactionNo, 
												storeName, 
												representative,
												represent, 
												address,
												cardNumber,
												acceptNo, 
												cardCorpName, 
												sequence, 
												productName2, 
												nozzleNo, 
												GlobalUtility.getDividedWith1000(liter), 
												GlobalUtility.getDividedWith1000(basePrice), 
												GlobalUtility.getStringValue(price),
												GlobalUtility.getStringValue(productPrice), 
												GlobalUtility.getStringValue(taxPrice),
												GlobalUtility.getStringValue(totalPrice),
												GlobalUtility.getStringValue(receivePrice), 
												Integer.toString(refund),
												option,
												monthCnt) ;
		} else {
			formatMsg = PumpMessageFormat.createCreditAcceptTatsunoFormat(	productName1, 
												"(" + storeCode + ")", 
												date, 
												tel,
												manager, 
												wDate, 
												odtNo, 
												transactionNo, 
												storeName,
												representative, 
												represent,
												address,
												cardNumber,
												acceptNo,
												cardCorpName,
												sequence, 
												productName2, 
												nozzleNo,
												GlobalUtility.getDividedWith1000(liter), 
												GlobalUtility.getDividedWith1000(basePrice), 
												GlobalUtility.getStringValue(price),
												GlobalUtility.getStringValue(productPrice), 
												GlobalUtility.getStringValue(taxPrice),
												GlobalUtility.getStringValue(totalPrice),
												GlobalUtility.getStringValue(receivePrice),
												GlobalUtility.getStringValue(preReceivePrice),
												option,
												monthCnt) ;
		}
		return formatMsg ;
	
	}
	
	
	/**
	 * 외상거래 영수증
	 *  
	 * @param itemInfo
	 * @param uPosMsg
	 * @param diffPrice
	 * @param isAccepted
	 * @return
	 */
	private static String createTrustTatsunoFormat(UPOSMessage_ItemInfo itemInfo, UPOSMessage uPosMsg, int diffPrice, boolean isAccepted) {
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
		int    limitType			= 0;	// 한도 기준(금액 or 수량)
		String customerCode			= "";	// 거래처 코드
		String customerName 		= "";	// 거래처명
		String customerCardNo		= "";	// 거래처 카드번호
		String customerCarNo 		= "";	// 거래처 차량 번호 
		String limit 				= "";	// 한도량
		String saveLimit 			= "";	// 누적 사용량
		String remainLimit 			= "";	// 한도 잔량
		String sequence				= "";	// 순번구분
		String productName2			= "";	// 제품명
		String nozzleNo				= "";	// 노즐 번호
		String liter				= "";	// 주유량
		String basePrice			= "0";	// 단가
		String price				= "";	// 주유금액
		String productPrice			= "";	// 과세 물품가액
		String taxPrice				= "";	// 부가세
		String totalPrice			= "";	// 합계
		String bonusCardNo			= "";	// 보너스카드 번호
		String authNo				= "";	// 보너스카드 승인 번호
		String createPoint			= "";	// 발생 포인트
		String usePoint				= "";	// 가용 포인트
		String totalPoint			= "";	// 주유소 포인트
		String message				= "";	// 보너스카드 메세지
		int option 					= 0; 	// 가득주유 타입

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
					
					if (ICode.ADJBASE_CODE_LIMIT_01.equals(dwPumpM.getAdjbase_code_limit()))
						limitType = PumpMessageFormat.LITER_LIMIT_TYPE;
					else
						limitType = PumpMessageFormat.PRICE_LIMIT_TYPE;		// 한도적용 기준
					
					customerCode	 	= dwPumpM.getCust_code();			// 거래처코드
					customerName 		= dwPumpM.getDrive_name();		// 거래처 명
					customerCardNo 	= dwPumpM.getCust_card_no();	// 거래처 카드번호
					customerCarNo		= dwPumpM.getCar_no();				// 거래처 차량 번호
					limit 						= dwPumpM.getLimit();					// 한도량
					
					if ( ICode.DW_CARDADJ_IND_01.equals(dwPumpM.getCardadj_ind())){
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
					nozzleNo = uPosMsg.getNozzle_no() ;						// 노즐번호
					liter = PumpMUtil.convertNumberFormatFromPumpToPOS(itemInfo.getTotalOilAmount(),3) ;	// 주유량
					
					if ("1".equals(dwPumpM.getRcptunitprc_ind_prt()) )
						basePrice = PumpMUtil.convertNumberFormatFromPumpToPOS(itemInfo.getUnitPrice(),3) ;	// 단가
					
					price 				= itemInfo.getTotalOilPrice_after_discount() ;	// 주유금액
					productPrice 	= itemInfo.getTotalPrice_tax() ;							// 공급가액
					taxPrice 			= itemInfo.getTotalTaxPrice() ;							// 새액
					totalPrice 		= itemInfo.getTotalOilPrice_after_discount() ;	// 주유 금액
					
					bonusCardNo		= uPosMsg.getBonRSCard_no();				// 보너스카드 번호
					authNo				= uPosMsg.getBonRSCard_authNo();		// 보너스카드 승인 번호
					createPoint			= uPosMsg.getGs_point1();						// 발생 포인트
					usePoint				= uPosMsg.getGs_point2();						// 가용 포인트
					totalPoint			= "0";															// 주유소 포인트
					message				= uPosMsg.getBonRS_msg();					// 보너스카드 메세지
					option 				= DasNoSelfPumpingManager.getInstance().getCurrentOption(nozzleNo) ;					
				}
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}
		
		if ( IUPOSConstant.MESSAGETYPE_0084.equals(uPosMsg.getMessageType())){
			formatMsg = PumpMessageFormat.createTrustWithBonusTatsunoFormat(	productName1	
												, storeCode		
												, date			
												, tel				
												, manager			
												, wDate			
												, odtNo			
												, transactionNo	
												, storeName		
												, representative	
												, represent		
												, address			
												, limitType		
												, customerCode	
												, customerName 	
												, customerCardNo 	
												, customerCarNo 	
												, limit 			
												, saveLimit 		
												, remainLimit 	
												, sequence		
												, productName2	
												, nozzleNo		
												, liter			
												, basePrice		
												, price			
												, productPrice	
												, taxPrice		
												, totalPrice
												, bonusCardNo
												, authNo
												, createPoint
												, usePoint
												, totalPoint
												, message
												,option) ;
		} else {
			formatMsg = PumpMessageFormat.createTrustTatsunoFormat(		productName1	
												, storeCode		
												, date			
												, tel				
												, manager			
												, wDate			
												, odtNo			
												, transactionNo	
												, storeName		
												, representative	
												, represent		
												, address			
												, limitType		
												, customerCode	
												, customerName 	
												, customerCardNo 	
												, customerCarNo 	
												, limit 			
												, saveLimit 		
												, remainLimit 	
												, sequence		
												, productName2	
												, nozzleNo		
												, liter			
												, basePrice		
												, price			
												, productPrice	
												, taxPrice		
												, totalPrice
												,option) ;
		}
		return formatMsg ;
	}
	
	

	/**
	 * 보너스 누적 전문을 생성한다.
	 * 
	 * @param khproc_no		: KH 처리번호
	 * @param nozzle_no		: 노즐 번호
	 * @param bonRSCard_no	: 보너스 카드 번호
	 * @return
	 */
	public static UPOSMessage createUPOSMessage_0013(String khproc_no, String nozzle_no, String bonRSCard_no) {
		UPOSMessage uPosMsg = null ;
		
		LogUtility.getPumpMLogger().info("[Pump M] Bonus Accumulation Request -> UPOSMessage(0013)") ;			
		
		String deviceType = IUPOSConstant.DEVICE_TYPE_3S ;
		UPOSMessage_ItemInfo item_info = null ;
		UPOSMessage_CampInfo camp_info = null ;
		String payment_amt = "" ;
		String loyalty_password = "" ;
		String loyality_type = "" ;
		String pos_ip = UPOSUtil.getPOSIP() ;
		String pos_port = UPOSUtil.getPOSPort() ;
		String bonRSCard_ID = "" ;
		String local_point = "" ;
		String local_occurPoint = "" ;
		String pos_saleDate = UPOSUtil.getPosSaleDate() ; 

		try {
			T_KH_PUMP_TRData trData = T_KH_PUMP_TRHandler.getHandler().getT_KH_PUMP_TRData(khproc_no) ;
			String trBasePrice = trData.getPreset_baseprice() ;
			
			if ((trBasePrice == null) || (trBasePrice.equals("")) || (trBasePrice.equals("0"))) {
				trBasePrice = trData.getBaseprice() ;
			}
			
			item_info = UPOSUtil.getUPOSMessage_ItemInfo(nozzle_no, khproc_no, trData.getEqpm_qty(), trBasePrice, trData.getEqpm_amt_prc()) ;
			payment_amt = GlobalUtility.getStringValue(trData.getEqpm_amt_prc()) ;
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}

		uPosMsg = CreateUPOSMessage.createUPOSMessage_0013(	deviceType ,
				khproc_no ,
				nozzle_no ,
				"",
				item_info ,
				bonRSCard_no ,
				camp_info ,
				payment_amt ,
				loyalty_password ,
				loyality_type ,
				pos_ip ,
				pos_port ,
				bonRSCard_ID ,
				local_point ,
				local_occurPoint ,
				pos_saleDate,
				"",
				"",
				"",
				"",
				"",
				"",									
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"") ;
		return uPosMsg ;														
	}
	
	/**
	 * 다쓰노 카드 결제 요청 전문을 UPOSMessage Class 로 변환을 요청한다. 다쓰노 셀프의 카드 결제 요청 전문은 HE_WorkingMessage
	 * Object 이며, 그 Object 의 Bonus_card 필드 유무 및 Card_Type 에 따라 그에 상응하는 UPOSMessage Object 로 변환한다.
	 * 
	 * 	Card_type								Bonus_card 있을 경우		Bonus_card=Space 인 경우
	 * 		1 : 신용카드(+보너스) 승인 요청
	 * 			-> UPOS (0031 , 0033)			0031					0033
	 * 		2 : 신용카드(+보너스) 승인 취소 요청
	 * 			-> UPOS (8031 , 8033)			8031					8033
	 * 
	 * @param workingMsg			: HE_WorkingMessage Object
	 * @param khproc_no				: KH 처리번호
	 * @param ignoreBonusCard		: 보너스 카드 무시 여부
	 * @return
	 */
	public static UPOSMessage createUPOSMessageFromWorkingMessage_DaSNo(WorkingMessage workingMsg, 
			String khproc_no, boolean ignoreBonusCard) {
		LogUtility.getLogger().info("[Pump M] Convert WorkingMessage to UPOSMessage for DaSNo.khproc_no=" + khproc_no) ;
		UPOSMessage uPosMsg = null ;
		HE_WorkingMessage heWorkingMsg = (HE_WorkingMessage) workingMsg ;
		
		heWorkingMsg.print();

		String nozzleNo = heWorkingMsg.getConnectNozzleNo() ; // 노즐 번호 (ODT 번호가 아님.)
		String cardType = heWorkingMsg.getCardType() ;
		int cardTypeInt = Integer.parseInt(cardType) ;
		String card_number = heWorkingMsg.getCardNumber() ;
		String bonus_card = PumpMUtil.getRealBonusCardNumber(heWorkingMsg.getBonusCard()) ;
		String basePrice = PumpMUtil.convertNumberFormatFromPumpToStandardFormat(heWorkingMsg.getBasePrice(),2) ;
		String payPrice = GlobalUtility.getPositiveValue(heWorkingMsg.getPrice()) ;
		String liter = PumpMUtil.convertNumberFormatFromPumpToStandardFormat(heWorkingMsg.getLiter(),3) ;
		String cashReceiptNo = PumpMUtil.getRealBonusCardNumber(heWorkingMsg.getCashReceiptNo());
//		String cashCount = heWorkingMsg.getCashCount();
		String custCardNo = heWorkingMsg.getCustCardNo();

//		여전법 대응에 따른 UposMessage 추가, 2015.11.19 - cwi 
		String creditCardReading_type = IConstant.CreditCardReading_Type_MAGNETIC ;
		String chipData = "";
		String certification_id = "";
		String signImage_Info = "";
		String signImage_Data = "";
		String term_ID = ODTUtility_Common.getTermId() ;
		String store_CD = ODTUtility_Common.getStoreCode();
		String encryptCredit_no = "";
		String creditPassCode = "";
		String selfPayment_type = "" ; // 2016.03.31 WooChul Jung. UPOSMessage 으로 Converting 한 이후, 이 함수를 호출한 함수에서 selfPayment_type 설정.
		String payment_Tax = GlobalUtility.getTaxPrice(payPrice) ;
		String charge = "";
		String credit_Round = "";
		String trx_No = ODTUtility_Common.getTrxNo();
		String trx_Seq = ODTUtility_Common.getTrxSeq();
		String term_Ver = ODTUtility_Common.getRomVer();
		String rTrade_Yn = "=";
		String coupon_Trade_Type = "0";
		String coupon_Acquier_Type = "";
		String term_Res_Code = "00";
		String txt_Direction = "0000";
		String fallback_Trx_Reason = "00";
		
		// tatsuno_hs okdhp7 (2012.12)
		String posEntryMode = "";
		String receipCard[] = heWorkingMsg.getCashReceiptNo().split("=");
		int dataLen = receipCard[0].length();
		LogUtility.getLogger().info("[Pump M] receipCard dataLen=" +  dataLen);
		
		/*
	 	AS-IS
		if(dataLen >= 16)
			posEntryMode = "9"; // 카드리딩
		else
			posEntryMode = "2"; // Key in
		*/
		
		
		//	PI2 20160324 twlee 장애대응 관련하여  현금영수증 요청 코드 변경
		//	보너스카드번호 (16) / 주민번호 (13)/ 사업자번호 (10) / 핸드폰 번호 (10-12)
		//  국세청송신주체(01:GSC, 02:스마트로) + 거래자구분(0:소비자, 1:사업자) + 확인자구분(0:신용카드번호 1:주민등록번호 2:사업자등록번호 3:기타 9:보너스카드번호)
		if (dataLen == 16 || dataLen == 18) {
			// 보너스 카드번호
			posEntryMode = "9" ;
			creditCardReading_type = IConstant.CreditCardReading_Type_MAGNETIC;
		} else if (dataLen == 13) {
			// 20160323 twlee PI2  주민번호로 현금영수증 처리를 막음
			posEntryMode = "1" ;
			creditCardReading_type = IConstant.CreditCardReading_Type_Keyin;
		} else if (dataLen == 10) {
			// 사업자 번호
			posEntryMode = "2" ;
			creditCardReading_type = IConstant.CreditCardReading_Type_Keyin;
		} else if (dataLen == 0) {
			//	20160707 twlee 
			//	신용카드 결제시 사용됨
			//	신용카드 결제시 현금영수증카드 번호가 올라오지 않아 dataLen이 0으로 됨. 따라서 신용카드 타입을 CreditCardReading_Type_MAGNETIC로 처리해야함.
			posEntryMode = "" ;
			creditCardReading_type = IConstant.CreditCardReading_Type_MAGNETIC;
		}else {
			// 핸드폰 번호
			posEntryMode = "3" ;
			creditCardReading_type = IConstant.CreditCardReading_Type_Keyin;
		}
		
		String loyaltyTypeData = "01" + heWorkingMsg.getLoyaltyType() + posEntryMode;
		//AS-IS LogUtility.getLogger().info("[Pump M] 현금영수증 거래구분=[" +  loyaltyTypeData + "] (거래자구분: 3번째 0=소비자 1=사업자, 입력방법: 4번째 2=KeyIn 9=카드)");
		 LogUtility.getLogger().info("[Pump M] 현금영수증 거래구분=[" +  loyaltyTypeData + "]국세청송신주체(01:GSC, 02:스마트로) + 거래자구분(0:소비자, 1:사업자) + 확인자구분(0:신용카드번호 1:주민등록번호 2:사업자등록번호 3:기타 9:보너스카드번호)");
		
		// tatsuno_hs okdhp7 (2012.12)
		String monthCount = heWorkingMsg.getMonthCount();
		
		//if(!monthCount.equals("61")) { // 61=신용포인트 결제
		// 2013.04.08 ksm 박동화 B 요청으로 수정.
		if(Integer.parseInt(monthCount) < 60 ) { // 신용포인트 결제라고 처리하는 기준이 60일 경우 
			// 이강호 2014-06-27
			//다쓰노(현성) 리터로 지정시 5만원 이상 결제시 할부 선택기능 안됨.
			//리터로 주유허가를 요청했을 때는 payPrice = 0 6만원결제 건이지만 할부적용안되고 있음.
			//ex) 단가:2000, 주유30L인경우 2000*30 = 6만원결제 건이지만 할부적용안되고 있음.
			if(Math.max(Integer.parseInt(payPrice), Float.parseFloat(basePrice)*Float.parseFloat(liter)) < 50000){
				monthCount = "0"; // 미만주유 재승인시 5만원 미만은 강제로 일시불 승인 요청하기 위함.
				LogUtility.getLogger().info("[Pump M] 5만원이하 강제 일시불 승인요청(미만주유 재승인시 사고방지용)");
			}
		}
		
		String loyalty_password = heWorkingMsg.getBonusPin().trim(); // tatsuno_hs okdhp7 (2012.12)

		LogUtility.getLogger().info("[Pump M] 할부개월수==" + monthCount);
				
		POS_DW dwPumpM = null ;
				
		if (card_number != null) {
			card_number.trim() ;
		}

		if (Double.parseDouble(basePrice) == 0) {
//			LogUtility.getLogger().debug("BASE PRICE IS 0");
			LogUtility.getLogger().debug("[Pump M] basePrice=" + heWorkingMsg.getBasePrice());
		}
		/**
		 * [2008.11.18] 다쓰노 셀프의 경우 결제 요청 전문 전송시 금액 혹은 리터 둘중 하나만 채워서 온다.
		 */
		if (!payPrice.equals("0")) {
			// 정액
			liter = GlobalUtility.getValueByCertainDecimal(PumpMUtil.calculateLiterFromPriceAndBasePrice(payPrice, basePrice),3) ;
		} else {
			// 정량
			payPrice = GlobalUtility.getPositiveValue(GlobalUtility.multiple(liter, basePrice)) ;			
		}
		
		boolean isCash 		 	= DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzleNo).isCash();
		boolean isCustCard 	 	= DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzleNo).isCustCard();
		boolean isCashCard 	 	= DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzleNo).isCashCard();
		boolean rlt  		 	= DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzleNo).isBonusCard();
		boolean isCreditCard 	= DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzleNo).isCreditCard();		
		
		int intDealType = 0;
		String strData = "";
		if (isCash ) {
			if (!isCashCard && !rlt) 			intDealType = 1;	//일반 (현금) : 보너스 누적(X) + 현금영수증(X)
			else if (!isCashCard && rlt)		intDealType = 2;	//일반 (현금) : 보너스 누적(O) + 현금영수증(X)
			else if (isCashCard && !rlt)		intDealType = 3;	//일반 (현금) : 보너스 누적(X) + 현금영수증(O)
			else if (isCashCard && rlt) 		intDealType = 4;	//일반 (현금) : 보너스 누적(O) + 현금영수증(O)
		} else if (isCreditCard) {
			if (!rlt) 
				intDealType = 11;	//신용 : 보너스 누적(X)
			else
				intDealType = 12;	//신용 : 보너스 누적(O)
			
		} else if (isCustCard && !isCreditCard && !rlt){
			intDealType = 21;	//외상 거래처 : 보너스 누적(X)
		} else if (isCustCard && rlt) {
			SqlSession session = null;
			try {
				session = SqlSessionFactoryManager.openSqlSession();
				// 거래처 보너스 적립 여부 판단
				T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(session, IConstant.POSPORTOCOL_CODEMASTER_0256);
				
				if ("0".equals(strData)) {
					LogUtility.getLogger().debug("[Pump M] 외상거래처 보너스 적립 거부 됨."); 
					intDealType = 21;	//외상 거래처 : 보너스 누적(X)
				} else {
					LogUtility.getLogger().debug("[Pump M] 외상거래처 보너스 적립 승인."); 
					
					POSHeader posMsg = PumpMODTSaleManager.getCustPOSPumpM(heWorkingMsg.getConnectNozzleNo()) ;
					
					if ((posMsg != null) && (posMsg instanceof POS_DW))
						dwPumpM = (POS_DW) posMsg ;
					
					intDealType = 22;	//외상 거래처 : 보너스 누적(O)
				}
			} catch (Exception e) {
				LogUtility.getLogger().error(e.getMessage(),e);;
			} finally {
				SqlSessionFactoryManager.closeSqlSession(session);
			}
		}
		
		String maskingCardNo = PumpMessageFormat.getPrintFormatCardNumber(card_number, true).replaceAll("-", "") ;
		String maskingCashReceiptNo = PumpMessageFormat.getPrintFormatCardNumberForPI2(cashReceiptNo, false);
		String cashReceiptNoForPI2 = ODTUtility_Common.getChangeCashReceiptNumber(cashReceiptNo);
		
		UPOSMessage_ItemInfo itemInfo = UPOSUtil.getUPOSMessage_ItemInfo(	nozzleNo, 
																			khproc_no,
																			liter, 
																			basePrice, 
																			payPrice) ;
		
		switch (cardTypeInt) {
		case 1:
			switch (intDealType) {
			case 1 :
				//일반 영수증은 pumpadapter에 영수증만 출력해주고 결과전문을 pos에 전송해준다.
				LogUtility.getLogger().info("[Pump M] Money Response -> UPOSMessage(0012)") ;

				uPosMsg = CreateUPOSMessage.createUPOSMessage_0012(	IUPOSConstant.DEVICE_TYPE_3S,
																	khproc_no,
																	nozzleNo, 
																	null,
																	itemInfo, 
																	custCardNo,
																	null,
																	null,
																	"1",
																	payPrice,
																	PumpMUtil.getOdtTermId(),
																	"1") ;	
				break;
			case 2 :
				LogUtility.getLogger().info("[Pump M] Money+Bonus Request -> UPOSMessage(0013)") ;
				
				uPosMsg = CreateUPOSMessage.createUPOSMessage_0013(	IUPOSConstant.DEVICE_TYPE_3S,
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
																	"",
																	"",
																	"",
																	UPOSUtil.getPosSaleDate(),
																	"",
																	"",
																	"",
																	"",
																	"1",
																	"",
																	creditCardReading_type ,
																	chipData ,
																	certification_id ,
																	signImage_Info ,
																	signImage_Data ,
																	term_ID ,
																	store_CD ,
																	card_number ,
																	creditPassCode ,
																	selfPayment_type ,
																	payment_Tax ,
																	charge ,
																	credit_Round ,
																	"",
																	trx_No ,
																	trx_Seq ,
																	term_Ver ,
																	rTrade_Yn ,
																	coupon_Trade_Type ,
																	coupon_Acquier_Type ,
																	term_Res_Code ,
																	txt_Direction ,
																	fallback_Trx_Reason) ;	
				break;
			case 3 :
				LogUtility.getLogger().info("[Pump M] 국세청현금영수증  -> UPOSMessage(0015)") ;
				uPosMsg = CreateUPOSMessage.createUPOSMessage_0015(	IUPOSConstant.DEVICE_TYPE_3S,
																	khproc_no,
																	nozzleNo,
																	null,
																	itemInfo,
																	maskingCashReceiptNo,
																	null,
																	payPrice,
																	//"0109",
																	loyaltyTypeData, // tatsuno_hs okdhp7 (2012.12)
																	UPOSUtil.getPOSIP(),
																	UPOSUtil.getPOSPort(),
																	UPOSUtil.getPosSaleDate(),
																	"",
																	"",
																	"",
																	"",
																	"",
																	"1",
																	"",
																	creditCardReading_type ,
																	chipData ,
																	certification_id ,
																	signImage_Info ,
																	signImage_Data ,
																	term_ID ,
																	store_CD ,
																	cashReceiptNoForPI2 ,
																	creditPassCode ,
																	selfPayment_type ,
																	payment_Tax ,
																	charge ,
																	credit_Round ,
																	"",
																	trx_No ,
																	trx_Seq ,
																	term_Ver ,
																	rTrade_Yn ,
																	coupon_Trade_Type ,
																	coupon_Acquier_Type ,
																	term_Res_Code ,
																	txt_Direction ,
																	fallback_Trx_Reason);
				break;
			case 4 :
				LogUtility.getLogger().info("[Pump M] 국세청현금영수증+GS보너스요청 -> UPOSMessage(0053)") ;
				
				uPosMsg = CreateUPOSMessage.createUPOSMessage_0053(	IUPOSConstant.DEVICE_TYPE_3S,
																	khproc_no,
																	nozzleNo,
																	null,
																	itemInfo,
																	maskingCashReceiptNo,
																	bonus_card,
																	null,
																	payPrice,
																	null,
																	//"0109",
																	loyaltyTypeData, // tatsuno_hs okdhp7 (2012.12)
																	UPOSUtil.getPOSIP(),
																	UPOSUtil.getPOSPort(),
																	"",
																	"",
																	"",
																	UPOSUtil.getPosSaleDate(),
																	"",
																	"",
																	"",
																	"",
																	"1",
																	"",
																	creditCardReading_type ,
																	chipData ,
																	certification_id ,
																	signImage_Info ,
																	signImage_Data ,
																	term_ID ,
																	store_CD ,
																	cashReceiptNoForPI2 ,
																	creditPassCode ,
																	selfPayment_type ,
																	payment_Tax ,
																	charge ,
																	credit_Round ,
																	"",
																	trx_No ,
																	trx_Seq ,
																	term_Ver ,
																	rTrade_Yn ,
																	coupon_Trade_Type ,
																	coupon_Acquier_Type ,
																	term_Res_Code ,
																	txt_Direction ,
																	fallback_Trx_Reason,
																	"3") ;	
				break;
			case 11 : 
				LogUtility.getLogger().info("[Pump M] Credit Request -> UPOSMessage(0031)") ;
				
				uPosMsg = CreateUPOSMessage.createUPOSMessage_0031 (IUPOSConstant.DEVICE_TYPE_3S, 							
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
																	//"0",
																	monthCount, // tatsuno_hs okdhp7 (2012.12)
																	null,
																	null,
																	null,
																	null,
																	null,
																	null,
//																	 여전법 대응에 따른 전문 수정, PI2, 2105-11-19 - cwi
																	creditCardReading_type,
																	chipData,
																	certification_id,
																	signImage_Info,
																	signImage_Data,
																	term_ID,
																	store_CD,
																	card_number,
																	creditPassCode,
																	selfPayment_type,
																	payment_Tax,
																	charge,
																	credit_Round,
																	null,
																	trx_No,
																	trx_Seq,
																	term_Ver,
																	rTrade_Yn,
																	coupon_Trade_Type ,
																	coupon_Acquier_Type ,
																	term_Res_Code ,
																	txt_Direction ,
																	fallback_Trx_Reason) ;	
				break;
			case 12 :
				LogUtility.getLogger().info("[Pump M] Credit+Bonus Request -> UPOSMessage(0033)") ;
				
				uPosMsg = CreateUPOSMessage.createUPOSMessage_0033 (IUPOSConstant.DEVICE_TYPE_3S, 
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
																	//"0",
																	monthCount, // tatsuno_hs okdhp7 (2012.12)
																	null,
																	null,
																	null,
																	null,
																	null,
																	null,
//																	 여전법 대응에 따른 전문 수정, PI2, 2105-11-19 - cwi
																	creditCardReading_type,
																	chipData,
																	certification_id,
																	signImage_Info,
																	signImage_Data,
																	term_ID,
																	store_CD,
																	card_number,
																	creditPassCode,
																	selfPayment_type,
																	payment_Tax,
																	charge,
																	credit_Round,
																	null,
																	trx_No,
																	trx_Seq,
																	term_Ver,
																	rTrade_Yn,
																	coupon_Trade_Type ,
																	coupon_Acquier_Type ,
																	term_Res_Code ,
																	txt_Direction ,
																	fallback_Trx_Reason,
																	"1") ;
				break;
			case 21 :
				LogUtility.getLogger().info("[Pump M] 외상 응답전문  -> UPOSMessage(0082)") ;
				
				//할인 고객인 경우 item info를 재구성해야되는 모르겠음 일단 테스트 후 결정
				
				uPosMsg = CreateUPOSMessage.createUPOSMessage_0082(	IUPOSConstant.DEVICE_TYPE_3S, 
																	khproc_no, 
																	nozzleNo, 
																	null,
																	itemInfo, 
																	custCardNo,
																	null,
																	null,
																	null,
																	null, 
																	null, 
																	null, 
																	null,
																	null,
																	null,
																	null,
																	null, 
																	null, 
																	null, 
																	null, 
																	null, 
																	null, 
																	"1",
																	null,
																	payPrice,
																	PumpMUtil.getOdtTermId(),
																	"1", 
																	"1", 
																	"2", 
																	null,
																	"2",
																	null,
																	null) ;

				break;
			case 22 :
				LogUtility.getLogger().info("[Pump M] 외상 + 보너스 요청전문  -> UPOSMessage(0083)") ;
				
				uPosMsg = CreateUPOSMessage.createUPOSMessage_0083(	IUPOSConstant.DEVICE_TYPE_3S, 
																	khproc_no, 
																	nozzleNo, 
																	null,
																	itemInfo,
																	dwPumpM.getCust_card_no(),
																	dwPumpM.getCust_code(),
																	dwPumpM.getCar_no(),
																	bonus_card, 
																	null, 
																	payPrice,
																	"", 
																	"", 
																	UPOSUtil.getPOSIP(),
																	UPOSUtil.getPOSPort(),
																	"",
																	"",
																	"",
																	UPOSUtil.getPosSaleDate(), 
																	null,
																	null,
																	null,
																	null,
																	null,
																	null,
																	creditCardReading_type ,
																	chipData ,
																	certification_id ,
																	signImage_Info ,
																	signImage_Data ,
																	term_ID ,
																	store_CD ,
																	card_number ,
																	creditPassCode ,
																	selfPayment_type ,
																	payment_Tax ,
																	charge ,
																	credit_Round ,
																	"",
																	trx_No ,
																	trx_Seq ,
																	term_Ver ,
																	rTrade_Yn ,
																	"",
																	"",
																	"",
																	"",
																	"") ;

				break;
			}
			break;
		case 2: //--- 미만주유 취소요청 시 호출됨
			/**
			 * 다쓰노의 경우 주유전 최초 승인 전문을 이용하여 주유 완료 이후 취소 전문을 만드는 경우 밖에 없다.
			 * 따라서 최초 승인 전문을 저장하여 이용하도록 한다.
			 */
			uPosMsg = createCancelUPOSMessageFromWorkingMessage_DaSNo(heWorkingMsg, khproc_no) ;
			break;
			
		case 3 : // tatsuno_hs okdhp7 (2012.12)	
			
			//---- (GS포인트 사용 승인 요청)
			LogUtility.getLogger().info("[pump M] GS Bonus Card Request -> UPOSMessage(0061)." + 
					"#khproc_no=" + khproc_no + "#nozzleNo=" + nozzleNo + "#payPrice=" + payPrice + "#liter=" + liter);	
			//LogUtility.getLogger().info("card_number=" + card_number);			
			//LogUtility.getLogger().info("bonusPin=" + loyalty_password);		
			String cardNumber = PumpMUtil.getRealBonusCardNumber(card_number);

//			LogUtility.getLogger().info("khproc_no=" + khproc_no);	
//			LogUtility.getLogger().info("nozzleNo=" + nozzleNo);
			//LogUtility.getLogger().info("cardNumber=" + cardNumber);
//			LogUtility.getLogger().info("payPrice=" + payPrice);
//			LogUtility.getLogger().info("liter=" + liter);
			//LogUtility.getLogger().info("loyalty_password=" + loyalty_password);
			
			uPosMsg = CreateUPOSMessage.createUPOSMessage_0061(	IUPOSConstant.DEVICE_TYPE_3S, 
																khproc_no, 
																nozzleNo, 
																null,
																itemInfo, 
																cardNumber, 
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
																creditCardReading_type ,
																chipData ,
																certification_id ,
																signImage_Info ,
																signImage_Data ,
																term_ID ,
																store_CD ,
																"" ,
																"" ,
																selfPayment_type ,
																payment_Tax ,
																charge ,
																credit_Round ,
																"",
																trx_No ,
																trx_Seq ,
																term_Ver ,
																rTrade_Yn ,
																coupon_Trade_Type ,
																coupon_Acquier_Type ,
																term_Res_Code ,
																txt_Direction ,
																fallback_Trx_Reason) ;
			
			break;
		case 11: //--- 재승인 처리 시 로직 추가 - PI2, CWI, 2015-12-09
			switch (intDealType) {
				case 11 : 
					LogUtility.getLogger().info("[Pump M] Credit Request -> UPOSMessage(0031)") ;
					
					uPosMsg = CreateUPOSMessage.createUPOSMessage_0031 (IUPOSConstant.DEVICE_TYPE_3S, 							
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
																		//"0",
																		monthCount, // gsc_hs okdhp7 (2012.12)
																		null,
																		null,
																		null,
																		null,
																		null,
																		null,
																		// 여전법 대응에 따른 전문 수정, PI2, 2105-11-19 - cwi
																		creditCardReading_type,
																		chipData,
																		certification_id,
																		signImage_Info,
																		signImage_Data,
																		term_ID,
																		store_CD,
																		card_number,
																		creditPassCode,
																		selfPayment_type,
																		payment_Tax,
																		charge,
																		credit_Round,
																		null,
																		trx_No,
																		trx_Seq,
																		term_Ver,
																		rTrade_Yn,
																		coupon_Trade_Type ,
																		coupon_Acquier_Type ,
																		term_Res_Code ,
																		txt_Direction ,
																		fallback_Trx_Reason) ;
					break;
				case 12 :
					LogUtility.getLogger().info("[Pump M] Credit+Bonus Request -> UPOSMessage(0033)") ;
					
					uPosMsg = CreateUPOSMessage.createUPOSMessage_0033 (IUPOSConstant.DEVICE_TYPE_3S, 
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
																		//"0",
																		monthCount, // gsc_hs okdhp7 (2012.12)
																		null,
																		null,
																		null,
																		null,
																		null,
																		null,
																		// 여전법 대응에 따른 전문 수정, PI2, 2105-11-19 - cwi
																		creditCardReading_type,
																		chipData,
																		certification_id,
																		signImage_Info,
																		signImage_Data,
																		term_ID,
																		store_CD,
																		card_number,
																		creditPassCode,
																		selfPayment_type,
																		payment_Tax,
																		charge,
																		credit_Round,
																		null,
																		trx_No,
																		trx_Seq,
																		term_Ver,
																		rTrade_Yn,
																		coupon_Trade_Type ,
																		coupon_Acquier_Type ,
																		term_Res_Code ,
																		txt_Direction ,
																		fallback_Trx_Reason,
																		"1") ;
					break;
			}
		break;
		default : 
				LogUtility.getLogger().info("[Pump M] cardTypeInt1 : " + cardTypeInt);
		}
		
		return uPosMsg ;
	}	
	

	public static UPOSMessage createUPOSMessageFromWorkingMessage_DaSNo_For_Zero(WorkingMessage workingMsg, 
			String khproc_no) {
		LogUtility.getPumpMLogger().info("[Pump M] Convert WorkingMessage to UPOSMessage for DaSNo") ;
		UPOSMessage uPosMsg = null ;
		HE_WorkingMessage heWorkingMsg = (HE_WorkingMessage) workingMsg ;
		
		heWorkingMsg.print();
		
		String nozzleNo 		= heWorkingMsg.getConnectNozzleNo() ; // 노즐 번호 (ODT 번호가 아님.)
		String card_number 	= heWorkingMsg.getCardNumber() ;
		String basePrice 		= PumpMUtil.convertNumberFormatFromPumpToStandardFormat(heWorkingMsg.getBasePrice(),2) ;
		String payPrice 			= GlobalUtility.getStringValue(heWorkingMsg.getPrice()) ;
		
		// 2011.11.11 ksm 
		// 다쓰노 현금 투입후 0리터 주유시 HF 0012 전문에 수량이 0으로 올라감.
		// 결제 전문이고 소모셀프의 경우 수량이 들어감. 통일성을 위해  수량 넣어줌.
		//String liter = PumpMathUtil.convertNumberFormatFromPumpToStandardFormat(heWorkingMsg.getLiter(),3) ;
		String liter = PumpMUtil.calculateLiterFromPriceAndBasePrice(payPrice, basePrice);	
		
//		String cashCount = heWorkingMsg.getCashCount();
		String custCardNo = heWorkingMsg.getCustCardNo();
				
		if (card_number != null) {
			card_number.trim() ;
		}

		UPOSMessage_ItemInfo itemInfo = UPOSUtil.getUPOSMessage_ItemInfo(	nozzleNo,
				khproc_no,
				liter,
				basePrice,
				payPrice) ;
		
		//일반 영수증은 pumpadapter에 영수증만 출력해주고 결과전문을 pos에 전송해준다.
		LogUtility.getPumpMLogger().info("[Pump M] Credit Response -> UPOSMessage(0012)") ;

		uPosMsg = CreateUPOSMessage.createUPOSMessage_0012(	IUPOSConstant.DEVICE_TYPE_3S,
				khproc_no,
				nozzleNo, 
				null,
				itemInfo, 
				custCardNo,
				null,
				null,
				"1",
				payPrice,
				PumpMUtil.getOdtTermId(),
				"1") ;		
		return uPosMsg ;
	}

	/**
	 * 응답 전문을 다쓰노 셀프에 전송하기 위해서 WorkineMessage Object 로 변환한다.
	 * 아래는 UPOSMessage 응답 전문의 Message Type 에 따른 전문 내용이다.
	 * 	신용카드 승인 응답	= 0032
	 * 	신용카드 승인 취소 응답 = 8032
	 * 	신용카드 + 보너스 승인 응답 = 0034
	 * 	신용카드 + 보너스 취소 응답 = 8034
	 * 	BL 체크 = 0072
	 * 
	 * @param uPosMsg	: UPOSMessage Object
	 * @return
	 */
	public static ArrayList<WorkingMessage> createWorkingMessageFromUPOSMessage_DaSNo(UPOSMessage uPosMsg) {
		LogUtility.getPumpMLogger().info("[Pump M] Convert UPOSMessage[" + uPosMsg.getMessageType() 
				+ "] to WorkingMessage for DaSNo.") ;

		ArrayList<WorkingMessage> workMsgArray = new ArrayList<WorkingMessage>() ;	

		int messageType = Integer.parseInt(uPosMsg.getMessageType()) ;
		switch (messageType) {
			case IUPOSConstant.MESSAGETYPE_INT_0032 : { 
				// 신용카드 승인 응답
				LogUtility.getPumpMLogger().info("[Pump M] Credit Response -> WorkingMessage") ;
				workMsgArray = getQM_WorkingMessage_DaSNo(uPosMsg, true) ;
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_8032 : { 
				// 신용카드 승인 취소 응답
				LogUtility.getPumpMLogger().info("[Pump M] Credit Cancel Response -> WorkingMessage") ;
				workMsgArray = getQM_WorkingMessage_DaSNo(uPosMsg, false) ;
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_0034 : { 
				// 신용카드 + 보너스 승인 응답
				LogUtility.getPumpMLogger().info("[Pump M] Credit+Bonus Response -> WorkingMessage") ;
				workMsgArray = getQM_WorkingMessage_DaSNo(uPosMsg, true) ;
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_8034 : { 
				// 신용카드 + 보너스 취소 응답
				LogUtility.getPumpMLogger().info("[Pump M] Credit+Bonus Cancel Response -> WorkingMessage") ;
				workMsgArray = getQM_WorkingMessage_DaSNo(uPosMsg, false) ;
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_0072 : { 
				LogUtility.getPumpMLogger().info("[Pump M] BL Response -> WorkingMessage") ;
				workMsgArray = getQM_WorkingMessage_DaSNo(uPosMsg, true) ;
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_0062 : {  // tatsuno_hs okdhp7 (2012.12)
				LogUtility.getPumpMLogger().info("[Pump M] GS Bonus Card Response -> WorkingMessage") ;
				workMsgArray = getQM_WorkingMessage_DaSNo(uPosMsg, true) ;
				break ;
			}
		}		
		return workMsgArray ;
	}
		
	/**
	 * 선승인 금액 출력 여부 판단
	 * ksm
	 * 
	 * @param nozzleNo	: 노즐번호
	 * @param transactionNo	: K/H처리번호
	 * @param price : 주유금액
	 * @return 0: 선승인 금액 출력안함. 그외 : 선승인금액 출력
	 */
	private static String getPreReceivePrice(String nozzleNo, String transactionNo, String price)
	{
		String preReceivePrice = "0";
		ArrayList<UPOSMessage> preApprvMessage = PumpMODTSaleManager.getUPOSMessageArray(nozzleNo, transactionNo);
		
		if(preApprvMessage != null)
		{			
			UPOSMessage upos = preApprvMessage.get(0);
		
			// 해당 건에 UPOSMessage가 1개 이상이고 신용카드 승인인경우 선승인값 얻어옴.
			if( preApprvMessage.size() > 0)
			{
				String messageType = upos.getMessageType();
				
				if(IUPOSConstant.MESSAGETYPE_0032.equals(messageType) 
					|| IUPOSConstant.MESSAGETYPE_0034.equals(messageType) 
					|| IUPOSConstant.MESSAGETYPE_0062.equals(messageType))
				{
					preReceivePrice = upos.getPayment_amt(); // index 0 인 것이 선승인이므로 이 값을 사용한다.
					
					// 선승인 금액과 주유 금액이 같으면 선승인금액 출력 안함.
					if(preReceivePrice.equals(price))
					{
						preReceivePrice = "0";
					}
				}
			}
		}
		else
		{
			// 승인에 관한 UPos가 없을 때 BL체크방식 가득주유시.
		}
		return preReceivePrice;
	}
	
	/**
	 * 실패 한 경우의 Format 을 만들어야 한다.
	 * 현재는 거스름돈 마이너스를 이용하여 적는다.
	 * 
	 * @param nozzleNo				: 노즐 번호
	 * @param khproc_no			: KH 처리번호
	 * @param pumpingPrice		: 주유 금액
	 * @param pumpingLiter		: 주유량
	 * @param payedPrice		: 결제 금액
	 * @param pumpingBasePrice	: 주유 단가
	 * @param uPosMsg			: 결제 요청에 대한 응답 전문 UPOSMessage
	 * @return
	 */
	public static WorkingMessage getQL_WorkingMessage_DaSNoFromODT(String nozzleNo, String khproc_no, 
			String pumpingPrice, String pumpingLiter, String payedPrice, String pumpingBasePrice, UPOSMessage uPosMsg) {
		
		QL_WorkingMessage qlWorkingMsg = null ;
		
		String odtNo 			= null ;
		String nozzle_no 	= uPosMsg.getNozzle_no() ;
		String receipt 			= null ;
		String length 			= "0" ;
		String mode 			= "1";		//pumpadapㄴter ql전문에 넣을 주유완료 여부 1: 주유완료, 0: 취소
		
		int payedPriceInt = 0 ;
		boolean isAccepted = true ;
		
		String ledCode = uPosMsg.getLed_code() ;

		// 결제 금액은 승인이 난 경우에만 의미가 있도록 한다.
		if ("1".equals(ledCode)) {
			// 승인이 난 경우
			payedPriceInt = Integer.parseInt(payedPrice) ;
			isAccepted = true ;
		}
		// ksm 2012.03.21 현금거래처 보너스 거절시 거스름돈 -금액 출력 문제로 추가
		// 결제금액을 그대로 살림.
		else if ("2".equals(ledCode)){
			// 현금보너스인 경우만
			if(uPosMsg.getMessageType().equals(IUPOSConstant.MESSAGETYPE_0014)) {
				payedPriceInt = Integer.parseInt(payedPrice) ;
				isAccepted = false ;
			}else{
				payedPriceInt = 0 ;
				isAccepted = false ;
			}
		}	
		else {
			// 승인이 나지 않은 경우
			payedPriceInt = 0 ;
			isAccepted = false ;
		}

		int pumpingPriceInt = Integer.parseInt(pumpingPrice) ;
		
		UPOSMessage_ItemInfo itemInfo = UPOSUtil.getUPOSMessage_ItemInfo(nozzleNo, 
													khproc_no,
													pumpingLiter,
													pumpingBasePrice, 
													pumpingPrice) ;
		
		int option = DasNoSelfPumpingManager.getInstance().getCurrentOption(nozzle_no) ;

		int messageType = Integer.parseInt(uPosMsg.getMessageType()) ;		
		
		LogUtility.getPumpMLogger().info("\n\n##### getQL_WorkingMessage_DaSNoFromODT() ") ;
		LogUtility.getPumpMLogger().info("option=" + option + "  messageType=" + messageType) ;		
		LogUtility.getPumpMLogger().info("nozzleNo=" + nozzleNo ) ;		
		LogUtility.getPumpMLogger().info("BounsNo=" + PumpMTransactionManager.getInstance().getCorporateBonus(nozzleNo)) ;		
		
		//	이강호 2014-07 법인카드보너스적립제외
		// 법인카드로 결제한 경우 보너스 카드가 없는 프로세스로 진행되었음. 영수증에는 보너스카드 정보, 출력메시지를 추가해준다.
		if(messageType == IUPOSConstant.MESSAGETYPE_INT_0032 && 
				!PumpMTransactionManager.getInstance().getCorporateBonus(nozzleNo).equals("")){
			messageType =  IUPOSConstant.MESSAGETYPE_INT_0034;
			uPosMsg.setMessageType(IUPOSConstant.MESSAGETYPE_0034);
			uPosMsg.setBonRS_msg("법인카드 보너스 포인트 누적 안함");
			uPosMsg.setBonRSCard_authNo("");
			uPosMsg.setBonRSCard_ID("");
			uPosMsg.setBonRSCard_no(PumpMTransactionManager.getInstance().getCorporateBonus(nozzleNo));
			uPosMsg.setBonRSCRSt_nm("");
			uPosMsg.setBonusSave_type("");
			uPosMsg.setGs_point1("0");
			uPosMsg.setGs_point2("0");
			uPosMsg.setGs_point3("0");
			uPosMsg.setGs_point4("0");
			uPosMsg.setLocal_point("0");
		}
		/**
		 * 주유완료가 0원이고, 선승인에 대한 취소가 제대로 된 경우 createReceiptWhenPumping0 
		 * 함수를 호출하여 영수증 전문을 생성한다. 
		 * 따라서 영수증에 나오는 전문은 주유금액 0 원, 결제 금액 0으로 나오도록 한다.
		 * 신용카드 승인인 경우에만 해당.
		 */
		if (((option == IConstant.FULL_PUMPING_OPTION_5) || (option == IConstant.FULL_PUMPING_OPTION_7)) &&
				(messageType == IUPOSConstant.MESSAGETYPE_INT_0034 || messageType == IUPOSConstant.MESSAGETYPE_INT_0032 ||
				 messageType == IUPOSConstant.MESSAGETYPE_INT_8034 || messageType == IUPOSConstant.MESSAGETYPE_INT_8032 ||
				 messageType == IUPOSConstant.MESSAGETYPE_INT_8062)) { // tatsuno_hs okdhp7 (2012.12) - 추가('8062')
			
			// 주유완료 금액이 0 원인 경우 
			LogUtility.getPumpMLogger().debug("주유금액이 0원이면서 신용승인이 정상적으로 취소된 경우 출력되는 영수증.");
			if(messageType == IUPOSConstant.MESSAGETYPE_INT_8062) // tatsuno_hs okdhp7 (2012.12) 추가('8062')
				receipt = createReceiptBonusPointWhenPumping0(itemInfo, uPosMsg, 0, isAccepted);
			else
				receipt = createReceiptWhenPumping0(itemInfo, uPosMsg, 0, isAccepted);
			length = Integer.toString(receipt.length()) ;
		} 
		else {
			// diffPrice 가 양수인 경우는 사용자가 받아야 할 금액이고, 음수인 경우는 주유소가 사용자로 부터 받아야 할 금액이다.
			// 음수가 나온다라는 자체는 문제가 있으며, 음수가 안나오도록 코드 구현을 개선해야 한다.
			int diffPrice = payedPriceInt - pumpingPriceInt ;
						
			switch (messageType) {
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
				case IUPOSConstant.MESSAGETYPE_INT_0082 : { 
					// 외상거래처 응답. --> 셀프용 
					receipt = createTrustTatsunoFormat(itemInfo, uPosMsg, diffPrice, isAccepted) ;
					length = Integer.toString(receipt.length()) ;
					break ;
				}
				case IUPOSConstant.MESSAGETYPE_INT_0084 : { 
					// 외상거래처 응답. --> 셀프용 
					receipt = createTrustTatsunoFormat(itemInfo, uPosMsg, diffPrice, isAccepted) ;
					length = Integer.toString(receipt.length()) ;
					break ;
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
				case IUPOSConstant.MESSAGETYPE_INT_0062 : { // tatsuno_hs okdhp7 (2012.12) 추가
					//  GS보너스 사용 승인 응답
					receipt = createReceiptByBonusPoint(itemInfo, uPosMsg, diffPrice, isAccepted) ;
		
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
			}		
		}
		
		odtNo = PumpMUtil.getODTNumberFromNozzleNo(nozzle_no) ;	

		qlWorkingMsg = new QL_WorkingMessage(odtNo, nozzle_no, length, receipt, mode, "", "") ;
	
		//주유후 영수증에 세차기OPT 세차할인권 인쇄 
		qlWorkingMsg.setBarCode(Barcode.getBarcodeNumber("3", pumpingPrice, nozzleNo, khproc_no, uPosMsg.getMessageType(), ledCode, null));
		
		
		return qlWorkingMsg ;
	}
	
	

	/**
	 * 다쓰노 ODT 에게 영수증 출력을 위한 WorkingMessage 생성을 요청한다. 이 Method 는 POS 로 부터의 현금 결제로 인한
	 * 주유시 사용된다.
	 * 
	 * @param nozzleNo			: 노즐 번호
	 * @param khproc_no			: KH 처리번호
	 * @param pumpingPrice		: 주유 금액
	 * @param pumpingLiter		: 주유 량
	 * @param payedPrice		: 결제 금액
	 * @param pumpingBasePrice	: 주유 단가
	 * @return
	 */
	public static WorkingMessage getQL_WorkingMessage_DaSNoFromPOS(String nozzleNo, String khproc_no, 
			String pumpingPrice, String pumpingLiter, String payedPrice, String pumpingBasePrice, boolean isPresetFromPOS ) {
		QL_WorkingMessage qlWorkingMsg = null ;
		
		String odtNo = null ;
		String nozzle_no = nozzleNo ;
		String receipt = null ;
		String length = "0" ;
        
		if (isPresetFromPOS)
			receipt = createReceiptByCashFromPos(nozzleNo, khproc_no, pumpingPrice, pumpingLiter, payedPrice, pumpingBasePrice) ;
		else
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
	 * 승인 응답 전문을 받은 이후 주유기 Adapter 로 전송하기 위한 WorkingMessage Array 를 생성 요청한다.
	 * 다쓰노 셀프 ODT 는 승인 응답 전문은 QM 전문이다. 
	 * 
	 * @param uPosMsg	: 승인 응답 전문
	 * @param isAccept	: 승인 or 취소승인 여부
	 * @return
	 */
	private static ArrayList<WorkingMessage> getQM_WorkingMessage_DaSNo(UPOSMessage uPosMsg , boolean isAccept) {
		ArrayList<WorkingMessage> workMsgArray = new ArrayList<WorkingMessage>() ;	

		QM_WoringMessage qmWorkingMsg = null ;
		
		String odtNo = null ;
		String nozzle_no = uPosMsg.getNozzle_no() ;
		String mode = "" ;
		
		/*
		 * UPOS Message			QM_WorkingMessage		QM_WorkingMessage
		 * 		Led-Code		승인응답-mode				취소응답-mode
		 * 	1	승인				1						4
		 * 	2	거부				2						5
		 * 	3	회선 실패			3						6
		 */
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
		odtNo = PumpMUtil.getODTNumberFromNozzleNo(nozzle_no) ;	
		//qmWorkingMsg = new QM_WoringMessage(odtNo, nozzle_no , mode , "") ; //ORG 

		// 단가, 금액까지 올려줘야 함.
		// 단가
		String strBasePrice = DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzle_no).getHeWorkingMsg().getBasePrice();
		
		//금액
		String strPrice =  DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzle_no).getHeWorkingMsg().getPrice();
		String strOilAmt =  DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzle_no).getHeWorkingMsg().getLiter();
		String van_MSG = uPosMsg.getVan_msg();
		String bonRS_MSG = uPosMsg.getBonRS_msg();
		String creditCardReadingType = uPosMsg.getCreditCardReading_type();
		String chipData = new String(uPosMsg.getChipData());
		String displayMsg = uPosMsg.getDisplay_msg();
		String storeCd = uPosMsg.getStore_cd();
		String paymentTax = uPosMsg.getPayment_tax();
		String charge = uPosMsg.getCharge();
		String catTrackingNum = uPosMsg.getCat_tracking_number(); 
		String trxNo = uPosMsg.getTrx_No(); 
		String trxSeq = uPosMsg.getTrx_Seq();
		String cardType = uPosMsg.getCard_Type();
		String trxProperNo = uPosMsg.getTrx_Proper_No();
		String issuerCode = uPosMsg.getIssuer_code();
		String txtDirection = uPosMsg.getTxt_Direction();
		String termResCode = uPosMsg.getTerm_Res_Code();
		String fallbackTrxReason = uPosMsg.getFallback_Trx_Reason();
		String vanResCode = uPosMsg.getVan_Res_Code();
		String payCardBalance = uPosMsg.getPayCard_balance();
		
		qmWorkingMsg = new QM_WoringMessage(odtNo, nozzle_no , mode , "", strOilAmt, strBasePrice, strPrice, "", "", "", van_MSG, bonRS_MSG, creditCardReadingType,
				chipData, displayMsg, storeCd, paymentTax, charge, catTrackingNum, trxNo, trxSeq, cardType, trxProperNo, issuerCode, txtDirection,
				termResCode, fallbackTrxReason, vanResCode, payCardBalance) ;
				
		workMsgArray.add(qmWorkingMsg) ;
		return workMsgArray;
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
	 * 신용 응답 전문과 보너스 누적 전문을 결합한 신용+보너스 응답 전문을 생성한다.
	 * 
	 * @param uposMsg0032	: 신용 응답 전문
	 * @param uposMsg0014	: 보너스 누적 응답 전문
	 * @return
	 */
	public static UPOSMessage mergeTo0034UPOSMessage(UPOSMessage uposMsg0032, UPOSMessage uposMsg0014) {
		LogUtility.getPumpMLogger().info("[Pump M] 0032 (Credit) + 0014 (Bonus) -> 0034 (Credit+Bonus)") ;	
		
		UPOSMessage uposMsg0034 = uposMsg0032 ;
		
		try {
			uposMsg0034 = CreateUPOSMessage.createUPOSMessage_0034(	uposMsg0032.getDeviceType(), 
												uposMsg0032.getPosReceipt_no(), 
												uposMsg0032.getNozzle_no(), 
												uposMsg0032.getEmp_no(), 
												uposMsg0032.getItem_info(), 
												uposMsg0032.getCustCard_No(),
												uposMsg0032.getSs_crStNum(),
												uposMsg0032.getSs_carNum(),
												uposMsg0032.getCreditCard_no(),
												uposMsg0032.getCredit_auth_no(), 
												uposMsg0032.getTrdate_creditCard(), 
												uposMsg0032.getCredit_authInfo(), 
												uposMsg0032.getExp_date(), 
												uposMsg0032.getIssuer_name(), 
												uposMsg0032.getAcquier_code(), 
												uposMsg0032.getAcquier_name(),
												uposMsg0032.getCredit_month(), 
												uposMsg0032.getLed_code(), 
												uposMsg0014.getBonRSCard_no(), 
												uposMsg0014.getBonRSCard_authNo(), 
												uposMsg0014.getTrdate_bonRSCard(),
												uposMsg0014.getBonRSCard_ID(), 
												uposMsg0014.getBonRSCRSt_nm(), 
												uposMsg0014.getGs_point1(), 
												uposMsg0014.getGs_point2(), 
												uposMsg0014.getGs_point3(), 
												uposMsg0014.getGs_point4(), 
												uposMsg0014.getLocal_point(), 
												uposMsg0014.getLocal_occurPoint(),
												uposMsg0014.getLoyaltyReqCode(), 
												uposMsg0032.getTitle_msg(), 
												uposMsg0032.getVan_msg(),
												uposMsg0014.getBonRS_msg(), 
												uposMsg0032.getCamp_info(), 
												uposMsg0032.getReceipt_type(), 
												uposMsg0032.getLoyality_id(), 
												uposMsg0032.getPayment_amt(), 
												uposMsg0032.getTerm_id(), 
												uposMsg0032.getLastPayment_yn(), 
												uposMsg0032.getTaxFreeCust_type(),
												uposMsg0032.getSupply_type(), 
												uposMsg0032.getDeal_type(), 
												uposMsg0032.getLoan_date()) ;
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
		}
		return uposMsg0034 ;
	}	
	
	/**
	 * 다쓰노 셀프의 가득 주유일 경우 마지막 전문에 lastPayment_yn 필드를 1로 설정하여 POS 로 전송한다.
	 * 1. 복합결제미처리완료 (0001) 인 경우는 1 로 설정을 한다.	-> 이 함수 호출 전에 설정을 한다.
	 * 		즉 만약 0001 인 경우는 1 로 설정을 하며, 이 함수를 호출하지는 않는다.
	 * 2. 가득 주유 처리시 마지막 전문이면 1 로 설정을 한다.
	 * 3. 그러치 않은 경우는 전부 0 으로 설정을 한다.
	 * 
	 * @param uposMsg		: UPOSMessage
	 */
	public static void preProcessingBeforeSendingToSaleM(UPOSMessage uposMsg) {
		try {
			String nozzleNo = uposMsg.getNozzle_no() ;
			// 가득 주유인지 여부 확인
			if (DasNoSelfPumpingManager.getInstance().isCurrentFullPumping(nozzleNo)) {
				// 2. 가득 주유 처리시 마지막 전문이면 1 로 설정을 한다.
				if (DasNoSelfPumpingManager.getInstance().isFinalResponseUPOSMessageForCurrentFullPumping(nozzleNo)) {
					// 미전송 확인필요 ksm 2013.11.29
					
					
					uposMsg.setLastPayment_yn(IUPOSConstant.LASTPAYMENT_YN_YES) ;
				} else {
					// 3. 그러치 않은 경우는 전부 0 으로 설정을 한다.
					uposMsg.setLastPayment_yn(IUPOSConstant.LASTPAYMENT_YN_NO) ;
				}
			} else {
				uposMsg.setLastPayment_yn(IUPOSConstant.LASTPAYMENT_YN_NO) ;
			}
		} catch (Exception e) {
			uposMsg.setLastPayment_yn(IUPOSConstant.LASTPAYMENT_YN_YES) ;
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
		}
	}
	
	/**
	 * 0001 전문 전송 여부를 설정한다.
	 * 다쓰노 셀프의 경우 TR 전문을 수신하면 이 함수를 호출하여 0001 전문을 POS 로 전송할 지 결정한다.
	 * 보내는 경우는 다음과 같다.
	 * 		1. POS 로 부터 Preset 에 의한 주유가 아닐 경우
	 * 		2. 가득 주유가 아닌 경우
	 * 		3. ODT 에 의한 결제일 경우
	 * 
	 * @param nozzle_no	: 노즐 번호
	 * @param khproc_no	: KH 처리번호
	 * @param workMsg	: 판매 완료 전문 (TR_WorkingMessage)
	 * @return
	 */
	public static boolean shouldSend0001UPOSMessageToSaleM(String nozzle_no, String khproc_no, WorkingMessage workMsg) {
		boolean rlt = false ;
		
		// Debug
		LogUtility.getPumpMLogger().debug("[Pump M] shouldSend0001UPOSMessageToSaleM in DaSNo") ;
		PumpMTransactionManager.getInstance().printTransactionData(nozzle_no) ;
		
		if (PumpMTransactionManager.getInstance().isPresetFromPOS(nozzle_no)) {
			rlt = false ;
/*			if (PumpMTransactionManager.getInstance().isPayed(nozzle_no)) {
				rlt = true ;
			} else {
				rlt = false ;
			}*/
		} else {
			// 가득 주유인 경우 0001 전문을 POS 로 전송하지 않는다.
			if (DasNoSelfPumpingManager.getInstance().isCurrentFullPumping(nozzle_no)) {
				DasNoODTNozzleInfo selfOdtFullPumpingMgr = DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzle_no);
				HE_WorkingMessage heWorkingMsg = selfOdtFullPumpingMgr.getHeWorkingMsg();			

				if (heWorkingMsg.getPrice().equals(((TR_WorkingMessage)workMsg).getPrice()))
					rlt = true ;
				else
					rlt = false ;
			} else {
				if (PumpMTransactionManager.getInstance().isPayed(nozzle_no)) {
					rlt = true ;
				} else {
					rlt = false ;
				}
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
    	if(IUPOSConstant.RESPOND_LEDCODE_2.equals(uPosMsg.getLed_code()) && IUPOSConstant.MESSAGETYPE_0016.equals(uPosMsg.getMessageType()) ){
    		LogUtility.getPumpMLogger().info("[현금영수증 승인실패] POS로 전송안함. LED_CODE=2, MESSAGE_TYPE=0016" ) ;
    		rlt = false;
    	}

    	return rlt ;	
    }
}