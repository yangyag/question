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
 * 	�پ��� ����� ���� ���μ����� ������ ���� ó���ȴ�.
 * 		0) ���� ����
 * 			(1) ���� ���ν�������, HE_WorkingMessage �� �׿� �����ϴ� UPOSMessage ���� ������ ������.
 * 			(2) �����Ϸ� ���� �̸� ���� (0�� ���� �� ���������� BL ����) �� ����
 * 				����� �޾ƾ� �� ������, �����Ϸ� (�ݾ�, Liter) �� ���� HE ������ �̿��Ͽ� ����
 * 				����ؾ� �� ������, ���� UPOSMessage �� �̿��Ͽ� ����
 * 			(3) ����� Ȥ�� ��� ���� ��û ����, �ٸ� ������ pending ������ ���� (0�� ����, ���� ������ BL üũ�ÿ��� pending ������ �������� ����)
 * 			(4) CAT M ���� ���� ���� ���� ����, pending ���� �����, �� ������ �״�� CAT M ���� �������ϰ�, pending ������ null �� ó����.
 * 			(5) ��� ���� �Ϸ��, QL (������) ������ �����Ͽ� Pump A �� ����
 * 	
 * 		1) ������
 * 			(1) HE_WorkingMessage -> uPOS Message �� ��ȯ
 * 			(2) HE_WorkingMessage (heOrg) �� ���� (���� ������� ���ؼ� �ӽ÷� ����)
 * 			(3) uPOS (respondUPOS) ���� ���� ���� �� ����
 * 			(4) uPOS ���� ������ QM_WorkingMessage �� ��ȯ�Ͽ� Pump A �� ��
 * 		2) �����Ϸ� ����
 * 			(1) 0�� ������ ��� : respondUPOS �� �̿��Ͽ� ��� uPOS Message �� �����Ͽ� CAT M ���� ����
 * 			(2) �̸� ������ ��� : 
 * 				a. heOrg �� �̿��Ͽ� ���� UPOSMessage �� ����
 * 				b. respondUPOS �� �̿��Ͽ� ��� UPOSMessage �� �����Ͽ� pendingUPOS ������ ����
 * 				c. ���� UPOSMessage �� CAT M �� ����
 * 				d. CAT M ���� ���� ���� UPOSMessage �� �����ϸ�, pendingUPOS ������ �ִ��� Ȯ���ϰ�, ������ CAT M �� ������
 * 			(3) �Ϲ� ������ ��� : ������ (QL) ���� �����Ͽ� Pump A �� ����
 * 
 *
 */
public class ODTUtility_DaSNo {


	/**
	 * BL üũ ������ �����Ѵ�.
	 * 
	 * @param workingMsg	: ������ Adapter �� ���� �ö�� HE ����
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

//		������ ������ ���� UposMessage �߰�, 2015.11.19 - cwi 
		String creditCardReading_type = IConstant.CreditCardReading_Type_MAGNETIC ;
		String chipData = "";
		String certification_id = "";
		String signImage_Info = "";
		String signImage_Data = "";
		String term_ID = ODTUtility_Common.getTermId() ;
		String store_CD = ODTUtility_Common.getStoreCode();
		String encryptCredit_no = "";
		String creditPassCode = "";
		String selfPayment_type = "" ; // 2016.03.31 WooChul Jung. Ȯ�� �ʿ�. ��� �������� Setting �� ������.
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
	 * ���� + ���ʽ�
	 * @param itemInfo
	 * @param uPosMsg
	 * @param diffPrice
	 * @param isAccepted
	 * @return
	 */
	private static String createCachWithBonusTatsunoFormat(UPOSMessage_ItemInfo itemInfo, UPOSMessage uPosMsg, int diffPrice, boolean isAccepted) {
		String formatMsg = "���� ������ �޾�����, ���� ������ ������ ���ܼ� Print ������ �����ϴ�." ;	
		
		String productName1			= "";	// ��ǰ��
		String storeCode			= "";	// �����ڵ� (������� ����)
		String date					= "";	// ��¥ (YYYYMMDDHHMMSS)
		String tel					= "";	// ������ȭ��ȣ
		String manager				= "";	// �����
		String wDate				= "";	// ��������
		String odtNo				= "";	// ODT ��ȣ
		String transactionNo		= "";	// �ŷ���ȣ
		String storeName			= "";	// �����
		String representative		= "";	// ��ǥ��
		String represent			= "";	// ����ڹ�ȣ
		String address				= "";	// ���� �ּ�
		String sequence				= "";	// ��������
		String productName2			= "";	// ��ǰ��
		String nozzleNo				= "";	// ���� ��ȣ
		String liter				= "";	// ������
		String basePrice			= "";	// �ܰ�
		String price				= "";	// �����ݾ�
		String productPrice			= "";	// ���� ��ǰ����
		String taxPrice				= "";	// �ΰ���
		String totalPrice			= "";	// �հ�
		String receivePrice			= "";	// ���� �ݾ�
	 	String refund				= "";	// ȯ�� �ݾ� 
		String bonusCardNo			= "";	// ���ʽ�ī�� ��ȣ
		String authNo				= "";	// ���ʽ�ī�� ���� ��ȣ
		String createPoint			= "";	// �߻� ����Ʈ
		String usePoint				= "";	// ���� ����Ʈ
		String totalPoint			= "";	// ������ ����Ʈ
		String message				= "";	// ���ʽ�ī�� �޼���
		int option 					= 0;	// �������� Ÿ��
		
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
					storeCode = T_KH_STOREHandler.getHandler().getStoreCode();		//�����ڵ�
					T_KH_STOREData storeData = 
						T_KH_STOREHandler.getHandler().getT_KH_STOREDataByStoreCode(storeCode) ;
					
					productName1 = productData.getGoods_name() ;			// ��ǰ��
					date = getWDateFormat() ;								// ��¥
					tel = "(" + storeData.getTel_nbr() + ")" ;				// ������ȭ��ȣ
					
					manager = storeData.getRep_name() ;						// �����
					wDate = getWorkingDateFormat(storeData.getBizhour_date()) ;			// ��������
					odtNo = PumpMUtil.getODTNumberFromNozzleNo(uPosMsg.getNozzle_no()) ;	//odt��ȣ					
					transactionNo = uPosMsg.getPosReceipt_no() ;	// �ŷ���ȣ
					storeName = storeData.getCust_name_disp() ;		// �����
					representative = storeData.getRep_name() ;		// ��ǥ��
					represent = storeData.getBizregno_nbr() ;		// ����ڹ�ȣ
					address = storeData.getBizstoreaddr_txt1()  + " " + storeData.getBizstoreaddr_txt2();	// �����ּ�					
					
					POSHeader posMsg = PumpMODTSaleManager.getCustPOSPumpM(itemInfo_item.getNozzleNo()) ;
					POS_DW dwPumpM = null ;
					if ((posMsg != null) && (posMsg instanceof POS_DW))
						dwPumpM = (POS_DW) posMsg ;					
					
					sequence = "01" ;		// ����
					productName2 = productData.getGoods_name() ;	// ��ǰ��
					nozzleNo = uPosMsg.getNozzle_no() ;				// �����ȣ
					liter = PumpMUtil.convertNumberFormatFromPumpToPOS(itemInfo.getTotalOilAmount(),3) ;	// ������
					
					if (posMsg == null) {
						basePrice = PumpMUtil.convertNumberFormatFromPumpToPOS(itemInfo.getUnitPrice(),3) ;	// �ܰ�
					} else {
						if ("1".equals(dwPumpM.getRcptunitprc_ind_prt()) )	//�ܰ� ��� ����
							basePrice = PumpMUtil.convertNumberFormatFromPumpToPOS(itemInfo.getUnitPrice(),3) ;	// �ܰ�
					}
					
					price = itemInfo.getTotalOilPrice_after_discount() ;				// �����ݾ�
					productPrice = itemInfo.getTotalPrice_tax() ;						// ���ް���
					taxPrice = itemInfo.getTotalTaxPrice() ;							// ����
					totalPrice = itemInfo.getTotalOilPrice_after_discount() ;			// ���� �ݾ�
					
					HE_WorkingMessage heWorkingMsg = DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzleNo).getHeWorkingMsg();
					
					receivePrice		= heWorkingMsg.getPrice();	// ���� �ݾ�
					
					if (!PumpMUtil.shouldSendingRejectAndReApproval(receivePrice, totalPrice, false)) 
						refund				= String.valueOf(Double.parseDouble(receivePrice) - Double.parseDouble(totalPrice));	// ȯ�� �ݾ�
					else
						refund				= "0" ;	// ȯ�� �ݾ�
					
