package com.gsc.kixxhub.module.pumpm.pump.data.odt;

import com.gsc.kixxhub.common.data.posdata.POSHeader;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.module.pumpm.pump.util.PumpLogUtil;

/**
 * 충전기 및 셀프ODT 의 경우 거래처 관련 전문의 송수신이 있으며, 이 경우 주유기로 부터 요청 받은 데이터와 요청에 의한 응답 데이터를 저장한다.
 * 이는 다음과 같은 용도로 사용된다.
 * 	충전기
 * 		카드 승인 요청시 충전기는 노즐 단가로써 요청을 하지만, Pump M 에서 이미 저장되어져 있는 판매단가로 변경하여 요청한다.
 * 		외상 거래처의 경우 판매 완료시 이 정보를 이용하여 UPOSMessage 를 구성하여 POS 로 전송한다.
 * 	소모 셀프
 * 		외상 거래처의 경우 판매 완료시 이 정보를 이용하여 UPOSMessage 를 구성하여 POS 로 전송한다.
 * 
 * 아래 정보의 초기화는 TR 혹은 SH 전문 수신시 초기화 한다.
 * 
 * @author WooChul Jung
 *
 */
public class PumpMSaleCustContent {
	
	private POSHeader posPumpM = null ;
	private WorkingMessage workMsg = null ;
	// 2016.05.25 WooChul Jung. Add Masking-Credit Card No field for Field-Discount (현장할인)
	private String cardNo = "" ;
	private String promptDiscount_yn = "0" ;
	
	
	public POSHeader getPosPumpM() {
		return posPumpM;
	}
	
	public WorkingMessage getWorkMsg() {
		return workMsg;
	}

	public void init() {
		posPumpM = null ;
		workMsg = null ;
		cardNo = "" ;
		promptDiscount_yn = "0" ;
	}

	public void print() {
		LogUtility.getPumpMLogger().debug("[PumpMSaleCustContent]") ;
		PumpLogUtil.printContent(posPumpM) ;
		PumpLogUtil.printContent(workMsg) ;
	}

	public void setPosPumpM(POSHeader posPumpM) {
		this.posPumpM = posPumpM;
	}

	public void setWorkMsg(WorkingMessage workMsg) {
		this.workMsg = workMsg;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getPromptDiscount_yn() {
		return promptDiscount_yn;
	}

	public void setPromptDiscount_yn(String promptDiscount_yn) {
		this.promptDiscount_yn = promptDiscount_yn;
	}
	
}
