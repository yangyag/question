package com.gsc.kixxhub.device.pumpa.devDatas;

import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.DataStruct;

/**
 * @author yd
 *
 */
class P02_DataStruct extends DataStruct{
	/**
	 * @param block
	 */
	public P02_DataStruct(String block) throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("block", 1);
		this.setString("fileNo", 2);
		
		if (block == SomoSelfDS.REGI_INFO) {
			// 데이터 블록, 사업자 정보
			this.setString("storeCode", 10);
			this.setString("regiNo", 12);
			this.setString("storeName", 40);
			this.setString("repName", 30);
			this.setString("storePost", 7);
			this.setString("storeAdd1", 50);
			this.setString("storeAdd2", 50);
			this.setString("phone", 16);
			this.setString("saMinAmt", 10);
			this.setString("bonusStoreCode", 10);

		} else if (block == SomoSelfDS.HEADER) {
			// 데이터 블록, 머리말/꼬리말
			this.setString("head", 50);
			this.setString("tail1", 50);
			this.setString("tail2", 50);
			
		} else if (block == SomoSelfDS.NOZZLE_INFO) {
			// 데이터 블록, 주유기 정보
			this.setString("nozzleNo", 2);		
			this.setString("basePrice", 4);
			this.setString("goodsCode", 4);		
			this.setString("goodsType", 14);	
			
		} else {
			LogUtility.getPumpALogger().debug("##### Incorrect data block" +
					" in Somo Self '02' #####");
		}	// end if
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C02_DataStruct


/**
 * @author yd
 *
 */
class P22_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public P22_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("date", 14);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C22_DataStruct


/**
 * @author yd
 *
 */
class P50_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public P50_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("flag", 1);
		this.setString("nozzleNo", 2);
		this.setString("state", 2);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C50_DataStruct


/**
 * @author yd
 *
 */
class P51_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public P51_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("nozzleNo", 2);
		this.setString("state", 1);
		this.setString("liter", 7);
		this.setString("basePrice", 4);
		this.setString("price", 6);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C51_DataStruct


/**
 * @author yd
 *
 */
class P52_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public P52_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("flag", 1);			// 플래그
		this.setString("nozzleNo", 2);		// 노즐번호
		this.setString("length", 4);		// 길이
		this.setString("cardNo", 18);		// 카드번호
		this.setString("carInfo", 18);		// 차량정보
		this.setString("oilCode", 4);		// 유종코드 
		this.setString("name", 26);			// 운전자명
		this.setString("liter", 7);			// 판매수량
		this.setString("basePrice", 6);		// 판매단가
		this.setString("jpLiter", 7);		// 전표량
		this.setString("transType", 1);		// 거래종류
		this.setString("customerType", 1);	// 고객종류
		this.setString("transStatus", 1);	// 거래상태
		this.setString("printBase", 1);		// 단가출력여부
		this.setString("deposite", 1);		// 보관증발행여부
		this.setString("float", 1);			// 소수점처리방식
		this.setString("receipt", 1);		// 계산서거래종류
		this.setString("monthLimit", 10);	// 한도수량
		this.setString("saveLimit", 10);	// 사용량
		this.setString("limitType", 1);		// 수량,금액 
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C52_DataStruct


/**
 * @author yd
 *
 */
class P54_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public P54_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("error", 1);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C54_DataStruct


/**
 * @author yd
 *
 */
class P55_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public P55_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("nozzleNo", 2);
		this.setString("mode", 1);
		this.setString("liter", 7);
		this.setString("basePrice", 4);
		this.setString("price", 6);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C55_DataStruct


/**
 * @author yd
 *
 */
class P56_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public P56_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("nozzleNo", 2);
		this.setString("bonusNumber", 16);
		this.setString("storeScore", 8);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end S56_DataStruct 


/**
 * @author yd
 *
 */
class P61_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public P61_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("fileNo", 1);
		this.setString("mode", 1);
		this.setString("status", 20);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C61_DataStruct


/**
 * @author yd
 *
 */
class P80_DataStruct extends DataStruct{
	/**
	 * @param type
	 */
	public P80_DataStruct(String type) throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo1", 1);
		this.setString("orderNo2", 2);
		this.setString("screenNo", 2);
		this.setString("dataType", 1);
		
