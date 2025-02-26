package com.gsc.kixxhub.module.pumpm.pump.data.cust;

import com.gsc.kixxhub.common.data.ICode;
import com.gsc.kixxhub.common.data.IConstant;
import com.gsc.kixxhub.common.data.ICustConstant;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_CODEMASTERHandler;
import com.gsc.kixxhub.common.utility.log.LogUtility;

public class CustReturnValue {

	private String amount1 = "" ;									// ��ǥ�ѵ� 1ȸ ����
	private String basePrice = "0" ;								// ���� �� �ܰ�
	private String cardno_nbr_cust = "" ;							// �ŷ�ó ī�� ��ȣ
	private String carno_nbr = "" ;									// ���� ��ȣ
	private String cust_cd_item = ""	;							// �ŷ�ó ���� (21:���ܰŷ�ó -> VIP ���� ���θ� �����Ҷ� ���)
	private String cust_code = "" ;									// �ŷ�ó �ڵ�
	private String cust_name = "" ;									// �ŷ�ó ��
	private String discountBasePrice = "0" ;						// ���� �� �ܰ�
	private String keepissue_ind = "" ;								// ���������࿩��
	private LimitAmount limitAmount = null ;						// �ѵ� ���� Object
	// ksm 2013.10.08 
	private String plunit_yn = "0";									// PL����ܰ����� Ȯ��.
	private String proc_ind_overlimit = ICode.PROC_IND_OVERLIMIT_01;	// �ѵ��ʰ��Ǹ�ó������
	private String rcptsheetissue_code_amtsale = "" ;				// ����ݾ�ó������	
	private String rcptsheetissue_ind_prtcarno = "" ;				// ������ ������ȣ ��� ����
	private String rcptunitprc_ind_prt = "" ; 						// ������ �ܰ� ��� ����
	private int state = 0 ;
	private String taxfree_cd = "" ;								// ���鼼 ���� (01:���� , 02:�鼼)
	private String trans_code_status = ICode.TRANS_CODE_STATUS_02 ;	// �ŷ� ���� (Default : ����) 
	private int type = 0 ;
	
	private String unitprc_code_stdn = "" ;							// �ܰ� ���� (03:�鼼�ܰ�)
	
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
	 * ���ε� �ܰ����� ���θ� üũ
	 * @return
	 * 		true : ���δܰ��� ���
	 * 		false : ������ �ȵ� ���
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
					.append("#").append("�ŷ�ó �ڵ�=").append(cust_code ) 
					.append("#").append("�ŷ�ó ī�� ��ȣ=").append(cardno_nbr_cust ) 
					.append("#").append("���� ��ȣ=").append(carno_nbr ) 
					.append("#").append("�ŷ�ó ��=").append(cust_name ) 
					.append("#").append("�ŷ�ó ����=").append(cust_cd_item ) 
					.append("#").append("������ �ܰ� ��� ����=").append(rcptunitprc_ind_prt ) 
					.append("#").append("�ܰ� ����=").append(unitprc_code_stdn ) 
					.append("#").append("�ŷ� ����=").append(trans_code_status ) 
					.append("#").append("����ݾ�ó������=").append(rcptsheetissue_code_amtsale ) 
					.append("#").append("������ ������ȣ ��� ����=").append(rcptsheetissue_ind_prtcarno ) 
					.append("#").append("���������࿩��=").append( keepissue_ind ) 
					.append("#").append("���� �� �ܰ�=").append(basePrice )
					.append("#").append("���� �� �ܰ�=").append(discountBasePrice ) 
					.append("#").append("��ǥ�ѵ� 1ȸ ����=").append(amount1 ) 
					.append("#").append("�ѵ��ʰ��Ǹ�ó������=").append(proc_ind_overlimit )
					.append("#").append("PL����ܰ����� Ȯ��=").append(plunit_yn)
					.append("#").append("���鼼 ����=").append(taxfree_cd)
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
