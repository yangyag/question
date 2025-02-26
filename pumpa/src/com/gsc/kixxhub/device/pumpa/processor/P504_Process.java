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
 * ������Ʈ�� : PI2
 * �ű� : 504 Process �ǸſϷ� ó��(������ ��� �� ����)
 * �������� : 2015.11.26
 * @author ������
 * 
 * Module -> GR -> Adaptor
 *
 */

public class P504_Process implements Processor {
	
	private DeviceSelector selector = null;
	private String nextCommand 	= IPumpConstant.COMMANDID_GR;
	
	public P504_Process() throws Exception {
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
		// TODO Auto-generated method stub
		return "504 Process";
	}

	/**
	 * DeviceSelector�� WorkingMessage�� ����
	 * 
	 * @param workMsg : WorkingMessage
	 */
	private void sendModuleMsg(WorkingMessage workMsg) throws Exception {
		String command = workMsg.getCommand();
		
		if (command.equals(IPumpConstant.COMMANDID_GR)) {
			
			selector.selectDevice(workMsg);
			
		} 
		
	}	// end sendModuleMsg

	public void startProcess(WorkingMessage workingMessage) throws Exception {

		String command = workingMessage.getCommand();
		
		if (command.equals(IPumpConstant.COMMANDID_GR)) {
			
			this.sendModuleMsg(workingMessage);
		
		} else {
			LogUtility.getPumpALogger().debug("### Incorrect workingMessage in Process 504 ###");
			
		}	// end if
	}

}
