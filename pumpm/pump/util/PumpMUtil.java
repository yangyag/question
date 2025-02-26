package com.gsc.kixxhub.module.pumpm.pump.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import org.apache.ibatis.session.SqlSession;

import com.gsc.kixxhub.common.data.ICode;
import com.gsc.kixxhub.common.data.IConstant;
import com.gsc.kixxhub.common.data.ICustConstant;
import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.IUPOSConstant;
import com.gsc.kixxhub.common.data.beacon.BeaconMessage;
import com.gsc.kixxhub.common.data.posdata.POSHeader;
import com.gsc.kixxhub.common.data.posdata.POSMessage;
import com.gsc.kixxhub.common.data.posdata.POS_CX;
import com.gsc.kixxhub.common.data.posdata.POS_DB;
import com.gsc.kixxhub.common.data.posdata.POS_DD_NozzleInfo;
import com.gsc.kixxhub.common.data.posdata.POS_DI;
import com.gsc.kixxhub.common.data.posdata.POS_DK;
import com.gsc.kixxhub.common.data.posdata.POS_DN;
import com.gsc.kixxhub.common.data.posdata.POS_DO;
import com.gsc.kixxhub.common.data.posdata.POS_DU;
import com.gsc.kixxhub.common.data.posdata.POS_DU_Car;
import com.gsc.kixxhub.common.data.posdata.POS_DY;
import com.gsc.kixxhub.common.data.posdata.POS_DY_Item;
import com.gsc.kixxhub.common.data.posdata.POS_KH_NozzleInfo;
import com.gsc.kixxhub.common.data.posdata.POS_KI;
import com.gsc.kixxhub.common.data.pump.D0_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.D0_nozzleInfo;
import com.gsc.kixxhub.common.data.pump.D0_odtInfo;
import com.gsc.kixxhub.common.data.pump.D1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.M1_DBoxInfo;
import com.gsc.kixxhub.common.data.pump.M1_NozzleInfo;
import com.gsc.kixxhub.common.data.pump.M1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P2_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P3_NozzleInfo;
import com.gsc.kixxhub.common.data.pump.P3_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P5_NozzleInfo;
import com.gsc.kixxhub.common.data.pump.P5_OdtInfo;
import com.gsc.kixxhub.common.data.pump.P5_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P6_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P7_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P9_NozInfo;
import com.gsc.kixxhub.common.data.pump.P9_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PC_StoreCodeInfo;
import com.gsc.kixxhub.common.data.pump.PC_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PF_CarInfo;
import com.gsc.kixxhub.common.data.pump.PF_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PG_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PQ_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PU_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.QF_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SJ_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.data.pump.format.PumpMessageFormat;
import com.gsc.kixxhub.common.data.upos.UPOSMessage;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_ItemInfo_Item;
import com.gsc.kixxhub.common.dbadapter.common.SqlSessionFactoryManager;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_CT_CATHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_CODEMASTERHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_CUST_CARD_INFOHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_CUST_CAR_INFOHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_CUST_INFOHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_KEYSHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_POS_INFOHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_PRC_AMT_GOALHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_PRODUCTHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_PUMP_TRHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_STOREHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_CUST_CARD_INFOData;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_CUST_CAR_INFOData;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_CUST_INFOData;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_KEYSData;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_PRODUCTData;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_PUMP_TRData;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_STOREData;
import com.gsc.kixxhub.common.dbadapter.pump.handler.T_NZ_INFOHandler;
import com.gsc.kixxhub.common.dbadapter.pump.handler.T_NZ_NOZZLEHandler;
import com.gsc.kixxhub.common.dbadapter.pump.handler.T_NZ_PARAMETERHandler;
import com.gsc.kixxhub.common.dbadapter.pump.vo.T_NZ_INFOData;
import com.gsc.kixxhub.common.dbadapter.pump.vo.T_NZ_NOZZLEData;
import com.gsc.kixxhub.common.dbadapter.pump.vo.T_NZ_PARAMETERData;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.common.utility.property.PropertyManager;
import com.gsc.kixxhub.common.utility.sync.Preamble;
import com.gsc.kixxhub.common.utility.sync.SyncManager;
import com.gsc.kixxhub.module.pumpm.pump.data.cust.CustReturnValue;
import com.gsc.kixxhub.module.pumpm.pump.data.cust.LimitAmount;
import com.gsc.kixxhub.module.pumpm.pump.manager.PumpMPriceManager;
import com.gsc.kixxhub.module.pumpm.pump.manager.PumpMTransactionManager;

public class PumpMUtil {

	private static String keepNo_Type = "1" ;	// 0 : POS , 1: KixxHub
	private static String keepNoDay = null ;
	private static String SA_MIN_AMT = null ;	// ���� ������ ����� ���� �ݾ�
		
	/**
	 * Preset �������� ���� ���װ� �ܰ��� �̿��Ͽ� ���� ���ϱ�
	 * [2008.11.18] by �������
	 * 		������ ��� �Ҽ��� 6�ڸ� ���ķδ� �����ϰ�, �Ҽ��� 5�ڸ� �ݿø�, �� ���� 4�ڸ� �ݿø� ���� ���� �Ҽ��� 3�ڸ����� 
	 * 		������ �̿��Ѵ�.
	 * @param price		: ���� �ݾ�
	 * @param basePrice	: ���� �ܰ�
	 * @return
	 */
	public static String calculateLiterFromPriceAndBasePrice(String price, String basePrice) {
		double priceDou = Double.parseDouble(price) ;
		double basePriceDou = Double.parseDouble(basePrice) ;
		
		double literDou = getPresetLiter(priceDou, basePriceDou) ;
				
		return Double.toString(literDou) ; 
	}
	
	/**
	 * ������ �ܰ��� �̿��Ͽ� �ݾ��� ���ϱ�
	 * [2008.11.18] by �������
	 * 		�Ҽ��� ù°�ڸ����� �ݿø� �Ѵ�.
	 * 
	 * @param liter
	 * @param basePrice
	 * @return
	 */
	public static String calculatePriceFromLiterAndBasePrice(String liter, String basePrice) {
		String price = "0" ;
		price = GlobalUtility.multiple(liter, basePrice)  ;
		double temp = Double.parseDouble(price) ;
		temp = Math.round(temp) ;
		price = GlobalUtility.getStringValue(Double.toString(temp)) ;
		return price ; 		
	}
	
	/**
	 * 
	 * ������� ���� �ö�� �ܰ� ���ڸ� POS Format ���� ����
	 * 
	 * @param basePrice	: ������� ���� �ö�� �ܰ� ���� 
	 * @return
	 */
	public static String convertBasePriceFromPumpToPOS(String basePrice) {
		return convertNumberFormatFromPumpToPOS(basePrice , 2 ,8) ;
	}
	
	
	
	/**
	 * 
	 * @param basePriceStr
	 * 
	 * @return
	 * 		�ܰ� (4.2)
	 */
	public static String convertBasePriceFromUPOSToPumpA(String basePriceStr) {
		if (GlobalUtility.isNullOrEmptyString(basePriceStr)) basePriceStr = "0" ;

		double basePriceDouble = Double.parseDouble(basePriceStr) ;
		double basePrice = basePriceDouble / GlobalUtility.INT_1000 ;
		return  PumpMUtil.convertNumberFormatFromPOSToPump(Double.toString(basePrice) , 4, 2) ;
	}
	
	
	
	/**
	 * ������� ���� �ö�� ���� ���ڸ� POS Format ���� ����
	 * 
	 * @param liter	: ������� ���� �ö�� ���� ���� 
	 * @return
	 */
	public static String convertLiterFromPumpToPOS(String liter) {
		return convertNumberFormatFromPumpToPOS(liter , 3 ,8) ;
	}
	
	/**
	 * 
	 * @param liter : ���� ���� ������ Liter (���� Liter * 1000)
	 * 
	 * @return
	 * 		���� (4.3)
	 */
	public static String convertLiterFromUPOSToPumpA(String literStr) {
		if (GlobalUtility.isNullOrEmptyString(literStr)) literStr = "0" ;
		
		double literDouble = Double.parseDouble(literStr) ;
		double liter = literDouble / GlobalUtility.INT_1000 ;
		return PumpMUtil.convertNumberFormatFromPOSToPump(Double.toString(liter) , 4, 3) ;
	}
	
	/**
	 * POS �� ���� ���� ���ڸ� �����⿡ �°Բ� ����
	 * 
	 * @param src					: POS �� ���� ���� ����
	 * @param returnValuePosLength	: �����Ⱑ ���ϴ� ������ �ڸ���
	 * @param returnValueDeciLength	: �����Ⱑ ���ϴ� �Ҽ��� �ڸ���
	 * @return
	 */
	public static String convertNumberFormatFromPOSToPump(String src , int returnValuePosLength, int returnValueDeciLength) {
		int dotPos = src.lastIndexOf(".") ;
		
		if (dotPos > 0) {
			String posiNumTemp = src.substring(0 , dotPos);
			String deciNumTemp = src.substring(dotPos + 1 , src.length());
			
			String posiNum = GlobalUtility.appending0Pre(posiNumTemp , returnValuePosLength) ;
			String deciNum = GlobalUtility.appending0End(deciNumTemp , returnValueDeciLength) ;
			
			return posiNum + deciNum ;
		} else {
			String posiNumTemp = src ;
			String deciNumTemp = "" ;
			
			String posiNum = GlobalUtility.appending0Pre(posiNumTemp , returnValuePosLength) ;
			String deciNum = GlobalUtility.appending0End(deciNumTemp , returnValueDeciLength) ;
			
			return posiNum + deciNum ;
		}
	}

