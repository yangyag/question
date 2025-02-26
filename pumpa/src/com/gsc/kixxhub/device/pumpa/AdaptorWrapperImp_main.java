package com.gsc.kixxhub.device.pumpa;

import java.util.ArrayList;
import java.util.Vector;

import com.gsc.kixxhub.common.data.pump.M1_DBoxInfo;
import com.gsc.kixxhub.common.data.pump.M1_NozzleInfo;
import com.gsc.kixxhub.common.data.pump.M1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P3_NozzleInfo;
import com.gsc.kixxhub.common.data.pump.P3_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P5_NozzleInfo;
import com.gsc.kixxhub.common.data.pump.P5_OdtInfo;
import com.gsc.kixxhub.common.data.pump.P5_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P7_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.Change;
import com.gsc.kixxhub.device.pumpa.common.Sleep;
import com.gsc.kixxhub.device.pumpa.service.listener.AdaptorListener;

public class AdaptorWrapperImp_main implements AdaptorListener {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//LogUtility.getPumpALogger().debug("AdaptorWrapperImp Class invoked");
		
		AdaptorListener listener = new AdaptorWrapperImp_main();
		AdaptorServiceImp 	adaptor = new AdaptorServiceImp();
		AdaptorServiceImp.dispMode=2;

		//#######################################################//
		//######### Start of set master environment data ########//
		//#######################################################//
		M1_WorkingMessage m1_wm = new M1_WorkingMessage(); // Master ����
		//m1_wm.setNozzleNo("");
		M1_DBoxInfo dBoxPortInfo;
		M1_NozzleInfo nozInfo;
		Vector<M1_DBoxInfo> dBoxPortList;
		Vector<M1_NozzleInfo> nozList;

		dBoxPortList = new Vector<M1_DBoxInfo>();
		nozList = new Vector<M1_NozzleInfo>();
		
		PropertiesManager pm = new PropertiesManager();
		
		pm.loadProp(); // load properties
		ArrayList<PropNozInfo> propNozList = pm.getNozzleInfos(); // get nozzles information
		
		if(propNozList == null) {
			LogUtility.getPumpALogger().debug("������ ���������� �����ϴ�.");
			return;
		} 
		
		Vector<String> portNoVec = new Vector<String>();
		
		for(int i=0; i<propNozList.size(); i++) {
			
			PropNozInfo propNoz = propNozList.get(i);
			/*
			LogUtility.getPumpALogger().debug("�����ȣ : " + propNoz.getNozNo());
			LogUtility.getPumpALogger().debug("��Ʈ��ȣ : " + propNoz.getPortNo());
			LogUtility.getPumpALogger().debug("�������� : " + propNoz.getProtocol());
			LogUtility.getPumpALogger().debug("��żӵ� : " + propNoz.getBaudRate());
			LogUtility.getPumpALogger().debug("����̽� ���� :" + propNoz.getDeviceType());
			LogUtility.getPumpALogger().debug("���� ODT : " + propNoz.getOdtNo());
			LogUtility.getPumpALogger().debug("���� : " + propNoz.getOil());
			LogUtility.getPumpALogger().debug("�ܰ� : " + propNoz.getBasePrice());
			LogUtility.getPumpALogger().debug("ROM Version : " + propNoz.getRomVersion());
			LogUtility.getPumpALogger().debug("���Ź��۴��ð� : " + propNoz.getReadBufferInterval());
			LogUtility.getPumpALogger().debug("���Ž��۴��ð� : " + propNoz.getReadStartInterval());
			LogUtility.getPumpALogger().debug("�۽Ž��۴��ð� : " + propNoz.getWriteStartInterval());
			LogUtility.getPumpALogger().debug("ȸ���ҷ�����Ƚ�� : " + propNoz.getLineErrorCount());
			LogUtility.getPumpALogger().debug("ȸ���ҷ���Ȯ��Ƚ�� : " + propNoz.getLineErrorSkipCount());
			LogUtility.getPumpALogger().debug("");
			 */
			if (portNoVec.contains(propNoz.getPortNo())==false)
				portNoVec.add(propNoz.getPortNo());
		}
		

