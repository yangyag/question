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
	 * [2008.12.14] ������, ����ö, ������, ����ȣ
	 * 
	 * ���� LED Code �� 1(����) �� �ƴ� 2 (����) Ȥ�� 3(TimeOut) �� ��� Message Type �� �����ϵ��� �Ѵ�.
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
			
			// PI2, 2016-03-18, CWI AS-IS uPOS Message�� FILLER ���� ����� ���������� �°� ����
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
				case IUPOSConstant.MESSAGETYPE_INT_8244 :{ //����carpay/������÷������� �߰� (������ⰳ��������Ʈ)
					// 2016-08-16 twlee 
		        	// selfPayment_Type ���� log����
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
	 * BL üũ ���� ����
	 * @param receiving_pumpa	: WorkingMessage (HE_WorkingMessage or HA_WorkingMessage)
	 * @return
	 */
	public static UPOSMessage create0071UPOSMessageFromWorkingMessage(WorkingMessage receiving_pumpa) {
		UPOSMessage uPosMsg = null ;
		String commandID = receiving_pumpa.getCommand() ;
    	
		LogUtility.getPumpMLogger().info("[Pump M] Create BL Check UPOSMessage") ;
		switch (commandID) {
			case IPumpConstant.COMMANDID_HA : {
				// HA	�Ҹ��� ī����� ���� ��û
				uPosMsg = ODTUtility_SoMo.create0071UPOSMessageFromWorkingMessage_SoMo(receiving_pumpa) ;
				break ;
			}
			case IPumpConstant.COMMANDID_HE : {
				// HE	�پ��� ���� ī�� ���� ���� ��û
				uPosMsg = ODTUtility_DaSNo.create0071UPOSMessageFromWorkingMessage_DaSNo(receiving_pumpa) ;
				break ;
			}
		}		
		return uPosMsg ;
	}
	
	/**
	 * createUPOSMessageFromWorkingMessage �޼ҵ�� ������ ODT Ȥ�� ���� ODT ���� ���� ��û�� u-POS ������
	 * �����ϱ� ���ؼ� ����Ѵ�.
	 * 
	 * @param receiving_pumpa	: Pump A �� ���� ���۹��� ���� ��û ����
	 * @param khproc_no			: KH ó����ȣ
	 * @return
	 */
	public static UPOSMessage createUPOSMessageFromWorkingMessage(WorkingMessage receiving_pumpa, String khproc_no) {
		UPOSMessage uPosMsg = null ;
		String commandID = receiving_pumpa.getCommand() ;
    	
		switch (commandID) {
			case IPumpConstant.COMMANDID_HA : {
				// HA	�Ҹ��� ī����� ���� ��û
				LogUtility.getPumpMLogger().info("[Pump M] �Ҹ��� ī����� ���� ��û������ UPOSMessage Class �� �����մϴ�.") ;
				uPosMsg = ODTUtility_SoMo.createUPOSMessageFromWorkingMessage_SoMo(receiving_pumpa, khproc_no) ;
				break ;
			}
			case IPumpConstant.COMMANDID_HE : {
				// HE	�پ��� ���� ī�� ���� ���� ��û
				LogUtility.getPumpMLogger().info("[Pump M] �پ��� ���� ī�� ���� ���� ��û������ UPOSMessage Class �� �����մϴ�.") ;
				uPosMsg = ODTUtility_DaSNo.createUPOSMessageFromWorkingMessage_DaSNo(receiving_pumpa, khproc_no, false) ;
				break ;
			}
			case IPumpConstant.COMMANDID_SB : {
				// SB	������ ī��  ���� ����/������� ��û
				LogUtility.getPumpMLogger().info("[Pump M] ������ ī��  ���� ����/������� ��û������ UPOSMessage Class �� �����մϴ�.") ;
				uPosMsg = ODTUtility_Recharge.createUPOSMessageFromWorkingMessage_Recharge(receiving_pumpa, khproc_no) ;
				break ;
			}
			case IPumpConstant.COMMANDID_BA : {
				// BA	������ ���ʽ� ���� ���� ��û
				LogUtility.getPumpMLogger().info("[Pump M] ������ ���ʽ� ���� ���� ��û������ UPOSMessage Class �� �����մϴ�.") ;
				uPosMsg = ODTUtility_Recharge.createUPOSMessageFromWorkingMessage_Recharge(receiving_pumpa, khproc_no) ;
				break ;
			}
			case IPumpConstant.COMMANDID_TJ : {
				// TJ	������ ���� ������ ��û
				LogUtility.getPumpMLogger().info("[Pump M] ������ ���� ������ ��û������ UPOSMessage Class �� �����մϴ�.") ;
				uPosMsg = ODTUtility_Recharge.createUPOSMessageFromWorkingMessage_Recharge(receiving_pumpa, khproc_no) ;
				break ;
			}
			case IPumpConstant.COMMANDID_TD : {
				// TD	������ ���ʽ� ī���� ����Ʈ ���� ��ȸ ��û
				LogUtility.getPumpMLogger().info("[Pump M] ������ ���ʽ� ī���� ����Ʈ ���� ��ȸ ��û ������ UPOSMessage Class �� �����մϴ�.") ;
				uPosMsg = ODTUtility_Recharge.createUPOSMessageFromWorkingMessage_Recharge(receiving_pumpa, khproc_no) ;
				
				break ;
			}
		}
		uPosMsg.print();
		return uPosMsg ;
	}


	/**
	 * UPOS ���� ������ �޾Ƽ� WorkingMessage �� ������ �Ѵ�. (���� ��������)
	 * 
	 * as-is, to-be ���� ������ ��� ó���ϵ��� ���� , ������, 2016.04.21
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
			 * 	���� ODT ����� ���, VAN ���� ���� ���� ���� ���� ���Ž�, LED CODE �� ���� ���ΰ� ǥ��
			 * 	��ִ����ý��ۿ����� LED CODE �� �������� �ʰ�, VAN �����ڵ�� ���
			 * 		�̷� ���Ͽ� ���� ODT �� ���� ���� ������ ��ִ����ý��ۿ��� ���Ž�, ���� ó���� �����ϱ� ���� LED CODE �� ������ �Է��ϵ��� ����.
			 * 	���ʽ� ��� �� ���ʽ� �ܵ� ������ ��� loyalty response code �� ����ؾ� ��.
			 * 
			 */
			int messageTypeInt = Integer.parseInt(uPosMsg.getMessageType()) ;
			switch (nozProtocolInt) {
				case IPumpConstant.PUMP_PROTOCOL_SOMO :
				case IPumpConstant.PUMP_PROTOCOL_DaSNo :
				case IPumpConstant.PUMP_PROTOCOL_NewDaSNo : {
					if (uPosMsg != null) {
						if ("0000".equals(uPosMsg.getVan_Res_Code())) {
							uPosMsg.setLed_code("1") ;	// ����
						}else if ("3333".equals(uPosMsg.getVan_Res_Code())) {
							uPosMsg.setLed_code("3") ;	// ȸ������
						}else {
							uPosMsg.setLed_code("2") ;	// �ź�
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
									uPosMsg.setLed_code("1") ;	// ����
								}else if ("3333".equals(uPosMsg.getVan_Res_Code())) {
									uPosMsg.setLed_code("3") ;	// ȸ������
								}else {
									uPosMsg.setLed_code("2") ;	// �ź�
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
				
				case IPumpConstant.PUMP_PROTOCOL_GSC_SELF :{	//PI2, GSC �ű� �������� �߰�, ���� ODT ����, 2015.11.18 - cwi
					workMsgArray = ODTUtility_GSCSelf.createWorkingMessageFromUPOSMessage_GSC(uPosMsg) ;
					break ;
				}
				case IPumpConstant.PUMP_PROTOCOL_NewRecharge : {   //PI2, �ű� ������ODT �������� �߰�, 2016.02.26 ������ 
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
	 * ���� ������ ���� uPos������ Filler��  �����Ѵ�.
	 * @author ksm
	 * @since 2013.07.19
	 * @param messageType
	 * @param uposMsg
	 */
	private static void fillFillerData(int messageType, UPOSMessage uposMsg) {
			
		/**
		 * 2016.03.31 WooChul Jung.
		 * 	�� �̻� ������� �ʴ� �ڵ���.
		 */
		/*if( messageType != IUPOSConstant.MESSAGETYPE_INT_0001 ){
			// Filler4 : ����� ��������(1) + RS + �������� ����(1) + RS + ȭ��Ưȭ�ŷ���ǰ�ڵ�(4)
			// Filler5 : ���ʽ�ī�� Reading ��ü(2) + RS + ��������Ʈ ����(3) + RS + �ſ�ī�� Reading ��ü(2) 
			uposMsg.setFiller4(""+IUPOSConstant.DELIMITER_RS_STRING+""+IUPOSConstant.DELIMITER_RS_STRING+"");
			uposMsg.setFiller5(""+IUPOSConstant.DELIMITER_RS_STRING+""+IUPOSConstant.DELIMITER_RS_STRING+"");
			
			// PI2, 2016-03-18, CWI AS-IS uPOS Message�� FILLER ���� ����� ���������� �°� ����
			uposMsg.setMobilePay_yn("");	  // ����� ��������
			uposMsg.setPromptDiscount_yn(""); // �������� ����
			uposMsg.setCustGoods_code("");	  // ȭ��Ưȭ�ŷ���ǰ�ڵ�
			
			uposMsg.setBonCardReading_type("");    // ���ʽ�ī�� ������ü
			uposMsg.setPartner_code("");		   // ��������Ʈ����
			uposMsg.setCreditCardReading_type(""); // �ſ�ī�帮����ü
		}*/
	}
	
	
    /**
	 * Sale M ���� ������ �� Pre Processing �� �����Ѵ�.
	 * 
	 * @param preambleData	: Preamble Object
	 */
	public static void preProcessingBeforeSendingToSaleM(Preamble preambleData) {
    	try {
			Object obj = preambleData.getPreamble() ;
			if (obj instanceof UPOSMessage) {
				UPOSMessage uposMsg = (UPOSMessage) obj ;
				
				// 1. �������� ���� ���� �������� lastPayment_yn �� �����Ѵ�. �̴� 0001 ������ �ö���� ��쿡�� 1 �� �����Ѵ�.
    			int messageType = Integer.parseInt(uposMsg.getMessageType()) ;
    			switch (messageType) {
    				case IUPOSConstant.MESSAGETYPE_INT_0001 :{		// ���հ�����ó���Ϸ� ��û�� ��쿡��  lastPayment_yn �� 1�� ����
    					Thread.sleep(1000);
    					uposMsg.setLastPayment_yn(IUPOSConstant.LASTPAYMENT_YN_YES) ;
    					break ;
    				}
    				default : {
    					int nozProtocolInt = PumpMUtil.getConnectedRepODTProtocolFromNozzleNo(uposMsg.getNozzle_no()) ;
    					
    					switch (nozProtocolInt) {
							/**
							 * [2008.12.14] ������, ����ȣ, ����ö, ������
							 *	���� ������ ������ ���� Message Type �� �����ϵ��� �Ѵ�. 
							 */ 							
    						case IPumpConstant.PUMP_PROTOCOL_SOMO : 
							case IPumpConstant.PUMP_PROTOCOL_DaSNo : 
							case IPumpConstant.PUMP_PROTOCOL_NewDaSNo :{	 // 2012.09.26 ksm
								changeMessageType(messageType, uposMsg) ;
								
								// 2013.07.19 ksm ���� upos ������ Filler4, Filler5 ����
								fillFillerData(messageType, uposMsg) ;
								
								break ;
							}
    					} 		
    					
    					switch (nozProtocolInt) {
    						// �پ��� �������� ���� ������ ��� ������ ���� ������ lastPayment_yn �� 1 �� �����Ѵ�.
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
    			
				// 2. ���� ������ PumpMODTSaleManager �� �����Ͽ� ���� ��� ��û ������ ���� ��� �̿��ϵ��� �Ѵ�.
				PumpMODTSaleManager.addUPOSMessage(uposMsg.getNozzle_no(), uposMsg) ;

				// 3. �ŷ�ó�� ���� ���� ���� �� ��� �ŷ�ó ������ UPOSMessage ���� ������ �����ϵ��� �Ѵ�.
				String nozzle_no = uposMsg.getNozzle_no() ;
				
				// PI2, CWI, 2016-04-11 
				// �ŷ�ó ī�带 �ε� �� �ʱ�ȭ ��ư�� Ŭ�� �� ��� �ŷ�ó ������ custHash�� ���� ������ �߻� 
				// �Ϲ� �ŷ��� ��� 4201ó�� �� hash���� �ʱ�ȭ ���� �ʴ� ������ �־� �̸� ���� ��.
				// ����ڵ�� GSC Self�� ��� ODT���� �ö�� �����͸��� Pos�� �����ϱ⿡ �Ʒ� ������ ���� ���� ���ϵ��� ������ �д�.
				if ((nozzle_no != null) && (nozzle_no.length() == 2)) {
					int newNozProtocolInt = PumpMUtil.getConnectedRepODTProtocolFromNozzleNo(uposMsg.getNozzle_no()) ;
					// if((newNozProtocolInt != IPumpConstant.PUMP_PROTOCOL_GSC_SELF) && (newNozProtocolInt != IPumpConstant.PUMP_PROTOCOL_NewRecharge)){
					if(newNozProtocolInt != IPumpConstant.PUMP_PROTOCOL_NewRecharge){
						
						POSHeader posObj = PumpMODTSaleManager.getCustPOSPumpM(nozzle_no) ; 
						if ((posObj != null) && (posObj instanceof POS_DW)) {
							POS_DW dwPosMsg = (POS_DW) posObj ;
							
							if (PumpMObjectValidation.validatePumpMObject(dwPosMsg)) {
								LogUtility.getLogger().info("[Pump M] add CustInfo to UPOSMessage before sending it to Sale M") ;
								uposMsg.setCustCard_No(dwPosMsg.getCust_card_no()) ;	// �ŷ�ó ī���ȣ
								uposMsg.setSs_crStNum(dwPosMsg.getCust_code()) ;		// �ŷ�ó ��ȣ
								uposMsg.setSs_carNum(dwPosMsg.getCar_no()) ;			// ���� ��ȣ
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
     * ODT �� ���� ��û�� ���� ���� �������� �Ʒ� ���׿� ���� pre-Processing �� ó���Ѵ�.
     * 		1. ���� ���� , ���� �߻� ����
     * 			���� ������ �̹� �������� ������ ���� ������ �̹� ���������� ���� �߻��Ǵ� ���� �߻� ������ ���Եȴ�.
     * 			������ �������� ��� �Ҷ��� POS �� ������ ������ ���� ������ �̹� �������� ���Եǵ��� �ؾ� �Ѵ�. 
     * 			(CAT �ܸ���� �̿� ���� ó���ϸ�, ���� �����ϰ� ó���ϵ��� �����Ͽ���.)
     * 
     * @param uPosMsg	: ODT �� ���� �߻��� ���� ��û ����
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
					 * 	���� ODT ����� ���, VAN ���� ���� ���� ���� ���� ���Ž�, LED CODE �� ���� ���ΰ� ǥ��
					 * 	��ִ����ý��ۿ����� LED CODE �� �������� �ʰ�, VAN �����ڵ�� ���
					 * 		�̷� ���Ͽ� ���� ODT �� ���� ���� ������ ��ִ����ý��ۿ��� ���Ž�, ���� ó���� �����ϱ� ���� LED CODE �� ������ �Է��ϵ��� ����.
					 * 	���ʽ� ��� �� ���ʽ� �ܵ� ������ ��� loyalty response code �� ����ؾ� ��.
					 * 
					 */
				int messageTypeInt = Integer.parseInt(uPosMsg.getMessageType()) ;
				switch (nozProtocolInt) {
					case IPumpConstant.PUMP_PROTOCOL_SOMO :
					case IPumpConstant.PUMP_PROTOCOL_DaSNo :
					case IPumpConstant.PUMP_PROTOCOL_NewDaSNo : {
						if (uPosMsg != null) {
							if ("0000".equals(uPosMsg.getVan_Res_Code())) {
								uPosMsg.setLed_code("1") ;	// ����
							}else if ("3333".equals(uPosMsg.getVan_Res_Code())) {
								uPosMsg.setLed_code("3") ;	// ȸ������
							}else {
								uPosMsg.setLed_code("2") ;	// �ź�
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
										uPosMsg.setLed_code("1") ;	// ����
									}else if ("3333".equals(uPosMsg.getVan_Res_Code())) {
										uPosMsg.setLed_code("3") ;	// ȸ������
									}else {
										uPosMsg.setLed_code("2") ;	// �ź�
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
						        	// selfPayment_typeHash�� ������ ���� selfPayment_type ���� ���������� ����
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
	 * 0001 ���� ���� ���θ� �����Ѵ�.
	 * 
	 * @param nozzle_no		: ���� ��ȣ
	 * @param khproc_no		: KH ó����ȣ
	 * @param workMsg		: �Ǹ� �Ϸ� ���� (TR_WorkingMessage or SH_WorkingMessage)
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
				case IPumpConstant.PUMP_PROTOCOL_GSC_SELF :{	//PI2, GSC �ű� �������� �߰�, ���� ODT ����, 2016.03.18 - CWI
					rlt = ODTUtility_GSCSelf.shouldSend0001UPOSMessageToSaleM(nozzle_no,khproc_no,workMsg) ;
					break ;
				}
				case IPumpConstant.PUMP_PROTOCOL_NewRecharge :  //2016.03.28 ������
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
	 * UPOSMessage ���� ������ Sale M ���� �������� �Ǵ��Ѵ�.
	 * �Ʒ� ������� Sale M ���� �������� �ʴ´�.
	 * 		1. GS���ʽ�������ȸ ����
	 * 		2. myLG���ʽ�������ȸ ���� - �����ҿ�
	 * 		3. ���ڻ�ǰ�ǰŷ�������ȸ ���� - �����ҿ�
	 * 		4. �� Protocol ���� �ѹ��� ����
	 * @param uPosMsg	: UPOSMessage ���� ����
	 * @return
	 */
    public static boolean shouldSendToSaleM(UPOSMessage uPosMsg) {
    	boolean rlt = true ;
    	
    	if (uPosMsg == null)  return false ;
    	
    	try {
    		int messageTypeInt = Integer.parseInt(uPosMsg.getMessageType()) ;
    		switch (messageTypeInt) {
    			// ������, ���� ������ ������ ������
    			case IUPOSConstant.MESSAGETYPE_INT_0112 :	// GS���ʽ�������ȸ ����
    			case IUPOSConstant.MESSAGETYPE_INT_0114 :	// myLG���ʽ�������ȸ ���� 
    			case IUPOSConstant.MESSAGETYPE_INT_0116 : 	// ���ڻ�ǰ�ǰŷ�������ȸ ���� 
//    			case IUPOSConstant.MESSAGETYPE_INT_0072 :	// �ſ�ī�� Ȯ�� ���� (BL üũ) -> BL ��� �� �ø����� ���� 
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
							case IPumpConstant.PUMP_PROTOCOL_GSC_SELF :{	//PI2, GSC �ű� �������� �߰�, ���� ODT ����, 2016.03.18 - CWI
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
	 * �ŷ�ó ��ȸ ��û�� ���� ���� ���� �������� ��ó���� �ǽ��Ѵ�.
	 * 
	 * @param pumpM_DW		: �ŷ�ó ��ȸ ��û�� ���� ���� ����
	 * @return
	 */
	public static boolean validatePOSPumpM_DW(POS_DW pumpM_DW) {
		boolean rlt = false ;
		
		String status_code_card = pumpM_DW.getStatus_code_card() ; 	// �ŷ� ����		
		String card_code_base = pumpM_DW.getCard_code_base() ;		// ī�� ����

		if (card_code_base.equals(ICode.CARD_CODE_BASE_06)) { // ī�� ���� 06 : �̵�� ī��
			rlt = false;			
		} else {
			if (status_code_card.equals(ICode.STATUS_CODE_CARD_01)) { // �ŷ����� 01 : ����
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
			} else {	// �ŷ����� 02, 03 : ����, ����
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
	 * PI2 2016-03-22 twlee ��ִ������� ���ݿ�����ó���� ��ȣȭ�� ī���ȣ�� ī���ȣ+"=" ó�� 
	 *  
	 * @param cardNumber	:	ī���ȣ
	 * @return
	 */
	public static String getChangeCashReceiptNumber(String cardNumber){
    	if(!GlobalUtility.isNullOrEmptyString(cardNumber)){
    		// 20160902 PI2 twlee ���ݿ�����ī��� ���ݿ����� ��Ͻ� '='�� ���ԵǾ� �ö�� �ߺ����� '=' ���� �ʰ� ó��
    		if(cardNumber.indexOf("=") == -1){
    			cardNumber += "=";
    		}
    	}
		return cardNumber ;
    }
    
    /**
	 * 
	 * PI2 2016-03-22 twlee ��ִ������� ���ʽ�ī�� ���� ī�� ��й�ȣ ��ȣȭ�Ͽ� ����("C"+��ȣȭ�Ⱥ�й�ȣ) 
	 * ��ȣȭ����(P:��,C:��ȣ��)+ ��й�ȣ
	 *  
	 * @param pwd	:	���ʽ�ī�� ��й�ȣ
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
//								uPosMsg.setCustCard_No(dwPosMsg.getCust_card_no()) ;	// �ŷ�ó ī���ȣ
								uPosMsg.setSs_crStNum(dwPosMsg.getCust_code()) ;		// �ŷ�ó ��ȣ
//								uPosMsg.setSs_carNum(dwPosMsg.getCar_no()) ;			// ���� ��ȣ
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
