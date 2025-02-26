package com.gsc.kixxhub.device.pumpa.processor;

import com.gsc.kixxhub.common.data.IPumpConstant;
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
 * 206 Process. 소모셀프 외상거래 승인처리.
 * 
 * 1. Module <- HB <- Adaptor
 * 2. Module -> HD -> Adaptor
 *
 */
public class P206_Process implements Processor {
	private String messageID 			= null;
	private String nextCommand 			= IPumpConstant.COMMANDID_HB;
	private String nozzleNo 			= null;
	private DeviceSelector selector 	= null;
	private TimerTask timer 			= null;
	

	
	public P206_Process() throws Exception {
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
		return "206 Process";
		
	}	// end getProcessID

	
	
	/**
	 * AdaptorServiceImp로 WorkingMessage를 전송
	 * 
	 * @param workMsg : WorkingMessage 
	 */
	private void sendDeviceMsg(WorkingMessage workMsg) throws Exception {
		messageID = GlobalUtility.getUniqueMessageID();
		workMsg.setMessageID(messageID);
		
		timer = new TimerTask(nozzleNo);
		timer.setName("Pump_206_Process");
		KHTimer.getInstance().setSchedule(timer, 
				IPumpConstant.PUMP_PROCESS_WAITTING_TIME_FROM_ADAPTOR) ;
		
		ProcessSelector.putProcess(nozzleNo, this);
		AdaptorServiceImp.sendDeviceMsg(workMsg);
		
	}	// end sendDeviceMsg



	/**
	 * DeviceSelector로 WorkingMessage를 전송
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
		
		if (command.equals(IPumpConstant.COMMANDID_HB)) {
			nextCommand = IPumpConstant.COMMANDID_HD;
			this.sendDeviceMsg(workMsg);
		
		} else if (command.equals(IPumpConstant.COMMANDID_HD)) {
			timer.cancel();
			ProcessSelector.removeProcess(nozzleNo);
			this.sendModuleMsg(workMsg);
			
		} else {
			LogUtility.getPumpALogger().debug("### Incorrect workingMessage in Process 206 ###");
			
		}	// end if
		
	}	// end startProcess
	
}
