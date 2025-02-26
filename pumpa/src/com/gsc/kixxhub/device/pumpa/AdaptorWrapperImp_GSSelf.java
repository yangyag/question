package com.gsc.kixxhub.device.pumpa;

import java.util.Vector;

import com.gsc.kixxhub.common.data.pump.M1_DBoxInfo;
import com.gsc.kixxhub.common.data.pump.M1_NozzleInfo;
import com.gsc.kixxhub.common.data.pump.M1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P2_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P5_NozzleInfo;
import com.gsc.kixxhub.common.data.pump.P5_OdtInfo;
import com.gsc.kixxhub.common.data.pump.P5_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P6_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.device.pumpa.common.Change;
import com.gsc.kixxhub.device.pumpa.common.Protocol;
import com.gsc.kixxhub.device.pumpa.datas.CommonInfo;
import com.gsc.kixxhub.device.pumpa.datas.E0_WorkingMessage;
import com.gsc.kixxhub.device.pumpa.datas.NozzleInfo;
import com.gsc.kixxhub.device.pumpa.datas.PaymentCardInfo;
import com.gsc.kixxhub.device.pumpa.service.listener.AdaptorListener;

public class AdaptorWrapperImp_GSSelf implements AdaptorListener {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//LogUtility.getPumpALogger().debug("AdaptorWrapperImp Class invoked");
		
		AdaptorListener listener = new AdaptorWrapperImp_GSSelf();
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

		
		String SelfODTNo="26";
		//======== DBox Port ���� ========//
		dBoxPortInfo = new M1_DBoxInfo();
		dBoxPortInfo.setDBoxPortNo("11");
		// ��ġ Device ����
		nozList = new Vector<M1_NozzleInfo>();

		nozInfo = new M1_NozzleInfo();
		nozInfo.setNozzleNo("01");
		nozInfo.setNozzleType("02"); // 01=�Ϲ�������, 02=����������
		nozInfo.setNozProtocol(Change.toString("%02d", Protocol.WooJoo));
		nozInfo.setBaudRate("9600");
		nozInfo.setRomVersion("000");
		nozInfo.setSelfOdtNo(SelfODTNo); // ����ODT
		nozList.add(nozInfo);

		nozInfo = new M1_NozzleInfo();
		nozInfo.setNozzleNo("02");
		nozInfo.setNozzleType("02"); // 01=�Ϲ�������, 02=����������
		nozInfo.setNozProtocol(Change.toString("%02d", Protocol.WooJoo));
		nozInfo.setBaudRate("9600");
		nozInfo.setRomVersion("000");
		nozInfo.setSelfOdtNo(SelfODTNo); // ����ODT
		nozList.add(nozInfo);
		
		dBoxPortInfo.setNozzleInfo(nozList);
		dBoxPortList.add(dBoxPortInfo);
		//===== End of set DBox Port =====//


		//=== DBox Port ���� ===//
		dBoxPortInfo = new M1_DBoxInfo();
		dBoxPortInfo.setDBoxPortNo("12");
		// ��ġ Device ����
		nozList = new Vector<M1_NozzleInfo>();

		nozInfo = new M1_NozzleInfo(); // �Ҹ� ���� ODT
		nozInfo.setNozzleNo(SelfODTNo);
		nozInfo.setNozzleType("05"); // ����ODT
		nozInfo.setNozProtocol(Change.toString("%02d", Protocol.GSSelfODT));
		nozInfo.setBaudRate("19200");
		nozInfo.setRomVersion("000");
		nozInfo.setSelfOdtNo("00"); // ���� ������
		nozList.add(nozInfo);

		dBoxPortInfo.setNozzleInfo(nozList);
		dBoxPortList.add(dBoxPortInfo);
		//===== End of set DBox Port =====//

					
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

		//--- ���� ȯ�漳�� ����ó�� ---//
		P1_WorkingMessage P1_wm = new P1_WorkingMessage();
		Vector<String> odtIDVec = new Vector<String>();
		odtIDVec.add(SelfODTNo);
		P1_wm.setOdtId(odtIDVec);
		P1_wm.setStoreCord("1234567890");
		P1_wm.setStoreRegiNum("123412341234");
		P1_wm.setStoreName("ű�� ������");
		P1_wm.setRepName("������");
		P1_wm.setStorePost("123-456");
		P1_wm.setStoreADDR1("����� ������ ���ʵ� �����͹̳�");
		P1_wm.setStoreADDR2("123-45");
		P1_wm.setTel("234-4857");
		P1_wm.setReportFootTitle("234-4857-1111");
		P1_wm.setSaMinAmt("12345");
		adaptor.sendModuleMsg(P1_wm);

		P2_WorkingMessage P2_wm = new P2_WorkingMessage();
		P2_wm.setOdtId(odtIDVec);
		P2_wm.setBaseHeadTitle("�⺻ �Ӹ��� �⺻ �Ӹ��� �⺻ �Ӹ���");
		P2_wm.setBaseFootTitle1("�⺻ ������1�⺻ ������1�⺻ ������1");
		P2_wm.setBaseFootTitle2("�⺻ ������2�⺻ ������2�⺻ ������2");
		adaptor.sendModuleMsg(P2_wm);

		P5_WorkingMessage P5_wm = new P5_WorkingMessage();
		P5_OdtInfo P5_odtWm = new P5_OdtInfo();
		P5_odtWm.setOdtID(SelfODTNo);
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
	
