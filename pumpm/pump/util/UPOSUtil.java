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
	 * POS �� ���� ���� DY ������ UPOSMessage_ItemInfo Class �� �����Ѵ�.
	 * 
	 * @param dyPumpMsg :
	 *            POS �� ���� ���� DY ����
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
				String goodsCode = dyItem.getGoodsCode(); // ��ǰ �ڵ�
				String bonGoodsCode = dyItem.getBonGoodsCode();
				String oil_ind = dyItem.getOil_ind(); // ����/���ܱ���
				String unitPrice_before_discount = GlobalUtility.getStringValue(GlobalUtility.getMultipleWith1000(dyItem.getUnitPrice_before_discount())); // ������ �ܰ�
				String oilAmount = GlobalUtility.getStringValue(GlobalUtility.getMultipleWith1000(dyItem.getOilAmount())); // ����
				String unitPrice_after_discount = GlobalUtility.getStringValue(GlobalUtility.getMultipleWith1000(dyItem.getUnitPrice_after_discount())); // ������ �ܰ�
				String tax_ind = dyItem.getTax_ind(); // 01:���� , 02:�鼼
				String price_before_tax = GlobalUtility.getStringValue(dyItem.getPrice_before_tax()); // ���ް���
				String taxPrice = GlobalUtility.getStringValue(dyItem.getTaxPrice()); // ����
				String oilPrice_before_discount = GlobalUtility.getStringValue(dyItem.getOilPrice_before_discount()); // ������ �ݾ�
				String oilPrice_after_discount = GlobalUtility.getStringValue(dyItem.getOilPrice_after_discount()); // ������ �ݾ�
				String khTransactionID = dyPumpMsg.getDyInfoArray()[0].getKhTransactionID(); // ��ǥ��ȣ
				String rentlimit_proc_ind_overlimit = dyItem.getRentlimit_proc_ind_overlimit(); // �ܻ����Ÿ��
				String unitDiscount_ind = dyItem.getUnitDiscount_ind(); // ���� ����
				String keep_no = dyItem.getKeepissue_no(); // ��������ȣ(10b)
				String issue_type = dyItem.getIssue_type(); // ������ ��������(2b)

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
	 * ���� ���� ��ȣ�� ���� ������ȣ�� ��ȸ�Ͽ� UPOSMessage �� Spec �� �°� �����Ͽ� �����Ѵ�.
	 * 
	 * @param carInfoArray :
	 *            ���� ��ȣ ����Ʈ
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
	 * ���� ���� ��ȣ�� ���� ������ȣ�� ��ȸ�Ͽ� UPOSMessage �� Spec �� �°� �����Ͽ� �����Ѵ�.
	 * 
	 * @param carInfoArray :
	 *            ���� ��ȣ ����Ʈ
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
	 * ������ �� �Ҹ����� �ǸſϷ� ���� ���� ����, �ܻ�ŷ�ó ���� ������ ���� ��� ItemInfo ������ �籸���Ѵ�. �̴� POS
	 * �� �����ϱ� �����̴�.
	 * 
	 * @param nozzle_no :
	 *            ���� ��ȣ
	 * @param posReceipt_no :
	 *            KH ó����ȣ
	 * @param liter :
	 *            �Ǹŷ�
	 * @param salesBasePrice :
	 *            �Ǹ� �ܰ�
	 * @param pumpPrice :
	 *            ���� �ݾ�
	 * @param amt :
	 *            �Ǹ� �ݾ�
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
		uPosMsgItemInfo.setRentlimit_proc_ind_overlimit(ICode.PROC_IND_OVERLIMIT_00); // �ܻ� ����
		itemInfo = UPOSMessage_ItemInfo
				.createUPOSMessage_ItemInfo(uPosMsgItemInfo);
		return itemInfo;
	}

	/**
	 * UPOSMessage �� ���Ե� �ܰ��� Pump A �� Spec �� �°� �����Ͽ� �����Ѵ�.
	 * 
	 * @param uPosMsg :
	 *            UPOSMessage Class (���� ����)
	 * @return ���� ������ �ܰ� �ݾ�
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
	 * CAT �ܸ��� ������ ��� ���� ������ �����´�. (Default = 0) [2008.11.20] Default ���� ���῭
	 * �����, ������ �����, ������ �븮�� �����Ͽ� �����Ǿ���
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
	 * ��ǰ���̺��� Download Flag �� �����Ѵ�.
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
	 * ODT �κ��� ���� ��û�� �Ʒ� ������ ������, ��������(UPOSMessage_ItemInfo_Item) �� �����. 
	 * GSC Self ��� ���ε� �ܰ��ν� ���� ��û�� �ü� �ִ�. �̷� ���� ODT �� ���� ���� ���� �� �ܰ� , �ݾ����� Item
	 * InfoItem �� �����Ѵ�. �׸��� ������ �ܰ� �� ������ �ݾ��� �� ������ ���ΰ��� ���ؼ� ����Ѵ�.
	 * 
	 * @param nozzleNo :
	 *            �����ȣ
	 * @param khTransactionID :
	 *            ű����� ��ȣ         
	 * @param liter :
	 *            ����
	 * @param baseprice :
	 *            �ܰ�
	 * @param price :
	 *            �ݾ�        
	 * @param fixedQty_yn :
	 *            ��������         
	 * @return
	 */
	public static UPOSMessage_ItemInfo_Item getGSCUPOSMessage_ItemInfo_Item(
			String nozzleNo, String khTransactionID, String liter,
			String baseprice, String price, String mobilePayYn) {
		
		/** ������ �񱳸� ���� ���� ���� */
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
		
		 // �ݾ� + ���� ����
		if ((getPrice != null && getPrice != 0) && (decimalLiter != null && decimalLiter.compareTo(zero)!=0)) {
			
			//LogUtility.getLogger().info("[Pump M] �ݾ�+���� ���� pumpTrData �߰� ó�� ") ;
			
			if (pumpTrData != null) {
				pumpTrData.setOil_preset_ind("1");		// ���������� ���� 0:����, 1:����
				pumpTrData.setPreset_qty_prc_ind("2");  // ������������ 0:����, 1:����, 2:����+����
				pumpTrData.setPreset_prc(price); // �ݾ�
			
				// �ŷ�ó�� ��� �Ѿ�� baseprice�� ����ϸ�, �ŷ�ó�� �ƴ� ��� pumpTrData�� baseprice�� ���
				if(baseprice != null || !baseprice.equals("0") || !baseprice.equals("")){
					Double nbefore = Double.parseDouble(baseprice);
					BigDecimal before = new BigDecimal(Integer.parseInt(String.valueOf(Math.round(nbefore))));
					before.setScale(0, RoundingMode.HALF_UP);
					LogUtility.getLogger().info("[Pump M] before="+before.toString()) ;
					pumpTrData.setBaseprice(before.toString()); // �ܰ�
					pumpTrData.setPreset_baseprice(before.toString()); // ���������ܰ� �Ҽ���3
				}else{
					pumpTrData.setPreset_baseprice(pumpTrData.getBaseprice()); // ���������ܰ� �Ҽ���3
				}
				// ����
				BigDecimal pump_qty = new BigDecimal(liter).divide(new BigDecimal(1000));
				pumpTrData.setPreset_qty(pump_qty.toString());
			}
			
		}
		// �ݾ� ���� å��
		else if (getPrice != null && getPrice != 0) {
//			 LogUtility.getLogger().info("[Pump M] �ݾ� ���� pumpTrData �߰� ó�� ") ;
			
			if (pumpTrData != null) {
				pumpTrData.setOil_preset_ind("1");		// ���������� ���� 0:����, 1:����
				pumpTrData.setPreset_qty_prc_ind("0");  // ������������ 0:����, 1:����, 2:����+����
				pumpTrData.setPreset_prc(price); // ����
				
				// �ŷ�ó�� ��� �Ѿ�� baseprice�� ����ϸ�, �ŷ�ó�� �ƴ� ��� pumpTrData�� baseprice�� ���
				if(baseprice != null || !baseprice.equals("0") || !baseprice.equals("")){
					Double nbefore = Double.parseDouble(baseprice);
					BigDecimal before = new BigDecimal(Integer.parseInt(String.valueOf(Math.round(nbefore))));
					before.setScale(0, RoundingMode.HALF_UP);
					LogUtility.getLogger().info("[Pump M] before="+before.toString()) ;
					pumpTrData.setBaseprice(before.toString()); // �ܰ�
					pumpTrData.setPreset_baseprice(before.toString()); // ���������ܰ� �Ҽ���3
				}else{
					pumpTrData.setPreset_baseprice(pumpTrData.getBaseprice()); // ���������ܰ� �Ҽ���3
				}
			}
			
		// ���� ���� ���	
		} else if (decimalLiter != null && decimalLiter.compareTo(zero)!=0) {	
//			 LogUtility.getLogger().info("[Pump M] ���� ���� pumpTrData �߰� ó�� ") ;
			
			if (pumpTrData != null) {
				pumpTrData.setOil_preset_ind("1");		// ���������� ���� 0:����, 1:����
				pumpTrData.setPreset_qty_prc_ind("1");  // ������������ 0:����, 1:����, 2:����+����
				
				// �ܰ�
				// �ŷ�ó�� ��� �Ѿ�� baseprice�� ����ϸ�, �ŷ�ó�� �ƴ� ��� pumpTrData�� baseprice�� ���
				if(baseprice != null || !baseprice.equals("0") || !baseprice.equals("")){
					Double nbefore = Double.parseDouble(baseprice);
					BigDecimal before = new BigDecimal(Integer.parseInt(String.valueOf(Math.round(nbefore))));
					before.setScale(0, RoundingMode.HALF_UP);
					LogUtility.getLogger().info("[Pump M] before="+before.toString()) ;
					pumpTrData.setBaseprice(before.toString()); // �ܰ�
					pumpTrData.setPreset_baseprice(before.toString()); // ���������ܰ� �Ҽ���3
				}else{
					pumpTrData.setPreset_baseprice(pumpTrData.getBaseprice()); // ���������ܰ� �Ҽ���3
				}
				
				// ����
				BigDecimal Baseprice = new BigDecimal(baseprice);
				BigDecimal pump_qty = new BigDecimal(liter).divide(new BigDecimal(1000));
				pumpTrData.setPreset_qty(pump_qty.toString());
				
				// ������
				BigDecimal fixedQty = Baseprice.multiply(pump_qty);
				pumpTrData.setPreset_prc(fixedQty.toString());
				
				if(pumpTrData.getPreset_qty() == null){
					pumpTrData.setPreset_qty("0");
				}
			}
		} else if ((getPrice != null && getPrice == 0) && (decimalLiter != null && decimalLiter.compareTo(zero) ==0)) {
			if (pumpTrData != null) {
				pumpTrData.setOil_preset_ind("0");		// ���������� ���� 0:����, 1:����
				pumpTrData.setPreset_qty("0");		
				pumpTrData.setPreset_prc("0");		
			}
		}

		if (pumpTrData == null)
			return null;

		return getUPOSMessage_OdtItemInfo_Item(pumpTrData, nozzleNo, mobilePayYn);
	
	}

	/**
	 * UPOSMessage �� ���ԵǾ� �ִ� �������� ����Ѵ�. �׸��� �̸� Pump A �� Spec �� �����ϵ��� �����Ѵ�.
	 * 
	 * @param uPosMsg :
	 *            UPOSMessage Class (���� ����)
	 * @return ���� ������ Liter ��
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
	 * ���� ���ϸ��� ���� ���� ������ �����´� (Default = 0) [2008.11.20] Default ���� ���῭ �����, ������
	 * �����, ������ �븮�� �����Ͽ� �����Ǿ���
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
	 * POS IP �� POS_INFO Table �� ���� ���� �´�.
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
	 * POS Port �� POS_INFO Table �� ���� ���� �´�.
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
	 * POS �� ���� ���ڸ� ��û�Ѵ�. (YYYYMMDD)
	 * 
	 * @return POS ���� ���� (YYYYMMDD)
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
	 * UPOSMessage �� ���Ե� �����ݾ��� Pump A �� Spec �� �°� �����Ͽ� �����Ѵ�.
	 * 
	 * @param uPosMsg :
	 *            UPOSMessage Class (���� ����)
	 * @return ���� ������ �ݾ�
	 */
	public static String getPriceForPumpA(UPOSMessage uPosMsg) {
		return PumpMUtil.convertPriceFromUPOSToPumpA(uPosMsg
				.getPayment_amt());
	}
	
	/** PI2, CWI, 2016-03-23  GSC Self�� �µ��� �߰� */
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
				
				if ((pumpTrData != null) && (ICode.OIL_PAID_IND_0.equals(pumpTrData.getOil_paid_ind()))   //  �������� 0:���� 1:����
						&& (ICode.LOCKING_IND_0.equals(pumpTrData.getLocking_ind())) //Locking ���� 0:���� 1:Locking
						&& (ICode.OIL_COMPLETED_IND_0.equals(pumpTrData.getOil_completed_ind()))) { //�Ϸ����� ���� 0:���� , 1:����
					if (isPreset) {
						if (!ICode.OIL_PRESET_IND_1.equals(pumpTrData.getOil_preset_ind())) { //���������� ���� 0:���� , 1:����
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
	 * TR ������ ��û�Ѵ�.
	 * 
	 * @param nozzleNo :
	 *            ���� ��ȣ
	 * @param completed :
	 *            ���� �� / �Ϸ� ����
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
	 * ��ǰ�ٿ�ε� ��û�� �޾Ƽ� ��ǰ���̺�κ��� ��ǰ������ �����Ѵ�.
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
			// ����/���� ��ǰ ������ �����մϴ�
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

			// ���� ������ �����մϴ�.
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
	 * ��ǰ�ٿ�ε� ��û�� �޾Ƽ� �������ڵ������� �����Ѵ�.
	 * @param catNo CATNO�� Ʋ���� ������ ����Ÿ�� �����ϴ�.
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
			
			//�������ڵ� ������ �����Ѵ�.
			String disCountRate = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_7110);
			if (GlobalUtility.isNullOrEmptyString(disCountRate)) {
				disCountRate = "0";
				LogUtility.getCATMLogger().debug("�������������� ������ " + disCountRate+ " % ����");
			}
		
			String barcode_Yn = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0440);
			if (GlobalUtility.isNullOrEmptyString(barcode_Yn)) {
				barcode_Yn = "n";
				LogUtility.getCATMLogger().debug("�������ڵ� ���࿩�� ������" + barcode_Yn+ "  ����");
			}
			
			String barcode_type = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0441);
			if (GlobalUtility.isNullOrEmptyString(barcode_type)) {
				barcode_type = "0";
				LogUtility.getCATMLogger().debug("�������ڵ� Type���� ������" + barcode_type+ "  ����");
			}
			
			String hdCarpay_Yn = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_7113);	//���հ�����뱸�� (������ⰳ�� ������Ʈ)
			if (GlobalUtility.isNullOrEmptyString(hdCarpay_Yn)) {
				hdCarpay_Yn = "0";
				LogUtility.getCATMLogger().debug("���հ�����뱸�� :" + hdCarpay_Yn+ "  ����");
			}
			
			String cashReceiptYn = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_7115); //���ݿ����� �ǹ����� ��뿩��(0:������, 1:���)
			if (GlobalUtility.isNullOrEmptyString(cashReceiptYn)) {
				cashReceiptYn = "0";
				LogUtility.getCATMLogger().debug("���ݿ����� �ǹ����� ��뿩�� ������ :" + cashReceiptYn+ "  ����");
			}

			String mobileWashYn = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_7117); //�ٷμ��� ��뿩��(0:������, 1:���)
			if (GlobalUtility.isNullOrEmptyString(mobileWashYn)) {
				mobileWashYn = "0";
				LogUtility.getCATMLogger().debug("�ٷμ��� ��뿩�� ������ :" + mobileWashYn+ "  ����");
			}
			
			// 2020.03.04 �����ڵ� "0440" ���ڸ��ϰ�� �� ���ڸ��� ������. - YHM
			if(barcode_Yn.length() == 2) {
				barcode_Yn = barcode_Yn.substring(1);
			}

			// 2019.11.20 �����ڵ� "0441" ���ڸ��ϰ�� �� ���ڸ��� ������. - SoonKwan
			if(barcode_type.length() == 2) {
				barcode_type = barcode_type.substring(1);
			}
			
			uPosBarcodeInfo.setCatNo(catNo);
			barcode_info_set.setDisCountRate(disCountRate); //��������������
			barcode_info_set.setBarcodeYn(barcode_Yn);      //���ڵ� ���࿩�� (0:�̹��� 1:����)
			barcode_info_set.setBarcodeType(barcode_type);  // ���ڵ�Ÿ��(1:�������ڵ� 2:�������ڵ�)
			barcode_info_set.setHdCarpayYn(hdCarpay_Yn);    // ���հ�����뱸��(0:������, 1:��ü���,2:�ٷ��������,3:����CarPay���)
			barcode_info_set.setMobileWashYn(mobileWashYn); // �ٷμ��� ��뿩��(0:��� ����, 1:���)
			barcode_info_set.setCashReceiptYn(cashReceiptYn); // ���ݿ����� �ǹ����� ��뿩��(0:��� ����, 1:���)
			
			String expdate = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0326); // ��ȿ�Ⱓ�� ��ĥ ���� (���� 30�� ����)
			if (GlobalUtility.isNullOrEmptyString(expdate)) {
				expdate = "90" ; // ���� ���� ��� ����
				LogUtility.getCATMLogger().debug("Barcode ��ȿ�Ⱓ�� CodeMaster �� ��� " + expdate+ " �Ϸ� ����");
			}
			//
			LogUtility.getCATMLogger().debug("expdate =" + expdate);
			barcode_info_set.setCarwashDate(expdate); //������ȿ�Ⱓ
			
			uPosBarcodeInfo.setBarcodeInfoset(barcode_info_set);
			
			// ���δܰ��� �����Ѵ�.
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
				LogUtility.getCATMLogger().error("�������ڵ� �ܰ� ����:",e.getMessage(), e);
			}
		}catch(Exception e) {
			LogUtility.getCATMLogger().error("�������� ������ : "+e.getMessage(), e);
		}finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}
		
		LogUtility.getCATMLogger().debug("uPosBarcodeInfo =" + uPosBarcodeInfo.getContents());		
		return uPosBarcodeInfo;
	}
	/**
	 * ODT �κ��� ���� ��û�� �Ʒ� ������ ������, ��������(UPOSMessage_ItemInfo) �� �����. �Ҹ��� �� ��������
	 * ��� ���ε� �ܰ��ν� ���� ��û�� �ü� �ִ�. �̷� ���� ODT �� ���� ���� ���� �� �ܰ� , �ݾ����� Item Info ��
	 * �����Ѵ�. �׸��� ������ �ܰ� �� ������ �ݾ��� �� ������ ���ΰ��� ���ؼ� ����Ѵ�.
	 * 
	 * @param nozzleNo :
	 *            �����ȣ
	 * @param reqLiter :
	 *            ����
	 * @param reqBasePrice :
	 *            �ܰ�
	 * @param reqPrice :
	 *            �ݾ�
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
	 * ���� ���� ��û�� ���� ����
	 * 
	 * @param nozzleNo
	 *            ���� ID
	 * @param completed
	 *            true : �����Ϸ��Ͽ����� ���������ʴ� �����ǿ� ���� ���� ���� ��û 
	 *            false : �������� ���� �ֽ� �����ǿ� ���� �������� ��û
	 * 
	 * [2008.11.12] �߰� ��� ���� by ���῭ ����� ������ ���� ��û�� �������� �ǿ� ���ؼ��� ������ �����Ѵ�. ����������
	 * ���� ��� �������� ���ٰ� �������� �Ѵ�.
	 * 
	 * @param price :
	 *            CAT ���� ���� �Է� ���� �ݾ�
	 * @param liter :
	 *            CAT ���� ���� �Է� ���� ����
	 * @return
	 */
	public static UPOSMessage_ItemInfo_Item getUPOSMessage_ItemInfo_Item(String nozzleNo, boolean completed, String price, String liter) {
		boolean isCatPreset = false;

		T_KH_PUMP_TRData pumpTrData = null;
		
		// ���� �Ϸᰡ ���� �ʾҰ�, CAT �ܸ���� ���� ���� �ݾ��� ���� ��� - ������ �����Ⱑ Preset ������ KIXXHUB �� ������ ���� ���
		if (!completed && !price.equals("") && !price.equals("0")) {
			/**
			 * CAT �ܸ���� ���� ���� �ݾװ� ���͸� �Է� ���� ���. �� �������� Preset �� ���� �������̶� �ϴ���
			 * CAT �ܸ���� ���� �ݾ� �Է��� �޾��� ��� ���� �Է°����� ���������� �����Ѵ�. �� CAT �ܸ��� �Է� ����
			 * Preset �ڷẸ�� �켱�� �Ѵ�.
			 * 
			 */
			LogUtility.getPumpMLogger().info(
					"[Pump M] Receive payment and liter from CAT Device. nozzleNo="
							+ nozzleNo + "#price=" + price + "#liter=" + liter);
			try {
				// �̹� ���� ������ �� ���̱� ������ ���ο� KH ó����ȣ�� �����ϰ�, �� KH ó����ȣ�� �״�� ���
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
				pumpTrData.setOil_preset_ind(ICode.OIL_PRESET_IND_1);   //���������� ���� 0:���� , 1:����
				pumpTrData.setPreset_qty_prc_ind(ICode.PRESET_QTY_PRC_IND_0_PRICE); //������������	 0:���� , 1=����
				pumpTrData.setPreset_prc(price); //����
				pumpTrData.setPreset_baseprice(pumpTrData.getBaseprice()); //���������ܰ� �Ҽ���3
				isCatPreset = true;
			}
		} else {
			pumpTrData = getT_KH_PUMP_TRData(nozzleNo, completed);
		}

		/**
		 * [2008.11.12] �߰� ��� ���� by ���῭ ����� ������ ���� ��û�� �������� �ǿ� ���ؼ��� ������ �����Ѵ�.
		 * ���������� ���� ��� �������� ���ٰ� �������� �Ѵ�.
		 */
		if ((!completed) && (pumpTrData != null)) {
			boolean isPumping = PumpMTransactionManager.getInstance().hasState(nozzleNo, IPumpConstant.KH_PUMPING);
			
			if (!isPumping) {
				LogUtility.getPumpMLogger().warn("[Pump M] isPumping is false, so pumping Info is null");
				pumpTrData = null;
			}
		}
		// CAT�ܸ��⿡�� �����Ϸ�� ������û��, ����Ϻ��հ��� ����������� ��� ����ó���� (���ΰ�, ���δܰ��� ���� �̽� �߻�)
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
				LogUtility.getPumpMLogger().warn("�ٷ����� ��������� Ȯ�� - ����������û ����ó���� ");
				return null;
			}
		}
		
		if (pumpTrData == null)
			return null;

		return getUPOSMessage_ItemInfo_Item(pumpTrData, isCatPreset);
	}	
	
	/**
	 * PI2, ������ , 2015-01-15
	 * ������ ODT - ķ���� ���� ���� ��û�� ���� ����
	 * 
	 * @param nozzleNo
	 *            ���� ID
	 * @param price :
	 *            ODT ���� ���� �Է� ���� �ݾ�
	 * @param liter :
	 *            ODT ���� ���� �Է� ���� ����
	 * @return
	 */
	public static UPOSMessage_ItemInfo_Item getUPOSMessage_ItemInfo_Item(String nozzleNo, String price, String liter) {
		boolean isOdtPreset = false;

		T_KH_PUMP_TRData pumpTrData = null;
		
		if (!price.equals("") && !price.equals("0")) {
			/**
			 * ODT�� ���� �ݾ� �Է��� �޾��� ��� ���� �Է°����� ���������� �����Ѵ�. 
			 */
			LogUtility.getLogger().info(
					"[Pump M] Receive payment and liter from ODT Device. nozzleNo="
							+ nozzleNo + ": price=" + price + ": liter=" + liter);
			try {
				String khProcNo = PumpMTransactionManager.getInstance().getKHTransactionIDWithoutCreation(nozzleNo);
				
				if (khProcNo != null) {
					pumpTrData = getPumpingInfo(nozzleNo);  // �ش� ������ ���õ�  �������� ������
				}
			} catch (Exception e) {
				LogUtility.getLogger().error(e.getMessage(), e);
			}

			if (pumpTrData != null) {
				pumpTrData.setOil_preset_ind("0");		   // ���������� ���� (0:����)
				pumpTrData.setEqpm_amt_prc(price);         // �������ݾ�
				pumpTrData.setEqpm_qty(liter);             // ��������
				pumpTrData.setOil_completed_ind("1");      // �����Ϸ��������� (1: �Ϸ�)
			}
		}
		return getUPOSMessage_ItemInfo_Item(pumpTrData, isOdtPreset);
	}
	
	
	/**
	 * ODT �κ��� ���� ��û�� �Ʒ� ������ ������, ��������(UPOSMessage_ItemInfo_Item) �� �����. �Ҹ��� ��
	 * �������� ��� ���ε� �ܰ��ν� ���� ��û�� �ü� �ִ�. �̷� ���� ODT �� ���� ���� ���� �� �ܰ� , �ݾ����� Item
	 * InfoItem �� �����Ѵ�. �׸��� ������ �ܰ� �� ������ �ݾ��� �� ������ ���ΰ��� ���ؼ� ����Ѵ�.
	 * 
	 * @param nozzleNo :
	 *            �����ȣ
	 * @param reqLiter :
	 *            ����
	 * @param reqBasePrice :
	 *            �ܰ�
	 * @param reqPrice :
	 *            �ݾ�
	 * @return
	 */
	public static UPOSMessage_ItemInfo_Item getUPOSMessage_ItemInfo_Item(
			String nozzleNo, String khTransactionID, String reqLiter,
			String reqBasePrice, String reqPrice) {
		UPOSMessage_ItemInfo_Item uPosMsgItemInfo = null;
		SqlSession session = null;

		String goodsCode = ""; // ��ǰ �ڵ�
		String bonGoodsCode = "";
		String oil_ind = ICode.OIL_IND_11; // ��ǰ ����
		String unitPrice_before_discount = "0"; // ������ �ܰ�
		String oilAmount = "0"; // ����
		String unitPrice_after_discount = "0"; // ������ �ܰ�
		String tax_ind = ICode.TAXFREE_CD_01; // 01:���� , 02:�鼼
		String price_before_tax = "0"; // ������ �ܰ�
		String taxPrice = "0"; // ����
		String oilPrice_before_discount = "0"; // ������ �ݾ�
		String oilPrice_after_discount = "0"; // ������ �ݾ�
		String rentlimit_proc_ind_overlimit = ICode.PROC_IND_OVERLIMIT_99; // �ܻ����Ÿ��
		String unitDiscount_ind = "0"; // ���� ���� -> ���� ���δ� Item Info �� �����ϴ�
		// CreateUPOSMessage.createUPOSMessage_ItemInfo_Item �Լ� ������ �����ȴ�.
		String keep_no = ""; // ��������ȣ(10b)
		String issue_type = "01"; // ������ ��������(2b)

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
	 * TR ������ �̿��Ͽ� UPOSMessage �� Item Info ������ �����Ѵ�. ���� catPreset = true �� ����
	 * TR ������ �� ������ �����ݾװ� ���������� ���� ���� �ƴ� CAT ���κ����� �Է� �ݾ����� ���� �����Ǿ��� ���̴�.
	 * 
	 * @param pumpTrData :
	 *            TR ����
	 * @param catPreset :
	 *            CAT ���� ������ �ݾ� �Է� ����
	 * @return
	 */
	public static UPOSMessage_ItemInfo_Item getUPOSMessage_ItemInfo_Item(
			T_KH_PUMP_TRData pumpTrData, boolean catPreset) {
		UPOSMessage_ItemInfo_Item uPosMsgItemInfo = null;
		SqlSession session = null;

		String goodsCode = null; // ��ǰ �ڵ�
		String bonGoodsCode = null; // ���ʽ� ��ǰ �ڵ�
		String oil_ind = null; // ��ǰ ����
		String unitPrice_before_discount = null; // ������ �ܰ�
		String oilAmount = null; // ����
		String unitPrice_after_discount = null; // ������ �ܰ�
		String tax_ind = ICode.TAXFREE_CD_01; // 01:���� , 02:�鼼
		String price_before_tax = null; // ���ް���
		String taxPrice = null; // ����
		String oilPrice_before_discount = null; // ������ �ݾ�
		String oilPrice_after_discount = null; // ������ �ݾ�
		String khTransactionID = null; // ��ǥ��ȣ
		String rentlimit_proc_ind_overlimit = ICode.PROC_IND_OVERLIMIT_99; // �ܻ����Ÿ��
		String unitDiscount_ind = ICode.DY_UNITDISCOUNT_IND_0 ; // ���� ����
		String keep_no = ""; // ��������ȣ(10b)
		String issue_type = ""; // ������ ��������(2b)

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
					// �׳� ������ ���
					preset_basePriceInt = basePriceInt;
				} else {
					// ����/���� �� ���
					preset_basePriceInt = Double.parseDouble(pumpTrData.getPreset_baseprice());
				}

				if ((pumpTrData.getOil_completed_ind().equals(ICode.OIL_COMPLETED_IND_1)) && !catPreset) {
					LogUtility.getPumpMLogger().debug("[Pump M] �����Ϸ�� ���̱� ������ �Ϸ� �����͸� ���ؼ� ���� ������ ����ϴ�.");
					
					priceInt = Double.parseDouble(pumpTrData.getEqpm_amt_prc());
					literInt = Double.parseDouble(pumpTrData.getEqpm_qty());
				} else {
					if (pumpTrData.getPreset_qty_prc_ind().equals(ICode.PRESET_QTY_PRC_IND_0_PRICE)) {
						LogUtility.getPumpMLogger().debug("[Pump M] �����Ϸ���� �ʰ� ���� �����̱� ������  ���� ������ ���� ������ ����ϴ�.");
						
						priceInt = Double.parseDouble(pumpTrData.getPreset_prc());
						literInt = GlobalUtility.getValueByCertainDecimal(PumpMUtil.getPresetLiter(priceInt, preset_basePriceInt), 3);
					} else if (pumpTrData.getPreset_qty_prc_ind().equals(ICode.PRESET_QTY_PRC_IND_1_LITER)) {
						LogUtility.getPumpMLogger().debug("[Pump M] �����Ϸ���� �ʰ� ���� �����̱� ������  ���� ������ ���� ������ ����ϴ�.");
						
						literInt = Double.parseDouble(pumpTrData.getPreset_qty());
						priceInt = GlobalUtility.multiple(literInt, preset_basePriceInt);
					}
				}

				unitPrice_before_discount = GlobalUtility.getMultipleWith1000(basePriceInt);
				oilAmount = GlobalUtility.getMultipleWith1000(literInt);

				// T_KH_PUMP_TR ���̺� �ܰ��� 0���� ���� ��찡 �߻��Ͽ� 0���� �� �ִ� ��� �Ϲ� �ܰ���
				// ���
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
	 * ���� ���� ��û�� ���� ���� ������ �����Ѵ�.
	 * 
	 * @param uPosMsg :
	 *            �������� ��û ����
	 * @param messageType :
	 *            ��û ������ Message Type
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
		
		// PI2, 2015-12-30, cwi, ODT���� �ö�� ��û�� ��� �����Ϸ� �Ŀ��� ODT�� ���� �������� ó�� �ϱ⿡
		// odt�� ��û ������ ������ false�� ó�� �Ѵ�.
		// odt���� �ö�� 4201�ϰ�� Kixx Hub ó���� ���� getUPOSMessage_ItemInfo_ItemFromODT �޼ҵ� ���
		// PI2, 2016-07-15, cwi, SelfOdt�� ��� ������ ������ ķ����������û ������ 4201 �������� ���� �Ѵ�.
		if(uPosMsg.getMessageType().equals("4201") && uPosMsg.getDeviceType().equals("3S")){
			String mobilePayYn = "";
			
			// �ŷ�ó ��û�� ��� ���� �ŷ�ó ��ȸ ������ ������ ItemInfo_Item �����Ѵ�.
			if(!uPosMsg.getCustCard_No().equals("")){
			
				// ������ ���� dwPumpM �����͸� ���� �´�.
				POSHeader posObj = PumpMODTSaleManager.getCustPOSPumpM(uPosMsg.getNozzle_no());
				POS_DW dwPumpM = null;
				if ((posObj != null) && (posObj instanceof POS_DW)) dwPumpM = (POS_DW) posObj;
				
				basePrice = GlobalUtility.getDividedWith1000(dwPumpM.getBasePrice());
			}
			
			boolean pump_completed = PumpMTransactionManager.getInstance().isSameState(uPosMsg.getNozzle_no(), IPumpConstant.KH_PUMP_COMPLETED);
			
			// ���� �Ϸ� �� KH ��ȣ ��������
			if(pump_completed){
				khTransactionID = PumpMTransactionManager.getInstance().getKHTransactionID(uPosMsg.getNozzle_no(),IPumpConstant.KH_PUMP_COMPLETED);
				LogUtility.getLogger().info("[Pump M] upos4201 Pump_Completed khTransactionID=" + khTransactionID);
			}else{
				// ���� �� KH ��ȣ ��������
				khTransactionID = PumpMTransactionManager.getInstance().getKHTransactionID(uPosMsg.getNozzle_no(), IPumpConstant.KH_ODT_PAID_REQ);
				LogUtility.getLogger().info("[Pump M] upos4201 Pump_Not_Completed khTransactionID=" + khTransactionID);
			}
			
			mobilePayYn = uPosMsg.getMobilePay_yn();
			itemInfoItem = getGSCUPOSMessage_ItemInfo_Item(uPosMsg.getNozzle_no(),
					khTransactionID,
					uPosMsg.getPump_qty(),    // ��������pump_qty
					basePrice, 				  // �ŷ�ó �ܰ�
					uPosMsg.getPump_amt(),
					mobilePayYn
					);   // �����ݾ�pump_amt
			
			sendUPOSMsg = UPOSUtil.process4201_4291(uPosMsg, itemInfoItem, false);
		
		/**
		 * ������Ʈ : PI2
		 * ���泻�� : �ű�(ķ���� �������� ��û- 4291)
		 * �������� : 2015.12.16
		 * ������ : ������  
		 * ������ ODT�� ���, �����Ϸ� �� Ȯ���� �ݾ�, �������� 4291������ ����
		 */
		} else if((uPosMsg.getMessageType().equals(IUPOSConstant.MESSAGETYPE_4291)) && (uPosMsg.getDeviceType().equals(IUPOSConstant.DEVICE_TYPE_3O))){
			if(!uPosMsg.getCustCard_No().equals("")){
				
				String khProcNo = PumpMTransactionManager.getInstance().getKHTransactionIDWithoutCreation(uPosMsg.getNozzle_no());  // �ش� �����ǰ� ���õ� KixxHub ó����ȣ�� ������ 
				LogUtility.getLogger().info("[Pump M] khProcNo="+khProcNo) ;
				
				POSHeader posMsg = PumpMODTSaleManager.getCustPOSPumpM(uPosMsg.getNozzle_no());  // �ش� �����ǰ� ���õ� �ŷ�ó��ȸ ������ ������
				POS_DW dwPumpM = null;

				if ((posMsg != null) && (posMsg instanceof POS_DW)) {
					dwPumpM = (POS_DW) posMsg;
					
					basePrice = dwPumpM.getBasePrice();
					
				}
				
				if(basePrice == null || basePrice.equals("")) basePrice = PumpMUtil.getBasePriceFromNozzleNo(uPosMsg.getNozzle_no());  //  ������ �ܰ��� ���ؿ�
				
				itemInfoItem = getUPOSMessage_ItemInfo_Item( uPosMsg.getNozzle_no(),           // �����ȣ 
						khProcNo,                                                              // POS ��ǥ��ȣ(KixxHubó����ȣ) 
						PumpMUtil.convertTotalLiterFromPumpTOPOS( uPosMsg.getPump_qty()),   // ����
						basePrice,      		                                               // �ܰ�
						uPosMsg.getPump_amt());                                                // �ݾ� 
			} else {
				itemInfoItem = UPOSUtil.getUPOSMessage_ItemInfo_Item( uPosMsg.getNozzle_no(),  // �����ȣ
						uPosMsg.getPump_amt(),                                                 // �ݾ�
						PumpMUtil.convertTotalLiterFromPumpTOPOS( uPosMsg.getPump_qty()));  // ���� 
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
	 * �ű�, ������ , 2016.03.29
	 * ������ ODT - ķ���� ���� ���� ��û �� ��ǰ���� ����
	 * 
	 * @param nozzleNo
	 *            ���� ID
	 * @param price :
	 *            ODT ���� ���� �Է� ���� �ݾ�
	 * @param liter :
	 *            ODT ���� ���� �Է� ���� ����
	 * @return
	 */
	private static UPOSMessage_ItemInfo_Item getUPOSMessage_ItemInfo_Item_Gas(String nozzleNo, String price, String liter) {
		boolean isOdtPreset = false;

		T_KH_PUMP_TRData pumpTrData = null;
		
		if (!price.equals("") && !price.equals("0")) {
			/**
			 * ODT�� ���� �ݾ� �Է��� �޾��� ��� ���� �Է°����� ���������� �����Ѵ�. 
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
				pumpTrData.setOil_preset_ind("0");		                   // ���������� ���� 0:����, 1:����
				pumpTrData.setEqpm_amt_prc(price);
				pumpTrData.setEqpm_qty(liter);
				pumpTrData.setOil_completed_ind("1");
			}
		}
		return getUPOSMessage_ItemInfo_Item(pumpTrData, isOdtPreset);
	}

	/** �Ѿ�� �����͸� ������� ItemInfo ���� - 2016.03.10, cwi */
	public static UPOSMessage_ItemInfo_Item getUPOSMessage_OdtItemInfo_Item(
			T_KH_PUMP_TRData pumpTrData, String nozzleNo, String mobilePayYn) {
		UPOSMessage_ItemInfo_Item uPosMsgItemInfo = null;
		SqlSession session = null;

		String goodsCode = null; // ��ǰ �ڵ�
		String bonGoodsCode = null; // ���ʽ� ��ǰ �ڵ�
		String oil_ind = null; // ��ǰ ����
		String unitPrice_before_discount = null; // ������ �ܰ�
		String oilAmount = null; // ����
		String unitPrice_after_discount = null; // ������ �ܰ�
		String tax_ind = "01"; // 01:���� , 02:�鼼
		String price_before_tax = null; // ���ް���
		String taxPrice = null; // ����
		String oilPrice_before_discount = null; // ������ �ݾ�
		String oilPrice_after_discount = null; // ������ �ݾ�
		String khTransactionID = null; // ��ǥ��ȣ
		String rentlimit_proc_ind_overlimit = "99"; // �ܻ����Ÿ��
		String unitDiscount_ind = "0"; // ���� ����
		String keep_no = ""; // ��������ȣ(10b)
		String issue_type = ""; // ������ ��������(2b)

		try {
			session = SqlSessionFactoryManager.openSqlSession();

			double basePriceInt = 0;
			double preset_basePriceInt = 0;
			double literInt = 0;
			double priceInt = 0;
			double beforePriceInt = 0;
			double nozzlePriceInt = 0;
			
			// ������ �ܰ��� ���ؿ�
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
				basePriceInt = Double.parseDouble(pumpTrData.getBaseprice()); // ���� �� �ܰ�
				nozzlePriceInt = Double.parseDouble(basePrice); // ������ �ܰ�
				
				LogUtility.getLogger().info("[������� ������Ʈ] getUPOSMessage_OdtItemInfo_Item : "+pumpTrData.getOil_preset_ind());
				
				if (pumpTrData.getOil_preset_ind().equals("0")) {
					// �׳� ������ ���
					preset_basePriceInt = basePriceInt;
					
					literInt = Double.parseDouble(pumpTrData.getPreset_qty());
					oilAmount = GlobalUtility.getMultipleWith1000(literInt); // ������
					
					priceInt = Double.parseDouble(pumpTrData.getPreset_prc()); // �� ���� �ݾ�
					oilPrice_before_discount = GlobalUtility.getStringValue(priceInt); // ���� �� �ݾ�
					oilPrice_after_discount = GlobalUtility.getStringValue(priceInt); // ���� �� �ݾ�
					
					LogUtility.getLogger().info("[������� ������Ʈ] getUPOSMessage_OdtItemInfo_Item ������ : " + pumpTrData.getPreset_qty()+"/"+pumpTrData.getPreset_prc());
					LogUtility.getLogger().info("[������� ������Ʈ] getUPOSMessage_OdtItemInfo_Item ������ : " + GlobalUtility.getMultipleWith1000(literInt)+"/"+GlobalUtility.getStringValue(priceInt));
					
				} else {
					// ����/���� �� ���
					preset_basePriceInt = Double.parseDouble(pumpTrData.getPreset_baseprice());
				}
				
				// �ݾ� + ������ ���
				if(pumpTrData.getPreset_qty_prc_ind().equals("2")){
					LogUtility.getLogger().debug("[Pump M] ���� �Ϸ� �� ������ ���� ������ ����ϴ�.");
					
					LogUtility.getLogger().debug("��������������Ʈ MOBILEPAY YN: " + mobilePayYn + "/" + pumpTrData.getPreset_baseprice());
					
					if(mobilePayYn != null && IUPOSConstant.MOBILEPAY_A.equals(mobilePayYn)){
						priceInt = Double.parseDouble(pumpTrData.getPreset_prc());          // �� ���� �ݾ�
						literInt = Double.parseDouble(pumpTrData.getPreset_qty());          // �� ���� ����
						
						oilPrice_before_discount = GlobalUtility.getStringValue(priceInt);  // ���� �� �ݾ�
						oilPrice_after_discount = GlobalUtility.getStringValue(priceInt);	// ���� �� �ݾ�
						
						oilAmount = GlobalUtility.getMultipleWith1000(literInt);			// ������
						
						T_KH_PUMP_TRData trData = T_KH_PUMP_TRHandler.getHandler().getT_KH_PUMP_TRData(khTransactionID) ;
						
						basePriceInt = Double.parseDouble(trData.getPreset_baseprice());	// ���� �� �ܰ� (��������� preset_basePrice ���)
						basePrice = trData.getPreset_baseprice();				        	// ���� �� �ܰ� (��������� preset_basePrice ���)
						
					} else {
						priceInt = Double.parseDouble(pumpTrData.getPreset_prc()); // �� ���� �ݾ�
						literInt = Double.parseDouble(pumpTrData.getPreset_qty());
						beforePriceInt = GlobalUtility.multiple(literInt, nozzlePriceInt); // ���� X ������ ���� �ܰ� 
						
						BigDecimal before = new BigDecimal(Integer.parseInt(String.valueOf(Math.round(beforePriceInt))));
						before.setScale(0, RoundingMode.HALF_UP);
						
						// ���� �� �ܰ��� ���� �� �ܰ��� ������ ��� ���� �� �ݾ��� ������ �ݾ����� ����
						// ������� �̽��� ���� ���� �߻��� ����
						if(basePrice.equals(pumpTrData.getBaseprice())){
							LogUtility.getLogger().info("[Pump M] �ܰ� ���� ������ �ݾ� ����");
							oilPrice_before_discount = GlobalUtility.getStringValue(priceInt); // ���� �� �ݾ�
						}else{
							LogUtility.getLogger().info("[Pump M] �ܰ� �̵��� ������ �ݾ� �̼���");
							oilPrice_before_discount = GlobalUtility.getPositiveValue(before.toString()); // ���� �� �ݾ�
						}
						oilPrice_after_discount = GlobalUtility.getStringValue(priceInt);			 // ���� �� �ݾ�
						oilAmount = GlobalUtility.getMultipleWith1000(literInt);						 // ������
						
						// 2016-05-27 pi2_phase2�� ���� + �ſ� or ���� + ������ ��� ���� �Ϸ� �� �̸������� ���� 4201��û �� (�����ݾ� - ���� �ݾ�)�� �����
						// �ݾ� �� ���ͷ� ���õǾ� 4201�� ��û ��. ������ �ݾ��� �����ݾ׸�ŭ �����Ǿ� ���� �ǰ� ���ʹ� ��������ŭ
						// ������ �Ǳ⿡ �����ݾװ� ���Ͱ� ���̰� �� �� ���� �� ��� (�ݾ� / �ܰ�)�� ���Ϳ� ������ ������ ���� ���̳� ��� �ݾ��� �������� ���͸� �ٽ� ���Ѵ�. 
						//String	pi2_phase	=	PropertyManager.getSingleton().getProperty(PropertyManager.KH_DEPLOY_PHASE, PropertyManager.KH_DEPLOY_PHASE_DEFAULT);
						//if(pi2_phase.equals("2")){
						  
						  literInt = GlobalUtility.getValueByCertainDecimal(PumpMUtil.getPresetLiter(priceInt, preset_basePriceInt), 3); // �� �����ݾ� / �ܰ�
						  
						  String literStr = GlobalUtility.getMultipleWith1000(literInt);
						  BigDecimal liter = new BigDecimal(literStr);
					  	  BigDecimal compareLiter = new BigDecimal(oilAmount);
					  	  
					  	  LogUtility.getLogger().info("[Pump M] �������ݾ� / �ܰ��� ����  ���� ��="+liter);
						  LogUtility.getLogger().info("[Pump M] �����Ϸ� �� �Ѿ�� ���� ��="+compareLiter);
						  
					  	  // ������ ���Ϳ� (�ݾ�/�ܰ�)�� ���� ���Ͱ��� �� �Ѵ�.
						  // ���� �񱳵� ������ ���� �ٸ� ��� (�ݾ�/�ܰ�)�� ���� ���Ͱ��� oilAmount�� ���� �Ѵ�.
					  	  if(liter.compareTo(compareLiter)!=0) oilAmount = GlobalUtility.getMultipleWith1000(literInt);	
					  	  
					  	  LogUtility.getLogger().info("[Pump M] �񱳿��� �� ���� ��="+oilAmount);
					    //}
					} 
					
				} else if (pumpTrData.getPreset_qty_prc_ind().equals("0")) {
					LogUtility.getLogger().debug("[Pump M] ���� �� ���� ������ ���� ������ ����ϴ�.");
					
					priceInt = Double.parseDouble(pumpTrData.getPreset_prc()); // �� ���� �ݾ�
					literInt = GlobalUtility.getValueByCertainDecimal(PumpMUtil.getPresetLiter(priceInt, preset_basePriceInt), 3); // ���ε� �ܰ��� ���� ����
					beforePriceInt = GlobalUtility.multiple(literInt, nozzlePriceInt); // ���� X ������ ���� �ܰ� 
					
					BigDecimal before = new BigDecimal(Integer.parseInt(String.valueOf(Math.round(beforePriceInt))));
					before.setScale(0, RoundingMode.HALF_UP);
					
					// ���� �� �ܰ��� ���� �� �ܰ��� ������ ��� ���� �� �ݾ��� ������ �ݾ����� ����
					if(basePrice.equals(pumpTrData.getBaseprice())){
						LogUtility.getLogger().info("[Pump M] �ܰ� ���� ������ �ݾ� ����");
						oilPrice_before_discount = GlobalUtility.getStringValue(priceInt); // ���� �� �ݾ�
					}else{
						LogUtility.getLogger().info("[Pump M] �ܰ� �̵��� ������ �ݾ� �̼���");
						oilPrice_before_discount = GlobalUtility.getStringValue(before.toString()); // ���� �� �ݾ�
					}
					oilPrice_after_discount = GlobalUtility.getStringValue(priceInt);           // ���� �� �ݾ�
					oilAmount = GlobalUtility.getMultipleWith1000(literInt);                      // ������
					
				} else if (pumpTrData.getPreset_qty_prc_ind().equals("1")) {
					LogUtility.getLogger().debug("[Pump M] ���� �� ���� ������ ���� ������ ����ϴ�.");
					
					// �ܰ�
					BigDecimal beforeBaseprice = new BigDecimal(basePrice).setScale(0,RoundingMode.HALF_UP);
					BigDecimal afterBaseprice = new BigDecimal(pumpTrData.getBaseprice()).setScale(0,RoundingMode.HALF_UP);
					
					// ����
					BigDecimal pump_qty = new BigDecimal(pumpTrData.getPreset_qty());
					
					// ������
					BigDecimal beforeQty = beforeBaseprice.multiply(pump_qty); // ���� ��
					BigDecimal afterQty = afterBaseprice.multiply(pump_qty).setScale(0,RoundingMode.HALF_UP);   // ���� ��
					
					oilPrice_before_discount = beforeQty.toString();						// ���� �� �ݾ�
					oilPrice_after_discount = afterQty.toString();							// ���� �� �ݾ�
					oilAmount = GlobalUtility.getMultipleWith1000(pump_qty.toString());		// ������
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
	 * ���� ���ο� ���õ� �������� �˾ƺ���
	 * 
	 * @param messageTypeInt :
	 *            UPOSMessage �� Message Type
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
			//case IUPOSConstant.MESSAGETYPE_INT_0232: //���������ȸ
			//case IUPOSConstant.MESSAGETYPE_INT_0234: //����IC������ȸ
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
	 * GS , GS& , myLG ����Ʈ ī�带 ������ ����Ʈ ��� ���� ������ �Ǵ��Ѵ�. GS , GS& , myLG ī�带 ������
	 * ����Ʈ ������ ���� �ſ�ī�� ������ �����ϰ� ���������, �Һ� ������ '61' �� ���еȴ�.
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

	// CAT �ܸ���κ��� Preset ������ ���� ��뿩�θ� üũ�Ѵ�.
	// ���� �����Ⱑ �ƴ� ���  pump state�� 651(���) �� �� preset ����
	// ���� �������� ��� pump state�� 651(���) �׸��� 253�� �� preset ����
	// �׽�Ʈ �� �Ҹ�, �پ���� �����⸦ ����� �� ���������� �������� ��ȭ������ �����⸦ ����� �� ���������� ������ ����.
	public static boolean isReadyForPreset(String nozzleID) {
		/*
	    	01:������ 02:Self ������ 03:Semi-Self 04:������ 05:����ODT
		*/
		
		boolean isReady = false;
		// ���� Ÿ��
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
							LogUtility.getPumpMLogger().info("[Pump M] ���� ���·� ���ؼ� Preset �� �� �� �����ϴ�. �����ڵ�=" 
						              + statePumpCodeInt + ". ���� ����=" + PumpMTransactionManager.getInstance().getCurrState(nozzleID));
							isReady = false;
						}
					}
				}
			}else{
				T_NZ_NOZZLEData nozInfo =  null;
				nozInfo =  T_NZ_NOZZLEHandler.getHandler().getT_NZ_NOZZLEDataByNozzleNo(nozzleID);
				if(nozInfo != null){
					T_NZ_NOZZLEData[] nzDataArray = null;	// ���� ��û�� Nozzle �� ����� ODT �� ����� ��� Nozzle ����
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
    	    							LogUtility.getPumpMLogger().info("[Pump M] ���� ���·� ���ؼ� Preset �� �� �� �����ϴ�. �����ڵ�=" 
    	  						              + statePumpCodeInt + ". ���� ����=" + PumpMTransactionManager.getInstance().getCurrState(tempNozzleID));
    	        						isReady = false;
    	        						break;
    	        					}
    	        				}else{
    	        					LogUtility.getPumpMLogger().info("[Pump M] ���� ���·� ���ؼ� Preset �� �� �� �����ϴ�.statePumpCode= Null");
    	        					isReady = false;
	        						break;
    	        				}
	        				}else{
	        					LogUtility.getPumpMLogger().info("[Pump M] ���� ���·� ���ؼ� Preset �� �� �� �����ϴ�. ODT �� ����� ���� ����= Null");
	        					isReady = false ;
	        					break;
	        				}
	        			}
	        		}else{
	        			LogUtility.getPumpMLogger().info("[Pump M] ���� ���·� ���ؼ� Preset �� �� �� �����ϴ�. ���� ����Array= Null");
	        		}
				}else{
					LogUtility.getPumpMLogger().info("[Pump M] ���� ���·� ���ؼ� Preset �� �� �� �����ϴ�. ���� ����= Null");
				}
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.toString(), e);
		}
		return isReady;
	}

	/**
	 * ���� ���� ��ȣ ��û�� ���� ���� ������ �����մϴ�.
	 * 
	 * @param uPosInfo
	 * @param duPumpMsg
	 * @param completed
	 *            true : �����Ϸ� false : ������
	 * @return
	 */
	public static UPOSMessage process4201_4291(UPOSMessage uPosInfo, POS_DU duPumpMsg, boolean completed) {
		UPOSMessage uPosMessageReturn = null;

		String deviceType = uPosInfo.getDeviceType();
		String posReceipt_no = null;
		String nozzle_no = uPosInfo.getNozzle_no();
		UPOSMessage_ItemInfo item_info = null;
		String custCard_No = ""; // �ŷ�óī���ȣ
		String ss_crStNum = ""; // �ŷ�ó��ȣ
		String ss_carNum = ""; // �ŷ�ó������ȣ
		String ss_crStNm = ""; // �ŷ�ó��
		String custCar_limit_type = ""; // �ŷ�ó���������ѵ����뱸��
		String limit_type = ""; // �ѵ��������
		String limit_amt = ""; // �ѵ���,�ѵ��ݾ�
		String limit_remain = ""; // �ܷ�,�ܾ�
		String bonRSCard_ID = ""; // ���ʽ���ID
		String local_point = ""; // ������(����)����
		String local_occurPoint = ""; // �߻���������
		UPOSMessage_CampInfo camp_info = null; // ķ�������� Structure
		UPOSMessage_AffilateInfo affilate_info = null; // ����ī�� ��ȸ����
		String download_flag = getDownloadFlag(); // �ٿ�ε� Flag
		String pos_saleDate = UPOSUtil.getPosSaleDate(); // POS��������
		String term_ID = uPosInfo.getTerm_id(); // �ܸ��� ��ȣ
		String led_code = IUPOSConstant.LEDCODE_1; // LED�ڵ� (default : �Ϲ� ����)

		String unitPrint_yn = "1"; // �ŷ� �ܰ� ��� ����
		String carNoPrint_yn = "1"; // ���� ��ȣ ��� ����
		String loanCustBonus_yn = "1"; // �ܻ� �ŷ�ó ���ʽ� ���� ����
		String taxFreeCust_type = "0"; // �鼼 ����
		String fixedQty_yn = "0"; // ���� �Է� ����
		String fixedQty = "0"; // ���� ��
		String save_head_title = ""; // ������ �Ӹ���
		String save_foot_title1 = ""; // ������ ������1
		String save_foot_title2 = ""; // ������ ������2
		String save_expire_date = ""; // ������ ���� ��ȿ �Ⱓ

		// DU������ �ŷ�ó�ڵ� �߰��� ���� ���� 2009.10.27 edited by ykjang
		UPOSMessage_TradeCondition tradeCondition = null; // ����ī�� ��ȸ����

		if (duPumpMsg.getDup() == 0) {
			led_code = IUPOSConstant.LEDCODE_5;
		} else {
			// DU������ �ŷ�ó�ڵ� �߰��� ���� ���� 2009.10.27 edited by ykjang
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
	 * �ŷ�ó ���� ������ / �����Ϸ� ������ �����Ͽ� �����մϴ�.
	 * 
	 * @param uPosInfo
	 * @param dyPumpMsg
	 * @param completed
	 *            true : �����Ϸ� false : ������
	 * @return
	 */
	public static UPOSMessage process4201_4291(UPOSMessage uPosInfo,	POS_DY dyPumpMsg, boolean completed) {
		UPOSMessage uPosMessageReturn = null;

		String deviceType = uPosInfo.getDeviceType();
		String posReceipt_no = null;
		String nozzle_no = uPosInfo.getNozzle_no();
		UPOSMessage_ItemInfo item_info = null;
		String custCard_No = ""; // �ŷ�óī���ȣ
		String ss_crStNum = ""; // �ŷ�ó��ȣ
		String ss_carNum = ""; // �ŷ�ó������ȣ
		String ss_crStNm = ""; // �ŷ�ó��
		String custCar_limit_type = ""; // �ŷ�ó���������ѵ����뱸��
		String adjbase_code_limit = ""; // �ѵ��������
		String limit_amt = ""; // �ѵ���,�ѵ��ݾ�
		String limit_remain = ""; // �ܷ�,�ܾ�
		String bonRSCard_ID = ""; // ���ʽ���ID
		String local_point = ""; // ������(����)����
		String local_occurPoint = ""; // �߻���������
		UPOSMessage_CampInfo camp_info = null; // ķ�������� Structure
		UPOSMessage_AffilateInfo affilate_info = null; // ����ī�� ��ȸ����
		String download_flag = getDownloadFlag(); // �ٿ�ε� Flag
		String pos_saleDate = UPOSUtil.getPosSaleDate(); // POS��������
		String term_ID = uPosInfo.getTerm_id(); // �ܸ��� ��ȣ
		String led_code = IUPOSConstant.LEDCODE_1; // LED�ڵ� (default : �Ϲ� ����)
		String unitPrint_yn = "1"; // �ŷ� �ܰ� ��� ����
		String carNoPrint_yn = "1"; // ���� ��ȣ ��� ����
		String loanCustBonus_yn = "1"; // �ܻ� �ŷ�ó ���ʽ� ���� ����
		String taxFreeCust_type = "0"; // �鼼 ����
		String fixedQty_yn = "0"; // ���� �Է� ����
		String fixedQty = "0"; // ���� ��
		String save_head_title = ""; // ������ �Ӹ���
		String save_foot_title1 = ""; // ������ ������1
		String save_foot_title2 = ""; // ������ ������2
		String save_expire_date = ""; // ������ ���� ��ȿ �Ⱓ

		String cust_card_ind = dyPumpMsg.getCust_card_ind();

		if (cust_card_ind.equals(ICode.CUST_CARD_IND_02)) {
			custCard_No = dyPumpMsg.getCust_card_no(); 				// �ŷ�óī���ȣ
		}
		ss_crStNum = dyPumpMsg.getCust_no(); 						// �ŷ�ó��ȣ
		ss_carNum = dyPumpMsg.getCar_no(); 							// �ŷ�ó������ȣ
		ss_crStNm = dyPumpMsg.getCust_name(); 						// �ŷ�ó��
		led_code = dyPumpMsg.getLed_code();
		posReceipt_no = dyPumpMsg.getKhTransactionID();
		unitPrint_yn = dyPumpMsg.getUnitPrint_yn(); 				// �ŷ� �ܰ� ��� ����
		carNoPrint_yn = dyPumpMsg.getCarNoPrint_yn(); 				// ���� ��ȣ ��� ����
		loanCustBonus_yn = dyPumpMsg.getCust_mileage_ind(); 		// �ܻ� �ŷ�ó ���ʽ� ���� ����
		taxFreeCust_type = dyPumpMsg.getTaxFreeCust_type(); 		// �鼼 ����
		fixedQty_yn = dyPumpMsg.getFixedQty_yn(); 					// ���� �Է� ����
		fixedQty = dyPumpMsg.getFixedQty(); 						// ���� ��
		save_head_title = dyPumpMsg.getSave_head_title(); 			// ������ �Ӹ���
		save_foot_title1 = dyPumpMsg.getSave_foot_title1(); 		// ������ ������1
		save_foot_title2 = dyPumpMsg.getSave_foot_title2(); 		// ������ ������2
		save_expire_date = dyPumpMsg.getSave_expire_date(); 		// ������ ���� ��ȿ �Ⱓ

		String card_code_base = dyPumpMsg.getCard_code_base(); 		// ī�� ����
		String cardadj_ind = dyPumpMsg.getCardadj_ind(); 			// ī�� ���� ����
		adjbase_code_limit = dyPumpMsg.getAdjbase_code_limit(); 	// �ѵ��������, 01=���� ,02=�ݾ�
		
		item_info = convertToUPOSMessage_ItemInfo(dyPumpMsg);
		UPOSMessage_TradeCondition affilate_info2 = null;
		
		if (card_code_base.equals(ICode.CARD_CODE_BASE_02)) {
			// �ܻ� �ŷ�ó // �ŷ�ó�� // ������
			if ((cardadj_ind.equals(ICode.CARDADJ_IND_02)) || (cardadj_ind.equals(ICode.CARDADJ_IND_03))) {
				custCar_limit_type = dyPumpMsg.getLimit_type();
				// ksm 2012.03.22 ���� �ø�.
				//limit_type = dyPumpMsg.getAdjbase_code_limit(); // �ѵ��������, 01=���� ,02=�ݾ�
				if (adjbase_code_limit.equals(ICode.ADJBASE_CODE_LIMIT_01)) {
					limit_amt = GlobalUtility.getMultipleWith1000(dyPumpMsg.getLimit()); // �ѵ���
					limit_remain = GlobalUtility.getMultipleWith1000(dyPumpMsg.getLimit_remains()); // �ܷ�
				} else {
					limit_amt = dyPumpMsg.getLimit(); // �ѵ��ݾ�
					limit_remain = dyPumpMsg.getLimit_remains(); // �ܾ�
				}
			}
		}
		
		//ksm 2012.03.23 ���⺸�� �ŷ�ó ó���� CAT���� �ѵ��ܷ� �� �ܷ� ����
		//	ī����� - 04: ���⺸���ŷ�ó

		if(ICode.CARD_CODE_BASE_04.equals(card_code_base)){	
			// ī�����뱸��(cardadj_ind) - 06: ���⺸���� , �ѵ��������(limit_type) - 01:���� 02:�ݾ�
			if ((ICode.CARDADJ_IND_06.equals(cardadj_ind)) && ICode.ADJBASE_CODE_LIMIT_01.equals(adjbase_code_limit)) {	
				
				custCar_limit_type = dyPumpMsg.getLimit_type();
				
				limit_amt = GlobalUtility.getMultipleWith1000(dyPumpMsg.getLimit()); // �ѵ���
				limit_remain = GlobalUtility.getMultipleWith1000(dyPumpMsg.getLimit_remains()); // �ܷ�
			}
		}
		
		// LED Code ��Ȯ��
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
	 * �Ϲ� ���� ������/�����Ϸ� ��û�� ���� ����
	 * 
	 * @param uPosInfo
	 * @param uPosMsgItemInfo
	 * @param completed
	 *            true : �����Ϸ� false : ������
	 * @return
	 */
	public static UPOSMessage process4201_4291(UPOSMessage uPosInfo,
			UPOSMessage_ItemInfo_Item uPosMsgItemInfo, boolean completed) {
		UPOSMessage uPosMessageReturn = null;

		String deviceType = uPosInfo.getDeviceType();
		String posReceipt_no = null;
		String nozzle_no = uPosInfo.getNozzle_no();
		UPOSMessage_ItemInfo item_info = null;
		String custCard_No = ""; // �ŷ�óī���ȣ
		String ss_crStNum = ""; // �ŷ�ó��ȣ
		String ss_carNum = ""; // �ŷ�ó������ȣ
		String ss_crStNm = ""; // �ŷ�ó��
		String custCar_limit_type = ""; // �ŷ�ó���������ѵ����뱸��
		String limit_type = ""; // �ѵ��������
		String limit_amt = ""; // �ѵ���,�ѵ��ݾ�
		String limit_remain = ""; // �ܷ�,�ܾ�
		String bonRSCard_ID = ""; // ���ʽ���ID
		String local_point = ""; // ������(����)����
		String local_occurPoint = ""; // �߻���������
		UPOSMessage_CampInfo camp_info = null; // ķ�������� Structure
		UPOSMessage_AffilateInfo affilate_info = null; // ����ī�� ��ȸ����
		String download_flag = getDownloadFlag(); // �ٿ�ε� Flag
		String pos_saleDate = UPOSUtil.getPosSaleDate(); // POS��������
		String term_ID = uPosInfo.getTerm_id(); // �ܸ��� ��ȣ
		String led_code = IUPOSConstant.LEDCODE_1; // LED�ڵ� (default : �Ϲ� ����)
		String unitPrint_yn = "1"; // �ŷ� �ܰ� ��� ����
		String carNoPrint_yn = "1"; // ���� ��ȣ ��� ����
		String loanCustBonus_yn = "1"; // �ܻ� �ŷ�ó ���ʽ� ���� ����
		String taxFreeCust_type = "0"; // �鼼 ����
		String fixedQty_yn = "0"; // ���� �Է� ����
		String fixedQty = "0"; // ���� ��
		String save_head_title = ""; // ������ �Ӹ���
		String save_foot_title1 = ""; // ������ ������1
		String save_foot_title2 = ""; // ������ ������2
		String save_expire_date = ""; // ������ ���� ��ȿ �Ⱓ
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
		// PI2, cwi, 2015-12-31 4202 �Ǵ� 4292 ������ ������ü �����Ͱ� ü������ �ʾ� �ش� ���� �߰�
		// PI2, cwi, 2016-02-16 ODT���� �ö�� �������� ���� ������ ���� ������ �߰� �Ѵ�.
		uPosMessageReturn.setCreditCardReading_type(uPosInfo.getCreditCardReading_type()); // �ſ�ī�� ������ü
		uPosMessageReturn.setBonCardReading_type(uPosInfo.getBonCardReading_type());	   // ���ʽ�ī�� ������ü
		uPosMessageReturn.setCustGoods_code(uPosInfo.getCustGoods_code()); 				   // ȭ��Ưȭ�ŷ���ǰ�ڵ�
		
		// PI2, cwi, 2016-03-04, ���ʽ� ī�尡 ���� ��� �������� ���θ� 0���� �����Ѵ�. - ������ 2
		if(GlobalUtility.isNullOrEmptyString(uPosInfo.getBonRSCard_no())){
			uPosMessageReturn.setPromptDiscount_yn("0");  // �������ο��� 
		}else{
			uPosMessageReturn.setPromptDiscount_yn(uPosInfo.getPromptDiscount_yn()); // �������ο��� 
		}
		
		// pi2, cwi, 2016-03-31
		// properties ���� �ҷ� �鿩 phase 1, 2�� �и� �ϸ� phase 1�� ��� �������� ���� 0���� ����
		/*String	pi2_phase	=	PropertyManager.getSingleton().getProperty(PropertyManager.KH_DEPLOY_PHASE, PropertyManager.KH_DEPLOY_PHASE_DEFAULT);
		if(("1").equals(pi2_phase)){
			if(uPosMessageReturn.getDeviceType().equals(IUPOSConstant.DEVICE_TYPE_3S)){
				uPosMessageReturn.setPromptDiscount_yn("0");  // �������ο��� 
			}
		}*/
		
		LogUtility.getLogger().debug("[Pump M] PromptDiscount_yn="+uPosMessageReturn.getPromptDiscount_yn());
		return uPosMessageReturn;
	}

	/**
	 * CAT�ܸ��� Preset ��û�� ���� ����(�ŷ�ó��)
	 * 
	 * @param uPosInfo
	 * @param uPosMsgItemInfo
	 * @param completed
	 *            true : �����Ϸ� false : ������
	 * @return
	 */
	public static UPOSMessage process4301(UPOSMessage uPosInfo,	POS_DY dyPumpMsg) {
		UPOSMessage uPosMessageReturn = null;

		String deviceType = uPosInfo.getDeviceType();
		String posReceipt_no = null;
		String nozzle_no = uPosInfo.getNozzle_no();
		UPOSMessage_ItemInfo item_info = null;
		String custCard_No = ""; // �ŷ�óī���ȣ
		String ss_crStNum = ""; // �ŷ�ó��ȣ
		String ss_carNum = ""; // �ŷ�ó������ȣ
		String ss_crStNm = ""; // �ŷ�ó��
		String custCar_limit_type = ""; // �ŷ�ó���������ѵ����뱸��
		String adjbase_code_limit = ""; // �ѵ��������
		String limit_amt = ""; // �ѵ���,�ѵ��ݾ�
		String limit_remain = ""; // �ܷ�,�ܾ�
		String bonRSCard_ID = ""; // ���ʽ���ID
		String local_point = ""; // ������(����)����
		String local_occurPoint = ""; // �߻���������
		UPOSMessage_CampInfo camp_info = null; // ķ�������� Structure
		UPOSMessage_AffilateInfo affilate_info = null; // ����ī�� ��ȸ����
		String download_flag = getDownloadFlag(); // �ٿ�ε� Flag
		String pos_saleDate = UPOSUtil.getPosSaleDate(); // POS��������
		String term_ID = uPosInfo.getTerm_id(); // �ܸ��� ��ȣ
		String led_code = IUPOSConstant.LEDCODE_1; // LED�ڵ� (default : �Ϲ� ����)
		String unitPrint_yn = "1"; // �ŷ� �ܰ� ��� ����
		String carNoPrint_yn = "1"; // ���� ��ȣ ��� ����
		String loanCustBonus_yn = "1"; // �ܻ� �ŷ�ó ���ʽ� ���� ����
		String taxFreeCust_type = "0"; // �鼼 ����
		String fixedQty_yn = "0"; // ���� �Է� ����
		String fixedQty = "0"; // ���� ��
		String save_head_title = ""; // ������ �Ӹ���
		String save_foot_title1 = ""; // ������ ������1
		String save_foot_title2 = ""; // ������ ������2
		String save_expire_date = ""; // ������ ���� ��ȿ �Ⱓ
		UPOSMessage_TradeCondition affilate_info2 = null;

		String cust_card_ind = dyPumpMsg.getCust_card_ind();

		if (cust_card_ind.equals(ICode.CUST_CARD_IND_02)) {
			custCard_No = dyPumpMsg.getCust_card_no(); // �ŷ�óī���ȣ
		}
		ss_crStNum = dyPumpMsg.getCust_no(); // �ŷ�ó��ȣ
		ss_carNum = dyPumpMsg.getCar_no(); // �ŷ�ó������ȣ
		ss_crStNm = dyPumpMsg.getCust_name(); // �ŷ�ó��
		led_code = dyPumpMsg.getLed_code();
		posReceipt_no = dyPumpMsg.getKhTransactionID();
		unitPrint_yn = dyPumpMsg.getUnitPrint_yn(); // �ŷ� �ܰ� ��� ����
		carNoPrint_yn = dyPumpMsg.getCarNoPrint_yn(); // ���� ��ȣ ��� ����
		loanCustBonus_yn = dyPumpMsg.getCust_mileage_ind(); // �ܻ� �ŷ�ó ���ʽ� ���� ����
		taxFreeCust_type = dyPumpMsg.getTaxFreeCust_type(); // �鼼 ����
		fixedQty_yn = dyPumpMsg.getFixedQty_yn(); // ���� �Է� ����
		fixedQty = dyPumpMsg.getFixedQty(); // ���� ��
		save_head_title = dyPumpMsg.getSave_head_title(); // ������ �Ӹ���
		save_foot_title1 = dyPumpMsg.getSave_foot_title1(); // ������ ������1
		save_foot_title2 = dyPumpMsg.getSave_foot_title2(); // ������ ������2
		save_expire_date = dyPumpMsg.getSave_expire_date(); // ������ ���� ��ȿ �Ⱓ

		String card_code_base = dyPumpMsg.getCard_code_base(); // ī�� ����
		String cardadj_ind = dyPumpMsg.getCardadj_ind(); // ī�� ���� ����
		adjbase_code_limit = dyPumpMsg.getAdjbase_code_limit(); // �ѵ��������, 01=���� ,02=�ݾ�
		
		item_info = convertToUPOSMessage_ItemInfo(dyPumpMsg);
		
		if (card_code_base.equals(ICode.CARD_CODE_BASE_02)) {
			// �ܻ� �ŷ�ó // �ŷ�ó�� // ������
			if ((cardadj_ind.equals(ICode.CARDADJ_IND_02)) || (cardadj_ind.equals(ICode.CARDADJ_IND_03))) {
				custCar_limit_type = dyPumpMsg.getLimit_type();
				// ksm 2012.03.22 ���� �ø�.
				//limit_type = dyPumpMsg.getAdjbase_code_limit(); // �ѵ��������, 01=���� ,02=�ݾ�
				if (adjbase_code_limit.equals(ICode.ADJBASE_CODE_LIMIT_01)) {
					limit_amt = GlobalUtility.getMultipleWith1000(dyPumpMsg.getLimit()); // �ѵ���
					limit_remain = GlobalUtility.getMultipleWith1000(dyPumpMsg.getLimit_remains()); // �ܷ�
				} else {
					limit_amt = dyPumpMsg.getLimit(); // �ѵ��ݾ�
					limit_remain = dyPumpMsg.getLimit_remains(); // �ܾ�
				}
			}
		}
		
		//ksm 2012.03.23 ���⺸�� �ŷ�ó ó���� CAT���� �ѵ��ܷ� �� �ܷ� ����
		//	ī����� - 04: ���⺸���ŷ�ó

		if(ICode.CARD_CODE_BASE_04.equals(card_code_base)){	
			// ī�����뱸��(cardadj_ind) - 06: ���⺸���� , �ѵ��������(limit_type) - 01:���� 02:�ݾ�
			if ((ICode.CARDADJ_IND_06.equals(cardadj_ind)) && ICode.ADJBASE_CODE_LIMIT_01.equals(adjbase_code_limit)) {	
				
				custCar_limit_type = dyPumpMsg.getLimit_type();
				
				limit_amt = GlobalUtility.getMultipleWith1000(dyPumpMsg.getLimit()); // �ѵ���
				limit_remain = GlobalUtility.getMultipleWith1000(dyPumpMsg.getLimit_remains()); // �ܷ�
			}
		}
		
		// LED Code ��Ȯ��
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
	 * CAT�ܸ��� Preset ��û�� ���� ����(�Ϲݰ�)
	 * 
	 * @param uPosInfo
	 * @param uPosMsgItemInfo
	 * @param completed
	 *            true : �����Ϸ� false : ������
	 * @return
	 */
	public static UPOSMessage process4301(UPOSMessage uPosInfo,
			UPOSMessage_ItemInfo_Item uPosMsgItemInfo) {
		UPOSMessage uPosMessageReturn = null;

		String deviceType = uPosInfo.getDeviceType();
		String posReceipt_no = null;
		String nozzle_no = uPosInfo.getNozzle_no();
		UPOSMessage_ItemInfo item_info = null;
		String custCard_No = ""; // �ŷ�óī���ȣ
		String ss_crStNum = ""; // �ŷ�ó��ȣ
		String ss_carNum = ""; // �ŷ�ó������ȣ
		String ss_crStNm = ""; // �ŷ�ó��
		String custCar_limit_type = ""; // �ŷ�ó���������ѵ����뱸��
		String limit_type = ""; // �ѵ��������
		String limit_amt = ""; // �ѵ���,�ѵ��ݾ�
		String limit_remain = ""; // �ܷ�,�ܾ�
		String bonRSCard_ID = ""; // ���ʽ���ID
		String local_point = ""; // ������(����)����
		String local_occurPoint = ""; // �߻���������
		UPOSMessage_CampInfo camp_info = null; // ķ�������� Structure
		UPOSMessage_AffilateInfo affilate_info = null; // ����ī�� ��ȸ����
		String download_flag = getDownloadFlag(); // �ٿ�ε� Flag
		String pos_saleDate = UPOSUtil.getPosSaleDate(); // POS��������
		String term_ID = uPosInfo.getTerm_id(); // �ܸ��� ��ȣ
		String led_code = IUPOSConstant.LEDCODE_1; // LED�ڵ� (default : �Ϲ� ����)
		String unitPrint_yn = "1"; // �ŷ� �ܰ� ��� ����
		String carNoPrint_yn = "1"; // ���� ��ȣ ��� ����
		String loanCustBonus_yn = "1"; // �ܻ� �ŷ�ó ���ʽ� ���� ����
		String taxFreeCust_type = "0"; // �鼼 ����
		String fixedQty_yn = "0"; // ���� �Է� ����
		String fixedQty = "0"; // ���� ��
		String save_head_title = ""; // ������ �Ӹ���
		String save_foot_title1 = ""; // ������ ������1
		String save_foot_title2 = ""; // ������ ������2
		String save_expire_date = ""; // ������ ���� ��ȿ �Ⱓ
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
	 * CAT M ���� ���� ���۽� Campaign ���� ���θ� �����Ѵ�. Campaign �� true �� ��� CAT M �� ������
	 * �ǰ�, CAT M �� CMS M ���� ������ �����Ѵ�. �׸��� CMS M �� ������ �����Ѵ�. 1. ���ʽ� ī�� ���� ���� ����
	 * ���� 2. Campaign ����
	 * 
	 * ���� CAT M ���� �����ϱ� �� ������ Ȯ���ϰ�, ���� ���� �� �ϳ��� ���� ��� Campaign �� false �� �����Ѵ�.
	 * 1. ������ Message Type Ȯ�� 2. Item Info (�����ڷ�) �� ���� ��� 3. ������/�Ϸ� ķ���� �������� ������
	 * �������� LED Code �� 0 �� ���
	 * 
	 * @param uPosMsg :
	 *            UPOSMessage
	 * @return
	 */
	public static boolean shouldCampaign(UPOSMessage uPosMsg) {

		int messageTypeInt = Integer.parseInt(uPosMsg.getMessageType());
		switch (messageTypeInt) {
			case IUPOSConstant.MESSAGETYPE_INT_0071: // �ſ�ī�� Ȯ�� ��û --> ������
			case IUPOSConstant.MESSAGETYPE_INT_8071: // �ſ�ī�� Ȯ�� ��ҿ�û --> ������
			case IUPOSConstant.MESSAGETYPE_INT_0101: // �ŷ�óī���ȣ ��û
			case IUPOSConstant.MESSAGETYPE_INT_0103: // GS���ʽ�ī���ȣ ��û
			case IUPOSConstant.MESSAGETYPE_INT_0105: // S���ʽ�ī���ȣ + �ŷ�óī���ȣ ��û
			case IUPOSConstant.MESSAGETYPE_INT_0107: // �ſ�ī���ȣ ��û
			case IUPOSConstant.MESSAGETYPE_INT_0102: // �ŷ�óī���ȣ ����
			case IUPOSConstant.MESSAGETYPE_INT_0104: // GS���ʽ�ī���ȣ ����
			case IUPOSConstant.MESSAGETYPE_INT_0106: // S���ʽ�ī���ȣ + �ŷ�óī���ȣ ����
			case IUPOSConstant.MESSAGETYPE_INT_0108: // �ſ�ī���ȣ ����
			case IUPOSConstant.MESSAGETYPE_INT_0302: // ��ǰ�ڵ�ٿ�ε� ����
			case IUPOSConstant.MESSAGETYPE_INT_0091: // ��������� ��� ��û
			case IUPOSConstant.MESSAGETYPE_INT_0119: // ���ڵ��ȣ ��ȸ ��û
			case IUPOSConstant.MESSAGETYPE_INT_0120: // ���ڵ��ȣ ��ȸ ����				
			case IUPOSConstant.MESSAGETYPE_INT_0113: // myLG ���ʽ�������ȸ��û
			case IUPOSConstant.MESSAGETYPE_INT_0115: // ���ڻ�ǰ�ǰŷ�������ȸ ��û
	
			case IUPOSConstant.MESSAGETYPE_INT_0021:
			case IUPOSConstant.MESSAGETYPE_INT_0015:			
			case IUPOSConstant.MESSAGETYPE_INT_0025:
			case IUPOSConstant.MESSAGETYPE_INT_0031:
			case IUPOSConstant.MESSAGETYPE_INT_0231:  //������� ��ȸ ��û    
			case IUPOSConstant.MESSAGETYPE_INT_0232:  //������� ��ȸ ����    
			case IUPOSConstant.MESSAGETYPE_INT_0041:
			case IUPOSConstant.MESSAGETYPE_INT_0045:  //���̰��� ��û
			case IUPOSConstant.MESSAGETYPE_INT_0047:  //���̰������ʽ� ��û

			case IUPOSConstant.MESSAGETYPE_INT_0235:  //����CarPay ������ȣ ��ȸ ��û
			case IUPOSConstant.MESSAGETYPE_INT_0241:  //������÷��� ���� ��û
			case IUPOSConstant.MESSAGETYPE_INT_0243:  //����CarPay ���� ��û
			case IUPOSConstant.MESSAGETYPE_INT_0245:  //�ٷμ��� ���� ��û
	
			case IUPOSConstant.MESSAGETYPE_INT_8021:
			case IUPOSConstant.MESSAGETYPE_INT_8015:
			case IUPOSConstant.MESSAGETYPE_INT_8025:
			case IUPOSConstant.MESSAGETYPE_INT_8031:
			case IUPOSConstant.MESSAGETYPE_INT_8041: 
			case IUPOSConstant.MESSAGETYPE_INT_8045:  //���̰������ ��û
			case IUPOSConstant.MESSAGETYPE_INT_8047:  //���̰������ʽ����  ��û				

			case IUPOSConstant.MESSAGETYPE_INT_8241:  //������÷��� ���� ��û
			case IUPOSConstant.MESSAGETYPE_INT_8243:  //����CarPay ���� ��û
			case IUPOSConstant.MESSAGETYPE_INT_8245:  //�ٷμ��� ������ҿ�û
			{
				LogUtility.getPumpMLogger().debug(
						"[Pump M] messageTypeInt=" + messageTypeInt	+ " : Set isCampaign=false");
				return false;
			}
			case IUPOSConstant.MESSAGETYPE_INT_4202:
			case IUPOSConstant.MESSAGETYPE_INT_4292: 
			case IUPOSConstant.MESSAGETYPE_INT_4302:{
				//2016.03.23, PI2, CWI, ODT���� ��û�� �����̸� CMSM���� �����ϱ� ���ؼ� ķ���� ���θ� TRUE�� �Ѵ�.
				if( uPosMsg.getDeviceType().equals(IUPOSConstant.DEVICE_TYPE_3S) || //SELF ODT
						uPosMsg.getDeviceType().equals(IUPOSConstant.DEVICE_TYPE_3O)){ //������ 
					
						String bonusCardNo = uPosMsg.getBonRSCard_no();
						
						if (GlobalUtility.isNullOrEmptyString(bonusCardNo)) {
							LogUtility.getPumpMLogger().debug("[Pump M] BonusCard Field = null " + " : Set isCampaign=false");
							return false;
						} else {
							LogUtility.getPumpMLogger().debug("[Pump M util-songkis] ODT���� ��û�� �����̸� CMSM���� �����ϱ� ���ؼ� ķ���� ���θ� TRUE�� �Ѵ�");
							return true;
						}
				}else{
					String ledCode = uPosMsg.getLed_code();
					
					LogUtility.getPumpMLogger().debug("[Pump M] led-code= " + uPosMsg.getLed_code() + " : Set isCampaign=false");
	
					if (ledCode.equals("0") || ledCode.equals("5")
							|| ledCode.equals("6") || ledCode.equals("7")
							|| ledCode.equals("B") || ledCode.equals("C")
							|| ledCode.equals("D") || ledCode.equals("E")
							|| ledCode.equals("F") || ledCode.equals("J") 		// 2013.05.07 ksm ledCode = J, K �߰�
							|| ledCode.equals("K") || ledCode.equals("Z")
							|| ledCode.equals("G")) {
						return false;	// 2016. 4. 14 ���� �������� ���ؼ� ����. ���������� CMS M ���� �ؾ� �Ѵٰ� ������ ������ �ʱ� Logic �� Setup �Ͽ���,
										// 	����ö ������ ���� Rule �� �������� ��.	 �� ��ó���ϰ� ����. �ֳ�, ���⼭ �ص� �Ǵµ�....
					} 
					
					break ; // 2017.11.14 as-is KixxHUB�� �����ϰ� ó�� (�Ʒ� ���� ���� Ȯ�� �ʿ�)
					/*else {
						return true;	// 2016. 4. 14 ���� �������� ���ؼ� ����. ���������� CMS M ���� �ؾ� �Ѵٰ� ������ ������ �ʱ� Logic �� Setup �Ͽ���,
						// 	����ö ������ ���� Rule �� �������� ��.	 �� ��ó���ϰ� ����. �ֳ�, ���⼭ �ص� �Ǵµ�....
					}*/
				}
			}
		}

		// ���� ������ ���� ��� (Item info)
		UPOSMessage_ItemInfo itemInfo = uPosMsg.getItem_info();
		int itemInfoNo = Integer.parseInt(itemInfo.getRecordNo());
		if (itemInfoNo == 0) {
			LogUtility.getPumpMLogger().debug("[Pump M] No ItemInfo " + " : Set isCampaign=false");
			return false;
		}
		// 2015-01-16 twsongkis campaign�� �������� �ʱ� ���� false�� ��ȯ
		// ���� campign ����� true�� ����
		return true;
	}

	/**
	 * CAT �ܸ���� ������û�� ���� ���� ������ ������ ���, �̹� Locking �� �����ǿ� ���ؼ� ������ �ؾ� ���� ���θ�
	 * �Ǵ��Ѵ�. ���� �ŷ�ó + �������� ��û�� ���, �켱 Ư�� �����ǿ� ���ؼ� Locking �� ����������, �ŷ�ó ���� ���� ����
	 * ����� �ȵǾ� �ְų�. �ٸ� ������ ���ؼ� ���� ������ �������� ���ϴ� ��쿡�� ������ �� �־�� �Ѵ�.
	 * 
	 * @param uPosMsg :
	 *            ���� ���� ��û�� ���� ���� ����
	 * @return : Locking ���� ����
	 */
	public static boolean shouldUnLock(UPOSMessage uPosMsg) {
		boolean rlt = false;

		try {
			int messageTypeInt = Integer.parseInt(uPosMsg.getMessageType());
			String posReceipt_no = uPosMsg.getPosReceipt_no();

			if (posReceipt_no.startsWith("K")) {
				// ���� ������ ���� ��� (Item info)
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
