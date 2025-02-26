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
	// ���ں��� SEQ�� "0000"������ ���ؼ�  �������ڸ� �����Ѵ�.
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
				LogUtility.getPumpMLogger().debug("[Pump M] Barcode T_CL_DISCOUNT Data�� Null�Դϴ�. Barcode�� �����Ҽ� �����ϴ�.");
				return "" ;
			}else{
				discountdata.print();
				Discn_amt = GlobalUtility.appending0Pre(Integer.toString(discountdata.getDiscn_amt() / 1000), 2) ;
				if (Discn_amt != null) {
					SimpleDateFormat formatter = new SimpleDateFormat ("yyMMdd");
					Date date = new Date ( );
					long currentTime = date.getTime();
					String expdate = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0326); // ��ȿ�Ⱓ�� ��ĥ ���� (���� 30�� ����)
					//	�׽�Ʈ
					if (GlobalUtility.isNullOrEmptyString(expdate)) {
						expdate = "90" ; // ���� ���� ��� ����
						LogUtility.getPumpMLogger().debug("[Pump M] Barcode ��ȿ�Ⱓ�� CodeMaster �� ��� " + expdate+ " �Ϸ� ����");
					}
					// 2019.11.07 SoonKwan
					// expdate��  ���ڸ��� �Էµɰ��տ� "0"�� ���δ�.					
					if(expdate.length() == 1) expdate = "0" + expdate;
					
					LogUtility.getPumpMLogger().debug("[Pump M] expdate =" + expdate);
					
					long after91 = Long.parseLong(expdate) * 86400000; // 1�� = 86400000 miliseconds
					
					//date.setTime(currentTime + after91); 		// ��ȿ ����
					//dTime = formatter.format ( date ); 			// ��ȿ���� yyMMdd
					//dTime = dTime.toString().substring(1);		// ��ȿ���� yMMdd
					
					date.setTime(currentTime);
					dTime = formatter.format ( date ); 			// �������� yyMMdd
					dTime = dTime.toString().substring(1);		// �������� yMMdd
					
					
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
					 * �����ڵ� 4�ڸ��� 6�ڸ����ϰ� �ںκ����������� ä��� 
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
					//�����ڵ尡 4�ڸ��� ���� ����2�ڸ��� �߰��Ѵ�
					if(codeLen > 6){
						store_code = store_code.substring(0, 6);
					}else if(codeLen < 6 ){
						String temp = "";
						for(int i=0; i < 6 - codeLen; i++){
							temp += " ";
						}
						store_code = store_code + temp;
					}
					
					// �ý���(3)+ ��������(YMMDD) + ��ȿ�Ⱓ(2) + �������αݾ�(2) + SEQ(4) + �������(6)
					//barcode = deviceType + "" + dTime +"" + Discn_amt + "" + getSeqNo + "" + store_code;
					barcode = deviceType + "00" + dTime +"" + expdate + "" + Discn_amt + "" + getSeqNo + "" + store_code;
					LogUtility.getPumpMLogger().debug("[Pump M] [����]Barcode: " + deviceType + "/" + dTime +"/" +expdate+"/"+ Discn_amt + "/" + getSeqNo + "/" + store_code);
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
	 * ������ �������� ������OPT �������α� �μ� 
	 *    ����
	 * 		 �� : �ý���(X)��ȿ����(YMMDD)�������αݾ�(XX)SEQ(XXXX)��������ڵ�(XXXX)	
			 �ý��� : A(POS), 6(CAT), 3(ODT), E (ETC)
			 �������αݾ�(XX) : ���� õ
			 SEQ(XXXX) : 0000 ~ 0009 ~ 000A ~ 000Z ~ ZZZZ 
			         ����, �빮�� Ȱ���ϰ�, ZZZZ �̸�, 0000 ���� �ٽ� Count
			         �� ���� ���� : 1,336,336 �� (34 *34*34*34)
			 ��������ڵ� : ���ڿ� �빮�� Ȱ��
	 * 
	 * @param deviceType : A(POS), 6(CAT), 3(ODT), E (ETC)
	 * @param price : ������ �ݾ� (��)
	 * @return
	 */
	// Barcode ����
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
			// 2016. 4. 14. ���� 15:48:31, PI2, Taekwon Lee ODT �ŷ�ó���� �ǿ� ���ؼ� ������ ���ڵ带 �������� ����. �پ����� ��� ����
			// �ǻ���� ����.
			if(!GlobalUtility.isNullOrEmptyString(messageType) 
					&& IUPOSConstant.MESSAGETYPE_0082.equals(messageType) || IUPOSConstant.MESSAGETYPE_0084.equals(messageType)){
				LogUtility.getPumpMLogger().debug("[Pump M]  �پ��븦 ������ ODT �ŷ�ó���� �ǿ� ���ؼ� ������ ���ڵ带 �������� ����" );
				return "";
			}else if(GlobalUtility.isNullOrEmptyString(messageType) 
					&& !GlobalUtility.isNullOrEmptyString(cardCodeBase) && cardCodeBase.equals(ICode.CARD_CODE_BASE_02)){
			// �Ҹ��� �ܻ�ŷ�ó && ���ʽ� �����̰� ���忡�� ���ʽ� ������ ���� ���� �ÿ� ���ڵ带 ����Ѵ�.
			// ������ �ŷ�ó ���ڵ带 ���ұ� ������ ����� �� ���� �Ѵ�.
				LogUtility.getPumpMLogger().debug("[Pump M]  �پ��븦 ������ ODT �ŷ�ó���� �ǿ� ���ؼ� ������ ���ڵ带 �������� ����" );
				return "";
			}
		}
		
		
		try {
			trDataInfo = T_KH_PUMP_TRHandler.getHandler().getT_KH_PUMP_TRData(khprocsNo);
			
			if(trDataInfo != null){
				if("0".equals(trDataInfo.getOil_completed_ind())){ // �Ϸ����� Yes
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
	 * //201812 ygh ī�������� �������ڵ� ����߰�
	 * ������ �������� �������ڵ� �μ� 
	 *    ���� 22�ڸ�
	 * 		 �� : ��������(YYMMDD)����(X)�����ݾ�(xxx)��������ڵ�(XXXXXX)�ý���(X)SEQ(XXXXX)
	 	     ���� : D(����), M(�ֹ���), K(����), P(����ֹ���)
			 �����ݾ�(xxx) : ���� õ (999,000���� ǥ��Ǹ� �̻�ݾ��� 999000������ ó���Ѵ�)
			 ��������ڵ�(XXXXXX) : ���ڿ� �빮�� Ȱ��, �����ڵ尡 4�ڸ��� ��� ��2�ڸ��� ����
			 �ý��� : A(POS), 6(CAT), 3(ODT), E (ETC)
			 SEQ(XXXXX) : 4�ڸ� �տ� 0�� �ٿ��� 5�ڸ� ���� 
			          0000  ~ ZZZZ 
			         ����, �빮�� Ȱ���ϰ�, ZZZZ �̸�, 0000 ���� �ٽ� Count
			         			 
	 * 
	 * @param deviceType : A(POS), 6(CAT), 3(ODT), E (ETC)
	 * @param price : ������ �ݾ� (��)
	 * @return
	 */
	// Barcode ���� (�������ڵ�)
	//�ǻ���������� �״�� �����Ѵ�
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
			// 2016. 4. 14. ���� 15:48:31, PI2, Taekwon Lee ODT �ŷ�ó���� �ǿ� ���ؼ� ������ ���ڵ带 �������� ����. �پ����� ��� ����
			// �ǻ���� ����.
			if(!GlobalUtility.isNullOrEmptyString(messageType) 
					&& IUPOSConstant.MESSAGETYPE_0082.equals(messageType) || IUPOSConstant.MESSAGETYPE_0084.equals(messageType)){
				LogUtility.getPumpMLogger().debug("[Pump M]  �پ��븦 ������ ODT �ŷ�ó���� �ǿ� ���ؼ� ������ ���ڵ带 �������� ����" );
				return "";
			}else if(GlobalUtility.isNullOrEmptyString(messageType) 
					&& !GlobalUtility.isNullOrEmptyString(cardCodeBase) && cardCodeBase.equals(ICode.CARD_CODE_BASE_02)){
			// �Ҹ��� �ܻ�ŷ�ó && ���ʽ� �����̰� ���忡�� ���ʽ� ������ ���� ���� �ÿ� ���ڵ带 ����Ѵ�.
			// ������ �ŷ�ó ���ڵ带 ���ұ� ������ ����� �� ���� �Ѵ�.
				LogUtility.getPumpMLogger().debug("[Pump M]  �پ��븦 ������ ODT �ŷ�ó���� �ǿ� ���ؼ� ������ ���ڵ带 �������� ����" );
				return "";
			}
		}
		
		
		try {
			trDataInfo = T_KH_PUMP_TRHandler.getHandler().getT_KH_PUMP_TRData(khprocsNo);
			
			if(trDataInfo != null){
				//���������� Ȯ���Ѵ�. BR������ �����ݾ��� ������� �ʰ�, �����Ϸ��ȣ�� �����ݾ��� ��µȴ�.
				if("0".equals(trDataInfo.getOil_completed_ind())){ // �Ϸ����� Yes
					barcodePrice = price;
				}else{		
					if(GlobalUtility.isNullOrEmptyString(trDataInfo.getEqpm_amt_prc())){
						barcodePrice = price;
					}else{
						barcodePrice = trDataInfo.getEqpm_amt_prc();
					}
				}
				//1206:����, 0660:�ֹ���, 0610:����ֹ���, 1030:����
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
	
	//201812 ygh ī�������� �������ڵ� ����߰�
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
			
			//�����ݾ׼���-999000�� �̻��� 999�� ǥ��
			if ( Integer.parseInt(price) > 990000) {
				price = "999000";
				LogUtility.getPumpMLogger().debug("[Pump M] Barcode price ���� �ʰ��� 999000�� ����");
			}
			pump_amt = GlobalUtility.appending0Pre(Integer.toString(Integer.parseInt(price) / 1000), 3) ;
			if (pump_amt != null) {
				//���ڼ���-�����ϱ����� �ƴ� ���� �ý����Ϸ� �����Ѵ�. 
				SimpleDateFormat formatter = new SimpleDateFormat ("yyMMdd");
				Date date = new Date();
				dTime = formatter.format ( date ); 			// ��ȿ���� yyMMdd
				//�����ڵ�
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
				//�����ڵ尡 4�ڸ��� ���� ����2�ڸ��� �߰��Ѵ�
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
				
				//SEQ-�Ϸù�ȣ���� (4�ڸ��� ������� ä���ϰ�, �ڸ��� ���߱����� �տ� 0�� ���δ�.)
				//2019.08.09 �������
				//deviceType 3�ڸ��� �Ѵ�. A01(POS), 6XX(CAT) ,300(ODT) EOO(ETC)
				
				getSeqNo = new String(getBarcodeUniqueKey());
				//getSeqNo = "0" + getSeqNo ;
				
				//barcode = dTime + goods_type + pump_amt + store_code+ deviceType + getSeqNo;
				barcode = dTime + goods_type + pump_amt + store_code+ deviceType +"00"+ getSeqNo;
				LogUtility.getPumpMLogger().debug("[Pump M] [����]Barcode: " + dTime + "/" + goods_type +"/" + pump_amt + "/" + store_code + "/" + deviceType + "/" + getSeqNo);
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
	 * [2016. 4. 14. ���� 15:48:31, PI2, songkis, ] barcode seq��ȣ�� �����ϱ� ���� ���Ӱ� �߰��Ѵ�.
	 * 
	 * ���ο� seq�� �����Ѵ�.
	 * �����ϴ� ������ ������ ����.
	 * 
	 * 	1. T_KH_KEYS ���̺��� KHTRKEY key ���� ���� Value ���� ���� �´�.
	 * 		1-1) ���� ���� ���
	 * 			0000���� �Ͽ� �����ϸ�, �� ���� Insert �Ѵ�.
	 * 		1-2) ���� �ִ� ���
	 * 				seq + 1 �� �Ͽ� �����ϸ�, �� ���� Update �Ѵ�.
	 * 
	 * 2019.08.09  ��¥���� seq��  ���� �����ؼ� �����Ѵ�.
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
			
			// �������ڿ��θ� Ȯ���Ѵ�
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
	// PI2, CWI, 2016-03-18, GSC SELF ���ڵ� ����
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
				LogUtility.getPumpMLogger().debug("[Pump M] Barcode T_CL_DISCOUNT Data�� Null�Դϴ�. Barcode�� �����Ҽ� �����ϴ�.");
				return "" ;
			}else{
				discountdata.print();
				Discn_amt = GlobalUtility.appending0Pre(Integer.toString(discountdata.getDiscn_amt() / 1000), 2) ;
				
				if (Discn_amt != null) {
					SimpleDateFormat formatter = new SimpleDateFormat ("yyMMdd");
					Date date = new Date ( );
					long currentTime = date.getTime();
					String expdate = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0326); // ��ȿ�Ⱓ�� ��ĥ ���� (���� 30�� ����)
					//	�׽�Ʈ
					if (GlobalUtility.isNullOrEmptyString(expdate)) {
						expdate = "90" ; // ���� ���� ��� ����
						LogUtility.getPumpMLogger().debug("[Pump M] Barcode ��ȿ�Ⱓ�� CodeMaster �� ��� " + expdate+ " �Ϸ� ����");
					}
					//
					LogUtility.getPumpMLogger().debug("[Pump M] expdate =" + expdate);
					
					long after91 = Long.parseLong(expdate) * 86400000; // 1�� = 86400000 miliseconds
					
					date.setTime(currentTime + after91); 		// ��ȿ ����
					dTime = formatter.format ( date ); 			// ��ȿ���� yyMMdd
					dTime = dTime.toString().substring(1);		// ��ȿ���� yMMdd
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
		// validate_yn true: ��ȿ�� ������(1) false: ��ȿ���� ���� ������(0)
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
	
	// Barcode ȸ�� ��������(1) + ���ڵ�(16) + �󼼼���
	public static String validateBarcode(String barcode) {
    	boolean isValidate = true;
    	String msg = "";
		// Barcode ��ȿ�� �˻�
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
    			msg = discount_amt + ",000�� ������ ���� �Դϴ�.";
    		}else{
    			msg = discount_amt + "�� ������ ���� �Դϴ�.";
    		}
    		//YMMDD - ���� ��¥�� ���ڵ� ��¥�� 0�� �� ��� ���ϱ� ���� 
    		//Y�⵵�� 0����4 ���̸� 0 5���� 9���� 10���� �⵵�� �ٲٴ� ���� �߰�
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
    			msg = "��ϵ� ��� �ƴմϴ�.";
    			return returnValidateBarcode(isValidate, barcode, msg);
    		}
    		if(Integer.parseInt(barcode_exp_date) < Integer.parseInt(today)){
    			isValidate = false;
    			msg = "��ȿ�Ⱓ�� �������ϴ�.";
    		}else{
    			try {
    				storeData = T_KH_STOREHandler.getHandler().getT_KH_STOREData();
    				if (storeData == null) {
    					LogUtility.getPumpMLogger().debug("[Pump M] Barcode validation storeData is Null");
    					isValidate = false;
    	    			msg = "���� ������ �����ϴ�.";
    	    			return returnValidateBarcode(isValidate, barcode, msg);
    				}else{
    					if(!GlobalUtility.isNullOrEmptyString(storeData.getStore_code())){
    						store_code = storeData.getStore_code();
    					}else{
    						LogUtility.getPumpMLogger().debug("[Pump M] Barcode validation store_code is Null or EmptyString");
    						isValidate = false;
    		    			msg = "���� �ڵ尡 �����ϴ�.";
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
    	    			msg = "���� �ڵ�� ���ڵ��� �����ڵ尡 ��ġ���� �ʽ��ϴ�.";
    				}
    			} catch (Exception e) {
    				LogUtility.getCATMLogger().error(e.getMessage(), e);
    				isValidate = false;
        			msg = "���ڵ� ȸ�� �� ������ �߻��Ͽ����ϴ�.";
    			}
    		}
    	}else{
    		isValidate = false;
    		msg = "���ڵ� ���Ŀ� ���� �ʽ��ϴ�.";
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
