package com.gsc.kixxhub.device.pumpa.processor;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.controller.DeviceSelector;
import com.gsc.kixxhub.device.pumpa.controller.ProcessSelector;
import com.gsc.kixxhub.device.pumpa.define.Processor;


/**
 * 신규 : SL 전문 처리  
 * 내용 : 충전기ODT 승인 응답 처리.
 * 1. Module <- SK <- Adaptor
 * 2. Module -> SL -> Adaptor
 * 생성일자 : 2016.03.28
 * 생성자 : 양일준
 */	

public class P227_Process implements Processor {
	private String nextCommand 	= IPumpConstant.COMMANDID_SL;
	private DeviceSelector selector = null;
	
	public P227_Process() throws Exception {
		selector = ProcessSelector.getSelector();
	}

	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.pump.define.Processor#canContinue(com.gsc.kixxhub.data.pump.WorkingMessage)
	 */
	public boolean canContinue(WorkingMessage message) throws Exception {
		boolean returnData = false;
		String command = message.getCommand();
		LogUtility.getLogger().info("******************227 Process********************command => "+command + " :: nextCommand =>" + nextCommand);
		
		if (nextCommand.equals(command)) {
			returnData = true;
			
		}	// end if
		
		return returnData;
		
	}	// end canContinue

	
	
	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.pump.define.Processor#getProcessID()
	 */
	public String getProcessID() throws Exception {
		return "227 Process";
		
	}	// end getProcessID



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
		
		if (command.equals(IPumpConstant.COMMANDID_SL)) {

			this.sendModuleMsg(workMsg);
			
		} else {
			LogUtility.getLogger().debug("### Incorrect workingMessage in Process 227 ###");
			
		}	// end if
	
	}	// end startProcess

}


