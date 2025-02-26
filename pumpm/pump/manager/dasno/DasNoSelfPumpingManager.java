package com.gsc.kixxhub.module.pumpm.pump.manager.dasno;

import java.util.Hashtable;

import com.gsc.kixxhub.common.data.IConstant;
import com.gsc.kixxhub.common.data.pump.HE_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.QL_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S4_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.data.upos.UPOSMessage;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.common.utility.property.PropertyManager;
import com.gsc.kixxhub.module.pumpm.pump.data.odt.DasNoODTNozzleInfo;
import com.gsc.kixxhub.module.pumpm.pump.util.ODTUtility_Common;
import com.gsc.kixxhub.module.pumpm.pump.util.ODTUtility_DaSNo;
import com.gsc.kixxhub.module.pumpm.pump.util.PumpMUtil;

public class DasNoSelfPumpingManager {
	
	public static final int FULL_PUMPING_PRICE = Integer.parseInt(PropertyManager.getSingleton().getProperty(PropertyManager.SELFODT_FULLPUMPING_PRICE, PropertyManager.SELFODT_FULLPUMPING_PRICE_DEFAULT)) ;
	private static DasNoSelfPumpingManager selfOdtFullPumpingMgr = null ;
	/**
	 * ����(or �������) ��û ������ �����Ѵ�.
	 * 
	 * @param nozzleNo		: ���� ��ȣ
	 * @param orgWorkMsg	: ������κ��� �ö�� ��û ����
	 * @param isCancel		: ���� or ��� ����
	 * @return
	 */
	private static WorkingMessage createWorkingMessage(String nozzleNo, WorkingMessage orgWorkMsg, boolean isCancel) {
		WorkingMessage workMsg = null ;

		HE_WorkingMessage heWorkMsg = ((HE_WorkingMessage)orgWorkMsg).createClone() ;
		if (isCancel) {
			heWorkMsg.setCardType("02") ;
		}
		workMsg = heWorkMsg ;
		
		return workMsg ;
	}
	
	/**
	 * �������Ϳ� �����ݾ׿� ���� WorkingMessage �� �����Ѵ�.
	 * 
	 * @param nozzleNo		: ���� ��ȣ
	 * @param orgWorkMsg	: ������� ���� �ö�� ���� ��û ����
	 * @param liter			: ����
	 * @param pumpingPrice	: �ݾ�
	 * @return
	 */
	private static WorkingMessage createWorkingMessage(String nozzleNo, WorkingMessage orgWorkMsg, 
			String liter, String pumpingPrice) {
		
		WorkingMessage workMsg= createWorkingMessage(nozzleNo, orgWorkMsg, liter, pumpingPrice, null);
		
		return workMsg;
	}
	
	/**
	 * �������Ϳ� �����ݾ׿� ���� WorkingMessage �� �����Ѵ�.
	 * 
	 * @param nozzleNo		: ���� ��ȣ
	 * @param orgWorkMsg	: ������� ���� �ö�� ���� ��û ����
	 * @param liter			: ����
	 * @param pumpingPrice	: �ݾ�
	 * @return
	 */
	private static WorkingMessage createWorkingMessage(String nozzleNo, WorkingMessage orgWorkMsg, 
			String liter, String pumpingPrice, String basePrice) {
		
		WorkingMessage workMsg = null ;

		HE_WorkingMessage heWorkMsg = ((HE_WorkingMessage)orgWorkMsg).createClone() ;
		
		heWorkMsg.setLiter(liter) ;
		heWorkMsg.setPrice(pumpingPrice) ;
		
		if (basePrice != null)
			heWorkMsg.setBasePrice(basePrice);
		
		workMsg = heWorkMsg ;		

		return workMsg ;
	}
	
	public static void destroy() {
		selfOdtFullPumpingMgr = null ;
	}
	
	public static DasNoSelfPumpingManager getInstance() {
		if (selfOdtFullPumpingMgr == null) {
			selfOdtFullPumpingMgr = new DasNoSelfPumpingManager() ;
		}
		return selfOdtFullPumpingMgr ;
	}
	
	public static void init() {
		selfOdtFullPumpingMgr = new DasNoSelfPumpingManager() ;
	}
		
	/**
	 * 	selfodt.fullpumping.option �� �ǹ̴� �Ʒ��� ����. �� Class �� �پ��� ���������� ���ȴ�.
	 *		0	: 	BlackList check -> Request for Approval
	 *		1	:	149,900 Won Request for Approval -> After pumping completion, request real pumping price for Approval -> 149,900 Won request for cancel
	 *		2	:	149,900 Won Request for Approval -> After pumping completion, 149,900 Won request for cancel -> request real pumping price for Approval
	 */
	private int FULL_OPTION = IConstant.FULL_PUMPING_OPTION_0 ;
	
	/**
	 * nozzleHash ���� �����ϰ� �ִ� ODTNozzleInfo Object �� ������ ��� ���� �����ȴ�.
	 * 	1. �پ��� �������� ���� ���� ��û�� �����ȴ�.
	 * 	2. POS �� ���� Preset �� ��쿡�� ���� ������ �ȴ�.
	 * �׸��� ������ ��� �����Ѵ�
	 * 	1. ���ο� KH ó����ȣ ������
	 * ODTNozzleInfo Object �� ���� ���� ���θ� �����ϰ� ������, ���� ������ ��� ���� �������� �����Ѵ�.
	 */
	public Hashtable<String, DasNoODTNozzleInfo> nozzleHash = null ;
	