		if (type == SomoSelfDS.STOP) {
			// 데이터 타입 '0'(입력 중지)
			// 없음
		} else if (type == SomoSelfDS.KEY){
			// 데이터 타입 '1'(키 입력)
			this.setString("function", 2);
			this.setString("section", 2);
			this.setString("nozzleNo", 2);
			this.setString("price", 7);
		} else if (type == SomoSelfDS.CARD){
			// 데이터 타입 '2'(카드 데이터)
			this.setString("flag", 1);
			this.setString("nozzleNo", 2);
			this.setString("length", 4);
			this.setString("cardType", 1);		// 카드타입
			this.setString("cardNo", 40);		// 카드번호
			this.setString("bonusCard", 40);	// 보너스카드번호
			this.setString("liter", 7);			// 수량
			this.setString("basePrice", 4);		// 단가
			this.setString("price", 8);			// 금액
			this.setString("cDate", 14);		// 신용카드승인시각
			this.setString("cNumber", 12);		// 신용카드승인번호
			this.setString("bDate", 14);		// 보너스누적승인시각
			this.setString("bonusNo", 12);		// 보너스누적승인번호
			this.setString("dNo", 12);			// 현금영수증승인번호
		} else {
			LogUtility.getPumpALogger().debug("##### Incorrect data type" +
			 							" in Somo Self '80' #####");
		}	// end if
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C80_DataStruct


/**
 * 
 * @author Mckelain
 *
 */
class P80N_DataStruct extends DataStruct{
	/**
	 * 신형소모셀프 신규 스펙
	 */
	public P80N_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("command_index", 1);
		this.setString("odtNo", 2);
		this.setString("nozzleNo", 2);
		this.setString("trType", 1);
		this.setString("authType", 2);
		this.setString("cardNum", 40);
		this.setString("bonusCard", 40);
		this.setString("liter", 7);
		this.setString("basePrice", 6);
		this.setString("price", 8);
		this.setString("c_number", 20);
		this.setString("b_number", 20);
		this.setString("d_number", 20);
		this.setString("pin", 8);
		
		this.setByte("etx");
		this.setByte("bcc");
		
	}
}

/**
 * @author yd
 *
 */
class P81_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public P81_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("status", 1);
		this.setString("deviceNo", 2);
		this.setString("errorCode", 2);
		this.setString("length", 2);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C81_DataStruct

class P82_DataStruct extends DataStruct{
	public P82_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("commandIndex", 1);
		this.setString("odtNumber", 2);
		this.setString("nozzleNumber", 2);
		this.setString("cash", 8);
		this.setString("cashCount", 8);
		this.setString("time", 14);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end P82_DataStruct


/**
 * @author yd
 *
 */
class P90_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public P90_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("nozzleNo", 2);
		this.setString("mode", 1);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C90_DataStruct


/**
 * @author yd
 *
 */
class P91_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public P91_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("nozzleNo", 2);
		this.setByte("status");
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C91_DataStruct

/**
 * @author yd
 *
 */
class P92_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public P92_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("nozzleNo", 2);				// 노즐번호
		this.setString("mode", 1);					// Mode
		this.setString("approvalType", 2);			// 결제유형
		this.setString("bonusType", 2);				// 보너스카드종류
		this.setString("bonusCode", 5);				// 보너스카드승인코드
		this.setString("authInfo", 34);				// 승인정보
		this.setString("liter", 7);					// 수량
		this.setString("basePrice", 4);				// 단가
		this.setString("price", 8);					// 금액
		this.setString("authTime", 14);				// 승인시각
		this.setString("authNo", 12);				// 승인번호
		this.setString("cardNo", 16);				// 카드번호
		this.setString("cardCorpCode", 4);			// 발급사코드번호
		this.setString("cardCorpName", 16);			// 발급사명
		this.setString("noteCode", 4);				// 매입사코드번호
		this.setString("noteName", 16);				// 매입사명
		this.setString("noteNo", 5);				// 전표번호
		this.setString("bonusNo", 16);				// 보너스카드번호
		this.setString("bonusTime", 14);			// 보너스승인시각
		this.setString("bonusAuthNo", 12);			// 보너스승인번호
		this.setString("dAuthNo", 12);				// 현금영수증승인번호
		this.setString("bonusGenerateScore", 8);	// 보너스발생점수
		this.setString("bonusScore", 8);			// 보너스가용점수
		this.setString("bonusTotalScore", 8);		// 보너스총누적점수
		this.setString("message", 100);				// 메시지
		this.setString("vanMessage", 100);			// Van사 메시지 
		this.setString("bonusMessage", 100);		// 보너스 메시지 
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C92_DataStruct
/**
 * 
 * @author Mckelain
 *
 */
class P92N_DataStruct extends DataStruct{
	/**
	 * 신형소모셀프 신규 스펙
	 */
	public P92N_DataStruct(int trTypeSize) throws Exception {
	
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command_id", 2);
		this.setString("command_index", 1);
		this.setString("odtNo", 2);
		this.setString("nozzleNo", 2);
		this.setString("divKey", 1);
		this.setString("command", 1);
		this.setString("cardNo", 40);
		this.setString("bonusCardNo", 40);
		this.setString("dup", 2);
		
		for (int i = 0; i < trTypeSize; i++) {
			this.setString("trType" + i, 1);
			this.setString("mode" + i, 1);
			this.setString("liter" + i, 7);
			this.setString("basePrice" + i, 6);
			this.setString("price" + i, 8);
			
		}	// end for
		
		this.setByte("etx");
		this.setByte("bcc");
		
	}
}


/**
 * @author yd
 *
 */
class P93_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public P93_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("nozzleNo", 2);
		this.setString("mode", 1);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C93_DataStruct



/**
 * @author yd
 *
 */
class P94_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public P94_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("nozzleNo", 2);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C94_DataStruct



/**
 * @author yd
 *
 */
class P95_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public P95_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("nozzleNo", 2);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C95_DataStruct



public class PrimeSelfDS {
	/**
	 * 
	 */
	public static final String CARD = "6";	// 데이터 타입 '2'
	/**
	 * 
	 */
	public static final String HEADER      = "2";	// 데이터 블록, 머리말/꼬리말
	/**
	 * 
	 */
	public static final String KEY  = "5";	// 데이터 타입 '1'
	
