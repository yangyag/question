package com.gsc.kixxhub.module.pumpm.pump.util;

import java.util.ArrayList;
import java.util.Vector;

import org.apache.ibatis.session.SqlSession;

import com.gsc.kixxhub.common.data.ICode;
import com.gsc.kixxhub.common.data.IConstant;
import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.IUPOSConstant;
import com.gsc.kixxhub.common.data.posdata.POS_DW;
import com.gsc.kixxhub.common.data.pump.BA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.BB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.CP_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PI_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SH_RepInfo;
import com.gsc.kixxhub.common.data.pump.SH_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.TD_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.TJ_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.data.pump.XA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.format.PumpMessageFormat;
import com.gsc.kixxhub.common.data.upos.CreateUPOSMessage;
import com.gsc.kixxhub.common.data.upos.UPOSMessage;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_CampInfo;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_ItemInfo;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_ItemInfo_Item;
import com.gsc.kixxhub.common.dbadapter.common.SqlSessionFactoryManager;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_BIN_INFOHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_PRODUCTHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_PUMP_TRHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_STOREHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_PUMP_TRData;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.common.utility.sync.Preamble;
import com.gsc.kixxhub.common.utility.sync.SyncManager;
import com.gsc.kixxhub.module.pumpm.pump.manager.PumpMODTSaleManager;

public class ODTUtility_Recharge {

