package com.gsc.kixxhub.device.pumpa.devDatas;

import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.DataStruct;

/**
 * @author yd
 *
 */
class S02_DataStruct extends DataStruct{
	/**
	 * @param block
	 */
	public S02_DataStruct(String block) throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("block", 1);
		this.setString("fileNo", 2);
		
		if (block == SomoSelfDS.REGI_INFO) {
			// ������ ���, ����� ����
			this.setString("storeCode", 10);
			this.setString("regiNo", 12);
			this.setString("storeName", 40);
			this.setString("repName", 30);
			this.setString("storePost", 7);
			this.setString("storeAdd1", 50);
			this.setString("storeAdd2", 50);
			this.setString("phone", 16);
			this.setString("saMinAmt", 10);
			this.setString("bonusStoreCode", 10);

		} else if (block == SomoSelfDS.HEADER) {
			// ������ ���, �Ӹ���/������
			this.setString("head", 50);
			this.setString("tail1", 50);
			this.setString("tail2", 50);
			
		} else if (block == SomoSelfDS.NOZZLE_INFO) {
			// ������ ���, ������ ����
			this.setString("nozzleNo", 2);		
			this.setString("basePrice", 4);
			this.setString("goodsCode", 4);		
			this.setString("goodsType", 14);	
			
		} else {
			LogUtility.getPumpALogger().debug("##### Incorrect data block" +
					" in Somo Self '02' #####");
		}	// end if
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C02_DataStruct


/**
 * @author yd
 *
 */
class S22_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public S22_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("date", 14);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C22_DataStruct


/**
 * @author yd
 *
 */
class S50_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public S50_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("flag", 1);
		this.setString("nozzleNo", 2);
		this.setString("state", 2);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C50_DataStruct


/**
 * @author yd
 *
 */
class S51_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public S51_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("nozzleNo", 2);
		this.setString("state", 1);
		this.setString("liter", 7);
		this.setString("basePrice", 4);
		this.setString("price", 6);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C51_DataStruct


/**
 * @author yd
 *
 */
class S52_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public S52_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("flag", 1);			// �÷���
		this.setString("nozzleNo", 2);		// �����ȣ
		this.setString("length", 4);		// ����
		this.setString("cardNo", 18);		// ī���ȣ
		this.setString("carInfo", 18);		// ��������
		this.setString("oilCode", 4);		// �����ڵ� 
		this.setString("name", 26);			// �����ڸ�
		this.setString("liter", 7);			// �Ǹż���
		this.setString("basePrice", 6);		// �ǸŴܰ�
		this.setString("jpLiter", 7);		// ��ǥ��
		this.setString("transType", 1);		// �ŷ�����
		this.setString("customerType", 1);	// ������
		this.setString("transStatus", 1);	// �ŷ�����
		this.setString("printBase", 1);		// �ܰ���¿���
		this.setString("deposite", 1);		// ���������࿩��
		this.setString("float", 1);			// �Ҽ���ó�����
		this.setString("receipt", 1);		// ��꼭�ŷ�����
		this.setString("monthLimit", 10);	// �ѵ�����
		this.setString("saveLimit", 10);	// ��뷮
		this.setString("limitType", 1);		// ����,�ݾ� 
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C52_DataStruct


/**
 * @author yd
 *
 */
class S54_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public S54_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("error", 1);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C54_DataStruct


/**
 * @author yd
 *
 */
class S55_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public S55_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("nozzleNo", 2);
		this.setString("mode", 1);
		this.setString("liter", 7);
		this.setString("basePrice", 4);
		this.setString("price", 6);

		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C55_DataStruct


/**
 * @author yd
 *
 */
class S56_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public S56_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("nozzleNo", 2);
		this.setString("bonusNumber", 16);
		this.setString("storeScore", 8);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end S56_DataStruct 


/**
 * @author yd
 *
 */
class S61_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public S61_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("fileNo", 1);
		this.setString("mode", 1);
		this.setString("status", 20);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C61_DataStruct


/**
 * @author yd
 *
 */
class S80_DataStruct extends DataStruct{
	/**
	 * @param type
	 */
	public S80_DataStruct(String type) throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo1", 1);
		this.setString("orderNo2", 2);
		this.setString("screenNo", 2);
		this.setString("dataType", 1);
		