	/**
	 * 
	 */
	public static final String NOZZLE_INFO = "3";	// 데이터 블록, 주유기 정보
	// "02" 명령코드(파일 데이터)에서 블록구분으로 사용
	/**
	 * 
	 */
	public static final String REGI_INFO   = "1";	// 데이터 블록, 사업자 정보
	// "80" 명령코드(입력 오더 응답)에서 사용
	/**
	 * 
	 */
	public static final String STOP = "4";	// 데이터 타입 '0'
	
	

	/**
	 * 소모 셀프 프로토콜 전문 DataStruct를 얻는다. 
	 * 
	 * @param command	: 명령 코드 
	 * @return
	 */
	public static DataStruct getDS(String command) {
		DataStruct returnData = null;
		
		try {
			if (command.equals("22")) {
				returnData = new P22_DataStruct();
			} else if (command.equals("50")) {
				returnData = new P50_DataStruct();
			} else if (command.equals("51")) {
				returnData = new P51_DataStruct();
			} else if (command.equals("52")) {
				returnData = new P52_DataStruct();
			} else if (command.equals("54")) {
				returnData = new P54_DataStruct();
			} else if (command.equals("55")) {
				returnData = new P55_DataStruct();
			} else if (command.equals("56")) {
				returnData = new P56_DataStruct();
			} else if (command.equals("61")) {
				returnData = new P61_DataStruct();
			} else if (command.equals("81")) {
				returnData = new P81_DataStruct();
			} else if (command.equals("90")) {
				returnData = new P90_DataStruct();
			} else if (command.equals("91")) {
				returnData = new P91_DataStruct();
			} else if (command.equals("92")) {
				returnData = new P92_DataStruct();
			} else if (command.equals("93")) {
				returnData = new P93_DataStruct();
			} else if (command.equals("94")) {
				returnData = new P94_DataStruct();
			} else if (command.equals("95")) {
				returnData = new P95_DataStruct();
			} else if (command.equals("80N")) {
				returnData = new P80N_DataStruct();
			} else if (command.equals("S3")) {
				returnData = new PS3_DataStruct();
			} else if (command.equals("S4")) {
				returnData = new PS4_DataStruct();
			} else if (command.equals("SJ")) {
				returnData = new PSJ_DataStruct();
			} else if (command.equals("82")) {
				returnData = new P82_DataStruct();
			} else {
				returnData = null;
			} // end if
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return returnData;
		
	}	// end getDS
	
	
	
	public static DataStruct getDS(String command, int trTypeSize) {
		DataStruct returnData = null;
		
		try {
			if (command.equals("92N")) {
				returnData = new P92N_DataStruct(trTypeSize);
			} else {
				returnData = null;
			}	// end if
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return returnData;
		
	}	// end getDS
	
	
	
	/**
	 * ODT 프로토콜 명령코드 "02", "80" 전문 DataStruct를 얻는다. 
	 * 
	 * @param command		: 명령 코드 
	 * @param parameter1 	: 데이터 블록(02 전문), 데이터 타입(80 전문)
	 * @return
	 */
	public static DataStruct getDS(String command, String parameter1) throws Exception {
		DataStruct returnData = null;
		
		if (command.equals("02")) {
			returnData = new P02_DataStruct(parameter1);
		} else if (command.equals("80")) {
			returnData = new P80_DataStruct(parameter1);
		} else {
			returnData = null;
		}	// end if
		
		return returnData;
	}	// end getDS
}



class PS3_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public PS3_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("liter", 7);
		this.setString("basePrice", 6);
		this.setString("price", 6);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C94_DataStruct



class PS4_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public PS4_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("liter", 7);
		this.setString("basePrice", 6);
		this.setString("price", 8);
		this.setString("totalGauge", 10);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C94_DataStruct



/**
 * @author yd
 *
 */
class PSJ_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public PSJ_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("nozzleNo", 2);
		this.setString("odtNo", 2);
		this.setString("totalGauge", 10);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end SJ_DataStruct