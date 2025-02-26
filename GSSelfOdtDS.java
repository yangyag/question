package com.gsc.kixxhub.device.pumpa.devDatas;

import com.gsc.kixxhub.device.pumpa.common.Change;
import com.gsc.kixxhub.device.pumpa.common.DataStruct;

/*
* ������Ʈ �� : PI2
* �Ͻ� : 2016.03.15
* �ű�
* @author ������
* */

//-- BC : �������� �������(ODT -> SC)
class GS_BC_DataStruct extends DataStruct {

	public GS_BC_DataStruct() throws Exception {
		
		this.setString("Command", 2);		
		this.setString("DeviceNo", 2);
		this.setString("ConnectDevNo", 2); 	
		this.setString("CashCount", 8);		// ��ұݾ�
		this.setString("Time", 14);			// ���Խð� 	
	}
}

//-- BI : ������������(ODT -> SC)
class GS_BI_DataStruct extends DataStruct {

	public GS_BI_DataStruct() throws Exception {
		
		this.setString("Command", 2);		
		this.setString("DeviceNo", 2);
		this.setString("ConnectDevNo", 2); 	
		this.setString("Cash", 8);		// ���Աݾ�
		this.setString("CashCount", 8);		// ���� �հ�ݾ�
		this.setString("Time", 14);			// ���Խð� 	
		
	}
}

//-- BR : ������ ���ڵ� ��û (ODT -> SC)
class GS_BR_DataStruct extends DataStruct {

	public GS_BR_DataStruct() throws Exception {
		
		/* * control ����*/
		this.setString("Command", 2);
		this.setString("DeviceNo", 2);
		this.setString("ConnectDevNo", 2);
//			body ���� 
		this.setString("Price", 8);
		this.setString("PosReceiptNo", 14);
		
	}
}

//-- CA : �ŷ�ó �� ����Ȯ�� ��û (ODT -> SC)
class GS_CA_DataStruct extends DataStruct {

	public GS_CA_DataStruct() throws Exception {
		
		this.setString("Command", 2);		
		this.setString("DeviceNo", 2);
		this.setString("ConnectDevNo", 2); 	
		this.setVString("CustomerNo", 40);	// ��ī���ȣ
		
	}
}


//-- GA : ���� ���� ��û (SC <- ODT) --//	
class GS_GA_DataStruct extends DataStruct {

	public GS_GA_DataStruct() throws Exception {
		
		/* * control ����*/
		this.setString("Command", 2);
		this.setString("DeviceNo", 2);
		this.setString("ConnectDevNo", 2);
		/* control ��*/
		/* datas ���� ���� 
		 * ���׸��� �������̸� ���´�.*/
		this.setVString("MessageType", 4);
		this.setVString("CardNumber", 100);
		this.setVString("BonusCard", 100);
		this.setVString("CustCardNo", 100);
		this.setVString("CashReceiptNo", 100);
		this.setVString("Liter", 7);
		this.setVString("BasePrice", 6);
		this.setVString("Price", 8);
		this.setVString("LedCode", 1);
		this.setVString("CreatedTime", 14);
		this.setVString("UPosByte", 0);
		
	}
}

/*
*  �������� ó��
*/

//-- GT : �ǸſϷ� ó�� (������ ��� �� ����)(ODT -> SC)
class GS_GT_DataStruct extends DataStruct {

	public GS_GT_DataStruct() throws Exception {
		
		this.setString("Command", 2);
		this.setString("DeviceNo", 2);
		this.setString("ConnectDevNo", 2);
		this.setString("Liter", 7);
		this.setString("BasePrice", 6);
		this.setString("Price", 8);
		this.setString("CreatedTime", 14);
	}
}

/**
* 
* @author ������
* ǥ�� ����ODT �������̽� ����
*
*/

/*
*  �ʱ�ȭ �۾�
*/

//--  PM : ODT ������� (SC <- ODT) --//	
class GS_PM_DataStruct extends DataStruct {

	public GS_PM_DataStruct() throws Exception {
		
		this.setString("Command", 2);
		this.setString("DeviceNo", 2);
		this.setString("ConnectDevNo", 2);
		this.setString("Mode", 1);
	}
}


//--  PV : ODT �������� (SC <- ODT) --//	
class GS_PV_DataStruct extends DataStruct {

	public GS_PV_DataStruct() throws Exception {
		
		this.setString("Command", 2);
		this.setString("DeviceNo", 2);
		this.setString("ConnectDevNo", 2);
		this.setString("Version", 4);
	}
}

//-- S3 : ���� ��  �ڷ�����(ODT -> SC)
class GS_S3_DataStruct extends DataStruct {

	public GS_S3_DataStruct() throws Exception {
		
		this.setString("Command", 2);		
		this.setString("DeviceNo", 2);		// �����ȣ
		this.setString("ConnectDevNo", 2); 	// ODT NO
		this.setString("Liter", 7);			// ���� 4.3
		this.setString("BasePrice", 6);		// �ܰ� 
		this.setString("Price", 8);			// ���� �ݾ�
		this.setString("WDate", 6);			// ������(YYMMDD)
		
	}
}

