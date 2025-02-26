package com.gsc.kixxhub.module.pumpm.pump.util;

import org.apache.ibatis.session.SqlSession;

import com.gsc.kixxhub.common.data.ICode;
import com.gsc.kixxhub.common.data.IConstant;
import com.gsc.kixxhub.common.data.ICustConstant;
import com.gsc.kixxhub.common.data.IUPOSConstant;
import com.gsc.kixxhub.common.data.posdata.POS_DT;
import com.gsc.kixxhub.common.data.posdata.POS_DU;
import com.gsc.kixxhub.common.data.posdata.POS_DV;
import com.gsc.kixxhub.common.data.posdata.POS_DW;
import com.gsc.kixxhub.common.data.pump.CB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.GB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HD_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PG_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S9_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.data.upos.CreateUPOSMessage;
import com.gsc.kixxhub.common.data.upos.UPOSMessage;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_AffilateInfo;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_CampInfo;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_ItemInfo;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_ItemInfo_Item;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_TradeCondition;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_TradeCondition_Item;
import com.gsc.kixxhub.common.dbadapter.common.SqlSessionFactoryManager;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_CHARGING_PERSONHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_CODEMASTERHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_PRODUCTHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_VIOLATIONHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_CHARGING_PERSONData;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_PRODUCTData;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_VIOLATIONData;
import com.gsc.kixxhub.common.dbadapter.pump.handler.T_NZ_NOZZLEHandler;
import com.gsc.kixxhub.common.dbadapter.pump.vo.T_NZ_NOZZLEData;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.module.pumpm.pump.data.cust.CustReturnValue;
import com.gsc.kixxhub.module.pumpm.pump.data.cust.LimitAmount;
import com.gsc.kixxhub.module.pumpm.pump.manager.PumpMODTSaleManager;
import com.gsc.kixxhub.module.pumpm.pump.manager.PumpMTransactionManager;

public class S9Util {

	/**
	 * 출하통제 여부를 판단하여 PO전문을 POS에 전송
	 * @param dwMsg
	 * @return
	 */
	public static boolean checkControlStatus(POS_DW dwMsg){
		boolean chkControl = false;
		if (	!"00".equals(dwMsg.getControl_status()) || 
				"1".equals(dwMsg.getControl_yn()))
			chkControl = true;
		
		return chkControl ;
	}
	
	/**
	 * 위배거래처이지만 출하허가인 거래처는 주유승인을 해준다.
	 * @param dwMsg
	 * @return
	 */
	public static boolean checkControlStatus2(POS_DW dwMsg){
		boolean chkControl = false;
		if ("1".equals(dwMsg.getControl_yn()))
			chkControl = true;
		
		return chkControl ;
	}

	/**
	 * POS에서 받은 DW전문을 이용하여 다쓰노 셀프에서 요청한 거래처 정보 요청에 대한 응답전문(CB)을 만든다.
	 * @
	 */
	public static CB_WorkingMessage convertCBWorkMsgFromPOSDWMsg (POS_DW dwMsg) {
		
		CB_WorkingMessage cbWorkingMsg = new CB_WorkingMessage();
		
		cbWorkingMsg.setNozzleNo(PumpMUtil.getODTNumberFromNozzleNo(dwMsg.getDeviceID()));
		cbWorkingMsg.setConnectNozzleNo(dwMsg.getDeviceID());
		cbWorkingMsg.setCustomerType("");
		cbWorkingMsg.setMessage("");
		
		cbWorkingMsg = getCustomer_type(dwMsg, cbWorkingMsg);
		
		cbWorkingMsg.print();
		
		return cbWorkingMsg;
	}
	
