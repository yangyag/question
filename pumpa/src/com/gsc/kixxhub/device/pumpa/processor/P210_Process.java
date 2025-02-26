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
 * 210 Process. 충전기 카드결제 승인처리.
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
			// PI의 응답으로 SB가 오는 경우(without 보너스카드 거래에서 신용 실패)
			// Process의 시작으로 SB가 오는 경우(Normal)
			// BB의 응답으로 SB가 오는 경우(with 보너스카드 거래에서 신용 실패)
			if (nextCommand.equals(IPumpConstant.COMMANDID_BB)
					|| nextCommand.equals(IPumpConstant.COMMANDID_SB)
					|| nextCommand.equals(IPumpConstant.COMMANDID_S4)) {
				returnData = true;

			} // end inner if

			// 충전기 ODT에서 판매완료 후 보너스적립, 상태값이 올라오는 경우, S8, BA전문이 무시되지 않도록 변경
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
	 * AdaptorServiceImp로 WorkingMessage를 전송
	 * 
	 * @param workMsg : WorkingMessage 
	 */
	private void sendDeviceMsg(WorkingMessage workMsg) throws Exception {
		String command 	= workMsg.getCommand();
		messageID 		= GlobalUtility.getUniqueMessageID();
		workMsg.setMessageID(messageID);

		if (command.equals(IPumpConstant.COMMANDID_SB)) {
			// 처음 거래가 실패인 경우 신용승인을 다시 요청하기 때문에 SB가 다시 올 수 있다.
			// 그 때 처음 PI가 만들어낸 TIMER는 CANCEL이 필요하다.
			if (timer != null) {
				timer.cancel();
				
			}
			
			nextCommand = IPumpConstant.COMMANDID_PI;
			
			// 카드결제 통신 실패 시 전송 될 PI 전문 
			PI_WorkingMessage piMessage = new PI_WorkingMessage();
			piMessage.setNozzleNo(nozzleNo);
			piMessage.setMode("2");
			piMessage.setCardType("0");
			piMessage.setNotice("통신실패        [확인] 버튼을 누르세요");
			
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
	 * DeviceSelector로 WorkingMessage를 전송
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