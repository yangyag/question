package com.gsc.kixxhub.module.pumpm.pump.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.gsc.kixxhub.common.data.ICode;
import com.gsc.kixxhub.common.data.IConstant;
import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.IUPOSConstant;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_CODEMASTERHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_KEYSHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_PUMP_TRHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_STOREHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_KEYSData;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_PUMP_TRData;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_STOREData;
import com.gsc.kixxhub.common.dbadapter.opt.handler.T_CL_DISCOUNTHandler;
import com.gsc.kixxhub.common.dbadapter.opt.vo.T_CL_DISCOUNTData;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;

public class Barcode {

	private static final String KH_TRANSACTION_KEY = "BARCODEKEY" ;
	// 일자별로 SEQ를 "0000"시작을 위해서  발행일자를 저장한다.
	// 2019.08.09
	// by SoonKwan
	private static final String KH_TRANSACTION_DAY = "BARCODEDAY" ; 
	
	
	public static String getBarcodeNumber(String deviceType, String price) {

		
		String barcode = "";
		
		if(GlobalUtility.isNullOrEmptyString(price)){
			LogUtility.getPumpMLogger().debug("[Pump M] Barcode price is null or EmptyString. "+price + " => '0'");
			price = "0";
			
		}
		
		LogUtility.getPumpMLogger().debug("[Pump M] Barcode price = "+price + " #Barcode price Int = "+Integer.parseInt(price));
		
		try {
			T_CL_DISCOUNTData discountdata = null;
			discountdata = new T_CL_DISCOUNTData();
			
			String store_code = null;
			String dTime = null;
			String Discn_amt = null;
			String getSeqNo = null;			
			
			discountdata = T_CL_DISCOUNTHandler.getHandler().getT_T_CL_DISCOUNTData(Integer.parseInt(price));
			if (discountdata == null){
				LogUtility.getPumpMLogger().debug("[Pump M] Barcode T_CL_DISCOUNT Data가 Null입니다. Barcode를 생성할수 없습니다.");
				return "" ;
			}else{
				discountdata.print();
				Discn_amt = GlobalUtility.appending0Pre(Integer.toString(discountdata.getDiscn_amt() / 1000), 2) ;
				if (Discn_amt != null) {
					SimpleDateFormat formatter = new SimpleDateFormat ("yyMMdd");
					Date date = new Date ( );
					long currentTime = date.getTime();
					String expdate = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0326); // 유효기간이 며칠 인지 (이후 30일 인지)
					//	테스트
					if (GlobalUtility.isNullOrEmptyString(expdate)) {
						expdate = "90" ; // 값이 없을 경우 지정
						LogUtility.getPumpMLogger().debug("[Pump M] Barcode 유효기간이 CodeMaster 에 없어서 " + expdate+ " 일로 지정");
					}
					// 2019.11.07 SoonKwan
					// expdate가  한자리로 입력될경우앞에 "0"을 붙인다.					
					if(expdate.length() == 1) expdate = "0" + expdate;
					
					LogUtility.getPumpMLogger().debug("[Pump M] expdate =" + expdate);
					
					long after91 = Long.parseLong(expdate) * 86400000; // 1일 = 86400000 miliseconds
					
					//date.setTime(currentTime + after91); 		// 유효 일자
					//dTime = formatter.format ( date ); 			// 유효일자 yyMMdd
					//dTime = dTime.toString().substring(1);		// 유효일자 yMMdd
					
					date.setTime(currentTime);
					dTime = formatter.format ( date ); 			// 영업일자 yyMMdd
					dTime = dTime.toString().substring(1);		// 영업일자 yMMdd
					
					
					T_KH_STOREData storeData = T_KH_STOREHandler.getHandler().getT_KH_STOREData() ;
				
					getSeqNo = new String(getBarcodeUniqueKey());
					
					if (storeData == null) {
						LogUtility.getPumpMLogger().debug("[Pump M] Barcode generation storeData is Null");
						return "";
					}else{
						if(!GlobalUtility.isNullOrEmptyString(storeData.getStore_code())){
							store_code = storeData.getStore_code();
						}else{
							LogUtility.getPumpMLogger().debug("[Pump M] Barcode generation store_code is Null or EmptyString");
							return "";
						}
					}
					
					int codeLen = store_code.length();
					/*
					 * 2019.08.02 SoonKwan
					 * 점포코드 4자리를 6자리로하고 뒤부분은공백으로 채운다 
					 */
					/*
					if(codeLen > 4){
						store_code = store_code.substring(codeLen - 4, codeLen);
					}else if(codeLen <=3 ){
						String temp = "";
						for(int i=0; i < 4 - codeLen; i++){
							temp += " ";
						}
						store_code = temp + store_code;
					}
					*/
					//매장코드가 4자리인 경우는 공백2자리를 추가한다
					if(codeLen > 6){
						store_code = store_code.substring(0, 6);
					}else if(codeLen < 6 ){
						String temp = "";
						for(int i=0; i < 6 - codeLen; i++){
							temp += " ";
						}
						store_code = store_code + temp;
					}
					
					// 시스템(3)+ 영업일자(YMMDD) + 유효기간(2) + 세차할인금액(2) + SEQ(4) + 발행매장(6)
					//barcode = deviceType + "" + dTime +"" + Discn_amt + "" + getSeqNo + "" + store_code;
					barcode = deviceType + "00" + dTime +"" + expdate + "" + Discn_amt + "" + getSeqNo + "" + store_code;
					LogUtility.getPumpMLogger().debug("[Pump M] [세차]Barcode: " + deviceType + "/" + dTime +"/" +expdate+"/"+ Discn_amt + "/" + getSeqNo + "/" + store_code);
				}

			} 			
		} catch (NumberFormatException e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
			barcode = "";
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
			barcode = "";
		}
		return barcode;
	}

	/**		
	 * 
	 * 주유후 영수증에 세차기OPT 세차할인권 인쇄 
	 *    설명
	 * 		 값 : 시스템(X)유효일자(YMMDD)세차할인금액(XX)SEQ(XXXX)발행매장코드(XXXX)	
			 시스템 : A(POS), 6(CAT), 3(ODT), E (ETC)
			 세차할인금액(XX) : 단위 천
			 SEQ(XXXX) : 0000 ~ 0009 ~ 000A ~ 000Z ~ ZZZZ 
			         숫자, 대문자 활용하고, ZZZZ 이면, 0000 부터 다시 Count
			         총 가능 개수 : 1,336,336 개 (34 *34*34*34)
			 발행매장코드 : 숫자와 대문자 활용
	 * 
	 * @param deviceType : A(POS), 6(CAT), 3(ODT), E (ETC)
	 * @param price : 실주유 금액 (원)
	 * @return
	 */
	// Barcode 발행
	public static String getBarcodeNumber(String deviceType, String price, String nozzleNo, String khprocsNo, String messageType, String ledCode, String cardCodeBase){
		LogUtility.getPumpMLogger().info("[Pump M] Barcode #deviceType:"+deviceType + " #price:" + price + " #nozzleNo: " + nozzleNo
				+ " #khprocsNo:" + khprocsNo + " #messageType:" + messageType + " #ledCode:" + ledCode + " #cardCodeBase:" + cardCodeBase);
		T_KH_PUMP_TRData trDataInfo = null;
		String barcodePrice = "";
		int nozProtocolInt = 0;
		if(!GlobalUtility.isNullOrEmptyString(nozzleNo)){
			nozProtocolInt = PumpMUtil.getConnectedRepODTProtocolFromNozzleNo(nozzleNo) ;
		}
		if(!GlobalUtility.isNullOrEmptyString(deviceType) && "3".equals(deviceType) 
				&& IPumpConstant.PUMP_PROTOCOL_NewDaSNo != nozProtocolInt && IPumpConstant.PUMP_PROTOCOL_DaSNo != nozProtocolInt){
			// 2016. 4. 14. 오후 15:48:31, PI2, Taekwon Lee ODT 거래처결제 건에 대해서 세차권 바코드를 발행하지 않음. 다쓰노일 경우 제외
			// 의사결정 사항.
			if(!GlobalUtility.isNullOrEmptyString(messageType) 
					&& IUPOSConstant.MESSAGETYPE_0082.equals(messageType) || IUPOSConstant.MESSAGETYPE_0084.equals(messageType)){
				LogUtility.getPumpMLogger().debug("[Pump M]  다쓰노를 제외한 ODT 거래처결제 건에 대해서 세차권 바코드를 발행하지 않음" );
				return "";
			}else if(GlobalUtility.isNullOrEmptyString(messageType) 
					&& !GlobalUtility.isNullOrEmptyString(cardCodeBase) && cardCodeBase.equals(ICode.CARD_CODE_BASE_02)){
			// 소모셀프 외상거래처 && 보너스 적립이고 매장에서 보너스 적립을 하지 않을 시에 바코드를 출력한다.
			// 하지만 거래처 바코드를 막았기 때문에 출력할 수 없게 한다.
				LogUtility.getPumpMLogger().debug("[Pump M]  다쓰노를 제외한 ODT 거래처결제 건에 대해서 세차권 바코드를 발행하지 않음" );
				return "";
			}
		}
		
		
		try {
			trDataInfo = T_KH_PUMP_TRHandler.getHandler().getT_KH_PUMP_TRData(khprocsNo);
			
			if(trDataInfo != null){
				if("0".equals(trDataInfo.getOil_completed_ind())){ // 완료정보 Yes
					barcodePrice = price;
				}else{		
					if(GlobalUtility.isNullOrEmptyString(trDataInfo.getEqpm_amt_prc())){
						barcodePrice = price;
					}else{
						barcodePrice = trDataInfo.getEqpm_amt_prc();
					}
				}
			}else{
				barcodePrice = price;
			}
			LogUtility.getPumpMLogger().debug("[Pump M] Barcode price: " + barcodePrice);
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		}
		
		return getBarcodeNumber(deviceType, barcodePrice);
		
	}

	/**		
	 * //201812 ygh 카닥주유소 주유바코드 기능추가
	 * 주유후 영수증에 주유바코드 인쇄 
	 *    설명 22자리
	 * 		 값 : 주유일자(YYMMDD)유종(X)주유금액(xxx)발행매장코드(XXXXXX)시스템(X)SEQ(XXXXX)
	 	     유종 : D(경유), M(휘발유), K(등유), P(고급휘발유)
			 주유금액(xxx) : 단위 천 (999,000까지 표기되며 이상금액은 999000원으로 처리한다)
			 발행매장코드(XXXXXX) : 숫자와 대문자 활용, 매장코드가 4자리인 경우 뒤2자리는 공백
			 시스템 : A(POS), 6(CAT), 3(ODT), E (ETC)
			 SEQ(XXXXX) : 4자리 앞에 0만 붙여서 5자리 맞춤 
			          0000  ~ ZZZZ 
			         숫자, 대문자 활용하고, ZZZZ 이면, 0000 부터 다시 Count
			         			 
	 * 
	 * @param deviceType : A(POS), 6(CAT), 3(ODT), E (ETC)
	 * @param price : 실주유 금액 (원)
	 * @return
	 */
	// Barcode 발행 (주유바코드)
	//의사결정사항은 그대로 유지한다
	public static String getBarcodeNumberPump(String deviceType, String price, String nozzleNo, String khprocsNo, String messageType, String ledCode, String cardCodeBase){
		LogUtility.getPumpMLogger().info("[Pump M] Barcode #deviceType:"+deviceType + " #price:" + price + " #nozzleNo: " + nozzleNo
				+ " #khprocsNo:" + khprocsNo + " #messageType:" + messageType + " #ledCode:" + ledCode + " #cardCodeBase:" + cardCodeBase);
		T_KH_PUMP_TRData trDataInfo = null;
		String barcodePrice = "";
		String barcodeGoodsType = "";
		
		int nozProtocolInt = 0;
		if(!GlobalUtility.isNullOrEmptyString(nozzleNo)){
			nozProtocolInt = PumpMUtil.getConnectedRepODTProtocolFromNozzleNo(nozzleNo) ;
		}
		if(!GlobalUtility.isNullOrEmptyString(deviceType) && "3".equals(deviceType) 
				&& IPumpConstant.PUMP_PROTOCOL_NewDaSNo != nozProtocolInt && IPumpConstant.PUMP_PROTOCOL_DaSNo != nozProtocolInt){
			// 2016. 4. 14. 오후 15:48:31, PI2, Taekwon Lee ODT 거래처결제 건에 대해서 세차권 바코드를 발행하지 않음. 다쓰노일 경우 제외
			// 의사결정 사항.
			if(!GlobalUtility.isNullOrEmptyString(messageType) 
					&& IUPOSConstant.MESSAGETYPE_0082.equals(messageType) || IUPOSConstant.MESSAGETYPE_0084.equals(messageType)){
				LogUtility.getPumpMLogger().debug("[Pump M]  다쓰노를 제외한 ODT 거래처결제 건에 대해서 세차권 바코드를 발행하지 않음" );
				return "";
			}else if(GlobalUtility.isNullOrEmptyString(messageType) 
					&& !GlobalUtility.isNullOrEmptyString(cardCodeBase) && cardCodeBase.equals(ICode.CARD_CODE_BASE_02)){
			// 소모셀프 외상거래처 && 보너스 적립이고 매장에서 보너스 적립을 하지 않을 시에 바코드를 출력한다.
			// 하지만 거래처 바코드를 막았기 때문에 출력할 수 없게 한다.
				LogUtility.getPumpMLogger().debug("[Pump M]  다쓰노를 제외한 ODT 거래처결제 건에 대해서 세차권 바코드를 발행하지 않음" );
				return "";
			}
		}
		
		
		try {
			trDataInfo = T_KH_PUMP_TRHandler.getHandler().getT_KH_PUMP_TRData(khprocsNo);
			
			if(trDataInfo != null){
				//주유내역을 확인한다. BR전문의 주유금액을 사용하지 않고, 주유완료신호의 주유금액이 출력된다.
				if("0".equals(trDataInfo.getOil_completed_ind())){ // 완료정보 Yes
					barcodePrice = price;
				}else{		
					if(GlobalUtility.isNullOrEmptyString(trDataInfo.getEqpm_amt_prc())){
						barcodePrice = price;
					}else{
						barcodePrice = trDataInfo.getEqpm_amt_prc();
					}
				}
				//1206:경유, 0660:휘발유, 0610:고급휘발유, 1030:등유
				if("1206".equals(trDataInfo.getGoods_code())) { 
					barcodeGoodsType = "D";
				} else if("0660".equals(trDataInfo.getGoods_code())) {
					barcodeGoodsType = "M";
				} else if("0610".equals(trDataInfo.getGoods_code())) {
					barcodeGoodsType = "P";
				} else if("1030".equals(trDataInfo.getGoods_code())) {
					barcodeGoodsType = "K";
				}
			}else{
				barcodePrice = price;
			}
			LogUtility.getPumpMLogger().debug("[Pump M] Barcode price: " + barcodePrice + "  barcodeGoodsType: " + barcodeGoodsType);
			
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		}
		
		return getBarcodeNumberPump(deviceType, barcodePrice, barcodeGoodsType);
		
	}
	
	//201812 ygh 카닥주유소 주유바코드 기능추가
	public static String getBarcodeNumberPump(String deviceType, String price, String goods_type) {

		
		String barcode = "";
		
		if(GlobalUtility.isNullOrEmptyString(price)){
			LogUtility.getPumpMLogger().debug("[Pump M] Barcode price is null or EmptyString. "+price + " => '0'");
			price = "0";
			
		}
		
		LogUtility.getPumpMLogger().debug("[Pump M] Barcode price = "+price + " #Barcode price Int = "+Integer.parseInt(price));
		
		try {
			String store_code = null;
			String dTime = null;
			String pump_amt = null;
			String getSeqNo = null;		
			
			//주유금액설정-999000원 이상은 999로 표기
			if ( Integer.parseInt(price) > 990000) {
				price = "999000";
				LogUtility.getPumpMLogger().debug("[Pump M] Barcode price 상한 초과로 999000로 변경");
			}
			pump_amt = GlobalUtility.appending0Pre(Integer.toString(Integer.parseInt(price) / 1000), 3) ;
			if (pump_amt != null) {
				//일자설정-영엽일기준이 아닌 현재 시스템일로 기준한다. 
				SimpleDateFormat formatter = new SimpleDateFormat ("yyMMdd");
				Date date = new Date();
				dTime = formatter.format ( date ); 			// 유효일자 yyMMdd
				//매장코드
				T_KH_STOREData storeData = T_KH_STOREHandler.getHandler().getT_KH_STOREData() ;
			
				if (storeData == null) {
					LogUtility.getPumpMLogger().debug("[Pump M] Barcode generation storeData is Null");
					return "";
				}else{
					if(!GlobalUtility.isNullOrEmptyString(storeData.getStore_code())){
						store_code = storeData.getStore_code();
					}else{
						LogUtility.getPumpMLogger().debug("[Pump M] Barcode generation store_code is Null or EmptyString");
						return "";
					}
				}
				//매장코드가 4자리인 경우는 공백2자리를 추가한다
				int codeLen = store_code.length();
				if(codeLen > 6){
					store_code = store_code.substring(0, 6);
				}else if(codeLen < 6 ){
					String temp = "";
					for(int i=0; i < 6 - codeLen; i++){
						temp += " ";
					}
					store_code = store_code + temp;
				}
				
				//SEQ-일련번호생성 (4자리를 순서대로 채번하고, 자리수 맞추기위해 앞에 0를 붙인다.)
				//2019.08.09 변경사항
				//deviceType 3자리로 한다. A01(POS), 6XX(CAT) ,300(ODT) EOO(ETC)
				
				getSeqNo = new String(getBarcodeUniqueKey());
				//getSeqNo = "0" + getSeqNo ;
				
				//barcode = dTime + goods_type + pump_amt + store_code+ deviceType + getSeqNo;
				barcode = dTime + goods_type + pump_amt + store_code+ deviceType +"00"+ getSeqNo;
				LogUtility.getPumpMLogger().debug("[Pump M] [주유]Barcode: " + dTime + "/" + goods_type +"/" + pump_amt + "/" + store_code + "/" + deviceType + "/" + getSeqNo);
			}

		} catch (NumberFormatException e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
			barcode = "";
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
			barcode = "";
		}
		return barcode;
	}
	
	/**
	 * [2016. 4. 14. 오후 15:48:31, PI2, songkis, ] barcode seq번호를 관리하기 위해 새롭게 추가한다.
	 * 
	 * 새로운 seq를 생성한다.
	 * 생성하는 기준은 다음과 같다.
	 * 
	 * 	1. T_KH_KEYS 테이블의 KHTRKEY key 값에 대한 Value 값을 가져 온다.
	 * 		1-1) 값이 없는 경우
	 * 			0000으로 하여 리턴하며, 그 값을 Insert 한다.
	 * 		1-2) 값이 있는 경우
	 * 				seq + 1 을 하여 리턴하며, 그 값을 Update 한다.
	 * 
	 * 2019.08.09  날짜별로 seq를  새로 생성해서 관리한다.
	 * by SoonKwan
	 * @return
	 */
	private synchronized static String getBarcodeUniqueKey() {	
		String preBarcodeID = null ;
		String newBarcodeID = null ;
		boolean isNew = false ;
		
		String preBarcodeDay = null;
		String newBarcodeDay = null;
		boolean isDay = false ;
		
		try {
			T_KH_KEYSData keysData = T_KH_KEYSHandler.getHandler().getT_KH_KEYSData(Barcode.KH_TRANSACTION_KEY) ;
			if (keysData == null) {
				newBarcodeID = "0000";
				isNew = true ;
			} else {
				preBarcodeID = keysData.getValue() ;
				//newBarcodeID = seqCode(preBarcodeID);
				isNew = false ;
			}
			
			// 발행일자여부를 확인한다
			SimpleDateFormat formatter = new SimpleDateFormat ("yyMMdd");
			Date date = new Date ( );
			long currentTime = date.getTime();
			date.setTime(currentTime); 		
			newBarcodeDay = formatter.format ( date ); 
			
			T_KH_KEYSData keysDay = T_KH_KEYSHandler.getHandler().getT_KH_KEYSData(Barcode.KH_TRANSACTION_DAY) ;
			if(keysDay != null){
				preBarcodeDay = keysDay.getValue();
				if(newBarcodeDay.equals(preBarcodeDay))
				{
					newBarcodeID = seqCode(Integer.parseInt(preBarcodeID));
				}
				else 
				{
					newBarcodeID = "0000";
				}
				isDay = false;
			}
			else {
				isDay = true;
				newBarcodeID = "0000";
			}
			
			if(isDay){
				T_KH_KEYSHandler.getHandler().insertT_KH_KEYSData(Barcode.KH_TRANSACTION_DAY, newBarcodeDay);
			} else {
				T_KH_KEYSHandler.getHandler().updateT_KH_KEYSData(Barcode.KH_TRANSACTION_DAY, newBarcodeDay);
			}
			//
			
			if (isNew) {
				T_KH_KEYSHandler.getHandler().insertT_KH_KEYSData(Barcode.KH_TRANSACTION_KEY, newBarcodeID);
			} else {
				T_KH_KEYSHandler.getHandler().updateT_KH_KEYSData(Barcode.KH_TRANSACTION_KEY, newBarcodeID);
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		} 
		LogUtility.getPumpMLogger().info("[Pump M] preBarcodeID=" + preBarcodeID + ":" + " newBarcodeID=" + newBarcodeID) ;
		
		return newBarcodeID ;
	}
	// PI2, CWI, 2016-03-18, GSC SELF 바코드 생성
	public static String getGSCSelfODTBarcodeNumber(String price) {
		
		String barcode = "";
		
		if(GlobalUtility.isNullOrEmptyString(price)){
			LogUtility.getPumpMLogger().debug("[Pump M] Barcode price is null or EmptyString. "+price + " => '0'");
			price = "0";
		}
		
		LogUtility.getPumpMLogger().debug("[Pump M] Barcode price = "+price + " #Barcode price Int = "+Integer.parseInt(price));
		
		try {
			T_CL_DISCOUNTData discountdata = null;
			discountdata = new T_CL_DISCOUNTData();
			
			String store_code = null;
			String dTime = null;
			String Discn_amt = null;
			String getSeqNo = null;			
			
			discountdata = T_CL_DISCOUNTHandler.getHandler().getT_T_CL_DISCOUNTData(Integer.parseInt(price));
			if (discountdata == null){
				LogUtility.getPumpMLogger().debug("[Pump M] Barcode T_CL_DISCOUNT Data가 Null입니다. Barcode를 생성할수 없습니다.");
				return "" ;
			}else{
				discountdata.print();
				Discn_amt = GlobalUtility.appending0Pre(Integer.toString(discountdata.getDiscn_amt() / 1000), 2) ;
				
				if (Discn_amt != null) {
					SimpleDateFormat formatter = new SimpleDateFormat ("yyMMdd");
					Date date = new Date ( );
					long currentTime = date.getTime();
					String expdate = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0326); // 유효기간이 며칠 인지 (이후 30일 인지)
					//	테스트
					if (GlobalUtility.isNullOrEmptyString(expdate)) {
						expdate = "90" ; // 값이 없을 경우 지정
						LogUtility.getPumpMLogger().debug("[Pump M] Barcode 유효기간이 CodeMaster 에 없어서 " + expdate+ " 일로 지정");
					}
					//
					LogUtility.getPumpMLogger().debug("[Pump M] expdate =" + expdate);
					
					long after91 = Long.parseLong(expdate) * 86400000; // 1일 = 86400000 miliseconds
					
					date.setTime(currentTime + after91); 		// 유효 일자
					dTime = formatter.format ( date ); 			// 유효일자 yyMMdd
					dTime = dTime.toString().substring(1);		// 유효일자 yMMdd
					T_KH_STOREData storeData = T_KH_STOREHandler.getHandler().getT_KH_STOREData() ;
				
					getSeqNo = new String(getBarcodeUniqueKey());
					
					if (storeData == null) {
						LogUtility.getPumpMLogger().debug("[Pump M] Barcode generation storeData is Null");
						return "";
					}else{
						if(!GlobalUtility.isNullOrEmptyString(storeData.getStore_code())){
							store_code = storeData.getStore_code();
						}else{
							LogUtility.getPumpMLogger().debug("[Pump M] Barcode generation store_code is Null or EmptyString");
							return "";
						}
					}
					
					
					int codeLen = store_code.length();
					if(codeLen > 4){
						store_code = store_code.substring(codeLen - 4, codeLen);
					}else if(codeLen <=3 ){
						String temp = "";
						for(int i=0; i < 4 - codeLen; i++){
							temp += " ";
						}
						store_code = temp + store_code;
					}
					
					barcode = dTime +"" + Discn_amt + "" + getSeqNo + "" + store_code;
					LogUtility.getPumpMLogger().debug("[Pump M] Barcode: " + dTime +"/" + Discn_amt + "/" + getSeqNo + "/" + store_code);
				}

			} 			
		} catch (NumberFormatException e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
			barcode = "";
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
			barcode = "";
		}
		return barcode;
	}
	

	public static String returnValidateBarcode(boolean validate_yn, String barcode, String msg){
		String returnMessage = "";
		String validate = "0";
		// validate_yn true: 유효한 세차권(1) false: 유효하지 않은 세차권(0)
		if(validate_yn){
			validate = "1";
		}else{
			validate = "0";
		}
		returnMessage = validate + barcode + msg;
		LogUtility.getCATMLogger().debug("returnValidateBarcode: " + returnMessage);
		
		return returnMessage;
	}
	
	private static String seqCode(int nSeq) {
		if(nSeq >= 9999) nSeq = 0;
		else
			nSeq++;
		return String.format("%04d", nSeq);
	}

	private static String seqCode(String seq){
		String getSeqNo = "0000";
		if(seq == null || seq.length() > 4){
			return getSeqNo;
		}
		byte[] barcodeByte = seq.getBytes();
		barcodeByte[3] = (byte)(barcodeByte[3] + 1);
		if (barcodeByte[3] == 58){
			barcodeByte[3] = (byte)(barcodeByte[3] + 7);
		}
		
		if (barcodeByte[3] == 91) {
			barcodeByte[2] = (byte)(barcodeByte[2] + 1);
			barcodeByte[3] = 48;
			
			if (barcodeByte[2] == 58) {
				barcodeByte[2] = (byte)(barcodeByte[2] + 7);
			}
			if (barcodeByte[2] == 91) {
				barcodeByte[1] = (byte)(barcodeByte[1] + 1);
				barcodeByte[2] = 48;
				
				if (barcodeByte[1] == 58) {
					barcodeByte[1] = (byte)(barcodeByte[1] + 7);
				}
				if (barcodeByte[1] == 91) {
					barcodeByte[0] = (byte)(barcodeByte[0] + 1);
					barcodeByte[1] = 48;
					if (barcodeByte[0] == 58) {
						barcodeByte[0] = (byte)(barcodeByte[0] + 7);
					}
					if (barcodeByte[0] == 91) {
						barcodeByte[0] = 48;
					}
				}
			}
		}
		getSeqNo = new String(barcodeByte);
	
		LogUtility.getPumpMLogger().debug("getSeqNo: " + getSeqNo);
		
		return getSeqNo;
	}
	
	// Barcode 회수 성공여부(1) + 바코드(16) + 상세설명
	public static String validateBarcode(String barcode) {
    	boolean isValidate = true;
    	String msg = "";
		// Barcode 유효성 검사
    	if(!GlobalUtility.isNullOrEmptyString(barcode) && barcode.length() == 16){
    		String store_code = "";
    		String barcode_store_code = "";
    		T_KH_STOREData storeData = null;
    		String deviceType = barcode.substring(0, 1);
    		String barcode_discount_amt = barcode.substring(6, 8);
    		String barcode_exp_date = barcode.substring(1, 6);
    		String today = GlobalUtility.getDateYYYYMMDD();
    		barcode_store_code = barcode.substring(12, 16);
    		today = today.substring(3, 8);
    		LogUtility.getPumpMLogger().debug("barcode_discount_amt is " + barcode_discount_amt);
    		int discount_amt = Integer.parseInt(barcode_discount_amt);
    		if(discount_amt != 0){
    			msg = discount_amt + ",000원 세차권 할인 입니다.";
    		}else{
    			msg = discount_amt + "원 세차권 할인 입니다.";
    		}
    		//YMMDD - 오늘 날짜나 바코드 날짜가 0년 일 경우 비교하기 위해 
    		//Y년도가 0에서4 사이면 0 5에서 9까지 10으로 년도를 바꾸는 로직 추가
    		int barcode_yy = Integer.parseInt(barcode_exp_date.substring(0,1));
    		int today_yy = Integer.parseInt(today.substring(0,1));
    		if(barcode_yy != 0 || today_yy != 0){
    			if(barcode_yy == 0){
        			if(today_yy >= 5 && today_yy <= 9){
        				barcode_exp_date = "10" + barcode_exp_date.substring(1);
            		}else if(today_yy >= 0 && today_yy <= 4){
            			barcode_exp_date = "0" + barcode_exp_date.substring(1);
            		}
        		}
        		if(today_yy == 0){
        			if(barcode_yy >= 5 && barcode_yy <= 9){
        				today = "10" + today.substring(1);
        			}else if(barcode_yy >= 0 && today_yy <= 4){
        				today = "0" + today.substring(1);
            		}
        		}
    		}
    		
    		if(!"A".equals(deviceType) && !"6".equals(deviceType) && !"3".equals(deviceType) && !"E".equals(deviceType))
    		{
    			isValidate = false;
    			msg = "등록된 장비가 아닙니다.";
    			return returnValidateBarcode(isValidate, barcode, msg);
    		}
    		if(Integer.parseInt(barcode_exp_date) < Integer.parseInt(today)){
    			isValidate = false;
    			msg = "유효기간이 지났습니다.";
    		}else{
    			try {
    				storeData = T_KH_STOREHandler.getHandler().getT_KH_STOREData();
    				if (storeData == null) {
    					LogUtility.getPumpMLogger().debug("[Pump M] Barcode validation storeData is Null");
    					isValidate = false;
    	    			msg = "매장 정보가 없습니다.";
    	    			return returnValidateBarcode(isValidate, barcode, msg);
    				}else{
    					if(!GlobalUtility.isNullOrEmptyString(storeData.getStore_code())){
    						store_code = storeData.getStore_code();
    					}else{
    						LogUtility.getPumpMLogger().debug("[Pump M] Barcode validation store_code is Null or EmptyString");
    						isValidate = false;
    		    			msg = "매장 코드가 없습니다.";
    		    			return returnValidateBarcode(isValidate, barcode, msg);
    					}
    				}
    				int codeLen = store_code.length();
    				if(codeLen > 4){
    					store_code = store_code.substring(codeLen - 4, codeLen);
    				}else if(codeLen <=3 ){
    					String temp = "";
    					for(int i=0; i < 4 - codeLen; i++){
    						temp += " ";
    					}
    					store_code = temp + store_code;
    				}
    				if(!barcode_store_code.equals(store_code)){
    					isValidate = false;
    	    			msg = "매장 코드와 바코드의 매장코드가 일치하지 않습니다.";
    				}
    			} catch (Exception e) {
    				LogUtility.getCATMLogger().error(e.getMessage(), e);
    				isValidate = false;
        			msg = "바코드 회수 중 오류가 발생하였습니다.";
    			}
    		}
    	}else{
    		isValidate = false;
    		msg = "바코드 형식에 맞지 않습니다.";
    		int codeLen = barcode.length();
			if(codeLen > 16){
				barcode = barcode.substring(codeLen - 4, codeLen);
			}else if(codeLen <=15 ){
				String temp = "";
				for(int i=0; i < 4 - codeLen; i++){
					temp += " ";
				}
				barcode = temp + barcode;
			}
    		LogUtility.getCATMLogger().debug("Barcode is null or EmptyString or not length 16" );
    	}
    	return returnValidateBarcode(isValidate, barcode, msg);
	}
}