	/**
	 * pi2, cwi, 2016-01-14 
	 * 고객 정보 조회 응답 전문을 GSC셀프로 보내기 위해서 uPOS 전문으로 변형 후 최종적으로 GB 전문으로 변경한다.
	 * 
	 * @param dwMsg		: POS Protocol DW 전문
	 * @return
	 */
	public static GB_WorkingMessage convertGBWorkMsgFromPOSDWMsg(POS_DW dwMsg) {
		
		PG_WorkingMessage pgWorkMsg = S9Util.convertPGWorkMsgFromPOSDWMsg(dwMsg) ;
		//충전기가 현금-무제한 고객에 대해서 내려보내는 실제 값이 현금-차량별로 셋팅 되기 때문에
		//GSC셀프의 경우 현금-무제한 고객이 존재한다면 이를 다시 현금 무제한 고객으로 변경 시켜 줌 
		//GSC셀프 고객구분에 현금-무제한이 없다면 아래의 코드 삭제
		
		String custcard_no = null; 	// 고객종류(01: 사용안함, 02: 차량별, 03: 고객별, 04: 1회정량)
		String cardadj_ind = dwMsg.getCardadj_ind() ;
		
		if (cardadj_ind.equals("00")) {
			custcard_no = "01" ;
		} else if (cardadj_ind.equals("01")) {
			custcard_no = "05" ;				
		} else if (cardadj_ind.equals("02")) {
			custcard_no = "02" ;
		} else if (cardadj_ind.equals("03")) {
			custcard_no = "03" ;
		} else if (cardadj_ind.equals("04")) {
			custcard_no = "04" ;
		} else if (cardadj_ind.equals("05")) {
			custcard_no = "06" ;
		}
		pgWorkMsg.setCusType(custcard_no);
		
		//위배거래처 체크
		//위배거래처인 경우 승인을 내어주지 않는다.
		if (checkControlStatus2(dwMsg))
			pgWorkMsg.setTransStatus("2");
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		SqlSession session = null;
		String nozzle_no = pgWorkMsg.getConnectNozzleNo();;
		String goodsCode = dwMsg.getGoods_code();
		String bonGoodsCode = "";
		String unitPrice_before_discount = "0";
		String unitPrice_after_discount = "0";
		String unitDiscount_ind = "00"; 	
		
		// With Out Pos일 경우 거래러에 등록된 상품코드와 불일 치 일 시 goodsCode가 0000으로 내려와 NullPointerException 이 발생됨
		// 상품코드가 불일치 할 경우 bonGoodsCode 값을 생성 및 할인전 단가를 조회하지 않도록 수정 하며,
		// 아래 Filler1 생성 시 거래처 종류를 강제로 02로 수정하도록 한다.
		if(!goodsCode.equals("0000") && goodsCode != null){
			try {
				session = SqlSessionFactoryManager.openSqlSession();
				
				T_NZ_NOZZLEData nzNozzleData = T_NZ_NOZZLEHandler.getHandler().getT_NZ_NOZZLEDataByNozzleNo(session ,nozzle_no)[0] ;
				T_KH_PRODUCTData productData = T_KH_PRODUCTHandler.getHandler().getT_KH_PRODUCTData(session, nzNozzleData.getGoods_code())[0] ;
				bonGoodsCode = productData.getGoods_code_bonus();
		
				double basePriceDou = Double.parseDouble(PumpMUtil.getBasePrice( productData, nzNozzleData));
				unitPrice_before_discount = GlobalUtility.getMultipleWith1000(basePriceDou);
				
			} catch (Exception e) {
				LogUtility.getLogger().error(e.getMessage(), e);
			} finally {
				SqlSessionFactoryManager.closeSqlSession(session);
			}
			
			// with out pos일 경우 거래처 정보가 정확하지 않을 시 BasePrice 값이 공백으로 내려옴
			// with pos일 경우 거래처 정보가 정확하지 않을 시 BasePrice 값이 0으로 내려옴
			if(dwMsg.getBasePrice() == null || dwMsg.getBasePrice().equals("")){
				dwMsg.setBasePrice("0");
			}
			
			Integer up = Integer.parseInt(dwMsg.getBasePrice()); // 할인 후 단가(거래처 상품 단가)
			if(up < 1){
				unitPrice_after_discount = unitPrice_before_discount;
				//dwMsg.setBasePrice(unitPrice_before_discount);
			}else{
//				unitPrice_after_discount = GlobalUtility.appending0End(dwMsg.getBasePrice(), 7);
				// pi2, cwi, 2016.06.30, 거래처 할인단가 셋팅 시 1000원 단위 이하로 떨어질 경우 뒤에 0이 하나 더 문제 수정
				unitPrice_after_discount = GlobalUtility.appending0Pre(GlobalUtility.getMultipleWith1000(dwMsg.getBasePrice()), 7);
				}
		}
	
		
		// 할인여부 (00: 미할인, 01: 할인)
		if((Integer.parseInt(unitPrice_before_discount) - Integer.parseInt(unitPrice_after_discount)) < (Integer.parseInt(unitPrice_before_discount))){
			unitDiscount_ind = "01"; // 할인
		}
		
		// 상품정보 레코드건수(노즐번호(1) + US + 상품코드(1) + US + 보너스상품코드 (1)+ US + 상품구분(0) + US + 할인전단가(1) + US + 수량(0) + US + 할인후 단가(1) + US + 과면세구분(0) + US + 공급가액(0)
		// 				   + US + 세금(0) + US + 할인전금액(0) + US + 할인후금액(0) + US + 전표번호(0) + US + 외상결제타입(0) + US + 할인여부 (1)+ US + 보관증번호(0) + US + 보관증발행유형(0) + US + filler)
		UPOSMessage_ItemInfo_Item itemInfo = CreateUPOSMessage.createUPOSMessage_ItemInfo_Item(nozzle_no, goodsCode, bonGoodsCode, "",
																						  unitPrice_before_discount, "", unitPrice_after_discount,
																						  "", "", "", "", "", "", "", unitDiscount_ind, "", "");
		UPOSMessage_ItemInfo item_info = null;
		item_info = UPOSMessage_ItemInfo.createUPOSMessage_ItemInfo(itemInfo);
		
		String custCard_No = pgWorkMsg.getSerialNumber(); // 거래처카드 번호
		String ss_crStNum = dwMsg.getCust_code();  		  // 거래처번호(코드)
		String ss_carNum = pgWorkMsg.getCarNumber();      // 거래처차량번호
		String ss_crStNm = pgWorkMsg.getDriverName();     // 거래처명(운전자 명 변수 사용)		
		
		String limit_type = "0";
		String limit_amt =  "0";
		String saveLimit = "0";
		
		if(dwMsg.getLimit_type() != null) limit_type = GlobalUtility.appending0Pre(pgWorkMsg.getLimitType(),2); //한도기준(01: 수량, 02: 금액)
		if(pgWorkMsg.getMonthLimit() != null) limit_amt = pgWorkMsg.getMonthLimit();    // 한도량,한도금액
		if(pgWorkMsg.getSaveLimit() != null) saveLimit = pgWorkMsg.getSaveLimit();  	// 사용량
		
		//////	 잔량 계산(한도량,한도금액 - 사용량)
		Integer limit = Integer.parseInt(limit_amt) - Integer.parseInt(saveLimit);
		String limit_remain = limit.toString();           // 잔량
		
		String led_code = pgWorkMsg.getTransStatus();     // LED코드(거래상태->외상거래 여부(1: 거래중, 2: 거래중지, 3:거래종료))
		String unitPrint_yn = pgWorkMsg.getPrintBase();   // 거래단가 단가출력여부(0: 출력안함, 1: 출력함)
		
		String carNoPrint_yn = "0"; //차량번호 출력 여부(0: 출력안함, 1: 출력함)
		if(!ss_carNum.equals("") || ss_carNum != null) carNoPrint_yn = "1";
			
		String FixedQty = "";
		if(dwMsg.getSliplimit_amt_stdcapa() != null) FixedQty = dwMsg.getSliplimit_amt_stdcapa(); // 정량값 
		
		
		String custcar_no = GlobalUtility.appending0Pre(pgWorkMsg.getTransType(),2);	  // 거래처 종류(00: 현금, 01: 외상, 02: 미등록카드)
		// PI2, CWI, 2016-02-17
		// 거래처 정보에 등록 된 상품과 사용자가 입력한 상품(노줄로 판단)이 다를 경우
		// 거래종류를 강제로 2로 수정한다.(노줄이 다를경우 할인 단가가 적용되지 않도록 하기 위함)
		String ConnectGoodsCode = "";
		try {
			ConnectGoodsCode = T_NZ_NOZZLEHandler.getHandler().getGoods_codeBYNozzle_no(nozzle_no);
		} catch (Exception e) {
			LogUtility.getLogger().error(e.getMessage(), e);
		}
		
		if (!goodsCode.equals(ConnectGoodsCode)){
			LogUtility.getLogger().info("선택한 상품코드와 등록된 거래처의 상품정보가 다릅니다.");
			custcar_no = "02";
		}else if(goodsCode.equals("0000") && goodsCode != null){
			LogUtility.getLogger().info("[With Out Pos] 선택한 상품코드와 등록된 거래처의 상품정보가 다릅니다.");
			custcar_no = "02";
		}else if(dwMsg.getBasePrice().equals("0") || dwMsg.getBasePrice() == null){
			LogUtility.getLogger().info("거래처 단가가 0입니다. 거래 불가능");
			custcar_no = "02";
		}
		
		String cust_nm = GlobalUtility.appending0Pre(pgWorkMsg.getReceiptType(), 2);  // 계산서거래종류(01: 현금포함, 02:현금불포함, 03:발행안함)
		//String cust_code =     GlobalUtility.appending0Pre(dwMsg.getRcptsheetissue_code_amtsale(), 2); 	 // 소수점처리방식(01: 절사, 02:반올림, 03:절상)
		String cust_code = "";
		if(!pgWorkMsg.getFloatTR().equals("") && pgWorkMsg.getFloatTR() != null){
			cust_code =     GlobalUtility.appending0Pre(pgWorkMsg.getFloatTR(), 2); 	 // 소수점처리방식(01: 절사, 02:반올림, 03:절상)
		}
		
		// tradeCondition = 거래종류 + US + 고객종류 + US + 계산서거래종류 + US + 소수점처리방식  -> 거래 조건으로 수
		UPOSMessage_TradeCondition_Item tradeCondition = new UPOSMessage_TradeCondition_Item();
		tradeCondition.setCustcar_no(custcar_no);
		tradeCondition.setCustcard_no(custcard_no);
		tradeCondition.setCust_nm(cust_nm);
		tradeCondition.setCust_code(cust_code);
		
		UPOSMessage_TradeCondition tradeCondition2  = null;
		tradeCondition2 =  UPOSMessage_TradeCondition.createUPOSMessage_TradeCondition(tradeCondition);
		
		
		// PI2, cwi,2016-01-18, 4208 전문을 기존 4202 전문과 동일한 포멧을 유지 하기 위해 사용하지 않는 변수를 생성한다.
		String custCar_limit_type = ""; // 거래처별차량별한도적용구분
		String bonRSCard_no = "";  
		String bonRSCard_ID = ""; // 보너스고객ID
		String local_point = ""; // 주유소(매장)점수
		String local_occurPoint = ""; // 발생매장점수
		UPOSMessage_CampInfo camp_info = null; // 캠페인정보 Structure
		UPOSMessage_AffilateInfo affilate_info = null; // 제휴카드 조회정보
		
		String download_flag = ""; // 다운로드 Flag
		String pos_saleDate = UPOSUtil.getPosSaleDate(); // POS영업일자
		String term_ID = ""; // 단말기 번호
		String loanCustBonus_yn = "1"; // 외상 거래처 보너스 적립 유무
		
		// 외상 거래처일 경우만 외상거래처 보너스 적립여부를 판 별 할수 있도록 수정
		if(custcar_no.equals("01")){
		String strData = "";
		try {
			session = SqlSessionFactoryManager.openSqlSession();
			// 거래처 보너스 적립 여부 판단
			strData = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(session, IConstant.POSPORTOCOL_CODEMASTER_0256);
			if ("0".equals(strData)) {
				LogUtility.getPumpMLogger().debug("외상거래처 보너스 적립 거부 됨."); 
				loanCustBonus_yn = "0";	//외상 거래처 : 보너스 누적(X)
			} else {
				LogUtility.getPumpMLogger().debug("외상거래처 보너스 적립 승인."); 
				loanCustBonus_yn = "1";	//외상 거래처 : 보너스 누적(O)
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);;
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);;
		}
		}
		
