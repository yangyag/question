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
import com.gsc.kixxhub.common.data.pump.SJ_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.ST_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.Change;
import com.gsc.kixxhub.device.pumpa.common.Command;
import com.gsc.kixxhub.device.pumpa.common.DataStruct;
import com.gsc.kixxhub.device.pumpa.devDatas.PrimeSelfDS;

public class TransPrimeSelf extends Translation {
	// byte[]�� �����ȣ�� 1�� ���ؼ� WorkingMessage ConnectNozzleNo�� ����
	
	private final String E_PRINTER_ERROR 	= "201";
	private final String E_PRINTER_PAPER 	= "202";
	private final String E_VOICE_BUSY 		= "203";
	private final String E_VOICE_ERROR 		= "204";
	private final String PRINTER_ERROR 		= "261";
	private final String PRINTER_PAPER 		= "262";
	private final String VOICE_BUSY 		= "263";
	private final String VOICE_ERROR 		= "264";
	
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
			// PrimeSelf ���� 52	 : �ܻ� ����/��� ���� 
			HD_WorkingMessage hdWorkingMessage = (HD_WorkingMessage) workingMessage;
			hdWorkingMessage.print();
			
			String monthLimit 	= hdWorkingMessage.getMonthLimit();	// �ѵ�����
			String saveLimit 	= hdWorkingMessage.getSaveLimit();	// ��뷮
			String saleLiter 	= hdWorkingMessage.getSaleLiter();
			String basePrice	= hdWorkingMessage.getBasePrice();
			String receiptLiter	= hdWorkingMessage.getReceiptLiter();
			
			DataStruct ss52DSt7 = new DataStruct();
			ss52DSt7.addString("commandID", "52", 2);
			ss52DSt7.addString("commandIndex", "1", 1);
			ss52DSt7.addString("odtNo", odtNo, 2);								//ODT ��ȣ
			ss52DSt7.addString("nozzleNo", connectNozzleNo, 2);					// �����ȣ
			ss52DSt7.addString("mode", this.generateBlank(1), 1);				// ���� 
			ss52DSt7.addString("trType", hdWorkingMessage.getTrType(), 1);		// ��������
			ss52DSt7.addString("serialNo", 
					hdWorkingMessage.getSerialNo(), 18);	// ī���ȣ
			ss52DSt7.addString("carNo", 
					hdWorkingMessage.getCarNo(), 18);		// ������ȣ 18�ڸ� (or ��������ȣ 4�ڸ�)
			ss52DSt7.addString("productCode", 
					hdWorkingMessage.getProductCode(), 18);	// �����ڵ� 
			ss52DSt7.addString("driverName", 
					hdWorkingMessage.getDriverName(), 50);	// �����ڸ� (or ��������)
			ss52DSt7.addString("saleLiter", 
					GlobalUtility.appending0Pre(saleLiter, 7), 7); 	// �Ǹż��� (������ space)
			ss52DSt7.addString("basePrice", 
					GlobalUtility.appending0Pre(basePrice, 6), 6);	// �ǸŴܰ�
			ss52DSt7.addString("receiptLiter", 
					GlobalUtility.appending0Pre(receiptLiter, 7), 7);	// ��ǥ��
			ss52DSt7.addString("transType", 
					hdWorkingMessage.getTransType(), 1);	// �ŷ�����(0: ����, 1: �ܻ�, 2: �̵��ī��)
			ss52DSt7.addString("cusType", 
					hdWorkingMessage.getCusType(), 1);		// ������(1: ������, 2: ������, 3: ����, 4:1ȸ����
															//         5: ������, 6: �����Է�, 7: ������ī��)
			ss52DSt7.addString("transStatus", 
					hdWorkingMessage.getTransStatus(), 1); 	// �ŷ�����->�ܻ�ŷ� ����(1: �ŷ���, 2: �ŷ�����, 3:�ŷ�����)
			ss52DSt7.addString("printBase", 
					hdWorkingMessage.getPrintBase(), 1);	// �ܰ���¿���(0: ��¾���, 1:���)
			ss52DSt7.addString("depositST", 
					hdWorkingMessage.getDepositST(), 1);	// ���������࿩��(1: �������, 2:����)
			ss52DSt7.addString("floatTR", 
					hdWorkingMessage.getFloatTR(), 1); 		// �Ҽ���ó�����(1: ����, 2:�ݿø�, 3:����)
			ss52DSt7.addString("receiptType", 
					hdWorkingMessage.getReceiptType(), 1); 	// ��꼭�ŷ�����(1: ��������, 2:���ݺ�����, 3:�������)
			ss52DSt7.addString("monthLimit", 
					GlobalUtility.appending0Pre(monthLimit, 18), 18);	// �ѵ�����
			ss52DSt7.addString("saveLimit", 
					GlobalUtility.appending0Pre(saveLimit, 18), 18);	// ��뷮
			ss52DSt7.addString("limitType", 
					hdWorkingMessage.getLimitType(), 1);	// �ѵ�����(1: ����, 2: �ݾ�)
			
