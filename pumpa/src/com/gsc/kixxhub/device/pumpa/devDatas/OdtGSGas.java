package com.gsc.kixxhub.device.pumpa.devDatas;

import com.gsc.kixxhub.device.pumpa.common.DataStruct;


/*
 * 생성일자 : 2016.03.28 양일준
 * 내용 : TCP/IP 통신방식 충전기 ODT용 DataStruct
 *
 */

/**
 * @author yd
 *
 */
class C1_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public C1_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("timeInfo", 12);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end C1_NewDataStruct


/**
 * @author yd
 *
 */
class C2_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public C2_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("cardNo", 18);		// 카드번호
		this.setString("carNo", 18);		// 차량번호
		this.setString("driverName", 30);	// 운전자명
		this.setString("liter", 7);			// 누적 사용량
		this.setString("basePrice", 6);		// 판매단가
		this.setString("jpLiter", 7);		// 전표량
		this.setString("transType", 1);		// 거래종류
		this.setString("cusType", 1);		// 고객종류 
		this.setString("transStatus", 1);	// 거래상태	
		this.setString("printBase", 1);		// 단가출력여부
		this.setString("depositST", 1);		// 보관증발행여부
		this.setString("floatTR", 1);		// 소수점처리방식
		this.setString("receiptType", 1);	// 계산서거래종류
		this.setString("monthLimit", 10);	// 월 한도수량
		this.setString("saveLimit", 10);	// 누적사용량 
		this.setString("limitType", 1);		// 한도기준 
		this.setString("cusNo", 6);		    // 거래처번호 
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end C2_NewDataStruct



/**
 * @author yd
 *
 */
class C3_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public C3_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("repNo", 2);
		this.setString("carNo", 18);
		this.setString("cardNo", 18);
		this.setString("driverName", 30);
		this.setString("dataFinish", 1);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end C3_NewDataStruct


/**
 * @author yd
 *
 */
class C4_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public C4_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end C4_NewDataStruct



/**
 * @author yd
 *
 */
class C5_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public C5_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("result", 1);
		this.setString("trans", 8);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end C5_NewDataStruct


/**
 * @author yd
 *
 */
class C7_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public C7_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("result", 1);		// 처리결과
		this.setString("bonusRep", 2);		// 보너스카드응답코드
		this.setString("bonusNumber", 16);	// 보너스카드번호
		this.setString("gsScore", 6);		// GS 일반점수
		this.setString("gsSpecialScore", 6);// GS 특별점수
		this.setString("ssScore", 6);		// SS 점수
		this.setString("birth", 4);			// 본인생일
		this.setString("partnerBirth", 4);	// 배우자생일
		this.setString("weddingDay", 4);	// 결혼기념일
		this.setString("babyBirth1", 4);	// 자녀1 생일
		this.setString("babyBirth2", 4);	// 자녀2 생일
		this.setString("plan", 1);			// 가입보상 플랜
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end C7_NewDataStructs


/**
 * @author yd
 *
 */
class CB_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public CB_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end CB_NewDataStruct


/**
 * @author yd
 *
 */
class CC_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public CC_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end CC_NewDataStruct



/**
 * @author yd
 *
 */
class CD_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public CD_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("storeName", 40);
		this.setString("regiNum", 12);
		this.setString("repNM", 30);
		this.setString("addr", 50);
		this.setString("tel", 16);
		this.setString("goodsType", 30);
		this.setString("basePrice", 6);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end CC_NewDataStruct


/**
 * @author yd
 *
 */
class CE_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public CE_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("head", 42);
		this.setString("tail1", 42);
		this.setString("tail2", 42);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end CE_NewDataStruct


/**
 * @author yd
 *
 */
class CF_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public CF_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("head", 42);
		this.setString("tail1", 42);
		this.setString("tail2", 42);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end CF_NewDataStruct



/**
 * @author yd
 *
 */
class CG_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public CG_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("head", 42);
		this.setString("tail1", 42);
		this.setString("tail2", 42);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end CG_NewDataStruct



/**
 * @author yd
 *
 */
