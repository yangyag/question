package com.gsc.kixxhub.module.pumpm.bundle.sub;

 
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import com.gsc.kixxhub.common.data.ICode;
import com.gsc.kixxhub.common.data.IConstant;
import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.posdata.POSHeader;
import com.gsc.kixxhub.common.data.posdata.POS_AA;
import com.gsc.kixxhub.common.data.posdata.POS_AB;
import com.gsc.kixxhub.common.data.posdata.POS_AB_DeviceState;
import com.gsc.kixxhub.common.data.posdata.POS_AE;
import com.gsc.kixxhub.common.data.pump.S8_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SE_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_STOREHandler;
import com.gsc.kixxhub.common.dbadapter.pump.handler.T_NZ_NOZZLEHandler;
import com.gsc.kixxhub.common.dbadapter.pump.vo.T_NZ_NOZZLEData;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.common.utility.sync.Preamble;
import com.gsc.kixxhub.common.utility.sync.SyncManager;
import com.gsc.kixxhub.module.pumpm.pump.manager.PumpMODTSaleManager;
import com.gsc.kixxhub.module.pumpm.pump.manager.PumpMPriceManager;
import com.gsc.kixxhub.module.pumpm.pump.manager.PumpMTransactionManager;
import com.gsc.kixxhub.module.pumpm.pump.util.PumpLogUtil;
import com.gsc.kixxhub.module.pumpm.pump.util.PumpMUtil;
 
public class StateMController extends StateMControllerBase {
 
	public static Hashtable<String, POS_AB_DeviceState> odtStateHash = new Hashtable<String, POS_AB_DeviceState>() ;
	public static Hashtable<String, POS_AB_DeviceState> pumpStateHash = new Hashtable<String, POS_AB_DeviceState>() ;
 
    /**
     * 주유기의 그 전 상태와 현 상태를 비교한다. 만약 다를 경우 State M 에게 전송하도록 한다.
     * 
     * @param currState	: 주유기로 부터 받은 상태 정보
     * @return
     * 		true : 전 상태와 같은 경우
     * 		false : 상태가 틀릴 경우
     */
    private boolean compareState(POS_AB_DeviceState currState) {
    	boolean rlt = true ;
    	if (currState.getDeviceType().equals(IConstant.POSPROTOCOL_TYPE_NOZZLE)) {
			if (pumpStateHash.containsKey(currState.getDeviceID())) {
				POS_AB_DeviceState preState = pumpStateHash.get(currState.getDeviceID()) ;
				if (!preState.getStateCode().equals(currState.getStateCode())) {
					rlt = false ;
					pumpStateHash.put(currState.getDeviceID(), currState) ;
				}
			} else {
				rlt = false ;
				pumpStateHash.put(currState.getDeviceID(), currState) ;
			}
    	} else if (currState.getDeviceType().equals(IConstant.POSPROTOCOL_TYPE_ODT)) {
			if (odtStateHash.containsKey(currState.getDeviceID())) {
				POS_AB_DeviceState preState = odtStateHash.get(currState.getDeviceID()) ;
				if (!preState.getStateCode().equals(currState.getStateCode())) {
					rlt = false ;
					odtStateHash.put(currState.getDeviceID(), currState) ;
				}
			} else {
				rlt = false ;
				odtStateHash.put(currState.getDeviceID(), currState) ;
			}
    	} 
		
		int stateCode = Integer.parseInt(currState.getStateCode()) ;
		switch (stateCode) {
			case IConstant.STATE_PUMP_STATECODE_653 :
				rlt = false ;
		}

    	return rlt ;
    }
 

    /**
     * 모든 주유기의 상태 정보를 POS 혹은 AMS 로 전송하기 위해서 ArrayList 로 변경한다.
     * 
     * @param pumpStateHash	: 모든 주유기의 상태 정보를 저장하고 있는 Hashtable
     * @return
     * 		POSPumpState ArrayList
     */
    private ArrayList<POS_AB_DeviceState> convertArrayList(Hashtable<String, POS_AB_DeviceState> pumpStateHash) {
    	
    	if ((pumpStateHash == null) || (pumpStateHash.size()==0)) {
    		return null ;
    	}
    	ArrayList<POS_AB_DeviceState> pumpStateArray = new ArrayList<POS_AB_DeviceState>() ;
    	Enumeration<POS_AB_DeviceState> e = pumpStateHash.elements() ;
    	while (e.hasMoreElements()) {
    		pumpStateArray.add(e.nextElement()) ;
    	}
    	return pumpStateArray ;
    }
 
