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
 * ODTUtility_GSCSelf �ű��߰� - CWI
 * �ű� ODT ������ ���� ODTUtility �߰�
 */
public class ODTUtility_GSCSelf {
	
	// PI2, CWI, 2016-03-18 �ܻ� ���� ���� ����
	public static UPOSMessage createUPOSMessageFromWorkingMessage_GSC_For_OISANG(WorkingMessage workingMsg, String khproc_no) {
		
		LogUtility.getPumpMLogger().info("[Pump M] Convert WorkingMessage to UPOSMessage for GSC") ;
		UPOSMessage uPosMsg = null ;
		GA_WorkingMessage gaWorkingMsg = (GA_WorkingMessage) workingMsg ;
		
        String tr_price  = gaWorkingMsg.getUnityMessage().getPayment_amt();                                                                                                                                                                           // �Ǹ� �ݾ�
        String nozzle_no =   gaWorkingMsg.getConnectNozzleNo();
        String custCard_No = gaWorkingMsg.getCustCardNo();
        String ss_crStNum =  gaWorkingMsg.getUnityMessage().getSs_crStNum();
        String ss_carNum =   gaWorkingMsg.getUnityMessage().getSs_carNum();
        
//      �Ʒ� �������� �ܻ����� ���� ������ ������ ä�� �ʿ䰡 ���� ���� ����. ���� Default ���� �����.
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
        // �ǹ� ���� ���� - ��
        
        String emp_no = "" ;                                                             // ������ ID (���� ODT �� ����)
        String term_id = gaWorkingMsg.getUnityMessage().getTerm_id();                    // �ܸ��� ��ȣ
        String lastPayment_yn = "0" ;                                                    // ������ ��������
        String led_code = "" ;                                                           // LED �ڵ�
        String keepDoc_limitDate = "" ;                                                  // ������ ������
        
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
	 * ���� ������ GSC ������ �����ϱ� ���ؼ� WorkineMessage Object �� ��ȯ�Ѵ�.
	 * �Ʒ��� UPOSMessage ���� ������ Message Type �� ���� ���� �����̴�.
	 * 	�ſ�ī�� ���� ����	= 0032
	 * 	�ſ�ī�� ���� ��� ���� = 8032
	 * 	�ſ�ī�� + ���ʽ� ���� ���� = 0034
	 * 	�ſ�ī�� + ���ʽ� ��� ���� = 8034
	 * 	BL üũ = 0072
	 * 
	 * @param uPosMsg	: UPOSMessage Object
	 * @return
	 */
	public static ArrayList<WorkingMessage> createWorkingMessageFromUPOSMessage_GSC(UPOSMessage uPosMsg) {
		LogUtility.getPumpMLogger().info("[Pump M] Convert UPOSMessage[" + uPosMsg.getMessageType() 
				+ "] to WorkingMessage for GSC.") ;

		ArrayList<WorkingMessage> workMsgArray = new ArrayList<WorkingMessage>() ;	
		
		// PI2, CWI, 2016-03-22, �Ѿ�� Upos �� GB�� �����ϴµ� �ּ����� �����͸��� ����ϱ⿡
		// ������ ���� �������� ó�� �Ѵ�.
		LogUtility.getPumpMLogger().info("[Pump M] Credit Response -> WorkingMessage") ;
		workMsgArray = getGB_WorkingMessage_GSC(uPosMsg) ;
		
		return workMsgArray ;
	}
	
	
	/**
	 * ���� ���� ������ ���� ���� ������ Adapter �� �����ϱ� ���� WorkingMessage Array �� ���� ��û�Ѵ�.
	 * GSC ���� ODT �� ���� ���� ������ GB �����̴�. 
	 * 
	 * @param uPosMsg	: ���� ���� ����
	 * @param isAccept	: ���� or ��ҽ��� ����
	 * @return
	 */
	private static ArrayList<WorkingMessage> getGB_WorkingMessage_GSC(UPOSMessage uPosMsg) {
		ArrayList<WorkingMessage> workMsgArray = new ArrayList<WorkingMessage>() ;	

		GB_WorkingMessage gbWorkingMsg = null ;
		
		String odtNo = null ;
		String nozzle_no = uPosMsg.getNozzle_no() ;
		
		odtNo = PumpMUtil.getODTNumberFromNozzleNo(nozzle_no) ;	
		
		// PI2, 2016-03-31, CWI, ���� �� GB���� ����
		gbWorkingMsg = new GB_WorkingMessage() ;
		gbWorkingMsg.setMessageType(uPosMsg.getMessageType());
		gbWorkingMsg.setNozzleNo(odtNo);
		gbWorkingMsg.setConnectNozzleNo(nozzle_no);
		gbWorkingMsg.setUnityMessage(uPosMsg);
		
		workMsgArray.add(gbWorkingMsg) ;
		
		return workMsgArray;
	}

	/**
	 * 0001 ���� ���� ���θ� �����Ѵ�.
	 * GSC ������ ��� GT ������ �����ϸ� �� �Լ��� ȣ���Ͽ� 0001 ������ POS �� ������ �� �����Ѵ�.
	 * ������ ���� ������ ����.
	 * 		1. POS �� ���� Preset �� ���� ������ �ƴ� ���
	 * 		2. ���� ������ �ƴ� ���
	 * 		3. ODT �� ���� ������ ���
	 * 
	 * @param nozzle_no	: ���� ��ȣ
	 * @param khproc_no	: KH ó����ȣ
	 * @param workMsg	: �Ǹ� �Ϸ� ���� (GT_WorkingMessage)
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
			
			// PI2, CWI, 2016-03-11 �ܻ�ŷ�ó�� ��� 0�� ������ �� pos�� 0001������ ���۵��� �ʵ��� �ؾ� �ǳ�,
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
	 * UPOSMessage ���� ������ Sale M ���� �������� �Ǵ��Ѵ�.
	 * 
	 * @param uPosMsg	: UPOSMessage ���� ����
	 * @return
	 */
    public static boolean shouldSendToSaleM(UPOSMessage uPosMsg) {
    	boolean rlt = true ;
    	
    	// 2013.07.18 ksm ���ݿ����� ���ν����� ��� POS�� ���� ����.
    	if(IUPOSConstant.RESPOND_LEDCODE_2.equals(uPosMsg.getLed_code()) && IUPOSConstant.MESSAGETYPE_0016.equals(uPosMsg.getMessageType()) ){
    		LogUtility.getPumpMLogger().info("[���ݿ����� ���ν���] POS�� ���۾���. LED_CODE=2, MESSAGE_TYPE=0016" ) ;
    		rlt = false;
    	}

    	return rlt ;	
    }
    
}