	public static boolean checkBizBin(String  binChekCardNo){
    	
    	boolean rlt = false;
    	SqlSession session = null;

		// 법인택시인 경우 보너스 적립을 하지 않는다.
		// IT기획팀 의사결정 사항. edited by ykjang 2009.10.14				
		try {
			
			session = SqlSessionFactoryManager.openSqlSession();
			if (T_KH_BIN_INFOHandler.getHandler().isExist(session, "03", binChekCardNo))
				rlt = true;

		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}
		
		return rlt;
    }
	
	
	/**
	 * 충전기로부터 진행된 외상 거래의 경우 판매 완료시 (충전기관점에서 판매완료 - SH 전문) KixxHub 에서 
	 * UPOSMessage 전문을 구성하여 POS 로 전송한다.
	 * 이 경우 충전기에 보내었던 전문인 DW 전문과 SH 전문을 이용하여 UPOSMessage 전문을 생성한다. 
	 * 
	 * < 관련 메일 > 2008년 10월 8일 오후 9시 47분
	 * 
	 * 회의 참석자
	 * 	편윤국 차장, 정동명 과장, 정우철 과장
	 * 가정
	 * 	단가 : 		1,000 원
	 * 	주유량 : 	5 리터
	 * 	주유금액 : 	5,000원
	 * 
	 * 예-1
	 * 	현금 10,000 원 결재 했을 경우
	 * 		UPOSMessage 응답 전문 (POS 로 전송되는 응답 전문)
	 * 			할인전단가 	1,000원
	 * 			할인후단가	1,000원
	 * 			주유량		10 리터
	 * 			할인전금액	10,000원
	 * 			할인후금액	10,000원
	 * 
	 * 예-2 ([2008.10.24] 일에 아래 내용 변경했음)
	 * 	현금 거래처	계약 단가 800 원
	 * 	현금 10,000 원 결재 했을 경우
	 * 		UPOSMessage 응답 전문 (POS 로 전송되는 응답 전문)
	 * 			할인전단가 	1,000원
	 * 			할인후단가	800원
	 * 			주유량		12.5 리터
	 * 			할인전금액	12,500원
	 * 			할인후금액	10,000원
	 * 
	 * [2008.10.24] 회의 참석자 : 편윤국 차장, 정동명 , 정우철
	 * 		1. 현금 거래처인데 현금으로 결재한 경우 리터를 재계산하지 않고, 금액을 재계산한다. -> [2008.10.08] 회의 내용 변경
	 * 		2. 거스름돈이 있는 경우 , 실 주유량과 거스름돈을 뺀 실 금액을 POS 로 전송하도록 한다.
	 * 
	 * @param dwPumpM			: POSProtocol DW 전문
	 * @param shWorkMsg			: PumpA SH 전문
	 * @param khproc_no			: KH 처리번호
	 * @return
	 */
	public static ArrayList<UPOSMessage> createUPOSMessage(POS_DW dwPumpM, SH_WorkingMessage shWorkMsg, 
			String khproc_no) {
		LogUtility.getPumpMLogger().debug("[Pump M] DW 전문과 SH 전문을 이용하여 현금/상품권/외상 UPOSMessage 를 구성한다.") ;
		if (dwPumpM != null) 
			LogUtility.getPumpMLogger().info(dwPumpM.toString());
		if (shWorkMsg != null) shWorkMsg.print() ;
		
		ArrayList<UPOSMessage> uPosMsgArray = new ArrayList<UPOSMessage>() ;
		
		try {
			UPOSMessage uPosMsgCustomer = null ;
			UPOSMessage uPosMsgKeep = null ;
			UPOSMessage uPosMsgCash = null ;	// 현금 응답
			UPOSMessage uPosMsgTicket = null ;	// 상품권 응답
			
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

			String nozzle_no = shWorkMsg.getNozzleNo() ;							// 노즐번호
			String emp_no = PumpMODTSaleManager.getChargingPersonID(nozzle_no) ;	// 충전원 ID
			String custCard_No = "" ;												// 거래처카드번호
			String ss_crStNum = "" ;												// 거래처번호
			String ss_carNum = "" ;													// 거래처차량번호
			String rcptsheetissue_code_amtsale = "" ;								// 매출금액처리구분
			String term_id = "         " ;											// 단말기 번호
			String lastPayment_yn = "0" ;											// 마지막 결제여부
			String led_code = "1" ;													// LED 코드
			String keepDoc_limitDate = "" ;											// 보관증 만료일
	
			if ((dwPumpM != null) && (PumpMObjectValidation.validatePumpMObject(dwPumpM))){
				custCard_No = dwPumpM.getCust_card_no() ;									// 거래처카드번호
				ss_crStNum = dwPumpM.getCust_code() ;										// 거래처번호
				ss_carNum = dwPumpM.getCar_no() ;											// 거래처차량번호
				rcptsheetissue_code_amtsale = dwPumpM.getRcptsheetissue_code_amtsale() ;	// 매출금액처리구분
			}
	
	//		ArrayList<UPOSMessage_ItemInfo_Item> itemInfoList = new ArrayList<UPOSMessage_ItemInfo_Item>() ;
	
			String nozBasePrice = PumpMUtil.convertBasePriceFromPumpToPOS(shWorkMsg.getUp1()) ;			// 계기단가
			String salesBasePrice = PumpMUtil.convertBasePriceFromPumpToPOS(shWorkMsg.getUp2()) ;		// 판매 단가
			String pumpPrice = shWorkMsg.getTotalAMT2() ;		// 판매 금액
			Vector<SH_RepInfo> repInfo = shWorkMsg.getRepInfo() ;
			
			for (int i = 0 ; i < repInfo.size() ; i++) {
				String flag = repInfo.get(i).getFlag() ;
				String liter = PumpMUtil.convertTotalLiterFromPumpTOPOS(repInfo.get(i).getLiter()) ;
				String amt = repInfo.get(i).getAmt() ;
				String keep_no = repInfo.get(i).getKeepNumber() ;
				
				if ("2".equals(flag)) {
					// 외상 일 경우
					LogUtility.getPumpMLogger().debug("[Pump M] SH 전문에 외상 정보가 있습니다.") ;
					
					SH_RepInfo keepInfo = getKeepInfo(repInfo) ;
					if (keepInfo != null) {
						LogUtility.getPumpMLogger().debug("[Pump M] SH 전문에 보관증 발행 정보가 있습니다. 보관증 발행 여부 =" + dwPumpM.getKeepissue_ind()) ;
						String keepLiter = PumpMUtil.convertTotalLiterFromPumpTOPOS(keepInfo.getLiter()) ;
						String keepAmt = keepInfo.getAmt() ;
						
						if (!"1".equals(dwPumpM.getKeepissue_ind())) {
							// 보관증 발행 여부가 '발행' 이 아닌 경우
							// 아닌 경우이지만 SH 전문에는 발행된 보관증이 올라온다. POS 로 전송할때에는 
							// 발행하지 않은 경우의 전문을 생성하여 보내도록 한다.
							UPOSMessage_ItemInfo itemInfo = null ;
							String liter2 = GlobalUtility.substract(liter, keepLiter) ;
							String amt2 = GlobalUtility.getStringValue(GlobalUtility.substract(amt, keepAmt)) ;
							itemInfo = UPOSUtil.createCustomerItemInfo(nozzle_no, 
									khproc_no,
									liter2, 
									salesBasePrice, 
									pumpPrice, 
									amt2) ;
							
							String payment_amt = itemInfo.getTotalOilPrice_after_discount() ;
							uPosMsgCustomer = CreateUPOSMessage.createUPOSMessage_0082(IUPOSConstant.DEVICE_TYPE_3O,
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
							
						} else {
							// 보관증 발행 여부가 '발행' 인 경우 -> 외상정보 및 
							// 거래처 UPOSMessage 생성
							UPOSMessage_ItemInfo itemInfo = null ;
	
							itemInfo = UPOSUtil.createCustomerItemInfo(nozzle_no, 
									khproc_no,
									liter, 
									salesBasePrice, 
									pumpPrice, 
									amt) ;
							
							String payment_amt = itemInfo.getTotalOilPrice_after_discount() ;
							uPosMsgCustomer = CreateUPOSMessage.createUPOSMessage_0082(IUPOSConstant.DEVICE_TYPE_3O,
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
							
							// 보관증 UPOSMessage 생성
							UPOSMessage_ItemInfo_Item itemInfoItem =
								UPOSUtil.getUPOSMessage_ItemInfo_Item(nozzle_no, 
										khproc_no,
										PumpMUtil.convertTotalLiterFromPumpTOPOS(keepInfo.getLiter()), 
										salesBasePrice, 
										keepInfo.getAmt()) ;
							itemInfoItem.setRentlimit_proc_ind_overlimit("00") ;	// 외상 인정
//							itemInfoItem.setKeep_no(makeKeepNumber(keepInfo.getKeepNumber())) ;		// 보관증 번호
							itemInfoItem.setKeep_no(keepInfo.getKeepNumber()) ;		// 보관증 번호
							
							itemInfoItem.setIssue_type("01") ;						// 보관증 유형 : 리터 (default)
							
							UPOSMessage_ItemInfo itemInfoKeep = UPOSMessage_ItemInfo.createUPOSMessage_ItemInfo(itemInfoItem) ;
							
							payment_amt = itemInfoKeep.getTotalOilPrice_after_discount() ;
							uPosMsgKeep = CreateUPOSMessage.createUPOSMessage_0082(IUPOSConstant.DEVICE_TYPE_3O,
													khproc_no,
													nozzle_no,
													emp_no, 
													itemInfoKeep, 
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
							
						}
					} else {				
						// 보관증 정보가 없을 경우
						UPOSMessage_ItemInfo itemInfo = null ;
	
						itemInfo = UPOSUtil.createCustomerItemInfo(nozzle_no, 
								khproc_no,
								liter, 
								salesBasePrice, 
								pumpPrice, 
								amt) ;
						
						String payment_amt = itemInfo.getTotalOilPrice_after_discount() ;
						uPosMsgCustomer = CreateUPOSMessage.createUPOSMessage_0082(IUPOSConstant.DEVICE_TYPE_3O,
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
					}
				}  else if ("1".equals(flag)) {
					int nozProtocolInt = PumpMUtil.getConnectedRepODTProtocolFromNozzleNo(nozzle_no) ;

					// 현금 응답 (0012)
					LogUtility.getLogger().debug("[Pump M] SH 전문에 현금 응답 정보가 있습니다.") ;		
					
					UPOSMessage_ItemInfo item_infoCash = null ;
					String payment_amtCash = null ;
					
					/**
					 * 2016.04.04 WooChul Jung
					 * 	To-Be LPG ODT 의 경우, 현금거래처 + 현금의 경우 재계산된 금액으로 ODT 에서 SH 에 포함되어서 올라옴. (As-Is 는 아님)
					 * 
					 * 예시>
					 *
					 *   점두가: 1,120 원, 거래처단가: 1,020 원
					 *   
					 *   As-Is  
					 *         1. 현금거래처 + 현금             
					 *              SH  전문 수량:   8.930 L   
					 *              SH  전문 금액:  10,002 원
					 *              POS 표시 수량:   8.930 L
					 *              POS 표시 금액:   9,108 원 (ODT서 올라온 금액을 KixxHub서 재계산 후 POS 전송)
					 *              
					 *         2. 일반고객 + 현금
					 *              SH  전문 수량:   8.930 L   
					 *              SH  전문 금액:  10,002 원
					 *              POS 표시 수량:   8.930 L
					 *              POS 표시 금액:  10,002 원 
					 *              
					 *   To-Be  
					 *         1. 현금거래처 + 현금           
					 *              SH  전문 수량:   9.879 L   
					 *              SH  전문 금액:  10,076 원
					 *              POS 표시 수량:   9.879 L
					 *              POS 표시 금액:  10,076 원 
					 *              
					 *         2. 일반고객 + 현금
					 *              SH  전문 수량:   8.991 L   
					 *              SH  전문 금액:  10,069 원
					 *              POS 표시 수량:   8.991 L 
					 *              POS 표시 금액:  10,069 원
					 *   
					 * 
					 */
					switch (nozProtocolInt) {
					
						case IPumpConstant.PUMP_PROTOCOL_Recharge : {
							boolean isDiscount = false ;
							
							// 거래처로 인한 할인된 단가가 있는지 조사한다.
							String tempSalesBasePrice = PumpMObjectValidation.getSalesBasePriceIfCustomer(nozzle_no) ;
							if (tempSalesBasePrice != null) {
								try {
									if ((Double.parseDouble(tempSalesBasePrice) > 0) && 
											(Double.parseDouble(tempSalesBasePrice) != Double.parseDouble(nozBasePrice))) {
										salesBasePrice = tempSalesBasePrice ;
										isDiscount = true ;
									} 
								} catch (Exception e) {
									LogUtility.getLogger().error(e.getMessage(), e) ;
								}
							}
							
							// 할인된 단가가 없을 경우 점두가를 설정한다.
							if (!isDiscount) {
								salesBasePrice = nozBasePrice ;
							}
							
							// [2008.10.08] 회의 참석자 : 편윤국, 정동명, 임성춘, 정우철
							
							// 거스름 돈이 있거나 혹은 할인된 단가인 경우 리터를 재계산한다. 이는 다음과 같은 이유 때문이다.
							// 1. ODT 로 부터 올라오는 리터는 주유량이며, 금액은 받은 금액(주유금액이 아님) 이다. 이 경우 주유량을 재계산할 필요가 있다.
							// 2. 거래처 인 경우 계약단가와 받은 금액을 이용하여 재계산할 필요가 있다.
			/*				if ((Double.parseDouble(PumpMathUtil.getPositiveValue(keep_no)) != 0 ) || isDiscount) {
								liter = Double.toString(PumpMathUtil.getPresetLiter(Double.parseDouble(amt), Double.parseDouble(salesBasePrice))) ;
								LogUtility.getLogger().debug("[Pump M] Liter 를 재계산합니다.liter="+liter) ;
							}
			*/				
							// [2008.10.24] 회의 참석자 : 정동명 , 정우철
							// 현금 거래처인데 현금으로 결재한 경우 리터를 재계산하지 않고, 금액을 재계산한다. -> [2008.10.08] 회의 내용 변경 
							// 거스름돈을 뺀다.
							if (Double.parseDouble(GlobalUtility.getPositiveValue(keep_no)) != 0 ) {
								amt = GlobalUtility.getPositiveValue(GlobalUtility.substract(amt, keep_no)) ;
							}
							
							// 만약 할인 거래처인 경우 주유량과 판매 금액 및 거래처 판매처리 구분을 통해서 금액을 재계산한다.
							if (isDiscount) {
								amt = PumpMUtil.handleRcptsheetissue_code_amtsale(salesBasePrice, 
										liter, 
										rcptsheetissue_code_amtsale) ;
							}
			
							item_infoCash = UPOSUtil.getUPOSMessage_ItemInfo(nozzle_no, 
									khproc_no, 
									liter, 
									salesBasePrice, 
									amt) ;
							payment_amtCash = item_infoCash.getTotalOilPrice_after_discount() ;
							break ;
						}
						case IPumpConstant.PUMP_PROTOCOL_NewRecharge : {
							item_infoCash = UPOSUtil.createCustomerItemInfo(nozzle_no, 
									khproc_no,
									liter, 
									salesBasePrice, 
									pumpPrice, 
									amt) ;
							
							payment_amtCash = item_infoCash.getTotalOilPrice_after_discount() ;
							break ;
						}						
					}	
					
					uPosMsgCash = CreateUPOSMessage.createUPOSMessage_0012(IUPOSConstant.DEVICE_TYPE_3O,
															khproc_no, 
															nozzle_no, 
															emp_no, 
															item_infoCash, 
															custCard_No, 
															ss_crStNum, 
															ss_carNum, 
															lastPayment_yn, 
															payment_amtCash, 
															term_id, 
															led_code) ;
				} else if ("4".equals(flag)) {
					// 상품권 응답 (0022)
					LogUtility.getPumpMLogger().debug("[Pump M] SH 전문에 상품권 응답 정보가 있습니다.") ;
					boolean isDiscount = false ;
					
					String tempSalesBasePrice = PumpMObjectValidation.getSalesBasePriceIfCustomer(nozzle_no) ;
					if (tempSalesBasePrice != null) {
						try {
							if ((Double.parseDouble(tempSalesBasePrice) > 0) && 
									(Double.parseDouble(tempSalesBasePrice) != Double.parseDouble(nozBasePrice))) {
								salesBasePrice = tempSalesBasePrice ;
								isDiscount = true ;
							} 
						} catch (Exception e) {
							LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
						}
					}
					
					if (!isDiscount) {
						salesBasePrice = nozBasePrice ;
					}
					
					if (Double.parseDouble(GlobalUtility.getStringValue(keep_no)) != 0 ) {
						amt = GlobalUtility.getStringValue(GlobalUtility.substract(amt, keep_no)) ;
					}
					
					if (isDiscount) {
						amt = PumpMUtil.handleRcptsheetissue_code_amtsale(salesBasePrice, 
								liter, 
								rcptsheetissue_code_amtsale) ;
					}
					
					UPOSMessage_ItemInfo item_infoTicket = UPOSUtil.getUPOSMessage_ItemInfo(nozzle_no, 
							khproc_no, 
							liter, 
							salesBasePrice, 
							amt) ;
					String payment_amtTicket = item_infoTicket.getTotalOilPrice_after_discount() ;
					uPosMsgTicket = CreateUPOSMessage.createUPOSMessage_0022(IUPOSConstant.DEVICE_TYPE_3O,
															khproc_no, 
															nozzle_no, 
															emp_no, 
															item_infoTicket, 
															custCard_No, 
															ss_crStNum, 
															ss_carNum, 
															lastPayment_yn, 
															payment_amtTicket, 
															term_id, 
															led_code) ;
				}
			}
			if (uPosMsgCustomer != null) {			// 외상 인정
				uPosMsgCustomer.setLastPayment_yn("0") ;
				uPosMsgArray.add(uPosMsgCustomer) ;			
			}
			
			if (uPosMsgKeep != null) {				// 보관증 발행
				uPosMsgKeep.setLastPayment_yn("0") ;
				uPosMsgArray.add(uPosMsgKeep) ;			
			}
			
			if (uPosMsgCash != null) {				// 현금 응답
				uPosMsgCash.setLastPayment_yn("0") ;
				uPosMsgArray.add(uPosMsgCash) ;
			}
			if (uPosMsgTicket != null) {			// 상품권 응답
				uPosMsgTicket.setLastPayment_yn("0") ;
				uPosMsgArray.add(uPosMsgTicket) ;
			}
	
			LogUtility.getPumpMLogger().debug("[Pump M] 생성된 UPOSMessage 전문은 다음과 같습니다.") ;
			if ((uPosMsgArray != null) && (uPosMsgArray.size() > 0)) {
			for (int i = 0 ; i < uPosMsgArray.size() ; i++) {
				uPosMsgArray.get(i).print() ;
			}
		}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		LogUtility.getPumpMLogger().debug("[Pump M] 생성된 UPOSMessage 전문 끝") ;
		
		return uPosMsgArray ;
	}
	
	/**
	 * 충전기 카드 결제 요청 전문을 UPOSMessage Class 로 변환한다. 
	 * 충전기로부터의 승인 요청 전문은 SB_WorkingMessage Class 로 전달되며, Card_Type 및 Mode 의 내용
	 * 여부에 따라서 그에 상응하는 UPOSMessage Class 로 변환한다.
	 * 
	 * 	Card_type						Mode=0(승인)		Mode=1(취소)
	 * 		0 : 신용카드	(+보너스)			0031/0033		8031/8033
	 * 		1 : 전자상품권 (+보너스)		0041/0043		8041/8043
	 * 		2 : 점수누적					0003			8003
	 * 		3 : GS 보너스 카드 점수 사용	0061			8061
	 * 		4 : myLG 점수 사용			0051			8051
	 * 		5 : 현금 영수증				0015			8015
	 * 
	 * @param workingMsg
	 * @param khproc_no			: KH 처리번호
	 * @return
	 */
	public static UPOSMessage createUPOSMessageFromWorkingMessage_Recharge(WorkingMessage workingMsg, String khproc_no) {
		LogUtility.getPumpMLogger().info("[Pump M] Convert WorkingMessage to UPOSMessage for Recharge.") ;
		UPOSMessage uPosMsg = null ;
		
		if (workingMsg instanceof TD_WorkingMessage) {
			TD_WorkingMessage tdWorkingMsg = (TD_WorkingMessage) workingMsg ;
			String nozzleNo = tdWorkingMsg.getNozzleNo() ;
			String bonRSCard_no = tdWorkingMsg.getBonusCardNo().trim() ;
			
//			int bonusCardType = 0 ;	// 0 : GS 보너스 카드 , 1 = myLG보너스 카드 (myLG 보너스 카드 포인트 조회는 사용되지 않음.) 
			// GS 보너스 점수 조회 요청 일 경우
			// PI2 20160324 twlee 장애대응 신규전문 대응하여 보너스 점수조회시  partner_code, store_cd 추가
			// 042 : GS 보너스 카드 , 077 = 신세계 포인트(만료예정)
			String partner_code = "042";
			String store_cd = ODTUtility_Common.getStoreCode();
			
			uPosMsg = CreateUPOSMessage.createUPOSMessage_0111(IUPOSConstant.DEVICE_TYPE_3O, 
					nozzleNo,
					bonRSCard_no, 
					UPOSUtil.getPOSIP(),
					UPOSUtil.getPOSPort(),
					"",
					"",
					partner_code,
					store_cd) ;

		} else if (workingMsg instanceof SB_WorkingMessage) {
			SB_WorkingMessage sbWorkingMsg = (SB_WorkingMessage) workingMsg ;
			
			String nozzleNo = sbWorkingMsg.getNozzleNo() ;
			String cardType = sbWorkingMsg.getCardType() ;
			String mode = sbWorkingMsg.getMode() ;
			int modeInt = Integer.parseInt(mode) ;	// 0=정상승인 ,1=승인취소
			int cardTypeInt = Integer.parseInt(cardType) ;
			String card_number = sbWorkingMsg.getContent() ;
			String bonus_card = PumpMUtil.getRealBonusCardNumber(sbWorkingMsg.getBonusNumber()) ;
			String payPrice = GlobalUtility.getStringValue(sbWorkingMsg.getPrice()) ;
			String loyalty_password = null ;
			
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
			String selfPayment_type = "";
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


			String liter = PumpMUtil.convertNumberFormatFromPumpToStandardFormat(sbWorkingMsg.getAmount(),3) ;
			String basePrice = PumpMUtil.convertNumberFormatFromPumpToStandardFormat(sbWorkingMsg.getBasePrice(),2) ;

			if (card_number != null) {
				card_number.trim() ;
			}
			switch (modeInt) {
				case 0 : {
					// 정상승인
					switch (cardTypeInt) {
						case 0 : {
							// 신용카드 (+보너스) 승인			
							boolean rlt = GlobalUtility.isNullOrEmptyString(bonus_card) ;
							String maskingCardNo = PumpMessageFormat.getPrintFormatCardNumber(card_number, true).replaceAll("-", "") ;
							
							if (rlt) {
								// 신용 승인 요청
								LogUtility.getPumpMLogger().info("[Pump M] Credit Request -> UPOSMessage(0031)") ;
								UPOSMessage_ItemInfo itemInfo = UPOSUtil.getUPOSMessage_ItemInfo(nozzleNo, khproc_no,
										liter, basePrice, payPrice) ;

								uPosMsg = CreateUPOSMessage.createUPOSMessage_0031(IUPOSConstant.DEVICE_TYPE_3O, 							
										khproc_no, 
										nozzleNo, 
										PumpMODTSaleManager.getChargingPersonID(nozzleNo),
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
								// 신용  + 보너스 승인 요청
								LogUtility.getPumpMLogger().info("[Pump M] Credit+Bonus Request -> UPOSMessage(0033)") ;
								UPOSMessage_ItemInfo itemInfo = UPOSUtil.getUPOSMessage_ItemInfo(nozzleNo, khproc_no,
										liter, basePrice, payPrice) ;

								uPosMsg = CreateUPOSMessage.createUPOSMessage_0033(IUPOSConstant.DEVICE_TYPE_3O, 
										khproc_no, 
										nozzleNo, 
										PumpMODTSaleManager.getChargingPersonID(nozzleNo),
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
								
								uPosMsg = CustUtil.isTaxiDoubleSavePointYn(uPosMsg);
							}								
							break ;
						}
						case 1 : {
							// 전자상품권 (+보너스) 승인
							boolean rlt = GlobalUtility.isNullOrEmptyString(bonus_card) ;
							String maskingCardNo = PumpMessageFormat.getPrintFormatCardNumber(card_number, true).replaceAll("-", "") ;
							if (rlt) {
								LogUtility.getPumpMLogger().info("[Pump M] Electronic Ticket Request -> UPOSMessage(0041)") ;
								UPOSMessage_ItemInfo itemInfo = UPOSUtil.getUPOSMessage_ItemInfo(nozzleNo, khproc_no,
										liter, basePrice, payPrice) ;
								uPosMsg = CreateUPOSMessage.createUPOSMessage_0041(IUPOSConstant.DEVICE_TYPE_3O,
										khproc_no, 
										nozzleNo, 
										PumpMODTSaleManager.getChargingPersonID(nozzleNo),
										itemInfo, 
										maskingCardNo, 
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
										fallback_Trx_Reason
								) ;
								
							} else {
								LogUtility.getPumpMLogger().info("[Pump M] Electronic Ticket+Bonus Request -> UPOSMessage(0043)") ;
								UPOSMessage_ItemInfo itemInfo = UPOSUtil.getUPOSMessage_ItemInfo(nozzleNo, khproc_no,
										liter, basePrice, payPrice) ;
								uPosMsg = CreateUPOSMessage.createUPOSMessage_0043(IUPOSConstant.DEVICE_TYPE_3O, 
										khproc_no, 
										nozzleNo, 
										PumpMODTSaleManager.getChargingPersonID(nozzleNo),
										itemInfo, 
										maskingCardNo, 
										bonus_card, 
										null, 
										payPrice, 
										null, 
										null, 
										null, 
										null, 
										UPOSUtil.getPOSIP(),
										UPOSUtil.getPOSPort(),
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
										fallback_Trx_Reason,
										"2") ;
							}		
							break ;
						}		
						case 2 : {
							// 점수누적 승인. 사용 안함.
							break ;
						}
						case 3 : {
							// GS 보너스 카드 점수 사용 승인
							LogUtility.getPumpMLogger().info("[Pump M] GS BonusCard Use Request -> UPOSMessage(0061)") ;
							UPOSMessage_ItemInfo itemInfo = UPOSUtil.getUPOSMessage_ItemInfo(nozzleNo, khproc_no,
									liter, basePrice, payPrice) ;
							String[] dataArray = card_number.split("==") ;
							bonus_card = dataArray[0] ;
							loyalty_password = dataArray[1] ;
							
							uPosMsg = CreateUPOSMessage.createUPOSMessage_0061(IUPOSConstant.DEVICE_TYPE_3O, 
											khproc_no,
											nozzleNo, 
											PumpMODTSaleManager.getChargingPersonID(nozzleNo),
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
											//전문변경에 따른 항목 추가 (2015.11.17)
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
							break ;
						}
						// 2012.07.09 ksm  my신한 포인트 사용 중지
						// 마케팅 전략팀 김준완 사원 CSR 요청 ( 2012-07-06 )
						/*case 4 : {
							// myLG 점수 사용 승인							
							bonus_card = PumpMUtil.getRealBonusCardNumber(card_number) ;
							LogUtility.getPumpMLogger().info("[Pump M] myLG Card Use Request -> UPOSMessage(0051)") ;
							UPOSMessage_ItemInfo itemInfo = UPOSUtil.getUPOSMessage_ItemInfo(nozzleNo, khproc_no,
									liter, basePrice, payPrice) ;
							uPosMsg = CreateUPOSMessage.createUPOSMessage_0051(IUPOSConstant.DEVICE_TYPE_3O, 
											khproc_no,
											nozzleNo, 
											PumpMODTSaleManager.getChargingPersonID(nozzleNo),
											itemInfo, 
											bonus_card,	// Check 해야 함. 
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
											null) ;
							break ;
						}*/
						case 5 : {
							// 현금 영수증 승인. 사용 안함.
							break ;
						}	
					}
					break ;
				}
				case 1 : {
					// 승인취소
					switch (cardTypeInt) {
						case 0 : {
							// 신용카드 (+보너스) 승인취소			
							boolean rlt = GlobalUtility.isNullOrEmptyString(bonus_card) ;
							UPOSMessage preUPOSMsg = null ;
							if (rlt) {
								LogUtility.getPumpMLogger().info("[Pump M] Credit Cancel Request -> UPOSMessage(8031)") ;
								preUPOSMsg = PumpMODTSaleManager.getPreAcceptedUPOSMessageForCancel(nozzleNo, 
										khproc_no, IUPOSConstant.MESSAGETYPE_8031, null, null) ;
								String maskingCardNo = preUPOSMsg.getCreditCard_no().replaceAll("-", "") ;
								uPosMsg = CreateUPOSMessage.createUPOSMessage_8031(IUPOSConstant.DEVICE_TYPE_3O, 
										preUPOSMsg.getPosReceipt_no() ,
										preUPOSMsg.getNozzle_no() ,
										preUPOSMsg.getItem_info(),
										preUPOSMsg.getEmp_no(),
										maskingCardNo,
										preUPOSMsg.getTrdate_creditCard() ,
										"0",
										preUPOSMsg.getCredit_auth_no() ,
										preUPOSMsg.getPayment_amt() ,
										preUPOSMsg.getPos_ip() ,
										preUPOSMsg.getPos_port() ,
										preUPOSMsg.getPos_saleDate(),
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
										preUPOSMsg.getSelfPayment_type() ,
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
								LogUtility.getPumpMLogger().info("[Pump M] Credit+Bonus Cancel Request -> UPOSMessage(8033)") ;
								preUPOSMsg = PumpMODTSaleManager.getPreAcceptedUPOSMessageForCancel(nozzleNo, 
										khproc_no, IUPOSConstant.MESSAGETYPE_8033, null, bonus_card) ;
								String maskingCardNo = preUPOSMsg.getCreditCard_no().replaceAll("-", "") ;
								uPosMsg = CreateUPOSMessage.createUPOSMessage_8033(IUPOSConstant.DEVICE_TYPE_3O, 
										preUPOSMsg.getPosReceipt_no(),
										preUPOSMsg.getNozzle_no() ,
										preUPOSMsg.getItem_info(),
										preUPOSMsg.getEmp_no(),
										maskingCardNo ,
										preUPOSMsg.getTrdate_creditCard() ,
										preUPOSMsg.getCredit_auth_no() ,
										preUPOSMsg.getBonRSCard_no(),
										preUPOSMsg.getTrdate_bonRSCard(),
										preUPOSMsg.getBonRSCard_ID(),
										preUPOSMsg.getBonRSCard_authNo(),
										preUPOSMsg.getPayment_amt() ,
										preUPOSMsg.getPos_ip() ,
										preUPOSMsg.getPos_port() ,
										preUPOSMsg.getPos_saleDate(),
										null,
										null,
										preUPOSMsg.getCreditCardReading_type() ,
										preUPOSMsg.getChipData() ,
										preUPOSMsg.getCertification_id() ,
										preUPOSMsg.getSignImage_Info() ,
										preUPOSMsg.getSignImage_Data() ,
										preUPOSMsg.getTerm_id() ,
										preUPOSMsg.getStore_cd() ,
										card_number.getBytes(),
										preUPOSMsg.getCreditPassCode() ,
										preUPOSMsg.getSelfPayment_type() ,
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
							break ;
						}
						case 1 : {
							// 전자상품권 (+보너스) 승인취소
							boolean rlt = GlobalUtility.isNullOrEmptyString(bonus_card) ;
							UPOSMessage preUPOSMsg = null ;
							if (rlt) {
								LogUtility.getLogger().info("[Pump M] Electronic Ticket Cancel Request -> UPOSMessage(8041)") ;
								preUPOSMsg = PumpMODTSaleManager.getPreAcceptedUPOSMessageForCancel(nozzleNo, 
										khproc_no, IUPOSConstant.MESSAGETYPE_8041, null, null) ;
/*								preUPOSMsg = PumpMODTSaleManager.getPreAcceptedUPOSMessageForCancel(nozzleNo, 
										khproc_no, IUPOSConstant.MESSAGETYPE_8041, card_number, null) ;*/
								String maskingCardNo = preUPOSMsg.getCreditCard_no().replaceAll("-", "") ;
								uPosMsg = CreateUPOSMessage.createUPOSMessage_8041(IUPOSConstant.DEVICE_TYPE_3O, 
													preUPOSMsg.getPosReceipt_no(), 
													preUPOSMsg.getNozzle_no(), 
													preUPOSMsg.getItem_info(),
													preUPOSMsg.getEmp_no(),
													maskingCardNo, 
													preUPOSMsg.getTrdate_creditCard(), 
													"0",
													preUPOSMsg.getCredit_auth_no(), 
													preUPOSMsg.getPayment_amt() ,
													preUPOSMsg.getPos_ip(), 
													preUPOSMsg.getPos_port(), 
													preUPOSMsg.getPos_saleDate(),
													null,
													null,
													preUPOSMsg.getCreditCardReading_type() ,
													preUPOSMsg.getChipData() ,
													preUPOSMsg.getCertification_id() ,
													preUPOSMsg.getSignImage_Info() ,
													preUPOSMsg.getSignImage_Data() ,
													preUPOSMsg.getTerm_id() ,
													preUPOSMsg.getStore_cd() ,
													card_number.getBytes(),
													preUPOSMsg.getCreditPassCode() ,
													preUPOSMsg.getSelfPayment_type() ,
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
								LogUtility.getPumpMLogger().info("[Pump M] Electronic Ticket+Bonus Cancel Request -> UPOSMessage(8043)") ;
								preUPOSMsg = PumpMODTSaleManager.getPreAcceptedUPOSMessageForCancel(nozzleNo, 
										khproc_no, IUPOSConstant.MESSAGETYPE_8043, null, bonus_card) ;
/*								preUPOSMsg = PumpMODTSaleManager.getPreAcceptedUPOSMessageForCancel(nozzleNo, 
										khproc_no, IUPOSConstant.MESSAGETYPE_8043, card_number, bonus_card) ;*/
								String maskingCardNo = preUPOSMsg.getCreditCard_no().replaceAll("-", "") ;
								uPosMsg = CreateUPOSMessage.createUPOSMessage_8043(IUPOSConstant.DEVICE_TYPE_3O, 
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
										preUPOSMsg.getPayment_amt() ,
										preUPOSMsg.getPos_ip(), 
										preUPOSMsg.getPos_port(), 
										preUPOSMsg.getPos_saleDate(),
										null,
										null,
										preUPOSMsg.getCreditCardReading_type() ,
										preUPOSMsg.getChipData() ,
										preUPOSMsg.getCertification_id() ,
										preUPOSMsg.getSignImage_Info() ,
										preUPOSMsg.getSignImage_Data() ,
										preUPOSMsg.getTerm_id() ,
										preUPOSMsg.getStore_cd() ,
										card_number.getBytes(),
										preUPOSMsg.getCreditPassCode() ,
										preUPOSMsg.getSelfPayment_type() ,
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
							break ;
						}
		
						case 2 : {
							// 점수누적 승인취소. 사용 안함.		
							break ;
						}
						case 3 : {
							// GS 보너스 카드 점수 사용 승인취소
							LogUtility.getPumpMLogger().info("[Pump M] GS BonusCard Use Cancel Request -> UPOSMessage(8061)") ;
							bonus_card = PumpMUtil.getRealBonusCardNumber(card_number) ;						
							UPOSMessage preUPOSMsg = null ;
							preUPOSMsg = PumpMODTSaleManager.getPreAcceptedUPOSMessageForCancel(nozzleNo, 
									khproc_no, IUPOSConstant.MESSAGETYPE_8061, null, bonus_card) ;
							uPosMsg = CreateUPOSMessage.createUPOSMessage_8061(IUPOSConstant.DEVICE_TYPE_3O, 
											preUPOSMsg.getPosReceipt_no(), 
												preUPOSMsg.getNozzle_no(), 
												preUPOSMsg.getItem_info(),
												preUPOSMsg.getEmp_no(),
												bonus_card,
												preUPOSMsg.getBonRSCard_authNo(), 
												preUPOSMsg.getTrdate_bonRSCard(), 
												preUPOSMsg.getBonRSCard_ID(), 
												preUPOSMsg.getBonRSCRSt_nm(), 
												preUPOSMsg.getPayment_amt() ,
												preUPOSMsg.getPos_ip(), 
												preUPOSMsg.getPos_port(), 
												preUPOSMsg.getPos_saleDate(),
												null,
												null,
												preUPOSMsg.getCreditCardReading_type() ,
												preUPOSMsg.getChipData() ,
												preUPOSMsg.getCertification_id() ,
												preUPOSMsg.getSignImage_Info() ,
												preUPOSMsg.getSignImage_Data() ,
												preUPOSMsg.getTerm_id() ,
												preUPOSMsg.getStore_cd() ,
												"".getBytes() ,
												"".getBytes() ,
												preUPOSMsg.getSelfPayment_type() ,
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
							break ;
						}
						//	2012.07.09 ksm  my신한 포인트 사용 중지
						// 마케팅 전략팀 김준완 사원 CSR 요청 ( 2012-07-06 )
						/*
						case 4 : {
							// myLG 점수 사용 승인취소
							LogUtility.getPumpMLogger().info("[Pump M] myLG Card Use Cancel Request -> UPOSMessage(8051)") ;
							bonus_card = PumpMUtil.getRealBonusCardNumber(card_number) ;							
							UPOSMessage preUPOSMsg = null ;
							preUPOSMsg = PumpMODTSaleManager.getPreAcceptedUPOSMessageForCancel(nozzleNo, 
									khproc_no, IUPOSConstant.MESSAGETYPE_8051, bonus_card, null) ;
							uPosMsg = CreateUPOSMessage.createUPOSMessage_8051(IUPOSConstant.DEVICE_TYPE_3O, 
									preUPOSMsg.getPosReceipt_no(), 
									preUPOSMsg.getNozzle_no(), 
									preUPOSMsg.getItem_info(), 
									preUPOSMsg.getEmp_no(),
									bonus_card, 
									preUPOSMsg.getTrdate_creditCard(),
									preUPOSMsg.getCredit_month(),
									preUPOSMsg.getCredit_auth_no(),
									preUPOSMsg.getPayment_amt() ,
									preUPOSMsg.getPos_ip(),
									preUPOSMsg.getPos_port(),
									preUPOSMsg.getPos_saleDate(), 
									null, 
									null) ;
							break ;
						} */
						case 5 : {
							// 현금 영수증 승인취소.  사용 안함.
							break ;
						}	
					}			
					break ;
				}
			}
		} else if (workingMsg instanceof TJ_WorkingMessage) {
			// 현금 영수증 요청
			// 현금 영수증 요청은 충전기 ODT 의 판매 완료 이후 발생한다. 하지만 충전기 ODT 로 부터 올라오는 요청 금액을 이용하여 구성한다.
			LogUtility.getPumpMLogger().info("[Pump M] Cash Receipt Request -> UPOSMessage(0015)") ;
			
			TJ_WorkingMessage tjWorkingMsg = (TJ_WorkingMessage) workingMsg ;	
			
			String dealType 	= tjWorkingMsg.getDealType();			// 거래구분(0: 소비자소득공제, 1: 사업자지출증빙)
			String dealAmount 	= tjWorkingMsg.getDealAmount();			// 거래금액
//			String keyINType 	= tjWorkingMsg.getKeyINType();			// 키인방법
			String certiNumber 	= tjWorkingMsg.getCertiNumber();		// 인증번호

			String deviceType = IUPOSConstant.DEVICE_TYPE_3O ;
			String posReceipt_no = khproc_no ;
			String nozzle_no = tjWorkingMsg.getNozzleNo() ;
			UPOSMessage_ItemInfo item_info = null ;
			String creditCard_no = PumpMUtil.getRealBonusCardNumber(certiNumber.trim());
			UPOSMessage_CampInfo camp_info = null ;
			String payment_amt = GlobalUtility.getStringValue(dealAmount) ;
			String loyality_type = "" ; 
			String pos_ip = UPOSUtil.getPOSIP() ;
			String pos_port = UPOSUtil.getPOSPort() ;
			String pos_saleDate = UPOSUtil.getPosSaleDate() ; 
			String credit_month = "0" ;
			
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
			String selfPayment_type = "";
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
			
			// 현금 영수증 요청은 카드 결재 및 포인트 사용을 제외한 금액을 현금 요청한다.
			// 이 전문이 올라왔다는 것은 주유금액에서 카드 결재 및 포인트 사용을 제외한 금액이 있다는 의미이다.
			try {
				T_KH_PUMP_TRData trData = T_KH_PUMP_TRHandler.getHandler().getT_KH_PUMP_TRData(posReceipt_no) ;

				String trBasePrice = trData.getPreset_baseprice() ;
				if ((trBasePrice == null) || (trBasePrice.equals("")) || (trBasePrice.equals("0"))) {
					trBasePrice = trData.getBaseprice() ;
				}
				
				String liter = PumpMUtil.calculateLiterFromPriceAndBasePrice(payment_amt, trBasePrice) ;
				item_info = UPOSUtil.getUPOSMessage_ItemInfo(nozzle_no, 
						posReceipt_no,
						liter, 
						trBasePrice, 
						payment_amt) ;
			} catch (Exception e) {
				LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
			}
			
			/*AS-IS
			 * //  현금 영수증 구분을 어떻게 할 것인가 ? 길이로 구분
			//	보너스카드번호 (16) / 주민번호 (13)/ 사업자번호 (10) / 핸드폰 번호 (10-12)
			int certiNumberLength = creditCard_no.length() ;
			String certiDest = null ;			// 국세청 송신주체	
			String certiSrcType = dealType ;	// 거래자 구분
			String certiNumberType = null ;		// 확인자 구분
			// 12월 22일 추영대 수정. 현금영수증카드는 카드번호 18자리.
			if (certiNumberLength == 16 || certiNumberLength == 18) {
				// 보너스 카드번호
				certiNumberType = "9" ;
				certiDest = "01" ;
			} else if (certiNumberLength == 13) {
				// 주민번호
				certiNumberType = "1" ;
				certiDest = "02" ;
			} else if (certiNumberLength == 10) {
				// 사업자 번호
				certiNumberType = "2" ;
				certiDest = "02" ;
			} else {
				// 핸드폰 번호
				certiNumberType = "3" ;
				certiDest = "02" ;
			}
			//	 국세청 송신주체(2byte)+{거래자구분(1byte)+확인자 구분(1byte)}  
			loyality_type = certiDest + certiSrcType + certiNumberType ;*/
			//20160324 twlee 장애대응 관련하여  현금영수증 요청 코드 변경
			//	보너스카드번호 (16) / 주민번호 (13)/ 사업자번호 (10) / 핸드폰 번호 (10-12)
			//  국세청송신주체(01:GSC, 02:스마트로) + 거래자구분(0:소비자, 1:사업자) + 확인자구분(0:신용카드번호 1:주민등록번호 2:사업자등록번호 3:기타 9:보너스카드번호)
			int certiNumberLength = creditCard_no.length() ;
			String certiDest = null ;			// 국세청 송신주체	
			String certiSrcType = dealType ;	// 거래자 구분
			String certiNumberType = null ;		// 확인자 구분
			if (certiNumberLength == 16 || certiNumberLength == 18) {
				// 보너스 카드번호
				certiNumberType = "9" ;
				certiDest = "01" ;
				creditCardReading_type = IConstant.CreditCardReading_Type_MAGNETIC;
			} else if (certiNumberLength == 13) {
				// 20160323 twlee PI2  주민번호로 현금영수증 처리를 막음
				certiNumberType = "1" ;
				certiDest = "01" ;
				creditCardReading_type = IConstant.CreditCardReading_Type_Keyin;
			} else if (certiNumberLength == 10) {
				// 사업자 번호
				certiNumberType = "2" ;
				certiDest = "01" ;
				creditCardReading_type = IConstant.CreditCardReading_Type_Keyin;
			} else {
				// 핸드폰 번호
				certiNumberType = "3" ;
				certiDest = "01" ;
				creditCardReading_type = IConstant.CreditCardReading_Type_Keyin;
			}
			//	 국세청 송신주체(2byte)+{거래자구분(1byte)+확인자 구분(1byte)}  
			loyality_type = certiDest + certiSrcType + certiNumberType ;
			String maskingCashReceiptNo = PumpMessageFormat.getPrintFormatCardNumberForPI2(creditCard_no, false);
			String cashReceiptNo = ODTUtility_Common.getChangeCashReceiptNumber(creditCard_no);
			
	
			uPosMsg = CreateUPOSMessage.createUPOSMessage_0015(deviceType ,
															posReceipt_no ,
															nozzle_no ,
															PumpMODTSaleManager.getChargingPersonID(nozzle_no),
															item_info ,
															maskingCashReceiptNo ,
															camp_info ,
															payment_amt ,
															loyality_type ,
															pos_ip ,
															pos_port ,
															pos_saleDate ,
															credit_month,
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
		} else if (workingMsg instanceof BA_WorkingMessage) {
			// 보너스 누적 요청 
			// 보너스 누적 요청은 충전기 ODT 로 부터 판매 완료 이후 올라온다. 따라서 이는 Pump_TR 테이블의 내용을 가지고 요청하도록 한다.
			LogUtility.getPumpMLogger().info("[Pump M] Bonus Accumulation Request -> UPOSMessage(0004)") ;			
			BA_WorkingMessage baWorkingMsg = (BA_WorkingMessage) workingMsg ;	
			
			baWorkingMsg.print() ;
			
			String deviceType = IUPOSConstant.DEVICE_TYPE_3O ;
			String nozzle_no = baWorkingMsg.getNozzleNo() ;
			UPOSMessage_ItemInfo item_info = null ;
			String bonRSCard_no = PumpMUtil.getRealBonusCardNumber(baWorkingMsg.getContent()) ;
			UPOSMessage_CampInfo camp_info = null ;
			String payment_amt = null ;
			String loyalty_password = "" ;
			String loyality_type = "" ;
			String pos_ip = UPOSUtil.getPOSIP() ;
			String pos_port = UPOSUtil.getPOSPort() ;
			String bonRSCard_ID = "" ;
			String local_point = "" ;
			String local_occurPoint = "" ;
			String pos_saleDate = UPOSUtil.getPosSaleDate() ; 

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
			String selfPayment_type = "";
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
			
			try {
				T_KH_PUMP_TRData trData = T_KH_PUMP_TRHandler.getHandler().getT_KH_PUMP_TRData(khproc_no) ;
				String trBasePrice = trData.getPreset_baseprice() ;
				if ((trBasePrice == null) || (trBasePrice.equals("")) || (trBasePrice.equals("0"))) {
					trBasePrice = trData.getBaseprice() ;
				}
				item_info = UPOSUtil.getUPOSMessage_ItemInfo(nozzle_no, khproc_no,
					trData.getEqpm_qty(), trBasePrice, trData.getEqpm_amt_prc()) ;
				payment_amt = GlobalUtility.getStringValue(trData.getEqpm_amt_prc()) ;
			} catch (Exception e) {
				LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
			}

			uPosMsg = CreateUPOSMessage.createUPOSMessage_0003(deviceType ,
															khproc_no ,
															nozzle_no ,
															PumpMODTSaleManager.getChargingPersonID(nozzle_no),
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
															"3",
															null,
															null,
															null,
															null,
															null,
															null,
															
//															전문변경에 따른 항목 추가 (2015.11.17)
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
					
		} else {
			
		}
		if (uPosMsg!= null)
			uPosMsg.print();
		return uPosMsg ;
	}

	/**
	 * 승인 요청에 대한 응답 전문 (UPOSMessage Object) 를 받아서 이를 충전기 ODT Spec 에 맞는 BB_WorkingMessage Object
	 * 로 변환한다.
	 * 아래 내용은 UPOSMessage Object 의 Message Type 에 따른 전문 내용이다.
	 * 
	 * 	신용카드 승인 응답	= 0032
	 * 	신용카드 + 보너스 승인 응답 = 0034
	 * 	전자상품권 승인 응답 = 0042
	 * 	전자상품권 + 보너스 승인 응답 = 0044
	 * 	GS 보너스 카드 점수 사용 응답 = 0062
	 * 	myLG 점수 사용 응답 = 0052
	 * 	현금 영수증 응답 = 0016
	 * 
	 * [2008.11.27] 변경 by 정순덕 차장님.
	 * 스마트로 정순덕 차장님께서 다음과 같이 변경을 요청하였습니다.
	 * 	1. 기존
	 * 		GS 포인트 이용 -> GS 보너스 서버
	 * 		기타 포인트 이용 (myLG 포함) -> VAN 사로 일반 신용카드 결재처럼 전문을 구성하되, 할부개월을 '61' 로 설정
	 * 	2. 변경 사항
	 * 		GS 포인트 이용 -> GS 보너스 서버
	 * 		myLG 포인트 이용 -> VAN 사로 포인트 사용 L3 전문을 전송하도록 한다.
	 * 		기타 포인트 이용 (GS , myLG 제외) -> VAN 사로 일반 신용카드 결재처럼 전문을 구성하되, 할부개월을 '61' 로 설정
	 * 
	 * @param uPosMsg
	 * @return
	 */
	public static ArrayList<WorkingMessage> createWorkingMessageFromUPOSMessage_Recharge(UPOSMessage uPosMsg) {
		LogUtility.getPumpMLogger().info("[Pump M] Convert UPOSMessage[" + uPosMsg.getMessageType() 
				+ "] to WorkingMessage for Recharge.") ;
		
		ArrayList<WorkingMessage> workMsgArray = new ArrayList<WorkingMessage>() ;
		int messageType = Integer.parseInt(uPosMsg.getMessageType()) ;
		String cardType 		= "0";				// 카드종류 (0=신용카드, 1=전자상품권, 2=점수누적,
													// 3=GS보너스카드 점수사용,	4=myLG 점수사용, 5=현금영수증
		
		// 충전기는 복합 결재가 가능하며, 또한 결재 이후 SH 전문이 오기 때문에, 모든 응답 전문의 lastPayment_yn 을 0 으로 설정한다.
		uPosMsg.setLastPayment_yn("0") ;
		
		switch (messageType) {
			case IUPOSConstant.MESSAGETYPE_INT_0004 : 
			case IUPOSConstant.MESSAGETYPE_INT_0014 : {
				// 보너스 누적 응답
				BB_WorkingMessage bbWorkingMsg = null ;
				if (uPosMsg.getLoyaltyReqCode().equals("00000")) {
					// 성공일 경우
					bbWorkingMsg = getBB_WorkingMessage(uPosMsg , "0");
				} else {
					// 실패일 경우
					bbWorkingMsg = getBB_WorkingMessage(uPosMsg , "1");
				}
				workMsgArray.add(bbWorkingMsg) ;
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_8046 :
			case IUPOSConstant.MESSAGETYPE_INT_8032 : {
				// 신용 카드 취소 요청 응답 
				LogUtility.getPumpMLogger().info("[Pump M] Credit Cancel Response(8032) -> WorkingMessage") ;
				cardType = "0" ;
				PI_WorkingMessage piWorkingMsg = getPI_WorkingMessage(uPosMsg , cardType, false, false) ;				
				workMsgArray.add(piWorkingMsg) ;				
				break ;				
			}
			case IUPOSConstant.MESSAGETYPE_INT_0046 :
			case IUPOSConstant.MESSAGETYPE_INT_0032 : { 
				// 신용카드 승인 응답
				LogUtility.getPumpMLogger().info("[Pump M] Credit Response(0032) -> WorkingMessage") ;	
				cardType = "0" ;
				PI_WorkingMessage piWorkingMsg = getPI_WorkingMessage(uPosMsg , cardType, true, false) ;				
				workMsgArray.add(piWorkingMsg) ;
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_8048 :
			case IUPOSConstant.MESSAGETYPE_INT_8034 : {
				// 신용카드 + 보너스 취소 요청 응답 
				LogUtility.getPumpMLogger().info("[Pump M] Credit+Bonus Cancel Response(8034) -> WorkingMessage") ;
				cardType = "0" ;
				PI_WorkingMessage piWorkingMsg = getPI_WorkingMessage(uPosMsg , cardType, true, false) ;
				workMsgArray.add(piWorkingMsg) ;
				BB_WorkingMessage bbWorkingMsg = null ;
				if (uPosMsg.getLoyaltyReqCode().equals("00000")) {
					bbWorkingMsg = getBB_WorkingMessage(uPosMsg , "0");
				} else {
					bbWorkingMsg = getBB_WorkingMessage(uPosMsg , "1");
				}
				workMsgArray.add(bbWorkingMsg) ;
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_0048 :
			case IUPOSConstant.MESSAGETYPE_INT_0034 : { 
				// 신용카드 + 보너스 승인 응답
				LogUtility.getPumpMLogger().info("[Pump M] Credit+Bonus Response(0034) -> WorkingMessage") ;	
				cardType = "0" ;
				PI_WorkingMessage piWorkingMsg = getPI_WorkingMessage(uPosMsg , cardType, true, false) ;
				workMsgArray.add(piWorkingMsg) ;
				BB_WorkingMessage bbWorkingMsg = null ;
				if (uPosMsg.getLoyaltyReqCode().equals("00000")) {
					bbWorkingMsg = getBB_WorkingMessage(uPosMsg , "0");
				} else {
					bbWorkingMsg = getBB_WorkingMessage(uPosMsg , "1");
				}				
				workMsgArray.add(bbWorkingMsg) ;
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_8042 : {
				// 전자상품권 취소 요청 응답 
				LogUtility.getPumpMLogger().info("[Pump M] Electronic Ticket Cancel Response(8042) -> WorkingMessage") ;
				cardType = "1" ;
				PI_WorkingMessage piWorkingMsg = getPI_WorkingMessage(uPosMsg , cardType, true, false) ;
				workMsgArray.add(piWorkingMsg) ;	
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_0042 : { 
				// 전자상품권 승인 응답
				LogUtility.getPumpMLogger().info("[Pump M] Electronic Ticket Response(0042) -> WorkingMessage") ;
				cardType = "1" ;
				PI_WorkingMessage piWorkingMsg = getPI_WorkingMessage(uPosMsg , cardType, true, false) ;
				workMsgArray.add(piWorkingMsg) ;	
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_8044 : {
				// 전자상품권 + 보너스 취소 요청 응답 
				LogUtility.getPumpMLogger().info("[Pump M] Electronic Ticket+Bonus Cancel Response(8044) -> WorkingMessage") ;
				cardType = "1" ;
				PI_WorkingMessage piWorkingMsg = getPI_WorkingMessage(uPosMsg , cardType, true, false) ;
				workMsgArray.add(piWorkingMsg) ;				
				BB_WorkingMessage bbWorkingMsg = null ;
				if (uPosMsg.getLoyaltyReqCode().equals("00000")) {
					bbWorkingMsg = getBB_WorkingMessage(uPosMsg , "0");
				} else {
					bbWorkingMsg = getBB_WorkingMessage(uPosMsg , "1");
				}				
				workMsgArray.add(bbWorkingMsg) ;
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_0044 : { 
				// 전자상품권 + 보너스 승인 응답
				LogUtility.getPumpMLogger().info("[Pump M] Electronic Ticket+Bonus Response(0044) -> WorkingMessage") ;
				cardType = "1" ;
				PI_WorkingMessage piWorkingMsg = getPI_WorkingMessage(uPosMsg , cardType, true, false) ;
				workMsgArray.add(piWorkingMsg) ;	
				BB_WorkingMessage bbWorkingMsg = null ;
				if (uPosMsg.getLoyaltyReqCode().equals("00000")) {
					bbWorkingMsg = getBB_WorkingMessage(uPosMsg , "0");
				} else {
					bbWorkingMsg = getBB_WorkingMessage(uPosMsg , "1");
				}				
				workMsgArray.add(bbWorkingMsg) ;
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_8062 : {
				// GS 보너스 카드 점수 취소 요청 응답 
				LogUtility.getPumpMLogger().info("[Pump M] GS BonusCard Use Cancel Response(8062) -> WorkingMessage") ;
				cardType = "3" ;
				PI_WorkingMessage piWorkingMsg = getPI_WorkingMessage(uPosMsg , cardType, false, true) ;
				workMsgArray.add(piWorkingMsg) ;
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_0062 : { 
				// GS 보너스 카드 점수 사용 응답
				LogUtility.getPumpMLogger().info("[Pump M] GS BonusCard Use Response(0062) -> WorkingMessage") ;
				cardType = "3" ;
				PI_WorkingMessage piWorkingMsg = getPI_WorkingMessage(uPosMsg , cardType, true, true) ;
				piWorkingMsg.setPrintContent(getGSPointUseMessage(uPosMsg, "0"));
				workMsgArray.add(piWorkingMsg) ;				
				break ;
			}
			//	2012.07.09 ksm  my신한 포인트 사용 중지
			// 마케팅 전략팀 김준완 사원 CSR 요청 ( 2012-07-06 )
			/*
			case IUPOSConstant.MESSAGETYPE_INT_8052 : {
				// myLG 점수 취소 요청 응답 
				LogUtility.getPumpMLogger().info("[Pump M]  myLG Card Use Cancel Response(8052) -> WorkingMessage") ;
				cardType = "4" ;
				PI_WorkingMessage piWorkingMsg = getPI_WorkingMessage(uPosMsg , cardType, false, false) ;
				workMsgArray.add(piWorkingMsg) ;	
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_0052 : { 
				// myLG 점수 사용 응답
				LogUtility.getPumpMLogger().info("[Pump M] myLG Card Use Response(0052) -> WorkingMessage") ;
				cardType = "4" ;
				PI_WorkingMessage piWorkingMsg = getPI_WorkingMessage(uPosMsg , cardType, true, false) ;
				workMsgArray.add(piWorkingMsg) ;	
				break ;
			}*/
			case IUPOSConstant.MESSAGETYPE_INT_0016 : { 
				// 현금 영수증 응답
				LogUtility.getPumpMLogger().info("[Pump M] Cash Receipt Response(0016) -> WorkingMessage") ;
				XA_WorkingMessage xaWorkingMsg = getXA_WorkingMessage(uPosMsg) ;
				workMsgArray.add(xaWorkingMsg) ;
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_0112 : {
				// 보너스 카드의 포인트 점수 조회 응답
				LogUtility.getPumpMLogger().info("[Pump M] Bonus Card Point inquiry Response(0112) -> WorkingMessage") ;
				CP_WorkingMessage cpWorkingMsg = getCP_WorkingMessage(uPosMsg) ;
				workMsgArray.add(cpWorkingMsg) ;
				break ;
			}
		}				
		return workMsgArray ;
	}

	/**
	 * 충전기 BB 전문 구성을 요청한다.
	 * 
	 * @param nozzle_no	: 노즐 번호
	 * @param successBP	: 성공 여부
	 * @param odtContent	: 메시지
	 * @param bonusMsg	: 출력 메시지
	 * @return
	 */
	private static BB_WorkingMessage getBB_WorkingMessage(String nozzle_no, String successBP, String odtContent, 
			String bonusCardNumber, String bonusAcceptNumber, String createPoint, String usePoint, String totalPoint,
			String storePoint, String bonusMessage) {
		BB_WorkingMessage bbWorkingMsg = null ;
		String printMsg = PumpMessageFormat.createBonusPrintODTFormat(bonusCardNumber, bonusAcceptNumber, 
				createPoint, usePoint, totalPoint, storePoint, bonusMessage) ;
		bbWorkingMsg = new BB_WorkingMessage(nozzle_no , successBP, odtContent , printMsg) ;
		return bbWorkingMsg ;
	}
	
	
	/**
	 * 
	 * @param uPosMsg		: 승인 응답 UPOS Message
	 * @param successBP		: 보너스 카드 성공 여부
	 * @return
	 */
	private static BB_WorkingMessage getBB_WorkingMessage(UPOSMessage uPosMsg, String successBP) {
		String nozzle_no = uPosMsg.getNozzle_no() ;
		//String odtContent  = "" ;
		// PI2 20160325 twlee 장애대응에서 odtScreenMsg를 내려주지 않아 display_msg로 변경
		String odtContent  = uPosMsg.getDisplay_msg() ;
		String bonusCardNumber  = uPosMsg.getBonRSCard_no() ;
		String bonusAcceptNumber = uPosMsg.getBonRSCard_authNo() ;
		String createPoint = uPosMsg.getGs_point1() ;
		String usePoint = uPosMsg.getGs_point2() ;
		String totalPoint = uPosMsg.getGs_point3() ;
		String storePoint = uPosMsg.getLocal_point() ;
		String bonusMessage = uPosMsg.getBonRS_msg() ;
		return getBB_WorkingMessage(nozzle_no, 
//				successBP,
//				2008.12.18 영수증 출력 단계로 바로 넘어가기 위한 정상 처리 - 추영대 -
				"0",
				odtContent, 
				bonusCardNumber, 
				bonusAcceptNumber, 
				createPoint, 
				usePoint, 
				totalPoint, 
				storePoint, 
				bonusMessage) ;
	}
	
	
	/**
	 * UPOSMessage 의 보너스 카드 포인트 점수 조회 응답 전문을 CP 전문으로 변경합니다.
	 * 
	 * @param posMsg	: UPOSMessage
	 * @return
	 */
	private static CP_WorkingMessage getCP_WorkingMessage(UPOSMessage posMsg) {
		CP_WorkingMessage cpWorkMsg = null ;
		
		String loyaltyReqCode = posMsg.getLoyaltyReqCode() ;
		if (loyaltyReqCode.equals("00000")) {
			cpWorkMsg = new CP_WorkingMessage(posMsg.getNozzle_no(), 
				posMsg.getBonRSCard_no(),
				posMsg.getGs_point2()) ;
		} else {
			cpWorkMsg = new CP_WorkingMessage(posMsg.getNozzle_no(), 
					posMsg.getBonRSCard_no(),
					"0") ;			
		}
		
		return cpWorkMsg;
	}
	
	/**
	 * UPOSMessage 의 부가정보에서 가맹점번호를 가져온다.
	 * 
	 * @param credit_AuthInfo	: UPOSMessag 의 부가정보
	 * @return
	 */
	private static String getFrancNumberFromCredit_AuthInfo(String credit_AuthInfo) {
		String francNumber = "" ;
		try {
			String[] authInfoArray = GlobalUtility.splitByteArrayToStringArray(credit_AuthInfo.getBytes(), IUPOSConstant.DELIMITER_0X1E) ;
			if (authInfoArray.length >= 3) {
				francNumber = authInfoArray[3] ;
			} else {
				francNumber = "" ;
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
			francNumber = "" ;
		}
		return francNumber.trim() ;
	}
	
	/**
	 * UPOSMessage 의 부가정보에서 전표 번호를 가져온다.
	 * 
	 * @param credit_AuthInfo	: UPOSMessag 의 부가정보
	 * @return
	 */
	/*private static String getNoteNumberFromCredit_AuthInof(String credit_AuthInfo) {
		String noteNumber = "" ;
		try {
			String[] authInfoArray = GlobalUtility.splitByteArrayToStringArray(credit_AuthInfo.getBytes(), IUPOSConstant.DELIMITER_0X1E) ;
			noteNumber = authInfoArray[1] ;
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
			noteNumber = "" ;
		}
		return noteNumber ;
	}*/
	
	/**
	 * GS보너스포인트 사용 성공시 응답 메세지 중 인쇄내역 내용을 만들기 위함.
	 * @param posMsg 	: 승인 응답 UPOS Message
	 * @param i			: 성공여부
	 * @return
	 */
	private static String getGSPointUseMessage(UPOSMessage uPosMsg, String successBP) {
//		String nozzle_no = uPosMsg.getNozzle_no() ;
//		String odtContent  = uPosMsg.getOdtScreenMsg() ;
		String bonusCardNumber  = uPosMsg.getBonRSCard_no() ;
		String bonusAcceptNumber = uPosMsg.getBonRSCard_authNo() ;
		String createPoint = uPosMsg.getGs_point1() ;
		String usePoint = uPosMsg.getGs_point2() ;
		String totalPoint = uPosMsg.getGs_point3() ;
		String gs_point4 = uPosMsg.getGs_point4() ;
		String storePoint = uPosMsg.getLocal_point() ;
		String bonusMessage = uPosMsg.getBonRS_msg() ;
		
		String printMsg = PumpMessageFormat.createBonusUsePrintODTFormat(bonusCardNumber, bonusAcceptNumber, 
				createPoint, usePoint, totalPoint, gs_point4, storePoint, bonusMessage);
		
		
		return printMsg;
	}
	
	/**
	 * 보관증 발행 정보가 있는지 조사하고 있으면, 보관증 발행 정보를 리턴한다.
	 * 
	 * @param repInfo	: SH 전문의 발행 정보 리스트
	 * @return
	 */
	public static SH_RepInfo getKeepInfo(Vector<SH_RepInfo> repInfo) {
		SH_RepInfo keepInfo = null ;
		try {
			if (repInfo == null) return null ;
			
			for (int i = 0 ; i < repInfo.size() ; i++) {
				String flag = repInfo.get(i).getFlag() ;
				if ("B".equals(flag)) {
					String keepAmt = repInfo.get(i).getAmt() ;
					if (Double.parseDouble(keepAmt) != 0) {
						return repInfo.get(i) ;
					}
				}
			}
			
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
		}
		return keepInfo ;
	}
		
	/**
	 * UPOSMessage Object 의 led_code 를 이용하여, 충전기 ODT 로 전송할 WorkingMessage 의 mode 를 요청한다.
	 * 
	 * @param led_code	: UPOSMessage 의 led_code
	 * @param isAcceptedRequest
	 * 		true : 승인 요청에 대한 응답
	 * 		false : 승인 취소 요청에 대한 응답
	 * @return
	 */
	private static String getModeFromLedCode_Recharge(String led_code, boolean isAcceptedRequest) {
		String mode = null ;
		if (isAcceptedRequest) {
			if (led_code.equals("1")) {
				mode = "0" ;
			} else if (led_code.equals("2")) {
				mode = "1" ;
			} else {
				mode = "2" ;
			}
		} else {
			if (led_code.equals("1")) {
				mode = "3" ;
			} else if (led_code.equals("2")) {
				mode = "4" ;
			} else {
				mode = "2" ;
			}
		}
		return mode ;
	}

	/**
	 * 응답 전문인 UPOSMessage Object 를 PI_WorkingMessage Object 로 변환한다.
	 * 
	 * @param uPosMsg	: UPOS Message
	 * @param cardType	: Card Type
	 * @param isAcceptedRequest	: true (승인 요청에 대한 응답) , false(승인 취소 요청에 대한 응답)
	 * @param isGSBonusCard		: true (GS Bonus Card 요청에 대한 응답), false (GS Bonus Card 와 관계 없음)
	 * @return
	 */
	private static PI_WorkingMessage getPI_WorkingMessage(UPOSMessage uPosMsg, String cardType,
			boolean isAcceptedRequest, boolean isGSBonusCard) {
		String mode 			= "";	// MODE (0정상승인,1거부,2통신실패,3최소성공,4취소실패)
		String recogDate 		= "";	// 승인일시
		String recogNumber 		= "";	// 승인번호
		String cardNumber 		= ""; 	// 카드번호
		String cardCorpName 	= "";	// 카드사명
		String francNumber 		= ""; 	// 가맹점번호
		String noteCorpCode 	= "";	// 전표매입사코드
		String noteCorpName 	= "";	// 전표매입사명
		String terminalNubmer 	= "";	// 단말기번호
		String noteNumberTemp 	= "";	// 전표번호(사용안함)
		String notice 			= "";	// NOTICE
		String noteNumber 		= "";	// 전표번호
		String recogConfid 		= "";	// 현마감신용승인번호
		String printContent 	= "";	// 인쇄내역 

		// GS Bonus Card 승인 응답 및 취소요청 응답인 경우 보너스 관련 필드를 참조한다.
		// 하지만 myLG 승인 응답 및 취소요청 응답은 신용카드 와 동일하게 처리한다.
		if (!isGSBonusCard) {
			mode = getModeFromLedCode_Recharge(uPosMsg.getLed_code() , isAcceptedRequest) ;// MODE (0정상승인,1거부,2통신실패,3취소성공,4취소실패)
			recogDate 		= uPosMsg.getTrdate_creditCard() ;	// 승인일시
			recogNumber 	= uPosMsg.getCredit_auth_no();		// 승인번호
			cardNumber 		= PumpMUtil.getCardNumberPre16Length(uPosMsg.getCreditCard_no()); 		// 카드번호
			cardCorpName 	= uPosMsg.getIssuer_name();			// 카드사명
			francNumber 	= getFrancNumberFromCredit_AuthInfo(uPosMsg.getCredit_authInfo()); 	// 가맹점번호
			noteCorpCode 	= uPosMsg.getAcquier_code();	// 전표매입사코드
			noteCorpName 	= uPosMsg.getAcquier_name();	// 전표매입사명
			terminalNubmer 	= uPosMsg.getTerm_id();			// 단말기번호
			noteNumberTemp 	= "";	
			// 전표번호(사용안함)			
			// PI2 20160325 twlee 장애대응에서 odtScreenMsg를 내려주지 않아 display_msg로 변경
			notice 			= uPosMsg.getDisplay_msg() ;		
			noteNumber 		= "" ;
			recogConfid 	= "1";							// 현마감신용승인번호
			printContent 	= uPosMsg.getVan_msg();			// 인쇄내역 
		} else {
			mode = getModeFromLedCode_Recharge(uPosMsg.getLed_code() , isAcceptedRequest) ;// MODE (0정상승인,1거부,2통신실패,3취소성공,4취소실패)
			recogDate 		= uPosMsg.getTrdate_bonRSCard();	// 승인일시
			recogNumber 	= uPosMsg.getBonRSCard_authNo();	// 승인번호
			cardNumber 		= PumpMUtil.getCardNumberPre16Length(uPosMsg.getBonRSCard_no()); 		// 카드번호
			cardCorpName 	= uPosMsg.getIssuer_name();			// 카드사명
			francNumber 	= getFrancNumberFromCredit_AuthInfo(uPosMsg.getCredit_authInfo()); 	// 가맹점번호
			noteCorpCode 	= uPosMsg.getAcquier_code();	// 전표매입사코드
			noteCorpName 	= uPosMsg.getAcquier_name();	// 전표매입사명
			terminalNubmer 	= uPosMsg.getTerm_id();							// 단말기번호
			noteNumberTemp 	= "";							// 전표번호(사용안함)
			// PI2 20160325 twlee 장애대응에서 odtScreenMsg를 내려주지 않아 display_msg로 변경
			notice 			= uPosMsg.getDisplay_msg() ;
			noteNumber 		= "" ;
			recogConfid 	= "1";							// 현마감신용승인번호
			printContent 	= uPosMsg.getBonRS_msg();			// 인쇄내역 
		}		
		PI_WorkingMessage piWorkingMsg = new PI_WorkingMessage(uPosMsg.getNozzle_no(),
				mode,
				recogDate,
				recogNumber,
				cardNumber,
				cardCorpName,
				francNumber,
				noteCorpCode,
				noteCorpName,
				terminalNubmer,
				noteNumberTemp,
				notice,
				cardType,
				noteNumber,
				recogConfid,
				printContent) ;		
		
		return piWorkingMsg ;
	}
	

	
	/**
	 * 현금 영수증 요청에 대한 응답 전문을 XA_WorkingMessage 로 변환합니다
	 * 
	 * @param posMsg	: 승인 응답 전문
	 * @return
	 */
	private static XA_WorkingMessage getXA_WorkingMessage(UPOSMessage posMsg) {
		String nozzle_no = posMsg.getNozzle_no() ;
		String successBP = "" ;		
		String odtContent = "" ;
		String printContent = "" ;
		
		String ledCode = posMsg.getLed_code() ;
		if (ledCode.equals(IUPOSConstant.LEDCODE_1)) {
			// 성공인 경우
			successBP = "0" ;
			printContent = posMsg.getVan_msg() ;
		} else {
			// 실패인 경우
			successBP = "1" ;
			printContent = posMsg.getVan_msg() ;
		}

		// PI2 20160325 twlee 장애대응에서 odtScreenMsg를 내려주지 않아 display_msg로 변경
		odtContent = posMsg.getDisplay_msg() ;
				
		String productName = "" ;
		String liter = "" ;
		String basePrice = "" ;
		String priceBeforeTax = "" ;
		String tax = "" ;
		String priceAfterTax = "" ;
		
		try {
			UPOSMessage_ItemInfo itemInfo = posMsg.getItem_info() ;
			UPOSMessage_ItemInfo_Item itemInfoItem = itemInfo.getItemInfoList().get(0) ;
			
			productName = T_KH_PRODUCTHandler.getHandler().getT_KH_PRODUCTData(itemInfoItem.getGoodsCode()).getGoods_name() ;
			liter = GlobalUtility.divide(itemInfoItem.getOilAmount() , "1000") ;
			basePrice = GlobalUtility.divide(itemInfoItem.getUnitPrice_after_discount() , "1000") ; ;
			priceBeforeTax = GlobalUtility.getStringValue(itemInfoItem.getPrice_before_tax()) ;
			tax = GlobalUtility.getStringValue(itemInfoItem.getTaxPrice()) ;
			priceAfterTax = GlobalUtility.getStringValue(itemInfoItem.getOilPrice_after_discount()) ;
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		String cashReceiptFormat = PumpMessageFormat.createCashReceiptODTFormat(posMsg.getTrdate_creditCard(), 
				posMsg.getCreditCard_no(), 
				posMsg.getCredit_auth_no(), 
				"현금(소득공제)",
				productName, 
				liter, 
				basePrice, 
				priceBeforeTax, 
				tax, 
				priceAfterTax, 
				printContent) ;
		
		
		XA_WorkingMessage xaWorkMsg = new XA_WorkingMessage(nozzle_no,
				successBP,
				odtContent,
				cashReceiptFormat) ;
		
		return xaWorkMsg;
	}
	
	public static Preamble invalidCardNo(SB_WorkingMessage sbWorkMsg){

		PI_WorkingMessage piWorkingMsg = new PI_WorkingMessage(sbWorkMsg.getNozzleNo(),
				"1",
				GlobalUtility.getDateYYYYMMDDHHMMSS(),
				GlobalUtility.appendingSPACEEnd(null, 12),
				GlobalUtility.appendingSPACEEnd(sbWorkMsg.getContent(),16),
				GlobalUtility.appendingSPACEEnd(null,20),
				GlobalUtility.appendingSPACEEnd(null,16),
				GlobalUtility.appendingSPACEEnd(null,3),
				GlobalUtility.appendingSPACEEnd(null,20),
				GlobalUtility.appendingSPACEEnd(null,10),
				GlobalUtility.appendingSPACEEnd(null,5),
				"카드번호 오류",
				"0",
				GlobalUtility.appendingSPACEEnd(null,10),
				GlobalUtility.appendingSPACEEnd(null,5),
				"카드번호 오류") ;		
		
		
		Preamble pumpPreamble = PumpMUtil.createWorkingMessagePreamble(null,
				SyncManager.DISE_PUMP_ADAPTER, 
				piWorkingMsg , 
				"") ;

		LogUtility.getPumpMLogger().debug("카드번호가 짧게 들어와 승인실패 메세지를 ODT에 전달함.");
		
		return pumpPreamble ;
    }
	
	/**
     * 2009.5.27 추영대 작성
     * 
     * 자영에 맞는 보관증 번호 체계로 보관증 번호를 바꿔준다.
     * 
     * 직영 : yymmdd + 1 + 001
     * 자영 : yddmm + '-' + 1 + '-' + 00001
     * 
     * @param keepNumber
     * @return
     */
    public static String makeKeepNumber(String keepNumber) {
    	String returnData 		= "";
    	SqlSession session = null;
    	
    	try {
			session = SqlSessionFactoryManager.openSqlSession();
			String storeCode = T_KH_STOREHandler.getHandler().getStore_code_class(session);
			LogUtility.getPumpMLogger().debug("[MakeKeepNumber] Store CODE = " + storeCode);
			
			if ("12".equals(storeCode) || "11".equals(storeCode)) {
				returnData = keepNumber.substring(0, 4) + "-" +
								keepNumber.substring(4, 5) + "-" +
								keepNumber.substring(5, 10);
				
			} else {
				returnData = keepNumber;
				
			}	// end if
			
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}finally{
			SqlSessionFactoryManager.closeSqlSession(session);
		}
		
		LogUtility.getPumpMLogger().debug("[MakeKeepNumber] KeepNumber = " + returnData);
    	return returnData;
    	
    }	// end makeKeepNumber
    
    
    
    /**
	 * 충전기 ODT 최종 판매데이터를 이용하여 거래처 고객 판매 / 현금 판매가 되었는지 여부를 확인한다.
	 * 
	 * @param shWorkMsg		: 충전기 ODT 최종 판매데이터 전송
	 * @return
	 */
	public static boolean shouldCreateMoreUPOSMessage(SH_WorkingMessage shWorkMsg) {
		boolean rlt = false ;
		try {
			Vector<SH_RepInfo> repInfo = shWorkMsg.getRepInfo() ;
			for (int i = 0 ; i < repInfo.size() ; i++) {
				String flag = repInfo.get(i).getFlag() ;				
				if ("2".equals(flag)) {
					rlt = true ;
					break ;
				} else if ("B".equals(flag)) {
					rlt = true ;
					break ;					
				} else if ("1".equals(flag)) {
					rlt = true ;
					break ;
				} else if ("4".equals(flag)) {
					rlt = true ;
					break ;
				} 
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		LogUtility.getPumpMLogger().debug("[Pump M] shouldCreateMoreUPOSMessage=" + rlt) ;
		return rlt ;
	}

    
    /**
	 * 0001 전문 전송 여부를 설정한다.
	 * 
	 * @param nozzle_no	: 노즐 번호
	 * @param khproc_no		: KH 처리번호
	 * @param workMsg	: 판매 완료 전문 (SH_WorkingMessage)
	 * @return
	 */
	public static boolean shouldSend0001UPOSMessageToSaleM(String nozzle_no, String khproc_no, WorkingMessage workMsg) {
		boolean rlt = true ;

		return rlt ;
	}
    
    /**
	 * UPOSMessage 응답 전문을 Sale M 으로 보낼지를 판단한다.
	 * 충전기에서 보너스 누적 응답 및 현금 영수증 응답은 판매 완료 이후 일어나는 것이기 때문에, 이 전문은 POS 로 전송하지 않는다.
	 * 
	 * @param uPosMsg	: UPOSMessage 응답 전문
	 * @return
	 */
    public static boolean shouldSendToSaleM(UPOSMessage uPosMsg) {
    	boolean rlt = true ;

    	try {
    		int messageTypeInt = Integer.parseInt(uPosMsg.getMessageType()) ;
    		switch (messageTypeInt) {
//    			case IUPOSConstant.MESSAGETYPE_INT_0004 :	// 보너스누적 응답 -> 보내도록 변경
    			case IUPOSConstant.MESSAGETYPE_INT_0112 :	// GS보너스점수조회 응답
//    			case IUPOSConstant.MESSAGETYPE_INT_0014 :	// 현금보너스 응답 -> 보내도록 변경
    			case IUPOSConstant.MESSAGETYPE_INT_0016 :	// 국세청현금영수증 응답
    			{
    				rlt = false ;
    				break ;
    			}
    		}
    	} catch (Exception e) {
    		LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
    	}
    	return rlt ;	
    }
}
