package com.gsc.kixxhub.module.pumpm.pump.manager.gsc;

import java.util.Hashtable;

import com.gsc.kixxhub.common.data.IConstant;
import com.gsc.kixxhub.common.data.pump.GA_WorkingMessage;
import com.gsc.kixxhub.common.data.upos.UPOSMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.common.utility.property.PropertyManager;
import com.gsc.kixxhub.module.pumpm.pump.data.odt.GSCSelfODTNozzleInfo;
import com.gsc.kixxhub.module.pumpm.pump.util.PumpMUtil;

public class GSCSelfPumpingManager {
	
	/**
	 * 	selfodt.fullpumping.option 의 의미는 아래와 같다. 이 Class 는 GSC 셀프에서만 사용된다.
	 *		0	: 	BlackList check -> Request for Approval
	 *		1	:	149,900 Won Request for Approval -> After pumping completion, request real pumping price for Approval -> 149,900 Won request for cancel
	 *		2	:	149,900 Won Request for Approval -> After pumping completion, 149,900 Won request for cancel -> request real pumping price for Approval
	 */
	public static final int FULL_PUMPING_PRICE = Integer.parseInt(PropertyManager.getSingleton().getProperty(PropertyManager.SELFODT_FULLPUMPING_PRICE, PropertyManager.SELFODT_FULLPUMPING_PRICE_DEFAULT)) ;
	private static GSCSelfPumpingManager selfOdtFullPumpingMgr = null ;
	
	public static void destroy() {
		selfOdtFullPumpingMgr = null ;
	}
	
	public static GSCSelfPumpingManager getInstance() {
		if (selfOdtFullPumpingMgr == null) {
			selfOdtFullPumpingMgr = new GSCSelfPumpingManager() ;
		}
		return selfOdtFullPumpingMgr ;
	}
	
	public static void init() {
		selfOdtFullPumpingMgr = new GSCSelfPumpingManager() ;
	}
	
	/**
	 * nozzleHash 에서 관리하고 있는 ODTNozzleInfo Object 는 다음의 경우 새로 생성된다.
	 * 	1. GSC 셀프에서 최초 결재 요청시 생성된다.
	 * 	2. POS 로 부터 Preset 인 경우에도 새로 생성이 된다.
	 * 그리고 다음의 경우 제거한다
	 * 	1. 새로운 KH 처리번호 생성시
	 * ODTNozzleInfo Object 는 가득 주유 여부를 관리하고 있으며, 가득 주유인 경우 관련 정보들을 갱신한다.
	 */
	public Hashtable<String, GSCSelfODTNozzleInfo> nozzleHash = null ;
	
	public Hashtable<String, String> nozzleNoHash = null ;
	
	public GSCSelfPumpingManager() {
		nozzleHash = new Hashtable<String, GSCSelfODTNozzleInfo>() ;
		nozzleNoHash = new Hashtable<String, String>() ;
	}
		
