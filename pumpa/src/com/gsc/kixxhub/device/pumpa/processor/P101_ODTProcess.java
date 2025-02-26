package com.gsc.kixxhub.device.pumpa.processor;

import java.util.Vector;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.D0_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.D0_odtInfo;
import com.gsc.kixxhub.common.data.pump.D1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.D2_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.D3_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.D4_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.D5_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.D6_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.D7_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.controller.DeviceSelector;
import com.gsc.kixxhub.device.pumpa.controller.ProcessSelector;
import com.gsc.kixxhub.device.pumpa.define.Processor;

/**
 * @author yd
 *
 * 101 Process(ODT). 충전기 ODT의 환경정보 설정.
 * D0_WorkingMessage를 받아 D1, D2, D3, D4, D5, D6, D7 WorkingMessage로 나누어 전송한다.
 * 
 * Module -> D0 -> Adaptor
 *
 */
public class P101_ODTProcess implements Processor {
	// Process 101 : 환경정보 설정 
	private String nextCommand = IPumpConstant.COMMANDID_D0;
	private DeviceSelector selector = null;
	
	
	
	public P101_ODTProcess() throws Exception {
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
		return "101 Process";
		
	}	// end getProcessID
	
	

	/**
	 * DeviceSelector로 WorkingMessage를 전송
	 * 
	 * @param workMsg : WorkingMessage
	 */
	private void sendModuleMsg(WorkingMessage workMsg) throws Exception {
		D0_WorkingMessage d0Message = (D0_WorkingMessage) workMsg;
		
		int odtCountI = Integer.parseInt(d0Message.getOdtCount());
		Vector<D0_odtInfo> odtInfoVector = d0Message.getOdtInfo();
		D0_odtInfo tempOdtInfo = null;
		String tempOdtId = null;
		WorkingMessage[] messageArray = new WorkingMessage[odtCountI * 7];
		D1_WorkingMessage tempD1WorkingMessage = null;
		D2_WorkingMessage tempD2WorkingMessage = null;
		D3_WorkingMessage tempD3WorkingMessage = null;
		D4_WorkingMessage tempD4WorkingMessage = null;
		D5_WorkingMessage tempD5WorkingMessage = null;
		D6_WorkingMessage tempD6WorkingMessage = null;
		D7_WorkingMessage tempD7WorkingMessage = null;
		
		// D0WorkingMessage의 D1~D7 내용 추출
		for (int i = 0, j = 0; i < messageArray.length; j++) {
			tempOdtInfo = odtInfoVector.get(j);

			tempOdtId = tempOdtInfo.getOdtID();
			
			tempD1WorkingMessage = (D1_WorkingMessage) d0Message.getD1Message().clone();
			tempD2WorkingMessage = (D2_WorkingMessage) d0Message.getD2Message().clone();
			tempD3WorkingMessage = (D3_WorkingMessage) d0Message.getD3Message().clone();
			tempD4WorkingMessage = (D4_WorkingMessage) d0Message.getD4Message().clone();
			tempD5WorkingMessage = (D5_WorkingMessage) d0Message.getD5Message().clone();
			tempD6WorkingMessage = (D6_WorkingMessage) d0Message.getD6Message().clone();
			tempD7WorkingMessage = (D7_WorkingMessage) d0Message.getD7Message().clone();
			
			tempD1WorkingMessage.setNozzleNo(tempOdtId);
			tempD2WorkingMessage.setNozzleNo(tempOdtId);
			tempD3WorkingMessage.setNozzleNo(tempOdtId);
			tempD4WorkingMessage.setNozzleNo(tempOdtId);
			tempD5WorkingMessage.setNozzleNo(tempOdtId);
			tempD6WorkingMessage.setNozzleNo(tempOdtId);
			tempD7WorkingMessage.setNozzleNo(tempOdtId);
			
			messageArray[i++] = tempD1WorkingMessage;
			messageArray[i++] = tempD2WorkingMessage;
			messageArray[i++] = tempD3WorkingMessage;
			messageArray[i++] = tempD4WorkingMessage;
			messageArray[i++] = tempD5WorkingMessage;
			messageArray[i++] = tempD6WorkingMessage;
			messageArray[i++] = tempD7WorkingMessage;

		}	// end for
		
		// D1부터 D7까지 ODT로 전송 
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < odtCountI; j++) {
				selector.selectDevice(messageArray[i + (j * 7)]);
				
			}	// end inner for
			
		}	// end for
	}	// end sendModuleMsg



	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.pump.define.Processor#startProcess(com.gsc.kixxhub.data.pump.WorkingMessage)
	 */
	public void startProcess(WorkingMessage workMsg) throws Exception {
		String command = workMsg.getCommand();
		
		if (command.equals(IPumpConstant.COMMANDID_D0)) {
			this.sendModuleMsg(workMsg);
			
		} else {
			LogUtility.getPumpALogger().debug("### Incorrect workingMessage in Process 101 ###");
			
		}	// end if
		
	}	// end startProcess

}
