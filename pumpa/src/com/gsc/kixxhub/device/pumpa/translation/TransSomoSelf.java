package com.gsc.kixxhub.device.pumpa.translation;

import java.util.Vector;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.HA_WorkingMessageCopy;
import com.gsc.kixxhub.common.data.pump.HB_WorkingMessageCopy;
import com.gsc.kixxhub.common.data.pump.HC_WorkingMessageCopy;
import com.gsc.kixxhub.common.data.pump.HD_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P2_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P5_1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P5_NozzleInfo;
import com.gsc.kixxhub.common.data.pump.P6_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PE_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.QD_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S3_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S4_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S8_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SE_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.Change;
import com.gsc.kixxhub.device.pumpa.common.Command;
import com.gsc.kixxhub.device.pumpa.common.DataStruct;
import com.gsc.kixxhub.device.pumpa.devDatas.SomoSelfDS;

public class TransSomoSelf extends Translation {
	// byte[]의 노즐번호에 1을 더해서 WorkingMessage ConnectNozzleNo에 대입
	
	private final String E_PRINTER_ERROR 	= "201";
	private final String E_PRINTER_PAPER 	= "202";
	private final String E_VOICE_BUSY 		= "203";
	private final String E_VOICE_ERROR 		= "204";
	private final String PRINTER_ERROR 		= "261";
	private final String PRINTER_PAPER 		= "262";
	private final String VOICE_BUSY 				= "263";
	private final String VOICE_ERROR 			= "264";
	
	/**
  	 * 소모 셀프 전문 명령코드 '80'의 데이터 중 
	 * BasePrice 데이터를 추출
	 * 
	 * @param message	: 소모 셀프 전문 
	 * @return			: BasePrice
	 */
	private String extractBasePrice(byte[] message) throws Exception {
		String returnMessage = "";
		String cardType = this.extractCardType(message);
		
		// 목표 위치까지 존재하는 구분 문자의 갯수 
		int counter     = 0;
		// 구분 문자(0x1C or 0X1F)
		byte separator  = 0;
		
		// SomoSelf 80 CardType 10 : 신용 승인 요청
		if (cardType.equals("10")) {
			return "";
			
		// SomoSelf 80 CardType 11 : 신용 승인 취소 요청
		} else if (cardType.equals("11")) {
			return "";

		// SomoSelf 80 CardType 40 : 보너스 누적 요청 
		} else if (cardType.equals("40")) {
			return "";
		
		// SomoSelf 80 CardType 41 : 보너스 누적 취소 요청 
		} else if (cardType.equals("41")) {
			return "";
			
		// SomoSelf 80 CardType 42 : 보너스 이용 요청 
		} else if (cardType.equals("42")) {
			counter = 10;
			separator = 0x1C;
			
		// SomoSelf 80 CardType 43 : 보너스 이용 취소 요청 
		} else if (cardType.equals("43")) {
			return "";
			
		} else {
			LogUtility.getPumpALogger().error(
					"# [TransSomoSelf] # " +
					"CardType ERROR in TransSomoSelf " +
					"extractBasePrice # " +
					"Current cardType : " + cardType + " #");
			
		}	// end if
		
		returnMessage = this.findData(message, counter, separator, 8, 3);
		
		if (returnMessage != null) {
			// 4.2 자리수 맞추기 
			switch (returnMessage.length()) {
			case 3:
				returnMessage = "000" + returnMessage;
				break;
			
			case 4:
				returnMessage = "00" + returnMessage;
				break;
			
			case 5:
				returnMessage = "0" + returnMessage;
				break;
				
			case 6:
				break;
			
			case 7:
				LogUtility.getPumpALogger().error(
						"# [TransSomoSelf] # Warning : Unsupported base price length #");
				returnMessage = returnMessage.substring(1);
				break;
			
			case 8:
				LogUtility.getPumpALogger().error(
						"# [TransSomoSelf] # Warning : Unsupported base price length #");
				returnMessage = returnMessage.substring(2);
				break;

			default:
				LogUtility.getPumpALogger().error(
						"# [TransSomoSelf] # Base Price Length ERROR in " +
						"TransSomoSelf extractPrice #");
				returnMessage = "";
				break;
				
			}	// end switch
			
		}	// end if
		
		return returnMessage;
		
	}	// end extractBasePrice
	
	
	
	/**
	 * 소모 셀프 전문 명령코드 '80'의 데이터 중 
	 * Card Number 데이터를 추출
	 * 
	 * @param message	: 소모 셀프 전문 
	 * @return			: Card Number
	 */
	private String extractCardNumber(byte[] message) throws Exception {
		String returnMessage = "";
		String cardType = this.extractCardType(message);
		
		// 목표 위치까지 존재하는 구분 문자의 갯수 
		int counter     = 0;
		// 구분 문자(0x1C or 0X1F)
		byte separator  = 0x1C;
		
		// SomoSelf 80 CardType 10 : 신용 승인 요청
		if (cardType.equals("10")) {
			counter = 4;
			
		// SomoSelf 80 CardType 11 : 신용 승인 취소 요청
		} else if (cardType.equals("11")) {
			counter = 5;

		// SomoSelf 80 CardType 40 : 보너스 누적 요청 
		} else if (cardType.equals("40")) {
			counter = 5;
		
		// SomoSelf 80 CardType 41 : 보너스 누적 취소 요청 
		} else if (cardType.equals("41")) {
			counter = 6;
			
		// SomoSelf 80 CardType 42 : 보너스 이용 요청 
		} else if (cardType.equals("42")) {
			counter = 5;
			
		// SomoSelf 80 CardType 43 : 보너스 이용 취소 요청 
		} else if (cardType.equals("43")) {
			counter = 6;
			
		} else {
			LogUtility.getPumpALogger().error(
					"# [TransSomoSelf] # " +
					"CardType ERROR in TransSomoSelf extractCardNumber # " +
					"Current cardType : " + cardType + " #");
			
		}	// end if
		
		returnMessage = this.findData(message, counter, separator, 37, 0);
		
		return returnMessage;
		
	}	// end extractCardNumber
	


	/**
	 * 소모 셀프 전문 명령코드 '80'의 데이터 중 
	 * Card Type 데이터를 추출
	 * 
	 * @param message	: 소모 셀프 전문
	 * @return			: Card type
	 */
	private String extractCardType(byte[] message) throws Exception {
		String returnMessage = "";
		
		if (message.length < 29) {
			LogUtility.getPumpALogger().error(
					"# [TransSomoSelf] # " +
					"Message Length ERROR " +
					"in TransSomoSelf extractCardType #");
			
			return null;
			
		}	// end if
		
		byte[] cardType 	= new byte[2];
		cardType[0] 		= message[27];
		cardType[1] 		= message[28];
		
		returnMessage = new String(cardType);
		
		return returnMessage;
		
	}	// end extractCardType
		
