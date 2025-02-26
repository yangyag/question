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
 * 209 Process. ������ ��ī�� ����ó��.
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
		 * 2016.04.04 ������
		 * ���泻�� : ������ ODT���� ���ʽ�ī��� ��ī�尡 ������ ī���ȣ�� ����Ʈ ��ȸ�� ��û�� ���,
		 * KixxHub�� T3, SK(0111) ������ ������� �����ϸ�, processSelector.startProcess()���� 
		 * �ڴʰ� �� 0111 ��û������ ���õ� �� �־� �Ʒ��� ���� ������
		 */
		if (command.equals(IPumpConstant.COMMANDID_SK)) {  // SK(0111) ������ �����Ͽ��� �������� �ʵ��� ���� 
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
	 * AdaptorServiceImp�� WorkingMessage�� ����
	 * 
	 * @param workMsg : WorkingMessage 
	 */
	private void sendDeviceMsg(WorkingMessage workMsg) throws Exception {
/*		
 * PF�� ������ S9�� �߻��� �ð��� �� �� �����Ƿ� Ÿ�̸Ӹ� �����ϴ� ���� ���ǹ��ϴ�.
 * 2009�� 3�� 10��.
 * 
		// PF �������� S9�� ���� ���� ���� Timer�� ����
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
		// S9�� ������ �� ���� ��ٸ���.
		KHTimer.getInstance().setSchedule(timer, 
				IPumpConstant.PUMP_PROCESS_WAITTING_TIME_FROM_ADAPTOR * 2) ;
		
		ProcessSelector.putProcess(nozzleNo, this);
		AdaptorServiceImp.sendDeviceMsg(workMsg);
		
	}	// end sendDeviceMsg



	/**
	 * DeviceSelector�� WorkingMessage�� ����
	 * 
	 * @param workMsg : WorkingMessage
	 */
	private void sendModuleMsg(WorkingMessage workMsg) throws Exception {
/*
 * PF�� ������ S9�� �߻��� �ð��� �� �� �����Ƿ� Ÿ�̸Ӹ� �����ϴ� ���� ���ǹ��ϴ�.
 * 2009�� 3�� 10��.
 * 
		if (workMsg.getCommand().equals(IPumpConstant.COMMANDID_PF)) {
			// PF : �ߺ����� ���� 
			nextCommand = IPumpConstant.COMMANDID_S9;
			timer = new TimerTask(nozzleNo, workMsg.getCommand(), messageID);
			timer.setName("Pump_209_Process");
			GSCTimer.getInstance().setSchedule(timer, 
					IPumpConstant.PUMP_PROCESS_WAITTING_TIME_FROM_MODULE) ;
			ProcessSelector.putProcess(nozzleNo, this);
			
		} else {
			// PG : ���� ����, NAK : �ش����� ���� 
			ProcessSelector.removeProcess(nozzleNo);
			
		}	// end if
*/
		
		ProcessSelector.removeProcess(nozzleNo);
		selector.selectDevice(workMsg);
		
	}	// end sendModuleMsg



	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.pump.define.Processor#startProcess(com.gsc.kixxhub.data.pump.WorkingMessage)
	 * 2016.04.07 ������
	 * ���泻�� : ������ ��û ó�� �� SK������ �����ص� ó���ϵ��� ����  
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