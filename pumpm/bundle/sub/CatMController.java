package com.gsc.kixxhub.module.pumpm.bundle.sub;

 
import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.ibatis.session.SqlSession;

import com.gsc.kixxhub.common.data.ICode;
import com.gsc.kixxhub.common.data.IConstant;
import com.gsc.kixxhub.common.data.ICustConstant;
import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.IUPOSConstant;
import com.gsc.kixxhub.common.data.beacon.BeaconMessage;
import com.gsc.kixxhub.common.data.beacon.BeaconMessageToByteArray;
import com.gsc.kixxhub.common.data.beacon.ByteArrayToBeaconMessage;
import com.gsc.kixxhub.common.data.beacon.JB_BeaconMessage;
import com.gsc.kixxhub.common.data.posdata.POSHeader;
import com.gsc.kixxhub.common.data.posdata.POS_DK;
import com.gsc.kixxhub.common.data.posdata.POS_DQ;
import com.gsc.kixxhub.common.data.posdata.POS_DT;
import com.gsc.kixxhub.common.data.posdata.POS_DX;
import com.gsc.kixxhub.common.data.posdata.POS_EN;
import com.gsc.kixxhub.common.data.pump.GB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.QL_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SL_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.data.upos.CreateUPOSMessage;
import com.gsc.kixxhub.common.data.upos.UPOSMessage;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_BarcodeInfo;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_CatalogInfo;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_ItemInfo;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_ItemInfo_Item;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_ShopCodeInfo;
import com.gsc.kixxhub.common.dbadapter.beacon.handler.BeaconDataHandler;
import com.gsc.kixxhub.common.dbadapter.common.SqlSessionFactoryManager;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_CT_CATHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_BIN_INFOHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_CODEMASTERHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_CUST_INFOHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_INTEGCUST_BIN_INFOHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_INTEGCUST_BIN_INFO_MEMORYHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_PUMP_TRHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_STOREHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.manager.LockingManager;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_CT_CATData;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_CUST_INFOData;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_PUMP_TRData;
import com.gsc.kixxhub.common.dbadapter.pump.handler.T_KH_CARWASH_COUPON_LISTHandler;
import com.gsc.kixxhub.common.dbadapter.pump.vo.T_KH_CARWASH_COUPON_LISTData;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.beacon.BeaconUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.common.utility.property.PropertyManager;
import com.gsc.kixxhub.common.utility.sync.Preamble;
import com.gsc.kixxhub.common.utility.sync.SyncManager;
import com.gsc.kixxhub.module.pumpm.pump.manager.PumpMODTSaleManager;
import com.gsc.kixxhub.module.pumpm.pump.manager.PumpMSyncManager;
import com.gsc.kixxhub.module.pumpm.pump.manager.PumpMTransactionManager;
import com.gsc.kixxhub.module.pumpm.pump.manager.dasno.DasNoSelfPumpingManager;
import com.gsc.kixxhub.module.pumpm.pump.manager.gsc.GSCSelfPumpingManager;
import com.gsc.kixxhub.module.pumpm.pump.util.Barcode;
import com.gsc.kixxhub.module.pumpm.pump.util.CustUtil;
import com.gsc.kixxhub.module.pumpm.pump.util.ODTUtility_Common;
import com.gsc.kixxhub.module.pumpm.pump.util.ODTUtility_DaSNo;
import com.gsc.kixxhub.module.pumpm.pump.util.ODTUtility_GSC_Self;
import com.gsc.kixxhub.module.pumpm.pump.util.PumpLogUtil;
import com.gsc.kixxhub.module.pumpm.pump.util.PumpMUtil;
import com.gsc.kixxhub.module.pumpm.pump.util.UPOSUtil;

public class CatMController extends CatMControllerBase {

	private boolean logSSDC=false;
	
	private Hashtable<String, UPOSMessage> uposCargoHash = new Hashtable<String, UPOSMessage>() ;

    /**
     * CAT M ���� ������ ���� �Ʒ��� ��Ȳ�� �����Ͽ� �߰����� �۾��� �����Ѵ�.
     * 		1. KH ó����ȣ�� ������, ���������� 0�� ���
     * 			�� ���� �߻��ɼ� �ִ�. ���� �ŷ�ó ������/�Ϸ� ���� ��û��, ���� ������ ������ �ŷ�ó ī�尡 ���� ��� �߻� �� �� �ִ�.
     * 			�� ��� unLocking �� ������ �־�� �Ѵ�.
     * 
     * @param preambleData	: Preamble Object including UPOSMessage object
     */
    private void handleBeforeSendingMsgToCATM(Preamble preambleData) {
    	
    	try {
    		Object obj = preambleData.getPreamble() ;
    		
    		if (obj instanceof UPOSMessage) {
    			UPOSMessage uPosMsg = (UPOSMessage) obj ;
    			
    			int messageType = Integer.parseInt(uPosMsg.getMessageType()) ;
    			
    			switch (messageType) {
    				/**
    				 * 2016. 4. 14. ���� 15:48:31, PI2, WooChul Jung
    				 * 		������ ��� ���� ���� �𸣱� ������, ����
    				 */    				 
	    			case IUPOSConstant.MESSAGETYPE_INT_4302 : {
	    				String custCar_limit_type = uPosMsg.getCustCar_limit_type() ;
	    				String ss_crStNum = uPosMsg.getSs_crStNum() ;
	    				
	    				/**
	    				 * 2016. 4. 14. ���� 15:48:31, PI2, WooChul Jung.
	    				 * 		ȸ�� ������ : ������ �̻�, ������ ����, ����ö ����.
	    				 * 		�䱸 ���� : by ������ ����
	    				 * 			������ ������, �ܻ� �ŷ�ó-�ѵ� ���� ��� ���� �Ұ��� CAT �ܸ��⿡ �����ϵ��� ����
	    				 * 			�ŷ�ó ī���� ������ �̿��Ͽ� ó���ؾ� �ϱ� ������, ���� �������� ó���ϵ��� �����Ͽ���.
	    				 * 		�׽�Ʈ ���
	    				 * 			�Ϲ� �ſ�, �ܻ� ���� (������), �ܻ� �ѵ�, ���� �ŷ�ó ���� ���� �׽�Ʈ �Ϸ��Ͽ���.
	    				 */
	    				if (!GlobalUtility.isNullOrEmptyString(ss_crStNum) && 
	    						(ICode.DY_LIMIT_TYPE_01.equals(custCar_limit_type) || ICode.DY_LIMIT_TYPE_02.equals(custCar_limit_type))) {
	    					// �ŷ�ó �ѵ� ���� ��� ���� ����
	    					LogUtility.getPumpMLogger().debug("[Pump M] �ŷ�ó �ѵ� �� - ������ ���� �Ұ�!") ;
	    					uPosMsg.setLed_code(IUPOSConstant.LEDCODE_G);
	    				} 

	    				String posReceipt_no = uPosMsg.getPosReceipt_no() ;
	    				
	    				if (UPOSUtil.shouldUnLock(uPosMsg)) {
	    					sendLockingInfoToPOS(uPosMsg.getNozzle_no(), posReceipt_no, IConstant.PUMP_SALE_UNLOCKING) ;
	    					// 2016. 4. 14. ���� 15:48:31, PI2, Taekwon Lee �ŷ�ó led_code�� ���� CAT �ܸ��� �������� ����� ������� �������� ����
		    				if(Integer.parseInt(uPosMsg.getMessageType()) == IUPOSConstant.MESSAGETYPE_INT_4302	){
		    					LogUtility.getPumpMLogger().info("[Pump M] CAT Preset �ŷ�ó �����Ұ��� ���� - ����� ������� Led_code=" + uPosMsg.getLed_code()) ;
		    					process0091(posReceipt_no);
		    				}
	    				}
	    				break ;
	    			}
	    			case IUPOSConstant.MESSAGETYPE_INT_4202 :
	    			case IUPOSConstant.MESSAGETYPE_INT_4292 : {
	    				String posReceipt_no = uPosMsg.getPosReceipt_no() ;
	    				
	    				if (UPOSUtil.shouldUnLock(uPosMsg)) {
	    					sendLockingInfoToPOS(uPosMsg.getNozzle_no(), posReceipt_no, IConstant.PUMP_SALE_UNLOCKING) ;
	    				}
	    				break ;
	    			}
    			}
    			preambleData.setCampaign(UPOSUtil.shouldCampaign(uPosMsg)) ;
    		}
    	} catch (Exception e) {
    		LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
    	}
    }
 
    private boolean isCashYn (String msgType){
    	boolean isCash = false;
    	
    	if ( msgType.equals(IUPOSConstant.MESSAGETYPE_0004 ) ||
    			msgType.equals(IUPOSConstant.MESSAGETYPE_0014 ) ||
				msgType.equals(IUPOSConstant.MESSAGETYPE_0016 )||
				msgType.equals(IUPOSConstant.MESSAGETYPE_0054 )||
				msgType.equals(IUPOSConstant.MESSAGETYPE_0084 ))
    		isCash = true;
    	return isCash;
    	
    }
 
