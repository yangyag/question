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
     * Pump M ���� ���� ���� KixxHub Port �ʱ�ȭ ������ Pump A �� �����մϴ�. (M1 ����)
     */
    @Override
	protected void onReceivingPumpAInit(Object input_PumpM_init) {
    	LogUtility.getPumpALogger().debug("[Pump A] M1 ������ Pump Adapter ���� �����ϴ�.") ;    
    	Preamble pumpPreamble = (Preamble) input_PumpM_init ;
    	WorkingMessage workingMessage = (WorkingMessage) pumpPreamble.getPreamble() ;
    	
    	if (adaptor != null) {
    		adaptor.init(workingMessage) ;
    	} 
    }
 
    /**
     * Pump M ���� ���� Pump A �� ������ �����մϴ�.
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
        	LogUtility.getPumpALogger().debug("Pump Adapter �ʱ�ȭ�� �����Ͽ� ������ ������ �� �����ϴ�") ;    	
    	}
    }
 
    @Override
	public void start() throws Exception {
    	LogUtility.getPumpALogger().debug("[Pump A] Pump Adapter �� �ʱ�ȭ �մϴ�.") ;    
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
