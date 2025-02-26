package com.gsc.kixxhub.device.pumpa.processor;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.AdaptorServiceImp;
import com.gsc.kixxhub.device.pumpa.define.Processor;

/**
 * �ű� : SK ���� ó��  
 * ���� : ������ODT ���� ��û ó��.
 * 1. Module <- SK <- Adaptor
 * 2. Module -> SL -> Adaptor
 * �������� : 2016.03.28
 * ������ : ������
 */	

public class P226_Process implements Processor {
	private String messageID 	= null;
	private String nextCommand 	= IPumpConstant.COMMANDID_SK;
	
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
		return "226 Process";
		
	}	// end getProcessID



	/**
	 * AdaptorServiceImp�� WorkingMessage�� ����
	 * 
	 * @param workMsg : WorkingMessage 
	 */
	private void sendDeviceMsg(WorkingMessage workMsg) throws Exception {
		messageID = GlobalUtility.getUniqueMessageID();
		workMsg.setMessageID(messageID);
		
		AdaptorServiceImp.sendDeviceMsg(workMsg);
		
	}	// end sendDeviceMsg



	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.pump.define.Processor#startProcess(com.gsc.kixxhub.data.pump.WorkingMessage)
	 */
	public void startProcess(WorkingMessage workMsg) throws Exception {
		String command = workMsg.getCommand();
		
		if (command.equals(IPumpConstant.COMMANDID_SK)) {
			
			this.sendDeviceMsg(workMsg);
		
		} else {
			LogUtility.getLogger().debug("### Incorrect workingMessage in Process 226 ###");
			
		}	// end if
	
	}	// end startProcess

}