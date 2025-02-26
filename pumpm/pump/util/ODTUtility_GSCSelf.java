package com.gsc.kixxhub.module.pumpm.pump.util;

import java.util.ArrayList;

import com.gsc.kixxhub.common.data.IUPOSConstant;
import com.gsc.kixxhub.common.data.pump.GA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.GB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.GT_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.data.upos.CreateUPOSMessage;
import com.gsc.kixxhub.common.data.upos.UPOSMessage;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_CampInfo;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_ItemInfo;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.module.pumpm.pump.manager.PumpMTransactionManager;

/** 
 * ODTUtility_GSCSelf 신규추가 - CWI
 * 신규 ODT 연동을 위한 ODTUtility 추가
 */
public class ODTUtility_GSCSelf {
	
	// PI2, CWI, 2016-03-18 외상 결재 응답 전문
	public static UPOSMessage createUPOSMessageFromWorkingMessage_GSC_For_OISANG(WorkingMessage workingMsg, String khproc_no) {
		
		LogUtility.getPumpMLogger().info("[Pump M] Convert WorkingMessage to UPOSMessage for GSC") ;
		UPOSMessage uPosMsg = null ;
		GA_WorkingMessage gaWorkingMsg = (GA_WorkingMessage) workingMsg ;
		
        String tr_price  = gaWorkingMsg.getUnityMessage().getPayment_amt();                                                                                                                                                                           // 판매 금액
        String nozzle_no =   gaWorkingMsg.getConnectNozzleNo();
        String custCard_No = gaWorkingMsg.getCustCardNo();
        String ss_crStNum =  gaWorkingMsg.getUnityMessage().getSs_crStNum();
        String ss_carNum =   gaWorkingMsg.getUnityMessage().getSs_carNum();
        
//      아래 변수들은 외상응답 전문 구성시 내용을 채울 필요가 없는 정보 들임. 따라서 Default 값을 사용함.
        String bonRSCard_no = "" ;
        String bonRSCard_authNo = "" ;
        String trdate_bonRSCard = "" ;
        String bonRSCard_ID = "" ;
        String bonRSCRSt_nm  = "" ;
        String gs_point1 = "" ;
        String gs_point2 = "" ;
        String gs_point3 = "" ;
        String gs_point4 = "" ;
        String local_point = "" ;
        String local_occurPoint = "" ;
        String loyaltyReqCode = "" ;
        String title_msg = "" ;                                                              
        UPOSMessage_CampInfo camp_info = null ;
        String receipt_type = "" ;
        String loyality_id = "" ;
        String taxFreeCust_type = "" ;
        String supply_type = "" ;
        String deal_type = "" ;
        String loan_date = "" ;
        // 의미 없는 변수 - 끝
        
        String emp_no = "" ;                                                             // 충전원 ID (셀프 ODT 는 없음)
        String term_id = gaWorkingMsg.getUnityMessage().getTerm_id();                    // 단말기 번호
        String lastPayment_yn = "0" ;                                                    // 마지막 결제여부
        String led_code = "" ;                                                           // LED 코드
        String keepDoc_limitDate = "" ;                                                  // 보관증 만료일
        
        UPOSMessage_ItemInfo itemInfo = gaWorkingMsg.getUnityMessage().getItem_info();
        
        String payment_amt = tr_price ;
        uPosMsg = CreateUPOSMessage.createUPOSMessage_0082( IUPOSConstant.DEVICE_TYPE_3S,
																			   khproc_no,
																			   nozzle_no,
																			   emp_no,
																			   itemInfo,
																			   custCard_No,
																			   ss_crStNum,
																			   ss_carNum,
																			   bonRSCard_no,
																			   bonRSCard_authNo,
																			   trdate_bonRSCard,
																			   bonRSCard_ID,
																			   bonRSCRSt_nm,
																			   gs_point1,
																			   gs_point2,
																			   gs_point3,
																			   gs_point4,
																			   local_point,
																			   local_occurPoint,
																			   loyaltyReqCode,
																			   title_msg,
																			   camp_info,
																			   receipt_type,
																			   loyality_id,
																			   payment_amt,
																			   term_id,
																			   lastPayment_yn,
																			   led_code,
																			   taxFreeCust_type,
																			   supply_type,
																			   deal_type,
																			   loan_date,
																			   keepDoc_limitDate) ;
         
		return uPosMsg;
	}
	
	
	/**
	 * 응답 전문을 GSC 셀프에 전송하기 위해서 WorkineMessage Object 로 변환한다.
	 * 아래는 UPOSMessage 응답 전문의 Message Type 에 따른 전문 내용이다.
	 * 	신용카드 승인 응답	= 0032
	 * 	신용카드 승인 취소 응답 = 8032
	 * 	신용카드 + 보너스 승인 응답 = 0034
	 * 	신용카드 + 보너스 취소 응답 = 8034
	 * 	BL 체크 = 0072
	 * 
	 * @param uPosMsg	: UPOSMessage Object
	 * @return
	 */
	public static ArrayList<WorkingMessage> createWorkingMessageFromUPOSMessage_GSC(UPOSMessage uPosMsg) {
		LogUtility.getPumpMLogger().info("[Pump M] Convert UPOSMessage[" + uPosMsg.getMessageType() 
				+ "] to WorkingMessage for GSC.") ;

		ArrayList<WorkingMessage> workMsgArray = new ArrayList<WorkingMessage>() ;	
		
		// PI2, CWI, 2016-03-22, 넘어온 Upos 및 GB를 생성하는데 최소한의 데이터만을 사용하기에
		// 동일한 생산 로직으로 처리 한다.
		LogUtility.getPumpMLogger().info("[Pump M] Credit Response -> WorkingMessage") ;
		workMsgArray = getGB_WorkingMessage_GSC(uPosMsg) ;
		
		return workMsgArray ;
	}
	
	
	/**
	 * 승인 응답 전문을 받은 이후 주유기 Adapter 로 전송하기 위한 WorkingMessage Array 를 생성 요청한다.
	 * GSC 셀프 ODT 는 승인 응답 전문은 GB 전문이다. 
	 * 
	 * @param uPosMsg	: 승인 응답 전문
	 * @param isAccept	: 승인 or 취소승인 여부
	 * @return
	 */
	private static ArrayList<WorkingMessage> getGB_WorkingMessage_GSC(UPOSMessage uPosMsg) {
		ArrayList<WorkingMessage> workMsgArray = new ArrayList<WorkingMessage>() ;	

		GB_WorkingMessage gbWorkingMsg = null ;
		
		String odtNo = null ;
		String nozzle_no = uPosMsg.getNozzle_no() ;
		
		odtNo = PumpMUtil.getODTNumberFromNozzleNo(nozzle_no) ;	
		
		// PI2, 2016-03-31, CWI, 변경 된 GB전문 저장
		gbWorkingMsg = new GB_WorkingMessage() ;
		gbWorkingMsg.setMessageType(uPosMsg.getMessageType());
		gbWorkingMsg.setNozzleNo(odtNo);
		gbWorkingMsg.setConnectNozzleNo(nozzle_no);
		gbWorkingMsg.setUnityMessage(uPosMsg);
		
		workMsgArray.add(gbWorkingMsg) ;
		
		return workMsgArray;
	}