    /**
     * Pump A 로 부터온 전문(SE or S8)을 POSPumpState class 로 변경하여 StateMController 에서 관리한다.
     * 
     * @param message	: SE_WorkingMessage 혹은 S8_WorkingMessage
     * @return
     * 		POSPumpState
     */
    private POS_AB_DeviceState createPumpState(WorkingMessage message) {
    	POS_AB_DeviceState pumpState = null ;

    	if (message.getCommand().equals(IPumpConstant.COMMANDID_SE) || 
    			(message.getCommand().equals(IPumpConstant.COMMANDID_S8))) {
    		S8_WorkingMessage seMessage = (S8_WorkingMessage) message ;
    		
//    		if (isExist(seMessage)) {
	    		if (seMessage.getDeviceType().equals(ICode.SELF_IND_EXIST_01_PUMP) ||
	    				seMessage.getDeviceType().equals(ICode.SELF_IND_EXIST_03_SEMI_SELF) ||
	    				seMessage.getDeviceType().equals(ICode.SELF_IND_EXIST_02_SELF_PUMP)) {
	    			if (seMessage.getStatus().equals(IPumpConstant.PUMP_STATECODE_OK)) {
	    				 pumpState = new POS_AB_DeviceState(IConstant.POSPROTOCOL_TYPE_NOZZLE , seMessage.getNozzleNo() ,
	    					 IPumpConstant.PUMP_STATECODE_OK , seMessage.getStatusCode()) ;
	    			} else if (seMessage.getStatus().equals(IPumpConstant.PUMP_STATECODE_ERROR)) {
	    				 pumpState = new POS_AB_DeviceState(IConstant.POSPROTOCOL_TYPE_NOZZLE , seMessage.getNozzleNo() ,
	    					 IPumpConstant.PUMP_STATECODE_ERROR , seMessage.getStatusCode()) ;
	    			}
	    		} else if ((seMessage.getDeviceType().equals(ICode.SELF_IND_EXIST_05_ODT_SELF))){
	    			if (seMessage.getStatus().equals(IPumpConstant.PUMP_STATECODE_OK)) {
	    				pumpState = new POS_AB_DeviceState(IConstant.POSPROTOCOL_TYPE_ODT , seMessage.getNozzleNo() ,
	    						 IPumpConstant.PUMP_STATECODE_OK , seMessage.getStatusCode()) ;
	    			} else if (seMessage.getStatus().equals(IPumpConstant.PUMP_STATECODE_ERROR)) {
	    				pumpState = new POS_AB_DeviceState(IConstant.POSPROTOCOL_TYPE_ODT , seMessage.getNozzleNo() ,
	    					 IPumpConstant.PUMP_STATECODE_ERROR , seMessage.getStatusCode()) ;
	    			}
	    		} else if (seMessage.getDeviceType().equals(ICode.SELF_IND_EXIST_04_ODT_RECHARGE)){
	    			if (seMessage.getStatus().equals(IPumpConstant.PUMP_STATECODE_OK)) {
	    				pumpState = new POS_AB_DeviceState(IConstant.POSPROTOCOL_TYPE_ODT , seMessage.getNozzleNo() ,
	    						 IPumpConstant.PUMP_STATECODE_OK , seMessage.getStatusCode()) ;
	    			} else if (seMessage.getStatus().equals(IPumpConstant.PUMP_STATECODE_ERROR)) {
	    				pumpState = new POS_AB_DeviceState(IConstant.POSPROTOCOL_TYPE_ODT , seMessage.getNozzleNo() ,
	    					 IPumpConstant.PUMP_STATECODE_ERROR , seMessage.getStatusCode()) ;
	    			}
	    		} 
    		//}
    	} 

    	return pumpState ;
    }
    
