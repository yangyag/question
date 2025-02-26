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
	private final String BNA_ERROR 			= "272"; // 추가 dhp (2011/01/18)
	private final String CARD_ERROR 		= "271";
	private final String E_BNA_ERROR 		= "212"; // 추가 dhp (2011/01/18)
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
	 * WorkingMessage를 다쓰노 셀프 전문으로 변환한다.
	 * 
	 * @param workingMessage 	: WorkingMessage
	 * @return					: 다쓰노 셀프 전문 
	 */
	public byte[] generateByteStream(WorkingMessage workingMessage) throws Exception {
		byte[] returnMessage = null;
		
		String workingMessageCommand = workingMessage.getCommand();
		
		
		if (workingMessageCommand.equals(IPumpConstant.COMMANDID_P6)) {
			// WorkingMessage P6 : 영업일 및 시간 설정
			// TatsunoSelf 22    : 시각설정 지시 
			P6_WorkingMessage p6WorkingMessage = (P6_WorkingMessage) workingMessage;
			
			String date = p6WorkingMessage.getSystemTime();
			
			DataStruct ts22DS = new DataStruct();
			ts22DS.addString("command", "22", 2);
			ts22DS.addString("orderNo", "1", 1);
			ts22DS.addString("date", "20" + date, 14);
			
			byte[] tempArray = ts22DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, p6WorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PE)) {
			// WorkingMessage PE : 충전기 상태 요청 
			// TatsunoSelf 41    : 상태통지 요구 
			DataStruct ts41DS = new DataStruct();
			ts41DS.addString("command", "41", 2);
			ts41DS.addString("orderNo", "1", 1);
			
			byte[] tempArray = ts41DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, workingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_P5_1)) {
			// WorkingMessage P5_1 : ODT 환경정보 설정 
			// SKIP
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PB)) {
			// WorkingMessage PB : 정액 / 정량 설정 
			// 보류 
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_S3)) {
			// WorkingMessage S3 : 주유 중 자료전송 
			// 보류 
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_S4)) {
			// WorkingMessage S4 : 주유완료 자료전송  
			// 보류 
			
		} else {
			LogUtility.getPumpALogger().error("### Not Supported " +
								"command(" + workingMessageCommand + ") " +
								"in TransTatsunoSelf ###");
			returnMessage = null;
			
		}	// end if
		
		// 로그 출력 
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
	 * 다쓰노 셀프 전문을 WorkingMessage로 변환한다.
	 * 
	 * @param message	: 다쓰노 셀프 전문
	 * @param command	: WorkingMessage Command
	 * @return			: WorkingMessage 
	 */
	public WorkingMessage generateWorkingMessage(byte[] message, String command) throws Exception {
		WorkingMessage returnMessage = null;
		
		// byte[]의 명령코드를 얻는다
		String tatsunoCommandString = this.getCommand(message);
		// byte[]의 노즐번호를 얻는다
		String nozzleNo = this.getNozzleNo(message);
		
		
		if (tatsunoCommandString.equals("81")) {
			// TatsunoSelf 81    : OPT 에러 통지
			// WorkingMessage SE : 주유 디바이스 이상정보 전송
			SE_WorkingMessage seWorkingMessage = new SE_WorkingMessage();
			seWorkingMessage.setNozzleNo(nozzleNo);

			DataStruct t81Interface = TatsunoSelfDS.getDS("81");
			t81Interface.setByteStream(message);
				
			// Device No 05 : 내장 프린터, 11 : 음성 합성, 15 : 사람검지 센서, 16 : 카드시큐리티 SW
			String deviceNo  = (String) t81Interface.getValue("deviceNo");
			String errorCode = (String) t81Interface.getValue("errorCode");
			String status 	 = (String) t81Interface.getValue("status");
				
			seWorkingMessage.setDeviceType("05");
			seWorkingMessage.setStatus(status);
			seWorkingMessage.setErrMsg(this.generateBlank(20));
			
			
			if (status.equals("0")) {
				// status 0 : 정상
				
				if (errorCode.equals("00")) {
					
					if (deviceNo.equals("13")) { // 추가 dhp (2011/01/18)
						// BNA 정상
						seWorkingMessage.setStatusCode(BNA_ERROR);
						seWorkingMessage.setErrMsg("BNA 이상 - 정상");
					}
					else {
						// ErrorCode 00 : 정상 
						seWorkingMessage.setStatusCode(NORMAL);
						seWorkingMessage.setErrMsg("정상");
					}
					
				} else if (errorCode.equals("14")) {
					// ErrorCode 14 : 내장 프린터 메카 에러
					seWorkingMessage.setStatusCode(PRINTER_MECHA);
					seWorkingMessage.setErrMsg("프린터 메카에러 - 정상");
					
				} else if (errorCode.equals("15")) {
					// ErrorCode 15 : 내장 프린터 오픈도어 
					seWorkingMessage.setStatusCode(PRINTER_OPEN);
					seWorkingMessage.setErrMsg("프린터 오픈도어 - 정상");
					
				} else if (errorCode.equals("16")) {
					// ErrorCode 16 : 내장 프린터 용지잘라라 -> 용지없음
					seWorkingMessage.setStatusCode(PRINTER_CUT);
					seWorkingMessage.setErrMsg("프린터 용지 없음 - 정상");
					
				} else if (errorCode.equals("10")) {
					// ErrorCode 10 : 음성 합성 Busy 상태 
					seWorkingMessage.setStatusCode(VOICE_BUSY);
					seWorkingMessage.setErrMsg("음성장치 Busy - 정상");
					
				} else if (errorCode.equals("11")) {
					// ErrorCode 11(이상(사용불가))는 Device 구분
					if (deviceNo.equals("11")) {
						// 음성 합성 Device
						seWorkingMessage.setStatusCode(VOICE_ERROR);
						seWorkingMessage.setErrMsg("음성장치 이상 - 정상");
						
					} else if (deviceNo.equals("15")) {
						// 사람검지 센서 Device
						seWorkingMessage.setStatusCode(SENSOR_ERROR);
						seWorkingMessage.setErrMsg("사람검지 센서 이상 - 정상");
						
					} else if (deviceNo.equals("16")) {
						// 카드 시큐리티 SW Device
						seWorkingMessage.setStatusCode(CARD_ERROR);
						seWorkingMessage.setErrMsg("카드 시큐리티 SW 이상 - 정상");
						
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
				// status 1 : ERROR 발생
				
				if (errorCode.equals("00")) {
					// ErrorCode 00 : 정상 
					seWorkingMessage.setStatusCode("251");
					seWorkingMessage.setErrMsg("정상");
					
				} else if (errorCode.equals("14")) {
					// ErrorCode 14 : 내장 프린터 메카 에러
					seWorkingMessage.setStatusCode(E_PRINTER_MECHA);
					seWorkingMessage.setErrMsg("프린터 메카에러");
					
				} else if (errorCode.equals("15")) {
					// ErrorCode 15 : 내장 프린터 오픈도어 
					seWorkingMessage.setStatusCode(E_PRINTER_OPEN);
					seWorkingMessage.setErrMsg("프린터 오픈도어");
					
				} else if (errorCode.equals("16")) {
					// ErrorCode 16 : 내장 프린터 용지잘라라 -> 용지없음
					seWorkingMessage.setStatusCode(E_PRINTER_CUT);
					seWorkingMessage.setErrMsg("프린터 용지 없음");
					
				} else if (errorCode.equals("10")) {
					// ErrorCode 10 : 음성 합성 Busy 상태 
					seWorkingMessage.setStatusCode(E_VOICE_BUSY);
					seWorkingMessage.setErrMsg("음성장치 Busy");
					
				} else if (errorCode.equals("11")) {
					// ErrorCode 11(이상(사용불가))는 Device 구분
					if (deviceNo.equals("11")) {
						// 음성 합성 Device
						seWorkingMessage.setStatusCode(E_VOICE_ERROR);
						seWorkingMessage.setErrMsg("음성장치 이상");
						
					} else if (deviceNo.equals("15")) {
						// 사람검지 센서 Device
						seWorkingMessage.setStatusCode(E_SENSOR_ERROR);
						seWorkingMessage.setErrMsg("사람검지 센서 이상");
						
					} else if (deviceNo.equals("16")) {
						// 카드 시큐리티 SW Device
						seWorkingMessage.setStatusCode(E_CARD_ERROR);
						seWorkingMessage.setErrMsg("카드 시큐리티 SW 이상");
						
					} else if (deviceNo.equals("13")) { // 추가 dhp (2011/01/18)
						// BNA 이상
						seWorkingMessage.setStatusCode(E_BNA_ERROR);
						seWorkingMessage.setErrMsg("BNA 이상");
						
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
	 * 다쓰노 셀프 전문에서 명령 코드를 추출 
	 * 
	 * @param message	: 다쓰노 셀프 전문
	 * @return			: 명령 코드 
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
	 * 다쓰노 셀프 전문에서 ODT 번호를 추출
	 * 
	 * @param message	: ODT 전문 
	 * @return			: ODT 번호 
	 */
	private String getNozzleNo(byte[] message) throws Exception {
		String returnMessage = null;
		
		byte nozzleNo = (byte) (message[1] - 63);
		
		if (nozzleNo > 64 || nozzleNo < 1) {
			// 주유기 번호 범위 체크 
			LogUtility.getPumpALogger().error("### Nozzle number error " +
											 "in TransSomoSelf. " +
											 "Current nozzleNo : " + 
											 nozzleNo + " ###");
			returnMessage = null;
			
		}	// end if 
		
		if (nozzleNo < 10){
			// 두자리 수 변환 
			returnMessage = "0" + String.valueOf(nozzleNo);
			
		} else {
			returnMessage = String.valueOf(nozzleNo);

		}	// end if
		
		return returnMessage;
		
	}	// end getNozzleNo
	
	

	/**
	 * byte 배열의 data를  완전한 다쓰노 셀프 전문 형태로 변환
	 * 
	 * @param data		: data
	 * @param nozzleNo	: 노즐 번호 
	 * @return			: 다쓰노 셀프 전문 
	 */
	private byte[] makeProtocol(byte[] data, String nozzleNo) throws Exception {
		byte[] returnData 		= null;
		byte blank 				= ' ';
		int returnDataCounter 	= 0;
		byte nozzleByte 		= Byte.parseByte(nozzleNo);
		
		// STX, SA, UA, ETX, BCC 만큼의 길이를 더한다.
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
