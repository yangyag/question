package com.gsc.kixxhub.module.pumpm.bundle.sub;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.ibatis.session.SqlSession;

import com.gsc.kixxhub.common.data.IBeaconConstant;
import com.gsc.kixxhub.common.data.ICode;
import com.gsc.kixxhub.common.data.IConstant;
import com.gsc.kixxhub.common.data.ICustConstant;
import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.ITopicConstant;
import com.gsc.kixxhub.common.data.IUPOSConstant;
import com.gsc.kixxhub.common.data.beacon.BeaconArkInfo;
import com.gsc.kixxhub.common.data.beacon.BeaconMessage;
import com.gsc.kixxhub.common.data.beacon.BeaconMessageToByteArray;
import com.gsc.kixxhub.common.data.beacon.BeaconPumpInfo;
import com.gsc.kixxhub.common.data.beacon.ByteArrayToBeaconMessage;
import com.gsc.kixxhub.common.data.beacon.JA_BeaconMessage;
import com.gsc.kixxhub.common.data.posdata.POSHeader;
import com.gsc.kixxhub.common.data.posdata.POS_AE;
import com.gsc.kixxhub.common.data.posdata.POS_CY;
import com.gsc.kixxhub.common.data.posdata.POS_DE;
import com.gsc.kixxhub.common.data.posdata.POS_DG;
import com.gsc.kixxhub.common.data.posdata.POS_DI;
import com.gsc.kixxhub.common.data.posdata.POS_DK;
import com.gsc.kixxhub.common.data.posdata.POS_DM;
import com.gsc.kixxhub.common.data.posdata.POS_DQ;
import com.gsc.kixxhub.common.data.posdata.POS_DT;
import com.gsc.kixxhub.common.data.posdata.POS_DV;
import com.gsc.kixxhub.common.data.posdata.POS_DW;
import com.gsc.kixxhub.common.data.posdata.POS_EN;
import com.gsc.kixxhub.common.data.posdata.POS_KJ;
import com.gsc.kixxhub.common.data.posdata.POS_KK;
import com.gsc.kixxhub.common.data.posdata.POS_KK2;
import com.gsc.kixxhub.common.data.posdata.POS_KL;
import com.gsc.kixxhub.common.data.pump.ACK_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.BA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.BC_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.BI_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.BR_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.BS_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.CA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.CB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.CL_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.GA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.GB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.GT_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HC_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HE_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HF_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PB_ST_TrInfo;
import com.gsc.kixxhub.common.data.pump.PB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PG_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PI_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PP_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PV_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.QL_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.QM_WoringMessage;
import com.gsc.kixxhub.common.data.pump.S3_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S4_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S8_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S9_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SE_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SF_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SG_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SH_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SJ_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SK_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SL_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.ST_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.TD_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.TJ_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.TR_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.data.pump.XA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.format.PumpMessageFormat;
import com.gsc.kixxhub.common.data.upos.CreateUPOSMessage;
import com.gsc.kixxhub.common.data.upos.UPOSMessage;
import com.gsc.kixxhub.common.data.upos.UPOSMessageUtility;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_ItemInfo;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_ItemInfo_Item;
import com.gsc.kixxhub.common.data.upos.UPOSMessage_TradeCondition;
import com.gsc.kixxhub.common.dbadapter.beacon.handler.BeaconDataHandler;
import com.gsc.kixxhub.common.dbadapter.common.SqlSessionFactoryManager;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_BIN_INFOHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_CODEMASTERHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_PUMP_TRHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_SALES_INFOHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.handler.T_KH_STOREHandler;
import com.gsc.kixxhub.common.dbadapter.kixxhub.manager.LockingManager;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_CODEMASTERData;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_PUMP_TRData;
import com.gsc.kixxhub.common.dbadapter.kixxhub.vo.T_KH_SALES_INFOData;
import com.gsc.kixxhub.common.dbadapter.pump.handler.T_KH_CARWASH_COUPON_LISTHandler;
import com.gsc.kixxhub.common.dbadapter.pump.handler.T_NZ_NOZZLEHandler;
import com.gsc.kixxhub.common.dbadapter.pump.vo.T_KH_CARWASH_COUPON_LISTData;
import com.gsc.kixxhub.common.dbadapter.pump.vo.T_NZ_NOZZLEData;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.beacon.BeaconUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.common.utility.property.PropertyManager;
import com.gsc.kixxhub.common.utility.sync.Preamble;
import com.gsc.kixxhub.common.utility.sync.SyncManager;
import com.gsc.kixxhub.common.utility.timer.KHTimer;
import com.gsc.kixxhub.module.pumpm.pump.data.cust.CustReturnValue;
import com.gsc.kixxhub.module.pumpm.pump.data.odt.DasNoODTNozzleInfo;
import com.gsc.kixxhub.module.pumpm.pump.manager.PumpMODTSaleManager;
import com.gsc.kixxhub.module.pumpm.pump.manager.PumpMPriceManager;
import com.gsc.kixxhub.module.pumpm.pump.manager.PumpMSyncManager;
import com.gsc.kixxhub.module.pumpm.pump.manager.PumpMTransactionManager;
import com.gsc.kixxhub.module.pumpm.pump.manager.dasno.DasNoSelfPumpingManager;
import com.gsc.kixxhub.module.pumpm.pump.manager.gsc.GSCSelfPumpingManager;
import com.gsc.kixxhub.module.pumpm.pump.util.Barcode;
import com.gsc.kixxhub.module.pumpm.pump.util.CustUtil;
import com.gsc.kixxhub.module.pumpm.pump.util.ODTUtility_Common;
import com.gsc.kixxhub.module.pumpm.pump.util.ODTUtility_DaSNo;
import com.gsc.kixxhub.module.pumpm.pump.util.ODTUtility_GSCSelf;
import com.gsc.kixxhub.module.pumpm.pump.util.ODTUtility_GSC_Self;
import com.gsc.kixxhub.module.pumpm.pump.util.ODTUtility_Recharge;
import com.gsc.kixxhub.module.pumpm.pump.util.ODTUtility_SoMo;
import com.gsc.kixxhub.module.pumpm.pump.util.PumpMObjectValidation;
import com.gsc.kixxhub.module.pumpm.pump.util.PumpMUtil;
import com.gsc.kixxhub.module.pumpm.pump.util.S9Util;
import com.gsc.kixxhub.module.pumpm.pump.util.UPOSUtil;
import com.gsc.kixxhub.module.pumpm.pump.util.etc.SendFullPumpingOtion;

public class PumpAController extends PumpAControllerBase {

	private int gStep = IPumpConstant.STEP_PUMP_RESOLVED;
	private boolean isAlreadyInitialize = false ;
	private int isUseFullPumping = 1;
	private boolean logSSDC = false; //org false

