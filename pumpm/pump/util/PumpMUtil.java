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
	private static String SA_MIN_AMT = null ;	// 셀프 주유기 재승인 인정 금액
		
	/**
	 * Preset 설정으로 인해 정액과 단가를 이용하여 정량 구하기
	 * [2008.11.18] by 오부장님
	 * 		정량의 경우 소수점 6자리 이후로는 절사하고, 소수점 5자리 반올림, 그 이후 4자리 반올림 이후 나온 소수점 3자리까지 
	 * 		정보를 이용한다.
	 * @param price		: 주유 금액
	 * @param basePrice	: 주유 단가
	 * @return
	 */
	public static String calculateLiterFromPriceAndBasePrice(String price, String basePrice) {
		double priceDou = Double.parseDouble(price) ;
		double basePriceDou = Double.parseDouble(basePrice) ;
		
		double literDou = getPresetLiter(priceDou, basePriceDou) ;
				
		return Double.toString(literDou) ; 
	}
	
	/**
	 * 수량과 단가를 이용하여 금액을 구하기
	 * [2008.11.18] by 오부장님
	 * 		소수점 첫째자리에서 반올림 한다.
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
	 * 주유기로 부터 올라온 단가 숫자를 POS Format 으로 변경
	 * 
	 * @param basePrice	: 주유기로 부터 올라온 단가 숫자 
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
	 * 		단가 (4.2)
	 */
	public static String convertBasePriceFromUPOSToPumpA(String basePriceStr) {
		if (GlobalUtility.isNullOrEmptyString(basePriceStr)) basePriceStr = "0" ;

		double basePriceDouble = Double.parseDouble(basePriceStr) ;
		double basePrice = basePriceDouble / GlobalUtility.INT_1000 ;
		return  PumpMUtil.convertNumberFormatFromPOSToPump(Double.toString(basePrice) , 4, 2) ;
	}
	
	
	
	/**
	 * 주유기로 부터 올라온 리터 숫자를 POS Format 으로 변경
	 * 
	 * @param liter	: 주유기로 부터 올라온 리터 숫자 
	 * @return
	 */
	public static String convertLiterFromPumpToPOS(String liter) {
		return convertNumberFormatFromPumpToPOS(liter , 3 ,8) ;
	}
	
	/**
	 * 
	 * @param liter : 승인 응답 전문의 Liter (실제 Liter * 1000)
	 * 
	 * @return
	 * 		수량 (4.3)
	 */
	public static String convertLiterFromUPOSToPumpA(String literStr) {
		if (GlobalUtility.isNullOrEmptyString(literStr)) literStr = "0" ;
		
		double literDouble = Double.parseDouble(literStr) ;
		double liter = literDouble / GlobalUtility.INT_1000 ;
		return PumpMUtil.convertNumberFormatFromPOSToPump(Double.toString(liter) , 4, 3) ;
	}
	
	/**
	 * POS 로 부터 받은 숫자를 주유기에 맞게끔 변경
	 * 
	 * @param src					: POS 로 부터 받은 숫자
	 * @param returnValuePosLength	: 주유기가 원하는 정수부 자리수
	 * @param returnValueDeciLength	: 주유기가 원하는 소수점 자리수
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
	 * 주유기로 부터 올라온 숫자를 POS 의 Format 에 맞게 변경
	 * 
	 * @param src			: 주유기로 부터 받은 숫자
	 * @param deciLength	: 주유기로 부터 받은 숫자에서 소수점 자리수
	 * @return
	 */
	public static String convertNumberFormatFromPumpToPOS(String src , int deciLength) {
		return convertNumberFormatFromPumpToStandardFormat(src, deciLength) ;
	}	
	
	
	/**
	 * 주유기로 부터 올라온 숫자를 POS 의 Format 에 맞게 변경
	 * 
	 * @param src			: 주유기로 부터 받은 숫자
	 * @param deciLength	: 주유기로 부터 받은 숫자에서 소수점 자리수
	 * @param returnValueLength	: POS 로 전송할 전체 길이 (현재 사용되지 않음)
	 * @return
	 */
	public static String convertNumberFormatFromPumpToPOS(String src , int deciLength, int returnValueLength) {
		return convertNumberFormatFromPumpToPOS(src, deciLength) ;
	}
	
	
	/**
	 * 주유기로 부터 올라온 숫자를 Standard Format 으로 변경
	 * 
	 * @param srcData		: 주유기로 부터 받은 숫자
	 * @param deciLength	: 주유기로 부터 받은 숫자에서 소수점 자리수
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
	 * UPOS 로 보내기 위해서 1000 을 곱한다.
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
	 * UPOSMessage 의 단가 및 리터를 Standard Format 에 맞게끔 변경한다.
	 * 
	 * @param srcData	: UPOSMessage 의 단가 or 리터 정보
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
	 * 주유기로 부터 올라온 금액 숫자를 POS Format 으로 변경
	 * 
	 * @param price	: 주유기로 부터 올라온 금액 숫자 
	 * @return
	 */
	public static String convertPriceFromPumpToPOS(String price) {
		return GlobalUtility.appending0Pre(price , 8) ;
	}
	
	/**
	 * UPOSMessage 의 금액을 Pump A 의 Spec 에 맞게 변경한다.
	 * 
	 * @param priceStr : UPOSMessage 의 금액
	 * @return
	 * 		결제 금액
	 */
	public static String convertPriceFromUPOSToPumpA(String priceStr) {
		if (GlobalUtility.isNullOrEmptyString(priceStr)) priceStr = "0" ;

		double priceDouble = Double.parseDouble(priceStr) ;
		double price = priceDouble ;
		return  PumpMUtil.convertNumberFormatFromPOSToPump(Double.toString(price) , 8, 0) ;
	}
	
	/**
	 * 
	 * 주유기로 부터 올라온 Total Gauage 숫자를 POS Format 으로 변경
	 * 
	 * @param totalLiter	: 주유기로 부터 올라온 Total Gauage 숫자 
	 * @return
	 */
	public static String convertTotalLiterFromPumpTOPOS(String totalLiter) {
		return convertNumberFormatFromPumpToPOS(totalLiter , 3 ,10) ;
	}
	
	/**
	 * fromArray 의 내용을 destArray 에 추가한다.
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
	 * 주유기/셀프ODT/충전기 초기화 정보를 구성할때 마지막으로 추가해야 할 정보를 구성한다.
	 * 
	 * @param storeCode	: 매장 코드
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
	 * 주유기/셀프ODT/충전기 초기화 정보를 전송하기 전에 물리적인 정보를 담은 전문(M1)을 구성한다.
	 * 
	 * @param storeCode	: 매장 코드
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
//				LogUtility.getPumpMLogger().debug("[Pump M] 주유기가 없어서, M1 전문을 구성할 수가 없습니다.") ;
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		return workMsgList ;
	}

	/**
	 * 가상의 주유시작 자료를 생성한다.
	 * 주유 시작 없이 주유 중/완료 데이터가 왔기 때문에, 임의로 주유 시작 전문을 생성해서 테이블에 넣는다
	 * 
	 * @param nozzle_no	: 노즐 번호
	 * @return
	 */	
	public static String createFakePumpStartContent(String nozzle_no) {
		String khTransactionID = null ;
		
		try {
			LogUtility.getPumpMLogger().warn("[Pump M] Create fakePumping Start content.nozzle_no="+nozzle_no) ;
			int nozzle_noInt = Integer.parseInt(nozzle_no) ;
			
			/**
			 * [2008.11.18] by WooChul Jung
			 * 		다음과 같은 Normal 한 상황과 Exception 한 상황이 있다.
			 * 			1. 주유시작 -> Preset -> 주유중
			 * 				이 경우 가상의 주유시작 전문을 다시 생성하며, 문제는 전혀 없다.
			 * 			2. 주유중 -> (100 만원이 넘는경우) -> Pump A 로 부터 Preset 이 전송되곤 한다.(PumpA 에러)
			 * 				이 경우 주유시작 전문을 다시 생성하며, 금액을 0 으로 설정해 버리면 보정된 금액이 의미 없게 된다.
			 * 		위의 두가지 이유로 인해 아래 initPumpPrice 함수 호출을 제거한다.
			 */
//			PumpMPriceManager.initPumpPrice(nozzle_noInt) ;
	
			khTransactionID = PumpMTransactionManager.getInstance().getKHTransactionID(nozzle_no, IPumpConstant.KH_PUMP_START) ;
			
			// 그 전의 Total Gauage 를 사용한다.
			String startTotalGuage = PumpMPriceManager.getTotalGauage(nozzle_noInt) ;
			try {
				T_KH_PUMP_TRData trData = T_KH_PUMP_TRHandler.getHandler().getT_KH_PUMP_TRData(khTransactionID) ;
				/**
				 * [2008.11.18] by WooChul Jung
				 * 		이미 주유 시작 정보가 테이블에 Update 된 경우에는 Update 하지 않는다.
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
	 * 주유기/셀프ODT/충전기를 초기화 하기 위한 모든 초기화 전문을 구성한다.
	 * 
	 * @param storeCode	: 매장 코드
	 * @param nozzle_no	: 노즐 번호
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
	 * 충전기 충전원 해지 요청
	 * POS 로 부터 받은 KI 전문을 P9_WorkingMessage Object 로 변환한다.
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
	 * POS 로 부터 받은 DI 전문을 PA_WorkingMessage Object 로 변환한다.
	 * 
	 * @param diPump	: POS 로 부터 받은 DI 전문
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
	 * POS 로 부터 받은 DK 전문을 PB_WorkingMessage Object 로 변환한다.
	 * 
	 * @param dkPump	: POS 로 부터 받은 DK 전문
	 * @return
	 */
	public static PB_WorkingMessage createPB_WorkMsg(POS_DK dkPump) {
		PB_WorkingMessage pbWorkMsg = new PB_WorkingMessage() ;
		pbWorkMsg.setNozzleNo(dkPump.getDeviceID()) ;
		pbWorkMsg.setCommandSet(dkPump.getCommand()) ;
		
		//==>> soon 20211104 OWIN 정액(2)/정량(3) 추가
		String command = dkPump.getCommand();
		
		//LogUtility.getLogger().info("[BEACON TEST] dkPump.getDeviceType() : " + dkPump.getDeviceType());
		//LogUtility.getLogger().info("[BEACON TEST] command : " + command);
		
		// 오윈 주유허가 요청일 경우 로그 기록
		if(command.equals(ICode.PRESET_QTY_PRC_IND_OWIN_PRICE) || command.equals(ICode.PRESET_QTY_PRC_IND_OWIN_LITER)) {
			LogUtility.getLogger().info("[Pump M] <Beacon Process> Received POS_DK Messaged 정액(2)/정량(3) : [" + command +"]");
		}
		
		// 정액/정량 요청에 대해 세팅
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
	 * 충전기로 부터 요청한 차량단축 번호 조회에 대한 응답을 전송한다.
	 * 
	 * @param duMsg		: POS 로 부터 받은 차량 단축 번호 조회 결과
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
	 * POS 로 전송하기위한 Preamble Object 생성
	 * 
	 * @param key		: Unique Key
	 * @param dest		: 목적지 (POS)
	 * @param message	: 전송할 전문
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
	 * POS 로 부터 받은 DN 전문을 주유기로 전송하기 위해서 PQ_WorkingMessage Object 를 생성한다.
	 * 
	 * @param dnMsg	: DN 전문
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
	 * POS 로 부터 받은 CX 전문을 PU_WorkingMessage Object 로 변환한다.
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
	 * POS 로 전송하기위한 Preamble Object 생성
	 * 
	 * @param key		: Unique Key
	 * @param dest		: 목적지 (POS)
	 * @param message	: 전송할 전문
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
	 * 주유기만 초기화 하기 위한 정보를 생성한다.
	 * 
	 * @param storeCode	: 매장 코드
	 * @param nozzle_no	: 노즐 번호 (null 일 경우 전체 주유기)
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
//				LogUtility.getPumpMLogger().debug("[Pump M] 주유기가 없어서, P3 전문을 구성할 수가 없습니다.") ;
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		return workMsgList ;	
	}
	
	/**
	 * 주유시작 자료를 생성한다.
	 * 	- T_KH_PUMP_TR 에 주유시작 내용을 저장한다.
	 * 
	 * @param sjWorkMsg	: Pump A 로 부터받은 주유 시작 전문
	 * @return
	 */
	public static String createPumpStartContent(SJ_WorkingMessage sjWorkMsg) {
		String khproc_no = null ;
	    try {
	    	int nozzle_noInt = Integer.parseInt(sjWorkMsg.getNozzleNo()) ;
	    	
    	    if (PumpMTransactionManager.getInstance().isSameState(sjWorkMsg.getNozzleNo(),IPumpConstant.KH_PUMP_START)) {
    			LogUtility.getPumpMLogger().info("[Pump M] Same state(KH_PUMP_START). update Creation Time only") ;
	    	    khproc_no = PumpMTransactionManager.getInstance().getKHTransactionID(sjWorkMsg.getNozzleNo()) ;	    	    	  
	    	    T_KH_PUMP_TRHandler.getHandler().updateCreationTime(khproc_no) ;		// 주유 시작 시간만 Update
    		} else {
	    	    khproc_no = PumpMTransactionManager.getInstance().getKHTransactionID(sjWorkMsg.getNozzleNo(),IPumpConstant.KH_PUMP_START) ;	    	    	  
	    	    String startTotalGuage = PumpMUtil.convertNumberFormatFromPumpToPOS(sjWorkMsg.getTotalGauge() , 3 ,11) ;

	    	    T_KH_PUMP_TRHandler.getHandler().updatePumpStartInfo_BY_khproc_no(khproc_no, startTotalGuage) ;
	    	    PumpMPriceManager.setTotalGauage(nozzle_noInt, startTotalGuage) ;							    	    	  
	    		LogUtility.getPumpMLogger().info("[Pump M] insert pumping start info into Table, nozID=" + sjWorkMsg.getNozzleNo()) ;
	    		
	    		// 이 주유건에 대한 단가를 저장한다.
	    		PumpMPriceManager.setBasePrice(nozzle_noInt, 
	    				Double.parseDouble(PumpMUtil.getBasePriceFromKHProcNo(sjWorkMsg.getNozzleNo(), khproc_no))) ;
    		}
    	    
    		// 2009년 10월 5일 추영대 추가.
    		GlobalUtility.printAnalysisLog(	"pumpM", 
    				khproc_no, 
    				sjWorkMsg.getNozzleNo(), 
    				"",	
    				"주유시작", 
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
	 * POS 로 부터 받은 DO 전문을 QF_WorkingMessage Object 로 변환한다.
	 * 
	 * @param doPump	: POS 로 부터 받은 DO 전문
	 * @return
	 */
	public static QF_WorkingMessage createQF_WorkMsg(POS_DO doPump) {
		QF_WorkingMessage gfWorkMsg = new QF_WorkingMessage() ;
		gfWorkMsg.setNozzleNo(doPump.getDeviceID()) ;
		gfWorkMsg.setMessageID(doPump.getMessageID()) ;
		return gfWorkMsg ;
	}
	
	/**
	 * 충전기만 초기화 하기 위한 정보를 생성한다.
	 * 
	 * @param storeCode	: 매장 코드
	 * @param rechargeodt_no	: 충전기 번호 (null 일 경우 모든 충전기)
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
				LogUtility.getPumpMLogger().debug("[Pump M] 충전기 ODT가 없어서, D0 전문을 보내지 않습니다.") ;
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		return workMsgList ;
	}
	
	/**
	 * 셀프 주유기만 초기화 하기 위한 정보를 생성한다.
	 * 
	 * @param storeCode	: 매장 코드
	 * @param selfodt_no	: 셀프 ODT 번호 (null 일 경우 모든 셀프 ODT)
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
//				LogUtility.getPumpMLogger().debug("[Pump M] 셀프 ODT가 없어서, P1 전문을 구성할 수가 없습니다.") ;
			}
				
			WorkingMessage p2WorkMsg = PumpMUtil.getP2WorkingMessage(storeCode, selfodt_no) ;
			if (p2WorkMsg != null) {
				LogUtility.getPumpMLogger().debug("[Pump M] Create P2 for Self ODT") ;
				p2WorkMsg.setMessageID(GlobalUtility.getUniqueMessageID()) ;
				workMsgList.add(p2WorkMsg) ;
			} else {
//				LogUtility.getPumpMLogger().debug("[Pump M] 셀프 ODT가 없어서, P2 전문을 구성할 수가 없습니다.") ;
			}
			
			WorkingMessage p5WorkMsg = PumpMUtil.getP5WorkingMessage(storeCode, selfodt_no) ;			
			if (p5WorkMsg != null) {
				LogUtility.getPumpMLogger().debug("[Pump M] Create P5 for Self ODT") ;
				p5WorkMsg.setMessageID(GlobalUtility.getUniqueMessageID()) ;
				workMsgList.add(p5WorkMsg) ;
			} else {
//				LogUtility.getPumpMLogger().debug("[Pump M] 셀프 ODT 가 없어서, P5 전문을 구성할 수가 없습니다.") ;
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
	 * 셀프 ODT PC 매장 환경 설정 정보 요청
	 * 매장 환경설정정보를 전달
	 * 
	 * @param storeCode : 매장 코드
	 * @param odt_no : null 일 경우 모든 ODT 이고, 특정 번호인 경우 그 번호만.
	 * @return : PC WorkingMessage
	 * @throws Exception
	 */
	private static PC_WorkingMessage getPCWorkingMessage(String storeCode, String selfodt_no) {
		PC_WorkingMessage pCWorkingMsg = new PC_WorkingMessage();
		Vector<PC_StoreCodeInfo> storeCodeInfoVector = new Vector<PC_StoreCodeInfo>() ;		
		Vector<String> odtIdVector = new Vector<String>() ;
		
		try {
			//복합결제사용구분 - 0: 미사용, 1: 전체사용, 2:바로주유사용, 3:현대CarPay사용
			String code_7113 = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_7113);
			
			//현금영수증 의무발행 사용여부 - 0:미사용, 1:사용
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
					odtIdVector.add(nozzleDataList[i].getNozzle_no()) ;	// Self ODT 번호
				}
			} else {
				odtIdVector.add(selfodt_no) ;
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		}
		
		LogUtility.getPumpMLogger().debug("[결제모듈 프로젝트] Integer.toString(storeCodeInfoVector.size()): "+ Integer.toString(storeCodeInfoVector.size())) ;
		
		for(PC_StoreCodeInfo storeCodeValue : storeCodeInfoVector) {
			LogUtility.getLogger().debug("[Pump M] 매장 환경설정 코드정보 : " + storeCodeValue.getCode());
			LogUtility.getLogger().debug("[Pump M] 매장 환경설정 코드정보 : " + storeCodeValue.getValue());
		}
		
		pCWorkingMsg.setOdtId(odtIdVector) ;	
		pCWorkingMsg.setStoreCodeCount(Integer.toString(storeCodeInfoVector.size()));
		pCWorkingMsg.setStoreCodeInfo(storeCodeInfoVector);
		
		return pCWorkingMsg;
	}

	/**
	 * added by yhcheon at 2009.05.18
	 * 셀프 주유기 운영중 초기화 하는 함수 ( P1, P2 )
	 * 
	 * @param storeCode	: 매장 코드
	 * @param selfodt_no	: 셀프 ODT 번호 (null 일 경우 모든 셀프 ODT)
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
//				LogUtility.getPumpMLogger().debug("[Pump M] 셀프 ODT가 없어서, P1 전문을 구성할 수가 없습니다.") ;
			}
				
			WorkingMessage p2WorkMsg = PumpMUtil.getP2WorkingMessage(storeCode, selfodt_no) ;
			if (p2WorkMsg != null) {
				LogUtility.getPumpMLogger().debug("[Pump M] Create P2 for Self ODT") ;
				p2WorkMsg.setMessageID(GlobalUtility.getUniqueMessageID()) ;
				workMsgList.add(p2WorkMsg) ;
			} else {
//				LogUtility.getPumpMLogger().debug("[Pump M] 셀프 ODT가 없어서, P2 전문을 구성할 수가 없습니다.") ;
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		return workMsgList ;
	}
	
	/**
	 * POS 로 부터 받은 DB 전문을 T_NZ_INFO Table 에 넣기 위해서 T_NZ_INFOData Object 로 변환한다.
	 * 
	 * @param data	: POS 로 부터 받은 DB 전문
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
	 * POS 로 부터 받은 DD 전문 중 노즐 정보를 T_NZ_NOZZLEData Object 로 변환한다.
	 * 
	 * @param data	: POS 로 부터 받은 DD 전문내의 노즐 정보
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
	 * POS 로 부터 받은 KH 전문 (Time Parameter) 을 테이블에 저장하기 위해서 Internal Object (T_NZ_PARAMETERData) 로 변환한다.
	 * 
	 * @param info	: KH 전문내 NozzleInfo 데이터
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
	 * CAT M 으로 전송하기위한 Preamble Object 생성
	 * 
	 * @param key		: Unique Key
	 * @param dest		: 목적지 (CAT M or CMS M)
	 * @param message	: 전송할 전문
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
	 * 주유기로 전송하기위한 Preamble Object 생성
	 * 
	 * @param key		: Unique Key
	 * @param dest		: 목적지 (Pump A)
	 * @param message	: 전송할 전문
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
	 * 상품 단가를 가져 온다.
	 * 
	 * @param goods_code		: 상품 코드
	 * @param self_ind_exist	: 주유기 노즐 타입
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
	 * 주유기에 맞는 상품 단가를 가져 온다. Self/Semi-Self 주유기인 경우 셀프단가를, 그 외는 점두가를 응답한다.
	 * 
	 * @param productData	: 상품 데이터
	 * @param nzNozzleData	: 노즐 데이터
	 * @return
	 */
	public static String getBasePrice(T_KH_PRODUCTData productData , String self_ind_exist) {
		String basePrice = null ;
//		if (self_ind_exist.equals(IPumpConstant.DEVICE_TYPE_SEMI_SELF) ||
//				self_ind_exist.equals(IPumpConstant.DEVICE_TYPE_SELF_PUMP) ||
//				self_ind_exist.equals(IPumpConstant.DEVICE_TYPE_ODT_SELF))
		if (self_ind_exist.equals(ICode.SELF_IND_EXIST_03_SEMI_SELF) ||
				self_ind_exist.equals(ICode.SELF_IND_EXIST_02_SELF_PUMP)) {
			basePrice = productData.getUnitprc_amt_self() ;	// 셀프 단가
			//셀프 단가가 0이면 점두가 적용
			if ( basePrice == null){
				basePrice = productData.getShopfrontprc_amt() ; // 점두가
			} else {
				if ( GlobalUtility.getPositiveValue(basePrice).equals("0")) {
					basePrice = productData.getShopfrontprc_amt() ; // 점두가
				}
			}
		} else
			basePrice = productData.getShopfrontprc_amt() ; // 점두가		
		return basePrice ;
	}
	
	/**
	 * 주유기에 맞는 상품 단가를 가져 온다. Self/Semi-Self 주유기인 경우 셀프단가를, 그 외는 점두가를 응답한다.
	 * 
	 * @param productData	: 상품 데이터
	 * @param nzNozzleData	: 노즐 데이터
	 * @return
	 */
	public static String getBasePrice(T_KH_PRODUCTData productData , T_NZ_NOZZLEData nzNozzleData) {
		String self_ind_exist = nzNozzleData.getSelf_ind_exist() ;
		return getBasePrice(productData, self_ind_exist) ;
	}	
	
	/**
	 * 특정 주유건에 대해서 그 주유건에 대한 단가를 구한다.
	 * 이는 점두가일수도 있고, 할인된 단가일수도 있다.
	 * 
	 * @param nozzle_no	: 노즐 번호
	 * @param khproc_no	: KH 처리번호
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
	 * 노즐의 단가를 요청한다.
	 * 
	 * @param nozzle_no	: 노즐 번호
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
	 * 상품 단가를 요청한다.
	 * 
	 * @param unitprc_code_stdn
	 * 		1 : 점두가
	 * 		2 : 셀프단가
	 * 		3 : 면세단가
	 * 		4 : 목표가
	 * 		5 : 사입단가
	 * @param productData	: 상품정보
	 * @return
	 */
	public static String getBasePriceWithUnitPrcCode(String unitprc_code_stdn, T_KH_PRODUCTData productData) {
		String unitPrice = null ;
		switch (unitprc_code_stdn) {
			case ICode.UNITPRC_CODE_STDN_01 :	// 점두가
				unitPrice = productData.getShopfrontprc_amt() ;
				break ;
			case ICode.UNITPRC_CODE_STDN_02 :	// 셀프 단가
				unitPrice = productData.getUnitprc_amt_self() ;
				break ;		
			case ICode.UNITPRC_CODE_STDN_03 :	// 면세단가
				unitPrice = productData.getPrc_amt_taxfree() ;
				break ;
			case ICode.UNITPRC_CODE_STDN_04 : {	// 목표가
				try {
					unitPrice = T_KH_PRC_AMT_GOALHandler.getHandler().getPrc_amt_goal(productData.getGoods_code()) ; 
				} catch (Exception e) {
					LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
				}
				break ;
			}
			case ICode.UNITPRC_CODE_STDN_05 :	// 사입단가
				unitPrice = productData.getPrc_amt_saip() ;
				break ;
		}
		
		if ((unitPrice == null) || unitPrice.equals("") || unitPrice.equals("0")) {
			LogUtility.getPumpMLogger().warn("[Pump M] 단가 기준( " + unitprc_code_stdn + ") 이 존재하지 않습니다." +
					" 이는 CAT 단말기 UI 상 에러로 처리하도록 설정합니다.") ;
			unitPrice = null ;
		}
		return unitPrice ;
	}
	
	/**
	 * 카드 번호에서 앞선 16 자리 구하기
	 * 
	 * @param cardNumber	: 카드 번호
	 * @return
	 */
	public static String getCardNumberPre16Length(String cardNumber) {
		return GlobalUtility.getStringWithSpecificStringLength(cardNumber, 16) ;
	}
	

	/**
	 * 노즐 번호와 연결되어 있는 ODT 의 Protocol 을 리턴한다.
	 * 
	 * @param nozzleNo	: 노즐 번호
	 * @return
	 */
	public static int getConnectedODTProtocolFromNozzleNo(String nozzleNo) {
		int protocolInt = IPumpConstant.PUMP_PROTOCOL_DEFAULT ;
		try {
			// 노즐 타입을 먼저 조사 (POS 에서 ODT 번호를 기입할 수 있는 오류가 발생할 수 있기 때문.
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
	 * 노즐 번호와 연결되어 있는 ODT 의 Protocol 을 리턴한다.
	 * 
	 * @param nozzleNo	: 노즐 번호
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
				case 55 :{	// 동화 프라임 셀프 추가 (추영대, 2009.01.06)
					protocolInt = IPumpConstant.PUMP_PROTOCOL_SOMO ;
					break ;
				}
				case 75 : {
					protocolInt = IPumpConstant.PUMP_PROTOCOL_Recharge ;
					break ;
				}
				
				// TCP/IP 통신방식 충전기ODT(76) 추가  양일준, 2016.04-21
				case 76 : {
					protocolInt = IPumpConstant.PUMP_PROTOCOL_NewRecharge ;
					break ;
				}
					
				// 2012.09.21 ksm 신형다쓰노 추가
				case 37: {
					protocolInt = IPumpConstant.PUMP_PROTOCOL_NewDaSNo;
					break;
				}
				// 2016.03.18 CWI 신형gsc 추가
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
	 * 특정 주유기의 경우 단가가 전송되지 않는다. 이로 인해 만약 주유완료의 단가가 000000 혹은 999999 인 경우는 
	 * Pump M 에서 관리하고 있는 단가를 이용하여 POS 로 전송한다.
	 * 
	 * @param nozzleNo		: 노즐 번호
	 * @param basePrice		: 주유기로 부터 받은 단가 (Pump A 로부터 전송되어진 단가)
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
	 * 충전기 ODT D0 환경 설정 정보 요청
	 * 
	 * @param storeCode : 매장 코드
	 * @param odt_no 	: null 일 경우 모든 충전기에 대한 정보 수집
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
		d0WorkingMsg.setStoreCode(storeCode) ;         //매장 코드 추가 2016.04.21 - 양일준
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
	 * 충전기 ODT D1 환경 설정 정보 요청
	 * 
	 * @param storeCode : 매장 코드
	 * @param nozzle_no	: 노즐 번호(충전기 번호)
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
	 * 보관증 번호 발행한다.
	 * 보관증 번호 규칙은 다음과 같다.
	 * 		YYMMDD1XXX (XXX 는 000 부터 999 까지이다.)
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

			// 보관증 앞에 들어갈 날짜
			String currentDay = "";
			// 보관증 뒤에 들어갈 순번
			String newSequenceNo = "";
			
			// 자영 충전소 구분
			if (ICode.STORE_CODE_CLASS_12.equals(storeCode) || ICode.STORE_CODE_CLASS_11.equals(storeCode)) {
				// 자영 충전소는 yymm
				currentDay = GlobalUtility.getDateYYYYMMDD().substring(2, 6) ;
				// 자영 충전소는 5자리 순번 
				newSequenceNo = "00001";
				
			} else {
				// yymmdd
				currentDay = GlobalUtility.getDateYYYYMMDD().substring(2) ;
				// 3자리 순번
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
					// DB에 있는 보관증 번호와 현재 날짜와 비교하여 다르다면 새로 부여한다.
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
		LogUtility.getPumpMLogger().debug("[Pump M] 보관증 유효기간을 발급합니다. keekeepNoPeriodDay="+keepNoPeriodDay) ;
		int day = 0 ;
		String periodDay = null ;
		
		try {
			day = Integer.parseInt(keepNoPeriodDay) ;
		} catch (Exception e) {
			LogUtility.getPumpMLogger().warn("[Pump M] 보관증 유효기간이 테이블에 없어서 0 으로 설정합니다.") ;
			day = 0 ;
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		
		long timeLog = System.currentTimeMillis() ;
		long preTimeLog = timeLog - (1000L * 60L * 60L * 24L * day) ;
		SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd") ;
		periodDay = formater.format(new Date(preTimeLog)) ;
		
		LogUtility.getPumpMLogger().debug("[Pump M] 보관증 유효기간은 다음과 같습니다. periodDay="+periodDay) ;
		return periodDay ;
	}
	
	
	/**
	 * 주유기/셀프 ODT/충전기 초기화를 위해서 M1 전문을 생성한다.
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
				// PI2, 2016.03.18, SC의 아이피값 가져옴
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
	 * 노즐 번호와 연결된 ODT 번호 요청
	 * 
	 * @param nozzle_no	: 노즐 번호
	 * @return
	 * 		ODT 번호 (없을시 Null)
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
	 * KIXXHUB 장비의 단말기 시리얼 번호 가져오기
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
	 * 셀프 ODT 환경 정보 P1
	 * 
	 * @param storeCode		: 매장 코드
	 * @param odt_no 		: null 일 경우 모든 ODT 에 대한 정보, 특정 값인 경우 그 값에 해당하는 정보만 채움.
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
				odtIdVector.add(nozzleDataList[i].getNozzle_no()) ;	// Self ODT 번호
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
	 * 셀프 ODT P2 환경 설정 정보 요청
	 * 
	 * @param storeCode : 매장 코드
	 * @param odt_no 	: null 일 경우 모든 ODT 이고, 특정 값인 경우 그 odt_no 에 대한 정보만 요청
	 * @return : P2 WorkingMessage 전송
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
		
		//세차권 바코드 출력 여부 조회 - PI2, CWI, 2016-03-10 GSC SELF에서 세차권 바코드 출력여부를 전달 받는다.
		// 0: 출력안함, 1: 주유바코드 , 2: 세차바코드
		String printBarCode = "0";
		
		try {
			// 바코드 사용여부 - 0: 미사용, 1: 사용
			String BarCode = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0440);
			
			if(BarCode != null && !"".equals(BarCode)) { 
				
				// 바코드를 사용 한다면
				if("1".equals(BarCode)) {
					
					// 바코드 종류 - 1:주유 바코드, 2:세차 바코드
					String BarCodeType = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0441);
				
					// 바코드 종류 체크
					if(BarCodeType != null && !"".equals(BarCodeType)) {
						
						// 2019.11.20 공통코드 "0441" 두자리일경우 앞 한자리는 버린다. - SoonKwan
						if(BarCodeType.length() == 2) {
							BarCodeType = BarCodeType.substring(1);
						}
						
						printBarCode = BarCodeType;
					}
					
				}
					
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error("[CarWash BarCode]세차권 바코드 출력 여부 조회시 에러 발생");
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		}
		
		if(printBarCode != null || !"".equals(printBarCode)) {
			p2WorkingMsg.setBarcodePrintYN(printBarCode);
		} 
		
		LogUtility.getPumpMLogger().info("[CarWash BarCode]세차권 바코드 출력 여부= "+p2WorkingMsg.getBarcodePrintYN());
		
		//다쓰노 셀프 영수증 출력시 머릿말 꼬릿말을 사용하기 위해서 다음 값을 셋팅
		PumpMessageFormat.setHeadPrint(nzInfoData.getTitle_txt_head()) ;
		PumpMessageFormat.setFootPrint1(nzInfoData.getTitle_txt_foot1());
		PumpMessageFormat.setFootPrint2(nzInfoData.getTitle_txt_foot2());
		return p2WorkingMsg ;
	}
	
	
	/**
	 * 일반 주유기의 P3 환경 설정 정보 요청
	 * 
	 * @param storeCode	: 매장 코드
	 * @param nozzle_no	: 노즐 번호 (null 인 경우 모든 일반 주유기)
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

			// 충전기/셀프ODT/셀프주유기 는 제외	
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
					// 영문 이름.
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
	 * 셀프 ODT P5 환경 설정 정보 요청
	 * [2008.12.14] 박동화 , 정우철
	 * 	셀프 주유기의 단가 변경시 초기화 일 경우에는 P5_OdtInfo 의 mode 를 0 으로 설정하고, 동작중일 경우에는 1 로 설정하도록 한다.
	 * 
	 * @param storeCode : 매장 코드
	 * @param odt_no : null 일 경우 모든 ODT 이고, 특정 번호인 경우 그 번호만.
	 * @return : P5 WorkingMessage
	 * @throws Exception
	 */
	public static P5_WorkingMessage getP5WorkingMessage(String storeCode, String odt_no) throws Exception {
		P5_WorkingMessage p5WorkingMsg = new P5_WorkingMessage() ;
		T_NZ_INFOData nzInfoData = T_NZ_INFOHandler.getHandler().getT_NZ_INFODataByStoreCode(storeCode) ;
		String posID = T_KH_POS_INFOHandler.getHandler().getPOSID(storeCode) ;
		ArrayList<String> selfODTList = null ;
		
		// 초기화시 가득주유 여부를 판단한다. 
		// POS로부터 값을 받지 못했을 경우에는 DB에서 값을 가져온다. Default는 가득주유(1)이다  -  2010.02.13 upkoo
		String useFullPumping = null; 
		if( T_KH_CODEMASTERHandler.getHandler().codeMaster.get(IConstant.POSPORTOCOL_CODEMASTER_0335) != null ) {
			useFullPumping =  T_KH_CODEMASTERHandler.getHandler().codeMaster.get(IConstant.POSPORTOCOL_CODEMASTER_0335);
			LogUtility.getPumpMLogger().debug("[Pump M] 가득주유 여부를 POS에서 받았을 경우-> useFullPumping-> "+ useFullPumping) ;
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
			
			// 초기화시 가득주유여부를 설정한다 - 2010.02.13 upkoo
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
					// 셀프 ODT 는 영문 명으로 내려 보냄.
					p5NozInfo.setGoodsType(productData.getGoods_name_eng()) ;
					
					nozzleInfoVector.add(p5NozInfo) ;
				}
				odtInfo.setNozzleInfo(nozzleInfoVector) ;
				odtInfo.setNozzleCount(new Integer(nozzleInfoVector.size()).toString()) ;
				
				// PI2, 2016-03-18, CWI - 신규 ODT의 승취승의 결재 방식 및 BL 체크등의 옵션을 Properties 에서 받아 P5전문에 셋팅 한다.
				// fullpumpingOption = 가득 및 일반주유 1.승승취, 2.승취승
				String fullpumpingOption = PropertyManager.getSingleton().getProperty(PropertyManager.SELFODT_FULLPUMPING_OPTION, PropertyManager.SELFODT_FULLPUMPING_OPTION_DEFAULT);
				odtInfo.setReApprovalOption(fullpumpingOption);
				LogUtility.getPumpMLogger().debug("[Pump M] 가득주유 옵션 [fullpumpingOption] ->" +  fullpumpingOption) ;
				
				// blcheckOption = 0.BL사용안함-가득주유만 해당, 1.BL사용함-가득주유만 해당
				String blcheckOption = PropertyManager.getSingleton().getProperty(PropertyManager.SELFODT_USEBL_OPTION, PropertyManager.SELFODT_USEBL_OPTION_DEFAULT);
				odtInfo.setUseBL(blcheckOption);
				LogUtility.getPumpMLogger().debug("[Pump M] BL Check Option [blcheckOption] ->" +  blcheckOption) ;
				if(blcheckOption == null){
					LogUtility.getPumpMLogger().debug("[Pump M] kixxhub.properties 파일에 selfodt.useBL.option 을 설정 설정해주세요.");
					blcheckOption = "0" ;
				}
				
				odtInfoVector.add(odtInfo) ;
			}
		}
		
		p5WorkingMsg.setOdtInfo(odtInfoVector) ;
		return p5WorkingMsg ;
	}
	
	/**
	 * [2010.02.13] 최순구
	 * 	셀프 주유기의 가득주유인 경우  P5_OdtInfo 의 mode 를 2 로 설정하고, useFullPumping  1 로 설정하도록 한다.
	 * 
	 * @param storeCode		: 매장 코드
	 * @param odt_no				: ODT 번호
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
		
		LogUtility.getPumpMLogger().debug("[Pump M] 가득주유 여부 [useFullPumping] ->" +  useFullPumping) ;
		
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
					// 셀프 ODT 는 영문 명으로 내려 보냄.
					p5NozInfo.setGoodsType(productData.getGoods_name_eng()) ;
					
					nozzleInfoVector.add(p5NozInfo) ;
				}
				odtInfo.setNozzleInfo(nozzleInfoVector) ;
				odtInfo.setNozzleCount(new Integer(nozzleInfoVector.size()).toString()) ;
				
				// PI2, 2016-03-18, CWI - 신규 ODT의 승취승의 결재 방식 및 BL 체크등의 옵션을 Properties 에서 받아 P5전문에 셋팅 한다.
				// fullpumpingOption = 가득 및 일반주유 1.승승취, 2.승취승
				String fullpumpingOption = PropertyManager.getSingleton().getProperty(PropertyManager.SELFODT_FULLPUMPING_OPTION, PropertyManager.SELFODT_FULLPUMPING_OPTION_DEFAULT);
				odtInfo.setReApprovalOption(fullpumpingOption);
				LogUtility.getPumpMLogger().debug("[Pump M] 가득주유 옵션 [fullpumpingOption] ->" +  fullpumpingOption) ;

				// blcheckOption = 0.BL사용안함-가득주유만 해당, 1.BL사용함-가득주유만 해당
				String blcheckOption = PropertyManager.getSingleton().getProperty(PropertyManager.SELFODT_USEBL_OPTION, PropertyManager.SELFODT_USEBL_OPTION_DEFAULT);
				odtInfo.setUseBL(blcheckOption);
				if(blcheckOption == null){
					LogUtility.getPumpMLogger().debug("[Pump M] kixxhub.properties 파일에 selfodt.useBL.option 을 설정 설정해주세요.");
					blcheckOption = "0" ;
				}
				
				odtInfoVector.add(odtInfo) ;
			}
		}
		
		p5WorkingMsg.setOdtInfo(odtInfoVector) ;
		return p5WorkingMsg;
	}

	/**
	 * [2008.12.14] 박동화 , 정우철
	 * 	셀프 주유기의 단가 변경시 초기화 일 경우에는 P5_OdtInfo 의 mode 를 0 으로 설정하고, 동작중일 경우에는 1 로 설정하도록 한다.
	 * 
	 * @param storeCode		: 매장 코드
	 * @param odt_no				: ODT 번호
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
					// 셀프 ODT 는 영문 명으로 내려 보냄.
					p5NozInfo.setGoodsType(productData.getGoods_name_eng()) ;
					
					nozzleInfoVector.add(p5NozInfo) ;
				}
				odtInfo.setNozzleInfo(nozzleInfoVector) ;
				odtInfo.setNozzleCount(new Integer(nozzleInfoVector.size()).toString()) ;
				
				// PI2, 2016-03-18, CWI - 신규 ODT의 승취승의 결재 방식 및 BL 체크등의 옵션을 Properties 에서 받아 P5전문에 셋팅 한다.
				// fullpumpingOption = 가득 및 일반주유 1.승승취, 2.승취승
				String fullpumpingOption = PropertyManager.getSingleton().getProperty(PropertyManager.SELFODT_FULLPUMPING_OPTION, PropertyManager.SELFODT_FULLPUMPING_OPTION_DEFAULT);
				odtInfo.setReApprovalOption(fullpumpingOption);
				LogUtility.getPumpMLogger().debug("[Pump M] 가득주유 옵션 [fullpumpingOption] ->" +  fullpumpingOption) ;
				
				// blcheckOption = 0.BL사용안함-가득주유만 해당, 1.BL사용함-가득주유만 해당
				String blcheckOption = PropertyManager.getSingleton().getProperty(PropertyManager.SELFODT_USEBL_OPTION, PropertyManager.SELFODT_USEBL_OPTION_DEFAULT);
				odtInfo.setUseBL(blcheckOption);
				LogUtility.getPumpMLogger().debug("[Pump M] BL Check Option [blcheckOption] ->" +  blcheckOption) ;
				if(blcheckOption == null){
					LogUtility.getPumpMLogger().debug("[Pump M] kixxhub.properties 파일에 selfodt.useBL.option 을 설정 설정해주세요.");
					blcheckOption = "0" ;
				}
				
				odtInfoVector.add(odtInfo) ;
			}
		}
		
		p5WorkingMsg.setOdtInfo(odtInfoVector) ;
		return p5WorkingMsg ;
	}	
	
	
	/**
	 * 주유기 초기화를 위한 P6 전문을 구성한다.
	 * 
	 * @param storeCode		: 매장 코드
	 * @return
	 * @throws Exception
	 */
	public static P6_WorkingMessage getP6WorkingMessage(String storeCode) throws Exception {
		P6_WorkingMessage p6WorkingMsg = new P6_WorkingMessage() ;
		String sysTime = GlobalUtility.getDateYYYYMMDDHHMMSS().substring(2);//12자라 
		String workDate = T_KH_STOREHandler.getHandler().getWorkingDate(storeCode) ;
		
		p6WorkingMsg.setSystemTime(sysTime) ;
		p6WorkingMsg.setWDate(workDate) ;
		
		return p6WorkingMsg ;
	}	
	
	
	/**
	 * POS 로 부터 받은 노즐별 Time Parameter 마스터 정보를 주유기 Adapter 으로 전송한다.
	 * 
	 * @param store_code
	 * @param nozzle_no		: null 인 경우 모든 노즐에 대해서
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
	 * 고객 카드 수행 응답 전문을 생성한다. 이는 POS 가 동작하지 않을 경우, 고객 카드 수행 요청에 대한 응답을 자체적으로 생성한다.
	 * 
	 * @param nozzleNo		: 노즐 번호
	 * @param goods_code	: 상품코드
	 * @param bonGoodsCode	: 보너스상품 코드
	 * @param oil_ind		: 유류/유외 구분
	 * @param base_price	: 상품 단가
	 * @param liter			: 주유량
	 * @param price			: 주유금액
	 * @param transactionID	: KH 처리 번호
	 * @param pro_ind_overlimit	: 외상결제타입
	 * @param isDiscount		: 할인 여부
	 * @param discountBasePrice	: 할인된 단가
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

		LogUtility.getPumpMLogger().debug("[Pump M] DY 전문을 구성합니다.") ;
		
		POS_DY_Item dyPumpM_Item = null ;
		SqlSession session = null;
		
		String goodsCode = goods_code ;					// 상품코드(18b)
		String unitPrice_before_discount = base_price ;	// 할인전단가(11b)
		String oilAmount = liter ;						// 수량(8b)
		String unitPrice_after_discount = null ;		// 할인후단가(10b)
		String tax_ind = ICode.TAXFREE_CD_01 ;			// 과면세구분(2b)
		String price_before_tax = null ;				// 공급가액(10b)
		String taxPrice = null ;						// 세금(10b)
		String oilPrice_before_discount = price ;		// 할인전금액(10b)
		String oilPrice_after_discount = null ;			// 할인후금액(10b)
		String khTransactionID = transactionID ;		// 전표번호(14b)
		String rentlimit_proc_ind_overlimit = pro_ind_overlimit ;	// 외상결제타입(2b)
		String unitDiscount_ind = null ;				// 할인여부(1b)
		String keepissue_no = null ;					// 보관증 번호(10b)
		String issue_type = null ;						// 보관증 발행 유형 (2b)
		
		double tempPrice = 0 ;
		
		try {
			session = SqlSessionFactoryManager.openSqlSession();
			double literInt = Double.parseDouble(liter) ; ;		
			double discountPriceInt = 0 ;

			/**
			 * 2016. 4. 14. 오후 15:48:31, PI2, WooChul Jung.
			 * 	PL 조건이 없는 경우 할인 단가가 안 올 수 있음. 이로 인해서 결제 금액이 0 으로 설정되는 경우가 있는데, 이를 막음.
			 */
			if (GlobalUtility.isNullOrEmptyString(discountBasePrice) || "0".equals(discountBasePrice)) {
				LogUtility.getPumpMLogger().info("[Pump M] 할인 단가가 0 이기 때문에, 할인 여부는 false 로 하고, 할인 단가를 점두가와 동일하게 구성합니다.");
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
				LogUtility.getPumpMLogger().debug("[Pump M] 할인전 단가와 할인후 단가가 동일하기 때문에, 금액은 주유금액으로 합니다.") ;
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

		LogUtility.getPumpMLogger().debug("[Pump M] 구성된 DY 전문은 다음과 같습니다.") ;
		if (dyPumpM_Item != null) 
			LogUtility.getPumpMLogger().info(dyPumpM_Item.toString());
		return dyPumpM_Item ;
	}
	
	/**
	 * DY 전문을 통해서 DY Array 를 만든다. 이는 한도 고객으로 인해서 한건의 주유정보를 여러 건으로 나눌 필요가 있기 때문이다.
	 * 
	 * @param dyPumpM_Item	: DY 전문
	 * @param limit			: 한도량
	 * @param limit_remains	: 한도 사용량
	 * @return
	 */
	private static POS_DY_Item[] getPOSPumpM_DY_ItemArrayInLimitLiter(POS_DY_Item dyPumpM_Item,
			String limit,
			double limit_remains) {
		
		LogUtility.getPumpMLogger().debug("[Pump M] 정량 한도 고객  으로 인해 Item Info 정보를 재구성합니다.") ;
		LogUtility.getPumpMLogger().info(dyPumpM_Item.toString());
		LogUtility.getPumpMLogger().debug("[Pump M] limit=" + limit + "#limit_remains=" + limit_remains) ;
		
		POS_DY_Item[] dyPumpMItemArray = null ;
		
		if (limit_remains <= 0 ) {
			resetBeforeBasePrice(dyPumpM_Item , dyPumpM_Item.getRentlimit_proc_ind_overlimit()) ;
			dyPumpMItemArray = new POS_DY_Item[1] ;
			dyPumpMItemArray[0] = dyPumpM_Item.toClone() ;
			dyPumpMItemArray[0].setRentlimit_proc_ind_overlimit(ICode.PROC_IND_OVERLIMIT_99) ;	// 현금
			//2016.03.11 WooChul Jung. If limit_proc_ind_overlimit == 01, then allow.
		} else if ("01".equals(dyPumpM_Item.getRentlimit_proc_ind_overlimit()) || (limit_remains >= Double.parseDouble(dyPumpM_Item.getOilAmount()))) {
			dyPumpMItemArray = new POS_DY_Item[1] ;
			dyPumpMItemArray[0] = dyPumpM_Item.toClone() ;
			dyPumpMItemArray[0].setRentlimit_proc_ind_overlimit(ICode.PROC_IND_OVERLIMIT_00) ;	// 정상
		} else {
			String exceedLiter = Double.toString( 
					GlobalUtility.substract(Double.parseDouble(dyPumpM_Item.getOilAmount()), limit_remains)) ;
			POS_DY_Item dyPumpM_Item0 = 
				getPOSPumpM_DY_ItemArrayInPayedLiter(dyPumpM_Item, Double.toString((limit_remains))) ; // 한도 결제 여부 = "1"
			dyPumpM_Item0.setRentlimit_proc_ind_overlimit(ICode.PROC_IND_OVERLIMIT_00) ;	// 정상
			
			POS_DY_Item dyPumpM_Item1 = 
				getPOSPumpM_DY_ItemArrayInPayedLiter(dyPumpM_Item, exceedLiter) ; 	// 한도 결제 여부 = "0"
			dyPumpM_Item1.setRentlimit_proc_ind_overlimit(dyPumpM_Item.getRentlimit_proc_ind_overlimit()) ;
			resetBeforeBasePrice(dyPumpM_Item1, dyPumpM_Item1.getRentlimit_proc_ind_overlimit()) ;
			
			dyPumpMItemArray = new POS_DY_Item[2] ;
			dyPumpMItemArray[0] = dyPumpM_Item0 ;
			dyPumpMItemArray[1] = dyPumpM_Item1 ;
		}		
		return dyPumpMItemArray ;
	}
	
	/**
	 * 한도 금액으로 인해 주유정보를 재계산한다.
	 * 
	 * @param dyPumpM_Item	: DY 전문
	 * @param limit			: 한도 금액
	 * @param limit_remains	: 남은 금액
	 * @return
	 */
	private static POS_DY_Item[] getPOSPumpM_DY_ItemArrayInLimitPrice(POS_DY_Item dyPumpM_Item,
			String limit,
			double limit_remains) {

		LogUtility.getPumpMLogger().debug("[Pump M] 정액 한도 고객  으로 인해 Item Info 정보를 재구성합니다#limit=" + limit) ;
		LogUtility.getPumpMLogger().info(dyPumpM_Item.toString());
		
		POS_DY_Item[] dyPumpMItemArray = null ;
		
		if (limit_remains <= 0) {
			resetBeforeBasePrice(dyPumpM_Item, dyPumpM_Item.getRentlimit_proc_ind_overlimit()) ;
			dyPumpMItemArray = new POS_DY_Item[1] ;
			dyPumpMItemArray[0] = dyPumpM_Item.toClone() ;
			dyPumpM_Item.setRentlimit_proc_ind_overlimit(ICode.PROC_IND_OVERLIMIT_99) ;	// 현금
			dyPumpMItemArray[0].setRentlimit_proc_ind_overlimit(dyPumpM_Item.getRentlimit_proc_ind_overlimit()) ;
			// 2016.03.11 WooChul Jung. If limit_proc_ind_overlimit == 01, then allow.
		} else if ("01".equals(dyPumpM_Item.getRentlimit_proc_ind_overlimit()) || limit_remains >= Double.parseDouble(dyPumpM_Item.getOilPrice_after_discount())) {
			dyPumpM_Item.setRentlimit_proc_ind_overlimit(ICode.PROC_IND_OVERLIMIT_00) ;	// 정상
			dyPumpMItemArray = new POS_DY_Item[1] ;
			dyPumpMItemArray[0] = dyPumpM_Item.toClone() ;
		} else {
			POS_DY_Item dyPumpM_Item0 = 
				getPOSPumpM_DY_ItemArrayInPayedPrice(dyPumpM_Item, Double.toString(limit_remains)) ; 	// 한도 결제 여부 = "1"
			
			String usedOilByLimit_remains = GlobalUtility.substract(dyPumpM_Item.getOilAmount(), dyPumpM_Item0.getOilAmount()) ;
			dyPumpM_Item0.setRentlimit_proc_ind_overlimit(ICode.PROC_IND_OVERLIMIT_00) ;	// 정상
			
			POS_DY_Item dyPumpM_Item1 = 
				getPOSPumpM_DY_ItemArrayInPayedLiter(dyPumpM_Item, usedOilByLimit_remains) ; 	// 한도 결제 여부 = "0"
			
			dyPumpM_Item1.setRentlimit_proc_ind_overlimit(dyPumpM_Item.getRentlimit_proc_ind_overlimit()) ;
			resetBeforeBasePrice(dyPumpM_Item1, dyPumpM_Item1.getRentlimit_proc_ind_overlimit()) ;
			
			dyPumpMItemArray = new POS_DY_Item[2] ;
			dyPumpMItemArray[0] = dyPumpM_Item0 ;
			dyPumpMItemArray[1] = dyPumpM_Item1 ;
		}		
		return dyPumpMItemArray ;
	}
	
	
	/**
	 * 사용량으로 주유정보를 재계산한다.
	 * 
	 * @param src_dyPumpM_Item	: dy 전문
	 * @param payedLiter		: 사용량
	 * @return
	 */
	private static POS_DY_Item getPOSPumpM_DY_ItemArrayInPayedLiter(POS_DY_Item src_dyPumpM_Item,
			String payedLiter) {		

		LogUtility.getPumpMLogger().debug("[Pump M] 결제된 Liter 를 이용하여 DY 전문을 재구성합니다.") ;
		LogUtility.getPumpMLogger().info(src_dyPumpM_Item.toString());
		LogUtility.getPumpMLogger().debug("[Pump M] payedLiter=" + payedLiter);
		
		POS_DY_Item dyPumpM_Item = src_dyPumpM_Item.toClone() ;
		
		String unitPrice_before_discount = dyPumpM_Item.getUnitPrice_before_discount() ;	// 할인전단가(11b)
		String oilAmount = payedLiter ;					// 수량(8b)
		String unitPrice_after_discount = dyPumpM_Item.getUnitPrice_after_discount() ;	// 할인후단가(10b)
		String price_before_tax = dyPumpM_Item.getPrice_before_tax() ;			// 공급가액(10b)
		String taxPrice = dyPumpM_Item.getTaxPrice() ;					// 세금(10b)
		String oilPrice_before_discount = dyPumpM_Item.getOilPrice_before_discount() ;	// 할인전금액(10b)
		String oilPrice_after_discount = dyPumpM_Item.getOilPrice_after_discount() ;	// 할인후금액(10b)

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

		LogUtility.getPumpMLogger().debug("[Pump M] 결제된 금액 를 이용하여 DY 전문을 재구성합니다.") ;
		LogUtility.getPumpMLogger().info(src_dyPumpM_Item.toString());
		
		POS_DY_Item dyPumpM_Item = src_dyPumpM_Item.toClone() ;
		
		String unitPrice_before_discount = dyPumpM_Item.getUnitPrice_before_discount() ;	// 할인전단가(11b)
		String oilAmount = "0" ;					// 수량(8b)
		String unitPrice_after_discount = dyPumpM_Item.getUnitPrice_after_discount() ;	// 할인후단가(10b)
		String price_before_tax = "0" ;			// 공급가액(10b)
		String taxPrice = "0" ;					// 세금(10b)
		String oilPrice_before_discount = "0" ;	// 할인전금액(10b)
		String oilPrice_after_discount = payedPrice ;	// 할인후금액(10b)

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
	 * 1회 정량 고객이 경우 DY 전문의 Item Info 를 재구성한다.
	 * 
	 * @param dyPumpM_Item	: DY 전문내 ItemInfo
	 * @param oneAmount		: 1회 정량
	 * @param keepissue_ind	: 보관증 발행 유무
	 * @return
	 */
	private static POS_DY_Item[] getPOSPumpM_DY_ItemArrayWhenOneAmountCustomer(POS_DY_Item dyPumpM_Item, 
			double oneAmount, String keepissue_ind) {

		LogUtility.getPumpMLogger().debug("[Pump M] 1회정량(정량입력) 으로 인해 Item Info 정보를 재구성합니다#oneAmount=" + oneAmount + "keepissue_ind = " + keepissue_ind) ;
		LogUtility.getPumpMLogger().info(dyPumpM_Item.toString());
		POS_DY_Item[] dyPumpMItemArray = null ;
		
		if (oneAmount <= 0 ) {
			resetBeforeBasePrice(dyPumpM_Item , dyPumpM_Item.getRentlimit_proc_ind_overlimit()) ;
			dyPumpMItemArray = new POS_DY_Item[1] ;
			dyPumpMItemArray[0] = dyPumpM_Item.toClone() ;
			dyPumpMItemArray[0].setRentlimit_proc_ind_overlimit(ICode.PROC_IND_OVERLIMIT_99) ;	// 현금
		} else if (oneAmount == Double.parseDouble(dyPumpM_Item.getOilAmount())) {
			dyPumpMItemArray = new POS_DY_Item[1] ;
			dyPumpMItemArray[0] = dyPumpM_Item.toClone() ;
			dyPumpMItemArray[0].setRentlimit_proc_ind_overlimit(ICode.PROC_IND_OVERLIMIT_00) ;	// 정상
		} else if (oneAmount > Double.parseDouble(dyPumpM_Item.getOilAmount())) {	
			
			// 보관증 유무가 값이 없거나 1 (보관증 발행 유) 인 경우는 보관증 발행으로 처리 한다.
			if ((keepissue_ind == null) || (keepissue_ind.equals(ICode.KEEPISSUE_IND_1))) {
				String remainsLiter = Double.toString( 
						GlobalUtility.substract(oneAmount, Double.parseDouble(dyPumpM_Item.getOilAmount()))) ;
				POS_DY_Item dyPumpM_Item0 = dyPumpM_Item.toClone() ;
				dyPumpM_Item0.setRentlimit_proc_ind_overlimit(ICode.PROC_IND_OVERLIMIT_00) ;	// 정상
				
				String issue_type = "01" ; // 수량 default
				double se_publish_base_won = 5000 ;
				try {
					T_NZ_INFOData infoData = T_NZ_INFOHandler.getHandler().getT_NZ_INFODataByStoreCode(
						T_KH_STOREHandler.getHandler().getStoreCode()) ;
					issue_type = infoData.getSe_publish_base() ;
					se_publish_base_won = Double.parseDouble(infoData.getSe_publish_base_won()) ;
					
				} catch (Exception e) {
					LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
					issue_type = "01" ; // 수량 default
					se_publish_base_won = 5000 ;
				}					
				
				POS_DY_Item dyPumpM_Item1 = 
					getPOSPumpM_DY_ItemArrayInPayedLiter(dyPumpM_Item, remainsLiter) ;
				resetBeforeBasePrice(dyPumpM_Item1) ;
				dyPumpM_Item1.setRentlimit_proc_ind_overlimit(dyPumpM_Item.getRentlimit_proc_ind_overlimit()) ;
				dyPumpM_Item1.setRentlimit_proc_ind_overlimit(ICode.PROC_IND_OVERLIMIT_00) ;	// 정상
				
				// 보관증 발행 금액 보다 작았을 경우는 보관증을 발행하지 않는다.
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
					dyPumpMItemArray[0].setRentlimit_proc_ind_overlimit(ICode.PROC_IND_OVERLIMIT_00) ;	// 정상
					dyPumpMItemArray[1] = dyPumpM_Item1 ;						
				}
			} else {
				dyPumpMItemArray = new POS_DY_Item[1] ;
				dyPumpMItemArray[0] = dyPumpM_Item.toClone() ;
				dyPumpMItemArray[0].setRentlimit_proc_ind_overlimit(ICode.PROC_IND_OVERLIMIT_00) ;	// 정상
			}
		} else {
			String exceedLiter = Double.toString( 
					GlobalUtility.substract(Double.parseDouble(dyPumpM_Item.getOilAmount()), oneAmount)) ;
			POS_DY_Item dyPumpM_Item0 = 
				getPOSPumpM_DY_ItemArrayInPayedLiter(dyPumpM_Item, Double.toString((oneAmount))) ; 
			dyPumpM_Item0.setRentlimit_proc_ind_overlimit(ICode.PROC_IND_OVERLIMIT_00) ;	// 정상
			
			POS_DY_Item dyPumpM_Item1 = 
				getPOSPumpM_DY_ItemArrayInPayedLiter(dyPumpM_Item, exceedLiter) ;
			dyPumpM_Item1.setRentlimit_proc_ind_overlimit(dyPumpM_Item.getRentlimit_proc_ind_overlimit()) ;
			resetBeforeBasePrice(dyPumpM_Item1, dyPumpM_Item1.getRentlimit_proc_ind_overlimit()) ;
			dyPumpM_Item1.setRentlimit_proc_ind_overlimit(ICode.PROC_IND_OVERLIMIT_99) ;	// 결재 요청
			
			dyPumpMItemArray = new POS_DY_Item[2] ;
			dyPumpMItemArray[0] = dyPumpM_Item0 ;
			dyPumpMItemArray[1] = dyPumpM_Item1 ;
		}		

		LogUtility.getPumpMLogger().debug("[Pump M] 1회정량(정량입력) 으로 인해 재구성된 전문은 다음과 같습니다.") ;
		if (dyPumpMItemArray != null) {
			for (int i = 0 ; i < dyPumpMItemArray.length; i++) {
				POS_DY_Item dyItem = dyPumpMItemArray[i] ;
				LogUtility.getPumpMLogger().info(dyItem.toString());
			}
		}
		
		return dyPumpMItemArray ;
	}
	
	/**
	 * 주유금액과 단가를 이용하여서 주유량을 계산한다.
	 * 
	 * @param priceDou		: 주유 금액
	 * @param basePriceDou	: 단가
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
	 * 공급가액을 계산한다.
	 * 	공급가액 = 금액 - 세금
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
	 * 보너스 카드 번호 구하기
	 * 
	 * @param bonusCardNumber	: ODT 로 부터 읽은 보너스 카드 번호
	 * @return
	 */
	public static String getRealBonusCardNumber(String bonusCardNumber) {
		if ((bonusCardNumber == null) || (bonusCardNumber.equals(""))) return "" ; 
		else {
			int position = bonusCardNumber.indexOf("=") ;
			if (position > 0) {
				return bonusCardNumber.substring(0,position) ;
			} else {
				//보너스 카드 번호 부에 Null 이 들어있을 경우 제거
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
	 * 셀프 주유기 재승인 인정 금액을 리턴한다.
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
	 * CAT 단말기로 부터 주유중 정보 요청시 금액까지 입력 한 경우는 현 주유건이 주유중인 경우에만 정보를 전송한다.
	 * 
	 * @param nozzle_no		: 노즐 번호
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
	 * 거래처에 등록된 매출금액 처리 구분과 할인단가, 수량을 통해서 할인된 금액을 재계산한다.
	 * @param basePrcie						: 판매 단가 
	 * @param liter							: 주유량
	 * @param rcptsheetissue_code_amtsale	: 금액 판매 처리 구분
	 * @return
	 */
	public static String handleRcptsheetissue_code_amtsale(double basePrcie, double liter, String rcptsheetissue_code_amtsale) {
		String payPrice = "";
		
		payPrice = Double.toString(handleRcptsheetissue_code_amtsale((basePrcie*liter), rcptsheetissue_code_amtsale));
		
		return payPrice;
	}
	
	/**
	 * 거래처에 등록된 매출금액 처리 구분을 통해서 주유 금액을 재계산한다.
	 * 
	 * @param rcptsheetissue_code_amtsale
	 * @return
	 */
	public static int handleRcptsheetissue_code_amtsale(double price, String rcptsheetissue_code_amtsale) {
		LogUtility.getPumpMLogger().debug("[Pump M] 매출금액 처리 구분을 통해서 주유 금액을 재계산합니다.price="+price) ;
		
		
//		20160516 PI2 twlee 기존 rcptsheetissue_code_amtsale: 04(실거래)에 대한 처리가 없어 withPos와 withOutPos의 결제금액에 차이가 생겼음.
//							POS에서 04(실거래)일 경우 반올림 처리하도록 구현되어있어 kixxhub에서도 반올림으로 처리. 
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
		LogUtility.getPumpMLogger().debug("[Pump M] 계산된 주유금액은 다음과 같습니다.retPrice="+retPrice) ;
		return retPrice ;
	}
	
	/**
	 * 거래처에 등록된 매출금액 처리 구분과 할인단가, 수량을 통해서 할인된 금액을 재계산한다.
	 * @param basePrcie						: 판매 단가 
	 * @param liter							: 주유량
	 * @param rcptsheetissue_code_amtsale	: 금액 판매 처리 구분
	 * @return
	 */
	public static String handleRcptsheetissue_code_amtsale(String basePrcie, String liter, String rcptsheetissue_code_amtsale) {
		String payPrice = "";
		
		payPrice = Double.toString(handleRcptsheetissue_code_amtsale((
				Double.parseDouble(basePrcie) * Double.parseDouble(liter)), rcptsheetissue_code_amtsale));
		
		return payPrice;
	}

	/**
	 * DY 전문내 ItemInfo 내에 보관증이 있는지를 조사한다.
	 * 
	 * @param dyItemArray	: DY 전문내 ItemInfo
	 * @return
	 */
	private static boolean hasKeepNumber(POS_DY_Item[] dyItemArray) {
		LogUtility.getPumpMLogger().debug("[Pump M] DY Item Info 에 보관증이 있는지 조사합니다.") ;
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
		
		LogUtility.getPumpMLogger().debug("[Pump M] DY Item Info 에 보관증이 조사 결과 다음과 같습니다. hasKeepNumber="+hasKeepNumber) ;
		return hasKeepNumber ;
	}

	/**
	 * ODT 와 연결된 노즐인지 여부 체크한다.
	 * 
	 * @param nozzleNo	: 노즐 번호
	 * @return
	 */
	public static boolean isNozzleConnectedToSelfODT(String nozzleNo) {
		int protocolTypeInt = getConnectedRepODTProtocolFromNozzleNo(nozzleNo) ;
		switch (protocolTypeInt) {
			case IPumpConstant.PUMP_PROTOCOL_DaSNo :
			case IPumpConstant.PUMP_PROTOCOL_NewDaSNo : //2012.09.21 ksm 신형다쓰노 추가.
			case IPumpConstant.PUMP_PROTOCOL_GSC_SELF : //2016.03.18 CWI gsc 추가.
			case IPumpConstant.PUMP_PROTOCOL_SOMO : {
				return true ;
			}
		}
		return false ;
	}

	/**
	 * 차량 단축번호를 통해서 거래처 정보를 생성하여 전송한다.
	 * 
	 * @param carno_nbr_short	: 차량 단축 번호
	 * @return
	 */
	public static POS_DU_Car[] processPOSPumpM_DU_Car(String carno_nbr_short) {
		ArrayList<POS_DU_Car> duPumpMCarList = new  ArrayList<POS_DU_Car>() ;
		SqlSession session = null;
		
		try {
			session = SqlSessionFactoryManager.openSqlSession();

			T_KH_CUST_CAR_INFOData[] carInfoDataArray = 
				T_KH_CUST_CAR_INFOHandler.getHandler().getT_KH_CUST_CAR_INFODataByCarno_nbr_short(session, carno_nbr_short) ;

			LogUtility.getPumpMLogger().debug("[Pump M] T_KH_CUST_CAR_INFO 테이블에 차량단축 번호("+ carno_nbr_short+ ")로 조회를 합니다.") ;

			if ((carInfoDataArray == null) || (carInfoDataArray.length == 0)) {
				LogUtility.getPumpMLogger().debug("[Pump M] T_KH_CUST_CAR_INFO 테이블에 차량단축 번호로 조회를 하였지만 없습니다.") ;
				return null ;
			} else {
				LogUtility.getPumpMLogger().debug("[Pump M] T_KH_CUST_CAR_INFO 테이블에 차량단축 번호로 조회 이후 결과는 다음과 같습니다.") ;
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
			
				LogUtility.getPumpMLogger().debug("[Pump M] T_KH_CUST_CARD_INFO 테이블에 다음과 같이 조회를 합니다. cust_code=" + 
						cust_code + "#store_code=" + store_code + "#carno_nbr=" + carno_nbr);

				T_KH_CUST_INFOData custInfoData =
					T_KH_CUST_INFOHandler.getHandler().getT_KH_CUST_INFOData(session, cust_code, store_code) ;
				
				
				/** [2008.10.30]
				 * 카드 정보가 있을 경우에는 당연히 거래처 정보가 있어야 한다. 하지만 없을 경우도 있기 때문에 이에 대한 처리를 아래와 같이 하도록 한다.
				 * 	거래처 정보 유무를 먼저 조사하여 없을 경우 다음 차량을 조사하도록 한다.
				 */
				if (custInfoData == null) {
					LogUtility.getPumpMLogger().debug("[Pump M] T_KH_CUST_INFO 테이블에 조회를 하였지만 결과는 없습니다. cust_code=" + cust_code) ;
				} else if ((cardInfoDataArray == null) || (cardInfoDataArray.length == 0)) {
					LogUtility.getPumpMLogger().debug("[Pump M] T_KH_CUST_CARD_INFO 테이블에 조회를 하였지만 결과는 없습니다. " +
							"따라서 차량 번호만 넣습니다.") ;
					
					POS_DU_Car duPumpMCar = new POS_DU_Car(carno_nbr , "", custInfoData.getCust_name() + "^" + custInfoData.getCust_code(), custInfoData.getCust_code()) ;					
					duPumpMCarList.add(duPumpMCar) ;
				} else {
					LogUtility.getPumpMLogger().debug("[Pump M] T_KH_CUST_INFO 테이블에 다음과 같이 조회를 합니다.cust_code=" + cust_code + "#store_code=" + store_code) ;

					for (int j = 0 ; j < cardInfoDataArray.length ; j++) {
						if ((cardInfoDataArray != null) && (cardInfoDataArray.length !=0)) {
							if (custInfoData != null) {
								String car_no = carno_nbr ;		// 차량번호
								String cust_card_no = cardInfoDataArray[j].getCardno_nbr_cust() ;	// 거래처 카드 번호
								String cust_name = custInfoData.getCust_name() ;		// 거래처 명

								POS_DU_Car duPumpMCar = new POS_DU_Car(car_no , cust_card_no, cust_name + "^" + cust_code, cust_code) ;
								LogUtility.getPumpMLogger().info(duPumpMCar.toString());
								
								duPumpMCarList.add(duPumpMCar) ;
							} else {
								LogUtility.getPumpMLogger().debug("[Pump M] T_KH_CUST_INFO 테이블에 조회를 하였지만 결과는 없습니다.") ;
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
	 * 차량 조회 응답시 차량조회가 한건도 안되었을 경우 PG 전문을 Pump A 로 전송한다.
	 * 이 경우 PG 전문은 차량 조회가 없다는 정보를 담게 된다.
	 *  
	 * @param duMsg		: POS Protocol 의 차량조회 응답 전문 (DU)
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
	 * POS 로 전송하기 위해서 DY 전문을 구성한다.
	 * 
	 * 카드 기준		01:현금카드
	 * 				02:외상카드
	 * 				03:용역보관 (POS에 한도체크 요청,POS 가 동작안할 경우 무조건 허용. 이후 TR 을 POS 로 전송)
	 * 				04:매출보관 (POS에 한도체크 요청,POS 가 동작안할 경우 무조건 허용. 이후 TR 을 POS 로 전송)
	 * 				05:VIP
	 * 				(01,05 는 현금 카드로직 , 02~04 는 외상카드 로직)
	 * 				09:존재하지 않음
	 * 
	 * 카드 적용 구분	01 : 사용 안함
	 * 				02 : 차량별
	 * 				03 : 거래처 별
	 * 				04 : 1회 정량
	 * 				05 : 무제한
	 * 
	 * 거래 상태		01 : 정상
	 * 				02 : 정지
	 * 				03 : 말소
	 * 
	 * 한도종류		카드기준=외상카드,카드적용구분=[차량별|거래처별]일경우 아래 01 혹은 02. 그외는 00
	 * 				01=차량별한도
	 * 				02=거래처외상한도
	 * 				00=의미 없음
	 * 
	 * 한도적용기준	한도종류가 [01|02] 일경우 아래 [01|02] 사용. 그외는 00
	 * 				01=수량
	 * 				02=금액
	 * 				00=의미 없음
	 * 
	 * 한도 수량		한도 적용 기준이 [01|02] 일경우 사용.
	 * 				수량일 경우 소수점 3자리
	 * 				금액일 경우는 정수
	 * 
	 * 잔량			한도 적용 기준이 [01|02] 일경우 사용.
	 * 				수량일 경우 소수점 3자리
	 * 				금액일 경우는 정수
	 * 
	 * @param messageID		: Message ID
	 * @param nozzle_no		: 노즐 번호
	 * @param cust_card_ind	: 차량번호/거래처 카드 번호 유무
	 * @param cust_card_no	: 거래처 카드 번호 or 차량번호
	 * @param taxFreeCust_type	: 면세 거래처 여부
	 * @param fixedQty_yn	: 정량입력 여부
	 * @param fixedQty		: 정량값
	 * @param goods_code	: 상품 코드
	 * @param basePrice		: 노즐 단가
	 * @param liter			: 주유량
	 * @param price			: 주유 금액
	 * @param khTransactionID	: KH 처리 번호
	 * @param taxfreecust_type	: 0 : 면세 아님, 1:등록 거래처 , 2: 일반
	 * @param cust_code	: 거래처 코드 (차량 번호가 기입되어 있을 경우 의미 있게 사용)
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
		String cust_mileage_ind = ICode.CUST_MILEAGE_IND_1 ;		// 외상 거래처 보너스 적립 유무
		String cust_no = "" ;										// 거래처 번호
		String cust_name = "" ;										// 거래처 명
		String car_no = "" ;										// 차량 번호
		String cust_cd_item = "" ;									// 거래처 유형
		String card_code_base = ICode.CARD_CODE_BASE_06 ;			// 카드 기준
		String rcptsheetissue_ind_prtcarno = ICode.RCPSHEETISSUE_IND_PRTCARNO_1 ;	// 영수증차량번호출력여부
		String rcptunitprc_ind_prt = ICode.RCPTUNITPRC_IND_PRT_1  ;					// 영수증단가표기여부
		String cardadj_ind = ICode.CARDADJ_IND_00 ;					// 카드 적용 구분
		String status_code_card = ICode.STATUS_CODE_CARD_01 ;		// 거래 상태
		String limit_type = ICode.DY_LIMIT_TYPE_00 ;				// 한도종류
		String adjbase_code_limit = ICode.ADJBASE_CODE_LIMIT_00 ; 	// 한도적용기준
		String limit = "0" ;										// 한도수량
		String save_expire_date	= "" ;								// 보관증 발행 유효 기간
		String save_head_title = "" ;								// 보관증 머리말	
		String save_foot_title1 = "" ;								// 보관증 꼬리말1	
		String save_foot_title2 = "" ;								// 보관증 꼬리말2	
		String led_code = IUPOSConstant.LEDCODE_1 ;					// LED Code
		String proc_ind_overlimit = ICode.PROC_IND_OVERLIMIT_00 ;	// 외상결제 타입	(00 - 05) 00=정상
		String oil_ind = ICode.OIL_IND_11 ; 						// 유류/유외 구분 (Default = 11)
		String unitprc_code_stdn = ICode.UNITPRC_CODE_STDN_01 ;		// 단가 기준 (03:면세단가)
		String rcptsheetissue_code_amtsale = "" ;					// 매출금액처리구분	
//		String taxfree_cd = ICode.TAXFREE_CD_01 ;					// 과세 면세 구분 (01 : 과세 , 02:면세)
										// 2016. 4. 14. 오후 15:48:31, PI2, WooChul Jung. 과면세 구분은 "단가 기준" 을 통해서 처리함.
		boolean isTaxFree = false ;									// 과면세 구분
		double limit_remainsDou = 0 ;
		POS_DY_Item[] dyInfoArray = null ;
		String keepissue_ind = ICode.KEEPISSUE_IND_1 ;
		int state = ICustConstant.STATE_0 ;
		String temp_cust_mileage_ind = ICode.CUST_MILEAGE_IND_1 ;
		
		// 자영인지 직영인지 구분한다. 12:자영 - upkoo
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
			// 차량번호로 주유 정보 요청시 
			custReturnValue = CustUtil.processCustWithCarNo(nozzle_no , cust_card_no, taxFreeCust_type, cust_code) ;
		} else {
			// 거래처 카드 번호로 주유정보 요청시
			// 현 날짜와 적용일자,종료일자를 항상 비교를 해서, 그 사이가 아니면, PL 이 등록되지 않은 고객으로 처리한다.
			// 즉 custReturnValue는 null이 된다.  - upkoo
			custReturnValue = CustUtil.processCustWithCustCardNo(nozzle_no , cust_card_no ,taxFreeCust_type) ;
		}
		
		// PreProcessing 을 진행한다.
		PumpMObjectValidation.validateCustReturnValue(custReturnValue, fixedQty_yn, fixedQty) ;
		
		if (custReturnValue == null) {
			LogUtility.getPumpMLogger().debug("[Pump M] 거래처 진행 이후 리턴 값이 없어서 Default 값을 구성한다.") ;
			
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
/*		2016. 4. 14. 오후 15:48:31, PI2, WooChul Jung.
 * 			사용되지 않는 코드이며, 면세여부는 단가기준으로 판단.
		if ((taxfree_cd != null) && (taxfree_cd.equals(ICode.TAXFREE_CD_02))) {
			isTaxFree = true ;
		}
		*/
		if (status_code_card.equals(ICode.STATUS_CODE_CARD_02) || status_code_card.equals(ICode.STATUS_CODE_CARD_03)) {
			LogUtility.getPumpMLogger().debug("[Pump M] 카드상태가 정지 혹은 말소가 된 것입니다.") ;
			// 카드가 정지 혹은 말소된 경우 
			state = ICustConstant.STATE_1 ;
		}

		// 카드 기준 설정 : card_code_base
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
				// 현금  고객 
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
				// 외상 고객
				cust_mileage_ind = temp_cust_mileage_ind ;
				card_code_base = ICode.CARD_CODE_BASE_02 ;	
				break ;
			}
			case ICustConstant.STATE_70 : {
				// 용역 보관 고객 - 점두가
				card_code_base = ICode.CARD_CODE_BASE_03 ;
				break ;
			}
			case ICustConstant.STATE_80 : {
				// 매출 보관 고객 - 점두가
				card_code_base = ICode.CARD_CODE_BASE_04 ;
				break ;
			}
			default : {
				card_code_base = ICode.CARD_CODE_BASE_06 ;
				break ;
			}				
		}

		// 카드 적용 구분 설정 : cardadj_ind
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
				cardadj_ind = ICode.CARDADJ_IND_00 ;	// 사용 안함
				break ;
			}
			case ICustConstant.STATE_30 : 
			case ICustConstant.STATE_31 : 
			case ICustConstant.STATE_32 : 
			case ICustConstant.STATE_33 : {
				cardadj_ind = ICode.CARDADJ_IND_01 ;	// 무제한
				break ;
			}
			case ICustConstant.STATE_40 : 	// 외상 거래처 - 차량별한도 - 정상
			case ICustConstant.STATE_41 : 
			case ICustConstant.STATE_42 : 
			case ICustConstant.STATE_43 : {
				cardadj_ind = ICode.CARDADJ_IND_03 ;	// 차량별
				break ;
			}
			case ICustConstant.STATE_50 : // 외상 거래처 - 거래처별한도 - 정상
			case ICustConstant.STATE_51 : 
			case ICustConstant.STATE_52 : 
			case ICustConstant.STATE_53 : {
				cardadj_ind = ICode.CARDADJ_IND_02 ;	// 거래처별
				break ;
			}
			case ICustConstant.STATE_60 : 
			case ICustConstant.STATE_61 : 
			case ICustConstant.STATE_62 : 
			case ICustConstant.STATE_63 : {
				cardadj_ind = ICode.CARDADJ_IND_04 ;	// 1회 정량
				break ;
			}
			case ICustConstant.STATE_90 : 
			case ICustConstant.STATE_91 : 
			case ICustConstant.STATE_92 : 
			case ICustConstant.STATE_93 : {
				cardadj_ind = ICode.CARDADJ_IND_05 ;	// 정량입력 - upkoo
				break ;
			}
			default : {
				cardadj_ind = ICode.CARDADJ_IND_00 ;	// 사용 안함	
				break ;
			}				
		}
		
		// 차량별 마스터의 상품 코드 불일치		- X3
		// PL 여신 문제										- X2
		// PL 상품 코드 불일치								- X1
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
			 * 2016. 4. 14. 오후 15:48:31, PI2, WooChul Jung.
			 * 	Bug Fix. 현금거래처 인데,  현금 거래처 - PL 문제일 경우, PL 여신정보가 존재하지 않는다라고 처리.
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
			 * 2016. 4. 14. 오후 15:48:31, PI2, WooChul Jung.
			 * 	Bug Fix. 현금거래처 인데, PL 상품 코드 불일치일 경우, 거래처고객판매여부에 따라서 점두가 일반 정상으로 판매
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
					LogUtility.getPumpMLogger().debug("[Pump M] 현금 거래처 - PL 상품 코드 불일치이기 때문에, '거래처고객판매여부'(매장마스터) 가 판매이기 " +
							"때문에 점두가로 판매를 한다. custsale_ind=" + custsale_ind) ;
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
					LogUtility.getPumpMLogger().debug("[Pump M] 외상 거래처 - 무제한 - PL 상품코드 불일치이기 때문에, '거래처고객판매여부'(매장마스터) 가 판매이기 " +
							"때문에 점두가로 판매를 한다. custsale_ind=" + custsale_ind) ;

					led_code = IUPOSConstant.LEDCODE_3 ;
					cardadj_ind = ICode.CARDADJ_IND_01 ;	// 무제한
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
					LogUtility.getPumpMLogger().debug("[Pump M] 외상 거래처 - 차량별한도 - PL 상품코드 불일치이기 때문에, '거래처고객판매여부'(매장마스터) 가 판매이기 " +
							"때문에 점두가로 판매를 한다. custsale_ind=" + custsale_ind) ;

					led_code = IUPOSConstant.LEDCODE_3 ;
					cardadj_ind = ICode.CARDADJ_IND_03 ;	// 차량별
					limit_type = ICode.DY_LIMIT_TYPE_01 ;	// 차량별 한도			
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
						LogUtility.getPumpMLogger().debug("[Pump M] 잔량이 " + applyRemainsDou + 
								" 이어서 0으로 재설정합니다. 이는 한도 초과입니다.") ;
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
					LogUtility.getPumpMLogger().debug("[Pump M] 외상 거래처 - 거래처별한도 - PL 상품코드 불일치이기 때문에, '거래처고객판매여부'(매장마스터) 가 판매이기 " +
							"때문에 점두가로 판매를 한다. custsale_ind=" + custsale_ind) ;

					led_code = IUPOSConstant.LEDCODE_3 ;
					cardadj_ind = ICode.CARDADJ_IND_02 ;	// 거래처별
					limit_type = ICode.DY_LIMIT_TYPE_02 ;	// 거래처별 한도		
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
						LogUtility.getPumpMLogger().debug("[Pump M] 잔량이 " + applyRemainsDou + 
																			" 이어서 0으로 재설정합니다. 이는 한도 초과 입니다.") ;
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
				
				// 자영매장의 경우
				if(ICode.STORE_CODE_CLASS_12.equals(storeCodeClass) || ICode.STORE_CODE_CLASS_11.equals(storeCodeClass)) {
					String amount1 = "";
					String discountBasePrice = "";
					try {
						//cardadj_ind-> 01:무제한 02:거래처별 03:차량별 04:1회정량	05:정량입력
						// fixedQty_yn-> 정량입력여부
						if ((custsale_ind != null) && (custsale_ind.equals("1"))) {
							LogUtility.getPumpMLogger().debug("[Pump M] 자영매장의 경우는 상 거래처 - 정량입력 - PL 상품코드 불일치시 점두가 외상판매한다.") ;
							discountBasePrice = custReturnValue.getDiscountBasePrice() ;
							
							// 거래조건이 없는(PL이없는) 정량입력 고객이면서 할인금액이 0인경우는 discountBasePrice를 basePrice로 한다. - upkoo 
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
								// 정량 입력 고객인데, 정량을 입력한 경우
								led_code = IUPOSConstant.LEDCODE_3 ;
								cardadj_ind = ICode.CARDADJ_IND_04 ;	// 1회 정량
								amount1 = fixedQty ;	
								
							} else {	
								// 정량입력 고객인데, 정량을 입력하지 않은경우
								LogUtility.getPumpMLogger().debug("[Pump M] 자영매장의 경우 외상 거래처 - 정량입력고객인데 정량을 입력하지 않은경우" +
										"주유한 리터로 정량을 설정한다.") ;
								
								// 외상 고객 - 정량 입력
								led_code = IUPOSConstant.LEDCODE_B ;
								cardadj_ind = ICode.CARDADJ_IND_05 ;	// 정량 입력
								
								// 정량입력을 하지않았을 경우 주유한 리터를 넣는다.
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
				// 직영매장의 경우
				else {
					proc_ind_overlimit = ICode.PROC_IND_OVERLIMIT_99 ;
					try {
						custsale_ind = T_KH_STOREHandler.getHandler().getCustsale_ind() ;
					} catch (Exception e) {
						LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
					}				
					if ((custsale_ind != null) && (custsale_ind.equals("1"))) {
						LogUtility.getPumpMLogger().debug("[Pump M] 상품 불일치 이지만, '거래처고객판매여부'(매장마스터) 가 판매이기 " +
								"때문에 점두가로 판매를 한다. custsale_ind=" + custsale_ind) ;
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
				// 용역 보관 고객 - 점두가
			case ICustConstant.STATE_80 : {
				// 매출 보관 고객 - 점두가
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
				// 영업 화물
			case ICustConstant.STATE_110 :
				// VIP
			case ICustConstant.STATE_10 : {
				// 현금 고객 
				cardadj_ind = ICode.CARDADJ_IND_01 ;	// 무제한
				proc_ind_overlimit = ICode.PROC_IND_OVERLIMIT_99 ;
				String discountBasePrice = custReturnValue.getDiscountBasePrice() ;
				boolean isDiscount = false ;
				
				// 2013.10.06  ksm  여기 수정필요.
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
				// 외상 고객 - 무제한
				led_code = IUPOSConstant.LEDCODE_3 ;
				cardadj_ind = ICode.CARDADJ_IND_01 ;	// 무제한
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
			case ICustConstant.STATE_40 : {	// 외상 거래처 - 차량별한도 - 정상
				led_code = IUPOSConstant.LEDCODE_3 ;
				cardadj_ind = ICode.CARDADJ_IND_03 ;	// 차량별
				limit_type = ICode.DY_LIMIT_TYPE_01 ;	// 차량별 한도			
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
					LogUtility.getPumpMLogger().debug("[Pump M] 잔량이 " + applyRemainsDou + 
							" 이어서 0으로 재설정합니다. 이는 한도 초과입니다.") ;
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
			case ICustConstant.STATE_50 : {	// 외상 거래처 - 거래처별한도 - 정상
				led_code = IUPOSConstant.LEDCODE_3 ;
				cardadj_ind = ICode.CARDADJ_IND_02 ;	// 거래처별
				limit_type = ICode.DY_LIMIT_TYPE_02 ;	// 거래처별 한도		
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
					LogUtility.getPumpMLogger().debug("[Pump M] 잔량이 " + applyRemainsDou + 
																		" 이어서 0으로 재설정합니다. 이는 한도 초과 입니다.") ;
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
				// 외상 고객 - 1회 정량
				led_code = IUPOSConstant.LEDCODE_3 ;
				cardadj_ind = ICode.CARDADJ_IND_04 ;	// 1회 정량
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
			
				// 자영매장의 경우
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
						
						LogUtility.getPumpMLogger().debug("[Pump M]자영매장의 경우 외상 거래처 - 정량입력 - 정상! " +
						"정량 입력 고객인데, 정량을 입력한 경우 점두가 외상판매!") ;
						
						// 정량 입력 고객인데, 정량을 입력한 경우
						led_code = IUPOSConstant.LEDCODE_3 ;
						cardadj_ind = ICode.CARDADJ_IND_04 ;	// 1회 정량
					}
					else {
						// 외상 고객 - 정량 입력
						led_code = IUPOSConstant.LEDCODE_B ;
						cardadj_ind = ICode.CARDADJ_IND_05 ;	// 정량 입력
					
						// upkoo 추가 - 정량입력을 하지않았을 경우, 주유한 liter를 넣는다.
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
						// 정량 입력 고객인데, 정량을 입력한 경우
						led_code = IUPOSConstant.LEDCODE_3 ;
						cardadj_ind = ICode.CARDADJ_IND_04 ;	// 1회 정량
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
						// 외상 고객 - 정량 입력
						led_code = IUPOSConstant.LEDCODE_B ;
						cardadj_ind = ICode.CARDADJ_IND_05 ;	// 정량 입력
					}
					break ;
				}
			}
			default : {
				LogUtility.getPumpMLogger().warn("[Pump M] 이 로그가 나오면 코드에 문제가 있다.") ;
				led_code = IUPOSConstant.LEDCODE_7 ;
			}
		}
		
		if (hasKeepNumber(dyInfoArray)) {			
			try {
				String storeCode = T_KH_STOREHandler.getHandler().getStoreCode() ;
				T_NZ_INFOData nzInfoData = T_NZ_INFOHandler.getHandler().getT_NZ_INFODataByStoreCode(storeCode) ;

				SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd") ;
				// 보관증 유효기간
				String expireDate = nzInfoData.getSave_expire_date() ;
				T_KH_STOREData storeData = 
				T_KH_STOREHandler.getHandler().getT_KH_STOREDataByStoreCode(storeCode) ;
				// 영업일
				String bizHourDate = storeData.getBizhour_date();
				
				// 현재 영업일에 보관증 유효기간을 더한다.
				// 영업일 형태가 올바르지 않다면 현재 날짜에서 보관증 유효기간을 더한다.
				if (bizHourDate.length() == 8) {
					// 영업일 중 년도
					int bizYear = Integer.parseInt(bizHourDate.substring(0, 4));
					// 영업일 중 월
					int bizMonth = Integer.parseInt(bizHourDate.substring(4, 6));
					// 영업일 중 일
					int bizDay = Integer.parseInt(bizHourDate.substring(6, 8));
					// 영업일에 보관증 유효기간을 더한다
					save_expire_date = formater.format( new GregorianCalendar(bizYear, bizMonth-1, 
							bizDay + Integer.parseInt(expireDate)).getTime()) ;
				} 
				else {
					// 보관증 유효기간을 ms단위로 변환한다. 
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
	 * 주유기에 설정된 단가로 주유정보를 재계산한다.
	 * 
	 * @param dyPumpM_Item	: DY 전문
	 */
	private static void resetBeforeBasePrice(POS_DY_Item dyPumpM_Item) {	

		LogUtility.getPumpMLogger().debug("[Pump M] 주유정보를 점두가로 재계산합니다. 사용되는 데이터는 다음과 같습니다.") ;
		LogUtility.getPumpMLogger().info(dyPumpM_Item.toString());


		dyPumpM_Item.setUnitPrice_after_discount(dyPumpM_Item.getUnitPrice_before_discount()) ;
		dyPumpM_Item.setOilPrice_after_discount(dyPumpM_Item.getOilPrice_before_discount()) ;
		dyPumpM_Item.setTaxPrice(GlobalUtility.getTaxPrice(dyPumpM_Item.getOilPrice_before_discount())) ;
		dyPumpM_Item.setPrice_before_tax(PumpMUtil.getPrice_before_tax(
				dyPumpM_Item.getOilPrice_before_discount(), dyPumpM_Item.getTaxPrice())) ;
		
		dyPumpM_Item.setUnitDiscount_ind("0") ;

		LogUtility.getPumpMLogger().debug("[Pump M] 점두가로 계산된 결과는 다음과 같습니다.") ;
		LogUtility.getPumpMLogger().info(dyPumpM_Item.toString());
	}
	
	/**
	 * 주유기에 설정된 단가로 주유정보를 재계산한다.
	 * 
	 * @param dyPumpM_Item	: DY 전문
	 * @param rentlimit_proc_ind_overlimit	: 외상결제타입
	 */
	private static void resetBeforeBasePrice(POS_DY_Item dyPumpM_Item , String rentlimit_proc_ind_overlimit) {	

		LogUtility.getPumpMLogger().debug("[Pump M] 주유정보를 재계산합니다. 사용되는 데이터는 다음과 같습니다.") ;
		LogUtility.getPumpMLogger().info(dyPumpM_Item.toString());
		LogUtility.getPumpMLogger().debug("[Pump M] rentlimit_proc_ind_overlimit=" + rentlimit_proc_ind_overlimit) ;

		if (rentlimit_proc_ind_overlimit.equals("02") || rentlimit_proc_ind_overlimit.equals("04")) {
			resetBeforeBasePrice(dyPumpM_Item) ;
		}
	}

	/**
	 * 면세 카드 고객으로 인해 Item Info 를 재구성할때 사용된다.
	 * 
	 * @param itemInfoItem
	 */
	public static void resetTaxFreePrice(UPOSMessage_ItemInfo_Item itemInfoItem) {
		
		LogUtility.getPumpMLogger().debug("[Pump M] 면세 고객이기 때문에 세금을 0으로 재계산합니다.") ;
		
		if (itemInfoItem == null) return ;
		
		try {
			String goodsCode = itemInfoItem.getGoodsCode() ;
			String taxFreePrice = T_KH_PRODUCTHandler.getHandler().getPrc_amt_taxfreeByGoods_code(goodsCode) ;
			
			String oilAmount = itemInfoItem.getOilAmount() ;								// 수량(8b)
			String unitPrice_after_discount = itemInfoItem.getUnitPrice_after_discount() ;	// 할인후단가(10b)
			String tax_ind = itemInfoItem.getTax_ind() ;									// 과면세구분(2b)
			String price_before_tax = itemInfoItem.getPrice_before_tax() ;					// 공급가액(10b)
			String taxPrice = itemInfoItem.getTaxPrice() ;									// 세금(10b)
			String oilPrice_after_discount = itemInfoItem.getOilPrice_after_discount() ;	// 할인후금액(10b)
			String unitDiscount_ind = itemInfoItem.getUnitDiscount_ind() ;					// 할인여부(1b)
			
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
		
		LogUtility.getPumpMLogger().debug("[Pump M] 재계산 결과 다음과 같습니다.") ;
		if (itemInfoItem != null) LogUtility.getPumpMLogger().debug(itemInfoItem.toString()) ;
		else {
			LogUtility.getPumpMLogger().debug("[Pump M] ItemInfoItem 이 없습니다.") ;
		}

	}
	
	/**
	 * 재승인 인정 금액을 null 로 초기화 시키는 함수.
	 * 재승인 인정 금액이 null이면 get함수 - getSa_min_amt() 에서는 DB에서 값을 구한다.
	 * 운영중에 POS에서 변경된 환경 설정 값을 실시간 반영하기 위해 첨가됨.
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
			//현금인 경우에는 재승인 최소 인정 금액과 상관없이 거스름돈을 발생시킨다.
			if (!isCreditCard )
				return false;
			
			if (IConstant.reRequest) {			// true  왜 한건지 모르겠음? ksm
				int paymentInt = Integer.parseInt(GlobalUtility.getStringValue(payment)) ;
				int pumpingPriceInt = Integer.parseInt(GlobalUtility.getStringValue(pumpingPrice)) ;
				int sas_min_amt = Integer.parseInt(GlobalUtility.getStringValue(getSa_min_amt())) ;
				
				int diff = paymentInt - pumpingPriceInt ;
				if (diff > sas_min_amt) {
					LogUtility.getPumpMLogger().info("[Pump M] paymentInt - pumpingPriceInt(" + diff + ") > sas_min_amt("+sas_min_amt+")") ;
					rlt = true ;
				}else{
					LogUtility.getPumpMLogger().info("[Pump M] 환경설정 0272 (셀프)거스름돈 기준금액 : " + sas_min_amt + " 으로 재승인 안함.") ;
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
