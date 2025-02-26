package com.gsc.kixxhub.module.pumpm.pump.util;

import java.util.ArrayList;

import org.apache.ibatis.session.SqlSession;

import com.gsc.kixxhub.common.data.GlobalString;
import com.gsc.kixxhub.common.data.ICode;
import com.gsc.kixxhub.common.data.IConstant;
import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.IUPOSConstant;
import com.gsc.kixxhub.common.data.posdata.POSHeader;
import com.gsc.kixxhub.common.data.posdata.POS_DW;
import com.gsc.kixxhub.common.data.pump.GA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.data.upos.UPOSMessage;
import com.gsc.kixxhub.common.dbadapter.common.SqlSessionFactoryManager;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_CT_CATHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_STOREHandler;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.cat.PropertyID;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.common.utility.sync.Preamble;
import com.gsc.kixxhub.module.pumpm.pump.manager.PumpMODTSaleManager;

public class ODTUtility_Common {

	public static int trackingNo = 0 ;
	public static int trx_No = 0 ;
	public static int trx_Seq = 0 ;
	
	public static synchronized String getTrackingNo() {
		if (trackingNo > 9990) {
			trackingNo = 0 ;
		}
		return GlobalUtility.appending0Pre(Integer.toString(trackingNo++), 4) ;
	}
	
	public static synchronized String getTrxNo() {
		if (trx_No > 990) {
			trx_No = 0 ;
		}
		return GlobalUtility.appending0Pre(Integer.toString(trx_No++), 3) ;
	}
	
	public static synchronized String getTrxSeq() {
		if (trx_Seq > 9990) {
			trx_Seq = 0 ;
		}
		return GlobalUtility.appending0Pre(Integer.toString(trx_Seq++), 4) ;
	}
	
	public static String getStoreCode() {
		String storeCd = "" ;		
		try {
			storeCd = T_KH_STOREHandler.getHandler().getStoreCode();
		} catch (Exception e) {
			LogUtility.getLogger().error(e.getMessage()) ;
		}		
		return storeCd ;
	}
	public static String getTermId() {
		SqlSession session = null;
		
		try {
			session = SqlSessionFactoryManager.openSqlSession();
			if(GlobalUtility.isNullOrEmptyString(GlobalString.ODT_TERM_ID)) {
				GlobalString.ODT_TERM_ID = T_CT_CATHandler.getHandler().getT_CT_CATData_by_crt_code_type(session, PropertyID.CAT_CRT_CODE_TYPE_SELF_ODT);
			}
		}  catch (Exception e) {
			LogUtility.getLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}
		return GlobalString.ODT_TERM_ID;
	}	// end getTermId
	
	public static synchronized String getRomVer() {
		return "0000" ;
	}
	
