package com.gsc.kixxhub.device.pumpa.translation;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.P3_1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S3_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S4_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S5_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S8_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SJ_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.Change;
import com.gsc.kixxhub.device.pumpa.common.DataStruct;
import com.gsc.kixxhub.device.pumpa.devDatas.TatsunoDS;

/**
 * TATSUNO ���� PROTOCOL �ڸ� ��
 * LITER = 6.4 
 * PRICE = 6.4
 * 
 * 
 * 
 * @author yd
 *
 */
public class TransTatsuno extends Translation {

	/**
	 * String Ÿ�� �����͸� Packed BCD ��� byte �迭 Ÿ������ ��ȯ
	 * 
	 * @param data	: String Ÿ�� ������ 
	 * @return		: Packed BCD ��� byte �迭
	 */
	private byte[] bcdToArray(String data) throws Exception {
		if (data.length() > 10) {
			LogUtility.getPumpALogger().error("### String length over ###");
			return null;
		}
		
		byte[] returnMessage 	= new byte[5];
		byte[] tempArray 		= new byte[10];
		
		int start = 10 - data.length();
		
		// String -> byte[10]
		for (int i = start, j = 0; i < tempArray.length; i++, j++) {
			byte temp 		= (byte) data.charAt(j);
			tempArray[i] 	= (byte) (temp - 48);
		
		}	// end for
		
		// byte[10] -> byte[5]
		for (int i = 0; i < returnMessage.length; i++) {
			byte upper  	= tempArray[i*2];
			upper 			= (byte) (upper << 4);
			upper 			= (byte) (upper & 240);
			byte downer 	= tempArray[i*2 + 1];
			downer 			= (byte) (downer & 15);
			
			returnMessage[i] = Change.getUnsignedByte((byte) (upper | downer));
			
		}	// end for

		return returnMessage;
		
	}	// end bcdToArray
	
	
	/**
	 * Packed BCD ��� byte �迭 Ÿ�� �����͸� String ���·� ��ȯ
	 * 
	 * @param data		: byte �迭 Ÿ�� ������
	 * @param length	: ��ȯ�Ǵ� String�� ����. ���ʺ��� ���̸� ���δ�.
	 * @return
	 */
	private String bcdToString(byte[] data, int length) throws Exception {
		StringBuffer returnMessage = new StringBuffer();
			
		for (int i = 0; i < data.length; i++) {
			byte upper 	= (byte) (data[i] >> 4);
			upper 		= (byte) (upper & 15);
			returnMessage.append(String.valueOf(upper));
				
			byte downer = (byte) (data[i] & 15);
			returnMessage.append(String.valueOf(downer));
		}	// end for

		// �ʿ��� ���̸�ŭ �߶󳻱� 
		int cutSize = returnMessage.length() - length;
		returnMessage.delete(0, cutSize);
		
		return returnMessage.toString();
	}	// end bcdToString
	
	
	
