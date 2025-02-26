package com.gsc.kixxhub.device.pumpa.translation;

import java.util.Vector;

import com.gsc.kixxhub.common.data.IConstant;
import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.posdata.POSHeader;
import com.gsc.kixxhub.common.data.posdata.POS_HE;
import com.gsc.kixxhub.common.data.posdata.POS_HF;
import com.gsc.kixxhub.common.data.pump.BB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.CP_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.D1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.D2_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.D3_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.D4_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.D5_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.D6_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.D7_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PF_CarInfo;
import com.gsc.kixxhub.common.data.pump.PF_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PG_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PI_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PP_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PQ_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S3_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S5_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S8_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S9_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SF_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SG_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SH_RepInfo;
import com.gsc.kixxhub.common.data.pump.SH_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SJ_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SK_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SL_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.data.pump.XA_WorkingMessage;
import com.gsc.kixxhub.common.data.upos.UPOSMessageUtility;
import com.gsc.kixxhub.common.utility.ByteUtil;
import com.gsc.kixxhub.common.utility.ClearUtil;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.Command;
import com.gsc.kixxhub.device.pumpa.common.DataStruct;
import com.gsc.kixxhub.device.pumpa.devDatas.OdtGSGas;


/**
 * 
 * 생성일자 : 2016.03.28 양일준
 * 내용 : TCP/IP 통신방식 충전기 ODT용  byte <-> WorkingMessage 변환 클래스 
 *
 * 1. byte -> WorkingMessage
 * 		- T1 -> S8
 * 		- T2 -> T2
 * 		- T3 -> S9
 * 		- SK -> SL
 * 		- T4 -> SH`
 * 		- TC -> SF
 * 		- TB -> SG
 * 		- TA -> SJ
 * 		     -> S5
 * 		- TD -> TD
 * 
 * 
 * 2. WorkingMessage -> byte
 * 		- D1 -> CD
 * 		- D2 -> CE
 * 		- XA -> XA
 * 		- D3 -> CF
 * 		- D4 -> CG
 * 		- D5 -> CH
 * 		- D6 -> CI
 * 		- D7 -> CN
 * 		- PB -> CJ
 * 		- PG -> C2
 * 		- PF -> C3
 * 		- PP -> CO
 * 		- PQ -> CK
 * 		- P9 -> CB
 * 		- P8 -> CC
 * 		- SI -> CC
 * 		- PE -> C1
 * 		- CP -> CP
 * 		- ACK -> ACK
 * 		- NAK -> NAK
 * 
 * 
 * @author GSC
 *
 */
public class TransGSGasOdt extends Translation {
    
