package com.gsc.kixxhub.module.pumpm.pump.data.cust;

import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;

public class LimitAmount {

	private String limit = "" ;
	// 01 : 정액 , , 02 : 정량 
	private int priceOrLiter = 0 ;
	/**
	 * 사용할 수 있는 잔량.
	 * 거래처별 한도 고객의 경우, 전제가 거래처별 한도를 기준으로 차량별 한도 차감하기 때문에, 전제로 사용 할 수 있는 잔량
	 * 을 미리 조사한다.
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
