package com.gsc.kixxhub.device.pumpa;

import java.util.Vector;

import com.gsc.kixxhub.common.data.pump.M1_DBoxInfo;
import com.gsc.kixxhub.common.data.pump.M1_NozzleInfo;
import com.gsc.kixxhub.common.data.pump.M1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P5_NozzleInfo;
import com.gsc.kixxhub.common.data.pump.P5_OdtInfo;
import com.gsc.kixxhub.common.data.pump.P5_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.device.pumpa.common.Change;
import com.gsc.kixxhub.device.pumpa.common.Protocol;
import com.gsc.kixxhub.device.pumpa.common.Sleep;
import com.gsc.kixxhub.device.pumpa.service.listener.AdaptorListener;

public class AdaptorWrapperImp_GSSelfODTi implements AdaptorListener {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//LogUtility.getPumpALogger().debug("AdaptorWrapperImp Class invoked");
		
		AdaptorListener listener = new AdaptorWrapperImp_GSSelfODTi();
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
	
		
		//======== DBox Port 정의 (주유기) ========//
		dBoxPortInfo = new M1_DBoxInfo();
		dBoxPortInfo.setDBoxPortNo("05");
		// 설치 Device 정의
		nozList = new Vector<M1_NozzleInfo>();

		String SelfODTNo="51";	// 다쓰노 셀프ODT	
		
		nozInfo = new M1_NozzleInfo();
		nozInfo.setNozzleNo("01");
		nozInfo.setNozzleType("02"); // 01=일반주유기, 02=셀프주유기
		nozInfo.setNozProtocol(Change.toString("%02d", Protocol.GSSelfVNoz)); 
		nozInfo.setBaudRate("");
		nozInfo.setRomVersion("000");
		nozInfo.setSelfOdtNo(SelfODTNo); // 연결ODT
		nozList.add(nozInfo);
		
		nozInfo = new M1_NozzleInfo();
		nozInfo.setNozzleNo("02");
		nozInfo.setNozzleType("02"); // 01=일반주유기, 02=셀프주유기
		nozInfo.setNozProtocol(Change.toString("%02d", Protocol.GSSelfVNoz)); 
		nozInfo.setBaudRate("");
		nozInfo.setRomVersion("000");
		nozInfo.setSelfOdtNo(SelfODTNo); // 연결ODT
		nozList.add(nozInfo);
		
		nozInfo = new M1_NozzleInfo();
		nozInfo.setNozzleNo("03");
		nozInfo.setNozzleType("02"); // 01=일반주유기, 02=셀프주유기
		nozInfo.setNozProtocol(Change.toString("%02d", Protocol.GSSelfVNoz)); 
		nozInfo.setBaudRate("");
		nozInfo.setRomVersion("000");
		nozInfo.setSelfOdtNo(SelfODTNo); // 연결ODT
		nozList.add(nozInfo);
		
		
		SelfODTNo="52";	// 다쓰노 셀프ODT
		
		nozInfo = new M1_NozzleInfo();
		nozInfo.setNozzleNo("04");
		nozInfo.setNozzleType("02"); // 01=일반주유기, 02=셀프주유기
		nozInfo.setNozProtocol(Change.toString("%02d", Protocol.GSSelfVNoz)); 
		nozInfo.setBaudRate("");
		nozInfo.setRomVersion("000");
		nozInfo.setSelfOdtNo(SelfODTNo); // 연결ODT
		nozList.add(nozInfo);
		
		nozInfo = new M1_NozzleInfo();
		nozInfo.setNozzleNo("05");
		nozInfo.setNozzleType("02"); // 01=일반주유기, 02=셀프주유기
		nozInfo.setNozProtocol(Change.toString("%02d", Protocol.GSSelfVNoz)); 
		nozInfo.setBaudRate("");
		nozInfo.setRomVersion("000");
		nozInfo.setSelfOdtNo(SelfODTNo); // 연결ODT
		nozList.add(nozInfo);
		
		nozInfo = new M1_NozzleInfo();
		nozInfo.setNozzleNo("06");
		nozInfo.setNozzleType("02"); // 01=일반주유기, 02=셀프주유기
		nozInfo.setNozProtocol(Change.toString("%02d", Protocol.GSSelfVNoz)); 
		nozInfo.setBaudRate("");
		nozInfo.setRomVersion("000");
		nozInfo.setSelfOdtNo(SelfODTNo); // 연결ODT
		nozList.add(nozInfo);
		
