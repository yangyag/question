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
	 * ���� + ���ʽ� ���� ������ ���
	 * @param itemInfo
	 * @param uPosMsg
	 * @param diffPrice
	 * @param isAccepted
	 * @return
	 */
	@SuppressWarnings("unused")
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
					
					bonusCardNo			= uPosMsg.getBonRSCard_no();	// ���ʽ�ī�� ��ȣ
					authNo				= uPosMsg.getBonRSCard_authNo();	// ���ʽ�ī�� ���� ��ȣ
					createPoint			= uPosMsg.getGs_point1();	// �߻� ����Ʈ
					usePoint			= uPosMsg.getGs_point2();	// ���� ����Ʈ
					totalPoint			= "0";	// ������ ����Ʈ
					message				= uPosMsg.getBonRS_msg();	// ���ʽ�ī�� �޼���
					

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
	 * ���� �ŷ�ó + ���ʽ� ���� ������ ���
	 * @param itemInfo
	 * @param uPosMsg
	 * @param diffPrice
	 * @param isAccepted
	 * @return
	 */
	@SuppressWarnings("unused")
	private static String createCustomerTatsunoFormat(UPOSMessage_ItemInfo itemInfo, UPOSMessage uPosMsg, int diffPrice, boolean isAccepted) {
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
		String customerCode			= "";	// �ŷ�ó �ڵ�
		String customerName 		= "";	// �ŷ�ó��
		String customerCardNo 		= "";	// �ŷ�ó ī���ȣ
		String customerCarNo 		= "";	// �ŷ�ó ���� ��ȣ 
		String limit 				= "";	// �ѵ���
		String saveLimit 			= "";	// ���� ��뷮
		String remainLimit 			= "";	// �ѵ� �ܷ�
		String cardNumber   		= "";	// �ſ�ī���ȣ
		String acceptNo     		= "";	// �ſ�ī�� ���ι�ȣ
		String cardCorpName 		= "";	// �ſ�ī�� ����ȸ��
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
		String cachReceiptNo	 	= "";	// ���ݿ����� ������ȣ
		String cachReceiptAuthNo 	= "";	// ���ݿ����� ���ι�ȣ
		String cachReceiptMessage 	= "";	// ���ݿ����� �޼���

		boolean isCreditCard = false;		// �ſ�ī�� ����
		
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
					
					if (posMsg == null) {
						
						customerCode = "";			// �ŷ�ó�ڵ�
						customerName = "";			// �ŷ�ó ��
						customerCardNo = "";		// �ŷ�ó ī���ȣ
						customerCarNo = "";			// �ŷ�ó ���� ��ȣ
					} else {
						customerCode = dwPumpM.getCust_code();			// �ŷ�ó�ڵ�
						customerName = dwPumpM.getDrive_name();			// �ŷ�ó ��
						customerCardNo = dwPumpM.getCust_card_no();		// �ŷ�ó ī���ȣ
						customerCarNo = dwPumpM.getCar_no();			// �ŷ�ó ���� ��ȣ
					}
					
					limit = "0";									// �ѵ���
					saveLimit = "0";								// ���� ��뷮
					remainLimit = "0";								// �ѵ� �ܷ�
					
					if (IUPOSConstant.MESSAGETYPE_0032.equals(uPosMsg.getMessageType()) ||
							IUPOSConstant.MESSAGETYPE_0034.equals(uPosMsg.getMessageType())){
						cardNumber = uPosMsg.getCreditCard_no() ;	// �ſ�ī�� ī���ȣ 
						acceptNo = uPosMsg.getCredit_auth_no() ;	// �ſ�ī�� ���ι�ȣ
						cardCorpName = uPosMsg.getAcquier_code() + " " + uPosMsg.getAcquier_name() ;	//�ſ�ī�� ����ȸ�� 	
						isCreditCard = true;						//�ſ�ī�� ����
						
					}
					
					sequence = "01" ;		// ����
					productName2 = productData.getGoods_name() ;	// ��ǰ��
					nozzleNo = uPosMsg.getNozzle_no() ;				// �����ȣ
					liter = PumpMUtil.convertNumberFormatFromPumpToPOS(itemInfo.getTotalOilAmount(),3) ;	// ������
					
					if (posMsg == null) {
						basePrice = PumpMUtil.convertNumberFormatFromPumpToPOS(itemInfo.getUnitPrice(),3) ;	// �ܰ�
					} else {
						if ("1".equals(dwPumpM.getRcptunitprc_ind_prt()) )	//�ܰ� ��� ����
							basePrice = PumpMUtil.convertNumberFormatFromPumpToPOS(itemInfo.getUnitPrice(),3) ;	// �ܰ�
						else
							basePrice = "0";
					}
					price = itemInfo.getTotalOilPrice_after_discount() ;				// �����ݾ�
					productPrice = itemInfo.getTotalPrice_tax() ;						// ���ް���
					taxPrice = itemInfo.getTotalTaxPrice() ;							// ����
					totalPrice = itemInfo.getTotalOilPrice_after_discount() ;			// ���� �ݾ�
					
					receivePrice		= uPosMsg.getItem_info().getTotalOilPrice_after_discount();	// ���� �ݾ�
					
					if (!PumpMUtil.shouldSendingRejectAndReApproval(receivePrice, totalPrice, isCreditCard) )
						refund	= String.valueOf(diffPrice);	// ȯ�� �ݾ�
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

					
					bonusCardNo			= uPosMsg.getBonRSCard_no();	// ���ʽ�ī�� ��ȣ
					authNo				= uPosMsg.getBonRSCard_authNo();	// ���ʽ�ī�� ���� ��ȣ
					createPoint			= uPosMsg.getGs_point1();	// �߻� ����Ʈ
					usePoint			= uPosMsg.getGs_point2();	// ���� ����Ʈ
					totalPoint			= "0";	// ������ ����Ʈ
					message				= uPosMsg.getBonRS_msg();	// ���ʽ�ī�� �޼���
					
					if (IUPOSConstant.MESSAGETYPE_0016.equals(uPosMsg.getMessageType()) ||
							IUPOSConstant.MESSAGETYPE_0054.equals(uPosMsg.getMessageType())){
						cachReceiptNo	 	= uPosMsg.getCreditCard_no();	// ���ݿ����� ������ȣ
						cachReceiptAuthNo 	= uPosMsg.getCredit_auth_no();	// ���ݿ����� ���ι�ȣ
						cachReceiptMessage 	= uPosMsg.getVan_msg();	// ���ݿ����� �޼���
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
	 * ���� ���� ������ ���
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
			errorName = "������üũ ����";
			break;
		case 1:
			errorName = "�ſ���� ����";
			break;
		case 2:
		case 5:
			errorName = "�ſ���� ����";
			break;
		case 3:
			errorName = "������� ����";
			break;
		case 4:
			errorName = "������� ���� ";
			break;
		case 6:
			errorName = "�ŷ�ó���� ���� ";
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
	 * VAN ��� ���� ������
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
			LogUtility.getPumpMLogger().debug("[Pump M] nozInfoOptionInt : " + nozInfoOptionInt); // �׽�Ʈ �α�
			LogUtility.getPumpMLogger().debug("[Pump M] optionState : " + optionState); // �׽�Ʈ �α�
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
	 * VAN ��� ���� TimeOut �߻���
	 * @param nozzleNo			: ���� ��ȣ
	 * @param uPosMsg			: ���� ��û�� ���� ���� ���� UPOSMessage
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
	 * ���� ���� ���� GSC ǥ�� SELF ODT ��� �޽��� ������ ��û�Ѵ�.
	 * 
	 * @param nozzleNo			: ���� ��ȣ
	 * @param khproc_no			: KH ó����ȣ
	 * @param pumpingPrice		: ���� �ݾ�
	 * @param pumpingLiter		: ���� ��
	 * @param payedPrice		: ���� �ݾ�
	 * @param pumpingBasePrice	: ���� �ܰ�
	 * @return
	 */
	@SuppressWarnings("unused")
	public static String createReceiptByCash(String nozzleNo, String khproc_no, 
			String pumpingPrice, String pumpingLiter, String payedPrice, String pumpingBasePrice) {
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
	 * �ſ� ���� ��û�� ���� ���� ������ �̿��Ͽ� �پ��� ODT ������ ����� ���� �޽��� ������ ��û�Ѵ�.
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
		String formatMsg = "���� ������ �޾�����, ���� ������ ������ ���ܼ� Print ������ �����ϴ�." ;		
		
		String productName1 = "" ;	// ��ǰ��
		String storeCode = "" ;		// �����ڵ�
		String date = "" ;			// ��¥(YYYYMMDDHHMMSS)
		String tel = "" ;			// ������ȭ��ȣ
		String manager = "" ;		// �����
		String wDate = "" ;			// ��������
		String odtNo = "" ;			// ODT ��ȣ
		String transactionNo = "" ;	// �ŷ���ȣ
		String storeName = "" ;		// �����
		String representative = "" ;	// ��ǥ��
		String represent = "" ;		// ����ڹ�ȣ
		String address = "" ;		// �����ּ�
		String cardNumber = "" ;	// ī���ȣ 
		String acceptNo = "" ;		// ���ι�ȣ 
		String cardCorpName = "" ;	// ����ȸ�� 
		String sequence = "" ;		// ����
		String productName2 = "" ;	// ��ǰ��
		String nozzleNo = "" ;		// �����ȣ
		String liter = "" ;			// ������
		String basePrice = "" ;		// �ܰ�
		String price = "" ;			// �����ݾ�
		String productPrice = "" ;	// ���ް���
		String taxPrice = "" ;		// ����
		String totalPrice = "" ;	// ���� �ݾ�
		String receivePrice = "" ;	// �����ݾ�
		
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
					
					if (isAccepted) {
						if (Integer.parseInt(liter) == 0)
							receivePrice = String.valueOf(refund);
						else
							receivePrice = String.valueOf(Integer.parseInt(itemInfo.getTotalOilPrice_after_discount()) 
										+ refund);			// �����ݾ�
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
	 * �ſ� ���� ��û�� ���� ���� ������ �̿��Ͽ� �پ��� ODT ������ ����� ���� �޽��� ������ ��û�Ѵ�.
	 * 
	 * @param pumpingItemInfo	: ���� ����
	 * @param uPosMsg			: �ſ� ���� ���� ����
	 * @param refund			: POS ���� �������� �����ؾ��� �ݾ�
	 * @param isAccepted		: ������ �� ���
	 * @return
	 */
	@SuppressWarnings("unused")
	private static String createReceiptByCredit(UPOSMessage_ItemInfo pumpingItemInfo, 
			UPOSMessage uPosMsg, int refund, boolean isAccepted) {
		String formatMsg = "���� ������ �޾�����, ���� ������ ������ ���ܼ� Print ������ �����ϴ�." ;		
		
		String productName1 = "" ;	// ��ǰ��
		String storeCode = "" ;		// �����ڵ�
		String date = "" ;			// ��¥(YYYYMMDDHHMMSS)
		String tel = "" ;			// ������ȭ��ȣ
		String manager = "" ;		// �����
		String wDate = "" ;			// ��������
		String odtNo = "" ;			// ODT ��ȣ
		String transactionNo = "" ;	// �ŷ���ȣ
		String storeName = "" ;		// �����
		String representative = "" ;	// ��ǥ��
		String represent = "" ;		// ����ڹ�ȣ
		String address = "" ;		// �����ּ�
		String cardNumber = "" ;	// ī���ȣ 
		String acceptNo = "" ;		// ���ι�ȣ 
		String cardCorpName = "" ;	// ����ȸ�� 
		String sequence = "" ;		// ����
		String productName2 = "" ;	// ��ǰ��
		String nozzleNo = "" ;		// �����ȣ
		String liter = "" ;			// ������
		String basePrice = "" ;		// �ܰ�
		String price = "" ;			// �����ݾ�
		String productPrice = "" ;	// ���ް���
		String taxPrice = "" ;		// ����
		String totalPrice = "" ;	// ���� �ݾ�
		String receivePrice = "" ;	// �����ݾ�
		
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
	 * �ſ� + ���ʽ� ���� ��û�� ���� ���� ������ �̿��Ͽ� �پ��� ODT ��� �޽����� ���� ��û�Ѵ�.
	 * 
	 * @param pumpingItemInfo	: ���� ����
	 * @param uPosMsg			: ���� ���� ����
	 * @param refund			: POS ���� �������� ������� �� �ݾ�
	 * @param isAccepted		: ������ �� ���
	 * @return
	 */
	@SuppressWarnings("unused")
	private static String createReceiptByCreditBonus(UPOSMessage_ItemInfo pumpingItemInfo, 
			UPOSMessage uPosMsg, int refund, boolean isAccepted) {
		String formatMsg = "���� ������ �޾�����, ���� ������ ������ ���ܼ� Print ������ �����ϴ�." ;		
		
		String productName1 = "" ; 	// ��ǰ��
		String storeCode = "" ; 	// �����ڵ�
		String date = "" ; 			// ��¥ (YYYYMMDDHHMMSS)
		String tel = "" ; 			// ������ȭ��ȣ
		String manager = "" ; 		// �����
		String wDate = "" ; 		// ��������
		String odtNo = "" ; 		// ODT ��ȣ
		String transactionNo = "" ; // �ŷ���ȣ
		String storeName = "" ; 	// �����
		String representative = "" ;// ��ǥ��
		String represent = "" ; 	// ����ڹ�ȣ
		String address = "" ; 		// ���� �ּ�
		String cardNumber = "" ; 	// ī���ȣ
		String acceptNo = "" ; 		// ���ι�ȣ
		String cardCorpName = "" ; 	// ����ȸ��
		String sequence = "" ; 		// ����
		String productName2 = "" ; 	// ��ǰ��
		String nozzleNo = "" ; 		// ���� ��ȣ
		String liter = "" ; 		// ������
		String basePrice = "" ; 	// �ܰ�
		String price = "" ; 		// �����ݾ�
		String productPrice = "" ; 	// ���ް���
		String taxPrice = "" ; 		// ����
		String totalPrice = "" ; 	// ���� �ݾ�
		String receivePrice = "" ; 	// ���� �ݾ�
		String bonusCardNo = "" ; 	// ī��No
		String authNo = "" ; 		// ����No
		String createPoint = "" ; 	// �߻�
		String usePoint = "" ; 		// ����
		String totalPoint = "" ; 	// �Ѵ���
		String message = "" ; 		// �޼���
		
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
	 * �����Ϸ� �ݾ��� 0 ���̾ ������ ��� ���� ������ Format
	 * 
	 * @param itemInfo		: �����Ϸ� �ڷ�
	 * @param posMsg		: ������ ��� ���� ����
	 * @param refund		: ȯ�� �ݾ�				
	 * @param isAccepted	: ���� or ���� ����
	 * @return
	 */
	@SuppressWarnings("unused")
	private static String createReceiptWhenPumping0(UPOSMessage_ItemInfo pumpingItemInfo, 
			UPOSMessage uPosMsg, int refund, boolean isAccepted) {

		String formatMsg = "���� ������ �޾�����, ���� ������ ������ ���ܼ� Print ������ �����ϴ�." ;		
		
		String productName1 = "" ;		// ��ǰ��
		String storeCode = "" ;			// �����ڵ�
		String date = "" ;				// ��¥(YYYYMMDDHHMMSS)
		String tel = "" ;				// ������ȭ��ȣ
		String manager = "" ;			// �����
		String wDate = "" ;				// ��������
		String odtNo = "" ;				// ODT ��ȣ
		String transactionNo = "" ;		// �ŷ���ȣ
		String storeName = "" ;			// �����
		String representative = "" ;	// ��ǥ��
		String represent = "" ;			// ����ڹ�ȣ
		String address = "" ;			// �����ּ�
		String cardNumber = "" ;		// ī���ȣ 
		String acceptNo = "" ;			// ���ι�ȣ 
		String cardCorpName = "" ;		// ����ȸ�� 
		String sequence = "" ;			// ����
		String productName2 = "" ;		// ��ǰ��
		String nozzleNo = "" ;			// �����ȣ
		String liter = "0" ;			// ������
		String basePrice = "" ;			// �ܰ�
		String price = "0" ;			// �����ݾ�
		String productPrice = "0" ;		// ���ް���
		String taxPrice = "0" ;			// ����
		String totalPrice = "0" ;		// ���� �ݾ�
		String receivePrice = "0" ;		// �����ݾ�
		
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
					
					manager = storeData.getRep_name() ;				// �����
					wDate = getWorkingDateFormat(storeData.getBizhour_date()) ;			// ��������
					odtNo = PumpMUtil.getODTNumberFromNozzleNo(uPosMsg.getNozzle_no()) ;					
					transactionNo = uPosMsg.getPosReceipt_no() ;	// �ŷ���ȣ
					storeName = storeData.getCust_name_disp() ;		// �����
					representative = storeData.getRep_name() ;		// ��ǥ��
					represent = storeData.getBizregno_nbr() ;		// ����ڹ�ȣ
					address = storeData.getBizstoreaddr_txt1() + " " + storeData.getBizstoreaddr_txt2();	// �����ּ�					
					cardNumber = uPosMsg.getCreditCard_no() ;		// ī���ȣ
//					acceptNo = uPosMsg.getCredit_auth_no() ;		// ���ι�ȣ
//					cardCorpName = uPosMsg.getAcquier_code() + " " + uPosMsg.getAcquier_name() ;	// ����ȸ�� 					
//					sequence = "01" ;														// ����
					productName2 = productData.getGoods_name() ;							// ��ǰ��
					nozzleNo = uPosMsg.getNozzle_no() ;										// �����ȣ
//					liter = pumpingItemInfo.getTotalOilAmount() ;							// ������
					basePrice = pumpingItemInfo.getUnitPrice() ;							// �ܰ�
//					price = pumpingItemInfo.getTotalOilPrice_after_discount() ;				// �����ݾ�
//					productPrice = pumpingItemInfo.getTotalPrice_tax() ;					// ���ް���
//					taxPrice = pumpingItemInfo.getTotalTaxPrice() ;							// ����
//					totalPrice = pumpingItemInfo.getTotalOilPrice_after_discount() ;		// �����ݾ�
					
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
	 * �ܻ� �ŷ�ó ���� ������ ���
	 * @param itemInfo
	 * @param uPosMsg
	 * @param diffPrice
	 * @param isAccepted
	 * @return
	 */
	@SuppressWarnings("unused")
	private static String createTrustTatsunoFormat(UPOSMessage_ItemInfo itemInfo, UPOSMessage uPosMsg, int diffPrice, boolean isAccepted) {
		String formatMsg = "���� ������ �޾�����, ���� ������ ������ ���ܼ� Print ������ �����ϴ�." ;	
		
		String productName1		= "";	// ��ǰ��
		String storeCode		= "";	// �����ڵ� (������� ����)
		String date				= "";	// ��¥ (YYYYMMDDHHMMSS)
		String tel				= "";	// ������ȭ��ȣ
		String manager			= "";	// �����
		String wDate			= "";	// ��������
		String odtNo			= "";	// ODT ��ȣ
		String transactionNo	= "";	// �ŷ���ȣ
		String storeName		= "";	// �����
		String representative	= "";	// ��ǥ��
		String represent		= "";	// ����ڹ�ȣ
		String address			= "";	// ���� �ּ�
		int    limitType		= 0;	// �ѵ� ����(�ݾ� or ����)
		String customerCode		= "";	// �ŷ�ó �ڵ�
		String customerName 	= "";	// �ŷ�ó��
		String customerCardNo 	= "";	// �ŷ�ó ī���ȣ
		String customerCarNo 	= "";	// �ŷ�ó ���� ��ȣ 
		String limit 			= "";	// �ѵ���
		String saveLimit 		= "";	// ���� ��뷮
		String remainLimit 		= "";	// �ѵ� �ܷ�
		String sequence			= "";	// ��������
		String productName2		= "";	// ��ǰ��
		String nozzleNo			= "";	// ���� ��ȣ
		String liter			= "";	// ������
		String basePrice		= "0";	// �ܰ�
		String price			= "";	// �����ݾ�
		String productPrice		= "";	// ���� ��ǰ����
		String taxPrice			= "";	// �ΰ���
		String totalPrice		= "";	// �հ�
//		String receivePrice		= "0";	// ���� �ݾ�

		
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
					
					if ("01".equals(dwPumpM.getAdjbase_code_limit()))
						limitType = PumpMessageFormat.LITER_LIMIT_TYPE;
					else
						limitType = PumpMessageFormat.PRICE_LIMIT_TYPE;		// �ѵ����� ����
					
					customerCode = dwPumpM.getCust_code();			// �ŷ�ó�ڵ�
					customerName = dwPumpM.getDrive_name();			// �ŷ�ó ��
					customerCardNo = dwPumpM.getCust_card_no();		// �ŷ�ó ī���ȣ
					customerCarNo = dwPumpM.getCar_no();			// �ŷ�ó ���� ��ȣ
					limit = dwPumpM.getLimit();						// �ѵ���
					if ( "01".equals(dwPumpM.getCardadj_ind())){
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
					nozzleNo = uPosMsg.getNozzle_no() ;				// �����ȣ
					liter = PumpMUtil.convertNumberFormatFromPumpToPOS(itemInfo.getTotalOilAmount(),3) ;	// ������
					
					if ("1".equals(dwPumpM.getRcptunitprc_ind_prt()) )
						basePrice = PumpMUtil.convertNumberFormatFromPumpToPOS(itemInfo.getUnitPrice(),3) ;	// �ܰ�
					price = itemInfo.getTotalOilPrice_after_discount() ;				// �����ݾ�
					productPrice = itemInfo.getTotalPrice_tax() ;					// ���ް���
					taxPrice = itemInfo.getTotalTaxPrice() ;							// ����
					totalPrice = itemInfo.getTotalOilPrice_after_discount() ;		// ���� �ݾ�

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
	 * ���� �� ����� Format �� ������ �Ѵ�.
	 * ����� �Ž����� ���̳ʽ��� �̿��Ͽ� ���´�.
	 * 
	 * @param nozzleNo			: ���� ��ȣ
	 * @param khproc_no			: KH ó����ȣ
	 * @param pumpingPrice		: ���� �ݾ�
	 * @param pumpingLiter		: ������
	 * @param payedPrice		: ���� �ݾ�
	 * @param pumpingBasePrice	: ���� �ܰ�
	 * @param uPosMsg			: ���� ��û�� ���� ���� ���� UPOSMessage
	 * @return
	 */
	public static WorkingMessage getQL_WorkingMessage_GSCSELFFromODT(String nozzleNo, String khproc_no, 
			String pumpingPrice, String pumpingLiter, String payedPrice, String pumpingBasePrice, UPOSMessage uPosMsg) {
		QL_WorkingMessage qlWorkingMsg = null ;
		
		String odtNo = null ;
		String nozzle_no = uPosMsg.getNozzle_no() ;
		String receipt = null ;
		String length = "0" ;
		String mode = "1";		//pumpadapter ql������ ���� �����Ϸ� ���� 1: �����Ϸ�, 0: ���
		
		int payedPriceInt = 0 ;
		boolean isAccepted = true ;
		
		String ledCode = uPosMsg.getLed_code() ;

		// ���� �ݾ��� ������ �� ��쿡�� �ǹ̰� �ֵ��� �Ѵ�.
		if ("1".equals(ledCode)) {
			// ������ �� ���
			payedPriceInt = Integer.parseInt(payedPrice) ;
			isAccepted = true ;
		} else {
			// ������ ���� ���� ���
			payedPriceInt = 0 ;
			isAccepted = false ;
		}

		int pumpingPriceInt = Integer.parseInt(pumpingPrice) ;
		
		UPOSMessage_ItemInfo itemInfo = UPOSUtil.getUPOSMessage_ItemInfo(nozzleNo, khproc_no,
				pumpingLiter, pumpingBasePrice, pumpingPrice) ;
		
		int option = DasNoSelfPumpingManager.getInstance().getCurrentOption(nozzle_no) ;
		
		/**
		 * �����Ϸᰡ 0���̰�, �����ο� ���� ��Ұ� ����� �� ��� createReceiptWhenPumping0 
		 * �Լ��� ȣ���Ͽ� ������ ������ �����Ѵ�. 
		 * ���� �������� ������ ������ �����ݾ� 0 ��, ���� �ݾ� 0���� �������� �Ѵ�.
		 */
		if ((option == IConstant.FULL_PUMPING_OPTION_5) || (option == IConstant.FULL_PUMPING_OPTION_7)) {
		// �����Ϸ� �ݾ��� 0 ���� ��� 
			receipt = createReceiptWhenPumping0(itemInfo, uPosMsg, 0, isAccepted) ;
			length = Integer.toString(receipt.length()) ;
		} else {
		// diffPrice �� ����� ���� ����ڰ� �޾ƾ� �� �ݾ��̰�, ������ ���� �����Ұ� ����ڷ� ���� �޾ƾ� �� �ݾ��̴�.
		// ������ ���´ٶ�� ��ü�� ������ ������, ������ �ȳ������� �ڵ� ������ �����ؾ� �Ѵ�.
			int diffPrice = payedPriceInt - pumpingPriceInt ;
			
			int messageType = Integer.parseInt(uPosMsg.getMessageType()) ;
			switch (messageType) {
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
				case IUPOSConstant.MESSAGETYPE_INT_0046 :
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
				case IUPOSConstant.MESSAGETYPE_INT_0048 :	
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
				case IUPOSConstant.MESSAGETYPE_INT_0072 : { 
					// �ſ�ī�� Ȯ�� ���� --> ������ -> �߻��� ���� ����. �߻��ϸ� �ڵ��� ������.
					LogUtility.getPumpMLogger().warn("[Pump M] This Log shouldn't happen. Please check code again.") ;
					receipt = createReceiptByCredit(itemInfo, uPosMsg, diffPrice, isAccepted) ;
					length = Integer.toString(receipt.length()) ;
					break ;
				}
				case IUPOSConstant.MESSAGETYPE_INT_0082 : { 
					// �ܻ�ŷ�ó ����. --> ������ 
					receipt = createTrustTatsunoFormat(itemInfo, uPosMsg, diffPrice, isAccepted) ;
					length = Integer.toString(receipt.length()) ;
					break ;
				}
			}		
		}
		
		odtNo = PumpMUtil.getODTNumberFromNozzleNo(nozzle_no) ;	
		//qlWorkingMsg = new QL_WorkingMessage(odtNo, nozzle_no, length, receipt, "1") ; //ORG
		qlWorkingMsg = new QL_WorkingMessage(odtNo, nozzle_no, length, receipt, mode, "", "") ;
		
		//������ �������� ������OPT �������α� �μ� 
		
		qlWorkingMsg.setBarCode(Barcode.getBarcodeNumber("3", pumpingPrice, nozzleNo, khproc_no, uPosMsg.getMessageType(), ledCode, null));
		
		return qlWorkingMsg ;
	}
	
	/**
	 * �پ��� ODT ���� ������ ����� ���� WorkingMessage ������ ��û�Ѵ�. �� Method �� POS �� ������ ���� ����� ����
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
				
		//������ �������� ������OPT �������α� �μ� 
		qlWorkingMsg.setBarCode(Barcode.getBarcodeNumber("3", pumpingPrice, nozzle_no, khproc_no, null, null, null));
		
		return qlWorkingMsg ;
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
	 * UPOSMessage ���� ������ Sale M ���� �������� �Ǵ��Ѵ�.
	 * 
	 * @param uPosMsg	: UPOSMessage ���� ����
	 * @return
	 */
    public static boolean shouldSendToSaleM(UPOSMessage uPosMsg) {
    	boolean rlt = true ;

    	return rlt ;	
    }
}
