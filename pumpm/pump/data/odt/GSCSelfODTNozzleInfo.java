package com.gsc.kixxhub.module.pumpm.pump.data.odt;

import java.util.ArrayList;

import com.gsc.kixxhub.common.data.IConstant;
import com.gsc.kixxhub.common.data.pump.GA_WorkingMessage;
import com.gsc.kixxhub.common.data.upos.UPOSMessage;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.module.pumpm.pump.manager.PumpMODTSaleManager;
import com.gsc.kixxhub.module.pumpm.pump.util.PumpLogUtil;

public class GSCSelfODTNozzleInfo {

	private UPOSMessage firstRespondUPOSMsg = null;
	private GA_WorkingMessage gaWorkingMsg		= null;		// ����ODT �� Original ��û ����
	// (Default) �Ϲ� ����
	private boolean isBonusCard	= false; 			// ���ʽ� ī�� ����
	private boolean isCash 			= false;		// ���� ����
	private boolean isCashCard	= false; 			// ���ݿ����� ī�� ����

	private boolean isCreditCard 	= false;		// �ſ�ī�� ����
	private boolean isCustCard 		= false;		// �ŷ�ó ī�� ����

	private boolean 	isFullPumping 			= false; 	// ���� ������ ���� ����
	private boolean 	isPumpingCompleted 		= false; 	// �����Ϸ� �ߴ��� ����

	private String 		nozzleNo 				= null;		// ���� ��ȣ
	private int option			 = IConstant.FULL_PUMPING_OPTION_8; 			// ����, ���, ����ν� Option

	private int optionState 	= IConstant.SELF_FULLPUMPING_OPTION_8_STATE_1; 	// ����, ���, ����ν� ���� ����
	private UPOSMessage pendingUPOSMsg			= null;		// �����Ϸ�� �����(or �����ݾ׽���) ���� ������ �� ����
	private ArrayList<UPOSMessage> respondUposMsgArray = new ArrayList<UPOSMessage>(); // ���� ����

	public GSCSelfODTNozzleInfo(String nozzleNo, boolean isFullPumping, int option) {
		setNozzleNo(nozzleNo);
		setFullPumping(isFullPumping);
		setOption(option);
		optionState = IConstant.SELF_FULLPUMPING_OPTION_8_STATE_1;

		pendingUPOSMsg	= null;
		gaWorkingMsg 		= null;
		isBonusCard 			= false;
		respondUposMsgArray = new ArrayList<UPOSMessage>();

		//LogUtility.getPumpMLogger().info(new StringBuffer("[Pump M] nozzleNo=").append(nozzleNo + ":isFullPumping?").append(isFullPumping + ":option=").append(option).toString());
		/**
		 * �� Construct �� ȣ�͵Ǵ� ���� ���ο� ���������� ���ֵȴٴ� ���̴�. �׷� ���� ���� �������� �ʱ�ȭ �Ѵ�.
		 */

		PumpMODTSaleManager.initSaleContent(nozzleNo);
	}

	public void addRespondUPOSMessage(UPOSMessage uPosMsg) {
		respondUposMsgArray.add(uPosMsg);
	}

	public UPOSMessage getFirstRespondUPOSMsg() {
		return firstRespondUPOSMsg;
	}

	public GA_WorkingMessage getGaWorkingMsg() {
		return gaWorkingMsg;
	}

	public String getNozzleNo() {
		return nozzleNo;
	}

	public int getOption() {
		return option;
	}

	public int getOptionState() {
		return optionState;
	}

	public UPOSMessage getPendingUPOSMsg() {
		return pendingUPOSMsg;
	}

	public ArrayList<UPOSMessage> getRespondedUPOSMessageArray() {
		return respondUposMsgArray;
	}

	public boolean isBonusCard() {
		return isBonusCard;
	}

	public boolean isCash() {
		return isCash;
	}

	public boolean isCashCard() {
		return isCashCard;
	}

	public boolean isCreditCard() {
		return isCreditCard;
	}

	public boolean isCustCard() {
		return isCustCard;
	}

	public boolean isFullPumping() {
		return isFullPumping;
	}

	public boolean isPumpingCompleted() {
		return isPumpingCompleted;
	}

