package com.gsc.kixxhub.device.pumpa.processor;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.CB_WorkingMessage;
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
 * 221 Process. 다쓰노 셀프 고객 확인 처리
 * 
 * 1. Module <- CA  <- Adaptor  
 * 2. Module -> CB -> Adaptor
 *
 */
public class P221_Process implements Processor {
	private String messageID 	= null;
	private String nextCommand 	= IPumpConstant.COMMANDID_CA;
	private String nozzleNo 	= null;
	private DeviceSelector selector = null;
	private TimerTask timer 	= null;
	
	
	
	public P221_Process() throws Exception {
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
		return "221 Process";
	}
	
	
	
	private void sendDeviceMsg(WorkingMessage workMsg) throws Exception {
		messageID = GlobalUtility.getUniqueMessageID();
		workMsg.setMessageID(messageID);
		
		CB_WorkingMessage cbMessage = new CB_WorkingMessage();
		cbMessage.setNozzleNo(workMsg.getNozzleNo());
		cbMessage.setConnectNozzleNo(workMsg.getConnectNozzleNo());
		cbMessage.setCustomerType("1");
		
		// CA에 대한 응답은 예외적으로 11초의 타임아웃을 설정한다.
		timer = new TimerTask(nozzleNo, cbMessage);
		timer.setName("Pump_221_Process");
		KHTimer.getInstance().setSchedule(timer, 11000) ;
		
		ProcessSelector.putProcess(nozzleNo, this);
 		AdaptorServiceImp.sendDeviceMsg(workMsg);
	}
	
	
	
	private void sendModuleMsg(WorkingMessage workMsg) throws Exception {
		selector.selectDevice(workMsg);
		
	}
	
	
	
	public void startProcess(WorkingMessage workingMessage) throws Exception {
		String command = workingMessage.getCommand();
		nozzleNo = workingMessage.getNozzleNo();
		
		if (command.equals(IPumpConstant.COMMANDID_CA)) {
			nextCommand	= IPumpConstant.COMMANDID_CB;
			this.sendDeviceMsg(workingMessage);
			
		} else if (command.equals(IPumpConstant.COMMANDID_CB)) {
			timer.cancel();
			ProcessSelector.removeProcess(nozzleNo);
			this.sendModuleMsg(workingMessage);
			
		} else {
			LogUtility.getPumpALogger().debug("### Incorrect workingMessage in Process 221 ###");
			
		}
		
	}
	
}
