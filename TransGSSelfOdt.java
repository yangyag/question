package com.gsc.kixxhub.device.pumpa.translation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import com.gsc.kixxhub.common.data.IConstant;
import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.posdata.POSHeader;
import com.gsc.kixxhub.common.data.posdata.POS_HE;
import com.gsc.kixxhub.common.data.posdata.POS_HF;
import com.gsc.kixxhub.common.data.posdata.POS_ME;
import com.gsc.kixxhub.common.data.posdata.POS_MF;
import com.gsc.kixxhub.common.data.pump.BC_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.BI_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.BR_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.BS_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.CA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.GA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.GB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.GT_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P2_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P3_1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P5_1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P5_NozzleInfo;
import com.gsc.kixxhub.common.data.pump.P6_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PC_StoreCodeInfo;
import com.gsc.kixxhub.common.data.pump.PC_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PM_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PU_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PV_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S3_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S4_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S8_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SE_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SJ_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.data.upos.MessageLogUtil;
import com.gsc.kixxhub.common.data.upos.UPOSMessage;
import com.gsc.kixxhub.common.data.upos.UPOSMessageUtility;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_ItemInfo;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_ItemInfo_Item;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_PaymentInfo;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_PaymentInfo_Item;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_TradeCondition_Item;
import com.gsc.kixxhub.common.utility.ByteUtil;
import com.gsc.kixxhub.common.utility.ClearUtil;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.cat.PropertyID;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.DataStruct;
import com.gsc.kixxhub.device.pumpa.devDatas.GSSelfOdtDS;

/*
* 프로젝트 명 : PI2
* 일시 : 2015.12.04
* 신규
* @author 정혜정
* */

public class TransGSSelfOdt extends Translation {
	
