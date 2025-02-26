package com.gsc.kixxhub.module.pumpm.pump.manager;

import java.util.Hashtable;

import com.gsc.kixxhub.common.data.ICode;
import com.gsc.kixxhub.common.data.IConstant;
import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.posdata.POSHeader;
import com.gsc.kixxhub.common.data.posdata.POS_DW;
import com.gsc.kixxhub.common.dbadapter.common.KHUtility;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_KEYSHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_PUMP_TRHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_KEYSData;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_PUMP_TRData;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.module.pumpm.pump.manager.dasno.DasNoSelfPumpingManager;
import com.gsc.kixxhub.module.pumpm.pump.manager.gsc.GSCSelfPumpingManager;
import com.gsc.kixxhub.module.pumpm.pump.util.PumpLogUtil;
import com.gsc.kixxhub.module.pumpm.pump.util.PumpMObjectValidation;
import com.gsc.kixxhub.module.pumpm.pump.util.PumpMUtil;
import com.gsc.kixxhub.module.pumpm.pump.util.UPOSUtil;

public class PumpMTransactionManager {
	
	class TransactionData {
		private String CorporateBouns = ""; //����ī�尡 ����ī���϶� ���ʽ�ī���ȣ   
		//�̰�ȣ 2014-07 ����ī�庸�ʽ���������
		private boolean CorporateCardYn = false; //����ī�忩��  
		private int currState = IPumpConstant.KH_PUMP_DEFAULT ;		// ����, ������, ����, Preset
		private String custCardNo = "";
		private int[] dataState = new int[IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED + 1] ;
		private boolean isPayed = false ;
		private String khTransactionID = "" ;						// KH ó����ȣ
		private String nozID = "" ;									// ���� ��ȣ
		private int presetFrom = IPumpConstant.PRESET_FROM_NONE ;	// �������� ������ ���� ������ �Ǿ��°� ([POS | non-POS])
		
		public TransactionData(String nozID , String khTransactionID , int state) {
			this.nozID = nozID ;
			this.khTransactionID = khTransactionID ;
			this.currState = state ;
			for (int i = 0 ; i < (IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED + 1) ; i++) {
				this.dataState[i] = 0 ;
			}
			this.dataState[state] = 1 ;
		}
		
		
		public String getCorporateBouns() {
			return CorporateBouns;
		}

		public int getCurrState() {
			return currState;
		}
		
		public String getCustCardNo() {
			return custCardNo;
		}

		public String getKhTransactionID() {
			return khTransactionID;
		}

		public String getNozID() {
			return nozID;
		}

		public int getPresetFrom() {
			return presetFrom;
		}

		public int getState(int state) {
			return this.dataState[state] ;
		}

		public boolean isCorporateCardYn() {
			return CorporateCardYn;
		}
		
		public boolean isPayed() {
			return isPayed;
		}

		public void print() {
			String sumState = "" ;
			for (int i = 0 ; i < dataState.length ; i++) {
				sumState = sumState + dataState[i] ;
			}
				
			LogUtility.getPumpMLogger().info(new StringBuffer("[TransactionData]").append(" ")
					.append("#").append("nozID=").append(nozID ) 
					.append("#").append("khTransactionID=").append(khTransactionID ) 
					.append("#").append("currState=").append(currState ) 
					.append("#").append("presetFrom=").append(presetFrom )
					.append("#").append("isPayed=").append(isPayed )
					.append("#").append("sumState=").append(sumState)
					.append("#").append("custCardNo=" ).append(custCardNo)
					.append("#").append("CorporateCardYn=" ).append(CorporateCardYn)
					.append("#").append("CorporateBouns=" ).append(CorporateBouns).toString()) ;
		}

		/**
		 * TransactionData ���� �ִ� �����͸� �ʱ�ȭ �Ѵ�. �̴� �����ϱ� �����̴�.
		 * 
		 * @param nozID
		 * @param khTransactionID
		 */
		public void reset(String nozID , String khTransactionID) {
			this.nozID = nozID ;
			this.khTransactionID = khTransactionID ;
			this.currState = IPumpConstant.KH_PUMP_DEFAULT ;
			this.presetFrom = IPumpConstant.PRESET_FROM_NONE ;
			this.isPayed = false ;
			this.custCardNo = "";
			for (int i = 0 ; i < (IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED + 1) ; i++) {
				this.dataState[i] = 0 ;
			}

			this.CorporateCardYn = false;
			this.CorporateBouns = "";
		}
		
		public void setCorporateBouns(String corporateBouns) {
			CorporateBouns = corporateBouns;
		}

		public void setCorporateCardYn(boolean corporateCardYn) {
			CorporateCardYn = corporateCardYn;
		}


		public void setCurrState(int state) {
			this.currState = state;
			this.dataState[state] = 1 ;
			
			if ((state == IPumpConstant.KH_PUMP_COMPLETED) || (state == IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED)) {
				print() ;
			}
		}


		public void setCustCardNo(String custCardNo) {
			this.custCardNo = custCardNo;
		}


		public void setKhTransactionID(String khTransactionID) {
			this.khTransactionID = khTransactionID;
		}


		public void setNozID(String nozID) {
			this.nozID = nozID;
		}
		
		
		public void setPayed(boolean isPayed) {
			this.isPayed = isPayed;
		}


		public void setPresetFrom(int presetFrom) {
			this.presetFrom = presetFrom;
			setCurrState(IPumpConstant.KH_PUMP_PRESET) ;
		}

		
	}
	class TransactionDataManager {
		public Hashtable<String, TransactionData> transactionDataHash = 
			new Hashtable<String, TransactionData>() ;		// ����� TransactionData �� �����ϴ� Hash ����
		