		dBoxPortInfo.setNozzleInfo(nozList);
		dBoxPortList.add(dBoxPortInfo);
		//===== End of set DBox Port =====//


		//======== DBox Port 정의 (ODT) ========//
		dBoxPortInfo = new M1_DBoxInfo();
		dBoxPortInfo.setDBoxPortNo("06");
		// 설치 Device 정의
		nozList = new Vector<M1_NozzleInfo>();

		nozInfo = new M1_NozzleInfo(); // 다쓰노 셀프 ODT
		nozInfo.setNozzleNo("51");
		nozInfo.setNozzleType("05"); // 셀프ODT
		nozInfo.setNozProtocol(Change.toString("%02d", Protocol.GSSelfODTi)); 
		nozInfo.setBaudRate("");
		nozInfo.setRomVersion("000");
		nozInfo.setSelfOdtNo("00"); // 연결 주유기
		nozList.add(nozInfo);
		
		nozInfo = new M1_NozzleInfo(); // 다쓰노 셀프 ODT
		nozInfo.setNozzleNo("52");
		nozInfo.setNozzleType("05"); // 셀프ODT
		nozInfo.setNozProtocol(Change.toString("%02d", Protocol.GSSelfODTi)); 
		nozInfo.setBaudRate("");
		nozInfo.setRomVersion("000");
		nozInfo.setSelfOdtNo("00"); // 연결 주유기
		nozList.add(nozInfo);

