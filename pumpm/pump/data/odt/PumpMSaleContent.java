package com.gsc.kixxhub.module.pumpm.pump.data.odt;

import java.util.ArrayList;

import com.gsc.kixxhub.common.data.IUPOSConstant;
import com.gsc.kixxhub.common.data.upos.UPOSMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;

/**
 * 현 주유건의 승인 응답 전문을 관리한다. 이 데이터의 초기화는 TR 혹은 SH (판매완료) 전문을 주유기로 부터 수신시 초기화 된다.
 * 
 * @author WooChul Jung
 *
 */
public class PumpMSaleContent {

	private String currKhKey = null ;
	private String lastPayment_yn = IUPOSConstant.LASTPAYMENT_YN_NO ;
	private ArrayList<UPOSMessage> uPosMsgArray = new ArrayList<UPOSMessage>() ;

	/**
	 * UPOSMessage 전문을 저장한다.
	 * 같은 KH 처리번호를 가지고 있다고 하면, 이는 복수 결제라고 가정하고, 응답 전문을 추가 저장한다.
	 * 하지만 KH 처리번호가 다른 경우, 저장된 응답 전문을 지우고, 새로 저장한다.
	 * 
	 * @param khKey		: KH 처리번호
	 * @param uPosMsg	: UPOSMessage 전문
	 */
	public synchronized void addContent(String khKey , UPOSMessage uPosMsg) {
		if (!isSame(khKey)) {
			String nozzleNo = null ;
			if (uPosMsg != null) {
				nozzleNo = uPosMsg.getNozzle_no() ;
				LogUtility.getPumpMLogger().info("[Pump M] Init PumpMSaleContent. nozID=" + nozzleNo) ;
			}
			init() ;
		}
		this.currKhKey = khKey ;
		uPosMsgArray.add(uPosMsg) ;
		setLastPayment_yn(uPosMsg.getLastPayment_yn()) ;
		print() ;
	}

	/**
	 * KH 처리번호를 반환한다.
	 * 
	 * @return
	 */
	public String getCurrKhKey() {
		return currKhKey;
	}

	public String getLastPayment_yn() {
		return lastPayment_yn;
	}
	
	/**
	 * 현 KH 처리번호에서 마지막 UPOSMessage 전문을 요청한다.
	 * @param khKey		: KH 처리번호
	 * @return
	 */
	public UPOSMessage getLastUPOSMessage(String khKey) {
		UPOSMessage rltValue = null ;
		
		if (isSame(khKey) && (uPosMsgArray.size() != 0)) {
			rltValue = uPosMsgArray.get(uPosMsgArray.size()-1) ;
		}		
		return rltValue ;	
	}

	/**
	 * KH 처리번호와 관련된 모든 UPOSMessage 전문을 반환한다.
	 * 
	 * @param khKey	: KH 처리번호
	 * @return
	 */
	public ArrayList<UPOSMessage> getUPOSMessageArray(String khKey) {
		if (isSame(khKey) && (uPosMsgArray.size() != 0)) {
			 return uPosMsgArray ;
		} else {
			return null ;
		}
	}

	/**
	 * 현 KH 처리번호에서 동일한 Message Type 을 가지고 있는 UPOSMessage 를 요청한다.
	 * 
	 * @param khKey			: KH 처리번호 
	 * @param messageType	: UPOSMessage 의 message type
	 * @return
	 */
	public synchronized UPOSMessage getUPOSMessageWithSameMessageType(String khKey, String messageType) {
		UPOSMessage rltValue = null ;
		
		if (isSame(khKey) || (uPosMsgArray.size() != 0)) {
			for (int i = (uPosMsgArray.size()-1) ; i >= 0 ; i--) {
				 String preMessageType = uPosMsgArray.get(i).getMessageType() ;
				 if (preMessageType.equals(messageType)) {
					 rltValue = uPosMsgArray.get(i) ;
					 break ;
				 }
			}
		}
		return rltValue ;		
	}

	// 2009.01.11 소모 셀프, 동화 프라임 셀프 가득 주유 관련 추가 (정우철)
	public synchronized UPOSMessage getUPOSMessageWithSameMessageType(String khKey, 
			String messageType,String credit_authNo, String bonus_authNo) {
			UPOSMessage rltValue = null ;
			if (credit_authNo == null) credit_authNo = "" ;
			if (bonus_authNo == null) bonus_authNo = "" ;

			if (isSame(khKey) || (uPosMsgArray.size() != 0)) {
				for (int i = (uPosMsgArray.size()-1) ; i >= 0 ; i--) {
					String preMessageType = uPosMsgArray.get(i).getMessageType() ;
					String preCredit_authNo = uPosMsgArray.get(i).getCredit_auth_no() ;
					String preBonus_authNo = uPosMsgArray.get(i).getBonRSCard_authNo() ;
					
					if (IUPOSConstant.MESSAGETYPE_0062.equals(preMessageType))
						bonus_authNo = credit_authNo.trim();
					
					if ((preMessageType.equals(messageType)) && 
							credit_authNo.trim().equals(preCredit_authNo.trim()) && 
							bonus_authNo.trim().equals(preBonus_authNo.trim())){
						rltValue = uPosMsgArray.get(i) ;
						break ;
					} else {
						LogUtility.getPumpMLogger().debug("[Pump M] 이전 결제 전문과 Matching 되는 건이 없습니다.preMessage. preMessageType=" + 
								preMessageType +
								"#preCredit_authNo=" + preCredit_authNo.trim() +
								"#preBonus_authNo=" + preBonus_authNo.trim() +
								"##nowMessage. messageType=" + messageType +
								"#credit_authNo=" + credit_authNo.trim() +
								"#bonus_authNo=" + bonus_authNo.trim());
					}
				}
			}
			return rltValue ; 
	}		

	/**
	 * Instance 를 재사용하기 위해서 초기화 한다.
	 *
	 */
	public void init() {
		currKhKey = null ;
		lastPayment_yn = IUPOSConstant.LASTPAYMENT_YN_NO ;
		uPosMsgArray.clear() ;
	}	

	/**
	 * 전문의 KH 처리번호가 동일한지 여부를 확인한다.
	 * 
	 * @param nextKhKey	: KH 처리 번호
	 * @return
	 */
	public boolean isSame(String nextKhKey) {
		if (currKhKey == null) return false ;
		if (currKhKey.equals(nextKhKey)) return true ;
		else return false ;
	}

	public void print() {
		LogUtility.getPumpMLogger().debug("[PumpMSaleContent]  currKhKey=" + currKhKey + "#lastPayment_yn=" + lastPayment_yn) ;
		if ((uPosMsgArray != null) && (uPosMsgArray.size() > 0)) {
			for (int i = 0 ; i < uPosMsgArray.size() ; i++) {
				LogUtility.getPumpMLogger().debug("messageType=" + uPosMsgArray.get(i).getMessageType() +
						"#posReceipt_no=" + uPosMsgArray.get(i).getPosReceipt_no() +
						"#nozzleNo=" + uPosMsgArray.get(i).getNozzle_no() +
						"#LED_CODE=" + uPosMsgArray.get(i).getLed_code());
			}
		}
		LogUtility.getPumpMLogger().debug("[PumpMSaleContent]  END") ;
	}	
	
	
	
	public void setLastPayment_yn(String lastPayment_yn) {
		this.lastPayment_yn = lastPayment_yn;
	}

}