					bonusCardNo		= uPosMsg.getBonRSCard_no();	// ���ʽ�ī�� ��ȣ
					authNo				= uPosMsg.getBonRSCard_authNo();	// ���ʽ�ī�� ���� ��ȣ
					createPoint			= uPosMsg.getGs_point1();	// �߻� ����Ʈ
					usePoint			= uPosMsg.getGs_point2();	// ���� ����Ʈ
					totalPoint			= "0";	// ������ ����Ʈ
					message			= uPosMsg.getBonRS_msg();	// ���ʽ�ī�� �޼���
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
	 * ��� UPOSMessage �� �����Ѵ�. �̴� ���� �ֱ��� ���� ������ �̿��Ͽ� ��� ������ �����Ѵ�.
	 * 
	 * @param heWorkingMsg		: HE ���� (Pump A �� ������ ���� ��û����)
	 * @param khproc_no			: KH ó����ȣ
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
//					 tatsuno_hs okdhp7 (2012.12) - GS����Ʈ ���� ��� 
					case IUPOSConstant.MESSAGETYPE_INT_0062 : {	
						
						String payPrice = GlobalUtility.getPositiveValue(heWorkingMsg.getPrice()); 
						
						//---- (GS����Ʈ ��� ��� ��û)
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
	 * ���ݰŷ�ó ���� or ��ǥ�ŷ�ó ���ݿ�����
	 */
	private static String createCustomerTatsunoFormat(UPOSMessage_ItemInfo itemInfo, UPOSMessage uPosMsg, int diffPrice, boolean isAccepted) {
		String formatMsg = "���� ������ �޾�����, ���� ������ ������ ���ܼ� Print ������ �����ϴ�." ;	
		
		String productName1					= "";	// ��ǰ��
		String storeCode					= "";	// �����ڵ� (������� ����)
		String date							= "";	// ��¥ (YYYYMMDDHHMMSS)
		String tel							= "";	// ������ȭ��ȣ
		String manager						= "";	// �����
		String wDate						= "";	// ��������
		String odtNo						= "";	// ODT ��ȣ
		String transactionNo				= "";	// �ŷ���ȣ
		String storeName					= "";	// �����
		String representative				= "";	// ��ǥ��
		String represent					= "";	// ����ڹ�ȣ
		String address						= "";	// ���� �ּ�
		String customerCode					= "";	// �ŷ�ó �ڵ�
		String customerName 				= "";	// �ŷ�ó��
		String customerCardNo 				= "";	// �ŷ�ó ī���ȣ
		String customerCarNo 				= "";	// �ŷ�ó ���� ��ȣ 
		String limit 						= "";	// �ѵ���
		String saveLimit 					= "";	// ���� ��뷮
		String remainLimit 					= "";	// �ѵ� �ܷ�
		String cardNumber   				= "";	// �ſ�ī���ȣ
		String acceptNo     				= "";	// �ſ�ī�� ���ι�ȣ
		String cardCorpName 				= "";	// �ſ�ī�� ����ȸ��
		String sequence						= "";	// ��������
		String productName2					= "";	// ��ǰ��
		String nozzleNo						= "";	// ���� ��ȣ
		String liter						= "";	// ������
		String basePrice					= "";	// �ܰ�
		String price						= "";	// �����ݾ�
		String productPrice					= "";	// ���� ��ǰ����
		String taxPrice						= "";	// �ΰ���
		String totalPrice					= "";	// �հ�
		String receivePrice					= "";	// ���� �ݾ�
	 	String refund						= "";	// ȯ�� �ݾ� 
		String bonusCardNo					= "";	// ���ʽ�ī�� ��ȣ
		String authNo						= "";	// ���ʽ�ī�� ���� ��ȣ
		String createPoint					= "";	// �߻� ����Ʈ
		String usePoint						= "";	// ���� ����Ʈ
		String totalPoint					= "";	// ������ ����Ʈ
		String message						= "";	// ���ʽ�ī�� �޼���
		String titleMessage	 				= "";	// ���ݿ����� �ŷ����� // tatsuno_hs okdhp7 (2013.03) �߰�
		String cachReceiptNo	 			= "";	// ���ݿ����� ������ȣ
		String cachReceiptAuthNo 			= "";	// ���ݿ����� ���ι�ȣ
		String cachReceiptMessage 			= "";	// ���ݿ����� �޼���
		int option = 0; 	// �������� Ÿ��

		boolean isCreditCard = false;		// �ſ�ī�� ����

		// 2013.05.20 ksm �Һΰ�����
		String monthCnt = "00";
//		 2011.09.07 ksm ������ �ݾ� ���� �߰�
		String preReceivePrice = "0";	// �����αݾ�
		
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
					storeCode = T_KH_STOREHandler.getHandler().getStoreCode();		//�����ڵ�
					T_KH_STOREData storeData = 
						T_KH_STOREHandler.getHandler().getT_KH_STOREDataByStoreCode(storeCode) ;
					
					productName1 = productData.getGoods_name() ;			// ��ǰ��
					date = getWDateFormat() ;													// ��¥
					tel = "(" + storeData.getTel_nbr() + ")" ;								// ������ȭ��ȣ
					
					manager = storeData.getRep_name() ;								// �����
					wDate = getWorkingDateFormat(storeData.getBizhour_date()) ;			// ��������
					odtNo = PumpMUtil.getODTNumberFromNozzleNo(uPosMsg.getNozzle_no()) ;	//odt��ȣ					
					transactionNo = uPosMsg.getPosReceipt_no() ;				// �ŷ���ȣ
					storeName = storeData.getCust_name_disp() ;					// �����
					representative = storeData.getRep_name() ;						// ��ǥ��
					represent = storeData.getBizregno_nbr() ;							// ����ڹ�ȣ
					address = storeData.getBizstoreaddr_txt1()  + " " + storeData.getBizstoreaddr_txt2();	// �����ּ�					
					
					POSHeader posMsg = PumpMODTSaleManager.getCustPOSPumpM(itemInfo_item.getNozzleNo()) ;
					POS_DW dwPumpM = null ;
					
					if ((posMsg != null) && (posMsg instanceof POS_DW))
						dwPumpM = (POS_DW) posMsg ;
					
					if (posMsg == null) {						
						customerCode = "";			// �ŷ�ó�ڵ�
						customerName = "";			// �ŷ�ó ��
						customerCardNo = "";		// �ŷ�ó ī���ȣ
						customerCarNo = "";			// �ŷ�ó ���� ��ȣ
					} else {
						customerCode 		= dwPumpM.getCust_code();			// �ŷ�ó�ڵ�
						customerName 		= dwPumpM.getDrive_name();		// �ŷ�ó ��
						customerCardNo 	= dwPumpM.getCust_card_no();	// �ŷ�ó ī���ȣ
						customerCarNo 		= dwPumpM.getCar_no();				// �ŷ�ó ���� ��ȣ
					}
					
					limit = "0";									// �ѵ���
					saveLimit = "0";							// ���� ��뷮
					remainLimit = "0";						// �ѵ� �ܷ�
					
					if (IUPOSConstant.MESSAGETYPE_0032.equals(uPosMsg.getMessageType()) ||
							IUPOSConstant.MESSAGETYPE_0034.equals(uPosMsg.getMessageType())){
						cardNumber = uPosMsg.getCreditCard_no() ;	// �ſ�ī�� ī���ȣ 
						acceptNo = uPosMsg.getCredit_auth_no() ;	// �ſ�ī�� ���ι�ȣ
						cardCorpName = uPosMsg.getAcquier_code() + " " + uPosMsg.getAcquier_name() ;	//�ſ�ī�� ����ȸ�� 	
						isCreditCard = true;						//�ſ�ī�� ����
						monthCnt = uPosMsg.getCredit_month();
					}
					
					sequence = "01" ;		// ����
					productName2 	= productData.getGoods_name() ;		// ��ǰ��
					nozzleNo 			= uPosMsg.getNozzle_no() ;				// �����ȣ
					liter = PumpMUtil.convertNumberFormatFromPumpToPOS(itemInfo.getTotalOilAmount(),3) ;	// ������
					
					if (posMsg == null) {
						basePrice = PumpMUtil.convertNumberFormatFromPumpToPOS(itemInfo.getUnitPrice(),3) ;	// �ܰ�
					} else {
						if ("1".equals(dwPumpM.getRcptunitprc_ind_prt()) )	//�ܰ� ��� ����
							basePrice = PumpMUtil.convertNumberFormatFromPumpToPOS(itemInfo.getUnitPrice(),3) ;	// �ܰ�
						else
							basePrice = "0";
					}
					
					price 				= itemInfo.getTotalOilPrice_after_discount() ;	// �����ݾ�
					productPrice 	= itemInfo.getTotalPrice_tax() ;							// ���ް���
					taxPrice 			= itemInfo.getTotalTaxPrice() ;							// ����
					totalPrice 		= itemInfo.getTotalOilPrice_after_discount() ;	// ���� �ݾ�
					
					receivePrice = uPosMsg.getItem_info().getTotalOilPrice_after_discount();	// ���� �ݾ�
					
					if (!PumpMUtil.shouldSendingRejectAndReApproval(receivePrice, totalPrice, isCreditCard) )
						refund	= String.valueOf(diffPrice);	// ȯ�� �ݾ�
					else
						refund = "0";
					
					if (IUPOSConstant.MESSAGETYPE_0054.equals(uPosMsg.getMessageType()) ||
							IUPOSConstant.MESSAGETYPE_0012.equals(uPosMsg.getMessageType()) ||
		 
							//2012.10.09 ksm ���ݰŷ�ó ���ʽ� ������ �ݾ�ǥ�� �̻�
							IUPOSConstant.MESSAGETYPE_0014.equals(uPosMsg.getMessageType()) ||	
							IUPOSConstant.MESSAGETYPE_0004.equals(uPosMsg.getMessageType()) ||
							IUPOSConstant.MESSAGETYPE_0016.equals(uPosMsg.getMessageType())) {
						
						HE_WorkingMessage heWrkMsg = DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzleNo).getHeWorkingMsg();
						
						// 2012.10.26 ksm ���ݰŷ�ó �Ž����� ó��
						//receivePrice = String.valueOf(Double.parseDouble(heWrkMsg.getPrice()));
						receivePrice = String.valueOf(Double.parseDouble(heWrkMsg.getCashCount()));
						
						refund = String.valueOf(Double.parseDouble(receivePrice) - Double.parseDouble(totalPrice));
					}
					
					bonusCardNo		= uPosMsg.getBonRSCard_no();				// ���ʽ�ī�� ��ȣ
					authNo				= uPosMsg.getBonRSCard_authNo();		// ���ʽ�ī�� ���� ��ȣ
					createPoint			= uPosMsg.getGs_point1();						// �߻� ����Ʈ
					usePoint				= uPosMsg.getGs_point2();						// ���� ����Ʈ
					totalPoint			= "0";															// ������ ����Ʈ
					message				= uPosMsg.getBonRS_msg();					// ���ʽ�ī�� �޼���
					option 				= DasNoSelfPumpingManager.getInstance().getCurrentOption(nozzleNo) ;

					//2011.09.08 ksm �����αݾ� ����.
					if(isCreditCard){
						preReceivePrice = getPreReceivePrice(nozzleNo, transactionNo, price);
					}
										
					if (IUPOSConstant.MESSAGETYPE_0016.equals(uPosMsg.getMessageType()) ||
							IUPOSConstant.MESSAGETYPE_0054.equals(uPosMsg.getMessageType())){
						titleMessage	 			= uPosMsg.getTitle_msg();		// ���ݿ����� �ŷ����� // tatsuno_hs okdhp7 (2012.12) ����
						cachReceiptNo	 			= uPosMsg.getCreditCard_no();	// ���ݿ����� ������ȣ
						cachReceiptAuthNo 		= uPosMsg.getCredit_auth_no();	// ���ݿ����� ���ι�ȣ
						cachReceiptMessage 	= uPosMsg.getVan_msg();				// ���ݿ����� �޼���
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
	 * VAN ��� ���� ������ 
	 * 
	 * by ����ȣ
	 * 
	 * @return
	 */
	public static QL_WorkingMessage createFailWhenReject(String nozzleNo, UPOSMessage uPosMsg) {
		LogUtility.getPumpMLogger().info("[Pump M] Create Fail Receipt Message for DasNo") ;
		String receipt = "" ;
		String length = "0" ;
		String odtNo = PumpMUtil.getODTNumberFromNozzleNo(nozzleNo) ;	
		String qlMode = "";
		String alarmState = "0"; 	// ����ODT���� ����� ���н� �����޽��� ����� ���� �߰� - �ּ��� 2010.03.17
		
		try {
			DasNoODTNozzleInfo nozInfo = DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzleNo) ;
			int nozInfoOptionInt = nozInfo.getOption() ;
			int optionState = nozInfo.getOptionState() ;
			String date = "" ;
			date = getWDateFormat();
			
			LogUtility.getPumpMLogger().debug("[Pump M] nozInfoOptionInt : " + nozInfoOptionInt); // �׽�Ʈ �α�
			LogUtility.getPumpMLogger().debug("[Pump M] optionState : " + optionState); // �׽�Ʈ �α�
			
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
																			//PI2 20160325 twlee uPosMsg.getOdtScreenMsg()	=> uPosMsg.getDisplay_msg()���� 
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
									//09.02.09 ���н� getOdtScreenMsg �� �޼����� ���� ������ ���� ����, getBonRS_msg ��ġ�� getVan_msg �ӽ÷� ���
									//	uPosMsg.getVan_msg(), 
									//	uPosMsg.getBonRS_msg(),
									//PI2 20160325 twlee ��ִ������� odtScreenMsg�� �������� �ʾ� display_msg�� ����
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
				case IConstant.FULL_PUMPING_OPTION_9 :{ //2016-04 �̰�ȣ
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
									//PI2 20160325 twlee uPosMsg.getOdtScreenMsg()	=> uPosMsg.getDisplay_msg()���� 
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
									//	09.02.09 ���н� getOdtScreenMsg �� �޼����� ���� ������ ���� ����, getBonRS_msg ��ġ�� getVan_msg �ӽ÷� ���
									//	uPosMsg.getVan_msg(), 
									//	uPosMsg.getBonRS_msg(),
									//PI2 20160325 twlee uPosMsg.getOdtScreenMsg()	=> uPosMsg.getDisplay_msg()���� 
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
									//	09.02.09 ���н� getOdtScreenMsg �� �޼����� ���� ������ ���� ����, getBonRS_msg ��ġ�� getVan_msg �ӽ÷� ���
									//	uPosMsg.getVan_msg(), 
									//	uPosMsg.getBonRS_msg(),
									//PI2 20160325 twlee uPosMsg.getOdtScreenMsg()	=> uPosMsg.getDisplay_msg()���� 
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
									//PI2 20160325 twlee uPosMsg.getOdtScreenMsg()	=> uPosMsg.getDisplay_msg()���� 
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
									//	09.02.09 ���н� getOdtScreenMsg �� �޼����� ���� ������ ���� ����, getBonRS_msg ��ġ�� getVan_msg �ӽ÷� ���
									//	uPosMsg.getVan_msg(), 
									//	uPosMsg.getBonRS_msg(),
									//PI2 20160325 twlee uPosMsg.getOdtScreenMsg()	=> uPosMsg.getDisplay_msg()���� 
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
									//	09.02.09 ���н� getOdtScreenMsg �� �޼����� ���� ������ ���� ����, getBonRS_msg ��ġ�� getVan_msg �ӽ÷� ���
									//	uPosMsg.getVan_msg(), 
									//	uPosMsg.getBonRS_msg(),
									//PI2 20160325 twlee uPosMsg.getOdtScreenMsg()	=> uPosMsg.getDisplay_msg()���� 
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
									//PI2 20160325 twlee uPosMsg.getOdtScreenMsg()	=> uPosMsg.getDisplay_msg()���� 
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
									//	09.02.09 ���н� getOdtScreenMsg �� �޼����� ���� ������ ���� ����, getBonRS_msg ��ġ�� getVan_msg �ӽ÷� ���
									//	uPosMsg.getVan_msg(), 
									//	uPosMsg.getBonRS_msg(),
									//PI2 20160325 twlee uPosMsg.getOdtScreenMsg()	=> uPosMsg.getDisplay_msg()���� 
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
									//PI2 20160325 twlee uPosMsg.getOdtScreenMsg()	=> uPosMsg.getDisplay_msg()���� 
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
									//	09.02.09 ���н� getOdtScreenMsg �� �޼����� ���� ������ ���� ����, getBonRS_msg ��ġ�� getVan_msg �ӽ÷� ���
									//	uPosMsg.getVan_msg(), 
									//	uPosMsg.getBonRS_msg(),
									//PI2 20160325 twlee uPosMsg.getOdtScreenMsg()	=> uPosMsg.getDisplay_msg()���� 
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
									//	09.02.09 ���н� getOdtScreenMsg �� �޼����� ���� ������ ���� ����, getBonRS_msg ��ġ�� getVan_msg �ӽ÷� ���
									//	uPosMsg.getVan_msg(), 
									//	uPosMsg.getBonRS_msg(),
									//PI2 20160325 twlee uPosMsg.getOdtScreenMsg()	=> uPosMsg.getDisplay_msg()���� 
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
									//PI2 20160325 twlee uPosMsg.getOdtScreenMsg()	=> uPosMsg.getDisplay_msg()���� 
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
									//	09.02.09 ���н� getOdtScreenMsg �� �޼����� ���� ������ ���� ����, getBonRS_msg ��ġ�� getVan_msg �ӽ÷� ���
									//	uPosMsg.getVan_msg(), 
									//	uPosMsg.getBonRS_msg(),
									//PI2 20160325 twlee uPosMsg.getOdtScreenMsg()	=> uPosMsg.getDisplay_msg()���� 
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
									//PI2 20160325 twlee uPosMsg.getOdtScreenMsg()	=> uPosMsg.getDisplay_msg()���� 
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
	 * VAN ��� ���� TimeOut �߻���
	 * 
	 * by ����ȣ
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

	// tatsuno_hs okdhp7 (2012.12) �߰�
	/**
	 * �����Ϸ� �ݾ��� 0 ���̾ ������ ��� ���� ������ Format
	 * 
	 * @param itemInfo		: �����Ϸ� �ڷ�
	 * @param posMsg		: ������ ��� ���� ����
	 * @param refund		: ȯ�� �ݾ�				
	 * @param isAccepted	: ���� or ���� ����
	 * @return
	 */
	private static String createReceiptBonusPointWhenPumping0(UPOSMessage_ItemInfo pumpingItemInfo, 
			UPOSMessage uPosMsg, int refund, boolean isAccepted) {

		String formatMsg = "���� ������ �޾�����, ���� ������ ������ ���ܼ� Print ������ �����ϴ�." ;		
		
		String productName1 		= "" ;		// ��ǰ��
		String storeCode 			= "" ;		// �����ڵ�
		String date 				= "" ;		// ��¥(YYYYMMDDHHMMSS)
		String tel 					= "" ;		// ������ȭ��ȣ
		String manager 				= "" ;		// �����
		String wDate				= "" ;		// ��������
		String odtNo 				= "" ;		// ODT ��ȣ
		String transactionNo 		= "" ;		// �ŷ���ȣ
		String storeName 			= "" ;		// �����
		String representative 		= "" ;		// ��ǥ��
		String represent 			= "" ;		// ����ڹ�ȣ
		String address 				= "" ;		// �����ּ�
		String cardNumber 			= "" ;		// ī���ȣ 
		String acceptNo 			= "" ;		// ���ι�ȣ 
		String cardCorpName 		= "" ;		// ����ȸ�� 
		String sequence 			= "" ;		// ����
		String productName2 		= "" ;		// ��ǰ��
		String nozzleNo 			= "" ;		// �����ȣ
		String liter 				= "0" ;		// ������
		String basePrice 			= "" ;		// �ܰ�
		String price 				= "0" ;		// �����ݾ�
		String productPrice 		= "0" ;		// ���ް���
		String taxPrice 			= "0" ;		// ����
		String totalPrice 			= "0" ;		// ���� �ݾ�
		String receivePrice 		= "0" ;		// �����ݾ�
		int option 					= 0; 		// ��������Ÿ�� 
		
		// 2011.09.07 ksm ������ �ݾ� ���� �߰�
		String preReceivePrice = "0";	// �����αݾ�
		
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
					
					manager = storeData.getRep_name() ;																				// �����
					wDate = getWorkingDateFormat(storeData.getBizhour_date()) ;									// ��������
					odtNo = PumpMUtil.getODTNumberFromNozzleNo(uPosMsg.getNozzle_no()) ;					
					transactionNo = uPosMsg.getPosReceipt_no() ;																// �ŷ���ȣ
					storeName = storeData.getCust_name_disp() ;																	// �����
					representative = storeData.getRep_name() ;																		// ��ǥ��
					represent = storeData.getBizregno_nbr() ;																			// ����ڹ�ȣ
					address = storeData.getBizstoreaddr_txt1() + " " + storeData.getBizstoreaddr_txt2();	// �����ּ�					
					cardNumber = uPosMsg.getCreditCard_no() ;																	// ī���ȣ
//					acceptNo = uPosMsg.getCredit_auth_no() ;		// ���ι�ȣ
//					cardCorpName = uPosMsg.getAcquier_code() + " " + uPosMsg.getAcquier_name() ;	// ����ȸ�� 					
//					sequence = "01" ;														// ����
					productName2 = productData.getGoods_name() ;															// ��ǰ��
					nozzleNo = uPosMsg.getNozzle_no() ;																				// �����ȣ
//					liter = pumpingItemInfo.getTotalOilAmount() ;																// ������
					basePrice = pumpingItemInfo.getUnitPrice() ;																	// �ܰ�
//					price = pumpingItemInfo.getTotalOilPrice_after_discount() ;											// �����ݾ�
//					productPrice = pumpingItemInfo.getTotalPrice_tax() ;														// ���ް���
//					taxPrice = pumpingItemInfo.getTotalTaxPrice() ;																// ����
//					totalPrice = pumpingItemInfo.getTotalOilPrice_after_discount() ;									// �����ݾ�
//					receivePrice = "0" ;
					option = DasNoSelfPumpingManager.getInstance().getCurrentOption(nozzleNo) ;
					
					//2011.09.08 ksm �����αݾ� ����.
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

	// tatsuno_hs okdhp7 (2012.12) �߰�
	/**
	 * GS ���ʽ� ����Ʈ ���� ��û�� ���� ���� ������ �̿��Ͽ� �پ��� ODT ������ ����� ���� �޽��� ������ ��û�Ѵ�.
	 * 
	 * @param pumpingItemInfo	: ���� ����
	 * @param uPosMsg			: �ſ� ���� ���� ����
	 * @param refund			: POS ���� �������� �����ؾ��� �ݾ�
	 * @param isAccepted		: ������ �� ���
	 * @return
	 */
	private static String createReceiptByBonusPoint(UPOSMessage_ItemInfo pumpingItemInfo, 
			UPOSMessage uPosMsg, int refund, boolean isAccepted) {
		String formatMsg = "���� ������ �޾�����, ���� ������ ������ ���ܼ� Print ������ �����ϴ�." ;		
		
		String productName1 		= "" ;		// ��ǰ��
		String storeCode 			= "" ;		// �����ڵ�
		String date 				= "" ;		// ��¥(YYYYMMDDHHMMSS)
		String tel 					= "" ;		// ������ȭ��ȣ
		String manager 				= "" ;		// �����
		String wDate 				= "" ;		// ��������
		String odtNo 				= "" ;		// ODT ��ȣ
		String transactionNo 		= "" ;		// �ŷ���ȣ
		String storeName 			= "" ;		// �����
		String representative 		= "" ;		// ��ǥ��
		String represent 			= "" ;		// ����ڹ�ȣ
		String address 				= "" ;		// �����ּ�
		String cardNumber 			= "" ;		// ī���ȣ 
		String acceptNo 			= "" ;		// ���ι�ȣ 
		String cardCorpName 		= "" ;		// ����ȸ�� 
		String sequence 			= "" ;		// ����
		String productName2 		= "" ;		// ��ǰ��
		String nozzleNo 			= "" ;		// �����ȣ
		String liter 				= "" ;		// ������
		String basePrice 			= "" ;		// �ܰ�
		String price 				= "" ;		// �����ݾ�
		String productPrice 		= "" ;		// ���ް���
		String taxPrice 			= "" ;		// ����
		String totalPrice 			= "" ;		// ���� �ݾ�
		String receivePrice 		= "" ;		// �����ݾ�
		int option 					= 0; 		// �������� Ÿ��
		
//		 2011.09.07 ksm ������ �ݾ� ���� �߰�
		String preReceivePrice = "0";	// �����αݾ�
		
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
					
					manager = storeData.getRep_name() ;				// �����
					wDate = getWorkingDateFormat(storeData.getBizhour_date()) ;			// ��������
					odtNo = PumpMUtil.getODTNumberFromNozzleNo(uPosMsg.getNozzle_no()) ;					
					transactionNo = uPosMsg.getPosReceipt_no() ;	// �ŷ���ȣ
					storeName = storeData.getCust_name_disp() ;		// �����
					representative = storeData.getRep_name() ;		// ��ǥ��
					represent = storeData.getBizregno_nbr() ;		// ����ڹ�ȣ
					address = storeData.getBizstoreaddr_txt1()  + " " + storeData.getBizstoreaddr_txt2();	// �����ּ�					
					cardNumber = uPosMsg.getCreditCard_no() ;	// ī���ȣ
					acceptNo = uPosMsg.getCredit_auth_no() ;	// ���ι�ȣ
					cardCorpName = uPosMsg.getAcquier_code() + " " + uPosMsg.getAcquier_name() ;	// ����ȸ�� 					
					sequence = "01" ;														// ����
					productName2 = productData.getGoods_name() ;							// ��ǰ��
					nozzleNo = uPosMsg.getNozzle_no() ;										// �����ȣ
					liter = pumpingItemInfo.getTotalOilAmount() ;							// ������
					basePrice = pumpingItemInfo.getUnitPrice() ;							// �ܰ�
					price = pumpingItemInfo.getTotalOilPrice_after_discount() ;				// �����ݾ�
					productPrice = pumpingItemInfo.getTotalPrice_tax() ;					// ���ް���
					taxPrice = pumpingItemInfo.getTotalTaxPrice() ;							// ����
					totalPrice = pumpingItemInfo.getTotalOilPrice_after_discount() ;		// �����ݾ�
					option = DasNoSelfPumpingManager.getInstance().getCurrentOption(nozzleNo) ;
										
					//2011.09.08 ksm �����αݾ� ����.
					preReceivePrice = getPreReceivePrice(nozzleNo, transactionNo, price);

					if (isAccepted) {
						receivePrice = itemInfo.getTotalOilPrice_after_discount() ;			// �����ݾ�
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
		
		if (refund != 0) { // ������ ������(������ ������� ����)
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
		} else { // ������ ������(�������� �� �̸����� ����� �ش�)
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
	 * ���� ���� ���� �پ��� ODT ��� �޽��� ������ ��û�Ѵ�.
	 * 
	 * @param nozzleNo			: ���� ��ȣ
	 * @param khproc_no			: KH ó����ȣ
	 * @param pumpingPrice		: ���� �ݾ�
	 * @param pumpingLiter		: ���� ��
	 * @param payedPrice		: ���� �ݾ�
	 * @param pumpingBasePrice	: ���� �ܰ�
	 * @return
	 */
	public static String createReceiptByCash(String nozzleNo, String khproc_no, 
			String pumpingPrice, String pumpingLiter, String payedPrice, String pumpingBasePrice) {
		String formatMsg = "���� ������ �޾�����, ���� ������ ������ ���ܼ� Print ������ �����ϴ�." ;		

		String productName1 			= "" ; 	// ��ǰ��
		String storeCode 				= "" ; 	// �����ڵ�
		String date 					= "" ; 	// ��¥ (YYYYMMDDHHMMSS)
		String tel 						= "" ; 	// ������ȭ��ȣ
		String manager 					= "" ; 	// �����
		String wDate 					= "" ; 	// ��������
		String odtNo 					= "" ; 	// ODT ��ȣ
		String transactionNo 			= "" ;	// �ŷ���ȣ
		String storeName 				= "" ; 	// �����
		String representative 			= "" ;	// ��ǥ��
		String represent 				= "" ; 	// ����ڹ�ȣ
		String address 					= "" ; 	// ���� �ּ�
		/*String cardNumber				= "" ;	// ī���ȣ
		String acceptNo 				= "" ;	// ���ι�ȣ
		String cardCorpName 			= "" ; 	// ����ȸ��
*/		String sequence 				= "" ;	// ����
		String productName2 			= "" ; 	// ��ǰ��
		String liter 					= "" ;	// ������
		String basePrice 				= "" ; 	// �ܰ�
		String price 					= "" ;	// �����ݾ�
		String productPrice 			= "" ; 	// ���ް���
		String taxPrice 				= "" ;	// ����
		String totalPrice 				= "" ; 	// ���� �ݾ�
		String receivePrice 			= payedPrice ; 	// ���� �ݾ�
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
					
					manager = storeData.getRep_name() ;														// �����
					wDate = getWorkingDateFormat(storeData.getBizhour_date()) ;			// ��������
					odtNo = PumpMUtil.getODTNumberFromNozzleNo(nozzleNo) ;			// ODT��ȣ		
					transactionNo = khproc_no ;																		// �ŷ���ȣ
					storeName = storeData.getCust_name_disp() ;											// �����
					representative = storeData.getRep_name() ;												// ��ǥ��
					represent = storeData.getBizregno_nbr() ;													// ����ڹ�ȣ
					address = storeData.getBizstoreaddr_txt1()  + " " + storeData.getBizstoreaddr_txt2();	// �����ּ�					
					sequence = "01" ;																							// ����
					productName2 = productData.getGoods_name() ;									// ��ǰ��
					liter = itemInfo.getTotalOilAmount() ;														// ������
					basePrice = itemInfo.getUnitPrice() ;															// �ܰ�
					price = itemInfo.getTotalOilPrice_after_discount() ;									// �����ݾ�
					productPrice = itemInfo.getTotalPrice_tax() ;											// ���ް���
					taxPrice = itemInfo.getTotalTaxPrice() ;														// ����
					totalPrice = itemInfo.getTotalOilPrice_after_discount() ;							// �����ݾ�
					
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
	 * ���� ���� ��û�� ���� ���� ������ �̿��Ͽ� �پ��� ODT ������ ����� ���� �޽��� ������ ��û�Ѵ�.
	 * 
	 * @param pumpingItemInfo	: ���� ����
	 * @param uPosMsg			: �ſ� ���� ���� ����
	 * @param refund			: POS ���� �������� �����ؾ��� �ݾ�
	 * @param isAccepted		: ������ �� ���
	 * @return
	 */

	@SuppressWarnings("unused")
	private static String createReceiptByCash(UPOSMessage_ItemInfo pumpingItemInfo, 
			UPOSMessage uPosMsg, int refund, boolean isAccepted) {
		String formatMsg 			= "���� ������ �޾�����, ���� ������ ������ ���ܼ� Print ������ �����ϴ�." ;		
		String productName1 		= "" ;		// ��ǰ��
		String storeCode 			= "" ;		// �����ڵ�
		String date 				= "" ;		// ��¥(YYYYMMDDHHMMSS)
		String tel 					= "" ;		// ������ȭ��ȣ
		String manager 				= "" ;		// �����
		String wDate 				= "" ;		// ��������
		String odtNo 				= "" ;		// ODT ��ȣ
		String transactionNo		= "" ;		// �ŷ���ȣ
		String storeName 			= "" ;		// �����
		String representative 		= "" ;		// ��ǥ��
		String represent 			= "" ;		// ����ڹ�ȣ
		String address 				= "" ;		// �����ּ�
		String cardNumber 			= "" ;		// ī���ȣ 
		String acceptNo 			= "" ;		// ���ι�ȣ 
		String cardCorpName 		= "" ;		// ����ȸ�� 
		String sequence 			= "" ;		// ����
		String productName2 		= "" ;		// ��ǰ��
		String nozzleNo 			= "" ;		// �����ȣ
		String liter 				= "" ;		// ������
		String basePrice 			= "" ;		// �ܰ�
		String price 				= "" ;		// �����ݾ�
		String productPrice 		= "" ;		// ���ް���
		String taxPrice 			= "" ;		// ����
		String totalPrice 			= "" ;		// ���� �ݾ�
		String receivePrice 		= "" ;		// �����ݾ�
		int option 					= 0; 		// �������� Ÿ��
		
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
					
					manager = storeData.getRep_name() ;				// �����
					wDate = getWorkingDateFormat(storeData.getBizhour_date()) ;			// ��������
					odtNo = PumpMUtil.getODTNumberFromNozzleNo(uPosMsg.getNozzle_no()) ;					
					transactionNo = uPosMsg.getPosReceipt_no() ;	// �ŷ���ȣ
					storeName = storeData.getCust_name_disp() ;		// �����
					representative = storeData.getRep_name() ;		// ��ǥ��
					represent = storeData.getBizregno_nbr() ;		// ����ڹ�ȣ
					address = storeData.getBizstoreaddr_txt1()  + " " + storeData.getBizstoreaddr_txt2();	// �����ּ�					
					cardNumber = uPosMsg.getCreditCard_no() ;	// ī���ȣ
					acceptNo = uPosMsg.getCredit_auth_no() ;	// ���ι�ȣ
					cardCorpName = uPosMsg.getAcquier_code() + " " + uPosMsg.getAcquier_name() ;	// ����ȸ�� 					
					sequence = "01" ;														// ����
					productName2 = productData.getGoods_name() ;							// ��ǰ��
					nozzleNo = uPosMsg.getNozzle_no() ;										// �����ȣ
					liter = pumpingItemInfo.getTotalOilAmount() ;							// ������
					basePrice = pumpingItemInfo.getUnitPrice() ;							// �ܰ�
					price = pumpingItemInfo.getTotalOilPrice_after_discount() ;				// �����ݾ�
					productPrice = pumpingItemInfo.getTotalPrice_tax() ;					// ���ް���
					taxPrice = pumpingItemInfo.getTotalTaxPrice() ;							// ����
					totalPrice = pumpingItemInfo.getTotalOilPrice_after_discount() ;		// �����ݾ�
					option = DasNoSelfPumpingManager.getInstance().getCurrentOption(nozzleNo) ;
					
					if (isAccepted) {
						if (Integer.parseInt(liter) == 0)
							receivePrice = String.valueOf(refund);
						else
							receivePrice = String.valueOf(Integer.parseInt(itemInfo.getTotalOilPrice_after_discount()) + refund);			// �����ݾ�
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
		String formatMsg = "���� ������ �޾�����, ���� ������ ������ ���ܼ� Print ������ �����ϴ�." ;		

		String productName1 = "" ; 	// ��ǰ��
		String storeCode = "" ; 	// �����ڵ�
		String date = "" ; 			// ��¥ (YYYYMMDDHHMMSS)
		String tel = "" ; 			// ������ȭ��ȣ
		String manager = "" ; 		// �����
		String wDate = "" ; 		// ��������
		String odtNo = "" ; 		// ODT ��ȣ
		String transactionNo = "" ;	// �ŷ���ȣ
		String storeName = "" ; 	// �����
		String representative = "" ;// ��ǥ��
		String represent = "" ; 	// ����ڹ�ȣ
		String address = "" ; 		// ���� �ּ�
		/*String cardNumber = "" ; 	// ī���ȣ
		String acceptNo = "" ; 		// ���ι�ȣ
		String cardCorpName = "" ; 	// ����ȸ��
*/		String sequence = "" ; 		// ����
		String productName2 = "" ; 	// ��ǰ��
		String liter = "" ; 		// ������
		String basePrice = "" ; 	// �ܰ�
		String price = "" ; 		// �����ݾ�
		String productPrice = "" ; 	// ���ް���
		String taxPrice = "" ; 		// ����
		String totalPrice = "" ; 	// ���� �ݾ�
		String receivePrice = payedPrice ; 	// ���� �ݾ�
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
					
					manager = storeData.getRep_name() ;			// �����
					wDate = getWorkingDateFormat(storeData.getBizhour_date()) ;			// ��������
					odtNo = PumpMUtil.getODTNumberFromNozzleNo(nozzleNo) ;					
					transactionNo = khproc_no ;	// �ŷ���ȣ
					storeName = storeData.getCust_name_disp() ;		// �����
					representative = storeData.getRep_name() ;	// ��ǥ��
					represent = storeData.getBizregno_nbr() ;		// ����ڹ�ȣ
					address = storeData.getBizstoreaddr_txt1()  + " " + storeData.getBizstoreaddr_txt2();	// �����ּ�					
					sequence = "01" ;		// ����
					productName2 = productData.getGoods_name() ;	// ��ǰ��
					liter = itemInfo.getTotalOilAmount() ;			// ������
					basePrice = itemInfo.getUnitPrice() ;		// �ܰ�
					price = itemInfo.getTotalOilPrice_after_discount() ;			// �����ݾ�
					productPrice = itemInfo.getTotalPrice_tax() ;	// ���ް���
					taxPrice = itemInfo.getTotalTaxPrice() ;		// ����
					totalPrice = itemInfo.getTotalOilPrice_after_discount() ;		// �����ݾ�
					
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
	 * �ſ� ���� ��û�� ���� ���� ������ �̿��Ͽ� �پ��� ODT ������ ����� ���� �޽��� ������ ��û�Ѵ�.
	 * 
	 * @param pumpingItemInfo	: ���� ����
	 * @param uPosMsg			: �ſ� ���� ���� ����
	 * @param refund			: POS ���� �������� �����ؾ��� �ݾ�
	 * @param isAccepted		: ������ �� ���
	 * @return
	 */
	private static String createReceiptByCredit(UPOSMessage_ItemInfo pumpingItemInfo, 
			UPOSMessage uPosMsg, int refund, boolean isAccepted) {
		String formatMsg = "���� ������ �޾�����, ���� ������ ������ ���ܼ� Print ������ �����ϴ�." ;		
		
		String productName1 		= "" ;		// ��ǰ��
		String storeCode 			= "" ;		// �����ڵ�
		String date 				= "" ;		// ��¥(YYYYMMDDHHMMSS)
		String tel 					= "" ;		// ������ȭ��ȣ
		String manager 				= "" ;		// �����
		String wDate 				= "" ;		// ��������
		String odtNo 				= "" ;		// ODT ��ȣ
		String transactionNo 		= "" ;		// �ŷ���ȣ
		String storeName 			= "" ;		// �����
		String representative 		= "" ;		// ��ǥ��
		String represent 			= "" ;		// ����ڹ�ȣ
		String address 				= "" ;		// �����ּ�
		String cardNumber 			= "" ;		// ī���ȣ 
		String acceptNo 			= "" ;		// ���ι�ȣ 
		String cardCorpName 		= "" ;		// ����ȸ�� 
		String sequence 			= "" ;		// ����
		String productName2 		= "" ;		// ��ǰ��
		String nozzleNo 			= "" ;		// �����ȣ
		String liter 				= "" ;		// ������
		String basePrice 			= "" ;		// �ܰ�
		String price 				= "" ;		// �����ݾ�
		String productPrice 		= "" ;		// ���ް���
		String taxPrice 			= "" ;		// ����
		String totalPrice 			= "" ;		// ���� �ݾ�
		String receivePrice 		= "" ;		// �����ݾ�
		int option 					= 0; 		// �������� Ÿ��
		String monthCnt			= "00";	// �Һΰ�����
		
//		 2011.09.07 ksm ������ �ݾ� ���� �߰�
		String preReceivePrice = "0";	// �����αݾ�
		
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
					
					manager = storeData.getRep_name() ;				// �����
					wDate = getWorkingDateFormat(storeData.getBizhour_date()) ;			// ��������
					odtNo = PumpMUtil.getODTNumberFromNozzleNo(uPosMsg.getNozzle_no()) ;					
					transactionNo = uPosMsg.getPosReceipt_no() ;	// �ŷ���ȣ
					storeName = storeData.getCust_name_disp() ;		// �����
					representative = storeData.getRep_name() ;		// ��ǥ��
					represent = storeData.getBizregno_nbr() ;		// ����ڹ�ȣ
					address = storeData.getBizstoreaddr_txt1()  + " " + storeData.getBizstoreaddr_txt2();	// �����ּ�					
					cardNumber = uPosMsg.getCreditCard_no() ;	// ī���ȣ
					acceptNo = uPosMsg.getCredit_auth_no() ;	// ���ι�ȣ
					cardCorpName = uPosMsg.getAcquier_code() + " " + uPosMsg.getAcquier_name() ;	// ����ȸ�� 					
					sequence = "01" ;														// ����
					productName2 = productData.getGoods_name() ;							// ��ǰ��
					nozzleNo = uPosMsg.getNozzle_no() ;										// �����ȣ
					liter = pumpingItemInfo.getTotalOilAmount() ;							// ������
					basePrice = pumpingItemInfo.getUnitPrice() ;							// �ܰ�
					price = pumpingItemInfo.getTotalOilPrice_after_discount() ;				// �����ݾ�
					productPrice = pumpingItemInfo.getTotalPrice_tax() ;					// ���ް���
					taxPrice = pumpingItemInfo.getTotalTaxPrice() ;							// ����
					totalPrice = pumpingItemInfo.getTotalOilPrice_after_discount() ;		// �����ݾ�
					option = DasNoSelfPumpingManager.getInstance().getCurrentOption(nozzleNo) ;
					
					//2013.05.20 ksm �Һΰ����� �߰�
					monthCnt = uPosMsg.getCredit_month();
					//2011.09.08 ksm �����αݾ� ����.
					preReceivePrice = getPreReceivePrice(nozzleNo, transactionNo, price);

					if (isAccepted) {
						receivePrice = itemInfo.getTotalOilPrice_after_discount() ;			// �����ݾ�
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
	 * �ſ� + ���ʽ� ���� ��û�� ���� ���� ������ �̿��Ͽ� �پ��� ODT ��� �޽����� ���� ��û�Ѵ�.
	 * 
	 * @param pumpingItemInfo	: ���� ����
	 * @param uPosMsg			: ���� ���� ����
	 * @param refund			: POS ���� �������� ������� �� �ݾ�
	 * @param isAccepted		: ������ �� ���
	 * @return
	 */
	private static String createReceiptByCreditBonus(UPOSMessage_ItemInfo pumpingItemInfo, 
			UPOSMessage uPosMsg, int refund, boolean isAccepted) {
		String formatMsg = "���� ������ �޾�����, ���� ������ ������ ���ܼ� Print ������ �����ϴ�." ;		
		
		String productName1 		= "" ; 	// ��ǰ��
		String storeCode 			= "" ; 	// �����ڵ�
		String date 				= "" ; 	// ��¥ (YYYYMMDDHHMMSS)
		String tel 					= "" ; 	// ������ȭ��ȣ
		String manager				= "" ; 	// �����
		String wDate 				= "" ; 	// ��������
		String odtNo 				= "" ; 	// ODT ��ȣ
		String transactionNo 		= "" ; 	// �ŷ���ȣ
		String storeName 			= "" ; 	// �����
		String representative 		= "" ;	// ��ǥ��
		String represent 			= "" ; 	// ����ڹ�ȣ
		String address 				= "" ; 	// ���� �ּ�
		String cardNumber 			= "" ; 	// ī���ȣ
		String acceptNo 			= "" ; 	// ���ι�ȣ
		String cardCorpName 		= "" ; 	// ����ȸ��
		String sequence 			= "" ; 	// ����
		String productName2 		= "" ; 	// ��ǰ��
		String nozzleNo 			= "" ; 	// ���� ��ȣ
		String liter 				= "" ; 	// ������
		String basePrice 			= "" ; 	// �ܰ�
		String price 				= "" ; 	// �����ݾ�
		String productPrice 		= "" ; 	// ���ް���
		String taxPrice 			= "" ; 	// ����
		String totalPrice 			= "" ; 	// ���� �ݾ�
		String receivePrice 		= "" ; 	// ���� �ݾ�
		String bonusCardNo 			= "" ; 	// ī��No
		String authNo 				= "" ; 	// ����No
		String createPoint 			= "" ; 	// �߻�
		String usePoint 			= "" ; 	// ����
		String totalPoint 			= "" ; 	// �Ѵ���
		String message 				= "" ; 	// �޼���
		int option 					= 0; 	// �������� Ÿ��
		
		// 2013.05.20 ksm �Һΰ�����
		String monthCnt 			= "00";
		
//		 2011.09.07 ksm ������ �ݾ� ���� �߰�
		String preReceivePrice = "0";	// �����αݾ�
		
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
					
					manager = storeData.getRep_name() ;				// �����
					wDate = getWorkingDateFormat(storeData.getBizhour_date()) ;			// ��������
					odtNo = PumpMUtil.getODTNumberFromNozzleNo(uPosMsg.getNozzle_no()) ;					
					transactionNo = uPosMsg.getPosReceipt_no() ;	// �ŷ���ȣ
					storeName = storeData.getCust_name_disp() ;		// �����
					representative = storeData.getRep_name() ;		// ��ǥ��
					represent = storeData.getBizregno_nbr() ;		// ����ڹ�ȣ
					address = storeData.getBizstoreaddr_txt1()  + " " + storeData.getBizstoreaddr_txt2();	// �����ּ�					
					cardNumber = uPosMsg.getCreditCard_no() ;	// ī���ȣ 
					acceptNo = uPosMsg.getCredit_auth_no() ;	// ���ι�ȣ
					cardCorpName = uPosMsg.getAcquier_code() + " " + uPosMsg.getAcquier_name() ;	// ����ȸ�� 					
					sequence = "01" ;		// ����
					productName2 = productData.getGoods_name() ;	// ��ǰ��
					nozzleNo = uPosMsg.getNozzle_no() ;				// �����ȣ
					liter = pumpingItemInfo.getTotalOilAmount() ;	// ������
					basePrice = pumpingItemInfo.getUnitPrice() ;	// �ܰ�
					price = pumpingItemInfo.getTotalOilPrice_after_discount() ;				// �����ݾ�
					productPrice = pumpingItemInfo.getTotalPrice_tax() ;					// ���ް���
					taxPrice = pumpingItemInfo.getTotalTaxPrice() ;							// ����
					totalPrice = pumpingItemInfo.getTotalOilPrice_after_discount() ;		// ���� �ݾ�
					
					if (isAccepted) {
						receivePrice = itemInfo.getTotalOilPrice_after_discount() ;			// �����ݾ�
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
					//2011.09.08 ksm �����αݾ� ����.
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
	
	// 2012.10.19 ksm ����û ���� ī�� ODT���� ���� ������ ����.
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
		
		String manager 			= storeData.getRep_name() ;															// �����
		String wDate 				= getWorkingDateFormat(storeData.getBizhour_date()) ;			// ��������
		String odtNo 				= nozzleNo;																						// �����ȣ
		String transactionNo 	= "0000000000" ;																				// �ŷ���ȣ
		String storeName 		= storeData.getCust_name_disp() ;													// �����
		String representative 	= storeData.getRep_name() ;															// ��ǥ��
		String represent 			= storeData.getBizregno_nbr() ;														// ����ڹ�ȣ
		String address 			= storeData.getBizstoreaddr_txt1()  + " " + storeData.getBizstoreaddr_txt2();	// �����ּ�		
			
		return PumpMessageFormat.createFailTatsunoFormat(tel, date, manager, wDate, odtNo, transactionNo, storeName, representative, represent, address, outputMessage);
	}

	
	/**
	 * �����Ϸ� �ݾ��� 0 ���̾ ������ ��� ���� ������ Format
	 * 
	 * @param itemInfo		: �����Ϸ� �ڷ�
	 * @param posMsg		: ������ ��� ���� ����
	 * @param refund		: ȯ�� �ݾ�				
	 * @param isAccepted	: ���� or ���� ����
	 * @return
	 */
	private static String createReceiptWhenPumping0(UPOSMessage_ItemInfo pumpingItemInfo, UPOSMessage uPosMsg, int refund, boolean isAccepted) {

		String formatMsg = "���� ������ �޾�����, ���� ������ ������ ���ܼ� Print ������ �����ϴ�." ;		
		
		String productName1 		= "" ;		// ��ǰ��
		String storeCode 			= "" ;		// �����ڵ�
		String date 				= "" ;		// ��¥(YYYYMMDDHHMMSS)
		String tel 					= "" ;		// ������ȭ��ȣ
		String manager 				= "" ;		// �����
		String wDate				= "" ;		// ��������
		String odtNo 				= "" ;		// ODT ��ȣ
		String transactionNo 		= "" ;		// �ŷ���ȣ
		String storeName 			= "" ;		// �����
		String representative 		= "" ;		// ��ǥ��
		String represent 			= "" ;		// ����ڹ�ȣ
		String address 				= "" ;		// �����ּ�
		String cardNumber 			= "" ;		// ī���ȣ 
		String acceptNo 			= "" ;		// ���ι�ȣ 
		String cardCorpName 		= "" ;		// ����ȸ�� 
		String sequence 			= "" ;		// ����
		String productName2 		= "" ;		// ��ǰ��
		String nozzleNo 			= "" ;		// �����ȣ
		String liter 				= "0" ;		// ������
		String basePrice 			= "" ;		// �ܰ�
		String price 				= "0" ;		// �����ݾ�
		String productPrice 		= "0" ;		// ���ް���
		String taxPrice 			= "0" ;		// ����
		String totalPrice 			= "0" ;		// ���� �ݾ�
		String receivePrice 		= "0" ;		// �����ݾ�
		int option 					= 0; 		// ��������Ÿ�� 
		String monthCnt = "00";
		
		// 2011.09.07 ksm ������ �ݾ� ���� �߰�
		String preReceivePrice = "0";	// �����αݾ�
		
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
					
					manager = storeData.getRep_name() ;																				// �����
					wDate = getWorkingDateFormat(storeData.getBizhour_date()) ;									// ��������
					odtNo = PumpMUtil.getODTNumberFromNozzleNo(uPosMsg.getNozzle_no()) ;					
					transactionNo = uPosMsg.getPosReceipt_no() ;																// �ŷ���ȣ
					storeName = storeData.getCust_name_disp() ;																	// �����
					representative = storeData.getRep_name() ;																		// ��ǥ��
					represent = storeData.getBizregno_nbr() ;																			// ����ڹ�ȣ
					address = storeData.getBizstoreaddr_txt1() + " " + storeData.getBizstoreaddr_txt2();	// �����ּ�					
					cardNumber = uPosMsg.getCreditCard_no() ;																	// ī���ȣ
//					acceptNo = uPosMsg.getCredit_auth_no() ;		// ���ι�ȣ
//					cardCorpName = uPosMsg.getAcquier_code() + " " + uPosMsg.getAcquier_name() ;	// ����ȸ�� 					
//					sequence = "01" ;														// ����
					productName2 = productData.getGoods_name() ;															// ��ǰ��
					nozzleNo = uPosMsg.getNozzle_no() ;																				// �����ȣ
//					liter = pumpingItemInfo.getTotalOilAmount() ;																// ������
					basePrice = pumpingItemInfo.getUnitPrice() ;																	// �ܰ�
//					price = pumpingItemInfo.getTotalOilPrice_after_discount() ;											// �����ݾ�
//					productPrice = pumpingItemInfo.getTotalPrice_tax() ;														// ���ް���
//					taxPrice = pumpingItemInfo.getTotalTaxPrice() ;																// ����
//					totalPrice = pumpingItemInfo.getTotalOilPrice_after_discount() ;									// �����ݾ�
//					receivePrice = "0" ;
					option = DasNoSelfPumpingManager.getInstance().getCurrentOption(nozzleNo) ;
					
					//2013.05.20 ksm 
					monthCnt = uPosMsg.getCredit_month();
					//2011.09.08 ksm �����αݾ� ����.
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
	 * �ܻ�ŷ� ������
	 *  
	 * @param itemInfo
	 * @param uPosMsg
	 * @param diffPrice
	 * @param isAccepted
	 * @return
	 */
	private static String createTrustTatsunoFormat(UPOSMessage_ItemInfo itemInfo, UPOSMessage uPosMsg, int diffPrice, boolean isAccepted) {
		String formatMsg = "���� ������ �޾�����, ���� ������ ������ ���ܼ� Print ������ �����ϴ�." ;	
		
		String productName1			= "";	// ��ǰ��
		String storeCode			= "";	// �����ڵ� (������� ����)
		String date					= "";	// ��¥ (YYYYMMDDHHMMSS)
		String tel					= "";	// ������ȭ��ȣ
		String manager				= "";	// �����
		String wDate				= "";	// ��������
		String odtNo				= "";	// ODT ��ȣ
		String transactionNo		= "";	// �ŷ���ȣ
		String storeName			= "";	// �����
		String representative		= "";	// ��ǥ��
		String represent			= "";	// ����ڹ�ȣ
		String address				= "";	// ���� �ּ�
		int    limitType			= 0;	// �ѵ� ����(�ݾ� or ����)
		String customerCode			= "";	// �ŷ�ó �ڵ�
		String customerName 		= "";	// �ŷ�ó��
		String customerCardNo		= "";	// �ŷ�ó ī���ȣ
		String customerCarNo 		= "";	// �ŷ�ó ���� ��ȣ 
		String limit 				= "";	// �ѵ���
		String saveLimit 			= "";	// ���� ��뷮
		String remainLimit 			= "";	// �ѵ� �ܷ�
		String sequence				= "";	// ��������
		String productName2			= "";	// ��ǰ��
		String nozzleNo				= "";	// ���� ��ȣ
		String liter				= "";	// ������
		String basePrice			= "0";	// �ܰ�
		String price				= "";	// �����ݾ�
		String productPrice			= "";	// ���� ��ǰ����
		String taxPrice				= "";	// �ΰ���
		String totalPrice			= "";	// �հ�
		String bonusCardNo			= "";	// ���ʽ�ī�� ��ȣ
		String authNo				= "";	// ���ʽ�ī�� ���� ��ȣ
		String createPoint			= "";	// �߻� ����Ʈ
		String usePoint				= "";	// ���� ����Ʈ
		String totalPoint			= "";	// ������ ����Ʈ
		String message				= "";	// ���ʽ�ī�� �޼���
		int option 					= 0; 	// �������� Ÿ��

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
					storeCode = T_KH_STOREHandler.getHandler().getStoreCode();		//�����ڵ�
					T_KH_STOREData storeData = 
						T_KH_STOREHandler.getHandler().getT_KH_STOREDataByStoreCode(storeCode) ;
					
					productName1 = productData.getGoods_name() ;			// ��ǰ��
					date = getWDateFormat() ;								// ��¥
					tel = "(" + storeData.getTel_nbr() + ")" ;				// ������ȭ��ȣ
					
					manager = storeData.getRep_name() ;						// �����
					wDate = getWorkingDateFormat(storeData.getBizhour_date()) ;			// ��������
					odtNo = PumpMUtil.getODTNumberFromNozzleNo(uPosMsg.getNozzle_no()) ;	//odt��ȣ					
					transactionNo = uPosMsg.getPosReceipt_no() ;	// �ŷ���ȣ
					storeName = storeData.getCust_name_disp() ;		// �����
					representative = storeData.getRep_name() ;		// ��ǥ��
					represent = storeData.getBizregno_nbr() ;		// ����ڹ�ȣ
					address = storeData.getBizstoreaddr_txt1()  + " " + storeData.getBizstoreaddr_txt2();	// �����ּ�					
					
					POSHeader posMsg = PumpMODTSaleManager.getCustPOSPumpM(itemInfo_item.getNozzleNo()) ;
					POS_DW dwPumpM = null ;
					if ((posMsg != null) && (posMsg instanceof POS_DW))
						dwPumpM = (POS_DW) posMsg ;
					
					if (ICode.ADJBASE_CODE_LIMIT_01.equals(dwPumpM.getAdjbase_code_limit()))
						limitType = PumpMessageFormat.LITER_LIMIT_TYPE;
					else
						limitType = PumpMessageFormat.PRICE_LIMIT_TYPE;		// �ѵ����� ����
					
					customerCode	 	= dwPumpM.getCust_code();			// �ŷ�ó�ڵ�
					customerName 		= dwPumpM.getDrive_name();		// �ŷ�ó ��
					customerCardNo 	= dwPumpM.getCust_card_no();	// �ŷ�ó ī���ȣ
					customerCarNo		= dwPumpM.getCar_no();				// �ŷ�ó ���� ��ȣ
					limit 						= dwPumpM.getLimit();					// �ѵ���
					
					if ( ICode.DW_CARDADJ_IND_01.equals(dwPumpM.getCardadj_ind())){
						saveLimit = "0";
						remainLimit = "0";
					} else {
						if (limitType == PumpMessageFormat.LITER_LIMIT_TYPE) {
							saveLimit = String.valueOf(Double.parseDouble(dwPumpM.getAccLimit()) 
										+ Double.parseDouble(PumpMUtil.convertNumberFormatFromPumpToPOS(itemInfo_item.getOilAmount(),3)));				// ���� ��뷮 
							remainLimit = String.valueOf(Double.parseDouble(limit) - Double.parseDouble(saveLimit));			// �ѵ� �ܷ�
						} else {
							saveLimit = String.valueOf(Double.parseDouble(dwPumpM.getAccLimit()) 
										+ Double.parseDouble(itemInfo_item.getOilPrice_after_discount()));				// ���� ��뷮 
							remainLimit = String.valueOf(Double.parseDouble(limit) - Double.parseDouble(saveLimit));			// �ѵ� �ܷ�		
						}
					}
										
					saveLimit = GlobalUtility.getValueByCertainDecimal(saveLimit, 3);
					remainLimit = GlobalUtility.getValueByCertainDecimal(remainLimit, 3);
					sequence = "01" ;		// ����
					productName2 = productData.getGoods_name() ;	// ��ǰ��
					nozzleNo = uPosMsg.getNozzle_no() ;						// �����ȣ
					liter = PumpMUtil.convertNumberFormatFromPumpToPOS(itemInfo.getTotalOilAmount(),3) ;	// ������
					
					if ("1".equals(dwPumpM.getRcptunitprc_ind_prt()) )
						basePrice = PumpMUtil.convertNumberFormatFromPumpToPOS(itemInfo.getUnitPrice(),3) ;	// �ܰ�
					
					price 				= itemInfo.getTotalOilPrice_after_discount() ;	// �����ݾ�
					productPrice 	= itemInfo.getTotalPrice_tax() ;							// ���ް���
					taxPrice 			= itemInfo.getTotalTaxPrice() ;							// ����
					totalPrice 		= itemInfo.getTotalOilPrice_after_discount() ;	// ���� �ݾ�
					
					bonusCardNo		= uPosMsg.getBonRSCard_no();				// ���ʽ�ī�� ��ȣ
					authNo				= uPosMsg.getBonRSCard_authNo();		// ���ʽ�ī�� ���� ��ȣ
					createPoint			= uPosMsg.getGs_point1();						// �߻� ����Ʈ
					usePoint				= uPosMsg.getGs_point2();						// ���� ����Ʈ
					totalPoint			= "0";															// ������ ����Ʈ
					message				= uPosMsg.getBonRS_msg();					// ���ʽ�ī�� �޼���
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
	 * ���ʽ� ���� ������ �����Ѵ�.
	 * 
	 * @param khproc_no		: KH ó����ȣ
	 * @param nozzle_no		: ���� ��ȣ
	 * @param bonRSCard_no	: ���ʽ� ī�� ��ȣ
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
	 * �پ��� ī�� ���� ��û ������ UPOSMessage Class �� ��ȯ�� ��û�Ѵ�. �پ��� ������ ī�� ���� ��û ������ HE_WorkingMessage
	 * Object �̸�, �� Object �� Bonus_card �ʵ� ���� �� Card_Type �� ���� �׿� �����ϴ� UPOSMessage Object �� ��ȯ�Ѵ�.
	 * 
	 * 	Card_type								Bonus_card ���� ���		Bonus_card=Space �� ���
	 * 		1 : �ſ�ī��(+���ʽ�) ���� ��û
	 * 			-> UPOS (0031 , 0033)			0031					0033
	 * 		2 : �ſ�ī��(+���ʽ�) ���� ��� ��û
	 * 			-> UPOS (8031 , 8033)			8031					8033
	 * 
	 * @param workingMsg			: HE_WorkingMessage Object
	 * @param khproc_no				: KH ó����ȣ
	 * @param ignoreBonusCard		: ���ʽ� ī�� ���� ����
	 * @return
	 */
	public static UPOSMessage createUPOSMessageFromWorkingMessage_DaSNo(WorkingMessage workingMsg, 
			String khproc_no, boolean ignoreBonusCard) {
		LogUtility.getLogger().info("[Pump M] Convert WorkingMessage to UPOSMessage for DaSNo.khproc_no=" + khproc_no) ;
		UPOSMessage uPosMsg = null ;
		HE_WorkingMessage heWorkingMsg = (HE_WorkingMessage) workingMsg ;
		
		heWorkingMsg.print();

		String nozzleNo = heWorkingMsg.getConnectNozzleNo() ; // ���� ��ȣ (ODT ��ȣ�� �ƴ�.)
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

//		������ ������ ���� UposMessage �߰�, 2015.11.19 - cwi 
		String creditCardReading_type = IConstant.CreditCardReading_Type_MAGNETIC ;
		String chipData = "";
		String certification_id = "";
		String signImage_Info = "";
		String signImage_Data = "";
		String term_ID = ODTUtility_Common.getTermId() ;
		String store_CD = ODTUtility_Common.getStoreCode();
		String encryptCredit_no = "";
		String creditPassCode = "";
		String selfPayment_type = "" ; // 2016.03.31 WooChul Jung. UPOSMessage ���� Converting �� ����, �� �Լ��� ȣ���� �Լ����� selfPayment_type ����.
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
			posEntryMode = "9"; // ī�帮��
		else
			posEntryMode = "2"; // Key in
		*/
		
		
		//	PI2 20160324 twlee ��ִ��� �����Ͽ�  ���ݿ����� ��û �ڵ� ����
		//	���ʽ�ī���ȣ (16) / �ֹι�ȣ (13)/ ����ڹ�ȣ (10) / �ڵ��� ��ȣ (10-12)
		//  ����û�۽���ü(01:GSC, 02:����Ʈ��) + �ŷ��ڱ���(0:�Һ���, 1:�����) + Ȯ���ڱ���(0:�ſ�ī���ȣ 1:�ֹε�Ϲ�ȣ 2:����ڵ�Ϲ�ȣ 3:��Ÿ 9:���ʽ�ī���ȣ)
		if (dataLen == 16 || dataLen == 18) {
			// ���ʽ� ī���ȣ
			posEntryMode = "9" ;
			creditCardReading_type = IConstant.CreditCardReading_Type_MAGNETIC;
		} else if (dataLen == 13) {
			// 20160323 twlee PI2  �ֹι�ȣ�� ���ݿ����� ó���� ����
			posEntryMode = "1" ;
			creditCardReading_type = IConstant.CreditCardReading_Type_Keyin;
		} else if (dataLen == 10) {
			// ����� ��ȣ
			posEntryMode = "2" ;
			creditCardReading_type = IConstant.CreditCardReading_Type_Keyin;
		} else if (dataLen == 0) {
			//	20160707 twlee 
			//	�ſ�ī�� ������ ����
			//	�ſ�ī�� ������ ���ݿ�����ī�� ��ȣ�� �ö���� �ʾ� dataLen�� 0���� ��. ���� �ſ�ī�� Ÿ���� CreditCardReading_Type_MAGNETIC�� ó���ؾ���.
			posEntryMode = "" ;
			creditCardReading_type = IConstant.CreditCardReading_Type_MAGNETIC;
		}else {
			// �ڵ��� ��ȣ
			posEntryMode = "3" ;
			creditCardReading_type = IConstant.CreditCardReading_Type_Keyin;
		}
		
		String loyaltyTypeData = "01" + heWorkingMsg.getLoyaltyType() + posEntryMode;
		//AS-IS LogUtility.getLogger().info("[Pump M] ���ݿ����� �ŷ�����=[" +  loyaltyTypeData + "] (�ŷ��ڱ���: 3��° 0=�Һ��� 1=�����, �Է¹��: 4��° 2=KeyIn 9=ī��)");
		 LogUtility.getLogger().info("[Pump M] ���ݿ����� �ŷ�����=[" +  loyaltyTypeData + "]����û�۽���ü(01:GSC, 02:����Ʈ��) + �ŷ��ڱ���(0:�Һ���, 1:�����) + Ȯ���ڱ���(0:�ſ�ī���ȣ 1:�ֹε�Ϲ�ȣ 2:����ڵ�Ϲ�ȣ 3:��Ÿ 9:���ʽ�ī���ȣ)");
		
		// tatsuno_hs okdhp7 (2012.12)
		String monthCount = heWorkingMsg.getMonthCount();
		
		//if(!monthCount.equals("61")) { // 61=�ſ�����Ʈ ����
		// 2013.04.08 ksm �ڵ�ȭ B ��û���� ����.
		if(Integer.parseInt(monthCount) < 60 ) { // �ſ�����Ʈ ������� ó���ϴ� ������ 60�� ��� 
			// �̰�ȣ 2014-06-27
			//�پ���(����) ���ͷ� ������ 5���� �̻� ������ �Һ� ���ñ�� �ȵ�.
			//���ͷ� �����㰡�� ��û���� ���� payPrice = 0 6�������� �������� �Һ�����ȵǰ� ����.
			//ex) �ܰ�:2000, ����30L�ΰ�� 2000*30 = 6�������� �������� �Һ�����ȵǰ� ����.
			if(Math.max(Integer.parseInt(payPrice), Float.parseFloat(basePrice)*Float.parseFloat(liter)) < 50000){
				monthCount = "0"; // �̸����� ����ν� 5���� �̸��� ������ �Ͻú� ���� ��û�ϱ� ����.
				LogUtility.getLogger().info("[Pump M] 5�������� ���� �Ͻú� ���ο�û(�̸����� ����ν� ��������)");
			}
		}
		
		String loyalty_password = heWorkingMsg.getBonusPin().trim(); // tatsuno_hs okdhp7 (2012.12)

		LogUtility.getLogger().info("[Pump M] �Һΰ�����==" + monthCount);
				
		POS_DW dwPumpM = null ;
				
		if (card_number != null) {
			card_number.trim() ;
		}

		if (Double.parseDouble(basePrice) == 0) {
//			LogUtility.getLogger().debug("BASE PRICE IS 0");
			LogUtility.getLogger().debug("[Pump M] basePrice=" + heWorkingMsg.getBasePrice());
		}
		/**
		 * [2008.11.18] �پ��� ������ ��� ���� ��û ���� ���۽� �ݾ� Ȥ�� ���� ���� �ϳ��� ä���� �´�.
		 */
		if (!payPrice.equals("0")) {
			// ����
			liter = GlobalUtility.getValueByCertainDecimal(PumpMUtil.calculateLiterFromPriceAndBasePrice(payPrice, basePrice),3) ;
		} else {
			// ����
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
			if (!isCashCard && !rlt) 			intDealType = 1;	//�Ϲ� (����) : ���ʽ� ����(X) + ���ݿ�����(X)
			else if (!isCashCard && rlt)		intDealType = 2;	//�Ϲ� (����) : ���ʽ� ����(O) + ���ݿ�����(X)
			else if (isCashCard && !rlt)		intDealType = 3;	//�Ϲ� (����) : ���ʽ� ����(X) + ���ݿ�����(O)
			else if (isCashCard && rlt) 		intDealType = 4;	//�Ϲ� (����) : ���ʽ� ����(O) + ���ݿ�����(O)
		} else if (isCreditCard) {
			if (!rlt) 
				intDealType = 11;	//�ſ� : ���ʽ� ����(X)
			else
				intDealType = 12;	//�ſ� : ���ʽ� ����(O)
			
		} else if (isCustCard && !isCreditCard && !rlt){
			intDealType = 21;	//�ܻ� �ŷ�ó : ���ʽ� ����(X)
		} else if (isCustCard && rlt) {
			SqlSession session = null;
			try {
				session = SqlSessionFactoryManager.openSqlSession();
				// �ŷ�ó ���ʽ� ���� ���� �Ǵ�
				T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(session, IConstant.POSPORTOCOL_CODEMASTER_0256);
				
				if ("0".equals(strData)) {
					LogUtility.getLogger().debug("[Pump M] �ܻ�ŷ�ó ���ʽ� ���� �ź� ��."); 
					intDealType = 21;	//�ܻ� �ŷ�ó : ���ʽ� ����(X)
				} else {
					LogUtility.getLogger().debug("[Pump M] �ܻ�ŷ�ó ���ʽ� ���� ����."); 
					
					POSHeader posMsg = PumpMODTSaleManager.getCustPOSPumpM(heWorkingMsg.getConnectNozzleNo()) ;
					
					if ((posMsg != null) && (posMsg instanceof POS_DW))
						dwPumpM = (POS_DW) posMsg ;
					
					intDealType = 22;	//�ܻ� �ŷ�ó : ���ʽ� ����(O)
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
				//�Ϲ� �������� pumpadapter�� �������� ������ְ� ��������� pos�� �������ش�.
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
				LogUtility.getLogger().info("[Pump M] ����û���ݿ�����  -> UPOSMessage(0015)") ;
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
				LogUtility.getLogger().info("[Pump M] ����û���ݿ�����+GS���ʽ���û -> UPOSMessage(0053)") ;
				
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
//																	 ������ ������ ���� ���� ����, PI2, 2105-11-19 - cwi
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
//																	 ������ ������ ���� ���� ����, PI2, 2105-11-19 - cwi
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
				LogUtility.getLogger().info("[Pump M] �ܻ� ��������  -> UPOSMessage(0082)") ;
				
				//���� ���� ��� item info�� �籸���ؾߵǴ� �𸣰��� �ϴ� �׽�Ʈ �� ����
				
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
				LogUtility.getLogger().info("[Pump M] �ܻ� + ���ʽ� ��û����  -> UPOSMessage(0083)") ;
				
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
		case 2: //--- �̸����� ��ҿ�û �� ȣ���
			/**
			 * �پ����� ��� ������ ���� ���� ������ �̿��Ͽ� ���� �Ϸ� ���� ��� ������ ����� ��� �ۿ� ����.
			 * ���� ���� ���� ������ �����Ͽ� �̿��ϵ��� �Ѵ�.
			 */
			uPosMsg = createCancelUPOSMessageFromWorkingMessage_DaSNo(heWorkingMsg, khproc_no) ;
			break;
			
		case 3 : // tatsuno_hs okdhp7 (2012.12)	
			
			//---- (GS����Ʈ ��� ���� ��û)
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
		case 11: //--- ����� ó�� �� ���� �߰� - PI2, CWI, 2015-12-09
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
																		// ������ ������ ���� ���� ����, PI2, 2105-11-19 - cwi
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
																		// ������ ������ ���� ���� ����, PI2, 2105-11-19 - cwi
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
		
		String nozzleNo 		= heWorkingMsg.getConnectNozzleNo() ; // ���� ��ȣ (ODT ��ȣ�� �ƴ�.)
		String card_number 	= heWorkingMsg.getCardNumber() ;
		String basePrice 		= PumpMUtil.convertNumberFormatFromPumpToStandardFormat(heWorkingMsg.getBasePrice(),2) ;
		String payPrice 			= GlobalUtility.getStringValue(heWorkingMsg.getPrice()) ;
		
		// 2011.11.11 ksm 
		// �پ��� ���� ������ 0���� ������ HF 0012 ������ ������ 0���� �ö�.
		// ���� �����̰� �Ҹ����� ��� ������ ��. ���ϼ��� ����  ���� �־���.
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
		
		//�Ϲ� �������� pumpadapter�� �������� ������ְ� ��������� pos�� �������ش�.
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
	 * ���� ������ �پ��� ������ �����ϱ� ���ؼ� WorkineMessage Object �� ��ȯ�Ѵ�.
	 * �Ʒ��� UPOSMessage ���� ������ Message Type �� ���� ���� �����̴�.
	 * 	�ſ�ī�� ���� ����	= 0032
	 * 	�ſ�ī�� ���� ��� ���� = 8032
	 * 	�ſ�ī�� + ���ʽ� ���� ���� = 0034
	 * 	�ſ�ī�� + ���ʽ� ��� ���� = 8034
	 * 	BL üũ = 0072
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
				// �ſ�ī�� ���� ����
				LogUtility.getPumpMLogger().info("[Pump M] Credit Response -> WorkingMessage") ;
				workMsgArray = getQM_WorkingMessage_DaSNo(uPosMsg, true) ;
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_8032 : { 
				// �ſ�ī�� ���� ��� ����
				LogUtility.getPumpMLogger().info("[Pump M] Credit Cancel Response -> WorkingMessage") ;
				workMsgArray = getQM_WorkingMessage_DaSNo(uPosMsg, false) ;
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_0034 : { 
				// �ſ�ī�� + ���ʽ� ���� ����
				LogUtility.getPumpMLogger().info("[Pump M] Credit+Bonus Response -> WorkingMessage") ;
				workMsgArray = getQM_WorkingMessage_DaSNo(uPosMsg, true) ;
				break ;
			}
			case IUPOSConstant.MESSAGETYPE_INT_8034 : { 
				// �ſ�ī�� + ���ʽ� ��� ����
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
	 * ������ �ݾ� ��� ���� �Ǵ�
	 * ksm
	 * 
	 * @param nozzleNo	: �����ȣ
	 * @param transactionNo	: K/Hó����ȣ
	 * @param price : �����ݾ�
	 * @return 0: ������ �ݾ� ��¾���. �׿� : �����αݾ� ���
	 */
	private static String getPreReceivePrice(String nozzleNo, String transactionNo, String price)
	{
		String preReceivePrice = "0";
		ArrayList<UPOSMessage> preApprvMessage = PumpMODTSaleManager.getUPOSMessageArray(nozzleNo, transactionNo);
		
		if(preApprvMessage != null)
		{			
			UPOSMessage upos = preApprvMessage.get(0);
		
			// �ش� �ǿ� UPOSMessage�� 1�� �̻��̰� �ſ�ī�� �����ΰ�� �����ΰ� ����.
			if( preApprvMessage.size() > 0)
			{
				String messageType = upos.getMessageType();
				
				if(IUPOSConstant.MESSAGETYPE_0032.equals(messageType) 
					|| IUPOSConstant.MESSAGETYPE_0034.equals(messageType) 
					|| IUPOSConstant.MESSAGETYPE_0062.equals(messageType))
				{
					preReceivePrice = upos.getPayment_amt(); // index 0 �� ���� �������̹Ƿ� �� ���� ����Ѵ�.
					
					// ������ �ݾװ� ���� �ݾ��� ������ �����αݾ� ��� ����.
					if(preReceivePrice.equals(price))
					{
						preReceivePrice = "0";
					}
				}
			}
		}
		else
		{
			// ���ο� ���� UPos�� ���� �� BLüũ��� ����������.
		}
		return preReceivePrice;
	}
	
	/**
	 * ���� �� ����� Format �� ������ �Ѵ�.
	 * ����� �Ž����� ���̳ʽ��� �̿��Ͽ� ���´�.
	 * 
	 * @param nozzleNo				: ���� ��ȣ
	 * @param khproc_no			: KH ó����ȣ
	 * @param pumpingPrice		: ���� �ݾ�
	 * @param pumpingLiter		: ������
	 * @param payedPrice		: ���� �ݾ�
	 * @param pumpingBasePrice	: ���� �ܰ�
	 * @param uPosMsg			: ���� ��û�� ���� ���� ���� UPOSMessage
	 * @return
	 */
	public static WorkingMessage getQL_WorkingMessage_DaSNoFromODT(String nozzleNo, String khproc_no, 
			String pumpingPrice, String pumpingLiter, String payedPrice, String pumpingBasePrice, UPOSMessage uPosMsg) {
		
		QL_WorkingMessage qlWorkingMsg = null ;
		
		String odtNo 			= null ;
		String nozzle_no 	= uPosMsg.getNozzle_no() ;
		String receipt 			= null ;
		String length 			= "0" ;
		String mode 			= "1";		//pumpadap��ter ql������ ���� �����Ϸ� ���� 1: �����Ϸ�, 0: ���
		
		int payedPriceInt = 0 ;
		boolean isAccepted = true ;
		
		String ledCode = uPosMsg.getLed_code() ;

		// ���� �ݾ��� ������ �� ��쿡�� �ǹ̰� �ֵ��� �Ѵ�.
		if ("1".equals(ledCode)) {
			// ������ �� ���
			payedPriceInt = Integer.parseInt(payedPrice) ;
			isAccepted = true ;
		}
		// ksm 2012.03.21 ���ݰŷ�ó ���ʽ� ������ �Ž����� -�ݾ� ��� ������ �߰�
		// �����ݾ��� �״�� �츲.
		else if ("2".equals(ledCode)){
			// ���ݺ��ʽ��� ��츸
			if(uPosMsg.getMessageType().equals(IUPOSConstant.MESSAGETYPE_0014)) {
				payedPriceInt = Integer.parseInt(payedPrice) ;
				isAccepted = false ;
			}else{
				payedPriceInt = 0 ;
				isAccepted = false ;
			}
		}	
		else {
			// ������ ���� ���� ���
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
		
		//	�̰�ȣ 2014-07 ����ī�庸�ʽ���������
		// ����ī��� ������ ��� ���ʽ� ī�尡 ���� ���μ����� ����Ǿ���. ���������� ���ʽ�ī�� ����, ��¸޽����� �߰����ش�.
		if(messageType == IUPOSConstant.MESSAGETYPE_INT_0032 && 
				!PumpMTransactionManager.getInstance().getCorporateBonus(nozzleNo).equals("")){
			messageType =  IUPOSConstant.MESSAGETYPE_INT_0034;
			uPosMsg.setMessageType(IUPOSConstant.MESSAGETYPE_0034);
			uPosMsg.setBonRS_msg("����ī�� ���ʽ� ����Ʈ ���� ����");
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
		 * �����Ϸᰡ 0���̰�, �����ο� ���� ��Ұ� ����� �� ��� createReceiptWhenPumping0 
		 * �Լ��� ȣ���Ͽ� ������ ������ �����Ѵ�. 
		 * ���� �������� ������ ������ �����ݾ� 0 ��, ���� �ݾ� 0���� �������� �Ѵ�.
		 * �ſ�ī�� ������ ��쿡�� �ش�.
		 */
		if (((option == IConstant.FULL_PUMPING_OPTION_5) || (option == IConstant.FULL_PUMPING_OPTION_7)) &&
				(messageType == IUPOSConstant.MESSAGETYPE_INT_0034 || messageType == IUPOSConstant.MESSAGETYPE_INT_0032 ||
				 messageType == IUPOSConstant.MESSAGETYPE_INT_8034 || messageType == IUPOSConstant.MESSAGETYPE_INT_8032 ||
				 messageType == IUPOSConstant.MESSAGETYPE_INT_8062)) { // tatsuno_hs okdhp7 (2012.12) - �߰�('8062')
			
			// �����Ϸ� �ݾ��� 0 ���� ��� 
			LogUtility.getPumpMLogger().debug("�����ݾ��� 0���̸鼭 �ſ������ ���������� ��ҵ� ��� ��µǴ� ������.");
			if(messageType == IUPOSConstant.MESSAGETYPE_INT_8062) // tatsuno_hs okdhp7 (2012.12) �߰�('8062')
				receipt = createReceiptBonusPointWhenPumping0(itemInfo, uPosMsg, 0, isAccepted);
			else
				receipt = createReceiptWhenPumping0(itemInfo, uPosMsg, 0, isAccepted);
			length = Integer.toString(receipt.length()) ;
		} 
		else {
			// diffPrice �� ����� ���� ����ڰ� �޾ƾ� �� �ݾ��̰�, ������ ���� �����Ұ� ����ڷ� ���� �޾ƾ� �� �ݾ��̴�.
			// ������ ���´ٶ�� ��ü�� ������ ������, ������ �ȳ������� �ڵ� ������ �����ؾ� �Ѵ�.
			int diffPrice = payedPriceInt - pumpingPriceInt ;
						
			switch (messageType) {
				case IUPOSConstant.MESSAGETYPE_INT_0032 : { 
					// �ſ�ī�� ���� ����
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
					// �ſ�ī�� + ���ʽ� ���� ����
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
					// �ܻ�ŷ�ó ����. --> ������ 
					receipt = createTrustTatsunoFormat(itemInfo, uPosMsg, diffPrice, isAccepted) ;
					length = Integer.toString(receipt.length()) ;
					break ;
				}
				case IUPOSConstant.MESSAGETYPE_INT_0084 : { 
					// �ܻ�ŷ�ó ����. --> ������ 
					receipt = createTrustTatsunoFormat(itemInfo, uPosMsg, diffPrice, isAccepted) ;
					length = Integer.toString(receipt.length()) ;
					break ;
				}
				case IUPOSConstant.MESSAGETYPE_INT_0012 :
					//���� ����
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
					//���� + ���ʽ� ����
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
					//����û���ݿ����� ����
				case IUPOSConstant.MESSAGETYPE_INT_0054 : 
					//����û ���� ������ + ���ʽ� ����
				{	
					receipt = createCustomerTatsunoFormat(itemInfo, uPosMsg, diffPrice, isAccepted) ;
					
					length = Integer.toString(receipt.length()) ;
					break;
				}
				case IUPOSConstant.MESSAGETYPE_INT_0004 :
					//���ʽ� ��������
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
				case IUPOSConstant.MESSAGETYPE_INT_0062 : { // tatsuno_hs okdhp7 (2012.12) �߰�
					//  GS���ʽ� ��� ���� ����
					receipt = createReceiptByBonusPoint(itemInfo, uPosMsg, diffPrice, isAccepted) ;
		
					length = Integer.toString(receipt.length()) ;
					break ;
				}
				case IUPOSConstant.MESSAGETYPE_INT_0072 : { 
					// �ſ�ī�� Ȯ�� ���� --> ������ -> �߻��� ���� ����. �߻��ϸ� �ڵ��� ������.
					LogUtility.getPumpMLogger().warn("[Pump M] This Log shouldn't happen. Please check code again.") ;
					
					receipt = createReceiptByCredit(itemInfo, uPosMsg, diffPrice, isAccepted) ;
					length = Integer.toString(receipt.length()) ;
					break ;
				}
			}		
		}
		
		odtNo = PumpMUtil.getODTNumberFromNozzleNo(nozzle_no) ;	

		qlWorkingMsg = new QL_WorkingMessage(odtNo, nozzle_no, length, receipt, mode, "", "") ;
	
		//������ �������� ������OPT �������α� �μ� 
		qlWorkingMsg.setBarCode(Barcode.getBarcodeNumber("3", pumpingPrice, nozzleNo, khproc_no, uPosMsg.getMessageType(), ledCode, null));
		
		
		return qlWorkingMsg ;
	}
	
	

	/**
	 * �پ��� ODT ���� ������ ����� ���� WorkingMessage ������ ��û�Ѵ�. �� Method �� POS �� ������ ���� ������ ����
	 * ������ ���ȴ�.
	 * 
	 * @param nozzleNo			: ���� ��ȣ
	 * @param khproc_no			: KH ó����ȣ
	 * @param pumpingPrice		: ���� �ݾ�
	 * @param pumpingLiter		: ���� ��
	 * @param payedPrice		: ���� �ݾ�
	 * @param pumpingBasePrice	: ���� �ܰ�
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
				
		//������ �������� ������OPT �������α� �μ� 
		qlWorkingMsg.setBarCode(Barcode.getBarcodeNumber("3", pumpingPrice, nozzle_no, khproc_no, null, null, null));
		return qlWorkingMsg ;
	}
	
	
		
	/**
	 * ���� ���� ������ ���� ���� ������ Adapter �� �����ϱ� ���� WorkingMessage Array �� ���� ��û�Ѵ�.
	 * �پ��� ���� ODT �� ���� ���� ������ QM �����̴�. 
	 * 
	 * @param uPosMsg	: ���� ���� ����
	 * @param isAccept	: ���� or ��ҽ��� ����
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
		 * 		Led-Code		��������-mode				�������-mode
		 * 	1	����				1						4
		 * 	2	�ź�				2						5
		 * 	3	ȸ�� ����			3						6
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

		// �ܰ�, �ݾױ��� �÷���� ��.
		// �ܰ�
		String strBasePrice = DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzle_no).getHeWorkingMsg().getBasePrice();
		
		//�ݾ�
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
	 * �������� ����ϱ� ���� ��¥
	 * 
	 * @return
	 */
	private static String getWDateFormat() {
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss") ;
		return formater.format(new Date()) ;	
	}
	
	/**
	 * �������� ����ϱ� ���� ������
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
	 * �ſ� ���� ������ ���ʽ� ���� ������ ������ �ſ�+���ʽ� ���� ������ �����Ѵ�.
	 * 
	 * @param uposMsg0032	: �ſ� ���� ����
	 * @param uposMsg0014	: ���ʽ� ���� ���� ����
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
	 * �پ��� ������ ���� ������ ��� ������ ������ lastPayment_yn �ʵ带 1�� �����Ͽ� POS �� �����Ѵ�.
	 * 1. ���հ�����ó���Ϸ� (0001) �� ���� 1 �� ������ �Ѵ�.	-> �� �Լ� ȣ�� ���� ������ �Ѵ�.
	 * 		�� ���� 0001 �� ���� 1 �� ������ �ϸ�, �� �Լ��� ȣ�������� �ʴ´�.
	 * 2. ���� ���� ó���� ������ �����̸� 1 �� ������ �Ѵ�.
	 * 3. �׷�ġ ���� ���� ���� 0 ���� ������ �Ѵ�.
	 * 
	 * @param uposMsg		: UPOSMessage
	 */
	public static void preProcessingBeforeSendingToSaleM(UPOSMessage uposMsg) {
		try {
			String nozzleNo = uposMsg.getNozzle_no() ;
			// ���� �������� ���� Ȯ��
			if (DasNoSelfPumpingManager.getInstance().isCurrentFullPumping(nozzleNo)) {
				// 2. ���� ���� ó���� ������ �����̸� 1 �� ������ �Ѵ�.
				if (DasNoSelfPumpingManager.getInstance().isFinalResponseUPOSMessageForCurrentFullPumping(nozzleNo)) {
					// ������ Ȯ���ʿ� ksm 2013.11.29
					
					
					uposMsg.setLastPayment_yn(IUPOSConstant.LASTPAYMENT_YN_YES) ;
				} else {
					// 3. �׷�ġ ���� ���� ���� 0 ���� ������ �Ѵ�.
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
	 * 0001 ���� ���� ���θ� �����Ѵ�.
	 * �پ��� ������ ��� TR ������ �����ϸ� �� �Լ��� ȣ���Ͽ� 0001 ������ POS �� ������ �� �����Ѵ�.
	 * ������ ���� ������ ����.
	 * 		1. POS �� ���� Preset �� ���� ������ �ƴ� ���
	 * 		2. ���� ������ �ƴ� ���
	 * 		3. ODT �� ���� ������ ���
	 * 
	 * @param nozzle_no	: ���� ��ȣ
	 * @param khproc_no	: KH ó����ȣ
	 * @param workMsg	: �Ǹ� �Ϸ� ���� (TR_WorkingMessage)
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
			// ���� ������ ��� 0001 ������ POS �� �������� �ʴ´�.
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
	 * UPOSMessage ���� ������ Sale M ���� �������� �Ǵ��Ѵ�.
	 * 
	 * @param uPosMsg	: UPOSMessage ���� ����
	 * @return
	 */
    public static boolean shouldSendToSaleM(UPOSMessage uPosMsg) {
    	boolean rlt = true ;
    	
    	// 2013.07.18 ksm ���ݿ����� ���ν����� ��� POS�� ���� ����.
    	if(IUPOSConstant.RESPOND_LEDCODE_2.equals(uPosMsg.getLed_code()) && IUPOSConstant.MESSAGETYPE_0016.equals(uPosMsg.getMessageType()) ){
    		LogUtility.getPumpMLogger().info("[���ݿ����� ���ν���] POS�� ���۾���. LED_CODE=2, MESSAGE_TYPE=0016" ) ;
    		rlt = false;
    	}

    	return rlt ;	
    }
}