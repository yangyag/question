package com.gsc.kixxhub.device.pumpa;

import java.util.Vector;

import com.gsc.kixxhub.common.data.pump.M1_DBoxInfo;
import com.gsc.kixxhub.common.data.pump.M1_NozzleInfo;
import com.gsc.kixxhub.common.data.pump.M1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P3_NozzleInfo;
import com.gsc.kixxhub.common.data.pump.P3_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.device.pumpa.common.Change;
import com.gsc.kixxhub.device.pumpa.common.Protocol;
import com.gsc.kixxhub.device.pumpa.common.Sleep;
import com.gsc.kixxhub.device.pumpa.service.listener.AdaptorListener;

public class AdaptorWrapperImp implements AdaptorListener {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//LogUtility.getPumpALogger().debug("AdaptorWrapperImp Class invoked");
		
		AdaptorListener 	listener = new AdaptorWrapperImp();
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
	
		
		//======== DBox Port 정의 ========//
		dBoxPortInfo = new M1_DBoxInfo();
		dBoxPortInfo.setDBoxPortNo("06");
		// 설치 Device 정의
		nozList = new Vector<M1_NozzleInfo>();

		nozInfo = new M1_NozzleInfo();
		nozInfo.setNozzleNo("07");
		nozInfo.setNozzleType("01"); // 01=일반주유기, 02=셀프주유기
		nozInfo.setNozProtocol(Change.toString("%02d", Protocol.SK));
		nozInfo.setBaudRate("9600");
		nozInfo.setRomVersion("000");
		nozInfo.setSelfOdtNo("00"); // 연결ODT
		nozList.add(nozInfo);
	
		nozInfo = new M1_NozzleInfo();
		nozInfo.setNozzleNo("08");
		nozInfo.setNozzleType("01"); // 01=일반주유기, 02=셀프주유기
		nozInfo.setNozProtocol(Change.toString("%02d", Protocol.SK));
		nozInfo.setBaudRate("9600");
		nozInfo.setRomVersion("000");
		nozInfo.setSelfOdtNo("00"); // 연결ODT
		nozList.add(nozInfo);

//		nozInfo = new M1_NozzleInfo();
//		nozInfo.setNozzleNo("05");
//		nozInfo.setNozzleType("01"); // 01=일반주유기, 02=셀프주유기
//		nozInfo.setNozProtocol(Change.toString("%02d", Protocol.WooJoo));
//		nozInfo.setBaudRate("9600");
//		nozInfo.setRomVersion("000");
//		nozInfo.setSelfOdtNo("00"); // 연결ODT
//		nozList.add(nozInfo);

