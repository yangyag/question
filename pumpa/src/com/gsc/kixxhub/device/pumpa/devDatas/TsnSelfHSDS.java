// - 추가 2012.08, dhp
package com.gsc.kixxhub.device.pumpa.devDatas;

import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.Change;
import com.gsc.kixxhub.device.pumpa.common.DataStruct;

//-- ES : ODT 이상정보 (KH <- ODT) --//	
class TH_ES_DataStruct extends DataStruct {

	public TH_ES_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");

		this.setString("command", 2);
		this.setString("status", 3);	
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

/*
 * 이하는 신규 정의한 전문
 */
//-- FC : POS 가득주유 통제 (KH -> ODT) --//	
class TH_FC_DataStruct extends DataStruct {

	public TH_FC_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");

		this.setString("command", 2);
		this.setString("nozzleNo", 2);	
		this.setString("useFullPumping", 1);	// 가득주유 허용여부
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- GD : 영수증 데이터 수신완료 (KH <- ODT) --//	
class TH_GD_DataStruct extends DataStruct {

	public TH_GD_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");

		this.setString("command", 2);
		this.setString("trNo", 4);		// 거래번호
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

/**
 * 
 * @author dhp
 * 신형 다쓰노셀프(현성) 신규 스펙
 *
 */

/*
 *  초기화 작업
 */

//-- IQ : 초기화 정보 요청 (KH <- ODT) --//	
class TH_IQ_DataStruct extends DataStruct {

	public TH_IQ_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");

		this.setString("command", 2);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- JP : 주유기 그룹 리스트 전송 (KH -> ODT) --//	
class TH_JP_DataStruct extends DataStruct {

	public TH_JP_DataStruct(int nozCnt) throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");

		this.setString("command", 2);
		for(int i=0; i<nozCnt; i++)
			this.setString("nozzleNo" + i, 2);	// 주유기 번호(최대 6개)
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

/*
 *  결제승인 처리
 */

//-- KA : 승인허가 (KH -> ODT) --//	
class TH_KA_DataStruct extends DataStruct {

	public TH_KA_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("trNo", 4);		// 거래번호
		this.setString("wcc", 1);		// 'A'
		this.setString("value", 7);		// 금액 또는 수량(소수점 3자리)
		//this.setString("message", 48); 	// 메시지(가변, 최대 48 byte)
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- KB : 승인허가(할인주유) (KH -> ODT) --//	
class TH_KB_DataStruct extends DataStruct {

	public TH_KB_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("trNo", 4);		// 거래번호
		this.setString("wcc", 1);		// 'A'
		this.setString("basePrice1", 4);// 할인전 단가
		this.setString("basePrice2", 4);// 할인후 단가
		this.setString("liter", 7);		// 수량(소수점 3자리)
		this.setString("price", 7);		// 금액
		this.setString("message", 48); 	// 메시지(가변, 최대 48 byte)
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}


//-- KC :  셀프외상 및 유대입금 요청에 대한 허가 (KH -> ODT) --//	
class TH_KC_DataStruct extends DataStruct {

	public TH_KC_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");
		

		this.setString("command", 2);
		this.setString("trNo", 4);		// 거래번호
		this.setString("wcc", 1);		// 'A'
		this.setString("basePrice1", 4);// 할인전 단가
		this.setString("basePrice2", 4);// 할인후 단가
		this.setString("liter", 7);		// 수량(소수점 3자리)
		this.setString("price", 7);		// 금액
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- KD : 신용승인요청 (KH <- ODT) --//	
class TH_KD_DataStruct extends DataStruct {

	public TH_KD_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("nozzleNo", 2);
		this.setString("sellerNo", 3); 	// 판매원 번호
		this.setString("saleType", 1);	// 판매구분(A:금액, Q:수량: F: Full tank)
		this.setString("value", 7);		// 금액 또는 수량(소수점 3자리)
		this.setString("wcc1", 1);		// 'A'
		this.setString("cardLen1", 2);	// 카드길이1
		this.setString("cardNo1", 38);	// 카드번호1(신용카드)
		this.setString("monthCnt", 2);	// 할부개월수
		this.setString("fs", 1);		// FS
		this.setString("wcc2", 1);		// 'A'
		this.setString("cardLen2", 2);	// 카드길이2
		this.setString("cardNo2", 38);	// 카드번호2(보너스카드)
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- KE : 현금승인요청 (KH <- ODT) --//	
class TH_KE_DataStruct extends DataStruct {

