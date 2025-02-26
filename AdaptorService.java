package com.gsc.kixxhub.device.pumpa.service;

import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.device.pumpa.service.listener.AdaptorListener;

public interface AdaptorService { 
	
	/**
	 * @param wm
	 */
	public void init(WorkingMessage wm);
	
	/**
	 * @param listener
	 */
	public void removeListener(AdaptorListener listener);
	
	/**
	 * @param wm
	 * @return
	 */
	public boolean sendModuleMsg(WorkingMessage wm);
	
	/**
	 * @param listener
	 */
	public void setListener(AdaptorListener listener);
	
	//public void sendAsynMessage(Object obj);
	
}