		P6_WorkingMessage P6_wm = new P6_WorkingMessage(); // �ð�����
		P6_wm.setCommand("P6");
		P6_wm.setWDate("080311");
		P6_wm.setSystemTime("080311132530");
		adaptor.sendModuleMsg(P6_wm);
		//--- End of ���� ȯ�漳�� ����ó�� 
		
		//--- ǥ�ؼ��� ȯ�漳�� ����ó�� ---//
		//-- ��������
		E0_WorkingMessage E0_wm = new E0_WorkingMessage(); 
		E0_wm.setGroup("01");
		CommonInfo commonInfo = new CommonInfo();
		commonInfo.setStoreName("�츮������");
		commonInfo.setIntroduce("�ȳ��ϼ���? �츮�������Դϴ�.");
		commonInfo.setNotice("ǥ�ؼ��� �����⸦ ������Դϴ�.");
		E0_wm.addDatas(commonInfo);
		adaptor.sendModuleMsg(E0_wm);

		//-- ��������(����/�ܰ�)
		E0_wm = new E0_WorkingMessage(); 
		E0_wm.setGroup("02"); 
		NozzleInfo nozzleInfo = new NozzleInfo();
		nozzleInfo.setID("01");
		nozzleInfo.setNozzleNo("11");
		nozzleInfo.setOilCode("1206");
		nozzleInfo.setOilName("����");
		nozzleInfo.setBasePrice("158800");
		E0_wm.addDatas(nozzleInfo);
		adaptor.sendModuleMsg(E0_wm);
		
		E0_wm = new E0_WorkingMessage(); 
		E0_wm.setGroup("02"); 
		nozzleInfo = new NozzleInfo();
		nozzleInfo.setID("02");
		nozzleInfo.setNozzleNo("12");
		nozzleInfo.setOilCode("1206");
		nozzleInfo.setOilName("����");
		nozzleInfo.setBasePrice("158800");
		E0_wm.addDatas(nozzleInfo);
		adaptor.sendModuleMsg(E0_wm);

		//-- �������� ����
		E0_wm = new E0_WorkingMessage(); 
		E0_wm.setGroup("04"); 
		PaymentCardInfo paymentInfo = new PaymentCardInfo();
		paymentInfo.setID("01");
		paymentInfo.setPaymentName("�ſ�ī��");
		E0_wm.addDatas(paymentInfo);
		adaptor.sendModuleMsg(E0_wm);
		
		E0_wm = new E0_WorkingMessage(); 
		E0_wm.setGroup("04"); 
		paymentInfo = new PaymentCardInfo();
		paymentInfo.setID("02");
		paymentInfo.setPaymentName("����");
		E0_wm.addDatas(paymentInfo);
		adaptor.sendModuleMsg(E0_wm);
		
		E0_wm = new E0_WorkingMessage(); 
		E0_wm.setGroup("04"); 
		paymentInfo = new PaymentCardInfo();
		paymentInfo.setID("03");
		paymentInfo.setPaymentName("�ŷ�óī��");
		E0_wm.addDatas(paymentInfo);
		adaptor.sendModuleMsg(E0_wm);



		//---- End of ȯ�漳��ó��----//

		String targetNoz="16";
		String targetODT="23";
/*
		// �����Ϸ�
		S4_WorkingMessage S4_wm = new S4_WorkingMessage();
		S4_wm.setNozzleNo(targetODT); // Self nozzle
		S4_wm.setConnectNozzleNo(targetNoz); // Self nozzle
		S4_wm.setFlag("1");
		S4_wm.setStatusFlag("5"); // �����Ϸ�
		S4_wm.setLiter("0000000");
		S4_wm.setBasePrice("    ");
		S4_wm.setPrice("000000");
		adaptor.sendModuleMsg(S4_wm);
*/
	/*
		// ��������(=��������) 
		PA_WorkingMessage PA_wm = new PA_WorkingMessage();
		PA_wm.setNozzleNo("01"); // Self nozzle
		PA_wm.setConnectNozzleNo("00"); // Self nozzle
		PA_wm.setNozzleState("1"); // 0=����, 1=�㰡
		adaptor.sendModuleMsg(PA_wm);
	
		// ��������(=��������)
		PA_WorkingMessage PA_wm2 = new PA_WorkingMessage();
		PA_wm2.setNozzleNo("02"); // Self nozzle
		PA_wm2.setConnectNozzleNo("00"); // Self nozzle
		PA_wm2.setNozzleState("1"); // 0=����, 1=�㰡
		adaptor.sendModuleMsg(PA_wm2);
*/
/*
		Sleep.sleep(1000);
		// ����/���� ���� (����: ����������)
		PB_WorkingMessage PB_wm = new PB_WorkingMessage();
		PB_wm.setNozzleNo("17"); // Self nozzle
		PB_wm.setConnectNozzleNo(SelfODTNo); // Self nozzle
		PB_wm.setCommandSet("0"); // 0=���׼���, 1=��������
		PB_wm.setLiter("0000000");
		PB_wm.setBasePrice("167700");
		PB_wm.setPrice("00300000");
		adaptor.sendModuleMsg(PB_wm);
*/
/*
		Sleep.sleep(5000);
		// ���а����� ��û
		P8_WorkingMessage P8_wm = new P8_WorkingMessage();
		P8_wm.setNozzleNo(targetNoz);
		adaptor.sendModuleMsg(P8_wm);
*/
/*
		// ������ �ڷ� ��û
		QF_WorkingMessage QF_wm = new QF_WorkingMessage();
		QF_wm.setNozzleNo(targetNoz);
		adaptor.sendModuleMsg(QF_wm);
*/
/*
		// �Ķ���� ����
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
		// ������ �������� ��û (������, ������ �Ѵ�.)
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
