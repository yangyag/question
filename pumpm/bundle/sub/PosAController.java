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
     * KixxHub 와 POS 의 연결상태 요청에 대한 응답을 한다.
     * 사용하지 않음.
     * 
     * @return
     * 		boolean	
     * 			true : 연결됨
     * 			flase : 연결이 안됨
     */
    public static boolean isPOSStarted() {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PosAController/" + "isPOSStarted()");
		
    	LogUtility.getPumpMLogger().info("[Pump M] isPOSConnected=" + isPOSStarted) ;
    	return isPOSStarted ;
    }

	private int gStep = IPumpConstant.STEP_PUMP_RESOLVED ;
 
	/**
     * POS A 로 부터 Timeout 이 발생된 전문에 대한 처리를 진행한다.
     *   	- 보관증 발급 조회 요청 / 응답 (DM/DN)		- 충전소
  	 *		- 고객 차량 조회 요청/응답 (DT/DU)			- 충전소
  	 *		- 고객 카드 조회 요청/응답 (DV/DW)			- 소모셀프 / 충전소
  	 *		- 고객 카드 수행 요청/응답 (DX/DY)			- CAT 단말기
     * @param receivingData	: timeout 이 발생된 데이터
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
				// 고객 카드 수행 요청/응답 (DX/DY)			- CAT 단말기
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
							// 거래처 자체 수행
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
							//영업화물 or 유가보조 거래  거래처 자체 수행
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
							// 거래처 차량 조회
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
		    			// 다쓰노 셀프 외상 거래 승인 요청
			    	  	// 고객 카드 조회 요청/응답 (DV/DW)			- 다쓰노셀프		    			
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
							//승인 실패인 경우 ODT에 실패 영수증을 보내준다.
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
							//실패 전문 전송
							sendMessage(preamble) ;	
						}
						PumpMODTSaleManager.setCustInfo(dwPumpM.getDeviceID(), cbWorkMsg, dwPumpM) ;
			    		break ;
		    		}
		    		// 신형 ODT의 거래처 정보 응답 전문 처리 - PI2, CWI, 2016-03-23
		    		case IPumpConstant.COMMANDID_GA : {
		    			
		    			// GSC Self 거래처 정보
			    	  	// 고객 카드 조회 요청/응답 (DV/DW)		    			
	    				LogUtility.getPumpMLogger().info("[Pump M] - TimeOut - GSC Self Customer Processing.") ;
	    				//POSPumpM_DV dvPosMsg = (POSPumpM_DV) sendingObj ;
	    				POS_DV dvPosMsg = new POS_DV((byte[]) sendingObj) ;
	    				POS_DW dwPumpM = S9Util.processPOSPumpM_DV(dvPosMsg) ;

						// 단가 할인이 되지 않을 시 0으로 셋팅되는 문제가 있어 GB에 셋팅된 BasePrice로 거래처 단가를 변경한다.
						GB_WorkingMessage gbWorkMsg = S9Util.convertGBWorkMsgFromPOSDWMsg(dwPumpM) ;
						dwPumpM.setBasePrice(gbWorkMsg.getBasePrice());
						
	    				Preamble preamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey, SyncManager.DISE_PUMP_ADAPTER, gbWorkMsg, "") ;
	    				LogUtility.getPumpMLogger().info("[Pump M] Send customer processing result to Pump Adapter.") ;
						sendMessage(preamble) ;
						
						PumpMODTSaleManager.setCustInfo(dwPumpM.getDeviceID(), gbWorkMsg, dwPumpM) ;
			    		break ;
		    		}
		    		case IPumpConstant.COMMANDID_HB : {
		    			// 소모 셀프 외상 거래 승인 요청
			    	  	// 고객 카드 조회 요청/응답 (DV/DW)			- 소모셀프		    			
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
			    		// 충전기 고객 카드 승인 요청 전문			    	
			    		S9_WorkingMessage s9WorkMsg = (S9_WorkingMessage) msg ;
			    		int s9Mode = Integer.parseInt(s9WorkMsg.getMode()) ;
			    		switch (s9Mode) {
							case 0 : {
					    	  	// 고객 카드 조회 요청/응답 (DV/DW)			- 충전소
								// 1. DV 전문과 자체 정보를 이용하여 DW 전문을 구성한다.
								// 2. DW 전문을 이용하여 Pump A 로 보내기 위한 PG 전문을 구성한다.
								// 3. Pump A 로 PG 전문을 전송한다.
								// 4. DW 전문과 PG 전문을 향후 사용하기 위해서 저장한다.
			    				LogUtility.getPumpMLogger().info("[Pump M] 자체(kixxHub) 정보로 고객 카드 정보를 조사합니다.") ;
			    				
			    				POS_DV dvPosMsg = new POS_DV((byte[]) sendingObj) ;
			    				// 1. DV 전문과 자체 정보를 이용하여 DW 전문을 구성한다.
								POS_DW dwPumpM = S9Util.processPOSPumpM_DV(dvPosMsg) ;
								// 2. DW 전문을 이용하여 Pump A 로 보내기 위한 PG 전문을 구성한다.
			    				PG_WorkingMessage pgWorkMsg = S9Util.convertPGWorkMsgFromPOSDWMsg(dwPumpM) ;
			    				Preamble preamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey, SyncManager.DISE_PUMP_ADAPTER, pgWorkMsg, "") ;
			    				LogUtility.getPumpMLogger().info("[Pump M] 자체(kixxHub) 정보로 고객 카드 정보를 충전기에 전송합니다.") ;
			    				// 3. Pump A 로 PG 전문을 전송한다.
		    					sendMessage(preamble) ;
		    					// 4. DW 전문과 PG 전문을 향후 사용하기 위해서 저장한다.
		    					PumpMODTSaleManager.setCustInfo(dwPumpM.getDeviceID(), pgWorkMsg, dwPumpM) ;
								break ;
							}
							case 1 : {
					    		// 고객 차량 조회 요청/응답 (DT/DU)			- 충전소
								// 1. DT 전문과 자체 정보를 이용하여 DU 전문을 구성한다.
								// 2. DU 전문의 내용을 분석해 본 결과 차량 갯수가
								//		1) 1개 일 경우, DV 전문을 만들어서 POS 로 다시 요청한다.
								//		2) 1개가 아닐 경우, WorkingMessage (NAK or PF 전문) 를 구성하여 Pump A 로 전송한다.
			    				LogUtility.getPumpMLogger().info("[Pump M] 자체(kixxHub) 정보로 차량정보를 조사합니다.") ;
//			    				POSPumpM_DT dtumpM = (POSPumpM_DT) sendingObj ;	
			    				POS_DT dtumpM = new POS_DT((byte[]) sendingObj) ;
			    				// 1. DT 전문과 자체 정보를 이용하여 DU 전문을 구성한다.
			    				POS_DU duPosPumpM = CustUtil.processCustomerCar(dtumpM.getMessageID(),
			    						dtumpM.getDeviceID(), dtumpM.getCar_short_no()) ;
			    				
								// 2. DU 전문의 내용을 분석해 본 결과 차량 갯수가
								//		1) 1개 일 경우, DV 전문을 만들어서 POS 로 다시 요청한다.
								//		2) 1개가 아닐 경우, WorkingMessage (NAK or PF 전문) 를 구성하여 Pump A 로 전송한다.
			    				if (duPosPumpM.getDup() == 1) {
			    					processS9WithDU(uniqueKey, duPosPumpM) ;
			    				} else {
			    					WorkingMessage workMsg = PumpMUtil.processPOSPumpM_DUForPumpA(duPosPumpM) ;
			    					Preamble preamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey, SyncManager.DISE_PUMP_ADAPTER, workMsg, "") ;
			    					LogUtility.getPumpMLogger().info("[Pump M] 자체(kixxHub) 정보로 차량정보를 전송합니다.") ;
			    					sendMessage(preamble) ;
			    				}

								break ;
							}
			    		}			    		
			    		break ;
			    	}
			    	case IPumpConstant.COMMANDID_SG : {
			    		// 보관증 발급 조회 요청 / 응답 (DM/DN)		- 충전소
			    		LogUtility.getPumpMLogger().info("[Pump M] 자체(kixxHub) 보관증 발급 조회를 조사합니다.") ;
			    		SG_WorkingMessage sgWorkMsg = (SG_WorkingMessage) msg ;
			    		PQ_WorkingMessage pqWorkMsg = PumpMUtil.createPQ_WorkMsg(sgWorkMsg.getMessageID(), 
			    				sgWorkMsg.getNozzleNo(), null) ;
			    		Preamble preamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey, SyncManager.DISE_PUMP_ADAPTER, pqWorkMsg, "") ;
	    				LogUtility.getPumpMLogger().info("[Pump M] 자체(kixxHub) 보관증 발급 조회 정보를 충전기에 전송합니다. Mode = 1") ;
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
	 * 주유기 초기화 정보를 POS 로 부터 받아서 이를 테이블에 저장한다. 받는 전문은 다음과 같다.
     * 		DB	: 일반 환경 설정 응답
     * 		DD	: 주유기 환경 설정 응답
     * 
     * @param receiving_posa	: POS 로 부터 전달받은 초기화 정보
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
     * Time Parameter 테이블에 POS 로 부터 받은 Time Parameter 정보를 저장한다. 저장시 KH 가 동작할때에는 (running=true)
     * 기존 정보를 삭제 한 이후 저장한다
     * 
     * @param khNozzleInfo	: POS 로 부터 받은 Time Parameter 정보
     * @param running		: KH 동작 여부
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
     * Time Parameter 테이블에 POS 로 부터 받은 Time Parameter 정보를 저장한다. 저장시 KH 가 동작할때에는 (running=true)
     * 기존 정보를 삭제 한 이후 저장한다
     * 
     * @param con			: Connection
     * @param khNozzleInfo	: POS 로 부터 받은 Time Parameter 정보
     * @param running		: KH 동작 여부
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
			// transType 가 미등록카드가 아닐 때
			// transStatus 가 정지가 아닐 때
			// transStatus 가 말소가 아닐 때 만 저장함
			LogUtility.getPumpMLogger().debug("[Pump M] 정상거래처가 아닌 경우 거래처 정보를 저장하지 않는다.");
			isSave = false ;			
		} 		
		return isSave;
	}
 
    /**
	 * POS 와 KixxHub 의 연결 상태를 보고한다.
	 */
	@Override
	public boolean isPOSConnected() {
		return isPOSStarted;
	}
 
    /**
     * POS A 로부터 전문을 수신한다. 받을 수 있는 전문은 다음과 같다.
     *		DB	: 일반 환경 설정 응답		
     * 		DD	: 주유기 환경 설정 응답		
     * 		DI	: 주유 제어 요청			
     * 		DK	: 정액/정량 설정 요청		
     * 		DN	: 보관증 발급조회 응답		
     * 		DO	: Preset 자료 요청		
     * 		DR	: Locking/unLocking 요청
     * 		KJ	: 충전기 충전원 해지 요청
     *      CX  : SelfODT Update/Install 요청 
     * 
     * @param receiving_posa	: POS A 로부터 받은 전문
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
	    			// 실시간 반영을 위하여.
	    			case IConstant.POSPROTOCOL_COMMANDID_DB :

	    				update_T_NZ_INFO(receiving_posa) ;
	    				
	    				// Pump Adapter에게 P1, P2 전문을 보내는 부분..
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
	    				
	    				// 재승인 인정금액 초기화
	    				PumpMUtil.setNull_Sa_min_amt() ;
	    				
	    				break ;
	    			// added by yhcheon at 2009.05.12 end
	    				
	    				
	    			/**
	    			 * 마감 교대 마감 시, POS 로 부터 충전기 충전원 해지 전문을 받으면 Pump A 로 전송한다. 
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
		    		 * 노즐별 Time Parameter 를 수신한다. 
		    		 * 노즐별 Time Parameter 는 초기 환경 정보에서 꼭 받아야 할 정보이며, 또한 KH 동작중에도 받을수 있다.
		    		 * 이 정보를 받으면 Table 에 Update 를 한 이후 Pump A 로 전송한다.
		    		 */
					case IConstant.POSPROTOCOL_COMMANDID_KH : {
						// 노즐별 Time Parameter 
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
					 * POS 로 부터 주유제어 요청 (비상정지/비상해지) 를 수신한다.
					 * 이는 한 노즐에 대해서 요청 받을 수도 있고, 전체 노즐에 대해서 요청을 받을수도 있다.
					 * Device ID 가 0000 인 경우는 전체 노즐로 간주된다.
					 */
	    			case IConstant.POSPROTOCOL_COMMANDID_DI : {
	    				// 주유 제어 요청	
	    		    	LogUtility.getPumpMLogger().info("[Pump M] Receive Pump Control from POS. NozID="+nozID) ;
	        			POS_DI diMsg = new POS_DI(receivingContents) ;
		    			LogUtility.getPumpMLogger().info(diMsg.toString());
	        			processDI(uniqueKey, diMsg) ;
	    		    	LogUtility.getPumpMLogger().info("[Pump M] Completed Pump Control. NozID="+nozID) ;
	    				break ;
	    			}
	    			/**
	    			 * POS 로 부터 Preset 요청을 수신한다.
	    			 * 수신 이후 Pump A 로 주유기 Preset 을 요청하고, POS 로는 KH 처리번호를 생성하여 전송한다.
	    			 */
	    			case IConstant.POSPROTOCOL_COMMANDID_DK : {
	    				// 정액/정량 설정 요청	
	    		    	LogUtility.getPumpMLogger().info("[Pump M] Receive Fixed Price/Liter Pump from POS. NozID="+nozID) ;
	        			POS_DK dkMsg = new POS_DK(receivingContents) ;
	    				// Debug
		    			LogUtility.getPumpMLogger().info(dkMsg.toString());
	
	        			PB_WorkingMessage pbWorkMsg = PumpMUtil.createPB_WorkMsg(dkMsg) ;
	        			
	        	    	String khproc_no = 
	        				PumpMTransactionManager.getInstance().getKHTransactionID(pbWorkMsg.getNozzleNo(), IPumpConstant.KH_PUMP_PRESET) ;
	        	    	PumpMTransactionManager.getInstance().setPresetInfo(pbWorkMsg.getNozzleNo(), IPumpConstant.PRESET_FROM_POS) ;
	        	    	
	        	    	// 2012.07.20 ksm  PB_WorkingMessage에 barcode 필드 설정.
	        	    	// twsongkis 2015-01-28 새로운 바코드 로직으로 barcode 셋팅
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
	    				
	    				// KH 처리번호를 POS 에 전송한다.
	    				POS_DL dlPumpM = new POS_DL(dkMsg.getMessageID(), dkMsg.getDeviceType(), dkMsg.getDeviceID(), khproc_no) ;
	    				pumpPreamble = PumpMUtil.createPOSMessagePreamble(null,SyncManager.DISE_POS_ADAPTER, dlPumpM, "") ;
	       		    	LogUtility.getPumpMLogger().info("[Pump M] Respond Fixed Price/Liter to POS. NozID="+nozID) ;
	    				sendMessage(pumpPreamble) ;        		
	    				
	    				break ;
	    			}
	    			/**
	    			 * POS 로 부터 현 주유기에 설정된 Preset 정보 요청을 수신하여 전송한다. 사용되지 않는 전문이다.
	    			 */
	    			case IConstant.POSPROTOCOL_COMMANDID_DO : {
	    				// Preset 자료 요청	    				
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
	    			 * 현 주유건의 중복 결재 방지를 위해서 Locking 혹은 unLocking 을 수신한다.
	    			 * 만약 Locking 혹은 unLocking 이 이미 설정된 경우에는 응답 전문에 성공여부를 전송한다.
	    			 */
	    			case IConstant.POSPROTOCOL_COMMANDID_DR : {
	    				// Locking/unLocking 요청	    				
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
	    			 * Pump M 에서는 locking 이 설정된 주유건의 경우 만약 1분 내로 unLocking 이 수신되지 않는다면
	    			 * 강제로 locking 을 해지한다. 하지만 만약 POS 에 의한 locking 주유건은 강제로 해지는 하지 않고 
	    			 * POS 로 locking 해지 할지 요청한다. 그 이후 응답 전문이 KF 전문이다.
	    			 */
	    			case IConstant.POSPROTOCOL_COMMANDID_KF : {
	    				// Locking 해지요청에 대한 응답
	    				LogUtility.getPumpMLogger().info("[Pump M] Receive Locking Release from POS. NozID="+nozID) ;
	    				POS_KF kfMsg = new POS_KF(receivingContents) ;
	    				
	    				LogUtility.getPumpMLogger().info(kfMsg.toString());
	    				if (kfMsg.getIsOK().equals("0")) {
	    					LockingManager.unlocking(kfMsg.getDeviceID(), kfMsg.getKhTransactionNum(), true) ;  
	    				}
	    				break ;
	    			}
	    			/**
	    			 * 고객 단축번호에 의한 정보 요청을 POS 로 전송하고 그에 대한 응답을 DU 전문으로 수신한다.
	    			 * 차량 조회 요청은 CAT 단말기, 소모셀프 그리고 충전기로 부터 발생할 수 있다.
	    			 */
	    			case IConstant.POSPROTOCOL_COMMANDID_DU : {
	    				// 고객 차량 조회 응답
	    				LogUtility.getPumpMLogger().info("[Pump M] Receive DU from POS. NozID="+nozID) ;
	    				POS_DU duMsg = new POS_DU(receivingContents) ;
	    				// Debug
	    				LogUtility.getPumpMLogger().info(duMsg.toString());
	    				proceedDU(uniqueKey, duMsg) ;
	    							
	    				break ;
	    			}
	    			/**
	    			 * 고객 카드 조회 요청에 대한 응답을 수신한다.
	    			 * 이는 충전기 및 소모셀프 로 부터 요청이 올 수 있으며, 이에 대한 응답을 전송한다.
	    			 * 다쓰노 셀프 추가 edited by ykjang
	    			 */
	    			case IConstant.POSPROTOCOL_COMMANDID_DW : {
	    				// 고객 카드 조회 응답
	    				LogUtility.getPumpMLogger().info("[Pump M] Receive DW from POS. NozID="+nozID) ;
	    				POS_DW dwMsg = new POS_DW(receivingContents) ;
	    				// Debug
	    				LogUtility.getPumpMLogger().info(dwMsg.toString());
	    				
	    				processDW(uniqueKey, dwMsg) ;
	    				break ;
	    			}
	    			/**
	    			 * 고객 카드 수행 요청에 대한 응답을 수신한다.
	    			 * 이는 CAT 단말기로부터 요청에 대한 응답이다.
	    			 */
	    			case IConstant.POSPROTOCOL_COMMANDID_DY : {
	    				// 고객 카드 수행  응답
	    				LogUtility.getPumpMLogger().info("[Pump M] Receive DY from POS. NozID="+nozID) ;	
	    				POS_DY dyMsg = new POS_DY(receivingContents) ;
	    				// Debug
	    				LogUtility.getPumpMLogger().info(dyMsg.toString());
	    				processDY(uniqueKey, dyMsg) ;
	    				
	    				break ;
	    			}
	    			/**
	    			 * 사용하지 않는 전문이다.
	    			 */
	    			case IConstant.POSPROTOCOL_COMMANDID_DN : {
	    				// 보관증 발급조회 응답	
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
	    			 * 가득주유여부에 대한 응답을 수신한다 - 2010.02.13 최순구
	    			 * 
	    			 */
		    		case IConstant.POSPROTOCOL_COMMANDID_PB : {
		    			LogUtility.getPumpMLogger().info("[Pump M] 가득주유여부에 대한 응답을 수신한다 Receive PB from POS. NozID="+nozID) ;
		    			
		    			storeCode = T_KH_STOREHandler.getHandler().getStoreCode();
		    			LogUtility.getPumpMLogger().info("[Pump M] Receive PB from POS. storeCode="+storeCode) ;
		    			
		    			WorkingMessage p5workMsg = PumpMUtil.getP5WorkingMessageForFullPumping(storeCode, null) ;
		    			
		    			if (p5workMsg != null) {
		    				p5workMsg.setMessageID(GlobalUtility.getUniqueMessageID()) ;
		    				Preamble p5Preamble = PumpMUtil.createWorkingMessagePreamble(SyncManager.getUniqueKey(),
		    	    				SyncManager.DISE_PUMP_ADAPTER, p5workMsg , "") ;
		    				sendMessage(p5Preamble) ;
		    				
		    				// P5 전문을 셀프 ODT 에 전송
		    				LogUtility.getPumpMLogger().info("[Pump M] Send P5 to Self ODT") ;
		    			}
		    			
		    			break ;
		    		}
		    		
		    		/**
	    			 * Beacon 
	    			 * POS로부터 수신된 BEACON 결제 관련전문은 장애대응으로 전송 함. 
	    			 * 
	    			 */
		    		case IConstant.POSPROTOCOL_COMMANDID_HE : {
		    			/*LogUtility.getLogger().info("[Pump M] 비콘결제전문 관련 요청을 수신 Receive HE from POS.") ;
		    			*/

		    			//PropertyID.P_HEADER_LENGTH -> POSHeader.startBodyOffset 으로 변경
		    			//pumpM에서 com.gsc.kixxhub.device.module.cat.CatModuleProc 접근 불가
		    			
		    			int uposLength = receivingContents.length - POSHeader.startBodyOffset;
						if (uposLength > 90) {
							byte[] 	uposBytes = new byte[uposLength];
						    System.arraycopy(receivingContents, POSHeader.startBodyOffset, uposBytes, 0, uposLength); 	// UPOS 전문 부분
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
     * POS A 로 부터 주유기 초기화 요청을 받는다.
     * 
     * @param input_posa_start	: 다음 두개의 String 중 하나를 받는다.
     * 		WITHPOS		: POS 와 연결이 되었기 때문에, POS 로 부터 초기화 테이블 정보를 받은 후 주유기/ODT 초기화를 한다.
     * 		WITHOUTPOS	: POS 와 연결이 안되기 때문에, KixxHub 내 차제 테이블을 이용하여 주유기/ODT 초기화를 한다.
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
     * POS A 로 부터, KixxHub 와 POS 의 연결상태 정보를 받는다
     * 
     * @param input_posa_isPosStarted	: true or false
     * 
     */
    @Override
	protected void onReceivingPosAIsPosConnected(boolean input_posa_isPosStarted) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PosAController/" + "onReceivingPosA_POSStarted()");
		
    	LogUtility.getPumpMLogger().info("[Pump M] POS 연결상태 수신=" + input_posa_isPosStarted) ;
        isPOSStarted = input_posa_isPosStarted ;
    }
    
    
    
    
    /**
     * 다른 Controller 로 부터 받은 전문을 POS A 로 전송한다. 받을수 있는 전문은 다음과 같다.
     * 		DE	: 주유 중 정보 전송			: PumpAController
     * 		DG	: 주유 완료 정보 전송			: PumpAController
     * 		DM	: 보관증 발급 조회 요청		: PumpAController
     * 		DP	: Preset 자료 요청 응답		: PumpAController
     * 		DQ	: Preset 정보 전송 			: PumpAController
     * 		DS	: Locking/unLocking 응답	: PumpAController
     *      CY  : selfODT Version 정보     : PumpAController
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
     * POS A 에게 주유기 전체 초기화가 완료되었음을 전송한다.
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
     * POS 로 부터 차량 단축 번호 요청에 대한 응답을 받아서 이를 CAT M or Pump A 에게 전송하도록 한다.
     * 
     * @param uniqueKey	: Unique Key
     * @param duMsg		: POS 로 부터 받은 DU 전문
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
				// CAT M 으로 부터 요청일 경우
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
				LogUtility.getPumpMLogger().info("[Pump M] 고객 차량 정보 응답을 CAT M 에게 전송합니다." ) ;
			} else if (obj instanceof WorkingMessage) {
				// Pump A 로부터의 요청일 경우				
				
				// 충전기의 경우 단축번호로 조회된 차량이 하나인 경우 다시 POS 로 고객 카드 조회 요청을 전송한다.
				if (duMsg.getDup() == 1) {
					processS9WithDU(uniqueKey, duMsg) ;
				} else {
					WorkingMessage workMsg = PumpMUtil.processPOSPumpM_DUForPumpA(duMsg) ;
					Preamble preamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey,SyncManager.DISE_PUMP_ADAPTER, workMsg, "") ; 
					sendMessage(preamble) ;	
					LogUtility.getPumpMLogger().info("[Pump M] 고객 차량 정보 응답을 Pump A 에게 전송합니다." ) ;
				}
				
			} else {
				LogUtility.getPumpMLogger().warn("[Pump M] POS 로 부터 DU 전문을 수신하였으나 송신한 전문을 찾을수 없습니다.") ;
			}
		} else {
			LogUtility.getPumpMLogger().warn("[Pump M] timeout 이 되어서 이미 처리되었습니다.") ;
		}    	
    }
    

    /**
     * POS 로 부터 주유제어 명령을 받아서 이를 Pump A 에게 전송한다.
     * 주유제어 명령은 다음과 같이 구분된다.
     * 	Command
     * 		0 : 주유(충전) 금지
     * 		1 : 주유(충전) 금지 해제
     * 		2 : 전체 주유(충전) 금지
     * 		3 : 전체 주유(충전) 금지 해제
     * 
     * @param uniqueKey	: Unique Key
     * @param diMsg		: 주유제어 명렁 전문 (DI 전문)
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
     * POS 에게 고객 카드 조회 요청에 대한 응답을 받아서 Pump A 에게 전송한다.
     * 
     * @param uniqueKey	: Preamble Object 의 Unique Key
     * @param dwMsg		: POS 로 부터 받은 DW 전문 
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
					LogUtility.getPumpMLogger().info("[Pump M] 소모셀프 고객 응답 전문입니다.") ;
					workMsg = S9Util.convertHDWorkMsgFromPOSDWMsg(dwMsg) ;
					preamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey, 
							SyncManager.DISE_PUMP_ADAPTER, workMsg, "") ;
					break ;
				}
				case IPumpConstant.PUMP_PROTOCOL_NewRecharge :   //2016.03.28 양일준 
				case IPumpConstant.PUMP_PROTOCOL_Recharge : {
					LogUtility.getPumpMLogger().info("[Pump M] 충전기 고객 응답 전문입니다.") ;
					workMsg = S9Util.convertPGWorkMsgFromPOSDWMsg(dwMsg) ;
					preamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey, 
							SyncManager.DISE_PUMP_ADAPTER, workMsg, "") ;
					break ;
				}
				case IPumpConstant.PUMP_PROTOCOL_DaSNo : 
				case IPumpConstant.PUMP_PROTOCOL_NewDaSNo :{	//2012.09.26 ksm
					LogUtility.getPumpMLogger().info("[Pump M] 다쓰노셀프 고객 응답 전문입니다.") ;
					workMsg = S9Util.convertCBWorkMsgFromPOSDWMsg(dwMsg) ;
					preamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey, 
							SyncManager.DISE_PUMP_ADAPTER, workMsg, "") ;
					break ;
				}
				// PI2, 신규 ODT 추가에 따른 프로세스 추가, 2016.01.15 - CWI
				case IPumpConstant.PUMP_PROTOCOL_GSC_SELF :{ 
					LogUtility.getPumpMLogger().info("[Pump M] GSCSelf 고객 응답 전문입니다.") ;
					workMsg = S9Util.convertGBWorkMsgFromPOSDWMsg(dwMsg);
					
					// 단가 할인이 되지 않을 시 0으로 셋팅되는 문제가 있어 GB에 셋팅된 BasePrice로 거래처 단가를 변경한다.
					GB_WorkingMessage gbwkm = (GB_WorkingMessage) workMsg;
					dwMsg.setBasePrice(gbwkm.getBasePrice());
					
					preamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey, 
							SyncManager.DISE_PUMP_ADAPTER, workMsg, "") ;
					break ;
				}
				default : {
					LogUtility.getPumpMLogger().warn("[Pump M] DW 전문의 응답 노즐을 살펴 보니 소모셀프도 충전기도 아닙니다. 이상하군요.") ;
					break ;
				}
			}
			
			LogUtility.getPumpMLogger().info("[Pump M] 고객카드 승인 응답 전문을 Pump A 에게 전송합니다.") ;
			sendMessage(preamble) ;	
			
			//다쓰노 셀프 거래처가 apl이 없는 경우에 승인불가 처리를 한 후 영수증 출력을 한다.
			// switch문 안에 넣지 않은 이유 : 실패 전문을 먼저 보내고 나서 처리해야 됨.
			if (protocolType == IPumpConstant.PUMP_PROTOCOL_DaSNo ) {
				CB_WorkingMessage cbWrkMsg = (CB_WorkingMessage)workMsg;
				
				if (ICustConstant.DASNO_CUST_TYPE_0.equals(cbWrkMsg.getCustomerType())) {
					// 승인 실패 메세지 출력
					String receipt ;
					//승인 실패인 경우 ODT에 실패 영수증을 보내준다.
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
					//실패 전문 전송
					sendMessage(preamble) ;	
				}
					
			}
//			 보내려는 전문을 향후를 위해서 저장한다.
			if (isCustInfoSave(protocolType, workMsg))
				PumpMODTSaleManager.setCustInfo(dwMsg.getDeviceID(), workMsg, dwMsg);
			
			if (S9Util.checkControlStatus(dwMsg)){
				SqlSession session = null;
				LogUtility.getPumpMLogger().info("[Pump M] 해당 거래처는 위배거래처입니다. cust_code = " + dwMsg.getCust_code());
				try {
					session = SqlSessionFactoryManager.openSqlSession();
					
					T_KH_VIOLATIONData data = T_KH_VIOLATIONHandler.getHandler().getT_KH_VIOLATIONDataByCustCode(session, dwMsg.getCust_code());
					
					if (data == null) {
						LogUtility.getPumpMLogger().info("[Pump M] 해당 거래처는 위배 내역이 존재하지 않습니다. cust_code = " + dwMsg.getCust_code() );
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
									"출하상태 메세지" +  POSHeader.DELIMITER_STRING +
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
			LogUtility.getPumpMLogger().warn("[Pump M] timeout 이 되어서 이미 처리되었습니다.") ;
		} 
	}
        
    
    /**
     * POS 에게 고객 카드 수행 요청에 대한 응답을 받아서 이를 CAT M 에게 전송한다.
     * 
     * @param uniqueKey	: Unique Key
     * @param dyMsg		: POS 로 부터 받은 DY 전문
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
			
			//영업화물 거래처인 경우 거래처카드, 보너스카드, 신용카드로 할인 및 보너스 적립 여부 판단. edited by  ykjang
			if ("05".equals(dyMsg.getCust_cd_item()))
				returnPOSMsg = CustUtil.isDiscountCargo(uPosMsg, returnPOSMsg, dyMsg.getCust_card_no());

			// 유가보조 거래처인 경우 국민 거래카드로 결제할 때 PL할인이 있으면 PL할인을 없으면 카드할인을 실시한다.
			
			// 2013.10.06 ksm   대불상단주유소 장애건.
			// 목표가와 점두가가 똑같을 경우 POS에서 DY전문에 할인여부가 0으로 넘어왔음.
			// POS수정 예정임.
			// KixxHUB도 DY 전문에 할인여부가 1이면 추가할인 적용하지 않음.
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
			LogUtility.getPumpMLogger().info("[Pump M] 고객 카드 수행 결과를 CAT M 에게 전송합니다.") ;
		} else {
			LogUtility.getPumpMLogger().warn("[Pump M] timeout 이 되어서 이미 처리되었습니다.") ;
		}    			
	}
      
    /**
     * 차량 단축 번호 조회 결과 1대 일 경우 Pump A 로 전송하지 않고, POS 로 고객조회 요청을 보낸다.
     * 
     * @param uniqueKey	: Unique Key
     * @param duMsg		: DU 전문
     */
    private void processS9WithDU(String uniqueKey, POS_DU duMsg) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PosAController/" + "processS9WithDU()");
		
		LogUtility.getPumpMLogger().info("[Pump M] 차량조회 결과 1대이기 때문에, POS 에 고객 조회 요청을 보낸다. 이는 " +
			"가상의 S9 전문을 만들어서 시작한다.") ;
		S9_WorkingMessage fakeS9WorkMsg = S9Util.createS9WorkingMessage(duMsg) ;
		String messageID = fakeS9WorkMsg.getMessageID() ;
		String khproc_no = "" ; // 사용하지 않음.
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
     * POS로 부터 ODT Update 명령을 받아서 이를 Pump A 에게 전송한다.
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
     * POS 와 연결된 이후 초기화시 초기화에 관련된 Table 을 모두 삭제 한 이후 정보를 받는다.
     * 		Table
     * 			일반환경 설정 
     * 			주유기 환경
     * 			노즐별 Parameter
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
	 * LockingManager 의 unLocking 함수는 이 함수를 호출하여서, POS 에게 unLocking 을 요청한다.
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
	 * LockingManager 의 unLocking 함수는 이 함수를 호출하여서, POS 에게 해지 했음을 알린다.
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
     * POS A 으로부터 요청 혹은 전송 받은 데이터를 다른 모듈에게 전송시 사용.
     * 		- POS Adapter
     * 			Command ID List							from where
     * 				- POS Protocol
     * 					DA	: 일반 환경 설정 요청			: PosAController
     * 					DC	: 주유기 환경 설정 요청		: PosAController
     * 					DE	: 주유 중 정보 전송			: PumpAController
     * 					DG	: 주유 완료 정보 전송			: PumpAController
     * 					DM	: 보관증 발급 조회 요청		: PumpAController
     * 					DP	: Preset 자료 요청 응답		: PumpAController
     * 					DQ	: Preset 정보 전송 			: PumpAController
     * 					DS	: Locking/unLocking 응답	: PumpAController
     * 					CY  : selfODT Version정보 응답  : PumpAController
     * 
     * 		- PumpAController
     * 			Command ID List
     * 				- PumpA Protocol
     * 					DI	: 주유 제어 명령 전송 			: PosAController 
     * 					DO	: Preset 자료 요청			: PosAController
     *					DK	: 정액 / 정량 설정 요청		: PosAController
     *					DN	: 보관량 요청 응답			: PosAController
     *					DR	: Locking/unLocking 요청	: PosAController
     *					   
     *
     * @param preambleData	: 전송할 데이터
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
	    					// POS 로 전송되는 전문이 전송되지 않을 경우 Pump M 으로 반송될지 여부를 설정.
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
     * KH 동작중에 POS 로 부터 받은 Time parameter 정보를 Pump A 에 전송한다.
     * 
     * @param khContent	: POS 로 부터 받은 Time Parameter 정보
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
     * PumpAController 에게 초기화 요청을 한다.
     * 
     * @param msg	: START String
     */
    private void sendStartMessage(String msg) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PosAController/" + "sendStartMessage()");
		
		getProducer_PumpMPumpA_InitReq().produce(msg);  
    }

	/**
	 * LockingManager 의 locking 함수는 이 함수를 호출하여서, POS 에게 locking 임을 알린다.
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
	 * 운영중에 주유기 일반 환경 설정 정보를 POS 로 부터 받아서 update 한다.
	 * 
	 *  added by yhcheon at 2009.05.14
     * 		DB	: 일반 환경 설정 응답
     * 
     * @param receiving_posa	: POS 로 부터 전달받은 초기화 정보
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
