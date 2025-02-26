package com.gsc.kixxhub.device.pumpa.processor;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.controller.DeviceSelector;
import com.gsc.kixxhub.device.pumpa.controller.ProcessSelector;
import com.gsc.kixxhub.device.pumpa.define.Processor;

/**
 * ½Å±Ô : 513 Process ODT install/update
 * 2019.08
 * @author SoonKwan
 *
 * Module -> PU -> Adaptor
 */
public class P513_Process implements Processor {

	private String nextCommand 	= IPumpConstant.COMMANDID_PU;
	private DeviceSelector selector = null;
	
	public P513_Process() throws Exception {
		selector = ProcessSelector.getSelector();
	}
	
	private void sendModuleMsg(WorkingMessage workMsg) throws Exception {
		
		String command = workMsg.getCommand();
		
		if (command.equals(IPumpConstant.COMMANDID_PU)) {
			
			selector.selectDevice(workMsg);
		} 
		
	}

	public boolean canContinue(WorkingMessage workMsg) throws Exception {
		boolean returnData = false;
		String command = workMsg.getCommand();
		
		if (nextCommand.equals(command)) {
			returnData = true;
			
		}
		
		return returnData;
	}

	public String getProcessID() throws Exception {
		// TODO Auto-generated method stub
		return "513 Process";
	}

	public void startProcess(WorkingMessage workMsg) throws Exception {
		String command = workMsg.getCommand();
		
		if (command.equals(IPumpConstant.COMMANDID_PU)) {
			
			this.sendModuleMsg(workMsg);
		
		} else {
			LogUtility.getLogger().debug("### Incorrect workingMessage in Process 513 ###");
			
		}
	}

}
