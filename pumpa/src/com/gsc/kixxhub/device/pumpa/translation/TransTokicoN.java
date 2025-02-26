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
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.Command;
import com.gsc.kixxhub.device.pumpa.common.DataStruct;
import com.gsc.kixxhub.device.pumpa.devDatas.TatsunoNDS;

/**
 * TOKICO ���� PROTOCOL �ڸ� ��
 * LITER = 4.2
 * PRICE = 6
 * 
 * @author yd
 *
 */
public class TransTokicoN extends Translation {

	/**
	 * WorkingMessage�� ������ ���� �������� ��ȯ�Ѵ�.
	 * 
	 * @param workingMessage 	: WorkingMessage
	 * @return					: ������ ���� ���� 
	 */
	public byte[] generateByteStream(WorkingMessage workingMessage) throws Exception {
		byte[] returnMessage = null;

		String workingMessageCommand = workingMessage.getCommand();

		
		if (workingMessageCommand.equals(IPumpConstant.COMMANDID_P8)) {
			// WorkingMessage P8 : ���а����� �ڷ� ��û
			// TatsunoN 20 : ������ ����ġ �䱸
			returnMessage = new byte[2];
			returnMessage[0] = 0x32;
			returnMessage[1] = 0x30;

			returnMessage = this.makeProtocol(returnMessage, workingMessage
					.getNozzleNo());

		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_SI)) {
			// WorkingMessage SI : ���������ڷ� ��û
			// TatsunoN 20 : ������ ����ġ �䱸
			returnMessage = new byte[2];
			returnMessage[0] = 0x32;
			returnMessage[1] = 0x30;

			returnMessage = this.makeProtocol(returnMessage, workingMessage
					.getNozzleNo());

		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PE)) {
			// WorkingMessage PE : ������ / ������ ���� ��û
			// TatsunoN 15 : ������ Status �䱸
			returnMessage = new byte[2];
			returnMessage[0] = 0x31;
			returnMessage[1] = 0x35;

			returnMessage = this.makeProtocol(returnMessage, workingMessage
					.getNozzleNo());

		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PA)) {
			// WorkingMessage PA : ���� ������
			// TatsunoN 12 : PumpLock
			// TatsunoN 14 : PumpLock ����
			PA_WorkingMessage paWorkingMessage = (PA_WorkingMessage) workingMessage;

			DataStruct t1xDS = new DataStruct();

			String state = paWorkingMessage.getNozzleState();

			if (state.equals("0")) {
				// ���� ����
				t1xDS.addString("command", "12", 2);

			} else if (state.equals("1")) {
				// ���� ���� ����
				t1xDS.addString("command", "14", 2);

			} else {
				LogUtility.getPumpALogger().error(
						"### State Error in " + "PA WorkingMessage. "
								+ "Current state : " + state + " ###");

			} // end inner if

			byte[] tempArray = t1xDS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, paWorkingMessage
					.getNozzleNo());

		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_P3_1)) {
			// WorkingMessage P3_1 : ������ ȯ������ ����
			// TatsunoN 10 : �����㰡 ��û
			P3_1_WorkingMessage p3WorkingMessage = (P3_1_WorkingMessage) workingMessage;

			DataStruct t10DS = new DataStruct();

			t10DS.addString("command", "10", 2);
			t10DS.addString("condition", "0", 1);
			t10DS.addString("preset", "0", 1);
			t10DS.addString("price", "000000", 6);
			t10DS.addString("flag", "2", 1);
			t10DS.addString("basePrice", p3WorkingMessage.getBasePrice()
					.substring(0, 4), 4);

			byte[] tempArray = t10DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, p3WorkingMessage
					.getNozzleNumber());

		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PB)) {
			// WorkingMessage PB : ���� / ���� ����
			// TatsunoN 10 : �����㰡 ��û
			PB_WorkingMessage pbWorkingMessage = (PB_WorkingMessage) workingMessage;

			String preset = pbWorkingMessage.getCommandSet();
			if (preset.equals("0")) {
				// PB WorkingMessage�� ���׼����� 0, �پ����� ���׼����� 2
				preset = "2";

			} // end inner if

			String basePrice = pbWorkingMessage.getBasePrice().substring(0, 4);
			String liter = pbWorkingMessage.getLiter();
			String price = pbWorkingMessage.getPrice();

			DataStruct t10DS = new DataStruct();

			t10DS.addString("command", "10", 2);
			t10DS.addString("condition", "2", 1); // ���� ��
			t10DS.addString("preset", preset, 1);

			if (preset.equals("1")) {
				// ���� ����
				// �پ���� ����
				t10DS.addString("liter", liter.substring(0, 6), 6);

			} else if (preset.equals("2")) {
				// ���� ����
				t10DS.addString("price", price.substring(2), 6);

			} else {
				LogUtility.getPumpALogger().error(
						"### PB WorkingMessage " + "preset ERROR. "
								+ "Current preset : "
								+ pbWorkingMessage.getCommandSet() + " ###");

			} // end inner if

			t10DS.addString("flag", "2", 1);
			t10DS.addString("basePrice", basePrice, 4);

			byte[] tempArray = t10DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, pbWorkingMessage
					.getNozzleNo());

		} else {
			LogUtility.getPumpALogger().error(
					"### Not Supported " + "command(" + workingMessageCommand
							+ ") in TransTokicoN ###");
			returnMessage = null;

		} // end if
		
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

	} // end generateByteArray

	
	/**
	 * ������ ���� ������ WorkingMessage�� ��ȯ�Ѵ�.
	 * 
	 * @param message	: ������ ���� ����
	 * @param command	: WorkingMessage Command
	 * @return			: WorkingMessage 
	 */
	public WorkingMessage generateWorkingMessage(byte[] message, String command) throws Exception {
		WorkingMessage returnMessage = null;

		// byte[]�� ����ڵ带 ��´�
		String tokicoCommandString = this.getCommand(message);
		// byte[]�� �����ȣ�� ��´�
		String nozzleNo = this.getNozzleNo(message);

		
		if (tokicoCommandString.equals("61")) {
			// TatsunoN 61 : �������� Status ����
			// WorkingMessage S3 : ����/���� �� �ڷ�����
			// WorkingMessage S8 : ������/������ ���� ����
			DataStruct t61Interface = TatsunoNDS.getDS("61");
			t61Interface.setByteStream(message);

			if (command.equals("S3")) {
				S3_WorkingMessage s3WorkingMessage = new S3_WorkingMessage();
				s3WorkingMessage.setNozzleNo(nozzleNo);
				// �پ���� ���� 
				s3WorkingMessage.setLiter(
						(String) t61Interface.getValue("liter") + "0");
				s3WorkingMessage.setPrice((String) t61Interface
						.getValue("price"));
				s3WorkingMessage.setBasePrice((String) t61Interface
						.getValue("basePrice")
						+ "00");
				s3WorkingMessage.setWDate(this.getSystemTime(6));

				returnMessage = s3WorkingMessage;

			} else if (command.equals("S8")) {
				S8_WorkingMessage s8WorkingMessage = new S8_WorkingMessage();
				s8WorkingMessage.setNozzleNo(nozzleNo);

				String status = (String) t61Interface.getValue("status");

				if (status.equals("0")) {
					// Status 0 : Nozzle Down, �ɸ� ����
					s8WorkingMessage.setStatusCode("651");

				} else if (status.equals("1")) {
					// Status 1 : Nozzle Up
					s8WorkingMessage.setStatusCode("652");

				} else if (status.equals("3")) {
					// Status 3 : ���� ��
					s8WorkingMessage.setStatusCode("653");

				} else if (status.equals("4")) {
					// Status 4 : ���� ���� (Nozzle Down)
					s8WorkingMessage.setStatusCode("654");

				} else {
					LogUtility.getPumpALogger().error(
							"###  ERROR : Incorrect "
									+ " status in '61' TokicoN "
									+ " protocol. Current mode : " + status
									+ " ###");

				}

				s8WorkingMessage.setDeviceType("01");
				s8WorkingMessage.setStatus("0");
				s8WorkingMessage.setErrMsg(this.generateBlank(20));
				s8WorkingMessage.setDetectTime(this.getSystemTime(12));
				s8WorkingMessage.setVersion(this.generateBlank(9));

				returnMessage = s8WorkingMessage;

			} else {
				LogUtility.getPumpALogger().error(
						"### Can't translate 61 -> " + command + " ###");

			} // end inner if

		} else if (tokicoCommandString.equals("65")) {
			// TatsunoN 65 : �������� �����ġ
			// WorkingMessage SJ : �������� �ڷ� ����
			// WorkingMessage S5 : Total Gauge ����
			DataStruct t65Interface = TatsunoNDS.getDS("65");
			t65Interface.setByteStream(message);
			String totalGauge = (String) t65Interface.getValue("liter");
			
			if (command.equals("SJ")) {
				SJ_WorkingMessage sjWorkingMessage = new SJ_WorkingMessage();

				sjWorkingMessage.setNozzleNo(nozzleNo);
				// ##### TransTatsunoN �� �ٸ� (�Ҽ��� �ڸ���) ####//
				// �پ��� ���� : 7.3, ������ ���� : 8.2
				sjWorkingMessage.setTotalGauge(totalGauge.substring(1, 10) + "0");
				sjWorkingMessage.setSystemTime(this.getSystemTime(12));

				returnMessage = sjWorkingMessage;

			} else if (command.equals("S5")) {
				S5_WorkingMessage s5WorkingMessage = new S5_WorkingMessage();

				s5WorkingMessage.setNozzleNo(nozzleNo);
				//##### TransTatsunoN �� �ٸ� (�Ҽ��� �ڸ���) ####//
				// �پ��� ���� : 7.3, ������ ���� : 8.2
				s5WorkingMessage.setTotalGauge(totalGauge.substring(1, 10) + "0");
				s5WorkingMessage.setSystemTime(this.getSystemTime(12));

				returnMessage = s5WorkingMessage;

			} else {
				LogUtility.getPumpALogger().error(
						"### Can't translate 65 -> " + command + " ###");

			} // end inner if

		} else {
			LogUtility.getPumpALogger().error(
					"### Not Supported " + "command(" + tokicoCommandString
							+ ") in TransTokicoN ###");
			returnMessage = null;

		} // end if
		
//		LogUtility.getPumpALogger().debug(this.printResultLog(message, returnMessage));

		return returnMessage;

	} // end generateWorkingMessage


	
	/**
	 * ������ ���� ������ S4 WorkingMessage�� ��ȯ�Ѵ�.
	 * 
	 * @param message	: ������ ���� ����
	 * @param command	: WorkingMessage Command
	 * @return			: WorkingMessage 
	 */
	public WorkingMessage generateWorkingMessage(byte[][] message,
			String command) throws Exception {
		WorkingMessage returnMessage = null;

		// TatsunoN 61 : �������� Status ����
		// TatsunoN 65 : �������� �����ġ
		// WorkingMessage S4 : ������ �����Ϸ� �ڷ�����
		S4_WorkingMessage s4WorkingMessage = new S4_WorkingMessage();

		s4WorkingMessage.setFlag("0"); // ���� ��
		s4WorkingMessage.setWDate(this.getSystemTime(6));
		s4WorkingMessage.setStatusFlag("0"); // ���� ��

		for (int i = 0; i < message.length; i++) {
			String tokicoCommandString = this.getCommand(message[i]);
			String nozzleNo = this.getNozzleNo(message[i]);
			s4WorkingMessage.setNozzleNo(nozzleNo);

			if (tokicoCommandString.equals("61")) {
				DataStruct t61Interface = TatsunoNDS.getDS("61");
				t61Interface.setByteStream(message[i]);
				//##### TransTatsunoN �� �ٸ� (�Ҽ��� �ڸ���) ####//
				s4WorkingMessage.setLiter(
						(String) t61Interface.getValue("liter") + "0");
				s4WorkingMessage.setBasePrice((String) t61Interface
						.getValue("basePrice")
						+ "00");
				s4WorkingMessage.setPrice("00"
						+ (String) t61Interface.getValue("price"));
				s4WorkingMessage.setSystemTime(this.getSystemTime(12));

			} else if (tokicoCommandString.equals("65")) {
				DataStruct t65Interface = TatsunoNDS.getDS("65");
				t65Interface.setByteStream(message[i]);
				
				String totalGauge = (String) t65Interface.getValue("liter");
				//##### TransTatsunoN �� �ٸ� (�Ҽ��� �ڸ���) ####//
				s4WorkingMessage.setTotalGauge(totalGauge.substring(1, 10) + "0");

			} else {

			} // end if

		} // end for

		returnMessage = s4WorkingMessage;

		return returnMessage;

	} // end generateWorkingMessage


	
	/**
	 * ������ ���� �������� ��� �ڵ带 ���� 
	 * 
	 * @param message	: ������ ���� ����
	 * @return			: ��� �ڵ� 
	 */
	private String getCommand(byte[] message) throws Exception {
		String returnMessage = null;

		byte[] commandArray = new byte[2];
		commandArray[0] = message[3];
		commandArray[1] = message[4];

		returnMessage = new String(commandArray);

		return returnMessage;

	} // end getCommand

	
	
	/**
	 * ������ ���� �������� �����ȣ�� ����
	 * 
	 * @param message	: ������ ���� ���� 
	 * @return			: ���� ��ȣ 
	 */
	private String getNozzleNo(byte[] message) throws Exception {
		String returnMessage = null;

		byte nozzleNo = (byte) (message[1] - 63);

		if (nozzleNo > 64 || nozzleNo < 1) {
			// ������ ��ȣ ���� üũ
			LogUtility.getPumpALogger()
					.error(
							"### Nozzle number(" + nozzleNo
									+ ") error in TokicoN ###");
			returnMessage = null;

		} else if (nozzleNo < 10) {
			// ���ڸ� �� ��ȯ
			returnMessage = "0" + String.valueOf(nozzleNo);

		} else {
			returnMessage = String.valueOf(nozzleNo);

		} // end if

		return returnMessage;

	} // end getNozzleNo

	
	
	/**
	 * byte �迭�� data��  ������ ������ ���� ���� ���·� ��ȯ
	 * 
	 * @param data		: data
	 * @param nozzleNo	: ���� ��ȣ 
	 * @return			: ������ ���� ���� 
	 */
	private byte[] makeProtocol(byte[] data, String nozzleNo) throws Exception {
		byte[] returnData = null;
		byte blank = ' ';
		int returnDataCounter = 0;
		byte nozzleByte = Byte.parseByte(nozzleNo);
		// STX, SA, UA, ETX, BCC ��ŭ�� ���̸� ���Ѵ�.
		int arrayLength = data.length + 5;

		returnData = new byte[arrayLength];

		returnData[returnDataCounter++] = Command.STX; // STX
		returnData[returnDataCounter++] = (byte) (nozzleByte + 0x3F); // SA
		returnData[returnDataCounter++] = blank; // UA

		for (int i = 0; i < data.length; i++) {
			returnData[returnDataCounter++] = data[i];

		} // end for

		returnData[returnDataCounter++] = Command.ETX; // ETX
		returnData[returnDataCounter++] = blank; // BCC

		return returnData;

	} // end makeProtocol

}
