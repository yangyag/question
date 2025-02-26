package com.gsc.kixxhub.device.pumpa.bundle;

 
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.common.utility.sync.Preamble;
import com.gsc.kixxhub.common.utility.sync.SyncManager;
import com.gsc.kixxhub.device.pumpa.AdaptorServiceImp;
import com.gsc.kixxhub.device.pumpa.service.AdaptorService;
import com.gsc.kixxhub.device.pumpa.service.listener.AdaptorListener;

public class PumpA extends PumpABase {
 
	class PumpAListener implements AdaptorListener {

		public boolean sendDeviceMsg(WorkingMessage obj) {
			WorkingMessage receivingWorkMessage = obj ;
			
			Preamble pumpPreamble = 
				Preamble.createPreamble(receivingWorkMessage.getMessageID() ,
						SyncManager.DISE_PUMP_ADAPTER,
						SyncManager.DISE_PUMP_MODULE,
						receivingWorkMessage,
						"") ;
			
//			LogUtility.getPumpALogger().debug("[Pump A] -> [Pump M] NoID =  " + receivingWorkMessage.getNozzleNo() + " , "
//					+ "CommandID = " + receivingWorkMessage.getCommand()) ;
			
			getProducer_PumpMPumpA_Data().produce(pumpPreamble) ;

			return true ;
		}

    	
    }
	private AdaptorService adaptor = null ;

    private PumpAListener listener = null ;
 
    /**
     * Pump M 으로 부터 받은 KixxHub Port 초기화 전문을 Pump A 로 전송합니다. (M1 전문)
     */
    @Override
	protected void onReceivingPumpAInit(Object input_PumpM_init) {
    	LogUtility.getPumpALogger().debug("[Pump A] M1 전문을 Pump Adapter 에게 보냅니다.") ;    
    	Preamble pumpPreamble = (Preamble) input_PumpM_init ;
    	WorkingMessage workingMessage = (WorkingMessage) pumpPreamble.getPreamble() ;
    	
    	if (adaptor != null) {
    		adaptor.init(workingMessage) ;
    	} 
    }
 
    /**
     * Pump M 으로 부터 Pump A 로 전문을 전송합니다.
     */
    @Override
	protected void onReceivingPumpM(Object input_PumpM) {
    	
    	Preamble pumpPreamble = (Preamble) input_PumpM ;
    	WorkingMessage workingMessage = (WorkingMessage) pumpPreamble.getPreamble() ;

    	LogUtility.getPumpALogger().debug("[Pump M] -> [Pump A] NoID =  " + workingMessage.getNozzleNo() + " , "
				+ "CommandID = " + workingMessage.getCommand()) ;    	
    	
    	if (adaptor != null) {
    		adaptor.sendModuleMsg(workingMessage) ;
    	} else {
        	LogUtility.getPumpALogger().debug("Pump Adapter 초기화가 실패하여 전문을 전송할 수 없습니다") ;    	
    	}
    }
 
    @Override
	public void start() throws Exception {
    	LogUtility.getPumpALogger().debug("[Pump A] Pump Adapter 를 초기화 합니다.") ;    
		registerListener() ;

	//        super.start();
        listener = new PumpAListener();		
		adaptor = new AdaptorServiceImp();
		adaptor.setListener(listener);
		
    }
    
    @Override
	public void stop() throws Exception {
//        super.stop();
        adaptor.removeListener(listener) ;
//        adaptor.destroy() ;
        this.listener = null ;
    }
}
