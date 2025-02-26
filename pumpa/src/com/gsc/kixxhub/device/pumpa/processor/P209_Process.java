package com.gsc.kixxhub.device.pumpa.processor;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.NAK_WorkingMessage;
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
 * 209 Process. 충전기 고객카드 승인처리.
 * 
 * 1. Module <- S9 <- Adaptor
 * 2. Module -> PG or PF or NAK -> Adaptor
 *
 */
public class P209_Process implements Processor {
	private String messageID 	= null;
	private String nextCommand 	= IPumpConstant.COMMANDID_S9;
	private String nozzleNo 	= null;
	private DeviceSelector selector = null;
	private TimerTask timer 	= null;
	

	public P209_Process() throws Exception {
		selector = ProcessSelector.getSelector();
	}



	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.pump.define.Processor#canContinue(com.gsc.kixxhub.data.pump.WorkingMessage)
	 */
	public boolean canContinue(WorkingMessage message) throws Exception {
		boolean returnData = false;
		String command = message.getCommand();
		
		/**
		 * 2016.04.04 양일준
		 * 변경내용 : 충전기 ODT에서 보너스카드와 고객카드가 동일한 카드번호로 포인트 조회를 요청할 경우,
		 * KixxHub는 T3, SK(0111) 전문을 순서대로 수신하며, processSelector.startProcess()에서 
		 * 뒤늦게 온 0111 요청전문이 무시될 수 있어 아래와 같이 변경함
		 */
		if (command.equals(IPumpConstant.COMMANDID_SK)) {  // SK(0111) 전문을 수신하여도 무시하지 않도록 변경 
			returnData = true;
		} else if (command.equals(IPumpConstant.COMMANDID_S9)) {
			if (nextCommand.equals(command)) {
				returnData = true;
				
			}	// end inner if
			
		} else if (command.equals(IPumpConstant.COMMANDID_PG)
				|| command.equals(IPumpConstant.COMMANDID_PF)
				|| command.equals(IPumpConstant.COMMANDID_NAK)) {
			
			if (!nextCommand.equals(IPumpConstant.COMMANDID_S9)) {
				returnData = true;
				
			}	// end inner if
				
		}	// end if
		
		return returnData;
		
	}	// end canContinue

	
	
	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.pump.define.Processor#getProcessID()
	 */
	public String getProcessID() throws Exception {
		return "209 Process";
		
	}	// end getProcessID
	
	
	
	/**
	 * AdaptorServiceImp로 WorkingMessage를 전송
	 * 
	 * @param workMsg : WorkingMessage 
	 */
	private void sendDeviceMsg(WorkingMessage workMsg) throws Exception {
/*		
 * PF의 응답인 S9가 발생할 시간은 알 수 없으므로 타이머를 적용하는 것은 무의미하다.
 * 2009년 3월 10일.
 * 
		// PF 응답으로 S9를 수신 받을 때는 Timer를 종료
		if (timer != null) {
			timer.cancel();
			timer = null;
			
		}// end if
*/		
		messageID = GlobalUtility.getUniqueMessageID();
		workMsg.setMessageID(messageID);
		
		NAK_WorkingMessage nakMessage = new NAK_WorkingMessage();
		nakMessage.setNozzleNo(nozzleNo);
		
		timer = new TimerTask(nozzleNo, nakMessage);
		timer.setName("Pump_209_Process");
		// S9의 응답은 더 오래 기다린다.
		KHTimer.getInstance().setSchedule(timer, 
				IPumpConstant.PUMP_PROCESS_WAITTING_TIME_FROM_ADAPTOR * 2) ;
		
		ProcessSelector.putProcess(nozzleNo, this);
		AdaptorServiceImp.sendDeviceMsg(workMsg);
		
	}	// end sendDeviceMsg



	/**
	 * DeviceSelector로 WorkingMessage를 전송
	 * 
	 * @param workMsg : WorkingMessage
	 */
	private void sendModuleMsg(WorkingMessage workMsg) throws Exception {
/*
 * PF의 응답인 S9가 발생할 시간은 알 수 없으므로 타이머를 적용하는 것은 무의미하다.
 * 2009년 3월 10일.
 * 
		if (workMsg.getCommand().equals(IPumpConstant.COMMANDID_PF)) {
			// PF : 중복차량 응답 
			nextCommand = IPumpConstant.COMMANDID_S9;
			timer = new TimerTask(nozzleNo, workMsg.getCommand(), messageID);
			timer.setName("Pump_209_Process");
			GSCTimer.getInstance().setSchedule(timer, 
					IPumpConstant.PUMP_PROCESS_WAITTING_TIME_FROM_MODULE) ;
			ProcessSelector.putProcess(nozzleNo, this);
			
		} else {
			// PG : 승인 응답, NAK : 해당차량 없음 
			ProcessSelector.removeProcess(nozzleNo);
			
		}	// end if
*/
		
		ProcessSelector.removeProcess(nozzleNo);
		selector.selectDevice(workMsg);
		
	}	// end sendModuleMsg



	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.pump.define.Processor#startProcess(com.gsc.kixxhub.data.pump.WorkingMessage)
	 * 2016.04.07 양일준
	 * 변경내용 : 고객정보 요청 처리 중 SK전문을 수신해도 처리하도록 변경  
	 */
	public void startProcess(WorkingMessage workMsg) throws Exception {
		String command = workMsg.getCommand();
		nozzleNo = workMsg.getNozzleNo();
		
		if (command.equals(IPumpConstant.COMMANDID_SK)) {
			this.sendModuleMsg(workMsg);
		} else if (command.equals(IPumpConstant.COMMANDID_PG)     ||
				command.equals(IPumpConstant.COMMANDID_PF) ||
				command.equals(IPumpConstant.COMMANDID_NAK)) {
			timer.cancel();
			this.sendModuleMsg(workMsg);	
		
		} else if (command.equals(IPumpConstant.COMMANDID_S9)) {
			nextCommand = IPumpConstant.COMMANDID_PG;
			this.sendDeviceMsg(workMsg);
			
		} else {
			LogUtility.getPumpALogger().debug("### Incorrect workingMessage in Process 209 ###");
			
		}	// end if
		
	}	// end startProcess
	
}