	/**
 	 * 소모 셀프 전문 명령코드 '80'의 데이터 중 
	 * Liter 데이터를 추출
	 * 
	 * @param message	: 소모 셀프 전문 
	 * @return			: liter
	 */
	private String extractLiter(byte[] message) throws Exception {
		String returnMessage = "";
		String cardType = this.extractCardType(message);
		
		// 목표 위치까지 존재하는 구분 문자의 갯수 
		int counter     = 0;
		// 구분 문자(0x1C or 0X1F)
		byte separator  = 0;
		
		// SomoSelf 80 CardType 10 : 신용 승인 요청
		if (cardType.equals("10")) {
			return "";
			
		// SomoSelf 80 CardType 11 : 신용 승인 취소 요청
		} else if (cardType.equals("11")) {
			return "";

		// SomoSelf 80 CardType 40 : 보너스 누적 요청 
		} else if (cardType.equals("40")) {
			return "";
		
		// SomoSelf 80 CardType 41 : 보너스 누적 취소 요청 
		} else if (cardType.equals("41")) {
			return "";
			
		// SomoSelf 80 CardType 42 : 보너스 이용 요청 
		} else if (cardType.equals("42")) {
			counter   = 11;
			separator = 0x1C;
			
		// SomoSelf 80 CardType 43 : 보너스 이용 취소 요청 
		} else if (cardType.equals("43")) {
			return "";
			
		} else {
			LogUtility.getPumpALogger().error(
					"# [TransSomoSelf] # " +
					"CardType ERROR in TransSomoSelf extractLiter # " +
					"Current cardType : " + cardType + " #");
			
		}	// end if
		
		int arrayLength   = 0;
		int startPosition = 0;
		int endPosition   = 0;
		
		// Liter의 시작 위치를 검색 
		for (int i = 0; counter > 0 && i < message.length; i++) {
			if (message[i] == separator) {
				startPosition = i + 1;
				counter --;
				
			}	// end inner if
			
		}	// end for
		
		// 시작 위치부터 Liter 데이터 추출
		for (int i = startPosition; i < message.length; i++) {
			if (message[i] == separator) {
				endPosition = i;
				break;
				
			}	// end iner if
				
		}	// end for
		
		arrayLength = endPosition - startPosition;
		
		// Liter값의 길이(최소 3, 최대 8) 검사 
		if (arrayLength > 8 || arrayLength < 3) {
			LogUtility.getPumpALogger().error(
					"# [TransSomoSelf] # Liter Data Size Error #");
			
		} else {
			byte[] tempArray = new byte[arrayLength];
			
			// tempArray[]에 Liter Data 대입 
			for (int i = 0, j = startPosition; i < tempArray.length; i++, j++) {
				tempArray[i] = message[j];
				
			}	// end for
			
			// 소수점 분리(WorkingMessage의 4.3 자리수 맞추기)
			byte[] downerBs 	= new byte[3];
			downerBs[0] 		= tempArray[arrayLength - 2];
			downerBs[1] 		= tempArray[arrayLength - 1];
			downerBs[2] 		= '0';
			
			byte[] upperBs = new byte[arrayLength - 2];
			for (int i = 0; i < upperBs.length; i++) {
				upperBs[i] = tempArray[i];
			}	// end for
			
			String downerString = new String(downerBs);
			String upperString  = new String(upperBs);
			
			int upperLength = upperString.length();
			
			switch (upperLength) {
			case 1:
				upperString = "000" + upperString;
				break;
			
			case 2:
				upperString = "00" + upperString;
				break;
			
			case 3:
				upperString = "0" + upperString;
				break;
				
			case 4:
				break;
			
			case 5:
				LogUtility.getPumpALogger().error(
						"# [TransSomoSelf] # Warning : Unsupported liter length #");
				upperString = upperString.substring(1);
				break;
			
			case 6:
				LogUtility.getPumpALogger().error(
						"# [TransSomoSelf] # Warning : Unsupported liter length #");
				upperString = upperString.substring(2);
				break;

			default:
				LogUtility.getPumpALogger().error(
						"# [TransSomoSelf] # Extract Liter Error #");
				break;
				
			}	// end switch
			
			returnMessage = upperString + downerString;
			
		}	// end if
		
		return returnMessage;
		
	}	// end extractLiter
	
	
	
	/**
 	 * 소모 셀프 전문 명령코드 '80'의 데이터 중 
	 * Price 데이터를 추출
	 * 
	 * @param message	: 소모 셀프 전문 
	 * @return			: Price
	 */
	private String extractPrice(byte[] message) throws Exception {
		String returnMessage = "";
		String cardType = this.extractCardType(message);
		
		// 목표 위치까지 존재하는 구분 문자의 갯수 
		int counter     = 0;
		// 구분 문자(0x1C or 0X1F)
		byte separator  = 0;
		// price의 최대 길이 
		int priceLength = 0;
		
		// SomoSelf 80 CardType 10 : 신용 승인 요청
		if (cardType.equals("10")) {
			counter = 6;
			separator = 0x1C;
			priceLength = 12;
			
		// SomoSelf 80 CardType 11 : 신용 승인 취소 요청
		} else if (cardType.equals("11")) {
			counter = 7;
			separator = 0x1C;
			priceLength = 12;

		// SomoSelf 80 CardType 40 : 보너스 누적 요청 
		} else if (cardType.equals("40")) {
			counter = 7;
			separator = 0x1C;
			priceLength = 12;
		
		// SomoSelf 80 CardType 41 : 보너스 누적 취소 요청 
		} else if (cardType.equals("41")) {
			counter = 8;
			separator = 0x1C;
			priceLength = 12;
			
		// SomoSelf 80 CardType 42 : 보너스 이용 요청 
		} else if (cardType.equals("42")) {
			counter = 7;
			separator = 0x1F;
			priceLength = 10;	
			
		// SomoSelf 80 CardType 43 : 보너스 이용 취소 요청 
		} else if (cardType.equals("43")) {
			return "";
			
		} else {
			LogUtility.getPumpALogger().error(
					"# [TransSomoSelf] # " +
					"CardType ERROR in TransSomoSelf extractPrice # " +
					"Current CardType : " + cardType + " #");
			
		}	// end if
			
		returnMessage = this.findData(message, counter, 
									separator, priceLength, 0);
		
		if (returnMessage != null) {
			// 8 자리수 맞추기 
			int priceLengthI = returnMessage.length();
			
			switch (priceLengthI) {
			case 1:
				returnMessage = "0000000" + returnMessage;
				break;
				
			case 2:
				returnMessage = "000000" + returnMessage;
				break;
				
			case 3:
				returnMessage = "00000" + returnMessage;
				break;
				
			case 4:
				returnMessage = "0000" + returnMessage;
				break;
				
			case 5:
				returnMessage = "000" + returnMessage;
				break;
				
			case 6:
				returnMessage = "00" + returnMessage;
				break;
				
			case 7:
				returnMessage = "0" + returnMessage;
				break;
				
			case 8:
				break;
				
			case 9:
				LogUtility.getPumpALogger().error(
						"# [TransSomoSelf] # Warning : Unsupported Price length #");
				returnMessage = returnMessage.substring(1);
				break;
				
			case 10:
				LogUtility.getPumpALogger().error(
						"# [TransSomoSelf] # Warning : Unsupported Price length #");
				returnMessage = returnMessage.substring(2);
				break;
				
			case 11:
				LogUtility.getPumpALogger().error(
						"# [TransSomoSelf] # Warning : Unsupported Price length #");
				returnMessage = returnMessage.substring(3);
				break;
				
			case 12:
				LogUtility.getPumpALogger().error(
						"# [TransSomoSelf] # Warning : Unsupported Price length #");
				returnMessage = returnMessage.substring(4);
				break;
				
			default:
				LogUtility.getPumpALogger().error(
						"# [TransSomoSelf] # PriceLength ERROR in TransSomoSelf extractPrice #");
				returnMessage = "";
				break;
			
			}	// end switch
			
		}	// end if

		return returnMessage;
		
	}	// end extractPrice
		
