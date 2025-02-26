package com.gsc.kixxhub.device.pumpa.processor;

import com.gsc.kixxhub.common.data.pump.FAIL_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.timer.KHTimerTask;
import com.gsc.kixxhub.device.pumpa.AdaptorServiceImp;
import com.gsc.kixxhub.device.pumpa.controller.ProcessSelector;

public class TimerTask extends KHTimerTask {
	private String   failCommand 	= null;
	private boolean isFromModule 	= false;
	private WorkingMessage message = null;
	private String   messageID 		= null;
	private String   nozzleNo 		= null;
	
	
	/**
	 * Process�� sendDeviceMsg �޼ҵ忡�� ����Ѵ�. 
	 * 
	 * @param nozzleNo
	 */
	public TimerTask(String nozzleNo) {
		isFromModule = true;
		this.nozzleNo = nozzleNo;
		
	}
	
	
	
	/**
	 * Fail_WorkingMessage�� �����ϴ� ������
	 * Process�� sendModuleMsg �޼ҵ忡�� ����Ѵ�.
	 * 
	 * @param nozzleNo		
	 * @param failCommand
	 * @param messageID
	 */
	public TimerTask(String nozzleNo, String failCommand, String messageID) {
		this.nozzleNo = nozzleNo;
		this.failCommand = failCommand;
		this.messageID = messageID;
		
	}
	

	
	/**
	 * Process�� sendDeviceMsg �޼ҵ忡�� ����Ѵ�.
	 * Adaptor�� ������ WorkingMessage�� �޴´�.
	 * 
	 * 
	 * @param nozzleNo
	 * @param message
	 */
	public TimerTask(String nozzleNo, WorkingMessage message) {
		isFromModule 	= true;
		this.nozzleNo 	= nozzleNo;
		this.message 	= message;
		
	}
	
	
	
	/* (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void execute() throws Exception {
		ProcessSelector.removeProcess(nozzleNo);
		
		if (!isFromModule) {
			FAIL_WorkingMessage failWorkingMessage = new FAIL_WorkingMessage();
			failWorkingMessage.setMessageID(messageID);
			failWorkingMessage.setNozzleNo(nozzleNo);
			failWorkingMessage.setFailCommand(failCommand);
			
			AdaptorServiceImp.sendDeviceMsg(failWorkingMessage);		
		
		} else {
			if (message != null) {
				ProcessSelector.getSelector().selectDevice(message);
				
			}	// end inner if
			
		}	// end if
		
	}	// end run

}
