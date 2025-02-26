package com.gsc.kixxhub.module.pumpm.bundle.sub;

 
import java.util.ArrayList;

import org.apache.ibatis.session.SqlSession;

import com.gsc.kixxhub.common.data.ICode;
import com.gsc.kixxhub.common.data.IConstant;
import com.gsc.kixxhub.common.data.ICustConstant;
import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.IUPOSConstant;
import com.gsc.kixxhub.common.data.posdata.POSHeader;
import com.gsc.kixxhub.common.data.posdata.POS_CX;
import com.gsc.kixxhub.common.data.posdata.POS_DA;
import com.gsc.kixxhub.common.data.posdata.POS_DB;
import com.gsc.kixxhub.common.data.posdata.POS_DC;
import com.gsc.kixxhub.common.data.posdata.POS_DD;
import com.gsc.kixxhub.common.data.posdata.POS_DD_NozzleInfo;
import com.gsc.kixxhub.common.data.posdata.POS_DI;
import com.gsc.kixxhub.common.data.posdata.POS_DK;
import com.gsc.kixxhub.common.data.posdata.POS_DL;
import com.gsc.kixxhub.common.data.posdata.POS_DN;
import com.gsc.kixxhub.common.data.posdata.POS_DO;
import com.gsc.kixxhub.common.data.posdata.POS_DR;
import com.gsc.kixxhub.common.data.posdata.POS_DS;
import com.gsc.kixxhub.common.data.posdata.POS_DT;
import com.gsc.kixxhub.common.data.posdata.POS_DU;
import com.gsc.kixxhub.common.data.posdata.POS_DV;
import com.gsc.kixxhub.common.data.posdata.POS_DW;
import com.gsc.kixxhub.common.data.posdata.POS_DX;
import com.gsc.kixxhub.common.data.posdata.POS_DY;
import com.gsc.kixxhub.common.data.posdata.POS_KD;
import com.gsc.kixxhub.common.data.posdata.POS_KE;
import com.gsc.kixxhub.common.data.posdata.POS_KF;
import com.gsc.kixxhub.common.data.posdata.POS_KG;
import com.gsc.kixxhub.common.data.posdata.POS_KH;
import com.gsc.kixxhub.common.data.posdata.POS_KH_NozzleInfo;
import com.gsc.kixxhub.common.data.pump.CB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.GB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HD_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P7_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PG_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PQ_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PU_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.QF_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.QL_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S9_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SG_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.data.pump.format.PumpMessageFormat;
import com.gsc.kixxhub.common.data.upos.UPOSMessage;
import com.gsc.kixxhub.common.data.upos.UPOSMessageUtility;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_ItemInfo_Item;
import com.gsc.kixxhub.common.dbadapter.common.SqlSessionFactoryManager;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_PUMP_TRHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_STOREHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_VIOLATIONHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.manager.LockingManager;
import com.gsc.kixxhub.common.dbadapter.kixxhub.manager.LockingManagerListener;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_PUMP_TRData;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_VIOLATIONData;
import com.gsc.kixxhub.common.dbadapter.pump.handler.T_NZ_INFOHandler;
import com.gsc.kixxhub.common.dbadapter.pump.handler.T_NZ_NOZZLEHandler;
import com.gsc.kixxhub.common.dbadapter.pump.handler.T_NZ_PARAMETERHandler;
import com.gsc.kixxhub.common.dbadapter.pump.vo.T_NZ_INFOData;
import com.gsc.kixxhub.common.dbadapter.pump.vo.T_NZ_NOZZLEData;
import com.gsc.kixxhub.common.dbadapter.pump.vo.T_NZ_PARAMETERData;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.common.utility.sync.Preamble;
import com.gsc.kixxhub.common.utility.sync.SyncManager;
import com.gsc.kixxhub.module.pumpm.pump.manager.PumpMODTSaleManager;
import com.gsc.kixxhub.module.pumpm.pump.manager.PumpMSyncManager;
import com.gsc.kixxhub.module.pumpm.pump.manager.PumpMTransactionManager;
import com.gsc.kixxhub.module.pumpm.pump.util.Barcode;
import com.gsc.kixxhub.module.pumpm.pump.util.CustUtil;
import com.gsc.kixxhub.module.pumpm.pump.util.ODTUtility_GSC_Self;
import com.gsc.kixxhub.module.pumpm.pump.util.PumpLogUtil;
import com.gsc.kixxhub.module.pumpm.pump.util.PumpMUtil;
import com.gsc.kixxhub.module.pumpm.pump.util.S9Util;
import com.gsc.kixxhub.module.pumpm.pump.util.UPOSUtil;

public class PosAController extends PosAControllerBase implements LockingManagerListener {
 
	private static boolean isPOSStarted = false ;
	
	private static boolean logSSDC=false; 
	/**
     * KixxHub �� POS �� ������� ��û�� ���� ������ �Ѵ�.
     * ������� ����.
     * 
     * @return
     * 		boolean	
     * 			true : �����
     * 			flase : ������ �ȵ�
     */
    public static boolean isPOSStarted() {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PosAController/" + "isPOSStarted()");
		
    	LogUtility.getPumpMLogger().info("[Pump M] isPOSConnected=" + isPOSStarted) ;
    	return isPOSStarted ;
    }

	private int gStep = IPumpConstant.STEP_PUMP_RESOLVED ;
 
	/**
     * POS A �� ���� Timeout �� �߻��� ������ ���� ó���� �����Ѵ�.
     *   	- ������ �߱� ��ȸ ��û / ���� (DM/DN)		- ������
  	 *		- �� ���� ��ȸ ��û/���� (DT/DU)			- ������
  	 *		- �� ī�� ��ȸ ��û/���� (DV/DW)			- �Ҹ��� / ������
  	 *		- �� ī�� ���� ��û/���� (DX/DY)			- CAT �ܸ���
     * @param receivingData	: timeout �� �߻��� ������
     */
    private void handleTimeOutContent(Preamble receivingData) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PosAController/" + "handleTimeOutContent()");
		
		Object waitingContent ;
		String uniqueKey = receivingData.getKey() ;

		LogUtility.getPumpMLogger().info("[Pump M] TimeOut Content from POS A. uniqueKey="+uniqueKey) ;
		// Debug Purpose
		Object sendingObj = receivingData.getPreamble() ;
		PumpLogUtil.printContent(sendingObj) ;

		if (uniqueKey == null) {
			LogUtility.getPumpMLogger().error("[Pump M] Drop because key is null.") ;
			return ;
		}
		waitingContent = PumpMSyncManager.getPreambleAndRemove(uniqueKey) ;
		
