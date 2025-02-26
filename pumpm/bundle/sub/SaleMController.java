package com.gsc.kixxhub.module.pumpm.bundle.sub;

 
import com.gsc.kixxhub.common.data.upos.UPOSMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.common.utility.sync.Preamble;
import com.gsc.kixxhub.common.utility.sync.SyncManager;
import com.gsc.kixxhub.module.pumpm.pump.util.ODTUtility_Common;

public class SaleMController extends SaleMControllerBase {
	
	private boolean logSSDC=false;

    /**
     * Sale M 에게 전문 송신.
     * Sale M 에게 전송할 전문은 응답 전문 및 주유완료 전문이 있다.
     * 
     * @param sending_salem	: Sale M 에게 전송할 전문
     */
    @Override
	protected void onReceivingPumpMData(Object sending_salem) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "SaleMController/" + "onSendingSaleM()");
		
		LogUtility.getLogger().info("[모바일플랫폼결제 LOG] : SSDC/" + "PUMP_M/" + "SaleMController/" + "onSendingSaleM()");
		
        sendMessage((Preamble)sending_salem) ; 
    }
 
    /**
     * Sale M 으로 보내기 전에 아래의 일들을 행한 이후 전송한다.
     *  1. 주유건의 결제 응답 전문에서 lastPayment_yn 을 설정한다. 이는 0001 전문이 올라왔을 경우에만 1 로 설정한다.
     *  2. 응답 전문을 PumpMODTSaleManager 에 저장하여 차후 취소 요청 전문이 있을 경우 이용하도록 한다.
     *  3. 거래처로 인한 결제 응답 인 경우 거래처 정보를 UPOSMessage 응답 전문에 포함하도록 한다.
     * 
     * @param preambleData
     */
    private void preProcessingBeforeSendingToSaleM(Preamble preambleData) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "SaleMController/" + "preProcessingBeforeSendingToSaleM()");
		
    	ODTUtility_Common.preProcessingBeforeSendingToSaleM(preambleData) ;
    }
 
    /**
     * SaleMController 에서 처리되어서 Sale M 에게 데이터를 전송한다.
     * SaleM 에 전송되는 데이터는 다음과 같다.
     * 		1. 승인/승인 취소 응답 전문
     * 		2. 주유완료 전문
     * 
     * @param preambleData	: 전송할 Preamble Object
     */
    private void sendMessage(Preamble preambleData) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "SaleMController/" + "sendMessage()");
		
    	int dest = preambleData.getDest() ;
    	switch (dest) {
	    	case SyncManager.DISE_POS_ADAPTER :
	    	case SyncManager.DISE_PUMP_ADAPTER :
	    	case SyncManager.DISE_CAT_MODULE :
	    	case SyncManager.DISE_STATE_MODULE :
	    		break ; 
	    	case SyncManager.DISE_SALE_MODULE : {
	    		LogUtility.getLogger().info("[모바일플랫폼결제 LOG] : SSDC/" + "PUMP_M/" + "SaleMController/" + "sendMessage()");
	    		Object obj = preambleData.getPreamble() ;
	    		/**
	    		 * 2016.04.01 WooChul Jung
	    		 * 	충전기의 경우, 판매완료 전문을 Pump A 에서 충전기로 부터 수신받게 되면, 0001 전문과 주유완료 전문을 동시에 보냄.
	    		 *  그리고 판매완료 전문은, 모든 결제가 완료되지 마자 충전기로 부터 올라옴.
	    		 *  따라서 다음과 같은 Case 가 발생하게 됨.
	    		 *  	충전기 -> 0032, T4 -> Pump A -> 0032,S4,SH -> Pump M
	    		 *  	-> 0032,DG,0001 -> Sale M -> 0032,DG,0032,0001,0032 -> POS A
	    		 *  Sale M 의 경우 DG 혹은 0001 이 수신받았을때 결제 미전송 내역을 POS A 로 다시 전송하게 되어 있음. 이로 인하여 0032 가 
	    		 *  POS A 로 세번 전송하게 됨.
	    		 *  
	    		 *  이로 인하여 Pump M 에서 Sale M 으로 전문 송신시 500 miliseconds 를 Sleep 하도록 함. 
	    		 *  이렇게 했을 경우, 충전기 뿐만 아니라 다른 ODT 의 전문들에도 Sleep 이 발생하지만 영향도는 없음
	    		 *  
	    		 *  2016.05.19 WooChul Jung
	    		 *  	외상응답전문과 판매완료 전문이 동시에 올라올 경우 아래 문제가 발생할 수 있음.
	    		 *  	외상응답전문 수신
	    		 *  		외상정보 입력 전에
	    		 *  	판매완료 전문 수신 - 해당 노즐의 모든 정보 초기화
	    		 *  	이후 외상응답전문에 외상정보 입력하고자 하나, 정보가 초기화 되었기 때문에, 없음.
	    		 *  	이로 인하여 preProcessingBeforeSendingToSaleM 함수 수행 이후 500 miliseconds 대기로 수정.
	    		 */
	    		// 1. 승인/승인 취소 응답 전문
	    		if (obj instanceof UPOSMessage) {
	    			LogUtility.getLogger().info("[모바일플랫폼결제 LOG] : SSDC/" + "PUMP_M/" + "SaleMController/" + "sendMessage() : UPOSMessage");
	    			/* 2017.05.16 beacon 결제인 경우 lastPayment_yn을 전문에서
	    			 * 전달 하는 값으로 사용하기 위해 분기 처리 
	    			 */
	    			UPOSMessage uPosMsg = (UPOSMessage) obj ;
	    			
	    			if(!"3M".equals(uPosMsg.getDeviceType())){
	    				preProcessingBeforeSendingToSaleM(preambleData) ;
	    			}
					/* 2012.06.15 ksm  현재 필요없는 로직이므로 주석처리.
	    			int messageType = Integer.parseInt(uPosMsg.getMessageType()) ;
	    			switch (messageType) {
	    				default : {
	    					getSalemcontrollerOutput_salemProducer().produce(preambleData) ;
	    					break ;
	    				}
	    			}*/
					try {
						Thread.sleep(500) ;
					} catch (InterruptedException e) {
						LogUtility.getLogger().error(e.getMessage(),e) ;
					}
					getProducer_SaleM_TrData().produce(preambleData) ;
	    		// 2. 주유완료 전문
	    		} else {
	    			try {
						Thread.sleep(500) ;
					} catch (InterruptedException e) {
						LogUtility.getLogger().error(e.getMessage(),e) ;
					}
	    			getProducer_SaleM_TrData().produce(preambleData) ;
	    		}
	    		break ; 
	    	}
    	}
    }
 
    @Override
	public void start() {
		registerListener() ;
        // TODO generated stub, insert your logic here.
 
    }
    
    @Override
	public void stop() {
        // TODO generated stub, insert your logic here.
    }

}
