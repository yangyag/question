package com.gsc.kixxhub.module.pumpm.pump.util;

import com.gsc.kixxhub.common.data.ICode;
import com.gsc.kixxhub.common.data.ICustConstant;
import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.posdata.POSHeader;
import com.gsc.kixxhub.common.data.posdata.POS_DW;
import com.gsc.kixxhub.common.data.pump.HF_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S3_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S4_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.ST_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.module.pumpm.pump.data.cust.CustReturnValue;
import com.gsc.kixxhub.module.pumpm.pump.data.cust.LimitAmount;
import com.gsc.kixxhub.module.pumpm.pump.manager.PumpMODTSaleManager;
import com.gsc.kixxhub.module.pumpm.pump.manager.PumpMPriceManager;
import com.gsc.kixxhub.module.pumpm.pump.manager.PumpMTransactionManager;

public class PumpMObjectValidation {

	/**
	 * 거래처인 경우 거래처 판매 단가를 리턴하고, 아닌 경우 null 을 리턴한다.
	 * @param nozzle_no
	 * @return
	 */
	public static String getSalesBasePriceIfCustomer(String nozzle_no) {
		String salesBasePrice = null ;
		POSHeader posMsg = PumpMODTSaleManager.getCustPOSPumpM(nozzle_no) ;
		
		if (posMsg != null) {
			if (posMsg instanceof POS_DW) {
				POS_DW dwPosM = (POS_DW) posMsg ;
								
				String card_code_base = dwPosM.getCard_code_base() ;		// 카드 기준

				if (validatePumpMObject(posMsg)) {
					
					//	2. 외상 거래처인지 현금 거래처인지 조사한다.
					if (card_code_base.equals(ICode.CARD_CODE_BASE_01)) {
						// 2-1) 현금 거래처이면
						// 			할인 단가를 이용하여 결재 금액을 재 구성하고, SB 전문을 수정한다.	-> Return
						salesBasePrice = dwPosM.getBasePrice();
					} else if (card_code_base.equals(ICode.CARD_CODE_BASE_02)) {
						// 2-2) 외상 거래처이면
						String rentlimit_proc_ind_overlimit = dwPosM.getRentlimit_proc_ind_overlimit() ;
						// 			외상결제타입 이 '02' , '04' 이면 점두가를 이용하여 SB 전문을 수정
						// 			'03' 이면 할인 단가를 이용하여 SB 전문을 수정
						// 			'01' 이면 ?
						
						if (rentlimit_proc_ind_overlimit.equals(ICode.PROC_IND_OVERLIMIT_03)) {
							salesBasePrice = dwPosM.getBasePrice();
						}
					}
				}				
			}			
		}		
		return salesBasePrice ;
	}
	
	/**
	 * 외상 거래처나 현금 거래처 정보를 통한 결재 일 경우 등록된 계약 단가와 판매 금액(계약단가 * 주유량)를 리턴하고, 
	 * 그러한 결재가 아닐 경우 Null 을 리턴한다.
	 * 
	 * @param nozzle_no
	 * @return
	 */
	public static String[] getSalesPriceIfCustomer(String nozzle_no, String liter) {
		String salesPrice[] = null ;
		POSHeader posMsg = PumpMODTSaleManager.getCustPOSPumpM(nozzle_no) ;
		
		if (posMsg != null) {
			if (posMsg instanceof POS_DW) {
				POS_DW dwPosM = (POS_DW) posMsg ;
								
				String card_code_base = dwPosM.getCard_code_base() ;		// 카드 기준
				String rcptsheetissue_code_amtsale = dwPosM.getRcptsheetissue_code_amtsale(); 	// 매출금액처리구분
	
				if (validatePumpMObject(posMsg)) {
					
					//	2. 외상 거래처인지 현금 거래처인지 조사한다.
					if (card_code_base.equals(ICode.CARD_CODE_BASE_01)) {
						salesPrice = new String[2] ;
						// 2-1) 현금 거래처이면
						// 			할인 단가를 이용하여 결재 금액을 재 구성하고, SB 전문을 수정한다.	-> Return
						salesPrice[0] = dwPosM.getBasePrice();
						salesPrice[1]  = 
								GlobalUtility.getStringValue(PumpMUtil.handleRcptsheetissue_code_amtsale(Double.parseDouble(salesPrice[0]), Double.parseDouble(liter), rcptsheetissue_code_amtsale));

					} else if (card_code_base.equals(ICode.CARD_CODE_BASE_02)) {
						// 2-2) 외상 거래처이면
						String rentlimit_proc_ind_overlimit = dwPosM.getRentlimit_proc_ind_overlimit() ;
						// 			외상결제타입 이 '02' , '04' 이면 점두가를 이용하여 SB 전문을 수정
						// 			'03' 이면 할인 단가를 이용하여 SB 전문을 수정
						// 			'01' 이면 ?
						
						if (rentlimit_proc_ind_overlimit.equals(ICode.PROC_IND_OVERLIMIT_03)) {
							salesPrice = new String[2] ;
							salesPrice[0] = dwPosM.getBasePrice();
							salesPrice[1]  = 
									GlobalUtility.getStringValue(PumpMUtil.handleRcptsheetissue_code_amtsale(Double.parseDouble(salesPrice[0]), Double.parseDouble(liter), rcptsheetissue_code_amtsale));

						}
					}
				}				
			}			
		}		
		return salesPrice ;
	}

