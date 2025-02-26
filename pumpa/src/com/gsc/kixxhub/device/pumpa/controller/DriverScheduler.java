package com.gsc.kixxhub.device.pumpa.controller;

import gnu.io.SerialPort;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.Change;
import com.gsc.kixxhub.device.pumpa.common.ObjectQue;
import com.gsc.kixxhub.device.pumpa.common.Protocol;
import com.gsc.kixxhub.device.pumpa.common.SerialConnectException;
import com.gsc.kixxhub.device.pumpa.common.SerialParam;
import com.gsc.kixxhub.device.pumpa.common.Sleep;
import com.gsc.kixxhub.device.pumpa.define.CCommDriver;
import com.gsc.kixxhub.device.pumpa.driver.CommGSGasODTMr;
import com.gsc.kixxhub.device.pumpa.driver.CommGSSelfODT;
import com.gsc.kixxhub.device.pumpa.driver.CommGSSelfODTi;
import com.gsc.kixxhub.device.pumpa.driver.CommGSSelfVNoz;
import com.gsc.kixxhub.device.pumpa.driver.CommMirae;
import com.gsc.kixxhub.device.pumpa.driver.CommPrimeSelf;
import com.gsc.kixxhub.device.pumpa.driver.CommPrimeSelfODT;
import com.gsc.kixxhub.device.pumpa.driver.CommSK;
import com.gsc.kixxhub.device.pumpa.driver.CommSomoSelf;
import com.gsc.kixxhub.device.pumpa.driver.CommSomoSelfN;
import com.gsc.kixxhub.device.pumpa.driver.CommTatsuno;
import com.gsc.kixxhub.device.pumpa.driver.CommTatsunoMPPL_vODT;
import com.gsc.kixxhub.device.pumpa.driver.CommTatsunoN;
import com.gsc.kixxhub.device.pumpa.driver.CommTatsunoSelf_MPP6;
import com.gsc.kixxhub.device.pumpa.driver.CommTatsuno_MPP6;
import com.gsc.kixxhub.device.pumpa.driver.CommTokico;
import com.gsc.kixxhub.device.pumpa.driver.CommTokicoN;
import com.gsc.kixxhub.device.pumpa.driver.CommWooJoo;
import com.gsc.kixxhub.device.pumpa.driverVersion.CommSomoSelfN_401;
import com.gsc.kixxhub.device.pumpa.driverVersion.CommTatsunoSelf_MPP6_361;
import com.gsc.kixxhub.device.pumpa.driverVersion.CommTatsuno_MPP_151;
import com.gsc.kixxhub.device.pumpa.driverVersion.CommTsnSelfHS_noz;
import com.gsc.kixxhub.device.pumpa.driverVersion.CommTsnSelfHS_odt;
import com.gsc.kixxhub.device.pumpa.driverVersion.CommWooJoo_001;
import com.gsc.kixxhub.device.pumpa.driverVersion.CommWooJoo_051;
import com.gsc.kixxhub.device.pumpa.driverVersion.CommWooJoo_061;
import com.gsc.kixxhub.device.pumpa.driverVersion.CommWooJoo_101;
import com.gsc.kixxhub.device.pumpa.driverVersion.CommWooJoo_801;

public class DriverScheduler implements Runnable {
	
	int	   dBoxPortNo, openProtocol=0;
	Hashtable<String, DevInfo> devTbl;
	public ObjectQue downQue = new ObjectQue(100);
	String portName; // ttySx
	String portNo; // POS 등록번호, 스마트주유소 PJT 추가
	Vector<TatsunoMPPNoz> tatsunoMPPNozVec; // 다쓰노 MPP 가상노즐번호 처리용 
	public ObjectQue upQue   = new ObjectQue(100);
	
	//--- Creator
	/**
	 * @param portName
	 * @param devTbl
	 * @param tatsunoMPPNozVec
	 */
	public DriverScheduler (String portName, Hashtable<String, DevInfo> devTbl,
			Vector<TatsunoMPPNoz>tatsunoMPPNozVec) {
		
		super();
		this.portName = portName;
		this.devTbl = devTbl;
		this.tatsunoMPPNozVec = tatsunoMPPNozVec;
		
		if (portName != null && portName.length() > 0) { // 스마트주유소 PJT 추가
			String s = portName.substring(portName.length() - 1);
			int n = Change.toValue(s) + 1;
			this.portNo = Change.toString("%02d", n);
		}
	}
	