class CH_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public CH_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("head", 42);
		this.setString("tail1", 42);
		this.setString("tail2", 42);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end CH_NewDataStruct



/**
 * @author yd
 *
 */
class CI_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public CI_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("head", 42);
		this.setString("tail1", 42);
		this.setString("tail2", 42);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end CI_NewDataStruct



/**
 * @author yd
 *
 */
class CJ_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public CJ_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("mode", 1);
		this.setString("liter", 7);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end CJ_NewDataStruct



/**
 * @author yd
 *
 */
class CK_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public CK_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("mode", 1);
		this.setString("liter", 7);
		this.setString("basePrice", 6);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end _NewDataStruct



/**
 * @author yd
 *
 */
class CN_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public CN_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("receiptMin", 5); // 영수증발행최소치(미사용)
		this.setString("depositMin", 5); // 보관증발행최소치(미사용)
		this.setString("minSale", 5);	 // 판매인정 최소치
		this.setString("loanRemain", 1); // 대여금 잔액표시 여부 
		this.setString("termWait", 2);	 // 판매종료대기시간 
		this.setString("emergStop", 3); // 비상정지 리터 
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end CN_NewDataStruct



/**
 * @author yd
 *
 */
class CO_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public CO_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("depositNumber", 10);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end CO_NewDataStruct



public class OdtGSGas {

	/**
	 * ODT 프로토콜 전문 DataStruct를 얻는다. 
	 * 
	 * @param command	: 명령 코드 
	 * @return
	 */
	public static DataStruct getDS(String command) throws Exception {
		DataStruct returnData = null;
		
		if (command.equals("C1")) {
			returnData = new C1_NewDataStruct();
			
		} else if (command.equals("C2")) {
			returnData = new C2_NewDataStruct();
			
		} else if (command.equals("C3")) {
			returnData = new C3_NewDataStruct();
			
		} else if (command.equals("C4")) {
			returnData = new C4_NewDataStruct();
			
		} else if (command.equals("C5")) {
			returnData = new C5_NewDataStruct();
			
		} else if (command.equals("C7")) {
			returnData = new C7_NewDataStruct();
			
		} else if (command.equals("CB")) {
			returnData = new CB_NewDataStruct();
			
		} else if (command.equals("CC")) {
			returnData = new CC_NewDataStruct();
			
		} else if (command.equals("CD")) {
			returnData = new CD_NewDataStruct();
			
		} else if (command.equals("CE")) {
			returnData = new CE_NewDataStruct();
			
		} else if (command.equals("CF")) {
			returnData = new CF_NewDataStruct();
			
		} else if (command.equals("CG")) {
			returnData = new CG_NewDataStruct();
			
		} else if (command.equals("CH")) {
			returnData = new CH_NewDataStruct();
			
		} else if (command.equals("CI")) {
			returnData = new CI_NewDataStruct();
			
		} else if (command.equals("CJ")) {
			returnData = new CJ_NewDataStruct();
			
		} else if (command.equals("CK")) {
			returnData = new CK_NewDataStruct();
			
		} else if (command.equals("CN")) {
			returnData = new CN_NewDataStruct();
			
		} else if (command.equals("CO")) {
			returnData = new CO_NewDataStruct();
			
		} else if (command.equals("T1")) {
			returnData = new T1_NewDataStruct();
			
		} else if (command.equals("T2")) {
			returnData = new T2_NewDataStruct();
			
		} else if (command.equals("T3")) {
			returnData = new T3_NewDataStruct();
			
		} else if (command.equals("T5")) {
			returnData = new T5_NewDataStruct();
			
		} else if (command.equals("TA")) {
			returnData = new TA_NewDataStruct();
			
		} else if (command.equals("TB")) {
			returnData = new TB_NewDataStruct();
			
		} else if (command.equals("TC")) {
			returnData = new TC_NewDataStruct();
		/**
		 * 변경내용 : 신규(충전기 ODT 신용, 신용+보너스, 현금영수증 승인 요청, 캠페인주유완료 정보 요청)
		 * 변경일자 : 2015.12.23 양일준
		 */	
		} else if (command.equals("SK")) {
			returnData = new SK_NewDataStruct();
			
		} else {
			returnData = null;
		}	// end if
		
		return returnData;
	}	// end getDS
	
	
	
