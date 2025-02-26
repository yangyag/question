package com.gsc.kixxhub.device.pumpa.controller;

import java.util.Hashtable;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.FAIL_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.AdaptorServiceImp;
import com.gsc.kixxhub.device.pumpa.define.Processor;
import com.gsc.kixxhub.device.pumpa.processor.P101_ODTProcess;
import com.gsc.kixxhub.device.pumpa.processor.P101_Process;
import com.gsc.kixxhub.device.pumpa.processor.P102_Process;
import com.gsc.kixxhub.device.pumpa.processor.P201_Process;
import com.gsc.kixxhub.device.pumpa.processor.P202_Process;
import com.gsc.kixxhub.device.pumpa.processor.P203_Process;
import com.gsc.kixxhub.device.pumpa.processor.P204_Process;
import com.gsc.kixxhub.device.pumpa.processor.P205_Process;
import com.gsc.kixxhub.device.pumpa.processor.P206_Process;
import com.gsc.kixxhub.device.pumpa.processor.P207_Process;
import com.gsc.kixxhub.device.pumpa.processor.P208_Process;
import com.gsc.kixxhub.device.pumpa.processor.P209_Process;
import com.gsc.kixxhub.device.pumpa.processor.P210_Process;
import com.gsc.kixxhub.device.pumpa.processor.P211_Process;
import com.gsc.kixxhub.device.pumpa.processor.P212_Process;
import com.gsc.kixxhub.device.pumpa.processor.P213_Process;
import com.gsc.kixxhub.device.pumpa.processor.P214_Process;
import com.gsc.kixxhub.device.pumpa.processor.P216_Process;
import com.gsc.kixxhub.device.pumpa.processor.P217_Process;
import com.gsc.kixxhub.device.pumpa.processor.P218_Process;
import com.gsc.kixxhub.device.pumpa.processor.P219_Process;
import com.gsc.kixxhub.device.pumpa.processor.P220_Process;
import com.gsc.kixxhub.device.pumpa.processor.P221_Process;
import com.gsc.kixxhub.device.pumpa.processor.P222_Process;
import com.gsc.kixxhub.device.pumpa.processor.P223_Process;
import com.gsc.kixxhub.device.pumpa.processor.P224_Process;
import com.gsc.kixxhub.device.pumpa.processor.P225_Process;
import com.gsc.kixxhub.device.pumpa.processor.P226_Process;
import com.gsc.kixxhub.device.pumpa.processor.P227_Process;
import com.gsc.kixxhub.device.pumpa.processor.P301_Process;
import com.gsc.kixxhub.device.pumpa.processor.P302_Process;
import com.gsc.kixxhub.device.pumpa.processor.P401_Process;
import com.gsc.kixxhub.device.pumpa.processor.P402_Process;
import com.gsc.kixxhub.device.pumpa.processor.P501_Process;
import com.gsc.kixxhub.device.pumpa.processor.P502_Process;
import com.gsc.kixxhub.device.pumpa.processor.P503_Process;
import com.gsc.kixxhub.device.pumpa.processor.P504_Process;
import com.gsc.kixxhub.device.pumpa.processor.P505_Process;
import com.gsc.kixxhub.device.pumpa.processor.P506_Process;
import com.gsc.kixxhub.device.pumpa.processor.P507_Process;
import com.gsc.kixxhub.device.pumpa.processor.P510_Process;
import com.gsc.kixxhub.device.pumpa.processor.P511_Process;
import com.gsc.kixxhub.device.pumpa.processor.P512_Process;
import com.gsc.kixxhub.device.pumpa.processor.P513_Process;

/*
 * Process�� ���� �����ϴ� ������ �ι� �ߺ��Ǵ� ���. 
 * -> Process�� �����ϴ� ������ ��û ������ �����Ͽ��� �� Table���� �ش� Nozzle�� Process�� �ִ��� üũ�Ͽ� Process ����.  
 * �ϳ��� Process�� ������ ��ٸ��� ��Ȳ���� ������ �ʿ���� ������ �޴´ٸ� ���ο� Process�� �����Ǿ� ó�� ����.
 * -> isContainsKey�� Ȯ���Ͽ� Process�� ����.
 * Process�� �������� �ʰ� �����ִ� ���
 * -> ��û�� ������ ������ ���� Process�� TimerTask�� ProcessSelector.removeProcess() Ȯ��.
 */


