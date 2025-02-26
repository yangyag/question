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
	String portNo; // POS ��Ϲ�ȣ, ����Ʈ������ PJT �߰�
	Vector<TatsunoMPPNoz> tatsunoMPPNozVec; // �پ��� MPP ��������ȣ ó���� 
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
		
		if (portName != null && portName.length() > 0) { // ����Ʈ������ PJT �߰�
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
		String ipAddress= devInfo.ipAddress; // PI2-�ڵ�ȭ, 2015-11-25 �߰�
		String ROMVer  	= devInfo.ROMVersion;
		
		//LogUtility.getPumpALogger().info("[Pump Adaptor] Protocol No = " + protocolNo) ;
		
		if (protocolNo==Protocol.WooJoo) { // ����
			
			if (ROMVer.equals("000") || ROMVer.equals("201")) // ����ǥ��, 201=��ٲ�1
				driver = new CommWooJoo (devNo, ROMVer); 
			else 
			if (ROMVer.equals("001")) // �Ҹ�1(Full EOT �ۼ��� ����)
				driver = new CommWooJoo_001 (devNo, ROMVer);
			else 
			if (ROMVer.equals("002")) // �Ҹ�2(��Ʈ�κ���))
				driver = new CommWooJoo (devNo, ROMVer);
			else 
			if (ROMVer.equals("051")) // �ѱ�ENE1(�ݼ�-���)
				driver = new CommWooJoo_051 (devNo, ROMVer);
			else
			if (ROMVer.equals("061") || ROMVer.equals("063")) // 061=ENE2(�ݼ�-������), 063=ENE4(�ʰ��)  // �߰�-063 (09/03/10)
				driver = new CommWooJoo_061 (devNo, ROMVer);
			else 
				// 102=��ȭ õ����(�����Ϸ� �������� �̼���ó��), 103=��ȭ ��õ����(�����/�ٿ�� ���� �߻�)
				// 104=��ȭ Vantage��, ����ٿ�� �����㰡 �ϸ� ������ �ȵ�
			if (ROMVer.equals("101") || ROMVer.equals("102") || ROMVer.equals("103") || ROMVer.equals("104")) // ��ȭ������
				driver = new CommWooJoo_101 (devNo, ROMVer);
			else 
			if (ROMVer.equals("801")) // �׽�Ʈ�� �߰�(08/12/05)
				driver = new CommWooJoo_801 (devNo, ROMVer);
			
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
	 	 						   	   SerialPort.STOPBITS_2,
	 	 						       SerialPort.PARITY_NONE);
		}
		else if (protocolNo==Protocol.Tatsuno) { // �پ��� ����
			driver = new CommTatsuno (devNo, ROMVer);
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
					   				   SerialPort.STOPBITS_1,
					   				   SerialPort.PARITY_EVEN);
		}
		else if (protocolNo==Protocol.TatsunoN) { // �پ��� ����
			driver = new CommTatsunoN (devNo, ROMVer);
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
					   				   SerialPort.STOPBITS_1,
					   				   SerialPort.PARITY_EVEN);
		}
		else if (protocolNo==Protocol.Tokico) { // ������ ����
			driver = new CommTokico (devNo, ROMVer, devTbl);
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
					   				   SerialPort.STOPBITS_2,
					   				   SerialPort.PARITY_NONE);
		}
		else if (protocolNo==Protocol.TokicoN) { // ������ ����
			
//			if (ROMVer.equals("000")) // ǥ��(������� �㰡)
				driver = new CommTokicoN (devNo, ROMVer); 
//			else 
//			if (ROMVer.equals("301") && ) // ����������1(����ٿ�� �㰡->�������ڷ� �̼��Ž� ���)
//				driver = new CommTokicoN (devNo, ROMVer);
			
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
					   				   SerialPort.STOPBITS_1,
					   				   SerialPort.PARITY_EVEN);
		}
		else if (protocolNo==Protocol.TatsunoSelfMPP4) { // �پ��뼿�� ODT 4����			
			if (ROMVer.equals("000") || ROMVer.equals("351")) // ǥ�ع��� or ����(1����~)
				driver = new CommTatsunoSelf_MPP6 (devNo, ROMVer, 4);
			else 
			if (ROMVer.equals("361")) // BNA ����
				driver = new CommTatsunoSelf_MPP6_361 (devNo, ROMVer, 4);
			
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
   									   SerialPort.STOPBITS_1,
   									   SerialPort.PARITY_EVEN);
		}
		else if (protocolNo==Protocol.TatsunoSelfMPP6) { // �پ��뼿�� ODT 6����			
			if (ROMVer.equals("000") || ROMVer.equals("351")) // ǥ�ع��� or ����(1����~)
				driver = new CommTatsunoSelf_MPP6 (devNo, ROMVer, 6);
			else 
			if (ROMVer.equals("361")) // BNA ����
				driver = new CommTatsunoSelf_MPP6_361 (devNo, ROMVer, 6);
			
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
   									   SerialPort.STOPBITS_1,
   									   SerialPort.PARITY_EVEN);
		}
		else if (protocolNo==Protocol.TatsunoSelfHS) { // �����پ��뼿��(����) - �߰� 2012.08, dhp
				
			// ����� ODT�� ������ ��Ʈ���� �⵿��.
			if (ROMVer.equals("371")) // ����� �ҹ���
				driver = new CommTsnSelfHS_noz (devNo, ROMVer);
			else 
			if (ROMVer.equals("372")) // ODT�� �ҹ���
				driver = new CommTsnSelfHS_odt (devNo, ROMVer);
			
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
									   SerialPort.STOPBITS_1,
									   SerialPort.PARITY_EVEN);
		}
		else if (protocolNo==Protocol.TatsunoMPPL_vODT) { // �پ��� MPP�� �Ϲ� ������� ����ODT		
			driver = new CommTatsunoMPPL_vODT (devNo, ROMVer, 4);
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
   									   SerialPort.STOPBITS_1,
   									   SerialPort.PARITY_EVEN);
		}
		else if (protocolNo==Protocol.SomoSelf) { // �Ҹ��� ODT
			driver = new CommSomoSelf (devNo, ROMVer);
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
   									   SerialPort.STOPBITS_1,
   									   SerialPort.PARITY_EVEN);
		}
		else if (protocolNo==Protocol.SomoSelfN) { // �Ҹ��� ODT ����
			//driver = new CommSomoSelfN (devNo, ROMVer);
	
			if (ROMVer.equals("000")) // ǥ�ع���
				driver = new CommSomoSelfN (devNo, ROMVer);
			else 
			if (ROMVer.equals("401")) // CNP ����(DLE/EOT NozID �߰�)
				driver = new CommSomoSelfN_401 (devNo, ROMVer);
			
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
   									   SerialPort.STOPBITS_1,
   									   SerialPort.PARITY_EVEN);
		} 
		else if (protocolNo==Protocol.PrimeSelf) { // ��ȭ�����Ӽ��� ODT
			driver = new CommPrimeSelf (devNo, ROMVer);
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
   									   SerialPort.STOPBITS_1,
   									   SerialPort.PARITY_EVEN);
		}
		else if (protocolNo==Protocol.PrimeSelfODT) { // ��ȭ�����Ӽ��� ����ODT
			driver = new CommPrimeSelfODT (devNo, ROMVer);
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
   									   SerialPort.STOPBITS_1,
   									   SerialPort.PARITY_EVEN);
		} 
		else if (protocolNo==Protocol.TatsunoMPP4 || // �پ��� MPP 4���� ������
				 protocolNo==Protocol.TatsunoMPP6) { // �پ��� MPP 6���� ������
			if (ROMVer.equals("000")) // MPP�� ���� ������
				driver = new CommTatsuno_MPP6 (devNo, ROMVer, tatsunoMPPNozVec); // tatsunoMPPNozVec = ���� �����ȣ ó����
			else
			if (ROMVer.equals("151")) // MPP�� �Ϲ� ������
				driver = new CommTatsuno_MPP_151 (devNo, ROMVer, tatsunoMPPNozVec); // tatsunoMPPNozVec = ���� �����ȣ ó����
			
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
					   				   SerialPort.STOPBITS_1,
					   				   SerialPort.PARITY_EVEN);
		}