	/**
	 * WorkingMessage�� �پ��� ���� �������� ��ȯ�Ѵ�.
	 * 
	 * @param workingMessage 	: WorkingMessage
	 * @return					: �پ��� ���� ���� 
	 */
	public byte[] generateByteStream(WorkingMessage workingMessage) throws Exception {
		byte[] returnMessage = null;
		
		String workingMessageCommand = workingMessage.getCommand();
		
		
		if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PB)) {
			// WorkingMessage PB 		: ���� / ���� ����
			// Tatsuno 0x02 (S/C->Pump) : Preset data �� �ܰ����� 
			PB_WorkingMessage pbWorkingMessage = 
				(PB_WorkingMessage) workingMessage;
			
			String commandSet 	= pbWorkingMessage.getCommandSet();
			// WorkingMessage Liter ǥ�� 4.3 / �پ��� Liter ǥ�� 6.4
			byte[] liter 		= this.bcdToArray("00" + pbWorkingMessage.getLiter() + "0");
			// WorkingMessage Price ǥ�� 8 / �پ��� Price ǥ�� 6.4
			byte[] price 		= this.bcdToArray(pbWorkingMessage.getPrice().substring(2) + "0000");
			// WorkingMessage BasePrice ǥ�� 4.2 / �پ��� BasePrice ǥ�� 6.4
			byte[] basePrice 	= this.bcdToArray(
								"00" + pbWorkingMessage.getBasePrice() + "00");
			
			DataStruct t02DS = new DataStruct();
			t02DS.addByte("command", (byte) 0x02);
			
			if (commandSet.equals("0")) {
				// PB Commandset 0 : ���� ����
				for (int i = 0; i < liter.length; i++) {
					t02DS.addByte("liter" + i, (byte) 0);
					
				}	// end for

				for (int i = 0; i < basePrice.length; i++) {
					t02DS.addByte("basePrice" + i, basePrice[i]);
					
				}	// end for
			
				for (int i = 0; i < price.length; i++) {
					t02DS.addByte("price" + i, price[i]);
					
				}	// end for

			} else if (commandSet.equals("1")) {
				// PB Commandset 1 : ���� ����
				for (int i = 0; i < liter.length; i++) {
					t02DS.addByte("liter" + i, liter[i]);
					
				}	// end for
				
				for (int i = 0; i < basePrice.length; i++) {
					t02DS.addByte("basePrice" + i, basePrice[i]);
					
				}	// end for
			
				for (int i = 0; i < price.length; i++) {
					t02DS.addByte("price" + i, (byte) 0);
					
				}	// end for
			
			} else {
				LogUtility.getPumpALogger().error("###  ERROR : Incorrect " +
												"commandSet in PB " +
												"WorkingMessage." +
												"Current commandSet : " +
												commandSet + " ###");
		
			}	// end inner if	
			
			byte[] tempArray = t02DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
					pbWorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_P3_1)) {
			// WorkingMessage P3 		: ������ ȯ������ ���� 			
			// Tatsuno 0x02 (S/C->Pump) : Preset data �� �ܰ����� 
			P3_1_WorkingMessage p3WorkingMessage = 
				(P3_1_WorkingMessage) workingMessage;
			// WorkingMessage BasePrice ǥ�� 4.2 / �پ��� BasePrice ǥ�� 6.4
			byte[] basePrice = this.bcdToArray("00" + p3WorkingMessage.getBasePrice() + "00");
			
			DataStruct t02DS = new DataStruct();
			t02DS.addByte("command", (byte) 0x02);
			
			for (int i = 0; i < 5; i++) {
				t02DS.addByte("liter" + i, (byte) 0);
			}

			for (int i = 0; i < basePrice.length; i++) {
				t02DS.addByte("basePrice" + i, basePrice[i]);
			}
			
			for (int i = 0; i < 5; i++) {
				t02DS.addByte("price" + i, (byte) 0);
			}

			byte[] tempArray = t02DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
					p3WorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_P8)) {
			// WorkingMessage P8 		: ���а����� �ڷ� ��û			
			// Tatsuno 0x20 (S/C->PUMP) : Total Gauge �䱸 
			returnMessage = new byte[2];
			returnMessage[0] = 0x20;
			returnMessage[1] = 0x00;
			
			returnMessage = this.makeProtocol(returnMessage, 
					workingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_SI)) {
			// WorkingMessage SI 		: ���������ڷ� ��û
			// Tatsuno 0x20 (S/C->PUMP) : Total Gauge �䱸 
			returnMessage = new byte[2];
			returnMessage[0] = 0x20;
			returnMessage[1] = 0x00;
			
			returnMessage = this.makeProtocol(returnMessage, 
					workingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PE)) {
			// WorkingMessage PE : ������ / ������ ���� ��û
			// Tatsuno 0x05      : ���� �������� �� �跮�� �������� �䱸 
			DataStruct t05DS = new DataStruct();
			t05DS.addByte("command", (byte) 0x05);
			t05DS.addString("Mode", "0", 1);	// ���� �� 
			t05DS.addString("PLK1", "0", 1);	// ���� �� 
			t05DS.addString("PLK2", "0", 1);	// ���� �� 
						
			byte[] tempArray = t05DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
					workingMessage.getNozzleNo());
			
		} else {
			LogUtility.getPumpALogger().error("### Not Supported " +
										"command(" + workingMessageCommand +
			 							") in TransTatsuno ###");
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
	 * �پ��� ���� ������ WorkingMessage�� ��ȯ�Ѵ�.
	 * 
	 * @param message	: �پ��� ���� ����
	 * @param command	: WorkingMessage Command
	 * @return			: WorkingMessage 
	 */
	public WorkingMessage generateWorkingMessage(byte[] message, 
													String command) throws Exception {
		WorkingMessage returnMessage = null;
		
		// byte[]�� �ִ� ����ڵ带 ��´�   
		// 05, 06 ����ڵ�� message[0]���� ���� �Ѵ�.
		byte tatsunoCommand 	= message[2];
		// byte[]�� �����ȣ�� ��´�
		String nozzleNo 		= this.getNozzleNo(message);
		
		
		if (tatsunoCommand == 0x00) {
			// Tatsuno 0x00 (Pump->S/C) : ���������� 
			// WorkingMessage S3 		: ����/���� �� �ڷ�����
			S3_WorkingMessage s3WorkingMessage = new S3_WorkingMessage();
			s3WorkingMessage.setNozzleNo(nozzleNo);

			DataStruct t00Interface = 
				TatsunoDS.getDS("00H", TatsunoDS.FROM_PUMP);
			t00Interface.setByteStream(message);
			
			byte[] liter = {(Byte) t00Interface.getValue("liter0"),
							(Byte) t00Interface.getValue("liter1"),
							(Byte) t00Interface.getValue("liter2"),
							(Byte) t00Interface.getValue("liter3"),
							(Byte) t00Interface.getValue("liter4")};
			byte[] price = {(Byte) t00Interface.getValue("price0"),
							(Byte) t00Interface.getValue("price1"),
							(Byte) t00Interface.getValue("price2"),
							(Byte) t00Interface.getValue("price3"),
							(Byte) t00Interface.getValue("price4")};
			byte[] basePrice = {(Byte) t00Interface.getValue("basePrice0"),
							(Byte) t00Interface.getValue("basePrice1"),
							(Byte) t00Interface.getValue("basePrice2"),
							(Byte) t00Interface.getValue("basePrice3"),
							(Byte) t00Interface.getValue("basePrice4")};

			// �پ��� Liter ǥ�� 6.4 / WorkingMessage Liter ǥ�� 4.3
			s3WorkingMessage.setLiter(this.bcdToString(liter, 8).substring(0, 7));
			// �پ��� Price ǥ�� 6.4 / WorkingMessage Price ǥ�� 6
			s3WorkingMessage.setPrice(this.bcdToString(price, 10).substring(0, 6));
			// �پ��� BasePrice ǥ�� 6.4 / WorkingMessage BasePrice ǥ�� 4.2
			s3WorkingMessage.setBasePrice(this.bcdToString(basePrice, 8).substring(0, 6));
			s3WorkingMessage.setWDate(this.getSystemTime(6));
				
			returnMessage = s3WorkingMessage;
				
		} else if (tatsunoCommand == 0x20) {
			// Tatsuno 0x20 (Pump->S/C) : Total Gauge Data �۽�
			// WorkingMessage SJ 		: �������� �ڷ� ����
			// WorkingMessage S5 		: Total Gauge ����
			if (command.equals("SJ")) {
				SJ_WorkingMessage sjWorkingMessage = new SJ_WorkingMessage();
				sjWorkingMessage.setNozzleNo(nozzleNo);
				
				DataStruct t20Interface = TatsunoDS.getDS("20H",
						TatsunoDS.FROM_PUMP);
				t20Interface.setByteStream(message);

				byte[] liter = { (Byte) t20Interface.getValue("liter0"),
						(Byte) t20Interface.getValue("liter1"),
						(Byte) t20Interface.getValue("liter2"),
						(Byte) t20Interface.getValue("liter3"),
						(Byte) t20Interface.getValue("liter4") };
				// �پ��� TotalGauge ǥ�� 8.2 / WorkingMessage TotalGauge ǥ�� 7.3
				sjWorkingMessage.setTotalGauge(this.bcdToString(liter, 10).substring(1) + "0");
				sjWorkingMessage.setSystemTime(this.getSystemTime(12));

				returnMessage = sjWorkingMessage;
				
			} else if (command.equals("S5")) {
				S5_WorkingMessage s5WorkingMessage = new S5_WorkingMessage();
				s5WorkingMessage.setNozzleNo(nozzleNo);

				DataStruct t20Interface = TatsunoDS.getDS("20H",
						TatsunoDS.FROM_PUMP);
				t20Interface.setByteStream(message);

				byte[] liter = { (Byte) t20Interface.getValue("liter0"),
						(Byte) t20Interface.getValue("liter1"),
						(Byte) t20Interface.getValue("liter2"),
						(Byte) t20Interface.getValue("liter3"),
						(Byte) t20Interface.getValue("liter4") };
				// �پ��� TotalGauge ǥ�� 8.2 / WorkingMessage TotalGauge ǥ�� 7.3
				s5WorkingMessage.setTotalGauge(this.bcdToString(liter, 10).substring(1) + "0");
				s5WorkingMessage.setSystemTime(this.getSystemTime(12));

				returnMessage = s5WorkingMessage;
				
			} else {
				LogUtility.getPumpALogger().error("### Can't translate 0x20 -> " + 
												  command + " ###");
				
			}	// end inner if
			
		} else if (message[0] == 0x06) {
			// Tatsuno 0x06      : �跮�� Status
			// WorkingMessage S8 : ������/������ ���� ����
			S8_WorkingMessage s8WorkingMessage = new S8_WorkingMessage();
			s8WorkingMessage.setNozzleNo(nozzleNo);

			DataStruct t06Interface = 
				TatsunoDS.getDS("06H", TatsunoDS.FROM_PUMP);
			t06Interface.setByteStream(message);
			// ��������, ������, Nozzle up/Down, In/Off line Ȯ�� 
			byte dst = ((Byte) t06Interface.getValue("dst"));
			
			if ((dst & 8) > 0) {
				// �����Ϸ�
				s8WorkingMessage.setStatusCode("654");
				
			} else if ((dst & 4) > 0) {
				// ���� ��
				s8WorkingMessage.setStatusCode("653");
				
			} else if ((dst & 2) > 0) {
				// ���� ��
				s8WorkingMessage.setStatusCode("652");
				
			} else if ((dst & 1) > 0) {
				// ��� 
				s8WorkingMessage.setStatusCode("651");
				
			} else {
				LogUtility.getPumpALogger().error("### Incorrect dst " +
												 "in Tatsuno 06H. " +
												 "Current dst : " +
												 dst + " ###");
					
			}	// end inner if
				
			s8WorkingMessage.setDeviceType("01");
			s8WorkingMessage.setStatus("0");
			s8WorkingMessage.setErrMsg(this.generateBlank(20));
			s8WorkingMessage.setDetectTime(this.getSystemTime(12));
			s8WorkingMessage.setVersion(this.generateBlank(9));
				
			returnMessage = s8WorkingMessage;
				
		} else {
			LogUtility.getPumpALogger().error("### Not Supported command(" + 
								tatsunoCommand + ") in TransTatsuno ###");
			returnMessage = null;
		
		}	// end if
		
//		LogUtility.getPumpALogger().debug(this.printResultLog(message, returnMessage));
		
		return returnMessage;

	}	// end generateWorkingMessage
	

	
	/**
	 * �پ��� ���� ������ S4 WorkingMessage�� ��ȯ�Ѵ�.
	 * 
	 * @param message	: �پ��� ���� ����. 
	 * 					  0x00, 0x20 ������ �ݵ�� ���ԵǾ�� �Ѵ�.
	 * @param command	: WorkingMessage Command
	 * @return			: WorkingMessage 
	 */
	public WorkingMessage generateWorkingMessage(byte[][] message, String command) throws Exception {
		WorkingMessage returnMessage = null;
	
		// Tatsuno 0x00 (Pump->S/C) : ���������� 
		// Tatsuno 0x20 (Pump->S/C) : Total Gauge Data �۽� 
		// WorkingMessage S4 		: ������ �����Ϸ� �ڷ�����
		S4_WorkingMessage s4WorkingMessage = new S4_WorkingMessage();
			
		s4WorkingMessage.setFlag("0");					// �÷��� ���� ��
		s4WorkingMessage.setWDate(this.getSystemTime(6));
		s4WorkingMessage.setStatusFlag("0");				// Status Flag ���� ��
			
		for (int i = 0; i < message.length; i++) {
			byte tatsunoCommand 	= message[i][2];
			String nozzleNo 		= this.getNozzleNo(message[i]);
			s4WorkingMessage.setNozzleNo(nozzleNo);
				
			if (tatsunoCommand == 0x00) {
				DataStruct t00Interface = 
					TatsunoDS.getDS("00H", TatsunoDS.FROM_PUMP);
				t00Interface.setByteStream(message[i]);
					
				byte[] liter 	 = {(Byte) t00Interface.getValue("liter0"),
									(Byte) t00Interface.getValue("liter1"),
									(Byte) t00Interface.getValue("liter2"),
									(Byte) t00Interface.getValue("liter3"),
									(Byte) t00Interface.getValue("liter4")};
				byte[] price     = {(Byte) t00Interface.getValue("price0"),
									(Byte) t00Interface.getValue("price1"),
									(Byte) t00Interface.getValue("price2"),
									(Byte) t00Interface.getValue("price3"),
									(Byte) t00Interface.getValue("price4")};
				byte[] basePrice = {(Byte) t00Interface.getValue("basePrice0"),
									(Byte) t00Interface.getValue("basePrice1"),
									(Byte) t00Interface.getValue("basePrice2"),
									(Byte) t00Interface.getValue("basePrice3"),
									(Byte) t00Interface.getValue("basePrice4")};
					
				
				// �پ��� Liter ǥ�� 6.4 / WorkingMessage Liter ǥ�� 4.3
				s4WorkingMessage.setLiter(this.bcdToString(liter, 8).substring(0, 7));
				// �پ��� Price ǥ�� 6.4 / WorkingMessage Price ǥ�� 8
				s4WorkingMessage.setPrice("00" + this.bcdToString(price, 10).substring(0, 6));
				// �پ��� BasePrice ǥ�� 6.4 / WorkingMessage BasePrice ǥ�� 4.2
				s4WorkingMessage.setBasePrice(this.bcdToString(basePrice, 8).substring(0, 6));
				
				
				s4WorkingMessage.setSystemTime(this.getSystemTime(12));
					
			} else if (tatsunoCommand == 0x20) {
				DataStruct t20Interface = 
					TatsunoDS.getDS("20H", TatsunoDS.FROM_PUMP);
				t20Interface.setByteStream(message[i]);
					
				byte[] liter = {(Byte) t20Interface.getValue("liter0"),
								(Byte) t20Interface.getValue("liter1"),
								(Byte) t20Interface.getValue("liter2"),
								(Byte) t20Interface.getValue("liter3"),
								(Byte) t20Interface.getValue("liter4")};
					
				s4WorkingMessage.setTotalGauge(this.bcdToString(liter, 10).substring(1) + "0");
					
			} else {

			}	// end inner if				
	
		}	// end for			

		returnMessage = s4WorkingMessage;
		
		return returnMessage;
	
	}	// end generateWorkingMessage
	

	
	/**
	 * �پ��� ���� �������� �����ȣ�� ����
	 * 
	 * @param message	: �پ��� ���� ���� 
	 * @return			: ���� ��ȣ 
	 */
	private String getNozzleNo(byte[] message)  throws Exception {
		String returnMessage = null;
		
		byte nozzleNo = (byte) (message[1] + 1);
		
		if (nozzleNo > 16 || nozzleNo < 1) {
			// ������ ��ȣ ���� üũ 
			LogUtility.getPumpALogger().error("### Nozzle number error " +
											 "in Tatsuno. " +
											 "Current nozzle number : " +
											 nozzleNo + " ###");
			returnMessage = null;
			
		} else if (nozzleNo <10){
			// ���ڸ� �� ��ȯ 
			returnMessage = "0" + String.valueOf(nozzleNo);
			
		} else {
			returnMessage = String.valueOf(nozzleNo);

		}	// end if
		
		return returnMessage;
		
	}	// end getNozzleNo
	
	
	
	/**
	 * byte �迭�� data��  ������ �پ��� ���� ���� ���·� ��ȯ
	 * 
	 * @param data		: data
	 * @param nozzleNo	: ���� ��ȣ 
	 * @return			: �پ��� ���� ���� 
	 */
	private byte[] makeProtocol(byte[] data, String nozzleNo) throws Exception {
		byte[] returnData 		= null;
		byte blank 				= ' ';
		int returnDataCounter 	= 0;
		byte command 			= data[0];
		byte nozzleByte 		= Byte.parseByte(nozzleNo);
		
		// ENQ, ACK ����
		if (command == 0x05 || command == 0x06) {
			int arrayLength = data.length + 2;
			
			returnData = new byte[arrayLength];
			
			returnData[returnDataCounter++] = command;	
			returnData[returnDataCounter++] = (byte) (nozzleByte - 1);	// UA
			
			for (int i = 1; i < data.length; i++) {
				returnData[returnDataCounter++] = data[i];
				
			}	// end for
			
			returnData[returnDataCounter++] = blank;					// LRC
			
		} else {
			int arrayLength = data.length + 3;

			returnData = new byte[arrayLength];
			
			returnData[returnDataCounter++] = Change.getUnsignedByte((byte) 0x82) ;	// STX	
			returnData[returnDataCounter++] = (byte) (nozzleByte - 1);	// UA
			
			for (int i = 0; i < data.length; i++) {
				returnData[returnDataCounter++] = data[i];
				
			}	// end for
			
			returnData[returnDataCounter++] = blank;					// LRC
		
		}	// end if
		
		return returnData;
		
	}	// end makeProtocol
	
}
