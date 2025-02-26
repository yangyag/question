package com.gsc.kixxhub.device.pumpa.translation;

import java.util.Hashtable;
import java.util.Vector;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.P3_1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P5_1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P5_NozzleInfo;
import com.gsc.kixxhub.common.data.pump.PA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S3_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S4_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S5_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S8_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SJ_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.Change;
import com.gsc.kixxhub.device.pumpa.common.Command;
import com.gsc.kixxhub.device.pumpa.common.DataStruct;
import com.gsc.kixxhub.device.pumpa.devDatas.SkDS;
import com.gsc.kixxhub.device.pumpa.devDatas.TsnSelfHSDS;

/**
 * SK PROTOCOL �ڸ� ��
 * LITER = 4.3
 * PRICE = 7
 * 
 * @author dhp
 *
 */
public class TransTsnSelfHS extends Translation {	
	
	/*
	 * GS �����ڵ� -> SK �����ڵ�� ��ȯ
	 */
	public static String getOilCodeForSK (String code) {
		String rtnCode = "";
		
		if(code.equals("0660")) 	 // �ֹ���
			rtnCode="1004";
		else if(code.equals("0610")) // ����ֹ���
			rtnCode="1001";
		else if(code.equals("1206")) // ����
			rtnCode="1036";
		else if(code.equals("1207")) // ��ް���
			rtnCode="1044";
			
		return rtnCode;
	}
	
	private DataStruct HE_ds;	
	
	/*
	 * ���� �ܰ�����
	 */
	public Hashtable<String, String> m_basePriceTbl;
	
	/*
	 * ��������
	 */
	String[][] m_oilInfo = {{"0660", "Kixx"},
						  	{"0610", "KixxPrime"},
						  	{"1206", "Diesel"},
						  	{"1207", "AdvDiesel"}};
	
	
	public TransTsnSelfHS (/*DataStruct he_ds,*/ Hashtable<String, String> basePriceTbl) throws Exception {
				
		super();
		m_basePriceTbl = basePriceTbl;
	}

