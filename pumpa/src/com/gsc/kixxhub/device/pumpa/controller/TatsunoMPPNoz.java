package com.gsc.kixxhub.device.pumpa.controller;

import java.util.Vector;

import com.gsc.kixxhub.common.utility.log.LogUtility;

public class TatsunoMPPNoz {
	
	/**
	 * 
	 */
	Vector<String> connectNozNoVec = new Vector<String>();
	/**
	 * 
	 */
	String ODTNo;
	
	/**
	 * @return
	 */
	public Vector<String> getConnectNozNoVec() throws Exception {
		return connectNozNoVec;
	}
	
	
	/**
	 * @return
	 */
	public String getODTNo() throws Exception {
		return ODTNo;
	}
	public void print(){
		LogUtility.getPumpALogger().debug(new StringBuffer("[TatsunoMPPNoz] Information")
				.append(" # ODTNo = ").append( ODTNo)
				.append(" # connectNozNoVec Size = ").append( connectNozNoVec.size()).toString()
				) ;
		
		if ((connectNozNoVec != null) && (connectNozNoVec.size() != 0)) {
			for (int i = 0 ; i < connectNozNoVec.size() ; i++) {
				LogUtility.getPumpALogger().debug(new StringBuffer("connectNozNoVec[").append( i + "] = ").append( connectNozNoVec.get(i)).toString());
				
			}	// end for
			
		}	// end inner if
		
	}	// end print
	/**
	 * @param connectNozNoVec
	 */
	public void setConnectNozNoVec(Vector<String> connectNozNoVec) throws Exception {
		this.connectNozNoVec = connectNozNoVec;
	}
	/**
	 * @param no
	 */
	public void setODTNo(String no) throws Exception {
		ODTNo = no;
	}
}