/**
 * �� Process�� �´� �ۼ��� ���� ������ ��ȿ���� �Ǵ��Ѵ�.
 * Process ������ �ùٸ��� �ʴ� ������ ������ ��� �� ������ �帧�� �����.
 * ��û�� �������� �����Ǿ� �ִ� Process�� ��� Timeout ����� ����Ͽ� �ڽ��� Process�� ������ �� �ִ�.
 * �� Process�� ������ ����� ���� �� ������ �������� ����� ������ ����.
 * 1. Process�� ���� ó���ؾ��� ������ WorkingMessage Command�� ����.
 * 2. WorkingMessage�� AdaptorServiceImp�� ����.
 * 3. WorkingMessage�� DeviceSelector�� ����.
 *
 * @author yd
 */
public class ProcessSelector {
	/**
	 * processTable�� �ش� nozzle�� process�� �ִ��� ����.
	 */
	private static boolean isContainsKey = false;
	/**
	 * ���� �������� Procee ����. �����ȣ�� Key�� ����Ѵ�.
	 */
	private static Hashtable<String, Processor> processTable = 
									new Hashtable<String, Processor>();
	/**
	 * AdaptorServiceImp���� setSelector()�� ȣ�������ν� DeviceSelector�� �����Ѵ�.
	 */
	private static DeviceSelector selector;
	
	
	
	/**
	 * Process Table�� ũ�� ���. �׽�Ʈ ��.
	 * 
	 * @throws Exception
	 */
	public static void checkProcess() throws Exception {
		LogUtility.getPumpALogger().debug(new StringBuffer("ProcessTable Size : ").append( processTable.size()).toString());
		
	}	// end putProcess
	
	

