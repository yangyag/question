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
     * �������� �� �� ���¿� �� ���¸� ���Ѵ�. ���� �ٸ� ��� State M ���� �����ϵ��� �Ѵ�.
     * 
     * @param currState	: ������� ���� ���� ���� ����
     * @return
     * 		true : �� ���¿� ���� ���
     * 		false : ���°� Ʋ�� ���
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
     * ��� �������� ���� ������ POS Ȥ�� AMS �� �����ϱ� ���ؼ� ArrayList �� �����Ѵ�.
     * 
     * @param pumpStateHash	: ��� �������� ���� ������ �����ϰ� �ִ� Hashtable
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
     * Pump A �� ���Ϳ� ����(SE or S8)�� POSPumpState class �� �����Ͽ� StateMController ���� �����Ѵ�.
     * 
     * @param message	: SE_WorkingMessage Ȥ�� S8_WorkingMessage
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
	* 1. �޼ҵ�� : isExist
	* 2. �ۼ��� : 2016. 4. 14. ���� 15:48:31, PI2.
	* 3. �ۼ��� :  WooChul Jung
	* 4. ���� :
	* 5. �����̷�: ������ �����ϴ��� üũ
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
			LogUtility.getPumpMLogger().info("[Pump M] Nozzle �� �������� �ʽ��ϴ�. nozzleNo=" + seMessage.getNozzleNo() + " #����=" + seMessage.getDeviceType());
		}
		
		return isExist;
	}

    /**
     * State M ���� ���� ������/ODT �� ���� ���� ��û�� �޾Ƽ� �̸� �����Ͽ� State M ���� �����Ѵ�.
     * State M ���� ���� �޴� Command ID �� ������ ����.
     * 		AA	: ���� ���� ��û				: State Module
     * 
     * @param receiving_statem	: ���� ���� ���� ���� ����
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
     * State M ���� ������/ODT �� ���¸� �����Ѵ�. State M ���� ���� ������ ������ ����.
     * 		SE	: ������/������ �̻� ���� ����
     * 		S8	: ������/������ ���� ����
     * 
     * @param sending_statem	: ���� ��⿡�� ���� ����
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
			          
	//		          LogUtility.getPumpMLogger().debug("[Pump M] ���� ���� ������ �޾ҽ��ϴ�. �����ȣ=" + pumpState.getDeviceID() + 
	//		        		  ", �����ڵ�=" + pumpState.getStateCode()) ;
			          
			          if (pumpState == null) return ;
			          
			          String statusCode = pumpState.getStateCode() ;
			          String deviceID = pumpState.getDeviceID() ;
			          
			          /**
			           * 2016. 4. 14. ���� 15:48:31, PI2, WooChul Jung
			           * 		�����ڵ� 650 �� ���� ����.
			           * 2016. 4. 14. ���� 15:48:31, PI2, WooChul Jung 
			           * 		Self ODT �� ��� 650 �� ������. ���� ������.
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
	//		         			LogUtility.getPumpMLogger().debug("[Pump M] ���°� �� �� ���¿� �����մϴ�. �����ȣ=" + pumpState.getDeviceID() + 
	//		        		  ", �����ڵ�=" + pumpState.getStateCode()) ;
			         		}
			         		
		         			// ������ Reset ���� ���� �ʱ�ȭ ���� �� ����
		         			if (Integer.parseInt(statusCode) == IConstant.STATE_PUMP_STATECODE_363) {
		         				LogUtility.getPumpMLogger().info("[Pump M] Send Initialization info to Recharge because of Recharge Reset. " +
		         						"Noz=" + deviceID) ;
		         				
		         				//�ʱ�ȭ�� KH���� ���� �� �������� �ŷ�ó ���� clear
		         				PumpMTransactionManager.getInstance().setNozzleState(deviceID, IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED) ;
		         				PumpMODTSaleManager.initSaleContent(deviceID) ;
		         				PumpMPriceManager.initPumpPrice(Integer.parseInt(deviceID));
		         				sendRechargeODTForReset(uniqueKey, messageID, deviceID) ;
		         			}
		         			
		         			// ���� ODT Reset ���� ���� �ʱ�ȭ ���� �� ����
		         			/* [2008.10.22]
		         			 * ���� ������ : �ڵ�ȭ �����, ������ ����, ����ö ����
		         			 * 	Self ODT Reset (252) ���� �ڵ�� �ʱ�ȭ ������ Pump A �� ������ �ʴ´�.
		         			 *  Pump A ���� �ʱ�ȭ �����͸� ODT �� �������� �Ѵ�.
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
     * State M ���� ������ �۽��Ѵ�. ���� ������ Command ID �� ������ ����.
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
     *					AB	: ���� ���� ��û ���� 			: PumpAController
     *					AE	: ���� ���� ����				: PumpAController
     * 
     * @param preambleData	: ������ Preamble Object
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
     * ������ ODT Reset �� �Ǿ��� ���, ������ �ʱ�ȭ�� ���ؼ�, �ʱ�ȭ ������ �����⿡ �����Ѵ�.
     * 
     * @param uniqueKey	: Unique Key in Preamble Object
     * @param messageID	: Message ID
     * @param deviceID	: ODT ��ȣ
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
     * Self ODT Reset �� �Ǿ��� ���, Self ODT �� �ʱ�ȭ�� ���ؼ� �ʱ�ȭ ������ �����Ѵ�.
     * @param uniqueKey	: Unique Key in Preamble Object
     * @param messageID	: Message ID
     * @param deviceID	: ODT ��ȣ
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
     * �������� �����ڵ带 ��ȯ �Ѵ�.
     * 
     * @param nozNo	: ���� ��ȣ
     * @return
     * 		������ �����ڵ�
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
    
    // beacon ������ 
    // �������ɻ��� ���� Ȯ�� �ʿ� 
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
					 * ��Ȯ�� ������ �ƴϿ��� �ּ� ó��
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
