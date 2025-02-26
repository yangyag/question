/**
 * 
 */
package com.gsc.kixxhub.device.pumpa.datas;

/**
 * @author GSC
 * 
 */
public class CashReceiptCardInfo {

	String cashReceiptName = "";
	String ID = "";
	String typeCode = "";

	/**
	 * @return the cashReceiptName
	 */
	public String getCashReceiptName() {
		return cashReceiptName;
	}

	/**
	 * @return the iD
	 */
	public String getID() {
		return ID;
	}

	/**
	 * @return the typeCode
	 */
	public String getTypeCode() {
		return typeCode;
	}

	/**
	 * @param cashReceiptName the cashReceiptName to set
	 */
	public void setCashReceiptName(String cashReceiptName) {
		this.cashReceiptName = cashReceiptName;
	}

	/**
	 * @param id
	 *            the iD to set
	 */
	public void setID(String id) {
		ID = id;
	}

	/**
	 * @param typeCode the typeCode to set
	 */
	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

}
