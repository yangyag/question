package com.gsc.kixxhub.device.pumpa.devDatas;

import com.gsc.kixxhub.device.pumpa.common.Change;
import com.gsc.kixxhub.device.pumpa.common.DataStruct;

/*
* 프로젝트 명 : PI2
* 일시 : 2016.03.15
* 신규
* @author 정혜정
* */

//-- BC : 지폐투입 취소정보(ODT -> SC)
class GS_BC_DataStruct extends DataStruct {

	public GS_BC_DataStruct() throws Exception {
		
		this.setString("Command", 2);		
		this.setString("DeviceNo", 2);
		this.setString("ConnectDevNo", 2); 	
		this.setString("CashCount", 8);		// 취소금액
		this.setString("Time", 14);			// 투입시각 	
	}
}

//-- BI : 지폐투입정보(ODT -> SC)
class GS_BI_DataStruct extends DataStruct {

	public GS_BI_DataStruct() throws Exception {
		
		this.setString("Command", 2);		
		this.setString("DeviceNo", 2);
		this.setString("ConnectDevNo", 2); 	
		this.setString("Cash", 8);		// 투입금액
		this.setString("CashCount", 8);		// 투입 합계금액
		this.setString("Time", 14);			// 투입시각 	
		
	}
}

//-- BR : 세차권 바코드 요청 (ODT -> SC)
class GS_BR_DataStruct extends DataStruct {

	public GS_BR_DataStruct() throws Exception {
		
		/* * control 시작*/
		this.setString("Command", 2);
		this.setString("DeviceNo", 2);
		this.setString("ConnectDevNo", 2);
//			body 영역 
		this.setString("Price", 8);
		this.setString("PosReceiptNo", 14);
		
	}
}

//-- CA : 거래처 고객 유형확인 요청 (ODT -> SC)
class GS_CA_DataStruct extends DataStruct {

	public GS_CA_DataStruct() throws Exception {
		
		this.setString("Command", 2);		
		this.setString("DeviceNo", 2);
		this.setString("ConnectDevNo", 2); 	
		this.setVString("CustomerNo", 40);	// 고객카드번호
		
	}
}


//-- GA : 결제 승인 요청 (SC <- ODT) --//	
class GS_GA_DataStruct extends DataStruct {

	public GS_GA_DataStruct() throws Exception {
		
		/* * control 시작*/
		this.setString("Command", 2);
		this.setString("DeviceNo", 2);
		this.setString("ConnectDevNo", 2);
		/* control 끝*/
		/* datas 영역 시작 
		 * 각항목은 가변길이를 갖는다.*/
		this.setVString("MessageType", 4);
		this.setVString("CardNumber", 100);
		this.setVString("BonusCard", 100);
		this.setVString("CustCardNo", 100);
		this.setVString("CashReceiptNo", 100);
		this.setVString("Liter", 7);
		this.setVString("BasePrice", 6);
		this.setVString("Price", 8);
		this.setVString("LedCode", 1);
		this.setVString("CreatedTime", 14);
		this.setVString("UPosByte", 0);
		
	}
}

/*
*  결제승인 처리
*/

//-- GT : 판매완료 처리 (영수증 출력 후 전송)(ODT -> SC)
class GS_GT_DataStruct extends DataStruct {

	public GS_GT_DataStruct() throws Exception {
		
		this.setString("Command", 2);
		this.setString("DeviceNo", 2);
		this.setString("ConnectDevNo", 2);
		this.setString("Liter", 7);
		this.setString("BasePrice", 6);
		this.setString("Price", 8);
		this.setString("CreatedTime", 14);
	}
}

/**
* 
* @author 정혜정
* 표준 셀프ODT 인터페이스 전문
*
*/

/*
*  초기화 작업
*/

//--  PM : ODT 모드정보 (SC <- ODT) --//	
class GS_PM_DataStruct extends DataStruct {

	public GS_PM_DataStruct() throws Exception {
		
		this.setString("Command", 2);
		this.setString("DeviceNo", 2);
		this.setString("ConnectDevNo", 2);
		this.setString("Mode", 1);
	}
}


//--  PV : ODT 버전정보 (SC <- ODT) --//	
class GS_PV_DataStruct extends DataStruct {

	public GS_PV_DataStruct() throws Exception {
		
		this.setString("Command", 2);
		this.setString("DeviceNo", 2);
		this.setString("ConnectDevNo", 2);
		this.setString("Version", 4);
	}
}

//-- S3 : 주유 중  자료전송(ODT -> SC)
class GS_S3_DataStruct extends DataStruct {

	public GS_S3_DataStruct() throws Exception {
		
		this.setString("Command", 2);		
		this.setString("DeviceNo", 2);		// 노즐번호
		this.setString("ConnectDevNo", 2); 	// ODT NO
		this.setString("Liter", 7);			// 정량 4.3
		this.setString("BasePrice", 6);		// 단가 
		this.setString("Price", 8);			// 주유 금액
		this.setString("WDate", 6);			// 영업일(YYMMDD)
		
	}
}

