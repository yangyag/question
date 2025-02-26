package com.gsc.kixxhub.device.pumpa.devDatas;

import com.gsc.kixxhub.device.pumpa.common.DataStruct;

/**
 * @author yd
 *
 */
class C0_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public C0_DataStruct() throws Exception {
		this.setByte("sa");
		this.setByte("stx");
		this.setString("nozzleNo", 2);
		
		this.setString("command", 2);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end c0_DataStruct

/**
 * @author yd
 *
 */
class E0_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public E0_DataStruct() throws Exception {
		this.setByte("sa");
		this.setByte("stx");
		this.setString("nozzleNo", 2);
		
		this.setString("command", 2);
		this.setString("mode",1);
		this.setString("liter", 7);
		this.setString("basePrice", 4);
		this.setString("price", 6);

		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end e0_DataStruct


/**
 * @author yd
 *
 */
class P0_DataStruct extends DataStruct{
	/**
	 * @param direction
	 */
	public P0_DataStruct(boolean direction) throws Exception {
		this.setByte("sa");
		this.setByte("stx");
		this.setString("nozzleNo", 2);
		
		this.setString("command", 2);

		if (direction == TatsunoDS.FROM_HUB) {
			// 없음 
		} else if (direction == TatsunoDS.FROM_PUMP) {
			this.setString("mode", 1);
			this.setString("liter", 7);
			this.setString("basePrice", 4);
			this.setString("price", 6);
		}	// end if
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end p0_DataStruct



/**
 * @author yd
 *
 */
class Q0_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public Q0_DataStruct() throws Exception {
		this.setByte("sa");
		this.setByte("stx");
		this.setString("nozzleNo", 2);
		
		this.setString("command", 1);
		this.setString("code", 2);

		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end Q0_DataStruct



/**
 * @author yd
 *
 */
class S0_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public S0_DataStruct() throws Exception {
		this.setByte("sa");
		this.setByte("stx");
		this.setString("nozzleNo", 2);
		
		this.setString("command", 2);
		this.setString("mode", 1);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end s0_DataStruct


/**
 * @author yd
 *
 */
class T0_DataStruct extends DataStruct{
	/**
	 * @param direction
	 */
	public T0_DataStruct(boolean direction) throws Exception {
		this.setByte("sa");
		this.setByte("stx");
		this.setString("nozzleNo", 2);
		
		this.setString("command", 2);
		
		if (direction == TatsunoDS.FROM_HUB) {
			// 없음 
		} else if (direction == TatsunoDS.FROM_PUMP){
			this.setString("totalGauge", 10);
		}	// end if
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}	// end t0_DataStruct


public class WoojooDS {
	/**
	 * 
	 */
	public static final boolean FROM_HUB  = true;
	/**
	 * 
	 */
	public static final boolean FROM_PUMP = false;
	
	
	/**
	 * 우주 프로토콜 명령코드 "T4" 전문 DataStruct를 얻는다.
	 * 
	 * @param command	: 명령 코드 
	 * @param direction	: 전송 방향
	 * @return
	 */
	public static DataStruct getDS(String command, boolean direction) {
		DataStruct returnData = null;
		
		try {
			if (command.equals("S0")) {
				returnData = new S0_DataStruct();

			} else if (command.equals("E0")) {
				returnData = new E0_DataStruct();

			} else if (command.equals("C0")) {
				returnData = new C0_DataStruct();

			} else if (command.equals("P0")) {
				returnData = new P0_DataStruct(direction);

			} else if (command.equals("T0")) {
				returnData = new T0_DataStruct(direction);
				
			} else if (command.equals("Q0")) {
				returnData = new Q0_DataStruct();

			} else {
				returnData = null;

			} // end if
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return returnData;
		
	}	// end getDS
	
}