		for (int a=0; a<portNoVec.size(); a++) {
			
			String szPort = portNoVec.get(a);
			nozList = new Vector<M1_NozzleInfo>();
			dBoxPortInfo = new M1_DBoxInfo();
			dBoxPortInfo.setDBoxPortNo(szPort);
			
			for (int i=0; i<propNozList.size(); i++) {
			
				PropNozInfo propNozInfo = propNozList.get(i);

				if (propNozInfo.getPortNo().equals(szPort)) {
					
					nozInfo = new M1_NozzleInfo();
					nozInfo.setNozzleNo(propNozInfo.getNozNo());
					nozInfo.setNozzleType(propNozInfo.getDeviceType()); 
					nozInfo.setNozProtocol(propNozInfo.getProtocol());
					nozInfo.setBaudRate(propNozInfo.getBaudRate());
					nozInfo.setRomVersion(propNozInfo.getRomVersion());
					nozInfo.setSelfOdtNo(propNozInfo.getOdtNo()); // ����ODT
					nozList.add(nozInfo);
				}
			}
			dBoxPortInfo.setNozzleInfo(nozList);
			dBoxPortList.add(dBoxPortInfo);
		}
			

		// dBoxPortList �� M1_WorkingMessage �� �߰�
		m1_wm.setDBoxInfo(dBoxPortList);
		//####################################################//
		//######### End of set master environment data #######//
		//####################################################//
		
		
		//######### Call adaptor ##########//
		adaptor.init(m1_wm); // �ʱ�ȭ ȣ��
		adaptor.setListener(listener);
		//###### End of Call adaptor ######//

		
		//######### Start of send WorkingMessage #######//
		//LogUtility.getPumpALogger().debug("P7_wm size====="+propNozList.size());
		boolean isExistOfODT = false;
		P3_WorkingMessage P3_wm = new P3_WorkingMessage();	
		Vector<P3_NozzleInfo> nozInfoVec = new Vector<P3_NozzleInfo>();

		P5_WorkingMessage P5_wm = new P5_WorkingMessage();
		Vector<P5_OdtInfo> odtInfoVec = new Vector<P5_OdtInfo>();
		
		for(int i=0; i<propNozList.size(); i++) {
			
			PropNozInfo propNoz = propNozList.get(i);
			P7_WorkingMessage P7_wm = new P7_WorkingMessage();

			//LogUtility.getPumpALogger().debug("\n"+"  nozNo="+propNoz.getNozNo());
			//LogUtility.getPumpALogger().debug("  ReadBuffInterval="+propNoz.getReadBufferInterval());
			//---- Time �Ķ���� ����
			P7_wm.setNozzleNo(propNoz.getNozNo());
			P7_wm.setReadBuffInterval(propNoz.getReadBufferInterval());
			P7_wm.setReadStartInterval(propNoz.getReadStartInterval());
			P7_wm.setWriteStartInterval(propNoz.getWriteStartInterval());
			P7_wm.setLineErrorCount(propNoz.getLineErrorCount());
			P7_wm.setLineErrorSkipCount(propNoz.getLineErrorSkipCount());
			adaptor.sendModuleMsg(P7_wm);

			if (propNoz.getDeviceType().equals("01")){
				//---- �ܰ�����
				P3_NozzleInfo P3_noz1 = new P3_NozzleInfo();
				P3_noz1.setNozzleNo(propNoz.getNozNo());
				P3_noz1.setBasePrice(propNoz.getBasePrice()+"00");
				nozInfoVec.add(P3_noz1);

				P3_wm.setNozzleInfo(nozInfoVec);
				adaptor.sendModuleMsg(P3_wm);
			}
			else if (propNoz.getDeviceType().equals("05")) {

				P5_OdtInfo P5_odt = new P5_OdtInfo();
				P5_odt.setOdtID(propNoz.getNozNo());
				P5_odt.setMode("0"); // �ʱ�ȭ ���
				odtInfoVec.add(P5_odt);
				
				isExistOfODT=true;
			} 
		} 
		
