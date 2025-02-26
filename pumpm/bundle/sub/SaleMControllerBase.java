package com.gsc.kixxhub.module.pumpm.bundle.sub;

 
import com.gsc.kixxhub.common.data.ITopicConstant;
import com.gsc.kixxhub.eventhandler.action.BaseBundleActivator;
import com.gsc.kixxhub.eventhandler.action.EventHandlerListener;
import com.gsc.kixxhub.eventhandler.service.IEventProducer;
import com.gsc.kixxhub.eventhandler.service.ITopicEvent;

abstract public class SaleMControllerBase extends BaseBundleActivator {

    private class OnSendingSaleMInvoker extends EventHandlerListener {

        private final String[] inputs = new String[] { ITopicConstant.TOPIC_PumpM_PumpMSaleM_Data };

        @Override
		public String[] getTopics() {
            return inputs;
        }

        @Override
		public void process(ITopicEvent[] events) {
            onReceivingPumpMData(events[0].getData());
        }
    }
    private IEventProducer producer_SaleM_TrData = null;
    private IEventProducer salemcontrollerOutput_catm = null;
    private IEventProducer salemcontrollerOutput_posa = null;
    private IEventProducer salemcontrollerOutput_pumpa = null;

    private IEventProducer salemcontrollerOutput_statem = null;

    protected IEventProducer getProducer_SaleM_TrData() {
        if (producer_SaleM_TrData == null) {
            producer_SaleM_TrData = getProducerSite(ITopicConstant.TOPIC_ALL_SaleM_TrData).getProducer();
        }
        return producer_SaleM_TrData;
    }

	protected IEventProducer getSalemcontrollerOutput_catmProducer() {
        if (salemcontrollerOutput_catm == null) {
            salemcontrollerOutput_catm = getProducerSite("salemcontroller.output_catm").getProducer();
        }
        return salemcontrollerOutput_catm;
    }


    protected IEventProducer getSalemcontrollerOutput_posaProducer() {
        if (salemcontrollerOutput_posa == null) {
            salemcontrollerOutput_posa = getProducerSite("salemcontroller.output_posa").getProducer();
        }
        return salemcontrollerOutput_posa;
    }

    protected IEventProducer getSalemcontrollerOutput_pumpaProducer() {
        if (salemcontrollerOutput_pumpa == null) {
            salemcontrollerOutput_pumpa = getProducerSite("salemcontroller.output_pumpa").getProducer();
        }
        return salemcontrollerOutput_pumpa;
    }

    protected IEventProducer getSalemcontrollerOutput_statemProducer() {
        if (salemcontrollerOutput_statem == null) {
            salemcontrollerOutput_statem = getProducerSite("salemcontroller.output_statem").getProducer();
        }
        return salemcontrollerOutput_statem;
    }

    abstract protected void onReceivingPumpMData(Object sending_salem);

    @Override
	public void registerListener() {
        addEventProcessor(new OnSendingSaleMInvoker());	
	}
 
}