		if (waitingContent == null) {
			LogUtility.getPumpMLogger().info("[Pump M] Drop becuase not Waiting content.") ;
			return ;
		} else {
			Preamble preambleObj = (Preamble) waitingContent ;
			Object msg = preambleObj.getPreamble() ;
			
			// Debug Purpose
			PumpLogUtil.printContent(msg) ;
			
			if (msg instanceof UPOSMessage) {
				// �� ī�� ���� ��û/���� (DX/DY)			- CAT �ܸ���
				LogUtility.getPumpMLogger().info("[Pump M] Timeout = UPOSMessage Class.") ;
				UPOSMessage uPosMsg = (UPOSMessage) msg ;
				int messageType = Integer.parseInt(uPosMsg.getMessageType()) ;
				switch (messageType) {
					case IUPOSConstant.MESSAGETYPE_INT_4201 :
					case IUPOSConstant.MESSAGETYPE_INT_4291 : 
					case IUPOSConstant.MESSAGETYPE_INT_4301 : {
						UPOSMessage_ItemInfo_Item itemInfoItem = null ;
						String custCard_car_type = uPosMsg.getCustCard_car_type() ;

						if (custCard_car_type.equals(IUPOSConstant.CUSTCARD_CAR_TYPE_1) 
								|| custCard_car_type.equals(IUPOSConstant.CUSTCARD_CAR_TYPE_3)) {
							// �ŷ�ó ��ü ����
							LogUtility.getPumpMLogger().info("[Pump M] - TimeOut - Customer Processing.") ;
							POS_DX dxPumpM = new POS_DX((byte[])receivingData.getPreamble()) ;
							String khproc_no = dxPumpM.getKhTransactionID() ;
							T_KH_PUMP_TRData pumpTrData = null ;
							
							try {
								pumpTrData = T_KH_PUMP_TRHandler.getHandler().getT_KH_PUMP_TRData(khproc_no) ;
								boolean isCatPreset = false ;
								
								if (!uPosMsg.getPump_amt().equals("") 
										&& uPosMsg.getMessageType().equals(IUPOSConstant.MESSAGETYPE_4201)
										&& !uPosMsg.getPump_amt().equals("0")) {
									LogUtility.getPumpMLogger().info("[Pump M] Set price, liter from CAT Device.") ;
									pumpTrData.setOil_preset_ind(ICode.OIL_PRESET_IND_1) ;
									pumpTrData.setPreset_qty_prc_ind(ICode.PRESET_QTY_PRC_IND_0_PRICE) ;
									pumpTrData.setPreset_prc(dxPumpM.getPrice()) ;
									pumpTrData.setPreset_baseprice(dxPumpM.getBasePrice()) ;
									isCatPreset = true ;
								}
								
								itemInfoItem = UPOSUtil.getUPOSMessage_ItemInfo_Item(pumpTrData, isCatPreset) ;
							} catch (Exception e) {
								LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
							}

							Preamble preamble = CustUtil.processCustomerInfoToPreamble(itemInfoItem,uniqueKey, messageType, uPosMsg) ;
							LogUtility.getPumpMLogger().info("[Pump M] Send customer processing result to CAT Device..") ;
							
							sendMessage(preamble) ;
						} else if (custCard_car_type.equals(IUPOSConstant.CUSTCARD_CAR_TYPE_4) 
								|| custCard_car_type.equals(IUPOSConstant.CUSTCARD_CAR_TYPE_5)) {
							//����ȭ�� or �������� �ŷ�  �ŷ�ó ��ü ����
							LogUtility.getPumpMLogger().info("[Pump M] - TimeOut - Customer Processing.") ;
							POS_DX dxPumpM = new POS_DX((byte[])receivingData.getPreamble()) ;
							String khproc_no = dxPumpM.getKhTransactionID() ;
							T_KH_PUMP_TRData pumpTrData = null ;
							
							try {
								pumpTrData = T_KH_PUMP_TRHandler.getHandler().getT_KH_PUMP_TRData(khproc_no) ;
								boolean isCatPreset = false ;
								
								if (!uPosMsg.getPump_amt().equals("") 
										&& uPosMsg.getMessageType().equals(IUPOSConstant.MESSAGETYPE_4201)
										&& !uPosMsg.getPump_amt().equals("0")) {
									LogUtility.getPumpMLogger().info("[Pump M] Set price, liter from CAT Device.") ;
									pumpTrData.setOil_preset_ind(ICode.OIL_PRESET_IND_1) ;
									pumpTrData.setPreset_qty_prc_ind(ICode.PRESET_QTY_PRC_IND_0_PRICE) ;
									pumpTrData.setPreset_prc(dxPumpM.getPrice()) ;
									pumpTrData.setPreset_baseprice(dxPumpM.getBasePrice()) ;
									isCatPreset = true ;
								}
								
								itemInfoItem = UPOSUtil.getUPOSMessage_ItemInfo_Item(pumpTrData, isCatPreset) ;
							} catch (Exception e) {
								LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
							}

							Preamble preamble = CustUtil.processCustomerInfoToPreamble(itemInfoItem,uniqueKey, messageType, uPosMsg) ;
							LogUtility.getPumpMLogger().info("[Pump M] Send customer processing result to CAT Device..") ;
							
							sendMessage(preamble) ;
						} else if (custCard_car_type.equals(IUPOSConstant.CUSTCARD_CAR_TYPE_2)) {
							// �ŷ�ó ���� ��ȸ
							LogUtility.getPumpMLogger().info("[Pump M] - Timeout - Car inquiring processing.") ;

							Preamble preamble = CustUtil.processCustomerCarToPreamble(uniqueKey, messageType, uPosMsg) ;
							LogUtility.getPumpMLogger().info("[Pump M] Send Car inquiring result to CAT Device.") ;

							sendMessage(preamble) ;		
						}
						break ;
					}
				}
			} else if (msg instanceof WorkingMessage) {
				LogUtility.getPumpMLogger().info("[Pump M] Timeout = WorkingMessage Class.") ;
				WorkingMessage workingMsg = (WorkingMessage) msg ;
		    	String commandID = workingMsg.getCommand() ;
		    	LogUtility.getPumpMLogger().info("[Pump M] Timeout WorkingMessage Command = "+commandID) ;
		    	
		    	switch (commandID) {
		    		case IPumpConstant.COMMANDID_CA : {
		    			// �پ��� ���� �ܻ� �ŷ� ���� ��û
			    	  	// �� ī�� ��ȸ ��û/���� (DV/DW)			- �پ��뼿��		    			
	    				LogUtility.getPumpMLogger().info("[Pump M] - TimeOut - Customer Processing.") ;
	    				//POSPumpM_DV dvPosMsg = (POSPumpM_DV) sendingObj ;
	    				POS_DV dvPosMsg = new POS_DV((byte[]) sendingObj) ;
						POS_DW dwPumpM = S9Util.processPOSPumpM_DV(dvPosMsg) ;
						CB_WorkingMessage cbWorkMsg = S9Util.convertCBWorkMsgFromPOSDWMsg(dwPumpM) ;
	    				Preamble preamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey, SyncManager.DISE_PUMP_ADAPTER, cbWorkMsg, "") ;
	    				LogUtility.getPumpMLogger().info("[Pump M] Send customer processing result to Pump Adapter.") ;
						sendMessage(preamble);
						
						if ( ICustConstant.DASNO_CUST_TYPE_0.equals(cbWorkMsg.getCustomerType()) ) {
							String receipt ;
							//���� ������ ��� ODT�� ���� �������� �����ش�.
							if (PumpMUtil.getConnectedODTProtocolFromNozzleNo(cbWorkMsg.getConnectNozzleNo()) == 90) {
								receipt = ODTUtility_GSC_Self.createErrorPrintFormat(
										6, 
										cbWorkMsg.getConnectNozzleNo(), 
										dwPumpM.getCust_card_no(), 
										"", 
										cbWorkMsg.getMessage(), 
										"0", 
										GlobalUtility.getDateYYYYMMDDHHMMSS());
							} else  {
								receipt = PumpMessageFormat.createErrorPrintFormat(
										6, 
										0, 
										cbWorkMsg.getConnectNozzleNo(), 
										dwPumpM.getCust_card_no(), 
										"", 
										"", 
										"", 
										"", 
										"",
										cbWorkMsg.getMessage(), 
										"", 
										"0", 
										GlobalUtility.getDateYYYYMMDDHHMMSS());
							}
							QL_WorkingMessage qlWrkMsg = new QL_WorkingMessage(cbWorkMsg.getNozzleNo(), cbWorkMsg.getConnectNozzleNo(), String.valueOf(receipt.length()), receipt, "0", "", "") ;
							
							preamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey, SyncManager.DISE_PUMP_ADAPTER, qlWrkMsg, "");
							//���� ���� ����
							sendMessage(preamble) ;	
						}
						PumpMODTSaleManager.setCustInfo(dwPumpM.getDeviceID(), cbWorkMsg, dwPumpM) ;
			    		break ;
		    		}
		    		// ���� ODT�� �ŷ�ó ���� ���� ���� ó�� - PI2, CWI, 2016-03-23
		    		case IPumpConstant.COMMANDID_GA : {
		    			
		    			// GSC Self �ŷ�ó ����
			    	  	// �� ī�� ��ȸ ��û/���� (DV/DW)		    			
	    				LogUtility.getPumpMLogger().info("[Pump M] - TimeOut - GSC Self Customer Processing.") ;
	    				//POSPumpM_DV dvPosMsg = (POSPumpM_DV) sendingObj ;
	    				POS_DV dvPosMsg = new POS_DV((byte[]) sendingObj) ;
	    				POS_DW dwPumpM = S9Util.processPOSPumpM_DV(dvPosMsg) ;

						// �ܰ� ������ ���� ���� �� 0���� ���õǴ� ������ �־� GB�� ���õ� BasePrice�� �ŷ�ó �ܰ��� �����Ѵ�.
						GB_WorkingMessage gbWorkMsg = S9Util.convertGBWorkMsgFromPOSDWMsg(dwPumpM) ;
						dwPumpM.setBasePrice(gbWorkMsg.getBasePrice());
						
	    				Preamble preamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey, SyncManager.DISE_PUMP_ADAPTER, gbWorkMsg, "") ;
	    				LogUtility.getPumpMLogger().info("[Pump M] Send customer processing result to Pump Adapter.") ;
						sendMessage(preamble) ;
						
						PumpMODTSaleManager.setCustInfo(dwPumpM.getDeviceID(), gbWorkMsg, dwPumpM) ;
			    		break ;
		    		}
		    		case IPumpConstant.COMMANDID_HB : {
		    			// �Ҹ� ���� �ܻ� �ŷ� ���� ��û
			    	  	// �� ī�� ��ȸ ��û/���� (DV/DW)			- �Ҹ���		    			
	    				LogUtility.getPumpMLogger().info("[Pump M] - TimeOut - Customer Processing.") ;
	    				//POSPumpM_DV dvPosMsg = (POSPumpM_DV) sendingObj ;
	    				POS_DV dvPosMsg = new POS_DV((byte[]) sendingObj) ;
						POS_DW dwPumpM = S9Util.processPOSPumpM_DV(dvPosMsg) ;
						HD_WorkingMessage hdWorkMsg = S9Util.convertHDWorkMsgFromPOSDWMsg(dwPumpM) ;
	    				Preamble preamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey, SyncManager.DISE_PUMP_ADAPTER, hdWorkMsg, "") ;
	    				LogUtility.getPumpMLogger().info("[Pump M] Send customer processing result to Pump Adapter.") ;
						sendMessage(preamble) ;
						
