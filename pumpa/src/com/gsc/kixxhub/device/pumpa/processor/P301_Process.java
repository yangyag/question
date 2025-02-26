package com.gsc.kixxhub.device.pumpa.processor;

import java.util.Vector;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.P9_NozInfo;
import com.gsc.kixxhub.common.data.pump.P9_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.controller.DeviceSelector;
import com.gsc.kixxhub.device.pumpa.controller.ProcessSelector;
import com.gsc.kixxhub.device.pumpa.define.Processor;

/**
 * @author yd
 *
 * 301 Process. 충전기 ODT 영업 마감 처리.
 * ODT 개수만큼 P9 WorkingMessage에 ODT 번호를 설정하여 전송.
 * P9을 ODT 개수만큼 나누어 전송하면 ProcessSelector는 ODT 개수만큼 S5의 응답을 받게된다.
 * 301 Process는 ODT 수 만큼 생성되어있지 않으므로 S5를 처리할 수 없다.
 * 그러므로 ERROR 메세지를 LOG에 출력하지만 Pump Module은 P9의 응답 S5를 처리하지 않으므로
 * 문제가 발생하지는 않는다.
 * P9의 응답 S5가 반드시 전송되어야 한다면 
 * 301 Process에서 P9 WorkingMessage를 ODT 개수만큼 나누는 작업을
 * Pump Module에서 진행해야 한다. 
 * 
 * 
 * 1. Module -> P9 -> Adaptor
 * 2. Module <- S5  <- Adaptor  
 *
 */
public class P301_Process implements Processor {
	private String nextCommand 	= IPumpConstant.COMMANDID_P9;
	private DeviceSelector selector = null;
	

	
	public P301_Process() throws Exception {
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
		return "301 Process";
		
	}	// end getProcessID



	/**
	 * DeviceSelector로 WorkingMessage를 전송
	 * 
	 * @param workMsg : WorkingMessage
	 */
	private void sendModuleMsg(WorkingMessage workMsg) throws Exception {
		P9_WorkingMessage p9WorkingMessage = (P9_WorkingMessage) workMsg;
		Vector<P9_NozInfo> nozInfoVector = p9WorkingMessage.getP9NozInfoVector();
		
		P9_WorkingMessage tempP9WorkingMessage = new P9_WorkingMessage();
		
		for (int i = 0; i < nozInfoVector.size(); i++) {
			P9_NozInfo nozInfo = nozInfoVector.get(i);
			
			tempP9WorkingMessage.setNozzleNo(nozInfo.getOdtNo());
			tempP9WorkingMessage.setConnectNozzleNo(nozInfo.getOdtNo());
			
			selector.selectDevice(tempP9WorkingMessage);
			
		}	// end for
		
	}	// end sendModuleMsg



	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.pump.define.Processor#startProcess(com.gsc.kixxhub.data.pump.WorkingMessage)
	 */
	public void startProcess(WorkingMessage workMsg) throws Exception {
		String command = workMsg.getCommand();
		
		if (command.equals(IPumpConstant.COMMANDID_P9)) {
			this.sendModuleMsg(workMsg);
		
		} else {
			LogUtility.getPumpALogger().debug("### Incorrect workingMessage in Process 301 ###");
			
		}	// end if
		
	}	// end startProcess

}