	public DasNoSelfPumpingManager() {
		String option = PropertyManager.getSingleton().getProperty(PropertyManager.SELFODT_FULLPUMPING_OPTION, PropertyManager.SELFODT_FULLPUMPING_OPTION_DEFAULT) ;
		try {
			FULL_OPTION = Integer.parseInt(option) ;
			if ((FULL_OPTION == IConstant.FULL_PUMPING_OPTION_0) || 
					(FULL_OPTION == IConstant.FULL_PUMPING_OPTION_1) || 
					(FULL_OPTION == IConstant.FULL_PUMPING_OPTION_2)) {
				
			} else {
				LogUtility.getPumpMLogger().warn("[Pump M] selfodt.fullpumping.option is not 1,2,3, so set 0 as default") ;
				FULL_OPTION = IConstant.FULL_PUMPING_OPTION_0 ;
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
		}
		nozzleHash = new Hashtable<String, DasNoODTNozzleInfo>() ;
	}
	
	/**
	 * ���� ������ �����Ѵ�. �� ���� �������� ���� �������� ��� ���ؼ� ���Ǿ� �� �� �ִ�.
	 * 
	 * @param nozzleNo		: ���� ��ȣ
	 * @param uPosMsg		: ���� UPOSMessage
	 */
	public void addRespondUPOSMessage(String nozzleNo, UPOSMessage uPosMsg) {
		try {
			DasNoODTNozzleInfo nozInfo = nozzleHash.get(nozzleNo) ;

			nozInfo.addRespondUPOSMessage(uPosMsg) ;
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
		}
	}
	
	/**
	 * �Ϲ� ������ ��� option1 => �½���  option2 => �����  �ɼǿ� ���� ���� ��� ����ǵ��� ó�� 
	 * @param nozzleNo		: ���� ��ȣ
	 */
	//2016-04 �̰�ȣ
	public void changeDasNoODTNozzleInfoLikeFullPumpingWithOption(String nozzleNo) {
		int option = 0;
		//selfodt.fullpumping.option ���� Ȯ���Ͽ� 1�� ��� �½��밡 �ǵ��� 
		try {
			String fullpumpingOption = PropertyManager.getSingleton().getProperty("selfodt.fullpumping.option", "0") ;
			option = Integer.parseInt(fullpumpingOption);
		} catch (Exception e) {
			LogUtility.getLogger().error(e.getMessage(), e) ;
			option = 0;
		}
		
		try {
			if (option == IConstant.FULL_PUMPING_OPTION_1) {
				changeDasNoODTNozzleInfoLikeFullPumpingWithOption9(nozzleNo);
			}
			else {
				changeDasNoODTNozzleInfoLikeFullPumpingWithOption6(nozzleNo);
			}
		} catch (Exception e) {
			LogUtility.getLogger().error(e.getMessage(), e) ;
		}			
	}	
	
	/**
	 * �Ϲ� ���������� ����� �ּ� �ݾ� ���� ũ�� ������ �����, ������� �ϱ����ؼ� ������ �����Ѵ�.
	 * 
	 * @param nozzleNo		: ���� ��ȣ
	 */
	public void changeDasNoODTNozzleInfoLikeFullPumpingWithOption6(String nozzleNo) {
		try {
			LogUtility.getPumpMLogger().info("[Pump M] changeDasNoODTNozzleInfoLikeFullPumpingWithOption6. nozID=" +nozzleNo) ;
			DasNoODTNozzleInfo nozInfo = nozzleHash.get(nozzleNo) ;
			nozInfo.setOption(IConstant.FULL_PUMPING_OPTION_6) ;				// ���� ���� Option 2 �� �������� �Ѵ�.
			nozInfo.setFullPumping(true) ;																			// ���� ������ �����Ѵ�.
			nozInfo.setPumpingCompleted(true) ;																// �����Ϸ� �Ǿ����� ���� �����Ѵ�.
			nozInfo.setOptionState(IConstant.SELF_FULLPUMPING_OPTION_6_STATE_1) ;	// ���� ���� Option 2 �� State 1 �� �̹� ���Ѱ����� �����Ѵ�.
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
		}			
	}
	
	/**
	 * [�Ϲ� ����] ����� �ּ� �ݾ� ���� ū ��� �����, �������� �ϱ����ؼ� ������ �����Ѵ�.
	 * 
	 * @param nozzleNo		: ���� ��ȣ
	 */
	public void changeDasNoODTNozzleInfoLikeFullPumpingWithOption9(String nozzleNo) {
		try {
			LogUtility.getLogger().info("[Pump M] changeDasNoODTNozzleInfoLikePumpingWithOption9. nozID=" +nozzleNo) ;
			DasNoODTNozzleInfo nozInfo = nozzleHash.get(nozzleNo) ;
			nozInfo.setOption(IConstant.FULL_PUMPING_OPTION_9) ;				// ���� ���� Option 1 �� �������� �Ѵ�.
			nozInfo.setFullPumping(true) ;																			// ���� ������ �����Ѵ�.
			nozInfo.setPumpingCompleted(true) ;																// �����Ϸ� �Ǿ����� ���� �����Ѵ�.
			nozInfo.setOptionState(IConstant.SELF_FULLPUMPING_OPTION_9_STATE_1) ;	// ���� ���� Option 1 �� State 1 �� �̹� ���Ѱ����� �����Ѵ�.
		} catch (Exception e) {
			LogUtility.getLogger().error(e.getMessage(), e) ;
		}			
	}
	
	/**
	 * [�Ϲ�����] �����Ϸ� �ݾ��� 0 ���� ��� ó�� : (���� -> (�����Ϸ� 0���� ���) -> ������ ���)
	 * 
	 * @param nozzleNo
	 */
	public void changeDasNoODTNozzleInfoWithOption5(String nozzleNo) {
		try {
			LogUtility.getPumpMLogger().info("[Pump M] changeDasNoODTNozzleInfoWithOption5. nozID=" +nozzleNo) ;
			DasNoODTNozzleInfo nozInfo = nozzleHash.get(nozzleNo) ;
			nozInfo.setOption(IConstant.FULL_PUMPING_OPTION_5) ;			// Option 5  �� �������� �Ѵ�.
			nozInfo.setFullPumping(true) ;																		// ���� ������ �����Ѵ�.
			nozInfo.setPumpingCompleted(true) ;															// �����Ϸ� �Ǿ����� ���� �����Ѵ�.
			nozInfo.setOptionState(IConstant.SELF_FULLPUMPING_OPTION_5_STATE_1) ;	// Option 5  �� State 1 �� �̹� ���Ѱ����� �����Ѵ�.
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
		}			
	}
	
	/**
	 * [��������] �����Ϸ� �ݾ��� 0 ���� ��� ó�� : (���� -> (�����Ϸ� 0���� ���) -> ������ ���)
	 * 
	 * @param nozzleNo
	 */
	public void changeDasNoODTNozzleInfoWithOption7(String nozzleNo) {
		try {
			LogUtility.getPumpMLogger().info("[Pump M] changeDasNoODTNozzleInfoWithOption7. nozID=" +nozzleNo) ;
			DasNoODTNozzleInfo nozInfo = nozzleHash.get(nozzleNo) ;
			nozInfo.setOption(IConstant.FULL_PUMPING_OPTION_7) ;			// Option 7  �� �������� �Ѵ�.
			nozInfo.setFullPumping(true) ;																		// ���� ������ �����Ѵ�.
			nozInfo.setPumpingCompleted(true) ;															// �����Ϸ� �Ǿ����� ���� �����Ѵ�.
			nozInfo.setOptionState(IConstant.SELF_FULLPUMPING_OPTION_7_STATE_1) ;	// Option 7  �� State 1 �� �̹� ���Ѱ����� �����Ѵ�.
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
		}			
	}
	
	/**
	 * [2008.11.21] by WooChul Jung
	 * �پ��� ������ ��� POS �� ���� Preset �� ���۵ȴ�. �� ��� ������ �ڷḦ �����ϰ� ���Ӱ� �� �������� ���� ������ �ƴ���
	 * ǥ���Ѵ�. ��ź �ŵ��� �������� ��� �Ʒ��� ���� ������ �߻��Ͽ���.
	 * 		1. ������-A : �մ� A �� ���� �����Ͽ� �����Ϸ�
	 * 		2  ������-B : �մ� B �� POS ���� ����/���� ���� �Ͽ� ���� �Ϸ�
	 * 			�̶� �Ʒ� �ڵ尡 ������ ��� ������-B �� �Ϸ�Ǿ����� �մ� A �� ī��� ���簡 ����Ǿ���.
	 * 
	 * @param nozzleNo	: ���� ��ȣ
	 * @param isFull	: ���� ���� ����
	 */
	public void createODTNozzleInfo(String nozzleNo, boolean isFull, int option) {
		try {
			if (PumpMUtil.isNozzleConnectedToSelfODT(nozzleNo)) {
				nozzleHash.remove(nozzleNo) ;
				DasNoODTNozzleInfo nozInfo = new DasNoODTNozzleInfo(nozzleNo, isFull, option) ;
				nozzleHash.put(nozzleNo, nozInfo) ;
			} 
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
		}
	}

	/**
	 * 
	 * @param nozzleNo
	 * @return
	 */
	public int getCurrentOption(String nozzleNo) {
		int option = IConstant.FULL_PUMPING_OPTION_8 ;
		try {
			DasNoODTNozzleInfo nozInfo = nozzleHash.get(nozzleNo) ;
			if (nozInfo != null) {
				option = nozInfo.getOption() ;
			} 
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage() ,e) ;
		}
		LogUtility.getPumpMLogger().info("[Pump M] " + nozzleNo + " Nozzle is option= " + option) ;
		return option ;
	}
	