	/**
	 * ������� ���� �ö�� ���ڸ� POS �� Format �� �°� ����
	 * 
	 * @param src			: ������� ���� ���� ����
	 * @param deciLength	: ������� ���� ���� ���ڿ��� �Ҽ��� �ڸ���
	 * @return
	 */
	public static String convertNumberFormatFromPumpToPOS(String src , int deciLength) {
		return convertNumberFormatFromPumpToStandardFormat(src, deciLength) ;
	}	
	
	
	/**
	 * ������� ���� �ö�� ���ڸ� POS �� Format �� �°� ����
	 * 
	 * @param src			: ������� ���� ���� ����
	 * @param deciLength	: ������� ���� ���� ���ڿ��� �Ҽ��� �ڸ���
	 * @param returnValueLength	: POS �� ������ ��ü ���� (���� ������ ����)
	 * @return
	 */
	public static String convertNumberFormatFromPumpToPOS(String src , int deciLength, int returnValueLength) {
		return convertNumberFormatFromPumpToPOS(src, deciLength) ;
	}
	
	
	/**
	 * ������� ���� �ö�� ���ڸ� Standard Format ���� ����
	 * 
	 * @param srcData		: ������� ���� ���� ����
	 * @param deciLength	: ������� ���� ���� ���ڿ��� �Ҽ��� �ڸ���
	 * @return
	 */
	public static String convertNumberFormatFromPumpToStandardFormat(String srcData , int deciLength) {
		try {
			if (GlobalUtility.isNullOrEmptyString(srcData)) return "0" ;
			
			double srcInt = Double.parseDouble(srcData) ;
			if (srcInt == 0) return "0" ;
			
			double srcDouble = Double.parseDouble(srcData) ;
			srcDouble = srcDouble / (GlobalUtility.INT_10[deciLength]) ;
			return Double.toString(srcDouble) ;
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
			return "0" ;
		}
	}
	
	
	/**
	 * UPOS �� ������ ���ؼ� 1000 �� ���Ѵ�.
	 * 
	 * @param src
	 * @return
	 */
	public static String convertNumberFormatFromPumpToUPOS(String src) {
		try {
			if (GlobalUtility.isNullOrEmptyString(src)) return src ;
			else {
				double srcDoub = Double.parseDouble(src) * 1000L ;
				return GlobalUtility.getStringValue(srcDoub) ;
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
			return "0" ;
		}
	}
	
	
	/**
	 * UPOSMessage �� �ܰ� �� ���͸� Standard Format �� �°Բ� �����Ѵ�.
	 * 
	 * @param srcData	: UPOSMessage �� �ܰ� or ���� ����
	 * @return
	 */
	public static String convertNumberFormatFromUPOSToStandardFormat(String srcData) {
		try {
			if (GlobalUtility.isNullOrEmptyString(srcData)) return "0" ;
			
			int srcInt = Integer.parseInt(srcData) ;
			if (srcInt == 0) return "0" ;
			
			double srcDouble = Double.parseDouble(srcData) ;
			srcDouble = srcDouble / (GlobalUtility.INT_10[3]) ;
			return Double.toString(srcDouble) ;
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
			return "0" ;
		}
	}
	
	/**
	 * 
	 * ������� ���� �ö�� �ݾ� ���ڸ� POS Format ���� ����
	 * 
	 * @param price	: ������� ���� �ö�� �ݾ� ���� 
	 * @return
	 */
	public static String convertPriceFromPumpToPOS(String price) {
		return GlobalUtility.appending0Pre(price , 8) ;
	}
	
	/**
	 * UPOSMessage �� �ݾ��� Pump A �� Spec �� �°� �����Ѵ�.
	 * 
	 * @param priceStr : UPOSMessage �� �ݾ�
	 * @return
	 * 		���� �ݾ�
	 */
	public static String convertPriceFromUPOSToPumpA(String priceStr) {
		if (GlobalUtility.isNullOrEmptyString(priceStr)) priceStr = "0" ;

		double priceDouble = Double.parseDouble(priceStr) ;
		double price = priceDouble ;
		return  PumpMUtil.convertNumberFormatFromPOSToPump(Double.toString(price) , 8, 0) ;
	}
	
	/**
	 * 
	 * ������� ���� �ö�� Total Gauage ���ڸ� POS Format ���� ����
	 * 
	 * @param totalLiter	: ������� ���� �ö�� Total Gauage ���� 
	 * @return
	 */
	public static String convertTotalLiterFromPumpTOPOS(String totalLiter) {
		return convertNumberFormatFromPumpToPOS(totalLiter , 3 ,10) ;
	}
	
	/**
	 * fromArray �� ������ destArray �� �߰��Ѵ�.
	 * 
	 * @param destArray
	 * @param fromArray
	 */
	public static void copyArrayList(ArrayList<WorkingMessage> destArray, ArrayList<WorkingMessage> fromArray) {
		if ((fromArray == null) || (fromArray.size() == 0)) return ;
		else {
			for (int i = 0 ; i < fromArray.size() ; i++) {
				destArray.add(fromArray.get(i)) ;
			}
		}
	}
	
	
	/**
	 * ������/����ODT/������ �ʱ�ȭ ������ �����Ҷ� ���������� �߰��ؾ� �� ������ �����Ѵ�.
	 * 
	 * @param storeCode	: ���� �ڵ�
	 * @return
	 */
	public static ArrayList<WorkingMessage> createCommonInitLastWorkingMessage(String storeCode) {
		ArrayList<WorkingMessage> workMsgList = new ArrayList<WorkingMessage>() ;
		try {
			WorkingMessage p6WorkMsg = PumpMUtil.getP6WorkingMessage(storeCode) ;
			LogUtility.getPumpMLogger().debug("[Pump M] Create P6") ;
			p6WorkMsg.setMessageID(GlobalUtility.getUniqueMessageID()) ;
			workMsgList.add(p6WorkMsg) ;
			
			WorkingMessage[] p7WorkMsgArray = PumpMUtil.getP7WorkingMessageArray(storeCode , null) ;
			LogUtility.getPumpMLogger().debug("[Pump M] Create P7") ;
			
			if ((p7WorkMsgArray != null) && (p7WorkMsgArray.length > 0)) {
				for (int i = 0 ; i < p7WorkMsgArray.length ; i++) {
					workMsgList.add(p7WorkMsgArray[i]) ;
				}
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		return workMsgList ;
	}
	
	/**
	 * ������/����ODT/������ �ʱ�ȭ ������ �����ϱ� ���� �������� ������ ���� ����(M1)�� �����Ѵ�.
	 * 
	 * @param storeCode	: ���� �ڵ�
	 * @return
	 */
	public static ArrayList<WorkingMessage> createCommonInitPreWorkingMessage(String storeCode) {
		ArrayList<WorkingMessage> workMsgList = new ArrayList<WorkingMessage>() ;
		try {
			WorkingMessage m1WorkMsg = PumpMUtil.getM1WorkingMessage() ;
			if (m1WorkMsg != null) {
				LogUtility.getPumpMLogger().debug("[Pump M] Create M1") ;
				m1WorkMsg.setMessageID(GlobalUtility.getUniqueMessageID()) ;
				workMsgList.add(m1WorkMsg) ;
			} else {
//				LogUtility.getPumpMLogger().debug("[Pump M] �����Ⱑ ���, M1 ������ ������ ���� �����ϴ�.") ;
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		return workMsgList ;
	}

	/**
	 * ������ �������� �ڷḦ �����Ѵ�.
	 * ���� ���� ���� ���� ��/�Ϸ� �����Ͱ� �Ա� ������, ���Ƿ� ���� ���� ������ �����ؼ� ���̺� �ִ´�
	 * 
	 * @param nozzle_no	: ���� ��ȣ
	 * @return
	 */	
	public static String createFakePumpStartContent(String nozzle_no) {
		String khTransactionID = null ;
		
		try {
			LogUtility.getPumpMLogger().warn("[Pump M] Create fakePumping Start content.nozzle_no="+nozzle_no) ;
			int nozzle_noInt = Integer.parseInt(nozzle_no) ;
			
			/**
			 * [2008.11.18] by WooChul Jung
			 * 		������ ���� Normal �� ��Ȳ�� Exception �� ��Ȳ�� �ִ�.
			 * 			1. �������� -> Preset -> ������
			 * 				�� ��� ������ �������� ������ �ٽ� �����ϸ�, ������ ���� ����.
			 * 			2. ������ -> (100 ������ �Ѵ°��) -> Pump A �� ���� Preset �� ���۵ǰ� �Ѵ�.(PumpA ����)
			 * 				�� ��� �������� ������ �ٽ� �����ϸ�, �ݾ��� 0 ���� ������ ������ ������ �ݾ��� �ǹ� ���� �ȴ�.
			 * 		���� �ΰ��� ������ ���� �Ʒ� initPumpPrice �Լ� ȣ���� �����Ѵ�.
			 */
//			PumpMPriceManager.initPumpPrice(nozzle_noInt) ;
	
			khTransactionID = PumpMTransactionManager.getInstance().getKHTransactionID(nozzle_no, IPumpConstant.KH_PUMP_START) ;
			
			// �� ���� Total Gauage �� ����Ѵ�.
			String startTotalGuage = PumpMPriceManager.getTotalGauage(nozzle_noInt) ;
			try {
				T_KH_PUMP_TRData trData = T_KH_PUMP_TRHandler.getHandler().getT_KH_PUMP_TRData(khTransactionID) ;
				/**
				 * [2008.11.18] by WooChul Jung
				 * 		�̹� ���� ���� ������ ���̺� Update �� ��쿡�� Update ���� �ʴ´�.
				 */
				if ((trData != null) && (!ICode.PRESET_QTY_PRC_IND_1_LITER.equals(trData.getPreset_qty_prc_ind()))) {
					T_KH_PUMP_TRHandler.getHandler().updatePumpStartInfo_BY_khproc_no(khTransactionID, startTotalGuage) ;
					LogUtility.getPumpMLogger().info("[Pump M] Insert fake Pumping Start content in Table.") ;	
				} else {
					LogUtility.getPumpMLogger().info("[Pump M] Pumping Start Content Already exist in Table.") ;
				}
			} catch (Exception e) {
				LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
			}
			
			PumpMPriceManager.setBasePrice(nozzle_noInt, Double.parseDouble(PumpMUtil.getBasePriceFromKHProcNo(nozzle_no, khTransactionID))) ;
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		
		return khTransactionID ;
	}
	
	/**
	 * ������/����ODT/�����⸦ �ʱ�ȭ �ϱ� ���� ��� �ʱ�ȭ ������ �����Ѵ�.
	 * 
	 * @param storeCode	: ���� �ڵ�
	 * @param nozzle_no	: ���� ��ȣ
	 * @return
	 */
	public static ArrayList<WorkingMessage> createInitAllWorkingMessage(String storeCode, String nozzle_no) {
		ArrayList<WorkingMessage> allWorkMsgArray = new ArrayList<WorkingMessage>() ;
		ArrayList<WorkingMessage> tempArray = createPumpInitWorkingMessage(storeCode, nozzle_no) ;
		copyArrayList(allWorkMsgArray, tempArray) ;
		
		tempArray = createSelfODTInitWorkingMessage(storeCode, nozzle_no) ;
		copyArrayList(allWorkMsgArray, tempArray) ;
		
		tempArray = createRechargeODTInitWorkingMessage(storeCode, nozzle_no) ;
		copyArrayList(allWorkMsgArray, tempArray) ;
		
		tempArray = createCommonInitLastWorkingMessage(storeCode) ;
		copyArrayList(allWorkMsgArray, tempArray) ;
		
		return allWorkMsgArray ;
	}
	
	/**
	 * ������ ������ ���� ��û
	 * POS �� ���� ���� KI ������ P9_WorkingMessage Object �� ��ȯ�Ѵ�.
	 * @return
	 * @throws Exception
	 */
	public static P9_WorkingMessage createP9WorkingMessage(POS_KI kiPump)throws Exception {
		P9_WorkingMessage p9WorkingMsg = new P9_WorkingMessage() ;
		
		ArrayList<String> odtDataArray  = null ;
		odtDataArray =  T_NZ_NOZZLEHandler.getHandler().getRechargeODTNoList() ;
		
		Vector<P9_NozInfo> odtInfoVector2 = new Vector<P9_NozInfo>() ;
		for (int i = 0 ; i < odtDataArray.size() ; i++) {
			String odtDataID = odtDataArray.get(i) ;
			P9_NozInfo p9OdtInfo = new P9_NozInfo() ;
			
			p9OdtInfo.setOdtNo(odtDataID) ;
			
			odtInfoVector2.add(p9OdtInfo) ;
		}
		
		p9WorkingMsg.setMessageID(kiPump.getMessageID()) ;
		
		p9WorkingMsg.setP9NozInfoVector(odtInfoVector2) ;
		p9WorkingMsg.setOdtCount(Integer.toString(odtInfoVector2.size())) ;
		
		return p9WorkingMsg ;
	}
	
	/**
	 * POS �� ���� ���� DI ������ PA_WorkingMessage Object �� ��ȯ�Ѵ�.
	 * 
	 * @param diPump	: POS �� ���� ���� DI ����
	 * @return
	 */
	public static PA_WorkingMessage createPA_WorkMsg(POS_DI diPump) { 
		PA_WorkingMessage paWorkMsg = new PA_WorkingMessage() ;
		paWorkMsg.setNozzleNo(diPump.getDeviceID()) ;
		paWorkMsg.setNozzleState(diPump.getCommand()) ;
		paWorkMsg.setMessageID(diPump.getMessageID()) ;
		return paWorkMsg ;
	}
	
	/**
	 * POS �� ���� ���� DK ������ PB_WorkingMessage Object �� ��ȯ�Ѵ�.
	 * 
	 * @param dkPump	: POS �� ���� ���� DK ����
	 * @return
	 */
	public static PB_WorkingMessage createPB_WorkMsg(POS_DK dkPump) {
		PB_WorkingMessage pbWorkMsg = new PB_WorkingMessage() ;
		pbWorkMsg.setNozzleNo(dkPump.getDeviceID()) ;
		pbWorkMsg.setCommandSet(dkPump.getCommand()) ;
		
		//==>> soon 20211104 OWIN ����(2)/����(3) �߰�
		String command = dkPump.getCommand();
		
		//LogUtility.getLogger().info("[BEACON TEST] dkPump.getDeviceType() : " + dkPump.getDeviceType());
		//LogUtility.getLogger().info("[BEACON TEST] command : " + command);
		
		// ���� �����㰡 ��û�� ��� �α� ���
		if(command.equals(ICode.PRESET_QTY_PRC_IND_OWIN_PRICE) || command.equals(ICode.PRESET_QTY_PRC_IND_OWIN_LITER)) {
			LogUtility.getLogger().info("[Pump M] <Beacon Process> Received POS_DK Messaged ����(2)/����(3) : [" + command +"]");
		}
		
		// ����/���� ��û�� ���� ����
		if(command.equals(ICode.PRESET_QTY_PRC_IND_0_PRICE)||command.equals(ICode.PRESET_QTY_PRC_IND_OWIN_PRICE) ) {
			pbWorkMsg.setPrice(dkPump.getPrice());
		} else {
			pbWorkMsg.setLiter(PumpMUtil.convertNumberFormatFromPOSToPump(dkPump.getLiter(),4,3)) ;
		}
		
		/*
		if (dkPump.getCommand().equals(ICode.PRESET_QTY_PRC_IND_0_PRICE)) {
			pbWorkMsg.setPrice(dkPump.getPrice()) ;
		} else {
			pbWorkMsg.setLiter(PumpMUtil.convertNumberFormatFromPOSToPump(dkPump.getLiter(),4,3)) ;
		}
		*/
		
		pbWorkMsg.setMessageID(dkPump.getMessageID()) ;
		pbWorkMsg.setBasePrice(PumpMUtil.convertNumberFormatFromPOSToPump(dkPump.getPreset_baesPrice(),4,2)) ;
				
		return pbWorkMsg ;
	}
	
	/**
	 * ������� ���� ��û�� �������� ��ȣ ��ȸ�� ���� ������ �����Ѵ�.
	 * 
	 * @param duMsg		: POS �� ���� ���� ���� ���� ��ȣ ��ȸ ���
	 * @return
	 */
	public static PF_WorkingMessage createPFWorkingMessage(POS_DU duMsg) {
		Vector<PF_CarInfo> carInfoVector = new Vector<PF_CarInfo>() ;
		POS_DU_Car[] carInfoArray = duMsg.getCarInfoArray() ;
		
		for (int i = 0 ; i < carInfoArray.length ; i++) {
			POS_DU_Car duCar = carInfoArray[i] ;			
			PF_CarInfo pfCarInfo = 
				new PF_CarInfo(duCar.getCar_no(), duCar.getCust_card_no(),duCar.getCust_name()) ;
			carInfoVector.add(pfCarInfo) ;
		}
		PF_WorkingMessage pfWorkMsg = new PF_WorkingMessage(duMsg.getDeviceID(),
				duMsg.getDeviceID(),
				carInfoVector) ;

		return pfWorkMsg;
	}
	
	

	/**
	 * POS �� �����ϱ����� Preamble Object ����
	 * 
	 * @param key		: Unique Key
	 * @param dest		: ������ (POS)
	 * @param message	: ������ ����
	 * @param log		:
	 * @return
	 * 		Preamble Object
	 */
	public static Preamble createPOSMessagePreamble(String key, int dest, POSMessage message , String log) {
		int from = SyncManager.DISE_PUMP_MODULE ;
		byte[] preamble = message.convertPOSContent() ;
		
		// Debug Purpose
		PumpLogUtil.printContent(key, from, dest, message) ;
		
		return Preamble.createPreamble(key, from, dest, preamble, log) ;
	}
	
	
	/**
	 * POS �� ���� ���� DN ������ ������� �����ϱ� ���ؼ� PQ_WorkingMessage Object �� �����Ѵ�.
	 * 
	 * @param dnMsg	: DN ����
	 * @return
	 */
	public static PQ_WorkingMessage createPQ_WorkMsg( String messageID, String nozzle_no,POS_DN dnMsg) {
		PQ_WorkingMessage pbWorkMsg = new PQ_WorkingMessage() ;
		pbWorkMsg.setNozzleNo(nozzle_no) ;
		pbWorkMsg.setMessageID(messageID) ;
		if ((dnMsg != null) && (dnMsg.getKeepNumberExist().equals("1"))) {
			pbWorkMsg.setMode("0") ;
			pbWorkMsg.setLiter(PumpMUtil.convertNumberFormatFromPOSToPump(dnMsg.getKeepLiter(),4,3)) ;
			pbWorkMsg.setUp(PumpMUtil.convertNumberFormatFromPOSToPump(dnMsg.getBasePrice(),4,2)) ;
		} else {
			pbWorkMsg.setMode("1") ;		
			pbWorkMsg.setLiter("") ;
			pbWorkMsg.setUp("") ;
		}		
		return pbWorkMsg ;
	}
	
	/**
	 * POS �� ���� ���� CX ������ PU_WorkingMessage Object �� ��ȯ�Ѵ�.
	 * 2019.08 SoonKwan
	 * @param cxPump
	 * @return
	 */
	public static PU_WorkingMessage createPU_WorkMsg(POS_CX cxPump) { 
		PU_WorkingMessage puWorkMsg = new PU_WorkingMessage() ;
		puWorkMsg.setNozzleNo(cxPump.getDeviceID()) ;
		//puWorkMsg.setNozzleState(cxPump.getCommand()) ;
		//puWorkMsg.setMessageID(cxPump.getMessageID()) ;
		return puWorkMsg ;
	}
	
	/**
	 * POS �� �����ϱ����� Preamble Object ����
	 * 
	 * @param key		: Unique Key
	 * @param dest		: ������ (POS)
	 * @param message	: ������ ����
	 * @param log		:
	 * @return
	 * 		Preamble Object
	 */
	public static Preamble createPreamble(String key, int dest, POSHeader message , String log) {
		int from = SyncManager.DISE_PUMP_MODULE ;
		byte[] preamble = null ;
		
		if (message instanceof POSMessage)
			preamble = ((POSMessage)message).convertPOSContent() ;
		else
			preamble = message.convertHeaderToPOSContentWithoutDataLength() ;
		
		// Debug Purpose
		PumpLogUtil.printContent(key, from, dest, message) ;
		
		return Preamble.createPreamble(key, from, dest, preamble, log) ;
	}
	
	/**
	 * �����⸸ �ʱ�ȭ �ϱ� ���� ������ �����Ѵ�.
	 * 
	 * @param storeCode	: ���� �ڵ�
	 * @param nozzle_no	: ���� ��ȣ (null �� ��� ��ü ������)
	 * @return
	 */
	public static ArrayList<WorkingMessage> createPumpInitWorkingMessage(String storeCode, String nozzle_no) {
		ArrayList<WorkingMessage> workMsgList = new ArrayList<WorkingMessage>() ;
		try {
			WorkingMessage p3WorkMsg = PumpMUtil.getP3WorkingMessage(storeCode, nozzle_no) ;
			if (p3WorkMsg != null) {
				LogUtility.getPumpMLogger().debug("[Pump M] Create P3") ;
				p3WorkMsg.setMessageID(GlobalUtility.getUniqueMessageID()) ;
				workMsgList.add(p3WorkMsg) ;
			} else {
//				LogUtility.getPumpMLogger().debug("[Pump M] �����Ⱑ ���, P3 ������ ������ ���� �����ϴ�.") ;
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		return workMsgList ;	
	}
	
	/**
	 * �������� �ڷḦ �����Ѵ�.
	 * 	- T_KH_PUMP_TR �� �������� ������ �����Ѵ�.
	 * 
	 * @param sjWorkMsg	: Pump A �� ���͹��� ���� ���� ����
	 * @return
	 */
	public static String createPumpStartContent(SJ_WorkingMessage sjWorkMsg) {
		String khproc_no = null ;
	    try {
	    	int nozzle_noInt = Integer.parseInt(sjWorkMsg.getNozzleNo()) ;
	    	
    	    if (PumpMTransactionManager.getInstance().isSameState(sjWorkMsg.getNozzleNo(),IPumpConstant.KH_PUMP_START)) {
    			LogUtility.getPumpMLogger().info("[Pump M] Same state(KH_PUMP_START). update Creation Time only") ;
	    	    khproc_no = PumpMTransactionManager.getInstance().getKHTransactionID(sjWorkMsg.getNozzleNo()) ;	    	    	  
	    	    T_KH_PUMP_TRHandler.getHandler().updateCreationTime(khproc_no) ;		// ���� ���� �ð��� Update
    		} else {
	    	    khproc_no = PumpMTransactionManager.getInstance().getKHTransactionID(sjWorkMsg.getNozzleNo(),IPumpConstant.KH_PUMP_START) ;	    	    	  
	    	    String startTotalGuage = PumpMUtil.convertNumberFormatFromPumpToPOS(sjWorkMsg.getTotalGauge() , 3 ,11) ;

	    	    T_KH_PUMP_TRHandler.getHandler().updatePumpStartInfo_BY_khproc_no(khproc_no, startTotalGuage) ;
	    	    PumpMPriceManager.setTotalGauage(nozzle_noInt, startTotalGuage) ;							    	    	  
	    		LogUtility.getPumpMLogger().info("[Pump M] insert pumping start info into Table, nozID=" + sjWorkMsg.getNozzleNo()) ;
	    		
	    		// �� �����ǿ� ���� �ܰ��� �����Ѵ�.
	    		PumpMPriceManager.setBasePrice(nozzle_noInt, 
	    				Double.parseDouble(PumpMUtil.getBasePriceFromKHProcNo(sjWorkMsg.getNozzleNo(), khproc_no))) ;
    		}
    	    
    		// 2009�� 10�� 5�� �߿��� �߰�.
    		GlobalUtility.printAnalysisLog(	"pumpM", 
    				khproc_no, 
    				sjWorkMsg.getNozzleNo(), 
    				"",	
    				"��������", 
    				"", 
    				new String("TOTAL GAUGE : " + sjWorkMsg.getTotalGauge()).getBytes(), 
    				"", 
    				"", 
    				"");
    			
    	    
	    } catch (Exception e) {
	    	LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
	    }
	    return khproc_no ;
	}
	
	/**
	 * POS �� ���� ���� DO ������ QF_WorkingMessage Object �� ��ȯ�Ѵ�.
	 * 
	 * @param doPump	: POS �� ���� ���� DO ����
	 * @return
	 */
	public static QF_WorkingMessage createQF_WorkMsg(POS_DO doPump) {
		QF_WorkingMessage gfWorkMsg = new QF_WorkingMessage() ;
		gfWorkMsg.setNozzleNo(doPump.getDeviceID()) ;
		gfWorkMsg.setMessageID(doPump.getMessageID()) ;
		return gfWorkMsg ;
	}
	
	/**
	 * �����⸸ �ʱ�ȭ �ϱ� ���� ������ �����Ѵ�.
	 * 
	 * @param storeCode	: ���� �ڵ�
	 * @param rechargeodt_no	: ������ ��ȣ (null �� ��� ��� ������)
	 * @return
	 */
	public static ArrayList<WorkingMessage> createRechargeODTInitWorkingMessage(String storeCode, String rechargeodt_no) {
		ArrayList<WorkingMessage> workMsgList = new ArrayList<WorkingMessage>() ;
		try {
			WorkingMessage d0WorkMsg = PumpMUtil.getD0WorkingMessage(storeCode , rechargeodt_no) ;
			if (d0WorkMsg != null) {
				LogUtility.getPumpMLogger().debug("[Pump M] Create D0 for Recharging ODT") ;
				d0WorkMsg.setMessageID(GlobalUtility.getUniqueMessageID()) ;
				workMsgList.add(d0WorkMsg) ;
			} else {
				LogUtility.getPumpMLogger().debug("[Pump M] ������ ODT�� ���, D0 ������ ������ �ʽ��ϴ�.") ;
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		return workMsgList ;
	}
	
	/**
	 * ���� �����⸸ �ʱ�ȭ �ϱ� ���� ������ �����Ѵ�.
	 * 
	 * @param storeCode	: ���� �ڵ�
	 * @param selfodt_no	: ���� ODT ��ȣ (null �� ��� ��� ���� ODT)
	 * @return
	 */
	public static ArrayList<WorkingMessage> createSelfODTInitWorkingMessage(String storeCode, String selfodt_no) {
		ArrayList<WorkingMessage> workMsgList = new ArrayList<WorkingMessage>() ;
		try {
			WorkingMessage p1WorkMsg = PumpMUtil.getP1WorkingMessage(storeCode, selfodt_no) ;
			if (p1WorkMsg != null) {
				LogUtility.getPumpMLogger().debug("[Pump M] Create P1 for Self ODT") ;
				p1WorkMsg.setMessageID(GlobalUtility.getUniqueMessageID()) ;
				workMsgList.add(p1WorkMsg) ;
			} else {
//				LogUtility.getPumpMLogger().debug("[Pump M] ���� ODT�� ���, P1 ������ ������ ���� �����ϴ�.") ;
			}
				
			WorkingMessage p2WorkMsg = PumpMUtil.getP2WorkingMessage(storeCode, selfodt_no) ;
			if (p2WorkMsg != null) {
				LogUtility.getPumpMLogger().debug("[Pump M] Create P2 for Self ODT") ;
				p2WorkMsg.setMessageID(GlobalUtility.getUniqueMessageID()) ;
				workMsgList.add(p2WorkMsg) ;
			} else {
//				LogUtility.getPumpMLogger().debug("[Pump M] ���� ODT�� ���, P2 ������ ������ ���� �����ϴ�.") ;
			}
			
			WorkingMessage p5WorkMsg = PumpMUtil.getP5WorkingMessage(storeCode, selfodt_no) ;			
			if (p5WorkMsg != null) {
				LogUtility.getPumpMLogger().debug("[Pump M] Create P5 for Self ODT") ;
				p5WorkMsg.setMessageID(GlobalUtility.getUniqueMessageID()) ;
				workMsgList.add(p5WorkMsg) ;
			} else {
//				LogUtility.getPumpMLogger().debug("[Pump M] ���� ODT �� ���, P5 ������ ������ ���� �����ϴ�.") ;
			}
			
			WorkingMessage pcWorkMsg = PumpMUtil.getPCWorkingMessage(storeCode, selfodt_no) ;			
			if (pcWorkMsg != null) {
				LogUtility.getPumpMLogger().debug("[Pump M] Create PC for Self ODT") ;
				pcWorkMsg.setMessageID(GlobalUtility.getUniqueMessageID()) ;
				workMsgList.add(pcWorkMsg) ;
			}
			
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		return workMsgList ;
	}
	
	/**
	 * ���� ODT PC ���� ȯ�� ���� ���� ��û
	 * ���� ȯ�漳�������� ����
	 * 
	 * @param storeCode : ���� �ڵ�
	 * @param odt_no : null �� ��� ��� ODT �̰�, Ư�� ��ȣ�� ��� �� ��ȣ��.
	 * @return : PC WorkingMessage
	 * @throws Exception
	 */
	private static PC_WorkingMessage getPCWorkingMessage(String storeCode, String selfodt_no) {
		PC_WorkingMessage pCWorkingMsg = new PC_WorkingMessage();
		Vector<PC_StoreCodeInfo> storeCodeInfoVector = new Vector<PC_StoreCodeInfo>() ;		
		Vector<String> odtIdVector = new Vector<String>() ;
		
		try {
			//���հ�����뱸�� - 0: �̻��, 1: ��ü���, 2:�ٷ��������, 3:����CarPay���
			String code_7113 = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_7113);
			
			//���ݿ����� �ǹ����� ��뿩�� - 0:�̻��, 1:���
			String code_7115 = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_7115);
			
			if(code_7113 != null && !"".equals(code_7113)) { 		
				PC_StoreCodeInfo storeCodeInfo = new PC_StoreCodeInfo();
				storeCodeInfo.setCode(IConstant.POSPORTOCOL_CODEMASTER_7113);
				storeCodeInfo.setValue(code_7113);			
				
				storeCodeInfoVector.add(storeCodeInfo);
			}
			
			if(code_7115 != null && !"".equals(code_7115)) {
				PC_StoreCodeInfo storeCodeInfo = new PC_StoreCodeInfo();
				storeCodeInfo.setCode(IConstant.POSPORTOCOL_CODEMASTER_7115);
				storeCodeInfo.setValue(code_7115);			
				
				storeCodeInfoVector.add(storeCodeInfo);
			}
			
			if (selfodt_no == null) {
				T_NZ_NOZZLEData[] nozzleDataList = 
					T_NZ_NOZZLEHandler.getHandler().getT_NZ_NOZZLEDataBySelfIndExist(ICode.SELF_IND_EXIST_05_ODT_SELF) ;
				if ((nozzleDataList == null) || (nozzleDataList.length == 0)){
					return null ;
				}
				for (int i = 0 ; i < nozzleDataList.length ; i++) {
					odtIdVector.add(nozzleDataList[i].getNozzle_no()) ;	// Self ODT ��ȣ
				}
			} else {
				odtIdVector.add(selfodt_no) ;
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		}
		
		LogUtility.getPumpMLogger().debug("[������� ������Ʈ] Integer.toString(storeCodeInfoVector.size()): "+ Integer.toString(storeCodeInfoVector.size())) ;
		
		for(PC_StoreCodeInfo storeCodeValue : storeCodeInfoVector) {
			LogUtility.getLogger().debug("[Pump M] ���� ȯ�漳�� �ڵ����� : " + storeCodeValue.getCode());
			LogUtility.getLogger().debug("[Pump M] ���� ȯ�漳�� �ڵ����� : " + storeCodeValue.getValue());
		}
		
		pCWorkingMsg.setOdtId(odtIdVector) ;	
		pCWorkingMsg.setStoreCodeCount(Integer.toString(storeCodeInfoVector.size()));
		pCWorkingMsg.setStoreCodeInfo(storeCodeInfoVector);
		
		return pCWorkingMsg;
	}

	/**
	 * added by yhcheon at 2009.05.18
	 * ���� ������ ��� �ʱ�ȭ �ϴ� �Լ� ( P1, P2 )
	 * 
	 * @param storeCode	: ���� �ڵ�
	 * @param selfodt_no	: ���� ODT ��ȣ (null �� ��� ��� ���� ODT)
	 * @return
	 */
	public static ArrayList<WorkingMessage> createSelfODTInitWorkingMessage2(String storeCode, String selfodt_no) {
		ArrayList<WorkingMessage> workMsgList = new ArrayList<WorkingMessage>() ;
		try {
			WorkingMessage p1WorkMsg = PumpMUtil.getP1WorkingMessage(storeCode, selfodt_no) ;
			if (p1WorkMsg != null) {
				LogUtility.getPumpMLogger().debug("[Pump M] Create P1 for Self ODT") ;
				p1WorkMsg.setMessageID(GlobalUtility.getUniqueMessageID()) ;
				workMsgList.add(p1WorkMsg) ;
			} else {
//				LogUtility.getPumpMLogger().debug("[Pump M] ���� ODT�� ���, P1 ������ ������ ���� �����ϴ�.") ;
			}
				
			WorkingMessage p2WorkMsg = PumpMUtil.getP2WorkingMessage(storeCode, selfodt_no) ;
			if (p2WorkMsg != null) {
				LogUtility.getPumpMLogger().debug("[Pump M] Create P2 for Self ODT") ;
				p2WorkMsg.setMessageID(GlobalUtility.getUniqueMessageID()) ;
				workMsgList.add(p2WorkMsg) ;
			} else {
//				LogUtility.getPumpMLogger().debug("[Pump M] ���� ODT�� ���, P2 ������ ������ ���� �����ϴ�.") ;
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		return workMsgList ;
	}
	
	/**
	 * POS �� ���� ���� DB ������ T_NZ_INFO Table �� �ֱ� ���ؼ� T_NZ_INFOData Object �� ��ȯ�Ѵ�.
	 * 
	 * @param data	: POS �� ���� ���� DB ����
	 * @return
	 */
	public static T_NZ_INFOData createT_NZ_INFOData(POS_DB data) {
		T_NZ_INFOData infoData = new T_NZ_INFOData() ;
		
		infoData.setStore_code(data.getStore_code()) ;
		infoData.setSa_min_amt(data.getSa_min_amt()) ;
		infoData.setMin_sale_price(data.getMin_sale_liter()) ;
		infoData.setLra_output_yn(data.getLra_output_yn()) ;
		infoData.setSe_publish_base(data.getSe_publish_base()) ;
		infoData.setSe_publish_base_won((data.getSe_publish_base_won())) ;
		infoData.setSc_wait_time(data.getSc_wait_time()) ;		
		infoData.setEmer_stop_lt(data.getEmer_stop_lt()) ;
		infoData.setTitle_txt_head(data.getTitle_txt_head()) ;
		infoData.setTitle_txt_foot1(data.getTitle_txt_foot1()) ;
		infoData.setTitle_txt_foot2(data.getTitle_txt_foot2()) ;
		infoData.setSave_expire_date(data.getSave_expire_date()) ;
		infoData.setSave_head_title(data.getSave_head_title()) ;
		infoData.setSave_foot_title1(data.getSave_foot_title1()) ;
		infoData.setSave_foot_title2(data.getSave_foot_title2()) ;
		infoData.setCc_head_title(data.getCc_head_title()) ;
		infoData.setCc_foot_title1(data.getCc_foot_title1()) ;
		infoData.setCc_foot_title2(data.getCc_foot_title2()) ;
		infoData.setBc_head_title(data.getBc_head_title()) ;
		infoData.setBc_foot_title1(data.getBc_foot_title1()) ;
		infoData.setBc_foot_title2(data.getBc_foot_title2()) ;
		infoData.setSpi_head_title(data.getSpi_head_title()) ;
		infoData.setSpi_foot_title1(data.getSpi_foot_title1()) ;
		infoData.setSpi_foot_title2(data.getSpi_foot_title2()) ;
		infoData.setTitle_txt_footreport(data.getTitle_txt_footreport()) ;

		return infoData ;
	}
		
	
	

	/**
	 * POS �� ���� ���� DD ���� �� ���� ������ T_NZ_NOZZLEData Object �� ��ȯ�Ѵ�.
	 * 
	 * @param data	: POS �� ���� ���� DD �������� ���� ����
	 * @return
	 */
	public static T_NZ_NOZZLEData createT_NZ_NOZZLEData(POS_DD_NozzleInfo data) {
		T_NZ_NOZZLEData nozzleData = new T_NZ_NOZZLEData() ;
		
		nozzleData.setNozzle_no(data.getNozzle_no()) ;
		nozzleData.setSelf_ind_exist(data.getSelf_ind_exist()) ;
		nozzleData.setUse_code_protocol(data.getUse_code_protocol()) ;
		nozzleData.setStore_code(data.getStore_code()) ;
		if ((data.getOdtno_no_connected() == null) || (data.getOdtno_no_connected().equals("")) || 
				(data.getOdtno_no_connected().equals("0"))) {
			nozzleData.setOdtno_no_connected("00") ;
		} else {
			nozzleData.setOdtno_no_connected(data.getOdtno_no_connected()) ;
		}
		nozzleData.setGoods_code(data.getGoods_code()) ;
		nozzleData.setPos_no(data.getPos_no()) ;
		nozzleData.setTank_no(data.getTank_no()) ;
		nozzleData.setDboxrelport_no(data.getDboxrelport_no()) ;
		nozzleData.setRom_version(data.getRom_version()) ;
		nozzleData.setBaud_rate(data.getBaud_rate()) ;
		
		return nozzleData ;
	}
	
	
	
	/**
	 * POS �� ���� ���� KH ���� (Time Parameter) �� ���̺� �����ϱ� ���ؼ� Internal Object (T_NZ_PARAMETERData) �� ��ȯ�Ѵ�.
	 * 
	 * @param info	: KH ������ NozzleInfo ������
	 * @return
	 */
	public static T_NZ_PARAMETERData createT_NZ_PARAMETERData(POS_KH_NozzleInfo info) {
		return new T_NZ_PARAMETERData(info.getNozzle_no(),
				info.getStore_code(),
				info.getReadTimeInterval(),
				info.getReadWaitInterval(),
				info.getWriteWaitInterval(),
				info.getLineErrorCount(),
				info.getLineErrorSkipCount()) ;
	}
	
	/**
	 * CAT M ���� �����ϱ����� Preamble Object ����
	 * 
	 * @param key		: Unique Key
	 * @param dest		: ������ (CAT M or CMS M)
	 * @param message	: ������ ����
	 * @param log		:
	 * @return
	 * 		Preamble Object
	 */
	public static Preamble createUPOSMessagePreamble(String key, int from, int dest, UPOSMessage uPosMsg , String log) {
		
		// Debug Purpose
//		PumpLogUtil.printContent(key, from, dest, uPosMsg) ;
		
		return Preamble.createPreamble(key ,from, dest, uPosMsg , "") ;
    }
	
	/**
	 * ������� �����ϱ����� Preamble Object ����
	 * 
	 * @param key		: Unique Key
	 * @param dest		: ������ (Pump A)
	 * @param message	: ������ ����
	 * @param log		:
	 * @return
	 * 		Preamble Object
	 */
	public static Preamble createWorkingMessagePreamble(String key, int dest, WorkingMessage message , String log) {
		int from = SyncManager.DISE_PUMP_MODULE ;

		// Debug Purpose
		PumpLogUtil.printContent(key, from, dest, message) ;
		
		return Preamble.createPreamble(key, from, dest , message , log) ;
	}
	
	/**
	 * ��ǰ �ܰ��� ���� �´�.
	 * 
	 * @param goods_code		: ��ǰ �ڵ�
	 * @param self_ind_exist	: ������ ���� Ÿ��
	 * @return
	 */
	public static String getBasePrice(String goods_code , String self_ind_exist) {
		String basePrice = null ;		
		try {
			T_KH_PRODUCTData productData = 
				T_KH_PRODUCTHandler.getHandler().getT_KH_PRODUCTData(goods_code) ;
			
			basePrice = getBasePrice(productData, self_ind_exist) ;
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage());
		} 
		return basePrice;
	}
	
	
	/**
	 * �����⿡ �´� ��ǰ �ܰ��� ���� �´�. Self/Semi-Self �������� ��� �����ܰ���, �� �ܴ� ���ΰ��� �����Ѵ�.
	 * 
	 * @param productData	: ��ǰ ������
	 * @param nzNozzleData	: ���� ������
	 * @return
	 */
	public static String getBasePrice(T_KH_PRODUCTData productData , String self_ind_exist) {
		String basePrice = null ;
//		if (self_ind_exist.equals(IPumpConstant.DEVICE_TYPE_SEMI_SELF) ||
//				self_ind_exist.equals(IPumpConstant.DEVICE_TYPE_SELF_PUMP) ||
//				self_ind_exist.equals(IPumpConstant.DEVICE_TYPE_ODT_SELF))
		if (self_ind_exist.equals(ICode.SELF_IND_EXIST_03_SEMI_SELF) ||
				self_ind_exist.equals(ICode.SELF_IND_EXIST_02_SELF_PUMP)) {
			basePrice = productData.getUnitprc_amt_self() ;	// ���� �ܰ�
			//���� �ܰ��� 0�̸� ���ΰ� ����
			if ( basePrice == null){
				basePrice = productData.getShopfrontprc_amt() ; // ���ΰ�
			} else {
				if ( GlobalUtility.getPositiveValue(basePrice).equals("0")) {
					basePrice = productData.getShopfrontprc_amt() ; // ���ΰ�
				}
			}
		} else
			basePrice = productData.getShopfrontprc_amt() ; // ���ΰ�		
		return basePrice ;
	}
	
	/**
	 * �����⿡ �´� ��ǰ �ܰ��� ���� �´�. Self/Semi-Self �������� ��� �����ܰ���, �� �ܴ� ���ΰ��� �����Ѵ�.
	 * 
	 * @param productData	: ��ǰ ������
	 * @param nzNozzleData	: ���� ������
	 * @return
	 */
	public static String getBasePrice(T_KH_PRODUCTData productData , T_NZ_NOZZLEData nzNozzleData) {
		String self_ind_exist = nzNozzleData.getSelf_ind_exist() ;
		return getBasePrice(productData, self_ind_exist) ;
	}	
	
	/**
	 * Ư�� �����ǿ� ���ؼ� �� �����ǿ� ���� �ܰ��� ���Ѵ�.
	 * �̴� ���ΰ��ϼ��� �ְ�, ���ε� �ܰ��ϼ��� �ִ�.
	 * 
	 * @param nozzle_no	: ���� ��ȣ
	 * @param khproc_no	: KH ó����ȣ
	 * @return
	 */
	public static String getBasePriceFromKHProcNo(String nozzle_no, String khproc_no) {
		String basePrice = null ;
		try {
			T_KH_PUMP_TRData trData = T_KH_PUMP_TRHandler.getHandler().getT_KH_PUMP_TRData(khproc_no) ;
			if (trData.getPreset_qty_prc_ind().equals(ICode.PRESET_QTY_PRC_IND_1_LITER)) {
				basePrice = trData.getPreset_baseprice() ;
			} else {
				basePrice = trData.getBaseprice() ;
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		
		try {
			if ((basePrice == null) || (basePrice.equals("0"))) {
				T_NZ_NOZZLEData tNzNozData = T_NZ_NOZZLEHandler.getHandler().getT_NZ_NOZZLEDataByNozzleNo(nozzle_no) ;
				T_KH_PRODUCTData productData = 
					T_KH_PRODUCTHandler.getHandler().getT_KH_PRODUCTData(tNzNozData.getGoods_code()) ;
				basePrice = getBasePrice(productData, tNzNozData) ; ;
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		return basePrice ;
	}

	/**
	 * ������ �ܰ��� ��û�Ѵ�.
	 * 
	 * @param nozzle_no	: ���� ��ȣ
	 * @return
	 */
	public static String getBasePriceFromNozzleNo(String nozzle_no) {
		String basePrice = null ;
		try {
			T_NZ_NOZZLEData tNzNozData = T_NZ_NOZZLEHandler.getHandler().getT_NZ_NOZZLEDataByNozzleNo(nozzle_no) ;
			T_KH_PRODUCTData productData = 
				T_KH_PRODUCTHandler.getHandler().getT_KH_PRODUCTData(tNzNozData.getGoods_code()) ;
			basePrice = getBasePrice(productData, tNzNozData) ; ;
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		return basePrice ;
	}
	
	/**
	 * ��ǰ �ܰ��� ��û�Ѵ�.
	 * 
	 * @param unitprc_code_stdn
	 * 		1 : ���ΰ�
	 * 		2 : �����ܰ�
	 * 		3 : �鼼�ܰ�
	 * 		4 : ��ǥ��
	 * 		5 : ���Դܰ�
	 * @param productData	: ��ǰ����
	 * @return
	 */
	public static String getBasePriceWithUnitPrcCode(String unitprc_code_stdn, T_KH_PRODUCTData productData) {
		String unitPrice = null ;
		switch (unitprc_code_stdn) {
			case ICode.UNITPRC_CODE_STDN_01 :	// ���ΰ�
				unitPrice = productData.getShopfrontprc_amt() ;
				break ;
			case ICode.UNITPRC_CODE_STDN_02 :	// ���� �ܰ�
				unitPrice = productData.getUnitprc_amt_self() ;
				break ;		
			case ICode.UNITPRC_CODE_STDN_03 :	// �鼼�ܰ�
				unitPrice = productData.getPrc_amt_taxfree() ;
				break ;
			case ICode.UNITPRC_CODE_STDN_04 : {	// ��ǥ��
				try {
					unitPrice = T_KH_PRC_AMT_GOALHandler.getHandler().getPrc_amt_goal(productData.getGoods_code()) ; 
				} catch (Exception e) {
					LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
				}
				break ;
			}
			case ICode.UNITPRC_CODE_STDN_05 :	// ���Դܰ�
				unitPrice = productData.getPrc_amt_saip() ;
				break ;
		}
		
		if ((unitPrice == null) || unitPrice.equals("") || unitPrice.equals("0")) {
			LogUtility.getPumpMLogger().warn("[Pump M] �ܰ� ����( " + unitprc_code_stdn + ") �� �������� �ʽ��ϴ�." +
					" �̴� CAT �ܸ��� UI �� ������ ó���ϵ��� �����մϴ�.") ;
			unitPrice = null ;
		}
		return unitPrice ;
	}
	
	/**
	 * ī�� ��ȣ���� �ռ� 16 �ڸ� ���ϱ�
	 * 
	 * @param cardNumber	: ī�� ��ȣ
	 * @return
	 */
	public static String getCardNumberPre16Length(String cardNumber) {
		return GlobalUtility.getStringWithSpecificStringLength(cardNumber, 16) ;
	}
	

	/**
	 * ���� ��ȣ�� ����Ǿ� �ִ� ODT �� Protocol �� �����Ѵ�.
	 * 
	 * @param nozzleNo	: ���� ��ȣ
	 * @return
	 */
	public static int getConnectedODTProtocolFromNozzleNo(String nozzleNo) {
		int protocolInt = IPumpConstant.PUMP_PROTOCOL_DEFAULT ;
		try {
			// ���� Ÿ���� ���� ���� (POS ���� ODT ��ȣ�� ������ �� �ִ� ������ �߻��� �� �ֱ� ����.
			String self_ind_exist = T_NZ_NOZZLEHandler.getHandler().getSelf_ind_exist(nozzleNo) ;
			if (self_ind_exist.equals(ICode.SELF_IND_EXIST_01_PUMP) || self_ind_exist.equals(ICode.SELF_IND_EXIST_03_SEMI_SELF) || 
					self_ind_exist.equals(ICode.SELF_IND_EXIST_05_ODT_SELF)) {
				return protocolInt ;
			}
						
			String odtID = getODTNumberFromNozzleNo(nozzleNo) ;
			if ((odtID == null) || (odtID.equals("") || (odtID.equals("00")))) {
				return IPumpConstant.PUMP_PROTOCOL_DEFAULT ;
			}

			String odtProtocol = T_NZ_NOZZLEHandler.getHandler().getUse_code_protocolByNozzle_no(odtID);
			
			protocolInt = Integer.parseInt(odtProtocol) ;

		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
			protocolInt = IPumpConstant.PUMP_PROTOCOL_DEFAULT ;
		}
		return protocolInt ;
	}
	
	/**
	 * ���� ��ȣ�� ����Ǿ� �ִ� ODT �� Protocol �� �����Ѵ�.
	 * 
	 * @param nozzleNo	: ���� ��ȣ
	 * @return
	 */
	public static int getConnectedRepODTProtocolFromNozzleNo(String nozzleNo) {
		int protocolInt = IPumpConstant.PUMP_PROTOCOL_DEFAULT ;
		try {
			
			int protocolTypeInt = getConnectedODTProtocolFromNozzleNo(nozzleNo) ;
			
			switch (protocolTypeInt) {
				case 30 :
				case 31 :
				case 90 : {
					protocolInt = IPumpConstant.PUMP_PROTOCOL_DaSNo ;
					break ;
				}
				case 40 :
				case 41 :
				case 55 :{	// ��ȭ ������ ���� �߰� (�߿���, 2009.01.06)
					protocolInt = IPumpConstant.PUMP_PROTOCOL_SOMO ;
					break ;
				}
				case 75 : {
					protocolInt = IPumpConstant.PUMP_PROTOCOL_Recharge ;
					break ;
				}
				
				// TCP/IP ��Ź�� ������ODT(76) �߰�  ������, 2016.04-21
				case 76 : {
					protocolInt = IPumpConstant.PUMP_PROTOCOL_NewRecharge ;
					break ;
				}
					
				// 2012.09.21 ksm �����پ��� �߰�
				case 37: {
					protocolInt = IPumpConstant.PUMP_PROTOCOL_NewDaSNo;
					break;
				}
				// 2016.03.18 CWI ����gsc �߰�
				case 91:
				case 92: {
					protocolInt = IPumpConstant.PUMP_PROTOCOL_GSC_SELF;
					break;
				}
				default : {
					protocolInt = IPumpConstant.PUMP_PROTOCOL_DEFAULT ;
					break ;
				}		
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
			protocolInt = IPumpConstant.PUMP_PROTOCOL_DEFAULT ;
		}
		return protocolInt ;
	}

	/**
	 * [2008.11.28] by WooChul Jung
	 * 
	 * Ư�� �������� ��� �ܰ��� ���۵��� �ʴ´�. �̷� ���� ���� �����Ϸ��� �ܰ��� 000000 Ȥ�� 999999 �� ���� 
	 * Pump M ���� �����ϰ� �ִ� �ܰ��� �̿��Ͽ� POS �� �����Ѵ�.
	 * 
	 * @param nozzleNo		: ���� ��ȣ
	 * @param basePrice		: ������� ���� ���� �ܰ� (Pump A �κ��� ���۵Ǿ��� �ܰ�)
	 */
	public static String getConvertBasePrice(String nozzleNo, String basePrice) {
		String reBasePrice = "" ;
		try {
			if ((basePrice == null) || ("999999".equals(basePrice)) || ("000000".equals(basePrice))) {
				reBasePrice  = PumpMUtil.convertNumberFormatFromPumpToPOS(
						PumpMPriceManager.getCurrBasePriceForPumpA(Integer.parseInt(nozzleNo)),2,11) ;
			} else {
				reBasePrice = PumpMUtil.convertNumberFormatFromPumpToPOS(basePrice,2,11) ;
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
			reBasePrice = PumpMUtil.convertNumberFormatFromPumpToPOS(basePrice,2,11) ;
		}
		return reBasePrice ;
	}
	
	/**
	 * ������ ODT D0 ȯ�� ���� ���� ��û
	 * 
	 * @param storeCode : ���� �ڵ�
	 * @param odt_no 	: null �� ��� ��� �����⿡ ���� ���� ����
	 * @return
	 * @throws Exception
	 */
	public static D0_WorkingMessage getD0WorkingMessage(String storeCode, String odtID) throws Exception {
		D0_WorkingMessage d0WorkingMsg = new D0_WorkingMessage() ;
		T_KH_STOREData storeData = T_KH_STOREHandler.getHandler().getT_KH_STOREDataByStoreCode(storeCode) ;
		T_NZ_INFOData nzInfoData = T_NZ_INFOHandler.getHandler().getT_NZ_INFODataByStoreCode(storeCode) ;
		
		ArrayList<String> odtDataArray  = null ;
		if (odtID == null) {
			odtDataArray = 
				T_NZ_NOZZLEHandler.getHandler().getRechargeODTNoList() ;
		} else {
			odtDataArray = new ArrayList<String>() ;
			odtDataArray.add(odtID) ;
		}
		
		if ((odtDataArray == null) || (odtDataArray.size() == 0)) {
			return null ;
		}
		
		T_NZ_NOZZLEData[] nzDataArray = T_NZ_NOZZLEHandler.getHandler().getT_NZ_NOZZLEDataByOdtno_no_connected(odtDataArray.get(0)) ;
		String goods_code = nzDataArray[0].getGoods_code() ;
		String repOdtID = nzDataArray[0].getNozzle_no() ;
//		String repOdtID = nzDataArray[0].getOdtno_no_connected() ;
		T_KH_PRODUCTData productData = T_KH_PRODUCTHandler.getHandler().getT_KH_PRODUCTData(goods_code) ;
		
		d0WorkingMsg.setNozzleNo(repOdtID) ;
		d0WorkingMsg.setStoreCode(storeCode) ;         //���� �ڵ� �߰� 2016.04.21 - ������
		d0WorkingMsg.setStoreName(storeData.getCust_name_disp()) ;
		d0WorkingMsg.setStoreRegiNum(storeData.getBizregno_nbr()) ;
		d0WorkingMsg.setRepName(storeData.getRep_name()) ;
		d0WorkingMsg.setStoreADDR(storeData.getBizstoreaddr_txt1() + " " + storeData.getBizstoreaddr_txt2()) ;
		d0WorkingMsg.setTel(storeData.getTel_nbr()) ;
		d0WorkingMsg.setGoodsType(productData.getGoods_name()) ;
		d0WorkingMsg.setBasePrice(PumpMUtil.convertNumberFormatFromPOSToPump(productData.getShopfrontprc_amt(),4,2)) ;
		d0WorkingMsg.setBaseHeadTitle(nzInfoData.getTitle_txt_head()) ;
		d0WorkingMsg.setBaseFootTitle1(nzInfoData.getTitle_txt_foot1()) ;
		d0WorkingMsg.setBaseFootTitle2(nzInfoData.getTitle_txt_foot2()) ;
		d0WorkingMsg.setSaveHeadTitle(nzInfoData.getSave_head_title()) ;
		d0WorkingMsg.setSaveFootTitle1(nzInfoData.getSave_foot_title1()) ;
		d0WorkingMsg.setSaveFootTitle2(nzInfoData.getSave_foot_title2()) ;
		d0WorkingMsg.setCcHeadTitle(nzInfoData.getCc_head_title()) ;
		d0WorkingMsg.setCcFootTitle1(nzInfoData.getCc_foot_title1()) ;
		d0WorkingMsg.setCcFootTitle2(nzInfoData.getCc_foot_title2()) ;
		d0WorkingMsg.setBcHeadTitle(nzInfoData.getBc_head_title()) ;
		d0WorkingMsg.setBcFootTitle1(nzInfoData.getBc_foot_title1()) ;
		d0WorkingMsg.setBcFootTitle2(nzInfoData.getBc_foot_title2()) ;
		d0WorkingMsg.setSpiHeadTitle(nzInfoData.getSpi_head_title()) ;
		d0WorkingMsg.setSpiFootTitle1(nzInfoData.getSpi_foot_title1()) ;
		d0WorkingMsg.setSpiFootTitle2(nzInfoData.getSpi_foot_title2()) ;
//		d0WorkingMsg.setReceiptMinLiter(PumpMUtil.mergeIntegerToPump(nzInfoData.getReceipt_min_liter(),2,3)) ;
//		d0WorkingMsg.setDepsitMinLiter(PumpMUtil.mergeIntegerToPump(nzInfoData.getDeposit_min_liter(),2,3)) ;
		String minSaleLiter = GlobalUtility.divide(nzInfoData.getMin_sale_price() , productData.getShopfrontprc_amt());		
		d0WorkingMsg.setMinSaleLiter(PumpMUtil.convertNumberFormatFromPOSToPump(minSaleLiter,2,3)) ;
		d0WorkingMsg.setLoanRemainDisp(nzInfoData.getLra_output_yn()) ;
		d0WorkingMsg.setTremWaitSec(nzInfoData.getSc_wait_time()) ;
		d0WorkingMsg.setEmergStopLiter(nzInfoData.getEmer_stop_lt()) ;
		
		Vector<D0_odtInfo> odtInfoVector = new Vector<D0_odtInfo>() ;
		
		for (int i = 0 ; i < odtDataArray.size() ; i++) {
			String odtDataID = odtDataArray.get(i) ;
			D0_odtInfo d0OdtInfo = new D0_odtInfo() ;
			T_NZ_NOZZLEData[] tNzNozDataArray = 
				T_NZ_NOZZLEHandler.getHandler().getT_NZ_NOZZLEDataByOdtno_no_connected(odtDataID) ;
			Vector<D0_nozzleInfo> nozzleInfoArray = new Vector<D0_nozzleInfo>() ;
			for (int j = 0 ; j < tNzNozDataArray.length ; j++) {
				D0_nozzleInfo d0NozInfo = new D0_nozzleInfo() ;
				d0NozInfo.setNozzleNumber(tNzNozDataArray[j].getNozzle_no()) ;
				nozzleInfoArray.add(d0NozInfo) ;
			}
			d0OdtInfo.setNozzleInfo(nozzleInfoArray) ;
			d0OdtInfo.setNozzleCount(Integer.toString(nozzleInfoArray.size())) ;
			d0OdtInfo.setOdtID(odtDataID) ;
			d0OdtInfo.setOdtPort(tNzNozDataArray[0].getDboxrelport_no()) ;
			
			odtInfoVector.add(d0OdtInfo) ;
		}
		
		d0WorkingMsg.setOdtInfo(odtInfoVector) ;
		d0WorkingMsg.setOdtCount(Integer.toString(odtInfoVector.size())) ;
		
		return d0WorkingMsg ;
	}

	
	/**
	 * ������ ODT D1 ȯ�� ���� ���� ��û
	 * 
	 * @param storeCode : ���� �ڵ�
	 * @param nozzle_no	: ���� ��ȣ(������ ��ȣ)
	 * @return
	 * @throws Exception
	 */
	public static WorkingMessage getD1WorkingMessage(String storeCode, String nozzle_no) throws Exception {
		D1_WorkingMessage d1WorkingMsg = null ;
		T_KH_STOREData storeData = T_KH_STOREHandler.getHandler().getT_KH_STOREDataByStoreCode(storeCode) ;
		String goods_code = T_NZ_NOZZLEHandler.getHandler().getGoods_codeBYNozzle_no(nozzle_no) ;
		T_KH_PRODUCTData productData = T_KH_PRODUCTHandler.getHandler().getT_KH_PRODUCTData(goods_code) ;
		
		d1WorkingMsg = new D1_WorkingMessage(GlobalUtility.getUniqueMessageID(),
				nozzle_no,
				storeData.getCust_name_disp(),
				storeData.getBizregno_nbr(),
				storeData.getRep_name(),
				storeData.getBizstoreaddr_txt1() + " " + storeData.getBizstoreaddr_txt2(),
				storeData.getTel_nbr(),
				goods_code,
				PumpMUtil.convertNumberFormatFromPOSToPump(productData.getShopfrontprc_amt(),4,2)) ;
		return d1WorkingMsg;
	}
	
	/**
	 * ������ ��ȣ �����Ѵ�.
	 * ������ ��ȣ ��Ģ�� ������ ����.
	 * 		YYMMDD1XXX (XXX �� 000 ���� 999 �����̴�.)
	 * @return
	 */
	public synchronized static String getKeepIssueNo() {	
		LogUtility.getPumpMLogger().debug("[Pump M] Publish new KeepNumber") ;

		SqlSession session = null;
		String keepNumber = null ;
		
		try {
			session = SqlSessionFactoryManager.openSqlSession();
			String storeCode = T_KH_STOREHandler.getHandler().getStore_code_class(session);
			LogUtility.getPumpMLogger().error("[KeepIssuneNo] Store CODE = " + storeCode);

			// ������ �տ� �� ��¥
			String currentDay = "";
			// ������ �ڿ� �� ����
			String newSequenceNo = "";
			
			// �ڿ� ������ ����
			if (ICode.STORE_CODE_CLASS_12.equals(storeCode) || ICode.STORE_CODE_CLASS_11.equals(storeCode)) {
				// �ڿ� �����Ҵ� yymm
				currentDay = GlobalUtility.getDateYYYYMMDD().substring(2, 6) ;
				// �ڿ� �����Ҵ� 5�ڸ� ���� 
				newSequenceNo = "00001";
				
			} else {
				// yymmdd
				currentDay = GlobalUtility.getDateYYYYMMDD().substring(2) ;
				// 3�ڸ� ����
				newSequenceNo = "001";
				
			}	// end if
			
			
			if (keepNoDay == null) {
				keepNoDay = currentDay ;
			}		
			
			if (keepNoDay.equals(currentDay)) {
				T_KH_KEYSData keysData = T_KH_KEYSHandler.getHandler().getT_KH_KEYSData(IPumpConstant.PUMP_KEEP_NUMBER) ;
				if (keysData == null) {
					keepNumber = keepNoDay + keepNo_Type + newSequenceNo ;
					T_KH_KEYSHandler.getHandler().insertT_KH_KEYSData(IPumpConstant.PUMP_KEEP_NUMBER, keepNumber);
					
				} else {
					// DB�� �ִ� ������ ��ȣ�� ���� ��¥�� ���Ͽ� �ٸ��ٸ� ���� �ο��Ѵ�.
					String dbKeepNo = keysData.getValue();
					String dbKeepDate = dbKeepNo.substring(0, currentDay.length());
					
					if (dbKeepDate.equals(currentDay)) {
						double keepno = Double.parseDouble(dbKeepNo) ;
						keepno++ ;
						keepNumber = GlobalUtility.appending0Pre(GlobalUtility.getStringValue(keepno) ,10) ;					
						T_KH_KEYSHandler.getHandler().updateT_KH_KEYSData(IPumpConstant.PUMP_KEEP_NUMBER, keepNumber);
						
					} else {
						keepNumber = keepNoDay + keepNo_Type + newSequenceNo ;
						T_KH_KEYSHandler.getHandler().updateT_KH_KEYSData(IPumpConstant.PUMP_KEEP_NUMBER, keepNumber);
						
					}
					
				}
				
			} else {
				keepNoDay = currentDay ;
				T_KH_KEYSData keysData = T_KH_KEYSHandler.getHandler().getT_KH_KEYSData(IPumpConstant.PUMP_KEEP_NUMBER) ;
				keepNumber = keepNoDay + keepNo_Type + newSequenceNo ;
				
				if (keysData == null) {
					T_KH_KEYSHandler.getHandler().insertT_KH_KEYSData(IPumpConstant.PUMP_KEEP_NUMBER, keepNumber);
					
				} else {
					T_KH_KEYSHandler.getHandler().updateT_KH_KEYSData(IPumpConstant.PUMP_KEEP_NUMBER, keepNumber);
					
				}	
				
			}	// end if			
			session.commit();
		} catch (Exception e) {
			session.rollback();
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
			
		}finally{
			SqlSessionFactoryManager.closeSqlSession(session);
		}
		
		LogUtility.getPumpMLogger().debug("[Pump M] keepNumber="+keepNumber) ;
		return keepNumber ;
	}
	
	
	@SuppressWarnings("unused")
	private static String getKeepNoExpredDate(String keepNoPeriodDay) {
		LogUtility.getPumpMLogger().debug("[Pump M] ������ ��ȿ�Ⱓ�� �߱��մϴ�. keekeepNoPeriodDay="+keepNoPeriodDay) ;
		int day = 0 ;
		String periodDay = null ;
		
		try {
			day = Integer.parseInt(keepNoPeriodDay) ;
		} catch (Exception e) {
			LogUtility.getPumpMLogger().warn("[Pump M] ������ ��ȿ�Ⱓ�� ���̺� ��� 0 ���� �����մϴ�.") ;
			day = 0 ;
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		
		long timeLog = System.currentTimeMillis() ;
		long preTimeLog = timeLog - (1000L * 60L * 60L * 24L * day) ;
		SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd") ;
		periodDay = formater.format(new Date(preTimeLog)) ;
		
		LogUtility.getPumpMLogger().debug("[Pump M] ������ ��ȿ�Ⱓ�� ������ �����ϴ�. periodDay="+periodDay) ;
		return periodDay ;
	}
	
	
	/**
	 * ������/���� ODT/������ �ʱ�ȭ�� ���ؼ� M1 ������ �����Ѵ�.
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static M1_WorkingMessage getM1WorkingMessage() throws Exception {
		M1_WorkingMessage m1WorkingMsg = new M1_WorkingMessage() ;
		ArrayList<String> dboxRelPort_NoList = T_NZ_NOZZLEHandler.getHandler().getDBoxRelPort_NoList() ;
		
		if (dboxRelPort_NoList == null) return null ;
		
		LogUtility.getPumpMLogger().debug("[Pump M] The number of Port  = " + dboxRelPort_NoList.size()) ;

		m1WorkingMsg.setDBoxPortCnt(Integer.toString(dboxRelPort_NoList.size())) ;
		Vector<M1_DBoxInfo> dBoxInfoVector = new Vector<M1_DBoxInfo>() ;
		
		for (int i = 0 ; i < dboxRelPort_NoList.size() ; i++) {
			M1_DBoxInfo dBoxInfo = new M1_DBoxInfo() ;
			dBoxInfo.setDBoxPortNo(dboxRelPort_NoList.get(i)) ;
			T_NZ_NOZZLEData[] tNzNozzleData = 
				T_NZ_NOZZLEHandler.getHandler().getT_NZ_NOZZLEDataByDBoxrelport_no(dboxRelPort_NoList.get(i)) ;
			
			Vector<M1_NozzleInfo> nozzleInfoVector = new Vector<M1_NozzleInfo>() ;
			for (int j = 0 ; j < tNzNozzleData.length ; j++) {
				M1_NozzleInfo m1NozzleInfo = new M1_NozzleInfo() ;
				// PI2, 2016.03.18, SC�� �����ǰ� ������
				String ip = PropertyManager.getSingleton().getProperty(PropertyManager.KH_HOST, PropertyManager.KH_HOST_DEFAULT);
				
				m1NozzleInfo.setNozzleNo(tNzNozzleData[j].getNozzle_no()) ;
				m1NozzleInfo.setNozzleType(tNzNozzleData[j].getSelf_ind_exist()) ;
				m1NozzleInfo.setNozProtocol(tNzNozzleData[j].getUse_code_protocol()) ;
				m1NozzleInfo.setSelfOdtNo(tNzNozzleData[j].getOdtno_no_connected()) ;				
				m1NozzleInfo.setBaudRate(tNzNozzleData[j].getBaud_rate()) ;
				m1NozzleInfo.setRomVersion(tNzNozzleData[j].getRom_version()) ;
				m1NozzleInfo.setIpAddress(ip);
				nozzleInfoVector.add(m1NozzleInfo) ;
			}

			dBoxInfo.setNozzleInfo(nozzleInfoVector) ;
			dBoxInfo.setNozCount(new Integer(nozzleInfoVector.size()).toString()) ;
			dBoxInfoVector.add(dBoxInfo) ;

		}
		m1WorkingMsg.setDBoxInfo(dBoxInfoVector) ;
		
		return m1WorkingMsg ;
	}
	
	/**
	 * ���� ��ȣ�� ����� ODT ��ȣ ��û
	 * 
	 * @param nozzle_no	: ���� ��ȣ
	 * @return
	 * 		ODT ��ȣ (������ Null)
	 */
	public static String getODTNumberFromNozzleNo(String nozzle_no) {
		String odtNo = null ;
		try {
			T_NZ_NOZZLEData nozzleData = T_NZ_NOZZLEHandler.getHandler().getT_NZ_NOZZLEDataByNozzleNo(nozzle_no) ;
			odtNo = nozzleData.getOdtno_no_connected() ;
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		return odtNo ;
	}

	
	/**
	 * KIXXHUB ����� �ܸ��� �ø��� ��ȣ ��������
	 * @return
	 */
	@SuppressWarnings("finally")
	public static String getOdtTermId() {
		
		SqlSession session = null;
		String termId = "";
		try {
			session = SqlSessionFactoryManager.openSqlSession();
			
			termId = T_CT_CATHandler.getHandler().getT_CT_CATData_by_crt_code_type(session, ICode.CRT_CODE_TYPE_5);
			
		}  catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
			return termId;
		}
	}
	
	
	/**
	 * ���� ODT ȯ�� ���� P1
	 * 
	 * @param storeCode		: ���� �ڵ�
	 * @param odt_no 		: null �� ��� ��� ODT �� ���� ����, Ư�� ���� ��� �� ���� �ش��ϴ� ������ ä��.
	 * @return : P1 WorkingMessage
	 * @throws Exception
	 */
	public static WorkingMessage getP1WorkingMessage(String storeCode, String odt_no) throws Exception {
		P1_WorkingMessage p1WorkingMsg ;
		
		LogUtility.getPumpMLogger().info("[Pump M] getP1WorkingMessage function : storeCode=" + storeCode + ":odt_no=" + odt_no);
		T_KH_STOREData storeData = T_KH_STOREHandler.getHandler().getT_KH_STOREDataByStoreCode(storeCode) ;
		T_NZ_INFOData nzInfoData = T_NZ_INFOHandler.getHandler().getT_NZ_INFODataByStoreCode(storeCode) ;
		if (nzInfoData == null) {
			LogUtility.getPumpMLogger().info("[Pump M] getP1WorkingMessage nzInfoData is null");
		}
		
		p1WorkingMsg = new P1_WorkingMessage() ;
		Vector<String> odtIdVector = new Vector<String>() ;
		
		if (odt_no == null) {
			T_NZ_NOZZLEData[] nozzleDataList = 
				T_NZ_NOZZLEHandler.getHandler().getT_NZ_NOZZLEDataBySelfIndExist(ICode.SELF_IND_EXIST_05_ODT_SELF) ;
			if ((nozzleDataList == null) || (nozzleDataList.length == 0)){
				return null ;
			}
			for (int i = 0 ; i < nozzleDataList.length ; i++) {
				odtIdVector.add(nozzleDataList[i].getNozzle_no()) ;	// Self ODT ��ȣ
			}
		} else {
			odtIdVector.add(odt_no) ;
		}

		p1WorkingMsg.setOdtId(odtIdVector) ;		
		p1WorkingMsg.setStoreCord(storeCode) ;
		p1WorkingMsg.setStoreRegiNum(storeData.getBizregno_nbr()) ;
		p1WorkingMsg.setStoreName(storeData.getCust_name_disp()) ;
		p1WorkingMsg.setRepName(storeData.getRep_name()) ;
		p1WorkingMsg.setStorePost(storeData.getBizstore_code_zip()) ;
		p1WorkingMsg.setStoreADDR1(storeData.getBizstoreaddr_txt1()) ;
		p1WorkingMsg.setStoreADDR2(storeData.getBizstoreaddr_txt2()) ;
		p1WorkingMsg.setTel(storeData.getTel_nbr()) ;
		p1WorkingMsg.setSaMinAmt(nzInfoData.getSa_min_amt()) ;
		p1WorkingMsg.setReportFootTitle(nzInfoData.getTitle_txt_footreport()) ;
		
		return p1WorkingMsg ;
	}

	
	/**
	 * ���� ODT P2 ȯ�� ���� ���� ��û
	 * 
	 * @param storeCode : ���� �ڵ�
	 * @param odt_no 	: null �� ��� ��� ODT �̰�, Ư�� ���� ��� �� odt_no �� ���� ������ ��û
	 * @return : P2 WorkingMessage ����
	 * @throws Exception
	 */
	public static P2_WorkingMessage getP2WorkingMessage(String storeCode, String odt_no) throws Exception {
		P2_WorkingMessage p2WorkingMsg = new P2_WorkingMessage() ;
		T_NZ_INFOData nzInfoData = T_NZ_INFOHandler.getHandler().getT_NZ_INFODataByStoreCode(storeCode) ;
		Vector<String> odtIdVector = new Vector<String>() ;
		
		if (odt_no == null) {
			T_NZ_NOZZLEData[] nozzleDataList = 
				T_NZ_NOZZLEHandler.getHandler().getT_NZ_NOZZLEDataBySelfIndExist(ICode.SELF_IND_EXIST_05_ODT_SELF) ;

			if ((nozzleDataList == null) || (nozzleDataList.length ==0)){
				return null ;
			}
			for (int i = 0 ; i < nozzleDataList.length ; i++) {
				odtIdVector.add(nozzleDataList[i].getNozzle_no()) ;
			}
		} else {
			odtIdVector.add(odt_no) ;
		}
		
		p2WorkingMsg.setOdtId(odtIdVector) ;
		p2WorkingMsg.setBaseHeadTitle(nzInfoData.getTitle_txt_head()) ;
		p2WorkingMsg.setBaseFootTitle1(nzInfoData.getTitle_txt_foot1()) ;
		p2WorkingMsg.setBaseFootTitle2(nzInfoData.getTitle_txt_foot2()) ;
		
		//������ ���ڵ� ��� ���� ��ȸ - PI2, CWI, 2016-03-10 GSC SELF���� ������ ���ڵ� ��¿��θ� ���� �޴´�.
		// 0: ��¾���, 1: �������ڵ� , 2: �������ڵ�
		String printBarCode = "0";
		
		try {
			// ���ڵ� ��뿩�� - 0: �̻��, 1: ���
			String BarCode = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0440);
			
			if(BarCode != null && !"".equals(BarCode)) { 
				
				// ���ڵ带 ��� �Ѵٸ�
				if("1".equals(BarCode)) {
					
					// ���ڵ� ���� - 1:���� ���ڵ�, 2:���� ���ڵ�
					String BarCodeType = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0441);
				
					// ���ڵ� ���� üũ
					if(BarCodeType != null && !"".equals(BarCodeType)) {
						
						// 2019.11.20 �����ڵ� "0441" ���ڸ��ϰ�� �� ���ڸ��� ������. - SoonKwan
						if(BarCodeType.length() == 2) {
							BarCodeType = BarCodeType.substring(1);
						}
						
						printBarCode = BarCodeType;
					}
					
				}
					
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error("[CarWash BarCode]������ ���ڵ� ��� ���� ��ȸ�� ���� �߻�");
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		}
		
		if(printBarCode != null || !"".equals(printBarCode)) {
			p2WorkingMsg.setBarcodePrintYN(printBarCode);
		} 
		
		LogUtility.getPumpMLogger().info("[CarWash BarCode]������ ���ڵ� ��� ����= "+p2WorkingMsg.getBarcodePrintYN());
		
		//�پ��� ���� ������ ��½� �Ӹ��� �������� ����ϱ� ���ؼ� ���� ���� ����
		PumpMessageFormat.setHeadPrint(nzInfoData.getTitle_txt_head()) ;
		PumpMessageFormat.setFootPrint1(nzInfoData.getTitle_txt_foot1());
		PumpMessageFormat.setFootPrint2(nzInfoData.getTitle_txt_foot2());
		return p2WorkingMsg ;
	}
	
	
	/**
	 * �Ϲ� �������� P3 ȯ�� ���� ���� ��û
	 * 
	 * @param storeCode	: ���� �ڵ�
	 * @param nozzle_no	: ���� ��ȣ (null �� ��� ��� �Ϲ� ������)
	 * @return
	 * @throws Exception
	 */
	public static P3_WorkingMessage getP3WorkingMessage(String storeCode, String nozzle_no) throws Exception {
		P3_WorkingMessage p3WorkingMsg = new P3_WorkingMessage() ;
		T_NZ_NOZZLEData[] tNzNozzleDataArray = null ;
		
		if (nozzle_no == null) {
			tNzNozzleDataArray = T_NZ_NOZZLEHandler.getHandler().getALLT_NZ_NOZZLEData() ;
		} else {
			T_NZ_NOZZLEData tNozzleData = T_NZ_NOZZLEHandler.getHandler().getT_NZ_NOZZLEDataByNozzleNo(nozzle_no) ;
			if (tNozzleData != null) {
				tNzNozzleDataArray = new T_NZ_NOZZLEData[1] ;
				tNzNozzleDataArray[0] = tNozzleData ;
			}			
		}
		
		if (tNzNozzleDataArray == null) return null ;
		
		Vector<P3_NozzleInfo> nozzleInfo = new Vector<P3_NozzleInfo>() ;
		String posID = T_KH_POS_INFOHandler.getHandler().getPOSID(storeCode) ;
		for (int i = 0 ; i < tNzNozzleDataArray.length ; i++) {
			T_NZ_NOZZLEData nozData = tNzNozzleDataArray[i] ;

			// ������/����ODT/���������� �� ����	
			if ((nozData.getSelf_ind_exist().equals(ICode.SELF_IND_EXIST_01_PUMP)) || 
					(nozData.getSelf_ind_exist().equals(ICode.SELF_IND_EXIST_03_SEMI_SELF))){
				P3_NozzleInfo p3NozzleInfo = new P3_NozzleInfo() ;
				T_KH_PRODUCTData productData = 
					T_KH_PRODUCTHandler.getHandler().getT_KH_PRODUCTData(nozData.getGoods_code()) ;
				
				if (productData == null) {
					LogUtility.getPumpMLogger().error("[Pump M] Not find Good Code=" + 
							nozData.getGoods_code() + " in Product table.") ;
				} else {				
					p3NozzleInfo.setNozzleNo(nozData.getNozzle_no()) ;
					p3NozzleInfo.setNozzleType(nozData.getSelf_ind_exist()) ;
					p3NozzleInfo.setNozzleProtocol(nozData.getUse_code_protocol()) ;
					p3NozzleInfo.setPosID(posID) ;
					p3NozzleInfo.setGoodsCode(nozData.getGoods_code()) ;
					// ���� �̸�.
					p3NozzleInfo.setGoodsType(productData.getGoods_name_eng()) ;
					
					String basePrice = getBasePrice(productData, nozData.getSelf_ind_exist()) ;
					p3NozzleInfo.setBasePrice(PumpMUtil.convertNumberFormatFromPOSToPump(basePrice , 4, 2)) ;
					p3NozzleInfo.setTankNumber(nozData.getTank_no()) ;
					p3NozzleInfo.setPortNumber(nozData.getDboxrelport_no()) ;
					
					nozzleInfo.add(p3NozzleInfo) ;
				}
			}
		}
		
		if (nozzleInfo.size() == 0) return null ;
		else {
			p3WorkingMsg.setNozzleInfo(nozzleInfo) ;
			return p3WorkingMsg ;
		}
	}

	
	/**
	 * ���� ODT P5 ȯ�� ���� ���� ��û
	 * [2008.12.14] �ڵ�ȭ , ����ö
	 * 	���� �������� �ܰ� ����� �ʱ�ȭ �� ��쿡�� P5_OdtInfo �� mode �� 0 ���� �����ϰ�, �������� ��쿡�� 1 �� �����ϵ��� �Ѵ�.
	 * 
	 * @param storeCode : ���� �ڵ�
	 * @param odt_no : null �� ��� ��� ODT �̰�, Ư�� ��ȣ�� ��� �� ��ȣ��.
	 * @return : P5 WorkingMessage
	 * @throws Exception
	 */
	public static P5_WorkingMessage getP5WorkingMessage(String storeCode, String odt_no) throws Exception {
		P5_WorkingMessage p5WorkingMsg = new P5_WorkingMessage() ;
		T_NZ_INFOData nzInfoData = T_NZ_INFOHandler.getHandler().getT_NZ_INFODataByStoreCode(storeCode) ;
		String posID = T_KH_POS_INFOHandler.getHandler().getPOSID(storeCode) ;
		ArrayList<String> selfODTList = null ;
		
		// �ʱ�ȭ�� �������� ���θ� �Ǵ��Ѵ�. 
		// POS�κ��� ���� ���� ������ ��쿡�� DB���� ���� �����´�. Default�� ��������(1)�̴�  -  2010.02.13 upkoo
		String useFullPumping = null; 
		if( T_KH_CODEMASTERHandler.getHandler().codeMaster.get(IConstant.POSPORTOCOL_CODEMASTER_0335) != null ) {
			useFullPumping =  T_KH_CODEMASTERHandler.getHandler().codeMaster.get(IConstant.POSPORTOCOL_CODEMASTER_0335);
			LogUtility.getPumpMLogger().debug("[Pump M] �������� ���θ� POS���� �޾��� ���-> useFullPumping-> "+ useFullPumping) ;
		} else {
			useFullPumping = "1";
		}
		
		if (odt_no == null) {
			selfODTList = T_NZ_NOZZLEHandler.getHandler().getSelfODTNoList() ;
			if (selfODTList == null) return null ;
		} else {
			selfODTList = new ArrayList<String>() ;
			selfODTList.add(odt_no) ;
		}
		
		String selfReceiptTitle = nzInfoData.getTitle_txt_footreport() ;
		String sysTime = GlobalUtility.getDateYYYYMMDDHHMMSS() ;

		Vector<P5_OdtInfo> odtInfoVector = new Vector<P5_OdtInfo>() ;
		for (int i = 0 ; i < selfODTList.size() ; i++) {
			P5_OdtInfo odtInfo = new P5_OdtInfo() ;
			odtInfo.setOdtID(selfODTList.get(i)) ;
			odtInfo.setPosID(posID) ;
			odtInfo.setSysTime(sysTime) ;
			odtInfo.setSelfReceiptTitle(selfReceiptTitle) ;
			odtInfo.setMode("0") ;
			
			// �ʱ�ȭ�� �����������θ� �����Ѵ� - 2010.02.13 upkoo
			odtInfo.setUseFullPumping(useFullPumping);
			
			T_NZ_NOZZLEData[] tNzNozDataArray = 
				T_NZ_NOZZLEHandler.getHandler().getT_NZ_NOZZLEDataByOdtno_no_connected(selfODTList.get(i)) ;
			
			if ((tNzNozDataArray == null) || (tNzNozDataArray.length == 0)) {
				LogUtility.getPumpMLogger().debug("[Pump M] No nozzle connected to Self ODT["+ selfODTList.get(i) +"]") ;
			} else {
				Vector<P5_NozzleInfo> nozzleInfoVector = new Vector<P5_NozzleInfo>() ;
				for (int j = 0 ; j < tNzNozDataArray.length ; j++) {
					T_NZ_NOZZLEData tNzNozData = tNzNozDataArray[j] ;
					P5_NozzleInfo p5NozInfo = new P5_NozzleInfo() ;
					
					T_KH_PRODUCTData productData = 
						T_KH_PRODUCTHandler.getHandler().getT_KH_PRODUCTData(tNzNozData.getGoods_code()) ;
	
					p5NozInfo.setNozzleNumber(tNzNozData.getNozzle_no()) ;
					String basePrice = getBasePrice(productData, tNzNozData) ;
	
					p5NozInfo.setBasePrice(PumpMUtil.convertNumberFormatFromPOSToPump(basePrice , 4, 2)) ;
					p5NozInfo.setGoodsCode(tNzNozData.getGoods_code()) ;
	//				p5NozInfo.setGoodsType(productData.getGoods_name()) ;
					// ���� ODT �� ���� ������ ���� ����.
					p5NozInfo.setGoodsType(productData.getGoods_name_eng()) ;
					
					nozzleInfoVector.add(p5NozInfo) ;
				}
				odtInfo.setNozzleInfo(nozzleInfoVector) ;
				odtInfo.setNozzleCount(new Integer(nozzleInfoVector.size()).toString()) ;
				
				// PI2, 2016-03-18, CWI - �ű� ODT�� ������� ���� ��� �� BL üũ���� �ɼ��� Properties ���� �޾� P5������ ���� �Ѵ�.
				// fullpumpingOption = ���� �� �Ϲ����� 1.�½���, 2.�����
				String fullpumpingOption = PropertyManager.getSingleton().getProperty(PropertyManager.SELFODT_FULLPUMPING_OPTION, PropertyManager.SELFODT_FULLPUMPING_OPTION_DEFAULT);
				odtInfo.setReApprovalOption(fullpumpingOption);
				LogUtility.getPumpMLogger().debug("[Pump M] �������� �ɼ� [fullpumpingOption] ->" +  fullpumpingOption) ;
				
				// blcheckOption = 0.BL������-���������� �ش�, 1.BL�����-���������� �ش�
				String blcheckOption = PropertyManager.getSingleton().getProperty(PropertyManager.SELFODT_USEBL_OPTION, PropertyManager.SELFODT_USEBL_OPTION_DEFAULT);
				odtInfo.setUseBL(blcheckOption);
				LogUtility.getPumpMLogger().debug("[Pump M] BL Check Option [blcheckOption] ->" +  blcheckOption) ;
				if(blcheckOption == null){
					LogUtility.getPumpMLogger().debug("[Pump M] kixxhub.properties ���Ͽ� selfodt.useBL.option �� ���� �������ּ���.");
					blcheckOption = "0" ;
				}
				
				odtInfoVector.add(odtInfo) ;
			}
		}
		
		p5WorkingMsg.setOdtInfo(odtInfoVector) ;
		return p5WorkingMsg ;
	}
	
	/**
	 * [2010.02.13] �ּ���
	 * 	���� �������� ���������� ���  P5_OdtInfo �� mode �� 2 �� �����ϰ�, useFullPumping  1 �� �����ϵ��� �Ѵ�.
	 * 
	 * @param storeCode		: ���� �ڵ�
	 * @param odt_no				: ODT ��ȣ
	 * @return
	 * @throws Exception
	 */
	public static P5_WorkingMessage getP5WorkingMessageForFullPumping(String storeCode, String odt_no) throws Exception {
	
		P5_WorkingMessage p5WorkingMsg = new P5_WorkingMessage() ;
		T_NZ_INFOData nzInfoData = T_NZ_INFOHandler.getHandler().getT_NZ_INFODataByStoreCode(storeCode) ;
		String posID = T_KH_POS_INFOHandler.getHandler().getPOSID(storeCode) ;
		ArrayList<String> selfODTList = null ;
		
		//upkoo - 2010.02.13
		SqlSession session = null;
		String useFullPumping = "";
		try {
			session = SqlSessionFactoryManager.openSqlSession();
			
			useFullPumping = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(session, IConstant.POSPORTOCOL_CODEMASTER_0335);
		
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}
		
		if( GlobalUtility.isNullOrEmptyString(useFullPumping )) {	
			useFullPumping = "1";
		}
		
		LogUtility.getPumpMLogger().debug("[Pump M] �������� ���� [useFullPumping] ->" +  useFullPumping) ;
		
		if (odt_no == null) {
			selfODTList = T_NZ_NOZZLEHandler.getHandler().getSelfODTNoList() ;
			if (selfODTList == null) return null ;
		} else {
			selfODTList = new ArrayList<String>() ;
			selfODTList.add(odt_no) ;
		}
		
		String selfReceiptTitle = nzInfoData.getTitle_txt_footreport() ;
		String sysTime = GlobalUtility.getDateYYYYMMDDHHMMSS() ;

		Vector<P5_OdtInfo> odtInfoVector = new Vector<P5_OdtInfo>() ;
		for (int i = 0 ; i < selfODTList.size() ; i++) {
			P5_OdtInfo odtInfo = new P5_OdtInfo() ;
			odtInfo.setOdtID(selfODTList.get(i)) ;
			odtInfo.setPosID(posID) ;
			odtInfo.setSysTime(sysTime) ;
			odtInfo.setSelfReceiptTitle(selfReceiptTitle) ;
			odtInfo.setMode("2") ;
			odtInfo.setUseFullPumping(useFullPumping);
			
			T_NZ_NOZZLEData[] tNzNozDataArray = 
				T_NZ_NOZZLEHandler.getHandler().getT_NZ_NOZZLEDataByOdtno_no_connected(selfODTList.get(i)) ;
			
			if ((tNzNozDataArray == null) || (tNzNozDataArray.length == 0)) {
				LogUtility.getPumpMLogger().debug("[Pump M] No nozzle connected to Self ODT["+ selfODTList.get(i) +"]") ;
			} else {
				Vector<P5_NozzleInfo> nozzleInfoVector = new Vector<P5_NozzleInfo>() ;
				for (int j = 0 ; j < tNzNozDataArray.length ; j++) {
					T_NZ_NOZZLEData tNzNozData = tNzNozDataArray[j] ;
					P5_NozzleInfo p5NozInfo = new P5_NozzleInfo() ;
					
					T_KH_PRODUCTData productData = 
						T_KH_PRODUCTHandler.getHandler().getT_KH_PRODUCTData(tNzNozData.getGoods_code()) ;
	
					p5NozInfo.setNozzleNumber(tNzNozData.getNozzle_no()) ;
					String basePrice = getBasePrice(productData, tNzNozData) ;
	
					p5NozInfo.setBasePrice(PumpMUtil.convertNumberFormatFromPOSToPump(basePrice , 4, 2)) ;
					p5NozInfo.setGoodsCode(tNzNozData.getGoods_code()) ;
	//				p5NozInfo.setGoodsType(productData.getGoods_name()) ;
					// ���� ODT �� ���� ������ ���� ����.
					p5NozInfo.setGoodsType(productData.getGoods_name_eng()) ;
					
					nozzleInfoVector.add(p5NozInfo) ;
				}
				odtInfo.setNozzleInfo(nozzleInfoVector) ;
				odtInfo.setNozzleCount(new Integer(nozzleInfoVector.size()).toString()) ;
				
				// PI2, 2016-03-18, CWI - �ű� ODT�� ������� ���� ��� �� BL üũ���� �ɼ��� Properties ���� �޾� P5������ ���� �Ѵ�.
				// fullpumpingOption = ���� �� �Ϲ����� 1.�½���, 2.�����
				String fullpumpingOption = PropertyManager.getSingleton().getProperty(PropertyManager.SELFODT_FULLPUMPING_OPTION, PropertyManager.SELFODT_FULLPUMPING_OPTION_DEFAULT);
				odtInfo.setReApprovalOption(fullpumpingOption);
				LogUtility.getPumpMLogger().debug("[Pump M] �������� �ɼ� [fullpumpingOption] ->" +  fullpumpingOption) ;

				// blcheckOption = 0.BL������-���������� �ش�, 1.BL�����-���������� �ش�
				String blcheckOption = PropertyManager.getSingleton().getProperty(PropertyManager.SELFODT_USEBL_OPTION, PropertyManager.SELFODT_USEBL_OPTION_DEFAULT);
				odtInfo.setUseBL(blcheckOption);
				if(blcheckOption == null){
					LogUtility.getPumpMLogger().debug("[Pump M] kixxhub.properties ���Ͽ� selfodt.useBL.option �� ���� �������ּ���.");
					blcheckOption = "0" ;
				}
				
				odtInfoVector.add(odtInfo) ;
			}
		}
		
		p5WorkingMsg.setOdtInfo(odtInfoVector) ;
		return p5WorkingMsg;
	}

	/**
	 * [2008.12.14] �ڵ�ȭ , ����ö
	 * 	���� �������� �ܰ� ����� �ʱ�ȭ �� ��쿡�� P5_OdtInfo �� mode �� 0 ���� �����ϰ�, �������� ��쿡�� 1 �� �����ϵ��� �Ѵ�.
	 * 
	 * @param storeCode		: ���� �ڵ�
	 * @param odt_no				: ODT ��ȣ
	 * @return
	 * @throws Exception
	 */
	public static P5_WorkingMessage getP5WorkingMessageForSendingOnly(String storeCode, String odt_no) throws Exception {
		P5_WorkingMessage p5WorkingMsg = new P5_WorkingMessage() ;
		T_NZ_INFOData nzInfoData = T_NZ_INFOHandler.getHandler().getT_NZ_INFODataByStoreCode(storeCode) ;
		String posID = T_KH_POS_INFOHandler.getHandler().getPOSID(storeCode) ;
		ArrayList<String> selfODTList = null ;
		
		if (odt_no == null) {
			selfODTList = T_NZ_NOZZLEHandler.getHandler().getSelfODTNoList() ;
			if (selfODTList == null) return null ;
		} else {
			selfODTList = new ArrayList<String>() ;
			selfODTList.add(odt_no) ;
		}
		
		String selfReceiptTitle = nzInfoData.getTitle_txt_footreport() ;
		String sysTime = GlobalUtility.getDateYYYYMMDDHHMMSS() ;

		Vector<P5_OdtInfo> odtInfoVector = new Vector<P5_OdtInfo>() ;
		for (int i = 0 ; i < selfODTList.size() ; i++) {
			P5_OdtInfo odtInfo = new P5_OdtInfo() ;
			odtInfo.setOdtID(selfODTList.get(i)) ;
			odtInfo.setPosID(posID) ;
			odtInfo.setSysTime(sysTime) ;
			odtInfo.setSelfReceiptTitle(selfReceiptTitle) ;
			odtInfo.setMode("1") ;
			
			T_NZ_NOZZLEData[] tNzNozDataArray = 
				T_NZ_NOZZLEHandler.getHandler().getT_NZ_NOZZLEDataByOdtno_no_connected(selfODTList.get(i)) ;
			
			if ((tNzNozDataArray == null) || (tNzNozDataArray.length == 0)) {
				LogUtility.getPumpMLogger().debug("[Pump M] No nozzle connected to Self ODT["+ selfODTList.get(i) +"]") ;
			} else {
				Vector<P5_NozzleInfo> nozzleInfoVector = new Vector<P5_NozzleInfo>() ;
				for (int j = 0 ; j < tNzNozDataArray.length ; j++) {
					T_NZ_NOZZLEData tNzNozData = tNzNozDataArray[j] ;
					P5_NozzleInfo p5NozInfo = new P5_NozzleInfo() ;
					
					T_KH_PRODUCTData productData = 
						T_KH_PRODUCTHandler.getHandler().getT_KH_PRODUCTData(tNzNozData.getGoods_code()) ;
	
					p5NozInfo.setNozzleNumber(tNzNozData.getNozzle_no()) ;
					String basePrice = getBasePrice(productData, tNzNozData) ;
	
					p5NozInfo.setBasePrice(PumpMUtil.convertNumberFormatFromPOSToPump(basePrice , 4, 2)) ;
					p5NozInfo.setGoodsCode(tNzNozData.getGoods_code()) ;
	//				p5NozInfo.setGoodsType(productData.getGoods_name()) ;
					// ���� ODT �� ���� ������ ���� ����.
					p5NozInfo.setGoodsType(productData.getGoods_name_eng()) ;
					
					nozzleInfoVector.add(p5NozInfo) ;
				}
				odtInfo.setNozzleInfo(nozzleInfoVector) ;
				odtInfo.setNozzleCount(new Integer(nozzleInfoVector.size()).toString()) ;
				
				// PI2, 2016-03-18, CWI - �ű� ODT�� ������� ���� ��� �� BL üũ���� �ɼ��� Properties ���� �޾� P5������ ���� �Ѵ�.
				// fullpumpingOption = ���� �� �Ϲ����� 1.�½���, 2.�����
				String fullpumpingOption = PropertyManager.getSingleton().getProperty(PropertyManager.SELFODT_FULLPUMPING_OPTION, PropertyManager.SELFODT_FULLPUMPING_OPTION_DEFAULT);
				odtInfo.setReApprovalOption(fullpumpingOption);
				LogUtility.getPumpMLogger().debug("[Pump M] �������� �ɼ� [fullpumpingOption] ->" +  fullpumpingOption) ;
				
				// blcheckOption = 0.BL������-���������� �ش�, 1.BL�����-���������� �ش�
				String blcheckOption = PropertyManager.getSingleton().getProperty(PropertyManager.SELFODT_USEBL_OPTION, PropertyManager.SELFODT_USEBL_OPTION_DEFAULT);
				odtInfo.setUseBL(blcheckOption);
				LogUtility.getPumpMLogger().debug("[Pump M] BL Check Option [blcheckOption] ->" +  blcheckOption) ;
				if(blcheckOption == null){
					LogUtility.getPumpMLogger().debug("[Pump M] kixxhub.properties ���Ͽ� selfodt.useBL.option �� ���� �������ּ���.");
					blcheckOption = "0" ;
				}
				
				odtInfoVector.add(odtInfo) ;
			}
		}
		
		p5WorkingMsg.setOdtInfo(odtInfoVector) ;
		return p5WorkingMsg ;
	}	
	
	
	/**
	 * ������ �ʱ�ȭ�� ���� P6 ������ �����Ѵ�.
	 * 
	 * @param storeCode		: ���� �ڵ�
	 * @return
	 * @throws Exception
	 */
	public static P6_WorkingMessage getP6WorkingMessage(String storeCode) throws Exception {
		P6_WorkingMessage p6WorkingMsg = new P6_WorkingMessage() ;
		String sysTime = GlobalUtility.getDateYYYYMMDDHHMMSS().substring(2);//12�ڶ� 
		String workDate = T_KH_STOREHandler.getHandler().getWorkingDate(storeCode) ;
		
		p6WorkingMsg.setSystemTime(sysTime) ;
		p6WorkingMsg.setWDate(workDate) ;
		
		return p6WorkingMsg ;
	}	
	
	
	/**
	 * POS �� ���� ���� ���� Time Parameter ������ ������ ������ Adapter ���� �����Ѵ�.
	 * 
	 * @param store_code
	 * @param nozzle_no		: null �� ��� ��� ���� ���ؼ�
	 * @return
	 * @throws Exception
	 */
	public static P7_WorkingMessage[] getP7WorkingMessageArray(String store_code, String nozzle_no) throws Exception {
		T_NZ_PARAMETERData[] parameterDatas = null ;
		P7_WorkingMessage[] p7WorkMsgArray = null ;	

		if (nozzle_no == null) {
			parameterDatas = 
				T_NZ_PARAMETERHandler.getHandler().getT_NZ_PARAMETERDataByStoreCode(store_code) ;
		} else {
			parameterDatas = new T_NZ_PARAMETERData[1] ;
			parameterDatas[0] = T_NZ_PARAMETERHandler.getHandler().getT_NZ_PARAMETERDataByNozzleNo(store_code, nozzle_no) ;
		}
		
		if ((parameterDatas == null) || (parameterDatas.length == 0)) return null ;
		
		p7WorkMsgArray = new P7_WorkingMessage[parameterDatas.length] ;
		for (int i = 0 ; i < p7WorkMsgArray.length ; i++) {
			p7WorkMsgArray[i] = new P7_WorkingMessage(GlobalUtility.getUniqueMessageID(), 
					parameterDatas[i].getNozzle_no(),
					parameterDatas[i].getReadTimeInterval(),
					parameterDatas[i].getReadWaitInterval(),
					parameterDatas[i].getWriteWaitInterval(),
					parameterDatas[i].getLineErrorCount(),
					parameterDatas[i].getLineErrorSkipCount()) ;			
		}		
		return p7WorkMsgArray ;
	}

	
	/**
	 * �� ī�� ���� ���� ������ �����Ѵ�. �̴� POS �� �������� ���� ���, �� ī�� ���� ��û�� ���� ������ ��ü������ �����Ѵ�.
	 * 
	 * @param nozzleNo		: ���� ��ȣ
	 * @param goods_code	: ��ǰ�ڵ�
	 * @param bonGoodsCode	: ���ʽ���ǰ �ڵ�
	 * @param oil_ind		: ����/���� ����
	 * @param base_price	: ��ǰ �ܰ�
	 * @param liter			: ������
	 * @param price			: �����ݾ�
	 * @param transactionID	: KH ó�� ��ȣ
	 * @param pro_ind_overlimit	: �ܻ����Ÿ��
	 * @param isDiscount		: ���� ����
	 * @param discountBasePrice	: ���ε� �ܰ�
	 * @return
	 */
	private static POS_DY_Item getPOSPumpM_DY_Item(String nozzleNo,
								String goods_code,
								String bonGoodsCode,
								String oil_ind,
								String base_price,
								String liter,
								String price,
								String transactionID,
								String pro_ind_overlimit,
								boolean isDiscount,
								String discountBasePrice,
								boolean isTaxFree,
								String rcptsheetissue_code_amtsale) {

		LogUtility.getPumpMLogger().debug("[Pump M] DY ������ �����մϴ�.") ;
		
		POS_DY_Item dyPumpM_Item = null ;
		SqlSession session = null;
		
		String goodsCode = goods_code ;					// ��ǰ�ڵ�(18b)
		String unitPrice_before_discount = base_price ;	// �������ܰ�(11b)
		String oilAmount = liter ;						// ����(8b)
		String unitPrice_after_discount = null ;		// �����Ĵܰ�(10b)
		String tax_ind = ICode.TAXFREE_CD_01 ;			// ���鼼����(2b)
		String price_before_tax = null ;				// ���ް���(10b)
		String taxPrice = null ;						// ����(10b)
		String oilPrice_before_discount = price ;		// �������ݾ�(10b)
		String oilPrice_after_discount = null ;			// �����ıݾ�(10b)
		String khTransactionID = transactionID ;		// ��ǥ��ȣ(14b)
		String rentlimit_proc_ind_overlimit = pro_ind_overlimit ;	// �ܻ����Ÿ��(2b)
		String unitDiscount_ind = null ;				// ���ο���(1b)
		String keepissue_no = null ;					// ������ ��ȣ(10b)
		String issue_type = null ;						// ������ ���� ���� (2b)
		
		double tempPrice = 0 ;
		
		try {
			session = SqlSessionFactoryManager.openSqlSession();
			double literInt = Double.parseDouble(liter) ; ;		
			double discountPriceInt = 0 ;

			/**
			 * 2016. 4. 14. ���� 15:48:31, PI2, WooChul Jung.
			 * 	PL ������ ���� ��� ���� �ܰ��� �� �� �� ����. �̷� ���ؼ� ���� �ݾ��� 0 ���� �����Ǵ� ��찡 �ִµ�, �̸� ����.
			 */
			if (GlobalUtility.isNullOrEmptyString(discountBasePrice) || "0".equals(discountBasePrice)) {
				LogUtility.getPumpMLogger().info("[Pump M] ���� �ܰ��� 0 �̱� ������, ���� ���δ� false �� �ϰ�, ���� �ܰ��� ���ΰ��� �����ϰ� �����մϴ�.");
				discountBasePrice = base_price ;
				isDiscount = false ;
			} 
				
			if (isDiscount) {
				unitDiscount_ind = ICode.DY_UNITDISCOUNT_IND_1 ;
			} else {
				unitDiscount_ind = ICode.DY_UNITDISCOUNT_IND_0 ;
			}

			discountPriceInt = Double.parseDouble(discountBasePrice) ;
			unitPrice_after_discount = discountBasePrice ;
			
			if (Double.parseDouble(unitPrice_before_discount) != Double.parseDouble(unitPrice_after_discount)) {
				tempPrice = discountPriceInt * literInt ;
			} else {
				LogUtility.getPumpMLogger().debug("[Pump M] ������ �ܰ��� ������ �ܰ��� �����ϱ� ������, �ݾ��� �����ݾ����� �մϴ�.") ;
				tempPrice = Double.parseDouble(price) ;
			}
			
			oilPrice_after_discount = 
				Double.toString(handleRcptsheetissue_code_amtsale(tempPrice, rcptsheetissue_code_amtsale)) ;
			
			if (isTaxFree) {
				tax_ind = ICode.TAXFREE_CD_02 ;
				price_before_tax = GlobalUtility.getStringValue(oilPrice_after_discount) ;
				taxPrice = "0" ;
			} else {
				tax_ind = ICode.TAXFREE_CD_01 ;
				taxPrice = GlobalUtility.getTaxPrice(oilPrice_after_discount) ;
				price_before_tax = PumpMUtil.getPrice_before_tax(oilPrice_after_discount, taxPrice) ;
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}
		
		dyPumpM_Item = new POS_DY_Item(goodsCode,
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
									keepissue_no,
									issue_type) ;

		LogUtility.getPumpMLogger().debug("[Pump M] ������ DY ������ ������ �����ϴ�.") ;
		if (dyPumpM_Item != null) 
			LogUtility.getPumpMLogger().info(dyPumpM_Item.toString());
		return dyPumpM_Item ;
	}
	
	/**
	 * DY ������ ���ؼ� DY Array �� �����. �̴� �ѵ� ������ ���ؼ� �Ѱ��� ���������� ���� ������ ���� �ʿ䰡 �ֱ� �����̴�.
	 * 
	 * @param dyPumpM_Item	: DY ����
	 * @param limit			: �ѵ���
	 * @param limit_remains	: �ѵ� ��뷮
	 * @return
	 */
	private static POS_DY_Item[] getPOSPumpM_DY_ItemArrayInLimitLiter(POS_DY_Item dyPumpM_Item,
			String limit,
			double limit_remains) {
		
		LogUtility.getPumpMLogger().debug("[Pump M] ���� �ѵ� ��  ���� ���� Item Info ������ �籸���մϴ�.") ;
		LogUtility.getPumpMLogger().info(dyPumpM_Item.toString());
		LogUtility.getPumpMLogger().debug("[Pump M] limit=" + limit + "#limit_remains=" + limit_remains) ;
		
		POS_DY_Item[] dyPumpMItemArray = null ;
		
		if (limit_remains <= 0 ) {
			resetBeforeBasePrice(dyPumpM_Item , dyPumpM_Item.getRentlimit_proc_ind_overlimit()) ;
			dyPumpMItemArray = new POS_DY_Item[1] ;
			dyPumpMItemArray[0] = dyPumpM_Item.toClone() ;
			dyPumpMItemArray[0].setRentlimit_proc_ind_overlimit(ICode.PROC_IND_OVERLIMIT_99) ;	// ����
			//2016.03.11 WooChul Jung. If limit_proc_ind_overlimit == 01, then allow.
		} else if ("01".equals(dyPumpM_Item.getRentlimit_proc_ind_overlimit()) || (limit_remains >= Double.parseDouble(dyPumpM_Item.getOilAmount()))) {
			dyPumpMItemArray = new POS_DY_Item[1] ;
			dyPumpMItemArray[0] = dyPumpM_Item.toClone() ;
			dyPumpMItemArray[0].setRentlimit_proc_ind_overlimit(ICode.PROC_IND_OVERLIMIT_00) ;	// ����
		} else {
			String exceedLiter = Double.toString( 
					GlobalUtility.substract(Double.parseDouble(dyPumpM_Item.getOilAmount()), limit_remains)) ;
			POS_DY_Item dyPumpM_Item0 = 
				getPOSPumpM_DY_ItemArrayInPayedLiter(dyPumpM_Item, Double.toString((limit_remains))) ; // �ѵ� ���� ���� = "1"
			dyPumpM_Item0.setRentlimit_proc_ind_overlimit(ICode.PROC_IND_OVERLIMIT_00) ;	// ����
			
			POS_DY_Item dyPumpM_Item1 = 
				getPOSPumpM_DY_ItemArrayInPayedLiter(dyPumpM_Item, exceedLiter) ; 	// �ѵ� ���� ���� = "0"
			dyPumpM_Item1.setRentlimit_proc_ind_overlimit(dyPumpM_Item.getRentlimit_proc_ind_overlimit()) ;
			resetBeforeBasePrice(dyPumpM_Item1, dyPumpM_Item1.getRentlimit_proc_ind_overlimit()) ;
			
			dyPumpMItemArray = new POS_DY_Item[2] ;
			dyPumpMItemArray[0] = dyPumpM_Item0 ;
			dyPumpMItemArray[1] = dyPumpM_Item1 ;
		}		
		return dyPumpMItemArray ;
	}
	
	/**
	 * �ѵ� �ݾ����� ���� ���������� �����Ѵ�.
	 * 
	 * @param dyPumpM_Item	: DY ����
	 * @param limit			: �ѵ� �ݾ�
	 * @param limit_remains	: ���� �ݾ�
	 * @return
	 */
	private static POS_DY_Item[] getPOSPumpM_DY_ItemArrayInLimitPrice(POS_DY_Item dyPumpM_Item,
			String limit,
			double limit_remains) {

		LogUtility.getPumpMLogger().debug("[Pump M] ���� �ѵ� ��  ���� ���� Item Info ������ �籸���մϴ�#limit=" + limit) ;
		LogUtility.getPumpMLogger().info(dyPumpM_Item.toString());
		
		POS_DY_Item[] dyPumpMItemArray = null ;
		
		if (limit_remains <= 0) {
			resetBeforeBasePrice(dyPumpM_Item, dyPumpM_Item.getRentlimit_proc_ind_overlimit()) ;
			dyPumpMItemArray = new POS_DY_Item[1] ;
			dyPumpMItemArray[0] = dyPumpM_Item.toClone() ;
			dyPumpM_Item.setRentlimit_proc_ind_overlimit(ICode.PROC_IND_OVERLIMIT_99) ;	// ����
			dyPumpMItemArray[0].setRentlimit_proc_ind_overlimit(dyPumpM_Item.getRentlimit_proc_ind_overlimit()) ;
			// 2016.03.11 WooChul Jung. If limit_proc_ind_overlimit == 01, then allow.
		} else if ("01".equals(dyPumpM_Item.getRentlimit_proc_ind_overlimit()) || limit_remains >= Double.parseDouble(dyPumpM_Item.getOilPrice_after_discount())) {
			dyPumpM_Item.setRentlimit_proc_ind_overlimit(ICode.PROC_IND_OVERLIMIT_00) ;	// ����
			dyPumpMItemArray = new POS_DY_Item[1] ;
			dyPumpMItemArray[0] = dyPumpM_Item.toClone() ;
		} else {
			POS_DY_Item dyPumpM_Item0 = 
				getPOSPumpM_DY_ItemArrayInPayedPrice(dyPumpM_Item, Double.toString(limit_remains)) ; 	// �ѵ� ���� ���� = "1"
			
			String usedOilByLimit_remains = GlobalUtility.substract(dyPumpM_Item.getOilAmount(), dyPumpM_Item0.getOilAmount()) ;
			dyPumpM_Item0.setRentlimit_proc_ind_overlimit(ICode.PROC_IND_OVERLIMIT_00) ;	// ����
			
			POS_DY_Item dyPumpM_Item1 = 
				getPOSPumpM_DY_ItemArrayInPayedLiter(dyPumpM_Item, usedOilByLimit_remains) ; 	// �ѵ� ���� ���� = "0"
			
			dyPumpM_Item1.setRentlimit_proc_ind_overlimit(dyPumpM_Item.getRentlimit_proc_ind_overlimit()) ;
			resetBeforeBasePrice(dyPumpM_Item1, dyPumpM_Item1.getRentlimit_proc_ind_overlimit()) ;
			
			dyPumpMItemArray = new POS_DY_Item[2] ;
			dyPumpMItemArray[0] = dyPumpM_Item0 ;
			dyPumpMItemArray[1] = dyPumpM_Item1 ;
		}		
		return dyPumpMItemArray ;
	}
	
	
	/**
	 * ��뷮���� ���������� �����Ѵ�.
	 * 
	 * @param src_dyPumpM_Item	: dy ����
	 * @param payedLiter		: ��뷮
	 * @return
	 */
	private static POS_DY_Item getPOSPumpM_DY_ItemArrayInPayedLiter(POS_DY_Item src_dyPumpM_Item,
			String payedLiter) {		

		LogUtility.getPumpMLogger().debug("[Pump M] ������ Liter �� �̿��Ͽ� DY ������ �籸���մϴ�.") ;
		LogUtility.getPumpMLogger().info(src_dyPumpM_Item.toString());
		LogUtility.getPumpMLogger().debug("[Pump M] payedLiter=" + payedLiter);
		
		POS_DY_Item dyPumpM_Item = src_dyPumpM_Item.toClone() ;
		
		String unitPrice_before_discount = dyPumpM_Item.getUnitPrice_before_discount() ;	// �������ܰ�(11b)
		String oilAmount = payedLiter ;					// ����(8b)
		String unitPrice_after_discount = dyPumpM_Item.getUnitPrice_after_discount() ;	// �����Ĵܰ�(10b)
		String price_before_tax = dyPumpM_Item.getPrice_before_tax() ;			// ���ް���(10b)
		String taxPrice = dyPumpM_Item.getTaxPrice() ;					// ����(10b)
		String oilPrice_before_discount = dyPumpM_Item.getOilPrice_before_discount() ;	// �������ݾ�(10b)
		String oilPrice_after_discount = dyPumpM_Item.getOilPrice_after_discount() ;	// �����ıݾ�(10b)

		oilPrice_before_discount = GlobalUtility.multiple(unitPrice_before_discount, oilAmount) ;
		oilPrice_after_discount = GlobalUtility.multiple(unitPrice_after_discount, oilAmount) ;
		
		taxPrice = GlobalUtility.getTaxPrice(oilPrice_after_discount) ;
		price_before_tax = PumpMUtil.getPrice_before_tax(oilPrice_after_discount, taxPrice) ;
			
		dyPumpM_Item.setUnitPrice_before_discount(unitPrice_before_discount) ;
		dyPumpM_Item.setOilAmount(oilAmount) ;
		dyPumpM_Item.setUnitPrice_after_discount(unitPrice_after_discount) ;
		dyPumpM_Item.setPrice_before_tax(price_before_tax) ;
		dyPumpM_Item.setTaxPrice(taxPrice) ;
		dyPumpM_Item.setOilPrice_before_discount(oilPrice_before_discount) ;
		dyPumpM_Item.setOilPrice_after_discount(oilPrice_after_discount) ;
		return dyPumpM_Item ;
	}
	
	private static POS_DY_Item getPOSPumpM_DY_ItemArrayInPayedPrice(POS_DY_Item src_dyPumpM_Item,
			String payedPrice) {		

		LogUtility.getPumpMLogger().debug("[Pump M] ������ �ݾ� �� �̿��Ͽ� DY ������ �籸���մϴ�.") ;
		LogUtility.getPumpMLogger().info(src_dyPumpM_Item.toString());
		
		POS_DY_Item dyPumpM_Item = src_dyPumpM_Item.toClone() ;
		
		String unitPrice_before_discount = dyPumpM_Item.getUnitPrice_before_discount() ;	// �������ܰ�(11b)
		String oilAmount = "0" ;					// ����(8b)
		String unitPrice_after_discount = dyPumpM_Item.getUnitPrice_after_discount() ;	// �����Ĵܰ�(10b)
		String price_before_tax = "0" ;			// ���ް���(10b)
		String taxPrice = "0" ;					// ����(10b)
		String oilPrice_before_discount = "0" ;	// �������ݾ�(10b)
		String oilPrice_after_discount = payedPrice ;	// �����ıݾ�(10b)

		taxPrice = GlobalUtility.getTaxPrice(oilPrice_after_discount) ;
		price_before_tax = PumpMUtil.getPrice_before_tax(oilPrice_after_discount, taxPrice) ;
		
		oilAmount = PumpMUtil.calculateLiterFromPriceAndBasePrice(oilPrice_after_discount, unitPrice_after_discount) ;
		oilPrice_before_discount = GlobalUtility.multiple(unitPrice_before_discount, oilAmount) ;
		
		dyPumpM_Item.setUnitPrice_before_discount(unitPrice_before_discount) ;
		dyPumpM_Item.setOilAmount(oilAmount) ;
		dyPumpM_Item.setUnitPrice_after_discount(unitPrice_after_discount) ;
		dyPumpM_Item.setPrice_before_tax(price_before_tax) ;
		dyPumpM_Item.setTaxPrice(taxPrice) ;
		dyPumpM_Item.setOilPrice_before_discount(oilPrice_before_discount) ;
		dyPumpM_Item.setOilPrice_after_discount(oilPrice_after_discount) ;
		return dyPumpM_Item ;
	}

	/**
	 * 1ȸ ���� ���� ��� DY ������ Item Info �� �籸���Ѵ�.
	 * 
	 * @param dyPumpM_Item	: DY ������ ItemInfo
	 * @param oneAmount		: 1ȸ ����
	 * @param keepissue_ind	: ������ ���� ����
	 * @return
	 */
	private static POS_DY_Item[] getPOSPumpM_DY_ItemArrayWhenOneAmountCustomer(POS_DY_Item dyPumpM_Item, 
			double oneAmount, String keepissue_ind) {

		LogUtility.getPumpMLogger().debug("[Pump M] 1ȸ����(�����Է�) ���� ���� Item Info ������ �籸���մϴ�#oneAmount=" + oneAmount + "keepissue_ind = " + keepissue_ind) ;
		LogUtility.getPumpMLogger().info(dyPumpM_Item.toString());
		POS_DY_Item[] dyPumpMItemArray = null ;
		
		if (oneAmount <= 0 ) {
			resetBeforeBasePrice(dyPumpM_Item , dyPumpM_Item.getRentlimit_proc_ind_overlimit()) ;
			dyPumpMItemArray = new POS_DY_Item[1] ;
			dyPumpMItemArray[0] = dyPumpM_Item.toClone() ;
			dyPumpMItemArray[0].setRentlimit_proc_ind_overlimit(ICode.PROC_IND_OVERLIMIT_99) ;	// ����
		} else if (oneAmount == Double.parseDouble(dyPumpM_Item.getOilAmount())) {
			dyPumpMItemArray = new POS_DY_Item[1] ;
			dyPumpMItemArray[0] = dyPumpM_Item.toClone() ;
			dyPumpMItemArray[0].setRentlimit_proc_ind_overlimit(ICode.PROC_IND_OVERLIMIT_00) ;	// ����
		} else if (oneAmount > Double.parseDouble(dyPumpM_Item.getOilAmount())) {	
			
			// ������ ������ ���� ���ų� 1 (������ ���� ��) �� ���� ������ �������� ó�� �Ѵ�.
			if ((keepissue_ind == null) || (keepissue_ind.equals(ICode.KEEPISSUE_IND_1))) {
				String remainsLiter = Double.toString( 
						GlobalUtility.substract(oneAmount, Double.parseDouble(dyPumpM_Item.getOilAmount()))) ;
				POS_DY_Item dyPumpM_Item0 = dyPumpM_Item.toClone() ;
				dyPumpM_Item0.setRentlimit_proc_ind_overlimit(ICode.PROC_IND_OVERLIMIT_00) ;	// ����
				
				String issue_type = "01" ; // ���� default
				double se_publish_base_won = 5000 ;
				try {
					T_NZ_INFOData infoData = T_NZ_INFOHandler.getHandler().getT_NZ_INFODataByStoreCode(
						T_KH_STOREHandler.getHandler().getStoreCode()) ;
					issue_type = infoData.getSe_publish_base() ;
					se_publish_base_won = Double.parseDouble(infoData.getSe_publish_base_won()) ;
					
				} catch (Exception e) {
					LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
					issue_type = "01" ; // ���� default
					se_publish_base_won = 5000 ;
				}					
				
				POS_DY_Item dyPumpM_Item1 = 
					getPOSPumpM_DY_ItemArrayInPayedLiter(dyPumpM_Item, remainsLiter) ;
				resetBeforeBasePrice(dyPumpM_Item1) ;
				dyPumpM_Item1.setRentlimit_proc_ind_overlimit(dyPumpM_Item.getRentlimit_proc_ind_overlimit()) ;
				dyPumpM_Item1.setRentlimit_proc_ind_overlimit(ICode.PROC_IND_OVERLIMIT_00) ;	// ����
				
				// ������ ���� �ݾ� ���� �۾��� ���� �������� �������� �ʴ´�.
				if (Double.parseDouble(dyPumpM_Item1.getOilPrice_after_discount()) < se_publish_base_won) {
					dyPumpMItemArray = new POS_DY_Item[1] ;
					dyPumpMItemArray[0] = dyPumpM_Item0 ;
				} else {
					dyPumpM_Item1.setIssue_type(issue_type) ;
					dyPumpM_Item1.setKeepissue_no(PumpMUtil.getKeepIssueNo()) ;
					
					dyPumpMItemArray = new POS_DY_Item[2] ;
//					dyPumpMItemArray[0] = dyPumpM_Item0 ;
					dyPumpMItemArray[0] = 
						getPOSPumpM_DY_ItemArrayInPayedLiter(dyPumpM_Item, Double.toString(oneAmount)) ;
					dyPumpMItemArray[0].setRentlimit_proc_ind_overlimit(dyPumpM_Item.getRentlimit_proc_ind_overlimit()) ;
					dyPumpMItemArray[0].setRentlimit_proc_ind_overlimit(ICode.PROC_IND_OVERLIMIT_00) ;	// ����
					dyPumpMItemArray[1] = dyPumpM_Item1 ;						
				}
			} else {
				dyPumpMItemArray = new POS_DY_Item[1] ;
				dyPumpMItemArray[0] = dyPumpM_Item.toClone() ;
				dyPumpMItemArray[0].setRentlimit_proc_ind_overlimit(ICode.PROC_IND_OVERLIMIT_00) ;	// ����
			}
		} else {
			String exceedLiter = Double.toString( 
					GlobalUtility.substract(Double.parseDouble(dyPumpM_Item.getOilAmount()), oneAmount)) ;
			POS_DY_Item dyPumpM_Item0 = 
				getPOSPumpM_DY_ItemArrayInPayedLiter(dyPumpM_Item, Double.toString((oneAmount))) ; 
			dyPumpM_Item0.setRentlimit_proc_ind_overlimit(ICode.PROC_IND_OVERLIMIT_00) ;	// ����
			
			POS_DY_Item dyPumpM_Item1 = 
				getPOSPumpM_DY_ItemArrayInPayedLiter(dyPumpM_Item, exceedLiter) ;
			dyPumpM_Item1.setRentlimit_proc_ind_overlimit(dyPumpM_Item.getRentlimit_proc_ind_overlimit()) ;
			resetBeforeBasePrice(dyPumpM_Item1, dyPumpM_Item1.getRentlimit_proc_ind_overlimit()) ;
			dyPumpM_Item1.setRentlimit_proc_ind_overlimit(ICode.PROC_IND_OVERLIMIT_99) ;	// ���� ��û
			
			dyPumpMItemArray = new POS_DY_Item[2] ;
			dyPumpMItemArray[0] = dyPumpM_Item0 ;
			dyPumpMItemArray[1] = dyPumpM_Item1 ;
		}		

		LogUtility.getPumpMLogger().debug("[Pump M] 1ȸ����(�����Է�) ���� ���� �籸���� ������ ������ �����ϴ�.") ;
		if (dyPumpMItemArray != null) {
			for (int i = 0 ; i < dyPumpMItemArray.length; i++) {
				POS_DY_Item dyItem = dyPumpMItemArray[i] ;
				LogUtility.getPumpMLogger().info(dyItem.toString());
			}
		}
		
		return dyPumpMItemArray ;
	}
	
	/**
	 * �����ݾװ� �ܰ��� �̿��Ͽ��� �������� ����Ѵ�.
	 * 
	 * @param priceDou		: ���� �ݾ�
	 * @param basePriceDou	: �ܰ�
	 * @return
	 */
	public static double getPresetLiter(double priceDou, double basePriceDou) {
		double literDou = priceDou / basePriceDou ;
		double temp = literDou * 10000L ;
		temp = Math.round(temp) ;
		temp = temp / 10L ;
		int literInt = (int) Math.round(temp) ;
		literDou = (double)literInt / 1000L ;				
		return literDou ; 
	}
	
	/**
	 * ���ް����� ����Ѵ�.
	 * 	���ް��� = �ݾ� - ����
	 * 
	 * @param price
	 * @return
	 */
	public static String getPrice_before_tax(String price, String taxPrice) {
		String price_before_tax = "0" ;
		
		price_before_tax = GlobalUtility.getStringValue(GlobalUtility.substract(price,taxPrice)) ;
		
		return price_before_tax ;
	}
	
	
	/**
	 * ���ʽ� ī�� ��ȣ ���ϱ�
	 * 
	 * @param bonusCardNumber	: ODT �� ���� ���� ���ʽ� ī�� ��ȣ
	 * @return
	 */
	public static String getRealBonusCardNumber(String bonusCardNumber) {
		if ((bonusCardNumber == null) || (bonusCardNumber.equals(""))) return "" ; 
		else {
			int position = bonusCardNumber.indexOf("=") ;
			if (position > 0) {
				return bonusCardNumber.substring(0,position) ;
			} else {
				//���ʽ� ī�� ��ȣ �ο� Null �� ������� ��� ����
				LogUtility.getPumpMLogger().debug("[Pump M] BnsCrdNm NoMatch '='");
				position = bonusCardNumber.indexOf((char)0x00);
				if (position > 0) {
					LogUtility.getPumpMLogger().debug("[Pump M] BnsCrdNm include Null. Null exclude!");
					return bonusCardNumber.substring(0,position) ;
				} else {
					return bonusCardNumber ;
				}
			}
		}
	}
	
	/**
	 * ���� ������ ����� ���� �ݾ��� �����Ѵ�.
	 * 
	 * @return
	 */
	public static String getSa_min_amt() {
		String sa_min_amt = "0" ;
		
		if (PumpMUtil.SA_MIN_AMT != null) {
			sa_min_amt = PumpMUtil.SA_MIN_AMT ;
		} else {
			try {
				String storeCode = T_KH_STOREHandler.getHandler().getStoreCode() ;
				T_NZ_INFOData infoData = T_NZ_INFOHandler.getHandler().getT_NZ_INFODataByStoreCode(storeCode) ;
				if (infoData != null) {
					sa_min_amt = GlobalUtility.getStringValue(infoData.getSa_min_amt()) ;
				}
				if (GlobalUtility.isNullOrEmptyString(sa_min_amt.trim())) {
					sa_min_amt ="0" ;
				}
			} catch (Exception e) {
				LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
				sa_min_amt ="0" ;
			}
		}		
		PumpMUtil.SA_MIN_AMT = sa_min_amt ;
		return sa_min_amt ;
	}
	
	/*
	 * 
	 * @return 
	 */
	public static String getStoreCodeClass() {
		SqlSession session = null;
		String storeCodeClass  = "";
		try {
			session = SqlSessionFactoryManager.openSqlSession();
			storeCodeClass = T_KH_STOREHandler.getHandler().getStore_code_class(session) ;
			LogUtility.getPumpMLogger().debug("[Pump M] storeCodeClass=" + storeCodeClass);
		}
		catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}
		
		return storeCodeClass;
	}
	
	/**
	 * 
	 * @param src
	 * @param beginIndex
	 * @param endIndex
	 * @return
	 */
	public static String getSubString(String src, int beginIndex, int endIndex) {
		String retValue = "" ;
		try {
			if (src != null) {
				retValue = src.substring(beginIndex, endIndex) ;
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
		}
		return retValue ;
	}
	
	/**
	 * CAT �ܸ���� ���� ������ ���� ��û�� �ݾױ��� �Է� �� ���� �� �������� �������� ��쿡�� ������ �����Ѵ�.
	 * 
	 * @param nozzle_no		: ���� ��ȣ
	 * @return
	 */
	public static T_KH_PUMP_TRData getT_KH_PUMP_TRDataByPumpingDontCarePreset(String nozzle_no) {
		T_KH_PUMP_TRData pumpTrData = null ;
		SqlSession session = null;
		try {
			session = SqlSessionFactoryManager.openSqlSession();
			String max_creation_time = T_KH_PUMP_TRHandler.getHandler().getMAX_creation_timeByNozzle_no(session, nozzle_no) ;
			pumpTrData = T_KH_PUMP_TRHandler.getHandler().getT_KH_PUMP_TRDataBynozzle_noANDcreation_time(session, nozzle_no, max_creation_time) ;
			
			if (pumpTrData.getOil_completed_ind().equals(ICode.OIL_COMPLETED_IND_1)) return null ;
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}

		return pumpTrData ;
	}
	
	/**
	 * �ŷ�ó�� ��ϵ� ����ݾ� ó�� ���а� ���δܰ�, ������ ���ؼ� ���ε� �ݾ��� �����Ѵ�.
	 * @param basePrcie						: �Ǹ� �ܰ� 
	 * @param liter							: ������
	 * @param rcptsheetissue_code_amtsale	: �ݾ� �Ǹ� ó�� ����
	 * @return
	 */
	public static String handleRcptsheetissue_code_amtsale(double basePrcie, double liter, String rcptsheetissue_code_amtsale) {
		String payPrice = "";
		
		payPrice = Double.toString(handleRcptsheetissue_code_amtsale((basePrcie*liter), rcptsheetissue_code_amtsale));
		
		return payPrice;
	}
	
	/**
	 * �ŷ�ó�� ��ϵ� ����ݾ� ó�� ������ ���ؼ� ���� �ݾ��� �����Ѵ�.
	 * 
	 * @param rcptsheetissue_code_amtsale
	 * @return
	 */
	public static int handleRcptsheetissue_code_amtsale(double price, String rcptsheetissue_code_amtsale) {
		LogUtility.getPumpMLogger().debug("[Pump M] ����ݾ� ó�� ������ ���ؼ� ���� �ݾ��� �����մϴ�.price="+price) ;
		
		
//		20160516 PI2 twlee ���� rcptsheetissue_code_amtsale: 04(�ǰŷ�)�� ���� ó���� ���� withPos�� withOutPos�� �����ݾ׿� ���̰� ������.
//							POS���� 04(�ǰŷ�)�� ��� �ݿø� ó���ϵ��� �����Ǿ��־� kixxhub������ �ݿø����� ó��. 
		int retPrice = (int) price ;		
		try {
			if (ICode.RCPTSHEETISSUE_CODE_AMTSALE_01.equals(rcptsheetissue_code_amtsale)) {
				retPrice = (int) Math.floor(price) ;
			} else if (ICode.RCPTSHEETISSUE_CODE_AMTSALE_02.equals(rcptsheetissue_code_amtsale) || ICode.RCPTSHEETISSUE_CODE_AMTSALE_04.equals(rcptsheetissue_code_amtsale)) {
				retPrice = (int) Math.round(price) ;
			} else if (ICode.RCPTSHEETISSUE_CODE_AMTSALE_03.equals(rcptsheetissue_code_amtsale)) {
				retPrice = (int) Math.ceil(price) ;
			} 
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		LogUtility.getPumpMLogger().debug("[Pump M] ���� �����ݾ��� ������ �����ϴ�.retPrice="+retPrice) ;
		return retPrice ;
	}
	
	/**
	 * �ŷ�ó�� ��ϵ� ����ݾ� ó�� ���а� ���δܰ�, ������ ���ؼ� ���ε� �ݾ��� �����Ѵ�.
	 * @param basePrcie						: �Ǹ� �ܰ� 
	 * @param liter							: ������
	 * @param rcptsheetissue_code_amtsale	: �ݾ� �Ǹ� ó�� ����
	 * @return
	 */
	public static String handleRcptsheetissue_code_amtsale(String basePrcie, String liter, String rcptsheetissue_code_amtsale) {
		String payPrice = "";
		
		payPrice = Double.toString(handleRcptsheetissue_code_amtsale((
				Double.parseDouble(basePrcie) * Double.parseDouble(liter)), rcptsheetissue_code_amtsale));
		
		return payPrice;
	}

	/**
	 * DY ������ ItemInfo ���� �������� �ִ����� �����Ѵ�.
	 * 
	 * @param dyItemArray	: DY ������ ItemInfo
	 * @return
	 */
	private static boolean hasKeepNumber(POS_DY_Item[] dyItemArray) {
		LogUtility.getPumpMLogger().debug("[Pump M] DY Item Info �� �������� �ִ��� �����մϴ�.") ;
		boolean hasKeepNumber = false ;
		
		if ((dyItemArray != null) && (dyItemArray.length !=0)){
			for (int i = 0 ; i < dyItemArray.length ; i++) {
				String keepNumber = dyItemArray[i].getKeepissue_no() ;
				if ((keepNumber != null) && (!keepNumber.equals(""))) {
					hasKeepNumber = true ;
					break ;
				}
			}
		}
		
		LogUtility.getPumpMLogger().debug("[Pump M] DY Item Info �� �������� ���� ��� ������ �����ϴ�. hasKeepNumber="+hasKeepNumber) ;
		return hasKeepNumber ;
	}

	/**
	 * ODT �� ����� �������� ���� üũ�Ѵ�.
	 * 
	 * @param nozzleNo	: ���� ��ȣ
	 * @return
	 */
	public static boolean isNozzleConnectedToSelfODT(String nozzleNo) {
		int protocolTypeInt = getConnectedRepODTProtocolFromNozzleNo(nozzleNo) ;
		switch (protocolTypeInt) {
			case IPumpConstant.PUMP_PROTOCOL_DaSNo :
			case IPumpConstant.PUMP_PROTOCOL_NewDaSNo : //2012.09.21 ksm �����پ��� �߰�.
			case IPumpConstant.PUMP_PROTOCOL_GSC_SELF : //2016.03.18 CWI gsc �߰�.
			case IPumpConstant.PUMP_PROTOCOL_SOMO : {
				return true ;
			}
		}
		return false ;
	}

	/**
	 * ���� �����ȣ�� ���ؼ� �ŷ�ó ������ �����Ͽ� �����Ѵ�.
	 * 
	 * @param carno_nbr_short	: ���� ���� ��ȣ
	 * @return
	 */
	public static POS_DU_Car[] processPOSPumpM_DU_Car(String carno_nbr_short) {
		ArrayList<POS_DU_Car> duPumpMCarList = new  ArrayList<POS_DU_Car>() ;
		SqlSession session = null;
		
		try {
			session = SqlSessionFactoryManager.openSqlSession();

			T_KH_CUST_CAR_INFOData[] carInfoDataArray = 
				T_KH_CUST_CAR_INFOHandler.getHandler().getT_KH_CUST_CAR_INFODataByCarno_nbr_short(session, carno_nbr_short) ;

			LogUtility.getPumpMLogger().debug("[Pump M] T_KH_CUST_CAR_INFO ���̺� �������� ��ȣ("+ carno_nbr_short+ ")�� ��ȸ�� �մϴ�.") ;

			if ((carInfoDataArray == null) || (carInfoDataArray.length == 0)) {
				LogUtility.getPumpMLogger().debug("[Pump M] T_KH_CUST_CAR_INFO ���̺� �������� ��ȣ�� ��ȸ�� �Ͽ����� �����ϴ�.") ;
				return null ;
			} else {
				LogUtility.getPumpMLogger().debug("[Pump M] T_KH_CUST_CAR_INFO ���̺� �������� ��ȣ�� ��ȸ ���� ����� ������ �����ϴ�.") ;
				// Debug
				for (int i = 0 ; i < carInfoDataArray.length ; i++) {
					carInfoDataArray[i].print() ;
				}
			}

			for (int i = 0 ; i < carInfoDataArray.length ; i++) {
				String cust_code = carInfoDataArray[i].getCust_code() ;
				String store_code = carInfoDataArray[i].getStore_code() ;
				String carno_nbr = carInfoDataArray[i].getCarno_nbr() ;
				T_KH_CUST_CARD_INFOData[] cardInfoDataArray = 
					T_KH_CUST_CARD_INFOHandler.getHandler().getT_KH_CUST_CARD_INFODataByCarno_nbr(session, cust_code, store_code, carno_nbr) ;
			
				LogUtility.getPumpMLogger().debug("[Pump M] T_KH_CUST_CARD_INFO ���̺� ������ ���� ��ȸ�� �մϴ�. cust_code=" + 
						cust_code + "#store_code=" + store_code + "#carno_nbr=" + carno_nbr);

				T_KH_CUST_INFOData custInfoData =
					T_KH_CUST_INFOHandler.getHandler().getT_KH_CUST_INFOData(session, cust_code, store_code) ;
				
				
				/** [2008.10.30]
				 * ī�� ������ ���� ��쿡�� �翬�� �ŷ�ó ������ �־�� �Ѵ�. ������ ���� ��쵵 �ֱ� ������ �̿� ���� ó���� �Ʒ��� ���� �ϵ��� �Ѵ�.
				 * 	�ŷ�ó ���� ������ ���� �����Ͽ� ���� ��� ���� ������ �����ϵ��� �Ѵ�.
				 */
				if (custInfoData == null) {
					LogUtility.getPumpMLogger().debug("[Pump M] T_KH_CUST_INFO ���̺� ��ȸ�� �Ͽ����� ����� �����ϴ�. cust_code=" + cust_code) ;
				} else if ((cardInfoDataArray == null) || (cardInfoDataArray.length == 0)) {
					LogUtility.getPumpMLogger().debug("[Pump M] T_KH_CUST_CARD_INFO ���̺� ��ȸ�� �Ͽ����� ����� �����ϴ�. " +
							"���� ���� ��ȣ�� �ֽ��ϴ�.") ;
					
					POS_DU_Car duPumpMCar = new POS_DU_Car(carno_nbr , "", custInfoData.getCust_name() + "^" + custInfoData.getCust_code(), custInfoData.getCust_code()) ;					
					duPumpMCarList.add(duPumpMCar) ;
				} else {
					LogUtility.getPumpMLogger().debug("[Pump M] T_KH_CUST_INFO ���̺� ������ ���� ��ȸ�� �մϴ�.cust_code=" + cust_code + "#store_code=" + store_code) ;

					for (int j = 0 ; j < cardInfoDataArray.length ; j++) {
						if ((cardInfoDataArray != null) && (cardInfoDataArray.length !=0)) {
							if (custInfoData != null) {
								String car_no = carno_nbr ;		// ������ȣ
								String cust_card_no = cardInfoDataArray[j].getCardno_nbr_cust() ;	// �ŷ�ó ī�� ��ȣ
								String cust_name = custInfoData.getCust_name() ;		// �ŷ�ó ��

								POS_DU_Car duPumpMCar = new POS_DU_Car(car_no , cust_card_no, cust_name + "^" + cust_code, cust_code) ;
								LogUtility.getPumpMLogger().info(duPumpMCar.toString());
								
								duPumpMCarList.add(duPumpMCar) ;
							} else {
								LogUtility.getPumpMLogger().debug("[Pump M] T_KH_CUST_INFO ���̺� ��ȸ�� �Ͽ����� ����� �����ϴ�.") ;
								break ;
							}
						}
					}
				}
			}		
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}
		return duPumpMCarList.toArray(new POS_DU_Car[duPumpMCarList.size()]) ;
	}	
	
	/**
	 * 
	 * ���� ��ȸ ����� ������ȸ�� �Ѱǵ� �ȵǾ��� ��� PG ������ Pump A �� �����Ѵ�.
	 * �� ��� PG ������ ���� ��ȸ�� ���ٴ� ������ ��� �ȴ�.
	 *  
	 * @param duMsg		: POS Protocol �� ������ȸ ���� ���� (DU)
	 * @return
	 */
	public static WorkingMessage processPOSPumpM_DUForPumpA(POS_DU duMsg) {
		WorkingMessage workMsg = null ;
		if (duMsg.getDup() == 0) {
			workMsg = PG_WorkingMessage.createNotFoundCustomerPG_WorkingMessage(duMsg.getMessageID(), 
					duMsg.getDeviceID(), 
					duMsg.getDeviceID()) ;
		} else {
			workMsg = PumpMUtil.createPFWorkingMessage(duMsg) ;
		}
		return workMsg ;
	}	
	
	/**
	 * POS �� �����ϱ� ���ؼ� DY ������ �����Ѵ�.
	 * 
	 * ī�� ����		01:����ī��
	 * 				02:�ܻ�ī��
	 * 				03:�뿪���� (POS�� �ѵ�üũ ��û,POS �� ���۾��� ��� ������ ���. ���� TR �� POS �� ����)
	 * 				04:���⺸�� (POS�� �ѵ�üũ ��û,POS �� ���۾��� ��� ������ ���. ���� TR �� POS �� ����)
	 * 				05:VIP
	 * 				(01,05 �� ���� ī����� , 02~04 �� �ܻ�ī�� ����)
	 * 				09:�������� ����
	 * 
	 * ī�� ���� ����	01 : ��� ����
	 * 				02 : ������
	 * 				03 : �ŷ�ó ��
	 * 				04 : 1ȸ ����
	 * 				05 : ������
	 * 
	 * �ŷ� ����		01 : ����
	 * 				02 : ����
	 * 				03 : ����
	 * 
	 * �ѵ�����		ī�����=�ܻ�ī��,ī�����뱸��=[������|�ŷ�ó��]�ϰ�� �Ʒ� 01 Ȥ�� 02. �׿ܴ� 00
	 * 				01=�������ѵ�
	 * 				02=�ŷ�ó�ܻ��ѵ�
	 * 				00=�ǹ� ����
	 * 
	 * �ѵ��������	�ѵ������� [01|02] �ϰ�� �Ʒ� [01|02] ���. �׿ܴ� 00
	 * 				01=����
	 * 				02=�ݾ�
	 * 				00=�ǹ� ����
	 * 
	 * �ѵ� ����		�ѵ� ���� ������ [01|02] �ϰ�� ���.
	 * 				������ ��� �Ҽ��� 3�ڸ�
	 * 				�ݾ��� ���� ����
	 * 
	 * �ܷ�			�ѵ� ���� ������ [01|02] �ϰ�� ���.
	 * 				������ ��� �Ҽ��� 3�ڸ�
	 * 				�ݾ��� ���� ����
	 * 
	 * @param messageID		: Message ID
	 * @param nozzle_no		: ���� ��ȣ
	 * @param cust_card_ind	: ������ȣ/�ŷ�ó ī�� ��ȣ ����
	 * @param cust_card_no	: �ŷ�ó ī�� ��ȣ or ������ȣ
	 * @param taxFreeCust_type	: �鼼 �ŷ�ó ����
	 * @param fixedQty_yn	: �����Է� ����
	 * @param fixedQty		: ������
	 * @param goods_code	: ��ǰ �ڵ�
	 * @param basePrice		: ���� �ܰ�
	 * @param liter			: ������
	 * @param price			: ���� �ݾ�
	 * @param khTransactionID	: KH ó�� ��ȣ
	 * @param taxfreecust_type	: 0 : �鼼 �ƴ�, 1:��� �ŷ�ó , 2: �Ϲ�
	 * @param cust_code	: �ŷ�ó �ڵ� (���� ��ȣ�� ���ԵǾ� ���� ��� �ǹ� �ְ� ���)
	 * @return
	 * @throws Exception
	 *

	 */
	public static POS_DY processPOSPumpM_DY(String messageID, String nozzle_no, String cust_card_ind, 
			String cust_card_no, String taxFreeCust_type, String fixedQty_yn, String fixedQty,
			String goods_code, String basePrice, String liter, String price, String khTransactionID,
			String cust_code) throws Exception {
		
		POS_DY dyPumpM = null ;
		String bonGoodsCode = "" ;
		String deviceID = nozzle_no ;
		String cust_mileage_ind = ICode.CUST_MILEAGE_IND_1 ;		// �ܻ� �ŷ�ó ���ʽ� ���� ����
		String cust_no = "" ;										// �ŷ�ó ��ȣ
		String cust_name = "" ;										// �ŷ�ó ��
		String car_no = "" ;										// ���� ��ȣ
		String cust_cd_item = "" ;									// �ŷ�ó ����
		String card_code_base = ICode.CARD_CODE_BASE_06 ;			// ī�� ����
		String rcptsheetissue_ind_prtcarno = ICode.RCPSHEETISSUE_IND_PRTCARNO_1 ;	// ������������ȣ��¿���
		String rcptunitprc_ind_prt = ICode.RCPTUNITPRC_IND_PRT_1  ;					// �������ܰ�ǥ�⿩��
		String cardadj_ind = ICode.CARDADJ_IND_00 ;					// ī�� ���� ����
		String status_code_card = ICode.STATUS_CODE_CARD_01 ;		// �ŷ� ����
		String limit_type = ICode.DY_LIMIT_TYPE_00 ;				// �ѵ�����
		String adjbase_code_limit = ICode.ADJBASE_CODE_LIMIT_00 ; 	// �ѵ��������
		String limit = "0" ;										// �ѵ�����
		String save_expire_date	= "" ;								// ������ ���� ��ȿ �Ⱓ
		String save_head_title = "" ;								// ������ �Ӹ���	
		String save_foot_title1 = "" ;								// ������ ������1	
		String save_foot_title2 = "" ;								// ������ ������2	
		String led_code = IUPOSConstant.LEDCODE_1 ;					// LED Code
		String proc_ind_overlimit = ICode.PROC_IND_OVERLIMIT_00 ;	// �ܻ���� Ÿ��	(00 - 05) 00=����
		String oil_ind = ICode.OIL_IND_11 ; 						// ����/���� ���� (Default = 11)
		String unitprc_code_stdn = ICode.UNITPRC_CODE_STDN_01 ;		// �ܰ� ���� (03:�鼼�ܰ�)
		String rcptsheetissue_code_amtsale = "" ;					// ����ݾ�ó������	
//		String taxfree_cd = ICode.TAXFREE_CD_01 ;					// ���� �鼼 ���� (01 : ���� , 02:�鼼)
										// 2016. 4. 14. ���� 15:48:31, PI2, WooChul Jung. ���鼼 ������ "�ܰ� ����" �� ���ؼ� ó����.
		boolean isTaxFree = false ;									// ���鼼 ����
		double limit_remainsDou = 0 ;
		POS_DY_Item[] dyInfoArray = null ;
		String keepissue_ind = ICode.KEEPISSUE_IND_1 ;
		int state = ICustConstant.STATE_0 ;
		String temp_cust_mileage_ind = ICode.CUST_MILEAGE_IND_1 ;
		
		// �ڿ����� �������� �����Ѵ�. 12:�ڿ� - upkoo
		String storeCodeClass  = getStoreCodeClass();
		
		try {
			T_KH_PRODUCTData productData = T_KH_PRODUCTHandler.getHandler().getT_KH_PRODUCTData(goods_code) ;
			temp_cust_mileage_ind = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0256);
			oil_ind = productData.getOil_ind() ;
			bonGoodsCode = productData.getGoods_code_bonus() ;
		} catch (Exception e1) {
			LogUtility.getPumpMLogger().error(e1.getMessage(),e1) ;
		}
		
		CustReturnValue custReturnValue = null ;
		
		if (cust_card_ind.equals(ICode.CUST_CARD_IND_01)) {
			// ������ȣ�� ���� ���� ��û�� 
			custReturnValue = CustUtil.processCustWithCarNo(nozzle_no , cust_card_no, taxFreeCust_type, cust_code) ;
		} else {
			// �ŷ�ó ī�� ��ȣ�� �������� ��û��
			// �� ��¥�� ��������,�������ڸ� �׻� �񱳸� �ؼ�, �� ���̰� �ƴϸ�, PL �� ��ϵ��� ���� ������ ó���Ѵ�.
			// �� custReturnValue�� null�� �ȴ�.  - upkoo
			custReturnValue = CustUtil.processCustWithCustCardNo(nozzle_no , cust_card_no ,taxFreeCust_type) ;
		}
		
		// PreProcessing �� �����Ѵ�.
		PumpMObjectValidation.validateCustReturnValue(custReturnValue, fixedQty_yn, fixedQty) ;
		
		if (custReturnValue == null) {
			LogUtility.getPumpMLogger().debug("[Pump M] �ŷ�ó ���� ���� ���� ���� ��� Default ���� �����Ѵ�.") ;
			
			SqlSession session = null;
			try {
				session = SqlSessionFactoryManager.openSqlSession();

				T_KH_PRODUCTData productData = T_KH_PRODUCTHandler.getHandler().getT_KH_PRODUCTData(goods_code) ;
				T_NZ_NOZZLEData nzNozzleData = T_NZ_NOZZLEHandler.getHandler().getT_NZ_NOZZLEDataByNozzleNo(session, nozzle_no)[0] ;

				custReturnValue = new CustReturnValue() ;
				custReturnValue.setState(ICustConstant.STATE_1) ;
				custReturnValue.setType(ICustConstant.TYPE_DEFAULT) ;
				custReturnValue.setBasePrice(PumpMUtil.getBasePrice(productData, nzNozzleData)) ;
			} catch (Exception e) {
				LogUtility.getPumpMLogger().error(e.getMessage(),e);
			} finally {
				SqlSessionFactoryManager.closeSqlSession(session);
			} 
		}
		
		cust_no 									= custReturnValue.getCust_code() ;
		cust_name 									= custReturnValue.getCust_name() ;
		car_no 										= custReturnValue.getCarno_nbr() ;
		cust_cd_item 								= custReturnValue.getCust_cd_item() ;
		rcptsheetissue_ind_prtcarno 				= custReturnValue.getRcptsheetissue_ind_prtcarno() ;	
		rcptunitprc_ind_prt 						= custReturnValue.getRcptunitprc_ind_prt()  ;
		proc_ind_overlimit 							= custReturnValue.getProc_ind_overlimit() ;
		unitprc_code_stdn 							= custReturnValue.getUnitprc_code_stdn() ;
		status_code_card 							= custReturnValue.getTrans_code_status() ;		
		state 										= custReturnValue.getState() ;
		keepissue_ind 								= custReturnValue.getKeepissue_ind() ;
		rcptsheetissue_code_amtsale 				= custReturnValue.getRcptsheetissue_code_amtsale();
//		taxfree_cd 									= custReturnValue.getTaxfree_cd() ;
		
		if ((unitprc_code_stdn != null) && (unitprc_code_stdn.equals(ICode.UNITPRC_CODE_STDN_03))) {
			isTaxFree = true ;
		}
/*		2016. 4. 14. ���� 15:48:31, PI2, WooChul Jung.
 * 			������ �ʴ� �ڵ��̸�, �鼼���δ� �ܰ��������� �Ǵ�.
		if ((taxfree_cd != null) && (taxfree_cd.equals(ICode.TAXFREE_CD_02))) {
			isTaxFree = true ;
		}
		*/
		if (status_code_card.equals(ICode.STATUS_CODE_CARD_02) || status_code_card.equals(ICode.STATUS_CODE_CARD_03)) {
			LogUtility.getPumpMLogger().debug("[Pump M] ī����°� ���� Ȥ�� ���Ұ� �� ���Դϴ�.") ;
			// ī�尡 ���� Ȥ�� ���ҵ� ��� 
			state = ICustConstant.STATE_1 ;
		}

		// ī�� ���� ���� : card_code_base
		switch (state) {			
			case ICustConstant.STATE_0 : 
			case ICustConstant.STATE_1 :{
				card_code_base = ICode.CARD_CODE_BASE_06 ;
				break ;
			}
			case ICustConstant.STATE_10 : 
			case ICustConstant.STATE_11 : 
			case ICustConstant.STATE_100 : 
			case ICustConstant.STATE_110 : 
			case ICustConstant.STATE_12 : 
			case ICustConstant.STATE_13 :{
				// ����  �� 
				card_code_base = ICode.CARD_CODE_BASE_01 ;
				break ;
			}
			case ICustConstant.STATE_30 : 
			case ICustConstant.STATE_31 : 
			case ICustConstant.STATE_32 :
			case ICustConstant.STATE_33 :
			case ICustConstant.STATE_40 : 
			case ICustConstant.STATE_41 : 
			case ICustConstant.STATE_42 :
			case ICustConstant.STATE_43 :
			case ICustConstant.STATE_50 : 
			case ICustConstant.STATE_51 : 
			case ICustConstant.STATE_52 :
			case ICustConstant.STATE_53 :
			case ICustConstant.STATE_60 : 
			case ICustConstant.STATE_61 : 
			case ICustConstant.STATE_62 : 
			case ICustConstant.STATE_63 : 
			case ICustConstant.STATE_90 :
			case ICustConstant.STATE_91 :
			case ICustConstant.STATE_92 :
			case ICustConstant.STATE_93 :{
				// �ܻ� ��
				cust_mileage_ind = temp_cust_mileage_ind ;
				card_code_base = ICode.CARD_CODE_BASE_02 ;	
				break ;
			}
			case ICustConstant.STATE_70 : {
				// �뿪 ���� �� - ���ΰ�
				card_code_base = ICode.CARD_CODE_BASE_03 ;
				break ;
			}
			case ICustConstant.STATE_80 : {
				// ���� ���� �� - ���ΰ�
				card_code_base = ICode.CARD_CODE_BASE_04 ;
				break ;
			}
			default : {
				card_code_base = ICode.CARD_CODE_BASE_06 ;
				break ;
			}				
		}

		// ī�� ���� ���� ���� : cardadj_ind
		switch (state) {			
			case ICustConstant.STATE_0 : 
			case ICustConstant.STATE_1 :
			case ICustConstant.STATE_10 : 
			case ICustConstant.STATE_11 : 
			case ICustConstant.STATE_12 : 
			case ICustConstant.STATE_13 : 
			case ICustConstant.STATE_70 : 
			case ICustConstant.STATE_80 : 
			case ICustConstant.STATE_100 : 
			case ICustConstant.STATE_110 : {
				cardadj_ind = ICode.CARDADJ_IND_00 ;	// ��� ����
				break ;
			}
			case ICustConstant.STATE_30 : 
			case ICustConstant.STATE_31 : 
			case ICustConstant.STATE_32 : 
			case ICustConstant.STATE_33 : {
				cardadj_ind = ICode.CARDADJ_IND_01 ;	// ������
				break ;
			}
			case ICustConstant.STATE_40 : 	// �ܻ� �ŷ�ó - �������ѵ� - ����
			case ICustConstant.STATE_41 : 
			case ICustConstant.STATE_42 : 
			case ICustConstant.STATE_43 : {
				cardadj_ind = ICode.CARDADJ_IND_03 ;	// ������
				break ;
			}
			case ICustConstant.STATE_50 : // �ܻ� �ŷ�ó - �ŷ�ó���ѵ� - ����
			case ICustConstant.STATE_51 : 
			case ICustConstant.STATE_52 : 
			case ICustConstant.STATE_53 : {
				cardadj_ind = ICode.CARDADJ_IND_02 ;	// �ŷ�ó��
				break ;
			}
			case ICustConstant.STATE_60 : 
			case ICustConstant.STATE_61 : 
			case ICustConstant.STATE_62 : 
			case ICustConstant.STATE_63 : {
				cardadj_ind = ICode.CARDADJ_IND_04 ;	// 1ȸ ����
				break ;
			}
			case ICustConstant.STATE_90 : 
			case ICustConstant.STATE_91 : 
			case ICustConstant.STATE_92 : 
			case ICustConstant.STATE_93 : {
				cardadj_ind = ICode.CARDADJ_IND_05 ;	// �����Է� - upkoo
				break ;
			}
			default : {
				cardadj_ind = ICode.CARDADJ_IND_00 ;	// ��� ����	
				break ;
			}				
		}
		
		// ������ �������� ��ǰ �ڵ� ����ġ		- X3
		// PL ���� ����										- X2
		// PL ��ǰ �ڵ� ����ġ								- X1
		switch (state) {
			case ICustConstant.STATE_0 : 
			case ICustConstant.STATE_1 :{
				led_code = IUPOSConstant.LEDCODE_5 ;
				break ;
			}
			case ICustConstant.STATE_56 : {
				led_code = IUPOSConstant.LEDCODE_F ;
				break ;
			}

			case ICustConstant.STATE_15 : 
			case ICustConstant.STATE_35 : 
			case ICustConstant.STATE_45 : 
			case ICustConstant.STATE_55 : 
			case ICustConstant.STATE_65 : 
			case ICustConstant.STATE_95 : {
				led_code = IUPOSConstant.LEDCODE_E ;
				break ;
			}
			case ICustConstant.STATE_14 : 
			case ICustConstant.STATE_34 : 
			case ICustConstant.STATE_44 : 
			case ICustConstant.STATE_54 : 
			case ICustConstant.STATE_64 : 
			case ICustConstant.STATE_94 : {
				led_code = IUPOSConstant.LEDCODE_D ;
				break ;
			}
			case ICustConstant.STATE_13 : 
			case ICustConstant.STATE_33 : 
			case ICustConstant.STATE_43 : 
			case ICustConstant.STATE_53 : 
			case ICustConstant.STATE_63 : 
			case ICustConstant.STATE_93 : {
				led_code = IUPOSConstant.LEDCODE_6 ;
				break ;
			}
			/**
			 * 2016. 4. 14. ���� 15:48:31, PI2, WooChul Jung.
			 * 	Bug Fix. ���ݰŷ�ó �ε�,  ���� �ŷ�ó - PL ������ ���, PL ���������� �������� �ʴ´ٶ�� ó��.
			 */
			case ICustConstant.STATE_12 :
			case ICustConstant.STATE_32 :
			case ICustConstant.STATE_42 :
			case ICustConstant.STATE_52 :
			case ICustConstant.STATE_62 : 
			case ICustConstant.STATE_92 :  {
				proc_ind_overlimit = ICode.PROC_IND_OVERLIMIT_99 ;
				led_code = IUPOSConstant.LEDCODE_7 ;
				break ;
			}
			/**
			 * 2016. 4. 14. ���� 15:48:31, PI2, WooChul Jung.
			 * 	Bug Fix. ���ݰŷ�ó �ε�, PL ��ǰ �ڵ� ����ġ�� ���, �ŷ�ó���Ǹſ��ο� ���� ���ΰ� �Ϲ� �������� �Ǹ�
			 */
			case ICustConstant.STATE_11 : {
				String custsale_ind = null ;
				proc_ind_overlimit = ICode.PROC_IND_OVERLIMIT_99 ;
				try {
					custsale_ind = T_KH_STOREHandler.getHandler().getCustsale_ind() ;
				} catch (Exception e) {
					LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
				}				
				if ((custsale_ind != null) && (custsale_ind.equals("1"))) {
					LogUtility.getPumpMLogger().debug("[Pump M] ���� �ŷ�ó - PL ��ǰ �ڵ� ����ġ�̱� ������, '�ŷ�ó���Ǹſ���'(���帶����) �� �Ǹ��̱� " +
							"������ ���ΰ��� �ǸŸ� �Ѵ�. custsale_ind=" + custsale_ind) ;
					POS_DY_Item dyPumpM_Item = getPOSPumpM_DY_Item(	deviceID,
							goods_code,
							bonGoodsCode,
							oil_ind,
							basePrice,
							liter,
							price,
							khTransactionID,
							proc_ind_overlimit,
							false ,
							basePrice,
							isTaxFree,
							rcptsheetissue_code_amtsale) ;
					dyInfoArray = new POS_DY_Item[1] ;
					dyInfoArray[0] = dyPumpM_Item ;
					led_code = IUPOSConstant.LEDCODE_3 ; 
				} else {
					led_code = IUPOSConstant.LEDCODE_C ;
				}
				break ;
			}
			case ICustConstant.STATE_31 : {
				String custsale_ind = null ;
				
				try {
					custsale_ind = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0251);
				} catch (Exception e) {
					LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
				}
				
				if ((custsale_ind != null) && (custsale_ind.equals("1"))) {
					LogUtility.getPumpMLogger().debug("[Pump M] �ܻ� �ŷ�ó - ������ - PL ��ǰ�ڵ� ����ġ�̱� ������, '�ŷ�ó���Ǹſ���'(���帶����) �� �Ǹ��̱� " +
							"������ ���ΰ��� �ǸŸ� �Ѵ�. custsale_ind=" + custsale_ind) ;

					led_code = IUPOSConstant.LEDCODE_3 ;
					cardadj_ind = ICode.CARDADJ_IND_01 ;	// ������
					proc_ind_overlimit = ICode.PROC_IND_OVERLIMIT_00 ;
					String discountBasePrice = custReturnValue.getDiscountBasePrice() ;	
					boolean isDiscount = false ;
					if (Double.parseDouble(basePrice) > Double.parseDouble(discountBasePrice)) {
						isDiscount = true ;
					}
					POS_DY_Item dyPumpM_Item = getPOSPumpM_DY_Item(deviceID,
							goods_code,
							bonGoodsCode,
							oil_ind,
							basePrice,
							liter,
							price,
							khTransactionID,
							proc_ind_overlimit,
							isDiscount ,
							discountBasePrice,
							isTaxFree,
							rcptsheetissue_code_amtsale) ;
					dyInfoArray = new POS_DY_Item[1] ;
					dyInfoArray[0] = dyPumpM_Item ;
				} else {
					led_code = IUPOSConstant.LEDCODE_C ;
				}
				break ;
			}
			case ICustConstant.STATE_41 : {
				String custsale_ind = null ;
				
				try {
					custsale_ind = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0251);
				} catch (Exception e) {
					LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
				}
				
				if ((custsale_ind != null) && (custsale_ind.equals("1"))) {
					LogUtility.getPumpMLogger().debug("[Pump M] �ܻ� �ŷ�ó - �������ѵ� - PL ��ǰ�ڵ� ����ġ�̱� ������, '�ŷ�ó���Ǹſ���'(���帶����) �� �Ǹ��̱� " +
							"������ ���ΰ��� �ǸŸ� �Ѵ�. custsale_ind=" + custsale_ind) ;

					led_code = IUPOSConstant.LEDCODE_3 ;
					cardadj_ind = ICode.CARDADJ_IND_03 ;	// ������
					limit_type = ICode.DY_LIMIT_TYPE_01 ;	// ������ �ѵ�			
					LimitAmount limitAmount = custReturnValue.getLimitAmount() ;
					String discountBasePrice = custReturnValue.getDiscountBasePrice() ;
					double baseRemainsDou = Double.parseDouble(limitAmount.getRemainsAmount()) ;				
					double applyRemainsDou = 0 ;
					
					limit = limitAmount.getLimit() ;
					limit_remainsDou = GlobalUtility.substract(Double.parseDouble(limit) , 
							Double.parseDouble(limitAmount.getUsedAmount())) ;
					
					LogUtility.getPumpMLogger().debug("[Pump M] baseRemainsDou="+baseRemainsDou + 
																		"#limit_remainsDou=" +limit_remainsDou ) ;

					if (baseRemainsDou >= limit_remainsDou) {
						applyRemainsDou = limit_remainsDou ;
					} else {
						applyRemainsDou = baseRemainsDou ;
					}				
					
					if (applyRemainsDou <= 0) {
						LogUtility.getPumpMLogger().debug("[Pump M] �ܷ��� " + applyRemainsDou + 
								" �̾ 0���� �缳���մϴ�. �̴� �ѵ� �ʰ��Դϴ�.") ;
						limit_remainsDou = 0 ;
						led_code = IUPOSConstant.LEDCODE_E ;
					} else {				
						boolean isDiscount = false ;
						if (Double.parseDouble(basePrice) > Double.parseDouble(discountBasePrice)) {
							isDiscount = true ;
						}
						POS_DY_Item dyPumpM_Item = getPOSPumpM_DY_Item(deviceID,
								goods_code,
								bonGoodsCode,
								oil_ind,
								basePrice,
								liter,
								price,
								khTransactionID,
								proc_ind_overlimit,
								isDiscount ,
								discountBasePrice,
								isTaxFree,
								rcptsheetissue_code_amtsale) ;
						int priceOrLiter = limitAmount.getPricePrLiter();
						if (priceOrLiter == ICustConstant.LIMIT_AMOUNT_LITER) {
							adjbase_code_limit = ICode.ADJBASE_CODE_LIMIT_01 ;
							dyInfoArray = getPOSPumpM_DY_ItemArrayInLimitLiter(dyPumpM_Item,limit, applyRemainsDou) ;
						} else {
							adjbase_code_limit = ICode.ADJBASE_CODE_LIMIT_02 ;
							dyInfoArray = getPOSPumpM_DY_ItemArrayInLimitPrice(dyPumpM_Item,limit, applyRemainsDou) ;
						}
					}
				} else {
					led_code = IUPOSConstant.LEDCODE_C ;
				}
				
				break ;
			} 
			case ICustConstant.STATE_51 : {
				String custsale_ind = null ;
				
				try {
					custsale_ind = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0251);
				} catch (Exception e) {
					LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
				}
				
				if ((custsale_ind != null) && (custsale_ind.equals("1"))) {
					LogUtility.getPumpMLogger().debug("[Pump M] �ܻ� �ŷ�ó - �ŷ�ó���ѵ� - PL ��ǰ�ڵ� ����ġ�̱� ������, '�ŷ�ó���Ǹſ���'(���帶����) �� �Ǹ��̱� " +
							"������ ���ΰ��� �ǸŸ� �Ѵ�. custsale_ind=" + custsale_ind) ;

					led_code = IUPOSConstant.LEDCODE_3 ;
					cardadj_ind = ICode.CARDADJ_IND_02 ;	// �ŷ�ó��
					limit_type = ICode.DY_LIMIT_TYPE_02 ;	// �ŷ�ó�� �ѵ�		
					LimitAmount limitAmount = custReturnValue.getLimitAmount() ;
					String discountBasePrice = custReturnValue.getDiscountBasePrice() ;
					double baseRemainsDou = Double.parseDouble(limitAmount.getRemainsAmount()) ;				
					double applyRemainsDou = 0 ;
					
					limit = limitAmount.getLimit() ;
					limit_remainsDou = GlobalUtility.substract(Double.parseDouble(limit) , 
							Double.parseDouble(limitAmount.getUsedAmount())) ;

					LogUtility.getPumpMLogger().debug("[Pump M] baseRemainsDou="+baseRemainsDou + 
																		"#limit_remainsDou=" +limit_remainsDou ) ;
					
					if (baseRemainsDou >= limit_remainsDou) {
						applyRemainsDou = limit_remainsDou ;
					} else {
						applyRemainsDou = baseRemainsDou ;
					}				
					
					if (applyRemainsDou <= 0) {
						LogUtility.getPumpMLogger().debug("[Pump M] �ܷ��� " + applyRemainsDou + 
																			" �̾ 0���� �缳���մϴ�. �̴� �ѵ� �ʰ� �Դϴ�.") ;
						limit_remainsDou = 0 ;
						led_code = IUPOSConstant.LEDCODE_E ;
					} else {
						boolean isDiscount = false ;
						if (Double.parseDouble(basePrice) > Double.parseDouble(discountBasePrice)) {
							isDiscount = true ;
						}
						POS_DY_Item dyPumpM_Item = getPOSPumpM_DY_Item(deviceID,
								goods_code,
								bonGoodsCode,
								oil_ind,
								basePrice,
								liter,
								price,
								khTransactionID,
								proc_ind_overlimit,
								isDiscount ,
								discountBasePrice,
								isTaxFree,
								rcptsheetissue_code_amtsale) ;
						int priceOrLiter = limitAmount.getPricePrLiter() ;				
						if (priceOrLiter == ICustConstant.LIMIT_AMOUNT_LITER) {
							adjbase_code_limit = ICode.ADJBASE_CODE_LIMIT_01 ;
							dyInfoArray = getPOSPumpM_DY_ItemArrayInLimitLiter(dyPumpM_Item,limit, applyRemainsDou) ;
						} else {
							adjbase_code_limit = ICode.ADJBASE_CODE_LIMIT_02 ;
							dyInfoArray = getPOSPumpM_DY_ItemArrayInLimitPrice(dyPumpM_Item,limit, applyRemainsDou) ;
						}
					} 
				} else {
					led_code = IUPOSConstant.LEDCODE_C ;
				}
				
				break ;
			}
			case ICustConstant.STATE_61 : 
			case ICustConstant.STATE_91 : {
				String custsale_ind = null ;
				
				try {
					custsale_ind = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0251);
				} catch (Exception e) {
					LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
				}
				
				// �ڿ������� ���
				if(ICode.STORE_CODE_CLASS_12.equals(storeCodeClass) || ICode.STORE_CODE_CLASS_11.equals(storeCodeClass)) {
					String amount1 = "";
					String discountBasePrice = "";
					try {
						//cardadj_ind-> 01:������ 02:�ŷ�ó�� 03:������ 04:1ȸ����	05:�����Է�
						// fixedQty_yn-> �����Է¿���
						if ((custsale_ind != null) && (custsale_ind.equals("1"))) {
							LogUtility.getPumpMLogger().debug("[Pump M] �ڿ������� ���� �� �ŷ�ó - �����Է� - PL ��ǰ�ڵ� ����ġ�� ���ΰ� �ܻ��Ǹ��Ѵ�.") ;
							discountBasePrice = custReturnValue.getDiscountBasePrice() ;
							
							// �ŷ������� ����(PL�̾���) �����Է� ���̸鼭 ���αݾ��� 0�ΰ��� discountBasePrice�� basePrice�� �Ѵ�. - upkoo 
							if(discountBasePrice == null || "".equals(discountBasePrice)) {
								discountBasePrice = basePrice;
							}
							
							boolean isDiscount = false ;
							if (Double.parseDouble(basePrice) > Double.parseDouble(discountBasePrice)) {
								isDiscount = true ;
							}
							
							LogUtility.getPumpMLogger().debug("####################" +
									"#custsale_ind=" + custsale_ind + 
									"#cust_no=" + cust_no +
									"#goods_code=" + goods_code +
									"#fixedQty_yn=" + fixedQty_yn +
									"#basePrice=" + basePrice +
									"#discountBasePrice=" + discountBasePrice
									);
							
							if ((fixedQty_yn != null) && (fixedQty_yn.equals("1"))) {
								// ���� �Է� ���ε�, ������ �Է��� ���
								led_code = IUPOSConstant.LEDCODE_3 ;
								cardadj_ind = ICode.CARDADJ_IND_04 ;	// 1ȸ ����
								amount1 = fixedQty ;	
								
							} else {	
								// �����Է� ���ε�, ������ �Է����� �������
								LogUtility.getPumpMLogger().debug("[Pump M] �ڿ������� ��� �ܻ� �ŷ�ó - �����Է°��ε� ������ �Է����� �������" +
										"������ ���ͷ� ������ �����Ѵ�.") ;
								
								// �ܻ� �� - ���� �Է�
								led_code = IUPOSConstant.LEDCODE_B ;
								cardadj_ind = ICode.CARDADJ_IND_05 ;	// ���� �Է�
								
								// �����Է��� �����ʾ��� ��� ������ ���͸� �ִ´�.
								if("".equals(amount1)) amount1 = liter;
							}
							
							POS_DY_Item dyPumpM_Item = getPOSPumpM_DY_Item(deviceID,
									goods_code,
									bonGoodsCode,
									oil_ind,
									basePrice,
									liter,
									price,
									khTransactionID,
									proc_ind_overlimit,
									isDiscount ,
									discountBasePrice,
									isTaxFree,
									rcptsheetissue_code_amtsale) ;

							dyInfoArray = getPOSPumpM_DY_ItemArrayWhenOneAmountCustomer(dyPumpM_Item, Double.parseDouble(amount1),
									keepissue_ind) ;
						}
						else {
							led_code = IUPOSConstant.LEDCODE_7 ;
						}
						
					} catch (Exception e) {
						LogUtility.getPumpMLogger().error(e.getMessage(),e);
					} 
				}
				// ���������� ���
				else {
					proc_ind_overlimit = ICode.PROC_IND_OVERLIMIT_99 ;
					try {
						custsale_ind = T_KH_STOREHandler.getHandler().getCustsale_ind() ;
					} catch (Exception e) {
						LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
					}				
					if ((custsale_ind != null) && (custsale_ind.equals("1"))) {
						LogUtility.getPumpMLogger().debug("[Pump M] ��ǰ ����ġ ������, '�ŷ�ó���Ǹſ���'(���帶����) �� �Ǹ��̱� " +
								"������ ���ΰ��� �ǸŸ� �Ѵ�. custsale_ind=" + custsale_ind) ;
						POS_DY_Item dyPumpM_Item = getPOSPumpM_DY_Item(	deviceID,
								goods_code,
								bonGoodsCode,
								oil_ind,
								basePrice,
								liter,
								price,
								khTransactionID,
								proc_ind_overlimit,
								false ,
								basePrice,
								isTaxFree,
								rcptsheetissue_code_amtsale) ;
						dyInfoArray = new POS_DY_Item[1] ;
						dyInfoArray[0] = dyPumpM_Item ;
						led_code = IUPOSConstant.LEDCODE_1 ; 
					} else {
						led_code = IUPOSConstant.LEDCODE_C ;
					}
				}
				break ;
			}
			case ICustConstant.STATE_70 :
				// �뿪 ���� �� - ���ΰ�
			case ICustConstant.STATE_80 : {
				// ���� ���� �� - ���ΰ�
				proc_ind_overlimit = ICode.PROC_IND_OVERLIMIT_00 ;
				String discountBasePrice = custReturnValue.getDiscountBasePrice() ;
				boolean isDiscount = false ;
				if (Double.parseDouble(basePrice) > Double.parseDouble(discountBasePrice)) {
					isDiscount = true ;
				}
				POS_DY_Item dyPumpM_Item = getPOSPumpM_DY_Item(deviceID,
						goods_code,
						bonGoodsCode,
						oil_ind,
						basePrice,
						liter,
						price,
						khTransactionID,
						proc_ind_overlimit,
						isDiscount ,
						discountBasePrice,
						isTaxFree,
						rcptsheetissue_code_amtsale) ;
				dyInfoArray = new POS_DY_Item[1] ;
				dyInfoArray[0] = dyPumpM_Item ;
				
				if (state == ICustConstant.STATE_12) {
					led_code = IUPOSConstant.LEDCODE_1 ;
				} else if (state == ICustConstant.STATE_70) {
					led_code = IUPOSConstant.LEDCODE_9 ;
				} else {
					led_code = IUPOSConstant.LEDCODE_8 ;
				}
				break ;
			}
			case ICustConstant.STATE_100 : 
				// ���� ȭ��
			case ICustConstant.STATE_110 :
				// VIP
			case ICustConstant.STATE_10 : {
				// ���� �� 
				cardadj_ind = ICode.CARDADJ_IND_01 ;	// ������
				proc_ind_overlimit = ICode.PROC_IND_OVERLIMIT_99 ;
				String discountBasePrice = custReturnValue.getDiscountBasePrice() ;
				boolean isDiscount = false ;
				
				// 2013.10.06  ksm  ���� �����ʿ�.
				if (Double.parseDouble(basePrice) > Double.parseDouble(discountBasePrice)) {
					isDiscount = true ;
				}
				
				POS_DY_Item dyPumpM_Item = getPOSPumpM_DY_Item(deviceID,
						goods_code,
						bonGoodsCode,
						oil_ind,
						basePrice,
						liter,
						price,
						khTransactionID,
						proc_ind_overlimit,
						isDiscount ,
						discountBasePrice,
						isTaxFree,
						rcptsheetissue_code_amtsale) ;
				dyInfoArray = new POS_DY_Item[1] ;
				dyInfoArray[0] = dyPumpM_Item ;
				
				if (state == ICustConstant.STATE_100) {
					led_code =IUPOSConstant.LEDCODE_4 ;
				} else if (state == ICustConstant.STATE_110) {
					led_code = IUPOSConstant.LEDCODE_2 ;
				} else {
					led_code = IUPOSConstant.LEDCODE_A ;
				} 
				break ;
			}
			case ICustConstant.STATE_30 : {
				// �ܻ� �� - ������
				led_code = IUPOSConstant.LEDCODE_3 ;
				cardadj_ind = ICode.CARDADJ_IND_01 ;	// ������
				proc_ind_overlimit = ICode.PROC_IND_OVERLIMIT_00 ;
				String discountBasePrice = custReturnValue.getDiscountBasePrice() ;	
				boolean isDiscount = false ;
				if (Double.parseDouble(basePrice) > Double.parseDouble(discountBasePrice)) {
					isDiscount = true ;
				}
				POS_DY_Item dyPumpM_Item = getPOSPumpM_DY_Item(deviceID,
						goods_code,
						bonGoodsCode,
						oil_ind,
						basePrice,
						liter,
						price,
						khTransactionID,
						proc_ind_overlimit,
						isDiscount ,
						discountBasePrice,
						isTaxFree,
						rcptsheetissue_code_amtsale) ;
				dyInfoArray = new POS_DY_Item[1] ;
				dyInfoArray[0] = dyPumpM_Item ;
				break ;				
			}
			case ICustConstant.STATE_40 : {	// �ܻ� �ŷ�ó - �������ѵ� - ����
				led_code = IUPOSConstant.LEDCODE_3 ;
				cardadj_ind = ICode.CARDADJ_IND_03 ;	// ������
				limit_type = ICode.DY_LIMIT_TYPE_01 ;	// ������ �ѵ�			
				LimitAmount limitAmount = custReturnValue.getLimitAmount() ;
				String discountBasePrice = custReturnValue.getDiscountBasePrice() ;
				double baseRemainsDou = Double.parseDouble(limitAmount.getRemainsAmount()) ;				
				double applyRemainsDou = 0 ;
				
				limit = limitAmount.getLimit() ;
				limit_remainsDou = GlobalUtility.substract(Double.parseDouble(limit) , 
						Double.parseDouble(limitAmount.getUsedAmount())) ;
				
				LogUtility.getPumpMLogger().debug("[Pump M] baseRemainsDou="+baseRemainsDou + 
																	"#limit_remainsDou=" +limit_remainsDou ) ;

				if (baseRemainsDou >= limit_remainsDou) {
					applyRemainsDou = limit_remainsDou ;
				} else {
					applyRemainsDou = baseRemainsDou ;
				}				
				
				if (applyRemainsDou <= 0) {
					LogUtility.getPumpMLogger().debug("[Pump M] �ܷ��� " + applyRemainsDou + 
							" �̾ 0���� �缳���մϴ�. �̴� �ѵ� �ʰ��Դϴ�.") ;
					limit_remainsDou = 0 ;
					led_code = IUPOSConstant.LEDCODE_E ;
				} else {				
					boolean isDiscount = false ;
					if (Double.parseDouble(basePrice) > Double.parseDouble(discountBasePrice)) {
						isDiscount = true ;
					}
					POS_DY_Item dyPumpM_Item = getPOSPumpM_DY_Item(deviceID,
							goods_code,
							bonGoodsCode,
							oil_ind,
							basePrice,
							liter,
							price,
							khTransactionID,
							proc_ind_overlimit,
							isDiscount ,
							discountBasePrice,
							isTaxFree,
							rcptsheetissue_code_amtsale) ;
					int priceOrLiter = limitAmount.getPricePrLiter();
					if (priceOrLiter == ICustConstant.LIMIT_AMOUNT_LITER) {
						adjbase_code_limit = ICode.ADJBASE_CODE_LIMIT_01 ;
						dyInfoArray = getPOSPumpM_DY_ItemArrayInLimitLiter(dyPumpM_Item,limit, applyRemainsDou) ;
					} else {
						adjbase_code_limit = ICode.ADJBASE_CODE_LIMIT_02 ;
						dyInfoArray = getPOSPumpM_DY_ItemArrayInLimitPrice(dyPumpM_Item,limit, applyRemainsDou) ;
					}
				}
			
				break ;
			}
			case ICustConstant.STATE_50 : {	// �ܻ� �ŷ�ó - �ŷ�ó���ѵ� - ����
				led_code = IUPOSConstant.LEDCODE_3 ;
				cardadj_ind = ICode.CARDADJ_IND_02 ;	// �ŷ�ó��
				limit_type = ICode.DY_LIMIT_TYPE_02 ;	// �ŷ�ó�� �ѵ�		
				LimitAmount limitAmount = custReturnValue.getLimitAmount() ;
				String discountBasePrice = custReturnValue.getDiscountBasePrice() ;
				double baseRemainsDou = Double.parseDouble(limitAmount.getRemainsAmount()) ;				
				double applyRemainsDou = 0 ;
				
				limit = limitAmount.getLimit() ;
				limit_remainsDou = GlobalUtility.substract(Double.parseDouble(limit) , 
						Double.parseDouble(limitAmount.getUsedAmount())) ;

				LogUtility.getPumpMLogger().debug("[Pump M] baseRemainsDou="+baseRemainsDou + 
																	"#limit_remainsDou=" +limit_remainsDou ) ;
				
				if (baseRemainsDou >= limit_remainsDou) {
					applyRemainsDou = limit_remainsDou ;
				} else {
					applyRemainsDou = baseRemainsDou ;
				}				
				
				if (applyRemainsDou <= 0) {
					LogUtility.getPumpMLogger().debug("[Pump M] �ܷ��� " + applyRemainsDou + 
																		" �̾ 0���� �缳���մϴ�. �̴� �ѵ� �ʰ� �Դϴ�.") ;
					limit_remainsDou = 0 ;
					led_code = IUPOSConstant.LEDCODE_E ;
				} else {
					boolean isDiscount = false ;
					if (Double.parseDouble(basePrice) > Double.parseDouble(discountBasePrice)) {
						isDiscount = true ;
					}
					POS_DY_Item dyPumpM_Item = getPOSPumpM_DY_Item(deviceID,
							goods_code,
							bonGoodsCode,
							oil_ind,
							basePrice,
							liter,
							price,
							khTransactionID,
							proc_ind_overlimit,
							isDiscount ,
							discountBasePrice,
							isTaxFree,
							rcptsheetissue_code_amtsale) ;
					int priceOrLiter = limitAmount.getPricePrLiter() ;				
					if (priceOrLiter == ICustConstant.LIMIT_AMOUNT_LITER) {
						adjbase_code_limit = ICode.ADJBASE_CODE_LIMIT_01 ;
						dyInfoArray = getPOSPumpM_DY_ItemArrayInLimitLiter(dyPumpM_Item,limit, applyRemainsDou) ;
					} else {
						adjbase_code_limit = ICode.ADJBASE_CODE_LIMIT_02 ;
						dyInfoArray = getPOSPumpM_DY_ItemArrayInLimitPrice(dyPumpM_Item,limit, applyRemainsDou) ;
					}
				}
				break ;
			}
			case ICustConstant.STATE_60 : {
				// �ܻ� �� - 1ȸ ����
				led_code = IUPOSConstant.LEDCODE_3 ;
				cardadj_ind = ICode.CARDADJ_IND_04 ;	// 1ȸ ����
				String amount1 = custReturnValue.getAmount1() ;			
				String discountBasePrice = custReturnValue.getDiscountBasePrice() ;
				boolean isDiscount = false ;
				if (Double.parseDouble(basePrice) > Double.parseDouble(discountBasePrice)) {
					isDiscount = true ;
				}
				POS_DY_Item dyPumpM_Item = getPOSPumpM_DY_Item(deviceID,
						goods_code,
						bonGoodsCode,
						oil_ind,
						basePrice,
						liter,
						price,
						khTransactionID,
						proc_ind_overlimit,
						isDiscount ,
						discountBasePrice,
						isTaxFree,
						rcptsheetissue_code_amtsale) ;

				dyInfoArray = getPOSPumpM_DY_ItemArrayWhenOneAmountCustomer(dyPumpM_Item, Double.parseDouble(amount1),
						keepissue_ind) ;
				break ;
			}
			case ICustConstant.STATE_90 : {	
			
				// �ڿ������� ���
				if(ICode.STORE_CODE_CLASS_12.equals(storeCodeClass) || ICode.STORE_CODE_CLASS_11.equals(storeCodeClass)) {
				
					String amount1 = fixedQty ;		
					String discountBasePrice = custReturnValue.getDiscountBasePrice() ;
					boolean isDiscount = false ;
					if (Double.parseDouble(basePrice) > Double.parseDouble(discountBasePrice)) {
						isDiscount = true ;
					}
				
					LogUtility.getPumpMLogger().debug("[Pump M] led_code=3#cardadj_ind=4" +
																		"#amount1=" + amount1 + 
																		"#discountBasePrice=" + discountBasePrice + 
																		"#goods_code=" + goods_code  + 
																		"#bonGoodsCode=" + bonGoodsCode  + 
																		"#oil_ind=" + oil_ind  + 
																		"#basePrice=" + basePrice  + 
																		"#liter=" + liter  + 
																		"#price=" + price + 
																		"#khTransactionID=" + khTransactionID  + 
																		"#proc_ind_overlimit=" + proc_ind_overlimit + 
																		"#isDiscount=" + isDiscount + 
																		"#discountBasePrice=" + discountBasePrice +
																		"#isTaxFree=" + isTaxFree + 
																		"#rcptsheetissue_code_amtsale=" + rcptsheetissue_code_amtsale
						) ;
				
					if ((fixedQty_yn != null) && (fixedQty_yn.equals(ICode.FIXEDQTY_YN_1))) {
						
						LogUtility.getPumpMLogger().debug("[Pump M]�ڿ������� ��� �ܻ� �ŷ�ó - �����Է� - ����! " +
						"���� �Է� ���ε�, ������ �Է��� ��� ���ΰ� �ܻ��Ǹ�!") ;
						
						// ���� �Է� ���ε�, ������ �Է��� ���
						led_code = IUPOSConstant.LEDCODE_3 ;
						cardadj_ind = ICode.CARDADJ_IND_04 ;	// 1ȸ ����
					}
					else {
						// �ܻ� �� - ���� �Է�
						led_code = IUPOSConstant.LEDCODE_B ;
						cardadj_ind = ICode.CARDADJ_IND_05 ;	// ���� �Է�
					
						// upkoo �߰� - �����Է��� �����ʾ��� ���, ������ liter�� �ִ´�.
						if("".equals(amount1)) {
							amount1 = liter;
						}
					}
				
					POS_DY_Item dyPumpM_Item = getPOSPumpM_DY_Item(deviceID,
							goods_code,
							bonGoodsCode,
							oil_ind,
							basePrice,
							liter,
							price,
							khTransactionID,
							proc_ind_overlimit,
							isDiscount ,
							discountBasePrice,
							isTaxFree,
							rcptsheetissue_code_amtsale) ;

					dyInfoArray = getPOSPumpM_DY_ItemArrayWhenOneAmountCustomer(dyPumpM_Item, Double.parseDouble(amount1),
							keepissue_ind) ;
				
					break ;
				}
				else {
					if ((fixedQty_yn != null) && (fixedQty_yn.equals(ICode.FIXEDQTY_YN_1))) {
						// ���� �Է� ���ε�, ������ �Է��� ���
						led_code = IUPOSConstant.LEDCODE_3 ;
						cardadj_ind = ICode.CARDADJ_IND_04 ;	// 1ȸ ����
						String amount1 = fixedQty ;		
						String discountBasePrice = custReturnValue.getDiscountBasePrice() ;
						boolean isDiscount = false ;
						if (Double.parseDouble(basePrice) > Double.parseDouble(discountBasePrice)) {
							isDiscount = true ;
						}
						POS_DY_Item dyPumpM_Item = getPOSPumpM_DY_Item(deviceID,
								goods_code,
								bonGoodsCode,
								oil_ind,
								basePrice,
								liter,
								price,
								khTransactionID,
								proc_ind_overlimit,
								isDiscount ,
								discountBasePrice,
								isTaxFree,
								rcptsheetissue_code_amtsale) ;

						dyInfoArray = getPOSPumpM_DY_ItemArrayWhenOneAmountCustomer(dyPumpM_Item, Double.parseDouble(amount1),
								keepissue_ind) ;
					} else {				
						// �ܻ� �� - ���� �Է�
						led_code = IUPOSConstant.LEDCODE_B ;
						cardadj_ind = ICode.CARDADJ_IND_05 ;	// ���� �Է�
					}
					break ;
				}
			}
			default : {
				LogUtility.getPumpMLogger().warn("[Pump M] �� �αװ� ������ �ڵ忡 ������ �ִ�.") ;
				led_code = IUPOSConstant.LEDCODE_7 ;
			}
		}
		
		if (hasKeepNumber(dyInfoArray)) {			
			try {
				String storeCode = T_KH_STOREHandler.getHandler().getStoreCode() ;
				T_NZ_INFOData nzInfoData = T_NZ_INFOHandler.getHandler().getT_NZ_INFODataByStoreCode(storeCode) ;

				SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd") ;
				// ������ ��ȿ�Ⱓ
				String expireDate = nzInfoData.getSave_expire_date() ;
				T_KH_STOREData storeData = 
				T_KH_STOREHandler.getHandler().getT_KH_STOREDataByStoreCode(storeCode) ;
				// ������
				String bizHourDate = storeData.getBizhour_date();
				
				// ���� �����Ͽ� ������ ��ȿ�Ⱓ�� ���Ѵ�.
				// ������ ���°� �ùٸ��� �ʴٸ� ���� ��¥���� ������ ��ȿ�Ⱓ�� ���Ѵ�.
				if (bizHourDate.length() == 8) {
					// ������ �� �⵵
					int bizYear = Integer.parseInt(bizHourDate.substring(0, 4));
					// ������ �� ��
					int bizMonth = Integer.parseInt(bizHourDate.substring(4, 6));
					// ������ �� ��
					int bizDay = Integer.parseInt(bizHourDate.substring(6, 8));
					// �����Ͽ� ������ ��ȿ�Ⱓ�� ���Ѵ�
					save_expire_date = formater.format( new GregorianCalendar(bizYear, bizMonth-1, 
							bizDay + Integer.parseInt(expireDate)).getTime()) ;
				} 
				else {
					// ������ ��ȿ�Ⱓ�� ms������ ��ȯ�Ѵ�. 
					long after31Day = 1000 * 60 * 60 * 24 * Long.parseLong(expireDate);
					save_expire_date = formater.format(new Date(System.currentTimeMillis() + after31Day)) ;
				} // end if
				
				save_head_title = nzInfoData.getSave_head_title() ;
				save_foot_title1 = nzInfoData.getSave_foot_title1() ;
				save_foot_title2 = nzInfoData.getSave_foot_title2() ;
				
			} catch (Exception e) {
				LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
			}			
		}
		
		dyPumpM = new POS_DY(messageID, 
				deviceID, 
				khTransactionID,
				cust_card_ind,
				cust_card_no,
				cust_mileage_ind,
				taxFreeCust_type,
				fixedQty_yn,
				GlobalUtility.getStringValue(fixedQty),
				cust_no,
				cust_name,
				car_no,
				cust_cd_item,
				card_code_base,
				rcptsheetissue_ind_prtcarno,
				rcptunitprc_ind_prt,
				cardadj_ind,
				status_code_card,
				limit_type,
				adjbase_code_limit,
				limit,
				Double.toString(limit_remainsDou),
				save_expire_date,
				save_head_title,
				save_foot_title1,
				save_foot_title2,
				led_code,
				dyInfoArray) ;
		return dyPumpM;
	}
	
	
	/**
	 * �����⿡ ������ �ܰ��� ���������� �����Ѵ�.
	 * 
	 * @param dyPumpM_Item	: DY ����
	 */
	private static void resetBeforeBasePrice(POS_DY_Item dyPumpM_Item) {	

		LogUtility.getPumpMLogger().debug("[Pump M] ���������� ���ΰ��� �����մϴ�. ���Ǵ� �����ʹ� ������ �����ϴ�.") ;
		LogUtility.getPumpMLogger().info(dyPumpM_Item.toString());


		dyPumpM_Item.setUnitPrice_after_discount(dyPumpM_Item.getUnitPrice_before_discount()) ;
		dyPumpM_Item.setOilPrice_after_discount(dyPumpM_Item.getOilPrice_before_discount()) ;
		dyPumpM_Item.setTaxPrice(GlobalUtility.getTaxPrice(dyPumpM_Item.getOilPrice_before_discount())) ;
		dyPumpM_Item.setPrice_before_tax(PumpMUtil.getPrice_before_tax(
				dyPumpM_Item.getOilPrice_before_discount(), dyPumpM_Item.getTaxPrice())) ;
		
		dyPumpM_Item.setUnitDiscount_ind("0") ;

		LogUtility.getPumpMLogger().debug("[Pump M] ���ΰ��� ���� ����� ������ �����ϴ�.") ;
		LogUtility.getPumpMLogger().info(dyPumpM_Item.toString());
	}
	
	/**
	 * �����⿡ ������ �ܰ��� ���������� �����Ѵ�.
	 * 
	 * @param dyPumpM_Item	: DY ����
	 * @param rentlimit_proc_ind_overlimit	: �ܻ����Ÿ��
	 */
	private static void resetBeforeBasePrice(POS_DY_Item dyPumpM_Item , String rentlimit_proc_ind_overlimit) {	

		LogUtility.getPumpMLogger().debug("[Pump M] ���������� �����մϴ�. ���Ǵ� �����ʹ� ������ �����ϴ�.") ;
		LogUtility.getPumpMLogger().info(dyPumpM_Item.toString());
		LogUtility.getPumpMLogger().debug("[Pump M] rentlimit_proc_ind_overlimit=" + rentlimit_proc_ind_overlimit) ;

		if (rentlimit_proc_ind_overlimit.equals("02") || rentlimit_proc_ind_overlimit.equals("04")) {
			resetBeforeBasePrice(dyPumpM_Item) ;
		}
	}

	/**
	 * �鼼 ī�� ������ ���� Item Info �� �籸���Ҷ� ���ȴ�.
	 * 
	 * @param itemInfoItem
	 */
	public static void resetTaxFreePrice(UPOSMessage_ItemInfo_Item itemInfoItem) {
		
		LogUtility.getPumpMLogger().debug("[Pump M] �鼼 ���̱� ������ ������ 0���� �����մϴ�.") ;
		
		if (itemInfoItem == null) return ;
		
		try {
			String goodsCode = itemInfoItem.getGoodsCode() ;
			String taxFreePrice = T_KH_PRODUCTHandler.getHandler().getPrc_amt_taxfreeByGoods_code(goodsCode) ;
			
			String oilAmount = itemInfoItem.getOilAmount() ;								// ����(8b)
			String unitPrice_after_discount = itemInfoItem.getUnitPrice_after_discount() ;	// �����Ĵܰ�(10b)
			String tax_ind = itemInfoItem.getTax_ind() ;									// ���鼼����(2b)
			String price_before_tax = itemInfoItem.getPrice_before_tax() ;					// ���ް���(10b)
			String taxPrice = itemInfoItem.getTaxPrice() ;									// ����(10b)
			String oilPrice_after_discount = itemInfoItem.getOilPrice_after_discount() ;	// �����ıݾ�(10b)
			String unitDiscount_ind = itemInfoItem.getUnitDiscount_ind() ;					// ���ο���(1b)
			
			if ((Double.parseDouble(taxFreePrice) * 1000L) < Double.parseDouble(unitPrice_after_discount)) {
				unitDiscount_ind = ICode.DY_UNITDISCOUNT_IND_1 ;
			}		
			
			tax_ind = ICode.TAXFREE_CD_02 ;
			taxPrice = "0" ;
			double real_oilAmount = Double.parseDouble(oilAmount) / 1000L ;
			double real_price =  real_oilAmount * Double.parseDouble(taxFreePrice) ;
			oilPrice_after_discount = GlobalUtility.getStringValue(real_price) ;
			price_before_tax = oilPrice_after_discount ;
			unitPrice_after_discount = GlobalUtility.getMultipleWith1000(taxFreePrice) ;
			
			itemInfoItem.setUnitPrice_after_discount(unitPrice_after_discount) ;
			itemInfoItem.setTax_ind(tax_ind) ;
			itemInfoItem.setPrice_before_tax(price_before_tax) ;
			itemInfoItem.setTaxPrice(taxPrice) ;
			itemInfoItem.setOilPrice_after_discount(oilPrice_after_discount) ;
			itemInfoItem.setUnitDiscount_ind(unitDiscount_ind) ;
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		
		LogUtility.getPumpMLogger().debug("[Pump M] ���� ��� ������ �����ϴ�.") ;
		if (itemInfoItem != null) LogUtility.getPumpMLogger().debug(itemInfoItem.toString()) ;
		else {
			LogUtility.getPumpMLogger().debug("[Pump M] ItemInfoItem �� �����ϴ�.") ;
		}

	}
	
	/**
	 * ����� ���� �ݾ��� null �� �ʱ�ȭ ��Ű�� �Լ�.
	 * ����� ���� �ݾ��� null�̸� get�Լ� - getSa_min_amt() ������ DB���� ���� ���Ѵ�.
	 * ��߿� POS���� ����� ȯ�� ���� ���� �ǽð� �ݿ��ϱ� ���� ÷����.
	 * added by yhcheon at 2009.05.19
	 */
	public static void setNull_Sa_min_amt() {
		PumpMUtil.SA_MIN_AMT = null ;
	}

	/**
	 * 
	 * @param payment
	 * @param pumpingPrice
	 * @return
	 */
	public static boolean shouldSendingRejectAndReApproval(String payment, String pumpingPrice,  boolean isCreditCard) {
		boolean rlt = false ;
		try {
			//������ ��쿡�� ����� �ּ� ���� �ݾװ� ������� �Ž������� �߻���Ų��.
			if (!isCreditCard )
				return false;
			
			if (IConstant.reRequest) {			// true  �� �Ѱ��� �𸣰���? ksm
				int paymentInt = Integer.parseInt(GlobalUtility.getStringValue(payment)) ;
				int pumpingPriceInt = Integer.parseInt(GlobalUtility.getStringValue(pumpingPrice)) ;
				int sas_min_amt = Integer.parseInt(GlobalUtility.getStringValue(getSa_min_amt())) ;
				
				int diff = paymentInt - pumpingPriceInt ;
				if (diff > sas_min_amt) {
					LogUtility.getPumpMLogger().info("[Pump M] paymentInt - pumpingPriceInt(" + diff + ") > sas_min_amt("+sas_min_amt+")") ;
					rlt = true ;
				}else{
					LogUtility.getPumpMLogger().info("[Pump M] ȯ�漳�� 0272 (����)�Ž����� ���رݾ� : " + sas_min_amt + " ���� ����� ����.") ;
				}
			} else {
				rlt = false ;
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
		}
		
		return rlt ;
	}

	public static Preamble createBeaconMessagePreamble(String key, int dest, BeaconMessage message , String log) {
		int from = SyncManager.DISE_PUMP_MODULE ;
		
		return Preamble.createPreamble(key, from, dest , message , log) ;
	}
}
