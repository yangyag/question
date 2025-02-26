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
		M1_WorkingMessage m1_wm = new M1_WorkingMessage(); // Master 전문
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
			LogUtility.getPumpALogger().debug("설정된 노즐정보가 없습니다.");
			return;
		} 
		
		Vector<String> portNoVec = new Vector<String>();
		
		for(int i=0; i<propNozList.size(); i++) {
			
			PropNozInfo propNoz = propNozList.get(i);
			/*
			LogUtility.getPumpALogger().debug("노즐번호 : " + propNoz.getNozNo());
			LogUtility.getPumpALogger().debug("포트번호 : " + propNoz.getPortNo());
			LogUtility.getPumpALogger().debug("프로토콜 : " + propNoz.getProtocol());
			LogUtility.getPumpALogger().debug("통신속도 : " + propNoz.getBaudRate());
			LogUtility.getPumpALogger().debug("디바이스 종류 :" + propNoz.getDeviceType());
			LogUtility.getPumpALogger().debug("연결 ODT : " + propNoz.getOdtNo());
			LogUtility.getPumpALogger().debug("유종 : " + propNoz.getOil());
			LogUtility.getPumpALogger().debug("단가 : " + propNoz.getBasePrice());
			LogUtility.getPumpALogger().debug("ROM Version : " + propNoz.getRomVersion());
			LogUtility.getPumpALogger().debug("수신버퍼대기시간 : " + propNoz.getReadBufferInterval());
			LogUtility.getPumpALogger().debug("수신시작대기시간 : " + propNoz.getReadStartInterval());
			LogUtility.getPumpALogger().debug("송신시작대기시간 : " + propNoz.getWriteStartInterval());
			LogUtility.getPumpALogger().debug("회선불량기준횟수 : " + propNoz.getLineErrorCount());
			LogUtility.getPumpALogger().debug("회선불량재확인횟수 : " + propNoz.getLineErrorSkipCount());
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
					nozInfo.setSelfOdtNo(propNozInfo.getOdtNo()); // 연결ODT
					nozList.add(nozInfo);
				}
			}
			dBoxPortInfo.setNozzleInfo(nozList);
			dBoxPortList.add(dBoxPortInfo);
		}
			

		// dBoxPortList 를 M1_WorkingMessage 에 추가
		m1_wm.setDBoxInfo(dBoxPortList);
		//####################################################//
		//######### End of set master environment data #######//
		//####################################################//
		
		
		//######### Call adaptor ##########//
		adaptor.init(m1_wm); // 초기화 호출
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
			//---- Time 파라미터 설정
			P7_wm.setNozzleNo(propNoz.getNozNo());
			P7_wm.setReadBuffInterval(propNoz.getReadBufferInterval());
			P7_wm.setReadStartInterval(propNoz.getReadStartInterval());
			P7_wm.setWriteStartInterval(propNoz.getWriteStartInterval());
			P7_wm.setLineErrorCount(propNoz.getLineErrorCount());
			P7_wm.setLineErrorSkipCount(propNoz.getLineErrorSkipCount());
			adaptor.sendModuleMsg(P7_wm);

			if (propNoz.getDeviceType().equals("01")){
				//---- 단가설정
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
				P5_odt.setMode("0"); // 초기화 모드
				odtInfoVec.add(P5_odt);
				
				isExistOfODT=true;
			} 
		} 
		
		//--- ODT 정보 처리 ---//
		if (isExistOfODT==true) { // Self ODT 가 있으면
		
			// 노즐정보 처리(단가설정)
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
						P5_noz.setGoodsCode(propNoz.getOil()); // 유종
						
						String oilName="";
						if (propNoz.getOil().equals("0660"))
							oilName = "Kixx";
						else if (propNoz.getOil().equals("0610"))
							oilName = "Kixx prime";
						else if (propNoz.getOil().equals("1206"))
							oilName = "Diesel";
						P5_noz.setGoodsType(oilName); // 최대 14 자리
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
		
		// 프리셋 설정처리(주유허가)
		Sleep.sleep(5000);
		for(int i=0; i<propNozList.size(); i++) {
			
			PropNozInfo propNoz = propNozList.get(i);
			
			if (propNoz.isSetLiter()==true) {
				PB_WorkingMessage PB_wm = new PB_WorkingMessage();
				PB_wm.setNozzleNo(propNoz.getNozNo()); // Self nozzle
				PB_wm.setConnectNozzleNo("00"); // Self nozzle
				PB_wm.setCommandSet("0"); // 0=정액설정, 1=정량설정
				PB_wm.setLiter("0000000");
				PB_wm.setBasePrice(propNoz.getBasePrice()+"00");
				PB_wm.setPrice(propNoz.getPresetValue());
				adaptor.sendModuleMsg(PB_wm);
			}
			
			if (propNoz.isSetPrice()==true) {
				PB_WorkingMessage PB_wm = new PB_WorkingMessage();
				PB_wm.setNozzleNo(propNoz.getNozNo()); // Self nozzle
				PB_wm.setConnectNozzleNo("00"); // Self nozzle
				PB_wm.setCommandSet("1"); // 0=정액설정, 1=정량설정
				PB_wm.setLiter(propNoz.getPresetValue() + "000");
				PB_wm.setBasePrice(propNoz.getBasePrice()+"00");
				PB_wm.setPrice("00000000");
				adaptor.sendModuleMsg(PB_wm);
			}
		}
		
		//--- 비상정지 처리 ---//
		int	nozLock=0;  // 0=unLocking, 1=locking

		if (nozLock==1) {
			for(int i=0; i<propNozList.size(); i++) {
				PropNozInfo propNoz = propNozList.get(i);
				PA_WorkingMessage PA_wm = new PA_WorkingMessage();
				PA_wm.setNozzleNo(propNoz.getNozNo()); // Self nozzle
				PA_wm.setConnectNozzleNo("00"); // Self nozzle
				PA_wm.setNozzleState("0"); // 0=금지, 1=허가
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