	public TH_KE_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("nozzleNo", 2);
		this.setString("sellerNo", 3); 	// 판매원 번호
		this.setString("saleType", 1);	// 판매구분(A:금액, Q:수량, F:Full tank)
		this.setString("value", 7);		// 금액 또는 수량(소수점 3자리)
		this.setString("wcc1", 1);		// 'A'	
		this.setString("cardLen1", 2);	// 카드길이1
		this.setString("cardNo1", 38);	// 카드번호1
		this.setString("fs", 1);		// FS
		this.setString("wcc2", 1);		// 'A'
		this.setString("cardLen2", 2); 	// 현금영수증 정보 길이
		this.setString("cardNo2", 38); 	// 현금영수증 정보
		this.setString("target", 1); 	// 발행대상(0:현금영수증 미발행, 1:소비자, 2:사업자)
		this.setString("inputCash", 1); // 최종입금액
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}


//-- KF : 현금입금 정보 (KH <- ODT) --//	
class TH_KF_DataStruct extends DataStruct {

	public TH_KF_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("nozzleNo", 2);
		this.setString("sellerNo", 3); 	// 판매원 번호
		this.setString("saleType", 1);	// 판매구분(A:금액, Q:수량, F:Full tank)
		this.setString("value", 7);		// 금액 또는 수량(소수점 3자리)
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}


//-- KG :  승인거부 (KH -> ODT) --//	
class TH_KG_DataStruct extends DataStruct {

	public TH_KG_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("errorCode", 4); // 오류번호
		this.setString("message", 48); 	// 메시지(가변, 최대 48 byte)
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}


//-- KI : 셀프ODT 외상세팅요청에 대한 외상유대입금 허가 (KH -> ODT) --//	
class TH_KI_DataStruct extends DataStruct {

	public TH_KI_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");		

		this.setString("command", 2);
		this.setString("trNo", 4);		// 거래번호
		this.setString("wcc", 1);		// 'A'
		this.setString("basePrice1", 4);// 할인전 단가
		this.setString("basePrice2", 4);// 할인후 단가
		this.setString("liter", 7);		// 수량(소수점 3자리)
		this.setString("price", 7);		// 금액
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- KJ : 셀프ODT 외상세팅요청에 대한 외상허가 (KH -> ODT) --//	
class TH_KJ_DataStruct extends DataStruct {

	public TH_KJ_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");		

		this.setString("command", 2);
		this.setString("trNo", 4);		// 거래번호
		this.setString("wcc", 1);		// 'A'
		this.setString("basePrice1", 4);// 할인전 단가
		this.setString("basePrice2", 4);// 할인후 단가
		this.setString("liter", 7);		// 수량(소수점 3자리)
		this.setString("price", 7);		// 금액
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}


//-- KK : 외상승인요청 (KH <- ODT) --//	
class TH_KK_DataStruct extends DataStruct {

	public TH_KK_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("nozzleNo", 2);
		this.setString("sellerNo", 3); 	// 판매원 번호
		this.setString("saleType", 1);	// 판매구분(A:금액, Q:수량, F:Full tank)
		this.setString("value", 7);		// 금액 또는 수량(소수점 3자리)
		this.setString("wcc1", 1);		// 'A'		
		this.setString("cardLen1", 2);	// 카드길이1
		this.setString("cardNo1", 38);	// 카드번호1(고객카드)
		this.setString("fs", 1);		// FS
		this.setString("wcc2", 1);		// 'A'
		this.setString("cardLen2", 2);	// 카드길이2
		this.setString("cardNo2", 38);	// 카드번호2(보너스카드)
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- KP : 셀프ODT 옵션사항 (KH -> ODT) --//	
class TH_KP_DataStruct extends DataStruct {

