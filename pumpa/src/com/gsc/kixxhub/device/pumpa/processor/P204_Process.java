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
 * 204 Process. Preset 자료 요청.
 * QF 또는 HF WorkingMessage로 시작이 가능한 Process.
 * 
 * 1. Module -> QF -> Adaptor or Module <- HF <- Adaptor
 * 2. Module <- HF <- Adaptor(QF로 시작한 경우)
 *
 */
public class P204_Process extends KHTimerTask implements Processor {
	private String failCommand = null;
	private String messageID 	= null;
	private String nextCommand = null;
	private String nozzleNo 	= null;
	private DeviceSelector selector =null;


	public P204_Process() throws Exception {
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
			// 204 Process를 시작하는 전문 구분 
			if (command.equals(IPumpConstant.COMMANDID_QF) 
					|| command.equals(IPumpConstant.COMMANDID_HF)) {
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
		
	}	// end run
	
	
	
	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.pump.define.Processor#getProcessID()
	 */
	public String getProcessID() throws Exception {
		return "204 Process";
		
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
		
	}	// end sendDeviceMsg



	/**
	 * DeviceSelector로 WorkingMessage를 전송
	 * 
	 * @param workMsg : WorkingMessage
	 */
	private void sendModuleMsg(WorkingMessage workMsg) throws Exception {
		failCommand = workMsg.getCommand();
		messageID   = workMsg.getMessageID();
		
		this.setName("Pump_204_Process");
		KHTimer.getInstance().setSchedule(this, 
				IPumpConstant.PUMP_PROCESS_WAITTING_TIME_FROM_MODULE) ;

		ProcessSelector.putProcess(nozzleNo, this);
		selector.selectDevice(workMsg);
		
	}	// end sendModuleMsg



	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.pump.define.Processor#startProcess(com.gsc.kixxhub.data.pump.WorkingMessage)
	 */
	public void startProcess(WorkingMessage workMsg) throws Exception {
		String command = workMsg.getCommand();
		nozzleNo = workMsg.getNozzleNo();
		
		if (command.equals(IPumpConstant.COMMANDID_QF)) {
			nextCommand = IPumpConstant.COMMANDID_HF;
			messageID = workMsg.getMessageID();

			this.sendModuleMsg(workMsg);
		
		} else if (command.equals(IPumpConstant.COMMANDID_HF)) {
			// QF의 응답으로 HF가 전송 된 경우
			if (IPumpConstant.COMMANDID_HF.equals(nextCommand)) {
				this.cancel();
				ProcessSelector.removeProcess(nozzleNo);
				
			}	// end inner if
			
			this.sendDeviceMsg(workMsg);
			
		} else {
			LogUtility.getPumpALogger().debug("### Incorrect workingMessage in Process 204 ###");
			
		}	// end if
		
	}	// end startProcess

}
