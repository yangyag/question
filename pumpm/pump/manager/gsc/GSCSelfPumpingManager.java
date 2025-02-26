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
	 * 	selfodt.fullpumping.option �� �ǹ̴� �Ʒ��� ����. �� Class �� GSC ���������� ���ȴ�.
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
	 * nozzleHash ���� �����ϰ� �ִ� ODTNozzleInfo Object �� ������ ��� ���� �����ȴ�.
	 * 	1. GSC �������� ���� ���� ��û�� �����ȴ�.
	 * 	2. POS �� ���� Preset �� ��쿡�� ���� ������ �ȴ�.
	 * �׸��� ������ ��� �����Ѵ�
	 * 	1. ���ο� KH ó����ȣ ������
	 * ODTNozzleInfo Object �� ���� ���� ���θ� �����ϰ� ������, ���� ������ ��� ���� �������� �����Ѵ�.
	 */
	public Hashtable<String, GSCSelfODTNozzleInfo> nozzleHash = null ;
	
	public Hashtable<String, String> nozzleNoHash = null ;
	
	public GSCSelfPumpingManager() {
		nozzleHash = new Hashtable<String, GSCSelfODTNozzleInfo>() ;
		nozzleNoHash = new Hashtable<String, String>() ;
	}
		
	/**
	 * ���� ������ �����Ѵ�. �� ���� �������� ���� �������� ��� ���ؼ� ���Ǿ� �� �� �ִ�.
	 * 
	 * @param nozzleNo		: ���� ��ȣ
	 * @param uPosMsg		: ���� UPOSMessage
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
	 * GSC ������ ��� POS �� ���� Preset �� ���۵ȴ�. �� ��� ������ �ڷḦ �����ϰ� ���Ӱ� �� �������� ���� ������ �ƴ���
	 * ǥ���Ѵ�. ��ź �ŵ��� �������� ��� �Ʒ��� ���� ������ �߻��Ͽ���.
	 * 		1. ������-A : �մ� A �� ���� �����Ͽ� �����Ϸ�
	 * 		2  ������-B : �մ� B �� POS ���� ����/���� ���� �Ͽ� ���� �Ϸ�
	 * 			�̶� �Ʒ� �ڵ尡 ������ ��� ������-B �� �Ϸ�Ǿ����� �մ� A �� ī��� ���簡 ����Ǿ���.
	 * 
	 * @param nozzleNo	: ���� ��ȣ
	 * @param isFull	: ���� ���� ����
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
	 * ����� ���� ������ ����� ���ٹ�ȣ�� Hashtable�� �����Ѵ�.
	 */
	public void createODTNozzleNo(String trx_proper_no, String nozzleNo) {
		nozzleNoHash.put(trx_proper_no, nozzleNo) ; // key : �ŷ����� ��ȣ, value : ���ٹ�ȣ
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
	 * Hashtable�� ������ ���������� �����´�
	 * @param trx_proper_no
	 * @return
	 */
	public String getGSCODTNozzleNo(String trx_proper_no) {
		return nozzleNoHash.get(trx_proper_no) ;
	}
	
	/**
	 * �����Ϸ� ���θ� �����Ѵ�.
	 * 
	 * @param nozzleNo	: ���� ��ȣ
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
	 * GSC ������ KH ó����ȣ ���� ������ �� �Լ��� ȣ���Ͽ� ���� ������ �����ϰ� �����ϵ��� �Ѵ�.
	 * 
	 * @param nozzleNo		: ���� ��ȣ
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
	 * �����Ϸ� ���θ� �����Ѵ�.
	 * 
	 * @param nozzleNo 		: ���� ��ȣ
	 * @param isCompleted	: �����Ϸ� ����
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
	 * ���� ��û ������ �����Ѵ�.
	 * 
	 * @param nozzleNo		: ���� ��ȣ
	 * @param gaWorkMsg		: ���� ��û ���� .GA WorkingMessage
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