		String taxFreeCust_type = ""; // 면세 유형
		String fixedQty_yn = ""; 	  // 정량 입력 여부
		String keepDoc_header = "";
		String keepDoc_tail1 = "";
		String keepDoc_tail2 = "";
		String keepDoc_limitDate = "";
		
		// uPOS생성
		UPOSMessage upos = CreateUPOSMessage.createUPOSMessage_4208( IUPOSConstant.DEVICE_TYPE_3S,
																	   nozzle_no,     // 노줄번호
																	   item_info,     // 상품정보
																	   custCard_No,   // 거래처카드 번호
																	   ss_crStNum,    // 거래처번호
																	   ss_carNum,     // 거래처차량번호
																	   ss_crStNm,     // 거래처명
																	   custCar_limit_type ,
																	   limit_type,    // 한도적용기준
																	   limit_amt,     // 한도량,한도금액
																	   limit_remain,  // 잔량,잔액  
																	   bonRSCard_no ,
																	   bonRSCard_ID ,
																	   local_point ,
																	   local_occurPoint ,
																	   camp_info ,
																	   affilate_info ,
																	   download_flag,
																	   pos_saleDate ,
																	   term_ID,
																	   led_code,	  // LED코드
																	   unitPrint_yn,  // 거래단가 출력여부
																	   carNoPrint_yn, // 차량번호 출력여부
																	   loanCustBonus_yn ,
																	   taxFreeCust_type ,
																	   fixedQty_yn ,
																	   FixedQty,	  // 정량값
																	   keepDoc_header ,
																	   keepDoc_tail1,
																	   keepDoc_tail2,
																	   keepDoc_limitDate,
																	   tradeCondition2
		);
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		
		// 최종 GB로 생성
		GB_WorkingMessage gbWmsg = new GB_WorkingMessage();
		gbWmsg.setMessageType(upos.getMessageType());
		gbWmsg.setNozzleNo(pgWorkMsg.getNozzleNo());
		gbWmsg.setConnectNozzleNo(pgWorkMsg.getConnectNozzleNo());
		gbWmsg.setUnityMessage(upos);
		gbWmsg.setBasePrice(unitPrice_after_discount);
		
