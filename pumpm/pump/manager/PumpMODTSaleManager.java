package com.gsc.kixxhub.module.pumpm.pump.manager;

import java.util.ArrayList;
import java.util.Hashtable;

import com.gsc.kixxhub.common.data.IUPOSConstant;
import com.gsc.kixxhub.common.data.posdata.POSHeader;
import com.gsc.kixxhub.common.data.posdata.POS_DW;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.data.upos.UPOSMessage;
import com.gsc.kixxhub.common.data.upos.UPOSMessageUtility;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_KEYSHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_KEYSData;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.module.pumpm.pump.data.odt.PumpMSaleContent;
import com.gsc.kixxhub.module.pumpm.pump.data.odt.PumpMSaleCustContent;
import com.gsc.kixxhub.module.pumpm.pump.util.ODTUtility_Common;
import com.gsc.kixxhub.module.pumpm.pump.util.PumpMUtil;

public class PumpMODTSaleManager {

	public static Hashtable<String, String> chargingPerson = new Hashtable<String, String>() ;
	public static Hashtable<String, PumpMSaleCustContent> custHash = new Hashtable<String, PumpMSaleCustContent>() ;
	/**
	 * KI ���� �۽� ���θ� �����Ѵ�.
	 */
	public static Hashtable<String, String> kiManager = new Hashtable<String, String>() ;
	
	/**
	 * 2016-08-16 twlee 
	 * PI2 �������� ���� �� filler1���� selfPayment_Type�� ��ü�ϸ鼭
	 * ��ҽ��ο�û ���� ����� timeout�� kixxHub���� �������� �����Ҷ�  
	 * selfPayment_Type�� �����־� �� ���� ä��� ���ؼ� ���Ǵ� hashTable
	 */
	public static Hashtable<String, String> selfPayment_typeHash = new Hashtable<String, String>() ;
	
	/**
	 * �� �������� ���� ���� ���θ� Ȯ���Ѵ�.
	 * 
	 * @param nozzle_no		: ���� ��ȣ
	 * @return
	 */
	public static String getNozzleBySelfPayment_type(String nozzle_no) {
		String tempSelfPayment_type = "";
		try {
			tempSelfPayment_type = selfPayment_typeHash.get(nozzle_no) ;
			
		} catch (Exception e) {
			LogUtility.getLogger().error(e.getMessage(), e) ;
		}
		
		return tempSelfPayment_type ;
	}
	
	/**
	 * 
	 * @param nozzle_no
	 * @param tempSelfPayment_type
	 */
	public static void setNozzleBySelfPayment_type(String nozzle_no, String tempSelfPayment_type) {
		try {
			selfPayment_typeHash.put(nozzle_no, tempSelfPayment_type) ;
		} catch (Exception e) {
			LogUtility.getLogger().error(e.getMessage(), e) ;
		}
	}
	
	/**
	 * uposHash : ���� ���� UPOSMessage
	 * custHash : '�ŷ�ó ���� POS �� ���� ���� ���� ����' AND '������ �� Self ODT ���� ��û�� ����' 
	 * 		�� �� ������ ������ �� Self ODT ���� ���Ǵ� �����̴�.
	 * 		�� �� ������ TR Ȥ�� SH (�ǸſϷ�) ���� ���Ž� �ʱ�ȭ �ȴ�.
	 * 
	 * chargingPerson : ������ ����
	 * 		�� ������ ���� ������� ���� ��û�� ���� ������ �����ϸ鼭 ����Ǵ� �����̴�. �̴� POS �� ���� ���� ������ ���۽� ���ȴ�.
	 */
	public static Hashtable<String, PumpMSaleContent> uposHash = new Hashtable<String, PumpMSaleContent>() ;
	
