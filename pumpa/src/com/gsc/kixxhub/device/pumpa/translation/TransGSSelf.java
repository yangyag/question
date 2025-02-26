package com.gsc.kixxhub.device.pumpa.translation;

import java.util.Iterator;
import java.util.Vector;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.Command;
import com.gsc.kixxhub.device.pumpa.common.DataStruct;
import com.gsc.kixxhub.device.pumpa.datas.E0_WorkingMessage;

/**
 * @author yd
 * 
 * Self ǥ�� ��������
 *
 */
public class TransGSSelf extends Translation {
	
	private final String DEFAULT_SEQUENCE = "1";
	/**
	 * ��������
	 */
	private final String GROUP_01 = "01";
	/**
	 * ������� ����
	 */
	private final String GROUP_02 = "02";
	/**
	 * �ݾ�/���� ����
	 */
	private final String GROUP_03 = "03";
	/**
	 * ��������
	 */
	private final String GROUP_04 = "04";
	/**
	 * ���ʽ�ī�� ����
	 */
	private final String GROUP_06 = "06";
	/**
	 * ���ݿ�����ī�� ����
	 */
	private final String GROUP_07 = "07";
	/**
	 * ķ���� ����
	 */
	private final String GROUP_08 = "08";
	/**
	 * ������ ����
	 */
	private final String GROUP_21 = "21";
	
	/**
	 * �����丮�� ����
	 */
	private final String GROUP_22 = "22";
	
	

