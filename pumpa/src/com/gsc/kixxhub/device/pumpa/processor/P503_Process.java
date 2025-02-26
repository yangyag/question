package com.gsc.kixxhub.device.pumpa.processor;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.AdaptorServiceImp;
import com.gsc.kixxhub.device.pumpa.controller.DeviceSelector;
import com.gsc.kixxhub.device.pumpa.controller.ProcessSelector;
import com.gsc.kixxhub.device.pumpa.define.Processor;

/* 프로젝트명 :PI2
 * 신규 : GA 전문 처리  
 * 503 Process. 표준셀프 카드 승인요청 응답 처리.
 * Module <-GA <- Adaptor
 * 변경이자 : 2016.03.15
 * 변경자 : 정혜정*/	

public class P503_Process implements Processor {

	private String nextCommand 	= IPumpConstant.COMMANDID_GA;
	private String messageID 	= null;

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
		return "503 Process";
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
		// TODO Auto-generated method stub
		String command = workingMessage.getCommand();
		
		if (command.equals(IPumpConstant.COMMANDID_GA)) {

			this.sendDeviceMsg(workingMessage);
			
		} else {
			LogUtility.getLogger().debug("### Incorrect workingMessage in Process 510 ###");
			
		}	// end if

	}

}