	/**
	 * @param devInfo
	 * @return
	 * @throws SerialConnectException
	 */
	private CCommDriver createDriver (DevInfo devInfo) throws Exception, SerialConnectException {
		
		CCommDriver 	driver=null;
		SerialParam 	sParam = new SerialParam ();
		int devNo 	   		= Change.toValue(devInfo.devNo);
		int protocolNo 	= Change.toValue(devInfo.protocol);
		int baudRate   	= Change.toValue(devInfo.baudRate);
		String ipAddress= devInfo.ipAddress; // PI2-박동화, 2015-11-25 추가
		String ROMVer  	= devInfo.ROMVersion;
		
		//LogUtility.getPumpALogger().info("[Pump Adaptor] Protocol No = " + protocolNo) ;
		
		if (protocolNo==Protocol.WooJoo) { // 우주
			
			if (ROMVer.equals("000") || ROMVer.equals("201")) // 우주표준, 201=길바꼬1
				driver = new CommWooJoo (devNo, ROMVer); 
			else 
			if (ROMVer.equals("001")) // 소모1(Full EOT 송수신 기종)
				driver = new CommWooJoo_001 (devNo, ROMVer);
			else 
			if (ROMVer.equals("002")) // 소모2(페트로비즈))
				driver = new CommWooJoo (devNo, ROMVer);
			else 
			if (ROMVer.equals("051")) // 한국ENE1(금성-경우)
				driver = new CommWooJoo_051 (devNo, ROMVer);
			else
			if (ROMVer.equals("061") || ROMVer.equals("063")) // 061=ENE2(금성-마이컴), 063=ENE4(초고속)  // 추가-063 (09/03/10)
				driver = new CommWooJoo_061 (devNo, ROMVer);
			else 
				// 102=동화 천정형(주유완료 상태정보 미수신처리), 103=동화 뉴천정형(노즐업/다운시 잔전 발생)
				// 104=동화 Vantage형, 노즐다운에서 주유허가 하면 프리셋 안됨
			if (ROMVer.equals("101") || ROMVer.equals("102") || ROMVer.equals("103") || ROMVer.equals("104")) // 동화프라임
				driver = new CommWooJoo_101 (devNo, ROMVer);
			else 
			if (ROMVer.equals("801")) // 테스트용 추가(08/12/05)
				driver = new CommWooJoo_801 (devNo, ROMVer);
			
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
	 	 						   	   SerialPort.STOPBITS_2,
	 	 						       SerialPort.PARITY_NONE);
		}
		else if (protocolNo==Protocol.Tatsuno) { // 다쓰노 구형
			driver = new CommTatsuno (devNo, ROMVer);
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
					   				   SerialPort.STOPBITS_1,
					   				   SerialPort.PARITY_EVEN);
		}
		else if (protocolNo==Protocol.TatsunoN) { // 다쓰노 신형
			driver = new CommTatsunoN (devNo, ROMVer);
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
					   				   SerialPort.STOPBITS_1,
					   				   SerialPort.PARITY_EVEN);
		}
		else if (protocolNo==Protocol.Tokico) { // 도끼꼬 구형
			driver = new CommTokico (devNo, ROMVer, devTbl);
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
					   				   SerialPort.STOPBITS_2,
					   				   SerialPort.PARITY_NONE);
		}
		else if (protocolNo==Protocol.TokicoN) { // 도끼꼬 신형
			
//			if (ROMVer.equals("000")) // 표준(노즐업시 허가)
				driver = new CommTokicoN (devNo, ROMVer); 
//			else 
//			if (ROMVer.equals("301") && ) // 도끼꼬신형1(노즐다운시 허가->주유중자료 미수신시 사용)
//				driver = new CommTokicoN (devNo, ROMVer);
			
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
					   				   SerialPort.STOPBITS_1,
					   				   SerialPort.PARITY_EVEN);
		}
		else if (protocolNo==Protocol.TatsunoSelfMPP4) { // 다쓰노셀프 ODT 4복식			
			if (ROMVer.equals("000") || ROMVer.equals("351")) // 표준버전 or 구형(1만원~)
				driver = new CommTatsunoSelf_MPP6 (devNo, ROMVer, 4);
			else 
			if (ROMVer.equals("361")) // BNA 버전
				driver = new CommTatsunoSelf_MPP6_361 (devNo, ROMVer, 4);
			
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
   									   SerialPort.STOPBITS_1,
   									   SerialPort.PARITY_EVEN);
		}
		else if (protocolNo==Protocol.TatsunoSelfMPP6) { // 다쓰노셀프 ODT 6복식			
			if (ROMVer.equals("000") || ROMVer.equals("351")) // 표준버전 or 구형(1만원~)
				driver = new CommTatsunoSelf_MPP6 (devNo, ROMVer, 6);
			else 
			if (ROMVer.equals("361")) // BNA 버전
				driver = new CommTatsunoSelf_MPP6_361 (devNo, ROMVer, 6);
			
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
   									   SerialPort.STOPBITS_1,
   									   SerialPort.PARITY_EVEN);
		}
		else if (protocolNo==Protocol.TatsunoSelfHS) { // 신형다쓰노셀프(현성) - 추가 2012.08, dhp
				
			// 노즐과 ODT를 동일한 포트에서 기동함.
			if (ROMVer.equals("371")) // 노즐용 롬버전
				driver = new CommTsnSelfHS_noz (devNo, ROMVer);
			else 
			if (ROMVer.equals("372")) // ODT용 롬버전
				driver = new CommTsnSelfHS_odt (devNo, ROMVer);
			
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
									   SerialPort.STOPBITS_1,
									   SerialPort.PARITY_EVEN);
		}
		else if (protocolNo==Protocol.TatsunoMPPL_vODT) { // 다쓰노 MPP형 일반 주유기용 가상ODT		
			driver = new CommTatsunoMPPL_vODT (devNo, ROMVer, 4);
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
   									   SerialPort.STOPBITS_1,
   									   SerialPort.PARITY_EVEN);
		}
		else if (protocolNo==Protocol.SomoSelf) { // 소모셀프 ODT
			driver = new CommSomoSelf (devNo, ROMVer);
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
   									   SerialPort.STOPBITS_1,
   									   SerialPort.PARITY_EVEN);
		}
		else if (protocolNo==Protocol.SomoSelfN) { // 소모셀프 ODT 신형
			//driver = new CommSomoSelfN (devNo, ROMVer);
	
			if (ROMVer.equals("000")) // 표준버전
				driver = new CommSomoSelfN (devNo, ROMVer);
			else 
			if (ROMVer.equals("401")) // CNP 버전(DLE/EOT NozID 추가)
				driver = new CommSomoSelfN_401 (devNo, ROMVer);
			
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
   									   SerialPort.STOPBITS_1,
   									   SerialPort.PARITY_EVEN);
		} 
		else if (protocolNo==Protocol.PrimeSelf) { // 동화프라임셀프 ODT
			driver = new CommPrimeSelf (devNo, ROMVer);
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
   									   SerialPort.STOPBITS_1,
   									   SerialPort.PARITY_EVEN);
		}
		else if (protocolNo==Protocol.PrimeSelfODT) { // 동화프라임셀프 가상ODT
			driver = new CommPrimeSelfODT (devNo, ROMVer);
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
   									   SerialPort.STOPBITS_1,
   									   SerialPort.PARITY_EVEN);
		} 
		else if (protocolNo==Protocol.TatsunoMPP4 || // 다쓰노 MPP 4복식 주유기
				 protocolNo==Protocol.TatsunoMPP6) { // 다쓰노 MPP 6복식 주유기
			if (ROMVer.equals("000")) // MPP형 셀프 주유기
				driver = new CommTatsuno_MPP6 (devNo, ROMVer, tatsunoMPPNozVec); // tatsunoMPPNozVec = 가상 노즐번호 처리용
			else
			if (ROMVer.equals("151")) // MPP형 일반 주유기
				driver = new CommTatsuno_MPP_151 (devNo, ROMVer, tatsunoMPPNozVec); // tatsunoMPPNozVec = 가상 노즐번호 처리용
			
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
					   				   SerialPort.STOPBITS_1,
					   				   SerialPort.PARITY_EVEN);
		}
