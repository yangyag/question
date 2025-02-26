package com.gsc.kixxhub.device.pumpa.translation;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.P3_1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S3_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S4_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S5_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S8_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SJ_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.Command;
import com.gsc.kixxhub.device.pumpa.common.DataStruct;
import com.gsc.kixxhub.device.pumpa.devDatas.SkDS;

/**
 * SK PROTOCOL �ڸ� ��
 * LITER = 4.3
 * PRICE = 7
 * 
 * @author yd
 *
 */
public class TransSk extends Translation {

	
	/**
	 * WorkingMessage�� ���� �������� ��ȯ�Ѵ�.
	 * 
	 * @param workingMessage 	: WorkingMessage
	 * @return					: ���� ���� 
	 */
	public byte[] generateByteStream(WorkingMessage workingMessage) throws Exception {
		byte[] returnMessage = null;
		
		String workingMessageCommand = workingMessage.getCommand();
		
		if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PB)) {
			// WorkingMessage PB : ����/���� ����
			// SK ST         	 : ���� ���� ���� 
			PB_WorkingMessage pbWorkingMessage = (PB_WorkingMessage) workingMessage;
			String commandSet = pbWorkingMessage.getCommandSet();
			String price      = pbWorkingMessage.getPrice();
			
			DataStruct stDS = new DataStruct();
			stDS.addString("command", "ST", 2);
			
			if (commandSet.equals("0")) {
				// PB CommandSet 0 : ���� ����
				stDS.addString("mode", "A", 1);
				stDS.addString("amount", price.substring(1), 7);
			
			} else if (commandSet.equals("1")) {
				// PB CommandSet 1 : ���� ����
				stDS.addString("mode", "Q", 1);
				stDS.addString("amount", pbWorkingMessage.getLiter(), 7);
				
			} else {
				LogUtility.getPumpALogger().error("### PB WorkingMessage " +
												"commandSet ERROR. " +
												"Current commandSet : " +
												commandSet + "###");
			
			}	// end inner if	
			
			byte[] tempArray = stDS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
											pbWorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PA)) {
			// WorkingMessage PA : ���� ������
			// SK RT         	 : ��� ����
			// SK IN         	 : ��� ���� ���� 
			PA_WorkingMessage paWorkingMessage = 
							(PA_WorkingMessage) workingMessage;
			String nozzleState = paWorkingMessage.getNozzleState();
			
			byte[] command = new byte[2];
			
			if (nozzleState.equals("0")) {
				// PA NozzleState 0 : ���� ����
				command[0] 	= 'S';
				command[1] 	= 'C';
	
			} else if(nozzleState.equals("1")) {
				// PA NozzleState 1 : ���� ���� ����
				command[0] 	= 'A';
				command[1] 	= 'P';
				
			} else {
				LogUtility.getPumpALogger().error("### PA WorkingMessage " +
												"nozzleState ERROR. " +
												"Current nozzleState : " +
												nozzleState + " ###");
				command = null;
			
			}	// end inner if
			
			returnMessage = this.makeProtocol(command, 
					paWorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_P3_1)) {
			// WorkingMessage P3_1 	: ������ ȯ������ ���� 
			// SK PC           		: �ܰ� ���� 
			P3_1_WorkingMessage p3WorkingMessage = (P3_1_WorkingMessage) workingMessage;

			DataStruct pcDS = new DataStruct();
			pcDS.addString("command", "PC", 2);
			pcDS.addString("basePrice", 
						p3WorkingMessage.getBasePrice().substring(0, 4), 4);
			
			byte[] tempArray = pcDS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
										p3WorkingMessage.getNozzleNumber());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_P8)) {
			// WorkingMessage P8	: Total Gauge �ڷ� ��û
			// SK CQ 				: Total Gauge ��û
			byte[] command = new byte[2];
			command[0] 	= 'C';
			command[1] 	= 'Q';
			
			returnMessage = this.makeProtocol(command, 
											workingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_SI)) {
			// WorkingMessage SI     : ���������ڷ� ��û	
			// SK CQ 				: Total Gauge ��û
			byte[] command = new byte[2];
			command[0] 	= 'C';
			command[1] 	= 'Q';
			
			returnMessage = this.makeProtocol(command, 
											workingMessage.getNozzleNo());
			
		} else {
			LogUtility.getPumpALogger().error("### Not Supported " +
					"command(" + workingMessageCommand +
					") in TransSk ###");
			returnMessage = null;
			
		}	// end if
		
		// �α� ��� 
		if (returnMessage != null) {
			LogUtility.getPumpALogger().debug(
					workingMessageCommand + "WorkingMessage -> " +  
					"[" + new String(returnMessage) + "]"); 
			
		} else {
			LogUtility.getPumpALogger().debug(
					workingMessageCommand + "WorkingMessage -> " + "null");
		}	// end if
		
		return returnMessage;
		
	}	// end generateByteStream
	
	
	
	public WorkingMessage generateWorkingMessage(byte[] message, 
			String command) throws Exception {
		WorkingMessage returnMessage = null;
		
		// byte[]�� ����ڵ带 ��´�
		String skCommandString = this.getCommand(message);
		// byte[]�� �����ȣ�� ��´�
		String nozzleNo            = this.getNozzleNo(message);
		
		if (skCommandString.equals(IPumpConstant.SK_COMMAND_AQ)) {
			// SK AQ 				: ���� �� 
			// WorkingMessage S8 	: ������/������ ���� ���� 
			S8_WorkingMessage s8WorkingMessage = new S8_WorkingMessage();
			s8WorkingMessage.setNozzleNo(nozzleNo);
			s8WorkingMessage.setDeviceType("01");
			s8WorkingMessage.setStatus("0");
			s8WorkingMessage.setStatusCode("652");
			s8WorkingMessage.setNozzleState("1");
			
			returnMessage = s8WorkingMessage;
			
		} else if (skCommandString.equals(IPumpConstant.SK_COMMAND_PP)) {
			// SK PP 				: ���� �� 
			// WorkingMessage S3 	: ������/������ ���� ���� 
			S3_WorkingMessage s3WorkingMessage = new S3_WorkingMessage();
			s3WorkingMessage.setNozzleNo(nozzleNo);

			DataStruct ppInterface = 
				SkDS.getDS(IPumpConstant.SK_COMMAND_PP);
			ppInterface.setByteStream(message);
			
			String liter = (String) ppInterface.getValue("liter");
			String price = (String) ppInterface.getValue("price");
			String basePrice = this.calcBasePrice(price, liter);
			s3WorkingMessage.setLiter(liter);
			s3WorkingMessage.setPrice(price.substring(1));
			s3WorkingMessage.setBasePrice(
					GlobalUtility.appending0Pre(basePrice, 4) + "00");
			s3WorkingMessage.setWDate(this.getSystemTime(6));
				
			returnMessage = s3WorkingMessage;
			
		} else if (skCommandString.equals(IPumpConstant.SK_COMMAND_LK)) {
			// SK LK 				: ���� �ٿ� 
			// WorkingMessage S8 	: ������/������ ���� ���� 
			S8_WorkingMessage s8WorkingMessage = new S8_WorkingMessage();
			s8WorkingMessage.setNozzleNo(nozzleNo);
			s8WorkingMessage.setDeviceType("01");
			s8WorkingMessage.setStatus("0");
			s8WorkingMessage.setStatusCode("651");
			s8WorkingMessage.setNozzleState("0");
			
			returnMessage = s8WorkingMessage;
			
		} else if (skCommandString.equals(IPumpConstant.SK_COMMAND_UL)) {
			// SK UL 				: ���� �ٿ� 
			// WorkingMessage S8 	: ������/������ ���� ���� 
			S8_WorkingMessage s8WorkingMessage = new S8_WorkingMessage();
			s8WorkingMessage.setNozzleNo(nozzleNo);
			s8WorkingMessage.setDeviceType("01");
			s8WorkingMessage.setStatus("0");
			s8WorkingMessage.setStatusCode("651");
			s8WorkingMessage.setNozzleState("2");
			
			returnMessage = s8WorkingMessage;
			
		} else if (skCommandString.equals(IPumpConstant.SK_COMMAND_CT)) {
			DataStruct ctInterface = 
				SkDS.getDS(IPumpConstant.SK_COMMAND_CT);
			ctInterface.setByteStream(message);
			
			// SK CT : Total gauge ���� 
			if (command.equals("SJ")) {
				// WorkingMessage SJ : �������� �ڷ� ���� 
				SJ_WorkingMessage sjWorkingMessage = new SJ_WorkingMessage();
				sjWorkingMessage.setNozzleNo(nozzleNo);
				sjWorkingMessage.setTotalGauge(
						(String) ctInterface.getValue("totalGauge"));
				sjWorkingMessage.setSystemTime(this.getSystemTime(12));

				returnMessage = sjWorkingMessage;
				
			} else if (command.equals("S5")){	
				// WorkingMessage S5 : Total Gauge ����
				S5_WorkingMessage s5WorkingMessage = new S5_WorkingMessage();
				s5WorkingMessage.setNozzleNo(nozzleNo);
				
				s5WorkingMessage.setTotalGauge(
						(String) ctInterface.getValue("totalGauge"));
				s5WorkingMessage.setSystemTime(this.getSystemTime(12));
				
				returnMessage = s5WorkingMessage;
				
			} else {
				LogUtility.getPumpALogger().error("TranSK ERROR. CT -> " + command);
				
			}	// end inner if
			
		} else {
			LogUtility.getPumpALogger().error("TransSK ERROR. " + 
							skCommandString + " Command�� �Ұ��ϹǷ� NULL ����.");
			returnMessage = null;
			
		}	// end if
		
		//LogUtility.getPumpALogger().debug(this.printResultLog(message, returnMessage));
		
		return returnMessage;
		
	}	// end generateWorkingMessage
	
	
	
	/**
	 * SK ������ S4 WorkingMessage�� ��ȯ�Ѵ�.
	 * 
	 * @param message	: SK ����. 
	 * 					  'TR', 'CT' ������ �ݵ�� ���ԵǾ�� �Ѵ�.
	 * @param command	: WorkingMessage Command
	 * @return			: WorkingMessage 
	 */
	public WorkingMessage generateWorkingMessage(byte[][] message, 
													String command) throws Exception {
		WorkingMessage returnMessage = null;
		
		// SK TR : �����Ϸ� �ڷ����� 
		// SK CT : Total Gauge ���� 
		// WorkingMessage S4 : �����Ϸ� �ڷ�����
		S4_WorkingMessage s4WorkingMessage = new S4_WorkingMessage();
			
		s4WorkingMessage.setFlag(this.generateBlank(1));
		s4WorkingMessage.setWDate(this.getSystemTime(6));
		s4WorkingMessage.setStatusFlag("0");
			
		for (int i = 0; i < message.length; i++) {
			String skCommandString	= this.getCommand(message[i]);
			String nozzleNo			= this.getNozzleNo(message[i]);
			
			s4WorkingMessage.setNozzleNo(nozzleNo);
				
			if (skCommandString.equals(IPumpConstant.SK_COMMAND_TR)) {
				DataStruct trInterface = 
					SkDS.getDS(IPumpConstant.SK_COMMAND_TR);
				trInterface.setByteStream(message[i]);
				
				s4WorkingMessage.setLiter((String) trInterface.getValue("liter"));
				s4WorkingMessage.setBasePrice(
						(String) trInterface.getValue("basePrice") + "00");
				s4WorkingMessage.setPrice("0" + 
						(String) trInterface.getValue("price"));
				s4WorkingMessage.setSystemTime(this.getSystemTime(12));
					
			} else if (skCommandString.equals(IPumpConstant.SK_COMMAND_CT)) {
				DataStruct ctInterface = 
					SkDS.getDS(IPumpConstant.SK_COMMAND_CT);
				ctInterface.setByteStream(message[i]);
					
				s4WorkingMessage.setTotalGauge(
						(String) ctInterface.getValue("totalGauge"));
					
			} else {

			}	// end if		
				
		}	// end for			
			
		returnMessage = s4WorkingMessage;
		
		return returnMessage;
	
	}	// end generateWorkingMessage
	
	
	
	/**
	 * SK �������� ��� �ڵ带 ���� 
	 * 
	 * @param message	: SK ����
	 * @return			: ��� �ڵ� 
	 */
	private String getCommand(byte[] message) throws Exception {
		String returnMessage = null;
		
		byte[] commandArray 	= new byte[2];
		commandArray[0] 		= message[4];
		commandArray[1] 		= message[5];
		
		returnMessage = new String(commandArray);
		
		return returnMessage;
	
	}	// end getCommand
	

	
	/**
	 * SK �������� �����ȣ�� ����
	 * 
	 * @param message	: SK ���� 
	 * @return			: ���� ��ȣ 
	 */
	private String getNozzleNo(byte[] message) throws Exception {
		String returnMessage = null;
		
		byte[] nozzleNo 	= new byte[2];
		nozzleNo[0]			= message[1];
		nozzleNo[1]     	= message[2];
		
		returnMessage = new String(nozzleNo);
		
		return returnMessage;
		
	}	// end getNozzleNo
	

	
	/**
	 * byte �迭�� data��  ������ SK ���� ���·� ��ȯ
	 * 
	 * @param data		: data
	 * @param nozzleNo	: ���� ��ȣ 
	 * @return			: SK ���� 
	 */
	private byte[] makeProtocol(byte[] data, String nozzleNo) throws Exception {
		byte[] returnData 		= null;
		byte blank 				= ' ';
		int returnDataCounter 	= 0;
		// SOH, �������ȣ(2), STX, ETX, LRC ��ŭ�� ���̸� ���Ѵ�.
		int arrayLength 		= data.length + 6;
		
		returnData = new byte[arrayLength];
		
		returnData[returnDataCounter++] = Command.SOH;					// SOH
		returnData[returnDataCounter++] = (byte) nozzleNo.charAt(0);	// �������ȣ(1)
		returnData[returnDataCounter++] = (byte) nozzleNo.charAt(1);	// �������ȣ(2)
		returnData[returnDataCounter++] = Command.STX;					// STX
		
		for (int i = 0; i < data.length; i++) {
			returnData[returnDataCounter++] = data[i];
			
		}	// end for
		
		returnData[returnDataCounter++] = Command.ETX;					// ETX
		returnData[returnDataCounter++] = blank;						// LRC		
		
		return returnData;
		
	}	// end makeProtocol
	
}