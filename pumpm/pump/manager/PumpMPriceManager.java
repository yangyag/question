package com.gsc.kixxhub.module.pumpm.pump.manager;

import com.gsc.kixxhub.common.data.IConstant;
import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.common.utility.property.PropertyManager;
import com.gsc.kixxhub.module.pumpm.pump.util.PumpMUtil;

public class PumpMPriceManager {

	public static int acceptablePriceGap = 2000 ;
	/**
	 * 현 주유건의 판매 단가
	 */
	public static double basePriceArray[] = null ;
	public static String basePriceArrayForPumpA[] = null ;
	public static final int INT_100_LITER_MIN = 100 ;
	
	public static final int INT_1000_LITER_DEFAULT = 1000 ;
	/**
	 * 리터 보정 단위
	 */
	public static final int INT_10000_LITER_DEFAULT = 10000 ;
	public static final int INT_100000_PRICE_MIN = 100000 ;
	/**
	 * 금액 보정 단위
	 */
	public static final int INT_1000000_PRICE_DEFAULT = 1000000 ;
	public static final int INT_90_LITER_MIN = 90 ;
	public static final int INT_900_LITER_DEFAULT = 900 ;
	
	public static final int INT_9000_LITER_DEFAULT = 9000 ;
	public static final int INT_90000_PRICE_MIN = 90000 ;
	public static final int INT_990000_PRICE_DEFAULT = 990000 ;

	public static int intervalInt = 1 ;
	public static double previousPumpLiterHash[] = null ;
	
	/**
	 * previousPumpPriceHash	: 직전 주유기로 부터 받은 주유 금액
	 * pumpSkippedPriceHash		: 보정되어져야 할 금액 (그전까지의 실 주유금액=previousPumpPriceHash+pumpSkippedPriceHash)
	 * previousPumpLiterHash	: 직전 주유기로 부터 받은 주유량
	 * pumpSkippedLiterHash		: 보정되어져야 할 주유량 (그전까지의 실 주유량=previousPumpLiterHash+pumpSkippedLiterHash)
	 * 각 노즐에 대한 금액 및 리터를 보정하기 위해 사용된다. 이에 대한 초기화는 주유완료 혹은 주유시작시 초기화 된다.
	 */
	public static int previousPumpPriceHash[] = null ;
	/**
	 * 주유 중 데이터의 경우 빈번히 발생하며 실질적으로 의미가 없는 데이터 이다. 단지 UI 용으로 Display 하기 위한 용도 이기 때문에,
	 * 빈번히 발생하고 있는 주유 중 데이터의 경우 Interval 에 따라서 사이 데이터는 누락시키도록 한다. 
	 */
	public static int pumpingInterval[] = null ;
	
	public static double pumpSkippedLiterHash[] = null ;
	public static int pumpSkippedPriceHash[] = null ;

	/**
	 * 주유기로 받은 가장 최근의 주유완료 혹은 주유시작의 Total Gauage. 이 데이터는 가상의 주유시작 전문을 생성할때 사용된다.
	 */
	public static String totalGauage[] = null ;
	
	public static void destroy() {
	
		
	}
	
	/**
	 * 노즐의 현 주유건에 대한 단가를 요청한다.
	 * 
	 * @param nozzle_no	: 노즐 번호
	 * @return
	 */
	public static double getBasePrice(int nozzle_noInt) {
		double basePrice = basePriceArray[nozzle_noInt] ;
		
		if (basePrice == 0) {
			basePrice = 
				Double.parseDouble(PumpMUtil.getBasePriceFromNozzleNo(GlobalUtility.appending0Pre(Integer.toString(nozzle_noInt), 2))) ;
			setBasePrice(nozzle_noInt, basePrice) ;
		}
		
		return basePrice ;
	}
	
	public static String getCurrBasePriceForPumpA(int nozzle_noInt) {
		String basePrice = "0" ;		
		basePrice = basePriceArrayForPumpA[nozzle_noInt] ;		
		if ((basePrice == null) || ("0".equals(basePrice))) {
			getBasePrice(nozzle_noInt) ;
			basePrice = basePriceArrayForPumpA[nozzle_noInt] ;
		}
		return basePrice ;
	}
	
