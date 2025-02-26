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
     * CAT M 으로 보내기 전에 아래의 상황을 조사하여 추가적인 작업을 수행한다.
     * 		1. KH 처리번호는 있지만, 주유정보가 0인 경우
     * 			이 경우는 발생될수 있다. 가령 거래처 주유중/완료 정보 요청시, 주유 정보는 있지만 거래처 카드가 없을 경우 발생 될 수 있다.
     * 			이 경우 unLocking 을 실행해 주어야 한다.
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
    				 * 2016. 4. 14. 오후 15:48:31, PI2, WooChul Jung
    				 * 		주유를 어느 정도 할지 모르기 때문에, 막음
    				 */    				 
	    			case IUPOSConstant.MESSAGETYPE_INT_4302 : {
	    				String custCar_limit_type = uPosMsg.getCustCar_limit_type() ;
	    				String ss_crStNum = uPosMsg.getSs_crStNum() ;
	    				
	    				/**
	    				 * 2016. 4. 14. 오후 15:48:31, PI2, WooChul Jung.
	    				 * 		회의 참석자 : 이익주 이사, 장윤기 차장, 정우철 차장.
	    				 * 		요구 사항 : by 장윤기 차장
	    				 * 			주유중 결제시, 외상 거래처-한도 고객의 경우 결제 불가로 CAT 단말기에 전송하도록 구현
	    				 * 			거래처 카드의 정보를 이용하여 처리해야 하기 때문에, 응답 전문에서 처리하도록 구현하였음.
	    				 * 		테스트 결과
	    				 * 			일반 신용, 외상 인정 (무제한), 외상 한도, 현금 거래처 고객에 대한 테스트 완료하였음.
	    				 */
	    				if (!GlobalUtility.isNullOrEmptyString(ss_crStNum) && 
	    						(ICode.DY_LIMIT_TYPE_01.equals(custCar_limit_type) || ICode.DY_LIMIT_TYPE_02.equals(custCar_limit_type))) {
	    					// 거래처 한도 고객의 경우 결제 막음
	    					LogUtility.getPumpMLogger().debug("[Pump M] 거래처 한도 고객 - 주유중 결제 불가!") ;
	    					uPosMsg.setLed_code(IUPOSConstant.LEDCODE_G);
	    				} 

	    				String posReceipt_no = uPosMsg.getPosReceipt_no() ;
	    				
	    				if (UPOSUtil.shouldUnLock(uPosMsg)) {
	    					sendLockingInfoToPOS(uPosMsg.getNozzle_no(), posReceipt_no, IConstant.PUMP_SALE_UNLOCKING) ;
	    					// 2016. 4. 14. 오후 15:48:31, PI2, Taekwon Lee 거래처 led_code에 따라 CAT 단말기 프리셋을 사용자 임의취소 전문으로 변경
		    				if(Integer.parseInt(uPosMsg.getMessageType()) == IUPOSConstant.MESSAGETYPE_INT_4302	){
		    					LogUtility.getPumpMLogger().info("[Pump M] CAT Preset 거래처 결제불가로 인한 - 사용자 임의취소 Led_code=" + uPosMsg.getLed_code()) ;
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
     * 단말기 등록 유무를 확인한다.
     * 등록된 단말기가 아닌 경우 false 를 리턴하여 CAT 단말기에게 알린다.
     * 
     * @param crt_no_seq	: 단말기 번호
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
     * CAM M 으로부터 받은 전문을 처리한다.
     * 
     * 다음과 같은 종류의 전문들이 CAT Module 로 부터 받는다.
     * 		1. Locking / unLocking 요청 (POS Protocol)
     * 			: TR 관리 테이블에 Locking/Unlocking 을 한 후 응답 전문을 CAT Module 에 보낸다.
     * 		2. 주유중 정보 요청 (POS Protocol with u-POS 전문)
     * 			: TR 관리 테이블에 주유 중 정보를 얻어서 CAT Module 에 보낸다.
     * 		3. 요청한 결제에 대한 응답 전문 (POS Protocol with u-POS 전문)
     * 			: Working Message Class 를 만들어서 Pump Adapter 에 보낸다.
     * 
     * @param receiving_catm	: CAM M 으로 부터 받은 전문
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
	        	 * BEACON 전문 :
	        	 * 	1. 장애대응 -> PumpA : 현금응답(0012),현금보너스응답(0014), 신용승인응답(0032), 신용+보너스누적응답(0034),
	        	 *     장애대응 -> POS     GS보너스 사용응답(0062), 쿠폰 사용응답(0064), 쿠폰 + 국세청현금영수증 사용응답(0066)
	        	 *                        주문정보 응답(0222)
	        	 * 	2. 장애대응 -> POS   : 현금취소응답(8012),현금보너스취소응답(8014), 신용승인취소응답(8032), 신용+보너스누적취소응답(8034),
	        	 *                        GS보너스 사용취소응답(8062), 쿠폰 사용취소응답(8064), 쿠폰 + 국세청현금영수증 사용취소응답(8066)
	        	 *                        Beacon 결제 연동 취소는 POS에서만 가능
	        	 *                        단말기에서 취소시는 비연동으로 취소
	        	 * 		- DeviceType   : 3M
	        	 * 2017.06.30
	        	 */
	        	if(IUPOSConstant.DEVICE_TYPE_3M.equals(uPosMsg.getDeviceType())){	// beacon전문 인 경우 beacon으로 전달
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
	        				
	        				LogUtility.getLogger().info("[Pump M] <Beacon Process> 승인 응답 전문 수신") ;
	        				
	        				
	        				if("1".equals(posUposMsg.getLed_code())){
	        					
	        					//Beacon에서 온 승인요청 전문
		        				LogUtility.getLogger().info("[Pump M] <Beacon Process> Send Beacon UPOSMessage (" + posUposMsg.getMessageType() + ") to SaleM") ;
						    	Preamble posPreamble = PumpMUtil.createUPOSMessagePreamble( syncUnique, 
																				 SyncManager.DISE_PUMP_MODULE,
																				 SyncManager.DISE_SALE_MODULE, 
																				 posUposMsg, 
																				 "") ;
						    	sendMessage(posPreamble) ;
	        					
	        				}else{
	        					/*
		        				 * Beacon 결제인 경우는 
		        				 * 승인 실패 난 경우 POS에 팝업을 띄우기 위해 
		        				 * messageType에 100을 더해서 
		        				 * POS로 실패 내역을 전송 
		        				 * ODT로직을 따른다.
		        				 * 2017.06.05 박종호과장,전기병과장 합의
		        				 */
	        					
	        					//승인 실패 인 경우
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
	        				  //주문요청 응답 전문을 처리한다.
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
        					//POS에서 온 취소 전문
        					LogUtility.getLogger().info("[Pump M] <Beacon Process> Beacon 승인 취소 응답 전문 수신") ;
        					
        					/*
        					 * pos에서 복구 반품 응답시, 반품 요청한 messageId동일한지 확인하기 때문에
        					 * messageId 입력  2017.05.17 lj
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
				    		  // CAT 단말기로부터 요청 - 캠페인주유완료  정보 요청
				    	  case IUPOSConstant.MESSAGETYPE_INT_4201 : {
				    		  // CAT 단말기로부터 요청 - 캠페인주유중 정보 요청	    	 
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
					    	// ODT or LPG 로부터 요청 - 캠페인주유완료  정보 응답
				    		// 2016-01-12, PI2, songkis, 생성된 캠패인 정보를 다시 PUMPA로 전달 한다.
					    	  LogUtility.getLogger().info("[Pump M] 현장할인 적용 캠페인 주유 정보 데이터 수신") ;
					    	  
					    	  if(uPosMsg != null && (
					    			  IUPOSConstant.MESSAGETYPE_4202.equals(uPosMsg.getMessageType()) && uPosMsg.getDeviceType().equals(IUPOSConstant.DEVICE_TYPE_3S) || 
					    			  IUPOSConstant.MESSAGETYPE_4292.equals(uPosMsg.getMessageType()) && uPosMsg.getDeviceType().equals(IUPOSConstant.DEVICE_TYPE_3O)
					        								) ) {			    	        
					    		 //LogUtility.getLogger().info("[Pump M] 현장할인 여부 ="+uPosMsg.getPromptDiscount_yn()) ;
				    			LogUtility.getLogger().info("[Pump M] Receive Request of PumpingInfo from CAT M. This originated from ODT or LPG. NozID="+nozID + 
				    					"#현장할인여부=" + uPosMsg.getPromptDiscount_yn()) ;
				    			if ("1".equals(uPosMsg.getPromptDiscount_yn())) {
				    				PumpMODTSaleManager.setPromptDiscount_yn(nozID, uPosMsg.getPromptDiscount_yn()) ;
				    			}
						    	sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
						    	//LogUtility.getLogger().info("[Pump M] Receive Request of Completed PumpInfo from CAT M. nozID="+nozID) ;
				    		}
					    	break;
					      }
				    	  case IUPOSConstant.MESSAGETYPE_INT_4301 : {
				    		  // CAT 단말기로부터 요청 - Preset	    	 
				    		  LogUtility.getPumpMLogger().info("[Pump M] Receive Request of NozzleState info from CAT M. nozID="+nozID) ;
				    		  // process4201_4291에 4301 전문 프로세스 추가
				    		  process4201_4291(syncUnique, receiving_catm, messageType, uPosMsg) ;
				    		  break ;
				    	  }
				    	  
				    	  case IUPOSConstant.MESSAGETYPE_INT_0301 : {
				    		  // CAT 단말기로부터 요청 - 상품 다운로드 요청
				    		  // 상품 다운로드 요청을 받아서 KixxHub 테이블에 저장된 상품 정보(노즐정보 + 상품정보 + 세차정보)를 CAT M 에게 전송한다.
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Request of Product info from CAT M. NozID="+nozID) ;
					    	  UPOSMessage sendUPOSMsg = process0301(uPosMsg) ; 
					    	  Preamble preamble = 
					    		  PumpMUtil.createUPOSMessagePreamble(syncUnique, SyncManager.DISE_PUMP_MODULE, SyncManager.DISE_CAT_MODULE, sendUPOSMsg, "") ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Product info to CAT M. NozID="+nozID) ;
				    		  sendMessage(preamble) ;
				    		  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_0091 : {
				    		  //CAT 단말기로부터 요청 - 사용자 임의취소
				    		  process0091(posReceipt_no);
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_0004 : {
				    		  // ODT 로 부터의 요청에 의한 응답 - 보너스누적 응답
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Bonus from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Bonus to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_0012 : {
				    		  // ODT 로 부터의 요청에 의한 응답 - 현금 응답
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Cash Receipt from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Cash Receipt to Pump A. posReceipt_no="+posReceipt_no) ;
				    		  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_0014 : {
				    		  // ODT 로 부터의 요청에 의한 응답 - 현금보너스누적 응답
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Cash+Bonus from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Cash+Bonus to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_0016 : {
				    		  // ODT 로 부터의 요청에 의한 응답 - 현금 영수증 응답
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Cash Receipt from CAT M. posReceipt_no="+posReceipt_no) ;		    	  
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Cash Receipt to Pump A. posReceipt_no="+posReceipt_no) ;    	  
					    	  break ;
				    	  }	 
				    	  case IUPOSConstant.MESSAGETYPE_INT_0032 : {
				    		  // ODT 로 부터의 요청에 의한 응답 - 신용승인 응답
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Credit from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Credit to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_0034 : {
				    		  // ODT 로 부터의 요청에 의한 응답 - 신용보너스 응답
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Credit+Bonus from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Credit+Bonus to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_0046 : {
				    		  // ODT 로 부터의 요청에 의한 응답 - 페이승인 응답
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Pay from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Pay to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_0048 : {
				    		  // ODT 로 부터의 요청에 의한 응답 - 페이보너스 응답
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Pay+Bonus from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Pay+Bonus to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }		    	  
				    	  case IUPOSConstant.MESSAGETYPE_INT_0052 : {
				    		  // ODT 로 부터의 요청에 의한 응답 - myLG점수사용 응답
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive myLG Use from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send myLG Use to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_0054 : {
				    		  // ODT 로 부터의 요청에 의한 응답 - 국세청 현금 영수증 +  GS보너스 누적 응답.
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive CashReceipt + Bonus from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send myLG Use to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_0062 : {
				    		  // ODT 로 부터의 요청에 의한 응답 - GS점수사용 응답
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive GS BonusCard Use from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send GS BonusCard Use to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_0064 : {
				    		  // ODT 로 부터의 요청에 의한 응답 - 쿠폰사용 응답
					    	  LogUtility.getLogger().info("[Pump M] Receive Coupon Use from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getLogger().info("[Pump M] Send Coupon Use to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_0066 : {
				    		  // ODT 로 부터의 요청에 의한 응답 - 쿠폰 + 국세청현금영수증 사용 응답
				    		  LogUtility.getLogger().info("[Pump M] Receive Coupon + CashReceipt Use from CAT M. posReceipt_no="+posReceipt_no) ;
				    		  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
				    		  LogUtility.getLogger().info("[Pump M] Send Coupon + CashReceipt Use to Pump A. posReceipt_no="+posReceipt_no) ;
				    		  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_0072 : {
				    		  // ODT 로 부터의 요청에 의한 응답 - 신용카드 확인 응답 - BL 체크
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Credit Card check from CAT M. posReceipt_no="+posReceipt_no) ;		    	  
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Credit Card check to Pump A. posReceipt_no="+posReceipt_no) ;    	  
					    	  break ;
				    	  }	 
				    	  case IUPOSConstant.MESSAGETYPE_INT_0112 : {
				    		  // ODT 로 부터의 요청에 의한 응답 - 보너스 카드의 포인트 점수 조회 응답
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Bonus Card Point inquiry from CAT M. posReceipt_no="+posReceipt_no) ;		    	  
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Bonus Card Point inquiry to Pump A. posReceipt_no="+posReceipt_no) ;    	  
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_8004 : {
				    		  // ODT 로 부터의 요청에 의한 응답 - 보너스누적 취소 응답
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Bonus Cancel from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Bonus Cancel to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_8014 : {
				    		  // ODT 로 부터의 요청에 의한 응답 - 현금보너스누적 취소 응답
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Cash+Bonus Cancel from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Cash+Bonus Cancel to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }case IUPOSConstant.MESSAGETYPE_INT_8032 : {
				    		  // ODT 로 부터의 요청에 의한 응답 - 신용승인 취소응답
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Credit Cancel from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Credit Cancel to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_8034 : {
				    		  // ODT 로 부터의 요청에 의한 응답 - 신용보너스 취소응답
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Credit+Bonus Cancel from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Credit+Bonus Cancel to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_8046 : {
				    		  // ODT 로 부터의 요청에 의한 응답 - 페이승인 취소응답
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Pay from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Pay Cancel to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_8048 : {
				    		  // ODT 로 부터의 요청에 의한 응답 - 페이보너스 취소응답
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Pay+Bonus from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Pay+Bonus Cancel to  Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }	
				    	  case IUPOSConstant.MESSAGETYPE_INT_8052 : {
				    		  // ODT 로 부터의 요청에 의한 응답 - myLG점수사용 취소응답
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive myLG Use Cancel from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send myLG Use Cancel to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_8062 : {
				    		  // ODT 로 부터의 요청에 의한 응답 - GS점수사용 취소응답
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive GS Bonus Card Use Cancel from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send GS Bonus Card Use Cancel to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_8064 : {
				    		  // ODT 로 부터의 요청에 의한 응답 - 모바일 쿠폰 취소응답
					    	  LogUtility.getLogger().info("[Pump M] Receive Mobile Coupon Use Cancel from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getLogger().info("[Pump M] Send Mobile Coupon Use Cancel to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_8066 : {
				    		  // ODT 로 부터의 요청에 의한 응답 - 모바일 쿠폰 + 국세청현금영수증 취소응답
				    		  LogUtility.getLogger().info("[Pump M] Receive Mobile Coupon + CashReceipt Use Cancel from CAT M. posReceipt_no="+posReceipt_no) ;
				    		  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
				    		  LogUtility.getLogger().info("[Pump M] Send Mobile Coupon + CashReceipt Use Cancel to Pump A. posReceipt_no="+posReceipt_no) ;
				    		  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_0082 : {
				    		  // ODT 로 부터의 요청에 의한 응답 - 외상 승인응답
					    	  LogUtility.getLogger().info("[Pump M] Receive Bonus from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getLogger().info("[Pump M] Send GS Bonus Card Use Cancel to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_0084 : {
				    		  // ODT 로 부터의 요청에 의한 응답 - GS점수사용 취소응답
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Bonus from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send GS Bonus Card Use Cancel to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_0232 : {
				    		  // ODT 로 부터의 요청에 의한 응답 - 간편결제응답
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive SimplePay from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send SimplePay to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_0236 : {
				    		  // ODT 로 부터의 요청에 의한 응답 - 현대CayPay 차량조회응답
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Vehicle inquiry(Hyundai) from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Vehicle inquiry(Hyundai) to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }	
				    	  case IUPOSConstant.MESSAGETYPE_INT_0242 : {
				    		  // ODT 로 부터의 요청에 의한 응답 - 모바일 복합결제응답
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Mobile(WebPay) from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Mobile(WebPay) to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }	
				    	  case IUPOSConstant.MESSAGETYPE_INT_0244 : {
				    		  // ODT 로 부터의 요청에 의한 응답 - 현대CayPay 복합결제응답
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive CarPay(Hyundai) from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send CarPay(Hyundai) to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }	
				    	  case IUPOSConstant.MESSAGETYPE_INT_0246 : {
				    		  // 바로세차 승인응답
				    		  LogUtility.getPumpMLogger().info("[Pump M] Receive Mobile(WebPay) from CAT M. posReceipt_no="+posReceipt_no) ;
				    		  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
				    		  LogUtility.getPumpMLogger().info("[Pump M] Send Mobile(WebPay) to Pump A. posReceipt_no="+posReceipt_no) ;
				    		  break ;
				    	  }	
				    	  case IUPOSConstant.MESSAGETYPE_INT_8242 : {
				    		  // ODT 로 부터의 요청에 의한 응답 - 모바일 복합결제취소응답
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Mobile(WebPay)Use Cancel from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Mobile(WebPay)Use Cancel to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }	
				    	  case IUPOSConstant.MESSAGETYPE_INT_8244 : {
				    		  // ODT 로 부터의 요청에 의한 응답 - 현대CayPay 복합결제취소응답
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive CarPay(Hyundai)Use Cancel from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send CarPay(Hyundai)Use Cancel to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }					    	  
				    	  case IUPOSConstant.MESSAGETYPE_INT_8246 : {
				    		  // 바로세차 승인취소 응답
					    	  LogUtility.getPumpMLogger().info("[Pump M] Receive Mobile(WebPay)Use Cancel from CAT M. posReceipt_no="+posReceipt_no) ;
					    	  sendWorkingMessageToPumpA(syncUnique, uPosMsg) ;
					    	  LogUtility.getPumpMLogger().info("[Pump M] Send Mobile(WebPay)Use Cancel to Pump A. posReceipt_no="+posReceipt_no) ;
					    	  break ;
				    	  }
				    	  case IUPOSConstant.MESSAGETYPE_INT_9994 : {
				    		  // ODT 로 부터의 요청에 의한 응답 - 승인응답 후 망취소
					    	  LogUtility.getLogger().info("[Pump M] Receive After Service Cancel from CAT M. posReceipt_no") ;
					    	  
					    	  //LogUtility.getLogger().info("[Pump M] 넘어온 거래고유 번호="+uPosMsg.getTrx_Proper_No()) ;
					    	  String nozzle_no = "";
					    	  String trx_proper_no = uPosMsg.getTrx_Proper_No();
					    	  nozzle_no = GSCSelfPumpingManager.getInstance().getGSCODTNozzleNo(trx_proper_no);
					    	  // hash map에 저장한 노줄번호 삭제
					    	  GSCSelfPumpingManager.getInstance().removeODTNozzleNo(trx_proper_no);
					    	  
					    	  uPosMsg.setNozzle_no(nozzle_no);
					    	  //LogUtility.getLogger().info("[Pump M] 가져온 노줄번호="+nozzle_no) ;
					    	  
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
		    			 * CAT으로 부터 Preset 요청을 수신한다.
		    			 * 수신 이후 Pump A 로 주유기 Preset 을 요청하고, POS로는 KH 처리번호를 생성하여 전송한다.
		    			 */
		    			case IConstant.POSPROTOCOL_COMMANDID_DK : {
		    				// 정액/정량 설정 요청	
		    		    	LogUtility.getPumpMLogger().info("[Pump M] Receive Fixed Price/Liter Pump from CAT. NozID="+nozID) ;
		        			POS_DK dkMsg = (POS_DK) posHeader ;
		    				// Debug
			    			LogUtility.getPumpMLogger().info(dkMsg.toString());
		        			PB_WorkingMessage pbWorkMsg = PumpMUtil.createPB_WorkMsg(dkMsg) ;
		        			
		        	    	String khproc_no = 
		        				PumpMTransactionManager.getInstance().getKHTransactionID(pbWorkMsg.getNozzleNo(), IPumpConstant.KH_PUMP_PRESET) ;
		        	    	PumpMTransactionManager.getInstance().setPresetInfo(pbWorkMsg.getNozzleNo(), IPumpConstant.PRESET_FROM_CAT) ;
		        	    	// 2012.07.20 ksm  PB_WorkingMessage에 barcode 필드 설정.
		        	    	// twsongkis 2015-01-28 새로운 바코드 로직으로 barcode 셋팅
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
		    				
		    				// KH 처리번호를 POS 에 전송한다.	- CAT 단말기로 부터 수신된 DK 이기 때문에, POS 로 DL 전문을 전송할 필요는 없음.
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
		 // CAT 단말기로부터 요청 - 사용자 임의 취소 (Locking 해지 요청 전문)
  	  
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
			    	  // CAT Preset 사용자 임의취소시 pump state 초기화
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
	 * CAT 단말기로 부터 상품 다운로드 요청을 받아서 이에 대한 응답전문을 구성한다.
	 * 만약 등록되지 않은 CAT 단말기로부터 요청이 왔을 경우 Led_Code 를 0 으로 하여 응답 전문을 구성한다.
	 * 이는 등록되지 않은 단말기를 의미한다.
	 * 
	 * 2019.08.09
	 * 세차바코드 및 주유바코드 출력시 필요정보를 FIller1에 넘겨준다.
	 * by SoonKwan
	 * @param uPosInfo	: 상품코드 다운로드 요청 전문
	 * @return			: 상품코드 다운로드 응답 전문
	 */
	public UPOSMessage process0301(UPOSMessage uPosInfo) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "CatMController/" + "process0301()");
		
		UPOSMessage uPosMessageReturn = null ;
		
		String deviceType = uPosInfo.getDeviceType() ;
		String download_flag = null ;
		UPOSMessage_CatalogInfo catalog_info = null ;
		String term_id = uPosInfo.getTerm_id() ; // 단말기 번호
		//String version = uPosInfo.getFiller1();		// 단말기 버전 정보
		// PI2, 여전법 대응에 따른 통합 Upos Message 적용, 2016.03.31 - CWI
		// filler1 -> Cat_version로 변환
		String version = uPosInfo.getCat_version();		// 단말기 버전 정보
		
		// 세차바코드를 출력하기위한 정보를 추가한다
		// Filler1에 넣어준다. 
		// 2019.08.07 SoonKwan 추가
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
				led_code = IUPOSConstant.U0302_LEDCODE_0 ;		// 등록된 단말기가 아님.
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
				 * 상품다운로드 응답시 'CAT 단말기 프린트 유무' '매장마일리지 적립 유무' 필드가 추가되었다.
				 * 그리고 아래 함수 호출 순서가 중요하다. download_flag 의 값은 앞 두 함수로 인해 최신 값으로 변경된다.
				 */
				localMileage_yn = UPOSUtil.getMileage_ind() ;
				posPrint_yn = UPOSUtil.getCATPrint_ind() ;
				download_flag = UPOSUtil.getDownloadFlag() ;
				
				//매장마일리지 적립 유무
				coupon_yn = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(session, IConstant.POSPORTOCOL_CODEMASTER_0304);

				if (coupon_yn == null)
					coupon_yn = IUPOSConstant.U0302_FILLER1_0;
				
				//쿠폰 제공처 코드
				couponSupplyCode = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(session, IConstant.POSPORTOCOL_CODEMASTER_219);
				
				if (couponSupplyCode == null)
					couponSupplyCode = "";
				
				/*// 테스트 목적으로 설정한다.
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
				
				//장애대응적용 후에 테스트 일때도  실매장코드을 올려야 되기 때문에 수정 
				shopCode = new UPOSMessage_ShopCodeInfo(	catData.getSys_use_ind() ,
						catData.getCrt_code_type(),
						catData.getStore_code()) ;
				
				led_code = IUPOSConstant.U0302_LEDCODE_1 ;		// 등록된 단말기임
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}
	
		sendCatVersion(term_id, version);
		
		// 미등록 단말기에는 기존방법으로 보낸다.
		// 바코드 정보를 확인할 필요는 없습니다
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
					barcode_info  // 2019.08.07 SoonKwan 세차바코드정보
					);
			
		}		
		//uPosMessageReturn.setFiller2(couponSupplyCode);
		uPosMessageReturn.setCust_code(couponSupplyCode);
		return uPosMessageReturn;
	}

	/**
	 * 캠페인 주유 정보 요청에 대한 응답
	 * 
	 * @param syncUnique		: 전문 Key
	 * @param receiving_catm	: CAM M 으로부터 온 Preamble Data
	 * @param messageType		: 전문 message type
	 * @param uPosMsg			: 캠페인 주유 정보 요청 전문
	 */
    private void process4201_4291(String syncUnique, Object receiving_catm, int messageType, UPOSMessage uPosMsg) {
    	if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "CatMController/" + "process4201_4291()");
		
    	String custCard_car_type = uPosMsg.getCustCard_car_type() ;
	  
    	if (GlobalUtility.isNullOrEmptyString(custCard_car_type)) {
    		LogUtility.getPumpMLogger().error("[Pump M] Set custCard_car_type 0 as default.") ;
    		custCard_car_type = IUPOSConstant.CUSTCARD_CAR_TYPE_0 ;
    	}
    		  
    	// 주유중인지 주유완료인지에 따라 POS에서 거래제약조건을 주기위해
    	// POS의 요청으로 추가 - upkoo
    	String isPumping = ICode.DX_ISPUMPING_IND_0 ;
	  
    	String term_id = uPosMsg.getTerm_id() ;
	  
    	
		
    	if (!isRegisteredCATDevice(term_id)) {
    		// 단말기가 등록되어 있지 않을 경우
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
    		 * 주유 중 정보 요청의 경우 외상 거래처 - 한도 고객에 대해서 결제 불가 전송으로 수정.
    		 * 따라서 응답 전문에서 처리를 해야 함.
    		 * 
    		 * => 아래의 로직은 주석 처리하여 기존 KixxHUB 로직과 동일하게 수정 (KixxHUB 고도화 양일준)
    		 */
    	/*}else if(messageType == IUPOSConstant.MESSAGETYPE_INT_4201 
    			&& ("1".equals(custCard_car_type) || "3".equals(custCard_car_type))){
    		// 2016-04-19 Taekwon Lee 거래처 - 주유중 결제 막음
        		LogUtility.getPumpMLogger().debug("[Pump M] 거래처 - 주유중 결제 불가!") ;
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
    		// 거래처별/차량별 구분 (0:없음 1:거래처카드로 주유정보요청 2:차량단축번호조회 3: 차량번호로 주유정보요청 
    		//                                     4:영업화물거래처조회, 5:유가보조거래처 조회)
    		// 캠페인 주유 정보 요청
    		LogUtility.getPumpMLogger().info("[Pump M] Receive Pump Info from CAT M.") ;
    		UPOSMessage sendUPOSMsg = UPOSUtil.getUPOSMessage_ItemInfo_Item(uPosMsg, messageType) ;		
		  
    		// 2013.05.06 ksm 통합거래처 확인.
    		// 주유중정보요청일 경우 통합거래처 결제 막도록 함.
    		if ( messageType == IUPOSConstant.MESSAGETYPE_INT_4201 
    			&& T_KH_INTEGCUST_BIN_INFO_MEMORYHandler.getHandler().isExist()
				&& (!"".equals(uPosMsg.getBonRSCard_no()) || !"".equals(uPosMsg.getCreditCard_no()))) {
			  	
    			//boolean bCjGoodsYn = false;
    			
				try{
					// PI2, 2016-03-31, 통합전문 구 필러 세분화
					//String[] filler5 = uPosMsg.getFiller5().split(IUPOSConstant.DELIMITER_RS_STRING);
					String bonCardReading_type = uPosMsg.getBonCardReading_type();        // 보너스카드 리딩매체
					String creditCardReading_type = uPosMsg.getCreditCardReading_type();  // 신용카드 리딩매체
					
					//String[] filler4 = GlobalUtility.splitByteArrayToStringArray(uPosMsg.getFiller4().getBytes(), IUPOSConstant.DELIMITER_0X1E); 
					String custGoods_Code = uPosMsg.getCustGoods_code(); // 신용카드 상품코드
					LogUtility.getLogger().debug("[통합거래처] 신용카드 상품코드 custGoods_Code : " + custGoods_Code);
					
					/*
					//상품이 있을 수도 없을 수도 있으므로 이후 판단함.
					if(filler4.length == 3){	// 화물특화거래상품코드 필드 있음.
						if("".equals(filler4[2])){
							LogUtility.getCATLogger().debug("[통합거래처] 상품코드 공백임.");
						}else{
							LogUtility.getCATLogger().debug("[통합거래처] 상품코드 : " + filler4[2]);
							bCjGoodsYn = true;
						}
					}
					
					// 신용카드와 보너스 모두 리딩매체가 IC인경우 bCjGoodsYn true로 변경.
	  				if("01".equals(filler5[0]) && "01".equals(filler5[2]))
	  				{
	  					bCjGoodsYn = true;
	  				} */
					
	  				//if(bCjGoodsYn){
					//if(filler4.length == 3){
					
//					상품코드 없는 통합거래처 적용을 위해 수정 [주석처리]
//					if(!custGoods_Code.equals("")){ // 화물특화거래상품코드 값이 있을 경우
						String isTarget =  T_KH_INTEGCUST_BIN_INFO_MEMORYHandler.getHandler().isTarget(	uPosMsg.getCreditCard_no(),
																										uPosMsg.getBonRSCard_no(),
																										/**
																										filler5[2],			// 신용카드 리딩매체
																										filler5[0],			// 보너스카드 리딩매체
																										filler4[2]);		// 신용카드 상품코드
																										*/
																										// 통합전문 사용에 따른 기존 filler 배열을 분리 
																										creditCardReading_type, // 신용카드 리딩매체
																										bonCardReading_type,    // 보너스카드 리딩매체
																										custGoods_Code);        // 신용카드 상품코드(화물특화거래상품코드)
						
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
					LogUtility.getCATLogger().debug("[통합거래처] Filler5  Split 실패. filler5 객체 없음.");
				}catch (IndexOutOfBoundsException e){
					LogUtility.getCATLogger().debug("[통합거래처] Filler5 항목 오류. 단말기 패치해요.");
				}catch(Exception e){
					LogUtility.getCATLogger().debug("[통합거래처] 무슨 에러인지 ?" + e.toString());
				}
		  	}else if(messageType == IUPOSConstant.MESSAGETYPE_INT_4291 
		  		//&&T_KH_INTEGCUST_BIN_INFO_MEMORYHandler.getHandler().isExist()
		  		&& (!"".equals(uPosMsg.getBonRSCard_no()) || !"".equals(uPosMsg.getCreditCard_no()))){
			  
		  		//boolean bCjGoodsYn = false;
		  		
		  		try{
		  			//String[] filler5 = uPosMsg.getFiller5().split(IUPOSConstant.DELIMITER_RS_STRING);
		  			String creditCardReading_type = uPosMsg.getCreditCardReading_type();  // 신용카드 리딩매체
		  			String bonCardReading_type = uPosMsg.getBonCardReading_type();        // 보너스카드 리딩매체		  	
		  			
		  			//String[] filler4 = GlobalUtility.splitByteArrayToStringArray(uPosMsg.getFiller4().getBytes(), IUPOSConstant.DELIMITER_0X1E); 
		  			String custGoods_Code = uPosMsg.getCustGoods_code();   // 신용카드 상품코드(화물특화거래상품코드)
		  			
		  			String targetCust = "";
		  			SqlSession session = null;
				  
		  			try {
		  				/*
		  				// 신용카드와 보너스 모두 리딩매체가 IC인경우 bCjGoodsYn true로 변경.
		  				if("01".equals(filler5[0]) && "01".equals(filler5[2]))
		  				{
		  					bCjGoodsYn = true;
		  				}*/
		  				
		  				//if(bCjGoodsYn){
		  				//if(filler4.length == 3){
		  				
		  				//20170306 이강호 
		  				//상품코드 없는 통합거래처 적용을 위해 수정 [주석처리]
		  				//if(!custGoods_Code.equals("")){// 화물특화거래상품코드 값이 있을 경우
			  				session = SqlSessionFactoryManager.openSqlSession();
			  				LogUtility.getCATLogger().debug("[통합거래처]  custGoods_Code : " + custGoods_Code );
			  				
			  				if(T_KH_INTEGCUST_BIN_INFOHandler.getHandler().isExist(session)){
			  					targetCust =  T_KH_INTEGCUST_BIN_INFOHandler.getHandler().getTargetCust(session,  
			  							 uPosMsg.getCreditCard_no(),
										 uPosMsg.getBonRSCard_no(),
			  							 creditCardReading_type, // 신용카드 리딩매체
			  							 bonCardReading_type,    // 보너스카드 리딩매체
			  							 custGoods_Code);          // 신용카드 상품코드
			  				}else{
			  					targetCust = "";
			  				}
		  				//}else{
		  				//	targetCust = "";
		  				//}
		  			}catch (Exception e){
		  				LogUtility.getCATLogger().debug("[통합거래처] DB 조회 중 에러 : " + e.toString(), e);
		  				
		  			}finally{
	  					SqlSessionFactoryManager.closeSqlSession(session);
		  			}
		  			if(!"".equals(targetCust)){
		  				LogUtility.getCATLogger().debug("[통합거래처] "   + targetCust);
						session = SqlSessionFactoryManager.openSqlSession();
						// 거래처 조회.
						String store_code = T_KH_STOREHandler.getHandler().getStoreCode();
						  
						try{
							T_KH_CUST_INFOData custInfoData = 
								T_KH_CUST_INFOHandler.getHandler().getT_KH_CUST_INFOData(session, targetCust, store_code) ;
							  
							if (custInfoData == null){
								LogUtility.getCATLogger().debug("[통합거래처] 거래처 코드가 존재하지않습니다. targetCust : " + targetCust);
							}else{	  
								int nCustCdItem 	= Integer.parseInt(custInfoData.getCust_cd_item());
								String goodsCd	= sendUPOSMsg.getItem_info().getItemInfoList().get(0).getGoodsCode();
								
								if(nCustCdItem >= 31 && nCustCdItem <= 36){
								// 거래처 유형 확인하여 보너스적립여부 판단.
								// 31 ~ 33 : 통합거래처 보너스적립함.
								// 34 ~ 36 : 통합거래처 보너스적립안함.
									if(nCustCdItem >= 34 && nCustCdItem <= 36){
										sendUPOSMsg.setLed_code(IUPOSConstant.LEDCODE_K);
									}
									  
									//  해당하는 거래처가 있으면 거래단가 구함.									
									LogUtility.getPumpMLogger().debug("[Pump M] 등록된 PL 을 검색합니다.cust_code_rep="+	targetCust+",goods_code="+ goodsCd) ;
									String basePrice = CustUtil.calcIntegCustBasePrice( session, store_code, targetCust, goodsCd);
									  
									if(!"".equals(basePrice)){
										LogUtility.getPumpMLogger().debug("[통합거래처] 거래단가 구해옴. basePrice = " + basePrice);
										
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
										  									  
										//LogUtility.getPumpMLogger().debug("[통합거래처] 1:" + unitPriceBeforeDiscount + " 2: " +  oilAmount + " 3: " + oilPriceBeforeDiscount +" 4:  " + oilPriceAfterDiscount);
										UPOSMessage_ItemInfo_Item newItemInfoItem 
											= CreateUPOSMessage.createUPOSMessage_ItemInfo_Item(sendUPOSMsg.getNozzle_no(), 
													goodsCd, 
													bonusGoodsCd, 
													oilInd, 												
													unitPriceBeforeDiscount, 								// 할인전단가
													oilAmount, 												// 수량
													GlobalUtility.getMultipleWith1000(basePrice), 			// 할인단가
													taxInd, 												// 과면세구분
													priceBeforeTax, 										// 공급가액
													taxPrice, 												// 부가세액
													oilPriceBeforeDiscount, 								// 할인전금액
													oilPriceAfterDiscount, 									// 할인후금액
													khTransactionID, 										// 전표번호
													ICode.PROC_IND_OVERLIMIT_99,							// 외상결제타입
													ICode.DY_UNITDISCOUNT_IND_1,							// 할인여부
													"", 													// 보관증번호
													ICode.SE_PUBLISH_BASE_01);								// 보관증발행유형
										UPOSMessage_ItemInfo newItemInfo = UPOSMessage_ItemInfo.createUPOSMessage_ItemInfo(newItemInfoItem);
										
										sendUPOSMsg.setItem_info(newItemInfo);
										// 거래처 정보 셋팅
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
												// 주유건에 대한 결제 진행 여부를 POS 에 통보
											sendLockingInfoToPOS(uPosMsg.getNozzle_no(), 
													sendUPOSMsg.getPosReceipt_no(), 
													IConstant.PUMP_SALE_LOCKING) ;
										}
											
										LogUtility.getPumpMLogger().info("[Pump M] Send Pump Info to CAT M.") ;		    
										
										sendMessage(preamble) ;
										return ;
	
									}else{
										LogUtility.getCATLogger().debug("[통합거래처]  조회된 거래단가/거래조건이 없습니다. 거래처 : "  + targetCust);
										  
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
									LogUtility.getCATLogger().debug("[통합거래처]  조회된 거래처는 통합거래처가 아닙니다. 거래처유형 : " + custInfoData.getCust_cd_item());
								}
							}
						}catch(Exception e){
							LogUtility.getCATLogger().debug("[통합거래처] DB 조회 중 에러 : " + e.toString());
						}finally{
							SqlSessionFactoryManager.closeSqlSession(session);
						}
		  			}
		  		}catch(NullPointerException e){
		  			LogUtility.getCATLogger().debug("[통합거래처] Filler5  Split 실패. filler5 객체 없음.", e);
				}catch (IndexOutOfBoundsException e){
					LogUtility.getCATLogger().debug("[통합거래처] Filler5 항목 오류. 단말기 패치해요.", e);
				}catch(Exception e){
					LogUtility.getCATLogger().debug("[통합거래처] 무슨 에러인지 ?" + e.toString(), e);
				}
		  	}

		  if (uPosMsg.getTaxFreeCust_type().equals(IUPOSConstant.TAXFREECUST_TYPE_2)) {
			  UPOSMessage_ItemInfo itemInfo = sendUPOSMsg.getItem_info() ;
			  if (!itemInfo.getRecordNo().equals("00")) {
				  // 면세 카드 - 일반 고객 - 면세 단가를 적용하도록 한다.
				  LogUtility.getPumpMLogger().info("[Pump M] TaxFree Card - Normal Customer.") ;
				  PumpMUtil.resetTaxFreePrice(itemInfo.getItemInfoList().get(0)) ;
			  }
		  }
		  
		  // 개인 국민 STARTRUCLK카드 인 경우 보너스카드 빈 정보를 보고 할인을 적용해준다.	  
		  String bin = uPosMsg.getBonRSCard_no();
		  
		  boolean isKBDiscount = false;
		  boolean KBStarUseYN = true;
		  
		  try {
			  if (!"1".equals(T_KH_CODEMASTERHandler.getHandler().codeMaster.get(IConstant.POSPORTOCOL_CODEMASTER_0334))) {
				LogUtility.getPumpMLogger().debug("[국민 유가보조 카드 ] 환경설정 : 0334 (사용안함 설정)"  );
				KBStarUseYN = false;
			  } else {
				LogUtility.getPumpMLogger().debug("[국민 유가보조 카드 ] 환경설정 : 0334 (사용함 설정)"  );
			  }			  
		  } catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		  } 

		  // pi2, cwi, 2016-03-31
		  // properties 값을 불러 들여 phase 1, 2를 분리 하며,
		  // phase 1일 경우 국민스타트럭 및 통합거래처 할인을 방지 한다.
		  // phase 2일 경우 통합거래처 할인을 허가 하되, 거래처일 경우 통합거래처 할인을 방지 한다.
		  //String pi2_phase = PropertyManager.getSingleton().getProperty("kixxhub.pi2.deploy.phase", "2").trim();
		  
		  //sendUPOSMsg.print() ;
		  //uPosMsg.print() ;
		 /* if(pi2_phase.equals("1")){
			  if(IUPOSConstant.DEVICE_TYPE_3S.equals(uPosMsg.getDeviceType()) || IUPOSConstant.DEVICE_TYPE_3O.equals(uPosMsg.getDeviceType())){
				  LogUtility.getLogger().debug("[국민 유가보조 카드 ] 셀프 또는 충전기 고객");
				  KBStarUseYN = false;
			  }
		  }else{*/
		  if(IUPOSConstant.DEVICE_TYPE_3S.equals(uPosMsg.getDeviceType()) || IUPOSConstant.DEVICE_TYPE_3O.equals(uPosMsg.getDeviceType())){
			  
			  if(IUPOSConstant.DEVICE_TYPE_3S.equals(uPosMsg.getDeviceType()) && !GlobalUtility.isNullOrEmptyString(uPosMsg.getCustCard_No())){
				  LogUtility.getLogger().debug("[국민 유가보조 카드 ] Self_CustCard Not null ");
				  KBStarUseYN = false;
			  }else if(IUPOSConstant.DEVICE_TYPE_3O.equals(uPosMsg.getDeviceType())){
				  LogUtility.getLogger().debug("[국민 유가보조 카드 ] Recharge");
				  KBStarUseYN = false;
			  }
		  }
		  

		  
		  LogUtility.getLogger().info("[Pump M] KBStarUseYN="+KBStarUseYN) ;

		  /* 20180531 ygh
		   * [국민 유가보조 카드 ]  
		   * 신용카드 리딩이 있을 경우만 리터당 15원 할인 처리 
		   */
//		  LogUtility.getLogger().info("ddddddddddd"+uPosMsg.getBonRSCard_no()) ;
//		  LogUtility.getLogger().info("fffffffffff"+uPosMsg.getCreditCard_no()) ;
		  if ("".equals(uPosMsg.getBonRSCard_no()) 
				  || "".equals(uPosMsg.getCreditCard_no())){
			  KBStarUseYN = false;
			  LogUtility.getLogger().info("[Pump M] 신용카드가 아닌 경우 스타트럭 제외 KBStarUseYN="+KBStarUseYN) ;
		  }
			  
		  
		  
		  if (KBStarUseYN) {
			  SqlSession session = null;
			  try {
				  session = SqlSessionFactoryManager.openSqlSession();
				  // ksm 01 : 영업화물 , 02: VIP, 03:운수사 , 06:화물운전자복지카드, 07:택시결제카드, 08 : 택시우대카드, 09: 국민유가보조카드, 10: 국민유가 IC보너스BIN 
				  isKBDiscount = T_KH_BIN_INFOHandler.getHandler().isExist(session, "10", bin);
				  LogUtility.getLogger().debug("[국민 유가보조 카드 ] isKBDiscount?="+isKBDiscount);
			  } catch (Exception e) {
				  LogUtility.getLogger().error(e.getMessage(),e);
			  } finally {
				  SqlSessionFactoryManager.closeSqlSession(session);
			  }
		  } 
		  
		  if (isKBDiscount) {
			  LogUtility.getLogger().debug("[국민 유가보조 카드 ] 통합거래처 상품정보 생성");
			  sendUPOSMsg = CustUtil.calcBasePrice(uPosMsg, sendUPOSMsg);
			  //sendUPOSMsg.setFiller3(ICustConstant.KB_STARTLUCK_CARD_DISCOUNT);
			  // PI2, 여전법 대응에 따른 통합 Upos Message 적용, 2015.11.19 - cwi
			  sendUPOSMsg.setRepCustDiscount_Type(ICustConstant.KB_STARTLUCK_CARD_DISCOUNT);
			 
			  // pi2, 2016-02-16, 셀프 ODT에서 국민 STARTRUCLK카드 일 경우 현장할인이 되지 않도록 Led 코드를 0으로 설정 한다.
			  if(IUPOSConstant.DEVICE_TYPE_3S.equals(sendUPOSMsg.getDeviceType())){
				  
				  /*
				   * 우리카드 중 화물유가보조카드(영업화물)일 경우는 셀프 ODT에서도 현장할인 처리 되도록 한다.
				   * 2021.04 GS칼텍스 마케팅운영팀 김민성 선임 요청에 의하여 긴급 하드코딩 처리
				   * 
				   * 0190-6102-3211-0000 ~ 0190-6102-3310-9999 : FDT1904001 우리영업화물-개인신용
				   * 0190-6102-3311-0000 ~ 0190-6102-3320-9999 : FDT1904002 우리영업화물-법인체크
				   * 0190-6102-3321-0000 ~ 0190-6102-3330-9999 : FDT1904003 우리영업화물-외상거래
				   * 
				   * */
					LogUtility.getLogger().debug("[Pump M] => 보너스 카드 : " + uPosMsg.getBonRSCard_no());
	
					if(uPosMsg.getBonRSCard_no() != null) {
	
						// 보너스카드번호
						long bonRsCardNo = Long.parseLong(uPosMsg.getBonRSCard_no());
						
						// 0190-6102-3211-0000 ~ 0190-6102-3310-9999 : FDT1904001 우리영업화물-개인신용
						long cardArea01S = 190610232110000l;
						long cardArea01E = 190610233109999l;
						
						// 0190-6102-3311-0000 ~ 0190-6102-3320-9999 : FDT1904002 우리영업화물-법인체크
						long cardArea02S = 190610233110000l;
						long cardArea02E = 190610233209999l;
						
						// 0190-6102-3321-0000 ~ 0190-6102-3330-9999 : FDT1904003 우리영업화물-외상거래
						long cardArea03S = 190610233210000l;
						long cardArea03E = 190610233309999l;
	
						if((bonRsCardNo >= cardArea01S && bonRsCardNo <= cardArea01E) || (bonRsCardNo >= cardArea02S && bonRsCardNo <= cardArea02E) || (bonRsCardNo >= cardArea03S && bonRsCardNo <= cardArea03E)) {
							
							LogUtility.getLogger().info("[Pump M] => 해당 카드는 우리영업화물 카드로써 현장할인 한도조회가 가능하도록 한다." );
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
				  // 주유건에 대한 결제 진행 여부를 POS 에 통보
				  sendLockingInfoToPOS(uPosMsg.getNozzle_no(), sendUPOSMsg.getPosReceipt_no(), IConstant.PUMP_SALE_LOCKING) ;
		  }
		  LogUtility.getPumpMLogger().info("[Pump M] Send Pump Info to CAT M.") ;
		  sendMessage(preamble) ;
		  
	  } else if (custCard_car_type.equals(IUPOSConstant.CUSTCARD_CAR_TYPE_1) || custCard_car_type.equals(IUPOSConstant.CUSTCARD_CAR_TYPE_3)) {
		  // 거래처별/차량별 구분 (0:없음 1:거래처카드로 주유정보요청 2:차량단축번호조회 3: 차량번호로 주유정보요청 
		  //                                     4:영업화물거래처조회, 5:유가보조거래처 조회)
		  // 고객 카드 수행 요청
		  LogUtility.getPumpMLogger().info("[Pump M] Receive Customer processing from CAT M.") ;
		  
		  // 주유 정보가 있는지 먼저 확인.
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
			  //Led_code  V: Preset 가능, W:사용중
				String pump_amt = uPosMsg.getPump_amt();
				String nozzle_no = uPosMsg.getNozzle_no();
				// 주유기 상태정보 체크
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
			  // 주유 정보가 없기 때문에 Campaign 을 수행할 필요가 없음.
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
		  
//		  LogUtility.getPumpMLogger().debug("[Pump M] 주유 정보가 있습니다. 수행을 계속 진행합니다.") ;
		  sendLockingInfoToPOS(uPosMsg.getNozzle_no(), sendUPOSMsg.getPosReceipt_no(), IConstant.PUMP_SALE_LOCKING) ;
		  
		  String messageID = GlobalUtility.getUniqueMessageID() ;
		  String nozzle_no = uPosMsg.getNozzle_no() ;
		  String cust_card_ind = "" ;	// 차량번호/거래처카드번호여부 (01:차량번호 , 02:거래처카드번호)
		  String cust_card_no = uPosMsg.getCustCard_No() ;
		  String taxFreeCust_type = uPosMsg.getTaxFreeCust_type() ;
		  String fixedQty_yn = uPosMsg.getFixedQty_yn() ;
		  String fixedQty = GlobalUtility.getDividedWith1000(GlobalUtility.getStringValue(uPosMsg.getFixedQty())) ;
		  String goods_code = itemInfoItem.getGoodsCode() ;
		  String basePrice = GlobalUtility.getDividedWith1000(itemInfoItem.getUnitPrice_before_discount()) ;
		  String liter = GlobalUtility.getDividedWith1000(itemInfoItem.getOilAmount()) ;
		  String price = itemInfoItem.getOilPrice_before_discount() ;
		  String khTransactionID = itemInfoItem.getKhTransactionID() ;
		  // 고객카드수행 요청 전문(4291)에 거래처코드(filler2) 추가에 따른 수정 2009.10.27 edited by ykjang
		  //String cust_code = uPosMsg.getFiller2();
		  // PI2, 여전법 대응에 따른 통합 Upos Message 적용 - 구필러 수정(통합전문 확인), 2015.11.19 - cwi
		  String cust_code = uPosMsg.getCust_code(); // 거래처 코드
		  
		  if (custCard_car_type.equals(IUPOSConstant.CUSTCARD_CAR_TYPE_1)) {
			  cust_card_ind = ICode.CUST_CARD_IND_02 ;
		  } else {
			  cust_card_ind = ICode.CUST_CARD_IND_01 ;
		  }
		  
		  // 주유중인지 아닌지 구분해서 POS로 보낸다. POS요구사항(정동명차장) - upkoo		  
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
			  // 거래처 카드 요청을 POS 로 전송하여, POS 로 부터 응답을 받도록 한다.
			  sendMessage(preamble) ;
		  } catch (Exception e) {
			  LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		  }
	  } else if (custCard_car_type.equals(IUPOSConstant.CUSTCARD_CAR_TYPE_4) ) {
		  // 거래처별/차량별 구분 (0:없음 1:거래처카드로 주유정보요청 2:차량단축번호조회 3: 차량번호로 주유정보요청 
		  //                                     4:영업화물거래처조회, 5:유가보조거래처 조회)
		  // 영업화물 처리 (거래처 카드 + 영업화물 우대카드 + 신용카드(화물운전자 복지카드 / 거래카드)
		  LogUtility.getPumpMLogger().info("[Pump M] Receive Cargo processing from CAT M.") ;
		  
		  processEtcCust(uPosMsg, messageType, syncUnique, receiving_catm);
		  
	  } else if (custCard_car_type.equals(IUPOSConstant.CUSTCARD_CAR_TYPE_5) ) {
		  // 거래처별/차량별 구분 (0:없음 1:거래처카드로 주유정보요청 2:차량단축번호조회 3: 차량번호로 주유정보요청 
		  //                                     4:영업화물거래처조회, 5:유가보조거래처 조회)
		  // 유가보조 거래처 처리
		  LogUtility.getPumpMLogger().info("[Pump M] Receive OIL Price Support processing from CAT M.") ;
		  
		  processEtcCust(uPosMsg, messageType, syncUnique, receiving_catm);
		  
	  }else {
		  // 고객 차량 조회 요청
		  LogUtility.getPumpMLogger().info("[Pump M] Receive Car Inquiry from CAT M.") ;
		  String messageID = GlobalUtility.getUniqueMessageID() ;
		  String nozzle_no = uPosMsg.getNozzle_no() ;
		  String car_short_no = uPosMsg.getCustCard_No() ;
								
		  try {

			  PumpMSyncManager.setSyncData(syncUnique, messageID, SyncManager.DISE_PUMP_MODULE, receiving_catm) ;
					
			  POS_DT dtPumpM = new POS_DT(messageID, nozzle_no, car_short_no) ;
			  Preamble preamble = PumpMUtil.createPOSMessagePreamble(syncUnique, SyncManager.DISE_POS_ADAPTER, dtPumpM, "") ;
			  LogUtility.getPumpMLogger().info("[Pump M] Send CAR Inquiry to POS A.") ;
			  // 고객 차량 조회 요청을 POS 로 전송하여 응답을 받도록 한다.
			  sendMessage(preamble) ;					
		  } catch (Exception e) {
			  LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		  }   		  
	  }
    }

    /**
     * 영업화물 및 유가보조 거래 처리 프로세스
     * @param uPosMsg
     * @param messageType
     * @param syncUnique
     * @param receiving_catm
     */
    private void processEtcCust( UPOSMessage uPosMsg, int messageType, String syncUnique, Object receiving_catm){
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "CatMController/" + "processEtcCust()");
		
//    	 주유 정보가 있는지 먼저 확인.
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
			  
			  // 주유 정보가 없기 때문에 Campaign 을 수행할 필요가 없음.
			  Preamble preamble = PumpMUtil.createUPOSMessagePreamble(syncUnique, SyncManager.DISE_PUMP_MODULE, 
					  SyncManager.DISE_CMS_MODULE, sendUPOSMsg, "") ;		    				
			  LogUtility.getPumpMLogger().info("[Pump M] No Pump Info in table. Send that to CAT M.") ;
			  sendMessage(preamble) ;		
			  return ;
		  }
		  
		  itemInfoItem = sendUPOSMsg.getItem_info().getItemInfoList().get(0);
		  
//		  영업화물인 경우 재요청하기 때문에 두번째 요청에 대해서는 locking을 수행하지 않는다.
		  String messageID = GlobalUtility.getUniqueMessageID() ;
		  String nozzle_no = uPosMsg.getNozzle_no() ;
		  String cust_card_ind = "" ;	// 차량번호/거래처카드번호여부 (01:차량번호 , 02:거래처카드번호)
		  String cust_card_no = uPosMsg.getCustCard_No() ;
		  String taxFreeCust_type = uPosMsg.getTaxFreeCust_type() ;
		  String fixedQty_yn = uPosMsg.getFixedQty_yn() ;
		  String fixedQty = GlobalUtility.getDividedWith1000(GlobalUtility.getStringValue(uPosMsg.getFixedQty())) ;
		  String goods_code = itemInfoItem.getGoodsCode() ;
		  String basePrice = GlobalUtility.getDividedWith1000(itemInfoItem.getUnitPrice_before_discount()) ;
		  String liter = GlobalUtility.getDividedWith1000(itemInfoItem.getOilAmount()) ;
		  String price = itemInfoItem.getOilPrice_before_discount() ;
		  String khTransactionID = itemInfoItem.getKhTransactionID() ;
		  // 고객카드수행 요청 전문(4291)에 거래처코드(filler2) 추가에 따른 수정 2009.10.27 edited by ykjang
		  // String cust_code = uPosMsg.getFiller2();	
		  // String cust_code = uPosMsg.getTradeCondition().getContents(); // 거래처 코드
		  String cust_code = uPosMsg.getCust_code(); // 거래처 코드
		  
		  // 거래처카드가 없는 경우 거래처카드 필드에 차량번호가 들어옴. 이럴 때는 차량 단축번호로 조회
		  if (cust_card_no.length() < 16){
			  cust_card_ind = ICode.CUST_CARD_IND_01 ;
		  } else {
			  cust_card_ind = ICode.CUST_CARD_IND_02 ;
		  }
		  
		  // 주유중인지 아닌지 구분해서 POS로 보낸다. POS요구사항(정동명차장) - upkoo
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
			  // 거래처 카드 요청을 POS 로 전송하여, POS 로 부터 응답을 받도록 한다.
			  sendMessage(preamble) ;
		  } catch (Exception e) {
			  LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		  }
    }
    

    
	/**
	 * CAT으로 부터 Preset 정보를 받아서 다음을 수행한다. 
	 * 1. T_KH_PUMP_TR Table update 
	 * 2. POS 에 Preset 정보 전송
	 * 
	 * @param uniqueKey :
	 *            Preamble Object 의 Unique Key
	 * @param messageID :
	 *            CAT으로 부터 온 전문 Message ID
	 * @param pbWorkMsg :
	 *            CAT으로로 부터 온 전문
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
			// 2009년 4월 21일 추영대 추가.
			GlobalUtility.printAnalysisLog("pumpM", khproc_no, pbWorkMsg.getNozzleNo(), "", "프리셋", "", dqPumpMMsg.convertPOSContent(), "", "", "");

		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(	"[Pump M] !!!!!!!!!!AnalysisLOG 출력 실패!!!!!!!!!!!", e);
		} // end try

		sendMessage(dqPreamble);
	}

	/**
     * ksm 2011.12.13 PumpAController의 EN전문 전송 및 SEQ DB Insert/Update 로직임.
     * twsongkis 2016. 4. 14. 오후 15:48:31, PI2, songkis,  DB Insert/Update 테이블 수정.
     * 서일석유에서 미만주유시 POS로 EN전문 전송이 안된다고 수정 요청옴. (대리점 영업1팀 김재준 차장 요청 CSR)
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
			String qlBarcodeStr = ql_WMsg.getBarCode(); // 시스템(X)유효일자(YMMDD)세차할인금액(XX)SEQ(XXXX)발행매장코드(XXXX)
			
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
					//인서트 
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
					//인서트 
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
	 * POS에 단말기 버전 정보 전송
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
     * Locking , unLocking 전문을 POS 에 전송한다.
     * 
     * @param nozID				: 결제를 시작하는 노즐 번호
     * @param khTransactionNum	: KH 처리 번호
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
     * CAT 으로부터 요청 혹은 전송 받은 데이터를 다른 모듈에게 전송시 사용.
     * 각 모듈 및 어댑터에게 전송할 데이터의 타입은 아래와 같다.
     * 
     * 		POS Adapter		: Preamble		byte[]
     * 		Pump Adapter	: PumpPreamble	WorkingMessage
     * 		CAT Module		: Preamble		byte[]
     * 		State Module	: Preamble		WorkingMessage
     * 		Sale Module		: Preamble		byte[]
     * 
     * @param preambleData	: 전송할 데이터
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
	    	/* 2016. 4. 14. 오후 15:48:31, PI2, WooChul Jung. Add comment
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
	        	// 요청전문의 selfPayment_Type 값을 추후 응답전문에 추가하기 위해 selfPayment_typeHash에 저장해 놓음
	        	PumpMODTSaleManager.setNozzleBySelfPayment_type(uPosMsg.getNozzle_no(), uPosMsg.getSelfPayment_type());
	        	
	        	// 2015-12-17, PI2, cwi, 생성된 캠패인 정보를 다시 PUMPA로 전달,
	        	// ODT와 CAT의 요청에 따라 전송지를 다르게 하기 위해 ODT에서 보낸 전문일 경우 DeviceType 및 MessageType의 제약을 걸어 분류 작업 진행
	        	if(uPosMsg != null && (
	        			IUPOSConstant.MESSAGETYPE_4202.equals(uPosMsg.getMessageType())
	        				&& uPosMsg.getDeviceType().equals(IUPOSConstant.DEVICE_TYPE_3S))){  
	    			
	        		// 2016.01.12, PI2, songkis, 보너스카드번호가 없는 경우 CMS M(현장캠페인, 현장할인여부확인, 한도조회)로 갈 필요가 없음.
	        		// 2016.02.16, PI2, cwi, ODT로 부터 올라온 PromptDiscount_yn값이 0일 경우 현장할인 미적용 대상으로 인식
	    			if( pi2_phase.equals("1") && (GlobalUtility.isNullOrEmptyString(uPosMsg.getBonRSCard_no()) || uPosMsg.getPromptDiscount_yn().equals("0"))) {
	    				
		    			if( GlobalUtility.isNullOrEmptyString(uPosMsg.getBonRSCard_no()))
		    					LogUtility.getLogger().debug("[Pump M] 보너스카드번호가 없는 경우 CMSM(현장캠페인, 현장할인여부확인, 한도조회) 으로 갈 필요가 없음.") ;		
		    				else
		    					LogUtility.getLogger().debug("[Pump M] 현장할인 여부가 0일 경우 CMSM(현장캠페인, 현장할인여부확인, 한도조회) 으로 갈 필요가 없음.") ;
	    				
		    			// PumpA로 전송
		    	        GB_WorkingMessage gbWirking = new GB_WorkingMessage();

		    	        // PumpA에서는 WorkingMessage만 수신이 가능하기에 GB전문에 UPOSMessage 담아 전달 함.
		    	        gbWirking.setMessageType(uPosMsg.getMessageType());    // 응답 전문으로 셋팅 후 전달
		    	        gbWirking.setNozzleNo(PumpMUtil.getODTNumberFromNozzleNo(uPosMsg.getNozzle_no())); // ODT넘버 설정
		    	        gbWirking.setConnectNozzleNo(uPosMsg.getNozzle_no());
		    	        gbWirking.setUnityMessage(uPosMsg); //Upos Message 등록
		    	        
		    	        // PumpA로 전달 되도록 수신처를 PumpA로 변경
		    	        preambleData.setDest(SyncManager.DISE_PUMP_ADAPTER);
		    	        preambleData.setPreamble(gbWirking);
		    	        
		    	        LogUtility.getLogger().info("[Pump M] PumpA으로 전송") ;
		    	        getProducer_PumpMPumpA_Data().produce(preambleData) ;
	    			
	    			}else if("2".equals(pi2_phase) && ( GlobalUtility.isNullOrEmptyString(uPosMsg.getBonRSCard_no()) || "0".equals(uPosMsg.getPromptDiscount_yn()))){
	    				if( GlobalUtility.isNullOrEmptyString(uPosMsg.getBonRSCard_no()))
	    					LogUtility.getLogger().debug("[Pump M] 보너스카드번호가 없는 경우 CMSM(현장캠페인, 현장할인여부확인, 한도조회) 으로 갈 필요가 없음.") ;		
	    				else
	    					LogUtility.getLogger().debug("[Pump M] 현장할인 여부가 0일 경우 CMSM(현장캠페인, 현장할인여부확인, 한도조회) 으로 갈 필요가 없음.") ;
    				
		    			// PumpA로 전송
		    	        GB_WorkingMessage gbWirking = new GB_WorkingMessage();
	
		    	        // PumpA에서는 WorkingMessage만 수신이 가능하기에 GB전문에 UPOSMessage 담아 전달 함.
		    	        gbWirking.setMessageType(uPosMsg.getMessageType());    // 응답 전문으로 셋팅 후 전달
		    	        gbWirking.setNozzleNo(PumpMUtil.getODTNumberFromNozzleNo(uPosMsg.getNozzle_no())); // ODT넘버 설정
		    	        gbWirking.setConnectNozzleNo(uPosMsg.getNozzle_no());
		    	        gbWirking.setUnityMessage(uPosMsg); //Upos Message 등록
		    	        
		    	        // PumpA로 전달 되도록 수신처를 PumpA로 변경
		    	        preambleData.setDest(SyncManager.DISE_PUMP_ADAPTER);
		    	        preambleData.setPreamble(gbWirking);
		    	        
		    	        LogUtility.getLogger().info("[Pump M] Send UPOSMessage to PUMP A") ;
		    	        getProducer_PumpMPumpA_Data().produce(preambleData) ;
	    			}else if("2".equals(pi2_phase) && ( !GlobalUtility.isNullOrEmptyString(uPosMsg.getBonRSCard_no()) || uPosMsg.getPromptDiscount_yn().equals("1"))){
	    				// CatM으로 전송
	    				LogUtility.getLogger().info("[Pump M] Send UPOSMessage to CAT M") ;
	    				getProducer_CatM_Data().produce(preambleData) ;
	    			}
	    			
	    			 /**
					 * 프로젝트 : PI2
					 * 변경내용 : 충전기 ODT 캠페인 주유정보 요청 응답 ( 신규 )
					 * 변경일자 : 2015.12.23 양일준
					 */ 
		    		} else if(uPosMsg.getMessageType().equals(IUPOSConstant.MESSAGETYPE_4292) 
		    				&& uPosMsg.getDeviceType().equals(IUPOSConstant.DEVICE_TYPE_3O)){
		    			if(pi2_phase.equals("1") || uPosMsg.getPromptDiscount_yn().equals("0")) {  		    				
		    				// PumpA로 전송
		        	        SL_WorkingMessage slWorking = new SL_WorkingMessage();

		        	        // PumpA에서는 WoringMessage만 수신이 가능하기에 SL전문에 UPOSMessage 담아 전달 한다.
		        	        slWorking.setMessageType(uPosMsg.getMessageType());    // 응답 전문으로 셋팅 후 전달
		        	        slWorking.setNozzleNo(PumpMUtil.getODTNumberFromNozzleNo(uPosMsg.getNozzle_no())); // ODT넘버 설정
		        	        slWorking.setConnectNozzleNo(uPosMsg.getNozzle_no());
		        	        slWorking.setUnityMessage(uPosMsg); //Upos Message 등록
		        	        
		        	        // PumpA로 전달 되도록 수신처를 PumpA로 변경
		        	        preambleData.setDest(SyncManager.DISE_PUMP_ADAPTER);
		        	        preambleData.setPreamble(slWorking);
		        	        
		        	        LogUtility.getLogger().info("[Pump M] Send UPOSMessage to PUMP A") ;
		        	        
		        	        //uPosMsg.print();
		        	        getProducer_PumpMPumpA_Data().produce(preambleData) ;
		        	        
		    			} else {
		    				// CatM으로 전송
		    				LogUtility.getLogger().info("[Pump M] Send UPOSMessage to CAT M") ;
		    				getProducer_CatM_Data().produce(preambleData) ;
			    			
		    		}
	    		} else {
	    				// CatM으로 전송
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
     * ODT 로 부터 발생된 요청에 대한 응답 전문을 Pump A 및 Sales M 에 전송한다.
     * 
     * @param workingMsgArray		: Pump A 로 전송할 응답 전문
     * @param uPosMsg				: Sales M(결국 POS) 으로 전송할 응답 전문
     */
    public void sendRespondingMsgToPumpA(ArrayList<WorkingMessage> workingMsgArray, UPOSMessage uPosMsg) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "CatMController/" + "sendRespondingMsgToPumpA()");
		    	
    	// Pump A 로 WorkingMessage 를 전송한다.
    	if ((workingMsgArray != null) && (workingMsgArray.size() != 0)) {
    		for (int i = 0 ; i < workingMsgArray.size() ; i++) {
    			Preamble pumpPreamble = PumpMUtil.createWorkingMessagePreamble(null,
    					SyncManager.DISE_PUMP_ADAPTER, workingMsgArray.get(i) , "") ;
    			sendMessage(pumpPreamble) ;
    		}
    	}
		
    	// Sale M 으로 승인 응답 전문을 전송한다.
    	if (uPosMsg != null) {
			String nozzle_no = uPosMsg.getNozzle_no() ;
			try {
				int nozProtocolInt = PumpMUtil.getConnectedRepODTProtocolFromNozzleNo(nozzle_no) ;
				
				switch (nozProtocolInt) {
					case IPumpConstant.PUMP_PROTOCOL_SOMO :
					case IPumpConstant.PUMP_PROTOCOL_DaSNo : 
					case IPumpConstant.PUMP_PROTOCOL_NewDaSNo :
					case IPumpConstant.PUMP_PROTOCOL_Recharge :
				    	// Sale M 으로 응답 전문을 보낼지에 대한 판단을 한다.
			    		if (ODTUtility_Common.shouldSendToSaleM(uPosMsg)) {
			    			
			    			//2012.04.23 ksm 
					    	//승인응답전문의 LED_CODE=2(거절) 이고 Credit_AuthInfo(부가정보)값이 "CU"(단말기일련번호중복)인 경우 전문추적번호를 500 증가시킴 
					    	if("2".equals(uPosMsg.getLed_code())){
					    		LogUtility.getLogger().info("[Check TrackingNo] uPOS전문의 LED_CODE 값 = 2" ) ;
					    		
					    		// 거절사유가 "CU" 단말기일련번호중복인 경우 전문추적번호 증가 시킴.
					    		if("CU".equals(uPosMsg.getCredit_authInfo())){
					    			LogUtility.getLogger().info("[Check TrackingNo] 단말기일련번호 중복 발생!!!" ) ;
					    			
					    			// 전문추적번호 증가
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
     * ODT 로부터의 승인 요청에 대한 응답 전문을 Pump A 로 보낸다.
     * 응답 전문은 다쓰노 셀프 요청에 의한것인지 다른것 (소모, 충전기)에 의한것인지에 따라서 틀리다.
     * 
     * @param syncUnique	: Unique Key
     * @param uPosMsg		: 응답 전문
     */
    private void sendWorkingMessageToPumpA(String syncUnique, UPOSMessage uPosMsg) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "CatMController/" + "sendWorkingMessageToPumpA()");
		    	
    	// PreProcessing 처리
    	ODTUtility_Common.preProcessingUPOSMessageFromCAT(uPosMsg) ;

    	String nozzleNo = uPosMsg.getNozzle_no() ;
		int nozProtocolInt = PumpMUtil.getConnectedRepODTProtocolFromNozzleNo(nozzleNo) ;
		
		// 결제했음을 설정한다.[2008.11.18]
		PumpMTransactionManager.getInstance().setPayed(uPosMsg.getMessageType(), uPosMsg.getNozzle_no()) ;
		
		switch (nozProtocolInt) {
			case IPumpConstant.PUMP_PROTOCOL_DaSNo : 
			case IPumpConstant.PUMP_PROTOCOL_NewDaSNo :{ 	// 2012.09.26 ksm
				DasNoSelfPumpingManager.getInstance().addRespondUPOSMessage(uPosMsg.getNozzle_no(), uPosMsg) ;

				/**
				 * [2008.11.20] 응답 전문이 에러인 경우를 처리해야 한다. (승인 거절, 회선 불량) 인 경우의 처리 방법 해야 함.
				 */

				QL_WorkingMessage qlMsg = null;

				int protocolTypeInt = PumpMUtil.getConnectedODTProtocolFromNozzleNo(nozzleNo)  ;
					
				if (DasNoSelfPumpingManager.getInstance().isPumpingCompleted(nozzleNo)) {
					LogUtility.getPumpMLogger().info("[Pump M] Receive UPOSMessage after Pumping Completed.") ;
					String ledCode = uPosMsg.getLed_code() ;	// 주유완료 이후 재승인 혹은 선승인 취소 전문이 정상대로 승인이 났는지 여부 Flag
					String msgType = uPosMsg.getMessageType() ;	// 결제 유형이 현금인 경우에는 실패이더라도 정상적인 출력 영수증을 찍어준다.
					String khproc_no = uPosMsg.getPosReceipt_no() ;
					
					// 거절이 난 경우 && 현금 결제가 아닌 경우
					if (IConstant.reRequest && IUPOSConstant.RESPOND_LEDCODE_2.equals(ledCode) && !isCashYn(msgType)) {						// by 박종호
						
						if (protocolTypeInt == 90)
							qlMsg = ODTUtility_GSC_Self.createFailWhenReject(nozzleNo, uPosMsg) ;
						else
							qlMsg = ODTUtility_DaSNo.createFailWhenReject(nozzleNo, uPosMsg) ;
						
						Preamble pumpPreamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_PUMP_ADAPTER, qlMsg , "") ;
   							
						sendMessage(pumpPreamble) ;	
						
						DasNoSelfPumpingManager.getInstance().setFullPumpingStateFinal(nozzleNo) ;
					// TimeOut 이 난 경우
					} else if (IConstant.reRequest && IUPOSConstant.RESPOND_LEDCODE_3.equals(ledCode)) {
						// by 박종호
						
						//QL_WorkingMessage qlMsg = ODTUtility_DaSNo.createFailWhenTimeOut(nozzleNo, uPosMsg) ;
						if (protocolTypeInt == 90)
							qlMsg = ODTUtility_GSC_Self.createFailWhenReject(nozzleNo, uPosMsg) ;
						else
							qlMsg = ODTUtility_DaSNo.createFailWhenReject(nozzleNo, uPosMsg) ;
						Preamble pumpPreamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_PUMP_ADAPTER, qlMsg , "") ;
   							
						sendMessage(pumpPreamble) ;	
							
						DasNoSelfPumpingManager.getInstance().setFullPumpingStateFinal(nozzleNo) ;
					} else {
						// 응답 전문이 성공적인 경우
						// 외상 또는 현금 결제로 보너스 누적 또는 현금 영수증 처리가 실패를 해도 영수증 출력을 되야된다.
						if (DasNoSelfPumpingManager.getInstance().shouldCreateQLWorkMsgAndStore(nozzleNo, uPosMsg)) {
							UPOSMessage_ItemInfo_Item itemInfoItem = uPosMsg.getItem_info().getItemInfoList().get(0);
							//String khproc_no = uPosMsg.getPosReceipt_no() ; 함수 위로 이동.
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
								
							// 프린터할 정보를 저장한다.
							DasNoSelfPumpingManager.getInstance().setQLWorkingMessage(nozzleNo, qlMsg) ;
						}
			    		
						//보너스 누적, 현금 영수증, 외상보너스 누적 시 실패가 나도 POS에서 매출 처리를 위해 LED_CODE를 정상승인(1)으로 전송한다.
						if (isCashYn(msgType) && !IUPOSConstant.RESPOND_LEDCODE_1.equals(ledCode)){
							uPosMsg.setLed_code(IUPOSConstant.RESPOND_LEDCODE_1);
						}
						
						// 다시 승인 요청해야 할 전문이 더 있는 경우
						UPOSMessage pendingUposMsg = DasNoSelfPumpingManager.getInstance().getPendingUPOSMsgAndRemove(nozzleNo) ;
						if (pendingUposMsg != null) {
							// CAT M 으로 다시 승인 요청해야 할 전문이 있을 경우 전송한다.
							LogUtility.getPumpMLogger().info("[Pump M] Has more UPOSMessage to be sent to CAT M.") ;
							Preamble posPreamble = 
								PumpMUtil.createUPOSMessagePreamble(SyncManager.getUniqueKey(),SyncManager.DISE_PUMP_ADAPTER,SyncManager.DISE_CMS_MODULE,pendingUposMsg,"") ;
							sendMessage(posPreamble) ;
						} else {
							LogUtility.getPumpMLogger().info("[Pump M] Has no UPOSMessage to be sent to CAT M. Send QL Msg to Pump A") ;
			    				
							// 마지막 응답 전문이면 Pump A 로 보낼 전문을 찾아서 보내도록 한다.
							qlMsg = DasNoSelfPumpingManager.getInstance().getQLWorkingMessage(nozzleNo) ;
							Preamble pumpPreamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_PUMP_ADAPTER, qlMsg , "") ;
							
							sendMessage(pumpPreamble) ;		
							
							//ksm 2011.12.14 미만주유시 EN전문 전송위해 호출.
							processOPTBarcode(qlMsg, khproc_no);
							
							// 승인 해야 할 전문이 없다는 것은 마지막 응답 전문이라는 것이다. 따라서 아래 함수를 호출하여, 마지막임을 설정한다.
							DasNoSelfPumpingManager.getInstance().setFullPumpingStateFinal(nozzleNo) ;
						}
					}
					// POS 로 응답 전문을 전송한다.
					sendRespondingMsgToPumpA(null, uPosMsg) ;
				} else {
					LogUtility.getPumpMLogger().info("[Pump M] Receive UPOSMessage before Pumping Start.") ;

					DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(
							uPosMsg.getNozzle_no()).setFirstRespondUPOSMsg(uPosMsg) ;
					
					// Pump A 로 보내기 위해서 WorkingMessage (QM) 전문을 생성한다.
					ArrayList<WorkingMessage> workingMsgArray = 
			    		ODTUtility_Common.createWorkingMessageFromUPOSMessage(syncUnique, uPosMsg) ;

					// 4. Pump A 로는 WorkingMessage 를 전송하고, Sale M 으로는 응답 전문을 전송
					sendRespondingMsgToPumpA(workingMsgArray, uPosMsg) ;
					
					// Pump A 로 보내기 위해서 WorkingMessage (QL) 전문을 생성한다.
					// 선승인이 실패 났을 경우만 보내도록 한다.
					
					String ledCode = uPosMsg.getLed_code() ;
					if (!IUPOSConstant.RESPOND_LEDCODE_1.equals(ledCode)) {
						if (protocolTypeInt == 90)
							qlMsg = ODTUtility_GSC_Self.createFailWhenReject(nozzleNo, uPosMsg) ;
						else
							qlMsg = ODTUtility_DaSNo.createFailWhenReject(nozzleNo, uPosMsg) ;
						LogUtility.getPumpMLogger().debug("[Pump M] 선승인이 실패 : " + qlMsg);
						Preamble pumpPreamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_PUMP_ADAPTER, qlMsg , "") ;
   							
						sendMessage(pumpPreamble) ;
					}
				}

				break ;
			}
			// 신형 ODT 연동, PI2, 2016-03-31 - CWI
			// 응답전문의 처리를 위해 넘어온 전문은 ODT로 넘기며, ODT측에서 전문의 정상 처리 여부를 판단 후(LED code 등록)
			// GA 전문을 통해 응답 결과를 전송해 준다. 따라서, PUMP M 에서는 넘어온 전문을 WorkingMessage로 변환 후 ODT에 내려 주기만 한다.
			case IPumpConstant.PUMP_PROTOCOL_GSC_SELF :{ 	
			
				LogUtility.getLogger().info("[Pump M] Receive UPOSMessage before Pumping Start.") ;
				
				// Pump A 로 보내기 위해서 WorkingMessage (GB) 전문을 생성한다.
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
			// 다쓰노 셀프 & GSC Self를 제외한 셀프 ODT 인 경우
			default : {
				// Pump A 로 보내기 위해서 WorkingMessage 전문을 생성한다.
				ArrayList<WorkingMessage> workingMsgArray = 
		    		ODTUtility_Common.createWorkingMessageFromUPOSMessage(syncUnique, uPosMsg) ;

				// Pump A 로는 WorkingMessage 를 전송하고, Sale M 으로는 응답 전문을 전송
	    		sendRespondingMsgToPumpA(workingMsgArray, uPosMsg) ;
				break ;
			}
		}
    }
    
    // Cat의 주유정보 요청에 대한 정상적인 응답건에 대해서 바코드 filler5에 셋팅해준다.
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
     * BEACON 로부터의 승인 요청에 대한 응답 전문을 Pump A 로 보낸다.
     * 
     * @param syncUnique	: Unique Key
     * @param uPosMsg		: 응답 전문
     */
    private void sendBeaconMessageToBeaconM(String syncUnique, UPOSMessage uPosMsg) {
    	
    	BeaconMessage beaconMsg = BeaconUtility.createBeaconMessageFromUPOSMessage(syncUnique,uPosMsg);
		
    	// Pump A 로 WorkingMessage 를 전송한다.
		Preamble pumpPreamble = PumpMUtil.createBeaconMessagePreamble(syncUnique,
				SyncManager.DISE_BEACON_MODULE, beaconMsg , "") ;
		LogUtility.getLogger().info("[Pump M] <Beacon Process> PumpM -> BeaconM 전송 : " + uPosMsg.getMessageType()) ;
		sendMessage(pumpPreamble) ;
    }
    
    /**
	 * 
	 * 
     * 주문정보 응답을 처리한다.
     * 
     * 
     * 1. 주문정보 정상일때 
     * 1.1 노즐 상태를 체크한다.
     * 1.2 노즐 사용 가능 할때 유종 일치 여부를 체크한다.
     * 
     * 2. 주문정보 응답전문을 ARK로 전송한다.
     * 
     * @param messageID : 메세지ID
     *        uPosMsg   : 주문정보 응답
     */
	private void process0222(String messageID, UPOSMessage uPosMsg) {
		//1.주문 정보 정상일때 유종 체크
		  
		  LogUtility.getLogger().info("[Pump M] <Beacon Process> 0222 처리") ;
		  
		  String filler2 = uPosMsg.getFiller2();

		  BeaconMessage bMsg = ByteArrayToBeaconMessage.createBeaconMessage(filler2.getBytes());
		  
		  if(bMsg != null){
			  //주문정상시
			  if("1".equals(bMsg.getOrderSt())){

				  boolean state = PumpAController.checkNozState(bMsg.getDisplayArkId());
				  
				  
				  //노즐사용 가능
				  if(state){
					  	bMsg.setNozStats("0");	// 노즐 사용 가능 
					  	
					  	boolean gdCd = BeaconDataHandler.checkGoodsCode(bMsg.getNozNo(), bMsg.getGdsCode());
					  	
					  	if (gdCd){
					  		bMsg.setGdsCheck("1");	// 유종 일치 
					  	} else {
					  		bMsg.setGdsCheck("2");	// 유종 불일치 
					  	}
				  } else {
					  	//노즐사용중
					    bMsg.setNozStats("1");
				  }
				  
				  
				  byte[] bm_jbFiller2 = BeaconMessageToByteArray.createBeaconMessageByteArray(bMsg);
				  uPosMsg.setFiller2(new String(bm_jbFiller2));
				  
					
			  }else{
				  LogUtility.getLogger().debug("[Pump M] <Beacon Process> 0222 주문정보 상태코드 => " + bMsg.getOrderSt() +" dArk no => " + bMsg.getDisplayArkId()) ;    
			  }
		  }else{
			  LogUtility.getLogger().debug("[Pump M] <Beacon Process> 0222 주문정보 상태코드 없음 dArk no => " + bMsg.getDisplayArkId()) ;
		  }
		  
		  //beacon전문 인 경우 beacon으로 전달 
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
