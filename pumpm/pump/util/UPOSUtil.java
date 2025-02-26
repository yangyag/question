package com.gsc.kixxhub.module.pumpm.pump.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import org.apache.ibatis.session.SqlSession;

import com.gsc.kixxhub.common.data.ICode;
import com.gsc.kixxhub.common.data.IConstant;
import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.IUPOSConstant;
import com.gsc.kixxhub.common.data.posdata.POSHeader;
import com.gsc.kixxhub.common.data.posdata.POS_DU;
import com.gsc.kixxhub.common.data.posdata.POS_DU_Car;
import com.gsc.kixxhub.common.data.posdata.POS_DW;
import com.gsc.kixxhub.common.data.posdata.POS_DY;
import com.gsc.kixxhub.common.data.posdata.POS_DY_Item;
import com.gsc.kixxhub.common.data.pump.S8_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.data.upos.CreateUPOSMessage;
import com.gsc.kixxhub.common.data.upos.UPOSMessage;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_AffilateInfo;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_AffilateInfo2;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_AffilateInfo_Item;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_AffilateInfo_Item2;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_BarcodeInfo;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_BarcodeInfo_Item;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_BarcodeInfo_Set;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_CampInfo;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_CatalogInfo;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_CatalogInfo_Goods_Item;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_CatalogInfo_Nozzle_Item;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_ItemInfo;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_ItemInfo_Item;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_TradeCondition;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_TradeCondition_Item;
import com.gsc.kixxhub.common.dbadapter.common.SqlSessionFactoryManager;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_CODEMASTERHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_POS_INFOHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_PRODUCTHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_PUMP_TRHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_SALES_INFOHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_STOREHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_CODEMASTERData;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_POS_INFOData;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_PRODUCTData;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_PUMP_TRData;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_SALES_INFOData;
import com.gsc.kixxhub.common.dbadapter.opt.handler.T_CL_DISCOUNTHandler;
import com.gsc.kixxhub.common.dbadapter.opt.vo.T_CL_DISCOUNTData;
import com.gsc.kixxhub.common.dbadapter.pump.handler.T_NZ_NOZZLEHandler;
import com.gsc.kixxhub.common.dbadapter.pump.vo.T_NZ_NOZZLEData;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.common.utility.property.PropertyManager;
import com.gsc.kixxhub.common.utility.sync.Preamble;
import com.gsc.kixxhub.common.utility.sync.SyncManager;
import com.gsc.kixxhub.module.pumpm.bundle.sub.StateMController;
import com.gsc.kixxhub.module.pumpm.pump.manager.PumpMODTSaleManager;
import com.gsc.kixxhub.module.pumpm.pump.manager.PumpMTransactionManager;
import com.gsc.kixxhub.module.pumpm.pump.manager.gsc.GSCSelfPumpingManager;

public class UPOSUtil {

	/**
	 * POS 로 부터 받은 DY 전문을 UPOSMessage_ItemInfo Class 로 변경한다.
	 * 
	 * @param dyPumpMsg :
	 *            POS 로 부터 받은 DY 전문
	 * @return
	 */
	public static UPOSMessage_ItemInfo convertToUPOSMessage_ItemInfo(
			POS_DY dyPumpMsg) {
		UPOSMessage_ItemInfo itemInfo = null;

		ArrayList<UPOSMessage_ItemInfo_Item> uPosMsgItemInfoArray = null;

		if (dyPumpMsg.getDup() != 0) {
			uPosMsgItemInfoArray = new ArrayList<UPOSMessage_ItemInfo_Item>();

			for (int i = 0; i < dyPumpMsg.getDup(); i++) {
				POS_DY_Item dyItem = dyPumpMsg.getDyInfoArray()[i];

				String nozzleNo = dyPumpMsg.getDeviceID();
				String goodsCode = dyItem.getGoodsCode(); // 상품 코드
				String bonGoodsCode = dyItem.getBonGoodsCode();
				String oil_ind = dyItem.getOil_ind(); // 유류/유외구분
				String unitPrice_before_discount = GlobalUtility.getStringValue(GlobalUtility.getMultipleWith1000(dyItem.getUnitPrice_before_discount())); // 할인전 단가
				String oilAmount = GlobalUtility.getStringValue(GlobalUtility.getMultipleWith1000(dyItem.getOilAmount())); // 수량
				String unitPrice_after_discount = GlobalUtility.getStringValue(GlobalUtility.getMultipleWith1000(dyItem.getUnitPrice_after_discount())); // 할인후 단가
				String tax_ind = dyItem.getTax_ind(); // 01:과세 , 02:면세
				String price_before_tax = GlobalUtility.getStringValue(dyItem.getPrice_before_tax()); // 공급가액
				String taxPrice = GlobalUtility.getStringValue(dyItem.getTaxPrice()); // 세금
				String oilPrice_before_discount = GlobalUtility.getStringValue(dyItem.getOilPrice_before_discount()); // 할인전 금액
				String oilPrice_after_discount = GlobalUtility.getStringValue(dyItem.getOilPrice_after_discount()); // 할인후 금액
				String khTransactionID = dyPumpMsg.getDyInfoArray()[0].getKhTransactionID(); // 전표번호
				String rentlimit_proc_ind_overlimit = dyItem.getRentlimit_proc_ind_overlimit(); // 외상결제타입
				String unitDiscount_ind = dyItem.getUnitDiscount_ind(); // 할인 여부
				String keep_no = dyItem.getKeepissue_no(); // 보관증번호(10b)
				String issue_type = dyItem.getIssue_type(); // 보관증 발행유형(2b)

				UPOSMessage_ItemInfo_Item itemInfo_item = CreateUPOSMessage
						.createUPOSMessage_ItemInfo_Item(nozzleNo, goodsCode,
								bonGoodsCode, oil_ind,
								unitPrice_before_discount, oilAmount,
								unitPrice_after_discount, tax_ind,
								price_before_tax, taxPrice,
								oilPrice_before_discount,
								oilPrice_after_discount, khTransactionID,
								rentlimit_proc_ind_overlimit, unitDiscount_ind,
								keep_no, issue_type);
				uPosMsgItemInfoArray.add(itemInfo_item);
			}
		}
		itemInfo = UPOSMessage_ItemInfo.createUPOSMessage_ItemInfo(uPosMsgItemInfoArray);

		return itemInfo;
	}

	/**
	 * 차량 단축 번호를 통한 차량번호를 조회하여 UPOSMessage 의 Spec 에 맞게 변경하여 응답한다.
	 * 
	 * @param carInfoArray :
	 *            차량 번호 리스트
	 * @return
	 */
	public static UPOSMessage_AffilateInfo createAffilate_info(
			POS_DU_Car[] carInfoArray) {
		UPOSMessage_AffilateInfo affilate_info = null;
		ArrayList<UPOSMessage_AffilateInfo_Item> affiliateInfoItemList = new ArrayList<UPOSMessage_AffilateInfo_Item>();
		for (int i = 0; i < carInfoArray.length; i++) {
			UPOSMessage_AffilateInfo_Item affiItem = new UPOSMessage_AffilateInfo_Item();
			/*String custName = "";
			if (carInfoArray[i].getCust_name().length() > 10)
				custName = carInfoArray[i].getCust_name().substring(0, 10);
			else
				custName = carInfoArray[i].getCust_name();

			affiItem.setCust_nm(custName);
			*/

			String[] ArrcustName = GlobalUtility.splitByteArrayToStringArray(
					carInfoArray[i].getCust_name().getBytes(),
					IUPOSConstant.DELIMITER_0X5E);

			if (ArrcustName.length == 2) {
				affiItem.setCust_nm(ArrcustName[0]);
				affiItem.setCust_code(ArrcustName[1]);
			} else {
				affiItem.setCust_nm(ArrcustName[0]);
			}
			
			affiItem.setCustcard_no(carInfoArray[i].getCust_card_no());
			affiItem.setCustcar_no(carInfoArray[i].getCar_no());
			affiliateInfoItemList.add(affiItem);
		}
		affilate_info = UPOSMessage_AffilateInfo
				.createUPOSMessage_Affilate(affiliateInfoItemList);
		return affilate_info;
	}

	/**
	 * 차량 단축 번호를 통한 차량번호를 조회하여 UPOSMessage 의 Spec 에 맞게 변경하여 응답한다.
	 * 
	 * @param carInfoArray :
	 *            차량 번호 리스트
	 * @return
	 */
	public static UPOSMessage_TradeCondition createAffilate_info(
			POS_DU_Car[] carInfoArray, boolean custCodeYn) {
		UPOSMessage_TradeCondition affilate_info = null;
		ArrayList<UPOSMessage_TradeCondition_Item> affiliateInfoItemList = new ArrayList<UPOSMessage_TradeCondition_Item>();
		for (int i = 0; i < carInfoArray.length; i++) {
			UPOSMessage_TradeCondition_Item affiItem = new UPOSMessage_TradeCondition_Item();

			String[] ArrcustName = GlobalUtility.splitByteArrayToStringArray(
					carInfoArray[i].getCust_name().getBytes(),
					IUPOSConstant.DELIMITER_0X5E);

			if (ArrcustName.length == 2) {
				affiItem.setCust_nm(ArrcustName[0]);
				affiItem.setCust_code(ArrcustName[1]);
			} else {
				affiItem.setCust_nm(ArrcustName[0]);
			}

			affiItem.setCustcard_no(carInfoArray[i].getCust_card_no());
			affiItem.setCustcar_no(carInfoArray[i].getCar_no());
			affiliateInfoItemList.add(affiItem);
		}
		affilate_info = UPOSMessage_TradeCondition
				.createUPOSMessage_TradeCondition(affiliateInfoItemList);
		return affilate_info;
	}

	/**
	 * 충전기 및 소모셀프의 판매완료 전문 수신 이후, 외상거래처 관련 결제가 있을 경우 ItemInfo 전문을 재구성한다. 이는 POS
	 * 로 전송하기 위함이다.
	 * 
	 * @param nozzle_no :
	 *            노즐 번호
	 * @param posReceipt_no :
	 *            KH 처리번호
	 * @param liter :
	 *            판매량
	 * @param salesBasePrice :
	 *            판매 단가
	 * @param pumpPrice :
	 *            주유 금액
	 * @param amt :
	 *            판매 금액
	 * @return
	 */
	public static UPOSMessage_ItemInfo createCustomerItemInfo(String nozzle_no,
			String posReceipt_no, String liter, String salesBasePrice,
			String pumpPrice, String amt) {
		UPOSMessage_ItemInfo itemInfo = null;
		UPOSMessage_ItemInfo_Item uPosMsgItemInfo = null;

		if (GlobalUtility.substract(Double.parseDouble(pumpPrice), Double
				.parseDouble(amt)) <= 0) {
			uPosMsgItemInfo = UPOSUtil.getUPOSMessage_ItemInfo_Item(nozzle_no,
					posReceipt_no, liter, salesBasePrice, pumpPrice);
		} else {
			uPosMsgItemInfo = UPOSUtil.getUPOSMessage_ItemInfo_Item(nozzle_no,
					posReceipt_no, liter, salesBasePrice, amt);
		}
		uPosMsgItemInfo.setRentlimit_proc_ind_overlimit(ICode.PROC_IND_OVERLIMIT_00); // 외상 인정
		itemInfo = UPOSMessage_ItemInfo
				.createUPOSMessage_ItemInfo(uPosMsgItemInfo);
		return itemInfo;
	}

	/**
	 * UPOSMessage 에 포함된 단가를 Pump A 의 Spec 에 맞게 변경하여 응답한다.
	 * 
	 * @param uPosMsg :
	 *            UPOSMessage Class (응답 전문)
	 * @return 승인 결제된 단가 금액
	 */
	public static String getBasePriceForPumpA(UPOSMessage uPosMsg) {
		double basePriceDouble = 0;

		UPOSMessage_ItemInfo item_info = uPosMsg.getItem_info();
		ArrayList<UPOSMessage_ItemInfo_Item> itemInfoList = item_info
				.getItemInfoList();

		if (Integer.parseInt(item_info.getRecordNo()) == 0) {
			SqlSession session = null;
			try {
				session = SqlSessionFactoryManager.openSqlSession();

				T_NZ_NOZZLEData tNzNozzleData = T_NZ_NOZZLEHandler.getHandler()
						.getT_NZ_NOZZLEDataByNozzleNo(session,
								uPosMsg.getNozzle_no())[0];
				T_KH_PRODUCTData productData = T_KH_PRODUCTHandler
						.getHandler()
						.getT_KH_PRODUCTData(session, tNzNozzleData.getGoods_code())[0];

				basePriceDouble = Double.parseDouble(PumpMUtil.getBasePrice(
						productData, tNzNozzleData));
			} catch (Exception e) {
				LogUtility.getPumpMLogger().error(e.getMessage(), e);
			} finally {
				SqlSessionFactoryManager.closeSqlSession(session);
			}
			return PumpMUtil.convertNumberFormatFromPOSToPump(Double
					.toString(basePriceDouble), 4, 2);
		}

		for (int i = 0; i < itemInfoList.size(); i++) {
			UPOSMessage_ItemInfo_Item itemInfo = itemInfoList.get(i);
			basePriceDouble = Double.parseDouble(itemInfo
					.getUnitPrice_after_discount());
			break;
		}

		return PumpMUtil.convertBasePriceFromUPOSToPumpA(Double
				.toString(basePriceDouble));
	}