	public byte[] generateByteStream(WorkingMessage workingMessage) throws Exception {
		byte[] returnMessage = null;
		
		String workingMessageCommand = workingMessage.getCommand();
		
		if (workingMessageCommand.equals(IPumpConstant.COMMANDID_E0)) {
			// WorkingMessage E0 : ȯ�漳�� ����  
			// ǥ�� E0	 		 : ȯ�漳�� ���� 
			E0_WorkingMessage e0WorkingMessage = (E0_WorkingMessage) workingMessage;
			String group = e0WorkingMessage.getGroup();
			Vector<Object> e0DataVector = e0WorkingMessage.getDatas();
			String e0DataVectorSize 	= String.valueOf(e0DataVector.size());
			Iterator<Object> iterator 			= e0DataVector.iterator();
			int iteratorCounter 		= 0;
			
			DataStruct e0Ds = new DataStruct();
			
			// Command
			e0Ds.addString("command", IPumpConstant.COMMANDID_E0, 2);
			// Sequence
			e0Ds.addString("sequence", DEFAULT_SEQUENCE, 1);
			// Group
			e0Ds.addString("group", group, 2);
			// ������ ���� 
			e0Ds.addString("dataSize", 
					GlobalUtility.appending0Pre(e0DataVectorSize, 2), 
					2);
			
			// ȯ�漳�� ���� 
			// GROUP 01 : ���� ���� 
			if (group.equals(GROUP_01)) {
				// �����
				e0Ds.addString("storeName", 
						(String) iterator.next(), 
						20);
				// �λ縻
				e0Ds.addString("introduce", 
						(String) iterator.next(), 
						50);
				// �������� 
				e0Ds.addString("notice", 
						(String) iterator.next(), 
						200);
				// �����ڵ�
				e0Ds.addString("storeCode", 
						(String) iterator.next(), 
						10);
				// ����� ��ȣ
				e0Ds.addString("regiNo", 
						(String) iterator.next(), 
						12);
				// ��ȣ
				e0Ds.addString("corpName", 
						(String) iterator.next(), 
						100);
				// ��ǥ�ڸ�
				e0Ds.addString("repName", 
						(String) iterator.next(), 
						50);
				// ����� �����ȣ
				e0Ds.addString("zipCode", 
						(String) iterator.next(), 
						7);
				// ����� �ּ�1
				e0Ds.addString("address1", 
						(String) iterator.next(), 
						100);
				// ����� �ּ� 2
				e0Ds.addString("address2", 
						(String) iterator.next(), 
						100);
				// ��ȭ��ȣ
				e0Ds.addString("tel", 
						(String) iterator.next(), 
						20);
				// �⺻ �Ӹ���
				e0Ds.addString("headPrint", 
						(String) iterator.next(), 
						50);
				// �⺻ ������ 1
				e0Ds.addString("footPrint1", 
						(String) iterator.next(), 
						50);
				// �⺻ ������ 2
				e0Ds.addString("footPrint2", 
						(String) iterator.next(), 
						50);
				// ����/���� ���� ��뿩��
				e0Ds.addString("presetStop", 
						(String) iterator.next(), 
						1);
				
				// GROUP 02 : ������� ���� 
			} else if (group.equals(GROUP_02)) {
				while (iterator.hasNext()) {
					// ID
					e0Ds.addString("id" + iteratorCounter, 
							(String) iterator.next(), 
							2);
					// ���� �ڵ�
					e0Ds.addString("code" + iteratorCounter, 
							(String) iterator.next(), 
							2);
					// �������� �� 
					e0Ds.addString("method" + iteratorCounter, 
							(String) iterator.next(), 
							12);
					
					iteratorCounter++;
					
				}	// end while
				
			// GROUP 04 : ���� ���� 
			} else if (group.equals(GROUP_04)) {
				while (iterator.hasNext()) {
					// ID
					e0Ds.addString("id" + iteratorCounter, 
							(String) iterator.next(), 
							2);
					// �����ȣ
					e0Ds.addString("nozzleNo" + iteratorCounter, 
							(String) iterator.next(), 
							2);
					// �����ڵ�
					e0Ds.addString("productCode" + iteratorCounter, 
							(String) iterator.next(), 
							4);
					// ������
					e0Ds.addString("productName" + iteratorCounter, 
							(String) iterator.next(), 
							14);
					// �ܰ� 
					e0Ds.addString("basePrice" + iteratorCounter, 
							(String) iterator.next(), 
							6);
					
					iteratorCounter++;
					
				}	// end while
			
				
			// GROUP 06 : ���ʽ�ī�� ���� 
			} else if (group.equals(GROUP_06)) {
				while (iterator.hasNext()) {
					// ID
					e0Ds.addString("id" + iteratorCounter, 
							(String) iterator.next(), 
							2);
					// ���� �ڵ�
					e0Ds.addString("code" + iteratorCounter, 
							(String) iterator.next(), 
							2);
					// ī�� �� 
					e0Ds.addString("cardName" + iteratorCounter, 
							(String) iterator.next(), 
							12);
					
					iteratorCounter++;
					
				}	// end while
			
			// GROUP 07 : ���ݿ�����ī�� ���� 
			} else if (group.equals(GROUP_07)) {
				while (iterator.hasNext()) {
					// ID
					e0Ds.addString("id" + iteratorCounter, 
							(String) iterator.next(), 
							2);
					// ���� �ڵ�
					e0Ds.addString("code" + iteratorCounter, 
							(String) iterator.next(), 
							4);
					// ī�� �� 
					e0Ds.addString("cardName" + iteratorCounter, 
							(String) iterator.next(), 
							12);
					
					iteratorCounter++;
					
				}	// end while
				
			// GROUP 08 : ķ���� ����
			} else if (group.equals(GROUP_08)) {
				while (iterator.hasNext()) {
					// ID
					e0Ds.addString("id" + iteratorCounter, 
							(String) iterator.next(), 
							2);
					// ���� �ڵ�
					e0Ds.addString("code" + iteratorCounter, 
							(String) iterator.next(), 
							4);
					// ĸ���� �� 
					e0Ds.addString("campaignName" + iteratorCounter, 
							(String) iterator.next(), 
							12);
					
					iteratorCounter++;
					
				}	// end while
				
			// GROUP 21 : ������ ����
			} else if (group.equals(GROUP_21)) {
				while (iterator.hasNext()) {
					// ID
					e0Ds.addString("id" + iteratorCounter, 
							(String) iterator.next(), 
							2);
					// ���� �ڵ�
					e0Ds.addString("code" + iteratorCounter, 
							(String) iterator.next(), 
							4);
					// ������ �� 
					e0Ds.addString("movieName" + iteratorCounter, 
							(String) iterator.next(), 
							12);
					// ���� ũ��
					e0Ds.addString("fileSize", 
							(String) iterator.next(), 
							6);
					
					// ������ ������
					String movieData = (String) iterator.next();
					e0Ds.addString("movieData", 
							movieData, 
							movieData.length());
					
					iteratorCounter++;
					
				}	// end while
				
			// GROUP 22 : �����丮�� ����
			} else if (group.equals(GROUP_22)) {
				// ID
				e0Ds.addString("id", 
						(String) iterator.next(), 
						2);
				// �����丮 ȭ���
				e0Ds.addString("historyName", 
						(String) iterator.next(), 
						100);
				
			}	// end if
			
			byte[] tempArray = e0Ds.getByteStream();
			
			returnMessage = this.makeProtocol(tempArray, 
								e0WorkingMessage.getNozzleNo());
			
		} else {
			LogUtility.getPumpALogger().error(
					"# [TransSelf] # " +
					"Not Supported command(" + 
					workingMessageCommand +
					"#");
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
		
	}	// end genearateByteArray
	
	
	
	public WorkingMessage generateWorkingMessage(byte[] message, 
													String command) throws Exception {
		WorkingMessage returnMessage = null;	
		
//		LogUtility.getPumpALogger().debug(this.printResultLog(message, returnMessage));
	
		return returnMessage;
	
	}	// end generateWorkingMessage
	


	/**
	 * byte �迭�� data��  ������ �Ҹ� ���� ���� ���·� ��ȯ
	 * 
	 * @param data		: data
	 * @param nozzleNo	: ���� ��ȣ 
	 * @return			: �Ҹ� ���� ���� 
	 */
	private byte[] makeProtocol(byte[] data, String nozzleNo) throws Exception {
		if (data == null) {
			return null;
		}	// end if
		
		byte[] returnData 		= null;
		byte blank 				= ' ';
		int returnDataCounter 	= 0;
		byte nozzleByte 		= Byte.parseByte(nozzleNo);
		
		// STX, SA, UA, ETX, BCC ��ŭ�� ���̸� ���Ѵ�.
		int arrayLength = data.length + 5;
		
		returnData = new byte[arrayLength];
		
		returnData[returnDataCounter++] = Command.STX;					// STX
		returnData[returnDataCounter++] = (byte) (nozzleByte + 0x40);	// SA
		returnData[returnDataCounter++] = blank;						// UA
		
		for (int i = 0; i < data.length; i++) {
			returnData[returnDataCounter++] = data[i];
			
		}	// end for
		
		returnData[returnDataCounter++] = Command.ETX;					// ETX
		returnData[returnDataCounter++] = blank;						// BCC		
		
		return returnData;
		
	}	// end makeProtocol
	
}
