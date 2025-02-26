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
	 * 승인(or 승인취소) 요청 전문을 생성한다.
	 * 
	 * @param nozzleNo		: 노즐 번호
	 * @param orgWorkMsg	: 주유기로부터 올라온 요청 전문
	 * @param isCancel		: 승인 or 취소 여부
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
	 * 주유리터와 주유금액에 따른 WorkingMessage 를 생성한다.
	 * 
	 * @param nozzleNo		: 노즐 번호
	 * @param orgWorkMsg	: 주유기로 부터 올라온 승인 요청 전문
	 * @param liter			: 리터
	 * @param pumpingPrice	: 금액
	 * @return
	 */
	private static WorkingMessage createWorkingMessage(String nozzleNo, WorkingMessage orgWorkMsg, 
			String liter, String pumpingPrice) {
		
		WorkingMessage workMsg= createWorkingMessage(nozzleNo, orgWorkMsg, liter, pumpingPrice, null);
		
		return workMsg;
	}
	
	/**
	 * 주유리터와 주유금액에 따른 WorkingMessage 를 생성한다.
	 * 
	 * @param nozzleNo		: 노즐 번호
	 * @param orgWorkMsg	: 주유기로 부터 올라온 승인 요청 전문
	 * @param liter			: 리터
	 * @param pumpingPrice	: 금액
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
	 * 	selfodt.fullpumping.option 의 의미는 아래와 같다. 이 Class 는 다쓰노 셀프에서만 사용된다.
	 *		0	: 	BlackList check -> Request for Approval
	 *		1	:	149,900 Won Request for Approval -> After pumping completion, request real pumping price for Approval -> 149,900 Won request for cancel
	 *		2	:	149,900 Won Request for Approval -> After pumping completion, 149,900 Won request for cancel -> request real pumping price for Approval
	 */
	private int FULL_OPTION = IConstant.FULL_PUMPING_OPTION_0 ;
	
	/**
	 * nozzleHash 에서 관리하고 있는 ODTNozzleInfo Object 는 다음의 경우 새로 생성된다.
	 * 	1. 다쓰노 셀프에서 최초 결재 요청시 생성된다.
	 * 	2. POS 로 부터 Preset 인 경우에도 새로 생성이 된다.
	 * 그리고 다음의 경우 제거한다
	 * 	1. 새로운 KH 처리번호 생성시
	 * ODTNozzleInfo Object 는 가득 주유 여부를 관리하고 있으며, 가득 주유인 경우 관련 정보들을 갱신한다.
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
	 * 응답 전문을 저장한다. 이 응답 전문들은 차후 영수증을 찍기 위해서 사용되어 질 수 있다.
	 * 
	 * @param nozzleNo		: 노즐 번호
	 * @param uPosMsg		: 응답 UPOSMessage
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
	 * 일반 주유인 경우 option1 => 승승취  option2 => 승취승  옵션에 따라서 결제 방식 적용되도록 처리 
	 * @param nozzleNo		: 노즐 번호
	 */
	//2016-04 이강호
	public void changeDasNoODTNozzleInfoLikeFullPumpingWithOption(String nozzleNo) {
		int option = 0;
		//selfodt.fullpumping.option 값을 확인하여 1인 경우 승승취가 되도록 
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
	 * 일반 주유이지만 재승인 최소 금액 보다 크기 때문에 선취소, 재승인을 하기위해서 정보를 변경한다.
	 * 
	 * @param nozzleNo		: 노즐 번호
	 */
	public void changeDasNoODTNozzleInfoLikeFullPumpingWithOption6(String nozzleNo) {
		try {
			LogUtility.getPumpMLogger().info("[Pump M] changeDasNoODTNozzleInfoLikeFullPumpingWithOption6. nozID=" +nozzleNo) ;
			DasNoODTNozzleInfo nozInfo = nozzleHash.get(nozzleNo) ;
			nozInfo.setOption(IConstant.FULL_PUMPING_OPTION_6) ;				// 가득 주유 Option 2 를 따르도록 한다.
			nozInfo.setFullPumping(true) ;																			// 가득 주유로 설정한다.
			nozInfo.setPumpingCompleted(true) ;																// 주유완료 되었음을 재차 설정한다.
			nozInfo.setOptionState(IConstant.SELF_FULLPUMPING_OPTION_6_STATE_1) ;	// 가득 주유 Option 2 의 State 1 을 이미 행한것으로 설정한다.
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
		}			
	}
	
	/**
	 * [일반 주유] 재승인 최소 금액 보다 큰 경우 재승인, 선승인을 하기위해서 정보를 변경한다.
	 * 
	 * @param nozzleNo		: 노즐 번호
	 */
	public void changeDasNoODTNozzleInfoLikeFullPumpingWithOption9(String nozzleNo) {
		try {
			LogUtility.getLogger().info("[Pump M] changeDasNoODTNozzleInfoLikePumpingWithOption9. nozID=" +nozzleNo) ;
			DasNoODTNozzleInfo nozInfo = nozzleHash.get(nozzleNo) ;
			nozInfo.setOption(IConstant.FULL_PUMPING_OPTION_9) ;				// 가득 주유 Option 1 를 따르도록 한다.
			nozInfo.setFullPumping(true) ;																			// 가득 주유로 설정한다.
			nozInfo.setPumpingCompleted(true) ;																// 주유완료 되었음을 재차 설정한다.
			nozInfo.setOptionState(IConstant.SELF_FULLPUMPING_OPTION_9_STATE_1) ;	// 가득 주유 Option 1 의 State 1 을 이미 행한것으로 설정한다.
		} catch (Exception e) {
			LogUtility.getLogger().error(e.getMessage(), e) ;
		}			
	}
	
	/**
	 * [일반주유] 주유완료 금액이 0 원인 경우 처리 : (승인 -> (주유완료 0원인 경우) -> 선승인 취소)
	 * 
	 * @param nozzleNo
	 */
	public void changeDasNoODTNozzleInfoWithOption5(String nozzleNo) {
		try {
			LogUtility.getPumpMLogger().info("[Pump M] changeDasNoODTNozzleInfoWithOption5. nozID=" +nozzleNo) ;
			DasNoODTNozzleInfo nozInfo = nozzleHash.get(nozzleNo) ;
			nozInfo.setOption(IConstant.FULL_PUMPING_OPTION_5) ;			// Option 5  를 따르도록 한다.
			nozInfo.setFullPumping(true) ;																		// 가득 주유로 설정한다.
			nozInfo.setPumpingCompleted(true) ;															// 주유완료 되었음을 재차 설정한다.
			nozInfo.setOptionState(IConstant.SELF_FULLPUMPING_OPTION_5_STATE_1) ;	// Option 5  의 State 1 을 이미 행한것으로 설정한다.
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
		}			
	}
	
	/**
	 * [가득주유] 주유완료 금액이 0 원인 경우 처리 : (승인 -> (주유완료 0원인 경우) -> 선승인 취소)
	 * 
	 * @param nozzleNo
	 */
	public void changeDasNoODTNozzleInfoWithOption7(String nozzleNo) {
		try {
			LogUtility.getPumpMLogger().info("[Pump M] changeDasNoODTNozzleInfoWithOption7. nozID=" +nozzleNo) ;
			DasNoODTNozzleInfo nozInfo = nozzleHash.get(nozzleNo) ;
			nozInfo.setOption(IConstant.FULL_PUMPING_OPTION_7) ;			// Option 7  를 따르도록 한다.
			nozInfo.setFullPumping(true) ;																		// 가득 주유로 설정한다.
			nozInfo.setPumpingCompleted(true) ;															// 주유완료 되었음을 재차 설정한다.
			nozInfo.setOptionState(IConstant.SELF_FULLPUMPING_OPTION_7_STATE_1) ;	// Option 7  의 State 1 을 이미 행한것으로 설정한다.
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
		}			
	}
	
	/**
	 * [2008.11.21] by WooChul Jung
	 * 다쓰노 셀프의 경우 POS 로 부터 Preset 이 전송된다. 이 경우 이전의 자료를 제거하고 새롭게 이 주유건이 가득 주유가 아님을
	 * 표시한다. 동탄 신도시 주유소의 경우 아래와 같은 문제가 발생하였다.
	 * 		1. 주유건-A : 손님 A 가 가득 주유하여 주유완료
	 * 		2  주유건-B : 손님 B 가 POS 에서 정액/정량 설정 하여 주유 완료
	 * 			이때 아래 코드가 없었을 당시 주유건-B 가 완료되었을때 손님 A 의 카드로 결재가 진행되었다.
	 * 
	 * @param nozzleNo	: 노즐 번호
	 * @param isFull	: 가득 주유 여부
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
	 * CAT M 으로 부터 응답전문을 받은 이후 추가로 보내어야 할 전문이 있는지를 조사한다. ( 이 함수는 주유 완료 이후 호출된다.)
	 * 
	 * @param nozzleNo	: 노즐 번호
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
					//2016-04 이강호
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
		 * Pending 전문은 더 이상 사용할 필요가 없기 때문에 제거한다.
		 */
		if (nozInfo != null) {
			nozInfo.setPendingUPOSMsg(null) ;
		}
		
		LogUtility.getPumpMLogger().debug("[Pump M] Investigate whether there is more UPOSMessage to be sent to CAT M.rlt="+rlt) ;	
		
		return uposMsg ;
	}
	
	/**
	 * 영수증 출력 전문을 생성한다.
	 * 
	 * @param nozzleNo	: 노즐 번호
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
	 * 주유완료 이후 승인 요청 및 승인 취소를 위해서 UPOSMessage 전문 생성을 요청한다. 연이어 보내어야 할 전문은 임시 저장한다.
	 * 
	 * @param nozzleNo	: 노즐 번호
	 * @param s4WorkMsg	: 주유기로 부터 올라온 주유완료 전문
	 * @return
	 */
	public UPOSMessage getUPOSMessageAfterPumping(String nozzleNo, S4_WorkingMessage s4WorkMsg, String khproc_no) {
		LogUtility.getPumpMLogger().info("[Pump M] Create Credit Request UPOSMessage after pumping completed.") ;
		UPOSMessage uPosMsg = null ;
		DasNoODTNozzleInfo nozInfo = nozzleHash.get(nozzleNo) ;
		int nozInfoOptionInt = nozInfo.getOption() ;
		
		switch (nozInfoOptionInt) {
			case IConstant.FULL_PUMPING_OPTION_0  : {
				// 주유 완료 정보를 가지고 결제 전문을 생성하도록 한다.
				// BL 체크 - 주유 완료 이후 주유 정보를 가지고 승인 요청
				LogUtility.getPumpMLogger().info("[Pump M] Create UPOSMessage using pumping info.") ;
				WorkingMessage workMsg = createWorkingMessage(nozzleNo, 
						nozInfo.getHeWorkingMsg(), s4WorkMsg.getLiter(), s4WorkMsg.getPrice()) ;
				uPosMsg = ODTUtility_Common.createUPOSMessageFromWorkingMessage(workMsg, khproc_no) ;
				nozInfo.setOptionState(IConstant.SELF_FULLPUMPING_OPTION_0_STATE_2) ;
				
				/**
				 * 2016.03.31 WooChul Jung.
				 * 	move filler1 to selfpayment_type
				 */
				uPosMsg.setSelfPayment_type(IConstant.UPOSMESSAGE_PAID_TYPE_22) ;			// 가득주유 재승인 요청 / 응답
				
				break ;
			}
			case IConstant.FULL_PUMPING_OPTION_1 : {
				// 승인 요청 전문 및 승인 취소 전문을 생성하고, 승인 요청 전문을 전송하려고 합니다.
				// 149900 원 승인 요청 - 주유 완료 이후 주유 완료 정보로 승인 요청 및 그 이후 149900 원 취소 요청.
				LogUtility.getPumpMLogger().info("[Pump M] Create Credit and Credit Cancel UPOSMessage, and send Credit Approval to CAT M.") ;
				WorkingMessage approvalWorkMsg = createWorkingMessage(nozzleNo, 
						nozInfo.getHeWorkingMsg(), s4WorkMsg.getLiter(), s4WorkMsg.getPrice()) ;
				WorkingMessage cancelWorkMsg = createWorkingMessage(nozzleNo, nozInfo.getHeWorkingMsg(), true) ;
				
				UPOSMessage cancelUPOSMsg = ODTUtility_Common.createUPOSMessageFromWorkingMessage(cancelWorkMsg, khproc_no) ;
				cancelUPOSMsg.setSelfPayment_type(IConstant.UPOSMESSAGE_PAID_TYPE_21) ;		// 가득주유 선승인취소 요청 / 응답
				
				uPosMsg = ODTUtility_Common.createUPOSMessageFromWorkingMessage(approvalWorkMsg, khproc_no) ;
				uPosMsg.setSelfPayment_type(IConstant.UPOSMESSAGE_PAID_TYPE_22) ;			// 가득주유 재승인 요청 / 응답
				
				nozInfo.setPendingUPOSMsg(cancelUPOSMsg) ;
				nozInfo.setOptionState(IConstant.SELF_FULLPUMPING_OPTION_1_STATE_2) ;
				
				nozInfo.print() ;
				break ;
			}
			case IConstant.FULL_PUMPING_OPTION_2 : {
				// 승인 요청 전문 및 승인 취소 전문을 생성하고, 승인 취소 요청 전문을 전송하려고 합니다.
				// 149900 원 승인 요청 - 주유 완료 이후 149900 원 취소 요청 및 그 이후 주유 완료 정보로 승인 요청.
				LogUtility.getPumpMLogger().info("[Pump M] Create Credit and Credit Cancel UPOSMessage, and send Credit Cancel to CAT M.") ;
				WorkingMessage approvalWorkMsg = createWorkingMessage(nozzleNo, nozInfo.getHeWorkingMsg(), s4WorkMsg.getLiter(), s4WorkMsg.getPrice()) ;
				WorkingMessage cancelWorkMsg = createWorkingMessage(nozzleNo, nozInfo.getHeWorkingMsg(), true) ;
				
				uPosMsg = ODTUtility_Common.createUPOSMessageFromWorkingMessage(cancelWorkMsg, khproc_no) ;
				uPosMsg.setSelfPayment_type(IConstant.UPOSMESSAGE_PAID_TYPE_21) ;			// 가득주유 선승인취소 요청 / 응답
				
				UPOSMessage approveUPOSMsg = ODTUtility_Common.createUPOSMessageFromWorkingMessage(approvalWorkMsg, khproc_no) ;
				approveUPOSMsg.setSelfPayment_type(IConstant.UPOSMESSAGE_PAID_TYPE_22) ;	// 가득주유 재승인 요청 / 응답
				
				nozInfo.setPendingUPOSMsg(approveUPOSMsg) ;		
				nozInfo.setOptionState(IConstant.SELF_FULLPUMPING_OPTION_2_STATE_2) ;
				
				nozInfo.print() ;
				break ;
			}	
			case IConstant.FULL_PUMPING_OPTION_5 : {
				// 주유금액이 0 원이어서 승인 응답 전문에 대한 취소만 요청한다.
				LogUtility.getPumpMLogger().info("[Pump M] Create Credit Cancel UPOSMessage, and send Credit Cancel to CAT M.") ;
				WorkingMessage cancelWorkMsg = createWorkingMessage(nozzleNo, nozInfo.getHeWorkingMsg(), true) ;
				
				uPosMsg = ODTUtility_Common.createUPOSMessageFromWorkingMessage(cancelWorkMsg, khproc_no) ;
				uPosMsg.setSelfPayment_type(IConstant.UPOSMESSAGE_PAID_TYPE_11) ;			// 일반 주유 선승인 취소 요청 / 응답
				
				nozInfo.setOptionState(IConstant.SELF_FULLPUMPING_OPTION_5_STATE_2) ;
				
				nozInfo.print() ;
				break ;
			}	
			case IConstant.FULL_PUMPING_OPTION_6 : {
				// 승인 요청 전문 및 승인 취소 전문을 생성하고, 승인 취소 요청 전문을 전송하려고 합니다.
				LogUtility.getPumpMLogger().info("[Pump M] Create Credit and Credit Cancel UPOSMessage, and send Credit Cancel to CAT M.") ;
				WorkingMessage approvalWorkMsg = createWorkingMessage(nozzleNo, nozInfo.getHeWorkingMsg(), s4WorkMsg.getLiter(), s4WorkMsg.getPrice()) ;
				WorkingMessage cancelWorkMsg = createWorkingMessage(nozzleNo, nozInfo.getHeWorkingMsg(), true) ;
				
				uPosMsg = ODTUtility_Common.createUPOSMessageFromWorkingMessage(cancelWorkMsg, khproc_no) ;
				uPosMsg.setSelfPayment_type(IConstant.UPOSMESSAGE_PAID_TYPE_11) ;			// 일반 주유 선승인 취소 요청 / 응답
				
				UPOSMessage approveUPOSMsg = ODTUtility_Common.createUPOSMessageFromWorkingMessage(approvalWorkMsg, khproc_no) ;
				approveUPOSMsg.setSelfPayment_type(IConstant.UPOSMESSAGE_PAID_TYPE_12) ;	// 일반 주유 재승인 요청 / 응답
				
				nozInfo.setPendingUPOSMsg(approveUPOSMsg) ;		
				nozInfo.setOptionState(IConstant.SELF_FULLPUMPING_OPTION_6_STATE_2) ;
				
				nozInfo.print() ;
				break ;
			}	
			case IConstant.FULL_PUMPING_OPTION_7 : {
				// 주유금액이 0 원이어서 승인 응답 전문에 대한 취소만 요청한다.
				LogUtility.getPumpMLogger().info("[Pump M] Create Credit Cancel UPOSMessage, and send Credit Cancel to CAT M.") ;
				WorkingMessage cancelWorkMsg = createWorkingMessage(nozzleNo, nozInfo.getHeWorkingMsg(), true) ;
				
				uPosMsg = ODTUtility_Common.createUPOSMessageFromWorkingMessage(cancelWorkMsg, khproc_no) ;
				uPosMsg.setSelfPayment_type(IConstant.UPOSMESSAGE_PAID_TYPE_21) ;			// 가득주유 선승인취소 요청 / 응답
				
				nozInfo.setOptionState(IConstant.SELF_FULLPUMPING_OPTION_7_STATE_2) ;
				
				nozInfo.print() ;
				break ;
			}	
			case IConstant.FULL_PUMPING_OPTION_9 : {
				//2016-04 이강호 
				// 승인 요청 전문 및 승인 취소 전문을 생성하고, 승인 요청 전문을 전송하려고 합니다.
				LogUtility.getLogger().info("[Pump M] Create Credit and Credit Cancel UPOSMessage, and send Credit Approval to CAT M.") ;
				
				WorkingMessage approvalWorkMsg = createWorkingMessage(nozzleNo, nozInfo.getHeWorkingMsg(), s4WorkMsg.getLiter(), s4WorkMsg.getPrice()) ;
				WorkingMessage cancelWorkMsg = createWorkingMessage(nozzleNo, nozInfo.getHeWorkingMsg(), true) ;
				
				UPOSMessage cancelUPOSMsg = ODTUtility_Common.createUPOSMessageFromWorkingMessage(cancelWorkMsg, khproc_no) ;
				cancelUPOSMsg.setSelfPayment_type(IConstant.UPOSMESSAGE_PAID_TYPE_11) ;		// 일반 주유 선승인취소 요청 / 응답
				
				uPosMsg = ODTUtility_Common.createUPOSMessageFromWorkingMessage(approvalWorkMsg, khproc_no) ;
				uPosMsg.setSelfPayment_type(IConstant.UPOSMESSAGE_PAID_TYPE_12) ;			// 일반 주유 재승인 요청 / 응답
				
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
	 * 다쓰노 셀프 외상/현금처리 UPOS응답전문 생성
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
		uPosMsg.setSelfPayment_type(IConstant.UPOSMESSAGE_PAID_TYPE_10) ;			// 외상은 일반주유 승인 응답으로 처리한다.
		
		return uPosMsg;
	}
	

	/**
	 * 소모셀프와 다쓰노 셀프의 가득 주유로 인해 발생된 승인 요청 전문인 경우, 승인 요청을 위해서 UPOSMessage 를 생성한다.
	 * 
	 * @param workMsg	: 주유기로부터 올라온 승인 요청 전문
	 * @return
	 */
	public UPOSMessage getUPOSMessageBeforePumping(WorkingMessage workMsg, String khproc_no) {
		UPOSMessage uPosMsg = null ;
		DasNoODTNozzleInfo nozInfo = nozzleHash.get(workMsg.getConnectNozzleNo()) ;
		int nozInfoOptionInt = nozInfo.getOption() ;

		LogUtility.getPumpMLogger().info("[Pump M] Pay processing before Full Pumping, nozInfoOptionInt="+ nozInfoOptionInt) ;
		
		switch (nozInfoOptionInt) {
			case IConstant.FULL_PUMPING_OPTION_0 : {
				// BL 체크용의 전문 생성
				LogUtility.getPumpMLogger().info("[Pump M] Create UPOSMessage(0071) for BL Check.") ;
				uPosMsg = ODTUtility_Common.create0071UPOSMessageFromWorkingMessage(workMsg) ;
				nozInfo.setOptionState(IConstant.SELF_FULLPUMPING_OPTION_0_STATE_1) ;
				
				break ;
			}
			case IConstant.FULL_PUMPING_OPTION_1 : {
				// 신용결제 요청 전문 생성
				LogUtility.getPumpMLogger().info("[Pump M] Create Credit UPOSMessage about 149,900 Won.") ;
				uPosMsg = ODTUtility_DaSNo.createUPOSMessageFromWorkingMessage_DaSNo(workMsg, khproc_no, true) ;
				nozInfo.setOptionState(IConstant.SELF_FULLPUMPING_OPTION_1_STATE_1) ;
				
				break ;
			}
			case IConstant.FULL_PUMPING_OPTION_2 : {
				// 신용결제 요청 전문 생성
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
	 * 요청한 노즐번호가 가득 주유로 인해 발생된 것인지 여부를 조사한다.
	 * 
	 * @param nozzleNo	: 노즐 번호
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
	 * 가득 주유에서 현 State 가 마지막 응답 전문인지 확인한다.
	 * 
	 * @param nozzleNo	: 노즐 번호
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
	 * 가득 주유인지 조사한다. 만약 가득 주유이면, 이 정보를 임시저장한다.
	 * 	1. 셀프 주유기와 연결된 노즐인지 확인한다.
	 * 	2. 노즐의 이전 자료를 삭제한다.
	 * 	3. 가득 주유인지 조사하고 (149,900원인지 조사), 그 여부를 자료에 저장한다.
	 * 
	 * @param nozzleNo		: 노즐 번호
	 * @param pumpingPrice	: 주유 금액
	 * @return
	 */
	public boolean isFullPumping(String nozzleNo, String pumpingPrice) {
		boolean rlt = false ;		
		try {
			DasNoODTNozzleInfo nozInfo = null ;
			// 1. 셀프 주유기와 연결된 노즐인지 확인한다.
			if (PumpMUtil.isNozzleConnectedToSelfODT(nozzleNo)) {
				// 2. 노즐의 이전 자료를 삭제한다.
				nozzleHash.remove(nozzleNo) ;
				int pumpingPriceInt = Integer.parseInt(GlobalUtility.getStringValue(pumpingPrice)) ;
				// 3. 가득 주유인지 조사하고 (149,900원인지 조사), 그 여부를 자료에 저장한다.
				if (pumpingPriceInt == FULL_PUMPING_PRICE) {
					nozInfo = new DasNoODTNozzleInfo(nozzleNo, true, FULL_OPTION) ;
					rlt = true ;
				} else {
					nozInfo = new DasNoODTNozzleInfo(nozzleNo, false, IConstant.FULL_PUMPING_OPTION_8) ;
					rlt = false ;
				}
				// 현 노즐의 주유가 가득 주유임을 설정한다.
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
	 * 주유완료 여부를 조사한다.
	 * 
	 * @param nozzleNo	: 노즐 번호
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
	 * 다쓰노 셀프의 KH 처리번호 새로 생성시 이 함수를 호출하여 기존 정보를 안전하게 제거하도록 한다.
	 * 
	 * @param nozzleNo		: 노즐 번호
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
	 * 가득 주유의 마지막 전문을 받았음을 설정한다.
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
	 * 주유완료 여부를 설정한다.
	 * 
	 * @param nozzleNo 		: 노즐 번호
	 * @param isCompleted	: 주유완료 여부
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
	 * Pump A 로 전송할 Print Format 을 저장한다.
	 * 
	 * @param nozzleNo		: 노즐 번호
	 * @param heWorkMsg		: QL 전문
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
	 * 승인 요청 전문을 저장한다.
	 * 
	 * @param nozzleNo		: 노즐 번호
	 * @param heWorkMsg		: 승인 요청 전문 .HE WorkingMessage
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
	 * Pump A로 전문을 전송하기 위해서 QL 전문을 생성할지 여부를 조사한다.
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
				// 주유완료 이후 재승인인 경우
					rlt = true ;
					break ;
				}
				/*
				case IPumpMConstant.FULL_PUMPING_OPTION_1 : {
					DasNoODTNozzleInfo odtNozInfo = nozzleHash.get(nozzleNo) ;
					odtNozInfo.print() ;
					if (odtNozInfo.getOptionState() == IPumpMConstant.SELF_FULLPUMPING_OPTION_1_STATE_2) {
					// 재승인인 경우
						rlt = true ;
					}
					break ;
				}
				case IPumpMConstant.FULL_PUMPING_OPTION_2 : {
					DasNoODTNozzleInfo odtNozInfo = nozzleHash.get(nozzleNo) ;
					odtNozInfo.print() ;
					if (odtNozInfo.getOptionState() == IPumpMConstant.SELF_FULLPUMPING_OPTION_2_STATE_3) {
					// 선 취소 이후 재승인인 경우
						rlt = true ;
					}
					break ;
				}
				case IPumpMConstant.FULL_PUMPING_OPTION_5 : {
				// 선취소 인 경우
					rlt = true ;
					break ;
				}
				case IPumpMConstant.FULL_PUMPING_OPTION_6 : {
					DasNoODTNozzleInfo odtNozInfo = nozzleHash.get(nozzleNo) ;
					odtNozInfo.print() ;
					if (odtNozInfo.getOptionState() == IPumpMConstant.SELF_FULLPUMPING_OPTION_6_STATE_3) {
					// 선 취소 이후 재승인인 경우
						rlt = true ;
					}
					break ;
				}
				*/
				
				// ksm 2012.03.21 
				// DasNoODTNozzleInfo odtNozInfo = nozzleHash.get(nozzleNo) ;  삭제 처리. 불필요함.
				// 첫라인에서 읽어온 DasNoODTNozzleInfo nozInfo = nozzleHash.get(nozzleNo) ; 로 대체가능.
				case IConstant.FULL_PUMPING_OPTION_1 : {
					if (nozInfo.getOptionState() == IConstant.SELF_FULLPUMPING_OPTION_1_STATE_2) {
					// 재승인인 경우
						rlt = true ;
					}
					break ;
				}
				case IConstant.FULL_PUMPING_OPTION_2 : {
					if (nozInfo.getOptionState() == IConstant.SELF_FULLPUMPING_OPTION_2_STATE_3) {
					// 선 취소 이후 재승인인 경우
						rlt = true ;
					}
					break ;
				}
				case IConstant.FULL_PUMPING_OPTION_5 : {
				// 선취소 인 경우
					rlt = true ;
					break ;
				}
				case IConstant.FULL_PUMPING_OPTION_6 : {
					if (nozInfo.getOptionState() == IConstant.SELF_FULLPUMPING_OPTION_6_STATE_3) {
					// 선 취소 이후 재승인인 경우
						rlt = true ;
					}
					break ;
				}
				case IConstant.FULL_PUMPING_OPTION_7 : {
					// 선취소 인 경우
					rlt = true ;
					break ;
				}
				//2016-04 이강호
				case IConstant.FULL_PUMPING_OPTION_9 : {
					if (nozInfo.getOptionState() == IConstant.SELF_FULLPUMPING_OPTION_9_STATE_2) {
					// 일반주유 승승취인경우 
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