	/**
	 * �� ������ ������ �ӽ� �����ϵ��� �Ѵ�. �̴� ��Ҹ� ���ؼ� ���Ǿ� ����. 
	 * ���� �������̸鼭 POS �� ���� ����/���� ������ ��� uPosMsg �� null �� �����Ѵ�.
	 * 
	 * @param nozzle_no	: ���� ��ȣ
	 * @param uPosMsg	: ���� ����
	 */
	public static void addUPOSMessage(String nozzle_no , UPOSMessage uPosMsg) {
		if (GlobalUtility.isNullOrEmptyString(nozzle_no)) return ;
		
		LogUtility.getPumpMLogger().debug("[Pump M] Store UPOSMessage. nozzle_no=" + nozzle_no) ;
		PumpMSaleContent saleContent = uposHash.get(nozzle_no) ;		
		if (saleContent == null) 
			saleContent = new PumpMSaleContent() ;
		
		saleContent.addContent(uPosMsg.getPosReceipt_no(), uPosMsg) ;
		uposHash.put(nozzle_no, saleContent) ;
		// ODT �� ���� ������ ������ �Ǹ� POS ReceiptNo �� ������ KHTransaction ID �� �����ϴ�.
		
	}
	
	/**
	 * �� �������� ���� ���� ���θ� Ȯ���Ѵ�.
	 * 
	 * @param nozzle_no		: ���� ��ȣ
	 * @param khproc_no		: KH ó����ȣ
	 * @return
	 */
	public static boolean containKiManager(String nozzle_no, String khproc_no) {
		boolean rlt = false ;
		try {
			String orgKHProc_no = kiManager.get(nozzle_no) ;
			if ((khproc_no != null) && (khproc_no.equals(orgKHProc_no))){
				rlt = true ;
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
			rlt = false ;
		}
		
		return rlt ;
	}

	public static void destroy() {
		if (uposHash != null) {
			uposHash.clear() ;
			uposHash = null ;
		} 
		if (custHash != null) {
			custHash.clear() ;
			custHash = null ;
		} 	
		if (chargingPerson != null) {
			chargingPerson.clear() ;
			chargingPerson = null ;
		} 	
		if (kiManager != null) {
			kiManager.clear() ;
			kiManager = null ;
		}
		if (selfPayment_typeHash != null) {
			selfPayment_typeHash.clear() ;
			selfPayment_typeHash = null ;
		} 	
		
	}
	
	/**
	 * �����⿡ �Ҵ�Ǿ��� ������ ID �� ��û�Ѵ�.
	 * 
	 * @param nozzleNo	: ���� ��ȣ
	 * @return	: ������ ID
	 */
	public static String getChargingPersonID(String nozzleNo) {
		String chargingPersonID = chargingPerson.get(nozzleNo) ;
		String chargerNozzleNo =  "CHARGER" + nozzleNo; 
		try {
			/**
			 * by ����ȣ
			 * 	�޸�(chargingPerson hashTable) �� ���� ���� ������ ID �� �������� ���� ���, T_KH_KEYS ���̺� �����ϴ��� 
			 * 	�ѹ��� �˻� �Ѵ�. ���� ���� �Ұ�� �� ���� chargingPerson Hashtable �� ������ ���ش�.
			 * 
			 */
			if (GlobalUtility.isNullOrEmptyString(chargingPersonID)) {
				T_KH_KEYSData keysData = T_KH_KEYSHandler.getHandler().getT_KH_KEYSData(chargerNozzleNo);
				
				if (keysData != null)
				{
					chargingPersonID =  keysData.getValue();
					chargingPerson.put(chargerNozzleNo, chargingPersonID) ;
				}
			}
			
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
		}
		LogUtility.getPumpMLogger().debug("[Pump M] nozzle_no=" + nozzleNo + "#chargingPersonID=" + chargingPersonID) ;
		return chargingPersonID ;
	}
	
	/**
	 * �� �����ǰ� ���õ� �ŷ�ó ������ ��ȯ�Ѵ�. �� ������ �� ������ �߻��� ODT �� ��û�� ���ؼ� POS �� ���� ���� �ŷ�ó ���� �����̴�.
	 * ������� ����ODT ���� ���ȴ�.
	 * 
	 * @param nozzle_no	: ���� ��ȣ
	 * @return
	 */
	public static POSHeader getCustPOSPumpM(String nozzle_no) {
		POSHeader posMsg = null ;
		
		PumpMSaleCustContent custContent = custHash.get(nozzle_no) ;		
		if (custContent != null) {
			posMsg = custContent.getPosPumpM() ;
		}		
		return posMsg ;
	}
	
	/**
	 * �� �����ǰ� ���õ� �ŷ�ó ������ ��ȯ�Ѵ�. �� ������ �� ������ �߻��� ODT �� ��û�� ���ؼ� POS �� ���� ���� �ŷ�ó ���� �������� ���� WORKING MESSAGE
	 * ������� ����ODT ���� ���ȴ�.
	 * 
	 * @param nozzle_no	: ���� ��ȣ
	 * @return
	 */
	public static WorkingMessage getCustPumpAPumpM(String nozzle_no) {
		WorkingMessage workMsg = null ;
		
		PumpMSaleCustContent custContent = custHash.get(nozzle_no) ;		
		if (custContent != null) {
			workMsg = custContent.getWorkMsg() ;
		}		
		return workMsg ;
	}
	
	public static void setCardNo(String nozzle_no, String cardNo) {
		try {			
			PumpMSaleCustContent custContent = custHash.get(nozzle_no) ;		
			if (custContent != null) {
				custContent.setCardNo(cardNo) ;
			}	
			
		} catch (Exception e) {
			LogUtility.getLogger().error(e.getMessage(),e) ;
		}
	}
	
	public static String getCardNo(String nozzle_no) {
		String cardNo = "" ;
		try {
			PumpMSaleCustContent custContent = custHash.get(nozzle_no) ;		
			if (custContent != null) {
				cardNo = custContent.getCardNo() ;
			}	
		} catch (Exception e) {
			LogUtility.getLogger().error(e.getMessage(),e) ;
		}
		return cardNo ;
	}
	
	public static void setPromptDiscount_yn(String nozzle_no, String promptDiscount_yn) {
		try {
			
			PumpMSaleCustContent custContent = custHash.get(nozzle_no) ;		
			if (custContent != null) {
				custContent.setPromptDiscount_yn(promptDiscount_yn) ;
			}	
			
		} catch (Exception e) {
			LogUtility.getLogger().error(e.getMessage(),e) ;
		}
	}

	public static String getPromptDiscount_yn(String nozzle_no) {
		String promptDiscount_yn = "" ;
		try {
			PumpMSaleCustContent custContent = custHash.get(nozzle_no) ;		
			if (custContent != null) {
				promptDiscount_yn = custContent.getPromptDiscount_yn() ;
			}	
		} catch (Exception e) {
			LogUtility.getLogger().error(e.getMessage(),e) ;
		}
		return promptDiscount_yn ;
	}
	
	/**
	 * ��� ��û�� ���� ���ȴ�.
	 * ���� Message Type �� 8033 �� ��� (���Ӱ� ���� ��� ��û����), �� ���ε� ������ 0034 �̸�, �ſ� ī�� ��ȣ�� ���ʽ� ī���ȣ
	 * �׸��� Message Type (0034) �� ��ġ�ϴ� ���� �ֱ��� ������ ã�´�.
	 * 
	 * @param nozzle_no		: ���� ��ȣ
	 * @param khproc_no			: KH ó����ȣ
	 * @param messageType	: ��� ��û�� Message Type
	 * @param creditCard	: ī�� ��ȣ
	 * @param bonusCard		: ���ʽ� ī�� ��ȣ
	 * @return
	 */
	public static UPOSMessage getPreAcceptedUPOSMessageForCancel(String nozzle_no, 
										String khproc_no, 
										String messageType,
										String creditCard,
										String bonusCard) {
		LogUtility.getPumpMLogger().debug("[Pump M] Find Latest Responded UPOSMessage From CAT M.") ;
		String pairMsgType = UPOSMessageUtility.getPairApprovalRespondMessageType(messageType) ;
		UPOSMessage preAcceptedUPOSMsg = getPreviousUPOSMessage(nozzle_no, khproc_no, pairMsgType) ;
		if (preAcceptedUPOSMsg == null) return null ;
		else {
			boolean rlt = true ;
			rlt = validate(preAcceptedUPOSMsg, creditCard, bonusCard) ;
			if (!rlt) return null ;
		}
		if (preAcceptedUPOSMsg != null) {
			LogUtility.getPumpMLogger().debug("[Pump M] Find Latest UPOSMessage") ;
		} else {
			LogUtility.getPumpMLogger().error("[Pump M] No Latest UPOSMessage") ;
		}
		return preAcceptedUPOSMsg ;
	}
	
	/**
	 * 2009.01.11 �Ҹ� ����, ��ȭ ������ ���� ���� ���� ���� �߰� (����ö)
	 * 
	 * 2016.03.30 WooChul Jung
	 * 	��û�� ���� ���� ������ ���
	 * 		ī���ȣ Masking �ʵ� : 
	 * 			�ſ�ī��, ����û���ݿ����� ���� ������ ��� : "-" �� �پ ���ŵ�. (FDK VAN ���� ����Ʈ �ϱ� ������ �����ؼ� ���޵�)
	 * 			���ʽ� ��� : ���� �״�� ���ŵ�. (Loyalty ���������� �״�� ������)
	 * 		���ʽ� ī���ȣ
	 * 			���ʽ� ������ : ���� �״�� ���ŵ�. (Loyalty ���������� �״�� ������)
	 * 	���� ī���ȣ Masking �ʵ带 �̿��Ͽ� ���ϴ� ���� �ǹ̰� ����.
	 * 
	 */
	public static UPOSMessage getPreAcceptedUPOSMessageForCancel(String nozzle_no, 
			String khproc_no, 
			String messageType,
			String creditCard,
			String bonusCard,
			String credit_authNo,
			String bonus_authNo) {
		LogUtility.getPumpMLogger().debug("[Pump M] Find Latest Responded UPOSMessage From CAT M.") ;
				
		String pairMsgType = UPOSMessageUtility.getPairApprovalRespondMessageType(messageType) ;
		UPOSMessage preAcceptedUPOSMsg = getPreviousUPOSMessage(nozzle_no, khproc_no, pairMsgType, credit_authNo, bonus_authNo) ;
		
		if (preAcceptedUPOSMsg != null) {
			LogUtility.getPumpMLogger().debug("[Pump M] Find Latest UPOSMessage") ;
		} else {
			LogUtility.getPumpMLogger().error("[Pump M] No Latest UPOSMessage") ;
		}
		
		if (preAcceptedUPOSMsg == null) {
			return null ;
		}
		else {
			boolean rlt = validate(preAcceptedUPOSMsg, creditCard, bonusCard) ;
			if (!rlt) {
				return null ;
			}
		}

		return preAcceptedUPOSMsg ;
	}
	
	/**
	 * ���� ���� ������ ��ȯ�Ѵ�.
	 * �� �Լ��� ȣ���� ODT �� ���� ������ �ִ��� ���θ� �Ǵ��Ҷ� ���ȴ�.
	 * 
	 * @param nozzle_no	: ���� ��ȣ
	 * @param khproc_no		: KH ó�� ��ȣ
	 * @return
	 */
	public static UPOSMessage getPreviousUPOSMessage(String nozzle_no, String khproc_no) {
		LogUtility.getPumpMLogger().debug("[Pump M] Find Latest UPOSMessage") ;
		UPOSMessage latestedRespondedUPOSMsg = null ;
		PumpMSaleContent saleContent = uposHash.get(nozzle_no) ;		
		if (saleContent != null) {
			latestedRespondedUPOSMsg = saleContent.getLastUPOSMessage(khproc_no) ;
		}
		if (latestedRespondedUPOSMsg != null) {
			LogUtility.getPumpMLogger().debug("[Pump M] Find Latest UPOSMessage") ;
		} else {
			LogUtility.getPumpMLogger().error("[Pump M] No Latest UPOSMessage") ;
		}
		return latestedRespondedUPOSMsg ;
	}
	
	
	/**
	 * KH ó�� ��ȣ�� Message Type �� ��ġ�ϴ� ���� UPOSMEssage �� �����´�.
	 * 
	 * @param nozzle_no		: �����ȣ
	 * @param khproc_no			: KH ó�� ��ȣ
	 * @param messageType	: Message Type
	 * @return
	 */
	private static UPOSMessage getPreviousUPOSMessage(String nozzle_no, String khproc_no, String messageType) {
		PumpMSaleContent saleContent = uposHash.get(nozzle_no) ;		
		if (saleContent == null) return null ;
		else {
			return saleContent.getUPOSMessageWithSameMessageType(khproc_no, messageType) ;
		}
	}
	
	private static UPOSMessage getPreviousUPOSMessage(String nozzle_no, 
			String khproc_no, String messageType,String credit_authNo, String bonus_authNo) {
		
		PumpMSaleContent saleContent = uposHash.get(nozzle_no) ; 
		if (saleContent == null) {		
			return null ;
		}
		else {
			return saleContent.getUPOSMessageWithSameMessageType(khproc_no, messageType, credit_authNo, bonus_authNo) ;
		}
	}
	
	/**
	 * ó����ȣ�� ���õ� ��� UPOSMessage �� �����´�.
	 * 
	 * @param nozzle_no	: �����ȣ
	 * @param khproc_no		: KH ó����ȣ
	 * @return
	 */
	public static ArrayList<UPOSMessage> getUPOSMessageArray(String nozzle_no, String khproc_no) {
		PumpMSaleContent saleContent = uposHash.get(nozzle_no) ;		
		if (saleContent == null) return null ;
		else {
			return saleContent.getUPOSMessageArray(khproc_no) ;
		}
	}
	
	/**
	 * PumpMODTSaleManager ���� �����ϰ� �ִ� �����͵��� �ʱ�ȭ �Ѵ�.
	 *
	 */
	public static void init() {
		if (uposHash != null) {
			uposHash.clear() ;
		} else {
			uposHash = new Hashtable<String, PumpMSaleContent>() ;
		}
		if (custHash != null) {
			custHash.clear() ;
		} else {
			custHash = new Hashtable<String, PumpMSaleCustContent>() ;
		}
		if (chargingPerson != null) {
			chargingPerson.clear() ;
		} else {
			chargingPerson = new Hashtable<String, String>() ;
		}
		if (kiManager != null) {
			kiManager.clear() ;
		} else {
			kiManager = new Hashtable<String, String>() ;
		}
		if (selfPayment_typeHash != null) {
			selfPayment_typeHash.clear() ;
		} else {
			selfPayment_typeHash = new Hashtable<String, String>() ;
		}
	}

	/**
	 * Ư�� ������ �Ʒ� �� Data �� �ʱ�ȭ �Ѵ�.
	 * 		1. UPOSMessage �� �����ϰ� �ִ� PumpMSaleContent
	 * 		2. �� �����ǰ� ����� �ŷ�ó ������ �����ϰ� �ִ� PumpMSaleCustContent
	 * 
	 * �� �Լ��� ������� ����ODT �� ���谡 �ִ�. �׸��� �� �Լ��� ȣ���� TR Ȥ�� SH (�ǸſϷ�) ������ Pump A �� ����
	 * ���Ž� ȣ��ȴ�. �پ��� ������ ��� POS �� ���� ODT �� ���� ���� ��û�� ȣ��Ǳ⵵ �Ѵ�.
	 * 
	 * @param nozzle_no
	 */
	public static void initSaleContent(String nozzle_no) {
		LogUtility.getPumpMLogger().debug("[Pump M] init ODTSaleContent & ODTCustContent :nozzle_no=" +nozzle_no ) ;
		PumpMSaleContent saleContent = uposHash.get(nozzle_no) ;		
		if (saleContent == null) {
			saleContent = new PumpMSaleContent() ;
			uposHash.put(nozzle_no, saleContent) ;
		} else {
			saleContent.init() ;
		}
		
		PumpMSaleCustContent saleCustContent = custHash.get(nozzle_no) ;		
		if (saleCustContent == null) {
			saleCustContent = new PumpMSaleCustContent() ;
			custHash.put(nozzle_no, saleCustContent) ;
		} else {
			saleCustContent.init() ;
		}
	}	
	
	/**
	 * 
	 * @param nozzle_no
	 * @param khproc_no
	 */
	public static void putInKiManager(String nozzle_no, String khproc_no) {
		try {
			kiManager.put(nozzle_no, khproc_no) ;
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
		}
	}
	
	/**
	 * �������� ��� ���� ��û/���� ������ �� ���� �Ҵ�� ������ ID �� ���Խ��Ѿ� �Ѵ�. �� ��� �� �Լ��� ȣ���Ͽ�
	 * ����� ������ ID �� �����Ѵ�.
	 * 
	 * @param nozzleNo	: ���� ��ȣ
	 * @param chargingPersonID	: ������ ID
	 */
	public static void setChargingPerson(String nozzleNo, String chargingPersonID) {
		LogUtility.getPumpMLogger().debug("[Pump M] nozzle_no=" + nozzleNo + "#chargingPersonID=" + chargingPersonID) ;
		try {
			/**
			 * by ����ȣ
			 * 	T_KH_KEYS ���̺� (ODT_Person_{�����ȣ} , {������ID}) �� �Է��Ѵ�.
			 * 	���� Key ���� ������ ���, Update �� �ϰ�, �������� ���� ���� ���� Insert �� �����Ѵ�.
			 * 
			 */
			String chargerNozzleNo =  "CHARGER" + nozzleNo; 
			T_KH_KEYSData keysData = T_KH_KEYSHandler.getHandler().getT_KH_KEYSData(chargerNozzleNo); 
			if (keysData == null)
			{
				T_KH_KEYSHandler.getHandler().insertT_KH_KEYSData(chargerNozzleNo, chargingPersonID);
			} else {
				
				T_KH_KEYSHandler.getHandler().updateT_KH_KEYSData(chargerNozzleNo, chargingPersonID);
			}
			
			chargingPerson.put(nozzleNo, chargingPersonID) ;
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
		}
	}
	
	/**
	 * GSC������ �ŷ�ó ���� ������ ���ĸ� ���ؼ� �����Ѵ�. - 2016.03.23
	 * ������ ������ ���� �ΰ��� �ſ�����(��û����)�� ���� �� �� ���� ��ŷ�޽����� ������ �ŷ�ó ������ �����ϵ��� �޼ҵ� �߰� 
     *
	 * @param nozzle_no	: ���� ��ȣ
	 * @param posMsg	: POS ���� , POS �� ���� ��û�� ���� ���� ����(KixxHub ������ ��ü ������ ���� ������ ���� �ִ�.)
	 */
	public static void setCustInfo(String nozzle_no , POSHeader posMsg) {
		LogUtility.getPumpMLogger().debug("[Pump M] Store Content related with Customer processing nozzle_no=" + nozzle_no) ;
		
		if ( posMsg != null) {
			if(GlobalUtility.isNullOrEmptyString(((POS_DW)posMsg).getCust_code())) {
				
				return;
			}
		}
		PumpMSaleCustContent custContent = custHash.get(nozzle_no) ;		
		if (custContent == null) {
			custContent = new PumpMSaleCustContent() ;
			custHash.put(nozzle_no, custContent) ;
		}
		
		custContent.setPosPumpM(posMsg) ;
	}

	/**
	 * ������� �Ҹ����� �ŷ�ó ���� ������ ���ĸ� ���ؼ� �����Ѵ�.
	 * 
	 * @param nozzle_no	: ���� ��ȣ
	 * @param workMsg	: Pump A ���� , ODT �� ���� ��û���� ����
	 * @param posMsg	: POS ���� , POS �� ���� ��û�� ���� ���� ����(KixxHub ������ ��ü ������ ���� ������ ���� �ִ�.)
	 */
	public static void setCustInfo(String nozzle_no , WorkingMessage workMsg, POSHeader posMsg) {
		LogUtility.getPumpMLogger().debug("[Pump M] Store Content related with Customer processing nozzle_no=" + nozzle_no) ;
		
		if ( posMsg != null) {
			if(GlobalUtility.isNullOrEmptyString(((POS_DW)posMsg).getCust_code())) {
				// 2012.10.16 ksm �ŷ�ó ī���ȣ�� �ſ�ī�尡 ������ ������.
				//LogUtility.getPumpMLogger().debug("[Pump M] Invalid Cust_card " + ((POSPumpM_DW)posMsg).getCust_card_no() + 
				//							" ### nozzle_no=" + nozzle_no) ;
				return;
			}
		}
		PumpMSaleCustContent custContent = custHash.get(nozzle_no) ;		
		if (custContent == null) {
			custContent = new PumpMSaleCustContent() ;
			custHash.put(nozzle_no, custContent) ;
		}
		
		custContent.setWorkMsg(workMsg) ;
		custContent.setPosPumpM(posMsg) ;
	}
	
	/**
	 * ������ ���� ������ 0001 �� ������ �Ǵ��� �Ѵ�.
	 * �� ODT �� (������, �Ҹ��� , �پ��� ����) �� ������ Ʋ����.
	 * 
	 * @param nozzle_no		: ���� ��ȣ
	 * @param khproc_no			: KH ó�� ��ȣ
	 * @return
	 */
	public static boolean shouldSend0001UPOSMessageToSaleM(String nozzle_no, String khproc_no, WorkingMessage workMsg) {
		return ODTUtility_Common.shouldSend0001UPOSMessageToSaleM(nozzle_no, khproc_no, workMsg) ;
	}
	
	
	
	/**
	 * UPOSMessage �� ������ �ִ� �ſ�ī���ȣ�� ���ʽ� ī���ȣ�� ��ġ�ϴ��� ���θ� �����Ѵ�.
	 * 
	 * @param preAcceptedUPosMsg	: UPOSMessage
	 * @param srcCreditCard			: �ſ�ī�� ��ȣ
	 * @param srcBonusCard			: ���ʽ�ī�� ��ȣ
	 * @return
	 */
	private static boolean validate(UPOSMessage preAcceptedUPosMsg, String srcCreditCard, String srcBonusCard) {
		boolean rlt = false ;
		String creditCard = PumpMUtil.getCardNumberPre16Length(srcCreditCard) ;
		String bonusCard = PumpMUtil.getCardNumberPre16Length(srcBonusCard) ;
		
		String preCreditCard = PumpMUtil.getCardNumberPre16Length(preAcceptedUPosMsg.getCreditCard_no()) ;
		String preBonusCard = PumpMUtil.getCardNumberPre16Length(preAcceptedUPosMsg.getBonRSCard_no()) ;
		
		if (IUPOSConstant.MESSAGETYPE_0062.equals(preAcceptedUPosMsg.getMessageType()))
			preCreditCard = "";
		
		if ((srcCreditCard != null) && !"".equals(srcCreditCard)) {
			if (creditCard.equals(preCreditCard) && bonusCard.equals(preBonusCard)) {
				rlt = true ;
			}
		} else {
			if (bonusCard.equals(preBonusCard)) {
				rlt = true ;
			}
		}
		return rlt ;
	}
}
