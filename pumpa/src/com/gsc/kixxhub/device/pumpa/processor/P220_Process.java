package com.gsc.kixxhub.device.pumpa.processor;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.FAIL_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.QL_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.common.utility.timer.KHTimer;
import com.gsc.kixxhub.common.utility.timer.KHTimerTask;
import com.gsc.kixxhub.device.pumpa.AdaptorServiceImp;
import com.gsc.kixxhub.device.pumpa.common.Sleep;
import com.gsc.kixxhub.device.pumpa.controller.DeviceSelector;
import com.gsc.kixxhub.device.pumpa.controller.ProcessSelector;
import com.gsc.kixxhub.device.pumpa.define.Processor;

/**
 * @author yd
 * 
 * 220 Process. �پ��뼿�� �ǸſϷ� ó��.
 * 
 * 1. Module -> QL -> Adaptor
 * 2. Module <- TR  <- Adaptor (QL�� MODE ���� "1"�� �ƴ϶�� TR�� ����)  
 *
 */
public class P220_Process extends KHTimerTask implements Processor {
	private String failCommand 	= null;
	private String messageID 	= null;
	private String nextCommand 	= IPumpConstant.COMMANDID_QL;
	private String nozzleNo 	= null;
	private DeviceSelector selector = null;
	

	
	public P220_Process() throws Exception {
		selector = ProcessSelector.getSelector();
	}



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
		return "220 Process";
		
	}	// end getProcessID

	
	
	/**
	 * AdaptorServiceImp�� WorkingMessage�� ����
	 * 
	 * @param workMsg : WorkingMessage 
	 */
	private void sendDeviceMsg(WorkingMessage workMsg) throws Exception {
		workMsg.setMessageID(messageID);
 		AdaptorServiceImp.sendDeviceMsg(workMsg);

	} // end sendDeviceMsg

	
	
	/**
	 * DeviceSelector�� WorkingMessage�� ����
	 * 
	 * @param workMsg : WorkingMessage
	 */
	private void sendModuleMsg(WorkingMessage workMsg) throws Exception {
		// �پ��� ���� ����� Ư�� �� ������ �����̸� �����Ͽ� ������ ������.
		Sleep.sleep(1000);
		
		QL_WorkingMessage qlMessage = (QL_WorkingMessage) workMsg;
		
		if (qlMessage.getMode().equals("1")) {
			// QL�� ���� TR�� ����.
			messageID 	= workMsg.getMessageID();
			failCommand = workMsg.getCommand();
			this.setName("Pump_302_Process");
			KHTimer.getInstance().setSchedule(this, 
					IPumpConstant.PUMP_PROCESS_WAITTING_TIME_FROM_MODULE) ;
			
			ProcessSelector.putProcess(nozzleNo, this);
			
		} else {
			// QL�� ���� TR�� ������.

		}
		selector.selectDevice(workMsg);
		
	} // end sendModuleMsg

	
	
	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.pump.define.Processor#startProcess(com.gsc.kixxhub.data.pump.WorkingMessage)
	 */
	public void startProcess(WorkingMessage workMsg) throws Exception {
		String command = workMsg.getCommand();
		nozzleNo = workMsg.getNozzleNo();

		if (command.equals(IPumpConstant.COMMANDID_QL)) {
			nextCommand	= IPumpConstant.COMMANDID_TR;
			this.sendModuleMsg(workMsg);

		} else if (command.equals(IPumpConstant.COMMANDID_TR)) {
			this.cancel();
			ProcessSelector.removeProcess(nozzleNo);
			this.sendDeviceMsg(workMsg);

		} else {
			LogUtility.getPumpALogger().debug("### Incorrect workingMessage in Process 220 ###");

		} // end if

	} // end startProcess

}