		/**
		 * ���� ó�� �������� KH ó����ȣ�� �����Ǿ��� ��� �� �Լ��� ȣ���Ͽ� TransactionData �� �����Ѵ�.
		 * �� ������ ���ο� KH ó����ȣ�� ������ �Ǹ� �̹� ������ TransactionData �� �����Ѵ�.
		 * 
		 * @param nozID				: ���� ��ȣ
		 * @param khTransactionID	: KH ó����ȣ
		 * @param state				: ���� ����
		 * @return
		 */
		public TransactionData createTransactionData(String nozID, String khTransactionID, int state) {
			TransactionData data = new TransactionData(nozID , khTransactionID , state) ;
			transactionDataHash.put(nozID, data) ;
			return data ;
		}
		
		/**
		 * ���� ������ TransactionData �� ��û�Ѵ�.
		 * 
		 * @param nozID		: ���� ��ȣ
		 * @return
		 */
		public TransactionData getTransactionData(String nozID) {
			TransactionData data = transactionDataHash.get(nozID) ;
			return data ;
		}
		
		public boolean hasState(String nozID, int state) {
			TransactionData data = transactionDataHash.get(nozID) ;
			return (data.getState(state) == 1) ? true : false ;
		}
		
		/**
		 * �����Ϸ��̸鼭 �� �������� POS �� Preset �� ���� ������ ���� ���� ����.
		 * @param nozID
		 * @return
		 */
		public boolean isPumpingCompletedByPOSPreset(String nozID) {
			boolean rlt = false ;
			try {
				TransactionData data = transactionDataHash.get(nozID) ;
				if (data.getPresetFrom() == IPumpConstant.PRESET_FROM_POS) {
					if (data.getState(IPumpConstant.KH_PUMP_COMPLETED) == 1) {
						rlt = true ;
					}
				}
			} catch (Exception e) {
				LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
			}
			return rlt ;
		}
		
		public void printTransactionData(String nozID) {
			TransactionData data = transactionDataHash.get(nozID) ;
			if (data != null) data.print() ;
		}
	}
	
	/**
	 * �� �����ǿ� ���� State �� �����ϰ� �ִ�. �̿� ���� �ʱ�ȭ�� ���ο� ������ �߻��� ���� �������� �ʱ�ȭ �Ѵ�.
	 */
	private static PumpMTransactionManager transactionManager = null ;			// SingleTon ����
	
	public static void destroy() {
		if (transactionManager != null) {
			transactionManager = null ;
		}
	}
	
	public static PumpMTransactionManager getInstance() {
		if (transactionManager == null) {
			transactionManager = new PumpMTransactionManager() ;
		}
		return transactionManager ;
	}
	
	public static void init() {
		if (transactionManager == null) {
			transactionManager = new PumpMTransactionManager() ;
		}
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		LogUtility.getPumpMLogger().debug(getInstance().getKHTransactionID("1" , 0)) ;
		LogUtility.getPumpMLogger().debug(getInstance().getKHTransactionID("1" , 1)) ;
		LogUtility.getPumpMLogger().debug(getInstance().getKHTransactionID("1" , 2)) ;
		LogUtility.getPumpMLogger().debug(getInstance().getKHTransactionID("1" , 0)) ;
		LogUtility.getPumpMLogger().debug(getInstance().getKHTransactionID("1" , 1)) ;
		LogUtility.getPumpMLogger().debug(getInstance().getKHTransactionID("1" , 2)) ;
	}

