package com.gsc.kixxhub.device.pumpa.processor;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.FAIL_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.common.utility.timer.KHTimer;
import com.gsc.kixxhub.common.utility.timer.KHTimerTask;
import com.gsc.kixxhub.device.pumpa.AdaptorServiceImp;
import com.gsc.kixxhub.device.pumpa.controller.DeviceSelector;
import com.gsc.kixxhub.device.pumpa.controller.ProcessSelector;
import com.gsc.kixxhub.device.pumpa.define.Processor;

/**
 * @author yd
 *
 * 302 Process. 토털게이지 자료 조회.
 * 
 * 1. Module -> P8 -> Adaptor
 * 2. Module <- S5 <- Adaptor  
 *
 */
public class P302_Process extends KHTimerTask implements Processor {
	private String failCommand 	= null;
	private String messageID 	= null;
	private String nextCommand 	= IPumpConstant.COMMANDID_P8;
	private String nozzleNo 	= null;
	private DeviceSelector selector = null;
	

	
	public P302_Process() throws Exception {
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
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void execute() throws Exception {
		ProcessSelector.removeProcess(nozzleNo);
		
		FAIL_WorkingMessage failWorkingMessage = new FAIL_WorkingMessage();
		failWorkingMessage.setMessageID(messageID);
		failWorkingMessage.setNozzleNo(nozzleNo);
		failWorkingMessage.setFailCommand(failCommand);
		
		AdaptorServiceImp.sendDeviceMsg(failWorkingMessage);			
		
	}	// end run
	
	
	
	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.pump.define.Processor#getProcessID()
	 */
	public String getProcessID() throws Exception {
		return "302 Process";
		
	}	// end getProcessID

	
	
	/**
	 * AdaptorServiceImp로 WorkingMessage를 전송
	 * 
	 * @param workMsg : WorkingMessage 
	 */
	private void sendDeviceMsg(WorkingMessage workMsg) throws Exception {
		workMsg.setMessageID(messageID);
 		AdaptorServiceImp.sendDeviceMsg(workMsg);
		
	}	// end sendDeviceMsg



	/**
	 * DeviceSelector로 WorkingMessage를 전송
	 * 
	 * @param workMsg : WorkingMessage
	 */
	private void sendModuleMsg(WorkingMessage workMsg) throws Exception {
		messageID 	= workMsg.getMessageID();
		failCommand = workMsg.getCommand();
		this.setName("Pump_302_Process");
		KHTimer.getInstance().setSchedule(this, 
				IPumpConstant.PUMP_PROCESS_WAITTING_TIME_FROM_MODULE) ;
		
		ProcessSelector.putProcess(nozzleNo, this);
		selector.selectDevice(workMsg);
		
	}	// end sendModuleMsg



	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.pump.define.Processor#startProcess(com.gsc.kixxhub.data.pump.WorkingMessage)
	 */
	public void startProcess(WorkingMessage workMsg) throws Exception {
		String command = workMsg.getCommand();
		nozzleNo = workMsg.getNozzleNo();
		
		if (command.equals(IPumpConstant.COMMANDID_P8)) {
			nextCommand	= IPumpConstant.COMMANDID_S5;
			
			this.sendModuleMsg(workMsg);
		
		} else if (command.equals(IPumpConstant.COMMANDID_S5)) {
			this.cancel();
			ProcessSelector.removeProcess(nozzleNo);
			this.sendDeviceMsg(workMsg);
			
		} else {
			LogUtility.getPumpALogger().debug("### Incorrect workingMessage in Process 302 ###");
			
		}	// end if
		
	}	// end startProcess

}