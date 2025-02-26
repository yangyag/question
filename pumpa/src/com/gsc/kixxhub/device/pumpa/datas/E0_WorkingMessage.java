package com.gsc.kixxhub.device.pumpa.datas;

import java.util.Vector;

import com.gsc.kixxhub.common.data.pump.WorkingMessage;

public class E0_WorkingMessage extends WorkingMessage {

	private int dataCount = 0;
	private Vector<Object> datas = new Vector<Object>();
	private String group = "";

	/**
	 * 
	 */
	public E0_WorkingMessage() {
		this.setCommand("E0");
	}

	/**
	 * @param datas the datas to add
	 */
	public void addDatas(Object data) {
		this.datas.add(data);
	}

	/**
	 * @return the dataCount
	 */
	public int getDataCount() {
		return dataCount;
	}

	/**
	 * @return the datas
	 */
	public Vector<Object> getDatas() {
		return datas;
	}

	/**
	 * @return the group
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * @param dataCount
	 *            the dataCount to set
	 */
	public void setDataCount(int dataCount) {
		this.dataCount = dataCount;
	}

	/**
	 * @param datas the datas to set
	 */
	public void setDatas(Vector<Object> datas) {
		this.datas = datas;
	}

	/**
	 * @param group
	 *            the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}

}