		dBoxPortInfo.setNozzleInfo(nozList);
		dBoxPortList.add(dBoxPortInfo);
		//===== End of set DBox Port =====//

/*		
		//======== DBox Port 정의 ========//
		dBoxPortInfo = new M1_DBoxInfo();
		dBoxPortInfo.setDBoxPortNo("05");
		// 설치 Device 정의
		nozList = new Vector<M1_NozzleInfo>();

		nozInfo = new M1_NozzleInfo();
		nozInfo.setNozzleNo("03");
		nozInfo.setNozzleType("01"); // 01=일반주유기, 02=셀프주유기
		nozInfo.setNozProtocol(Change.toString("%02d", Protocol.WooJoo));
		nozInfo.setBaudRate("9600");
		nozInfo.setRomVersion("051");
		nozInfo.setSelfOdtNo("00"); // 연결ODT
		nozList.add(nozInfo);
		
		dBoxPortInfo.setNozzleInfo(nozList);
		dBoxPortList.add(dBoxPortInfo);
		//===== End of set DBox Port =====//


		//======== DBox Port 정의 ========//
		dBoxPortInfo = new M1_DBoxInfo();
		dBoxPortInfo.setDBoxPortNo("07");
		// 설치 Device 정의
		nozList = new Vector<M1_NozzleInfo>();

		nozInfo = new M1_NozzleInfo();
		nozInfo.setNozzleNo("07");
		nozInfo.setNozzleType("01"); // 01=일반주유기, 02=셀프주유기
		nozInfo.setNozProtocol(Change.toString("%02d", Protocol.WooJoo));
		nozInfo.setBaudRate("9600");
		nozInfo.setRomVersion("101");
		nozInfo.setSelfOdtNo("00"); // 연결ODT
		nozList.add(nozInfo);
	
		nozInfo = new M1_NozzleInfo();
		nozInfo.setNozzleNo("08");
		nozInfo.setNozzleType("01"); // 01=일반주유기, 02=셀프주유기
		nozInfo.setNozProtocol(Change.toString("%02d", Protocol.WooJoo));
		nozInfo.setBaudRate("9600");
		nozInfo.setRomVersion("101");
		nozInfo.setSelfOdtNo("00"); // 연결ODT
		nozList.add(nozInfo);

		dBoxPortInfo.setNozzleInfo(nozList);
		dBoxPortList.add(dBoxPortInfo);
		//===== End of set DBox Port =====//


		//======== DBox Port 정의 ========//
		dBoxPortInfo = new M1_DBoxInfo();
		dBoxPortInfo.setDBoxPortNo("08");
		// 설치 Device 정의
		nozList = new Vector<M1_NozzleInfo>();

		nozInfo = new M1_NozzleInfo();
		nozInfo.setNozzleNo("10");
		nozInfo.setNozzleType("01"); // 01=일반주유기, 02=셀프주유기
		nozInfo.setNozProtocol(Change.toString("%02d", Protocol.TatsunoN));
		//nozInfo.setNozProtocol(Change.toString("%02d", Protocol.TokicoN));
		nozInfo.setBaudRate("19200");
		nozInfo.setRomVersion("000");
		nozInfo.setSelfOdtNo("00"); // 연결ODT
		nozList.add(nozInfo);
		
		nozInfo = new M1_NozzleInfo();
		nozInfo.setNozzleNo("11");
		nozInfo.setNozzleType("01"); // 01=일반주유기, 02=셀프주유기
		nozInfo.setNozProtocol(Change.toString("%02d", Protocol.TatsunoN));
		//nozInfo.setNozProtocol(Change.toString("%02d", Protocol.TokicoN));
		nozInfo.setBaudRate("19200");
		nozInfo.setRomVersion("000");
		nozInfo.setSelfOdtNo("00"); // 연결ODT
		nozList.add(nozInfo);
		
		dBoxPortInfo.setNozzleInfo(nozList);
		dBoxPortList.add(dBoxPortInfo);
		//===== End of set DBox Port =====//
*/
/*
		//======== DBox Port 정의 ========//
		dBoxPortInfo = new M1_DBoxInfo();
		dBoxPortInfo.setDBoxPortNo("08");
		// 설치 Device 정의
		nozList = new Vector<M1_NozzleInfo>();

		nozInfo = new M1_NozzleInfo();
		nozInfo.setNozzleNo("11");
		nozInfo.setNozzleType("01"); // 01=일반주유기, 02=셀프주유기
		nozInfo.setNozProtocol(Change.toString("%02d", Protocol.WooJoo));
		nozInfo.setBaudRate("9600");
		nozInfo.setRomVersion("102"); // 동화프라임
		nozInfo.setSelfOdtNo("00"); // 연결ODT
		nozList.add(nozInfo);

		nozInfo = new M1_NozzleInfo();
		nozInfo.setNozzleNo("12");
		nozInfo.setNozzleType("01"); // 01=일반주유기, 02=셀프주유기
		nozInfo.setNozProtocol(Change.toString("%02d", Protocol.WooJoo));
		nozInfo.setBaudRate("9600");
		nozInfo.setRomVersion("102"); // 동화프라임
		nozInfo.setSelfOdtNo("00"); // 연결ODT
		nozList.add(nozInfo);

		dBoxPortInfo.setNozzleInfo(nozList);
		dBoxPortList.add(dBoxPortInfo);
		//===== End of set DBox Port =====//
*/
	
	
		String SelfODTNo="22";
	/*	
		//======== DBox Port 정의 ========//
		dBoxPortInfo = new M1_DBoxInfo();
		dBoxPortInfo.setDBoxPortNo("06");
		// 설치 Device 정의
		nozList = new Vector<M1_NozzleInfo>();

		nozInfo = new M1_NozzleInfo();
		nozInfo.setNozzleNo("12");
		nozInfo.setNozzleType("02"); // 01=일반주유기, 02=셀프주유기
		nozInfo.setNozProtocol(Change.toString("%02d", Protocol.TatsunoMPP4)); 
		nozInfo.setBaudRate("19200");
		nozInfo.setRomVersion("000");
		nozInfo.setSelfOdtNo(SelfODTNo); // 연결ODT
		nozList.add(nozInfo);
		
		nozInfo = new M1_NozzleInfo();
		nozInfo.setNozzleNo("13");
		nozInfo.setNozzleType("02"); // 01=일반주유기, 02=셀프주유기
		nozInfo.setNozProtocol(Change.toString("%02d", Protocol.TatsunoMPP4)); 
		nozInfo.setBaudRate("19200");
		nozInfo.setRomVersion("000");
		nozInfo.setSelfOdtNo(SelfODTNo); // 연결ODT
		nozList.add(nozInfo);
		
		//nozInfo = new M1_NozzleInfo();
		//nozInfo.setNozzleNo("14");
		//nozInfo.setNozzleType("02"); // 01=일반주유기, 02=셀프주유기
		//nozInfo.setNozProtocol(Change.toString("%02d", Protocol.TatsunoMPP4)); 
		//nozInfo.setBaudRate("19200");
		//nozInfo.setRomVersion("000");
		//nozInfo.setSelfOdtNo("22"); // 연결ODT
		//nozList.add(nozInfo);
		
		dBoxPortInfo.setNozzleInfo(nozList);
		dBoxPortList.add(dBoxPortInfo);
		//===== End of set DBox Port =====//
*/

					
		// dBoxPortList 를 M1_WorkingMessage 에 추가
		m1_wm.setDBoxInfo(dBoxPortList);
		//####################################################//
		//######### End of set master environment data #######//
		//####################################################//
		
		
		//######### Call adaptor ##########//
		adaptor.init(m1_wm); // 초기화 호출
		adaptor.setListener(listener);
		//###### End of Call adaptor ######//
		