	/**
	 * 
	 * @param nozzleNo
	 * @return
	 */
	public DasNoODTNozzleInfo getDasNoODTNozzleInfo(String nozzleNo) {
		return nozzleHash.get(nozzleNo) ;
	}
	
	public int getFullPumpingOption(){
		return FULL_OPTION;
	}
	
	/**
	 * CAT M ���� ���� ���������� ���� ���� �߰��� ������� �� ������ �ִ����� �����Ѵ�. ( �� �Լ��� ���� �Ϸ� ���� ȣ��ȴ�.)
	 * 
	 * @param nozzleNo	: ���� ��ȣ
	 * @return
	 */
	public UPOSMessage getPendingUPOSMsgAndRemove(String nozzleNo) {
		boolean rlt = false ;
		UPOSMessage uposMsg = null ;
		DasNoODTNozzleInfo nozInfo = null ;
		try {
			nozInfo = nozzleHash.get(nozzleNo) ;
			int nozInfoOptionInt = nozInfo.getOption() ;
			
			switch (nozInfoOptionInt) {
				case IConstant.FULL_PUMPING_OPTION_0 : {
					break ;
				}
				case IConstant.FULL_PUMPING_OPTION_1 : {
					if (nozInfo.getOptionState() == IConstant.SELF_FULLPUMPING_OPTION_1_STATE_2) {
						uposMsg = nozInfo.getPendingUPOSMsg() ;	
						nozInfo.setOptionState(IConstant.SELF_FULLPUMPING_OPTION_1_STATE_3) ;
					}
					break ;
				}
				case IConstant.FULL_PUMPING_OPTION_2 : {
					if (nozInfo.getOptionState() == IConstant.SELF_FULLPUMPING_OPTION_2_STATE_2) {
						uposMsg = nozInfo.getPendingUPOSMsg() ;	
						nozInfo.setOptionState(IConstant.SELF_FULLPUMPING_OPTION_2_STATE_3) ;
					}
					break ;
				}
				case IConstant.FULL_PUMPING_OPTION_5 : {
					break ;
				}
				case IConstant.FULL_PUMPING_OPTION_6 : {
					if (nozInfo.getOptionState() == IConstant.SELF_FULLPUMPING_OPTION_6_STATE_2) {
						uposMsg = nozInfo.getPendingUPOSMsg() ;	
						nozInfo.setOptionState(IConstant.SELF_FULLPUMPING_OPTION_6_STATE_3) ;
					}
					break ;
				}
				case IConstant.FULL_PUMPING_OPTION_7 : {
					break ;
				}
				case IConstant.FULL_PUMPING_OPTION_9 : {
					//2016-04 �̰�ȣ
					if (nozInfo.getOptionState() == IConstant.SELF_FULLPUMPING_OPTION_9_STATE_2) {
						uposMsg = nozInfo.getPendingUPOSMsg() ;	
						nozInfo.setOptionState(IConstant.SELF_FULLPUMPING_OPTION_9_STATE_3) ;
					}
					break ;
				}
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage() ,e) ;
		}

		// debug
		if (uposMsg != null) {
			rlt = true ;
		} else {
			rlt = false ;
		}

		/**
		 * Pending ������ �� �̻� ����� �ʿ䰡 ���� ������ �����Ѵ�.
		 */
		if (nozInfo != null) {
			nozInfo.setPendingUPOSMsg(null) ;
		}
		
		LogUtility.getPumpMLogger().debug("[Pump M] Investigate whether there is more UPOSMessage to be sent to CAT M.rlt="+rlt) ;	
		
		return uposMsg ;
	}
	