	/**
	 * 0001 전문 전송 여부를 설정한다.
	 * GSC 셀프의 경우 GT 전문을 수신하면 이 함수를 호출하여 0001 전문을 POS 로 전송할 지 결정한다.
	 * 보내는 경우는 다음과 같다.
	 * 		1. POS 로 부터 Preset 에 의한 주유가 아닐 경우
	 * 		2. 가득 주유가 아닌 경우
	 * 		3. ODT 에 의한 결제일 경우
	 * 
	 * @param nozzle_no	: 노즐 번호
	 * @param khproc_no	: KH 처리번호
	 * @param workMsg	: 판매 완료 전문 (GT_WorkingMessage)
	 * @return
	 */
	public static boolean shouldSend0001UPOSMessageToSaleM(String nozzle_no, String khproc_no, WorkingMessage workMsg) {
		boolean rlt = false ;
		
		// Debug
		LogUtility.getPumpMLogger().debug("[Pump M] shouldSend0001UPOSMessageToSaleM in GSC") ;
		PumpMTransactionManager.getInstance().printTransactionData(nozzle_no) ;
		
		if (PumpMTransactionManager.getInstance().isPresetFromPOS(nozzle_no)) {
			rlt = false ;
		} else {
			
			GT_WorkingMessage gtMessage = (GT_WorkingMessage) workMsg;
			
			// PI2, CWI, 2016-03-11 외상거래처의 경우 0원 주유일 시 pos에 0001전문을 전송되지 않도록 해야 되나,
			if(!gtMessage.getPrice().equals("")){
				rlt = true ;
				LogUtility.getPumpMLogger().debug("[Pump M] isPayedFromODT") ;
			} else{
				rlt = false ;
				LogUtility.getPumpMLogger().debug("[Pump M] isNotPayedFromODT") ;
			}
		}
		return rlt ;
	}
	
	
	/**
	 * UPOSMessage 응답 전문을 Sale M 으로 보낼지를 판단한다.
	 * 
	 * @param uPosMsg	: UPOSMessage 응답 전문
	 * @return
	 */
    public static boolean shouldSendToSaleM(UPOSMessage uPosMsg) {
    	boolean rlt = true ;
    	
    	// 2013.07.18 ksm 현금영수증 승인실패인 경우 POS로 전송 안함.
    	if(IUPOSConstant.RESPOND_LEDCODE_2.equals(uPosMsg.getLed_code()) && IUPOSConstant.MESSAGETYPE_0016.equals(uPosMsg.getMessageType()) ){
    		LogUtility.getPumpMLogger().info("[현금영수증 승인실패] POS로 전송안함. LED_CODE=2, MESSAGE_TYPE=0016" ) ;
    		rlt = false;
    	}

    	return rlt ;	
    }
    
}