	/**
	 * ODT 프로토콜 명령코드 "T4" 전문 DataStruct를 얻는다.
	 * 
	 * @param command	: 명령 코드 
	 * @param repNo		: 결재 유형 개수 
	 * @return
	 */
	public static DataStruct getDS(String command, int repNo)  {
		DataStruct returnData = null;
		
		try {
			if (command.equals("T4")) {
				returnData = new T4_NewDataStruct(repNo);

			} else {
				returnData = null;

			} // end if
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return returnData;
		
	}	// getDS
	
}

/**
 * @author yd
 *
 */
class SK_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public SK_NewDataStruct() throws Exception {
		/* * control 시작*/
		this.setByte("soh");
		this.setString("DeviceNo", 2);
		this.setByte("stx");
		/* control 끝*/
		
		this.setString("Command", 2);
		this.setString("ConnectDevNo", 2);     //연결 디바이스 번호, 충전기 ODT의 경우 01

		/* datas 영역 시작 
		 * 각항목은 가변길이를 갖는다.*/
		this.setVString("MessageType", 4);     //메시지 타입
		this.setVString("CardNumber", 100);    //카드번호
		this.setVString("BonusCard", 100);     //보너스카드번호
		this.setVString("CustCardNo", 100);    //거래처카드번호
		this.setVString("CashReceiptNo", 100); //현금영수증 번호
		this.setVString("Amount", 7);          //수량
		this.setVString("BasePrice", 6);       //단가
		this.setVString("Price", 8);           //금액
		this.setVString("LedCode", 1);         //LED 코드
		this.setVString("CreatedTime", 14);    //생성일자
		//this.setVString("CashCount", 8);
		this.setVString("UPosByte", 0);        //UposMessage
		this.setByte("etx");                   //ETX
		
	}
}	// end SK_NewDataStruct



/**
 * @author yd
 *
 */
class T1_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public T1_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("screen", 1);		// 상태표시
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end T1_NewDataStruct



/**
 * @author yd
 *
 */
class T2_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public T2_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("liter", 7);
		this.setString("price", 8);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end T2_NewDataStruct



/**
 * @author yd
 *
 */
class T3_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public T3_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("mode", 1);
		this.setString("carNumber", 16);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end _NewDataStruct



/**
 * @author yd
 *
 */
class T4_NewDataStruct extends DataStruct {
	/**
	 * @param repNo
	 */
	public T4_NewDataStruct(int repNo) throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("cardNumber", 16);	// 카드번호
		this.setString("boyNumber", 4);		// 충전원 번호
		this.setString("totalLiter", 7);	// 계기수량
		this.setString("up1", 6);			// 계기단가
		this.setString("totalAMT1", 8);		// 총계기금액
		this.setString("up2", 6);			// 판매단가
		this.setString("totalAMT2", 8);		// 총 판매금액
		this.setString("totalGauge", 10);	// 토탈게이지
		this.setString("repNo", 1);			// 결재유형개수
		
		for (int i = 0; i < repNo; i++) {
			this.setString("flag" + i, 1);			// 결재유형
			this.setString("liter" + i, 7);			// 결재수량
			this.setString("amt" + i, 8);			// 제시금액
			this.setString("keepNumber" + i, 10);	// 보관증 번호 또는 거스름돈
			
		}	// end for
		
//		this.setString("keyFlag", 1);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
	
}	// end _NewDataStruct

/**
 * @author yd
 *
 */
class T5_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public T5_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("boyNumber", 4);
		this.setString("cardNumber", 18);
		this.setString("price", 8);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end T5_NewDataStruct


/**
 * @author yd
 *
 */
class TA_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public TA_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("totalGauge", 10);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end TA_NewDataStruct



/**
 * @author yd
 *
 */
class TB_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public TB_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("keepNumber", 10);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end TB_NewDataStruct



/**
 * @author yd
 *
 */
class TC_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public TC_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end TC_NewDataStruct