	/**
	 * 이전까지 주유금액을 리턴한다.
	 * 
	 * @param nozzle_noInt
	 * @return
	 */
	public static double getCurrPrice(int nozzle_noInt) {
		return (pumpSkippedPriceHash[nozzle_noInt] + previousPumpPriceHash[nozzle_noInt]) ;
	}
	
	
	/**
	 * 현 주유건에 대한 보정된 리터를 계산한다.
	 * 
	 * @param nozzle_noInt	: 노즐 번호
	 * @param pumpLiter		: 주유기로 부터 받은 리터
	 * @return
	 */
	public static String getRightPumpLiter(int nozzle_noInt, String pumpLiter) {
		double pumpLiterDou = Double.parseDouble(pumpLiter) ;
		// 주유정보 보정함.
		double rightPumpLiter = skippedPumpingLiter(nozzle_noInt, previousPumpLiterHash[nozzle_noInt], pumpLiterDou) ;
	
		previousPumpLiterHash[nozzle_noInt] = rightPumpLiter ;
		
		return Double.toString(rightPumpLiter) ;
	}

	/**
	 * 노즐 상품에 따라서 100 (or 10)만원 이상을 처리하지 못하기 때문에, 현 주유 정보의 가격을 그 전과 비교하여 올바른 가격으로 산출.
	 * 
	 * @param nozzle_noInt	: 노즐 번호
	 * @param pumpPrice	: 주유기로 부터 받은 가격
	 * @return
	 */
	public static String getRightPumpPrice(int nozzle_noInt, String pumpPrice) {
		int pumpPriceDou = Integer.parseInt(pumpPrice) ;
		int rightPumpPrice = skippedPumpingPrice(nozzle_noInt, previousPumpPriceHash[nozzle_noInt], pumpPriceDou) ;
			
		previousPumpPriceHash[nozzle_noInt] = rightPumpPrice ;
		
		return Integer.toString(rightPumpPrice) ;
	}	
	
	/**
	 * 현 주유건에 대해, 단가 * 리터의 값이 주유완료 금액과의 차이가 +/- 100 원 이상 발생시 
	 * 주유완료 금액을 무시하고 단가 * 리터의 값을 올바른 주유완료금액으로 인정한다
	 * 
	 * @param nozzle_noInt
	 * @param pumpLiter
	 * @param pumpPrice
	 * @param basePrice
	 * @return
	 */
	public static String getRightPumpS4Price(int nozzle_noInt, String pumpLiter, String pumpPrice, String basePrice) {
		String setPumpPrice = pumpPrice;
		double pumpLiter_double = Double.parseDouble(pumpLiter);
		double basePrice_double = Double.parseDouble(PumpMUtil.convertNumberFormatFromPumpToPOS(basePrice,2));
		int pumpPrice_int = Integer.parseInt(pumpPrice);
		
		double priceGap = (pumpLiter_double * basePrice_double) - pumpPrice_int ;
		
		if (priceGap > 100 || -100 > priceGap) {
			setPumpPrice = Double.toString(Math.floor(pumpLiter_double * basePrice_double)) ;
			LogUtility.getPumpMLogger().debug("[Pump M] ReSet Right S4Price, nz="+nozzle_noInt
					+ "#주유금액=" + pumpPrice + "#주유리터=" + pumpLiter + "#단가=" + basePrice + "#보정된주유금액=" + setPumpPrice					
			);
		}
		
		return setPumpPrice;
	}

	/**
	 * 보정된 리터를 리턴한다.
	 * 
	 * @param nozzle_noInt
	 * @return
	 */
	public static double getSkippedLiter(int nozzle_noInt) {
		return pumpSkippedLiterHash[nozzle_noInt] ;
	}

	/**
	 * 보정된 금액을 리턴한다.
	 *  
	 * @param nozzle_noInt
	 * @return
	 */
	public static int getSkippedPrice(int nozzle_noInt) {
		return pumpSkippedPriceHash[nozzle_noInt] ;
	}
	
	/**
	 * 현 노즐의 Total Gauage 를 가져 온다.
	 * 가상의 주유시작 전문을 만들때 이 함수를 이용하여 Total Gauage 를 이용한다.
	 * 
	 * @param nozzle_noInt		: 노즐 번호
	 * @return
	 */
	public static String getTotalGauage(int nozzle_noInt) {
		return totalGauage[nozzle_noInt] ;
	}
	
