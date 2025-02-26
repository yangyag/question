//-----------------------------------------------------//
//----- 한국ENE-마이컴(Protocol=WooJoo, ROMVer=061) ---//
//----- 적용 주유기 ROMVer : 3.0, 4.0 -----------------//
//-----------------------------------------------------//
package com.gsc.kixxhub.device.pumpa.driverVersion;

import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.SerialConnectException;
import com.gsc.kixxhub.device.pumpa.driver.CommWooJoo;

public class CommWooJoo_061 extends CommWooJoo {
	
	/**
	 * @param nozNum
	 * @param romVerStr
	 */
	public CommWooJoo_061(int nozNum, String romVerStr) {
		
		super(nozNum, romVerStr);
		
		baseReadStartInterval  =5+3; // 스마트주유소 PJT 보정
		baseWriteStartInterval =5+3; // 스마트주유소 PJT 보정
		baseReadBuffInterval   =10+8; // 스마트주유소 PJT 보정
		baseMinErrCnt		   =13;

		readCmdInterval    =10; // 고정
	}
	
	/* //--Need test
	// this module is writing by 1 byte (For ENE)
	public boolean sendText(byte[] buf) throws Exception, SerialConnectException {

		byte[]	TxByt = new byte[1];
		Sleep.sleep(writeStartInterval);
		
	    try {
	    	for (int i=0; i<buf.length; i++) {
	    		TxByt[0] = buf[i];
	    		os.write(TxByt, 0, 1);
	    		Sleep.sleep(3); // 최적값=3
	    	}
		} catch (IOException e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
			return false;
		}
		
		return true;
	}*/

	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.pump.driver.CommWooJoo#recvTail_proc()
	 */
	@Override
	protected boolean recvTail_proc (byte ACK_NAK) throws Exception, SerialConnectException {
		
		//--- send : SA + ACK/NAK ---//
		rtn = sendText (ACK_NAK);
		if(rtn==false) {
			//lineErrCnt++;
			return false;
		}

		if (dispLevel>=3) 
			LogUtility.getPumpALogger().info("1.Send ACK (Noz="+nozNo+")");
			
		//--- recv : SA + EOT ---//
		if (recvCmd(RxBuf) < 1) {
			//lineErrCnt++;
			return false;
		}
	
		if (RxBuf[1] == EOT) {	//--- recv : SA + EOT ---//
			if (dispLevel>=3) 
				LogUtility.getPumpALogger().info("1.Recv EOT1 (Noz="+nozNo+")");
		}
		
		//--- send : SA + EOT ---//
		rtn = sendText (EOT);
		if(rtn==false) {
			//lineErrCnt++;
			return false;
		}

		if (dispLevel>=3) 
			LogUtility.getPumpALogger().info("1.Send EOT (Noz="+nozNo+")");
		/*
		//--- recv : SA + EOT ---//
		if (recvEOT(RxBuf) < 1) {
			lineErrCnt++;
			return false;
		}
	
		if (RxBuf[1] == EOT) {	//--- recv : SA + EOT ---//
			if (dispLevel>=2) 
				LogUtility.getPumpALogger().info("1.Recv EOT2 (Noz="+nozNo+")");
		}*/

		return true;
	}
	
	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.pump.driver.CommWooJoo#sendTail_proc()
	 */
	@Override
	protected boolean sendTail_proc () throws Exception, SerialConnectException {
		boolean rtn;
		
		//--- send : SA + EOT ---//
		rtn = sendText (EOT);
		if(rtn==false) {
			//lineErrCnt++;
			return false;
		}

		if (dispLevel>=3) 
			LogUtility.getPumpALogger().info("2.Send EOT (Noz="+nozNo+")");

		if (recvCmd(RxBuf) < 1) {
			if (dispLevel>=3) 
				LogUtility.getPumpALogger().info("2.Recv EOT fail (Noz="+nozNo+")");
			//lineErrCnt++;
			return false;
		}
	
		//--- recv : SA + EOT ---//
		if (RxBuf[1] == EOT) {	
			if (dispLevel>=3) 
				LogUtility.getPumpALogger().info("2.Recv EOT (Noz="+nozNo+")");
		}
		
		return true;
	}
	
}
