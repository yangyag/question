package com.gsc.kixxhub.device.pumpa.common;

import gnu.io.SerialPort;

public class SerialParam {

    /**
     * 
     */
    public int    baudRate;
    /**
     * 
     */
    public int    dataBits;
    /**
     * 
     */
    public int    flowControlIn;
    /**
     * 
     */
    public int    flowControlOut;
    /**
     * 
     */
    public int    parity;
    /**
     * 
     */
    public String portName="";
    /**
     * 
     */
    public int    stopBits;

    //Default constructer. Sets parameters to no port, 9600 baud, no flow
    //control, 8 data bits, 1 stop bit, no parity.
    /**
     * 
     */
    public SerialParam () {
    	this("", 9600, SerialPort.FLOWCONTROL_NONE, 
    				   SerialPort.FLOWCONTROL_NONE, 
    				   SerialPort.DATABITS_8,
    				   SerialPort.STOPBITS_1,
    				   SerialPort.PARITY_NONE);
    }

    //Paramaterized constructer.
    /**
     * @param portName
     * @param baudRate
     * @param flowControlIn
     * @param flowControlOut
     * @param databits
     * @param stopbits
     * @param parity
     */
    public SerialParam (String portName, int baudRate, int flowControlIn, 
			    int flowControlOut, int databits, int stopbits, 
			    int parity) {
    	
    	this.portName = portName;
    	this.baudRate = baudRate;
    	this.flowControlIn = flowControlIn;
    	this.flowControlOut = flowControlOut;
    	this.dataBits = databits;
    	this.stopBits = stopbits;
    	this.parity = parity;
    }
    
    /**
     * @param baudRate
     * @param databits
     * @param stopbits
     * @param parity
     */
    public void setParam (int baudRate, int databits, int stopbits, int parity) {
		
		this.baudRate = baudRate;
		this.dataBits = databits;
		this.stopBits = stopbits;
		this.parity = parity;
    }
    
    /**
     * @param portName
     * @param baudRate
     * @param flowControlIn
     * @param flowControlOut
     * @param databits
     * @param stopbits
     * @param parity
     */
    public void setParam (String portName, int baudRate, int flowControlIn, 
		    	int flowControlOut, int databits, int stopbits, 
		    	int parity) {
    	
    	this.portName = portName;
    	this.baudRate = baudRate;
    	this.flowControlIn = flowControlIn;
    	this.flowControlOut = flowControlOut;
    	this.dataBits = databits;
    	this.stopBits = stopbits;
    	this.parity = parity;
    }
}
