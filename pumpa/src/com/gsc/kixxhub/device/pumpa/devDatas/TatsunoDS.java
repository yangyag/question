package com.gsc.kixxhub.device.pumpa.devDatas;

import com.gsc.kixxhub.device.pumpa.common.DataStruct;

/**
 * @author yd
 *
 */
class OT00_DataStruct extends DataStruct{
	/**
	 * @param direction
	 */
	public OT00_DataStruct(boolean direction) throws Exception {
		this.setByte("stx");
		this.setByte("ua");
		
		this.setByte("command");
		if (direction == TatsunoDS.FROM_HUB) {
			// 없음
		} else  if (direction == TatsunoDS.FROM_PUMP) {
			this.setByte("liter0");
			this.setByte("liter1");
			this.setByte("liter2");
			this.setByte("liter3");
			this.setByte("liter4");
			this.setByte("basePrice0");
			this.setByte("basePrice1");
			this.setByte("basePrice2");
			this.setByte("basePrice3");
			this.setByte("basePrice4");
			this.setByte("price0");
			this.setByte("price1");
			this.setByte("price2");
			this.setByte("price3");
			this.setByte("price4");
			
		}	// end if
		
		this.setByte("lrc");
	}
}	// end OT00_DataStruct


/**
 * @author yd
 *
 */
class OT01_DataStruct extends DataStruct{
	/**
	 * @param direction
	 */
	public OT01_DataStruct(boolean direction) throws Exception {
		this.setByte("stx");
		this.setByte("ua");
		
		this.setByte("command");
		if (direction == TatsunoDS.FROM_HUB) {
			// 없음
		} else  if (direction == TatsunoDS.FROM_PUMP) {
			// 없음
		}	// end if
		
		this.setByte("lrc");
	}
}	// end OT01_DataStruct


/**
 * @author yd
 *
 */
class OT02_DataStruct extends DataStruct{
	/**
	 * @param direction
	 */
	public OT02_DataStruct(boolean direction) throws Exception {
		this.setByte("stx");
		this.setByte("ua");
		
		this.setByte("command");
		if (direction == TatsunoDS.FROM_HUB) {
			this.setByte("liter0");
			this.setByte("liter1");
			this.setByte("liter2");
			this.setByte("liter3");
			this.setByte("liter4");
			this.setByte("basePrice0");
			this.setByte("basePrice1");
			this.setByte("basePrice2");
			this.setByte("basePrice3");
			this.setByte("basePrice4");
			this.setByte("price0");
			this.setByte("price1");
			this.setByte("price2");
			this.setByte("price3");
			this.setByte("price4");
		} else  if (direction == TatsunoDS.FROM_PUMP) {
			this.setByte("liter0");
			this.setByte("liter1");
			this.setByte("liter2");
			this.setByte("liter3");
			this.setByte("liter4");
		}	// end if
		
		this.setByte("lrc");
	}
}	// end OT02_DataStruct


/**
 * @author yd
 *
 */
class OT05_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public OT05_DataStruct() throws Exception {
		this.setByte("command");
		
		this.setByte("ua");
		
		this.setByte("mode");
		this.setByte("plk1");
		this.setByte("plk2");		
		
		this.setByte("lrc");
	}
}	// end OT05_DataStruct


/**
 * @author yd
 *
 */
class OT06_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public OT06_DataStruct() throws Exception {
		this.setByte("command");
		
		this.setByte("ua");
		
		this.setByte("dst");
		this.setByte("notUse");
		
		this.setByte("lrc");
	}
}	// end OT06_DataStruct


/**
 * @author yd
 *
 */
