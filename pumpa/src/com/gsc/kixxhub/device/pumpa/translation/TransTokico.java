package com.gsc.kixxhub.device.pumpa.translation;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.S3_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S4_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S5_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SJ_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.Command;
import com.gsc.kixxhub.device.pumpa.common.DataStruct;
import com.gsc.kixxhub.device.pumpa.devDatas.TokicoDS;

/**
 * TOKICO 구형 PROTOCOL 자리 수
 * LITER = 2.3
 * PRICE = 5 
 * 
 * 
 * @author yd
 *
 */
public class TransTokico extends Translation {
	// 기준 노즐 번호
	private int baseNozzle 	= 0;
	
	
	
	/**
	 * @param baseNozzle
	 */
	public TransTokico(int baseNozzle){
		this.baseNozzle = baseNozzle;
		
	}


	
	/**
	 * WorkingMessage를 도끼꼬 구형 전문으로 변환한다.
	 * 
	 * @param workingMessage 	: WorkingMessage
	 * @return					: 도끼꼬 구형 전문 
	 */
	public byte[] generateByteStream(WorkingMessage workingMessage) throws Exception {
		byte[] returnMessage = null;
		String workingMessageCommand = workingMessage.getCommand();
		
		
		if (workingMessageCommand.equals(IPumpConstant.COMMANDID_P8)) {
			// WorkingMessage P8 : 토털게이지 자료 요청
			returnMessage = new byte[2];
			returnMessage[0] = 't';
			returnMessage[1] = Command.SOH;
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_SI)) {
			// WorkingMessage SI : 주유시작자료 요청
			returnMessage = new byte[2];
			returnMessage[0] = 't';
			returnMessage[1] = Command.SOH;

		} else {
			LogUtility.getPumpALogger().error(
					"### Not Supported " + "command(" + workingMessageCommand
							+ ") in TransTokico ###");
			
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
		
	}	// end generateByteStream
	
	
	
	/**
	 * 도끼꼬 구형 전문을 WorkingMessage로 변환한다.
	 * 주유 중 자료를 전송하는 노즐의 Status 값은 '4' 이다.
	 * 주유 중 자료의 liter는 2.3 이다.
	 * 주유기로부터 단가 data는 전송되지 않으므로 price / liter 계산을 한다.
	 * S5 or SJ 전문 변환 시 status는 반드시 null 이어야 한다.
	 * 
	 * @param status				: 노즐 별 status
	 * @param message				: 노즐 별 data
	 * @param command				: WorkingMessage Command
	 * @param nozzleCountFromBase	: Base Nozzle기준의 Nozzle 번호 
	 * @return						: WorkingMessage
	 */
	public WorkingMessage generateWorkingMessage(byte[] status, byte[] message, 
									String command, int nozzleCountFromBase) throws Exception {
		WorkingMessage returnMessage = null;
		// Data 기준 노즐 개수
		int messageCount = message.length / 10;
		// 노즐 번호
		String nozzleNo = this.getNozzleNo(nozzleCountFromBase);
		
		
		// WorkingMessage S3 : 주유/충전 중 자료전송
		if (command.equals(IPumpConstant.COMMANDID_S3)) {
			//if (status[nozzleCountFromBase] == '4') {
				// 해당 Nozzle 데이터의 위치
				DataStruct ds 		= TokicoDS.getDS("S3", messageCount);
				ds.setByteStream(message);
				
				String literString 		= (String) ds.getValue("liter");
				String priceString 		= (String) ds.getValue("price");
				String basePriceString 	= null;
				double literD 			= Integer.parseInt(literString) * 0.001;
				int price 				= Integer.parseInt(priceString);
				
				S3_WorkingMessage s3WorkingMessage = new S3_WorkingMessage();
				
				if (literD > 0) {
					basePriceString = String.valueOf((int)(price / literD));
					s3WorkingMessage.setBasePrice(this.setLength(basePriceString, 4) + "00");
					
				} else {
					s3WorkingMessage.setBasePrice("000000");

				}
				
				s3WorkingMessage.setNozzleNo(nozzleNo);
				s3WorkingMessage.setLiter(this.setLength(literString, 7));
				s3WorkingMessage.setPrice(this.setLength(priceString, 6));
				s3WorkingMessage.setWDate(this.getSystemTime(6));
				
				returnMessage = s3WorkingMessage;

//			} else {
//				LogUtility.getPumpALogger().error("### Status Error " +
//									"to translate \"S3\" in TransTokico ###");
//				return null;
//				
//			}	// end inner if

		// WorkingMessage S5 : Total Gauge 전송
		} else if (command.equals(IPumpConstant.COMMANDID_S5)) {
			if (status == null) {
				DataStruct ds = TokicoDS.getDS("S5", messageCount);
				ds.setByteStream(message);
				
				S5_WorkingMessage s5WorkingMessage = new S5_WorkingMessage();
				s5WorkingMessage.setNozzleNo(nozzleNo);
				s5WorkingMessage.setTotalGauge((String) ds.getValue("totalGauge" + 
												nozzleCountFromBase));
				s5WorkingMessage.setSystemTime(this.getSystemTime(12));
				
				returnMessage = s5WorkingMessage;
				
			} else {
				LogUtility.getPumpALogger().error("### Status Error " +
								"to translate \"S5\" in TransTokico ###");
				return null;
				
			}	// end inner if
		
		//	WorkingMessage SJ : Total Gauge 전송
		} else if (command.equals(IPumpConstant.COMMANDID_SJ)) {
			if (status == null) {
				DataStruct ds = TokicoDS.getDS("SJ", messageCount);
				ds.setByteStream(message);
				
				SJ_WorkingMessage sjWorkingMessage = new SJ_WorkingMessage();
				sjWorkingMessage.setNozzleNo(nozzleNo);
				sjWorkingMessage.setTotalGauge((String) ds.getValue("totalGauge" + 
						nozzleCountFromBase));
				sjWorkingMessage.setSystemTime(this.getSystemTime(12));
				
				returnMessage = sjWorkingMessage;
				
			} else {
				LogUtility.getPumpALogger().error("### Status Error " +
								"to translate \"SJ\" in TransTokico ###");
				return null;
				
			}	// end inner if
		
		} else {
			LogUtility.getPumpALogger().error(
					"### Not Supported " + "command(" + command + 
										") in TransTokico ###");
			returnMessage = null;
			
		}	// end if
		
//		LogUtility.getPumpALogger().debug(this.printResultLog(message, returnMessage));
		
		return returnMessage;
		
	}	// end generateWorkingMessage
	
	
	
