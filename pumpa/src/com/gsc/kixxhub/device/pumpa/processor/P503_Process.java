package com.gsc.kixxhub.device.pumpa.processor;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.AdaptorServiceImp;
import com.gsc.kixxhub.device.pumpa.controller.DeviceSelector;
import com.gsc.kixxhub.device.pumpa.controller.ProcessSelector;
import com.gsc.kixxhub.device.pumpa.define.Processor;

/* ������Ʈ�� :PI2
 * �ű� : GA ���� ó��  
 * 503 Process. ǥ�ؼ��� ī�� ���ο�û ���� ó��.
 * Module <-GA <- Adaptor
 * �������� : 2016.03.15
 * ������ : ������*/	

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
	 * AdaptorServiceImp�� WorkingMessage�� ����
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