	public TH_KP_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");

		this.setString("command", 2);
		this.setString("setSeller", 1);		// 판매원 지정유무
		this.setString("receiptType", 1);	// 영수증 발행방식
		this.setString("waitTime", 3);		// 승인대기시간
		this.setString("storeCode", 5);		// 주유소코드
		this.setString("introduce", 40);	// ODT 인사말
		this.setString("receiptHead", 10);	// 영수증 헤더 데이터
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- KU : 현금거래처(현금승인요청) (KH <- ODT) ---//	
class TH_KU_DataStruct extends DataStruct {

	public TH_KU_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("nozzleNo", 2);
		this.setString("sellerNo", 3); 	// 판매원 번호
		this.setString("basePrice1", 4);// 할인전 단가
		this.setString("basePrice2", 4);// 할인후 단가	
		this.setString("liter", 7);		// 수량
		this.setString("price", 7);		// 금액
		this.setString("inputCash", 7);	// 투입합계금액
		this.setString("cardLen1", 2);	// 카드길이1
		this.setString("cardNo1", 38);	// 카드번호1(현금영수증번호)
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- KV : 현금거래처(신용승인요청) (KH <- ODT) --//	
class TH_KV_DataStruct extends DataStruct {

	public TH_KV_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("nozzleNo", 2);
		this.setString("sellerNo", 3); 	// 판매원 번호
		this.setString("basePrice1", 4);// 할인전 단가
		this.setString("basePrice2", 4);// 할인후 단가	
		this.setString("liter", 7);		// 수량
		this.setString("price", 7);		// 금액
		this.setString("wcc1", 1);		// 'A'		
		this.setString("cardLen1", 2);	// 카드길이1
		this.setString("cardNo1", 38);	// 카드번호1
		this.setString("monthCnt", 2);	// 할부개월수
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- KX : 현금승인요청-현금영수증 포함 (KH <- ODT) --//	
class TH_KX_DataStruct extends DataStruct {

	public TH_KX_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("nozzleNo", 2);
		this.setString("sellerNo", 3); 	// 판매원 번호
		this.setString("saleType", 1);	// 판매구분(A:금액, Q:수량, F:Full tank)
		this.setString("value", 7);		// 금액 또는 수량(소수점 3자리)
		this.setString("wcc1", 1);		// 'A'		
		this.setString("cardLen1", 2);	// 카드길이1
		this.setString("cardNo1", 38);	// 카드번호1
		this.setString("fs", 1);		// FS
		this.setString("wcc2", 1);		// 'A'
		this.setString("cardLen2", 2); 	// 현금영수증 정보 길이
		this.setString("cardNo2", 38); 	// 현금영수증 정보
		this.setString("target", 1); 	// 발행대상(0:현금영수증 미발행, 1:소비자, 2:사업자)
		this.setString("inputCash", 7); // 최종입금액
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- KY : 현금거래처(현금승인요청-현금영수증번호) (KH <- ODT) --//	
class TH_KY_DataStruct extends DataStruct {

	public TH_KY_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("nozzleNo", 2);
		this.setString("sellerNo", 3); 	// 판매원 번호
		this.setString("basePrice1", 4);// 할인전 단가
		this.setString("basePrice2", 4);// 할인후 단가	
		this.setString("liter", 7);		// 수량
		this.setString("price", 7);		// 금액
		this.setString("wcc1", 1);		// 'A'		
		this.setString("cardLen1", 2);	// 카드길이1
		this.setString("cardNo1", 38);	// 카드번호1(현금영수증번호)
		this.setString("target", 1);	// 발행대상
		this.setString("inputCash", 7);	// 투입합계금액
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- LA : 외상승인요청-2 (KH <- ODT) --//	
class TH_LA_DataStruct extends DataStruct {