						PumpMODTSaleManager.setCustInfo(dwPumpM.getDeviceID(), hdWorkMsg, dwPumpM) ;
			    		break ;
		    		}
			    	case IPumpConstant.COMMANDID_S9 : {
			    		// ������ �� ī�� ���� ��û ����			    	
			    		S9_WorkingMessage s9WorkMsg = (S9_WorkingMessage) msg ;
			    		int s9Mode = Integer.parseInt(s9WorkMsg.getMode()) ;
			    		switch (s9Mode) {
							case 0 : {
					    	  	// �� ī�� ��ȸ ��û/���� (DV/DW)			- ������
								// 1. DV ������ ��ü ������ �̿��Ͽ� DW ������ �����Ѵ�.
								// 2. DW ������ �̿��Ͽ� Pump A �� ������ ���� PG ������ �����Ѵ�.
								// 3. Pump A �� PG ������ �����Ѵ�.
								// 4. DW ������ PG ������ ���� ����ϱ� ���ؼ� �����Ѵ�.
			    				LogUtility.getPumpMLogger().info("[Pump M] ��ü(kixxHub) ������ �� ī�� ������ �����մϴ�.") ;
			    				
			    				POS_DV dvPosMsg = new POS_DV((byte[]) sendingObj) ;
			    				// 1. DV ������ ��ü ������ �̿��Ͽ� DW ������ �����Ѵ�.
								POS_DW dwPumpM = S9Util.processPOSPumpM_DV(dvPosMsg) ;
								// 2. DW ������ �̿��Ͽ� Pump A �� ������ ���� PG ������ �����Ѵ�.
			    				PG_WorkingMessage pgWorkMsg = S9Util.convertPGWorkMsgFromPOSDWMsg(dwPumpM) ;
			    				Preamble preamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey, SyncManager.DISE_PUMP_ADAPTER, pgWorkMsg, "") ;
			    				LogUtility.getPumpMLogger().info("[Pump M] ��ü(kixxHub) ������ �� ī�� ������ �����⿡ �����մϴ�.") ;
			    				// 3. Pump A �� PG ������ �����Ѵ�.
		    					sendMessage(preamble) ;
		    					// 4. DW ������ PG ������ ���� ����ϱ� ���ؼ� �����Ѵ�.
		    					PumpMODTSaleManager.setCustInfo(dwPumpM.getDeviceID(), pgWorkMsg, dwPumpM) ;
								break ;
							}
							case 1 : {
					    		// �� ���� ��ȸ ��û/���� (DT/DU)			- ������
								// 1. DT ������ ��ü ������ �̿��Ͽ� DU ������ �����Ѵ�.
								// 2. DU ������ ������ �м��� �� ��� ���� ������
								//		1) 1�� �� ���, DV ������ ���� POS �� �ٽ� ��û�Ѵ�.
								//		2) 1���� �ƴ� ���, WorkingMessage (NAK or PF ����) �� �����Ͽ� Pump A �� �����Ѵ�.
			    				LogUtility.getPumpMLogger().info("[Pump M] ��ü(kixxHub) ������ ���������� �����մϴ�.") ;
//			    				POSPumpM_DT dtumpM = (POSPumpM_DT) sendingObj ;	
			    				POS_DT dtumpM = new POS_DT((byte[]) sendingObj) ;
			    				// 1. DT ������ ��ü ������ �̿��Ͽ� DU ������ �����Ѵ�.
			    				POS_DU duPosPumpM = CustUtil.processCustomerCar(dtumpM.getMessageID(),
			    						dtumpM.getDeviceID(), dtumpM.getCar_short_no()) ;
			    				
								// 2. DU ������ ������ �м��� �� ��� ���� ������
								//		1) 1�� �� ���, DV ������ ���� POS �� �ٽ� ��û�Ѵ�.
								//		2) 1���� �ƴ� ���, WorkingMessage (NAK or PF ����) �� �����Ͽ� Pump A �� �����Ѵ�.
			    				if (duPosPumpM.getDup() == 1) {
			    					processS9WithDU(uniqueKey, duPosPumpM) ;
			    				} else {
			    					WorkingMessage workMsg = PumpMUtil.processPOSPumpM_DUForPumpA(duPosPumpM) ;
			    					Preamble preamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey, SyncManager.DISE_PUMP_ADAPTER, workMsg, "") ;
			    					LogUtility.getPumpMLogger().info("[Pump M] ��ü(kixxHub) ������ ���������� �����մϴ�.") ;
			    					sendMessage(preamble) ;
			    				}

								break ;
							}
			    		}			    		
			    		break ;
			    	}
			    	case IPumpConstant.COMMANDID_SG : {
			    		// ������ �߱� ��ȸ ��û / ���� (DM/DN)		- ������
			    		LogUtility.getPumpMLogger().info("[Pump M] ��ü(kixxHub) ������ �߱� ��ȸ�� �����մϴ�.") ;
			    		SG_WorkingMessage sgWorkMsg = (SG_WorkingMessage) msg ;
			    		PQ_WorkingMessage pqWorkMsg = PumpMUtil.createPQ_WorkMsg(sgWorkMsg.getMessageID(), 
			    				sgWorkMsg.getNozzleNo(), null) ;
			    		Preamble preamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey, SyncManager.DISE_PUMP_ADAPTER, pqWorkMsg, "") ;
	    				LogUtility.getPumpMLogger().info("[Pump M] ��ü(kixxHub) ������ �߱� ��ȸ ������ �����⿡ �����մϴ�. Mode = 1") ;
    					sendMessage(preamble) ;
			    		break ;
			    	}
		    	}
			} 
		}
    	LogUtility.getPumpMLogger().info("[Pump M] Completed TimeOut processing.") ;
	}
 
    /**
	 * 
	 * ������ �ʱ�ȭ ������ POS �� ���� �޾Ƽ� �̸� ���̺� �����Ѵ�. �޴� ������ ������ ����.
     * 		DB	: �Ϲ� ȯ�� ���� ����
     * 		DD	: ������ ȯ�� ���� ����
     * 
     * @param receiving_posa	: POS �� ���� ���޹��� �ʱ�ȭ ����
     * 
     */
    private void initializeTable(Object receiving_posa) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PosAController/" + "initializeTable()");
		    	
        Preamble receivingData = (Preamble) receiving_posa ;
        String uniqueKey = receivingData.getKey() ;
        byte[] receivingContents = (byte[])receivingData.getPreamble() ;
    	POSHeader posHeader = POSHeader.createHeader(receivingContents) ; 
    	String receivingCommandID = posHeader.getCommandID() ;
    	
		SqlSession session = null;
		boolean rlt = true ;
        
		try {
			session = SqlSessionFactoryManager.openSqlSession();

    		switch (receivingCommandID) {
	    		case IConstant.POSPROTOCOL_COMMANDID_DB : {
		    		POS_DB dbContent = new POS_DB(receivingContents) ;
		    		LogUtility.getPumpMLogger().info("[Pump M] Receive DB (Initialization).") ;
		    		
	    			LogUtility.getPumpMLogger().info(dbContent.toString());

		    		T_NZ_INFOData tNzInfoData = PumpMUtil.createT_NZ_INFOData(dbContent) ;    		
		    		rlt = T_NZ_INFOHandler.getHandler().insertT_NZ_INFOData(session, tNzInfoData) ;
		    		if (rlt == false) {
		    			LogUtility.getPumpMLogger().error("[Pump M] Fail to insert 'T_NZ_INFO' TABLE");
		    		}
		    		LogUtility.getPumpMLogger().debug("[Pump M] completed to insert data into T_NZ_INFO.") ;
		    		gStep = IPumpConstant.STEP_PUMP_DB_COMPLETED ;
					break ;
	    		}
	    		case IConstant.POSPROTOCOL_COMMANDID_DD : {
		    		POS_DD ddContent = new POS_DD(receivingContents) ;
		    		LogUtility.getPumpMLogger().info("[Pump M] Receive DD (Initialization).") ;
		    		
	    			LogUtility.getPumpMLogger().info(ddContent.toString());
		    		if (ddContent.getLast() == IConstant.DUP_LAST) {
		    			gStep = IPumpConstant.STEP_PUMP_DD_COMPLETED ;
		    		} else {
		    			gStep = IPumpConstant.STEP_PUMP_DD_CONTINUING ;
		    		}
		    		
		    		POS_DD_NozzleInfo[] nozzleInfo = ddContent.getNozzleInfoArray() ;
		    		if (nozzleInfo != null) {
		    			for (int i = 0 ; i < nozzleInfo.length ; i++) {
		    				if ((nozzleInfo[i].getDboxrelport_no() != null) && (!nozzleInfo[i].getDboxrelport_no().equals(""))) {
			    				T_NZ_NOZZLEData tNzNozzleData = PumpMUtil.createT_NZ_NOZZLEData(nozzleInfo[i]) ;
				    			rlt = T_NZ_NOZZLEHandler.getHandler().insertT_NZ_NOZZLEData(session, tNzNozzleData) ;
					    		if (rlt == false) {
					    			LogUtility.getPumpMLogger().error("[Pump M] Fail to insert 'T_NZ_NOZZLE' TABLE");
					    		}
		    				} else {
		    					LogUtility.getPumpMLogger().error("[Pump M] No D-Box Port in Pump Info." + nozzleInfo[i].getNozzle_no()) ;
		        				LogUtility.getPumpMLogger().info(nozzleInfo[i].toString());
		    				}
		    			}
		    		}
		    		LogUtility.getPumpMLogger().info("[Pump M] completed to insert data into T_NZ_NOZZLE.") ;
	    			break ;
	    		}
	    		case IConstant.POSPROTOCOL_COMMANDID_KH : {
		    		POS_KH khContent = new POS_KH(receivingContents) ;
		    		LogUtility.getPumpMLogger().info("[Pump M] Receive KH (Initialization)") ;
		    		// debug
    				LogUtility.getPumpMLogger().info(khContent.toString());
		    		if (khContent.getLast() == IConstant.DUP_LAST) {
		    			gStep = IPumpConstant.STEP_PUMP_KH_COMPLETED ;
		    		} else {
		    			gStep = IPumpConstant.STEP_PUMP_KH_CONTINUING ;
		    		}		    		
		    		
		    		POS_KH_NozzleInfo[] khNozzleInfo = khContent.getNozzleInfoArray() ;
		    		insertTimeParameterTable(session, khNozzleInfo, false) ;	    		
	    		}
    		}
   	
		   	switch (gStep) {
			   	case IPumpConstant.STEP_PUMP_DB_COMPLETED : {
			    	POS_DC dcContent = new POS_DC(GlobalUtility.getUniqueMessageID()) ;
			    	Preamble dcPreamble = PumpMUtil.createPOSMessagePreamble(uniqueKey, SyncManager.DISE_POS_ADAPTER, dcContent, "") ;
			    	sendMessage(dcPreamble) ;
			   		break ;
			   	}
			   	case IPumpConstant.STEP_PUMP_DD_COMPLETED : {
			    	POS_KG kgContent = new POS_KG(GlobalUtility.getUniqueMessageID()) ;
			    	Preamble kgPreamble = PumpMUtil.createPOSMessagePreamble(uniqueKey, SyncManager.DISE_POS_ADAPTER, kgContent, "") ;
			    	sendMessage(kgPreamble) ;
			   		break ;
			   	}
			   	case IPumpConstant.STEP_PUMP_KH_COMPLETED : {
			   		sendStartMessage(IPumpConstant.START);    
			   		break ;		   		
			   	}
		   	}
		   	session.commit();
		} catch (Exception e) {
			session.rollback();
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		} 
 
    }
 
    
    /**
     * Time Parameter ���̺� POS �� ���� ���� Time Parameter ������ �����Ѵ�. ����� KH �� �����Ҷ����� (running=true)
     * ���� ������ ���� �� ���� �����Ѵ�
     * 
     * @param khNozzleInfo	: POS �� ���� ���� Time Parameter ����
     * @param running		: KH ���� ����
     */
    private void insertTimeParameterTable(POS_KH_NozzleInfo[] khNozzleInfo, boolean running) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PosAController/" + "insertTimeParameterTable()");
		
		SqlSession session = null;
        
		try {
			session = SqlSessionFactoryManager.openSqlSession();			
			insertTimeParameterTable(session, khNozzleInfo, running) ;
			session.commit();
		} catch (Exception e) {
			session.rollback();
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		} 
    }
    
    
    /**
     * Time Parameter ���̺� POS �� ���� ���� Time Parameter ������ �����Ѵ�. ����� KH �� �����Ҷ����� (running=true)
     * ���� ������ ���� �� ���� �����Ѵ�
     * 
     * @param con			: Connection
     * @param khNozzleInfo	: POS �� ���� ���� Time Parameter ����
     * @param running		: KH ���� ����
     */
    private void insertTimeParameterTable(SqlSession session, POS_KH_NozzleInfo[] khNozzleInfo, boolean running) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PosAController/" + "insertTimeParameterTable()");
		
    	try {
	    	boolean rlt = true ;	    	
			if (khNozzleInfo != null) {
				for (int i = 0 ; i < khNozzleInfo.length ; i++) {
					T_NZ_PARAMETERData tNzNozzleData = PumpMUtil.createT_NZ_PARAMETERData(khNozzleInfo[i]) ;
					if (running) {
						T_NZ_PARAMETERHandler.getHandler().removeT_NZ_PARAMETERData(session, tNzNozzleData.getNozzle_no()) ;
					}
	    			rlt = T_NZ_PARAMETERHandler.getHandler().insertT_NZ_PARAMETERData(session, tNzNozzleData) ;
		    		if (rlt == false) {
		    			LogUtility.getPumpMLogger().error("[Pump M] Fail to insert 'T_NZ_PARAMETER' TABLE.");
		    		}    				
				}
			}	
    	} catch (Exception e) {
    		LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
    	}
    }
 
    
    private boolean isCustInfoSave(int protocolType, WorkingMessage workMsg) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PosAController/" + "isCustInfoSave()");
		
		boolean isSave = true;
		String transType = "";
		String transStatus = "";
		
		switch (protocolType) {
			case IPumpConstant.PUMP_PROTOCOL_SOMO : {
				HD_WorkingMessage hdWrkMsg = (HD_WorkingMessage)workMsg;
	
				transType = hdWrkMsg.getTransType();
				transStatus = hdWrkMsg.getTransStatus();
				hdWrkMsg = null;
				break ;
			}
			case IPumpConstant.PUMP_PROTOCOL_Recharge : {
				PG_WorkingMessage pgWrkMsg = (PG_WorkingMessage)workMsg;
	
				transType = pgWrkMsg.getTransType();
				transStatus = pgWrkMsg.getTransStatus();
				pgWrkMsg = null;
				break ;
			}
			case IPumpConstant.PUMP_PROTOCOL_DaSNo : 
			case IPumpConstant.PUMP_PROTOCOL_NewDaSNo :{	//2012.09.26 ksm
				CB_WorkingMessage cbWrkMsg = (CB_WorkingMessage)workMsg;
				if (cbWrkMsg.getCustomerType().equals(ICustConstant.DASNO_CUST_TYPE_0))
					return false;
				else
					return true;
			}
		}
		
		
		if (transType.equals("2") || 
				transStatus.equals("2") ||
				transStatus.equals("3") ) {
			// transType �� �̵��ī�尡 �ƴ� ��
			// transStatus �� ������ �ƴ� ��
			// transStatus �� ���Ұ� �ƴ� �� �� ������
			LogUtility.getPumpMLogger().debug("[Pump M] ����ŷ�ó�� �ƴ� ��� �ŷ�ó ������ �������� �ʴ´�.");
			isSave = false ;			
		} 		
		return isSave;
	}
 
    /**
	 * POS �� KixxHub �� ���� ���¸� �����Ѵ�.
	 */
	@Override
	public boolean isPOSConnected() {
		return isPOSStarted;
	}
 
    /**
     * POS A �κ��� ������ �����Ѵ�. ���� �� �ִ� ������ ������ ����.
     *		DB	: �Ϲ� ȯ�� ���� ����		
     * 		DD	: ������ ȯ�� ���� ����		
     * 		DI	: ���� ���� ��û			
     * 		DK	: ����/���� ���� ��û		
     * 		DN	: ������ �߱���ȸ ����		
     * 		DO	: Preset �ڷ� ��û		
     * 		DR	: Locking/unLocking ��û
     * 		KJ	: ������ ������ ���� ��û
     *      CX  : SelfODT Update/Install ��û 
     * 
     * @param receiving_posa	: POS A �κ��� ���� ����
     */
    @Override
	protected void onReceivingPosAData(Object receiving_posa) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PosAController/" + "onReceivingPosA()");
			
    	try {
	        Preamble receivingData = (Preamble) receiving_posa ;
	        
	        switch (receivingData.getFail()) {
	        	case Preamble.FAIL_FLAG_POS_NOT_WORKING :
	        	case Preamble.FAIL_FLAG_POS_TIMEOUT : {
	        		LogUtility.getPumpMLogger().info("[Pump M] Receive Message from POS A because of timeout.") ;
	        		handleTimeOutContent(receivingData) ;
	        		return ;
	        	}
	        }
	        
	        String uniqueKey = receivingData.getKey() ;
	        byte[] receivingContents = (byte[])receivingData.getPreamble() ;
	    	POSHeader posHeader = POSHeader.createHeader(receivingContents) ; 
	    	String receivingCommandID = posHeader.getCommandID() ;
	    	String nozID = posHeader.getDeviceID();
	    	
	    	PumpLogUtil.printContent(uniqueKey, receivingData.getFrom(),receivingData.getDest(),receivingData.getPreamble()) ;
	
	    	if (gStep != IPumpConstant.STEP_PUMP_RUNNING) {
	    		switch (receivingCommandID) {
	    			case IConstant.POSPROTOCOL_COMMANDID_DB :
	    			case IConstant.POSPROTOCOL_COMMANDID_DD :
	    			case IConstant.POSPROTOCOL_COMMANDID_KH :
	    				initializeTable(receiving_posa) ;
	    				break ;
	    		}
	    	} else {
	    		Preamble pumpPreamble = null ;
	    		switch (receivingCommandID) {
	    		
	    			// added by yhcheon at 2009.05.12 start
	    			// �ǽð� �ݿ��� ���Ͽ�.
	    			case IConstant.POSPROTOCOL_COMMANDID_DB :

	    				update_T_NZ_INFO(receiving_posa) ;
	    				
	    				// Pump Adapter���� P1, P2 ������ ������ �κ�..
	    				String storeCode;
	    				try {
	    					storeCode = T_KH_STOREHandler.getHandler().getStoreCode();
	    					
	    					ArrayList<WorkingMessage> selfWorkMsgArray = PumpMUtil.createSelfODTInitWorkingMessage2(storeCode, null) ;					
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
	    				
	    				// ����� �����ݾ� �ʱ�ȭ
	    				PumpMUtil.setNull_Sa_min_amt() ;
	    				
	    				break ;
	    			// added by yhcheon at 2009.05.12 end
	    				
	    				
	    			/**
	    			 * ���� ���� ���� ��, POS �� ���� ������ ������ ���� ������ ������ Pump A �� �����Ѵ�. 
	    			 */
	    			case IConstant.POSPROTOCOL_COMMANDID_KI : {
//	    				POSPumpM_KI kiContent = new POSPumpM_KI(receivingContents) ;
//	    				LogUtility.getPumpMLogger().info("[Pump M] Receive GasODT Recharger Reset from POS. NozID="+nozID) ;
//	    				
//	    				P9_WorkingMessage p9WorkMsg = PumpMUtil.createP9WorkingMessage(kiContent) ;
//	    				LogUtility.getPumpMLogger().info("[Pump M] createP9WorkingMessage. NozID="+nozID) ;
//	    				
//	    				pumpPreamble = PumpMUtil.createPreamble(uniqueKey,SyncManager.DISE_PUMP_ADAPTER, p9WorkMsg, "") ;
//	    		    	LogUtility.getPumpMLogger().info("[Pump M] Send P9 to Pump A. NozID="+nozID) ;
//	    				sendMessage(pumpPreamble) ;  
	    				break ;
	    				
	    			}
		    		/**
		    		 * ���� Time Parameter �� �����Ѵ�. 
		    		 * ���� Time Parameter �� �ʱ� ȯ�� �������� �� �޾ƾ� �� �����̸�, ���� KH �����߿��� ������ �ִ�.
		    		 * �� ������ ������ Table �� Update �� �� ���� Pump A �� �����Ѵ�.
		    		 */
					case IConstant.POSPROTOCOL_COMMANDID_KH : {
						// ���� Time Parameter 
			    		POS_KH khContent = new POS_KH(receivingContents) ;
			    		LogUtility.getPumpMLogger().info("[Pump M] Receive Time Parameter Master from POS. NozID="+nozID) ;
			    		// debug
		    			LogUtility.getPumpMLogger().info(khContent.toString());
			    		POS_KH_NozzleInfo[] khNozzleInfo = khContent.getNozzleInfoArray() ;
			    		insertTimeParameterTable(khNozzleInfo, true) ;
			    		
			    		sendP7WorkingMessageToPumpA(khContent) ;					
						break ;
					}
					/**
					 * POS �� ���� �������� ��û (�������/�������) �� �����Ѵ�.
					 * �̴� �� ���� ���ؼ� ��û ���� ���� �ְ�, ��ü ���� ���ؼ� ��û�� �������� �ִ�.
					 * Device ID �� 0000 �� ���� ��ü ����� ���ֵȴ�.
					 */
	    			case IConstant.POSPROTOCOL_COMMANDID_DI : {
	    				// ���� ���� ��û	
	    		    	LogUtility.getPumpMLogger().info("[Pump M] Receive Pump Control from POS. NozID="+nozID) ;
	        			POS_DI diMsg = new POS_DI(receivingContents) ;
		    			LogUtility.getPumpMLogger().info(diMsg.toString());
	        			processDI(uniqueKey, diMsg) ;
	    		    	LogUtility.getPumpMLogger().info("[Pump M] Completed Pump Control. NozID="+nozID) ;
	    				break ;
	    			}
	    			/**
	    			 * POS �� ���� Preset ��û�� �����Ѵ�.
	    			 * ���� ���� Pump A �� ������ Preset �� ��û�ϰ�, POS �δ� KH ó����ȣ�� �����Ͽ� �����Ѵ�.
	    			 */
	    			case IConstant.POSPROTOCOL_COMMANDID_DK : {
	    				// ����/���� ���� ��û	
	    		    	LogUtility.getPumpMLogger().info("[Pump M] Receive Fixed Price/Liter Pump from POS. NozID="+nozID) ;
	        			POS_DK dkMsg = new POS_DK(receivingContents) ;
	    				// Debug
		    			LogUtility.getPumpMLogger().info(dkMsg.toString());
	
	        			PB_WorkingMessage pbWorkMsg = PumpMUtil.createPB_WorkMsg(dkMsg) ;
	        			
	        	    	String khproc_no = 
	        				PumpMTransactionManager.getInstance().getKHTransactionID(pbWorkMsg.getNozzleNo(), IPumpConstant.KH_PUMP_PRESET) ;
	        	    	PumpMTransactionManager.getInstance().setPresetInfo(pbWorkMsg.getNozzleNo(), IPumpConstant.PRESET_FROM_POS) ;
	        	    	
	        	    	// 2012.07.20 ksm  PB_WorkingMessage�� barcode �ʵ� ����.
	        	    	// twsongkis 2015-01-28 ���ο� ���ڵ� �������� barcode ����
	        	    	pbWorkMsg.setBarCode(Barcode.getBarcodeNumber("A", pbWorkMsg.getPrice(), nozID, khproc_no, null, null, null));
	        	    	
	        			try {
	        				T_KH_PUMP_TRHandler.getHandler().updatePresetInfo_BY_khproc_no(khproc_no, 
	        						dkMsg.getCommand(), dkMsg.getLiter(), dkMsg.getPrice(), dkMsg.getPreset_baesPrice()) ;
	        			} catch (Exception e) {
	        				LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
	        			}
	    				pumpPreamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey,SyncManager.DISE_PUMP_ADAPTER, pbWorkMsg, "") ;
	    		    	LogUtility.getPumpMLogger().info("[Pump M] Request Fixed Price/Liter to Pump A. NozID="+nozID) ;
	    				sendMessage(pumpPreamble) ; 
	    				
	    				// KH ó����ȣ�� POS �� �����Ѵ�.
	    				POS_DL dlPumpM = new POS_DL(dkMsg.getMessageID(), dkMsg.getDeviceType(), dkMsg.getDeviceID(), khproc_no) ;
	    				pumpPreamble = PumpMUtil.createPOSMessagePreamble(null,SyncManager.DISE_POS_ADAPTER, dlPumpM, "") ;
	       		    	LogUtility.getPumpMLogger().info("[Pump M] Respond Fixed Price/Liter to POS. NozID="+nozID) ;
	    				sendMessage(pumpPreamble) ;        		
	    				
	    				break ;
	    			}
	    			/**
	    			 * POS �� ���� �� �����⿡ ������ Preset ���� ��û�� �����Ͽ� �����Ѵ�. ������ �ʴ� �����̴�.
	    			 */
	    			case IConstant.POSPROTOCOL_COMMANDID_DO : {
	    				// Preset �ڷ� ��û	    				
	    		    	LogUtility.getPumpMLogger().info("[Pump M] Receive DO from POS. NozID="+nozID) ;
	        			POS_DO doMsg = new POS_DO(receivingContents) ;
	    				// Debug
		    			LogUtility.getPumpMLogger().info(doMsg.toString());
	        			try {
		    				QF_WorkingMessage qfWorkMsg = PumpMUtil.createQF_WorkMsg(doMsg) ;
		    				pumpPreamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey,SyncManager.DISE_PUMP_ADAPTER, qfWorkMsg, "") ;
		    		    	LogUtility.getPumpMLogger().info("[Pump M] Send QF to Pump A. NozID="+nozID) ;
		        			PumpMSyncManager.setSyncData(doMsg.getMessageID(), 
		        					doMsg.getMessageID(), SyncManager.DISE_PUMP_MODULE, receiving_posa) ;
		    		    	sendMessage(pumpPreamble) ;
	        			} catch (Exception e) {
	        				LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
	        			}
	    				break ;
	    			}
	    			/**
	    			 * �� �������� �ߺ� ���� ������ ���ؼ� Locking Ȥ�� unLocking �� �����Ѵ�.
	    			 * ���� Locking Ȥ�� unLocking �� �̹� ������ ��쿡�� ���� ������ �������θ� �����Ѵ�.
	    			 */
	    			case IConstant.POSPROTOCOL_COMMANDID_DR : {
	    				// Locking/unLocking ��û	    				
	    		    	LogUtility.getPumpMLogger().info("[Pump M] Receive Locking/unLocking From POS. NozID="+nozID) ;
	    				POS_DR drMsg = new POS_DR(receivingContents) ;
	    				// Debug
	    				LogUtility.getPumpMLogger().info(drMsg.toString());
	    				String isOK = IConstant.PUMP_SALE_LOCK_UNLOCK_OK ;
	    				if (drMsg.getIsLock().equals(IConstant.PUMP_SALE_LOCKING)) {
	    					isOK = LockingManager.locking(drMsg.getDeviceID(), 
	    							drMsg.getKhTransactionNum(),IConstant.LOCKING_SRC_POS, false) ;
	    				} else {
	    					isOK = LockingManager.unlocking(drMsg.getDeviceID(), drMsg.getKhTransactionNum(), false) ;    					
	    				}
	    				POS_DS dsPumpm = new POS_DS(drMsg.getMessageID(), drMsg.getDeviceType(),
	    						drMsg.getDeviceID(), drMsg.getKhTransactionNum() , drMsg.getIsLock() , isOK) ;
	    				
	         			Preamble preambleData = PumpMUtil.createPOSMessagePreamble(null
	         					, SyncManager.DISE_POS_ADAPTER, dsPumpm, "") ;
	    		    	LogUtility.getPumpMLogger().info("[Pump M] Respond Locking/unLocking result to POS. NozID="+nozID) ;
	         			sendMessage(preambleData) ;
	    				break ;
	    			}
	    			/**
	    			 * Pump M ������ locking �� ������ �������� ��� ���� 1�� ���� unLocking �� ���ŵ��� �ʴ´ٸ�
	    			 * ������ locking �� �����Ѵ�. ������ ���� POS �� ���� locking �������� ������ ������ ���� �ʰ� 
	    			 * POS �� locking ���� ���� ��û�Ѵ�. �� ���� ���� ������ KF �����̴�.
	    			 */
	    			case IConstant.POSPROTOCOL_COMMANDID_KF : {
	    				// Locking ������û�� ���� ����
	    				LogUtility.getPumpMLogger().info("[Pump M] Receive Locking Release from POS. NozID="+nozID) ;
	    				POS_KF kfMsg = new POS_KF(receivingContents) ;
	    				
	    				LogUtility.getPumpMLogger().info(kfMsg.toString());
	    				if (kfMsg.getIsOK().equals("0")) {
	    					LockingManager.unlocking(kfMsg.getDeviceID(), kfMsg.getKhTransactionNum(), true) ;  
	    				}
	    				break ;
	    			}
	    			/**
	    			 * �� �����ȣ�� ���� ���� ��û�� POS �� �����ϰ� �׿� ���� ������ DU �������� �����Ѵ�.
	    			 * ���� ��ȸ ��û�� CAT �ܸ���, �Ҹ��� �׸��� ������� ���� �߻��� �� �ִ�.
	    			 */
	    			case IConstant.POSPROTOCOL_COMMANDID_DU : {
	    				// �� ���� ��ȸ ����
	    				LogUtility.getPumpMLogger().info("[Pump M] Receive DU from POS. NozID="+nozID) ;
	    				POS_DU duMsg = new POS_DU(receivingContents) ;
	    				// Debug
	    				LogUtility.getPumpMLogger().info(duMsg.toString());
	    				proceedDU(uniqueKey, duMsg) ;
	    							
	    				break ;
	    			}
	    			/**
	    			 * �� ī�� ��ȸ ��û�� ���� ������ �����Ѵ�.
	    			 * �̴� ������ �� �Ҹ��� �� ���� ��û�� �� �� ������, �̿� ���� ������ �����Ѵ�.
	    			 * �پ��� ���� �߰� edited by ykjang
	    			 */
	    			case IConstant.POSPROTOCOL_COMMANDID_DW : {
	    				// �� ī�� ��ȸ ����
	    				LogUtility.getPumpMLogger().info("[Pump M] Receive DW from POS. NozID="+nozID) ;
	    				POS_DW dwMsg = new POS_DW(receivingContents) ;
	    				// Debug
	    				LogUtility.getPumpMLogger().info(dwMsg.toString());
	    				
	    				processDW(uniqueKey, dwMsg) ;
	    				break ;
	    			}
	    			/**
	    			 * �� ī�� ���� ��û�� ���� ������ �����Ѵ�.
	    			 * �̴� CAT �ܸ���κ��� ��û�� ���� �����̴�.
	    			 */
	    			case IConstant.POSPROTOCOL_COMMANDID_DY : {
	    				// �� ī�� ����  ����
	    				LogUtility.getPumpMLogger().info("[Pump M] Receive DY from POS. NozID="+nozID) ;	
	    				POS_DY dyMsg = new POS_DY(receivingContents) ;
	    				// Debug
	    				LogUtility.getPumpMLogger().info(dyMsg.toString());
	    				processDY(uniqueKey, dyMsg) ;
	    				
	    				break ;
	    			}
	    			/**
	    			 * ������� �ʴ� �����̴�.
	    			 */
	    			case IConstant.POSPROTOCOL_COMMANDID_DN : {
	    				// ������ �߱���ȸ ����	
	    		    	LogUtility.getPumpMLogger().info("[Pump M] Receive DN from POS. NozID="+nozID) ;
	        			POS_DN dnMsg = new POS_DN(receivingContents) ;
	    				// Debug
	    				LogUtility.getPumpMLogger().info(dnMsg.toString());
	    				PQ_WorkingMessage pqWorkMsg = PumpMUtil.createPQ_WorkMsg(dnMsg.getMessageID(), dnMsg.getDeviceID(), dnMsg) ;
	    				pumpPreamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey,SyncManager.DISE_PUMP_ADAPTER, pqWorkMsg, "") ;
	    		    	LogUtility.getPumpMLogger().info("[Pump M] Send PQ to Pump A. NozID="+nozID) ;
	    				sendMessage(pumpPreamble) ;        			
	        			break ;
	    			}
		    		
	    			/**
	    			 * �����������ο� ���� ������ �����Ѵ� - 2010.02.13 �ּ���
	    			 * 
	    			 */
		    		case IConstant.POSPROTOCOL_COMMANDID_PB : {
		    			LogUtility.getPumpMLogger().info("[Pump M] �����������ο� ���� ������ �����Ѵ� Receive PB from POS. NozID="+nozID) ;
		    			
		    			storeCode = T_KH_STOREHandler.getHandler().getStoreCode();
		    			LogUtility.getPumpMLogger().info("[Pump M] Receive PB from POS. storeCode="+storeCode) ;
		    			
		    			WorkingMessage p5workMsg = PumpMUtil.getP5WorkingMessageForFullPumping(storeCode, null) ;
		    			
		    			if (p5workMsg != null) {
		    				p5workMsg.setMessageID(GlobalUtility.getUniqueMessageID()) ;
		    				Preamble p5Preamble = PumpMUtil.createWorkingMessagePreamble(SyncManager.getUniqueKey(),
		    	    				SyncManager.DISE_PUMP_ADAPTER, p5workMsg , "") ;
		    				sendMessage(p5Preamble) ;
		    				
		    				// P5 ������ ���� ODT �� ����
		    				LogUtility.getPumpMLogger().info("[Pump M] Send P5 to Self ODT") ;
		    			}
		    			
		    			break ;
		    		}
		    		
		    		/**
	    			 * Beacon 
	    			 * POS�κ��� ���ŵ� BEACON ���� ���������� ��ִ������� ���� ��. 
	    			 * 
	    			 */
		    		case IConstant.POSPROTOCOL_COMMANDID_HE : {
		    			/*LogUtility.getLogger().info("[Pump M] ���ܰ������� ���� ��û�� ���� Receive HE from POS.") ;
		    			*/

		    			//PropertyID.P_HEADER_LENGTH -> POSHeader.startBodyOffset ���� ����
		    			//pumpM���� com.gsc.kixxhub.device.module.cat.CatModuleProc ���� �Ұ�
		    			
		    			int uposLength = receivingContents.length - POSHeader.startBodyOffset;
						if (uposLength > 90) {
							byte[] 	uposBytes = new byte[uposLength];
						    System.arraycopy(receivingContents, POSHeader.startBodyOffset, uposBytes, 0, uposLength); 	// UPOS ���� �κ�
			    			UPOSMessage upos = UPOSMessageUtility.createUPOSMessage(uposBytes);
			    			
			    			Preamble preamble = PumpMUtil.createUPOSMessagePreamble(uniqueKey,
									SyncManager.DISE_PUMP_ADAPTER,
									SyncManager.DISE_CAT_MODULE, upos, "");
		    				sendMessage(preamble) ;
						}
		    			break ;
		    		}
		    		case IConstant.POSPROTOCOL_COMMANDID_CX: {
	    		    	LogUtility.getPumpMLogger().info("[Pump M] Receive CX from POS. NozID="+nozID) ;
	        			POS_CX cxMsg = new POS_CX(receivingContents) ;
	        			LogUtility.getPumpMLogger().info(cxMsg.toString());
	        			processCX(uniqueKey, cxMsg) ;
		    			break;
		    		}
	    		}
	    	}
    	} catch (Exception e) {
    		LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
    	} catch (Throwable e1) {
    		LogUtility.getPumpMLogger().error(e1.getMessage(),e1) ;
    	}
    }

	/**
     * POS A �� ���� ������ �ʱ�ȭ ��û�� �޴´�.
     * 
     * @param input_posa_start	: ���� �ΰ��� String �� �ϳ��� �޴´�.
     * 		WITHPOS		: POS �� ������ �Ǿ��� ������, POS �� ���� �ʱ�ȭ ���̺� ������ ���� �� ������/ODT �ʱ�ȭ�� �Ѵ�.
     * 		WITHOUTPOS	: POS �� ������ �ȵǱ� ������, KixxHub �� ���� ���̺��� �̿��Ͽ� ������/ODT �ʱ�ȭ�� �Ѵ�.
     * 
     */
    @Override
	protected void onReceivingPosAInitReq(String input_posa_start) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PosAController/" + "onInputPosaStart()");
		
		LogUtility.getPumpMLogger().info("[Pump M] Start Pump Initialization " + input_posa_start) ;
    	if (input_posa_start.equals(IConstant.KH_WITHPOS_START)) {        	
    		removeInitializeTable() ;
        	POS_DA pumpMDA = new POS_DA(GlobalUtility.getUniqueMessageID()) ;

        	Preamble preamble = PumpMUtil.createPOSMessagePreamble(SyncManager.getUniqueKey(), SyncManager.DISE_POS_ADAPTER, pumpMDA, "") ;

        	gStep = IPumpConstant.STEP_PUMP_START ;    		
        	sendMessage(preamble) ;
    	} else if (input_posa_start.equals(IConstant.KH_WITHOUTPOS_START)){
    		sendStartMessage(IPumpConstant.START);    		
    	}
    }
 
    
	/**
     * 
     * POS A �� ����, KixxHub �� POS �� ������� ������ �޴´�
     * 
     * @param input_posa_isPosStarted	: true or false
     * 
     */
    @Override
	protected void onReceivingPosAIsPosConnected(boolean input_posa_isPosStarted) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PosAController/" + "onReceivingPosA_POSStarted()");
		
    	LogUtility.getPumpMLogger().info("[Pump M] POS ������� ����=" + input_posa_isPosStarted) ;
        isPOSStarted = input_posa_isPosStarted ;
    }
    
    
    
    
    /**
     * �ٸ� Controller �� ���� ���� ������ POS A �� �����Ѵ�. ������ �ִ� ������ ������ ����.
     * 		DE	: ���� �� ���� ����			: PumpAController
     * 		DG	: ���� �Ϸ� ���� ����			: PumpAController
     * 		DM	: ������ �߱� ��ȸ ��û		: PumpAController
     * 		DP	: Preset �ڷ� ��û ����		: PumpAController
     * 		DQ	: Preset ���� ���� 			: PumpAController
     * 		DS	: Locking/unLocking ����	: PumpAController
     *      CY  : selfODT Version ����     : PumpAController
     *      
     * @param sending_posa				
     */
    @Override
	protected void onReceivingPumpMData(Object sending_posa) {
//		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PosAController/" + "onSendingPosA()");
		
        sendMessage((Preamble)sending_posa) ; 
    }
    
    /**
     * 
     * POS A ���� ������ ��ü �ʱ�ȭ�� �Ϸ�Ǿ����� �����Ѵ�.
     * 
     * @param input_pumpa_completed	: true or false
     * 
     */
    @Override
	protected void onReceivingPumpMInitCompleted(boolean input_pumpa_completed) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PosAController/" + "OnInputPumpACompleted()");
		
    	if (input_pumpa_completed == true) {
    		LogUtility.getPumpMLogger().info("[Pump M] Completed Pump Initialization.") ;
    		gStep = IPumpConstant.STEP_PUMP_RUNNING ;    
    	}
    }
    
    /**
     * POS �� ���� ���� ���� ��ȣ ��û�� ���� ������ �޾Ƽ� �̸� CAT M or Pump A ���� �����ϵ��� �Ѵ�.
     * 
     * @param uniqueKey	: Unique Key
     * @param duMsg		: POS �� ���� ���� DU ����
     *
     */
    private void proceedDU(String uniqueKey, POS_DU duMsg) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PosAController/" + "proceedDU()");
		
    	Object uPosPreambleObj = null ;
		
		uPosPreambleObj = PumpMSyncManager.getPreambleAndRemove(uniqueKey) ;
		if (uPosPreambleObj != null) {
			Preamble posPreamble = (Preamble) uPosPreambleObj ;
			Object obj = posPreamble.getPreamble() ; ;
			if (obj instanceof UPOSMessage) {
				// CAT M ���� ���� ��û�� ���
				UPOSMessage uPosMsg = (UPOSMessage) obj ;

				UPOSMessage returnPOSMsg = null ;
				if (uPosMsg.getMessageType().equals(IUPOSConstant.MESSAGETYPE_4201)) {
					returnPOSMsg = UPOSUtil.process4201_4291(uPosMsg, duMsg, false) ;
				} else {
					returnPOSMsg = UPOSUtil.process4201_4291(uPosMsg, duMsg, true) ;
				}
				Preamble preamble = PumpMUtil.createUPOSMessagePreamble(posPreamble.getKey(), SyncManager.DISE_PUMP_MODULE, 
						SyncManager.DISE_CAT_MODULE, returnPOSMsg, "") ;
				sendMessage(preamble) ;	
				LogUtility.getPumpMLogger().info("[Pump M] �� ���� ���� ������ CAT M ���� �����մϴ�." ) ;
			} else if (obj instanceof WorkingMessage) {
				// Pump A �κ����� ��û�� ���				
				
				// �������� ��� �����ȣ�� ��ȸ�� ������ �ϳ��� ��� �ٽ� POS �� �� ī�� ��ȸ ��û�� �����Ѵ�.
				if (duMsg.getDup() == 1) {
					processS9WithDU(uniqueKey, duMsg) ;
				} else {
					WorkingMessage workMsg = PumpMUtil.processPOSPumpM_DUForPumpA(duMsg) ;
					Preamble preamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey,SyncManager.DISE_PUMP_ADAPTER, workMsg, "") ; 
					sendMessage(preamble) ;	
					LogUtility.getPumpMLogger().info("[Pump M] �� ���� ���� ������ Pump A ���� �����մϴ�." ) ;
				}
				
			} else {
				LogUtility.getPumpMLogger().warn("[Pump M] POS �� ���� DU ������ �����Ͽ����� �۽��� ������ ã���� �����ϴ�.") ;
			}
		} else {
			LogUtility.getPumpMLogger().warn("[Pump M] timeout �� �Ǿ �̹� ó���Ǿ����ϴ�.") ;
		}    	
    }
    

    /**
     * POS �� ���� �������� ����� �޾Ƽ� �̸� Pump A ���� �����Ѵ�.
     * �������� ����� ������ ���� ���еȴ�.
     * 	Command
     * 		0 : ����(����) ����
     * 		1 : ����(����) ���� ����
     * 		2 : ��ü ����(����) ����
     * 		3 : ��ü ����(����) ���� ����
     * 
     * @param uniqueKey	: Unique Key
     * @param diMsg		: �������� �� ���� (DI ����)
     */
    private void processDI(String uniqueKey, POS_DI diMsg) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PosAController/" + "processDI()");
		
		Preamble pumpPreamble = null ;
		String[] nozIDList = null ;
		
		switch (Integer.parseInt(diMsg.getCommand())) {
			case IConstant.POSPROTOCOL_DI_PUMP_LOCK :
			case IConstant.POSPROTOCOL_DI_PUMP_UNLOCK : {
				PA_WorkingMessage paWorkMsg = PumpMUtil.createPA_WorkMsg(diMsg) ;
				
				pumpPreamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey,
						SyncManager.DISE_PUMP_ADAPTER, paWorkMsg, "") ;
				sendMessage(pumpPreamble) ;
				break ;
			}
			case IConstant.POSPROTOCOL_DI_PUMP_ALL_LOCK : {
				try {
					nozIDList = T_NZ_NOZZLEHandler.getHandler().getNozIDWithoutSelfODT() ;
				} catch (Exception e) {
					LogUtility.getPumpMLogger().error(e.getMessage(),e);
					return ;
				}
				if (nozIDList != null) {    						
			        for (int i = 0 ; i < nozIDList.length ; i++) {
			    		PA_WorkingMessage paWorkMsg = PumpMUtil.createPA_WorkMsg(diMsg) ;
			        	paWorkMsg.setNozzleNo(nozIDList[i]) ;
						paWorkMsg.setNozzleState(Integer.toString(IConstant.POSPROTOCOL_DI_PUMP_LOCK)) ;

    					pumpPreamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey,SyncManager.DISE_PUMP_ADAPTER, paWorkMsg, "") ;
    					sendMessage(pumpPreamble) ;
			        }
				}
				break ;
			}
			case IConstant.POSPROTOCOL_DI_PUMP_ALL_UNLOCK: {
				try {
					nozIDList = T_NZ_NOZZLEHandler.getHandler().getNozIDWithoutSelfODT() ;
				} catch (Exception e) {
					LogUtility.getPumpMLogger().error(e.getMessage(),e);
					return ;
				}
				if (nozIDList != null) {    						
			        for (int i = 0 ; i < nozIDList.length ; i++) {
			    		PA_WorkingMessage paWorkMsg = PumpMUtil.createPA_WorkMsg(diMsg) ;
			        	paWorkMsg.setNozzleNo(nozIDList[i]) ;
						paWorkMsg.setNozzleState(Integer.toString(IConstant.POSPROTOCOL_DI_PUMP_UNLOCK)) ;

    					pumpPreamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey,SyncManager.DISE_PUMP_ADAPTER, paWorkMsg, "") ;
    					sendMessage(pumpPreamble) ;
			        }
				}
				break ;
			}
		}
    }
    

    /**
     * POS ���� �� ī�� ��ȸ ��û�� ���� ������ �޾Ƽ� Pump A ���� �����Ѵ�.
     * 
     * @param uniqueKey	: Preamble Object �� Unique Key
     * @param dwMsg		: POS �� ���� ���� DW ���� 
     */
	private void processDW(String uniqueKey, POS_DW dwMsg) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PosAController/" + "processDW()");
		
		Object uPosPreambleObj = PumpMSyncManager.getPreambleAndRemove(uniqueKey) ;
		WorkingMessage workMsg = null ;
		
		if (uPosPreambleObj != null) {
			Preamble preamble = null ;
			int protocolType = PumpMUtil.getConnectedRepODTProtocolFromNozzleNo(dwMsg.getDeviceID()) ;
			
			switch (protocolType) {
				case IPumpConstant.PUMP_PROTOCOL_SOMO : {
					LogUtility.getPumpMLogger().info("[Pump M] �Ҹ��� �� ���� �����Դϴ�.") ;
					workMsg = S9Util.convertHDWorkMsgFromPOSDWMsg(dwMsg) ;
					preamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey, 
							SyncManager.DISE_PUMP_ADAPTER, workMsg, "") ;
					break ;
				}
				case IPumpConstant.PUMP_PROTOCOL_NewRecharge :   //2016.03.28 ������ 
				case IPumpConstant.PUMP_PROTOCOL_Recharge : {
					LogUtility.getPumpMLogger().info("[Pump M] ������ �� ���� �����Դϴ�.") ;
					workMsg = S9Util.convertPGWorkMsgFromPOSDWMsg(dwMsg) ;
					preamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey, 
							SyncManager.DISE_PUMP_ADAPTER, workMsg, "") ;
					break ;
				}
				case IPumpConstant.PUMP_PROTOCOL_DaSNo : 
				case IPumpConstant.PUMP_PROTOCOL_NewDaSNo :{	//2012.09.26 ksm
					LogUtility.getPumpMLogger().info("[Pump M] �پ��뼿�� �� ���� �����Դϴ�.") ;
					workMsg = S9Util.convertCBWorkMsgFromPOSDWMsg(dwMsg) ;
					preamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey, 
							SyncManager.DISE_PUMP_ADAPTER, workMsg, "") ;
					break ;
				}
				// PI2, �ű� ODT �߰��� ���� ���μ��� �߰�, 2016.01.15 - CWI
				case IPumpConstant.PUMP_PROTOCOL_GSC_SELF :{ 
					LogUtility.getPumpMLogger().info("[Pump M] GSCSelf �� ���� �����Դϴ�.") ;
					workMsg = S9Util.convertGBWorkMsgFromPOSDWMsg(dwMsg);
					
					// �ܰ� ������ ���� ���� �� 0���� ���õǴ� ������ �־� GB�� ���õ� BasePrice�� �ŷ�ó �ܰ��� �����Ѵ�.
					GB_WorkingMessage gbwkm = (GB_WorkingMessage) workMsg;
					dwMsg.setBasePrice(gbwkm.getBasePrice());
					
					preamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey, 
							SyncManager.DISE_PUMP_ADAPTER, workMsg, "") ;
					break ;
				}
				default : {
					LogUtility.getPumpMLogger().warn("[Pump M] DW ������ ���� ������ ���� ���� �Ҹ����� �����⵵ �ƴմϴ�. �̻��ϱ���.") ;
					break ;
				}
			}
			
			LogUtility.getPumpMLogger().info("[Pump M] ��ī�� ���� ���� ������ Pump A ���� �����մϴ�.") ;
			sendMessage(preamble) ;	
			
			//�پ��� ���� �ŷ�ó�� apl�� ���� ��쿡 ���κҰ� ó���� �� �� ������ ����� �Ѵ�.
			// switch�� �ȿ� ���� ���� ���� : ���� ������ ���� ������ ���� ó���ؾ� ��.
			if (protocolType == IPumpConstant.PUMP_PROTOCOL_DaSNo ) {
				CB_WorkingMessage cbWrkMsg = (CB_WorkingMessage)workMsg;
				
				if (ICustConstant.DASNO_CUST_TYPE_0.equals(cbWrkMsg.getCustomerType())) {
					// ���� ���� �޼��� ���
					String receipt ;
					//���� ������ ��� ODT�� ���� �������� �����ش�.
					if (PumpMUtil.getConnectedODTProtocolFromNozzleNo(cbWrkMsg.getConnectNozzleNo()) == 90) {

						receipt = ODTUtility_GSC_Self.createErrorPrintFormat(
								6, 
								cbWrkMsg.getConnectNozzleNo(), 
								dwMsg.getCust_card_no(), 
								"", 
								cbWrkMsg.getMessage(), 
								"0", 
								GlobalUtility.getDateYYYYMMDDHHMMSS());
					} else  {
						receipt = PumpMessageFormat.createErrorPrintFormat(
											6, 
											0, 
											cbWrkMsg.getConnectNozzleNo(), 
											dwMsg.getCust_card_no(), 
											"", 
											"", 
											"", 
											"", 
											"",
											cbWrkMsg.getMessage(), 
											"", 
											"0", 
											GlobalUtility.getDateYYYYMMDDHHMMSS());
					}
					
					QL_WorkingMessage qlWrkMsg = new QL_WorkingMessage(cbWrkMsg.getNozzleNo(), cbWrkMsg.getConnectNozzleNo(), String.valueOf(receipt.length()), receipt, "0", "", "") ;
					
					preamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey, SyncManager.DISE_PUMP_ADAPTER, qlWrkMsg, "");
					//���� ���� ����
					sendMessage(preamble) ;	
				}
					
			}