    /**
	* <pre>
	* 1. 메소드명 : isExist
	* 2. 작성일 : 2016. 4. 14. 오후 15:48:31, PI2.
	* 3. 작성자 :  WooChul Jung
	* 4. 설명 :
	* 5. 변경이력: 노즐이 존재하는지 체크
	* </pre>
	* @param seMessage
	* @return
	*/
	private boolean isExist(S8_WorkingMessage seMessage) {
		boolean isExist = true ;
		
		try {
			T_NZ_NOZZLEData nozzleData = T_NZ_NOZZLEHandler.getHandler().getT_NZ_NOZZLEDataByNozzleNo(seMessage.getNozzleNo()) ;
			
			LogUtility.getPumpMLogger().info("[Pump M] T_NZ_NOZZLEData: "  );
			
			if (nozzleData == null) {
				isExist = false ;
			} else {
				if (seMessage.getDeviceType().equals(nozzleData.getSelf_ind_exist())) {
					isExist = true ;
				} else {
					isExist = false ;
				}
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
			isExist = false ;
		}
		
		if (!isExist) {
			LogUtility.getPumpMLogger().info("[Pump M] Nozzle 이 존재하지 않습니다. nozzleNo=" + seMessage.getNozzleNo() + " #종류=" + seMessage.getDeviceType());
		}
		
		return isExist;
	}

    /**
     * State M 으로 부터 주유기/ODT 의 상태 정보 요청을 받아서 이를 수행하여 State M 에게 전송한다.
     * State M 으로 부터 받는 Command ID 는 다음과 같다.
     * 		AA	: 상태 정보 요청				: State Module
     * 
     * @param receiving_statem	: 상태 모듈로 부터 받은 전문
     * 
     */
    @Override
	protected void onReceivingAmsMData(Object receiving_statem) {
    	LogUtility.getPumpMLogger().info("[Pump M] Receive state request from State M.") ;
    	
        Preamble receiveData = (Preamble) receiving_statem ;
        byte[] receiveContents = (byte[]) receiveData.getPreamble() ;
    	POSHeader posHeader = POSHeader.createHeader(receiveContents) ; 
        String uniqueKey = receiveData.getKey() ;
        
    	PumpLogUtil.printContent(uniqueKey, receiveData.getFrom(),receiveData.getDest(),receiveData.getPreamble()) ;

    	if (posHeader.getCommandID().equals(IConstant.POSPROTOCOL_COMMANDID_AA)) {
            POS_AA aaPumpm = new POS_AA(receiveContents) ;
            
            POS_AB abPumpm = null ;
            
            if (aaPumpm.getDeviceType().equals(IConstant.POSPROTOCOL_TYPE_NOZZLE)) {
            	if (aaPumpm.getDeviceID().equals(IConstant.POSPROTOCOL_NO_00)) {
            		abPumpm = new POS_AB(aaPumpm.getMessageID(),aaPumpm.getSource(),aaPumpm.getDeviceType(), aaPumpm.getDeviceID()
            				, convertArrayList(pumpStateHash)) ;
            	} else {
            		POS_AB_DeviceState pumpState = pumpStateHash.get(aaPumpm.getDeviceID()) ;
            		if (pumpState == null) {
            			abPumpm = new POS_AB(aaPumpm.getMessageID(),aaPumpm.getSource(),aaPumpm.getDeviceType(), aaPumpm.getDeviceID(),null ) ;
            		} else {
            			abPumpm = new POS_AB(aaPumpm.getMessageID(),aaPumpm.getSource(),pumpState ) ;
            		}
            	}
            } else if (aaPumpm.getDeviceType().equals(IConstant.POSPROTOCOL_TYPE_ODT)) {
            	if (aaPumpm.getDeviceID().equals(IConstant.POSPROTOCOL_NO_00)) {
            		abPumpm = new POS_AB(aaPumpm.getMessageID(),aaPumpm.getSource(), aaPumpm.getDeviceType(), aaPumpm.getDeviceID()
            				, convertArrayList(odtStateHash)) ;
            	} else {
            		POS_AB_DeviceState pumpState = odtStateHash.get(aaPumpm.getDeviceID()) ;
            		if (pumpState == null) {
            			abPumpm = new POS_AB(aaPumpm.getMessageID(),aaPumpm.getSource(),aaPumpm.getDeviceType(), aaPumpm.getDeviceID(),null ) ;
            		} else {
            			abPumpm = new POS_AB(aaPumpm.getMessageID(),aaPumpm.getSource(),pumpState ) ;
            		}       		
            	}
            } 
            
            LogUtility.getPumpMLogger().info("[Pump M] Send Pump State to State M.") ;

    		Preamble sendData = PumpMUtil.createPOSMessagePreamble(uniqueKey, SyncManager.DISE_STATE_MODULE, 
    				abPumpm, "") ;
    		sendMessage(sendData) ;
    		Preamble posSendData = PumpMUtil.createPOSMessagePreamble(GlobalUtility.getUniqueMessageID(), SyncManager.DISE_POS_ADAPTER, 
    				abPumpm, "") ;
    		sendMessage(posSendData) ;
    	}
    }
    
    /**
     * State M 에게 주유기/ODT 의 상태를 전송한다. State M 에게 보낼 전문은 다음과 같다.
     * 		SE	: 주유기/충전기 이상 정보 전송
     * 		S8	: 주유기/충전기 상태 전송
     * 
     * @param sending_statem	: 상태 모듈에게 보낼 전문
     * 					
     */
    @Override
	protected void onReceivingPumpMData(Object sending_statem) {

    	try {
	    	Preamble receiveData = (Preamble) sending_statem ;
		    WorkingMessage receiveWorkingMessage = (WorkingMessage) receiveData.getPreamble() ;
		    String commandID = receiveWorkingMessage.getCommand() ;
		    String uniqueKey = null ;
		    String messageID = receiveWorkingMessage.getMessageID() ;
	
		    switch (commandID) {
			      case IPumpConstant.COMMANDID_SE :
			      case IPumpConstant.COMMANDID_S8 : {
			          POS_AB_DeviceState pumpState = createPumpState(receiveWorkingMessage) ;
			          
	//		          LogUtility.getPumpMLogger().debug("[Pump M] 주유 상태 정보를 받았습니다. 노즐번호=" + pumpState.getDeviceID() + 
	//		        		  ", 상태코드=" + pumpState.getStateCode()) ;
			          
			          if (pumpState == null) return ;
			          
			          String statusCode = pumpState.getStateCode() ;
			          String deviceID = pumpState.getDeviceID() ;
			          
			          /**
			           * 2016. 4. 14. 오후 15:48:31, PI2, WooChul Jung
			           * 		상태코드 650 은 없는 것임.
			           * 2016. 4. 14. 오후 15:48:31, PI2, WooChul Jung 
			           * 		Self ODT 의 경우 650 이 정상임. 따라서 존재함.
			           */
			          if (GlobalUtility.isNullOrEmptyString(statusCode)) {
			        	  LogUtility.getPumpMLogger().warn("[Pump M] Drop State Content becuase stateCode=" + statusCode) ;
			        	  return ;
			          }
			          
			          if (pumpState != null) {
				        	boolean rlt = false ;

				        	rlt = compareState(pumpState) ; 
			         		if (!rlt) {
			         			POSHeader posMessage = null ;
			         			POSHeader amsMessage = null ;
			         			if (receiveWorkingMessage.getCommand().equals(IPumpConstant.COMMANDID_SE)) {
			         				posMessage = new POS_AE(messageID,IConstant.POSPROTOCOL_SYSTEM_POS,(SE_WorkingMessage)receiveWorkingMessage) ;
			         				amsMessage = new POS_AE(messageID,IConstant.POSPROTOCOL_SYSTEM_AMS,(SE_WorkingMessage)receiveWorkingMessage) ;
			         			} else if (receiveWorkingMessage.getCommand().equals(IPumpConstant.COMMANDID_S8)) {
			         				posMessage = new POS_AB(messageID,IConstant.POSPROTOCOL_SYSTEM_POS,(S8_WorkingMessage)receiveWorkingMessage) ;
			         				amsMessage = new POS_AB(messageID,IConstant.POSPROTOCOL_SYSTEM_AMS,(S8_WorkingMessage)receiveWorkingMessage) ;
			         				posMessage.setCommandID(IConstant.POSPROTOCOL_COMMANDID_AE) ;
			         				amsMessage.setCommandID(IConstant.POSPROTOCOL_COMMANDID_AE) ;
			         			}
			         			Preamble preambleData = PumpMUtil.createPreamble(uniqueKey
			         					, SyncManager.DISE_STATE_MODULE, posMessage, "") ;
			         			Preamble amsPreambleData = PumpMUtil.createPreamble(uniqueKey
			         					, SyncManager.DISE_STATE_MODULE, amsMessage, "") ;
			         			LogUtility.getPumpMLogger().info("[Pump M] Send Pump State to State M. : Noz=" + 
			         					deviceID + " : StateCode=" + statusCode) ;
			         			sendMessage(preambleData) ;
			         			sendMessage(amsPreambleData) ;		         			
			         		} else {
	//		         			LogUtility.getPumpMLogger().debug("[Pump M] 상태가 그 전 상태와 동일합니다. 노즐번호=" + pumpState.getDeviceID() + 
	//		        		  ", 상태코드=" + pumpState.getStateCode()) ;
			         		}
			         		
		         			// 충전기 Reset 으로 인해 초기화 정보 재 전송
		         			if (Integer.parseInt(statusCode) == IConstant.STATE_PUMP_STATECODE_363) {
		         				LogUtility.getPumpMLogger().info("[Pump M] Send Initialization info to Recharge because of Recharge Reset. " +
		         						"Noz=" + deviceID) ;
		         				
		         				//초기화시 KH상태 변경 및 저장중인 거래처 정보 clear
		         				PumpMTransactionManager.getInstance().setNozzleState(deviceID, IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED) ;
		         				PumpMODTSaleManager.initSaleContent(deviceID) ;
		         				PumpMPriceManager.initPumpPrice(Integer.parseInt(deviceID));
		         				sendRechargeODTForReset(uniqueKey, messageID, deviceID) ;
		         			}
		         			
		         			// 셀프 ODT Reset 으로 인해 초기화 정보 재 전송
		         			/* [2008.10.22]
		         			 * 미팅 참석자 : 박동화 부장님, 오종훈 과장, 정우철 과장
		         			 * 	Self ODT Reset (252) 상태 코드시 초기화 정보를 Pump A 로 보내지 않는다.
		         			 *  Pump A 에서 초기화 데이터를 ODT 로 보내도록 한다.
		         			 */
		         			
/*		         			if (Integer.parseInt(statusCode) == IPumpConstant.PUMP_STATECODE_252) {
		         				LogUtility.getPumpMLogger().info("[Pump M] Send Initialization info to Recharge because of SelfODT Reset. " +
		         						"Noz=" + deviceID) ;
		         				sendSelfODTReset(uniqueKey, messageID, deviceID) ;
		         			}*/
			          }
			          break ;
			      }
		    }
    	} catch (Exception e) {
    		LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
    	} catch (Throwable e1) {
    		LogUtility.getPumpMLogger().error(e1.getMessage(),e1) ;
    	}
    }
    
    /**
     * 
     * State M 에게 전문을 송신한다. 보낼 전문의 Command ID 는 다음과 같다.
     * 
     * Output
     * 		POS Adapter		: Preamble		byte[]
     * 		Pump Adapter	: PumpPreamble	WorkingMessage
     * 		CAT Module		: Preamble		byte[]
     * 		State Module	: Preamble		WorkingMessage
     * 		Sale Module		: Preamble		byte[]
     * 
     * Send Where
     * 		- State Module
     * 			Command ID List							from where
     * 				- POS Protocol
     *					AB	: 상태 정보 요청 응답 			: PumpAController
     *					AE	: 상태 정보 전송				: PumpAController
     * 
     * @param preambleData	: 전송할 Preamble Object
     * 
     */
    private void sendMessage(Preamble preambleData) {
    	int dest = preambleData.getDest() ;
    	switch (dest) {
	    	case SyncManager.DISE_POS_ADAPTER : {
	    		getProducer_PumpMPosA_Data().produce(preambleData);
	    		break ;
	    	}
	    	case SyncManager.DISE_PUMP_ADAPTER :
	    		getProducer_PumpMPumpA_Data().produce(preambleData) ;
	    		break ; 
	    	case SyncManager.DISE_CAT_MODULE :
	    		break ; 
	    	case SyncManager.DISE_STATE_MODULE :
	    		getProducer_AmsM_PumpState().produce(preambleData) ;
	    		break ; 
	    	case SyncManager.DISE_SALE_MODULE :
	    		break ; 
    	}
    }
    
    /**
     * 충전기 ODT Reset 이 되었을 경우, 충전기 초기화를 위해서, 초기화 정보를 충전기에 전송한다.
     * 
     * @param uniqueKey	: Unique Key in Preamble Object
     * @param messageID	: Message ID
     * @param deviceID	: ODT 번호
     */
    private void sendRechargeODTForReset(String uniqueKey, String messageID, String deviceID) {
		String storeCode;
		try {
			storeCode = T_KH_STOREHandler.getHandler().getStoreCode();
			
			ArrayList<WorkingMessage> rechargeWorkMsgArray = PumpMUtil.createRechargeODTInitWorkingMessage(storeCode, deviceID) ;					
			if ((rechargeWorkMsgArray == null) || (rechargeWorkMsgArray.size() == 0)) {	
				LogUtility.getPumpMLogger().info("[Pump M] No Recharge.") ;
			}
			else {
				for (int i = 0 ; i < rechargeWorkMsgArray.size() ; i++) {
					Preamble preamble = PumpMUtil.createWorkingMessagePreamble(SyncManager.getUniqueKey(),
		    				SyncManager.DISE_PUMP_ADAPTER, rechargeWorkMsgArray.get(i) , "") ;
					sendMessage(preamble) ;
				}
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}		
    }
    
    /**
     * Self ODT Reset 이 되었을 경우, Self ODT 의 초기화를 위해서 초기화 정보를 전송한다.
     * @param uniqueKey	: Unique Key in Preamble Object
     * @param messageID	: Message ID
     * @param deviceID	: ODT 번호
     */
    private void sendSelfODTReset(String uniqueKey, String messageID, String deviceID) {
		String storeCode;
		try {
			storeCode = T_KH_STOREHandler.getHandler().getStoreCode();
			
			ArrayList<WorkingMessage> selfWorkMsgArray = PumpMUtil.createSelfODTInitWorkingMessage(storeCode, deviceID) ;					
			if ((selfWorkMsgArray == null) || (selfWorkMsgArray.size() == 0)) {		
				LogUtility.getPumpMLogger().info("[Pump M] No SelfODT") ;
			}
			else {
				for (int i = 0 ; i < selfWorkMsgArray.size() ; i++) {
					Preamble preamble = PumpMUtil.createWorkingMessagePreamble(SyncManager.getUniqueKey(),
		    				SyncManager.DISE_PUMP_ADAPTER, selfWorkMsgArray.get(i) , "") ;
					sendMessage(preamble) ;
				}
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
    }

    /**
     * 주유기의 상태코드를 반환 한다.
     * 
     * @param nozNo	: 노즐 번호
     * @return
     * 		주유기 상태코드
     */
    public static int getPumpStateCode(String nozzleNo) {
    	
    	int stateCode = IConstant.STATE_PUMP_STATECODE_651;
    	
    	POS_AB_DeviceState posPumpState = pumpStateHash.get(nozzleNo);
    	if(posPumpState != null ){
    		stateCode = Integer.parseInt(posPumpState.getStateCode());
    	}
    	
    	return stateCode;
    }

	@Override
	public void start() {
		registerListener() ;
    }
    
    @Override
	public void stop() {

    }
    
    // beacon 양일준 
    // 주유가능상태 종류 확인 필요 
    public static boolean ckPumpState(String nozNo) {
    	boolean canPreset = false ;
    	
    	POS_AB_DeviceState posPumpState = pumpStateHash.get(nozNo);
    	if(posPumpState == null){
    		return canPreset;
    	}
    	
    	int currStateCode = Integer.parseInt(posPumpState.getStateCode()) ;
    	String currState = pumpStateHash.get(nozNo).getState() ;
    	
    	LogUtility.getLogger().info("[Pump M] <Beacon Process> nozNo: "+nozNo +" ,currStateCode : "+currStateCode+",currState:"+currState);
    	if (pumpStateHash.containsKey(nozNo)) {
			if (currStateCode == IConstant.STATE_PUMP_STATECODE_651 || currStateCode == IConstant.STATE_PUMP_STATECODE_657
					/*
					 * 정확한 정보가 아니여서 주석 처리
					 && currState == IPumpConstant.PUMP_STATECODE_OK
					 
					 */
					
					) {
				canPreset = true ;
			}
		} else {
			LogUtility.getLogger().error("[Pump M] <Beacon Process> No Nozzle Data ") ;
			canPreset = false ;
		}
    	LogUtility.getLogger().info("[Pump M] <Beacon Process> canPreset ===> " + canPreset);
    	return canPreset ;
    }

}