	private TransactionDataManager dataManager = new TransactionDataManager() ;	
				// TransactionData �� �����ϰ� �ִ� Manager ����
	/**
	 * �� �������� ����ī�尡 ����ī���϶� ���ʽ�ī���ȣ ��û�Ѵ�.
	 * 
	 * @param nozID		: ���� ��ȣ
	 * @return
	 */
	public String getCorporateBonus(String nozID) {
		String corporateBonus = "" ;
		try {
			TransactionData data = dataManager.getTransactionData(nozID) ;
			if (data != null) {
				corporateBonus = data.getCorporateBouns() ;
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		return corporateBonus ;
	}
	
	/**
	 * �� ������ ���¸� ��ȯ�Ѵ�.
	 * 
	 * @param nozID		: ���� ��ȣ
	 * @param state		: ���� ����
	 * @return
	 */
	public int getCurrState(String nozID) {
		int currState = IPumpConstant.KH_PUMP_DEFAULT ;
		try {
			TransactionData data =  dataManager.getTransactionData(nozID) ;
			if (data != null) {
				currState = data.getCurrState() ;
			}

		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		return currState ;
	}	

	/**
	 * ������ - �� ī�� ��ȣ�� �����Ѵ�.
	 * @param nozID
	 */
	public String getCustCardNumber(String nozID){
		TransactionData data = dataManager.getTransactionData(nozID) ;
		
		return data.getCustCardNo();
		
	}
	
	/**
	 * �پ��� Self ODT �� KH ó����ȣ�� ��û�Ѵ�.
	 * 
	 * @param nozID		: ���� ��ȣ
	 * @param state		: ��û�� ����
	 * @return
	 */
	private String getDasNoSelfODTKHTransactionID(String nozID , int state) {
		String khproc_no = null ;
		
		TransactionData data = dataManager.getTransactionData(nozID) ;

		int preState = IPumpConstant.KH_PUMP_DEFAULT ;
		
		if (state == IPumpConstant.KH_STATE_RESET) {
			state = IPumpConstant.KH_PUMP_DEFAULT ;
			khproc_no = getNewKHTransactionID(nozID) ;
			data = dataManager.getTransactionData(nozID) ;
			LogUtility.getPumpMLogger().info("[Pump M] Reset ��û�� ó���մϴ�." + "nozzle=" + nozID + "# NewKHTransactionID.Create(" + khproc_no + ")");			
		} else if (data != null) {
			preState = data.getCurrState() ;
			
			/**
			 * [2008.11.17] ȸ�� by �ڵ�ȭ ����, ������ ����, ����ö
			 * 
			 * ������ ��� ���ο� KH ó����ȣ�� �����ϵ��� �Ѵ�.
			 * 		1. �Ǹ� �Ϸ� �� ���
			 * 			- Normal �� ���
			 * 		2. �� �� ������ �Ϸ� �����̸�, �� ���°� Preset �� ��� (POS �� ���� Preset �� ���)
			 * 			- ODT ���� �ܵ� �Ǹ� �ϴ� ���
			 * 		3. �� �� ������ �Ϸ��̰�, POS �� ���� Preset �� ���� ��쿴����, �� ���°� ���� ��û�� ���
			 * 			- ODT ���� �ܵ� �Ǹ��ϴٰ�, KH �� �������� �ʰ� ODT �� ���� ���� ���(�ſ� �������� ��Ȳ��)
			 */
			if ((preState == IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED) || 
					((state == IPumpConstant.KH_PUMP_PRESET) && 
					  (preState == IPumpConstant.KH_PUMP_COMPLETED)) || 
					((state == IPumpConstant.KH_ODT_PAID_REQ) && 
					  (preState == IPumpConstant.KH_PUMP_COMPLETED) && 
					  dataManager.isPumpingCompletedByPOSPreset(nozID))){
				khproc_no = getNewKHTransactionID(nozID) ;
			} else {
				khproc_no = data.getKhTransactionID() ;
			}
		} else {
			khproc_no = getNewKHTransactionID(nozID) ;
			data = dataManager.getTransactionData(nozID) ;
		}
		
		PumpLogUtil.printStateChange(nozID, preState, state, khproc_no) ;
		
		data.setCurrState(state) ;
		data.setKhTransactionID(khproc_no) ;
		
		return khproc_no ;	
	}

	/**
	 * GSC Self ODT �� KH ó����ȣ�� ��û�Ѵ�.
	 * 
	 * @param nozID		: ���� ��ȣ
	 * @param state		: ��û�� ����
	 * @return
	 */
	private String getGSCSelfODTKHTransactionID(String nozID , int state) {
		String khproc_no = null ;
		
		TransactionData data = dataManager.getTransactionData(nozID) ;

		int preState = IPumpConstant.KH_PUMP_DEFAULT ;
		
		if (data != null) {
			preState = data.getCurrState() ;
			
			/**
			 * [2008.11.17] ȸ�� by �ڵ�ȭ ����, ������ ����, ����ö
			 * 
			 * ������ ��� ���ο� KH ó����ȣ�� �����ϵ��� �Ѵ�.
			 * 		1. �Ǹ� �Ϸ� �� ���
			 * 			- Normal �� ���
			 * 		2. �� �� ������ �Ϸ� �����̸�, �� ���°� Preset �� ��� (POS �� ���� Preset �� ���)
			 * 			- ODT ���� �ܵ� �Ǹ� �ϴ� ���
			 * 		3. �� �� ������ �Ϸ��̰�, POS �� ���� Preset �� ���� ��쿴����, �� ���°� ���� ��û�� ���
			 * 			- ODT ���� �ܵ� �Ǹ��ϴٰ�, KH �� �������� �ʰ� ODT �� ���� ���� ���(�ſ� �������� ��Ȳ��)
			 */
			if ((preState == IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED) || 
					((state == IPumpConstant.KH_PUMP_PRESET) && (preState == IPumpConstant.KH_PUMP_COMPLETED)) || 
					((state == IPumpConstant.KH_ODT_PAID_REQ) && (preState == IPumpConstant.KH_PUMP_COMPLETED) && dataManager.isPumpingCompletedByPOSPreset(nozID))){
				khproc_no = getNewKHTransactionID(nozID) ;
			} else {
				khproc_no = data.getKhTransactionID() ;
			}
		} else {
			khproc_no = getNewKHTransactionID(nozID) ;
			data = dataManager.getTransactionData(nozID) ;
		}
		
		LogUtility.getLogger().info(" preState : {} " + preState);
		
		PumpLogUtil.printStateChange(nozID, preState, state, khproc_no) ;
		
		data.setCurrState(state) ;
		data.setKhTransactionID(khproc_no) ;
		
		return khproc_no ;	
	}
	
	/**
	 * ���� ������ ó����ȣ�� ��û�Ѵ�.
	 * 
	 * @param nozID	: ���� ��ȣ
	 * @return
	 */
	public synchronized String getKHTransactionID(String nozID) {
		String khproc_no = null ;
		try {
				TransactionData data = dataManager.getTransactionData(nozID) ;
				if (data == null) {
					getNewKHTransactionID(nozID) ;
				}
				khproc_no = data.getKhTransactionID() ;
				if (khproc_no == null) {
					khproc_no = getKHTransactionID(nozID, IPumpConstant.KH_PUMP_START) ;
				}
				LogUtility.getPumpMLogger().debug("[Pump M] nozID(" + nozID + ") : KHID ("+khproc_no+").");
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		return khproc_no ;
	}
	
	
	/**
	 * ���� ���� KH ó����ȣ�� ��û�Ѵ�. 
	 * 
	 * @param nozID	: ���� ��ȣ
	 * @param state	: �� ������ ���� ����
	 * @return
	 * 
	 */
	public String getKHTransactionID(String nozID , int state) {
		String khproc_no = null ;
		
		int protocolType = PumpMUtil.getConnectedRepODTProtocolFromNozzleNo(nozID) ;
		switch (protocolType) {
			case IPumpConstant.PUMP_PROTOCOL_SOMO :  {
				khproc_no = getSoMoSelfODTKHTransactionID(nozID, state) ;
				break ; 
			}
			case IPumpConstant.PUMP_PROTOCOL_DaSNo : 
			case IPumpConstant.PUMP_PROTOCOL_NewDaSNo :{ 	//2012.09.26 ksm
				
				khproc_no = getDasNoSelfODTKHTransactionID(nozID, state) ;
				break ; 
			}
			case IPumpConstant.PUMP_PROTOCOL_GSC_SELF :{ 	//2015.12.24 CWI
				khproc_no = getGSCSelfODTKHTransactionID(nozID, state) ;
				break ; 
			}
			case IPumpConstant.PUMP_PROTOCOL_NewRecharge :  //2016.03.28 ������ 
			case IPumpConstant.PUMP_PROTOCOL_Recharge : {
				khproc_no = getRechargeODTKHTransactionID(nozID, state) ;
				break ; 
			}
			case IPumpConstant.PUMP_PROTOCOL_DEFAULT : {
				khproc_no = getPumpKHTransactionID(nozID, state) ;
				break ; 				
			}
		}		

		return khproc_no ;
	}
	
	/**
	 * �ܸ��⿡�� ��û�� ��� ���� ������ ó����ȣ�� ��û�Ѵ�.
	 * 
	 * @param nozID	: ���� ��ȣ
	 * @return
	 */
	public synchronized String getKHTransactionIDWithoutCreation(String nozID) {
		String khproc_no = null ;
		try {
				TransactionData data = dataManager.getTransactionData(nozID) ;
				if (data != null) {
					khproc_no = data.getKhTransactionID() ;
				}
				LogUtility.getPumpMLogger().debug("[Pump M] nozID(" + nozID + ") : KHID ("+khproc_no+").");
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		return khproc_no ;
	}
	
	
	/**
	 * ���� ������ ���ο� ó����ȣ�� ��û�Ѵ�.
	 * 2008:10:06 �߰��� �Լ�
	 * 	POS �� ���� ���� �ð��� KixxHub �� ������ ���, KixxHub �� �ý��� �ð��� �����Ѵ�.
	 * 	�� ��� ���� �ð��� ���������ν� KH ó����ȣ�� ������ ��ȣ�� �����Ǵ� ��찡 �ִ�. �̸� �����ϱ� ���ؼ� KH ó����ȣ�� �����ϴ��� ���θ�
	 * 	üũ�Ѵ�. 
	 * 
	 * @param nozID	: ���� ��ȣ
	 * @return
	 */
	private synchronized String getNewKHTransactionID(String nozID) {
		String khproc_no = null ;
		
		khproc_no = KHUtility.getNewKHTransactionID() ;
		
		LogUtility.getPumpMLogger().debug("[Pump M] Set nozID(" + nozID + ") : New KHID ("+khproc_no+").");
		
		try {
			T_KH_PUMP_TRHandler.getHandler().insertT_KH_PUMP_TRDataByKHKeyAndNozzleNo(khproc_no, nozID) ;
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		
		// TransactionData �ʱ�ȭ
		TransactionData data = dataManager.getTransactionData(nozID);
		if (data == null) {
			dataManager.createTransactionData(nozID, khproc_no, IPumpConstant.KH_PUMP_DEFAULT) ;
		} else {
			data.reset(nozID, khproc_no) ;
		}
		
		try {
			int nozProtocolInt = PumpMUtil.getConnectedRepODTProtocolFromNozzleNo(nozID) ;
			
			switch (nozProtocolInt) {
				case IPumpConstant.PUMP_PROTOCOL_SOMO : {

					break ;
				}
				case IPumpConstant.PUMP_PROTOCOL_DaSNo : 
				case IPumpConstant.PUMP_PROTOCOL_NewDaSNo :{	//2012.09.26 ksm
					DasNoSelfPumpingManager.getInstance().removeODTNozzleInfo(nozID) ;
					break ;
				}
				case IPumpConstant.PUMP_PROTOCOL_GSC_SELF :{	//2016.03.23 CWI
					GSCSelfPumpingManager.getInstance().removeODTNozzleInfo(nozID) ;
					break ;
				}
				case IPumpConstant.PUMP_PROTOCOL_Recharge : {

					break ;
				}
				default : {
				}
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		
		return khproc_no ;
	}
	
	/**
	 * ���� �� �������� ��� �� �� ���¿� ���Ͽ� ���� ������ ��ģ��.
	 * Pump Adapter �� ���� ���� ���� ���� ���� �� �����Ͱ� ���� ���, ���� ���� ������ ��ü �����Ѵ�.
	 * 
	 * @param nozID	: ���� ��ȣ
	 * @param state	: ���� ���� (�����߸� �ü� �ִ�.)
	 * @return
	 */
	public String getPumpingKHTransactionID(String nozID , int state) {
		String khproc_no = null ;
		
		TransactionData data = dataManager.getTransactionData(nozID) ;

		if (data != null) {
			int preState = data.getCurrState() ;
			switch (state) {
				case IPumpConstant.KH_PUMPING : {
					if ((preState == IPumpConstant.KH_PUMPING) || (preState == IPumpConstant.KH_PUMP_START)){
						khproc_no = data.getKhTransactionID();
					} else {
						LogUtility.getPumpMLogger().info("[Pump M] Create Fake Start Pump Info.") ;
						khproc_no = PumpMUtil.createFakePumpStartContent(nozID) ; 
					} 
					break ;
				}
				default : {
					khproc_no = data.getKhTransactionID() ;
					break ;
				}
			}
		} else {
			khproc_no = getNewKHTransactionID(nozID) ;
			data = dataManager.getTransactionData(nozID) ;
		}
		
		data.setCurrState(state) ;
		data.setKhTransactionID(khproc_no) ;
		
		return khproc_no ;	
	}
	
	/**
	 * �������� KH ó����ȣ�� ��û�Ѵ�.
	 * 
	 * @param nozID		: ���� ��ȣ
	 * @param state		: ��û�� ����
	 * @return
	 */
	public String getPumpKHTransactionID(String nozID , int state) {
		String khproc_no = null ;
		
		TransactionData data = dataManager.getTransactionData(nozID) ;

		int preState = IPumpConstant.KH_PUMP_DEFAULT ;
		
		if (state == IPumpConstant.KH_STATE_RESET) {
			state = IPumpConstant.KH_PUMP_DEFAULT ;
			khproc_no = getNewKHTransactionID(nozID) ;
			data = dataManager.getTransactionData(nozID) ;
			LogUtility.getPumpMLogger().info("[Pump M] Reset ��û�� ó���մϴ�." + "nozzle=" + nozID + "# NewKHTransactionID.Create(" + khproc_no + ")");			
		} else if (data != null) {
			preState = data.getCurrState() ;
			
			if (preState == IPumpConstant.KH_PUMP_COMPLETED) {
				khproc_no = getNewKHTransactionID(nozID) ;
			} else {
				khproc_no = data.getKhTransactionID() ;
			}
		} else {
			khproc_no = getNewKHTransactionID(nozID) ;
			data = dataManager.getTransactionData(nozID) ;
		}

		PumpLogUtil.printStateChange(nozID, preState, state, khproc_no) ;

		data.setCurrState(state) ;
		data.setKhTransactionID(khproc_no) ;
		
		return khproc_no ;	
	}
	
	/**
	 * �������� KH ó����ȣ�� ��û�Ѵ�.
	 * 
	 * @param nozID		: ���� ��ȣ
	 * @param state		: ��û�� ����
	 * @return
	 */
	private String getRechargeODTKHTransactionID(String nozID , int state) {
		String khproc_no = null ;
		String custCardNo = "";
		
		TransactionData data = dataManager.getTransactionData(nozID) ;

		int preState = IPumpConstant.KH_PUMP_DEFAULT ;
		
		if (state == IPumpConstant.KH_STATE_RESET) {
			state = IPumpConstant.KH_PUMP_DEFAULT ;
			khproc_no = getNewKHTransactionID(nozID) ;
			data = dataManager.getTransactionData(nozID) ;
			LogUtility.getPumpMLogger().info("[Pump M] Reset ��û�� ó���մϴ�." + "nozzle=" + nozID + "# NewKHTransactionID.Create(" + khproc_no + ")");			
		} else if (data != null) {
			preState = data.getCurrState() ;
			
			if (preState == IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED ) {
				khproc_no = getNewKHTransactionID(nozID) ;
				
				custCardNo = "";
//				LogUtility.getPumpMLogger().debug("[TEST][PumpM] ���ֳ�??? ����� custCardNo="+custCardNo);
			} else {
				khproc_no = data.getKhTransactionID() ;
				
				POSHeader posObj = PumpMODTSaleManager.getCustPOSPumpM(nozID) ;
				if ((posObj != null) && (posObj instanceof POS_DW)) {
					POS_DW dwPosMsg = (POS_DW) posObj ;
					
					if (PumpMObjectValidation.validatePumpMObject(dwPosMsg)) {
						custCardNo = dwPosMsg.getCust_code();
//						LogUtility.getPumpMLogger().debug("[TEST][PumpM] ���Է��ϳ�??? custCardNo="+custCardNo);
					}
				}
				
			}
		} else {
			khproc_no = getNewKHTransactionID(nozID) ;
			data = dataManager.getTransactionData(nozID) ;
		}
		
		PumpLogUtil.printStateChange(nozID, preState, state, khproc_no) ;
		
		data.setCurrState(state) ;
		data.setKhTransactionID(khproc_no) ;
		
		data.setCustCardNo(custCardNo);
		
		
		return khproc_no ;	
	}
	
	/**
	 * �Ҹ� Self ODT �� KH ó����ȣ�� ��û�Ѵ�.
	 * 
	 * @param nozID		: ���� ��ȣ
	 * @param state		: ��û�� ����
	 * @return
	 */
	private String getSoMoSelfODTKHTransactionID(String nozID , int state) {
		String khproc_no = null ;
		
		TransactionData data = dataManager.getTransactionData(nozID) ;
		
		int preState = IPumpConstant.KH_PUMP_DEFAULT ;
		
		if (state == IPumpConstant.KH_STATE_RESET) {
			state = IPumpConstant.KH_PUMP_DEFAULT ;
			khproc_no = getNewKHTransactionID(nozID) ;
			data = dataManager.getTransactionData(nozID) ;
			LogUtility.getPumpMLogger().info("[Pump M] Reset ��û�� ó���մϴ�." + "nozzle=" + nozID + "# NewKHTransactionID.Create(" + khproc_no + ")");			
		} else if (data != null) {
			preState = data.getCurrState() ;
			/**
			 * [2008.11.17] ȸ�� by �ڵ�ȭ ����, ������ ����, ����ö
			 * 
			 * ������ ��� ���ο� KH ó����ȣ�� �����ϵ��� �Ѵ�.
			 * 		1. �Ǹ� �Ϸ� �� ���
			 * 			- Normal �� ���
			 * 		2. �� �� ������ �Ϸ� �����̸�, �� ���°� Preset �� ��� (POS �� ���� Preset �� ���)
			 * 			- ODT ���� �ܵ� �Ǹ� �ϴ� ���
			 * 		3. �� �� ������ �Ϸ��̰�, POS �� ���� Preset �� ���� ��쿴����, �� ���°� ���� ��û�� ���
			 * 			- ODT ���� �ܵ� �Ǹ��ϴٰ�, KH �� �������� �ʰ� ODT �� ���� ���� ���(�ſ� �������� ��Ȳ��)
			 */
			if ((preState == IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED) || 
					((state == IPumpConstant.KH_PUMP_PRESET) && (preState == IPumpConstant.KH_PUMP_COMPLETED))
					/*|| 
					((state == IPumpConstant.KH_ODT_PAID_REQ) && 
					  (preState == IPumpConstant.KH_PUMP_COMPLETED) && 
					  dataManager.isPumpingCompletedByPOSPreset(nozID))*/){
				khproc_no = getNewKHTransactionID(nozID) ;
				LogUtility.getPumpMLogger().info("[Pump M] nozzle=" + nozID + "# NewKHTransactionID.Create(" + khproc_no + ")");
			} else {
				khproc_no = data.getKhTransactionID() ;
			}
		} else {
			khproc_no = getNewKHTransactionID(nozID) ;
			data = dataManager.getTransactionData(nozID) ;
			LogUtility.getPumpMLogger().info("[Pump M] data is null." + "nozzle=" + nozID + "# NewKHTransactionID.Create(" + khproc_no + ")");
		}
		
		PumpLogUtil.printStateChange(nozID, preState, state, khproc_no) ;
		
		data.setCurrState(state) ;
		data.setKhTransactionID(khproc_no) ;
		
		return khproc_no ;	
	}
	
	private String getTrackingNO(int trackingNo) {
		
		// 2012.04.24 ksm  �ܸ����Ϸù�ȣ �ߺ��� ���� ����������ȣ ���� + 500
		trackingNo = trackingNo + 500;
		if(trackingNo > 9999) {
			trackingNo = 1;
		}
		
		// ����������ȣ�� 4�ڸ��� �����.
		String no = Integer.toString(trackingNo);
		if(trackingNo < 10) {
			no = "000" + no;
		} else if( trackingNo < 100) {
			no = "00" + no;
		} else  if( trackingNo < 1000) {
			no = "0" + no;
		}
		
		return no;
	}
	
	/**
	 * �� ������ ������ ���� ������ �ִ��� �����Ѵ�.
	 * 
	 * @param nozID		: ���� ��ȣ
	 * @param state		: ���� ����
	 * @return
	 */
	public boolean hasState(String nozID, int state) {
		boolean rlt = false ;
		try {
			rlt =  dataManager.hasState(nozID, state) ;
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		return rlt ;
	}
	
	// 2012.04.24 ksm  CATMProc�� ODTUtil ���� ������.
	//public synchronized String getUniqueValue() {
	public synchronized String increaseTrackingValue() {
		// db���� smt ����������ȣ�� get
		// t_kh_keys ���̺��� ���� (unique�� value�� ������ insert�� �� update�Ѵ�.)
		T_KH_KEYSData data;
		String value = "0001";

//		 ksm
		String UNIQUE = "ODTSALES"; 
		
		try {
			data = T_KH_KEYSHandler.getHandler().getT_KH_KEYSData(UNIQUE);
			
			//test ksm
			LogUtility.getPumpMLogger().debug("[Pump M] �ߺ��߻� ������ȣ : " + data.getValue()) ;
			
			if(data == null || data.getValue() == null || data.getValue().length() == 0) {
				// table�� unique�� ���� insert�� �ȵ� ���.
				T_KH_KEYSHandler.getHandler().insertT_KH_KEYSData(UNIQUE, "0002");
				return value;
			}
			
			// �ܸ����Ϸù�ȣ �ߺ��� ���� ����������ȣ ���� + 500
			value = getTrackingNO(Integer.parseInt( data.getValue()) );
			
//			test ksm
			LogUtility.getPumpMLogger().debug("[[Pump M] ���� ������ȣ : " + value.toString()) ;
			
			// unique�� value�� update
			T_KH_KEYSHandler.getHandler().updateT_KH_KEYSData(UNIQUE, value);
		
		} catch (Exception e) {
			LogUtility.getCATLogger().error(e.getMessage(), e);
			return null;
		}
		return value;
	}

	/**
	 * �� �������� ����ī�尡 ����ī������ ��û�Ѵ�.
	 * 
	 * @param nozID		: ���� ��ȣ
	 * @return
	 */
	public boolean isCorporate(String nozID) {
		boolean isCorporate = false ;
		try {
			TransactionData data = dataManager.getTransactionData(nozID) ;
			if (data != null) {
				isCorporate = data.isCorporateCardYn() ;
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		return isCorporate ;
	}
	
	/**
	 * �� �������� ODT �κ��� ����Ǿ������� ��û�Ѵ�.
	 * 
	 * @param nozID		: ���� ��ȣ
	 * @return
	 */
	public boolean isPayed(String nozID) {
		boolean isPayed = false ;
		try {
			TransactionData data = dataManager.getTransactionData(nozID) ;
			if (data != null) {
				isPayed = data.isPayed() ;
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		return isPayed ;
	}
	
	/**
	 * CAT �� ���� Preset �� ���� ������������ �����Ѵ�.
	 * 
	 * @param nozID		: ���� ��ȣ
	 * @return
	 */
	public boolean isPresetFromCAT(String nozID) {
		boolean rlt = false ;	
		try {
			TransactionData data = dataManager.getTransactionData(nozID) ;
			if (data != null) {
				if (data.getPresetFrom() == IPumpConstant.PRESET_FROM_CAT) {
					rlt = true ;
					LogUtility.getPumpMLogger().info("[Pump M] is preset from CAT.") ;
				}
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		return rlt ;
	}
	
	
	/**
	 * POS �� ���� Preset �� ���� ������������ �����Ѵ�.
	 * 
	 * @param nozID		: ���� ��ȣ
	 * @return
	 */
	public boolean isPresetFromPOS(String nozID) {
		boolean rlt = false ;	
		try {
			TransactionData data = dataManager.getTransactionData(nozID) ;
			if (data != null) {
				if (data.getPresetFrom() == IPumpConstant.PRESET_FROM_POS) {
					rlt = true ;
					LogUtility.getPumpMLogger().info("[Pump M] is preset from POS.") ;
				}
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		return rlt ;
	}
	/**
	 * �������۰� �����Ϸ��� ��� �� �� ���¿� ���Ͽ� ������ ��� true �� �����Ѵ�.
	 * 
	 * @param nozID	: ���� ��ȣ
	 * @param state	: ���� ����
	 * @return
	 */
	public boolean isSameState(String nozID, int state) {
		boolean rlt = false ;		
		TransactionData data = dataManager.getTransactionData(nozID) ;

		if (data != null) {
			int preState = data.getCurrState() ;
			switch (state) {
				case IPumpConstant.KH_PUMP_COMPLETED : {
					if (preState == IPumpConstant.KH_PUMP_COMPLETED) {
						rlt = true ;
					}
					break ;
				}
				case IPumpConstant.KH_PUMP_START : {
					if (preState == IPumpConstant.KH_PUMP_START) {
						rlt = true ;
					}
					break ;
				}
				case IPumpConstant.KH_PUMP_PRESET : {
					if (preState == IPumpConstant.KH_PUMP_PRESET) {
						rlt = true ;
					}
					break ;
				}
				case IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED : {
					if (preState == IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED) {
						rlt = true ;
					}
					break ;
				}
				default : {
					rlt = false ;
					break ;
				}
			}
		} else {
			rlt = false ;
		}
		return rlt ;	
	}
	
	/**
	 * �����Ϸ� �ݾ��� 0���� ��� POS �� �����Ϸ� ������ �������� ���θ� �����Ѵ�. �Ʒ� �����߿� �ϳ��� �ش�Ǹ� �����Ѵ�.
	 * 
	 * 	1. �� �������� ���簡 �Ȱ��̸� �����Ѵ�.
	 * 	2. Self �������ΰ�� POS �� ���� Preset Ȥ�� ����� ���̸� �����ϵ��� �Ѵ�. -> 3���� ���ӵ�. (2008.10.05. ������ : ������, ����ö)
	 * 	3. ������/Semi-Self/�������� ��� , POS �� ���� Preset �� ���� ���̸� �����Ѵ�.
	 * 
	 * @param nozID		: ���� ��ȣ
	 * @return
	 */
	public boolean needToSendToPOS_IFPumpCompleted_ZeroOne(String nozID) {
		LogUtility.getPumpMLogger().info("[Pump M] Completed Pumping Price is Zero. nozID="+ nozID) ;

		boolean rlt = false ;		

		try {
			TransactionData data = dataManager.getTransactionData(nozID) ;
			if (data != null) {
				T_KH_PUMP_TRData trData = T_KH_PUMP_TRHandler.getHandler().getT_KH_PUMP_TRData(data.getKhTransactionID()) ;
					
				if (trData.getOil_paid_ind().equals("1")) {
					LogUtility.getPumpMLogger().info("[Pump M] Already Payed.") ;
					rlt = true ;
				} else {
//					String nozType = T_NZ_NOZZLEHandler.getHandler().getSelf_ind_exist(nozID) ;
					if (data.getPresetFrom() == IPumpConstant.PRESET_FROM_POS) {
						rlt = true ;
						LogUtility.getPumpMLogger().info("[Pump M] is preset from POS.") ;
					}
/*					if (nozType == IPumpConstant.DEVICE_TYPE_SELF_PUMP) {
						rlt = true ;
						LogUtility.getPumpMLogger().info("[Pump M] Self Pump Device.") ;
					} else {
						if (data.getPresetFrom() == IPumpConstant.PRESET_FROM_POS) {
							rlt = true ;
							LogUtility.getPumpMLogger().info("[Pump M] is preset from POS.") ;
						}
					}*/
				}
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		
		LogUtility.getPumpMLogger().info("[Pump M] Completed Pumping Price is Zero. nozID="+ nozID +
				": should be sent to POS=" + rlt) ;
		return rlt ;
	}
	
	
	public void printTransactionData(String nozID) {
		if (dataManager != null)
			dataManager.printTransactionData(nozID) ;
	}
	
	
	/**
	 * ���� KHó����ȣ�� TransactionData�� �����Ѵ�.
	 * CAT�ܸ��⿡�� CAT Preset���߿� �������� ��� KH Data�� �����ϱ� ���� ����Ѵ�.
	 * @param nozID	: ���� ��ȣ
	 * @return
	 */
	public void reset(String nozID, String khproc_no){
		// TransactionData �ʱ�ȭ
		TransactionData data = dataManager.getTransactionData(nozID);
		if (data == null) {
			dataManager.createTransactionData(nozID, khproc_no, IPumpConstant.KH_PUMP_DEFAULT) ;
		} else {
			data.reset(nozID, khproc_no) ;
		}
		
	}
	
	/**
	 * �� �������� ����ī�尡 ����ī������ �����Ѵ�.
	 * 
	 * @param nozID		: ���� ��ȣ
	 */
	public void setCorporate(String nozID, boolean corporateCardYn) {
		try {
			LogUtility.getPumpMLogger().debug("[Pump M] ����ī�弳��: nozID="+nozID+"#corporateCardYn="+corporateCardYn );
			
			TransactionData data = dataManager.getTransactionData(nozID) ;
			if (data != null) {
				data.setCorporateCardYn(corporateCardYn) ;
			}
		
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
	}

	/**
	 * �� �������� ���ʽ�ī���ȣ�� �����Ѵ�. ����ī�� ������ �����ź����μ��� ������ ��½� ���.
	 * 
	 * @param nozID		: ���� ��ȣ
	 */
	public void setCorporateBonus(String nozID, String corporateBouns) {
		try {
			
			TransactionData data = dataManager.getTransactionData(nozID) ;
			if (data != null) {
				data.setCorporateBouns(corporateBouns);
			}
			
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
	}
	
	/**
	 * ������ - �� ī�� ��ȣ�� �����Ѵ�.
	 * @param nozID
	 * @param serialNumber
	 */
	public void setCustCardNumber(String nozID, String serialNumber){
	
		TransactionData data = dataManager.getTransactionData(nozID) ;
		
		if (data != null) {
			data.setCustCardNo(serialNumber);
			LogUtility.getPumpMLogger().debug("[Pump M] S9:��ī�� ��ȣ�� �����մϴ� "+ serialNumber);
		}
	}
	
	/**
	 * ������ ���¸� �����Ѵ�.
	 * 
	 * @param nozID		: ���� ��ȣ
	 * @param state		: ���� ����
	 */
	public void setNozzleState(String nozID, int state) {
		TransactionData data = dataManager.getTransactionData(nozID) ;

		if (data != null) {
			int preState = IPumpConstant.KH_PUMP_DEFAULT ;
			String khproc_no = null ;
			preState = data.getCurrState() ;
			data.setCurrState(state) ;
			khproc_no = data.getKhTransactionID() ;
			PumpLogUtil.printStateChange(nozID, preState, state, khproc_no) ;
		} 
	}
	
	/**
	 * �� ������ �� �������� ODT �� ���� ����Ǿ����� �����Ѵ�.
	 * 
	 * @param nozID		: ���� ��ȣ
	 */
	public void setPayed(String messageType, String nozID) {
		try {
			int messageTypeInt = Integer.parseInt(messageType) ;
			if (UPOSUtil.isPayedUPOSMessage(messageTypeInt)) {
				TransactionData data = dataManager.getTransactionData(nozID) ;
				if (data != null) {
					data.setPayed(true) ;
				}
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
	}
	
	/**
	 * �� ������ Preset �� ��� ���� ��û�� �޾Ƽ� ������ �Ǵ����� �����Ѵ�.
	 * 
	 * @param nozID			: ���� ��ȣ
	 * @param presetFrom	: Preset �� ��û�� ��
	 */
	public void setPresetInfo(String nozID, int presetFrom) {
		TransactionData data = dataManager.getTransactionData(nozID) ;
		data.setPresetFrom(presetFrom) ;	
		try {
			int nozProtocolInt = PumpMUtil.getConnectedRepODTProtocolFromNozzleNo(nozID) ;
			
			switch (nozProtocolInt) {
				case IPumpConstant.PUMP_PROTOCOL_DaSNo : 
				case IPumpConstant.PUMP_PROTOCOL_NewDaSNo :{  //2012.09.26 ksm
					if (presetFrom == IPumpConstant.PRESET_FROM_POS) {
						DasNoSelfPumpingManager.getInstance().
							createODTNozzleInfo(nozID, false ,IConstant.FULL_PUMPING_OPTION_8) ;
					}
					break ;
				}
				case IPumpConstant.PUMP_PROTOCOL_GSC_SELF :{  //2015.11.04 cwi
					if (presetFrom == IPumpConstant.PRESET_FROM_POS)
						GSCSelfPumpingManager.getInstance().
							createODTNozzleInfo(nozID, false ,IConstant.FULL_PUMPING_OPTION_8) ;
					break ;
				}
				default : {
				}
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
	}
	
	/**
	 * �������۰� �����Ϸ��� ��� �� �� ���¿� ���Ͽ� ������ ��� true �� �����Ѵ�.
	 * 
	 * @param nozID	: ���� ��ȣ
	 * @param state	: ���� ����
	 * @return
	 */
	public boolean isPresetState(String nozID, String pumpType) {
		boolean rlt = false ;		
		//LogUtility.getLogger().info("[Pump M] state==========================" + state);
		TransactionData data = dataManager.getTransactionData(nozID) ;
		
		try {
			if(data == null){
				LogUtility.getLogger().info("������ ���� ");
				rlt = true ; 
			} else {
				int currState = data.getCurrState() ;
				LogUtility.getLogger().info("currState :"+currState +"");
				
				if(pumpType.equals(ICode.SELF_IND_EXIST_01_PUMP) &&currState == IPumpConstant.KH_PUMP_COMPLETED){  // �Ϲ� ������ 
					LogUtility.getLogger().info("�Ϲ� ������ , ���� , currState :"+currState);
					rlt = true ;
				} else if (pumpType.equals(ICode.SELF_IND_EXIST_02_SELF_PUMP) && currState == IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED){  // SELF ������ 
					LogUtility.getLogger().info("����  ������ , ���� , currState :"+currState);
					rlt = true ;
				} 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		LogUtility.getLogger().info("������ ���� Ȯ�ΰ� :  "+ rlt);
		return rlt ;	
	}	

}
