package com.gsc.kixxhub.device.pumpa.devDatas;

import com.gsc.kixxhub.device.pumpa.common.DataStruct;

/**
 * @author yd
 *
 */
class NT10_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public NT10_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("condition", 1);
		this.setString("preset", 1);
		this.setString("price", 6);
		this.setString("flag", 1);
		this.setString("basePrice", 4);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C10_DataStruct



/**
 * @author yd
 *
 */
class NT12_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public NT12_DataStruct() throws Exception {
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
class NT13_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public NT13_DataStruct() throws Exception {
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
class NT14_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public NT14_DataStruct() throws Exception {
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
class NT15_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public NT15_DataStruct() throws Exception {
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
class NT20_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public NT20_DataStruct() throws Exception {
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
class NT60_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public NT60_DataStruct() throws Exception {
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
class NT61_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public NT61_DataStruct() throws Exception {
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
class NT62_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public NT62_DataStruct() throws Exception {
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
class NT65_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public NT65_DataStruct() throws Exception {
		this.setByte("stx");
		this.setByte("sa");
		this.setByte("ua");
		
		this.setString("command", 2);
		this.setString("n1", 1);
		this.setString("spare", 5);
		this.setString("liter", 10);
		this.setString("price", 10);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end C65_DataStruct


public class TatsunoNDS {
	/**
	 * 다쓰노 신형 프로토콜 전문 DataStruct를 얻는다. 
	 * 
	 * @param command	: 명령 코드 
	 * @return
	 */
	public static DataStruct getDS(String command) {
		DataStruct returnData = null;
		
		try {
			if (command.equals("10")) {
				returnData = new NT10_DataStruct();

			} else if (command.equals("12")) {
				returnData = new NT12_DataStruct();

			} else if (command.equals("13")) {
				returnData = new NT13_DataStruct();

			} else if (command.equals("14")) {
				returnData = new NT14_DataStruct();

			} else if (command.equals("15")) {
				returnData = new NT15_DataStruct();

			} else if (command.equals("20")) {
				returnData = new NT20_DataStruct();

			} else if (command.equals("60")) {
				returnData = new NT60_DataStruct();

			} else if (command.equals("61")) {
				returnData = new NT61_DataStruct();

			} else if (command.equals("62")) {
				returnData = new NT62_DataStruct();

			} else if (command.equals("65")) {
				returnData = new NT65_DataStruct();

			} else {
				returnData = null;

			} // end if
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return returnData;
		
	}	// end getDS
	
}


