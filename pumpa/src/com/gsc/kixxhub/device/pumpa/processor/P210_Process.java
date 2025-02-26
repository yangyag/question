package com.gsc.kixxhub.device.pumpa.processor;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.NAK_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PI_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.common.utility.timer.KHTimer;
import com.gsc.kixxhub.device.pumpa.AdaptorServiceImp;
import com.gsc.kixxhub.device.pumpa.controller.DeviceSelector;
import com.gsc.kixxhub.device.pumpa.controller.ProcessSelector;
import com.gsc.kixxhub.device.pumpa.define.Processor;

/**
 * @author yd
 *
 * 210 Process. ������ ī����� ����ó��.
 * 
 * 1. Module <- SB  <- Adaptor
 * 2. Module -> PI  -> Adaptor
 * 3. Module -> BB  -> Adaptor or Module <- S4 <- Adaptor
 * 4. Module <- SH  <- Adaptor
 * 5. Module -> ACK -> Adaptor 
 *
 */
public class P210_Process implements Processor {
	private String messageID 	= null;
	private String nextCommand 	= IPumpConstant.COMMANDID_SB;
	private String nozzleNo 	= null;
	private DeviceSelector selector = null;
	private TimerTask timer 	= null;	

	
	
	public P210_Process() throws Exception {
		selector = ProcessSelector.getSelector();
	}



	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.pump.define.Processor#canContinue(com.gsc.kixxhub.data.pump.WorkingMessage)
	 */
	public boolean canContinue(WorkingMessage message) throws Exception {
		boolean returnData = false;
		String command = message.getCommand();
		
		if (command.equals(IPumpConstant.COMMANDID_PI)
				|| command.equals(IPumpConstant.COMMANDID_BB)
				|| command.equals(IPumpConstant.COMMANDID_SH)
				|| command.equals(IPumpConstant.COMMANDID_ACK)) {

			if (nextCommand.equals(command)) {
				returnData = true;

			} // end inner if

		} else if (command.equals(IPumpConstant.COMMANDID_S4)) {
			if (nextCommand.equals(IPumpConstant.COMMANDID_BB)
					|| nextCommand.equals(IPumpConstant.COMMANDID_S4)) {
				returnData = true;

			} // end inner if
			
		} else if (command.equals(IPumpConstant.COMMANDID_SB)) {
			// PI�� �������� SB�� ���� ���(without ���ʽ�ī�� �ŷ����� �ſ� ����)
			// Process�� �������� SB�� ���� ���(Normal)
			// BB�� �������� SB�� ���� ���(with ���ʽ�ī�� �ŷ����� �ſ� ����)
			if (nextCommand.equals(IPumpConstant.COMMANDID_BB)
					|| nextCommand.equals(IPumpConstant.COMMANDID_SB)
					|| nextCommand.equals(IPumpConstant.COMMANDID_S4)) {
				returnData = true;

			} // end inner if

			// ������ ODT���� �ǸſϷ� �� ���ʽ�����, ���°��� �ö���� ���, S8, BA������ ���õ��� �ʵ��� ����
		} else if (command.equals(IPumpConstant.COMMANDID_S8)||
				command.equals(IPumpConstant.COMMANDID_BA)){
			returnData = true;
		} // end if

		return returnData;

	} // end canContinue

	
	
	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.pump.define.Processor#getProcessID()
	 */
	public String getProcessID() throws Exception {
		return "210 Process";
		
	}	// end getProcessID

	
	