	/**
	 * ������ ��� ������ �����Ѵ�.
	 * 
	 * @param nozzleNo	: ���� ��ȣ
	 * @return
	 */
	public QL_WorkingMessage getQLWorkingMessage(String nozzleNo) {
		QL_WorkingMessage qlWorkMsg = null ;
		try {
			DasNoODTNozzleInfo nozInfo = nozzleHash.get(nozzleNo) ;
			qlWorkMsg = nozInfo.getQlWorkMsg() ;
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
		}		
		return qlWorkMsg ;
	}
	
	/**
	 * �����Ϸ� ���� ���� ��û �� ���� ��Ҹ� ���ؼ� UPOSMessage ���� ������ ��û�Ѵ�. ���̾� ������� �� ������ �ӽ� �����Ѵ�.
	 * 
	 * @param nozzleNo	: ���� ��ȣ
	 * @param s4WorkMsg	: ������� ���� �ö�� �����Ϸ� ����
	 * @return
	 */
	public UPOSMessage getUPOSMessageAfterPumping(String nozzleNo, S4_WorkingMessage s4WorkMsg, String khproc_no) {
		LogUtility.getPumpMLogger().info("[Pump M] Create Credit Request UPOSMessage after pumping completed.") ;
		UPOSMessage uPosMsg = null ;
		DasNoODTNozzleInfo nozInfo = nozzleHash.get(nozzleNo) ;
		int nozInfoOptionInt = nozInfo.getOption() ;
		
		switch (nozInfoOptionInt) {
			case IConstant.FULL_PUMPING_OPTION_0  : {
				// ���� �Ϸ� ������ ������ ���� ������ �����ϵ��� �Ѵ�.
				// BL üũ - ���� �Ϸ� ���� ���� ������ ������ ���� ��û
				LogUtility.getPumpMLogger().info("[Pump M] Create UPOSMessage using pumping info.") ;
				WorkingMessage workMsg = createWorkingMessage(nozzleNo, 
						nozInfo.getHeWorkingMsg(), s4WorkMsg.getLiter(), s4WorkMsg.getPrice()) ;
				uPosMsg = ODTUtility_Common.createUPOSMessageFromWorkingMessage(workMsg, khproc_no) ;
				nozInfo.setOptionState(IConstant.SELF_FULLPUMPING_OPTION_0_STATE_2) ;
				
				/**
				 * 2016.03.31 WooChul Jung.
				 * 	move filler1 to selfpayment_type
				 */
				uPosMsg.setSelfPayment_type(IConstant.UPOSMESSAGE_PAID_TYPE_22) ;			// �������� ����� ��û / ����
				
				break ;
			}
			case IConstant.FULL_PUMPING_OPTION_1 : {
				// ���� ��û ���� �� ���� ��� ������ �����ϰ�, ���� ��û ������ �����Ϸ��� �մϴ�.
				// 149900 �� ���� ��û - ���� �Ϸ� ���� ���� �Ϸ� ������ ���� ��û �� �� ���� 149900 �� ��� ��û.
				LogUtility.getPumpMLogger().info("[Pump M] Create Credit and Credit Cancel UPOSMessage, and send Credit Approval to CAT M.") ;
				WorkingMessage approvalWorkMsg = createWorkingMessage(nozzleNo, 
						nozInfo.getHeWorkingMsg(), s4WorkMsg.getLiter(), s4WorkMsg.getPrice()) ;
				WorkingMessage cancelWorkMsg = createWorkingMessage(nozzleNo, nozInfo.getHeWorkingMsg(), true) ;
				
				UPOSMessage cancelUPOSMsg = ODTUtility_Common.createUPOSMessageFromWorkingMessage(cancelWorkMsg, khproc_no) ;
				cancelUPOSMsg.setSelfPayment_type(IConstant.UPOSMESSAGE_PAID_TYPE_21) ;		// �������� ��������� ��û / ����
				
				uPosMsg = ODTUtility_Common.createUPOSMessageFromWorkingMessage(approvalWorkMsg, khproc_no) ;
				uPosMsg.setSelfPayment_type(IConstant.UPOSMESSAGE_PAID_TYPE_22) ;			// �������� ����� ��û / ����
				
				nozInfo.setPendingUPOSMsg(cancelUPOSMsg) ;
				nozInfo.setOptionState(IConstant.SELF_FULLPUMPING_OPTION_1_STATE_2) ;
				
				nozInfo.print() ;
				break ;
			}
			case IConstant.FULL_PUMPING_OPTION_2 : {
				// ���� ��û ���� �� ���� ��� ������ �����ϰ�, ���� ��� ��û ������ �����Ϸ��� �մϴ�.
				// 149900 �� ���� ��û - ���� �Ϸ� ���� 149900 �� ��� ��û �� �� ���� ���� �Ϸ� ������ ���� ��û.
				LogUtility.getPumpMLogger().info("[Pump M] Create Credit and Credit Cancel UPOSMessage, and send Credit Cancel to CAT M.") ;
				WorkingMessage approvalWorkMsg = createWorkingMessage(nozzleNo, nozInfo.getHeWorkingMsg(), s4WorkMsg.getLiter(), s4WorkMsg.getPrice()) ;
				WorkingMessage cancelWorkMsg = createWorkingMessage(nozzleNo, nozInfo.getHeWorkingMsg(), true) ;
				
				uPosMsg = ODTUtility_Common.createUPOSMessageFromWorkingMessage(cancelWorkMsg, khproc_no) ;
				uPosMsg.setSelfPayment_type(IConstant.UPOSMESSAGE_PAID_TYPE_21) ;			// �������� ��������� ��û / ����
				
				UPOSMessage approveUPOSMsg = ODTUtility_Common.createUPOSMessageFromWorkingMessage(approvalWorkMsg, khproc_no) ;
				approveUPOSMsg.setSelfPayment_type(IConstant.UPOSMESSAGE_PAID_TYPE_22) ;	// �������� ����� ��û / ����
				
				nozInfo.setPendingUPOSMsg(approveUPOSMsg) ;		
				nozInfo.setOptionState(IConstant.SELF_FULLPUMPING_OPTION_2_STATE_2) ;
				
				nozInfo.print() ;
				break ;
			}	
			case IConstant.FULL_PUMPING_OPTION_5 : {
				// �����ݾ��� 0 ���̾ ���� ���� ������ ���� ��Ҹ� ��û�Ѵ�.
				LogUtility.getPumpMLogger().info("[Pump M] Create Credit Cancel UPOSMessage, and send Credit Cancel to CAT M.") ;
				WorkingMessage cancelWorkMsg = createWorkingMessage(nozzleNo, nozInfo.getHeWorkingMsg(), true) ;
				
				uPosMsg = ODTUtility_Common.createUPOSMessageFromWorkingMessage(cancelWorkMsg, khproc_no) ;
				uPosMsg.setSelfPayment_type(IConstant.UPOSMESSAGE_PAID_TYPE_11) ;			// �Ϲ� ���� ������ ��� ��û / ����
				
				nozInfo.setOptionState(IConstant.SELF_FULLPUMPING_OPTION_5_STATE_2) ;
				
				nozInfo.print() ;
				break ;
			}	
			case IConstant.FULL_PUMPING_OPTION_6 : {
				// ���� ��û ���� �� ���� ��� ������ �����ϰ�, ���� ��� ��û ������ �����Ϸ��� �մϴ�.
				LogUtility.getPumpMLogger().info("[Pump M] Create Credit and Credit Cancel UPOSMessage, and send Credit Cancel to CAT M.") ;
				WorkingMessage approvalWorkMsg = createWorkingMessage(nozzleNo, nozInfo.getHeWorkingMsg(), s4WorkMsg.getLiter(), s4WorkMsg.getPrice()) ;
				WorkingMessage cancelWorkMsg = createWorkingMessage(nozzleNo, nozInfo.getHeWorkingMsg(), true) ;
				
				uPosMsg = ODTUtility_Common.createUPOSMessageFromWorkingMessage(cancelWorkMsg, khproc_no) ;
				uPosMsg.setSelfPayment_type(IConstant.UPOSMESSAGE_PAID_TYPE_11) ;			// �Ϲ� ���� ������ ��� ��û / ����
				
				UPOSMessage approveUPOSMsg = ODTUtility_Common.createUPOSMessageFromWorkingMessage(approvalWorkMsg, khproc_no) ;
				approveUPOSMsg.setSelfPayment_type(IConstant.UPOSMESSAGE_PAID_TYPE_12) ;	// �Ϲ� ���� ����� ��û / ����
				
				nozInfo.setPendingUPOSMsg(approveUPOSMsg) ;		
				nozInfo.setOptionState(IConstant.SELF_FULLPUMPING_OPTION_6_STATE_2) ;
				
				nozInfo.print() ;
				break ;
			}	
			case IConstant.FULL_PUMPING_OPTION_7 : {
				// �����ݾ��� 0 ���̾ ���� ���� ������ ���� ��Ҹ� ��û�Ѵ�.
				LogUtility.getPumpMLogger().info("[Pump M] Create Credit Cancel UPOSMessage, and send Credit Cancel to CAT M.") ;
				WorkingMessage cancelWorkMsg = createWorkingMessage(nozzleNo, nozInfo.getHeWorkingMsg(), true) ;
				
				uPosMsg = ODTUtility_Common.createUPOSMessageFromWorkingMessage(cancelWorkMsg, khproc_no) ;
				uPosMsg.setSelfPayment_type(IConstant.UPOSMESSAGE_PAID_TYPE_21) ;			// �������� ��������� ��û / ����
				
				nozInfo.setOptionState(IConstant.SELF_FULLPUMPING_OPTION_7_STATE_2) ;
				
				nozInfo.print() ;
				break ;
			}	
			case IConstant.FULL_PUMPING_OPTION_9 : {
				//2016-04 �̰�ȣ 
				// ���� ��û ���� �� ���� ��� ������ �����ϰ�, ���� ��û ������ �����Ϸ��� �մϴ�.
				LogUtility.getLogger().info("[Pump M] Create Credit and Credit Cancel UPOSMessage, and send Credit Approval to CAT M.") ;
				
				WorkingMessage approvalWorkMsg = createWorkingMessage(nozzleNo, nozInfo.getHeWorkingMsg(), s4WorkMsg.getLiter(), s4WorkMsg.getPrice()) ;
				WorkingMessage cancelWorkMsg = createWorkingMessage(nozzleNo, nozInfo.getHeWorkingMsg(), true) ;
				
				UPOSMessage cancelUPOSMsg = ODTUtility_Common.createUPOSMessageFromWorkingMessage(cancelWorkMsg, khproc_no) ;
				cancelUPOSMsg.setSelfPayment_type(IConstant.UPOSMESSAGE_PAID_TYPE_11) ;		// �Ϲ� ���� ��������� ��û / ����
				
				uPosMsg = ODTUtility_Common.createUPOSMessageFromWorkingMessage(approvalWorkMsg, khproc_no) ;
				uPosMsg.setSelfPayment_type(IConstant.UPOSMESSAGE_PAID_TYPE_12) ;			// �Ϲ� ���� ����� ��û / ����
				
				nozInfo.setPendingUPOSMsg(cancelUPOSMsg) ;
				nozInfo.setOptionState(IConstant.SELF_FULLPUMPING_OPTION_9_STATE_2) ;
				
				nozInfo.print() ;
				break ;
			}
		}
		
		LogUtility.getPumpMLogger().info("[Pump M] End - getUPOSMessageAfterPumping ") ;
		
		return uPosMsg ;
	}
	