	public byte[] generateByteStream(WorkingMessage workingMessage) throws Exception {
		byte[] returnMessage = null;
		
		String workingMessageCommand = workingMessage.getCommand();
		
		if(workingMessageCommand.equals(IPumpConstant.COMMANDID_P5_1))
		{
			//ODT노즐구성 설정
			P5_1_WorkingMessage p5_1_WorkingMessage = (P5_1_WorkingMessage) workingMessage;
			
			DataStruct p5DS = new DataStruct();
			
			p5DS.addString("Command", "P5", 2);
			p5DS.addString("DeviceNo", p5_1_WorkingMessage.getNozzleNo(), 2);
			p5DS.addString("ConnectDevNo", p5_1_WorkingMessage.getConnectNozzleNo(), 2);
			p5DS.addString("ODTId", p5_1_WorkingMessage.getOdtID(), 2);
			p5DS.addString("SysTime", p5_1_WorkingMessage.getSysTime(), 14);
			p5DS.addString("IpAddress", p5_1_WorkingMessage.getIpAddress(), 20);
			p5DS.addString("PortNo", p5_1_WorkingMessage.getPortNo(), 10);
			
			int size = p5_1_WorkingMessage.getNozzleInfo().size();
			
			p5DS.addString("NozCount", Integer.toString(size), 2);
			
			
			// 노즐번호, 단가, 상품코드(유종코드), 상품명(유종명)을 노즐 개수만큼 반복한다.
			for(int i=0; i<size; i++)
			{
				P5_NozzleInfo nozzInfo = p5_1_WorkingMessage.getNozzleInfo().elementAt(i);
				
				p5DS.addString("NozNo"+i+1, nozzInfo.getNozzleNumber(), 2);
				p5DS.addString("BasePrice"+i+1, nozzInfo.getBasePrice(), 6);
				p5DS.addString("GoodsCode"+i+1, nozzInfo.getGoodsCode(), 18);
				p5DS.addString("GoodsType"+i+1, nozzInfo.getGoodsType(), 40);
			}
			
			p5DS.addString("UseFullPumping", p5_1_WorkingMessage.getUseFullPumping(), 1);
			p5DS.addString("ReApprovalOption", p5_1_WorkingMessage.getReApprovalOption(), 1);
			p5DS.addString("UseBL", p5_1_WorkingMessage.getUseBL(), 1);
					
			returnMessage = p5DS.getByteStream();
			
		}
		else if(workingMessageCommand.equals(IPumpConstant.COMMANDID_P1))
		{
			// 매장정보 
			P1_WorkingMessage p1_WorkingMessage = (P1_WorkingMessage) workingMessage;
			
			DataStruct p1DS = new DataStruct();
			
			p1DS.addString("Command", workingMessageCommand, 2);
			p1DS.addString("DeviceNo", p1_WorkingMessage.getNozzleNo(), 2);
			p1DS.addString("ConnectDevNo", p1_WorkingMessage.getConnectNozzleNo(), 2);
			
			p1DS.addVString("StoreCord", p1_WorkingMessage.getStoreCord(), 10);
			p1DS.addVString("StoreRegiNum", p1_WorkingMessage.getStoreRegiNum(), 20);
			p1DS.addVString("StoreName", p1_WorkingMessage.getStoreName(), 50);
			p1DS.addVString("RepName", p1_WorkingMessage.getRepName(), 20);
			p1DS.addVString("StorePost", p1_WorkingMessage.getStorePost(), 10);
			p1DS.addVString("StoreADDR1", p1_WorkingMessage.getStoreADDR1(), 100);
			p1DS.addVString("StoreADDR2", p1_WorkingMessage.getStoreADDR2(), 100);
			p1DS.addVString("Tel", p1_WorkingMessage.getTel(), 20);
			
			p1DS.addVString("SaMinAmt", p1_WorkingMessage.getSaMinAmt(), 8);
			p1DS.addVString("ReportFootTitle", p1_WorkingMessage.getStoreCord(), 10);
			
			returnMessage = p1DS.getByteStream();
		}
		else if(workingMessageCommand.equals(IPumpConstant.COMMANDID_P2))
		{
			// ODT 영수증 정보(머리말/꼬리말)
			P2_WorkingMessage p2_WorkingMessage = (P2_WorkingMessage) workingMessage;
			
			DataStruct p2DS = new DataStruct();
			
			p2DS.addString("Command", workingMessageCommand, 2);
			p2DS.addString("DeviceNo", p2_WorkingMessage.getNozzleNo(), 2);
			p2DS.addString("ConnectDevNo", p2_WorkingMessage.getConnectNozzleNo(), 2);
			p2DS.addVString("BaseHeadTitle", p2_WorkingMessage.getBaseHeadTitle(), 200);
			p2DS.addVString("BaseFootTitle1", p2_WorkingMessage.getBaseFootTitle1(), 200);
			p2DS.addVString("BaseFootTitle2", p2_WorkingMessage.getBaseFootTitle2(), 200);
			p2DS.addVString("BarcodePrintYN", p2_WorkingMessage.getBarcodePrintYN(), 1);
			
			returnMessage = p2DS.getByteStream();
		}
		else if(workingMessageCommand.equals(IPumpConstant.COMMANDID_PC))
		{
			// 매장 환경설정 정보
			PC_WorkingMessage pc_WorkingMessage = (PC_WorkingMessage) workingMessage;
			
			DataStruct pcDS = new DataStruct();
			
			pcDS.addString("Command", workingMessageCommand, 2);
			pcDS.addString("DeviceNo", pc_WorkingMessage.getNozzleNo(), 2);
			pcDS.addString("ConnectDevNo", pc_WorkingMessage.getConnectNozzleNo(), 2);
			
			pcDS.addString("CodeCount", pc_WorkingMessage.getStoreCodeCount(), 2);

			for(int i=0; i<pc_WorkingMessage.getStoreCodeInfo().size(); i++)
			{
				PC_StoreCodeInfo storeCodeInfo = pc_WorkingMessage.getStoreCodeInfo().elementAt(i);
				
				pcDS.addVString("Code_" + i, storeCodeInfo.getCode(), 4);
				pcDS.addVString("Value_" + i, storeCodeInfo.getValue(), 200);
			}
			
			returnMessage = pcDS.getByteStream();
			
			// 생성 된 PC전문 로그
			LogUtility.getLogger().debug("[pc_WorkingMessage] returnMessage : " + new String(returnMessage));
		}
		else if(workingMessageCommand.equals(IPumpConstant.COMMANDID_P6))
		{
			// 영업일 및 시간 설정
			P6_WorkingMessage p6_WorkingMessage = (P6_WorkingMessage) workingMessage;
			
			DataStruct p6DS = new DataStruct();
			
			p6DS.addString("Command", workingMessageCommand, 2);
			p6DS.addString("DeviceNo", p6_WorkingMessage.getNozzleNo(), 2);
			p6DS.addString("ConnectDevNo", p6_WorkingMessage.getConnectNozzleNo(), 2);
			// 시스템 시간설정
			p6DS.addString("SysTime", p6_WorkingMessage.getSystemTime(), 12);
			
			returnMessage = p6DS.getByteStream();
		}
		else if(workingMessageCommand.equals(IPumpConstant.COMMANDID_GB))
		{
			// 결제 승인응답02
			GB_WorkingMessage gb_WorkingMessage = (GB_WorkingMessage) workingMessage;
			String cmdId = IConstant.POSPROTOCOL_COMMANDID_HF;
			
			DataStruct gbDS = new DataStruct();
			
			gbDS.addString("Command", workingMessageCommand, 2);
			gbDS.addString("DeviceNo", gb_WorkingMessage.getNozzleNo(), 2);
			gbDS.addString("ConnectDevNo", gb_WorkingMessage.getConnectNozzleNo(), 2);
			gbDS.addVString("MessageType", gb_WorkingMessage.getMessageType(), 4);
			
			if(MessageLogUtil.isMfMessage(gb_WorkingMessage.getMessageType())){
				cmdId = IConstant.POSPROTOCOL_COMMANDID_MF;
			}

//			UPOSMessage 헤더를 덧붙인다.
			POSHeader posHeader = new POSHeader(IConstant.POSPROTOCOL_TYPE_KH,
							    				IConstant.POSPROTOCOL_TYPE_ODT,
							    				cmdId,
							    				IConstant.POSPROTOCOL_TYPE_KH,
							    				GlobalUtility.appending0Pre(gb_WorkingMessage.getConnectNozzleNo(), 4),
							    				GlobalUtility.getDateYYYYMMDDHHMMSS(),
							    				GlobalUtility.getUniqueMessageID());
			
			
			/*//TEST START 
			
			if(gb_WorkingMessage.getUnityMessage().getPaymentInfo() != null){		
				ArrayList<UPOSMessage_PaymentInfo_Item> tempPIA = gb_WorkingMessage.getUnityMessage().getPaymentInfo().getPayMentInfoList();				
				if(tempPIA != null){					
					if(tempPIA.size() > 0){
						for(int i=0; tempPIA.size()>i; i++){
							UPOSMessage_PaymentInfo_Item tempPI = tempPIA.get(i);
							ArrayList<UPOSMessage_ItemInfo_Item> tempUIIA = tempPI.getItem_info().getItemInfoList();
							for(int j=0; tempUIIA.size()> j; j++){
								UPOSMessage_ItemInfo_Item tempUII = tempUIIA.get(j);
								double basePrice = Double.parseDouble(tempUII.getUnitPrice_after_discount()) - 100000;	 //할인단가
								double priceInt = Double.parseDouble(tempUII.getOilPrice_after_discount()); // 결제 금액
								double literInt = GlobalUtility.getValueByCertainDecimal(getPresetLiter(priceInt, basePrice), 3); // 할인된 단가로 구한 리터
								
								LogUtility.getPumpALogger().info("[결제모듈개선 프로젝트 ] ********* 금액 : "+tempUII.getOilPrice_after_discount());
								LogUtility.getPumpALogger().info("[결제모듈개선 프로젝트 ] ********* 변경 전 수량/단가 : "+  tempUII.getUnitPrice_after_discount() +"/" + tempUII.getUnitPrice_after_discount());
								
								String literStr = GlobalUtility.getMultipleWith1000(literInt);
								
								LogUtility.getPumpALogger().info("[결제모듈개선 프로젝트 ] ********* 변경 후 수량/단가 : "+  literStr +"/" + GlobalUtility.getStringValue(literInt));
								

								tempUII.setOilAmount(literStr);
								tempUII.setUnitPrice_after_discount(GlobalUtility.getStringValue(basePrice));
								
							}							
						}					
					}
				}
			}
			
			
			//TEST END
*/			
			
			
			byte[] bodyUpos = UPOSMessageUtility.createUPOSByteArray(gb_WorkingMessage.getUnityMessage());
			
//			upos header + body 조합 
			byte[] preamble = posHeader.mergeHeaderBody(posHeader.convertHeaderToPOSContentWithoutDataLength(), bodyUpos);
//			header 처음에 SOH(0x01)과 body 끝에 ETB(0x17)를 붙인다.
    		preamble = UPOSMessageUtility.getByteWithSOH_ETB(preamble);
    		
//    		header부분의 data length에 CRC 길이 제외 한 길이를 세팅 
    		byte[] dataLen= new byte[]{preamble[34], preamble[35], preamble[36], preamble[37]};
    		String len = Integer.toString(Integer.valueOf(new String(dataLen))-4);
    		byte[] tempDataLen = GlobalUtility.appending0Pre(len, 4).getBytes();
    		
//    		header부분의 data length 수정 
    		for(int i=0; i<tempDataLen.length; i++)
    		{
    			preamble[34+i] = tempDataLen[i];
    		}
    		
    		byte[] tempGbData = gbDS.getByteStream();
//    		tempGbData : upos를 제외한 GB전문,  preamble : 헤더를 포함한 통합전문 
			byte[] gbData = new byte[tempGbData.length + preamble.length + 1];
			
			// gbData 배열에 tempGbData 값을 복사한다.
			System.arraycopy(tempGbData, 0, gbData, 0, tempGbData.length);
			// tempGbData을 복사한 gbData 배열에 unityMessage 를 덧붙인다.
			System.arraycopy(preamble, 0, gbData, tempGbData.length, preamble.length);
//		  	0x1C(FS)를 덧붙여서 전문을 전송한다.
			gbData[gbData.length-1] = (byte)0x1C;
			
			/*========================================================================================*/
			
			LogUtility.getPumpALogger().info("♣ ♣ ♣ ♣ GB 전문 MessageType:nozNo = " + gb_WorkingMessage.getMessageType() + " : " + gb_WorkingMessage.getNozzleNo());
			
			int messgeType = Integer.parseInt(gb_WorkingMessage.getMessageType());
			UPOSMessage gbUpos = gb_WorkingMessage.getUnityMessage();
			
		
			if(MessageLogUtil.isMfMessage(gb_WorkingMessage.getMessageType())){			
				UPOSMessage_PaymentInfo paymentInfo = gbUpos.getPaymentInfo();
				if(paymentInfo != null){
					ArrayList<UPOSMessage_PaymentInfo_Item> paymentInfoList = paymentInfo.getPayMentInfoList();
					if(paymentInfoList != null){
						for(int i=0; i<paymentInfoList.size(); i++) {
							UPOSMessage_ItemInfo item_info = paymentInfoList.get(i).getItem_info();
							int recordNo = Integer.parseInt(item_info.getRecordNo());
							ArrayList<UPOSMessage_ItemInfo_Item> itemInfoList = null;
							
							if(recordNo > 0)
							{
								itemInfoList = item_info.getItemInfoList();
								
								for(int j=0; j<recordNo; j++)
								{
									UPOSMessage_ItemInfo_Item item = itemInfoList.get(j);
									LogUtility.getPumpALogger().debug("▶ ▶ ▶ ▶ " + j+1 + "행 : 유종  = " + item.getGoodsCode() + " , 할인전 단가 = " +item.getUnitPrice_before_discount());
									LogUtility.getPumpALogger().debug("▶ ▶ ▶ ▶ 할인단가  = " +(Integer.parseInt(item.getUnitPrice_before_discount()) - Integer.parseInt(item.getUnitPrice_after_discount())) + " 할인후 단가 = " +item.getUnitPrice_after_discount());
									LogUtility.getPumpALogger().debug("▶ ▶ ▶ ▶ 수량 = " + item.getOilAmount());
									LogUtility.getPumpALogger().debug(item.print());
								}
							}
						}
					}
				}
			} else {
				UPOSMessage_ItemInfo item_info = gbUpos.getItem_info();
				int recordNo = Integer.parseInt(item_info.getRecordNo());
				ArrayList<UPOSMessage_ItemInfo_Item> itemInfoList = null;
				
				if(recordNo > 0)
				{
					itemInfoList = item_info.getItemInfoList();
					
					for(int i=0; i<recordNo; i++)
					{
						UPOSMessage_ItemInfo_Item item = itemInfoList.get(i);
						LogUtility.getPumpALogger().debug("▶ ▶ ▶ ▶ " + i+1 + "행 : 유종  = " + item.getGoodsCode() + " , 할인전 단가 = " +item.getUnitPrice_before_discount());
						LogUtility.getPumpALogger().debug("▶ ▶ ▶ ▶ 할인단가  = " +(Integer.parseInt(item.getUnitPrice_before_discount()) - Integer.parseInt(item.getUnitPrice_after_discount())) + " 할인후 단가 = " +item.getUnitPrice_after_discount());
						LogUtility.getPumpALogger().debug("▶ ▶ ▶ ▶ 수량 = " + item.getOilAmount());
						LogUtility.getPumpALogger().debug(item.print());
					}
				}
			}
			
			switch (messgeType) {
//				할인단가, 거래종류 
			case 4208:
				
				int tradRecordNo = Integer.parseInt(gbUpos.getTradeCondition().getRecordNo());
				ArrayList<UPOSMessage_TradeCondition_Item> tradeConditionList = gbUpos.getTradeCondition().getTradeConditionList();
				
				for(int i=0; i<tradRecordNo; i++)
				{
					UPOSMessage_TradeCondition_Item item = tradeConditionList.get(i);
					LogUtility.getPumpALogger().debug("▶ ▶ ▶ ▶ 거래종류 = " + item.getCustcar_no() + " ledCode = " + gbUpos.getLed_code());
					LogUtility.getPumpALogger().debug("▶ ▶ ▶ ▶ 고객종류 = " + item.getCustcard_no() + " 소수점처리방식  = " + gbUpos.getCust_code());
				}
				
				LogUtility.getPumpALogger().debug("▶ ▶ ▶ ▶ 한도적용기준= " + gbUpos.getLimit_type() + " 한도량,금액= " + gbUpos.getLimit_amt());
				LogUtility.getPumpALogger().debug("▶ ▶ ▶ ▶ 잔량, 잔액 = " +gbUpos.getLimit_amt() );
				
				// 외상거래처인 경우 
				if(tradeConditionList.get(0).getCustcar_no().equals("01"))
				{
					
					LogUtility.getPumpALogger().debug("▶ ▶ ▶ ▶ 외상거래처 보너스 적립유무  = " + gbUpos.getLoanCustBonus_yn());
				}
			
				break;
				
			case 4202:
				
				LogUtility.getPumpALogger().debug("▶ ▶ ▶ ▶ 현장할인 여부  = " + gbUpos.getPromptDiscount_yn());
				break;
				
			case 12:
			case 14:
			case 32:
			case 34:
			case 62:
			case 82:
			case 84:
			case 72:
			case 9994:
				
				LogUtility.getPumpALogger().debug("▶ ▶ ▶ ▶ VAN 응답코드 = " + gbUpos.getVan_Res_Code() + " : " + gbUpos.getDisplay_msg());
				LogUtility.getPumpALogger().debug("▶ ▶ ▶ ▶ 신용카드승인번호 = " + gbUpos.getCredit_auth_no());
				LogUtility.getPumpALogger().debug("▶ ▶ ▶ ▶ 결제금액 = " + gbUpos.getPayment_amt() + ", 선불카드잔액 = " + gbUpos.getPayCard_balance());
				LogUtility.getPumpALogger().debug("▶ ▶ ▶ ▶ 보너스 응답코드  = " + gbUpos.getLoyaltyReqCode() + " 보너스 메세지  = " + gbUpos.getBonRS_msg());
				
				break;
				
			default: 
				
				
				break;
			}
			
			
			/*========================================================================================*/
			returnMessage = gbData;
			
			
		}
		else if(workingMessageCommand.equals(IPumpConstant.COMMANDID_PA))
		{
			// 노즐 제어명령(주유기)
			PA_WorkingMessage pa_WorkingMessage = (PA_WorkingMessage) workingMessage;
			
			DataStruct fcDS = new DataStruct();
			
			fcDS.addString("Command", workingMessageCommand, 2);
			fcDS.addString("DeviceNo", pa_WorkingMessage.getConnectNozzleNo(), 2);
			fcDS.addString("ConnectDevNo", pa_WorkingMessage.getNozzleNo(), 2);
			
			fcDS.addString("NozzleState", pa_WorkingMessage.getNozzleState(), 1);
			
			returnMessage = fcDS.getByteStream();
		}
		
		else if(workingMessageCommand.equals(IPumpConstant.COMMANDID_PB))
		{
			// 정액 / 정량 설정
			PB_WorkingMessage pb_WorkingMessage = (PB_WorkingMessage) workingMessage;
			
			DataStruct pbDS = new DataStruct();
			
			pbDS.addString("Command", workingMessageCommand, 2);
			pbDS.addString("DeviceNo", pb_WorkingMessage.getNozzleNo(), 2);
			pbDS.addString("ConnectDevNo", pb_WorkingMessage.getConnectNozzleNo(), 2);
			
			pbDS.addString("Command2", pb_WorkingMessage.getCommandSet(), 1);
			pbDS.addString("Liter", pb_WorkingMessage.getLiter(), 7);
			pbDS.addString("BasePrice", pb_WorkingMessage.getBasePrice(), 6);
			pbDS.addString("Price", pb_WorkingMessage.getPrice(), 8);
			
			returnMessage = pbDS.getByteStream();
		}
		else if(workingMessageCommand.equals(IPumpConstant.COMMANDID_P3_1))
		{
			// 
			P3_1_WorkingMessage p3_1_WorkingMessage = (P3_1_WorkingMessage) workingMessage;
			
			DataStruct p3DS = new DataStruct();

			p3DS.addString("Command", "P3", 2);
			p3DS.addString("DeviceNo", p3_1_WorkingMessage.getNozzleNumber(), 2);
			p3DS.addString("ConnectDevNo", p3_1_WorkingMessage.getConnectNozzleNo(), 2);
			
			p3DS.addString("NozzleNo", p3_1_WorkingMessage.getNozzleNumber(), 2);
			p3DS.addString("GoodsCode", p3_1_WorkingMessage.getGoodsCode(), 18);
			p3DS.addString("BasePrice", p3_1_WorkingMessage.getBasePrice(), 6);
			
			returnMessage = p3DS.getByteStream();
		}
		else if(workingMessageCommand.equals(IPumpConstant.COMMANDID_BS))
		{
//			세차권 바코드 응답(s/c -> ODT)
			BS_WorkingMessage bs_WorkingMessage = (BS_WorkingMessage) workingMessage;
			
			DataStruct bsDS = new DataStruct();
			
			bsDS.addString("Command", workingMessageCommand, 2);
			bsDS.addString("DeviceNo", bs_WorkingMessage.getNozzleNo(), 2);
			bsDS.addString("ConnectDevNo", bs_WorkingMessage.getConnectNozzleNo(), 2);
			bsDS.addString("Barcode", bs_WorkingMessage.getBarcode(), 32);

			returnMessage = bsDS.getByteStream();
			
		}
		else if(workingMessageCommand.equals(IPumpConstant.COMMANDID_PU))
		{
			//SelfODT Update/Install (s/c -> ODT)
			PU_WorkingMessage pu_WorkingMessage = (PU_WorkingMessage) workingMessage;
			
			DataStruct puDS = new DataStruct();
			puDS.addString("Command", workingMessageCommand, 2);
			puDS.addString("DeviceNo", pu_WorkingMessage.getNozzleNo(), 2);
			puDS.addString("ConnectDevNo", pu_WorkingMessage.getConnectNozzleNo(), 2);
			
			returnMessage = puDS.getByteStream();			
		}
		
		
		// 로그 출력 
		if (returnMessage != null) {
//			LogUtility.getPumpALogger().debug(
//					workingMessageCommand + "WorkingMessage -> [" + new String(returnMessage) + "]"); 
			
		} else {
			LogUtility.getPumpALogger().debug(
					workingMessageCommand + "WorkingMessage -> " + "null");
		}	// end if
		
		return returnMessage;
		
	}	// end genearateByteArray
	
	
	public double getPresetLiter(double priceDou, double basePriceDou) {
		double literDou = priceDou / basePriceDou ;
		double temp = literDou * 10000L ;
		temp = Math.round(temp) ;
		temp = temp / 10L ;
		int literInt = (int) Math.round(temp) ;
		literDou = (double)literInt / 1000L ;				
		return literDou ; 
	}
	
	
	/**
	 * ODT 전문을 WorkingMessage로 변환한다.
	 * 
	 * @param message[] 	: ODT 전문. 
	 * @param command		: WorkingMessage Command
	 * @return				: WorkingMessage 
	 */
	public WorkingMessage generateWorkingMessage(byte[] message, String command) throws Exception {
		WorkingMessage returnMessage = null;	
		
		if(command.equals(IPumpConstant.COMMANDID_PM))
		{
			// ODT 모드 정보 
			DataStruct pmInterface = GSSelfOdtDS.getDS(IPumpConstant.COMMANDID_PM);
			pmInterface.setByteStream(message);
			
			PM_WorkingMessage pmWorkingMessage =  new PM_WorkingMessage();
			pmWorkingMessage.setCommand(command);
			pmWorkingMessage.setNozzleNo((String)pmInterface.getValue("DeviceNo"));
			pmWorkingMessage.setConnectNozzleNo((String)pmInterface.getValue("ConnectDevNo"));
			pmWorkingMessage.setMode((String)pmInterface.getValue("Mode"));
			
			returnMessage = pmWorkingMessage;
			
		}
		
		else if(command.equals(IPumpConstant.COMMANDID_GA))
		{
			// 결제 승인처리
			DataStruct gaInterface = GSSelfOdtDS.getDS(IPumpConstant.COMMANDID_GA);
			gaInterface.setVByteStream(message);
			
			GA_WorkingMessage gaWorkingMessage =  new GA_WorkingMessage();
			
			gaWorkingMessage.setCommand((String)gaInterface.getValue("Command"));
			gaWorkingMessage.setNozzleNo((String)gaInterface.getValue("DeviceNo"));
			gaWorkingMessage.setConnectNozzleNo((String)gaInterface.getValue("ConnectDevNo"));
			gaWorkingMessage.setMessageType((String)gaInterface.getValue("MessageType"));
			gaWorkingMessage.setCardNumber((String)gaInterface.getValue("CardNumber"));
			gaWorkingMessage.setBonusCard((String)gaInterface.getValue("BonusCard"));
			gaWorkingMessage.setCustCardNo((String)gaInterface.getValue("CustCardNo"));
			gaWorkingMessage.setCashReceiptNo((String)gaInterface.getValue("CashReceiptNo"));
			// 수량, 단가 , 금액, 할부개월수 값이 입력되지 않으면 워킹메세지에 값을 세팅하지 않아 초기값을 유지하도록 한다.
			String liter = (String)gaInterface.getValue("Liter");
			String basePrice = (String)gaInterface.getValue("BasePrice");			
			String price = (String)gaInterface.getValue("Price");
			if(!liter.equals(""))
			{
				gaWorkingMessage.setLiter(liter);
			}
			
			if(!basePrice.equals(""))
			{
				gaWorkingMessage.setBasePrice(basePrice);
			}
			
			if(!price.equals(""))
			{
				gaWorkingMessage.setPrice(price);
			}
			
			gaWorkingMessage.setLedCode((String)gaInterface.getValue("LedCode"));
			gaWorkingMessage.setCreatedTime((String)gaInterface.getValue("CreatedTime"));

			// 헤더를 포함한 upos전체 전문
			byte[] uPosByte = (byte[])gaInterface.getValue("UPosByte");
			
			uPosByte = ByteUtil.removeFirstByte(uPosByte, IConstant.DELIMITER_SOH);
			uPosByte = ByteUtil.removeLastByte(uPosByte, IConstant.DELIMITER_ETB);
			
			// 모바일결제 (결제모듈개선 프로젝트 2021.01.06)
			if(uPosByte[2]=='M' && uPosByte[3]=='E')
			{
				POS_ME pos_me = new POS_ME(uPosByte);
				gaWorkingMessage.setUnityMessage(pos_me.getUPosMsg());
				toPrint(uPosByte);
			}
			// 모바일결제 (결제모듈개선 프로젝트 2021.01.06)
			else if(uPosByte[2]=='M' && uPosByte[3]=='F')
			{
				POS_MF pos_mf = new POS_MF(uPosByte);
				gaWorkingMessage.setUnityMessage(pos_mf.getUPosMsg());				
				toPrint(uPosByte);
			}					
			// led 코드가 없으면 신용승인요청 LED=S 이면  첫 4201 전문 요청 
			else if(gaWorkingMessage.getLedCode().equals("") || gaWorkingMessage.getLedCode().equals("S") )
			{
				// 통합 전문을 UPOS Message(HE)로 변환한다. 
				POS_HE pos_he = new POS_HE(uPosByte);				
				gaWorkingMessage.setUnityMessage(pos_he.getUPosMsg());
			}
			else
			{
				// 통합 전문을 UPOS Message(HF)로 변환한다. 
				POS_HF pos_hf = new POS_HF(uPosByte);
				gaWorkingMessage.setUnityMessage(pos_hf.getUPosMsg());		
				
			}
		
			// 민감한 정보(신용카드 번호 ) 메모리 삭제 시작 
			
			ClearUtil.setClearString(message);
			ClearUtil.setClearString(uPosByte);
			message = null;
			uPosByte = null;
			// 민감한 정보(암호화된 신용카드번호, 신용카드 비밀번호, 신용카드 번호 ) 메모리 삭제 끝
			
			returnMessage = gaWorkingMessage;
			
		}
		else if(command.equals(IPumpConstant.COMMANDID_GT))
		{
			
			// 판매완료 처리 (영수증 출력 후 전송)
			DataStruct gtInterface = GSSelfOdtDS.getDS(IPumpConstant.COMMANDID_GT);
			gtInterface.setByteStream(message);
			
			GT_WorkingMessage gtWorkingMessage =  new GT_WorkingMessage();
			
			gtWorkingMessage.setCommand((String)gtInterface.getValue("Command"));
			gtWorkingMessage.setNozzleNo((String)gtInterface.getValue("DeviceNo"));
			gtWorkingMessage.setConnectNozzleNo((String)gtInterface.getValue("ConnectDevNo"));
			gtWorkingMessage.setCreatedTime((String)gtInterface.getValue("CreatedTime"));
			// 수량, 단가 , 금액값이 입력되지 않으면 워킹메세지에 값을 세팅하지 않아 초기값을 유지하도록 한다.
			String liter = (String)gtInterface.getValue("Liter");
			String basePrice = (String)gtInterface.getValue("BasePrice");
			String price = (String)gtInterface.getValue("Price");
			
			if(!liter.equals(""))
			{
				gtWorkingMessage.setLiter(liter);
			}
			
			if(!price.equals(""))
			{
				gtWorkingMessage.setPrice(price);
			}
			if(!basePrice.equals(""))
			{
				gtWorkingMessage.setBasePrice(basePrice);
			}
			
			returnMessage = gtWorkingMessage;
		}
		
		else if(command.equals(IPumpConstant.COMMANDID_BI))
		{
			// 지폐투입정보
			DataStruct biInterface = GSSelfOdtDS.getDS(IPumpConstant.COMMANDID_BI);
			biInterface.setByteStream(message);

			BI_WorkingMessage biWorkingMessage = new BI_WorkingMessage();
			
			biWorkingMessage.setCommand((String)biInterface.getValue("Command"));
			biWorkingMessage.setNozzleNo((String)biInterface.getValue("DeviceNo"));
			biWorkingMessage.setConnectNozzleNo((String)biInterface.getValue("ConnectDevNo"));
			biWorkingMessage.setCash((String)biInterface.getValue("Cash"));
			biWorkingMessage.setCashCount((String)biInterface.getValue("CashCount"));
			biWorkingMessage.setTime((String)biInterface.getValue("Time"));
			
			returnMessage = biWorkingMessage;
		}
		else if(command.equals(IPumpConstant.COMMANDID_BC))
		{
			// 지폐투입 취소정보
			DataStruct bcInterface = GSSelfOdtDS.getDS(IPumpConstant.COMMANDID_BC);
			bcInterface.setByteStream(message);

			BC_WorkingMessage bcWorkingMessage = new BC_WorkingMessage();
			
			bcWorkingMessage.setCommand((String)bcInterface.getValue("Command"));
			bcWorkingMessage.setNozzleNo((String)bcInterface.getValue("DeviceNo"));
			bcWorkingMessage.setConnectNozzleNo((String)bcInterface.getValue("ConnectDevNo"));
			bcWorkingMessage.setCashCount((String)bcInterface.getValue("CashCount"));
			bcWorkingMessage.setTime((String)bcInterface.getValue("Time"));
			
			returnMessage = bcWorkingMessage;
		}
		
		else if(command.equals(IPumpConstant.COMMANDID_S3))
		{
			// 주유 중  자료전송
			DataStruct s3Interface = GSSelfOdtDS.getDS(IPumpConstant.COMMANDID_S3);
			s3Interface.setByteStream(message);

			S3_WorkingMessage s3WorkingMessage = new S3_WorkingMessage();
			
			s3WorkingMessage.setCommand((String)s3Interface.getValue("Command"));
			s3WorkingMessage.setNozzleNo((String)s3Interface.getValue("DeviceNo"));
			s3WorkingMessage.setConnectNozzleNo((String)s3Interface.getValue("ConnectDevNo"));
			// 수량, 단가 , 금액값이 입력되지 않으면 워킹메세지에 값을 세팅하지 않아 초기값을 유지하도록 한다.
			String liter = (String)s3Interface.getValue("Liter");
			String basePrice = (String)s3Interface.getValue("BasePrice");
			String price = (String)s3Interface.getValue("Price");
			
			if(!liter.equals(""))
			{
				s3WorkingMessage.setLiter(liter);
			}
			
			if(!basePrice.equals(""))
			{
				s3WorkingMessage.setBasePrice(basePrice);
			}
			
			if(!price.equals(""))
			{
				s3WorkingMessage.setPrice(price);
			}
			
			s3WorkingMessage.setWDate((String)s3Interface.getValue("WDate"));
			
			returnMessage = s3WorkingMessage;
		}
		
		else if(command.equals(IPumpConstant.COMMANDID_S4))
		{
			// 주유 완료 자료전송(주유기)
			DataStruct s4Interface = GSSelfOdtDS.getDS(IPumpConstant.COMMANDID_S4);
			s4Interface.setByteStream(message);

			S4_WorkingMessage s4WorkingMessage = new S4_WorkingMessage();
			
			s4WorkingMessage.setCommand((String)s4Interface.getValue("Command"));
			s4WorkingMessage.setNozzleNo((String)s4Interface.getValue("DeviceNo"));
			s4WorkingMessage.setConnectNozzleNo((String)s4Interface.getValue("ConnectDevNo"));

			// 수량, 단가 , 금액값이 입력되지 않으면 워킹메세지에 값을 세팅하지 않아 초기값을 유지하도록 한다.
			String liter = (String)s4Interface.getValue("Liter");
			String basePrice = (String)s4Interface.getValue("BasePrice");
			String price = (String)s4Interface.getValue("Price");
			
			
			if(!liter.equals(""))
			{
				s4WorkingMessage.setLiter(liter);
			}
			
			if(!basePrice.equals(""))
			{
				s4WorkingMessage.setBasePrice(basePrice);
			}
			
			if(!price.equals(""))
			{
				s4WorkingMessage.setPrice(price);
			}
			
			s4WorkingMessage.setWDate((String)s4Interface.getValue("WDate"));
			s4WorkingMessage.setSystemTime((String)s4Interface.getValue("SystemTime"));
			s4WorkingMessage.setTotalGauge((String)s4Interface.getValue("TotalGauge"));
			s4WorkingMessage.setStatusFlag((String)s4Interface.getValue("StatusFlag"));
			s4WorkingMessage.setFlag((String)s4Interface.getValue("StatusFlag"));
			
			returnMessage = s4WorkingMessage;
		}
		
		else if(command.equals(IPumpConstant.COMMANDID_SE))
		{
			// 주유 디바이스 이상정보 전송
			DataStruct seInterface = GSSelfOdtDS.getDS(IPumpConstant.COMMANDID_SE);
			seInterface.setByteStream(message);

			SE_WorkingMessage seWorkingMessage = new SE_WorkingMessage();
			
			seWorkingMessage.setCommand((String)seInterface.getValue("Command"));
			seWorkingMessage.setNozzleNo((String)seInterface.getValue("DeviceNo"));
			seWorkingMessage.setConnectNozzleNo((String)seInterface.getValue("ConnectDevNo"));
			seWorkingMessage.setDeviceType(GlobalUtility.appending0Pre((String)seInterface.getValue("DeviceType"),2));
			seWorkingMessage.setStatus((String)seInterface.getValue("Status"));
			seWorkingMessage.setStatusCode((String)seInterface.getValue("StatusCode"));
			seWorkingMessage.setErrMsg((String)seInterface.getValue("ErrMsg"));
			seWorkingMessage.setDetectTime((String)seInterface.getValue("DetectTime"));
			
			returnMessage = seWorkingMessage;
		}
		else if(command.equals(IPumpConstant.COMMANDID_S8))
		{
			// 주유기 / ODT 상태 전송
			DataStruct s8Interface = GSSelfOdtDS.getDS(IPumpConstant.COMMANDID_S8);
			s8Interface.setByteStream(message);

			S8_WorkingMessage s8WorkingMessage = new S8_WorkingMessage();
			
			s8WorkingMessage.setCommand((String)s8Interface.getValue("Command"));
			s8WorkingMessage.setNozzleNo((String)s8Interface.getValue("DeviceNo"));	// 노즐번호 
			s8WorkingMessage.setConnectNozzleNo((String)s8Interface.getValue("ConnectDevNo"));	// odtNo
			s8WorkingMessage.setDeviceType(GlobalUtility.appending0Pre((String)s8Interface.getValue("DeviceType"), 2));
			s8WorkingMessage.setStatus("0");
			
			String status = (String)s8Interface.getValue("Status");
			//20190218 ygh 상태메시지 추가 
			String errMsg = (String)s8Interface.getValue("Status");
			// 주유기 허가 전 다운(001)
			if(status.equals("000"))
			{
				status = "651";
				errMsg = "주유기 허가 전 다운(000)";
			}
			// 주유기 허가 전 업 
			else if(status.equals("001"))
			{
				status = "652";
				errMsg = "주유기 허가 전 업(001)";
			}
			// 주유기 허가 후 다운 
			else if(status.equals("002"))
			{
				status = "651";
				errMsg = "주유기 허가 후 다운(002)";
			}
			// 주유기 허가 후 업  
			else if(status.equals("003"))
			{
				status = "652";
				errMsg = "주유기 허가 후 업(003)";
			}
			// 주유기 주유중 
			else if(status.equals("004"))
			{
				status = "653";
				errMsg = "주유기 주유중(004)";
			}
			// 주유기 주유완료 
			else if(status.equals("005"))
			{
				status = "654";
				errMsg = "주유기 주유완료(005)";
			}
			
			s8WorkingMessage.setStatusCode(status);
			s8WorkingMessage.setNozzleState(status);
			s8WorkingMessage.setErrMsg(errMsg);
			
			// 20171110 As-is KixxHUB와 동일하게 추가 
			s8WorkingMessage.setDetectTime(GlobalUtility.getDateYYYYMMDDHHMMSS().substring(2));
			
			returnMessage = s8WorkingMessage;
		}
		else if(command.equals(IPumpConstant.COMMANDID_CA))
		{
			// 거래처 고객유형확인 요청
			DataStruct caInterface = GSSelfOdtDS.getDS(IPumpConstant.COMMANDID_CA);
			caInterface.setByteStream(message);

			CA_WorkingMessage caWorkingMessage = new CA_WorkingMessage();
			
			caWorkingMessage.setCommand((String)caInterface.getValue("Command"));
			caWorkingMessage.setNozzleNo((String)caInterface.getValue("DeviceNo"));
			caWorkingMessage.setConnectNozzleNo((String)caInterface.getValue("ConnectDevNo"));
			caWorkingMessage.setCardNo((String)caInterface.getValue("CustomerNo"));

			returnMessage = caWorkingMessage;
		}
		else if(command.equals(IPumpConstant.COMMANDID_BR))
		{
			// 세차권 바코드 요청 (ODT -> S/C)
			DataStruct brInterface = GSSelfOdtDS.getDS(IPumpConstant.COMMANDID_BR);
			brInterface.setByteStream(message);

			BR_WorkingMessage brWorkingMessage = new BR_WorkingMessage();
			
			brWorkingMessage.setCommand((String)brInterface.getValue("Command"));
			brWorkingMessage.setNozzleNo((String)brInterface.getValue("DeviceNo"));
			brWorkingMessage.setConnectNozzleNo((String)brInterface.getValue("ConnectDevNo"));
			brWorkingMessage.setPrice((String)brInterface.getValue("Price"));
			brWorkingMessage.setPosReceiptNo((String)brInterface.getValue("PosReceiptNo"));
			returnMessage = brWorkingMessage;
			
		}
		else if(command.equals(IPumpConstant.COMMANDID_SJ))
		{
			// 주유시작전  TotalGauge 전송  (ODT -> S/C)
			DataStruct sjInterface = GSSelfOdtDS.getDS(IPumpConstant.COMMANDID_SJ);
			sjInterface.setByteStream(message);

			SJ_WorkingMessage sjWorkingMessage = new SJ_WorkingMessage();
			
			sjWorkingMessage.setCommand((String)sjInterface.getValue("Command"));
			sjWorkingMessage.setNozzleNo((String)sjInterface.getValue("DeviceNo"));
			sjWorkingMessage.setConnectNozzleNo((String)sjInterface.getValue("ConnectDevNo"));
			sjWorkingMessage.setSystemTime((String)sjInterface.getValue("SystemTime"));
			sjWorkingMessage.setTotalGauge((String)sjInterface.getValue("TotalGauge"));
			returnMessage = sjWorkingMessage;
			
		}
		else if(command.equals(IPumpConstant.COMMANDID_PV))
		{
			// SelfODT Version 전송(ODT -> S/C)
			DataStruct pvInterface = GSSelfOdtDS.getDS(IPumpConstant.COMMANDID_PV);
			pvInterface.setByteStream(message);
			PV_WorkingMessage pvWorkingMessage = new PV_WorkingMessage();
			
			pvWorkingMessage.setCommand((String)pvInterface.getValue("Command"));
			pvWorkingMessage.setNozzleNo((String)pvInterface.getValue("DeviceNo"));
			pvWorkingMessage.setConnectNozzleNo((String)pvInterface.getValue("ConnectDevNo"));
			pvWorkingMessage.setVersion((String)pvInterface.getValue("Version"));
			returnMessage = pvWorkingMessage;
		}
		else {
			LogUtility.getPumpALogger().error("TransGSSelf generateWorkingMessage fail! " + 
					command + " 지원하지 않는 전문.");
			returnMessage = null;
			
		}	// end if
		
		return returnMessage;
	
	}	// end generateWorkingMessage
	
