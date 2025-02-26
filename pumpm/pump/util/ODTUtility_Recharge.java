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

		// �����ý��� ��� ���ʽ� ������ ���� �ʴ´�.
		// IT��ȹ�� �ǻ���� ����. edited by ykjang 2009.10.14				
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
	 * ������κ��� ����� �ܻ� �ŷ��� ��� �Ǹ� �Ϸ�� (������������� �ǸſϷ� - SH ����) KixxHub ���� 
	 * UPOSMessage ������ �����Ͽ� POS �� �����Ѵ�.
	 * �� ��� �����⿡ �������� ������ DW ������ SH ������ �̿��Ͽ� UPOSMessage ������ �����Ѵ�. 
	 * 
	 * < ���� ���� > 2008�� 10�� 8�� ���� 9�� 47��
	 * 
	 * ȸ�� ������
	 * 	������ ����, ������ ����, ����ö ����
	 * ����
	 * 	�ܰ� : 		1,000 ��
	 * 	������ : 	5 ����
	 * 	�����ݾ� : 	5,000��
	 * 
	 * ��-1
	 * 	���� 10,000 �� ���� ���� ���
	 * 		UPOSMessage ���� ���� (POS �� ���۵Ǵ� ���� ����)
	 * 			�������ܰ� 	1,000��
	 * 			�����Ĵܰ�	1,000��
	 * 			������		10 ����
	 * 			�������ݾ�	10,000��
	 * 			�����ıݾ�	10,000��
	 * 
	 * ��-2 ([2008.10.24] �Ͽ� �Ʒ� ���� ��������)
	 * 	���� �ŷ�ó	��� �ܰ� 800 ��
	 * 	���� 10,000 �� ���� ���� ���
	 * 		UPOSMessage ���� ���� (POS �� ���۵Ǵ� ���� ����)
	 * 			�������ܰ� 	1,000��
	 * 			�����Ĵܰ�	800��
	 * 			������		12.5 ����
	 * 			�������ݾ�	12,500��
	 * 			�����ıݾ�	10,000��
	 * 
	 * [2008.10.24] ȸ�� ������ : ������ ����, ������ , ����ö
	 * 		1. ���� �ŷ�ó�ε� �������� ������ ��� ���͸� �������� �ʰ�, �ݾ��� �����Ѵ�. -> [2008.10.08] ȸ�� ���� ����
	 * 		2. �Ž������� �ִ� ��� , �� �������� �Ž������� �� �� �ݾ��� POS �� �����ϵ��� �Ѵ�.
	 * 
	 * @param dwPumpM			: POSProtocol DW ����
	 * @param shWorkMsg			: PumpA SH ����
	 * @param khproc_no			: KH ó����ȣ
	 * @return
	 */
	public static ArrayList<UPOSMessage> createUPOSMessage(POS_DW dwPumpM, SH_WorkingMessage shWorkMsg, 
			String khproc_no) {
		LogUtility.getPumpMLogger().debug("[Pump M] DW ������ SH ������ �̿��Ͽ� ����/��ǰ��/�ܻ� UPOSMessage �� �����Ѵ�.") ;
		if (dwPumpM != null) 
			LogUtility.getPumpMLogger().info(dwPumpM.toString());
		if (shWorkMsg != null) shWorkMsg.print() ;
		
		ArrayList<UPOSMessage> uPosMsgArray = new ArrayList<UPOSMessage>() ;
		
		try {
			UPOSMessage uPosMsgCustomer = null ;
			UPOSMessage uPosMsgKeep = null ;
			UPOSMessage uPosMsgCash = null ;	// ���� ����
			UPOSMessage uPosMsgTicket = null ;	// ��ǰ�� ����
			
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

			String nozzle_no = shWorkMsg.getNozzleNo() ;							// �����ȣ
			String emp_no = PumpMODTSaleManager.getChargingPersonID(nozzle_no) ;	// ������ ID
			String custCard_No = "" ;												// �ŷ�óī���ȣ
			String ss_crStNum = "" ;												// �ŷ�ó��ȣ
			String ss_carNum = "" ;													// �ŷ�ó������ȣ
			String rcptsheetissue_code_amtsale = "" ;								// ����ݾ�ó������
			String term_id = "         " ;											// �ܸ��� ��ȣ
			String lastPayment_yn = "0" ;											// ������ ��������
			String led_code = "1" ;													// LED �ڵ�
			String keepDoc_limitDate = "" ;											// ������ ������
	
			if ((dwPumpM != null) && (PumpMObjectValidation.validatePumpMObject(dwPumpM))){
				custCard_No = dwPumpM.getCust_card_no() ;									// �ŷ�óī���ȣ
				ss_crStNum = dwPumpM.getCust_code() ;										// �ŷ�ó��ȣ
				ss_carNum = dwPumpM.getCar_no() ;											// �ŷ�ó������ȣ
				rcptsheetissue_code_amtsale = dwPumpM.getRcptsheetissue_code_amtsale() ;	// ����ݾ�ó������
			}
	
	//		ArrayList<UPOSMessage_ItemInfo_Item> itemInfoList = new ArrayList<UPOSMessage_ItemInfo_Item>() ;
	
			String nozBasePrice = PumpMUtil.convertBasePriceFromPumpToPOS(shWorkMsg.getUp1()) ;			// ���ܰ�
			String salesBasePrice = PumpMUtil.convertBasePriceFromPumpToPOS(shWorkMsg.getUp2()) ;		// �Ǹ� �ܰ�
			String pumpPrice = shWorkMsg.getTotalAMT2() ;		// �Ǹ� �ݾ�
			Vector<SH_RepInfo> repInfo = shWorkMsg.getRepInfo() ;
			
			for (int i = 0 ; i < repInfo.size() ; i++) {
				String flag = repInfo.get(i).getFlag() ;
				String liter = PumpMUtil.convertTotalLiterFromPumpTOPOS(repInfo.get(i).getLiter()) ;
				String amt = repInfo.get(i).getAmt() ;
				String keep_no = repInfo.get(i).getKeepNumber() ;
				
				if ("2".equals(flag)) {
					// �ܻ� �� ���
					LogUtility.getPumpMLogger().debug("[Pump M] SH ������ �ܻ� ������ �ֽ��ϴ�.") ;
					
					SH_RepInfo keepInfo = getKeepInfo(repInfo) ;
					if (keepInfo != null) {
						LogUtility.getPumpMLogger().debug("[Pump M] SH ������ ������ ���� ������ �ֽ��ϴ�. ������ ���� ���� =" + dwPumpM.getKeepissue_ind()) ;
						String keepLiter = PumpMUtil.convertTotalLiterFromPumpTOPOS(keepInfo.getLiter()) ;
						String keepAmt = keepInfo.getAmt() ;
						
						if (!"1".equals(dwPumpM.getKeepissue_ind())) {
							// ������ ���� ���ΰ� '����' �� �ƴ� ���
							// �ƴ� ��������� SH �������� ����� �������� �ö�´�. POS �� �����Ҷ����� 
							// �������� ���� ����� ������ �����Ͽ� �������� �Ѵ�.
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
							// ������ ���� ���ΰ� '����' �� ��� -> �ܻ����� �� 
							// �ŷ�ó UPOSMessage ����
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
							
							// ������ UPOSMessage ����
							UPOSMessage_ItemInfo_Item itemInfoItem =
								UPOSUtil.getUPOSMessage_ItemInfo_Item(nozzle_no, 
										khproc_no,
										PumpMUtil.convertTotalLiterFromPumpTOPOS(keepInfo.getLiter()), 
										salesBasePrice, 
										keepInfo.getAmt()) ;
							itemInfoItem.setRentlimit_proc_ind_overlimit("00") ;	// �ܻ� ����
//							itemInfoItem.setKeep_no(makeKeepNumber(keepInfo.getKeepNumber())) ;		// ������ ��ȣ
							itemInfoItem.setKeep_no(keepInfo.getKeepNumber()) ;		// ������ ��ȣ
							
							itemInfoItem.setIssue_type("01") ;						// ������ ���� : ���� (default)
							
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
						// ������ ������ ���� ���
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

					// ���� ���� (0012)
					LogUtility.getLogger().debug("[Pump M] SH ������ ���� ���� ������ �ֽ��ϴ�.") ;		
					
					UPOSMessage_ItemInfo item_infoCash = null ;
					String payment_amtCash = null ;
					
					/**
					 * 2016.04.04 WooChul Jung
					 * 	To-Be LPG ODT �� ���, ���ݰŷ�ó + ������ ��� ����� �ݾ����� ODT ���� SH �� ���ԵǾ �ö��. (As-Is �� �ƴ�)
					 * 
					 * ����>
					 *
					 *   ���ΰ�: 1,120 ��, �ŷ�ó�ܰ�: 1,020 ��
					 *   
					 *   As-Is  
					 *         1. ���ݰŷ�ó + ����             
					 *              SH  ���� ����:   8.930 L   
					 *              SH  ���� �ݾ�:  10,002 ��
					 *              POS ǥ�� ����:   8.930 L
					 *              POS ǥ�� �ݾ�:   9,108 �� (ODT�� �ö�� �ݾ��� KixxHub�� ���� �� POS ����)
					 *              
					 *         2. �Ϲݰ� + ����
					 *              SH  ���� ����:   8.930 L   
					 *              SH  ���� �ݾ�:  10,002 ��
					 *              POS ǥ�� ����:   8.930 L
					 *              POS ǥ�� �ݾ�:  10,002 �� 
					 *              
					 *   To-Be  
					 *         1. ���ݰŷ�ó + ����           
					 *              SH  ���� ����:   9.879 L   
					 *              SH  ���� �ݾ�:  10,076 ��
					 *              POS ǥ�� ����:   9.879 L
					 *              POS ǥ�� �ݾ�:  10,076 �� 
					 *              
					 *         2. �Ϲݰ� + ����
					 *              SH  ���� ����:   8.991 L   
					 *              SH  ���� �ݾ�:  10,069 ��
					 *              POS ǥ�� ����:   8.991 L 
					 *              POS ǥ�� �ݾ�:  10,069 ��
					 *   
					 * 
					 */
					switch (nozProtocolInt) {
					
						case IPumpConstant.PUMP_PROTOCOL_Recharge : {
							boolean isDiscount = false ;
							
							// �ŷ�ó�� ���� ���ε� �ܰ��� �ִ��� �����Ѵ�.
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
							
							// ���ε� �ܰ��� ���� ��� ���ΰ��� �����Ѵ�.
							if (!isDiscount) {
								salesBasePrice = nozBasePrice ;
							}
							
							// [2008.10.08] ȸ�� ������ : ������, ������, �Ӽ���, ����ö
							
							// �Ž��� ���� �ְų� Ȥ�� ���ε� �ܰ��� ��� ���͸� �����Ѵ�. �̴� ������ ���� ���� �����̴�.
							// 1. ODT �� ���� �ö���� ���ʹ� �������̸�, �ݾ��� ���� �ݾ�(�����ݾ��� �ƴ�) �̴�. �� ��� �������� ������ �ʿ䰡 �ִ�.
							// 2. �ŷ�ó �� ��� ���ܰ��� ���� �ݾ��� �̿��Ͽ� ������ �ʿ䰡 �ִ�.
			/*				if ((Double.parseDouble(PumpMathUtil.getPositiveValue(keep_no)) != 0 ) || isDiscount) {
								liter = Double.toString(PumpMathUtil.getPresetLiter(Double.parseDouble(amt), Double.parseDouble(salesBasePrice))) ;
								LogUtility.getLogger().debug("[Pump M] Liter �� �����մϴ�.liter="+liter) ;
							}
			*/				
							// [2008.10.24] ȸ�� ������ : ������ , ����ö
							// ���� �ŷ�ó�ε� �������� ������ ��� ���͸� �������� �ʰ�, �ݾ��� �����Ѵ�. -> [2008.10.08] ȸ�� ���� ���� 
							// �Ž������� ����.
							if (Double.parseDouble(GlobalUtility.getPositiveValue(keep_no)) != 0 ) {
								amt = GlobalUtility.getPositiveValue(GlobalUtility.substract(amt, keep_no)) ;
							}
							
							// ���� ���� �ŷ�ó�� ��� �������� �Ǹ� �ݾ� �� �ŷ�ó �Ǹ�ó�� ������ ���ؼ� �ݾ��� �����Ѵ�.
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
					// ��ǰ�� ���� (0022)
					LogUtility.getPumpMLogger().debug("[Pump M] SH ������ ��ǰ�� ���� ������ �ֽ��ϴ�.") ;
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
			if (uPosMsgCustomer != null) {			// �ܻ� ����
				uPosMsgCustomer.setLastPayment_yn("0") ;
				uPosMsgArray.add(uPosMsgCustomer) ;			
			}
			
			if (uPosMsgKeep != null) {				// ������ ����
				uPosMsgKeep.setLastPayment_yn("0") ;
				uPosMsgArray.add(uPosMsgKeep) ;			
			}
			
			if (uPosMsgCash != null) {				// ���� ����
				uPosMsgCash.setLastPayment_yn("0") ;
				uPosMsgArray.add(uPosMsgCash) ;
			}
			if (uPosMsgTicket != null) {			// ��ǰ�� ����
				uPosMsgTicket.setLastPayment_yn("0") ;
				uPosMsgArray.add(uPosMsgTicket) ;
			}
	
			LogUtility.getPumpMLogger().debug("[Pump M] ������ UPOSMessage ������ ������ �����ϴ�.") ;
			if ((uPosMsgArray != null) && (uPosMsgArray.size() > 0)) {
			for (int i = 0 ; i < uPosMsgArray.size() ; i++) {
				uPosMsgArray.get(i).print() ;
			}
		}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		LogUtility.getPumpMLogger().debug("[Pump M] ������ UPOSMessage ���� ��") ;
		
		return uPosMsgArray ;
	}
	
	/**
	 * ������ ī�� ���� ��û ������ UPOSMessage Class �� ��ȯ�Ѵ�. 
	 * ������κ����� ���� ��û ������ SB_WorkingMessage Class �� ���޵Ǹ�, Card_Type �� Mode �� ����
	 * ���ο� ���� �׿� �����ϴ� UPOSMessage Class �� ��ȯ�Ѵ�.
	 * 
	 * 	Card_type						Mode=0(����)		Mode=1(���)
	 * 		0 : �ſ�ī��	(+���ʽ�)			0031/0033		8031/8033
	 * 		1 : ���ڻ�ǰ�� (+���ʽ�)		0041/0043		8041/8043
	 * 		2 : ��������					0003			8003
	 * 		3 : GS ���ʽ� ī�� ���� ���	0061			8061
	 * 		4 : myLG ���� ���			0051			8051
	 * 		5 : ���� ������				0015			8015
	 * 
	 * @param workingMsg
	 * @param khproc_no			: KH ó����ȣ
	 * @return
	 */
	public static UPOSMessage createUPOSMessageFromWorkingMessage_Recharge(WorkingMessage workingMsg, String khproc_no) {
		LogUtility.getPumpMLogger().info("[Pump M] Convert WorkingMessage to UPOSMessage for Recharge.") ;
		UPOSMessage uPosMsg = null ;
		
		if (workingMsg instanceof TD_WorkingMessage) {
			TD_WorkingMessage tdWorkingMsg = (TD_WorkingMessage) workingMsg ;
			String nozzleNo = tdWorkingMsg.getNozzleNo() ;
			String bonRSCard_no = tdWorkingMsg.getBonusCardNo().trim() ;
			
//			int bonusCardType = 0 ;	// 0 : GS ���ʽ� ī�� , 1 = myLG���ʽ� ī�� (myLG ���ʽ� ī�� ����Ʈ ��ȸ�� ������ ����.) 
			// GS ���ʽ� ���� ��ȸ ��û �� ���
			// PI2 20160324 twlee ��ִ��� �ű����� �����Ͽ� ���ʽ� ������ȸ��  partner_code, store_cd �߰�
			// 042 : GS ���ʽ� ī�� , 077 = �ż��� ����Ʈ(���Ό��)
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
			int modeInt = Integer.parseInt(mode) ;	// 0=������� ,1=�������
			int cardTypeInt = Integer.parseInt(cardType) ;
			String card_number = sbWorkingMsg.getContent() ;
			String bonus_card = PumpMUtil.getRealBonusCardNumber(sbWorkingMsg.getBonusNumber()) ;
			String payPrice = GlobalUtility.getStringValue(sbWorkingMsg.getPrice()) ;
			String loyalty_password = null ;
			
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
					// �������
					switch (cardTypeInt) {
						case 0 : {
							// �ſ�ī�� (+���ʽ�) ����			
							boolean rlt = GlobalUtility.isNullOrEmptyString(bonus_card) ;
							String maskingCardNo = PumpMessageFormat.getPrintFormatCardNumber(card_number, true).replaceAll("-", "") ;
							
							if (rlt) {
								// �ſ� ���� ��û
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
								// �ſ�  + ���ʽ� ���� ��û
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
								
								uPosMsg = CustUtil.isTaxiDoubleSavePointYn(uPosMsg);
							}								
							break ;
						}
						case 1 : {
							// ���ڻ�ǰ�� (+���ʽ�) ����
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
										"2") ;
							}		
							break ;
						}		
						case 2 : {
							// �������� ����. ��� ����.
							break ;
						}
						case 3 : {
							// GS ���ʽ� ī�� ���� ��� ����
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
											//�������濡 ���� �׸� �߰� (2015.11.17)
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
						// 2012.07.09 ksm  my���� ����Ʈ ��� ����
						// ������ ������ ���ؿ� ��� CSR ��û ( 2012-07-06 )
						/*case 4 : {
							// myLG ���� ��� ����							
							bonus_card = PumpMUtil.getRealBonusCardNumber(card_number) ;
							LogUtility.getPumpMLogger().info("[Pump M] myLG Card Use Request -> UPOSMessage(0051)") ;
							UPOSMessage_ItemInfo itemInfo = UPOSUtil.getUPOSMessage_ItemInfo(nozzleNo, khproc_no,
									liter, basePrice, payPrice) ;
							uPosMsg = CreateUPOSMessage.createUPOSMessage_0051(IUPOSConstant.DEVICE_TYPE_3O, 
											khproc_no,
											nozzleNo, 
											PumpMODTSaleManager.getChargingPersonID(nozzleNo),
											itemInfo, 
											bonus_card,	// Check �ؾ� ��. 
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
							// ���� ������ ����. ��� ����.
							break ;
						}	
					}
					break ;
				}
				case 1 : {
					// �������
					switch (cardTypeInt) {
						case 0 : {
							// �ſ�ī�� (+���ʽ�) �������			
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
							// ���ڻ�ǰ�� (+���ʽ�) �������
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
							// �������� �������. ��� ����.		
							break ;
						}
						case 3 : {
							// GS ���ʽ� ī�� ���� ��� �������
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
						//	2012.07.09 ksm  my���� ����Ʈ ��� ����
						// ������ ������ ���ؿ� ��� CSR ��û ( 2012-07-06 )
						/*
						case 4 : {
							// myLG ���� ��� �������
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
							// ���� ������ �������.  ��� ����.
							break ;
						}	
					}			
					break ;
				}
			}
		} else if (workingMsg instanceof TJ_WorkingMessage) {
			// ���� ������ ��û
			// ���� ������ ��û�� ������ ODT �� �Ǹ� �Ϸ� ���� �߻��Ѵ�. ������ ������ ODT �� ���� �ö���� ��û �ݾ��� �̿��Ͽ� �����Ѵ�.
			LogUtility.getPumpMLogger().info("[Pump M] Cash Receipt Request -> UPOSMessage(0015)") ;
			
			TJ_WorkingMessage tjWorkingMsg = (TJ_WorkingMessage) workingMsg ;	
			
			String dealType 	= tjWorkingMsg.getDealType();			// �ŷ�����(0: �Һ��ڼҵ����, 1: �������������)
			String dealAmount 	= tjWorkingMsg.getDealAmount();			// �ŷ��ݾ�
//			String keyINType 	= tjWorkingMsg.getKeyINType();			// Ű�ι��
			String certiNumber 	= tjWorkingMsg.getCertiNumber();		// ������ȣ

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
			
			// ���� ������ ��û�� ī�� ���� �� ����Ʈ ����� ������ �ݾ��� ���� ��û�Ѵ�.
			// �� ������ �ö�Դٴ� ���� �����ݾ׿��� ī�� ���� �� ����Ʈ ����� ������ �ݾ��� �ִٴ� �ǹ��̴�.
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
			 * //  ���� ������ ������ ��� �� ���ΰ� ? ���̷� ����
			//	���ʽ�ī���ȣ (16) / �ֹι�ȣ (13)/ ����ڹ�ȣ (10) / �ڵ��� ��ȣ (10-12)
			int certiNumberLength = creditCard_no.length() ;
			String certiDest = null ;			// ����û �۽���ü	
			String certiSrcType = dealType ;	// �ŷ��� ����
			String certiNumberType = null ;		// Ȯ���� ����
			// 12�� 22�� �߿��� ����. ���ݿ�����ī��� ī���ȣ 18�ڸ�.
			if (certiNumberLength == 16 || certiNumberLength == 18) {
				// ���ʽ� ī���ȣ
				certiNumberType = "9" ;
				certiDest = "01" ;
			} else if (certiNumberLength == 13) {
				// �ֹι�ȣ
				certiNumberType = "1" ;
				certiDest = "02" ;
			} else if (certiNumberLength == 10) {
				// ����� ��ȣ
				certiNumberType = "2" ;
				certiDest = "02" ;
			} else {
				// �ڵ��� ��ȣ
				certiNumberType = "3" ;
				certiDest = "02" ;
			}
			//	 ����û �۽���ü(2byte)+{�ŷ��ڱ���(1byte)+Ȯ���� ����(1byte)}  
			loyality_type = certiDest + certiSrcType + certiNumberType ;*/
			//20160324 twlee ��ִ��� �����Ͽ�  ���ݿ����� ��û �ڵ� ����
			//	���ʽ�ī���ȣ (16) / �ֹι�ȣ (13)/ ����ڹ�ȣ (10) / �ڵ��� ��ȣ (10-12)
			//  ����û�۽���ü(01:GSC, 02:����Ʈ��) + �ŷ��ڱ���(0:�Һ���, 1:�����) + Ȯ���ڱ���(0:�ſ�ī���ȣ 1:�ֹε�Ϲ�ȣ 2:����ڵ�Ϲ�ȣ 3:��Ÿ 9:���ʽ�ī���ȣ)
			int certiNumberLength = creditCard_no.length() ;
			String certiDest = null ;			// ����û �۽���ü	
			String certiSrcType = dealType ;	// �ŷ��� ����
			String certiNumberType = null ;		// Ȯ���� ����
			if (certiNumberLength == 16 || certiNumberLength == 18) {
				// ���ʽ� ī���ȣ
				certiNumberType = "9" ;
				certiDest = "01" ;
				creditCardReading_type = IConstant.CreditCardReading_Type_MAGNETIC;
			} else if (certiNumberLength == 13) {
				// 20160323 twlee PI2  �ֹι�ȣ�� ���ݿ����� ó���� ����
				certiNumberType = "1" ;
				certiDest = "01" ;
				creditCardReading_type = IConstant.CreditCardReading_Type_Keyin;
			} else if (certiNumberLength == 10) {
				// ����� ��ȣ
				certiNumberType = "2" ;
				certiDest = "01" ;
				creditCardReading_type = IConstant.CreditCardReading_Type_Keyin;
			} else {
				// �ڵ��� ��ȣ
				certiNumberType = "3" ;
				certiDest = "01" ;
				creditCardReading_type = IConstant.CreditCardReading_Type_Keyin;
			}
			//	 ����û �۽���ü(2byte)+{�ŷ��ڱ���(1byte)+Ȯ���� ����(1byte)}  
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
															
															//�������濡 ���� �׸� �߰� (2015.11.17)
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
			// ���ʽ� ���� ��û 
			// ���ʽ� ���� ��û�� ������ ODT �� ���� �Ǹ� �Ϸ� ���� �ö�´�. ���� �̴� Pump_TR ���̺��� ������ ������ ��û�ϵ��� �Ѵ�.
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
															
//															�������濡 ���� �׸� �߰� (2015.11.17)
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
	 * ���� ��û�� ���� ���� ���� (UPOSMessage Object) �� �޾Ƽ� �̸� ������ ODT Spec �� �´� BB_WorkingMessage Object
	 * �� ��ȯ�Ѵ�.
	 * �Ʒ� ������ UPOSMessage Object �� Message Type �� ���� ���� �����̴�.
	 * 
	 * 	�ſ�ī�� ���� ����	= 0032
	 * 	�ſ�ī�� + ���ʽ� ���� ���� = 0034
	 * 	���ڻ�ǰ�� ���� ���� = 0042
	 * 	���ڻ�ǰ�� + ���ʽ� ���� ���� = 0044
	 * 	GS ���ʽ� ī�� ���� ��� ���� = 0062
	 * 	myLG ���� ��� ���� = 0052
	 * 	���� ������ ���� = 0016
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
	 * @param uPosMsg
	 * @return
	 */
	public static ArrayList<WorkingMessage> createWorkingMessageFromUPOSMessage_Recharge(UPOSMessage uPosMsg) {
		LogUtility.getPumpMLogger().info("[Pump M] Convert UPOSMessage[" + uPosMsg.getMessageType() 
				+ "] to WorkingMessage for Recharge.") ;
		
		ArrayList<WorkingMessage> workMsgArray = new ArrayList<WorkingMessage>() ;
		int messageType = Integer.parseInt(uPosMsg.getMessageType()) ;
		String cardType 		= "0";				// ī������ (0=�ſ�ī��, 1=���ڻ�ǰ��, 2=��������,
													// 3=GS���ʽ�ī�� �������,	4=myLG �������, 5=���ݿ�����
		
		// ������� ���� ���簡 �����ϸ�, ���� ���� ���� SH ������ ���� ������, ��� ���� ������ lastPayment_yn �� 0 ���� �����Ѵ�.
		uPosMsg.setLastPayment_yn("0") ;
		
		switch (messageType) {
			case IUPOSConstant.MESSAGETYPE_INT_0004 : 
			case IUPOSConstant.MESSAGETYPE_INT_0014 : {
				// ���ʽ� ���� ����
				BB_WorkingMessage bbWorkingMsg = null ;
				if (uPosMsg.getLoyaltyReqCode().equals("00000")) {
					// ������ ���
					bbWorkingMsg = getBB_WorkingMessage(uPosMsg , "0");
				} else {
					// ������ ���
					bbWorkingMsg = getBB_WorkingMessage(uPosMsg , "1");
				}
				workMsgArray.add(bbWorkingMsg) ;
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_8046 :
			case IUPOSConstant.MESSAGETYPE_INT_8032 : {
				// �ſ� ī�� ��� ��û ���� 
				LogUtility.getPumpMLogger().info("[Pump M] Credit Cancel Response(8032) -> WorkingMessage") ;
				cardType = "0" ;
				PI_WorkingMessage piWorkingMsg = getPI_WorkingMessage(uPosMsg , cardType, false, false) ;				
				workMsgArray.add(piWorkingMsg) ;				
				break ;				
			}
			case IUPOSConstant.MESSAGETYPE_INT_0046 :
			case IUPOSConstant.MESSAGETYPE_INT_0032 : { 
				// �ſ�ī�� ���� ����
				LogUtility.getPumpMLogger().info("[Pump M] Credit Response(0032) -> WorkingMessage") ;	
				cardType = "0" ;
				PI_WorkingMessage piWorkingMsg = getPI_WorkingMessage(uPosMsg , cardType, true, false) ;				
				workMsgArray.add(piWorkingMsg) ;
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_8048 :
			case IUPOSConstant.MESSAGETYPE_INT_8034 : {
				// �ſ�ī�� + ���ʽ� ��� ��û ���� 
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
				// �ſ�ī�� + ���ʽ� ���� ����
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
				// ���ڻ�ǰ�� ��� ��û ���� 
				LogUtility.getPumpMLogger().info("[Pump M] Electronic Ticket Cancel Response(8042) -> WorkingMessage") ;
				cardType = "1" ;
				PI_WorkingMessage piWorkingMsg = getPI_WorkingMessage(uPosMsg , cardType, true, false) ;
				workMsgArray.add(piWorkingMsg) ;	
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_0042 : { 
				// ���ڻ�ǰ�� ���� ����
				LogUtility.getPumpMLogger().info("[Pump M] Electronic Ticket Response(0042) -> WorkingMessage") ;
				cardType = "1" ;
				PI_WorkingMessage piWorkingMsg = getPI_WorkingMessage(uPosMsg , cardType, true, false) ;
				workMsgArray.add(piWorkingMsg) ;	
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_8044 : {
				// ���ڻ�ǰ�� + ���ʽ� ��� ��û ���� 
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
				// ���ڻ�ǰ�� + ���ʽ� ���� ����
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
				// GS ���ʽ� ī�� ���� ��� ��û ���� 
				LogUtility.getPumpMLogger().info("[Pump M] GS BonusCard Use Cancel Response(8062) -> WorkingMessage") ;
				cardType = "3" ;
				PI_WorkingMessage piWorkingMsg = getPI_WorkingMessage(uPosMsg , cardType, false, true) ;
				workMsgArray.add(piWorkingMsg) ;
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_0062 : { 
				// GS ���ʽ� ī�� ���� ��� ����
				LogUtility.getPumpMLogger().info("[Pump M] GS BonusCard Use Response(0062) -> WorkingMessage") ;
				cardType = "3" ;
				PI_WorkingMessage piWorkingMsg = getPI_WorkingMessage(uPosMsg , cardType, true, true) ;
				piWorkingMsg.setPrintContent(getGSPointUseMessage(uPosMsg, "0"));
				workMsgArray.add(piWorkingMsg) ;				
				break ;
			}
			//	2012.07.09 ksm  my���� ����Ʈ ��� ����
			// ������ ������ ���ؿ� ��� CSR ��û ( 2012-07-06 )
			/*
			case IUPOSConstant.MESSAGETYPE_INT_8052 : {
				// myLG ���� ��� ��û ���� 
				LogUtility.getPumpMLogger().info("[Pump M]  myLG Card Use Cancel Response(8052) -> WorkingMessage") ;
				cardType = "4" ;
				PI_WorkingMessage piWorkingMsg = getPI_WorkingMessage(uPosMsg , cardType, false, false) ;
				workMsgArray.add(piWorkingMsg) ;	
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_0052 : { 
				// myLG ���� ��� ����
				LogUtility.getPumpMLogger().info("[Pump M] myLG Card Use Response(0052) -> WorkingMessage") ;
				cardType = "4" ;
				PI_WorkingMessage piWorkingMsg = getPI_WorkingMessage(uPosMsg , cardType, true, false) ;
				workMsgArray.add(piWorkingMsg) ;	
				break ;
			}*/
			case IUPOSConstant.MESSAGETYPE_INT_0016 : { 
				// ���� ������ ����
				LogUtility.getPumpMLogger().info("[Pump M] Cash Receipt Response(0016) -> WorkingMessage") ;
				XA_WorkingMessage xaWorkingMsg = getXA_WorkingMessage(uPosMsg) ;
				workMsgArray.add(xaWorkingMsg) ;
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_0112 : {
				// ���ʽ� ī���� ����Ʈ ���� ��ȸ ����
				LogUtility.getPumpMLogger().info("[Pump M] Bonus Card Point inquiry Response(0112) -> WorkingMessage") ;
				CP_WorkingMessage cpWorkingMsg = getCP_WorkingMessage(uPosMsg) ;
				workMsgArray.add(cpWorkingMsg) ;
				break ;
			}
		}				
		return workMsgArray ;
	}

	/**
	 * ������ BB ���� ������ ��û�Ѵ�.
	 * 
	 * @param nozzle_no	: ���� ��ȣ
	 * @param successBP	: ���� ����
	 * @param odtContent	: �޽���
	 * @param bonusMsg	: ��� �޽���
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
	 * @param uPosMsg		: ���� ���� UPOS Message
	 * @param successBP		: ���ʽ� ī�� ���� ����
	 * @return
	 */
	private static BB_WorkingMessage getBB_WorkingMessage(UPOSMessage uPosMsg, String successBP) {
		String nozzle_no = uPosMsg.getNozzle_no() ;
		//String odtContent  = "" ;
		// PI2 20160325 twlee ��ִ������� odtScreenMsg�� �������� �ʾ� display_msg�� ����
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
//				2008.12.18 ������ ��� �ܰ�� �ٷ� �Ѿ�� ���� ���� ó�� - �߿��� -
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
	 * UPOSMessage �� ���ʽ� ī�� ����Ʈ ���� ��ȸ ���� ������ CP �������� �����մϴ�.
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
	 * UPOSMessage �� �ΰ��������� ��������ȣ�� �����´�.
	 * 
	 * @param credit_AuthInfo	: UPOSMessag �� �ΰ�����
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
	 * UPOSMessage �� �ΰ��������� ��ǥ ��ȣ�� �����´�.
	 * 
	 * @param credit_AuthInfo	: UPOSMessag �� �ΰ�����
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
	 * GS���ʽ�����Ʈ ��� ������ ���� �޼��� �� �μ⳻�� ������ ����� ����.
	 * @param posMsg 	: ���� ���� UPOS Message
	 * @param i			: ��������
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
	 * ������ ���� ������ �ִ��� �����ϰ� ������, ������ ���� ������ �����Ѵ�.
	 * 
	 * @param repInfo	: SH ������ ���� ���� ����Ʈ
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
	 * UPOSMessage Object �� led_code �� �̿��Ͽ�, ������ ODT �� ������ WorkingMessage �� mode �� ��û�Ѵ�.
	 * 
	 * @param led_code	: UPOSMessage �� led_code
	 * @param isAcceptedRequest
	 * 		true : ���� ��û�� ���� ����
	 * 		false : ���� ��� ��û�� ���� ����
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
	 * ���� ������ UPOSMessage Object �� PI_WorkingMessage Object �� ��ȯ�Ѵ�.
	 * 
	 * @param uPosMsg	: UPOS Message
	 * @param cardType	: Card Type
	 * @param isAcceptedRequest	: true (���� ��û�� ���� ����) , false(���� ��� ��û�� ���� ����)
	 * @param isGSBonusCard		: true (GS Bonus Card ��û�� ���� ����), false (GS Bonus Card �� ���� ����)
	 * @return
	 */
	private static PI_WorkingMessage getPI_WorkingMessage(UPOSMessage uPosMsg, String cardType,
			boolean isAcceptedRequest, boolean isGSBonusCard) {
		String mode 			= "";	// MODE (0�������,1�ź�,2��Ž���,3�ּҼ���,4��ҽ���)
		String recogDate 		= "";	// �����Ͻ�
		String recogNumber 		= "";	// ���ι�ȣ
		String cardNumber 		= ""; 	// ī���ȣ
		String cardCorpName 	= "";	// ī����
		String francNumber 		= ""; 	// ��������ȣ
		String noteCorpCode 	= "";	// ��ǥ���Ի��ڵ�
		String noteCorpName 	= "";	// ��ǥ���Ի��
		String terminalNubmer 	= "";	// �ܸ����ȣ
		String noteNumberTemp 	= "";	// ��ǥ��ȣ(������)
		String notice 			= "";	// NOTICE
		String noteNumber 		= "";	// ��ǥ��ȣ
		String recogConfid 		= "";	// �������ſ���ι�ȣ
		String printContent 	= "";	// �μ⳻�� 

		// GS Bonus Card ���� ���� �� ��ҿ�û ������ ��� ���ʽ� ���� �ʵ带 �����Ѵ�.
		// ������ myLG ���� ���� �� ��ҿ�û ������ �ſ�ī�� �� �����ϰ� ó���Ѵ�.
		if (!isGSBonusCard) {
			mode = getModeFromLedCode_Recharge(uPosMsg.getLed_code() , isAcceptedRequest) ;// MODE (0�������,1�ź�,2��Ž���,3��Ҽ���,4��ҽ���)
			recogDate 		= uPosMsg.getTrdate_creditCard() ;	// �����Ͻ�
			recogNumber 	= uPosMsg.getCredit_auth_no();		// ���ι�ȣ
			cardNumber 		= PumpMUtil.getCardNumberPre16Length(uPosMsg.getCreditCard_no()); 		// ī���ȣ
			cardCorpName 	= uPosMsg.getIssuer_name();			// ī����
			francNumber 	= getFrancNumberFromCredit_AuthInfo(uPosMsg.getCredit_authInfo()); 	// ��������ȣ
			noteCorpCode 	= uPosMsg.getAcquier_code();	// ��ǥ���Ի��ڵ�
			noteCorpName 	= uPosMsg.getAcquier_name();	// ��ǥ���Ի��
			terminalNubmer 	= uPosMsg.getTerm_id();			// �ܸ����ȣ
			noteNumberTemp 	= "";	
			// ��ǥ��ȣ(������)			
			// PI2 20160325 twlee ��ִ������� odtScreenMsg�� �������� �ʾ� display_msg�� ����
			notice 			= uPosMsg.getDisplay_msg() ;		
			noteNumber 		= "" ;
			recogConfid 	= "1";							// �������ſ���ι�ȣ
			printContent 	= uPosMsg.getVan_msg();			// �μ⳻�� 
		} else {
			mode = getModeFromLedCode_Recharge(uPosMsg.getLed_code() , isAcceptedRequest) ;// MODE (0�������,1�ź�,2��Ž���,3��Ҽ���,4��ҽ���)
			recogDate 		= uPosMsg.getTrdate_bonRSCard();	// �����Ͻ�
			recogNumber 	= uPosMsg.getBonRSCard_authNo();	// ���ι�ȣ
			cardNumber 		= PumpMUtil.getCardNumberPre16Length(uPosMsg.getBonRSCard_no()); 		// ī���ȣ
			cardCorpName 	= uPosMsg.getIssuer_name();			// ī����
			francNumber 	= getFrancNumberFromCredit_AuthInfo(uPosMsg.getCredit_authInfo()); 	// ��������ȣ
			noteCorpCode 	= uPosMsg.getAcquier_code();	// ��ǥ���Ի��ڵ�
			noteCorpName 	= uPosMsg.getAcquier_name();	// ��ǥ���Ի��
			terminalNubmer 	= uPosMsg.getTerm_id();							// �ܸ����ȣ
			noteNumberTemp 	= "";							// ��ǥ��ȣ(������)
			// PI2 20160325 twlee ��ִ������� odtScreenMsg�� �������� �ʾ� display_msg�� ����
			notice 			= uPosMsg.getDisplay_msg() ;
			noteNumber 		= "" ;
			recogConfid 	= "1";							// �������ſ���ι�ȣ
			printContent 	= uPosMsg.getBonRS_msg();			// �μ⳻�� 
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
	 * ���� ������ ��û�� ���� ���� ������ XA_WorkingMessage �� ��ȯ�մϴ�
	 * 
	 * @param posMsg	: ���� ���� ����
	 * @return
	 */
	private static XA_WorkingMessage getXA_WorkingMessage(UPOSMessage posMsg) {
		String nozzle_no = posMsg.getNozzle_no() ;
		String successBP = "" ;		
		String odtContent = "" ;
		String printContent = "" ;
		
		String ledCode = posMsg.getLed_code() ;
		if (ledCode.equals(IUPOSConstant.LEDCODE_1)) {
			// ������ ���
			successBP = "0" ;
			printContent = posMsg.getVan_msg() ;
		} else {
			// ������ ���
			successBP = "1" ;
			printContent = posMsg.getVan_msg() ;
		}

		// PI2 20160325 twlee ��ִ������� odtScreenMsg�� �������� �ʾ� display_msg�� ����
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
				"����(�ҵ����)",
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
				"ī���ȣ ����",
				"0",
				GlobalUtility.appendingSPACEEnd(null,10),
				GlobalUtility.appendingSPACEEnd(null,5),
				"ī���ȣ ����") ;		
		
		
		Preamble pumpPreamble = PumpMUtil.createWorkingMessagePreamble(null,
				SyncManager.DISE_PUMP_ADAPTER, 
				piWorkingMsg , 
				"") ;

		LogUtility.getPumpMLogger().debug("ī���ȣ�� ª�� ���� ���ν��� �޼����� ODT�� ������.");
		
		return pumpPreamble ;
    }
	
	/**
     * 2009.5.27 �߿��� �ۼ�
     * 
     * �ڿ��� �´� ������ ��ȣ ü��� ������ ��ȣ�� �ٲ��ش�.
     * 
     * ���� : yymmdd + 1 + 001
     * �ڿ� : yddmm + '-' + 1 + '-' + 00001
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
	 * ������ ODT ���� �Ǹŵ����͸� �̿��Ͽ� �ŷ�ó �� �Ǹ� / ���� �ǸŰ� �Ǿ����� ���θ� Ȯ���Ѵ�.
	 * 
	 * @param shWorkMsg		: ������ ODT ���� �Ǹŵ����� ����
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
	 * 0001 ���� ���� ���θ� �����Ѵ�.
	 * 
	 * @param nozzle_no	: ���� ��ȣ
	 * @param khproc_no		: KH ó����ȣ
	 * @param workMsg	: �Ǹ� �Ϸ� ���� (SH_WorkingMessage)
	 * @return
	 */
	public static boolean shouldSend0001UPOSMessageToSaleM(String nozzle_no, String khproc_no, WorkingMessage workMsg) {
		boolean rlt = true ;

		return rlt ;
	}
    
    /**
	 * UPOSMessage ���� ������ Sale M ���� �������� �Ǵ��Ѵ�.
	 * �����⿡�� ���ʽ� ���� ���� �� ���� ������ ������ �Ǹ� �Ϸ� ���� �Ͼ�� ���̱� ������, �� ������ POS �� �������� �ʴ´�.
	 * 
	 * @param uPosMsg	: UPOSMessage ���� ����
	 * @return
	 */
    public static boolean shouldSendToSaleM(UPOSMessage uPosMsg) {
    	boolean rlt = true ;

    	try {
    		int messageTypeInt = Integer.parseInt(uPosMsg.getMessageType()) ;
    		switch (messageTypeInt) {
//    			case IUPOSConstant.MESSAGETYPE_INT_0004 :	// ���ʽ����� ���� -> �������� ����
    			case IUPOSConstant.MESSAGETYPE_INT_0112 :	// GS���ʽ�������ȸ ����
//    			case IUPOSConstant.MESSAGETYPE_INT_0014 :	// ���ݺ��ʽ� ���� -> �������� ����
    			case IUPOSConstant.MESSAGETYPE_INT_0016 :	// ����û���ݿ����� ����
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
