package com.gsc.kixxhub.device.pumpa.translation;

import java.util.Vector;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.BI_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.F0_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HC_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HD_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P2_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P5_1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P5_NozzleInfo;
import com.gsc.kixxhub.common.data.pump.P6_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PB_ST_TrInfo;
import com.gsc.kixxhub.common.data.pump.PB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PE_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.QD_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S3_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S4_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S8_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SE_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.ST_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.Change;
import com.gsc.kixxhub.device.pumpa.common.Command;
import com.gsc.kixxhub.device.pumpa.common.DataStruct;
import com.gsc.kixxhub.device.pumpa.devDatas.SomoSelfDS;

public class TransSomoSelfN extends Translation {
	// byte[]�� �����ȣ�� 1�� ���ؼ� WorkingMessage ConnectNozzleNo�� ����
	
	private final String E_PRINTER_ERROR 	= "201";
	private final String E_PRINTER_PAPER 	= "202";
	private final String E_VOICE_BUSY 		= "203";
	private final String E_VOICE_ERROR 		= "204";
	public int ODTVersion = 0;
	private final String PRINTER_ERROR 		= "261";
	private final String PRINTER_PAPER 		= "262";
	private final String VOICE_BUSY 				= "263";
	
	private final String VOICE_ERROR 			= "264";
	/**
	 * WorkingMessage�� �Ҹ� ���� �������� ��ȯ�Ѵ�.
	 * 
	 * @param workingMessage 	: WorkingMessage
	 * @return					: �Ҹ� ���� ���� 
	 */
	public byte[] generateByteStream(WorkingMessage workingMessage) throws Exception {
		byte[] returnMessage = null;
		
		String workingMessageCommand = workingMessage.getCommand();
		// �����ȣ�� ���Ѵ�(ODT ��ȣ�� WorkingMessage�� NozzleNo, 
		//               ���� ��ȣ�� WorkingMessage�� ConnectNozzleNo) 
		String connectNozzleNo = this.getConnectNozzleNo(workingMessage);
		String odtNo = this.getOdtNo(workingMessage);
		
		if (workingMessageCommand.equals(IPumpConstant.COMMANDID_HD)) {
			// WorkingMessage HD : �ܻ� ���� 
			// SomoSelf ���� 52	 : �ܻ� ����/��� ���� 
			HD_WorkingMessage hdWorkingMessage = (HD_WorkingMessage) workingMessage;
			hdWorkingMessage.print();
			
			String monthLimit 	= hdWorkingMessage.getMonthLimit();	// �ѵ�����
			String saveLimit 			= hdWorkingMessage.getSaveLimit();		// ��뷮
			String saleLiter 			= hdWorkingMessage.getSaleLiter();
			String basePrice			= hdWorkingMessage.getBasePrice();
			String receiptLiter		= hdWorkingMessage.getReceiptLiter();
			
			DataStruct ss52DSt7 = new DataStruct();
			ss52DSt7.addString("commandID", "52", 2);
			ss52DSt7.addString("commandIndex", "1", 1);
			ss52DSt7.addString("odtNo", odtNo, 2);									//ODT ��ȣ
			ss52DSt7.addString("nozzleNo", connectNozzleNo, 2);			// �����ȣ
			ss52DSt7.addString("mode", this.generateBlank(1), 1);			// ���� 
			ss52DSt7.addString("trType", 
					hdWorkingMessage.getTrType(), 1);									// ��������
			ss52DSt7.addString("serialNo", 
					hdWorkingMessage.getSerialNo(), 18);								// ī���ȣ
			ss52DSt7.addString("carNo", 
					hdWorkingMessage.getCarNo(), 18);									// ������ȣ 18�ڸ� (or ��������ȣ 4�ڸ�)
			ss52DSt7.addString("productCode", 
					hdWorkingMessage.getProductCode(), 18);						// �����ڵ� 
			ss52DSt7.addString("driverName", 
					hdWorkingMessage.getDriverName(), 50);						// �����ڸ� (or ��������)
			ss52DSt7.addString("saleLiter", 
					GlobalUtility.appending0Pre(saleLiter, 7), 7); 					// �Ǹż��� (������ space)
			ss52DSt7.addString("basePrice", 
					GlobalUtility.appending0Pre(basePrice, 6), 6);					// �ǸŴܰ�
			ss52DSt7.addString("receiptLiter", 
					GlobalUtility.appending0Pre(receiptLiter, 7), 7);				// ��ǥ��
			ss52DSt7.addString("transType", 
					hdWorkingMessage.getTransType(), 1);								// �ŷ�����(0: ����, 1: �ܻ�, 2: �̵��ī��)
			ss52DSt7.addString("cusType", 
					hdWorkingMessage.getCusType(), 1);								// ������(1: ������, 2: ������, 3: ����, 4:1ȸ����
																													//                5: ������, 6: �����Է�, 7: ������ī��)
			ss52DSt7.addString("transStatus", 
					hdWorkingMessage.getTransStatus(), 1); 							// �ŷ�����->�ܻ�ŷ� ����(1: �ŷ���, 2: �ŷ�����, 3:�ŷ�����)
			ss52DSt7.addString("printBase", 
					hdWorkingMessage.getPrintBase(), 1);								// �ܰ���¿���(0: ��¾���, 1:���)
			ss52DSt7.addString("depositST", 
					hdWorkingMessage.getDepositST(), 1);								// ���������࿩��(1: �������, 2:����)
			ss52DSt7.addString("floatTR", 
					hdWorkingMessage.getFloatTR(), 1); 								// �Ҽ���ó�����(1: ����, 2:�ݿø�, 3:����)
			ss52DSt7.addString("receiptType", 
					hdWorkingMessage.getReceiptType(), 1); 						// ��꼭�ŷ�����(1: ��������, 2:���ݺ�����, 3:�������)
			ss52DSt7.addString("monthLimit", 
					GlobalUtility.appending0Pre(monthLimit, 18), 18);			// �ѵ�����
			ss52DSt7.addString("saveLimit", 
					GlobalUtility.appending0Pre(saveLimit, 18), 18);				// ��뷮
			ss52DSt7.addString("limitType", 
					hdWorkingMessage.getLimitType(), 1);								// �ѵ�����(1: ����, 2: �ݾ�)
			
			// ksm 2012.06.08 ���� ���ڵ� �߰�
			ss52DSt7.addString("barCode", 
					hdWorkingMessage.getBarCode(), 16);								// �������ڵ�
			
			byte[] tempArray = ss52DSt7.getByteStream();
			
			returnMessage = this.makeProtocol(tempArray, hdWorkingMessage.getNozzleNo());
		
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_HC)) {
			// WorkingMessage HC : �Ҹ���  ����/�ſ�/���ʽ� ����/��� ���� 
			// SomoSelf ���� 52	 : �ܻ� �� ���� (trType 1,2,3,4,5,6,A,B,C,D,E,F,G,H,I) 
			HC_WorkingMessage hcWorkingMessage = (HC_WorkingMessage) workingMessage;
			hcWorkingMessage.print();			
			
			String generateScore 	= hcWorkingMessage.getGenerateScore(); 	// �߻�����
			String score 					= hcWorkingMessage.getScore(); 					// ��������
			String totalScore 			= hcWorkingMessage.getTotalScore();			// �Ѵ�������
			String storePoint 			= hcWorkingMessage.getStorePoint();			// �������� 
			
			DataStruct ss52DS = new DataStruct();
									
			// ����κ�
			ss52DS.addString("commandID", "52", 2);
			ss52DS.addString("commandIndex", "1", 1);
			ss52DS.addString("odtNo", odtNo, 2);																		//ODT ��ȣ
			ss52DS.addString("nozzleNo", connectNozzleNo, 2);												// �����ȣ
			ss52DS.addString("mode", hcWorkingMessage.getMode(), 1);								// MODE
			ss52DS.addString("trType", hcWorkingMessage.getTrType(), 1);							// ��������
			ss52DS.addString("authInfo", hcWorkingMessage.getAuthInfo(), 35);					// ��������
			
			if (ODTVersion >= 1) { // 2011.06.28 (FS �߰� ����)
				
				ss52DS.addString("price", (char) 0x1C + hcWorkingMessage.getPrice() + (char) 0x1C, 
						hcWorkingMessage.getPrice().getBytes().length + 2);									// �ݾ�
				ss52DS.addString("authTime", hcWorkingMessage.getAuthTime() + (char) 0x1C, 
						hcWorkingMessage.getAuthTime().getBytes().length + 1);							// ���νð�
				ss52DS.addString("authNo", hcWorkingMessage.getAuthNo() + (char) 0x1C, 
						hcWorkingMessage.getAuthNo().getBytes().length + 1);							// ���ι�ȣ
				ss52DS.addString("cardNo", hcWorkingMessage.getCardNo() + (char) 0x1C, 
						hcWorkingMessage.getCardNo().getBytes().length + 1);								// ī���ȣ
				ss52DS.addString("cardCorpName", hcWorkingMessage.getCardCorpName() + (char) 0x1C, 
						hcWorkingMessage.getCardCorpName().getBytes().length + 1);				// ī���� 
				ss52DS.addString("noteCorpCode", hcWorkingMessage.getNoteCorpCode() + (char) 0x1C, 
						hcWorkingMessage.getNoteCorpCode().getBytes().length + 1);				// ��ǥ���Ի� �ڵ�
				ss52DS.addString("noteCorpName", hcWorkingMessage.getNoteCorpName() + (char) 0x1C, 
						hcWorkingMessage.getNoteCorpName().getBytes().length + 1);				// ��ǥ���Ի��
				ss52DS.addString("noteNumber", hcWorkingMessage.getNoteNumber() + (char) 0x1C, 
						hcWorkingMessage.getNoteNumber().getBytes().length + 1);					// ��ǥ��ȣ
				ss52DS.addString("bonusAuthCode", hcWorkingMessage.getBonusAuthCode() + (char) 0x1C, 
						hcWorkingMessage.getBonusAuthCode().getBytes().length + 1);				// ���ʽ������ڵ�
				ss52DS.addString("bonusCardNumber", hcWorkingMessage.getBonusCardNumber() + (char) 0x1C, 
						hcWorkingMessage.getBonusCardNumber().getBytes().length + 1);			// ���ʽ�ī���ȣ 
				ss52DS.addString("bonusAuthTime", hcWorkingMessage.getBonusAuthTime() + (char) 0x1C, 
						hcWorkingMessage.getBonusAuthTime().getBytes().length + 1);				// ���ʽ� ���νð� 	
				ss52DS.addString("bonusAuthNumber", hcWorkingMessage.getBonusAuthNumber() + (char) 0x1C, 
						hcWorkingMessage.getBonusAuthNumber().getBytes().length + 1);			// ���ʽ� ���ι�ȣ
				ss52DS.addString("dAuthNumber", hcWorkingMessage.getDAuthNumber() + (char) 0x1C, 
						hcWorkingMessage.getDAuthNumber().getBytes().length + 1);					// ���ݿ����� ���ι�ȣ
				
				String generateScore2 	= this.removeZero(generateScore);
				String score2 					= this.removeZero(score);
				String totalScore2 			= this.removeZero(totalScore);
				String storePoint2 			= this.removeZero(storePoint);
				
				ss52DS.addString("generateScore", generateScore2 + (char) 0x1C, 
						generateScore2.getBytes().length + 1);													// �߻�����
				ss52DS.addString("score", score2 + (char) 0x1C, 
						score2.getBytes().length + 1);																	// ��������
				ss52DS.addString("totalScore", totalScore2 + (char) 0x1C, 
						totalScore2.getBytes().length + 1);															// �Ѵ�������				
				ss52DS.addString("storePoint", storePoint2 + (char) 0x1C, 
						storePoint2.getBytes().length + 1);															// �������� 
				ss52DS.addString("publicMsg", hcWorkingMessage.getPublicMsg() + (char) 0x1C, 
						hcWorkingMessage.getPublicMsg().getBytes().length + 1);				// �޽���(tittle)
				ss52DS.addString("vanMsg", hcWorkingMessage.getVanMsg() + (char) 0x1C, 
						hcWorkingMessage.getVanMsg().getBytes().length + 1);					// ���޽���
				ss52DS.addString("bnsMsg", hcWorkingMessage.getBnsMsg() + (char) 0x1C, 
						hcWorkingMessage.getBnsMsg().getBytes().length + 1);					// ���ʽ��޽���
				ss52DS.addString("basePrice", hcWorkingMessage.getBasePrice() + (char) 0x1C, 
						hcWorkingMessage.getBasePrice().getBytes().length + 1);					// �ܰ�(09/12/09 �߰�) ���ݰŷ�ó����
				ss52DS.addString("barCode", hcWorkingMessage.getBarCode() + (char) 0x1C,
						hcWorkingMessage.getBarCode().getBytes().length + 1);					// �������ڵ�(12/06/01 �߰�)
			}
			else { // ���� ���� ����
				ss52DS.addString("price", 
						hcWorkingMessage.getPrice(), 8);							// �ݾ�
				ss52DS.addString("authTime", 
						hcWorkingMessage.getAuthTime(), 15);				// ���νð�
				ss52DS.addString("authNo", 
						hcWorkingMessage.getAuthNo(), 20);					// ���ι�ȣ
				ss52DS.addString("cardNo", 
						hcWorkingMessage.getCardNo(), 40);					// ī���ȣ
				ss52DS.addString("cardCorpName", 
						hcWorkingMessage.getCardCorpName(), 30);		// ī���� 
				ss52DS.addString("noteCorpCode", 
						hcWorkingMessage.getNoteCorpCode(), 4);			// ��ǥ���Ի� �ڵ�
				ss52DS.addString("noteCorpName", 
						hcWorkingMessage.getNoteCorpName(), 30);		// ��ǥ���Ի��
				ss52DS.addString("noteNumber", 
						hcWorkingMessage.getNoteNumber(), 12);			// ��ǥ��ȣ
				ss52DS.addString("bonusAuthCode", 
						hcWorkingMessage.getBonusAuthCode(), 5);		// ���ʽ������ڵ�
				ss52DS.addString("bonusCardNumber", 
						hcWorkingMessage.getBonusCardNumber(), 40);		// ���ʽ�ī���ȣ 
				ss52DS.addString("bonusAuthTime", 
						hcWorkingMessage.getBonusAuthTime(), 15);				// ���ʽ� ���νð� 	
				ss52DS.addString("bonusAuthNumber", 
						hcWorkingMessage.getBonusAuthNumber(), 20);		// ���ʽ� ���ι�ȣ
				ss52DS.addString("dAuthNumber", 
						hcWorkingMessage.getDAuthNumber(), 20);				// ���ݿ����� ���ι�ȣ
				ss52DS.addString("generateScore", 
						this.removeZero(generateScore), 12);				// �߻�����
				ss52DS.addString("score", 
						this.removeZero(score), 12);							// ��������
				ss52DS.addString("totalScore", 
						this.removeZero(totalScore), 12);					// �Ѵ�������
				ss52DS.addString("storePoint", 
						this.removeZero(storePoint), 12);					// �������� 
				ss52DS.addString("publicMsg", 
						hcWorkingMessage.getPublicMsg(), 50);		// �޽���(tittle)
				ss52DS.addString("vanMsg", 
						hcWorkingMessage.getVanMsg(), 500);		// ���޽���
				ss52DS.addString("bnsMsg", 
						hcWorkingMessage.getBnsMsg(), 500);		// ���ʽ��޽���
				ss52DS.addString("basePrice", 
						hcWorkingMessage.getBasePrice(), 6);			// �ܰ�(09/12/09 �߰�)
			}			
			
			byte[] tempArray = ss52DS.getByteStream();
			
			returnMessage = this.makeProtocol(tempArray, hcWorkingMessage.getNozzleNo());
						
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_P1)) {
			// WorkingMessage P1 : ���� ������ ����� ���� ����
			// SomoSelf 02       : ȯ�漳�� ���� 
			P1_WorkingMessage p1WorkingMessage = 
								(P1_WorkingMessage) workingMessage;
			
			String storeName 	= p1WorkingMessage.getStoreName();
			String repName 		= p1WorkingMessage.getRepName();
			String storeAdd1 	= p1WorkingMessage.getStoreADDR1();
			String storeAdd2 	= p1WorkingMessage.getStoreADDR2();
			String phone 			= p1WorkingMessage.getTel();
			String saMinAmt	= p1WorkingMessage.getSaMinAmt().trim();
			
			DataStruct ss02_01DS = new DataStruct();
			ss02_01DS.addString("command", "02", 2);
			ss02_01DS.addString("orderNo", "1", 1);
			ss02_01DS.addString("block", "2", 1);
			ss02_01DS.addString("fileNo", "01", 2);
			ss02_01DS.addString("fileLength", "235", 3);
			ss02_01DS.addString("storeCode", p1WorkingMessage.getStoreCord(), 10);
			ss02_01DS.addString("regiNo", p1WorkingMessage.getStoreRegiNum(), 12);
			ss02_01DS.addString("storeName", this.subStringCheck(storeName, 40), 40);
			ss02_01DS.addString("repName", this.subStringCheck(repName, 30), 30);
			ss02_01DS.addString("storePost", p1WorkingMessage.getStorePost(), 7);
			ss02_01DS.addString("storeAdd1", this.subStringCheck(storeAdd1, 50), 50);
			ss02_01DS.addString("storeAdd2", this.subStringCheck(storeAdd2, 50), 50);
			ss02_01DS.addString("phone", this.subStringCheck(phone, 16), 16);
			ss02_01DS.addString("saMinAmt",	GlobalUtility.appending0Pre(saMinAmt, 10), 10);
			ss02_01DS.addString("bonusStoreCode", this.generateBlank(10), 10);
			
			byte[] tempArray = ss02_01DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, p1WorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_P2)) {
			// WorkingMessage P2 : ���� ������ �Ӹ��� / ������ ���� 
			// SomoSelf 02       : ȯ�漳�� ����
			P2_WorkingMessage p2WorkingMessage = 
						(P2_WorkingMessage) workingMessage;
			
			DataStruct ss02_02DS = new DataStruct();
			ss02_02DS.addString("command", "02", 2);
			ss02_02DS.addString("orderNo", "1", 1);
			ss02_02DS.addString("block", "2", 1);
			ss02_02DS.addString("fileNo", "02", 2);
			ss02_02DS.addString("fileLength", "000", 3);
			ss02_02DS.addString("head",p2WorkingMessage.getBaseHeadTitle(), 50);
			ss02_02DS.addString("tail1", p2WorkingMessage.getBaseFootTitle1(), 50);
			ss02_02DS.addString("tail2", p2WorkingMessage.getBaseFootTitle2(), 50);
			
			byte[] tempArray = ss02_02DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, p2WorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_P6)) {
			// WorkingMessage P6 : ������ �� �ð� ����
			// SomoSelf 22       : �ð����� ���� 
			P6_WorkingMessage p6WorkingMessage = 
							(P6_WorkingMessage) workingMessage;
			
			String date = p6WorkingMessage.getSystemTime();
			
			DataStruct ss22DS = new DataStruct();
			
			ss22DS.addString("command", "22", 2);
			ss22DS.addString("orderNo", "1", 1);
			ss22DS.addString("date", "20" + date, 14);
			
			byte[] tempArray = ss22DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, p6WorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_P5_1)) {
			// WorkingMessage P5_1 : ODT ȯ������ ���� 
			// SomoSelf 02         : ȯ�漳�� ����
			P5_1_WorkingMessage p5WorkingMessage = 
							(P5_1_WorkingMessage) workingMessage;
			
			Vector<P5_NozzleInfo> nozzleInfoVector = 
							p5WorkingMessage.getNozzleInfo();
			
			DataStruct ss02DS = new DataStruct();
			
			ss02DS.addString("command", "02", 2);
			ss02DS.addString("orderNo", "1", 1);
			ss02DS.addString("block", "2", 1);
			ss02DS.addString("fileNo", "05", 2);
			ss02DS.addString("fileLength", "096", 3);

			for (int i = 0; i < nozzleInfoVector.size(); i++) {
				P5_NozzleInfo nozzleInfo = nozzleInfoVector.get(i);

				String nozzleNo  	= nozzleInfo.getNozzleNumber();
				int nozzleNoInt		= Change.toValue(nozzleNo);
				String basePrice 	= nozzleInfo.getBasePrice();
				String goodsCode 	= nozzleInfo.getGoodsCode();
				String goodsType 	= nozzleInfo.getGoodsType();
				
				ss02DS.addString("nozzleNo"  + i, 
									Change.toString("%02d", nozzleNoInt), 2);
				ss02DS.addString("basePrice" + i, 
									this.subStringCheck(basePrice, 4), 4);
				ss02DS.addString("goodsCode" + i, 
									this.subStringCheck(goodsCode, 4), 4);
				ss02DS.addString("goodsType" + i, 
									this.subStringCheck(goodsType, 14), 14);
				
			}	// end for
			
			// ������ 'Z'�� ä��� 
			int zCharBlankSize = 96 - (nozzleInfoVector.size() * 24);
			ss02DS.addString("blank", 
					this.generateBlank(zCharBlankSize, "Z"), zCharBlankSize);

			byte[] tempArray = ss02DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, p5WorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PE)) {
			// WorkingMessage PE : ������ / ������ ���� ��û
			// SomoSelf 95       : ������ �������� ��û 
			PE_WorkingMessage peWorkingMessage = 
							(PE_WorkingMessage) workingMessage;
			
			DataStruct ss95DS = new DataStruct();
			
			ss95DS.addString("command", "95", 2);
			ss95DS.addString("orderNo", "1", 1);
			ss95DS.addString("nozzleNo", connectNozzleNo, 2);
			
			byte[] tempArray = ss95DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, peWorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PA)) {
			// WorkingMessage PA : ����������
			// SomoSelf 50       : ������ ���� 
			PA_WorkingMessage paWorkingMessage = 
							(PA_WorkingMessage) workingMessage;
			String state = paWorkingMessage.getNozzleState();
			
			DataStruct ss50DS = new DataStruct();
			
			ss50DS.addString("command", "50", 2);
			ss50DS.addString("flag", "0", 1);
			ss50DS.addString("nozzleNo", connectNozzleNo, 2);
			
			// PA State 0 : ����(����) ���� 
			if (state.equals("0")) {
				ss50DS.addString("nozzleState", "70", 2);
				
			// PA State 1 : ����(����) ���� ���� 
			} else if (state.equals("1")) {
				ss50DS.addString("nozzleState", "80", 2);
				
			} else {
				LogUtility.getPumpALogger().error(
						new StringBuffer("# [TransSomoSelf] # ")
						.append("PA WorkingMessage state Error # ")
						.append("Current state : ").append( state ).append(" # ").toString());
				
			}	// end inner if
			
			byte[] tempArray = ss50DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray,paWorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_QD)) {
			// WorkingMessage QD : ���ʽ� ���� ���� 
			// SomoSelf 56       : ������ ���� ���� 
			QD_WorkingMessage qdWorkingMessage = 
							(QD_WorkingMessage) workingMessage;
			
			DataStruct ss56DS = new DataStruct();
			
			ss56DS.addString("command", "56", 2);
			ss56DS.addString("nozzleNo", connectNozzleNo, 2);
			ss56DS.addString("bonusCardNumber", qdWorkingMessage.getBonusCardNum(), 16);
			ss56DS.addString("accScore", qdWorkingMessage.getAccScore(), 8);
			
			byte[] tempArray = ss56DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray,qdWorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PB)) {
			// WorkingMessage PB : ���� / ���� ����
			// SomoSelf 55       : �� ���� ���� 
			PB_WorkingMessage pbWorkingMessage = 
							(PB_WorkingMessage) workingMessage;
			
			DataStruct ss55DS = new DataStruct();
			
			ss55DS.addString("command", "55", 2);
			ss55DS.addString("orderNo", "1", 1);
			ss55DS.addString("nozzleNo", connectNozzleNo, 2);
			ss55DS.addString("mode", pbWorkingMessage.getCommandSet(), 1);
			ss55DS.addString("liter", pbWorkingMessage.getLiter(), 7);
			ss55DS.addString("basePrice", pbWorkingMessage.getBasePrice().substring(0, 4), 4);
			ss55DS.addString("price", pbWorkingMessage.getPrice().substring(2), 6);
			ss55DS.addString("barcode", pbWorkingMessage.getBarCode(), 16);	// 2012.07.19 ksm 
						
			byte[] tempArray = ss55DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, pbWorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_S3)) {
			// WorkingMessage S3 : ����/���� �� �ڷ�����(Process 202���� �ش� ODT�� �߰� �� ���)
			// SomoSelf 51       : ���� ��, ���� �Ϸ� 
			S3_WorkingMessage s3WorkingMessage = (S3_WorkingMessage) workingMessage;
			
			DataStruct ss51DS = new DataStruct();
			
			ss51DS.addString("command", "51", 2);
			ss51DS.addString("orderNo", "1", 1);
			ss51DS.addString("nozzleNo", connectNozzleNo, 2);
			ss51DS.addString("state", "4", 1);
			ss51DS.addString("liter", s3WorkingMessage.getLiter(), 7);
			ss51DS.addString("basePrice", s3WorkingMessage.getBasePrice().substring(0, 4), 4);
			ss51DS.addString("price", s3WorkingMessage.getPrice(), 6);
			
			byte[] tempArray = ss51DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
						s3WorkingMessage.getNozzleNo());
		
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_S4)) {
			// WorkingMessage S4 : �����Ϸ� �ڷ�����(Process 203���� �ش� ODT�� �߰� �� ���)
			// SomoSelf 51       : ���� ��, ���� �Ϸ� 
			S4_WorkingMessage s4WorkingMessage = 
						(S4_WorkingMessage) workingMessage;
			
			DataStruct ss51DS = new DataStruct();
			
			ss51DS.addString("command", "51", 2);
			ss51DS.addString("orderNo", "1", 1);
			ss51DS.addString("nozzleNo", connectNozzleNo, 2);
			ss51DS.addString("state", "5", 1);
			ss51DS.addString("liter", s4WorkingMessage.getLiter(), 7);
			ss51DS.addString("basePrice", s4WorkingMessage.getBasePrice().substring(0, 4), 4);
			ss51DS.addString("price", s4WorkingMessage.getPrice().substring(2), 6);
			
			byte[] tempArray = ss51DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
						s4WorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_S8)) {
			// WorkingMessage S8 : ������ / ������ ���� ���� 
			// SomoSelf 50       : ������ ���� 
			S8_WorkingMessage s8WorkingMessage = (S8_WorkingMessage) workingMessage;
			String s8StatusCode 	= s8WorkingMessage.getStatusCode();
			// �⺻ �� 20(���) 
			String ss50StatusCode 	= "20";
			String flag 		= "";
			DataStruct ss50DS 	= new DataStruct();
			
			s8WorkingMessage.print();
			
			if (s8StatusCode.length() > 2) {
				// ���� ���´� status code�� ���ڸ� �� �̴�.
				String nozzleState = s8WorkingMessage.getNozzleState();
				flag = "0";
				
				if (nozzleState.length() > 1) {
					if (nozzleState.equals("651")) {
						// ���
						ss50StatusCode = "20";
						
					} else if (nozzleState.equals("652")) {
						// ���� ��
						ss50StatusCode = "30";
						
					} else if (nozzleState.equals("653")) {
						// ���� ��
						ss50StatusCode = "40";
						
					} else if (nozzleState.equals("654")) {
						// ���� �Ϸ� 
						ss50StatusCode = "50";
						
					} else if (nozzleState.equals("656")) {
						// ��������(�������)
						ss50StatusCode = "70";
						
					} else if (nozzleState.equals("657")) {
						// ��� ���� 
						ss50StatusCode = "80";
						
					} else if (nozzleState.equals("601")) {
						// ȸ�� �ҷ� 
						ss50StatusCode = "60";
						
					} else {
						LogUtility.getPumpALogger().error(
								"S8_WorkingMessage NozzleState �� Ȯ�� �ʿ�!! " +
								"���� NozzleState �� : " + s8StatusCode);
						
					}	
					
				} else {
					ss50StatusCode = nozzleState + "0";
					
				}	
				
			} else {
				// ���� ���´� status code�� ���ڸ� �� �̴�. 
				ss50StatusCode = s8StatusCode;
				flag = "1";

			}	// end inner if
			
			ss50DS.addString("command", "50", 2);
			ss50DS.addString("flag", flag, 1);
			ss50DS.addString("nozzleNo", connectNozzleNo, 2);
			ss50DS.addString("state", ss50StatusCode, 2);

			byte[] tempArray = ss50DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
						s8WorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_SE)) {
			// WorkingMessage SE : ȸ���ҷ��� SE
			// SomoSelf 50       : ������ ����  
			SE_WorkingMessage seWorkingMessage = 
						(SE_WorkingMessage) workingMessage;
			
			DataStruct ss50DS = new DataStruct();
			
			ss50DS.addString("command", "50", 2);
			ss50DS.addString("flag", "0", 1);
			ss50DS.addString("nozzleNo", connectNozzleNo, 2);
			ss50DS.addString("state", "60", 2);
			
			byte[] tempArray = ss50DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, seWorkingMessage.getNozzleNo());
			
		} else {
			LogUtility.getPumpALogger().error(
					"# [TransSomoSelf] # " +
					"Not Supported command(" + 
					workingMessageCommand +
					") in TransSomoSelf #");
			returnMessage = null;
		
		}	// end if
		
		/*
		// �α� ��� 
		if (returnMessage != null) {
			LogUtility.getPumpALogger().debug(
					workingMessageCommand + "WorkingMessage -> " +  
					"[" + new String(returnMessage) + "]"); 
			
		} else {
			LogUtility.getPumpALogger().debug(
					workingMessageCommand + "WorkingMessage -> " + "null");
		}	// end if
		 */
		return returnMessage;
		
	}	// end genearateByteArray
		
	
	/**
	 * �Ҹ� ���� ������ WorkingMessage�� ��ȯ�Ѵ�.
	 * 
	 * @param message	: �Ҹ��� ����
	 * @param command	: WorkingMessage Command
	 * @return			: WorkingMessage 
	 */
	public WorkingMessage generateWorkingMessage(byte[] message, 
													String command) throws Exception {
		WorkingMessage returnMessage = null;	
		// byte[]�� �ִ� ����ڵ带 ��´�
		String somoCommandString = this.getCommand(message);
		// byte[]�� ODT��ȣ�� ��´�
		String odtNumber 	= this.getOdtNoFromBytes(message);
		
		
		if (somoCommandString.equals("51")) {
			// SomoSelf 51 : ���� ��, ���� �Ϸ�
			// WorkingMessage S3 : ����/���� �� �ڷ�����
			// WorkingMessage S4 : ������ �����Ϸ� �ڷ�����
			DataStruct s51Interface = SomoSelfDS.getDS("51");
			s51Interface.setByteStream(message);
			
			String state = (String) s51Interface.getValue("state");
			
			if (state.equals("4")) {
				// SomoSelf 51 State 4 : ���� �� 
				S3_WorkingMessage s3WorkingMessage = new S3_WorkingMessage();
				s3WorkingMessage.setNozzleNo(odtNumber);
				s3WorkingMessage.setLiter((String) s51Interface.getValue("liter"));
				s3WorkingMessage.setPrice((String) s51Interface.getValue("price"));
				s3WorkingMessage.setBasePrice(
							(String) s51Interface.getValue("basePrice") + "00");
				s3WorkingMessage.setWDate(this.getSystemTime(6));	 
				
				returnMessage = s3WorkingMessage;
				
			} else if (state.equals("5")) {
				// SomoSelf 51 State 5 : ���� �Ϸ�  
				S4_WorkingMessage s4WorkingMessage = new S4_WorkingMessage();
				s4WorkingMessage.setNozzleNo(odtNumber);
				s4WorkingMessage.setFlag("0");		// ���� ��(FLAG)
				s4WorkingMessage.setLiter(
						(String) s51Interface.getValue("liter"));
				s4WorkingMessage.setBasePrice(
						(String) s51Interface.getValue("basePrice") + "00");
				s4WorkingMessage.setPrice("00" + 
						(String) s51Interface.getValue("price"));
				s4WorkingMessage.setWDate(this.getSystemTime(6));
				s4WorkingMessage.setSystemTime(this.getSystemTime(12));
				s4WorkingMessage.setTotalGauge(this.generateBlank(10));
				s4WorkingMessage.setStatusFlag("0");	// ���� ��(STATUS_FLAG)
				
				returnMessage = s4WorkingMessage;
				
			} else {
				LogUtility.getPumpALogger().error(
						"### [TransSomoSelf] ###\n" +
						"### Incorrect state in 51 Somo Self ###\n" +
						"### Current state : " + state + "   ###");
				returnMessage = null;
					
			}	// end inner if
			
		} else 	if (somoCommandString.equals("80")) {
			// SomoSelf ���� 80 : ����/�ſ�/���ʽ�/�ܻ� ����/��� ��û 
			// WorkingMessage HA : �ܻ� ��
			// WorkingMessage HB : �ܻ� 
			DataStruct s80NInterface = SomoSelfDS.getDS("80N");
			s80NInterface.setByteStream(message);
			
			String odtNo			= (String) s80NInterface.getValue("odtNo");
			String targetNozzleNo 	= (String) s80NInterface.getValue("nozzleNo");
			String trType 			= (String) s80NInterface.getValue("trType");
			
			if (trType.equals("7"))
			{
				// TR_TYPE = 7 (�ܻ� ���� ��û)
				HB_WorkingMessage hbWorkingMessage = new HB_WorkingMessage();
				hbWorkingMessage.setNozzleNo(odtNo);
				hbWorkingMessage.setConnectNozzleNo(targetNozzleNo);
				hbWorkingMessage.setCommandIndex((String) s80NInterface.getValue("command_index"));
				hbWorkingMessage.setTrType(trType);
				hbWorkingMessage.setAuthType((String) s80NInterface.getValue("authType"));
				hbWorkingMessage.setCardNumber((String) s80NInterface.getValue("cardNum"));
				hbWorkingMessage.setBonusCard((String) s80NInterface.getValue("bonusCard"));
				hbWorkingMessage.setLiter((String) s80NInterface.getValue("liter"));
				hbWorkingMessage.setBasePrice((String) s80NInterface.getValue("basePrice"));
				hbWorkingMessage.setPrice((String) s80NInterface.getValue("price"));
				hbWorkingMessage.setCNumber(this.generateBlank(20));
				hbWorkingMessage.setBNumber(this.generateBlank(20));
				hbWorkingMessage.setDNumber(this.generateBlank(20));
				hbWorkingMessage.setPin(this.generateBlank(8));
				
				returnMessage = hbWorkingMessage;

				hbWorkingMessage.print();				
				
			} else {
								
				HA_WorkingMessage haWorkingMessage = new HA_WorkingMessage();
				
				haWorkingMessage.setNozzleNo(odtNo);
				haWorkingMessage.setConnectNozzleNo(targetNozzleNo);
				haWorkingMessage.setCommandIndex((String) s80NInterface.getValue("command_index"));
				haWorkingMessage.setTrType(trType);		
				haWorkingMessage.setAuthType((String) s80NInterface.getValue("authType"));
				haWorkingMessage.setCardNumber((String) s80NInterface.getValue("cardNum"));
				haWorkingMessage.setBonusCard((String) s80NInterface.getValue("bonusCard"));
				haWorkingMessage.setLiter((String) s80NInterface.getValue("liter"));
				haWorkingMessage.setBasePrice((String) s80NInterface.getValue("basePrice"));
				haWorkingMessage.setPrice((String) s80NInterface.getValue("price"));
				haWorkingMessage.setCNumber((String) s80NInterface.getValue("c_number"));
				haWorkingMessage.setBNumber((String) s80NInterface.getValue("b_number"));
				haWorkingMessage.setDNumber((String) s80NInterface.getValue("d_number"));
				
				haWorkingMessage.setPin((String) s80NInterface.getValue("pin"));
				
				returnMessage = haWorkingMessage;

				haWorkingMessage.print();
				
			}	// end inner if
			
		} else if (somoCommandString.equals("50")) {
			// SomoSelf 50 		 : ������ ���� 
			// WorkingMessage PA : ���� ���� ���
			PA_WorkingMessage paWorkingMessage = new PA_WorkingMessage();
			paWorkingMessage.setNozzleNo(odtNumber);
			paWorkingMessage.setPassThrough(false);
			
			DataStruct s50Interface = SomoSelfDS.getDS("50");
			s50Interface.setByteStream(message);
				
			String state 	= (String) s50Interface.getValue("state");
			String nozzleNo = (String) s50Interface.getValue("nozzleNo");
			
			paWorkingMessage.setTargetNozzleNo(nozzleNo);
				
			// SomoSelf 50 State 70 : ��������(�������)
			if (state.equals("70")) {
				paWorkingMessage.setNozzleState("0");					
				
			// SomoSelf 50 State 80 : ������� 
			} else if (state.equals("80")) {
				paWorkingMessage.setNozzleState("1");					
				
			} else {
				LogUtility.getPumpALogger().error(
						"# [TransSomoSelf] # " +
						"Incorrect state in '50' Somo Self protocol # " +
						"Current state : " + state + " #");
				
			}	// end inner if
				
			returnMessage = paWorkingMessage;
			
		} else if (somoCommandString.equals("92")) {
			// SomoSelf ���� 92 : �����㰡/�ǸſϷ� 
			// WorkingMessage PB : �����㰡 ��û 
			// WorkingMessage TR : �ǸſϷ� 
			int dupI = this.getDup(message);
			DataStruct s92NInterface = SomoSelfDS.getDS("92N", dupI);
			s92NInterface.setByteStream(message);

			String odtNo			= (String) s92NInterface.getValue("odtNo");
			String targetNozzleNo 	= (String) s92NInterface.getValue("nozzleNo");
			String divKey 	= (String) s92NInterface.getValue("divKey");
			
			if (divKey.equals("1"))	{
				// divKey = 1 (�����㰡 ��û) 
				PB_WorkingMessage pbWorkingMessage = new PB_WorkingMessage();
				String commandSet = (String)s92NInterface.getValue("command");

				if (commandSet.equals("2")) {
					// SomoSelf 2 (���� ����) = PB_WorkingMessage 0
					commandSet = "0";
					
				} else if (commandSet.equals("0")) {
					// SomoSelf 0 (Free ����) = PB_WorkingMessage 2
					commandSet = "2";

				}	// end inner if
				
				pbWorkingMessage.setDirection(IPumpConstant.DIRECTION_FROM_ODT);
				pbWorkingMessage.setPassThrough(false);
				pbWorkingMessage.setNozzleNo(odtNo);
				pbWorkingMessage.setConnectNozzleNo(targetNozzleNo);
				pbWorkingMessage.setTargetNozzleNo(targetNozzleNo);
				pbWorkingMessage.setCommandSet(commandSet);
				pbWorkingMessage.setCardNo((String)s92NInterface.getValue("cardNo"));
				pbWorkingMessage.setBonusCardNo((String)s92NInterface.getValue("bonusCardNo"));
				pbWorkingMessage.setDup((String) s92NInterface.getValue("dup"));
				
				Vector<PB_ST_TrInfo> trInfoVector = new Vector<PB_ST_TrInfo>();
				
				for (int i = 0; i < dupI; i++) {
					PB_ST_TrInfo trInfo = new PB_ST_TrInfo();
					
					trInfo.setTrType((String)s92NInterface.getValue("trType" + i));
					trInfo.setMode((String)s92NInterface.getValue("mode" + i));
					trInfo.setLiter((String)s92NInterface.getValue("liter" + i));
					trInfo.setBasePrice((String)s92NInterface.getValue("basePrice" + i));
					trInfo.setPrice((String)s92NInterface.getValue("price" + i));
					
					pbWorkingMessage.setLiter((String)s92NInterface.getValue("liter" + i));
					pbWorkingMessage.setBasePrice((String)s92NInterface.getValue("basePrice" + i));
					pbWorkingMessage.setPrice((String)s92NInterface.getValue("price" + i));
					
					trInfoVector.add(trInfo);
					
				}	// end for

				pbWorkingMessage.setTrInfoVector(trInfoVector);
				
				returnMessage = pbWorkingMessage;

				pbWorkingMessage.print();				
				
			} else if (divKey.equals("2")) {
				// divKey = 2 (�ǸſϷ�)
				ST_WorkingMessage stWorkingMessage = new ST_WorkingMessage();
				
				stWorkingMessage.setNozzleNo(odtNo);
				stWorkingMessage.setConnectNozzleNo(targetNozzleNo);
				stWorkingMessage.setCommandSet((String)s92NInterface.getValue("command"));
				stWorkingMessage.setCardNo((String)s92NInterface.getValue("cardNo"));
				stWorkingMessage.setBonusCardNo((String)s92NInterface.getValue("bonusCardNo"));
				stWorkingMessage.setDup((String) s92NInterface.getValue("dup"));
				
				Vector<PB_ST_TrInfo> trInfoVector = new Vector<PB_ST_TrInfo>();
				
				for (int i = 0; i < dupI; i++) {
					PB_ST_TrInfo trInfo = new PB_ST_TrInfo();
					
					trInfo.setTrType((String)s92NInterface.getValue("trType" + i));
					trInfo.setMode((String)s92NInterface.getValue("mode" + i));
					trInfo.setLiter((String)s92NInterface.getValue("liter" + i));
					trInfo.setBasePrice((String)s92NInterface.getValue("basePrice" + i));
					trInfo.setPrice((String)s92NInterface.getValue("price" + i));
					
					trInfoVector.add(trInfo);
					
				}	// end for

				stWorkingMessage.setTrInfoVector(trInfoVector);
				
				returnMessage = stWorkingMessage;

				stWorkingMessage.print();				
				
			} else {
				LogUtility.getPumpALogger().error(
						"# [TransSomoSelfN] # " +
						"Incorrect divKey in '92' Somo Self protocol # " +
						"Current divKey : " + divKey + " #");
			}
			
		} else if (somoCommandString.equals("93")) {
			// SomoSelf 93 		 : ������ �������/����  
			// WorkingMessage PA : ���� ���� ���
			PA_WorkingMessage paWorkingMessage = new PA_WorkingMessage();
			DataStruct s93Interface = SomoSelfDS.getDS("93");
			s93Interface.setByteStream(message);
			
			String nozzleNumber = (String) s93Interface.getValue("nozzleNo");
				
			paWorkingMessage.setNozzleNo(odtNumber);
			paWorkingMessage.setConnectNozzleNo(nozzleNumber);
			paWorkingMessage.setTargetNozzleNo(nozzleNumber);
			paWorkingMessage.setNozzleState((String) s93Interface.getValue("mode"));					
				
			returnMessage = paWorkingMessage;
			
		} else if (somoCommandString.equals("91")) {
			// SomoSelf 91 		 : ���� ���� ����   
			// WorkingMessage S8 : 
			S8_WorkingMessage s8WorkingMessage = new S8_WorkingMessage();
			String statusCode = "251";
			String statusMsg = "";
			byte statusB;
			DataStruct s91Interface = SomoSelfDS.getDS("91");
			s91Interface.setByteStream(message);
			
			statusB = (Byte) s91Interface.getValue("status");
			
			switch (statusB) {
			case 0x10:	// ��ü ����
			case 0x11:	// �ʱ�ȭ��
			case 0x12:	// ���� ���� ȭ��
				statusCode = "231";	// �������� ���
				statusMsg = "�������� ���";
				break;
				
			case 0x13:	// ���� �ݾ� �Ǵ� ������ ���� ��� ����
				statusCode = "232";	// �ݾ׼��� ���
				statusMsg = "�ݾ׼��� ���";
				break;
				
			case 0x14:	// �ſ�ī�� ���� ��� ����
			case 0x15:	// ���� ���� ��� ����
			case 0x16:	// �ܻ�ī�� ���� ��� ����
			case 0x17:	// ��ī�� ���� ��� ����
			case 0x18:	// ���ʽ� ī�� ���� ��� ����(���ʽ� ���)
				statusCode = "233";	// ���� �Է� ��� 
				statusMsg = "�����Է� ���";
				break;

			case 0x19:	// ���ʽ� ī�� ���� ��� ����(���ʽ� ����)
				statusCode = "234";	// ���ʽ� ī�� �Է� ��� 
				statusMsg = "���ʽ� �Է� ���";
				break;
				
			case 0x20:	// ���� ��û
				statusCode = "236";	// ���� ��û ��
				statusMsg = "���� ��û ��";
				break;
				
			case 0x21:	// ���� ��� ����
				statusCode = "235";	// ���� ��� 
				statusMsg = "���� ���";
				break;
				
			case 0x22:	// ���ݿ����� ��ȣ/ī�� �Է� ��� ����
				statusCode = "251";	// ����
				break;
				
			case 0x23:	// ������ ����Ʈ 
				statusCode = "251";	// ����
				break;

			default:
				statusCode = "251";	// ����
				break;
			
			}	// end switch
			
			s8WorkingMessage.setNozzleNo((String) s91Interface.getValue("nozzleNo"));
			s8WorkingMessage.setDeviceType("02");
			s8WorkingMessage.setStatus("1");
			s8WorkingMessage.setStatusCode(statusCode);
			s8WorkingMessage.setErrMsg(statusMsg);
			s8WorkingMessage.setNozzleState("0");
			s8WorkingMessage.setDetectTime(this.getSystemTime(12));
				
			returnMessage = s8WorkingMessage;

		} else if (somoCommandString.equals("81")) {
			// SomoSelf 81 		 : ����̽� ���� ����
			// WorkingMessage SE : ���� ����̽� �̻����� ����
			SE_WorkingMessage seWorkingMessage = new SE_WorkingMessage();
			seWorkingMessage.setNozzleNo(odtNumber);
				
			DataStruct s81Interface = SomoSelfDS.getDS("81");
			s81Interface.setByteStream(message);
				
			String errorCode = (String) s81Interface.getValue("errorCode");
			String status    = (String) s81Interface.getValue("status");
			
			seWorkingMessage.setDeviceType("05");
			seWorkingMessage.setStatus(status);
			seWorkingMessage.setErrMsg(this.generateBlank(20));
	
			if (status.equals("0")) {
				// status 0 : ���� ȸ��
				
				// SomoSelf 81 ErrorCode 00 : ����
				if (errorCode.equals("00")) {
					seWorkingMessage.setStatusCode("251");
					seWorkingMessage.setErrMsg("����");
						
				// SomoSelf 81 ErrorCode 14 : ���������� ���� 
				} else if (errorCode.equals("14")){
					seWorkingMessage.setStatusCode(PRINTER_ERROR);
					seWorkingMessage.setErrMsg("������ ���� - ����");
						
				// SomoSelf 81 ErrorCode 16 : ���������� ��������
				} else if (errorCode.equals("16")){
					seWorkingMessage.setStatusCode(PRINTER_PAPER);
					seWorkingMessage.setErrMsg("������ �������� - ����");
						
				// SomoSelf 81 ErrorCode 10 : ���� Busy ���� 
				} else if (errorCode.equals("10")){
					seWorkingMessage.setStatusCode(VOICE_BUSY);
					seWorkingMessage.setErrMsg("������ġ Busy - ����");	
					
				// SomoSelf 81 ErrorCode 11 : ���� ���� 
				} else if (errorCode.equals("11")){
					seWorkingMessage.setStatusCode(VOICE_ERROR);
					seWorkingMessage.setErrMsg("������ġ �̻� - ����");
						
				} else {
					LogUtility.getPumpALogger().error(
							"### [TransSomoSelf] ###\n" +
							"### Incorrect error code ###\n" +
							"### Current error code : " + errorCode + " ###");					
				}	
				
			} else if (status.equals("1")) {
				// status 1 : ���� �߻� 
				
				// SomoSelf 81 ErrorCode 00 : ����
				if (errorCode.equals("00")) {
					seWorkingMessage.setStatusCode("251");
					seWorkingMessage.setErrMsg("����");
						
				// SomoSelf 81 ErrorCode 14 : ���������� ���� 
				} else if (errorCode.equals("14")){
					seWorkingMessage.setStatusCode(E_PRINTER_ERROR);
					seWorkingMessage.setErrMsg("������ ����");
						
				// SomoSelf 81 ErrorCode 16 : ���������� ��������
				} else if (errorCode.equals("16")){
					seWorkingMessage.setStatusCode(E_PRINTER_PAPER);
					seWorkingMessage.setErrMsg("������ ��������");
						
				// SomoSelf 81 ErrorCode 10 : ���� Busy ���� 
				} else if (errorCode.equals("10")){
					seWorkingMessage.setStatusCode(E_VOICE_BUSY);
					seWorkingMessage.setErrMsg("������ġ Busy");	
					
				// SomoSelf 81 ErrorCode 11 : ���� ���� 
				} else if (errorCode.equals("11")){
					seWorkingMessage.setStatusCode(E_VOICE_ERROR);
					seWorkingMessage.setErrMsg("������ġ �̻�");
						
				} else {
					LogUtility.getPumpALogger().error(
							"# [TransSomoSelf] # " +
							"Incorrect error code # " +
							"Current error code : " + errorCode + " #");					
				}
				
			} else {
				LogUtility.getPumpALogger().error(
						"# [TransSomoSelf] # " +
						"Status Error. # " +
						"Current status : " + status + " #");
				
			}	// end inner if
				
			seWorkingMessage.setDetectTime(this.getSystemTime(12));
			seWorkingMessage.setVersion(this.generateBlank(9));
				
			returnMessage = seWorkingMessage;

		} else if (somoCommandString.equals("82")) {
			// SomoSelf 82 		 : ���� ��������
			// WorkingMessage BI : ���� ��������
			BI_WorkingMessage biWorkingMessage = new BI_WorkingMessage();
			
			DataStruct s82Interface = SomoSelfDS.getDS("82");
			s82Interface.setByteStream(message);
			
			String odtno 	= (String) s82Interface.getValue("odtNo");
			String nozNo 	= (String) s82Interface.getValue("nozzleNo");
			String cash  	= (String) s82Interface.getValue("cash");
			String cashCount= (String) s82Interface.getValue("cashCount");
			String time		= (String) s82Interface.getValue("time");			
			
			biWorkingMessage.setNozzleNo(odtno);
			biWorkingMessage.setConnectNozzleNo(nozNo);
			biWorkingMessage.setTargetNozzleNo(nozNo);
			biWorkingMessage.setCash(cash);
			biWorkingMessage.setCashCount(cashCount);
			biWorkingMessage.setTime(time);
			
			returnMessage = biWorkingMessage;	
			
		} else if (somoCommandString.equals("90")) {
			// SomoSelf 90 		 : ���� / ���� ���� ��û(���� ����)
			// WorkingMessage F0 : ���� / ���� ���� ��û(���� ����)
			F0_WorkingMessage f0WorkingMessage = new F0_WorkingMessage();
			
			DataStruct s90Interface = SomoSelfDS.getDS("90");
			s90Interface.setByteStream(message);
			
			String connectNozzleNo = (String) s90Interface.getValue("nozzleNo");
			
			f0WorkingMessage.setNozzleNo(odtNumber);
			f0WorkingMessage.setConnectNozzleNo(connectNozzleNo);
			f0WorkingMessage.setTargetNozzleNo(connectNozzleNo);
			f0WorkingMessage.setFlag((String) s90Interface.getValue("mode"));
			
			returnMessage = f0WorkingMessage;			
			
		} else {
			LogUtility.getPumpALogger().error(
					"# [TransSomoSelf] # " +
					"Not Supported command(" + somoCommandString +
					") in TransSomoSelf #");
			returnMessage = null;
			
		}	// end if
		
//		LogUtility.getPumpALogger().debug(this.printResultLog(message, returnMessage));
	
		return returnMessage;
	
	}	// end generateWorkingMessage
	

	/**
	 * �Ҹ� ���� �������� ��� �ڵ带 ���� 
	 * 
	 * @param message	: �Ҹ� ���� ����
	 * @return			: ��� �ڵ� 
	 */	
	private String getCommand(byte[] message) throws Exception {
		String returnMessage = null;
		
		byte[] commandArray 	= new byte[2];
		commandArray[0] 		= message[3];
		commandArray[1] 		= message[4];
		
		returnMessage = new String(commandArray);
		
		return returnMessage;
	
	}	// end getCommand
	
	
	/**
	 * WorkingMessage�� ConnectNozzleNo ���� ���ڸ� ���� ����
	 * 
	 * @param message	: WorkingMessage
	 * @return			: ConnectNozzleNo
	 */
	private String getConnectNozzleNo(WorkingMessage message) throws Exception {
		String returnMessage = message.getConnectNozzleNo();
		
		// ���ڸ� ���� ���ڸ��� ���� 
		if (returnMessage.length() < 2) {
			returnMessage = "0" + returnMessage;
			
		}	// end if
		
		return returnMessage;
		
	}	// end getConnectNozzleNo
	

	/**
	 * �Ҹ��� ���� 52 ������ DUP �� ���� 
	 * 
	 * @param message
	 * @return
	 * @throws Exception
	 */
	private int getDup(byte[] message) throws Exception {
		int returnData = 0;
		String tempString = null;
		
		byte[] commandArray 	= new byte[2];
		commandArray[0] 		= message[92];
		commandArray[1] 		= message[93];
		
		tempString = new String(commandArray);
		returnData = Integer.parseInt(tempString);
		
		return returnData;
	
	}	// end getCommand
	
	/**
	 * WorkingMessage�� OdtNo ���� ���ڸ� ���� ����
	 * 
	 * @param WorkingMessage
	 * @return OdtNo
	 * @throws Exception
	 * 
	 * by Mckelain 
	 */
	private String getOdtNo(WorkingMessage message) throws Exception {
		String returnMessage = message.getNozzleNo();
		
		// ���ڸ� ���� ���ڸ��� ���� 
		if (returnMessage.length() < 2) {
			returnMessage = "0" + returnMessage;
			
		}	// end if
		
		return returnMessage;
	}
	
	/**
	 * �Ҹ� ���� �������� ODT ��ȣ�� ����
	 * 
	 * @param message	: �Ҹ� ���� ���� 
	 * @return			: ODT ��ȣ 
	 */
	private String getOdtNoFromBytes(byte[] message) throws Exception {
		String returnMessage = null;
		
		byte nozzleNo = (byte) (message[1] - 63);
		
		// ������ ��ȣ ���� üũ 
		if (nozzleNo > 64 || nozzleNo < 1) {
			LogUtility.getPumpALogger().error(
					"# [TransSomoSelf] # " +
					"ODT number error in TransSomoSelf # " +
					"Current ODT number : " + nozzleNo + " #");
			returnMessage = "";
			
		}	// end if 
		
		// ���ڸ� �� ��ȯ 
		if (nozzleNo < 10){
			returnMessage = "0" + String.valueOf(nozzleNo);
			
		} else {
			returnMessage = String.valueOf(nozzleNo);

		}	// end if
		
		return returnMessage;
		
	}	// end getNozzleNo
	
	public int getODTVersion() {
		return ODTVersion;
	}

	/**
	 * byte �迭�� data��  ������ �Ҹ� ���� ���� ���·� ��ȯ
	 * 
	 * @param data		: data
	 * @param nozzleNo	: ���� ��ȣ 
	 * @return			: �Ҹ� ���� ���� 
	 */
	private byte[] makeProtocol(byte[] data, String nozzleNo) throws Exception {
		if (data == null) {
			return null;
		}	// end if
		
		byte[] returnData 		= null;
		byte blank 				= ' ';
		int returnDataCounter 	= 0;
		byte nozzleByte 		= Byte.parseByte(nozzleNo);
		
		// STX, SA, UA, ETX, BCC ��ŭ�� ���̸� ���Ѵ�.
		int arrayLength = data.length + 5;
		
		returnData = new byte[arrayLength];
		
		returnData[returnDataCounter++] = Command.STX;						// STX
		returnData[returnDataCounter++] = (byte) (nozzleByte + 0x3F);	// SA
		returnData[returnDataCounter++] = blank;										// UA
		
		for (int i = 0; i < data.length; i++) {
			returnData[returnDataCounter++] = data[i];
			
		}	// end for
		
		returnData[returnDataCounter++] = Command.ETX;					// ETX
		returnData[returnDataCounter++] = blank;									// BCC		
		
		return returnData;
		
	}	// end makeProtocol

	/**
	 * ���ڷ� �̷���� String�� �պκ� '0'�� �����.
	 * Example) 0000900 -> 900,     000-900 -> -900
	 * 
	 * 
	 * @param data
	 * @return
	 */
	private String removeZero (String data) {
		String returnData = "";
		byte[] tempBs = data.getBytes();
		
		for (int i = 0; i < tempBs.length; i++) {
			if (tempBs[i] != '0') {
				returnData = new String(tempBs, i, tempBs.length - i);
				break;
				
			}	// end if
			
		}	// end for
		
		return returnData;
		
	}	// end removeZero

	public void setODTVersion(int version) {
		ODTVersion = version;
	}
	
}