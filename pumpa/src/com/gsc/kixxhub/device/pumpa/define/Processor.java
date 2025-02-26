package com.gsc.kixxhub.device.pumpa.define;

import com.gsc.kixxhub.common.data.pump.WorkingMessage;

public interface Processor {
	/**
	 * ���� Process�� ó�� �ؾ��ϴ� WorkingMessage ���θ� ��´�.
	 * 
	 * @param workingMessage : WorkingMessage
	 * @return
	 */
	public boolean canContinue(WorkingMessage workingMessage) throws Exception ;
	

	
	/**
	 * Process�� ID�� ��´�.
	 * 
	 * @return : String Ÿ���� Process�� ID
	 */
	public String getProcessID() throws Exception ;
	
	
	
	/**
	 * sendModuleMsg(), sendDeviceMsg() �� 
	 * WorkingMessage�� �´� �޼ҵ带 ȣ���Ѵ�.
	 * 
	 * @param workingMessage : WorkingMessage
	 */
	public void startProcess (WorkingMessage workingMessage) throws Exception ;

}