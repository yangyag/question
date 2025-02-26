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
import com.gsc.kixxhub.device.pumpa.service.listener.AdaptorListener;

public class AdaptorWrapperImp_PrimeSelf implements AdaptorListener {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//LogUtility.getPumpALogger().debug("AdaptorWrapperImp Class invoked");
		
		AdaptorListener listener = new AdaptorWrapperImp_PrimeSelf();
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

		
		//======== DBox Port ���� ========//
		dBoxPortInfo = new M1_DBoxInfo();
		dBoxPortInfo.setDBoxPortNo("10");
		// ��ġ Device ����
		nozList = new Vector<M1_NozzleInfo>();

		nozInfo = new M1_NozzleInfo();
		nozInfo.setNozzleNo("53");
		nozInfo.setNozzleType("02"); // 01=�Ϲ�������, 02=����������
		nozInfo.setNozProtocol(Change.toString("%02d", Protocol.PrimeSelf));
		nozInfo.setBaudRate("9600");
		nozInfo.setRomVersion("000");
		nozInfo.setSelfOdtNo("62"); // ����ODT
		nozList.add(nozInfo);

		nozInfo = new M1_NozzleInfo();
		nozInfo.setNozzleNo("54");
		nozInfo.setNozzleType("02"); // 01=�Ϲ�������, 02=����������
		nozInfo.setNozProtocol(Change.toString("%02d", Protocol.PrimeSelf));
		nozInfo.setBaudRate("9600");
		nozInfo.setRomVersion("000");
		nozInfo.setSelfOdtNo("62"); // ����ODT
		nozList.add(nozInfo);
		
		nozInfo = new M1_NozzleInfo();
		nozInfo.setNozzleNo("55");
		nozInfo.setNozzleType("02"); // 01=�Ϲ�������, 02=����������
		nozInfo.setNozProtocol(Change.toString("%02d", Protocol.PrimeSelf));
		nozInfo.setBaudRate("9600");
		nozInfo.setRomVersion("000");
		nozInfo.setSelfOdtNo("62"); // ����ODT
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
		nozInfo.setNozzleNo("62");
		nozInfo.setNozzleType("05"); // ����ODT
		nozInfo.setNozProtocol(Change.toString("%02d", Protocol.PrimeSelfODT)); 
		nozInfo.setBaudRate("9600");
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
 
		//--- �����Ӽ��� ȯ�漳�� ����ó�� ---//
		P1_WorkingMessage P1_wm = new P1_WorkingMessage();
		Vector<String> odtIDVec = new Vector<String>();
		odtIDVec.add("62");
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
		P5_odtWm.setOdtID("62");
		P5_odtWm.setNozzleCount("1");
		Vector<P5_NozzleInfo> nozInfoVec = new Vector<P5_NozzleInfo>();
		Vector<P5_OdtInfo> odtInfoVec = new Vector<P5_OdtInfo>();
		
		P5_NozzleInfo P5_nozWm = new P5_NozzleInfo();
		P5_nozWm.setNozzleNumber("53");
		P5_nozWm.setBasePrice("157700"); // 4.2
		P5_nozWm.setGoodsCode("1206");
		P5_nozWm.setGoodsType("DIESEL");  
		nozInfoVec.add(P5_nozWm);
		
		P5_NozzleInfo P5_nozWm2 = new P5_NozzleInfo();
		P5_nozWm2.setNozzleNumber("54");
		P5_nozWm2.setBasePrice("158800"); // 4.2
		P5_nozWm2.setGoodsCode("0660");
		P5_nozWm2.setGoodsType("KIXX"); 
		nozInfoVec.add(P5_nozWm2);
		
		P5_NozzleInfo P5_nozWm3 = new P5_NozzleInfo();
		P5_nozWm3.setNozzleNumber("55");
		P5_nozWm3.setBasePrice("159900"); // 4.2
		P5_nozWm3.setGoodsCode("0610");
		P5_nozWm3.setGoodsType("KIXX PRIME"); 
		nozInfoVec.add(P5_nozWm3);
		
		P5_odtWm.setNozzleInfo(nozInfoVec);	
		odtInfoVec.add(P5_odtWm);
		P5_wm.setOdtInfo(odtInfoVec);
		adaptor.sendModuleMsg(P5_wm);
	
		P6_WorkingMessage P6_wm = new P6_WorkingMessage(); // �ð�����
		P6_wm.setCommand("P6");
		P6_wm.setWDate("080311");
		P6_wm.setSystemTime("20080311132530");
		adaptor.sendModuleMsg(P6_wm);
		//--- End of ��ȭ�����Ӽ��� ȯ�漳�� ����ó�� 
		
		
		/*
		// �ܰ������ P5����
		Sleep.sleep(30000);
		P5_WorkingMessage _P5_wm2 = new P5_WorkingMessage();
		Vector<P5_OdtInfo> _odtInfoVec2 = new Vector<P5_OdtInfo>();
		//==== ODT ���� ====//
		P5_OdtInfo _P5_odtWm22 = new P5_OdtInfo();
		_P5_odtWm22.setOdtID("62");
		_P5_odtWm22.setNozzleCount("3");
		_P5_odtWm22.setMode("1");
		Vector<P5_NozzleInfo> _nozInfoVec22 = new Vector<P5_NozzleInfo>();
		
		P5_NozzleInfo _P5_nozWm22_1 = new P5_NozzleInfo();
		_P5_nozWm22_1.setNozzleNumber("53");
		_P5_nozWm22_1.setBasePrice("161100"); // 4.2
		_P5_nozWm22_1.setGoodsCode("0660"); // �ֹ���
		_P5_nozWm22_1.setGoodsType("KIXX"); // �ִ� 14 �ڸ�
		_nozInfoVec22.add(_P5_nozWm22_1);
		
		P5_NozzleInfo _P5_nozWm22_2 = new P5_NozzleInfo();
		_P5_nozWm22_2.setNozzleNumber("54");
		_P5_nozWm22_2.setBasePrice("162200"); // 4.2
		_P5_nozWm22_2.setGoodsCode("1206"); // ����
		_P5_nozWm22_2.setGoodsType("DIESEL"); // �ִ� 14 �ڸ�
		_nozInfoVec22.add(_P5_nozWm22_2);
		
		P5_NozzleInfo _P5_nozWm22_3 = new P5_NozzleInfo();
		_P5_nozWm22_3.setNozzleNumber("55");
		_P5_nozWm22_3.setBasePrice("163300"); // 4.2
		_P5_nozWm22_3.setGoodsCode("0610");
		_P5_nozWm22_3.setGoodsType("KIXX PRIME"); // �ִ� 14 �ڸ�
		_nozInfoVec22.add(_P5_nozWm22_3);
		
		_P5_odtWm22.setNozzleInfo(_nozInfoVec22);
		_odtInfoVec2.add(_P5_odtWm22);
		//==== End of ODT ���� ====//
		
		_P5_wm2.setOdtInfo(_odtInfoVec2);	
		adaptor.sendModuleMsg(_P5_wm2);
		*/
		

		//---- End of ȯ�漳��ó��----//

		String targetNoz="05";
		String targetODT="25";
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
		Sleep.sleep(10000);
		// ����/���� ���� (����: ����������)
		PB_WorkingMessage PB_wm = new PB_WorkingMessage();
		PB_wm.setNozzleNo("04"); // Self nozzle
		PB_wm.setConnectNozzleNo("25"); // Self nozzle
		PB_wm.setCommandSet("0"); // 0=���׼���, 1=��������
		PB_wm.setLiter("0000000");
		PB_wm.setBasePrice("167700");
		PB_wm.setPrice("00020000");
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
