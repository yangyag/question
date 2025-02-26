package com.gsc.kixxhub.device.pumpa.translation;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.P6_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SE_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.Command;
import com.gsc.kixxhub.device.pumpa.common.DataStruct;
import com.gsc.kixxhub.device.pumpa.devDatas.TatsunoSelfDS;

public class TransTatsunoSelf_MPP6 extends Translation {
	private final String BNA_ERROR 			= "272"; // �߰� dhp (2011/01/18)
	private final String CARD_ERROR 		= "271";
	private final String E_BNA_ERROR 		= "212"; // �߰� dhp (2011/01/18)
	private final String E_CARD_ERROR 		= "211";
	private final String E_PRINTER_CUT 		= "207";
	private final String E_PRINTER_MECHA 	= "205";
	private final String E_PRINTER_OPEN 	= "206";
	private final String E_SENSOR_ERROR 	= "210";
	private final String E_VOICE_BUSY 		= "208";
	private final String E_VOICE_ERROR 		= "209";
	private final String NORMAL				= "251";
	private final String PRINTER_CUT 		= "267";
	private final String PRINTER_MECHA 		= "265";
	private final String PRINTER_OPEN 		= "266";
	private final String SENSOR_ERROR 		= "270";
	private final String VOICE_BUSY 		= "268";
	private final String VOICE_ERROR 		= "269";
	
	
	/**
	 * WorkingMessage�� �پ��� ���� �������� ��ȯ�Ѵ�.
	 * 
	 * @param workingMessage 	: WorkingMessage
	 * @return					: �پ��� ���� ���� 
	 */
	public byte[] generateByteStream(WorkingMessage workingMessage) throws Exception {
		byte[] returnMessage = null;
		
		String workingMessageCommand = workingMessage.getCommand();
		
		
		if (workingMessageCommand.equals(IPumpConstant.COMMANDID_P6)) {
			// WorkingMessage P6 : ������ �� �ð� ����
			// TatsunoSelf 22    : �ð����� ���� 
			P6_WorkingMessage p6WorkingMessage = (P6_WorkingMessage) workingMessage;
			
			String date = p6WorkingMessage.getSystemTime();
			
			DataStruct ts22DS = new DataStruct();
			ts22DS.addString("command", "22", 2);
			ts22DS.addString("orderNo", "1", 1);
			ts22DS.addString("date", "20" + date, 14);
			
			byte[] tempArray = ts22DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, p6WorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PE)) {
			// WorkingMessage PE : ������ ���� ��û 
			// TatsunoSelf 41    : �������� �䱸 
			DataStruct ts41DS = new DataStruct();
			ts41DS.addString("command", "41", 2);
			ts41DS.addString("orderNo", "1", 1);
			
			byte[] tempArray = ts41DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, workingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_P5_1)) {
			// WorkingMessage P5_1 : ODT ȯ������ ���� 
			// SKIP
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PB)) {
			// WorkingMessage PB : ���� / ���� ���� 
			// ���� 
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_S3)) {
			// WorkingMessage S3 : ���� �� �ڷ����� 
			// ���� 
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_S4)) {
			// WorkingMessage S4 : �����Ϸ� �ڷ�����  
			// ���� 
			
		} else {
			LogUtility.getPumpALogger().error("### Not Supported " +
								"command(" + workingMessageCommand + ") " +
								"in TransTatsunoSelf ###");
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
	
	}	// end generateByteArray
	
	
	
	/**
	 * �پ��� ���� ������ WorkingMessage�� ��ȯ�Ѵ�.
	 * 
	 * @param message	: �پ��� ���� ����
	 * @param command	: WorkingMessage Command
	 * @return			: WorkingMessage 
	 */
	public WorkingMessage generateWorkingMessage(byte[] message, String command) throws Exception {
		WorkingMessage returnMessage = null;
		
		// byte[]�� ����ڵ带 ��´�
		String tatsunoCommandString = this.getCommand(message);
		// byte[]�� �����ȣ�� ��´�
		String nozzleNo = this.getNozzleNo(message);
		
		
		if (tatsunoCommandString.equals("81")) {
			// TatsunoSelf 81    : OPT ���� ����
			// WorkingMessage SE : ���� ����̽� �̻����� ����
			SE_WorkingMessage seWorkingMessage = new SE_WorkingMessage();
			seWorkingMessage.setNozzleNo(nozzleNo);

			DataStruct t81Interface = TatsunoSelfDS.getDS("81");
			t81Interface.setByteStream(message);
				
			// Device No 05 : ���� ������, 11 : ���� �ռ�, 15 : ������� ����, 16 : ī���ť��Ƽ SW
			String deviceNo  = (String) t81Interface.getValue("deviceNo");
			String errorCode = (String) t81Interface.getValue("errorCode");
			String status 	 = (String) t81Interface.getValue("status");
				
			seWorkingMessage.setDeviceType("05");
			seWorkingMessage.setStatus(status);
			seWorkingMessage.setErrMsg(this.generateBlank(20));
			
			
			if (status.equals("0")) {
				// status 0 : ����
				
				if (errorCode.equals("00")) {
					
					if (deviceNo.equals("13")) { // �߰� dhp (2011/01/18)
						// BNA ����
						seWorkingMessage.setStatusCode(BNA_ERROR);
						seWorkingMessage.setErrMsg("BNA �̻� - ����");
					}
					else {
						// ErrorCode 00 : ���� 
						seWorkingMessage.setStatusCode(NORMAL);
						seWorkingMessage.setErrMsg("����");
					}
					
				} else if (errorCode.equals("14")) {
					// ErrorCode 14 : ���� ������ ��ī ����
					seWorkingMessage.setStatusCode(PRINTER_MECHA);
					seWorkingMessage.setErrMsg("������ ��ī���� - ����");
					
				} else if (errorCode.equals("15")) {
					// ErrorCode 15 : ���� ������ ���µ��� 
					seWorkingMessage.setStatusCode(PRINTER_OPEN);
					seWorkingMessage.setErrMsg("������ ���µ��� - ����");
					
				} else if (errorCode.equals("16")) {
					// ErrorCode 16 : ���� ������ �����߶�� -> ��������
					seWorkingMessage.setStatusCode(PRINTER_CUT);
					seWorkingMessage.setErrMsg("������ ���� ���� - ����");
					
				} else if (errorCode.equals("10")) {
					// ErrorCode 10 : ���� �ռ� Busy ���� 
					seWorkingMessage.setStatusCode(VOICE_BUSY);
					seWorkingMessage.setErrMsg("������ġ Busy - ����");
					
				} else if (errorCode.equals("11")) {
					// ErrorCode 11(�̻�(���Ұ�))�� Device ����
					if (deviceNo.equals("11")) {
						// ���� �ռ� Device
						seWorkingMessage.setStatusCode(VOICE_ERROR);
						seWorkingMessage.setErrMsg("������ġ �̻� - ����");
						
					} else if (deviceNo.equals("15")) {
						// ������� ���� Device
						seWorkingMessage.setStatusCode(SENSOR_ERROR);
						seWorkingMessage.setErrMsg("������� ���� �̻� - ����");
						
					} else if (deviceNo.equals("16")) {
						// ī�� ��ť��Ƽ SW Device
						seWorkingMessage.setStatusCode(CARD_ERROR);
						seWorkingMessage.setErrMsg("ī�� ��ť��Ƽ SW �̻� - ����");
						
					} else {
						LogUtility.getPumpALogger().error(
								"# [TransTatsunoSelf] # " +
								"Can't set DeviceNo # Current DeviceNo : " + 
								deviceNo + " #");
						
					}
					
				} else {
					LogUtility.getPumpALogger().error(
							"# [TransTatsunoSelf]   # " +
							"Can't set error code # Current error code : " + 
							errorCode + " #");
					
				}	
				
			} else if (status.equals("1")) {
				// status 1 : ERROR �߻�
				
				if (errorCode.equals("00")) {
					// ErrorCode 00 : ���� 
					seWorkingMessage.setStatusCode("251");
					seWorkingMessage.setErrMsg("����");
					
				} else if (errorCode.equals("14")) {
					// ErrorCode 14 : ���� ������ ��ī ����
					seWorkingMessage.setStatusCode(E_PRINTER_MECHA);
					seWorkingMessage.setErrMsg("������ ��ī����");
					
				} else if (errorCode.equals("15")) {
					// ErrorCode 15 : ���� ������ ���µ��� 
					seWorkingMessage.setStatusCode(E_PRINTER_OPEN);
					seWorkingMessage.setErrMsg("������ ���µ���");
					
				} else if (errorCode.equals("16")) {
					// ErrorCode 16 : ���� ������ �����߶�� -> ��������
					seWorkingMessage.setStatusCode(E_PRINTER_CUT);
					seWorkingMessage.setErrMsg("������ ���� ����");
					
				} else if (errorCode.equals("10")) {
					// ErrorCode 10 : ���� �ռ� Busy ���� 
					seWorkingMessage.setStatusCode(E_VOICE_BUSY);
					seWorkingMessage.setErrMsg("������ġ Busy");
					
				} else if (errorCode.equals("11")) {
					// ErrorCode 11(�̻�(���Ұ�))�� Device ����
					if (deviceNo.equals("11")) {
						// ���� �ռ� Device
						seWorkingMessage.setStatusCode(E_VOICE_ERROR);
						seWorkingMessage.setErrMsg("������ġ �̻�");
						
					} else if (deviceNo.equals("15")) {
						// ������� ���� Device
						seWorkingMessage.setStatusCode(E_SENSOR_ERROR);
						seWorkingMessage.setErrMsg("������� ���� �̻�");
						
					} else if (deviceNo.equals("16")) {
						// ī�� ��ť��Ƽ SW Device
						seWorkingMessage.setStatusCode(E_CARD_ERROR);
						seWorkingMessage.setErrMsg("ī�� ��ť��Ƽ SW �̻�");
						
					} else if (deviceNo.equals("13")) { // �߰� dhp (2011/01/18)
						// BNA �̻�
						seWorkingMessage.setStatusCode(E_BNA_ERROR);
						seWorkingMessage.setErrMsg("BNA �̻�");
						
					}else {
						LogUtility.getPumpALogger().error(
								"# [TransTatsunoSelf] # " +
								"Can't set DeviceNo # Current DeviceNo : " + 
								deviceNo + " #");
						
					}
					
				} else {
					LogUtility.getPumpALogger().error(
							"# [TransTatsunoSelf]   # " +
							"Can't set error code # Current error code : " + 
							errorCode + " #");
					
				}	
				
			} else {
				LogUtility.getPumpALogger().error(
						"# [TransTatsunoSelf]   # " +
						"Status Error.        # " +
						"Current status : " + status + " #");
				
			}	// end inner if
				
				
			seWorkingMessage.setDetectTime(this.getSystemTime(12));
			seWorkingMessage.setVersion(this.generateBlank(9));
				
			returnMessage = seWorkingMessage;
			
		} else {
			LogUtility.getPumpALogger().error("### Not Supported " +
										"command(" + tatsunoCommandString +
										") in TransTatsunoSelf ###");
			returnMessage = null;
			
		}	// end if
			
//		LogUtility.getPumpALogger().debug(this.printResultLog(message, returnMessage));
		
		return returnMessage;
	
	}	// end generateWorkingMessage
	

	
	/**
	 * �پ��� ���� �������� ��� �ڵ带 ���� 
	 * 
	 * @param message	: �پ��� ���� ����
	 * @return			: ��� �ڵ� 
	 */
	private String getCommand(byte[] message) throws Exception {
		String returnMessage = null;
		
		byte[] commandArray = new byte[2];
		commandArray[0] = message[3];
		commandArray[1] = message[4];
		
		returnMessage = new String(commandArray);
		
		return returnMessage;
	
	}	// end getCommand

	

	/**
	 * �پ��� ���� �������� ODT ��ȣ�� ����
	 * 
	 * @param message	: ODT ���� 
	 * @return			: ODT ��ȣ 
	 */
	private String getNozzleNo(byte[] message) throws Exception {
		String returnMessage = null;
		
		byte nozzleNo = (byte) (message[1] - 63);
		
		if (nozzleNo > 64 || nozzleNo < 1) {
			// ������ ��ȣ ���� üũ 
			LogUtility.getPumpALogger().error("### Nozzle number error " +
											 "in TransSomoSelf. " +
											 "Current nozzleNo : " + 
											 nozzleNo + " ###");
			returnMessage = null;
			
		}	// end if 
		
		if (nozzleNo < 10){
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
		byte nozzleByte 		= Byte.parseByte(nozzleNo);
		
		// STX, SA, UA, ETX, BCC ��ŭ�� ���̸� ���Ѵ�.
		int arrayLength = data.length + 5;
		
		returnData = new byte[arrayLength];
		
		returnData[returnDataCounter++] = Command.STX;					// STX
		returnData[returnDataCounter++] = (byte) (nozzleByte + 0x3F);	// SA
		returnData[returnDataCounter++] = blank;						// UA
		
		for (int i = 0; i < data.length; i++) {
			returnData[returnDataCounter++] = data[i];
			
		}	// end for
		
		returnData[returnDataCounter++] = Command.ETX;					// ETX
		returnData[returnDataCounter++] = blank;						// BCC		
		
		return returnData;
		
	}	// end makeProtocol
	
}
