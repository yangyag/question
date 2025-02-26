package com.gsc.kixxhub.device.pumpa.define;

import java.io.InputStream;
import java.io.OutputStream;

import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.device.pumpa.common.SerialConnectException;
import com.gsc.kixxhub.device.pumpa.common.SerialParam;

public interface ICommDriver {
	
    /**
	 * @return
	 * @throws SerialConnectException
	 */
	public boolean closeCommPort () throws SerialConnectException;
    
    /**
	 * @param buf
	 * @return
	 * @throws SerialConnectException
	 */
	public boolean flushBuffer (byte[] buf) throws SerialConnectException;
    
	/**
	 * @return
	 * @throws SerialConnectException
	 */
	/**
	 * @return
	 * @throws SerialConnectException
	 */
	public SerialParam getCommParams () throws SerialConnectException;

	/**
	 * @return
	 */
	public InputStream getInputStream ();
	
	/**
     * @return
     */
    public int getNozzleNumber ();
	
	/**
	 * @return
	 */
	public OutputStream getOutputStream ();
	
	/**
	 * @return
	 * @throws SerialConnectException
	 */
	public boolean initCommPort () throws SerialConnectException;
	
	/**
	 * @param str
	 * @return
	 * @throws SerialConnectException
	 */
	public boolean openCommPort (String str) throws SerialConnectException;
	
	/**
	 * @param buf
	 * @return
	 * @throws SerialConnectException
	 */
	public int recvText (byte[] buf) throws SerialConnectException;

	/**
	 * @param wm
	 * @return
	 * @throws SerialConnectException
	 */
	public WorkingMessage requestData (WorkingMessage wm) throws SerialConnectException;
	
	/**
	 * @param buf
	 * @return
	 * @throws SerialConnectException
	 */
	public boolean sendText (byte buf) throws SerialConnectException;

	/**
	 * @param buf
	 * @return
	 * @throws SerialConnectException
	 */
	public boolean sendText (byte[] buf) throws SerialConnectException;
	
	/**
	 * @param obj
	 * @return
	 * @throws SerialConnectException
	 */
	public boolean setCommParams (SerialParam obj) throws SerialConnectException;
	
	/**
	 * @param is
	 */
	public void setInputStream (InputStream is);
	
	/**
     * @param nozNo
     */
    public void setNozzleNumber (int nozNo);
		
	/**
	 * @param os
	 */
	public void setOutputStream (OutputStream os);

}