	/**
	 * CAT 단말기 영수증 출력 여부 정보를 가져온다. (Default = 0) [2008.11.20] Default 값은 오춘열
	 * 부장님, 편윤국 차장님, 장윤기 대리의 협의하에 결정되었음
	 * 
	 * @return
	 */
	public static String getCATPrint_ind() {
		String catPrint_ind = "0";
		SqlSession session = null;
		try {
			session = SqlSessionFactoryManager.openSqlSession();
			T_KH_CODEMASTERData codeData = T_KH_CODEMASTERHandler.getHandler()
					.getT_KH_CODEMASTERDataByCode(session,
							IConstant.POSPORTOCOL_CODEMASTER_0040);
			String chg_dt = null;
			if (codeData != null) {
				catPrint_ind = codeData.getValue();
				chg_dt = codeData.getChg_dt();
				T_KH_PRODUCTHandler.getHandler().setDownloadFlag(chg_dt);
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}
		return catPrint_ind;
	}

	/**
	 * 상품테이블의 Download Flag 를 전송한다.
	 * 
	 * @return
	 */
	public static String getDownloadFlag() {
		String downloadFlag = null;
		try {
			downloadFlag = T_KH_PRODUCTHandler.getHandler().getDownloadFlag(
					false);
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		}
		return downloadFlag;
	}

	/**
	 * PI2, 2016-03-10, CWI
	 * ODT 로부터 승인 요청시 아래 정보를 가지고, 노즐정보(UPOSMessage_ItemInfo_Item) 를 만든다. 
	 * GSC Self 경우 할인된 단가로써 승인 요청이 올수 있다. 이로 인해 ODT 로 부터 받은 수량 및 단가 , 금액으로 Item
	 * InfoItem 을 구성한다. 그리고 할인전 단가 및 할인전 금액은 그 노즐의 점두가를 통해서 계산한다.
	 * 
	 * @param nozzleNo :
	 *            노즐번호
	 * @param khTransactionID :
	 *            킥스허브 번호         
	 * @param liter :
	 *            수량
	 * @param baseprice :
	 *            단가
	 * @param price :
	 *            금액        
	 * @param fixedQty_yn :
	 *            정량여부         
	 * @return
	 */
	public static UPOSMessage_ItemInfo_Item getGSCUPOSMessage_ItemInfo_Item(
			String nozzleNo, String khTransactionID, String liter,
			String baseprice, String price, String mobilePayYn) {
		
		/** 더블형 비교를 위한 변수 생성 */
		BigDecimal decimalLiter = new BigDecimal(liter);
		BigDecimal zero = new BigDecimal("0");
		
		T_KH_PUMP_TRData pumpTrData = null;
		
		LogUtility.getLogger().info("[Pump M] Receive payment and liter from ODT Device. nozzleNo="
						+ nozzleNo + ": price=" + price + ": liter=" + liter + ": baseprice=" + baseprice);
		
		Integer getPrice = Integer.parseInt(price);
		try {
			if (khTransactionID != null) {
				pumpTrData = getPumpingInfo(nozzleNo);
			}
		} catch (Exception e) {
			LogUtility.getLogger().error(e.getMessage(), e);
		}
		if(baseprice == null || baseprice.equals("")) baseprice = pumpTrData.getBaseprice();
		
		 // 금액 + 리터 기준
		if ((getPrice != null && getPrice != 0) && (decimalLiter != null && decimalLiter.compareTo(zero)!=0)) {
			
			//LogUtility.getLogger().info("[Pump M] 금액+리터 단위 pumpTrData 추가 처리 ") ;
			
			if (pumpTrData != null) {
				pumpTrData.setOil_preset_ind("1");		// 프리셋정보 여부 0:없음, 1:있음
				pumpTrData.setPreset_qty_prc_ind("2");  // 정액정량여부 0:정액, 1:정량, 2:정액+정약
				pumpTrData.setPreset_prc(price); // 금액
			
				// 거래처일 경우 넘어온 baseprice를 사용하며, 거래처가 아닐 경우 pumpTrData의 baseprice를 사용
				if(baseprice != null || !baseprice.equals("0") || !baseprice.equals("")){
					Double nbefore = Double.parseDouble(baseprice);
					BigDecimal before = new BigDecimal(Integer.parseInt(String.valueOf(Math.round(nbefore))));
					before.setScale(0, RoundingMode.HALF_UP);
					LogUtility.getLogger().info("[Pump M] before="+before.toString()) ;
					pumpTrData.setBaseprice(before.toString()); // 단가
					pumpTrData.setPreset_baseprice(before.toString()); // 정액정량단가 소수점3
				}else{
					pumpTrData.setPreset_baseprice(pumpTrData.getBaseprice()); // 정액정량단가 소수점3
				}
				// 수량
				BigDecimal pump_qty = new BigDecimal(liter).divide(new BigDecimal(1000));
				pumpTrData.setPreset_qty(pump_qty.toString());
			}
			
		}
		// 금액 단위 책정
		else if (getPrice != null && getPrice != 0) {
//			 LogUtility.getLogger().info("[Pump M] 금액 단위 pumpTrData 추가 처리 ") ;
			
			if (pumpTrData != null) {
				pumpTrData.setOil_preset_ind("1");		// 프리셋정보 여부 0:없음, 1:있음
				pumpTrData.setPreset_qty_prc_ind("0");  // 정액정량여부 0:정액, 1:정량, 2:정액+정약
				pumpTrData.setPreset_prc(price); // 정액
				
				// 거래처일 경우 넘어온 baseprice를 사용하며, 거래처가 아닐 경우 pumpTrData의 baseprice를 사용
				if(baseprice != null || !baseprice.equals("0") || !baseprice.equals("")){
					Double nbefore = Double.parseDouble(baseprice);
					BigDecimal before = new BigDecimal(Integer.parseInt(String.valueOf(Math.round(nbefore))));
					before.setScale(0, RoundingMode.HALF_UP);
					LogUtility.getLogger().info("[Pump M] before="+before.toString()) ;
					pumpTrData.setBaseprice(before.toString()); // 단가
					pumpTrData.setPreset_baseprice(before.toString()); // 정액정량단가 소수점3
				}else{
					pumpTrData.setPreset_baseprice(pumpTrData.getBaseprice()); // 정액정량단가 소수점3
				}
			}
			
		// 리터 단위 계산	
		} else if (decimalLiter != null && decimalLiter.compareTo(zero)!=0) {	
//			 LogUtility.getLogger().info("[Pump M] 리터 단위 pumpTrData 추가 처리 ") ;
			
			if (pumpTrData != null) {
				pumpTrData.setOil_preset_ind("1");		// 프리셋정보 여부 0:없음, 1:있음
				pumpTrData.setPreset_qty_prc_ind("1");  // 정액정량여부 0:정액, 1:정량, 2:정액+정약
				
				// 단가
				// 거래처일 경우 넘어온 baseprice를 사용하며, 거래처가 아닐 경우 pumpTrData의 baseprice를 사용
				if(baseprice != null || !baseprice.equals("0") || !baseprice.equals("")){
					Double nbefore = Double.parseDouble(baseprice);
					BigDecimal before = new BigDecimal(Integer.parseInt(String.valueOf(Math.round(nbefore))));
					before.setScale(0, RoundingMode.HALF_UP);
					LogUtility.getLogger().info("[Pump M] before="+before.toString()) ;
					pumpTrData.setBaseprice(before.toString()); // 단가
					pumpTrData.setPreset_baseprice(before.toString()); // 정액정량단가 소수점3
				}else{
					pumpTrData.setPreset_baseprice(pumpTrData.getBaseprice()); // 정액정량단가 소수점3
				}
				
				// 수량
				BigDecimal Baseprice = new BigDecimal(baseprice);
				BigDecimal pump_qty = new BigDecimal(liter).divide(new BigDecimal(1000));
				pumpTrData.setPreset_qty(pump_qty.toString());
				
				// 정량가
				BigDecimal fixedQty = Baseprice.multiply(pump_qty);
				pumpTrData.setPreset_prc(fixedQty.toString());
				
				if(pumpTrData.getPreset_qty() == null){
					pumpTrData.setPreset_qty("0");
				}
			}
		} else if ((getPrice != null && getPrice == 0) && (decimalLiter != null && decimalLiter.compareTo(zero) ==0)) {
			if (pumpTrData != null) {
				pumpTrData.setOil_preset_ind("0");		// 프리셋정보 여부 0:없음, 1:있음
				pumpTrData.setPreset_qty("0");		
				pumpTrData.setPreset_prc("0");		
			}
		}

		if (pumpTrData == null)
			return null;

		return getUPOSMessage_OdtItemInfo_Item(pumpTrData, nozzleNo, mobilePayYn);
	
	}

	/**
	 * UPOSMessage 에 포함되어 있는 주유량을 계산한다. 그리고 이를 Pump A 의 Spec 에 충족하도록 변경한다.
	 * 
	 * @param uPosMsg :
	 *            UPOSMessage Class (응답 전문)
	 * @return 승인 결제된 Liter 량
	 */
	public static String getLiterForPumpA(UPOSMessage uPosMsg) {
		double literDouble = 0;

		UPOSMessage_ItemInfo item_info = uPosMsg.getItem_info();
		ArrayList<UPOSMessage_ItemInfo_Item> itemInfoList = item_info
				.getItemInfoList();

		for (int i = 0; i < itemInfoList.size(); i++) {
			UPOSMessage_ItemInfo_Item itemInfo = itemInfoList.get(i);
			literDouble = literDouble
					+ Double.parseDouble(itemInfo.getOilAmount());
		}
		return PumpMUtil.convertLiterFromUPOSToPumpA(Double
				.toString(literDouble));
	}

	/**
	 * 현장 마일리지 적립 여부 정보를 가져온다 (Default = 0) [2008.11.20] Default 값은 오춘열 부장님, 편윤국
	 * 차장님, 장윤기 대리의 협의하에 결정되었음
	 * 
	 * @return
	 */
	public static String getMileage_ind() {
		String mileage_ind = ICode.MILEAGE_IND_0 ;
		SqlSession session = null;
		try {
			session = SqlSessionFactoryManager.openSqlSession();
			T_KH_CODEMASTERData codeData = T_KH_CODEMASTERHandler.getHandler()
					.getT_KH_CODEMASTERDataByCode(session,
							IConstant.POSPORTOCOL_CODEMASTER_0134);
			String chg_dt = null;
			if (codeData != null) {
				mileage_ind = codeData.getValue();
				chg_dt = codeData.getChg_dt();
				T_KH_PRODUCTHandler.getHandler().setDownloadFlag(chg_dt);
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}
		return mileage_ind;
	}

	/**
	 * POS IP 를 POS_INFO Table 로 부터 가져 온다.
	 * 
	 * @return POS IP
	 */
	public static String getPOSIP() {
		String posIP = null;
		try {
			T_KH_POS_INFOData posInfoData = T_KH_POS_INFOHandler.getHandler()
					.getT_KH_POS_INFOData();
			if (posInfoData == null) {
				LogUtility.getPumpMLogger().error("[Pump M] Not Find POS IP");
			} else {
				posIP = posInfoData.getIpaddr_nbr_pos();
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		}

		return posIP;
	}

	// public static T_KH_PUMP_TRData getT_KH_PUMP_TRData(String nozzleNo ,
	// boolean completed) {
	// T_KH_PUMP_TRData pumpTrData = null ;
	// try {
	// if (PosAController.isPOSStarted()) {
	// pumpTrData =
	// T_KH_PUMP_TRHandler.getHandler().getPumpInformation(nozzleNo, completed,
	// "%") ;
	// } else {
	// pumpTrData =
	// T_KH_PUMP_TRHandler.getHandler().getPumpInformation(nozzleNo, completed,
	// "0") ;
	// }
	// } catch (Exception e) {
	// LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
	// }
	// return pumpTrData ;
	// }

	/**
	 * POS Port 를 POS_INFO Table 로 부터 가져 온다.
	 * 
	 * @return POS Port
	 */
	public static String getPOSPort() {
		String posPort = null;
		try {
			T_KH_POS_INFOData posInfoData = T_KH_POS_INFOHandler.getHandler()
					.getT_KH_POS_INFOData();
			if (posInfoData == null) {
				LogUtility.getPumpMLogger().error("[Pump M] Not Find POS Port");
			} else {
				posPort = posInfoData.getPortno_nbr_posconnect();
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		}

		return posPort;
	}

	/**
	 * POS 의 영업 일자를 요청한다. (YYYYMMDD)
	 * 
	 * @return POS 영업 일자 (YYYYMMDD)
	 */
	public static String getPosSaleDate() {
		String posSaleDate = null;
		try {
			posSaleDate = T_KH_STOREHandler.getHandler().getWorkingDate();
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		}
		return posSaleDate;
	}

	
	/**
	 * UPOSMessage 에 포함된 주유금액을 Pump A 의 Spec 에 맞게 변경하여 응답한다.
	 * 
	 * @param uPosMsg :
	 *            UPOSMessage Class (응답 전문)
	 * @return 승인 결제된 금액
	 */
	public static String getPriceForPumpA(UPOSMessage uPosMsg) {
		return PumpMUtil.convertPriceFromUPOSToPumpA(uPosMsg
				.getPayment_amt());
	}
	
	/** PI2, CWI, 2016-03-23  GSC Self에 맞도록 추가 */
	public static T_KH_PUMP_TRData getPumpingInfo(String nozzleNo) {
		
		T_KH_PUMP_TRData pumpTrData = null;
		
		try {
			String khProcNo = PumpMTransactionManager.getInstance().getKHTransactionIDWithoutCreation(nozzleNo);
			
			if (khProcNo != null) {
				pumpTrData = T_KH_PUMP_TRHandler.getHandler().getT_KH_PUMP_TRData(khProcNo);
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		}
		return pumpTrData;
	}

	public static T_KH_PUMP_TRData getPumpingInfo(String nozzleNo, boolean isPreset) {
		
		T_KH_PUMP_TRData pumpTrData = null;
		
		try {
			String khProcNo = PumpMTransactionManager.getInstance().getKHTransactionIDWithoutCreation(nozzleNo);
			
			if (khProcNo != null) {
				pumpTrData = T_KH_PUMP_TRHandler.getHandler().getT_KH_PUMP_TRData(khProcNo);
				
				if ((pumpTrData != null) && (ICode.OIL_PAID_IND_0.equals(pumpTrData.getOil_paid_ind()))   //  결제여부 0:없음 1:있음
						&& (ICode.LOCKING_IND_0.equals(pumpTrData.getLocking_ind())) //Locking 여부 0:해지 1:Locking
						&& (ICode.OIL_COMPLETED_IND_0.equals(pumpTrData.getOil_completed_ind()))) { //완료정보 여부 0:없음 , 1:있음
					if (isPreset) {
						if (!ICode.OIL_PRESET_IND_1.equals(pumpTrData.getOil_preset_ind())) { //프리셋정보 여부 0:없음 , 1:있음
							pumpTrData = null;
						}
					}
				} else {
					pumpTrData = null;
				}
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		}
		return pumpTrData;
	}

	/**
	 * TR 전문을 요청한다.
	 * 
	 * @param nozzleNo :
	 *            노즐 번호
	 * @param completed :
	 *            주유 중 / 완료 여부
	 * @return
	 */
	public static T_KH_PUMP_TRData getT_KH_PUMP_TRData(String nozzleNo, boolean completed) {
		T_KH_PUMP_TRData pumpTrData = null;
		try {
			if (!completed) {
				pumpTrData = getPumpingInfo(nozzleNo, true);
			} else {
				pumpTrData = T_KH_PUMP_TRHandler.getHandler().getPumpInformation(nozzleNo, completed, "%");
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		}
		return pumpTrData;
	}


	/**
	 * 상품다운로드 요청을 받아서 상품테이블로부터 상품정보를 전달한다.
	 * 
	 * @return UPOSMessage_CatalogInfo
	 */
	public static UPOSMessage_CatalogInfo getUPOSMessage_Catalog_info() {
		LogUtility.getPumpMLogger().debug(
				"[Pump M] Process ProductInfo for CAT Device");
		UPOSMessage_CatalogInfo uPosCataLogInfo = null;
		ArrayList<UPOSMessage_CatalogInfo_Nozzle_Item> catalog_info_nozInfoArray = null;
		ArrayList<UPOSMessage_CatalogInfo_Goods_Item> catalog_info_goodsArray = null;
		SqlSession session = null;

		try {
			session = SqlSessionFactoryManager.openSqlSession();
			// 유류/유외 상품 정보를 수집합니다
			T_KH_PRODUCTData[] productData = T_KH_PRODUCTHandler.getHandler()
					.getT_KH_PRODUCTDataByCat_goods_ind(session, "1");
			if (productData != null) {
				catalog_info_goodsArray = new ArrayList<UPOSMessage_CatalogInfo_Goods_Item>();
				for (int i = 0; i < productData.length; i++) {
					try {
						String goods_name = productData[i].getGoods_name();
						goods_name = GlobalUtility.getByteWithSpecificByteLength(
								goods_name, 14);

						UPOSMessage_CatalogInfo_Goods_Item goodsInfo = CreateUPOSMessage
								.createUPOSMessage_CatalogInfo_Goods_Item(
										productData[i].getGoods_code(),
										productData[i].getGoods_code_bonus(),
										productData[i].getGoods_code_taxfree(),
										productData[i].getOil_ind(),
										goods_name,
										GlobalUtility.getMultipleWith1000(productData[i].getShopfrontprc_amt()),
										GlobalUtility.getMultipleWith1000(productData[i].getPrc_amt_taxfree()));
						catalog_info_goodsArray.add(goodsInfo);
					} catch (Exception e) {
						LogUtility.getPumpMLogger().error(e.getMessage(), e);
					}
				}
			}

			// 노즐 정보를 수집합니다.
			T_NZ_NOZZLEData[] nzNozzleDataArray = T_NZ_NOZZLEHandler
					.getHandler().getT_NZ_NOZZLEDataByNozzleAndRecharge(session);
			if (nzNozzleDataArray != null) {
				catalog_info_nozInfoArray = new ArrayList<UPOSMessage_CatalogInfo_Nozzle_Item>();

				for (int i = 0; i < nzNozzleDataArray.length; i++) {
					try {
						String nozzleNo = nzNozzleDataArray[i].getNozzle_no();
						String goods_code = nzNozzleDataArray[i]
								.getGoods_code();
						String basePrice = PumpMUtil.getBasePrice(goods_code,
								nzNozzleDataArray[i].getSelf_ind_exist());
						String basePriceForUPOS = GlobalUtility.getMultipleWith1000(basePrice);
						UPOSMessage_CatalogInfo_Nozzle_Item nozInfo = CreateUPOSMessage
								.createUPOSMessage_CatalogInfo_Nozzle_Item(
										nozzleNo, goods_code, basePriceForUPOS);
						catalog_info_nozInfoArray.add(nozInfo);
					} catch (Exception e) {
						LogUtility.getPumpMLogger().error(e.getMessage(), e);
					}
				}
			}
			uPosCataLogInfo = CreateUPOSMessage.createUPOSMessage_CatalogInfo(
					catalog_info_goodsArray, catalog_info_nozInfoArray);

		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}
		return uPosCataLogInfo;
	}

	/**
	 * 2019.08.07
	 * by SoonKwan
	 * 상품다운로드 요청을 받아서 세차바코드정보를 전달한다.
	 * @param catNo CATNO만 틀리고 나머지 마스타를 동일하다.
	 * @return UPOSMessage_BarcodeInfo
	 */
	public static UPOSMessage_BarcodeInfo getUPOSMessage_Barcode_info(String catNo)
	{
		LogUtility.getCATMLogger().debug("Process BarcodeInfo for CAT Device["+catNo+"]");
	
		UPOSMessage_BarcodeInfo uPosBarcodeInfo = null;
		ArrayList<UPOSMessage_BarcodeInfo_Item> barcode_info_Array = null;
		UPOSMessage_BarcodeInfo_Set barcode_info_set = null;
		SqlSession session = null;
		
		try{
			session = SqlSessionFactoryManager.openSqlSession();
			uPosBarcodeInfo =  new UPOSMessage_BarcodeInfo();
			barcode_info_set = new UPOSMessage_BarcodeInfo_Set();
			
			//세차바코드 정보를 수집한다.
			String disCountRate = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_7110);
			if (GlobalUtility.isNullOrEmptyString(disCountRate)) {
				disCountRate = "0";
				LogUtility.getCATMLogger().debug("공공조달할인율 미지정 " + disCountRate+ " % 지정");
			}
		
			String barcode_Yn = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0440);
			if (GlobalUtility.isNullOrEmptyString(barcode_Yn)) {
				barcode_Yn = "n";
				LogUtility.getCATMLogger().debug("세차바코드 발행여부 미지정" + barcode_Yn+ "  지정");
			}
			
			String barcode_type = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0441);
			if (GlobalUtility.isNullOrEmptyString(barcode_type)) {
				barcode_type = "0";
				LogUtility.getCATMLogger().debug("세차바코드 Type여부 미지정" + barcode_type+ "  지정");
			}
			
			String hdCarpay_Yn = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_7113);	//복합결제사용구분 (결제모듈개선 프로젝트)
			if (GlobalUtility.isNullOrEmptyString(hdCarpay_Yn)) {
				hdCarpay_Yn = "0";
				LogUtility.getCATMLogger().debug("복합결제사용구분 :" + hdCarpay_Yn+ "  지정");
			}
			
			String cashReceiptYn = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_7115); //현금영수증 의무발행 사용여부(0:사용안함, 1:사용)
			if (GlobalUtility.isNullOrEmptyString(cashReceiptYn)) {
				cashReceiptYn = "0";
				LogUtility.getCATMLogger().debug("현금영수증 의무발행 사용여부 미지정 :" + cashReceiptYn+ "  지정");
			}

			String mobileWashYn = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_7117); //바로세차 사용여부(0:사용안함, 1:사용)
			if (GlobalUtility.isNullOrEmptyString(mobileWashYn)) {
				mobileWashYn = "0";
				LogUtility.getCATMLogger().debug("바로세차 사용여부 미지정 :" + mobileWashYn+ "  지정");
			}
			
			// 2020.03.04 공통코드 "0440" 두자리일경우 앞 한자리는 버린다. - YHM
			if(barcode_Yn.length() == 2) {
				barcode_Yn = barcode_Yn.substring(1);
			}

			// 2019.11.20 공통코드 "0441" 두자리일경우 앞 한자리는 버린다. - SoonKwan
			if(barcode_type.length() == 2) {
				barcode_type = barcode_type.substring(1);
			}
			
			uPosBarcodeInfo.setCatNo(catNo);
			barcode_info_set.setDisCountRate(disCountRate); //공공조달할인율
			barcode_info_set.setBarcodeYn(barcode_Yn);      //바코드 발행여부 (0:미발행 1:발행)
			barcode_info_set.setBarcodeType(barcode_type);  // 바코드타입(1:주유바코드 2:세차바코드)
			barcode_info_set.setHdCarpayYn(hdCarpay_Yn);    // 복합결제사용구분(0:사용안함, 1:전체사용,2:바로주유사용,3:현대CarPay사용)
			barcode_info_set.setMobileWashYn(mobileWashYn); // 바로세차 사용여부(0:사용 안함, 1:사용)
			barcode_info_set.setCashReceiptYn(cashReceiptYn); // 현금영수증 의무발행 사용여부(0:사용 안함, 1:사용)
			
			String expdate = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0326); // 유효기간이 며칠 인지 (이후 30일 인지)
			if (GlobalUtility.isNullOrEmptyString(expdate)) {
				expdate = "90" ; // 값이 없을 경우 지정
				LogUtility.getCATMLogger().debug("Barcode 유효기간이 CodeMaster 에 없어서 " + expdate+ " 일로 지정");
			}
			//
			LogUtility.getCATMLogger().debug("expdate =" + expdate);
			barcode_info_set.setCarwashDate(expdate); //세차유효기간
			
			uPosBarcodeInfo.setBarcodeInfoset(barcode_info_set);
			
			// 할인단가를 수집한다.
			try{
				T_CL_DISCOUNTData[] disCountDataArray = T_CL_DISCOUNTHandler
						.getHandler().getALLT_CL_DISCOUNTData(session);
				if(disCountDataArray != null){
					LogUtility.getCATMLogger().debug("getALLT_CL_DISCOUNTData =" + uPosBarcodeInfo.getContents());

					barcode_info_Array = new ArrayList<UPOSMessage_BarcodeInfo_Item>();
					for(int i=0; i<disCountDataArray.length;i++) {
						try{
							String startDate = disCountDataArray[i].getApply_dt_fr();
							String endDate = disCountDataArray[i].getApply_dt_to();
							int startAmount = disCountDataArray[i].getPump_amt_fr();
							int endAmount = disCountDataArray[i].getPump_amt_to();
							int disCountAmount = disCountDataArray[i].getDiscn_amt();
							
							UPOSMessage_BarcodeInfo_Item barInfo = CreateUPOSMessage
									.createUPOSMessage_BarcodeInfo_Item(
									startDate, 
									endDate, 
									startAmount, 
									endAmount, 
									disCountAmount);				
							barcode_info_Array.add(barInfo);
						} catch (Exception e) {
							LogUtility.getCATMLogger().error(e.getMessage(), e);
						}
					}
					if(barcode_info_Array.size() == 0) {
						uPosBarcodeInfo.setDisCountRecord("00");
					}
					else {
						uPosBarcodeInfo.setDisCountRecord(GlobalUtility.appending0Pre(Integer.toString(barcode_info_Array.size()),2)) ; 
					}
					uPosBarcodeInfo.setBarcodeInfoList(barcode_info_Array);
				}
				else 
				{
					uPosBarcodeInfo.setDisCountRecord("00");				
				}
			}catch(Exception e) {
				uPosBarcodeInfo.setDisCountRecord("00");
				LogUtility.getCATMLogger().error("세차바코드 단가 없음:",e.getMessage(), e);
			}
		}catch(Exception e) {
			LogUtility.getCATMLogger().error("공공조달 할인율 : "+e.getMessage(), e);
		}finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}
		
