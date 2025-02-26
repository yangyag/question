package com.gsc.kixxhub.device.pumpa.processor;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.QL_WorkingMessage;
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
 * 223 Process. 다쓰노 셀프 지폐투입 취소 요청
 * 
 * 1. Module <- BC  <- Adaptor  
 * 2. Module -> QL  -> Adaptor
 *
 */
public class P223_Process implements Processor {
	private String messageID 	= null;
	private String nextCommand 	= IPumpConstant.COMMANDID_BC;
	private String nozzleNo 	= null;
	private DeviceSelector selector = null;
	private TimerTask timer 	= null;
	
	
	
	public P223_Process() throws Exception {
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
		return "223 Process";
		
	}
	
	
	
	private void sendDeviceMsg(WorkingMessage workMsg) throws Exception {
		messageID = GlobalUtility.getUniqueMessageID();
		workMsg.setMessageID(messageID);
		
		QL_WorkingMessage qlMessage = new QL_WorkingMessage();
		qlMessage.setNozzleNo(workMsg.getNozzleNo());
		qlMessage.setConnectNozzleNo(workMsg.getConnectNozzleNo());
		qlMessage.setContent("카운터에 문의하여 주시기 바랍니다.");
		
		timer = new TimerTask(nozzleNo, qlMessage);
		timer.setName("Pump_223_Process");
		KHTimer.getInstance().setSchedule(timer, 
				IPumpConstant.PUMP_PROCESS_WAITTING_TIME_FROM_ADAPTOR) ;
		
		ProcessSelector.putProcess(nozzleNo, this);
 		AdaptorServiceImp.sendDeviceMsg(workMsg);
 		
	}
	
	
	
	private void sendModuleMsg(WorkingMessage workMsg) throws Exception {
		selector.selectDevice(workMsg);
		
	}
	
	
	
	public void startProcess(WorkingMessage workingMessage) throws Exception {
		String command = workingMessage.getCommand();
		nozzleNo = workingMessage.getNozzleNo();
		
		if (command.equals(IPumpConstant.COMMANDID_BC)) {
			nextCommand	= IPumpConstant.COMMANDID_QL;
			this.sendDeviceMsg(workingMessage);
			
		} else if (command.equals(IPumpConstant.COMMANDID_QL)) {
			timer.cancel();
			ProcessSelector.removeProcess(nozzleNo);
			this.sendModuleMsg(workingMessage);
			
		} else {
			LogUtility.getPumpALogger().debug("### Incorrect workingMessage in Process 223 ###");
			
		}	// end if
		
	}

}