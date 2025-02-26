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
 * WOOJOO PROTOCOL 자리 수
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
	 * WorkingMessage를 우주 전문으로 변환한다.
	 * 
	 * @param workingMessage 	: WorkingMessage
	 * @return					: 우주 전문 
	 */
	public byte[] generateByteStream(WorkingMessage workingMessage) throws Exception {
		byte[] returnMessage = null;
		
		String workingMessageCommand = workingMessage.getCommand();
				
		if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PA)) {
			// WorkingMessage PA : 노즐 제어명령
			// Woojoo C0         : 주유허가 취소요청
			// Woojoo E0         : 정액 정량 지정 
			PA_WorkingMessage paWorkingMessage = (PA_WorkingMessage) workingMessage;
			String nozzleState = paWorkingMessage.getNozzleState();
			
			if (nozzleState.equals("0")) {
				// PA NozzleState 0 : 주유 금지
				returnMessage 		= new byte[2];
				returnMessage[0] 	= 'c';
				returnMessage[1] 	= '0';
				
				returnMessage = this.makeProtocol(returnMessage, paWorkingMessage.getNozzleNo());
	
			} else if(nozzleState.equals("1")) {
				// PA NozzleState 1 : 주유 금지 해제
				
				/*
				 * 주유 허가 보류 
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
			// WorkingMessage PB : 정액/정량 설정
			// Woojoo E0         : 정액 정량 지정 
			PB_WorkingMessage pbWorkingMessage = (PB_WorkingMessage) workingMessage;
			String commandSet = pbWorkingMessage.getCommandSet();
			String price      = pbWorkingMessage.getPrice();
			
			DataStruct e0DS = new DataStruct();
			e0DS.addString("command", "e0", 2);
			
			if (commandSet.equals("0")) {
				// PB CommandSet 0 : 정액 설정
				e0DS.addString("mode", "2", 1);
				e0DS.addString("liter", "0000000", 7);
				e0DS.addString("basePrice", pbWorkingMessage.getBasePrice().substring(0, 4), 4);
				e0DS.addString("price", price.substring(2), 6);
			
			} else if (commandSet.equals("1")) {
				// PB CommandSet 1 : 정량 설정
				e0DS.addString("mode", "1", 1);
				e0DS.addString("liter", pbWorkingMessage.getLiter(), 7);
				e0DS.addString("basePrice", pbWorkingMessage.getBasePrice().substring(0, 4), 4);
				e0DS.addString("price", "000000", 6);
				
			} else if (commandSet.equals("2")) {
				// PB CommandSet 2 : Full 지정 
				e0DS.addString("mode", "0", 1);
				e0DS.addString("liter", "0000000", 7);
				e0DS.addString("basePrice", pbWorkingMessage.getBasePrice().substring(0, 4), 4);
				e0DS.addString("price", "000000", 6);
				
			} else if (commandSet.equals("3")) {
				// PB CommandSet 3 : 단가 변경  
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
			// WorkingMessage P3_1 : 주유기 환경정보 설정 
			// Woojoo E0           : 정액 정량 지정 
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
			// WorkingMessage P8     : Total Gauge 자료 요청
			// Woojoo T0 (S/C->Pump) : Total Gauge 요청
			returnMessage 		= new byte[2];
			returnMessage[0] 	= 't';
			returnMessage[1] 	= '0';
			
			returnMessage = this.makeProtocol(returnMessage, workingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_SI)) {
			// WorkingMessage SI     : 주유시작자료 요청	
			// Woojoo T0 (S/C->Pump) : Total Gauge 요청
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
	 * 우주 전문을 WorkingMessage로 변환한다.
	 * 
	 * @param message	: 우주 전문
	 * @param command	: WorkingMessage Command
	 * @return			: WorkingMessage 
	 */
	public WorkingMessage generateWorkingMessage(byte[] message, String command) throws Exception {
		
		WorkingMessage returnMessage = null;
		
		// byte[]의 명령코드를 얻는다
		String woojooCommandString = this.getCommand(message).substring(0, 1);
		// byte[]의 노즐번호를 얻는다
		String nozzleNo            = this.getNozzleNo(message);
		
		if (woojooCommandString.equals("p")) {
			// Woojoo P0 (Pump->S/C) : 주유완료 자료응답 
			// WorkingMessage S3     : 주유/충전 중 자료전송
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
			// Woojoo q0 (Pump->S/C) : 계량기의 에러 상태값 전달  
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
			// Woojoo T0 (Pump->S/C) : Total gauge 응답 
			if (command.equals("SJ")) {
				// WorkingMessage SJ : 주유시작 자료 응답 
				SJ_WorkingMessage sjWorkingMessage = new SJ_WorkingMessage();
				sjWorkingMessage.setNozzleNo(nozzleNo);

				DataStruct t0Interface = WoojooDS.getDS("T0", WoojooDS.FROM_PUMP);
				t0Interface.setByteStream(message);
				
				sjWorkingMessage.setTotalGauge((String) t0Interface.getValue("totalGauge"));
				sjWorkingMessage.setSystemTime(this.getSystemTime(12));

				returnMessage = sjWorkingMessage;
				
			} else if (command.equals("S5")){	
				// WorkingMessage S5 : Total Gauge 전송
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
			// Woojoo S0 (Pump->S/C) : 주유기 상태 전송 
			// WorkingMessage S8     : 주유기/충전기 상태 전송
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
				// 주유 중
				s8WorkingMessage.setStatusCode("653");
				
			} else if (mode.equals("5")) {
				// 주유 완료
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
	 * 우주 전문을 S4 WorkingMessage로 변환한다.
	 * 
	 * @param message	: 우주 전문. 
	 * 					  'p0', 't0' 전문이 반드시 포함되어야 한다.
	 * @param command	: WorkingMessage Command
	 * @return			: WorkingMessage 
	 */
	public WorkingMessage generateWorkingMessage(byte[][] message, String command) throws Exception {
		WorkingMessage returnMessage = null;
		
		// Woojoo P0 (Pump->S/C) : 주유완료 자료응답 
		// Woojoo T0 (Pump->S/C) : Total Gauge 응답 
		// WorkingMessage S4 : 주유소 주유완료 자료전송
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
	 * 우주 전문에서 명령 코드를 추출 
	 * 
	 * @param message	: 우주 전문
	 * @return			: 명령 코드 
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
	 * 우주 전문에서 노즐번호를 추출
	 * 
	 * @param message	: 우주 전문 
	 * @return			: 노즐 번호 
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
	 * byte 배열의 data를  완전한 우주 전문 형태로 변환
	 * 
	 * @param data		: data
	 * @param nozzleNo	: 노즐 번호 
	 * @return			: 우주 전문 
	 */
	private byte[] makeProtocol(byte[] data, String nozzleNo) throws Exception {
		byte[] returnData 		= null;
		byte blank 					= ' ';
		int returnDataCounter 	= 0;
		byte nozzleByte 		= Byte.parseByte(nozzleNo);
		// SA, STX, 주유기번호(2), ETX, BCC 만큼의 길이를 더한다.
		int arrayLength 		= data.length + 6;
		
		returnData = new byte[arrayLength];
		
		returnData[returnDataCounter++] = (byte) (nozzleByte + 0x40);// SA
		returnData[returnDataCounter++] = Command.STX;					// STX
		returnData[returnDataCounter++] = (byte) nozzleNo.charAt(0);	// 주유기번호(1)
		returnData[returnDataCounter++] = (byte) nozzleNo.charAt(1);	// 주유기번호(2)
		
		for (int i = 0; i < data.length; i++) {
			returnData[returnDataCounter++] = data[i];
		}	// end for
		
		returnData[returnDataCounter++] = Command.ETX;		// ETX
		returnData[returnDataCounter++] = blank;						// BCC		
		
		return returnData;
		
	}	// end makeProtocol

}