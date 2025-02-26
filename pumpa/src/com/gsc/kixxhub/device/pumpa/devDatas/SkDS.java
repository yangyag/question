package com.gsc.kixxhub.device.pumpa.devDatas;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.device.pumpa.common.DataStruct;

/**
 * @author yd
 *
 */
class AP_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public AP_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("nozzleNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		
		this.setByte("etx");
		this.setByte("lrc");
	}
}	// end AP_DataStruct

/**
 * @author yd
 *
 */
class AQ_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public AQ_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("nozzleNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		
		this.setByte("etx");
		this.setByte("lrc");
	}
}	// end AQ_DataStruct



/**
 * @author yd
 *
 */
class CQ_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public CQ_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("nozzleNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		
		this.setByte("etx");
		this.setByte("lrc");
	}
}	// end CQ_DataStruct



/**
 * @author yd
 *
 */
class CT_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public CT_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("nozzleNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("totalGauge", 10);
		
		this.setByte("etx");
		this.setByte("lrc");
	}
}	// end CT_DataStruct



/**
 * @author yd
 *
 */
class IN_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public IN_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("nozzleNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		
		this.setByte("etx");
		this.setByte("lrc");
	}
}	// end IN_DataStruct



/**
 * @author yd
 *
 */
class KH_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public KH_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("nozzleNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		
		this.setByte("etx");
		this.setByte("lrc");
	}
}	// end KH_DataStruct



/**
 * @author yd
 *
 */
class LK_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public LK_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("nozzleNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		
		this.setByte("etx");
		this.setByte("lrc");
	}
}	// end LK_DataStruct



/**
 * @author yd
 *
 */
class PC_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public PC_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("nozzleNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("basePrice", 4);
		
		this.setByte("etx");
		this.setByte("lrc");
	}
}	// end PC_DataStruct



/**
 * @author yd
 *
 */
class PP_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public PP_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("nozzleNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("liter", 7);
		this.setString("price", 7);
		
		this.setByte("etx");
		this.setByte("lrc");
	}
}	// end PP_DataStruct



/**
 * @author yd
 *
 */
class RT_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public RT_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("nozzleNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		
		this.setByte("etx");
		this.setByte("lrc");
	}
}	// end RT_DataStruct



public class SkDS {

	public static DataStruct getDS(String command) {
		DataStruct returnData = null;
		
		try {
			if (command.equals(IPumpConstant.SK_COMMAND_AQ)) {
				returnData = new AQ_DataStruct();

			} else if (command.equals(IPumpConstant.SK_COMMAND_PP)) {
				returnData = new PP_DataStruct();

			} else if (command.equals(IPumpConstant.SK_COMMAND_LK)) {
				returnData = new LK_DataStruct();

			} else if (command.equals(IPumpConstant.SK_COMMAND_UL)) {
				returnData = new UL_DataStruct();

			} else if (command.equals(IPumpConstant.SK_COMMAND_TR)) {
				returnData = new TR_DataStruct();

			} else if (command.equals(IPumpConstant.SK_COMMAND_CT)) {
				returnData = new CT_DataStruct();

			} else if (command.equals(IPumpConstant.SK_COMMAND_PC)) {
				returnData = new PC_DataStruct();

			} else if (command.equals(IPumpConstant.SK_COMMAND_AP)) {
				returnData = new AP_DataStruct();

			} else if (command.equals(IPumpConstant.SK_COMMAND_ST)) {
				returnData = new ST_DataStruct();

			} else if (command.equals(IPumpConstant.SK_COMMAND_KH)) {
				returnData = new KH_DataStruct();

			} else if (command.equals(IPumpConstant.SK_COMMAND_RT)) {
				returnData = new RT_DataStruct();

			} else if (command.equals(IPumpConstant.SK_COMMAND_IN)) {
				returnData = new IN_DataStruct();

			} else if (command.equals(IPumpConstant.SK_COMMAND_TQ)) {
				returnData = new TQ_DataStruct();

			} else if (command.equals(IPumpConstant.SK_COMMAND_CQ)) {
				returnData = new CQ_DataStruct();

			} else {
				returnData = null;

			} // end if
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}	
		
		return returnData;
		
	}	// end getDS
	
}



/**
 * @author yd
 *
 */
class ST_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public ST_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("nozzleNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("mode", 1);
		this.setString("amount", 7);
		
		this.setByte("etx");
		this.setByte("lrc");
	}
}	// end ST_DataStruct



/**
 * @author yd
 *
 */
class TQ_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public TQ_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("nozzleNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		
		this.setByte("etx");
		this.setByte("lrc");
	}
}	// end TQ_DataStruct



/**
 * @author yd
 *
 */
class TR_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public TR_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("nozzleNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("basePrice", 4);
		this.setString("liter", 7);
		this.setString("price", 7);
		
		this.setByte("etx");
		this.setByte("lrc");
	}
}	// end TR_DataStruct



/**
 * @author yd
 *
 */
class UL_DataStruct extends DataStruct{
	/**
	 * 
	 */
	public UL_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("nozzleNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		
		this.setByte("etx");
		this.setByte("lrc");
	}
}	// end _DataStruct