//		else if (protocolNo==Protocol.TatsunoMPPL) { // 다쓰노 MPP형 일반 주유기
//			driver = new CommTatsuno_MPPL (devNo, ROMVer, tatsunoMPPNozVec); // tatsunoMPPNozVec = 가상 노즐번호 처리용
//			sParam.setParam (baudRate, SerialPort.DATABITS_8,
//					   				   SerialPort.STOPBITS_1,
//					   				   SerialPort.PARITY_EVEN);
//		}
		else if (protocolNo==Protocol.SK) { // SK 주유기
			driver = new CommSK (devNo, ROMVer);
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
   									   SerialPort.STOPBITS_2,
   									   SerialPort.PARITY_EVEN);
		}
		else if (protocolNo==Protocol.GasODT) { // 충전기 ODT
			driver = new CommMirae (devNo);
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
   									   SerialPort.STOPBITS_1,
   									   SerialPort.PARITY_NONE);
		}
		/**
		 * 변경내용 : 신규(TCP/IP 통신방식)
		 * 변경일자 : 2016.02.12 양일준
		 */
		else if (protocolNo==Protocol.newGasODT) { // 충전기 ODT (TCP/IP)
			LogUtility.getLogger().info("eriver sheduler 충전기 ODT 실행 ");
			driver = new CommGSGasODTMr (devNo, ROMVer, ipAddress);
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
   									   SerialPort.STOPBITS_1,
   									   SerialPort.PARITY_NONE);
		}
		// GS 표준 프로토콜(Serial통신) - 시험 개발
		else if (protocolNo==Protocol.GSSelfODT) { // 표준 셀프ODT
			driver = new CommGSSelfODT (devNo, ROMVer, 4);
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
   									   SerialPort.STOPBITS_1,
   									   SerialPort.PARITY_EVEN);
		}		
		// GS 표준셀프 ODT 프로토콜(LAN통신) - PI2-박동화, 2015-11-18 추가
		else if (protocolNo==Protocol.GSSelfODTi) { // 표준 셀프ODT
			driver = new CommGSSelfODTi (devNo, ROMVer, ipAddress);
		}	
		// GS 표준셀프 주유기 프로토콜(가상주유기) - PI2-박동화, 2015-11-18 추가
		else if (protocolNo==Protocol.GSSelfVNoz) { // 표준 셀프주유기(가상)
			driver = new CommGSSelfVNoz (devNo, ROMVer);
		}
		/*// 소모셀프 IC용(여전법 대응) - PI2-박동화, 2015-11-05 추가
		else if (protocolNo==Protocol.SomoSelfIC) {
			
			// 노즐과 ODT를 동일한 포트에서 기동함.
			if (ROMVer.equals("421")) // 노즐용 롬버전
				driver = new CommSomoSelfIC_noz (devNo, ROMVer);
			else 
			if (ROMVer.equals("422")) // ODT용 롬버전
				driver = new CommSomoSelfIC_odt (devNo, ROMVer);
			
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
									   SerialPort.STOPBITS_1,
									   SerialPort.PARITY_EVEN);
		}*/
		
		driver.setSerialParam(sParam);
		driver.protocol = protocolNo;
		
		return driver;
	}
	
	/**
	 * @param driver
	 * @return
	 */
	private PortManager openCommPort (CCommDriver driver) {

		PortManager portMgr = new PortManager();
		SerialParam sParam = new SerialParam();
		boolean rtn;
		
		try {
		    //--- Open CommPort ---//
			rtn = portMgr.openCommPort (portName);
	
			if (rtn==true)
				LogUtility.getPumpALogger().info("Open port success : " + portNo + "(" + portName + ")");
			else {
				LogUtility.getPumpALogger().info("Open port fail !!!!! : " + portNo + "(" + portName + ")");
		    	LogUtility.getPumpALogger().error("   Oops! Please check the port number and parameters");
				return null;
			}
	
		    //--- Set CommPort Parameters ---//
			sParam = driver.getSerialParam();
		    rtn = portMgr.setCommParams (sParam);
	
		    if (rtn==true) {
			    LogUtility.getPumpALogger().info(" -baudRate=" + sParam.baudRate);
			    LogUtility.getPumpALogger().info(" -dataBits=" + sParam.dataBits);
			    LogUtility.getPumpALogger().info(" -stopBits=" + sParam.stopBits);
			    LogUtility.getPumpALogger().info(" -parity  =" + sParam.parity + "\n");
		    }
		    else {
		    	LogUtility.getPumpALogger().error("Set parameter fail !!!!! : " + portNo + "(" + portName + ")");
		    	LogUtility.getPumpALogger().error("   Oops! Please check the port parameters");
				return null;
		    }
		    
		} catch (SerialConnectException e) {
			LogUtility.getPumpALogger().error("Open port fail !!!!! : " + portNo + "(" + portName + ")");
			LogUtility.getPumpALogger().error(e.getMessage(),e);
			return null;
		}
		 catch (Exception e) {
			LogUtility.getPumpALogger().error("Open port fail !!!!! : " + portNo + "(" + portName + ")");
			LogUtility.getPumpALogger().error(e.getMessage(),e);
			return null;
		}

	    return portMgr;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		startScheduling();
	}

	/**
	 * @throws SerialConnectException
	 */
	private void startScheduling () {
		
		Vector<CCommDriver> drvList = new Vector<CCommDriver>();
		int		nozCnt=0;
		
		CCommDriver 	driver = null;
		PortManager 	portMgr = null;
		
		try {
			
			Enumeration<DevInfo> devInfoEnum = devTbl.elements();
			while (devInfoEnum.hasMoreElements()) {
				
				DevInfo devInfo = devInfoEnum.nextElement();
	
				driver = createDriver(devInfo); // Protocol driver 생성
				
				// PI2-박동화, 2015-11-16 추가변경
				if (driver.getInterface_method() == 0) { // Serial 방식
					
					if (driver.protocol != openProtocol) {
						portMgr = openCommPort (driver);
						openProtocol = driver.protocol;
					}
					
					if (portMgr != null) { // 스마트 주유소 PJT 수정
						driver.nozType 		   = Change.toValue(devInfo.devType); // 2=self nozzle
						driver.connectDeviceNo = Change.toValue(devInfo.connectDevNo);
						driver.setInputStream(portMgr.getInputStream());
						driver.setOutputStream(portMgr.getOutputStream());
						drvList.add (driver);
					}
				} 
				// PI2-박동화, 2015-11-16 추가변경
				else if(driver.getInterface_method() == 1) { // TCP/IP 방식
					
					if (driver.protocol != openProtocol) {
						driver.nozType 		   = Change.toValue(devInfo.devType); // 2=self nozzle
						driver.connectDeviceNo = Change.toValue(devInfo.connectDevNo);
						driver.ipAddress 	   = devInfo.ipAddress;
						drvList.add (driver);
					}
				}
				
				Thread.sleep(10);
				
			}	
		}
	    catch (SerialConnectException e) {
			LogUtility.getPumpALogger().error("Catched SerialConnectException in DriverScheduler's create process------>>>>");
			LogUtility.getPumpALogger().error(e.getMessage(), e);
	    }
		catch (Exception e) {
			LogUtility.getPumpALogger().error("Catched Exception in DriverScheduler's create process------>>>>");
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}

		Sleep.sleep(500);
		
    	//-###### Driver polling sequence ######-//
		WorkingMessage wm = null;
		WorkingMessage rcvWm = null;
		nozCnt = drvList.size();
		int cnt, i;

	    while (true) {
	
		    try {
		    	
				if (downQue.isEmpty() == false) {
					wm = (WorkingMessage) downQue.deQueue();
//					LogUtility.getPumpALogger().debug("----->>> downQue.deQueue() wm=" + wm.getCommand() +
//							" ltemCnt=" + downQue.getItemCount());
				}
				else
					wm = null;
				
			} catch (Exception e1) {
				LogUtility.getPumpALogger().error(e1.getMessage(), e1);
			} 
	
		    for (i=0; i<nozCnt; i++) {

		    	try {		    	    	
					
				    //--- Data request ---//
				    driver = drvList.get(nozCnt - i - 1);
				    	
				    if (wm != null) { // 전송 데이터 있으면
				    		
				    	if (driver.getNozzleNumber() == Change.toValue(wm.getNozzleNo())) {			    			
				    		//LogUtility.getPumpALogger().info("3. DriverScheduler 수신완료(DriverScheduler) : devNo=" + 
				    				//wm.getNozzleNo() + " cmd=" + wm.getCommand() + "\n");
				    			
				    		driver.insertSendQueue(wm); // Data insert to Driver's sendQue
				    	}
				    }
				    	
				    driver.requestData(); // Request to device
	
				    //--- Upload recv workingMessage to module
				    cnt = driver.getRecvQueueCnt();
				    for (int c=0; c<cnt; c++) {
				    		
				    	rcvWm = driver.extractRecvQueue();
				    	//System.out.printf("<<<<<<<<Up Message(in DriverScheduler)=%s\n", rcvWm.getCommand());
				    		
				    	upQue.enQueue(rcvWm); // Insert to upload queue
				    }
			    	
				    //LogUtility.getPumpALogger().debug("--------driver scheduler------");

				    //Sleep.sleep(20); // Temp TimeDelay
				}
			    catch (SerialConnectException e) {
					LogUtility.getPumpALogger().error("Catched SerialConnectException in DriverScheduler's polling process------>>>>");
					LogUtility.getPumpALogger().error(e.getMessage(), e);
				}
				catch (Exception e) {
					LogUtility.getPumpALogger().error("Catched Exception in DriverScheduler's polling process------>>>>");
					LogUtility.getPumpALogger().error(e.getMessage(), e);
				}
		    	
		    	// PI2-박동화, 2016-01-02 추가 
		    	if (driver.getInterface_method()==1) { // TCP/IP		    		
		    		
		    		int msec = (40 / nozCnt) + 5;
		    		Sleep.sleep(msec); 
		    	}
		    	else				    
				    Sleep.sleep(1); // PI2-박동화, 2015-11-20 추가
			}
		}
	}

}