	/**
	 * �־��� WorkingMessage�� ������ Processor�� �����Ѵ�. 
	 * 
	 * COMMAND 	: 	PROCESSOR ID
	 * 
 	 *	D0		:	101
	 *	BA		:	211
	 *	P1, P2, P3, P4, P5, P6, P7	:	101
	 *	PA		:	201
	 *	S3		:	202
	 *	S4		:	203, 210
	 *	HF, QF	:	204
	 *	PB		:	205
	 *	HB, HD	:	206
	 *	HA, HC, QD	:	207
	 *	HE, QM	:	208
	 *	QL, TR	:	220
	 *	PF, PG, S9 	:	209
	 *	PI, SB	:	210
	 *	BB		:	210, 211
	 *	PP, SF	:	213
	 *	CP, TD	:	217
	 *	P9		:	301
	 *	S5		:	301, 302
	 *	P8		:	302
	 *	PE, S8	:	402
	 *	SE		:	401
	 *	SG, PQ	:	218
	 *	SH		:	210, 216
	 *	SI, SJ	:	214
	 *	ST		:	219
	 *	SY		:	102
	 *	TJ, XA	:	212
	 *  PM		:	501
	 *	GA		:	502
	 *	GB		:	503
	 *	GT		:	504
	 *	BR		:	505
	 *	BS		:	506 
	 *  F0, EX	:	����(Pump Module�� �������� ����)
	 *  PU      :   513
	 * @return Processor
	 * @throws Exception 
	 */
	private static Processor getProcessor(WorkingMessage workingMessage, String nozzleNo, String command) throws Exception{
		Processor returnProcessor = null;
		isContainsKey = processTable.containsKey(nozzleNo);
		
		if (isContainsKey) {
			returnProcessor = processTable.get(nozzleNo);
			
		} else {
			// Module�κ��� FAIL ���� �� Process ���� 
			if (command.equals(IPumpConstant.COMMANDID_FAIL)) {
				LogUtility.getPumpALogger().debug(
						new StringBuffer("# [ProcessSelector V2]  #")
						.append("ProcessSelector���� FailWorkingMessage ���� # ")
						.append("FAIL COMMAND : ")
						.append(((FAIL_WorkingMessage)workingMessage).getFailCommand()).append(" # ")
						.append("NozzleNo : ").append( nozzleNo).append(" # ") 
						.append(processTable.get(nozzleNo).getProcessID()).append(" ���� #").toString());
				removeProcess(nozzleNo);
				// �Ҹ��� ī����� ����ó��(207) 
			} else if (command.equals(IPumpConstant.COMMANDID_HA)){
				returnProcessor = new P207_Process();
				
				// �Ҹ��� �ܻ�ŷ� ����ó��(206) 
			} else if (command.equals(IPumpConstant.COMMANDID_HB)){
				returnProcessor = new P206_Process();
				
				// �پ��뼿�� ī����� ����ó��(208) 
			} else if (command.equals(IPumpConstant.COMMANDID_HE)){
				returnProcessor = new P208_Process();
				
				// ������ / ������ �������� (402) 
			} else if (command.equals(IPumpConstant.COMMANDID_S8)){
				returnProcessor = new P402_Process();
								
				// ���������ڷ� ��û/����(214)
			} else if (command.equals(IPumpConstant.COMMANDID_SI)){
				returnProcessor = new P214_Process();
				
				// ���������ڷ� ��û/����(214)
			} else if (command.equals(IPumpConstant.COMMANDID_SJ)){
				returnProcessor = new P214_Process();
				
				// �ǸſϷ��ڷ� ��û/����(219)
			} else if (command.equals(IPumpConstant.COMMANDID_ST)) {
				returnProcessor = new P219_Process();
												
				// ���а����� �ڷ� ��û(302) 
			} else if (command.equals(IPumpConstant.COMMANDID_P8)){
				returnProcessor = new P302_Process();
				
				// Preset �ڷ� ��û(204)	
			} else if (command.equals(IPumpConstant.COMMANDID_HF)){
				returnProcessor = new P204_Process();
				
				// ���� ������(201) 
			} else if (command.equals(IPumpConstant.COMMANDID_PA)){
				returnProcessor = new P201_Process();
				
				// ����/���� ����(205) 
			} else if (command.equals(IPumpConstant.COMMANDID_PB)){
				returnProcessor = new P205_Process();
				
				// ������ / ������ ��������(402)
			} else if (command.equals(IPumpConstant.COMMANDID_PE)){
				returnProcessor = new P402_Process();
				
				// Preset �ڷ� ��û (204)
			} else if (command.equals(IPumpConstant.COMMANDID_QF)){
				returnProcessor = new P204_Process();
				
				// �پ��뼿�� �ǸſϷ�(220)
			} else if (command.equals(IPumpConstant.COMMANDID_QL)){
				returnProcessor = new P220_Process();
				
				// ���� / ���� �� �ڷ� ���� (202)
			} else if (command.equals(IPumpConstant.COMMANDID_S3)){
				returnProcessor = new P202_Process();
				
				// �����Ϸ� �ڷ� ���� (203)
				// ������ ī����� ����ó��(210)
			} else if (command.equals(IPumpConstant.COMMANDID_S4)){
				returnProcessor = new P203_Process();
				
		
				// ȯ������ ����(101) 
			} else if (command.equals(IPumpConstant.COMMANDID_D0)) {
				nozzleNo = "temp";
				returnProcessor = new P101_ODTProcess();
				
				// ������ ODT ���ʽ�ī�� ���� ����ó��(211)
			} else if (command.equals(IPumpConstant.COMMANDID_BA)) {
				returnProcessor = new P211_Process();			
				
				// ȯ������ ����(101)
			} else if (command.equals(IPumpConstant.COMMANDID_P1)){
				nozzleNo = "temp";
				returnProcessor = new P101_Process();
				
				// ȯ������ ����(101)
			} else if (command.equals(IPumpConstant.COMMANDID_P2)){
				nozzleNo = "temp";
				returnProcessor = new P101_Process();
				
				// ȯ������ ����(101)
			} else if (command.equals(IPumpConstant.COMMANDID_P3)){
				nozzleNo = "temp";
				returnProcessor = new P101_Process();
				
				// ȯ������ ����(101)
			} else if (command.equals(IPumpConstant.COMMANDID_P5)){
				nozzleNo = "temp";
				returnProcessor = new P101_Process();
				
				// ȯ������ ����(101)
			} else if (command.equals(IPumpConstant.COMMANDID_P6)){
				nozzleNo = "temp";
				returnProcessor = new P101_Process();
				
				// ȯ������ ����(101)
			} else if (command.equals(IPumpConstant.COMMANDID_P7)){
				nozzleNo = "temp";
				returnProcessor = new P101_Process();			
				
				// ȯ������ ����(101)
			} else if (command.equals(IPumpConstant.COMMANDID_PC)){ 
				returnProcessor = new P101_Process();
				// ���� ���� ó��(301)
			} else if (command.equals(IPumpConstant.COMMANDID_P9)){
				returnProcessor = new P301_Process();
				
				// ������ ��ī�� ����ó�� (209)
			} else if (command.equals(IPumpConstant.COMMANDID_S9)){
				returnProcessor = new P209_Process();
				
				// ������ ī����� ����ó��(210) 
			} else if (command.equals(IPumpConstant.COMMANDID_SB)){
				returnProcessor = new P210_Process();
				
				// ���� ����̽� �̻����� ����(401) 
			} else if (command.equals(IPumpConstant.COMMANDID_SE)){
				returnProcessor = new P401_Process();
				
				// ������ ������ �߱�  ó��(213)
			} else if (command.equals(IPumpConstant.COMMANDID_SF)){
				returnProcessor = new P213_Process();
				
				// ������ ������  ��ȸ ó��(218) 
			} else if (command.equals(IPumpConstant.COMMANDID_SG)){
				//returnProcessor = processTable.get(nozzleNo);
				returnProcessor = new P218_Process();
				
				// ������ ī����� ����ó��(210)
				// ������ ���ݰ���(216)
			} else if (command.equals(IPumpConstant.COMMANDID_SH)){
				returnProcessor = new P216_Process();
			
				// ���� ��뿩�� ����(102) 
			} else if (command.equals(IPumpConstant.COMMANDID_SY)){
				returnProcessor = new P102_Process();
				
				// ������ ���ʽ� ��ȸ(217)
			} else if (command.equals(IPumpConstant.COMMANDID_TD)) {
				returnProcessor = new P217_Process();
				
				// ������ ODT ���ݿ����� ����ó��(212)
			} else if (command.equals(IPumpConstant.COMMANDID_TJ)){
				returnProcessor = new P212_Process();
				
			} else if (command.equals(IPumpConstant.COMMANDID_CA)){
				returnProcessor = new P221_Process();
				
			} else if (command.equals(IPumpConstant.COMMANDID_BI)){
				returnProcessor = new P222_Process();
				
			} else if (command.equals(IPumpConstant.COMMANDID_BC)){
				returnProcessor = new P223_Process();
				
			} else if (command.equals(IPumpConstant.COMMANDID_E0)){
				returnProcessor = new P224_Process();

			} else if (command.equals(IPumpConstant.COMMANDID_CL)){ // ���ó��(���ϴ���)
				returnProcessor = new P225_Process();
								
				// ������  ���� �߰� ����
				/* ������Ʈ�� :PI2
				 * ���泻�� : SK, SL ���� ó�� ����߰� 
				 * �������� : 2015.11.25
				 * ������ : ������*/	
			// ������ ODT ���� ��û 
			} else if (command.equals(IPumpConstant.COMMANDID_SK)){
					returnProcessor = new P226_Process();
					
            // ������ ODT ���� ���� 		
			} else if (command.equals(IPumpConstant.COMMANDID_SL)){
				returnProcessor = new P227_Process();		
			//ǥ�� ���� ���� �߰� ��
				
			//ǥ�� ���� ���� �߰� ����
				/* ������Ʈ�� :PI2
				 * ���泻�� : ǥ�ؼ��� PM, PV, GA, GR, GL, GT, FC,BR,BS ���� ó�� ����߰� 
				 * �������� : 2015.11.25
				 * ������ : ������*/	
			// ODT ������� ����(501)
			} else if (command.equals(IPumpConstant.COMMANDID_PM)){
				returnProcessor = new P501_Process();
			
				// ODT �������� ����
			} else if (command.equals(IPumpConstant.COMMANDID_PV)){
				returnProcessor = new P502_Process();
			
				// �������� ��û
			} else if (command.equals(IPumpConstant.COMMANDID_GA)){
				returnProcessor = new P503_Process();
				
				// �������� ��û ��
			} else if (command.equals(IPumpConstant.COMMANDID_GB)){
				returnProcessor = new P510_Process();
				
				// ODT ������� �Է¿�û
			} else if (command.equals(IPumpConstant.COMMANDID_GR)){
				returnProcessor = new P504_Process();
				
				// ������ ���ó��
			} else if (command.equals(IPumpConstant.COMMANDID_GL)){ 
				returnProcessor = new P505_Process();
				
				// �ǸſϷ� ó�� (������ ��� �� ����)
			} else if (command.equals(IPumpConstant.COMMANDID_GT)){ 
				returnProcessor = new P506_Process();
				
				// �������� ����
			} else if (command.equals(IPumpConstant.COMMANDID_FC)){ 
				returnProcessor = new P507_Process();
				
				// ������ ���ڵ� ��û 
			} else if (command.equals(IPumpConstant.COMMANDID_BR)){ 
				returnProcessor = new P511_Process();
				
				// ������ ���ڵ� ��û�� ���� ���� 
			} else if (command.equals(IPumpConstant.COMMANDID_BS)){ 
				returnProcessor = new P512_Process();
				
				// ODT P/G Install/Update ��û
			} else if (command.equals(IPumpConstant.COMMANDID_PU)){ 
				returnProcessor = new P513_Process();
				
			}
				// ǥ�� ���� ���� �߰� ��
			else {
				LogUtility.getPumpALogger().error(new StringBuffer("ó�� �� �� ���� WorkingMessage ����. ").append(	"Command : ").append( command).toString());
				
			}	// end inner if
			
		}	// end if
				
		return returnProcessor;
		
	}	// end getProcessor

	
	
