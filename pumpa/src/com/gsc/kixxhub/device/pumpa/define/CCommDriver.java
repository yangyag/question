package com.gsc.kixxhub.device.pumpa.define;

import gnu.io.CommPortIdentifier; // <- javax.comm.* (CentOS-7 64Bit 용)
import gnu.io.SerialPort;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Formatter;

import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.BytesQue2;
import com.gsc.kixxhub.device.pumpa.common.ObjectQue;
import com.gsc.kixxhub.device.pumpa.common.SerialConnectException;
import com.gsc.kixxhub.device.pumpa.common.SerialParam;

public class CCommDriver {

    public 	int		connectDeviceNo=0;
    /**
	 * PI2-박동화, 2015-11-17 추가
	 */
	protected 	int		interface_method = 0; // 0: Serial, 1: TCP/IP

    /**
     * PI2-박동화, 2015-11-24 추가
     */
	
    public 	String ipAddress="";
	/**
	 * 
	 */
	public InputStream	       	is;
	protected int	m_nNoResponseCnt=0;
	/**
     * 
     */
    private int	maxQueCnt = 10;
	
	protected int	minErrCnt, maxSkipCnt;

    /**
	 * 
	 */
	public 	int		nozNo=0;
	/**
	 * 
	 */
	public 	int		nozType=1; // 1=일반, 2=Self, 3=SemiSelf, 4=충전기
	/**
     * 
     */
    private boolean	open;
	
	/**
	 * 
	 */
	public OutputStream       	os;
	/**
	 * 
	 */
	public CommPortIdentifier 	portId;
	/**
	 * 
	 */
	public 	int		protocol=0;
	/**
     * 
     */
    public ObjectQue 	rcvQue = new ObjectQue(maxQueCnt);
	
	protected int	readBuffInterval;
	protected int	readStartInterval;
	/**
	 * 
	 */
	public Thread				readThread;
	/**
     * 
     */
    public 	String romVer="";
	/**
     * 
     */
    public ObjectQue 	sndQue = new ObjectQue(maxQueCnt);
	/**
	 * 
	 */
	public SerialParam			sParam;
	
    /**
	 * 
	 */
	public SerialPort	       	sPort;
    /**
     * 
     */
    public BytesQue2 	TxQue  = new BytesQue2(maxQueCnt);
    protected int	writeStartInterval;
	  
    public String addTotalGauge (String szGauge, String szLiter) throws Exception {
		
		Float fGauge = new Float(szGauge.substring(0,7) + "." + szGauge.substring(7,10));
		Float fLiter = new Float(szLiter.substring(0,4) + "." + szLiter.substring(4,7));
		Float fGauge2 = fGauge + fLiter;
		
		Formatter form = new Formatter();
		form.format("%011.3f\n",fGauge2);
				
		return form.toString().substring(0,7)+form.toString().substring(8,11);
	}
    
    /**
     * @return
     */
    public WorkingMessage extractRecvQueue () throws Exception {
    	return (WorkingMessage) rcvQue.deQueue();
    }
    
    /**
     * @return
     */
    public WorkingMessage extractSendQueue () throws Exception {
    	return (WorkingMessage) sndQue.deQueue();
    }
    
    /**
	 * @param buffer
	 * @return
	 * @throws SerialConnectException
	 */
	public boolean flushBuffer(byte[] buffer) throws Exception, SerialConnectException {

		for (int i=0; i<buffer.length; i++)
			buffer[i] = 0;
		
		return true;
	}

    /**
	 * @return
	 */
	public int getConnectDeviceNo() throws Exception {
		return connectDeviceNo;
	}
    
    /**
	 * @return
	 */
	public InputStream getInputStream () throws Exception {
		return this.is;
	}

    public synchronized int getInterface_method() {
		return interface_method;
	}
    
    /**
     * @return
     */
    public int getNozzleNumber () throws Exception {
    	return this.nozNo;
    }

	
	/**
	 * @return
	 */
	public OutputStream getOutputStream () throws Exception {
		return this.os;
	}
	
	/**
     * @return
     */
    public int getRecvQueueCnt () throws Exception {
    	return rcvQue.getItemCount();
    }

	/**
     * @return
     */
    public int getSendQueueCnt () throws Exception {
    	return sndQue.getItemCount();
    }
	
	/**
	 * @return
	 * @throws SerialConnectException
	 */
	public SerialParam getSerialParam() throws Exception, SerialConnectException {

		return this.sParam;
	}

	/**
     * @param wm
     */
    public void insertRecvQueue (WorkingMessage wm) throws Exception {
    	if (wm != null) {
    		rcvQue.enQueue(wm);
			//LogUtility.getPumpALogger().debug("\n<<<<< Transmitted Up data : " +
				//"nozNo=" + nozNo + " command=" + wm.getCommand());
    	}
    }
	
	/**
     * @param wm
     */
    public void insertSendQueue (WorkingMessage wm) throws Exception {
    	if (wm != null) {
    		sndQue.enQueue(wm);
			//LogUtility.getPumpALogger().debug("\n>>>>> Received  Down data : " +
				//"nozNo=" + nozNo + " command=" + wm.getCommand());
    	}
    }
	
	public String pack (byte[] buf, byte targetBit) {
		
		int	i;
		String rst="";
		
		try {
			for (i=0; i<buf.length; i++) {
				if (buf[i]==targetBit) 
					break;
			}
			rst = new String(buf).substring(0, i);
			
		} catch(Exception e) {
			LogUtility.getPumpALogger().error("pack error!");
			return rst;
		}
		
		return rst;
	}
	
	
	/**
	 * @throws SerialConnectException
	 */
	public void requestData() throws SerialConnectException, Exception {
		
	}

	/**
	 * @param connectDeviceNo
	 */
	public void setConnectDeviceNo(int connectDeviceNo) throws Exception {
		this.connectDeviceNo = connectDeviceNo;
	}

	/**
	 * @param is
	 */
	public void setInputStream (InputStream is) throws Exception {
		this.is = is;
	}
	
	public synchronized void setInterface_method(int interface_method) {
		this.interface_method = interface_method;
	}
	
	/**
     * @param nozNo
     */
    public void setNozzleNumber (int nozNo) throws Exception {
    	this.nozNo = nozNo;
    }
	
	/**
	 * @param os
	 */
	public void setOutputStream (OutputStream os) throws Exception {
		this.os = os;
	}

	/**
	 * @param sParam
	 * @throws SerialConnectException
	 */
	public void setSerialParam(SerialParam sParam) throws Exception, SerialConnectException {

		this.sParam = sParam;
	}
}
