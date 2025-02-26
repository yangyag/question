package com.gsc.kixxhub.module.pumpm.pump.data.odt;

import java.util.ArrayList;

import com.gsc.kixxhub.common.data.IUPOSConstant;
import com.gsc.kixxhub.common.data.upos.UPOSMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;

/**
 * �� �������� ���� ���� ������ �����Ѵ�. �� �������� �ʱ�ȭ�� TR Ȥ�� SH (�ǸſϷ�) ������ ������� ���� ���Ž� �ʱ�ȭ �ȴ�.
 * 
 * @author WooChul Jung
 *
 */
public class PumpMSaleContent {

	private String currKhKey = null ;
	private String lastPayment_yn = IUPOSConstant.LASTPAYMENT_YN_NO ;
	private ArrayList<UPOSMessage> uPosMsgArray = new ArrayList<UPOSMessage>() ;

	/**
	 * UPOSMessage ������ �����Ѵ�.
	 * ���� KH ó����ȣ�� ������ �ִٰ� �ϸ�, �̴� ���� ������� �����ϰ�, ���� ������ �߰� �����Ѵ�.
	 * ������ KH ó����ȣ�� �ٸ� ���, ����� ���� ������ �����, ���� �����Ѵ�.
	 * 
	 * @param khKey		: KH ó����ȣ
	 * @param uPosMsg	: UPOSMessage ����
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
	 * KH ó����ȣ�� ��ȯ�Ѵ�.
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
	 * �� KH ó����ȣ���� ������ UPOSMessage ������ ��û�Ѵ�.
	 * @param khKey		: KH ó����ȣ
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
	 * KH ó����ȣ�� ���õ� ��� UPOSMessage ������ ��ȯ�Ѵ�.
	 * 
	 * @param khKey	: KH ó����ȣ
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
	 * �� KH ó����ȣ���� ������ Message Type �� ������ �ִ� UPOSMessage �� ��û�Ѵ�.
	 * 
	 * @param khKey			: KH ó����ȣ 
	 * @param messageType	: UPOSMessage �� message type
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

	// 2009.01.11 �Ҹ� ����, ��ȭ ������ ���� ���� ���� ���� �߰� (����ö)
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
						LogUtility.getPumpMLogger().debug("[Pump M] ���� ���� ������ Matching �Ǵ� ���� �����ϴ�.preMessage. preMessageType=" + 
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
	 * Instance �� �����ϱ� ���ؼ� �ʱ�ȭ �Ѵ�.
	 *
	 */
	public void init() {
		currKhKey = null ;
		lastPayment_yn = IUPOSConstant.LASTPAYMENT_YN_NO ;
		uPosMsgArray.clear() ;
	}	

	/**
	 * ������ KH ó����ȣ�� �������� ���θ� Ȯ���Ѵ�.
	 * 
	 * @param nextKhKey	: KH ó�� ��ȣ
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
