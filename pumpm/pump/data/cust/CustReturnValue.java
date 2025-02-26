package com.gsc.kixxhub.module.pumpm.pump.data.cust;

import com.gsc.kixxhub.common.data.ICode;
import com.gsc.kixxhub.common.data.IConstant;
import com.gsc.kixxhub.common.data.ICustConstant;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_CODEMASTERHandler;
import com.gsc.kixxhub.common.utility.log.LogUtility;

public class CustReturnValue {

	private String amount1 = "" ;									// 전표한도 1회 정량
	private String basePrice = "0" ;								// 할인 전 단가
	private String cardno_nbr_cust = "" ;							// 거래처 카드 번호
	private String carno_nbr = "" ;									// 차량 번호
	private String cust_cd_item = ""	;							// 거래처 유형 (21:집단거래처 -> VIP 인지 여부를 조사할때 사용)
	private String cust_code = "" ;									// 거래처 코드
	private String cust_name = "" ;									// 거래처 명
	private String discountBasePrice = "0" ;						// 할인 후 단가
	private String keepissue_ind = "" ;								// 보관증발행여부
	private LimitAmount limitAmount = null ;						// 한도 관련 Object
	// ksm 2013.10.08 
	private String plunit_yn = "0";									// PL적용단가인지 확인.
	private String proc_ind_overlimit = ICode.PROC_IND_OVERLIMIT_01;	// 한도초과판매처리구분
	private String rcptsheetissue_code_amtsale = "" ;				// 매출금액처리구분	
	private String rcptsheetissue_ind_prtcarno = "" ;				// 영수증 차량번호 출력 여부
	private String rcptunitprc_ind_prt = "" ; 						// 영수증 단가 출력 여부
	private int state = 0 ;
	private String taxfree_cd = "" ;								// 과면세 구분 (01:과세 , 02:면세)
	private String trans_code_status = ICode.TRANS_CODE_STATUS_02 ;	// 거래 상태 (Default : 정지) 
	private int type = 0 ;
	
	private String unitprc_code_stdn = "" ;							// 단가 기준 (03:면세단가)
	
	public CustReturnValue() {
		setType(ICustConstant.TYPE_DEFAULT) ;
		setState(ICustConstant.STATE_0) ;		
	}	
	
	public CustReturnValue(int state, int type, String basePrice, String discountBasePrice, LimitAmount limitAmount) {
		this.state = state ;
		this.type = type ;
		this.basePrice = basePrice ;
		this.discountBasePrice = discountBasePrice ;
		this.limitAmount = limitAmount ;
	}

	public String getAmount1() {
		return amount1;
	}

	public String getBasePrice() {
		return basePrice;
	}

	public String getCardno_nbr_cust() {
		return cardno_nbr_cust;
	}

	public String getCarno_nbr() {
		return carno_nbr;
	}

	public String getCust_cd_item() {
		return cust_cd_item;
	}

	public String getCust_code() {
		return cust_code;
	}
	
	public String getCust_name() {
		return cust_name;
	}
	
	public String getDiscountBasePrice() {
		return discountBasePrice;
	}

	public String getKeepissue_ind() {
		return keepissue_ind;
	}

	public LimitAmount getLimitAmount() {
		return limitAmount;
	}

	public String getPlunit_yn() {
		return plunit_yn;
	}

	public String getProc_ind_overlimit() {
		return proc_ind_overlimit;
	}

	public String getRcptsheetissue_code_amtsale() {
		return rcptsheetissue_code_amtsale;
	}

	public String getRcptsheetissue_ind_prtcarno() {
		return rcptsheetissue_ind_prtcarno;
	}

	public String getRcptunitprc_ind_prt() {
		return rcptunitprc_ind_prt;
	}

	public int getState() {
		return state;
	}

	public String getTaxfree_cd() {
		return taxfree_cd;
	}

	public String getTrans_code_status() {
		return trans_code_status;
	}

	public int getType() {
		return type;
	}

	public String getUnitprc_code_stdn() {
		return unitprc_code_stdn;
	}

	/**
	 * 할인된 단가인지 여부를 체크
	 * @return
	 * 		true : 할인단가일 경우
	 * 		false : 할인이 안된 경우
	 */
	public boolean isDiscount() {
		if ((discountBasePrice == null) || (discountBasePrice.equals(""))) {
			return false ;
		} else {
			if (discountBasePrice.equals(basePrice)) {
				return false ;
			}
		}
		return true ;
	}