		LogUtility.getCATMLogger().debug("uPosBarcodeInfo =" + uPosBarcodeInfo.getContents());		
		return uPosBarcodeInfo;
	}
	/**
	 * ODT 로부터 승인 요청시 아래 정보를 가지고, 노즐정보(UPOSMessage_ItemInfo) 를 만든다. 소모셀프 및 충전기의
	 * 경우 할인된 단가로써 승인 요청이 올수 있다. 이로 인해 ODT 로 부터 받은 수량 및 단가 , 금액으로 Item Info 를
	 * 구성한다. 그리고 할인전 단가 및 할인전 금액은 그 노즐의 점두가를 통해서 계산한다.
	 * 
	 * @param nozzleNo :
	 *            노즐번호
	 * @param reqLiter :
	 *            수량
	 * @param reqBasePrice :
	 *            단가
	 * @param reqPrice :
	 *            금액
	 * @return
	 */
	public static UPOSMessage_ItemInfo getUPOSMessage_ItemInfo(String nozzleNo,
			String khTransactionID, String reqLiter, String reqBasePrice,
			String reqPrice) {
		UPOSMessage_ItemInfo itemInfo_Item = null;
		UPOSMessage_ItemInfo_Item uPosMsgItemInfo = null;

		uPosMsgItemInfo = getUPOSMessage_ItemInfo_Item(nozzleNo, khTransactionID, reqLiter, reqBasePrice, reqPrice);
		itemInfo_Item = UPOSMessage_ItemInfo.createUPOSMessage_ItemInfo(uPosMsgItemInfo);

		return itemInfo_Item;
	}

	
	/**
	 * 주유 정보 요청에 대한 응답
	 * 
	 * @param nozzleNo
	 *            노즐 ID
	 * @param completed
	 *            true : 주유완료하였지만 결제하지않는 주유건에 대한 주유 정보 요청 
	 *            false : 결제하지 않은 최신 주유건에 대한 주유정보 요청
	 * 
	 * [2008.11.12] 추가 요건 사항 by 오춘열 부장님 주유중 정보 요청시 주유중인 건에 대해서만 정보를 전송한다. 주유중이지
	 * 않은 경우 주유건이 없다고 보내도록 한다.
	 * 
	 * @param price :
	 *            CAT 으로 부터 입력 받은 금액
	 * @param liter :
	 *            CAT 으로 부터 입력 받은 리터
	 * @return
	 */
	public static UPOSMessage_ItemInfo_Item getUPOSMessage_ItemInfo_Item(String nozzleNo, boolean completed, String price, String liter) {
		boolean isCatPreset = false;

		T_KH_PUMP_TRData pumpTrData = null;
		
		// 주유 완료가 되지 않았고, CAT 단말기로 부터 결재 금액을 받은 경우 - 가정은 주유기가 Preset 정보를 KIXXHUB 로 보내지 못할 경우
		if (!completed && !price.equals("") && !price.equals("0")) {
			/**
			 * CAT 단말기로 부터 결제 금액과 리터를 입력 받은 경우. 현 주유건이 Preset 에 의한 주유건이라 하더라도
			 * CAT 단말기로 부터 금액 입력을 받았을 경우 받은 입력값으로 주유정보를 구성한다. 즉 CAT 단말기 입력 값이
			 * Preset 자료보다 우선시 한다.
			 * 
			 */
			LogUtility.getPumpMLogger().info(
					"[Pump M] Receive payment and liter from CAT Device. nozzleNo="
							+ nozzleNo + "#price=" + price + "#liter=" + liter);
			try {
				// 이미 주유 시작이 된 건이기 때문에 새로운 KH 처리번호가 존재하고, 이 KH 처리번호를 그대로 사용
				String khProcNo = PumpMTransactionManager.getInstance().getKHTransactionIDWithoutCreation(nozzleNo);
				
				if (khProcNo != null) {
					pumpTrData = getPumpingInfo(nozzleNo, false);
				}
			} catch (Exception e) {
				LogUtility.getPumpMLogger().error(e.getMessage(), e);
			}

			// try {
			// pumpTrData =
			// T_KH_PUMP_TRHandler.getHandler().getPumpingInfoByNozzle_noDontCarePreset(nozzleNo)
			// ;
			// } catch (Exception e) {
			// LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
			// }

			if (pumpTrData != null) {
				pumpTrData.setOil_preset_ind(ICode.OIL_PRESET_IND_1);   //프리셋정보 여부 0:없음 , 1:있음
				pumpTrData.setPreset_qty_prc_ind(ICode.PRESET_QTY_PRC_IND_0_PRICE); //정액정량여부	 0:정액 , 1=정량
				pumpTrData.setPreset_prc(price); //정액
				pumpTrData.setPreset_baseprice(pumpTrData.getBaseprice()); //정액정량단가 소수점3
				isCatPreset = true;
			}
		} else {
			pumpTrData = getT_KH_PUMP_TRData(nozzleNo, completed);
		}

		/**
		 * [2008.11.12] 추가 요건 사항 by 오춘열 부장님 주유중 정보 요청시 주유중인 건에 대해서만 정보를 전송한다.
		 * 주유중이지 않은 경우 주유건이 없다고 보내도록 한다.
		 */
		if ((!completed) && (pumpTrData != null)) {
			boolean isPumping = PumpMTransactionManager.getInstance().hasState(nozzleNo, IPumpConstant.KH_PUMPING);
			
			if (!isPumping) {
				LogUtility.getPumpMLogger().warn("[Pump M] isPumping is false, so pumping Info is null");
				pumpTrData = null;
			}
		}
		// CAT단말기에서 주유완료건 결제요청시, 모바일복합결제 사전예약건일 경우 거절처리함 (점두가, 할인단가로 인한 이슈 발생)
		if(completed && pumpTrData != null){
			T_KH_SALES_INFOData[] rtnValue = null;
			SqlSession session = null;
			try {
				session = SqlSessionFactoryManager.openSqlSession();
				rtnValue = T_KH_SALES_INFOHandler.getHandler().getT_KH_SALES_INFOData( 	session, 
						pumpTrData.getKhproc_no(), 
						null, 
						ICode.POS_SENDING_YES, 
						IConstant.PROC_TYPE_MF);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				SqlSessionFactoryManager.closeSqlSession(session);
			}
			
			if(rtnValue!= null ){
				LogUtility.getPumpMLogger().warn("바로주유 사전예약건 확인 - 주유정보요청 거절처리함 ");
				return null;
			}
		}
		
		if (pumpTrData == null)
			return null;

		return getUPOSMessage_ItemInfo_Item(pumpTrData, isCatPreset);
	}	
	
	/**
	 * PI2, 양일준 , 2015-01-15
	 * 충전기 ODT - 캠페인 주유 정보 요청에 대한 응답
	 * 
	 * @param nozzleNo
	 *            노즐 ID
	 * @param price :
	 *            ODT 으로 부터 입력 받은 금액
	 * @param liter :
	 *            ODT 으로 부터 입력 받은 리터
	 * @return
	 */
	public static UPOSMessage_ItemInfo_Item getUPOSMessage_ItemInfo_Item(String nozzleNo, String price, String liter) {
		boolean isOdtPreset = false;

		T_KH_PUMP_TRData pumpTrData = null;
		
		if (!price.equals("") && !price.equals("0")) {
			/**
			 * ODT로 부터 금액 입력을 받았을 경우 받은 입력값으로 주유정보를 구성한다. 
			 */
			LogUtility.getLogger().info(
					"[Pump M] Receive payment and liter from ODT Device. nozzleNo="
							+ nozzleNo + ": price=" + price + ": liter=" + liter);
			try {
				String khProcNo = PumpMTransactionManager.getInstance().getKHTransactionIDWithoutCreation(nozzleNo);
				
				if (khProcNo != null) {
					pumpTrData = getPumpingInfo(nozzleNo);  // 해당 충전건 관련된  주유정보 가져옴
				}
			} catch (Exception e) {
				LogUtility.getLogger().error(e.getMessage(), e);
			}

			if (pumpTrData != null) {
				pumpTrData.setOil_preset_ind("0");		   // 프리셋정보 여부 (0:없음)
				pumpTrData.setEqpm_amt_prc(price);         // 현주유금액
				pumpTrData.setEqpm_qty(liter);             // 현주유량
				pumpTrData.setOil_completed_ind("1");      // 주유완료정보여부 (1: 완료)
			}
		}
		return getUPOSMessage_ItemInfo_Item(pumpTrData, isOdtPreset);
	}
	
	
	/**
	 * ODT 로부터 승인 요청시 아래 정보를 가지고, 노즐정보(UPOSMessage_ItemInfo_Item) 를 만든다. 소모셀프 및
	 * 충전기의 경우 할인된 단가로써 승인 요청이 올수 있다. 이로 인해 ODT 로 부터 받은 수량 및 단가 , 금액으로 Item
	 * InfoItem 을 구성한다. 그리고 할인전 단가 및 할인전 금액은 그 노즐의 점두가를 통해서 계산한다.
	 * 
	 * @param nozzleNo :
	 *            노즐번호
	 * @param reqLiter :
	 *            수량
	 * @param reqBasePrice :
	 *            단가
	 * @param reqPrice :
	 *            금액
	 * @return
	 */
	public static UPOSMessage_ItemInfo_Item getUPOSMessage_ItemInfo_Item(
			String nozzleNo, String khTransactionID, String reqLiter,
			String reqBasePrice, String reqPrice) {
		UPOSMessage_ItemInfo_Item uPosMsgItemInfo = null;
		SqlSession session = null;

		String goodsCode = ""; // 상품 코드
		String bonGoodsCode = "";
		String oil_ind = ICode.OIL_IND_11; // 상품 구분
		String unitPrice_before_discount = "0"; // 할인전 단가
		String oilAmount = "0"; // 수량
		String unitPrice_after_discount = "0"; // 할인후 단가
		String tax_ind = ICode.TAXFREE_CD_01; // 01:과세 , 02:면세
		String price_before_tax = "0"; // 할인후 단가
		String taxPrice = "0"; // 세금
		String oilPrice_before_discount = "0"; // 할인전 금액
		String oilPrice_after_discount = "0"; // 할인후 금액
		String rentlimit_proc_ind_overlimit = ICode.PROC_IND_OVERLIMIT_99; // 외상결제타입
		String unitDiscount_ind = "0"; // 할인 여부 -> 할인 여부는 Item Info 를 구성하는
		// CreateUPOSMessage.createUPOSMessage_ItemInfo_Item 함수 내에서 설정된다.
		String keep_no = ""; // 보관증번호(10b)
		String issue_type = "01"; // 보관증 발행유형(2b)

		try {
			session = SqlSessionFactoryManager.openSqlSession();

			T_NZ_NOZZLEData tNzNozzleData = T_NZ_NOZZLEHandler.getHandler().getT_NZ_NOZZLEDataByNozzleNo(session, nozzleNo)[0];
			goodsCode = tNzNozzleData.getGoods_code();
			T_KH_PRODUCTData productData = T_KH_PRODUCTHandler.getHandler().getT_KH_PRODUCTData(session, goodsCode)[0];
			oil_ind = productData.getOil_ind();
			bonGoodsCode = productData.getGoods_code_bonus();

			double basePriceDou 	= Double.parseDouble(PumpMUtil.getBasePrice( productData, tNzNozzleData));
			double reqBasePriceDou 	= Double.parseDouble(reqBasePrice);
			double reqLiterDou 		= Double.parseDouble(reqLiter);
			
			if (reqBasePriceDou != basePriceDou) {
				unitPrice_before_discount = GlobalUtility.getMultipleWith1000(basePriceDou);
				oilAmount = GlobalUtility.getMultipleWith1000(reqLiterDou);
				unitPrice_after_discount = GlobalUtility.getMultipleWith1000(reqBasePriceDou);
				oilPrice_before_discount = GlobalUtility.getStringValue(GlobalUtility.multiple(basePriceDou, reqLiterDou));
				oilPrice_after_discount = GlobalUtility.getStringValue(reqPrice);

				taxPrice = GlobalUtility.getTaxPrice(oilPrice_after_discount);
				price_before_tax = PumpMUtil.getPrice_before_tax(oilPrice_after_discount, taxPrice);
			} else {
				oilAmount = GlobalUtility.getMultipleWith1000(reqLiterDou);
				unitPrice_after_discount = GlobalUtility.getMultipleWith1000(reqBasePriceDou);
				oilPrice_after_discount = GlobalUtility.getStringValue(reqPrice);

				taxPrice = GlobalUtility.getTaxPrice(oilPrice_after_discount);
				price_before_tax = PumpMUtil.getPrice_before_tax(oilPrice_after_discount, taxPrice);

				oilPrice_before_discount = oilPrice_after_discount;
				unitPrice_before_discount = unitPrice_after_discount;
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}

		uPosMsgItemInfo = CreateUPOSMessage.createUPOSMessage_ItemInfo_Item(
				nozzleNo, 
				goodsCode, 
				bonGoodsCode, 
				oil_ind,
				unitPrice_before_discount, 
				oilAmount,
				unitPrice_after_discount,
				tax_ind, 
				price_before_tax, taxPrice, 
				oilPrice_before_discount,
				oilPrice_after_discount,
				khTransactionID,
				rentlimit_proc_ind_overlimit, 
				unitDiscount_ind, keep_no,
				issue_type);
		return uPosMsgItemInfo;
	}

	/**
	 * TR 전문을 이용하여 UPOSMessage 의 Item Info 내용을 구성한다. 만약 catPreset = true 인 경우는
	 * TR 전문은 현 주유의 주유금액과 주유량으로 계산된 것이 아닌 CAT 으로부터의 입력 금액으로 부터 구성되어진 값이다.
	 * 
	 * @param pumpTrData :
	 *            TR 전문
	 * @param catPreset :
	 *            CAT 으로 부터의 금액 입력 여부
	 * @return
	 */
	public static UPOSMessage_ItemInfo_Item getUPOSMessage_ItemInfo_Item(
			T_KH_PUMP_TRData pumpTrData, boolean catPreset) {
		UPOSMessage_ItemInfo_Item uPosMsgItemInfo = null;
		SqlSession session = null;

		String goodsCode = null; // 상품 코드
		String bonGoodsCode = null; // 보너스 상품 코드
		String oil_ind = null; // 상품 구분
		String unitPrice_before_discount = null; // 할인전 단가
		String oilAmount = null; // 수량
		String unitPrice_after_discount = null; // 할인후 단가
		String tax_ind = ICode.TAXFREE_CD_01; // 01:과세 , 02:면세
		String price_before_tax = null; // 공급가액
		String taxPrice = null; // 세금
		String oilPrice_before_discount = null; // 할인전 금액
		String oilPrice_after_discount = null; // 할인후 금액
		String khTransactionID = null; // 전표번호
		String rentlimit_proc_ind_overlimit = ICode.PROC_IND_OVERLIMIT_99; // 외상결제타입
		String unitDiscount_ind = ICode.DY_UNITDISCOUNT_IND_0 ; // 할인 여부
		String keep_no = ""; // 보관증번호(10b)
		String issue_type = ""; // 보관증 발행유형(2b)

		try {
			session = SqlSessionFactoryManager.openSqlSession();

			double basePriceInt = 0;
			double preset_basePriceInt = 0;
			double literInt = 0;
			double priceInt = 0;

			if (pumpTrData == null) {
				LogUtility.getPumpMLogger().error("[Pump M] No Pump Info.");
				return null;

			} else {
				khTransactionID = pumpTrData.getKhproc_no();
				goodsCode = pumpTrData.getGoods_code();

				T_KH_PRODUCTData productData = T_KH_PRODUCTHandler.getHandler().getT_KH_PRODUCTData(session, goodsCode)[0];
				oil_ind = productData.getOil_ind();
				bonGoodsCode = productData.getGoods_code_bonus();
				basePriceInt = Double.parseDouble(pumpTrData.getBaseprice());
				
				if (pumpTrData.getOil_preset_ind().equals(ICode.OIL_PRESET_IND_0)) {
					// 그냥 주유일 경우
					preset_basePriceInt = basePriceInt;
				} else {
					// 정액/정량 일 경우
					preset_basePriceInt = Double.parseDouble(pumpTrData.getPreset_baseprice());
				}

				if ((pumpTrData.getOil_completed_ind().equals(ICode.OIL_COMPLETED_IND_1)) && !catPreset) {
					LogUtility.getPumpMLogger().debug("[Pump M] 주유완료된 것이기 때문에 완료 데이터를 통해서 주유 정보를 얻습니다.");
					
					priceInt = Double.parseDouble(pumpTrData.getEqpm_amt_prc());
					literInt = Double.parseDouble(pumpTrData.getEqpm_qty());
				} else {
					if (pumpTrData.getPreset_qty_prc_ind().equals(ICode.PRESET_QTY_PRC_IND_0_PRICE)) {
						LogUtility.getPumpMLogger().debug("[Pump M] 주유완료되지 않고 정액 주유이기 때문에  정액 주유에 대한 정보를 얻습니다.");
						
						priceInt = Double.parseDouble(pumpTrData.getPreset_prc());
						literInt = GlobalUtility.getValueByCertainDecimal(PumpMUtil.getPresetLiter(priceInt, preset_basePriceInt), 3);
					} else if (pumpTrData.getPreset_qty_prc_ind().equals(ICode.PRESET_QTY_PRC_IND_1_LITER)) {
						LogUtility.getPumpMLogger().debug("[Pump M] 주유완료되지 않고 정량 주유이기 때문에  정량 주유에 대한 정보를 얻습니다.");
						
						literInt = Double.parseDouble(pumpTrData.getPreset_qty());
						priceInt = GlobalUtility.multiple(literInt, preset_basePriceInt);
					}
				}

				unitPrice_before_discount = GlobalUtility.getMultipleWith1000(basePriceInt);
				oilAmount = GlobalUtility.getMultipleWith1000(literInt);

				// T_KH_PUMP_TR 테이블에 단가가 0으로 들어가는 경우가 발생하여 0으로 들어가 있는 경우 일반 단가를
				// 사용
				if (preset_basePriceInt == 0)
					unitPrice_after_discount = GlobalUtility.getMultipleWith1000(basePriceInt);
				else
					unitPrice_after_discount = GlobalUtility.getMultipleWith1000(preset_basePriceInt);

				oilPrice_before_discount = GlobalUtility.getStringValue(priceInt);
				oilPrice_after_discount = oilPrice_before_discount;

				taxPrice = GlobalUtility.getTaxPrice(oilPrice_after_discount);
				price_before_tax = PumpMUtil.getPrice_before_tax(oilPrice_after_discount, taxPrice);

				if (basePriceInt != preset_basePriceInt) {
					unitDiscount_ind = ICode.DY_UNITDISCOUNT_IND_1 ;
				}
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}

		uPosMsgItemInfo = CreateUPOSMessage.createUPOSMessage_ItemInfo_Item(
				pumpTrData.getNozzle_no(), 
				goodsCode, 
				bonGoodsCode, 
				oil_ind,
				unitPrice_before_discount, 
				oilAmount, 
				unitPrice_after_discount,
				tax_ind,
				price_before_tax, 
				taxPrice, 
				oilPrice_before_discount,
				oilPrice_after_discount,
				khTransactionID,
				rentlimit_proc_ind_overlimit,
				unitDiscount_ind, keep_no,
				issue_type);

		return uPosMsgItemInfo;
	}

	/**
	 * 주유 정보 요청에 의한 응답 전문을 구성한다.
	 * 
	 * @param uPosMsg :
	 *            주유정보 요청 전문
	 * @param messageType :
	 *            요청 전문의 Message Type
	 * @return
	 */
	public static UPOSMessage getUPOSMessage_ItemInfo_Item(UPOSMessage uPosMsg, int messageType) {
		UPOSMessage_ItemInfo_Item itemInfoItem = null;
		UPOSMessage sendUPOSMsg = null;
		boolean completed = false;
		String khTransactionID = null;
		String basePrice = null;
		
		if (messageType == IUPOSConstant.MESSAGETYPE_INT_4201)
			completed = false;
		else
			completed = true;
		
		// PI2, 2015-12-30, cwi, ODT에서 올라온 요청의 경우 주유완료 후에도 ODT의 값을 기준으로 처리 하기에
		// odt의 요청 전문은 무조건 false로 처리 한다.
		// odt에서 올라온 4201일경우 Kixx Hub 처리를 위해 getUPOSMessage_ItemInfo_ItemFromODT 메소드 사용
		// PI2, 2016-07-15, cwi, SelfOdt의 경우 주유전 주유후 캠페인정보요청 전문은 4201 전문으로 통일 한다.
		if(uPosMsg.getMessageType().equals("4201") && uPosMsg.getDeviceType().equals("3S")){
			String mobilePayYn = "";
			
			// 거래처 요청일 경우 이전 거래처 조회 정보를 가지고 ItemInfo_Item 구성한다.
			if(!uPosMsg.getCustCard_No().equals("")){
			
				// 저장해 놓은 dwPumpM 데이터를 가져 온다.
				POSHeader posObj = PumpMODTSaleManager.getCustPOSPumpM(uPosMsg.getNozzle_no());
				POS_DW dwPumpM = null;
				if ((posObj != null) && (posObj instanceof POS_DW)) dwPumpM = (POS_DW) posObj;
				
				basePrice = GlobalUtility.getDividedWith1000(dwPumpM.getBasePrice());
			}
			
			boolean pump_completed = PumpMTransactionManager.getInstance().isSameState(uPosMsg.getNozzle_no(), IPumpConstant.KH_PUMP_COMPLETED);
			
			// 주유 완료 후 KH 번호 가져오기
			if(pump_completed){
				khTransactionID = PumpMTransactionManager.getInstance().getKHTransactionID(uPosMsg.getNozzle_no(),IPumpConstant.KH_PUMP_COMPLETED);
				LogUtility.getLogger().info("[Pump M] upos4201 Pump_Completed khTransactionID=" + khTransactionID);
			}else{
				// 주유 전 KH 번호 가져오기
				khTransactionID = PumpMTransactionManager.getInstance().getKHTransactionID(uPosMsg.getNozzle_no(), IPumpConstant.KH_ODT_PAID_REQ);
				LogUtility.getLogger().info("[Pump M] upos4201 Pump_Not_Completed khTransactionID=" + khTransactionID);
			}
			
			mobilePayYn = uPosMsg.getMobilePay_yn();
			itemInfoItem = getGSCUPOSMessage_ItemInfo_Item(uPosMsg.getNozzle_no(),
					khTransactionID,
					uPosMsg.getPump_qty(),    // 주유수량pump_qty
					basePrice, 				  // 거래처 단가
					uPosMsg.getPump_amt(),
					mobilePayYn
					);   // 주유금액pump_amt
			
			sendUPOSMsg = UPOSUtil.process4201_4291(uPosMsg, itemInfoItem, false);
		
		/**
		 * 프로젝트 : PI2
		 * 변경내용 : 신규(캠페인 주유정보 요청- 4291)
		 * 변경일자 : 2015.12.16
		 * 변경자 : 양일준  
		 * 충전기 ODT의 경우, 충전완료 후 확정된 금액, 수량으로 4291전문을 구성
		 */
		} else if((uPosMsg.getMessageType().equals(IUPOSConstant.MESSAGETYPE_4291)) && (uPosMsg.getDeviceType().equals(IUPOSConstant.DEVICE_TYPE_3O))){
			if(!uPosMsg.getCustCard_No().equals("")){
				
				String khProcNo = PumpMTransactionManager.getInstance().getKHTransactionIDWithoutCreation(uPosMsg.getNozzle_no());  // 해당 충전건과 관련된 KixxHub 처리번호를 가져옴 
				LogUtility.getLogger().info("[Pump M] khProcNo="+khProcNo) ;
				
				POSHeader posMsg = PumpMODTSaleManager.getCustPOSPumpM(uPosMsg.getNozzle_no());  // 해당 충전건과 관련된 거래처조회 정보를 가져옴
				POS_DW dwPumpM = null;

				if ((posMsg != null) && (posMsg instanceof POS_DW)) {
					dwPumpM = (POS_DW) posMsg;
					
					basePrice = dwPumpM.getBasePrice();
					
				}
				
				if(basePrice == null || basePrice.equals("")) basePrice = PumpMUtil.getBasePriceFromNozzleNo(uPosMsg.getNozzle_no());  //  노줄의 단가를 구해옴
				
				itemInfoItem = getUPOSMessage_ItemInfo_Item( uPosMsg.getNozzle_no(),           // 노즐번호 
						khProcNo,                                                              // POS 전표번호(KixxHub처리번호) 
						PumpMUtil.convertTotalLiterFromPumpTOPOS( uPosMsg.getPump_qty()),   // 수량
						basePrice,      		                                               // 단가
						uPosMsg.getPump_amt());                                                // 금액 
			} else {
				itemInfoItem = UPOSUtil.getUPOSMessage_ItemInfo_Item( uPosMsg.getNozzle_no(),  // 노즐번호
						uPosMsg.getPump_amt(),                                                 // 금액
						PumpMUtil.convertTotalLiterFromPumpTOPOS( uPosMsg.getPump_qty()));  // 수량 
			}

			sendUPOSMsg = UPOSUtil.process4201_4291(uPosMsg, itemInfoItem, true);
		} else {
			itemInfoItem = UPOSUtil.getUPOSMessage_ItemInfo_Item( uPosMsg.getNozzle_no(), 
					completed, 
					uPosMsg.getPump_amt(), 
					uPosMsg.getPump_qty());

			sendUPOSMsg = UPOSUtil.process4201_4291(uPosMsg, itemInfoItem, completed);
		}
		
		return sendUPOSMsg;
	}
	

	/**
	 * 신규, 양일준 , 2016.03.29
	 * 충전기 ODT - 캠페인 주유 정보 요청 중 상품정보 생성
	 * 
	 * @param nozzleNo
	 *            노즐 ID
	 * @param price :
	 *            ODT 으로 부터 입력 받은 금액
	 * @param liter :
	 *            ODT 으로 부터 입력 받은 리터
	 * @return
	 */
	private static UPOSMessage_ItemInfo_Item getUPOSMessage_ItemInfo_Item_Gas(String nozzleNo, String price, String liter) {
		boolean isOdtPreset = false;

		T_KH_PUMP_TRData pumpTrData = null;
		
		if (!price.equals("") && !price.equals("0")) {
			/**
			 * ODT로 부터 금액 입력을 받았을 경우 받은 입력값으로 주유정보를 구성한다. 
			 */
			LogUtility.getLogger().info(
					"[Pump M] Receive payment and liter from ODT Device. nozzleNo="
							+ nozzleNo + ": price=" + price + ": liter=" + liter);
			try {
				String khProcNo = PumpMTransactionManager.getInstance().getKHTransactionIDWithoutCreation(nozzleNo);
				
				if (khProcNo != null) {
					pumpTrData = getPumpingInfo(nozzleNo);
				}
			} catch (Exception e) {
				LogUtility.getLogger().error(e.getMessage(), e);
			}

			if (pumpTrData != null) {
				pumpTrData.setOil_preset_ind("0");		                   // 프리셋정보 여부 0:없음, 1:있음
				pumpTrData.setEqpm_amt_prc(price);
				pumpTrData.setEqpm_qty(liter);
				pumpTrData.setOil_completed_ind("1");
			}
		}
		return getUPOSMessage_ItemInfo_Item(pumpTrData, isOdtPreset);
	}

	/** 넘어온 데이터를 기반으로 ItemInfo 생성 - 2016.03.10, cwi */
	public static UPOSMessage_ItemInfo_Item getUPOSMessage_OdtItemInfo_Item(
			T_KH_PUMP_TRData pumpTrData, String nozzleNo, String mobilePayYn) {
		UPOSMessage_ItemInfo_Item uPosMsgItemInfo = null;
		SqlSession session = null;

		String goodsCode = null; // 상품 코드
		String bonGoodsCode = null; // 보너스 상품 코드
		String oil_ind = null; // 상품 구분
		String unitPrice_before_discount = null; // 할인전 단가
		String oilAmount = null; // 수량
		String unitPrice_after_discount = null; // 할인후 단가
		String tax_ind = "01"; // 01:과세 , 02:면세
		String price_before_tax = null; // 공급가액
		String taxPrice = null; // 세금
		String oilPrice_before_discount = null; // 할인전 금액
		String oilPrice_after_discount = null; // 할인후 금액
		String khTransactionID = null; // 전표번호
		String rentlimit_proc_ind_overlimit = "99"; // 외상결제타입
		String unitDiscount_ind = "0"; // 할인 여부
		String keep_no = ""; // 보관증번호(10b)
		String issue_type = ""; // 보관증 발행유형(2b)

		try {
			session = SqlSessionFactoryManager.openSqlSession();

			double basePriceInt = 0;
			double preset_basePriceInt = 0;
			double literInt = 0;
			double priceInt = 0;
			double beforePriceInt = 0;
			double nozzlePriceInt = 0;
			
			// 노줄의 단가를 구해옴
			String basePrice = PumpMUtil.getBasePriceFromNozzleNo(nozzleNo);
			
			if (pumpTrData == null) {
				LogUtility.getLogger().error("[Pump M] No Pump Info.");
				return null;

			} else {
				khTransactionID = pumpTrData.getKhproc_no();
				goodsCode = pumpTrData.getGoods_code();

				T_KH_PRODUCTData productData = T_KH_PRODUCTHandler.getHandler().getT_KH_PRODUCTData(session, goodsCode)[0] ;
				oil_ind = productData.getOil_ind();
				bonGoodsCode = productData.getGoods_code_bonus();
				basePriceInt = Double.parseDouble(pumpTrData.getBaseprice()); // 할인 후 단가
				nozzlePriceInt = Double.parseDouble(basePrice); // 할인전 단가
				
				LogUtility.getLogger().info("[결제모듈 프로젝트] getUPOSMessage_OdtItemInfo_Item : "+pumpTrData.getOil_preset_ind());
				
				if (pumpTrData.getOil_preset_ind().equals("0")) {
					// 그냥 주유일 경우
					preset_basePriceInt = basePriceInt;
					
					literInt = Double.parseDouble(pumpTrData.getPreset_qty());
					oilAmount = GlobalUtility.getMultipleWith1000(literInt); // 주유량
					
					priceInt = Double.parseDouble(pumpTrData.getPreset_prc()); // 현 주유 금액
					oilPrice_before_discount = GlobalUtility.getStringValue(priceInt); // 할인 전 금액
					oilPrice_after_discount = GlobalUtility.getStringValue(priceInt); // 할인 후 금액
					
					LogUtility.getLogger().info("[결제모듈 프로젝트] getUPOSMessage_OdtItemInfo_Item 주유량 : " + pumpTrData.getPreset_qty()+"/"+pumpTrData.getPreset_prc());
					LogUtility.getLogger().info("[결제모듈 프로젝트] getUPOSMessage_OdtItemInfo_Item 주유량 : " + GlobalUtility.getMultipleWith1000(literInt)+"/"+GlobalUtility.getStringValue(priceInt));
					
				} else {
					// 정액/정량 일 경우
					preset_basePriceInt = Double.parseDouble(pumpTrData.getPreset_baseprice());
				}
				
				// 금액 + 리터일 경우
				if(pumpTrData.getPreset_qty_prc_ind().equals("2")){
					LogUtility.getLogger().debug("[Pump M] 주유 완료 후 주유에 대한 정보를 얻습니다.");
					
					LogUtility.getLogger().debug("결제개선프로젝트 MOBILEPAY YN: " + mobilePayYn + "/" + pumpTrData.getPreset_baseprice());
					
					if(mobilePayYn != null && IUPOSConstant.MOBILEPAY_A.equals(mobilePayYn)){
						priceInt = Double.parseDouble(pumpTrData.getPreset_prc());          // 현 주유 금액
						literInt = Double.parseDouble(pumpTrData.getPreset_qty());          // 현 주유 수량
						
						oilPrice_before_discount = GlobalUtility.getStringValue(priceInt);  // 할인 전 금액
						oilPrice_after_discount = GlobalUtility.getStringValue(priceInt);	// 할인 후 금액
						
						oilAmount = GlobalUtility.getMultipleWith1000(literInt);			// 주유량
						
						T_KH_PUMP_TRData trData = T_KH_PUMP_TRHandler.getHandler().getT_KH_PUMP_TRData(khTransactionID) ;
						
						basePriceInt = Double.parseDouble(trData.getPreset_baseprice());	// 할인 후 단가 (사전예약시 preset_basePrice 사용)
						basePrice = trData.getPreset_baseprice();				        	// 할인 전 단가 (사전예약시 preset_basePrice 사용)
						
					} else {
						priceInt = Double.parseDouble(pumpTrData.getPreset_prc()); // 현 주유 금액
						literInt = Double.parseDouble(pumpTrData.getPreset_qty());
						beforePriceInt = GlobalUtility.multiple(literInt, nozzlePriceInt); // 리터 X 할인전 노줄 단가 
						
						BigDecimal before = new BigDecimal(Integer.parseInt(String.valueOf(Math.round(beforePriceInt))));
						before.setScale(0, RoundingMode.HALF_UP);
						
						// 할인 전 단가와 할인 후 단가가 동일할 경우 할인 후 금액을 할인전 금액으로 셋팅
						// 역계산의 이슈로 인한 문제 발생을 방지
						if(basePrice.equals(pumpTrData.getBaseprice())){
							LogUtility.getLogger().info("[Pump M] 단가 동일 할인전 금액 수정");
							oilPrice_before_discount = GlobalUtility.getStringValue(priceInt); // 할인 전 금액
						}else{
							LogUtility.getLogger().info("[Pump M] 단가 미동일 할인전 금액 미수정");
							oilPrice_before_discount = GlobalUtility.getPositiveValue(before.toString()); // 할인 전 금액
						}
						oilPrice_after_discount = GlobalUtility.getStringValue(priceInt);			 // 할인 후 금액
						oilAmount = GlobalUtility.getMultipleWith1000(literInt);						 // 주유량
						
						// 2016-05-27 pi2_phase2의 쿠폰 + 신용 or 쿠폰 + 현금일 경우 주유 완료 후 미만주유에 따른 4201요청 시 (주유금액 - 쿠폰 금액)을 계산해
						// 금액 및 리터로 셋팅되어 4201가 요청 됨. 문제는 금액이 쿠폰금액만큼 차감되어 셋팅 되고 리터는 주유량만큼
						// 셋팅이 되기에 주유금액과 리터가 차이가 날 수 있음 이 경우 (금액 / 단가)의 리터와 주유된 리터의 값이 차이날 경우 금액을 기준으로 리터를 다시 구한다. 
						//String	pi2_phase	=	PropertyManager.getSingleton().getProperty(PropertyManager.KH_DEPLOY_PHASE, PropertyManager.KH_DEPLOY_PHASE_DEFAULT);
						//if(pi2_phase.equals("2")){
						  
						  literInt = GlobalUtility.getValueByCertainDecimal(PumpMUtil.getPresetLiter(priceInt, preset_basePriceInt), 3); // 현 주유금액 / 단가
						  
						  String literStr = GlobalUtility.getMultipleWith1000(literInt);
						  BigDecimal liter = new BigDecimal(literStr);
					  	  BigDecimal compareLiter = new BigDecimal(oilAmount);
					  	  
					  	  LogUtility.getLogger().info("[Pump M] 현주유금액 / 단가로 구한  리터 값="+liter);
						  LogUtility.getLogger().info("[Pump M] 주유완료 후 넘어온 리터 값="+compareLiter);
						  
					  	  // 주유된 리터와 (금액/단가)로 구한 리터값을 비교 한다.
						  // 만약 비교된 리터의 값이 다를 경우 (금액/단가)로 구한 리터값을 oilAmount로 셋팅 한다.
					  	  if(liter.compareTo(compareLiter)!=0) oilAmount = GlobalUtility.getMultipleWith1000(literInt);	
					  	  
					  	  LogUtility.getLogger().info("[Pump M] 비교연산 후 리터 값="+oilAmount);
					    //}
					} 
					
				} else if (pumpTrData.getPreset_qty_prc_ind().equals("0")) {
					LogUtility.getLogger().debug("[Pump M] 주유 전 정액 주유에 대한 정보를 얻습니다.");
					
					priceInt = Double.parseDouble(pumpTrData.getPreset_prc()); // 현 주유 금액
					literInt = GlobalUtility.getValueByCertainDecimal(PumpMUtil.getPresetLiter(priceInt, preset_basePriceInt), 3); // 할인된 단가로 구한 리터
					beforePriceInt = GlobalUtility.multiple(literInt, nozzlePriceInt); // 리터 X 할인전 노줄 단가 
					
					BigDecimal before = new BigDecimal(Integer.parseInt(String.valueOf(Math.round(beforePriceInt))));
					before.setScale(0, RoundingMode.HALF_UP);
					
					// 할인 전 단가와 할인 후 단가가 동일할 경우 할인 후 금액을 할인전 금액으로 셋팅
					if(basePrice.equals(pumpTrData.getBaseprice())){
						LogUtility.getLogger().info("[Pump M] 단가 동일 할인전 금액 수정");
						oilPrice_before_discount = GlobalUtility.getStringValue(priceInt); // 할인 전 금액
					}else{
						LogUtility.getLogger().info("[Pump M] 단가 미동일 할인전 금액 미수정");
						oilPrice_before_discount = GlobalUtility.getStringValue(before.toString()); // 할인 전 금액
					}
					oilPrice_after_discount = GlobalUtility.getStringValue(priceInt);           // 할인 후 금액
					oilAmount = GlobalUtility.getMultipleWith1000(literInt);                      // 주유량
					
				} else if (pumpTrData.getPreset_qty_prc_ind().equals("1")) {
					LogUtility.getLogger().debug("[Pump M] 주유 전 정량 주유에 대한 정보를 얻습니다.");
					
					// 단가
					BigDecimal beforeBaseprice = new BigDecimal(basePrice).setScale(0,RoundingMode.HALF_UP);
					BigDecimal afterBaseprice = new BigDecimal(pumpTrData.getBaseprice()).setScale(0,RoundingMode.HALF_UP);
					
					// 수량
					BigDecimal pump_qty = new BigDecimal(pumpTrData.getPreset_qty());
					
					// 정량가
					BigDecimal beforeQty = beforeBaseprice.multiply(pump_qty); // 할인 전
					BigDecimal afterQty = afterBaseprice.multiply(pump_qty).setScale(0,RoundingMode.HALF_UP);   // 할인 후
					
					oilPrice_before_discount = beforeQty.toString();						// 할인 전 금액
					oilPrice_after_discount = afterQty.toString();							// 할인 후 금액
					oilAmount = GlobalUtility.getMultipleWith1000(pump_qty.toString());		// 주유량
				}
				LogUtility.getLogger().info("[Pump M] oilPrice_before_discount="+oilPrice_before_discount);
				
				unitPrice_before_discount = GlobalUtility.getMultipleWith1000(basePrice);
				unitPrice_after_discount = GlobalUtility.getMultipleWith1000(basePriceInt);

				taxPrice = GlobalUtility.getTaxPrice(oilPrice_after_discount);
				price_before_tax = PumpMUtil.getPrice_before_tax(oilPrice_after_discount, taxPrice);

				if (basePriceInt != preset_basePriceInt) {
					unitDiscount_ind = "1";
				}
			}
		} catch (Exception e) {
			LogUtility.getLogger().error(e.getMessage(), e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}

		uPosMsgItemInfo = CreateUPOSMessage.createUPOSMessage_ItemInfo_Item(
																							pumpTrData.getNozzle_no(), 
																							goodsCode, 
																							bonGoodsCode, 
																							oil_ind,
																							unitPrice_before_discount, 
																							oilAmount, 
																							unitPrice_after_discount,
																							tax_ind, 
																							price_before_tax, 
																							taxPrice, 
																							oilPrice_before_discount,
																							oilPrice_after_discount, 
																							khTransactionID,
																							rentlimit_proc_ind_overlimit, 
																							unitDiscount_ind,
																							keep_no,
																							issue_type);
		return uPosMsgItemInfo;
	}


	/**
	 * 결제 여부와 관련된 전문인지 알아본다
	 * 
	 * @param messageTypeInt :
	 *            UPOSMessage 의 Message Type
	 * @return
	 */
	public static boolean isPayedUPOSMessage(int messageTypeInt) {
		boolean rlt = false;

		try {
			switch (messageTypeInt) {
			case IUPOSConstant.MESSAGETYPE_INT_0004:
			case IUPOSConstant.MESSAGETYPE_INT_0012:
			case IUPOSConstant.MESSAGETYPE_INT_0022:
			case IUPOSConstant.MESSAGETYPE_INT_0014:
			case IUPOSConstant.MESSAGETYPE_INT_0024:
			case IUPOSConstant.MESSAGETYPE_INT_0016:
			case IUPOSConstant.MESSAGETYPE_INT_0026:
			case IUPOSConstant.MESSAGETYPE_INT_0032:
			case IUPOSConstant.MESSAGETYPE_INT_0034:
			case IUPOSConstant.MESSAGETYPE_INT_0042:
			case IUPOSConstant.MESSAGETYPE_INT_0044:
			case IUPOSConstant.MESSAGETYPE_INT_0046:
			case IUPOSConstant.MESSAGETYPE_INT_0048:
			case IUPOSConstant.MESSAGETYPE_INT_0052:
			case IUPOSConstant.MESSAGETYPE_INT_0054:
			case IUPOSConstant.MESSAGETYPE_INT_0056:
			case IUPOSConstant.MESSAGETYPE_INT_0062:
			case IUPOSConstant.MESSAGETYPE_INT_0082:
			case IUPOSConstant.MESSAGETYPE_INT_0084:
			case IUPOSConstant.MESSAGETYPE_INT_0092:
			case IUPOSConstant.MESSAGETYPE_INT_0094:
			case IUPOSConstant.MESSAGETYPE_INT_0096:
			//case IUPOSConstant.MESSAGETYPE_INT_0232: //간편결제조회
			//case IUPOSConstant.MESSAGETYPE_INT_0234: //현금IC난수조회
			{
				rlt = true;
			}
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		}
		return rlt;
	}

	/**
	 * GS , GS& , myLG 포인트 카드를 제외한 포인트 사용 결제 유무를 판단한다. GS , GS& , myLG 카드를 제외한
	 * 포인트 결제인 경우는 신용카드 전문과 동일하게 사용하지만, 할부 개월이 '61' 로 구분된다.
	 * 
	 * @param uPosMsg
	 * @return
	 */
	public static boolean isPointUseResponseWithoutGSCard_MyLGCard(int messageType, UPOSMessage uPosMsg) {
		boolean rlt = false ;
		try {
			switch (messageType) {
				case IUPOSConstant.MESSAGETYPE_INT_0046 :
				case IUPOSConstant.MESSAGETYPE_INT_0032 :
				case IUPOSConstant.MESSAGETYPE_INT_8046 :
				case IUPOSConstant.MESSAGETYPE_INT_8032 : { 	
					String credit_month = uPosMsg.getCredit_month() ;
					if (IConstant.OTHER_POINT_USE_CARD_MONTH.equals(credit_month)) {
						LogUtility.getPumpMLogger().info("[Pump M] Point Card Use (Not GS, myLG)") ;
						rlt = true ;
					}
					break ;
				}
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		}
		return rlt;
	}

	// CAT 단말기로부터 Preset 이전에 노즐 사용여부를 체크한다.
	// 셀프 주유기가 아닌 경우  pump state가 651(대기) 일 때 preset 가능
	// 셀프 주유기인 경우 pump state가 651(대기) 그리고 253일 때 preset 가능
	// 테스트 시 소모, 다쓰노는 주유기를 들었을 때 상태정보를 보내지만 동화셀프는 주유기를 들었을 때 상태정보를 보내지 않음.
	public static boolean isReadyForPreset(String nozzleID) {
		/*
	    	01:주유기 02:Self 주유기 03:Semi-Self 04:충전기 05:셀프ODT
		*/
		
		boolean isReady = false;
		// 노즐 타입
		String self_ind_exist = null;
		String statePumpCode = null;
		if(GlobalUtility.isNullOrEmptyString(nozzleID)){
			isReady = false;
			LogUtility.getCATMLogger().debug("Nozzle_no is Null or EmptyString!");
		}
		try {
			self_ind_exist = T_NZ_NOZZLEHandler.getHandler().getSelf_ind_exist(nozzleID);
			if(ICode.SELF_IND_EXIST_01_PUMP.equals(self_ind_exist) || ICode.SELF_IND_EXIST_03_SEMI_SELF.equals(self_ind_exist) 
					|| ICode.SELF_IND_EXIST_04_ODT_RECHARGE.equals(self_ind_exist)){
				if(StateMController.pumpStateHash.get(nozzleID) != null){
					statePumpCode = StateMController.pumpStateHash.get(nozzleID).getStateCode();
					if(!GlobalUtility.isNullOrEmptyString(statePumpCode)){
						int statePumpCodeInt = Integer.parseInt(statePumpCode);
						if(((statePumpCodeInt == IConstant.STATE_PUMP_STATECODE_651) || (statePumpCodeInt == IConstant.STATE_PUMP_STATECODE_657)) 
								&& !(PumpMTransactionManager.getInstance().getCurrState(nozzleID) == IPumpConstant.KH_PUMP_PRESET)){
							isReady = true;
						} else {
							LogUtility.getPumpMLogger().info("[Pump M] 노즐 상태로 인해서 Preset 을 할 수 없습니다. 상태코드=" 
						              + statePumpCodeInt + ". 노즐 상태=" + PumpMTransactionManager.getInstance().getCurrState(nozzleID));
							isReady = false;
						}
					}
				}
			}else{
				T_NZ_NOZZLEData nozInfo =  null;
				nozInfo =  T_NZ_NOZZLEHandler.getHandler().getT_NZ_NOZZLEDataByNozzleNo(nozzleID);
				if(nozInfo != null){
					T_NZ_NOZZLEData[] nzDataArray = null;	// 최초 요청한 Nozzle 과 연결된 ODT 의 연결된 모든 Nozzle 정보
					nzDataArray = T_NZ_NOZZLEHandler.getHandler().getT_NZ_NOZZLEDataByOdtno_no_connected(nozInfo.getOdtno_no_connected());
	        		if(nzDataArray != null){
	        			for(int i=0; i < nzDataArray.length; i++){
	        				String tempNozzleID = nzDataArray[i].getNozzle_no();
	        				if(StateMController.pumpStateHash.get(tempNozzleID) != null){
	        					statePumpCode = StateMController.pumpStateHash.get(tempNozzleID).getStateCode();
    	        				if(!GlobalUtility.isNullOrEmptyString(statePumpCode)){
    	        					int statePumpCodeInt = Integer.parseInt(statePumpCode);
    	        					if((statePumpCodeInt == IConstant.STATE_PUMP_STATECODE_651 || statePumpCodeInt == IConstant.STATE_PUMP_STATECODE_231
    	        							||  statePumpCodeInt == IConstant.STATE_PUMP_STATECODE_657)  
    	        							&& !(PumpMTransactionManager.getInstance().getCurrState(tempNozzleID) == IPumpConstant.KH_PUMP_PRESET)){
    	        						isReady = true;
    	        					}else{
    	    							LogUtility.getPumpMLogger().info("[Pump M] 노즐 상태로 인해서 Preset 을 할 수 없습니다. 상태코드=" 
    	  						              + statePumpCodeInt + ". 노즐 상태=" + PumpMTransactionManager.getInstance().getCurrState(tempNozzleID));
    	        						isReady = false;
    	        						break;
    	        					}
    	        				}else{
    	        					LogUtility.getPumpMLogger().info("[Pump M] 노즐 상태로 인해서 Preset 을 할 수 없습니다.statePumpCode= Null");
    	        					isReady = false;
	        						break;
    	        				}
	        				}else{
	        					LogUtility.getPumpMLogger().info("[Pump M] 노즐 상태로 인해서 Preset 을 할 수 없습니다. ODT 와 연결된 노즐 정보= Null");
	        					isReady = false ;
	        					break;
	        				}
	        			}
	        		}else{
	        			LogUtility.getPumpMLogger().info("[Pump M] 노즐 상태로 인해서 Preset 을 할 수 없습니다. 노즐 정보Array= Null");
	        		}
				}else{
					LogUtility.getPumpMLogger().info("[Pump M] 노즐 상태로 인해서 Preset 을 할 수 없습니다. 노즐 정보= Null");
				}
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.toString(), e);
		}
		return isReady;
	}

	/**
	 * 차량 단축 번호 요청에 대한 응답 전문을 구성합니다.
	 * 
	 * @param uPosInfo
	 * @param duPumpMsg
	 * @param completed
	 *            true : 주유완료 false : 주유중
	 * @return
	 */
	public static UPOSMessage process4201_4291(UPOSMessage uPosInfo, POS_DU duPumpMsg, boolean completed) {
		UPOSMessage uPosMessageReturn = null;

		String deviceType = uPosInfo.getDeviceType();
		String posReceipt_no = null;
		String nozzle_no = uPosInfo.getNozzle_no();
		UPOSMessage_ItemInfo item_info = null;
		String custCard_No = ""; // 거래처카드번호
		String ss_crStNum = ""; // 거래처번호
		String ss_carNum = ""; // 거래처차량번호
		String ss_crStNm = ""; // 거래처명
		String custCar_limit_type = ""; // 거래처별차량별한도적용구분
		String limit_type = ""; // 한도적용기준
		String limit_amt = ""; // 한도량,한도금액
		String limit_remain = ""; // 잔량,잔액
		String bonRSCard_ID = ""; // 보너스고객ID
		String local_point = ""; // 주유소(매장)점수
		String local_occurPoint = ""; // 발생매장점수
		UPOSMessage_CampInfo camp_info = null; // 캠페인정보 Structure
		UPOSMessage_AffilateInfo affilate_info = null; // 제휴카드 조회정보
		String download_flag = getDownloadFlag(); // 다운로드 Flag
		String pos_saleDate = UPOSUtil.getPosSaleDate(); // POS영업일자
		String term_ID = uPosInfo.getTerm_id(); // 단말기 번호
		String led_code = IUPOSConstant.LEDCODE_1; // LED코드 (default : 일반 정상)

		String unitPrint_yn = "1"; // 거래 단가 출력 여부
		String carNoPrint_yn = "1"; // 차량 번호 출력 여부
		String loanCustBonus_yn = "1"; // 외상 거래처 보너스 적립 유무
		String taxFreeCust_type = "0"; // 면세 유형
		String fixedQty_yn = "0"; // 정량 입력 여부
		String fixedQty = "0"; // 정량 값
		String save_head_title = ""; // 보관증 머리말
		String save_foot_title1 = ""; // 보관증 꼬리말1
		String save_foot_title2 = ""; // 보관증 꼬리말2
		String save_expire_date = ""; // 보관증 발행 유효 기간

		// DU전문에 거래처코드 추가에 따른 수정 2009.10.27 edited by ykjang
		UPOSMessage_TradeCondition tradeCondition = null; // 제휴카드 조회정보

		if (duPumpMsg.getDup() == 0) {
			led_code = IUPOSConstant.LEDCODE_5;
		} else {
			// DU전문에 거래처코드 추가에 따른 수정 2009.10.27 edited by ykjang
			affilate_info = createAffilate_info(duPumpMsg.getCarInfoArray());
			led_code = IUPOSConstant.LEDCODE_1;

			//affilate_info2 = createAffilate_info(duPumpMsg.getCarInfoArray(), true);
			tradeCondition = new UPOSMessage_TradeCondition();
			led_code = IUPOSConstant.LEDCODE_1;
		}

		taxFreeCust_type = uPosInfo.getTaxFreeCust_type();

		if (completed) {
			uPosMessageReturn = CreateUPOSMessage.createUPOSMessage_4292(
					deviceType, 
					posReceipt_no, 
					nozzle_no, 
					item_info,
					custCard_No, 
					ss_crStNum, 
					ss_carNum, 
					ss_crStNm,
					custCar_limit_type,
					limit_type, 
					GlobalUtility.getStringValue(limit_amt), 
					GlobalUtility.getStringValue(limit_remain), 
					uPosInfo.getBonRSCard_no(), 
					bonRSCard_ID,
					local_point,
					local_occurPoint, 
					camp_info, 
					affilate_info,
					download_flag,
					pos_saleDate, 
					term_ID,
					led_code,
					unitPrint_yn,
					carNoPrint_yn,
					loanCustBonus_yn,
					taxFreeCust_type,
					fixedQty_yn,
					PumpMUtil.convertNumberFormatFromPumpToUPOS(fixedQty),
					save_head_title, 
					save_foot_title1, 
					save_foot_title2,
					save_expire_date,
					tradeCondition);

		} else {
			uPosMessageReturn = CreateUPOSMessage.createUPOSMessage_4202(
					deviceType, 
					posReceipt_no, 
					nozzle_no, 
					item_info,
					custCard_No,
					ss_crStNum, 
					ss_carNum, 
					ss_crStNm,
					custCar_limit_type,
					limit_type, 
					GlobalUtility.getStringValue(limit_amt), 
					GlobalUtility.getStringValue(limit_remain), 
					uPosInfo.getBonRSCard_no(), 
					bonRSCard_ID,
					local_point,
					local_occurPoint,
					camp_info,
					affilate_info, 
					download_flag,
					pos_saleDate,
					term_ID,
					led_code, 
					unitPrint_yn,
					carNoPrint_yn, 
					loanCustBonus_yn, 
					taxFreeCust_type,
					fixedQty_yn, 
					PumpMUtil.convertNumberFormatFromPumpToUPOS(fixedQty),
					save_head_title,
					save_foot_title1, 
					save_foot_title2,
					save_expire_date,
					tradeCondition);
		}

		if (tradeCondition != null) {
			uPosMessageReturn.setTradeCondition(tradeCondition);
		}

		return uPosMessageReturn;
	}

	
	/**
	 * 거래처 고객의 주유중 / 주유완료 정보를 구성하여 응답합니다.
	 * 
	 * @param uPosInfo
	 * @param dyPumpMsg
	 * @param completed
	 *            true : 주유완료 false : 주유중
	 * @return
	 */
	public static UPOSMessage process4201_4291(UPOSMessage uPosInfo,	POS_DY dyPumpMsg, boolean completed) {
		UPOSMessage uPosMessageReturn = null;

		String deviceType = uPosInfo.getDeviceType();
		String posReceipt_no = null;
		String nozzle_no = uPosInfo.getNozzle_no();
		UPOSMessage_ItemInfo item_info = null;
		String custCard_No = ""; // 거래처카드번호
		String ss_crStNum = ""; // 거래처번호
		String ss_carNum = ""; // 거래처차량번호
		String ss_crStNm = ""; // 거래처명
		String custCar_limit_type = ""; // 거래처별차량별한도적용구분
		String adjbase_code_limit = ""; // 한도적용기준
		String limit_amt = ""; // 한도량,한도금액
		String limit_remain = ""; // 잔량,잔액
		String bonRSCard_ID = ""; // 보너스고객ID
		String local_point = ""; // 주유소(매장)점수
		String local_occurPoint = ""; // 발생매장점수
		UPOSMessage_CampInfo camp_info = null; // 캠페인정보 Structure
		UPOSMessage_AffilateInfo affilate_info = null; // 제휴카드 조회정보
		String download_flag = getDownloadFlag(); // 다운로드 Flag
		String pos_saleDate = UPOSUtil.getPosSaleDate(); // POS영업일자
		String term_ID = uPosInfo.getTerm_id(); // 단말기 번호
		String led_code = IUPOSConstant.LEDCODE_1; // LED코드 (default : 일반 정상)
		String unitPrint_yn = "1"; // 거래 단가 출력 여부
		String carNoPrint_yn = "1"; // 차량 번호 출력 여부
		String loanCustBonus_yn = "1"; // 외상 거래처 보너스 적립 유무
		String taxFreeCust_type = "0"; // 면세 유형
		String fixedQty_yn = "0"; // 정량 입력 여부
		String fixedQty = "0"; // 정량 값
		String save_head_title = ""; // 보관증 머리말
		String save_foot_title1 = ""; // 보관증 꼬리말1
		String save_foot_title2 = ""; // 보관증 꼬리말2
		String save_expire_date = ""; // 보관증 발행 유효 기간

		String cust_card_ind = dyPumpMsg.getCust_card_ind();

		if (cust_card_ind.equals(ICode.CUST_CARD_IND_02)) {
			custCard_No = dyPumpMsg.getCust_card_no(); 				// 거래처카드번호
		}
		ss_crStNum = dyPumpMsg.getCust_no(); 						// 거래처번호
		ss_carNum = dyPumpMsg.getCar_no(); 							// 거래처차량번호
		ss_crStNm = dyPumpMsg.getCust_name(); 						// 거래처명
		led_code = dyPumpMsg.getLed_code();
		posReceipt_no = dyPumpMsg.getKhTransactionID();
		unitPrint_yn = dyPumpMsg.getUnitPrint_yn(); 				// 거래 단가 출력 여부
		carNoPrint_yn = dyPumpMsg.getCarNoPrint_yn(); 				// 차량 번호 출력 여부
		loanCustBonus_yn = dyPumpMsg.getCust_mileage_ind(); 		// 외상 거래처 보너스 적립 유무
		taxFreeCust_type = dyPumpMsg.getTaxFreeCust_type(); 		// 면세 유형
		fixedQty_yn = dyPumpMsg.getFixedQty_yn(); 					// 정량 입력 여부
		fixedQty = dyPumpMsg.getFixedQty(); 						// 정량 값
		save_head_title = dyPumpMsg.getSave_head_title(); 			// 보관증 머리말
		save_foot_title1 = dyPumpMsg.getSave_foot_title1(); 		// 보관증 꼬리말1
		save_foot_title2 = dyPumpMsg.getSave_foot_title2(); 		// 보관증 꼬리말2
		save_expire_date = dyPumpMsg.getSave_expire_date(); 		// 보관증 발행 유효 기간

		String card_code_base = dyPumpMsg.getCard_code_base(); 		// 카드 기준
		String cardadj_ind = dyPumpMsg.getCardadj_ind(); 			// 카드 적용 구분
		adjbase_code_limit = dyPumpMsg.getAdjbase_code_limit(); 	// 한도적용기준, 01=수량 ,02=금액
		
		item_info = convertToUPOSMessage_ItemInfo(dyPumpMsg);
		UPOSMessage_TradeCondition affilate_info2 = null;
		
		if (card_code_base.equals(ICode.CARD_CODE_BASE_02)) {
			// 외상 거래처 // 거래처별 // 차량별
			if ((cardadj_ind.equals(ICode.CARDADJ_IND_02)) || (cardadj_ind.equals(ICode.CARDADJ_IND_03))) {
				custCar_limit_type = dyPumpMsg.getLimit_type();
				// ksm 2012.03.22 위로 올림.
				//limit_type = dyPumpMsg.getAdjbase_code_limit(); // 한도적용기준, 01=수량 ,02=금액
				if (adjbase_code_limit.equals(ICode.ADJBASE_CODE_LIMIT_01)) {
					limit_amt = GlobalUtility.getMultipleWith1000(dyPumpMsg.getLimit()); // 한도량
					limit_remain = GlobalUtility.getMultipleWith1000(dyPumpMsg.getLimit_remains()); // 잔량
				} else {
					limit_amt = dyPumpMsg.getLimit(); // 한도금액
					limit_remain = dyPumpMsg.getLimit_remains(); // 잔액
				}
			}
		}
		
		//ksm 2012.03.23 매출보관 거래처 처리시 CAT으로 한도잔량 및 잔량 전송
		//	카드기준 - 04: 매출보관거래처

		if(ICode.CARD_CODE_BASE_04.equals(card_code_base)){	
			// 카드적용구분(cardadj_ind) - 06: 매출보관용 , 한도적용기준(limit_type) - 01:수량 02:금액
			if ((ICode.CARDADJ_IND_06.equals(cardadj_ind)) && ICode.ADJBASE_CODE_LIMIT_01.equals(adjbase_code_limit)) {	
				
				custCar_limit_type = dyPumpMsg.getLimit_type();
				
				limit_amt = GlobalUtility.getMultipleWith1000(dyPumpMsg.getLimit()); // 한도량
				limit_remain = GlobalUtility.getMultipleWith1000(dyPumpMsg.getLimit_remains()); // 잔량
			}
		}
		
		// LED Code 재확인
		if (item_info == null) {
			switch (led_code) {
				case IUPOSConstant.LEDCODE_1 :
				case IUPOSConstant.LEDCODE_2 :
				case IUPOSConstant.LEDCODE_3 :
				case IUPOSConstant.LEDCODE_4 :
				case IUPOSConstant.LEDCODE_8 :
				case IUPOSConstant.LEDCODE_9 :
				case IUPOSConstant.LEDCODE_A :
					LogUtility.getPumpMLogger().debug("[Pump M] No Pump Info : Set LED Code=0");
					LogUtility.getPumpMLogger().warn("[Pump M] Code developer should investigate code in more detail.");					
					led_code = IUPOSConstant.LEDCODE_0;
					break ;
			}
		}

		if (completed) {
			uPosMessageReturn = CreateUPOSMessage.createUPOSMessage_4292(
					deviceType, posReceipt_no, nozzle_no, item_info,
					custCard_No, ss_crStNum, ss_carNum, ss_crStNm,
					custCar_limit_type, adjbase_code_limit, GlobalUtility.getStringValue(limit_amt), 
					GlobalUtility.getStringValue(limit_remain), 
					uPosInfo.getBonRSCard_no(), bonRSCard_ID, local_point,
					local_occurPoint, camp_info, affilate_info, download_flag,
					pos_saleDate, term_ID, led_code, unitPrint_yn,
					carNoPrint_yn, loanCustBonus_yn, taxFreeCust_type,
					fixedQty_yn, PumpMUtil.convertNumberFormatFromPumpToUPOS(fixedQty),
					save_head_title, save_foot_title1, save_foot_title2,
					save_expire_date, affilate_info2);

		} else {
			uPosMessageReturn = CreateUPOSMessage.createUPOSMessage_4202(
					deviceType, posReceipt_no, nozzle_no, item_info,
					custCard_No, ss_crStNum, ss_carNum, ss_crStNm,
					custCar_limit_type, adjbase_code_limit, GlobalUtility.getStringValue(limit_amt), 
					GlobalUtility.getStringValue(limit_remain), 
					uPosInfo.getBonRSCard_no(), bonRSCard_ID, local_point,
					local_occurPoint, camp_info, affilate_info, download_flag,
					pos_saleDate, term_ID, led_code, unitPrint_yn,
					carNoPrint_yn, loanCustBonus_yn, taxFreeCust_type,
					fixedQty_yn, PumpMUtil.convertNumberFormatFromPumpToUPOS(fixedQty),
					save_head_title, save_foot_title1, save_foot_title2,
					save_expire_date, affilate_info2);
		}

		return uPosMessageReturn;
	}

	
	/**
	 * 일반 고객의 주유중/주유완료 요청에 대한 응답
	 * 
	 * @param uPosInfo
	 * @param uPosMsgItemInfo
	 * @param completed
	 *            true : 주유완료 false : 주유중
	 * @return
	 */
	public static UPOSMessage process4201_4291(UPOSMessage uPosInfo,
			UPOSMessage_ItemInfo_Item uPosMsgItemInfo, boolean completed) {
		UPOSMessage uPosMessageReturn = null;

		String deviceType = uPosInfo.getDeviceType();
		String posReceipt_no = null;
		String nozzle_no = uPosInfo.getNozzle_no();
		UPOSMessage_ItemInfo item_info = null;
		String custCard_No = ""; // 거래처카드번호
		String ss_crStNum = ""; // 거래처번호
		String ss_carNum = ""; // 거래처차량번호
		String ss_crStNm = ""; // 거래처명
		String custCar_limit_type = ""; // 거래처별차량별한도적용구분
		String limit_type = ""; // 한도적용기준
		String limit_amt = ""; // 한도량,한도금액
		String limit_remain = ""; // 잔량,잔액
		String bonRSCard_ID = ""; // 보너스고객ID
		String local_point = ""; // 주유소(매장)점수
		String local_occurPoint = ""; // 발생매장점수
		UPOSMessage_CampInfo camp_info = null; // 캠페인정보 Structure
		UPOSMessage_AffilateInfo affilate_info = null; // 제휴카드 조회정보
		String download_flag = getDownloadFlag(); // 다운로드 Flag
		String pos_saleDate = UPOSUtil.getPosSaleDate(); // POS영업일자
		String term_ID = uPosInfo.getTerm_id(); // 단말기 번호
		String led_code = IUPOSConstant.LEDCODE_1; // LED코드 (default : 일반 정상)
		String unitPrint_yn = "1"; // 거래 단가 출력 여부
		String carNoPrint_yn = "1"; // 차량 번호 출력 여부
		String loanCustBonus_yn = "1"; // 외상 거래처 보너스 적립 유무
		String taxFreeCust_type = "0"; // 면세 유형
		String fixedQty_yn = "0"; // 정량 입력 여부
		String fixedQty = "0"; // 정량 값
		String save_head_title = ""; // 보관증 머리말
		String save_foot_title1 = ""; // 보관증 꼬리말1
		String save_foot_title2 = ""; // 보관증 꼬리말2
		String save_expire_date = ""; // 보관증 발행 유효 기간
		UPOSMessage_TradeCondition affilate_info2 = null;
		
		item_info = UPOSMessage_ItemInfo.createUPOSMessage_ItemInfo(uPosMsgItemInfo);

		if (item_info != null) {
			ArrayList<UPOSMessage_ItemInfo_Item> itemInfoArray = item_info.getItemInfoList();
			UPOSMessage_ItemInfo_Item itemInfo_item = null;
			
			if (item_info.isItemInfoExist()) {
				itemInfo_item = itemInfoArray.get(0);
				posReceipt_no = itemInfo_item.getKhTransactionID();
				led_code = IUPOSConstant.LEDCODE_1;
			} else {
				LogUtility.getPumpMLogger().debug("[Pump M] No Pump Info: Set ledCode=0");
				led_code = IUPOSConstant.LEDCODE_0;
			}
		}

		if (completed) {
			uPosMessageReturn = CreateUPOSMessage.createUPOSMessage_4292(deviceType, 
																			posReceipt_no, 
																			nozzle_no, 
																			item_info,
																			custCard_No,
																			ss_crStNum, 
																			ss_carNum, 
																			ss_crStNm,
																			custCar_limit_type,
																			limit_type, 
																			GlobalUtility.getPositiveValue(limit_amt), 
																			GlobalUtility.getPositiveValue(limit_remain), 
																			uPosInfo.getBonRSCard_no(), 
																			bonRSCard_ID, local_point,
																			local_occurPoint,
																			camp_info, 
																			affilate_info,
																			download_flag,
																			pos_saleDate,
																			term_ID, 
																			led_code, 
																			unitPrint_yn,
																			carNoPrint_yn,
																			loanCustBonus_yn,
																			taxFreeCust_type,	
																			fixedQty_yn,
																			PumpMUtil.convertNumberFormatFromPumpToUPOS(fixedQty),
																			save_head_title, 
																			save_foot_title1, 
																			save_foot_title2,
																			save_expire_date,
																			affilate_info2);

		} else {
 			uPosMessageReturn = CreateUPOSMessage.createUPOSMessage_4202(deviceType,
 					posReceipt_no,
 					nozzle_no, 
 					item_info,
 					custCard_No, 
 					ss_crStNum, 
 					ss_carNum,
 					ss_crStNm,	
 					custCar_limit_type,
 					limit_type,																
 					GlobalUtility.getPositiveValue(limit_amt), 
 					GlobalUtility.getPositiveValue(limit_remain), 
 					uPosInfo.getBonRSCard_no(),
 					bonRSCard_ID, 
 					local_point,
 					local_occurPoint,
 					camp_info,
 					affilate_info,
 					download_flag,
 					pos_saleDate,
 					term_ID,
 					led_code, 
 					unitPrint_yn,
 					carNoPrint_yn,
 					loanCustBonus_yn, 
 					taxFreeCust_type,
 					fixedQty_yn, 
 					PumpMUtil.convertNumberFormatFromPumpToUPOS(fixedQty),
 					save_head_title, 
 					save_foot_title1, 
 					save_foot_title2,
 					save_expire_date,
 					affilate_info2);
		}
		// PI2, cwi, 2015-12-31 4202 또는 4292 전문에 리딩매체 데이터가 체워지지 않아 해당 내용 추가
		// PI2, cwi, 2016-02-16 ODT에서 올라온 현장할인 여부 내역을 응답 전문에 추가 한다.
		uPosMessageReturn.setCreditCardReading_type(uPosInfo.getCreditCardReading_type()); // 신용카드 리딩매체
		uPosMessageReturn.setBonCardReading_type(uPosInfo.getBonCardReading_type());	   // 보너스카드 리딩매체
		uPosMessageReturn.setCustGoods_code(uPosInfo.getCustGoods_code()); 				   // 화물특화거래상품코드
		
		// PI2, cwi, 2016-03-04, 보너스 카드가 없을 경우 현장할인 여부를 0으로 변경한다. - 페이지 2
		if(GlobalUtility.isNullOrEmptyString(uPosInfo.getBonRSCard_no())){
			uPosMessageReturn.setPromptDiscount_yn("0");  // 현장할인여부 
		}else{
			uPosMessageReturn.setPromptDiscount_yn(uPosInfo.getPromptDiscount_yn()); // 현장할인여부 
		}
		
		// pi2, cwi, 2016-03-31
		// properties 값을 불러 들여 phase 1, 2를 분리 하며 phase 1일 경우 현장할인 강제 0으로 셋팅
		/*String	pi2_phase	=	PropertyManager.getSingleton().getProperty(PropertyManager.KH_DEPLOY_PHASE, PropertyManager.KH_DEPLOY_PHASE_DEFAULT);
		if(("1").equals(pi2_phase)){
			if(uPosMessageReturn.getDeviceType().equals(IUPOSConstant.DEVICE_TYPE_3S)){
				uPosMessageReturn.setPromptDiscount_yn("0");  // 현장할인여부 
			}
		}*/
		
		LogUtility.getLogger().debug("[Pump M] PromptDiscount_yn="+uPosMessageReturn.getPromptDiscount_yn());
		return uPosMessageReturn;
	}

	/**
	 * CAT단말기 Preset 요청에 대한 응답(거래처고객)
	 * 
	 * @param uPosInfo
	 * @param uPosMsgItemInfo
	 * @param completed
	 *            true : 주유완료 false : 주유중
	 * @return
	 */
	public static UPOSMessage process4301(UPOSMessage uPosInfo,	POS_DY dyPumpMsg) {
		UPOSMessage uPosMessageReturn = null;

		String deviceType = uPosInfo.getDeviceType();
		String posReceipt_no = null;
		String nozzle_no = uPosInfo.getNozzle_no();
		UPOSMessage_ItemInfo item_info = null;
		String custCard_No = ""; // 거래처카드번호
		String ss_crStNum = ""; // 거래처번호
		String ss_carNum = ""; // 거래처차량번호
		String ss_crStNm = ""; // 거래처명
		String custCar_limit_type = ""; // 거래처별차량별한도적용구분
		String adjbase_code_limit = ""; // 한도적용기준
		String limit_amt = ""; // 한도량,한도금액
		String limit_remain = ""; // 잔량,잔액
		String bonRSCard_ID = ""; // 보너스고객ID
		String local_point = ""; // 주유소(매장)점수
		String local_occurPoint = ""; // 발생매장점수
		UPOSMessage_CampInfo camp_info = null; // 캠페인정보 Structure
		UPOSMessage_AffilateInfo affilate_info = null; // 제휴카드 조회정보
		String download_flag = getDownloadFlag(); // 다운로드 Flag
		String pos_saleDate = UPOSUtil.getPosSaleDate(); // POS영업일자
		String term_ID = uPosInfo.getTerm_id(); // 단말기 번호
		String led_code = IUPOSConstant.LEDCODE_1; // LED코드 (default : 일반 정상)
		String unitPrint_yn = "1"; // 거래 단가 출력 여부
		String carNoPrint_yn = "1"; // 차량 번호 출력 여부
		String loanCustBonus_yn = "1"; // 외상 거래처 보너스 적립 유무
		String taxFreeCust_type = "0"; // 면세 유형
		String fixedQty_yn = "0"; // 정량 입력 여부
		String fixedQty = "0"; // 정량 값
		String save_head_title = ""; // 보관증 머리말
		String save_foot_title1 = ""; // 보관증 꼬리말1
		String save_foot_title2 = ""; // 보관증 꼬리말2
		String save_expire_date = ""; // 보관증 발행 유효 기간
		UPOSMessage_TradeCondition affilate_info2 = null;

		String cust_card_ind = dyPumpMsg.getCust_card_ind();

		if (cust_card_ind.equals(ICode.CUST_CARD_IND_02)) {
			custCard_No = dyPumpMsg.getCust_card_no(); // 거래처카드번호
		}
		ss_crStNum = dyPumpMsg.getCust_no(); // 거래처번호
		ss_carNum = dyPumpMsg.getCar_no(); // 거래처차량번호
		ss_crStNm = dyPumpMsg.getCust_name(); // 거래처명
		led_code = dyPumpMsg.getLed_code();
		posReceipt_no = dyPumpMsg.getKhTransactionID();
		unitPrint_yn = dyPumpMsg.getUnitPrint_yn(); // 거래 단가 출력 여부
		carNoPrint_yn = dyPumpMsg.getCarNoPrint_yn(); // 차량 번호 출력 여부
		loanCustBonus_yn = dyPumpMsg.getCust_mileage_ind(); // 외상 거래처 보너스 적립 유무
		taxFreeCust_type = dyPumpMsg.getTaxFreeCust_type(); // 면세 유형
		fixedQty_yn = dyPumpMsg.getFixedQty_yn(); // 정량 입력 여부
		fixedQty = dyPumpMsg.getFixedQty(); // 정량 값
		save_head_title = dyPumpMsg.getSave_head_title(); // 보관증 머리말
		save_foot_title1 = dyPumpMsg.getSave_foot_title1(); // 보관증 꼬리말1
		save_foot_title2 = dyPumpMsg.getSave_foot_title2(); // 보관증 꼬리말2
		save_expire_date = dyPumpMsg.getSave_expire_date(); // 보관증 발행 유효 기간

		String card_code_base = dyPumpMsg.getCard_code_base(); // 카드 기준
		String cardadj_ind = dyPumpMsg.getCardadj_ind(); // 카드 적용 구분
		adjbase_code_limit = dyPumpMsg.getAdjbase_code_limit(); // 한도적용기준, 01=수량 ,02=금액
		
		item_info = convertToUPOSMessage_ItemInfo(dyPumpMsg);
		
		if (card_code_base.equals(ICode.CARD_CODE_BASE_02)) {
			// 외상 거래처 // 거래처별 // 차량별
			if ((cardadj_ind.equals(ICode.CARDADJ_IND_02)) || (cardadj_ind.equals(ICode.CARDADJ_IND_03))) {
				custCar_limit_type = dyPumpMsg.getLimit_type();
				// ksm 2012.03.22 위로 올림.
				//limit_type = dyPumpMsg.getAdjbase_code_limit(); // 한도적용기준, 01=수량 ,02=금액
				if (adjbase_code_limit.equals(ICode.ADJBASE_CODE_LIMIT_01)) {
					limit_amt = GlobalUtility.getMultipleWith1000(dyPumpMsg.getLimit()); // 한도량
					limit_remain = GlobalUtility.getMultipleWith1000(dyPumpMsg.getLimit_remains()); // 잔량
				} else {
					limit_amt = dyPumpMsg.getLimit(); // 한도금액
					limit_remain = dyPumpMsg.getLimit_remains(); // 잔액
				}
			}
		}
		
		//ksm 2012.03.23 매출보관 거래처 처리시 CAT으로 한도잔량 및 잔량 전송
		//	카드기준 - 04: 매출보관거래처

		if(ICode.CARD_CODE_BASE_04.equals(card_code_base)){	
			// 카드적용구분(cardadj_ind) - 06: 매출보관용 , 한도적용기준(limit_type) - 01:수량 02:금액
			if ((ICode.CARDADJ_IND_06.equals(cardadj_ind)) && ICode.ADJBASE_CODE_LIMIT_01.equals(adjbase_code_limit)) {	
				
				custCar_limit_type = dyPumpMsg.getLimit_type();
				
				limit_amt = GlobalUtility.getMultipleWith1000(dyPumpMsg.getLimit()); // 한도량
				limit_remain = GlobalUtility.getMultipleWith1000(dyPumpMsg.getLimit_remains()); // 잔량
			}
		}
		
		// LED Code 재확인
		if (item_info == null) {
			switch (led_code) {
				case IUPOSConstant.LEDCODE_1 :
				case IUPOSConstant.LEDCODE_2 :
				case IUPOSConstant.LEDCODE_3 :
				case IUPOSConstant.LEDCODE_4 :
				case IUPOSConstant.LEDCODE_8 :
				case IUPOSConstant.LEDCODE_9 :
				case IUPOSConstant.LEDCODE_A :
					LogUtility.getPumpMLogger().debug("[Pump M] No Pump Info : Set LED Code=0");
					LogUtility.getPumpMLogger().warn("[Pump M] Code developer should investigate code in more detail.");
					
					led_code = IUPOSConstant.LEDCODE_0 ;
					break ;
			}
		}

			uPosMessageReturn = CreateUPOSMessage.createUPOSMessage_4302(
					deviceType, posReceipt_no, nozzle_no, item_info,
					custCard_No, ss_crStNum, ss_carNum, ss_crStNm,
					custCar_limit_type, adjbase_code_limit, GlobalUtility.getStringValue(limit_amt), 
					GlobalUtility.getStringValue(limit_remain), 
					uPosInfo.getBonRSCard_no(), bonRSCard_ID, local_point,
					local_occurPoint, camp_info, affilate_info, download_flag,
					pos_saleDate, term_ID, led_code, unitPrint_yn,
					carNoPrint_yn, loanCustBonus_yn, taxFreeCust_type,
					fixedQty_yn, PumpMUtil.convertNumberFormatFromPumpToUPOS(fixedQty),
					save_head_title, save_foot_title1, save_foot_title2,
					save_expire_date, affilate_info2);

		return uPosMessageReturn;
	}
	
	
	/**
	 * CAT단말기 Preset 요청에 대한 응답(일반고객)
	 * 
	 * @param uPosInfo
	 * @param uPosMsgItemInfo
	 * @param completed
	 *            true : 주유완료 false : 주유중
	 * @return
	 */
	public static UPOSMessage process4301(UPOSMessage uPosInfo,
			UPOSMessage_ItemInfo_Item uPosMsgItemInfo) {
		UPOSMessage uPosMessageReturn = null;

		String deviceType = uPosInfo.getDeviceType();
		String posReceipt_no = null;
		String nozzle_no = uPosInfo.getNozzle_no();
		UPOSMessage_ItemInfo item_info = null;
		String custCard_No = ""; // 거래처카드번호
		String ss_crStNum = ""; // 거래처번호
		String ss_carNum = ""; // 거래처차량번호
		String ss_crStNm = ""; // 거래처명
		String custCar_limit_type = ""; // 거래처별차량별한도적용구분
		String limit_type = ""; // 한도적용기준
		String limit_amt = ""; // 한도량,한도금액
		String limit_remain = ""; // 잔량,잔액
		String bonRSCard_ID = ""; // 보너스고객ID
		String local_point = ""; // 주유소(매장)점수
		String local_occurPoint = ""; // 발생매장점수
		UPOSMessage_CampInfo camp_info = null; // 캠페인정보 Structure
		UPOSMessage_AffilateInfo affilate_info = null; // 제휴카드 조회정보
		String download_flag = getDownloadFlag(); // 다운로드 Flag
		String pos_saleDate = UPOSUtil.getPosSaleDate(); // POS영업일자
		String term_ID = uPosInfo.getTerm_id(); // 단말기 번호
		String led_code = IUPOSConstant.LEDCODE_1; // LED코드 (default : 일반 정상)
		String unitPrint_yn = "1"; // 거래 단가 출력 여부
		String carNoPrint_yn = "1"; // 차량 번호 출력 여부
		String loanCustBonus_yn = "1"; // 외상 거래처 보너스 적립 유무
		String taxFreeCust_type = "0"; // 면세 유형
		String fixedQty_yn = "0"; // 정량 입력 여부
		String fixedQty = "0"; // 정량 값
		String save_head_title = ""; // 보관증 머리말
		String save_foot_title1 = ""; // 보관증 꼬리말1
		String save_foot_title2 = ""; // 보관증 꼬리말2
		String save_expire_date = ""; // 보관증 발행 유효 기간
		UPOSMessage_TradeCondition affilate_info2 = null;

		item_info = UPOSMessage_ItemInfo.createUPOSMessage_ItemInfo(uPosMsgItemInfo);

		if (item_info != null) {
			ArrayList<UPOSMessage_ItemInfo_Item> itemInfoArray = item_info.getItemInfoList();
			UPOSMessage_ItemInfo_Item itemInfo_item = null;
			
			if (item_info.isItemInfoExist()) {
				itemInfo_item = itemInfoArray.get(0);
				posReceipt_no = itemInfo_item.getKhTransactionID();
				led_code = IUPOSConstant.LEDCODE_1;
			} else {
				LogUtility.getPumpMLogger().debug("[Pump M] No Pump Info: Set ledCode=0");
				led_code = IUPOSConstant.LEDCODE_0;
			}
		}

			uPosMessageReturn = CreateUPOSMessage.createUPOSMessage_4302(
					deviceType, posReceipt_no, nozzle_no, item_info,
					custCard_No, ss_crStNum, ss_carNum, ss_crStNm,
					custCar_limit_type, limit_type, 
					GlobalUtility.getStringValue(limit_amt), 
					GlobalUtility.getStringValue(limit_remain), 
					uPosInfo.getBonRSCard_no(), bonRSCard_ID, local_point,
					local_occurPoint, camp_info, affilate_info, download_flag,
					pos_saleDate, term_ID, led_code, unitPrint_yn,
					carNoPrint_yn, loanCustBonus_yn, taxFreeCust_type,	fixedQty_yn, 
					PumpMUtil.convertNumberFormatFromPumpToUPOS(fixedQty),
					save_head_title, save_foot_title1, save_foot_title2,
					save_expire_date, affilate_info2);

		return uPosMessageReturn;
	}
	
	/**
	 * CAT M 으로 전문 전송시 Campaign 실행 여부를 조사한다. Campaign 이 true 인 경우 CAT M 로 전송이
	 * 되고, CAT M 은 CMS M 으로 정보를 전송한다. 그리고 CMS M 은 다음을 수행한다. 1. 보너스 카드 고객에 대한 매장
	 * 점수 2. Campaign 수행
	 * 
	 * 따라서 CAT M 으로 전송하기 전 다음을 확인하고, 다음 사항 중 하나에 속할 경우 Campaign 을 false 로 리턴한다.
	 * 1. 전문의 Message Type 확인 2. Item Info (주유자료) 가 없는 경우 3. 주유중/완료 캠페인 주유정보 응답인
	 * 전문에서 LED Code 가 0 인 경우
	 * 
	 * @param uPosMsg :
	 *            UPOSMessage
	 * @return
	 */
	public static boolean shouldCampaign(UPOSMessage uPosMsg) {

		int messageTypeInt = Integer.parseInt(uPosMsg.getMessageType());
		switch (messageTypeInt) {
			case IUPOSConstant.MESSAGETYPE_INT_0071: // 신용카드 확인 요청 --> 셀프용
			case IUPOSConstant.MESSAGETYPE_INT_8071: // 신용카드 확인 취소요청 --> 셀프용
			case IUPOSConstant.MESSAGETYPE_INT_0101: // 거래처카드번호 요청
			case IUPOSConstant.MESSAGETYPE_INT_0103: // GS보너스카드번호 요청
			case IUPOSConstant.MESSAGETYPE_INT_0105: // S보너스카드번호 + 거래처카드번호 요청
			case IUPOSConstant.MESSAGETYPE_INT_0107: // 신용카드번호 요청
			case IUPOSConstant.MESSAGETYPE_INT_0102: // 거래처카드번호 응답
			case IUPOSConstant.MESSAGETYPE_INT_0104: // GS보너스카드번호 응답
			case IUPOSConstant.MESSAGETYPE_INT_0106: // S보너스카드번호 + 거래처카드번호 응답
			case IUPOSConstant.MESSAGETYPE_INT_0108: // 신용카드번호 응답
			case IUPOSConstant.MESSAGETYPE_INT_0302: // 상품코드다운로드 응답
			case IUPOSConstant.MESSAGETYPE_INT_0091: // 사용자임의 취소 요청
			case IUPOSConstant.MESSAGETYPE_INT_0119: // 바코드번호 조회 요청
			case IUPOSConstant.MESSAGETYPE_INT_0120: // 바코드번호 조회 응답				
			case IUPOSConstant.MESSAGETYPE_INT_0113: // myLG 보너스점수조회요청
			case IUPOSConstant.MESSAGETYPE_INT_0115: // 전자상품권거래내역조회 요청
	
			case IUPOSConstant.MESSAGETYPE_INT_0021:
			case IUPOSConstant.MESSAGETYPE_INT_0015:			
			case IUPOSConstant.MESSAGETYPE_INT_0025:
			case IUPOSConstant.MESSAGETYPE_INT_0031:
			case IUPOSConstant.MESSAGETYPE_INT_0231:  //간편결제 조회 요청    
			case IUPOSConstant.MESSAGETYPE_INT_0232:  //간편결제 조회 응답    
			case IUPOSConstant.MESSAGETYPE_INT_0041:
			case IUPOSConstant.MESSAGETYPE_INT_0045:  //페이결제 요청
			case IUPOSConstant.MESSAGETYPE_INT_0047:  //페이결제보너스 요청

			case IUPOSConstant.MESSAGETYPE_INT_0235:  //현대CarPay 차량번호 조회 요청
			case IUPOSConstant.MESSAGETYPE_INT_0241:  //모바일플랫폼 결제 요청
			case IUPOSConstant.MESSAGETYPE_INT_0243:  //현대CarPay 결제 요청
			case IUPOSConstant.MESSAGETYPE_INT_0245:  //바로세차 결제 요청
	
			case IUPOSConstant.MESSAGETYPE_INT_8021:
			case IUPOSConstant.MESSAGETYPE_INT_8015:
			case IUPOSConstant.MESSAGETYPE_INT_8025:
			case IUPOSConstant.MESSAGETYPE_INT_8031:
			case IUPOSConstant.MESSAGETYPE_INT_8041: 
			case IUPOSConstant.MESSAGETYPE_INT_8045:  //페이결제취소 요청
			case IUPOSConstant.MESSAGETYPE_INT_8047:  //페이결제보너스취소  요청				

			case IUPOSConstant.MESSAGETYPE_INT_8241:  //모바일플랫폼 결제 요청
			case IUPOSConstant.MESSAGETYPE_INT_8243:  //현대CarPay 결제 요청
			case IUPOSConstant.MESSAGETYPE_INT_8245:  //바로세차 승인취소요청
			{
				LogUtility.getPumpMLogger().debug(
						"[Pump M] messageTypeInt=" + messageTypeInt	+ " : Set isCampaign=false");
				return false;
			}
			case IUPOSConstant.MESSAGETYPE_INT_4202:
			case IUPOSConstant.MESSAGETYPE_INT_4292: 
			case IUPOSConstant.MESSAGETYPE_INT_4302:{
				//2016.03.23, PI2, CWI, ODT에서 요청된 전문이며 CMSM으로 전달하기 위해서 캠페인 여부를 TRUE로 한다.
				if( uPosMsg.getDeviceType().equals(IUPOSConstant.DEVICE_TYPE_3S) || //SELF ODT
						uPosMsg.getDeviceType().equals(IUPOSConstant.DEVICE_TYPE_3O)){ //충전기 
					
						String bonusCardNo = uPosMsg.getBonRSCard_no();
						
						if (GlobalUtility.isNullOrEmptyString(bonusCardNo)) {
							LogUtility.getPumpMLogger().debug("[Pump M] BonusCard Field = null " + " : Set isCampaign=false");
							return false;
						} else {
							LogUtility.getPumpMLogger().debug("[Pump M util-songkis] ODT에서 요청된 전문이며 CMSM으로 전달하기 위해서 캠페인 여부를 TRUE로 한다");
							return true;
						}
				}else{
					String ledCode = uPosMsg.getLed_code();
					
					LogUtility.getPumpMLogger().debug("[Pump M] led-code= " + uPosMsg.getLed_code() + " : Set isCampaign=false");
	
					if (ledCode.equals("0") || ledCode.equals("5")
							|| ledCode.equals("6") || ledCode.equals("7")
							|| ledCode.equals("B") || ledCode.equals("C")
							|| ledCode.equals("D") || ledCode.equals("E")
							|| ledCode.equals("F") || ledCode.equals("J") 		// 2013.05.07 ksm ledCode = J, K 추가
							|| ledCode.equals("K") || ledCode.equals("Z")
							|| ledCode.equals("G")) {
						return false;	// 2016. 4. 14 현장 할인으로 인해서 수정. 현장할인은 CMS M 에서 해야 한다고 장윤기 차장이 초기 Logic 을 Setup 하였고,
										// 	정우철 차장은 그의 Rule 을 따르도록 함.	 좀 어처구니가 없음. 왜냐, 여기서 해도 되는데....
					} 
					
					break ; // 2017.11.14 as-is KixxHUB와 동일하게 처리 (아래 로직 관련 확인 필요)
					/*else {
						return true;	// 2016. 4. 14 현장 할인으로 인해서 수정. 현장할인은 CMS M 에서 해야 한다고 장윤기 차장이 초기 Logic 을 Setup 하였고,
						// 	정우철 차장은 그의 Rule 을 따르도록 함.	 좀 어처구니가 없음. 왜냐, 여기서 해도 되는데....
					}*/
				}
			}
		}

		// 주유 정보가 없는 경우 (Item info)
		UPOSMessage_ItemInfo itemInfo = uPosMsg.getItem_info();
		int itemInfoNo = Integer.parseInt(itemInfo.getRecordNo());
		if (itemInfoNo == 0) {
			LogUtility.getPumpMLogger().debug("[Pump M] No ItemInfo " + " : Set isCampaign=false");
			return false;
		}
		// 2015-01-16 twsongkis campaign을 수행하지 않기 위해 false로 변환
		// 추후 campign 수행시 true로 변경
		return true;
	}

	/**
	 * CAT 단말기로 주유요청에 대한 응답 전문을 전송할 경우, 이미 Locking 건 주유건에 대해서 해지를 해야 할지 여부를
	 * 판단한다. 가령 거래처 + 주유정보 요청일 경우, 우선 특정 주유건에 대해서 Locking 을 수행하지만, 거래처 로직 수행 이후
	 * 등록이 안되어 있거나. 다른 이유로 인해서 주유 결제를 진행하지 못하는 경우에는 해지를 해 주어야 한다.
	 * 
	 * @param uPosMsg :
	 *            주유 정보 요청에 의한 응답 전문
	 * @return : Locking 해지 여부
	 */
	public static boolean shouldUnLock(UPOSMessage uPosMsg) {
		boolean rlt = false;

		try {
			int messageTypeInt = Integer.parseInt(uPosMsg.getMessageType());
			String posReceipt_no = uPosMsg.getPosReceipt_no();

			if (posReceipt_no.startsWith("K")) {
				// 주유 정보가 없는 경우 (Item info)
				UPOSMessage_ItemInfo itemInfo = uPosMsg.getItem_info();
				int itemInfoNo = Integer.parseInt(itemInfo.getRecordNo());
				if (itemInfoNo == 0) {
					LogUtility.getPumpMLogger().debug("[Pump M] No ItemInfo : Process unlocking Logic. led-code=" + uPosMsg.getLed_code());
					
					rlt = true;
				} else {
					// LED Code
					switch (messageTypeInt) {
						case IUPOSConstant.MESSAGETYPE_INT_4202:
						case IUPOSConstant.MESSAGETYPE_INT_4292: 
						case IUPOSConstant.MESSAGETYPE_INT_4302: {
							String ledCode = uPosMsg.getLed_code();
							switch (ledCode) {
								case IUPOSConstant.LEDCODE_0 :
								case IUPOSConstant.LEDCODE_5 :
								case IUPOSConstant.LEDCODE_6 :
								case IUPOSConstant.LEDCODE_7 :
								case IUPOSConstant.LEDCODE_B :
								case IUPOSConstant.LEDCODE_C :
								case IUPOSConstant.LEDCODE_D :
								case IUPOSConstant.LEDCODE_E :
								case IUPOSConstant.LEDCODE_F :
								case IUPOSConstant.LEDCODE_G :
								case IUPOSConstant.LEDCODE_W :
								case IUPOSConstant.LEDCODE_Z :
									LogUtility.getPumpMLogger().debug("[Pump M] led-code=" + uPosMsg.getLed_code()	+ ": Process unlocking Logic");
									rlt = true;
									break ;
							}
						}
					}
				}
			} else {
				rlt = false;
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		}
		return rlt;
	}
}
