package com.gsc.kixxhub.device.pumpa.processor;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.P1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P2_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P3_1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P3_NozzleInfo;
import com.gsc.kixxhub.common.data.pump.P3_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P5_1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P5_NozzleInfo;
import com.gsc.kixxhub.common.data.pump.P5_OdtInfo;
import com.gsc.kixxhub.common.data.pump.P5_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PC_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.device.pumpa.controller.DevInfo;
import com.gsc.kixxhub.device.pumpa.controller.DeviceSelector;
import com.gsc.kixxhub.device.pumpa.controller.ProcessSelector;
import com.gsc.kixxhub.device.pumpa.define.Processor;

/**
 * @author yd
 *
 * 101 Process. ȯ������ ����.
 * P1, P2, P3, P5, P6, P7 WorkingMessage �� �ϳ��� �����Ѵ�.
 * P3_WorkingMessage�� P3_1_WorkingMessage�� ������ �����Ѵ�.
 * P5_WorkingMessage�� P5_1_WorkingMessage�� ������ �����Ѵ�.
 * 
 * 1. Module -> P1 -> Adaptor
 * 1. Module -> P2 -> Adaptor
 * 1. Module -> P3 -> Adaptor
 * 1. Module -> P5 -> Adaptor
 * 1. Module -> P6 -> Adaptor
 * 1. Module -> P7 -> Adaptor
 *
 * ������Ʈ : PI2
 * ���泻�� : ���� (P5 ���ι��:ReApprovalOption, B/L:UseBL ������� �߰�)
 * �������� : 2016.03.15
 * ������ : ������ 
 *
 *
 */

public class P101_Process implements Processor {
	private DeviceSelector selector = null;

	
	
	public P101_Process() throws Exception {
		selector = ProcessSelector.getSelector();
	}



	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.pump.define.Processor#canContinue(com.gsc.kixxhub.data.pump.WorkingMessage)
	 */
	public boolean canContinue(WorkingMessage message) throws Exception {
		// P1, P2, P3, P5, P6, P7, PC WorkingMessage�� ������ ������� ������ �����ϴ�.
		boolean returnData = false;
		String command = message.getCommand();
		
		if (command.equals(IPumpConstant.COMMANDID_P1)
				|| command.equals(IPumpConstant.COMMANDID_P2)
				|| command.equals(IPumpConstant.COMMANDID_P3)
				|| command.equals(IPumpConstant.COMMANDID_P5)
				|| command.equals(IPumpConstant.COMMANDID_P6)
				|| command.equals(IPumpConstant.COMMANDID_P7)
				|| command.equals(IPumpConstant.COMMANDID_PC)) 
		{
			returnData = true;
			
		}	// end if
		
		return returnData;
		
	}	// end canContinue
	
	
	
	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.pump.define.Processor#getProcessID()
	 */
	public String getProcessID() throws Exception {
		
		return "P101 Process";
		
	}	// end getProcessID

	
	
