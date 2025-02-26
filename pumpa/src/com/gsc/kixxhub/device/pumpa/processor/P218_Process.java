package com.gsc.kixxhub.device.pumpa.processor;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.NAK_WorkingMessage;
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
 * 218 Process. 충전기 보관량 조회.
 * 
 * 1. Module <- SG  <- Adaptor  
 * 2. Module -> PQ -> Adaptor
 *
 */
public class P218_Process implements Processor {
	private String messageID 	= null;
	private String nextCommand 	= IPumpConstant.COMMANDID_SG;
	private String nozzleNo 	= null;
	private DeviceSelector selector = null;
	private TimerTask timer 	= null;
	
	
	public P218_Process() throws Exception {
		selector = ProcessSelector.getSelector();
	}

	
	public boolean canContinue(WorkingMessage workingMessage) throws Exception {
		boolean returnData = false;
		String command = workingMessage.getCommand();
		
		if (nextCommand.equals(command)) {
			returnData = true;
			
		}	// end if
		
		return returnData;
	}
	
	
	public String getProcessID() throws Exception {
		return "218 Process";
	}
	
	
	private void sendDeviceMsg(WorkingMessage workMsg) throws Exception {
		messageID = GlobalUtility.getUniqueMessageID();
		workMsg.setMessageID(messageID);
		
		NAK_WorkingMessage nakMessage = new NAK_WorkingMessage();
		nakMessage.setNozzleNo(nozzleNo);
		
		timer = new TimerTask(nozzleNo, nakMessage);
		timer.setName("Pump_218_Process");
		KHTimer.getInstance().setSchedule(timer, 
				IPumpConstant.PUMP_PROCESS_WAITTING_TIME_FROM_ADAPTOR) ;
		
		ProcessSelector.putProcess(nozzleNo, this);
 		AdaptorServiceImp.sendDeviceMsg(workMsg);
	}
	
	private void sendModuleMsg(WorkingMessage workMsg) throws Exception {
		selector.selectDevice(workMsg);
		
	}
	
	
	public void startProcess(WorkingMessage workingMessage) throws Exception {
		String command = workingMessage.getCommand();
		nozzleNo = workingMessage.getNozzleNo();
		
		if (command.equals(IPumpConstant.COMMANDID_SG)) {
			nextCommand	= IPumpConstant.COMMANDID_PQ;
			this.sendDeviceMsg(workingMessage);
			
		} else if (command.equals(IPumpConstant.COMMANDID_PQ)) {
			timer.cancel();
			ProcessSelector.removeProcess(nozzleNo);
			this.sendModuleMsg(workingMessage);
			
		} else {
			LogUtility.getPumpALogger().debug("### Incorrect workingMessage in Process 218 ###");
			
		}
		
	}
	
}