	/**
	 * 응답 전문을 저장한다. 이 응답 전문들은 차후 영수증을 찍기 위해서 사용되어 질 수 있다.
	 * 
	 * @param nozzleNo		: 노즐 번호
	 * @param uPosMsg		: 응답 UPOSMessage
	 */
	public void addRespondUPOSMessage(String nozzleNo, UPOSMessage uPosMsg) {
		try {
			GSCSelfODTNozzleInfo nozInfo = nozzleHash.get(nozzleNo) ;

			nozInfo.addRespondUPOSMessage(uPosMsg) ;
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
		}
	}
	
	
	/**
	 * [2008.11.21] by WooChul Jung
	 * GSC 셀프의 경우 POS 로 부터 Preset 이 전송된다. 이 경우 이전의 자료를 제거하고 새롭게 이 주유건이 가득 주유가 아님을
	 * 표시한다. 동탄 신도시 주유소의 경우 아래와 같은 문제가 발생하였다.
	 * 		1. 주유건-A : 손님 A 가 가득 주유하여 주유완료
	 * 		2  주유건-B : 손님 B 가 POS 에서 정액/정량 설정 하여 주유 완료
	 * 			이때 아래 코드가 없었을 당시 주유건-B 가 완료되었을때 손님 A 의 카드로 결재가 진행되었다.
	 * 
	 * @param nozzleNo	: 노즐 번호
	 * @param isFull	: 가득 주유 여부
	 */
	public void createODTNozzleInfo(String nozzleNo, boolean isFull, int option) {
		try {
			if (PumpMUtil.isNozzleConnectedToSelfODT(nozzleNo)) {
				nozzleHash.remove(nozzleNo) ;
				GSCSelfODTNozzleInfo nozInfo = new GSCSelfODTNozzleInfo(nozzleNo, isFull, option) ;
				nozzleHash.put(nozzleNo, nozInfo) ;
			} 
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
		}
	}
	/**
	 * PI2, CWI, 2016.03.08
	 * 망취소 응답 전문에 사용할 노줄번호를 Hashtable에 저장한다.
	 */
	public void createODTNozzleNo(String trx_proper_no, String nozzleNo) {
		nozzleNoHash.put(trx_proper_no, nozzleNo) ; // key : 거래고유 번호, value : 노줄번호
	}
	
	
	/**
	 * 
	 * @param nozzleNo
	 * @return
	 */
	public int getCurrentOption(String nozzleNo) {
		int option = IConstant.FULL_PUMPING_OPTION_8 ;
		try {
			GSCSelfODTNozzleInfo nozInfo = nozzleHash.get(nozzleNo) ;
			if (nozInfo != null) {
				option = nozInfo.getOption() ;
			} 
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage() ,e) ;
		}
		LogUtility.getPumpMLogger().info("[Pump M] " + nozzleNo + " Nozzle is option= " + option) ;
		return option ;
	}
	
	
	/**
	 * 
	 * @param nozzleNo
	 * @return
	 */
	public GSCSelfODTNozzleInfo getGSCODTNozzleInfo(String nozzleNo) {
		return nozzleHash.get(nozzleNo) ;
	}
	
	/**
	 * PI2, CWI, 2016.03.17
	 * Hashtable에 저장한 노줄정보를 가져온다
	 * @param trx_proper_no
	 * @return
	 */
	public String getGSCODTNozzleNo(String trx_proper_no) {
		return nozzleNoHash.get(trx_proper_no) ;
	}
	
	/**
	 * 주유완료 여부를 조사한다.
	 * 
	 * @param nozzleNo	: 노즐 번호
	 * @return
	 */
	public boolean isPumpingCompleted(String nozzleNo) {
		boolean rlt = false ;
		try {
			GSCSelfODTNozzleInfo nozInfo = nozzleHash.get(nozzleNo) ;
			rlt =  nozInfo.isPumpingCompleted() ;
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage() ,e) ;
		}
		return rlt ;
	}
	
	
	/**
	 * GSC 셀프의 KH 처리번호 새로 생성시 이 함수를 호출하여 기존 정보를 안전하게 제거하도록 한다.
	 * 
	 * @param nozzleNo		: 노즐 번호
	 */
	public void removeODTNozzleInfo(String nozzleNo) {
		try {
			if (PumpMUtil.isNozzleConnectedToSelfODT(nozzleNo)) {
				nozzleHash.remove(nozzleNo) ;
			} 	
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage() , e) ;
		}
	}
	
	
	public void removeODTNozzleNo(String trx_proper_no) {
		nozzleNoHash.remove(trx_proper_no) ;
	}
	
	
	/**
	 * 주유완료 여부를 설정한다.
	 * 
	 * @param nozzleNo 		: 노즐 번호
	 * @param isCompleted	: 주유완료 여부
	 */
	public void setPumpingCompleted(String nozzleNo, boolean isCompleted) {
		try {
			GSCSelfODTNozzleInfo nozInfo = nozzleHash.get(nozzleNo) ;
			nozInfo.setPumpingCompleted(isCompleted) ;
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
		}
	}
	
	/**
	 * 승인 요청 전문을 저장한다.
	 * 
	 * @param nozzleNo		: 노즐 번호
	 * @param gaWorkMsg		: 승인 요청 전문 .GA WorkingMessage
	 */
	public void setWorkingMessage(String nozzleNo, GA_WorkingMessage gaWorkMsg) {
		try {
			GSCSelfODTNozzleInfo nozInfo = nozzleHash.get(nozzleNo) ;
			nozInfo.setGaWorkingMsg(gaWorkMsg) ;
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
		}
	}
	
}