	/**
	 * �پ��� ���� �ܻ�/����ó�� UPOS�������� ����
	 */
	public UPOSMessage getUPOSMessageAfterPumpingOiSang(String nozzleNo, S4_WorkingMessage s4WorkMsg, String khproc_no){
		LogUtility.getPumpMLogger().info("[Pump M] OiSang/CASH Request UPOSMessage after pumping completed.") ;
		UPOSMessage uPosMsg = null ;
		DasNoODTNozzleInfo nozInfo = nozzleHash.get(nozzleNo) ;
		
		WorkingMessage approvalWorkMsg = createWorkingMessage(nozzleNo, 
				nozInfo.getHeWorkingMsg(), s4WorkMsg.getLiter(), s4WorkMsg.getPrice(), s4WorkMsg.getBasePrice()) ;

		uPosMsg = ODTUtility_Common.createUPOSMessageFromWorkingMessage(approvalWorkMsg, khproc_no) ;
		/**
		 * 2016.03.31 WooChul Jung.
		 * 	move filler1 to selfpayment_type
		 */
		uPosMsg.setSelfPayment_type(IConstant.UPOSMESSAGE_PAID_TYPE_10) ;			// �ܻ��� �Ϲ����� ���� �������� ó���Ѵ�.
		
		return uPosMsg;
	}
	