	/**
	 * 거래처 수행으로 인한 리턴 값을 Validation 하도록 한다.
	 * 
	 * 변경 사항 [2008.11.06] by 오춘열 부장님
	 * 		차량별 한도에서 무제한 처리
	 * 			- 한도를 입력하지 않은 경우
	 * 			- 한도를 Zero 로 입력 한 경우
	 * 			- 차량별한도인데 거래처카드에 차량번호를 등록하지 않을 경우
	 * 
	 * 		보관증 기능 보완 및 추가
	 * 			- 1회정량에서 정량을 Zero로 하면 무제한 처리
	 * 			- 정량입력에서 Zero로 입력하면 무제한으로 처리되게
	 * 			- 정량거래처의 경우도 환경설정이 금액으로 되어 있으면 금액으로 발행되게
	 * @param custReturnValue		: 거래처 수행이후 리턴 값
	 * @return
	 */
	public static boolean validateCustReturnValue(CustReturnValue custReturnValue, String fixedQty_yn, String fixedQty) {
		boolean rlt = true ;
		try {
			int state = custReturnValue.getState() ;
			switch (state) {
			/**
			 * 
			 * 변경 사항 [2008.11.06] by 오춘열 부장님
			 * 		차량별 한도에서 무제한 처리
			 * 			- 한도를 입력하지 않은 경우
			 * 			- 한도를 Zero 로 입력 한 경우
=			 * 
			 * 		보관증 기능 보완 및 추가
			 * 			- 1회정량에서 정량을 Zero로 하면 무제한 처리
			 * 			- 정량입력에서 Zero로 입력하면 무제한으로 처리되게
			 * 			- 정량거래처의 경우도 환경설정이 금액으로 되어 있으면 금액으로 발행되게
			 */
				case ICustConstant.STATE_60 :
				case ICustConstant.STATE_61 :
				case ICustConstant.STATE_62 :
				case ICustConstant.STATE_63 :
				case ICustConstant.STATE_64 :
				case ICustConstant.STATE_65 : {
					// 1회정량에서 정량을 Zero로 하면 무제한 처리
					String amount1 = custReturnValue.getAmount1() ;
					if ((amount1 == null) || (Double.parseDouble(amount1) == 0)) {
						LogUtility.getPumpMLogger().info("[Pump M] Change State " + state + "->" + (state-30) +" because 1Amount is 0") ;
						custReturnValue.setState(state - 30) ;	// 무제한으로 변경
					}
					break ;
				}	

				case ICustConstant.STATE_90 :
				case ICustConstant.STATE_91 :
				case ICustConstant.STATE_92 :
				case ICustConstant.STATE_93 :
				case ICustConstant.STATE_94 :
				case ICustConstant.STATE_95 : {
					// 정량입력에서 Zero로 입력하면 무제한으로 처리되게
					if ("1".equals(fixedQty_yn) && ((fixedQty == null) || (Double.parseDouble(fixedQty) == 0))){
						LogUtility.getPumpMLogger().info("[Pump M] Change State " + state + "->" + (state-60) +" because fixedQty is 0") ;
						custReturnValue.setState(state - 60) ;	// 무제한으로 변경						
					}	
					break ;
				}
				
				case ICustConstant.STATE_40 :
				case ICustConstant.STATE_41 :
				case ICustConstant.STATE_42 :
				case ICustConstant.STATE_43 :
				case ICustConstant.STATE_44 :
				case ICustConstant.STATE_45 : {
					// 한도를 Zero 로 입력 한 경우 무제한으로 처리
					// 한도를 입력하지 않은 경우 무제한으로 처리
					LimitAmount limitAmount = custReturnValue.getLimitAmount() ;
					if (limitAmount != null) {
						String limit = limitAmount.getLimit() ;
						if ((limit == null) || ("".equals(limit) || (0 == Double.parseDouble(limit)))) {
							LogUtility.getPumpMLogger().info("[Pump M] Change State " + state + "->" + (state-10) +" because limit is 0") ;
							custReturnValue.setState(state - 10) ;	// 무제한으로 변경		
						}
					}
				}
			}			
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
				
		return rlt;
	}
	
	/**
	 * 주유기로 부터 전송되는 Preset 데이터를 검증한다.
	 * 아래 조건 중 하나에 일치할 경우 버린다.
	 * 		[2008.11.07] 변경 사항 by 정우철
	 * 		1. 현재 주유중인 금액이 9 만원을 넘은 경우
	 * 			Reason > Pump A 와 주유기 통신시 주유량 혹은 금액 중 하나가 0 인 경우 Preset 데이터로 인식해서 Pump M 으로 전송함.
	 * 				이 경우는 주유기 마다 틀리지만, 10만원 혹은 100만원이 넘는 경우 종종 발생함. 이로 인해 금액 보정이 잘못 될 수 있음. 
	 * 
	 * @param hfWorkMsg
	 * @return
	 */
	private static boolean validateHFWorkingMessage(HF_WorkingMessage hfWorkMsg) {
		boolean rlt = true ;
		double pumpingPrice = PumpMPriceManager.getCurrPrice(Integer.parseInt(hfWorkMsg.getNozzleNo())) ;
		if (pumpingPrice > PumpMPriceManager.INT_90000_PRICE_MIN) {
			LogUtility.getPumpMLogger().warn("[Pump M] Drop HF Info. Curr Pumping Price="+ pumpingPrice) ;
			rlt = false ;
		}		
		return rlt;
	}
	
	/**
	 * 거래상태 및 카드 기준이 정상인지 확인한다.
	 * 카드 기준 06 : 미등록 카드, 거래상태 02, 03 : 정지, 말소 가 아니어야 한다.
	 * 
	 * @param pumpM_DW
	 * @return
	 */
	private static boolean validatePOSPumpM_DW(POS_DW pumpM_DW) {
		return ODTUtility_Common.validatePOSPumpM_DW(pumpM_DW) ;
	}
	
	/**
	 * Pump A 로 부터 전송받은 WorkingMessage 를 검증한다.
	 * 
	 * @param workMsg	: 검증하고자 하는 WorkingMessage
	 * @return
	 */
	public static boolean validatePumpAObject(WorkingMessage workMsg) {
		boolean rlt = true ;
		
		try {
			if (workMsg instanceof S3_WorkingMessage) {
				// 주유중 전문 Validation
				rlt = validateS3WorkingMessage((S3_WorkingMessage)workMsg) ;
			} else if (workMsg instanceof S4_WorkingMessage) {
				// 주유완료 전문 Validation				
				rlt = validateS4WorkingMessage((S4_WorkingMessage)workMsg) ;			
			} else if (workMsg instanceof SB_WorkingMessage) {
				// 충전기 카드 결재 요청 전문 Validation
				rlt = validateSBWorkingMessage((SB_WorkingMessage)workMsg) ;			
			} else if (workMsg instanceof HF_WorkingMessage) {
				// Preset 전문 Validation
				rlt = validateHFWorkingMessage((HF_WorkingMessage)workMsg) ;			
			} else if (workMsg instanceof ST_WorkingMessage) {
				rlt = validateST_WorkingMessage((ST_WorkingMessage)workMsg) ;					
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		return rlt ;
	}	

	/**
	 * 
	 * @param posMsg
	 * @return
	 */
	public static boolean validatePumpMObject(POSHeader posMsg) {
		boolean rlt = true ;
		try {
			if (posMsg instanceof POS_DW) {
				rlt = validatePOSPumpM_DW((POS_DW)posMsg) ;
			} 
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		
		return rlt ;
	}

	/**
	 * 주유중 전문을 검증한다. 
	 * 
	 * 아래와 같을 경우 전문을 버린다.
	 * 		[2008.11.07] 변경 사항 by 정우철 - 실제 매장에서 아래와 같이 발생한 경우가 있음.
	 * 		1. 주유금액과 주유량이 0 인 경우	-> Pump A 에서 주유데이터 Parsing 시 에러가 발생하는 경우가 있음.
	 * 			이 경우 주유금액과 주유량이 0 으로 설정되곤 함.
	 * 			Reason > 주유기와 Pump A 통신시 데이터가 이상하여서 주유량과 금액이 0 으로 잘못 처리되는 경우가 있음.
	 * 				이로 인해 금액 보정 로직에 영향을 미치므로 이 정보는 누락시킴.
	 * 
	 * @param s3WorkMsg		: 주유중 전문
	 * @return
	 */
	private static boolean validateS3WorkingMessage(S3_WorkingMessage s3WorkMsg) {
		boolean rlt = true ;
		double price = Double.parseDouble(s3WorkMsg.getPrice()) ;
		double liter = Double.parseDouble(s3WorkMsg.getLiter()) ;
		
		// 1. 주유금액과 주유량이 0 인 경우
		if ((price == 0) && (liter == 0)) {
			LogUtility.getPumpMLogger().warn("[Pump M] Drop S3. Price=0,Liter=0") ;
			rlt = false ;
		} 
		
		return rlt ;
	}


	/**
	 * 주유완료 전문을 검증한다.
	 * 
	 * @param s4WorkMsg		: 주유완료 전문
	 * @return
	 */
	private static boolean validateS4WorkingMessage(S4_WorkingMessage s4WorkMsg) {
		boolean rlt = true ;
		// TODO By 박종호
		
		return rlt ;
	}
	
	/**
	 * 	1. PumpMODTSaleManager 에 DW 전문이 있는지 조사한다.
	 * 		1-1) 없으면 	-> Return 한다.
	 * 		1-2) 있으면	-> 2
	 * 	2. 외상 거래처인지 현금 거래처인지 조사한다.
	 * 		DW 전문의 카드 기준이 01 이면 현금거래처 , 02 면 외상거래처 (03,04 ? )
	 * 		2-1) 현금 거래처이면
	 * 			할인 단가를 이용하여 결재 금액을 재 구성하고, SB 전문을 수정한다.	-> Return
	 * 		2-2) 외상 거래처이면
	 * 			외상결제타입 이 '02' , '04' 이면 점두가를 이용하여 SB 전문을 수정
	 * 			'03' 이면 할인 단가 이면 점두가를 이용하여 SB 전문을 수정
	 * 			'01' 이면 ?
	 * 		
	 * 
	 * @param sbWorkMsg	: 충전기 신용카드 요청 전문
	 * @return
	 */
	private static boolean validateSBWorkingMessage(SB_WorkingMessage sbWorkMsg) {
		boolean rlt = true ;
		
		POSHeader posMsg = PumpMODTSaleManager.getCustPOSPumpM(sbWorkMsg.getNozzleNo()) ;
		
		// 1. PumpMODTSaleManager 에 DW 전문이 있는지 조사한다.
		if (posMsg != null) {
			if (posMsg instanceof POS_DW) {
				String discountBasePrice = 
					PumpMUtil.convertNumberFormatFromPumpToStandardFormat(sbWorkMsg.getBasePrice(),2) ;
				String discountPayPrice = sbWorkMsg.getPrice() ;				
				String amountSB =  PumpMUtil.convertNumberFormatFromPumpToStandardFormat(sbWorkMsg.getAmount(), 3);
				
				POS_DW dwPosM = (POS_DW) posMsg ;
								
				String card_code_base = dwPosM.getCard_code_base() ;		// 카드 기준
				String rcptsheetissue_code_amtsale = dwPosM.getRcptsheetissue_code_amtsale(); 	// 매출금액처리구분
				
				if (validatePumpMObject(posMsg)) {
					
					//	2. 외상 거래처인지 현금 거래처인지 조사한다.
					if (card_code_base.equals(ICode.CARD_CODE_BASE_01)) {
						// 2-1) 현금 거래처이면
						// 			할인 단가를 이용하여 결재 금액을 재 구성하고, SB 전문을 수정한다.	-> Return
						discountBasePrice = dwPosM.getBasePrice();						
						String nzBasePrice = PumpMUtil.getBasePriceFromNozzleNo(sbWorkMsg.getNozzleNo()) ;		
						// 거래처 판매 단가와 점두가가 틀릴 경우만 재계산한다.
						LogUtility.getPumpMLogger().debug("[Pump M] 현금거래처 DSBP="+discountBasePrice+":NZBP"+nzBasePrice) ;
						if (Double.parseDouble(discountBasePrice) != Double.parseDouble(nzBasePrice)) {
							discountPayPrice  = 
									GlobalUtility.getStringValue(PumpMUtil.handleRcptsheetissue_code_amtsale(Double.parseDouble(discountBasePrice), Double.parseDouble(amountSB), rcptsheetissue_code_amtsale));
	
							sbWorkMsg.setBasePrice(PumpMUtil.convertNumberFormatFromPOSToPump(discountBasePrice, 4, 2)) ;
							sbWorkMsg.setPrice(discountPayPrice) ;
						}
						
					} else if (card_code_base.equals(ICode.CARD_CODE_BASE_02)) {
						// 2-2) 외상 거래처이면
						String rentlimit_proc_ind_overlimit = dwPosM.getRentlimit_proc_ind_overlimit() ;
						// 			외상결제타입 이 '02' , '04' 이면 점두가를 이용하여 SB 전문을 수정
						// 			'03' 이면 할인 단가를 이용하여 SB 전문을 수정
						// 			'01' 이면 ?
						
						if (rentlimit_proc_ind_overlimit.equals(ICode.PROC_IND_OVERLIMIT_03)) {
							discountBasePrice = dwPosM.getBasePrice();
							String nzBasePrice = PumpMUtil.getBasePriceFromNozzleNo(sbWorkMsg.getNozzleNo()) ;	
							// 거래처 판매 단가와 점두가가 틀릴 경우만 재계산한다.
							LogUtility.getPumpMLogger().debug("[Pump M] 외상거래처 DSBP="+discountBasePrice+":NZBP"+nzBasePrice) ;
							if (Double.parseDouble(discountBasePrice) != Double.parseDouble(nzBasePrice)) {
								discountPayPrice  = 
										GlobalUtility.getStringValue(PumpMUtil.handleRcptsheetissue_code_amtsale(Double.parseDouble(discountBasePrice), Double.parseDouble(amountSB), rcptsheetissue_code_amtsale));
								
								sbWorkMsg.setBasePrice(PumpMUtil.convertNumberFormatFromPOSToPump(discountBasePrice, 4, 2)) ;
								sbWorkMsg.setPrice(discountPayPrice) ;
							}
						}
					}
					
					LogUtility.getPumpMLogger().debug("[Pump M] 변화된 SB 전문은 다음과 같습니다.(validateSBWorkingMessage)") ;
					sbWorkMsg.print() ;
				}				
			}			
		}				
		return rlt ;
	}

	
	/**
	 * 
	 * @param stWorkMsg
	 * @return
	 */
	private static boolean validateST_WorkingMessage(ST_WorkingMessage stWorkMsg) {
		boolean rlt = true ;
		
		// 그 전 상태와 동일할 경우 ST 전문을 버린다.
		if (PumpMTransactionManager.getInstance().isSameState(stWorkMsg.getConnectNozzleNo(),IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED)) {
			rlt = false ;
		} else {
			rlt = true ;
		}
		
		return rlt;
	}

	
}