	/**
	 * [2008.12.14] 정동명, 정우철, 오정훈, 박종호
	 * 
	 * 만약 LED Code 가 1(성공) 이 아닌 2 (거절) 혹은 3(TimeOut) 인 경우 Message Type 을 변경하도록 한다.
	 * 		0032	-> 0132
	 * 		0034	-> 0134
	 * 		0052	-> 0152
	 * 		0062	-> 0162
	 * 		0046	-> 0146
	 * 		0048	-> 0148
	 * 		0242	-> 0342
	 * 		0244	-> 0344
	 * 		8032	-> 8132
	 * 		8034	-> 8146
	 * 		8046	-> 8148
	 * 		8048	-> 8134
	 * 		8052	-> 8152
	 * 		8062	-> 8162
	 * 		8242	-> 8342
	 * 		8244	-> 8344
	 * 
	 * @param uposMsg
	 */
	private static void changeMessageType(int messageType, UPOSMessage uposMsg) {
		try {
			String ledCode = uposMsg.getLed_code() ;
			
			// PI2, 2016-03-18, CWI AS-IS uPOS Message의 FILLER 값을 변경된 통합전문에 맞게 수정
			//String filer1 = uposMsg.getFiller1() ;
			String SelfPayment_Type = uposMsg.getSelfPayment_type();
			
			switch (messageType) {
				case IUPOSConstant.MESSAGETYPE_INT_0032 :
				case IUPOSConstant.MESSAGETYPE_INT_0034 :
				case IUPOSConstant.MESSAGETYPE_INT_0052 :
				case IUPOSConstant.MESSAGETYPE_INT_0062 :
				case IUPOSConstant.MESSAGETYPE_INT_8032 :
				case IUPOSConstant.MESSAGETYPE_INT_8034 :
				case IUPOSConstant.MESSAGETYPE_INT_0046 :
				case IUPOSConstant.MESSAGETYPE_INT_0048 :
				case IUPOSConstant.MESSAGETYPE_INT_0242 :
				case IUPOSConstant.MESSAGETYPE_INT_0244 :
				case IUPOSConstant.MESSAGETYPE_INT_8046 :
				case IUPOSConstant.MESSAGETYPE_INT_8048 :					
				case IUPOSConstant.MESSAGETYPE_INT_8052 :
				case IUPOSConstant.MESSAGETYPE_INT_8062 : 
				case IUPOSConstant.MESSAGETYPE_INT_8242 :
				case IUPOSConstant.MESSAGETYPE_INT_8244 :{ //현대carpay/모바일플랫폼결제 추가 (결제모듈개선프로젝트)
					// 2016-08-16 twlee 
		        	// selfPayment_Type 관련 log변경
					if ((!IUPOSConstant.LEDCODE_1.equals(ledCode))  && 
							(IConstant.UPOSMESSAGE_PAID_TYPE_11.equals(SelfPayment_Type) || IConstant.UPOSMESSAGE_PAID_TYPE_12.equals(SelfPayment_Type)
									|| IConstant.UPOSMESSAGE_PAID_TYPE_21.equals(SelfPayment_Type) ||IConstant.UPOSMESSAGE_PAID_TYPE_22.equals(SelfPayment_Type))){
						int newMessageType = messageType + 100 ;
						uposMsg.setMessageType(GlobalUtility.appending0Pre(Integer.toString(newMessageType), 4)) ;
						LogUtility.getPumpMLogger().info("[Pump M] Fail Response. Change MsgType from=" + messageType + " to=" + newMessageType + " NozzleNo: " + uposMsg.getNozzle_no() +  " / Led_Code: " + ledCode + " / SelfPayment_Type: " + SelfPayment_Type) ;
					}
					break ;
				}
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
		}
	}
	
	/**
	 * BL 체크 전문 생성
	 * @param receiving_pumpa	: WorkingMessage (HE_WorkingMessage or HA_WorkingMessage)
	 * @return
	 */
	public static UPOSMessage create0071UPOSMessageFromWorkingMessage(WorkingMessage receiving_pumpa) {
		UPOSMessage uPosMsg = null ;
		String commandID = receiving_pumpa.getCommand() ;
    	
		LogUtility.getPumpMLogger().info("[Pump M] Create BL Check UPOSMessage") ;
		switch (commandID) {
			case IPumpConstant.COMMANDID_HA : {
				// HA	소모셀프 카드결제 승인 요청
				uPosMsg = ODTUtility_SoMo.create0071UPOSMessageFromWorkingMessage_SoMo(receiving_pumpa) ;
				break ;
			}
			case IPumpConstant.COMMANDID_HE : {
				// HE	다쓰노 셀프 카드 결제 승인 요청
				uPosMsg = ODTUtility_DaSNo.create0071UPOSMessageFromWorkingMessage_DaSNo(receiving_pumpa) ;
				break ;
			}
		}		
		return uPosMsg ;
	}
	
	/**
	 * createUPOSMessageFromWorkingMessage 메소드는 충전기 ODT 혹은 셀프 ODT 에서 승인 요청시 u-POS 전문을
	 * 변경하기 위해서 사용한다.
	 * 
	 * @param receiving_pumpa	: Pump A 로 부터 전송받은 결제 요청 전문
	 * @param khproc_no			: KH 처리번호
	 * @return
	 */
	public static UPOSMessage createUPOSMessageFromWorkingMessage(WorkingMessage receiving_pumpa, String khproc_no) {
		UPOSMessage uPosMsg = null ;
		String commandID = receiving_pumpa.getCommand() ;
    	
		switch (commandID) {
			case IPumpConstant.COMMANDID_HA : {
				// HA	소모셀프 카드결제 승인 요청
				LogUtility.getPumpMLogger().info("[Pump M] 소모셀프 카드결제 승인 요청전문을 UPOSMessage Class 로 변경합니다.") ;
				uPosMsg = ODTUtility_SoMo.createUPOSMessageFromWorkingMessage_SoMo(receiving_pumpa, khproc_no) ;
				break ;
			}
			case IPumpConstant.COMMANDID_HE : {
				// HE	다쓰노 셀프 카드 결제 승인 요청
				LogUtility.getPumpMLogger().info("[Pump M] 다쓰노 셀프 카드 결제 승인 요청전문을 UPOSMessage Class 로 변경합니다.") ;
				uPosMsg = ODTUtility_DaSNo.createUPOSMessageFromWorkingMessage_DaSNo(receiving_pumpa, khproc_no, false) ;
				break ;
			}
			case IPumpConstant.COMMANDID_SB : {
				// SB	충전기 카드  결제 승인/승인취소 요청
				LogUtility.getPumpMLogger().info("[Pump M] 충전기 카드  결제 승인/승인취소 요청전문을 UPOSMessage Class 로 변경합니다.") ;
				uPosMsg = ODTUtility_Recharge.createUPOSMessageFromWorkingMessage_Recharge(receiving_pumpa, khproc_no) ;
				break ;
			}
			case IPumpConstant.COMMANDID_BA : {
				// BA	충전기 보너스 점수 누적 요청
				LogUtility.getPumpMLogger().info("[Pump M] 충전기 보너스 점수 누적 요청전문을 UPOSMessage Class 로 변경합니다.") ;
				uPosMsg = ODTUtility_Recharge.createUPOSMessageFromWorkingMessage_Recharge(receiving_pumpa, khproc_no) ;
				break ;
			}
			case IPumpConstant.COMMANDID_TJ : {
				// TJ	충전기 현금 영수증 요청
				LogUtility.getPumpMLogger().info("[Pump M] 충전기 현금 영수증 요청전문을 UPOSMessage Class 로 변경합니다.") ;
				uPosMsg = ODTUtility_Recharge.createUPOSMessageFromWorkingMessage_Recharge(receiving_pumpa, khproc_no) ;
				break ;
			}
			case IPumpConstant.COMMANDID_TD : {
				// TD	충전기 보너스 카드의 포인트 점수 조회 요청
				LogUtility.getPumpMLogger().info("[Pump M] 충전기 보너스 카드의 포인트 점수 조회 요청 전문을 UPOSMessage Class 로 변경합니다.") ;
				uPosMsg = ODTUtility_Recharge.createUPOSMessageFromWorkingMessage_Recharge(receiving_pumpa, khproc_no) ;
				
				break ;
			}
		}
		uPosMsg.print();
		return uPosMsg ;
	}


	/**
	 * UPOS 응답 전문을 받아서 WorkingMessage 로 변경을 한다. (승인 응답전문)
	 * 
	 * as-is, to-be 응답 전문을 모두 처리하도록 변경 , 양일준, 2016.04.21
	 * @param messageID
	 * @param uPosMsg
	 * @return
	 */
	public static ArrayList<WorkingMessage> createWorkingMessageFromUPOSMessage(String messageID, UPOSMessage uPosMsg) {
		LogUtility.getPumpMLogger().info("[Pump M] Convert UPOSMessage to WorkingMessage") ;
		ArrayList<WorkingMessage> workMsgArray = null ;		
		String nozzle_no = uPosMsg.getNozzle_no() ;
		try {
			int nozProtocolInt = PumpMUtil.getConnectedRepODTProtocolFromNozzleNo(nozzle_no) ;
			
			/**
			 * 2016.03.18
			 * 	기존 ODT 장비의 경우, VAN 으로 부터 승인 응답 전문 수신시, LED CODE 에 성공 여부가 표기
			 * 	장애대응시스템에서는 LED CODE 를 설정하지 않고, VAN 응답코드로 사용
			 * 		이로 인하여 기존 ODT 에 대한 응답 전문을 장애대응시스템에서 수신시, 이후 처리를 수행하기 전에 LED CODE 의 정보를 입력하도록 수정.
			 * 	보너스 사용 및 보너스 단독 적립의 경우 loyalty response code 를 사용해야 함.
			 * 
			 */
			int messageTypeInt = Integer.parseInt(uPosMsg.getMessageType()) ;
			switch (nozProtocolInt) {
				case IPumpConstant.PUMP_PROTOCOL_SOMO :
				case IPumpConstant.PUMP_PROTOCOL_DaSNo :
				case IPumpConstant.PUMP_PROTOCOL_NewDaSNo : {
					if (uPosMsg != null) {
						if ("0000".equals(uPosMsg.getVan_Res_Code())) {
							uPosMsg.setLed_code("1") ;	// 승인
						}else if ("3333".equals(uPosMsg.getVan_Res_Code())) {
							uPosMsg.setLed_code("3") ;	// 회선실패
						}else {
							uPosMsg.setLed_code("2") ;	// 거부
						}
					}
					break;
				}
				case IPumpConstant.PUMP_PROTOCOL_Recharge : {
					if (uPosMsg != null) {
						switch (messageTypeInt) {
							case IUPOSConstant.MESSAGETYPE_INT_0004:
							case IUPOSConstant.MESSAGETYPE_INT_8004:
							case IUPOSConstant.MESSAGETYPE_INT_0062:
							case IUPOSConstant.MESSAGETYPE_INT_8062:{
								if ("00000".equals(uPosMsg.getLoyaltyReqCode())) {
									uPosMsg.setLed_code("1") ;
								} else {
									uPosMsg.setLed_code("2") ;
								}
								break;
							}
							default:{
								if ("0000".equals(uPosMsg.getVan_Res_Code())) {
									uPosMsg.setLed_code("1") ;	// 승인
								}else if ("3333".equals(uPosMsg.getVan_Res_Code())) {
									uPosMsg.setLed_code("3") ;	// 회선실패
								}else {
									uPosMsg.setLed_code("2") ;	// 거부
								}
							}
						}
					}
					break;
				}
			}
			switch (nozProtocolInt) {
				case IPumpConstant.PUMP_PROTOCOL_SOMO : {
					workMsgArray = ODTUtility_SoMo.createWorkingMessageFromUPOSMessage_SoMo(uPosMsg) ;
					break ;
				}
				case IPumpConstant.PUMP_PROTOCOL_DaSNo : 
				case IPumpConstant.PUMP_PROTOCOL_NewDaSNo :{	//2012.09.26 ksm
					workMsgArray = ODTUtility_DaSNo.createWorkingMessageFromUPOSMessage_DaSNo(uPosMsg) ;
					break ;
				}
				case IPumpConstant.PUMP_PROTOCOL_Recharge : {
					workMsgArray = ODTUtility_Recharge.createWorkingMessageFromUPOSMessage_Recharge(uPosMsg) ;
					break ;
				}
				
				case IPumpConstant.PUMP_PROTOCOL_GSC_SELF :{	//PI2, GSC 신규 프로토콜 추가, 통합 ODT 연동, 2015.11.18 - cwi
					workMsgArray = ODTUtility_GSCSelf.createWorkingMessageFromUPOSMessage_GSC(uPosMsg) ;
					break ;
				}
				case IPumpConstant.PUMP_PROTOCOL_NewRecharge : {   //PI2, 신규 충전기ODT 프로토콜 추가, 2016.02.26 양일준 
					workMsgArray = ODTUtility_NewRecharge.createWorkingMessageFromUPOSMessage_NewRecharge(uPosMsg) ;
					break ;
				}
				default : {
					LogUtility.getLogger().error("[Pump M] Strange Protocol Type=" + nozProtocolInt) ;					
				}
			}
			
		} catch (Exception e) {
			LogUtility.getLogger().error(e.getMessage(),e) ;
		}
		if (workMsgArray != null) {
			for (int i = 0 ; i < workMsgArray.size() ; i++) {
				workMsgArray.get(i).setMessageID(messageID) ;
				workMsgArray.get(i).setDirection(IPumpConstant.DIRECTION_FROM_MODULE) ;
			}
		}
		return workMsgArray ;
	}
	
	
	/**
	 * 셀프 결제에 대한 uPos전문의 Filler를  구성한다.
	 * @author ksm
	 * @since 2013.07.19
	 * @param messageType
	 * @param uposMsg
	 */
	private static void fillFillerData(int messageType, UPOSMessage uposMsg) {
			
		/**
		 * 2016.03.31 WooChul Jung.
		 * 	더 이상 사용하지 않는 코드임.
		 */
		/*if( messageType != IUPOSConstant.MESSAGETYPE_INT_0001 ){
			// Filler4 : 모바일 결제여부(1) + RS + 현장할인 여부(1) + RS + 화물특화거래상품코드(4)
			// Filler5 : 보너스카드 Reading 매체(2) + RS + 제휴포인트 종류(3) + RS + 신용카드 Reading 매체(2) 
			uposMsg.setFiller4(""+IUPOSConstant.DELIMITER_RS_STRING+""+IUPOSConstant.DELIMITER_RS_STRING+"");
			uposMsg.setFiller5(""+IUPOSConstant.DELIMITER_RS_STRING+""+IUPOSConstant.DELIMITER_RS_STRING+"");
			
			// PI2, 2016-03-18, CWI AS-IS uPOS Message의 FILLER 값을 변경된 통합전문에 맞게 수정
			uposMsg.setMobilePay_yn("");	  // 모바일 결제여부
			uposMsg.setPromptDiscount_yn(""); // 현장할인 여부
			uposMsg.setCustGoods_code("");	  // 화물특화거래상품코드
			
			uposMsg.setBonCardReading_type("");    // 보너스카드 리딩매체
			uposMsg.setPartner_code("");		   // 제휴포인트종류
			uposMsg.setCreditCardReading_type(""); // 신용카드리딩매체
		}*/
	}
	
	
    /**
	 * Sale M 으로 보내기 전 Pre Processing 을 진행한다.
	 * 
	 * @param preambleData	: Preamble Object
	 */
	public static void preProcessingBeforeSendingToSaleM(Preamble preambleData) {
    	try {
			Object obj = preambleData.getPreamble() ;
			if (obj instanceof UPOSMessage) {
				UPOSMessage uposMsg = (UPOSMessage) obj ;
				
				// 1. 주유건의 결제 응답 전문에서 lastPayment_yn 을 설정한다. 이는 0001 전문이 올라왔을 경우에만 1 로 설정한다.
    			int messageType = Integer.parseInt(uposMsg.getMessageType()) ;
    			switch (messageType) {
    				case IUPOSConstant.MESSAGETYPE_INT_0001 :{		// 복합결제미처리완료 요청인 경우에만  lastPayment_yn 을 1로 설정
    					Thread.sleep(1000);
    					uposMsg.setLastPayment_yn(IUPOSConstant.LASTPAYMENT_YN_YES) ;
    					break ;
    				}
    				default : {
    					int nozProtocolInt = PumpMUtil.getConnectedRepODTProtocolFromNozzleNo(uposMsg.getNozzle_no()) ;
    					
    					switch (nozProtocolInt) {
							/**
							 * [2008.12.14] 정동명, 박종호, 정우철, 오종훈
							 *	응답 전문이 실패일 경우는 Message Type 을 변경하도록 한다. 
							 */ 							
    						case IPumpConstant.PUMP_PROTOCOL_SOMO : 
							case IPumpConstant.PUMP_PROTOCOL_DaSNo : 
							case IPumpConstant.PUMP_PROTOCOL_NewDaSNo :{	 // 2012.09.26 ksm
								changeMessageType(messageType, uposMsg) ;
								
								// 2013.07.19 ksm 응답 upos 전문에 Filler4, Filler5 구성
								fillFillerData(messageType, uposMsg) ;
								
								break ;
							}
    					} 		
    					
    					switch (nozProtocolInt) {
    						// 다쓰노 셀프에서 가득 주유인 경우 마지막 응답 전문에 lastPayment_yn 을 1 로 설정한다.
	    					case IPumpConstant.PUMP_PROTOCOL_DaSNo : 
							case IPumpConstant.PUMP_PROTOCOL_NewDaSNo :{	//2012.09.26 ksm
    							ODTUtility_DaSNo.preProcessingBeforeSendingToSaleM(uposMsg) ;
    							break ;
    						}
    						default : {
    							uposMsg.setLastPayment_yn(IUPOSConstant.LASTPAYMENT_YN_NO) ;
    						}
    					} 					
    				}
    			}
    			
				// 2. 응답 전문을 PumpMODTSaleManager 에 저장하여 차후 취소 요청 전문이 있을 경우 이용하도록 한다.
				PumpMODTSaleManager.addUPOSMessage(uposMsg.getNozzle_no(), uposMsg) ;

				// 3. 거래처로 인한 결제 응답 인 경우 거래처 정보를 UPOSMessage 응답 전문에 포함하도록 한다.
				String nozzle_no = uposMsg.getNozzle_no() ;
				
				// PI2, CWI, 2016-04-11 
				// 거래처 카드를 로드 후 초기화 버튼을 클릭 할 경우 거래처 정보가 custHash에 남는 문제가 발생 
				// 일반 거래일 경우 4201처리 시 hash값이 초기화 되지 않는 문제가 있어 이를 수정 함.
				// 방어코드로 GSC Self의 경우 ODT에서 올라온 데이터만을 Pos에 전송하기에 아래 로직을 수행 하지 못하도록 제약을 둔다.
				if ((nozzle_no != null) && (nozzle_no.length() == 2)) {
					int newNozProtocolInt = PumpMUtil.getConnectedRepODTProtocolFromNozzleNo(uposMsg.getNozzle_no()) ;
					// if((newNozProtocolInt != IPumpConstant.PUMP_PROTOCOL_GSC_SELF) && (newNozProtocolInt != IPumpConstant.PUMP_PROTOCOL_NewRecharge)){
					if(newNozProtocolInt != IPumpConstant.PUMP_PROTOCOL_NewRecharge){
						
						POSHeader posObj = PumpMODTSaleManager.getCustPOSPumpM(nozzle_no) ; 
						if ((posObj != null) && (posObj instanceof POS_DW)) {
							POS_DW dwPosMsg = (POS_DW) posObj ;
							
							if (PumpMObjectValidation.validatePumpMObject(dwPosMsg)) {
								LogUtility.getLogger().info("[Pump M] add CustInfo to UPOSMessage before sending it to Sale M") ;
								uposMsg.setCustCard_No(dwPosMsg.getCust_card_no()) ;	// 거래처 카드번호
								uposMsg.setSs_crStNum(dwPosMsg.getCust_code()) ;		// 거래처 번호
								uposMsg.setSs_carNum(dwPosMsg.getCar_no()) ;			// 차량 번호
							}
						}
						
					}
				}
			}
    	} catch (Exception e) {
    		LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
    	}
    }
    
	
	/**
     * ODT 로 부터 요청에 의한 응답 전문에서 아래 사항에 대한 pre-Processing 을 처리한다.
     * 		1. 매장 점수 , 매장 발생 점수
     * 			응답 전문은 이번 주유건을 제외한 매장 점수와 이번 주유건으로 인해 발생되는 매장 발생 점수가 포함된다.
     * 			하지만 영수증에 출력 할때와 POS 에 전송할 전문의 매장 점수는 이번 주유건이 포함되도록 해야 한다. 
     * 			(CAT 단말기는 이와 같이 처리하며, 따라서 동일하게 처리하도록 구성하였다.)
     * 
     * @param uPosMsg	: ODT 로 부터 발생된 결제 요청 전문
     */
    public static void preProcessingUPOSMessageFromCAT(UPOSMessage uPosMsg) {
    	try {
    		
    		if (uPosMsg != null) {
    			// 1. Change Local Point = Local Point + Occur Point
    			String local_point = GlobalUtility.getStringValue(uPosMsg.getLocal_point()) ;
				String local_occurPoint = GlobalUtility.getStringValue(uPosMsg.getLocal_occurPoint()) ;
				String newLocal_Point = GlobalUtility.getStringValue(GlobalUtility.plus(local_point, local_occurPoint)) ;
				int newLocalPointInt = (int) Double.parseDouble(newLocal_Point) ;
				if (newLocalPointInt != 0) {
					LogUtility.getPumpMLogger().debug("[Pump M] Add localPoint=localPoint+OccurPoint: local_point="+ local_point + ":" +
							"local_occurPoint="+ local_occurPoint) ;
					uPosMsg.setLocal_point(newLocal_Point) ;
				}
				String nozzle_no = uPosMsg.getNozzle_no() ;

				int nozProtocolInt = PumpMUtil.getConnectedRepODTProtocolFromNozzleNo(nozzle_no) ;
					
					/**
					 * 2016.03.18
					 * 	기존 ODT 장비의 경우, VAN 으로 부터 승인 응답 전문 수신시, LED CODE 에 성공 여부가 표기
					 * 	장애대응시스템에서는 LED CODE 를 설정하지 않고, VAN 응답코드로 사용
					 * 		이로 인하여 기존 ODT 에 대한 응답 전문을 장애대응시스템에서 수신시, 이후 처리를 수행하기 전에 LED CODE 의 정보를 입력하도록 수정.
					 * 	보너스 사용 및 보너스 단독 적립의 경우 loyalty response code 를 사용해야 함.
					 * 
					 */
				int messageTypeInt = Integer.parseInt(uPosMsg.getMessageType()) ;
				switch (nozProtocolInt) {
					case IPumpConstant.PUMP_PROTOCOL_SOMO :
					case IPumpConstant.PUMP_PROTOCOL_DaSNo :
					case IPumpConstant.PUMP_PROTOCOL_NewDaSNo : {
						if (uPosMsg != null) {
							if ("0000".equals(uPosMsg.getVan_Res_Code())) {
								uPosMsg.setLed_code("1") ;	// 승인
							}else if ("3333".equals(uPosMsg.getVan_Res_Code())) {
								uPosMsg.setLed_code("3") ;	// 회선실패
							}else {
								uPosMsg.setLed_code("2") ;	// 거부
							}
						}
						break;
					}
					case IPumpConstant.PUMP_PROTOCOL_Recharge : {
						if (uPosMsg != null) {
							switch (messageTypeInt) {
								case IUPOSConstant.MESSAGETYPE_INT_0004:
								case IUPOSConstant.MESSAGETYPE_INT_8004:
								case IUPOSConstant.MESSAGETYPE_INT_0062:
								case IUPOSConstant.MESSAGETYPE_INT_8062:{
									if ("00000".equals(uPosMsg.getLoyaltyReqCode())) {
										uPosMsg.setLed_code("1") ;
									} else {
										uPosMsg.setLed_code("2") ;
									}
									break;
								}
								default:{
									if ("0000".equals(uPosMsg.getVan_Res_Code())) {
										uPosMsg.setLed_code("1") ;	// 승인
									}else if ("3333".equals(uPosMsg.getVan_Res_Code())) {
										uPosMsg.setLed_code("3") ;	// 회선실패
									}else {
										uPosMsg.setLed_code("2") ;	// 거부
									}
								}
							}
						}
						break;
					}
				}
				
				switch (nozProtocolInt) {
		    		case IPumpConstant.PUMP_PROTOCOL_SOMO :
		            case IPumpConstant.PUMP_PROTOCOL_DaSNo :
		            case IPumpConstant.PUMP_PROTOCOL_NewDaSNo : {
		            	if (uPosMsg != null) {
			            	switch (messageTypeInt) {
			            		case IUPOSConstant.MESSAGETYPE_INT_0032 :
								case IUPOSConstant.MESSAGETYPE_INT_0034 :
								case IUPOSConstant.MESSAGETYPE_INT_0046 :
								case IUPOSConstant.MESSAGETYPE_INT_0048 :
								case IUPOSConstant.MESSAGETYPE_INT_0052 :
								case IUPOSConstant.MESSAGETYPE_INT_0062 :
								case IUPOSConstant.MESSAGETYPE_INT_0242 :
								case IUPOSConstant.MESSAGETYPE_INT_0244 :	
								case IUPOSConstant.MESSAGETYPE_INT_8032 :
								case IUPOSConstant.MESSAGETYPE_INT_8034 :
								case IUPOSConstant.MESSAGETYPE_INT_8242 :
								case IUPOSConstant.MESSAGETYPE_INT_8244 :
								case IUPOSConstant.MESSAGETYPE_INT_8052 :
								case IUPOSConstant.MESSAGETYPE_INT_8062 : {
					            	// 2016-08-16 twlee 
						        	// selfPayment_typeHash에 저장해 놓은 selfPayment_type 값을 응답전문에 넣음
					            	uPosMsg.setSelfPayment_type(PumpMODTSaleManager.getNozzleBySelfPayment_type(uPosMsg.getNozzle_no()));
					            }
							}
		            	}
		            	break;
					}
	    		}
	    	}
    	} catch (Exception e) {
    		LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
    	}		
	}
	
	/**
	 * 0001 전문 전송 여부를 설정한다.
	 * 
	 * @param nozzle_no		: 노즐 번호
	 * @param khproc_no		: KH 처리번호
	 * @param workMsg		: 판매 완료 전문 (TR_WorkingMessage or SH_WorkingMessage)
	 * @return
	 */
	public static boolean shouldSend0001UPOSMessageToSaleM(String nozzle_no, String khproc_no, WorkingMessage workMsg) {
		boolean rlt = true ;
		try {
			int nozProtocolInt = PumpMUtil.getConnectedRepODTProtocolFromNozzleNo(nozzle_no) ;
			
			switch (nozProtocolInt) {
				case IPumpConstant.PUMP_PROTOCOL_SOMO : {
					rlt = ODTUtility_SoMo.shouldSend0001UPOSMessageToSaleM(nozzle_no,khproc_no,workMsg) ;
					break ;
				}
				case IPumpConstant.PUMP_PROTOCOL_DaSNo : 
				case IPumpConstant.PUMP_PROTOCOL_NewDaSNo :{	//2012.09.26 ksm
					rlt = ODTUtility_DaSNo.shouldSend0001UPOSMessageToSaleM(nozzle_no,khproc_no,workMsg) ;
					break ;
				}
				case IPumpConstant.PUMP_PROTOCOL_GSC_SELF :{	//PI2, GSC 신규 프로토콜 추가, 통합 ODT 연동, 2016.03.18 - CWI
					rlt = ODTUtility_GSCSelf.shouldSend0001UPOSMessageToSaleM(nozzle_no,khproc_no,workMsg) ;
					break ;
				}
				case IPumpConstant.PUMP_PROTOCOL_NewRecharge :  //2016.03.28 양일준
				case IPumpConstant.PUMP_PROTOCOL_Recharge : {
					rlt = ODTUtility_Recharge.shouldSend0001UPOSMessageToSaleM(nozzle_no,khproc_no,workMsg) ;
					break ;
				}
				default : {
				}
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
		
		LogUtility.getPumpMLogger().info("[Pump M] ODTUtility_Common.shouldSend0001UPOSMessageToSaleM = " + rlt) ;
		return rlt ;
	}
	
	/**
	 * UPOSMessage 응답 전문을 Sale M 으로 보낼지를 판단한다.
	 * 아래 내용들은 Sale M 으로 전송하지 않는다.
	 * 		1. GS보너스점수조회 응답
	 * 		2. myLG보너스점수조회 응답 - 충전소용
	 * 		3. 전자상품권거래내역조회 응답 - 충전소용
	 * 		4. 각 Protocol 별로 한번더 검증
	 * @param uPosMsg	: UPOSMessage 응답 전문
	 * @return
	 */
    public static boolean shouldSendToSaleM(UPOSMessage uPosMsg) {
    	boolean rlt = true ;
    	
    	if (uPosMsg == null)  return false ;
    	
    	try {
    		int messageTypeInt = Integer.parseInt(uPosMsg.getMessageType()) ;
    		switch (messageTypeInt) {
    			// 충전기, 셀프 주유소 공통인 전문들
    			case IUPOSConstant.MESSAGETYPE_INT_0112 :	// GS보너스점수조회 응답
    			case IUPOSConstant.MESSAGETYPE_INT_0114 :	// myLG보너스점수조회 응답 
    			case IUPOSConstant.MESSAGETYPE_INT_0116 : 	// 전자상품권거래내역조회 응답 
//    			case IUPOSConstant.MESSAGETYPE_INT_0072 :	// 신용카드 확인 응답 (BL 체크) -> BL 결과 도 올리도록 변경 
    			{
    				rlt = false ;
    				break ;
    			}
    			default : {
    				String nozzle_no = uPosMsg.getNozzle_no() ;
    				try {
    					int nozProtocolInt = PumpMUtil.getConnectedRepODTProtocolFromNozzleNo(nozzle_no) ;
    					
    					switch (nozProtocolInt) {
    						case IPumpConstant.PUMP_PROTOCOL_SOMO : {
    							rlt = ODTUtility_SoMo.shouldSendToSaleM(uPosMsg) ;
    							break ;
    						}
    						case IPumpConstant.PUMP_PROTOCOL_DaSNo : 
							case IPumpConstant.PUMP_PROTOCOL_NewDaSNo :{	//2012.09.26 ksm
    							rlt = ODTUtility_DaSNo.shouldSendToSaleM(uPosMsg) ;
    							break ;
    						}
							case IPumpConstant.PUMP_PROTOCOL_GSC_SELF :{	//PI2, GSC 신규 프로토콜 추가, 통합 ODT 연동, 2016.03.18 - CWI
    							rlt = ODTUtility_GSCSelf.shouldSendToSaleM(uPosMsg) ;
    							break ;
    						}
    						case IPumpConstant.PUMP_PROTOCOL_Recharge : {
    							rlt = ODTUtility_Recharge.shouldSendToSaleM(uPosMsg) ;
    							break ;
    						}
    						case IPumpConstant.PUMP_PROTOCOL_NewRecharge : {
    							rlt = ODTUtility_NewRecharge.shouldSendToSaleM(uPosMsg) ;
    							break ;
    						}
    					}
    				} catch (Exception e) {
    					LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
    				}
    				
    				break ;
    			}
    		}
    	} catch (Exception e) {
    		LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
    	}
    	
    	LogUtility.getPumpMLogger().info("[Pump M] ODTUtility_Common.shouldSendToSaleM=" + rlt) ;
    	return rlt ;	
    }
	
	/**
	 * 거래처 조회 요청에 대한 응답 전문 수신이후 전처리를 실시한다.
	 * 
	 * @param pumpM_DW		: 거래처 조회 요청에 의한 응답 전문
	 * @return
	 */
	public static boolean validatePOSPumpM_DW(POS_DW pumpM_DW) {
		boolean rlt = false ;
		
		String status_code_card = pumpM_DW.getStatus_code_card() ; 	// 거래 상태		
		String card_code_base = pumpM_DW.getCard_code_base() ;		// 카드 기준

		if (card_code_base.equals(ICode.CARD_CODE_BASE_06)) { // 카드 기준 06 : 미등록 카드
			rlt = false;			
		} else {
			if (status_code_card.equals(ICode.STATUS_CODE_CARD_01)) { // 거래상태 01 : 정상
				rlt = true;		
				
				int nozProtocolInt = PumpMUtil.getConnectedRepODTProtocolFromNozzleNo(pumpM_DW.getDeviceID()) ;
				
				switch (nozProtocolInt) {
					case IPumpConstant.PUMP_PROTOCOL_SOMO : {
						rlt = ODTUtility_SoMo.validatePOSPumpM_DW(pumpM_DW.getDeviceID(),pumpM_DW) ;
						break ;
					}
					default : {
					}
				}
			} else {	// 거래상태 02, 03 : 정지, 말소
				rlt = false;	
			}
		}
		
		LogUtility.getPumpMLogger().info("[Pump M] ODTUtility_Common.validatePOSPumpM_DW=" + rlt) ;
		return rlt ;
	}

	public static void logAdditionalInfo(GA_WorkingMessage gaWorkMsg) {
		try {
			/**
			 * if message Type is 0013 AND prompt-discountYn = '1' and 100% prompt-discount then leave 100% prompt-discount in log file
			 */
			String nozID = gaWorkMsg.getConnectNozzleNo() ;
			String messageType = gaWorkMsg.getMessageType() ;
			String promptDiscountYn = gaWorkMsg.getUnityMessage().getPromptDiscount_yn() ;
			String paymentAmt = GlobalUtility.getIntString(gaWorkMsg.getUnityMessage().getPayment_amt(), 8) ;
			String preCardNo = PumpMODTSaleManager.getCardNo(nozID) ;
			
			if (IUPOSConstant.MESSAGETYPE_0013.equals(messageType) &&
					"1".equals(promptDiscountYn) && 
					"0".equals(paymentAmt)) {
				LogUtility.getLogger().info("[Pump M] 100% promptDiscount payment request.nozID=" + nozID + "#pre-CardNo=" + preCardNo) ;
			}
			
		} catch (Exception e) {
			LogUtility.getLogger().error(e.getMessage(),e);
		}		
	}
	
	/**
	 * 
	 * PI2 2016-03-22 twlee 장애대응에서 현금영수증처리시 암호화된 카드번호는 카드번호+"=" 처리 
	 *  
	 * @param cardNumber	:	카드번호
	 * @return
	 */
	public static String getChangeCashReceiptNumber(String cardNumber){
    	if(!GlobalUtility.isNullOrEmptyString(cardNumber)){
    		// 20160902 PI2 twlee 현금영수증카드로 현금영수증 등록시 '='이 포함되어 올라와 중복으로 '=' 붙지 않게 처리
    		if(cardNumber.indexOf("=") == -1){
    			cardNumber += "=";
    		}
    	}
		return cardNumber ;
    }
    
    /**
	 * 
	 * PI2 2016-03-22 twlee 장애대응에서 보너스카드 사용시 카드 비밀번호 암호화하여 전송("C"+암호화된비밀번호) 
	 * 암호화유형(P:평문,C:암호문)+ 비밀번호
	 *  
	 * @param pwd	:	보너스카드 비밀번호
	 * @return
	 */
    public static String getChangeLoyaltyPassword(String pwd){
    	String encryptPwd = "";
    	try{
    		if(!GlobalUtility.isNullOrEmptyString(pwd)){
    			encryptPwd = "C"+TripleDES.encryptText(pwd);
        	}
    	} catch (Exception e) {
			LogUtility.getLogger().error(e.getMessage(),e);
		}
    	
		return encryptPwd ;
    }
    
    /**
	 * 2016.06.01 WooChul Jung
	 * 	Set the customer card in u-POS
	 */
	
	public static void setCustomerCard(UPOSMessage uPosMsg) {
		try {
			String nozzle_no = uPosMsg.getNozzle_no() ;
			
			if(GlobalUtility.isNullOrEmptyString(nozzle_no)) return;
			
			try {
				int nozProtocolInt = PumpMUtil.getConnectedRepODTProtocolFromNozzleNo(nozzle_no) ;
				
				switch (nozProtocolInt) {
					case IPumpConstant.PUMP_PROTOCOL_NewRecharge : {
						POSHeader posObj = PumpMODTSaleManager.getCustPOSPumpM(nozzle_no) ; 
						if ((posObj != null) && (posObj instanceof POS_DW)) {
							POS_DW dwPosMsg = (POS_DW) posObj ;
							
							if (PumpMObjectValidation.validatePumpMObject(dwPosMsg)) {
								LogUtility.getLogger().info("[Pump M] add CustInfo to UPOSMessage before sending it to Sale M") ;
//								uPosMsg.setCustCard_No(dwPosMsg.getCust_card_no()) ;	// 거래처 카드번호
								uPosMsg.setSs_crStNum(dwPosMsg.getCust_code()) ;		// 거래처 번호
//								uPosMsg.setSs_carNum(dwPosMsg.getCar_no()) ;			// 차량 번호
							}
						}
					}
				}
			} catch (Exception e) {
				LogUtility.getLogger().error(e.getMessage(),e) ;
			}			
		} catch (Exception e) {
			LogUtility.getLogger().error(e.getMessage(),e);
		}
	}
}
