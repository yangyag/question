package com.gsc.kixxhub.device.pumpa.devDatas;

import com.gsc.kixxhub.device.pumpa.common.DataStruct;

/**
 * @author yd
 *
 */
class BA_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public BA_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("cardNumber", 40);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end BA_DataStruct


/**
 * @author yd
 *
 */
class BB_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public BB_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("success", 1);
		this.setString("display", 64);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end BB_DataStruct



/**
 * @author yd
 *
 */
class C1_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public C1_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("timeInfo", 12);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end C1_DataStruct


/**
 * @author yd
 *
 */
class C2_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public C2_DataStruct() throws Exception {
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
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end C2_DataStruct



/**
 * @author yd
 *
 */
class C3_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public C3_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("repNo", 2);
		this.setString("carNo", 18);
		this.setString("cardNo", 18);
		this.setString("driverName", 30);
		this.setString("dataFinish", 1);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end C3_DataStruct


/**
 * @author yd
 *
 */
class C4_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public C4_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end C4_DataStruct



/**
 * @author yd
 *
 */
class C5_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public C5_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("result", 1);
		this.setString("trans", 8);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end C5_DataStruct



/**
 * @author yd
 *
 */
class C6_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public C6_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("result", 1);			// 처리결과
		this.setString("recogDate", 14);		// 승인일시
		this.setString("recogNumber", 12);		// 승인번호
		this.setString("cardNumber", 16);		// 카드번호
		this.setString("cardCorpName", 20);		// 카드사명
		this.setString("francNumber", 16);		// 가맹점번호
		this.setString("noteCorpCode", 3);		// 전표매입사코드
		this.setString("noteCorpName", 20);		// 전표매입사명
		this.setString("terminalNumber", 10);	// 단말기 번호
		this.setString("noteNumberTemp", 5);	// 전표번호
		this.setString("notice", 64);			// Notice
		this.setString("noteNumber", 10);		// 전표번호
		this.setString("recogConfid", 5);		// 현마감신용승인순번
		this.setString("printContent", 500);	// 인쇄내역
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end C6_DataStruct



/**
 * @author yd
 *
 */
class C7_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public C7_DataStruct() throws Exception {
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
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end C7_DataStruct



/**
 * @author yd
 *
 */
class C8_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public C8_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("result", 1);			// 처리결과
		this.setString("recodDate", 14);		// 승인일시 
		this.setString("recodNumber", 12);		// 승인번호
		this.setString("cardNumber", 16);		// 카드번호
		this.setString("cardCorpName", 20);		// 카드사명
		this.setString("francNumber", 16);		// 가맹점번호
		this.setString("noteCorpCode", 3);		// 전표매입사코드
		this.setString("noteCorpName", 20);		// 전표매입사명
		this.setString("terminalNumber", 10);	// 단말기 번호
		this.setString("noteNumberTemp", 5);	// 전표번호
		this.setString("notice", 64);			// Notice
		this.setString("noteNumber", 10);		// 전표번호
		this.setString("recogConfid", 5);		// 현마감신용승인순번
		this.setString("printBuffer", 500);		// 인쇄내역
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end C8_DataStruct



/**
 * @author yd
 *
 */
class CB_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public CB_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end CB_DataStruct


/**
 * @author yd
 *
 */
class CC_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public CC_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end CC_DataStruct


/**
 * @author yd
 *
 */
class CD_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public CD_DataStruct() throws Exception {
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
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end CC_DataStruct



/**
 * @author yd
 *
 */
class CE_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public CE_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("head", 42);
		this.setString("tail1", 42);
		this.setString("tail2", 42);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end CE_DataStruct



/**
 * @author yd
 *
 */
class CF_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public CF_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("head", 42);
		this.setString("tail1", 42);
		this.setString("tail2", 42);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end CF_DataStruct



/**
 * @author yd
 *
 */
class CG_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public CG_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("head", 42);
		this.setString("tail1", 42);
		this.setString("tail2", 42);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end CG_DataStruct



/**
 * @author yd
 *
 */
class CH_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public CH_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("head", 42);
		this.setString("tail1", 42);
		this.setString("tail2", 42);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end CH_DataStruct



/**
 * @author yd
 *
 */
class CI_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public CI_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("head", 42);
		this.setString("tail1", 42);
		this.setString("tail2", 42);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end CI_DataStruct



/**
 * @author yd
 *
 */
class CJ_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public CJ_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("mode", 1);
		this.setString("liter", 7);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end CJ_DataStruct



/**
 * @author yd
 *
 */
class CK_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public CK_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("mode", 1);
		this.setString("liter", 7);
		this.setString("basePrice", 6);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end _DataStruct



/**
 * @author yd
 *
 */
class CN_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public CN_DataStruct() throws Exception {
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
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end CN_DataStruct



/**
 * @author yd
 *
 */
class CO_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public CO_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("depositNumber", 10);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end CO_DataStruct



/**
 * @author yd
 *
 */
class CP_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public CP_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("bonusNumber", 16);
		this.setString("pointScore", 10);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end CP_DataStruct



public class OdtDS {

