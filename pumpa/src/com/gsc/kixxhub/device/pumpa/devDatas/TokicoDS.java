package com.gsc.kixxhub.device.pumpa.devDatas;

import com.gsc.kixxhub.device.pumpa.common.DataStruct;

/**
 * @author yd
 *
 */
class LiterPriceDataStruct extends DataStruct {
	/**
	 * @param nozzleCount
	 */
	public LiterPriceDataStruct() throws Exception {
			this.setString("price", 5);
			this.setString("liter", 5);
		
	}
	
}	// end Tokico_DataStructs



public class TokicoDS {
	/**
	 * 도끼꼬 구형 프로토콜 명령코드 "T4" 전문 DataStruct를 얻는다.
	 * 
	 * @param command		: 명령 코드 
	 * @param nozzleCount	: 노즐 개수 
	 * @return
	 */
	public static DataStruct getDS(String command, int nozzleCount) {
		DataStruct returnData = null;
		
		try {
			if (command.equals("S3")) {
				returnData = new LiterPriceDataStruct();

			} else if (command.equals("S5") || command.equals("SJ")) {
				returnData = new TotalGaugeDataStruct(nozzleCount);

			} else {
				returnData = null;
			}
			
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
class TotalGaugeDataStruct extends DataStruct {
	/**
	 * @param nozzleCount
	 */
	public TotalGaugeDataStruct(int nozzleCount) throws Exception {
		for (int i = 0; i < nozzleCount; i++) {
			this.setString("totalGauge" + i, 10);
			
		}
		
	}
	
}	// end Tokico_DataStructs