		dBoxPortInfo.setNozzleInfo(nozList);
		dBoxPortList.add(dBoxPortInfo);
		//===== End of set DBox Port =====//

		
		/*
		//======== DBox Port 정의 ========//
		dBoxPortInfo = new M1_DBoxInfo();
		dBoxPortInfo.setDBoxPortNo("11");
		// 설치 Device 정의
		nozList = new Vector<M1_NozzleInfo>();

		nozInfo = new M1_NozzleInfo();
		nozInfo.setNozzleNo("05");
		nozInfo.setNozzleType("01"); // 01=일반주유기, 02=셀프주유기
		nozInfo.setNozProtocol(Change.toString("%02d", Protocol.WooJoo)); 
		nozInfo.setBaudRate("4800");
		nozInfo.setRomVersion("000");
		nozInfo.setSelfOdtNo("00"); // 연결ODT
		nozList.add(nozInfo);
		
		nozInfo = new M1_NozzleInfo();
		nozInfo.setNozzleNo("06");
		nozInfo.setNozzleType("01"); // 01=일반주유기, 02=셀프주유기
		nozInfo.setNozProtocol(Change.toString("%02d", Protocol.WooJoo)); 
		nozInfo.setBaudRate("4800");
		nozInfo.setRomVersion("000");
		nozInfo.setSelfOdtNo("00"); // 연결ODT
		nozList.add(nozInfo);
		
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
		
		
		//######### Start of send WorkingMessage #######//
		//--- 다쓰노셀프 환경설정 정보처리(다운로드 필수) ---//
		P5_WorkingMessage P5_wm2 = new P5_WorkingMessage();
		Vector<P5_OdtInfo> odtInfoVec2 = new Vector<P5_OdtInfo>();
		
		//SelfODTNo="22";
		//==== ODT 설정 ====//
		P5_OdtInfo P5_odtWm22 = new P5_OdtInfo();
		P5_odtWm22.setOdtID("51");
		P5_odtWm22.setNozzleCount("3");
		P5_odtWm22.setMode("0"); // 초기화 모드
		Vector<P5_NozzleInfo> nozInfoVec22 = new Vector<P5_NozzleInfo>();
		
		P5_NozzleInfo P5_nozWm22_1 = new P5_NozzleInfo();
		P5_nozWm22_1.setNozzleNumber("01");
		P5_nozWm22_1.setBasePrice("151100"); // 4.2
		P5_nozWm22_1.setGoodsCode("0660"); // 휘발유
		P5_nozWm22_1.setGoodsType("KIXX"); // 최대 14 자리
		nozInfoVec22.add(P5_nozWm22_1);
		
		P5_NozzleInfo P5_nozWm22_2 = new P5_NozzleInfo();
		P5_nozWm22_2.setNozzleNumber("02");
		P5_nozWm22_2.setBasePrice("152200"); // 4.2
		P5_nozWm22_2.setGoodsCode("1206"); // 경유
		P5_nozWm22_2.setGoodsType("DIESEL"); // 최대 14 자리
		nozInfoVec22.add(P5_nozWm22_2);
		
		P5_NozzleInfo P5_nozWm22_3 = new P5_NozzleInfo();
		P5_nozWm22_3.setNozzleNumber("03");
		P5_nozWm22_3.setBasePrice("153300"); // 4.2
		P5_nozWm22_3.setGoodsCode("0610");
		P5_nozWm22_3.setGoodsType("KIXX PRIME"); // 최대 14 자리
		nozInfoVec22.add(P5_nozWm22_3);
		
		P5_odtWm22.setNozzleInfo(nozInfoVec22);
		odtInfoVec2.add(P5_odtWm22);
		//==== End of ODT 설정 ====//
		
		P5_wm2.setOdtInfo(odtInfoVec2);	
		adaptor.sendModuleMsg(P5_wm2);
		//--- End of 다쓰노셀프 환경설정 정보처리 ---//
		
		
		
		P5_WorkingMessage _P5_wm2 = new P5_WorkingMessage();
		Vector<P5_OdtInfo> _odtInfoVec2 = new Vector<P5_OdtInfo>();
		//==== ODT 설정 ====//
		P5_OdtInfo _P5_odtWm22 = new P5_OdtInfo();
		_P5_odtWm22.setOdtID("52");
		_P5_odtWm22.setNozzleCount("3");
		_P5_odtWm22.setMode("0");
		Vector<P5_NozzleInfo> _nozInfoVec22 = new Vector<P5_NozzleInfo>();
		
		P5_NozzleInfo _P5_nozWm22_1 = new P5_NozzleInfo();
		_P5_nozWm22_1.setNozzleNumber("04");
		_P5_nozWm22_1.setBasePrice("161100"); // 4.2
		_P5_nozWm22_1.setGoodsCode("0660"); // 휘발유
		_P5_nozWm22_1.setGoodsType("KIXX"); // 최대 14 자리
		_nozInfoVec22.add(_P5_nozWm22_1);
		
		P5_NozzleInfo _P5_nozWm22_2 = new P5_NozzleInfo();
		_P5_nozWm22_2.setNozzleNumber("05");
		_P5_nozWm22_2.setBasePrice("162200"); // 4.2
		_P5_nozWm22_2.setGoodsCode("1206"); // 경유
		_P5_nozWm22_2.setGoodsType("DIESEL"); // 최대 14 자리
		_nozInfoVec22.add(_P5_nozWm22_2);
		
		P5_NozzleInfo _P5_nozWm22_3 = new P5_NozzleInfo();
		_P5_nozWm22_3.setNozzleNumber("06");
		_P5_nozWm22_3.setBasePrice("163300"); // 4.2
		_P5_nozWm22_3.setGoodsCode("0610");
		_P5_nozWm22_3.setGoodsType("KIXX PRIME"); // 최대 14 자리
		_nozInfoVec22.add(_P5_nozWm22_3);
		
		_P5_odtWm22.setNozzleInfo(_nozInfoVec22);
		_odtInfoVec2.add(_P5_odtWm22);
		//==== End of ODT 설정 ====//
		
		_P5_wm2.setOdtInfo(_odtInfoVec2);	
		adaptor.sendModuleMsg(_P5_wm2);
		
		
		
		
		
		/*
		// 단가변경용 P5전문
		Sleep.sleep(10000);
		P5_WorkingMessage _P5_wm2 = new P5_WorkingMessage();
		Vector<P5_OdtInfo> _odtInfoVec2 = new Vector<P5_OdtInfo>();
		//==== ODT 설정 ====//
		P5_OdtInfo _P5_odtWm22 = new P5_OdtInfo();
		_P5_odtWm22.setOdtID(SelfODTNo);
		_P5_odtWm22.setNozzleCount("3");
		_P5_odtWm22.setMode("1");
		Vector<P5_NozzleInfo> _nozInfoVec22 = new Vector<P5_NozzleInfo>();
		
		P5_NozzleInfo _P5_nozWm22_1 = new P5_NozzleInfo();
		_P5_nozWm22_1.setNozzleNumber("12");
		_P5_nozWm22_1.setBasePrice("161100"); // 4.2
		_P5_nozWm22_1.setGoodsCode("0660"); // 휘발유
		_P5_nozWm22_1.setGoodsType("KIXX"); // 최대 14 자리
		_nozInfoVec22.add(_P5_nozWm22_1);
		
		P5_NozzleInfo _P5_nozWm22_2 = new P5_NozzleInfo();
		_P5_nozWm22_2.setNozzleNumber("13");
		_P5_nozWm22_2.setBasePrice("162200"); // 4.2
		_P5_nozWm22_2.setGoodsCode("1206"); // 경유
		_P5_nozWm22_2.setGoodsType("DIESEL"); // 최대 14 자리
		_nozInfoVec22.add(_P5_nozWm22_2);
		
		P5_NozzleInfo _P5_nozWm22_3 = new P5_NozzleInfo();
		_P5_nozWm22_3.setNozzleNumber("14");
		_P5_nozWm22_3.setBasePrice("163300"); // 4.2
		_P5_nozWm22_3.setGoodsCode("0610");
		_P5_nozWm22_3.setGoodsType("KIXX PRIME"); // 최대 14 자리
		_nozInfoVec22.add(_P5_nozWm22_3);
		
		_P5_odtWm22.setNozzleInfo(_nozInfoVec22);
		_odtInfoVec2.add(_P5_odtWm22);
		//==== End of ODT 설정 ====//
		
		_P5_wm2.setOdtInfo(_odtInfoVec2);	
		adaptor.sendModuleMsg(_P5_wm2);
		*/
		

/*
		// 주유기 환경설정(노즐단가)
		P3_WorkingMessage P3_wm = new P3_WorkingMessage();	
		Vector<P3_NozzleInfo> nozInfoVec3 = new Vector<P3_NozzleInfo>();
	
		P3_NozzleInfo P3_noz1 = new P3_NozzleInfo();
		P3_noz1.setNozzleNo("05");
		P3_noz1.setBasePrice("125000");
		nozInfoVec3.add(P3_noz1);
	
		P3_NozzleInfo P3_noz2 = new P3_NozzleInfo();
		P3_noz2.setNozzleNo("06");
		P3_noz2.setBasePrice("125000");
		nozInfoVec3.add(P3_noz2);

		P3_wm.setNozzleInfo(nozInfoVec3);
		adaptor.sendModuleMsg(P3_wm);
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
		// 노즐제어(=주유금지) 
		PA_WorkingMessage PA_wm = new PA_WorkingMessage();
		PA_wm.setNozzleNo("03"); // Self nozzle
		PA_wm.setConnectNozzleNo("00"); // Self nozzle
		PA_wm.setNozzleState("0"); // 0=금지, 1=허가
		adaptor.sendModuleMsg(PA_wm);
	

		Sleep.sleep(2000);
		// 노즐제어(=주유허가)
		PA_WorkingMessage PA_wm2 = new PA_WorkingMessage();
		PA_wm2.setNozzleNo("04"); // Self nozzle
		PA_wm2.setConnectNozzleNo("00"); // Self nozzle
		PA_wm2.setNozzleState("0"); // 0=금지, 1=허가
		adaptor.sendModuleMsg(PA_wm2);
*/

		// 정액/정량 설정 (셀프: 선결제지시)
		Sleep.sleep(10000);
		PB_WorkingMessage PB_wm = new PB_WorkingMessage();
		PB_wm.setNozzleNo("02"); // Self nozzle
		PB_wm.setConnectNozzleNo("51"); // Self nozzle
		PB_wm.setCommandSet("0"); // 0=정액설정, 1=정량설정
		PB_wm.setLiter("0000000");
		PB_wm.setBasePrice("178800");
		PB_wm.setPrice("00020000");
		adaptor.sendModuleMsg(PB_wm);

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
		P7_wm.setNozzleNo("02");
		P7_wm.setReadBuffInterval("0010");
		P7_wm.setReadStartInterval("0010");
		P7_wm.setWriteStartInterval("0010");
		P7_wm.setLineErrorCount("08");
		P7_wm.setLineErrorSkipCount("0150");
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
