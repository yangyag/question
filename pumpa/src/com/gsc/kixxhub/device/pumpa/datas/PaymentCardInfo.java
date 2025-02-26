/**
 * 
 */
package com.gsc.kixxhub.device.pumpa.datas;

/**
 * @author GSC
 * 
 */
public class PaymentCardInfo {

	String ID = "";
	String paymentName = "";
	String typeCode = "";

	/**
	 * @return the iD
	 */
	public String getID() {
		return ID;
	}

	/**
	 * @return the cardType
	 */
	public String getPaymentName() {
		return paymentName;
	}

	/**
	 * @return the typeCode
	 */
	public String getTypeCode() {
		return typeCode;
	}

	/**
	 * @param id
	 *            the iD to set
	 */
	public void setID(String id) {
		ID = id;
	}

	/**
	 * @param cardType
	 *            the cardType to set
	 */
	public void setPaymentName(String cardType) {
		this.paymentName = cardType;
	}

	/**
	 * @param typeCode the typeCode to set
	 */
	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

}
