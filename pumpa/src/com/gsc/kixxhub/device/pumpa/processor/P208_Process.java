package com.gsc.kixxhub.device.pumpa.processor;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.QM_WoringMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.common.utility.timer.KHTimer;
import com.gsc.kixxhub.device.pumpa.AdaptorServiceImp;
import com.gsc.kixxhub.device.pumpa.controller.DeviceSelector;
import com.gsc.kixxhub.device.pumpa.controller.ProcessSelector;
import com.gsc.kixxhub.device.pumpa.define.Processor;

/**
 * @author yd
 *
 * 208 Process. �پ��뼿�� ī�� ����ó��.
 * 
 * 1. Module <- HE <- Adaptor
 * 2. Module -> QM -> Adaptor
 *
 */
public class P208_Process implements Processor {
	private String messageID 	= null;
	private String nextCommand 	= IPumpConstant.COMMANDID_HE;
	private String nozzleNo 	= null;
	private DeviceSelector selector = null;
	private TimerTask timer 	= null;
	
    
	
	public P208_Process() throws Exception {
		selector = ProcessSelector.getSelector();
	}



	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.pump.define.Processor#canContinue(com.gsc.kixxhub.data.pump.WorkingMessage)
	 */
	public boolean canContinue(WorkingMessage message) throws Exception {
		boolean returnData = false;
		String command = message.getCommand();
		
		if (nextCommand.equals(command)) {
			returnData = true;
			
		}	// end if
		
		return returnData;
		
	}	// end canContinue

	
	
	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.pump.define.Processor#getProcessID()
	 */
	public String getProcessID() throws Exception {
		return "208 Process";
		
	}	// end getProcessID

	
	
	/**
	 * AdaptorServiceImp�� WorkingMessage�� ����
	 * 
	 * @param workMsg : WorkingMessage 
	 */
	private void sendDeviceMsg(WorkingMessage workMsg) throws Exception {
		messageID = GlobalUtility.getUniqueMessageID();
		workMsg.setMessageID(messageID);
		
		// ī����� ��� ���� �� ���� �� QM ���� 
		QM_WoringMessage qmMessage = new QM_WoringMessage();
		qmMessage.setNozzleNo(nozzleNo);
		qmMessage.setConnectNozzleNo(workMsg.getConnectNozzleNo());
		qmMessage.setMode("3");
		
		timer = new TimerTask(nozzleNo, qmMessage);
		timer.setName("Pump_208_Process");
		KHTimer.getInstance().setSchedule(timer, 
				IPumpConstant.PUMP_PROCESS_WAITTING_TIME_FROM_ADAPTOR) ;
		ProcessSelector.putProcess(nozzleNo, this);
		AdaptorServiceImp.sendDeviceMsg(workMsg);
		
	}	// end sendDeviceMsg



	/**
	 * DeviceSelector�� WorkingMessage�� ����
	 * 
	 * @param workMsg : WorkingMessage
	 */
	private void sendModuleMsg(WorkingMessage workMsg) throws Exception {
		selector.selectDevice(workMsg);
		
	}	// end sendModuleMsg



	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.pump.define.Processor#startProcess(com.gsc.kixxhub.data.pump.WorkingMessage)
	 */
	public void startProcess(WorkingMessage workMsg) throws Exception {
		String command = workMsg.getCommand();
		nozzleNo = workMsg.getNozzleNo();
		
		if (command.equals(IPumpConstant.COMMANDID_HE)) {
			nextCommand = IPumpConstant.COMMANDID_QM;
			this.sendDeviceMsg(workMsg);
		
		} else if (command.equals(IPumpConstant.COMMANDID_QM)) {
			timer.cancel();
			ProcessSelector.removeProcess(nozzleNo);
			this.sendModuleMsg(workMsg);
			
		} else {
			LogUtility.getPumpALogger().debug("### Incorrect workingMessage in Process 208 ###");
			
		}	// end if
	
	}	// end startProcess

}