	public TH_LA_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("nozzleNo", 2);
		this.setString("sellerNo", 3); 	// 판매원 번호
		this.setString("saleType", 1);	// 판매구분(A:금액, Q:수량, F:Full tank)
		this.setString("limitBase", 2);	// 한도기준
		this.setString("avaLimit", 18);	// 잔여한도
		this.setString("value", 7);	    // 신청수량/금액
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- LD : GS포인트승인요청 (KH <- ODT) --//	
class TH_LD_DataStruct extends DataStruct {

	public TH_LD_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("nozzleNo", 2);
		this.setString("sellerNo", 3); 	// 판매원 번호
		this.setString("saleType", 1);	// 판매구분(A:금액, Q:수량, F:Full tank)
		this.setString("value", 7);		// 금액 또는 수량(소수점 3자리)
		this.setString("wcc1", 1);		// 'A'		
		this.setString("cardLen1", 2);	// 카드길이1
		this.setString("cardNo1", 37);	// 카드번호1(고객카드)
		this.setString("fs", 1);		// FS
		this.setString("pinLen", 2);	// 비밀번호 길이
		this.setString("pin", 15);		// 비밀번호
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}


//-- LS : 셀프ODT 외상세팅요청에 대한 거래처정보 송신-외상처리를 위해 신규추가 (KH -> ODT) --//	
class TH_LS_DataStruct extends DataStruct {

	public TH_LS_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");

		this.setString("command", 2);
		this.setString("trNo", 4);			// 거래번호
		this.setString("wcc", 1);			// 'A'
		this.setString("basePrice1", 4);	// 할인전 단가
		this.setString("basePrice2", 4);	// 할인후 단가
		this.setString("liter", 7);			// 수량(소수점 3자리)
		this.setString("price", 7);			// 금액
		this.setString("driveName", 20);	// 운전자명(거래처명)
		this.setString("carNo", 18);		// 차량번호
		this.setString("cardAdjInd", 02);	// 고객기준
		this.setString("limitBase", 02);	// 한도기준
		this.setString("limit", 18);		// 총한도
		this.setString("accLimit", 18);		// 누적사용한도
		this.setString("message", 48);		// 메시지
				
		this.setByte("etx");
		this.setByte("bcc");
	}
}


//-- LT : POS 선결제 세팅요청 (KH -> ODT) --//	
class TH_LT_DataStruct extends DataStruct {

	public TH_LT_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");

		this.setString("command", 2);
		this.setString("trNo", 4);		// 거래번호
		this.setString("wcc", 1);		// 
		this.setString("value", 2);		// 수량 또는 금액
		this.setString("message", 2);	// 메시지(가변)
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- NG : 작업요청 거부 (KH -> ODT) --//	
class TH_NG_DataStruct extends DataStruct {

	public TH_NG_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("message", 48); // 메시지(가변, 최대 48 byte)
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- NR : 영수증 발행요청 (KH <- ODT) --//	
class TH_NR_DataStruct extends DataStruct {

	public TH_NR_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");

		this.setString("command", 2);
		this.setString("trNo", 4);		// 거래번호
		this.setString("currPage", 3);	// 현재페이지
		this.setString("totalPage", 1);	// 전체페이지
		this.setString("data", 4);		// 데이터(가변, 최대 1024 byte)
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- OT : ODT 상태정보 (KH <- ODT) --//	
class TH_OT_DataStruct extends DataStruct {

	public TH_OT_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");

		this.setString("command", 2);
		this.setString("datas", 10);	
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- PS : 영수증 데이터 송신 (KH  ODT) --//	
class TH_PS_DataStruct extends DataStruct {

	public TH_PS_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");

		this.setString("command", 2);
		this.setString("trNo", 4);		// 거래번호
		this.setString("currPage", 1);	// 현재페이지
		this.setString("totPage", 1);	// 전체페이지
		this.setString("content", 1024);	// 영수증 데이터
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- SP : 주유기 상태 송신 (KH -> ODT) --//	
class TH_SP_DataStruct extends DataStruct {

	public TH_SP_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");

