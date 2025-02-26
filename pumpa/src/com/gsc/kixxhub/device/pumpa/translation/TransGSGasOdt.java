package com.gsc.kixxhub.device.pumpa.translation;

import java.util.Vector;

import com.gsc.kixxhub.common.data.IConstant;
import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.posdata.POSHeader;
import com.gsc.kixxhub.common.data.posdata.POS_HE;
import com.gsc.kixxhub.common.data.posdata.POS_HF;
import com.gsc.kixxhub.common.data.pump.BB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.CP_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.D1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.D2_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.D3_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.D4_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.D5_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.D6_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.D7_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PF_CarInfo;
import com.gsc.kixxhub.common.data.pump.PF_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PG_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PI_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PP_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PQ_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S3_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S5_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S8_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S9_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SF_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SG_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SH_RepInfo;
import com.gsc.kixxhub.common.data.pump.SH_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SJ_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SK_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SL_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.data.pump.XA_WorkingMessage;
import com.gsc.kixxhub.common.data.upos.UPOSMessageUtility;
import com.gsc.kixxhub.common.utility.ByteUtil;
import com.gsc.kixxhub.common.utility.ClearUtil;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.Command;
import com.gsc.kixxhub.device.pumpa.common.DataStruct;
import com.gsc.kixxhub.device.pumpa.devDatas.OdtGSGas;


/**
 * 
 * �������� : 2016.03.28 ������
 * ���� : TCP/IP ��Ź�� ������ ODT��  byte <-> WorkingMessage ��ȯ Ŭ���� 
 *
 * 1. byte -> WorkingMessage
 * 		- T1 -> S8
 * 		- T2 -> T2
 * 		- T3 -> S9
 * 		- SK -> SL
 * 		- T4 -> SH`
 * 		- TC -> SF
 * 		- TB -> SG
 * 		- TA -> SJ
 * 		     -> S5
 * 		- TD -> TD
 * 
 * 
 * 2. WorkingMessage -> byte
 * 		- D1 -> CD
 * 		- D2 -> CE
 * 		- XA -> XA
 * 		- D3 -> CF
 * 		- D4 -> CG
 * 		- D5 -> CH
 * 		- D6 -> CI
 * 		- D7 -> CN
 * 		- PB -> CJ
 * 		- PG -> C2
 * 		- PF -> C3
 * 		- PP -> CO
 * 		- PQ -> CK
 * 		- P9 -> CB
 * 		- P8 -> CC
 * 		- SI -> CC
 * 		- PE -> C1
 * 		- CP -> CP
 * 		- ACK -> ACK
 * 		- NAK -> NAK
 * 
 * 
 * @author GSC
 *
 */
public class TransGSGasOdt extends Translation {
    
