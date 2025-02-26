package com.gsc.kixxhub.module.pumpm.pump.util;

import java.util.ArrayList;

import com.gsc.kixxhub.common.data.IUPOSConstant;
import com.gsc.kixxhub.common.data.pump.SL_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.data.upos.UPOSMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;

/**
 * �������� ���� ���� ���� ������ ���� WorkingMessgae ����
 * �������� : 2016.03.28 ������
 * 0004(���ʽ� ���� ����), 0016(���ݿ����� ����), 0032(�ſ� ���� ����),
 * 0034(�ſ� + ���ʽ� ���� ����), 0062(���ʽ� ��� ����), 4292(ķ���������Ϸ����� ����),
 * 0014(���� + ���ʽ� ���� ����)
 */
public class ODTUtility_NewRecharge {
	public static ArrayList<WorkingMessage> createWorkingMessageFromUPOSMessage_NewRecharge(UPOSMessage uPosMsg) {
		int messageType = Integer.parseInt(uPosMsg.getMessageType()) ;
		LogUtility.getLogger().info("[Pump M] Convert UPOSMessage[" + messageType
				+ "] to WorkingMessage for NewRecharge.") ;
		
		ArrayList<WorkingMessage> workMsgArray = new ArrayList<WorkingMessage>() ;
		
		// ������� ���� ���簡 �����ϸ�, ���� ���� ���� SH ������ ���� ������, ��� ���� ������ lastPayment_yn �� 0 ���� �����Ѵ�.
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
//    			case IUPOSConstant.MESSAGETYPE_INT_0004 :	// ���ʽ����� ���� -> �������� ����
    			case IUPOSConstant.MESSAGETYPE_INT_0112 :	// GS���ʽ�������ȸ ����
//    			case IUPOSConstant.MESSAGETYPE_INT_0014 :	// ���ݺ��ʽ� ���� -> �������� ����
    			case IUPOSConstant.MESSAGETYPE_INT_0016 :	// ����û���ݿ����� ����
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

