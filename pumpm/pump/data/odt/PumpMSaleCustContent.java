package com.gsc.kixxhub.module.pumpm.pump.data.odt;

import com.gsc.kixxhub.common.data.posdata.POSHeader;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.module.pumpm.pump.util.PumpLogUtil;

/**
 * ������ �� ����ODT �� ��� �ŷ�ó ���� ������ �ۼ����� ������, �� ��� ������� ���� ��û ���� �����Ϳ� ��û�� ���� ���� �����͸� �����Ѵ�.
 * �̴� ������ ���� �뵵�� ���ȴ�.
 * 	������
 * 		ī�� ���� ��û�� ������� ���� �ܰ��ν� ��û�� ������, Pump M ���� �̹� ����Ǿ��� �ִ� �ǸŴܰ��� �����Ͽ� ��û�Ѵ�.
 * 		�ܻ� �ŷ�ó�� ��� �Ǹ� �Ϸ�� �� ������ �̿��Ͽ� UPOSMessage �� �����Ͽ� POS �� �����Ѵ�.
 * 	�Ҹ� ����
 * 		�ܻ� �ŷ�ó�� ��� �Ǹ� �Ϸ�� �� ������ �̿��Ͽ� UPOSMessage �� �����Ͽ� POS �� �����Ѵ�.
 * 
 * �Ʒ� ������ �ʱ�ȭ�� TR Ȥ�� SH ���� ���Ž� �ʱ�ȭ �Ѵ�.
 * 
 * @author WooChul Jung
 *
 */
public class PumpMSaleCustContent {
	
	private POSHeader posPumpM = null ;
	private WorkingMessage workMsg = null ;
	// 2016.05.25 WooChul Jung. Add Masking-Credit Card No field for Field-Discount (��������)
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