//		else if (protocolNo==Protocol.TatsunoMPPL) { // �پ��� MPP�� �Ϲ� ������
//			driver = new CommTatsuno_MPPL (devNo, ROMVer, tatsunoMPPNozVec); // tatsunoMPPNozVec = ���� �����ȣ ó����
//			sParam.setParam (baudRate, SerialPort.DATABITS_8,
//					   				   SerialPort.STOPBITS_1,
//					   				   SerialPort.PARITY_EVEN);
//		}
		else if (protocolNo==Protocol.SK) { // SK ������
			driver = new CommSK (devNo, ROMVer);
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
   									   SerialPort.STOPBITS_2,
   									   SerialPort.PARITY_EVEN);
		}
		else if (protocolNo==Protocol.GasODT) { // ������ ODT
			driver = new CommMirae (devNo);
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
   									   SerialPort.STOPBITS_1,
   									   SerialPort.PARITY_NONE);
		}
		/**
		 * ���泻�� : �ű�(TCP/IP ��Ź��)
		 * �������� : 2016.02.12 ������
		 */
		else if (protocolNo==Protocol.newGasODT) { // ������ ODT (TCP/IP)
			LogUtility.getLogger().info("eriver sheduler ������ ODT ���� ");
			driver = new CommGSGasODTMr (devNo, ROMVer, ipAddress);
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
   									   SerialPort.STOPBITS_1,
   									   SerialPort.PARITY_NONE);
		}
		// GS ǥ�� ��������(Serial���) - ���� ����
		else if (protocolNo==Protocol.GSSelfODT) { // ǥ�� ����ODT
			driver = new CommGSSelfODT (devNo, ROMVer, 4);
			sParam.setParam (baudRate, SerialPort.DATABITS_8,
   									   SerialPort.STOPBITS_1,
   									   SerialPort.PARITY_EVEN);
		}		
		// GS ǥ�ؼ��� ODT ��������(LAN���) - PI2-�ڵ�ȭ, 2015-11-18 �߰�
		else if (protocolNo==Protocol.GSSelfODTi) { // ǥ�� ����ODT
			driver = new CommGSSelfODTi (devNo, ROMVer, ipAddress);
		}	
		// GS ǥ�ؼ��� ������ ��������(����������) - PI2-�ڵ�ȭ, 2015-11-18 �߰�
		else if (protocolNo==Protocol.GSSelfVNoz) { // ǥ�� ����������(����)
			driver = new CommGSSelfVNoz (devNo, ROMVer);
		}
		/*// �Ҹ��� IC��(������ ����) - PI2-�ڵ�ȭ, 2015-11-05 �߰�
		else if (protocolNo==Protocol.SomoSelfIC) {
			
			// ����� ODT�� ������ ��Ʈ���� �⵿��.
			if (ROMVer.equals("421")) // ����� �ҹ���
				driver = new CommSomoSelfIC_noz (devNo, ROMVer);
			else 
			if (ROMVer.equals("422")) // ODT�� �ҹ���
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
	
				driver = createDriver(devInfo); // Protocol driver ����
				
				// PI2-�ڵ�ȭ, 2015-11-16 �߰�����
				if (driver.getInterface_method() == 0) { // Serial ���
					
					if (driver.protocol != openProtocol) {
						portMgr = openCommPort (driver);
						openProtocol = driver.protocol;
					}
					
					if (portMgr != null) { // ����Ʈ ������ PJT ����
						driver.nozType 		   = Change.toValue(devInfo.devType); // 2=self nozzle
						driver.connectDeviceNo = Change.toValue(devInfo.connectDevNo);
						driver.setInputStream(portMgr.getInputStream());
						driver.setOutputStream(portMgr.getOutputStream());
						drvList.add (driver);
					}
				} 
				// PI2-�ڵ�ȭ, 2015-11-16 �߰�����
				else if(driver.getInterface_method() == 1) { // TCP/IP ���
					
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
				    	
				    if (wm != null) { // ���� ������ ������
				    		
				    	if (driver.getNozzleNumber() == Change.toValue(wm.getNozzleNo())) {			    			
				    		//LogUtility.getPumpALogger().info("3. DriverScheduler ���ſϷ�(DriverScheduler) : devNo=" + 
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
		    	
		    	// PI2-�ڵ�ȭ, 2016-01-02 �߰� 
		    	if (driver.getInterface_method()==1) { // TCP/IP		    		
		    		
		    		int msec = (40 / nozCnt) + 5;
		    		Sleep.sleep(msec); 
		    	}
		    	else				    
				    Sleep.sleep(1); // PI2-�ڵ�ȭ, 2015-11-20 �߰�
			}
		}
	}

}
