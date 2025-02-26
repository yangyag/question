package com.gsc.kixxhub.device.pumpa.processor;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.AdaptorServiceImp;
import com.gsc.kixxhub.device.pumpa.define.Processor;

/**
* 프로젝트명 : PI2
* 신규 : 501 Process ODT 모드정보
* 변경일자 : 2016.03.15
* @author 정혜정
* 
* Module <- PM <- Adaptor
*
*/

public class P501_Process implements Processor {
	
	private String messageID = null;
	private String nextCommand = IPumpConstant.COMMANDID_PM;
	
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
		return "501 Process";
	}

	/**
	 * AdaptorServiceImp로 WorkingMessage를 전송
	 * 
	 * @param workingMessage : WorkingMessage 
	 */
	private void sendDeviceMsg(WorkingMessage workingMessage) throws Exception {
		messageID = GlobalUtility.getUniqueMessageID();
		workingMessage.setMessageID(messageID);
		AdaptorServiceImp.sendDeviceMsg(workingMessage);
		
	}	// end sendDeviceMsg

	public void startProcess(WorkingMessage workingMessage) throws Exception {
		String command = workingMessage.getCommand();	
		
		if (command.equals(IPumpConstant.COMMANDID_PM)) {
			this.sendDeviceMsg(workingMessage);
		
		} else {
			LogUtility.getPumpALogger().debug("### Incorrect workingMessage in Process 501 ###");
			
		}	// end if

	}

}