	/*
	 * SOON TEST Message
	 */
	public void toPrint(byte[] rtn) {
		
		try{
			//byte[] rtn = ByteUtil.removeSOH_ETB(recv);
			
			int uposLength = rtn.length - PropertyID.P_HEADER_LENGTH;
			byte[] 	uposBytes = new byte[uposLength];
	
		    System.arraycopy(rtn, PropertyID.P_HEADER_LENGTH, uposBytes, 0, uposLength); 	// UPOS 전문 부분
	
		    UPOSMessage uPosMsg = UPOSMessageUtility.createUPOSMessage(uposBytes);
		    
		    if(uPosMsg.getPaymentInfo().getRecordNo().equals("00")) {
		    	LogUtility.getCATMLogger().info("결제모듈  결제레코드건수 : " + uPosMsg.getPaymentInfo().getRecordNo());
		    }
			else {
				
				LogUtility.getLogger().debug("[ ♣ ♣ ♣ ♣ GA 전문(모바일 결제)  ♣ ♣ ♣ ♣]");
				String[] words = uPosMsg.getString().split("#") ;
				for (int i = 0 ; i < words.length ; i++) {
					LogUtility.getLogger().debug(words[i]) ;
				}
				
			}
		}catch(Exception e){
			LogUtility.getCATMLogger().error("[결제모듈]"+new String(rtn));
		}
	}
	

}
