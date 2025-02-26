package com.gsc.kixxhub.module.pumpm.pump.util;

import java.util.ArrayList;

import com.gsc.kixxhub.common.data.IUPOSConstant;
import com.gsc.kixxhub.common.data.pump.SL_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.data.upos.UPOSMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;

/**
 * 통합전문 형태 응답 전문 생성을 위한 WorkingMessgae 생성
 * 생성일자 : 2016.03.28 양일준
 * 0004(보너스 적립 응답), 0016(현금영수증 응답), 0032(신용 승인 응답),
 * 0034(신용 + 보너스 승인 응답), 0062(보너스 사용 응답), 4292(캠페인주유완료정보 응답),
 * 0014(현금 + 보너스 승인 응답)
 */
public class ODTUtility_NewRecharge {
	public static ArrayList<WorkingMessage> createWorkingMessageFromUPOSMessage_NewRecharge(UPOSMessage uPosMsg) {
		int messageType = Integer.parseInt(uPosMsg.getMessageType()) ;
		LogUtility.getLogger().info("[Pump M] Convert UPOSMessage[" + messageType
				+ "] to WorkingMessage for NewRecharge.") ;
		
		ArrayList<WorkingMessage> workMsgArray = new ArrayList<WorkingMessage>() ;
		
		// 충전기는 복합 결재가 가능하며, 또한 결재 이후 SH 전문이 오기 때문에, 모든 응답 전문의 lastPayment_yn 을 0 으로 설정한다.
		//uPosMsg.setLastPayment_yn("0") ;
		
		SL_WorkingMessage slWorkingMsg = null ;
		
		String odtNo = null ;
		String nozzle_no = uPosMsg.getNozzle_no() ;
		odtNo = PumpMUtil.getODTNumberFromNozzleNo(nozzle_no) ;	
		//String price = UPOSUtil.getPriceForPumpA(uPosMsg) ;
		String khprocsNo = uPosMsg.getPosReceipt_no() ;	
			 
		
		slWorkingMsg = new SL_WorkingMessage() ;
		slWorkingMsg.setMessageType(uPosMsg.getMessageType());
		slWorkingMsg.setNozzleNo(odtNo);
		slWorkingMsg.setConnectNozzleNo(nozzle_no);
		slWorkingMsg.setUnityMessage(uPosMsg);
		slWorkingMsg.setPosReceiptNo(khprocsNo);
		
		workMsgArray.add(slWorkingMsg) ;
			
		return workMsgArray ;
	}

    public static boolean shouldSendToSaleM(UPOSMessage uPosMsg) {
    	boolean rlt = true ;

    	try {
    		int messageTypeInt = Integer.parseInt(uPosMsg.getMessageType()) ;
    		switch (messageTypeInt) {
//    			case IUPOSConstant.MESSAGETYPE_INT_0004 :	// 보너스누적 응답 -> 보내도록 변경
    			case IUPOSConstant.MESSAGETYPE_INT_0112 :	// GS보너스점수조회 응답
//    			case IUPOSConstant.MESSAGETYPE_INT_0014 :	// 현금보너스 응답 -> 보내도록 변경
    			case IUPOSConstant.MESSAGETYPE_INT_0016 :	// 국세청현금영수증 응답
    			{
    				rlt = false ;
    				break ;
    			}
    		}
    	} catch (Exception e) {
    		LogUtility.getLogger().error(e.getMessage(),e) ;
    	}
    	return rlt ;	
    }
}