	/**
	 * ODT 전문을 WorkingMessage로 변환한다.
	 * 
	 * @param message	: ODT 전문
	 * @param command	: WorkingMessage Command
	 * @return			: WorkingMessage 
	 */
	public WorkingMessage generateWorkingMessage(byte[] message, 
													String command) throws Exception {
		WorkingMessage returnMessage = null;
		boolean printLogFlag = true; 
		
		// byte[]에 있는 명령코드를 얻는다
		String odtCommandString = this.getCommand(message);
		// byte[]의 ODT번호를 얻는다
		String odtNo 			= this.getOdtNo(message);
		
		if (odtCommandString.equals("T1")) {
			// ODT T1            : ODT 상태 표시 
			// WorkingMessage S8 : 주유기/충전기 상태 전송
			S8_WorkingMessage s8WorkingMessage = new S8_WorkingMessage();
			s8WorkingMessage.setNozzleNo(odtNo);
			
			DataStruct t1Interface = OdtGSGas.getDS("T1");
			t1Interface.setByteStream(message);
				
			String status = (String) t1Interface.getValue("screen");
				
			s8WorkingMessage.setDeviceType("04");
			s8WorkingMessage.setStatus("0");
				
			// ODT T1 Status 0 : 대기상태 
			if (status.equals("0")) {
				printLogFlag = false;
				s8WorkingMessage.setStatusCode("361");
				s8WorkingMessage.setErrMsg("대기상태");
				
			// ODT T1 Status 1 : 충전원 미등록 상태 
			} else if (status.equals("1")) {
				s8WorkingMessage.setStatusCode("362");
				s8WorkingMessage.setErrMsg("충전원 미등록 상태");
				
			// ODT T1 Status 2 : RESET 상태
			} else if (status.equals("2")) {
				s8WorkingMessage.setStatusCode("363");
				s8WorkingMessage.setErrMsg("RESET 상태");
				
			// ODT T1 Status 4 : 판매 종료 
			} else if (status.equals("4")) {
				s8WorkingMessage.setStatusCode("364");
				s8WorkingMessage.setErrMsg("판매 종료");
				
			// ODT T1 Status 5 : 결재처리중 
			} else if (status.equals("5")) {
				s8WorkingMessage.setStatusCode("365");
				s8WorkingMessage.setErrMsg("결제처리중");
				
			// ODT T1 Status 6 : 인쇄 중
			} else if (status.equals("6")) {
				s8WorkingMessage.setStatusCode("366");
				s8WorkingMessage.setErrMsg("인쇄 중");
				
			// ODT T1 Status 8 : 인쇄용지 없음 
			} else if (status.equals("8")) {
				s8WorkingMessage.setStatus("1");
				s8WorkingMessage.setStatusCode("301");
				s8WorkingMessage.setErrMsg("인쇄용지 없음");
				
			// ODT T1 Status 9 : 충전기 상태 불량 
			} else if (status.equals("9")) {
				s8WorkingMessage.setStatus("1");
				s8WorkingMessage.setStatusCode("302");
				s8WorkingMessage.setErrMsg("충전기 상태 불량");
				
			} else {
				LogUtility.getLogger().error("### Incorrect status " +
											 "in T1 ODT. Current status : " +
											 status + " ###") ; 
				
			}	// end inner if
				
			s8WorkingMessage.setErrMsg(this.generateBlank(20));
			s8WorkingMessage.setDetectTime(this.getSystemTime(12));
			s8WorkingMessage.setVersion(this.generateBlank(9));
				
			returnMessage = s8WorkingMessage;
			
		} else if (odtCommandString.equals("T2")) {
			// ODT T2 			 : 현재판매 현황 전송
			// WorkingMessage S3 : 주유/충전 중 자료전송
			S3_WorkingMessage s3WorkingMessage = new S3_WorkingMessage();
			s3WorkingMessage.setNozzleNo(odtNo);
			
			DataStruct t2Interface = OdtGSGas.getDS("T2");
			t2Interface.setByteStream(message);
				
			s3WorkingMessage.setLiter((String) t2Interface.getValue("liter"));
			s3WorkingMessage.setPrice(
					((String) t2Interface.getValue("price")).substring(2));
			s3WorkingMessage.setWDate(this.getSystemTime(6));	
				
			returnMessage = s3WorkingMessage;
			
		} else if (odtCommandString.equals("T3")) {
			// ODT T3 			 : 고객카드 승인 요청 
			// WorkingMessage S9 : 고객카드 승인 요청
			S9_WorkingMessage s9WorkingMessage = new S9_WorkingMessage();
			s9WorkingMessage.setNozzleNo(odtNo);
			
			DataStruct t3Interface = OdtGSGas.getDS("T3");
			t3Interface.setByteStream(message);
				
			s9WorkingMessage.setMode((String) t3Interface.getValue("mode"));
			s9WorkingMessage.setSerialNumber(
					(String) t3Interface.getValue("carNumber"));
				
			returnMessage = s9WorkingMessage;
			
		/**
		 * 프로젝트 : PI2
		 * 변경내용 : 신규(충전기 ODT 신용, 신용+보너스, 현금영수증 승인 요청, 캠페인주유완료 정보 요청)
		 * 변경일자 : 2015.12.23 양일준
		 */
		}  else if (odtCommandString.equals(IPumpConstant.COMMANDID_SK)) {
			//결제 승인처리
			DataStruct skInterface = OdtGSGas.getDS(IPumpConstant.COMMANDID_SK);
			skInterface.setVByteStream_Gas(message, 8);

			SK_WorkingMessage skWorkingMessage =  new SK_WorkingMessage();
			
			skWorkingMessage.setNozzleNo((String)skInterface.getValue("DeviceNo"));
			skWorkingMessage.setCommand((String)skInterface.getValue("Command"));
			skWorkingMessage.setConnectNozzleNo((String)skInterface.getValue("ConnectDevNo"));			
			skWorkingMessage.setMessageType((String)skInterface.getValue("MessageType"));
			skWorkingMessage.setCardNumber((String)skInterface.getValue("CardNumber"));
			skWorkingMessage.setBonusCard((String)skInterface.getValue("BonusCard"));
			skWorkingMessage.setCustCardNo((String)skInterface.getValue("CustCardNo"));
			skWorkingMessage.setCashReceiptNo((String)skInterface.getValue("CashReceiptNo"));
			String amount = (String)skInterface.getValue("Amount");
			String basePrice = (String)skInterface.getValue("BasePrice");	
			String price = (String)skInterface.getValue("Price");
			skWorkingMessage.setLedCode((String)skInterface.getValue("LedCode"));
			skWorkingMessage.setCreatedTime((String)skInterface.getValue("CreatedTime"));
			
			//수량, 단가 , 금액, 할부개월수 값이 입력되지 않으면 워킹메세지에 값을 세팅하지 않아 초기값을 유지하도록 한다.
			
			if(!amount.equals(""))
			{
				skWorkingMessage.setAmount(amount);
			}
			
			if(!basePrice.equals(""))
			{
				skWorkingMessage.setBasePrice(basePrice);
			}
			
			if(!price.equals(""))
			{
				skWorkingMessage.setPrice(price);
			}
			
			//헤더를 포함한 upos전체 전문
			byte[] uPosByte = (byte[])skInterface.getValue("UPosByte");
			
			byte[] temp = null;
			
			temp = ByteUtil.removeSOH_ETB(uPosByte);
			ClearUtil.setClearString(uPosByte);
			uPosByte = temp;
			
			// led 코드가 없으면 신용승인요청
			if(skWorkingMessage.getLedCode().equals("")) {
				// 통합 전문을 UPOS Message(HE)로 변환한다. 
				POS_HE pos_he = new POS_HE(uPosByte);
				skWorkingMessage.setUnityMessage(pos_he.getUPosMsg());
				
			} else {
				//통합 전문을 UPOS Message(HF)로 변환한다. 
				POS_HF pos_hf = new POS_HF(uPosByte);
				skWorkingMessage.setUnityMessage(pos_hf.getUPosMsg());
				
			}/*
			
			LogUtility.getLogger().info(" [pumpA] 현장할인 대상 여부 : " + skWorkingMessage.getUnityMessage().getPromptDiscount_yn());*/
			
			ClearUtil.setClearString(message);
			ClearUtil.setClearString(uPosByte);
			
			returnMessage = skWorkingMessage;
				
		}  else if (odtCommandString.equals("T4")) {
			// ODT T4 			 : 판매 데이터 전송 
			// WorkingMessage SH : 충전기 ODT 최종 판매데이터 전송
			SH_WorkingMessage shWorkingMessage = new SH_WorkingMessage();
			shWorkingMessage.setNozzleNo(odtNo);
			
			// message[71]는 결재유형 개수 
			int repNoI = message[71] - 48;
			
			DataStruct  t4Interface = OdtGSGas.getDS("T4", repNoI);
			t4Interface.setByteStream(message);
				
			shWorkingMessage.setWDate(this.getSystemTime(6));
			shWorkingMessage.setSystemTime(this.getSystemTime(12));	// 시스템시간
			shWorkingMessage.setSerialNumber(
					(String) t4Interface.getValue("cardNumber"));	// 고객카드번호
			shWorkingMessage.setBoyNumber(
					(String) t4Interface.getValue("boyNumber"));	// 충전원번호
			shWorkingMessage.setTotalLiter(
					(String) t4Interface.getValue("totalLiter"));	// 수량
			shWorkingMessage.setUp1((String) t4Interface.getValue("up1"));	// 계기단가 
			shWorkingMessage.setTotalAMT1(
					(String) t4Interface.getValue("totalAMT1"));	// 계기금액
			shWorkingMessage.setUp2((String) t4Interface.getValue("up2"));	// 판매단가
			shWorkingMessage.setTotalAMT2(
					(String) t4Interface.getValue("totalAMT2"));	// 판매금액
			shWorkingMessage.setTotalGauge(
					(String) t4Interface.getValue("totalGauge"));	// 토탈게이지
			shWorkingMessage.setRepNo(
					(String) t4Interface.getValue("repNo"));		// 결재 유형 개수
					
			Vector<SH_RepInfo> repInfoVector = new Vector<SH_RepInfo>();
			
			for (int i = 0; i < repNoI; i++) {
				SH_RepInfo repInfo = new SH_RepInfo();
				repInfo.setFlag((String) t4Interface.getValue("flag" + i));		// 결재유형
				repInfo.setLiter((String) t4Interface.getValue("liter" + i));		// 결재수량
				repInfo.setAmt((String) t4Interface.getValue("amt" + i));			// 제시금액
				// 보관증 번호 또는 거스름돈
				repInfo.setKeepNumber(
						(String) t4Interface.getValue("keepNumber" + i));	
				repInfoVector.add(repInfo);
				
			}	// end for
				
			shWorkingMessage.setRepInfo(repInfoVector);
				
			returnMessage = shWorkingMessage;
				
		} else if (odtCommandString.equals("TC")) {
			// ODT TC 			 : 보관증 번호 부여 요청 
			// WorkingMessage SF : 보관증 번호 부여 요청 
			SF_WorkingMessage sfWorkingMessage = new SF_WorkingMessage();
			sfWorkingMessage.setNozzleNo(odtNo);
				
			// 내용 없음 
				
			returnMessage = sfWorkingMessage;
				
		} else if (odtCommandString.equals("TB")) {
			// ODT TB 			 : 보관량 조회 
			// WorkingMessage SG : 보관량 조회 요청 
			SG_WorkingMessage sgWorkingMessage = new SG_WorkingMessage();
			sgWorkingMessage.setNozzleNo(odtNo);
			
			DataStruct tbInterface = OdtGSGas.getDS("TB");
			tbInterface.setByteStream(message);
				
			sgWorkingMessage.setKeepNumber(
					(String) tbInterface.getValue("keepNumber"));
				
			returnMessage = sgWorkingMessage;
				
		} else if (odtCommandString.equals("TA")) {
			// ODT TA 			 : 토털게이지 전송
			// WorkingMessage SJ : 주유시작 자료 응답 
			// WorkingMessage S5 : 토털게이지 전송
			
			if ("SJ".equals(command)) {
				SJ_WorkingMessage sjWorkingMessage = new SJ_WorkingMessage();
				sjWorkingMessage.setNozzleNo(odtNo);
				
				DataStruct taInterface = OdtGSGas.getDS("TA");
				taInterface.setByteStream(message);

				sjWorkingMessage.setTotalGauge((String) taInterface
											.getValue("totalGauge"));
				sjWorkingMessage.setSystemTime(this.getSystemTime(12));

				returnMessage = sjWorkingMessage;
				
			} else {
				S5_WorkingMessage s5WorkingMessage = new S5_WorkingMessage();
				s5WorkingMessage.setNozzleNo(odtNo);

				DataStruct taInterface = OdtGSGas.getDS("TA");
				taInterface.setByteStream(message);

				s5WorkingMessage.setTotalGauge((String) taInterface
											.getValue("totalGauge"));
				s5WorkingMessage.setSystemTime(this.getSystemTime(12));

				returnMessage = s5WorkingMessage;
				
			}	// end inner if
			
		} else {
			LogUtility.getLogger().error("### Not Supported command(" + odtCommandString + ") in TransGSGasOdt ###");
			returnMessage = null;
			
		}	// end if
		
		// 로그 출력 시 ETX 이후는 생략
		int printFinishIndex = message.length;
		
		for (int i = 0; i < printFinishIndex; i++) {
			if (message[i] == IPumpConstant.DELIMITER_0X03) {
				printFinishIndex = i;
				break;
			}	// end if
		}	// end for
		
		// ETX, SUM 출력 
		printFinishIndex = printFinishIndex + 2;
		
		/*
		// 로그 출력 
		if (printLogFlag) {
			if (returnMessage != null) {
				LogUtility.getLogger().debug(
						"[" + new String(message, 0, printFinishIndex) + "]>>" + 
												returnMessage.getCommand()); 
				
			} else {
				LogUtility.getLogger().debug(
						"[" + new String(message) + "]>>" + "NULL");
				
			}	// end inner if
			
		}	// end if
		*/
		returnMessage.setConnectNozzleNo(odtNo);
		
		return returnMessage;
		
	}	// end generateWorkingMessage
	
	
	