		Sleep.sleep(2000);
		//######### Start of send WorkingMessage #######//

		// 주유기 환경설정(노즐단가)
		P3_WorkingMessage P3_wm = new P3_WorkingMessage();	
		Vector<P3_NozzleInfo> nozInfoVec3 = new Vector<P3_NozzleInfo>();

//		P3_NozzleInfo P3_noz1 = new P3_NozzleInfo();
//		P3_noz1.setNozzleNo("01");
//		P3_noz1.setBasePrice("157700");
//		nozInfoVec3.add(P3_noz1);
//
//		P3_NozzleInfo P3_noz2 = new P3_NozzleInfo();
//		P3_noz2.setNozzleNo("02");
//		P3_noz2.setBasePrice("157700");
//		nozInfoVec3.add(P3_noz2);
//
//		P3_NozzleInfo P3_noz3 = new P3_NozzleInfo();
//		P3_noz3.setNozzleNo("03");
//		P3_noz3.setBasePrice("157700");
//		nozInfoVec3.add(P3_noz3);

		P3_NozzleInfo P3_noz4 = new P3_NozzleInfo();
		P3_noz4.setNozzleNo("07");
		P3_noz4.setBasePrice("158800");
		nozInfoVec3.add(P3_noz4);

		P3_NozzleInfo P3_noz5 = new P3_NozzleInfo();
		P3_noz5.setNozzleNo("08");
		P3_noz5.setBasePrice("169900");
		nozInfoVec3.add(P3_noz5);
/*
		P3_NozzleInfo P3_noz6 = new P3_NozzleInfo();
		P3_noz6.setNozzleNo("10");
		P3_noz6.setBasePrice("157700");
		nozInfoVec3.add(P3_noz6);

		P3_NozzleInfo P3_noz7 = new P3_NozzleInfo();
		P3_noz7.setNozzleNo("11");
		P3_noz7.setBasePrice("157700");
		nozInfoVec3.add(P3_noz7);

		P3_NozzleInfo P3_noz8 = new P3_NozzleInfo();
		P3_noz8.setNozzleNo("05");
		P3_noz8.setBasePrice("156600");
		nozInfoVec3.add(P3_noz8);

		P3_NozzleInfo P3_noz9 = new P3_NozzleInfo();
		P3_noz9.setNozzleNo("06");
		P3_noz9.setBasePrice("157700");
		nozInfoVec3.add(P3_noz9);
	
		P3_NozzleInfo P3_noz10 = new P3_NozzleInfo();
		P3_noz10.setNozzleNo("10");
		P3_noz10.setBasePrice("194800");
		nozInfoVec3.add(P3_noz10);
		
		P3_NozzleInfo P3_noz11 = new P3_NozzleInfo();
		P3_noz11.setNozzleNo("11");
		P3_noz11.setBasePrice("193800");
		nozInfoVec3.add(P3_noz11);
		
		P3_NozzleInfo P3_noz12 = new P3_NozzleInfo();
		P3_noz12.setNozzleNo("12");
		P3_noz12.setBasePrice("194800");
		nozInfoVec3.add(P3_noz12);

		P3_NozzleInfo P3_noz13 = new P3_NozzleInfo();
		P3_noz13.setNozzleNo("13");
		P3_noz13.setBasePrice("193800");
		nozInfoVec3.add(P3_noz13);
*/
		P3_wm.setNozzleInfo(nozInfoVec3);
		adaptor.sendModuleMsg(P3_wm);
		
		
		
		
		
		
		/*
		Sleep.sleep(20000);

		P3_WorkingMessage P3_wm2 = new P3_WorkingMessage();	
		Vector<P3_NozzleInfo> nozInfoVec32 = new Vector<P3_NozzleInfo>();
		
		P3_NozzleInfo P3_noz11 = new P3_NozzleInfo();
		P3_noz11.setNozzleNo("08");
		P3_noz11.setBasePrice("188800");
		nozInfoVec32.add(P3_noz11);

		P3_wm2.setNozzleInfo(nozInfoVec32);
		adaptor.sendModuleMsg(P3_wm2);
		*/
		
		
		
		