			byte[] tempArray = ss52DSt7.getByteStream();
			
			returnMessage = this.makeProtocol(tempArray, 
								hdWorkingMessage.getNozzleNo());
		
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_HC)) {
			// WorkingMessage HC : �Ҹ���  ����/�ſ�/���ʽ� ����/��� ���� 
			// PrimeSelf ���� 52	 : �ܻ� �� ���� (trType 1,2,3,4,5,6,A,B,C,D,E,F,G,H,I) 
			HC_WorkingMessage hcWorkingMessage = (HC_WorkingMessage) workingMessage;
			hcWorkingMessage.print();			
			
			String generateScore 	= hcWorkingMessage.getGenerateScore(); 	// �߻�����
			String score 			= hcWorkingMessage.getScore(); 			// ��������
			String totalScore 		= hcWorkingMessage.getTotalScore();		// �Ѵ�������
			String storePoint 		= hcWorkingMessage.getStorePoint();		// �������� 
			String vanMsg			= hcWorkingMessage.getVanMsg().trim();	// ���޽���
			String bnsMsg			= hcWorkingMessage.getBnsMsg().trim();	// ���ʽ��޽���
			
			
			DataStruct ss52DS = new DataStruct();
			
			ss52DS.addString("commandID", "52", 2);
			ss52DS.addString("commandIndex", "1", 1);
			ss52DS.addString("odtNo", odtNo, 2);								//ODT ��ȣ
			ss52DS.addString("nozzleNo", connectNozzleNo, 2);					// �����ȣ
			ss52DS.addString("mode", hcWorkingMessage.getMode(), 1);			// MODE
			ss52DS.addString("trType", hcWorkingMessage.getTrType(), 1);		// ��������
			ss52DS.addString("authInfo", hcWorkingMessage.getAuthInfo(), 35);	// ��������
			ss52DS.addString("price", 
					hcWorkingMessage.getPrice(), 8);			// �ݾ�
			ss52DS.addString("authTime", 
					hcWorkingMessage.getAuthTime(), 15);		// ���νð�
			ss52DS.addString("authNo", 
					hcWorkingMessage.getAuthNo(), 20);			// ���ι�ȣ
			ss52DS.addString("cardNo", 
					hcWorkingMessage.getCardNo(), 40);			// ī���ȣ
			ss52DS.addString("cardCorpName", 
					hcWorkingMessage.getCardCorpName(), 30);	// ī���� 
			ss52DS.addString("noteCorpCode", 
					hcWorkingMessage.getNoteCorpCode(), 4);		// ��ǥ���Ի� �ڵ�
			ss52DS.addString("noteCorpName", 
					hcWorkingMessage.getNoteCorpName(), 30);	// ��ǥ���Ի��
			ss52DS.addString("noteNumber", 
					hcWorkingMessage.getNoteNumber(), 12);		// ��ǥ��ȣ
			ss52DS.addString("bonusAuthCode", 
					hcWorkingMessage.getBonusAuthCode(), 5);		// ���ʽ������ڵ�
			ss52DS.addString("bonusCardNumber", 
					hcWorkingMessage.getBonusCardNumber(), 40);		// ���ʽ�ī���ȣ 
			ss52DS.addString("bonusAuthTime", 
					hcWorkingMessage.getBonusAuthTime(), 15);		// ���ʽ� ���νð� 	
			ss52DS.addString("bonusAuthNumber", 
					hcWorkingMessage.getBonusAuthNumber(), 20);		// ���ʽ� ���ι�ȣ
			ss52DS.addString("dAuthNumber", 
					hcWorkingMessage.getDAuthNumber(), 20);			// ���ݿ����� ���ι�ȣ
			ss52DS.addString("generateScore", 
					this.removeZero(generateScore), 12);		// �߻�����
			ss52DS.addString("score", 
					this.removeZero(score), 12);				// ��������
			ss52DS.addString("totalScore", 
					this.removeZero(totalScore), 12);			// �Ѵ�������
			ss52DS.addString("storePoint", 
					this.removeZero(storePoint), 12);			// �������� 
			ss52DS.addString("publicMsg", 
					hcWorkingMessage.getPublicMsg(), 50);		// �޽���(tittle)
			ss52DS.addByte("FS1", IPumpConstant.DELIMITER_0X1C);
			ss52DS.addString("vanMsg", vanMsg, vanMsg.getBytes().length); // ���޽���
			ss52DS.addByte("FS2", IPumpConstant.DELIMITER_0X1C);
			ss52DS.addString("bnsMsg", bnsMsg, bnsMsg.getBytes().length); // ���ʽ��޽���
			ss52DS.addByte("FS3", IPumpConstant.DELIMITER_0X1C);
			ss52DS.addString("basePrice", 
					hcWorkingMessage.getBasePrice(), 6);		// �ܰ�(09/12/09 �߰�)

			byte[] tempArray = ss52DS.getByteStream();
			
			returnMessage = this.makeProtocol(tempArray, 
								hcWorkingMessage.getNozzleNo());
						
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_P1)) {
			// WorkingMessage P1 : ���� ������ ����� ���� ����
			// PrimeSelf 02       : ȯ�漳�� ���� 
			P1_WorkingMessage p1WorkingMessage = 
								(P1_WorkingMessage) workingMessage;
			
			String storeName 	= p1WorkingMessage.getStoreName();
			String repName 		= p1WorkingMessage.getRepName();
			String storeAdd1 	= p1WorkingMessage.getStoreADDR1();
			String storeAdd2 	= p1WorkingMessage.getStoreADDR2();
			String phone 		= p1WorkingMessage.getTel();
			String saMinAmt		= p1WorkingMessage.getSaMinAmt().trim();
			
			DataStruct ss02_01DS = new DataStruct();
			ss02_01DS.addString("command", "02", 2);
			ss02_01DS.addString("orderNo", "1", 1);
			ss02_01DS.addString("block", "2", 1);
			ss02_01DS.addString("fileNo", "01", 2);
			ss02_01DS.addString("fileLength", "235", 3);
			ss02_01DS.addString("storeCode", 
					p1WorkingMessage.getStoreCord(), 10);
			ss02_01DS.addString("regiNo", 
					p1WorkingMessage.getStoreRegiNum(), 12);
			ss02_01DS.addString("storeName", 
					this.subStringCheck(storeName, 40), 40);
			ss02_01DS.addString("repName", 
					this.subStringCheck(repName, 30), 30);
			ss02_01DS.addString("storePost", 
					p1WorkingMessage.getStorePost(), 7);
			ss02_01DS.addString("storeAdd1", 
					this.subStringCheck(storeAdd1, 50), 50);
			ss02_01DS.addString("storeAdd2", 
					this.subStringCheck(storeAdd2, 50), 50);
			ss02_01DS.addString("phone", this.subStringCheck(phone, 16), 16);
			ss02_01DS.addString("saMinAmt",	GlobalUtility.appending0Pre(saMinAmt, 10), 10);
			ss02_01DS.addString("bonusStoreCode", this.generateBlank(10), 10);
			
			byte[] tempArray = ss02_01DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
					p1WorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_P2)) {
			// WorkingMessage P2 : ���� ������ �Ӹ��� / ������ ���� 
			// PrimeSelf 02       : ȯ�漳�� ����
			P2_WorkingMessage p2WorkingMessage = 
						(P2_WorkingMessage) workingMessage;
			
			DataStruct ss02_02DS = new DataStruct();
			ss02_02DS.addString("command", "02", 2);
			ss02_02DS.addString("orderNo", "1", 1);
			ss02_02DS.addString("block", "2", 1);
			ss02_02DS.addString("fileNo", "02", 2);
			ss02_02DS.addString("fileLength", "000", 3);
			ss02_02DS.addString("head", 
					p2WorkingMessage.getBaseHeadTitle(), 50);
			ss02_02DS.addString("tail1", 
					p2WorkingMessage.getBaseFootTitle1(), 50);
			ss02_02DS.addString("tail2", 
					p2WorkingMessage.getBaseFootTitle2(), 50);
			
			byte[] tempArray = ss02_02DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
					p2WorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_P6)) {
			// WorkingMessage P6 : ������ �� �ð� ����
			// PrimeSelf 22       : �ð����� ���� 
			P6_WorkingMessage p6WorkingMessage = 
							(P6_WorkingMessage) workingMessage;
			
			String date = p6WorkingMessage.getSystemTime();
			
			DataStruct ss22DS = new DataStruct();
			
			ss22DS.addString("command", "22", 2);
			ss22DS.addString("orderNo", "1", 1);
			ss22DS.addString("date", "20" + date, 14);
			
			byte[] tempArray = ss22DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
								p6WorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_P5_1)) {
			// WorkingMessage P5_1 : ODT ȯ������ ���� 
			// PrimeSelf 02         : ȯ�漳�� ����
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
			returnMessage = this.makeProtocol(tempArray, 
								p5WorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PE)) {
			// WorkingMessage PE : ������ / ������ ���� ��û
			// PrimeSelf 95       : ������ �������� ��û 
			PE_WorkingMessage peWorkingMessage = 
							(PE_WorkingMessage) workingMessage;
			
			DataStruct ss95DS = new DataStruct();
			
			ss95DS.addString("command", "95", 2);
			ss95DS.addString("orderNo", "1", 1);
			ss95DS.addString("nozzleNo", connectNozzleNo, 2);
			
			byte[] tempArray = ss95DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
								peWorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PA)) {
			// WorkingMessage PA : ����������
			// PrimeSelf 50       : ������ ���� 
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
						new StringBuffer("# [TransPrimeSelf] # ")
						.append("PA WorkingMessage state Error # ")
						.append("Current state : ").append( state).append(" # ").toString());
				
			}	// end inner if
			
			byte[] tempArray = ss50DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
								paWorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_QD)) {
			// WorkingMessage QD : ���ʽ� ���� ���� 
			// PrimeSelf 56       : ������ ���� ���� 
			QD_WorkingMessage qdWorkingMessage = 
							(QD_WorkingMessage) workingMessage;
			
			DataStruct ss56DS = new DataStruct();
			
			ss56DS.addString("command", "56", 2);
			ss56DS.addString("nozzleNo", connectNozzleNo, 2);
			ss56DS.addString("bonusCardNumber", 
							qdWorkingMessage.getBonusCardNum(), 16);
			ss56DS.addString("accScore", qdWorkingMessage.getAccScore(), 8);
			
			byte[] tempArray = ss56DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
							qdWorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PB)) {
			// WorkingMessage PB : ���� / ���� ����
			// PrimeSelf 55       : �� ���� ���� 
			PB_WorkingMessage pbWorkingMessage = 
							(PB_WorkingMessage) workingMessage;
			
			DataStruct ss55DS = new DataStruct();
			
			ss55DS.addString("command", "55", 2);
			ss55DS.addString("orderNo", "1", 1);
			ss55DS.addString("nozzleNo", odtNo, 2);
			ss55DS.addString("mode", pbWorkingMessage.getCommandSet(), 1);
			ss55DS.addString("liter", pbWorkingMessage.getLiter(), 7);
			ss55DS.addString("basePrice", 
						pbWorkingMessage.getBasePrice().substring(0, 4), 4);
			ss55DS.addString("price", 
						pbWorkingMessage.getPrice().substring(2), 6);
			
			byte[] tempArray = ss55DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
							pbWorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_S3)) {
			// WorkingMessage S3 : ����/���� �� �ڷ�����(Process 202���� �ش� ODT�� �߰� �� ���)
			// PrimeSelf 51       : ���� ��, ���� �Ϸ� 
			S3_WorkingMessage s3WorkingMessage = (S3_WorkingMessage) workingMessage;
			
			DataStruct ss51DS = new DataStruct();
			
			ss51DS.addString("command", "51", 2);
			ss51DS.addString("orderNo", "1", 1);
			ss51DS.addString("nozzleNo", connectNozzleNo, 2);
			ss51DS.addString("state", "4", 1);
			ss51DS.addString("liter", s3WorkingMessage.getLiter(), 7);
			ss51DS.addString("basePrice", 
						s3WorkingMessage.getBasePrice().substring(0, 4), 4);
			ss51DS.addString("price", s3WorkingMessage.getPrice(), 6);
			
			byte[] tempArray = ss51DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
						s3WorkingMessage.getNozzleNo());
		
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_S4)) {
			// WorkingMessage S4 : �����Ϸ� �ڷ�����(Process 203���� �ش� ODT�� �߰� �� ���)
			// PrimeSelf 51       : ���� ��, ���� �Ϸ� 
			S4_WorkingMessage s4WorkingMessage = 
						(S4_WorkingMessage) workingMessage;
			
			DataStruct ss51DS = new DataStruct();
			
			ss51DS.addString("command", "51", 2);
			ss51DS.addString("orderNo", "1", 1);
			ss51DS.addString("nozzleNo", connectNozzleNo, 2);
			ss51DS.addString("state", "5", 1);
			ss51DS.addString("liter", s4WorkingMessage.getLiter(), 7);
			ss51DS.addString("basePrice", 
						s4WorkingMessage.getBasePrice().substring(0, 4), 4);
			ss51DS.addString("price", 
						s4WorkingMessage.getPrice().substring(2), 6);
			
			byte[] tempArray = ss51DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
						s4WorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_S8)) {
			// WorkingMessage S8 : ������ / ������ ���� ���� 
			// PrimeSelf 50       : ������ ���� 
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
			
		} else {
			LogUtility.getPumpALogger().error(
					"# [TransPrimeSelf] # " +
					"Not Supported command(" + 
					workingMessageCommand +
					") in TransPrimeSelf #");
			returnMessage = null;
		
		}	// end if
		
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
			// PrimeSelf 51 : ���� ��, ���� �Ϸ�
			// WorkingMessage S3 : ����/���� �� �ڷ�����
			// WorkingMessage S4 : ������ �����Ϸ� �ڷ�����
			DataStruct s51Interface = PrimeSelfDS.getDS("51");
			s51Interface.setByteStream(message);
			
			String state = (String) s51Interface.getValue("state");
			
			if (state.equals("4")) {
				// PrimeSelf 51 State 4 : ���� �� 
				S3_WorkingMessage s3WorkingMessage = new S3_WorkingMessage();
				s3WorkingMessage.setNozzleNo(odtNumber);
				s3WorkingMessage.setLiter((String) s51Interface.getValue("liter"));
				s3WorkingMessage.setPrice((String) s51Interface.getValue("price"));
				s3WorkingMessage.setBasePrice(
							(String) s51Interface.getValue("basePrice") + "00");
				s3WorkingMessage.setWDate(this.getSystemTime(6));	 
				
				returnMessage = s3WorkingMessage;
				
			} else if (state.equals("5")) {
				// PrimeSelf 51 State 5 : ���� �Ϸ�  
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
						new StringBuffer("### [TransPrimeSelf] ###\n" )
						.append("### Incorrect state in 51 Somo Self ###\n")
						.append("### Current state : ").append( state ).append("   ###").toString());
				returnMessage = null;
					
			}	// end inner if
			
		} else 	if (somoCommandString.equals("80")) {
			// PrimeSelf ���� 80 : ����/�ſ�/���ʽ�/�ܻ� ����/��� ��û 
			// WorkingMessage HA : �ܻ� ��
			// WorkingMessage HB : �ܻ� 
			DataStruct s80NInterface = PrimeSelfDS.getDS("80N");
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
			// PrimeSelf 50 		 : ������ ���� 
			// WorkingMessage PA : ���� ���� ���
			PA_WorkingMessage paWorkingMessage = new PA_WorkingMessage();
			paWorkingMessage.setNozzleNo(odtNumber);
			paWorkingMessage.setPassThrough(false);
			
			DataStruct s50Interface = PrimeSelfDS.getDS("50");
			s50Interface.setByteStream(message);
				
			String state 	= (String) s50Interface.getValue("state");
			String nozzleNo = (String) s50Interface.getValue("nozzleNo");
			
			paWorkingMessage.setTargetNozzleNo(nozzleNo);
				
			// PrimeSelf 50 State 70 : ��������(�������)
			if (state.equals("70")) {
				paWorkingMessage.setNozzleState("0");					
				
			// PrimeSelf 50 State 80 : ������� 
			} else if (state.equals("80")) {
				paWorkingMessage.setNozzleState("1");					
				
			} else {
				LogUtility.getPumpALogger().error(
						"# [TransPrimeSelf] # " +
						"Incorrect state in '50' Somo Self protocol # " +
						"Current state : " + state + " #");
				
			}	// end inner if
				
			returnMessage = paWorkingMessage;
			
		} else if (somoCommandString.equals("92")) {
			// PrimeSelf ���� 92 : �����㰡/�ǸſϷ� 
			// WorkingMessage PB : �����㰡 ��û 
			// WorkingMessage TR : �ǸſϷ� 
			int dupI = this.getDup(message);
			DataStruct s92NInterface = PrimeSelfDS.getDS("92N", dupI);
			s92NInterface.setByteStream(message);

			String odtNo			= (String) s92NInterface.getValue("odtNo");
			String targetNozzleNo 	= (String) s92NInterface.getValue("nozzleNo");
			String divKey 	= (String) s92NInterface.getValue("divKey");
			
			if (divKey.equals("1"))	{
				// divKey = 1 (�����㰡 ��û) 
				PB_WorkingMessage pbWorkingMessage = new PB_WorkingMessage();
				String commandSet = (String)s92NInterface.getValue("command");

				if (commandSet.equals("2")) {
					// PrimeSelf 2 (���� ����) = PB_WorkingMessage 0
					commandSet = "0";
					
				} else if (commandSet.equals("0")) {
					// PrimeSelf 0 (Free ����) = PB_WorkingMessage 2
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
						"# [TransPrimeSelf] # " +
						"Incorrect divKey in '92' Somo Self protocol # " +
						"Current divKey : " + divKey + " #");
			}
			
		} else if (somoCommandString.equals("93")) {
			// PrimeSelf 93 		 : ������ �������/����  
			// WorkingMessage PA : ���� ���� ���
			PA_WorkingMessage paWorkingMessage = new PA_WorkingMessage();
			DataStruct s93Interface = PrimeSelfDS.getDS("93");
			s93Interface.setByteStream(message);
			
			String nozzleNumber = (String) s93Interface.getValue("nozzleNo");
				
			paWorkingMessage.setNozzleNo(odtNumber);
			paWorkingMessage.setConnectNozzleNo(nozzleNumber);
			paWorkingMessage.setTargetNozzleNo(nozzleNumber);
			paWorkingMessage.setNozzleState((String) s93Interface.getValue("mode"));					
				
			returnMessage = paWorkingMessage;
/*
 * 
 * StatusCode ����(POS�� ����) �� �۾�.
 * 
		} else if (somoCommandString.equals("91")) {
			// PrimeSelf 91 		 : ���� ���� ����   
			// WorkingMessage S8 : 
			S8_WorkingMessage s8WorkingMessage = new S8_WorkingMessage();
			String statusCode = "651";
			byte statusB;
			DataStruct s91Interface = PrimeSelfDS.getDS("91");
			s91Interface.setByteStream(message);
			
			statusB = (Byte) s91Interface.getValue("status");
			
			if (statusB == 0x15) {
				 // ���� �Է� 
				statusCode = "";
				
			} else if (statusB == 0x17) {
				// ��� ���� 
				statusCode = "";
				
			} else if (statusB == 0x18) {
				// �����ݾ� / ������ ���� ���
				statusCode = "";
				
			} else if (statusB == 0x19) {
				// �ſ�ī�� ���Դ�� 
				statusCode = "";
				
			} else if (statusB == 0x20) {
				// ���ʽ� ī�� ���Դ�� 
				statusCode = "";
				
			} else if (statusB == 0x21) {
				// ������� 
				statusCode = "";
				
			} else {

			}	// end inner if
			
			s8WorkingMessage.setNozzleNo(odtNumber);
			s8WorkingMessage.setDeviceType("05");
			s8WorkingMessage.setStatus("0");
			s8WorkingMessage.setStatusCode(statusCode);
			s8WorkingMessage.setNozzleState("0");					
				
			returnMessage = s8WorkingMessage;
*/			
		} else if (somoCommandString.equals("81")) {
			// PrimeSelf 81 		 : ����̽� ���� ����
			// WorkingMessage SE : ���� ����̽� �̻����� ����
			SE_WorkingMessage seWorkingMessage = new SE_WorkingMessage();
			seWorkingMessage.setNozzleNo(odtNumber);
				
			DataStruct s81Interface = PrimeSelfDS.getDS("81");
			s81Interface.setByteStream(message);
				
			String errorCode = (String) s81Interface.getValue("errorCode");
			String status    = (String) s81Interface.getValue("status");
			
			seWorkingMessage.setDeviceType("05");
			seWorkingMessage.setStatus(status);
			seWorkingMessage.setErrMsg(this.generateBlank(20));
	
			if (status.equals("0")) {
				// status 0 : ���� ȸ��
				
				// PrimeSelf 81 ErrorCode 00 : ����
				if (errorCode.equals("00")) {
					seWorkingMessage.setStatusCode("251");
					seWorkingMessage.setErrMsg("����");
						
				// PrimeSelf 81 ErrorCode 14 : ���������� ���� 
				} else if (errorCode.equals("14")){
					seWorkingMessage.setStatusCode(PRINTER_ERROR);
					seWorkingMessage.setErrMsg("������ ���� - ����");
						
				// PrimeSelf 81 ErrorCode 16 : ���������� ��������
				} else if (errorCode.equals("16")){
					seWorkingMessage.setStatusCode(PRINTER_PAPER);
					seWorkingMessage.setErrMsg("������ �������� - ����");
						
				// PrimeSelf 81 ErrorCode 10 : ���� Busy ���� 
				} else if (errorCode.equals("10")){
					seWorkingMessage.setStatusCode(VOICE_BUSY);
					seWorkingMessage.setErrMsg("������ġ Busy - ����");	
					
				// PrimeSelf 81 ErrorCode 11 : ���� ���� 
				} else if (errorCode.equals("11")){
					seWorkingMessage.setStatusCode(VOICE_ERROR);
					seWorkingMessage.setErrMsg("������ġ �̻� - ����");
						
				} else {
					LogUtility.getPumpALogger().error(
							"### [TransPrimeSelf] ###\n" +
							"### Incorrect error code ###\n" +
							"### Current error code : " + errorCode + " ###");
					
				}	
				
			} else if (status.equals("1")) {
				// status 1 : ���� �߻� 
				
				// PrimeSelf 81 ErrorCode 00 : ����
				if (errorCode.equals("00")) {
					seWorkingMessage.setStatusCode("251");
					seWorkingMessage.setErrMsg("����");
						
				// PrimeSelf 81 ErrorCode 14 : ���������� ���� 
				} else if (errorCode.equals("14")){
					seWorkingMessage.setStatusCode(E_PRINTER_ERROR);
					seWorkingMessage.setErrMsg("������ ����");
						
				// PrimeSelf 81 ErrorCode 16 : ���������� ��������
				} else if (errorCode.equals("16")){
					seWorkingMessage.setStatusCode(E_PRINTER_PAPER);
					seWorkingMessage.setErrMsg("������ ��������");
						
				// PrimeSelf 81 ErrorCode 10 : ���� Busy ���� 
				} else if (errorCode.equals("10")){
					seWorkingMessage.setStatusCode(E_VOICE_BUSY);
					seWorkingMessage.setErrMsg("������ġ Busy");	
					
				// PrimeSelf 81 ErrorCode 11 : ���� ���� 
				} else if (errorCode.equals("11")){
					seWorkingMessage.setStatusCode(E_VOICE_ERROR);
					seWorkingMessage.setErrMsg("������ġ �̻�");
						
				} else {
					LogUtility.getPumpALogger().error(
							"# [TransPrimeSelf] # " +
							"Incorrect error code # " +
							"Current error code : " + errorCode + " #");
					
				}
				
			} else {
				LogUtility.getPumpALogger().error(
						"# [TransPrimeSelf] # " +
						"Status Error. # " +
						"Current status : " + status + " #");
				
			}	// end inner if
				
			seWorkingMessage.setDetectTime(this.getSystemTime(12));
			seWorkingMessage.setVersion(this.generateBlank(9));
				
			returnMessage = seWorkingMessage;

		} else if (somoCommandString.equals("90")) {
			// PrimeSelf 90 		 : ���� / ���� ���� ��û(���� ����)
			// WorkingMessage F0 : ���� / ���� ���� ��û(���� ����)
			F0_WorkingMessage f0WorkingMessage = new F0_WorkingMessage();
			
			DataStruct s90Interface = PrimeSelfDS.getDS("90");
			s90Interface.setByteStream(message);
			
			String connectNozzleNo = (String) s90Interface.getValue("nozzleNo");
			
			f0WorkingMessage.setNozzleNo(odtNumber);
			f0WorkingMessage.setConnectNozzleNo(connectNozzleNo);
			f0WorkingMessage.setTargetNozzleNo(connectNozzleNo);
			f0WorkingMessage.setFlag((String) s90Interface.getValue("mode"));
			
			returnMessage = f0WorkingMessage;
			
		} else if (somoCommandString.equals("S3")) {
			// PrimeSelf S3 		 : ���� / ���� ���� ��û(���� ����)
			// WorkingMessage S3 : ���� / ���� ���� ��û(���� ����)
			S3_WorkingMessage s3WorkingMessage = new S3_WorkingMessage();
			
			DataStruct s3Interface = PrimeSelfDS.getDS("S3");
			s3Interface.setByteStream(message);
			
			s3WorkingMessage.setNozzleNo(odtNumber);
			s3WorkingMessage.setLiter((String) s3Interface.getValue("liter"));
			s3WorkingMessage.setPrice((String) s3Interface.getValue("price"));
			s3WorkingMessage.setBasePrice(
						(String) s3Interface.getValue("basePrice"));
			s3WorkingMessage.setWDate(this.getSystemTime(6));	 
			
			returnMessage = s3WorkingMessage;
			
		} else if (somoCommandString.equals("S4")) {
			// PrimeSelf S4 		 : ���� / ���� ���� ��û(���� ����)
			// WorkingMessage S4 : ���� / ���� ���� ��û(���� ����)
			S4_WorkingMessage s4WorkingMessage = new S4_WorkingMessage();
			
			DataStruct s4Interface = PrimeSelfDS.getDS("S4");
			s4Interface.setByteStream(message);
			
			s4WorkingMessage.setNozzleNo(odtNumber);
			s4WorkingMessage.setFlag("0");		// ���� ��(FLAG)
			s4WorkingMessage.setLiter(
					(String) s4Interface.getValue("liter"));
			s4WorkingMessage.setBasePrice(
					(String) s4Interface.getValue("basePrice"));
			s4WorkingMessage.setPrice((String) s4Interface.getValue("price"));
			s4WorkingMessage.setWDate(this.getSystemTime(6));
			s4WorkingMessage.setSystemTime(this.getSystemTime(12));
			s4WorkingMessage.setTotalGauge((String) s4Interface.getValue("totalGauge"));
			s4WorkingMessage.setStatusFlag("0");	// ���� ��(STATUS_FLAG)
			
			returnMessage = s4WorkingMessage;
			
		} else if (somoCommandString.equals("SJ")) {
			// PrimeSelf SJ 		 : 
			// WorkingMessage SJ : 
			SJ_WorkingMessage sjWorkingMessage = new SJ_WorkingMessage();
			
			DataStruct sjInterface = PrimeSelfDS.getDS("SJ");
			sjInterface.setByteStream(message);
			
			sjWorkingMessage.setNozzleNo(odtNumber);
			sjWorkingMessage.setTotalGauge((String) sjInterface.getValue("totalGauge"));
			sjWorkingMessage.setSystemTime(this.getSystemTime(12));
			
			returnMessage = sjWorkingMessage;
			
		} else if (somoCommandString.equals("82")) {
			// PrimeSelf 82		 : 
			// WorkingMessage BI : 
			BI_WorkingMessage biWorkingMessage = new BI_WorkingMessage();
			
			DataStruct p82Interface = PrimeSelfDS.getDS("82");
			p82Interface.setByteStream(message);
			
			biWorkingMessage.setNozzleNo((String) p82Interface.getValue("odtNumber"));
			biWorkingMessage.setConnectNozzleNo((String) p82Interface.getValue("nozzleNumber"));
			biWorkingMessage.setCash((String) p82Interface.getValue("cash"));
			biWorkingMessage.setCashCount((String) p82Interface.getValue("cashCount"));
			biWorkingMessage.setTime((String) p82Interface.getValue("time"));
			
			returnMessage = biWorkingMessage;
			
		} else {
			LogUtility.getPumpALogger().error(
					"# [TransPrimeSelf] # " +
					"Not Supported command(" + somoCommandString +
					") in TransPrimeSelf #");
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
					"# [TransPrimeSelf] # " +
					"ODT number error in TransPrimeSelf # " +
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
		
		returnData[returnDataCounter++] = Command.STX;					// STX
		returnData[returnDataCounter++] = (byte) (nozzleByte + 0x3F);	// SA
		returnData[returnDataCounter++] = blank;						// UA
		
		for (int i = 0; i < data.length; i++) {
			returnData[returnDataCounter++] = data[i];
			
		}	// end for
		
		returnData[returnDataCounter++] = Command.ETX;					// ETX
		returnData[returnDataCounter++] = blank;						// BCC		
		
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
	
}