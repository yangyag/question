/**
 * 
 */
package com.gsc.kixxhub.device.pumpa.datas;

/**
 * @author GSC
 * 
 */
public class MotionPictureInfo {

	Object data = null;
	int fileSize = 0;
	String ID = "";
	String pictureName = "";
	String typeCode="";

	/**
	 * @return the data
	 */
	public Object getData() {
		return data;
	}

	/**
	 * @return the fileSize
	 */
	public int getFileSize() {
		return fileSize;
	}

	/**
	 * @return the iD
	 */
	public String getID() {
		return ID;
	}

	/**
	 * @return the pictureName
	 */
	public String getPictureName() {
		return pictureName;
	}

	/**
	 * @return the typeCode
	 */
	public String getTypeCode() {
		return typeCode;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(Object data) {
		this.data = data;
	}

	/**
	 * @param fileSize
	 *            the fileSize to set
	 */
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	/**
	 * @param id
	 *            the iD to set
	 */
	public void setID(String id) {
		ID = id;
	}

	/**
	 * @param pictureName
	 *            the pictureName to set
	 */
	public void setPictureName(String pictureName) {
		this.pictureName = pictureName;
	}

	/**
	 * @param typeCode the typeCode to set
	 */
	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}
}