	/**
	 * Pump M Component 가 정지되면서 Clear 할 작업들을 열거한다.
	 * 
	 */
	private void destroyData() {
		if (logSSDC == true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/" + "destroyData()");

		gStep = IPumpConstant.STEP_PUMP_RESOLVED;

		LockingManager.destroy();
		PumpMODTSaleManager.destroy();
		PumpMPriceManager.destroy();
		DasNoSelfPumpingManager.destroy();
		PumpMSyncManager.destroy();
		PumpMTransactionManager.destroy();
	}

	/**
	 * Pump A 로부터 온 전문에 대해서 공통적으로 처리해야 하는 부분들 외에 주유기 특성에 따라서 추가적으로 해야 할 일들을
	 * 진행한다.
	 * 
	 * @param uniqueKey :
	 *            Unique Key
	 * @param nozzleNo :
	 *            노즐 번호
	 * @param commandID :
	 *            Pump A 로 부터 받은 Command ID
	 * @param workMsg :
	 *            Pump A 로 부터 받은 전문
	 */
	private void handleWorkingMessageForAdditionalWork(String uniqueKey,
			String nozzleNo, String commandID, WorkingMessage workMsg,	String khproc_no) {
		
		if (logSSDC == true)
			LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/"+ "handleWorkingMessageForAdditionalWork()");

		if (nozzleNo == null) {
			LogUtility.getPumpMLogger().error("[Pump M] Noz=null");
			return;
		}
		LogUtility.getPumpMLogger().info("[Pump M] handleWorkingMessageForAdditionalWork. commandID=" + commandID) ;
		
		try {
			int protocolType = PumpMUtil.getConnectedRepODTProtocolFromNozzleNo(nozzleNo);
			
			switch (protocolType) {
			case IPumpConstant.PUMP_PROTOCOL_DaSNo:
			case IPumpConstant.PUMP_PROTOCOL_NewDaSNo: { // 2012.09.26 ksm
				switch (commandID) {
				case IPumpConstant.COMMANDID_S4: {

					/**
					 * [2008.11.26] by WooChul Jung 다쓰노 셀프의 경우 주유완료의 의미는 매우
					 * 중요하다. 만약 주유완료가 한번이상 오면 첫번째 것만 처리하고 나머지는 버린다. 자칫 잘못하면 이중
					 * 삼중 승인이 재차 날 위험이 있다.
					 */

					if (DasNoSelfPumpingManager.getInstance().isPumpingCompleted(nozzleNo)) {
						LogUtility.getPumpMLogger().warn("[Pump M] DasNo : Receive S4 more than one time. Drop S4");
						return;
					}

					// 주유완료인 경우
					S4_WorkingMessage s4WorkMsg = (S4_WorkingMessage) workMsg;

					String pumpingPrice = GlobalUtility.getStringValue(PumpMUtil.convertPriceFromPumpToPOS(s4WorkMsg.getPrice()));
					String pumpingLiter = PumpMUtil.convertLiterFromPumpToPOS(s4WorkMsg.getLiter());
					String pumpingBasePrice = PumpMUtil.convertBasePriceFromPumpToPOS(s4WorkMsg.getBasePrice());

					UPOSMessage preUposMsg = PumpMODTSaleManager.getPreviousUPOSMessage(nozzleNo, khproc_no);
					T_KH_PUMP_TRData pumpTrData = T_KH_PUMP_TRHandler.getHandler().getT_KH_PUMP_TRData(khproc_no);

					String prePayedPrice = null;
					double prePayedpriceInt = 0;
					double prePayedliterInt = 0;
					double prePayedbasePriceInt = 0;

					LogUtility.getPumpMLogger().debug("[Pump M] process DaSNo after receiving S4 with following value");

					pumpTrData.print();

					DasNoSelfPumpingManager.getInstance().setPumpingCompleted(nozzleNo, true);

					/**
					 * [2008.11.20] 소모셀프 ODT 로부터 결제 거절 이후 POS 로 부터 Preset 으로 인한
					 * 주유가 생길 수 있다. 이 경우 preUposMsg 는 거절의 UPOSMessage 이며, 이로 인해
					 * 문제가 생길수 있다. 아래 경우중 한가지 일 경우는 POS 로 부터의 Preset 이라 간주한다. 1.
					 * 명시적으로 POS 로 부터 Preset 인 경우 -> 이 경우만 처리한다. 2. 이 주유건과 관련된
					 * 응답 전문이 없을 경우 3. 이 주유건과 관련된 가장 최근의 응답 전문이 거절이나 회선 불량인 경우
					 * (ledCode 가 1이 아닌경우) 4. LED Code 가 없는 경우
					 */

					if (PumpMTransactionManager.getInstance().isPresetFromPOS(nozzleNo)) {
						// POS 로 부터의 결제로 가정
						LogUtility.getPumpMLogger().info("[Pump M] Pumping from POS Preset. NozID="	+ nozzleNo);

						if (pumpTrData.getPreset_qty_prc_ind().equals(ICode.PRESET_QTY_PRC_IND_0_PRICE)) {
							// 정액 주유일 경우
							LogUtility.getPumpMLogger().info("[UPOSUtil] Preset with price.");
							prePayedpriceInt = Double.parseDouble(pumpTrData.getPreset_prc());
						} else if (pumpTrData.getPreset_qty_prc_ind().equals(ICode.PRESET_QTY_PRC_IND_1_LITER)) {
							// 정량 주유일 경우
							LogUtility.getPumpMLogger().info("[UPOSUtil] Preset with liter");
							prePayedliterInt = Double.parseDouble(pumpTrData.getPreset_qty());
							prePayedbasePriceInt = Double.parseDouble(pumpTrData.getPreset_baseprice());
							prePayedpriceInt = GlobalUtility.multiple(	prePayedliterInt, prePayedbasePriceInt);
						}

						// POS 로 부터 받은 Preset 만큼 결제되었다라고 가정한다.
						prePayedPrice = GlobalUtility.getStringValue(prePayedpriceInt);

						// QL (영수증 전문) 을 생성해서 Pump A 로 전송한다.
						WorkingMessage qlMsg = ODTUtility_DaSNo.getQL_WorkingMessage_DaSNoFromPOS(nozzleNo, 
								khproc_no, 
								pumpingPrice, 
								pumpingLiter,
								prePayedPrice, 
								pumpingBasePrice, 
								true);

						LogUtility.getPumpMLogger().info("[Pump M] Send Receipt to Pump A. NozID=" + nozzleNo);
						Preamble preamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey,	SyncManager.DISE_PUMP_ADAPTER, qlMsg, "");
						sendMessage(preamble);

						// 세차권 발행 내역을 Pos A로 전송한다.
						processOPTBarcode(qlMsg, khproc_no);

					} else if (IConstant.reRequest	&& "0".equals(pumpingPrice)) {
						// ODT 로 부터의 결제이면서 주유금액이 0 원인 경우
						LogUtility.getPumpMLogger().info("[Pump M] Payment From ODT. But Pumping Price is 0");
						// 선 결제가 BL 체크인 경우는 취소 전문을 보내지 않는다.
						boolean isCash = DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzleNo).isCash();

						if (DasNoSelfPumpingManager.getInstance().getCurrentOption(nozzleNo) == IConstant.FULL_PUMPING_OPTION_0) {

							// QL (영수증 전문) 을 생성해서 Pump A 로 전송한다.
							String payedPrice = "0";

							if (isCash)
								payedPrice = DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzleNo).getHeWorkingMsg().getPrice();

							WorkingMessage qlMsg = null;

							if (PumpMUtil.getConnectedODTProtocolFromNozzleNo(nozzleNo) == 90)
								qlMsg = ODTUtility_GSC_Self.getQL_WorkingMessage_GSCSELFFromPOS(nozzleNo, 
										khproc_no,
										pumpingPrice, 
										pumpingLiter,
										payedPrice, 
										pumpingBasePrice);
							else
								qlMsg = ODTUtility_DaSNo.getQL_WorkingMessage_DaSNoFromPOS(nozzleNo, 
										khproc_no,
										pumpingPrice, 
										pumpingLiter,
										payedPrice, 
										pumpingBasePrice,
										false);

							LogUtility.getPumpMLogger().info("[Pump M] Send Receipt to Pump A. NozID=" + nozzleNo);

							Preamble pumpPreamble = PumpMUtil.createWorkingMessagePreamble( uniqueKey, SyncManager.DISE_PUMP_ADAPTER, qlMsg, "");
							sendMessage(pumpPreamble);

							// 현금이면서 주유금액이 0원일 경우
							if (isCash) {
								//	20160728 PI2 twlee
								//	현금결제이면서 주유금액이 0원이면 다쓰노는 POS에 0012전문을 보내지만 소모에서는 보내지 않는다.
								//	다쓰노와 소모의 0012전문 전송 차이에 대한 정확한 이유는 알 수 없음.
								LogUtility.getPumpMLogger().info("[Pump M] 현금 결제이면서 주유금액이 0원인 경우 POS에 주유완료와 UPOS전문을 전달한다.");

								DasNoODTNozzleInfo nozInfo = DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzleNo);

								WorkingMessage HEwrkMsg = nozInfo.getHeWorkingMsg();
								UPOSMessage uPosMsg = ODTUtility_DaSNo.createUPOSMessageFromWorkingMessage_DaSNo_For_Zero(HEwrkMsg, khproc_no);

								/**
								 * 2016.03.31 WooChul Jung.
								 * 	move filler1 to selfpayment_type
								 */
								uPosMsg.setSelfPayment_type(IConstant.UPOSMESSAGE_PAID_TYPE_22);
							
								LogUtility.getPumpMLogger().info("[Pump M] Send UPOSMessage(0012) to SaleM");

								Preamble posPreamble = PumpMUtil.createUPOSMessagePreamble(null, SyncManager.DISE_PUMP_MODULE, SyncManager.DISE_SALE_MODULE,	uPosMsg, "");
								sendMessage(posPreamble);

								uPosMsg = CreateUPOSMessage.createUPOSMessage_0001( IUPOSConstant.DEVICE_TYPE_3S, khproc_no, "1", "", "");

								LogUtility.getPumpMLogger().info("[Pump M] Send UPOSMessage(0001) to SaleM");

								posPreamble = PumpMUtil.createUPOSMessagePreamble(null, SyncManager.DISE_PUMP_MODULE, SyncManager.DISE_SALE_MODULE, uPosMsg,	"");

								sendMessage(posPreamble);
							}
						} else {
							if (DasNoSelfPumpingManager.getInstance().isCurrentFullPumping(nozzleNo)) {
								// 가득 주유인 경우 (주유금액은 0원)
								DasNoSelfPumpingManager.getInstance().changeDasNoODTNozzleInfoWithOption7(nozzleNo);
							} else {
								// 일반 주유인 경우 (주유금액은 0원)
								DasNoSelfPumpingManager.getInstance().changeDasNoODTNozzleInfoWithOption5(nozzleNo);
							}
							UPOSMessage uPosMsg = DasNoSelfPumpingManager.getInstance().getUPOSMessageAfterPumping(nozzleNo, s4WorkMsg, khproc_no);
							
							LogUtility.getPumpMLogger().info("[Pump M] Send UPOSMessage to Cat M. NozID="+ nozzleNo);	
							
							Preamble preamble = PumpMUtil.createUPOSMessagePreamble(uniqueKey, SyncManager.DISE_PUMP_ADAPTER, SyncManager.DISE_CMS_MODULE, uPosMsg, "");
							sendMessage(preamble);
						}

					} else {
						// ODT 로 부터의 결제이면서 주유금액이 0이 아닌 경우
						LogUtility.getPumpMLogger().info("[Pump M] Payment From ODT. NozID=" + nozzleNo);

						/*
						 * 다쓰노 현금/외상 결제 추가. edited by ykjang // 현금 결제는 주유완료
						 * 후에 보너스 누적과 현금영수증 처리를 한다.
						 */
						boolean isCash = DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzleNo).isCash();
						boolean isCustCard = DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzleNo).isCustCard();
						boolean isCashCard = DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzleNo).isCashCard();
						boolean isBonusCard = DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzleNo).isBonusCard();
						boolean isCreditCard = DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzleNo).isCreditCard();

						if (isCustCard && !isCreditCard && !isCash) {
							// 외상 거래처
							// 외상은 보너스거래를 결제시점에 한다. BY GSC IT 기획팀
							// 모든 보너스 적립은 거래시점에 적립한다. 제휴사업팀 양승호과장 2010.11.17
							UPOSMessage uPosMsg = DasNoSelfPumpingManager.getInstance().getUPOSMessageAfterPumpingOiSang(nozzleNo, s4WorkMsg, khproc_no);

							if (IUPOSConstant.MESSAGETYPE_0083.equals(uPosMsg.getMessageType())) {
								// 보너스 카드가 들어오면 보너스 적립해야됨.
								LogUtility.getPumpMLogger().info("[Pump M] 외상 보너스 적립 요청 . K/H처리번호=" + uPosMsg.getPosReceipt_no());

								Preamble preamble = PumpMUtil.createUPOSMessagePreamble(	uniqueKey,	SyncManager.DISE_PUMP_ADAPTER, SyncManager.DISE_CAT_MODULE, uPosMsg,"");
								sendMessage(preamble);
							} else {
								LogUtility.getPumpMLogger().info("[Pump M] Send UPOSMessage to SalesM. NozID="	+ nozzleNo);
								Preamble preamble = PumpMUtil.createUPOSMessagePreamble(	uniqueKey,	SyncManager.DISE_PUMP_MODULE,SyncManager.DISE_SALE_MODULE, uPosMsg,"");
								sendMessage(preamble);

								PumpMTransactionManager.getInstance().setPayed(uPosMsg.getMessageType(),	uPosMsg.getNozzle_no());
								// 영수증 출력
								QlWorkingMsg_DasNOFromODT(uniqueKey, 
										nozzleNo,
										khproc_no, 
										pumpingPrice, 
										pumpingLiter,
										pumpingPrice, 
										pumpingBasePrice, 
										uPosMsg);
							}
						} else if (isCreditCard && !isCash) {
							// 신용카드
							if (DasNoSelfPumpingManager.getInstance().isCurrentFullPumping(nozzleNo)) {
								// 가득인 경우
								DasNoODTNozzleInfo selfOdtFullPumpingMgr = DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzleNo);
								HE_WorkingMessage heWorkingMsg = selfOdtFullPumpingMgr.getHeWorkingMsg();

								if (heWorkingMsg.getPrice().equals(s4WorkMsg.getPrice())
										&& DasNoSelfPumpingManager.getInstance().getFullPumpingOption() != IConstant.FULL_PUMPING_OPTION_0) {
									WorkingMessage qlMsg = null;
									UPOSMessage_ItemInfo_Item itemInfoItem = preUposMsg.getItem_info().getItemInfoList().get(0);
									prePayedPrice = itemInfoItem.getOilPrice_after_discount();

									if (PumpMUtil.getConnectedODTProtocolFromNozzleNo(nozzleNo) == 90)
										qlMsg = ODTUtility_GSC_Self.getQL_WorkingMessage_GSCSELFFromODT(nozzleNo, 
												khproc_no,
												pumpingPrice,
												pumpingLiter,
												prePayedPrice,
												pumpingBasePrice,
												preUposMsg);
									else
										qlMsg = ODTUtility_DaSNo.getQL_WorkingMessage_DaSNoFromODT(nozzleNo, 
												khproc_no,
												pumpingPrice,
												pumpingLiter,
												prePayedPrice,
												pumpingBasePrice,
												preUposMsg);

									LogUtility.getPumpMLogger().info("[Pump M] Send Receipt to Pump A. NozID="+ nozzleNo);
									Preamble preamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey, SyncManager.DISE_PUMP_ADAPTER, qlMsg, "");
									sendMessage(preamble);

									// 세차권 발행 내역을 Pos A로 전송한다.
									processOPTBarcode(qlMsg, khproc_no);
								} else {
									// ODT 로 부터의 결제이면서 가득 주유인 경우
									LogUtility.getPumpMLogger().info("[Pump M] Full Pumping. NozID="	+ nozzleNo);
									UPOSMessage uPosMsg = DasNoSelfPumpingManager.getInstance().getUPOSMessageAfterPumping(	nozzleNo, s4WorkMsg, khproc_no);
									LogUtility.getPumpMLogger().info("[Pump M] Send UPOSMessage to Cat M. NozID=" + nozzleNo);
									Preamble preamble = PumpMUtil.createUPOSMessagePreamble(	uniqueKey, SyncManager.DISE_PUMP_ADAPTER, SyncManager.DISE_CMS_MODULE, uPosMsg, "");
									sendMessage(preamble);
								}
							} else {
								// 가득주유가 아닌 경우
								UPOSMessage_ItemInfo_Item itemInfoItem = preUposMsg.getItem_info().getItemInfoList().get(0);
								prePayedPrice = itemInfoItem.getOilPrice_after_discount();
								
								LogUtility.getPumpMLogger().info("[Pump M] prePayedPrice="	+ prePayedPrice);

								if (PumpMUtil.shouldSendingRejectAndReApproval(prePayedPrice, pumpingPrice, isCreditCard) /*||
								mMessageType == IUPOSConstant.MESSAGETYPE_INT_0062*/) { // tatsuno_hs okdhp7 (2012.12)
									
									LogUtility.getPumpMLogger().info("[Pump M] 재승인 시작");
									
									// 주유완료 금액과 결제 금액을 비교하여서, 그 차이가 재승인최소인정 금액
									// 보다 클 경우
									// 선취소, 재승인을 위한 흐름을 타도록 한다.
									// DasNoSelfPumpingManager 가 관리하고 있는 현 주유건의
									// 데이터를 가득 주유 Option-2 처럼 변경하도록 한다.
									DasNoSelfPumpingManager.getInstance().changeDasNoODTNozzleInfoLikeFullPumpingWithOption6(nozzleNo);
									UPOSMessage uPosMsg = DasNoSelfPumpingManager.getInstance().getUPOSMessageAfterPumping(	nozzleNo, s4WorkMsg, khproc_no);
									LogUtility.getPumpMLogger().info("[Pump M] Send UPOSMessage to Cat M. NozID=" + nozzleNo);
									Preamble preamble = PumpMUtil.createUPOSMessagePreamble(uniqueKey,
											SyncManager.DISE_PUMP_ADAPTER,
											SyncManager.DISE_CMS_MODULE,
											uPosMsg, "");
									sendMessage(preamble);

								} else {
									// QL (영수증 전문) 을 생성해서 Pump A 로 전송한다.

									LogUtility.getPumpMLogger().info("영수증 출력");
									
									WorkingMessage qlMsg = null;

									if (PumpMUtil.getConnectedODTProtocolFromNozzleNo(nozzleNo) == 90){
										qlMsg = ODTUtility_GSC_Self.getQL_WorkingMessage_GSCSELFFromODT(nozzleNo, 
												khproc_no,
												pumpingPrice,
												pumpingLiter,
												prePayedPrice,
												pumpingBasePrice,
												preUposMsg);
									} else {
										qlMsg = ODTUtility_DaSNo.getQL_WorkingMessage_DaSNoFromODT(nozzleNo, 
												khproc_no,
												pumpingPrice,
												pumpingLiter,
												prePayedPrice,
												pumpingBasePrice,
												preUposMsg);
									}
									
									LogUtility.getPumpMLogger().info("[Pump M] Send Receipt to Pump A. NozID="+ nozzleNo);
									
									Preamble preamble = PumpMUtil.createWorkingMessagePreamble(	uniqueKey, SyncManager.DISE_PUMP_ADAPTER,	qlMsg, "");
									sendMessage(preamble);

									// 세차권 발행 내역을 Pos A로 전송한다.
									processOPTBarcode(qlMsg, khproc_no);
								}
							}
						} else if (isCash) {
							UPOSMessage uPosMsg = DasNoSelfPumpingManager.getInstance().getUPOSMessageAfterPumpingOiSang(nozzleNo, s4WorkMsg, khproc_no);

							if (!isBonusCard && !isCashCard) {
								// 현금영수증, 보너스 적립이 없는 경우에는 결과전문을 pos에 전송하고 QL전문을
								// 생성한다.
								Preamble preamble = PumpMUtil.createUPOSMessagePreamble(uniqueKey,
										SyncManager.DISE_PUMP_MODULE,
										SyncManager.DISE_SALE_MODULE, 
										uPosMsg,"");
								LogUtility.getPumpMLogger().info(	"[Pump M] Send UPOSMessage to SalesM. NozID=" + nozzleNo);
								sendMessage(preamble);

								PumpMTransactionManager.getInstance().setPayed(uPosMsg.getMessageType(),	uPosMsg.getNozzle_no());
								prePayedPrice = DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzleNo).getHeWorkingMsg().getCashCount();
								// 영수증 출력
								QlWorkingMsg_DasNOFromODT(uniqueKey, nozzleNo, khproc_no, pumpingPrice, pumpingLiter, prePayedPrice, pumpingBasePrice, uPosMsg);
							} else {
								Preamble preamble = PumpMUtil.createUPOSMessagePreamble(uniqueKey,
										SyncManager.DISE_PUMP_ADAPTER,
										SyncManager.DISE_CAT_MODULE, 
										uPosMsg,"");
								LogUtility.getPumpMLogger().info("[Pump M] Send UPOSMessage to CATM. NozID=" + nozzleNo);
								sendMessage(preamble);
							}
						}
					}
					break;
				}
				}
				break;
			}
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		}
	}


	/**
	 * Pump M 초기화 이후 초기화시 진행해야 할 사항을 진행한다.
	 * 
	 */
	private void initData() {
		if (logSSDC == true)
			LogUtility.getCATLogger().info(
					"SSDC/" + "PUMP_M/" + "PupmAController/" + "initData()");

		gStep = IPumpConstant.STEP_PUMP_RESOLVED;

		LockingManager.init();
		PumpMODTSaleManager.init();
		GSCSelfPumpingManager.init(); // 신규 ODT PumpingManager 추가, 2015.11.18- cwi
		PumpMPriceManager.init();
		DasNoSelfPumpingManager.init();
		PumpMSyncManager.init();
		PumpMTransactionManager.init();

		try {
			SendFullPumpingOtion sndFullPumpingoptin = new SendFullPumpingOtion();
			sndFullPumpingoptin.addListener(this);
			KHTimer.getInstance().setSchedule(sndFullPumpingoptin, 30000,	10000);
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		} 
	}


	public boolean isAlreadyInitialize() {
		return isAlreadyInitialize;
	}

	/**
	 * 주유기로 부터 올라온 단가와 원래 그 주유기의 단가가 틀린 경우 단가를 재 전송한다.
	 * 
	 * @param nozzleNo :
	 *            노즐 번호
	 * @param nzDevBasePrice :
	 *            주유기 계기 단가
	 */
	private void notifyBasePriceIfDifferent(String nozzle_no,	String nzDevBasePrice) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/" + "notifyBasePriceIfDifferent()");

		try {
			String basePrice = PumpMUtil.getBasePriceFromNozzleNo(nozzle_no);
			
			if (Double.parseDouble(basePrice) != Double.parseDouble(nzDevBasePrice)) {
				sendBasePrice(nozzle_no);
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		}
	}

	/**
	 * Preset 으로 인해 단가가 변경될수 있기 때문에 주유완료 이후 주유기에 설정된 단가와 주유기 실제 단가를 조사하여, 변경된 경우
	 * 원래 단가를 재 전송한다.
	 * 
	 * @param khproc_no :
	 *            KH 처리번호
	 * @param nozzle_no :
	 *            주유완료 한 Nozzle 번호
	 */
	private void notifyBasePriceToPumpAfterPumpCompletion(String khproc_no, 	String nozzle_no) {
		
		if (logSSDC == true)
			LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/" + "notifyBasePriceToPumpAfterPumpCompletion()");

		try {
			T_KH_PUMP_TRData pumpTRData = T_KH_PUMP_TRHandler.getHandler().getT_KH_PUMP_TRData(khproc_no);
			
			if (pumpTRData.getOil_preset_ind().equals(ICode.OIL_PRESET_IND_1)) {
				double basePrice = GlobalUtility.getDoubleValue(pumpTRData.getBaseprice());
				double presetBasePrice = GlobalUtility.getDoubleValue(pumpTRData.getPreset_baseprice());

				if (basePrice != presetBasePrice) {
					sendBasePrice(nozzle_no);
				} else {
					// LogUtility.getPumpMLogger().info("[Pump M] 방금 주유한 단가가 계기단가와 일치합니다.") ;
				}
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		}
	}

	/**
	 * POS 로 부터 주유기 단가 변경을 받았을 경우 모든 주유기에 대해서 주유기 단가 변경 요청한다. 충전기일 경우 D0 전문을
	 * 전송하고, 일반 주유기일 경우 P3 전문, 그리고 셀프주유기/ODT 일 경우에는 P5 전문을 송신한다.
	 * 
	 */
	private void notifyChangedBasePriceToAllPumpAndODT() {
		if (logSSDC == true)
			LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/"+ "notifyChangedBasePriceToAllPumpAndODT()");

		if (gStep != IPumpConstant.STEP_PUMP_RUNNING)
			return;

		LogUtility.getPumpMLogger().info("[Pump M] Notify BasePrice to All Pump.");

		try {
			String storeCode = T_KH_STOREHandler.getHandler().getStoreCode();

			WorkingMessage d0workMsg = PumpMUtil.getD0WorkingMessage(storeCode,
					null);
			if (d0workMsg != null) {
				d0workMsg.setMessageID(GlobalUtility.getUniqueMessageID());
				Preamble d0Preamble = PumpMUtil.createWorkingMessagePreamble(SyncManager
						.getUniqueKey(), SyncManager.DISE_PUMP_ADAPTER,
						d0workMsg, "");

				// D0 전문을 충전기에 전송.
				LogUtility.getPumpMLogger().info("[Pump M] Send D0 to Recharge.");
				sendMessage(d0Preamble);
			} else {
				// LogUtility.getPumpMLogger().info("[Pump M] 충전기가 없어서, D0 전문을 주유기에
				// 보내지 않습니다.") ;
			}

			WorkingMessage p3workMsg = PumpMUtil.getP3WorkingMessage(storeCode,
					null);
			if (p3workMsg != null) {
				p3workMsg.setMessageID(GlobalUtility.getUniqueMessageID());
				Preamble p3Preamble = PumpMUtil.createWorkingMessagePreamble(SyncManager
						.getUniqueKey(), SyncManager.DISE_PUMP_ADAPTER,
						p3workMsg, "");

				// P3 전문을 주유기에 전송
				LogUtility.getPumpMLogger().info("[Pump M] Send P3 to Pump.");
				sendMessage(p3Preamble);
			} else {
				// LogUtility.getPumpMLogger().info("[Pump M] 주유기가 없어서, P3 전문을 보내지
				// 않습니다.") ;
			}

			WorkingMessage p5workMsg = PumpMUtil
					.getP5WorkingMessageForSendingOnly(storeCode, null);

			if (p5workMsg != null) {
				p5workMsg.setMessageID(GlobalUtility.getUniqueMessageID());
				Preamble p5Preamble = PumpMUtil.createWorkingMessagePreamble(SyncManager
						.getUniqueKey(), SyncManager.DISE_PUMP_ADAPTER,
						p5workMsg, "");
				sendMessage(p5Preamble);

				// P5 전문을 셀프 ODT 에 전송
				LogUtility.getPumpMLogger().info("[Pump M] Send P5 to Self ODT");
			} else {
				// LogUtility.getPumpMLogger().info("[Pump M] 셀프 ODT가 없어서, P5 전문을
				// 주유기에 보내지 않습니다.") ;
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		}
	}

	/**
	 * Pump A 로부터 전문을 전송 받아서 그에 대한 처리를 진행한다. 받을수 있는 전문과 그에 따른 처리는 다음과 같다. -
	 * PumpA Protocol S3 주유/충전 중 자료 전송 (주유기,셀프,충전기) S4 주유완료 자료 전송 (주유기, 셀프) :
	 * Issue -> 충전기 주유완료 정보는 ? HF Preset 자료 전송 : Issue -> 주유기 Adapter 로 부터 올 필요
	 * 없음. ( 이 정보는 이미 Preset 정보를 관리하는 테이블로 부터 정보를 받아서 보냄) HB 소모셀프 외상거래 승인 요청 HA
	 * 소모셀프 카드결제 승인 요청 HC 소모셀프 주유 완료시 결제 정보 전송 다쓰노 셀프 주유 완료시 결제 정보 전송 HE 다쓰노 셀프
	 * 카드 결제 승인 요청 S9 고객 카드 승인 요청 SB 충전기 카드 결제 승인 요청 SH 충전기 ODT 최종 판매 데이터 전송 BA
	 * 보너스 점수 누적 요청 TJ 현금 영수증 요청 SF 보관증 번호 부여 요청 SG 보관량 조회 요청 SJ 주유시작 자료 전송 S5
	 * 토털게이지 전송 SE 주유 디바이스 이상 정보 전송 receiving_pumpa Data 를 그대로 StateMController
	 * 로 보낸다. S8 주유기/충전기 상태 전송 receiving_pumpa Data 를 그대로 StateMController 로
	 * 보낸다. CA 다쓰노 셀프 고객 확인 요청
	 * 
	 * 
	 * ACK - 일반 환경정보 설정 응답 (P2) - 주유기 환경 정보 설정 응답 (P3) - ODT 환경 정보 설정 (셀프 , 충전기)
	 * (P5) - 영업일 및 시간 설정 (P6) - 충전기 ODT POS 환경정보 설정 (PT)
	 *  - 노즐 제어 명렁 응답 (주유기,셀프,충전기) (PA) - 정액/정량 설정 응답 (주유기,셀프,충전기) (PB) - 고객 확인
	 * 응답 ( 다쓰노 셀프) (CB)
	 * 
	 * To POS Adapter S3 주유/충전 중 자료 전송 (주유기,셀프,충전기) SG 보관량 조회 요청 SJ 주유시작 자료 전송
	 * 
	 * To CAT Module HB 소모셀프 외상거래 승인 요청 HA 소모셀프 카드결제 승인 요청 HE 다쓰노 셀프 카드 결제 승인 요청
	 * S9 고객 카드 승인 요청 SB 충전기 카드 결제 승인 요청 BA 보너스 점수 누적 요청 TJ 현금 영수증 요청
	 * 
	 * To State Module SE 주유 디바이스 이상 정보 전송 receiving_pumpa Data 를 그대로
	 * StateMController 로 보낸다. S8 주유기/충전기 상태 전송 receiving_pumpa Data 를 그대로
	 * StateMController 로 보낸다.
	 * 
	 * To Sales Module S4 주유완료 자료 전송 (주유기, 셀프) HC 소모셀프 주유 완료시 결제 정보 전송 다쓰노 셀프 주유
	 * 완료시 결제 정보 전송 SH 충전기 ODT 최종 판매 데이터 전송
	 * 
	 * in Pump Module SF 보관증 번호 부여 요청 S5 토털게이지 전송
	 * 
	 * @param receiving_pumpa :
	 *            전송받은 전문
	 * 
	 */
	@Override
	protected void onReceivingPumpAData(Object receiving_pumpa) {
		if (logSSDC == true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/" + "onReceivingPumpA()");

		try {
			if (gStep != IPumpConstant.STEP_PUMP_RUNNING) {
				LogUtility.getPumpMLogger().info("[Pump M] Not initialization.");
				return;
			}
			Preamble receiveData = (Preamble) receiving_pumpa;
			WorkingMessage receiveWorkingMessage = (WorkingMessage) receiveData.getPreamble();
			String commandID = receiveWorkingMessage.getCommand();
			
			// Pump A 로 부터 전송되는 Preamble Object 의 uniqueKey 는 WorkingMessage 의
			// messageID 와 동일하다.
			// 이는 Pump A 로 부터 요청 전문에 대한 응답인 WorkingMessage 의 messageID 를 동일하게 내려
			// 주기 위해서이다.
			// 즉 다른 모듈과 통신할때 messageID 를 유지하기 위해서 Preamble 의 uniqueKey 를 사용하기
			// 때문이다.
			String uniqueKey = receiveData.getKey();
			String messageID = receiveWorkingMessage.getMessageID();

			// PumpLogUtil.printContent(uniqueKey,
			// receiveData.getFrom(),receiveData.getDest(),receiveData.getPreamble())
			// ;

			switch (commandID) {
			case IPumpConstant.COMMANDID_ACK: {
				break;
			}
				/**
				 * 주유 시작 자료를 받으면 T_KH_PUMP_TR 에 주유시작 정보를 Update 한다.
				 */
			case IPumpConstant.COMMANDID_SJ: {
				// 공통 - 주유 시작 자료 전송
				SJ_WorkingMessage sjWorkMsg = (SJ_WorkingMessage) receiveWorkingMessage;
				LogUtility.getPumpMLogger().info("[Pump M] Pumping Start Receive. NozID=" + sjWorkMsg.getNozzleNo());
				sjWorkMsg.print();

				int nozzle_noInt = Integer.parseInt(sjWorkMsg.getNozzleNo());
				PumpMPriceManager.initPumpPrice(nozzle_noInt); // 금액/리터 보정 데이터를  초기화 한다
				String khproc_no = PumpMUtil.createPumpStartContent(sjWorkMsg); // 주유 시작 전문을  T_KH_PUMP_TR 테이블에 저장한다.

				/*
				 * 매장 Beacon 사용 여부를 확인하여 
				 * 주유시 시작 전문을 Ark에 전달한다.
				 * 2017.06.30 lj
				 */
				// 매장 Beacon 사용 여부 확인
				if(IBeaconConstant.isBeaconStore){
					//Beacon 사용 매장용 주유시작 작업을 수행한다.
					processBeaconPumpingStart(uniqueKey, khproc_no);
				}
				
				break;
			}
				/**
				 * 주유 중 자료를 받으면, 금액 보정을 한 이후 POS 로 전송한다. 그리고 kixxhub.properties
				 * 파일내의 pump.pumping.interval 에 설정된 값에 따라서 POS 에 전송한다. 이는 POS 의
				 * 부하를 막으며 (UI 갱신), POS KixxHub 통신상의 부하 역시 줄이기 위함이다. ex>
				 * pump.pumping.interval=3 으로 설정되어 있으며 3번의 주유중 데이터 중 하나만 전송한다.
				 */
			case IPumpConstant.COMMANDID_S3: {
				// 공통 - 주유/충전 중 자료 전송
				S3_WorkingMessage s3WorkMsg = (S3_WorkingMessage) receiveWorkingMessage;

				String khproc_no = PumpMTransactionManager.getInstance().getPumpingKHTransactionID(s3WorkMsg.getNozzleNo(), IPumpConstant.KH_PUMPING);

				if (PumpMObjectValidation.validatePumpAObject(s3WorkMsg)) {
					int nozzle_noInt = Integer.parseInt(s3WorkMsg.getNozzleNo());

					String price = PumpMPriceManager.getRightPumpPrice(nozzle_noInt, s3WorkMsg.getPrice());
					String liter = PumpMPriceManager.getRightPumpLiter(
							nozzle_noInt, PumpMUtil.convertNumberFormatFromPumpToPOS(s3WorkMsg.getLiter(), 3, 8));

					boolean rlt = PumpMPriceManager.isSendPumpingContentToPOS(	commandID, nozzle_noInt);

					if (rlt) {
						POS_DE dePumpMMsg = new POS_DE(messageID, s3WorkMsg, khproc_no, price, liter,
								T_KH_STOREHandler.getHandler().getWorkingDate());
						Preamble dePreamble = PumpMUtil.createPOSMessagePreamble(null, SyncManager.DISE_POS_ADAPTER, dePumpMMsg, "");

						// [2008.05.07]
						// 1. [개선사항] POS 와 KixxHub 통신시 전문이 많을 경우 Delay 가 생긴다. 특히
						// 많은 전문이 주유중 정보인데, 이를 방지하기 위해서
						// POS A 가 POS 로 주유중 전문을 송신할때, Preamble 의 ACK 여부를 NO 로
						// 하면 바로 POS 로 전문을 전송한다.
						dePreamble.setIsAckRequired(Preamble.IS_ACK_REQUIRED_NO);
						sendMessage(dePreamble);
						
					} else {
						// LogUtility.getPumpMLogger().debug("[Pump M]
						// 주유중자료전송안함.Interval때문") ;
					}
				} else {
					LogUtility.getPumpMLogger().warn("[Pump M] Can't trust S3WorkingMessage");
				}
				
				/*
				 * 매장 Beacon 사용 여부를 확인하여 
				 * 주유기로 부터 주유시작 전문을 수신하지 못한 경우
				 * 시작 전문을 생성해서 Ark에 전달한다.
				 * 
				 * 2017.06.30 lj
				 */
				//매장 Beacon 사용 여부 확인
				if(IBeaconConstant.isBeaconStore){
					processBeaconS3(uniqueKey, khproc_no);
				}
				
				break;
			}
				/**
				 * 주유완료 자료를 받으면 T_KH_PUMP_TR 테이블에 내용을 Update 한다. 그리고 주유완료의 금액이 0
				 * 인 경우는, 상황에 따라서 POS 로 전송하지 않는다. 구체적 내용은 processS4 함수 참조.
				 */
			case IPumpConstant.COMMANDID_S4: {
				// 공통 - 주유 완료 자료 전송
				boolean process = true;
				String khproc_no ="";
				S4_WorkingMessage s4WorkMsg = (S4_WorkingMessage) receiveWorkingMessage;

				LogUtility.getPumpMLogger().info("[Pump M] Pumping Completed. NozID="	+ s4WorkMsg.getNozzleNo());

				s4WorkMsg.print();

				if (PumpMObjectValidation.validatePumpAObject(s4WorkMsg)) {

					int nozzle_noInt = Integer.parseInt(s4WorkMsg.getNozzleNo());
					int status_Flag = Integer.parseInt(s4WorkMsg.getStatusFlag());

					if (PumpMTransactionManager.getInstance().isSameState(s4WorkMsg.getNozzleNo(),	IPumpConstant.KH_PUMP_COMPLETED)) {
						
						String totalGauage = PumpMUtil.convertNumberFormatFromPumpToPOS(s4WorkMsg.getTotalGauge(), 3, 11);
						
						if (Double.parseDouble(totalGauage) == Double.parseDouble(PumpMPriceManager.getTotalGauage(nozzle_noInt))) {
							process = false;
							// 그 전 상태도 주유완료이며 Total Gauage 도 동일할 경우 버린다.
							LogUtility.getPumpMLogger().info("[Pump M] Drop S4. NozID=" + s4WorkMsg.getNozzleNo());
						} else {
							process = true;
							// 그 전 상태도 주유완료이지만, Total Gauage 가 다를 경우 가상의 주유시작
							// 자료를 생성한다. 이는 주유건으로 인정한다.
							LogUtility.getPumpMLogger().info( "[Pump M] Create Fake PumpingStart Content. NozID=" + s4WorkMsg.getNozzleNo());
							PumpMUtil.createFakePumpStartContent(s4WorkMsg.getNozzleNo());
						}
					}

					// 충전기ODT 주유완료 자료의 status_Flag 가 9 일 때는 충전기ODT 비정상 종료로 인하여
					// 정상적인 판매완료 자료를
					// 받지 못한 경우이다. 이 때는 S4 전문이 아니라 KJ 전문을 처리하도록 한다.
					LogUtility.getPumpMLogger().info("[Pump M] ODT status_Flag =" + status_Flag);

					if (status_Flag == 9) {
						LogUtility.getPumpMLogger().info("[Pump M] process KJ START");
						khproc_no = PumpMTransactionManager.getInstance().getKHTransactionID(s4WorkMsg.getNozzleNo(), IPumpConstant.KH_PUMP_COMPLETED);
						s4WorkMsg.setBasePrice(PumpMUtil.getBasePriceFromKHProcNo(s4WorkMsg.getNozzleNo(), khproc_no));
						processKJ(uniqueKey, messageID, s4WorkMsg, khproc_no);

						process = false;
					}

					if (process) {
						khproc_no = PumpMTransactionManager.getInstance().getKHTransactionID(s4WorkMsg.getNozzleNo(), IPumpConstant.KH_PUMP_COMPLETED);
						processS4(uniqueKey, messageID, s4WorkMsg, khproc_no);
						// 주유 완료 이후, 만약 노즐이 ODT 와 연결되어 있다면 추가적인 작업을 진행하도록 한다.
						handleWorkingMessageForAdditionalWork(uniqueKey, s4WorkMsg.getNozzleNo(), commandID, s4WorkMsg, khproc_no);
						
					}
					PumpMPriceManager.initPumpPrice(nozzle_noInt);
				}

				// 변경 날짜 : 2008.10.02
				// 미팅 참석자 : 편윤국, 정동명, 정우철
				// 계기 단가와 노즐의 단가가 다를경우 단가를 주유기에 재 전송한다.
				// 참고로 processS4 전문내에 주유기 단가를 재 전송하는 로직이 있다. 이 로직은 Preset 단가와
				// 주유기의 단가가
				// 틀릴 경우 보내도록 되어있다.
				notifyBasePriceIfDifferent(s4WorkMsg.getNozzleNo(),	PumpMUtil.convertBasePriceFromPumpToPOS(s4WorkMsg.getBasePrice()));

				/*
				 * Beacon 사용 매장이면서,
				 * Beacon 주문인 경우 주유완료 정보를 ARK에 전송 한다.
				 * 2017.05.17 lj
				 */
				//매장 Beacon 사용 여부 확인
				if(IBeaconConstant.isBeaconStore){
					sendS4BeaconMessage(uniqueKey, khproc_no, s4WorkMsg);
				}
				
				break;
			}
				/**
				 * Preset 자료를 받아서 T_KH_PUMP_TR 테이블에 정보를 Update 한다. 그리고 이 정보를 POS
				 * 로 전송한다. Pump A로 부터 받은 Preset 자료는 Pump A 에서 잘못 생성해서 주유데이터를
				 * Preset 데이터로 보내기도 한다. 이를 방지하기 위해서 9만원 이상 주유중인데 Preset 자료를 받은
				 * 경우 버리도록 한다.
				 */
			case IPumpConstant.COMMANDID_HF: {
				// 공통 - Preset 자료 전송
				HF_WorkingMessage hfWorkMsg = (HF_WorkingMessage) receiveWorkingMessage;
				LogUtility.getPumpMLogger().info("[Pump M] Receive Preset. NozID="	+ hfWorkMsg.getNozzleNo());
				hfWorkMsg.print();

				if (PumpMObjectValidation.validatePumpAObject(hfWorkMsg)) {
					if (PumpMTransactionManager.getInstance().isSameState( hfWorkMsg.getNozzleNo(), IPumpConstant.KH_PUMP_PRESET)) {
						LogUtility.getPumpMLogger().info("[Pump M] Same Preset Info. Drop.");
					} else {
						processHF(uniqueKey, messageID, hfWorkMsg);
						LogUtility.getPumpMLogger().info("[Pump M] Respond Preset to POS");
					}
				}
				break;
			}
				/**
				 * 상태 정보 수신시 그 상태 정보값이 변경되었을 경우 POS 로 전송하도록 한다. Pump M 에서는 항상 현재
				 * 상태 정보를 저장하고 있다.
				 */
			case IPumpConstant.COMMANDID_SE:
				// 공통 - 주유 디바이스 이상 정보 전송
			case IPumpConstant.COMMANDID_S8: {
				// 공통 - 주유기/충전기 상태 전송
				S8_WorkingMessage s8Wm = (S8_WorkingMessage) receiveData.getPreamble();
				
				s8Wm.print();
				
				/*
				 * 2017.06.30 lj
				 * 비콘 매장이면서 셀프 주유기인 경우
				 * ODT 상태 정보를 저장한다.
				  	654 : 주유완료
					699 : 시작화면 이동
					235 : 주유대기
				 * 
				 */
				if(IBeaconConstant.isBeaconStore){
					if(s8Wm.getDeviceType().equals(ICode.SELF_IND_EXIST_02_SELF_PUMP)) {
						LogUtility.getLogger().info("beacon self status 1: "+s8Wm.getStatusCode());
						if("654".equals(s8Wm.getStatusCode()) || "699".equals(s8Wm.getStatusCode()) || "235".equals(s8Wm.getStatusCode())){
							LogUtility.getLogger().info("beacon self status 2: "+s8Wm.getNozzleNo()+s8Wm.getStatusCode()+", time :" + s8Wm.getDetectTime());
							IBeaconConstant.beaconODTstate.put(s8Wm.getNozzleNo()+s8Wm.getStatusCode(), s8Wm);
						}
					}
				}
				
				
				if(s8Wm.getDeviceType().equals(ICode.SELF_IND_EXIST_02_SELF_PUMP) 
						&& Integer.parseInt(s8Wm.getStatusCode()) == IConstant.STATE_PUMP_STATECODE_699){
					
					LogUtility.getLogger().info("[Pump M] " + s8Wm.getNozzleNo()+ "번 노즐 KH처리번호 초기화");
					
					PumpMTransactionManager.getInstance().getKHTransactionID(s8Wm.getNozzleNo(),IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED);
					PumpMODTSaleManager.initSaleContent(s8Wm.getNozzleNo());
					
					break;
				} else {
					receiveData.setDest(SyncManager.DISE_STATE_MODULE);
					sendMessage(receiveData);
					
					break;
				}
			}
				/**
				 * 소모셀프 외상거래 조회 요청 수신시 POS 로 조회 요청을 하고, 그 응답 전문을 받아서 Pump A 로
				 * 전송한다. 조회 요청의 Process 는 충전기의 외상거래 조회 요청과 동일하다. 단지 상품 정보가 포함되는
				 * 것이 다르다.
				 */
			case IPumpConstant.COMMANDID_HB: {
				// 소모 셀프- 소모 셀프 외상 거래 승인 요청
				HB_WorkingMessage hbWorkMsg = (HB_WorkingMessage) receiveWorkingMessage;
				String cardNo = hbWorkMsg.getCardNumber();

				// 시작 결제 정보를 보고 정보 리셋
				if (hbWorkMsg.getCommandIndex().equals("9")	|| hbWorkMsg.getCommandIndex().equals("0")) {
					
					PumpMTransactionManager.getInstance().setNozzleState( hbWorkMsg.getConnectNozzleNo(), IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED);
					PumpMODTSaleManager.initSaleContent(hbWorkMsg.getConnectNozzleNo());
				}

				// By 박종호, 소모 셀프 고객카드 리딩시 16자리 이후 =< 제거
				if (cardNo != null) {
					if (cardNo.length() > 15) {
						hbWorkMsg.setCardNumber(PumpMUtil.getSubString(cardNo, 0, 16));
					}
				}

				LogUtility.getPumpMLogger().info( "[Pump M] Receive HB (CustProcessing) Request from SoMo. ODTID=" + hbWorkMsg.getNozzleNo());
				hbWorkMsg.print();

				String khproc_no = PumpMTransactionManager.getInstance().getKHTransactionID(hbWorkMsg.getConnectNozzleNo(),
						IPumpConstant.KH_ODT_PAID_REQ);

				sendLockingInfoToPOS(hbWorkMsg.getConnectNozzleNo(), khproc_no, IConstant.PUMP_SALE_LOCKING);

				processHB(uniqueKey, receiving_pumpa, messageID, hbWorkMsg, khproc_no);

				break;
			}
				/**
				 * 소모셀프의 카드 결제 승인 요청 수신이후 CAT M 으로 승인 요청/응답을 받아서 소모셀프로 전송한다.
				 * 현금거래처의 할인 단가 적용인 경우는 소모셀프 ODT 에서 할인된 단가로 요청을 한다. 이는 충전기와
				 * 다른점이다. 충전기의 경우 주유단가로써 요청을 하며, 현금거래처의 판매단가 및 금액 변경은 Pump M 에서
				 * 한다.
				 */
				// By 박종호
				// 운수사 버스 - 공제 카드 처리를 위해서 위의 로직을 아래와 같이 변경함.
			case IPumpConstant.COMMANDID_HA: {
				// 소모 셀프- 소모 셀프 신용 카드 결제 승인 요청
				HA_WorkingMessage haWorkMsg = (HA_WorkingMessage) receiveWorkingMessage;

				LogUtility.getPumpMLogger().info("[Pump M] Receive HA from SoMo. ODTID="+ haWorkMsg.getNozzleNo());

				// 시작 결제 정보를 보고 정보 리셋 - 동화 프라임 업그레이드 문제로 일시 보류
				if (haWorkMsg.getCommandIndex().equals("9") || haWorkMsg.getCommandIndex().equals("0")) {
					PumpMTransactionManager.getInstance().setNozzleState(haWorkMsg.getConnectNozzleNo(),
							IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED);
					PumpMODTSaleManager.initSaleContent(haWorkMsg.getConnectNozzleNo());
				}

				SqlSession session = null;

				try {
					session = SqlSessionFactoryManager.openSqlSession();

					String binChekCardNo = "";

					if (haWorkMsg.getCardNumber() != null && haWorkMsg.getCardNumber().length() > 5)
						binChekCardNo = haWorkMsg.getCardNumber().substring(0,6);
					
					String track2Check ="";
/////////////// 2012.10.08 ksm 조달청 셀프 현장할인 막기위해
					if(!GlobalUtility.isNullOrEmptyString(haWorkMsg.getCardNumber()) && haWorkMsg.getCardNumber().length() > 36 ){
						track2Check = haWorkMsg.getCardNumber().substring(32, 36);
					}
					
					//if("2080".equals(track2Check)){
//20171204 ygh 로직제거	(소매지원팀 김지영차장님 요청 신자영은 수정)							
//					if("6950".equals(track2Check)){
//						LogUtility.getPumpMLogger().info("[Pump M] 승인거절-조달청 제휴카드! Track2=" + track2Check);
//						HC_WorkingMessage hcWorkMsg = new HC_WorkingMessage();
//
//						hcWorkMsg.setNozzleNo(haWorkMsg.getNozzleNo());
//						hcWorkMsg.setConnectNozzleNo(haWorkMsg	.getConnectNozzleNo());
//						hcWorkMsg.setMode("2");
//						hcWorkMsg.setTrType("1");
//
//						hcWorkMsg.setVanMsg("현장할인 카드입니다. 직원에게 문의하세요.");
//
//						Preamble preamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_PUMP_ADAPTER, hcWorkMsg, "");
//						LogUtility.getPumpMLogger().info("[Pump M] Send WorkMsg to Pump A");
//						sendMessage(preamble);
//						// 취소된 결제 임으로 관련 정보 리셋
//						PumpMTransactionManager.getInstance().setNozzleState(haWorkMsg.getConnectNozzleNo(),
//								IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED);
//						PumpMODTSaleManager.initSaleContent(haWorkMsg.getConnectNozzleNo());
//						
//						break;
//					}
//////////////////////////////////////////////////////////////////////////////////////////////////////////
					
					LogUtility.getPumpMLogger().info("[Pump M] binChekCardNo =" + binChekCardNo);

					if (T_KH_BIN_INFOHandler.getHandler().isExist(session, ICode.BIN_INFO_03,	binChekCardNo)
							|| T_KH_BIN_INFOHandler.getHandler().isExist(session,	ICode.BIN_INFO_09, binChekCardNo)) {

						LogUtility.getPumpMLogger().debug("[Pump M] 운수사 BIN CHECK");

						if (PumpMODTSaleManager.getCustPOSPumpM(haWorkMsg.getConnectNozzleNo()) != null) {
							LogUtility.getPumpMLogger().debug("[Pump M] 거래처카드 정보가 있기 때문에 정상 처리함.");

							String khproc_no = PumpMTransactionManager
									.getInstance().getKHTransactionID(	haWorkMsg.getConnectNozzleNo(), IPumpConstant.KH_ODT_PAID_REQ);

							processHA(messageID, haWorkMsg, khproc_no);

						} else {
							LogUtility.getPumpMLogger().debug("[Pump M] 거래처카드 정보가 없음. 거래거절");
							HC_WorkingMessage hcWorkMsg = new HC_WorkingMessage();

							hcWorkMsg.setNozzleNo(haWorkMsg.getNozzleNo());
							hcWorkMsg.setConnectNozzleNo(haWorkMsg	.getConnectNozzleNo());
							hcWorkMsg.setMode("2");
							hcWorkMsg.setTrType("1");

							hcWorkMsg.setVanMsg("유가거래 보조카드로는 현장 결제를 하실 수 없습니다. 사무실로 방문하여 문의하여 주십시오.");

							Preamble preamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_PUMP_ADAPTER, hcWorkMsg, "");
							LogUtility.getPumpMLogger().info("[Pump M] Send WorkMsg to Pump A");
							
							sendMessage(preamble);
							// 취소된 결제 임으로 관련 정보 리셋
							PumpMTransactionManager.getInstance().setNozzleState(haWorkMsg.getConnectNozzleNo(),
									IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED);
							PumpMODTSaleManager.initSaleContent(haWorkMsg.getConnectNozzleNo());
						}
					} else {
						String khproc_no = PumpMTransactionManager.getInstance().getKHTransactionID(haWorkMsg.getConnectNozzleNo(),
								IPumpConstant.KH_ODT_PAID_REQ);
						processHA(messageID, haWorkMsg, khproc_no);
					}
					
				} catch (Exception e) {
					LogUtility.getPumpMLogger().error(e.getMessage(), e);
				} finally {
					SqlSessionFactoryManager.closeSqlSession(session);
				}
				break;
			}
				/**
				 * 소모셀프 판매 완료 수신이후 가령 외상 거래처 판매, 현금 판매 혹은 재승인, 선승인 취소 전문이 있는지
				 * 조사하여, 그 내용에 따라서 u-POS 를 재구성하여 POS 로 전송한다. 그리고 소모셀프의 경우 모든 응답
				 * 전문은 lastPayment_yn 은 0 으로 설정하며, 판매완료 수신 이후 0001 을 전송한다.
				 */
			case IPumpConstant.COMMANDID_ST: {
				// 소모셀프 - 소모셀프 판매 완료
				ST_WorkingMessage stWorkMsg = (ST_WorkingMessage) receiveWorkingMessage;
				String cardNo = stWorkMsg.getCardNo();

				// By 박종호, 소모 셀프 고객카드 리딩시 16자리 이후 =< 제거
				if (cardNo != null) {
					if (cardNo.length() > 15) {
						stWorkMsg.setCardNo(PumpMUtil.getSubString(cardNo, 0, 16));
					}
				}

				LogUtility.getPumpMLogger().info("[Pump M] Receive ST from SoMo. ODTID=" + stWorkMsg.getNozzleNo());
				stWorkMsg.print();

				if (PumpMObjectValidation.validatePumpAObject(stWorkMsg)) {
					String khproc_no = PumpMTransactionManager.getInstance().getKHTransactionID(stWorkMsg.getConnectNozzleNo(),
							IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED);
					processST(uniqueKey, messageID, commandID, stWorkMsg,	khproc_no);
				}
				break;
			}
				/**
				 * 다쓰노 셀프의 신용카드 결제 승인 요청 수신한다. 주유건마다 이는 한건만 온다라고 가정한다.
				 */
				// By 박종호
				// 운수사 버스 - 공제 카드 처리를 위해서 위의 로직을 아래와 같이 변경함.
			case IPumpConstant.COMMANDID_HE: {

				HE_WorkingMessage heWorkMsg = (HE_WorkingMessage) receiveWorkingMessage;

				LogUtility.getPumpMLogger().info("[Pump M] Receive HE from DaSNo. ODTID=" + heWorkMsg.getNozzleNo());

				heWorkMsg.print();

				// 다쓰노 셀프의 경우 HE 전문이 올라오는 경우는 새로운 노즐이라고 가정한다. 이로 인해 기존 상태를 판매완료
				// 상태로 변경한다.
				// 이를 통해서 결제 요청이 올때마다 새로운 KH 처리번호를 생성하게 된다.
				PumpMTransactionManager.getInstance().setNozzleState(heWorkMsg.getConnectNozzleNo(),
						IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED);
				SqlSession session = null;

				try {
					session = SqlSessionFactoryManager.openSqlSession();
					String binChekCardNo = "";
					String custCode = "";
					
					String track2Check = "";		// 2012.10.09 ksm

					if (!GlobalUtility.isNullOrEmptyString(heWorkMsg.getCardNumber())){
						binChekCardNo = heWorkMsg.getCardNumber().substring(0,6);
												
						// 2012.10.09 ksm  신용카드일 경우 Track2 데이터까지 길이가 37 자리임.
						// 2012.10.12 ksm  보너스 카드는 Track2 데이터가 없음. 신형다쓰노의 경우 신용카드 자리수 체크를 하지않음.
						if(heWorkMsg.getCardNumber().length() > 36){
							track2Check = heWorkMsg.getCardNumber().substring(32, 36);
						}
					}
					
/////////////2012.10.09 ksm 조달청 셀프 현장할인 막기위해 임시로직 추가
					//if("2080".equals(track2Check)){
//20171204 ygh 로직제거	(소매지원팀 김지영차장님 요청 신자영은 수정)							
//					if("6950".equals(track2Check)){
//						LogUtility.getPumpMLogger().info("[Pump M] 승인거절-조달청 제휴카드! Track2 : " + track2Check);
//						
//						QM_WoringMessage qmWorkMsg = new QM_WoringMessage();
//
//						String outputMessage = "현장할인 카드입니다. 직원에게 문의하세요.";
//						
//						qmWorkMsg.setNozzleNo(heWorkMsg.getNozzleNo());
//						qmWorkMsg.setMode("2");
//						qmWorkMsg.setMessage(outputMessage);
//
//						Preamble preamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_PUMP_ADAPTER, qmWorkMsg, "");
//						
//						LogUtility.getPumpMLogger().info("[Pump M] Send WorkMsg to Pump A");
//						
//						sendMessage(preamble);
//						
//						String receiptString = ODTUtility_DaSNo.createReceiptByFail(heWorkMsg.getNozzleNo(), outputMessage);
//						
//						String length = Integer.toString(receiptString.length()); 
//						
//						QL_WorkingMessage qlMsg = new QL_WorkingMessage(heWorkMsg.getNozzleNo(), heWorkMsg.getConnectNozzleNo(), length, receiptString, "1", "", "") ;
//
//						Preamble preamblePrint = PumpMUtil.createWorkingMessagePreamble(uniqueKey, SyncManager.DISE_PUMP_ADAPTER, qlMsg, "");
//						sendMessage(preamblePrint);
//
//						return;
//					}
///////////////////////////////////////////////////////////////////////////////////
					
					POSHeader posMsg = PumpMODTSaleManager.getCustPOSPumpM(heWorkMsg.getConnectNozzleNo());
					
					if (posMsg != null)
						custCode = ((POS_DW) posMsg).getCust_code();

					LogUtility.getLogger().info("[Pump M] binChekCardNo =" + binChekCardNo);

					if ((T_KH_BIN_INFOHandler.getHandler().isExist(session, "03", binChekCardNo) 
							|| T_KH_BIN_INFOHandler.getHandler().isExist(session, "09", binChekCardNo))
							&& GlobalUtility.isNullOrEmptyString(custCode)) {
						
						LogUtility.getLogger().debug("운수사 BIN CHECK");
						LogUtility.getLogger().info("다쓰노 셀프 - 운수사 BIN 카드는 무조건 거절함");
						
						QM_WoringMessage qmWorkMsg = new QM_WoringMessage();

						qmWorkMsg.setNozzleNo(heWorkMsg.getNozzleNo());
						qmWorkMsg.setMode("2");

						Preamble preamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_PUMP_ADAPTER, qmWorkMsg, "");
						
						LogUtility.getLogger().info("[Pump M] Send WorkMsg to Pump A");
						
						sendMessage(preamble);
					} else {
						// kixxhub처리번호를 재생성하기 전에 CA전문에 의한 POS로부터 받아온 DW전문을 재조회하여
						// 가지고 있다가
						// KIXXHUB처리번호가 새롭게 따지면 다시 넣어준다.

						WorkingMessage workMsg = PumpMODTSaleManager	.getCustPumpAPumpM(heWorkMsg.getConnectNozzleNo());

						POS_DW dwPumpM = null;

						if ((posMsg != null) && (posMsg instanceof POS_DW))
							dwPumpM = (POS_DW) posMsg;

						// KH처리번호 생성
						String khproc_no = PumpMTransactionManager.getInstance().getKHTransactionID(	heWorkMsg.getConnectNozzleNo(), IPumpConstant.KH_ODT_PAID_REQ);

						DasNoSelfPumpingManager.getInstance().createODTNozzleInfo( heWorkMsg.getConnectNozzleNo(), 
																					false,
																					IConstant.FULL_PUMPING_OPTION_0);
						/* 2012.10.09 ksm 사용하는 곳이 없으므로 삭제.
						DasNoODTNozzleInfo nozInfo 
													= DasNoSelfPumpingManager.getInstance().nozzleHash.get(heWorkMsg.getConnectNozzleNo());
						*/
						PumpMODTSaleManager.setCustInfo(heWorkMsg.getConnectNozzleNo(), workMsg, dwPumpM);

						processHE(uniqueKey, messageID, heWorkMsg, khproc_no);
					}

				} catch (Exception e) {
					LogUtility.getPumpMLogger().error(e.getMessage(), e);
				} finally {
					SqlSessionFactoryManager.closeSqlSession(session);
				}
				break;
			}
				/**
				 * 다쓰노 셀프 판매완료 수신한다. 수신 이후 0001 전문을 보낼것인지를 조사하고 처리한다.
				 */
			case IPumpConstant.COMMANDID_TR: {
				// 다쓰노 셀프- 다쓰노 셀프 판매 완료
				TR_WorkingMessage trWorkMsg = (TR_WorkingMessage) receiveWorkingMessage;

				LogUtility.getPumpMLogger().info("[Pump M] Receive TR from DaSNo. ODTID=" + trWorkMsg.getNozzleNo());
				trWorkMsg.print();

				String khproc_no = PumpMTransactionManager.getInstance().getKHTransactionID(trWorkMsg.getConnectNozzleNo(),
						IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED);
				processTR(uniqueKey, messageID, commandID, trWorkMsg, khproc_no);
				break;
			}
			
			/**
			 * PI2, 신규 ODT 추가에 따른 프로세스 추가, 2015.11.18 - cwi
			 */
			case IPumpConstant.COMMANDID_GA: {

				GA_WorkingMessage gaWorkMsg = (GA_WorkingMessage) receiveWorkingMessage;

				// PI2, CWI, 2015-12-31 GA 전문에 LedCode값이 있을 시 이전 데이터의 응답 요청으로
				// led code값이 있을 시 받은 전문을 SALE_MODULE로 전송 한다.
				// 최초 승인(4201) 요청 시 LedCode 값을 S로 전달 받도록 협의가 되었으며, LedCode 값이 null이 아니고 S가 아닐 경우 pos 응답 전문이 전달 되록 조건 추가 - 2016.02.12
				if (!"".equals(gaWorkMsg.getLedCode()) && !"S".equals(gaWorkMsg.getLedCode())){
					
					// 응답 전문 처리
					UPOSMessage uPosMsg = gaWorkMsg.getUnityMessage();
					
					if (uPosMsg != null) {
						//복합결제 - 사전예약 응답 전문 수신시 할인된단가로 pumpTR 정보 수정 2021.04.01 양일준
						if(IUPOSConstant.MESSAGETYPE_0242.equals(uPosMsg.getMessageType())
								&& IUPOSConstant.RESPOND_LEDCODE_1.equals(uPosMsg.getLed_code())
								&& uPosMsg.getFiller1() != null
								&& IUPOSConstant.MOBILE_PAYMENT_APPROVAL_TYPE_21.equals(uPosMsg.getFiller1().substring(0, 2))
								){		
							
							try {
								Double nbefore = Double.parseDouble(gaWorkMsg.getBasePrice());
								BigDecimal before = new BigDecimal(Integer.parseInt(String.valueOf(Math.round(nbefore/100))));
								before.setScale(0, RoundingMode.HALF_UP);
																
								T_KH_PUMP_TRHandler.getHandler().updatePresetBasePrice_BY_khproc_no(uPosMsg.getPosReceipt_no(), before.toString());
								
								LogUtility.getLogger().info("[결제모듈개선 프로젝트 ] 사전예약 승인시 단가 재조정 완료 : " + before.toString() + " / " + uPosMsg.getPosReceipt_no());
							} catch (Exception e) {
								LogUtility.getPumpMLogger().error(e.getMessage(), e);
							}							

							SqlSession session = null;
							
							try {
								session = SqlSessionFactoryManager.openSqlSession();
								
								byte[] inputData = UPOSMessageUtility.createUPOSByteArray(uPosMsg);
								
								POSHeader posHeader = POSHeader.createHeader(inputData) ;	
								
								String msgId = posHeader.getMessageID() ;
						
								LogUtility.getLogger().info("[결제모듈개선 프로젝트 ] T_KH_SALES_INFO 사전예약 저장 시작");
						    	if (!insertTRData(session, inputData, uPosMsg.getPosReceipt_no(), GlobalUtility.getUniqueMessageID(), "", IConstant.PROC_TYPE_MF, ICode.POS_SENDING_YES))
						    		LogUtility.getLogger().error("[결제모듈개선 프로젝트] T_KH_SALES_INFO INSERT error ");
						    	
							} catch (Exception e) {						
								LogUtility.getSCMLogger().error(e.getMessage(),e);
							} finally {
								SqlSessionFactoryManager.closeSqlSession(session);
							}
							LogUtility.getLogger().info("[결제모듈개선 프로젝트 ] T_KH_SALES_INFO 사전예약 저장 완료");
						}
						
				    	// Sale M 으로 응답 전문을 보낼지에 대한 판단을 한다.
			    		if (ODTUtility_Common.shouldSendToSaleM(uPosMsg)) {
			    			
			    			//2012.04.23 ksm 
					    	//승인응답전문의 LED_CODE=2(거절) 이고 Credit_AuthInfo(부가정보)값이 "CU"(단말기일련번호중복)인 경우 전문추적번호를 500 증가시	킴 
					    	if("2".equals(gaWorkMsg.getLedCode())){
					    		LogUtility.getLogger().info("[Check TrackingNo] 응답 전문의 LED_CODE 값 = 2" ) ;
					    		
					    		// 거절사유가 "CU" 단말기일련번호중복인 경우 전문추적번호 증가 시킴.
					    		if("CU".equals(uPosMsg.getCredit_authInfo())){
					    			LogUtility.getLogger().info("[Check TrackingNo] 단말기일련번호 중복 발생!!!" ) ;
					    			
					    			// 전문추적번호 증가
					    			PumpMTransactionManager.getInstance().increaseTrackingValue();
					    		}
					    	}
					    	    			
			    		   	LogUtility.getLogger().info("[Pump M] Send UPOSMessage to SaleM") ;
			    		   	
					    	Preamble posPreamble = PumpMUtil.createUPOSMessagePreamble( null, 
																			 SyncManager.DISE_PUMP_MODULE,
																			 SyncManager.DISE_SALE_MODULE, 
																			 uPosMsg, 
																			 "") ;
					    	sendMessage(posPreamble) ;
					    	
			    		}
			    	}
					
				}else{
					
					// 거래처 정보 조회 - PI2, 2016-01-12, cwi
					// 거래처 요청 전문은 기존 소모 셀프와 동일하게 요청을 보냄(POSPumpM_DV 전문)
					// 거래처 응답 전문은 uPOS 전문(4208)으로 변환 후 GB전문에 넣어 내려 보냄
					if (gaWorkMsg.getMessageType().equals(IUPOSConstant.MESSAGETYPE_4207)) {
					
						// PI2, CWI, 2016-04-11
						// 거래처 요청 전문이 올 경우 이전 노줄의 데이터를 초기화 한다.
						LogUtility.getLogger().info("[Pump M] 거래처 정보 요청.ConnectNozzleNo="+gaWorkMsg.getConnectNozzleNo());
						PumpMTransactionManager.getInstance().setNozzleState(gaWorkMsg.getConnectNozzleNo(), IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED);
						PumpMODTSaleManager.initSaleContent(gaWorkMsg.getConnectNozzleNo());
						
						try {
							// 거래처 정보 요청시 단가는 0 으로 요청한다. -- 박종호
							// ksm 2012.06.13 초기화시로 셋팅으로 수정함. 
							String preset_basePrice = "0";

							POS_DV dvPumpM = null;
							String cust_card_no = gaWorkMsg.getUnityMessage().getCustCard_No(); // 거래처 카드번호
							
							String nozzle_no = gaWorkMsg.getConnectNozzleNo(); // 노즐 번호
							
							String goods_code = T_NZ_NOZZLEHandler.getHandler().getGoods_codeBYNozzle_no(nozzle_no); // 상품 코드
							
							// pi2, cwi, 2016-04-15
							// 거래처 시작 거래일 경우 4207요청 시 KH번호를 새로히 발급 받는다. - 운영팀 요청 사항
							String khTransactionID = PumpMTransactionManager.getInstance().getKHTransactionID(gaWorkMsg.getConnectNozzleNo(), IPumpConstant.KH_ODT_PAID_REQ);
							LogUtility.getLogger().info("[Pump M] Pump_Not_Completed khTransactionID=" + khTransactionID);
							
							dvPumpM = new POS_DV(messageID, nozzle_no, khTransactionID, cust_card_no, goods_code, preset_basePrice);
							
							Preamble preamble = PumpMUtil.createPreamble(uniqueKey, SyncManager.DISE_POS_ADAPTER, dvPumpM, "");
							PumpMSyncManager.setSyncData(uniqueKey, dvPumpM.getMessageID(), SyncManager.DISE_PUMP_MODULE, receiving_pumpa);

							LogUtility.getLogger().info("[Pump M] Request Cust Processing to POS");

							sendMessage(preamble);
						} catch (Exception e) {
							LogUtility.getLogger().error(e.getMessage(), e);
						}
							
					// PI2, 2015-12-02, cwi
					// 여전법 대응에 따라 ODT의 upos 처리를 위해 초기 카드 리딩 시 캠페인 주유정보를 불러 오도록 추가
					}else if (gaWorkMsg.getMessageType().equals(IUPOSConstant.MESSAGETYPE_4201)) {

						// PI2, CWI, 2016-02-11 주유 완료 후 ODT가 멈출 경우 노줄의 상태가 주유 완료로 HASH 에 저장되어 있어 동일한 KH번호로
						// 처리 되는 문제가 발생, 이를 방지 하기 위해 첫 4201요청 시 LedCode 값이 S로 넘어올 경우 노줄 상태를 판매 완료로 설정 한다.
						// 회의 참석자 : 조원익, 박동화 사장님, 장차장님, 권순관 이사님, 박종호 과장님, 송기석 과장님
						if ("S".equals(gaWorkMsg.getLedCode())){
							
							/*
							 * 2016.05.09 WooChul Jung.
							 * 	New Self ODT 의 경우, 정상 상태 정보가 ODT 로 부터 전송되지 않는다.
							 *  따라서 최초 거래 시점에서, KH 는 ODT 가 정상이라고 판단하고, 정상 상태 정보를 KH 에서 생성해서 POS 로 전송한다. 
							 * 
							 */						
							try {
								WorkingMessage stateMsg = S8_WorkingMessage.createSelfODTOKState(gaWorkMsg.getConnectNozzleNo()) ;
								Preamble statePreamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_STATE_MODULE, stateMsg, "");
								sendMessage(statePreamble) ;
							} catch (Exception e) {
								LogUtility.getLogger().error(e.getMessage(),e) ;
							}
							
							POS_DW dwPumpM = null;
							
							// PI2, CWI, 2016-04-11 거래처일 경우 4207 요청 시 KH 처리번호를 생성하도록 수정
							if(GlobalUtility.isNullOrEmptyString(gaWorkMsg.getUnityMessage().getCustCard_No())){
								LogUtility.getLogger().info("[Pump M] 거래처 외 첫 시작 4201 요청.ConnectNozzleNo="+gaWorkMsg.getConnectNozzleNo());
								PumpMTransactionManager.getInstance().setNozzleState(gaWorkMsg.getConnectNozzleNo(), IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED);	
								PumpMODTSaleManager.initSaleContent(gaWorkMsg.getConnectNozzleNo());
								
							}else{
								LogUtility.getLogger().info("[Pump M] 거래처 첫 시작 4201 요청.ConnectNozzleNo="+gaWorkMsg.getConnectNozzleNo());
							}
							
							// 저장해 놓은 dwPumpM 데이터를 가져 온다.
							POSHeader posObj = PumpMODTSaleManager.getCustPOSPumpM(gaWorkMsg.getConnectNozzleNo());								
							if ((posObj != null) && (posObj instanceof POS_DW)) dwPumpM = (POS_DW) posObj;
							
							// 노줄의 상태를 초기화
							GSCSelfPumpingManager.getInstance().createODTNozzleInfo(gaWorkMsg.getConnectNozzleNo(), false, IConstant.FULL_PUMPING_OPTION_0);
							
							// pi2, cwi, 2016-02-19
							// createODTNozzleInfo 사용 시 현 노줄의 정보를 초기화 하기에 pi2에서 사용하는 시스템에 문제가 발생됨.(앞단에서 저장한 거래처 정보가 삭제 되는 문제)
							// to-be시스템의 경우 소모 방식을 체택하기에 특별히 전문을 저장할 일이 적으나, 거래처의 재승인 시 4201에 상품스트럭츠를 재 생성 해야 하는 문제가 있어
							// 추가적으로 노줄 초기화 후 앞 거래처 전문을 재 저장해 hash에 저장 해 놓는다.
							// 추후 createODTNozzleInfo 기능의 필요성이 무의미 할 경우 createODTNozzleInfo를 삭제 하고, 전문을 다시 저장하는 로직을 빼도 무관하다.
							if(dwPumpM != null){
								PumpMODTSaleManager.setCustInfo(dwPumpM.getDeviceID(), dwPumpM) ;
							}
							
							/**
							 * 2016.05.25 WooChul Jung.
							 * 	Set credit-card No for Prompt-Discount logging
							 */
							if (!GlobalUtility.isNullOrEmptyString(gaWorkMsg.getCardNumber())) {
								PumpMODTSaleManager.setCardNo(gaWorkMsg.getConnectNozzleNo(), gaWorkMsg.getCardNumber()) ;
							}
						}
						
						Preamble preamble = PumpMUtil.createUPOSMessagePreamble(messageID,
								SyncManager.DISE_CAT_MODULE,
								SyncManager.DISE_PUMP_MODULE, 
								gaWorkMsg.getUnityMessage(), "");
						
						sendMessage(preamble);
					
					} else{
						
						// PI2, CWI, 2105-12-29, 캠페인 주유정보 요청에 따라 새로운 KH 처리는 CatMController에서 4201 처리 시 생성 하도록 수정
						SqlSession session = null;
			
						try {

							ODTUtility_Common.logAdditionalInfo(gaWorkMsg) ;

							session = SqlSessionFactoryManager.openSqlSession();
							String binChekCardNo = "";
							String track2Check = ""; // 2012.10.09 ksm
			
							if (!GlobalUtility.isNullOrEmptyString(gaWorkMsg.getCardNumber())){
								binChekCardNo = gaWorkMsg.getCardNumber().substring(0,6);
														
								// 2012.10.09 ksm  신용카드일 경우 Track2 데이터까지 길이가 37 자리임.
								// 2012.10.12 ksm  보너스 카드는 Track2 데이터가 없음. 신형다쓰노의 경우 신용카드 자리수 체크를 하지않음.
								if(gaWorkMsg.getCardNumber().length() > 36){
									track2Check = gaWorkMsg.getCardNumber().substring(32, 36);
								}
							}
							
							// 2012.10.09 ksm 조달청 셀프 현장할인 막기위해 임시로직 추가
							// if("2080".equals(track2Check)){
//20171204 ygh 로직제거	(소매지원팀 김지영차장님 요청 신자영은 수정)							
//							if("6950".equals(track2Check)){
//								LogUtility.getLogger().info("[승인거절] 조달청 제휴카드! Track2 : " + track2Check);
//								
//								Integer oldMessageType = Integer.parseInt(gaWorkMsg.getMessageType());
//								Integer changeMessageType = oldMessageType + 1;
//								String  newMessageType = GlobalUtility.appending0Pre(changeMessageType.toString(),4);
//								
//								GB_WorkingMessage gbWorkMsg = new GB_WorkingMessage();
//								gbWorkMsg.setMessageType(newMessageType);
//								gbWorkMsg.setNozzleNo(gaWorkMsg.getNozzleNo());
//								gbWorkMsg.setConnectNozzleNo(gaWorkMsg.getConnectNozzleNo());
//								gbWorkMsg.setMode("2");
//								
//								String emp_no 			= gaWorkMsg.getUnityMessage().getEmp_no(); 	   // 충전원 ID
//								String custCard_No 		= gaWorkMsg.getCustCardNo(); 				   // 거래처 카드번호
//								String ss_crStNum 		= gaWorkMsg.getUnityMessage().getSs_crStNum(); // 거래처번호
//								String ss_carNum 		= gaWorkMsg.getUnityMessage().getSs_carNum();  // 거래처차량번호
//								String term_id 			= gaWorkMsg.getUnityMessage().getTerm_id() ;   // Default
//								String led_code 		= ""; // ODT에서 채워서 다시 올려줘야 하기에 ""로 설정
//								String lastPayment_yn 	= ""; // 마지막 결제 여부 체크
//								String nozzleNo       = gaWorkMsg.getConnectNozzleNo();		// 노줄번호
//								String paymentAmt 	  = gaWorkMsg.getPrice();				// 결재 금액
//								UPOSMessage_ItemInfo item_info = gaWorkMsg.getUnityMessage().getItem_info();
//								UPOSMessage_TradeCondition affilate_info2 = null;
//								
//								UPOSMessage uPosMsg = new UPOSMessage();
//								
//								uPosMsg.setMessageType(newMessageType);
//								uPosMsg.setItem_info(item_info);
//								uPosMsg.setEmp_no(emp_no);
//								uPosMsg.setCustCard_No(custCard_No);
//								uPosMsg.setSs_crStNum(ss_crStNum);
//								uPosMsg.setSs_carNum(ss_carNum);
//								uPosMsg.setTerm_id(term_id);
//								uPosMsg.setLed_code(led_code);
//								uPosMsg.setLastPayment_yn(lastPayment_yn); // 마지막 결제 처리
//								uPosMsg.setNozzle_no(nozzleNo);
//								uPosMsg.setPayment_amt(paymentAmt);
//								uPosMsg.setVan_Res_Code("1111"); //VAN응답코드
//								uPosMsg.setTradeCondition(affilate_info2);
//								uPosMsg.setDisplay_msg("조달청 제휴카드 신용 승인 거절");
//								uPosMsg.setVan_msg("조달청 제휴카드 신용 승인 거절");
//								uPosMsg.setPosReceipt_no(gaWorkMsg.getUnityMessage().getPosReceipt_no()); // 전표번호
//								uPosMsg.setBonRSCard_no(gaWorkMsg.getUnityMessage().getBonRSCard_no());   // 보너스카드번호
//								uPosMsg.setTrdate_creditCard(gaWorkMsg.getUnityMessage().getTrdate_creditCard()); // 신용승인일시
//								
//								gbWorkMsg.setUnityMessage(uPosMsg);
//								
//								Preamble preamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_PUMP_ADAPTER, gbWorkMsg, "");
//								
//								LogUtility.getLogger().info("[Pump M] Send WorkMsg to Pump A");
//								
//								sendMessage(preamble);
//			
//								// 취소된 결제 임으로 관련 정보 리셋
//								PumpMTransactionManager.getInstance().setNozzleState(	gaWorkMsg.getConnectNozzleNo(), IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED);
//								PumpMODTSaleManager.initSaleContent(gaWorkMsg.getConnectNozzleNo());
//								
//								break;
//							}
							
							// 운수사 체크 시 일부 거래처의 정보가 메모리에 남아 있어 정상 체크가 되지 않는 문제가 확인되어
							// 관련 체크 로직 수정 - 2016.02.26. cwi
							if (T_KH_BIN_INFOHandler.getHandler().isExist(session, "03", binChekCardNo) 
									|| T_KH_BIN_INFOHandler.getHandler().isExist(session, "09", binChekCardNo)){
								
								LogUtility.getLogger().debug("운수사 BIN CHECK");
								
								POSHeader posMsg = PumpMODTSaleManager.getCustPOSPumpM(gaWorkMsg.getConnectNozzleNo());
								String custCode = "";
								
								if (posMsg != null)
									custCode = ((POS_DW) posMsg).getCust_code();
								
								if (!("").equals(custCode) && !("").equals(gaWorkMsg.getCustCardNo())) {
									
									LogUtility.getLogger().info("[Pump M] 거래처카드 정보가 있기 때문에 정상 처리함.");
									
									String khproc_no = gaWorkMsg.getUnityMessage().getPosReceipt_no();
									
									processGA(uniqueKey, messageID, gaWorkMsg, khproc_no);
									
								} else {
								
									LogUtility.getLogger().info("[Pump M] 거래처카드 정보가 없음. 거래거절");
									
									Integer oldMessageType = Integer.parseInt(gaWorkMsg.getMessageType());
									Integer changeMessageType = oldMessageType + 1;
									String  newMessageType = GlobalUtility.appending0Pre(changeMessageType.toString(),4);
									
									// 운수사 카드로 일반 승인요청 시 ODT로 응답 전문을 생성 해 내려 준다.(uPOS 생성)
									GB_WorkingMessage gbWorkMsg = new GB_WorkingMessage();
									gbWorkMsg.setMessageType(newMessageType);
									gbWorkMsg.setNozzleNo(gaWorkMsg.getNozzleNo());
									gbWorkMsg.setConnectNozzleNo(gaWorkMsg.getConnectNozzleNo());
									gbWorkMsg.setMode("2");

									String emp_no 			= gaWorkMsg.getUnityMessage().getEmp_no(); 	   // 충전원 ID
									String custCard_No 		= gaWorkMsg.getCustCardNo(); 				   // 거래처 카드번호
									String ss_crStNum 		= gaWorkMsg.getUnityMessage().getSs_crStNum(); // 거래처번호
									String ss_carNum 		= gaWorkMsg.getUnityMessage().getSs_carNum();  // 거래처차량번호
									String term_id 			= gaWorkMsg.getUnityMessage().getTerm_id() ;   // Default
									String led_code 		= ""; // ODT에서 채워서 다시 올려줘야 하기에 ""로 설정
									String lastPayment_yn 	= ""; // 마지막 결제 여부 체크
									String nozzleNo       = gaWorkMsg.getConnectNozzleNo();		// 노줄번호
									String paymentAmt 	  = gaWorkMsg.getPrice();				// 결재 금액
									UPOSMessage_ItemInfo item_info = gaWorkMsg.getUnityMessage().getItem_info();
									UPOSMessage_TradeCondition affilate_info2 = null;

									UPOSMessage uPosMsg = new UPOSMessage();

									uPosMsg.setMessageType(newMessageType);
									uPosMsg.setItem_info(item_info);
									uPosMsg.setEmp_no(emp_no);
									uPosMsg.setCustCard_No(custCard_No);
									uPosMsg.setSs_crStNum(ss_crStNum);
									uPosMsg.setSs_carNum(ss_carNum);
									uPosMsg.setTerm_id(term_id);
									uPosMsg.setLed_code(led_code);
									uPosMsg.setLastPayment_yn(lastPayment_yn); // 마지막 결제 처리
									uPosMsg.setNozzle_no(nozzleNo);
									uPosMsg.setPayment_amt(paymentAmt);
									uPosMsg.setVan_Res_Code("1111"); 		   // VAN응답코드
									uPosMsg.setTradeCondition(affilate_info2);
									uPosMsg.setDisplay_msg("운수사(유가보조)카드 신용 승인 거절");
									uPosMsg.setVan_msg("운수사(유가보조)카드 신용 승인 거절");
									uPosMsg.setPosReceipt_no(gaWorkMsg.getUnityMessage().getPosReceipt_no()); // 전표번호
									uPosMsg.setBonRSCard_no(gaWorkMsg.getUnityMessage().getBonRSCard_no());   // 보너스카드번호
									uPosMsg.setTrdate_creditCard(gaWorkMsg.getUnityMessage().getTrdate_creditCard()); // 신용승인일시
									
									gbWorkMsg.setUnityMessage(uPosMsg);
									
									Preamble preamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_PUMP_ADAPTER, gbWorkMsg, "");
									
									LogUtility.getLogger().info("[Pump M] Send WorkMsg to Pump A");
									
									sendMessage(preamble);
									
									// 취소된 결제 임으로 관련 정보 리셋
									PumpMTransactionManager.getInstance().setNozzleState(	gaWorkMsg.getConnectNozzleNo(), IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED);
									PumpMODTSaleManager.initSaleContent(gaWorkMsg.getConnectNozzleNo());
									
								}
							} else {
								
								String khproc_no = gaWorkMsg.getUnityMessage().getPosReceipt_no();
								
								processGA(uniqueKey, messageID, gaWorkMsg, khproc_no);
							}
							
						} catch (Exception e) {
							LogUtility.getLogger().error(e.getMessage(), e);
						} finally {
							SqlSessionFactoryManager.closeSqlSession(session);
						}
					}
				}
				break;
			}
			
			
				/**
				 * PI2, 신규 ODT 추가에 따른 프로세스 추가, 2016.03.24 - CWI
				 * GSC Self 세차권 바코드 요청 전문 추가
				 */
			case IPumpConstant.COMMANDID_BR: {
				
				BR_WorkingMessage brWorkMsg = (BR_WorkingMessage) receiveWorkingMessage;
				
				LogUtility.getPumpMLogger().info("[Pump M] Receive BR from GSC Self. ODTID=" + brWorkMsg.getNozzleNo());
				brWorkMsg.print();
				
				BS_WorkingMessage bsMsg = new BS_WorkingMessage();
				bsMsg.setConnectNozzleNo(brWorkMsg.getConnectNozzleNo());
				bsMsg.setNozzleNo(brWorkMsg.getNozzleNo());

				try {
					String barcode = "";
					//barcode_type(1:주유바코드, 2:세차바코드)
					String barcode_type = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0441);
					
					LogUtility.getPumpMLogger().info("[Pump M] Receive BR from GSC Self. BarcodeType = " + barcode_type);
					
					if ("1".equals(barcode_type) || "01".equals(barcode_type)) {
						//주유바코드 생성로직
						if(!brWorkMsg.getPrice().equals("")){
							
							// 주유바코드
							barcode = Barcode.getBarcodeNumberPump("3", brWorkMsg.getPrice(), brWorkMsg.getConnectNozzleNo(), brWorkMsg.getPosReceiptNo(), null, null, null);			
							LogUtility.getPumpMLogger().info("[PUMP M] barcode="+barcode);
						}else{
							LogUtility.getPumpMLogger().error("[PUMP M] 주유 금액이 존재 하지 않습니다.!");
						}
						bsMsg.setBarcode(barcode);
						
						Preamble pumpPreamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_PUMP_ADAPTER, bsMsg, "") ;
						
						sendMessage(pumpPreamble);

						// POS에 EN전문 송신(바코드 출력 여부 저장)
						processOPTGSCSelfBarcode(bsMsg, brWorkMsg.getPosReceiptNo());
					}
					else if ("2".equals(barcode_type) || "02".equals(barcode_type)) {
						// 세차바코드 생성 로직 처리
						if(!brWorkMsg.getPrice().equals("")){
							
							// 세차바코드
							barcode = Barcode.getBarcodeNumber("3", brWorkMsg.getPrice(), brWorkMsg.getConnectNozzleNo(), brWorkMsg.getPosReceiptNo(), null, null, null);			
							LogUtility.getPumpMLogger().info("[PUMP M] barcode="+barcode);
						}else{
							LogUtility.getPumpMLogger().error("[PUMP M] 주유 금액이 존재 하지 않습니다.!");
						}
						bsMsg.setBarcode(barcode);
						
						Preamble pumpPreamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_PUMP_ADAPTER, bsMsg, "") ;
						
						sendMessage(pumpPreamble);
						
						// POS에 EN전문 송신(바코드 출력 여부 저장)
						 processOPTGSCSelfBarcode(bsMsg, brWorkMsg.getPosReceiptNo());
					}
					else {
						//주유도세차도 아닐때 빈바코드를 전송한다. 
						//BR로 요청을 받았으니 BS송신을 해줘야 정상처리
						Preamble pumpPreamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_PUMP_ADAPTER, bsMsg, "") ;
						sendMessage(pumpPreamble);
					}
					
				} catch (Exception e) {
					LogUtility.getPumpMLogger().error(e.getMessage(), e);
				}
				break;
			}
			
				/**
				 * PI2, 신규 ODT 추가에 따른 프로세스 추가, 2015.11.18 - CWI
				 * GSC Self 판매완료 수신한다. 수신 이후 0001 전문을 보낼것인지를 조사하고 처리 진행
				 */
			case IPumpConstant.COMMANDID_GT: {
				// GSC Self - GSC Self  판매 완료
				GT_WorkingMessage gtWorkMsg = (GT_WorkingMessage) receiveWorkingMessage;
	
				/**
				 * 2016.05.19 WooChul Jung
				 * 	GT 전문 수신 이후 해당 노즐에 대한 초기화가 발생함.
				 * 	승인 응답 전문 이후 GT 전문이 동시에 수신할 경우, 노즐 정보 초기화로 인한 Side Effect 발생할 수 있음.
				 * 	이로 인하여 GT 전문 수신하면 1초 대기 이후 실행.
				 */
				Thread.sleep(1000) ;
				LogUtility.getLogger().info("[Pump M] Waiting 1 seconds. Receive GT from GSC Self. ODTID=" + gtWorkMsg.getNozzleNo());
				gtWorkMsg.print();
				
				// PI2, CWI, 2016-04-05
			    // ODT로 부터 올라온 주유 중 자료 및 주유중 자료의 길이 문제로 parsing 처리가 되지 않아
				// 이후 주유 시 리터 및 주유량 보정으로 인한 DE전문 혹은 DG전문의 litter 수치가 소숫점 3자리 이상으로 증가 되는 문제를 방지 하기 위해
				// 판매 완료 처리시에도 금액/리터의 데이터를 초기화 한다.
				int nozzle_noInt = Integer.parseInt(gtWorkMsg.getConnectNozzleNo());
				PumpMPriceManager.initPumpPrice(nozzle_noInt);
				
				String khproc_no = PumpMTransactionManager.getInstance().getKHTransactionID(gtWorkMsg.getConnectNozzleNo(),IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED);
				processGT(uniqueKey, messageID, commandID, gtWorkMsg, khproc_no);
				break;
			}
			
			
				/**
				 * 다쓰노 셀프로 부터 카드정보 조회가 수신 신용카드/ 거래처 카드 응답 전문은 CB
				 */
			case IPumpConstant.COMMANDID_CA: {
				CA_WorkingMessage caWorkMsg = (CA_WorkingMessage) receiveWorkingMessage;

				LogUtility.getLogger().info("[Pump M] Receive CA from ODT. ODTID=" + caWorkMsg.getNozzleNo());
				caWorkMsg.print();

				SqlSession session = null;

				caWorkMsg.setCardNo(caWorkMsg.getCardNo().substring(0, caWorkMsg.getCardNo().trim().indexOf("=")));

				try {
					session = SqlSessionFactoryManager.openSqlSession();
					boolean isCargo = false;
					CustReturnValue custRtnValue = null;

					if (caWorkMsg.getCardNo() != null) {
						custRtnValue = CustUtil.processCustWithCustCardNo( caWorkMsg.getConnectNozzleNo(), caWorkMsg	.getCardNo(), "0");

						if (custRtnValue.getState() == ICustConstant.STATE_100)
							isCargo = true;
					}

					// 영업화물 체크
					if (isCargo) {

						CB_WorkingMessage cbWorkingMsg = new CB_WorkingMessage();

						cbWorkingMsg.setNozzleNo(caWorkMsg.getNozzleNo());
						cbWorkingMsg.setConnectNozzleNo(caWorkMsg.getConnectNozzleNo());
						cbWorkingMsg.setCustomerType(IPumpConstant.CB_CUSTOMER_TYPE_CUS_CASH); // 현금 거래처로 세팅
						cbWorkingMsg.setSaveBonus("1");

						Preamble cbPreamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey, SyncManager.DISE_PUMP_ADAPTER, cbWorkingMsg, "");

						sendMessage(cbPreamble);

						// 영업화물인 경우 DW전문을 임의로 만들어 준다. edited by ykJang
						String goods_code = T_NZ_NOZZLEHandler.getHandler().getGoods_codeBYNozzle_no(caWorkMsg.getConnectNozzleNo()); // 상품 코드

						POS_DW dwPumpM = new POS_DW("", caWorkMsg
								.getConnectNozzleNo(), "", caWorkMsg
								.getCardNo(), "", custRtnValue.getCust_name(),
								custRtnValue.getCust_code(), goods_code,
								custRtnValue.getDiscountBasePrice(),
								custRtnValue.getCust_cd_item(), "", "", "", "",
								"", "1", "", "", "", "", "", "", "", "", "");

						PumpMODTSaleManager.setCustInfo(caWorkMsg.getConnectNozzleNo(), caWorkMsg, dwPumpM);
					}
					// 영업화물이 아닌 경우 거래처 정보 조회.
					else {
						String goods_code = T_NZ_NOZZLEHandler.getHandler().getGoods_codeBYNozzle_no( caWorkMsg.getConnectNozzleNo()); // 상품 코드

						String unitPrice = "0";

						POS_DV dvPumpM = null;

						dvPumpM = new POS_DV(messageID, caWorkMsg.getConnectNozzleNo(), "", 
																					caWorkMsg.getCardNo(), goods_code, unitPrice);
						
						Preamble preamble = PumpMUtil.createPreamble(uniqueKey, SyncManager.DISE_POS_ADAPTER, dvPumpM, "");
						PumpMSyncManager.setSyncData(uniqueKey, dvPumpM.getMessageID(), SyncManager.DISE_PUMP_MODULE, receiving_pumpa);

						LogUtility.getLogger().info("[Pump M] Request Cust Processing to POS");
						sendMessage(preamble);
					}

				} catch (Exception e) {
					LogUtility.getLogger().error(e.getMessage(), e);
				} finally {
					SqlSessionFactoryManager.closeSqlSession(session);
				}
				break;
			}
			
				/**
				 * 프로젝트 : PI2
				 * 내용: TCP/IP 통신방식 적용 충전기 ODT 승인 요청 및 응답 처리  
				 * 충전기ODT 신용승인 요청(0031)/응답(0032), 신용+보너스승인 요청(0033)/응답(0034), 캠페인정보 요청(4291)/응답(4292), 
				 * 현금영수증 요청(0015)/응답(0016) 전문 생성, 보너스누적 요청(0003)/응답(0004), GS점수사용 요청(0061)/응답(0062), 
				 * GS보너스점수조회 요청(0111), 현금+보너스 요청(0013)/응답(0014), 쿠폰사용 요청(0063)/응답(0064), 쿠폰사용 취소 요청(8063)/응답(8064)
				 * 변경일자 : 2015.12.23 양일준
				 * 
				 * '유가보조거래카드승인 + 보너스적립' 요청 (0033) 프로세스 
				 * 1. 0033 (ODT -> KH)
				 * 2. 0034 (ODT <- KH)  장애대응에서 거절(응답코드: 4123)
				 * 3. 0031 (ODT -> KH)  ODT에서 VAN응답코드가 4123일 경우, 단독신용승인 변경 및 요청
				 * 4. 0032 (ODT <- KH)
				 */
			case IPumpConstant.COMMANDID_SK:{
				SK_WorkingMessage skWorkMsg = (SK_WorkingMessage) receiveWorkingMessage;
				String skMessageType = skWorkMsg.getMessageType();
				String nozzleNo = skWorkMsg.getNozzleNo();
				UPOSMessage uPos = skWorkMsg.getUnityMessage();
				uPos.setMessageID(skWorkMsg.getMessageID());
				
				if(skMessageType.equals(IUPOSConstant.MESSAGETYPE_0031)        // 신용승인 요청
						||skMessageType.equals(IUPOSConstant.MESSAGETYPE_0033) // 신용 + 보너스 요청
						){        
					
					LogUtility.getLogger().info("[Pump M] Receive SK WorkingMessage(" + skMessageType + ") from NewRecharge ODT. ODTID=" + nozzleNo);
					//uPos.print();
					
					String binChekCardNo = "";
					
					if (!GlobalUtility.isNullOrEmptyString(uPos.getCreditCard_no().trim())) {
	
						binChekCardNo = uPos.getCreditCard_no().substring(0, 6);
					}
	
					LogUtility.getLogger().info("[Pump M] binChekCardNo =" + binChekCardNo);
	
					// 법인택시 보너스 적립 여부 체크
					boolean rlt = ODTUtility_Recharge.checkBizBin(binChekCardNo);
	
					LogUtility.getLogger().debug("[Pump M] 법인 택시 BIN CHECK : " + rlt);
	
					if (rlt) {
	
						if (PumpMODTSaleManager.getCustPOSPumpM(nozzleNo) != null) {
							
							LogUtility.getLogger().debug("[Pump M] 거래처카드 정보가 있기 때문에 정상 처리함.");
	
							String posReceipt_no = PumpMTransactionManager.getInstance().getKHTransactionID(nozzleNo); 
							
							sendLockingInfoToPOS(nozzleNo, posReceipt_no, IConstant.PUMP_SALE_LOCKING);
		
							Preamble preamble = PumpMUtil.createUPOSMessagePreamble(messageID,
									SyncManager.DISE_PUMP_ADAPTER,
									SyncManager.DISE_CMS_MODULE, uPos, "");
							
							LogUtility.getLogger().info("[Pump M] Send UPOSMessage to CAT M");
							sendMessage(preamble);
							
						} else {
							LogUtility.getLogger().debug("[Pump M] 거래처카드 정보가 없음. 거래처 카드 리딩 메세지 출력");
							
							SL_WorkingMessage slWorkMsg = new SL_WorkingMessage();
							UPOSMessage returnUpos = new UPOSMessage();
							
							String odtNo = PumpMUtil.getODTNumberFromNozzleNo(nozzleNo);
							
							returnUpos.setDeviceType(uPos.getDeviceType());
							returnUpos.setMessageType(UPOSMessageUtility.getRespondMessageType(uPos.getMessageType()));
							returnUpos.setPosReceipt_no(uPos.getPosReceipt_no());
							returnUpos.setNozzle_no(nozzleNo);
							returnUpos.setCreditCard_no(uPos.getCreditCard_no());
							returnUpos.setEmp_no(uPos.getEmp_no());
							returnUpos.setPayment_amt(uPos.getPayment_amt());
							returnUpos.setBonusSave_type(uPos.getBonusSave_type());
							returnUpos.setDeal_type(uPos.getDeal_type());
							returnUpos.setPartner_code(uPos.getPartner_code());
							returnUpos.setCreditCardReading_type(uPos.getCoupon_Trade_Type());
							returnUpos.setDisplay_msg("운수사거래카드만으로는 신용승인을 받을수 없습니다.");
							returnUpos.setStore_cd(uPos.getStore_cd());
							returnUpos.setVan_Res_Code("1111");
							
							slWorkMsg.setUnityMessage(returnUpos);	
							slWorkMsg.setMessageType(UPOSMessageUtility.getRespondMessageType(uPos.getMessageType()));
							slWorkMsg.setNozzleNo(odtNo);
							slWorkMsg.setConnectNozzleNo(nozzleNo);
							slWorkMsg.setPosReceiptNo(uPos.getPosReceipt_no());
							
							Preamble preamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_PUMP_ADAPTER, slWorkMsg, "");
							LogUtility.getLogger().info("[Pump M] Send WorkMsg to Pump A");
							sendMessage(preamble);
							
						}
					} else {
						
						String posReceipt_no = PumpMTransactionManager.getInstance().getKHTransactionID(nozzleNo); 
						
						sendLockingInfoToPOS(nozzleNo, posReceipt_no, IConstant.PUMP_SALE_LOCKING);
	
						Preamble preamble = PumpMUtil.createUPOSMessagePreamble(messageID,
								SyncManager.DISE_PUMP_ADAPTER,
								SyncManager.DISE_CMS_MODULE, uPos, "");
						
						LogUtility.getLogger().info("[Pump M] Send UPOSMessage to CAT M");
						sendMessage(preamble);
						
					}
				} else if(skMessageType.equals(IUPOSConstant.MESSAGETYPE_0015)            // 국세청상품권현금영수증 요청
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_0003)        // 보너스누적 요청
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_0061)        // GS점수사용 요청
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_0111)        // GS보너스점수조회 요청
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_0063)		  // 쿠폰사용 요청
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_0065)		  // 쿠폰 + 국세청현금영수증 사용 요청
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_0013)	      // 현금 + 보너스 요청 ( 현장할인 적용 시 사용 )
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_0045)        // 페이승인 요청
						    ||skMessageType.equals(IUPOSConstant.MESSAGETYPE_0047)        // 페이 + 보너스 요청
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_8045)        // 페이사용 취소요청
						    ||skMessageType.equals(IUPOSConstant.MESSAGETYPE_8047)        // 페이 + 보너스 취소요청						    
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_8063)     	// 쿠폰사용 취소 요청
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_8065)){      // 쿠폰 + 국세청현금영수증 사용 취소 요청
						
					LogUtility.getLogger().info("[Pump M] Receive SK WorkingMessage(" + skMessageType + ") from NewRecharge ODT. ODTID=" + nozzleNo);
					//uPos.print();

					String posReceipt_no = PumpMTransactionManager.getInstance().getKHTransactionID(nozzleNo); 
					
					// 국세청상품권현금영수증 요청(0015) 시 캠페인주유완료정보 요청(4291)이 없기 때문에 KH에서 KixxHub 처리번호를 넣어줌.  
					// (ODT에서 승인요청 전문을 생성 시, 전문에 KH처리번호를 넣기 위해서는 캠페인주유완료정보 요청/응답이 필요함)
					if(skMessageType.equals(IUPOSConstant.MESSAGETYPE_0015) && uPos.getPosReceipt_no().equals("00000000000000")){
						uPos.setPosReceipt_no(posReceipt_no);
					}
					
					sendLockingInfoToPOS(nozzleNo, posReceipt_no, IConstant.PUMP_SALE_LOCKING);

					Preamble preamble = PumpMUtil.createUPOSMessagePreamble(messageID,
							SyncManager.DISE_PUMP_ADAPTER,
							SyncManager.DISE_CMS_MODULE, uPos, "");
					
					LogUtility.getLogger().info("[Pump M] Send UPOSMessage to CAT M");
					sendMessage(preamble);
							
				// LED코드가 포함된 응답전문을 돌려받으면 SALE MODULE로 전송 
				} else if(skMessageType.equals(IUPOSConstant.MESSAGETYPE_0032)      	// 신용승인 응답
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_0034)      // 신용+ 보너스 응답
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_0015)      // 국세청상품권현금영수증 요청
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_0004)      // 보너스누적 응답 
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_0062)      // GS점수사용 응답
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_0064)      // 쿠폰사용 응답 
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_0066)      // 쿠폰 + 국세청현금영수증 사용 응답 
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_0014)      // 현금 + 보너스 응답
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_0046)      // 페이승인 요청
						    ||skMessageType.equals(IUPOSConstant.MESSAGETYPE_0048)      // 페이 + 보너스 요청	
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_8046)      // 페이승인 요청
						    ||skMessageType.equals(IUPOSConstant.MESSAGETYPE_8048)      // 페이 + 보너스 요청												    
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_8064) 	   // 쿠폰사용 취소 응답  
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_8066)){    // 쿠폰 + 국세청현금영수증 사용 취소 응답  
					LogUtility.getLogger().info("[Pump M] Receive SK WorkingMessage(" + skMessageType + " + LED) from NewRecharge ODT . ODTID=" + nozzleNo);
					
					//UPOSMessage uPos = skWorkMsg.getUnityMessage();
					//uPos.print();
					
					//응답 확인전문을 ODT로부터 받아 Sale M으로 보냄
					//Sale M 으로 응답 전문을 보낼지에 대한 판단을 한다.(As-Is 경우, CatMController에 있던 로직) 
					if (ODTUtility_Common.shouldSendToSaleM(uPos)) {
		    			//2012.04.23 ksm 
				    	//승인응답전문의 LED_CODE=2(거절) 이고 Credit_AuthInfo(부가정보)값이 "CU"(단말기일련번호중복)인 경우 전문추적번호를 500 증가시	킴 
				    	if("2".equals(skWorkMsg.getLedCode())){
				    		
				    		LogUtility.getLogger().info("[Pump M] [Check TrackingNo] 응답 전문의 LED_CODE 값 = 2" ) ;
				    		
				    		// 거절사유가 "CU" 단말기일련번호중복인 경우 전문추적번호 증가 시킴.
				    		if("CU".equals(uPos.getCredit_authInfo())){
				    			
				    			LogUtility.getLogger().info("[Pump M] [Check TrackingNo] 단말기일련번호 중복 발생!!!" ) ;
				    			// 전문추적번호 증가
				    			PumpMTransactionManager.getInstance().increaseTrackingValue();
				    		}
				    	}
				    	    			
		    		   	LogUtility.getLogger().info("[Pump M] Send UPOSMessage to SaleM") ;
		    		   	
				    	Preamble posPreamble = PumpMUtil.createUPOSMessagePreamble( null, 
																		 SyncManager.DISE_PUMP_MODULE,
																		 SyncManager.DISE_SALE_MODULE, 
																		 uPos, 
																		 "") ;
				    	sendMessage(posPreamble) ;
		    		}
						
					// 캠페인주유완료정보 요청
					// 충전기 ODT의 경우, CustCard_Car_Type(거래처별/차량별 구분) 값이 '0'으로 올라옴 
				} else if (skMessageType.equals(IUPOSConstant.MESSAGETYPE_4291)){    // 캠페인주유완료정보 요청 
						
						LogUtility.getLogger().info("[Pump M] Receive SK WorkingMessage(4291) from NewRecharge ODT. ODTID=" + nozzleNo);
	
						Preamble preamble = PumpMUtil.createUPOSMessagePreamble(messageID,
								SyncManager.DISE_CAT_MODULE,
								SyncManager.DISE_PUMP_MODULE, 
								uPos, "");
						
						//skWorkMsg.getUnityMessage().print();
						
						LogUtility.getLogger().info("[Pump M] Send UPOSMessage to CAT M");
						sendMessage(preamble);
							
				} 
				break;
			}
			
				/**
				 * Serial 통신 방식 충전기 ODT의 승인 요청 처리
				 * 충전기의 카드 결제 요청을 수신한다. 현금 거래처인 경우는 거래처 단가 및 금액으로 변경하여서 VAN 사에
				 * 요청한다. 충전기로 부터 오는 모든 결제 요청은 주유 단가로 오기 때문이다.
				 */
			case IPumpConstant.COMMANDID_SB: {
				
				SB_WorkingMessage sbWorkMsg = (SB_WorkingMessage) receiveWorkingMessage;
				
				LogUtility.getLogger().info("[Pump M] Receive SB from Recharge. ODTID=" + sbWorkMsg.getNozzleNo());
				sbWorkMsg.print();

				String binChekCardNo = "";

				if (!GlobalUtility.isNullOrEmptyString(sbWorkMsg.getContent().trim())) {

					// 신용카드번호 필드의 데이타 길이 체크
					if (sbWorkMsg.getContent().trim().length() < 10) {
						sendMessage(ODTUtility_Recharge	.invalidCardNo(sbWorkMsg));
						return;
					}
					binChekCardNo = sbWorkMsg.getContent().substring(0, 6);
				}

				if (!GlobalUtility.isNullOrEmptyString(sbWorkMsg.getBonusNumber().trim())) {

					// 보너스카드번호 필드의 데이타 길이 체크
					if (sbWorkMsg.getBonusNumber().trim().length() < 16) {
						sendMessage(ODTUtility_Recharge	.invalidCardNo(sbWorkMsg));
						return;
					}
					binChekCardNo = sbWorkMsg.getContent().substring(0, 6);
				}

				LogUtility.getLogger().info("[Pump M] binChekCardNo =" + binChekCardNo);

				// 법인택시 보너스 적립 여부 체크
				boolean rlt = ODTUtility_Recharge.checkBizBin(binChekCardNo);

				LogUtility.getLogger().debug("법인 택시 BIN CHECK : " + rlt);

				if (rlt) {
					// 법인 택시는 보너스 적립을 하지 않는다.
					sbWorkMsg.setBonusNumber("");

					if (PumpMODTSaleManager.getCustPOSPumpM(sbWorkMsg.getNozzleNo()) != null) {
						
						LogUtility.getLogger().debug("거래처카드 정보가 있기 때문에 정상 처리함.");
						PumpMObjectValidation.validatePumpAObject(sbWorkMsg);

						String posReceipt_no = PumpMTransactionManager.getInstance().getKHTransactionID(sbWorkMsg.getConnectNozzleNo());
						UPOSMessage uPos = ODTUtility_Common.createUPOSMessageFromWorkingMessage(sbWorkMsg, posReceipt_no);

						POSHeader posObj = PumpMODTSaleManager.getCustPOSPumpM(sbWorkMsg.getConnectNozzleNo());

						if ((posObj != null) && (posObj instanceof POS_DW)) {
							POS_DW dwPosMsg = (POS_DW) posObj;

							if (PumpMObjectValidation.validatePumpMObject(dwPosMsg)) {
								LogUtility.getLogger().info("[Pump M] add CustInfo to UPOSMessage before sending it. Cust_code=" + dwPosMsg.getCust_code());
								// Upos 전문에서 Ss_crStNum 항목만을 체크해서 매장마일리지를 적립한다.
								// Ss_crStNum 항목에는 거래처 카드 번호 또는 거래처 코드 둘 중 하나가
								// 들어오면 처리된다.
								uPos.setSs_crStNum(dwPosMsg.getCust_code()); // 거래처 코드
							}
						}

						uPos.setFiller2("");
						sendLockingInfoToPOS(sbWorkMsg.getNozzleNo(), uPos.getPosReceipt_no(), IConstant.PUMP_SALE_LOCKING);

						Preamble preamble = PumpMUtil.createUPOSMessagePreamble(messageID,
								SyncManager.DISE_PUMP_ADAPTER,
								SyncManager.DISE_CMS_MODULE, uPos, "");
						LogUtility.getLogger().info("[Pump M] Send UPOSMessage to CAT M");
						preamble.setCoLtdFlag(true);

						sendMessage(preamble);
					} else {
						LogUtility.getLogger().debug("거래처카드 정보가 없음. 거래처 카드 리딩 메세지 출력");
						PI_WorkingMessage piWorkMsg = new PI_WorkingMessage();

						piWorkMsg.setNozzleNo(sbWorkMsg.getNozzleNo());
						piWorkMsg.setMode("2");
						piWorkMsg.setCardType("0");
						piWorkMsg.setNotice("운수사거래카드만으로는 신용승인을 받을수 없습니다.[확인]클릭");

						Preamble preamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_PUMP_ADAPTER, piWorkMsg, "");
						LogUtility.getLogger().info("[Pump M] Send WorkMsg to Pump A");
						sendMessage(preamble);
					}
				} else {
					PumpMObjectValidation.validatePumpAObject(sbWorkMsg);

					String posReceipt_no = PumpMTransactionManager
							.getInstance().getKHTransactionID(
									sbWorkMsg.getConnectNozzleNo());
					UPOSMessage uPos = ODTUtility_Common.createUPOSMessageFromWorkingMessage(sbWorkMsg,posReceipt_no);

					sendLockingInfoToPOS(sbWorkMsg.getNozzleNo(), uPos.getPosReceipt_no(), IConstant.PUMP_SALE_LOCKING);

					POSHeader posObj = PumpMODTSaleManager	.getCustPOSPumpM(sbWorkMsg.getConnectNozzleNo());
					
					if ((posObj != null) && (posObj instanceof POS_DW)) {
						POS_DW dwPosMsg = (POS_DW) posObj;

						if (PumpMObjectValidation.validatePumpMObject(dwPosMsg)) {
							LogUtility.getLogger().info("[Pump M] add CustInfo to UPOSMessage before sending it. Cust_code=" + dwPosMsg.getCust_code());
							// Upos 전문에서 Ss_crStNum 항목만을 체크해서 매장마일리지를 적립한다.
							// Ss_crStNum 항목에는 거래처 카드 번호 또는 거래처 코드 둘 중 하나가 들어오면
							// 처리된다.
							uPos.setSs_crStNum(dwPosMsg.getCust_code()); // 거래처 코드
						}
					}

					Preamble preamble = PumpMUtil.createUPOSMessagePreamble(messageID,
							SyncManager.DISE_PUMP_ADAPTER,
							SyncManager.DISE_CMS_MODULE, uPos, "");
					
					LogUtility.getLogger().info("[Pump M] Send UPOSMessage to CAT M");
					sendMessage(preamble);
				}

				break;
			}
				/**
				 * 충전기로 부터 고객 카드 조회 요청을 수신한다. 이에 대한 처리를 POS 에 요청하고, 그 응답을 수신하여
				 * 충전기로 전송한다.
				 */
			case IPumpConstant.COMMANDID_S9: {
				// 충전기- 충전기 고객 카드 승인 요청 전문
				S9_WorkingMessage s9WorkMsg = (S9_WorkingMessage) receiveWorkingMessage;
				LogUtility.getPumpMLogger().info("[Pump M] Receive S9(Cust Processing) from Recharge. ODTID=" + s9WorkMsg.getNozzleNo());
				s9WorkMsg.print();

				processS9(uniqueKey, receiving_pumpa, messageID, s9WorkMsg);

				break;
			}
				/**
				 * 충전기의 보너스 점수 누적 요청 수신한다. 수신에 대한 응답을 받은 이후 충전기로 전송하고, 보너스 누적
				 * 전문은 POS 로 전송하지 않는다. 누적은 판매 완료 (SH) 전문 수신 이후 일어 날 수 있기 때문이다.
				 */
			case IPumpConstant.COMMANDID_BA: {
				// 충전기- 충전기 보너스 점수 누적 요청
				BA_WorkingMessage baWorkMsg = (BA_WorkingMessage) receiveWorkingMessage;
				
				LogUtility.getPumpMLogger().info("[Pump M] Receive BA from Recharge. ODTID=" + baWorkMsg.getNozzleNo());
				baWorkMsg.print();

				String posReceipt_no = PumpMTransactionManager.getInstance().getKHTransactionID(baWorkMsg.getConnectNozzleNo());
				UPOSMessage uPos = ODTUtility_Common.createUPOSMessageFromWorkingMessage(baWorkMsg, posReceipt_no);

				// POSPumpMHeader posObj =
				// PumpMODTSaleManager.getCustPOSPumpM(baWorkMsg.getConnectNozzleNo())
				// ;

				String custCardNo = PumpMTransactionManager.getInstance().getCustCardNumber(baWorkMsg.getConnectNozzleNo());

				// LogUtility.getPumpMLogger().debug("[TEST] 들어가있나???
				// custCardNo="+custCardNo);
				if (!"".equals(custCardNo)) {

					LogUtility.getPumpMLogger().info("[Pump M] add CustInfo to UPOSMessage before sending it. custCardNo=" + custCardNo);
					// Upos 전문에서 Ss_crStNum 항목만을 체크해서 매장마일리지를 적립한다.
					// Ss_crStNum 항목에는 거래처 카드 번호 또는 거래처 코드 둘 중 하나가 들어오면 처리된다.
					uPos.setSs_crStNum(custCardNo); // 거래처 카드 번호
				}

				Preamble preamble = PumpMUtil.createUPOSMessagePreamble(messageID,
						SyncManager.DISE_PUMP_ADAPTER,
						SyncManager.DISE_CMS_MODULE, uPos, "");
				
				LogUtility.getPumpMLogger().info("[Pump M] Send UPOSMessage to CAT M.");
				sendMessage(preamble);
				break;
			}
				/**
				 * 충전기 현금 영수증 요청을 수신한다. 수신에 대한 응답을 받은 이후 충전기로 전송하고, 이 전문은 POS 로는
				 * 전송하지 않는다, 현금 영수증 요청은 판매완료(SH) 전문 수신 이후 일어 날 수 있기 때문이다.
				 */
			case IPumpConstant.COMMANDID_TJ: {
				// 충전기- 충전기 현금 영수증 요청
				TJ_WorkingMessage tjWorkMsg = (TJ_WorkingMessage) receiveWorkingMessage;
				LogUtility.getPumpMLogger().info("[Pump M] Receive TJ from Recharge. ODTID=" + tjWorkMsg.getNozzleNo());
				tjWorkMsg.print();

				String posReceipt_no = PumpMTransactionManager.getInstance()
						.getKHTransactionID(tjWorkMsg.getConnectNozzleNo());
				UPOSMessage uPos = ODTUtility_Common.createUPOSMessageFromWorkingMessage(tjWorkMsg, 	posReceipt_no);

				// PI2 20160324 twlee 장애대응에서 주민번호로 현금영수증 발급 불가로 인해 kixxhub에서 장애대응에 보내지 않고 현금영수증 발급불가 처리
				if(IUPOSConstant.MESSAGETYPE_0015.equals(uPos.getMessageType()) && ("0101".equals(uPos.getLoyality_type()) && "02".equals(uPos.getCreditCardReading_type()))){
						String nozzle_no = uPos.getNozzle_no();
						String successBP = "1" ;	// 성공:0 실패:1	
						String odtContent = "주민번호로 현금영수증 불가";
						XA_WorkingMessage xaWorkMsg = new XA_WorkingMessage(nozzle_no, successBP, odtContent,"");
						
						// Pump A 로 WorkingMessage 를 전송한다.
		    			Preamble pumpPreamble = PumpMUtil.createWorkingMessagePreamble(null,
		    					SyncManager.DISE_PUMP_ADAPTER, xaWorkMsg , "") ;
		    			LogUtility.getLogger().info("[Pump M] Send WorkingMessage to Pump A");
		    			sendMessage(pumpPreamble) ;
				}else{
					Preamble preamble = PumpMUtil.createUPOSMessagePreamble(messageID,
							SyncManager.DISE_PUMP_ADAPTER,
							SyncManager.DISE_CMS_MODULE, uPos, "");
					
					LogUtility.getLogger().info("[Pump M] Send UPOSMessage to CAT M");
					sendMessage(preamble);
				}
				break;
			}
				/**
				 * 충전기로 부터 보너스 카드 포인트 점수 조회 요청을 수신한다. 응답 전문을 VAN 사 로 부터 받아서 이를
				 * 충전기에 전송한다.
				 */
			case IPumpConstant.COMMANDID_TD: {
				// 충전기- 충전기 보너스 카드의 포인트 점수 조회 요청
				TD_WorkingMessage tdWorkMsg = (TD_WorkingMessage) receiveWorkingMessage;
				LogUtility.getPumpMLogger().info("[Pump M] Receive TD from Recharge. ODTID=" + tdWorkMsg.getNozzleNo());
				tdWorkMsg.print();

				String posReceipt_no = PumpMTransactionManager.getInstance().getKHTransactionID(tdWorkMsg.getConnectNozzleNo());
				UPOSMessage uPos = ODTUtility_Common.createUPOSMessageFromWorkingMessage(tdWorkMsg, posReceipt_no);

				Preamble preamble = PumpMUtil.createUPOSMessagePreamble(messageID,
						SyncManager.DISE_PUMP_ADAPTER,
						SyncManager.DISE_CMS_MODULE, uPos, "");
				
				LogUtility.getPumpMLogger().info("[Pump M] Send UPOSMessage to CAT M");

				sendMessage(preamble);
				break;
			}
				/**
				 * 충전기의 판매완료전문 (SH) 수신한다. 구체적인 내용은 processSH 함수를 참조하기 바란다.
				 */
			case IPumpConstant.COMMANDID_SH: {
				// 충전기- 충전기 ODT 최종 판매 데이터 전송
				SH_WorkingMessage shWorkMsg = (SH_WorkingMessage) receiveWorkingMessage;
				
				LogUtility.getPumpMLogger().info("[Pump M] Receive SH from Recharge. ODTID=" + shWorkMsg.getNozzleNo());
				shWorkMsg.print();

				String khKey = PumpMTransactionManager.getInstance().getKHTransactionID(shWorkMsg.getNozzleNo(), 
						IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED);

				processSH(uniqueKey, shWorkMsg, khKey);
				break;
			}
				/**
				 * 충전기로 부터 보관증 번호 부여 요청을 수신한다. 보관증 번호는 KixxHub 자체내에서 관리하며 Unique
				 * 한 번호를 생성하여 전송한다.
				 */
			case IPumpConstant.COMMANDID_SF: {
				// 충전기- 보관증 번호 부여 요청
				SF_WorkingMessage sfWorkMsg = (SF_WorkingMessage) receiveWorkingMessage;
				
				LogUtility.getPumpMLogger().info("[Pump M] Receive SF from Recharge. ODTID=" + sfWorkMsg.getNozzleNo());
				sfWorkMsg.print();

				processSF(uniqueKey, messageID, sfWorkMsg);

				break;
			}
				/**
				 * 충전기로 부터 보관량 조회 요청 전문 수신이다. 하지만 사용하지 않는 전문이다.
				 */
			case IPumpConstant.COMMANDID_SG: {
				// 충전기- 보관량 조회 요청
				SG_WorkingMessage sgWorkMsg = (SG_WorkingMessage) receiveWorkingMessage;
				
				LogUtility.getPumpMLogger().info("[Pump M] Receive SG from Recharge. ODTID=" + sgWorkMsg.getNozzleNo());
				sgWorkMsg.print();

				POS_DM dmPumpMMsg = new POS_DM(messageID, sgWorkMsg);
				Preamble dmPreamble = PumpMUtil.createPOSMessagePreamble(uniqueKey, SyncManager.DISE_POS_ADAPTER, dmPumpMMsg, "");

				LogUtility.getPumpMLogger().info("[Pump M] Send KeepInfo Req to POS");
				sendMessage(dmPreamble);
				break;
			}
			
			case IPumpConstant.COMMANDID_BI: {
				// 전방POS에 현금 투입금액을 전송.
				BI_WorkingMessage biWorkMsg = (BI_WorkingMessage) receiveWorkingMessage;

				PosSendCashCount(messageID, biWorkMsg, "1");
				break;
			}
			case IPumpConstant.COMMANDID_BC: {
				// 전방POS에 현금 투입 취소금액을 전송.
				// ODT에 거스름돈 발생 영수증 전송
				BC_WorkingMessage bcWorkMsg = (BC_WorkingMessage) receiveWorkingMessage;

				PosSendCashCount(messageID, bcWorkMsg, "0");

				String nozzleNo = bcWorkMsg.getConnectNozzleNo();
				String pumpingPrice = "0";
				String pumpingLiter = "0";
				String pumpingBasePrice = PumpMUtil.getBasePriceFromNozzleNo(nozzleNo);

				LogUtility.getPumpMLogger().debug("ODT에서 취소 메뉴 선택 nozzle=" + nozzleNo);

				UPOSMessage_ItemInfo itemInfo = UPOSUtil.getUPOSMessage_ItemInfo(nozzleNo, "", pumpingLiter,
						pumpingBasePrice, bcWorkMsg.getCashCount());

				UPOSMessage uPosMsg = CreateUPOSMessage.createUPOSMessage_0012(IUPOSConstant.DEVICE_TYPE_3S,
						"", 
						nozzleNo, 
						null, 
						itemInfo, 
						"", 
						null, 
						null,
						"1", 
						bcWorkMsg.getCashCount(), 
						PumpMUtil.getOdtTermId(), 
						"1");
				uPosMsg.print();
				QlWorkingMsg_DasNOFromODT(	uniqueKey, nozzleNo, "", pumpingPrice, pumpingLiter, bcWorkMsg.getCashCount(),
						pumpingBasePrice, uPosMsg);
				break;
			}
			case IPumpConstant.COMMANDID_CL: {
				LogUtility.getPumpMLogger().debug("[Pump M] 취소 전문 : ODT 관련 정보 초기화 ");
				CL_WorkingMessage clWorkMsg = (CL_WorkingMessage) receiveWorkingMessage;

				PumpMODTSaleManager.initSaleContent(clWorkMsg.getNozzleNo());
			}
				/**
				 * 기존 SC 에서는 POS 로 부터 Total Gauage 요청시 전송하는 Protocol 이 존재하였지만,
				 * u-Station 의 KixxHub 는 주유건마다 Total Gaugae 를 첨부해서 POS 로 전송하기
				 * 때문에 아래 전문은 사용하지 않는다.
				 */
			case IPumpConstant.COMMANDID_S5: {
				// 공통 - 토털게이지 전송 Nothing (토털 게이지는 각 주유건마다 보내기 때문에 S5 전문은 사용하지
				// 않는다.)
				break;
			}
			/**
			 * 2019-07-29 SoonKwan
			 * SelfODT Version 정보를 올려준다.
			 */
			case IPumpConstant.COMMANDID_PV:{
				PV_WorkingMessage pvWorkMsg = (PV_WorkingMessage) receiveWorkingMessage;

				LogUtility.getPumpMLogger().info("[Pump M] SelfODT Update Completed. ODTID="	+ pvWorkMsg.getNozzleNo()
						+ "Version = "+pvWorkMsg.getVersion());
				POS_CY cyPumpMMsg = new POS_CY(messageID,pvWorkMsg,"1");
				Preamble cyPreamble = PumpMUtil.createPOSMessagePreamble(uniqueKey, SyncManager.DISE_POS_ADAPTER, cyPumpMMsg, "");

				LogUtility.getPumpMLogger().info("[Pump M] Send SelfODT Version Info. to POS");
				sendMessage(cyPreamble);
				break;
			}
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		} catch (Throwable e1) {
			LogUtility.getPumpMLogger().error(e1.getMessage(), e1);
		}
	}

	/**
	 * 다른 Controller 로 부터 받은 전문을 Pump A 로 전문을 전송한다. 다른 Controller 로 부터 받을 수 있는
	 * 전문은 다음과 같다. DI : 주유 제어 요청 : PosAController DK : 정액/정량 설정 요청 :
	 * PosAController DN : 보관증 발급조회 응답 : PosAController DO : Preset 자료 요청 :
	 * PosAController
	 * 
	 * @param sending_pumpa :
	 *            전송할 전문
	 * 
	 */
	@Override
	protected void onReceivingPumpMData(Object sending_pumpa) {
		if (logSSDC == true)	LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/" + "onSendingPumpA()");
		sendMessage((Preamble) sending_pumpa);
	}

	/**
	 * Pump M 으로부터 Pump A 초기화 요청 수신한다. 초기화 수신을 받으면, Pump M 이 관리하고 있는 테이블을 통해서
	 * Pump A 를 통해서 주유기/ODT 로 환경정보를 전송한다.
	 * 
	 * @param input_pumpa_start :
	 *            START String
	 * 
	 */
	@Override
	protected void onReceivingPumpMInitReq(String input_pumpa_start) {
		if (logSSDC == true)	LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/"+ "onInputPumpAStart()");

		if (input_pumpa_start.equals(IPumpConstant.START)) {
			LogUtility.getPumpMLogger().info("[Pump M] Activate Pump Initialization.");
			startPumpAdapter();
		}
	}

	/**
	 * KixxHub M 로 부터 변경된 Download Flag 를 전송받는다. 이는 상품테이블의 정보가 변경되었다는 의미이며, 이로
	 * 인해서 Pump A 에 단가 초기화 전문을 전송한다.
	 * 
	 * @param input_downloadFlag :
	 *            상품 코드의 Download Flag
	 * 
	 */
	@Override
	protected void onReceivingScMDownloadFlagInfo(String input_downloadFlag) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info(
					"SSDC/" + "PUMP_M/" + "PupmAController/"
							+ "onReceivingDownloadFlag()");

		LogUtility.getPumpMLogger().info(
				"[Pump M] Receive BasePrice change, so notify basePrice to Pump. "
						+ "Download ver=" + input_downloadFlag);
		notifyChangedBasePriceToAllPumpAndODT();
	}

	/**
	 * 현금 투입/취소 시 전방pos에 전송
	 * 
	 * @param inOutGubun :
	 *            투입/취소 구분
	 */
	private void PosSendCashCount(String messageID, WorkingMessage workMsg,
			String inOutGubun) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/"+ "PosSendCashCount()");

		POS_KL klMsg = null;
		if ("1".equals(inOutGubun)) {
			// 투입
			BI_WorkingMessage biWorkMsg = (BI_WorkingMessage) workMsg;
			klMsg = new POS_KL(messageID, biWorkMsg, biWorkMsg.getCash(), inOutGubun, "");
		} else {
			// 취소
			BC_WorkingMessage bcWorkMsg = (BC_WorkingMessage) workMsg;
			klMsg = new POS_KL(messageID, bcWorkMsg, bcWorkMsg.getCashCount(), inOutGubun, "");
		}

		Preamble dePreamble = PumpMUtil.createPOSMessagePreamble(null,	SyncManager.DISE_POS_ADAPTER, klMsg, "");

		dePreamble.setIsAckRequired(Preamble.IS_ACK_REQUIRED_NO);
		sendMessage(dePreamble);
	}

	/**
	 * 현금 고객인 경우에는 투입금액을 pos에 전송한다.
	 */
	private void posSendKK(String messageID, String uniqueKey,
			HE_WorkingMessage heWorkMsg, String khproc_no) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/" + "posSendKK()");
		POS_KK kkPumpM = null;

		kkPumpM = new POS_KK(messageID, heWorkMsg, khproc_no,
				GlobalUtility.getStringValue(PumpMUtil.convertNumberFormatFromPumpToPOS(heWorkMsg.getPrice(), 0)));
		Preamble preamble = PumpMUtil.createPOSMessagePreamble(uniqueKey, SyncManager.DISE_POS_ADAPTER, kkPumpM, "");
		// PumpMSyncManager.setSyncData(uniqueKey,
		// kkPumpM.getMessageID(),SyncManager.DISE_PUMP_MODULE, heWorkMsg) ;
		//					
		LogUtility.getPumpMLogger().info("[Pump M] Request Cust Processing to POS");
		sendMessage(preamble);
	}

	
	/**
	 * 신형 ODT 연동, PI2, 2016-03-28 - CWI
	 * 현금 고객인 경우에는 투입금액을 pos에 전송한다.
	 */
	private void posSendKK2(String messageID, String uniqueKey,
			GA_WorkingMessage gaWorkMsg, String khproc_no) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/" + "posSendKK()");
		POS_KK2 kkPumpM = null;

		kkPumpM = new POS_KK2(messageID, gaWorkMsg, khproc_no,
				GlobalUtility.getStringValue(PumpMUtil.convertNumberFormatFromPumpToPOS(gaWorkMsg.getPrice(), 0)));
		Preamble preamble = PumpMUtil.createPOSMessagePreamble(uniqueKey, SyncManager.DISE_POS_ADAPTER, kkPumpM, "");
		
		LogUtility.getPumpMLogger().info("[Pump M] Request Cust Processing to POS");
		sendMessage(preamble);
	}
	
	
	/**
	 * 2016.06.25 CWI processGA 추가 
	 * GSC Self로 부터 카드 결제 승인 요청을 받아서 처리한다. 승인 요청시 구분하여 처리를 해야 하는 것은
	 * 가득주유인 경우와 아닌 경우이다.
	 * 
	 * @param messageID :
	 *            Pump A 로부터 받은 전문의 messageID
	 * @param heWorkMsg :
	 *            Pump A 로부터 받은 전문
	 * @param khproc_no :
	 *            KH 처리번호
	 */
	private void processGA(String uniqueKey, String messageID, GA_WorkingMessage gaWorkMsg, String khproc_no) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/" + "processGA()");

		sendLockingInfoToPOS(gaWorkMsg.getConnectNozzleNo(), khproc_no,	IConstant.PUMP_SALE_LOCKING);

		// GSCSelf 의 경우 다쓰노 셀프 처럼 가득 주유를 따로 처리할 필요가 없다. GSCSelf는 결제 금액보다 주유금액이 작을 경우
		// 취소 전문이 ODT 로 부터 전송된다.
		// 만약 현금 요청(지폐사용) 인 경우 현금 응답 전문(0012) 를 만든뒤 POS 에 전송하고 HC 전문을 Pump A 로
		// 바로 전송한다.
		String type = gaWorkMsg.getMessageType();
		LogUtility.getPumpMLogger().info("type="+type);
		
		/**
		 * GSC Self 외상거래처 로직 추가 GA_WorkingMsg에서 고객유형 추가 2009.04.28
		 */
		
		// 0171 - 현금투입금액 전송(신규 MessageType)
		if (type.equals(IUPOSConstant.MESSAGETYPE_0171)) { 
			
			LogUtility.getPumpMLogger().info("[T_KH_PUMP_TRHandler] Update for payed info= khproc_no="+ khproc_no);
			try {
				T_KH_PUMP_TRHandler.getHandler().updatePumpPaidInfo_BY_khproc_no(khproc_no, "1");
			} catch (Exception e) {
				LogUtility.getPumpMLogger().error(e.getMessage(), e);
			}

			// 투입 금액을 Upos로 부터 받아 GA전문에 넣어 준다.
			String price = gaWorkMsg.getUnityMessage().getPayment_amt();
			gaWorkMsg.setPrice(price);
			
			/**
			 * 현금 고객인 경우에는 투입금액을 pos에 전송한다.
			 */
			posSendKK2(messageID, uniqueKey, gaWorkMsg, khproc_no);
			
			return;
			
		} else if (type.equals(IUPOSConstant.MESSAGETYPE_0011) || type.equals(IUPOSConstant.MESSAGETYPE_0081)) { 
			
			// 순수현금 또는 순수외상 요청 전문
			String emp_no 			= gaWorkMsg.getUnityMessage().getEmp_no(); 	   // 충전원 ID
			String custCard_No 		= gaWorkMsg.getCustCardNo(); 				   // 거래처 카드번호
			String ss_crStNum 		= gaWorkMsg.getUnityMessage().getSs_crStNum(); // 거래처번호
			String ss_carNum 		= gaWorkMsg.getUnityMessage().getSs_carNum();  // 거래처차량번호
			String term_id 			= gaWorkMsg.getUnityMessage().getTerm_id() ;   // Default
			String led_code 		= ""; // ODT에서 채워서 다시 올려줘야 하기에 ""로 설정
			String lastPayment_yn 	= ""; // 마지막 결제 여부 체크
			String nozzleNo       = gaWorkMsg.getConnectNozzleNo();				  // 노줄번호
			String paymentAmt 	  = gaWorkMsg.getUnityMessage().getPayment_amt(); // 결재 금액
			UPOSMessage_TradeCondition affilate_info2 = null;
			String trDate = GlobalUtility.getDateYYYYMMDDHHMMSS();
			
			// 현금 & 외상 거래 시 생성한 4201로 대체
			UPOSMessage_ItemInfo item_infoCash = gaWorkMsg.getUnityMessage().getItem_info();

			GB_WorkingMessage gbMsg = new GB_WorkingMessage();
			UPOSMessage uPosMsg = null;
			// 순수 현금 결제일 경우
			if(type.equals(IUPOSConstant.MESSAGETYPE_0011)){
				
				uPosMsg = CreateUPOSMessage.createUPOSMessage_0012(	IUPOSConstant.DEVICE_TYPE_3S, 
																						khproc_no,
																						nozzleNo, 
																						emp_no, 
																						item_infoCash,
																						custCard_No, 
																						ss_crStNum, 
																						ss_carNum,
																						lastPayment_yn, 
																						paymentAmt, 
																						term_id,
																						led_code);
				
				gbMsg.setMessageType(IUPOSConstant.MESSAGETYPE_0012);
				
			}else if(type.equals(IUPOSConstant.MESSAGETYPE_0081)){
			// 순수 외상 결제일 경우
				
				uPosMsg = ODTUtility_GSCSelf.createUPOSMessageFromWorkingMessage_GSC_For_OISANG(gaWorkMsg, khproc_no);
				
				gbMsg.setMessageType(IUPOSConstant.MESSAGETYPE_0082);
				
			}
		
			uPosMsg.setVan_Res_Code("0000"); //VAN응답코드
			uPosMsg.setTradeCondition(affilate_info2);
			uPosMsg.setLastPayment_yn("0"); // 마지막 결제 처리
			uPosMsg.setPosReceipt_no(gaWorkMsg.getUnityMessage().getPosReceipt_no()); // 전표번호
			uPosMsg.setTrdate_creditCard(gaWorkMsg.getUnityMessage().getTrdate_creditCard()); // 신용승인일시
			uPosMsg.setTrdate_creditCard(trDate);
			
			gbMsg.setConnectNozzleNo(gaWorkMsg.getConnectNozzleNo());
			gbMsg.setNozzleNo(gaWorkMsg.getNozzleNo());
			gbMsg.setUnityMessage(uPosMsg);
			
			// 순수현금 및 순수외상 고객이 아닌 경우(현금거래처, 신용카드, GS포인트, 현금(보너스 누적), 외상거래처(보너스 누적))
			Preamble pumpPreamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_PUMP_ADAPTER, gbMsg, "") ;
			
			sendMessage(pumpPreamble);
			
		} else {
			
			// PI2, 2016.03.25, CWI
			// ODT 망취소 기능 추가와 관련 망취소 전문에 노줄 번호가 존재 하지 않아 승인 요청전 넘어온 workingMessage의 노줄번호를
			// hash map에 저장한 뒤 망취소 전문의 응답이 올경우 저장해 놓은 노줄번호를 불러 온다.
			// 망취소 전문에 필요로 하는 key값은 unique한 거래고유번호로 지정한다.
			if(gaWorkMsg.getMessageType().equals(IUPOSConstant.MESSAGETYPE_9993)){
				
				GSCSelfPumpingManager.getInstance().createODTNozzleNo(gaWorkMsg.getUnityMessage().getTrx_Proper_No(), gaWorkMsg.getConnectNozzleNo());
			}
		
			// 순수현금 및 순수외상 고객이 아닌 경우(현금거래처, 신용카드, GS포인트, 현금(보너스 누적), 외상거래처(보너스 누적))
			Preamble preamble = PumpMUtil.createUPOSMessagePreamble( messageID,
																	 SyncManager.DISE_PUMP_ADAPTER,
																	 SyncManager.DISE_CMS_MODULE, 
																	 gaWorkMsg.getUnityMessage(), "");
			
			LogUtility.getPumpMLogger().info("[Pump M] Send UPOSMessage (from GSC) to CAT M");
			sendMessage(preamble);
		}
	}

	/**
	 * PI2, 신규 ODT 연동에 따른 업무로직 추가, 2016-03-25 - CWI
	 * GSC Self로 부터 온 결제완료 전문 수행한다.
	 * 
	 * 1) UPOSMessage 존재 여부 (1) 존재하지 않을 경우 (POS 로 부터 Preset 으로 인한 주유로 가정.) a)
	 * Preset 주유금액과 실제 주유금액 일치 여부 (a) 일치할 경우 (b) 일치하지 않을 경우 (2) 존재하지 않을 경우 (ODT
	 * 로 부터 결제 요청에 의한 주유로 가정.) a) Preset 주유금액과 실제 주유금액 일치 여부 (a) 일치할 경우 (b) 일치하지
	 * 않을 경우
	 * 
	 * @param uniqueKey :
	 *            Preamble Object 의 Unique Key
	 * @param messageID :
	 *            주유기로 부터 온 전문 Message ID
	 * @param commandID :
	 *            주유기로 부터 온 전문 CommandID
	 * @param trWorkMsg :
	 *            주유기로 부터 온 판매완료 전문
	 * @param khproc_no :
	 *            KH 처리번호
	 */
	private void processGT(String uniqueKey, String messageID, String commandID, GT_WorkingMessage gtWorkMsg, String khproc_no) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/" + "processTR()");

		try {
			// 2009년 5월 8일 추영대 추가.
			GlobalUtility.printAnalysisLog("pumpM", khproc_no, gtWorkMsg.getNozzleNo(), "", "판매완료", "", 
					new String("LITER : "	+ gtWorkMsg.getLiter() + ", PRICE : " + gtWorkMsg.getPrice()).getBytes(), "", "", "");

		} catch (Exception e) {
			LogUtility.getPumpMLogger().error("!!!!!!!!!!AnalysisLOG 출력 실패!!!!!!!!!!!");
		} // end try

		/**
		 * GSC Self의 경우 결제 내역이 있으면 0001 전문을 POS 로 전송하도록 한다. 만약 없다면 보내지 않는다. (이는
		 * POS 로 부터 Preset 에 의한 주유여부를 조사한다. (lastpayment_yn=1 로 설정)
		 */
		if (PumpMODTSaleManager.shouldSend0001UPOSMessageToSaleM(gtWorkMsg.getConnectNozzleNo(), khproc_no, gtWorkMsg)) {
			UPOSMessage uPosMsg = CreateUPOSMessage.createUPOSMessage_0001(IUPOSConstant.DEVICE_TYPE_3S, khproc_no, "1", "", "");
			Preamble posPreamble = PumpMUtil.createUPOSMessagePreamble( SyncManager.getUniqueKey(), 
																		SyncManager.DISE_PUMP_MODULE,
																		SyncManager.DISE_SALE_MODULE, 
																		uPosMsg, "");
			
			LogUtility.getPumpMLogger().info("[Pump M] Send Self 0001 UPOSMessage to SaleM.");
			sendMessage(posPreamble);
		}

		PumpMODTSaleManager.initSaleContent(gtWorkMsg.getConnectNozzleNo());
	}

	/**
	 * 소모 셀프로 부터 카드 결제 승인 요청을 받아서 처리한다.
	 * 
	 * @param messageID :
	 *            Pump A 로부터 받은 전문의 messageID
	 * @param haWorkMsg :
	 *            Pump A 로부터 받은 전문
	 * @param khproc_no :
	 *            KH 처리번호
	 */
	private void processHA(String messageID, HA_WorkingMessage haWorkMsg, String khproc_no) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/" + "processHA()");

		sendLockingInfoToPOS(haWorkMsg.getConnectNozzleNo(), khproc_no, IConstant.PUMP_SALE_LOCKING);

		// 소모 셀프의 경우 다쓰노 셀프 처럼 가득 주유를 따로 처리할 필요가 없다. 소모 셀프는 결제 금액보다 주유금액이 작을 경우
		// 취소 전문이 ODT 로 부터 전송된다.
		// 만약 현금 요청(지폐사용) 인 경우 현금 응답 전문(0012) 를 만든뒤 POS 에 전송하고 HC 전문을 Pump A 로
		// 바로 전송한다.
		String type = haWorkMsg.getTrType();

		if ("5".equals(type)) {
			/**
			 * [2008.11.20] 참석자 :박동화 부장, 김명수 책임, 나진희 선임 연구원, 정우철 원인 : 소모셀프 ODT 는
			 * 현금을 내뱉는 기능이 없으며, 현금 승인 요청시 KixxHub 와의 통신 실패일 경우 문제가 생길수 있다. 즉
			 * 소모셀프 ODT 는 현금을 받고 주유를 허용하지만, 시재에는 누락될 수 있다. 조치 사항 : 소모 셀프는 현금결제
			 * 수단이 있으며, 현금 결제일 경우에는 현금 요청/응답을 사용하지 않는다. 대신 TR 전문의 결제 수단에 포함되어 있기
			 * 때문에, 이를 이용하여 현금 전문을 생성해서 POS 로 보낸다.
			 */
			LogUtility.getPumpMLogger().info("[Pump M] Create 0012 & HC directly because of cash request.");

			UPOSMessage uPosMsg = ODTUtility_SoMo.create0012UPOSMessageFromCashRequest_SoMo(haWorkMsg, khproc_no);
			// POS로 현금 결제 uPosMsg 를 보내지 않고 받은 현금이 얼마라는 POS 전문을 전송한다.
			/*
			 * uPosMsg.setLastPayment_yn("0") ; Preamble preamble =
			 * PumpMUtil.createPreamble(messageID,
			 * SyncManager.DISE_PUMP_ADAPTER, SyncManager.DISE_SALE_MODULE,
			 * uPos, "") ; LogUtility.getPumpMLogger().info("[Pump M] Send 0012
			 * (Cash) to Sale M.") ; sendMessage(preamble) ;
			 */

			// 2012.06.28 ksm 현금 전문의 경우 uposHash에 저장하지 않고 있음.
			// 소모셀프 세차권 바코드 출력 관련하여 추가함.
			PumpMODTSaleManager.addUPOSMessage(haWorkMsg.getConnectNozzleNo(), uPosMsg);

			POS_KK kkPumpMMsg = new POS_KK(messageID, haWorkMsg, khproc_no);
			Preamble kkPreamble = PumpMUtil.createPOSMessagePreamble(null,	SyncManager.DISE_POS_ADAPTER, kkPumpMMsg, "");
			
			LogUtility.getPumpMLogger().info("[Pump M] Send Input Cash(KK) to Pos A.");
			
			sendMessage(kkPreamble);

			try {
				LogUtility.getPumpMLogger().info("[T_KH_PUMP_TRHandler] Update for payed info. khproc_no=" + khproc_no);
				
				T_KH_PUMP_TRHandler.getHandler().updatePumpPaidInfo_BY_khproc_no(khproc_no, "1");
			} catch (Exception e) {
				LogUtility.getPumpMLogger().error(e.getMessage(), e);
			}

			ArrayList<WorkingMessage> workMsgArray = ODTUtility_SoMo.createWorkingMessageFromUPOSMessage_SoMo(uPosMsg);
			
			if ((workMsgArray != null) && (workMsgArray.size() != 0)) {
				for (int i = 0; i < workMsgArray.size(); i++) {
					Preamble pumpAPreamble = PumpMUtil.createWorkingMessagePreamble(messageID, 
							SyncManager.DISE_PUMP_ADAPTER, 	
							workMsgArray.get(i), 
							"");
					
					LogUtility.getPumpMLogger().info("[Pump M] Respond Cash to Pump A");
					
					sendMessage(pumpAPreamble);
				}
			}
		} else {
			UPOSMessage uPos = null;

			// 유가보조 거래처인 경우 단가할인 적용(국민카드)
			POSHeader posObj = PumpMODTSaleManager.getCustPOSPumpM(haWorkMsg.getConnectNozzleNo());

			POS_DW dwPumpM = null;
			if ((posObj != null) && (posObj instanceof POS_DW)) {
				dwPumpM = (POS_DW) posObj;
			}

			String UnitPrice_after_discount = "0";
			String card_code_base = "";
			String strData = null;

			if (dwPumpM != null) {
				UnitPrice_after_discount = CustUtil.calcBasePrice(haWorkMsg.getBasePrice(), 
						dwPumpM.getCust_cd_item(), 
						haWorkMsg.getConnectNozzleNo(), 
						haWorkMsg.getCardNumber());
				card_code_base = dwPumpM.getCard_code_base();
			}

			// 2012.06.13 ksm 할인단가가 0이면 할인전단가로 셋팅함.
			// 할인단가가 너무 낮으면 장애로 판단하면 어떨지 고민...관련 장애 발생시 적용 고려 필요.
			if (!"0".equals(UnitPrice_after_discount))
				haWorkMsg.setBasePrice(UnitPrice_after_discount);

			// 보너스 누적요청 && 외상거래처 이면 ( 카드기준 01:현금거래처, 02:외상거래처, 03:용역보관, 04:매출보관,
			// 06:미등록카드)
			if (type.equals("3") && card_code_base.equals(ICode.CARD_CODE_BASE_02)) {

				SqlSession session = null;
				try {
					session = SqlSessionFactoryManager.openSqlSession();

					// 외상거래처 보너스 적립여부(0256)
					strData = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(session, IConstant.POSPORTOCOL_CODEMASTER_0256);
				} catch (Exception e) {
					LogUtility.getPumpMLogger().error(e.getMessage(), e);
				} finally {
					SqlSessionFactoryManager.closeSqlSession(session);
				}

				if ("0".equals(strData)) {
					LogUtility.getPumpMLogger().debug("[Pump M] 외상거래처 보너스 적립 거부 됨.");

					// 2012.07.12 ksm 세차바코드 로직 수정
					String barCode = "";
					HC_WorkingMessage hcWorkingMsg = null;
					try {
						String printBarCode = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0440);
						if ("1".equals(printBarCode))
							// twsongkis 2015-01-28 새로운 Barcode 클래스의 barcode로직으로 변경
							barCode = Barcode.getBarcodeNumber("3", haWorkMsg.getPrice(), haWorkMsg.getConnectNozzleNo(), khproc_no, null, null, card_code_base);			// 세차바코드
						
					} catch (Exception e) {
						LogUtility.getPumpMLogger().error(	"[CarWash BarCode] 세차 바코드 출력 여부 조회시 에러 발생");
						LogUtility.getPumpMLogger().error(e.getMessage(), e);
					} finally {
						hcWorkingMsg = new HC_WorkingMessage(haWorkMsg.getNozzleNo(), 
								haWorkMsg.getConnectNozzleNo(),
								type, 
								"2", 
								"", 
								haWorkMsg.getPrice(), 
								"",
								"",
								"",
								// cardCorpNumber ,
								"", "", "", "", "", haWorkMsg.getBonusCard(),
								"", "", "", "0", "0", "0", "0", "보너스 누적 거부",
								"", "외상거래처 보너스 포인트 누적 안함.", 
								haWorkMsg.getBasePrice(),
								// 2012.06.12 ksm 세차바코드 추가됨.
								barCode);
					}

					Preamble preamble = PumpMUtil.createWorkingMessagePreamble(null,	SyncManager.DISE_PUMP_ADAPTER, hcWorkingMsg, "");
					LogUtility.getPumpMLogger().info("[Pump M] Send WorkMsg to Pump A");
					sendMessage(preamble);

					return;
				}
			}
			//이강호 2014-07 법인카드보너스적립제외
			//신용카드 결제시
			if(type.equals("1") ) {
				
				try {
						String binChekCardNo6 = "";
						String binChekCardNo8 = "";
						String track2Check ="";
	
					if (haWorkMsg.getCardNumber() != null && haWorkMsg.getCardNumber().length() > 5){
						binChekCardNo6 = haWorkMsg.getCardNumber().substring(0,6);
						binChekCardNo8 = haWorkMsg.getCardNumber().substring(0,8);
					}
					if(!GlobalUtility.isNullOrEmptyString(haWorkMsg.getCardNumber()) && haWorkMsg.getCardNumber().length() > 36 ){
						track2Check = haWorkMsg.getCardNumber().substring(33, 37);
					}
					
					//롯데 상품코드 && 카드빈
					if("0008".equals(track2Check) && "94092000".equals(binChekCardNo8)){
						LogUtility.getPumpMLogger().debug("[Pump M] LOTTE BIN8=" + binChekCardNo8 + "#LOTTE CODE="+track2Check +
								"#khproc_no="+khproc_no);
						PumpMTransactionManager.getInstance().setCorporate(haWorkMsg.getConnectNozzleNo(), true);
					}
					//농협 카드빈
					else if("943416".equals(binChekCardNo6)||
					   "943417".equals(binChekCardNo6)){
						LogUtility.getPumpMLogger().debug("[Pump M] LOTTE BIN6=" + binChekCardNo6 + "#khproc_no="+khproc_no);
						PumpMTransactionManager.getInstance().setCorporate(haWorkMsg.getConnectNozzleNo(), true);
					}else{
						LogUtility.getPumpMLogger().debug("[Pump M] 법인카드대상아님. "+" khproc_no="+khproc_no);
					}
										
					
				} catch (Exception e) {
					LogUtility.getPumpMLogger().error(e.getMessage(), e);
				}
			}
			//보너스 적립시
			if (type.equals("3") ) {
				//이강호 2014-07 법인카드보너스적립제외
				//롯데카드: 상품코드 체크, 농협: 카드빈 프리픽스 체크
				//ODT거래만 kixxhub에서 처리함. 나머지 VAN서버에서 처리. 
				
				if (PumpMTransactionManager.getInstance().isCorporate(haWorkMsg.getConnectNozzleNo())) {
					LogUtility.getPumpMLogger().debug("[Pump M] 법인카드 보너스 적립 거부 됨."+ " khproc_no="+khproc_no);

					String barCode = "";
					HC_WorkingMessage hcWorkingMsg = null;
					try {
						String printBarCode = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0440);
						if ("1".equals(printBarCode))
							barCode = Barcode.getBarcodeNumber("3", haWorkMsg.getPrice(), haWorkMsg.getConnectNozzleNo(), khproc_no, null, null, null);			// 세차바코드

					} catch (Exception e) {
						LogUtility.getPumpMLogger().error(	"[CarWash BarCode] 세차 바코드 출력 여부 조회시 에러 발생");
						LogUtility.getPumpMLogger().error(e.getMessage(), e);
					} finally {
						hcWorkingMsg = new HC_WorkingMessage(haWorkMsg.getNozzleNo(), 
								haWorkMsg.getConnectNozzleNo(),
								type, 
								"1", 
								"G000000000000000", 
								haWorkMsg.getPrice(), 
								"",
								"",
								"",
								// cardCorpNumber ,
								"", "", "", "", "00000", PumpMUtil.getRealBonusCardNumber(haWorkMsg.getBonusCard()),
								"00000000000000", "         ", "", "0", "0", "0", "0", "보너스누적 거부",
								"", "법인카드 보너스 포인트 누적 안함", 
								haWorkMsg.getBasePrice(),
								// 2012.06.12 ksm 세차바코드 추가됨.
								barCode);
					}

					Preamble preamble = PumpMUtil.createWorkingMessagePreamble(null,	SyncManager.DISE_PUMP_ADAPTER, hcWorkingMsg, "");
					LogUtility.getPumpMLogger().info("[Pump M] Send WorkMsg to Pump A");
					sendMessage(preamble);

					return;
				}
			}
			uPos = ODTUtility_Common.createUPOSMessageFromWorkingMessage(haWorkMsg, khproc_no);
			// PI2 20160330 twlee 장애대응에서 주민번호로 현금영수증 발급 불가로 인해 kixxhub에서 장애대응에 보내지 않고 현금영수증 발급불가 처리
			if(IUPOSConstant.MESSAGETYPE_0015.equals(uPos.getMessageType()) && ("0101".equals(uPos.getLoyality_type()) && "02".equals(uPos.getCreditCardReading_type()))){
					HC_WorkingMessage hcWorkMsg = new HC_WorkingMessage();
					hcWorkMsg.setNozzleNo(haWorkMsg.getNozzleNo());
					hcWorkMsg.setConnectNozzleNo(haWorkMsg	.getConnectNozzleNo());
					hcWorkMsg.setMode("2");
					hcWorkMsg.setTrType("F");
					hcWorkMsg.setVanMsg("주민번호로 현금영수증 불가");
					Preamble preamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_PUMP_ADAPTER, hcWorkMsg, "");
					LogUtility.getLogger().info("[Pump M] Send WorkMsg to Pump A");
					sendMessage(preamble);
			}else{
				Preamble preamble = PumpMUtil.createUPOSMessagePreamble(messageID,	
																SyncManager.DISE_PUMP_ADAPTER, 
																SyncManager.DISE_CMS_MODULE,
																uPos, "");
				LogUtility.getLogger().info("[Pump M] Request payment Info to CAT M. khproc_no = " 	+ khproc_no);
				sendMessage(preamble);
			}
		}
	}

	/**
	 * 소모셀프로부터 온 외상거래 승인 요청을 받아서 수행한다. 요청 전문의 내용은 card Type 에 따라서 다음과 같다. Card
	 * Type 5 : 외상고객정보 요청 (응답은 PG전문) 6 : 신용카드 분실,도난,정지 체크 이 요청 전문에 대한 응답은, POS
	 * 동작시 POS 에게 질의(DV전문)하여 그 응답 전문(DW전문)을 받아서 Pump A 에게 전송하고, POS 가 동작하지 않을 경우
	 * KixxHub 자체 테이블의 정보를 이용하여 Pump A 에게 전송한다.
	 * 
	 * @param uniqueKey :
	 *            Preamble Object 의 Unique Key
	 * @param receiving_pumpa :
	 *            주유기로 부터 온 외상 거래처 요청 전문
	 * @param messageID :
	 *            주유기로 부터 온 전문 Message ID
	 * @param hbWorkMsg :
	 *            주유기로 부터 온 전문
	 * @param khproc_no :
	 *            KH 처리번호
	 */
	private void processHB(String uniqueKey, Object receiving_pumpa, String messageID, HB_WorkingMessage hbWorkMsg, String khproc_no) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/" + "processHB()");

		try {
			String preset_basePrice = "0";// PumpMathUtil.convertBasePriceFromPumpToPOS(hbWorkMsg.getBasePrice())
											// ;

			// 거래처 정보 요청시 단가는 0 으로 요청한다. -- 박종호
			// ksm 2012.06.13 초기화시로 셋팅으로 수정함. ㅡ.,ㅡ
			// preset_basePrice = "0";

			POS_DV dvPumpM = null;
			String cust_card_no = hbWorkMsg.getCardNumber(); // 거래처 카드번호
			String nozzle_no = hbWorkMsg.getConnectNozzleNo(); // 노즐 번호
			String goods_code = T_NZ_NOZZLEHandler.getHandler().getGoods_codeBYNozzle_no(nozzle_no); // 상품 코드

			dvPumpM = new POS_DV(messageID, nozzle_no, khproc_no, cust_card_no, goods_code, preset_basePrice);

			Preamble preamble = PumpMUtil.createPOSMessagePreamble(uniqueKey, SyncManager.DISE_POS_ADAPTER, dvPumpM, "");

			PumpMSyncManager.setSyncData(uniqueKey, dvPumpM.getMessageID(), SyncManager.DISE_PUMP_MODULE, receiving_pumpa);

			LogUtility.getPumpMLogger().info("[Pump M] Request Cust Processing to POS");

			sendMessage(preamble);
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		}
	}

	/**
	 * 다쓰노 셀프로 부터 카드 결제 승인 요청을 받아서 처리한다. 승인 요청시 구분하여 처리를 해야 하는 것은 가득주유인 경우와 아닌
	 * 경우이다.
	 * 
	 * @param messageID :
	 *            Pump A 로부터 받은 전문의 messageID
	 * @param heWorkMsg :
	 *            Pump A 로부터 받은 전문
	 * @param khproc_no :
	 *            KH 처리번호
	 */
	private void processHE(String uniqueKey, String messageID, HE_WorkingMessage heWorkMsg, String khproc_no) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/" + "processHE()");

		sendLockingInfoToPOS(heWorkMsg.getConnectNozzleNo(), khproc_no,	IConstant.PUMP_SALE_LOCKING);

		UPOSMessage uPos = null;
		
		/**
		 * 2016.06.28 WooChul Jung. Bug Fix.
		 * 	기존 코드의 경우, 현금+현금거래처 인데, 0원 주유할 경우 DG 전문을 POS 에 전송안됨. 그 이유는 현금+현금거래처인 경우
		 * 	paid_ind 를 1 로 설정 하지 않음.
		 * 	따라서 현금으로 결제된 건이라고 하면, paid_ind 를 무조건 1 로 변경함.
		 */
		if (!GlobalUtility.isNullOrEmptyString(heWorkMsg.getCashCount())){
			LogUtility.getLogger().info("[T_KH_PUMP_TRHandler] Update for payed info= khproc_no=" + khproc_no);
			try {
				T_KH_PUMP_TRHandler.getHandler().updatePumpPaidInfo_BY_khproc_no(khproc_no, "1");
			} catch (Exception e) {
				LogUtility.getLogger().error(e.getMessage(), e);
			}
		}

		/**
		 * 다쓰노 셀프 외상거래처 로직 추가 HE_WorkingMsg에서 고객유형 추가 2009.04.28 edited by
		 * ykjang
		 */

		if (!GlobalUtility.isNullOrEmptyString(heWorkMsg.getCashCount())	&& GlobalUtility.isNullOrEmptyString(heWorkMsg.getCustCardNo())) {

			// 현금 고객인 경우
			QM_WoringMessage qmWorkMsg = new QM_WoringMessage();

			qmWorkMsg.setNozzleNo(heWorkMsg.getNozzleNo());
			qmWorkMsg.setConnectNozzleNo(heWorkMsg.getConnectNozzleNo());
			qmWorkMsg.setMode("1");
			qmWorkMsg.setBasePrice(PumpMUtil.convertNumberFormatFromPOSToPump(
					PumpMUtil.getBasePriceFromNozzleNo(heWorkMsg.getConnectNozzleNo()), 4, 2)); // 적용단가
			qmWorkMsg.setPrice(heWorkMsg.getPrice());	// 승인금액
			qmWorkMsg.setCertiInfo(khproc_no); 				// 승인번호
			qmWorkMsg.setCertiTime(GlobalUtility.getDateYYYYMMDDHHMMSS()); // 승인시간

			Preamble preamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_PUMP_ADAPTER, qmWorkMsg, "");
			LogUtility.getPumpMLogger().info("[Pump M] Send WorkMsg to Pump A");

			DasNoSelfPumpingManager.getInstance().setWorkingMessage(heWorkMsg.getConnectNozzleNo(), heWorkMsg);
			// 현금 고객인 경우 fullpumping option을 카드번호 확인 후 주유완료 후 결제로 변경한다.

			sendMessage(preamble);

			/*LogUtility.getPumpMLogger().info("[T_KH_PUMP_TRHandler] Update for payed info= khproc_no=" + khproc_no);
			try {
				T_KH_PUMP_TRHandler.getHandler().updatePumpPaidInfo_BY_khproc_no(khproc_no, "1");
			} catch (Exception e) {
				LogUtility.getPumpMLogger().error(e.getMessage(), e);
			}*/

			/**
			 * 현금 고객인 경우에는 투입금액을 pos에 전송한다.
			 */
			posSendKK(messageID, uniqueKey, heWorkMsg, khproc_no);

			return;
		} else {
			// 현금 고객이 아닌 경우
			if (IPumpConstant.CB_CUSTOMER_TYPE_CUS_CASH.equals(heWorkMsg.getCustomerType())
					|| IPumpConstant.CB_CUSTOMER_TYPE_GENERAL.equals(heWorkMsg.getCustomerType())
					// 2012.09.21 ksm 신형다쓰노인경우 ""으로 넘김.
					|| "".equals(heWorkMsg.getCustomerType())) {
				
				//이강호 2014-07 법인카드보너스적립제외
				//롯데카드: 상품코드 체크, 농협: 카드빈 프리픽스 체크
				//ODT거래만 kixxhub에서 처리함. 나머지 VAN서버에서 처리 
				try {
					String binChekCardNo6 = "";
					String binChekCardNo8 = "";
					String track2Check ="";
					String BounsCardNo ="";

					if (heWorkMsg.getCardNumber() != null && heWorkMsg.getCardNumber().length() > 5){
						binChekCardNo6 = heWorkMsg.getCardNumber().substring(0,6);
						binChekCardNo8 = heWorkMsg.getCardNumber().substring(0,8);
					}
					if(!GlobalUtility.isNullOrEmptyString(heWorkMsg.getCardNumber()) && heWorkMsg.getCardNumber().length() > 36 ){
						track2Check = heWorkMsg.getCardNumber().substring(33, 37);
					}
					if(!GlobalUtility.isNullOrEmptyString(heWorkMsg.getBonusCard()) && heWorkMsg.getBonusCard().length() >= 16){
						BounsCardNo = heWorkMsg.getBonusCard().substring(0,16);
					}
					
					//롯데 상품코드 && 카드빈
					if("0008".equals(track2Check) && "94092000".equals(binChekCardNo8)){
						LogUtility.getPumpMLogger().debug("[Pump M] LOTTE BIN8=" + binChekCardNo8 + 
								"#LOTTE CODE" + track2Check + "#BounsCardNo=" + BounsCardNo + "#khproc_no="+khproc_no);
						
						PumpMTransactionManager.getInstance().setCorporate(heWorkMsg.getConnectNozzleNo(), true);
						PumpMTransactionManager.getInstance().setCorporateBonus(heWorkMsg.getConnectNozzleNo(), BounsCardNo);
					}
					//농협 카드빈
					else if("943416".equals(binChekCardNo6)|| "943417".equals(binChekCardNo6)){
						LogUtility.getPumpMLogger().debug("[Pump M] NH BIN6=" + binChekCardNo6 + "#BounsCardNo="+khproc_no +
								"#khproc_no="+khproc_no);
						
						PumpMTransactionManager.getInstance().setCorporate(heWorkMsg.getConnectNozzleNo(), true);
						PumpMTransactionManager.getInstance().setCorporateBonus(heWorkMsg.getConnectNozzleNo(), BounsCardNo);
					}else{
						LogUtility.getPumpMLogger().debug("[Pump M] 법인카드대상아님. "+" khproc_no="+khproc_no);
					}
									
				
				} catch (Exception e) {
					LogUtility.getPumpMLogger().error(e.getMessage(), e);
				}
			
				////이강호 2014-07 법인카드보너스적립제외
				//법인카드 결제인 경우 HE 전문(다쓰노)에서 보너스카드번호를 제거 후, 보너스카드 리딩안한 것처럼 이후 프로세스 진행.
				if( PumpMTransactionManager.getInstance().isCorporate(heWorkMsg.getConnectNozzleNo())){
					heWorkMsg.setBonusCard("");
					LogUtility.getPumpMLogger().debug("[Pump M] BonusCard Null 처리 완료."+ " khproc_no="+khproc_no);
					
				}
				
				// 현금거래처 or 일반 신용카드 거래
				POSHeader posMsg = PumpMODTSaleManager.getCustPOSPumpM(heWorkMsg.getConnectNozzleNo());
				WorkingMessage workMsg = PumpMODTSaleManager.getCustPumpAPumpM(heWorkMsg.getConnectNozzleNo());
				POS_DW dwPumpM = null;
				boolean isDiscountCargo = false;

				if (IPumpConstant.CB_CUSTOMER_TYPE_CUS_CASH.equals(heWorkMsg.getCustomerType())) {
					// 현금거래처인 경우 단가를 변경시킨다.

					if ((posMsg != null) && (posMsg instanceof POS_DW)) {
						dwPumpM = (POS_DW) posMsg;
						LogUtility.getPumpMLogger().info(dwPumpM.toString());

						// 거래처 유형 (05: 영업화물) && 상품중분류 경유
						if (ICode.CUST_CD_ITEM_05.equals(dwPumpM.getCust_cd_item())	
								&& "12".equals(dwPumpM.getGoods_code().substring(0, 2)))
							isDiscountCargo = CustUtil.isDiscountCargo(heWorkMsg.getBonusCard(), heWorkMsg.getCardNumber());

						if (isDiscountCargo)
							if (Double.parseDouble(PumpMUtil.convertNumberFormatFromPOSToPump(dwPumpM.getBasePrice(), 4, 2)) != 0)
								heWorkMsg.setBasePrice(PumpMUtil.convertNumberFormatFromPOSToPump(dwPumpM.getBasePrice(), 4, 2));

						// 현금거래처이면서 버스운수사, 영업화물, 유가보조, 운수사, 개인택시가 아닌 거래처는 PL단가할인
						// 적용
						if (!(ICode.CUST_CD_ITEM_04.equals(dwPumpM.getCust_cd_item())
								|| ICode.CUST_CD_ITEM_05.equals(dwPumpM.getCust_cd_item())
								|| ICode.CUST_CD_ITEM_26.equals(dwPumpM.getCust_cd_item())
								|| ICode.CUST_CD_ITEM_51.equals(dwPumpM.getCust_cd_item()) 
								|| ICode.CUST_CD_ITEM_52.equals(dwPumpM.getCust_cd_item()))
								&& ICode.CARD_CODE_BASE_01.equals(dwPumpM.getCard_code_base())) {
							
							heWorkMsg.setBasePrice(PumpMUtil.convertNumberFormatFromPOSToPump(dwPumpM.getBasePrice(), 4, 2));
						}

						String UnitPrice_after_discount = CustUtil.calcBasePrice(heWorkMsg.getBasePrice(),
								dwPumpM.getCust_cd_item(), 
								heWorkMsg.getConnectNozzleNo(),
								heWorkMsg.getCardNumber());

						if (!"0".equals(UnitPrice_after_discount))
							heWorkMsg.setBasePrice(UnitPrice_after_discount);
					}

					// 요청 전문을 저장한다. 이는 차후 가득 주유로 인한 선취소, 재승인을 위해서 사용되어 진다.
					DasNoSelfPumpingManager.getInstance().setWorkingMessage(heWorkMsg.getConnectNozzleNo(), heWorkMsg);

					if (!GlobalUtility.isNullOrEmptyString(heWorkMsg.getCashCount())) {
						// 현금 거래처가 현금으로 결제하는 경우
						QM_WoringMessage qmWorkMsg = new QM_WoringMessage();

						qmWorkMsg.setNozzleNo(heWorkMsg.getNozzleNo());
						qmWorkMsg.setConnectNozzleNo(heWorkMsg.getConnectNozzleNo());
						qmWorkMsg.setMode("1");
						qmWorkMsg.setBasePrice(heWorkMsg.getBasePrice()); // 적용단가						
						qmWorkMsg.setLiter(heWorkMsg.getLiter()); // 승인리터
						qmWorkMsg.setPrice(heWorkMsg.getPrice()); // 승인금액
						
						//	2012.10.10 ksm 신형다쓰노 현금거래처 처리시 리터승인 문제
						// 리터값이 들어오면 ODT에서 리터를 선택했다는 것임.
						// 리터값이 있을 경우 거래처단가로 계산하여  
						// 1. "(리터*거래단가) >= 투입금액" 이면 투입금액으로 승인
						// 2. "(리터*거래단가) < 투입금액" 이면 리터로 승인
						if(!"0".equals(PumpMUtil.convertNumberFormatFromUPOSToStandardFormat(heWorkMsg.getLiter()))){
							//1. 리터와 거래단가로 주유금액 구함.
							String calcAmt = GlobalUtility.multiple(PumpMUtil.convertLiterFromPumpToPOS(heWorkMsg.getLiter()), 
									PumpMUtil.convertBasePriceFromPumpToPOS(heWorkMsg.getBasePrice()));
							
							// 2. 주유금액과 투입금액 비교
							if ( (int)Double.parseDouble(calcAmt) >= Integer.parseInt(heWorkMsg.getCashCount()) ){
								qmWorkMsg.setLiter("0000000"); 									// 승인리터
								qmWorkMsg.setPrice(heWorkMsg.getCashCount()); 	// 승인금액(투입금액)
								heWorkMsg.setPrice(heWorkMsg.getCashCount());	// 거스름돈 처리를 위해 승인금액에 투입금액을 셋팅.
							}else{
								heWorkMsg.setPrice(heWorkMsg.getCashCount());	// 거스름돈 처리를 위해 승인금액에 투입금액을 셋팅.
							}
						}
						
						qmWorkMsg.setCertiInfo(khproc_no); // 승인번호
						qmWorkMsg.setCertiTime(GlobalUtility.getDateYYYYMMDDHHMMSS()); // 승인시간

						Preamble preamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_PUMP_ADAPTER, qmWorkMsg, "");
						LogUtility.getPumpMLogger().info("[Pump M] Send WorkMsg to Pump A");
						sendMessage(preamble);

						/**
						 * 현금 고객인 경우에는 투입금액을 pos에 전송한다.
						 */
						posSendKK(messageID, uniqueKey, heWorkMsg, khproc_no);

						return;
					}
				}

				if (DasNoSelfPumpingManager.getInstance().isFullPumping(heWorkMsg.getConnectNozzleNo(),
						GlobalUtility.getStringValue(heWorkMsg.getPrice()))) {
					// 가득 주유인 경우
					// 요청 전문을 저장한다. 이는 차후 가득 주유로 인한 선취소, 재승인을 위해서 사용되어 진다.
					DasNoSelfPumpingManager.getInstance().setWorkingMessage( heWorkMsg.getConnectNozzleNo(), heWorkMsg);
					uPos = DasNoSelfPumpingManager.getInstance().getUPOSMessageBeforePumping(heWorkMsg, khproc_no);
					/**
					 * 2016.03.31 WooChul Jung.
					 * 	move filler1 to selfpayment_type
					 */
					uPos.setSelfPayment_type(IConstant.UPOSMESSAGE_PAID_TYPE_20);
				} else {
					// 가득 주유가 아닌 경우
					// 요청 전문을 저장한다. 이는 차후 재승인 최소 금액보다 클 경우 선취소, 재승인을 위해서 사용되어
					// 진다.
					DasNoSelfPumpingManager.getInstance().setWorkingMessage(heWorkMsg.getConnectNozzleNo(), heWorkMsg);

					uPos = ODTUtility_Common.createUPOSMessageFromWorkingMessage(heWorkMsg,	khproc_no);
					/**
					 * 2016.03.31 WooChul Jung.
					 * 	move filler1 to selfpayment_type
					 */
					uPos.setSelfPayment_type(IConstant.UPOSMESSAGE_PAID_TYPE_10);}

				if (isDiscountCargo)
					uPos.setFiller2("U");

				// 현금 거래처인 경우
				// DasNoSelfPumpingManager.getInstance().isFullPumping 이 method를
				// 통해
				// PumpMODTSaleManager 이 class를 초기화하기 때문에 다시 넣어 준다.
				if (dwPumpM != null)
					PumpMODTSaleManager.setCustInfo(heWorkMsg.getConnectNozzleNo(), workMsg, dwPumpM);

				//uPos.print();

				Preamble preamble = PumpMUtil.createUPOSMessagePreamble(messageID,
						SyncManager.DISE_PUMP_ADAPTER,
						SyncManager.DISE_CMS_MODULE, 
						uPos, "");
				LogUtility.getPumpMLogger().info("[Pump M] Send UPOSMessage (from DaSNo) to CAT M");
				sendMessage(preamble);

			} else if (IPumpConstant.CB_CUSTOMER_TYPE_CUS_OISANG.equals(heWorkMsg.getCustomerType())
					|| IPumpConstant.CB_CUSTOMER_TYPE_CUS_FIX_AMT.equals(heWorkMsg.getCustomerType())) {
				// 외상인 경우 또는 정량 고객인 경우
				// 정량 거래처는 셀프에서 처리하지 않음. 2009.04.29 by 편윤국
				QM_WoringMessage qmWorkMsg = new QM_WoringMessage();

				qmWorkMsg.setNozzleNo(heWorkMsg.getNozzleNo());
				qmWorkMsg.setConnectNozzleNo(heWorkMsg.getConnectNozzleNo());

				// 외상거래처는 기존에 받은 전문(DW)을 통해 한도를 체크한다.
				POSHeader posMsg = PumpMODTSaleManager.getCustPOSPumpM(heWorkMsg.getConnectNozzleNo());
				POS_DW dwPumpM = null;
				
				if ((posMsg != null) && (posMsg instanceof POS_DW))
					dwPumpM = (POS_DW) posMsg;

				qmWorkMsg.setBasePrice(PumpMUtil.convertNumberFormatFromPOSToPump(dwPumpM.getBasePrice(), 4, 2)); // 판매단가

				// tatsuno_hs okdhp7 (2013.02) -> 다쓰노셀프(현성) 외상처리 개선관련 
				qmWorkMsg.setDriveName(dwPumpM.getDrive_name());
				qmWorkMsg.setCarNo(dwPumpM.getCar_no());
				qmWorkMsg.setCardAdjInd(dwPumpM.getCardadj_ind());
				qmWorkMsg.setLimitBase(dwPumpM.getAdjbase_code_limit());
				qmWorkMsg.setLimit(dwPumpM.getLimit());
				qmWorkMsg.setAccLimit(dwPumpM.getAccLimit());
				
				
				double remainAmount = 0;
				String errMessage = "";
				remainAmount = Double.parseDouble(dwPumpM.getLimit()) - Double.parseDouble(dwPumpM.getAccLimit());

				LogUtility.getPumpMLogger().debug("[Pump M] remainAmount=" + remainAmount + 
						"#dwPumpM.getAdjbase_code_limit()="	+ dwPumpM.getAdjbase_code_limit());

				String goodsCode = "";
				
				try {
					goodsCode = T_NZ_NOZZLEHandler.getHandler().getGoods_codeBYNozzle_no(heWorkMsg.getConnectNozzleNo());
				} catch (Exception e) {
					LogUtility.getPumpMLogger().error(e.getMessage(), e);
				}

				if (goodsCode.equals(dwPumpM.getGoods_code())) {
					if (ICode.CARDADJ_IND_01.equals(dwPumpM.getCardadj_ind())) {
						// 카드 적용 구분이 무제한인 경우
						qmWorkMsg.setMode("1");
					} else {
						Double accPrice = Double.parseDouble(heWorkMsg.getPrice());
						Double accLiter = GlobalUtility.getValueByCertainDecimal(	
								accPrice / Double.parseDouble(PumpMUtil.convertBasePriceFromPumpToPOS(heWorkMsg.getBasePrice())),3);

						if (ICode.ADJBASE_CODE_LIMIT_01.equals(dwPumpM.getAdjbase_code_limit())) {
							// 한도적용기준 = 수량
							// 잔량 = 한도잔량 - 누적사용량

							LogUtility.getPumpMLogger().debug("PumpMathUtil.convertLiterFromPumpToPOS(heWorkMsg.getLiter()) : "
									+ PumpMUtil.convertLiterFromPumpToPOS(heWorkMsg.getLiter()));

							if (CustUtil.isCustRecipt(remainAmount, accLiter, dwPumpM.getRentlimit_proc_ind_overlimit()))
								qmWorkMsg.setMode("1");
							else {
								qmWorkMsg.setMode("2");
								errMessage = "한도 초과";
							}

						} else {
							// 한도적용기준 = 금액
							// 한도적용 기준이 금액인 경우 ODT에서 선택된 금액에 거래단가를 나눈 수량을 한도잔량과
							// 비교하여 거래승인,거부를 판단한다.
							double setLitter = Double.parseDouble(heWorkMsg.getPrice()) / Double.parseDouble(dwPumpM.getBasePrice());

							LogUtility.getPumpMLogger().debug("[Pump M] setLitter=" + setLitter);
							
							remainAmount = remainAmount / Double.parseDouble(dwPumpM.getBasePrice());

							if (CustUtil.isCustRecipt(remainAmount, setLitter,dwPumpM.getRentlimit_proc_ind_overlimit()))
								qmWorkMsg.setMode("1");
							else {
								qmWorkMsg.setMode("2");
								errMessage = "한도 초과";
							}
						}
					}
				} else {
					// 상품코드가 틀릴 경우 승인 실패
					qmWorkMsg.setMode("2");
					errMessage = "상품코드 불일치";
				}

				// 단가가 없는 경우는 pl이 있더라도 차량별 유종에 선택된 유종이 일치하지 않는 경우 단가가 0으로 내려온다.
				if ("0".equals(dwPumpM.getBasePrice()))
					qmWorkMsg.setMode("2");

				DasNoSelfPumpingManager.getInstance().setWorkingMessage( heWorkMsg.getConnectNozzleNo(), heWorkMsg);

				qmWorkMsg.setLiter(heWorkMsg.getLiter()); // 승인리터
				qmWorkMsg.setPrice(heWorkMsg.getPrice()); // 승인금액
				qmWorkMsg.setCertiInfo(khproc_no); // 승인번호
				qmWorkMsg.setCertiTime(GlobalUtility.getDateYYYYMMDDHHMMSS()); // 승인시간

				Preamble preamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_PUMP_ADAPTER, qmWorkMsg, "");

				LogUtility.getPumpMLogger().info("[Pump M] Send WorkMsg to Pump A");
				sendMessage(preamble);

				if ("2".equals(qmWorkMsg.getMode())) {

					String receipt;
					if (PumpMUtil.getConnectedODTProtocolFromNozzleNo(heWorkMsg.getConnectNozzleNo()) == 90) {
						
						receipt = ODTUtility_GSC_Self.createErrorPrintFormat(6, 
								heWorkMsg.getConnectNozzleNo(), 
								dwPumpM.getCust_card_no(), 
								"", 
								errMessage,
								"0", 
								GlobalUtility.getDateYYYYMMDDHHMMSS());
					} else {
						receipt = PumpMessageFormat.createErrorPrintFormat(	6,
								0, 
								heWorkMsg.getConnectNozzleNo(), 
								dwPumpM.getCust_card_no(), 
								"", 
								"", 
								"", 
								"", 
								"",
								errMessage, 
								"", 
								"0", 
								GlobalUtility.getDateYYYYMMDDHHMMSS());
					}
					// 거래처 승인 실패시 거래처 정보 clear
					PumpMODTSaleManager.initSaleContent(heWorkMsg.getConnectNozzleNo());

					QL_WorkingMessage qlWrkMsg = new QL_WorkingMessage(heWorkMsg.getNozzleNo(), 
							heWorkMsg.getConnectNozzleNo(), 
							String.valueOf(receipt.length()), 
							receipt, 
							"0",
							"", 
							"");

					preamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey, SyncManager.DISE_PUMP_ADAPTER, qlWrkMsg, "");
					// 실패 전문 전송
					sendMessage(preamble);
					qmWorkMsg = null;
					dwPumpM = null;
				}
			}
		}
	}

	/**
	 * 주유기로 부터 Preset 정보를 받아서 다음을 수행한다. 1. T_KH_PUMP_TR Table update 2. POS 에
	 * Preset 정보 전송
	 * 
	 * @param uniqueKey :
	 *            Preamble Object 의 Unique Key
	 * @param messageID :
	 *            주유기로 부터 온 전문 Message ID
	 * @param hfWorkMsg :
	 *            주유기로 부터 온 전문
	 */
	private void processHF(String uniqueKey, String messageID, HF_WorkingMessage hfWorkMsg) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/" + "processHF()");

		Object preamble = PumpMSyncManager.getPreambleAndRemove(messageID);
		String khproc_no = null;
		String commandID = null;

		String type = hfWorkMsg.getType() ;
		String price = "0" ;
		String liter = "0" ;
		if (type.equals("0")) {
			price = PumpMUtil.convertPriceFromPumpToPOS(hfWorkMsg.getPrice()) ;
			liter = "0" ;
		} else if (type.equals("1")) {
			price = "0" ;
			liter = PumpMUtil.convertLiterFromPumpToPOS(hfWorkMsg.getLiter()) ;	
		} else {
			price = "0" ;
			liter = "0" ;
		}
		String base_price = PumpMUtil.convertBasePriceFromPumpToPOS(hfWorkMsg.getBasePrice()) ;
		
		POS_DQ dqPumpMMsg = new POS_DQ(messageID, hfWorkMsg, khproc_no, price, liter, base_price);

		if (preamble != null) {
			LogUtility.getPumpMLogger().warn("[Pump M] This Log shouldn't happen");
			khproc_no = PumpMTransactionManager.getInstance().getKHTransactionID(hfWorkMsg.getNozzleNo());
			commandID = IConstant.POSPROTOCOL_COMMANDID_DP;
		}

		if (khproc_no == null) {
			khproc_no = PumpMTransactionManager.getInstance().getKHTransactionID(hfWorkMsg.getNozzleNo(), IPumpConstant.KH_PUMP_PRESET);
			commandID = IConstant.POSPROTOCOL_COMMANDID_DQ;
		}

		dqPumpMMsg.setTransactionID(khproc_no);
		dqPumpMMsg.setCommandID(commandID);

		String preset_qty_prc_ind = dqPumpMMsg.getType();
		String preset_qty = dqPumpMMsg.getLiter();
		String preset_prc = dqPumpMMsg.getPrice();
		String preset_basePrice = dqPumpMMsg.getBase_price();
		try {
			T_KH_PUMP_TRHandler.getHandler().updatePresetInfo_BY_khproc_no(khproc_no, 
					preset_qty_prc_ind, 
					preset_qty, 
					preset_prc,
					preset_basePrice);
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		}
		Preamble dqPreamble = PumpMUtil.createPOSMessagePreamble(null,	SyncManager.DISE_POS_ADAPTER, dqPumpMMsg, "");

		try {
			// 2009년 4월 21일 추영대 추가.
			GlobalUtility.printAnalysisLog("pumpM", khproc_no, hfWorkMsg.getNozzleNo(), "", "프리셋", "", dqPumpMMsg.convertPOSContent(), "", "", "");

		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(	"[Pump M] !!!!!!!!!!AnalysisLOG 출력 실패!!!!!!!!!!!", e);
		} // end try

		sendMessage(dqPreamble);
	}

	/**
	 * 충전기로 부터 온 비정상ODT리셋 시 주유 정보 처리를 한다.
	 * 
	 * @param uniqueKey
	 * @param messageID
	 * @param s4WorkMsg
	 * @param khproc_no
	 */
	private void processKJ(String uniqueKey, String messageID,
			S4_WorkingMessage s4WorkMsg, String khproc_no) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/" + "processKJ()");

		int nozzle_noInt = Integer.parseInt(s4WorkMsg.getNozzleNo());
		String price = s4WorkMsg.getPrice();
		String resetBasePrice = Integer.toString((int) Double.parseDouble(PumpMUtil.getBasePriceFromNozzleNo(s4WorkMsg.getNozzleNo())) * 100);
		s4WorkMsg.setBasePrice(resetBasePrice);

		try {
			String liter = PumpMUtil.convertNumberFormatFromPumpToPOS(	s4WorkMsg.getLiter(), 3, 8);

			T_KH_PUMP_TRData pumpTRData = T_KH_PUMP_TRHandler.getHandler().getT_KH_PUMP_TRData(khproc_no);
			POS_KJ kjPumpMMsg = new POS_KJ(	messageID, 
					s4WorkMsg, 
					khproc_no, 
					pumpTRData.getGoods_code(), 
					pumpTRData.getStart_eqpm_qty(), 
					PumpMUtil.convertTotalLiterFromPumpTOPOS(s4WorkMsg.getTotalGauge()),
					pumpTRData.getOil_start_datetime_to(), 
					price, 
					liter,
					pumpTRData.getBizhour_date(),
					PumpMUtil.getConvertBasePrice(s4WorkMsg.getNozzleNo(), s4WorkMsg.getBasePrice()));

			String eqpm_qty = kjPumpMMsg.getLiter();
			String eqpm_amt_prc = kjPumpMMsg.getPrice();
			String oil_datetime_to = kjPumpMMsg.getSystem_time();
			String end_eqpm_qty = kjPumpMMsg.getTotal_gauge();

			T_KH_PUMP_TRHandler.getHandler().updatePumpCompletedInfo_BY_khproc_no(khproc_no, 
					eqpm_qty,
					eqpm_amt_prc, 
					oil_datetime_to, 
					end_eqpm_qty);
			PumpMPriceManager.setTotalGauage(nozzle_noInt, end_eqpm_qty);

			Preamble preamble = PumpMUtil.createPOSMessagePreamble(null,	SyncManager.DISE_POS_ADAPTER, kjPumpMMsg, "");
			LogUtility.getPumpMLogger().info("[Pump M] Send KJ Content to POS A nozzle="	+ s4WorkMsg.getNozzleNo());
			sendMessage(preamble);

			PumpMPriceManager.initPumpPrice(nozzle_noInt);
						
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		}
	}

	// 2012.06.07 ksm 소모셀프 세차바코드 POS전송
	// OPT를 위해 세차 바코드를 DB에 저장했으나 임시로 사용하는 세차권이므로 DB저장 제외함.
	// 2016. 4. 14. 오후 15:48:31, PI2, twsongkis 새로운 바코드 로직 수행 / 세차권 DB 저장
	private void processOPTBarcode(String carWashBarCode, String nozzleNo, String khproc_no) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/"+ "processOPTBarcode()");

		if (!"".equals(carWashBarCode)) {
			String getMessageID = GlobalUtility.getUniqueMessageID();
			
			try {
				POS_EN enPumpMMsg = new POS_EN(getMessageID, nozzleNo, khproc_no, carWashBarCode, 
						T_KH_STOREHandler.getHandler().getWorkingDate());
	
				Preamble preambleM = PumpMUtil.createPOSMessagePreamble(GlobalUtility.getUniqueMessageID(), 
						SyncManager.DISE_SALE_MODULE, 
						enPumpMMsg, "");
				LogUtility.getPumpMLogger().info("[Pump M] Send EN Content to Sale M");
				sendMessage(preambleM);
			} catch (Exception e) {
				LogUtility.getPumpMLogger().error(e.getMessage(),e);
			}
			
			
			T_KH_CARWASH_COUPON_LISTData carwash_coupon_listSLT = new T_KH_CARWASH_COUPON_LISTData();
			T_KH_CARWASH_COUPON_LISTData carwash_coupon_list = new T_KH_CARWASH_COUPON_LISTData();
			String qlBarcodeStr = carWashBarCode; // 시스템(X)유효일자(YMMDD)세차할인금액(XX)SEQ(XXXX)발행매장코드(XXXX)
			
			carwash_coupon_list.setBarcode(qlBarcodeStr);
			String creation_time = GlobalUtility.getDateYYYYMMDDHHMMSS();
			carwash_coupon_list.setCreation_time(creation_time) ;
			String disc_amt = qlBarcodeStr.substring(6, 8);
			carwash_coupon_list.setDisc_amt(disc_amt);
			carwash_coupon_list.setUse_yn("N");
			carwash_coupon_list.setKhproc_no(khproc_no);
			
			LogUtility.getPumpMLogger().debug("[Pump M] CL_CARWASH_COUPON_LIST Insert : " + qlBarcodeStr + "/" + creation_time + "/" + disc_amt);
			SqlSession session = null;
			try {
				session = SqlSessionFactoryManager.openSqlSession();
				
				// insert to database
				carwash_coupon_listSLT = T_KH_CARWASH_COUPON_LISTHandler.getHandler().getT_T_KH_CARWASH_COUPON_LISTData(session, qlBarcodeStr);
				if (carwash_coupon_listSLT != null)
				{
					//인서트 
					String max_seq = T_KH_CARWASH_COUPON_LISTHandler.getHandler().getMaxSeq(qlBarcodeStr);
					if("99".equals(max_seq)){
						carwash_coupon_list.setSeq(max_seq);
						LogUtility.getPumpMLogger().debug("[Pump M] updateT_KH_CARWASH_COUPON_LIST - " + carwash_coupon_listSLT);
						T_KH_CARWASH_COUPON_LISTHandler.getHandler().updateT_KH_CARWASH_COUPON_LISTData(session, carwash_coupon_list);
					}else{
						max_seq =  (Integer.parseInt(max_seq) + 1) + "";
						carwash_coupon_list.setSeq(max_seq);
						LogUtility.getPumpMLogger().debug("[Pump M] updateT_KH_CARWASH_COUPON_LIST - " + carwash_coupon_listSLT);
						T_KH_CARWASH_COUPON_LISTHandler.getHandler().insertT_KH_CARWASH_COUPON_LISTData(session, carwash_coupon_list);
					}
				} else {
					//인서트 
					carwash_coupon_list.setSeq("00");
					LogUtility.getPumpMLogger().debug("[Pump M] insertT_KH_CARWASH_COUPON_LIST - " + carwash_coupon_listSLT);
					T_KH_CARWASH_COUPON_LISTHandler.getHandler().insertT_KH_CARWASH_COUPON_LISTData(session, carwash_coupon_list);
					
				}
				session.commit();
			} catch (Exception e) {
				LogUtility.getPumpMLogger().error(e.getMessage(),e);;
				session.rollback();
			} finally {
				SqlSessionFactoryManager.closeSqlSession(session);
			}
		}
	}

	private void processOPTBarcode(WorkingMessage workMsg, String khproc_no) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/"+ "processOPTBarcode()");

		QL_WorkingMessage ql_WMsg = (QL_WorkingMessage) workMsg;
		if (!ql_WMsg.getBarCode().equals("")) {
			String getMessageID = GlobalUtility.getUniqueMessageID();
			try {
				POS_EN enPumpMMsg = new POS_EN(getMessageID, khproc_no, ql_WMsg ,
						T_KH_STOREHandler.getHandler().getWorkingDate());
	
				Preamble preambleM = PumpMUtil.createPOSMessagePreamble(GlobalUtility.getUniqueMessageID(), SyncManager.DISE_SALE_MODULE,enPumpMMsg, "");
				LogUtility.getPumpMLogger().info("[Pump M] Send EN Content to Sale M");
				sendMessage(preambleM);
			} catch (Exception e) {
				LogUtility.getPumpMLogger().error(e.getMessage(),e);
			}

			T_KH_CARWASH_COUPON_LISTData carwash_coupon_listSLT = new T_KH_CARWASH_COUPON_LISTData();
			T_KH_CARWASH_COUPON_LISTData carwash_coupon_list = new T_KH_CARWASH_COUPON_LISTData();
			String qlBarcodeStr = ql_WMsg.getBarCode(); // 시스템(X)유효일자(YMMDD)세차할인금액(XX)SEQ(XXXX)발행매장코드(XXXX)
			
			carwash_coupon_list.setBarcode(qlBarcodeStr);
			String creation_time = GlobalUtility.getDateYYYYMMDDHHMMSS();
			carwash_coupon_list.setCreation_time(creation_time) ;
			String disc_amt = qlBarcodeStr.substring(6, 8);
			carwash_coupon_list.setDisc_amt(disc_amt);
			carwash_coupon_list.setUse_yn("N");
			carwash_coupon_list.setKhproc_no(khproc_no);
			
			LogUtility.getPumpMLogger().debug("[Pump M] CL_CARWASH_COUPON_LIST Insert : " + qlBarcodeStr + "/" + creation_time + "/" + disc_amt);
			SqlSession session = null;
			try {
				session = SqlSessionFactoryManager.openSqlSession();
				
				// insert to database
				carwash_coupon_listSLT = T_KH_CARWASH_COUPON_LISTHandler.getHandler().getT_T_KH_CARWASH_COUPON_LISTData(session, qlBarcodeStr);
				if (carwash_coupon_listSLT != null)
				{
					//인서트 
					String max_seq = T_KH_CARWASH_COUPON_LISTHandler.getHandler().getMaxSeq(qlBarcodeStr);
					if("99".equals(max_seq)){
						carwash_coupon_list.setSeq(max_seq);
						LogUtility.getPumpMLogger().debug("[Pump M] updateT_KH_CARWASH_COUPON_LIST - " + carwash_coupon_listSLT);
						T_KH_CARWASH_COUPON_LISTHandler.getHandler().updateT_KH_CARWASH_COUPON_LISTData(session, carwash_coupon_list);
					}else{
						max_seq =  (Integer.parseInt(max_seq) + 1) + "";
						carwash_coupon_list.setSeq(max_seq);
						LogUtility.getPumpMLogger().debug("[Pump M] updateT_KH_CARWASH_COUPON_LIST - " + carwash_coupon_listSLT);
						T_KH_CARWASH_COUPON_LISTHandler.getHandler().insertT_KH_CARWASH_COUPON_LISTData(session, carwash_coupon_list);
					}
				} else {
					//인서트 
					carwash_coupon_list.setSeq("00");
					LogUtility.getPumpMLogger().debug("[Pump M] insertT_KH_CARWASH_COUPON_LIST - " + carwash_coupon_listSLT);
					T_KH_CARWASH_COUPON_LISTHandler.getHandler().insertT_KH_CARWASH_COUPON_LISTData(session, carwash_coupon_list);
					
				}
				session.commit();
			} catch (Exception e) {
				LogUtility.getPumpMLogger().error(e.getMessage(),e);;
				session.rollback();
			} finally {
				SqlSessionFactoryManager.closeSqlSession(session);
			}
		}
	}

	
	/** PI2, 신규 ODT 추가에 따른 프로세스 추가, 2016-03-23 - CWI
     * ksm 2011.12.13 PumpAController의 EN전문 전송 및 SEQ DB Insert/Update 로직임.
     * twsongkis 2016. 4. 14. 오후 15:48:31, PI2, songkis,  DB Insert/Update 테이블 수정.
     * 서일석유에서 미만주유시 POS로 EN전문 전송이 안된다고 수정 요청옴. (대리점 영업1팀 김재준 차장 요청 CSR)
     * @param workMsg
     */
	private void processOPTGSCSelfBarcode(WorkingMessage workMsg, String khproc_no) {

		BS_WorkingMessage bsMsg = (BS_WorkingMessage) workMsg;

		if("".equals(bsMsg.getBarcode()) == false) {
			
			// POS에 EN 전문 송신 처리
			String getMessageID = GlobalUtility.getUniqueMessageID();
			
			try {
				POS_EN enPumpMMsg = new POS_EN(getMessageID, khproc_no, bsMsg, T_KH_STOREHandler.getHandler().getWorkingDate());

				Preamble preambleM = PumpMUtil.createPOSMessagePreamble(GlobalUtility.getUniqueMessageID(), SyncManager.DISE_SALE_MODULE, enPumpMMsg, "");
				LogUtility.getPumpMLogger().info("[Pump M] Send EN Content to Sale M");
				sendMessage(preambleM);
			} catch(Exception e) {
				LogUtility.getPumpMLogger().error(e.getMessage(), e);
			}

			// POS에 Insert 함으로 KixxHUB에는 Insert 처리 안함
			/* 
			T_KH_CARWASH_COUPON_LISTData carwash_coupon_listSLT = new T_KH_CARWASH_COUPON_LISTData();
			T_KH_CARWASH_COUPON_LISTData carwash_coupon_list = new T_KH_CARWASH_COUPON_LISTData();
			String bsBarcodeStr = bsMsg.getBarcode(); // 시스템(X)유효일자(YMMDD)세차할인금액(XX)SEQ(XXXX)발행매장코드(XXXX)

			String creation_time = GlobalUtility.getDateYYYYMMDDHHMMSS();
			String disc_amt = bsBarcodeStr.substring(6, 8);

			carwash_coupon_list.setBarcode(bsBarcodeStr);
			carwash_coupon_list.setCreation_time(creation_time);
			carwash_coupon_list.setDisc_amt(disc_amt);
			carwash_coupon_list.setUse_yn("N");
			carwash_coupon_list.setKhproc_no(khproc_no);

			LogUtility.getPumpMLogger().debug("[Pump M] CL_CARWASH_COUPON_LIST Insert : " + bsBarcodeStr + "/" + creation_time + "/" + disc_amt);
			SqlSession session = null;
			try {
				session = SqlSessionFactoryManager.openSqlSession();

				// insert to database
				carwash_coupon_listSLT = T_KH_CARWASH_COUPON_LISTHandler.getHandler().getT_T_KH_CARWASH_COUPON_LISTData(session, bsBarcodeStr);
				if(carwash_coupon_listSLT != null) {
					// 인서트
					String max_seq = T_KH_CARWASH_COUPON_LISTHandler.getHandler().getMaxSeq(bsBarcodeStr);
					if("99".equals(max_seq)) {
						carwash_coupon_list.setSeq(max_seq);
						LogUtility.getPumpMLogger().debug("[Pump M] updateT_KH_CARWASH_COUPON_LIST - " + carwash_coupon_listSLT);
						T_KH_CARWASH_COUPON_LISTHandler.getHandler().updateT_KH_CARWASH_COUPON_LISTData(session, carwash_coupon_list);
					} else {
						max_seq = (Integer.parseInt(max_seq) + 1) + "";
						carwash_coupon_list.setSeq(max_seq);
						LogUtility.getPumpMLogger().debug("[Pump M] updateT_KH_CARWASH_COUPON_LIST - " + carwash_coupon_listSLT);
						T_KH_CARWASH_COUPON_LISTHandler.getHandler().insertT_KH_CARWASH_COUPON_LISTData(session, carwash_coupon_list);
					}
				} else {
					// 인서트
					carwash_coupon_list.setSeq("00");
					LogUtility.getPumpMLogger().debug("[Pump M] insertT_KH_CARWASH_COUPON_LIST - " + carwash_coupon_listSLT);
					T_KH_CARWASH_COUPON_LISTHandler.getHandler().insertT_KH_CARWASH_COUPON_LISTData(session, carwash_coupon_list);

				}
				session.commit();
			} catch(Exception e) {
				LogUtility.getPumpMLogger().error(e.getMessage(), e);
				session.rollback();
			} finally {
				SqlSessionFactoryManager.closeSqlSession(session);
			}
			*/
		}
	}
	
	
	/**
	 * 주유기로 부터 온 주유 완료 전문를 처리한다. 주유 완료 금액이 0원일 경우 주유완료건으로 인정하는 경우가 있다.
	 * (PumpMTransactionManager.needToSendToPOS_IFPumpCompleted_ZeroOne() 함수 참조)
	 * 주유완료 금액이 0원이 아닐 경우, T_KH_PUMP_TR 테이블을 update 하고, POS 에 주유완료 전문(DG전문)을
	 * 전송한다. 그리고 주유완료 이후, 계기단가와 주유기의 단가를 비교하여 다를 경우 주유단가 정보를 전송한다.
	 * 
	 * @param uniqueKey :
	 *            Preamble Object 의 Unique Key
	 * @param messageID :
	 *            주유기로 부터 온 전문 Message ID
	 * @param s4WorkMsg :
	 *            주유기로 부터 온 전문
	 * @param khproc_no :
	 *            KH 처리번호
	 */
	private void processS4(String uniqueKey, String messageID, S4_WorkingMessage s4WorkMsg, String khproc_no) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/" + "processS4()");

		int nozzle_noInt = Integer.parseInt(s4WorkMsg.getNozzleNo());

		String price = PumpMPriceManager.getRightPumpPrice(nozzle_noInt, s4WorkMsg.getPrice());

		// // 주유기에서 올라온 단가와 KIXXHUB가 알고 있는 단가가 틀릴경우, KIXXHUB가 알고 있는 단가로 저장한다.
		// 09.03.27 의견 작성 -- 박종호
		// 주유기 단가가 비정상이 아니고, 단가변경이 제대로 이뤄지지 않았을 경우에는 주유기 표시 금액과 포스 결제 금액의 차이가
		// 발생하는 문제 및, 주유기 단가가 다를 경우 단가 재전송 프로로세스가 작동하지 않는 문제 와 소모셀프 단가 할인 받는 고객의
		// 경우에 단가 재전송하지 않는 문제가 사이드 이펙트로 발생하여 이 프로세스는 적용하지 않는다.
		// 이 로직의 추가에 대해서 정우철 차장과 협의했을시, 적용하지 않는 것이 맞다는 의견을 받은 적이 있음. 현재 이 로직이 적용
		// 되어 매장 패치가 이뤄 진 곳은 없음.
		// // String resetBasePrice =
		// Integer.toString((int)Double.parseDouble(PumpMUtil.getBasePriceFromKHProcNo(s4WorkMsg.getNozzleNo(),
		// khproc_no)) * 100 );
		// String resetBasePrice =
		// Integer.toString((int)Double.parseDouble(PumpMUtil.getBasePriceFromNozzleNo(s4WorkMsg.getNozzleNo()))
		// * 100 );
		// LogUtility.getPumpMLogger().info("[Pump M] KBP int" + resetBasePrice + ",
		// PBP" + s4WorkMsg.getBasePrice() );
		// if (!resetBasePrice.equals(s4WorkMsg.getBasePrice()) ) {
		// LogUtility.getPumpMLogger().info("[Pump M] ReSet BasePrice = " +
		// resetBasePrice);
		// s4WorkMsg.setBasePrice(resetBasePrice);
		// }

		try {
			int priceInt = Integer.parseInt(price);
			boolean sendS4ContentToPOS = true;

			if (priceInt == 0) {
				if (PumpMTransactionManager.getInstance().needToSendToPOS_IFPumpCompleted_ZeroOne(s4WorkMsg.getNozzleNo())) {
					sendS4ContentToPOS = true;
				} else {
					sendS4ContentToPOS = false;
				}
			}

			String liter = PumpMPriceManager.getRightPumpLiter(nozzle_noInt,
					PumpMUtil.convertNumberFormatFromPumpToPOS(s4WorkMsg.getLiter(), 3, 8));

			// 주유 완료 자료의 리터 * 단가의 값이 금액보다 100 원 이상의 차이가 발생 하였을 경우, 리터 * 단가의 값으로
			// 보정한다.
			// price = PumpMPriceManager.getRightPumpS4Price(nozzle_noInt,
			// liter, price, s4WorkMsg.getBasePrice());

			T_KH_PUMP_TRData pumpTRData = T_KH_PUMP_TRHandler.getHandler().getT_KH_PUMP_TRData(khproc_no);
			
			POS_DG dgPumpMMsg = new POS_DG(	messageID, 
					s4WorkMsg, 
					khproc_no, 
					pumpTRData.getGoods_code(), 
					pumpTRData.getStart_eqpm_qty(), 
					PumpMUtil.convertTotalLiterFromPumpTOPOS(s4WorkMsg.getTotalGauge()),
					pumpTRData.getOil_start_datetime_to(), 
					price, 
					liter,																												
					pumpTRData.getBizhour_date(), 
					PumpMUtil.getConvertBasePrice(s4WorkMsg.getNozzleNo(), s4WorkMsg.getBasePrice()));

			try {
				// 2009년 4월 21일 추영대 작성.
				GlobalUtility.printAnalysisLog("pumpM", khproc_no, s4WorkMsg
						.getNozzleNo(), String
						.valueOf(IConstant.STATE_PUMP_STATECODE_654), "주유완료", "",
						dgPumpMMsg.convertPOSContent(), "", "", "");

			} catch (Exception e) {
				LogUtility.getPumpMLogger().error(	"[Pump M] !!!!!!!!!!AnalysisLOG 출력 실패!!!!!!!!!!!", e);
			} // end try

			String eqpm_qty = dgPumpMMsg.getLiter();
			String eqpm_amt_prc = dgPumpMMsg.getPrice();
			String oil_datetime_to = dgPumpMMsg.getSystem_time();
			String end_eqpm_qty = dgPumpMMsg.getTotal_gauge();

			T_KH_PUMP_TRHandler.getHandler().updatePumpCompletedInfo_BY_khproc_no(	khproc_no, 
																					eqpm_qty, 
																					eqpm_amt_prc, 
																					oil_datetime_to, 
																					end_eqpm_qty);
			PumpMPriceManager.setTotalGauage(nozzle_noInt, end_eqpm_qty);

			if (!sendS4ContentToPOS) {
				LogUtility.getPumpMLogger().info("[Pump M] Drop DG content.");
				dgPumpMMsg = null; // ksm 2012.06.13
			} else {
				// 주유완료 전문 (DG) 를 Sale M 으로 전송
				Preamble preamble = PumpMUtil.createPOSMessagePreamble(null,	SyncManager.DISE_SALE_MODULE, dgPumpMMsg, "");
				LogUtility.getPumpMLogger().info("[Pump M] Send DG Content to Sale M");
				sendMessage(preamble);

				// Thread.sleep(100) ; // ksm 2012.03.06 주유완료 후 주유단가 설정시 딜레이둠.
				// 주유단가를 재 전송 (조건 : Preset 설정이면서, Preset 설정 단가가 계기단가와 다를 경우.)
				notifyBasePriceToPumpAfterPumpCompletion(khproc_no, s4WorkMsg.getNozzleNo());
				
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		}
	}

	/**
	 * 충전기로부터 고객카드 승인 요청전문(S9전문) 을 받아서 수행한다. 전문의 내용은 S9 전문의 mode 에 따라서 다음과 같이
	 * 구분된다. Mode 0 : 고객 카드에서 읽은 일련번호로 승인을 요청함. (먼저 충전원 카드인지 여부를 체크 한 이후 아니면
	 * 고객카드로 인식한다.) 1 : 수작업으로 입력된 차량정보로 승인을 요청함. 2 : 충전원 조회를 요청함. Mode 2 를 제외하고는
	 * POS 동작시 POS 에게 요청하여 그 응답 전문을 Pump A 로 전송하고, POS 가 동작하지 않을 경우 KixxHub 내
	 * 테이블의 정보를 이용하여 Pump A 에게 전송한다.
	 * 
	 * @param uniqueKey :
	 *            Preamble Object 의 Unique Key
	 * @param receiving_pumpa :
	 *            충전기로 부터 요청받은 전문
	 * @param messageID :
	 *            주유기로 부터 온 전문 Message ID
	 * @param s9WorkMsg :
	 *            주유기로 부터 온 전문
	 */
	private void processS9(String uniqueKey, Object receiving_pumpa,
			String messageID, S9_WorkingMessage s9WorkMsg) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info(
					"SSDC/" + "PUMP_M/" + "PupmAController/" + "processS9()");

		int s9Mode = Integer.parseInt(s9WorkMsg.getMode());
		switch (s9Mode) {
		case 0: {
			// 고객 카드에서 읽은 일련번호로 승인을 요청합니다.
			LogUtility.getPumpMLogger().info(
					"[Pump M] 고객 카드(or 충전원 카드)에서 읽은 일련번호로 승인을 요청을 받았습니다");
			// 먼저 충전원 카드인지 검사하고 충전원 카드가 아닐 경우 고객 카드를 검증합니다.
			WorkingMessage workMsg = S9Util
					.getPGWorkingMessageForChargingPerson(s9WorkMsg);
			if (((PG_WorkingMessage) workMsg).getTransType().equals("0")) {
				// 충전원 카드인 경우
				LogUtility.getPumpMLogger().info("[Pump M] 충전원 카드입니다.");
				LogUtility.getPumpMLogger().info(
						"[Pump M] 고객정보를 삭제 합니다. 새로운 KH 처리번호를 발행합니다."
								+ uniqueKey);

				resetCustDataNewKHProcNo(workMsg);
				Preamble pgPreamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey,
						SyncManager.DISE_PUMP_ADAPTER, workMsg, "");

				LogUtility.getPumpMLogger().info(
						"[Pump M] 충전원 카드정보를 전송합니다." + uniqueKey);

				sendMessage(pgPreamble);
			} else {
				String khproc_no = ""; // 사용하지 않음.

				// 충전원 카드가 아닌 경우 고객 카드 검증.
				LogUtility.getPumpMLogger().info(
						"[Pump M] 충전원 카드가 아닙니다. 고객 카드인지 점검합니다.");
				LogUtility.getPumpMLogger().info("[Pump M] POS 에게 고객 카드정보 요청을 합니다.");

				POS_DV dvPumpM = S9Util.getPOSPumpM_DV(messageID,
						s9WorkMsg.getNozzleNo(), khproc_no, s9WorkMsg
								.getSerialNumber());

				try {
					PumpMSyncManager.setSyncData(uniqueKey, messageID,
							SyncManager.DISE_PUMP_MODULE, receiving_pumpa);

					Preamble preamble = PumpMUtil.createPOSMessagePreamble(uniqueKey,
							SyncManager.DISE_POS_ADAPTER, dvPumpM, "");
					sendMessage(preamble);
				} catch (Exception e) {
					LogUtility.getPumpMLogger().error(e.getMessage(), e);
				}

			}
			break;
		}
		case 1: {
			// 수작업으로 입력된 차량정보로 승인을 요청합니다.
			// 중복 차량이 있을 경우 모든 차량정보를 전송합니다.
			LogUtility.getPumpMLogger().info("[Pump M] POS 에게 차량정보를 요청합니다.");
			POS_DT dtumpM = S9Util.getPOSPumpM_DT(messageID, s9WorkMsg
					.getNozzleNo(), s9WorkMsg.getSerialNumber());

			try {
				PumpMSyncManager.setSyncData(uniqueKey, messageID,
						SyncManager.DISE_PUMP_MODULE, receiving_pumpa);

				Preamble preamble = PumpMUtil.createPOSMessagePreamble(uniqueKey,
						SyncManager.DISE_POS_ADAPTER, dtumpM, "");
				sendMessage(preamble);
			} catch (Exception e) {
				LogUtility.getPumpMLogger().error(e.getMessage(), e);
			}
			break;
		}
		case 2: {
			// 충전원 등록을 요청합니다.
			LogUtility.getPumpMLogger().info("[Pump M] 충전원 카드 조회 요청을 받았습니다");
			WorkingMessage workMsg = S9Util
					.getPGWorkingMessageForChargingPerson(s9WorkMsg);
			Preamble pgPreamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey,
					SyncManager.DISE_PUMP_ADAPTER, workMsg, "");
			LogUtility.getPumpMLogger().info("[Pump M] 충전원 카드 조회 요청에 대한 응답을 전송합니다.");
			sendMessage(pgPreamble);
			break;
		}
		}
	}

	/**
	 * 주유기로 부터 보관증 번호 부여 요청을 수행합니다.
	 * 
	 * @param uniqueKey :
	 *            Preamble Object 의 Unique Key
	 * @param messageID :
	 *            주유기로 부터 온 전문 Message ID
	 * @param sfWorkMsg :
	 *            주유기로 부터 온 전문
	 */
	private void processSF(String uniqueKey, String messageID,
			SF_WorkingMessage sfWorkMsg) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info(
					"SSDC/" + "PUMP_M/" + "PupmAController/" + "processSF()");

		/*
		 * PP_WorkingMessage ppWorkMsg = null ; try { T_KH_KEYSData keysData =
		 * T_KH_KEYSHandler.getHandler().getT_KH_KEYSData(IPumpConstant.PUMP_KEEP_NUMBER) ;
		 * String keepNumber = null ;
		 * 
		 * if (keysData == null) { keepNumber =
		 * GlobalUtility.getKHTransactionDate() ;
		 * T_KH_KEYSHandler.getHandler().insertT_KH_KEYSData(IPumpConstant.PUMP_KEEP_NUMBER,
		 * keepNumber); } else { int keepno =
		 * Integer.parseInt(keysData.getValue()) ; keepno++ ; keepNumber =
		 * Integer.toString(keepno) ;
		 * T_KH_KEYSHandler.getHandler().updateT_KH_KEYSData(IPumpConstant.PUMP_KEEP_NUMBER,
		 * keepNumber); }
		 * 
		 * ppWorkMsg = new
		 * PP_WorkingMessage(sfWorkMsg.getMessageID(),sfWorkMsg.getNozzleNo(),keepNumber ) ; }
		 * catch (Exception e) { LogUtility.getPumpMLogger().error(e.getMessage(),e) ; }
		 */

		String keepNumber = PumpMUtil.getKeepIssueNo();
		PP_WorkingMessage ppWorkMsg = new PP_WorkingMessage(sfWorkMsg
				.getMessageID(), sfWorkMsg.getNozzleNo(), keepNumber);

		ppWorkMsg.setMessageID(messageID);
		Preamble ppPreamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey,
				SyncManager.DISE_PUMP_ADAPTER, ppWorkMsg, "");

		LogUtility.getPumpMLogger().info("[Pump M] Send Keep No to Pump A");
		sendMessage(ppPreamble);
	}

	/**
	 * 충전기 판매 완료 전문을 받으면 다음을 수행한다. 1. 충전기에 ACK 전문을 송신한다. 2. 현금/상품권/외상 결제가 SH 전문에
	 * 포함된 경우 UPOSMessage 전문을 만들어서 전송한다. 외상 - 0082 상품권 - 0022 현금 - 0012 3. POS 에
	 * 결제 완료 전문 (MessageType = 0001) 을 전송한다. (lastpayment_yn=1 로 설정) 4.
	 * SaleContent 에 저장된 정보를 초기화한다.(제거한다)
	 * 
	 * @param uniqueKey :
	 *            Unique Key
	 * @param shWorkMsg :
	 *            SH WorkingMessage
	 * @param khproc_no :
	 *            KH 처리번호
	 */
	private void processSH(String uniqueKey, SH_WorkingMessage shWorkMsg,
			String khproc_no) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/" + "processSH()");

		// 전문변경에 따른 항목 추가(2015.11.23)
		String term_Res_Code = shWorkMsg.getTerm_Res_Code(); 			// 단말기 응답 코드
		String txt_Direction = shWorkMsg.getTxt_Direction(); 			// 전문방향
		    
		ACK_WorkingMessage ackWorkMsg = new ACK_WorkingMessage(shWorkMsg.getMessageID(), shWorkMsg.getNozzleNo());

		// 1. 충전기에 ACK 전문을 송신한다.
		Preamble ackPreamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey, SyncManager.DISE_PUMP_ADAPTER, ackWorkMsg, "");
		
		LogUtility.getPumpMLogger().info("[Pump M] Receive SH from Recharge, so Send ACK to Pump A");
		sendMessage(ackPreamble);

		// 2. 현금/상품권/외상 결제가 SH 전문에 포함된 경우 UPOSMessage 전문을 만들어서 전송한다.
		if (ODTUtility_Recharge.shouldCreateMoreUPOSMessage(shWorkMsg)) {
			POSHeader posMsg = PumpMODTSaleManager.getCustPOSPumpM(shWorkMsg.getNozzleNo());
			POS_DW dwPumpM = null;

			if ((posMsg != null) && (posMsg instanceof POS_DW)) {
				dwPumpM = (POS_DW) posMsg;
			}

			ArrayList<UPOSMessage> uPosMsgArray = ODTUtility_Recharge.createUPOSMessage(dwPumpM, shWorkMsg, khproc_no);

			if ((uPosMsgArray != null) && (uPosMsgArray.size() > 0)) {
				LogUtility.getPumpMLogger().info("[Pump M] Send Created UPOSMessage(0012 0022 0082) to SaleM. size="	+ uPosMsgArray.size());

				for (int i = 0; i < uPosMsgArray.size(); i++) {
					UPOSMessage uPosMsg = uPosMsgArray.get(i);
					Preamble posPreamble = PumpMUtil.createUPOSMessagePreamble(	SyncManager.getUniqueKey(), 
							SyncManager.DISE_PUMP_MODULE,
							SyncManager.DISE_SALE_MODULE, 
							uPosMsg, "");
					sendMessage(posPreamble);
				}
			}
		}

		// 3. POS 에 결제 완료 전문 (MessageType = 0001) 을 전송한다. (lastpayment_yn=1 로 설정)
		if (PumpMODTSaleManager.shouldSend0001UPOSMessageToSaleM(shWorkMsg.getNozzleNo(), khproc_no, shWorkMsg)) {
			
			UPOSMessage uPosMsg = CreateUPOSMessage.createUPOSMessage_0001(IUPOSConstant.DEVICE_TYPE_3O, khproc_no, "1", term_Res_Code, txt_Direction);
			
			Preamble posPreamble = PumpMUtil.createUPOSMessagePreamble(	SyncManager.getUniqueKey(), 
					SyncManager.DISE_PUMP_MODULE,
					SyncManager.DISE_SALE_MODULE, 
					uPosMsg, "");

			LogUtility.getPumpMLogger().info("[Pump M] Send 0001 UPOSMessage to SaleM.");

			sendMessage(posPreamble);
		}

		// 4. SaleContent 에 저장된 정보를 초기화한다.(제거한다)
		PumpMODTSaleManager.initSaleContent(shWorkMsg.getNozzleNo());
	}

	/**
	 * 
	 * 소모셀프 판매완료전문 (ST 전문) 에 대한 수행을 실시한다. ST 전문에는 현금 및 외상 그리고 POS 로 부터의 결제 여부 및
	 * 선승인취소 실패, 재승인 실패 내용들이 담겨져 있다. 이러한 내용들을 재 구성하여서 POS 로 전송하도록 한다.
	 * 
	 * @param uniqueKey :
	 *            Unique Key
	 * @param messageID :
	 *            Message ID
	 * @param commandID :
	 *            Command ID
	 * @param stWorkMsg :
	 *            판매 완료 전문 (ST 전문)
	 * @param khproc_no :
	 *            KH 처리번호
	 */
	private void processST(String uniqueKey, String messageID, String commandID, ST_WorkingMessage stWorkMsg, String khproc_no) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/" + "processST()");

		// 외상 거래처의 경우 ST 전문 및 기존 DW(POS Protocol) HD(Pump A Spec) 전문을
		// 이용하여 외상 전문을 생성해서 POS 로 전송한다.
		String nozzleNo = stWorkMsg.getConnectNozzleNo();
		// String cardNo = stWorkMsg.getCardNo(); // (SOMO SELF) 카드 번호
		// String bonusCardNo = stWorkMsg.getBonusCardNo(); // (SOMO SELF) 보너스
		// 카드 번호
		Vector<PB_ST_TrInfo> trInfoVector = stWorkMsg.getTrInfoVector(); // TR_TYPE,
																			// MODE,
																			// 결제
																			// 수량,
																			// 결제
																			// 단가,
																			// 결제
																			// 금액

		int size = 0;
		if (trInfoVector != null) {
			size = trInfoVector.size();
		}

		// 2009년 5월 8일 추영대 추가.
		// LOG 출력
		StringBuffer dataLog = new StringBuffer();
		for (int i = 0; i < size; i++) {
			PB_ST_TrInfo trInfo = trInfoVector.get(i);

			dataLog.append("Mode : " + trInfo.getMode() + ", TRTYPE : " + trInfo.getTrType() + " / ");

		} // end for

		try {
			GlobalUtility.printAnalysisLog("pumpM", khproc_no, stWorkMsg.getNozzleNo(), "", "판매완료", "", dataLog.toString().getBytes(), "", "", "");

		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(	"[Pump M] !!!!!!!!!!AnalysisLOG 출력 실패!!!!!!!!!!!", e);
		} // end try

		for (int i = 0; i < size; i++) {
			PB_ST_TrInfo trInfo = trInfoVector.get(i);
			String mode = trInfo.getMode(); // 1 : 승인
			// 2 : 선취소
			// 3 : 재승인 ( 현금, POS 에서의 Preset 인 경우는 실 주유금액)
			// 4 : 선취소 실패
			// 5 : 재승인 실패

			String trType = trInfo.getTrType(); // 0 : POS 로 부터의 Preset
			// 1 : 신용카드 승인
			// 2 : 신용카드 승인 취소
			// 5 : 현금 승인
			// 7 : 외상 승인 요청
			// A : GS 보너스/GS & 보너스 이용 승인
			// B : GS 보너스/GS & 보너스 이용 취소
			// C : 기타(myLG 포함) 보너스 카드 이용 승인
			// D : 기타(myLG 포함) 보너스 카드 이용 취소
			// F : 현금영수증 승인

			/*
			 * TrType ( 5: 현금, 7: 외상 ), Mode (1:승인, 2:선취소, 3:재승인, 4:선취소실패,
			 * 5:재승인실패, 6: 주유없이 취소버튼 클릭) 외상 거래에 의한 결제일 경우 외상 전문을 생성해서 POS 에
			 * 전송한다.
			 */
			if ("7".equals(trType) && "1".equals(mode)) {
				POSHeader posMsg = PumpMODTSaleManager.getCustPOSPumpM(nozzleNo);
				POS_DW dwPumpM = null;
				if ((posMsg != null) && (posMsg instanceof POS_DW)) {
					dwPumpM = (POS_DW) posMsg;
				}

				// ??? ksm 왜 array로 받아서 처리하지?
				ArrayList<UPOSMessage> uPosMsgArray = ODTUtility_SoMo	.createUPOSMessage(nozzleNo, dwPumpM, trInfo, khproc_no);
				if ((uPosMsgArray != null) && (uPosMsgArray.size() > 0)) {
					LogUtility.getPumpMLogger().info(	"[Pump M] Send Created UPOSMessage(0082) to SaleM. size=" + uPosMsgArray.size());

					// 2012.06.28 ksm 외상 전문의 경우 uposHash에 저장하지 않고 있음.
					// 소모셀프 세차권 바코드 출력 관련하여 추가함.
					PumpMODTSaleManager.addUPOSMessage(nozzleNo, uPosMsgArray.get(0));

					for (int j = 0; j < uPosMsgArray.size(); j++) {
						UPOSMessage uPosMsg = uPosMsgArray.get(j);

						Preamble posPreamble = PumpMUtil.createUPOSMessagePreamble(SyncManager.getUniqueKey(),
								SyncManager.DISE_PUMP_MODULE,
								SyncManager.DISE_SALE_MODULE, 
								uPosMsg, "");
						sendMessage(posPreamble);
						/*
						 * 2012.06.28 ksm 외상의 경우 0082 가 두번 POS로 전송됨. 확인필요. 0001
						 * 전송시 미전송 조회하면서 걸리는것으로 보임. 0.06 sec 차이임.
						 */
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			} else if ("5".equals(trType) 
					&& ("1".equals(mode) || "3".equals(mode))) {
				// } else if ("5".equals(trType) && ("1".equals(mode))) {
				// 2012.07.05 ksm
				// 현금 미만주유의 경우(mode =3) POS로 0012 전문을 전송하지 않았음. 전송하도록 수정.
				// 92번 판매완료 전문에 현금 투입금액 넘어옴.
				// 0원0리터 주유시 문제가 되므로 결제금액 체크하여 분기시킴.
				
				// 현금 응답 전문을 만들어서 POS 로 전송해야 한다.
				
				String paymentAmt = trInfo.getPrice();				
				
				if (!"0".equals(GlobalUtility.getStringValue(paymentAmt))) {
					String emp_no 				= ""; // Default
					String custCard_No 		= ""; // Default
					String ss_crStNum 			= ""; // Default
					String ss_carNum 			= ""; // Default
					String lastPayment_yn 	= "";
					String term_id 				= ""; // Default
					String led_code 				= IUPOSConstant.RESPOND_LEDCODE_1; // Default 1 : 승인

					String salesBasePrice = PumpMUtil.convertBasePriceFromPumpToPOS(trInfo.getBasePrice());// 판매 단가
					String paymentLiter 	= PumpMUtil.convertTotalLiterFromPumpTOPOS(trInfo.getLiter()); 		// 결제 리터

					POSHeader posMsg = PumpMODTSaleManager.getCustPOSPumpM(nozzleNo);

					POS_DW dwPumpM = null;
					if ((posMsg != null) && (posMsg instanceof POS_DW)) {
						dwPumpM 	= (POS_DW) posMsg;
						custCard_No 	= dwPumpM.getCust_card_no();
						ss_crStNum 	= dwPumpM.getCust_code();
						ss_carNum 		= dwPumpM.getCar_no();
					}

					UPOSMessage_ItemInfo item_infoCash = UPOSUtil.getUPOSMessage_ItemInfo(nozzleNo, 
							khproc_no, 
							paymentLiter, 
							salesBasePrice, 
							paymentAmt);

					UPOSMessage uPosMsgCash = CreateUPOSMessage.createUPOSMessage_0012(IUPOSConstant.DEVICE_TYPE_3S, 
							khproc_no,
							nozzleNo, 
							emp_no, 
							item_infoCash,
							custCard_No, 
							ss_crStNum, 
							ss_carNum,
							lastPayment_yn, 
							paymentAmt, 
							term_id,
							led_code);

					Preamble posPreamble = PumpMUtil.createUPOSMessagePreamble(SyncManager.getUniqueKey(), 
							SyncManager.DISE_PUMP_MODULE,
							SyncManager.DISE_SALE_MODULE, 
							uPosMsgCash, "");
					sendMessage(posPreamble);
				} else {
					LogUtility.getPumpMLogger().info("[Pump M] 현금결제 후 0원 주유인 경우 전문을 만들지 않는다.");
				}
			} else if ("5".equals(trType) && "6".equals(mode)) {
				// 현금 투입 후, 주유기 노즐 업/다운 발생하지 않은 상황에서 취소 버튼을 눌렀을 경우
				// DG를 생성해 POS 로 전송
				LogUtility.getPumpMLogger().info("[Pump M] Send DG Content to Sale M : SOMO SELF CASH 0Liter 0Won - 취소버튼 클릭");
				try {
					T_KH_PUMP_TRData pumpTRData = T_KH_PUMP_TRHandler.getHandler().getT_KH_PUMP_TRData(khproc_no);
					POS_DG dgPumpMMsg = new POS_DG(	messageID,
							khproc_no, 
							pumpTRData.getGoods_code(), 
							"0", "0",	"0", "0", 
							pumpTRData.getBizhour_date(), 
							nozzleNo,
							"0", 
							GlobalUtility.appending0End(pumpTRData.getBaseprice(), 6));
					// dgPumpMMsg.print();
					// LogUtility.getPumpMLogger().info("PumpUtil.appending0Pre(pumpTRData.getBaseprice(),
					// 6)" + PumpUtil.appending0Pre(pumpTRData.getBaseprice(),
					// 6));

					Preamble preamble = PumpMUtil.createPOSMessagePreamble(null,
							SyncManager.DISE_SALE_MODULE, dgPumpMMsg, "");
					sendMessage(preamble);

				} catch (Exception e) {
					LogUtility.getPumpMLogger().error(e.getMessage(), e);
				}
			} else if ("F".equals(trType) && "1".equals(mode)) {
				// 현금 영수증 발행된 현금 결제의 경우
				// 현금영수증 HF 가 이미 POS 로 전송 되었음으로 현금 HF를 보내지 않는다
			}
		}

		// 2012.06.07 ksm
		// 판매완료 받았을 경우 세차권 사용인지 확인 후 POS로 EN전문 전송
		// 단, 0원0리터 주유일 경우 EN전문 생성안함.
		// 1. 세차권출력 여부 확인 - (셀프) 세차 바코드 출력 여부
		String printBarCode = "0";

		try {
			printBarCode = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0440);
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error("[CarWash BarCode]세차 바코드 출력 여부 조회시 에러 발생");
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		}
		// LogUtility.getPumpMLogger().info("[CarWash BarCode] (셀프) 세차 바코드 출력 여부 : "
		// + printBarCode);

		if ("1".equals(printBarCode)) {
			// 2. 최종 결제 성공여부 확인
			// 승인에 대한 취소 혹은 재승인 실패 전문이 있는 경우 바코드 전송 안함.
			// BL체크 주유 승인
			// 승인승인취소/승인취소승인
			if (!ODTUtility_SoMo.containFailContent(stWorkMsg)) {

				Integer sumAmt = 0;
				// String mode = "";

				String messageType = "";
				String ledCode = "";
				String pymtAmt = "0";

				ArrayList<UPOSMessage> uposArray = PumpMODTSaleManager.getUPOSMessageArray(nozzleNo, khproc_no);

				UPOSMessage uposM = null;

				if (uposArray == null) {
					// POS선결제인지 여부 확인. 선결제이면 EN전문 생성을 위해 금액 생성함.
					if (PumpMTransactionManager.getInstance().isPresetFromPOS(nozzleNo)) {
						sumAmt = 1;
					}
				} else {
					for (int i = 0; i < uposArray.size(); i++) {
						uposM = uposArray.get(i);
						messageType = uposM.getMessageType();
						ledCode = uposM.getLed_code();
						pymtAmt = uposM.getPayment_amt();

						LogUtility.getPumpMLogger().info("[CarWash BarCode] MessageType : ("	+ messageType + ") 금액 : (" + pymtAmt + ") LedCode : (" + ledCode + ")");

						// 보너스적립의 경우 금액 계산 제외 할 필요는 없네요. 정상적일 경우만 보너스 적립 요청하므로.
						// 보너스 적립의 경우 2배 금액 됨.
						if ("8".equals(messageType.substring(0, 1))) {
							sumAmt -= Integer.parseInt(pymtAmt);
						} else {
							sumAmt += Integer.parseInt(pymtAmt);
						}
					}
				}

				try {
					T_KH_PUMP_TRData pumpTRData = T_KH_PUMP_TRHandler.getHandler().getT_KH_PUMP_TRData(khproc_no);

					if (pumpTRData == null || "0".equals(pumpTRData.getEqpm_amt_prc())) {
						sumAmt = 0;
					}
				} catch (Exception e) {
					LogUtility.getPumpMLogger().error(	"[CarWash BarCode] 주유자료 조회 시 Exception 발생 : " + e.toString());
				}

				String pymtSum = sumAmt.toString();

				// 판매완료의 결제금액 합이 0 초과인 경우에만 바코드 계산하여 처리함.
				if (sumAmt > 0) {
					// 세차 바코드 생성
					// twsongkis 2015-01-28 새로운 Barcode 클래스의 barcode로직으로 변경
					String barCode = Barcode.getBarcodeNumber("3", pymtSum, nozzleNo, khproc_no, messageType, ledCode, null);			// 세차바코드
					LogUtility.getPumpMLogger().info("[CarWash BarCode] 바코드 값   :  " + barCode);
					
					// 세차 바코드 POS 전송
					processOPTBarcode(barCode, nozzleNo, khproc_no);
				} else {
					LogUtility.getPumpMLogger().info("[CarWash BarCode] 주유금액이 0원이거나 판매완료의 금액 합이 0 이하 이므로 정상적 결제가 아님. "+ pymtAmt);
				}
			} else {
				LogUtility.getPumpMLogger().info("[CarWash BarCode] 승인실패 건이 있으므로 세차권 출력안함.");
			}
		}
		// ///////////////////////////////////////////////////////////////////////////////////////////////////

		if (PumpMODTSaleManager.shouldSend0001UPOSMessageToSaleM(stWorkMsg.getConnectNozzleNo(), khproc_no, stWorkMsg)) {
			UPOSMessage uPosMsg = CreateUPOSMessage.createUPOSMessage_0001(IUPOSConstant.DEVICE_TYPE_3S, khproc_no, "1", "", "");
			Preamble posPreamble = PumpMUtil.createUPOSMessagePreamble(SyncManager.getUniqueKey(), 
					SyncManager.DISE_PUMP_MODULE,
					SyncManager.DISE_SALE_MODULE, 
					uPosMsg, "");
			LogUtility.getPumpMLogger().info("[Pump M] Send Self 0001 UPOSMessage to SaleM. 0001 전문이 다른 결제 전문보다 먼저 전송 될 수" 
					+ " 있기 때문에, 0001 전문은 1초 대기 이후 전송");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				LogUtility.getPumpMLogger().error(e.getMessage(), e);
			}
			sendMessage(posPreamble);
		}

		PumpMODTSaleManager.initSaleContent(stWorkMsg.getConnectNozzleNo());
	}

	/**
	 * 다쓰노 셀프로 부터 온 결제완료 전문 수행한다.
	 * 
	 * 1) UPOSMessage 존재 여부 (1) 존재하지 않을 경우 (POS 로 부터 Preset 으로 인한 주유로 가정.) a)
	 * Preset 주유금액과 실제 주유금액 일치 여부 (a) 일치할 경우 (b) 일치하지 않을 경우 (2) 존재하지 않을 경우 (ODT
	 * 로 부터 결제 요청에 의한 주유로 가정.) a) Preset 주유금액과 실제 주유금액 일치 여부 (a) 일치할 경우 (b) 일치하지
	 * 않을 경우
	 * 
	 * @param uniqueKey :
	 *            Preamble Object 의 Unique Key
	 * @param messageID :
	 *            주유기로 부터 온 전문 Message ID
	 * @param commandID :
	 *            주유기로 부터 온 전문 CommandID
	 * @param trWorkMsg :
	 *            주유기로 부터 온 판매완료 전문
	 * @param khproc_no :
	 *            KH 처리번호
	 */
	private void processTR(String uniqueKey, String messageID, String commandID, TR_WorkingMessage trWorkMsg, String khproc_no) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/" + "processTR()");

		handleWorkingMessageForAdditionalWork(uniqueKey, trWorkMsg
				.getConnectNozzleNo(), commandID, trWorkMsg, khproc_no);

		try {
			// 2009년 5월 8일 추영대 추가.
			GlobalUtility.printAnalysisLog("pumpM", khproc_no, trWorkMsg.getNozzleNo(), "", "판매완료", "", 
					new String("LITER : "	+ trWorkMsg.getLiter() + ", PRICE : " + trWorkMsg.getPrice()).getBytes(), "", "", "");

		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(	"[Pump M] !!!!!!!!!!AnalysisLOG 출력 실패!!!!!!!!!!!", e);
		} // end try

		/**
		 * 다쓰노 셀프의 경우 결제 내역이 있으면 0001 전문을 POS 로 전송하도록 한다. 만약 없다면 보내지 않는다. (이는
		 * POS 로 부터 Preset 에 의한 주유여부를 조사한다. (lastpayment_yn=1 로 설정)
		 * 
		 */
		if (PumpMODTSaleManager.shouldSend0001UPOSMessageToSaleM(trWorkMsg.getConnectNozzleNo(), khproc_no, trWorkMsg)) {
			UPOSMessage uPosMsg = CreateUPOSMessage.createUPOSMessage_0001(IUPOSConstant.DEVICE_TYPE_3S, khproc_no, "1", "", "");
			Preamble posPreamble = PumpMUtil.createUPOSMessagePreamble(SyncManager.getUniqueKey(), 
					SyncManager.DISE_PUMP_MODULE,
					SyncManager.DISE_SALE_MODULE, 
					uPosMsg, "");
			LogUtility.getPumpMLogger().info("[Pump M] Send Self 0001 UPOSMessage to SaleM.");
			sendMessage(posPreamble);
		}

		PumpMODTSaleManager.initSaleContent(trWorkMsg.getConnectNozzleNo());
	}

	/**
	 * 다쓰노 셀프 영수증 출력
	 * 
	 */
	private void QlWorkingMsg_DasNOFromODT(String uniqueKey, String nozzleNo,
			String khproc_no, String pumpingPrice, String pumpingLiter,
			String payedPrice, String pumpingBasePrice, UPOSMessage uPosMsg) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/" + "QlWorkingMsg_DasNOFromODT()");

		WorkingMessage qlMsg = null;

		if (PumpMUtil.getConnectedODTProtocolFromNozzleNo(nozzleNo) == 90) {
			qlMsg = ODTUtility_GSC_Self.getQL_WorkingMessage_GSCSELFFromODT(
					nozzleNo, khproc_no, pumpingPrice, pumpingLiter,
					payedPrice, pumpingBasePrice, uPosMsg);
		} else {
			qlMsg = ODTUtility_DaSNo.getQL_WorkingMessage_DaSNoFromODT(
					nozzleNo, khproc_no, pumpingPrice, pumpingLiter,
					payedPrice, pumpingBasePrice, uPosMsg);
		}

		LogUtility.getPumpMLogger().info("[Pump M] Send Receipt to Pump A. NozID=" + nozzleNo);
		Preamble preamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey, SyncManager.DISE_PUMP_ADAPTER, qlMsg, "");
		sendMessage(preamble);

		// 세차권 발행 내역을 Pos A로 전송한다.
		processOPTBarcode(qlMsg, khproc_no);
	}

	/**
	 * 상황에 따라 고객정보 를 삭제 하고 킥스허브 처리 번호를 새로이 발행한다.
	 * 
	 * @param commandID
	 * @param workMsg
	 */
	private void resetCustDataNewKHProcNo(WorkingMessage workMsg) {

		PumpMTransactionManager.getInstance().setNozzleState(	workMsg.getNozzleNo(), IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED);
		PumpMODTSaleManager.initSaleContent(workMsg.getNozzleNo());
	}

	/**
	 * 주유기에 단가를 전송합니다.
	 * 
	 * @param nozzle_no :
	 *            노즐 번호
	 */
	public void sendBasePrice(String nozzle_no) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info(
					"SSDC/" + "PUMP_M/" + "PupmAController/"
							+ "sendBasePrice()");

		try {
			LogUtility.getPumpMLogger().info(
					"Send Nozzle BasePrice. NozID=" + nozzle_no);
			String storeCode = T_KH_STOREHandler.getHandler().getStoreCode();
			Preamble pumpPreamble = null;
			WorkingMessage workMsg = null;

			// 주유기 Type 을 먼저 조사
			T_NZ_NOZZLEData tNozData = T_NZ_NOZZLEHandler.getHandler()
					.getT_NZ_NOZZLEDataByNozzleNo(nozzle_no);
			String self_ind_exist = tNozData.getSelf_ind_exist();

			if (self_ind_exist.equals(ICode.SELF_IND_EXIST_01_PUMP)
					|| self_ind_exist
							.equals(ICode.SELF_IND_EXIST_03_SEMI_SELF)) {
				// 주유기 / Semi 주유기 일 경우 P3
				LogUtility.getPumpMLogger()
						.info("[Pump M] create P3 WorkingMessage");
				workMsg = PumpMUtil.getP3WorkingMessage(storeCode, nozzle_no);
				if (workMsg != null) {
					workMsg.setMessageID(GlobalUtility.getUniqueMessageID());
					pumpPreamble = PumpMUtil.createWorkingMessagePreamble(SyncManager
							.getUniqueKey(), SyncManager.DISE_PUMP_ADAPTER,
							workMsg, "");

					LogUtility.getPumpMLogger().info("[Pump M] Send P3 to Nozzle");
					sendMessage(pumpPreamble);

					Thread.sleep(1000);
				} else {
					// LogUtility.getPumpMLogger().info("[Pump M] P3 전문을 보내지 않습니다.")
					// ;
				}
			} else if (self_ind_exist.equals(ICode.SELF_IND_EXIST_02_SELF_PUMP)) {
				// 셀프 주유기 일 경우 P5 (ODT ID) 로 요청
				LogUtility.getPumpMLogger().info("[Pump M] create P5 WorkingMessage");
				String odtID = tNozData.getOdtno_no_connected();
				workMsg = PumpMUtil.getP5WorkingMessageForSendingOnly(storeCode, odtID);

				if (workMsg != null) {
					workMsg.setMessageID(GlobalUtility.getUniqueMessageID());
					pumpPreamble = PumpMUtil.createWorkingMessagePreamble(SyncManager.getUniqueKey(), 
							SyncManager.DISE_PUMP_ADAPTER,
							workMsg, "");

					sendMessage(pumpPreamble);

					LogUtility.getPumpMLogger().info("[Pump M] Send P5 WorkingMessage to Self-Nozzle");
					Thread.sleep(1000);
				} else {
					// LogUtility.getPumpMLogger().info("[Pump M] 셀프 ODT가 없어서, P5 전문을
					// 주유기에 보내지 않습니다.") ;
				}

			} else if (self_ind_exist
					.equals(ICode.SELF_IND_EXIST_04_ODT_RECHARGE)) {
				// 충전기일 경우 D1
				LogUtility.getPumpMLogger().info("[Pump M] create D1 WorkingMessage");
				workMsg = PumpMUtil.getD1WorkingMessage(storeCode, nozzle_no);

				if (workMsg != null) {
					workMsg.setMessageID(GlobalUtility.getUniqueMessageID());
					pumpPreamble = PumpMUtil.createWorkingMessagePreamble(SyncManager
							.getUniqueKey(), SyncManager.DISE_PUMP_ADAPTER,
							workMsg, "");
					sendMessage(pumpPreamble);

					LogUtility.getPumpMLogger().info(
							"[Pump M] Send D1 WorkingMessage to Recharge");
					Thread.sleep(1000);
				} else {
					// LogUtility.getPumpMLogger().info("[Pump M] D1 전문을 주유기에 보내지
					// 않습니다.") ;
				}
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		}
	}

	/**
	 * POS 로 부터 초기화 전문을 다 받은 경우 성공했음을 POS A 에게 전송한다.
	 * 
	 * @param success :
	 *            true or false
	 */
	private void sendCompletedMessage(boolean success) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info(
					"SSDC/" + "PUMP_M/" + "PupmAController/"
							+ "sendCompletedMessage()");

		gStep = IPumpConstant.STEP_PUMP_RUNNING;
		getProducer_PumpMPosA_InitCompleted().produce(
				Boolean.valueOf(success));
		getProducer_PosA_InitCompleted().produce(
				Boolean.valueOf(success));		
	}

	/**
	 * 가득주유 불가능 시간 설정을 odt에 전달.
	 * 
	 */
	public void sendFullPumping() {
		if (logSSDC == true)
			LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/"+ "sendFullPumping()");

		SqlSession session = null;

		try {
			String storeCode = T_KH_STOREHandler.getHandler().getStoreCode();
			boolean isSend = false;

			session = SqlSessionFactoryManager.openSqlSession();
			String FullPumpingOptionUseYN = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(session, IConstant.POSPORTOCOL_CODEMASTER_0212);

			if (GlobalUtility.isNullOrEmptyString(FullPumpingOptionUseYN))
				return;

			if ("0".equals(FullPumpingOptionUseYN))
				return;

			String startTime = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(session, IConstant.POSPORTOCOL_CODEMASTER_0213);
			String endTime = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(session, IConstant.POSPORTOCOL_CODEMASTER_0214);

			if (GlobalUtility.isNullOrEmptyString(startTime) || GlobalUtility.isNullOrEmptyString(endTime))
				return;

			String systime = GlobalUtility.getDateYYYYMMDDHHMMSS();
			String yyyymmdd1 = GlobalUtility.getDateYYYYMMDD();
			String yyyymmdd2 = GlobalUtility.getDateYYYYMMDD();
			long sysHHMM = Long.parseLong(systime.substring(0, 12));

			if (Integer.parseInt(startTime) > Integer.parseInt(endTime)) {
				yyyymmdd2 = GlobalUtility.getNextDay(1,	IConstant.iyyyyMMdd, IConstant.DAY_OF_MONTH);
			}

			if (sysHHMM >= Long.parseLong(yyyymmdd1 + startTime)
					&& sysHHMM <= Long.parseLong(yyyymmdd2 + endTime)) {
				if (isUseFullPumping == 1) {
					isSend = true;
				} else {
					isSend = false;
				}
				isUseFullPumping = 0;
			} else {
				if (isUseFullPumping == 0) {
					isSend = true;
				} else {
					isSend = false;
				}
				isUseFullPumping = 1;
			}

			if (isSend) {
				T_KH_CODEMASTERData data = new T_KH_CODEMASTERData();

				data.setChg_dt(GlobalUtility.getDateYYYYMMDDHHMMSS());
				data.setGubun("1");
				data.setCode(IConstant.POSPORTOCOL_CODEMASTER_0335);
				data.setValue(String.valueOf(isUseFullPumping));

				data.print();

				if (GlobalUtility.isNullOrEmptyString(T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(session,	IConstant.POSPORTOCOL_CODEMASTER_0335)))
					T_KH_CODEMASTERHandler.getHandler().insertT_KH_CODEMASTERData(session, data);
				else
					T_KH_CODEMASTERHandler.getHandler().updateT_KH_CODEMASTERData(session, data);

				WorkingMessage p5workMsg = PumpMUtil.getP5WorkingMessageForFullPumping(storeCode, null);

				if (p5workMsg != null) {
					p5workMsg.setMessageID(GlobalUtility.getUniqueMessageID());
					Preamble p5Preamble = PumpMUtil.createWorkingMessagePreamble(SyncManager.getUniqueKey(), 
							SyncManager.DISE_PUMP_ADAPTER,
							p5workMsg, "");
					sendMessage(p5Preamble);

					// P5 전문을 셀프 ODT 에 전송
					LogUtility.getPumpMLogger().info("[Pump M] Send P5 to Self ODT");
				}
			}
			session.commit();
		} catch (Exception e) {
			session.rollback();
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		} finally {
			SqlSessionFactoryManager.closeSqlSession(session);
		}
	}
	
	/**
	 * Locking , unLocking 전문을 POS 에 전송한다.
	 * 
	 * @param nozID :
	 *            결제를 시작하는 노즐 번호
	 * @param khproc_no :
	 *            KH 처리 번호
	 */
	private void sendLockingInfoToPOS(String nozID, String khproc_no,	String isLock) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/" + "sendLockingInfoToPOS()");

		if ((khproc_no == null) || ("".equals(khproc_no))) {
			return;
		}
		if (isLock.equals(IConstant.PUMP_SALE_LOCKING)) {
			LockingManager.locking(nozID, khproc_no, IConstant.LOCKING_SRC_ODT, true);
		} else {
			LockingManager.unlocking(nozID, khproc_no, true);
		}
	}

	/**
	 * Pump M 내의 각 Controller 및 Pump A 에게 전문을 송신한다. 보낼 전문의 Class Type 및 전문
	 * Command ID 는 다음과 같다.
	 * 
	 * Output POS Adapter : Preamble byte[] Pump Adapter : PumpPreamble
	 * WorkingMessage CAT Module : Preamble byte[] State Module : Preamble
	 * WorkingMessage Sale Module : Preamble byte[]
	 * 
	 * Send Where - PosAController Command ID List from where - POS Protocol DE :
	 * 주유 중 정보 전송 : PumpAController DM : 보관증 발급 조회 : PumpAController DQ : Preset
	 * 자료 전송 : PumpAController
	 *  - Pump Adapter Command ID List - PumpA Protocol PA : 주유 금지/해지 명령 :
	 * PosAController QF : Preset 자료 요청 : PosAController PB : 정액 / 정량 설정 요청 :
	 * PosAController PQ : 보관량 요청 응답 : PosAController
	 * 
	 * P2 : 일반 환경 정보 설정 : PumpAController P3 : 주유기 환경정보 설정 : PumpAController P5 :
	 * ODT 환경정보 설정 (셀프,충전기): PumpAController P6 : 영업일 및 시간 설정 : PumpAController
	 * PT : 충전기 ODT POS 환경정보설정 : PumpAController
	 * 
	 * HD : 소모셀프 - 외상고객 승인 응답 : CATMController HC : 소모셀프 - 카드결제 승인 응답 :
	 * CATMController QM : 다쓰노셀프 - 카드결제승인응답 : CATMController
	 * 
	 * PG : 충전기 - 고객카드 승인 요청 응답 - 중복차량이 아닌 경우 : PumpAController PF : 충전기 - 고객카드
	 * 승인 요청 응답 - 중복차량 존재하는 경우 : PumpAController NAK : 충전기 - 고객카드 승인 요청 응답 -
	 * 존재하지 않는 경우 : PumpAController PI : 충전기 - 신용카드 승인 요청 응답 : CATMController PK :
	 * 충전기 - 보너스 카드 처리 요청 응답 : CATMController PL : 충전기 - 전자상품권 처리 응답 :
	 * CATMController BB : 충전기 - 보너스 점수 누적 응답 : CATMController XA : 충전기 - 현금 영수증
	 * 요청 응답 : CATMController PP : 충전기 - 발행 보관증 번호 응답 : PumpAController PQ : 충전기 -
	 * 보관량 조회 요청 응답 : PumpAController
	 *  - CatMController Command ID List from where - POS Protocol including SMT
	 * 전문 HB : 소모셀프 - 카드결제 승인 요청 : PumpAController HE : 다쓰노셀프 - 카드결제 승인 요청 :
	 * PumpAController SB : 충전기 - 카드결제 승인 요청 : PumpAController BA : 충전기 - 보너스 점수
	 * 누적 요청 : PumpAController TJ : 충전기 - 현금 영수증 요청 : PumpAController
	 *  - StateMController Command ID List from where - PumpA Protocol SE : 주유
	 * 디바이스 이상 정보 전송 : PumpAController S8 : 주유기/충전기 상태 전송 : PumpAController
	 *  - SaleMController Command ID List from where - POS Protocol DG : 주유 완료
	 * 정보 전송 : PumpAController
	 * 
	 * @param preambleData :
	 *            전송할 전문
	 */
	private void sendMessage(Preamble preambleData) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info(
					"SSDC/" + "PUMP_M/" + "PupmAController/" + "sendMessage()");

		int dest = preambleData.getDest();
		switch (dest) {
		case SyncManager.DISE_POS_ADAPTER: {
			getProducer_PumpMPosA_Data().produce(preambleData);
			break;
		}
		case SyncManager.DISE_PUMP_ADAPTER: {
			WorkingMessage workMsg = (WorkingMessage) preambleData.getPreamble();
			if (workMsg != null) {
				workMsg.setDirection(IPumpConstant.DIRECTION_FROM_MODULE);
				getProducer_PumpA_Data().produce(preambleData);
			} else {
				LogUtility.getPumpMLogger().error("[Pump M] Strange content.");
			}
			break;
		}
		case SyncManager.DISE_CMS_MODULE:
		case SyncManager.DISE_CAT_MODULE: {
			LogUtility.getLogger().info(ITopicConstant.TOPIC_PumpM_PumpMCatM_Data);
			LogUtility.getLogger().info(ITopicConstant.TOPIC_PumpAController_CatMController_Data);
			
			
			getProducer_PumpMCatM_Data().produce(preambleData);
			break;
		}
		case SyncManager.DISE_STATE_MODULE: {
			getProducer_PumpMAmsM_Data().produce(preambleData);
			break;
		}
		case SyncManager.DISE_SALE_MODULE: {
			getProducer_PumpMSaleM_Data().produce(preambleData);
			break; // DISE_KH_MODULE_SM
		}
		// PI2, CWI, 2016-03-25, 캠페인 주유정보를 가져오기 위해  새로운 연결통로 생성(PumpAController -> CatMController)
		case SyncManager.DISE_PUMP_MODULE: {
			getProducer_PumpM_Data().produce(preambleData);
			break; 
		}
		case SyncManager.DISE_BEACON_MODULE: {
			getProducer_PumpMBeaconM_Data().produce(preambleData);
			break; 
		}
		}
	}

	public void setAlreadyInitialize(boolean isAlreadyInitialize) {
		this.isAlreadyInitialize = isAlreadyInitialize;
	}

	@Override
	public void start() {
		registerListener() ;
		initData();
	}

	
	/**
	 * Pump A 를 통해서 주유기 초기화 전문을 구성하여 전송한다.
	 * 
	 */
	private void startPumpAdapter() {
		if (logSSDC == true)
			LogUtility.getCATLogger().info(
					"SSDC/" + "PUMP_M/" + "PupmAController/"
							+ "startPumpAdapter()");

		if (isAlreadyInitialize() == true) {
			LogUtility.getPumpMLogger().info("[Pump M] 이미 Pump A 에 초기화 요청 전문이 전송되었습니다. 따라서 더 이상 초기화 요청을 진행하지 않습니다.");
			return ;
		}
		
		setAlreadyInitialize(true);
		
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				String storeCode;
				try {
					storeCode = T_KH_STOREHandler.getHandler().getStoreCode();

					ArrayList<WorkingMessage> preWorkMsg = PumpMUtil
							.createCommonInitPreWorkingMessage(storeCode);
					if ((preWorkMsg == null) || (preWorkMsg.size() == 0)) {
						LogUtility.getPumpMLogger().info("[Pump M] No Pump Device is registered.");
					} else {
						for (int i = 0; i < preWorkMsg.size(); i++) {
							Preamble preamble = PumpMUtil.createWorkingMessagePreamble(SyncManager.getUniqueKey(),
									SyncManager.DISE_PUMP_ADAPTER, 
									preWorkMsg.get(i), "");
							getProducer_PumpA_Init().produce(preamble);
						}

						ArrayList<WorkingMessage> allWorkMsgArray = PumpMUtil.createInitAllWorkingMessage(storeCode, null);
						if ((allWorkMsgArray == null)	|| (allWorkMsgArray.size() == 0)) {
						} else {
							for (int i = 0; i < allWorkMsgArray.size(); i++) {
								Preamble preamble = PumpMUtil.createWorkingMessagePreamble(SyncManager.getUniqueKey(),
										SyncManager.DISE_PUMP_ADAPTER,
										allWorkMsgArray.get(i), "");
								sendMessage(preamble);
							}
						}
					}
				} catch (Exception e) {
					LogUtility.getPumpMLogger().error(e.getMessage(), e);
				} finally {
					LogUtility.getPumpMLogger().info("[Pump M] Completed to send Pump Info");

					// 주유기 초기화 완료되었음을 POS A 로 전송
					LogUtility.getPumpMLogger().info("[Pump M] Send completed message to POS A");
					sendCompletedMessage(true);
				}
			}
		});
		t.start();
	}
	
	@Override
	public void stop() {
		destroyData();
	}
	
	@Override
	protected void onReceivingBeaconMData(Object receiving_beaconm) {
		try {
			Preamble receiveData = (Preamble) receiving_beaconm;
			UPOSMessage upos = (UPOSMessage) receiveData.getPreamble();
			String messageID = receiveData.getKey();
			
			upos.print();
			
			if(IUPOSConstant.MESSAGETYPE_0221.equals(upos.getMessageType())){
				//주문정보 요청
				process0221(messageID, upos);
			} else if(IUPOSConstant.MESSAGETYPE_0223.equals(upos.getMessageType())){
				//preset 요청 
				process0223(messageID, upos);
			} else if(IUPOSConstant.MESSAGETYPE_0228.equals(upos.getMessageType())){
				//차량 이탈
				process0228(messageID, upos);
			} else if (IUPOSConstant.MESSAGETYPE_0229.equals(upos.getMessageType())){
				//preset 취소 요청
				process0229(messageID, upos);
			} else {
				//승인 요청 전문을 처리한다.
				
				/*
	        	 * BEACON 전문 :
	        	 * 	1. PumpA -> PumpA : 현금요청(0011),현금보너스요청(0013), 신용승인응답(0031), 신용+보너스누적응답(0033),
	        	 *                      GS보너스 사용응답(0061), 쿠폰 사용응답(0063),
	        	 * 		- DeviceType   : 3M
	        	 * 2017.06.30
	        	 */
				
				//pos에 결제건 락 요청
				sendLockingInfoToPOS(upos.getNozzle_no(), upos.getPosReceipt_no(), IConstant.PUMP_SALE_LOCKING) ;
				//승인 요청 전문을 CATM로 전송한다.
				Preamble preamble = PumpMUtil.createUPOSMessagePreamble( messageID,
						SyncManager.DISE_BEACON_MODULE,
						SyncManager.DISE_CAT_MODULE, 
						upos, "");

				LogUtility.getLogger().info("[Pump M] <Beacon Process> 결제요청 전문 CAT M 전송");
				sendMessage(preamble);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ARK 에서 받은 주문정보요청을 처리한다.
	 * 1. display ARK에 연결되어있는 주유기 상태 체크
	 * 
	 * 2. 주유기 사용 가능시 주문정보 장애대응에 전송
	 * 2.1 주유기 노즐 정보 추가
	 * 
	 * 3. 주유기 사용 불가시 주유기 사용 불가 전문을 ARK에 전송
	 * 
	 * @param messageID :
	 *            메세지 ID
	 * @param upos :
	 *            주문정보 UPOSMessage
	 */
	private void process0221(String messageID, UPOSMessage upos) throws CloneNotSupportedException {
		BeaconMessage bMsg = ByteArrayToBeaconMessage.createBeaconMessage(upos.getFiller2().getBytes());
		String dArkNo = bMsg.getDisplayArkId() ;
		
		LogUtility.getLogger().info("[Pump M] <Beacon Process> 0221(주문정보 요청) 처리 - Display Ark 정보 Hashmap 저장  dArk No : " + dArkNo) ;
		
		BeaconArkInfo arkInfo = new BeaconArkInfo() ; 
		arkInfo.setArkNo(dArkNo) ;
		arkInfo.setNoz1(bMsg.getNoz1().trim()) ;
		arkInfo.setNoz2(bMsg.getNoz2().trim()) ;
		arkInfo.setNoz3(bMsg.getNoz3().trim()) ;
		arkInfo.setNoz4(bMsg.getNoz4().trim()) ;
		arkInfo.setNoz5(bMsg.getNoz5().trim()) ;
		arkInfo.setNoz6(bMsg.getNoz6().trim()) ;
		arkInfo.setNoz7(bMsg.getNoz7().trim()) ;
		arkInfo.setNoz8(bMsg.getNoz8().trim()) ;
		arkInfo.print(" ") ;
		IBeaconConstant.beaconArkData.put(dArkNo, arkInfo) ;
		
		boolean state = true;
		
		state = checkNozState(dArkNo);
		
		//1. display ark에 연결 되어 있는 주유기 상태 체크
		if(state){
			/* 2. 주유기 사용 가능시
			 * 노즐 정보 설정 후 
			 * 주문 정보 장애대응 전달
			*/
			bMsg.setNozStats("0");
			bMsg.setNoz1(BeaconDataHandler.getNozzleData(arkInfo.getNoz1()));
			bMsg.setNoz2(BeaconDataHandler.getNozzleData(arkInfo.getNoz2()));
			bMsg.setNoz3(BeaconDataHandler.getNozzleData(arkInfo.getNoz3()));
			bMsg.setNoz4(BeaconDataHandler.getNozzleData(arkInfo.getNoz4()));
			bMsg.setNoz5(BeaconDataHandler.getNozzleData(arkInfo.getNoz5()));
			bMsg.setNoz6(BeaconDataHandler.getNozzleData(arkInfo.getNoz6()));
			bMsg.setNoz7(BeaconDataHandler.getNozzleData(arkInfo.getNoz7()));
			bMsg.setNoz8(BeaconDataHandler.getNozzleData(arkInfo.getNoz8()));
			
			upos.setFiller2(new String(BeaconMessageToByteArray.createBeaconMessageByteArray(bMsg)));
			
			//주문정보 요청 장애대응에 전달
			/*Preamble preamble = PumpMUtil.createPreamble(	messageID,
					SyncManager.DISE_PUMP_ADAPTER,
					SyncManager.DISE_CAT_MODULE, 
					upos, "");*/
			
			Preamble preamble = new Preamble() ;
			preamble.setKey(messageID) ;
			preamble.setFrom(SyncManager.DISE_BEACON_MODULE) ;
			preamble.setDest(SyncManager.DISE_CAT_MODULE) ;
			preamble.setPreamble(upos) ;
			preamble.setLog("") ;
			
			LogUtility.getLogger().info("[Pump M] <Beacon Process> Send UPOSMessage 0221 to CAT M");
			sendMessage(preamble);

		}else{
			/* 2. 주유기 사용 불가능시
			 불가능 전문 ARK에 전달
			*/
			
			UPOSMessage upos0222 = upos.clone();
			
			BeaconMessage bMsg0222 = ByteArrayToBeaconMessage.createBeaconMessage(upos.getFiller2().getBytes());
			bMsg0222.setCommand(IPumpConstant.COMMANDID_JB);
			bMsg0222.setDirection("K");
			//노즐 상태
			bMsg0222.setNozStats("1");
			
			byte[] bm_jbFiller2 = BeaconMessageToByteArray.createBeaconMessageByteArray(bMsg0222);
			
			upos0222.setMessageType(IUPOSConstant.MESSAGETYPE_0222);
			upos0222.setFiller2(new String(bm_jbFiller2));
			
			LogUtility.getLogger().info("[Pump M] <Beacon Process> Send UPOSMessage 0222 to pumpA");
			
			sendBeaconMessageToBeaconM(messageID, upos0222);
		}
	}
	
	/**
	 * ARK 에서 받은 차량이탈을 처리한다.
	 * 
	 * 1. AE 전문을 POS에 전송한다.
	 * 2. ACK 전문을 ARK에 전송한다. 
	 * 
	 * @param messageID :
	 *            메세지 ID
	 * @param upos :
	 *            주문정보 UPOSMessage
	 */
	private void process0228(String messageID, UPOSMessage upos) throws CloneNotSupportedException {
		LogUtility.getLogger().info("[Pump M] <Beacon Process>  0228(차량이탈) 처리");
		UPOSMessage upos0220 = upos.clone();
		
		String filler2 = upos.getFiller2();
		BeaconMessage bMsg = ByteArrayToBeaconMessage.createBeaconMessage(filler2.getBytes());
		String msgId = bMsg.getMessageId();
		
		//	1. AE 전문 POS 전송 (상태값 : 292)
		LogUtility.getLogger().info("[Pump M] <Beacon Process> 0228(차량이탈)  - AE POS 전송");
		SE_WorkingMessage seWokringMessage = new SE_WorkingMessage() ;
		seWokringMessage.setNozzleNo(bMsg.getNozNo()); // 노즐번호 
		seWokringMessage.setDeviceType(ICode.EXIST_06_BEACON);
		seWokringMessage.setStatus(IPumpConstant.PUMP_STATECODE_ERROR);
		seWokringMessage.setStatusCode(String.valueOf(IConstant.STATE_ARK_STATECODE_292));
		POS_AE posMessage = new POS_AE(messageID,IConstant.POSPROTOCOL_TYPE_POS,seWokringMessage) ;
		LogUtility.getLogger().info("POS AE 완료");
		Preamble preambleData = PumpMUtil.createPreamble(messageID
				, SyncManager.DISE_POS_ADAPTER, posMessage, "") ;
		LogUtility.getLogger().info("POS AE 프림블 완료");
		/*Preamble preambleData = new Preamble() ;
		preambleData.setKey(messageID) ;
		preambleData.setFrom(SyncManager.DISE_PUMP_MODULE) ;
		preambleData.setDest(SyncManager.DISE_POS_ADAPTER) ;
		preambleData.setPreamble(posMessage) ;
		preambleData.setLog("") */;
		
		sendMessage(preambleData) ;
		
		//  2. ARK(0220 uPosMessage) 전송
		BeaconMessage ackBMsg = new BeaconMessage() ;
		ackBMsg.setCommand(IPumpConstant.COMMANDID_JZ);
		ackBMsg.setDirection("K");
		ackBMsg.setValue("0");	//0:정상 ACK
		ackBMsg.setMessageId(msgId);
		
		upos0220.setMessageType(IUPOSConstant.MESSAGETYPE_0220);
		upos0220.setFiller2(new String(BeaconMessageToByteArray.createBeaconMessageByteArray(ackBMsg)));
		
		sendBeaconMessageToBeaconM(messageID, upos0220);
	}
	
	/**
	 * ARK 에서 받은 preset 취소 요청을 처리한다.
	 * 
	 * 1. 주유기 상태를 체크한다.
	 * 2. 주유기 상태가 노즐업 이거나, 
	 *    주유 중인 경우 ARK에 ACK전문을 전송한다.
	 * 3. 주유기 상태가 노즐업 이거나, 주유 중이 아닌 경우   
	 * 3.1. 주유기 비상정지
	 * 3.2. 주유기 비상정지 해제
	 * 3.3. 주유기 단가 재전송
	 * 3.4. POS에 AE전문 전송
	 * 3.5. ARK에 ACK전문 전송(JZ)
	 * 3.6. 기존 저장하고 있던 주문정보요청 전문 삭제
	 * 
	 * @param messageID :
	 *            메세지 ID
	 * @param upos :
	 *            주문정보 UPOSMessage
	 */
	private void process0229(String messageID, UPOSMessage upos) throws Exception {
		LogUtility.getLogger().info("[Pump M] <Beacon Process> 0229(취소 요청) 처리") ;
		String filler2 = upos.getFiller2();						
		BeaconMessage bMsg = ByteArrayToBeaconMessage.createBeaconMessage(filler2.getBytes());						
		String nozzleNo = bMsg.getNozNo();
		String khproc_no = bMsg.getKhProcs_no();
		String msgId = bMsg.getMessageId();
		UPOSMessage upos0220 = upos.clone();
		
		/*
		 * 노즐업, 주유 중인 경우는 preset 취소 불가
		 */
		int pumpstateCode = StateMController.getPumpStateCode(nozzleNo);
		
		if(pumpstateCode == IConstant.STATE_PUMP_STATECODE_652 
				|| pumpstateCode == IConstant.STATE_PUMP_STATECODE_653 ){	
			
			LogUtility.getLogger().info("[Pump M] <Beacon Process> 취소요청 - 주유기사용중 취소 처리 불가 ") ;
			// 1. NAK(0220 uPosMessage) 전송
			BeaconMessage ackBMsg = ByteArrayToBeaconMessage.createBeaconMessage(filler2.getBytes());
			ackBMsg.setCommand(IPumpConstant.COMMANDID_JZ);
			ackBMsg.setDirection("K");
			ackBMsg.setValue("1");	// 1:NAK
			ackBMsg.setMessageId(msgId);
			
			upos0220.setMessageType(IUPOSConstant.MESSAGETYPE_0220);
			upos0220.setFiller2(new String(BeaconMessageToByteArray.createBeaconMessageByteArray(ackBMsg)));
			
			sendBeaconMessageToBeaconM(messageID, upos0220);
			
		} else {
			
			// 1. 주유기 Preset 해제
			String dIcommand =""; //주유기 비상정지/해제 명령
			POS_DI diMsg = null;
			
			dIcommand ="0"; //주유기 비상정지 명령
			LogUtility.getLogger().info("[Pump M] <Beacon Process> Preset 해제 - 비상정지");
			diMsg = new POS_DI(messageID, nozzleNo, dIcommand);
			LogUtility.getLogger().info(diMsg.toString());
			processDI(messageID, diMsg) ;

			Thread.sleep(500);
			
			dIcommand ="1"; //주유기 비상정지 해제 명령
			LogUtility.getLogger().info("[Pump M] <Beacon Process> Preset 해제 - 비상정지 해제");
			diMsg = new POS_DI(messageID, nozzleNo, dIcommand);
			LogUtility.getLogger().info(diMsg.toString());
			processDI(messageID, diMsg) ;
			
			//노즐 상태 초기화
			//주유기 Type 을 먼저 조사
			T_NZ_NOZZLEData tNozData = T_NZ_NOZZLEHandler.getHandler().getT_NZ_NOZZLEDataByNozzleNo(nozzleNo);
			String self_ind_exist = tNozData.getSelf_ind_exist();
			
			if(ICode.SELF_IND_EXIST_02_SELF_PUMP.equals(self_ind_exist)){
				PumpMTransactionManager.getInstance().setNozzleState(nozzleNo, IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED);
			}else{
				PumpMTransactionManager.getInstance().setNozzleState(nozzleNo, IPumpConstant.KH_PUMP_COMPLETED);
			}
			
			Thread.sleep(500);
			
			// 주유단가를 재 전송 (조건 : Preset 설정이면서, Preset 설정 단가가 계기단가와 다를 경우.)
			//notifyBasePriceToPumpAfterPumpCompletion(khproc_no, nozzleNo);
			sendBasePrice(nozzleNo);
			
			// 일반 취소 : 293 
			// 자동프리셋 취소 : 294
			// AE 전문 POS 전송 (상태값 : 293)
			LogUtility.getLogger().info("[Pump M] <Beacon Process> Preset 해제  - AE(294) POS 전송");
			SE_WorkingMessage seWokringMessage = new SE_WorkingMessage() ;
			seWokringMessage.setNozzleNo(bMsg.getNozNo()); // 노즐번호 
			seWokringMessage.setDeviceType(ICode.EXIST_06_BEACON);
			seWokringMessage.setStatus(IPumpConstant.PUMP_STATECODE_ERROR);
			//프리셋 취소 
			seWokringMessage.setStatusCode(String.valueOf(IConstant.STATE_ARK_STATECODE_294));
			POS_AE posMessage = new POS_AE(messageID, IConstant.POSPROTOCOL_TYPE_POS, seWokringMessage) ;
			Preamble preambleData = PumpMUtil.createPreamble(messageID
					, SyncManager.DISE_POS_ADAPTER, posMessage, "") ;
			
			sendMessage(preambleData) ;
			
			// ARK(0220 uPosMessage) 전송
			BeaconMessage ackBMsg = ByteArrayToBeaconMessage.createBeaconMessage(filler2.getBytes());
			ackBMsg.setCommand(IPumpConstant.COMMANDID_JZ);
			ackBMsg.setDirection("K");
			ackBMsg.setValue("0");	//0:정상 ACK
			ackBMsg.setMessageId(msgId);

			upos0220.setMessageType(IUPOSConstant.MESSAGETYPE_0220);
			upos0220.setPosReceipt_no(khproc_no);
			upos0220.setFiller2(new String(BeaconMessageToByteArray.createBeaconMessageByteArray(ackBMsg)));
		
			sendBeaconMessageToBeaconM(messageID, upos0220);
			
			//주문 정보 Hashmap 삭제 (remove(kh번호), exception 처리)
	    	IBeaconConstant.beaconPumpData.remove(bMsg.getKhProcs_no()) ;
		}
	}
	
	private void process0223(String messageID, UPOSMessage upos) throws Exception {
		LogUtility.getLogger().info("[Pump M] <Beacon Process> 0223(Preset 요청) 처리") ;
		String filler2 = upos.getFiller2();
		
		BeaconMessage bMsg = ByteArrayToBeaconMessage.createBeaconMessage(filler2.getBytes());
		
		//유종을 체크한다.
		boolean chkGoodsCd = BeaconDataHandler.checkGoodsCode(bMsg.getNozNo(), bMsg.getGdsCode()) ;
		
		if(!chkGoodsCd){

			UPOSMessage upos0224 = upos.clone();
			BeaconMessage jdMsg = ByteArrayToBeaconMessage.createBeaconMessage(upos.getFiller2().getBytes());
			
			jdMsg.setCommand(IPumpConstant.COMMANDID_JD);
			jdMsg.setDirection("K");
			jdMsg.setNozStats("3");
			
			upos0224.setMessageType(IUPOSConstant.MESSAGETYPE_0224);
			upos0224.setFiller2(new String(BeaconMessageToByteArray.createBeaconMessageByteArray(jdMsg)));
			LogUtility.getLogger().info("[Pump M] <Beacon Process> Send UPOSMessage 0224 to pumpA");
			
			
			sendBeaconMessageToBeaconM(messageID, upos0224);
			
		} else {
			
			//노즐 상태 체크
			boolean state = checkNozState(bMsg.getDisplayArkId());
			/*
			 * 주유기 상태가 사용 가능인 경우
			 * 셀프 주유기 인 경우는 ODT상태 코드를 한번 더 확인 한다.
			 * 셀프 주유기에서 주유 완료후 영수증 출력 중 preset 설정시
			 * preset 설정 안 되는 현상을 방어하기 위해 추가 
			 * 699(화면이동) 상태 인경우 preset 가능 2017.06.22
			 */
		
			//셀프주유기 종류
			String self_ind_exist = "";
			
			if(state){
				T_NZ_NOZZLEData tNozData = T_NZ_NOZZLEHandler.getHandler().getT_NZ_NOZZLEDataByNozzleNo(bMsg.getNozNo());
				self_ind_exist = tNozData.getSelf_ind_exist();
				
				LogUtility.getLogger().info("[beacon] pumpacontroller 주유기타입 : " + self_ind_exist);
				
				if(self_ind_exist.equals(ICode.SELF_IND_EXIST_02_SELF_PUMP)) {
					LogUtility.getLogger().info("1");
					
					//주유대기
					S8_WorkingMessage wm235 = (S8_WorkingMessage) IBeaconConstant.beaconODTstate.get(bMsg.getNozNo() + "235");
					LogUtility.getLogger().info("2");
					//주유완료
					S8_WorkingMessage wm654 = (S8_WorkingMessage) IBeaconConstant.beaconODTstate.get(bMsg.getNozNo() + "654");
					LogUtility.getLogger().info("3");
					//시작화면 이동
					S8_WorkingMessage wm699 = (S8_WorkingMessage) IBeaconConstant.beaconODTstate.get(bMsg.getNozNo() + "699");
					LogUtility.getLogger().info("4");
					
					long wm235time = 0L;
					long wm699time = 0L;
					long wm654time = 0L;
					
					if(wm235 != null){
						LogUtility.getLogger().info("5 :"+ wm235.getDetectTime());
						wm235time = Long.parseLong(wm235.getDetectTime());
						LogUtility.getLogger().info("5-1" + wm235time);
						
					}
					
					if(wm654 != null){
						LogUtility.getLogger().info("6 :"+  wm654.getDetectTime());
						wm654time = Long.parseLong(wm235.getDetectTime());
						LogUtility.getLogger().info("6-1" + wm654time);
					}
					
					if(wm699 != null){
						LogUtility.getLogger().info("7:"+ wm699.getDetectTime());
						wm699time = Long.parseLong(wm699.getDetectTime());
						LogUtility.getLogger().info("7-1" + wm699time);
					}
					
					if(wm654time <= wm699time && wm235time < wm699time){
						LogUtility.getLogger().info("8");
						LogUtility.getLogger().debug("[Pump M] <Beacon Process> Preset 사용 가능") ;
						state = true;
					}else{
						LogUtility.getLogger().info("9");
						LogUtility.getLogger().debug("[Pump M] <Beacon Process> Preset 사용 불가") ;
						state = false;
					}
				}
			}
			
			LogUtility.getLogger().info("10");
			
			//주유기 상태가 사용 불가일 경우
			if(!state){
				UPOSMessage upos0224 = upos.clone();
				BeaconMessage jdMsg = ByteArrayToBeaconMessage.createBeaconMessage(upos.getFiller2().getBytes());
				
				jdMsg.setCommand(IPumpConstant.COMMANDID_JD);
				jdMsg.setDirection("K");
				jdMsg.setNozStats("1");
				
				upos0224.setMessageType(IUPOSConstant.MESSAGETYPE_0224);
				upos0224.setFiller2(new String(BeaconMessageToByteArray.createBeaconMessageByteArray(jdMsg)));
				LogUtility.getLogger().info("[Pump M] Send UPOSMessage 0224 to pumpA");

				sendBeaconMessageToBeaconM(messageID, upos0224);
			}else{				
				UPOSMessage upos0224 = upos.clone();
				
				String source = IConstant.POSPROTOCOL_TYPE_POS;
				String deviceType = IConstant.POSPROTOCOL_TYPE_NOZZLE;
				String nozzleNo = bMsg.getNozNo();
				String command = bMsg.getPmType();
				String liter = bMsg.getPmLiter().trim();
				String price = bMsg.getPmPrice().trim();
				String presetBaseprice = bMsg.getGdsPrice().trim();
				
				bMsg.print("==>");
				
				//1. 비상정지 설정
				LogUtility.getLogger().info("[Pump M] <Beacon Process> Preset Process - 비상정지");
				String dIcommand =""; //주유기 비상정지/해제 명령
				POS_DI diMsg = null;
				
				dIcommand ="0"; //주유기 비상정지 명령
				diMsg = new POS_DI(messageID, nozzleNo, dIcommand);
				LogUtility.getLogger().info(diMsg.toString());
				processDI(messageID, diMsg) ;

				Thread.sleep(500);
				
				//2. 비상정지 해제
				LogUtility.getLogger().info("[Pump M] <Beacon Process> Preset Process - 비상정지 해제");
				dIcommand ="1"; //주유기 비상정지 해제 명령
				diMsg = new POS_DI(messageID, nozzleNo, dIcommand);
				LogUtility.getLogger().info(diMsg.toString());
				processDI(messageID, diMsg) ;
				
				Thread.sleep(500);
				
				//3. preset 설정
				LogUtility.getLogger().info("[Pump M] <Beacon Process> Preset Process - Preset 요청");
				//정액/정량 설정 요청	
				String udtYn = PropertyManager.getSingleton().getProperty(PropertyManager.OWIN_PRESET_CODE_UPDATE_YN, PropertyManager.OWIN_PRESET_CODE_UPDATE_YN_DEFALUT);
				
				/*
				 * Preset 요청 기본 값(0: 정액 / 1: 정량)
				 * 
				 * kixxhub.properties 파일에 kh.beacon.update_yn 설정값에 따라 0일경우 기존과 동일, 1일경우 (2: 정액 / 3: 정량) 으로 보냄
				 * 2 또는 3으로 보낼경우 Owin 결제건으로 인식하여 미만주유 발생시 영수증 상에 거스름 돈 표시되지 않게 처리 됨
				 * 
				 * */
				if(!PropertyManager.OWIN_PRESET_CODE_UPDATE_YN_DEFALUT.equals(udtYn)){
					//==>> soon 20211104 셀프 주유기 일 경우에만 0->2 또는 1->3으로 변경
					if(self_ind_exist.equals(ICode.SELF_IND_EXIST_02_SELF_PUMP)){
						
						if(command.equals(ICode.PRESET_QTY_PRC_IND_0_PRICE)) {
							command = ICode.PRESET_QTY_PRC_IND_OWIN_PRICE;	   //OWIN 정액 설정
						} else {
							command = ICode.PRESET_QTY_PRC_IND_OWIN_LITER;	;  //OWIN 정량 설정						
						}
					}
					
					LogUtility.getLogger().info("[Pump M] <Beacon Process> Preset Process - Preset 정액(0,2)/정량(1,3) : ["+command+"]");
				} 
				
				POS_DK dkMsg = new POS_DK(source, messageID, deviceType, nozzleNo, command, liter, price, presetBaseprice);
				
				PB_WorkingMessage pbWorkMsg = PumpMUtil.createPB_WorkMsg(dkMsg) ;
				
				Preamble pumpPreamble = null ;
				String nozID = dkMsg.getDeviceID() ;
				
				LogUtility.getLogger().info("[Pump M] <Beacon Process> Receive Fixed Price/Liter Pump from Beacon. NozID="+nozID) ;
				
				String khproc_no = 
					PumpMTransactionManager.getInstance().getKHTransactionID(pbWorkMsg.getNozzleNo(), IPumpConstant.KH_PUMP_PRESET) ;
				PumpMTransactionManager.getInstance().setPresetInfo(pbWorkMsg.getNozzleNo(), IPumpConstant.PRESET_FROM_POS) ;
		
				// 바코드 설정
				// 새로운 바코드 로직으로 barcode 셋팅
				pbWorkMsg.setBarCode(Barcode.getBarcodeNumber("E", pbWorkMsg.getPrice(), nozID, khproc_no, null, null, null));
				
				try {
					T_KH_PUMP_TRHandler.getHandler().updatePresetInfo_BY_khproc_no(khproc_no, 
							dkMsg.getCommand(), dkMsg.getLiter(), dkMsg.getPrice(), dkMsg.getPreset_baesPrice()) ;
				} catch (Exception e) {
					LogUtility.getLogger().error(e.getMessage(),e) ;
				}
				pumpPreamble = PumpMUtil.createWorkingMessagePreamble(messageID,SyncManager.DISE_PUMP_ADAPTER, pbWorkMsg, "") ;
				LogUtility.getLogger().info("[Pump M] <Beacon Process> Request Fixed Price/Liter to Pump A. NozID="+nozID) ;
				sendMessage(pumpPreamble) ; 
				
				// 3. 주문 정보 Hashmap 저장
				// ARK에 주문시작, 주문완료 전문을 전송하기 위해 preset 요청 전문을 HashMap에 저장한다. 
				LogUtility.getLogger().info("[Pump M] <Beacon Process> Preset Process - 주문번호 저장");
				
				BeaconPumpInfo beaconPumpInfo = new BeaconPumpInfo();
				beaconPumpInfo.setUpos(upos);
				IBeaconConstant.beaconPumpData.put(khproc_no,beaconPumpInfo) ;

				processHF(messageID, pbWorkMsg.getMessageID(), khproc_no, pbWorkMsg);
				LogUtility.getLogger().info("[Pump M] <Beacon Process> Respond Preset to POS");
				
				//	4. 0224 uPosMessage BeaconM 전송 
				BeaconMessage jdMsg = ByteArrayToBeaconMessage.createBeaconMessage(upos.getFiller2().getBytes());
				jdMsg.setCommand(IPumpConstant.COMMANDID_JD);
				jdMsg.setDirection("K");
				jdMsg.setNozStats("0");
				jdMsg.setKhProcs_no(khproc_no);
				jdMsg.setNozNo(nozzleNo);
				
				upos0224.setMessageType(IUPOSConstant.MESSAGETYPE_0224);
				upos0224.setPosReceipt_no(khproc_no);
				upos0224.setFiller2(new String(BeaconMessageToByteArray.createBeaconMessageByteArray(jdMsg)));
				
				LogUtility.getLogger().info("[Pump M] <Beacon Process> Send UPOSMessage 0224 to pumpA");
				
				sendBeaconMessageToBeaconM(messageID, upos0224);
			}
		}
	}
	
	/**
     * POS 로 부터 주유제어 명령을 받아서 이를 Pump A 에게 전송한다.
     * 주유제어 명령은 다음과 같이 구분된다.
     * 	Command
     * 		0 : 주유(충전) 금지
     * 		1 : 주유(충전) 금지 해제
     * 		2 : 전체 주유(충전) 금지
     * 		3 : 전체 주유(충전) 금지 해제
     * 
     * @param uniqueKey	: Unique Key
     * @param diMsg		: 주유제어 명렁 전문 (DI 전문)
     */
    private void processDI(String uniqueKey, POS_DI diMsg) {
		if (logSSDC==true) LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PosAController/" + "processDI()");
		
		Preamble pumpPreamble = null ;
		String[] nozIDList = null ;
		
		switch (Integer.parseInt(diMsg.getCommand())) {
			case IConstant.POSPROTOCOL_DI_PUMP_LOCK :
			case IConstant.POSPROTOCOL_DI_PUMP_UNLOCK : {
				PA_WorkingMessage paWorkMsg = PumpMUtil.createPA_WorkMsg(diMsg) ;
				
				pumpPreamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey,
						SyncManager.DISE_PUMP_ADAPTER, paWorkMsg, "") ;
				sendMessage(pumpPreamble) ;
				break ;
			}
			case IConstant.POSPROTOCOL_DI_PUMP_ALL_LOCK : {
				try {
					nozIDList = T_NZ_NOZZLEHandler.getHandler().getNozIDWithoutSelfODT() ;
				} catch (Exception e) {
					LogUtility.getPumpMLogger().error(e.getMessage(),e);
					return ;
				}
				if (nozIDList != null) {    						
			        for (int i = 0 ; i < nozIDList.length ; i++) {
			    		PA_WorkingMessage paWorkMsg = PumpMUtil.createPA_WorkMsg(diMsg) ;
			        	paWorkMsg.setNozzleNo(nozIDList[i]) ;
						paWorkMsg.setNozzleState(Integer.toString(IConstant.POSPROTOCOL_DI_PUMP_LOCK)) ;

    					pumpPreamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey,SyncManager.DISE_PUMP_ADAPTER, paWorkMsg, "") ;
    					sendMessage(pumpPreamble) ;
			        }
				}
				break ;
			}
			case IConstant.POSPROTOCOL_DI_PUMP_ALL_UNLOCK: {
				try {
					nozIDList = T_NZ_NOZZLEHandler.getHandler().getNozIDWithoutSelfODT() ;
				} catch (Exception e) {
					LogUtility.getPumpMLogger().error(e.getMessage(),e);
					return ;
				}
				if (nozIDList != null) {    						
			        for (int i = 0 ; i < nozIDList.length ; i++) {
			    		PA_WorkingMessage paWorkMsg = PumpMUtil.createPA_WorkMsg(diMsg) ;
			        	paWorkMsg.setNozzleNo(nozIDList[i]) ;
						paWorkMsg.setNozzleState(Integer.toString(IConstant.POSPROTOCOL_DI_PUMP_UNLOCK)) ;

    					pumpPreamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey,SyncManager.DISE_PUMP_ADAPTER, paWorkMsg, "") ;
    					sendMessage(pumpPreamble) ;
			        }
				}
				break ;
			}
		}
    }    
    
   /**
	 * 통합 UPOS전문 filler2에 주유 시작 전문을 생성한다.
	 * beaconMessage command JF을 생성한다.
	 * 
	 * 주유기 중 몇몇 주유기가 주유 시작 전문을 Kixxhub에서 전달해 주지 
	 * 않아서 주유 중 전문을 수신하면 주유 시작 전문을 ARK에 보냈는지 확인 후
	 * 주유 시작 전문을 생성해서 ARK에 전달한다.
	 * 
	 * @param messageID :
	 *            메세지 ID
	 * @return khproc_no :
	 *            KH 처리번호
	 */
	private void processBeaconS3(String messageID, String khproc_no) throws CloneNotSupportedException {
		//kixxhub 처리번호가 존재하면 Beacon에 전송
		if(IBeaconConstant.beaconPumpData.containsKey(khproc_no)){
			BeaconPumpInfo beaconPumpInfo = (BeaconPumpInfo) IBeaconConstant.beaconPumpData.get(khproc_no);
			
			//주유 시작전문을 이미 전송했는지 확인한다.
			if(!beaconPumpInfo.isPumpingSJalreadySent()){
				LogUtility.getLogger().info("[Pump M] <Beacon Process> preset 자료 존재 " + khproc_no) ;
			
				//이미 시작 데이터를 보낸 경우 보내지 않음
				UPOSMessage uposTemp = (UPOSMessage)beaconPumpInfo.getUpos();
				
				//주유 시작 upos 생성
				UPOSMessage upos = makeBeaconPumpingStart(uposTemp);
				
				//주유 시작 전문 전송으로 설정
				beaconPumpInfo.setPumpingSJalreadySent(true);
				
				LogUtility.getLogger().info("[Pump M] <Beacon Process> 주유시작 자료 ProxyArk 전송 " + khproc_no) ;

				sendBeaconMessageToBeaconM(messageID, upos);
			}
		}
	}
	
	/**
	 * Beacon 매장용 주유시작 전문을 생성한 후 ARK에 전문을 전달한다.
	 * 
	 * @param messageID :
	 *            메세지 ID
	 * @param khproc_no :
	 *            KH 처리번호
	 */
	private void processBeaconPumpingStart(String messageID, String khproc_no) throws CloneNotSupportedException {
		//kixxhub 처리번호가 존재하면 Beacon에 전송
		if(IBeaconConstant.beaconPumpData.containsKey(khproc_no)){
			LogUtility.getLogger().info("[Pump M] <Beacon Process> 확인 - 주유시작자료 0225 생성 : K/H처리번호 : " + khproc_no) ;
			
			BeaconPumpInfo beaconPumpInfo = (BeaconPumpInfo) IBeaconConstant.beaconPumpData.get(khproc_no);
			UPOSMessage uposTemp = (UPOSMessage)beaconPumpInfo.getUpos();
			
			uposTemp.setPosReceipt_no(khproc_no);
			//주유 시작 upos 생성
			UPOSMessage upos = makeBeaconPumpingStart(uposTemp);

			beaconPumpInfo.setPumpingSJalreadySent(true);
			
			sendBeaconMessageToBeaconM(messageID, upos);
			
		}
	}
	
	/**
	 *
	 * 주유 완료 전문을 ARK에 전송한다. 
	 * 
	 * @param messageID :
	 *            메세지 ID
	 *        khproc_no :
	 *            KH 처리번호
	 *        s4WorkMsg :
	 *            주유기에서 받은 주유 완료 전문 
	 */
	private void sendS4BeaconMessage(String messageID, String khproc_no, S4_WorkingMessage s4WorkMsg) throws Exception {
		//kixxhub 처리번호가 존재하면 Beacon에 전송
		LogUtility.getLogger().info("sendS4BeaconMessage");
		if(IBeaconConstant.beaconPumpData.containsKey(khproc_no)){
			LogUtility.getLogger().info("IBeaconConstant.beaconPumpData.containsKey(khproc_no)");
			UPOSMessage_ItemInfo_Item itemInfoItem = null;
			boolean completed = true;
			T_KH_PUMP_TRData pumpTrData = null;
			boolean isCatPreset = false;
			UPOSMessage sendUPOSMsg = null;
			BeaconMessage bm_jg = null;
			
			BeaconPumpInfo beaconPumpInfo = (BeaconPumpInfo) IBeaconConstant.beaconPumpData.get(khproc_no);
			UPOSMessage uposTemp = (UPOSMessage)beaconPumpInfo.getUpos();
			UPOSMessage upos = uposTemp.clone();
			
			upos.setNozzle_no(s4WorkMsg.getNozzleNo());
			
			//주유 완료 자료를 불러온다.
			SqlSession session = null;
			session = SqlSessionFactoryManager.openSqlSession();
			
			T_KH_PUMP_TRData[] rlt = T_KH_PUMP_TRHandler.getHandler().getT_KH_PUMP_TRData(session, khproc_no) ;
			
			SqlSessionFactoryManager.closeSqlSession(session);
			
			//주유 완료 자료를 확인한다.
			if(rlt == null || rlt.length < 1){
				LogUtility.getLogger().info("[Pump M] <Beacon Process> No T_KH_PUMP_TRData was found. khproc_no : " + khproc_no) ;
				return;
			}
			pumpTrData = rlt[0];
			
			//상품 정보를 만든다.
			itemInfoItem = UPOSUtil.getUPOSMessage_ItemInfo_Item(pumpTrData, isCatPreset);

			//주유완료 전문을 만든다.
			sendUPOSMsg = UPOSUtil.process4201_4291(upos, itemInfoItem, completed);
			
			//beacon 주유완료 전문으로 설정한다.
			sendUPOSMsg.setMessageType(IUPOSConstant.MESSAGETYPE_0227);
			
			//Beacon메세지를 만든다.
			bm_jg = ByteArrayToBeaconMessage.createBeaconMessage(upos.getFiller2().getBytes());

			bm_jg.setCommand("JG");
			bm_jg.setDirection("K");
			
			//노즐번호 -> preset 정보 사용
			//OTI값 -> preset 정보 사용 
			//주문번호 -> preset 정보 사용
			//차량번호 -> preset 정보 사용
			
			//주유완료시간
			bm_jg.setPmTime(pumpTrData.getOil_datetime_to());
			//유종
			bm_jg.setGdsCode(pumpTrData.getGoods_code());
			//단가
			bm_jg.setGdsPrice(pumpTrData.getBaseprice());
			//주유 수량
			bm_jg.setPmLiter(pumpTrData.getEqpm_qty());
			//주유 금액
			bm_jg.setPmPrice(pumpTrData.getEqpm_amt_prc());
				
			byte[] bm_jgFiller2 = BeaconMessageToByteArray.createBeaconMessageByteArray(bm_jg);
		
			sendUPOSMsg.setFiller2(new String(bm_jgFiller2));
		    
			sendBeaconMessageToBeaconM(messageID, sendUPOSMsg);
		    
			IBeaconConstant.beaconPumpData.remove(khproc_no);
		    
		    //1일 지난 주문 요청 삭제
		    Enumeration<String> beaconPumpDatakeys = IBeaconConstant.beaconPumpData.keys();
		    
		    while(beaconPumpDatakeys.hasMoreElements()){
		    	String beaconPumpDatakey = beaconPumpDatakeys.nextElement();
		    	BeaconPumpInfo beaconData = (BeaconPumpInfo) IBeaconConstant.beaconPumpData.get(beaconPumpDatakey);	
		    	
		    	if(System.currentTimeMillis() - beaconData.getCreateTimeMillis() > 60 * 60 * 1000 * 24){
		    		IBeaconConstant.beaconPumpData.remove(beaconPumpDatakey);
		    	}
		    }
		    
		}
	}
	
	/**
	 * 통합 UPOS전문 filler2에 주유 시작 전문을 생성한다.
	 * beaconMessage command JF을 생성한다.
	 * 
	 * @param uposTemp :
	 *            주문 정보 요청 전문
	 * @return UPOSMessage :
	 *            주유 시작 전문
	 */
	private UPOSMessage makeBeaconPumpingStart(UPOSMessage uposTemp) throws CloneNotSupportedException {
		UPOSMessage upos = uposTemp.clone();
		upos.setMessageType(IUPOSConstant.MESSAGETYPE_0225);
		upos.setCamp_info(null);
		upos.setItem_info(null);
		upos.setTradeCondition(null);
		
		BeaconMessage bm_jf = null;
		
		bm_jf = ByteArrayToBeaconMessage.createBeaconMessage(upos.getFiller2().getBytes());

		bm_jf.setCommand("JF");
		bm_jf.setDirection("K");
		//노즐 번호 -> preset 자료 사용
		//OTI값 -> preset 자료 사용
		//주문번호 -> preset 자료 사용 
		//차량번호 -> preset 자료 사용
		//주유시작시간 
		bm_jf.setPmTime(GlobalUtility.getDateYYYYMMDDHHMMSS());					
		
		byte[] bm_jfFiller2 = BeaconMessageToByteArray.createBeaconMessageByteArray(bm_jf);

		upos.setFiller2(new String(bm_jfFiller2));
		
		return upos;
	}
	
	/**
	 * beacon, Nozzle state 양일준  
	 * 
	 * @param arkNo : display ARK 번호
	 * @return boolean : 주유기 사용 가능 여부
	 */
	public static boolean checkNozState(String arkNo){
		
		boolean canPreset = false ; 
		
		T_NZ_NOZZLEHandler t_nz_nozzleHandler = null ;
		
		try{
			t_nz_nozzleHandler = T_NZ_NOZZLEHandler.getHandler() ; 
		}catch(Exception e){
			e.printStackTrace() ;
		}
		
		BeaconArkInfo arkInfo = (BeaconArkInfo) IBeaconConstant.beaconArkData.get(arkNo) ;
		
		String[] nozArr = new String[8];
		nozArr[0] = arkInfo.getNoz1();
		nozArr[1] = arkInfo.getNoz2();
		nozArr[2] = arkInfo.getNoz3();
		nozArr[3] = arkInfo.getNoz4();
		nozArr[4] = arkInfo.getNoz5();
		nozArr[5] = arkInfo.getNoz6();
		nozArr[6] = arkInfo.getNoz7();
		nozArr[7] = arkInfo.getNoz8();
		
		for(int i=0 ; i<nozArr.length ; i++){
			if(!(nozArr[i] == null || "".equals(nozArr[i]))){
				String nozNo = nozArr[i] ;
				String self_ind_exist = t_nz_nozzleHandler.getSelf_ind_exist(nozNo) ;
				
				// Nozzle state Check 
				LogUtility.getLogger().info("[Pump M] <Beacon Process> 비콘결제 노즐 상태 확인 - 노즐번호 : " + nozNo + ", 노즐타입 : " + self_ind_exist) ;
				
				if(StateMController.ckPumpState(nozNo) && PumpMTransactionManager.getInstance().isPresetState(nozNo, self_ind_exist)){
					LogUtility.getLogger().info("[Pump M] <Beacon Process> " + nozNo + "번 노즐 사용 가능") ;
					canPreset = true ;
				} else {
					LogUtility.getLogger().info("[Pump M] <Beacon Process> Nozzle No."+ nozNo+ " cannot be used. canPreset = " + canPreset) ;
					canPreset = false ;
					break ;
				}	
			}
		}
		
		return canPreset ;
	}
	
	/**
	 * Beacon으로 부터 Preset 정보를 받아서 다음을 수행한다. 
	 * 1. T_KH_PUMP_TR Table update 
	 * 2. POS 에 Preset 정보 전송
	 * 
	 * @param uniqueKey :
	 *            Preamble Object 의 Unique Key
	 * @param messageID :
	 *            CAT으로 부터 온 전문 Message ID
	 * @param pbWorkMsg :
	 *            CAT으로로 부터 온 전문
	 */
	
	private void processHF(String uniqueKey, String messageID, String khproc_no, PB_WorkingMessage pbWorkMsg) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/" + "processHF()");
		
		LogUtility.getLogger().info("[Pump M] <Beacon Process> processHF") ;
		String commandID = null;

		String type = "0" ;
		String price = "0" ;
		String liter = "0" ;
		price = PumpMUtil.convertPriceFromPumpToPOS(pbWorkMsg.getPrice()) ;
		String base_price = PumpMUtil.convertBasePriceFromPumpToPOS(pbWorkMsg.getBasePrice()) ;
		String nozzleNo = pbWorkMsg.getNozzleNo();	
		
		POS_DQ dqPumpMMsg = new POS_DQ(messageID, nozzleNo, khproc_no, type, price, liter, base_price);
		
		if (khproc_no == null) {
			khproc_no = PumpMTransactionManager.getInstance().getKHTransactionID(pbWorkMsg.getNozzleNo(), IPumpConstant.KH_PUMP_PRESET);
		}
		
		commandID = IConstant.POSPROTOCOL_COMMANDID_DQ;

		dqPumpMMsg.setTransactionID(khproc_no);
		dqPumpMMsg.setCommandID(commandID);
		dqPumpMMsg.setDeviceType(IConstant.POSPROTOCOL_TYPE_ARK);
		

		String preset_qty_prc_ind = dqPumpMMsg.getType();
		String preset_qty = dqPumpMMsg.getLiter();
		String preset_prc = dqPumpMMsg.getPrice();
		String preset_basePrice = dqPumpMMsg.getBase_price();
		try {
			T_KH_PUMP_TRHandler.getHandler().updatePresetInfo_BY_khproc_no(khproc_no, 
					preset_qty_prc_ind, 
					preset_qty, 
					preset_prc,
					preset_basePrice);
		} catch (Exception e) {
			LogUtility.getLogger().error(e.getMessage(), e);
		}
		Preamble dqPreamble = PumpMUtil.createPreamble(messageID,	SyncManager.DISE_POS_ADAPTER, dqPumpMMsg, "");

		try {
			// 2009년 4월 21일 추영대 추가.
			GlobalUtility.printAnalysisLog("pumpM", khproc_no, pbWorkMsg.getNozzleNo(), "", "프리셋", "", dqPumpMMsg.convertPOSContent(), "", "", "");

		} catch (Exception e) {
			LogUtility.getLogger().error(	"[Pump M] !!!!!!!!!!AnalysisLOG 출력 실패!!!!!!!!!!!", e);
		} // end try

		sendMessage(dqPreamble);
	}
	
	 /**
     * BEACON 로부터의 승인 요청에 대한 응답 전문을 Pump A 로 보낸다.
     * 
     * @param syncUnique	: Unique Key
     * @param uPosMsg		: 응답 전문
     */
    private void sendBeaconMessageToBeaconM(String syncUnique, UPOSMessage uPosMsg) {
    	
    	BeaconMessage beaconMsg = BeaconUtility.createBeaconMessageFromUPOSMessage(syncUnique,uPosMsg);
		
    	// Pump A 로 WorkingMessage 를 전송한다.
		Preamble pumpPreamble = PumpMUtil.createBeaconMessagePreamble(syncUnique,
				SyncManager.DISE_BEACON_MODULE, beaconMsg , "") ;
		LogUtility.getLogger().info("[Pump M] <Beacon Process> PumpM -> BeaconM 전송 : " + uPosMsg.getMessageType()) ;
		sendMessage(pumpPreamble) ;
    }
    
	/**
    * 
    * <pre>
    * 1. 메소드명 : insertTRData
    * 2. 작성일 : 2021. 4. 07. 오후 15:48:31, PI2.
    * 3. 작성자 : 양일준
    * 4. 설명 :   TR자료를 DB 에 저장한다.
    * 5. 변경이력:
    * </pre>
    * @param session
    * @param inputPreamble
    * @param khManageNo
    * @param messageID
    * @param seqNo
    * @param reponseType
    * @param send_ind
    * @return
    */
	private boolean insertTRData(SqlSession session, byte[] inputPreamble, String khManageNo, String messageID, 
			String seqNo, String reponseType, String send_ind){

		if (logSSDC==true) LogUtility.getSCMLogger().info("SSDC/" + "KH_M/" + "pumpAController/" + "insertTRData()");
		
    	boolean isInsert = false;
    	
    	try {
    		T_KH_SALES_INFOData data = new T_KH_SALES_INFOData();
    		
    		data.setKhproc_no(khManageNo);
    		data.setMessage_id(messageID);
    		data.setKhseq_no(seqNo);
    		data.setProc_type(reponseType);
    		data.setPreamble(inputPreamble);
    		data.setProc_time(GlobalUtility.getDateYYYYMMDDHHMMSS());
    		data.setSend_ind(send_ind);
    		
    		LogUtility.getSCMLogger().debug("[Pump A Controller] TR 데이터를 저장한다. khManageNo=" + khManageNo + "#messageID=" + messageID + 
    				"#seqNo=" + seqNo  + "#reponseType=" + reponseType );
    		isInsert = T_KH_SALES_INFOHandler.getHandler().insertT_KH_SALES_INFOData(session, data);
		} catch (Exception e) {
			LogUtility.getSCMLogger().error(e.getMessage(),e) ;
		} 
		return isInsert;
    }

}