		return gbWmsg;
	}
	
	
	/**
	 * 고객 정보 조회 응답 전문을 소모셀프로 보내기 위해서 HD 전문으로 변경한다.
	 * 
	 * @param dwMsg		: POS Protocol DW 전문
	 * @return
	 */
	public static HD_WorkingMessage convertHDWorkMsgFromPOSDWMsg(POS_DW dwMsg) {
		
		PG_WorkingMessage pgWorkMsg = S9Util.convertPGWorkMsgFromPOSDWMsg(dwMsg) ;
		//충전기가 현금-무제한 고객에 대해서 내려보내는 실제 값이 현금-차량별로 셋팅 되기 때문에
		//소모셀프의 경우 현금-무제한 고객이 존재한다면 이를 다시 현금 무제한 고객으로 변경 시켜 줌 
		//소모셀프 고객구분에 현금-무제한이 없다면 아래의 코드 삭제
		
		String cusType = null; 	// 고객종류(1: 사용안함, 2: 차량별, 3: 고객별, 4:1회정량)
		String cardadj_ind = dwMsg.getCardadj_ind() ;
		if (cardadj_ind.equals(ICode.DW_CARDADJ_IND_00)) {	// Default
			cusType = "1" ;	// 사용 안함
		} else if (cardadj_ind.equals(ICode.DW_CARDADJ_IND_01)) {	// 무제한
			cusType = "5" ;	// 무제한			
		} else if (cardadj_ind.equals(ICode.DW_CARDADJ_IND_02)) {	// 차량별
			cusType = "2" ;	// 차량별
		} else if (cardadj_ind.equals(ICode.DW_CARDADJ_IND_03)) {	// 거래처별
			cusType = "3" ;	// 거래처별
		} else if (cardadj_ind.equals(ICode.DW_CARDADJ_IND_04)) {	// 1회 정량
			cusType = "4" ;	// 1회 정량
		} else if (cardadj_ind.equals(ICode.DW_CARDADJ_IND_05)) {	// 정량 입력
			cusType = "6" ;	// 정량 입력
		}
		//	 ksm 2012.03.22 cusType - 8 : 매출보관 으로 처리.
		//else if (cardadj_ind.equals("06")) {
		//	cusType = "8" ;
		//}
		
		pgWorkMsg.setCusType(cusType);
		
		//위배거래처 체크
		//위배거래처인 경우 승인을 내어주지 않는다.
		if (checkControlStatus2(dwMsg))
			pgWorkMsg.setTransStatus("2");
		
		
		// 2012.07.12 ksm 세차바코드 생성 로직 수정
		// twsongkis 2015-01-28 새로운 바코드 로직 수정 - 기존 판매금액이 내려오지 않아 ""으로 처리 / 고객 정보조회 응답임
		String barCode = "";
		try {
			String printBarCode = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0440);
			
			if("1".equals(printBarCode))
				barCode = "";
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error("[CarWash BarCode]세차 바코드 출력 여부 조회시 에러 발생");
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		}
		
		return new HD_WorkingMessage(pgWorkMsg,dwMsg.getGoods_code(), barCode) ;
	}
	
	/**
	 * POS 로 부터 받은 고객카드 조회에 대한 응답 전문을 주유기로 전송하기 위해서 PG 전문을 구성한다.
	 * 
	 * @param dwMsg	: POS 로 부터 받은 DW 전문
	 * @return
	 */
	public static PG_WorkingMessage convertPGWorkMsgFromPOSDWMsg(POS_DW dwMsg) {
		
		PG_WorkingMessage pgWorkMsg = null ;
		
		String messageID = dwMsg.getMessageID() ;			// Message ID
		String odtNo = null ;								// ODT ID
		String nozzleID = dwMsg.getDeviceID() ;				// Nozzle ID
		String serialNumber = null ;						// 카드번호
		String cusNumber = "";		                      	// 거래처번호 추가 양일준 2016.04.21
		String carNumber = null;							// 차량번호 (or 충전원번호)
		String driverName = null;							// 운전자명 (or 충전원명)
		String totalLiter = null; 							// 판매수량 (소모셀프-사용안함 space,충전기-사용함)
		String up = null;									// 판매단가
		String jpLiter = null;								// 전표량
		String transType = null;							// 거래종류(0: 현금, 1: 외상, 2: 미등록카드)
		String cusType = null; 								// 고객종류(1: 사용안함, 2: 차량별, 3: 고객별, 4:1회정량
															//        			5: 무제한, 6: 정량입력, 7: 충전원카드)
		String transStatus = null; 							// 거래상태->외상거래 여부(1: 거래중, 2: 거래중지, 3:거래종료)
		String printBase = null; 							// 단가출력여부(0: 출력안함, 1:판매단가, 2:점두가)
		String depositST = null; 							// 보관증발행여부(1: 발행안함, 2:발행)
		String floatTR = null; 								// 소수점처리방식(1: 절사, 2:반올림, 3:절상)
		String receiptType = null; 							// 계산서거래종류(1: 현금포함, 2:현금불포함, 3:발행안함)
		String monthLimit = "0"; 							// 한도수량
		String saveLimit = "0"; 							// 사용량
		String limitType = "1"; 							// 한도기준(1: 수량, 2: 금액)
		
		try {
			odtNo = PumpMUtil.getODTNumberFromNozzleNo(nozzleID) ;
			serialNumber = dwMsg.getCust_card_no() ;	
			cusNumber = dwMsg.getCust_code();
			carNumber = dwMsg.getCar_no();					
			driverName = dwMsg.getDrive_name();		
			up = PumpMUtil.convertNumberFormatFromPOSToPump(dwMsg.getBasePrice() , 4, 2) ;	
			jpLiter = PumpMUtil.convertNumberFormatFromPOSToPump(dwMsg.getSliplimit_amt_stdcapa() , 4, 3) ;
			
			/*
			 * card_code_base			transType (0:현금 , 1:외상, 2:미등록카드)
			 * 		01 : 현금 거래처		0
			 * 		02 : 외상 거래처		1
			 * 		03 : 용역 보관		0
			 * 		04 : 매출 보관		0
			 * 		06 : 미등록 카드		2
			 */
			String card_code_base = dwMsg.getCard_code_base() ;

			if (card_code_base.equals(ICode.CARD_CODE_BASE_01)) {
				transType = "0" ;
			} else if (card_code_base.equals(ICode.CARD_CODE_BASE_02)) {
				transType = "1" ;
			} else if (card_code_base.equals(ICode.CARD_CODE_BASE_03)) {
				transType = "0" ;
			} else if (card_code_base.equals(ICode.CARD_CODE_BASE_04)) {
				transType = "0" ;
			} else if (card_code_base.equals(ICode.CARD_CODE_BASE_06)) {
				transType = "2" ;
			} 

			/*
			 * cardadj_ind			cus_type
			 * 		00 : Default	1 : 사용안함
			 * 		01 : 무제한		5 : 무제한
			 * 		02 : 차량별		2 : 차량별
			 * 		03 : 거래처별		3 : 고객별
			 * 		04 : 1회 정량		4 : 1회 정량
			 * 		05 : 정량입력		6 : 정량입력
			 */
			String cardadj_ind = dwMsg.getCardadj_ind() ;
			if (cardadj_ind.equals(ICode.DW_CARDADJ_IND_00)) {			// 사용 안함
				cusType = "1" ;	// 사용 안함
			} else if (cardadj_ind.equals(ICode.DW_CARDADJ_IND_01)) {	// 무제한
				if (card_code_base.equals(ICode.CARD_CODE_BASE_01)) { 
				// 고객구분이 현금거래처-무제한 일 경우 ODT는 현금-무제한이 없기 때문에 값을 차량별 로 셋팅해 준다. 
					cusType = "2" ;	// 차량별
				} else {
					cusType = "5" ;	// 무제한
				}
			} else if (cardadj_ind.equals(ICode.DW_CARDADJ_IND_02)) {	// 차량별
				cusType = "2" ;	// 차량별
			} else if (cardadj_ind.equals(ICode.DW_CARDADJ_IND_03)) {	// 거래처별
				cusType = "3" ;	// 거래처별
			} else if (cardadj_ind.equals(ICode.DW_CARDADJ_IND_04)) {	// 1회 정량
				cusType = "4" ;	// 1회 정량
			} else if (cardadj_ind.equals(ICode.DW_CARDADJ_IND_05)) {	// 정량 입력
				cusType = "6" ;	// 정량 입력
			} 

			/*
			 * status_code_card		transStatus
			 * 		01 : 정상		1 : 거래중
			 * 		02 : 정지		2 : 거래중지
			 * 		03 : 말소		3 : 거래종료
			 */
			String status_code_card = dwMsg.getStatus_code_card() ;
			if (status_code_card.equals(ICode.STATUS_CODE_CARD_01)) {
				transStatus = "1" ;
			} else if (status_code_card.equals(ICode.STATUS_CODE_CARD_02)) {
				transStatus = "2" ;
			} else if (status_code_card.equals(ICode.STATUS_CODE_CARD_03)) {
				transStatus = "3" ;
			}
		
			/*
			 * 단가 출력 여부
			 * rcptunitprc_ind_prt		printBase
			 * 		0 : 출력 안함			
			 * 		1 : 판매단가		
			 *		2 : 점두가			
			 */
			printBase = dwMsg.getRcptunitprc_ind_prt() ;
			
			/* 2011.02.16 ksm 단가출력여부 값 그대로 사용.
			if (rcptunitprc_ind_prt.equals("0")) {
				printBase = "0" ;
			} else if (rcptunitprc_ind_prt.equals("1")) {
				printBase = "1" ;
			} else if (rcptunitprc_ind_prt.equals("2")) {
				printBase = "2" ;
			}
			*/

						
			/*
			 * 보관증 발행 여부
			 * 		keepissue_ind		depositST
			 * 		0 : 아니요				1
			 * 		1 : 예					2
			 */
			String keepissue_ind = dwMsg.getKeepissue_ind() ;
			if (ICode.KEEPISSUE_IND_0.equals(keepissue_ind)) {
				depositST = "1" ;
			} else if (ICode.KEEPISSUE_IND_1.equals(keepissue_ind)) {
				depositST = "2" ;
			} 
			
			/*
			 * 소수점 처리 방식
			 * 		rcptsheetissue_code_amtsale			floatTR
			 * 			01 : 절사							1
			 * 			02 : 반올림							2
			 * 			03 : 절상							3
			 */
			String rcptsheetissue_code_amtsale = dwMsg.getRcptsheetissue_code_amtsale() ;
			if (rcptsheetissue_code_amtsale.equals(ICode.RCPTSHEETISSUE_CODE_AMTSALE_01)) {
				floatTR = "1" ;
			} else if (rcptsheetissue_code_amtsale.equals(ICode.RCPTSHEETISSUE_CODE_AMTSALE_02)) {
				floatTR = "2" ;
			} else if (rcptsheetissue_code_amtsale.equals(ICode.RCPTSHEETISSUE_CODE_AMTSALE_03)) {
				floatTR = "3" ;
			} 
			
			/*
			 * 계산서 거래 종류
			 */
			receiptType = "1" ; 		// default

			/*
			 * 한도 수량(금액) / 누적 사용량 (금액)( monthLimit / saveLimit )
			 * 
			 * adjbase_code_limit		limitType
			 * 		01 : 수량				1
			 * 		02 : 금액				2
			 */
			String adjbase_code_limit = dwMsg.getAdjbase_code_limit() ;
			if (adjbase_code_limit.equals(ICode.ADJBASE_CODE_LIMIT_01)) {
				limitType = "1" ;
				monthLimit = 
					PumpMUtil.convertNumberFormatFromPOSToPump(dwMsg.getLimit() , 15, 3) ; 	// 한도수량 
				saveLimit = 
					PumpMUtil.convertNumberFormatFromPOSToPump(dwMsg.getAccLimit() , 15, 3);	// 사용량	
			} else if (adjbase_code_limit.equals(ICode.ADJBASE_CODE_LIMIT_02)) {
				limitType = "2" ;	
				monthLimit = GlobalUtility.getStringValue(dwMsg.getLimit()) ; 					// 한도금액
				saveLimit = GlobalUtility.getStringValue(dwMsg.getAccLimit()) ;				// 사용금액
			} 
			
					
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
//		LogUtility.getPumpMLogger().debug("[TEST] 혹시 위에는 처리 안되고 빠져 나왔나??? nozzleID="+nozzleID);

		//위배거래처 체크
		//위배거래처인 경우 승인을 내어주지 않는다.
		if (checkControlStatus2(dwMsg))
			transStatus = "2";
		
//		고객 카드 정보를 TransactionData Class 에 저장한다.
//		LogUtility.getPumpMLogger().debug("[TEST] 고객 카드 정보를 TransactionData Class 에 저장한다 serialNumber=" + serialNumber + ", nozzleID="+nozzleID);
		if (!transType.equals("2") && !transStatus.equals("2") && !transStatus.equals("3")) {
			// transType 가 미등록카드가 아닐 때
			// transStatus 가 정지가 아닐 때
			// transStatus 가 말소가 아닐 때 만 저장함
			PumpMTransactionManager.getInstance().setCustCardNumber(nozzleID, serialNumber);	
		}
		
		pgWorkMsg = new PG_WorkingMessage(messageID,
									odtNo,
									nozzleID,
									serialNumber,
									cusNumber, 
									carNumber,
									driverName,
									totalLiter,
									up,
									jpLiter,
									transType,
									cusType,
									transStatus,
									printBase,
									depositST,
									floatTR,
									receiptType,
									monthLimit,
									saveLimit,
									limitType) ;
		
		return pgWorkMsg ;
	}
	

	/**
	 * DU 전문을 이용하여 가상의 S9 전문을 구성한다. 이는 차량조회시 차량이 1건인 경우 고객 조회를 다시 요청하도록 한다.
	 * 
	 * @param duMsg	: POS Protocol DU 전문 (고객 차량 조회 응답)
	 * @return
	 */
	public static S9_WorkingMessage createS9WorkingMessage(POS_DU duMsg) {
		LogUtility.getPumpMLogger().info("[Pump M] Convert DU to S9.duMsg.getDup()=" + duMsg.getDup()) ;
		S9_WorkingMessage s9WorkMsg = null ;
		
		
		if (duMsg.getDup() == 1) {
			String messageID = duMsg.getMessageID() ;
			String odtID = duMsg.getDeviceID() ;
			String nozzleID = duMsg.getDeviceID() ;
			String mode = "0" ;
			String serialNumber = duMsg.getCarInfoArray()[0].getCust_card_no() ;
			
			s9WorkMsg = new S9_WorkingMessage(messageID,
								odtID,
								nozzleID,
								mode,
								serialNumber) ;
		}
		LogUtility.getPumpMLogger().debug("[Pump M] Changed S9 is like below.") ;
		if (s9WorkMsg != null) s9WorkMsg.print() ;
		
		return s9WorkMsg;
	}

	private static CB_WorkingMessage getCustomer_type( POS_DW dwMsg, CB_WorkingMessage cbWorkingMsg){
		
		if ((ICode.STATUS_CODE_CARD_02.equals(dwMsg.getStatus_code_card()) || ICode.STATUS_CODE_CARD_03.equals(dwMsg.getStatus_code_card())) ) {
			if (GlobalUtility.isNullOrEmptyString(dwMsg.getCust_code()))
				// 일반고객
				cbWorkingMsg.setCustomerType(ICustConstant.DASNO_CUST_TYPE_1);
			else {
				// 거래 정지 외상 거래처
				cbWorkingMsg.setCustomerType(ICustConstant.DASNO_CUST_TYPE_0);
				cbWorkingMsg.setMessage("거래정지");
			}
			return cbWorkingMsg;
		}
		
		String saveBonusYn = "0";
		
		if (ICode.CARD_CODE_BASE_01.equals(dwMsg.getCard_code_base())) {
			//현금거래처
			cbWorkingMsg.setCustomerType(ICustConstant.DASNO_CUST_TYPE_2);
			saveBonusYn = "1";
		}
		else if (ICode.CARD_CODE_BASE_02.equals(dwMsg.getCard_code_base())) {
			//외상거래처
			try {
				saveBonusYn = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0256);
			} catch (Exception e) {
				 LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
			} 
			
			if (ICode.CARDADJ_IND_05.equals(dwMsg.getCardadj_ind())) {	// 정량 입력
				cbWorkingMsg.setCustomerType(ICustConstant.DASNO_CUST_TYPE_4);
			}else {
				//일반 외상 거래처
				cbWorkingMsg.setCustomerType(ICustConstant.DASNO_CUST_TYPE_3);
			}

		} else if (ICode.CARD_CODE_BASE_06.equals(dwMsg.getCard_code_base())) {
			//미등록카드는 일반거래
			cbWorkingMsg.setCustomerType(ICustConstant.DASNO_CUST_TYPE_2);
			cbWorkingMsg.setMessage("미등록 카드입니다.");
			saveBonusYn = "1";
		}


		if ( Double.parseDouble(dwMsg.getBasePrice()) == 0) {
			cbWorkingMsg.setCustomerType(ICustConstant.DASNO_CUST_TYPE_0);
			cbWorkingMsg.setMessage("거래조건 불일치");
		}
		
		cbWorkingMsg.setSaveBonus(saveBonusYn);
		
		//위배거래처 체크
		//위배거래처인 경우 승인을 내어주지 않는다.
		if (checkControlStatus(dwMsg))
			cbWorkingMsg.setCustomerType(ICustConstant.DASNO_CUST_TYPE_0);
		
		return cbWorkingMsg;
	}
	
	/**
	 * 충전기로부터 '고객 카드 승인요청'(S9전문) 에서 충전원 조회인 경우 충전원의 정보를 전송합니다.
	 * 
	 * @param s9WorkMsg	: 주유기로부터 올라온 S9 전문
	 * @return
	 */
	public static WorkingMessage getPGWorkingMessageForChargingPerson(S9_WorkingMessage s9WorkMsg) {
		  WorkingMessage workMsg = null ;
		  try {
			T_KH_CHARGING_PERSONData chargingPersonData = 
				  T_KH_CHARGING_PERSONHandler.getHandler().getT_KH_CHARGING_PERSONData(s9WorkMsg.getSerialNumber()) ;
			if (chargingPersonData != null) {
				workMsg = new PG_WorkingMessage() ;
				((PG_WorkingMessage)workMsg).setSerialNumber(chargingPersonData.getCardno_nbr()) ;
				((PG_WorkingMessage)workMsg).setCarNumber(chargingPersonData.getEmpl_no()) ; ;
				((PG_WorkingMessage)workMsg).setDriverName(chargingPersonData.getEmpl_name()) ;
				((PG_WorkingMessage)workMsg).setNozzleNo(s9WorkMsg.getNozzleNo()) ;
				((PG_WorkingMessage)workMsg).setCusType("7") ;				// 충전원 카드 설정
				((PG_WorkingMessage)workMsg).setTransType("0") ;			// 현금 설정
				((PG_WorkingMessage)workMsg).setTransStatus("1") ;			// 거래중 설정		
				LogUtility.getPumpMLogger().debug("[Pump M] Charging Card") ;
				
				PumpMODTSaleManager.setChargingPerson(s9WorkMsg.getNozzleNo(), chargingPersonData.getEmpl_no()) ;
			} 
		  } catch (Exception e) {
			  LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		  } finally {
/*			  if ((workMsg == null) && (s9WorkMsg.getSerialNumber().startsWith("0190610000229879"))) {
				  LogUtility.getPumpMLogger().info("[Pump M] 충전원을 강제로 등록합니다.");
					workMsg = new PG_WorkingMessage() ;
					((PG_WorkingMessage)workMsg).setSerialNumber("0190610000229879") ;
					((PG_WorkingMessage)workMsg).setCarNumber("0010") ; ;
					((PG_WorkingMessage)workMsg).setDriverName("박종호") ;
					((PG_WorkingMessage)workMsg).setNozzleNo(s9WorkMsg.getNozzleNo()) ;
					((PG_WorkingMessage)workMsg).setCusType("7") ;			// 충전원 카드 설정
					((PG_WorkingMessage)workMsg).setTransType("0") ;			// 현금 설정
					((PG_WorkingMessage)workMsg).setTransStatus("1") ;		// 거래중 설정		
					LogUtility.getPumpMLogger().debug("[Pump M] Charging Card") ;
					
					PumpMODTSaleManager.setChargingPerson(s9WorkMsg.getNozzleNo(), "0010") ;
			  } else */
				  if (workMsg == null) {
					workMsg = new PG_WorkingMessage() ;
					((PG_WorkingMessage)workMsg).setNozzleNo(s9WorkMsg.getNozzleNo()) ;
					((PG_WorkingMessage)workMsg).setCusType("7") ;		// 충전원 카드 설정
					((PG_WorkingMessage)workMsg).setTransType("2") ;		// 미등록 카드 설정
					((PG_WorkingMessage)workMsg).setTransStatus("2") ;	// 거래 중지 설정	
					LogUtility.getPumpMLogger().debug("[Pump M] No Charging Card") ;
			  }
		  }
		  workMsg.setMessageID(s9WorkMsg.getMessageID()) ;
		  return workMsg ;
	}
	
	/**
	 * 고객 차량 조회를 POS 로 요청하기 위해서 DT 전문을 구성한다.
	 * 
	 * @param messageID		: Message ID
	 * @param nozzleNo		: 노즐 번호
	 * @param car_short_no	: 차량 단축 번호
	 * @return
	 */
	public static POS_DT getPOSPumpM_DT(String messageID, String nozzleNo, String car_short_no) {
		return new POS_DT(messageID, nozzleNo, car_short_no);
	}

	/**
	 * POS 로 고객카드 조회 요청을 하기 위해서 DV 전문을 생성한다.
	 * 
	 * @param messageID			: Message ID
	 * @param nozzle_no			: 노즐 번호
	 * @param khTransactionID	: KH 처리번호
	 * @param cust_card_no		: 거래처 카드 번호
	 * @return
	 */
	public static POS_DV getPOSPumpM_DV(String messageID, String nozzle_no, String khTransactionID, String cust_card_no) {
		POS_DV dvPumpMData = null ;
		SqlSession session = null;
		
		String goods_code = "" ;	// 상품 코드
		String basePrice = "" ;		// 점두가
		try {
			session = SqlSessionFactoryManager.openSqlSession();
			
			T_NZ_NOZZLEData nzData = T_NZ_NOZZLEHandler.getHandler().getT_NZ_NOZZLEDataByNozzleNo(session, nozzle_no)[0] ;
			goods_code = nzData.getGoods_code() ;
			basePrice = T_KH_PRODUCTHandler.getHandler().getBasePrice(session, goods_code, nzData.getSelf_ind_exist()) ;
		
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}
		
		dvPumpMData = new POS_DV(messageID, nozzle_no, khTransactionID, cust_card_no, goods_code, basePrice);
		return dvPumpMData ;
	}
	
	/**
	 * POS 로 부터 응답이 없어서 DV 전문 요청에 대한 DW 응답 전문을 KixxHub 자체적으로 생성한다.
	 * 
	 * @param dvPosMsg	: POS 로 전송했던 DV 전문
	 * @return			: 자체 생성한 DW 전문
	 */
	public static POS_DW processPOSPumpM_DV(POS_DV dvPosMsg) {
		LogUtility.getPumpMLogger().info("[Pump M] 자체적으로 DV 전문을 이용하여 DW 전문을 구성한다.") ;		
		// Debug Purpose
		LogUtility.getPumpMLogger().info(dvPosMsg.toString());
		String messageID 							= dvPosMsg.getMessageID() ;				
		String deviceID 							= dvPosMsg.getDeviceID() ;			// 노즐 번호
		String khTransactionID 						= dvPosMsg.getKhTransactionID() ;	// KH 처리번호
		String cust_card_no 						= dvPosMsg.getCust_card_no() ;		// 거래처 카드 번호
		String car_no 								= "" ;								// 차량 번호
		String drive_name 							= "" ;								// 운전자 명
		String cust_code 							= "" ;
		String goods_code 							= dvPosMsg.getGoods_code() ;		// 상품 코드 (Default 는 노즐의 상품 코드)
		String basePrice 							= "" ;								// 판매 단가
		String cust_cd_item 						= "" ;								// 거래처 유형
		String card_code_base 						= "" ;								// 카드 기준
		String rentlimit_proc_ind_overlimit			= ICode.PROC_IND_OVERLIMIT_01 ;		// 외상결제타입(2b)
		String cardadj_ind 							= ICode.CARDADJ_IND_00 ;			// 카드 적용 구분
		String status_code_card						= ICode.STATUS_CODE_CARD_02 ;		// 거래 상태 (Default : 정지)
		String sliplimit_amt_stdcapa 				= "" ;								// 전표한도 1회 정량
		String rcptunitprc_ind_prt 					= "" ;								// 영수증단가표기여부
		String keepissue_ind 						= "" ;								// 보관증발행여부
		String rcptsheetissue_code_amtsale			= "" ;								// 매출금액처리구분
		String receipt_type	 						= "" ;								// 계산서 거래 종류
		String limit_type 							= "" ;								// 한도종류
		String adjbase_code_limit 					= "" ;								// 한도적용기준
		String limit 								= "0" ;								// 한도수량
		String accLimit 							= "0" ;								// 누적사용량
		
		POS_DW dwPosPumpMsg = null ;
		CustReturnValue custReturnValue = CustUtil.processCustWithCustCardNo(dvPosMsg.getDeviceID() ,
				dvPosMsg.getCust_card_no() ,
				ICode.DX_TAXFREECUST_TYPE_0) ;

		int state = ICustConstant.STATE_0 ;
		
		if (custReturnValue != null) {
			state = custReturnValue.getState() ;
			
			car_no = custReturnValue.getCarno_nbr() ;						// 차량 번호
			drive_name = custReturnValue.getCust_name() ;					// 운전자 명
			cust_code = custReturnValue.getCust_code() ;					// 거래처 번호
			cust_cd_item = custReturnValue.getCust_cd_item() ;				// 거래처 유형
			rentlimit_proc_ind_overlimit = custReturnValue.getProc_ind_overlimit() ;	// 한도초과판매처리구분

			// 카드 기준 설정 : card_code_base
			switch (state) {			
				case ICustConstant.STATE_0 : 
				case ICustConstant.STATE_1 :{
					card_code_base = ICode.CARD_CODE_BASE_06 ;
					break ;
				}
				case ICustConstant.STATE_10 : 
				case ICustConstant.STATE_11 : 
				case ICustConstant.STATE_100 : 
				case ICustConstant.STATE_110 : 
				case ICustConstant.STATE_12 : 
				case ICustConstant.STATE_13 :{
					// 현금  고객 
					card_code_base = ICode.CARD_CODE_BASE_01 ;
					break ;
				}
				case ICustConstant.STATE_30 : 
				case ICustConstant.STATE_31 : 
				case ICustConstant.STATE_32 :
				case ICustConstant.STATE_33 :
				case ICustConstant.STATE_40 : 
				case ICustConstant.STATE_41 : 
				case ICustConstant.STATE_42 :
				case ICustConstant.STATE_43 :
				case ICustConstant.STATE_50 : 
				case ICustConstant.STATE_51 : 
				case ICustConstant.STATE_52 :
				case ICustConstant.STATE_53 :
				case ICustConstant.STATE_60 : 
				case ICustConstant.STATE_61 : 
				case ICustConstant.STATE_62 : 
				case ICustConstant.STATE_63 : 
				case ICustConstant.STATE_90 :
				case ICustConstant.STATE_91 :
				case ICustConstant.STATE_92 :
				case ICustConstant.STATE_93 :{
					// 외상 고객
					card_code_base = ICode.CARD_CODE_BASE_02 ;
					break ;
				}
				case ICustConstant.STATE_70 : {
					// 용역 보관 고객 - 점두가
					card_code_base = ICode.CARD_CODE_BASE_03 ;
					break ;
				}
				case ICustConstant.STATE_80 : {
					// 매출 보관 고객 - 점두가
					card_code_base = ICode.CARD_CODE_BASE_04 ;
					break ;
				}
				default : {
					card_code_base = ICode.CARD_CODE_BASE_06 ;
					break ;
				}				
			}

			// 카드 적용 구분 설정 : cardadj_ind
			switch (state) {			
				case ICustConstant.STATE_0 : 
				case ICustConstant.STATE_1 :
				case ICustConstant.STATE_10 : 
				case ICustConstant.STATE_11 : 
				case ICustConstant.STATE_12 : 
				case ICustConstant.STATE_13 : 
				case ICustConstant.STATE_70 : 
				case ICustConstant.STATE_80 : 
				case ICustConstant.STATE_100 : 
				case ICustConstant.STATE_110 : {
					cardadj_ind = ICode.DW_CARDADJ_IND_00 ;	// 사용 안함
					break ;
				}
				case ICustConstant.STATE_30 : 
				case ICustConstant.STATE_31 : 
				case ICustConstant.STATE_32 : 
				case ICustConstant.STATE_33 : {
					cardadj_ind = ICode.DW_CARDADJ_IND_01 ;	// 무제한
					break ;
				}
				case ICustConstant.STATE_40 : // 외상 거래처 - 차량별한도 - 정상
				case ICustConstant.STATE_41 : 
				case ICustConstant.STATE_42 : 
				case ICustConstant.STATE_43 : {
					cardadj_ind = ICode.DW_CARDADJ_IND_02 ;	// 차량별
					break ;
				}
				case ICustConstant.STATE_50 : // 외상 거래처 - 거래처별한도 - 정상
				case ICustConstant.STATE_51 : 
				case ICustConstant.STATE_52 : 
				case ICustConstant.STATE_53 : {
					cardadj_ind = ICode.DW_CARDADJ_IND_03 ;	// 거래처별
					break ;
				}
				case ICustConstant.STATE_60 : 	// 외상 거래처 - 1회 정량  - 정상
				case ICustConstant.STATE_61 : 
				case ICustConstant.STATE_62 : 
				case ICustConstant.STATE_63 : {
					cardadj_ind = ICode.DW_CARDADJ_IND_04 ;	// 1회 정량
					break ;
				}
				case ICustConstant.STATE_90 : 	// 외상 거래처 - 정량입력  - 정상
				case ICustConstant.STATE_91 : 
				case ICustConstant.STATE_92 : 
				case ICustConstant.STATE_93 : {
					cardadj_ind = ICode.DW_CARDADJ_IND_05 ;	// 정량 입력
					break ;
				}
				default : {
					cardadj_ind = ICode.DW_CARDADJ_IND_00 ;				
					break ;
				}				
			}		
			
			// 상품 코드 : goods_code
			switch (state) {			
				case ICustConstant.STATE_11 : 
				case ICustConstant.STATE_13 :
				case ICustConstant.STATE_31 : 
				case ICustConstant.STATE_33 :
				case ICustConstant.STATE_41 : 
				case ICustConstant.STATE_43 :
				case ICustConstant.STATE_51 : 
				case ICustConstant.STATE_53 :
				case ICustConstant.STATE_61 : 
				case ICustConstant.STATE_63 :
				case ICustConstant.STATE_91 : 
				case ICustConstant.STATE_93 : {
					goods_code = ICode.DW_GOODS_NOT_MATCHING ;
					break ;
				}
				default : {
					goods_code = dvPosMsg.getGoods_code() ;				
					break ;
				}				
			}	
			
			status_code_card = custReturnValue.getTrans_code_status() ;			// 거래 상태			
			
			// PL이 없는 경우 HOS의 기초정보 > 기준정보 > 환경설정 화면의 매장기본설정 탭의 설정코드 0251, 
			// 여신 미등록 상품 현금처리 여부가 '1'인 경우 점두가로 단가를 세팅한다.
			if (Integer.parseInt(custReturnValue.getDiscountBasePrice()) == 0) {
				String custsale_ind = null ;
				
				try {
					custsale_ind = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0251);
				} catch (Exception e) {
					LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
				}
				
				if ((custsale_ind != null) && (custsale_ind.equals("1"))) {
					LogUtility.getPumpMLogger().debug("[Pump M] 상품 불일치 이지만, '거래처고객판매여부'(매장마스터) 가 판매이기 " +
							"때문에 점두가로 판매를 한다. custsale_ind=" + custsale_ind) ;
					basePrice = custReturnValue.getBasePrice();
				} else {
					/**
					 * 2016. 4. 14. 오후 15:48:31, PI2, 이택원 Bug Fix.
					 * 	PL 여신에 미등록되어 있고, 매장기본설정 역시 판매 불가인 경우
					 * 	카드를 미등록 카드로 변경을 하고, 상태를 정지로 강제로 설정함.
					 */
					basePrice = custReturnValue.getBasePrice(); 
					card_code_base = ICode.CARD_CODE_BASE_06 ;
					status_code_card = ICode.STATUS_CODE_CARD_02 ;
				}
			} else {
				basePrice = custReturnValue.getDiscountBasePrice() ;			// 판매 단가				
			}
			
			sliplimit_amt_stdcapa = custReturnValue.getAmount1() ;				// 전표한도 1회 정량
			rcptunitprc_ind_prt = custReturnValue.getRcptunitprc_ind_prt() ;	// 영수증단가표기여부
			keepissue_ind = custReturnValue.getKeepissue_ind() ;				// 보관증발행여부
			rcptsheetissue_code_amtsale = custReturnValue.getRcptsheetissue_code_amtsale() ;	// 매출금액처리구분
			receipt_type = ICode.DW_RECEIPT_TYPE_1 ;					// 계산서 거래 종류

			/**
			 * rcptunitprc_ind_prt (POS , KH)		ODT
			 * 		0	인쇄 안함						0 (영수증 단가 표시 - X)
			 * 		1	거래 단가						1 (영수증 단가 표시 - O)
			 * 		2	점두가						1 (영수증 단가 표시 - O)
			 */
			/*  2011.02.16 ksm 영수증 단가 출력여부값 그대로 사용.
			if ("0".equals(rcptunitprc_ind_prt)) {
				rcptunitprc_ind_prt = "0" ;
			} else if ("1".equals(rcptunitprc_ind_prt)) {
				rcptunitprc_ind_prt = "1" ;
			} else if ("2".equals(rcptunitprc_ind_prt)) {
				rcptunitprc_ind_prt = "2" ;
			}
			*/
			
			// 한도종류 : limit_type
			switch (state) {			
				case ICustConstant.STATE_40 : {
					limit_type = ICode.DY_LIMIT_TYPE_01 ;
					break ;
				}
				case ICustConstant.STATE_50 : {
					limit_type = ICode.DY_LIMIT_TYPE_02 ;
					break ;
				}	
				default : {
					limit_type = ICode.DY_LIMIT_TYPE_00 ;
					break ;	
				}
			}
			
			// 한도적용 기준 : adjbase_code_limit
			switch (state) {			
				case ICustConstant.STATE_40 : 
				case ICustConstant.STATE_50 : {
					LimitAmount limitAmount = custReturnValue.getLimitAmount() ;

					limit = limitAmount.getLimit() ;
					accLimit = limitAmount.getUsedAmount() ;
					int priceOrLiter = limitAmount.getPricePrLiter() ;	
					
					if (priceOrLiter == ICustConstant.LIMIT_AMOUNT_LITER) {
						adjbase_code_limit = ICode.ADJBASE_CODE_LIMIT_01 ;
					} else {
						adjbase_code_limit = ICode.ADJBASE_CODE_LIMIT_02 ;						
					}
					
					break ;
				}	
			}
			
		} else {
			LogUtility.getPumpMLogger().debug("[Pump M] 거래처 진행 이후 리턴 값이 없어서 Default 값을 구성한다.") ;
			card_code_base = ICode.CARD_CODE_BASE_06 ;
			cardadj_ind = ICode.CARDADJ_IND_00 ;
			status_code_card = ICode.STATUS_CODE_CARD_02 ;
		}
		


		/**
		 * CC하부 거래처 위배거래처 체크
		 */
		SqlSession session = null;

		String control_status = "00";
		String control_yn = "0";
		
		try {
			session = SqlSessionFactoryManager.openSqlSession();
			T_KH_VIOLATIONData data = T_KH_VIOLATIONHandler.getHandler().getT_KH_VIOLATIONDataByCustCode(session, cust_code);
			
			if (data != null){
				control_status = data.getControl_status();
				control_yn = data.getControl_yn();
			} 
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}		
		
		dwPosPumpMsg = new 	POS_DW(messageID, 
				deviceID, 
				khTransactionID,
				cust_card_no,
				car_no,
				drive_name,
				cust_code,
				goods_code,
				basePrice,
				cust_cd_item,
				card_code_base,
				cardadj_ind,
				status_code_card,
				rentlimit_proc_ind_overlimit,
				sliplimit_amt_stdcapa,
				rcptunitprc_ind_prt,
				keepissue_ind,
				rcptsheetissue_code_amtsale,
				receipt_type,
				limit_type,
				adjbase_code_limit,
				limit,
				accLimit,
				control_status,
				control_yn)  ;

		LogUtility.getPumpMLogger().debug("[Pump M] DW Content is like below.") ;
		LogUtility.getPumpMLogger().info(dwPosPumpMsg.toString());
		return dwPosPumpMsg ;
	}
	
}
