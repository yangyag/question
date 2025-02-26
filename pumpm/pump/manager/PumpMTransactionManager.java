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
		private String CorporateBouns = ""; //결제카드가 법인카드일때 보너스카드번호   
		//이강호 2014-07 법인카드보너스적립제외
		private boolean CorporateCardYn = false; //법인카드여부  
		private int currState = IPumpConstant.KH_PUMP_DEFAULT ;		// 시작, 주유중, 결제, Preset
		private String custCardNo = "";
		private int[] dataState = new int[IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED + 1] ;
		private boolean isPayed = false ;
		private String khTransactionID = "" ;						// KH 처리번호
		private String nozID = "" ;									// 노즐 번호
		private int presetFrom = IPumpConstant.PRESET_FROM_NONE ;	// 주유건이 누구에 의해 시작이 되었는가 ([POS | non-POS])
		
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
		 * TransactionData 내에 있는 데이터를 초기화 한다. 이는 재사용하기 위함이다.
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
			new Hashtable<String, TransactionData>() ;		// 노즐과 TransactionData 를 관리하는 Hash 변수
		
		/**
		 * 제일 처음 주유건의 KH 처리번호가 생성되었을 경우 이 함수를 호출하여 TransactionData 를 생성한다.
		 * 그 이후의 새로운 KH 처리번호가 생성이 되면 이미 생성된 TransactionData 를 재사용한다.
		 * 
		 * @param nozID				: 노즐 번호
		 * @param khTransactionID	: KH 처리번호
		 * @param state				: 상태 정보
		 * @return
		 */
		public TransactionData createTransactionData(String nozID, String khTransactionID, int state) {
			TransactionData data = new TransactionData(nozID , khTransactionID , state) ;
			transactionDataHash.put(nozID, data) ;
			return data ;
		}
		
		/**
		 * 노즐에 설정된 TransactionData 를 요청한다.
		 * 
		 * @param nozID		: 노즐 번호
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
		 * 주유완료이면서 이 주유건이 POS 의 Preset 에 의한 주유건 인지 여부 조사.
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
	 * 한 주유건에 대한 State 를 관리하고 있다. 이에 대한 초기화는 새로운 주유건 발생시 이전 주유건을 초기화 한다.
	 */
	private static PumpMTransactionManager transactionManager = null ;			// SingleTon 변수
	
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
				// TransactionData 를 관리하고 있는 Manager 변수
	/**
	 * 현 주유건이 결제카드가 법인카드일때 보너스카드번호 요청한다.
	 * 
	 * @param nozID		: 노즐 번호
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
	 * 현 노즐의 상태를 반환한다.
	 * 
	 * @param nozID		: 노즐 번호
	 * @param state		: 상태 정보
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
	 * 충전소 - 고객 카드 번호를 리턴한다.
	 * @param nozID
	 */
	public String getCustCardNumber(String nozID){
		TransactionData data = dataManager.getTransactionData(nozID) ;
		
		return data.getCustCardNo();
		
	}
	
	/**
	 * 다쓰노 Self ODT 의 KH 처리번호를 요청한다.
	 * 
	 * @param nozID		: 노즐 번호
	 * @param state		: 요청시 상태
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
			LogUtility.getPumpMLogger().info("[Pump M] Reset 요청을 처리합니다." + "nozzle=" + nozID + "# NewKHTransactionID.Create(" + khproc_no + ")");			
		} else if (data != null) {
			preState = data.getCurrState() ;
			
			/**
			 * [2008.11.17] 회의 by 박동화 부장, 편윤국 차장, 정우철
			 * 
			 * 다음의 경우 새로운 KH 처리번호를 생성하도록 한다.
			 * 		1. 판매 완료 인 경우
			 * 			- Normal 한 경우
			 * 		2. 그 전 주유가 완료 상태이며, 현 상태가 Preset 인 경우 (POS 로 부터 Preset 인 경우)
			 * 			- ODT 없이 단독 판매 하는 경우
			 * 		3. 그 전 주유가 완료이고, POS 로 부터 Preset 에 의한 경우였으며, 현 상태가 결재 요청인 경우
			 * 			- ODT 없이 단독 판매하다가, KH 를 리셋하지 않고 ODT 만 새로 켰을 경우(매우 예외적인 상황임)
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
	 * GSC Self ODT 의 KH 처리번호를 요청한다.
	 * 
	 * @param nozID		: 노즐 번호
	 * @param state		: 요청시 상태
	 * @return
	 */
	private String getGSCSelfODTKHTransactionID(String nozID , int state) {
		String khproc_no = null ;
		
		TransactionData data = dataManager.getTransactionData(nozID) ;

		int preState = IPumpConstant.KH_PUMP_DEFAULT ;
		
		if (data != null) {
			preState = data.getCurrState() ;
			
			/**
			 * [2008.11.17] 회의 by 박동화 부장, 편윤국 차장, 정우철
			 * 
			 * 다음의 경우 새로운 KH 처리번호를 생성하도록 한다.
			 * 		1. 판매 완료 인 경우
			 * 			- Normal 한 경우
			 * 		2. 그 전 주유가 완료 상태이며, 현 상태가 Preset 인 경우 (POS 로 부터 Preset 인 경우)
			 * 			- ODT 없이 단독 판매 하는 경우
			 * 		3. 그 전 주유가 완료이고, POS 로 부터 Preset 에 의한 경우였으며, 현 상태가 결재 요청인 경우
			 * 			- ODT 없이 단독 판매하다가, KH 를 리셋하지 않고 ODT 만 새로 켰을 경우(매우 예외적인 상황임)
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
	 * 현재 노즐의 처리번호를 요청한다.
	 * 
	 * @param nozID	: 노즐 번호
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
	 * 노즐에 대한 KH 처리번호를 요청한다. 
	 * 
	 * @param nozID	: 노즐 번호
	 * @param state	: 현 시점의 노즐 상태
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
			case IPumpConstant.PUMP_PROTOCOL_NewRecharge :  //2016.03.28 양일준 
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
	 * 단말기에서 요청한 경우 현재 노즐의 처리번호를 요청한다.
	 * 
	 * @param nozID	: 노즐 번호
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
	 * 현재 노즐의 새로운 처리번호를 요청한다.
	 * 2008:10:06 추가된 함수
	 * 	POS 로 부터 현재 시간이 KixxHub 로 내려올 경우, KixxHub 는 시스템 시간을 변경한다.
	 * 	이 경우 과거 시간이 내려옴으로써 KH 처리번호가 기존의 번호로 생성되는 경우가 있다. 이를 방지하기 위해서 KH 처리번호가 존재하는지 여부를
	 * 	체크한다. 
	 * 
	 * @param nozID	: 노즐 번호
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
		
		// TransactionData 초기화
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
	 * 주유 중 데이터일 경우 그 전 상태와 비교하여 검증 과정을 거친다.
	 * Pump Adapter 로 부터 주유 시작 없이 주유 중 데이터가 오는 경우, 주유 시작 전문을 자체 생성한다.
	 * 
	 * @param nozID	: 노즐 번호
	 * @param state	: 노즐 상태 (주유중만 올수 있다.)
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
	 * 주유기의 KH 처리번호를 요청한다.
	 * 
	 * @param nozID		: 노즐 번호
	 * @param state		: 요청시 상태
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
			LogUtility.getPumpMLogger().info("[Pump M] Reset 요청을 처리합니다." + "nozzle=" + nozID + "# NewKHTransactionID.Create(" + khproc_no + ")");			
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
	 * 충전기의 KH 처리번호를 요청한다.
	 * 
	 * @param nozID		: 노즐 번호
	 * @param state		: 요청시 상태
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
			LogUtility.getPumpMLogger().info("[Pump M] Reset 요청을 처리합니다." + "nozzle=" + nozID + "# NewKHTransactionID.Create(" + khproc_no + ")");			
		} else if (data != null) {
			preState = data.getCurrState() ;
			
			if (preState == IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED ) {
				khproc_no = getNewKHTransactionID(nozID) ;
				
				custCardNo = "";
//				LogUtility.getPumpMLogger().debug("[TEST][PumpM] 들어가있나??? 비었음 custCardNo="+custCardNo);
			} else {
				khproc_no = data.getKhTransactionID() ;
				
				POSHeader posObj = PumpMODTSaleManager.getCustPOSPumpM(nozID) ;
				if ((posObj != null) && (posObj instanceof POS_DW)) {
					POS_DW dwPosMsg = (POS_DW) posObj ;
					
					if (PumpMObjectValidation.validatePumpMObject(dwPosMsg)) {
						custCardNo = dwPosMsg.getCust_code();
//						LogUtility.getPumpMLogger().debug("[TEST][PumpM] 재입력하나??? custCardNo="+custCardNo);
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
	 * 소모 Self ODT 의 KH 처리번호를 요청한다.
	 * 
	 * @param nozID		: 노즐 번호
	 * @param state		: 요청시 상태
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
			LogUtility.getPumpMLogger().info("[Pump M] Reset 요청을 처리합니다." + "nozzle=" + nozID + "# NewKHTransactionID.Create(" + khproc_no + ")");			
		} else if (data != null) {
			preState = data.getCurrState() ;
			/**
			 * [2008.11.17] 회의 by 박동화 부장, 편윤국 차장, 정우철
			 * 
			 * 다음의 경우 새로운 KH 처리번호를 생성하도록 한다.
			 * 		1. 판매 완료 인 경우
			 * 			- Normal 한 경우
			 * 		2. 그 전 주유가 완료 상태이며, 현 상태가 Preset 인 경우 (POS 로 부터 Preset 인 경우)
			 * 			- ODT 없이 단독 판매 하는 경우
			 * 		3. 그 전 주유가 완료이고, POS 로 부터 Preset 에 의한 경우였으며, 현 상태가 결재 요청인 경우
			 * 			- ODT 없이 단독 판매하다가, KH 를 리셋하지 않고 ODT 만 새로 켰을 경우(매우 예외적인 상황임)
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
		
		// 2012.04.24 ksm  단말기일련번호 중복에 의한 전문추적번호 증가 + 500
		trackingNo = trackingNo + 500;
		if(trackingNo > 9999) {
			trackingNo = 1;
		}
		
		// 전문추적번호를 4자리로 맞춘다.
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
	 * 현 노즐의 다음의 상태 정보가 있는지 조사한다.
	 * 
	 * @param nozID		: 노즐 번호
	 * @param state		: 상태 정보
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
	
	// 2012.04.24 ksm  CATMProc의 ODTUtil 에서 가져옴.
	//public synchronized String getUniqueValue() {
	public synchronized String increaseTrackingValue() {
		// db에서 smt 전문추적번호를 get
		// t_kh_keys 테이블을 참조 (unique한 value가 없으면 insert한 후 update한다.)
		T_KH_KEYSData data;
		String value = "0001";

//		 ksm
		String UNIQUE = "ODTSALES"; 
		
		try {
			data = T_KH_KEYSHandler.getHandler().getT_KH_KEYSData(UNIQUE);
			
			//test ksm
			LogUtility.getPumpMLogger().debug("[Pump M] 중복발생 추적번호 : " + data.getValue()) ;
			
			if(data == null || data.getValue() == null || data.getValue().length() == 0) {
				// table에 unique한 값이 insert가 안된 경우.
				T_KH_KEYSHandler.getHandler().insertT_KH_KEYSData(UNIQUE, "0002");
				return value;
			}
			
			// 단말기일련번호 중복에 의한 전문추적번호 증가 + 500
			value = getTrackingNO(Integer.parseInt( data.getValue()) );
			
//			test ksm
			LogUtility.getPumpMLogger().debug("[[Pump M] 증가 추적번호 : " + value.toString()) ;
			
			// unique한 value를 update
			T_KH_KEYSHandler.getHandler().updateT_KH_KEYSData(UNIQUE, value);
		
		} catch (Exception e) {
			LogUtility.getCATLogger().error(e.getMessage(), e);
			return null;
		}
		return value;
	}

	/**
	 * 현 주유건이 결제카드가 법인카드인지 요청한다.
	 * 
	 * @param nozID		: 노즐 번호
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
	 * 현 주유건이 ODT 로부터 결재되었는지를 요청한다.
	 * 
	 * @param nozID		: 노즐 번호
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
	 * CAT 로 부터 Preset 에 의한 주유건인지를 조사한다.
	 * 
	 * @param nozID		: 노즐 번호
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
	 * POS 로 부터 Preset 에 의한 주유건인지를 조사한다.
	 * 
	 * @param nozID		: 노즐 번호
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
	 * 주유시작과 주유완료의 경우 그 전 상태와 비교하여 동일일 경우 true 로 리턴한다.
	 * 
	 * @param nozID	: 노즐 번호
	 * @param state	: 상태 정보
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
	 * 주유완료 금액이 0원인 경우 POS 로 주유완료 전문을 전송할지 여부를 조사한다. 아래 사항중에 하나에 해당되면 전송한다.
	 * 
	 * 	1. 그 주유건이 결재가 된것이면 전송한다.
	 * 	2. Self 주유기인경우 POS 로 부터 Preset 혹은 결재된 것이면 전송하도록 한다. -> 3번에 종속됨. (2008.10.05. 참석자 : 편윤국, 정우철)
	 * 	3. 주유기/Semi-Self/충전기인 경우 , POS 로 부터 Preset 에 의한 것이면 전송한다.
	 * 
	 * @param nozID		: 노즐 번호
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
	 * 현재 KH처리번호의 TransactionData를 리셋한다.
	 * CAT단말기에서 CAT Preset도중에 종료했을 경우 KH Data를 리셋하기 위해 사용한다.
	 * @param nozID	: 노즐 번호
	 * @return
	 */
	public void reset(String nozID, String khproc_no){
		// TransactionData 초기화
		TransactionData data = dataManager.getTransactionData(nozID);
		if (data == null) {
			dataManager.createTransactionData(nozID, khproc_no, IPumpConstant.KH_PUMP_DEFAULT) ;
		} else {
			data.reset(nozID, khproc_no) ;
		}
		
	}
	
	/**
	 * 현 주유건이 결제카드가 법인카드인지 설정한다.
	 * 
	 * @param nozID		: 노즐 번호
	 */
	public void setCorporate(String nozID, boolean corporateCardYn) {
		try {
			LogUtility.getPumpMLogger().debug("[Pump M] 법인카드설정: nozID="+nozID+"#corporateCardYn="+corporateCardYn );
			
			TransactionData data = dataManager.getTransactionData(nozID) ;
			if (data != null) {
				data.setCorporateCardYn(corporateCardYn) ;
			}
		
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
	}

	/**
	 * 현 주유건의 보너스카드번호를 저장한다. 법인카드 결제시 적립거부프로세스 영수증 출력시 사용.
	 * 
	 * @param nozID		: 노즐 번호
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
	 * 충전소 - 고객 카드 번호를 저장한다.
	 * @param nozID
	 * @param serialNumber
	 */
	public void setCustCardNumber(String nozID, String serialNumber){
	
		TransactionData data = dataManager.getTransactionData(nozID) ;
		
		if (data != null) {
			data.setCustCardNo(serialNumber);
			LogUtility.getPumpMLogger().debug("[Pump M] S9:고객카드 번호를 저장합니다 "+ serialNumber);
		}
	}
	
	/**
	 * 노즐의 상태를 변경한다.
	 * 
	 * @param nozID		: 노즐 번호
	 * @param state		: 상태 정보
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
	 * 이 노즐의 현 주유건이 ODT 에 의해 결재되었음을 설정한다.
	 * 
	 * @param nozID		: 노즐 번호
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
	 * 현 노즐의 Preset 이 어디서 부터 요청을 받아서 시작이 되는지를 설정한다.
	 * 
	 * @param nozID			: 노즐 번호
	 * @param presetFrom	: Preset 을 요청한 곳
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
	 * 주유시작과 주유완료의 경우 그 전 상태와 비교하여 동일일 경우 true 로 리턴한다.
	 * 
	 * @param nozID	: 노즐 번호
	 * @param state	: 상태 정보
	 * @return
	 */
	public boolean isPresetState(String nozID, String pumpType) {
		boolean rlt = false ;		
		//LogUtility.getLogger().info("[Pump M] state==========================" + state);
		TransactionData data = dataManager.getTransactionData(nozID) ;
		
		try {
			if(data == null){
				LogUtility.getLogger().info("데이터 없음 ");
				rlt = true ; 
			} else {
				int currState = data.getCurrState() ;
				LogUtility.getLogger().info("currState :"+currState +"");
				
				if(pumpType.equals(ICode.SELF_IND_EXIST_01_PUMP) &&currState == IPumpConstant.KH_PUMP_COMPLETED){  // 일반 주유기 
					LogUtility.getLogger().info("일반 주유기 , 정상 , currState :"+currState);
					rlt = true ;
				} else if (pumpType.equals(ICode.SELF_IND_EXIST_02_SELF_PUMP) && currState == IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED){  // SELF 주유기 
					LogUtility.getLogger().info("셀프  주유기 , 정상 , currState :"+currState);
					rlt = true ;
				} 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		LogUtility.getLogger().info("주유기 상태 확인값 :  "+ rlt);
		return rlt ;	
	}	

}
