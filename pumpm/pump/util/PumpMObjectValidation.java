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
	 * �ŷ�ó�� ��� �ŷ�ó �Ǹ� �ܰ��� �����ϰ�, �ƴ� ��� null �� �����Ѵ�.
	 * @param nozzle_no
	 * @return
	 */
	public static String getSalesBasePriceIfCustomer(String nozzle_no) {
		String salesBasePrice = null ;
		POSHeader posMsg = PumpMODTSaleManager.getCustPOSPumpM(nozzle_no) ;
		
		if (posMsg != null) {
			if (posMsg instanceof POS_DW) {
				POS_DW dwPosM = (POS_DW) posMsg ;
								
				String card_code_base = dwPosM.getCard_code_base() ;		// ī�� ����

				if (validatePumpMObject(posMsg)) {
					
					//	2. �ܻ� �ŷ�ó���� ���� �ŷ�ó���� �����Ѵ�.
					if (card_code_base.equals(ICode.CARD_CODE_BASE_01)) {
						// 2-1) ���� �ŷ�ó�̸�
						// 			���� �ܰ��� �̿��Ͽ� ���� �ݾ��� �� �����ϰ�, SB ������ �����Ѵ�.	-> Return
						salesBasePrice = dwPosM.getBasePrice();
					} else if (card_code_base.equals(ICode.CARD_CODE_BASE_02)) {
						// 2-2) �ܻ� �ŷ�ó�̸�
						String rentlimit_proc_ind_overlimit = dwPosM.getRentlimit_proc_ind_overlimit() ;
						// 			�ܻ����Ÿ�� �� '02' , '04' �̸� ���ΰ��� �̿��Ͽ� SB ������ ����
						// 			'03' �̸� ���� �ܰ��� �̿��Ͽ� SB ������ ����
						// 			'01' �̸� ?
						
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
	 * �ܻ� �ŷ�ó�� ���� �ŷ�ó ������ ���� ���� �� ��� ��ϵ� ��� �ܰ��� �Ǹ� �ݾ�(���ܰ� * ������)�� �����ϰ�, 
	 * �׷��� ���簡 �ƴ� ��� Null �� �����Ѵ�.
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
								
				String card_code_base = dwPosM.getCard_code_base() ;		// ī�� ����
				String rcptsheetissue_code_amtsale = dwPosM.getRcptsheetissue_code_amtsale(); 	// ����ݾ�ó������
	
				if (validatePumpMObject(posMsg)) {
					
					//	2. �ܻ� �ŷ�ó���� ���� �ŷ�ó���� �����Ѵ�.
					if (card_code_base.equals(ICode.CARD_CODE_BASE_01)) {
						salesPrice = new String[2] ;
						// 2-1) ���� �ŷ�ó�̸�
						// 			���� �ܰ��� �̿��Ͽ� ���� �ݾ��� �� �����ϰ�, SB ������ �����Ѵ�.	-> Return
						salesPrice[0] = dwPosM.getBasePrice();
						salesPrice[1]  = 
								GlobalUtility.getStringValue(PumpMUtil.handleRcptsheetissue_code_amtsale(Double.parseDouble(salesPrice[0]), Double.parseDouble(liter), rcptsheetissue_code_amtsale));

					} else if (card_code_base.equals(ICode.CARD_CODE_BASE_02)) {
						// 2-2) �ܻ� �ŷ�ó�̸�
						String rentlimit_proc_ind_overlimit = dwPosM.getRentlimit_proc_ind_overlimit() ;
						// 			�ܻ����Ÿ�� �� '02' , '04' �̸� ���ΰ��� �̿��Ͽ� SB ������ ����
						// 			'03' �̸� ���� �ܰ��� �̿��Ͽ� SB ������ ����
						// 			'01' �̸� ?
						
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
	 * �ŷ�ó �������� ���� ���� ���� Validation �ϵ��� �Ѵ�.
	 * 
	 * ���� ���� [2008.11.06] by ���῭ �����
	 * 		������ �ѵ����� ������ ó��
	 * 			- �ѵ��� �Է����� ���� ���
	 * 			- �ѵ��� Zero �� �Է� �� ���
	 * 			- �������ѵ��ε� �ŷ�óī�忡 ������ȣ�� ������� ���� ���
	 * 
	 * 		������ ��� ���� �� �߰�
	 * 			- 1ȸ�������� ������ Zero�� �ϸ� ������ ó��
	 * 			- �����Է¿��� Zero�� �Է��ϸ� ���������� ó���ǰ�
	 * 			- �����ŷ�ó�� ��쵵 ȯ�漳���� �ݾ����� �Ǿ� ������ �ݾ����� ����ǰ�
	 * @param custReturnValue		: �ŷ�ó �������� ���� ��
	 * @return
	 */
	public static boolean validateCustReturnValue(CustReturnValue custReturnValue, String fixedQty_yn, String fixedQty) {
		boolean rlt = true ;
		try {
			int state = custReturnValue.getState() ;
			switch (state) {
			/**
			 * 
			 * ���� ���� [2008.11.06] by ���῭ �����
			 * 		������ �ѵ����� ������ ó��
			 * 			- �ѵ��� �Է����� ���� ���
			 * 			- �ѵ��� Zero �� �Է� �� ���
=			 * 
			 * 		������ ��� ���� �� �߰�
			 * 			- 1ȸ�������� ������ Zero�� �ϸ� ������ ó��
			 * 			- �����Է¿��� Zero�� �Է��ϸ� ���������� ó���ǰ�
			 * 			- �����ŷ�ó�� ��쵵 ȯ�漳���� �ݾ����� �Ǿ� ������ �ݾ����� ����ǰ�
			 */
				case ICustConstant.STATE_60 :
				case ICustConstant.STATE_61 :
				case ICustConstant.STATE_62 :
				case ICustConstant.STATE_63 :
				case ICustConstant.STATE_64 :
				case ICustConstant.STATE_65 : {
					// 1ȸ�������� ������ Zero�� �ϸ� ������ ó��
					String amount1 = custReturnValue.getAmount1() ;
					if ((amount1 == null) || (Double.parseDouble(amount1) == 0)) {
						LogUtility.getPumpMLogger().info("[Pump M] Change State " + state + "->" + (state-30) +" because 1Amount is 0") ;
						custReturnValue.setState(state - 30) ;	// ���������� ����
					}
					break ;
				}	

				case ICustConstant.STATE_90 :
				case ICustConstant.STATE_91 :
				case ICustConstant.STATE_92 :
				case ICustConstant.STATE_93 :
				case ICustConstant.STATE_94 :
				case ICustConstant.STATE_95 : {
					// �����Է¿��� Zero�� �Է��ϸ� ���������� ó���ǰ�
					if ("1".equals(fixedQty_yn) && ((fixedQty == null) || (Double.parseDouble(fixedQty) == 0))){
						LogUtility.getPumpMLogger().info("[Pump M] Change State " + state + "->" + (state-60) +" because fixedQty is 0") ;
						custReturnValue.setState(state - 60) ;	// ���������� ����						
					}	
					break ;
				}
				
				case ICustConstant.STATE_40 :
				case ICustConstant.STATE_41 :
				case ICustConstant.STATE_42 :
				case ICustConstant.STATE_43 :
				case ICustConstant.STATE_44 :
				case ICustConstant.STATE_45 : {
					// �ѵ��� Zero �� �Է� �� ��� ���������� ó��
					// �ѵ��� �Է����� ���� ��� ���������� ó��
					LimitAmount limitAmount = custReturnValue.getLimitAmount() ;
					if (limitAmount != null) {
						String limit = limitAmount.getLimit() ;
						if ((limit == null) || ("".equals(limit) || (0 == Double.parseDouble(limit)))) {
							LogUtility.getPumpMLogger().info("[Pump M] Change State " + state + "->" + (state-10) +" because limit is 0") ;
							custReturnValue.setState(state - 10) ;	// ���������� ����		
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
	 * ������� ���� ���۵Ǵ� Preset �����͸� �����Ѵ�.
	 * �Ʒ� ���� �� �ϳ��� ��ġ�� ��� ������.
	 * 		[2008.11.07] ���� ���� by ����ö
	 * 		1. ���� �������� �ݾ��� 9 ������ ���� ���
	 * 			Reason > Pump A �� ������ ��Ž� ������ Ȥ�� �ݾ� �� �ϳ��� 0 �� ��� Preset �����ͷ� �ν��ؼ� Pump M ���� ������.
	 * 				�� ���� ������ ���� Ʋ������, 10���� Ȥ�� 100������ �Ѵ� ��� ���� �߻���. �̷� ���� �ݾ� ������ �߸� �� �� ����. 
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
	 * �ŷ����� �� ī�� ������ �������� Ȯ���Ѵ�.
	 * ī�� ���� 06 : �̵�� ī��, �ŷ����� 02, 03 : ����, ���� �� �ƴϾ�� �Ѵ�.
	 * 
	 * @param pumpM_DW
	 * @return
	 */
	private static boolean validatePOSPumpM_DW(POS_DW pumpM_DW) {
		return ODTUtility_Common.validatePOSPumpM_DW(pumpM_DW) ;
	}
	
	/**
	 * Pump A �� ���� ���۹��� WorkingMessage �� �����Ѵ�.
	 * 
	 * @param workMsg	: �����ϰ��� �ϴ� WorkingMessage
	 * @return
	 */
	public static boolean validatePumpAObject(WorkingMessage workMsg) {
		boolean rlt = true ;
		
		try {
			if (workMsg instanceof S3_WorkingMessage) {
				// ������ ���� Validation
				rlt = validateS3WorkingMessage((S3_WorkingMessage)workMsg) ;
			} else if (workMsg instanceof S4_WorkingMessage) {
				// �����Ϸ� ���� Validation				
				rlt = validateS4WorkingMessage((S4_WorkingMessage)workMsg) ;			
			} else if (workMsg instanceof SB_WorkingMessage) {
				// ������ ī�� ���� ��û ���� Validation
				rlt = validateSBWorkingMessage((SB_WorkingMessage)workMsg) ;			
			} else if (workMsg instanceof HF_WorkingMessage) {
				// Preset ���� Validation
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
	 * ������ ������ �����Ѵ�. 
	 * 
	 * �Ʒ��� ���� ��� ������ ������.
	 * 		[2008.11.07] ���� ���� by ����ö - ���� ���忡�� �Ʒ��� ���� �߻��� ��찡 ����.
	 * 		1. �����ݾװ� �������� 0 �� ���	-> Pump A ���� ���������� Parsing �� ������ �߻��ϴ� ��찡 ����.
	 * 			�� ��� �����ݾװ� �������� 0 ���� �����ǰ� ��.
	 * 			Reason > ������� Pump A ��Ž� �����Ͱ� �̻��Ͽ��� �������� �ݾ��� 0 ���� �߸� ó���Ǵ� ��찡 ����.
	 * 				�̷� ���� �ݾ� ���� ������ ������ ��ġ�Ƿ� �� ������ ������Ŵ.
	 * 
	 * @param s3WorkMsg		: ������ ����
	 * @return
	 */
	private static boolean validateS3WorkingMessage(S3_WorkingMessage s3WorkMsg) {
		boolean rlt = true ;
		double price = Double.parseDouble(s3WorkMsg.getPrice()) ;
		double liter = Double.parseDouble(s3WorkMsg.getLiter()) ;
		
		// 1. �����ݾװ� �������� 0 �� ���
		if ((price == 0) && (liter == 0)) {
			LogUtility.getPumpMLogger().warn("[Pump M] Drop S3. Price=0,Liter=0") ;
			rlt = false ;
		} 
		
		return rlt ;
	}


	/**
	 * �����Ϸ� ������ �����Ѵ�.
	 * 
	 * @param s4WorkMsg		: �����Ϸ� ����
	 * @return
	 */
	private static boolean validateS4WorkingMessage(S4_WorkingMessage s4WorkMsg) {
		boolean rlt = true ;
		// TODO By ����ȣ
		
		return rlt ;
	}
	
	/**
	 * 	1. PumpMODTSaleManager �� DW ������ �ִ��� �����Ѵ�.
	 * 		1-1) ������ 	-> Return �Ѵ�.
	 * 		1-2) ������	-> 2
	 * 	2. �ܻ� �ŷ�ó���� ���� �ŷ�ó���� �����Ѵ�.
	 * 		DW ������ ī�� ������ 01 �̸� ���ݰŷ�ó , 02 �� �ܻ�ŷ�ó (03,04 ? )
	 * 		2-1) ���� �ŷ�ó�̸�
	 * 			���� �ܰ��� �̿��Ͽ� ���� �ݾ��� �� �����ϰ�, SB ������ �����Ѵ�.	-> Return
	 * 		2-2) �ܻ� �ŷ�ó�̸�
	 * 			�ܻ����Ÿ�� �� '02' , '04' �̸� ���ΰ��� �̿��Ͽ� SB ������ ����
	 * 			'03' �̸� ���� �ܰ� �̸� ���ΰ��� �̿��Ͽ� SB ������ ����
	 * 			'01' �̸� ?
	 * 		
	 * 
	 * @param sbWorkMsg	: ������ �ſ�ī�� ��û ����
	 * @return
	 */
	private static boolean validateSBWorkingMessage(SB_WorkingMessage sbWorkMsg) {
		boolean rlt = true ;
		
		POSHeader posMsg = PumpMODTSaleManager.getCustPOSPumpM(sbWorkMsg.getNozzleNo()) ;
		
		// 1. PumpMODTSaleManager �� DW ������ �ִ��� �����Ѵ�.
		if (posMsg != null) {
			if (posMsg instanceof POS_DW) {
				String discountBasePrice = 
					PumpMUtil.convertNumberFormatFromPumpToStandardFormat(sbWorkMsg.getBasePrice(),2) ;
				String discountPayPrice = sbWorkMsg.getPrice() ;				
				String amountSB =  PumpMUtil.convertNumberFormatFromPumpToStandardFormat(sbWorkMsg.getAmount(), 3);
				
				POS_DW dwPosM = (POS_DW) posMsg ;
								
				String card_code_base = dwPosM.getCard_code_base() ;		// ī�� ����
				String rcptsheetissue_code_amtsale = dwPosM.getRcptsheetissue_code_amtsale(); 	// ����ݾ�ó������
				
				if (validatePumpMObject(posMsg)) {
					
					//	2. �ܻ� �ŷ�ó���� ���� �ŷ�ó���� �����Ѵ�.
					if (card_code_base.equals(ICode.CARD_CODE_BASE_01)) {
						// 2-1) ���� �ŷ�ó�̸�
						// 			���� �ܰ��� �̿��Ͽ� ���� �ݾ��� �� �����ϰ�, SB ������ �����Ѵ�.	-> Return
						discountBasePrice = dwPosM.getBasePrice();						
						String nzBasePrice = PumpMUtil.getBasePriceFromNozzleNo(sbWorkMsg.getNozzleNo()) ;		
						// �ŷ�ó �Ǹ� �ܰ��� ���ΰ��� Ʋ�� ��츸 �����Ѵ�.
						LogUtility.getPumpMLogger().debug("[Pump M] ���ݰŷ�ó DSBP="+discountBasePrice+":NZBP"+nzBasePrice) ;
						if (Double.parseDouble(discountBasePrice) != Double.parseDouble(nzBasePrice)) {
							discountPayPrice  = 
									GlobalUtility.getStringValue(PumpMUtil.handleRcptsheetissue_code_amtsale(Double.parseDouble(discountBasePrice), Double.parseDouble(amountSB), rcptsheetissue_code_amtsale));
	
							sbWorkMsg.setBasePrice(PumpMUtil.convertNumberFormatFromPOSToPump(discountBasePrice, 4, 2)) ;
							sbWorkMsg.setPrice(discountPayPrice) ;
						}
						
					} else if (card_code_base.equals(ICode.CARD_CODE_BASE_02)) {
						// 2-2) �ܻ� �ŷ�ó�̸�
						String rentlimit_proc_ind_overlimit = dwPosM.getRentlimit_proc_ind_overlimit() ;
						// 			�ܻ����Ÿ�� �� '02' , '04' �̸� ���ΰ��� �̿��Ͽ� SB ������ ����
						// 			'03' �̸� ���� �ܰ��� �̿��Ͽ� SB ������ ����
						// 			'01' �̸� ?
						
						if (rentlimit_proc_ind_overlimit.equals(ICode.PROC_IND_OVERLIMIT_03)) {
							discountBasePrice = dwPosM.getBasePrice();
							String nzBasePrice = PumpMUtil.getBasePriceFromNozzleNo(sbWorkMsg.getNozzleNo()) ;	
							// �ŷ�ó �Ǹ� �ܰ��� ���ΰ��� Ʋ�� ��츸 �����Ѵ�.
							LogUtility.getPumpMLogger().debug("[Pump M] �ܻ�ŷ�ó DSBP="+discountBasePrice+":NZBP"+nzBasePrice) ;
							if (Double.parseDouble(discountBasePrice) != Double.parseDouble(nzBasePrice)) {
								discountPayPrice  = 
										GlobalUtility.getStringValue(PumpMUtil.handleRcptsheetissue_code_amtsale(Double.parseDouble(discountBasePrice), Double.parseDouble(amountSB), rcptsheetissue_code_amtsale));
								
								sbWorkMsg.setBasePrice(PumpMUtil.convertNumberFormatFromPOSToPump(discountBasePrice, 4, 2)) ;
								sbWorkMsg.setPrice(discountPayPrice) ;
							}
						}
					}
					
					LogUtility.getPumpMLogger().debug("[Pump M] ��ȭ�� SB ������ ������ �����ϴ�.(validateSBWorkingMessage)") ;
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
		
		// �� �� ���¿� ������ ��� ST ������ ������.
		if (PumpMTransactionManager.getInstance().isSameState(stWorkMsg.getConnectNozzleNo(),IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED)) {
			rlt = false ;
		} else {
			rlt = true ;
		}
		
		return rlt;
	}

	
}