		if (type == SomoSelfDS.STOP) {
			// ������ Ÿ�� '0'(�Է� ����)
			// ����
		} else if (type == SomoSelfDS.KEY){
			// ������ Ÿ�� '1'(Ű �Է�)
			this.setString("function", 2);
			this.setString("section", 2);
			this.setString("nozzleNo", 2);
			this.setString("price", 7);
		} else if (type == SomoSelfDS.CARD){
			// ������ Ÿ�� '2'(ī�� ������)
			this.setString("flag", 1);
			this.setString("nozzleNo", 2);
			this.setString("length", 4);
			this.setString("cardType", 1);		// ī��Ÿ��
			this.setString("cardNo", 40);		// ī���ȣ
			this.setString("bonusCard", 40);	// ���ʽ�ī���ȣ
			this.setString("liter", 7);			// ����
			this.setString("basePrice", 4);		// �ܰ�
			this.setString("price", 8);			// �ݾ�
			this.setString("cDate", 14);		// �ſ�ī����νð�
			this.setString("cNumber", 12);		// �ſ�ī����ι�ȣ
			this.setString("bDate", 14);		// ���ʽ��������νð�
			this.setString("bonusNo", 12);		// ���ʽ��������ι�ȣ
			this.setString("dNo", 12);			// ���ݿ��������ι�ȣ
		} else {
			LogUtility.getPumpALogger().debug("##### Incorrect data type" +
			 							" in Somo Self '80' #####");
		}	// end if
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C80_DataStruct


/**
 * 
 * @author Mckelain
 *
 */
class S80N_DataStruct extends DataStruct{
	/**
	 * �����Ҹ��� �ű� ����
	 */
	public S80N_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("command_index", 1);
		this.setString("odtNo", 2);
		this.setString("nozzleNo", 2);
		this.setString("trType", 1);
		this.setString("authType", 2);
		this.setString("cardNum", 40);
		this.setString("bonusCard", 40);
		this.setString("liter", 7);
		this.setString("basePrice", 6);
		this.setString("price", 8);
		this.setString("c_number", 20);
		this.setString("b_number", 20);
		this.setString("d_number", 20);
		this.setString("pin", 8);
		
		this.setByte("etx");
		this.setByte("bcc");
		
	}
}

/**
 * @author yd
 *
 */
class S81_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public S81_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("status", 1);
		this.setString("deviceNo", 2);
		this.setString("errorCode", 2);
		this.setString("length", 2);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C81_DataStruct

/**
 * @author dhp
 *
 */
class S82_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public S82_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command_id", 2);
		this.setString("command_index", 1);
		this.setString("odtNo", 2);
		this.setString("nozzleNo", 2);
		this.setString("cash", 8);
		this.setString("cashCount", 8);
		this.setString("time", 14);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C81_DataStruct

/**
 * @author yd
 *
 */
class S90_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public S90_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("nozzleNo", 2);
		this.setString("mode", 1);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C90_DataStruct


/**
 * @author yd
 *
 */
class S91_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public S91_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("nozzleNo", 2);
		this.setByte("status");
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C91_DataStruct


/**
 * @author yd
 *
 */
class S92_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public S92_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("nozzleNo", 2);				// �����ȣ
		this.setString("mode", 1);					// Mode
		this.setString("approvalType", 2);			// ��������
		this.setString("bonusType", 2);				// ���ʽ�ī������
		this.setString("bonusCode", 5);				// ���ʽ�ī������ڵ�
		this.setString("authInfo", 34);				// ��������
		this.setString("liter", 7);					// ����
		this.setString("basePrice", 4);				// �ܰ�
		this.setString("price", 8);					// �ݾ�
		this.setString("authTime", 14);				// ���νð�
		this.setString("authNo", 12);				// ���ι�ȣ
		this.setString("cardNo", 16);				// ī���ȣ
		this.setString("cardCorpCode", 4);			// �߱޻��ڵ��ȣ
		this.setString("cardCorpName", 16);			// �߱޻��
		this.setString("noteCode", 4);				// ���Ի��ڵ��ȣ
		this.setString("noteName", 16);				// ���Ի��
		this.setString("noteNo", 5);				// ��ǥ��ȣ
		this.setString("bonusNo", 16);				// ���ʽ�ī���ȣ
		this.setString("bonusTime", 14);			// ���ʽ����νð�
		this.setString("bonusAuthNo", 12);			// ���ʽ����ι�ȣ
		this.setString("dAuthNo", 12);				// ���ݿ��������ι�ȣ
		this.setString("bonusGenerateScore", 8);	// ���ʽ��߻�����
		this.setString("bonusScore", 8);			// ���ʽ���������
		this.setString("bonusTotalScore", 8);		// ���ʽ��Ѵ�������
		this.setString("message", 100);				// �޽���
		this.setString("vanMessage", 100);			// Van�� �޽��� 
		this.setString("bonusMessage", 100);		// ���ʽ� �޽��� 
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C92_DataStruct

/**
 * 
 * @author Mckelain
 *
 */
class S92N_DataStruct extends DataStruct{
	/**
	 * �����Ҹ��� �ű� ����
	 */
	public S92N_DataStruct(int trTypeSize) throws Exception {
	
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command_id", 2);
		this.setString("command_index", 1);
		this.setString("odtNo", 2);
		this.setString("nozzleNo", 2);
		this.setString("divKey", 1);
		this.setString("command", 1);
		this.setString("cardNo", 40);
		this.setString("bonusCardNo", 40);
		this.setString("dup", 2);
		
		for (int i = 0; i < trTypeSize; i++) {
			this.setString("trType" + i, 1);
			this.setString("mode" + i, 1);
			this.setString("liter" + i, 7);
			this.setString("basePrice" + i, 6);
			this.setString("price" + i, 8);
			
		}	// end for
		
		this.setByte("etx");
		this.setByte("bcc");
		
	}
}
/**
 * @author yd
 *
 */
class S93_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public S93_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("nozzleNo", 2);
		this.setString("mode", 1);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C93_DataStruct


/**
 * @author yd
 *
 */
class S94_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public S94_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("nozzleNo", 2);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C94_DataStruct



/**
 * @author yd
 *
 */
class S95_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public S95_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("nozzleNo", 2);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C95_DataStruct



public class SomoSelfDS {
	/**
	 * 
	 */
	public static final String CARD = "6";	// ������ Ÿ�� '2'
	/**
	 * 
	 */
	public static final String HEADER      = "2";	// ������ ���, �Ӹ���/������
	/**
	 * 
	 */
	public static final String KEY  = "5";	// ������ Ÿ�� '1'
	