	/**
	 * PumpMPriceManager 에서 관리하고 있는 데이터들을 초기화 한다.
	 *
	 */
	public static void init() {
		String interval = PropertyManager.getSingleton().getProperty(PropertyManager.PUMP_PUMPING_INTERVAL, PropertyManager.PUMP_PUMPING_INTERVAL_DEFAULT) ;
		String acceptablePriceGapStr = PropertyManager.getSingleton().getProperty(PropertyManager.PUMP_PUMPING_ACCEPTABLEPRICEGAP, PropertyManager.PUMP_PUMPING_ACCEPTABLEPRICEGAP_DEFAULT) ;
		intervalInt = Integer.parseInt(interval) ;
		acceptablePriceGap = Integer.parseInt(acceptablePriceGapStr) ;
		if (intervalInt < 1) {
			intervalInt = 1 ;
		}
		pumpingInterval = new int[IConstant.PUMP_MAX] ;

		totalGauage = new String[IConstant.PUMP_MAX] ;
		basePriceArray = new double[IConstant.PUMP_MAX] ;
		
		previousPumpPriceHash = new int[IConstant.PUMP_MAX] ;
		pumpSkippedPriceHash = new int[IConstant.PUMP_MAX] ;
		
		
		previousPumpLiterHash = new double[IConstant.PUMP_MAX] ;
		pumpSkippedLiterHash = new double[IConstant.PUMP_MAX] ;
		
		basePriceArrayForPumpA = new String[IConstant.PUMP_MAX] ;
		
		for (int i = 0 ; i < IConstant.PUMP_MAX ; i++) {
			pumpingInterval[i] = 0 ;
			totalGauage[i] = "0" ;
			basePriceArray[i] = 0 ;

			previousPumpPriceHash[i] = 0 ;
			pumpSkippedPriceHash[i] = 0 ;

			previousPumpLiterHash[i] = 0 ;
			pumpSkippedLiterHash[i] = 0 ;
			
			basePriceArrayForPumpA[i] = "0" ;
		}		
	}
	
	
	/**
	 * 현 노즐의 가격을 초기화한다.
	 * 이는 PumpA 로 부터 주유시작 받은 이후, PumpA 로 부터 주유완료 받은 이후 수행한다.
	 * 또한 PumpA 로 부터 주유시작 전문이 오지 않아서 Pump M 에서 가상의 주유시작 전문을 만들때에도 호출된다.
	 * @param nozzle_noInt	: 노즐 번호
	 */
	public static void initPumpPrice(int nozzle_noInt) {
		LogUtility.getLogger().info("[Pump M] 노줄의 가격및 리터 초기화.nozzleNo=" + nozzle_noInt);
		
		pumpingInterval[nozzle_noInt] = 0 ;
		
		previousPumpPriceHash[nozzle_noInt] = 0 ;
		pumpSkippedPriceHash[nozzle_noInt] = 0 ;
		previousPumpLiterHash[nozzle_noInt] = 0 ;
		pumpSkippedLiterHash[nozzle_noInt] = 0 ;
	}
	