		//--- ODT ���� ó�� ---//
		if (isExistOfODT==true) { // Self ODT �� ������
		
			// �������� ó��(�ܰ�����)
			for (int i=0; i<odtInfoVec.size(); i++) {
			
				int nozCnt=0;
				P5_OdtInfo P5_odt = odtInfoVec.get(i);
				Vector<P5_NozzleInfo> P5_nozInfoVec = new Vector<P5_NozzleInfo>();
				
				for(int j=0; j<propNozList.size(); j++) {
					
					PropNozInfo propNoz = propNozList.get(j);
					
					if (propNoz.getOdtNo().equals(P5_odt.getOdtID())) {
						
						P5_NozzleInfo P5_noz = new P5_NozzleInfo();
						P5_noz.setNozzleNumber(propNoz.getNozNo());
						P5_noz.setBasePrice(propNoz.getBasePrice()); 
						P5_noz.setGoodsCode(propNoz.getOil()); // ����
						
						String oilName="";
						if (propNoz.getOil().equals("0660"))
							oilName = "Kixx";
						else if (propNoz.getOil().equals("0610"))
							oilName = "Kixx prime";
						else if (propNoz.getOil().equals("1206"))
							oilName = "Diesel";
						P5_noz.setGoodsType(oilName); // �ִ� 14 �ڸ�
						P5_nozInfoVec.add(P5_noz);
	
						P5_odt.setNozzleInfo(P5_nozInfoVec);
						nozCnt++;
					}
				}
				
				P5_odt.setNozzleCount(Change.toString("%d", nozCnt));
			}
			
			P5_wm.setOdtInfo(odtInfoVec);	
			adaptor.sendModuleMsg(P5_wm);
		}
		
		// ������ ����ó��(�����㰡)
		Sleep.sleep(5000);
		for(int i=0; i<propNozList.size(); i++) {
			
			PropNozInfo propNoz = propNozList.get(i);
			
			if (propNoz.isSetLiter()==true) {
				PB_WorkingMessage PB_wm = new PB_WorkingMessage();
				PB_wm.setNozzleNo(propNoz.getNozNo()); // Self nozzle
				PB_wm.setConnectNozzleNo("00"); // Self nozzle
				PB_wm.setCommandSet("0"); // 0=���׼���, 1=��������
				PB_wm.setLiter("0000000");
				PB_wm.setBasePrice(propNoz.getBasePrice()+"00");
				PB_wm.setPrice(propNoz.getPresetValue());
				adaptor.sendModuleMsg(PB_wm);
			}
			
			if (propNoz.isSetPrice()==true) {
				PB_WorkingMessage PB_wm = new PB_WorkingMessage();
				PB_wm.setNozzleNo(propNoz.getNozNo()); // Self nozzle
				PB_wm.setConnectNozzleNo("00"); // Self nozzle
				PB_wm.setCommandSet("1"); // 0=���׼���, 1=��������
				PB_wm.setLiter(propNoz.getPresetValue() + "000");
				PB_wm.setBasePrice(propNoz.getBasePrice()+"00");
				PB_wm.setPrice("00000000");
				adaptor.sendModuleMsg(PB_wm);
			}
		}
		
		//--- ������� ó�� ---//
		int	nozLock=0;  // 0=unLocking, 1=locking

		if (nozLock==1) {
			for(int i=0; i<propNozList.size(); i++) {
				PropNozInfo propNoz = propNozList.get(i);
				PA_WorkingMessage PA_wm = new PA_WorkingMessage();
				PA_wm.setNozzleNo(propNoz.getNozNo()); // Self nozzle
				PA_wm.setConnectNozzleNo("00"); // Self nozzle
				PA_wm.setNozzleState("0"); // 0=����, 1=�㰡
				adaptor.sendModuleMsg(PA_wm);
				//Sleep.sleep(1000);
			}
		}
		
		//######### End of send workingMessage #######//
	}

	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.service.listener.AdaptorListener#sendDeviceMsg(com.gsc.kixxhub.data.pump.WorkingMessage)
	 */
	public boolean sendDeviceMsg(WorkingMessage wm) {

		//LogUtility.getPumpALogger().debug("\n<<<<<<<<<< Up Message(in AdaptorWrapperImp) noz="
						//+ wm.getNozzleNo() + " cmd=" + wm.getCommand());

		return true;
	}
}
