package com.gsc.kixxhub.device.pumpa.controller;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.gsc.kixxhub.common.data.IConstant;
import com.gsc.kixxhub.common.data.pump.HC_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HD_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.M1_DBoxInfo;
import com.gsc.kixxhub.common.data.pump.M1_NozzleInfo;
import com.gsc.kixxhub.common.data.pump.M1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P2_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P5_1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P5_NozzleInfo;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.common.utility.property.PropertyManager;
import com.gsc.kixxhub.device.pumpa.common.Change;
import com.gsc.kixxhub.device.pumpa.common.Protocol;
import com.gsc.kixxhub.device.pumpa.common.Sleep;
import com.gsc.kixxhub.device.pumpa.common.Time;

public class DeviceSelector {

	private static 	int[] S3_cnt = new int[101];

	// ���� ������/ODT ���ἳ����
	Hashtable<String, String> connectDBoxPortNoTbl = new Hashtable<String, String>(); ; // key = devNo
	Hashtable<String, String> connectODTNoTbl = new Hashtable<String, String>();

	Hashtable<String, Hashtable> dBoxPortTbl = new Hashtable<String, Hashtable>(); // key = dBoxPortNo
	// ����̽� ������ ȯ�漳����
	Hashtable<String, DevInfo> devAllTbl = new Hashtable<String, DevInfo>();
	Hashtable<String, String> deviceTypeTbl = new Hashtable<String, String>();
	Hashtable<String, String> devProtocolTbl = new Hashtable<String, String>();
	Vector<DriverScheduler> drvSchVec = new Vector<DriverScheduler>();
	
	// Hashtable<String, String> connectNozNoTbl = new Hashtable<String, String>();
	Vector<String> selfDevNoVec = new Vector<String>();  
	
	// �پ���6����(����) ��������ȣ ó����
	Vector<TatsunoMPPNoz> tatsunoMPPNozVec = new Vector<TatsunoMPPNoz>();

	/**
	 * @param nozNo
	 * @return
	 */
	public String getConnectODTNo(String nozNo) throws Exception {

		String cODTNo = connectODTNoTbl.get(nozNo);

		return cODTNo;
	}

	/**
	 * @param devNo
	 * @return
	 */
	public String getDBoxPortNo(String devNo) throws Exception {

		String dBoxPortStr = connectDBoxPortNoTbl.get(devNo);

		return dBoxPortStr;
	}

	/**
	 * @return
	 */
	public Hashtable<String, DevInfo> getDevAllTbl() throws Exception {
		return devAllTbl;
	}

	/**
	 * @param devNo
	 * @return
	 */
	public String getDeviceProtocol(String devNo) throws Exception {

		String protocol = devProtocolTbl.get(devNo);

		return protocol;
	}

	/**
	 * @param devNo
	 * @return
	 */
	public String getDeviceType(String devNo) throws Exception {

		String devType = deviceTypeTbl.get(devNo);

		return devType;
	}

