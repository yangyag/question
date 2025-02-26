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
		
		//할인단가와 점두가가 일치하고 중분류 "경유" 12XX 이면
		//유가보조 거래처인지 확인 하고 유가보조 거래처이면 카드번호 bin을 체크하여 환경정보(0333)에 등록되어 있는 할인 단가를 적용해준다.
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
				
				LogUtility.getLogger().info("calcBasePrice => 보너스 카드 : " + rcvUposMsg.getBonRSCard_no() );
				
				long bonRsCardNo = Long.parseLong(rcvUposMsg.getBonRSCard_no());

				// 0190-6102-3211-0000 ~ 0190-6102-3310-9999 : FDT1904001 우리영업화물-개인신용
				long cardArea01S = 190610232110000l;
				long cardArea01E = 190610233109999l;

				// 0190-6102-3311-0000 ~ 0190-6102-3320-9999 : FDT1904002 우리영업화물-법인체크
				long cardArea02S = 190610233110000l;
				long cardArea02E = 190610233209999l;

				// 0190-6102-3321-0000 ~ 0190-6102-3330-9999 : FDT1904003 우리영업화물-외상거래
				long cardArea03S = 190610233210000l;
				long cardArea03E = 190610233309999l;

				/*
				 * 우리영업화물 카드의 경우 별도의 계산로직을 적용한다.
				 * */
				if((bonRsCardNo >= cardArea01S && bonRsCardNo <= cardArea01E) || (bonRsCardNo >= cardArea02S && bonRsCardNo <= cardArea02E) || (bonRsCardNo >= cardArea03S && bonRsCardNo <= cardArea03E)) {
					
					LogUtility.getLogger().info("calcBasePrice => 해당 카드는 우리영업화물 카드로써 수량을 반올림 처리하여 계산하도록 한다." );
					
					// 소수점 첫째자리 이하 반올림
					double reOilAmt = Math.round(oilAmt);

					// 소수점 둘째자리 이하 반올림
					//double reOilAmt = Math.round(oilAmt * 10) / 10.0;
					LogUtility.getLogger().info("calcBasePrice => 수량 : " + oilAmt );
					LogUtility.getLogger().info("calcBasePrice => 수량 반올림 : " + reOilAmt );
					
					// 할인할 금액(반올림 된 수량 * 리터당 할인금액)
					double disPrice = reOilAmt * discountRate;
					LogUtility.getLogger().info("calcBasePrice => 할인할 금액 : " + disPrice );
					
					// 할인 전 원금액(할인전 단가 * 수량)
					double originalPrice = unitPrice_before * oilAmt;
					LogUtility.getLogger().info("calcBasePrice => 원 금액 : " + originalPrice );
					
					// 할인 후 금액 : 할인 전 원금액 - 할인금액
					double finalPrice = originalPrice - disPrice;
					LogUtility.getLogger().info("calcBasePrice => 할인 후 결제할 금액 : " + finalPrice );
					
					// 총 할인금액을 제외한 나머지 금액 적용
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
				
				// PI2, CWI, 2016-04-27, 통합거래처 할인 시 ItemInfo의 할인 여부가 0으로 표기 되고 있으며, 
				// 할인 후 단가가 할인전 단가와 상이 하기에 1로 수정 되도록 변경
				// 방어코드로 할인전 단가가 할인 후 단가보다 작을 경우만 할인여부를 1로 설정 한다.(2016-05-03)
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
				
				LogUtility.getLogger().debug("[국민 유가보조 카드 ] 통합거래처 상품정보 재구성");
				itemInfoItem.print();
			}
			// 2013.07.15 ksm  KB스타트럭 카드로 12XX(경유)가 아닌 유종 처리시  Item 빠지는 현상 발생하여 조치함.
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
	 * 통합거래처 거래단가 구함.
	 * @author ksm
	 * @param storeCd
	 * @param targetCust
	 * @param goodsCd
	 * @return
	 */
	public static String calcIntegCustBasePrice(SqlSession session, String storeCd, String targetCust, String goodsCd){
		 
		T_KH_PLData plData = null ;
		
		try{		 
			LogUtility.getCATLogger().debug("[통합거래처] 등록된 PL 을 검색합니다.cust_code_rep="+	targetCust+"#store_code="+ storeCd) ;
			
			Date date = Calendar.getInstance().getTime();
			
			String currDateYYYYMMDD = (new SimpleDateFormat("yyyyMMdd").format(date)) ;
			//T_KH_STOREHandler.getHandler().getWorkingDate() ;    
			// 기존 영업일 기준에서 시스템 시간 기준으로 변경. POS로직과 동일하게 변경.
			LogUtility.getCATLogger().debug("[Pump M] 통합거래처. 시스템시간 : " + currDateYYYYMMDD);
			
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
						LogUtility.getCATLogger().debug("[통합거래처] 할인단가를 구하지 못했습니다.");
						return "";
					}
				}
			} else {
				LogUtility.getCATLogger().debug("[통합거래처] PL번호를 구하지 못했습니다.");
				return "";
			}
		}catch(Exception e){
			LogUtility.getCATLogger().error("[통합거래처] 거래단가구하다가 Exception 발생" + e.toString(), e);
		}
		
		return "";
	}
	
	/**
	 * PL 테이블 에서 거래처 관련 PL 데이터 존재 여부를 확인한다.
	 * 
	 * @param con			: Connection
	 * @param cust_code_rep	: 대표 거래처 번호
	 * @param store_code	: 매장 코드
	 * @param goods_code	: 상품 코드
	 * @return
	 * @throws Exception
	 */
	private static T_KH_PLData checkPLData(SqlSession session, String cust_code_rep, String store_code) throws Exception {
		
		T_KH_PLData plData = null ;
		
		LogUtility.getPumpMLogger().debug("[Pump M] 등록된 PL 을 검색합니다.cust_code_rep="+ 
				cust_code_rep+"#store_code="+ store_code) ;
		
		Date date = Calendar.getInstance().getTime();
		//String currDateYYYYMMDD = T_KH_STOREHandler.getHandler().getWorkingDate() ;
		// 영업일 기준에서 시스템 시간기준으로 변경함. POS로직과 동일하게 변경.
		String currDateYYYYMMDD = (new SimpleDateFormat("yyyyMMdd").format(date)) ;
		
		T_KH_PLData[] plDataArray = 
			T_KH_PLHandler.getHandler().getT_KH_PLDataByYYYYMMDD(session, cust_code_rep, store_code, currDateYYYYMMDD) ;
		
		if (plDataArray != null) {
			plData = plDataArray[0] ;
		}
		
		LogUtility.getPumpMLogger().debug("[Pump M]  거래처 정보의 PL 을 조사결과 다음과 같다.") ;
		if (plData != null) plData.print() ;
		else {
			LogUtility.getPumpMLogger().debug("[Pump M] 일치하는 PL 이 없습니다.") ;
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
		LogUtility.getPumpMLogger().debug("[Pump M] PL 여신 정보와 PL 계약 단가를 확인한 결과=" + rlt) ;
	
		return plPriceData ;
	}	
	
	
	/**
	 * 거래처 차량 마스터에 등록된 상품 코드와 현 주유건의 상품 코드가 일치 여부를 확인한다.
	 * 
	 * @param productData		: 상품 정보
	 * @param custCarInfoData	: 거래처 차량 마스터 정보
	 * @return
	 */
	private static boolean compareGoodsCodeInCustCarInfoTable(T_KH_PRODUCTData productData , 
			T_KH_CUST_CAR_INFOData custCarInfoData) {
		boolean rlt = false ;

		LogUtility.getPumpMLogger().debug("[Pump M] 거래처 차량 마스터에 설정된 상품과 노즐의 상품을 비교하기 위한 데이터이다.") ;
			
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

		LogUtility.getPumpMLogger().debug("[Pump M] 비교 결과는 다음과 같습니다. rlt = " + rlt) ;
		return rlt ;
	}

	
	
	
	/**
	 * 외상고객을 위한 로직 수행을 요청한다.
	 * 
	 * 변경 사항 [2008.11.06] by 오춘열 부장님
	 * 		차량별 한도에서 무제한 처리
	 * 			- 차량별한도인데 거래처카드에 차량번호를 등록하지 않을 경우
	 * 
	 * @param con : Connection
	 * @param custInfoData : 거래처카드 정보
	 * @param custCarInfoData : 거래처차량 마스터 정보
	 * @param custCardInfoData : 거래처카드 정보
	 * @param productData : 상품 정보
	 * @param taxfreecust_type	: 0 : 면세 아님, 1:등록 거래처 , 2: 일반
	 * @return
	 * 		CustReturnValue
	 */
	private static CustReturnValue getCustReturnValueForRentCustomer(SqlSession session, T_KH_CUST_INFOData custInfoData, 
			T_KH_CUST_CAR_INFOData custCarInfoData,	T_KH_CUST_CARD_INFOData custCardInfoData, 
			T_KH_PRODUCTData productData, boolean supportGoodsCode,	String taxfreecust_type) {
		
		// Debug
			LogUtility.getPumpMLogger().debug("[Pump M] 외상 고객 처리를 위한 고객 데이터입니다." + "supportGoodsCode="+supportGoodsCode +
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
				case ICode.CARDADJ_IND_01 : {	// 무제한
					if ((supportGoodsCode == false)) {
						custReturnValue.setState(ICustConstant.STATE_33) ;		// 외상 거래처 - 무제한 - 차량 유종 코드 불일치
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// 점두가 적용
						
						return custReturnValue ;	// 현금고객-노즐의 상품코드와 일치하지 않음.
					}
					
					T_KH_PLData plData = checkPLData(session, cust_code_rep, store_code) ;
					if (plData == null) {
						custReturnValue.setState(ICustConstant.STATE_32) ;		// 외상 거래처 - 무제한 - PL 문제
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// 점두가 적용
							
						return custReturnValue ;	
					}
					
/*					T_KH_PL_PRICEData plPriceData = 
						checkPLPriceData(session, cust_code_rep, store_code, plData.getPl_no(), goods_code) ;
*/					
					T_KH_PL_PRICEData plPriceData = 
						checkPLPriceData(session, cust_code_rep, store_code, plData.getPl_no(), goods_code) ;


					if ((plPriceData == null)) {
						custReturnValue.setState(ICustConstant.STATE_31) ;		// 외상 거래처 - 무제한 - 상품코드 불일치 
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// 점두가 적용
						
						return custReturnValue ;	
					} 			

					if (taxfreecust_type.equals(ICode.DX_TAXFREECUST_TYPE_1)) {
						if (!plPriceData.getTaxfree_cd().equals(ICode.TAXFREE_CD_02)) {
							LogUtility.getPumpMLogger().debug("[Pump M] 면세로 요청된 것이, PL 계약단가가 과세로 설정되어 있습니다.") ;
							custReturnValue.setState(ICustConstant.STATE_35) ;		// 면세로 요청했는데 면세 고객이 아님.
							custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// 점두가 적용
							
							return custReturnValue ;					
						}
					}
					
					String discountBasePrice = getDiscountBasePrice(productData, plPriceData) ;
					
					if (discountBasePrice == null) {
						custReturnValue.setState(ICustConstant.STATE_34) ;		// 외상 거래처 - 무제한 - 설정된 단가가 존재하지 않음
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// 점두가 적용
						
						return custReturnValue ;
					}
					
					custReturnValue.setUnitprc_code_stdn(plPriceData.getUnitprc_code_stdn()) ;
					custReturnValue.setTaxfree_cd(plPriceData.getTaxfree_cd());
					custReturnValue.setState(ICustConstant.STATE_30) ;		// 외상 거래처 - 무제한 - 정상
					custReturnValue.setType(ICustConstant.TYPE_CUST_BASEPRICE) ;	// 단가 적용
					custReturnValue.setDiscountBasePrice(discountBasePrice) ;
						
					return custReturnValue ;	
				}
				case ICode.CARDADJ_IND_02 : {	// 거래처별
					if ((supportGoodsCode == false)) {
						custReturnValue.setState(ICustConstant.STATE_53) ;		// 외상 거래처 - 거래처별한도 - 차량 유종 코드 불일치
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// 점두가 적용
						
						return custReturnValue ;	
					}
					
					T_KH_PLData plData = checkPLData(session, cust_code_rep, store_code) ;
					if (plData == null) {
						custReturnValue.setState(ICustConstant.STATE_52) ;		// 외상 거래처 - 거래처별한도 - PL 문제
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// 점두가 적용
							
						return custReturnValue ;	
					}
					
					
					/*	T_KH_PL_PRICEData plPriceData = 
							checkPLPriceData(session, cust_code_rep, store_code, plData.getPl_no(), goods_code) ;
					 */					
					T_KH_PL_PRICEData plPriceData = 
						checkPLPriceData(session, cust_code_rep, store_code, plData.getPl_no(), goods_code) ;
		

					if (plPriceData == null) {
						custReturnValue.setState(ICustConstant.STATE_51) ;		// 외상 거래처 - 거래처별한도 - 상품코드 불일치 
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// 점두가 적용
						
						return custReturnValue ;	
					} 			

					if (taxfreecust_type.equals(ICode.DX_TAXFREECUST_TYPE_1)) {
						if (!plPriceData.getTaxfree_cd().equals(ICode.TAXFREE_CD_02)) {
							LogUtility.getPumpMLogger().debug("[Pump M] 면세로 요청된 것이, PL 계약단가가 과세로 설정되어 있습니다.") ;
							custReturnValue.setState(ICustConstant.STATE_55) ;		// 면세로 요청했는데 면세 고객이 아님.
							custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// 점두가 적용
							
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
						// 거래처별 한도 테이블에 정보가 없을 경우 에러로 처리
						custReturnValue.setState(ICustConstant.STATE_56) ;		// 외상 거래처 - 거래처별한도 - 거래처별 한도테이블에 정보가 없는 경우
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;
						
						return custReturnValue ;	
					}

					
					if (discountBasePrice == null) {
						custReturnValue.setState(ICustConstant.STATE_54) ;		// 외상 거래처 - 거래처별한도 - 설정된 단가가 존재하지 않음
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// 점두가 적용
						
						return custReturnValue ;
					}

					String cardno_nbr = custCardInfoData.getCarno_nbr() ;
					
					if ((cardno_nbr == null) || ("".equals(cardno_nbr))) {
						custReturnValue.setUnitprc_code_stdn(plPriceData.getUnitprc_code_stdn()) ;
						custReturnValue.setTaxfree_cd(plPriceData.getTaxfree_cd());
						custReturnValue.setState(ICustConstant.STATE_30) ;				// 외상 거래처 - 무제한 - 정상
						custReturnValue.setType(ICustConstant.TYPE_CUST_BASEPRICE) ;	// 단가 적용
						custReturnValue.setDiscountBasePrice(discountBasePrice) ;
						
						return custReturnValue ;
					}
					
					// 거래처별 한도 테이블 존재 유무 확인 - 존재시 남은 수량을 입력
					String cust_code = custCardInfoData.getCust_code() ;
					String carno_nbr = custCardInfoData.getCarno_nbr() ;
					T_KH_CAR_LIMIT_INFOData carLimitInfoData = null ;
					
					if ((carno_nbr != null) && (!carno_nbr.equals(""))) {
						T_KH_CAR_LIMIT_INFOData[] carLimitInfoDataArray = 
							T_KH_CAR_LIMIT_INFOHandler.getHandler().getT_KH_CAR_LIMIT_INFOData(session, cust_code, carno_nbr, goods_code) ;
						
						if ((carLimitInfoDataArray != null) && (carLimitInfoDataArray.length != 0)) {
							carLimitInfoData = carLimitInfoDataArray[0] ;
							LogUtility.getPumpMLogger().debug("[Pump M] 거래처 한도 고객이지만 차량별 한도도 등록되어 있습니다. 정보는 아래와 같습니다.") ;
							carLimitInfoData.print() ;
						}
					}
					
					if (carLimitInfoData == null) {
						if (tKhLimitInfo.getAdjbase_code_limit().equals(ICode.ADJBASE_CODE_LIMIT_01)) {
							// 수량 
							priceOrLiter = ICustConstant.LIMIT_AMOUNT_LITER ;
							limit = tKhLimitInfo.getLimit_cnt_monthly() ;
							usedAmount = tKhLimitInfo.getAcclimit_cnt_monthly() ;
						} else {
							// 단가
							priceOrLiter = ICustConstant.LIMIT_AMOUNT_PRICE ;
							limit = tKhLimitInfo.getLimit_amt_monthly() ;
							usedAmount = tKhLimitInfo.getAcclimit_amt_monthly() ;
						}
						limitAmount = new LimitAmount(priceOrLiter,limit,usedAmount) ;
							
						custReturnValue.setUnitprc_code_stdn(plPriceData.getUnitprc_code_stdn()) ;
						custReturnValue.setTaxfree_cd(plPriceData.getTaxfree_cd());
						custReturnValue.setState(ICustConstant.STATE_50) ;			// 외상 거래처 - 거래처별한도 - 정상
						custReturnValue.setType(ICustConstant.TYPE_CUST_LIMIT) ;	// 거래처별
						custReturnValue.setDiscountBasePrice(discountBasePrice) ;
						custReturnValue.setLimitAmount(limitAmount) ;
						
						return custReturnValue ;	
					} else {
						LogUtility.getPumpMLogger().debug("[Pump M] 차량 한도가 등록된 거래처 한도 고객입니다.이에 대한 처리를 실시힙니다.") ;
						String remainsAmount = "" ;
						String custLimitCodeLimit = tKhLimitInfo.getAdjbase_code_limit() ;
						if (custLimitCodeLimit.equals(ICode.ADJBASE_CODE_LIMIT_01)) {
							// 수량 
							priceOrLiter = ICustConstant.LIMIT_AMOUNT_LITER ;
							limit = tKhLimitInfo.getLimit_cnt_monthly() ;
							usedAmount = tKhLimitInfo.getAcclimit_cnt_monthly() ;
							remainsAmount = GlobalUtility.substract(limit, usedAmount) ;
						} else {
							// 금액
							priceOrLiter = ICustConstant.LIMIT_AMOUNT_PRICE ;
							limit = tKhLimitInfo.getLimit_amt_monthly() ;
							usedAmount = tKhLimitInfo.getAcclimit_amt_monthly() ;
							remainsAmount = GlobalUtility.substract(limit, usedAmount) ;
						}						
						
						if (carLimitInfoData.getAdjbase_code_limit().equals(ICode.ADJBASE_CODE_LIMIT_01)) {
							// 수량 
							priceOrLiter = ICustConstant.LIMIT_AMOUNT_LITER ;
							limit = carLimitInfoData.getLimit_cnt_monthly() ;
							usedAmount = carLimitInfoData.getAcclimit_cnt_monthly() ;
							if (custLimitCodeLimit.equals(ICode.ADJBASE_CODE_LIMIT_02)) {
								remainsAmount = GlobalUtility.divide(remainsAmount,discountBasePrice) ;
							}
						} else {
							// 금액
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
						custReturnValue.setState(ICustConstant.STATE_50) ;			// 외상 거래처 - 거래처별한도 - 정상
						custReturnValue.setType(ICustConstant.TYPE_CUST_LIMIT) ;	// 거래처별
						custReturnValue.setDiscountBasePrice(discountBasePrice) ;
						custReturnValue.setLimitAmount(limitAmount) ;
						
						return custReturnValue ;	
					}
				}
				case ICode.CARDADJ_IND_03 : {	// 차량별
					if ((supportGoodsCode == false)) {
						custReturnValue.setState(ICustConstant.STATE_43) ;		// 외상 거래처 - 차량별한도 - 차량 유종 코드 불일치
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// 점두가 적용
						
						return custReturnValue ;	
					}
					
					T_KH_PLData plData = checkPLData(session, cust_code_rep, store_code) ;
					if (plData == null) {
						custReturnValue.setState(ICustConstant.STATE_42) ;		// 외상 거래처 - 차량별한도 - PL 문제
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// 점두가 적용
							
						return custReturnValue ;		
					}
					
					/*	T_KH_PL_PRICEData plPriceData = 
							checkPLPriceData(session, cust_code_rep, store_code, plData.getPl_no(), goods_code) ;
					*/					
					T_KH_PL_PRICEData plPriceData = 
						checkPLPriceData(session, cust_code_rep, store_code, plData.getPl_no(), goods_code) ;

					if (plPriceData == null) {
						custReturnValue.setState(ICustConstant.STATE_41) ;		// 외상 거래처 - 차량별한도 - 상품코드 불일치 
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// 점두가 적용
						
						return custReturnValue ;	
					} 			

					if (taxfreecust_type.equals(ICode.DX_TAXFREECUST_TYPE_1)) {
						if (!plPriceData.getTaxfree_cd().equals(ICode.TAXFREE_CD_02)) {
							LogUtility.getPumpMLogger().debug("[Pump M] 면세로 요청된 것이, PL 계약단가가 과세로 설정되어 있습니다.") ;
							custReturnValue.setState(ICustConstant.STATE_45) ;		// 면세로 요청했는데 면세 고객이 아님.
							custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// 점두가 적용
							
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
					 * 기존정보
					 * 차량별 한도 테이블에 정보가 없을 경우 무제한으로 처리
					 * 
					 * 추가 - [2008.11.06] by 오춘열 부장님.
					 * 		차량별 한도에서 무제한 처리
					 * 			- 차량별 한도인데 거래처카드에 차량번호를 등록하지 않은 경우
					 * 
					 */
					if ((tKhLimitInfo == null) || (carno_nbr == null) || ("".equals(carno_nbr))) {
						LogUtility.getPumpMLogger().info("[Pump M] 차량별 한도 테이블 혹은 차량 번호가 거래처 카드에 존재 하지 않아서 " +
								"무제한 거래처 처럼 처리한다.") ;
						String discountBasePrice = getDiscountBasePrice(productData, plPriceData) ;
						
						if (discountBasePrice == null) {
							custReturnValue.setState(ICustConstant.STATE_34) ;		// 외상 거래처 - 무제한 - 설정된 단가가 존재하지 않음
							custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// 점두가 적용
							
							return custReturnValue ;
						}
						custReturnValue.setUnitprc_code_stdn(plPriceData.getUnitprc_code_stdn()) ;
						custReturnValue.setTaxfree_cd(plPriceData.getTaxfree_cd());
						custReturnValue.setState(ICustConstant.STATE_30) ;		// 외상 거래처 - 무제한 - 정상
						custReturnValue.setType(ICustConstant.TYPE_CUST_BASEPRICE) ;	// 단가 적용
						custReturnValue.setDiscountBasePrice(discountBasePrice) ;
							
						return custReturnValue ;	
					}
					
					LimitAmount limitAmount = null ;
					int priceOrLiter = 0 ;
					String limit = "" ;
					String usedAmount = "" ;
					String discountBasePrice = getDiscountBasePrice(productData, plPriceData) ;	
					
					if (discountBasePrice == null) {
						custReturnValue.setState(ICustConstant.STATE_44) ;		// 외상 거래처 - 차량별한도 - 설정된 단가가 존재하지 않음
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// 점두가 적용
						
						return custReturnValue ;
					}

					if (tKhLimitInfo.getAdjbase_code_limit().equals(ICode.ADJBASE_CODE_LIMIT_01)) {
						// 수량 
						priceOrLiter = ICustConstant.LIMIT_AMOUNT_LITER ;
						limit = tKhLimitInfo.getLimit_cnt_monthly() ;
						usedAmount = tKhLimitInfo.getAcclimit_cnt_monthly() ;
					} else {
						// 단가
						priceOrLiter = ICustConstant.LIMIT_AMOUNT_PRICE ;
						limit = tKhLimitInfo.getLimit_amt_monthly() ;
						usedAmount = tKhLimitInfo.getAcclimit_amt_monthly() ;
					}
					limitAmount = new LimitAmount(priceOrLiter,limit,usedAmount) ;
					
					custReturnValue.setUnitprc_code_stdn(plPriceData.getUnitprc_code_stdn()) ;
					custReturnValue.setTaxfree_cd(plPriceData.getTaxfree_cd());
					custReturnValue.setState(ICustConstant.STATE_40) ;		// 외상 거래처 - 차량별한도 - 정상
					custReturnValue.setType(ICustConstant.TYPE_CAR_LIMIT) ;	// 차량별
					custReturnValue.setDiscountBasePrice(discountBasePrice) ;
					custReturnValue.setLimitAmount(limitAmount) ;
							
					return custReturnValue ;
				}
				case ICode.CARDADJ_IND_04 : {		// 1회 정량
					if ((supportGoodsCode == false)) {
						custReturnValue.setState(ICustConstant.STATE_63) ;		// 외상 거래처 - 1회 정량 - 차량 유종 코드 불일치
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// 점두가 적용
						
						return custReturnValue ;	
					}
					
					if (custCardInfoData == null) {
						LogUtility.getPumpMLogger().warn("[Pump M] 차량 번호만 등록된 고객은 1회 정량이 있을 수 없다. " +
							"하지만 1회 정량으로 등록 되어 있기 때문에, 이에 대해서 조사를 해야 한다.") ;
						custReturnValue.setState(ICustConstant.STATE_62) ;		// 외상 거래처 - 1 회 정량 - PL 문제
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// 점두가 적용
						
						return custReturnValue ;	
					} 
					
					T_KH_PLData plData = checkPLData(session, cust_code_rep, store_code) ;
					if (plData == null) {
						custReturnValue.setState(ICustConstant.STATE_62) ;		// 외상 거래처 - 1 회 정량 - PL 문제
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// 점두가 적용
						
						return custReturnValue ;	
					}
					
					/*	T_KH_PL_PRICEData plPriceData = 
							checkPLPriceData(session, cust_code_rep, store_code, plData.getPl_no(), goods_code) ;
					 */					
					T_KH_PL_PRICEData plPriceData = 
						checkPLPriceData(session, cust_code_rep, store_code, plData.getPl_no(), goods_code) ;


					if (plPriceData == null) {
						custReturnValue.setState(ICustConstant.STATE_61) ;		// 외상 거래처 - 1 회 정량 - 상품코드 불일치 
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// 점두가 적용
						
						return custReturnValue ;	
					} 			

					if (taxfreecust_type.equals(ICode.DX_TAXFREECUST_TYPE_1)) {
						if (!plPriceData.getTaxfree_cd().equals(ICode.TAXFREE_CD_02)) {
							LogUtility.getPumpMLogger().debug("[Pump M] 면세로 요청된 것이, PL 계약단가가 과세로 설정되어 있습니다.") ;
							custReturnValue.setState(ICustConstant.STATE_65) ;		// 면세로 요청했는데 면세 고객이 아님.
							custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// 점두가 적용
							
							return custReturnValue ;					
						}
					}
					
					custReturnValue.setAmount1(custCardInfoData.getSliplimit_amt_stdcapa()) ;

					String discountBasePrice = getDiscountBasePrice(productData, plPriceData) ;
					
					if (discountBasePrice == null) {
						custReturnValue.setState(ICustConstant.STATE_64) ;		// 외상 거래처 - 1회 정량 - 설정된 단가가 존재하지 않음
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// 점두가 적용
						
						return custReturnValue ;
					}
					custReturnValue.setUnitprc_code_stdn(plPriceData.getUnitprc_code_stdn()) ;
					custReturnValue.setTaxfree_cd(plPriceData.getTaxfree_cd());
					custReturnValue.setState(ICustConstant.STATE_60) ;		// 외상 거래처 - 1 회 정량 - 정상
					custReturnValue.setType(ICustConstant.TYPE_CUST_BASEPRICE) ;	// 단가 적용
					custReturnValue.setDiscountBasePrice(discountBasePrice) ;
						
					return custReturnValue ;	
				}
				case ICode.CARDADJ_IND_05 : {	// 정량 입력
					if ((supportGoodsCode == false)) {
						custReturnValue.setState(ICustConstant.STATE_93) ;		// 외상 거래처 - 정량입력 - 차량 유종 코드 불일치
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// 점두가 적용
						
						return custReturnValue ;	
					}
					
					T_KH_PLData plData = checkPLData(session, cust_code_rep, store_code) ;
					if (plData == null) {
						custReturnValue.setState(ICustConstant.STATE_92) ;		// 외상 거래처 - 정량입력 - PL 문제
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// 점두가 적용
						
						return custReturnValue ;	
					}

					/*	T_KH_PL_PRICEData plPriceData = 
					checkPLPriceData(session, cust_code_rep, store_code, plData.getPl_no(), goods_code) ;
					 */					
					T_KH_PL_PRICEData plPriceData = 
						checkPLPriceData(session, cust_code_rep, store_code, plData.getPl_no(), goods_code) ;


					if (plPriceData == null) {
						custReturnValue.setState(ICustConstant.STATE_91) ;		// 외상 거래처 - 정량입력 - 상품코드 불일치 
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// 점두가 적용
						
						return custReturnValue ;	
					} 			

					if (taxfreecust_type.equals(ICode.DX_TAXFREECUST_TYPE_1)) {
						if (!plPriceData.getTaxfree_cd().equals(ICode.TAXFREE_CD_02)) {
							LogUtility.getPumpMLogger().debug("[Pump M] 면세로 요청된 것이, PL 계약단가가 과세로 설정되어 있습니다.") ;
							custReturnValue.setState(ICustConstant.STATE_95) ;		// 면세로 요청했는데 면세 고객이 아님.
							custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// 점두가 적용
							
							return custReturnValue ;					
						}
					}
					
					String discountBasePrice = getDiscountBasePrice(productData, plPriceData) ;

					if (discountBasePrice == null) {
						custReturnValue.setState(ICustConstant.STATE_94) ;		// 외상 거래처 - 정량입력 - 설정된 단가가 존재하지 않음
						custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	// 점두가 적용
						
						return custReturnValue ;
					}
					
					custReturnValue.setUnitprc_code_stdn(plPriceData.getUnitprc_code_stdn()) ;
					custReturnValue.setTaxfree_cd(plPriceData.getTaxfree_cd());
					custReturnValue.setState(ICustConstant.STATE_90) ;		// 외상 거래처 - 정량입력  - 정상
					custReturnValue.setType(ICustConstant.TYPE_CUST_BASEPRICE) ;	// 단가 적용
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
	 * 현금 거래처 고객을 위한 로직 수행을 요청한다.
	 * 
	 * @param con : Connection 
	 * @param custInfoData : 거래처 정보
	 * @param productData : 상품테이블 정보
	 * @param nzNozzleData : 노즐 정보
	 * @param supportGoodsCode : 차량 마스터에 의해 유종 일치 여부
	 * @param taxfreecust_type	: 0 : 면세 아님, 1:등록 거래처 , 2: 일반
	 * @return
	 * 		CustReturnValue
	 */
	private static CustReturnValue getCustReturnValueForVIP(	SqlSession session, 
																T_KH_CUST_INFOData custInfoData, 
																T_KH_PRODUCTData productData, 
																boolean supportGoodsCode, 
																String taxfreecust_type) {

		// Debug
			LogUtility.getPumpMLogger().debug("[Pump M] VIP 고객 처리를 위한 고객 데이터입니다." + "supportGoodsCode="+
													supportGoodsCode+"#taxfreecust_type="+taxfreecust_type) ;
			if (custInfoData != null) {
				custInfoData.print() ;
			}
			if (productData != null) {
				productData.print() ;
			} else {
				LogUtility.getPumpMLogger().debug("[Pump M] productData is null") ;
			}
		
		String cust_code_rep = custInfoData.getCust_code_rep() ; 	// 거래처 대표 코드 - PL 여신 체크시 사용한다.
		String store_code = custInfoData.getStore_code() ;
		CustReturnValue custReturnValue = new CustReturnValue() ;
		String goods_code = productData.getGoods_code() ;

		try {
			if ((supportGoodsCode == false)) {
				custReturnValue.setState(ICustConstant.STATE_13) ;		// 현금 거래처 - 차량 유종 코드 불일치
				custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	
				
				return custReturnValue ;
			}
			
			T_KH_PLData plData = checkPLData(session, cust_code_rep, store_code) ;
			if (plData == null) {
				custReturnValue.setState(ICustConstant.STATE_12) ;		// PL 여신 문제
				custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	
						
				return custReturnValue ;
			}
			
			/*					T_KH_PL_PRICEData plPriceData = 
			checkPLPriceData(con, cust_code_rep, store_code, plData.getPl_no(), goods_code) ;
*/					
			T_KH_PL_PRICEData plPriceData = checkPLPriceData(session, cust_code_rep, store_code, plData.getPl_no(), goods_code) ;

			
			if (plPriceData == null) {
				custReturnValue.setState(ICustConstant.STATE_11) ;		//  PL 상품 코드 불일치
				custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	
				
				return custReturnValue ;
			}
			
			if (taxfreecust_type.equals(ICode.DX_TAXFREECUST_TYPE_1)) {
				if (!plPriceData.getTaxfree_cd().equals(ICode.TAXFREE_CD_02)) {
					LogUtility.getPumpMLogger().debug("[Pump M] 면세로 요청된 것이, PL 계약단가가 과세로 설정되어 있습니다.") ;
					custReturnValue.setState(ICustConstant.STATE_15) ;		// 현금 거래처 - 면세로 요청했는데 면세 고객이 아님.
					custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	
					
					return custReturnValue ;					
				}
			}
			
			String discountBasePrice = getDiscountBasePrice(productData, plPriceData) ;
			
			if (discountBasePrice == null) {
				custReturnValue.setState(ICustConstant.STATE_14) ;		// 현금 거래처 - 설정된 단가가 존재하지 않음
				custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;	
				
				return custReturnValue ;
			}

			custReturnValue.setUnitprc_code_stdn(plPriceData.getUnitprc_code_stdn()) ;
			custReturnValue.setTaxfree_cd(plPriceData.getTaxfree_cd());
			custReturnValue.setState(ICustConstant.STATE_10) ;		// 정상
			custReturnValue.setType(ICustConstant.TYPE_CUST_BASEPRICE) ;	// 할인단가 적용
			custReturnValue.setDiscountBasePrice(discountBasePrice) ;		//할인 단가
			custReturnValue.setCust_code(cust_code_rep);	//거래처 코드
			
			// 2013.10.14 ksm 현금거래처이며 정상 할인단가 적용됐을 경우 할인여부 셋팅.
			custReturnValue.setPlunit_yn("1");
			
			return custReturnValue ;	// 현금고객 - 이상무 
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		return null ;
	}

	/**
	 * 할인 (or 할인되지 않은) 단가 계산을 요청한다.
	 * 
	 * @param productData : 상품 정보
	 * @param tKhPLPrice : PL 여신 정보
	 * @return
	 * 		할인 단가
	 */
	private static String getDiscountBasePrice(T_KH_PRODUCTData productData , T_KH_PL_PRICEData tKhPLPrice) {
		
		// Debug purpose
			LogUtility.getPumpMLogger().debug("[Pump M] 할인단가를 구하려고 합니다. 할인 단가를 구하기 위한 정보는 다음과 같습니다.") ;
			productData.print() ;
			tKhPLPrice.print() ;
		
		String discountBaesPrice = null ;
		String contcond_code = tKhPLPrice.getContcond_code() ;
		int contcond_codeInt = Integer.parseInt(contcond_code) ;
		String basePrice = null ;
		String unitprc_code_stdn = null ;
		
		switch (contcond_codeInt) {			
			case 1 : {
				// 율 
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
					// 할인
					discountBaesPriceDouble = GlobalUtility.substract(basePriceDouble , (basePriceDouble * dscnt_val_rateDouble * 0.01)) ;
				} else {
					// 할증
					discountBaesPriceDouble = GlobalUtility.plus(basePriceDouble ,(basePriceDouble * dscnt_val_rateDouble * 0.01)) ;
				}
				
				// 할인율 소수점 처리방식 적용
				discountBaesPriceDouble = GlobalUtility.getCodeDecPoint(discountBaesPriceDouble , digit_code_decpoint_discontrate, way_code_decpoint_discontrate) ;
				discountBaesPrice = Double.toString(discountBaesPriceDouble) ;
				break ;
			}
			case 2 : {
				// 금액
				unitprc_code_stdn = tKhPLPrice.getUnitprc_code_stdn() ;
				basePrice = PumpMUtil.getBasePriceWithUnitPrcCode(unitprc_code_stdn, productData) ;
				
				if (basePrice == null) return null ;
				
				String dscnt_code_type = tKhPLPrice.getDscnt_code_type() ;
				String dscnt_amt = tKhPLPrice.getDscnt_amt() ;
				
				double basePriceDouble = Double.parseDouble(basePrice) ;
				double dscnt_amtDouble = Double.parseDouble(dscnt_amt) ;
				double discountBaesPriceDouble = 0 ;
				if (dscnt_code_type.equals(ICode.DSCNT_CODE_TYPE_01)) {
					// 할인
					discountBaesPriceDouble = GlobalUtility.substract(basePriceDouble , dscnt_amtDouble) ;
				} else {
					// 할증
					discountBaesPriceDouble = GlobalUtility.plus(basePriceDouble , dscnt_amtDouble) ;
				}
				discountBaesPrice = Double.toString(discountBaesPriceDouble) ;
				break ;
			}
			case 3 : {
				// 협정단가
				discountBaesPrice = tKhPLPrice.getPrc_amt_agree() ;
				break ;
			}
		}
		
		return GlobalUtility.getStringValue(discountBaesPrice) ;
	}
	
	/**
	 * 차량별 한도 정보를 차량별 한도 마스터로부터 가져온다.
	 * 
	 * @param con			: Connection
	 * @param custCarInfo	: 차량 마스터 정보
	 * @param productData	: 주유된 노즐의 상품 정보
	 * @return
	 */
	private static T_KH_CAR_LIMIT_INFOData getT_KH_CAR_LIMIT_INFOData(SqlSession session, T_KH_CUST_CAR_INFOData custCarInfo, 
			T_KH_PRODUCTData productData) {
		
		T_KH_CAR_LIMIT_INFOData carLimitInfoData = null ;

		LogUtility.getPumpMLogger().debug("[Pump M] 차량별 한도 정보를 요청하기 위한 데이터들이다.") ;			
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

		LogUtility.getPumpMLogger().debug("[Pump M] 차량별 한도 정보결과는 다음과 같습니다. ") ;			
		if (carLimitInfoData != null) {
			carLimitInfoData.print() ;
		} else {
			LogUtility.getPumpMLogger().debug("[Pump M] carLimitInfoData=" + carLimitInfoData );
		}	
		return carLimitInfoData ;
	}
	
	
	/**
	 * 거래처 한도 정보를 거래처 테이블로 부터 가져 온다.
	 * 
	 * @param con			: Connection
	 * @param custInfoData	: 거래처 마스터 정보
	 * @param productData	: 주유된 노즐의 상품 정보
	 * @return
	 */
	private static T_KH_CUST_LIMIT_INFOData getT_KH_CUST_LIMIT_INFOData(SqlSession session, T_KH_CUST_INFOData custInfoData, 
			T_KH_PRODUCTData productData) {
		
		T_KH_CUST_LIMIT_INFOData custLimitInfoData = null ;

		LogUtility.getPumpMLogger().debug("[Pump M] 거래처별 한도 정보를 요청하기 위한 데이터들이다.") ;			
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

		LogUtility.getPumpMLogger().debug("[Pump M] 거래처별 한도 정보결과는 다음과 같습니다.") ;			
		if (custLimitInfoData != null) {
			custLimitInfoData.print() ;
		} else {
			LogUtility.getPumpMLogger().debug("[Pump M] custLimitInfoData=" + custLimitInfoData );
		}
		
		return custLimitInfoData ;
	}
	
	/**
	 * 다쓰노 셀프 odt에서 HE전문을 받아서 POS로부터 받아온 DW전문을 가지고 해당 거래 승인 여부를 판단한다.
	 * @return
	 * @param remainAmt : 잔량
	 * @param reqAmt : 승인 요청 수량
	 * @param rentlimit_proc_ind_overlimit : 외상결제 타입
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
	 * 단가할인 로직을 적용할지 여부를 판단
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
			LogUtility.getPumpMLogger().info("[Pump M]-영업화물 할인 bsCardNo=" + bsCardNo + "#creditCardNo=" + creditCardNo);
			
			//영업화물 우대카드 BIN체크
			boolean isCargo = T_KH_BIN_INFOHandler.getHandler().isCargo(session, bsCardNo);
			
			//화물운전자 복지카드 체크
			boolean isCreditCargo = T_KH_BIN_INFOHandler.getHandler().isCreditCargo(session, creditCardNo, ICode.BIN_INFO_06);
			
			//거래카드 체크
			boolean isDealCargo = T_KH_BIN_INFOHandler.getHandler().isCreditCargo(session, creditCardNo, ICode.BIN_INFO_03);
			
			LogUtility.getPumpMLogger().debug(	"[Pump M] 영업화물 테스트. isCargo=" + isCargo +
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
	 * 영업화물 거래처 로직 추가
	 * 거래처카드(영업화물우대카드), 보너스 카드(영업화물우대카드) , 신용카드(화물운전자복지카드/거래카드) 
	 * 이 조건이 만족하는 경우에만 할인 단가를 적용해준다.
	 * 거래처 카드가 일반 거래처 카드인 경우에는 등록된 PL을 적용한다.
	 * 보너스카드가 영업화물 우대카드이고 신용카드가 화물운전자복지카드/거래카드 인 경우에 
	 * 4201. 4291 전문 Filler2에 'U'를 넣어준다. 
	 * 
	 */

	public static UPOSMessage isDiscountCargo(UPOSMessage rcvUposMsg, UPOSMessage sndUposMsg, String cust_card_no){

		String bsCardNo = rcvUposMsg.getBonRSCard_no();
		String creditCardNo = rcvUposMsg.getCreditCard_no();
		boolean isDiscountCargo = false;
		
		isDiscountCargo = isDiscountCargo(bsCardNo, creditCardNo);
		
		if (!isDiscountCargo) {
			//위의 조건이 만족하지 않으면 할인단가 적용 안함.
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
	 * 국민 유가보조 카드 단가할인 로직을 적용할지 여부를 판단
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
			
			//국민 유가보조 카드 체크
			isKBCard = T_KH_BIN_INFOHandler.getHandler().isCreditCargo(session, creditCardNo, ICode.BIN_INFO_09);
			
			if (!"1".equals(T_KH_CODEMASTERHandler.getHandler().codeMaster.get(IConstant.POSPORTOCOL_CODEMASTER_0334))) {
				LogUtility.getPumpMLogger().debug("[Pump M] 국민 유가보조 카드. 환경설정 : 0334 (사용안함 설정)"  );
				isKBCard = false;
			}
			
			LogUtility.getPumpMLogger().debug("[Pump M] 국민 유가보조 카드. isKBCard=" + isKBCard  );
		
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
			return isKBCard;
		}
		
	}
	
	/**
	 * 유가보조 거래처 로직 추가
	 * 거래처카드, , 신용카드(국민 거래카드) 
	 * PL 계약단가가 점두가보다 할인된 단가이면 PL단가를 적용하고 그렇지 않으면 점두가에 15원 할인을 적용한다.
	 * @param rcvUposMsg
	 * @param sndUposMsg
	 * @param cust_card_no
	 */
	// 2013.10.10 ksm 유가보조 거래의 경우 PL적용한 단가인 경우  PL단가로 적용됨. - 장C, 전D, 김D 확인.
	public static UPOSMessage isOilPriceSupport(UPOSMessage rcvUposMsg, UPOSMessage sndUposMsg, String unitDiscount_ind) {
		String creditCardNo = rcvUposMsg.getCreditCard_no();
		boolean isDiscountKBCard = false;
		
		ArrayList<UPOSMessage_ItemInfo_Item> itemList = sndUposMsg.getItem_info().getItemInfoList();

		if (itemList == null) return sndUposMsg;
		
		UPOSMessage_ItemInfo_Item itemInfoItem = itemList.get(0);

		// pl할인이 없는 경우 카드할인 적용
		// 2013.10.06 ksm  단가할인 여부 조건 추가.
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
	 * 단가할인 로직을 적용할지 여부를 판단
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
			
			//택시 복지카드 체크(결제)
			boolean isCreditTaxi = T_KH_BIN_INFOHandler.getHandler().isCreditCargo(session, creditCardNo, ICode.BIN_INFO_07);
			//택시 우대카드 빈 체크(보너스 카드)
			boolean isTaxiBs = T_KH_BIN_INFOHandler.getHandler().isCreditCargo(session, bsCardNo, ICode.BIN_INFO_08);
			
			LogUtility.getPumpMLogger().debug("[Pump M] 택시 더블적립. isCreditTaxi=" + isCreditTaxi + "#isTaxiBs=" + isTaxiBs  );
			
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
	 * PL 계약 단가를 가져 온다.
	 * 
	 * @param con			: Connection
	 * @param cust_code_rep	: 대표 거래처 번호
	 * @param store_code	: 매장 번호
	 * @param pl_no			: PL 번호
	 * @param goods_code	: 상품 번호
	 * @return
	 * @throws Exception
	 *//*
	private static T_KH_PL_PRICEData checkPLPriceData(SqlSession session, String cust_code_rep, String store_code, 
			String pl_no, String goods_code) throws Exception {
		if (LogUtility.getPumpMLogger().getLevel().toInt() <= Level.DEBUG_INT) {
			LogUtility.getPumpMLogger().debug("[Pump M] 상품 코드와 일치하는 거래처 정보의 PL 계약 단가를 조사합니다." +
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
			LogUtility.getPumpMLogger().debug("[Pump M] 상품 코드와 일치하는 거래처 정보의 PL 계약단가를  조사결과 다음과 같다.") ;
			if (plPriceData != null) plPriceData.print() ;
			else {
				LogUtility.getPumpMLogger().debug("[Pump M] 일치하는 PL 이 없습니다.") ;
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
	 * 차량 단축 번호를 통해서 차량 조회 요청
	 * 
	 * @param syncUnique	: Unique Key
	 * @param nozzle_no		: 노즐 번호
	 * @param car_short_no	: 차량 단축 번호
	 * @return
	 */
	public static POS_DU processCustomerCar(String syncUnique, String nozzle_no, String car_short_no) {
		LogUtility.getPumpMLogger().info("[Pump M] 차량 단축번호를 통해서 차량 조회를 수행합니다.") ;
		POS_DU_Car[] duPumpMCarArray = PumpMUtil.processPOSPumpM_DU_Car(car_short_no) ;
		POS_DU duPumpM = new POS_DU(syncUnique, nozzle_no , duPumpMCarArray) ;		
		
		// Debug Purpose
		LogUtility.getPumpMLogger().debug("[Pump M] 차량 단축번호를 통해서 차량 조회를 수행하여 결과는 다음과 같습니다.") ;
		LogUtility.getPumpMLogger().info(duPumpM.toString());
		
		return duPumpM ;
	}
	
	/**
	 * 차량 단축 번호를 통해서 차량 조회 요청 수행 이후 Preamble Object 를 생성한다.
	 * 
	 * @param syncUnique	: Unique Key
	 * @param messageType	: Message Type
	 * @param uPosMsg		: 요청 전문
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
	* 1. 메소드명 : processCustomerInfoToDY
	* 2. 작성일 : 2016. 4. 14. 오후 15:48:31, PI2.
	* 3. 작성자 : WooChul Jung
	* 4. 설명 :고객 카드 수행 요청을 한다.
	* 5. 변경이력:	차량 번호로 고객 카드 수행 요청시, 차량 번호 + 거래처 코드를 이용하여 수행 필요
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
		String cust_card_ind = ICode.CUST_CARD_IND_02 ;					// 차량번호/거래처카드번호 유무
		String cust_card_no = uPosMsg.getCustCard_No() ;				// 거래처 카드 번호
		String taxFreeCust_type = uPosMsg.getTaxFreeCust_type() ;		// 면세 거래처 여부
		String fixedQty_yn = uPosMsg.getFixedQty_yn() ;					// 정량입력 여부
		String fixedQty = "" ;											// 정량값
		String goods_code = itemInfoItem.getGoodsCode() ;
		String basePrice = PumpMUtil.convertNumberFormatFromUPOSToStandardFormat(itemInfoItem.getUnitPrice_before_discount()) ;
		String liter = PumpMUtil.convertNumberFormatFromUPOSToStandardFormat(itemInfoItem.getOilAmount()) ;
		String price = itemInfoItem.getOilPrice_before_discount() ;
		String khTransactionID = itemInfoItem.getKhTransactionID() ;	
		String custCard_car_type = uPosMsg.getCustCard_car_type() ;
		String cust_code = uPosMsg.getFiller2() ; 						// 거래처 코드. 2016. 4. 14. 오후 15:48:31, PI2, WooChul Jung. Bug Fix
		
		// 정량 입력 여부
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
			LogUtility.getPumpMLogger().error("[Pump M] 고객 카드 수행하는 중에 에러가 발생했습니다. 이는 존재하지 않기 때문에 일어났을 확률이 높기 " +
					" 때문에 고객 카드가 존재하지 않는 것으로 처리합니다.") ;
			dyPumpM = null ;
		}
		
		if (dyPumpM != null) {
			LogUtility.getPumpMLogger().debug("[Pump M] 고객 카드 수행 결과 다음과 같습니다.") ;
			LogUtility.getPumpMLogger().info(dyPumpM.toString());
		} else {
			LogUtility.getPumpMLogger().warn("[Pump M] 고객 카드 수행 결과 없습니다.") ;
		}
		
		return dyPumpM ;
	}
	
	/**
	 * 고객 카드 수행 요청 수행 이후 Preamble Object 를 생성한다.
	 * 
	 * @param syncUnique	: Unique Key
	 * @param messageType	: Message Type
	 * @param uPosMsg		: 요청 전문
	 * @return
	 */
	public static Preamble processCustomerInfoToPreamble(UPOSMessage_ItemInfo_Item itemInfoItem, 
			String syncUnique, int messageType, UPOSMessage uPosMsg) {
		
		POS_DY dyPumpM = CustUtil.processCustomerInfoToDY(itemInfoItem, syncUnique, uPosMsg) ;	
		if (dyPumpM == null) {
			LogUtility.getPumpMLogger().info("[Pump M] 거래처 수행은 하였지만 정보가 없습니다. 주유중/완료 정보 요청을 처리를 합니다.") ;
		} else {
			// Debug Purpose
			LogUtility.getPumpMLogger().info("[Pump M] 거래처 수행을 하였고, 정보는 아래와 같습니다.") ;
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
		
		// 영업화물인 경우 처리
		if (ICode.CUST_CD_ITEM_05.equals(dyPumpM.getCust_cd_item()))
			sendUPOSMsg = CustUtil.isDiscountCargo(uPosMsg, sendUPOSMsg, dyPumpM.getCust_card_no());
		
		// 유가보조거래처 처리
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
	 * 차량 번호를 통해서 거래처 로직 수행을 요청한다.
	 * 면세 여부가 만약 등록 거래처인 경우, PL 계약 단가가 면세인지 여부를 체크 한다.
	 * 
	 * @param nozzle_no		: 노즐 번호
	 * @param car_no	: 차량 번호
	 * @param taxfreecust_type	: 0 : 면세 아님, 1:등록 거래처 , 2: 일반
	 * @param cust_code	: 거래처 코드
	 * @return
	 */
	public static CustReturnValue processCustWithCarNo(String nozzle_no, String car_no, String taxfreecust_type, String cust_code) {
		
		LogUtility.getPumpMLogger().info("[Pump M] 차량 번호를 이용하여 거래처 로직을 수행합니다. " +
				"노즐번호=" + nozzle_no + "#차량번호=" + car_no + "#거래처코드=" + cust_code) ;		

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
				throw new CustException("[Pump M] 거래처 차량 번호가  거래처 차량 마스터에 없습니다.car_no=" + car_no) ;
			}
			
			custInfoData = 
				T_KH_CUST_INFOHandler.getHandler().getT_KH_CUST_INFOData(session, custCarInfoData.getCust_code(), storeCode) ;
			
			if (custInfoData == null) {
				throw new CustException("[Pump M] 거래처 차량 번호가  거래처 마스터에 없습니다.car_no=" + car_no) ;
			}
			
			card_code_base = custInfoData.getCard_code_base() ;
			proc_ind_overlimit = custInfoData.getProc_ind_overlimit() ;
			rcptunitprc_ind_prt = custInfoData.getRcptunitprc_ind_prt() ;
			rcptsheetissue_ind_prtcarno = custInfoData.getRcptsheetissue_ind_prtcarno() ;
			// 2016. 4. 14. 오후 15:48:31, PI2, twsongkis 
			// codemaster 기준으로 보관증 발행여부를 한번 더 체크.
			keepissue_ind = custInfoData.getKeepissue_ind() ;
			if(ICode.KEEPISSUE_IND_1.equals(keepissue_ind)){
				keepissue_ind = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0019);
			}
			rcptsheetissue_code_amtsale = custInfoData.getRcptsheetissue_code_amtsale() ;
			cust_cd_item = custInfoData.getCust_cd_item() ;
			
			switch (card_code_base) {				
				case ICode.CARD_CODE_BASE_01 : {
					// 현금 거래처
					custReturnValue = getCustReturnValueForVIP(session, custInfoData, productData,  
							true, taxfreecust_type) ;
					break ;
				}
				case ICode.CARD_CODE_BASE_02 : {
					// 외상 거래처
					custReturnValue = getCustReturnValueForRentCustomer(session, custInfoData, 
							custCarInfoData, null, productData, true, taxfreecust_type) ;	
					break ;
				}
				case ICode.CARD_CODE_BASE_03 : {
					// 용역 보관	- 점두가로 허용 
					custReturnValue = new CustReturnValue() ;
					custReturnValue.setState(ICustConstant.STATE_70) ;
					custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;
					String basePrice = PumpMUtil.getBasePrice(productData, nzNozzleData) ;
					custReturnValue.setDiscountBasePrice(basePrice) ;
					break ;
				}
				case ICode.CARD_CODE_BASE_04 : {
					// 매출 보관	- 점두가로 허용 		
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
			// VIP , 영업화물인지 조사
			if (isSuccess(custReturnValue.getState())) {
				if (ICode.CUST_CD_ITEM_05.equals(custReturnValue.getCust_cd_item())) {
					custReturnValue.setState(ICustConstant.STATE_100) ;		// 영업화물
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
		LogUtility.getPumpMLogger().debug("[Pump M] 거래처카드 수행결과는 다음과 같습니다." ) ;
		if (custReturnValue == null) {
			LogUtility.getPumpMLogger().debug("[Pump M] 등록이 되어 있지 않아서 Default 전문을 구성합니다.") ;
			custReturnValue = new CustReturnValue() ;
			custReturnValue.setState(ICustConstant.STATE_1) ;
			custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;
		}
		custReturnValue.print() ;
		return custReturnValue ;
	}
	

	/**
	 * 거래처 카드 번호에 따른 거래처 로직 수행을 요청한다.
	 * 
	 * @param nozzle_no : 노즐 번호
	 * @param cardno_nbr_cust : 거래처 카드 번호
	 * @param taxfreecust_type	: 0 : 면세 관계 없음 아님, 1: CAT 단말기로 부터 면세 고객으로 요첟되었음 , 2: 일반(사용되지 않음)
	 * @return
	 * 2013.10.08 ksm PL이 존재하여 PL단가가 적용될 경우 알수있도록 수정 필요.
	 */
	public static CustReturnValue processCustWithCustCardNo(String nozzle_no , String cardno_nbr_cust, String taxfreecust_type) {
		
		LogUtility.getPumpMLogger().info("[Pump M] 거래처 카드를 수행합니다. nozzle_no=" + nozzle_no + ". cardno_nbr_cust=" + Base64Util.encode(cardno_nbr_cust.split("=")[0])) ;

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
				LogUtility.getPumpMLogger().warn("[Pump M] 거래처 카드가 T_KH_CUST_CARD_INFO 테이블에 없습니다. 영업화물인지 조사합니다.") ;
				
				// 영업화물인지 조사 , cust_cd_item=05
				boolean isCargo = T_KH_BIN_INFOHandler.getHandler().isCargo(session, cardno_nbr_cust) ;
				if (isCargo) {
					LogUtility.getPumpMLogger().info("[Pump M] 영업화물입니다. 거래처 카드 마스터에서 영업화물 정보를 가져옵니다.") ;
					custInfoData = T_KH_CUST_INFOHandler.getHandler().getT_KH_CUST_INFODataByCust_cd_item(session, ICode.CUST_CD_ITEM_05) ;
				}
				
				if (custInfoData == null) {
					throw new CustException("[Pump M] 거래처 카드가 거래처 마스터에 없습니다.cardno_nbr_cust=" +  Base64Util.encode(cardno_nbr_cust.split("=")[0])) ;
				}
			}
			
			if (custInfoData == null) {
				custInfoData = T_KH_CUST_INFOHandler.getHandler().getT_KH_CUST_INFOData(session, custCardInfoData.getCust_code(), custCardInfoData.getStore_code()) ;
			}
			
			if (custInfoData == null) {
				throw new CustException("[Pump M] 거래처 카드가 거래처 마스터에 없습니다.cardno_nbr_cust=" +  Base64Util.encode(cardno_nbr_cust.split("=")[0])) ;
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
				LogUtility.getPumpMLogger().debug("[Pump M] 거래처 카드 정보에 차량 번호가 없다. 이는 허용한다.") ;
				custCarInfoData = null ;
				supportGoodsCode = true ;				
			} else if ((custCarInfoDataArray == null) || (custCarInfoDataArray.length == 0)) {
				// 차량이 등록되지 않은 거래처 카드는 전체 유종에 대해서 지원한다라고 전제한다. 하지만 차량이 있는 경우는 차량마스터의 유종 코드와 비교한다.
				LogUtility.getPumpMLogger().debug("[Pump M] 거래처 카드는 있지만 차량은 등록되지 않은 고객이어서 유종을 허용한다.") ;
				custCarInfoData = null ;
				supportGoodsCode = true ;
			} else {
				custCarInfoData = custCarInfoDataArray[0] ;
				
				// debug
				custCarInfoData.print() ;
				
				if (compareGoodsCodeInCustCarInfoTable(productData, custCarInfoData)) {
					LogUtility.getPumpMLogger().debug("[Pump M] 차량 마스터 내의 거래처 카드 정보를 이용한 결과 노즐 유종을 지원한다.") ;
					supportGoodsCode = true ;
				} else {
					LogUtility.getPumpMLogger().debug("[Pump M] 거래처 카드의 차량은 차량마스터에 등록되어 있지만 노즐이 틀려 지원하지 않습니다.") ;
					custCarInfoData = null ;
					supportGoodsCode = false ;
				}
			}
			
			switch (card_code_base) {
				case ICode.CARD_CODE_BASE_01 : {
					// 현금 거래처
					custReturnValue = getCustReturnValueForVIP(	session, 
							custInfoData, 
							productData,  
							supportGoodsCode, 
							taxfreecust_type) ;
					break ;
				}
				case ICode.CARD_CODE_BASE_02 : {
					// 외상 거래처
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
					// 용역 보관	- 점두가로 허용 
					custReturnValue = new CustReturnValue() ;
					custReturnValue.setState(ICustConstant.STATE_70) ;
					custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;
					String basePrice = PumpMUtil.getBasePrice(productData, nzNozzleData) ;
					custReturnValue.setDiscountBasePrice(basePrice) ;
					break ;
				}
				case ICode.CARD_CODE_BASE_04 : {
					// 매출 보관	- 점두가로 허용 		
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
			// VIP , 영업화물인지 조사
			if (isSuccess(custReturnValue.getState())) {
				if (ICode.CUST_CD_ITEM_05.equals(custReturnValue.getCust_cd_item())) {
					custReturnValue.setState(ICustConstant.STATE_100) ;		// 영업화물
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
		
		LogUtility.getPumpMLogger().debug("[Pump M] 거래처카드 수행결과는 다음과 같습니다." ) ;
		if (custReturnValue == null) {
			LogUtility.getPumpMLogger().debug("[Pump M] 등록이 되어 있지 않아서 Default 전문을 구성합니다.") ;
			custReturnValue = new CustReturnValue() ;
			custReturnValue.setState(ICustConstant.STATE_1) ;
			custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;
		}
		custReturnValue.print() ;
		return custReturnValue ;
	}
}
