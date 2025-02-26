package com.gsc.kixxhub.device.pumpa.service.listener;

import com.gsc.kixxhub.common.data.pump.WorkingMessage;


public interface AdaptorListener {
	
	/**
	 * @param obj
	 * @return
	 */
	public boolean sendDeviceMsg(WorkingMessage obj);	
	
	//public void notifyAsynMessage(Object obj) ;
	
}
