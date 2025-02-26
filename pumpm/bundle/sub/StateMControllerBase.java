package com.gsc.kixxhub.module.pumpm.bundle.sub;

 
import com.gsc.kixxhub.common.data.ITopicConstant;
import com.gsc.kixxhub.eventhandler.action.BaseBundleActivator;
import com.gsc.kixxhub.eventhandler.action.EventHandlerListener;
import com.gsc.kixxhub.eventhandler.service.IEventProducer;
import com.gsc.kixxhub.eventhandler.service.ITopicEvent;

abstract public class StateMControllerBase extends BaseBundleActivator {

    private class OnReceivingStateMInvoker extends EventHandlerListener {

        private final String[] inputs = new String[] { ITopicConstant.TOPIC_AmsM_PumpMAmsM_Data };

        @Override
		public String[] getTopics() {
            return inputs;
        }

        @Override
		public void process(ITopicEvent[] events) {
            onReceivingAmsMData(events[0].getData());
        }
    }
    private class OnSendingStateMInvoker extends EventHandlerListener {

        private final String[] inputs = new String[] { ITopicConstant.TOPIC_PumpM_PumpMAmsM_Data };

        @Override
		public String[] getTopics() {
            return inputs;
        }

        @Override
		public void process(ITopicEvent[] events) {
            onReceivingPumpMData(events[0].getData());
        }
    }
    private IEventProducer producer_AmsM_PumpState = null;
    private IEventProducer producer_PumpMPosA_Data = null;
    private IEventProducer producer_PumpMPumpA_Data = null;
    private IEventProducer statemcontrollerOutput_catm = null;
    
    private IEventProducer statemcontrollerOutput_posa = null;
    private IEventProducer statemcontrollerOutput_salem = null;
    
    protected IEventProducer getProducer_AmsM_PumpState() {
        if (producer_AmsM_PumpState == null) {
            producer_AmsM_PumpState = getProducerSite(ITopicConstant.TOPIC_PumpM_AmsM_PumpState).getProducer();
        }
        return producer_AmsM_PumpState;
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

    protected IEventProducer getStatemcontrollerOutput_catmProducer() {
        if (statemcontrollerOutput_catm == null) {
            statemcontrollerOutput_catm = getProducerSite("statemcontroller.output_catm").getProducer();
        }
        return statemcontrollerOutput_catm;
    }

    protected IEventProducer getStatemcontrollerOutput_posaProducer() {
        if (statemcontrollerOutput_posa == null) {
            statemcontrollerOutput_posa = getProducerSite("statemcontroller.output_posa").getProducer();
        }
        return statemcontrollerOutput_posa;
    }

    protected IEventProducer getStatemcontrollerOutput_salemProducer() {
        if (statemcontrollerOutput_salem == null) {
            statemcontrollerOutput_salem = getProducerSite("statemcontroller.output_salem").getProducer();
        }
        return statemcontrollerOutput_salem;
    }

    abstract protected void onReceivingAmsMData(Object receiving_statem);

    abstract protected void onReceivingPumpMData(Object sending_statem);
    
    @Override
	public void registerListener() {
        addEventProcessor(new OnReceivingStateMInvoker());
        addEventProcessor(new OnSendingStateMInvoker());
	}
}
