package com.gsc.kixxhub.device.pumpa.controller;

import gnu.io.CommPortIdentifier;
import gnu.io.CommPortOwnershipListener;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener; // <- javax.comm.* (CentOS-7 64Bit 용)
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.SerialConnectException;
import com.gsc.kixxhub.device.pumpa.common.SerialParam;

public class PortManager implements SerialPortEventListener, CommPortOwnershipListener, 
	Runnable {

    /**
     * 
     */
    private InputStream	       	is;
    /**
     * 
     */
    private boolean				open;
    /**
     * 
     */
    private OutputStream       	os;
    /**
     * 
     */
    private CommPortIdentifier 	portId;
    /**
     * 
     */
    private Thread				readThread;
    /**
     * 
     */
    private SerialParam			sParam;
    /**
     * 
     */
    private SerialPort	       	sPort;

	/**
	 * @return
	 * @throws SerialConnectException
	 */
	public boolean closeCommPort() throws Exception {

		//--- If port is alread closed just return.
		if (!open) {
		    return  false;
		}

		//--- Remove the key listener.
		//messageAreaOut.removeKeyListener(keyHandler);

		//--- Check to make sure sPort has reference to avoid a NPE.
		if (sPort != null) {
		    try {
		    	//--- close the i/o streams.
		    	os.close();
		    	is.close();
		    } catch (IOException e) {
		        throw new SerialConnectException("Closing I/O streams error");
		    }

		    //--- Close the port.
		    sPort.close();

		    //--- Remove the ownership listener.
		    portId.removePortOwnershipListener(this);
		}

		open = false;
	
		return true;
	}

	/**
	 * @return
	 * @throws SerialConnectException
	 */
	public SerialParam getCommParams() throws Exception {
		
		sParam.baudRate 		= sPort.getBaudRate();
		sParam.flowControlIn 	= sPort.getFlowControlMode();
		sParam.flowControlOut 	= sPort.getFlowControlMode();
		sParam.dataBits 		= sPort.getDataBits();
		sParam.stopBits 		= sPort.getStopBits();
		sParam.parity 			= sPort.getParity();

		return sParam;
	}
	
	/**
	 * @return
	 * @throws SerialConnectException
	 */
	public SerialPort getCommPort() throws Exception {
		return sPort;
	}

	/**
	 * @return
	 */
	public InputStream getInputStream () {
		return this.is;
	}
	
	/**
	 * @return
	 */
	public OutputStream getOutputStream () {
		return this.os;
	}
	
	/**
	 * @param obj
	 * @return
	 */
	public boolean initCommPort (Object obj) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * @param portName
	 * @return
	 * @throws SerialConnectException
	 */
	public boolean openCommPort(String portName) throws Exception {

		//--- Obtain a CommPortIdentifier object for the port you want to open.
	    try {
	        portId = CommPortIdentifier.getPortIdentifier(portName);
	    	LogUtility.getCATLogger().debug("Get port ID =[" + portId + "]");
		    } catch (NoSuchPortException e) {
	    	LogUtility.getPumpALogger().debug ("Get port ID error : " + e.getMessage());
	        throw new SerialConnectException (e.getMessage());
	    }

	    //--- Open the port represented by the CommPortIdentifier object. 
	    //--- Give the open call a relatively long timeout of 3 seconds
	    try {
	    	LogUtility.getCATLogger().debug ("open Before");
	        sPort = (SerialPort) portId.open("KixxHub Pumpdaptor", 3000);
	    	LogUtility.getCATLogger().debug ("open After");
	    	LogUtility.getCATLogger().debug ("setCommParams Before");
	        setCommParams (); //default params
	        LogUtility.getCATLogger().debug ("setCommParams After");

	    } catch (PortInUseException e) {
	    	LogUtility.getPumpALogger().debug ("\nOpen comm port fail!!!!! : " + e.getMessage());
	        throw new SerialConnectException(e.getMessage());
	    }

	    //--- Open the input and output streams for the connection. If they won't
	    //--- open, close the port before throwing an exception.
	    try {
	        os = sPort.getOutputStream();
	        is = sPort.getInputStream();
	    } catch (IOException e) {
	        sPort.close();
	        throw new SerialConnectException ("Opening I/O streams error");
	    }
/*
	    //-- Add this object as an event listener for the serial port.
	    try {
	        sPort.addEventListener(this);
	    } catch (TooManyListenersException e) {
	        sPort.close();
	        throw new SerialConnectException("Too many listeners added");
	    }

	    //-- Set notifyOnDataAvailable to true to allow event driven input.
	    sPort.notifyOnDataAvailable(true);
*/

	    //-- Set notifyOnBreakInterrup to allow event driven break handling.
	    //sPort.notifyOnBreakInterrupt(true);

	    //--- Set receive timeout to allow breaking out of polling loop during
	    // input handling.
	    try {
	        sPort.enableReceiveTimeout(50);
	    } catch (UnsupportedCommOperationException e) {
	        throw new SerialConnectException("Enable receive timeout");
	    }

	    //--- Add ownership listener to allow ownership event handling.
	    //portId.addPortOwnershipListener(this);

	    open = true;
	    
		//readThread = new Thread(this);
		//readThread.start();
	    
		return true;
	}

	/* (non-Javadoc)
	 * @see javax.comm.CommPortOwnershipListener#ownershipChange(int)
	 */
	public void ownershipChange(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @param buffer
	 * @return
	 * @throws SerialConnectException
	 */
	public boolean purgeCommPort(byte[] buffer) throws Exception {

		for (int i=0; i<buffer.length; i++)
			buffer[i] = 0;
		
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// TODO 자동 생성된 메소드 스텁
		
	}

	// To be invoked when InputStream(is) has a receiving data
	/* (non-Javadoc)
	 * @see javax.comm.SerialPortEventListener#serialEvent(javax.comm.SerialPortEvent)
	 */
	public void serialEvent(SerialPortEvent event) {

		int	numBytes;
		
		switch (event.getEventType()) {
	
			case SerialPortEvent.BI:
			case SerialPortEvent.OE:
			case SerialPortEvent.FE:
			case SerialPortEvent.PE:
			case SerialPortEvent.CD:
			case SerialPortEvent.CTS:
			case SerialPortEvent.DSR:
			case SerialPortEvent.RI:
			case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
				break;
			case SerialPortEvent.DATA_AVAILABLE:
				byte[] readBuffer = new byte[200];

		    try {
		    	while (is.available() > 0)
		    		numBytes = is.read(readBuffer);

		    	LogUtility.getPumpALogger().debug("Received Text by event : " + new String(readBuffer) + "\n");
		    } catch (IOException e) {}
	
		    break;
		}
	}
	
	/**
	 * @return
	 * @throws SerialConnectException
	 */
	public boolean setCommParams() throws Exception { // default param
		
		try {
		    sPort.setSerialPortParams(9600,
		    						SerialPort.DATABITS_8, 
		    						SerialPort.STOPBITS_1, 
		    						SerialPort.PARITY_NONE);
		} catch (UnsupportedCommOperationException e) {
		    throw new SerialConnectException ("Unsupported parameter");			
		} 
		
		try {
		    sPort.setSerialPortParams(9600,
		    						SerialPort.DATABITS_8, 
		    						SerialPort.STOPBITS_1, 
		    						SerialPort.PARITY_NONE);
		} catch (UnsupportedCommOperationException e) {
		    throw new SerialConnectException ("Unsupported parameter");
		} 

		return true;
	}
	
	/**
	 * @param sParam
	 * @return
	 * @throws SerialConnectException
	 */
	public boolean setCommParams(SerialParam sParam) throws Exception {
		
		try {
			sPort.setSerialPortParams (sParam.baudRate,
									   sParam.dataBits,
									   sParam.stopBits,
									   sParam.parity);
		} catch (UnsupportedCommOperationException e) {
		    throw new SerialConnectException ("Unsupported parameter");
		} 
		
		try {
			sPort.setSerialPortParams (sParam.baudRate,
									   sParam.dataBits,
									   sParam.stopBits,
									   sParam.parity);
		} catch (UnsupportedCommOperationException e) {
		    throw new SerialConnectException ("Unsupported parameter");
		} 

		return true;
	}
	
	/**
	 * @param sPort
	 * @throws SerialConnectException
	 */
	public void setCommPort(SerialPort sPort) throws Exception {
		this.sPort = sPort;
	}
	
	/**
	 * @param is
	 */
	public void setInputStream (InputStream is) {
		this.is = is;
	}

	/**
	 * @param os
	 */
	public void setOutputStream (OutputStream os) {
		this.os = os;
	}
}
