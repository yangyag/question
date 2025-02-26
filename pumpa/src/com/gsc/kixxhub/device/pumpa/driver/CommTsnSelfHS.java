// - 추가 2012.08, dhp
package com.gsc.kixxhub.device.pumpa.driver;

import gnu.io.CommPortOwnershipListener;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;

import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.SerialConnectException;
import com.gsc.kixxhub.device.pumpa.common.Sleep;
import com.gsc.kixxhub.device.pumpa.define.CCommDriver;

public class CommTsnSelfHS extends CCommDriver implements SerialPortEventListener, CommPortOwnershipListener, 
	Runnable {

	protected byte ACK = 0x06;
	protected int	baseMinErrCnt=5, baseMaxSkipCnt=200;
	
	
	protected int	baseReadBuffInterval   = 50+5; // 스마트주유소 PJT 보정
	protected int	baseReadStartInterval  = 15+5; // 스마트주유소 PJT 보정
	protected int	baseWriteStartInterval = 15+5; // 스마트주유소 PJT 보정
	protected byte DC1 = 0x11;

	protected byte DC2 = 0x12;
	
	protected byte DC3 = 0x13;
	protected byte DC4 = 0x14;
    
	protected byte ENQ = 0x05;
	protected byte[] 	enq_Buf = new byte[3];
	protected byte EOB = 0x17;
	protected byte EOT = 0x04;
	protected byte ETX = 0x03;
	protected byte FS  = 0x1c;
	protected byte NAK = 0x15;
	
	protected byte[] 	nozID = new byte[2];
	protected String 	nozStr = "";
	protected int	readCmdInterval = baseReadStartInterval; // readBuffInterval when read command(ACK/EOT etc.)
	protected byte[] 	RxBuf;
	protected byte SOH = 0x01;
	protected byte STX = 0x02;
    	
    protected byte[] 	TxBuf;
        
    public CommTsnSelfHS (int nozNum, String romVerStr) throws Exception {
    	    	
		readBuffInterval   = baseReadBuffInterval;
		readStartInterval  = baseReadStartInterval;
		writeStartInterval = baseWriteStartInterval;
		minErrCnt 		   = baseMinErrCnt;
		maxSkipCnt 		   = baseMaxSkipCnt;
		
    }
 

	protected boolean compareBCC(byte[] buf) throws Exception {
		int	i;
	
		for (i=0; i<buf.length-2; i++) {
			if (buf[i]==ETX) 
				break;
		}
		
		if (getBCC (buf) == buf[i+1])		
			return true;
		else
			return false;
	}

	protected boolean compareNozID(byte[] buf) throws Exception {
		
		if (buf[1]==nozID[0] && buf[2]==nozID[1])		
			return true;
		else 
			return false;
	}
	
	protected byte getBCC(byte[] dat) throws Exception {
		
		byte 	bcc=0;
		int 	stxIdx=0, etxIdx=0, i, j;
		
		for (i=0; i<dat.length-1; i++) {
			if (dat[i]==SOH) 
				break;
		}
		stxIdx = i + 1; // start from STX + 3
		
		for(j=i; j<dat.length-1; j++) {
			if (dat[j]==ETX)
				break;
		}
		etxIdx = j;
		
		if (stxIdx<=0 || etxIdx<=0) // No exist STX, ETX
			return -1;
		
		//--- Calculate BCC
		for (i=stxIdx; i<=etxIdx; i++) {
			bcc = (byte) (bcc ^ dat[i]); // XOR
		}
		
		return bcc;
	}

	protected int getBufferLength(byte[] buf) throws Exception {
		int bufLen=0;
		for (bufLen=0; bufLen<buf.length; bufLen++)
			if (buf[bufLen]==ETX) break;
		bufLen+=2;
		
		return bufLen;
	}
	
	public void ownershipChange(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public int recvCmd(byte[] RxBuf) throws Exception, SerialConnectException {

		int		c=0, i=0, loop=0;
		byte[]	RxByt = new byte[1];
		int		recvLoopCnt=readCmdInterval/10;
		int		len=2;
		
		flushBuffer (RxBuf);
		Sleep.sleep(readStartInterval);

		try {
			
			for (c=0; c<RxBuf.length; c++) {
				
				RxByt[0] = 0x00;
				
				if (is.read(RxByt, 0, 1) < 1) { // no recv data
					//---check timeout
					if (loop >= recvLoopCnt) break;
					else loop++;
					
					c--;
					Sleep.sleep(2);
					continue;
				} else
					loop=0;

				RxBuf[i] = RxByt[0];
				
				i++;			
				if (i >= len)
					break;
			}

			return i;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogUtility.getPumpALogger().error(e.getMessage(), e);
			return 0;
		}
	}
	
	protected boolean recvTail_proc (byte ACK_NAK) throws Exception, SerialConnectException {

		sendText(ACK_NAK);
		
		return true;
	}
	
	public int recvText(byte[] RxBuf) throws Exception, SerialConnectException {

		int		numBytes=0, c=0, i=0, loop=0;
		boolean	STXFlag=false;
		byte[]	RxByt = new byte[1];
		int		recvLoopCnt=readBuffInterval/10;
		
		flushBuffer (RxBuf);
		Sleep.sleep(readStartInterval);

		try {
			
			for (c=0; c<RxBuf.length-1; c++) {
			
				RxByt[0] = 0x00;

				if (is.read(RxByt, 0, 1) < 1) { // no recv data
					//---check timeout
					if (loop >= recvLoopCnt) break;
					else loop++;

					c--;
					Sleep.sleep(2); // wait
					continue;
				} else
					loop=0;

				RxBuf[i] = RxByt[0];
				
				if (RxBuf[i]==STX)
					STXFlag=true;

				if (STXFlag==false)
					if (RxBuf[i]==EOT || RxBuf[i]==ACK || RxBuf[i]==NAK) { // recv EOT/ACK/NAK
						numBytes=i+1;
						break;
					}
				
				if (RxBuf[i]==ETX) { // recv ETX next STX
					if (STXFlag==true) {
						RxByt[0] = 0x00;
						is.read(RxByt, 0, 1); // read BCC
						RxBuf[i+1] = RxByt[0];
						numBytes=i+2;
						break;
					}
					else { // recv ETX none STX
						flushBuffer(RxBuf);
						numBytes=0;
						break;
					}
				}
								
				i++;
			}
			return numBytes;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogUtility.getPumpALogger().error(e.getMessage(), e);
			return 0;
		}
	}
	
	public void run() {

	}	
	
	protected boolean sendTail_proc () throws Exception, SerialConnectException {
				
		return true;
	}
	

	public boolean sendText(byte cmd) throws Exception, SerialConnectException {

		byte[]	sndBuf = new byte[1];
		boolean	rtn;

		Sleep.sleep(writeStartInterval);
		
		//--- send : SA + ENQ ---//
		sndBuf[0] = cmd;
		rtn = sendText(sndBuf);
		if(rtn==false) {
			LogUtility.getPumpALogger().info("\n0.Send Cmd fail! (Noz=" + nozNo + ") Cmd=" + cmd);
			return false;
		}
		//System.out.printf ("Send Text : 0x%02X 0x%02X\n", sndBuf[0], sndBuf[1]);
	
		return true;
	}
	
	public boolean sendText(byte[] buf) throws Exception, SerialConnectException {

		Sleep.sleep(writeStartInterval);
		
	    try {
			os.write(buf);
		} catch (IOException e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
		
		return true;
	}

			
	public boolean sendText(String txt) throws Exception, SerialConnectException {

		Sleep.sleep(writeStartInterval);
		
	    try {
			os.write(txt.getBytes());
		} catch (IOException e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	
		return true;
	}

	// To be invoked when InputStream(is) has a receiving data
	public void serialEvent(SerialPortEvent event) {

	}

	protected void setBCC(byte[] buf) throws Exception {
		int	i;
		
		for (i=0; i<buf.length-2; i++) {
			if (buf[i]==ETX) 
				break;
		}
		
		buf[i+1] = getBCC(buf);
	}
}
