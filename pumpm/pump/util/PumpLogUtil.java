package com.gsc.kixxhub.module.pumpm.pump.util;

import com.gsc.kixxhub.common.data.IConstant;
import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.posdata.POSHeader;
import com.gsc.kixxhub.common.data.pump.BB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.D0_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.GA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.GB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.GL_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.GR_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HC_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HE_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HF_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.M1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P2_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P3_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P5_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P6_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P7_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P8_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PE_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PF_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PG_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PI_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PP_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PQ_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.QF_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.QL_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.QM_WoringMessage;
import com.gsc.kixxhub.common.data.pump.S3_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S4_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S5_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S8_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S9_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SE_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SF_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SG_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SH_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SI_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SJ_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.ST_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.TJ_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.data.pump.XA_WorkingMessage;
import com.gsc.kixxhub.common.data.upos.UPOSMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.common.utility.sync.SyncManager;

public class PumpLogUtil {
	
	public static void printContent(Object obj) {
		try {
			if (obj == null) {
				LogUtility.getPumpMLogger().info("[Pump M] Obj is null") ;
			} else  {
	    		if (obj instanceof UPOSMessage) {
	    			//((UPOSMessage)obj).print() ;
	    		} else if (obj instanceof WorkingMessage) {
	    			if (obj instanceof D0_WorkingMessage) {
						  ((D0_WorkingMessage)obj).print() ;
					  } else if (obj instanceof HE_WorkingMessage) {
						  ((HE_WorkingMessage)obj).print() ;
					  } else if (obj instanceof GA_WorkingMessage) {
						  ((GA_WorkingMessage)obj).print() ;
	    			  } else if (obj instanceof HC_WorkingMessage) {
						  ((HC_WorkingMessage)obj).print() ;
					  } else if (obj instanceof HB_WorkingMessage) {
						  ((HB_WorkingMessage)obj).print() ;
					  } else if (obj instanceof HA_WorkingMessage) {
						  ((HA_WorkingMessage)obj).print() ;
					  } else if (obj instanceof PA_WorkingMessage) {
						  ((PA_WorkingMessage)obj).print() ;
					  } else if (obj instanceof P8_WorkingMessage) {
						  ((P8_WorkingMessage)obj).print() ;
					  } else if (obj instanceof P6_WorkingMessage) {
						  ((P6_WorkingMessage)obj).print() ;
					  } else if (obj instanceof P5_WorkingMessage) {
						  ((P5_WorkingMessage)obj).print() ;
					  } else if (obj instanceof P3_WorkingMessage) {
						  ((P3_WorkingMessage)obj).print() ;
					  } else if (obj instanceof P2_WorkingMessage) {
						  ((P2_WorkingMessage)obj).print() ;
					  } else if (obj instanceof P1_WorkingMessage) {
						  ((P1_WorkingMessage)obj).print() ;					  
					  } else if (obj instanceof M1_WorkingMessage) {
						  ((M1_WorkingMessage)obj).print() ;
					  } else if (obj instanceof QM_WoringMessage) {
						  ((QM_WoringMessage)obj).print() ;
					  } else if (obj instanceof GB_WorkingMessage) {
						  ((GB_WorkingMessage)obj).print() ;
					  } else if (obj instanceof HF_WorkingMessage) {
						  ((HF_WorkingMessage)obj).print() ;
					  } else if (obj instanceof QF_WorkingMessage) {
						  ((QF_WorkingMessage)obj).print() ;
					  } else if (obj instanceof PQ_WorkingMessage) {
						  ((PQ_WorkingMessage)obj).print() ;
					  } else if (obj instanceof PP_WorkingMessage) {
						  ((PP_WorkingMessage)obj).print() ;
					  } else if (obj instanceof PG_WorkingMessage) {
						  ((PG_WorkingMessage)obj).print() ;
					  } else if (obj instanceof PF_WorkingMessage) {
						  ((PF_WorkingMessage)obj).print() ;
					  } else if (obj instanceof PE_WorkingMessage) {
						  ((PE_WorkingMessage)obj).print() ;
					  } else if (obj instanceof PB_WorkingMessage) {
						  ((PB_WorkingMessage)obj).print() ;					  
					  } else if (obj instanceof HC_WorkingMessage) {
						  ((HC_WorkingMessage)obj).print() ;
					  } else if (obj instanceof QM_WoringMessage) {
						  ((QM_WoringMessage)obj).print() ;
					  } else if (obj instanceof PI_WorkingMessage) {
						  ((PI_WorkingMessage)obj).print() ;
					  } else if (obj instanceof BB_WorkingMessage) {
						  ((BB_WorkingMessage)obj).print() ;
					  } else if (obj instanceof XA_WorkingMessage) {
						  ((XA_WorkingMessage)obj).print() ;
					  } else if (obj instanceof QL_WorkingMessage) {
						  ((QL_WorkingMessage)obj).print() ;
	    			  } else if (obj instanceof GL_WorkingMessage) {
						  ((GL_WorkingMessage)obj).print() ;
	    			  } else if (obj instanceof TJ_WorkingMessage) {
						  ((TJ_WorkingMessage)obj).print() ;
					  } else if (obj instanceof SJ_WorkingMessage) {
						  ((SJ_WorkingMessage)obj).print() ;
					  } else if (obj instanceof SI_WorkingMessage) {
						  ((SI_WorkingMessage)obj).print() ;
					  } else if (obj instanceof SH_WorkingMessage) {
						  ((SH_WorkingMessage)obj).print() ;
					  } else if (obj instanceof SG_WorkingMessage) {
						  ((SG_WorkingMessage)obj).print() ;
					  } else if (obj instanceof SF_WorkingMessage) {
						  ((SF_WorkingMessage)obj).print() ;
					  } else if (obj instanceof SE_WorkingMessage) {
//						  ((SE_WorkingMessage)obj).print() ;
					  } else if (obj instanceof SB_WorkingMessage) {
						  ((SB_WorkingMessage)obj).print() ;
					  } else if (obj instanceof S9_WorkingMessage) {
						  ((S9_WorkingMessage)obj).print() ;
					  } else if (obj instanceof S8_WorkingMessage) {
//						  ((S8_WorkingMessage)obj).print() ;
					  } else if (obj instanceof S5_WorkingMessage) {
						  ((S5_WorkingMessage)obj).print() ;
					  } else if (obj instanceof S4_WorkingMessage) {
						  ((S4_WorkingMessage)obj).print() ;
					  } else if (obj instanceof S3_WorkingMessage) {
						  ((S3_WorkingMessage)obj).print() ;
					  } else if (obj instanceof P7_WorkingMessage) {
						  ((P7_WorkingMessage)obj).print() ;
					  } else if (obj instanceof ST_WorkingMessage) {
						  ((ST_WorkingMessage)obj).print() ;
					  } else if (obj instanceof GR_WorkingMessage) {
							 ((GR_WorkingMessage)obj).print() ;
					  }
	
	    		} else if (obj instanceof POSHeader) {
	    			if (!IConstant.POSPROTOCOL_COMMANDID_DE.equals(((POSHeader) obj).getCommandID()) ||
	    					!IConstant.POSPROTOCOL_COMMANDID_AB.equals(((POSHeader) obj).getCommandID())
	    					)
	    				LogUtility.getLogger().debug(((POSHeader)obj).toString());
	    		}
	    	}
		} catch (Exception e) {
			LogUtility.getCoreLogger().error(e.getMessage(), e);
		}
	}
	
	/**
	 * Log Utility Method
	 * @param from	: 전문 송신자
	 * @param dest	: 전문 수신자
	 * @param obj	: 전문
	 */
	public static void printContent(String key, int from, int dest, Object obj) {
    		
		if ((from < 0) || (from >= SyncManager.DISE_MODULE_NAME.length) || 
				(dest < 0) || (dest >= SyncManager.DISE_MODULE_NAME.length)) {
			LogUtility.getPumpMLogger().error("[Pump M] Don't know Command ID --- From = " + from + " => To " + dest) ;
    			return ;
		}
	
		printContent(obj) ;

	}
	
	public static void printStateChange(String nozID, int preState, int currState, String khProc_no) {
		try {
			LogUtility.getPumpMLogger().debug("[Pump M] nozID(" + nozID +") : [" + 
					IPumpConstant.KH_PUMP_STATE_STRING[preState] + "] => [" + IPumpConstant.KH_PUMP_STATE_STRING[currState] +
					"] , KHID=" + khProc_no) ;
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e) ;
		}
		
	}
}
