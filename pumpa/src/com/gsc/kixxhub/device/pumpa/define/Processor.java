package com.gsc.kixxhub.device.pumpa.define;

import com.gsc.kixxhub.common.data.pump.WorkingMessage;

public interface Processor {
	/**
	 * 현재 Process가 처리 해야하는 WorkingMessage 여부를 얻는다.
	 * 
	 * @param workingMessage : WorkingMessage
	 * @return
	 */
	public boolean canContinue(WorkingMessage workingMessage) throws Exception ;
	

	
	/**
	 * Process의 ID를 얻는다.
	 * 
	 * @return : String 타입의 Process의 ID
	 */
	public String getProcessID() throws Exception ;
	
	
	
	/**
	 * sendModuleMsg(), sendDeviceMsg() 중 
	 * WorkingMessage에 맞는 메소드를 호출한다.
	 * 
	 * @param workingMessage : WorkingMessage
	 */
	public void startProcess (WorkingMessage workingMessage) throws Exception ;

}