	/**
	 * �Ҹ����� �پ��� ������ ���� ������ ���� �߻��� ���� ��û ������ ���, ���� ��û�� ���ؼ� UPOSMessage �� �����Ѵ�.
	 * 
	 * @param workMsg	: ������κ��� �ö�� ���� ��û ����
	 * @return
	 */
	public UPOSMessage getUPOSMessageBeforePumping(WorkingMessage workMsg, String khproc_no) {
		UPOSMessage uPosMsg = null ;
		DasNoODTNozzleInfo nozInfo = nozzleHash.get(workMsg.getConnectNozzleNo()) ;
		int nozInfoOptionInt = nozInfo.getOption() ;

		LogUtility.getPumpMLogger().info("[Pump M] Pay processing before Full Pumping, nozInfoOptionInt="+ nozInfoOptionInt) ;
		
		switch (nozInfoOptionInt) {
			case IConstant.FULL_PUMPING_OPTION_0 : {
				// BL üũ���� ���� ����
				LogUtility.getPumpMLogger().info("[Pump M] Create UPOSMessage(0071) for BL Check.") ;
				uPosMsg = ODTUtility_Common.create0071UPOSMessageFromWorkingMessage(workMsg) ;
				nozInfo.setOptionState(IConstant.SELF_FULLPUMPING_OPTION_0_STATE_1) ;
				
				break ;
			}
			case IConstant.FULL_PUMPING_OPTION_1 : {
				// �ſ���� ��û ���� ����
				LogUtility.getPumpMLogger().info("[Pump M] Create Credit UPOSMessage about 149,900 Won.") ;
				uPosMsg = ODTUtility_DaSNo.createUPOSMessageFromWorkingMessage_DaSNo(workMsg, khproc_no, true) ;
				nozInfo.setOptionState(IConstant.SELF_FULLPUMPING_OPTION_1_STATE_1) ;
				
				break ;
			}
			case IConstant.FULL_PUMPING_OPTION_2 : {
				// �ſ���� ��û ���� ����
				LogUtility.getPumpMLogger().info("[Pump M] Create Credit UPOSMessage about 149,900 Won.") ;
				uPosMsg = ODTUtility_DaSNo.createUPOSMessageFromWorkingMessage_DaSNo(workMsg, khproc_no, true) ;
				nozInfo.setOptionState(IConstant.SELF_FULLPUMPING_OPTION_2_STATE_1) ;
				
				break ;
			}
		}
		// debug
		uPosMsg.print() ;
		
		return uPosMsg ;
	}
	
	
	
