package com.gsc.kixxhub.device.pumpa.processor;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.HA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HC_WorkingMessage;
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
 * 207 Process. 소모셀프 카드결제 승인처리.
 * 
 * 1. Module <- HA <- Adaptor
 * 2. Module -> HC -> Adaptor
 *
 */
public class P207_Process implements Processor {
	private String messageID 	= null;
	private String nextCommand 	= IPumpConstant.COMMANDID_HA;
	private String nozzleNo  	= null;
	private DeviceSelector selector = null;
	private TimerTask timer		= null;
	

	
	public P207_Process() throws Exception {
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
	 * @see com.gsc.kixxhub.adaptor.pump.define.Processor#getProcessID()
	 */
	public String getProcessID() throws Exception {
		return "207 Process";
		
	}	// end getProcessID
	
	
	
	/**
	 * AdaptorServiceImp로 WorkingMessage를 전송
	 * 
	 * @param workMsg : WorkingMessage 
	 */
	private void sendDeviceMsg(WorkingMessage workMsg) throws Exception {
		messageID = GlobalUtility.getUniqueMessageID();
		workMsg.setMessageID(messageID);

		HA_WorkingMessage haMessage = (HA_WorkingMessage) workMsg;
		// 카드결제 통신 실패 시 전송 될 HC 전문 
		HC_WorkingMessage hcMessage = new HC_WorkingMessage();
		String trType 				= haMessage.getTrType();
		
		hcMessage.setNozzleNo(haMessage.getNozzleNo());
		hcMessage.setConnectNozzleNo(haMessage.getConnectNozzleNo());
		
		if (trType.equals("2") || trType.equals("4") || 
				trType.equals("B") || trType.equals("D") || 
				trType.equals("G") || trType.equals("I")) {
			// 취소 요청에 대한 응답은 취소 실패
			hcMessage.setMode("6");
			
		} else {
			// 취소 요청 외 응답은 통신 실패 
			hcMessage.setMode("3");
			
		}	// end if
		
		hcMessage.setTrType(trType);
		hcMessage.setPrice(haMessage.getPrice());
		hcMessage.setCardNo(haMessage.getCardNumber());
		hcMessage.setBonusCardNumber(haMessage.getBonusCard());
		
		timer = new TimerTask(nozzleNo, hcMessage);
		timer.setName("Pump_207_Process");
		KHTimer.getInstance().setSchedule(timer, 
				IPumpConstant.PUMP_PROCESS_WAITTING_TIME_FROM_ADAPTOR) ;
		
		ProcessSelector.putProcess(nozzleNo, this);
		AdaptorServiceImp.sendDeviceMsg(workMsg);
		
	}	// end sendDeviceMsg



	/**
	 * DeviceSelector로 WorkingMessage를 전송
	 * 
	 * @param workMsg : WorkingMessage
	 */
	private void sendModuleMsg(WorkingMessage workMsg) throws Exception {
		selector.selectDevice(workMsg);
		
	}	// end sendModuleMsg



	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.pump.define.Processor#startProcess(com.gsc.kixxhub.data.pump.WorkingMessage)
	 */
	public void startProcess(WorkingMessage workMsg) throws Exception {
		String command = workMsg.getCommand();
		nozzleNo = workMsg.getNozzleNo();
		
		if (command.equals(IPumpConstant.COMMANDID_HA)) {
			nextCommand = IPumpConstant.COMMANDID_HC;
			this.sendDeviceMsg(workMsg);
			
		} else if(command.equals(IPumpConstant.COMMANDID_HC)) {
			timer.cancel();
			ProcessSelector.removeProcess(nozzleNo);
			this.sendModuleMsg(workMsg);
		
		} else {
			LogUtility.getPumpALogger().debug("### Incorrect workingMessage in Process 207 ###");
			
		}	// end if
		
	}	// end startProcess

}