	public void print() {
			LogUtility.getPumpMLogger().debug(new StringBuffer("[CustReturnValue]").append(" ")
					.append("#").append("state=").append(state ) 
					.append("#").append("type=").append(type ) 
					.append("#").append("거래처 코드=").append(cust_code ) 
					.append("#").append("거래처 카드 번호=").append(cardno_nbr_cust ) 
					.append("#").append("차량 번호=").append(carno_nbr ) 
					.append("#").append("거래처 명=").append(cust_name ) 
					.append("#").append("거래처 유형=").append(cust_cd_item ) 
					.append("#").append("영수증 단가 출력 여부=").append(rcptunitprc_ind_prt ) 
					.append("#").append("단가 기준=").append(unitprc_code_stdn ) 
					.append("#").append("거래 상태=").append(trans_code_status ) 
					.append("#").append("매출금액처리구분=").append(rcptsheetissue_code_amtsale ) 
					.append("#").append("영수증 차량번호 출력 여부=").append(rcptsheetissue_ind_prtcarno ) 
					.append("#").append("보관증발행여부=").append( keepissue_ind ) 
					.append("#").append("할인 전 단가=").append(basePrice )
					.append("#").append("할인 후 단가=").append(discountBasePrice ) 
					.append("#").append("전표한도 1회 정량=").append(amount1 ) 
					.append("#").append("한도초과판매처리구분=").append(proc_ind_overlimit )
					.append("#").append("PL적용단가인지 확인=").append(plunit_yn)
					.append("#").append("과면세 구분=").append(taxfree_cd)
					.toString());
			
			if (limitAmount != null)
				limitAmount.print("");
	}

	public void setAmount1(String amount1) {
		this.amount1 = amount1;
	}

	public void setBasePrice(String basePrice) {
		this.basePrice = basePrice;
	}

	public void setCardno_nbr_cust(String cardno_nbr_cust) {
		this.cardno_nbr_cust = cardno_nbr_cust;
	}

	public void setCarno_nbr(String carno_nbr) {
		this.carno_nbr = carno_nbr;
	}

	public void setCust_cd_item(String cust_cd_item) {
		this.cust_cd_item = cust_cd_item;
	}

	public void setCust_code(String cust_code) {
		this.cust_code = cust_code;
	}

	public void setCust_name(String cust_name) {
		this.cust_name = cust_name;
	}

	public void setDiscountBasePrice(String discountBasePrice) {
		this.discountBasePrice = discountBasePrice;
	}
		
	public void setKeepissue_ind(String keepissue_ind) {
		String codemasterKeepissue_ind = "0" ; 	//Default
		try {
			codemasterKeepissue_ind = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0019);
			if (codemasterKeepissue_ind.equals("0") || ICode.KEEPISSUE_IND_0.equals(keepissue_ind)) {
				this.keepissue_ind = ICode.KEEPISSUE_IND_0 ;
			} else {
				this.keepissue_ind = ICode.KEEPISSUE_IND_1 ;
			}
			LogUtility.getPumpMLogger().debug("[Pump M] keepissue_ind = " + keepissue_ind + ": " +
					" codemasterKeepissue_ind = " + codemasterKeepissue_ind) ;
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
	}

	public void setLimitAmount(LimitAmount limitAmount) {
		this.limitAmount = limitAmount;
	}

	public void setPlunit_yn(String plunit_yn) {
		this.plunit_yn = plunit_yn;
	}

	public void setProc_ind_overlimit(String proc_ind_overlimit) {
		this.proc_ind_overlimit = proc_ind_overlimit;
	}
	
	public void setRcptsheetissue_code_amtsale(String rcptsheetissue_code_amtsale) {
		this.rcptsheetissue_code_amtsale = rcptsheetissue_code_amtsale;
	}

	public void setRcptsheetissue_ind_prtcarno(String rcptsheetissue_ind_prtcarno) {
		this.rcptsheetissue_ind_prtcarno = rcptsheetissue_ind_prtcarno;
	}

	public void setRcptunitprc_ind_prt(String rcptunitprc_ind_prt) {
		this.rcptunitprc_ind_prt = rcptunitprc_ind_prt;
	}

	public void setState(int state) {
		this.state = state;
	}

	public void setTaxfree_cd(String taxfree_cd) {
		if (taxfree_cd == null) this.taxfree_cd = "" ;
		else this.taxfree_cd = taxfree_cd;
	}

	public void setTrans_code_status(String trans_code_status) {
		this.trans_code_status = trans_code_status;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setUnitprc_code_stdn(String unitprc_code_stdn) {
		this.unitprc_code_stdn = unitprc_code_stdn;
	}

}