	/**
	 * 구분자(separator)를 포함한 byte 배열 타입의 데이터에서 구분자가 
	 * 특정 횟수(counter)만큼 지난 위치에 있는 데이터를 얻는다.
	 * 
	 * @param message		: 구분자를 포함한 데이터
	 * @param counter		: 구분자의 횟수
	 * @param separator		: 구분자 
	 * @param maxLength		: 데이터의 최대 길이 
	 * @param minLength		: 데이터의 최소 길이 
	 * @return
	 */
	private String findData(byte[] message, int counter, byte separator, 
												int maxLength, int minLength) throws Exception {
		String returnMessage = "";
		
		int arrayLength   = 0;
		int startPosition = 0;
		int endPosition   = 0;
			
		// 시작 위치를 검색 
		for (int i = 0; counter > 0 && i < message.length; i++) {
			if (message[i] == separator) {
				startPosition = i + 1;
				counter --;
				
			}	// end inner if
			
		}	// end for

		// 시작 위치부터 data 추출 
		for (int i = startPosition; i < message.length; i++) {
			if (message[i] == separator) {
				endPosition = i;
				break;
				
			}	// end iner if
				
		}	// end for
			
		arrayLength = endPosition - startPosition;
			
		// 값 범위 검사
		if (arrayLength > maxLength || arrayLength <= minLength) {
			LogUtility.getPumpALogger().error(
					"# [TransSomoSelf] # Length ERROR in TransSomoSelf extract data. #");
										
		} else {
			byte[] tempArray = new byte[arrayLength];
				
			for (int i = 0, j = startPosition; i < tempArray.length; i++, j++) {
				tempArray[i] = message[j];
				
			}	// end for
				
			returnMessage = new String(tempArray);
			
		}	// end if
		
		return returnMessage;
		
	}	// end findData	
	
