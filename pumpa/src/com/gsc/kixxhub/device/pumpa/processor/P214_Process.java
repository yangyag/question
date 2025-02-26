package com.gsc.kixxhub.device.pumpa.processor;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.FAIL_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.common.utility.timer.KHTimer;
import com.gsc.kixxhub.common.utility.timer.KHTimerTask;
import com.gsc.kixxhub.device.pumpa.AdaptorServiceImp;
import com.gsc.kixxhub.device.pumpa.controller.DeviceSelector;
import com.gsc.kixxhub.device.pumpa.controller.ProcessSelector;
import com.gsc.kixxhub.device.pumpa.define.Processor;

/**
 * @author yd
 * 
 * 214 Process. 주유시작 자료 처리.
 * 
 * 1. Module -> SI -> Adaptor or Module <- SJ <- Adaptor 
 * 2. Module <- SJ <- Adaptor (SI로 시작한 경우)
 *
 */
public class P214_Process extends KHTimerTask implements Processor {
	private String failCommand 	= null;
	private String messageID 	= null;
	private String nextCommand 	= null;
	private String nozzleNo 	= null;
	private DeviceSelector selector = null;
	

	
	public P214_Process() throws Exception {
		selector = ProcessSelector.getSelector();
	}



	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.pump.define.Processor#canContinue(com.gsc.kixxhub.data.pump.WorkingMessage)
	 */
	public boolean canContinue(WorkingMessage message) throws Exception {
		boolean returnData = false;
		String command = message.getCommand();
		
		if (nextCommand != null) {
			if (nextCommand.equals(command)) {
				returnData = true;
				
			}	// end inner if
			
		} else {
			// 214 Process를 시작하는 전문 구분 
			if (command.equals(IPumpConstant.COMMANDID_SI) 
					|| command.equals(IPumpConstant.COMMANDID_SJ)) {
				returnData = true;
				
			}	// end inner if
			
		}	// end if
		
		return returnData;
		
	}	// end canContinue

	
	
	/* (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void execute() throws Exception {
		ProcessSelector.removeProcess(nozzleNo);

		FAIL_WorkingMessage failWorkingMessage = new FAIL_WorkingMessage();
		failWorkingMessage.setMessageID(messageID);
		failWorkingMessage.setNozzleNo(nozzleNo);
		failWorkingMessage.setFailCommand(failCommand);

		AdaptorServiceImp.sendDeviceMsg(failWorkingMessage);

	} // end run

	
	
	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.pump.define.Processor#getProcessID()
	 */
	public String getProcessID() throws Exception {
		return "214 Process";
		
	}	// end getProcessID

	
	
	/**
	 * AdaptorServiceImp로 WorkingMessage를 전송
	 * 
	 * @param workMsg : WorkingMessage 
	 */
	private void sendDeviceMsg(WorkingMessage workMsg) throws Exception {
		if (messageID == null) {
			messageID = GlobalUtility.getUniqueMessageID();
			
		}	// end if
		
		workMsg.setMessageID(messageID);
		AdaptorServiceImp.sendDeviceMsg(workMsg);

	} // end sendDeviceMsg

	
	
	/**
	 * DeviceSelector로 WorkingMessage를 전송
	 * 
	 * @param workMsg : WorkingMessage
	 */
	private void sendModuleMsg(WorkingMessage workMsg) throws Exception {
		failCommand = workMsg.getCommand();
		this.setName("Pump_214_Process");
		KHTimer.getInstance().setSchedule(this, 
				IPumpConstant.PUMP_PROCESS_WAITTING_TIME_FROM_MODULE);

		ProcessSelector.putProcess(nozzleNo, this);
		selector.selectDevice(workMsg);
		
	} // end sendModuleMsg

	
	
	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.pump.define.Processor#startProcess(com.gsc.kixxhub.data.pump.WorkingMessage)
	 */
	public void startProcess(WorkingMessage workMsg) throws Exception {
		String command = workMsg.getCommand();
		nozzleNo = workMsg.getNozzleNo();

		if (command.equals(IPumpConstant.COMMANDID_SI)) {
			messageID 	= workMsg.getMessageID();
			nextCommand	= IPumpConstant.COMMANDID_SJ;
			
			this.sendModuleMsg(workMsg);

		} else if (command.equals(IPumpConstant.COMMANDID_SJ)) {
			// SI의 응답으로 SJ가 전송 된 경우
			if (IPumpConstant.COMMANDID_SJ.equals(nextCommand)) {
				this.cancel();
				ProcessSelector.removeProcess(nozzleNo);
				
			}	// end inner if
			
			this.sendDeviceMsg(workMsg);

		} else {
			LogUtility.getPumpALogger().debug("### Incorrect workingMessage in Process 214 ###");

		} // end if

	} // end startProcess
	
}