	/**
	 * WorkingMessage를 ODT 전문으로 변환한다.
	 * 
	 * @param workingMessage 	: WorkingMessage
	 * @return					: ODT 전문 
	 */
	public byte[] generateByteStream(WorkingMessage workingMessage) throws Exception {
		byte[] returnMessage = null;
		
		String workingMessageCommand = workingMessage.getCommand();
		
		
		if (workingMessageCommand.equals(IPumpConstant.COMMANDID_D1)) {
			// WorkingMessage D1 : 충전소 내역 설정 
			// ODT CD            : 충전소 내역 설정 
			D1_WorkingMessage d1WorkingMessage = 
						(D1_WorkingMessage) workingMessage;
			
			DataStruct d1DS = new DataStruct();
			d1DS.addString("command", "CD", 2);
			d1DS.addString("storeName", d1WorkingMessage.getStoreName(), 40);
			d1DS.addString("regiNum", d1WorkingMessage.getStoreRegiNum(), 12);
			d1DS.addString("repNM", d1WorkingMessage.getRepName(), 30);
			d1DS.addString("addr", d1WorkingMessage.getStoreADDR(), 50);
			d1DS.addString("tel", d1WorkingMessage.getTel(), 16);
			d1DS.addString("goodsType", d1WorkingMessage.getGoodsType(), 30);
			d1DS.addString("basePrice", d1WorkingMessage.getBasePrice(), 6);
			d1DS.addString("storeCode", d1WorkingMessage.getStoreCode(), 10);
			
			byte[] tempArray = d1DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
							d1WorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_D2)) {
			// WorkingMessage D2 : 기본영수증 인쇄내역 
			// ODT CE            : 기본영수증 인쇄내역 
			D2_WorkingMessage d2WorkingMessage = (D2_WorkingMessage) workingMessage;
			
			DataStruct d2DS = new DataStruct();
			d2DS.addString("command", "CE", 2);
			d2DS.addString("head", d2WorkingMessage.getBaseHeadTitle(), 42);
			d2DS.addString("tail1", d2WorkingMessage.getBaseFootTitle1(), 42);
			d2DS.addString("tail2", d2WorkingMessage.getBaseFootTitle2(), 42);
			
			byte[] tempArray = d2DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
							d2WorkingMessage.getNozzleNo());

		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_XA)) {
			// WorkingMessage XA : 현금 영수증 응답 
			// ODT XA            : 현금 영수증 증명 
			XA_WorkingMessage xaWorkingMessage = 
							(XA_WorkingMessage) workingMessage;
			
			String printContent = xaWorkingMessage.getPrintContent();
			
			DataStruct xaDS = new DataStruct();
			xaDS.addString("command", "XA", 2);
			xaDS.addString("successBP", xaWorkingMessage.getSuccessBP(), 1);
			xaDS.addString("odtContent", setOdtLCD16Bytes(xaWorkingMessage.getOdtContent()), 64);
			xaDS.addString("printContent", printContent, printContent.getBytes().length);
			
			byte[] tempArray = xaDS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
							xaWorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_D3)) {
			// WorkingMessage D3 : 보관증 인쇄내역 
			// ODT CF            : 보관증 인쇄내역 
			D3_WorkingMessage d3WorkingMessage = 
						(D3_WorkingMessage) workingMessage;
			
			DataStruct d3DS = new DataStruct();
			d3DS.addString("command", "CF", 2);
			d3DS.addString("head", d3WorkingMessage.getSaveHeadTitle(), 42);
			d3DS.addString("tail1", d3WorkingMessage.getSaveFootTitle1(), 42);
			d3DS.addString("tail2", d3WorkingMessage.getSaveFootTitle2(), 42);
			
			byte[] tempArray = d3DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
						d3WorkingMessage.getNozzleNo());

		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_D4)) {
			// WorkingMessage D4 : 고객카드 인쇄내역
			// ODT CG			 : 고객카드 인쇄내역
			D4_WorkingMessage d4WorkingMessage = 
						(D4_WorkingMessage) workingMessage;
			
			DataStruct d4DS = new DataStruct();
			d4DS.addString("command", "CG", 2);
			d4DS.addString("head", d4WorkingMessage.getCcHeadTitle(), 42);
			d4DS.addString("tail1", d4WorkingMessage.getCcFootTitle1(), 42);
			d4DS.addString("tail2", d4WorkingMessage.getCcFootTitle2(), 42);
			
			byte[] tempArray = d4DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
						d4WorkingMessage.getNozzleNo());

		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_D5)) {
			// WorkingMessage D5 : 보너스카드 인쇄내역
			// ODT CH			 : 보너스카드 인쇄내역
			D5_WorkingMessage d5WorkingMessage = 
						(D5_WorkingMessage) workingMessage;
			
			DataStruct d5DS = new DataStruct();
			d5DS.addString("command", "CH", 2);
			d5DS.addString("head", d5WorkingMessage.getBcHeadTitle(), 42);
			d5DS.addString("tail1", d5WorkingMessage.getBcFootTitle1(), 42);
			d5DS.addString("tail2", d5WorkingMessage.getBcFootTitle2(), 42);
			
			byte[] tempArray = d5DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
						d5WorkingMessage.getNozzleNo());

		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_D6)) {
			// WorkingMessage D6 : 매장공시 인쇄내역 
			// ODT CI			 : 매장공시 인쇄내역
			D6_WorkingMessage d6WorkingMessage = 
						(D6_WorkingMessage) workingMessage;
			
			DataStruct d6DS = new DataStruct();
			d6DS.addString("command", "CI", 2);
			d6DS.addString("head", d6WorkingMessage.getSpiHeadTitle(), 42);
			d6DS.addString("tail1", d6WorkingMessage.getSpiFootTitle1(), 42);
			d6DS.addString("tail2", d6WorkingMessage.getSpiFootTitle2(), 42);
			
			byte[] tempArray = d6DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
						d6WorkingMessage.getNozzleNo());

		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_D7)) {
			// WorkingMessage D7 : 충전기 ODT POS 환경정보
			// ODT CN 			 : 충전기 ODT POS 환경정보
			D7_WorkingMessage d7WorkingMessage = 
						(D7_WorkingMessage) workingMessage;
			
			DataStruct d7DS = new DataStruct();
			d7DS.addString("command", "CN", 2);
			d7DS.addString("receiptMin", 
					d7WorkingMessage.getReceiptMinLiter(), 5);
			d7DS.addString("depositMin", 
					d7WorkingMessage.getDepsitMinLiter(), 5);
			d7DS.addString("minSale", 
					d7WorkingMessage.getMinSaleLiter(), 5);
			d7DS.addString("loanRemain", 
					d7WorkingMessage.getLoanRemainDisp(), 1);
			d7DS.addString("termWait", 
					d7WorkingMessage.getTremWaitSec(), 2);
			d7DS.addString("emergStop", 
					d7WorkingMessage.getEmergStopLiter(), 3);
			
			byte[] tempArray = d7DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
					d7WorkingMessage.getNozzleNo());

		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PB)) {
			// WorkingMessage PB : 정액/정량 설정
			// ODT CJ 			 : 정액/정량 설정 
			PB_WorkingMessage pbWorkingMessage = 
						(PB_WorkingMessage) workingMessage;
			
			String mode 	= pbWorkingMessage.getCommandSet();
			String price 	= pbWorkingMessage.getPrice();
			
			DataStruct cjDS = new DataStruct();
			cjDS.addString("command", "CJ", 2);
			cjDS.addString("mode", mode, 1);
			
			// PB mode 0 : 정액 설정
			if (mode.equals("0")) {
				cjDS.addString("liter", pbWorkingMessage.getLiter(), 7);
				
			// PB mode 1 : 정량 설정
			} else if (mode.equals("1")){
				cjDS.addString("amt", price.substring(1), 7);
				
			} else {
				LogUtility.getLogger().error("### Incorrect mode " +
											 "in odt 'PB'. Current mode : " +
											 mode + " ###") ;
			
			}	// end inner if
			
			byte[] tempArray = cjDS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
					pbWorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PG)) {
			// WorkingMessage PG : 고객카드 승인 응답 
			// ODT C2			 : 고객카드 승인 및 판매 승인 여부 전송 
			PG_WorkingMessage pgWorkingMessage = 
								(PG_WorkingMessage) workingMessage;
			
			DataStruct c2DS = new DataStruct();
			c2DS.addString("command", "C2", 2);
			c2DS.addString("cardNo", pgWorkingMessage.getSerialNumber(), 18);
			c2DS.addString("carNo", pgWorkingMessage.getCarNumber(), 18);
			c2DS.addString("driverName", pgWorkingMessage.getDriverName(), 30);
			c2DS.addString("liter", pgWorkingMessage.getTotalLiter(), 7);
			c2DS.addString("basePricve", pgWorkingMessage.getUp(), 6);
			c2DS.addString("jpLiter", pgWorkingMessage.getJpLiter(), 7);
			c2DS.addString("transType", pgWorkingMessage.getTransType(), 1);
			c2DS.addString("cusType", pgWorkingMessage.getCusType(), 1);
			c2DS.addString("transStatus", pgWorkingMessage.getTransStatus(), 1);
			c2DS.addString("printBase", pgWorkingMessage.getPrintBase(), 1);
			c2DS.addString("depositST", pgWorkingMessage.getDepositST(), 1);
			c2DS.addString("floatTR", pgWorkingMessage.getFloatTR(), 1);
			c2DS.addString("receiptType", pgWorkingMessage.getReceiptType(), 1);
			c2DS.addString("monthLimit", pgWorkingMessage.getMonthLimit(), 10);
			c2DS.addString("saveLimit", pgWorkingMessage.getSaveLimit(), 10);
			c2DS.addString("limitType", pgWorkingMessage.getLimitType(), 1);
			c2DS.addString("cusNo", pgWorkingMessage.getCusNumber(), 6);     // PI2, 거래처 번호 추가 양일준 2016.04.18
			LogUtility.getLogger().info(" 거래처번호:" + pgWorkingMessage.getCusNumber());  // PI2, 거래처 번호 추가 양일준 2016.04.18
		/*	
			
			LogUtility.getLogger().info(" command:" + "C2");
			LogUtility.getLogger().info(" 카드번호:" + pgWorkingMessage.getSerialNumber());
			LogUtility.getLogger().info(" 거래처번호:" + pgWorkingMessage.getCusNumber());
			LogUtility.getLogger().info(" 차량번호:" + pgWorkingMessage.getCarNumber());
			LogUtility.getLogger().info(" 고객 이름:" + pgWorkingMessage.getDriverName());
			LogUtility.getLogger().info(" 수량:" + pgWorkingMessage.getTotalLiter());
			LogUtility.getLogger().info(" 단가:" + pgWorkingMessage.getUp());
			LogUtility.getLogger().info(" 전표량:" + pgWorkingMessage.getJpLiter());
			LogUtility.getLogger().info(" 거래종류:" + pgWorkingMessage.getTransType());
			LogUtility.getLogger().info(" 고객종류:" + pgWorkingMessage.getCusType());
			LogUtility.getLogger().info(" 거래상태:" +pgWorkingMessage.getTransStatus());
			LogUtility.getLogger().info(" 단가출력여부:" + pgWorkingMessage.getPrintBase());
			LogUtility.getLogger().info(" 보관증발행여부:" + pgWorkingMessage.getDepositST());
			LogUtility.getLogger().info(" 소수점처리방식:" + pgWorkingMessage.getFloatTR());
			LogUtility.getLogger().info(" 계산서 거래 종류:" + pgWorkingMessage.getReceiptType());
			LogUtility.getLogger().info(" 월 한도수량:" + pgWorkingMessage.getMonthLimit());
			LogUtility.getLogger().info(" 누적사용량:" + pgWorkingMessage.getSaveLimit());
			LogUtility.getLogger().info(" 한도기준:" + pgWorkingMessage.getLimitType());
			*/
			
			byte[] tempArray = c2DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
								pgWorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PF)) {
			// WorkingMessage PF : 중복차량 응답 
			// ODT X3            : 중복 차량번호 전송 
			PF_WorkingMessage pfWorkingMessage = 
				(PF_WorkingMessage) workingMessage;
			
			// 중복 반복 차량 수 
			String repNo 					 = pfWorkingMessage.getRepNO();
			int repNoI 						 = Integer.parseInt(repNo);
			Vector<PF_CarInfo> carInfoVector = pfWorkingMessage.getCarInfoVector();
	
			DataStruct x3DS = new DataStruct();
			x3DS.addString("command", "X3", 2);
			x3DS.addString("repNo", repNo, 2);
//			c3DS.addString("repNo", 
//					GlobalUtility.appendingSPACEPre(String.valueOf(repNoI), 2), 2);
			
			for (int i = 0; i < repNoI; i++) {
				PF_CarInfo carInfo = carInfoVector.get(i);
				
				x3DS.addString("carNo"+i, carInfo.getCarNumber(), 12);
				x3DS.addString("cardNo"+i, carInfo.getSerialNumber(), 16);
				
				String driverName = "";
				if ( !GlobalUtility.isNullOrEmptyString(carInfo.getName())) {
					if (carInfo.getName().getBytes().length > 10) {
						byte[] aaaa = new byte[10];
						System.arraycopy(carInfo.getName().getBytes(), 0, aaaa, 0, 10);
						driverName = new String(aaaa);
					} else 
						driverName = carInfo.getName();
				}
				x3DS.addString("driverName"+i, driverName, 10);
				
//				// 다음 중복차량 데이터가 존재하면 1, 마지막 데이터는 0
//				if (i < repNoI - 1) {
//					c3DS.addString("dataFinish"+i, "1", 1);
//					
//				} else {
//					c3DS.addString("dataFinish"+i, "0", 1);
//
//				}	// end if
				
			}	// end for
			
			byte[] tempArray = x3DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
								pfWorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_SL)) {
//			 승인응답
			SL_WorkingMessage sl_WorkingMessage = (SL_WorkingMessage) workingMessage;
			
			DataStruct slDS = new DataStruct();
			slDS.addString("Command", workingMessageCommand, 2);
			slDS.addString("ConnectDevNo", sl_WorkingMessage.getConnectNozzleNo(), 2);
			slDS.addVString("MessageType", sl_WorkingMessage.getMessageType(), 4);

//			UPOSMessage 헤더를 덧붙인다.
			POSHeader posHeader = new POSHeader(IConstant.POSPROTOCOL_TYPE_KH,
							    				IConstant.POSPROTOCOL_TYPE_ODT,
							    				IConstant.POSPROTOCOL_COMMANDID_HF,
							    				IConstant.POSPROTOCOL_TYPE_KH,
							    				GlobalUtility.appending0Pre(sl_WorkingMessage.getConnectNozzleNo(), 4),
							    				GlobalUtility.getDateYYYYMMDDHHMMSS(),
							    				GlobalUtility.getUniqueMessageID());
			
			
			byte[] bodyUpos = UPOSMessageUtility.createUPOSByteArray(sl_WorkingMessage.getUnityMessage());
			
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
    		
    		byte[] tempSlData = slDS.getByteStream();
//    		tempGbData : upos를 제외한 SL전문,  preamble : 헤더를 포함한 통합전문 
			byte[] slData = new byte[tempSlData.length + preamble.length + 1];
			
			// slData 배열에 tempSlData 값을 복사한다.
			System.arraycopy(tempSlData, 0, slData, 0, tempSlData.length);
			// tempSlData을 복사한 slData 배열에 unityMessage 를 덧붙인다.
			System.arraycopy(preamble, 0, slData, tempSlData.length, preamble.length);
//		  	0x1C(FS)를 덧붙여서 전문을 전송한다.
			slData[slData.length-1] = (byte)0x1C;
			
			returnMessage = slData;
			returnMessage = this.makeProtocol(slData, 
					sl_WorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PI)) {
			// WorkingMessage PI : 신용카드 승인처리 응답
			// ODT C6            : 신용카드 결과 전송 
			// ODT C8			 : 전자상품권 결과 전송
			// ODT C9			 : 보너스카드 포인트 점수 사용 승인  
			PI_WorkingMessage piWorkingMessage = 
							(PI_WorkingMessage) workingMessage;
			
			String cardType 	= piWorkingMessage.getCardType();
			String printContent = piWorkingMessage.getPrintContent();
			byte[] tempArray 	= null;
			
			// PI CardType 0 : 신용카드 
			if (cardType.equals("0")) {
				
				DataStruct c6DS = new DataStruct();
				c6DS.addString("command", "C6", 2);
				c6DS.addString("result", piWorkingMessage.getMode(), 1);
				c6DS.addString("recogDate", 
						piWorkingMessage.getRecogDate(), 14);
				c6DS.addString("recogNumber", 
						piWorkingMessage.getRecogNumber(), 12);
				c6DS.addString("cardNumber", 
						piWorkingMessage.getCardNumber(), 16);
				c6DS.addString("cardCorpName", 
						piWorkingMessage.getCardCorpName(), 20);
				c6DS.addString("francNumber", 
						piWorkingMessage.getFrancNumber(), 16);
				c6DS.addString("noteCorpCode", 
						piWorkingMessage.getNoteCorpCode(), 3);
				c6DS.addString("noteCorpName", 
						piWorkingMessage.getNoteCorpName(), 20);
				c6DS.addString("terminalNumber", 
						piWorkingMessage.getTerminalNubmer(), 10);
				c6DS.addString("noteNumberTemp", 
						piWorkingMessage.getNoteNumberTemp(), 5);
				c6DS.addString("notice", 
						setOdtLCD16Bytes(piWorkingMessage.getNotice()), 64);
				c6DS.addString("noteNumber", 
						piWorkingMessage.getNoteNumber(), 10);
				c6DS.addString("recogConfid", 
						piWorkingMessage.getRecogConfid(), 5);
				c6DS.addString("printContent", 
						printContent, printContent.getBytes().length);
				
				tempArray = c6DS.getByteStream();
				
			// PI CardType 1 : 전자상품권  
			} else if (cardType.equals("1")) {
				DataStruct c8DS = new DataStruct();
				c8DS.addString("command", "C8", 2);
				c8DS.addString("result", piWorkingMessage.getMode(), 1);
				c8DS.addString("recogDate", 
						piWorkingMessage.getRecogDate(), 14);
				c8DS.addString("recogNumber", 
						piWorkingMessage.getRecogNumber(), 12);
				c8DS.addString("cardNumber", 
						piWorkingMessage.getCardNumber(), 16);
				c8DS.addString("cardCorpName", 
						piWorkingMessage.getCardCorpName(), 20);
				c8DS.addString("francNumber", 
						piWorkingMessage.getFrancNumber(), 16);
				c8DS.addString("noteCorpCode", 
						piWorkingMessage.getNoteCorpCode(), 3);
				c8DS.addString("noteCorpName", 
						piWorkingMessage.getNoteCorpName(), 20);
				c8DS.addString("terminalNumber", 
						piWorkingMessage.getTerminalNubmer(), 10);
				c8DS.addString("noteNumberTemp", 
						piWorkingMessage.getNoteNumberTemp(), 5);
				c8DS.addString("notice", 
						setOdtLCD16Bytes(piWorkingMessage.getNotice()), 64);
				c8DS.addString("noteNumber", 
						piWorkingMessage.getNoteNumber(), 10);
				c8DS.addString("recogConfid", 
						piWorkingMessage.getRecogConfid(), 5);
				c8DS.addString("printContent", 
						printContent, printContent.getBytes().length);
				
				tempArray = c8DS.getByteStream();
				
			// PI CardType 3 : GS 보너스카드 점수 사용
			} else if (cardType.equals("3")) {
				DataStruct c9DS = new DataStruct();
				c9DS.addString("command", "C9", 2);
				c9DS.addString("result", piWorkingMessage.getMode(), 1);
				c9DS.addString("recogDate", 
						piWorkingMessage.getRecogDate(), 14);
				c9DS.addString("recogNumber", 
						piWorkingMessage.getRecogNumber(), 12);
				c9DS.addString("cardNumber", 
						piWorkingMessage.getCardNumber(), 16);
				c9DS.addString("cardCorpName", 
						piWorkingMessage.getCardCorpName(), 20);
				c9DS.addString("francNumber", 
						piWorkingMessage.getFrancNumber(), 16);
				c9DS.addString("noteCorpCode", 
						piWorkingMessage.getNoteCorpCode(), 3);
				c9DS.addString("noteCorpName", 
						piWorkingMessage.getNoteCorpName(), 20);
				c9DS.addString("terminalNumber", 
						piWorkingMessage.getTerminalNubmer(), 10);
				c9DS.addString("noteNumberTemp", 
						piWorkingMessage.getNoteNumberTemp(), 5);
				c9DS.addString("notice", 
						setOdtLCD16Bytes(piWorkingMessage.getNotice()), 64);
				c9DS.addString("recogType", "1", 1);
				c9DS.addString("noteNumber", 
						piWorkingMessage.getNoteNumber(), 10);
				c9DS.addString("recogConfid", 
						piWorkingMessage.getRecogConfid(), 5);
				c9DS.addString("printContent", 
						printContent, printContent.getBytes().length);
				
				tempArray = c9DS.getByteStream();
			}
				
			//2012.07.09 ksm  my신한 포인트 사용 중지
			// 마케팅 전략팀 김준완 사원 CSR 요청 ( 2012-07-06 )
			// PI CardType 4 : myLG 점수사용
			/* else if (cardType.equals("4")) {
				DataStruct c9DS = new DataStruct();
				c9DS.addString("command", "C9", 2);
				c9DS.addString("result", piWorkingMessage.getMode(), 1);
				c9DS.addString("recogDate", 
						piWorkingMessage.getRecogDate(), 14);
				c9DS.addString("recogNumber", 
						piWorkingMessage.getRecogNumber(), 12);
				c9DS.addString("cardNumber", 
						piWorkingMessage.getCardNumber(), 16);
				c9DS.addString("cardCorpName", 
						piWorkingMessage.getCardCorpName(), 20);
				c9DS.addString("francNumber", 
						piWorkingMessage.getFrancNumber(), 16);
				c9DS.addString("noteCorpCode", 
						piWorkingMessage.getNoteCorpCode(), 3);
				c9DS.addString("noteCorpName", 
						piWorkingMessage.getNoteCorpName(), 20);
				c9DS.addString("terminalNumber", 
						piWorkingMessage.getTerminalNubmer(), 10);
				c9DS.addString("noteNumberTemp", 
						piWorkingMessage.getNoteNumberTemp(), 5);
				c9DS.addString("notice", setOdtLCD16Bytes(piWorkingMessage.getNotice()), 64);
				c9DS.addString("recogType", "2", 1);
				c9DS.addString("noteNumber", 
						piWorkingMessage.getNoteNumber(), 10);
				c9DS.addString("recogConfid", 
						piWorkingMessage.getRecogConfid(), 5);
				c9DS.addString("printContent", 
						printContent, printContent.getBytes().length);
				
				tempArray = c9DS.getByteStream();
					
			}*/ 
			else {
				LogUtility.getLogger().error("### Not Supported cardType " +
												 "in PI WorkingMessage. " +
												 "Current cardType : " +
												 cardType + " ###");
		
			}	// end inner if

			returnMessage = this.makeProtocol(tempArray, 
							piWorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_BB)) {
			// WorkingMessage BB : 보너스 점수 누적 응답
			// ODT BB			 : 보너스 점수 누적 / 현금 영수증 발행 결과 
			BB_WorkingMessage bbWorkingMessage = 
						(BB_WorkingMessage) workingMessage;

			String printContent = 
					this.changeNewLine(bbWorkingMessage.getPrintContent());
			
			DataStruct bbDS = new DataStruct();
			bbDS.addString("command", "BB", 2);
			bbDS.addString("success", bbWorkingMessage.getSuccessBP(), 1);
			bbDS.addString("display", setOdtLCD16Bytes(bbWorkingMessage.getOdtContent()), 64);
			bbDS.addString("printContent", printContent, printContent.getBytes().length);
			
			byte[] tempArray = bbDS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
							bbWorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PP)) {
			// WorkingMessage PP : 발행 보관증 번호 응답
			// ODT CO			 : 보관증 발급번호 부여 
			PP_WorkingMessage ppWorkingMessage = 
						(PP_WorkingMessage) workingMessage;
			
			DataStruct coDS = new DataStruct();
			coDS.addString("command", "CO", 2);
			coDS.addString("depositNumber", 
						ppWorkingMessage.getKeepNumber(), 10);
			
			byte[] tempArray = coDS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
							ppWorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PQ)) {
			// WorkingMessage PQ : 보관량 응답
			// ODT CK			 : 보관량 전송 
			PQ_WorkingMessage pqWorkingMessage = 
						(PQ_WorkingMessage) workingMessage;
			
			DataStruct ckDS = new DataStruct();
			ckDS.addString("command", "CK", 2);
			ckDS.addString("mode", pqWorkingMessage.getMode(), 1);
			ckDS.addString("liter", pqWorkingMessage.getLiter(), 7);
			ckDS.addString("basePrice", pqWorkingMessage.getUp(), 6);
			
			byte[] tempArray = ckDS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
							pqWorkingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_P9)) {
			// WorkingMessage P9 : 영업마감 처리
			// ODT CB			 : ODT 마감 
			DataStruct cbDS = new DataStruct();
			cbDS.addString("command", "CB", 2);
			
			byte[] tempArray = cbDS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
							workingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_P8)) {
			// WorkingMessage P8 : 토털게이지 자료 요청 
			// ODT CC			 : 토탈게이지 요청 
			DataStruct ccDS = new DataStruct();
			ccDS.addString("command", "CC", 2);
			
			byte[] tempArray = ccDS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
							workingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_SI)) {
			// WorkingMessage SI : 주유시작자료 요청
			// ODT CC			 : 토탈게이지 요청
			DataStruct ccDS = new DataStruct();
			ccDS.addString("command", "CC", 2);
			
			byte[] tempArray = ccDS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
								workingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_PE)) {
			// WorkingMessage PE : 주유기/충전기 상태 요청
			// ODT C1			 : ODT 호출 
			DataStruct c1DS = new DataStruct();
			c1DS.addString("command", "C1", 2);
			c1DS.addString("timeInfo", this.getSystemTime(12), 12);
			
			byte[] tempArray = c1DS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
								workingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_CP)) {
			// WorkingMessage CP : 포인트 점수 전송 
			// ODT CP			 : 포인트 점수 전송 
			CP_WorkingMessage cpWorkingMessage = 
					(CP_WorkingMessage) workingMessage;
			
			DataStruct cpDS = new DataStruct();
			cpDS.addString("command", "CP", 2);
			cpDS.addString("bonusNumber", cpWorkingMessage.getBonusCardNo(), 16);
			cpDS.addString("pointScore", cpWorkingMessage.getBonusCardPoint(), 10);
			
			byte[] tempArray = cpDS.getByteStream();
			returnMessage = this.makeProtocol(tempArray, 
								workingMessage.getNozzleNo());
			
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_ACK)) {
			// WorkingMessage ACK
			// ODT ACK
			String nozzleNo = workingMessage.getNozzleNo();
			byte[] ackMessage = new byte[6];
			
			ackMessage[0] = Command.SOH;
			ackMessage[1] = (byte) nozzleNo.charAt(0);
			ackMessage[2] = (byte) nozzleNo.charAt(1);
			ackMessage[3] = Command.STX;
			ackMessage[4] = Command.ACK;
			ackMessage[5] = Command.ETX;
			
			returnMessage = ackMessage;
			
		
		} else if (workingMessageCommand.equals(IPumpConstant.COMMANDID_NAK)) {
			// WorkingMessage NAK
			// ODT NAK
			String nozzleNo = workingMessage.getNozzleNo();
			byte[] nakMessage = new byte[6];
			
			nakMessage[0] = Command.SOH;
			nakMessage[1] = (byte) nozzleNo.charAt(0);
			nakMessage[2] = (byte) nozzleNo.charAt(1);
			nakMessage[3] = Command.STX;
			nakMessage[4] = Command.NAK;
			nakMessage[5] = Command.ETX;
			
			returnMessage = nakMessage;
			
		} else {
			LogUtility.getLogger().error("### Not Supported " +
											 "command(" + workingMessageCommand +
											 ") in TransGSGasOdt ###");
			returnMessage = null;
			
		}	// end if

		/*
		// 로그 출력 
		if (returnMessage != null) {
			LogUtility.getLogger().debug(
					workingMessageCommand + "WorkingMessage -> " +  
					"[" + new String(returnMessage) + "]"); 
			
		} else {
			LogUtility.getLogger().debug(
					workingMessageCommand + "WorkingMessage -> " + "null");
		}	// end if
		*/
		
		return returnMessage;
		
	}	// end generateByteArray



	/**
	 * ODT 전문에서 명령 코드를 추출 
	 * 
	 * @param message	: ODT 전문
	 * @return			: 명령 코드 
	 */
	private String getCommand(byte[] message) throws Exception {
		String returnMessage = null;
		
		byte[] commandArray 	= new byte[2];
		commandArray[0] 	 	= message[4];
		commandArray[1] 		= message[5];
		
		returnMessage = new String(commandArray);
		
		return returnMessage;
	
	}	// end getCommand
	
	

	/**
	 * byte 배열의 data를  완전한 ODT 전문 형태로 변환
	 * 
	 * @param data		: data
	 * @param nozzleNo	: 노즐 번호 
	 * @return			: ODT 전문 
	 */
	private byte[] makeProtocol(byte[] data, String odtNo) throws Exception {
		if (data == null) {
			return null;
			
		}	// end if
		
		byte[] returnData 		= null;
		int returnDataCounter 	= 0;
		
		
		// ETB, CRC16(4), SUM 제외 - 2015.12.28 양일준 
		int arrayLength = data.length + 5;
		
		returnData = new byte[arrayLength];
		
		// SOH, ODT_NO(2), STX, ETX
		returnData[returnDataCounter++] = Command.SOH;				// SOH
		returnData[returnDataCounter++] = (byte) odtNo.charAt(0);	// ODT_NO_1
		returnData[returnDataCounter++] = (byte) odtNo.charAt(1);	// ODT_NO_2
		returnData[returnDataCounter++] = Command.STX;				// STX
		
		for (int i = 0; i < data.length; i++) {
			returnData[returnDataCounter++] = data[i];
			
		}	// end for
		
		returnData[returnDataCounter++] = Command.ETX;				// ETX
		
		return returnData;
		
	}	// end makeProtocol
	
	
	
	/**
	 * ODT 전문에서 ODT번호를 추출
	 * 
	 * @param message	: ODT 전문 
	 * @return			: ODT 번호 
	 */
	private String getOdtNo(byte[] message) throws Exception {
		String returnMessage = null;
		
		byte[] odtNo 	= new byte[2];
		odtNo[0] 		= message[1];
		odtNo[1] 		= message[2];
		
		returnMessage = new String(odtNo);
		
		return returnMessage;
		
	}	// end getNozzleNo
	
	
	
	/**
	 * '\n' 문자를 0x1E로 변환
	 * 
	 * @param message	: '\n' 형태의 문자열
	 * @return			: 변환된 문자열
	 */
	private String changeNewLine(String message) throws Exception {
		String returnMessage;
		
		byte[] tempBs = message.getBytes();
		
		for (int i = 0; i < tempBs.length; i++) {
			// "\n" = 0xA
			if (tempBs[i] == 0xA) {
				tempBs[i] = 0x1E;
				
			}	// end if
			
		}	// end for
		
		returnMessage = new String(tempBs);
		
		return returnMessage;
		
	}	// end changeNewLine
	
	
	
	/**
	 * ODT 충전기 액정에 표시되는 문자를 16 bytes로 구분 지을 수 있도록 한다.
	 * 한글로 이루어진 문자에서 16 bytes를 맞추기 위해 필요한 경우 
	 * 15 bytes 다음 공백 한칸을 추가한다.  
	 *   
	 * 
	 * @param message : 액정에 표시되는 문자
	 * @return
	 * @throws Exception
	 */
	private String setOdtLCD16Bytes(String message) throws Exception {
		
		StringBuffer returnMessage = new StringBuffer();
		String tempString 	= null;
		byte[] tempBs 		= null;
		int bytecount 		= 0;	
		int length 			= 16;	// 구분 지을 길이
		
		for (int i=0; i < message.length(); i++)
		{
			tempString = message.substring(i, i+1);
			tempBs = tempString.getBytes();
			bytecount = bytecount + tempBs.length;
			
			if (bytecount > length )
			{
				returnMessage.append(" ");
				bytecount = 0;
				bytecount = bytecount + tempBs.length;
				returnMessage.append(tempString);
				
			} else if (bytecount == length ) {
				returnMessage.append(tempString);
				bytecount = 0;
				
			} else {
				returnMessage.append(tempString);
				
			}	// end if
			
		}	// end for
		
//		int divide 			= message.length() / length;
//		while (divide > 0) {
//			tempString = message.substring(0, length); 
//			returnMessage.append(tempString);
//			returnMessage.append(rs);
//			message = message.substring(tempString.length());
//			divide = message.length() / length;
//		}
		
		return returnMessage.toString();
		
	}	// end setOdtLCD16Bytes
	
}