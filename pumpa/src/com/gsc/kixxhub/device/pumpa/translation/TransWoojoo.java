package com.gsc.kixxhub.device.pumpa.translation;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.F0_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P3_1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S3_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S4_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S5_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S8_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SJ_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.Command;
import com.gsc.kixxhub.device.pumpa.common.DataStruct;
import com.gsc.kixxhub.device.pumpa.devDatas.WoojooDS;

/**
 * WOOJOO PROTOCOL �ڸ� ��
 * LITER = 4.3 
 * PRICE = 6
 * 
 * 
 * 
 * @author yd
 *
 */
public class TransWoojoo extends Translation {		
	/**
	 * WorkingMessage�� ���� �������� ��ȯ�Ѵ�.
	 * 
	 * @param workingMessage 	: WorkingMessage
	 * @return					: ���� ���� 
	 */
	public byte[] generateByteStream(WorkingMessage workingMessage) throws Exception {
		byte[] returnMessage = null;
		
		String workingMessageCommand = workingMessage.getCommand();
				
		if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PA)) {
			// WorkingMessage PA : ���� ������
			// Woojoo C0         : �����㰡 ��ҿ�û
			// Woojoo E0         : ���� ���� ���� 
			PA_WorkingMessage paWorkingMessage = (PA_WorkingMessage) workingMessage;
			String nozzleState = paWorkingMessage.getNozzleState();
			
			if (nozzleState.equals("0")) {
				// PA NozzleState 0 : ���� ����
				returnMessage 		= new byte[2];
				returnMessage[0] 	= 'c';
				returnMessage[1] 	= '0';
				
				returnMessage = this.makeProtocol(returnMessage, paWorkingMessage.getNozzleNo());
	
			} else if(nozzleState.equals("1")) {
				// PA NozzleState 1 : ���� ���� ����
				
				/*
				 * ���� �㰡 ���� 
				 * 
				DataStruct e0DS = new DataStruct();
				e0DS.addString("command", "e0", 2);
				e0DS.addString("mode", "0", 1);
				e0DS.addString("liter", "0000000", 7);
				e0DS.addString("basePrice", "0000", 4);
				e0DS.addString("price", "000000", 6);
				
				byte[] tempArray = e0DS.getByteStream();
				returnMessage = this.makeProtocol(tempArray, 
											paWorkingMessage.getNozzleNo());
				*/
				returnMessage = null;
			
			} else {
				LogUtility.getPumpALogger().error("### PA WorkingMessage " +
												"nozzleState ERROR. " +
												"Current nozzleState : " +
												nozzleState + " ###");
				returnMessage = null;
			
			}	// end inner if
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PB)) {
			// WorkingMessage PB : ����/���� ����
			// Woojoo E0         : ���� ���� ���� 
			PB_WorkingMessage pbWorkingMessage = (PB_WorkingMessage) workingMessage;
			String commandSet = pbWorkingMessage.getCommandSet();
			String price      = pbWorkingMessage.getPrice();
			
			DataStruct e0DS = new DataStruct();
			e0DS.addString("command", "e0", 2);
			
			if (commandSet.equals("0")) {
				// PB CommandSet 0 : ���� ����
				e0DS.addString("mode", "2", 1);
				e0DS.addString("liter", "0000000", 7);
				e0DS.addString("basePrice", pbWorkingMessage.getBasePrice().substring(0, 4), 4);
				e0DS.addString("price", price.substring(2), 6);
			
			} else if (commandSet.equals("1")) {
				// PB CommandSet 1 : ���� ����
				e0DS.addString("mode", "1", 1);
				e0DS.addString("liter", pbWorkingMessage.getLiter(), 7);
				e0DS.addString("basePrice", pbWorkingMessage.getBasePrice().substring(0, 4), 4);
				e0DS.addString("price", "000000", 6);
				
			} else if (commandSet.equals("2")) {
				// PB CommandSet 2 : Full ���� 
				e0DS.addString("mode", "0", 1);
				e0DS.addString("liter", "0000000", 7);
				e0DS.addString("basePrice", pbWorkingMessage.getBasePrice().substring(0, 4), 4);
				e0DS.addString("price", "000000", 6);
				
			} else if (commandSet.equals("3")) {
				// PB CommandSet 3 : �ܰ� ����  
				e0DS.addString("mode", "3", 1);
				e0DS.addString("liter", pbWorkingMessage.getLiter(), 7);
				e0DS.addString("basePrice", pbWorkingMessage.getBasePrice().substring(0, 4), 4);
				e0DS.addString("price", price.substring(2), 6);
			
			} else {
				LogUtility.getPumpALogger().error("### PB WorkingMessage " +
												"commandSet ERROR. " +
												"Current commandSet : " +
												commandSet + "###");
			
			}	// end inner if	
			
			byte[] tempArray = e0DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, pbWorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_P3_1)) {
			// WorkingMessage P3_1 : ������ ȯ������ ���� 
			// Woojoo E0           : ���� ���� ���� 
			P3_1_WorkingMessage p3WorkingMessage = (P3_1_WorkingMessage) workingMessage;

			DataStruct e0DS = new DataStruct();
			e0DS.addString("command", "e0", 2);
			e0DS.addString("mode", "0", 1);
			e0DS.addString("liter", "0000000", 7);
			e0DS.addString("basePrice", p3WorkingMessage.getBasePrice().substring(0, 4), 4);
			e0DS.addString("price", "000000", 6);
			
			byte[] tempArray = e0DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, p3WorkingMessage.getNozzleNumber());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_P8)) {
			// WorkingMessage P8     : Total Gauge �ڷ� ��û
			// Woojoo T0 (S/C->Pump) : Total Gauge ��û
			returnMessage 		= new byte[2];
			returnMessage[0] 	= 't';
			returnMessage[1] 	= '0';
			
			returnMessage = this.makeProtocol(returnMessage, workingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_SI)) {
			// WorkingMessage SI     : ���������ڷ� ��û	
			// Woojoo T0 (S/C->Pump) : Total Gauge ��û
			returnMessage 		= new byte[2];
			returnMessage[0] 	= 't';
			returnMessage[1] 	= '0';
			
			returnMessage = this.makeProtocol(returnMessage, workingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_F0)) {
			F0_WorkingMessage f0WorkingMessage = (F0_WorkingMessage) workingMessage;
			
			DataStruct f0DS = new DataStruct();
			f0DS.addString("command", "f0", 2);
			f0DS.addString("flag", f0WorkingMessage.getFlag(), 1);
			
			byte[] tempArray = f0DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, f0WorkingMessage.getTargetNozzleNo());
		
		} else {
			LogUtility.getPumpALogger().error("### Not Supported " + "command(" + workingMessageCommand + ") in TransWoojoo ###");
			returnMessage = null;
		
		}	// end if
		
		/*
		// �α� ��� 
		if (returnMessage != null) {
			LogUtility.getPumpALogger().debug(
					workingMessageCommand + "WorkingMessage -> " +  
					"[" + new String(returnMessage) + "]"); 
			
		} else {
			LogUtility.getPumpALogger().debug(
					workingMessageCommand + "WorkingMessage -> " + "null");
		}	// end if
		*/
		return returnMessage;
	
	}	// end generateByteArray
	
	/**
	 * ���� ������ WorkingMessage�� ��ȯ�Ѵ�.
	 * 
	 * @param message	: ���� ����
	 * @param command	: WorkingMessage Command
	 * @return			: WorkingMessage 
	 */
	public WorkingMessage generateWorkingMessage(byte[] message, String command) throws Exception {
		
		WorkingMessage returnMessage = null;
		
		// byte[]�� ����ڵ带 ��´�
		String woojooCommandString = this.getCommand(message).substring(0, 1);
		// byte[]�� �����ȣ�� ��´�
		String nozzleNo            = this.getNozzleNo(message);
		
		if (woojooCommandString.equals("p")) {
			// Woojoo P0 (Pump->S/C) : �����Ϸ� �ڷ����� 
			// WorkingMessage S3     : ����/���� �� �ڷ�����
			S3_WorkingMessage s3WorkingMessage = new S3_WorkingMessage();
			s3WorkingMessage.setNozzleNo(nozzleNo);

			DataStruct p0Interface = WoojooDS.getDS("P0", WoojooDS.FROM_PUMP);
			p0Interface.setByteStream(message);
				
			s3WorkingMessage.setLiter((String) p0Interface.getValue("liter"));
			s3WorkingMessage.setPrice((String) p0Interface.getValue("price"));
			s3WorkingMessage.setBasePrice((String) p0Interface.getValue("basePrice") + "00");
			s3WorkingMessage.setWDate(this.getSystemTime(6));
			
			returnMessage = s3WorkingMessage;
			
		} else if (woojooCommandString.equals("q")) {
			// Woojoo q0 (Pump->S/C) : �跮���� ���� ���°� ����  
			// WorkingMessage Q0     : 
			S8_WorkingMessage s8WorkingMessage = new S8_WorkingMessage();
			s8WorkingMessage.setNozzleNo(nozzleNo);

			DataStruct q0Interface = WoojooDS.getDS("Q0", WoojooDS.FROM_PUMP);
			q0Interface.setByteStream(message);
			
			s8WorkingMessage.setDeviceType("01");
			s8WorkingMessage.setStatus("1");
			s8WorkingMessage.setStatusCode((String) q0Interface.getValue("code"));
				
			returnMessage = s8WorkingMessage;
				
		} else if (woojooCommandString.equals("t")) {
			// Woojoo T0 (Pump->S/C) : Total gauge ���� 
			if (command.equals("SJ")) {
				// WorkingMessage SJ : �������� �ڷ� ���� 
				SJ_WorkingMessage sjWorkingMessage = new SJ_WorkingMessage();
				sjWorkingMessage.setNozzleNo(nozzleNo);

				DataStruct t0Interface = WoojooDS.getDS("T0", WoojooDS.FROM_PUMP);
				t0Interface.setByteStream(message);
				
				sjWorkingMessage.setTotalGauge((String) t0Interface.getValue("totalGauge"));
				sjWorkingMessage.setSystemTime(this.getSystemTime(12));

				returnMessage = sjWorkingMessage;
				
			} else if (command.equals("S5")){	
				// WorkingMessage S5 : Total Gauge ����
				S5_WorkingMessage s5WorkingMessage = new S5_WorkingMessage();
				s5WorkingMessage.setNozzleNo(nozzleNo);

				DataStruct t0Interface = WoojooDS.getDS("T0", WoojooDS.FROM_PUMP);
				t0Interface.setByteStream(message);
				
				s5WorkingMessage.setTotalGauge((String) t0Interface.getValue("totalGauge"));
				s5WorkingMessage.setSystemTime(this.getSystemTime(12));
				
				returnMessage = s5WorkingMessage;
				
			} else {
				LogUtility.getPumpALogger().error("### Can't translate T0 -> " + command + " ###");
				
			}	// end inner if
			
		} else if (woojooCommandString.equals("s")) {
			// Woojoo S0 (Pump->S/C) : ������ ���� ���� 
			// WorkingMessage S8     : ������/������ ���� ����
			S8_WorkingMessage s8WorkingMessage = new S8_WorkingMessage();
			s8WorkingMessage.setNozzleNo(nozzleNo);
			
			DataStruct s0Interface = WoojooDS.getDS("S0", WoojooDS.FROM_PUMP);
			s0Interface.setByteStream(message);
			String mode = (String) s0Interface.getValue("mode");
				
			s8WorkingMessage.setDeviceType("01");
			s8WorkingMessage.setStatus("0");
				
			if (mode.equals("0") || mode.equals("2")) {
				// Nozzle Down
				s8WorkingMessage.setStatusCode("651");
				
			} else if (mode.equals("1") || mode.equals("3")) {
				// Nozzle Up
				s8WorkingMessage.setStatusCode("652");
				
			} else if (mode.equals("4")) {
				// ���� ��
				s8WorkingMessage.setStatusCode("653");
				
			} else if (mode.equals("5")) {
				// ���� �Ϸ�
				s8WorkingMessage.setStatusCode("654");				
				
			} else {
				LogUtility.getPumpALogger().error("###  ERROR : Incorrect mode" +
										" in 's0' Woojoo protocol. " +
										" Current mode : " + mode + " ###");
				
			}	// end inner if
				
			s8WorkingMessage.setErrMsg(this.generateBlank(20));
			s8WorkingMessage.setDetectTime(this.getSystemTime(12));
			s8WorkingMessage.setVersion(this.generateBlank(9));
			
			returnMessage = s8WorkingMessage;
				
		} else {
			LogUtility.getPumpALogger().error("### Not Supported command(" + 	woojooCommandString + ") in TransWoojoo ###");
			returnMessage = null;
		
		}	// end if
		
//		LogUtility.getPumpALogger().debug(this.printResultLog(message, returnMessage)); 
		
		return returnMessage;
		
	}	// end generateWorkingMessage
		
	
	/**
	 * ���� ������ S4 WorkingMessage�� ��ȯ�Ѵ�.
	 * 
	 * @param message	: ���� ����. 
	 * 					  'p0', 't0' ������ �ݵ�� ���ԵǾ�� �Ѵ�.
	 * @param command	: WorkingMessage Command
	 * @return			: WorkingMessage 
	 */
	public WorkingMessage generateWorkingMessage(byte[][] message, String command) throws Exception {
		WorkingMessage returnMessage = null;
		
		// Woojoo P0 (Pump->S/C) : �����Ϸ� �ڷ����� 
		// Woojoo T0 (Pump->S/C) : Total Gauge ���� 
		// WorkingMessage S4 : ������ �����Ϸ� �ڷ�����
		S4_WorkingMessage s4WorkingMessage = new S4_WorkingMessage();
			
		s4WorkingMessage.setFlag(this.generateBlank(1));
		s4WorkingMessage.setWDate(this.getSystemTime(6));
		s4WorkingMessage.setStatusFlag("0");
		
		for (int i = 0; i < message.length; i++) {
			String woojooCommandString 	= this.getCommand(message[i]);
			String nozzleNo            					= this.getNozzleNo(message[i]);
			
			s4WorkingMessage.setNozzleNo(nozzleNo);
			
			if (woojooCommandString.equals("p0")) {
				DataStruct p0Interface = WoojooDS.getDS("P0", WoojooDS.FROM_PUMP);
				p0Interface.setByteStream(message[i]);
				
				s4WorkingMessage.setLiter((String) p0Interface.getValue("liter"));
				s4WorkingMessage.setBasePrice((String) p0Interface.getValue("basePrice") + "00");
				s4WorkingMessage.setPrice("00" + (String) p0Interface.getValue("price"));
				s4WorkingMessage.setSystemTime(this.getSystemTime(12));
				
			} else if (woojooCommandString.equals("t0")) {
				DataStruct t0Interface = WoojooDS.getDS("T0", WoojooDS.FROM_PUMP);
				t0Interface.setByteStream(message[i]);
				
				s4WorkingMessage.setTotalGauge((String) t0Interface.getValue("totalGauge"));
					
			} else {

			}	// end inner if		
				
		}	// end for			
			
		returnMessage = s4WorkingMessage;
		
		return returnMessage;
	
	}	// end generateWorkingMessage
	

	
	/**
	 * ���� �������� ��� �ڵ带 ���� 
	 * 
	 * @param message	: ���� ����
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
	 * ���� �������� �����ȣ�� ����
	 * 
	 * @param message	: ���� ���� 
	 * @return			: ���� ��ȣ 
	 */
	private String getNozzleNo(byte[] message) throws Exception {
		String returnMessage = null;
		
		byte[] nozzleNo 	= new byte[2];
		nozzleNo[0]		= message[2];
		nozzleNo[1]     	= message[3];
		
		returnMessage = new String(nozzleNo);
		
		return returnMessage;
		
	}	// end getNozzleNo
	

	
	/**
	 * byte �迭�� data��  ������ ���� ���� ���·� ��ȯ
	 * 
	 * @param data		: data
	 * @param nozzleNo	: ���� ��ȣ 
	 * @return			: ���� ���� 
	 */
	private byte[] makeProtocol(byte[] data, String nozzleNo) throws Exception {
		byte[] returnData 		= null;
		byte blank 					= ' ';
		int returnDataCounter 	= 0;
		byte nozzleByte 		= Byte.parseByte(nozzleNo);
		// SA, STX, �������ȣ(2), ETX, BCC ��ŭ�� ���̸� ���Ѵ�.
		int arrayLength 		= data.length + 6;
		
		returnData = new byte[arrayLength];
		
		returnData[returnDataCounter++] = (byte) (nozzleByte + 0x40);// SA
		returnData[returnDataCounter++] = Command.STX;					// STX
		returnData[returnDataCounter++] = (byte) nozzleNo.charAt(0);	// �������ȣ(1)
		returnData[returnDataCounter++] = (byte) nozzleNo.charAt(1);	// �������ȣ(2)
		
		for (int i = 0; i < data.length; i++) {
			returnData[returnDataCounter++] = data[i];
		}	// end for
		
		returnData[returnDataCounter++] = Command.ETX;		// ETX
		returnData[returnDataCounter++] = blank;						// BCC		
		
		return returnData;
		
	}	// end makeProtocol

}