package com.gsc.kixxhub.device.pumpa.processor;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.controller.DeviceSelector;
import com.gsc.kixxhub.device.pumpa.controller.ProcessSelector;
import com.gsc.kixxhub.device.pumpa.define.Processor;

/**
 * 프로젝트명 : PI2
 * 신규 : 507 Process 가득주유 통제
 * 변경일자 : 2015.11.27
 * @author 정혜정
 * 
 * Module -> FC -> Adaptor
 *
 */

public class P507_Process implements Processor {

	private DeviceSelector selector = null;
	private String nextCommand 	= IPumpConstant.COMMANDID_FC;
	
	public P507_Process() throws Exception {
		selector = ProcessSelector.getSelector();
	}

	/**
	 * DeviceSelector로 WorkingMessage를 전송
	 * 
	 * @param workMsg : WorkingMessage
	 */
	private void sendModuleMsg(WorkingMessage workMsg) throws Exception {
		String command = workMsg.getCommand();
		
		if (command.equals(IPumpConstant.COMMANDID_FC)) {
			
			selector.selectDevice(workMsg);
			
		} 
		
	}	// end sendModuleMsg
	
	
	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.pump.define.Processor#startProcess(com.gsc.kixxhub.data.pump.WorkingMessage)
	 */
	public void startProcess(WorkingMessage workMsg) throws Exception {
		String command = workMsg.getCommand();
		
		if (command.equals(IPumpConstant.COMMANDID_FC)) {
			this.sendModuleMsg(workMsg);
		
		} else {
			LogUtility.getLogger().debug("### Incorrect workingMessage in Process 507 ###");
			
		}	// end if
		
	}	// end startProcess

	
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
		
		return "507 Process";
		
	}	// end getProcessID

}
