package com.gsc.kixxhub.module.pumpm.pump.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.ibatis.session.SqlSession;

import com.gsc.kixxhub.common.data.ICode;
import com.gsc.kixxhub.common.data.IConstant;
import com.gsc.kixxhub.common.data.ICustConstant;
import com.gsc.kixxhub.common.data.IUPOSConstant;
import com.gsc.kixxhub.common.data.posdata.POS_DU;
import com.gsc.kixxhub.common.data.posdata.POS_DU_Car;
import com.gsc.kixxhub.common.data.posdata.POS_DY;
import com.gsc.kixxhub.common.data.upos.UPOSMessage;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_ItemInfo;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_ItemInfo_Item;
import com.gsc.kixxhub.common.dbadapter.common.SqlSessionFactoryManager;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_BIN_INFOHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_CAR_LIMIT_INFOHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_CODEMASTERHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_CUST_CARD_INFOHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_CUST_CAR_INFOHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_CUST_INFOHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_CUST_LIMIT_INFOHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_PLHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_PL_PRICEHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_PRODUCTHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_STOREHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_CAR_LIMIT_INFOData;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_CUST_CARD_INFOData;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_CUST_CAR_INFOData;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_CUST_INFOData;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_CUST_LIMIT_INFOData;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_PLData;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_PL_PRICEData;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_PRODUCTData;
import com.gsc.kixxhub.common.dbadapter.pump.handler.T_NZ_NOZZLEHandler;
import com.gsc.kixxhub.common.dbadapter.pump.vo.T_NZ_NOZZLEData;
import com.gsc.kixxhub.common.utility.Base64Util;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.common.utility.sync.Preamble;
import com.gsc.kixxhub.common.utility.sync.SyncManager;
import com.gsc.kixxhub.module.pumpm.exception.CustException;
import com.gsc.kixxhub.module.pumpm.pump.data.cust.CustReturnValue;
import com.gsc.kixxhub.module.pumpm.pump.data.cust.LimitAmount;

public class CustUtil {
	
	
	public static String calcBasePrice(String preBasePrice, String custCdItem, String nozzleNo, String cardNumber){
		
		SqlSession session = null;
		String goods_code = "";
		String preStorePrice = "";
		boolean isDiscountKBCard = false;
		String UnitPrice_after_discount = "0";
		
		try {
			session = SqlSessionFactoryManager.openSqlSession();
			
			T_NZ_NOZZLEData nzData = T_NZ_NOZZLEHandler.getHandler().getT_NZ_NOZZLEDataByNozzleNo(session, nozzleNo)[0] ;
			goods_code = nzData.getGoods_code() ;
			preStorePrice = T_KH_PRODUCTHandler.getHandler().getBasePrice(session, goods_code, nzData.getSelf_ind_exist()) ;
		
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}
		
		//���δܰ��� ���ΰ��� ��ġ�ϰ� �ߺз� "����" 12XX �̸�
		//�������� �ŷ�ó���� Ȯ�� �ϰ� �������� �ŷ�ó�̸� ī���ȣ bin�� üũ�Ͽ� ȯ������(0333)�� ��ϵǾ� �ִ� ���� �ܰ��� �������ش�.
		if ( preBasePrice.equals(PumpMUtil.convertNumberFormatFromPOSToPump(preStorePrice,4,2)) 
				&& "12".equals(goods_code.substring(0,2)) ) {
			if (ICode.CUST_CD_ITEM_26.equals(custCdItem))
				isDiscountKBCard = CustUtil.isDiscountKBCard(cardNumber);
			
			if ( isDiscountKBCard ) {
				int discountRate = 0;
				try {
					session = SqlSessionFactoryManager.openSqlSession();
					discountRate = Integer.parseInt(
										T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(session, IConstant.POSPORTOCOL_CODEMASTER_0333));
					
				} catch (Exception e) {
					LogUtility.getPumpMLogger().debug(e.getMessage(), e);
				} finally {
					SqlSessionFactoryManager.closeSqlSession(session);
				}
				
				double unitPrice_before = Double.parseDouble(preStorePrice);
				double unitPrice_After = GlobalUtility.substract(unitPrice_before , discountRate);

				UnitPrice_after_discount = PumpMUtil.convertNumberFormatFromPOSToPump(GlobalUtility.getStringValue(unitPrice_After),4,2);
				
			}
		}
		
		
		return UnitPrice_after_discount;
	}
		
