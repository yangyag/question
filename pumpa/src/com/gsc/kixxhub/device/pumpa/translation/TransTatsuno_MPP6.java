package com.gsc.kixxhub.device.pumpa.translation;

import java.util.Vector;

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
import com.gsc.kixxhub.device.pumpa.controller.TatsunoMPPNoz;
import com.gsc.kixxhub.device.pumpa.devDatas.TatsunoMppDS;

/**
 * TATSUNO MPP6 PROTOCOL 자리 수
 * LITER = 3.3 
 * PRICE = 6
 * 
 * 
 * @author yd
 *
 */
public class TransTatsuno_MPP6 extends Translation {
	// DeviceSelector의 TatsunoMppNozVec을 생성자에서 전달 받는다. 
	private Vector<TatsunoMPPNoz> nozVector;
	
	
	
	/**
	 * 
	 */
	public TransTatsuno_MPP6() {
		
	}
	
	
	
	/**
	 * @param nozVector
	 */
	public TransTatsuno_MPP6(Vector<TatsunoMPPNoz> nozVector) {
		this.nozVector = nozVector;
		
	}
	
	

	/**
	 * WorkingMessage를 다쓰노 초고속 전문으로 변환한다.
	 * 
	 * @param workingMessage 	: WorkingMessage
	 * @return					: 다쓰노 초고속 전문 
	 */
	public byte[] generateByteStream(WorkingMessage workingMessage) throws Exception {
		byte[] returnMessage = null;
		
		String workingMessageCommand 	= workingMessage.getCommand();
		String nozzleNo 				= workingMessage.getNozzleNo();
		// 노즐번호 위치 
		int position 					= this.getNozzlePosition(nozzleNo);
		
		if (position < 0) {
			LogUtility.getPumpALogger().error("### Nozzle position error " +
											 "in TransTatsunoMPP. " +
											 "Current position : " +
											 position + " ###");
			
		}	// end if
		
		int tempNozI = Integer.parseInt(nozzleNo);
		// 기준 노즐 번호 구하기 
		String tempNozzleNo = String.valueOf(tempNozI - position);
	
		
		if (workingMessageCommand.equals(IPumpConstant.COMMANDID_P8)) {
			// WorkingMessage P8 : 토털게이지 자료 요청
			// TatsunoMPP 20     : 주유기 적산치 요구 
			returnMessage = new byte[2];
			returnMessage[0] = 0x32;
			returnMessage[1] = 0x30;
			
			returnMessage = this.makeProtocol(returnMessage, nozzleNo);
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_SI)) {
			// WorkingMessage SI : 주유시작자료 요청
			// TatsunoMPP 20     : 주유기 적산치 요구 
			returnMessage = new byte[2];
			returnMessage[0] = 0x32;
			returnMessage[1] = 0x30;
			
			returnMessage = this.makeProtocol(returnMessage, nozzleNo);
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PE)) {
			// WorkingMessage PE : 주유기 / 충전기 상태 요청
			// TatsunoMPP 15     : 주유기 Status 요구 
			returnMessage = new byte[2];
			returnMessage[0] = 0x31;
			returnMessage[1] = 0x35;
			
			returnMessage = this.makeProtocol(returnMessage, nozzleNo);
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PA)) {
			// WorkingMessage PA : 노즐 제어명령
			// TatsunoMPP 11     : 주유허가 요청 
			PA_WorkingMessage paWorkingMessage = 
					(PA_WorkingMessage) workingMessage;
			String state = paWorkingMessage.getNozzleState();

			DataStruct returnDS = new DataStruct();
			
			if (state.equals("0")) {
				// 주유 금지 
				returnDS.addString("command", "13", 2);
				
			} else if (state.equals("1")) {
				// 주유 금지 해제 
				returnDS.addString("command", "11", 2);
				returnDS.addString("condition", "0", 1);		// 임의 값 
				returnDS.addString("preset", "1", 1);			// 임의 값 
				returnDS.addString("price", "000000", 6);		// 임의 값 			
				
				for (int i = 0; i < 6; i++) {
					returnDS.addString("flag" + i, "1", 1);
					returnDS.addString("basePrice" + i, "0000", 4);	// 임의 값 
				}
				
			} else {
				LogUtility.getPumpALogger().error("### PA WorkingMessage " +
												 "state Error. " +
												 "Current state : " +
												 state + " ###");
				
			}	// end inner if
						
			byte[] tempArray = returnDS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, tempNozzleNo);
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_P3_1)) {
			// WorkingMessage P3_1 : 주유기 환경정보 설정
			// TatsunoMPP 11       : 주유허가 요청 
			P3_1_WorkingMessage p3WorkingMessage = 
										(P3_1_WorkingMessage) workingMessage;
			
			DataStruct t10DS = new DataStruct();
			
			t10DS.addString("command", "11", 2);
			t10DS.addString("condition", "0", 1); 
			t10DS.addString("preset", "0", 1);
			t10DS.addString("price", "000000", 6);		 			
			
			for (int i = 0; i < 6; i++) {
				if (i == position) {
					t10DS.addString("flag" + i, "2", 1);
					t10DS.addString("basePrice" + i, 
							p3WorkingMessage.getBasePrice().substring(0, 4), 4);
					break;
					
				} else {
					t10DS.addString("flag" + i, "2", 1);
					t10DS.addString("basePrice" + i, "0000", 4);
					
				}	// end if
				
			}	// end for

			byte[] tempArray = t10DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, tempNozzleNo);
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PB)) {
			// WorkingMessage PB : 정액 / 정량 설정
			// TatsunoMPP 11     : 주유허가 요청 
			PB_WorkingMessage pbWorkingMessage = 
					(PB_WorkingMessage) workingMessage;
			
			String preset = pbWorkingMessage.getCommandSet();
			String basePrice = pbWorkingMessage.getBasePrice().substring(0, 4);
			String liter = pbWorkingMessage.getLiter();
			String price = pbWorkingMessage.getPrice();

			if (preset.equals("0")) {
				// PB WorkingMessage의 정액설정은 0, 다쓰노의 정액설정은 2
				preset = "2";
			
			}	// end inner if			
			
			
			DataStruct t11DS = new DataStruct();
			
			t11DS.addString("command", "11", 2);
			t11DS.addString("condition", "2", 1); 
			t11DS.addString("preset", preset, 1);
				
			if (preset.equals("1")) {
				// 정량 설정
				t11DS.addString("liter", liter.substring(1), 6);
				
			} else if (preset.equals("2")) {
				// 정액 설정
				t11DS.addString("price", price.substring(2), 6);		 			
				
			} else {
				LogUtility.getPumpALogger().error("### PB WorkingMessage " +
											"preset ERROR. " +
											"Current preset : " +
											pbWorkingMessage.getCommandSet() + 
											" ###");
				
			}	// end inner if

			for (int i = 0; i < 6; i++) {
				/*
				 * 2008년 11월 21일 동탄 신도시 주유소 테스트 통해 수정  
				 */
				
				if (i == position) {
					t11DS.addString("flag" + i, "2", 1);
					t11DS.addString("basePrice" + i, basePrice, 4);
					break;
					
				} else {
					t11DS.addString("flag" + i, "0", 1);
					t11DS.addString("basePrice" + i, "0000", 4);

				}	// end if
				
				/*
				t11DS.addString("flag" + i, "2", 1);
				t11DS.addString("basePrice" + i, basePrice, 4);
				*/
			}	// end for

			byte[] tempArray = t11DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, tempNozzleNo);
			
		} else {
			LogUtility.getPumpALogger().error("### Not Supported " +
											"command(" + workingMessageCommand +
											") in TransTatsunoMPP ###");
			returnMessage = null;
		
		}	// end if
		
		/*
		// 로그 출력 
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
	 * 다쓰노 초고속 전문을 WorkingMessage로 변환한다.
	 * 
	 * @param message	: 다쓰노 초고속 전문
	 * @param command	: WorkingMessage Command
	 * @return			: WorkingMessage 
	 */
	public WorkingMessage generateWorkingMessage(byte[] message, 
													String command) throws Exception {
		WorkingMessage returnMessage = null;
		
		String tatsunoCommandString = this.getCommand(message);
		// byte[]의 노즐번호를 얻는다
		String nozzleNo 			= this.getNozzleNo(message);
		
		
		if (tatsunoCommandString.equals("61")) {
			// TatsunoMPP 61     : 주유기의 Status 보고 
			// WorkingMessage S8 : 주유기/충전기 상태 전송
			// WorkingMessage S3 : 주유/충전 중 자료전송
			DataStruct t61Interface = TatsunoMppDS.getDS("61");
			t61Interface.setByteStream(message);

			// 노즐 번호 계산 
			String tempNozString = (String) t61Interface.getValue("nozzle");
			int tempNozI = Integer.parseInt(tempNozString);
			
			if (tempNozI != 0) {
				tempNozI = tempNozI - 1;
				
			}	// end if
			
			nozzleNo = this.getStringCalc(nozzleNo, tempNozI);
			
			if (command.equals("S8")) {
				S8_WorkingMessage s8WorkingMessage = new S8_WorkingMessage();
				s8WorkingMessage.setNozzleNo(nozzleNo);
				
				String status = (String) t61Interface.getValue("status");
				
				if (status.equals("0")) {
					// Nozzle Down, 걸린 상태
					s8WorkingMessage.setStatusCode("651");
					
				} else if (status.equals("1")) {
					// Nozzle Up
					s8WorkingMessage.setStatusCode("652");
					
				} else if (status.equals("3")) {
					// 주유 중 
					s8WorkingMessage.setStatusCode("653");
					
				} else if (status.equals("4")) {
					// 주유 종료 (Nozzle Down)
					s8WorkingMessage.setStatusCode("654");					
					
				} else {
					LogUtility.getPumpALogger().error("### Incorrect status" +
											 " in '61' TatsunoMPP " +
											 "protocol. Current status : " +
											 status + " ###");
					
				}	// end inner if
				
				s8WorkingMessage.setDeviceType("01");
				s8WorkingMessage.setStatus("0");
				s8WorkingMessage.setErrMsg(this.generateBlank(20));
				s8WorkingMessage.setDetectTime(this.getSystemTime(12));
				s8WorkingMessage.setVersion(this.generateBlank(9));
				
				returnMessage = s8WorkingMessage;
				
			} else if (command.equals("S3")) {
				S3_WorkingMessage s3WorkingMessage = new S3_WorkingMessage();

				s3WorkingMessage.setNozzleNo(nozzleNo);
				s3WorkingMessage.setLiter("0" + 
						(String) t61Interface.getValue("liter"));
				s3WorkingMessage.setPrice(
						(String) t61Interface.getValue("price"));
				s3WorkingMessage.setWDate(this.getSystemTime(6));
				s3WorkingMessage.setBasePrice(
						(String) t61Interface.getValue("basePrice") + "00");
				
				returnMessage = s3WorkingMessage;
				
			} else {
				LogUtility.getPumpALogger().error("### Can't translate 61 -> " + 
												  command + " ###");
				
			}	// end inner if
			
		} else {
			LogUtility.getPumpALogger().error("### Not Supported " +
											"command(" + tatsunoCommandString +
											") in TransTatsunoMPP ###");
			returnMessage = null;
		
		}	// end if		
		
//		LogUtility.getPumpALogger().debug(this.printResultLog(message, returnMessage));
		
		return returnMessage;

	}	// end generateWorkingMessage
	
	
	
	/**
	 * 다쓰노 초고속 전문을 WorkingMessage로 변환한다.
	 * 
	 * @param message	: 다쓰노 초고속 전문
	 * @param command	: WorkingMessage Command
	 * @param mppNozzle	: 다쓰노 MPP 노즐 번호 
	 * @return			: WorkingMessage 
	 */
	public WorkingMessage generateWorkingMessage(byte[] message, 
												 String command, int mppNozzle) throws Exception 
	{
		WorkingMessage returnMessage = null;
		
		// byte[]의 명령코드를 얻는다
		String tatsunoCommandString = this.getCommand(message);
		// byte[]의 노즐번호를 얻는다
		String nozzleNo 			= this.getStringCalc(
									this.getNozzleNo(message), mppNozzle - 1);
		
			
		if (tatsunoCommandString.equals("65")) {
			// TatsunoMPP 65 	 : 주유기의 적산계치 
			// WorkingMessage SJ : 주유시작 자료 응답 
			// WorkingMessage S5 : Total Gauge 전송
			DataStruct t65Interface = null;
			
			// 노즐 개수 분석하여 DataStruct얻기
			if (message[7] != 0x20) {
				t65Interface = TatsunoMppDS.getDS("65", 3);
				
			} else if (message[6] != 0x20) {
				t65Interface = TatsunoMppDS.getDS("65", 2);
				
			} else {
				t65Interface = TatsunoMppDS.getDS("65", 1);

			}	// end inner if
				
			t65Interface.setByteStream(message);
			
			if (command.equals("SJ")) {
				SJ_WorkingMessage sjWorkingMessage = new SJ_WorkingMessage();
				sjWorkingMessage.setNozzleNo(nozzleNo);

				if (mppNozzle == 1) {
					sjWorkingMessage.setTotalGauge((String) t65Interface
							.getValue("liter0"));

				} else if (mppNozzle == 2) {
					sjWorkingMessage.setTotalGauge((String) t65Interface
							.getValue("liter1"));

				} else if (mppNozzle == 3) {
					sjWorkingMessage.setTotalGauge((String) t65Interface
							.getValue("liter2"));

				} else {
					LogUtility.getPumpALogger().error("### Incorrect mppNozzleNo " +
													 "in TatsunoMpp 65. " +
													 "Current mppNozzleNo : " +
													 mppNozzle + " ###");

				} // end inner if

				sjWorkingMessage.setSystemTime(this.getSystemTime(12));

				returnMessage = sjWorkingMessage;
				
			} else 	if (command.equals("S5")) {
				S5_WorkingMessage s5WorkingMessage = new S5_WorkingMessage();
				s5WorkingMessage.setNozzleNo(nozzleNo);

				if (mppNozzle == 1) {
					s5WorkingMessage.setTotalGauge((String) t65Interface
							.getValue("liter0"));

				} else if (mppNozzle == 2) {
					s5WorkingMessage.setTotalGauge((String) t65Interface
							.getValue("liter1"));

				} else if (mppNozzle == 3) {
					s5WorkingMessage.setTotalGauge((String) t65Interface
							.getValue("liter2"));

				} else {
					LogUtility.getPumpALogger().error("### Incorrect mppNozzleNo " +
													 "in TatsunoMpp 65. " +
													 "Current mppNozzleNo : " +
													 mppNozzle + " ###");

				} // end inner if

				s5WorkingMessage.setSystemTime(this.getSystemTime(12));

				returnMessage = s5WorkingMessage;
			
			} else {
				LogUtility.getPumpALogger().error("### Can't translate 65 -> " + 
												command + " ###");
				
			}	// end inner if
			
		} else {
			LogUtility.getPumpALogger().error("### Not Supported " +
										"command(" + tatsunoCommandString +
										") in TransTatsunoMPP ###");
			returnMessage = null;
			
		}	// end if
		
//		LogUtility.getPumpALogger().debug(this.printResultLog(message, returnMessage));
		
		return returnMessage;
		
	}	// end generateWorkingMessage
	
	

	/**
	 * 다쓰노 초고속 전문을 S4 WorkingMessage로 변환한다.
	 * 
	 * @param message	: 다쓰노 초고속 전문. 
	 * 					  '61', '65' 전문이 반드시 포함되어야 한다.
	 * @param command	: WorkingMessage Command
	 * @return			: WorkingMessage 
	 */
	public WorkingMessage generateWorkingMessage(byte[][] message, 
												  String command, int noz) throws Exception {
		WorkingMessage returnMessage = null;
		
		// noz 범위(1~3) 검사 
		if (noz < 1 || noz > 3 ) {
			LogUtility.getPumpALogger().error("###  NozzleNo error in " +
											  "TransTatsunoMPP." +
											  "Current NozzleNo : " +
											  noz + " ####");
			
			return null;
			
		}	// end if
		
		// 노즐 번호 위치
		int position = noz - 1;
		// 실제 노즐 번호 데이터(기준 노즐 + 노즐 번호 위치) 얻기 
		String nozzleNo = this.getNozzleNo(message[0]);
		nozzleNo 		= this.getStringCalc(nozzleNo, position);
		
		// TatsunoMPP 61     : 주유기의 Status 보고
		// TatsunoMPP 65     : 주유기의 적산계치
		// WorkingMessage S4 : 주유소 주유완료 자료전송
		S4_WorkingMessage s4WorkingMessage = new S4_WorkingMessage();
			
		s4WorkingMessage.setFlag("0");					// 임의 값
		s4WorkingMessage.setWDate(this.getSystemTime(6));
		s4WorkingMessage.setStatusFlag("0");				// 임의 값
		
		for (int i = 0; i < message.length; i++) {
			String tatsunoCommandString = this.getCommand(message[i]);
			
			if (tatsunoCommandString.equals("61")) {
				DataStruct t61Interface = TatsunoMppDS.getDS("61");
				t61Interface.setByteStream(message[i]);
				
				s4WorkingMessage.setLiter("0" + 
						(String) t61Interface.getValue("liter"));
				s4WorkingMessage.setBasePrice(
						(String) t61Interface.getValue("basePrice") + "00");
				s4WorkingMessage.setPrice("00" + 
						(String) t61Interface.getValue("price"));
				s4WorkingMessage.setSystemTime(this.getSystemTime(12));
					
			} else if (tatsunoCommandString.equals("65")) {
				DataStruct t65Interface = null;
				
				// 노즐 개수 분석하여 DataStruct얻기
				if (message[i][7] != 0x20) {
					t65Interface = TatsunoMppDS.getDS("65", 3);
					
				} else if (message[i][6] != 0x20) {
					t65Interface = TatsunoMppDS.getDS("65", 2);
					
				} else {
					t65Interface = TatsunoMppDS.getDS("65", 1);

				}	// end inner if
				
				t65Interface.setByteStream(message[i]);
				
				s4WorkingMessage.setTotalGauge(
						(String) t65Interface.getValue("liter" + position));
					
			} else {
				
			}	// end if		
	
		}	// end for
		
		s4WorkingMessage.setNozzleNo(nozzleNo);

		returnMessage = s4WorkingMessage;
			
		return returnMessage;		
	
	}	// end generateWorkingMessage
	

	
	/**
	 * 다쓰노 초고속 전문에서 명령 코드를 추출 
	 * 
	 * @param message	: 다쓰노 초고속 전문
	 * @return			: 명령 코드 
	 */
	private String getCommand(byte[] message) throws Exception {
		String returnMessage = null;
		
		byte[] commandArray = new byte[2];
		commandArray[0] 	= message[3];
		commandArray[1] 	= message[4];
		
		returnMessage = new String(commandArray);
		
		return returnMessage;
	
	}	// end getCommand		
	
	

	/**
	 * 다쓰노 초고속 전문에서 노즐번호를 추출
	 * 
	 * @param message	: 다쓰노 초고속 전문 
	 * @return			: 노즐 번호 
	 */
	private String getNozzleNo(byte[] message) throws Exception {
		String returnMessage = null;
		
		byte nozzleNo = (byte) (message[1] - 63);
		
		if (nozzleNo > 64 || nozzleNo < 1) {
			// 주유기 번호 범위 체크 
			LogUtility.getPumpALogger().error("### Nozzle number error " +
											 "in TatsunoMPP. " +
											 "Current Nozzle number : " +
											 nozzleNo + " ###");
			returnMessage = null;
			
		} else if (nozzleNo <10){
			// 두자리 수 변환 
			returnMessage = "0" + String.valueOf(nozzleNo);
			
		} else {
			returnMessage = String.valueOf(nozzleNo);

		}	// end if
		
		return returnMessage;
		
	}	// end getNozzleNo
	

	
	/**
	 * BaseNozzle을 기준으로 노즐 번호의 위치를 얻는다.
	 * 
	 * @param nozzleNo	: 노즐 번호 
	 * @return			: 0부터 시작하는 위치 값 
	 */
	private int getNozzlePosition(String nozzleNo) throws Exception {
		int returnValue = -1;
		
		for (int i = 0; i < nozVector.size(); i++) {
			TatsunoMPPNoz nozzleInfo = nozVector.get(i);
			
			Vector<String> nozzleVector = nozzleInfo.getConnectNozNoVec();
			
			for (int j = 0; j < nozzleVector.size(); j++) {
				if (nozzleVector.get(j).equals(nozzleNo)) {
					returnValue = j;
					
				}	// end if
				
			}	// end inner for
			
		}	// end for
		
		return returnValue;
		
	}	// end getFirstNozzleNo
	
	
	/**
	 * String 타입의 숫자에 int 타입의 데이터를 더하여 String 타입으로 얻는다.
	 * 
	 * @param operand1	: String 타입 숫자 
	 * @param operand2	: int 타입 숫자 
	 * @return			: String 타입 결과 
	 */
	private String getStringCalc(String operand1, int operand2) throws Exception {
		String returnMessage = null;
		
		int tempI = Integer.parseInt(operand1);
		
		tempI = tempI + operand2;
		
		if (tempI < 10) {
			returnMessage = "0" + String.valueOf(tempI);
			
		} else {
			returnMessage = String.valueOf(tempI);
			
		}
		
		return returnMessage;
		
	}	// end getStringCalc
	
	
	
	/**
	 * byte 배열의 data를  완전한 다쓰노 초고속 전문 형태로 변환
	 * 
	 * @param data		: data
	 * @param nozzleNo	: 노즐 번호 
	 * @return			: 다쓰노 초고속 전문 
	 */
	private byte[] makeProtocol(byte[] data, String nozzleNo) throws Exception {
		byte[] returnData 		= null;
		byte blank 				= ' ';
		int returnDataCounter 	= 0;
		byte nozzleByte 		= Byte.parseByte(nozzleNo);
		// STX, SA, UA, ETX, BCC 만큼의 길이를 더한다.
		int arrayLength 		= data.length + 5;
		
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