	/**
	 * ODT 프로토콜 전문 DataStruct를 얻는다. 
	 * 
	 * @param command	: 명령 코드 
	 * @return
	 */
	public static DataStruct getDS(String command) throws Exception {
		DataStruct returnData = null;
		
		if (command.equals("C1")) {
			returnData = new C1_DataStruct();
			
		} else if (command.equals("C2")) {
			returnData = new C2_DataStruct();
			
		} else if (command.equals("C3")) {
			returnData = new C3_DataStruct();
			
		} else if (command.equals("C4")) {
			returnData = new C4_DataStruct();
			
		} else if (command.equals("C5")) {
			returnData = new C5_DataStruct();
			
		} else if (command.equals("C6")) {
			returnData = new C6_DataStruct();
			
		} else if (command.equals("C7")) {
			returnData = new C7_DataStruct();
			
		} else if (command.equals("C8")) {
			returnData = new C8_DataStruct();
			
		} else if (command.equals("CB")) {
			returnData = new CB_DataStruct();
			
		} else if (command.equals("CC")) {
			returnData = new CC_DataStruct();
			
		} else if (command.equals("CD")) {
			returnData = new CD_DataStruct();
			
		} else if (command.equals("CE")) {
			returnData = new CE_DataStruct();
			
		} else if (command.equals("CF")) {
			returnData = new CF_DataStruct();
			
		} else if (command.equals("CG")) {
			returnData = new CG_DataStruct();
			
		} else if (command.equals("CH")) {
			returnData = new CH_DataStruct();
			
		} else if (command.equals("CI")) {
			returnData = new CI_DataStruct();
			
		} else if (command.equals("CJ")) {
			returnData = new CJ_DataStruct();
			
		} else if (command.equals("CK")) {
			returnData = new CK_DataStruct();
			
		} else if (command.equals("CN")) {
			returnData = new CN_DataStruct();
			
		} else if (command.equals("CO")) {
			returnData = new CO_DataStruct();
			
		} else if (command.equals("CP")) {
			returnData = new CP_DataStruct();
			
		} else if (command.equals("BB")) {
			returnData = new BB_DataStruct();
			
		} else if (command.equals("T1")) {
			returnData = new T1_DataStruct();
			
		} else if (command.equals("T2")) {
			returnData = new T2_DataStruct();
			
		} else if (command.equals("T3")) {
			returnData = new T3_DataStruct();
			
		} else if (command.equals("T5")) {
			returnData = new T5_DataStruct();
			
		} else if (command.equals("T6")) {
			returnData = new T6_DataStruct();
			
		} else if (command.equals("TA")) {
			returnData = new TA_DataStruct();
			
		} else if (command.equals("TB")) {
			returnData = new TB_DataStruct();
			
		} else if (command.equals("TC")) {
			returnData = new TC_DataStruct();
			
		} else if (command.equals("TD")) {
			returnData = new TD_DataStruct();
			
		} else if (command.equals("TJ")) {
			returnData = new TJ_DataStruct();
			
		} else if (command.equals("BA")) {
			returnData = new BA_DataStruct();
			
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
				returnData = new T4_DataStruct(repNo);

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
class T1_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public T1_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("screen", 1);		// 상태표시
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end T1_DataStruct



/**
 * @author yd
 *
 */
class T2_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public T2_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("liter", 7);
		this.setString("price", 8);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end T2_DataStruct



/**
 * @author yd
 *
 */
class T3_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public T3_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("mode", 1);
		this.setString("carNumber", 16);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end _DataStruct



/**
 * @author yd
 *
 */
class T4_DataStruct extends DataStruct {
	/**
	 * @param repNo
	 */
	public T4_DataStruct(int repNo) throws Exception {
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
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
	
}	// end _DataStruct



/**
 * @author yd
 *
 */
class T5_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public T5_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("boyNumber", 4);
		this.setString("cardNumber", 18);
		this.setString("price", 8);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end T5_DataStruct



/**
 * @author yd
 *
 */
class T6_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public T6_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("cardType", 1);	// 카드종류
		this.setString("mode", 1);		// 처리구분
		this.setString("content", 40);	// 카드내용
		this.setString("cDate", 14);	// 승인일시
		this.setString("cNumber", 12);	// 승인번호 
		this.setString("liter", 7);		// 수량
		this.setString("basePrice", 6);	// 단가
		this.setString("price", 8);		// 금액 
		this.setString("bonusString", 5);
		this.setString("bonusCardNo", 40);	// 보너스카드번호
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end T6_DataStruct



/**
 * @author yd
 *
 */
class TA_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public TA_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("totalGauge", 10);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end TA_DataStruct



/**
 * @author yd
 *
 */
class TB_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public TB_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("keepNumber", 10);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end TB_DataStruct



/**
 * @author yd
 *
 */
class TC_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public TC_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end TC_DataStruct



/**
 * @author yd
 *
 */
class TD_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public TD_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("bonusNumber", 16);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end TD_DataStruct



/**
 * @author yd
 *
 */
class TJ_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public TJ_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("dealType", 1);
		this.setString("dealAmount", 9);
		this.setString("keyInType", 1);
		this.setString("certiNumber", 39);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end TD_DataStruct

