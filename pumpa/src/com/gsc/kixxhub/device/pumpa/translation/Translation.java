package com.gsc.kixxhub.device.pumpa.translation;

import java.util.Calendar;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;

public class Translation {
	/**
	 * 가격과 리터 데이터로 단가를 계산한다.
	 * liter parameter는 반드시 4.3 형태를 지켜야 한다.
	 * 계산결과는 소수가 없는 정수 형태이다.
	 * 
	 * @param price	: 가격
	 * @param liter	: 리터(4.3)
	 * @return	: 단가
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
	 * 원하는 크기의 빈칸을 만든다
	 * 
	 * @param length 	: 빈칸 길이
	 * @return			: String 형태 빈칸
	 */
	public String generateBlank(int length) throws Exception {
		StringBuffer buffer = new StringBuffer();
		
		for (int i = 0; i < length; i++) {
			buffer.append(" ");
			
		}	// end for
		
		return buffer.toString();
		
	}	// end generateBlank
	
	
	
	/**
	 * 문자열을 원하는 길이만큼 만든다
	 * 
	 * @param length	: 길이
	 * @param append	: 문자
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
	 * YYMMDDhhmmss 형식의 현재 시간 구하기
	 * 
	 * @param length 	: 12(YYMMDDhhmmss), 6(YYMMDD), 2(YY) 중 원하는 자릿수 입력. 기본 값은 12
	 * @return			: String 형태 현재 시간
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
			//YY (YYYY 중 앞 2개)
			returnData = year.substring(0, 2);
		
		} else {
			// YYMMDDhhmmss
			returnData = year.substring(2) + month + day + hour + minute + second;
		}	// end if
		
		return returnData;
	
	}	// end getSystemTime

	
	
	/**
	 * generateWorkingMessage()의 마지막 단계에서 로그 출력 시 
	 * ETX 부터는 출력하지 않는다.
	 * TransOdt에서는 예외 상황 처리 때문에 이 메소드를 사용하지 않는다.
	 * 
	 * @param message : 주유기 전문
	 * @param wMessage : Kixx Hub 전문
	 * @return : 로그 출력 메세지 
	 */
	public String printResultLog(byte[] message, WorkingMessage wMessage){
		String returnMessage = "";
		int etxIndex = message.length;
		
		// 로그 출력 시 ETX 이후는 생략
		for (int i = 0; i < etxIndex; i++) {
			if (message[i] == IPumpConstant.DELIMITER_0X03) {
				etxIndex = i;
				break;
			}	// end if
		}	// end for
		
		// 로그 출력
		if (wMessage != null) {
			returnMessage = "[" + new String(message, 0, etxIndex) + "]>>" + wMessage.getCommand(); 
			
		} else {
			returnMessage = "[" + new String(message) + "]>>" + "NULL";
			
		}	// end if
		
		return returnMessage;
		
	}	// end printResultLog
	
	
	
	/**
	 * 문자열을 원하는 길이로 잘라준다. 
	 * 문자열이 원하는 길이보다 짧다면, 그대로 반환한다. 
	 * 
	 * @param message	: 문자열 대상
	 * @param length	: 길이
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