	/**
	 * WorkingMessage�� ODT �������� ��ȯ�Ѵ�.
	 * 
	 * @param workingMessage 	: WorkingMessage
	 * @return					: ODT ���� 
	 */
	public byte[] generateByteStream(WorkingMessage workingMessage) throws Exception {
		byte[] returnMessage = null;
		
		String workingMessageCommand = workingMessage.getCommand();
		
		// �����ȣ�� ���Ѵ�(ODT ��ȣ�� WorkingMessage�� NozzleNo, 
		// ���� ��ȣ�� WorkingMessage�� ConnectNozzleNo) 
		String connectNozzleNo = workingMessage.getConnectNozzleNo();
		String odtNo = workingMessage.getNozzleNo();	
		
		if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PB)) {
			// WorkingMessage PB : ����/���� ����
			// SK ST         	 : ���� ���� ���� 
			PB_WorkingMessage pbWorkingMessage = (PB_WorkingMessage) workingMessage;
			String commandSet = pbWorkingMessage.getCommandSet();
			String price      = pbWorkingMessage.getPrice();
			String liter      = pbWorkingMessage.getLiter();
			
			DataStruct stDS = new DataStruct();
			stDS.addString("command", "ST", 2);
			
			if (commandSet.equals("0")) {
				// PB CommandSet 0 : ���� ����
				stDS.addString("mode", "A", 1);
				stDS.addString("amount", price.substring(1, 8), 7);
			
			} else if (commandSet.equals("1")) {
				// PB CommandSet 1 : ���� ����
				stDS.addString("mode", "Q", 1);
				stDS.addString("amount", liter, 7);
				
			} else {
				LogUtility.getPumpALogger().error("### PB WorkingMessage " +
												"commandSet ERROR. " +
												"Current commandSet : " +
												commandSet + "###");
			
			}	// end inner if	
			
			byte[] tempArray = stDS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
											pbWorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PA)) {
			// WorkingMessage PA : ���� ������
			// SK RT         	 : ��� ����
			// SK IN         	 : ��� ���� ���� 
			PA_WorkingMessage paWorkingMessage = 
							(PA_WorkingMessage) workingMessage;
			String nozzleState = paWorkingMessage.getNozzleState();
			
			byte[] command = new byte[2];
			
			if (nozzleState.equals("0")) {
				// PA NozzleState 0 : ���� ����
				command[0] 	= 'S';
				command[1] 	= 'C';
	
			} else if(nozzleState.equals("1")) {
				// PA NozzleState 1 : ���� ���� ����
				command[0] 	= 'A';
				command[1] 	= 'P';
				
			} else {
				LogUtility.getPumpALogger().error("### PA WorkingMessage " +
												"nozzleState ERROR. " +
												"Current nozzleState : " +
												nozzleState + " ###");
				command = null;
			
			}	// end inner if
			
			returnMessage = this.makeProtocol(command, 
					paWorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_P3_1)) {
			// WorkingMessage P3_1 	: ������ ȯ������ ���� 
			// SK PC           		: �ܰ� ���� 
			P3_1_WorkingMessage p3WorkingMessage = (P3_1_WorkingMessage) workingMessage;

			DataStruct pcDS = new DataStruct();
			pcDS.addString("command", "PC", 2);
			pcDS.addString("basePrice", 
						p3WorkingMessage.getBasePrice().substring(0, 4), 4);
			
			byte[] tempArray = pcDS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
										p3WorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_P8)) {
			// WorkingMessage P8	: Total Gauge �ڷ� ��û
			// SK CQ 				: Total Gauge ��û
			byte[] command = new byte[2];
			command[0] 	= 'C';
			command[1] 	= 'Q';
			
			returnMessage = this.makeProtocol(command, 
											workingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_SI)) {
			// WorkingMessage SI     : ���������ڷ� ��û	
			// SK CQ 				: Total Gauge ��û
			byte[] command = new byte[2];
			command[0] 	= 'C';
			command[1] 	= 'Q';
			
			returnMessage = this.makeProtocol(command, 
											workingMessage.getNozzleNo());
			
		}
		
		/*******************************************
		 *  ���� ���� ODT �� : generateByteStream ()
		 ******************************************/
		else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_S8)) {
			// WorkingMessage S8 : ������ / ������ ���� ���� 
			S8_WorkingMessage s8WorkingMessage = (S8_WorkingMessage) workingMessage;
			String s8StatusCode = s8WorkingMessage.getStatusCode();
			// �⺻ �� AQ(���) 
			String spStatusCode = "";
			String flag 		= "";
			
//			LogUtility.getPumpALogger().info("++++ getNozzleState=" + s8WorkingMessage.getNozzleState());
//			LogUtility.getPumpALogger().info("++++ s8StatusCode=" + s8WorkingMessage.getStatusCode());
			
			DataStruct SP_ds = new DataStruct();
			
			s8WorkingMessage.print();
						
			if (s8StatusCode.length() > 2) {
				// ���� ���´� status code�� ���ڸ� �� �̴�.
				String nozzleState = s8WorkingMessage.getNozzleState();
				flag = "0";
				
				if (s8StatusCode.equals("651")) {
					// ����ٿ�(���)
					if(nozzleState.equals("0"))
						spStatusCode = "LK";
					else if(nozzleState.equals("2"))
						spStatusCode = "UL";
						
				} else if (s8StatusCode.equals("652")) {
					// ���� ��
					if(nozzleState.equals("1"))
						spStatusCode = "AQ";
					else if(nozzleState.equals("3"))
						spStatusCode = "AQ";
									
				} else if (s8StatusCode.equals("653")) {
					// ���� ��
					spStatusCode = "PP";
						
				} else if (s8StatusCode.equals("654")) {
					// ���� �Ϸ� 
					spStatusCode = "LK";
						
				} 
/*				else if (nozzleState.equals("656")) {
					// ��������(�������)
					ss50StatusCode = "70";
						
				} else if (nozzleState.equals("657")) {
					// ��� ���� 
					ss50StatusCode = "80";
						
				} else if (nozzleState.equals("601")) {
					// ȸ�� �ҷ� 
					ss50StatusCode = "60";
						
				} */
				else {

					if(!s8StatusCode.substring(0,1).equals("2")) // 2xx �� ODT �������������
						LogUtility.getPumpALogger().error("S8_WorkingMessage NozzleState �� Ȯ�� �ʿ�!! " +
								"���� NozzleState �� : " + s8StatusCode);
				}				
			} else {
				// ���� ���´� status code�� ���ڸ� �� �̴�. 
				spStatusCode = s8StatusCode;
				flag = "1";

			}	// end inner if
			
			SP_ds.addString("command", "SP", 2);
			SP_ds.addString("nozzleNo", connectNozzleNo, 2);
			SP_ds.addString("state", spStatusCode, 2);

			byte[] tempArray = SP_ds.getByteStream();
			returnMessage = this.makeProtocol(tempArray, s8WorkingMessage.getNozzleNo());

		} 
		else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_SE)) {
			// �ƹ��͵� ���� ����
		}
		else {			
			LogUtility.getPumpALogger().error("TransTsnSelfHS generateByteStream fail! " + 
					workingMessageCommand + " : �������� �ʴ� ����.");
			
			returnMessage = null;
		
		}	// end if		
		
		
		// �α� ��� 
		if (returnMessage != null) {
			LogUtility.getPumpALogger().debug(
					workingMessageCommand + "WorkingMessage -> " +  
					"[" + new String(returnMessage) + "]"); 
			
		} else {
			LogUtility.getPumpALogger().debug(
					workingMessageCommand + "WorkingMessage -> " + "null");
		}	// end if
		
		return returnMessage;
		
	}	// end generateByteStream
	
	
	
	/**
	 * WorkingMessage�� ODT �������� ��ȯ�Ѵ�. (P5_1 -> YL(��������))
	 * ����ODT ����
	 * @param workingMessage 	: WorkingMessage
	 * @return					: ODT ���� 
	 */
	public byte[][] generateByteStreams(WorkingMessage workingMessage) throws Exception {
		byte[][] returnMessage = new byte[m_oilInfo.length][];
		
		String workingMessageCommand = workingMessage.getCommand();

		// �����ȣ�� ���Ѵ�(ODT ��ȣ�� WorkingMessage�� NozzleNo, 
		// ���� ��ȣ�� WorkingMessage�� ConnectNozzleNo) 
		String connectNozzleNo = workingMessage.getConnectNozzleNo();
		String odtNo = workingMessage.getNozzleNo();	

		// ODT_NO ��ȯ : POS -> ODT
		odtNo = TsnSelfHSDS.getOdtNo_forODT (odtNo);
		
		if (workingMessageCommand.equals(IPumpConstant.COMMANDID_P5_1)) { // YL(��������)
			// WorkingMessage P5_1 : ODT ȯ������ ���� 
			// TsnHSSelf 02         : ȯ�漳�� ����
			P5_1_WorkingMessage p5WorkingMessage = 
							(P5_1_WorkingMessage) workingMessage;
			
			Vector<P5_NozzleInfo> nozzleInfoVector = 
							p5WorkingMessage.getNozzleInfo();
			
			for(int oilCnt=0; oilCnt < m_oilInfo.length; oilCnt++) {

				Vector<String> nozVec = new Vector<String>();
				
				for (int i=0; i < nozzleInfoVector.size(); i++) {
					P5_NozzleInfo nozzleInfo = nozzleInfoVector.get(i);

					String nozzleNo  = nozzleInfo.getNozzleNumber();
					String goodsCode = nozzleInfo.getGoodsCode();
					
					if(m_oilInfo[oilCnt][0].equals(goodsCode))
						nozVec.add(nozzleNo);
					
					// �ܰ�����(1����)
					if(oilCnt==0) {
						m_basePriceTbl.put(nozzleNo, nozzleInfo.getBasePrice());
						//LogUtility.getPumpALogger().info("++++ �ܰ����� oilCnt=" + oilCnt + ", nozzleNo=" + nozzleNo + ", base_price=" + nozzleInfo.getBasePrice());
					}
					
					//LogUtility.getPumpALogger().info("++++ ����ó�� oilCnt=" + oilCnt + ", m_oilInfo=" + m_oilInfo[oilCnt][0] + ", nozzleNo=" + nozzleNo + ", goodsCode=" + goodsCode);
				}

				DataStruct YL_ds = new DataStruct();

				YL_ds.addString("command", "YL", 2);
				YL_ds.addString("oilCode", getOilCodeForSK(m_oilInfo[oilCnt][0]), 4);
				YL_ds.addString("oilName", m_oilInfo[oilCnt][1], 10);
				//YL_ds.addString("reserved", "", 6);			

				for(int i=0; i<nozVec.size(); i++) {
					YL_ds.addString("nozzleNo" + i, nozVec.get(i), 2);
					//LogUtility.getPumpALogger().info("++++ YL_ds.addString=" + nozVec.get(i));
				}

				byte[] tempArray = YL_ds.getByteStream();
				//LogUtility.getPumpALogger().info("++++ tempArray    =" + new String(tempArray));
				//Log.datas(tempArray, tempArray.length, 20);
				
				returnMessage[oilCnt] = this.makeProtocol(tempArray, p5WorkingMessage.getNozzleNo());	
//				LogUtility.getPumpALogger().info("++++ returnMessage=" + new String(returnMessage[oilCnt]));
//				Log.datas(returnMessage[oilCnt], returnMessage[oilCnt].length, 20);
			}
			
		}
		else {
			LogUtility.getPumpALogger().error("TransTsnSelfHS generateByteStreams fail! " + 
					workingMessageCommand + " : �������� �ʴ� ����.");
		}

		// �α� ��� 
		if (returnMessage != null) {
			LogUtility.getPumpALogger().debug(workingMessageCommand + "WorkingMessage -> " +  
					"[" + new String(returnMessage[0]) + "]"); 
			LogUtility.getPumpALogger().debug(workingMessageCommand + "WorkingMessage -> " +  
					"[" + new String(returnMessage[1]) + "]"); 
			LogUtility.getPumpALogger().debug(workingMessageCommand + "WorkingMessage -> " +  
					"[" + new String(returnMessage[2]) + "]"); 
			LogUtility.getPumpALogger().debug(workingMessageCommand + "WorkingMessage -> " +  
					"[" + new String(returnMessage[3]) + "]"); 
		} else {
			LogUtility.getPumpALogger().debug(
					workingMessageCommand + "WorkingMessage -> " + "null");
		}	// end if
		

		return returnMessage;
	}
	
	
	
	/**
	 * ODT ������ WorkingMessage�� ��ȯ�Ѵ�.
	 * 
	 * @param message[] 	: ODT ����. 
	 * @param command		: WorkingMessage Command
	 * @return				: WorkingMessage 
	 */
	public WorkingMessage generateWorkingMessage(byte[] message, String command) throws Exception {
		
		WorkingMessage returnMessage = null;
		
		// byte[]�� ����ڵ带 ��´�
		String thCommandString = this.getCommand(message);
		// byte[]�� �����ȣ�� ��´�
		String nozzleNo        = this.getNozzleNo(message);
		
		if (nozzleNo.charAt(0) == 'S') // S �� �����ϸ� ODT
			nozzleNo = TsnSelfHSDS.getOdtNo_forPOS (nozzleNo); // ODT_NO ��ȯ : ODT -> POS
		
		if (thCommandString.equals(IPumpConstant.SK_COMMAND_AQ)) {
			// SK AQ 				: ���� �� 
			// WorkingMessage S8 	: ������/������ ���� ���� 
			S8_WorkingMessage s8WorkingMessage = new S8_WorkingMessage();
			s8WorkingMessage.setNozzleNo(nozzleNo);
			s8WorkingMessage.setDeviceType("01");
			s8WorkingMessage.setStatus("0");
			s8WorkingMessage.setStatusCode("652");
			s8WorkingMessage.setNozzleState("1");
			
			returnMessage = s8WorkingMessage;
			
		} else if (thCommandString.equals(IPumpConstant.SK_COMMAND_PP)) {
			// SK PP 				: ���� �� 
			// WorkingMessage S3 	: ������/������ ���� ���� 
			S3_WorkingMessage s3WorkingMessage = new S3_WorkingMessage();
			s3WorkingMessage.setNozzleNo(nozzleNo);

			DataStruct ppInterface = 
				SkDS.getDS(IPumpConstant.SK_COMMAND_PP);
			ppInterface.setByteStream(message);
			
			String liter = (String) ppInterface.getValue("liter");
			String price = (String) ppInterface.getValue("price");
			String basePrice = this.calcBasePrice(price, liter);
			s3WorkingMessage.setLiter(liter);
			s3WorkingMessage.setPrice(price.substring(1));
			s3WorkingMessage.setBasePrice(
					GlobalUtility.appending0Pre(basePrice, 4) + "00");
			s3WorkingMessage.setWDate(this.getSystemTime(6));
				
			returnMessage = s3WorkingMessage;
			
		} else if (thCommandString.equals(IPumpConstant.SK_COMMAND_LK)) {
			// SK LK 				: ���� �ٿ� 
			// WorkingMessage S8 	: ������/������ ���� ���� 
			S8_WorkingMessage s8WorkingMessage = new S8_WorkingMessage();
			s8WorkingMessage.setNozzleNo(nozzleNo);
			s8WorkingMessage.setDeviceType("01");
			s8WorkingMessage.setStatus("0");
			s8WorkingMessage.setStatusCode("651");
			s8WorkingMessage.setNozzleState("0");
			
			returnMessage = s8WorkingMessage;
			
		} else if (thCommandString.equals(IPumpConstant.SK_COMMAND_UL)) {
			// SK UL 				: ���� �ٿ� 
			// WorkingMessage S8 	: ������/������ ���� ���� 
			S8_WorkingMessage s8WorkingMessage = new S8_WorkingMessage();
			s8WorkingMessage.setNozzleNo(nozzleNo);
			s8WorkingMessage.setDeviceType("01");
			s8WorkingMessage.setStatus("0");
			s8WorkingMessage.setStatusCode("651");
			s8WorkingMessage.setNozzleState("2");
			
			returnMessage = s8WorkingMessage;
			
		} else if (thCommandString.equals(IPumpConstant.SK_COMMAND_CT)) {
			DataStruct ctInterface = 
				SkDS.getDS(IPumpConstant.SK_COMMAND_CT);
			ctInterface.setByteStream(message);
			
			// SK CT : Total gauge ���� 
			if (command.equals("SJ")) {
				// WorkingMessage SJ : �������� �ڷ� ���� 
				SJ_WorkingMessage sjWorkingMessage = new SJ_WorkingMessage();
				sjWorkingMessage.setNozzleNo(nozzleNo);
				sjWorkingMessage.setTotalGauge(
						(String) ctInterface.getValue("totalGauge"));
				sjWorkingMessage.setSystemTime(this.getSystemTime(12));

				returnMessage = sjWorkingMessage;
				
			} else if (command.equals("S5")){	
				// WorkingMessage S5 : Total Gauge ����
				S5_WorkingMessage s5WorkingMessage = new S5_WorkingMessage();
				s5WorkingMessage.setNozzleNo(nozzleNo);
				
				s5WorkingMessage.setTotalGauge(
						(String) ctInterface.getValue("totalGauge"));
				s5WorkingMessage.setSystemTime(this.getSystemTime(12));
				
				returnMessage = s5WorkingMessage;				
			} 
			else {
				LogUtility.getPumpALogger().error(" ERROR. CT -> " + command);
				
			}	// end inner if

		} 		
		else {
			LogUtility.getPumpALogger().error("TransTsnSelfHS generateWorkingMessage fail! " + 
							thCommandString + " �������� �ʴ� ����.");
			returnMessage = null;
			
		}	// end if
		
		//LogUtility.getPumpALogger().debug(this.printResultLog(message, returnMessage));
		
		return returnMessage;
		
	}	// end generateWorkingMessage
	
	/**
	 * ODT ������ WorkingMessage�� ��ȯ�Ѵ�.
	 * 
	 * @param message[][]	: ODT ����. 
	 * @param command		: WorkingMessage Command
	 * @return				: WorkingMessage 
	 */
	public WorkingMessage generateWorkingMessage(byte[][] message, 
													String command) throws Exception {
		WorkingMessage returnMessage = null;
		
		// SK TR : �����Ϸ� �ڷ����� 
		// SK CT : Total Gauge ���� 
		// WorkingMessage S4 : �����Ϸ� �ڷ�����
		S4_WorkingMessage s4WorkingMessage = new S4_WorkingMessage();
			
		s4WorkingMessage.setFlag(this.generateBlank(1));
		s4WorkingMessage.setWDate(this.getSystemTime(6));
		s4WorkingMessage.setStatusFlag("0");

		for (int i = 0; i < message.length; i++) {
			String thCommandString	= this.getCommand(message[i]);
			String nozzleNo			= this.getNozzleNo(message[i]);
			
			s4WorkingMessage.setNozzleNo(nozzleNo);
				
			if (thCommandString.equals(IPumpConstant.SK_COMMAND_TR)) {
				DataStruct trInterface = 
					SkDS.getDS(IPumpConstant.SK_COMMAND_TR);
				trInterface.setByteStream(message[i]);
				
				s4WorkingMessage.setLiter((String) trInterface.getValue("liter"));
				s4WorkingMessage.setBasePrice(
						(String) trInterface.getValue("basePrice") + "00");
				s4WorkingMessage.setPrice("0" + 
						(String) trInterface.getValue("price"));
				s4WorkingMessage.setSystemTime(this.getSystemTime(12));
					
			} else if (thCommandString.equals(IPumpConstant.SK_COMMAND_CT)) {
				DataStruct ctInterface = 
					SkDS.getDS(IPumpConstant.SK_COMMAND_CT);
				ctInterface.setByteStream(message[i]);
					
				s4WorkingMessage.setTotalGauge(
						(String) ctInterface.getValue("totalGauge"));
					
			} else {
				LogUtility.getPumpALogger().error("TransTsnSelfHS generateWorkingMessage2 fail! " + 
						thCommandString + " : �������� �ʴ� ����.");
			}	// end if		
				
		}	// end for			
			
		returnMessage = s4WorkingMessage;
		
		return returnMessage;
	
	}	// end generateWorkingMessage			
			
	
	/**
	 * SK �������� ��� �ڵ带 ���� 
	 * 
	 * @param message	: SK ����
	 * @return			: ��� �ڵ� 
	 */
	public String getCommand(byte[] message) throws Exception {
		String returnMessage = null;
		
		byte[] commandArray 	= new byte[2];
		commandArray[0] 		= message[4];
		commandArray[1] 		= message[5];
		
		returnMessage = new String(commandArray);
		
		return returnMessage;
	
	}	// end getCommand
	

	
	public DataStruct getHE_ds() {
		return HE_ds;
	}
	

	
	/**
	 * SK �������� �����ȣ�� ����
	 * 
	 * @param message	: SK ���� 
	 * @return			: ���� ��ȣ 
	 */
	private String getNozzleNo(byte[] message) throws Exception {
		String returnMessage = null;
		
		byte[] nozzleNo 	= new byte[2];
		nozzleNo[0]			= message[1];
		nozzleNo[1]     	= message[2];
		
		returnMessage = new String(nozzleNo);
		
		return returnMessage;
		
	}	// end getNozzleNo

	/**
	 * byte �迭�� data��  ������ SK ���� ���·� ��ȯ
	 * 
	 * @param data		: data
	 * @param nozzleNo	: ���� ��ȣ 
	 * @return			: SK ���� 
	 */
	public byte[] makeProtocol(byte[] data, String nozzleNo) throws Exception {
		String devNo = nozzleNo;
		byte[] returnData 		= null;
		byte blank 				= ' ';
		int returnDataCounter 	= 0;
		// SOH, �������ȣ(2), STX, ETX, LRC ��ŭ�� ���̸� ���Ѵ�.
		int arrayLength 		= data.length + 6;
		
		//LogUtility.getPumpALogger().debug("++++ makeProtocol nozzleNo = " + nozzleNo);
		
		if(Change.toValue(nozzleNo) >= 71) {
			//LogUtility.getPumpALogger().debug("++++ makeProtocol nozzleNo = " + nozzleNo + " devNo=" + devNo);
			devNo = TsnSelfHSDS.getOdtNo_forODT (nozzleNo); // ODT_NO ��ȯ : POS -> ODT
		}
		
		returnData = new byte[arrayLength];
		
		returnData[returnDataCounter++] = Command.SOH;					// SOH
		returnData[returnDataCounter++] = (byte) devNo.charAt(0);	// �������ȣ(1)
		returnData[returnDataCounter++] = (byte) devNo.charAt(1);	// �������ȣ(2)
		returnData[returnDataCounter++] = Command.STX;					// STX
		
		for (int i = 0; i < data.length; i++) {
			returnData[returnDataCounter++] = data[i];
			
		}	// end for
		
		returnData[returnDataCounter++] = Command.ETX;					// ETX
		returnData[returnDataCounter++] = blank;						// LRC		
		
		return returnData;
		
	}	// end makeProtocol
	
}