package com.gsc.kixxhub.device.pumpa.devDatas;

import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.DataStruct;

public class TatsunoSelfDS {
	/**
	 * 
	 */
	public static final int CARD  = 4;
	/**
	 * 
	 */
	public static final int HEADER      = 6;	// ������ ���, �Ӹ���/������
	/**
	 * 
	 */
	public static final int HUMAN = 3;
	/**
	 * 
	 */
	public static final int KEY  = 9;	// ������ Ÿ�� '1'
	// ������ Ÿ�� '2'�� TatsunoSelfDS.CARD�� ����Ѵ�.
	/**
	 * 
	 */
	public static final int LAMP  = 1;
	
	/**
	 * 
	 */
	public static final int NOZZLE_INFO = 7;	// ������ ���, ������ ����
	// "02" ����ڵ�(���� ������)���� ��ϱ������� ���
	/**
	 * 
	 */
	public static final int REGI_INFO   = 5;	// ������ ���, ����� ����
	// "80" ����ڵ�(�Է� ���� ����)���� ���
	/**
	 * 
	 */
	public static final int STOP = 8;	// ������ Ÿ�� '0'
	
	// "42", "82" ����ڵ忡�� ����̽� �������� ��� 
	/**
	 * 
	 */
	public static final int TIMER = 0;
	/**
	 * 
	 */
	public static final int VOICE = 2;
	
	/**
	 * �پ��� ���� �������� ���� DataStruct�� ��´�. 
	 * 
	 * @param command	: ��� �ڵ� 
	 * @return
	 */
	public static DataStruct getDS(String command) {
		DataStruct returnData = null;
		
		try {
			if (command.equals("01")) {
				returnData = new TS01_DataStruct();

			} else if (command.equals("20")) {
				returnData = new TS20_DataStruct();

			} else if (command.equals("23")) {
				returnData = new TS23_DataStruct();

			} else if (command.equals("24")) {
				returnData = new TS24_DataStruct();

			} else if (command.equals("40")) {
				returnData = new TS40_DataStruct();

			} else if (command.equals("41")) {
				returnData = new TS41_DataStruct();

			} else if (command.equals("43")) {
				returnData = new TS43_DataStruct();

			} else if (command.equals("60")) {
				returnData = new TS60_DataStruct();

			} else if (command.equals("61")) {
				returnData = new TS61_DataStruct();

			} else if (command.equals("81")) {
				returnData = new TS81_DataStruct();

			} else {
				returnData = null;

			} // end if
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return returnData;
		
	}	// end getDS
	
	
	
	/**
	 * �پ��� ���� �������� ����ڵ� "T4" ���� DataStruct�� ��´�.
	 * 
	 * @param command	: ��� �ڵ� 
	 * @param parameter	: ����̽� No(42, 82 ����), Block(02 ����), 
	 * 					  ������ Ÿ��(80 ����)
	 * @return
	 */
	public static DataStruct getDS(String command, int parameter) throws Exception {
		DataStruct returnData = null;
		
		if (command.equals("42")) {
			returnData = new TS42_DataStruct(parameter);	
		
		} else if (command.equals("82")) {
			returnData = new TS82_DataStruct(parameter);
		
		} else if (command.equals("02")) {
			returnData = new TS02_DataStruct(parameter);
		
		} else if (command.equals("80")) {
			returnData = new TS80_DataStruct(parameter);
		
		} else {
			returnData = null;
		
		}	// end if
		
		return returnData;		
		
	}	// end getDS
	
}



/**
 * @author yd
 *
 */
class TS01_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public TS01_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("fileNo", 20);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end TS01_DataStruct


/**
 * @author yd
 *
 */
class TS02_DataStruct extends DataStruct{
	/**
	 * @param block
	 */
	public TS02_DataStruct(int block) throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("block", 1);
		this.setString("fileNo", 2);
		
		if (block == TatsunoSelfDS.REGI_INFO) {
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

		} else if (block == TatsunoSelfDS.HEADER) {
			// ������ ���, �Ӹ���/������
			this.setString("head", 50);
			this.setString("tail1", 50);
			this.setString("tail2", 50);
			
		} else if (block == TatsunoSelfDS.NOZZLE_INFO) {
			// ������ ���, ������ ����
			this.setString("nozzleNo", 2);		// ���� ��
			this.setString("basePrice", 4);
			this.setString("goodsCode", 4);		// ���� ��
			this.setString("goodsType", 30);	// ���� ��
			
		} else {
			LogUtility.getPumpALogger().debug("##### Incorrect data block" +
					" in Somo Self '02' #####");
		}	// end if
		
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end TS02_DataStruct


/**
 * @author yd
 *
 */
class TS20_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public TS20_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("control", 1);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end TS20_DataStruct


/**
 * @author yd
 *
 */
class TS22_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public TS22_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("date", 14);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end TS22_DataStruct


/**
 * @author yd
 *
 */
class TS23_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public TS23_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("screen", 2);
		this.setString("writeScreen", 1);
		this.setString("screenData", 245);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end TS23_DataStruct


/**
 * @author yd
 *
 */
class TS24_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public TS24_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("alarm", 1);
		this.setString("reserve", 1);
		this.setString("error", 20);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end TS24_DataStruct


/**
 * @author yd
 *
 */
class TS40_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public TS40_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo1", 1);
		this.setString("orderNo2", 2);
		this.setString("keyGroupID", 2);
		this.setString("reserve1", 2);
		this.setString("orderCondition", 1);
		this.setString("inputCondition", 4);
		this.setString("inputSize", 2);
		this.setString("reserve2", 1);
		this.setString("orderLength", 2);
		// ���� ����, �������� �����ϱ� ���� �� �ڸ��� ���� 
		this.setString("orderName", 2);
		this.setString("defaultLength", 2);
		// ����Ʈ ���� 
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end TS40_DataStruct


/**
 * @author yd
 *
 */
class TS41_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public TS41_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end TS41_DataStruct


/**
 * @author yd
 *
 */
class TS42_DataStruct extends DataStruct{
	/**
	 * @param deviceNo
	 */
	public TS42_DataStruct(int deviceNo) throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("deviceNo", 2);
		this.setString("deviceCommand", 2);
		
