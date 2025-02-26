package com.gsc.kixxhub.module.pumpm.bundle.sub;

 
import com.gsc.kixxhub.common.data.ITopicConstant;
import com.gsc.kixxhub.eventhandler.action.BaseBundleActivator;
import com.gsc.kixxhub.eventhandler.action.EventHandlerListener;
import com.gsc.kixxhub.eventhandler.service.IEventProducer;
import com.gsc.kixxhub.eventhandler.service.ITopicEvent;
 
abstract public class PosAControllerBase extends BaseBundleActivator {

    private class OnInputPosaStartInvoker extends EventHandlerListener {

        private final String[] inputs = new String[] { ITopicConstant.TOPIC_PosA_PumpMPosA_InitReq };

        @Override
		public String[] getTopics() {
            return inputs;
        }

        @Override
		public void process(ITopicEvent[] events) {
            onReceivingPosAInitReq((String) events[0].getData());
        }
    }
	private class OnInputPumpACompletedInvoker extends EventHandlerListener {

        private final String[] inputs = new String[] { ITopicConstant.TOPIC_PumpM_PumpMPosA_InitCompleted };

        @Override
		public String[] getTopics() {
            return inputs;
        }

        @Override
		public void process(ITopicEvent[] events) {
            onReceivingPumpMInitCompleted(((Boolean) events[0].getData()).booleanValue());
        }
    }
	private class OnReceivingPosA_POSStartedInvoker extends EventHandlerListener {

        private final String[] inputs = new String[] { ITopicConstant.TOPIC_PosA_PumpMPosA_IsPosConnected };

        @Override
		public String[] getTopics() {
            return inputs;
        }

        @Override
		public void process(ITopicEvent[] events) {
            onReceivingPosAIsPosConnected(((Boolean) events[0].getData()).booleanValue());
        }
    }
	private class OnReceivingPosAInvoker extends EventHandlerListener {

        private final String[] inputs = new String[] { ITopicConstant.TOPIC_PosA_PumpMPosA_Data };

        @Override
		public String[] getTopics() {
            return inputs;
        }

        @Override
		public void process(ITopicEvent[] events) {
            onReceivingPosAData(events[0].getData());
        }
    }
	private class OnSendingPosAInvoker extends EventHandlerListener {

        private final String[] inputs = new String[] { ITopicConstant.TOPIC_PumpM_PumpMPosA_Data };

        @Override
		public String[] getTopics() {
            return inputs;
        }

        @Override
		public void process(ITopicEvent[] events) {
            onReceivingPumpMData(events[0].getData());
        }
    }
	private IEventProducer posacontrollerOutput_salem = null;

    private IEventProducer posacontrollerOutput_statem = null;
    private IEventProducer producer_PosA_Data = null;
    private IEventProducer producer_PumpMCatM_Data = null;
    private IEventProducer producer_PumpMPumpA_Data = null;
    private IEventProducer producer_PumpMPumpA_InitReq = null;
    
    protected IEventProducer getPosacontrollerOutput_salemProducer() {
        if (posacontrollerOutput_salem == null) {
            posacontrollerOutput_salem = getProducerSite("posacontroller.output_salem").getProducer();
        }
        return posacontrollerOutput_salem;
    }


    protected IEventProducer getPosacontrollerOutput_statemProducer() {
        if (posacontrollerOutput_statem == null) {
            posacontrollerOutput_statem = getProducerSite("posacontroller.output_statem").getProducer();
        }
        return posacontrollerOutput_statem;
    }

    protected IEventProducer getProducer_PosA_Data() {
        if (producer_PosA_Data == null) {
            producer_PosA_Data = getProducerSite(ITopicConstant.TOPIC_All_PosA_Data).getProducer();
        }
        return producer_PosA_Data;
    }

    protected IEventProducer getProducer_PumpMCatM_Data() {
        if (producer_PumpMCatM_Data == null) {
            producer_PumpMCatM_Data = getProducerSite(ITopicConstant.TOPIC_PumpM_PumpMCatM_Data).getProducer();
        }
        return producer_PumpMCatM_Data;
    }

    protected IEventProducer getProducer_PumpMPumpA_Data() {
        if (producer_PumpMPumpA_Data == null) {
            producer_PumpMPumpA_Data = getProducerSite(ITopicConstant.TOPIC_PumpM_PumpMPumpA_Data).getProducer();
        }
        return producer_PumpMPumpA_Data;
    }

    protected IEventProducer getProducer_PumpMPumpA_InitReq() {
        if (producer_PumpMPumpA_InitReq == null) {
            producer_PumpMPumpA_InitReq = getProducerSite(ITopicConstant.TOPIC_PumpM_PumpMPumpA_InitReq).getProducer();
        }
        return producer_PumpMPumpA_InitReq;
    }

    abstract protected void onReceivingPosAData(Object receiving_posa);

	abstract protected void onReceivingPosAInitReq(String input_posa_start);

	abstract protected void onReceivingPosAIsPosConnected(boolean input_posa_isPosStarted);

	abstract protected void onReceivingPumpMData(Object sending_posa);

	abstract protected void onReceivingPumpMInitCompleted(boolean input_pumpa_completed);

	@Override
	public void registerListener() {
        addEventProcessor(new OnReceivingPosAInvoker());
        addEventProcessor(new OnInputPosaStartInvoker());
        addEventProcessor(new OnReceivingPosA_POSStartedInvoker());
        addEventProcessor(new OnInputPumpACompletedInvoker());
        addEventProcessor(new OnSendingPosAInvoker());	
	}
 
}
