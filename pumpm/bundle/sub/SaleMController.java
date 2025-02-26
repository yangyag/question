package com.gsc.kixxhub.module.pumpm.bundle.sub;

 
import com.gsc.kixxhub.common.data.upos.UPOSMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.common.utility.sync.Preamble;
import com.gsc.kixxhub.common.utility.sync.SyncManager;
import com.gsc.kixxhub.module.pumpm.pump.util.ODTUtility_Common;

public class SaleMController extends SaleMControllerBase {
	
	private boolean logSSDC=false;

    /**
     * Sale M ���� ���� �۽�.
     * Sale M ���� ������ ������ ���� ���� �� �����Ϸ� ������ �ִ�.
     * 
     * @param sending_salem	: Sale M ���� ������ ����
     */
    @Override
	protected void onReceivingPumpMData(Object sending_salem) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "SaleMController/" + "onSendingSaleM()");
		
		LogUtility.getLogger().info("[������÷������� LOG] : SSDC/" + "PUMP_M/" + "SaleMController/" + "onSendingSaleM()");
		
        sendMessage((Preamble)sending_salem) ; 
    }
 
    /**
     * Sale M ���� ������ ���� �Ʒ��� �ϵ��� ���� ���� �����Ѵ�.
     *  1. �������� ���� ���� �������� lastPayment_yn �� �����Ѵ�. �̴� 0001 ������ �ö���� ��쿡�� 1 �� �����Ѵ�.
     *  2. ���� ������ PumpMODTSaleManager �� �����Ͽ� ���� ��� ��û ������ ���� ��� �̿��ϵ��� �Ѵ�.
     *  3. �ŷ�ó�� ���� ���� ���� �� ��� �ŷ�ó ������ UPOSMessage ���� ������ �����ϵ��� �Ѵ�.
     * 
     * @param preambleData
     */
    private void preProcessingBeforeSendingToSaleM(Preamble preambleData) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "SaleMController/" + "preProcessingBeforeSendingToSaleM()");
		
    	ODTUtility_Common.preProcessingBeforeSendingToSaleM(preambleData) ;
    }
 
    /**
     * SaleMController ���� ó���Ǿ Sale M ���� �����͸� �����Ѵ�.
     * SaleM �� ���۵Ǵ� �����ʹ� ������ ����.
     * 		1. ����/���� ��� ���� ����
     * 		2. �����Ϸ� ����
     * 
     * @param preambleData	: ������ Preamble Object
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
	    		LogUtility.getLogger().info("[������÷������� LOG] : SSDC/" + "PUMP_M/" + "SaleMController/" + "sendMessage()");
	    		Object obj = preambleData.getPreamble() ;
	    		/**
	    		 * 2016.04.01 WooChul Jung
	    		 * 	�������� ���, �ǸſϷ� ������ Pump A ���� ������� ���� ���Źް� �Ǹ�, 0001 ������ �����Ϸ� ������ ���ÿ� ����.
	    		 *  �׸��� �ǸſϷ� ������, ��� ������ �Ϸ���� ���� ������� ���� �ö��.
	    		 *  ���� ������ ���� Case �� �߻��ϰ� ��.
	    		 *  	������ -> 0032, T4 -> Pump A -> 0032,S4,SH -> Pump M
	    		 *  	-> 0032,DG,0001 -> Sale M -> 0032,DG,0032,0001,0032 -> POS A
	    		 *  Sale M �� ��� DG Ȥ�� 0001 �� ���Ź޾����� ���� ������ ������ POS A �� �ٽ� �����ϰ� �Ǿ� ����. �̷� ���Ͽ� 0032 �� 
	    		 *  POS A �� ���� �����ϰ� ��.
	    		 *  
	    		 *  �̷� ���Ͽ� Pump M ���� Sale M ���� ���� �۽Ž� 500 miliseconds �� Sleep �ϵ��� ��. 
	    		 *  �̷��� ���� ���, ������ �Ӹ� �ƴ϶� �ٸ� ODT �� �����鿡�� Sleep �� �߻������� ���⵵�� ����
	    		 *  
	    		 *  2016.05.19 WooChul Jung
	    		 *  	�ܻ����������� �ǸſϷ� ������ ���ÿ� �ö�� ��� �Ʒ� ������ �߻��� �� ����.
	    		 *  	�ܻ��������� ����
	    		 *  		�ܻ����� �Է� ����
	    		 *  	�ǸſϷ� ���� ���� - �ش� ������ ��� ���� �ʱ�ȭ
	    		 *  	���� �ܻ����������� �ܻ����� �Է��ϰ��� �ϳ�, ������ �ʱ�ȭ �Ǿ��� ������, ����.
	    		 *  	�̷� ���Ͽ� preProcessingBeforeSendingToSaleM �Լ� ���� ���� 500 miliseconds ���� ����.
	    		 */
	    		// 1. ����/���� ��� ���� ����
	    		if (obj instanceof UPOSMessage) {
	    			LogUtility.getLogger().info("[������÷������� LOG] : SSDC/" + "PUMP_M/" + "SaleMController/" + "sendMessage() : UPOSMessage");
	    			/* 2017.05.16 beacon ������ ��� lastPayment_yn�� ��������
	    			 * ���� �ϴ� ������ ����ϱ� ���� �б� ó�� 
	    			 */
	    			UPOSMessage uPosMsg = (UPOSMessage) obj ;
	    			
	    			if(!"3M".equals(uPosMsg.getDeviceType())){
	    				preProcessingBeforeSendingToSaleM(preambleData) ;
	    			}
					/* 2012.06.15 ksm  ���� �ʿ���� �����̹Ƿ� �ּ�ó��.
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
	    		// 2. �����Ϸ� ����
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