	/**
	 * WorkingMessage를 소모 셀프 전문으로 변환한다.
	 * 
	 * @param workingMessage 	: WorkingMessage
	 * @return					: 소모 셀프 전문 
	 */
	public byte[] generateByteStream(WorkingMessage workingMessage) throws Exception {
		byte[] returnMessage = null;
		
		String workingMessageCommand = workingMessage.getCommand();
		// 노즐번호를 구한다(ODT 번호는 NozzleNo, 노즐 번호는 ConnectNozzleNo) 
		String connectNozzleNo = this.getConnectNozzleNo(workingMessage);
		
		if (workingMessageCommand.equals(IPumpConstant.COMMANDID_HD)) {
			// WorkingMessage HD : 외상고객 승인응답
			// SomoSelf 52       : VAN, TMS 승인 응답정보 
			HD_WorkingMessage hdWorkingMessage = 
				(HD_WorkingMessage) workingMessage;
			
			DataStruct ss52DS = new DataStruct();
			ss52DS.addString("command", "52", 2);
			ss52DS.addString("orderNo", "1", 1);
			ss52DS.addString("flag", "2", 1);												// 플래그
			ss52DS.addString("nozzleNo", connectNozzleNo, 2);			// 노즐번호
			ss52DS.addString("length", "0114", 4);									// 길이
			ss52DS.addString("cardNo", 
					hdWorkingMessage.getSerialNo(), 18);							// 카드번호
			ss52DS.addString("carInfo", 
					hdWorkingMessage.getCarNo(), 18);								// 차량정보
			ss52DS.addString("oilCode", 
								hdWorkingMessage.getProductCode(), 4);		// 유종코드 
			ss52DS.addString("name", 
					hdWorkingMessage.getDriverName(), 26);					// 운전자명
			ss52DS.addString("liter", 
					hdWorkingMessage.getSaleLiter(), 7);							// 판매수량
			ss52DS.addString("basePrice", 
					hdWorkingMessage.getBasePrice(), 6);							// 판매단가
			ss52DS.addString("jpLiter", 
					hdWorkingMessage.getReceiptLiter(), 7);						// 전표량
			ss52DS.addString("transType", 
					hdWorkingMessage.getTransType(), 1);							// 거래종류
			ss52DS.addString("customerType", 
					hdWorkingMessage.getCusType(), 1);							// 고객종류
			ss52DS.addString("transStatus", 
					hdWorkingMessage.getTransStatus(), 1);						// 거래상태
			ss52DS.addString("printBase", 
					hdWorkingMessage.getPrintBase(), 1);							// 단가출력여부
			ss52DS.addString("deposite", 
					hdWorkingMessage.getDepositST(), 1);							// 보관증발행여부
			ss52DS.addString("float", 
					hdWorkingMessage.getFloatTR(), 1);								// 소수점처리방식
			ss52DS.addString("receipt", 
					hdWorkingMessage.getReceiptType(), 1);						// 계산서거래종류
			ss52DS.addString("monthLimit", 
					hdWorkingMessage.getMonthLimit(), 10);					// 한도수량
			ss52DS.addString("saveLimit", 
					hdWorkingMessage.getSaveLimit(), 10);						// 사용량
			ss52DS.addString("limitType", 
					hdWorkingMessage.getLimitType(), 1);							// 수량,금액 
			
			byte[] tempArray = ss52DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
								hdWorkingMessage.getNozzleNo());
		
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_P1)) {
			// WorkingMessage P1 : 셀프 주유기 사업자 정보 설정
			// SomoSelf 02       : 환경설정 정보 
			P1_WorkingMessage p1WorkingMessage = 
								(P1_WorkingMessage) workingMessage;
			
			String storeName 	= p1WorkingMessage.getStoreName();
			String repName 		= p1WorkingMessage.getRepName();
			String storeAdd1 	= p1WorkingMessage.getStoreADDR1();
			String storeAdd2 	= p1WorkingMessage.getStoreADDR2();
			String phone 		= p1WorkingMessage.getTel();
			
			DataStruct ss02_01DS = new DataStruct();
			ss02_01DS.addString("command", "02", 2);
			ss02_01DS.addString("orderNo", "1", 1);
			ss02_01DS.addString("block", "2", 1);
			ss02_01DS.addString("fileNo", "01", 2);
			ss02_01DS.addString("fileLength", "235", 3);
			ss02_01DS.addString("storeCode", 
					p1WorkingMessage.getStoreCord(), 10);
			ss02_01DS.addString("regiNo", 
					p1WorkingMessage.getStoreRegiNum(), 12);
			ss02_01DS.addString("storeName", 
					this.subStringCheck(storeName, 40), 40);
			ss02_01DS.addString("repName", 
					this.subStringCheck(repName, 30), 30);
			ss02_01DS.addString("storePost", 
					p1WorkingMessage.getStorePost(), 7);
			ss02_01DS.addString("storeAdd1", 
					this.subStringCheck(storeAdd1, 50), 50);
			ss02_01DS.addString("storeAdd2", 
					this.subStringCheck(storeAdd2, 50), 50);
			ss02_01DS.addString("phone", this.subStringCheck(phone, 16), 16);
			ss02_01DS.addString("saMinAmt", 
					"00000" + p1WorkingMessage.getSaMinAmt(), 10);
			ss02_01DS.addString("bonusStoreCode", this.generateBlank(10), 10);
			
			byte[] tempArray = ss02_01DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
					p1WorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_P2)) {
			// WorkingMessage P2 : 셀프 주유기 머리말 / 꼬리말 설정 
			// SomoSelf 02       : 환경설정 정보
			P2_WorkingMessage p2WorkingMessage = 
						(P2_WorkingMessage) workingMessage;
			
			DataStruct ss02_02DS = new DataStruct();
			ss02_02DS.addString("command", "02", 2);
			ss02_02DS.addString("orderNo", "1", 1);
			ss02_02DS.addString("block", "2", 1);
			ss02_02DS.addString("fileNo", "02", 2);
			ss02_02DS.addString("fileLength", "000", 3);
			ss02_02DS.addString("head", 
					p2WorkingMessage.getBaseHeadTitle(), 50);
			ss02_02DS.addString("tail1", 
					p2WorkingMessage.getBaseFootTitle1(), 50);
			ss02_02DS.addString("tail2", 
					p2WorkingMessage.getBaseFootTitle2(), 50);
			
			byte[] tempArray = ss02_02DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
					p2WorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_P6)) {
			// WorkingMessage P6 : 영업일 및 시간 설정
			// SomoSelf 22       : 시각설정 지시 
			P6_WorkingMessage p6WorkingMessage = 
							(P6_WorkingMessage) workingMessage;
			
			String date = p6WorkingMessage.getSystemTime();
			
			DataStruct ss22DS = new DataStruct();
			
			ss22DS.addString("command", "22", 2);
			ss22DS.addString("orderNo", "1", 1);
			ss22DS.addString("date", "20" + date, 14);
			
			byte[] tempArray = ss22DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
								p6WorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_P5_1)) {
			// WorkingMessage P5_1 : ODT 환경정보 설정 
			// SomoSelf 02         : 환경설정 정보
			P5_1_WorkingMessage p5WorkingMessage = 
							(P5_1_WorkingMessage) workingMessage;
			
			Vector<P5_NozzleInfo> nozzleInfoVector = 
							p5WorkingMessage.getNozzleInfo();
			
			DataStruct ss02DS = new DataStruct();
			
			ss02DS.addString("command", "02", 2);
			ss02DS.addString("orderNo", "1", 1);
			ss02DS.addString("block", "2", 1);
			ss02DS.addString("fileNo", "05", 2);
			ss02DS.addString("fileLength", "096", 3);

			for (int i = 0; i < nozzleInfoVector.size(); i++) {
				P5_NozzleInfo nozzleInfo = nozzleInfoVector.get(i);

				String nozzleNo  	= nozzleInfo.getNozzleNumber();
				int nozzleNoInt		= Change.toValue(nozzleNo);
				String basePrice 	= nozzleInfo.getBasePrice();
				String goodsCode 	= nozzleInfo.getGoodsCode();
				String goodsType 	= nozzleInfo.getGoodsType();
				
				ss02DS.addString("nozzleNo"  + i, 
									Change.toString("%02d", nozzleNoInt - 1), 2);
				ss02DS.addString("basePrice" + i, 
									this.subStringCheck(basePrice, 4), 4);
				ss02DS.addString("goodsCode" + i, 
									this.subStringCheck(goodsCode, 4), 4);
				ss02DS.addString("goodsType" + i, 
									this.subStringCheck(goodsType, 14), 14);
				
			}	// end for
			
			// 공백은 'Z'로 채운다 
			int zCharBlankSize = 96 - (nozzleInfoVector.size() * 24);
			ss02DS.addString("blank", 
					this.generateBlank(zCharBlankSize, "Z"), zCharBlankSize);

			byte[] tempArray = ss02DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
								p5WorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PE)) {
			// WorkingMessage PE : 주유기 / 충전기 상태 요청
			// SomoSelf 95       : 주유기 오류상태 요청 
			PE_WorkingMessage peWorkingMessage = 
							(PE_WorkingMessage) workingMessage;
			
			DataStruct ss95DS = new DataStruct();
			
			ss95DS.addString("command", "95", 2);
			ss95DS.addString("orderNo", "1", 1);
			ss95DS.addString("nozzleNo", connectNozzleNo, 2);
			
			byte[] tempArray = ss95DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
								peWorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PA)) {
			// WorkingMessage PA : 노즐제어명령
			// SomoSelf 50       : 주유기 상태 
			PA_WorkingMessage paWorkingMessage = 
							(PA_WorkingMessage) workingMessage;
			String state = paWorkingMessage.getNozzleState();
			
			DataStruct ss50DS = new DataStruct();
			
			ss50DS.addString("command", "50", 2);
			ss50DS.addString("flag", "0", 1);
			ss50DS.addString("nozzleNo", connectNozzleNo, 2);
			
			// PA State 0 : 주유(충전) 금지 
			if (state.equals("0")) {
				ss50DS.addString("nozzleState", "70", 2);
				
			// PA State 1 : 주유(충전) 금지 해제 
			} else if (state.equals("1")) {
				ss50DS.addString("nozzleState", "80", 2);
				
			} else {
				LogUtility.getPumpALogger().error(
						new StringBuffer("# [TransSomoSelf] # ")
						.append("PA WorkingMessage state Error # ")
						.append("Current state : ").append(  state ).append(" # ").toString());
				
			}	// end inner if
			
			byte[] tempArray = ss50DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
								paWorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_QD)) {
			// WorkingMessage QD : 보너스 매장 점수 
			// SomoSelf 56       : 주유소 매장 점수 
			QD_WorkingMessage qdWorkingMessage = 
							(QD_WorkingMessage) workingMessage;
			
			DataStruct ss56DS = new DataStruct();
			
			ss56DS.addString("command", "56", 2);
			ss56DS.addString("nozzleNo", connectNozzleNo, 2);
			ss56DS.addString("bonusCardNumber", 
							qdWorkingMessage.getBonusCardNum(), 16);
			ss56DS.addString("accScore", qdWorkingMessage.getAccScore(), 8);
			
			byte[] tempArray = ss56DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
							qdWorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PB)) {
			// WorkingMessage PB : 정액 / 정량 설정
			// SomoSelf 55       : 선 결제 지시 
			PB_WorkingMessage pbWorkingMessage = 
							(PB_WorkingMessage) workingMessage;
			
			DataStruct ss55DS = new DataStruct();
			
			ss55DS.addString("command", "55", 2);
			ss55DS.addString("orderNo", "1", 1);
			ss55DS.addString("nozzleNo", connectNozzleNo, 2);
			ss55DS.addString("mode", pbWorkingMessage.getCommandSet(), 1);
			ss55DS.addString("liter", pbWorkingMessage.getLiter(), 7);
			ss55DS.addString("basePrice", 
						pbWorkingMessage.getBasePrice().substring(0, 4), 4);
			ss55DS.addString("price", 
						pbWorkingMessage.getPrice().substring(2), 6);
			
			byte[] tempArray = ss55DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
							pbWorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_S3)) {
			// WorkingMessage S3 : 주유/충전 중 자료전송(Process 202에서 해당 ODT로 중계 시 사용)
			// SomoSelf 51       : 주유 중, 주유 완료 
			S3_WorkingMessage s3WorkingMessage = (S3_WorkingMessage) workingMessage;
			
			DataStruct ss51DS = new DataStruct();
			
			ss51DS.addString("command", "51", 2);
			ss51DS.addString("orderNo", "1", 1);
			ss51DS.addString("nozzleNo", connectNozzleNo, 2);
			ss51DS.addString("state", "4", 1);
			ss51DS.addString("liter", s3WorkingMessage.getLiter(), 7);
			ss51DS.addString("basePrice", 
						s3WorkingMessage.getBasePrice().substring(0, 4), 4);
			ss51DS.addString("price", s3WorkingMessage.getPrice(), 6);
			
			byte[] tempArray = ss51DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
						s3WorkingMessage.getNozzleNo());
		
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_S4)) {
			// WorkingMessage S4 : 주유완료 자료전송(Process 203에서 해당 ODT로 중계 시 사용)
			// SomoSelf 51       : 주유 중, 주유 완료 
			S4_WorkingMessage s4WorkingMessage = 
						(S4_WorkingMessage) workingMessage;
			
			DataStruct ss51DS = new DataStruct();
			
			ss51DS.addString("command", "51", 2);
			ss51DS.addString("orderNo", "1", 1);
			ss51DS.addString("nozzleNo", connectNozzleNo, 2);
			ss51DS.addString("state", "5", 1);
			ss51DS.addString("liter", s4WorkingMessage.getLiter(), 7);
			ss51DS.addString("basePrice", 
						s4WorkingMessage.getBasePrice().substring(0, 4), 4);
			ss51DS.addString("price", 
						s4WorkingMessage.getPrice().substring(2), 6);
			
			byte[] tempArray = ss51DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
						s4WorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_S8)) {
			// WorkingMessage S8 : 주유기 / 충전기 상태 전송 
			// SomoSelf 50       : 주유기 상태 
			S8_WorkingMessage s8WorkingMessage = (S8_WorkingMessage) workingMessage;
			
			DataStruct ss50DS = new DataStruct();
			
			ss50DS.addString("command", "50", 2);
			ss50DS.addString("flag", s8WorkingMessage.getStatus(), 1);
			ss50DS.addString("nozzleNo", connectNozzleNo, 2);
			
			String status = s8WorkingMessage.getStatusCode();
			
			// S8 Status 651 : 대기(노즐다운)
			if (status.equals("651")) {
				ss50DS.addString("state", "20", 2);
				
			// S8 Status 652 : 노즐업
			} else if (status.equals("652")) {
				ss50DS.addString("state", "30", 2);
				
			// S8 Status 653 : 주유 중
			} else if (status.equals("653")) {
				ss50DS.addString("state", "40", 2);
				
			// S8 Status 654 : 주유완료 
			} else if (status.equals("654")) {
				ss50DS.addString("state", "50", 2);
				
			// S8 Status 601 : 통신회선 불량
			} else if (status.equals("601")) {
				ss50DS.addString("state", "60", 2);
				
			// S8 Status 656 : 주유금지(비상정지)
			} else if (status.equals("656")) {
				ss50DS.addString("state", "70", 2);
				
			// S8 Status 657 : 비상해제
			} else if (status.equals("657")) {
				ss50DS.addString("state", "80", 2);
				
			} else {
				LogUtility.getPumpALogger().error(
						"# [TransSomoSelf] # " +
						"Incorrect status code in Somo Self # " +
						"Current status : " + status + " #");
				
			}	// end inner if
			
			byte[] tempArray = ss50DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
						s8WorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_HC)) {
			// WorkingMessage HC : 소모셀프 카드결제 승인처리
			// SomoSelf 92       : 승인결과 완료 통지(주유허가)
			HC_WorkingMessageCopy hcWorkingMessage = (HC_WorkingMessageCopy) workingMessage;
			
			DataStruct ss92DS = new DataStruct();
			
			ss92DS.addString("command", "92", 2);
			ss92DS.addString("orderNo", "1", 1);
			ss92DS.addString("nozzleNo", connectNozzleNo, 2);	// 노즐번호
			ss92DS.addString("mode", 
					hcWorkingMessage.getMode(), 1);						// Mode
			ss92DS.addString("approvalType", 
					hcWorkingMessage.getTrType(), 2);						// 결제유형
			ss92DS.addString("bonusType", 
					hcWorkingMessage.getBonusCardType(), 2);		// 보너스카드종류
			ss92DS.addString("bonusCode", 
					hcWorkingMessage.getBonusAuthCode(), 5);		// 보너스카드승인코드
			ss92DS.addString("authInfo", 
					hcWorkingMessage.getAuthInfo(), 34);					// 승인정보
			ss92DS.addString("liter", 
					hcWorkingMessage.getLiter(), 7);							// 수량
			ss92DS.addString("basePrice", 
					hcWorkingMessage.getBasePrice().substring(0, 4), 4);	// 단가
			ss92DS.addString("price", 
					hcWorkingMessage.getPrice(), 8);							// 금액
			ss92DS.addString("authTime", 
					hcWorkingMessage.getAuthTime(), 14);				// 승인시각
			ss92DS.addString("authNo", 
					hcWorkingMessage.getAuthNo(), 12);					// 승인번호
			ss92DS.addString("cardNo", 
					hcWorkingMessage.getCardNo(), 16);					// 카드번호
			ss92DS.addString("cardCorpCode", 
					hcWorkingMessage.getCardCorpNumber(), 4);	// 발급사코드번호
			ss92DS.addString("cardCorpName", 
					hcWorkingMessage.getCardCorpName(), 16);		// 발급사명
			ss92DS.addString("noteCode", 
					hcWorkingMessage.getNoteCorpCode(), 4);			// 매입사코드번호
			ss92DS.addString("noteName", 
					hcWorkingMessage.getNoteCorpName(), 16);		// 매입사명
			ss92DS.addString("noteNo", 
					hcWorkingMessage.getNoteNumber(), 5);			// 전표번호
			ss92DS.addString("bonusNo", 
					hcWorkingMessage.getBonusCardNumber(), 16);	// 보너스카드번호
			ss92DS.addString("bonusTime", 
					hcWorkingMessage.getBonusAuthTime(), 14);			// 보너스승인시각
			ss92DS.addString("bonusAuthNo", 
					hcWorkingMessage.getBonusAuthNumber(), 12);	// 보너스승인번호
			ss92DS.addString("dAuthNo", 
					hcWorkingMessage.getDAuthNumber(), 12);			// 현금영수증승인번호
			ss92DS.addString("bonusGenerateScore", 
					hcWorkingMessage.getGenerateScore(), 8);				// 보너스발생점수
			ss92DS.addString("bonusScore", 
					hcWorkingMessage.getScore(), 8);								// 보너스가용점수
			ss92DS.addString("bonusTotalScore", 
					hcWorkingMessage.getTotalScore(), 8);						// 보너스총누적점수
			ss92DS.addString("message", 
					hcWorkingMessage.getPublicMsg(), 100);				// 메시지	
			ss92DS.addString("VanMessage", 
					hcWorkingMessage.getVanMsg(), 100);					// Van사 메시지 
			ss92DS.addString("BonusMessage", 
					hcWorkingMessage.getBnsMsg(), 100);					// 보너스 메시지 
			
			byte[] tempArray = ss92DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
								hcWorkingMessage.getNozzleNo());
			
		} else {
			LogUtility.getPumpALogger().error(
					"# [TransSomoSelf] # " +
					"Not Supported command(" + 
					workingMessageCommand +
					") in TransSomoSelf #");
			returnMessage = null;
		
		}	// end if
		
		return returnMessage;
		
	}	// end genearateByteArray
	
	
	/**
	 * 소모 셀프 전문을 WorkingMessage로 변환한다.
	 * 
	 * @param message	: 소모셀프 전문
	 * @param command	: WorkingMessage Command
	 * @return			: WorkingMessage 
	 */
	public WorkingMessage generateWorkingMessage(byte[] message, 
													String command) throws Exception {
		WorkingMessage returnMessage = null;
		
		// byte[]에 있는 명령코드를 얻는다
		String somoCommandString = this.getCommand(message);
		// byte[]의 ODT번호를 얻는다
		String nozzleNo = this.getNozzleNo(message);
		
		if (somoCommandString.equals("51")) {
			// SomoSelf 51 : 주유 중, 주유 완료
			// WorkingMessage S3 : 주유/충전 중 자료전송
			// WorkingMessage S4 : 주유소 주유완료 자료전송
			DataStruct s51Interface = SomoSelfDS.getDS("51");
			s51Interface.setByteStream(message);
			
			String state = (String) s51Interface.getValue("state");
			
			if (state.equals("4")) {
				// SomoSelf 51 State 4 : 주유 중 
				S3_WorkingMessage s3WorkingMessage = new S3_WorkingMessage();
				s3WorkingMessage.setNozzleNo(nozzleNo);
				s3WorkingMessage.setLiter((String) s51Interface.getValue("liter"));
				s3WorkingMessage.setPrice((String) s51Interface.getValue("price"));
				s3WorkingMessage.setBasePrice(
							(String) s51Interface.getValue("basePrice") + "00");
				s3WorkingMessage.setWDate(this.getSystemTime(6));	 
				
				returnMessage = s3WorkingMessage;
				
			} else if (state.equals("5")) {
				// SomoSelf 51 State 5 : 주유 완료  
				S4_WorkingMessage s4WorkingMessage = new S4_WorkingMessage();
				s4WorkingMessage.setNozzleNo(nozzleNo);
				s4WorkingMessage.setFlag("0");		// 임의 값(FLAG)
				s4WorkingMessage.setLiter(
						(String) s51Interface.getValue("liter"));
				s4WorkingMessage.setBasePrice(
						(String) s51Interface.getValue("basePrice") + "00");
				s4WorkingMessage.setPrice("00" + 
						(String) s51Interface.getValue("price"));
				s4WorkingMessage.setWDate(this.getSystemTime(6));
				s4WorkingMessage.setSystemTime(this.getSystemTime(12));
				s4WorkingMessage.setTotalGauge(this.generateBlank(10));
				s4WorkingMessage.setStatusFlag("0");	// 임의 값(STATUS_FLAG)
				
				returnMessage = s4WorkingMessage;
				
			} else {
				LogUtility.getPumpALogger().error(
						"### [TransSomoSelf] ###\n" +
						"### Incorrect state in 51 Somo Self ###\n" +
						"### Current state : " + state + "   ###");
				returnMessage = null;
					
			}	// end inner if
			
		} else 	if (somoCommandString.equals("80")) {
			// SomoSelf 80 : 입력 오더 응답 
			// WorkingMessage HA : 소모셀프 카드결제 승인처리
			// WorkingMessage HB : 소모셀프 외상거래 승인처리
			DataStruct s80Interface = 
						SomoSelfDS.getDS("80", SomoSelfDS.CARD);
			s80Interface.setByteStream(message);
				
			String flag 			= (String) s80Interface.getValue("flag");
			String connectNozzleNo 	= (String) s80Interface.getValue("nozzleNo");
				
			// SomoSelf 80 Flag 0 : 신용승인 
			if (flag.equals("0")) {
				HA_WorkingMessageCopy haWorkingMessage = new HA_WorkingMessageCopy();
				haWorkingMessage.setNozzleNo(nozzleNo);
				haWorkingMessage.setConnectNozzleNo(
										this.getPlusOne(connectNozzleNo));
				
				String preYear 	= this.getSystemTime(2);
				String cardType = this.extractCardType(message);
				
				// SomoSelf 80 CardType 10 : 신용카드 승인 요청 
				if (cardType.equals("10")) {
					haWorkingMessage.setCardType("1");
					haWorkingMessage.setCardNumber(
										this.extractCardNumber(message));
				
				// SomoSelf 80 CardType 11 : 신용카드 승인 취소 
				} else if (cardType.equals("11")) {
					String cDate   = this.findData(message, 10, 
													(byte) 0x1C, 6, 0);
					String cNumber = this.findData(message, 11, 
													(byte) 0x1C, 12, 0);
					
					haWorkingMessage.setCardType("2");
					haWorkingMessage.setCardNumber(
										this.extractCardNumber(message));
					haWorkingMessage.setCDate(preYear + cDate + "000000");
					haWorkingMessage.setCNumber(cNumber);
					
				// SomoSelf 80 CardType 42 : 보너스 이용 요청 
				} else if (cardType.equals("42")) {
					haWorkingMessage.setCardType("3");
					haWorkingMessage.setBonusCard(
							this.extractCardNumber(message));
					haWorkingMessage.setPin(this.findData(message, 7, 
													(byte) 0x1C, 16, 0));
					
				// SomoSelf 80 CardType 43 : 보너스 이용 취소
				} else if (cardType.equals("43")) {
					haWorkingMessage.setCardType("4");
					haWorkingMessage.setBonusCard(
							this.extractCardNumber(message));
					
				// SomoSelf 80 CardType 40 : 보너스 누적 요청 
				} else if (cardType.equals("40")) {
					haWorkingMessage.setCardType("5");
					haWorkingMessage.setBonusCard(
							this.extractCardNumber(message));
					
				// SomoSelf 80 CardType 41 : 보너스 누적 취소
				} else if (cardType.equals("41")) {
					String cDate   = this.findData(message,  9, 
												(byte) 0x1C,  6, 0);
					String cNumber = this.findData(message, 10, 
												(byte) 0x1C, 12, 0);
					
					haWorkingMessage.setCardType("6");
					haWorkingMessage.setBonusCard(
							this.extractCardNumber(message));
					haWorkingMessage.setCDate(preYear + cDate + "000000");
					haWorkingMessage.setCNumber(cNumber);
					
				} else {
					LogUtility.getPumpALogger().error(
							"# [TransSomoSelf] # " +
							"Incorrect card type # " +
							"Current card type : " + cardType + " #");
					
				}	// end if
				
				haWorkingMessage.setLiter(this.extractLiter(message));
				haWorkingMessage.setBasePrice(
						this.extractBasePrice(message) + "00");
				haWorkingMessage.setPrice(this.extractPrice(message));
				
				returnMessage = haWorkingMessage;

			// SomoSelf 80 Flag 2 : 외상거래  
			} else if(flag.equals("2")) {
				String cardType = (String) s80Interface.getValue("cardType");
				
				HB_WorkingMessageCopy hbWorkingMessage = new HB_WorkingMessageCopy();
				hbWorkingMessage.setNozzleNo(nozzleNo);
				hbWorkingMessage.setConnectNozzleNo(
										this.getPlusOne(connectNozzleNo));
				hbWorkingMessage.setCardType(cardType);
				hbWorkingMessage.setCardNumber(
						(String) s80Interface.getValue("cardNo"));
				hbWorkingMessage.setBonusCard(
						(String) s80Interface.getValue("bonusCard"));
				hbWorkingMessage.setLiter(
						(String) s80Interface.getValue("liter"));
				hbWorkingMessage.setBasePrice(
						(String) s80Interface.getValue("basePrice") + "00");
				hbWorkingMessage.setPrice(
						(String) s80Interface.getValue("price"));
					
				if (cardType.equals("1")) {
					// 승인 요청 시 NULL 처리
					hbWorkingMessage.setCDate  (this.generateBlank(14));
					hbWorkingMessage.setCNumber(this.generateBlank(12));
					hbWorkingMessage.setBDate  (this.generateBlank(14));
					hbWorkingMessage.setBNumber(this.generateBlank(12));
					hbWorkingMessage.setDNumber(this.generateBlank(12));
						
				} else {
					hbWorkingMessage.setCDate  (
							(String) s80Interface.getValue("cDate"));
					hbWorkingMessage.setCNumber(
							(String) s80Interface.getValue("cNumber"));
					hbWorkingMessage.setBDate  (
							(String) s80Interface.getValue("bDate"));
					hbWorkingMessage.setBNumber(
							(String) s80Interface.getValue("bonusNo"));
					hbWorkingMessage.setDNumber(
							(String) s80Interface.getValue("dNo"));
						
				}	
					
				returnMessage = hbWorkingMessage;
					
			} else {
				LogUtility.getPumpALogger().error(
						"# [TransSomoSelf] # " +
						"Incorrect flag in 80 Somo Self # " +
						"Current flag : " + flag + " #");
				returnMessage = null;
				
			}	// end inner if
			
		} else if (somoCommandString.equals("50")) {
			// SomoSelf 50 		 : 주유기 상태 
			// WorkingMessage PA : 노즐 제어 명령
			PA_WorkingMessage paWorkingMessage = new PA_WorkingMessage();
			paWorkingMessage.setNozzleNo(nozzleNo);
			
			DataStruct s50Interface = SomoSelfDS.getDS("50");
			s50Interface.setByteStream(message);
				
			String state = (String) s50Interface.getValue("state");
				
			// SomoSelf 50 State 70 : 주유금지(비상정지)
			if (state.equals("70")) {
				paWorkingMessage.setNozzleState("0");					
				
			// SomoSelf 50 State 80 : 비상해제 
			} else if (state.equals("80")) {
				paWorkingMessage.setNozzleState("1");					
				
			} else {
				LogUtility.getPumpALogger().error(
						"# [TransSomoSelf] # " +
						"Incorrect state in '50' Somo Self protocol # " +
						"Current state : " + state + " #");
				
			}	// end inner if
				
			returnMessage = paWorkingMessage;
			
		} else if (somoCommandString.equals("92")) {
			// SomoSelf 92 		 : 승인결과 완료 통지(주유허가)
			// WorkingMessage HC : 소모셀프 카드결제 승인처리
			HC_WorkingMessageCopy hcWorkingMessage = new HC_WorkingMessageCopy();
			hcWorkingMessage.setNozzleNo(nozzleNo);
			
			DataStruct s92Interface = SomoSelfDS.getDS("92");
			s92Interface.setByteStream(message);
			
			String connectNozzleNo = (String) s92Interface.getValue("nozzleNo");
			
			hcWorkingMessage.setConnectNozzleNo(
					this.getPlusOne(connectNozzleNo));
			hcWorkingMessage.setMode((String) s92Interface.getValue("mode"));
			hcWorkingMessage.setDirection(
					IPumpConstant.DIRECTION_FROM_ADAPTOR);				// 전송 방향 
			hcWorkingMessage.setTrType(
					(String) s92Interface.getValue("approvalType"));				// 결제유형구분 
			hcWorkingMessage.setBonusCardType(
					(String) s92Interface.getValue("bonusType"));					// 보너스카드종류 
			hcWorkingMessage.setBonusAuthCode(
					(String) s92Interface.getValue("bonusCode"));					// 보너스승인코드
			hcWorkingMessage.setAuthInfo(
					(String) s92Interface.getValue("authInfo"));						// 승인정보
			hcWorkingMessage.setLiter(
					(String) s92Interface.getValue("liter"));								// 수량
			hcWorkingMessage.setBasePrice(
					(String) s92Interface.getValue("basePrice") + "00");			// 단가
			hcWorkingMessage.setPrice(
					(String) s92Interface.getValue("price"));								// 금액
			hcWorkingMessage.setAuthTime(
					(String) s92Interface.getValue("authTime"));						// 승인시각
			hcWorkingMessage.setAuthNo(
					(String) s92Interface.getValue("authNo"));							// 승인번호
			hcWorkingMessage.setCardNo(
					(String) s92Interface.getValue("cardNo"));							// 카드번호 
			hcWorkingMessage.setCardCorpNumber(
					(String) s92Interface.getValue("cardCorpCode"));				// 카드사번호
			hcWorkingMessage.setCardCorpName(
					(String) s92Interface.getValue("cardCorpName"));			// 카드사명
			hcWorkingMessage.setNoteCorpCode(
					(String) s92Interface.getValue("noteCode"));						// 전표매입사 코드
			hcWorkingMessage.setNoteCorpName(
					(String) s92Interface.getValue("noteName"));					// 전표매입사명
			hcWorkingMessage.setNoteNumber(
					(String) s92Interface.getValue("noteNo"));							// 전표번호
			hcWorkingMessage.setBonusCardNumber(
					(String) s92Interface.getValue("bonusNo"));						// 보너스카드번호
			hcWorkingMessage.setBonusAuthTime(
					(String) s92Interface.getValue("bonusTime"));					// 보너스 승인시각
			hcWorkingMessage.setBonusAuthNumber(
					(String) s92Interface.getValue("bonusAuthNo"));				// 보너스 승인번호
			hcWorkingMessage.setDAuthNumber(
					(String) s92Interface.getValue("dAuthNo")); 						// 현금영수증 승인번호
			hcWorkingMessage.setGenerateScore(
					(String) s92Interface.getValue("bonusGenerateScore"));	// 발생점수
			hcWorkingMessage.setScore(
					(String) s92Interface.getValue("bonusScore"));					// 가용점수
			hcWorkingMessage.setTotalScore(
					(String) s92Interface.getValue("bonusTotalScore"));			// 총누적점수
			hcWorkingMessage.setPublicMsg(
					(String) s92Interface.getValue("message"));						// 메시지
			hcWorkingMessage.setVanMsg(
					(String) s92Interface.getValue("vanMessage"));					// 밴사메시지
			hcWorkingMessage.setBnsMsg(
					(String) s92Interface.getValue("bonusMessage"));			// 보너스메시지

			returnMessage = hcWorkingMessage;

		} else if (somoCommandString.equals("81")) {
			// SomoSelf 81 		 : 디바이스 에러 통지
			// WorkingMessage SE : 주유 디바이스 이상정보 전송
			SE_WorkingMessage seWorkingMessage = new SE_WorkingMessage();
			seWorkingMessage.setNozzleNo(nozzleNo);
				
			DataStruct s81Interface = SomoSelfDS.getDS("81");
			s81Interface.setByteStream(message);
				
			String errorCode = (String) s81Interface.getValue("errorCode");
			String status    = (String) s81Interface.getValue("status");
			
			seWorkingMessage.setDeviceType("05");
			seWorkingMessage.setStatus(status);
			seWorkingMessage.setErrMsg(this.generateBlank(20));
	
			if (status.equals("0")) {
				// status 0 : 에러 회복
				
				// SomoSelf 81 ErrorCode 00 : 정상
				if (errorCode.equals("00")) {
					seWorkingMessage.setStatusCode("251");
					seWorkingMessage.setErrMsg("정상");
						
				// SomoSelf 81 ErrorCode 14 : 내장프린터 에러 
				} else if (errorCode.equals("14")){
					seWorkingMessage.setStatusCode(PRINTER_ERROR);
					seWorkingMessage.setErrMsg("프린터 에러 - 정상");
						
				// SomoSelf 81 ErrorCode 16 : 내장프린터 용지없음
				} else if (errorCode.equals("16")){
					seWorkingMessage.setStatusCode(PRINTER_PAPER);
					seWorkingMessage.setErrMsg("프린터 용지없음 - 정상");
						
				// SomoSelf 81 ErrorCode 10 : 음성 Busy 상태 
				} else if (errorCode.equals("10")){
					seWorkingMessage.setStatusCode(VOICE_BUSY);
					seWorkingMessage.setErrMsg("음성장치 Busy - 정상");	
					
				// SomoSelf 81 ErrorCode 11 : 음성 고장 
				} else if (errorCode.equals("11")){
					seWorkingMessage.setStatusCode(VOICE_ERROR);
					seWorkingMessage.setErrMsg("음성장치 이상 - 정상");
						
				} else {
					LogUtility.getPumpALogger().error(
							"### [TransSomoSelf] ###\n" +
							"### Incorrect error code ###\n" +
							"### Current error code : " + errorCode + " ###");
					
				}	
				
			} else if (status.equals("1")) {
				// status 1 : 에러 발생 
				
				// SomoSelf 81 ErrorCode 00 : 정상
				if (errorCode.equals("00")) {
					seWorkingMessage.setStatusCode("251");
					seWorkingMessage.setErrMsg("정상");
						
				// SomoSelf 81 ErrorCode 14 : 내장프린터 에러 
				} else if (errorCode.equals("14")){
					seWorkingMessage.setStatusCode(E_PRINTER_ERROR);
					seWorkingMessage.setErrMsg("프린터 에러");
						
				// SomoSelf 81 ErrorCode 16 : 내장프린터 용지없음
				} else if (errorCode.equals("16")){
					seWorkingMessage.setStatusCode(E_PRINTER_PAPER);
					seWorkingMessage.setErrMsg("프린터 용지없음");
						
				// SomoSelf 81 ErrorCode 10 : 음성 Busy 상태 
				} else if (errorCode.equals("10")){
					seWorkingMessage.setStatusCode(E_VOICE_BUSY);
					seWorkingMessage.setErrMsg("음성장치 Busy");	
					
				// SomoSelf 81 ErrorCode 11 : 음성 고장 
				} else if (errorCode.equals("11")){
					seWorkingMessage.setStatusCode(E_VOICE_ERROR);
					seWorkingMessage.setErrMsg("음성장치 이상");
						
				} else {
					LogUtility.getPumpALogger().error(
							"# [TransSomoSelf] # " +
							"Incorrect error code # " +
							"Current error code : " + errorCode + " #");					
				}				
			} else {
				LogUtility.getPumpALogger().error(
						"# [TransSomoSelf] # " +
						"Status Error. # " +
						"Current status : " + status + " #");
				
			}	// end inner if
				
			seWorkingMessage.setDetectTime(this.getSystemTime(12));
			seWorkingMessage.setVersion(this.generateBlank(9));
				
			returnMessage = seWorkingMessage;

		} else {
			LogUtility.getPumpALogger().error(
					"# [TransSomoSelf] # " +
					"Not Supported command(" + somoCommandString +
					") in TransSomoSelf #");
			returnMessage = null;
			
		}	// end if
	
		return returnMessage;
	
	}	// end generateWorkingMessage
	
	
	/**
	 * 소모 셀프 전문에서 명령 코드를 추출 
	 * 
	 * @param message	: 소모 셀프 전문
	 * @return			: 명령 코드 
	 */	
	private String getCommand(byte[] message) throws Exception {
		String returnMessage = null;
		
		byte[] commandArray 	= new byte[2];
		commandArray[0] 		= message[3];
		commandArray[1] 		= message[4];
		
		returnMessage = new String(commandArray);
		
		return returnMessage;
	
	}	// end getCommand
	
	/**
	 * WorkingMessage의 ConnectNozzleNo 값을 두자리 수로 추출
	 * 
	 * @param message	: WorkingMessage
	 * @return			: ConnectNozzleNo
	 */
	private String getConnectNozzleNo(WorkingMessage message) throws Exception {
		String returnMessage = message.getConnectNozzleNo();
		
		int tempI 		= Integer.parseInt(returnMessage);
		returnMessage 	= String.valueOf(tempI - 1);
		
		// 한자리 값은 두자리로 변경 
		if (returnMessage.length() < 2) {
			returnMessage = "0" + returnMessage;
			
		}	// end if
		
		return returnMessage;
		
	}	// end getConnectNozzleNo
	
	/**
	 * 소모 셀프 전문에서 ODT 번호를 추출
	 * 
	 * @param message	: 소모 셀프 전문 
	 * @return			: ODT 번호 
	 */
	private String getNozzleNo(byte[] message) throws Exception {
		String returnMessage = null;
		
		byte nozzleNo = (byte) (message[1] - 63);
		
		// 주유기 번호 범위 체크 
		if (nozzleNo > 64 || nozzleNo < 1) {
			LogUtility.getPumpALogger().error(
					"# [TransSomoSelf] # " +
					"Nozzle number error in TransSomoSelf # " +
					"Current nozzle number : " + nozzleNo + " #");
			returnMessage = null;
			
		}	// end if 
		
		// 두자리 수 변환 
		if (nozzleNo < 10){
			returnMessage = "0" + String.valueOf(nozzleNo);
			
		} else {
			returnMessage = String.valueOf(nozzleNo);

		}	// end if
		
		return returnMessage;
		
	}	// end getNozzleNo
	
	/**
	 * String 타입 숫자값에 1을 더하여 String 형태로 반환
	 * 
	 * @param message	: String 타입 숫자
	 * @return			
	 */
	private String getPlusOne(String message) throws Exception {
		String returnMessage = null;
		
		int tempI = Integer.parseInt(message) + 1;
		
		// 한자리 값은 두자리로 변경 
		if (tempI < 10) {
			returnMessage = "0" + String.valueOf(tempI);
			
		} else {
			returnMessage = String.valueOf(tempI);
			
		}	// end if
		
		return returnMessage;
		
	}	// end getConnectNozzleNo
	
	/**
	 * byte 배열의 data를  완전한 소모 셀프 전문 형태로 변환
	 * 
	 * @param data		: data
	 * @param nozzleNo	: 노즐 번호 
	 * @return			: 소모 셀프 전문 
	 */
	private byte[] makeProtocol(byte[] data, String nozzleNo) throws Exception {
		if (data == null) {
			return null;
		}	// end if
		
		byte[] returnData 		= null;
		byte blank 				= ' ';
		int returnDataCounter 	= 0;
		byte nozzleByte 		= Byte.parseByte(nozzleNo);
		
		// STX, SA, UA, ETX, BCC 만큼의 길이를 더한다.
		int arrayLength = data.length + 5;
		
		returnData = new byte[arrayLength];
		
		returnData[returnDataCounter++] = Command.STX;						// STX
		returnData[returnDataCounter++] = (byte) (nozzleByte + 0x3F);	// SA
		returnData[returnDataCounter++] = blank;										// UA
		
		for (int i = 0; i < data.length; i++) {
			returnData[returnDataCounter++] = data[i];
			
		}	// end for
		
		returnData[returnDataCounter++] = Command.ETX;					// ETX
		returnData[returnDataCounter++] = blank;									// BCC		
		
		return returnData;
		
	}	// end makeProtocol
}