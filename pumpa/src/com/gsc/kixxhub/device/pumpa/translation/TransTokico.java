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
 * TOKICO ���� PROTOCOL �ڸ� ��
 * LITER = 2.3
 * PRICE = 5 
 * 
 * 
 * @author yd
 *
 */
public class TransTokico extends Translation {
	// ���� ���� ��ȣ
	private int baseNozzle 	= 0;
	
	
	
	/**
	 * @param baseNozzle
	 */
	public TransTokico(int baseNozzle){
		this.baseNozzle = baseNozzle;
		
	}


	
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
			returnMessage = new byte[2];
			returnMessage[0] = 't';
			returnMessage[1] = Command.SOH;
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_SI)) {
			// WorkingMessage SI : ���������ڷ� ��û
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
		
	}	// end generateByteStream
	
	
	
	/**
	 * ������ ���� ������ WorkingMessage�� ��ȯ�Ѵ�.
	 * ���� �� �ڷḦ �����ϴ� ������ Status ���� '4' �̴�.
	 * ���� �� �ڷ��� liter�� 2.3 �̴�.
	 * ������κ��� �ܰ� data�� ���۵��� �����Ƿ� price / liter ����� �Ѵ�.
	 * S5 or SJ ���� ��ȯ �� status�� �ݵ�� null �̾�� �Ѵ�.
	 * 
	 * @param status				: ���� �� status
	 * @param message				: ���� �� data
	 * @param command				: WorkingMessage Command
	 * @param nozzleCountFromBase	: Base Nozzle������ Nozzle ��ȣ 
	 * @return						: WorkingMessage
	 */
	public WorkingMessage generateWorkingMessage(byte[] status, byte[] message, 
									String command, int nozzleCountFromBase) throws Exception {
		WorkingMessage returnMessage = null;
		// Data ���� ���� ����
		int messageCount = message.length / 10;
		// ���� ��ȣ
		String nozzleNo = this.getNozzleNo(nozzleCountFromBase);
		
		
		// WorkingMessage S3 : ����/���� �� �ڷ�����
		if (command.equals(IPumpConstant.COMMANDID_S3)) {
			//if (status[nozzleCountFromBase] == '4') {
				// �ش� Nozzle �������� ��ġ
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

		// WorkingMessage S5 : Total Gauge ����
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
		
		//	WorkingMessage SJ : Total Gauge ����
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
	 * ������ ���� ������ S4 WorkingMessage�� ��ȯ�Ѵ�.
	 * ������κ��� �ܰ� data�� ���۵��� �����Ƿ� price / liter ����� �Ѵ�.
	 * 
	 * @param price					: ����
	 * @param liter					: ����(2.3)
	 * @param totalGauge			: �� ���� �� totalgauge data
	 * @param command				: WorkingMessage Command
	 * @param nozzleCountFromBase	: Base Nozzle������ Nozzle ��ȣ 
	 * @return						: WorkingMessage
	 */
	public WorkingMessage generateWorkingMessage(int price, int liter, 
				byte[] totalGauge, String command, int nozzleCountFromBase) throws Exception {
		// TotalGauge ����
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
		returnMessage.setFlag("0");					// �÷��� ���� ��
		returnMessage.setWDate(this.getSystemTime(6));
		returnMessage.setSystemTime(this.getSystemTime(12));
		returnMessage.setStatusFlag("0");			// Status Flag ���� ��
		returnMessage.setBasePrice(this.setLength(basePriceString, 4) + "00");
		
		// TotalGauge data ����
		DataStruct totalGaugeDs = TokicoDS.getDS("S5", messageCount);
		totalGaugeDs.setByteStream(totalGauge);
		
		returnMessage.setTotalGauge((String) totalGaugeDs.getValue("totalGauge" + 
				nozzleCountFromBase));
		
		returnMessage.setLiter(this.setLength(literString, 7));
		returnMessage.setPrice(this.setLength(priceString, 8));
			
		return returnMessage;
		
	}	// end generateWorkingMessage 
	
	
	
	/**
	 * ������ ���� �������� �����ȣ�� ����
	 * 
	 * @param message	: ������ ���� 
	 * @return			: ���� ��ȣ 
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
	 * ���ڿ� ���ʿ� "0"�� �ٿ� ���ϴ� ���� ��ŭ ������. 
	 * 
	 * @param message	: ���ڿ�
	 * @param length	: ���� 
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