	public static UPOSMessage calcBasePrice(UPOSMessage rcvUposMsg, UPOSMessage sndUposMsg){
	
		ArrayList<UPOSMessage_ItemInfo_Item> newItemList = new ArrayList<UPOSMessage_ItemInfo_Item>();
		UPOSMessage_ItemInfo newItemInfo = new UPOSMessage_ItemInfo();
		
		String recordNo = sndUposMsg.getItem_info().getRecordNo();
		
		if (Integer.parseInt(recordNo) == 0)
			return sndUposMsg;
		
		ArrayList<UPOSMessage_ItemInfo_Item> itemList = sndUposMsg.getItem_info().getItemInfoList();
		UPOSMessage_ItemInfo_Item itemInfoItem = itemList.get(0);
		
		SqlSession session = null;
		
		int discountRate = 0;
		try {
			session = SqlSessionFactoryManager.openSqlSession();
			discountRate = Integer.parseInt(
					T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(session, IConstant.POSPORTOCOL_CODEMASTER_0333));
			
		} catch (Exception e) {
			LogUtility.getPumpMLogger().debug(e.getMessage(), e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}

		for(int i=0; i < Integer.parseInt(recordNo); i++){
			itemInfoItem = itemList.get(i);
			
			if ("12".equals(itemInfoItem.getGoodsCode().substring(0,2))){
				double unitPrice_before = Double.parseDouble(PumpMUtil.convertNumberFormatFromPumpToPOS(itemInfoItem.getUnitPrice_after_discount(),3));
				double oilAmt = Double.parseDouble(PumpMUtil.convertNumberFormatFromPumpToPOS(itemInfoItem.getOilAmount(),3));
				double unitPrice_After = GlobalUtility.substract(unitPrice_before , discountRate);
				
				CustReturnValue custReturnValue = processCustWithCustCardNo(itemInfoItem.getNozzleNo(), rcvUposMsg.getCustCard_No(), sndUposMsg.getTaxFreeCust_type());
				
				String price_After = PumpMUtil.handleRcptsheetissue_code_amtsale(unitPrice_After,oilAmt, custReturnValue.getRcptsheetissue_code_amtsale());
				
				LogUtility.getLogger().info("calcBasePrice => ���ʽ� ī�� : " + rcvUposMsg.getBonRSCard_no() );
				
				long bonRsCardNo = Long.parseLong(rcvUposMsg.getBonRSCard_no());

				// 0190-6102-3211-0000 ~ 0190-6102-3310-9999 : FDT1904001 �츮����ȭ��-���νſ�
				long cardArea01S = 190610232110000l;
				long cardArea01E = 190610233109999l;

				// 0190-6102-3311-0000 ~ 0190-6102-3320-9999 : FDT1904002 �츮����ȭ��-����üũ
				long cardArea02S = 190610233110000l;
				long cardArea02E = 190610233209999l;

				// 0190-6102-3321-0000 ~ 0190-6102-3330-9999 : FDT1904003 �츮����ȭ��-�ܻ�ŷ�
				long cardArea03S = 190610233210000l;
				long cardArea03E = 190610233309999l;

				/*
				 * �츮����ȭ�� ī���� ��� ������ �������� �����Ѵ�.
				 * */
				if((bonRsCardNo >= cardArea01S && bonRsCardNo <= cardArea01E) || (bonRsCardNo >= cardArea02S && bonRsCardNo <= cardArea02E) || (bonRsCardNo >= cardArea03S && bonRsCardNo <= cardArea03E)) {
					
					LogUtility.getLogger().info("calcBasePrice => �ش� ī��� �츮����ȭ�� ī��ν� ������ �ݿø� ó���Ͽ� ����ϵ��� �Ѵ�." );
					
					// �Ҽ��� ù°�ڸ� ���� �ݿø�
					double reOilAmt = Math.round(oilAmt);

					// �Ҽ��� ��°�ڸ� ���� �ݿø�
					//double reOilAmt = Math.round(oilAmt * 10) / 10.0;
					LogUtility.getLogger().info("calcBasePrice => ���� : " + oilAmt );
					LogUtility.getLogger().info("calcBasePrice => ���� �ݿø� : " + reOilAmt );
					
					// ������ �ݾ�(�ݿø� �� ���� * ���ʹ� ���αݾ�)
					double disPrice = reOilAmt * discountRate;
					LogUtility.getLogger().info("calcBasePrice => ������ �ݾ� : " + disPrice );
					
					// ���� �� ���ݾ�(������ �ܰ� * ����)
					double originalPrice = unitPrice_before * oilAmt;
					LogUtility.getLogger().info("calcBasePrice => �� �ݾ� : " + originalPrice );
					
					// ���� �� �ݾ� : ���� �� ���ݾ� - ���αݾ�
					double finalPrice = originalPrice - disPrice;
					LogUtility.getLogger().info("calcBasePrice => ���� �� ������ �ݾ� : " + finalPrice );
					
					// �� ���αݾ��� ������ ������ �ݾ� ����
					price_After = Double.toString(finalPrice);
				}
				
				String UnitPrice_after_discount = GlobalUtility.getStringValue(unitPrice_After);
				String OilPrice_after_discount = GlobalUtility.getStringValue(price_After);
				
				itemInfoItem.setUnitPrice_after_discount(PumpMUtil.convertNumberFormatFromPumpToUPOS(UnitPrice_after_discount));
				itemInfoItem.setOilPrice_after_discount(OilPrice_after_discount);
				
				String taxPrice = GlobalUtility.getTaxPrice(GlobalUtility.getStringValue(OilPrice_after_discount)) ;
				String price_before_tax = PumpMUtil.getPrice_before_tax(OilPrice_after_discount, taxPrice) ;
				
				itemInfoItem.setTaxPrice(taxPrice);
				itemInfoItem.setPrice_before_tax(price_before_tax);
				
				// PI2, CWI, 2016-04-27, ���հŷ�ó ���� �� ItemInfo�� ���� ���ΰ� 0���� ǥ�� �ǰ� ������, 
				// ���� �� �ܰ��� ������ �ܰ��� ���� �ϱ⿡ 1�� ���� �ǵ��� ����
				// ����ڵ�� ������ �ܰ��� ���� �� �ܰ����� ���� ��츸 ���ο��θ� 1�� ���� �Ѵ�.(2016-05-03)
				String unitDiscount_indTemp = itemInfoItem.getUnitDiscount_ind();
				try {
					double unitPrice_before_discountDou = Double.parseDouble(PumpMUtil.convertNumberFormatFromPumpToPOS(itemInfoItem.getUnitPrice_before_discount(),3)) ;
					double unitPrice_after_discountDou = Double.parseDouble(UnitPrice_after_discount) ;
					if (unitPrice_after_discountDou < unitPrice_before_discountDou) {
						unitDiscount_indTemp = "1" ;
					}
					LogUtility.getLogger().info("unitPrice_before_discountDou="+unitPrice_before_discountDou);
					LogUtility.getLogger().info("unitPrice_after_discountDou="+unitPrice_after_discountDou);
				} catch (Exception e) {
					LogUtility.getLogger().error(e.getMessage(),e) ;
				}
				itemInfoItem.setUnitDiscount_ind(unitDiscount_indTemp);
				
				newItemList.add(itemInfoItem);
				
				LogUtility.getLogger().debug("[���� �������� ī�� ] ���հŷ�ó ��ǰ���� �籸��");
				itemInfoItem.print();
			}
			// 2013.07.15 ksm  KB��ŸƮ�� ī��� 12XX(����)�� �ƴ� ���� ó����  Item ������ ���� �߻��Ͽ� ��ġ��.
			else{
				newItemList.add(itemInfoItem);
			}
		}

		newItemInfo.setRecordNo(recordNo);
		newItemInfo.setItemInfoList(newItemList);
		sndUposMsg.setItem_info(newItemInfo);

		return sndUposMsg;
	}
	
	/**
	 * 2013.05.15
	 * ���հŷ�ó �ŷ��ܰ� ����.
	 * @author ksm
	 * @param storeCd
	 * @param targetCust
	 * @param goodsCd
	 * @return
	 */
	public static String calcIntegCustBasePrice(SqlSession session, String storeCd, String targetCust, String goodsCd){
		 
		T_KH_PLData plData = null ;
		
		try{		 
			LogUtility.getCATLogger().debug("[���հŷ�ó] ��ϵ� PL �� �˻��մϴ�.cust_code_rep="+	targetCust+"#store_code="+ storeCd) ;
			
			Date date = Calendar.getInstance().getTime();
			
			String currDateYYYYMMDD = (new SimpleDateFormat("yyyyMMdd").format(date)) ;
			//T_KH_STOREHandler.getHandler().getWorkingDate() ;    
			// ���� ������ ���ؿ��� �ý��� �ð� �������� ����. POS������ �����ϰ� ����.
			LogUtility.getCATLogger().debug("[Pump M] ���հŷ�ó. �ý��۽ð� : " + currDateYYYYMMDD);
			
			T_KH_PLData[] plDataArray = 
				T_KH_PLHandler.getHandler().getT_KH_PLDataByYYYYMMDD(session, targetCust, storeCd, currDateYYYYMMDD) ;
				
			if (plDataArray != null) {
				plData = plDataArray[0] ;
				
				T_KH_PL_PRICEData[] plPrice 
						= T_KH_PL_PRICEHandler.getHandler().getT_KH_PL_PRICEData(session, targetCust, storeCd, plData.getPl_no(),goodsCd);
				
				if(plPrice != null){
					
					T_KH_PRODUCTData goodsData = T_KH_PRODUCTHandler.getHandler().getT_KH_PRODUCTData(goodsCd) ;

					String custPrice = getDiscountBasePrice(goodsData, plPrice[0]);
					
					if(custPrice != null) {
						return custPrice;
					} else {
						LogUtility.getCATLogger().debug("[���հŷ�ó] ���δܰ��� ������ ���߽��ϴ�.");
						return "";
					}
				}
			} else {
				LogUtility.getCATLogger().debug("[���հŷ�ó] PL��ȣ�� ������ ���߽��ϴ�.");
				return "";
			}
		}catch(Exception e){
			LogUtility.getCATLogger().error("[���հŷ�ó] �ŷ��ܰ����ϴٰ� Exception �߻�" + e.toString(), e);
		}
		
		return "";
	}
	
	/**
	 * PL ���̺� ���� �ŷ�ó ���� PL ������ ���� ���θ� Ȯ���Ѵ�.
	 * 
	 * @param con			: Connection
	 * @param cust_code_rep	: ��ǥ �ŷ�ó ��ȣ
	 * @param store_code	: ���� �ڵ�
	 * @param goods_code	: ��ǰ �ڵ�
	 * @return
	 * @throws Exception
	 */
	private static T_KH_PLData checkPLData(SqlSession session, String cust_code_rep, String store_code) throws Exception {
		
		T_KH_PLData plData = null ;
		
		LogUtility.getPumpMLogger().debug("[Pump M] ��ϵ� PL �� �˻��մϴ�.cust_code_rep="+ 
				cust_code_rep+"#store_code="+ store_code) ;
		
		Date date = Calendar.getInstance().getTime();
		//String currDateYYYYMMDD = T_KH_STOREHandler.getHandler().getWorkingDate() ;
		// ������ ���ؿ��� �ý��� �ð��������� ������. POS������ �����ϰ� ����.
		String currDateYYYYMMDD = (new SimpleDateFormat("yyyyMMdd").format(date)) ;
		
		T_KH_PLData[] plDataArray = 
			T_KH_PLHandler.getHandler().getT_KH_PLDataByYYYYMMDD(session, cust_code_rep, store_code, currDateYYYYMMDD) ;
		
		if (plDataArray != null) {
			plData = plDataArray[0] ;
		}
		
		LogUtility.getPumpMLogger().debug("[Pump M]  �ŷ�ó ������ PL �� ������ ������ ����.") ;
		if (plData != null) plData.print() ;
		else {
			LogUtility.getPumpMLogger().debug("[Pump M] ��ġ�ϴ� PL �� �����ϴ�.") ;
		}

		return plData ;
	}
	
	/**
	* PL .
	* Return 
	* boolean true : .
	* false : .
	* plPriceData : .
	* 
	* @param con : Connection
	* @param cust_code_rep : 
	* @param store_code : 
	* @param pl_no : PL 
	* @param goods_code : 
	* @return
	* @throws Exception
	*/
	public static T_KH_PL_PRICEData checkPLPriceData(SqlSession session, String cust_code_rep, String store_code, 
												String pl_no, String goods_code) throws Exception {
				LogUtility.getPumpMLogger().debug("[Pump M] PL ." +
												"#cust_code_rep="+cust_code_rep +
												"#store_code="+store_code +
												"#pl_no="+pl_no +
												"#goods_code="+goods_code) ;
	
		T_KH_PL_PRICEData plPriceData = null ;
		boolean rlt = false ;
	
		T_KH_PL_PRICEData[] priceDataArray = 
						T_KH_PL_PRICEHandler.getHandler().getT_KH_PL_PRICEDataWithPl_no(session, cust_code_rep, store_code, pl_no) ;
		
		if ((priceDataArray != null) && (priceDataArray.length > 0)) {
			rlt = true ; // PL
			
			for (int i = 0 ; i < priceDataArray.length ; i++) {
				String custGoodsCode = priceDataArray[i].getGoods_code() ;
				
				if (goods_code.equals(custGoodsCode)) {
					plPriceData = priceDataArray[i] ; // PL 
					break ;
				}
			}
		} else {
			rlt = false ; // PL 
		}

		if (plPriceData != null) plPriceData.print() ;
		else {
			rlt = false ;
		}
		LogUtility.getPumpMLogger().debug("[Pump M] PL ���� ������ PL ��� �ܰ��� Ȯ���� ���=" + rlt) ;
	
		return plPriceData ;
	}	
	
	
	/**
	 * �ŷ�ó ���� �����Ϳ� ��ϵ� ��ǰ �ڵ�� �� �������� ��ǰ �ڵ尡 ��ġ ���θ� Ȯ���Ѵ�.
	 * 
	 * @param productData		: ��ǰ ����
	 * @param custCarInfoData	: �ŷ�ó ���� ������ ����
	 * @return
	 */
	private static boolean compareGoodsCodeInCustCarInfoTable(T_KH_PRODUCTData productData , 
			T_KH_CUST_CAR_INFOData custCarInfoData) {
		boolean rlt = false ;

		LogUtility.getPumpMLogger().debug("[Pump M] �ŷ�ó ���� �����Ϳ� ������ ��ǰ�� ������ ��ǰ�� ���ϱ� ���� �������̴�.") ;
			
		if (custCarInfoData != null) custCarInfoData.print() ;
		if (productData != null) productData.print() ;


		if ((custCarInfoData.getGoodsclass_code_mid() == null) || (custCarInfoData.getGoodsclass_code_mid().equals(""))) {
			String main_goods_cd = custCarInfoData.getMain_goods_cd() ;
			if (main_goods_cd != null) {
				if (main_goods_cd.equals("****")) {
					rlt = true ;
				} else if (main_goods_cd.equals(productData.getGoods_code())){
					rlt = true ;
				}
			}			
		} else {
			String goodsClass_code_mid = custCarInfoData.getGoodsclass_code_mid() ;
			if (goodsClass_code_mid.equals(productData.getGoodsclass_code_mid())) {
				rlt = true ;
			}
		}

		LogUtility.getPumpMLogger().debug("[Pump M] �� ����� ������ �����ϴ�. rlt = " + rlt) ;
		return rlt ;
	}

	
	
	
	/**
	 * �ܻ���� ���� ���� ������ ��û�Ѵ�.
	 * 
	 * ���� ���� [2008.11.06] by ���῭ �����
	 * 		������ �ѵ����� ������ ó��
	 * 			- �������ѵ��ε� �ŷ�óī�忡 ������ȣ�� ������� ���� ���
	 * 
	 * @param con : Connection
	 * @param custInfoData : �ŷ�óī�� ����
	 * @param custCarInfoData : �ŷ�ó���� ������ ����
	 * @param custCardInfoData : �ŷ�óī�� ����
	 * @param productData : ��ǰ ����
	 * @param taxfreecust_type	: 0 : �鼼 �ƴ�, 1:��� �ŷ�ó , 2: �Ϲ�
	 * @return
	 * 		CustReturnValue
	 */
	private static CustReturnValue getCustReturnValueForRentCustomer(SqlSession session, T_KH_CUST_INFOData custInfoData, 
			T_KH_CUST_CAR_INFOData custCarInfoData,	T_KH_CUST_CARD_INFOData custCardInfoData, 
			T_KH_PRODUCTData productData, boolean supportGoodsCode,	String taxfreecust_type) {
		
		// Debug
			LogUtility.getPumpMLogger().debug("[Pump M] �ܻ� �� ó���� ���� �� �������Դϴ�." + "supportGoodsCode="+supportGoodsCode +
					"taxfreecust_type="+taxfreecust_type) ;
			if (custInfoData != null) {
				custInfoData.print() ;
			}
			if (custCardInfoData != null) {
				custCardInfoData.print() ;
			} else {
				LogUtility.getPumpMLogger().debug("[Pump M] custCardInfoData is null") ;
			}
			if (custCarInfoData != null) {
				custCarInfoData.print() ;
			} else {
				LogUtility.getPumpMLogger().debug("[Pump M] custCarInfoData is null") ;
			}
			if (productData != null) {
				productData.print() ;
			} else {
				LogUtility.getPumpMLogger().debug("[Pump M] productData is null") ;
			}
		
		String cust_code_rep = custInfoData.getCust_code_rep() ;
		String store_code = custInfoData.getStore_code() ;
		CustReturnValue custReturnValue = new CustReturnValue() ;

		try {
			String goods_code = productData.getGoods_code() ;
			String cardadj_ind = custInfoData.getCardadj_ind() ;

			switch (cardadj_ind) {
				case ICode.CARDADJ_IND_01 : {	// ������
					if ((supportGoodsCode == false)) {
						custReturnValue.setState(ICustConstant.STATE_33) ;		// �ܻ� �ŷ�ó - ������ - ���� ���� �ڵ� ����ġ
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// ���ΰ� ����
						
						return custReturnValue ;	// ���ݰ�-������ ��ǰ�ڵ�� ��ġ���� ����.
					}
					
					T_KH_PLData plData = checkPLData(session, cust_code_rep, store_code) ;
					if (plData == null) {
						custReturnValue.setState(ICustConstant.STATE_32) ;		// �ܻ� �ŷ�ó - ������ - PL ����
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// ���ΰ� ����
							
						return custReturnValue ;	
					}
					
/*					T_KH_PL_PRICEData plPriceData = 
						checkPLPriceData(session, cust_code_rep, store_code, plData.getPl_no(), goods_code) ;
*/					
					T_KH_PL_PRICEData plPriceData = 
						checkPLPriceData(session, cust_code_rep, store_code, plData.getPl_no(), goods_code) ;


					if ((plPriceData == null)) {
						custReturnValue.setState(ICustConstant.STATE_31) ;		// �ܻ� �ŷ�ó - ������ - ��ǰ�ڵ� ����ġ 
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// ���ΰ� ����
						
						return custReturnValue ;	
					} 			

					if (taxfreecust_type.equals(ICode.DX_TAXFREECUST_TYPE_1)) {
						if (!plPriceData.getTaxfree_cd().equals(ICode.TAXFREE_CD_02)) {
							LogUtility.getPumpMLogger().debug("[Pump M] �鼼�� ��û�� ����, PL ���ܰ��� ������ �����Ǿ� �ֽ��ϴ�.") ;
							custReturnValue.setState(ICustConstant.STATE_35) ;		// �鼼�� ��û�ߴµ� �鼼 ���� �ƴ�.
							custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// ���ΰ� ����
							
							return custReturnValue ;					
						}
					}
					
					String discountBasePrice = getDiscountBasePrice(productData, plPriceData) ;
					
					if (discountBasePrice == null) {
						custReturnValue.setState(ICustConstant.STATE_34) ;		// �ܻ� �ŷ�ó - ������ - ������ �ܰ��� �������� ����
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// ���ΰ� ����
						
						return custReturnValue ;
					}
					
					custReturnValue.setUnitprc_code_stdn(plPriceData.getUnitprc_code_stdn()) ;
					custReturnValue.setTaxfree_cd(plPriceData.getTaxfree_cd());
					custReturnValue.setState(ICustConstant.STATE_30) ;		// �ܻ� �ŷ�ó - ������ - ����
					custReturnValue.setType(ICustConstant.TYPE_CUST_BASEPRICE) ;	// �ܰ� ����
					custReturnValue.setDiscountBasePrice(discountBasePrice) ;
						
					return custReturnValue ;	
				}
				case ICode.CARDADJ_IND_02 : {	// �ŷ�ó��
					if ((supportGoodsCode == false)) {
						custReturnValue.setState(ICustConstant.STATE_53) ;		// �ܻ� �ŷ�ó - �ŷ�ó���ѵ� - ���� ���� �ڵ� ����ġ
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// ���ΰ� ����
						
						return custReturnValue ;	
					}
					
					T_KH_PLData plData = checkPLData(session, cust_code_rep, store_code) ;
					if (plData == null) {
						custReturnValue.setState(ICustConstant.STATE_52) ;		// �ܻ� �ŷ�ó - �ŷ�ó���ѵ� - PL ����
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// ���ΰ� ����
							
						return custReturnValue ;	
					}
					
					
					/*	T_KH_PL_PRICEData plPriceData = 
							checkPLPriceData(session, cust_code_rep, store_code, plData.getPl_no(), goods_code) ;
					 */					
					T_KH_PL_PRICEData plPriceData = 
						checkPLPriceData(session, cust_code_rep, store_code, plData.getPl_no(), goods_code) ;
		

					if (plPriceData == null) {
						custReturnValue.setState(ICustConstant.STATE_51) ;		// �ܻ� �ŷ�ó - �ŷ�ó���ѵ� - ��ǰ�ڵ� ����ġ 
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// ���ΰ� ����
						
						return custReturnValue ;	
					} 			

					if (taxfreecust_type.equals(ICode.DX_TAXFREECUST_TYPE_1)) {
						if (!plPriceData.getTaxfree_cd().equals(ICode.TAXFREE_CD_02)) {
							LogUtility.getPumpMLogger().debug("[Pump M] �鼼�� ��û�� ����, PL ���ܰ��� ������ �����Ǿ� �ֽ��ϴ�.") ;
							custReturnValue.setState(ICustConstant.STATE_55) ;		// �鼼�� ��û�ߴµ� �鼼 ���� �ƴ�.
							custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// ���ΰ� ����
							
							return custReturnValue ;					
						}
					}
					
					LimitAmount limitAmount = null ;
					int priceOrLiter = 0 ;
					String limit = "" ;
					String usedAmount = "" ;
					String discountBasePrice = getDiscountBasePrice(productData, plPriceData) ;		
					T_KH_CUST_LIMIT_INFOData tKhLimitInfo = 
						getT_KH_CUST_LIMIT_INFOData(session, custInfoData, productData) ;

					if (tKhLimitInfo == null) {
						// �ŷ�ó�� �ѵ� ���̺� ������ ���� ��� ������ ó��
						custReturnValue.setState(ICustConstant.STATE_56) ;		// �ܻ� �ŷ�ó - �ŷ�ó���ѵ� - �ŷ�ó�� �ѵ����̺� ������ ���� ���
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;
						
						return custReturnValue ;	
					}

					
					if (discountBasePrice == null) {
						custReturnValue.setState(ICustConstant.STATE_54) ;		// �ܻ� �ŷ�ó - �ŷ�ó���ѵ� - ������ �ܰ��� �������� ����
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// ���ΰ� ����
						
						return custReturnValue ;
					}

					String cardno_nbr = custCardInfoData.getCarno_nbr() ;
					
					if ((cardno_nbr == null) || ("".equals(cardno_nbr))) {
						custReturnValue.setUnitprc_code_stdn(plPriceData.getUnitprc_code_stdn()) ;
						custReturnValue.setTaxfree_cd(plPriceData.getTaxfree_cd());
						custReturnValue.setState(ICustConstant.STATE_30) ;				// �ܻ� �ŷ�ó - ������ - ����
						custReturnValue.setType(ICustConstant.TYPE_CUST_BASEPRICE) ;	// �ܰ� ����
						custReturnValue.setDiscountBasePrice(discountBasePrice) ;
						
						return custReturnValue ;
					}
					
					// �ŷ�ó�� �ѵ� ���̺� ���� ���� Ȯ�� - ����� ���� ������ �Է�
					String cust_code = custCardInfoData.getCust_code() ;
					String carno_nbr = custCardInfoData.getCarno_nbr() ;
					T_KH_CAR_LIMIT_INFOData carLimitInfoData = null ;
					
					if ((carno_nbr != null) && (!carno_nbr.equals(""))) {
						T_KH_CAR_LIMIT_INFOData[] carLimitInfoDataArray = 
							T_KH_CAR_LIMIT_INFOHandler.getHandler().getT_KH_CAR_LIMIT_INFOData(session, cust_code, carno_nbr, goods_code) ;
						
						if ((carLimitInfoDataArray != null) && (carLimitInfoDataArray.length != 0)) {
							carLimitInfoData = carLimitInfoDataArray[0] ;
							LogUtility.getPumpMLogger().debug("[Pump M] �ŷ�ó �ѵ� �������� ������ �ѵ��� ��ϵǾ� �ֽ��ϴ�. ������ �Ʒ��� �����ϴ�.") ;
							carLimitInfoData.print() ;
						}
					}
					
					if (carLimitInfoData == null) {
						if (tKhLimitInfo.getAdjbase_code_limit().equals(ICode.ADJBASE_CODE_LIMIT_01)) {
							// ���� 
							priceOrLiter = ICustConstant.LIMIT_AMOUNT_LITER ;
							limit = tKhLimitInfo.getLimit_cnt_monthly() ;
							usedAmount = tKhLimitInfo.getAcclimit_cnt_monthly() ;
						} else {
							// �ܰ�
							priceOrLiter = ICustConstant.LIMIT_AMOUNT_PRICE ;
							limit = tKhLimitInfo.getLimit_amt_monthly() ;
							usedAmount = tKhLimitInfo.getAcclimit_amt_monthly() ;
						}
						limitAmount = new LimitAmount(priceOrLiter,limit,usedAmount) ;
							
						custReturnValue.setUnitprc_code_stdn(plPriceData.getUnitprc_code_stdn()) ;
						custReturnValue.setTaxfree_cd(plPriceData.getTaxfree_cd());
						custReturnValue.setState(ICustConstant.STATE_50) ;			// �ܻ� �ŷ�ó - �ŷ�ó���ѵ� - ����
						custReturnValue.setType(ICustConstant.TYPE_CUST_LIMIT) ;	// �ŷ�ó��
						custReturnValue.setDiscountBasePrice(discountBasePrice) ;
						custReturnValue.setLimitAmount(limitAmount) ;
						
						return custReturnValue ;	
					} else {
						LogUtility.getPumpMLogger().debug("[Pump M] ���� �ѵ��� ��ϵ� �ŷ�ó �ѵ� ���Դϴ�.�̿� ���� ó���� �ǽ����ϴ�.") ;
						String remainsAmount = "" ;
						String custLimitCodeLimit = tKhLimitInfo.getAdjbase_code_limit() ;
						if (custLimitCodeLimit.equals(ICode.ADJBASE_CODE_LIMIT_01)) {
							// ���� 
							priceOrLiter = ICustConstant.LIMIT_AMOUNT_LITER ;
							limit = tKhLimitInfo.getLimit_cnt_monthly() ;
							usedAmount = tKhLimitInfo.getAcclimit_cnt_monthly() ;
							remainsAmount = GlobalUtility.substract(limit, usedAmount) ;
						} else {
							// �ݾ�
							priceOrLiter = ICustConstant.LIMIT_AMOUNT_PRICE ;
							limit = tKhLimitInfo.getLimit_amt_monthly() ;
							usedAmount = tKhLimitInfo.getAcclimit_amt_monthly() ;
							remainsAmount = GlobalUtility.substract(limit, usedAmount) ;
						}						
						
						if (carLimitInfoData.getAdjbase_code_limit().equals(ICode.ADJBASE_CODE_LIMIT_01)) {
							// ���� 
							priceOrLiter = ICustConstant.LIMIT_AMOUNT_LITER ;
							limit = carLimitInfoData.getLimit_cnt_monthly() ;
							usedAmount = carLimitInfoData.getAcclimit_cnt_monthly() ;
							if (custLimitCodeLimit.equals(ICode.ADJBASE_CODE_LIMIT_02)) {
								remainsAmount = GlobalUtility.divide(remainsAmount,discountBasePrice) ;
							}
						} else {
							// �ݾ�
							priceOrLiter = ICustConstant.LIMIT_AMOUNT_PRICE ;
							limit = carLimitInfoData.getLimit_amt_monthly() ;
							usedAmount = carLimitInfoData.getAcclimit_amt_monthly() ;
							if (custLimitCodeLimit.equals(ICode.ADJBASE_CODE_LIMIT_01)) {
								remainsAmount = GlobalUtility.multiple(remainsAmount,discountBasePrice) ;
							}
						}
						limitAmount = new LimitAmount(priceOrLiter,limit,usedAmount,remainsAmount) ;
							
						custReturnValue.setUnitprc_code_stdn(plPriceData.getUnitprc_code_stdn()) ;
						custReturnValue.setTaxfree_cd(plPriceData.getTaxfree_cd());
						custReturnValue.setState(ICustConstant.STATE_50) ;			// �ܻ� �ŷ�ó - �ŷ�ó���ѵ� - ����
						custReturnValue.setType(ICustConstant.TYPE_CUST_LIMIT) ;	// �ŷ�ó��
						custReturnValue.setDiscountBasePrice(discountBasePrice) ;
						custReturnValue.setLimitAmount(limitAmount) ;
						
						return custReturnValue ;	
					}
				}
				case ICode.CARDADJ_IND_03 : {	// ������
					if ((supportGoodsCode == false)) {
						custReturnValue.setState(ICustConstant.STATE_43) ;		// �ܻ� �ŷ�ó - �������ѵ� - ���� ���� �ڵ� ����ġ
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// ���ΰ� ����
						
						return custReturnValue ;	
					}
					
					T_KH_PLData plData = checkPLData(session, cust_code_rep, store_code) ;
					if (plData == null) {
						custReturnValue.setState(ICustConstant.STATE_42) ;		// �ܻ� �ŷ�ó - �������ѵ� - PL ����
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// ���ΰ� ����
							
						return custReturnValue ;		
					}
					
					/*	T_KH_PL_PRICEData plPriceData = 
							checkPLPriceData(session, cust_code_rep, store_code, plData.getPl_no(), goods_code) ;
					*/					
					T_KH_PL_PRICEData plPriceData = 
						checkPLPriceData(session, cust_code_rep, store_code, plData.getPl_no(), goods_code) ;

					if (plPriceData == null) {
						custReturnValue.setState(ICustConstant.STATE_41) ;		// �ܻ� �ŷ�ó - �������ѵ� - ��ǰ�ڵ� ����ġ 
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// ���ΰ� ����
						
						return custReturnValue ;	
					} 			

					if (taxfreecust_type.equals(ICode.DX_TAXFREECUST_TYPE_1)) {
						if (!plPriceData.getTaxfree_cd().equals(ICode.TAXFREE_CD_02)) {
							LogUtility.getPumpMLogger().debug("[Pump M] �鼼�� ��û�� ����, PL ���ܰ��� ������ �����Ǿ� �ֽ��ϴ�.") ;
							custReturnValue.setState(ICustConstant.STATE_45) ;		// �鼼�� ��û�ߴµ� �鼼 ���� �ƴ�.
							custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// ���ΰ� ����
							
							return custReturnValue ;					
						}
					}
					
					T_KH_CAR_LIMIT_INFOData tKhLimitInfo = null ;
					String carno_nbr = null ;
					
					if (custCarInfoData != null)
						carno_nbr = custCarInfoData.getCarno_nbr() ;
					if ((carno_nbr == null) || ("".equals(carno_nbr))) {
						
					} else {
						tKhLimitInfo = 
							getT_KH_CAR_LIMIT_INFOData(session, custCarInfoData, productData) ;
					}
					
					/**
					 * ��������
					 * ������ �ѵ� ���̺� ������ ���� ��� ���������� ó��
					 * 
					 * �߰� - [2008.11.06] by ���῭ �����.
					 * 		������ �ѵ����� ������ ó��
					 * 			- ������ �ѵ��ε� �ŷ�óī�忡 ������ȣ�� ������� ���� ���
					 * 
					 */
					if ((tKhLimitInfo == null) || (carno_nbr == null) || ("".equals(carno_nbr))) {
						LogUtility.getPumpMLogger().info("[Pump M] ������ �ѵ� ���̺� Ȥ�� ���� ��ȣ�� �ŷ�ó ī�忡 ���� ���� �ʾƼ� " +
								"������ �ŷ�ó ó�� ó���Ѵ�.") ;
						String discountBasePrice = getDiscountBasePrice(productData, plPriceData) ;
						
						if (discountBasePrice == null) {
							custReturnValue.setState(ICustConstant.STATE_34) ;		// �ܻ� �ŷ�ó - ������ - ������ �ܰ��� �������� ����
							custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// ���ΰ� ����
							
							return custReturnValue ;
						}
						custReturnValue.setUnitprc_code_stdn(plPriceData.getUnitprc_code_stdn()) ;
						custReturnValue.setTaxfree_cd(plPriceData.getTaxfree_cd());
						custReturnValue.setState(ICustConstant.STATE_30) ;		// �ܻ� �ŷ�ó - ������ - ����
						custReturnValue.setType(ICustConstant.TYPE_CUST_BASEPRICE) ;	// �ܰ� ����
						custReturnValue.setDiscountBasePrice(discountBasePrice) ;
							
						return custReturnValue ;	
					}
					
					LimitAmount limitAmount = null ;
					int priceOrLiter = 0 ;
					String limit = "" ;
					String usedAmount = "" ;
					String discountBasePrice = getDiscountBasePrice(productData, plPriceData) ;	
					
					if (discountBasePrice == null) {
						custReturnValue.setState(ICustConstant.STATE_44) ;		// �ܻ� �ŷ�ó - �������ѵ� - ������ �ܰ��� �������� ����
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// ���ΰ� ����
						
						return custReturnValue ;
					}

					if (tKhLimitInfo.getAdjbase_code_limit().equals(ICode.ADJBASE_CODE_LIMIT_01)) {
						// ���� 
						priceOrLiter = ICustConstant.LIMIT_AMOUNT_LITER ;
						limit = tKhLimitInfo.getLimit_cnt_monthly() ;
						usedAmount = tKhLimitInfo.getAcclimit_cnt_monthly() ;
					} else {
						// �ܰ�
						priceOrLiter = ICustConstant.LIMIT_AMOUNT_PRICE ;
						limit = tKhLimitInfo.getLimit_amt_monthly() ;
						usedAmount = tKhLimitInfo.getAcclimit_amt_monthly() ;
					}
					limitAmount = new LimitAmount(priceOrLiter,limit,usedAmount) ;
					
					custReturnValue.setUnitprc_code_stdn(plPriceData.getUnitprc_code_stdn()) ;
					custReturnValue.setTaxfree_cd(plPriceData.getTaxfree_cd());
					custReturnValue.setState(ICustConstant.STATE_40) ;		// �ܻ� �ŷ�ó - �������ѵ� - ����
					custReturnValue.setType(ICustConstant.TYPE_CAR_LIMIT) ;	// ������
					custReturnValue.setDiscountBasePrice(discountBasePrice) ;
					custReturnValue.setLimitAmount(limitAmount) ;
							
					return custReturnValue ;
				}
				case ICode.CARDADJ_IND_04 : {		// 1ȸ ����
					if ((supportGoodsCode == false)) {
						custReturnValue.setState(ICustConstant.STATE_63) ;		// �ܻ� �ŷ�ó - 1ȸ ���� - ���� ���� �ڵ� ����ġ
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// ���ΰ� ����
						
						return custReturnValue ;	
					}
					
					if (custCardInfoData == null) {
						LogUtility.getPumpMLogger().warn("[Pump M] ���� ��ȣ�� ��ϵ� ���� 1ȸ ������ ���� �� ����. " +
							"������ 1ȸ �������� ��� �Ǿ� �ֱ� ������, �̿� ���ؼ� ���縦 �ؾ� �Ѵ�.") ;
						custReturnValue.setState(ICustConstant.STATE_62) ;		// �ܻ� �ŷ�ó - 1 ȸ ���� - PL ����
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// ���ΰ� ����
						
						return custReturnValue ;	
					} 
					
					T_KH_PLData plData = checkPLData(session, cust_code_rep, store_code) ;
					if (plData == null) {
						custReturnValue.setState(ICustConstant.STATE_62) ;		// �ܻ� �ŷ�ó - 1 ȸ ���� - PL ����
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// ���ΰ� ����
						
						return custReturnValue ;	
					}
					
					/*	T_KH_PL_PRICEData plPriceData = 
							checkPLPriceData(session, cust_code_rep, store_code, plData.getPl_no(), goods_code) ;
					 */					
					T_KH_PL_PRICEData plPriceData = 
						checkPLPriceData(session, cust_code_rep, store_code, plData.getPl_no(), goods_code) ;


					if (plPriceData == null) {
						custReturnValue.setState(ICustConstant.STATE_61) ;		// �ܻ� �ŷ�ó - 1 ȸ ���� - ��ǰ�ڵ� ����ġ 
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// ���ΰ� ����
						
						return custReturnValue ;	
					} 			

					if (taxfreecust_type.equals(ICode.DX_TAXFREECUST_TYPE_1)) {
						if (!plPriceData.getTaxfree_cd().equals(ICode.TAXFREE_CD_02)) {
							LogUtility.getPumpMLogger().debug("[Pump M] �鼼�� ��û�� ����, PL ���ܰ��� ������ �����Ǿ� �ֽ��ϴ�.") ;
							custReturnValue.setState(ICustConstant.STATE_65) ;		// �鼼�� ��û�ߴµ� �鼼 ���� �ƴ�.
							custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// ���ΰ� ����
							
							return custReturnValue ;					
						}
					}
					
					custReturnValue.setAmount1(custCardInfoData.getSliplimit_amt_stdcapa()) ;

					String discountBasePrice = getDiscountBasePrice(productData, plPriceData) ;
					
					if (discountBasePrice == null) {
						custReturnValue.setState(ICustConstant.STATE_64) ;		// �ܻ� �ŷ�ó - 1ȸ ���� - ������ �ܰ��� �������� ����
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// ���ΰ� ����
						
						return custReturnValue ;
					}
					custReturnValue.setUnitprc_code_stdn(plPriceData.getUnitprc_code_stdn()) ;
					custReturnValue.setTaxfree_cd(plPriceData.getTaxfree_cd());
					custReturnValue.setState(ICustConstant.STATE_60) ;		// �ܻ� �ŷ�ó - 1 ȸ ���� - ����
					custReturnValue.setType(ICustConstant.TYPE_CUST_BASEPRICE) ;	// �ܰ� ����
					custReturnValue.setDiscountBasePrice(discountBasePrice) ;
						
					return custReturnValue ;	
				}
				case ICode.CARDADJ_IND_05 : {	// ���� �Է�
					if ((supportGoodsCode == false)) {
						custReturnValue.setState(ICustConstant.STATE_93) ;		// �ܻ� �ŷ�ó - �����Է� - ���� ���� �ڵ� ����ġ
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// ���ΰ� ����
						
						return custReturnValue ;	
					}
					
					T_KH_PLData plData = checkPLData(session, cust_code_rep, store_code) ;
					if (plData == null) {
						custReturnValue.setState(ICustConstant.STATE_92) ;		// �ܻ� �ŷ�ó - �����Է� - PL ����
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// ���ΰ� ����
						
						return custReturnValue ;	
					}

					/*	T_KH_PL_PRICEData plPriceData = 
					checkPLPriceData(session, cust_code_rep, store_code, plData.getPl_no(), goods_code) ;
					 */					
					T_KH_PL_PRICEData plPriceData = 
						checkPLPriceData(session, cust_code_rep, store_code, plData.getPl_no(), goods_code) ;


					if (plPriceData == null) {
						custReturnValue.setState(ICustConstant.STATE_91) ;		// �ܻ� �ŷ�ó - �����Է� - ��ǰ�ڵ� ����ġ 
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// ���ΰ� ����
						
						return custReturnValue ;	
					} 			

					if (taxfreecust_type.equals(ICode.DX_TAXFREECUST_TYPE_1)) {
						if (!plPriceData.getTaxfree_cd().equals(ICode.TAXFREE_CD_02)) {
							LogUtility.getPumpMLogger().debug("[Pump M] �鼼�� ��û�� ����, PL ���ܰ��� ������ �����Ǿ� �ֽ��ϴ�.") ;
							custReturnValue.setState(ICustConstant.STATE_95) ;		// �鼼�� ��û�ߴµ� �鼼 ���� �ƴ�.
							custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// ���ΰ� ����
							
							return custReturnValue ;					
						}
					}
					
					String discountBasePrice = getDiscountBasePrice(productData, plPriceData) ;

					if (discountBasePrice == null) {
						custReturnValue.setState(ICustConstant.STATE_94) ;		// �ܻ� �ŷ�ó - �����Է� - ������ �ܰ��� �������� ����
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// ���ΰ� ����
						
						return custReturnValue ;
					}
					
					custReturnValue.setUnitprc_code_stdn(plPriceData.getUnitprc_code_stdn()) ;
					custReturnValue.setTaxfree_cd(plPriceData.getTaxfree_cd());
					custReturnValue.setState(ICustConstant.STATE_90) ;		// �ܻ� �ŷ�ó - �����Է�  - ����
					custReturnValue.setType(ICustConstant.TYPE_CUST_BASEPRICE) ;	// �ܰ� ����
					custReturnValue.setDiscountBasePrice(discountBasePrice) ;
					
					return custReturnValue ;	
				}
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		return custReturnValue ;
	}

	
	/**
	 * ���� �ŷ�ó ���� ���� ���� ������ ��û�Ѵ�.
	 * 
	 * @param con : Connection 
	 * @param custInfoData : �ŷ�ó ����
	 * @param productData : ��ǰ���̺� ����
	 * @param nzNozzleData : ���� ����
	 * @param supportGoodsCode : ���� �����Ϳ� ���� ���� ��ġ ����
	 * @param taxfreecust_type	: 0 : �鼼 �ƴ�, 1:��� �ŷ�ó , 2: �Ϲ�
	 * @return
	 * 		CustReturnValue
	 */
	private static CustReturnValue getCustReturnValueForVIP(	SqlSession session, 
																T_KH_CUST_INFOData custInfoData, 
																T_KH_PRODUCTData productData, 
																boolean supportGoodsCode, 
																String taxfreecust_type) {

		// Debug
			LogUtility.getPumpMLogger().debug("[Pump M] VIP �� ó���� ���� �� �������Դϴ�." + "supportGoodsCode="+
													supportGoodsCode+"#taxfreecust_type="+taxfreecust_type) ;
			if (custInfoData != null) {
				custInfoData.print() ;
			}
			if (productData != null) {
				productData.print() ;
			} else {
				LogUtility.getPumpMLogger().debug("[Pump M] productData is null") ;
			}
		
		String cust_code_rep = custInfoData.getCust_code_rep() ; 	// �ŷ�ó ��ǥ �ڵ� - PL ���� üũ�� ����Ѵ�.
		String store_code = custInfoData.getStore_code() ;
		CustReturnValue custReturnValue = new CustReturnValue() ;
		String goods_code = productData.getGoods_code() ;

		try {
			if ((supportGoodsCode == false)) {
				custReturnValue.setState(ICustConstant.STATE_13) ;		// ���� �ŷ�ó - ���� ���� �ڵ� ����ġ
				custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	
				
				return custReturnValue ;
			}
			
			T_KH_PLData plData = checkPLData(session, cust_code_rep, store_code) ;
			if (plData == null) {
				custReturnValue.setState(ICustConstant.STATE_12) ;		// PL ���� ����
				custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	
						
				return custReturnValue ;
			}
			
			/*					T_KH_PL_PRICEData plPriceData = 
			checkPLPriceData(con, cust_code_rep, store_code, plData.getPl_no(), goods_code) ;
*/					
			T_KH_PL_PRICEData plPriceData = checkPLPriceData(session, cust_code_rep, store_code, plData.getPl_no(), goods_code) ;

			
			if (plPriceData == null) {
				custReturnValue.setState(ICustConstant.STATE_11) ;		//  PL ��ǰ �ڵ� ����ġ
				custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	
				
				return custReturnValue ;
			}
			
			if (taxfreecust_type.equals(ICode.DX_TAXFREECUST_TYPE_1)) {
				if (!plPriceData.getTaxfree_cd().equals(ICode.TAXFREE_CD_02)) {
					LogUtility.getPumpMLogger().debug("[Pump M] �鼼�� ��û�� ����, PL ���ܰ��� ������ �����Ǿ� �ֽ��ϴ�.") ;
					custReturnValue.setState(ICustConstant.STATE_15) ;		// ���� �ŷ�ó - �鼼�� ��û�ߴµ� �鼼 ���� �ƴ�.
					custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	
					
					return custReturnValue ;					
				}
			}
			
			String discountBasePrice = getDiscountBasePrice(productData, plPriceData) ;
			
			if (discountBasePrice == null) {
				custReturnValue.setState(ICustConstant.STATE_14) ;		// ���� �ŷ�ó - ������ �ܰ��� �������� ����
				custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	
				
				return custReturnValue ;
			}

			custReturnValue.setUnitprc_code_stdn(plPriceData.getUnitprc_code_stdn()) ;
			custReturnValue.setTaxfree_cd(plPriceData.getTaxfree_cd());
			custReturnValue.setState(ICustConstant.STATE_10) ;		// ����
			custReturnValue.setType(ICustConstant.TYPE_CUST_BASEPRICE) ;	// ���δܰ� ����
			custReturnValue.setDiscountBasePrice(discountBasePrice) ;		//���� �ܰ�
			custReturnValue.setCust_code(cust_code_rep);	//�ŷ�ó �ڵ�
			
			// 2013.10.14 ksm ���ݰŷ�ó�̸� ���� ���δܰ� ������� ��� ���ο��� ����.
			custReturnValue.setPlunit_yn("1");
			
			return custReturnValue ;	// ���ݰ� - �̻� 
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		return null ;
	}

	/**
	 * ���� (or ���ε��� ����) �ܰ� ����� ��û�Ѵ�.
	 * 
	 * @param productData : ��ǰ ����
	 * @param tKhPLPrice : PL ���� ����
	 * @return
	 * 		���� �ܰ�
	 */
	private static String getDiscountBasePrice(T_KH_PRODUCTData productData , T_KH_PL_PRICEData tKhPLPrice) {
		
		// Debug purpose
			LogUtility.getPumpMLogger().debug("[Pump M] ���δܰ��� ���Ϸ��� �մϴ�. ���� �ܰ��� ���ϱ� ���� ������ ������ �����ϴ�.") ;
			productData.print() ;
			tKhPLPrice.print() ;
		
		String discountBaesPrice = null ;
		String contcond_code = tKhPLPrice.getContcond_code() ;
		int contcond_codeInt = Integer.parseInt(contcond_code) ;
		String basePrice = null ;
		String unitprc_code_stdn = null ;
		
		switch (contcond_codeInt) {			
			case 1 : {
				// �� 
				unitprc_code_stdn = tKhPLPrice.getUnitprc_code_stdn() ;
				basePrice = PumpMUtil.getBasePriceWithUnitPrcCode(unitprc_code_stdn, productData) ;
				
				if (basePrice == null) return null ;
				
				String dscnt_code_type = tKhPLPrice.getDscnt_code_type() ;
				String dscnt_val_rate = tKhPLPrice.getDscnt_val_rate() ;
				String digit_code_decpoint_discontrate = tKhPLPrice.getDigit_code_decpoint_discntrate() ;
				String way_code_decpoint_discontrate = tKhPLPrice.getWay_code_decpoint_discntrate() ;
				
				double dscnt_val_rateDouble = Double.parseDouble(dscnt_val_rate) ;
				double basePriceDouble = Double.parseDouble(basePrice) ;
				double discountBaesPriceDouble = 0 ;
				if (dscnt_code_type.equals(ICode.DSCNT_CODE_TYPE_01)) {
					// ����
					discountBaesPriceDouble = GlobalUtility.substract(basePriceDouble , (basePriceDouble * dscnt_val_rateDouble * 0.01)) ;
				} else {
					// ����
					discountBaesPriceDouble = GlobalUtility.plus(basePriceDouble ,(basePriceDouble * dscnt_val_rateDouble * 0.01)) ;
				}
				
				// ������ �Ҽ��� ó����� ����
				discountBaesPriceDouble = GlobalUtility.getCodeDecPoint(discountBaesPriceDouble , digit_code_decpoint_discontrate, way_code_decpoint_discontrate) ;
				discountBaesPrice = Double.toString(discountBaesPriceDouble) ;
				break ;
			}
			case 2 : {
				// �ݾ�
				unitprc_code_stdn = tKhPLPrice.getUnitprc_code_stdn() ;
				basePrice = PumpMUtil.getBasePriceWithUnitPrcCode(unitprc_code_stdn, productData) ;
				
				if (basePrice == null) return null ;
				
				String dscnt_code_type = tKhPLPrice.getDscnt_code_type() ;
				String dscnt_amt = tKhPLPrice.getDscnt_amt() ;
				
				double basePriceDouble = Double.parseDouble(basePrice) ;
				double dscnt_amtDouble = Double.parseDouble(dscnt_amt) ;
				double discountBaesPriceDouble = 0 ;
				if (dscnt_code_type.equals(ICode.DSCNT_CODE_TYPE_01)) {
					// ����
					discountBaesPriceDouble = GlobalUtility.substract(basePriceDouble , dscnt_amtDouble) ;
				} else {
					// ����
					discountBaesPriceDouble = GlobalUtility.plus(basePriceDouble , dscnt_amtDouble) ;
				}
				discountBaesPrice = Double.toString(discountBaesPriceDouble) ;
				break ;
			}
			case 3 : {
				// �����ܰ�
				discountBaesPrice = tKhPLPrice.getPrc_amt_agree() ;
				break ;
			}
		}
		
		return GlobalUtility.getStringValue(discountBaesPrice) ;
	}
	
	/**
	 * ������ �ѵ� ������ ������ �ѵ� �����ͷκ��� �����´�.
	 * 
	 * @param con			: Connection
	 * @param custCarInfo	: ���� ������ ����
	 * @param productData	: ������ ������ ��ǰ ����
	 * @return
	 */
	private static T_KH_CAR_LIMIT_INFOData getT_KH_CAR_LIMIT_INFOData(SqlSession session, T_KH_CUST_CAR_INFOData custCarInfo, 
			T_KH_PRODUCTData productData) {
		
		T_KH_CAR_LIMIT_INFOData carLimitInfoData = null ;

		LogUtility.getPumpMLogger().debug("[Pump M] ������ �ѵ� ������ ��û�ϱ� ���� �����͵��̴�.") ;			
		if (custCarInfo != null) {
			custCarInfo.print() ;
		} else {
			LogUtility.getPumpMLogger().debug("[Pump M] custCarInfo=" + custCarInfo );
		}		
		
		if (custCarInfo == null) return null ;

		try {
			T_KH_CAR_LIMIT_INFOData[] carLimitInfoDataArray = 
				T_KH_CAR_LIMIT_INFOHandler.getHandler().getT_KH_CAR_LIMIT_INFOData(session, 
									custCarInfo.getCust_code(), custCarInfo.getCarno_nbr());
						
			if ((carLimitInfoDataArray == null) || (carLimitInfoDataArray.length == 0)) {
				carLimitInfoData =  null ;
			} else {
				carLimitInfoData = carLimitInfoDataArray[0] ;
			}

		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}

		LogUtility.getPumpMLogger().debug("[Pump M] ������ �ѵ� ��������� ������ �����ϴ�. ") ;			
		if (carLimitInfoData != null) {
			carLimitInfoData.print() ;
		} else {
			LogUtility.getPumpMLogger().debug("[Pump M] carLimitInfoData=" + carLimitInfoData );
		}	
		return carLimitInfoData ;
	}
	
	
	/**
	 * �ŷ�ó �ѵ� ������ �ŷ�ó ���̺�� ���� ���� �´�.
	 * 
	 * @param con			: Connection
	 * @param custInfoData	: �ŷ�ó ������ ����
	 * @param productData	: ������ ������ ��ǰ ����
	 * @return
	 */
	private static T_KH_CUST_LIMIT_INFOData getT_KH_CUST_LIMIT_INFOData(SqlSession session, T_KH_CUST_INFOData custInfoData, 
			T_KH_PRODUCTData productData) {
		
		T_KH_CUST_LIMIT_INFOData custLimitInfoData = null ;

		LogUtility.getPumpMLogger().debug("[Pump M] �ŷ�ó�� �ѵ� ������ ��û�ϱ� ���� �����͵��̴�.") ;			
		if (custInfoData != null) {
			custInfoData.print() ;
		} else {
			LogUtility.getPumpMLogger().debug("[Pump M] custInfoData=" + custInfoData );
		}

		
		if (custInfoData == null) return null ;
		
		try {
			T_KH_CUST_LIMIT_INFOData[] custLimitInfoDataArray = 
				T_KH_CUST_LIMIT_INFOHandler.getHandler().getT_KH_CUST_LIMIT_INFOData(session, 
						custInfoData.getCust_code());
						
			if ((custLimitInfoDataArray == null) || (custLimitInfoDataArray.length == 0)) {
				custLimitInfoData = null ;
			} else {
				custLimitInfoData = custLimitInfoDataArray[0] ;
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}

		LogUtility.getPumpMLogger().debug("[Pump M] �ŷ�ó�� �ѵ� ��������� ������ �����ϴ�.") ;			
		if (custLimitInfoData != null) {
			custLimitInfoData.print() ;
		} else {
			LogUtility.getPumpMLogger().debug("[Pump M] custLimitInfoData=" + custLimitInfoData );
		}
		
		return custLimitInfoData ;
	}
	
	/**
	 * �پ��� ���� odt���� HE������ �޾Ƽ� POS�κ��� �޾ƿ� DW������ ������ �ش� �ŷ� ���� ���θ� �Ǵ��Ѵ�.
	 * @return
	 * @param remainAmt : �ܷ�
	 * @param reqAmt : ���� ��û ����
	 * @param rentlimit_proc_ind_overlimit : �ܻ���� Ÿ��
	 */
	public static boolean isCustRecipt(double remainAmt, double reqAmt, String rentlimit_proc_ind_overlimit ){
		boolean iaAccept = false;
		
		if (remainAmt >= reqAmt )
			iaAccept = true;
		else
			if(ICode.PROC_IND_OVERLIMIT_00.equals(rentlimit_proc_ind_overlimit) || 
					ICode.PROC_IND_OVERLIMIT_01.equals(rentlimit_proc_ind_overlimit) )
				iaAccept = true;
			else
				iaAccept = false;
		
		
		return iaAccept;
		
	}
	
	/**
	 * �ܰ����� ������ �������� ���θ� �Ǵ�
	 * @param bsCardNo
	 * @param creditCardNo
	 * @return
	 */
	@SuppressWarnings("finally")
	public static boolean isDiscountCargo(String bsCardNo, String creditCardNo){
		boolean isDiscountCargo = false;
		
		SqlSession session = null;
		
		try {
			session = SqlSessionFactoryManager.openSqlSession();
			LogUtility.getPumpMLogger().info("[Pump M]-����ȭ�� ���� bsCardNo=" + bsCardNo + "#creditCardNo=" + creditCardNo);
			
			//����ȭ�� ���ī�� BINüũ
			boolean isCargo = T_KH_BIN_INFOHandler.getHandler().isCargo(session, bsCardNo);
			
			//ȭ�������� ����ī�� üũ
			boolean isCreditCargo = T_KH_BIN_INFOHandler.getHandler().isCreditCargo(session, creditCardNo, ICode.BIN_INFO_06);
			
			//�ŷ�ī�� üũ
			boolean isDealCargo = T_KH_BIN_INFOHandler.getHandler().isCreditCargo(session, creditCardNo, ICode.BIN_INFO_03);
			
			LogUtility.getPumpMLogger().debug(	"[Pump M] ����ȭ�� �׽�Ʈ. isCargo=" + isCargo +
					"#isCreditCargo=" + isCreditCargo +
					"#isDealCargo=" + isDealCargo );
			
			if ( isCargo && (isCreditCargo ||  isDealCargo) ) isDiscountCargo = true;
		
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
			return isDiscountCargo;
		}
		
	}
	
	/**
	 * ����ȭ�� �ŷ�ó ���� �߰�
	 * �ŷ�óī��(����ȭ�����ī��), ���ʽ� ī��(����ȭ�����ī��) , �ſ�ī��(ȭ�������ں���ī��/�ŷ�ī��) 
	 * �� ������ �����ϴ� ��쿡�� ���� �ܰ��� �������ش�.
	 * �ŷ�ó ī�尡 �Ϲ� �ŷ�ó ī���� ��쿡�� ��ϵ� PL�� �����Ѵ�.
	 * ���ʽ�ī�尡 ����ȭ�� ���ī���̰� �ſ�ī�尡 ȭ�������ں���ī��/�ŷ�ī�� �� ��쿡 
	 * 4201. 4291 ���� Filler2�� 'U'�� �־��ش�. 
	 * 
	 */

	public static UPOSMessage isDiscountCargo(UPOSMessage rcvUposMsg, UPOSMessage sndUposMsg, String cust_card_no){

		String bsCardNo = rcvUposMsg.getBonRSCard_no();
		String creditCardNo = rcvUposMsg.getCreditCard_no();
		boolean isDiscountCargo = false;
		
		isDiscountCargo = isDiscountCargo(bsCardNo, creditCardNo);
		
		if (!isDiscountCargo) {
			//���� ������ �������� ������ ���δܰ� ���� ����.
			String recordNo = sndUposMsg.getItem_info().getRecordNo();
			ArrayList<UPOSMessage_ItemInfo_Item> itemList = sndUposMsg.getItem_info().getItemInfoList();
			
			ArrayList<UPOSMessage_ItemInfo_Item> newItemList = new ArrayList<UPOSMessage_ItemInfo_Item>();
			UPOSMessage_ItemInfo newItemInfo = new UPOSMessage_ItemInfo();
			
			for(int i=0; i < Integer.parseInt(recordNo); i++){
				UPOSMessage_ItemInfo_Item itemInfoItem = itemList.get(i);
				
				itemInfoItem.setUnitPrice_after_discount(itemInfoItem.getUnitPrice_before_discount());
				itemInfoItem.setOilPrice_after_discount(itemInfoItem.getOilPrice_before_discount());
				
				String taxPrice = GlobalUtility.getTaxPrice(itemInfoItem.getOilPrice_after_discount()) ;
				String price_before_tax = PumpMUtil.getPrice_before_tax(itemInfoItem.getOilPrice_after_discount(), taxPrice) ;
				
				itemInfoItem.setTaxPrice(taxPrice);
				itemInfoItem.setPrice_before_tax(price_before_tax);
				
				newItemList.add(itemInfoItem);
			}
			
			newItemInfo.setRecordNo(recordNo);
			newItemInfo.setItemInfoList(newItemList);
			sndUposMsg.setItem_info(newItemInfo);
		}	
			
		return sndUposMsg;
	}
	
	
	/**
	 * ���� �������� ī�� �ܰ����� ������ �������� ���θ� �Ǵ�
	 * @param bsCardNo
	 * @param creditCardNo
	 * @return
	 */
	@SuppressWarnings("finally")
	public static boolean isDiscountKBCard(String creditCardNo){
		boolean isKBCard = false;
		
		SqlSession session = null;
		
		try {
			session = SqlSessionFactoryManager.openSqlSession();
			
			//���� �������� ī�� üũ
			isKBCard = T_KH_BIN_INFOHandler.getHandler().isCreditCargo(session, creditCardNo, ICode.BIN_INFO_09);
			
			if (!"1".equals(T_KH_CODEMASTERHandler.getHandler().codeMaster.get(IConstant.POSPORTOCOL_CODEMASTER_0334))) {
				LogUtility.getPumpMLogger().debug("[Pump M] ���� �������� ī��. ȯ�漳�� : 0334 (������ ����)"  );
				isKBCard = false;
			}
			
			LogUtility.getPumpMLogger().debug("[Pump M] ���� �������� ī��. isKBCard=" + isKBCard  );
		
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
			return isKBCard;
		}
		
	}
	
	/**
	 * �������� �ŷ�ó ���� �߰�
	 * �ŷ�óī��, , �ſ�ī��(���� �ŷ�ī��) 
	 * PL ���ܰ��� ���ΰ����� ���ε� �ܰ��̸� PL�ܰ��� �����ϰ� �׷��� ������ ���ΰ��� 15�� ������ �����Ѵ�.
	 * @param rcvUposMsg
	 * @param sndUposMsg
	 * @param cust_card_no
	 */
	// 2013.10.10 ksm �������� �ŷ��� ��� PL������ �ܰ��� ���  PL�ܰ��� �����. - ��C, ��D, ��D Ȯ��.
	public static UPOSMessage isOilPriceSupport(UPOSMessage rcvUposMsg, UPOSMessage sndUposMsg, String unitDiscount_ind) {
		String creditCardNo = rcvUposMsg.getCreditCard_no();
		boolean isDiscountKBCard = false;
		
		ArrayList<UPOSMessage_ItemInfo_Item> itemList = sndUposMsg.getItem_info().getItemInfoList();

		if (itemList == null) return sndUposMsg;
		
		UPOSMessage_ItemInfo_Item itemInfoItem = itemList.get(0);

		// pl������ ���� ��� ī������ ����
		// 2013.10.06 ksm  �ܰ����� ���� ���� �߰�.
		if ( itemInfoItem.getUnitPrice_after_discount().equals(itemInfoItem.getUnitPrice_before_discount()) 
				&& ICode.DY_UNITDISCOUNT_IND_0.equals(unitDiscount_ind)) {
			isDiscountKBCard = isDiscountKBCard(creditCardNo);
			
			if (isDiscountKBCard) {
				sndUposMsg = calcBasePrice(rcvUposMsg, sndUposMsg);
				
			}	
		}
		
		return sndUposMsg;
	}	
	
	public static boolean isSuccess(int state) {		
		switch (state) {
			case ICustConstant.STATE_10 :
			case ICustConstant.STATE_30 :
			case ICustConstant.STATE_40 :
			case ICustConstant.STATE_50 :
			case ICustConstant.STATE_60 :
			case ICustConstant.STATE_90 :
			case ICustConstant.STATE_70 :
			case ICustConstant.STATE_80 :
			case ICustConstant.STATE_100 :
			case ICustConstant.STATE_110 : {
				return true ;
			}
		}
		
		return false ;		
	}
	
	/**
	 * �ܰ����� ������ �������� ���θ� �Ǵ�
	 * @param bsCardNo
	 * @param creditCardNo
	 * @return
	 */
	@SuppressWarnings("finally")
	public static boolean isTaxiDoubleSavePoint(String bsCardNo, String creditCardNo){
		boolean isDoubleYn = false;
		
		SqlSession session = null;
		
		try {
			session = SqlSessionFactoryManager.openSqlSession();
			
			//�ý� ����ī�� üũ(����)
			boolean isCreditTaxi = T_KH_BIN_INFOHandler.getHandler().isCreditCargo(session, creditCardNo, ICode.BIN_INFO_07);
			//�ý� ���ī�� �� üũ(���ʽ� ī��)
			boolean isTaxiBs = T_KH_BIN_INFOHandler.getHandler().isCreditCargo(session, bsCardNo, ICode.BIN_INFO_08);
			
			LogUtility.getPumpMLogger().debug("[Pump M] �ý� ��������. isCreditTaxi=" + isCreditTaxi + "#isTaxiBs=" + isTaxiBs  );
			
			if ( isCreditTaxi && isTaxiBs ) isDoubleYn = true;
		
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
			return isDoubleYn;
		}
		
	}
/*	
	
	*//**
	 * PL ��� �ܰ��� ���� �´�.
	 * 
	 * @param con			: Connection
	 * @param cust_code_rep	: ��ǥ �ŷ�ó ��ȣ
	 * @param store_code	: ���� ��ȣ
	 * @param pl_no			: PL ��ȣ
	 * @param goods_code	: ��ǰ ��ȣ
	 * @return
	 * @throws Exception
	 *//*
	private static T_KH_PL_PRICEData checkPLPriceData(SqlSession session, String cust_code_rep, String store_code, 
			String pl_no, String goods_code) throws Exception {
		if (LogUtility.getPumpMLogger().getLevel().toInt() <= Level.DEBUG_INT) {
			LogUtility.getPumpMLogger().debug("[Pump M] ��ǰ �ڵ�� ��ġ�ϴ� �ŷ�ó ������ PL ��� �ܰ��� �����մϴ�." +
					"cust_code_rep="+cust_code_rep +"," +
					"store_code="+store_code +"," +
					"pl_no="+pl_no +"," +
					"goods_code="+goods_code) ;
		}
		
		T_KH_PL_PRICEData plPriceData = null ;
		
		T_KH_PL_PRICEData[] priceDataArray = 
			T_KH_PL_PRICEHandler.getHandler().getT_KH_PL_PRICEData(con, cust_code_rep, store_code, 
					pl_no, goods_code) ;
		if (priceDataArray != null) {
			plPriceData = priceDataArray[0] ;
		}
		
		// debug
		if (LogUtility.getPumpMLogger().getLevel().toInt() <= Level.DEBUG_INT) {
			LogUtility.getPumpMLogger().debug("[Pump M] ��ǰ �ڵ�� ��ġ�ϴ� �ŷ�ó ������ PL ���ܰ���  ������ ������ ����.") ;
			if (plPriceData != null) plPriceData.print() ;
			else {
				LogUtility.getPumpMLogger().debug("[Pump M] ��ġ�ϴ� PL �� �����ϴ�.") ;
			}

		}
		return plPriceData ;
	}*/


	public static UPOSMessage isTaxiDoubleSavePointYn(UPOSMessage rcvUposMsg) {
		
		String bsCardNo = rcvUposMsg.getBonRSCard_no();
		String creditCardNo = rcvUposMsg.getCreditCard_no();
		boolean isTaxiDoubleSavePoint = false;
		
		isTaxiDoubleSavePoint = isTaxiDoubleSavePoint(bsCardNo, creditCardNo);
		
		if (isTaxiDoubleSavePoint) {
			rcvUposMsg.setFiller2(IUPOSConstant.FILLER2_W);
		}	
			
		return rcvUposMsg;
	}
	
	/**
	 * ���� ���� ��ȣ�� ���ؼ� ���� ��ȸ ��û
	 * 
	 * @param syncUnique	: Unique Key
	 * @param nozzle_no		: ���� ��ȣ
	 * @param car_short_no	: ���� ���� ��ȣ
	 * @return
	 */
	public static POS_DU processCustomerCar(String syncUnique, String nozzle_no, String car_short_no) {
		LogUtility.getPumpMLogger().info("[Pump M] ���� �����ȣ�� ���ؼ� ���� ��ȸ�� �����մϴ�.") ;
		POS_DU_Car[] duPumpMCarArray = PumpMUtil.processPOSPumpM_DU_Car(car_short_no) ;
		POS_DU duPumpM = new POS_DU(syncUnique, nozzle_no , duPumpMCarArray) ;		
		
		// Debug Purpose
		LogUtility.getPumpMLogger().debug("[Pump M] ���� �����ȣ�� ���ؼ� ���� ��ȸ�� �����Ͽ� ����� ������ �����ϴ�.") ;
		LogUtility.getPumpMLogger().info(duPumpM.toString());
		
		return duPumpM ;
	}
	
	/**
	 * ���� ���� ��ȣ�� ���ؼ� ���� ��ȸ ��û ���� ���� Preamble Object �� �����Ѵ�.
	 * 
	 * @param syncUnique	: Unique Key
	 * @param messageType	: Message Type
	 * @param uPosMsg		: ��û ����
	 * @return
	 */
	public static Preamble processCustomerCarToPreamble(String syncUnique, int messageType, UPOSMessage uPosMsg) {
		String nozzle_no = uPosMsg.getNozzle_no() ;
		String car_short_no = uPosMsg.getCustCard_No() ;
		POS_DU duPumpM = CustUtil.processCustomerCar(syncUnique, nozzle_no, car_short_no) ;
		
		UPOSMessage sendUPOSMsg = null ;
		if (messageType == IUPOSConstant.MESSAGETYPE_INT_4201) {
			sendUPOSMsg = UPOSUtil.process4201_4291(uPosMsg , duPumpM, false) ;
		} else {
			sendUPOSMsg = UPOSUtil.process4201_4291(uPosMsg , duPumpM, true) ;
		}
		Preamble preamble = PumpMUtil.createUPOSMessagePreamble(syncUnique, SyncManager.DISE_PUMP_MODULE, 
				SyncManager.DISE_CAT_MODULE, sendUPOSMsg, "") ;

		return preamble ;
	}
	
	/**
	 * 
	* <pre>
	* 1. �޼ҵ�� : processCustomerInfoToDY
	* 2. �ۼ��� : 2016. 4. 14. ���� 15:48:31, PI2.
	* 3. �ۼ��� : WooChul Jung
	* 4. ���� :�� ī�� ���� ��û�� �Ѵ�.
	* 5. �����̷�:	���� ��ȣ�� �� ī�� ���� ��û��, ���� ��ȣ + �ŷ�ó �ڵ带 �̿��Ͽ� ���� �ʿ�
	* </pre>
	* @param itemInfoItem
	* @param syncUnique
	* @param uPosMsg
	* @return
	 */
	private static POS_DY processCustomerInfoToDY(UPOSMessage_ItemInfo_Item itemInfoItem, 
			String syncUnique, 
			UPOSMessage uPosMsg) {
		String messageID = GlobalUtility.getUniqueMessageID() ;
		String nozzle_no = uPosMsg.getNozzle_no() ;
		String cust_card_ind = ICode.CUST_CARD_IND_02 ;					// ������ȣ/�ŷ�óī���ȣ ����
		String cust_card_no = uPosMsg.getCustCard_No() ;				// �ŷ�ó ī�� ��ȣ
		String taxFreeCust_type = uPosMsg.getTaxFreeCust_type() ;		// �鼼 �ŷ�ó ����
		String fixedQty_yn = uPosMsg.getFixedQty_yn() ;					// �����Է� ����
		String fixedQty = "" ;											// ������
		String goods_code = itemInfoItem.getGoodsCode() ;
		String basePrice = PumpMUtil.convertNumberFormatFromUPOSToStandardFormat(itemInfoItem.getUnitPrice_before_discount()) ;
		String liter = PumpMUtil.convertNumberFormatFromUPOSToStandardFormat(itemInfoItem.getOilAmount()) ;
		String price = itemInfoItem.getOilPrice_before_discount() ;
		String khTransactionID = itemInfoItem.getKhTransactionID() ;	
		String custCard_car_type = uPosMsg.getCustCard_car_type() ;
		String cust_code = uPosMsg.getFiller2() ; 						// �ŷ�ó �ڵ�. 2016. 4. 14. ���� 15:48:31, PI2, WooChul Jung. Bug Fix
		
		// ���� �Է� ����
		if ((fixedQty_yn != null) && (ICode.FIXEDQTY_YN_1.equals(fixedQty_yn))) {
			fixedQty = PumpMUtil.convertNumberFormatFromUPOSToStandardFormat(uPosMsg.getFixedQty()) ;
		} else {
			fixedQty = uPosMsg.getFixedQty() ;
		}
		
		if (custCard_car_type.equals(IUPOSConstant.CUSTCARD_CAR_TYPE_1) || custCard_car_type.equals(IUPOSConstant.CUSTCARD_CAR_TYPE_4) ) {
			cust_card_ind = ICode.CUST_CARD_IND_02 ;
		} else {
			cust_card_ind = ICode.CUST_CARD_IND_01 ;
		}
		
		POS_DY dyPumpM = null ;
		
		try {
			dyPumpM = PumpMUtil.processPOSPumpM_DY(messageID, nozzle_no, cust_card_ind, cust_card_no,
					taxFreeCust_type, fixedQty_yn, fixedQty, goods_code , basePrice , liter , price , 
					khTransactionID, cust_code);
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
			LogUtility.getPumpMLogger().error("[Pump M] �� ī�� �����ϴ� �߿� ������ �߻��߽��ϴ�. �̴� �������� �ʱ� ������ �Ͼ�� Ȯ���� ���� " +
					" ������ �� ī�尡 �������� �ʴ� ������ ó���մϴ�.") ;
			dyPumpM = null ;
		}
		
		if (dyPumpM != null) {
			LogUtility.getPumpMLogger().debug("[Pump M] �� ī�� ���� ��� ������ �����ϴ�.") ;
			LogUtility.getPumpMLogger().info(dyPumpM.toString());
		} else {
			LogUtility.getPumpMLogger().warn("[Pump M] �� ī�� ���� ��� �����ϴ�.") ;
		}
		
		return dyPumpM ;
	}
	
	/**
	 * �� ī�� ���� ��û ���� ���� Preamble Object �� �����Ѵ�.
	 * 
	 * @param syncUnique	: Unique Key
	 * @param messageType	: Message Type
	 * @param uPosMsg		: ��û ����
	 * @return
	 */
	public static Preamble processCustomerInfoToPreamble(UPOSMessage_ItemInfo_Item itemInfoItem, 
			String syncUnique, int messageType, UPOSMessage uPosMsg) {
		
		POS_DY dyPumpM = CustUtil.processCustomerInfoToDY(itemInfoItem, syncUnique, uPosMsg) ;	
		if (dyPumpM == null) {
			LogUtility.getPumpMLogger().info("[Pump M] �ŷ�ó ������ �Ͽ����� ������ �����ϴ�. ������/�Ϸ� ���� ��û�� ó���� �մϴ�.") ;
		} else {
			// Debug Purpose
			LogUtility.getPumpMLogger().info("[Pump M] �ŷ�ó ������ �Ͽ���, ������ �Ʒ��� �����ϴ�.") ;
			LogUtility.getPumpMLogger().info(dyPumpM.toString());
		}
		UPOSMessage sendUPOSMsg = null ;
		
		if (dyPumpM != null) {
			if (messageType == IUPOSConstant.MESSAGETYPE_INT_4201) {
				sendUPOSMsg = UPOSUtil.process4201_4291(uPosMsg , dyPumpM, false) ;
			} else if(messageType == IUPOSConstant.MESSAGETYPE_INT_4301){
				sendUPOSMsg = UPOSUtil.process4301(uPosMsg , dyPumpM) ;
			} else {
				sendUPOSMsg = UPOSUtil.process4201_4291(uPosMsg , dyPumpM, true) ;
			}
		} else {
			if (messageType == IUPOSConstant.MESSAGETYPE_INT_4201) {
				sendUPOSMsg = UPOSUtil.process4201_4291(uPosMsg , itemInfoItem, false) ;
			} else if(messageType == IUPOSConstant.MESSAGETYPE_INT_4301){
				sendUPOSMsg = UPOSUtil.process4301(uPosMsg , itemInfoItem) ;
			} else {
				sendUPOSMsg = UPOSUtil.process4201_4291(uPosMsg , itemInfoItem, true) ;
			}
		}
		
		// ����ȭ���� ��� ó��
		if (ICode.CUST_CD_ITEM_05.equals(dyPumpM.getCust_cd_item()))
			sendUPOSMsg = CustUtil.isDiscountCargo(uPosMsg, sendUPOSMsg, dyPumpM.getCust_card_no());
		
		// ���������ŷ�ó ó��
		if (ICode.CUST_CD_ITEM_26.equals(dyPumpM.getCust_cd_item())){
			String unitDiscount_ind = dyPumpM.getDyInfoArray()[0].getUnitDiscount_ind();
			sendUPOSMsg = CustUtil.isOilPriceSupport(uPosMsg, sendUPOSMsg, unitDiscount_ind);
		}
		sendUPOSMsg.setPosReceipt_no(itemInfoItem.getKhTransactionID()) ;

		Preamble preamble = PumpMUtil.createUPOSMessagePreamble(	syncUnique, 
				SyncManager.DISE_PUMP_MODULE, 
				SyncManager.DISE_CMS_MODULE, 
				sendUPOSMsg, "") ;
		return preamble ;
	}
	

	/**
	 * ���� ��ȣ�� ���ؼ� �ŷ�ó ���� ������ ��û�Ѵ�.
	 * �鼼 ���ΰ� ���� ��� �ŷ�ó�� ���, PL ��� �ܰ��� �鼼���� ���θ� üũ �Ѵ�.
	 * 
	 * @param nozzle_no		: ���� ��ȣ
	 * @param car_no	: ���� ��ȣ
	 * @param taxfreecust_type	: 0 : �鼼 �ƴ�, 1:��� �ŷ�ó , 2: �Ϲ�
	 * @param cust_code	: �ŷ�ó �ڵ�
	 * @return
	 */
	public static CustReturnValue processCustWithCarNo(String nozzle_no, String car_no, String taxfreecust_type, String cust_code) {
		
		LogUtility.getPumpMLogger().info("[Pump M] ���� ��ȣ�� �̿��Ͽ� �ŷ�ó ������ �����մϴ�. " +
				"�����ȣ=" + nozzle_no + "#������ȣ=" + car_no + "#�ŷ�ó�ڵ�=" + cust_code) ;		

		CustReturnValue custReturnValue = null ;
		SqlSession session = null;
		try {
			session = SqlSessionFactoryManager.openSqlSession();
			T_NZ_NOZZLEData nzNozzleData = T_NZ_NOZZLEHandler.getHandler().getT_NZ_NOZZLEDataByNozzleNo(session ,nozzle_no)[0] ;
			T_KH_PRODUCTData productData = T_KH_PRODUCTHandler.getHandler().getT_KH_PRODUCTData(session, nzNozzleData.getGoods_code())[0] ;
			
			T_KH_CUST_INFOData custInfoData = null ;
			T_KH_CUST_CAR_INFOData custCarInfoData = null ; 
			
			String storeCode = T_KH_STOREHandler.getHandler().getStoreCode() ;
			String card_code_base = null ;
			String proc_ind_overlimit = null ;
			String rcptunitprc_ind_prt = null ;
			String rcptsheetissue_ind_prtcarno = null ;
			String keepissue_ind = null ;
			String rcptsheetissue_code_amtsale = null ;
			String cust_cd_item = null ;
			
			T_KH_CUST_CAR_INFOData[] custCarInfoDataArray = 
				T_KH_CUST_CAR_INFOHandler.getHandler().getT_KH_CUST_CAR_INFOData(session, storeCode, cust_code, car_no) ;
			
			if ((custCarInfoDataArray == null) || (custCarInfoDataArray.length == 0)) {
				custCarInfoData = null ;
			} else {
				custCarInfoData = custCarInfoDataArray[0] ;
				if (!compareGoodsCodeInCustCarInfoTable(productData, custCarInfoData)) {
					custCarInfoData = null ;
				}
			}

			if (custCarInfoData == null) {
				throw new CustException("[Pump M] �ŷ�ó ���� ��ȣ��  �ŷ�ó ���� �����Ϳ� �����ϴ�.car_no=" + car_no) ;
			}
			
			custInfoData = 
				T_KH_CUST_INFOHandler.getHandler().getT_KH_CUST_INFOData(session, custCarInfoData.getCust_code(), storeCode) ;
			
			if (custInfoData == null) {
				throw new CustException("[Pump M] �ŷ�ó ���� ��ȣ��  �ŷ�ó �����Ϳ� �����ϴ�.car_no=" + car_no) ;
			}
			
			card_code_base = custInfoData.getCard_code_base() ;
			proc_ind_overlimit = custInfoData.getProc_ind_overlimit() ;
			rcptunitprc_ind_prt = custInfoData.getRcptunitprc_ind_prt() ;
			rcptsheetissue_ind_prtcarno = custInfoData.getRcptsheetissue_ind_prtcarno() ;
			// 2016. 4. 14. ���� 15:48:31, PI2, twsongkis 
			// codemaster �������� ������ ���࿩�θ� �ѹ� �� üũ.
			keepissue_ind = custInfoData.getKeepissue_ind() ;
			if(ICode.KEEPISSUE_IND_1.equals(keepissue_ind)){
				keepissue_ind = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0019);
			}
			rcptsheetissue_code_amtsale = custInfoData.getRcptsheetissue_code_amtsale() ;
			cust_cd_item = custInfoData.getCust_cd_item() ;
			
			switch (card_code_base) {				
				case ICode.CARD_CODE_BASE_01 : {
					// ���� �ŷ�ó
					custReturnValue = getCustReturnValueForVIP(session, custInfoData, productData,  
							true, taxfreecust_type) ;
					break ;
				}
				case ICode.CARD_CODE_BASE_02 : {
					// �ܻ� �ŷ�ó
					custReturnValue = getCustReturnValueForRentCustomer(session, custInfoData, 
							custCarInfoData, null, productData, true, taxfreecust_type) ;	
					break ;
				}
				case ICode.CARD_CODE_BASE_03 : {
					// �뿪 ����	- ���ΰ��� ��� 
					custReturnValue = new CustReturnValue() ;
					custReturnValue.setState(ICustConstant.STATE_70) ;
					custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;
					String basePrice = PumpMUtil.getBasePrice(productData, nzNozzleData) ;
					custReturnValue.setDiscountBasePrice(basePrice) ;
					break ;
				}
				case ICode.CARD_CODE_BASE_04 : {
					// ���� ����	- ���ΰ��� ��� 		
					custReturnValue = new CustReturnValue() ;
					custReturnValue.setState(ICustConstant.STATE_80) ;
					custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;
					String basePrice = PumpMUtil.getBasePrice(productData, nzNozzleData) ;
					custReturnValue.setDiscountBasePrice(basePrice) ;
					break ;
				}
			}
			
			// Default Setting
			custReturnValue.setBasePrice(PumpMUtil.getBasePrice(productData, nzNozzleData)) ;
			custReturnValue.setCarno_nbr(car_no) ;
			custReturnValue.setCust_code(custCarInfoData.getCust_code()) ;
			custReturnValue.setCust_name(custInfoData.getCust_name()) ;
			custReturnValue.setTrans_code_status(custInfoData.getTrans_code_status()) ;
			custReturnValue.setProc_ind_overlimit(proc_ind_overlimit) ;
			custReturnValue.setCardno_nbr_cust("") ;
			custReturnValue.setRcptunitprc_ind_prt(rcptunitprc_ind_prt) ;
			custReturnValue.setRcptsheetissue_ind_prtcarno(rcptsheetissue_ind_prtcarno) ;			
			custReturnValue.setKeepissue_ind(keepissue_ind) ;
			custReturnValue.setRcptsheetissue_code_amtsale(rcptsheetissue_code_amtsale) ;
			custReturnValue.setCust_cd_item(cust_cd_item) ;
		} catch (CustException e) {
			LogUtility.getPumpMLogger().error(e.getMessage()) ;
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		} 
		
		if (custReturnValue != null) {
			// VIP , ����ȭ������ ����
			if (isSuccess(custReturnValue.getState())) {
				if (ICode.CUST_CD_ITEM_05.equals(custReturnValue.getCust_cd_item())) {
					custReturnValue.setState(ICustConstant.STATE_100) ;		// ����ȭ��
				} else if (custReturnValue.getState() == ICustConstant.STATE_10) {
					try {
						if (T_KH_BIN_INFOHandler.getHandler().isVIP(custReturnValue.getCardno_nbr_cust())) {
							custReturnValue.setState(ICustConstant.STATE_110) ;		// VIP
						}
					} catch (Exception e) {
						LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
					}
				}
			}			
		}
		
		// Debug Purpose
		LogUtility.getPumpMLogger().debug("[Pump M] �ŷ�óī�� �������� ������ �����ϴ�." ) ;
		if (custReturnValue == null) {
			LogUtility.getPumpMLogger().debug("[Pump M] ����� �Ǿ� ���� �ʾƼ� Default ������ �����մϴ�.") ;
			custReturnValue = new CustReturnValue() ;
			custReturnValue.setState(ICustConstant.STATE_1) ;
			custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;
		}
		custReturnValue.print() ;
		return custReturnValue ;
	}
	

	/**
	 * �ŷ�ó ī�� ��ȣ�� ���� �ŷ�ó ���� ������ ��û�Ѵ�.
	 * 
	 * @param nozzle_no : ���� ��ȣ
	 * @param cardno_nbr_cust : �ŷ�ó ī�� ��ȣ
	 * @param taxfreecust_type	: 0 : �鼼 ���� ���� �ƴ�, 1: CAT �ܸ���� ���� �鼼 ������ ��I�Ǿ��� , 2: �Ϲ�(������ ����)
	 * @return
	 * 2013.10.08 ksm PL�� �����Ͽ� PL�ܰ��� ����� ��� �˼��ֵ��� ���� �ʿ�.
	 */
	public static CustReturnValue processCustWithCustCardNo(String nozzle_no , String cardno_nbr_cust, String taxfreecust_type) {
		
		LogUtility.getPumpMLogger().info("[Pump M] �ŷ�ó ī�带 �����մϴ�. nozzle_no=" + nozzle_no + ". cardno_nbr_cust=" + Base64Util.encode(cardno_nbr_cust.split("=")[0])) ;

		CustReturnValue custReturnValue = null ;
		SqlSession session = null;
		
		try {
			session = SqlSessionFactoryManager.openSqlSession();
			boolean supportGoodsCode = true ;
			
			T_KH_CUST_CARD_INFOData custCardInfoData = 
				T_KH_CUST_CARD_INFOHandler.getHandler().getT_KH_CUST_CARD_INFODataByCardNoNbrCust(session, cardno_nbr_cust) ;

			T_NZ_NOZZLEData nzNozzleData = T_NZ_NOZZLEHandler.getHandler().getT_NZ_NOZZLEDataByNozzleNo(session ,nozzle_no)[0] ;
			T_KH_PRODUCTData productData = T_KH_PRODUCTHandler.getHandler().getT_KH_PRODUCTData(session, nzNozzleData.getGoods_code())[0] ;
			
			T_KH_CUST_INFOData custInfoData = null ;
			T_KH_CUST_CAR_INFOData custCarInfoData = null ; 
			
			if (custCardInfoData == null) {
				LogUtility.getPumpMLogger().warn("[Pump M] �ŷ�ó ī�尡 T_KH_CUST_CARD_INFO ���̺� �����ϴ�. ����ȭ������ �����մϴ�.") ;
				
				// ����ȭ������ ���� , cust_cd_item=05
				boolean isCargo = T_KH_BIN_INFOHandler.getHandler().isCargo(session, cardno_nbr_cust) ;
				if (isCargo) {
					LogUtility.getPumpMLogger().info("[Pump M] ����ȭ���Դϴ�. �ŷ�ó ī�� �����Ϳ��� ����ȭ�� ������ �����ɴϴ�.") ;
					custInfoData = T_KH_CUST_INFOHandler.getHandler().getT_KH_CUST_INFODataByCust_cd_item(session, ICode.CUST_CD_ITEM_05) ;
				}
				
				if (custInfoData == null) {
					throw new CustException("[Pump M] �ŷ�ó ī�尡 �ŷ�ó �����Ϳ� �����ϴ�.cardno_nbr_cust=" +  Base64Util.encode(cardno_nbr_cust.split("=")[0])) ;
				}
			}
			
			if (custInfoData == null) {
				custInfoData = T_KH_CUST_INFOHandler.getHandler().getT_KH_CUST_INFOData(session, custCardInfoData.getCust_code(), custCardInfoData.getStore_code()) ;
			}
			
			if (custInfoData == null) {
				throw new CustException("[Pump M] �ŷ�ó ī�尡 �ŷ�ó �����Ϳ� �����ϴ�.cardno_nbr_cust=" +  Base64Util.encode(cardno_nbr_cust.split("=")[0])) ;
			}
			
			String card_code_base = custInfoData.getCard_code_base() ;
			String proc_ind_overlimit = custInfoData.getProc_ind_overlimit() ;
			String rcptunitprc_ind_prt = custInfoData.getRcptunitprc_ind_prt() ;
			String rcptsheetissue_ind_prtcarno = custInfoData.getRcptsheetissue_ind_prtcarno() ;
			String keepissue_ind = custInfoData.getKeepissue_ind() ;
			String rcptsheetissue_code_amtsale = custInfoData.getRcptsheetissue_code_amtsale() ;
			String cust_cd_item = custInfoData.getCust_cd_item() ;
			String cardno_nbr = null ;
			
			T_KH_CUST_CAR_INFOData[] custCarInfoDataArray  = null ;
			
			if (custCardInfoData != null) {
				cardno_nbr = custCardInfoData.getCarno_nbr() ;	
				
				if ((cardno_nbr != null) && (!"".equals(cardno_nbr))) {
					custCarInfoDataArray = T_KH_CUST_CAR_INFOHandler.getHandler().getT_KH_CUST_CAR_INFOData(session, custCardInfoData.getStore_code(), 
							custCardInfoData.getCust_code(), custCardInfoData.getCarno_nbr()) ;
				}
			}
			
			if ((cardno_nbr == null) || ("".equals(cardno_nbr))) {
				LogUtility.getPumpMLogger().debug("[Pump M] �ŷ�ó ī�� ������ ���� ��ȣ�� ����. �̴� ����Ѵ�.") ;
				custCarInfoData = null ;
				supportGoodsCode = true ;				
			} else if ((custCarInfoDataArray == null) || (custCarInfoDataArray.length == 0)) {
				// ������ ��ϵ��� ���� �ŷ�ó ī��� ��ü ������ ���ؼ� �����Ѵٶ�� �����Ѵ�. ������ ������ �ִ� ���� ������������ ���� �ڵ�� ���Ѵ�.
				LogUtility.getPumpMLogger().debug("[Pump M] �ŷ�ó ī��� ������ ������ ��ϵ��� ���� ���̾ ������ ����Ѵ�.") ;
				custCarInfoData = null ;
				supportGoodsCode = true ;
			} else {
				custCarInfoData = custCarInfoDataArray[0] ;
				
				// debug
				custCarInfoData.print() ;
				
				if (compareGoodsCodeInCustCarInfoTable(productData, custCarInfoData)) {
					LogUtility.getPumpMLogger().debug("[Pump M] ���� ������ ���� �ŷ�ó ī�� ������ �̿��� ��� ���� ������ �����Ѵ�.") ;
					supportGoodsCode = true ;
				} else {
					LogUtility.getPumpMLogger().debug("[Pump M] �ŷ�ó ī���� ������ ���������Ϳ� ��ϵǾ� ������ ������ Ʋ�� �������� �ʽ��ϴ�.") ;
					custCarInfoData = null ;
					supportGoodsCode = false ;
				}
			}
			
			switch (card_code_base) {
				case ICode.CARD_CODE_BASE_01 : {
					// ���� �ŷ�ó
					custReturnValue = getCustReturnValueForVIP(	session, 
							custInfoData, 
							productData,  
							supportGoodsCode, 
							taxfreecust_type) ;
					break ;
				}
				case ICode.CARD_CODE_BASE_02 : {
					// �ܻ� �ŷ�ó
					custReturnValue = getCustReturnValueForRentCustomer(	session, 
							custInfoData, 
							custCarInfoData, 
							custCardInfoData, 
							productData, 
							supportGoodsCode,
							taxfreecust_type) ;	
					break ;
				}
				case ICode.CARD_CODE_BASE_03 : {
					// �뿪 ����	- ���ΰ��� ��� 
					custReturnValue = new CustReturnValue() ;
					custReturnValue.setState(ICustConstant.STATE_70) ;
					custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;
					String basePrice = PumpMUtil.getBasePrice(productData, nzNozzleData) ;
					custReturnValue.setDiscountBasePrice(basePrice) ;
					break ;
				}
				case ICode.CARD_CODE_BASE_04 : {
					// ���� ����	- ���ΰ��� ��� 		
					custReturnValue = new CustReturnValue() ;
					custReturnValue.setState(ICustConstant.STATE_80) ;
					custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;
					String basePrice = PumpMUtil.getBasePrice(productData, nzNozzleData) ;
					custReturnValue.setDiscountBasePrice(basePrice) ;
					break ;
				}
			}
			
			// Default Setting
			custReturnValue.setBasePrice(PumpMUtil.getBasePrice(productData, nzNozzleData)) ;
			if (custCardInfoData != null) {
				custReturnValue.setCarno_nbr(custCardInfoData.getCarno_nbr()) ;
				custReturnValue.setCust_code(custCardInfoData.getCust_code()) ;
			}
			custReturnValue.setCust_name(custInfoData.getCust_name()) ;
			custReturnValue.setTrans_code_status(custInfoData.getTrans_code_status()) ;
			custReturnValue.setProc_ind_overlimit(proc_ind_overlimit) ;
			custReturnValue.setCardno_nbr_cust(cardno_nbr_cust) ;
			custReturnValue.setRcptunitprc_ind_prt(rcptunitprc_ind_prt) ;
			custReturnValue.setRcptsheetissue_ind_prtcarno(rcptsheetissue_ind_prtcarno) ;			
			custReturnValue.setKeepissue_ind(keepissue_ind) ;
			custReturnValue.setRcptsheetissue_code_amtsale(rcptsheetissue_code_amtsale) ;
			custReturnValue.setCust_cd_item(cust_cd_item) ;
		} catch (CustException e) {
			LogUtility.getPumpMLogger().error(e.getMessage());
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		} 
		
		if (custReturnValue != null) {
			// VIP , ����ȭ������ ����
			if (isSuccess(custReturnValue.getState())) {
				if (ICode.CUST_CD_ITEM_05.equals(custReturnValue.getCust_cd_item())) {
					custReturnValue.setState(ICustConstant.STATE_100) ;		// ����ȭ��
				} else if (custReturnValue.getState() == ICustConstant.STATE_10) {
					try {
						if (T_KH_BIN_INFOHandler.getHandler().isVIP(custReturnValue.getCardno_nbr_cust())) {
							custReturnValue.setState(ICustConstant.STATE_110) ;		// VIP
						}
					} catch (Exception e) {
						LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
					}
				}
			}
		}
		
		LogUtility.getPumpMLogger().debug("[Pump M] �ŷ�óī�� �������� ������ �����ϴ�." ) ;
		if (custReturnValue == null) {
			LogUtility.getPumpMLogger().debug("[Pump M] ����� �Ǿ� ���� �ʾƼ� Default ������ �����մϴ�.") ;
			custReturnValue = new CustReturnValue() ;
			custReturnValue.setState(ICustConstant.STATE_1) ;
			custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;
		}
		custReturnValue.print() ;
		return custReturnValue ;
	}
}
