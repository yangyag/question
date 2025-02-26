package com.gsc.kixxhub.device.pumpa.devDatas;

import com.gsc.kixxhub.device.pumpa.common.DataStruct;

public class TatsunoMppDS {
	/**
	 * 다쓰노 초고속 프로토콜 전문 DataStruct를 얻는다. 
	 * 
	 * @param command	: 명령 코드 
	 * @return
	 */
	public static DataStruct getDS(String command) {
		DataStruct returnData = null;
		
		try {
			if (command.equals("11")) {
				returnData = new TM11_DataStruct();

			} else if (command.equals("12")) {
				returnData = new TM12_DataStruct();

			} else if (command.equals("13")) {
				returnData = new TM13_DataStruct();

			} else if (command.equals("14")) {
				returnData = new TM14_DataStruct();

			} else if (command.equals("15")) {
				returnData = new TM15_DataStruct();

			} else if (command.equals("20")) {
				returnData = new TM20_DataStruct();

			} else if (command.equals("60")) {
				returnData = new TM60_DataStruct();

			} else if (command.equals("61")) {
				returnData = new TM61_DataStruct();

			} else if (command.equals("62")) {
				returnData = new TM62_DataStruct();

			} else {
				returnData = null;

			} // end if
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return returnData;
		
	}	// end getDS
	
	
	
	/**
	 * 다쓰노 초고속 프로토콜 명령코드 "65" 전문 DataStruct를 얻는다.
	 * 
	 * @param command		: 명령 코드 
	 * @param nozzleCount	: 노즐 개수 
	 * @return
	 */
	public static DataStruct getDS(String command, int nozzleCount) throws Exception {
		DataStruct returnData = null;
		
		if (command.equals("65")) {
			returnData = new TM65_DataStruct(nozzleCount);
			
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
class TM11_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public TM11_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("condition", 1);
		this.setString("preset", 1);
		this.setString("price", 6);

		for (int i = 0; i < 6; i++) {
			this.setString("flag" + i, 1);
			this.setString("basePrice" + i, 4);	
		}
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C10_DataStruct


/**
 * @author yd
 *
 */
class TM12_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public TM12_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		// 없음
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C12_DataStruct


/**
 * @author yd
 *
 */
class TM13_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public TM13_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		// 없음
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C13_DataStruct


/**
 * @author yd
 *
 */
class TM14_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public TM14_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		// 없음
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C14_DataStruct


/**
 * @author yd
 *
 */
class TM15_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public TM15_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		// 없음
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C15_DataStruct


/**
 * @author yd
 *
 */
class TM20_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public TM20_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		// 없음
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C20_DataStruct


/**
 * @author yd
 *
 */
class TM60_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public TM60_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("situation", 1);
		this.setString("content", 1);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C11_DataStruct


/**
 * @author yd
 *
 */
class TM61_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public TM61_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("status", 1);
		this.setString("liter", 6);
		this.setString("basePriceFlag", 1);
		this.setString("basePrice", 4);
		this.setString("price", 6);
		this.setString("nozzle", 1);
		this.setString("flag", 1);
		this.setString("type", 1);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C61_DataStruct


/**
 * @author yd
 *
 */
class TM62_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public TM62_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("code", 2);
		this.setString("situation", 1);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C62_DataStruct


/**
 * @author yd
 *
 */
class TM65_DataStruct extends DataStruct{
	/**
	 * @param nozzleCount
	 */
	public TM65_DataStruct(int nozzleCount) throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("n1", 1);
		this.setString("n2", 1);
		this.setString("n3", 1);
		this.setString("spare", 3);
		
		for (int i = 0; i < nozzleCount; i++) {
			this.setString("liter" + i, 10);
			this.setString("price" + i, 10);
			
		}	// end for
		
		this.setByte("etx");
		this.setByte("bcc");
		
	}
	
}	// end C65_DataStruct


