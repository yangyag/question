package com.gsc.kixxhub.module.pumpm.pump.data.cust;

import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;

public class LimitAmount {

	private String limit = "" ;
	// 01 : ���� , , 02 : ���� 
	private int priceOrLiter = 0 ;
	/**
	 * ����� �� �ִ� �ܷ�.
	 * �ŷ�ó�� �ѵ� ���� ���, ������ �ŷ�ó�� �ѵ��� �������� ������ �ѵ� �����ϱ� ������, ������ ��� �� �� �ִ� �ܷ�
	 * �� �̸� �����Ѵ�.
	 */
	private String remainsAmount = "" ;
	
	private String usedAmount = "" ;
	
	public LimitAmount() {}

	public LimitAmount(int priceOrLiter, String limit , String usedAmount) {
		this.priceOrLiter = priceOrLiter ;
		this.limit = limit ;
		this.usedAmount = usedAmount ;
		this.remainsAmount = GlobalUtility.substract(limit, usedAmount) ;
	}

	public LimitAmount(int priceOrLiter, String limit , String usedAmount, String remainsAmount) {
		this.priceOrLiter = priceOrLiter ;
		this.limit = limit ;
		this.usedAmount = usedAmount ;
		this.remainsAmount = remainsAmount ;
	}
	
	public String getLimit() {
		return limit;
	}
	
	public int getPricePrLiter() {
		return priceOrLiter;
	}
	
	public String getRemainsAmount() {
		return remainsAmount;
	}
	
	
	public String getUsedAmount() {
		return usedAmount;
	}

	public void print(String prefix) {
			LogUtility.getPumpMLogger().debug(new StringBuffer( prefix).append("[LimitAmount]").append(" ")
					.append("#").append("priceOrLiter=").append(priceOrLiter ) 
					.append("#").append("limit=").append(limit ) 
					.append("#").append("usedAmount=").append(usedAmount).toString()) ;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

	public void setPricePrLiter(int priceOrLiter) {
		this.priceOrLiter = priceOrLiter;
	}

	public void setRemainsAmount(String remainsAmount) {
		this.remainsAmount = remainsAmount;
	}

	public void setUsedAmount(String usedAmount) {
		this.usedAmount = usedAmount;
	}
	
	
}