		if (deviceNo == TatsunoSelfDS.TIMER) {
			this.setString("timer", 3);			
		} else if (deviceNo == TatsunoSelfDS.LAMP) {
			this.setString("status", 8);
			this.setString("reserve", 16);
		} else if (deviceNo == TatsunoSelfDS.VOICE) {
			this.setString("condition", 1);
			this.setString("timer", 2);
			this.setString("voiceNo", 2);
		} else if (deviceNo == TatsunoSelfDS.HUMAN) {
			
		} else if (deviceNo == TatsunoSelfDS.CARD) {

		} else {
			LogUtility.getPumpALogger().debug("### Incorrect device number " +
												"in tatsuno self 42");
		}	// end if
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end TS42_DataStruct


/**
 * @author yd
 *
 */
class TS43_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public TS43_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end TS43_DataStruct


/**
 * @author yd
 *
 */
class TS60_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public TS60_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("status", 2);
		this.setString("count", 3);
		this.setString("fileNo", 2);
		this.setString("fileName", 8);
		this.setString("extension", 3);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end TS60_DataStruct


/**
 * @author yd
 *
 */
class TS61_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public TS61_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("mode", 1);
		this.setString("fileStatus", 20);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end TS61_DataStruct


/**
 * @author yd
 *
 */
class TS80_DataStruct extends DataStruct{
	/**
	 * @param type
	 */
	public TS80_DataStruct(int type) throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo1", 1);
		this.setString("orderNo2", 2);
		this.setString("screenNo", 2);
		this.setString("type", 1);

		if (type == TatsunoSelfDS.STOP) {
			// ������ Ÿ�� '0'(�Է� ����)
			// ����
		} else if (type == TatsunoSelfDS.KEY) {
			// ������ Ÿ�� '1'(Ű �Է�)
			this.setString("function", 2);
			this.setString("numberInput", 2);
			this.setString("inputData", 164);	// �Էµ�����
			// (type(1)/ī���ȣ(40)/���ʽ�ī���ȣ(40)/����(7)/�ܰ�(4)/�ݾ�(8)/�ſ�ī����νð�(14)/
			// �ſ�ī����ι�ȣ(12)/���ʽ��������νð�(14)/���ʽ��������ι�ȣ(12)/���ݿ��������ι�ȣ(12))
			
		} else if (type == TatsunoSelfDS.CARD) {
			// ������ Ÿ�� '2'(�ڱ�ī�� �Է�)
			this.setString("reserve", 80);
			
			this.setString("status2", 2);
			this.setString("length2", 2);
			this.setString("cardData2", 76);
			this.setString("status1", 2);
			this.setString("length1", 2);
			this.setString("cardData1", 76);
			
			
		} else {

		}
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end TS80_DataStruct


/**
 * @author yd
 *
 */
class TS81_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public TS81_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("status", 1);
		this.setString("deviceNo", 2);
		this.setString("errorCode", 2);
		this.setString("dataLength", 2);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end TS81_DataStruct


/**
 * @author yd
 *
 */
class TS82_DataStruct extends DataStruct{
	/**
	 * @param deviceNo
	 */
	public TS82_DataStruct(int deviceNo) throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("orderNo", 1);
		this.setString("deviceNo", 2);
		this.setString("reqCommand", 2);
		
		if (deviceNo == TatsunoSelfDS.TIMER) {
			this.setString("state", 2);			
		} else if (deviceNo == TatsunoSelfDS.LAMP) {
			this.setString("state", 2);
		} else if (deviceNo == TatsunoSelfDS.VOICE) {
			this.setString("state", 2);
		} else if (deviceNo == TatsunoSelfDS.HUMAN) {
			
		} else if (deviceNo == TatsunoSelfDS.CARD) {
			this.setString("status", 2);
			this.setString("state", 1);
		} else {
			LogUtility.getPumpALogger().debug("### Incorrect device number " +
											"in tatsuno self 82");
		}	// end if
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end TS81_DataStruct


