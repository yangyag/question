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

public class AdaptorWrapperImp_test implements AdaptorListener {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//LogUtility.getPumpALogger().debug("AdaptorWrapperImp Class invoked");
		
		AdaptorListener listener = new AdaptorWrapperImp_test();
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
		dBoxPortInfo.setDBoxPortNo("05");
		// 설치 Device 정의
		nozList = new Vector<M1_NozzleInfo>();

		nozInfo = new M1_NozzleInfo();
		nozInfo.setNozzleNo("01");
		nozInfo.setNozzleType("01"); // 01=일반주유기, 02=셀프주유기
		nozInfo.setNozProtocol(Change.toString("%02d", Protocol.WooJoo));
		nozInfo.setBaudRate("9600");
		nozInfo.setRomVersion("000");
		nozInfo.setSelfOdtNo("00"); // 연결ODT
		nozList.add(nozInfo);
	
		nozInfo = new M1_NozzleInfo();
		nozInfo.setNozzleNo("02");
		nozInfo.setNozzleType("01"); // 01=일반주유기, 02=셀프주유기
		nozInfo.setNozProtocol(Change.toString("%02d", Protocol.WooJoo));
		nozInfo.setBaudRate("9600");
		nozInfo.setRomVersion("000");
		nozInfo.setSelfOdtNo("00"); // 연결ODT
		nozList.add(nozInfo);
		
		dBoxPortInfo.setNozzleInfo(nozList);
		dBoxPortList.add(dBoxPortInfo);
		//===== End of set DBox Port =====//


		//======== DBox Port 정의 ========//
		dBoxPortInfo = new M1_DBoxInfo();
		dBoxPortInfo.setDBoxPortNo("06");
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


		//======== DBox Port 정의 ========//
		dBoxPortInfo = new M1_DBoxInfo();
		dBoxPortInfo.setDBoxPortNo("07");
		// 설치 Device 정의
		nozList = new Vector<M1_NozzleInfo>();

