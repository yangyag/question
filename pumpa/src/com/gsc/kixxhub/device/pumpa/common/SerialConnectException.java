package com.gsc.kixxhub.device.pumpa.common;

public class SerialConnectException extends Exception {

    /**
     * Constructs a <code>SerialConnectionException</code>
     * with no detail message.
    */
    public SerialConnectException() {
    	super();
    }

    /**
     * Constructs a <code>SerialConnectionException</code>
     * with the specified detail message.
     * 
     * @param   s   the detail message.
    */
    public SerialConnectException(String str) {
    	super(str);
    }

}