	/**
	 * ���� ������ DeviceSelector�� ��´�. 
	 * 
	 * @return
	 */
	public static DeviceSelector getSelector() throws Exception {
		return selector;
		
	}	// end getSelector
	
	
	
	
	/**
	 * ProcessTable�� ���ο� Process�� �߰��Ѵ�.
	 * 
	 * @param nozzleNo	: �����ȣ 
	 * @param process	: Process
	 */
	public static void putProcess(String nozzleNo, Processor process) throws Exception {
		// ���� Process�� �ִٸ� ��ü�Ѵ�.
		if (processTable.containsKey(nozzleNo)) {
			processTable.remove(nozzleNo);
			
		}	// end if
		
		processTable.put(nozzleNo, process);

//		LogUtility.getPumpALogger().debug("putProcess() -> ProcessTable Size : ").append( processTable.size());

	}	// end putProcess
	
	
	
	/**
	 * ProcessTable�� Ư�� Process�� �����Ѵ�.
	 * 
	 * @param nozzleNo	: �����ϰ��� �ϴ� Process�� �����ȣ 
	 */
	public static void removeProcess(String nozzleNo) throws Exception {
		if (processTable.containsKey(nozzleNo)) {
			processTable.remove(nozzleNo);
			
		} else {
			LogUtility.getPumpALogger().error(
					"RemoveProcess fail!!");
			
		}	// end if
		
//		LogUtility.getPumpALogger().debug("removeProcess() -> ProcessTable Size : ").append( processTable.size());
	}	// end removeProcess
	

	
	/**
	 * WorkingMessage�� ������ Process�� ã�� �ش� Process�� �����Ų��.
	 * 
	 * @param workingMessage	: WorkingMessage
	 */
	public static void selectProcess (WorkingMessage workingMessage) throws Exception {
		Processor process = null;

		String nozzleNo = workingMessage.getNozzleNo();
		String command = workingMessage.getCommand();
		
		// �ش� WorkingMessage�� Processor�� �˻�.
		process = getProcessor(workingMessage, nozzleNo, command);
		
		if (process != null) {
			startProcess(process, workingMessage, nozzleNo, command);
			
		} else {
			LogUtility.getPumpALogger().error(
					new StringBuffer("[ProcessSelector V2] Process = null. ")
					.append(" # Command  : ").append( command) 
					.append(" # NozzleNo : ").append( nozzleNo)
					.append(" # isContainsKey : ").append( isContainsKey).toString());
			
		}	// end if
		
	}	// end selectProcess
	
	
	