		//---- End of 환경설정처리----//

		String targetNoz="01";
		String targetODT="22";
/*
		// 주유완료
		S4_WorkingMessage S4_wm = new S4_WorkingMessage();
		S4_wm.setNozzleNo(targetODT); // Self nozzle
		S4_wm.setConnectNozzleNo(targetNoz); // Self nozzle
		S4_wm.setFlag("1");
		S4_wm.setStatusFlag("5"); // 주유완료
		S4_wm.setLiter("0000000");
		S4_wm.setBasePrice("    ");
		S4_wm.setPrice("000000");
		adaptor.sendModuleMsg(S4_wm);
*/
/*
		Sleep.sleep(10000);
		// 노즐제어(=주유금지) 
		PA_WorkingMessage PA_wm = new PA_WorkingMessage();
		PA_wm.setNozzleNo("01"); // Self nozzle
		PA_wm.setConnectNozzleNo("00"); // Self nozzle
		PA_wm.setNozzleState("0"); // 0=금지, 1=허가
		adaptor.sendModuleMsg(PA_wm);
*/

//		Sleep.sleep(20000);
//		// 노즐제어(=주유금지 해제)
//		PA_WorkingMessage PA_wm2 = new PA_WorkingMessage();
//		PA_wm2.setNozzleNo("08"); // Self nozzle
//		PA_wm2.setConnectNozzleNo("00"); // Self nozzle
//		PA_wm2.setNozzleState("0"); // 0=금지, 1=허가
//		adaptor.sendModuleMsg(PA_wm2);	
//
//		Sleep.sleep(20000);
//		// 노즐제어(=주유금지)
//		PA_WorkingMessage PA_wm3 = new PA_WorkingMessage();
//		PA_wm3.setNozzleNo("08"); // Self nozzle
//		PA_wm3.setConnectNozzleNo("00"); // Self nozzle
//		PA_wm3.setNozzleState("1"); // 0=금지, 1=허가
//		adaptor.sendModuleMsg(PA_wm3);


//		Sleep.sleep(10000);
//		// 정액/정량 설정 (셀프: 선결제지시)
//		PB_WorkingMessage PB_wm = new PB_WorkingMessage();
//		PB_wm.setNozzleNo("07"); // Self nozzle
//		PB_wm.setConnectNozzleNo("00"); // Self nozzle
//		PB_wm.setCommandSet("1"); // 0=정액설정, 1=정량설정
//		PB_wm.setLiter("0020000");
//		PB_wm.setBasePrice("195300");
//		PB_wm.setPrice("00000000");
//		adaptor.sendModuleMsg(PB_wm);

/*
		Sleep.sleep(5000);
		// 토털게이지 요청
		P8_WorkingMessage P8_wm = new P8_WorkingMessage();
		P8_wm.setNozzleNo(targetNoz);
		adaptor.sendModuleMsg(P8_wm);
*/
/*
		// 프리셋 자료 요청
		QF_WorkingMessage QF_wm = new QF_WorkingMessage();
		QF_wm.setNozzleNo(targetNoz);
		adaptor.sendModuleMsg(QF_wm);
*/
/*
		// 파라미터 설정
		P7_WorkingMessage P7_wm = new P7_WorkingMessage();
		P7_wm.setNozzleNo("01");
		P7_wm.setReadBuffInterval("0000");
		P7_wm.setReadStartInterval("0000");
		P7_wm.setWriteStartInterval("0000");
		P7_wm.setLineErrorCount("00");
		P7_wm.setLineErrorSkipCount("8000"); // 9000~9003은 dispLevel 설정
		adaptor.sendModuleMsg(P7_wm);
*/
/* 
		// 주유기 상태정보 요청 (사용않음, 구현은 한다.)
		PE_WorkingMessage PE_wm = new PE_WorkingMessage();
		PE_wm.setNozzleNo(targetNoz);
		adaptor.sendModuleMsg(PE_wm);
		//######### End of send workingMessage #######//
*/
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
