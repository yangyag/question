package com.gsc.kixxhub.device.pumpa.processor;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.AdaptorServiceImp;
import com.gsc.kixxhub.device.pumpa.controller.DeviceSelector;
import com.gsc.kixxhub.device.pumpa.controller.ProcessSelector;
import com.gsc.kixxhub.device.pumpa.define.Processor;

/**
 * 프로젝트명 : PI2
 * 신규 : 506 Process 판매완료 처리(영수증 출력 후 전송)
 * 변경일자 : 2015.11.26
 * @author 정혜정
 * 
 * Module <- GT <- Adaptor
 *
 */

public class P506_Process implements Processor {

	private String nextCommand 	= IPumpConstant.COMMANDID_GT;
	private String messageID = null;

	public boolean canContinue(WorkingMessage workingMessage) throws Exception {
		boolean returnData = false;
		String command = workingMessage.getCommand();
		
		if (nextCommand.equals(command)) {
			returnData = true;
			
		}	// end if
		
		return returnData;
	}
	
	public String getProcessID() throws Exception {
		// TODO Auto-generated method stub
		return "512 Process";
	}

	/**
	 * AdaptorServiceImp로 WorkingMessage를 전송
	 * 
	 * @param workMsg : WorkingMessage 
	 */
	private void sendDeviceMsg(WorkingMessage workMsg) throws Exception {
		messageID = GlobalUtility.getUniqueMessageID();
		workMsg.setMessageID(messageID);
		AdaptorServiceImp.sendDeviceMsg(workMsg);
		
	}	// end sendDeviceMsg

	public void startProcess(WorkingMessage workingMessage) throws Exception {
		
		String command = workingMessage.getCommand();
		
		if (command.equals(IPumpConstant.COMMANDID_GT)) {
			
			this.sendDeviceMsg(workingMessage);
		
		} else {
			LogUtility.getPumpALogger().debug("### Incorrect workingMessage in Process 506 ###");
			
		}	// end if

	}

}
