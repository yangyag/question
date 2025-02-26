package com.gsc.kixxhub.module.pumpm.bundle.sub;

 
import com.gsc.kixxhub.common.data.ITopicConstant;
import com.gsc.kixxhub.eventhandler.action.BaseBundleActivator;
import com.gsc.kixxhub.eventhandler.action.EventHandlerListener;
import com.gsc.kixxhub.eventhandler.service.IEventProducer;
import com.gsc.kixxhub.eventhandler.service.ITopicEvent;

abstract public class PumpAControllerBase extends BaseBundleActivator {

    private class OnInputPumpAStartInvoker extends EventHandlerListener {

        private final String[] inputs = new String[] { ITopicConstant.TOPIC_PumpM_PumpMPumpA_InitReq };

        @Override
		public String[] getTopics() {
            return inputs;
        }

        @Override
		public void process(ITopicEvent[] events) {
            onReceivingPumpMInitReq((String) events[0].getData());
        }
    }
    private class OnReceivingDownloadFlagInvoker extends EventHandlerListener {

        private final String[] inputs = new String[] { ITopicConstant.TOPIC_ScM_PumpMPumpA_DownloadFlagInfo };

        @Override
		public String[] getTopics() {
            return inputs;
        }

        @Override
		public void process(ITopicEvent[] events) {
            onReceivingScMDownloadFlagInfo((String) events[0].getData());
        }
    }
    private class OnReceivingPumpAInvoker extends EventHandlerListener {

        private final String[] inputs = new String[] { ITopicConstant.TOPIC_PumpA_PumpMPumpA_Data };

        @Override
		public String[] getTopics() {
            return inputs;
        }

        @Override
		public void process(ITopicEvent[] events) {
            onReceivingPumpAData(events[0].getData());
        }
    }
    private class OnSendingPumpAInvoker extends EventHandlerListener {

        private final String[] inputs = new String[] { ITopicConstant.TOPIC_PumpM_PumpMPumpA_Data };

        @Override
		public String[] getTopics() {
            return inputs;
        }

        @Override
		public void process(ITopicEvent[] events) {
            onReceivingPumpMData(events[0].getData());
        }
    }
    
    private class OnReceivingBeaconMInvoker extends EventHandlerListener {

        private final String[] inputs = new String[] { ITopicConstant.TOPIC_BeaconM_BeaconM_Data };

        @Override
		public String[] getTopics() {
            return inputs;
        }

        @Override
		public void process(ITopicEvent[] events) {
        	onReceivingBeaconMData(events[0].getData());
        }
    }
    
    private IEventProducer producer_PosA_InitCompleted = null;
    private IEventProducer producer_PumpA_Data = null;
    private IEventProducer producer_PumpA_Init = null;
    private IEventProducer producer_PumpM_Data = null;
    private IEventProducer producer_PumpMAmsM_Data = null;
    
    private IEventProducer producer_PumpMCatM_Data = null;
    private IEventProducer producer_PumpMPosA_Data = null;
    private IEventProducer producer_PumpMPosA_InitCompleted = null;
    private IEventProducer producer_PumpMSaleM_Data = null;
    private IEventProducer producer_BeaconM_Data = null;

    protected IEventProducer getProducer_PosA_InitCompleted() {
        if (producer_PosA_InitCompleted == null) {
        	producer_PosA_InitCompleted = getProducerSite(ITopicConstant.TOPIC_PumpM_PosA_InitCompleted).getProducer();
        }
        return producer_PosA_InitCompleted;
    }

    protected IEventProducer getProducer_PumpA_Data() {
        if (producer_PumpA_Data == null) {
            producer_PumpA_Data = getProducerSite(ITopicConstant.TOPIC_PumpM_PumpA_Data).getProducer();
        }
        return producer_PumpA_Data;
    }

    protected IEventProducer getProducer_PumpA_Init() {
        if (producer_PumpA_Init == null) {
            producer_PumpA_Init = getProducerSite(ITopicConstant.TOPIC_PumpM_PumpA_Init).getProducer();
        }
        return producer_PumpA_Init;
    }

	/**
     * PI2, 2016-03-25, CWI, ODT에서 캠페인 정보를 수신하기 위해 새로운 경로 추가
     * pumpAController -> catMController 
     */
    protected IEventProducer getProducer_PumpM_Data() {
        if (producer_PumpM_Data == null) {
        	producer_PumpM_Data = getProducerSite(ITopicConstant.TOPIC_PumpAController_CatMController_Data).getProducer();
        }
        return producer_PumpM_Data;
    }
    
    protected IEventProducer getProducer_PumpMAmsM_Data() {
        if (producer_PumpMAmsM_Data == null) {
            producer_PumpMAmsM_Data = getProducerSite(ITopicConstant.TOPIC_PumpM_PumpMAmsM_Data).getProducer();
        }
        return producer_PumpMAmsM_Data;
    }
    
    protected IEventProducer getProducer_PumpMCatM_Data() {
        if (producer_PumpMCatM_Data == null) {
            producer_PumpMCatM_Data = getProducerSite(ITopicConstant.TOPIC_PumpM_PumpMCatM_Data).getProducer();
        }
        return producer_PumpMCatM_Data;
    }

    protected IEventProducer getProducer_PumpMPosA_Data() {
        if (producer_PumpMPosA_Data == null) {
            producer_PumpMPosA_Data = getProducerSite(ITopicConstant.TOPIC_PumpM_PumpMPosA_Data).getProducer();
        }
        return producer_PumpMPosA_Data;
    }

    protected IEventProducer getProducer_PumpMPosA_InitCompleted() {
        if (producer_PumpMPosA_InitCompleted == null) {
            producer_PumpMPosA_InitCompleted = getProducerSite(ITopicConstant.TOPIC_PumpM_PumpMPosA_InitCompleted).getProducer();
        }
        return producer_PumpMPosA_InitCompleted;
    }

    protected IEventProducer getProducer_PumpMSaleM_Data() {
        if (producer_PumpMSaleM_Data == null) {
            producer_PumpMSaleM_Data = getProducerSite(ITopicConstant.TOPIC_PumpM_PumpMSaleM_Data).getProducer();
        }
        return producer_PumpMSaleM_Data;
    }
    
    protected IEventProducer getProducer_PumpMBeaconM_Data() {
        if (producer_BeaconM_Data == null) {
        	producer_BeaconM_Data = getProducerSite(ITopicConstant.TOPIC_PumpM_BeaconM_Data).getProducer();
        }
        return producer_BeaconM_Data;
    }

    abstract protected void onReceivingPumpAData(Object receiving_pumpa);

    abstract protected void onReceivingPumpMData(Object sending_pumpa);

    abstract protected void onReceivingPumpMInitReq(String input_pumpa_start);
    
    abstract protected void onReceivingScMDownloadFlagInfo(String input_downloadFlag);
    
    abstract protected void onReceivingBeaconMData(Object receiving_beaconm);
    
    @Override
	public void registerListener() {
        addEventProcessor(new OnReceivingPumpAInvoker());
        addEventProcessor(new OnSendingPumpAInvoker());
        addEventProcessor(new OnReceivingDownloadFlagInvoker());
        addEventProcessor(new OnInputPumpAStartInvoker());
        addEventProcessor(new OnReceivingBeaconMInvoker());
	}
}
