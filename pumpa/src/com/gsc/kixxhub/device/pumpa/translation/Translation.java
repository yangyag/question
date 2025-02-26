package com.gsc.kixxhub.device.pumpa.translation;

import java.util.Calendar;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;

public class Translation {
	/**
	 * ���ݰ� ���� �����ͷ� �ܰ��� ����Ѵ�.
	 * liter parameter�� �ݵ�� 4.3 ���¸� ���Ѿ� �Ѵ�.
	 * ������� �Ҽ��� ���� ���� �����̴�.
	 * 
	 * @param price	: ����
	 * @param liter	: ����(4.3)
	 * @return	: �ܰ�
	 */
	public String calcBasePrice(String price, String liter) {
		String returnData = "";
		
		double literD 	= Integer.parseInt(liter) * 0.001;
		int priceI 		= Integer.parseInt(price);
		
		if (literD > 0) {
			returnData = String.valueOf((int)(priceI / literD));
			
		} else {
			returnData = "0";

		}	// end if
		
		return returnData;
		
	}	// end calcBasePrice

	
	
	/**
	 * ���ϴ� ũ���� ��ĭ�� �����
	 * 
	 * @param length 	: ��ĭ ����
	 * @return			: String ���� ��ĭ
	 */
	public String generateBlank(int length) throws Exception {
		StringBuffer buffer = new StringBuffer();
		
		for (int i = 0; i < length; i++) {
			buffer.append(" ");
			
		}	// end for
		
		return buffer.toString();
		
	}	// end generateBlank
	
	
	
	/**
	 * ���ڿ��� ���ϴ� ���̸�ŭ �����
	 * 
	 * @param length	: ����
	 * @param append	: ����
	 * @return
	 */
	public String generateBlank(int length, String append) throws Exception {
		StringBuffer buffer = new StringBuffer();
		
		for (int i = 0; i < length; i++) {
			buffer.append(append);
			
		}	// end for
		
		return buffer.toString();
		
	}	// end generateBlank
	
	
	
	
	/**
	 * YYMMDDhhmmss ������ ���� �ð� ���ϱ�
	 * 
	 * @param length 	: 12(YYMMDDhhmmss), 6(YYMMDD), 2(YY) �� ���ϴ� �ڸ��� �Է�. �⺻ ���� 12
	 * @return			: String ���� ���� �ð�
	 */
	public String getSystemTime(int length) throws Exception {
		
		String returnData = null; 
		Calendar calendar = Calendar.getInstance();
		
		String year = Integer.toString(calendar.get(Calendar.YEAR));
		String month = Integer.toString(calendar.get(Calendar.MONTH) + 1);
		
		if(month.length() != 2){
			month = "0" + month;
		}	// end if
		
		String day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
		
		if(day.length() != 2){
			day = "0" + day;	
		}	// end if
		
		String hour = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
		
		if(hour.length() != 2){
			hour = "0" + hour;	
		}	// end if
		
		String minute = Integer.toString(calendar.get(Calendar.MINUTE));
		
		if(minute.length() != 2){
			minute = "0" + minute;	
		}	// end if
		
		String second = Integer.toString(calendar.get(Calendar.SECOND));
		
		if(second.length() != 2){
			second = "0" + second;	
		}	// end if
		
		if (length == 12) {
			// YYMMDDhhmmss
			returnData = year.substring(2) + month + day + hour + minute + second;
		
		} else if (length == 6) {
			//YYMMDD 
			returnData = year.substring(2) + month + day;
			
		} else if (length == 2) {
			//YY (YYYY �� �� 2��)
			returnData = year.substring(0, 2);
		
		} else {
			// YYMMDDhhmmss
			returnData = year.substring(2) + month + day + hour + minute + second;
		}	// end if
		
		return returnData;
	
	}	// end getSystemTime

	
	
	/**
	 * generateWorkingMessage()�� ������ �ܰ迡�� �α� ��� �� 
	 * ETX ���ʹ� ������� �ʴ´�.
	 * TransOdt������ ���� ��Ȳ ó�� ������ �� �޼ҵ带 ������� �ʴ´�.
	 * 
	 * @param message : ������ ����
	 * @param wMessage : Kixx Hub ����
	 * @return : �α� ��� �޼��� 
	 */
	public String printResultLog(byte[] message, WorkingMessage wMessage){
		String returnMessage = "";
		int etxIndex = message.length;
		
		// �α� ��� �� ETX ���Ĵ� ����
		for (int i = 0; i < etxIndex; i++) {
			if (message[i] == IPumpConstant.DELIMITER_0X03) {
				etxIndex = i;
				break;
			}	// end if
		}	// end for
		
		// �α� ���
		if (wMessage != null) {
			returnMessage = "[" + new String(message, 0, etxIndex) + "]>>" + wMessage.getCommand(); 
			
		} else {
			returnMessage = "[" + new String(message) + "]>>" + "NULL";
			
		}	// end if
		
		return returnMessage;
		
	}	// end printResultLog
	
	
	
	/**
	 * ���ڿ��� ���ϴ� ���̷� �߶��ش�. 
	 * ���ڿ��� ���ϴ� ���̺��� ª�ٸ�, �״�� ��ȯ�Ѵ�. 
	 * 
	 * @param message	: ���ڿ� ���
	 * @param length	: ����
	 * @return
	 */
	public String subStringCheck(String message, int length) throws Exception {
		String returnMessage = null;
		
		if (message.length() > length) {
			returnMessage = message.substring(0, length);
			
		} else {
			returnMessage = message;
			
		}	// end if
		
		return returnMessage;
		
	}	// end subStringCheck

}