	/**
	 * DeviceSelector�� WorkingMessage�� ����
	 * 
	 * @param workMsg : WorkingMessage
	 */
	private void sendModuleMsg(WorkingMessage workMsg) throws Exception {
		String command = workMsg.getCommand();
		
		if (command.equals(IPumpConstant.COMMANDID_P3)) {
			P3_WorkingMessage p3WorkingMessage = (P3_WorkingMessage) workMsg;
			P3_1_WorkingMessage p3_1WorkingMessage = null;
			
			Vector<P3_NozzleInfo> nozzleInfoVector = p3WorkingMessage.getNozzleInfo();
			
			for (int i = 0; i < nozzleInfoVector.size(); i++) {
				P3_NozzleInfo nozzleInfo = nozzleInfoVector.get(i);
				
				p3_1WorkingMessage = new P3_1_WorkingMessage();
				
				// WorkingMessage ���� �Է�
				p3_1WorkingMessage.setMessageID(workMsg.getMessageID());
				p3_1WorkingMessage.setNozzleNo(nozzleInfo.getNozzleNo());
				p3_1WorkingMessage.setDirection(workMsg.getDirection());
				
				// P3 WorkingMessage ���� �Է�
				p3_1WorkingMessage.setNozzleNumber(nozzleInfo.getNozzleNo());
				p3_1WorkingMessage.setNozzleType(nozzleInfo.getNozzleType());
				p3_1WorkingMessage.setNozzleProtocol(nozzleInfo.getNozzleProtocol());
				p3_1WorkingMessage.setPosID(nozzleInfo.getPosID());
				p3_1WorkingMessage.setGoodsCode(nozzleInfo.getGoodsCode());
				p3_1WorkingMessage.setGoodsType(nozzleInfo.getGoodsType());
				p3_1WorkingMessage.setBasePrice(nozzleInfo.getBasePrice());
				p3_1WorkingMessage.setTankNumber(nozzleInfo.getTankNumber());
				p3_1WorkingMessage.setPortNumber(nozzleInfo.getPortNumber());
				
				selector.selectDevice(p3_1WorkingMessage);
				
			}	// end for
			
		} else {
			Hashtable<String, DevInfo> table = selector.getDevAllTbl();
			Enumeration<String> keys = table.keys();
			
			while (keys.hasMoreElements()) {
				String element  = keys.nextElement();
				DevInfo devInfo = table.get(element);
				String nozzleNo = workMsg.getNozzleNo();
				
				if (devInfo.devType.equals("05")) {
					// devType ���� 05(self ODT)��� ȯ�漳�� ������ �����Ѵ�.
					if (command.equals(IPumpConstant.COMMANDID_P5)) {
						P5_WorkingMessage p5WorkingMessage = (P5_WorkingMessage) workMsg;
						P5_1_WorkingMessage tempP5WorkingMessage = null;
						
						Vector<P5_OdtInfo> odtInfoVector = p5WorkingMessage.getOdtInfo();
						
						for (int i = 0; i < odtInfoVector.size(); i++) {
							P5_OdtInfo odtInfo = odtInfoVector.get(i);
							
							String odtId = odtInfo.getOdtID();
							
							if (!(element.equals(odtId))) {
								continue;
								
							}	
							
							Vector<P5_NozzleInfo> nozzleInfoVector = odtInfo.getNozzleInfo();
							
							tempP5WorkingMessage = new P5_1_WorkingMessage();
							// WorkingMessage ���� �Է�
							tempP5WorkingMessage.setMessageID(workMsg.getMessageID());
							tempP5WorkingMessage.setNozzleNo(element);
							tempP5WorkingMessage.setConnectNozzleNo("00");
							tempP5WorkingMessage.setDirection(workMsg.getDirection());
							
							// P5 WorkingMessage ���� �Է� 
							tempP5WorkingMessage.setOdtID(odtId);
							tempP5WorkingMessage.setPosID(odtInfo.getPosID());
							tempP5WorkingMessage.setSysTime(odtInfo.getSysTime());
							tempP5WorkingMessage.setNozzleCount(odtInfo.getNozzleCount());
							tempP5WorkingMessage.setNozzleInfo(nozzleInfoVector);
							tempP5WorkingMessage.setSelfReceiptTitle(odtInfo.getSelfReceiptTitle());
							tempP5WorkingMessage.setMode(odtInfo.getMode());
							tempP5WorkingMessage.setUseFullPumping(odtInfo.getUseFullPumping());
							tempP5WorkingMessage.setReApprovalOption(odtInfo.getReApprovalOption());
							tempP5WorkingMessage.setUseBL(odtInfo.getUseBL());
							
							selector.selectDevice(tempP5WorkingMessage);
								
							
						}	// end for
						
					} else if (command.equals(IPumpConstant.COMMANDID_P1)) {
						P1_WorkingMessage p1WorkingMessage = (P1_WorkingMessage) workMsg;
						Vector<String> odtIdVector = p1WorkingMessage.getOdtId();
						
						for (int i = 0; i < odtIdVector.size(); i++) {
							String odtId = odtIdVector.get(i);
							
							if (!(element.equals(odtId))) {
								continue;
								
							}
							
							p1WorkingMessage.setNozzleNo(odtId);
							p1WorkingMessage.setConnectNozzleNo("00");
							
							selector.selectDevice((P1_WorkingMessage) p1WorkingMessage.clone());
							
						}	// end for
						
					} else if (command.equals(IPumpConstant.COMMANDID_P2)) {
						P2_WorkingMessage p2WorkingMessage = (P2_WorkingMessage) workMsg;
						Vector<String> odtIdVector = p2WorkingMessage.getOdtId();
						
						for (int i = 0; i < odtIdVector.size(); i++) {
							String odtId = odtIdVector.get(i);
							
							if (!(element.equals(odtId))) {
								continue;
								
							}
							
							p2WorkingMessage.setNozzleNo(odtIdVector.get(i));
							p2WorkingMessage.setConnectNozzleNo("00");
							
							selector.selectDevice((P2_WorkingMessage) p2WorkingMessage.clone());
							
						}	// end for
						
					} else if (command.equals(IPumpConstant.COMMANDID_PC)) {
						PC_WorkingMessage pcWorkingMessage = (PC_WorkingMessage) workMsg;
						Vector<String> odtIdVector = pcWorkingMessage.getOdtId();
						
						for (int i = 0; i < odtIdVector.size(); i++) {
							String odtId = odtIdVector.get(i);
							
							if (!(element.equals(odtId))) {
								continue;
								
							}
							
							pcWorkingMessage.setNozzleNo(odtId);
							pcWorkingMessage.setConnectNozzleNo("00");
							
							selector.selectDevice((PC_WorkingMessage) pcWorkingMessage.clone());
							
						}	// end for
						
					} else if (command.equals(IPumpConstant.COMMANDID_P7)) {
						if (element.equals(nozzleNo)) {
							selector.selectDevice(workMsg);
							
						}
						
					} else {
						// P6 : ������ �� �ð� ���� ȯ�� ���� 
						workMsg.setNozzleNo(element);
						workMsg.setConnectNozzleNo("00");
						
						selector.selectDevice(workMsg);
						
					}	// end inner if
				
				} else {
					// P7 : ������ / ODT �Ķ���� ���� 
					if (command.equals(IPumpConstant.COMMANDID_P7)) {
						if (element.equals(nozzleNo)) {
							selector.selectDevice(workMsg);
							
						}
						
					}	// end inner if
					
				}	// end if
				
			}	// end while
			
		}	// end if
		
	}	// end sendModuleMsg

	
	
	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.pump.define.Processor#startProcess(com.gsc.kixxhub.data.pump.WorkingMessage)
	 */
	public void startProcess(WorkingMessage workMsg) throws Exception {
		this.sendModuleMsg(workMsg);
			
	}	// end startProcess

}
