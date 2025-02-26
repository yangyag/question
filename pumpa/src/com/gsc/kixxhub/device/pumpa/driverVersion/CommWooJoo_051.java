//-----------------------------------------------------//
//----- 한국ENE-경우(Protocol=WooJoo, ROMVer=051) -----//
//----- 적용 주유기 ROMVer : 5.x, 6.x -----------------//
//-----------------------------------------------------//
package com.gsc.kixxhub.device.pumpa.driverVersion;

import com.gsc.kixxhub.device.pumpa.driver.CommWooJoo;

public class CommWooJoo_051 extends CommWooJoo {

	/**
	 * @param nozNum
	 * @param romVerStr
	 */
	public CommWooJoo_051(int nozNum, String romVerStr) {
		
		super(nozNum, romVerStr);
		
		baseReadStartInterval  =5+2; // 스마트주유소 PJT 보정
		baseWriteStartInterval =5+2; // 스마트주유소 PJT 보정
		baseReadBuffInterval   =15+5; // 스마트주유소 PJT 보정 
		baseMinErrCnt		   =10;

		readCmdInterval    =15; // 고정
	}


	/****
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
			if (dispLevel>=1) 
				LogUtility.getPumpALogger().debug("2.Recv EOT fail (Noz="+nozNo+")");
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
	
	protected boolean recvTail_proc () throws Exception, SerialConnectException {
		
		//--- send : SA + ACK ---//
		rtn = sendText (ACK);
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
		
//		// 08.05.16 삭제 - 미요청시 응답않음	
//		//--- send : SA + EOT ---//
//		rtn = sendText (EOT);
//		if(rtn==false) {
//			lineErrCnt++;
//			return false;
//		}
//
//		if (dispLevel>=3) 
//			LogUtility.getPumpALogger().debug("1.Send EOT (Noz="+nozNo+")");
//		
//		//--- recv : SA + EOT ---//
//		if (recvEOT(RxBuf) < 1) {
//			lineErrCnt++;
//			return false;
//		}
//	
//		if (RxBuf[1] == EOT) {	//--- recv : SA + EOT ---//
//			if (dispLevel>=3) 
//				LogUtility.getPumpALogger().debug("1.Recv EOT2 (Noz="+nozNo+")");
//		}

		return true;
	}
	****/
}
