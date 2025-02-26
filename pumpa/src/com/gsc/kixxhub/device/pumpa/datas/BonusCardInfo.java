/**
 * 
 */
package com.gsc.kixxhub.device.pumpa.datas;

/**
 * @author GSC
 * 
 */
public class BonusCardInfo {

	String cardName = "";
	String ID = "";
	String typeCode = "";

	/**
	 * @return the cardType
	 */
	public String getCardName() {
		return cardName;
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
	 * @param cardType
	 *            the cardType to set
	 */
	public void setCardName(String cardType) {
		this.cardName = cardType;
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