		nozInfo = new M1_NozzleInfo();
		nozInfo.setNozzleNo("03");
		nozInfo.setNozzleType("01"); // 01=일반주유기, 02=셀프주유기
		nozInfo.setNozProtocol(Change.toString("%02d", Protocol.WooJoo));
		nozInfo.setBaudRate("9600");
		nozInfo.setRomVersion("000");
		nozInfo.setSelfOdtNo("00"); // 연결ODT
		nozList.add(nozInfo);
	
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

/*
		//======== DBox Port 정의 ========//
		dBoxPortInfo = new M1_DBoxInfo();
		dBoxPortInfo.setDBoxPortNo("05");
		// 설치 Device 정의
		nozList = new Vector<M1_NozzleInfo>();

		nozInfo = new M1_NozzleInfo();
		nozInfo.setNozzleNo("10");
		nozInfo.setNozzleType("01"); // 01=일반주유기, 02=셀프주유기
		nozInfo.setNozProtocol(Change.toString("%02d", Protocol.TatsunoN));
		nozInfo.setBaudRate("19200");
		nozInfo.setRomVersion("000");
		nozInfo.setSelfOdtNo("00"); // 연결ODT
		nozList.add(nozInfo);
		
		nozInfo = new M1_NozzleInfo();
		nozInfo.setNozzleNo("11");
		nozInfo.setNozzleType("01"); // 01=일반주유기, 02=셀프주유기
		nozInfo.setNozProtocol(Change.toString("%02d", Protocol.TatsunoN));
		nozInfo.setBaudRate("19200");
		nozInfo.setRomVersion("000");
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
/*
		//=== DBox Port 정의 ===//
		dBoxPortInfo = new M1_DBoxInfo();
		dBoxPortInfo.setDBoxPortNo("11");
		// 설치 Device 정의
		nozList = new Vector<M1_NozzleInfo>();

		nozInfo = new M1_NozzleInfo(); // 소모 셀프 ODT
		nozInfo.setNozzleNo("21");
		nozInfo.setNozzleType("05"); // 셀프ODT
		nozInfo.setNozProtocol(Change.toString("%02d", Protocol.SomoSelf)); 
		nozInfo.setBaudRate("19200");
		nozInfo.setRomVersion("000");
		nozInfo.setSelfOdtNo("00"); // 연결 주유기
		nozList.add(nozInfo);

		dBoxPortInfo.setNozzleInfo(nozList);
		dBoxPortList.add(dBoxPortInfo);
		//===== End of set DBox Port =====//
*/	
/*
		//======== DBox Port 정의 ========//
		dBoxPortInfo = new M1_DBoxInfo();
		dBoxPortInfo.setDBoxPortNo("10");
		// 설치 Device 정의
		nozList = new Vector<M1_NozzleInfo>();

		nozInfo = new M1_NozzleInfo(); // 다쓰노 셀프 ODT
		nozInfo.setNozzleNo(SelfODTNo);
		nozInfo.setNozzleType("05"); // 셀프ODT
		nozInfo.setNozProtocol(Change.toString("%02d", Protocol.TatsunoSelfMPP4)); 
		nozInfo.setBaudRate("19200");
		nozInfo.setRomVersion("000");
		nozInfo.setSelfOdtNo("00"); // 연결 주유기
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
		
		Sleep.sleep(1000);
		//######### Start of send WorkingMessage #######//
/*
		//--- 소모셀프 환경설정 정보처리 ---//
		P1_WorkingMessage P1_wm = new P1_WorkingMessage();
		Vector<String> odtIDVec = new Vector<String>();
		odtIDVec.add("21");
		P1_wm.setOdtId(odtIDVec);
		P1_wm.setStoreCord("1234567890");
		P1_wm.setStoreRegiNum("123412341234");
		P1_wm.setStoreName("킥스 주유소");
		P1_wm.setRepName("주유기");
		P1_wm.setStorePost("123-456");
		P1_wm.setStoreADDR1("서울시 강남구 서초동 남부터미널");
		P1_wm.setStoreADDR2("123-45");
		P1_wm.setTel("234-4857");
		P1_wm.setReportFootTitle("234-4857-1111");
		P1_wm.setSaMinAmt("12345");
		adaptor.sendModuleMsg(P1_wm);

		P2_WorkingMessage P2_wm = new P2_WorkingMessage();
		P2_wm.setOdtId(odtIDVec);
		P2_wm.setBaseHeadTitle("기본 머리말 기본 머리말 기본 머리말");
		P2_wm.setBaseFootTitle1("기본 꼬리말1기본 꼬리말1기본 꼬리말1");
		P2_wm.setBaseFootTitle2("기본 꼬리말2기본 꼬리말2기본 꼬리말2");
		adaptor.sendModuleMsg(P2_wm);

		P5_WorkingMessage P5_wm = new P5_WorkingMessage();
		P5_OdtInfo P5_odtWm = new P5_OdtInfo();
		P5_odtWm.setOdtID("21");
		P5_odtWm.setNozzleCount("2");
		Vector<P5_NozzleInfo> nozInfoVec = new Vector<P5_NozzleInfo>();
		Vector<P5_OdtInfo> odtInfoVec = new Vector<P5_OdtInfo>();
		
		P5_NozzleInfo P5_nozWm = new P5_NozzleInfo();
		P5_nozWm.setNozzleNumber("01");
		P5_nozWm.setBasePrice("157700"); // 4.2
		P5_nozWm.setGoodsCode("1206");
		P5_nozWm.setGoodsType("DIESEL");  
		nozInfoVec.add(P5_nozWm);
		
		P5_NozzleInfo P5_nozWm2 = new P5_NozzleInfo();
		P5_nozWm2.setNozzleNumber("02");
		P5_nozWm2.setBasePrice("185500"); // 4.2
		P5_nozWm2.setGoodsCode("0660");
		P5_nozWm2.setGoodsType("KIXX"); 
		nozInfoVec.add(P5_nozWm2);
		
		P5_odtWm.setNozzleInfo(nozInfoVec);	
		odtInfoVec.add(P5_odtWm);
		P5_wm.setOdtInfo(odtInfoVec);
		adaptor.sendModuleMsg(P5_wm);
	
		P6_WorkingMessage P6_wm = new P6_WorkingMessage(); // 시각설정
		P6_wm.setCommand("P6");
		P6_wm.setWDate("080311");
		P6_wm.setSystemTime("080311132530");
		adaptor.sendModuleMsg(P6_wm);
		//--- End of 소모셀프 환경설정 정보처리 
*/