	/**
	 * 도끼꼬 구형 전문을 S4 WorkingMessage로 변환한다.
	 * 주유기로부터 단가 data는 전송되지 않으므로 price / liter 계산을 한다.
	 * 
	 * @param price					: 가격
	 * @param liter					: 수량(2.3)
	 * @param totalGauge			: 각 노즐 별 totalgauge data
	 * @param command				: WorkingMessage Command
	 * @param nozzleCountFromBase	: Base Nozzle기준의 Nozzle 번호 
	 * @return						: WorkingMessage
	 */
	public WorkingMessage generateWorkingMessage(int price, int liter, 
				byte[] totalGauge, String command, int nozzleCountFromBase) throws Exception {
		// TotalGauge 개수
		String priceString 		= String.valueOf(price);
		String literString 		= String.valueOf(liter);
		String basePriceString	= null;
		String nozzleNo 		= this.getNozzleNo(nozzleCountFromBase);
		int messageCount 		= totalGauge.length / 10;
		double literD 			= liter * 0.001;
		
		if (liter > 0) {
			basePriceString = String.valueOf((int)(price / literD));
			
		} else {
			basePriceString = "0000";
			
		}	// end if
		
		S4_WorkingMessage returnMessage = new S4_WorkingMessage();
		
		returnMessage.setNozzleNo(nozzleNo);
		returnMessage.setFlag("0");					// 플래그 임의 값
		returnMessage.setWDate(this.getSystemTime(6));
		returnMessage.setSystemTime(this.getSystemTime(12));
		returnMessage.setStatusFlag("0");			// Status Flag 임의 값
		returnMessage.setBasePrice(this.setLength(basePriceString, 4) + "00");
		
		// TotalGauge data 추출
		DataStruct totalGaugeDs = TokicoDS.getDS("S5", messageCount);
		totalGaugeDs.setByteStream(totalGauge);
		
		returnMessage.setTotalGauge((String) totalGaugeDs.getValue("totalGauge" + 
				nozzleCountFromBase));
		
		returnMessage.setLiter(this.setLength(literString, 7));
		returnMessage.setPrice(this.setLength(priceString, 8));
			
		return returnMessage;
		
	}	// end generateWorkingMessage 
	
	
	
	/**
	 * 도끼꼬 구형 전문에서 노즐번호를 추출
	 * 
	 * @param message	: 도끼꼬 전문 
	 * @return			: 노즐 번호 
	 */
	private String getNozzleNo(int nozzleCountFromBase) throws Exception {
		String returnMessage = null;
		
		returnMessage = String.valueOf(baseNozzle + nozzleCountFromBase);
		
		if (returnMessage.length() == 1) {
			returnMessage = "0" + returnMessage;
			
		}	// end if
		
		return returnMessage;
		
	}	// end getNozzleNo
	
	
	
	/**
	 * 문자열 왼쪽에 "0"을 붙여 원하는 길이 만큼 만들어낸다. 
	 * 
	 * @param message	: 문자열
	 * @param length	: 길이 
	 * @return	
	 */
	public String setLength(String message, int length) throws Exception {
		String returnMessage	= message;
		int messageLength 		= message.length();
		int zeroLength 			= length - messageLength;

		if (zeroLength < 0) {
			LogUtility.getPumpALogger().error("### ZeroLength ERROR " +
									"in TransTokico ###");
			
		} else {
			for (int i = 0; i < zeroLength	; i++) {
				returnMessage = "0" + returnMessage;
				
			}	// end for

		}	// end if
		
		return returnMessage;
		
	}	// end setLength
	
}