	/**
	 * 주유중 정보의 경우 POS 에게 보내는 전문이 너무 빈번하기 때문에 Interval 간격을 주었다.
	 * [2008.05.08]
	 * 	1. [개선사항] 주유중 데이터의 경우 KixxHub Port 당 0.1 초에 최대 1건이 전송되기 때문에, 8 개의 Port 를 가지고 있는 KixxHub 는
	 * 		최대 0.1 초당 8개의 주유중 전문을 전송할 수 있다. 이 경우 Delay 가 생길수 있기 때문에, Interval 변수를 둔다.
	 * 
	 * @param commandID	: POS 전문 Command ID
	 * @param nozzleNo	: 노즐 번호
	 * @return
	 */
	public static boolean isSendPumpingContentToPOS(String commandID, int nozzle_noInt) {
		boolean rlt = true ;		
		try {
			// 주유중 전문일 경우
			if (commandID == IPumpConstant.COMMANDID_S3) {
				if ((pumpingInterval[nozzle_noInt] % intervalInt) == 0) {
					rlt = true ;
				} else {
					rlt = false ;
				}
				pumpingInterval[nozzle_noInt]++ ;
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		return rlt ;
	}
	
	/**
	 * 현 주유건에 대한 노즐의 단가를 저정한다. 이는 노즐의 점두가 혹은 Preset 에 의한 단가일수 있다.
	 * 이 함수는 주유시작 전문을 PumpA 로 부터 받으면, T_KH_PUMP_TR 테이블의 정보를 이용하여 설정한다.
	 * 
	 * @param nozzle_noInt	: 노즐 번호
	 * @param basePrice		: 단가
	 */
	public static void setBasePrice(int nozzle_noInt, double basePrice) {
		basePriceArray[nozzle_noInt] = basePrice ;
		basePriceArrayForPumpA[nozzle_noInt] = GlobalUtility.getStringValue(basePrice * 100) ;
		LogUtility.getPumpMLogger().debug("[Pump M] nozzle_no="+nozzle_noInt+"#basePrice=" +
				basePrice+"#basePriceArrayForPumpA=" +basePriceArrayForPumpA[nozzle_noInt]) ;
	}
	
	
	/**
	 * 주유 시작 및 주유 완료시 Total Gauage 를 저장한다
	 * 이 정보는 가상의 주유시작 전문을 만들때 사용한다.
	 * 
	 * @param nozzle_noInt		: 노즐 번호
	 * @param currGauage	: 현재 Total Gauage
	 */
	public static void setTotalGauage(int nozzle_noInt, String currGauage) {
		totalGauage[nozzle_noInt] = currGauage ;
	}

	/**
	 * 누락된 주유량에 대해서 조사를 한다.
	 * 주유량의 경우
	 * 	그전 주유량과의 차이가 900 리터 인 경우 1000 리터를 보정한다.
	 * 	그전 주유량과의 차이가 90 리터 인 경우 100 리터를 보정한다.
	 * 
	 * 2013.12.16 흑석주유소  10000리터 이상 주유시 11000까지만 주유중정보 표시하고 이후 보정로직 예외사항으로 1000대로 떨어짐.
	 *                                          더 큰 문제는 전자식 계기 증가량이 1000대로 넘어옴. 소모에 문의결과 주유기 프로토콜상 수정이 어렵다고 함.
	 * 
	 * @param nozzle_no		: 노즐 번호
	 * @param preLiter		: 그전 주유 량
	 * @param currLitere	: 현재 주유 량
	 * @return
	 */
	public static double skippedPumpingLiter(int nozzle_no, double preLiter, double currLiter) {
				
		if (preLiter == 0 )
			return currLiter;
		
		double skippedLiter = preLiter - currLiter ;
		double rightLiter = 0;

		if (skippedLiter < 0)
			return currLiter;
		
		rightLiter = (int)((skippedLiter + 50) * 0.01) * 100;
		rightLiter = rightLiter + currLiter;
		//LogUtility.getPumpMLogger().debug("[TEST] rightLiter - currLiter = " + rightLiter);
		// 테스트 장비 주유가 4000리터만큼만 가능함. 테스트 불가.
		if(rightLiter - currLiter > INT_9000_LITER_DEFAULT && currLiter > 1000 ){
			//LogUtility.getPumpMLogger().debug("[TEST] rightLiter : " + rightLiter);
			return rightLiter;
		}
		
		if (rightLiter - currLiter > INT_90_LITER_MIN  && rightLiter - currLiter <= INT_900_LITER_DEFAULT && currLiter > 100 ){
			rightLiter = currLiter;
		}
		
		if (rightLiter - currLiter > INT_900_LITER_DEFAULT && currLiter > 1000 ){
			rightLiter = currLiter;
		}	
		
		if (rightLiter == 0)
			rightLiter = currLiter;
		
		return rightLiter;
		
	}
	
	/**
	 * 누락된 금액에 대해서 조사를 한다.
	 * 금액의 경우
	 * 	그전 금액과의 차이가 99만원 인 경우
	 * 		그전 금액이 99만원에서 100만원 사이인 경우 100만원 보정한다.
	 * 	그전 금액과의 차이가 9만원 인 경우
	 * 		그전 금액이 9만원에서 10만원 사이인 경우 10만원 보정한다.
	 * 
	 * @param nozzle_no	: 노즐 번호
	 * @param prePrice	: 그전 주유 금액
	 * @param currPrice	: 현재 주유 금액
	 * @return
	 */
	private static int skippedPumpingPrice(int nozzle_no, int prePrice, int currPrice) {
		
		if (prePrice == 0 )
			return currPrice;
		
		int skippedPrice = prePrice - currPrice ;
		int rightPrice = 0;
		
		if (skippedPrice < 0)
			return currPrice;
		
		rightPrice = (int)((skippedPrice + 50000) * 0.00001) * 100000;
		rightPrice = rightPrice + currPrice;
		
		if (rightPrice - currPrice > INT_90000_PRICE_MIN  && 
				rightPrice - currPrice <= INT_990000_PRICE_DEFAULT&&
				currPrice > INT_100000_PRICE_MIN ){
			rightPrice = currPrice;
		}
		
		if (rightPrice - currPrice > INT_990000_PRICE_DEFAULT &&
				currPrice > INT_1000000_PRICE_DEFAULT ){
			rightPrice = currPrice;
		}
		
		if (rightPrice == 0)
			rightPrice = currPrice;
		
		return rightPrice;
	}

}