		//######### Start of send WorkingMessage #######//
		//--- 다쓰노셀프 환경설정 정보처리(다운로드 필수) ---//
		//P5_WorkingMessage P5_wm2 = new P5_WorkingMessage();
		//Vector<P5_OdtInfo> odtInfoVec2 = new Vector<P5_OdtInfo>();
	/*	
		//==== ODT 설정 ====//
		P5_OdtInfo P5_odtWm22 = new P5_OdtInfo();
		P5_odtWm22.setOdtID(SelfODTNo);
		P5_odtWm22.setNozzleCount("2");
		Vector<P5_NozzleInfo> nozInfoVec22 = new Vector<P5_NozzleInfo>();
		
		P5_NozzleInfo P5_nozWm22_1 = new P5_NozzleInfo();
		P5_nozWm22_1.setNozzleNumber("12");
		P5_nozWm22_1.setBasePrice("111100"); // 4.2
		P5_nozWm22_1.setGoodsCode("0660"); // 휘발유
		P5_nozWm22_1.setGoodsType("KIXX"); // 최대 14 자리
		nozInfoVec22.add(P5_nozWm22_1);
		
		P5_NozzleInfo P5_nozWm22_2 = new P5_NozzleInfo();
		P5_nozWm22_2.setNozzleNumber("13");
		P5_nozWm22_2.setBasePrice("111100"); // 4.2
		P5_nozWm22_2.setGoodsCode("1206"); // 경유
		P5_nozWm22_2.setGoodsType("DIESEL"); // 최대 14 자리
		nozInfoVec22.add(P5_nozWm22_2);
		
		//P5_NozzleInfo P5_nozWm22_3 = new P5_NozzleInfo();
		//P5_nozWm22_3.setNozzleNumber("14");
		//P5_nozWm22_3.setBasePrice("111100"); // 4.2
		//P5_nozWm22_3.setGoodsCode("0660");
		//P5_nozWm22_3.setGoodsType("KIXX"); // 최대 14 자리
		//nozInfoVec22.add(P5_nozWm22_3);
		
		P5_odtWm22.setNozzleInfo(nozInfoVec22);	
		odtInfoVec2.add(P5_odtWm22);
		//==== End of ODT 설정 ====//
		*/
		//P5_wm2.setOdtInfo(odtInfoVec2);	
		//adaptor.sendModuleMsg(P5_wm2);
		//--- End of 다쓰노셀프 환경설정 정보처리 ---//


		// 주유기 환경설정(노즐단가)
		P3_WorkingMessage P3_wm = new P3_WorkingMessage();	
		Vector<P3_NozzleInfo> nozInfoVec3 = new Vector<P3_NozzleInfo>();

		P3_NozzleInfo P3_noz1 = new P3_NozzleInfo();
		P3_noz1.setNozzleNo("01");
		P3_noz1.setBasePrice("151100");
		nozInfoVec3.add(P3_noz1);

		P3_NozzleInfo P3_noz2 = new P3_NozzleInfo();
		P3_noz2.setNozzleNo("02");
		P3_noz2.setBasePrice("152200");
		nozInfoVec3.add(P3_noz2);

		P3_NozzleInfo P3_noz3 = new P3_NozzleInfo();
		P3_noz3.setNozzleNo("03");
		P3_noz3.setBasePrice("153300");
		nozInfoVec3.add(P3_noz3);

		P3_NozzleInfo P3_noz4 = new P3_NozzleInfo();
		P3_noz4.setNozzleNo("05");
		P3_noz4.setBasePrice("154400");
		nozInfoVec3.add(P3_noz4);

		P3_NozzleInfo P3_noz5 = new P3_NozzleInfo();
		P3_noz5.setNozzleNo("06");
		P3_noz5.setBasePrice("155500");
		nozInfoVec3.add(P3_noz5);

		P3_NozzleInfo P3_noz6 = new P3_NozzleInfo();
		P3_noz6.setNozzleNo("07");
		P3_noz6.setBasePrice("156600");
		nozInfoVec3.add(P3_noz6);

		P3_NozzleInfo P3_noz7 = new P3_NozzleInfo();
		P3_noz7.setNozzleNo("08");
		P3_noz7.setBasePrice("157700");
		nozInfoVec3.add(P3_noz7);
/*
		P3_NozzleInfo P3_noz8 = new P3_NozzleInfo();
		P3_noz8.setNozzleNo("08");
		P3_noz8.setBasePrice("194800");
		nozInfoVec3.add(P3_noz8);
	
		P3_NozzleInfo P3_noz9 = new P3_NozzleInfo();
		P3_noz9.setNozzleNo("09");
		P3_noz9.setBasePrice("155900");
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
		PA_wm.setNozzleNo("01"); // Self nozzle
		PA_wm.setConnectNozzleNo("00"); // Self nozzle
		PA_wm.setNozzleState("0"); // 0=금지, 1=허가
		adaptor.sendModuleMsg(PA_wm);
	
		// 노즐제어(=주유금지)
		PA_WorkingMessage PA_wm2 = new PA_WorkingMessage();
		PA_wm2.setNozzleNo("02"); // Self nozzle
		PA_wm2.setConnectNozzleNo("00"); // Self nozzle
		PA_wm2.setNozzleState("0"); // 0=금지, 1=허가
		adaptor.sendModuleMsg(PA_wm2);
*/
/*
		// 정액/정량 설정 (셀프: 선결제지시)
		PB_WorkingMessage PB_wm = new PB_WorkingMessage();
		PB_wm.setNozzleNo("13"); // Self nozzle
		PB_wm.setConnectNozzleNo("00"); // Self nozzle
		PB_wm.setCommandSet("0"); // 0=정액설정, 1=정량설정
		PB_wm.setLiter("0000000");
		PB_wm.setBasePrice("188900");
		PB_wm.setPrice("00020000");
		adaptor.sendModuleMsg(PB_wm);
*/
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
		P7_wm.setNozzleNo("07");
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