	/**
	 * 
	 */
	public static final String NOZZLE_INFO = "3";	// ������ ���, ������ ����
	// "02" ����ڵ�(���� ������)���� ��ϱ������� ���
	/**
	 * 
	 */
	public static final String REGI_INFO   = "1";	// ������ ���, ����� ����
	// "80" ����ڵ�(�Է� ���� ����)���� ���
	/**
	 * 
	 */
	public static final String STOP = "4";	// ������ Ÿ�� '0'
	
	

	/**
	 * �Ҹ� ���� �������� ���� DataStruct�� ��´�. 
	 * 
	 * @param command	: ��� �ڵ� 
	 * @return
	 */
	public static DataStruct getDS(String command) {
		DataStruct returnData = null;
		
		try {
			if (command.equals("22")) {
				returnData = new S22_DataStruct();
			} else if (command.equals("50")) {
				returnData = new S50_DataStruct();
			} else if (command.equals("51")) {
				returnData = new S51_DataStruct();
			} else if (command.equals("52")) {
				returnData = new S52_DataStruct();
			} else if (command.equals("54")) {
				returnData = new S54_DataStruct();
			} else if (command.equals("55")) {
				returnData = new S55_DataStruct();
			} else if (command.equals("56")) {
				returnData = new S56_DataStruct();
			} else if (command.equals("61")) {
				returnData = new S61_DataStruct();
			} else if (command.equals("81")) {
				returnData = new S81_DataStruct();
			} else if (command.equals("82")) {
				returnData = new S82_DataStruct();
			} else if (command.equals("90")) {
				returnData = new S90_DataStruct();
			} else if (command.equals("91")) {
				returnData = new S91_DataStruct();
			} else if (command.equals("92")) {
				returnData = new S92_DataStruct();
			} else if (command.equals("93")) {
				returnData = new S93_DataStruct();
			} else if (command.equals("94")) {
				returnData = new S94_DataStruct();
			} else if (command.equals("95")) {
				returnData = new S95_DataStruct();
			} else if (command.equals("80N")) {
				returnData = new S80N_DataStruct();
			} else {
				returnData = null;
			} // end if
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return returnData;
		
	}	// end getDS
	
	
	
	public static DataStruct getDS(String command, int trTypeSize) {
		DataStruct returnData = null;
		
		try {
			if (command.equals("92N")) {
				returnData = new S92N_DataStruct(trTypeSize);
			} else {
				returnData = null;
			}	// end if
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return returnData;
		
	}	// end getDS
	
	
	
	/**
	 * ODT �������� ����ڵ� "02", "80" ���� DataStruct�� ��´�. 
	 * 
	 * @param command		: ��� �ڵ� 
	 * @param parameter1 	: ������ ���(02 ����), ������ Ÿ��(80 ����)
	 * @return
	 */
	public static DataStruct getDS(String command, String parameter1) throws Exception {
		DataStruct returnData = null;
		
		if (command.equals("02")) {
			returnData = new S02_DataStruct(parameter1);
		} else if (command.equals("80")) {
			returnData = new S80_DataStruct(parameter1);
		} else {
			returnData = null;
		}	// end if
		
		return returnData;
	}	// end getDS
}