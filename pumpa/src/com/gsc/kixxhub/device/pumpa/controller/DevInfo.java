package com.gsc.kixxhub.device.pumpa.controller;

import com.gsc.kixxhub.common.utility.log.LogUtility;

public class DevInfo {
	/**
	 * 
	 */
	public String baudRate;
	/**
	 * 
	 */
	public String connectDevNo;
	/**
	 * 
	 */
	public String devNo;
	/**
	 * 
	 */
	public String devType;
	/**
	 * KIXXHUB IP
	 */
	public String ipAddress = "";	// PI2-박동화, 2015-11-24 추가
	/**
	 * 
	 */
	public String protocol;
	/**
	 * 
	 */
	public String ROMVersion;
	
	/**
	 * 
	 */
	public void print() throws Exception {
			LogUtility.getPumpALogger().debug("[DevInfo] Information");
			LogUtility.getPumpALogger().debug(" devNo = " + devNo); 
			LogUtility.getPumpALogger().debug(" devType = " + devType); 
			LogUtility.getPumpALogger().debug(" protocol = " + protocol); 
			LogUtility.getPumpALogger().debug(" baudRate = " + baudRate); 
			LogUtility.getPumpALogger().debug(" ROMVersion = " + ROMVersion); 
			LogUtility.getPumpALogger().debug(" ipAddress = " + ipAddress); 
			LogUtility.getPumpALogger().debug(" connectDevNo = " + connectDevNo); 
	}
}
