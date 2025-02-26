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
 * 301 Process. ������ ODT ���� ���� ó��.
 * ODT ������ŭ P9 WorkingMessage�� ODT ��ȣ�� �����Ͽ� ����.
 * P9�� ODT ������ŭ ������ �����ϸ� ProcessSelector�� ODT ������ŭ S5�� ������ �ްԵȴ�.
 * 301 Process�� ODT �� ��ŭ �����Ǿ����� �����Ƿ� S5�� ó���� �� ����.
 * �׷��Ƿ� ERROR �޼����� LOG�� ��������� Pump Module�� P9�� ���� S5�� ó������ �����Ƿ�
 * ������ �߻������� �ʴ´�.
 * P9�� ���� S5�� �ݵ�� ���۵Ǿ�� �Ѵٸ� 
 * 301 Process���� P9 WorkingMessage�� ODT ������ŭ ������ �۾���
 * Pump Module���� �����ؾ� �Ѵ�. 
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
	 * DeviceSelector�� WorkingMessage�� ����
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