		this.setString("command", 2);
		this.setString("nozzleNo", 2);	// 주유기번호
		this.setString("state", 2);		// 상태(AQ:주유허가 요청, LK:노들다운, PP:주유중, UL:노즐업 또는 다운, 세팅됨
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- YL : 유종정보 송신 (KH -> ODT) --//	
class TH_YL_DataStruct extends DataStruct {

	public TH_YL_DataStruct(int nozCnt) throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");

		this.setString("command", 2);
		this.setString("oilCode", 4);	// 유종코드
		this.setString("oilName", 10);	// 유종명
		//this.setString("reserved", 6);	// 의미없는 데이터
		for(int i=0; i<nozCnt; i++)
			this.setString("nozzleNo" + i, 2);	// 주유기 번호
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

public class TsnSelfHSDS {
	
	/**
	 * 프로토콜 전문 DataStruct를 얻는다. 
	 * 
	 * @param command	: 명령 코드 
	 * @return
	 */
	public static DataStruct getDS(String command) {
		DataStruct returnData = null;
		
		try {
			if (command.equals("IQ")) {
				returnData = new TH_IQ_DataStruct();
			} else if (command.equals("KD")) {
				returnData = new TH_KD_DataStruct();
			} else if (command.equals("KK")) {
				returnData = new TH_KK_DataStruct();
			} else if (command.equals("LA")) {
				returnData = new TH_LA_DataStruct();
			} else if (command.equals("KE")) {
				returnData = new TH_KE_DataStruct();
			} else if (command.equals("KX")) {
				returnData = new TH_KX_DataStruct();
			} else if (command.equals("KU")) {
				returnData = new TH_KU_DataStruct();
			} else if (command.equals("KY")) {
				returnData = new TH_KY_DataStruct();
			} else if (command.equals("KV")) {
				returnData = new TH_KV_DataStruct();
			} else if (command.equals("KF")) {
				returnData = new TH_KF_DataStruct();
			} else if (command.equals("KA")) {
				returnData = new TH_KA_DataStruct();
			} else if (command.equals("KB")) {
				returnData = new TH_KB_DataStruct();
			} else if (command.equals("NG")) {
				returnData = new TH_NG_DataStruct();
			} else if (command.equals("KG")) {
				returnData = new TH_KG_DataStruct();
			} else if (command.equals("KC")) {
				returnData = new TH_KC_DataStruct();
			} else if (command.equals("KI")) {
				returnData = new TH_KI_DataStruct();
			} else if (command.equals("KJ")) {
				returnData = new TH_KJ_DataStruct();
			} else if (command.equals("LS")) {
				returnData = new TH_LS_DataStruct();
			} else if (command.equals("LD")) {
				returnData = new TH_LD_DataStruct();
			} else if (command.equals("NR")) {
				returnData = new TH_NR_DataStruct();
			} else if (command.equals("OT")) {
				returnData = new TH_OT_DataStruct();
			} else if (command.equals("ES")) {
				returnData = new TH_ES_DataStruct();
			} else if (command.equals("GD")) {
				returnData = new TH_GD_DataStruct();
			} else if (command.equals("SP")) {
				returnData = new TH_SP_DataStruct();
			} else if (command.equals("LT")) {
				returnData = new TH_LT_DataStruct();
			} else if (command.equals("FC")) {
				returnData = new TH_FC_DataStruct();
			} else if (command.equals("KP")) {
				returnData = new TH_KP_DataStruct();
				
			} else {
				returnData = null;
			} // end if
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return returnData;
		
	}
	
	public static DataStruct getDS(String command, int trTypeSize) {
		DataStruct returnData = null;
		
		try {
			if (command.equals("YL")) {
				returnData = new TH_YL_DataStruct(trTypeSize);
			} else if (command.equals("JP")) {
				returnData = new TH_JP_DataStruct(trTypeSize);
			} else {
				returnData = null;
			}	// end if
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return returnData;
		
	}	// end getDS
	
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
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		LogUtility.getPumpALogger().debug("for ODT======" + getOdtNo_forODT("83"));

		LogUtility.getPumpALogger().debug("for POS======" + getOdtNo_forPOS("SD"));
	}
}