	/**
	 * ODT ������ WorkingMessage�� ��ȯ�Ѵ�.
	 * 
	 * @param message	: ODT ����
	 * @param command	: WorkingMessage Command
	 * @return			: WorkingMessage 
	 */
	public WorkingMessage generateWorkingMessage(byte[] message, 
													String command) throws Exception {
		WorkingMessage returnMessage = null;
		boolean printLogFlag = true; 
		
		// byte[]�� �ִ� ����ڵ带 ��´�
		String odtCommandString = this.getCommand(message);
		// byte[]�� ODT��ȣ�� ��´�
		String odtNo 			= this.getOdtNo(message);
		
		if (odtCommandString.equals("T1")) {
			// ODT T1            : ODT ���� ǥ�� 
			// WorkingMessage S8 : ������/������ ���� ����
			S8_WorkingMessage s8WorkingMessage = new S8_WorkingMessage();
			s8WorkingMessage.setNozzleNo(odtNo);
			
			DataStruct t1Interface = OdtGSGas.getDS("T1");
			t1Interface.setByteStream(message);
				
			String status = (String) t1Interface.getValue("screen");
				
			s8WorkingMessage.setDeviceType("04");
			s8WorkingMessage.setStatus("0");
				
			// ODT T1 Status 0 : ������ 
			if (status.equals("0")) {
				printLogFlag = false;
				s8WorkingMessage.setStatusCode("361");
				s8WorkingMessage.setErrMsg("������");
				
			// ODT T1 Status 1 : ������ �̵�� ���� 
			} else if (status.equals("1")) {
				s8WorkingMessage.setStatusCode("362");
				s8WorkingMessage.setErrMsg("������ �̵�� ����");
				
			// ODT T1 Status 2 : RESET ����
			} else if (status.equals("2")) {
				s8WorkingMessage.setStatusCode("363");
				s8WorkingMessage.setErrMsg("RESET ����");
				
			// ODT T1 Status 4 : �Ǹ� ���� 
			} else if (status.equals("4")) {
				s8WorkingMessage.setStatusCode("364");
				s8WorkingMessage.setErrMsg("�Ǹ� ����");
				
			// ODT T1 Status 5 : ����ó���� 
			} else if (status.equals("5")) {
				s8WorkingMessage.setStatusCode("365");
				s8WorkingMessage.setErrMsg("����ó����");
				
			// ODT T1 Status 6 : �μ� ��
			} else if (status.equals("6")) {
				s8WorkingMessage.setStatusCode("366");
				s8WorkingMessage.setErrMsg("�μ� ��");
				
			// ODT T1 Status 8 : �μ���� ���� 
			} else if (status.equals("8")) {
				s8WorkingMessage.setStatus("1");
				s8WorkingMessage.setStatusCode("301");
				s8WorkingMessage.setErrMsg("�μ���� ����");
				
			// ODT T1 Status 9 : ������ ���� �ҷ� 
			} else if (status.equals("9")) {
				s8WorkingMessage.setStatus("1");
				s8WorkingMessage.setStatusCode("302");
				s8WorkingMessage.setErrMsg("������ ���� �ҷ�");
				
			} else {
				LogUtility.getLogger().error("### Incorrect status " +
											 "in T1 ODT. Current status : " +
											 status + " ###") ; 
				
			}	// end inner if
				
			s8WorkingMessage.setErrMsg(this.generateBlank(20));
			s8WorkingMessage.setDetectTime(this.getSystemTime(12));
			s8WorkingMessage.setVersion(this.generateBlank(9));
				
			returnMessage = s8WorkingMessage;
			
		} else if (odtCommandString.equals("T2")) {
			// ODT T2 			 : �����Ǹ� ��Ȳ ����
			// WorkingMessage S3 : ����/���� �� �ڷ�����
			S3_WorkingMessage s3WorkingMessage = new S3_WorkingMessage();
			s3WorkingMessage.setNozzleNo(odtNo);
			
			DataStruct t2Interface = OdtGSGas.getDS("T2");
			t2Interface.setByteStream(message);
				
			s3WorkingMessage.setLiter((String) t2Interface.getValue("liter"));
			s3WorkingMessage.setPrice(
					((String) t2Interface.getValue("price")).substring(2));
			s3WorkingMessage.setWDate(this.getSystemTime(6));	
				
			returnMessage = s3WorkingMessage;
			
		} else if (odtCommandString.equals("T3")) {
			// ODT T3 			 : ��ī�� ���� ��û 
			// WorkingMessage S9 : ��ī�� ���� ��û
			S9_WorkingMessage s9WorkingMessage = new S9_WorkingMessage();
			s9WorkingMessage.setNozzleNo(odtNo);
			
			DataStruct t3Interface = OdtGSGas.getDS("T3");
			t3Interface.setByteStream(message);
				
			s9WorkingMessage.setMode((String) t3Interface.getValue("mode"));
			s9WorkingMessage.setSerialNumber(
					(String) t3Interface.getValue("carNumber"));
				
			returnMessage = s9WorkingMessage;
			
		/**
		 * ������Ʈ : PI2
		 * ���泻�� : �ű�(������ ODT �ſ�, �ſ�+���ʽ�, ���ݿ����� ���� ��û, ķ���������Ϸ� ���� ��û)
		 * �������� : 2015.12.23 ������
		 */
		}  else if (odtCommandString.equals(IPumpConstant.COMMANDID_SK)) {
			//���� ����ó��
			DataStruct skInterface = OdtGSGas.getDS(IPumpConstant.COMMANDID_SK);
			skInterface.setVByteStream_Gas(message, 8);

			SK_WorkingMessage skWorkingMessage =  new SK_WorkingMessage();
			
			skWorkingMessage.setNozzleNo((String)skInterface.getValue("DeviceNo"));
			skWorkingMessage.setCommand((String)skInterface.getValue("Command"));
			skWorkingMessage.setConnectNozzleNo((String)skInterface.getValue("ConnectDevNo"));			
			skWorkingMessage.setMessageType((String)skInterface.getValue("MessageType"));
			skWorkingMessage.setCardNumber((String)skInterface.getValue("CardNumber"));
			skWorkingMessage.setBonusCard((String)skInterface.getValue("BonusCard"));
			skWorkingMessage.setCustCardNo((String)skInterface.getValue("CustCardNo"));
			skWorkingMessage.setCashReceiptNo((String)skInterface.getValue("CashReceiptNo"));
			String amount = (String)skInterface.getValue("Amount");
			String basePrice = (String)skInterface.getValue("BasePrice");	
			String price = (String)skInterface.getValue("Price");
			skWorkingMessage.setLedCode((String)skInterface.getValue("LedCode"));
			skWorkingMessage.setCreatedTime((String)skInterface.getValue("CreatedTime"));
			
			//����, �ܰ� , �ݾ�, �Һΰ����� ���� �Էµ��� ������ ��ŷ�޼����� ���� �������� �ʾ� �ʱⰪ�� �����ϵ��� �Ѵ�.
			
			if(!amount.equals(""))
			{
				skWorkingMessage.setAmount(amount);
			}
			
			if(!basePrice.equals(""))
			{
				skWorkingMessage.setBasePrice(basePrice);
			}
			
			if(!price.equals(""))
			{
				skWorkingMessage.setPrice(price);
			}
			
			//����� ������ upos��ü ����
			byte[] uPosByte = (byte[])skInterface.getValue("UPosByte");
			
			byte[] temp = null;
			
			temp = ByteUtil.removeSOH_ETB(uPosByte);
			ClearUtil.setClearString(uPosByte);
			uPosByte = temp;
			
			// led �ڵ尡 ������ �ſ���ο�û
			if(skWorkingMessage.getLedCode().equals("")) {
				// ���� ������ UPOS Message(HE)�� ��ȯ�Ѵ�. 
				POS_HE pos_he = new POS_HE(uPosByte);
				skWorkingMessage.setUnityMessage(pos_he.getUPosMsg());
				
			} else {
				//���� ������ UPOS Message(HF)�� ��ȯ�Ѵ�. 
				POS_HF pos_hf = new POS_HF(uPosByte);
				skWorkingMessage.setUnityMessage(pos_hf.getUPosMsg());
				
			}/*
			
			LogUtility.getLogger().info(" [pumpA] �������� ��� ���� : " + skWorkingMessage.getUnityMessage().getPromptDiscount_yn());*/
			
			ClearUtil.setClearString(message);
			ClearUtil.setClearString(uPosByte);
			
			returnMessage = skWorkingMessage;
				
		}  else if (odtCommandString.equals("T4")) {
			// ODT T4 			 : �Ǹ� ������ ���� 
			// WorkingMessage SH : ������ ODT ���� �Ǹŵ����� ����
			SH_WorkingMessage shWorkingMessage = new SH_WorkingMessage();
			shWorkingMessage.setNozzleNo(odtNo);
			
			// message[71]�� �������� ���� 
			int repNoI = message[71] - 48;
			
			DataStruct  t4Interface = OdtGSGas.getDS("T4", repNoI);
			t4Interface.setByteStream(message);
				
			shWorkingMessage.setWDate(this.getSystemTime(6));
			shWorkingMessage.setSystemTime(this.getSystemTime(12));	// �ý��۽ð�
			shWorkingMessage.setSerialNumber(
					(String) t4Interface.getValue("cardNumber"));	// ��ī���ȣ
			shWorkingMessage.setBoyNumber(
					(String) t4Interface.getValue("boyNumber"));	// ��������ȣ
			shWorkingMessage.setTotalLiter(
					(String) t4Interface.getValue("totalLiter"));	// ����
			shWorkingMessage.setUp1((String) t4Interface.getValue("up1"));	// ���ܰ� 
			shWorkingMessage.setTotalAMT1(
					(String) t4Interface.getValue("totalAMT1"));	// ���ݾ�
			shWorkingMessage.setUp2((String) t4Interface.getValue("up2"));	// �ǸŴܰ�
			shWorkingMessage.setTotalAMT2(
					(String) t4Interface.getValue("totalAMT2"));	// �Ǹűݾ�
			shWorkingMessage.setTotalGauge(
					(String) t4Interface.getValue("totalGauge"));	// ��Ż������
			shWorkingMessage.setRepNo(
					(String) t4Interface.getValue("repNo"));		// ���� ���� ����
					
			Vector<SH_RepInfo> repInfoVector = new Vector<SH_RepInfo>();
			
			for (int i = 0; i < repNoI; i++) {
				SH_RepInfo repInfo = new SH_RepInfo();
				repInfo.setFlag((String) t4Interface.getValue("flag" + i));		// ��������
				repInfo.setLiter((String) t4Interface.getValue("liter" + i));		// �������
				repInfo.setAmt((String) t4Interface.getValue("amt" + i));			// ���ñݾ�
				// ������ ��ȣ �Ǵ� �Ž�����
				repInfo.setKeepNumber(
						(String) t4Interface.getValue("keepNumber" + i));	
				repInfoVector.add(repInfo);
				
			}	// end for
				
			shWorkingMessage.setRepInfo(repInfoVector);
				
			returnMessage = shWorkingMessage;
				
		} else if (odtCommandString.equals("TC")) {
			// ODT TC 			 : ������ ��ȣ �ο� ��û 
			// WorkingMessage SF : ������ ��ȣ �ο� ��û 
			SF_WorkingMessage sfWorkingMessage = new SF_WorkingMessage();
			sfWorkingMessage.setNozzleNo(odtNo);
				
			// ���� ���� 
				
			returnMessage = sfWorkingMessage;
				
		} else if (odtCommandString.equals("TB")) {
			// ODT TB 			 : ������ ��ȸ 
			// WorkingMessage SG : ������ ��ȸ ��û 
			SG_WorkingMessage sgWorkingMessage = new SG_WorkingMessage();
			sgWorkingMessage.setNozzleNo(odtNo);
			
			DataStruct tbInterface = OdtGSGas.getDS("TB");
			tbInterface.setByteStream(message);
				
			sgWorkingMessage.setKeepNumber(
					(String) tbInterface.getValue("keepNumber"));
				
			returnMessage = sgWorkingMessage;
				
		} else if (odtCommandString.equals("TA")) {
			// ODT TA 			 : ���а����� ����
			// WorkingMessage SJ : �������� �ڷ� ���� 
			// WorkingMessage S5 : ���а����� ����
			
			if ("SJ".equals(command)) {
				SJ_WorkingMessage sjWorkingMessage = new SJ_WorkingMessage();
				sjWorkingMessage.setNozzleNo(odtNo);
				
				DataStruct taInterface = OdtGSGas.getDS("TA");
				taInterface.setByteStream(message);

				sjWorkingMessage.setTotalGauge((String) taInterface
											.getValue("totalGauge"));
				sjWorkingMessage.setSystemTime(this.getSystemTime(12));

				returnMessage = sjWorkingMessage;
				
			} else {
				S5_WorkingMessage s5WorkingMessage = new S5_WorkingMessage();
				s5WorkingMessage.setNozzleNo(odtNo);

				DataStruct taInterface = OdtGSGas.getDS("TA");
				taInterface.setByteStream(message);

				s5WorkingMessage.setTotalGauge((String) taInterface
											.getValue("totalGauge"));
				s5WorkingMessage.setSystemTime(this.getSystemTime(12));

				returnMessage = s5WorkingMessage;
				
			}	// end inner if
			
		} else {
			LogUtility.getLogger().error("### Not Supported command(" + odtCommandString + ") in TransGSGasOdt ###");
			returnMessage = null;
			
		}	// end if
		
		// �α� ��� �� ETX ���Ĵ� ����
		int printFinishIndex = message.length;
		
		for (int i = 0; i < printFinishIndex; i++) {
			if (message[i] == IPumpConstant.DELIMITER_0X03) {
				printFinishIndex = i;
				break;
			}	// end if
		}	// end for
		
		// ETX, SUM ��� 
		printFinishIndex = printFinishIndex + 2;
		
		/*
		// �α� ��� 
		if (printLogFlag) {
			if (returnMessage != null) {
				LogUtility.getLogger().debug(
						"[" + new String(message, 0, printFinishIndex) + "]>>" + 
												returnMessage.getCommand()); 
				
			} else {
				LogUtility.getLogger().debug(
						"[" + new String(message) + "]>>" + "NULL");
				
			}	// end inner if
			
		}	// end if
		*/
		returnMessage.setConnectNozzleNo(odtNo);
		
		return returnMessage;
		
	}	// end generateWorkingMessage
	
	
	