	/**
	 * ��û�� �����ȣ�� ���� ������ ���� �߻��� ������ ���θ� �����Ѵ�.
	 * 
	 * @param nozzleNo	: ���� ��ȣ
	 * @return
	 */
	public boolean isCurrentFullPumping(String nozzleNo) {
		boolean rlt = false ;
		try {
			DasNoODTNozzleInfo nozInfo = nozzleHash.get(nozzleNo) ;
			if (nozInfo != null) {
				rlt = nozInfo.isFullPumping() ;
			} else {
				rlt = false ;
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage() ,e) ;
		}
		LogUtility.getPumpMLogger().info("[Pump M] " + nozzleNo + " Nozzle is Full-Pumping? " + rlt) ;
		return rlt ;
	}

	/**
	 * ���� �������� �� State �� ������ ���� �������� Ȯ���Ѵ�.
	 * 
	 * @param nozzleNo	: ���� ��ȣ
	 * @return
	 */
	public boolean isFinalResponseUPOSMessageForCurrentFullPumping(String nozzleNo) {
		boolean rlt = false ;
		try {
			DasNoODTNozzleInfo nozInfo = nozzleHash.get(nozzleNo) ;
			if (nozInfo != null) {
				int state = nozInfo.getOptionState() ;
				if (state == IConstant.SELF_FULLPUMPING_FINAL) {
					rlt = true ;
				} else {
					rlt = false ;
				}
			} else {
				rlt = false ;
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
		}
		LogUtility.getPumpMLogger().info("[Pump M] isFinalResponseUPOSMessageForCurrentFullPumping=" + rlt) ;
		return rlt ;
	}
	
	/**
	 * ���� �������� �����Ѵ�. ���� ���� �����̸�, �� ������ �ӽ������Ѵ�.
	 * 	1. ���� ������� ����� �������� Ȯ���Ѵ�.
	 * 	2. ������ ���� �ڷḦ �����Ѵ�.
	 * 	3. ���� �������� �����ϰ� (149,900������ ����), �� ���θ� �ڷῡ �����Ѵ�.
	 * 
	 * @param nozzleNo		: ���� ��ȣ
	 * @param pumpingPrice	: ���� �ݾ�
	 * @return
	 */
	public boolean isFullPumping(String nozzleNo, String pumpingPrice) {
		boolean rlt = false ;		
		try {
			DasNoODTNozzleInfo nozInfo = null ;
			// 1. ���� ������� ����� �������� Ȯ���Ѵ�.
			if (PumpMUtil.isNozzleConnectedToSelfODT(nozzleNo)) {
				// 2. ������ ���� �ڷḦ �����Ѵ�.
				nozzleHash.remove(nozzleNo) ;
				int pumpingPriceInt = Integer.parseInt(GlobalUtility.getStringValue(pumpingPrice)) ;
				// 3. ���� �������� �����ϰ� (149,900������ ����), �� ���θ� �ڷῡ �����Ѵ�.
				if (pumpingPriceInt == FULL_PUMPING_PRICE) {
					nozInfo = new DasNoODTNozzleInfo(nozzleNo, true, FULL_OPTION) ;
					rlt = true ;
				} else {
					nozInfo = new DasNoODTNozzleInfo(nozzleNo, false, IConstant.FULL_PUMPING_OPTION_8) ;
					rlt = false ;
				}
				// �� ������ ������ ���� �������� �����Ѵ�.
				nozzleHash.put(nozzleNo, nozInfo) ;
			} else {
				rlt = false ;
			}		
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage() , e) ;
		}
		LogUtility.getPumpMLogger().info("[Pump M] is Pumping? nozzleNo=" + nozzleNo +",rlt="+rlt) ;
		return rlt ;
	}
	