//-- S4 :  ���� �Ϸ� �ڷ�����(������)(ODT -> SC)
class GS_S4_DataStruct extends DataStruct {

	public GS_S4_DataStruct() throws Exception {
		
		this.setString("Command", 2);		
		this.setString("DeviceNo", 2);
		this.setString("ConnectDevNo", 2); 	
		
		this.setString("Liter", 7);			// ���� 4.3
		this.setString("BasePrice", 6);		// �ܰ� 	
		this.setString("Price", 8);			// ���� �ݾ�	
		this.setString("WDate", 6);			// ������(YYMMDD)
		this.setString("SystemTime", 12);	// �ý��۽ð�
		this.setString("TotalGauge", 10);	// ��Ż ������(7.3)
		this.setString("StatusFlag", 1);	// ����(0:����, 1: ������)
	}
}


//-- S8 : ������ / ODT ���� ����(ODT -> SC)
class GS_S8_DataStruct extends DataStruct {

	public GS_S8_DataStruct() throws Exception {
		
		this.setString("Command", 2);		
		this.setString("DeviceNo", 2);
		this.setString("ConnectDevNo", 2); 	
		this.setString("DeviceType", 1);	// ����̽� Ÿ��
		this.setString("Status", 3);	// ������ / ODT ���� �ڵ�	
		
	}
}

//-- SE : ���� ����̽� �̻����� ����(ODT -> SC)
class GS_SE_DataStruct extends DataStruct {

	public GS_SE_DataStruct() throws Exception {
		
		this.setString("Command", 2);		
		this.setString("DeviceNo", 2);
		this.setString("ConnectDevNo", 2); 	
		this.setString("DeviceType", 1);	// ����̽� Ÿ��
		this.setString("Status", 1);		// ���� 	
		this.setString("StatusCode", 3);	// ������ / ODT ���� �ڵ�	
		this.setString("ErrMsg", 20);		// �����޼���
		this.setString("DetectTime", 12);	// ����ð�(YYMMDDhhmmss)
		
	}
}

//-- SJ : ����������  TotalGauge ���� (ODT -> SC)
class GS_SJ_DataStruct extends DataStruct {

	public GS_SJ_DataStruct() throws Exception {
		
		/* * control ����*/
		this.setString("Command", 2);
		this.setString("DeviceNo", 2);
		this.setString("ConnectDevNo", 2);
		this.setString("SystemTime", 12);
		this.setString("TotalGauge", 10);
		
	}
}



public class GSSelfOdtDS {
	
	/**
	 * �������� ���� DataStruct�� ��´�. 
	 * 
	 * @param command	: ��� �ڵ� 
	 * @return
	 */
	
	
	public static DataStruct getDS(String command) {
		DataStruct returnData = null;
		
		try {
			if (command.equals("PM")) {
				returnData = new GS_PM_DataStruct();
			} else if (command.equals("PV")) {
				returnData = new GS_PV_DataStruct();
			} else if (command.equals("GA")) {
				returnData = new GS_GA_DataStruct();
			} else if (command.equals("GT")) {
				returnData = new GS_GT_DataStruct();
			} else if (command.equals("BI")) {
				returnData = new GS_BI_DataStruct();
			} else if (command.equals("BC")) {
				returnData = new GS_BC_DataStruct();
			} else if (command.equals("S3")) {
				returnData = new GS_S3_DataStruct();   
			} else if (command.equals("S4")) {
				returnData = new GS_S4_DataStruct();
			} else if (command.equals("SE")) {
				returnData = new GS_SE_DataStruct();
			} else if (command.equals("S8")) {
				returnData = new GS_S8_DataStruct();
			} else if (command.equals("CA")) {
				returnData = new GS_CA_DataStruct();
			} else if (command.equals("BR")) {
				returnData = new GS_BR_DataStruct();
			} else if (command.equals("SJ")) {
				returnData = new GS_SJ_DataStruct();
			}else {
				returnData = null;
			} // end if
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return returnData;
		
	}
	
	/*
	 *  ODT_NO ��ȯ : POS -> ODT
	 *  71 (POS) -> S1 (ODT)
	 */
	public static String getOdtNo_forODT (String odtNo) {
		
		int nOdtNo = Integer.parseInt(odtNo) - 70;
		
		return "S" + Change.binToHex (nOdtNo);
	}
	
	/*
	 *  ODT_NO ��ȯ : ODT -> POS
	 *  S1 (ODT) -> 71 (POS)
	 */
	public static String getOdtNo_forPOS (String odtNo) {
		
		String odtStr = odtNo.substring(1,2);
				
		return Change.toString(70 + Change.hexToBin (odtStr));
	}	
}