	public void init(WorkingMessage wm) { // Initilization
		
//		LogUtility.getPumpALogger().info("\n<KixxHub Pump Adaptor version info>");
//		LogUtility.getPumpALogger().info(" -Version : 1.1.7, Issue-date : 2009/07/20");
		
		try {
			setDeviceEnv(wm);
		} catch (Exception e) {
			// TODO �ڵ� ������ catch ���
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	}

	/**
	 * @param orgWm
	 * @return
	 */
	public boolean insertDrvSchDownQue(WorkingMessage orgWm) throws Exception { // 2008.07.22�� ����
	//public boolean insertDrvSchDownQue(WorkingMessage wm) {
		
		WorkingMessage wm = (WorkingMessage) orgWm.clone(); 
	
		//LogUtility.getPumpALogger().info("+++Called insertDrvSchDownQue(), cmd="+wm.getCommand()+
				//" noz="+wm.getNozzleNo()+" conNoz="+wm.getConnectNozzleNo());

		String dBoxPortStr = getDBoxPortNo(wm.getNozzleNo());
		int dBoxPortNo = Change.toValue(dBoxPortStr);

		Enumeration<DriverScheduler> drvSchEnum = drvSchVec.elements();
		while (drvSchEnum.hasMoreElements()) {

			DriverScheduler drvSch = drvSchEnum.nextElement();

			if (drvSch.dBoxPortNo == dBoxPortNo) {
				drvSch.downQue.enQueue(wm);
				return true;
			}
		}

		//System.out.printf ("\nInsert DriverScheduler's downQue fail! : %s\n", wm.getNozzleNo());
		return false;
	}

	/**
	 * @param nozStr
	 * @return
	 */
	public boolean isSelfDevice(String nozStr) throws Exception {

		return selfDevNoVec.contains(nozStr);
	}

	/**
	 * @param wm
	 * @return
	 */
	public boolean selectDevice(WorkingMessage wm) throws Exception { // Called by ProcessSelector

		boolean rtn;
		
		LogUtility.getPumpALogger().info("selectDevice() ȣ��� : cmd=" + wm.getCommand()+
				" noz="+wm.getNozzleNo()+" conNoz="+wm.getConnectNozzleNo()+" tarNoz="+wm.getTargetNozzleNo());
		
		// �ش� D-Box Port�� ������ ����
		rtn = insertDrvSchDownQue(wm);
		// ���������� �� ODT, EX_WorkingMessage �� �߰�ó��
		if (isSelfDevice(wm.getNozzleNo())==true || wm.getCommand().equals("EX")) { 
			
			selfDevExchangeData(wm); // �߰� ȣ��
			
			//LogUtility.getPumpALogger().info("2. Called selectDevice() by ProcessSelector : cmd=" + 
				//wm.getCommand()+" noz="+wm.getNozzleNo()+" conNoz=" + wm.getConnectNozzleNo()+ "\n");
		}
		
		//--- �پ���6����(����) ��������ȣ ó���� ---//
		if (wm.getCommand().equals("P5_1")) {
			
			P5_1_WorkingMessage P5_wm = (P5_1_WorkingMessage) wm;
			
			if (P5_wm.getMode().equals("0")) { // 0=�ʱ�ȭ ���, 1=�ܰ����� ���
				
				String odtNo = P5_wm.getOdtID();
				String protocol = getDeviceProtocol(odtNo);
							
				int nProtocol = Change.toValue(protocol);
				if (nProtocol == Protocol.TatsunoSelfMPP4 ||
					nProtocol == Protocol.TatsunoSelfMPP6 ||
					nProtocol == Protocol.TatsunoMPPL_vODT ||
					nProtocol == Protocol.GSSelfODT) { // �پ��� MPP�� ������ ����ODT -> 09/06/11 �߰�
	
					//LogUtility.getPumpALogger().info("@@@@Set Tatsuno Self Info. ODT=" + odtNo);
	
					TatsunoMPPNoz tsnMppNoz = new TatsunoMPPNoz();
					tsnMppNoz.setODTNo(odtNo);
					
					Vector<P5_NozzleInfo> nozInfoVec = P5_wm.getNozzleInfo();
					Enumeration<P5_NozzleInfo> nozInfoEnum = nozInfoVec.elements();
	
					Vector<String> conNozNoVec = new Vector<String>();
					
					while(nozInfoEnum.hasMoreElements()) {
						
						P5_NozzleInfo nozInfo = nozInfoEnum.nextElement();
						conNozNoVec.add(nozInfo.getNozzleNumber());
	
						//System.out.printf("@@@@@Tatsuno MPP(1) :: nozNo=%s\n", nozInfo.getNozzleNumber());
						//LogUtility.getPumpALogger().info("@@@@ -self nozzle="+ nozInfo.getNozzleNumber());
					}
					tsnMppNoz.setConnectNozNoVec(conNozNoVec);
					tatsunoMPPNozVec.add(tsnMppNoz);
					LogUtility.getPumpALogger().info("\n");
				}
			}
		} //-- End of �پ��� ���� ������ ������� ó���� --//

		return rtn;
	}

	//public void selfDevExchangeData(WorkingMessage orgWm) { // 2008.07.22�� ����
	/**
	 * @param orgWm
	 */
	public void selfDevExchangeData(WorkingMessage orgWm) throws Exception {

		boolean istransmitLog = false;
		String orgNozNo=orgWm.getNozzleNo();
		
		WorkingMessage wm = (WorkingMessage) orgWm.clone(); 
		//WorkingMessage wm = (WorkingMessage) orgWm;
		
		String nozStr = wm.getNozzleNo();
		String command = wm.getCommand();
		int fromDevType = Change.toValue(getDeviceType(nozStr));
		int protocol;
		
		//LogUtility.getPumpALogger().info("---Called selfDevExchangeData(), cmd="+wm.getCommand()+
				//" noz="+wm.getNozzleNo()+" conNoz="+wm.getConnectNozzleNo());
		
		if (wm.getCommand().equals("EX")) { // EX_WorkingMessage
			
			wm.setNozzleNo(wm.getTargetNozzleNo()); // target device

			insertDrvSchDownQue(wm);
			istransmitLog = true;
		}
		else if (fromDevType == 2) { //#### ���������⿡�� ����ODT�� ���� �߰� ####//

			String conODTNo = getConnectODTNo(nozStr);
			wm.setNozzleNo(conODTNo); // target device
			protocol = Change.toValue(getDeviceProtocol(conODTNo));
			wm.setConnectNozzleNo(nozStr);

			// ������ �������� ����
			if (command.equals("S8") && (protocol==Protocol.SomoSelf ||
										 protocol==Protocol.SomoSelfN  ||
										 protocol==Protocol.TatsunoSelfHS /*||
										 protocol==Protocol.TatsunoSelfMPP4 ||
				  						 protocol==Protocol.TatsunoSelfMPP6*/)) {
				insertDrvSchDownQue(wm);
				istransmitLog = true;
			}			
			// ������ �̻�����(ȸ���ҷ�) ���� // �߰�(09/02/17)
			else if (command.equals("SE") && (protocol==Protocol.SomoSelf ||
										 protocol==Protocol.SomoSelfN  ||
										 protocol==Protocol.TatsunoSelfHS  /*||
										 protocol==Protocol.TatsunoSelfMPP4 ||
										 protocol==Protocol.TatsunoSelfMPP6*/)) {
				insertDrvSchDownQue(wm);
				istransmitLog = false;
			}
			
			// ������ ȯ�漳�� ���� (PI2-�ڵ�ȭ, 2015-11-18 �߰�)
			else if (command.equals("P3_1") && (protocol==Protocol.GSSelfODTi )) {

				insertDrvSchDownQue(wm);
				istransmitLog = true;
			}
			
			// �������� ���
			else if (command.equals("PA") && (protocol==Protocol.SomoSelf ||
					 						  protocol==Protocol.SomoSelfN ||
											  protocol==Protocol.TatsunoSelfMPP4 ||
					  						  protocol==Protocol.TatsunoSelfMPP6 ||
					  						  protocol==Protocol.GSSelfODTi)) {
				insertDrvSchDownQue(wm);
				istransmitLog = true;
			}
			// ����/���� ����(�پ��� ������ ��� POS->Nozzle�ǰ��� ODT->Nozzle�� ��� ��� �ش�) 
			else if (command.equals("PB") && (protocol==Protocol.SomoSelf ||
											  protocol==Protocol.SomoSelfN ||
											  protocol==Protocol.TatsunoSelfMPP4 ||
					  						  protocol==Protocol.TatsunoSelfMPP6 ||
					  						  protocol==Protocol.GSSelfODT || 
					  						  protocol==Protocol.GSSelfODTi )) {

				insertDrvSchDownQue(wm);
				istransmitLog = true;
			}
			// ������ �ڷ�����
			else if (command.equals("S3") && (protocol==Protocol.SomoSelf ||
					  						  protocol==Protocol.SomoSelfN ||
											  protocol==Protocol.TatsunoSelfMPP4 ||
					  						  protocol==Protocol.TatsunoSelfMPP6 ||
					  						  protocol==Protocol.GSSelfODT /*||
											  protocol==Protocol.TatsunoSelfHS*/ )) {
								
				// �����߰�(09/09/28)
				int idx = Change.toValue(wm.getConnectNozzleNo()); // nozNo
				if (S3_cnt[idx] % 3 == 0) {
					insertDrvSchDownQue(wm);
					//transmitFlag = true;
					
					S3_cnt[idx]=0;
				}
				S3_cnt[idx]++;
								
				//insertDrvSchDownQue(wm);
				//transmitFlag = true;
			} 
			// �����Ϸ� �ڷ�����
			else if (command.equals("S4") && (protocol==Protocol.SomoSelf ||
					  						  protocol==Protocol.SomoSelfN ||
											  protocol==Protocol.TatsunoSelfMPP4 ||
					  						  protocol==Protocol.TatsunoSelfMPP6 ||
					  						  protocol==Protocol.GSSelfODT ||
											  protocol==Protocol.TatsunoSelfHS)) {
				
				// �����߰�(09/09/28)
				S3_cnt[Change.toValue(wm.getConnectNozzleNo())]=0; // nozNo
				
				insertDrvSchDownQue(wm); // send to ODT
				
				/*
				// �پ��� ���� ������ ��� ����(QL) temp -> for test
				Sleep.sleep(3000);
				QL_WorkingMessage QL_wm = new QL_WorkingMessage();
				QL_wm.setNozzleNo(wm.getNozzleNo());
				QL_wm.setMode("1");
				QL_wm.setConnectNozzleNo(wm.getConnectNozzleNo());
				PumpMessageFormat prtForm = new PumpMessageFormat();
				QL_wm.setContent(new String (prtForm.getData()));
				insertDrvSchDownQue(QL_wm);
				*/
				
				istransmitLog=true;
			}
		}
		else if (fromDevType == 5) { //#### ����ODT ���� ����������� ���� �߰� ####//

			String ODTNo = wm.getNozzleNo();
			String tarNozNo = wm.getTargetNozzleNo();
			protocol = Change.toValue(getDeviceProtocol(ODTNo));
			
			//---- ��ȭ������ ODT ȯ�漳�� ----//
			if (protocol==Protocol.PrimeSelfODT) {
				
				if (wm.getCommand().equals("P5_1")) {
					
					P5_1_WorkingMessage P5_1_wm = (P5_1_WorkingMessage) wm;
					
					if (!tarNozNo.equals("")) { 
						
						P5_1_wm.setNozzleNo(tarNozNo);
						insertDrvSchDownQue(P5_1_wm);
	
						LogUtility.getPumpALogger().info("��ȭ���� P5_1 �߰� to noz=" + P5_1_wm.getNozzleNo() + 
								//" tarNoz=" + tarNozNo +
								" protocol=" + protocol + " command=" + P5_1_wm.getCommand() +
								" messageId=" + P5_1_wm.getMessageID());
					}
				}
				else if (wm.getCommand().equals("P1")) {
					
					P1_WorkingMessage P1_wm = (P1_WorkingMessage) wm;
					
					if (!tarNozNo.equals("")) {

						Vector<String> odtIDVec = new Vector<String>();
						odtIDVec.add("00");
						P1_wm.setOdtId(odtIDVec);
						
						P1_wm.setNozzleNo(tarNozNo);
						P1_wm.setConnectNozzleNo("00");
						insertDrvSchDownQue(P1_wm);
	
						LogUtility.getPumpALogger().info("��ȭ���� P1 �߰� to noz=" + P1_wm.getNozzleNo() + 
								//" tarNoz=" + tarNozNo +
								" protocol=" + protocol + " command=" + P1_wm.getCommand() +
								" messageId=" + P1_wm.getMessageID());
					}
				}
				else if (wm.getCommand().equals("P2")) {
					
					P2_WorkingMessage P2_wm = (P2_WorkingMessage) wm;
					
					if (!tarNozNo.equals("")) {

						Vector<String> odtIDVec = new Vector<String>();
						odtIDVec.add("00");
						P2_wm.setOdtId(odtIDVec);
						
						P2_wm.setNozzleNo(tarNozNo);
						P2_wm.setConnectNozzleNo("00");
						insertDrvSchDownQue(P2_wm);
	
						LogUtility.getPumpALogger().info("��ȭ���� P2 �߰� to noz=" + P2_wm.getNozzleNo() + 
								//" tarNoz=" + tarNozNo +
								" protocol=" + protocol + " command=" + P2_wm.getCommand() +
								" messageId=" + P2_wm.getMessageID());
					}
				}
				else if (wm.getCommand().equals("HC")) {

					HC_WorkingMessage HC_wm = (HC_WorkingMessage) wm;
					
					if (HC_wm.isPassThrough()==false) {
						HC_wm.setNozzleNo(tarNozNo);
						HC_wm.setConnectNozzleNo(ODTNo); // ODT
						insertDrvSchDownQue(HC_wm);
						
						LogUtility.getPumpALogger().info("��ȭ���� HC �߰� from="+ODTNo+" to="+tarNozNo);
						
						istransmitLog = true;
					}
				}
				else if (wm.getCommand().equals("HD")) {

					HD_WorkingMessage HD_wm = (HD_WorkingMessage) wm;

					if (HD_wm.isPassThrough()==false) {
						HD_wm.setNozzleNo(tarNozNo);
						HD_wm.setConnectNozzleNo(ODTNo); // ODT
						insertDrvSchDownQue(HD_wm);
						
						LogUtility.getPumpALogger().info("��ȭ���� HD �߰� from="+ODTNo+" to="+tarNozNo);
						
						istransmitLog = true;
					}
				}
			}

			//---- �Ҹ���/�پ��뼿�� ----//
			if (command.equals("PB") && (protocol==Protocol.SomoSelf || // �����⿡ �����㰡
					  					 protocol==Protocol.SomoSelfN ||
					  					 protocol==Protocol.TatsunoSelfMPP4 ||
					  					 protocol==Protocol.TatsunoSelfMPP6 ||
					  					 protocol==Protocol.GSSelfODT ||
										 protocol==Protocol.TatsunoSelfHS)) {
									
				wm.setNozzleNo(tarNozNo);
				wm.setConnectNozzleNo(ODTNo); // ODT
				insertDrvSchDownQue(wm);
					
				LogUtility.getPumpALogger().info("############ PB ���� �߰� from="+ODTNo+" to="+tarNozNo);
				wm.print("");
					
				istransmitLog = true;
			}
			else if (command.equals("F0") && (protocol==Protocol.SomoSelf || // �������� ����(�Ҹ��� ����)
 					 						  protocol==Protocol.SomoSelfN)) {

				wm.setNozzleNo(tarNozNo);
				wm.setConnectNozzleNo(ODTNo); // ODT
				insertDrvSchDownQue(wm);
				
				LogUtility.getPumpALogger().info("############ F0 ���� �߰� from="+ODTNo+" to="+tarNozNo);
				
				istransmitLog = true;
			}
			else if (command.equals("PA") && (protocol==Protocol.SomoSelf || // �������/����
					  						  protocol==Protocol.SomoSelfN /* ||
					  						  protocol==Protocol.TatsunoSelfMPP4 ||
					  						  protocol==Protocol.TatsunoSelfMPP6 */)) {

				wm.setNozzleNo(tarNozNo);
				wm.setConnectNozzleNo(ODTNo); // ODT
				insertDrvSchDownQue(wm);
				
				LogUtility.getPumpALogger().info("############ PA ���� �߰� from="+ODTNo+" to="+tarNozNo);
				
				istransmitLog = true;
			}
			else if (command.equals("P3_1") && (protocol==Protocol.TatsunoSelfHS )) { // �ܰ�����

				wm.setNozzleNo(tarNozNo);
				wm.setConnectNozzleNo(ODTNo); // ODT
				insertDrvSchDownQue(wm);
				
				LogUtility.getPumpALogger().info("############ P3_1 ���� �߰� from="+ODTNo+" to="+tarNozNo);
				
				istransmitLog = true;
			}
			
			/*
			else if (command.equals("HE")) { // �پ��뼿�� ī����� ���ο�û: Temp -> For test
				// ���ΰ�� ����(QM) �߻�
				Sleep.sleep(5000);
				QM_WoringMessage sndWm = new QM_WoringMessage();
				sndWm.setNozzleNo(wm.getNozzleNo());
				sndWm.setConnectNozzleNo(wm.getConnectNozzleNo());
				sndWm.setMode("1");
				insertDrvSchDownQue(sndWm);

				transmitFlag = true;
			}
			*/
				
		}
		
		if (istransmitLog == true) {
			//LogUtility.getPumpALogger().debug("\n<<=>> Exchanged between self device : " +
			LogUtility.getPumpALogger().debug("Exchanged between self device : " +
					orgNozNo + "->" + wm.getNozzleNo()	+ "(" + wm.getCommand() + ")");
		}
	}

	/**
	 * @param wm
	 */
	public void setDeviceEnv(WorkingMessage wm) throws Exception { // ����̽� ������ ȯ�漳��

		if (wm.getCommand().equals("M1")) {

			M1_WorkingMessage M1_wm = (M1_WorkingMessage) wm;
			Enumeration dBoxEnum = M1_wm.getDBoxInfo().elements(); // DBox List
			
			LogUtility.getPumpALogger().info("<<<<< ����̽� ������ ȯ�� ����ó�� >>>>>");
			LogUtility.getPumpALogger().info("Time : " + Time.currentTime());
			LogUtility.getPumpALogger().info("D-Box Port count : " + M1_wm.getDBoxInfo().size() + "\n");
			
			while (dBoxEnum.hasMoreElements()) {

				M1_DBoxInfo m1_dBoxInfo = (M1_DBoxInfo) dBoxEnum.nextElement();
				Enumeration devEnum = m1_dBoxInfo.getNozzleInfo().elements(); // Nozzle List
				Hashtable<String, DevInfo> devTbl = new Hashtable<String, DevInfo>();
				
				LogUtility.getPumpALogger().info("[D-Box Port : " + m1_dBoxInfo.getDBoxPortNo() + "]\n");

				while (devEnum.hasMoreElements()) {

					M1_NozzleInfo m1_nozInfo = (M1_NozzleInfo) devEnum.nextElement();
					DevInfo devInfo = new DevInfo();

					devInfo.devNo = m1_nozInfo.getNozzleNo();
					devInfo.devType = m1_nozInfo.getNozzleType();
					devInfo.protocol = m1_nozInfo.getNozProtocol();
					devInfo.baudRate = m1_nozInfo.getBaudRate();
					devInfo.ipAddress = m1_nozInfo.getIpAddress(); // PI2-�ڵ�ȭ, 2015-11-24 �߰� 
					devInfo.ROMVersion = m1_nozInfo.getRomVersion();
					devInfo.connectDevNo = m1_nozInfo.getSelfOdtNo();

					devTbl.put(devInfo.devNo, devInfo);
					devAllTbl.put(devInfo.devNo, devInfo);
					
					devProtocolTbl.put(devInfo.devNo, devInfo.protocol);
					deviceTypeTbl.put(devInfo.devNo, devInfo.devType);

					// ���� ����� DBoxPortNo�� ���ϱ� ����
					connectDBoxPortNoTbl.put(m1_nozInfo.getNozzleNo(), m1_dBoxInfo.getDBoxPortNo());

					// ���� ������("02")�� ����ODT("05") �� ����ODT ����
					if (devInfo.devType.equals("02") || devInfo.devType.equals("05")) {

						selfDevNoVec.add(devInfo.devNo);

						if (devInfo.devType.equals("02")) {
							connectODTNoTbl.put(devInfo.devNo, devInfo.connectDevNo);
						}
					}
					
					LogUtility.getPumpALogger().info(" deviceNo    =" + devInfo.devNo);
					LogUtility.getPumpALogger().info(" deviceType  =" + devInfo.devType);
					LogUtility.getPumpALogger().info(" protocol    =" + devInfo.protocol);
					LogUtility.getPumpALogger().info(" baudRate    =" + devInfo.baudRate);
					LogUtility.getPumpALogger().info(" ipAddress   =" + devInfo.ipAddress);
					LogUtility.getPumpALogger().info(" ROMVersion  =" + devInfo.ROMVersion);
					LogUtility.getPumpALogger().info(" connectDevNo=" + devInfo.connectDevNo + "\n");
				}

				dBoxPortTbl.put(m1_dBoxInfo.getDBoxPortNo(), devTbl);
			}
		}
	}

	/**
	 * 
	 */
	public void setDriverScheduler() {

		Thread drvThread;
		String commPortId;

		try {
			Enumeration<String> portNoEnum = dBoxPortTbl.keys(); // DBox Port No List

			while (portNoEnum.hasMoreElements()) {
	
				String szPortNo = portNoEnum.nextElement();
				Hashtable<String, DevInfo> devTbl = new Hashtable<String, DevInfo>();
	
				devTbl = dBoxPortTbl.get(szPortNo);
	
				String sc_os = PropertyManager.getSingleton().getProperty(PropertyManager.KH_OS, PropertyManager.KH_OS_DEFAULT) ;
				
				if (IConstant.KH_OS_WIN.equalsIgnoreCase(sc_os)) {
					commPortId = Change.toString("COM%d", Change.toValue(szPortNo));
				} else {
					commPortId = Change.toString("/dev/ttyS%d", Change.toValue(szPortNo) - 1);
					//commPortId = Change.toString("/dev/ttyUSB%d", Change.toValue(szPortNo) - 1);
				}
				
				DriverScheduler drvSch = new DriverScheduler(commPortId, devTbl, tatsunoMPPNozVec);
				drvSch.dBoxPortNo = Change.toValue(szPortNo);
				drvSchVec.add(drvSch);
	
				drvThread = new Thread(drvSch);
				drvThread.setName("PumpAdaptor_setDriverScheduler");
				drvThread.start();

				Sleep.sleep(50); // ����Ʈ������ PJT �߰�
			}
			
		} catch (Exception e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	
	}

	/**
	 * 
	 */
	public void startDataGatherer() {

		Thread gatherThread;
		DataGatherer gatherer = new DataGatherer();

		try {
			gatherer.init(this, drvSchVec);
			gatherThread = new Thread(gatherer);
			gatherThread.setName("PumpAdaptor_startDataGatherer");
			gatherThread.start();
		}
		catch (Exception e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	}

}