    /**
     * �ܸ��� ��� ������ Ȯ���Ѵ�.
     * ��ϵ� �ܸ��Ⱑ �ƴ� ��� false �� �����Ͽ� CAT �ܸ��⿡�� �˸���.
     * 
     * @param crt_no_seq	: �ܸ��� ��ȣ
     * @return
     */
    public boolean isRegisteredCATDevice(String crt_no_seq) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "CatMController/" + "isRegisteredCATDevice()");
		
    	boolean rlt = false ;
    	
		SqlSession session = null;
		try {
			session = SqlSessionFactoryManager.openSqlSession();
			T_CT_CATData catData = T_CT_CATHandler.getHandler().getT_CT_CATData(session, crt_no_seq) ;
			if (catData == null) {
				LogUtility.getPumpMLogger().warn("[Pump M] Not registered CAT Device in Table. crt_no_seq=" + crt_no_seq) ;
				rlt = false ;
			} else {
				LogUtility.getPumpMLogger().info("[Pump M] registered CAT Device in Table. crt_no_seq=" + crt_no_seq) ;
				rlt = true ;
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}
		
		return rlt ;
    }
    
    /**
     * 
     * CAM M ���κ��� ���� ������ ó���Ѵ�.
     * 
     * ������ ���� ������ �������� CAT Module �� ���� �޴´�.
     * 		1. Locking / unLocking ��û (POS Protocol)
     * 			: TR ���� ���̺� Locking/Unlocking �� �� �� ���� ������ CAT Module �� ������.
     * 		2. ������ ���� ��û (POS Protocol with u-POS ����)
     * 			: TR ���� ���̺� ���� �� ������ �� CAT Module �� ������.
     * 		3. ��û�� ������ ���� ���� ���� (POS Protocol with u-POS ����)
     * 			: Working Message Class �� ���� Pump Adapter �� ������.
     * 
     * @param receiving_catm	: CAM M ���� ���� ���� ����
     */
    @Override
	protected void onReceivingCatMData(Object receiving_catm) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "CatMController/" + "onReceivingCatM()");
		
    	try {
	        Preamble receivingData = (Preamble) receiving_catm ;
	        String syncUnique = receivingData.getKey() ;
	        
	    	PumpLogUtil.printContent(syncUnique, receivingData.getFrom(),receivingData.getDest(),receivingData.getPreamble()) ;
	    	
	        if (receivingData.getPreamble() instanceof UPOSMessage) {
	        	UPOSMessage uPosMsg = (UPOSMessage) receivingData.getPreamble() ;
	        	int messageType = Integer.parseInt(uPosMsg.getMessageType()) ;
	        	String nozID = uPosMsg.getNozzle_no() ;
	        	String posReceipt_no = uPosMsg.getPosReceipt_no() ;
	        	
	        	LogUtility.getPumpMLogger().debug("[Pump M] receive UPOS messageType : " + messageType+", uPosMsg id"+ uPosMsg.getMessageID());
	        	
	        	
	        	
	        	/*
	        	 * BEACON ���� :
	        	 * 	1. ��ִ��� -> PumpA : ��������(0012),���ݺ��ʽ�����(0014), �ſ��������(0032), �ſ�+���ʽ���������(0034),
	        	 *     ��ִ��� -> POS     GS���ʽ� �������(0062), ���� �������(0064), ���� + ����û���ݿ����� �������(0066)
	        	 *                        �ֹ����� ����(0222)
	        	 * 	2. ��ִ��� -> POS   : �����������(8012),���ݺ��ʽ��������(8014), �ſ�����������(8032), �ſ�+���ʽ������������(8034),
	        	 *                        GS���ʽ� ����������(8062), ���� ����������(8064), ���� + ����û���ݿ����� ����������(8066)
	        	 *                        Beacon ���� ���� ��Ҵ� POS������ ����
	        	 *                        �ܸ��⿡�� ��ҽô� �񿬵����� ���
	        	 * 		- DeviceType   : 3M
	        	 * 2017.06.30
	        	 */
	        	if(IUPOSConstant.DEVICE_TYPE_3M.equals(uPosMsg.getDeviceType())){	// beacon���� �� ��� beacon���� ����
	        		switch (messageType) {
	        			case IUPOSConstant.MESSAGETYPE_INT_0014 :	        				
	        			case IUPOSConstant.MESSAGETYPE_INT_0032 : 
	        			case IUPOSConstant.MESSAGETYPE_INT_0034 :
	        			case IUPOSConstant.MESSAGETYPE_INT_0046 :
	        			case IUPOSConstant.MESSAGETYPE_INT_0048 : 	
	        			case IUPOSConstant.MESSAGETYPE_INT_0052 :	
	        			case IUPOSConstant.MESSAGETYPE_INT_0062 :
	        			case IUPOSConstant.MESSAGETYPE_INT_0064 :
	        			case IUPOSConstant.MESSAGETYPE_INT_0066 :
	        			{
	        				UPOSMessage posUposMsg = uPosMsg.clone();
	        				
	        				LogUtility.getLogger().info("[Pump M] <Beacon Process> ���� ���� ���� ����") ;
	        				
	        				
	        				if("1".equals(posUposMsg.getLed_code())){
	        					
	        					//Beacon���� �� ���ο�û ����
		        				LogUtility.getLogger().info("[Pump M] <Beacon Process> Send Beacon UPOSMessage (" + posUposMsg.getMessageType() + ") to SaleM") ;
						    	Preamble posPreamble = PumpMUtil.createUPOSMessagePreamble( syncUnique, 
																				 SyncManager.DISE_PUMP_MODULE,
																				 SyncManager.DISE_SALE_MODULE, 
																				 posUposMsg, 
																				 "") ;
						    	sendMessage(posPreamble) ;
	        					
	        				}else{
	        					/*
		        				 * Beacon ������ ���� 
		        				 * ���� ���� �� ��� POS�� �˾��� ���� ���� 
		        				 * messageType�� 100�� ���ؼ� 
		        				 * POS�� ���� ������ ���� 
		        				 * ODT������ ������.
		        				 * 2017.06.05 ����ȣ����,���⺴���� ����
		        				 */
	        					
	        					//���� ���� �� ���
	        					if(messageType == IUPOSConstant.MESSAGETYPE_INT_0032 
        	    					|| messageType == IUPOSConstant.MESSAGETYPE_INT_0034
        	    					|| messageType == IUPOSConstant.MESSAGETYPE_INT_0052
        	    					|| messageType == IUPOSConstant.MESSAGETYPE_INT_0062
        	    					|| messageType == IUPOSConstant.MESSAGETYPE_INT_0046
        	    					|| messageType == IUPOSConstant.MESSAGETYPE_INT_0048
        	    					){
    		        					posUposMsg.setMessageType(GlobalUtility.appending0Pre(String.valueOf(messageType+100), 4));
    		        					posUposMsg.setSelfPayment_type("12");
    		        					
    		        					LogUtility.getLogger().info("[Pump M] <Beacon Process> Fail Response. Change MsgType from=" + messageType + " to=" + posUposMsg.getMessageType() + " NozzleNo: " + posUposMsg.getNozzle_no() +  " / Led_Code: " + posUposMsg.getLed_code() + " / SelfPayment_Type: " + posUposMsg.getSelfPayment_type()) ;
    		        					
    		        					LogUtility.getLogger().info("[Pump M] <Beacon Process> Send Beacon UPOSMessage (" + posUposMsg.getMessageType() + ") to SaleM") ;
    							    	Preamble posPreamble = PumpMUtil.createUPOSMessagePreamble( syncUnique, 
    																					 SyncManager.DISE_PUMP_MODULE,
    																					 SyncManager.DISE_SALE_MODULE, 
    																					 posUposMsg, 
    																					 "") ;
    							    	sendMessage(posPreamble) ;
        		        					
        	    				}
	        				}

	        				LogUtility.getLogger().info("[Pump M] <Beacon Process> Send Beacon UPOSMessage (" + messageType + ") to ARK + msgID : "+uPosMsg.getMessageID() ) ;
	        				sendBeaconMessageToBeaconM(syncUnique,uPosMsg);
					    	
					    	break;
	        			}
	        			case IUPOSConstant.MESSAGETYPE_INT_0222 :{
	        				  //�ֹ���û ���� ������ ó���Ѵ�.
				    		  process0222(syncUnique, uPosMsg);
					    	  break ;
				          }
	        			case IUPOSConstant.MESSAGETYPE_INT_8012 : 
	        			case IUPOSConstant.MESSAGETYPE_INT_8014 :
	        			case IUPOSConstant.MESSAGETYPE_INT_8032 : 
	        			case IUPOSConstant.MESSAGETYPE_INT_8034 :
	        			case IUPOSConstant.MESSAGETYPE_INT_8046 :
	        			case IUPOSConstant.MESSAGETYPE_INT_8048 :
	        			case IUPOSConstant.MESSAGETYPE_INT_8052 :
	        			case IUPOSConstant.MESSAGETYPE_INT_8062 :
	        			case IUPOSConstant.MESSAGETYPE_INT_8064 :
	        			case IUPOSConstant.MESSAGETYPE_INT_8066 :{
        					//POS���� �� ��� ����
        					LogUtility.getLogger().info("[Pump M] <Beacon Process> Beacon ���� ��� ���� ���� ����") ;
        					
        					/*
        					 * pos���� ���� ��ǰ �����, ��ǰ ��û�� messageId�������� Ȯ���ϱ� ������
        					 * messageId �Է�  2017.05.17 lj
        					 * 
        					 */
        					uPosMsg.setMessageID(syncUnique);
					    	Preamble posPreamble = PumpMUtil.createUPOSMessagePreamble( syncUnique, 
																			 SyncManager.DISE_PUMP_MODULE,
																			 SyncManager.DISE_SALE_MODULE, 
																			 uPosMsg, 
																			 "") ;
					    	sendMessage(posPreamble) ;
					    						    	
        					break ;
        					}
	        			default:{
	        				sendBeaconMessageToBeaconM(syncUnique,uPosMsg);
	        				break;
	        			}
	        		}
	        		
	        	} else {
		        	switch (messageType) {
				    	  case IUPOSConstant.MESSAGETYPE_INT_4291 :
				    		  // CAT �ܸ���κ��� ��û - ķ���������Ϸ�  ���� ��û
				    	  case IUPOSConstant.MESSAGETYPE_INT_4201 : {
				    		  // CAT �ܸ���κ��� ��û - ķ���������� ���� ��û	    	 
				    	  	  if (messageType == IUPOSConstant.MESSAGETYPE_INT_4201) {
				    			  LogUtility.getPumpMLogger().info("[Pump M] Receive Request of PumpingInfo from CAT M. NozID="+nozID) ;
				    		  } else {
				    			  LogUtility.getPumpMLogger().info("[Pump M] Receive Request of Completed PumpInfo from CAT M. nozID="+nozID) ;
				    		  }		 
				    		  process4201_4291(syncUnique, receiving_catm, messageType, uPosMsg) ;
				    		  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_4292 : 
					      case IUPOSConstant.MESSAGETYPE_INT_4202 : {
					    	// ODT or LPG �κ��� ��û - ķ���������Ϸ�  ���� ����
				    		// 2016-01-12, PI2, songkis, ������ ķ���� ������ �ٽ� PUMPA�� ���� �Ѵ�.
					    	  LogUtility.getLogger().info("[Pump M] �������� ���� ķ���� ���� ���� ������ ����") ;
					    	  
					    	  if(uPosMsg != null && (
					    			  IUPOSConstant.MESSAGETYPE_4202.equals(uPosMsg.getMessageType()) && uPosMsg.getDeviceType().equals(IUPOSConstant.DEVICE_TYPE_3S) || 
					    			  IUPOSConstant.MESSAGETYPE_4292.equals(uPosMsg.getMessageType()) && uPosMsg.getDeviceType().equals(IUPOSConstant.DEVICE_TYPE_3O)
					        								) ) {			    	        
					    		 //LogUtility.getLogger().info("[Pump M] �������� ���� ="+uPosMsg.getPromptDiscount_yn()) ;
				    			LogUtility.getLogger().info("[Pump M] Receive Request of PumpingInfo from CAT M. This originated from ODT or LPG. NozID="+nozID + 
				    					"#�������ο���=" + uPosMsg.getPromptDiscount_yn()) ;
				    			if ("1".equals(uPosMsg.getPromptDiscount_yn())) {
				    				PumpMODTSaleManager.setPromptDiscount_yn(nozID, uPosMsg.getPromptDiscount_yn()) ;
				    			}
						    	sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
						    	//LogUtility.getLogger().info("[Pump M] Receive Request of Completed PumpInfo from CAT M. nozID="+nozID) ;
				    		}
					    	break;
					      }
				    	  case IUPOSConstant.MESSAGETYPE_INT_4301 : {
				    		  // CAT �ܸ���κ��� ��û - Preset	    	 
				    		  LogUtility.getPumpMLogger().info("[Pump M] Receive Request of NozzleState info from CAT M. nozID="+nozID) ;
				    		  // process4201_4291�� 4301 ���� ���μ��� �߰�
				    		  process4201_4291(syncUnique, receiving_catm, messageType, uPosMsg) ;
				    		  break ;
				    	  }
				    	  
				    	  case IUPOSConstant.MESSAGETYPE_INT_0301 : {
				    		  // CAT �ܸ���κ��� ��û - ��ǰ �ٿ�ε� ��û
				    		  // ��ǰ �ٿ�ε� ��û�� �޾Ƽ� KixxHub ���̺� ����� ��ǰ ����(�������� + ��ǰ���� + ��������)�� CAT M ���� �����Ѵ�.
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Request of Product info from CAT M. NozID="+nozID) ;
					    	  UPOSMessage sendUPOSMsg = process0301(uPosMsg) ; 
					    	  Preamble preamble = 
					    		  PumpMUtil.createUPOSMessagePreamble(syncUnique, SyncManager.DISE_PUMP_MODULE, SyncManager.DISE_CAT_MODULE, sendUPOSMsg, "") ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Product info to CAT M. NozID="+nozID) ;
				    		  sendMessage(preamble) ;
				    		  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_0091 : {
				    		  //CAT �ܸ���κ��� ��û - ����� �������
				    		  process0091(posReceipt_no);
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_0004 : {
				    		  // ODT �� ������ ��û�� ���� ���� - ���ʽ����� ����
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Bonus from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Bonus to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_0012 : {
				    		  // ODT �� ������ ��û�� ���� ���� - ���� ����
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Cash Receipt from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Cash Receipt to Pump A. posReceipt_no="+posReceipt_no) ;
				    		  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_0014 : {
				    		  // ODT �� ������ ��û�� ���� ���� - ���ݺ��ʽ����� ����
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Cash+Bonus from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Cash+Bonus to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_0016 : {
				    		  // ODT �� ������ ��û�� ���� ���� - ���� ������ ����
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Cash Receipt from CAT M. posReceipt_no="+posReceipt_no) ;		    	  
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Cash Receipt to Pump A. posReceipt_no="+posReceipt_no) ;    	  
					    	  break ;
				    	  }	 
				    	  case IUPOSConstant.MESSAGETYPE_INT_0032 : {
				    		  // ODT �� ������ ��û�� ���� ���� - �ſ���� ����
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Credit from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Credit to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_0034 : {
				    		  // ODT �� ������ ��û�� ���� ���� - �ſ뺸�ʽ� ����
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Credit+Bonus from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Credit+Bonus to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_0046 : {
				    		  // ODT �� ������ ��û�� ���� ���� - ���̽��� ����
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Pay from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Pay to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_0048 : {
				    		  // ODT �� ������ ��û�� ���� ���� - ���̺��ʽ� ����
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Pay+Bonus from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Pay+Bonus to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }		    	  
				    	  case IUPOSConstant.MESSAGETYPE_INT_0052 : {
				    		  // ODT �� ������ ��û�� ���� ���� - myLG������� ����
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive myLG Use from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send myLG Use to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_0054 : {
				    		  // ODT �� ������ ��û�� ���� ���� - ����û ���� ������ +  GS���ʽ� ���� ����.
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive CashReceipt + Bonus from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send myLG Use to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_0062 : {
				    		  // ODT �� ������ ��û�� ���� ���� - GS������� ����
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive GS BonusCard Use from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send GS BonusCard Use to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_0064 : {
				    		  // ODT �� ������ ��û�� ���� ���� - ������� ����
					    	  LogUtility.getLogger().info("[Pump M] Receive Coupon Use from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getLogger().info("[Pump M] Send Coupon Use to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_0066 : {
				    		  // ODT �� ������ ��û�� ���� ���� - ���� + ����û���ݿ����� ��� ����
				    		  LogUtility.getLogger().info("[Pump M] Receive Coupon + CashReceipt Use from CAT M. posReceipt_no="+posReceipt_no) ;
				    		  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
				    		  LogUtility.getLogger().info("[Pump M] Send Coupon + CashReceipt Use to Pump A. posReceipt_no="+posReceipt_no) ;
				    		  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_0072 : {
				    		  // ODT �� ������ ��û�� ���� ���� - �ſ�ī�� Ȯ�� ���� - BL üũ
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Credit Card check from CAT M. posReceipt_no="+posReceipt_no) ;		    	  
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Credit Card check to Pump A. posReceipt_no="+posReceipt_no) ;    	  
					    	  break ;
				    	  }	 
				    	  case IUPOSConstant.MESSAGETYPE_INT_0112 : {
				    		  // ODT �� ������ ��û�� ���� ���� - ���ʽ� ī���� ����Ʈ ���� ��ȸ ����
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Bonus Card Point inquiry from CAT M. posReceipt_no="+posReceipt_no) ;		    	  
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Bonus Card Point inquiry to Pump A. posReceipt_no="+posReceipt_no) ;    	  
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_8004 : {
				    		  // ODT �� ������ ��û�� ���� ���� - ���ʽ����� ��� ����
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Bonus Cancel from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Bonus Cancel to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_8014 : {
				    		  // ODT �� ������ ��û�� ���� ���� - ���ݺ��ʽ����� ��� ����
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Cash+Bonus Cancel from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Cash+Bonus Cancel to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }case IUPOSConstant.MESSAGETYPE_INT_8032 : {
				    		  // ODT �� ������ ��û�� ���� ���� - �ſ���� �������
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Credit Cancel from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Credit Cancel to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_8034 : {
				    		  // ODT �� ������ ��û�� ���� ���� - �ſ뺸�ʽ� �������
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Credit+Bonus Cancel from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Credit+Bonus Cancel to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_8046 : {
				    		  // ODT �� ������ ��û�� ���� ���� - ���̽��� �������
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Pay from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Pay Cancel to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_8048 : {
				    		  // ODT �� ������ ��û�� ���� ���� - ���̺��ʽ� �������
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Pay+Bonus from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Pay+Bonus Cancel to  Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }	
				    	  case IUPOSConstant.MESSAGETYPE_INT_8052 : {
				    		  // ODT �� ������ ��û�� ���� ���� - myLG������� �������
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive myLG Use Cancel from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send myLG Use Cancel to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_8062 : {
				    		  // ODT �� ������ ��û�� ���� ���� - GS������� �������
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive GS Bonus Card Use Cancel from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send GS Bonus Card Use Cancel to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_8064 : {
				    		  // ODT �� ������ ��û�� ���� ���� - ����� ���� �������
					    	  LogUtility.getLogger().info("[Pump M] Receive Mobile Coupon Use Cancel from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getLogger().info("[Pump M] Send Mobile Coupon Use Cancel to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_8066 : {
				    		  // ODT �� ������ ��û�� ���� ���� - ����� ���� + ����û���ݿ����� �������
				    		  LogUtility.getLogger().info("[Pump M] Receive Mobile Coupon + CashReceipt Use Cancel from CAT M. posReceipt_no="+posReceipt_no) ;
				    		  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
				    		  LogUtility.getLogger().info("[Pump M] Send Mobile Coupon + CashReceipt Use Cancel to Pump A. posReceipt_no="+posReceipt_no) ;
				    		  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_0082 : {
				    		  // ODT �� ������ ��û�� ���� ���� - �ܻ� ��������
					    	  LogUtility.getLogger().info("[Pump M] Receive Bonus from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getLogger().info("[Pump M] Send GS Bonus Card Use Cancel to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_0084 : {
				    		  // ODT �� ������ ��û�� ���� ���� - GS������� �������
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Bonus from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send GS Bonus Card Use Cancel to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_0232 : {
				    		  // ODT �� ������ ��û�� ���� ���� - �����������
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive SimplePay from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send SimplePay to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_0236 : {
				    		  // ODT �� ������ ��û�� ���� ���� - ����CayPay ������ȸ����
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Vehicle inquiry(Hyundai) from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Vehicle inquiry(Hyundai) to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }	
				    	  case IUPOSConstant.MESSAGETYPE_INT_0242 : {
				    		  // ODT �� ������ ��û�� ���� ���� - ����� ���հ�������
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Mobile(WebPay) from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Mobile(WebPay) to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }	
				    	  case IUPOSConstant.MESSAGETYPE_INT_0244 : {
				    		  // ODT �� ������ ��û�� ���� ���� - ����CayPay ���հ�������
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive CarPay(Hyundai) from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send CarPay(Hyundai) to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }	
				    	  case IUPOSConstant.MESSAGETYPE_INT_0246 : {
				    		  // �ٷμ��� ��������
				    		  LogUtility.getPumpMLogger().info("[Pump M] Receive Mobile(WebPay) from CAT M. posReceipt_no="+posReceipt_no) ;
				    		  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
				    		  LogUtility.getPumpMLogger().info("[Pump M] Send Mobile(WebPay) to Pump A. posReceipt_no="+posReceipt_no) ;
				    		  break ;
				    	  }	
				    	  case IUPOSConstant.MESSAGETYPE_INT_8242 : {
				    		  // ODT �� ������ ��û�� ���� ���� - ����� ���հ����������
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Mobile(WebPay)Use Cancel from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Mobile(WebPay)Use Cancel to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }	
				    	  case IUPOSConstant.MESSAGETYPE_INT_8244 : {
				    		  // ODT �� ������ ��û�� ���� ���� - ����CayPay ���հ����������
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive CarPay(Hyundai)Use Cancel from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send CarPay(Hyundai)Use Cancel to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }					    	  
				    	  case IUPOSConstant.MESSAGETYPE_INT_8246 : {
				    		  // �ٷμ��� ������� ����
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Mobile(WebPay)Use Cancel from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Mobile(WebPay)Use Cancel to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_9994 : {
				    		  // ODT �� ������ ��û�� ���� ���� - �������� �� �����
					    	  LogUtility.getLogger().info("[Pump M] Receive After Service Cancel from CAT M. posReceipt_no") ;
					    	  
					    	  //LogUtility.getLogger().info("[Pump M] �Ѿ�� �ŷ����� ��ȣ="+uPosMsg.getTrx_Proper_No()) ;
					    	  String nozzle_no = "";
					    	  String trx_proper_no = uPosMsg.getTrx_Proper_No();
					    	  nozzle_no = GSCSelfPumpingManager.getInstance().getGSCODTNozzleNo(trx_proper_no);
					    	  // hash map�� ������ ���ٹ�ȣ ����
					    	  GSCSelfPumpingManager.getInstance().removeODTNozzleNo(trx_proper_no);
					    	  
					    	  uPosMsg.setNozzle_no(nozzle_no);
					    	  //LogUtility.getLogger().info("[Pump M] ������ ���ٹ�ȣ="+nozzle_no) ;
					    	  
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getLogger().info("[Pump M] Send After Service Cancel to Pump A. posReceipt_no") ;
					    	  break ;
				    	  }
		        	}
	        	}
	     
	       	} else if (receivingData.getPreamble() instanceof POSHeader) {
	       		
	       		POSHeader posHeader = (POSHeader)receivingData.getPreamble() ;
		    	String receivingCommandID = posHeader.getCommandID() ;
		    	String uniqueKey = receivingData.getKey();		    	
				
		    	Preamble pumpPreamble = null ;
		    	String nozID = posHeader.getDeviceID() ;
		    	
				
		    		switch (receivingCommandID) {
			    		/**
		    			 * CAT���� ���� Preset ��û�� �����Ѵ�.
		    			 * ���� ���� Pump A �� ������ Preset �� ��û�ϰ�, POS�δ� KH ó����ȣ�� �����Ͽ� �����Ѵ�.
		    			 */
		    			case IConstant.POSPROTOCOL_COMMANDID_DK : {
		    				// ����/���� ���� ��û	
		    		    	LogUtility.getPumpMLogger().info("[Pump M] Receive Fixed Price/Liter Pump from CAT. NozID="+nozID) ;
		        			POS_DK dkMsg = (POS_DK) posHeader ;
		    				// Debug
			    			LogUtility.getPumpMLogger().info(dkMsg.toString());
		        			PB_WorkingMessage pbWorkMsg = PumpMUtil.createPB_WorkMsg(dkMsg) ;
		        			
		        	    	String khproc_no = 
		        				PumpMTransactionManager.getInstance().getKHTransactionID(pbWorkMsg.getNozzleNo(), IPumpConstant.KH_PUMP_PRESET) ;
		        	    	PumpMTransactionManager.getInstance().setPresetInfo(pbWorkMsg.getNozzleNo(), IPumpConstant.PRESET_FROM_CAT) ;
		        	    	// 2012.07.20 ksm  PB_WorkingMessage�� barcode �ʵ� ����.
		        	    	// twsongkis 2015-01-28 ���ο� ���ڵ� �������� barcode ����
		        	    	pbWorkMsg.setBarCode(Barcode.getBarcodeNumber("6", pbWorkMsg.getPrice(), nozID, khproc_no, null, null, null));
		        	    	
		        	    	try {
		        				T_KH_PUMP_TRHandler.getHandler().updatePresetInfo_BY_khproc_no(khproc_no, 
		        						dkMsg.getCommand(), dkMsg.getLiter(), dkMsg.getPrice(), dkMsg.getPreset_baesPrice()) ;
		        			} catch (Exception e) {
		        				LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		        			}
		    				pumpPreamble = PumpMUtil.createWorkingMessagePreamble(syncUnique,SyncManager.DISE_PUMP_ADAPTER, pbWorkMsg, "") ;
		    		    	LogUtility.getPumpMLogger().info("[Pump M] Request Fixed Price/Liter to Pump A. NozID="+nozID) ;
		    				sendMessage(pumpPreamble) ; 
		    				
	    					processHF(uniqueKey, pbWorkMsg.getMessageID(), khproc_no, pbWorkMsg);
	    					LogUtility.getPumpMLogger().info("[Pump M] Respond Preset to POS");
		    				
		    				// KH ó����ȣ�� POS �� �����Ѵ�.	- CAT �ܸ���� ���� ���ŵ� DK �̱� ������, POS �� DL ������ ������ �ʿ�� ����.
/*		    				POS_DL dlPumpM = new POS_DL(dkMsg.getMessageID(), dkMsg.getDeviceType(), dkMsg.getDeviceID(), khproc_no) ;
		    				pumpPreamble = PumpMUtil.createPOSMessagePreamble(null,SyncManager.DISE_POS_ADAPTER, dlPumpM, "") ;
		       		    	LogUtility.getPumpMLogger().info("[Pump M] Respond Fixed Price/Liter to POS. NozID="+nozID) ;
		    				sendMessage(pumpPreamble) ;    */    		
		    				
		    				break ;
		    			}
		    		}
	       	}
    	} catch (Exception e) {
    		LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
    	} catch (Throwable e1) {
    		LogUtility.getPumpMLogger().error(e1.getMessage(),e1) ;
    	}
    }
   


	/**
     * 
     * @param receiving_catm
     * 
     * 	Parameter
     * 		sending_catm
     * 			Preamble	byte[]
     */
    @Override
	protected void onReceivingPumpMData(Object sending_catm) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "CatMController/" + "onSendingCatM()");
    	sendMessage((Preamble) sending_catm) ;
    }

	

	private void process0091(String posReceipt_no){
		 // CAT �ܸ���κ��� ��û - ����� ���� ��� (Locking ���� ��û ����)
  	  
  	  LogUtility.getPumpMLogger().info("[Pump M] Receive Locking Release from CAT M. " +
  	  		"posReceipt_no="+posReceipt_no) ;
  	
  	  try {
	    	  if ((posReceipt_no != null) && (posReceipt_no.startsWith(IConstant.PREFIX_K))) {
		    	  String nozzle_no = "" ;
	    		  T_KH_PUMP_TRData pumpTrData = T_KH_PUMP_TRHandler.getHandler().getT_KH_PUMP_TRData(posReceipt_no) ;
	    		  if (pumpTrData != null) {
		    		  nozzle_no = pumpTrData.getNozzle_no() ;
			    	  sendLockingInfoToPOS(nozzle_no, posReceipt_no, IConstant.PUMP_SALE_UNLOCKING) ;
			    	  LogUtility.getPumpMLogger().info("[Pump M] handle locking release. ") ;
			    	  // CAT Preset ����� ������ҽ� pump state �ʱ�ȭ
			    	  if(PumpMTransactionManager.getInstance().isPresetFromCAT(nozzle_no) 
			    			  && "0".equals(pumpTrData.getOil_start_ind()) && "0".equals(pumpTrData.getOil_completed_ind())){
		    			  PumpMTransactionManager.getInstance().getKHTransactionID(nozzle_no, IPumpConstant.KH_STATE_RESET) ;
			    	  }
			    	  
	    		  } else {
	    			  LogUtility.getPumpMLogger().warn("[Pump M] not exist in Table.") ;
	    		  }
	    	  } else {
	    		  LogUtility.getPumpMLogger().info("[Pump M] Not KH Transaction ID format") ;
	    	  }
  	  } catch (Exception e) {
  		  LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
  	  }
	}
    
	/**
	 * CAT �ܸ���� ���� ��ǰ �ٿ�ε� ��û�� �޾Ƽ� �̿� ���� ���������� �����Ѵ�.
	 * ���� ��ϵ��� ���� CAT �ܸ���κ��� ��û�� ���� ��� Led_Code �� 0 ���� �Ͽ� ���� ������ �����Ѵ�.
	 * �̴� ��ϵ��� ���� �ܸ��⸦ �ǹ��Ѵ�.
	 * 
	 * 2019.08.09
	 * �������ڵ� �� �������ڵ� ��½� �ʿ������� FIller1�� �Ѱ��ش�.
	 * by SoonKwan
	 * @param uPosInfo	: ��ǰ�ڵ� �ٿ�ε� ��û ����
	 * @return			: ��ǰ�ڵ� �ٿ�ε� ���� ����
	 */
	public UPOSMessage process0301(UPOSMessage uPosInfo) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "CatMController/" + "process0301()");
		
		UPOSMessage uPosMessageReturn = null ;
		
		String deviceType = uPosInfo.getDeviceType() ;
		String download_flag = null ;
		UPOSMessage_CatalogInfo catalog_info = null ;
		String term_id = uPosInfo.getTerm_id() ; // �ܸ��� ��ȣ
		//String version = uPosInfo.getFiller1();		// �ܸ��� ���� ����
		// PI2, ������ ������ ���� ���� Upos Message ����, 2016.03.31 - CWI
		// filler1 -> Cat_version�� ��ȯ
		String version = uPosInfo.getCat_version();		// �ܸ��� ���� ����
		
		// �������ڵ带 ����ϱ����� ������ �߰��Ѵ�
		// Filler1�� �־��ش�. 
		// 2019.08.07 SoonKwan �߰�
		UPOSMessage_BarcodeInfo  barcode_info = null;
		
		UPOSMessage_ShopCodeInfo shopCode = null ;
		String led_code = IUPOSConstant.LEDCODE_0 ;
		String localMileage_yn = IUPOSConstant.LOCALMILEAGE_YN_0 ;
		String posPrint_yn = IUPOSConstant.POSPRINT_YN_0 ;
		String coupon_yn = IUPOSConstant.U0302_FILLER1_0;
		String couponSupplyCode = "";
		
		SqlSession session = null;
		try {
			session = SqlSessionFactoryManager.openSqlSession();
			T_CT_CATData catData = T_CT_CATHandler.getHandler().getT_CT_CATData(session, term_id) ;
			if (catData == null) {
				LogUtility.getPumpMLogger().warn("[Pump M] Not registered CAT Device in Table. term_id=" + term_id) ;
				shopCode = null ;
				catalog_info = null ;
				download_flag = null ;
				led_code = IUPOSConstant.U0302_LEDCODE_0 ;		// ��ϵ� �ܸ��Ⱑ �ƴ�.
				barcode_info = null;
			} else
			{
				LogUtility.getPumpMLogger().info("[Pump M] registered CAT Device in Table. term_id=" + term_id) ;
				catData.print() ;

				catalog_info = UPOSUtil.getUPOSMessage_Catalog_info() ;
				barcode_info = UPOSUtil.getUPOSMessage_Barcode_info(catData.getCrt_no_seq()) ;
				
				/**
				 * [2008.11.19] by WooChul Jung
				 * 
				 * ��ǰ�ٿ�ε� ����� 'CAT �ܸ��� ����Ʈ ����' '���帶�ϸ��� ���� ����' �ʵ尡 �߰��Ǿ���.
				 * �׸��� �Ʒ� �Լ� ȣ�� ������ �߿��ϴ�. download_flag �� ���� �� �� �Լ��� ���� �ֽ� ������ ����ȴ�.
				 */
				localMileage_yn = UPOSUtil.getMileage_ind() ;
				posPrint_yn = UPOSUtil.getCATPrint_ind() ;
				download_flag = UPOSUtil.getDownloadFlag() ;
				
				//���帶�ϸ��� ���� ����
				coupon_yn = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(session, IConstant.POSPORTOCOL_CODEMASTER_0304);

				if (coupon_yn == null)
					coupon_yn = IUPOSConstant.U0302_FILLER1_0;
				
				//���� ����ó �ڵ�
				couponSupplyCode = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(session, IConstant.POSPORTOCOL_CODEMASTER_219);
				
				if (couponSupplyCode == null)
					couponSupplyCode = "";
				
				/*// �׽�Ʈ �������� �����Ѵ�.
				if (PropertyManager.getSingleton().getProperty(PropertyManager.KH_REAL, PropertyManager.KH_REAL_DEFAULT).trim().equals("0")) { 
					LogUtility.getPumpMLogger().error("[Pump M-TEST] Set store code as  \"V00000\"") ;
					shopCode = new UPOSMessage_ShopCodeInfo(	catData.getSys_use_ind() ,
							catData.getCrt_code_type(),
							IConstant.STORE_CODE_DEFAULT_V00000) ;
				} else {
					shopCode = new UPOSMessage_ShopCodeInfo(	catData.getSys_use_ind() ,
							catData.getCrt_code_type(),
							catData.getStore_code()) ;
				}*/
				
				//��ִ������� �Ŀ� �׽�Ʈ �϶���  �Ǹ����ڵ��� �÷��� �Ǳ� ������ ���� 
				shopCode = new UPOSMessage_ShopCodeInfo(	catData.getSys_use_ind() ,
						catData.getCrt_code_type(),
						catData.getStore_code()) ;
				
				led_code = IUPOSConstant.U0302_LEDCODE_1 ;		// ��ϵ� �ܸ�����
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}
	
		sendCatVersion(term_id, version);
		
		// �̵�� �ܸ��⿡�� ����������� ������.
		// ���ڵ� ������ Ȯ���� �ʿ�� �����ϴ�
		// by SoonKwan
		if(led_code == IUPOSConstant.U0302_LEDCODE_0)
		{
			uPosMessageReturn = CreateUPOSMessage.createUPOSMessage_0302(deviceType, 
					shopCode, 
					download_flag, 
					catalog_info, 
					led_code, 
					posPrint_yn, 
					localMileage_yn,
					coupon_yn
					);
		}
		else {
			uPosMessageReturn = CreateUPOSMessage.createUPOSMessage_0302(deviceType, 
					shopCode, 
					download_flag, 
					catalog_info, 
					led_code, 
					posPrint_yn, 
					localMileage_yn,
					coupon_yn,
					barcode_info  // 2019.08.07 SoonKwan �������ڵ�����
					);
			
		}		
		//uPosMessageReturn.setFiller2(couponSupplyCode);
		uPosMessageReturn.setCust_code(couponSupplyCode);
		return uPosMessageReturn;
	}

	/**
	 * ķ���� ���� ���� ��û�� ���� ����
	 * 
	 * @param syncUnique		: ���� Key
	 * @param receiving_catm	: CAM M ���κ��� �� Preamble Data
	 * @param messageType		: ���� message type
	 * @param uPosMsg			: ķ���� ���� ���� ��û ����
	 */
    private void process4201_4291(String syncUnique, Object receiving_catm, int messageType, UPOSMessage uPosMsg) {
    	if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "CatMController/" + "process4201_4291()");
		
    	String custCard_car_type = uPosMsg.getCustCard_car_type() ;
	  
    	if (GlobalUtility.isNullOrEmptyString(custCard_car_type)) {
    		LogUtility.getPumpMLogger().error("[Pump M] Set custCard_car_type 0 as default.") ;
    		custCard_car_type = IUPOSConstant.CUSTCARD_CAR_TYPE_0 ;
    	}
    		  
    	// ���������� �����Ϸ������� ���� POS���� �ŷ����������� �ֱ�����
    	// POS�� ��û���� �߰� - upkoo
    	String isPumping = ICode.DX_ISPUMPING_IND_0 ;
	  
    	String term_id = uPosMsg.getTerm_id() ;
	  
    	
		
    	if (!isRegisteredCATDevice(term_id)) {
    		// �ܸ��Ⱑ ��ϵǾ� ���� ���� ���
    		LogUtility.getPumpMLogger().warn("[Pump M] Not registered Device, so send UPOSMessage to CAT M without PumpInfo.") ;
    		UPOSMessage_ItemInfo_Item itemInfoItem = null ;
    		UPOSMessage sendUPOSMsg = null ;
    		if (messageType == IUPOSConstant.MESSAGETYPE_INT_4201) {
    			sendUPOSMsg = UPOSUtil.process4201_4291(uPosMsg , itemInfoItem, false) ;
    		} else if(messageType == IUPOSConstant.MESSAGETYPE_INT_4301) {
    			sendUPOSMsg = UPOSUtil.process4301(uPosMsg , itemInfoItem) ;
    		}else{	// IUPOSConstant.MESSAGETYPE_INT_4291 case only
    			sendUPOSMsg = UPOSUtil.process4201_4291(uPosMsg , itemInfoItem, true) ;
    		}
		  
    		sendUPOSMsg.setLed_code(IUPOSConstant.LEDCODE_Z) ;
    		Preamble preamble = PumpMUtil.createUPOSMessagePreamble(syncUnique, 
					SyncManager.DISE_PUMP_MODULE, 
					SyncManager.DISE_CMS_MODULE, 
					sendUPOSMsg, 
					"");
    		sendMessage(preamble);

    		/**
    		 * ���� �� ���� ��û�� ��� �ܻ� �ŷ�ó - �ѵ� ���� ���ؼ� ���� �Ұ� �������� ����.
    		 * ���� ���� �������� ó���� �ؾ� ��.
    		 * 
    		 * => �Ʒ��� ������ �ּ� ó���Ͽ� ���� KixxHUB ������ �����ϰ� ���� (KixxHUB ��ȭ ������)
    		 */
    	/*}else if(messageType == IUPOSConstant.MESSAGETYPE_INT_4201 
    			&& ("1".equals(custCard_car_type) || "3".equals(custCard_car_type))){
    		// 2016-04-19 Taekwon Lee �ŷ�ó - ������ ���� ����
        		LogUtility.getPumpMLogger().debug("[Pump M] �ŷ�ó - ������ ���� �Ұ�!") ;
        		UPOSMessage_ItemInfo_Item itemInfoItem = null ;
        		UPOSMessage sendUPOSMsg = null ;
    			sendUPOSMsg = UPOSUtil.process4201_4291(uPosMsg , itemInfoItem, false) ;
    			sendUPOSMsg.setLed_code(IUPOSConstant.LEDCODE_G);
        		Preamble preamble = PumpMUtil.createUPOSMessagePreamble(syncUnique, 
        				SyncManager.DISE_PUMP_MODULE, 
        				SyncManager.DISE_CMS_MODULE, 
        				sendUPOSMsg, 
        				"");
        		sendMessage(preamble);*/
    		
    	}else if (custCard_car_type.equals(IUPOSConstant.CUSTCARD_CAR_TYPE_0)) {
    		// �ŷ�ó��/������ ���� (0:���� 1:�ŷ�óī��� ����������û 2:���������ȣ��ȸ 3: ������ȣ�� ����������û 
    		//                                     4:����ȭ���ŷ�ó��ȸ, 5:���������ŷ�ó ��ȸ)
    		// ķ���� ���� ���� ��û
    		LogUtility.getPumpMLogger().info("[Pump M] Receive Pump Info from CAT M.") ;
    		UPOSMessage sendUPOSMsg = UPOSUtil.getUPOSMessage_ItemInfo_Item(uPosMsg, messageType) ;		
		  
    		// 2013.05.06 ksm ���հŷ�ó Ȯ��.
    		// ������������û�� ��� ���հŷ�ó ���� ������ ��.
    		if ( messageType == IUPOSConstant.MESSAGETYPE_INT_4201 
    			&& T_KH_INTEGCUST_BIN_INFO_MEMORYHandler.getHandler().isExist()
				&& (!"".equals(uPosMsg.getBonRSCard_no()) || !"".equals(uPosMsg.getCreditCard_no()))) {
			  	
    			//boolean bCjGoodsYn = false;
    			
				try{
					// PI2, 2016-03-31, �������� �� �ʷ� ����ȭ
					//String[] filler5 = uPosMsg.getFiller5().split(IUPOSConstant.DELIMITER_RS_STRING);
					String bonCardReading_type = uPosMsg.getBonCardReading_type();        // ���ʽ�ī�� ������ü
					String creditCardReading_type = uPosMsg.getCreditCardReading_type();  // �ſ�ī�� ������ü
					
					//String[] filler4 = GlobalUtility.splitByteArrayToStringArray(uPosMsg.getFiller4().getBytes(), IUPOSConstant.DELIMITER_0X1E); 
					String custGoods_Code = uPosMsg.getCustGoods_code(); // �ſ�ī�� ��ǰ�ڵ�
					LogUtility.getLogger().debug("[���հŷ�ó] �ſ�ī�� ��ǰ�ڵ� custGoods_Code : " + custGoods_Code);
					
					/*
					//��ǰ�� ���� ���� ���� ���� �����Ƿ� ���� �Ǵ���.
					if(filler4.length == 3){	// ȭ��Ưȭ�ŷ���ǰ�ڵ� �ʵ� ����.
						if("".equals(filler4[2])){
							LogUtility.getCATLogger().debug("[���հŷ�ó] ��ǰ�ڵ� ������.");
						}else{
							LogUtility.getCATLogger().debug("[���հŷ�ó] ��ǰ�ڵ� : " + filler4[2]);
							bCjGoodsYn = true;
						}
					}
					
					// �ſ�ī��� ���ʽ� ��� ������ü�� IC�ΰ�� bCjGoodsYn true�� ����.
	  				if("01".equals(filler5[0]) && "01".equals(filler5[2]))
	  				{
	  					bCjGoodsYn = true;
	  				} */
					
	  				//if(bCjGoodsYn){
					//if(filler4.length == 3){
					
//					��ǰ�ڵ� ���� ���հŷ�ó ������ ���� ���� [�ּ�ó��]
//					if(!custGoods_Code.equals("")){ // ȭ��Ưȭ�ŷ���ǰ�ڵ� ���� ���� ���
						String isTarget =  T_KH_INTEGCUST_BIN_INFO_MEMORYHandler.getHandler().isTarget(	uPosMsg.getCreditCard_no(),
																										uPosMsg.getBonRSCard_no(),
																										/**
																										filler5[2],			// �ſ�ī�� ������ü
																										filler5[0],			// ���ʽ�ī�� ������ü
																										filler4[2]);		// �ſ�ī�� ��ǰ�ڵ�
																										*/
																										// �������� ��뿡 ���� ���� filler �迭�� �и� 
																										creditCardReading_type, // �ſ�ī�� ������ü
																										bonCardReading_type,    // ���ʽ�ī�� ������ü
																										custGoods_Code);        // �ſ�ī�� ��ǰ�ڵ�(ȭ��Ưȭ�ŷ���ǰ�ڵ�)
						
						if(!"".equals(isTarget)){
							sendUPOSMsg.setLed_code(IUPOSConstant.LEDCODE_J);
							
							Preamble preamble = PumpMUtil.createUPOSMessagePreamble(syncUnique, 
									SyncManager.DISE_PUMP_MODULE, 
									SyncManager.DISE_CMS_MODULE, 
									sendUPOSMsg, 
									"") ;		    				
							sendMessage(preamble) ;
							return;
						}
//					}
				}catch(NullPointerException e){
					LogUtility.getCATLogger().debug("[���հŷ�ó] Filler5  Split ����. filler5 ��ü ����.");
				}catch (IndexOutOfBoundsException e){
					LogUtility.getCATLogger().debug("[���հŷ�ó] Filler5 �׸� ����. �ܸ��� ��ġ�ؿ�.");
				}catch(Exception e){
					LogUtility.getCATLogger().debug("[���հŷ�ó] ���� �������� ?" + e.toString());
				}
		  	}else if(messageType == IUPOSConstant.MESSAGETYPE_INT_4291 
		  		//&&T_KH_INTEGCUST_BIN_INFO_MEMORYHandler.getHandler().isExist()
		  		&& (!"".equals(uPosMsg.getBonRSCard_no()) || !"".equals(uPosMsg.getCreditCard_no()))){
			  
		  		//boolean bCjGoodsYn = false;
		  		
		  		try{
		  			//String[] filler5 = uPosMsg.getFiller5().split(IUPOSConstant.DELIMITER_RS_STRING);
		  			String creditCardReading_type = uPosMsg.getCreditCardReading_type();  // �ſ�ī�� ������ü
		  			String bonCardReading_type = uPosMsg.getBonCardReading_type();        // ���ʽ�ī�� ������ü		  	
		  			
		  			//String[] filler4 = GlobalUtility.splitByteArrayToStringArray(uPosMsg.getFiller4().getBytes(), IUPOSConstant.DELIMITER_0X1E); 
		  			String custGoods_Code = uPosMsg.getCustGoods_code();   // �ſ�ī�� ��ǰ�ڵ�(ȭ��Ưȭ�ŷ���ǰ�ڵ�)
		  			
		  			String targetCust = "";
		  			SqlSession session = null;
				  
		  			try {
		  				/*
		  				// �ſ�ī��� ���ʽ� ��� ������ü�� IC�ΰ�� bCjGoodsYn true�� ����.
		  				if("01".equals(filler5[0]) && "01".equals(filler5[2]))
		  				{
		  					bCjGoodsYn = true;
		  				}*/
		  				
		  				//if(bCjGoodsYn){
		  				//if(filler4.length == 3){
		  				
		  				//20170306 �̰�ȣ 
		  				//��ǰ�ڵ� ���� ���հŷ�ó ������ ���� ���� [�ּ�ó��]
		  				//if(!custGoods_Code.equals("")){// ȭ��Ưȭ�ŷ���ǰ�ڵ� ���� ���� ���
			  				session = SqlSessionFactoryManager.openSqlSession();
			  				LogUtility.getCATLogger().debug("[���հŷ�ó]  custGoods_Code : " + custGoods_Code );
			  				
			  				if(T_KH_INTEGCUST_BIN_INFOHandler.getHandler().isExist(session)){
			  					targetCust =  T_KH_INTEGCUST_BIN_INFOHandler.getHandler().getTargetCust(session,  
			  							 uPosMsg.getCreditCard_no(),
										 uPosMsg.getBonRSCard_no(),
			  							 creditCardReading_type, // �ſ�ī�� ������ü
			  							 bonCardReading_type,    // ���ʽ�ī�� ������ü
			  							 custGoods_Code);          // �ſ�ī�� ��ǰ�ڵ�
			  				}else{
			  					targetCust = "";
			  				}
		  				//}else{
		  				//	targetCust = "";
		  				//}
		  			}catch (Exception e){
		  				LogUtility.getCATLogger().debug("[���հŷ�ó] DB ��ȸ �� ���� : " + e.toString(), e);
		  				
		  			}finally{
	  					SqlSessionFactoryManager.closeSqlSession(session);
		  			}
		  			if(!"".equals(targetCust)){
		  				LogUtility.getCATLogger().debug("[���հŷ�ó] "   + targetCust);
						session = SqlSessionFactoryManager.openSqlSession();
						// �ŷ�ó ��ȸ.
						String store_code = T_KH_STOREHandler.getHandler().getStoreCode();
						  
						try{
							T_KH_CUST_INFOData custInfoData = 
								T_KH_CUST_INFOHandler.getHandler().getT_KH_CUST_INFOData(session, targetCust, store_code) ;
							  
							if (custInfoData == null){
								LogUtility.getCATLogger().debug("[���հŷ�ó] �ŷ�ó �ڵ尡 ���������ʽ��ϴ�. targetCust : " + targetCust);
							}else{	  
								int nCustCdItem 	= Integer.parseInt(custInfoData.getCust_cd_item());
								String goodsCd	= sendUPOSMsg.getItem_info().getItemInfoList().get(0).getGoodsCode();
								
								if(nCustCdItem >= 31 && nCustCdItem <= 36){
								// �ŷ�ó ���� Ȯ���Ͽ� ���ʽ��������� �Ǵ�.
								// 31 ~ 33 : ���հŷ�ó ���ʽ�������.
								// 34 ~ 36 : ���հŷ�ó ���ʽ���������.
									if(nCustCdItem >= 34 && nCustCdItem <= 36){
										sendUPOSMsg.setLed_code(IUPOSConstant.LEDCODE_K);
									}
									  
									//  �ش��ϴ� �ŷ�ó�� ������ �ŷ��ܰ� ����.									
									LogUtility.getPumpMLogger().debug("[Pump M] ��ϵ� PL �� �˻��մϴ�.cust_code_rep="+	targetCust+",goods_code="+ goodsCd) ;
									String basePrice = CustUtil.calcIntegCustBasePrice( session, store_code, targetCust, goodsCd);
									  
									if(!"".equals(basePrice)){
										LogUtility.getPumpMLogger().debug("[���հŷ�ó] �ŷ��ܰ� ���ؿ�. basePrice = " + basePrice);
										
										String bonusGoodsCd				= sendUPOSMsg.getItem_info().getItemInfoList().get(0).getBonGoodsCode();
										String oilInd					= sendUPOSMsg.getItem_info().getItemInfoList().get(0).getOil_ind();
										String unitPriceBeforeDiscount	= sendUPOSMsg.getItem_info().getItemInfoList().get(0).getUnitPrice_before_discount();
										String oilAmount				= sendUPOSMsg.getItem_info().getItemInfoList().get(0).getOilAmount();
										String taxInd					= sendUPOSMsg.getItem_info().getItemInfoList().get(0).getTax_ind();
										String khTransactionID			= sendUPOSMsg.getItem_info().getItemInfoList().get(0).getKhTransactionID();
										  
										String oilPriceBeforeDiscount = sendUPOSMsg.getItem_info().getItemInfoList().get(0).getOilPrice_before_discount();
										  
										String oilPriceAfterDiscount = 
												PumpMUtil.calculatePriceFromLiterAndBasePrice(PumpMUtil.convertNumberFormatFromUPOSToStandardFormat(oilAmount), basePrice);
										  
										String taxPrice = GlobalUtility.getTaxPrice(oilPriceAfterDiscount);
										String priceBeforeTax = PumpMUtil.getPrice_before_tax(oilPriceAfterDiscount, taxPrice);
										  									  
										//LogUtility.getPumpMLogger().debug("[���հŷ�ó] 1:" + unitPriceBeforeDiscount + " 2: " +  oilAmount + " 3: " + oilPriceBeforeDiscount +" 4:  " + oilPriceAfterDiscount);
										UPOSMessage_ItemInfo_Item newItemInfoItem 
											= CreateUPOSMessage.createUPOSMessage_ItemInfo_Item(sendUPOSMsg.getNozzle_no(), 
													goodsCd, 
													bonusGoodsCd, 
													oilInd, 												
													unitPriceBeforeDiscount, 								// �������ܰ�
													oilAmount, 												// ����
													GlobalUtility.getMultipleWith1000(basePrice), 			// ���δܰ�
													taxInd, 												// ���鼼����
													priceBeforeTax, 										// ���ް���
													taxPrice, 												// �ΰ�����
													oilPriceBeforeDiscount, 								// �������ݾ�
													oilPriceAfterDiscount, 									// �����ıݾ�
													khTransactionID, 										// ��ǥ��ȣ
													ICode.PROC_IND_OVERLIMIT_99,							// �ܻ����Ÿ��
													ICode.DY_UNITDISCOUNT_IND_1,							// ���ο���
													"", 													// ��������ȣ
													ICode.SE_PUBLISH_BASE_01);								// ��������������
										UPOSMessage_ItemInfo newItemInfo = UPOSMessage_ItemInfo.createUPOSMessage_ItemInfo(newItemInfoItem);
										
										sendUPOSMsg.setItem_info(newItemInfo);
										// �ŷ�ó ���� ����
										sendUPOSMsg.setSs_crStNum(targetCust);
										sendUPOSMsg.setSs_crStNm(custInfoData.getCust_name());
										
										//sendUPOSMsg.print();
										  
										Preamble preamble = PumpMUtil.createUPOSMessagePreamble(syncUnique, 
												SyncManager.DISE_PUMP_MODULE, 
												SyncManager.DISE_CMS_MODULE, 
												sendUPOSMsg, 
												"");
										
										if ((!sendUPOSMsg.getLed_code().equals(IUPOSConstant.LEDCODE_0)) 
												&& (sendUPOSMsg.getPosReceipt_no().startsWith(IConstant.PREFIX_K))) {
												// �����ǿ� ���� ���� ���� ���θ� POS �� �뺸
											sendLockingInfoToPOS(uPosMsg.getNozzle_no(), 
													sendUPOSMsg.getPosReceipt_no(), 
													IConstant.PUMP_SALE_LOCKING) ;
										}
											
										LogUtility.getPumpMLogger().info("[Pump M] Send Pump Info to CAT M.") ;		    
										
										sendMessage(preamble) ;
										return ;
	
									}else{
										LogUtility.getCATLogger().debug("[���հŷ�ó]  ��ȸ�� �ŷ��ܰ�/�ŷ������� �����ϴ�. �ŷ�ó : "  + targetCust);
										  
										sendUPOSMsg.setLed_code(IUPOSConstant.LEDCODE_7);
										
										Preamble preamble = PumpMUtil.createUPOSMessagePreamble(syncUnique, 
												SyncManager.DISE_PUMP_MODULE, 
												SyncManager.DISE_CMS_MODULE, 
												sendUPOSMsg, 
												"") ;		    
										
										sendMessage(preamble) ;
										return;
									}								  								  
								} else {
									LogUtility.getCATLogger().debug("[���հŷ�ó]  ��ȸ�� �ŷ�ó�� ���հŷ�ó�� �ƴմϴ�. �ŷ�ó���� : " + custInfoData.getCust_cd_item());
								}
							}
						}catch(Exception e){
							LogUtility.getCATLogger().debug("[���հŷ�ó] DB ��ȸ �� ���� : " + e.toString());
						}finally{
							SqlSessionFactoryManager.closeSqlSession(session);
						}
		  			}
		  		}catch(NullPointerException e){
		  			LogUtility.getCATLogger().debug("[���հŷ�ó] Filler5  Split ����. filler5 ��ü ����.", e);
				}catch (IndexOutOfBoundsException e){
					LogUtility.getCATLogger().debug("[���հŷ�ó] Filler5 �׸� ����. �ܸ��� ��ġ�ؿ�.", e);
				}catch(Exception e){
					LogUtility.getCATLogger().debug("[���հŷ�ó] ���� �������� ?" + e.toString(), e);
				}
		  	}

		  if (uPosMsg.getTaxFreeCust_type().equals(IUPOSConstant.TAXFREECUST_TYPE_2)) {
			  UPOSMessage_ItemInfo itemInfo = sendUPOSMsg.getItem_info() ;
			  if (!itemInfo.getRecordNo().equals("00")) {
				  // �鼼 ī�� - �Ϲ� �� - �鼼 �ܰ��� �����ϵ��� �Ѵ�.
				  LogUtility.getPumpMLogger().info("[Pump M] TaxFree Card - Normal Customer.") ;
				  PumpMUtil.resetTaxFreePrice(itemInfo.getItemInfoList().get(0)) ;
			  }
		  }
		  
		  // ���� ���� STARTRUCLKī�� �� ��� ���ʽ�ī�� �� ������ ���� ������ �������ش�.	  
		  String bin = uPosMsg.getBonRSCard_no();
		  
		  boolean isKBDiscount = false;
		  boolean KBStarUseYN = true;
		  
		  try {
			  if (!"1".equals(T_KH_CODEMASTERHandler.getHandler().codeMaster.get(IConstant.POSPORTOCOL_CODEMASTER_0334))) {
				LogUtility.getPumpMLogger().debug("[���� �������� ī�� ] ȯ�漳�� : 0334 (������ ����)"  );
				KBStarUseYN = false;
			  } else {
				LogUtility.getPumpMLogger().debug("[���� �������� ī�� ] ȯ�漳�� : 0334 (����� ����)"  );
			  }			  
		  } catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		  } 

		  // pi2, cwi, 2016-03-31
		  // properties ���� �ҷ� �鿩 phase 1, 2�� �и� �ϸ�,
		  // phase 1�� ��� ���ν�ŸƮ�� �� ���հŷ�ó ������ ���� �Ѵ�.
		  // phase 2�� ��� ���հŷ�ó ������ �㰡 �ϵ�, �ŷ�ó�� ��� ���հŷ�ó ������ ���� �Ѵ�.
		  //String pi2_phase = PropertyManager.getSingleton().getProperty("kixxhub.pi2.deploy.phase", "2").trim();
		  
		  //sendUPOSMsg.print() ;
		  //uPosMsg.print() ;
		 /* if(pi2_phase.equals("1")){
			  if(IUPOSConstant.DEVICE_TYPE_3S.equals(uPosMsg.getDeviceType()) || IUPOSConstant.DEVICE_TYPE_3O.equals(uPosMsg.getDeviceType())){
				  LogUtility.getLogger().debug("[���� �������� ī�� ] ���� �Ǵ� ������ ��");
				  KBStarUseYN = false;
			  }
		  }else{*/
		  if(IUPOSConstant.DEVICE_TYPE_3S.equals(uPosMsg.getDeviceType()) || IUPOSConstant.DEVICE_TYPE_3O.equals(uPosMsg.getDeviceType())){
			  
			  if(IUPOSConstant.DEVICE_TYPE_3S.equals(uPosMsg.getDeviceType()) && !GlobalUtility.isNullOrEmptyString(uPosMsg.getCustCard_No())){
				  LogUtility.getLogger().debug("[���� �������� ī�� ] Self_CustCard Not null ");
				  KBStarUseYN = false;
			  }else if(IUPOSConstant.DEVICE_TYPE_3O.equals(uPosMsg.getDeviceType())){
				  LogUtility.getLogger().debug("[���� �������� ī�� ] Recharge");
				  KBStarUseYN = false;
			  }
		  }
		  

		  
		  LogUtility.getLogger().info("[Pump M] KBStarUseYN="+KBStarUseYN) ;

		  /* 20180531 ygh
		   * [���� �������� ī�� ]  
		   * �ſ�ī�� ������ ���� ��츸 ���ʹ� 15�� ���� ó�� 
		   */
//		  LogUtility.getLogger().info("ddddddddddd"+uPosMsg.getBonRSCard_no()) ;
//		  LogUtility.getLogger().info("fffffffffff"+uPosMsg.getCreditCard_no()) ;
		  if ("".equals(uPosMsg.getBonRSCard_no()) 
				  || "".equals(uPosMsg.getCreditCard_no())){
			  KBStarUseYN = false;
			  LogUtility.getLogger().info("[Pump M] �ſ�ī�尡 �ƴ� ��� ��ŸƮ�� ���� KBStarUseYN="+KBStarUseYN) ;
		  }
			  
		  
		  
		  if (KBStarUseYN) {
			  SqlSession session = null;
			  try {
				  session = SqlSessionFactoryManager.openSqlSession();
				  // ksm 01 : ����ȭ�� , 02: VIP, 03:����� , 06:ȭ�������ں���ī��, 07:�ýð���ī��, 08 : �ýÿ��ī��, 09: ������������ī��, 10: �������� IC���ʽ�BIN 
				  isKBDiscount = T_KH_BIN_INFOHandler.getHandler().isExist(session, "10", bin);
				  LogUtility.getLogger().debug("[���� �������� ī�� ] isKBDiscount?="+isKBDiscount);
			  } catch (Exception e) {
				  LogUtility.getLogger().error(e.getMessage(),e);
			  } finally {
				  SqlSessionFactoryManager.closeSqlSession(session);
			  }
		  } 
		  
		  if (isKBDiscount) {
			  LogUtility.getLogger().debug("[���� �������� ī�� ] ���հŷ�ó ��ǰ���� ����");
			  sendUPOSMsg = CustUtil.calcBasePrice(uPosMsg, sendUPOSMsg);
			  //sendUPOSMsg.setFiller3(ICustConstant.KB_STARTLUCK_CARD_DISCOUNT);
			  // PI2, ������ ������ ���� ���� Upos Message ����, 2015.11.19 - cwi
			  sendUPOSMsg.setRepCustDiscount_Type(ICustConstant.KB_STARTLUCK_CARD_DISCOUNT);
			 
			  // pi2, 2016-02-16, ���� ODT���� ���� STARTRUCLKī�� �� ��� ���������� ���� �ʵ��� Led �ڵ带 0���� ���� �Ѵ�.
			  if(IUPOSConstant.DEVICE_TYPE_3S.equals(sendUPOSMsg.getDeviceType())){
				  
				  /*
				   * �츮ī�� �� ȭ����������ī��(����ȭ��)�� ���� ���� ODT������ �������� ó�� �ǵ��� �Ѵ�.
				   * 2021.04 GSĮ�ؽ� �����ÿ�� ��μ� ���� ��û�� ���Ͽ� ��� �ϵ��ڵ� ó��
				   * 
				   * 0190-6102-3211-0000 ~ 0190-6102-3310-9999 : FDT1904001 �츮����ȭ��-���νſ�
				   * 0190-6102-3311-0000 ~ 0190-6102-3320-9999 : FDT1904002 �츮����ȭ��-����üũ
				   * 0190-6102-3321-0000 ~ 0190-6102-3330-9999 : FDT1904003 �츮����ȭ��-�ܻ�ŷ�
				   * 
				   * */
					LogUtility.getLogger().debug("[Pump M] => ���ʽ� ī�� : " + uPosMsg.getBonRSCard_no());
	
					if(uPosMsg.getBonRSCard_no() != null) {
	
						// ���ʽ�ī���ȣ
						long bonRsCardNo = Long.parseLong(uPosMsg.getBonRSCard_no());
						
						// 0190-6102-3211-0000 ~ 0190-6102-3310-9999 : FDT1904001 �츮����ȭ��-���νſ�
						long cardArea01S = 190610232110000l;
						long cardArea01E = 190610233109999l;
						
						// 0190-6102-3311-0000 ~ 0190-6102-3320-9999 : FDT1904002 �츮����ȭ��-����üũ
						long cardArea02S = 190610233110000l;
						long cardArea02E = 190610233209999l;
						
						// 0190-6102-3321-0000 ~ 0190-6102-3330-9999 : FDT1904003 �츮����ȭ��-�ܻ�ŷ�
						long cardArea03S = 190610233210000l;
						long cardArea03E = 190610233309999l;
	
						if((bonRsCardNo >= cardArea01S && bonRsCardNo <= cardArea01E) || (bonRsCardNo >= cardArea02S && bonRsCardNo <= cardArea02E) || (bonRsCardNo >= cardArea03S && bonRsCardNo <= cardArea03E)) {
							
							LogUtility.getLogger().info("[Pump M] => �ش� ī��� �츮����ȭ�� ī��ν� �������� �ѵ���ȸ�� �����ϵ��� �Ѵ�." );
							sendUPOSMsg.setPromptDiscount_yn("1");
							
						} else {
							sendUPOSMsg.setPromptDiscount_yn("0");
						}
	
					} else {
						sendUPOSMsg.setPromptDiscount_yn("0");
					}
			  }
		  }
		  
		  sendUPOSMsg.setTaxFreeCust_type(uPosMsg.getTaxFreeCust_type()) ;
		
		  Preamble preamble = PumpMUtil.createUPOSMessagePreamble(syncUnique, 
				  SyncManager.DISE_PUMP_MODULE, 
				  SyncManager.DISE_CMS_MODULE, 
				  sendUPOSMsg, "") ;
		  
		  if ((!sendUPOSMsg.getLed_code().equals(IUPOSConstant.LEDCODE_0)) && (sendUPOSMsg.getPosReceipt_no().startsWith(IConstant.PREFIX_K))) {
				  // �����ǿ� ���� ���� ���� ���θ� POS �� �뺸
				  sendLockingInfoToPOS(uPosMsg.getNozzle_no(), sendUPOSMsg.getPosReceipt_no(), IConstant.PUMP_SALE_LOCKING) ;
		  }
		  LogUtility.getPumpMLogger().info("[Pump M] Send Pump Info to CAT M.") ;
		  sendMessage(preamble) ;
		  
	  } else if (custCard_car_type.equals(IUPOSConstant.CUSTCARD_CAR_TYPE_1) || custCard_car_type.equals(IUPOSConstant.CUSTCARD_CAR_TYPE_3)) {
		  // �ŷ�ó��/������ ���� (0:���� 1:�ŷ�óī��� ����������û 2:���������ȣ��ȸ 3: ������ȣ�� ����������û 
		  //                                     4:����ȭ���ŷ�ó��ȸ, 5:���������ŷ�ó ��ȸ)
		  // �� ī�� ���� ��û
		  LogUtility.getPumpMLogger().info("[Pump M] Receive Customer processing from CAT M.") ;
		  
		  // ���� ������ �ִ��� ���� Ȯ��.
		  UPOSMessage_ItemInfo_Item itemInfoItem = null ;
		  	  
		  if (messageType == IUPOSConstant.MESSAGETYPE_INT_4201) {
			  itemInfoItem = UPOSUtil.getUPOSMessage_ItemInfo_Item(uPosMsg.getNozzle_no(),
					  false, 
					  uPosMsg.getPump_amt(), 
					  uPosMsg.getPump_qty()) ;
		  } else if(uPosMsg.getMessageType().equals(IUPOSConstant.MESSAGETYPE_4291)){
			  itemInfoItem = 
					UPOSUtil.getUPOSMessage_ItemInfo_Item(uPosMsg.getNozzle_no(),
							true, 
							uPosMsg.getPump_amt(), 
							uPosMsg.getPump_qty()) ;
			  			  
		  } else if(messageType == IUPOSConstant.MESSAGETYPE_INT_4301){
			  //Led_code  V: Preset ����, W:�����
				String pump_amt = uPosMsg.getPump_amt();
				String nozzle_no = uPosMsg.getNozzle_no();
				// ������ �������� üũ
				if(UPOSUtil.isReadyForPreset(nozzle_no)){
					if(!GlobalUtility.isNullOrEmptyString(pump_amt) && Integer.parseInt(pump_amt) > 0){
						itemInfoItem = UPOSUtil.getUPOSMessage_ItemInfo_Item(uPosMsg.getNozzle_no(), uPosMsg.getPump_amt(), uPosMsg.getPump_qty());
						uPosMsg = UPOSUtil.process4301(uPosMsg, itemInfoItem);
						uPosMsg.setLed_code(IUPOSConstant.LEDCODE_V);
						uPosMsg.setFiller6(Barcode.getBarcodeNumber("6", pump_amt, uPosMsg.getNozzle_no(), uPosMsg.getPosReceipt_no(), uPosMsg.getMessageType(), uPosMsg.getLed_code(), null));
						PumpMTransactionManager.getInstance().setPresetInfo(uPosMsg.getNozzle_no(), IPumpConstant.PRESET_FROM_CAT) ;
					}else{
						itemInfoItem = null;
						uPosMsg = UPOSUtil.process4301(uPosMsg, itemInfoItem);
						uPosMsg.setLed_code(IUPOSConstant.LEDCODE_W);
					}
				}else{
					itemInfoItem = null;
					uPosMsg = UPOSUtil.process4301(uPosMsg, itemInfoItem);
					uPosMsg.setLed_code(IUPOSConstant.LEDCODE_W);
				}
		  } else { // IUPOSConstant.MESSAGETYPE_INT_4291 case only
			  itemInfoItem = 
					UPOSUtil.getUPOSMessage_ItemInfo_Item(uPosMsg.getNozzle_no(),true,
							uPosMsg.getPump_amt(), uPosMsg.getPump_qty()) ;
		  }
		  
		  UPOSMessage sendUPOSMsg = null ;
		  
		  if (messageType == IUPOSConstant.MESSAGETYPE_INT_4201) {
			  sendUPOSMsg = UPOSUtil.process4201_4291(uPosMsg , itemInfoItem, false) ;
		  } else if(messageType == IUPOSConstant.MESSAGETYPE_INT_4301){
			  sendUPOSMsg = uPosMsg;
		  }else {
			  sendUPOSMsg = UPOSUtil.process4201_4291(uPosMsg , itemInfoItem, true) ;
		  }

		  if (itemInfoItem == null) {
			  // ���� ������ ���� ������ Campaign �� ������ �ʿ䰡 ����.
			  Preamble preamble = PumpMUtil.createUPOSMessagePreamble(	syncUnique, 
					  SyncManager.DISE_PUMP_MODULE, 
					  SyncManager.DISE_CMS_MODULE, 
					  sendUPOSMsg, 
					  "") ;		    				
			  LogUtility.getPumpMLogger().info("[Pump M] No Pump Info in table. Send that to CAT M.") ;
			  sendMessage(preamble);
			  return;
		  }

		  uposCargoHash.put(uPosMsg.getNozzle_no(), sendUPOSMsg);
		  
//		  LogUtility.getPumpMLogger().debug("[Pump M] ���� ������ �ֽ��ϴ�. ������ ��� �����մϴ�.") ;
		  sendLockingInfoToPOS(uPosMsg.getNozzle_no(), sendUPOSMsg.getPosReceipt_no(), IConstant.PUMP_SALE_LOCKING) ;
		  
		  String messageID = GlobalUtility.getUniqueMessageID() ;
		  String nozzle_no = uPosMsg.getNozzle_no() ;
		  String cust_card_ind = "" ;	// ������ȣ/�ŷ�óī���ȣ���� (01:������ȣ , 02:�ŷ�óī���ȣ)
		  String cust_card_no = uPosMsg.getCustCard_No() ;
		  String taxFreeCust_type = uPosMsg.getTaxFreeCust_type() ;
		  String fixedQty_yn = uPosMsg.getFixedQty_yn() ;
		  String fixedQty = GlobalUtility.getDividedWith1000(GlobalUtility.getStringValue(uPosMsg.getFixedQty())) ;
		  String goods_code = itemInfoItem.getGoodsCode() ;
		  String basePrice = GlobalUtility.getDividedWith1000(itemInfoItem.getUnitPrice_before_discount()) ;
		  String liter = GlobalUtility.getDividedWith1000(itemInfoItem.getOilAmount()) ;
		  String price = itemInfoItem.getOilPrice_before_discount() ;
		  String khTransactionID = itemInfoItem.getKhTransactionID() ;
		  // ��ī����� ��û ����(4291)�� �ŷ�ó�ڵ�(filler2) �߰��� ���� ���� 2009.10.27 edited by ykjang
		  //String cust_code = uPosMsg.getFiller2();
		  // PI2, ������ ������ ���� ���� Upos Message ���� - ���ʷ� ����(�������� Ȯ��), 2015.11.19 - cwi
		  String cust_code = uPosMsg.getCust_code(); // �ŷ�ó �ڵ�
		  
		  if (custCard_car_type.equals(IUPOSConstant.CUSTCARD_CAR_TYPE_1)) {
			  cust_card_ind = ICode.CUST_CARD_IND_02 ;
		  } else {
			  cust_card_ind = ICode.CUST_CARD_IND_01 ;
		  }
		  
		  // ���������� �ƴ��� �����ؼ� POS�� ������. POS�䱸����(����������) - upkoo		  
		  if (messageType == IUPOSConstant.MESSAGETYPE_INT_4201) {
			  isPumping = ICode.DX_ISPUMPING_IND_1;
		  } 
		  
		  try {
			  POS_DX dxPumpM = new POS_DX(messageID, 
						nozzle_no, 
						cust_card_ind,
						cust_card_no, 
						taxFreeCust_type,
						fixedQty_yn,
						fixedQty,
						goods_code, 
						basePrice, 
						liter, 
						price, 
						khTransactionID, 
						isPumping,
						cust_code) ;
					
			  Preamble preamble = PumpMUtil.createPOSMessagePreamble(syncUnique, SyncManager.DISE_POS_ADAPTER, dxPumpM, "") ;					
			  PumpMSyncManager.setSyncData(syncUnique, messageID, SyncManager.DISE_PUMP_MODULE, receiving_catm) ;
			  LogUtility.getPumpMLogger().info("[Pump M] Send Request of Customer Processing to POS.") ;	
			  // �ŷ�ó ī�� ��û�� POS �� �����Ͽ�, POS �� ���� ������ �޵��� �Ѵ�.
			  sendMessage(preamble) ;
		  } catch (Exception e) {
			  LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		  }
	  } else if (custCard_car_type.equals(IUPOSConstant.CUSTCARD_CAR_TYPE_4) ) {
		  // �ŷ�ó��/������ ���� (0:���� 1:�ŷ�óī��� ����������û 2:���������ȣ��ȸ 3: ������ȣ�� ����������û 
		  //                                     4:����ȭ���ŷ�ó��ȸ, 5:���������ŷ�ó ��ȸ)
		  // ����ȭ�� ó�� (�ŷ�ó ī�� + ����ȭ�� ���ī�� + �ſ�ī��(ȭ�������� ����ī�� / �ŷ�ī��)
		  LogUtility.getPumpMLogger().info("[Pump M] Receive Cargo processing from CAT M.") ;
		  
		  processEtcCust(uPosMsg, messageType, syncUnique, receiving_catm);
		  
	  } else if (custCard_car_type.equals(IUPOSConstant.CUSTCARD_CAR_TYPE_5) ) {
		  // �ŷ�ó��/������ ���� (0:���� 1:�ŷ�óī��� ����������û 2:���������ȣ��ȸ 3: ������ȣ�� ����������û 
		  //                                     4:����ȭ���ŷ�ó��ȸ, 5:���������ŷ�ó ��ȸ)
		  // �������� �ŷ�ó ó��
		  LogUtility.getPumpMLogger().info("[Pump M] Receive OIL Price Support processing from CAT M.") ;
		  
		  processEtcCust(uPosMsg, messageType, syncUnique, receiving_catm);
		  
	  }else {
		  // �� ���� ��ȸ ��û
		  LogUtility.getPumpMLogger().info("[Pump M] Receive Car Inquiry from CAT M.") ;
		  String messageID = GlobalUtility.getUniqueMessageID() ;
		  String nozzle_no = uPosMsg.getNozzle_no() ;
		  String car_short_no = uPosMsg.getCustCard_No() ;
								
		  try {

			  PumpMSyncManager.setSyncData(syncUnique, messageID, SyncManager.DISE_PUMP_MODULE, receiving_catm) ;
					
			  POS_DT dtPumpM = new POS_DT(messageID, nozzle_no, car_short_no) ;
			  Preamble preamble = PumpMUtil.createPOSMessagePreamble(syncUnique, SyncManager.DISE_POS_ADAPTER, dtPumpM, "") ;
			  LogUtility.getPumpMLogger().info("[Pump M] Send CAR Inquiry to POS A.") ;
			  // �� ���� ��ȸ ��û�� POS �� �����Ͽ� ������ �޵��� �Ѵ�.
			  sendMessage(preamble) ;					
		  } catch (Exception e) {
			  LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		  }   		  
	  }
    }

    /**
     * ����ȭ�� �� �������� �ŷ� ó�� ���μ���
     * @param uPosMsg
     * @param messageType
     * @param syncUnique
     * @param receiving_catm
     */
    private void processEtcCust( UPOSMessage uPosMsg, int messageType, String syncUnique, Object receiving_catm){
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "CatMController/" + "processEtcCust()");
		
//    	 ���� ������ �ִ��� ���� Ȯ��.
		  UPOSMessage_ItemInfo_Item itemInfoItem = null ;
		  
		  UPOSMessage sendUPOSMsg = null ;
		  String isPumping = "";
		  
		  sendUPOSMsg = uposCargoHash.remove(uPosMsg.getNozzle_no());
		  
		  if (sendUPOSMsg == null) {
			  if (messageType == IUPOSConstant.MESSAGETYPE_INT_4201) {
				  sendUPOSMsg = UPOSUtil.process4201_4291(uPosMsg , itemInfoItem, false) ;
			  } else  if (messageType == IUPOSConstant.MESSAGETYPE_INT_4301) {
				  sendUPOSMsg = UPOSUtil.process4301(uPosMsg , itemInfoItem) ;
			  }else {
				  sendUPOSMsg = UPOSUtil.process4201_4291(uPosMsg , itemInfoItem, true) ;
			  }
			  
			  // ���� ������ ���� ������ Campaign �� ������ �ʿ䰡 ����.
			  Preamble preamble = PumpMUtil.createUPOSMessagePreamble(syncUnique, SyncManager.DISE_PUMP_MODULE, 
					  SyncManager.DISE_CMS_MODULE, sendUPOSMsg, "") ;		    				
			  LogUtility.getPumpMLogger().info("[Pump M] No Pump Info in table. Send that to CAT M.") ;
			  sendMessage(preamble) ;		
			  return ;
		  }
		  
		  itemInfoItem = sendUPOSMsg.getItem_info().getItemInfoList().get(0);
		  
//		  ����ȭ���� ��� ���û�ϱ� ������ �ι�° ��û�� ���ؼ��� locking�� �������� �ʴ´�.
		  String messageID = GlobalUtility.getUniqueMessageID() ;
		  String nozzle_no = uPosMsg.getNozzle_no() ;
		  String cust_card_ind = "" ;	// ������ȣ/�ŷ�óī���ȣ���� (01:������ȣ , 02:�ŷ�óī���ȣ)
		  String cust_card_no = uPosMsg.getCustCard_No() ;
		  String taxFreeCust_type = uPosMsg.getTaxFreeCust_type() ;
		  String fixedQty_yn = uPosMsg.getFixedQty_yn() ;
		  String fixedQty = GlobalUtility.getDividedWith1000(GlobalUtility.getStringValue(uPosMsg.getFixedQty())) ;
		  String goods_code = itemInfoItem.getGoodsCode() ;
		  String basePrice = GlobalUtility.getDividedWith1000(itemInfoItem.getUnitPrice_before_discount()) ;
		  String liter = GlobalUtility.getDividedWith1000(itemInfoItem.getOilAmount()) ;
		  String price = itemInfoItem.getOilPrice_before_discount() ;
		  String khTransactionID = itemInfoItem.getKhTransactionID() ;
		  // ��ī����� ��û ����(4291)�� �ŷ�ó�ڵ�(filler2) �߰��� ���� ���� 2009.10.27 edited by ykjang
		  // String cust_code = uPosMsg.getFiller2();	
		  // String cust_code = uPosMsg.getTradeCondition().getContents(); // �ŷ�ó �ڵ�
		  String cust_code = uPosMsg.getCust_code(); // �ŷ�ó �ڵ�
		  
		  // �ŷ�óī�尡 ���� ��� �ŷ�óī�� �ʵ忡 ������ȣ�� ����. �̷� ���� ���� �����ȣ�� ��ȸ
		  if (cust_card_no.length() < 16){
			  cust_card_ind = ICode.CUST_CARD_IND_01 ;
		  } else {
			  cust_card_ind = ICode.CUST_CARD_IND_02 ;
		  }
		  
		  // ���������� �ƴ��� �����ؼ� POS�� ������. POS�䱸����(����������) - upkoo
		  if (messageType == IUPOSConstant.MESSAGETYPE_INT_4201) {
			  isPumping = ICode.DX_ISPUMPING_IND_1 ;
		  } 
		  
		  try {
			  POS_DX dxPumpM = new POS_DX(messageID, 
					  nozzle_no, 
					  cust_card_ind,
					  cust_card_no, 
					  taxFreeCust_type,
					  fixedQty_yn,
					  fixedQty,
					  goods_code, 
					  basePrice, 
					  liter, 
					  price, 
					  khTransactionID, 
					  isPumping,
					  cust_code) ;
					
			  Preamble preamble = PumpMUtil.createPOSMessagePreamble(syncUnique,  
					  SyncManager.DISE_POS_ADAPTER, dxPumpM, "") ;	
			  
			  PumpMSyncManager.setSyncData(syncUnique, 
					  messageID, SyncManager.DISE_PUMP_MODULE, receiving_catm) ;
			  
			  LogUtility.getPumpMLogger().info("[Pump M] Send Request of Customer Processing to POS.") ;	
			  // �ŷ�ó ī�� ��û�� POS �� �����Ͽ�, POS �� ���� ������ �޵��� �Ѵ�.
			  sendMessage(preamble) ;
		  } catch (Exception e) {
			  LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		  }
    }
    

    
	/**
	 * CAT���� ���� Preset ������ �޾Ƽ� ������ �����Ѵ�. 
	 * 1. T_KH_PUMP_TR Table update 
	 * 2. POS �� Preset ���� ����
	 * 
	 * @param uniqueKey :
	 *            Preamble Object �� Unique Key
	 * @param messageID :
	 *            CAT���� ���� �� ���� Message ID
	 * @param pbWorkMsg :
	 *            CAT���η� ���� �� ����
	 */
	
	private void processHF(String uniqueKey, String messageID, String khproc_no, PB_WorkingMessage pbWorkMsg) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/" + "processHF()");
		String commandID = null;

		String type = "0" ;
		String price = "0" ;
		String liter = "0" ;
		price = PumpMUtil.convertPriceFromPumpToPOS(pbWorkMsg.getPrice()) ;
		String base_price = PumpMUtil.convertBasePriceFromPumpToPOS(pbWorkMsg.getBasePrice()) ;
		String nozzleNo = pbWorkMsg.getNozzleNo();	
		
		POS_DQ dqPumpMMsg = new POS_DQ(messageID, nozzleNo, khproc_no, type, price, liter, base_price);
		
		if (khproc_no == null) {
			khproc_no = PumpMTransactionManager.getInstance().getKHTransactionID(pbWorkMsg.getNozzleNo(), IPumpConstant.KH_PUMP_PRESET);
			commandID = IConstant.POSPROTOCOL_COMMANDID_DQ;
		}

		dqPumpMMsg.setTransactionID(khproc_no);
		dqPumpMMsg.setCommandID(commandID);

		String preset_qty_prc_ind = dqPumpMMsg.getType();
		String preset_qty = dqPumpMMsg.getLiter();
		String preset_prc = dqPumpMMsg.getPrice();
		String preset_basePrice = dqPumpMMsg.getBase_price();
		try {
			T_KH_PUMP_TRHandler.getHandler().updatePresetInfo_BY_khproc_no(khproc_no, 
					preset_qty_prc_ind, 
					preset_qty, 
					preset_prc,
					preset_basePrice);
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		}
		Preamble dqPreamble = PumpMUtil.createPOSMessagePreamble(null,	SyncManager.DISE_POS_ADAPTER, dqPumpMMsg, "");

		try {
			// 2009�� 4�� 21�� �߿��� �߰�.
			GlobalUtility.printAnalysisLog("pumpM", khproc_no, pbWorkMsg.getNozzleNo(), "", "������", "", dqPumpMMsg.convertPOSContent(), "", "", "");

		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(	"[Pump M] !!!!!!!!!!AnalysisLOG ��� ����!!!!!!!!!!!", e);
		} // end try

		sendMessage(dqPreamble);
	}

	/**
     * ksm 2011.12.13 PumpAController�� EN���� ���� �� SEQ DB Insert/Update ������.
     * twsongkis 2016. 4. 14. ���� 15:48:31, PI2, songkis,  DB Insert/Update ���̺� ����.
     * ���ϼ������� �̸������� POS�� EN���� ������ �ȵȴٰ� ���� ��û��. (�븮�� ����1�� ������ ���� ��û CSR)
     * @param workMsg
     * @param khproc_no
     */
	private void processOPTBarcode(WorkingMessage workMsg, String khproc_no) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/" + "processOPTBarcode()");

		QL_WorkingMessage ql_WMsg = (QL_WorkingMessage) workMsg;
		if (!ql_WMsg.getBarCode().equals(""))
		{
			String getMessageID = GlobalUtility.getUniqueMessageID();
			try {
				POS_EN enPumpMMsg = new POS_EN(getMessageID, khproc_no, ql_WMsg, 
						T_KH_STOREHandler.getHandler().getWorkingDate());
				
				Preamble preambleM = PumpMUtil.createPOSMessagePreamble(GlobalUtility.getUniqueMessageID(), SyncManager.DISE_SALE_MODULE, enPumpMMsg, "") ;
				LogUtility.getPumpMLogger().info("[Pump M] Send EN Content to Sale M") ;
				sendMessage(preambleM) ;	
			} catch (Exception e) {
				LogUtility.getPumpMLogger().error(e.getMessage(),e);
			}
			
			
			T_KH_CARWASH_COUPON_LISTData carwash_coupon_listSLT = new T_KH_CARWASH_COUPON_LISTData();
			T_KH_CARWASH_COUPON_LISTData carwash_coupon_list = new T_KH_CARWASH_COUPON_LISTData();
			String qlBarcodeStr = ql_WMsg.getBarCode(); // �ý���(X)��ȿ����(YMMDD)�������αݾ�(XX)SEQ(XXXX)��������ڵ�(XXXX)
			
			carwash_coupon_list.setBarcode(qlBarcodeStr);
			String creation_time = GlobalUtility.getDateYYYYMMDDHHMMSS();
			carwash_coupon_list.setCreation_time(creation_time) ;
			String disc_amt = qlBarcodeStr.substring(6, 8);
			carwash_coupon_list.setDisc_amt(disc_amt);
			carwash_coupon_list.setUse_yn("N");
			carwash_coupon_list.setKhproc_no(khproc_no);
			
			LogUtility.getPumpMLogger().debug("[Pump M] CL_CARWASH_COUPON_LIST Insert : " + qlBarcodeStr + "/" + creation_time + "/" + disc_amt);
			SqlSession session = null;
			try {
				session = SqlSessionFactoryManager.openSqlSession();
				
				// insert to database
				carwash_coupon_listSLT = T_KH_CARWASH_COUPON_LISTHandler.getHandler().getT_T_KH_CARWASH_COUPON_LISTData(session, qlBarcodeStr);
				if (carwash_coupon_listSLT != null)
				{
					//�μ�Ʈ 
					String max_seq = T_KH_CARWASH_COUPON_LISTHandler.getHandler().getMaxSeq(qlBarcodeStr);
					if("99".equals(max_seq)){
						carwash_coupon_list.setSeq(max_seq);
						LogUtility.getPumpMLogger().debug("[Pump M] updateT_KH_CARWASH_COUPON_LIST - " + carwash_coupon_listSLT);
						T_KH_CARWASH_COUPON_LISTHandler.getHandler().updateT_KH_CARWASH_COUPON_LISTData(session, carwash_coupon_list);
					}else{
						max_seq =  (Integer.parseInt(max_seq) + 1) + "";
						carwash_coupon_list.setSeq(max_seq);
						LogUtility.getPumpMLogger().debug("[Pump M] updateT_KH_CARWASH_COUPON_LIST - " + carwash_coupon_listSLT);
						T_KH_CARWASH_COUPON_LISTHandler.getHandler().insertT_KH_CARWASH_COUPON_LISTData(session, carwash_coupon_list);
					}
				} else {
					//�μ�Ʈ 
					carwash_coupon_list.setSeq("00");
					LogUtility.getPumpMLogger().debug("[Pump M] insertT_KH_CARWASH_COUPON_LIST - " + carwash_coupon_listSLT);
					T_KH_CARWASH_COUPON_LISTHandler.getHandler().insertT_KH_CARWASH_COUPON_LISTData(session, carwash_coupon_list);
					
				}
				session.commit();
			} catch (Exception e) {
				LogUtility.getPumpMLogger().error(e.getMessage(),e);;
				session.rollback();
			} finally {
				SqlSessionFactoryManager.closeSqlSession(session);
			}
		}
	}    
    
	/**
	 * POS�� �ܸ��� ���� ���� ����
	 * @param crt_no_seq
	 */
	private void sendCatVersion(String crt_no_seq, String version) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "CatMController/" + "sendCatVersion()");
		
		POSHeader posheader = new POSHeader(IConstant.POSPROTOCOL_SYSTEM_KH, 
				IConstant.POSPROTOCOL_SYSTEM_POS, 
				IConstant.POSPROTOCOL_COMMANDID_HJ, 
				IConstant.POSPROTOCOL_TYPE_NOZZLE, 
				"0001", 
				GlobalUtility.getDateYYYYMMDDHHMMSS(),
				GlobalUtility.getUniqueMessageID());
		

		String body = crt_no_seq + POSHeader.DELIMITER_STRING + version;
		
		byte[] bytePreamble = posheader.mergeHeaderBody(posheader.convertHeaderToPOSContentWithoutDataLength(), body.getBytes());
		
		Preamble preamble = Preamble.createPreamble(GlobalUtility.getUniqueMessageID(), 
				SyncManager.DISE_PUMP_MODULE, 
				SyncManager.DISE_POS_ADAPTER, 
				bytePreamble, 
				"" );
		sendMessage(preamble);
		posheader = null;
	}

	/**
     * Locking , unLocking ������ POS �� �����Ѵ�.
     * 
     * @param nozID				: ������ �����ϴ� ���� ��ȣ
     * @param khTransactionNum	: KH ó�� ��ȣ
     * @param isLock			: Locking or unLocking
     */
    private void sendLockingInfoToPOS(String nozID, String khTransactionNum, String isLock) {
  	  if (isLock.equals(IConstant.PUMP_SALE_LOCKING)) {
  		  LockingManager.locking(nozID, khTransactionNum, IConstant.LOCKING_SRC_CAT, true) ;
  	  } else {
  		  LockingManager.unlocking(nozID, khTransactionNum, true) ;
  	  }
    }
    
    
    /**
     * 
     * CAT ���κ��� ��û Ȥ�� ���� ���� �����͸� �ٸ� ��⿡�� ���۽� ���.
     * �� ��� �� ����Ϳ��� ������ �������� Ÿ���� �Ʒ��� ����.
     * 
     * 		POS Adapter		: Preamble		byte[]
     * 		Pump Adapter	: PumpPreamble	WorkingMessage
     * 		CAT Module		: Preamble		byte[]
     * 		State Module	: Preamble		WorkingMessage
     * 		Sale Module		: Preamble		byte[]
     * 
     * @param preambleData	: ������ ������
     */
    private void sendMessage(Preamble preambleData) {
    	int dest = preambleData.getDest() ;
    	String	pi2_phase	=	PropertyManager.getSingleton().getProperty("kixxhub.pi2.deploy.phase", "2").trim(); 
    	
    	switch (dest) {
	    	case SyncManager.DISE_POS_ADAPTER :
	    		getProducer_PumpMPosA_Data().produce(preambleData) ;
	    		break ; 
	    	case SyncManager.DISE_PUMP_ADAPTER :
	    		getProducer_PumpMPumpA_Data().produce(preambleData) ;
	    		break ; 
	    	/* 2016. 4. 14. ���� 15:48:31, PI2, WooChul Jung. Add comment
	    	 * 	The reason why the message to set CMS_MODULE is sent to CAT_MODULE
	    	 * 		Campaign logic is handled in CMS_MODULE, and CMS_MODULE is connected by CAT_MODULE
	    	 * 		So if CAT device requests the Pump Information, the flow is like below.
	    	 * 			CAT Device -> CAT A -> CAT M -> Pump M (Filling Pump Info) -> CAT M -> CMS M (Filling campaign) -> CAT M -> CAT A -> CAT Device
	    	*/	
	    	case SyncManager.DISE_CMS_MODULE :
	    	case SyncManager.DISE_CAT_MODULE : {
	    		handleBeforeSendingMsgToCATM(preambleData) ;
	    		
	        	UPOSMessage uPosMsg = (UPOSMessage) preambleData.getPreamble() ;
	        	
	        	// 2016-08-16 twlee 
	        	// ��û������ selfPayment_Type ���� ���� ���������� �߰��ϱ� ���� selfPayment_typeHash�� ������ ����
	        	PumpMODTSaleManager.setNozzleBySelfPayment_type(uPosMsg.getNozzle_no(), uPosMsg.getSelfPayment_type());
	        	
	        	// 2015-12-17, PI2, cwi, ������ ķ���� ������ �ٽ� PUMPA�� ����,
	        	// ODT�� CAT�� ��û�� ���� �������� �ٸ��� �ϱ� ���� ODT���� ���� ������ ��� DeviceType �� MessageType�� ������ �ɾ� �з� �۾� ����
	        	if(uPosMsg != null && (
	        			IUPOSConstant.MESSAGETYPE_4202.equals(uPosMsg.getMessageType())
	        				&& uPosMsg.getDeviceType().equals(IUPOSConstant.DEVICE_TYPE_3S))){  
	    			
	        		// 2016.01.12, PI2, songkis, ���ʽ�ī���ȣ�� ���� ��� CMS M(����ķ����, �������ο���Ȯ��, �ѵ���ȸ)�� �� �ʿ䰡 ����.
	        		// 2016.02.16, PI2, cwi, ODT�� ���� �ö�� PromptDiscount_yn���� 0�� ��� �������� ������ ������� �ν�
	    			if( pi2_phase.equals("1") && (GlobalUtility.isNullOrEmptyString(uPosMsg.getBonRSCard_no()) || uPosMsg.getPromptDiscount_yn().equals("0"))) {
	    				
		    			if( GlobalUtility.isNullOrEmptyString(uPosMsg.getBonRSCard_no()))
		    					LogUtility.getLogger().debug("[Pump M] ���ʽ�ī���ȣ�� ���� ��� CMSM(����ķ����, �������ο���Ȯ��, �ѵ���ȸ) ���� �� �ʿ䰡 ����.") ;		
		    				else
		    					LogUtility.getLogger().debug("[Pump M] �������� ���ΰ� 0�� ��� CMSM(����ķ����, �������ο���Ȯ��, �ѵ���ȸ) ���� �� �ʿ䰡 ����.") ;
	    				
		    			// PumpA�� ����
		    	        GB_WorkingMessage gbWirking = new GB_WorkingMessage();

		    	        // PumpA������ WorkingMessage�� ������ �����ϱ⿡ GB������ UPOSMessage ��� ���� ��.
		    	        gbWirking.setMessageType(uPosMsg.getMessageType());    // ���� �������� ���� �� ����
		    	        gbWirking.setNozzleNo(PumpMUtil.getODTNumberFromNozzleNo(uPosMsg.getNozzle_no())); // ODT�ѹ� ����
		    	        gbWirking.setConnectNozzleNo(uPosMsg.getNozzle_no());
		    	        gbWirking.setUnityMessage(uPosMsg); //Upos Message ���
		    	        
		    	        // PumpA�� ���� �ǵ��� ����ó�� PumpA�� ����
		    	        preambleData.setDest(SyncManager.DISE_PUMP_ADAPTER);
		    	        preambleData.setPreamble(gbWirking);
		    	        
		    	        LogUtility.getLogger().info("[Pump M] PumpA���� ����") ;
		    	        getProducer_PumpMPumpA_Data().produce(preambleData) ;
	    			
	    			}else if("2".equals(pi2_phase) && ( GlobalUtility.isNullOrEmptyString(uPosMsg.getBonRSCard_no()) || "0".equals(uPosMsg.getPromptDiscount_yn()))){
	    				if( GlobalUtility.isNullOrEmptyString(uPosMsg.getBonRSCard_no()))
	    					LogUtility.getLogger().debug("[Pump M] ���ʽ�ī���ȣ�� ���� ��� CMSM(����ķ����, �������ο���Ȯ��, �ѵ���ȸ) ���� �� �ʿ䰡 ����.") ;		
	    				else
	    					LogUtility.getLogger().debug("[Pump M] �������� ���ΰ� 0�� ��� CMSM(����ķ����, �������ο���Ȯ��, �ѵ���ȸ) ���� �� �ʿ䰡 ����.") ;
    				
		    			// PumpA�� ����
		    	        GB_WorkingMessage gbWirking = new GB_WorkingMessage();
	
		    	        // PumpA������ WorkingMessage�� ������ �����ϱ⿡ GB������ UPOSMessage ��� ���� ��.
		    	        gbWirking.setMessageType(uPosMsg.getMessageType());    // ���� �������� ���� �� ����
		    	        gbWirking.setNozzleNo(PumpMUtil.getODTNumberFromNozzleNo(uPosMsg.getNozzle_no())); // ODT�ѹ� ����
		    	        gbWirking.setConnectNozzleNo(uPosMsg.getNozzle_no());
		    	        gbWirking.setUnityMessage(uPosMsg); //Upos Message ���
		    	        
		    	        // PumpA�� ���� �ǵ��� ����ó�� PumpA�� ����
		    	        preambleData.setDest(SyncManager.DISE_PUMP_ADAPTER);
		    	        preambleData.setPreamble(gbWirking);
		    	        
		    	        LogUtility.getLogger().info("[Pump M] Send UPOSMessage to PUMP A") ;
		    	        getProducer_PumpMPumpA_Data().produce(preambleData) ;
	    			}else if("2".equals(pi2_phase) && ( !GlobalUtility.isNullOrEmptyString(uPosMsg.getBonRSCard_no()) || uPosMsg.getPromptDiscount_yn().equals("1"))){
	    				// CatM���� ����
	    				LogUtility.getLogger().info("[Pump M] Send UPOSMessage to CAT M") ;
	    				getProducer_CatM_Data().produce(preambleData) ;
	    			}
	    			
	    			 /**
					 * ������Ʈ : PI2
					 * ���泻�� : ������ ODT ķ���� �������� ��û ���� ( �ű� )
					 * �������� : 2015.12.23 ������
					 */ 
		    		} else if(uPosMsg.getMessageType().equals(IUPOSConstant.MESSAGETYPE_4292) 
		    				&& uPosMsg.getDeviceType().equals(IUPOSConstant.DEVICE_TYPE_3O)){
		    			if(pi2_phase.equals("1") || uPosMsg.getPromptDiscount_yn().equals("0")) {  		    				
		    				// PumpA�� ����
		        	        SL_WorkingMessage slWorking = new SL_WorkingMessage();

		        	        // PumpA������ WoringMessage�� ������ �����ϱ⿡ SL������ UPOSMessage ��� ���� �Ѵ�.
		        	        slWorking.setMessageType(uPosMsg.getMessageType());    // ���� �������� ���� �� ����
		        	        slWorking.setNozzleNo(PumpMUtil.getODTNumberFromNozzleNo(uPosMsg.getNozzle_no())); // ODT�ѹ� ����
		        	        slWorking.setConnectNozzleNo(uPosMsg.getNozzle_no());
		        	        slWorking.setUnityMessage(uPosMsg); //Upos Message ���
		        	        
		        	        // PumpA�� ���� �ǵ��� ����ó�� PumpA�� ����
		        	        preambleData.setDest(SyncManager.DISE_PUMP_ADAPTER);
		        	        preambleData.setPreamble(slWorking);
		        	        
		        	        LogUtility.getLogger().info("[Pump M] Send UPOSMessage to PUMP A") ;
		        	        
		        	        //uPosMsg.print();
		        	        getProducer_PumpMPumpA_Data().produce(preambleData) ;
		        	        
		    			} else {
		    				// CatM���� ����
		    				LogUtility.getLogger().info("[Pump M] Send UPOSMessage to CAT M") ;
		    				getProducer_CatM_Data().produce(preambleData) ;
			    			
		    		}
	    		} else {
	    				// CatM���� ����
		    			LogUtility.getLogger().info("[Pump M] Send UPOSMessage to CAT M") ;
		    			getProducer_CatM_Data().produce(preambleData) ;
	    			}
	    		break ; 
	    	}
	    	case SyncManager.DISE_STATE_MODULE :
	    		break ;
	    	case SyncManager.DISE_SALE_MODULE : {
	    		getProducer_PumpMSaleM_Data().produce(preambleData) ;
	    		break ; 
	    	}
	    	case SyncManager.DISE_BEACON_MODULE :{
	    		getProducer_BeaconM_Data().produce(preambleData) ;
	    		break ;
	    	}
    	}
    }

    /**
     * ODT �� ���� �߻��� ��û�� ���� ���� ������ Pump A �� Sales M �� �����Ѵ�.
     * 
     * @param workingMsgArray		: Pump A �� ������ ���� ����
     * @param uPosMsg				: Sales M(�ᱹ POS) ���� ������ ���� ����
     */
    public void sendRespondingMsgToPumpA(ArrayList<WorkingMessage> workingMsgArray, UPOSMessage uPosMsg) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "CatMController/" + "sendRespondingMsgToPumpA()");
		    	
    	// Pump A �� WorkingMessage �� �����Ѵ�.
    	if ((workingMsgArray != null) && (workingMsgArray.size() != 0)) {
    		for (int i = 0 ; i < workingMsgArray.size() ; i++) {
    			Preamble pumpPreamble = PumpMUtil.createWorkingMessagePreamble(null,
    					SyncManager.DISE_PUMP_ADAPTER, workingMsgArray.get(i) , "") ;
    			sendMessage(pumpPreamble) ;
    		}
    	}
		
    	// Sale M ���� ���� ���� ������ �����Ѵ�.
    	if (uPosMsg != null) {
			String nozzle_no = uPosMsg.getNozzle_no() ;
			try {
				int nozProtocolInt = PumpMUtil.getConnectedRepODTProtocolFromNozzleNo(nozzle_no) ;
				
				switch (nozProtocolInt) {
					case IPumpConstant.PUMP_PROTOCOL_SOMO :
					case IPumpConstant.PUMP_PROTOCOL_DaSNo : 
					case IPumpConstant.PUMP_PROTOCOL_NewDaSNo :
					case IPumpConstant.PUMP_PROTOCOL_Recharge :
				    	// Sale M ���� ���� ������ �������� ���� �Ǵ��� �Ѵ�.
			    		if (ODTUtility_Common.shouldSendToSaleM(uPosMsg)) {
			    			
			    			//2012.04.23 ksm 
					    	//�������������� LED_CODE=2(����) �̰� Credit_AuthInfo(�ΰ�����)���� "CU"(�ܸ����Ϸù�ȣ�ߺ�)�� ��� ����������ȣ�� 500 ������Ŵ 
					    	if("2".equals(uPosMsg.getLed_code())){
					    		LogUtility.getLogger().info("[Check TrackingNo] uPOS������ LED_CODE �� = 2" ) ;
					    		
					    		// ���������� "CU" �ܸ����Ϸù�ȣ�ߺ��� ��� ����������ȣ ���� ��Ŵ.
					    		if("CU".equals(uPosMsg.getCredit_authInfo())){
					    			LogUtility.getLogger().info("[Check TrackingNo] �ܸ����Ϸù�ȣ �ߺ� �߻�!!!" ) ;
					    			
					    			// ����������ȣ ����
					    			PumpMTransactionManager.getInstance().increaseTrackingValue();
					    		}
					    	}
					    	    			
			    		   	LogUtility.getLogger().info("[Pump M] Send UPOSMessage to SaleM") ;
			    		   	
					    	Preamble posPreamble = PumpMUtil.createUPOSMessagePreamble( null, 
																			 SyncManager.DISE_PUMP_MODULE,
																			 SyncManager.DISE_SALE_MODULE, 
																			 uPosMsg, 
																			 "") ;
					    	sendMessage(posPreamble) ;
			    		}
						break ;
				}
			} catch (Exception e) {
				LogUtility.getLogger().error(e.getMessage(),e) ;
			}
    	}
    }
    
    /**
     * ODT �κ����� ���� ��û�� ���� ���� ������ Pump A �� ������.
     * ���� ������ �پ��� ���� ��û�� ���Ѱ����� �ٸ��� (�Ҹ�, ������)�� ���Ѱ������� ���� Ʋ����.
     * 
     * @param syncUnique	: Unique Key
     * @param uPosMsg		: ���� ����
     */
    private void sendWorkingMessageToPumpA(String syncUnique, UPOSMessage uPosMsg) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "CatMController/" + "sendWorkingMessageToPumpA()");
		    	
    	// PreProcessing ó��
    	ODTUtility_Common.preProcessingUPOSMessageFromCAT(uPosMsg) ;

    	String nozzleNo = uPosMsg.getNozzle_no() ;
		int nozProtocolInt = PumpMUtil.getConnectedRepODTProtocolFromNozzleNo(nozzleNo) ;
		
		// ���������� �����Ѵ�.[2008.11.18]
		PumpMTransactionManager.getInstance().setPayed(uPosMsg.getMessageType(), uPosMsg.getNozzle_no()) ;
		
		switch (nozProtocolInt) {
			case IPumpConstant.PUMP_PROTOCOL_DaSNo : 
			case IPumpConstant.PUMP_PROTOCOL_NewDaSNo :{ 	// 2012.09.26 ksm
				DasNoSelfPumpingManager.getInstance().addRespondUPOSMessage(uPosMsg.getNozzle_no(), uPosMsg) ;

				/**
				 * [2008.11.20] ���� ������ ������ ��츦 ó���ؾ� �Ѵ�. (���� ����, ȸ�� �ҷ�) �� ����� ó�� ��� �ؾ� ��.
				 */

				QL_WorkingMessage qlMsg = null;

				int protocolTypeInt = PumpMUtil.getConnectedODTProtocolFromNozzleNo(nozzleNo)  ;
					
				if (DasNoSelfPumpingManager.getInstance().isPumpingCompleted(nozzleNo)) {
					LogUtility.getPumpMLogger().info("[Pump M] Receive UPOSMessage after Pumping Completed.") ;
					String ledCode = uPosMsg.getLed_code() ;	// �����Ϸ� ���� ����� Ȥ�� ������ ��� ������ ������ ������ ������ ���� Flag
					String msgType = uPosMsg.getMessageType() ;	// ���� ������ ������ ��쿡�� �����̴��� �������� ��� �������� ����ش�.
					String khproc_no = uPosMsg.getPosReceipt_no() ;
					
					// ������ �� ��� && ���� ������ �ƴ� ���
					if (IConstant.reRequest && IUPOSConstant.RESPOND_LEDCODE_2.equals(ledCode) && !isCashYn(msgType)) {						// by ����ȣ
						
						if (protocolTypeInt == 90)
							qlMsg = ODTUtility_GSC_Self.createFailWhenReject(nozzleNo, uPosMsg) ;
						else
							qlMsg = ODTUtility_DaSNo.createFailWhenReject(nozzleNo, uPosMsg) ;
						
						Preamble pumpPreamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_PUMP_ADAPTER, qlMsg , "") ;
   							
						sendMessage(pumpPreamble) ;	
						
						DasNoSelfPumpingManager.getInstance().setFullPumpingStateFinal(nozzleNo) ;
					// TimeOut �� �� ���
					} else if (IConstant.reRequest && IUPOSConstant.RESPOND_LEDCODE_3.equals(ledCode)) {
						// by ����ȣ
						
						//QL_WorkingMessage qlMsg = ODTUtility_DaSNo.createFailWhenTimeOut(nozzleNo, uPosMsg) ;
						if (protocolTypeInt == 90)
							qlMsg = ODTUtility_GSC_Self.createFailWhenReject(nozzleNo, uPosMsg) ;
						else
							qlMsg = ODTUtility_DaSNo.createFailWhenReject(nozzleNo, uPosMsg) ;
						Preamble pumpPreamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_PUMP_ADAPTER, qlMsg , "") ;
   							
						sendMessage(pumpPreamble) ;	
							
						DasNoSelfPumpingManager.getInstance().setFullPumpingStateFinal(nozzleNo) ;
					} else {
						// ���� ������ �������� ���
						// �ܻ� �Ǵ� ���� ������ ���ʽ� ���� �Ǵ� ���� ������ ó���� ���и� �ص� ������ ����� �Ǿߵȴ�.
						if (DasNoSelfPumpingManager.getInstance().shouldCreateQLWorkMsgAndStore(nozzleNo, uPosMsg)) {
							UPOSMessage_ItemInfo_Item itemInfoItem = uPosMsg.getItem_info().getItemInfoList().get(0);
							//String khproc_no = uPosMsg.getPosReceipt_no() ; �Լ� ���� �̵�.
							String pumpingPrice = itemInfoItem.getOilPrice_after_discount() ;
							String pumpingLiter = GlobalUtility.getDividedWith1000(itemInfoItem.getOilAmount()) ;
							String prePayedPrice = "" ;
							if ( isCashYn(msgType) && !uPosMsg.getMessageType().endsWith(IUPOSConstant.MESSAGETYPE_0084))
								prePayedPrice = DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzleNo).getHeWorkingMsg().getCashCount();
							else
								prePayedPrice = itemInfoItem.getOilPrice_after_discount() ;

							String pumpingBasePrice = GlobalUtility.getDividedWith1000(itemInfoItem.getUnitPrice_after_discount()) ; ;
	
							if (protocolTypeInt == 90)
								qlMsg = (QL_WorkingMessage) ODTUtility_GSC_Self.getQL_WorkingMessage_GSCSELFFromODT(nozzleNo, khproc_no, 
										pumpingPrice, pumpingLiter, prePayedPrice, pumpingBasePrice, uPosMsg) ;
							else
								qlMsg = (QL_WorkingMessage) ODTUtility_DaSNo.getQL_WorkingMessage_DaSNoFromODT(nozzleNo, khproc_no, 
									pumpingPrice, pumpingLiter, prePayedPrice, pumpingBasePrice, uPosMsg) ;
								
							// �������� ������ �����Ѵ�.
							DasNoSelfPumpingManager.getInstance().setQLWorkingMessage(nozzleNo, qlMsg) ;
						}
			    		
						//���ʽ� ����, ���� ������, �ܻ󺸳ʽ� ���� �� ���а� ���� POS���� ���� ó���� ���� LED_CODE�� �������(1)���� �����Ѵ�.
						if (isCashYn(msgType) && !IUPOSConstant.RESPOND_LEDCODE_1.equals(ledCode)){
							uPosMsg.setLed_code(IUPOSConstant.RESPOND_LEDCODE_1);
						}
						
						// �ٽ� ���� ��û�ؾ� �� ������ �� �ִ� ���
						UPOSMessage pendingUposMsg = DasNoSelfPumpingManager.getInstance().getPendingUPOSMsgAndRemove(nozzleNo) ;
						if (pendingUposMsg != null) {
							// CAT M ���� �ٽ� ���� ��û�ؾ� �� ������ ���� ��� �����Ѵ�.
							LogUtility.getPumpMLogger().info("[Pump M] Has more UPOSMessage to be sent to CAT M.") ;
							Preamble posPreamble = 
								PumpMUtil.createUPOSMessagePreamble(SyncManager.getUniqueKey(),SyncManager.DISE_PUMP_ADAPTER,SyncManager.DISE_CMS_MODULE,pendingUposMsg,"") ;
							sendMessage(posPreamble) ;
						} else {
							LogUtility.getPumpMLogger().info("[Pump M] Has no UPOSMessage to be sent to CAT M. Send QL Msg to Pump A") ;
			    				
							// ������ ���� �����̸� Pump A �� ���� ������ ã�Ƽ� �������� �Ѵ�.
							qlMsg = DasNoSelfPumpingManager.getInstance().getQLWorkingMessage(nozzleNo) ;
							Preamble pumpPreamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_PUMP_ADAPTER, qlMsg , "") ;
							
							sendMessage(pumpPreamble) ;		
							
							//ksm 2011.12.14 �̸������� EN���� �������� ȣ��.
							processOPTBarcode(qlMsg, khproc_no);
							
							// ���� �ؾ� �� ������ ���ٴ� ���� ������ ���� �����̶�� ���̴�. ���� �Ʒ� �Լ��� ȣ���Ͽ�, ���������� �����Ѵ�.
							DasNoSelfPumpingManager.getInstance().setFullPumpingStateFinal(nozzleNo) ;
						}
					}
					// POS �� ���� ������ �����Ѵ�.
					sendRespondingMsgToPumpA(null, uPosMsg) ;
				} else {
					LogUtility.getPumpMLogger().info("[Pump M] Receive UPOSMessage before Pumping Start.") ;

					DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(
							uPosMsg.getNozzle_no()).setFirstRespondUPOSMsg(uPosMsg) ;
					
					// Pump A �� ������ ���ؼ� WorkingMessage (QM) ������ �����Ѵ�.
					ArrayList<WorkingMessage> workingMsgArray = 
			    		ODTUtility_Common.createWorkingMessageFromUPOSMessage(syncUnique, uPosMsg) ;

					// 4. Pump A �δ� WorkingMessage �� �����ϰ�, Sale M ���δ� ���� ������ ����
					sendRespondingMsgToPumpA(workingMsgArray, uPosMsg) ;
					
					// Pump A �� ������ ���ؼ� WorkingMessage (QL) ������ �����Ѵ�.
					// �������� ���� ���� ��츸 �������� �Ѵ�.
					
					String ledCode = uPosMsg.getLed_code() ;
					if (!IUPOSConstant.RESPOND_LEDCODE_1.equals(ledCode)) {
						if (protocolTypeInt == 90)
							qlMsg = ODTUtility_GSC_Self.createFailWhenReject(nozzleNo, uPosMsg) ;
						else
							qlMsg = ODTUtility_DaSNo.createFailWhenReject(nozzleNo, uPosMsg) ;
						LogUtility.getPumpMLogger().debug("[Pump M] �������� ���� : " + qlMsg);
						Preamble pumpPreamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_PUMP_ADAPTER, qlMsg , "") ;
   							
						sendMessage(pumpPreamble) ;
					}
				}

				break ;
			}
			// ���� ODT ����, PI2, 2016-03-31 - CWI
			// ���������� ó���� ���� �Ѿ�� ������ ODT�� �ѱ��, ODT������ ������ ���� ó�� ���θ� �Ǵ� ��(LED code ���)
			// GA ������ ���� ���� ����� ������ �ش�. ����, PUMP M ������ �Ѿ�� ������ WorkingMessage�� ��ȯ �� ODT�� ���� �ֱ⸸ �Ѵ�.
			case IPumpConstant.PUMP_PROTOCOL_GSC_SELF :{ 	
			
				LogUtility.getLogger().info("[Pump M] Receive UPOSMessage before Pumping Start.") ;
				
				// Pump A �� ������ ���ؼ� WorkingMessage (GB) ������ �����Ѵ�.
				ArrayList<WorkingMessage> workingMsgArray = ODTUtility_Common.createWorkingMessageFromUPOSMessage(syncUnique, uPosMsg) ;

				// PI2, 2015-12-30 
				if ((workingMsgArray != null) && (workingMsgArray.size() != 0)) {
		    		for (int i = 0 ; i < workingMsgArray.size() ; i++) {
		    			
		    			GB_WorkingMessage gbMsg = (GB_WorkingMessage) workingMsgArray.get(i);
		    			
		    			Preamble pumpPreamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_PUMP_ADAPTER, gbMsg, "") ;
		    			
		    			sendMessage(pumpPreamble) ;
		    		}
		    	}
				break ;
			}
			// �پ��� ���� & GSC Self�� ������ ���� ODT �� ���
			default : {
				// Pump A �� ������ ���ؼ� WorkingMessage ������ �����Ѵ�.
				ArrayList<WorkingMessage> workingMsgArray = 
		    		ODTUtility_Common.createWorkingMessageFromUPOSMessage(syncUnique, uPosMsg) ;

				// Pump A �δ� WorkingMessage �� �����ϰ�, Sale M ���δ� ���� ������ ����
	    		sendRespondingMsgToPumpA(workingMsgArray, uPosMsg) ;
				break ;
			}
		}
    }
    
    // Cat�� �������� ��û�� ���� �������� ����ǿ� ���ؼ� ���ڵ� filler5�� �������ش�.
	private void setBarcode(Preamble preambleData) {
		Object obj = preambleData.getPreamble() ;
		
		if (obj instanceof UPOSMessage) {
			UPOSMessage uPosMsg = (UPOSMessage) obj ;
			
			int messageType = Integer.parseInt(uPosMsg.getMessageType()) ;
			
			switch (messageType) {
    			case IUPOSConstant.MESSAGETYPE_INT_4202 :
    			case IUPOSConstant.MESSAGETYPE_INT_4292 : 
    			case IUPOSConstant.MESSAGETYPE_INT_4302 :{
    				String ledCode = uPosMsg.getLed_code();
    				if(!IUPOSConstant.LEDCODE_Z.equals(ledCode) && !IUPOSConstant.LEDCODE_0.equals(ledCode)){
    					uPosMsg.setFiller6(Barcode.getBarcodeNumber("6", uPosMsg.getItem_info().getTotalOilPrice_after_discount(), uPosMsg.getNozzle_no(), uPosMsg.getPosReceipt_no(), uPosMsg.getMessageType(), uPosMsg.getLed_code(), null));
    					LogUtility.getPumpMLogger().debug("Filler6: " + uPosMsg.getFiller6());
    				}
    			}
    			break ;
    			
    		}
			
		}
		
	}
	
	 /**
     * BEACON �κ����� ���� ��û�� ���� ���� ������ Pump A �� ������.
     * 
     * @param syncUnique	: Unique Key
     * @param uPosMsg		: ���� ����
     */
    private void sendBeaconMessageToBeaconM(String syncUnique, UPOSMessage uPosMsg) {
    	
    	BeaconMessage beaconMsg = BeaconUtility.createBeaconMessageFromUPOSMessage(syncUnique,uPosMsg);
		
    	// Pump A �� WorkingMessage �� �����Ѵ�.
		Preamble pumpPreamble = PumpMUtil.createBeaconMessagePreamble(syncUnique,
				SyncManager.DISE_BEACON_MODULE, beaconMsg , "") ;
		LogUtility.getLogger().info("[Pump M] <Beacon Process> PumpM -> BeaconM ���� : " + uPosMsg.getMessageType()) ;
		sendMessage(pumpPreamble) ;
    }
    
    /**
	 * 
	 * 
     * �ֹ����� ������ ó���Ѵ�.
     * 
     * 
     * 1. �ֹ����� �����϶� 
     * 1.1 ���� ���¸� üũ�Ѵ�.
     * 1.2 ���� ��� ���� �Ҷ� ���� ��ġ ���θ� üũ�Ѵ�.
     * 
     * 2. �ֹ����� ���������� ARK�� �����Ѵ�.
     * 
     * @param messageID : �޼���ID
     *        uPosMsg   : �ֹ����� ����
     */
	private void process0222(String messageID, UPOSMessage uPosMsg) {
		//1.�ֹ� ���� �����϶� ���� üũ
		  
		  LogUtility.getLogger().info("[Pump M] <Beacon Process> 0222 ó��") ;
		  
		  String filler2 = uPosMsg.getFiller2();

		  BeaconMessage bMsg = ByteArrayToBeaconMessage.createBeaconMessage(filler2.getBytes());
		  
		  if(bMsg != null){
			  //�ֹ������
			  if("1".equals(bMsg.getOrderSt())){

				  boolean state = PumpAController.checkNozState(bMsg.getDisplayArkId());
				  
				  
				  //������ ����
				  if(state){
					  	bMsg.setNozStats("0");	// ���� ��� ���� 
					  	
					  	boolean gdCd = BeaconDataHandler.checkGoodsCode(bMsg.getNozNo(), bMsg.getGdsCode());
					  	
					  	if (gdCd){
					  		bMsg.setGdsCheck("1");	// ���� ��ġ 
					  	} else {
					  		bMsg.setGdsCheck("2");	// ���� ����ġ 
					  	}
				  } else {
					  	//��������
					    bMsg.setNozStats("1");
				  }
				  
				  
				  byte[] bm_jbFiller2 = BeaconMessageToByteArray.createBeaconMessageByteArray(bMsg);
				  uPosMsg.setFiller2(new String(bm_jbFiller2));
				  
					
			  }else{
				  LogUtility.getLogger().debug("[Pump M] <Beacon Process> 0222 �ֹ����� �����ڵ� => " + bMsg.getOrderSt() +" dArk no => " + bMsg.getDisplayArkId()) ;    
			  }
		  }else{
			  LogUtility.getLogger().debug("[Pump M] <Beacon Process> 0222 �ֹ����� �����ڵ� ���� dArk no => " + bMsg.getDisplayArkId()) ;
		  }
		  
		  //beacon���� �� ��� beacon���� ���� 
		  sendBeaconMessageToBeaconM(messageID,uPosMsg);
	}
	
	@Override
	public void start() {
		registerListener() ;
    }
	
	@Override
	public void stop() {
        // TODO generated stub, insert your logic here.
    }
}
