package com.gsc.kixxhub.device.pumpa.bundle;

 
import com.gsc.kixxhub.common.data.ITopicConstant;
import com.gsc.kixxhub.eventhandler.action.BaseBundleActivator;
import com.gsc.kixxhub.eventhandler.action.EventHandlerListener;
import com.gsc.kixxhub.eventhandler.service.IEventProducer;
import com.gsc.kixxhub.eventhandler.service.ITopicEvent;

abstract public class PumpABase extends BaseBundleActivator {

    private class OnInputPumpAStartInvoker extends EventHandlerListener {

        private final String[] inputs = new String[] { ITopicConstant.TOPIC_PumpM_PumpA_Init };

        @Override
		public String[] getTopics() {
            return inputs;
        }

        public void process(ITopicEvent[] events) {
            onReceivingPumpAInit(events[0].getData());
        }
    }

    private class OnInputPumpMInvoker extends EventHandlerListener {
 
    	private final String[] inputs = new String[] { ITopicConstant.TOPIC_PumpM_PumpA_Data };

        @Override
		public String[] getTopics() {
            return inputs;
        }
 
        public void process(ITopicEvent[] events) {
            onReceivingPumpM(events[0].getData());
        }
    }
    private IEventProducer producer_PumpMPumpA_Data = null;

    protected IEventProducer getProducer_PumpMPumpA_Data() {
        if (producer_PumpMPumpA_Data == null) {
            producer_PumpMPumpA_Data = getProducerSite(ITopicConstant.TOPIC_PumpA_PumpMPumpA_Data).getProducer();
        }
        return producer_PumpMPumpA_Data;
    }
 
    abstract protected void onReceivingPumpAInit(Object input_PumpM_init);
 
	abstract protected void onReceivingPumpM(Object input_PumpM);

    @Override
	public void registerListener() {
        addEventProcessor(new OnInputPumpAStartInvoker());
        addEventProcessor(new OnInputPumpMInvoker());		
	}

}