	/**
	 * �����Ϸ� ���θ� �����Ѵ�.
	 * 
	 * @param nozzleNo	: ���� ��ȣ
	 * @return
	 */
	public boolean isPumpingCompleted(String nozzleNo) {
		boolean rlt = false ;
		try {
			DasNoODTNozzleInfo nozInfo = nozzleHash.get(nozzleNo) ;
			rlt =  nozInfo.isPumpingCompleted() ;
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage() ,e) ;
		}
		return rlt ;
	}
	
	/**
	 * �پ��� ������ KH ó����ȣ ���� ������ �� �Լ��� ȣ���Ͽ� ���� ������ �����ϰ� �����ϵ��� �Ѵ�.
	 * 
	 * @param nozzleNo		: ���� ��ȣ
	 */
	public void removeODTNozzleInfo(String nozzleNo) {
		try {
			if (PumpMUtil.isNozzleConnectedToSelfODT(nozzleNo)) {
				nozzleHash.remove(nozzleNo) ;
			} 	
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage() , e) ;
		}
	}
	
	/**
	 * ���� ������ ������ ������ �޾����� �����Ѵ�.
	 *  
	 * @param nozzleNo
	 */
	public void setFullPumpingStateFinal(String nozzleNo) {
		try {
			DasNoODTNozzleInfo nozInfo = nozzleHash.get(nozzleNo) ;
			nozInfo.setOptionState(IConstant.SELF_FULLPUMPING_FINAL) ;
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
		}
	}
	
	/**
	 * �����Ϸ� ���θ� �����Ѵ�.
	 * 
	 * @param nozzleNo 		: ���� ��ȣ
	 * @param isCompleted	: �����Ϸ� ����
	 */
	public void setPumpingCompleted(String nozzleNo, boolean isCompleted) {
		try {
			DasNoODTNozzleInfo nozInfo = nozzleHash.get(nozzleNo) ;
			nozInfo.setPumpingCompleted(isCompleted) ;
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
		}
	}

	/**
	 * Pump A �� ������ Print Format �� �����Ѵ�.
	 * 
	 * @param nozzleNo		: ���� ��ȣ
	 * @param heWorkMsg		: QL ����
	 */
	public void setQLWorkingMessage(String nozzleNo, QL_WorkingMessage qlWorkMsg) {
		try {
			DasNoODTNozzleInfo nozInfo = nozzleHash.get(nozzleNo) ;
			nozInfo.setQlWorkMsg(qlWorkMsg) ;
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
		}
	}
	
	/**
	 * ���� ��û ������ �����Ѵ�.
	 * 
	 * @param nozzleNo		: ���� ��ȣ
	 * @param heWorkMsg		: ���� ��û ���� .HE WorkingMessage
	 */
	public void setWorkingMessage(String nozzleNo, HE_WorkingMessage heWorkMsg) {
		try {
			DasNoODTNozzleInfo nozInfo = nozzleHash.get(nozzleNo) ;
			nozInfo.setHeWorkingMsg(heWorkMsg) ;
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
		}
	}
	
	/**
	 * Pump A�� ������ �����ϱ� ���ؼ� QL ������ �������� ���θ� �����Ѵ�.
	 * 
	 * @param uPosMsg	: UPOSMessage
	 * @return
	 */
	public boolean shouldCreateQLWorkMsgAndStore(String nozzleNo, UPOSMessage uPosMsg) {
		boolean rlt = false ;
		try {
			DasNoODTNozzleInfo nozInfo = nozzleHash.get(nozzleNo) ;
			int nozInfoOptionInt = nozInfo.getOption() ;
			nozInfo.print() ;
			
			switch (nozInfoOptionInt) {
				case IConstant.FULL_PUMPING_OPTION_0 : {
				// �����Ϸ� ���� ������� ���
					rlt = true ;
					break ;
				}
				/*
				case IPumpMConstant.FULL_PUMPING_OPTION_1 : {
					DasNoODTNozzleInfo odtNozInfo = nozzleHash.get(nozzleNo) ;
					odtNozInfo.print() ;
					if (odtNozInfo.getOptionState() == IPumpMConstant.SELF_FULLPUMPING_OPTION_1_STATE_2) {
					// ������� ���
						rlt = true ;
					}
					break ;
				}
				case IPumpMConstant.FULL_PUMPING_OPTION_2 : {
					DasNoODTNozzleInfo odtNozInfo = nozzleHash.get(nozzleNo) ;
					odtNozInfo.print() ;
					if (odtNozInfo.getOptionState() == IPumpMConstant.SELF_FULLPUMPING_OPTION_2_STATE_3) {
					// �� ��� ���� ������� ���
						rlt = true ;
					}
					break ;
				}
				case IPumpMConstant.FULL_PUMPING_OPTION_5 : {
				// ����� �� ���
					rlt = true ;
					break ;
				}
				case IPumpMConstant.FULL_PUMPING_OPTION_6 : {
					DasNoODTNozzleInfo odtNozInfo = nozzleHash.get(nozzleNo) ;
					odtNozInfo.print() ;
					if (odtNozInfo.getOptionState() == IPumpMConstant.SELF_FULLPUMPING_OPTION_6_STATE_3) {
					// �� ��� ���� ������� ���
						rlt = true ;
					}
					break ;
				}
				*/
				
				// ksm 2012.03.21 
				// DasNoODTNozzleInfo odtNozInfo = nozzleHash.get(nozzleNo) ;  ���� ó��. ���ʿ���.
				// ù���ο��� �о�� DasNoODTNozzleInfo nozInfo = nozzleHash.get(nozzleNo) ; �� ��ü����.
				case IConstant.FULL_PUMPING_OPTION_1 : {
					if (nozInfo.getOptionState() == IConstant.SELF_FULLPUMPING_OPTION_1_STATE_2) {
					// ������� ���
						rlt = true ;
					}
					break ;
				}
				case IConstant.FULL_PUMPING_OPTION_2 : {
					if (nozInfo.getOptionState() == IConstant.SELF_FULLPUMPING_OPTION_2_STATE_3) {
					// �� ��� ���� ������� ���
						rlt = true ;
					}
					break ;
				}
				case IConstant.FULL_PUMPING_OPTION_5 : {
				// ����� �� ���
					rlt = true ;
					break ;
				}
				case IConstant.FULL_PUMPING_OPTION_6 : {
					if (nozInfo.getOptionState() == IConstant.SELF_FULLPUMPING_OPTION_6_STATE_3) {
					// �� ��� ���� ������� ���
						rlt = true ;
					}
					break ;
				}
				case IConstant.FULL_PUMPING_OPTION_7 : {
					// ����� �� ���
					rlt = true ;
					break ;
				}
				//2016-04 �̰�ȣ
				case IConstant.FULL_PUMPING_OPTION_9 : {
					if (nozInfo.getOptionState() == IConstant.SELF_FULLPUMPING_OPTION_9_STATE_2) {
					// �Ϲ����� �½����ΰ�� 
						rlt = true ;
					}
					break ;
				}
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
		}
		LogUtility.getPumpMLogger().info("[Pump M] Check whether this UPOSMessage should be sent to Pump A. rlt=" + rlt) ;		
		return rlt ;
	}
	
}
