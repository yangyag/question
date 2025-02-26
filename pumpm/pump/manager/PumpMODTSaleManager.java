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
	 * KI 전문 송신 여부를 관리한다.
	 */
	public static Hashtable<String, String> kiManager = new Hashtable<String, String>() ;
	
	/**
	 * 2016-08-16 twlee 
	 * PI2 통합전문 변경 후 filler1값을 selfPayment_Type로 대체하면서
	 * 취소승인요청 전문 스펙과 timeout시 kixxHub에서 응답전문 구성할때  
	 * selfPayment_Type이 빠져있어 이 값을 채우기 위해서 사용되는 hashTable
	 */
	public static Hashtable<String, String> selfPayment_typeHash = new Hashtable<String, String>() ;
	
	/**
	 * 이 주유건의 승인 거절 여부를 확인한다.
	 * 
	 * @param nozzle_no		: 노즐 번호
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
	 * uposHash : 승인 응답 UPOSMessage
	 * custHash : '거래처 관련 POS 로 부터 응답 받은 전문' AND '충전기 및 Self ODT 에서 요청한 전문' 
	 * 		위 두 변수는 충전기 및 Self ODT 에서 사용되는 변수이다.
	 * 		이 두 변수는 TR 혹은 SH (판매완료) 전문 수신시 초기화 된다.
	 * 
	 * chargingPerson : 충전원 정보
	 * 		이 변수는 최초 충전기로 부터 요청에 의한 응답을 전송하면서 저장되는 변수이다. 이는 POS 로 결제 관련 전문을 전송시 사용된다.
	 */
	public static Hashtable<String, PumpMSaleContent> uposHash = new Hashtable<String, PumpMSaleContent>() ;
	
	/**
	 * 현 노즐의 전문을 임시 저장하도록 한다. 이는 취소를 위해서 사용되어 진다. 
	 * 셀프 주유기이면서 POS 로 부터 정액/정량 설정일 경우 uPosMsg 는 null 로 설정한다.
	 * 
	 * @param nozzle_no	: 노즐 번호
	 * @param uPosMsg	: 응답 전문
	 */
	public static void addUPOSMessage(String nozzle_no , UPOSMessage uPosMsg) {
		if (GlobalUtility.isNullOrEmptyString(nozzle_no)) return ;
		
		LogUtility.getPumpMLogger().debug("[Pump M] Store UPOSMessage. nozzle_no=" + nozzle_no) ;
		PumpMSaleContent saleContent = uposHash.get(nozzle_no) ;		
		if (saleContent == null) 
			saleContent = new PumpMSaleContent() ;
		
		saleContent.addContent(uPosMsg.getPosReceipt_no(), uPosMsg) ;
		uposHash.put(nozzle_no, saleContent) ;
		// ODT 로 부터 결제가 시작이 되면 POS ReceiptNo 는 노즐의 KHTransaction ID 와 동일하다.
		
	}
	
	/**
	 * 이 주유건의 승인 거절 여부를 확인한다.
	 * 
	 * @param nozzle_no		: 노즐 번호
	 * @param khproc_no		: KH 처리번호
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
	 * 충전기에 할당되어진 충전원 ID 를 요청한다.
	 * 
	 * @param nozzleNo	: 노즐 번호
	 * @return	: 충전원 ID
	 */
	public static String getChargingPersonID(String nozzleNo) {
		String chargingPersonID = chargingPerson.get(nozzleNo) ;
		String chargerNozzleNo =  "CHARGER" + nozzleNo; 
		try {
			/**
			 * by 박종호
			 * 	메모리(chargingPerson hashTable) 에 노즐에 대한 충전원 ID 가 존재하지 않을 경우, T_KH_KEYS 테이블에 존재하는지 
			 * 	한번더 검색 한다. 만약 존재 할경우 그 값을 chargingPerson Hashtable 에 설정을 해준다.
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
	 * 현 주유건과 관련된 거래처 전문을 반환한다. 이 전문은 현 주유건 발생시 ODT 의 요청에 의해서 POS 로 부터 받은 거래처 관련 전문이다.
	 * 충전기와 셀프ODT 에서 사용된다.
	 * 
	 * @param nozzle_no	: 노즐 번호
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
	 * 현 주유건과 관련된 거래처 전문을 반환한다. 이 전문은 현 주유건 발생시 ODT 의 요청에 의해서 POS 로 부터 받은 거래처 관련 전문으로 만든 WORKING MESSAGE
	 * 충전기와 셀프ODT 에서 사용된다.
	 * 
	 * @param nozzle_no	: 노즐 번호
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
	 * 취소 요청을 위해 사용된다.
	 * 가령 Message Type 이 8033 인 경우 (새롭게 보낼 취소 요청전문), 기 승인된 전문은 0034 이며, 신용 카드 번호와 보너스 카드번호
	 * 그리고 Message Type (0034) 와 일치하는 가장 최근의 전문을 찾는다.
	 * 
	 * @param nozzle_no		: 노즐 번호
	 * @param khproc_no			: KH 처리번호
	 * @param messageType	: 취소 요청의 Message Type
	 * @param creditCard	: 카드 번호
	 * @param bonusCard		: 보너스 카드 번호
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
	 * 2009.01.11 소모 셀프, 동화 프라임 셀프 가득 주유 관련 추가 (정우철)
	 * 
	 * 2016.03.30 WooChul Jung
	 * 	요청에 대한 응답 전문의 경우
	 * 		카드번호 Masking 필드 : 
	 * 			신용카드, 국세청현금영수증 결제 유형일 경우 : "-" 가 붙어서 수신됨. (FDK VAN 에서 프린트 하기 쉽도록 수정해서 전달됨)
	 * 			보너스 사용 : 보낸 그대로 수신됨. (Loyalty 서버에서는 그대로 응답함)
	 * 		보너스 카드번호
	 * 			보너스 누적시 : 보낸 그대로 수신됨. (Loyalty 서버에서는 그대로 응답함)
	 * 	따라서 카드번호 Masking 필드를 이용하여 비교하는 것은 의미가 없음.
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
	 * 직전 응답 전문을 반환한다.
	 * 이 함수의 호출은 ODT 를 통한 결제가 있는지 여부를 판단할때 사용된다.
	 * 
	 * @param nozzle_no	: 노즐 번호
	 * @param khproc_no		: KH 처리 번호
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
	 * KH 처리 번호와 Message Type 이 일치하는 이전 UPOSMEssage 를 가져온다.
	 * 
	 * @param nozzle_no		: 노즐번호
	 * @param khproc_no			: KH 처리 번호
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
	 * 처리번호에 관련된 모든 UPOSMessage 를 가져온다.
	 * 
	 * @param nozzle_no	: 노즐번호
	 * @param khproc_no		: KH 처리번호
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
	 * PumpMODTSaleManager 에서 관리하고 있는 데이터들을 초기화 한다.
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
	 * 특정 노즐의 아래 두 Data 를 초기화 한다.
	 * 		1. UPOSMessage 를 관리하고 있는 PumpMSaleContent
	 * 		2. 현 주유건과 관계된 거래처 정보를 관리하고 있는 PumpMSaleCustContent
	 * 
	 * 이 함수는 충전기와 셀프ODT 와 관계가 있다. 그리고 이 함수의 호출은 TR 혹은 SH (판매완료) 전문을 Pump A 로 부터
	 * 수신시 호출된다. 다쓰노 셀프의 경우 POS 로 부터 ODT 로 부터 결제 요청시 호출되기도 한다.
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
	 * 충전기의 경우 승인 요청/응답 전문에 각 노즐에 할당된 충전원 ID 를 포함시켜야 한다. 이 경우 이 함수를 호출하여
	 * 노즐과 충전원 ID 를 저장한다.
	 * 
	 * @param nozzleNo	: 노즐 번호
	 * @param chargingPersonID	: 충전원 ID
	 */
	public static void setChargingPerson(String nozzleNo, String chargingPersonID) {
		LogUtility.getPumpMLogger().debug("[Pump M] nozzle_no=" + nozzleNo + "#chargingPersonID=" + chargingPersonID) ;
		try {
			/**
			 * by 박종호
			 * 	T_KH_KEYS 테이블에 (ODT_Person_{노즐번호} , {충전원ID}) 를 입력한다.
			 * 	만약 Key 값이 존재할 경우, Update 를 하고, 존재하지 않을 경우는 새로 Insert 를 진행한다.
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
	 * GSC셀프의 거래처 관련 전문을 향후를 위해서 저장한다. - 2016.03.23
	 * 여전법 대응에 따라 민감한 신용정보(요청전문)를 보유 할 수 없어 워킹메시지를 제외한 거래처 전문만 저장하도록 메소드 추가 
     *
	 * @param nozzle_no	: 노즐 번호
	 * @param posMsg	: POS 전문 , POS 로 부터 요청에 의한 응답 전문(KixxHub 내에서 자체 구성한 응답 전문일 수도 있다.)
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
	 * 충전기와 소모셀프의 거래처 관련 전문을 향후를 위해서 저장한다.
	 * 
	 * @param nozzle_no	: 노즐 번호
	 * @param workMsg	: Pump A 전문 , ODT 로 부터 요청받은 전문
	 * @param posMsg	: POS 전문 , POS 로 부터 요청에 의한 응답 전문(KixxHub 내에서 자체 구성한 응답 전문일 수도 있다.)
	 */
	public static void setCustInfo(String nozzle_no , WorkingMessage workMsg, POSHeader posMsg) {
		LogUtility.getPumpMLogger().debug("[Pump M] Store Content related with Customer processing nozzle_no=" + nozzle_no) ;
		
		if ( posMsg != null) {
			if(GlobalUtility.isNullOrEmptyString(((POS_DW)posMsg).getCust_code())) {
				// 2012.10.16 ksm 거래처 카드번호에 신용카드가 들어오면 문제됨.
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
	 * 마지막 결제 여부인 0001 일 보낼지 판단을 한다.
	 * 각 ODT 별 (충전기, 소모셀프 , 다쓰노 셀프) 그 기준이 틀리다.
	 * 
	 * @param nozzle_no		: 노즐 번호
	 * @param khproc_no			: KH 처리 번호
	 * @return
	 */
	public static boolean shouldSend0001UPOSMessageToSaleM(String nozzle_no, String khproc_no, WorkingMessage workMsg) {
		return ODTUtility_Common.shouldSend0001UPOSMessageToSaleM(nozzle_no, khproc_no, workMsg) ;
	}
	
	
	
	/**
	 * UPOSMessage 가 가지고 있는 신용카드번호와 보너스 카드번호가 일치하는지 여부를 조사한다.
	 * 
	 * @param preAcceptedUPosMsg	: UPOSMessage
	 * @param srcCreditCard			: 신용카드 번호
	 * @param srcBonusCard			: 보너스카드 번호
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