	/**
	 * DeviceSelector�� �����Ѵ�.
	 * 
	 * @param selector	: DeviceSelector
	 */
	public static void setSelector(DeviceSelector selector) throws Exception {
		ProcessSelector.selector = selector;
		
	}	// end setSelector
	

	
	/**
	 * �־��� Processor�� start�Ѵ�.
	 * 
	 * @param process
	 * @param workingMessage
	 * @param nozzleNo
	 * @param command
	 * @throws Exception
	 */
	private static void startProcess(Processor process, 
									 WorkingMessage workingMessage, 
									 String nozzleNo, 
									 String command) throws Exception 
	{
		String direction = workingMessage.getDirection();
		
		// �־��� WorkingMessage�� �ش� Process���� ���� �ʿ�� �ϴ� �޼������� Ȯ��
		if (process.canContinue(workingMessage)) {
			process.startProcess(workingMessage);
		} else {
			LogUtility.getPumpALogger().error(
					new StringBuffer("[ProcessSelector V2]Process ������ ���� �ʴ� Command ����. ")
					.append("Process  : ").append( process.getProcessID())
					.append(" # Command  : ").append( command)
					.append(" # NozzleNo : ").append( nozzleNo).toString());
			
			// PumpModule�� ���� �۽ŵ� WorkingMessage���, FAIL�� �����Ѵ�.
			if (direction.equals(IPumpConstant.DIRECTION_FROM_MODULE)) {
				FAIL_WorkingMessage failMessage = new FAIL_WorkingMessage();
				failMessage.setNozzleNo(nozzleNo);
				failMessage.setFailCommand(command);
				
				AdaptorServiceImp.sendDeviceMsg(failMessage);
				
			}	// end inner if
			
		}	// end if	
		
	}	// end startProcess


	
	private ProcessSelector(){
		
	}

}
