package com.gsc.kixxhub.device.pumpa.processor;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.controller.DeviceSelector;
import com.gsc.kixxhub.device.pumpa.controller.ProcessSelector;
import com.gsc.kixxhub.device.pumpa.define.Processor;

/* 프로젝트명 :PI2
 * 신규 : GB 전문 처리  
 * 510 Process. 표준셀프 카드 승인요청 응답 처리.
 * Module -> GB -> Adaptor
 * 변경이자 : 2016.01.06
 * 변경자 : 정혜정*/	

public class P510_Process implements Processor {
	private String nextCommand 	= IPumpConstant.COMMANDID_GB;
	private DeviceSelector selector = null;
	
	public P510_Process() throws Exception {
		selector = ProcessSelector.getSelector();
	}

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
		
		if (command.equals(IPumpConstant.COMMANDID_GB)) {

			this.sendModuleMsg(workMsg);
			
		} else {
			LogUtility.getLogger().debug("### Incorrect workingMessage in Process 510 ###");
			
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
		return "510 Process";
		
	}	// end getProcessID

}
