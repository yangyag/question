package com.gsc.kixxhub.device.pumpa.processor;

import com.gsc.kixxhub.common.data.IPumpConstant;
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
 * 402 Process. 주유기/충전기 상태전송.
 * 
 * 1. Module -> PE -> Adaptor or 2. Module <- S8 <- Adaptor
 * 2. Module <- S8 <- Adaptor (PE로 시작한 경우) 
 *
 */
public class P402_Process implements Processor {
	private String messageID 	= null;
	private String nextCommand 	= null;
	private String nozzleNo 	= null;
	private DeviceSelector selector = null;
	private TimerTask timer 	= null;
	

	
	public P402_Process() throws Exception {
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
			// 402 Process를 시작하는 전문 구분 
			if (command.equals(IPumpConstant.COMMANDID_PE) 
					|| command.equals(IPumpConstant.COMMANDID_S8)) {
				returnData = true;
				
			}	// end inner if
			
		}	// end if
		
		return returnData;
		
	}	// end canContinue

	
	
	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.pump.define.Processor#getProcessID()
	 */
	public String getProcessID() throws Exception {
		return "402 Process";
		
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
		messageID 	= workMsg.getMessageID();
		timer 		= new TimerTask(nozzleNo, workMsg.getCommand(), messageID);
		timer.setName("Pump_402_Process");
		KHTimer.getInstance().setSchedule(timer, 
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
		
		if (command.equals(IPumpConstant.COMMANDID_PE)) {
			nextCommand = IPumpConstant.COMMANDID_S8;

			this.sendModuleMsg(workMsg);
		
		} else if (command.equals(IPumpConstant.COMMANDID_S8)) {
			// PE의 응답으로 S8이 전송 된 경우
			if (IPumpConstant.COMMANDID_S8.equals(nextCommand)) {
				timer.cancel();
				ProcessSelector.removeProcess(nozzleNo);
				
			}	// end inner if
			
			this.sendDeviceMsg(workMsg);
			
		} else {
			LogUtility.getPumpALogger().debug("### Incorrect workingMessage in Process 402 ###");
			
		}	// end if
		
	}	// end startProcess
	
}