//			 �������� ������ ���ĸ� ���ؼ� �����Ѵ�.
			if (isCustInfoSave(protocolType, workMsg))
				PumpMODTSaleManager.setCustInfo(dwMsg.getDeviceID(), workMsg, dwMsg);
			
			if (S9Util.checkControlStatus(dwMsg)){
				SqlSession session = null;
				LogUtility.getPumpMLogger().info("[Pump M] �ش� �ŷ�ó�� ����ŷ�ó�Դϴ�. cust_code = " + dwMsg.getCust_code());
				try {
					session = SqlSessionFactoryManager.openSqlSession();
					
					T_KH_VIOLATIONData data = T_KH_VIOLATIONHandler.getHandler().getT_KH_VIOLATIONDataByCustCode(session, dwMsg.getCust_code());
					
					if (data == null) {
						LogUtility.getPumpMLogger().info("[Pump M] �ش� �ŷ�ó�� ���� ������ �������� �ʽ��ϴ�. cust_code = " + dwMsg.getCust_code() );
						return;
					}

					POSHeader posheader = new POSHeader(IConstant.POSPROTOCOL_SYSTEM_KH, 
							IConstant.POSPROTOCOL_SYSTEM_POS, 
							IConstant.POSPROTOCOL_COMMANDID_PO, 
							IConstant.POSPROTOCOL_TYPE_NOZZLE, 
							workMsg.getNozzleNo(), 
							GlobalUtility.getDateYYYYMMDDHHMMSS(),
							uniqueKey);

					String body =  "" + POSHeader.DELIMITER_STRING + 
									dwMsg.getCust_code() + POSHeader.DELIMITER_STRING + 
									data.getControl_status() +  POSHeader.DELIMITER_STRING +
									"���ϻ��� �޼���" +  POSHeader.DELIMITER_STRING +
									data.getLimit_amt_credit() +  POSHeader.DELIMITER_STRING +
									data.getViolation_amt() ;
					
					byte[] bytePreamble = posheader.mergeHeaderBody(posheader.convertHeaderToPOSContentWithoutDataLength(), body.getBytes());
					LogUtility.getPumpMLogger().debug(new String(bytePreamble));
					Preamble sndPreamble = Preamble.createPreamble(uniqueKey, SyncManager.DISE_PUMP_MODULE, SyncManager.DISE_POS_ADAPTER, bytePreamble, "");

					sendMessage(sndPreamble);
					posheader = null;
				} catch (Exception e) {
					LogUtility.getPumpMLogger().error(e.getMessage(),e);
				} finally {
					SqlSessionFactoryManager.closeSqlSession(session);
				} 
			}
		} else {
			LogUtility.getPumpMLogger().warn("[Pump M] timeout �� �Ǿ �̹� ó���Ǿ����ϴ�.") ;
		} 
	}
        
    
    /**
     * POS ���� �� ī�� ���� ��û�� ���� ������ �޾Ƽ� �̸� CAT M ���� �����Ѵ�.
     * 
     * @param uniqueKey	: Unique Key
     * @param dyMsg		: POS �� ���� ���� DY ����
     */
    private void processDY(String uniqueKey, POS_DY dyMsg) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PosAController/" + "processDY()");
		
		Object uPosPreambleObj = PumpMSyncManager.getPreambleAndRemove(uniqueKey) ;

		if (uPosPreambleObj != null) {
			Preamble posPreamble = (Preamble) uPosPreambleObj ;
			UPOSMessage uPosMsg = (UPOSMessage) posPreamble.getPreamble() ;

			String posReceipt_no = null ;

			if (dyMsg != null) {
				posReceipt_no = dyMsg.getKhTransactionID() ;
			}
			
			UPOSMessage returnPOSMsg = null ;
			if (uPosMsg.getMessageType().equals(IUPOSConstant.MESSAGETYPE_4201)) {
				returnPOSMsg = UPOSUtil.process4201_4291(uPosMsg, dyMsg, false) ;
			} else {
				returnPOSMsg = UPOSUtil.process4201_4291(uPosMsg, dyMsg, true) ;
			}
			
			returnPOSMsg.setPosReceipt_no(posReceipt_no) ;
			
			//����ȭ�� �ŷ�ó�� ��� �ŷ�óī��, ���ʽ�ī��, �ſ�ī��� ���� �� ���ʽ� ���� ���� �Ǵ�. edited by  ykjang
			if ("05".equals(dyMsg.getCust_cd_item()))
				returnPOSMsg = CustUtil.isDiscountCargo(uPosMsg, returnPOSMsg, dyMsg.getCust_card_no());

			// �������� �ŷ�ó�� ��� ���� �ŷ�ī��� ������ �� PL������ ������ PL������ ������ ī�������� �ǽ��Ѵ�.
			
			// 2013.10.06 ksm   ��һ�������� ��ְ�.
			// ��ǥ���� ���ΰ��� �Ȱ��� ��� POS���� DY������ ���ο��ΰ� 0���� �Ѿ����.
			// POS���� ������.
			// KixxHUB�� DY ������ ���ο��ΰ� 1�̸� �߰����� �������� ����.
			String unitDiscount_ind = ICode.DY_UNITDISCOUNT_IND_0;
			
			if(dyMsg.getDyInfoArray() != null && dyMsg.getDyInfoArray().length != 0){
				unitDiscount_ind = dyMsg.getDyInfoArray()[0].getUnitDiscount_ind();
			}
			
			if (ICode.CUST_CD_ITEM_26.equals(dyMsg.getCust_cd_item()))
				returnPOSMsg = CustUtil.isOilPriceSupport(uPosMsg, returnPOSMsg, unitDiscount_ind);
			
			Preamble preamble = PumpMUtil.createUPOSMessagePreamble(posPreamble.getKey(), 
					SyncManager.DISE_PUMP_MODULE, 
					SyncManager.DISE_CAT_MODULE, 
					returnPOSMsg, "") ;
			sendMessage(preamble) ;	
			LogUtility.getPumpMLogger().info("[Pump M] �� ī�� ���� ����� CAT M ���� �����մϴ�.") ;
		} else {
			LogUtility.getPumpMLogger().warn("[Pump M] timeout �� �Ǿ �̹� ó���Ǿ����ϴ�.") ;
		}    			
	}
      
    /**
     * ���� ���� ��ȣ ��ȸ ��� 1�� �� ��� Pump A �� �������� �ʰ�, POS �� ����ȸ ��û�� ������.
     * 
     * @param uniqueKey	: Unique Key
     * @param duMsg		: DU ����
     */
    private void processS9WithDU(String uniqueKey, POS_DU duMsg) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PosAController/" + "processS9WithDU()");
		
		LogUtility.getPumpMLogger().info("[Pump M] ������ȸ ��� 1���̱� ������, POS �� �� ��ȸ ��û�� ������. �̴� " +
			"������ S9 ������ ���� �����Ѵ�.") ;
		S9_WorkingMessage fakeS9WorkMsg = S9Util.createS9WorkingMessage(duMsg) ;
		String messageID = fakeS9WorkMsg.getMessageID() ;
		String khproc_no = "" ; // ������� ����.
		Preamble fakePumpPreamble = 
			Preamble.createPreamble(fakeS9WorkMsg.getMessageID() ,
					SyncManager.DISE_PUMP_ADAPTER,
					SyncManager.DISE_PUMP_MODULE,
					fakeS9WorkMsg,
					"") ;
		try {
			POS_DV dvPumpM = 
				S9Util.getPOSPumpM_DV(messageID, fakeS9WorkMsg.getNozzleNo(), khproc_no, fakeS9WorkMsg.getSerialNumber()) ;
			PumpMSyncManager.setSyncData(uniqueKey,
					messageID, SyncManager.DISE_PUMP_MODULE, fakePumpPreamble) ;
				
			Preamble preamble = PumpMUtil.createPOSMessagePreamble(uniqueKey, SyncManager.DISE_POS_ADAPTER, dvPumpM, "") ;
			sendMessage(preamble) ;
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
    } 
    /**
     * 2019.07.29 SoonKwan
     * POS�� ���� ODT Update ����� �޾Ƽ� �̸� Pump A ���� �����Ѵ�.
     * @param uniqueKey
     * @param cxMsg
     */
    private void processCX(String uniqueKey,POS_CX cxMsg) {
    	
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PosAController/" + "processCX()");
		
		Preamble pumpPreamble = null ;
		ArrayList<String> selfODTList = null ;
		
		try{
			if(!cxMsg.getDeviceID().equals("00")) {
				PU_WorkingMessage puWorkMsg = PumpMUtil.createPU_WorkMsg(cxMsg) ;
				
				pumpPreamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey,
						SyncManager.DISE_PUMP_ADAPTER, puWorkMsg, "") ;
				sendMessage(pumpPreamble) ;
				return;
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
			return ;
		}
		
		try {
			selfODTList = T_NZ_NOZZLEHandler.getHandler().getSelfODTNoList();
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
			return ;
		}
		
		try {
			if (selfODTList != null) {    						
		        for (int i = 0 ; i < selfODTList.size() ; i++) {
		    		PU_WorkingMessage puWorkMsg = PumpMUtil.createPU_WorkMsg(cxMsg) ;
		        	puWorkMsg.setNozzleNo(selfODTList.get(i)) ;
					pumpPreamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey,
							SyncManager.DISE_PUMP_ADAPTER, puWorkMsg, "") ;
					sendMessage(pumpPreamble) ;
		        }
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error("Error 03 "+e.getMessage(),e);
			return ;
		}
    }
    
    /**
     * 
     * POS �� ����� ���� �ʱ�ȭ�� �ʱ�ȭ�� ���õ� Table �� ��� ���� �� ���� ������ �޴´�.
     * 		Table
     * 			�Ϲ�ȯ�� ���� 
     * 			������ ȯ��
     * 			���� Parameter
     */
    private void removeInitializeTable() {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PosAController/" + "removeInitializeTable()");
		
		LogUtility.getPumpMLogger().info("[Pump M] Remove All Pump Table.") ;
		SqlSession session = null;
		try {
			session = SqlSessionFactoryManager.openSqlSession();
   		
			T_NZ_INFOHandler.getHandler().removeALLT_NZ_INFOData(session) ;
			T_NZ_NOZZLEHandler.getHandler().removeALLT_NZ_NOZZLEData(session) ;
			T_NZ_PARAMETERHandler.getHandler().removeALLT_NZ_PARAMETERData(session) ;
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}     	
    }
    
    
    /**
	 * LockingManager �� unLocking �Լ��� �� �Լ��� ȣ���Ͽ���, POS ���� unLocking �� ��û�Ѵ�.
	 */
	@Override
	public boolean requestReleaseLocking(String nozzle_no, String khproc_no) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PosAController/" + "requestReleaseLocking()");
		
    	if ((khproc_no == null) || ("".equals(khproc_no))) {
    		return false ;
    	}
		POS_KE kePumpm = new POS_KE(GlobalUtility.getUniqueMessageID(), nozzle_no, khproc_no) ;		
		Preamble preambleData = PumpMUtil.createPOSMessagePreamble(null, SyncManager.DISE_POS_ADAPTER, kePumpm, "") ;
    	LogUtility.getPumpMLogger().info("[Pump M] Request Locking Release to POS.") ;
		sendMessage(preambleData) ;
		return true;
	}

    /**
	 * LockingManager �� unLocking �Լ��� �� �Լ��� ȣ���Ͽ���, POS ���� ���� ������ �˸���.
	 */
	@Override
	public boolean sendLockingMsgToPOS(String nozzle_no, String khproc_no) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PosAController/" + "sendLockingMsgToPOS()");
		
		LogUtility.getPumpMLogger().info("[Pump M] Send Locking to POS.nozID=" +nozzle_no + 
				"#khproc_no=" +khproc_no + "#isLock="+IConstant.PUMP_SALE_LOCKING) ;
    	if ((khproc_no == null) || ("".equals(khproc_no))) {
    		return false ;
    	}    	
		POS_KD kdPumpM = new POS_KD(GlobalUtility.getUniqueMessageID(), IConstant.POSPROTOCOL_TYPE_NOZZLE, 
				nozzle_no, khproc_no, IConstant.PUMP_SALE_LOCKING) ;
		Preamble posPreamble = 
			PumpMUtil.createPOSMessagePreamble(null,SyncManager.DISE_POS_ADAPTER,kdPumpM ,"") ;
		sendMessage(posPreamble) ;
		return true;
	}
    
    /**
     * 
     * POS A ���κ��� ��û Ȥ�� ���� ���� �����͸� �ٸ� ��⿡�� ���۽� ���.
     * 		- POS Adapter
     * 			Command ID List							from where
     * 				- POS Protocol
     * 					DA	: �Ϲ� ȯ�� ���� ��û			: PosAController
     * 					DC	: ������ ȯ�� ���� ��û		: PosAController
     * 					DE	: ���� �� ���� ����			: PumpAController
     * 					DG	: ���� �Ϸ� ���� ����			: PumpAController
     * 					DM	: ������ �߱� ��ȸ ��û		: PumpAController
     * 					DP	: Preset �ڷ� ��û ����		: PumpAController
     * 					DQ	: Preset ���� ���� 			: PumpAController
     * 					DS	: Locking/unLocking ����	: PumpAController
     * 					CY  : selfODT Version���� ����  : PumpAController
     * 
     * 		- PumpAController
     * 			Command ID List
     * 				- PumpA Protocol
     * 					DI	: ���� ���� ��� ���� 			: PosAController 
     * 					DO	: Preset �ڷ� ��û			: PosAController
     *					DK	: ���� / ���� ���� ��û		: PosAController
     *					DN	: ������ ��û ����			: PosAController
     *					DR	: Locking/unLocking ��û	: PosAController
     *					   
     *
     * @param preambleData	: ������ ������
     */
    private void sendMessage(Preamble preambleData) {
//		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PosAController/" + "sendMessage()");
		
    	int dest = preambleData.getDest() ;
    	switch (dest) {
	    	case SyncManager.DISE_POS_ADAPTER : {
	    		Object sendingObj = preambleData.getPreamble() ;
	    		if (sendingObj instanceof POSHeader) {
	    			String receivingCommandID = ((POSHeader)sendingObj).getCommandID() ;
	    			switch (receivingCommandID) {
	    				case IConstant.POSPROTOCOL_COMMANDID_DE :
	    				case IConstant.POSPROTOCOL_COMMANDID_DG :
	    				case IConstant.POSPROTOCOL_COMMANDID_DL :
	    				case IConstant.POSPROTOCOL_COMMANDID_AE :
	    				case IConstant.POSPROTOCOL_COMMANDID_DP :
	    				case IConstant.POSPROTOCOL_COMMANDID_DQ :
	    				case IConstant.POSPROTOCOL_COMMANDID_KD : {
	    					// POS �� ���۵Ǵ� ������ ���۵��� ���� ��� Pump M ���� �ݼ۵��� ���θ� ����.
	    					preambleData.setKey("") ;
	    					break ;
	    				}
	    			}
	    		}
	    		getProducer_PosA_Data().produce(preambleData) ;
	    		break ; 
	    	}
	    	case SyncManager.DISE_PUMP_ADAPTER :
	    		getProducer_PumpMPumpA_Data().produce(preambleData) ;
	    		break ; 
	    	case SyncManager.DISE_CMS_MODULE :
	    	case SyncManager.DISE_CAT_MODULE :
	    		getProducer_PumpMCatM_Data().produce(preambleData) ;
	    		break ;
	    	case SyncManager.DISE_STATE_MODULE :
	    	case SyncManager.DISE_SALE_MODULE :
	    		break ; 
    	}
    }
    
    /**
     * KH �����߿� POS �� ���� ���� Time parameter ������ Pump A �� �����Ѵ�.
     * 
     * @param khContent	: POS �� ���� ���� Time Parameter ����
     */
	private void sendP7WorkingMessageToPumpA(POS_KH khContent) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PosAController/" + "sendP7WorkingMessageToPumpA()");
		
		try {			
			if ((khContent == null) || (khContent.getDup() ==0)) return ;
			
			ArrayList<P7_WorkingMessage> p7WorkMsgList = new ArrayList<P7_WorkingMessage>() ;
			POS_KH_NozzleInfo[] khNozzleInfoArray = khContent.getNozzleInfoArray() ;
			
			String store_code = T_KH_STOREHandler.getHandler().getStoreCode();
			for (int i = 0 ; i < khNozzleInfoArray.length ; i++) {
				P7_WorkingMessage[] p7WorkMsg = PumpMUtil.getP7WorkingMessageArray(store_code, khNozzleInfoArray[i].getNozzle_no()) ;
				
				if ((p7WorkMsg != null) && (p7WorkMsg.length == 1)){
					p7WorkMsgList.add(p7WorkMsg[0]) ;
				}
			}
			
			if (p7WorkMsgList.size() > 0) {
				for (int i = 0 ; i < p7WorkMsgList.size() ; i++) {
					Preamble preamble = PumpMUtil.createWorkingMessagePreamble(SyncManager.getUniqueKey(),
		    				SyncManager.DISE_PUMP_ADAPTER, p7WorkMsgList.get(i) , "") ;
					sendMessage(preamble) ;
				}
			}			
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
	}

	/**
     * PumpAController ���� �ʱ�ȭ ��û�� �Ѵ�.
     * 
     * @param msg	: START String
     */
    private void sendStartMessage(String msg) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PosAController/" + "sendStartMessage()");
		
		getProducer_PumpMPumpA_InitReq().produce(msg);  
    }

	/**
	 * LockingManager �� locking �Լ��� �� �Լ��� ȣ���Ͽ���, POS ���� locking ���� �˸���.
	 */
	@Override
	public boolean sendUnlockingMsgToPOS(String nozzle_no, String khproc_no) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PosAController/" + "sendUnlockingMsgToPOS()");
		
		LogUtility.getPumpMLogger().info("[Pump M] Send Unlocking to POS.nozID=" +nozzle_no +
				"#khproc_no=" +khproc_no + "#isLock="+IConstant.PUMP_SALE_UNLOCKING) ;
    	if ((khproc_no == null) || ("".equals(khproc_no))) {
    		return false ;
    	}
		POS_KD kdPumpM = new POS_KD(GlobalUtility.getUniqueMessageID(), IConstant.POSPROTOCOL_TYPE_NOZZLE, 
				nozzle_no, khproc_no, IConstant.PUMP_SALE_UNLOCKING) ;
		Preamble posPreamble = 
			PumpMUtil.createPOSMessagePreamble(null,SyncManager.DISE_POS_ADAPTER,kdPumpM ,"") ;
		sendMessage(posPreamble) ;
		return true;
	}

	@Override
	public void start() {
		registerListener() ;
        
        gStep = IPumpConstant.STEP_PUMP_RESOLVED ;
        isPOSStarted = false ;
        
        LockingManager.setLockingMgrListener(this) ;
    }

	@Override
	public void stop() {
        
        gStep = IPumpConstant.STEP_PUMP_RESOLVED ;
        isPOSStarted = false ;
        
        LockingManager.removeLockingMgrListener() ;        
    }
	
	/**
	 * 
	 * ��߿� ������ �Ϲ� ȯ�� ���� ������ POS �� ���� �޾Ƽ� update �Ѵ�.
	 * 
	 *  added by yhcheon at 2009.05.14
     * 		DB	: �Ϲ� ȯ�� ���� ����
     * 
     * @param receiving_posa	: POS �� ���� ���޹��� �ʱ�ȭ ����
     * 
     */
    private void update_T_NZ_INFO(Object receiving_posa) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PosAController/" + "update_T_NZ_INFO()");
		    	
        Preamble receivingData = (Preamble) receiving_posa ;
        String uniqueKey = receivingData.getKey() ;
        
        byte[] receivingContents = (byte[])receivingData.getPreamble() ;
    	POSHeader posHeader = POSHeader.createHeader(receivingContents) ; 
    	String receivingCommandID = posHeader.getCommandID() ;
  	
		SqlSession session = null;
		boolean rlt = true ;
        
		if ( receivingCommandID == IConstant.POSPROTOCOL_COMMANDID_DB) {
			
			try {
				session = SqlSessionFactoryManager.openSqlSession();

				POS_DB dbContent = new POS_DB(receivingContents) ;

    			LogUtility.getPumpMLogger().info(dbContent.toString());
	    		
				rlt = T_NZ_INFOHandler.getHandler().removeALLT_NZ_INFOData(session) ;
				if (rlt == false) {
	    			LogUtility.getPumpMLogger().error("[Pump M] Fail to remove all 'T_NZ_INFO' TABLE");				
				}
				
	    		T_NZ_INFOData tNzInfoData = PumpMUtil.createT_NZ_INFOData(dbContent) ;
	    		
	    		rlt = T_NZ_INFOHandler.getHandler().insertT_NZ_INFOData(session, tNzInfoData) ;
	    		
	    		if (rlt == true) {
	    			LogUtility.getPumpMLogger().debug("[Pump M] T_NZ_INFO table insert");
	    		}
	    		else {
	    			LogUtility.getPumpMLogger().error("[Pump M] T_NZ_INFO table insert fail");
	    		}
	    		
		    	POS_DC dcContent = new POS_DC(GlobalUtility.getUniqueMessageID()) ;
		    	Preamble dcPreamble = PumpMUtil.createPOSMessagePreamble(uniqueKey, SyncManager.DISE_POS_ADAPTER, dcContent, "") ;
		    	sendMessage(dcPreamble) ;
		    	
		    	session.commit();
			}
			catch (Exception e) {
				session.rollback();
				LogUtility.getPumpMLogger().error(e.getMessage(),e);
			} finally {
				SqlSessionFactoryManager.closeSqlSession(session);
			} 			
		}
    }
	
}