class OT10_DataStruct extends DataStruct{
	/**
	 * @param direction
	 */
	public OT10_DataStruct(boolean direction) throws Exception {
		this.setByte("stx");
		this.setByte("ua");
		
		this.setByte("command");
		if (direction == TatsunoDS.FROM_HUB) {
			this.setByte("basePrice0");
			this.setByte("basePrice1");
			this.setByte("basePrice2");
			this.setByte("basePrice3");
			this.setByte("basePrice4");
		} else  if (direction == TatsunoDS.FROM_PUMP) {
			this.setByte("liter0");
			this.setByte("liter1");
			this.setByte("liter2");
			this.setByte("liter3");
			this.setByte("liter4");
			this.setByte("basePrice0");
			this.setByte("basePrice1");
			this.setByte("basePrice2");
			this.setByte("basePrice3");
			this.setByte("basePrice4");
			this.setByte("price0");
			this.setByte("price1");
			this.setByte("price2");
			this.setByte("price3");
			this.setByte("price4");
		}	// end if
		
		this.setByte("lrc");
	}
}	// end OT10_DataStruct


/**
 * @author yd
 *
 */
class OT20_DataStruct extends DataStruct{
	/**
	 * @param direction
	 */
	public OT20_DataStruct(boolean direction) throws Exception {
		this.setByte("stx");
		this.setByte("ua");
		
		this.setByte("command");
		if (direction == TatsunoDS.FROM_HUB) {
			this.setByte("octane");
		} else  if (direction == TatsunoDS.FROM_PUMP) {
			this.setByte("octane");
			this.setByte("liter0");
			this.setByte("liter1");
			this.setByte("liter2");
			this.setByte("liter3");
			this.setByte("liter4");
			this.setByte("price0");
			this.setByte("price1");
			this.setByte("price2");
			this.setByte("price3");
			this.setByte("price4");
		}	// end if
		
		this.setByte("lrc");
	}
}	// end OT20_DataStruct


/**
 * @author yd
 *
 */
class OT24_DataStruct extends DataStruct{
	/**
	 * @param direction
	 */
	public OT24_DataStruct(boolean direction) throws Exception {
		this.setByte("stx");
		this.setByte("ua");
		
		this.setByte("command");
		if (direction == TatsunoDS.FROM_HUB) {
			this.setByte("octane");
			this.setByte("liter0");
			this.setByte("liter1");
			this.setByte("liter2");
			this.setByte("liter3");
			this.setByte("liter4");
			this.setByte("price0");
			this.setByte("price1");
			this.setByte("price2");
			this.setByte("price3");
			this.setByte("price4");
		} else  if (direction == TatsunoDS.FROM_PUMP) {
			this.setByte("octane");
			this.setByte("liter0");
			this.setByte("liter1");
			this.setByte("liter2");
			this.setByte("liter3");
			this.setByte("liter4");
			this.setByte("price0");
			this.setByte("price1");
			this.setByte("price2");
			this.setByte("price3");
			this.setByte("price4");
		}	// end if
		
		this.setByte("lrc");
	}
}	// end OT24_DataStruct


public class TatsunoDS {
	/**
	 * 
	 */
	public static final boolean FROM_HUB  = true;
	/**
	 * 
	 */
	public static final boolean FROM_PUMP = false;
	
	
	/**
	 * 다쓰노 구형 프로토콜 전문 DataStruct를 얻는다. 
	 * 
	 * @param command	: 명령 코드 
	 * @param direction	: 전송 방향
	 * @return
	 */
	public static DataStruct getDS(String command, boolean direction) {
		DataStruct returnData = null;
		
		try {
			if (command.equals("05H")) {
				returnData = new OT05_DataStruct();

			} else if (command.equals("06H")) {
				returnData = new OT06_DataStruct();

			} else if (command.equals("00H")) {
				returnData = new OT00_DataStruct(direction);

			} else if (command.equals("01H")) {
				returnData = new OT01_DataStruct(direction);

			} else if (command.equals("02H")) {
				returnData = new OT02_DataStruct(direction);

			} else if (command.equals("10H")) {
				returnData = new OT10_DataStruct(direction);

			} else if (command.equals("20H")) {
				returnData = new OT20_DataStruct(direction);

			} else if (command.equals("24H")) {
				returnData = new OT24_DataStruct(direction);

			} else {
				returnData = null;

			} // end if
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return returnData;
		
	}	// end getDS	
	
}