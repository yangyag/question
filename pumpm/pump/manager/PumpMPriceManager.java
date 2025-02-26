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
	 * �� �������� �Ǹ� �ܰ�
	 */
	public static double basePriceArray[] = null ;
	public static String basePriceArrayForPumpA[] = null ;
	public static final int INT_100_LITER_MIN = 100 ;
	
	public static final int INT_1000_LITER_DEFAULT = 1000 ;
	/**
	 * ���� ���� ����
	 */
	public static final int INT_10000_LITER_DEFAULT = 10000 ;
	public static final int INT_100000_PRICE_MIN = 100000 ;
	/**
	 * �ݾ� ���� ����
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
	 * previousPumpPriceHash	: ���� ������� ���� ���� ���� �ݾ�
	 * pumpSkippedPriceHash		: �����Ǿ����� �� �ݾ� (���������� �� �����ݾ�=previousPumpPriceHash+pumpSkippedPriceHash)
	 * previousPumpLiterHash	: ���� ������� ���� ���� ������
	 * pumpSkippedLiterHash		: �����Ǿ����� �� ������ (���������� �� ������=previousPumpLiterHash+pumpSkippedLiterHash)
	 * �� ���� ���� �ݾ� �� ���͸� �����ϱ� ���� ���ȴ�. �̿� ���� �ʱ�ȭ�� �����Ϸ� Ȥ�� �������۽� �ʱ�ȭ �ȴ�.
	 */
	public static int previousPumpPriceHash[] = null ;
	/**
	 * ���� �� �������� ��� ����� �߻��ϸ� ���������� �ǹ̰� ���� ������ �̴�. ���� UI ������ Display �ϱ� ���� �뵵 �̱� ������,
	 * ����� �߻��ϰ� �ִ� ���� �� �������� ��� Interval �� ���� ���� �����ʹ� ������Ű���� �Ѵ�. 
	 */
	public static int pumpingInterval[] = null ;
	
	public static double pumpSkippedLiterHash[] = null ;
	public static int pumpSkippedPriceHash[] = null ;

	/**
	 * ������� ���� ���� �ֱ��� �����Ϸ� Ȥ�� ���������� Total Gauage. �� �����ʹ� ������ �������� ������ �����Ҷ� ���ȴ�.
	 */
	public static String totalGauage[] = null ;
	
	public static void destroy() {
	
		
	}
	
	/**
	 * ������ �� �����ǿ� ���� �ܰ��� ��û�Ѵ�.
	 * 
	 * @param nozzle_no	: ���� ��ȣ
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
	 * �������� �����ݾ��� �����Ѵ�.
	 * 
	 * @param nozzle_noInt
	 * @return
	 */
	public static double getCurrPrice(int nozzle_noInt) {
		return (pumpSkippedPriceHash[nozzle_noInt] + previousPumpPriceHash[nozzle_noInt]) ;
	}
	
	
	/**
	 * �� �����ǿ� ���� ������ ���͸� ����Ѵ�.
	 * 
	 * @param nozzle_noInt	: ���� ��ȣ
	 * @param pumpLiter		: ������� ���� ���� ����
	 * @return
	 */
	public static String getRightPumpLiter(int nozzle_noInt, String pumpLiter) {
		double pumpLiterDou = Double.parseDouble(pumpLiter) ;
		// �������� ������.
		double rightPumpLiter = skippedPumpingLiter(nozzle_noInt, previousPumpLiterHash[nozzle_noInt], pumpLiterDou) ;
	
		previousPumpLiterHash[nozzle_noInt] = rightPumpLiter ;
		
		return Double.toString(rightPumpLiter) ;
	}

	/**
	 * ���� ��ǰ�� ���� 100 (or 10)���� �̻��� ó������ ���ϱ� ������, �� ���� ������ ������ �� ���� ���Ͽ� �ùٸ� �������� ����.
	 * 
	 * @param nozzle_noInt	: ���� ��ȣ
	 * @param pumpPrice	: ������� ���� ���� ����
	 * @return
	 */
	public static String getRightPumpPrice(int nozzle_noInt, String pumpPrice) {
		int pumpPriceDou = Integer.parseInt(pumpPrice) ;
		int rightPumpPrice = skippedPumpingPrice(nozzle_noInt, previousPumpPriceHash[nozzle_noInt], pumpPriceDou) ;
			
		previousPumpPriceHash[nozzle_noInt] = rightPumpPrice ;
		
		return Integer.toString(rightPumpPrice) ;
	}	
	
	/**
	 * �� �����ǿ� ����, �ܰ� * ������ ���� �����Ϸ� �ݾװ��� ���̰� +/- 100 �� �̻� �߻��� 
	 * �����Ϸ� �ݾ��� �����ϰ� �ܰ� * ������ ���� �ùٸ� �����Ϸ�ݾ����� �����Ѵ�
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
					+ "#�����ݾ�=" + pumpPrice + "#��������=" + pumpLiter + "#�ܰ�=" + basePrice + "#�����������ݾ�=" + setPumpPrice					
			);
		}
		
		return setPumpPrice;
	}

	/**
	 * ������ ���͸� �����Ѵ�.
	 * 
	 * @param nozzle_noInt
	 * @return
	 */
	public static double getSkippedLiter(int nozzle_noInt) {
		return pumpSkippedLiterHash[nozzle_noInt] ;
	}

	/**
	 * ������ �ݾ��� �����Ѵ�.
	 *  
	 * @param nozzle_noInt
	 * @return
	 */
	public static int getSkippedPrice(int nozzle_noInt) {
		return pumpSkippedPriceHash[nozzle_noInt] ;
	}
	
	/**
	 * �� ������ Total Gauage �� ���� �´�.
	 * ������ �������� ������ ���鶧 �� �Լ��� �̿��Ͽ� Total Gauage �� �̿��Ѵ�.
	 * 
	 * @param nozzle_noInt		: ���� ��ȣ
	 * @return
	 */
	public static String getTotalGauage(int nozzle_noInt) {
		return totalGauage[nozzle_noInt] ;
	}
	
	/**
	 * PumpMPriceManager ���� �����ϰ� �ִ� �����͵��� �ʱ�ȭ �Ѵ�.
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
	 * �� ������ ������ �ʱ�ȭ�Ѵ�.
	 * �̴� PumpA �� ���� �������� ���� ����, PumpA �� ���� �����Ϸ� ���� ���� �����Ѵ�.
	 * ���� PumpA �� ���� �������� ������ ���� �ʾƼ� Pump M ���� ������ �������� ������ ���鶧���� ȣ��ȴ�.
	 * @param nozzle_noInt	: ���� ��ȣ
	 */
	public static void initPumpPrice(int nozzle_noInt) {
		LogUtility.getLogger().info("[Pump M] ������ ���ݹ� ���� �ʱ�ȭ.nozzleNo=" + nozzle_noInt);
		
		pumpingInterval[nozzle_noInt] = 0 ;
		
		previousPumpPriceHash[nozzle_noInt] = 0 ;
		pumpSkippedPriceHash[nozzle_noInt] = 0 ;
		previousPumpLiterHash[nozzle_noInt] = 0 ;
		pumpSkippedLiterHash[nozzle_noInt] = 0 ;
	}
	
	/**
	 * ������ ������ ��� POS ���� ������ ������ �ʹ� ����ϱ� ������ Interval ������ �־���.
	 * [2008.05.08]
	 * 	1. [��������] ������ �������� ��� KixxHub Port �� 0.1 �ʿ� �ִ� 1���� ���۵Ǳ� ������, 8 ���� Port �� ������ �ִ� KixxHub ��
	 * 		�ִ� 0.1 �ʴ� 8���� ������ ������ ������ �� �ִ�. �� ��� Delay �� ����� �ֱ� ������, Interval ������ �д�.
	 * 
	 * @param commandID	: POS ���� Command ID
	 * @param nozzleNo	: ���� ��ȣ
	 * @return
	 */
	public static boolean isSendPumpingContentToPOS(String commandID, int nozzle_noInt) {
		boolean rlt = true ;		
		try {
			// ������ ������ ���
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
	 * �� �����ǿ� ���� ������ �ܰ��� �����Ѵ�. �̴� ������ ���ΰ� Ȥ�� Preset �� ���� �ܰ��ϼ� �ִ�.
	 * �� �Լ��� �������� ������ PumpA �� ���� ������, T_KH_PUMP_TR ���̺��� ������ �̿��Ͽ� �����Ѵ�.
	 * 
	 * @param nozzle_noInt	: ���� ��ȣ
	 * @param basePrice		: �ܰ�
	 */
	public static void setBasePrice(int nozzle_noInt, double basePrice) {
		basePriceArray[nozzle_noInt] = basePrice ;
		basePriceArrayForPumpA[nozzle_noInt] = GlobalUtility.getStringValue(basePrice * 100) ;
		LogUtility.getPumpMLogger().debug("[Pump M] nozzle_no="+nozzle_noInt+"#basePrice=" +
				basePrice+"#basePriceArrayForPumpA=" +basePriceArrayForPumpA[nozzle_noInt]) ;
	}
	
	
	/**
	 * ���� ���� �� ���� �Ϸ�� Total Gauage �� �����Ѵ�
	 * �� ������ ������ �������� ������ ���鶧 ����Ѵ�.
	 * 
	 * @param nozzle_noInt		: ���� ��ȣ
	 * @param currGauage	: ���� Total Gauage
	 */
	public static void setTotalGauage(int nozzle_noInt, String currGauage) {
		totalGauage[nozzle_noInt] = currGauage ;
	}

	/**
	 * ������ �������� ���ؼ� ���縦 �Ѵ�.
	 * �������� ���
	 * 	���� ���������� ���̰� 900 ���� �� ��� 1000 ���͸� �����Ѵ�.
	 * 	���� ���������� ���̰� 90 ���� �� ��� 100 ���͸� �����Ѵ�.
	 * 
	 * 2013.12.16 �漮������  10000���� �̻� ������ 11000������ ���������� ǥ���ϰ� ���� �������� ���ܻ������� 1000��� ������.
	 *                                          �� ū ������ ���ڽ� ��� �������� 1000��� �Ѿ��. �Ҹ� ���ǰ�� ������ �������ݻ� ������ ��ƴٰ� ��.
	 * 
	 * @param nozzle_no		: ���� ��ȣ
	 * @param preLiter		: ���� ���� ��
	 * @param currLitere	: ���� ���� ��
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
		// �׽�Ʈ ��� ������ 4000���͸�ŭ�� ������. �׽�Ʈ �Ұ�.
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
	 * ������ �ݾ׿� ���ؼ� ���縦 �Ѵ�.
	 * �ݾ��� ���
	 * 	���� �ݾװ��� ���̰� 99���� �� ���
	 * 		���� �ݾ��� 99�������� 100���� ������ ��� 100���� �����Ѵ�.
	 * 	���� �ݾװ��� ���̰� 9���� �� ���
	 * 		���� �ݾ��� 9�������� 10���� ������ ��� 10���� �����Ѵ�.
	 * 
	 * @param nozzle_no	: ���� ��ȣ
	 * @param prePrice	: ���� ���� �ݾ�
	 * @param currPrice	: ���� ���� �ݾ�
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