	/**
	 * AdaptorServiceImp�� WorkingMessage�� ����
	 * 
	 * @param workMsg : WorkingMessage 
	 */
	private void sendDeviceMsg(WorkingMessage workMsg) throws Exception {
		String command 	= workMsg.getCommand();
		messageID 		= GlobalUtility.getUniqueMessageID();
		workMsg.setMessageID(messageID);

		if (command.equals(IPumpConstant.COMMANDID_SB)) {
			// ó�� �ŷ��� ������ ��� �ſ������ �ٽ� ��û�ϱ� ������ SB�� �ٽ� �� �� �ִ�.
			// �� �� ó�� PI�� ���� TIMER�� CANCEL�� �ʿ��ϴ�.
			if (timer != null) {
				timer.cancel();
				
			}
			
			nextCommand = IPumpConstant.COMMANDID_PI;
			
			// ī����� ��� ���� �� ���� �� PI ���� 
			PI_WorkingMessage piMessage = new PI_WorkingMessage();
			piMessage.setNozzleNo(nozzleNo);
			piMessage.setMode("2");
			piMessage.setCardType("0");
			piMessage.setNotice("��Ž���        [Ȯ��] ��ư�� ��������");
			
			timer = new TimerTask(nozzleNo, piMessage);
			timer.setName("Pump_210_Process");
			KHTimer.getInstance().setSchedule(timer, 
					IPumpConstant.PUMP_PROCESS_WAITTING_TIME_FROM_ADAPTOR) ;
			
		} else if (command.equals(IPumpConstant.COMMANDID_SH)) {
			timer.cancel();
			nextCommand = IPumpConstant.COMMANDID_ACK;
			
			NAK_WorkingMessage nakMessage = new NAK_WorkingMessage();
			nakMessage.setNozzleNo(nozzleNo);
			
			timer = new TimerTask(nozzleNo, command, messageID);
			timer.setName("Pump_210_Process");
			KHTimer.getInstance().setSchedule(timer, 
					IPumpConstant.PUMP_PROCESS_WAITTING_TIME_FROM_ADAPTOR) ;
			
		} else if (command.equals(IPumpConstant.COMMANDID_S4)) {
			timer.cancel();
			nextCommand = IPumpConstant.COMMANDID_SH;
			
			timer = new TimerTask(nozzleNo, command, messageID);
			timer.setName("Pump_210_Process");
			KHTimer.getInstance().setSchedule(timer, 
					IPumpConstant.PUMP_PROCESS_WAITTING_TIME_FROM_MODULE) ;

		} // end if
		
		ProcessSelector.putProcess(nozzleNo, this);
		AdaptorServiceImp.sendDeviceMsg(workMsg);
		
	} // end sendDeviceMsg

	
	
	/**
	 * DeviceSelector�� WorkingMessage�� ����
	 * 
	 * @param workMsg : WorkingMessage
	 */
	private void sendModuleMsg(WorkingMessage workMsg) throws Exception {
		String command = workMsg.getCommand();
		
		if (command.equals(IPumpConstant.COMMANDID_PI)) {
			nextCommand = IPumpConstant.COMMANDID_BB;
			
			timer = new TimerTask(nozzleNo, command, messageID);
			timer.setName("Pump_210_Process");
			KHTimer.getInstance().setSchedule(timer, 
					IPumpConstant.PUMP_PROCESS_WAITTING_TIME_FROM_MODULE) ;
			ProcessSelector.putProcess(nozzleNo, this);
			
		} else if (command.equals(IPumpConstant.COMMANDID_BB)) {
			nextCommand = IPumpConstant.COMMANDID_S4;
			
			timer = new TimerTask(nozzleNo, command, messageID);
			timer.setName("Pump_210_Process");
			KHTimer.getInstance().setSchedule(timer, 
					IPumpConstant.PUMP_PROCESS_WAITTING_TIME_FROM_MODULE) ;
			ProcessSelector.putProcess(nozzleNo, this);
			
		} else if (command.equals(IPumpConstant.COMMANDID_ACK)) {
			ProcessSelector.removeProcess(nozzleNo);
			
		}	// end if

		selector.selectDevice(workMsg);
		
	} // end sendModuleMsg

	
	
	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.pump.define.Processor#startProcess(com.gsc.kixxhub.data.pump.WorkingMessage)
	 */
	public void startProcess(WorkingMessage workMsg) throws Exception {
		String command = workMsg.getCommand();
		nozzleNo = workMsg.getNozzleNo();

		if (command.equals(IPumpConstant.COMMANDID_SB)
				|| command.equals(IPumpConstant.COMMANDID_SH)
				|| command.equals(IPumpConstant.COMMANDID_S4)) {
			this.sendDeviceMsg(workMsg);

		} else if (command.equals(IPumpConstant.COMMANDID_PI)
				|| command.equals(IPumpConstant.COMMANDID_BB)
				|| command.equals(IPumpConstant.COMMANDID_ACK)) {
			timer.cancel();
			this.sendModuleMsg(workMsg);

		} else {
			LogUtility.getPumpALogger().debug("### Incorrect workingMessage in Process 210 ###");

		} // end if
		
	}	// end startProcess

}