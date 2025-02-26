//-----------------------------------------------------------------//
//----- 소모1-Full EOT 송수신 기종(Protocol=WooJoo, ROMVer=001) ---//
//----- 적용 주유기 ROMVer :          -----------------------------//
//-----------------------------------------------------------------//
package com.gsc.kixxhub.device.pumpa.driverVersion;

import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.Command;
import com.gsc.kixxhub.device.pumpa.common.SerialConnectException;
import com.gsc.kixxhub.device.pumpa.driver.CommWooJoo;

public class CommWooJoo_001 extends CommWooJoo {
	
	/**
	 * @param nozNum
	 * @param romVerStr
	 */
	public CommWooJoo_001(int nozNum, String romVerStr) {
		super(nozNum, romVerStr);
	}

	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.pump.driver.CommWooJoo#recvTail_proc()
	 */
	@Override
	protected boolean recvTail_proc (byte ACK_NAK) throws Exception, SerialConnectException {
		
		//--- send : SA + ACK/NAK ---//
		rtn = sendText (ACK_NAK);
		if(rtn==false) {
			lineErrCnt++;
			return false;
		}

		if (dispLevel>=3) 
			LogUtility.getPumpALogger().debug("1.Send ACK (Noz="+nozNo+")");
				
		//--- recv : SA + EOT ---//
		if (recvCmd(RxBuf) < 1) {
			lineErrCnt++;
			return false;
		}
	
		if (RxBuf[1] == EOT) {	//--- recv : SA + EOT ---//
			if (dispLevel>=3) 
				LogUtility.getPumpALogger().debug("1.Recv EOT1 (Noz="+nozNo+")");
		}
		
		//--- send : SA + EOT ---//
		rtn = sendText (EOT);
		if(rtn==false) {
			lineErrCnt++;
			return false;
		}

		if (dispLevel>=3) 
			LogUtility.getPumpALogger().debug("1.Send EOT (Noz="+nozNo+")");
		
		//--- recv : SA + EOT ---//
		if (recvCmd(RxBuf) < 1) {
			lineErrCnt++;
			return false;
		}
	
		if (RxBuf[1] == EOT) {	//--- recv : SA + EOT ---//
			if (dispLevel>=3) 
				LogUtility.getPumpALogger().debug("1.Recv EOT2 (Noz="+nozNo+")");
		}

		return true;
	}
	
	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.pump.driver.CommWooJoo#sendTail_proc()
	 */
	@Override
	protected boolean sendTail_proc () throws Exception, SerialConnectException {
		boolean rtn;
		
		//--- send : SA + EOT ---//
		rtn = sendText (Command.EOT);
		if(rtn==false) {
			lineErrCnt++;
			return false;
		}

		if (dispLevel>=3) 
			LogUtility.getPumpALogger().debug("2.Send EOT (Noz="+nozNo+")");

		if (recvCmd(RxBuf) < 1) {
			if (dispLevel>=3) 
				LogUtility.getPumpALogger().debug("\n2.Recv EOT fail (Noz="+nozNo+")");
			lineErrCnt++;
			return false;
		}
	
		//--- recv : SA + EOT ---//
		if (RxBuf[1] == Command.EOT) {	
			if (dispLevel>=3) 
				LogUtility.getPumpALogger().debug("2.Recv EOT (Noz="+nozNo+")");
		}
		
		return true;
	}
	
}
