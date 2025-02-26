package com.gsc.kixxhub.module.pumpm.bundle.sub;
 
import com.gsc.kixxhub.common.data.ITopicConstant;
import com.gsc.kixxhub.eventhandler.action.BaseBundleActivator;
import com.gsc.kixxhub.eventhandler.action.EventHandlerListener;
import com.gsc.kixxhub.eventhandler.service.IEventProducer;
import com.gsc.kixxhub.eventhandler.service.ITopicEvent;

abstract public class CatMControllerBase extends BaseBundleActivator {

    private class OnReceivingCatMInvoker extends EventHandlerListener {

        private final String[] inputs = new String[] { ITopicConstant.TOPIC_CatM_PumpMCatM_Data };

        @Override
		public String[] getTopics() {
            return inputs;
        }

        @Override
		public void process(ITopicEvent[] events) {
            onReceivingCatMData(events[0].getData());
        }
    }
    private class OnReceivingPumpMInvoker extends EventHandlerListener {

        private final String[] inputs = new String[] { ITopicConstant.TOPIC_PumpAController_CatMController_Data };

        @Override
		public String[] getTopics() {
            return inputs;
        }

        @Override
		public void process(ITopicEvent[] events) {
        	onReceivingCatMData(events[0].getData());
        }
    }
    private class OnSendingCatMInvoker extends EventHandlerListener {

        private final String[] inputs = new String[] { ITopicConstant.TOPIC_PumpM_PumpMCatM_Data };

        @Override
		public String[] getTopics() {
            return inputs;
        }

        @Override
		public void process(ITopicEvent[] events) {
            onReceivingPumpMData(events[0].getData());
        }
    }
    private IEventProducer catmcontrollerOutput_catm = null;
    private IEventProducer catmcontrollerOutput_statem = null;

    private IEventProducer producer_PumpMPosA_Data = null;
    private IEventProducer producer_PumpMPumpA_Data = null;
    
    private IEventProducer producer_PumpMSaleM_Data = null;
    private IEventProducer producer_PumpMBeaconM_Data = null;

    protected IEventProducer getCatmcontrollerOutput_statemProducer() {
        if (catmcontrollerOutput_statem == null) {
            catmcontrollerOutput_statem = getProducerSite("catmcontroller.output_statem").getProducer();
        }
        return catmcontrollerOutput_statem;
    }
    
    protected IEventProducer getProducer_CatM_Data() {
        if (catmcontrollerOutput_catm == null) {
            catmcontrollerOutput_catm = getProducerSite(ITopicConstant.TOPIC_PumpM_CatM_Data).getProducer();
        }
        return catmcontrollerOutput_catm;
    }
    

	protected IEventProducer getProducer_PumpMPosA_Data() {
        if (producer_PumpMPosA_Data == null) {
            producer_PumpMPosA_Data = getProducerSite(ITopicConstant.TOPIC_PumpM_PumpMPosA_Data).getProducer();
        }
        return producer_PumpMPosA_Data;
    }


    protected IEventProducer getProducer_PumpMPumpA_Data() {
        if (producer_PumpMPumpA_Data == null) {
            producer_PumpMPumpA_Data = getProducerSite(ITopicConstant.TOPIC_PumpM_PumpMPumpA_Data).getProducer();
        }
        return producer_PumpMPumpA_Data;
    }

    protected IEventProducer getProducer_PumpMSaleM_Data() {
        if (producer_PumpMSaleM_Data == null) {
            producer_PumpMSaleM_Data = getProducerSite(ITopicConstant.TOPIC_PumpM_PumpMSaleM_Data).getProducer();
        }
        return producer_PumpMSaleM_Data;
    }
    
    protected IEventProducer getProducer_BeaconM_Data() {
        if (producer_PumpMBeaconM_Data == null) {
        	producer_PumpMBeaconM_Data = getProducerSite(ITopicConstant.TOPIC_PumpM_BeaconM_Data).getProducer();
        }
        return producer_PumpMBeaconM_Data;
    }


    abstract protected void onReceivingCatMData(Object receiving_catm);

    abstract protected void onReceivingPumpMData(Object sending_catm);

    @Override
	public void registerListener() {
        addEventProcessor(new OnReceivingCatMInvoker());
        addEventProcessor(new OnSendingCatMInvoker());	
        addEventProcessor(new OnReceivingPumpMInvoker());	
	}
}