//-- S4 :  주유 완료 자료전송(주유기)(ODT -> SC)
class GS_S4_DataStruct extends DataStruct {

	public GS_S4_DataStruct() throws Exception {
		
		this.setString("Command", 2);		
		this.setString("DeviceNo", 2);
		this.setString("ConnectDevNo", 2); 	
		
		this.setString("Liter", 7);			// 정량 4.3
		this.setString("BasePrice", 6);		// 단가 	
		this.setString("Price", 8);			// 주유 금액	
		this.setString("WDate", 6);			// 영업일(YYMMDD)
		this.setString("SystemTime", 12);	// 시스템시간
		this.setString("TotalGauge", 10);	// 토탈 게이지(7.3)
		this.setString("StatusFlag", 1);	// 상태(0:정상, 1: 비정상)
	}
}


//-- S8 : 주유기 / ODT 상태 전송(ODT -> SC)
class GS_S8_DataStruct extends DataStruct {

	public GS_S8_DataStruct() throws Exception {
		
		this.setString("Command", 2);		
		this.setString("DeviceNo", 2);
		this.setString("ConnectDevNo", 2); 	
		this.setString("DeviceType", 1);	// 디바이스 타입
		this.setString("Status", 3);	// 주유기 / ODT 상태 코드	
		
	}
}

//-- SE : 주유 디바이스 이상정보 전송(ODT -> SC)
class GS_SE_DataStruct extends DataStruct {

	public GS_SE_DataStruct() throws Exception {
		
		this.setString("Command", 2);		
		this.setString("DeviceNo", 2);
		this.setString("ConnectDevNo", 2); 	
		this.setString("DeviceType", 1);	// 디바이스 타입
		this.setString("Status", 1);		// 상태 	
		this.setString("StatusCode", 3);	// 주유기 / ODT 상태 코드	
		this.setString("ErrMsg", 20);		// 에러메세지
		this.setString("DetectTime", 12);	// 검출시각(YYMMDDhhmmss)
		
	}
}

//-- SJ : 주유시작전  TotalGauge 전송 (ODT -> SC)
class GS_SJ_DataStruct extends DataStruct {

	public GS_SJ_DataStruct() throws Exception {
		
		/* * control 시작*/
		this.setString("Command", 2);
		this.setString("DeviceNo", 2);
		this.setString("ConnectDevNo", 2);
		this.setString("SystemTime", 12);
		this.setString("TotalGauge", 10);
		
	}
}



public class GSSelfOdtDS {
	
	/**
	 * 프로토콜 전문 DataStruct를 얻는다. 
	 * 
	 * @param command	: 명령 코드 
	 * @return
	 */
	
	
	public static DataStruct getDS(String command) {
		DataStruct returnData = null;
		
		try {
			if (command.equals("PM")) {
				returnData = new GS_PM_DataStruct();
			} else if (command.equals("PV")) {
				returnData = new GS_PV_DataStruct();
			} else if (command.equals("GA")) {
				returnData = new GS_GA_DataStruct();
			} else if (command.equals("GT")) {
				returnData = new GS_GT_DataStruct();
			} else if (command.equals("BI")) {
				returnData = new GS_BI_DataStruct();
			} else if (command.equals("BC")) {
				returnData = new GS_BC_DataStruct();
			} else if (command.equals("S3")) {
				returnData = new GS_S3_DataStruct();   
			} else if (command.equals("S4")) {
				returnData = new GS_S4_DataStruct();
			} else if (command.equals("SE")) {
				returnData = new GS_SE_DataStruct();
			} else if (command.equals("S8")) {
				returnData = new GS_S8_DataStruct();
			} else if (command.equals("CA")) {
				returnData = new GS_CA_DataStruct();
			} else if (command.equals("BR")) {
				returnData = new GS_BR_DataStruct();
			} else if (command.equals("SJ")) {
				returnData = new GS_SJ_DataStruct();
			}else {
				returnData = null;
			} // end if
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return returnData;
		
	}
	
	/*
	 *  ODT_NO 변환 : POS -> ODT
	 *  71 (POS) -> S1 (ODT)
	 */
	public static String getOdtNo_forODT (String odtNo) {
		
		int nOdtNo = Integer.parseInt(odtNo) - 70;
		
		return "S" + Change.binToHex (nOdtNo);
	}
	
	/*
	 *  ODT_NO 변환 : ODT -> POS
	 *  S1 (ODT) -> 71 (POS)
	 */
	public static String getOdtNo_forPOS (String odtNo) {
		
		String odtStr = odtNo.substring(1,2);
				
		return Change.toString(70 + Change.hexToBin (odtStr));
	}	
}