	/**
	 * WorkingMessage�� ODT �������� ��ȯ�Ѵ�.
	 * 
	 * @param workingMessage 	: WorkingMessage
	 * @return					: ODT ���� 
	 */
	public byte[] generateByteStream(WorkingMessage workingMessage) throws Exception {
		byte[] returnMessage = null;
		
		String workingMessageCommand = workingMessage.getCommand();
		
		
		if (workingMessageCommand.equals(IPumpConstant.COMMANDID_D1)) {
			// WorkingMessage D1 : ������ ���� ���� 
			// ODT CD            : ������ ���� ���� 
			D1_WorkingMessage d1WorkingMessage = 
						(D1_WorkingMessage) workingMessage;
			
			DataStruct d1DS = new DataStruct();
			d1DS.addString("command", "CD", 2);
			d1DS.addString("storeName", d1WorkingMessage.getStoreName(), 40);
			d1DS.addString("regiNum", d1WorkingMessage.getStoreRegiNum(), 12);
			d1DS.addString("repNM", d1WorkingMessage.getRepName(), 30);
			d1DS.addString("addr", d1WorkingMessage.getStoreADDR(), 50);
			d1DS.addString("tel", d1WorkingMessage.getTel(), 16);
			d1DS.addString("goodsType", d1WorkingMessage.getGoodsType(), 30);
			d1DS.addString("basePrice", d1WorkingMessage.getBasePrice(), 6);
			d1DS.addString("storeCode", d1WorkingMessage.getStoreCode(), 10);
			
			byte[] tempArray = d1DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
							d1WorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_D2)) {
			// WorkingMessage D2 : �⺻������ �μ⳻�� 
			// ODT CE            : �⺻������ �μ⳻�� 
			D2_WorkingMessage d2WorkingMessage = (D2_WorkingMessage) workingMessage;
			
			DataStruct d2DS = new DataStruct();
			d2DS.addString("command", "CE", 2);
			d2DS.addString("head", d2WorkingMessage.getBaseHeadTitle(), 42);
			d2DS.addString("tail1", d2WorkingMessage.getBaseFootTitle1(), 42);
			d2DS.addString("tail2", d2WorkingMessage.getBaseFootTitle2(), 42);
			
			byte[] tempArray = d2DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
							d2WorkingMessage.getNozzleNo());

		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_XA)) {
			// WorkingMessage XA : ���� ������ ���� 
			// ODT XA            : ���� ������ ���� 
			XA_WorkingMessage xaWorkingMessage = 
							(XA_WorkingMessage) workingMessage;
			
			String printContent = xaWorkingMessage.getPrintContent();
			
			DataStruct xaDS = new DataStruct();
			xaDS.addString("command", "XA", 2);
			xaDS.addString("successBP", xaWorkingMessage.getSuccessBP(), 1);
			xaDS.addString("odtContent", setOdtLCD16Bytes(xaWorkingMessage.getOdtContent()), 64);
			xaDS.addString("printContent", printContent, printContent.getBytes().length);
			
			byte[] tempArray = xaDS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
							xaWorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_D3)) {
			// WorkingMessage D3 : ������ �μ⳻�� 
			// ODT CF            : ������ �μ⳻�� 
			D3_WorkingMessage d3WorkingMessage = 
						(D3_WorkingMessage) workingMessage;
			
			DataStruct d3DS = new DataStruct();
			d3DS.addString("command", "CF", 2);
			d3DS.addString("head", d3WorkingMessage.getSaveHeadTitle(), 42);
			d3DS.addString("tail1", d3WorkingMessage.getSaveFootTitle1(), 42);
			d3DS.addString("tail2", d3WorkingMessage.getSaveFootTitle2(), 42);
			
			byte[] tempArray = d3DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
						d3WorkingMessage.getNozzleNo());

		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_D4)) {
			// WorkingMessage D4 : ��ī�� �μ⳻��
			// ODT CG			 : ��ī�� �μ⳻��
			D4_WorkingMessage d4WorkingMessage = 
						(D4_WorkingMessage) workingMessage;
			
			DataStruct d4DS = new DataStruct();
			d4DS.addString("command", "CG", 2);
			d4DS.addString("head", d4WorkingMessage.getCcHeadTitle(), 42);
			d4DS.addString("tail1", d4WorkingMessage.getCcFootTitle1(), 42);
			d4DS.addString("tail2", d4WorkingMessage.getCcFootTitle2(), 42);
			
			byte[] tempArray = d4DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
						d4WorkingMessage.getNozzleNo());

		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_D5)) {
			// WorkingMessage D5 : ���ʽ�ī�� �μ⳻��
			// ODT CH			 : ���ʽ�ī�� �μ⳻��
			D5_WorkingMessage d5WorkingMessage = 
						(D5_WorkingMessage) workingMessage;
			
			DataStruct d5DS = new DataStruct();
			d5DS.addString("command", "CH", 2);
			d5DS.addString("head", d5WorkingMessage.getBcHeadTitle(), 42);
			d5DS.addString("tail1", d5WorkingMessage.getBcFootTitle1(), 42);
			d5DS.addString("tail2", d5WorkingMessage.getBcFootTitle2(), 42);
			
			byte[] tempArray = d5DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
						d5WorkingMessage.getNozzleNo());

		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_D6)) {
			// WorkingMessage D6 : ������� �μ⳻�� 
			// ODT CI			 : ������� �μ⳻��
			D6_WorkingMessage d6WorkingMessage = 
						(D6_WorkingMessage) workingMessage;
			
			DataStruct d6DS = new DataStruct();
			d6DS.addString("command", "CI", 2);
			d6DS.addString("head", d6WorkingMessage.getSpiHeadTitle(), 42);
			d6DS.addString("tail1", d6WorkingMessage.getSpiFootTitle1(), 42);
			d6DS.addString("tail2", d6WorkingMessage.getSpiFootTitle2(), 42);
			
			byte[] tempArray = d6DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
						d6WorkingMessage.getNozzleNo());

		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_D7)) {
			// WorkingMessage D7 : ������ ODT POS ȯ������
			// ODT CN 			 : ������ ODT POS ȯ������
			D7_WorkingMessage d7WorkingMessage = 
						(D7_WorkingMessage) workingMessage;
			
			DataStruct d7DS = new DataStruct();
			d7DS.addString("command", "CN", 2);
			d7DS.addString("receiptMin", 
					d7WorkingMessage.getReceiptMinLiter(), 5);
			d7DS.addString("depositMin", 
					d7WorkingMessage.getDepsitMinLiter(), 5);
			d7DS.addString("minSale", 
					d7WorkingMessage.getMinSaleLiter(), 5);
			d7DS.addString("loanRemain", 
					d7WorkingMessage.getLoanRemainDisp(), 1);
			d7DS.addString("termWait", 
					d7WorkingMessage.getTremWaitSec(), 2);
			d7DS.addString("emergStop", 
					d7WorkingMessage.getEmergStopLiter(), 3);
			
			byte[] tempArray = d7DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
					d7WorkingMessage.getNozzleNo());

		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PB)) {
			// WorkingMessage PB : ����/���� ����
			// ODT CJ 			 : ����/���� ���� 
			PB_WorkingMessage pbWorkingMessage = 
						(PB_WorkingMessage) workingMessage;
			
			String mode 	= pbWorkingMessage.getCommandSet();
			String price 	= pbWorkingMessage.getPrice();
			
			DataStruct cjDS = new DataStruct();
			cjDS.addString("command", "CJ", 2);
			cjDS.addString("mode", mode, 1);
			
			// PB mode 0 : ���� ����
			if (mode.equals("0")) {
				cjDS.addString("liter", pbWorkingMessage.getLiter(), 7);
				
			// PB mode 1 : ���� ����
			} else if (mode.equals("1")){
				cjDS.addString("amt", price.substring(1), 7);
				
			} else {
				LogUtility.getLogger().error("### Incorrect mode " +
											 "in odt 'PB'. Current mode : " +
											 mode + " ###") ;
			
			}	// end inner if
			
			byte[] tempArray = cjDS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
					pbWorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PG)) {
			// WorkingMessage PG : ��ī�� ���� ���� 
			// ODT C2			 : ��ī�� ���� �� �Ǹ� ���� ���� ���� 
			PG_WorkingMessage pgWorkingMessage = 
								(PG_WorkingMessage) workingMessage;
			
			DataStruct c2DS = new DataStruct();
			c2DS.addString("command", "C2", 2);
			c2DS.addString("cardNo", pgWorkingMessage.getSerialNumber(), 18);
			c2DS.addString("carNo", pgWorkingMessage.getCarNumber(), 18);
			c2DS.addString("driverName", pgWorkingMessage.getDriverName(), 30);
			c2DS.addString("liter", pgWorkingMessage.getTotalLiter(), 7);
			c2DS.addString("basePricve", pgWorkingMessage.getUp(), 6);
			c2DS.addString("jpLiter", pgWorkingMessage.getJpLiter(), 7);
			c2DS.addString("transType", pgWorkingMessage.getTransType(), 1);
			c2DS.addString("cusType", pgWorkingMessage.getCusType(), 1);
			c2DS.addString("transStatus", pgWorkingMessage.getTransStatus(), 1);
			c2DS.addString("printBase", pgWorkingMessage.getPrintBase(), 1);
			c2DS.addString("depositST", pgWorkingMessage.getDepositST(), 1);
			c2DS.addString("floatTR", pgWorkingMessage.getFloatTR(), 1);
			c2DS.addString("receiptType", pgWorkingMessage.getReceiptType(), 1);
			c2DS.addString("monthLimit", pgWorkingMessage.getMonthLimit(), 10);
			c2DS.addString("saveLimit", pgWorkingMessage.getSaveLimit(), 10);
			c2DS.addString("limitType", pgWorkingMessage.getLimitType(), 1);
			c2DS.addString("cusNo", pgWorkingMessage.getCusNumber(), 6);     // PI2, �ŷ�ó ��ȣ �߰� ������ 2016.04.18
			LogUtility.getLogger().info(" �ŷ�ó��ȣ:" + pgWorkingMessage.getCusNumber());  // PI2, �ŷ�ó ��ȣ �߰� ������ 2016.04.18
		/*	
			
			LogUtility.getLogger().info(" command:" + "C2");
			LogUtility.getLogger().info(" ī���ȣ:" + pgWorkingMessage.getSerialNumber());
			LogUtility.getLogger().info(" �ŷ�ó��ȣ:" + pgWorkingMessage.getCusNumber());
			LogUtility.getLogger().info(" ������ȣ:" + pgWorkingMessage.getCarNumber());
			LogUtility.getLogger().info(" �� �̸�:" + pgWorkingMessage.getDriverName());
			LogUtility.getLogger().info(" ����:" + pgWorkingMessage.getTotalLiter());
			LogUtility.getLogger().info(" �ܰ�:" + pgWorkingMessage.getUp());
			LogUtility.getLogger().info(" ��ǥ��:" + pgWorkingMessage.getJpLiter());
			LogUtility.getLogger().info(" �ŷ�����:" + pgWorkingMessage.getTransType());
			LogUtility.getLogger().info(" ������:" + pgWorkingMessage.getCusType());
			LogUtility.getLogger().info(" �ŷ�����:" +pgWorkingMessage.getTransStatus());
			LogUtility.getLogger().info(" �ܰ���¿���:" + pgWorkingMessage.getPrintBase());
			LogUtility.getLogger().info(" ���������࿩��:" + pgWorkingMessage.getDepositST());
			LogUtility.getLogger().info(" �Ҽ���ó�����:" + pgWorkingMessage.getFloatTR());
			LogUtility.getLogger().info(" ��꼭 �ŷ� ����:" + pgWorkingMessage.getReceiptType());
			LogUtility.getLogger().info(" �� �ѵ�����:" + pgWorkingMessage.getMonthLimit());
			LogUtility.getLogger().info(" ������뷮:" + pgWorkingMessage.getSaveLimit());
			LogUtility.getLogger().info(" �ѵ�����:" + pgWorkingMessage.getLimitType());
			*/
			
			byte[] tempArray = c2DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
								pgWorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PF)) {
			// WorkingMessage PF : �ߺ����� ���� 
			// ODT X3            : �ߺ� ������ȣ ���� 
			PF_WorkingMessage pfWorkingMessage = 
				(PF_WorkingMessage) workingMessage;
			
			// �ߺ� �ݺ� ���� �� 
			String repNo 					 = pfWorkingMessage.getRepNO();
			int repNoI 						 = Integer.parseInt(repNo);
			Vector<PF_CarInfo> carInfoVector = pfWorkingMessage.getCarInfoVector();
	
			DataStruct x3DS = new DataStruct();
			x3DS.addString("command", "X3", 2);
			x3DS.addString("repNo", repNo, 2);
//			c3DS.addString("repNo", 
//					GlobalUtility.appendingSPACEPre(String.valueOf(repNoI), 2), 2);
			
			for (int i = 0; i < repNoI; i++) {
				PF_CarInfo carInfo = carInfoVector.get(i);
				
				x3DS.addString("carNo"+i, carInfo.getCarNumber(), 12);
				x3DS.addString("cardNo"+i, carInfo.getSerialNumber(), 16);
				
				String driverName = "";
				if ( !GlobalUtility.isNullOrEmptyString(carInfo.getName())) {
					if (carInfo.getName().getBytes().length > 10) {
						byte[] aaaa = new byte[10];
						System.arraycopy(carInfo.getName().getBytes(), 0, aaaa, 0, 10);
						driverName = new String(aaaa);
					} else 
						driverName = carInfo.getName();
				}
				x3DS.addString("driverName"+i, driverName, 10);
				
//				// ���� �ߺ����� �����Ͱ� �����ϸ� 1, ������ �����ʹ� 0
//				if (i < repNoI - 1) {
//					c3DS.addString("dataFinish"+i, "1", 1);
//					
//				} else {
//					c3DS.addString("dataFinish"+i, "0", 1);
//
//				}	// end if
				
			}	// end for
			
			byte[] tempArray = x3DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
								pfWorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_SL)) {
//			 ��������
			SL_WorkingMessage sl_WorkingMessage = (SL_WorkingMessage) workingMessage;
			
			DataStruct slDS = new DataStruct();
			slDS.addString("Command", workingMessageCommand, 2);
			slDS.addString("ConnectDevNo", sl_WorkingMessage.getConnectNozzleNo(), 2);
			slDS.addVString("MessageType", sl_WorkingMessage.getMessageType(), 4);

//			UPOSMessage ����� �����δ�.
			POSHeader posHeader = new POSHeader(IConstant.POSPROTOCOL_TYPE_KH,
							    				IConstant.POSPROTOCOL_TYPE_ODT,
							    				IConstant.POSPROTOCOL_COMMANDID_HF,
							    				IConstant.POSPROTOCOL_TYPE_KH,
							    				GlobalUtility.appending0Pre(sl_WorkingMessage.getConnectNozzleNo(), 4),
							    				GlobalUtility.getDateYYYYMMDDHHMMSS(),
							    				GlobalUtility.getUniqueMessageID());
			
			
			byte[] bodyUpos = UPOSMessageUtility.createUPOSByteArray(sl_WorkingMessage.getUnityMessage());
			
//			upos header + body ���� 
			byte[] preamble = posHeader.mergeHeaderBody(posHeader.convertHeaderToPOSContentWithoutDataLength(), bodyUpos);
//			header ó���� SOH(0x01)�� body ���� ETB(0x17)�� ���δ�.
    		preamble = UPOSMessageUtility.getByteWithSOH_ETB(preamble);
    		
//    		header�κ��� data length�� CRC ���� ���� �� ���̸� ���� 
    		byte[] dataLen= new byte[]{preamble[34], preamble[35], preamble[36], preamble[37]};
    		String len = Integer.toString(Integer.valueOf(new String(dataLen))-4);
    		byte[] tempDataLen = GlobalUtility.appending0Pre(len, 4).getBytes();
    		
//    		header�κ��� data length ���� 
    		for(int i=0; i<tempDataLen.length; i++)
    		{
    			preamble[34+i] = tempDataLen[i];
    		}
    		
    		byte[] tempSlData = slDS.getByteStream();
//    		tempGbData : upos�� ������ SL����,  preamble : ����� ������ �������� 
			byte[] slData = new byte[tempSlData.length + preamble.length + 1];
			
			// slData �迭�� tempSlData ���� �����Ѵ�.
			System.arraycopy(tempSlData, 0, slData, 0, tempSlData.length);
			// tempSlData�� ������ slData �迭�� unityMessage �� �����δ�.
			System.arraycopy(preamble, 0, slData, tempSlData.length, preamble.length);
//		  	0x1C(FS)�� ���ٿ��� ������ �����Ѵ�.
			slData[slData.length-1] = (byte)0x1C;
			
			returnMessage = slData;
			returnMessage = this.makeProtocol(slData, 
					sl_WorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PI)) {
			// WorkingMessage PI : �ſ�ī�� ����ó�� ����
			// ODT C6            : �ſ�ī�� ��� ���� 
			// ODT C8			 : ���ڻ�ǰ�� ��� ����
			// ODT C9			 : ���ʽ�ī�� ����Ʈ ���� ��� ����  
			PI_WorkingMessage piWorkingMessage = 
							(PI_WorkingMessage) workingMessage;
			
			String cardType 	= piWorkingMessage.getCardType();
			String printContent = piWorkingMessage.getPrintContent();
			byte[] tempArray 	= null;
			
			// PI CardType 0 : �ſ�ī�� 
			if (cardType.equals("0")) {
				
				DataStruct c6DS = new DataStruct();
				c6DS.addString("command", "C6", 2);
				c6DS.addString("result", piWorkingMessage.getMode(), 1);
				c6DS.addString("recogDate", 
						piWorkingMessage.getRecogDate(), 14);
				c6DS.addString("recogNumber", 
						piWorkingMessage.getRecogNumber(), 12);
				c6DS.addString("cardNumber", 
						piWorkingMessage.getCardNumber(), 16);
				c6DS.addString("cardCorpName", 
						piWorkingMessage.getCardCorpName(), 20);
				c6DS.addString("francNumber", 
						piWorkingMessage.getFrancNumber(), 16);
				c6DS.addString("noteCorpCode", 
						piWorkingMessage.getNoteCorpCode(), 3);
				c6DS.addString("noteCorpName", 
						piWorkingMessage.getNoteCorpName(), 20);
				c6DS.addString("terminalNumber", 
						piWorkingMessage.getTerminalNubmer(), 10);
				c6DS.addString("noteNumberTemp", 
						piWorkingMessage.getNoteNumberTemp(), 5);
				c6DS.addString("notice", 
						setOdtLCD16Bytes(piWorkingMessage.getNotice()), 64);
				c6DS.addString("noteNumber", 
						piWorkingMessage.getNoteNumber(), 10);
				c6DS.addString("recogConfid", 
						piWorkingMessage.getRecogConfid(), 5);
				c6DS.addString("printContent", 
						printContent, printContent.getBytes().length);
				
				tempArray = c6DS.getByteStream();
				
			// PI CardType 1 : ���ڻ�ǰ��  
			} else if (cardType.equals("1")) {
				DataStruct c8DS = new DataStruct();
				c8DS.addString("command", "C8", 2);
				c8DS.addString("result", piWorkingMessage.getMode(), 1);
				c8DS.addString("recogDate", 
						piWorkingMessage.getRecogDate(), 14);
				c8DS.addString("recogNumber", 
						piWorkingMessage.getRecogNumber(), 12);
				c8DS.addString("cardNumber", 
						piWorkingMessage.getCardNumber(), 16);
				c8DS.addString("cardCorpName", 
						piWorkingMessage.getCardCorpName(), 20);
				c8DS.addString("francNumber", 
						piWorkingMessage.getFrancNumber(), 16);
				c8DS.addString("noteCorpCode", 
						piWorkingMessage.getNoteCorpCode(), 3);
				c8DS.addString("noteCorpName", 
						piWorkingMessage.getNoteCorpName(), 20);
				c8DS.addString("terminalNumber", 
						piWorkingMessage.getTerminalNubmer(), 10);
				c8DS.addString("noteNumberTemp", 
						piWorkingMessage.getNoteNumberTemp(), 5);
				c8DS.addString("notice", 
						setOdtLCD16Bytes(piWorkingMessage.getNotice()), 64);
				c8DS.addString("noteNumber", 
						piWorkingMessage.getNoteNumber(), 10);
				c8DS.addString("recogConfid", 
						piWorkingMessage.getRecogConfid(), 5);
				c8DS.addString("printContent", 
						printContent, printContent.getBytes().length);
				
				tempArray = c8DS.getByteStream();
				
			// PI CardType 3 : GS ���ʽ�ī�� ���� ���
			} else if (cardType.equals("3")) {
				DataStruct c9DS = new DataStruct();
				c9DS.addString("command", "C9", 2);
				c9DS.addString("result", piWorkingMessage.getMode(), 1);
				c9DS.addString("recogDate", 
						piWorkingMessage.getRecogDate(), 14);
				c9DS.addString("recogNumber", 
						piWorkingMessage.getRecogNumber(), 12);
				c9DS.addString("cardNumber", 
						piWorkingMessage.getCardNumber(), 16);
				c9DS.addString("cardCorpName", 
						piWorkingMessage.getCardCorpName(), 20);
				c9DS.addString("francNumber", 
						piWorkingMessage.getFrancNumber(), 16);
				c9DS.addString("noteCorpCode", 
						piWorkingMessage.getNoteCorpCode(), 3);
				c9DS.addString("noteCorpName", 
						piWorkingMessage.getNoteCorpName(), 20);
				c9DS.addString("terminalNumber", 
						piWorkingMessage.getTerminalNubmer(), 10);
				c9DS.addString("noteNumberTemp", 
						piWorkingMessage.getNoteNumberTemp(), 5);
				c9DS.addString("notice", 
						setOdtLCD16Bytes(piWorkingMessage.getNotice()), 64);
				c9DS.addString("recogType", "1", 1);
				c9DS.addString("noteNumber", 
						piWorkingMessage.getNoteNumber(), 10);
				c9DS.addString("recogConfid", 
						piWorkingMessage.getRecogConfid(), 5);
				c9DS.addString("printContent", 
						printContent, printContent.getBytes().length);
				
				tempArray = c9DS.getByteStream();
			}
				
			//2012.07.09 ksm  my���� ����Ʈ ��� ����
			// ������ ������ ���ؿ� ��� CSR ��û ( 2012-07-06 )
			// PI CardType 4 : myLG �������
			/* else if (cardType.equals("4")) {
				DataStruct c9DS = new DataStruct();
				c9DS.addString("command", "C9", 2);
				c9DS.addString("result", piWorkingMessage.getMode(), 1);
				c9DS.addString("recogDate", 
						piWorkingMessage.getRecogDate(), 14);
				c9DS.addString("recogNumber", 
						piWorkingMessage.getRecogNumber(), 12);
				c9DS.addString("cardNumber", 
						piWorkingMessage.getCardNumber(), 16);
				c9DS.addString("cardCorpName", 
						piWorkingMessage.getCardCorpName(), 20);
				c9DS.addString("francNumber", 
						piWorkingMessage.getFrancNumber(), 16);
				c9DS.addString("noteCorpCode", 
						piWorkingMessage.getNoteCorpCode(), 3);
				c9DS.addString("noteCorpName", 
						piWorkingMessage.getNoteCorpName(), 20);
				c9DS.addString("terminalNumber", 
						piWorkingMessage.getTerminalNubmer(), 10);
				c9DS.addString("noteNumberTemp", 
						piWorkingMessage.getNoteNumberTemp(), 5);
				c9DS.addString("notice", setOdtLCD16Bytes(piWorkingMessage.getNotice()), 64);
				c9DS.addString("recogType", "2", 1);
				c9DS.addString("noteNumber", 
						piWorkingMessage.getNoteNumber(), 10);
				c9DS.addString("recogConfid", 
						piWorkingMessage.getRecogConfid(), 5);
				c9DS.addString("printContent", 
						printContent, printContent.getBytes().length);
				
				tempArray = c9DS.getByteStream();
					
			}*/ 
			else {
				LogUtility.getLogger().error("### Not Supported cardType " +
												 "in PI WorkingMessage. " +
												 "Current cardType : " +
												 cardType + " ###");
		
			}	// end inner if

			returnMessage = this.makeProtocol(tempArray, 
							piWorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_BB)) {
			// WorkingMessage BB : ���ʽ� ���� ���� ����
			// ODT BB			 : ���ʽ� ���� ���� / ���� ������ ���� ��� 
			BB_WorkingMessage bbWorkingMessage = 
						(BB_WorkingMessage) workingMessage;

			String printContent = 
					this.changeNewLine(bbWorkingMessage.getPrintContent());
			
			DataStruct bbDS = new DataStruct();
			bbDS.addString("command", "BB", 2);
			bbDS.addString("success", bbWorkingMessage.getSuccessBP(), 1);
			bbDS.addString("display", setOdtLCD16Bytes(bbWorkingMessage.getOdtContent()), 64);
			bbDS.addString("printContent", printContent, printContent.getBytes().length);
			
			byte[] tempArray = bbDS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
							bbWorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PP)) {
			// WorkingMessage PP : ���� ������ ��ȣ ����
			// ODT CO			 : ������ �߱޹�ȣ �ο� 
			PP_WorkingMessage ppWorkingMessage = 
						(PP_WorkingMessage) workingMessage;
			
			DataStruct coDS = new DataStruct();
			coDS.addString("command", "CO", 2);
			coDS.addString("depositNumber", 
						ppWorkingMessage.getKeepNumber(), 10);
			
			byte[] tempArray = coDS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
							ppWorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PQ)) {
			// WorkingMessage PQ : ������ ����
			// ODT CK			 : ������ ���� 
			PQ_WorkingMessage pqWorkingMessage = 
						(PQ_WorkingMessage) workingMessage;
			
			DataStruct ckDS = new DataStruct();
			ckDS.addString("command", "CK", 2);
			ckDS.addString("mode", pqWorkingMessage.getMode(), 1);
			ckDS.addString("liter", pqWorkingMessage.getLiter(), 7);
			ckDS.addString("basePrice", pqWorkingMessage.getUp(), 6);
			
			byte[] tempArray = ckDS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
							pqWorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_P9)) {
			// WorkingMessage P9 : �������� ó��
			// ODT CB			 : ODT ���� 
			DataStruct cbDS = new DataStruct();
			cbDS.addString("command", "CB", 2);
			
			byte[] tempArray = cbDS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
							workingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_P8)) {
			// WorkingMessage P8 : ���а����� �ڷ� ��û 
			// ODT CC			 : ��Ż������ ��û 
			DataStruct ccDS = new DataStruct();
			ccDS.addString("command", "CC", 2);
			
			byte[] tempArray = ccDS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
							workingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_SI)) {
			// WorkingMessage SI : ���������ڷ� ��û
			// ODT CC			 : ��Ż������ ��û
			DataStruct ccDS = new DataStruct();
			ccDS.addString("command", "CC", 2);
			
			byte[] tempArray = ccDS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
								workingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PE)) {
			// WorkingMessage PE : ������/������ ���� ��û
			// ODT C1			 : ODT ȣ�� 
			DataStruct c1DS = new DataStruct();
			c1DS.addString("command", "C1", 2);
			c1DS.addString("timeInfo", this.getSystemTime(12), 12);
			
			byte[] tempArray = c1DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
								workingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_CP)) {
			// WorkingMessage CP : ����Ʈ ���� ���� 
			// ODT CP			 : ����Ʈ ���� ���� 
			CP_WorkingMessage cpWorkingMessage = 
					(CP_WorkingMessage) workingMessage;
			
			DataStruct cpDS = new DataStruct();
			cpDS.addString("command", "CP", 2);
			cpDS.addString("bonusNumber", cpWorkingMessage.getBonusCardNo(), 16);
			cpDS.addString("pointScore", cpWorkingMessage.getBonusCardPoint(), 10);
			
			byte[] tempArray = cpDS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
								workingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_ACK)) {
			// WorkingMessage ACK
			// ODT ACK
			String nozzleNo = workingMessage.getNozzleNo();
			byte[] ackMessage = new byte[6];
			
			ackMessage[0] = Command.SOH;
			ackMessage[1] = (byte) nozzleNo.charAt(0);
			ackMessage[2] = (byte) nozzleNo.charAt(1);
			ackMessage[3] = Command.STX;
			ackMessage[4] = Command.ACK;
			ackMessage[5] = Command.ETX;
			
			returnMessage = ackMessage;
			
		
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_NAK)) {
			// WorkingMessage NAK
			// ODT NAK
			String nozzleNo = workingMessage.getNozzleNo();
			byte[] nakMessage = new byte[6];
			
			nakMessage[0] = Command.SOH;
			nakMessage[1] = (byte) nozzleNo.charAt(0);
			nakMessage[2] = (byte) nozzleNo.charAt(1);
			nakMessage[3] = Command.STX;
			nakMessage[4] = Command.NAK;
			nakMessage[5] = Command.ETX;
			
			returnMessage = nakMessage;
			
		} else {
			LogUtility.getLogger().error("### Not Supported " +
											 "command(" + workingMessageCommand +
											 ") in TransGSGasOdt ###");
			returnMessage = null;
			
		}	// end if

		/*
		// �α� ��� 
		if (returnMessage != null) {
			LogUtility.getLogger().debug(
					workingMessageCommand + "WorkingMessage -> " +  
					"[" + new String(returnMessage) + "]"); 
			
		} else {
			LogUtility.getLogger().debug(
					workingMessageCommand + "WorkingMessage -> " + "null");
		}	// end if
		*/
		
		return returnMessage;
		
	}	// end generateByteArray



	/**
	 * ODT �������� ��� �ڵ带 ���� 
	 * 
	 * @param message	: ODT ����
	 * @return			: ��� �ڵ� 
	 */
	private String getCommand(byte[] message) throws Exception {
		String returnMessage = null;
		
		byte[] commandArray 	= new byte[2];
		commandArray[0] 	 	= message[4];
		commandArray[1] 		= message[5];
		
		returnMessage = new String(commandArray);
		
		return returnMessage;
	
	}	// end getCommand
	
	

	/**
	 * byte �迭�� data��  ������ ODT ���� ���·� ��ȯ
	 * 
	 * @param data		: data
	 * @param nozzleNo	: ���� ��ȣ 
	 * @return			: ODT ���� 
	 */
	private byte[] makeProtocol(byte[] data, String odtNo) throws Exception {
		if (data == null) {
			return null;
			
		}	// end if
		
		byte[] returnData 		= null;
		int returnDataCounter 	= 0;
		
		
		// ETB, CRC16(4), SUM ���� - 2015.12.28 ������ 
		int arrayLength = data.length + 5;
		
		returnData = new byte[arrayLength];
		
		// SOH, ODT_NO(2), STX, ETX
		returnData[returnDataCounter++] = Command.SOH;				// SOH
		returnData[returnDataCounter++] = (byte) odtNo.charAt(0);	// ODT_NO_1
		returnData[returnDataCounter++] = (byte) odtNo.charAt(1);	// ODT_NO_2
		returnData[returnDataCounter++] = Command.STX;				// STX
		
		for (int i = 0; i < data.length; i++) {
			returnData[returnDataCounter++] = data[i];
			
		}	// end for
		
		returnData[returnDataCounter++] = Command.ETX;				// ETX
		
		return returnData;
		
	}	// end makeProtocol
	
	
	
	/**
	 * ODT �������� ODT��ȣ�� ����
	 * 
	 * @param message	: ODT ���� 
	 * @return			: ODT ��ȣ 
	 */
	private String getOdtNo(byte[] message) throws Exception {
		String returnMessage = null;
		
		byte[] odtNo 	= new byte[2];
		odtNo[0] 		= message[1];
		odtNo[1] 		= message[2];
		
		returnMessage = new String(odtNo);
		
		return returnMessage;
		
	}	// end getNozzleNo
	
	
	
	/**
	 * '\n' ���ڸ� 0x1E�� ��ȯ
	 * 
	 * @param message	: '\n' ������ ���ڿ�
	 * @return			: ��ȯ�� ���ڿ�
	 */
	private String changeNewLine(String message) throws Exception {
		String returnMessage;
		
		byte[] tempBs = message.getBytes();
		
		for (int i = 0; i < tempBs.length; i++) {
			// "\n" = 0xA
			if (tempBs[i] == 0xA) {
				tempBs[i] = 0x1E;
				
			}	// end if
			
		}	// end for
		
		returnMessage = new String(tempBs);
		
		return returnMessage;
		
	}	// end changeNewLine
	
	
	
	/**
	 * ODT ������ ������ ǥ�õǴ� ���ڸ� 16 bytes�� ���� ���� �� �ֵ��� �Ѵ�.
	 * �ѱ۷� �̷���� ���ڿ��� 16 bytes�� ���߱� ���� �ʿ��� ��� 
	 * 15 bytes ���� ���� ��ĭ�� �߰��Ѵ�.  
	 *   
	 * 
	 * @param message : ������ ǥ�õǴ� ����
	 * @return
	 * @throws Exception
	 */
	private String setOdtLCD16Bytes(String message) throws Exception {
		
		StringBuffer returnMessage = new StringBuffer();
		String tempString 	= null;
		byte[] tempBs 		= null;
		int bytecount 		= 0;	
		int length 			= 16;	// ���� ���� ����
		
		for (int i=0; i < message.length(); i++)
		{
			tempString = message.substring(i, i+1);
			tempBs = tempString.getBytes();
			bytecount = bytecount + tempBs.length;
			
			if (bytecount > length )
			{
				returnMessage.append(" ");
				bytecount = 0;
				bytecount = bytecount + tempBs.length;
				returnMessage.append(tempString);
				
			} else if (bytecount == length ) {
				returnMessage.append(tempString);
				bytecount = 0;
				
			} else {
				returnMessage.append(tempString);
				
			}	// end if
			
		}	// end for
		
//		int divide 			= message.length() / length;
//		while (divide > 0) {
//			tempString = message.substring(0, length); 
//			returnMessage.append(tempString);
//			returnMessage.append(rs);
//			message = message.substring(tempString.length());
//			divide = message.length() / length;
//		}
		
		return returnMessage.toString();
		
	}	// end setOdtLCD16Bytes
	
}