	public void print() {
			String isPendingUPOSMsg = "0";
			String isGaWorkingMsg = "0";
			String isFirstUPOSMsg = "0";

			if (gaWorkingMsg != null) {
				isGaWorkingMsg = "1";
			}
			if (pendingUPOSMsg != null) {
				isPendingUPOSMsg = "1";
			}
			if (firstRespondUPOSMsg != null) {
				isFirstUPOSMsg = "1";
			}
			LogUtility.getPumpMLogger().debug(new StringBuffer("[Pump M-ODTNozzleInfo]").append(" ").append("#").append("state=")
									.append( optionState).append("#").append("isFullPumping=")
									.append( isFullPumping).append("#")
									.append( "isPumpingCompleted=")
									.append( isPumpingCompleted).append("#").append("nozzleNo=")
									.append( nozzleNo).append("#").append("option=").append(option).append("#")
									.append( "isWorkingMsg=").append(isGaWorkingMsg).append("#")
									.append( "isPendingUPOSMsg=").append(isPendingUPOSMsg)
									.append( "isFirstUPOSMsg=").append(isFirstUPOSMsg).append("#")
									.append( "isBonusCard=").append(isBonusCard).append("#")
									.append( "isCashCard=").append(isCashCard).append("#")
									.append( "isCash=").append(isCash).append("#").append("isCustCard=")
									.append( isCustCard).append("#").append("isCreditCard=")
									.append( isCreditCard).toString());
			PumpLogUtil.printContent(gaWorkingMsg);
			// PumpLogUtil.printContent(pendingUPOSMsg) ;
			// PumpLogUtil.printContent(qlWorkMsg) ;
			PumpLogUtil.printContent(firstRespondUPOSMsg);
			/*
			 * if ((respondUposMsgArray != null) && (respondUposMsgArray.size() >
			 * 0)) { for (int i = 0 ; i < respondUposMsgArray.size() ; i++) {
			 * PumpLogUtil.printContent(respondUposMsgArray.get(i)) ; } }
			 */
			LogUtility.getPumpMLogger().debug("[Pump M-ODTNozzleInfo] End");
	}

	public void setBonusCard(boolean isBonusCard) {
		this.isBonusCard = isBonusCard;
	}

	public void setCash(boolean isCash) {
		this.isCash = isCash;
	}

	public void setCashCard(boolean isCashCard) {
		this.isCashCard = isCashCard;
	}

	public void setCreditCard(boolean isCreditCard) {
		this.isCreditCard = isCreditCard;
	}

	public void setCustCard(boolean isCustCard) {
		this.isCustCard = isCustCard;
	}

	public void setFirstRespondUPOSMsg(UPOSMessage firstRespondUPOSMsg) {
		LogUtility.getPumpMLogger().info("[Pump M] First UPOSMessage for " + nozzleNo);
		this.firstRespondUPOSMsg = firstRespondUPOSMsg;
	}

	public void setFullPumping(boolean isFullPumping) {
		this.isFullPumping = isFullPumping;
	}

	public void setGaWorkingMsg(GA_WorkingMessage workingMsg) {

		this.gaWorkingMsg = workingMsg;
		
		try {
			if (workingMsg != null) {
				// GSC �����κ����� ��û������ GA ������ ���ʽ� ī�� ��ȣ�� �ִ����� �����Ѵ�.
				String bonusCard	= workingMsg.getBonusCard();
				isBonusCard			= !GlobalUtility.isNullOrEmptyString(bonusCard);
				isCashCard 			= !GlobalUtility.isNullOrEmptyString(workingMsg.getCashReceiptNo());
				isCustCard 			= !GlobalUtility.isNullOrEmptyString(workingMsg.getCustCardNo());
				isCreditCard 		= !GlobalUtility.isNullOrEmptyString(workingMsg.getCardNumber());
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		}
	}

	public void setNozzleNo(String nozzleNo) {
		this.nozzleNo = nozzleNo;
	}

	public void setOption(int option) {
		this.option = option;
	}

	public void setOptionState(int state) {
		this.optionState = state;
		LogUtility.getPumpMLogger().info(new StringBuffer("[Pump M] NozID=").append(nozzleNo + ":state=").append(state).toString());
	}

	public void setPendingUPOSMsg(UPOSMessage pendingWorkingMsg) {
		this.pendingUPOSMsg = pendingWorkingMsg;
	}

	public void setPumpingCompleted(boolean isPumpingCompleted) {
		this.isPumpingCompleted = isPumpingCompleted;
	}
}
