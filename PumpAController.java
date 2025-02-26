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
	 * Pump M Component �� �����Ǹ鼭 Clear �� �۾����� �����Ѵ�.
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
	 * Pump A �κ��� �� ������ ���ؼ� ���������� ó���ؾ� �ϴ� �κе� �ܿ� ������ Ư���� ���� �߰������� �ؾ� �� �ϵ���
	 * �����Ѵ�.
	 * 
	 * @param uniqueKey :
	 *            Unique Key
	 * @param nozzleNo :
	 *            ���� ��ȣ
	 * @param commandID :
	 *            Pump A �� ���� ���� Command ID
	 * @param workMsg :
	 *            Pump A �� ���� ���� ����
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
					 * [2008.11.26] by WooChul Jung �پ��� ������ ��� �����Ϸ��� �ǹ̴� �ſ�
					 * �߿��ϴ�. ���� �����Ϸᰡ �ѹ��̻� ���� ù��° �͸� ó���ϰ� �������� ������. ��ĩ �߸��ϸ� ����
					 * ���� ������ ���� �� ������ �ִ�.
					 */

					if (DasNoSelfPumpingManager.getInstance().isPumpingCompleted(nozzleNo)) {
						LogUtility.getPumpMLogger().warn("[Pump M] DasNo : Receive S4 more than one time. Drop S4");
						return;
					}

					// �����Ϸ��� ���
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
					 * [2008.11.20] �Ҹ��� ODT �κ��� ���� ���� ���� POS �� ���� Preset ���� ����
					 * ������ ���� �� �ִ�. �� ��� preUposMsg �� ������ UPOSMessage �̸�, �̷� ����
					 * ������ ����� �ִ�. �Ʒ� ����� �Ѱ��� �� ���� POS �� ������ Preset �̶� �����Ѵ�. 1.
					 * ��������� POS �� ���� Preset �� ��� -> �� ��츸 ó���Ѵ�. 2. �� �����ǰ� ���õ�
					 * ���� ������ ���� ��� 3. �� �����ǰ� ���õ� ���� �ֱ��� ���� ������ �����̳� ȸ�� �ҷ��� ���
					 * (ledCode �� 1�� �ƴѰ��) 4. LED Code �� ���� ���
					 */

					if (PumpMTransactionManager.getInstance().isPresetFromPOS(nozzleNo)) {
						// POS �� ������ ������ ����
						LogUtility.getPumpMLogger().info("[Pump M] Pumping from POS Preset. NozID="	+ nozzleNo);

						if (pumpTrData.getPreset_qty_prc_ind().equals(ICode.PRESET_QTY_PRC_IND_0_PRICE)) {
							// ���� ������ ���
							LogUtility.getPumpMLogger().info("[UPOSUtil] Preset with price.");
							prePayedpriceInt = Double.parseDouble(pumpTrData.getPreset_prc());
						} else if (pumpTrData.getPreset_qty_prc_ind().equals(ICode.PRESET_QTY_PRC_IND_1_LITER)) {
							// ���� ������ ���
							LogUtility.getPumpMLogger().info("[UPOSUtil] Preset with liter");
							prePayedliterInt = Double.parseDouble(pumpTrData.getPreset_qty());
							prePayedbasePriceInt = Double.parseDouble(pumpTrData.getPreset_baseprice());
							prePayedpriceInt = GlobalUtility.multiple(	prePayedliterInt, prePayedbasePriceInt);
						}

						// POS �� ���� ���� Preset ��ŭ �����Ǿ��ٶ�� �����Ѵ�.
						prePayedPrice = GlobalUtility.getStringValue(prePayedpriceInt);

						// QL (������ ����) �� �����ؼ� Pump A �� �����Ѵ�.
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

						// ������ ���� ������ Pos A�� �����Ѵ�.
						processOPTBarcode(qlMsg, khproc_no);

					} else if (IConstant.reRequest	&& "0".equals(pumpingPrice)) {
						// ODT �� ������ �����̸鼭 �����ݾ��� 0 ���� ���
						LogUtility.getPumpMLogger().info("[Pump M] Payment From ODT. But Pumping Price is 0");
						// �� ������ BL üũ�� ���� ��� ������ ������ �ʴ´�.
						boolean isCash = DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzleNo).isCash();

						if (DasNoSelfPumpingManager.getInstance().getCurrentOption(nozzleNo) == IConstant.FULL_PUMPING_OPTION_0) {

							// QL (������ ����) �� �����ؼ� Pump A �� �����Ѵ�.
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

							// �����̸鼭 �����ݾ��� 0���� ���
							if (isCash) {
								//	20160728 PI2 twlee
								//	���ݰ����̸鼭 �����ݾ��� 0���̸� �پ���� POS�� 0012������ �������� �Ҹ𿡼��� ������ �ʴ´�.
								//	�پ���� �Ҹ��� 0012���� ���� ���̿� ���� ��Ȯ�� ������ �� �� ����.
								LogUtility.getPumpMLogger().info("[Pump M] ���� �����̸鼭 �����ݾ��� 0���� ��� POS�� �����Ϸ�� UPOS������ �����Ѵ�.");

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
								// ���� ������ ��� (�����ݾ��� 0��)
								DasNoSelfPumpingManager.getInstance().changeDasNoODTNozzleInfoWithOption7(nozzleNo);
							} else {
								// �Ϲ� ������ ��� (�����ݾ��� 0��)
								DasNoSelfPumpingManager.getInstance().changeDasNoODTNozzleInfoWithOption5(nozzleNo);
							}
							UPOSMessage uPosMsg = DasNoSelfPumpingManager.getInstance().getUPOSMessageAfterPumping(nozzleNo, s4WorkMsg, khproc_no);
							
							LogUtility.getPumpMLogger().info("[Pump M] Send UPOSMessage to Cat M. NozID="+ nozzleNo);	
							
							Preamble preamble = PumpMUtil.createUPOSMessagePreamble(uniqueKey, SyncManager.DISE_PUMP_ADAPTER, SyncManager.DISE_CMS_MODULE, uPosMsg, "");
							sendMessage(preamble);
						}

					} else {
						// ODT �� ������ �����̸鼭 �����ݾ��� 0�� �ƴ� ���
						LogUtility.getPumpMLogger().info("[Pump M] Payment From ODT. NozID=" + nozzleNo);

						/*
						 * �پ��� ����/�ܻ� ���� �߰�. edited by ykjang // ���� ������ �����Ϸ�
						 * �Ŀ� ���ʽ� ������ ���ݿ����� ó���� �Ѵ�.
						 */
						boolean isCash = DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzleNo).isCash();
						boolean isCustCard = DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzleNo).isCustCard();
						boolean isCashCard = DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzleNo).isCashCard();
						boolean isBonusCard = DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzleNo).isBonusCard();
						boolean isCreditCard = DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzleNo).isCreditCard();

						if (isCustCard && !isCreditCard && !isCash) {
							// �ܻ� �ŷ�ó
							// �ܻ��� ���ʽ��ŷ��� ���������� �Ѵ�. BY GSC IT ��ȹ��
							// ��� ���ʽ� ������ �ŷ������� �����Ѵ�. ���޻���� ���ȣ���� 2010.11.17
							UPOSMessage uPosMsg = DasNoSelfPumpingManager.getInstance().getUPOSMessageAfterPumpingOiSang(nozzleNo, s4WorkMsg, khproc_no);

							if (IUPOSConstant.MESSAGETYPE_0083.equals(uPosMsg.getMessageType())) {
								// ���ʽ� ī�尡 ������ ���ʽ� �����ؾߵ�.
								LogUtility.getPumpMLogger().info("[Pump M] �ܻ� ���ʽ� ���� ��û . K/Hó����ȣ=" + uPosMsg.getPosReceipt_no());

								Preamble preamble = PumpMUtil.createUPOSMessagePreamble(	uniqueKey,	SyncManager.DISE_PUMP_ADAPTER, SyncManager.DISE_CAT_MODULE, uPosMsg,"");
								sendMessage(preamble);
							} else {
								LogUtility.getPumpMLogger().info("[Pump M] Send UPOSMessage to SalesM. NozID="	+ nozzleNo);
								Preamble preamble = PumpMUtil.createUPOSMessagePreamble(	uniqueKey,	SyncManager.DISE_PUMP_MODULE,SyncManager.DISE_SALE_MODULE, uPosMsg,"");
								sendMessage(preamble);

								PumpMTransactionManager.getInstance().setPayed(uPosMsg.getMessageType(),	uPosMsg.getNozzle_no());
								// ������ ���
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
							// �ſ�ī��
							if (DasNoSelfPumpingManager.getInstance().isCurrentFullPumping(nozzleNo)) {
								// ������ ���
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

									// ������ ���� ������ Pos A�� �����Ѵ�.
									processOPTBarcode(qlMsg, khproc_no);
								} else {
									// ODT �� ������ �����̸鼭 ���� ������ ���
									LogUtility.getPumpMLogger().info("[Pump M] Full Pumping. NozID="	+ nozzleNo);
									UPOSMessage uPosMsg = DasNoSelfPumpingManager.getInstance().getUPOSMessageAfterPumping(	nozzleNo, s4WorkMsg, khproc_no);
									LogUtility.getPumpMLogger().info("[Pump M] Send UPOSMessage to Cat M. NozID=" + nozzleNo);
									Preamble preamble = PumpMUtil.createUPOSMessagePreamble(	uniqueKey, SyncManager.DISE_PUMP_ADAPTER, SyncManager.DISE_CMS_MODULE, uPosMsg, "");
									sendMessage(preamble);
								}
							} else {
								// ���������� �ƴ� ���
								UPOSMessage_ItemInfo_Item itemInfoItem = preUposMsg.getItem_info().getItemInfoList().get(0);
								prePayedPrice = itemInfoItem.getOilPrice_after_discount();
								
								LogUtility.getPumpMLogger().info("[Pump M] prePayedPrice="	+ prePayedPrice);

								if (PumpMUtil.shouldSendingRejectAndReApproval(prePayedPrice, pumpingPrice, isCreditCard) /*||
								mMessageType == IUPOSConstant.MESSAGETYPE_INT_0062*/) { // tatsuno_hs okdhp7 (2012.12)
									
									LogUtility.getPumpMLogger().info("[Pump M] ����� ����");
									
									// �����Ϸ� �ݾװ� ���� �ݾ��� ���Ͽ���, �� ���̰� ������ּ����� �ݾ�
									// ���� Ŭ ���
									// �����, ������� ���� �帧�� Ÿ���� �Ѵ�.
									// DasNoSelfPumpingManager �� �����ϰ� �ִ� �� ��������
									// �����͸� ���� ���� Option-2 ó�� �����ϵ��� �Ѵ�.
									DasNoSelfPumpingManager.getInstance().changeDasNoODTNozzleInfoLikeFullPumpingWithOption6(nozzleNo);
									UPOSMessage uPosMsg = DasNoSelfPumpingManager.getInstance().getUPOSMessageAfterPumping(	nozzleNo, s4WorkMsg, khproc_no);
									LogUtility.getPumpMLogger().info("[Pump M] Send UPOSMessage to Cat M. NozID=" + nozzleNo);
									Preamble preamble = PumpMUtil.createUPOSMessagePreamble(uniqueKey,
											SyncManager.DISE_PUMP_ADAPTER,
											SyncManager.DISE_CMS_MODULE,
											uPosMsg, "");
									sendMessage(preamble);

								} else {
									// QL (������ ����) �� �����ؼ� Pump A �� �����Ѵ�.

									LogUtility.getPumpMLogger().info("������ ���");
									
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

									// ������ ���� ������ Pos A�� �����Ѵ�.
									processOPTBarcode(qlMsg, khproc_no);
								}
							}
						} else if (isCash) {
							UPOSMessage uPosMsg = DasNoSelfPumpingManager.getInstance().getUPOSMessageAfterPumpingOiSang(nozzleNo, s4WorkMsg, khproc_no);

							if (!isBonusCard && !isCashCard) {
								// ���ݿ�����, ���ʽ� ������ ���� ��쿡�� ��������� pos�� �����ϰ� QL������
								// �����Ѵ�.
								Preamble preamble = PumpMUtil.createUPOSMessagePreamble(uniqueKey,
										SyncManager.DISE_PUMP_MODULE,
										SyncManager.DISE_SALE_MODULE, 
										uPosMsg,"");
								LogUtility.getPumpMLogger().info(	"[Pump M] Send UPOSMessage to SalesM. NozID=" + nozzleNo);
								sendMessage(preamble);

								PumpMTransactionManager.getInstance().setPayed(uPosMsg.getMessageType(),	uPosMsg.getNozzle_no());
								prePayedPrice = DasNoSelfPumpingManager.getInstance().getDasNoODTNozzleInfo(nozzleNo).getHeWorkingMsg().getCashCount();
								// ������ ���
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
	 * Pump M �ʱ�ȭ ���� �ʱ�ȭ�� �����ؾ� �� ������ �����Ѵ�.
	 * 
	 */
	private void initData() {
		if (logSSDC == true)
			LogUtility.getCATLogger().info(
					"SSDC/" + "PUMP_M/" + "PupmAController/" + "initData()");

		gStep = IPumpConstant.STEP_PUMP_RESOLVED;

		LockingManager.init();
		PumpMODTSaleManager.init();
		GSCSelfPumpingManager.init(); // �ű� ODT PumpingManager �߰�, 2015.11.18- cwi
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
	 * ������� ���� �ö�� �ܰ��� ���� �� �������� �ܰ��� Ʋ�� ��� �ܰ��� �� �����Ѵ�.
	 * 
	 * @param nozzleNo :
	 *            ���� ��ȣ
	 * @param nzDevBasePrice :
	 *            ������ ��� �ܰ�
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
	 * Preset ���� ���� �ܰ��� ����ɼ� �ֱ� ������ �����Ϸ� ���� �����⿡ ������ �ܰ��� ������ ���� �ܰ��� �����Ͽ�, ����� ���
	 * ���� �ܰ��� �� �����Ѵ�.
	 * 
	 * @param khproc_no :
	 *            KH ó����ȣ
	 * @param nozzle_no :
	 *            �����Ϸ� �� Nozzle ��ȣ
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
					// LogUtility.getPumpMLogger().info("[Pump M] ��� ������ �ܰ��� ���ܰ��� ��ġ�մϴ�.") ;
				}
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		}
	}

	/**
	 * POS �� ���� ������ �ܰ� ������ �޾��� ��� ��� �����⿡ ���ؼ� ������ �ܰ� ���� ��û�Ѵ�. �������� ��� D0 ������
	 * �����ϰ�, �Ϲ� �������� ��� P3 ����, �׸��� ����������/ODT �� ��쿡�� P5 ������ �۽��Ѵ�.
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

				// D0 ������ �����⿡ ����.
				LogUtility.getPumpMLogger().info("[Pump M] Send D0 to Recharge.");
				sendMessage(d0Preamble);
			} else {
				// LogUtility.getPumpMLogger().info("[Pump M] �����Ⱑ ���, D0 ������ �����⿡
				// ������ �ʽ��ϴ�.") ;
			}

			WorkingMessage p3workMsg = PumpMUtil.getP3WorkingMessage(storeCode,
					null);
			if (p3workMsg != null) {
				p3workMsg.setMessageID(GlobalUtility.getUniqueMessageID());
				Preamble p3Preamble = PumpMUtil.createWorkingMessagePreamble(SyncManager
						.getUniqueKey(), SyncManager.DISE_PUMP_ADAPTER,
						p3workMsg, "");

				// P3 ������ �����⿡ ����
				LogUtility.getPumpMLogger().info("[Pump M] Send P3 to Pump.");
				sendMessage(p3Preamble);
			} else {
				// LogUtility.getPumpMLogger().info("[Pump M] �����Ⱑ ���, P3 ������ ������
				// �ʽ��ϴ�.") ;
			}

			WorkingMessage p5workMsg = PumpMUtil
					.getP5WorkingMessageForSendingOnly(storeCode, null);

			if (p5workMsg != null) {
				p5workMsg.setMessageID(GlobalUtility.getUniqueMessageID());
				Preamble p5Preamble = PumpMUtil.createWorkingMessagePreamble(SyncManager
						.getUniqueKey(), SyncManager.DISE_PUMP_ADAPTER,
						p5workMsg, "");
				sendMessage(p5Preamble);

				// P5 ������ ���� ODT �� ����
				LogUtility.getPumpMLogger().info("[Pump M] Send P5 to Self ODT");
			} else {
				// LogUtility.getPumpMLogger().info("[Pump M] ���� ODT�� ���, P5 ������
				// �����⿡ ������ �ʽ��ϴ�.") ;
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		}
	}

	/**
	 * Pump A �κ��� ������ ���� �޾Ƽ� �׿� ���� ó���� �����Ѵ�. ������ �ִ� ������ �׿� ���� ó���� ������ ����. -
	 * PumpA Protocol S3 ����/���� �� �ڷ� ���� (������,����,������) S4 �����Ϸ� �ڷ� ���� (������, ����) :
	 * Issue -> ������ �����Ϸ� ������ ? HF Preset �ڷ� ���� : Issue -> ������ Adapter �� ���� �� �ʿ�
	 * ����. ( �� ������ �̹� Preset ������ �����ϴ� ���̺�� ���� ������ �޾Ƽ� ����) HB �Ҹ��� �ܻ�ŷ� ���� ��û HA
	 * �Ҹ��� ī����� ���� ��û HC �Ҹ��� ���� �Ϸ�� ���� ���� ���� �پ��� ���� ���� �Ϸ�� ���� ���� ���� HE �پ��� ����
	 * ī�� ���� ���� ��û S9 �� ī�� ���� ��û SB ������ ī�� ���� ���� ��û SH ������ ODT ���� �Ǹ� ������ ���� BA
	 * ���ʽ� ���� ���� ��û TJ ���� ������ ��û SF ������ ��ȣ �ο� ��û SG ������ ��ȸ ��û SJ �������� �ڷ� ���� S5
	 * ���а����� ���� SE ���� ����̽� �̻� ���� ���� receiving_pumpa Data �� �״�� StateMController
	 * �� ������. S8 ������/������ ���� ���� receiving_pumpa Data �� �״�� StateMController ��
	 * ������. CA �پ��� ���� �� Ȯ�� ��û
	 * 
	 * 
	 * ACK - �Ϲ� ȯ������ ���� ���� (P2) - ������ ȯ�� ���� ���� ���� (P3) - ODT ȯ�� ���� ���� (���� , ������)
	 * (P5) - ������ �� �ð� ���� (P6) - ������ ODT POS ȯ������ ���� (PT)
	 *  - ���� ���� �� ���� (������,����,������) (PA) - ����/���� ���� ���� (������,����,������) (PB) - �� Ȯ��
	 * ���� ( �پ��� ����) (CB)
	 * 
	 * To POS Adapter S3 ����/���� �� �ڷ� ���� (������,����,������) SG ������ ��ȸ ��û SJ �������� �ڷ� ����
	 * 
	 * To CAT Module HB �Ҹ��� �ܻ�ŷ� ���� ��û HA �Ҹ��� ī����� ���� ��û HE �پ��� ���� ī�� ���� ���� ��û
	 * S9 �� ī�� ���� ��û SB ������ ī�� ���� ���� ��û BA ���ʽ� ���� ���� ��û TJ ���� ������ ��û
	 * 
	 * To State Module SE ���� ����̽� �̻� ���� ���� receiving_pumpa Data �� �״��
	 * StateMController �� ������. S8 ������/������ ���� ���� receiving_pumpa Data �� �״��
	 * StateMController �� ������.
	 * 
	 * To Sales Module S4 �����Ϸ� �ڷ� ���� (������, ����) HC �Ҹ��� ���� �Ϸ�� ���� ���� ���� �پ��� ���� ����
	 * �Ϸ�� ���� ���� ���� SH ������ ODT ���� �Ǹ� ������ ����
	 * 
	 * in Pump Module SF ������ ��ȣ �ο� ��û S5 ���а����� ����
	 * 
	 * @param receiving_pumpa :
	 *            ���۹��� ����
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
			
			// Pump A �� ���� ���۵Ǵ� Preamble Object �� uniqueKey �� WorkingMessage ��
			// messageID �� �����ϴ�.
			// �̴� Pump A �� ���� ��û ������ ���� ������ WorkingMessage �� messageID �� �����ϰ� ����
			// �ֱ� ���ؼ��̴�.
			// �� �ٸ� ���� ����Ҷ� messageID �� �����ϱ� ���ؼ� Preamble �� uniqueKey �� ����ϱ�
			// �����̴�.
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
				 * ���� ���� �ڷḦ ������ T_KH_PUMP_TR �� �������� ������ Update �Ѵ�.
				 */
			case IPumpConstant.COMMANDID_SJ: {
				// ���� - ���� ���� �ڷ� ����
				SJ_WorkingMessage sjWorkMsg = (SJ_WorkingMessage) receiveWorkingMessage;
				LogUtility.getPumpMLogger().info("[Pump M] Pumping Start Receive. NozID=" + sjWorkMsg.getNozzleNo());
				sjWorkMsg.print();

				int nozzle_noInt = Integer.parseInt(sjWorkMsg.getNozzleNo());
				PumpMPriceManager.initPumpPrice(nozzle_noInt); // �ݾ�/���� ���� �����͸�  �ʱ�ȭ �Ѵ�
				String khproc_no = PumpMUtil.createPumpStartContent(sjWorkMsg); // ���� ���� ������  T_KH_PUMP_TR ���̺� �����Ѵ�.

				/*
				 * ���� Beacon ��� ���θ� Ȯ���Ͽ� 
				 * ������ ���� ������ Ark�� �����Ѵ�.
				 * 2017.06.30 lj
				 */
				// ���� Beacon ��� ���� Ȯ��
				if(IBeaconConstant.isBeaconStore){
					//Beacon ��� ����� �������� �۾��� �����Ѵ�.
					processBeaconPumpingStart(uniqueKey, khproc_no);
				}
				
				break;
			}
				/**
				 * ���� �� �ڷḦ ������, �ݾ� ������ �� ���� POS �� �����Ѵ�. �׸��� kixxhub.properties
				 * ���ϳ��� pump.pumping.interval �� ������ ���� ���� POS �� �����Ѵ�. �̴� POS ��
				 * ���ϸ� ������ (UI ����), POS KixxHub ��Ż��� ���� ���� ���̱� �����̴�. ex>
				 * pump.pumping.interval=3 ���� �����Ǿ� ������ 3���� ������ ������ �� �ϳ��� �����Ѵ�.
				 */
			case IPumpConstant.COMMANDID_S3: {
				// ���� - ����/���� �� �ڷ� ����
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
						// 1. [��������] POS �� KixxHub ��Ž� ������ ���� ��� Delay �� �����. Ư��
						// ���� ������ ������ �����ε�, �̸� �����ϱ� ���ؼ�
						// POS A �� POS �� ������ ������ �۽��Ҷ�, Preamble �� ACK ���θ� NO ��
						// �ϸ� �ٷ� POS �� ������ �����Ѵ�.
						dePreamble.setIsAckRequired(Preamble.IS_ACK_REQUIRED_NO);
						sendMessage(dePreamble);
						
					} else {
						// LogUtility.getPumpMLogger().debug("[Pump M]
						// �������ڷ����۾���.Interval����") ;
					}
				} else {
					LogUtility.getPumpMLogger().warn("[Pump M] Can't trust S3WorkingMessage");
				}
				
				/*
				 * ���� Beacon ��� ���θ� Ȯ���Ͽ� 
				 * ������� ���� �������� ������ �������� ���� ���
				 * ���� ������ �����ؼ� Ark�� �����Ѵ�.
				 * 
				 * 2017.06.30 lj
				 */
				//���� Beacon ��� ���� Ȯ��
				if(IBeaconConstant.isBeaconStore){
					processBeaconS3(uniqueKey, khproc_no);
				}
				
				break;
			}
				/**
				 * �����Ϸ� �ڷḦ ������ T_KH_PUMP_TR ���̺� ������ Update �Ѵ�. �׸��� �����Ϸ��� �ݾ��� 0
				 * �� ����, ��Ȳ�� ���� POS �� �������� �ʴ´�. ��ü�� ������ processS4 �Լ� ����.
				 */
			case IPumpConstant.COMMANDID_S4: {
				// ���� - ���� �Ϸ� �ڷ� ����
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
							// �� �� ���µ� �����Ϸ��̸� Total Gauage �� ������ ��� ������.
							LogUtility.getPumpMLogger().info("[Pump M] Drop S4. NozID=" + s4WorkMsg.getNozzleNo());
						} else {
							process = true;
							// �� �� ���µ� �����Ϸ�������, Total Gauage �� �ٸ� ��� ������ ��������
							// �ڷḦ �����Ѵ�. �̴� ���������� �����Ѵ�.
							LogUtility.getPumpMLogger().info( "[Pump M] Create Fake PumpingStart Content. NozID=" + s4WorkMsg.getNozzleNo());
							PumpMUtil.createFakePumpStartContent(s4WorkMsg.getNozzleNo());
						}
					}

					// ������ODT �����Ϸ� �ڷ��� status_Flag �� 9 �� ���� ������ODT ������ ����� ���Ͽ�
					// �������� �ǸſϷ� �ڷḦ
					// ���� ���� ����̴�. �� ���� S4 ������ �ƴ϶� KJ ������ ó���ϵ��� �Ѵ�.
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
						// ���� �Ϸ� ����, ���� ������ ODT �� ����Ǿ� �ִٸ� �߰����� �۾��� �����ϵ��� �Ѵ�.
						handleWorkingMessageForAdditionalWork(uniqueKey, s4WorkMsg.getNozzleNo(), commandID, s4WorkMsg, khproc_no);
						
					}
					PumpMPriceManager.initPumpPrice(nozzle_noInt);
				}

				// ���� ��¥ : 2008.10.02
				// ���� ������ : ������, ������, ����ö
				// ��� �ܰ��� ������ �ܰ��� �ٸ���� �ܰ��� �����⿡ �� �����Ѵ�.
				// ����� processS4 �������� ������ �ܰ��� �� �����ϴ� ������ �ִ�. �� ������ Preset �ܰ���
				// �������� �ܰ���
				// Ʋ�� ��� �������� �Ǿ��ִ�.
				notifyBasePriceIfDifferent(s4WorkMsg.getNozzleNo(),	PumpMUtil.convertBasePriceFromPumpToPOS(s4WorkMsg.getBasePrice()));

				/*
				 * Beacon ��� �����̸鼭,
				 * Beacon �ֹ��� ��� �����Ϸ� ������ ARK�� ���� �Ѵ�.
				 * 2017.05.17 lj
				 */
				//���� Beacon ��� ���� Ȯ��
				if(IBeaconConstant.isBeaconStore){
					sendS4BeaconMessage(uniqueKey, khproc_no, s4WorkMsg);
				}
				
				break;
			}
				/**
				 * Preset �ڷḦ �޾Ƽ� T_KH_PUMP_TR ���̺� ������ Update �Ѵ�. �׸��� �� ������ POS
				 * �� �����Ѵ�. Pump A�� ���� ���� Preset �ڷ�� Pump A ���� �߸� �����ؼ� ���������͸�
				 * Preset �����ͷ� �����⵵ �Ѵ�. �̸� �����ϱ� ���ؼ� 9���� �̻� �������ε� Preset �ڷḦ ����
				 * ��� �������� �Ѵ�.
				 */
			case IPumpConstant.COMMANDID_HF: {
				// ���� - Preset �ڷ� ����
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
				 * ���� ���� ���Ž� �� ���� �������� ����Ǿ��� ��� POS �� �����ϵ��� �Ѵ�. Pump M ������ �׻� ����
				 * ���� ������ �����ϰ� �ִ�.
				 */
			case IPumpConstant.COMMANDID_SE:
				// ���� - ���� ����̽� �̻� ���� ����
			case IPumpConstant.COMMANDID_S8: {
				// ���� - ������/������ ���� ����
				S8_WorkingMessage s8Wm = (S8_WorkingMessage) receiveData.getPreamble();
				
				s8Wm.print();
				
				/*
				 * 2017.06.30 lj
				 * ���� �����̸鼭 ���� �������� ���
				 * ODT ���� ������ �����Ѵ�.
				  	654 : �����Ϸ�
					699 : ����ȭ�� �̵�
					235 : �������
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
					
					LogUtility.getLogger().info("[Pump M] " + s8Wm.getNozzleNo()+ "�� ���� KHó����ȣ �ʱ�ȭ");
					
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
				 * �Ҹ��� �ܻ�ŷ� ��ȸ ��û ���Ž� POS �� ��ȸ ��û�� �ϰ�, �� ���� ������ �޾Ƽ� Pump A ��
				 * �����Ѵ�. ��ȸ ��û�� Process �� �������� �ܻ�ŷ� ��ȸ ��û�� �����ϴ�. ���� ��ǰ ������ ���ԵǴ�
				 * ���� �ٸ���.
				 */
			case IPumpConstant.COMMANDID_HB: {
				// �Ҹ� ����- �Ҹ� ���� �ܻ� �ŷ� ���� ��û
				HB_WorkingMessage hbWorkMsg = (HB_WorkingMessage) receiveWorkingMessage;
				String cardNo = hbWorkMsg.getCardNumber();

				// ���� ���� ������ ���� ���� ����
				if (hbWorkMsg.getCommandIndex().equals("9")	|| hbWorkMsg.getCommandIndex().equals("0")) {
					
					PumpMTransactionManager.getInstance().setNozzleState( hbWorkMsg.getConnectNozzleNo(), IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED);
					PumpMODTSaleManager.initSaleContent(hbWorkMsg.getConnectNozzleNo());
				}

				// By ����ȣ, �Ҹ� ���� ��ī�� ������ 16�ڸ� ���� =< ����
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
				 * �Ҹ����� ī�� ���� ���� ��û �������� CAT M ���� ���� ��û/������ �޾Ƽ� �Ҹ����� �����Ѵ�.
				 * ���ݰŷ�ó�� ���� �ܰ� ������ ���� �Ҹ��� ODT ���� ���ε� �ܰ��� ��û�� �Ѵ�. �̴� �������
				 * �ٸ����̴�. �������� ��� �����ܰ��ν� ��û�� �ϸ�, ���ݰŷ�ó�� �ǸŴܰ� �� �ݾ� ������ Pump M ����
				 * �Ѵ�.
				 */
				// By ����ȣ
				// ����� ���� - ���� ī�� ó���� ���ؼ� ���� ������ �Ʒ��� ���� ������.
			case IPumpConstant.COMMANDID_HA: {
				// �Ҹ� ����- �Ҹ� ���� �ſ� ī�� ���� ���� ��û
				HA_WorkingMessage haWorkMsg = (HA_WorkingMessage) receiveWorkingMessage;

				LogUtility.getPumpMLogger().info("[Pump M] Receive HA from SoMo. ODTID="+ haWorkMsg.getNozzleNo());

				// ���� ���� ������ ���� ���� ���� - ��ȭ ������ ���׷��̵� ������ �Ͻ� ����
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
/////////////// 2012.10.08 ksm ����û ���� �������� ��������
					if(!GlobalUtility.isNullOrEmptyString(haWorkMsg.getCardNumber()) && haWorkMsg.getCardNumber().length() > 36 ){
						track2Check = haWorkMsg.getCardNumber().substring(32, 36);
					}
					
					//if("2080".equals(track2Check)){
//20171204 ygh ��������	(�Ҹ������� ����������� ��û ���ڿ��� ����)							
//					if("6950".equals(track2Check)){
//						LogUtility.getPumpMLogger().info("[Pump M] ���ΰ���-����û ����ī��! Track2=" + track2Check);
//						HC_WorkingMessage hcWorkMsg = new HC_WorkingMessage();
//
//						hcWorkMsg.setNozzleNo(haWorkMsg.getNozzleNo());
//						hcWorkMsg.setConnectNozzleNo(haWorkMsg	.getConnectNozzleNo());
//						hcWorkMsg.setMode("2");
//						hcWorkMsg.setTrType("1");
//
//						hcWorkMsg.setVanMsg("�������� ī���Դϴ�. �������� �����ϼ���.");
//
//						Preamble preamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_PUMP_ADAPTER, hcWorkMsg, "");
//						LogUtility.getPumpMLogger().info("[Pump M] Send WorkMsg to Pump A");
//						sendMessage(preamble);
//						// ��ҵ� ���� ������ ���� ���� ����
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

						LogUtility.getPumpMLogger().debug("[Pump M] ����� BIN CHECK");

						if (PumpMODTSaleManager.getCustPOSPumpM(haWorkMsg.getConnectNozzleNo()) != null) {
							LogUtility.getPumpMLogger().debug("[Pump M] �ŷ�óī�� ������ �ֱ� ������ ���� ó����.");

							String khproc_no = PumpMTransactionManager
									.getInstance().getKHTransactionID(	haWorkMsg.getConnectNozzleNo(), IPumpConstant.KH_ODT_PAID_REQ);

							processHA(messageID, haWorkMsg, khproc_no);

						} else {
							LogUtility.getPumpMLogger().debug("[Pump M] �ŷ�óī�� ������ ����. �ŷ�����");
							HC_WorkingMessage hcWorkMsg = new HC_WorkingMessage();

							hcWorkMsg.setNozzleNo(haWorkMsg.getNozzleNo());
							hcWorkMsg.setConnectNozzleNo(haWorkMsg	.getConnectNozzleNo());
							hcWorkMsg.setMode("2");
							hcWorkMsg.setTrType("1");

							hcWorkMsg.setVanMsg("�����ŷ� ����ī��δ� ���� ������ �Ͻ� �� �����ϴ�. �繫�Ƿ� �湮�Ͽ� �����Ͽ� �ֽʽÿ�.");

							Preamble preamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_PUMP_ADAPTER, hcWorkMsg, "");
							LogUtility.getPumpMLogger().info("[Pump M] Send WorkMsg to Pump A");
							
							sendMessage(preamble);
							// ��ҵ� ���� ������ ���� ���� ����
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
				 * �Ҹ��� �Ǹ� �Ϸ� �������� ���� �ܻ� �ŷ�ó �Ǹ�, ���� �Ǹ� Ȥ�� �����, ������ ��� ������ �ִ���
				 * �����Ͽ�, �� ���뿡 ���� u-POS �� �籸���Ͽ� POS �� �����Ѵ�. �׸��� �Ҹ����� ��� ��� ����
				 * ������ lastPayment_yn �� 0 ���� �����ϸ�, �ǸſϷ� ���� ���� 0001 �� �����Ѵ�.
				 */
			case IPumpConstant.COMMANDID_ST: {
				// �Ҹ��� - �Ҹ��� �Ǹ� �Ϸ�
				ST_WorkingMessage stWorkMsg = (ST_WorkingMessage) receiveWorkingMessage;
				String cardNo = stWorkMsg.getCardNo();

				// By ����ȣ, �Ҹ� ���� ��ī�� ������ 16�ڸ� ���� =< ����
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
				 * �پ��� ������ �ſ�ī�� ���� ���� ��û �����Ѵ�. �����Ǹ��� �̴� �ѰǸ� �´ٶ�� �����Ѵ�.
				 */
				// By ����ȣ
				// ����� ���� - ���� ī�� ó���� ���ؼ� ���� ������ �Ʒ��� ���� ������.
			case IPumpConstant.COMMANDID_HE: {

				HE_WorkingMessage heWorkMsg = (HE_WorkingMessage) receiveWorkingMessage;

				LogUtility.getPumpMLogger().info("[Pump M] Receive HE from DaSNo. ODTID=" + heWorkMsg.getNozzleNo());

				heWorkMsg.print();

				// �پ��� ������ ��� HE ������ �ö���� ���� ���ο� �����̶�� �����Ѵ�. �̷� ���� ���� ���¸� �ǸſϷ�
				// ���·� �����Ѵ�.
				// �̸� ���ؼ� ���� ��û�� �ö����� ���ο� KH ó����ȣ�� �����ϰ� �ȴ�.
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
												
						// 2012.10.09 ksm  �ſ�ī���� ��� Track2 �����ͱ��� ���̰� 37 �ڸ���.
						// 2012.10.12 ksm  ���ʽ� ī��� Track2 �����Ͱ� ����. �����پ����� ��� �ſ�ī�� �ڸ��� üũ�� ��������.
						if(heWorkMsg.getCardNumber().length() > 36){
							track2Check = heWorkMsg.getCardNumber().substring(32, 36);
						}
					}
					
/////////////2012.10.09 ksm ����û ���� �������� �������� �ӽ÷��� �߰�
					//if("2080".equals(track2Check)){
//20171204 ygh ��������	(�Ҹ������� ����������� ��û ���ڿ��� ����)							
//					if("6950".equals(track2Check)){
//						LogUtility.getPumpMLogger().info("[Pump M] ���ΰ���-����û ����ī��! Track2 : " + track2Check);
//						
//						QM_WoringMessage qmWorkMsg = new QM_WoringMessage();
//
//						String outputMessage = "�������� ī���Դϴ�. �������� �����ϼ���.";
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
						
						LogUtility.getLogger().debug("����� BIN CHECK");
						LogUtility.getLogger().info("�پ��� ���� - ����� BIN ī��� ������ ������");
						
						QM_WoringMessage qmWorkMsg = new QM_WoringMessage();

						qmWorkMsg.setNozzleNo(heWorkMsg.getNozzleNo());
						qmWorkMsg.setMode("2");

						Preamble preamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_PUMP_ADAPTER, qmWorkMsg, "");
						
						LogUtility.getLogger().info("[Pump M] Send WorkMsg to Pump A");
						
						sendMessage(preamble);
					} else {
						// kixxhubó����ȣ�� ������ϱ� ���� CA������ ���� POS�κ��� �޾ƿ� DW������ ����ȸ�Ͽ�
						// ������ �ִٰ�
						// KIXXHUBó����ȣ�� ���Ӱ� ������ �ٽ� �־��ش�.

						WorkingMessage workMsg = PumpMODTSaleManager	.getCustPumpAPumpM(heWorkMsg.getConnectNozzleNo());

						POS_DW dwPumpM = null;

						if ((posMsg != null) && (posMsg instanceof POS_DW))
							dwPumpM = (POS_DW) posMsg;

						// KHó����ȣ ����
						String khproc_no = PumpMTransactionManager.getInstance().getKHTransactionID(	heWorkMsg.getConnectNozzleNo(), IPumpConstant.KH_ODT_PAID_REQ);

						DasNoSelfPumpingManager.getInstance().createODTNozzleInfo( heWorkMsg.getConnectNozzleNo(), 
																					false,
																					IConstant.FULL_PUMPING_OPTION_0);
						/* 2012.10.09 ksm ����ϴ� ���� �����Ƿ� ����.
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
				 * �پ��� ���� �ǸſϷ� �����Ѵ�. ���� ���� 0001 ������ ������������ �����ϰ� ó���Ѵ�.
				 */
			case IPumpConstant.COMMANDID_TR: {
				// �پ��� ����- �پ��� ���� �Ǹ� �Ϸ�
				TR_WorkingMessage trWorkMsg = (TR_WorkingMessage) receiveWorkingMessage;

				LogUtility.getPumpMLogger().info("[Pump M] Receive TR from DaSNo. ODTID=" + trWorkMsg.getNozzleNo());
				trWorkMsg.print();

				String khproc_no = PumpMTransactionManager.getInstance().getKHTransactionID(trWorkMsg.getConnectNozzleNo(),
						IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED);
				processTR(uniqueKey, messageID, commandID, trWorkMsg, khproc_no);
				break;
			}
			
			/**
			 * PI2, �ű� ODT �߰��� ���� ���μ��� �߰�, 2015.11.18 - cwi
			 */
			case IPumpConstant.COMMANDID_GA: {

				GA_WorkingMessage gaWorkMsg = (GA_WorkingMessage) receiveWorkingMessage;

				// PI2, CWI, 2015-12-31 GA ������ LedCode���� ���� �� ���� �������� ���� ��û����
				// led code���� ���� �� ���� ������ SALE_MODULE�� ���� �Ѵ�.
				// ���� ����(4201) ��û �� LedCode ���� S�� ���� �޵��� ���ǰ� �Ǿ�����, LedCode ���� null�� �ƴϰ� S�� �ƴ� ��� pos ���� ������ ���� �Ƿ� ���� �߰� - 2016.02.12
				if (!"".equals(gaWorkMsg.getLedCode()) && !"S".equals(gaWorkMsg.getLedCode())){
					
					// ���� ���� ó��
					UPOSMessage uPosMsg = gaWorkMsg.getUnityMessage();
					
					if (uPosMsg != null) {
						//���հ��� - �������� ���� ���� ���Ž� ���εȴܰ��� pumpTR ���� ���� 2021.04.01 ������
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
								
								LogUtility.getLogger().info("[������ⰳ�� ������Ʈ ] �������� ���ν� �ܰ� ������ �Ϸ� : " + before.toString() + " / " + uPosMsg.getPosReceipt_no());
							} catch (Exception e) {
								LogUtility.getPumpMLogger().error(e.getMessage(), e);
							}							

							SqlSession session = null;
							
							try {
								session = SqlSessionFactoryManager.openSqlSession();
								
								byte[] inputData = UPOSMessageUtility.createUPOSByteArray(uPosMsg);
								
								POSHeader posHeader = POSHeader.createHeader(inputData) ;	
								
								String msgId = posHeader.getMessageID() ;
						
								LogUtility.getLogger().info("[������ⰳ�� ������Ʈ ] T_KH_SALES_INFO �������� ���� ����");
						    	if (!insertTRData(session, inputData, uPosMsg.getPosReceipt_no(), GlobalUtility.getUniqueMessageID(), "", IConstant.PROC_TYPE_MF, ICode.POS_SENDING_YES))
						    		LogUtility.getLogger().error("[������ⰳ�� ������Ʈ] T_KH_SALES_INFO INSERT error ");
						    	
							} catch (Exception e) {						
								LogUtility.getSCMLogger().error(e.getMessage(),e);
							} finally {
								SqlSessionFactoryManager.closeSqlSession(session);
							}
							LogUtility.getLogger().info("[������ⰳ�� ������Ʈ ] T_KH_SALES_INFO �������� ���� �Ϸ�");
						}
						
				    	// Sale M ���� ���� ������ �������� ���� �Ǵ��� �Ѵ�.
			    		if (ODTUtility_Common.shouldSendToSaleM(uPosMsg)) {
			    			
			    			//2012.04.23 ksm 
					    	//�������������� LED_CODE=2(����) �̰� Credit_AuthInfo(�ΰ�����)���� "CU"(�ܸ����Ϸù�ȣ�ߺ�)�� ��� ����������ȣ�� 500 ������	Ŵ 
					    	if("2".equals(gaWorkMsg.getLedCode())){
					    		LogUtility.getLogger().info("[Check TrackingNo] ���� ������ LED_CODE �� = 2" ) ;
					    		
					    		// ���������� "CU" �ܸ����Ϸù�ȣ�ߺ��� ��� ����������ȣ ���� ��Ŵ.
					    		if("CU".equals(uPosMsg.getCredit_authInfo())){
					    			LogUtility.getLogger().info("[Check TrackingNo] �ܸ����Ϸù�ȣ �ߺ� �߻�!!!" ) ;
					    			
					    			// ����������ȣ ����
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
					
					// �ŷ�ó ���� ��ȸ - PI2, 2016-01-12, cwi
					// �ŷ�ó ��û ������ ���� �Ҹ� ������ �����ϰ� ��û�� ����(POSPumpM_DV ����)
					// �ŷ�ó ���� ������ uPOS ����(4208)���� ��ȯ �� GB������ �־� ���� ����
					if (gaWorkMsg.getMessageType().equals(IUPOSConstant.MESSAGETYPE_4207)) {
					
						// PI2, CWI, 2016-04-11
						// �ŷ�ó ��û ������ �� ��� ���� ������ �����͸� �ʱ�ȭ �Ѵ�.
						LogUtility.getLogger().info("[Pump M] �ŷ�ó ���� ��û.ConnectNozzleNo="+gaWorkMsg.getConnectNozzleNo());
						PumpMTransactionManager.getInstance().setNozzleState(gaWorkMsg.getConnectNozzleNo(), IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED);
						PumpMODTSaleManager.initSaleContent(gaWorkMsg.getConnectNozzleNo());
						
						try {
							// �ŷ�ó ���� ��û�� �ܰ��� 0 ���� ��û�Ѵ�. -- ����ȣ
							// ksm 2012.06.13 �ʱ�ȭ�÷� �������� ������. 
							String preset_basePrice = "0";

							POS_DV dvPumpM = null;
							String cust_card_no = gaWorkMsg.getUnityMessage().getCustCard_No(); // �ŷ�ó ī���ȣ
							
							String nozzle_no = gaWorkMsg.getConnectNozzleNo(); // ���� ��ȣ
							
							String goods_code = T_NZ_NOZZLEHandler.getHandler().getGoods_codeBYNozzle_no(nozzle_no); // ��ǰ �ڵ�
							
							// pi2, cwi, 2016-04-15
							// �ŷ�ó ���� �ŷ��� ��� 4207��û �� KH��ȣ�� ������ �߱� �޴´�. - ��� ��û ����
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
					// ������ ������ ���� ODT�� upos ó���� ���� �ʱ� ī�� ���� �� ķ���� ���������� �ҷ� ������ �߰�
					}else if (gaWorkMsg.getMessageType().equals(IUPOSConstant.MESSAGETYPE_4201)) {

						// PI2, CWI, 2016-02-11 ���� �Ϸ� �� ODT�� ���� ��� ������ ���°� ���� �Ϸ�� HASH �� ����Ǿ� �־� ������ KH��ȣ��
						// ó�� �Ǵ� ������ �߻�, �̸� ���� �ϱ� ���� ù 4201��û �� LedCode ���� S�� �Ѿ�� ��� ���� ���¸� �Ǹ� �Ϸ�� ���� �Ѵ�.
						// ȸ�� ������ : ������, �ڵ�ȭ �����, �������, �Ǽ��� �̻��, ����ȣ �����, �۱⼮ �����
						if ("S".equals(gaWorkMsg.getLedCode())){
							
							/*
							 * 2016.05.09 WooChul Jung.
							 * 	New Self ODT �� ���, ���� ���� ������ ODT �� ���� ���۵��� �ʴ´�.
							 *  ���� ���� �ŷ� ��������, KH �� ODT �� �����̶�� �Ǵ��ϰ�, ���� ���� ������ KH ���� �����ؼ� POS �� �����Ѵ�. 
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
							
							// PI2, CWI, 2016-04-11 �ŷ�ó�� ��� 4207 ��û �� KH ó����ȣ�� �����ϵ��� ����
							if(GlobalUtility.isNullOrEmptyString(gaWorkMsg.getUnityMessage().getCustCard_No())){
								LogUtility.getLogger().info("[Pump M] �ŷ�ó �� ù ���� 4201 ��û.ConnectNozzleNo="+gaWorkMsg.getConnectNozzleNo());
								PumpMTransactionManager.getInstance().setNozzleState(gaWorkMsg.getConnectNozzleNo(), IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED);	
								PumpMODTSaleManager.initSaleContent(gaWorkMsg.getConnectNozzleNo());
								
							}else{
								LogUtility.getLogger().info("[Pump M] �ŷ�ó ù ���� 4201 ��û.ConnectNozzleNo="+gaWorkMsg.getConnectNozzleNo());
							}
							
							// ������ ���� dwPumpM �����͸� ���� �´�.
							POSHeader posObj = PumpMODTSaleManager.getCustPOSPumpM(gaWorkMsg.getConnectNozzleNo());								
							if ((posObj != null) && (posObj instanceof POS_DW)) dwPumpM = (POS_DW) posObj;
							
							// ������ ���¸� �ʱ�ȭ
							GSCSelfPumpingManager.getInstance().createODTNozzleInfo(gaWorkMsg.getConnectNozzleNo(), false, IConstant.FULL_PUMPING_OPTION_0);
							
							// pi2, cwi, 2016-02-19
							// createODTNozzleInfo ��� �� �� ������ ������ �ʱ�ȭ �ϱ⿡ pi2���� ����ϴ� �ý��ۿ� ������ �߻���.(�մܿ��� ������ �ŷ�ó ������ ���� �Ǵ� ����)
							// to-be�ý����� ��� �Ҹ� ����� ü���ϱ⿡ Ư���� ������ ������ ���� ������, �ŷ�ó�� ����� �� 4201�� ��ǰ��Ʈ������ �� ���� �ؾ� �ϴ� ������ �־�
							// �߰������� ���� �ʱ�ȭ �� �� �ŷ�ó ������ �� ������ hash�� ���� �� ���´�.
							// ���� createODTNozzleInfo ����� �ʿ伺�� ���ǹ� �� ��� createODTNozzleInfo�� ���� �ϰ�, ������ �ٽ� �����ϴ� ������ ���� �����ϴ�.
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
						
						// PI2, CWI, 2105-12-29, ķ���� �������� ��û�� ���� ���ο� KH ó���� CatMController���� 4201 ó�� �� ���� �ϵ��� ����
						SqlSession session = null;
			
						try {

							ODTUtility_Common.logAdditionalInfo(gaWorkMsg) ;

							session = SqlSessionFactoryManager.openSqlSession();
							String binChekCardNo = "";
							String track2Check = ""; // 2012.10.09 ksm
			
							if (!GlobalUtility.isNullOrEmptyString(gaWorkMsg.getCardNumber())){
								binChekCardNo = gaWorkMsg.getCardNumber().substring(0,6);
														
								// 2012.10.09 ksm  �ſ�ī���� ��� Track2 �����ͱ��� ���̰� 37 �ڸ���.
								// 2012.10.12 ksm  ���ʽ� ī��� Track2 �����Ͱ� ����. �����پ����� ��� �ſ�ī�� �ڸ��� üũ�� ��������.
								if(gaWorkMsg.getCardNumber().length() > 36){
									track2Check = gaWorkMsg.getCardNumber().substring(32, 36);
								}
							}
							
							// 2012.10.09 ksm ����û ���� �������� �������� �ӽ÷��� �߰�
							// if("2080".equals(track2Check)){
//20171204 ygh ��������	(�Ҹ������� ����������� ��û ���ڿ��� ����)							
//							if("6950".equals(track2Check)){
//								LogUtility.getLogger().info("[���ΰ���] ����û ����ī��! Track2 : " + track2Check);
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
//								String emp_no 			= gaWorkMsg.getUnityMessage().getEmp_no(); 	   // ������ ID
//								String custCard_No 		= gaWorkMsg.getCustCardNo(); 				   // �ŷ�ó ī���ȣ
//								String ss_crStNum 		= gaWorkMsg.getUnityMessage().getSs_crStNum(); // �ŷ�ó��ȣ
//								String ss_carNum 		= gaWorkMsg.getUnityMessage().getSs_carNum();  // �ŷ�ó������ȣ
//								String term_id 			= gaWorkMsg.getUnityMessage().getTerm_id() ;   // Default
//								String led_code 		= ""; // ODT���� ä���� �ٽ� �÷���� �ϱ⿡ ""�� ����
//								String lastPayment_yn 	= ""; // ������ ���� ���� üũ
//								String nozzleNo       = gaWorkMsg.getConnectNozzleNo();		// ���ٹ�ȣ
//								String paymentAmt 	  = gaWorkMsg.getPrice();				// ���� �ݾ�
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
//								uPosMsg.setLastPayment_yn(lastPayment_yn); // ������ ���� ó��
//								uPosMsg.setNozzle_no(nozzleNo);
//								uPosMsg.setPayment_amt(paymentAmt);
//								uPosMsg.setVan_Res_Code("1111"); //VAN�����ڵ�
//								uPosMsg.setTradeCondition(affilate_info2);
//								uPosMsg.setDisplay_msg("����û ����ī�� �ſ� ���� ����");
//								uPosMsg.setVan_msg("����û ����ī�� �ſ� ���� ����");
//								uPosMsg.setPosReceipt_no(gaWorkMsg.getUnityMessage().getPosReceipt_no()); // ��ǥ��ȣ
//								uPosMsg.setBonRSCard_no(gaWorkMsg.getUnityMessage().getBonRSCard_no());   // ���ʽ�ī���ȣ
//								uPosMsg.setTrdate_creditCard(gaWorkMsg.getUnityMessage().getTrdate_creditCard()); // �ſ�����Ͻ�
//								
//								gbWorkMsg.setUnityMessage(uPosMsg);
//								
//								Preamble preamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_PUMP_ADAPTER, gbWorkMsg, "");
//								
//								LogUtility.getLogger().info("[Pump M] Send WorkMsg to Pump A");
//								
//								sendMessage(preamble);
//			
//								// ��ҵ� ���� ������ ���� ���� ����
//								PumpMTransactionManager.getInstance().setNozzleState(	gaWorkMsg.getConnectNozzleNo(), IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED);
//								PumpMODTSaleManager.initSaleContent(gaWorkMsg.getConnectNozzleNo());
//								
//								break;
//							}
							
							// ����� üũ �� �Ϻ� �ŷ�ó�� ������ �޸𸮿� ���� �־� ���� üũ�� ���� �ʴ� ������ Ȯ�εǾ�
							// ���� üũ ���� ���� - 2016.02.26. cwi
							if (T_KH_BIN_INFOHandler.getHandler().isExist(session, "03", binChekCardNo) 
									|| T_KH_BIN_INFOHandler.getHandler().isExist(session, "09", binChekCardNo)){
								
								LogUtility.getLogger().debug("����� BIN CHECK");
								
								POSHeader posMsg = PumpMODTSaleManager.getCustPOSPumpM(gaWorkMsg.getConnectNozzleNo());
								String custCode = "";
								
								if (posMsg != null)
									custCode = ((POS_DW) posMsg).getCust_code();
								
								if (!("").equals(custCode) && !("").equals(gaWorkMsg.getCustCardNo())) {
									
									LogUtility.getLogger().info("[Pump M] �ŷ�óī�� ������ �ֱ� ������ ���� ó����.");
									
									String khproc_no = gaWorkMsg.getUnityMessage().getPosReceipt_no();
									
									processGA(uniqueKey, messageID, gaWorkMsg, khproc_no);
									
								} else {
								
									LogUtility.getLogger().info("[Pump M] �ŷ�óī�� ������ ����. �ŷ�����");
									
									Integer oldMessageType = Integer.parseInt(gaWorkMsg.getMessageType());
									Integer changeMessageType = oldMessageType + 1;
									String  newMessageType = GlobalUtility.appending0Pre(changeMessageType.toString(),4);
									
									// ����� ī��� �Ϲ� ���ο�û �� ODT�� ���� ������ ���� �� ���� �ش�.(uPOS ����)
									GB_WorkingMessage gbWorkMsg = new GB_WorkingMessage();
									gbWorkMsg.setMessageType(newMessageType);
									gbWorkMsg.setNozzleNo(gaWorkMsg.getNozzleNo());
									gbWorkMsg.setConnectNozzleNo(gaWorkMsg.getConnectNozzleNo());
									gbWorkMsg.setMode("2");

									String emp_no 			= gaWorkMsg.getUnityMessage().getEmp_no(); 	   // ������ ID
									String custCard_No 		= gaWorkMsg.getCustCardNo(); 				   // �ŷ�ó ī���ȣ
									String ss_crStNum 		= gaWorkMsg.getUnityMessage().getSs_crStNum(); // �ŷ�ó��ȣ
									String ss_carNum 		= gaWorkMsg.getUnityMessage().getSs_carNum();  // �ŷ�ó������ȣ
									String term_id 			= gaWorkMsg.getUnityMessage().getTerm_id() ;   // Default
									String led_code 		= ""; // ODT���� ä���� �ٽ� �÷���� �ϱ⿡ ""�� ����
									String lastPayment_yn 	= ""; // ������ ���� ���� üũ
									String nozzleNo       = gaWorkMsg.getConnectNozzleNo();		// ���ٹ�ȣ
									String paymentAmt 	  = gaWorkMsg.getPrice();				// ���� �ݾ�
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
									uPosMsg.setLastPayment_yn(lastPayment_yn); // ������ ���� ó��
									uPosMsg.setNozzle_no(nozzleNo);
									uPosMsg.setPayment_amt(paymentAmt);
									uPosMsg.setVan_Res_Code("1111"); 		   // VAN�����ڵ�
									uPosMsg.setTradeCondition(affilate_info2);
									uPosMsg.setDisplay_msg("�����(��������)ī�� �ſ� ���� ����");
									uPosMsg.setVan_msg("�����(��������)ī�� �ſ� ���� ����");
									uPosMsg.setPosReceipt_no(gaWorkMsg.getUnityMessage().getPosReceipt_no()); // ��ǥ��ȣ
									uPosMsg.setBonRSCard_no(gaWorkMsg.getUnityMessage().getBonRSCard_no());   // ���ʽ�ī���ȣ
									uPosMsg.setTrdate_creditCard(gaWorkMsg.getUnityMessage().getTrdate_creditCard()); // �ſ�����Ͻ�
									
									gbWorkMsg.setUnityMessage(uPosMsg);
									
									Preamble preamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_PUMP_ADAPTER, gbWorkMsg, "");
									
									LogUtility.getLogger().info("[Pump M] Send WorkMsg to Pump A");
									
									sendMessage(preamble);
									
									// ��ҵ� ���� ������ ���� ���� ����
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
				 * PI2, �ű� ODT �߰��� ���� ���μ��� �߰�, 2016.03.24 - CWI
				 * GSC Self ������ ���ڵ� ��û ���� �߰�
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
					//barcode_type(1:�������ڵ�, 2:�������ڵ�)
					String barcode_type = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0441);
					
					LogUtility.getPumpMLogger().info("[Pump M] Receive BR from GSC Self. BarcodeType = " + barcode_type);
					
					if ("1".equals(barcode_type) || "01".equals(barcode_type)) {
						//�������ڵ� ��������
						if(!brWorkMsg.getPrice().equals("")){
							
							// �������ڵ�
							barcode = Barcode.getBarcodeNumberPump("3", brWorkMsg.getPrice(), brWorkMsg.getConnectNozzleNo(), brWorkMsg.getPosReceiptNo(), null, null, null);			
							LogUtility.getPumpMLogger().info("[PUMP M] barcode="+barcode);
						}else{
							LogUtility.getPumpMLogger().error("[PUMP M] ���� �ݾ��� ���� ���� �ʽ��ϴ�.!");
						}
						bsMsg.setBarcode(barcode);
						
						Preamble pumpPreamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_PUMP_ADAPTER, bsMsg, "") ;
						
						sendMessage(pumpPreamble);

						// POS�� EN���� �۽�(���ڵ� ��� ���� ����)
						processOPTGSCSelfBarcode(bsMsg, brWorkMsg.getPosReceiptNo());
					}
					else if ("2".equals(barcode_type) || "02".equals(barcode_type)) {
						// �������ڵ� ���� ���� ó��
						if(!brWorkMsg.getPrice().equals("")){
							
							// �������ڵ�
							barcode = Barcode.getBarcodeNumber("3", brWorkMsg.getPrice(), brWorkMsg.getConnectNozzleNo(), brWorkMsg.getPosReceiptNo(), null, null, null);			
							LogUtility.getPumpMLogger().info("[PUMP M] barcode="+barcode);
						}else{
							LogUtility.getPumpMLogger().error("[PUMP M] ���� �ݾ��� ���� ���� �ʽ��ϴ�.!");
						}
						bsMsg.setBarcode(barcode);
						
						Preamble pumpPreamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_PUMP_ADAPTER, bsMsg, "") ;
						
						sendMessage(pumpPreamble);
						
						// POS�� EN���� �۽�(���ڵ� ��� ���� ����)
						 processOPTGSCSelfBarcode(bsMsg, brWorkMsg.getPosReceiptNo());
					}
					else {
						//������������ �ƴҶ� ����ڵ带 �����Ѵ�. 
						//BR�� ��û�� �޾����� BS�۽��� ����� ����ó��
						Preamble pumpPreamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_PUMP_ADAPTER, bsMsg, "") ;
						sendMessage(pumpPreamble);
					}
					
				} catch (Exception e) {
					LogUtility.getPumpMLogger().error(e.getMessage(), e);
				}
				break;
			}
			
				/**
				 * PI2, �ű� ODT �߰��� ���� ���μ��� �߰�, 2015.11.18 - CWI
				 * GSC Self �ǸſϷ� �����Ѵ�. ���� ���� 0001 ������ ������������ �����ϰ� ó�� ����
				 */
			case IPumpConstant.COMMANDID_GT: {
				// GSC Self - GSC Self  �Ǹ� �Ϸ�
				GT_WorkingMessage gtWorkMsg = (GT_WorkingMessage) receiveWorkingMessage;
	
				/**
				 * 2016.05.19 WooChul Jung
				 * 	GT ���� ���� ���� �ش� ���� ���� �ʱ�ȭ�� �߻���.
				 * 	���� ���� ���� ���� GT ������ ���ÿ� ������ ���, ���� ���� �ʱ�ȭ�� ���� Side Effect �߻��� �� ����.
				 * 	�̷� ���Ͽ� GT ���� �����ϸ� 1�� ��� ���� ����.
				 */
				Thread.sleep(1000) ;
				LogUtility.getLogger().info("[Pump M] Waiting 1 seconds. Receive GT from GSC Self. ODTID=" + gtWorkMsg.getNozzleNo());
				gtWorkMsg.print();
				
				// PI2, CWI, 2016-04-05
			    // ODT�� ���� �ö�� ���� �� �ڷ� �� ������ �ڷ��� ���� ������ parsing ó���� ���� �ʾ�
				// ���� ���� �� ���� �� ������ �������� ���� DE���� Ȥ�� DG������ litter ��ġ�� �Ҽ��� 3�ڸ� �̻����� ���� �Ǵ� ������ ���� �ϱ� ����
				// �Ǹ� �Ϸ� ó���ÿ��� �ݾ�/������ �����͸� �ʱ�ȭ �Ѵ�.
				int nozzle_noInt = Integer.parseInt(gtWorkMsg.getConnectNozzleNo());
				PumpMPriceManager.initPumpPrice(nozzle_noInt);
				
				String khproc_no = PumpMTransactionManager.getInstance().getKHTransactionID(gtWorkMsg.getConnectNozzleNo(),IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED);
				processGT(uniqueKey, messageID, commandID, gtWorkMsg, khproc_no);
				break;
			}
			
			
				/**
				 * �پ��� ������ ���� ī������ ��ȸ�� ���� �ſ�ī��/ �ŷ�ó ī�� ���� ������ CB
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

					// ����ȭ�� üũ
					if (isCargo) {

						CB_WorkingMessage cbWorkingMsg = new CB_WorkingMessage();

						cbWorkingMsg.setNozzleNo(caWorkMsg.getNozzleNo());
						cbWorkingMsg.setConnectNozzleNo(caWorkMsg.getConnectNozzleNo());
						cbWorkingMsg.setCustomerType(IPumpConstant.CB_CUSTOMER_TYPE_CUS_CASH); // ���� �ŷ�ó�� ����
						cbWorkingMsg.setSaveBonus("1");

						Preamble cbPreamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey, SyncManager.DISE_PUMP_ADAPTER, cbWorkingMsg, "");

						sendMessage(cbPreamble);

						// ����ȭ���� ��� DW������ ���Ƿ� ����� �ش�. edited by ykJang
						String goods_code = T_NZ_NOZZLEHandler.getHandler().getGoods_codeBYNozzle_no(caWorkMsg.getConnectNozzleNo()); // ��ǰ �ڵ�

						POS_DW dwPumpM = new POS_DW("", caWorkMsg
								.getConnectNozzleNo(), "", caWorkMsg
								.getCardNo(), "", custRtnValue.getCust_name(),
								custRtnValue.getCust_code(), goods_code,
								custRtnValue.getDiscountBasePrice(),
								custRtnValue.getCust_cd_item(), "", "", "", "",
								"", "1", "", "", "", "", "", "", "", "", "");

						PumpMODTSaleManager.setCustInfo(caWorkMsg.getConnectNozzleNo(), caWorkMsg, dwPumpM);
					}
					// ����ȭ���� �ƴ� ��� �ŷ�ó ���� ��ȸ.
					else {
						String goods_code = T_NZ_NOZZLEHandler.getHandler().getGoods_codeBYNozzle_no( caWorkMsg.getConnectNozzleNo()); // ��ǰ �ڵ�

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
				 * ������Ʈ : PI2
				 * ����: TCP/IP ��Ź�� ���� ������ ODT ���� ��û �� ���� ó��  
				 * ������ODT �ſ���� ��û(0031)/����(0032), �ſ�+���ʽ����� ��û(0033)/����(0034), ķ�������� ��û(4291)/����(4292), 
				 * ���ݿ����� ��û(0015)/����(0016) ���� ����, ���ʽ����� ��û(0003)/����(0004), GS������� ��û(0061)/����(0062), 
				 * GS���ʽ�������ȸ ��û(0111), ����+���ʽ� ��û(0013)/����(0014), ������� ��û(0063)/����(0064), ������� ��� ��û(8063)/����(8064)
				 * �������� : 2015.12.23 ������
				 * 
				 * '���������ŷ�ī����� + ���ʽ�����' ��û (0033) ���μ��� 
				 * 1. 0033 (ODT -> KH)
				 * 2. 0034 (ODT <- KH)  ��ִ������� ����(�����ڵ�: 4123)
				 * 3. 0031 (ODT -> KH)  ODT���� VAN�����ڵ尡 4123�� ���, �ܵ��ſ���� ���� �� ��û
				 * 4. 0032 (ODT <- KH)
				 */
			case IPumpConstant.COMMANDID_SK:{
				SK_WorkingMessage skWorkMsg = (SK_WorkingMessage) receiveWorkingMessage;
				String skMessageType = skWorkMsg.getMessageType();
				String nozzleNo = skWorkMsg.getNozzleNo();
				UPOSMessage uPos = skWorkMsg.getUnityMessage();
				uPos.setMessageID(skWorkMsg.getMessageID());
				
				if(skMessageType.equals(IUPOSConstant.MESSAGETYPE_0031)        // �ſ���� ��û
						||skMessageType.equals(IUPOSConstant.MESSAGETYPE_0033) // �ſ� + ���ʽ� ��û
						){        
					
					LogUtility.getLogger().info("[Pump M] Receive SK WorkingMessage(" + skMessageType + ") from NewRecharge ODT. ODTID=" + nozzleNo);
					//uPos.print();
					
					String binChekCardNo = "";
					
					if (!GlobalUtility.isNullOrEmptyString(uPos.getCreditCard_no().trim())) {
	
						binChekCardNo = uPos.getCreditCard_no().substring(0, 6);
					}
	
					LogUtility.getLogger().info("[Pump M] binChekCardNo =" + binChekCardNo);
	
					// �����ý� ���ʽ� ���� ���� üũ
					boolean rlt = ODTUtility_Recharge.checkBizBin(binChekCardNo);
	
					LogUtility.getLogger().debug("[Pump M] ���� �ý� BIN CHECK : " + rlt);
	
					if (rlt) {
	
						if (PumpMODTSaleManager.getCustPOSPumpM(nozzleNo) != null) {
							
							LogUtility.getLogger().debug("[Pump M] �ŷ�óī�� ������ �ֱ� ������ ���� ó����.");
	
							String posReceipt_no = PumpMTransactionManager.getInstance().getKHTransactionID(nozzleNo); 
							
							sendLockingInfoToPOS(nozzleNo, posReceipt_no, IConstant.PUMP_SALE_LOCKING);
		
							Preamble preamble = PumpMUtil.createUPOSMessagePreamble(messageID,
									SyncManager.DISE_PUMP_ADAPTER,
									SyncManager.DISE_CMS_MODULE, uPos, "");
							
							LogUtility.getLogger().info("[Pump M] Send UPOSMessage to CAT M");
							sendMessage(preamble);
							
						} else {
							LogUtility.getLogger().debug("[Pump M] �ŷ�óī�� ������ ����. �ŷ�ó ī�� ���� �޼��� ���");
							
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
							returnUpos.setDisplay_msg("�����ŷ�ī�常���δ� �ſ������ ������ �����ϴ�.");
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
				} else if(skMessageType.equals(IUPOSConstant.MESSAGETYPE_0015)            // ����û��ǰ�����ݿ����� ��û
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_0003)        // ���ʽ����� ��û
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_0061)        // GS������� ��û
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_0111)        // GS���ʽ�������ȸ ��û
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_0063)		  // ������� ��û
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_0065)		  // ���� + ����û���ݿ����� ��� ��û
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_0013)	      // ���� + ���ʽ� ��û ( �������� ���� �� ��� )
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_0045)        // ���̽��� ��û
						    ||skMessageType.equals(IUPOSConstant.MESSAGETYPE_0047)        // ���� + ���ʽ� ��û
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_8045)        // ���̻�� ��ҿ�û
						    ||skMessageType.equals(IUPOSConstant.MESSAGETYPE_8047)        // ���� + ���ʽ� ��ҿ�û						    
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_8063)     	// ������� ��� ��û
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_8065)){      // ���� + ����û���ݿ����� ��� ��� ��û
						
					LogUtility.getLogger().info("[Pump M] Receive SK WorkingMessage(" + skMessageType + ") from NewRecharge ODT. ODTID=" + nozzleNo);
					//uPos.print();

					String posReceipt_no = PumpMTransactionManager.getInstance().getKHTransactionID(nozzleNo); 
					
					// ����û��ǰ�����ݿ����� ��û(0015) �� ķ���������Ϸ����� ��û(4291)�� ���� ������ KH���� KixxHub ó����ȣ�� �־���.  
					// (ODT���� ���ο�û ������ ���� ��, ������ KHó����ȣ�� �ֱ� ���ؼ��� ķ���������Ϸ����� ��û/������ �ʿ���)
					if(skMessageType.equals(IUPOSConstant.MESSAGETYPE_0015) && uPos.getPosReceipt_no().equals("00000000000000")){
						uPos.setPosReceipt_no(posReceipt_no);
					}
					
					sendLockingInfoToPOS(nozzleNo, posReceipt_no, IConstant.PUMP_SALE_LOCKING);

					Preamble preamble = PumpMUtil.createUPOSMessagePreamble(messageID,
							SyncManager.DISE_PUMP_ADAPTER,
							SyncManager.DISE_CMS_MODULE, uPos, "");
					
					LogUtility.getLogger().info("[Pump M] Send UPOSMessage to CAT M");
					sendMessage(preamble);
							
				// LED�ڵ尡 ���Ե� ���������� ���������� SALE MODULE�� ���� 
				} else if(skMessageType.equals(IUPOSConstant.MESSAGETYPE_0032)      	// �ſ���� ����
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_0034)      // �ſ�+ ���ʽ� ����
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_0015)      // ����û��ǰ�����ݿ����� ��û
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_0004)      // ���ʽ����� ���� 
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_0062)      // GS������� ����
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_0064)      // ������� ���� 
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_0066)      // ���� + ����û���ݿ����� ��� ���� 
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_0014)      // ���� + ���ʽ� ����
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_0046)      // ���̽��� ��û
						    ||skMessageType.equals(IUPOSConstant.MESSAGETYPE_0048)      // ���� + ���ʽ� ��û	
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_8046)      // ���̽��� ��û
						    ||skMessageType.equals(IUPOSConstant.MESSAGETYPE_8048)      // ���� + ���ʽ� ��û												    
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_8064) 	   // ������� ��� ����  
							||skMessageType.equals(IUPOSConstant.MESSAGETYPE_8066)){    // ���� + ����û���ݿ����� ��� ��� ����  
					LogUtility.getLogger().info("[Pump M] Receive SK WorkingMessage(" + skMessageType + " + LED) from NewRecharge ODT . ODTID=" + nozzleNo);
					
					//UPOSMessage uPos = skWorkMsg.getUnityMessage();
					//uPos.print();
					
					//���� Ȯ�������� ODT�κ��� �޾� Sale M���� ����
					//Sale M ���� ���� ������ �������� ���� �Ǵ��� �Ѵ�.(As-Is ���, CatMController�� �ִ� ����) 
					if (ODTUtility_Common.shouldSendToSaleM(uPos)) {
		    			//2012.04.23 ksm 
				    	//�������������� LED_CODE=2(����) �̰� Credit_AuthInfo(�ΰ�����)���� "CU"(�ܸ����Ϸù�ȣ�ߺ�)�� ��� ����������ȣ�� 500 ������	Ŵ 
				    	if("2".equals(skWorkMsg.getLedCode())){
				    		
				    		LogUtility.getLogger().info("[Pump M] [Check TrackingNo] ���� ������ LED_CODE �� = 2" ) ;
				    		
				    		// ���������� "CU" �ܸ����Ϸù�ȣ�ߺ��� ��� ����������ȣ ���� ��Ŵ.
				    		if("CU".equals(uPos.getCredit_authInfo())){
				    			
				    			LogUtility.getLogger().info("[Pump M] [Check TrackingNo] �ܸ����Ϸù�ȣ �ߺ� �߻�!!!" ) ;
				    			// ����������ȣ ����
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
						
					// ķ���������Ϸ����� ��û
					// ������ ODT�� ���, CustCard_Car_Type(�ŷ�ó��/������ ����) ���� '0'���� �ö�� 
				} else if (skMessageType.equals(IUPOSConstant.MESSAGETYPE_4291)){    // ķ���������Ϸ����� ��û 
						
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
				 * Serial ��� ��� ������ ODT�� ���� ��û ó��
				 * �������� ī�� ���� ��û�� �����Ѵ�. ���� �ŷ�ó�� ���� �ŷ�ó �ܰ� �� �ݾ����� �����Ͽ��� VAN �翡
				 * ��û�Ѵ�. ������� ���� ���� ��� ���� ��û�� ���� �ܰ��� ���� �����̴�.
				 */
			case IPumpConstant.COMMANDID_SB: {
				
				SB_WorkingMessage sbWorkMsg = (SB_WorkingMessage) receiveWorkingMessage;
				
				LogUtility.getLogger().info("[Pump M] Receive SB from Recharge. ODTID=" + sbWorkMsg.getNozzleNo());
				sbWorkMsg.print();

				String binChekCardNo = "";

				if (!GlobalUtility.isNullOrEmptyString(sbWorkMsg.getContent().trim())) {

					// �ſ�ī���ȣ �ʵ��� ����Ÿ ���� üũ
					if (sbWorkMsg.getContent().trim().length() < 10) {
						sendMessage(ODTUtility_Recharge	.invalidCardNo(sbWorkMsg));
						return;
					}
					binChekCardNo = sbWorkMsg.getContent().substring(0, 6);
				}

				if (!GlobalUtility.isNullOrEmptyString(sbWorkMsg.getBonusNumber().trim())) {

					// ���ʽ�ī���ȣ �ʵ��� ����Ÿ ���� üũ
					if (sbWorkMsg.getBonusNumber().trim().length() < 16) {
						sendMessage(ODTUtility_Recharge	.invalidCardNo(sbWorkMsg));
						return;
					}
					binChekCardNo = sbWorkMsg.getContent().substring(0, 6);
				}

				LogUtility.getLogger().info("[Pump M] binChekCardNo =" + binChekCardNo);

				// �����ý� ���ʽ� ���� ���� üũ
				boolean rlt = ODTUtility_Recharge.checkBizBin(binChekCardNo);

				LogUtility.getLogger().debug("���� �ý� BIN CHECK : " + rlt);

				if (rlt) {
					// ���� �ýô� ���ʽ� ������ ���� �ʴ´�.
					sbWorkMsg.setBonusNumber("");

					if (PumpMODTSaleManager.getCustPOSPumpM(sbWorkMsg.getNozzleNo()) != null) {
						
						LogUtility.getLogger().debug("�ŷ�óī�� ������ �ֱ� ������ ���� ó����.");
						PumpMObjectValidation.validatePumpAObject(sbWorkMsg);

						String posReceipt_no = PumpMTransactionManager.getInstance().getKHTransactionID(sbWorkMsg.getConnectNozzleNo());
						UPOSMessage uPos = ODTUtility_Common.createUPOSMessageFromWorkingMessage(sbWorkMsg, posReceipt_no);

						POSHeader posObj = PumpMODTSaleManager.getCustPOSPumpM(sbWorkMsg.getConnectNozzleNo());

						if ((posObj != null) && (posObj instanceof POS_DW)) {
							POS_DW dwPosMsg = (POS_DW) posObj;

							if (PumpMObjectValidation.validatePumpMObject(dwPosMsg)) {
								LogUtility.getLogger().info("[Pump M] add CustInfo to UPOSMessage before sending it. Cust_code=" + dwPosMsg.getCust_code());
								// Upos �������� Ss_crStNum �׸��� üũ�ؼ� ���帶�ϸ����� �����Ѵ�.
								// Ss_crStNum �׸񿡴� �ŷ�ó ī�� ��ȣ �Ǵ� �ŷ�ó �ڵ� �� �� �ϳ���
								// ������ ó���ȴ�.
								uPos.setSs_crStNum(dwPosMsg.getCust_code()); // �ŷ�ó �ڵ�
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
						LogUtility.getLogger().debug("�ŷ�óī�� ������ ����. �ŷ�ó ī�� ���� �޼��� ���");
						PI_WorkingMessage piWorkMsg = new PI_WorkingMessage();

						piWorkMsg.setNozzleNo(sbWorkMsg.getNozzleNo());
						piWorkMsg.setMode("2");
						piWorkMsg.setCardType("0");
						piWorkMsg.setNotice("�����ŷ�ī�常���δ� �ſ������ ������ �����ϴ�.[Ȯ��]Ŭ��");

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
							// Upos �������� Ss_crStNum �׸��� üũ�ؼ� ���帶�ϸ����� �����Ѵ�.
							// Ss_crStNum �׸񿡴� �ŷ�ó ī�� ��ȣ �Ǵ� �ŷ�ó �ڵ� �� �� �ϳ��� ������
							// ó���ȴ�.
							uPos.setSs_crStNum(dwPosMsg.getCust_code()); // �ŷ�ó �ڵ�
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
				 * ������� ���� �� ī�� ��ȸ ��û�� �����Ѵ�. �̿� ���� ó���� POS �� ��û�ϰ�, �� ������ �����Ͽ�
				 * ������� �����Ѵ�.
				 */
			case IPumpConstant.COMMANDID_S9: {
				// ������- ������ �� ī�� ���� ��û ����
				S9_WorkingMessage s9WorkMsg = (S9_WorkingMessage) receiveWorkingMessage;
				LogUtility.getPumpMLogger().info("[Pump M] Receive S9(Cust Processing) from Recharge. ODTID=" + s9WorkMsg.getNozzleNo());
				s9WorkMsg.print();

				processS9(uniqueKey, receiving_pumpa, messageID, s9WorkMsg);

				break;
			}
				/**
				 * �������� ���ʽ� ���� ���� ��û �����Ѵ�. ���ſ� ���� ������ ���� ���� ������� �����ϰ�, ���ʽ� ����
				 * ������ POS �� �������� �ʴ´�. ������ �Ǹ� �Ϸ� (SH) ���� ���� ���� �Ͼ� �� �� �ֱ� �����̴�.
				 */
			case IPumpConstant.COMMANDID_BA: {
				// ������- ������ ���ʽ� ���� ���� ��û
				BA_WorkingMessage baWorkMsg = (BA_WorkingMessage) receiveWorkingMessage;
				
				LogUtility.getPumpMLogger().info("[Pump M] Receive BA from Recharge. ODTID=" + baWorkMsg.getNozzleNo());
				baWorkMsg.print();

				String posReceipt_no = PumpMTransactionManager.getInstance().getKHTransactionID(baWorkMsg.getConnectNozzleNo());
				UPOSMessage uPos = ODTUtility_Common.createUPOSMessageFromWorkingMessage(baWorkMsg, posReceipt_no);

				// POSPumpMHeader posObj =
				// PumpMODTSaleManager.getCustPOSPumpM(baWorkMsg.getConnectNozzleNo())
				// ;

				String custCardNo = PumpMTransactionManager.getInstance().getCustCardNumber(baWorkMsg.getConnectNozzleNo());

				// LogUtility.getPumpMLogger().debug("[TEST] ���ֳ�???
				// custCardNo="+custCardNo);
				if (!"".equals(custCardNo)) {

					LogUtility.getPumpMLogger().info("[Pump M] add CustInfo to UPOSMessage before sending it. custCardNo=" + custCardNo);
					// Upos �������� Ss_crStNum �׸��� üũ�ؼ� ���帶�ϸ����� �����Ѵ�.
					// Ss_crStNum �׸񿡴� �ŷ�ó ī�� ��ȣ �Ǵ� �ŷ�ó �ڵ� �� �� �ϳ��� ������ ó���ȴ�.
					uPos.setSs_crStNum(custCardNo); // �ŷ�ó ī�� ��ȣ
				}

				Preamble preamble = PumpMUtil.createUPOSMessagePreamble(messageID,
						SyncManager.DISE_PUMP_ADAPTER,
						SyncManager.DISE_CMS_MODULE, uPos, "");
				
				LogUtility.getPumpMLogger().info("[Pump M] Send UPOSMessage to CAT M.");
				sendMessage(preamble);
				break;
			}
				/**
				 * ������ ���� ������ ��û�� �����Ѵ�. ���ſ� ���� ������ ���� ���� ������� �����ϰ�, �� ������ POS �δ�
				 * �������� �ʴ´�, ���� ������ ��û�� �ǸſϷ�(SH) ���� ���� ���� �Ͼ� �� �� �ֱ� �����̴�.
				 */
			case IPumpConstant.COMMANDID_TJ: {
				// ������- ������ ���� ������ ��û
				TJ_WorkingMessage tjWorkMsg = (TJ_WorkingMessage) receiveWorkingMessage;
				LogUtility.getPumpMLogger().info("[Pump M] Receive TJ from Recharge. ODTID=" + tjWorkMsg.getNozzleNo());
				tjWorkMsg.print();

				String posReceipt_no = PumpMTransactionManager.getInstance()
						.getKHTransactionID(tjWorkMsg.getConnectNozzleNo());
				UPOSMessage uPos = ODTUtility_Common.createUPOSMessageFromWorkingMessage(tjWorkMsg, 	posReceipt_no);

				// PI2 20160324 twlee ��ִ������� �ֹι�ȣ�� ���ݿ����� �߱� �Ұ��� ���� kixxhub���� ��ִ����� ������ �ʰ� ���ݿ����� �߱޺Ұ� ó��
				if(IUPOSConstant.MESSAGETYPE_0015.equals(uPos.getMessageType()) && ("0101".equals(uPos.getLoyality_type()) && "02".equals(uPos.getCreditCardReading_type()))){
						String nozzle_no = uPos.getNozzle_no();
						String successBP = "1" ;	// ����:0 ����:1	
						String odtContent = "�ֹι�ȣ�� ���ݿ����� �Ұ�";
						XA_WorkingMessage xaWorkMsg = new XA_WorkingMessage(nozzle_no, successBP, odtContent,"");
						
						// Pump A �� WorkingMessage �� �����Ѵ�.
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
				 * ������� ���� ���ʽ� ī�� ����Ʈ ���� ��ȸ ��û�� �����Ѵ�. ���� ������ VAN �� �� ���� �޾Ƽ� �̸�
				 * �����⿡ �����Ѵ�.
				 */
			case IPumpConstant.COMMANDID_TD: {
				// ������- ������ ���ʽ� ī���� ����Ʈ ���� ��ȸ ��û
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
				 * �������� �ǸſϷ����� (SH) �����Ѵ�. ��ü���� ������ processSH �Լ��� �����ϱ� �ٶ���.
				 */
			case IPumpConstant.COMMANDID_SH: {
				// ������- ������ ODT ���� �Ǹ� ������ ����
				SH_WorkingMessage shWorkMsg = (SH_WorkingMessage) receiveWorkingMessage;
				
				LogUtility.getPumpMLogger().info("[Pump M] Receive SH from Recharge. ODTID=" + shWorkMsg.getNozzleNo());
				shWorkMsg.print();

				String khKey = PumpMTransactionManager.getInstance().getKHTransactionID(shWorkMsg.getNozzleNo(), 
						IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED);

				processSH(uniqueKey, shWorkMsg, khKey);
				break;
			}
				/**
				 * ������� ���� ������ ��ȣ �ο� ��û�� �����Ѵ�. ������ ��ȣ�� KixxHub ��ü������ �����ϸ� Unique
				 * �� ��ȣ�� �����Ͽ� �����Ѵ�.
				 */
			case IPumpConstant.COMMANDID_SF: {
				// ������- ������ ��ȣ �ο� ��û
				SF_WorkingMessage sfWorkMsg = (SF_WorkingMessage) receiveWorkingMessage;
				
				LogUtility.getPumpMLogger().info("[Pump M] Receive SF from Recharge. ODTID=" + sfWorkMsg.getNozzleNo());
				sfWorkMsg.print();

				processSF(uniqueKey, messageID, sfWorkMsg);

				break;
			}
				/**
				 * ������� ���� ������ ��ȸ ��û ���� �����̴�. ������ ������� �ʴ� �����̴�.
				 */
			case IPumpConstant.COMMANDID_SG: {
				// ������- ������ ��ȸ ��û
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
				// ����POS�� ���� ���Աݾ��� ����.
				BI_WorkingMessage biWorkMsg = (BI_WorkingMessage) receiveWorkingMessage;

				PosSendCashCount(messageID, biWorkMsg, "1");
				break;
			}
			case IPumpConstant.COMMANDID_BC: {
				// ����POS�� ���� ���� ��ұݾ��� ����.
				// ODT�� �Ž����� �߻� ������ ����
				BC_WorkingMessage bcWorkMsg = (BC_WorkingMessage) receiveWorkingMessage;

				PosSendCashCount(messageID, bcWorkMsg, "0");

				String nozzleNo = bcWorkMsg.getConnectNozzleNo();
				String pumpingPrice = "0";
				String pumpingLiter = "0";
				String pumpingBasePrice = PumpMUtil.getBasePriceFromNozzleNo(nozzleNo);

				LogUtility.getPumpMLogger().debug("ODT���� ��� �޴� ���� nozzle=" + nozzleNo);

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
				LogUtility.getPumpMLogger().debug("[Pump M] ��� ���� : ODT ���� ���� �ʱ�ȭ ");
				CL_WorkingMessage clWorkMsg = (CL_WorkingMessage) receiveWorkingMessage;

				PumpMODTSaleManager.initSaleContent(clWorkMsg.getNozzleNo());
			}
				/**
				 * ���� SC ������ POS �� ���� Total Gauage ��û�� �����ϴ� Protocol �� �����Ͽ�����,
				 * u-Station �� KixxHub �� �����Ǹ��� Total Gaugae �� ÷���ؼ� POS �� �����ϱ�
				 * ������ �Ʒ� ������ ������� �ʴ´�.
				 */
			case IPumpConstant.COMMANDID_S5: {
				// ���� - ���а����� ���� Nothing (���� �������� �� �����Ǹ��� ������ ������ S5 ������ �������
				// �ʴ´�.)
				break;
			}
			/**
			 * 2019-07-29 SoonKwan
			 * SelfODT Version ������ �÷��ش�.
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
	 * �ٸ� Controller �� ���� ���� ������ Pump A �� ������ �����Ѵ�. �ٸ� Controller �� ���� ���� �� �ִ�
	 * ������ ������ ����. DI : ���� ���� ��û : PosAController DK : ����/���� ���� ��û :
	 * PosAController DN : ������ �߱���ȸ ���� : PosAController DO : Preset �ڷ� ��û :
	 * PosAController
	 * 
	 * @param sending_pumpa :
	 *            ������ ����
	 * 
	 */
	@Override
	protected void onReceivingPumpMData(Object sending_pumpa) {
		if (logSSDC == true)	LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/" + "onSendingPumpA()");
		sendMessage((Preamble) sending_pumpa);
	}

	/**
	 * Pump M ���κ��� Pump A �ʱ�ȭ ��û �����Ѵ�. �ʱ�ȭ ������ ������, Pump M �� �����ϰ� �ִ� ���̺��� ���ؼ�
	 * Pump A �� ���ؼ� ������/ODT �� ȯ�������� �����Ѵ�.
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
	 * KixxHub M �� ���� ����� Download Flag �� ���۹޴´�. �̴� ��ǰ���̺��� ������ ����Ǿ��ٴ� �ǹ��̸�, �̷�
	 * ���ؼ� Pump A �� �ܰ� �ʱ�ȭ ������ �����Ѵ�.
	 * 
	 * @param input_downloadFlag :
	 *            ��ǰ �ڵ��� Download Flag
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
	 * ���� ����/��� �� ����pos�� ����
	 * 
	 * @param inOutGubun :
	 *            ����/��� ����
	 */
	private void PosSendCashCount(String messageID, WorkingMessage workMsg,
			String inOutGubun) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/"+ "PosSendCashCount()");

		POS_KL klMsg = null;
		if ("1".equals(inOutGubun)) {
			// ����
			BI_WorkingMessage biWorkMsg = (BI_WorkingMessage) workMsg;
			klMsg = new POS_KL(messageID, biWorkMsg, biWorkMsg.getCash(), inOutGubun, "");
		} else {
			// ���
			BC_WorkingMessage bcWorkMsg = (BC_WorkingMessage) workMsg;
			klMsg = new POS_KL(messageID, bcWorkMsg, bcWorkMsg.getCashCount(), inOutGubun, "");
		}

		Preamble dePreamble = PumpMUtil.createPOSMessagePreamble(null,	SyncManager.DISE_POS_ADAPTER, klMsg, "");

		dePreamble.setIsAckRequired(Preamble.IS_ACK_REQUIRED_NO);
		sendMessage(dePreamble);
	}

	/**
	 * ���� ���� ��쿡�� ���Աݾ��� pos�� �����Ѵ�.
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
	 * ���� ODT ����, PI2, 2016-03-28 - CWI
	 * ���� ���� ��쿡�� ���Աݾ��� pos�� �����Ѵ�.
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
	 * 2016.06.25 CWI processGA �߰� 
	 * GSC Self�� ���� ī�� ���� ���� ��û�� �޾Ƽ� ó���Ѵ�. ���� ��û�� �����Ͽ� ó���� �ؾ� �ϴ� ����
	 * ���������� ���� �ƴ� ����̴�.
	 * 
	 * @param messageID :
	 *            Pump A �κ��� ���� ������ messageID
	 * @param heWorkMsg :
	 *            Pump A �κ��� ���� ����
	 * @param khproc_no :
	 *            KH ó����ȣ
	 */
	private void processGA(String uniqueKey, String messageID, GA_WorkingMessage gaWorkMsg, String khproc_no) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/" + "processGA()");

		sendLockingInfoToPOS(gaWorkMsg.getConnectNozzleNo(), khproc_no,	IConstant.PUMP_SALE_LOCKING);

		// GSCSelf �� ��� �پ��� ���� ó�� ���� ������ ���� ó���� �ʿ䰡 ����. GSCSelf�� ���� �ݾ׺��� �����ݾ��� ���� ���
		// ��� ������ ODT �� ���� ���۵ȴ�.
		// ���� ���� ��û(������) �� ��� ���� ���� ����(0012) �� ����� POS �� �����ϰ� HC ������ Pump A ��
		// �ٷ� �����Ѵ�.
		String type = gaWorkMsg.getMessageType();
		LogUtility.getPumpMLogger().info("type="+type);
		
		/**
		 * GSC Self �ܻ�ŷ�ó ���� �߰� GA_WorkingMsg���� ������ �߰� 2009.04.28
		 */
		
		// 0171 - �������Աݾ� ����(�ű� MessageType)
		if (type.equals(IUPOSConstant.MESSAGETYPE_0171)) { 
			
			LogUtility.getPumpMLogger().info("[T_KH_PUMP_TRHandler] Update for payed info= khproc_no="+ khproc_no);
			try {
				T_KH_PUMP_TRHandler.getHandler().updatePumpPaidInfo_BY_khproc_no(khproc_no, "1");
			} catch (Exception e) {
				LogUtility.getPumpMLogger().error(e.getMessage(), e);
			}

			// ���� �ݾ��� Upos�� ���� �޾� GA������ �־� �ش�.
			String price = gaWorkMsg.getUnityMessage().getPayment_amt();
			gaWorkMsg.setPrice(price);
			
			/**
			 * ���� ���� ��쿡�� ���Աݾ��� pos�� �����Ѵ�.
			 */
			posSendKK2(messageID, uniqueKey, gaWorkMsg, khproc_no);
			
			return;
			
		} else if (type.equals(IUPOSConstant.MESSAGETYPE_0011) || type.equals(IUPOSConstant.MESSAGETYPE_0081)) { 
			
			// �������� �Ǵ� �����ܻ� ��û ����
			String emp_no 			= gaWorkMsg.getUnityMessage().getEmp_no(); 	   // ������ ID
			String custCard_No 		= gaWorkMsg.getCustCardNo(); 				   // �ŷ�ó ī���ȣ
			String ss_crStNum 		= gaWorkMsg.getUnityMessage().getSs_crStNum(); // �ŷ�ó��ȣ
			String ss_carNum 		= gaWorkMsg.getUnityMessage().getSs_carNum();  // �ŷ�ó������ȣ
			String term_id 			= gaWorkMsg.getUnityMessage().getTerm_id() ;   // Default
			String led_code 		= ""; // ODT���� ä���� �ٽ� �÷���� �ϱ⿡ ""�� ����
			String lastPayment_yn 	= ""; // ������ ���� ���� üũ
			String nozzleNo       = gaWorkMsg.getConnectNozzleNo();				  // ���ٹ�ȣ
			String paymentAmt 	  = gaWorkMsg.getUnityMessage().getPayment_amt(); // ���� �ݾ�
			UPOSMessage_TradeCondition affilate_info2 = null;
			String trDate = GlobalUtility.getDateYYYYMMDDHHMMSS();
			
			// ���� & �ܻ� �ŷ� �� ������ 4201�� ��ü
			UPOSMessage_ItemInfo item_infoCash = gaWorkMsg.getUnityMessage().getItem_info();

			GB_WorkingMessage gbMsg = new GB_WorkingMessage();
			UPOSMessage uPosMsg = null;
			// ���� ���� ������ ���
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
			// ���� �ܻ� ������ ���
				
				uPosMsg = ODTUtility_GSCSelf.createUPOSMessageFromWorkingMessage_GSC_For_OISANG(gaWorkMsg, khproc_no);
				
				gbMsg.setMessageType(IUPOSConstant.MESSAGETYPE_0082);
				
			}
		
			uPosMsg.setVan_Res_Code("0000"); //VAN�����ڵ�
			uPosMsg.setTradeCondition(affilate_info2);
			uPosMsg.setLastPayment_yn("0"); // ������ ���� ó��
			uPosMsg.setPosReceipt_no(gaWorkMsg.getUnityMessage().getPosReceipt_no()); // ��ǥ��ȣ
			uPosMsg.setTrdate_creditCard(gaWorkMsg.getUnityMessage().getTrdate_creditCard()); // �ſ�����Ͻ�
			uPosMsg.setTrdate_creditCard(trDate);
			
			gbMsg.setConnectNozzleNo(gaWorkMsg.getConnectNozzleNo());
			gbMsg.setNozzleNo(gaWorkMsg.getNozzleNo());
			gbMsg.setUnityMessage(uPosMsg);
			
			// �������� �� �����ܻ� ���� �ƴ� ���(���ݰŷ�ó, �ſ�ī��, GS����Ʈ, ����(���ʽ� ����), �ܻ�ŷ�ó(���ʽ� ����))
			Preamble pumpPreamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_PUMP_ADAPTER, gbMsg, "") ;
			
			sendMessage(pumpPreamble);
			
		} else {
			
			// PI2, 2016.03.25, CWI
			// ODT ����� ��� �߰��� ���� ����� ������ ���� ��ȣ�� ���� ���� �ʾ� ���� ��û�� �Ѿ�� workingMessage�� ���ٹ�ȣ��
			// hash map�� ������ �� ����� ������ ������ �ð�� ������ ���� ���ٹ�ȣ�� �ҷ� �´�.
			// ����� ������ �ʿ�� �ϴ� key���� unique�� �ŷ�������ȣ�� �����Ѵ�.
			if(gaWorkMsg.getMessageType().equals(IUPOSConstant.MESSAGETYPE_9993)){
				
				GSCSelfPumpingManager.getInstance().createODTNozzleNo(gaWorkMsg.getUnityMessage().getTrx_Proper_No(), gaWorkMsg.getConnectNozzleNo());
			}
		
			// �������� �� �����ܻ� ���� �ƴ� ���(���ݰŷ�ó, �ſ�ī��, GS����Ʈ, ����(���ʽ� ����), �ܻ�ŷ�ó(���ʽ� ����))
			Preamble preamble = PumpMUtil.createUPOSMessagePreamble( messageID,
																	 SyncManager.DISE_PUMP_ADAPTER,
																	 SyncManager.DISE_CMS_MODULE, 
																	 gaWorkMsg.getUnityMessage(), "");
			
			LogUtility.getPumpMLogger().info("[Pump M] Send UPOSMessage (from GSC) to CAT M");
			sendMessage(preamble);
		}
	}

	/**
	 * PI2, �ű� ODT ������ ���� �������� �߰�, 2016-03-25 - CWI
	 * GSC Self�� ���� �� �����Ϸ� ���� �����Ѵ�.
	 * 
	 * 1) UPOSMessage ���� ���� (1) �������� ���� ��� (POS �� ���� Preset ���� ���� ������ ����.) a)
	 * Preset �����ݾװ� ���� �����ݾ� ��ġ ���� (a) ��ġ�� ��� (b) ��ġ���� ���� ��� (2) �������� ���� ��� (ODT
	 * �� ���� ���� ��û�� ���� ������ ����.) a) Preset �����ݾװ� ���� �����ݾ� ��ġ ���� (a) ��ġ�� ��� (b) ��ġ����
	 * ���� ���
	 * 
	 * @param uniqueKey :
	 *            Preamble Object �� Unique Key
	 * @param messageID :
	 *            ������� ���� �� ���� Message ID
	 * @param commandID :
	 *            ������� ���� �� ���� CommandID
	 * @param trWorkMsg :
	 *            ������� ���� �� �ǸſϷ� ����
	 * @param khproc_no :
	 *            KH ó����ȣ
	 */
	private void processGT(String uniqueKey, String messageID, String commandID, GT_WorkingMessage gtWorkMsg, String khproc_no) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/" + "processTR()");

		try {
			// 2009�� 5�� 8�� �߿��� �߰�.
			GlobalUtility.printAnalysisLog("pumpM", khproc_no, gtWorkMsg.getNozzleNo(), "", "�ǸſϷ�", "", 
					new String("LITER : "	+ gtWorkMsg.getLiter() + ", PRICE : " + gtWorkMsg.getPrice()).getBytes(), "", "", "");

		} catch (Exception e) {
			LogUtility.getPumpMLogger().error("!!!!!!!!!!AnalysisLOG ��� ����!!!!!!!!!!!");
		} // end try

		/**
		 * GSC Self�� ��� ���� ������ ������ 0001 ������ POS �� �����ϵ��� �Ѵ�. ���� ���ٸ� ������ �ʴ´�. (�̴�
		 * POS �� ���� Preset �� ���� �������θ� �����Ѵ�. (lastpayment_yn=1 �� ����)
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
	 * �Ҹ� ������ ���� ī�� ���� ���� ��û�� �޾Ƽ� ó���Ѵ�.
	 * 
	 * @param messageID :
	 *            Pump A �κ��� ���� ������ messageID
	 * @param haWorkMsg :
	 *            Pump A �κ��� ���� ����
	 * @param khproc_no :
	 *            KH ó����ȣ
	 */
	private void processHA(String messageID, HA_WorkingMessage haWorkMsg, String khproc_no) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/" + "processHA()");

		sendLockingInfoToPOS(haWorkMsg.getConnectNozzleNo(), khproc_no, IConstant.PUMP_SALE_LOCKING);

		// �Ҹ� ������ ��� �پ��� ���� ó�� ���� ������ ���� ó���� �ʿ䰡 ����. �Ҹ� ������ ���� �ݾ׺��� �����ݾ��� ���� ���
		// ��� ������ ODT �� ���� ���۵ȴ�.
		// ���� ���� ��û(������) �� ��� ���� ���� ����(0012) �� ����� POS �� �����ϰ� HC ������ Pump A ��
		// �ٷ� �����Ѵ�.
		String type = haWorkMsg.getTrType();

		if ("5".equals(type)) {
			/**
			 * [2008.11.20] ������ :�ڵ�ȭ ����, ���� å��, ������ ���� ������, ����ö ���� : �Ҹ��� ODT ��
			 * ������ ����� ����� ������, ���� ���� ��û�� KixxHub ���� ��� ������ ��� ������ ����� �ִ�. ��
			 * �Ҹ��� ODT �� ������ �ް� ������ ���������, ���翡�� ������ �� �ִ�. ��ġ ���� : �Ҹ� ������ ���ݰ���
			 * ������ ������, ���� ������ ��쿡�� ���� ��û/������ ������� �ʴ´�. ��� TR ������ ���� ���ܿ� ���ԵǾ� �ֱ�
			 * ������, �̸� �̿��Ͽ� ���� ������ �����ؼ� POS �� ������.
			 */
			LogUtility.getPumpMLogger().info("[Pump M] Create 0012 & HC directly because of cash request.");

			UPOSMessage uPosMsg = ODTUtility_SoMo.create0012UPOSMessageFromCashRequest_SoMo(haWorkMsg, khproc_no);
			// POS�� ���� ���� uPosMsg �� ������ �ʰ� ���� ������ �󸶶�� POS ������ �����Ѵ�.
			/*
			 * uPosMsg.setLastPayment_yn("0") ; Preamble preamble =
			 * PumpMUtil.createPreamble(messageID,
			 * SyncManager.DISE_PUMP_ADAPTER, SyncManager.DISE_SALE_MODULE,
			 * uPos, "") ; LogUtility.getPumpMLogger().info("[Pump M] Send 0012
			 * (Cash) to Sale M.") ; sendMessage(preamble) ;
			 */

			// 2012.06.28 ksm ���� ������ ��� uposHash�� �������� �ʰ� ����.
			// �Ҹ��� ������ ���ڵ� ��� �����Ͽ� �߰���.
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

			// �������� �ŷ�ó�� ��� �ܰ����� ����(����ī��)
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

			// 2012.06.13 ksm ���δܰ��� 0�̸� �������ܰ��� ������.
			// ���δܰ��� �ʹ� ������ ��ַ� �Ǵ��ϸ� ��� ���...���� ��� �߻��� ���� ��� �ʿ�.
			if (!"0".equals(UnitPrice_after_discount))
				haWorkMsg.setBasePrice(UnitPrice_after_discount);

			// ���ʽ� ������û && �ܻ�ŷ�ó �̸� ( ī����� 01:���ݰŷ�ó, 02:�ܻ�ŷ�ó, 03:�뿪����, 04:���⺸��,
			// 06:�̵��ī��)
			if (type.equals("3") && card_code_base.equals(ICode.CARD_CODE_BASE_02)) {

				SqlSession session = null;
				try {
					session = SqlSessionFactoryManager.openSqlSession();

					// �ܻ�ŷ�ó ���ʽ� ��������(0256)
					strData = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(session, IConstant.POSPORTOCOL_CODEMASTER_0256);
				} catch (Exception e) {
					LogUtility.getPumpMLogger().error(e.getMessage(), e);
				} finally {
					SqlSessionFactoryManager.closeSqlSession(session);
				}

				if ("0".equals(strData)) {
					LogUtility.getPumpMLogger().debug("[Pump M] �ܻ�ŷ�ó ���ʽ� ���� �ź� ��.");

					// 2012.07.12 ksm �������ڵ� ���� ����
					String barCode = "";
					HC_WorkingMessage hcWorkingMsg = null;
					try {
						String printBarCode = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0440);
						if ("1".equals(printBarCode))
							// twsongkis 2015-01-28 ���ο� Barcode Ŭ������ barcode�������� ����
							barCode = Barcode.getBarcodeNumber("3", haWorkMsg.getPrice(), haWorkMsg.getConnectNozzleNo(), khproc_no, null, null, card_code_base);			// �������ڵ�
						
					} catch (Exception e) {
						LogUtility.getPumpMLogger().error(	"[CarWash BarCode] ���� ���ڵ� ��� ���� ��ȸ�� ���� �߻�");
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
								"", "", "", "0", "0", "0", "0", "���ʽ� ���� �ź�",
								"", "�ܻ�ŷ�ó ���ʽ� ����Ʈ ���� ����.", 
								haWorkMsg.getBasePrice(),
								// 2012.06.12 ksm �������ڵ� �߰���.
								barCode);
					}

					Preamble preamble = PumpMUtil.createWorkingMessagePreamble(null,	SyncManager.DISE_PUMP_ADAPTER, hcWorkingMsg, "");
					LogUtility.getPumpMLogger().info("[Pump M] Send WorkMsg to Pump A");
					sendMessage(preamble);

					return;
				}
			}
			//�̰�ȣ 2014-07 ����ī�庸�ʽ���������
			//�ſ�ī�� ������
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
					
					//�Ե� ��ǰ�ڵ� && ī���
					if("0008".equals(track2Check) && "94092000".equals(binChekCardNo8)){
						LogUtility.getPumpMLogger().debug("[Pump M] LOTTE BIN8=" + binChekCardNo8 + "#LOTTE CODE="+track2Check +
								"#khproc_no="+khproc_no);
						PumpMTransactionManager.getInstance().setCorporate(haWorkMsg.getConnectNozzleNo(), true);
					}
					//���� ī���
					else if("943416".equals(binChekCardNo6)||
					   "943417".equals(binChekCardNo6)){
						LogUtility.getPumpMLogger().debug("[Pump M] LOTTE BIN6=" + binChekCardNo6 + "#khproc_no="+khproc_no);
						PumpMTransactionManager.getInstance().setCorporate(haWorkMsg.getConnectNozzleNo(), true);
					}else{
						LogUtility.getPumpMLogger().debug("[Pump M] ����ī����ƴ�. "+" khproc_no="+khproc_no);
					}
										
					
				} catch (Exception e) {
					LogUtility.getPumpMLogger().error(e.getMessage(), e);
				}
			}
			//���ʽ� ������
			if (type.equals("3") ) {
				//�̰�ȣ 2014-07 ����ī�庸�ʽ���������
				//�Ե�ī��: ��ǰ�ڵ� üũ, ����: ī��� �����Ƚ� üũ
				//ODT�ŷ��� kixxhub���� ó����. ������ VAN�������� ó��. 
				
				if (PumpMTransactionManager.getInstance().isCorporate(haWorkMsg.getConnectNozzleNo())) {
					LogUtility.getPumpMLogger().debug("[Pump M] ����ī�� ���ʽ� ���� �ź� ��."+ " khproc_no="+khproc_no);

					String barCode = "";
					HC_WorkingMessage hcWorkingMsg = null;
					try {
						String printBarCode = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0440);
						if ("1".equals(printBarCode))
							barCode = Barcode.getBarcodeNumber("3", haWorkMsg.getPrice(), haWorkMsg.getConnectNozzleNo(), khproc_no, null, null, null);			// �������ڵ�

					} catch (Exception e) {
						LogUtility.getPumpMLogger().error(	"[CarWash BarCode] ���� ���ڵ� ��� ���� ��ȸ�� ���� �߻�");
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
								"00000000000000", "         ", "", "0", "0", "0", "0", "���ʽ����� �ź�",
								"", "����ī�� ���ʽ� ����Ʈ ���� ����", 
								haWorkMsg.getBasePrice(),
								// 2012.06.12 ksm �������ڵ� �߰���.
								barCode);
					}

					Preamble preamble = PumpMUtil.createWorkingMessagePreamble(null,	SyncManager.DISE_PUMP_ADAPTER, hcWorkingMsg, "");
					LogUtility.getPumpMLogger().info("[Pump M] Send WorkMsg to Pump A");
					sendMessage(preamble);

					return;
				}
			}
			uPos = ODTUtility_Common.createUPOSMessageFromWorkingMessage(haWorkMsg, khproc_no);
			// PI2 20160330 twlee ��ִ������� �ֹι�ȣ�� ���ݿ����� �߱� �Ұ��� ���� kixxhub���� ��ִ����� ������ �ʰ� ���ݿ����� �߱޺Ұ� ó��
			if(IUPOSConstant.MESSAGETYPE_0015.equals(uPos.getMessageType()) && ("0101".equals(uPos.getLoyality_type()) && "02".equals(uPos.getCreditCardReading_type()))){
					HC_WorkingMessage hcWorkMsg = new HC_WorkingMessage();
					hcWorkMsg.setNozzleNo(haWorkMsg.getNozzleNo());
					hcWorkMsg.setConnectNozzleNo(haWorkMsg	.getConnectNozzleNo());
					hcWorkMsg.setMode("2");
					hcWorkMsg.setTrType("F");
					hcWorkMsg.setVanMsg("�ֹι�ȣ�� ���ݿ����� �Ұ�");
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
	 * �Ҹ����κ��� �� �ܻ�ŷ� ���� ��û�� �޾Ƽ� �����Ѵ�. ��û ������ ������ card Type �� ���� ������ ����. Card
	 * Type 5 : �ܻ������ ��û (������ PG����) 6 : �ſ�ī�� �н�,����,���� üũ �� ��û ������ ���� ������, POS
	 * ���۽� POS ���� ����(DV����)�Ͽ� �� ���� ����(DW����)�� �޾Ƽ� Pump A ���� �����ϰ�, POS �� �������� ���� ���
	 * KixxHub ��ü ���̺��� ������ �̿��Ͽ� Pump A ���� �����Ѵ�.
	 * 
	 * @param uniqueKey :
	 *            Preamble Object �� Unique Key
	 * @param receiving_pumpa :
	 *            ������� ���� �� �ܻ� �ŷ�ó ��û ����
	 * @param messageID :
	 *            ������� ���� �� ���� Message ID
	 * @param hbWorkMsg :
	 *            ������� ���� �� ����
	 * @param khproc_no :
	 *            KH ó����ȣ
	 */
	private void processHB(String uniqueKey, Object receiving_pumpa, String messageID, HB_WorkingMessage hbWorkMsg, String khproc_no) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/" + "processHB()");

		try {
			String preset_basePrice = "0";// PumpMathUtil.convertBasePriceFromPumpToPOS(hbWorkMsg.getBasePrice())
											// ;

			// �ŷ�ó ���� ��û�� �ܰ��� 0 ���� ��û�Ѵ�. -- ����ȣ
			// ksm 2012.06.13 �ʱ�ȭ�÷� �������� ������. ��.,��
			// preset_basePrice = "0";

			POS_DV dvPumpM = null;
			String cust_card_no = hbWorkMsg.getCardNumber(); // �ŷ�ó ī���ȣ
			String nozzle_no = hbWorkMsg.getConnectNozzleNo(); // ���� ��ȣ
			String goods_code = T_NZ_NOZZLEHandler.getHandler().getGoods_codeBYNozzle_no(nozzle_no); // ��ǰ �ڵ�

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
	 * �پ��� ������ ���� ī�� ���� ���� ��û�� �޾Ƽ� ó���Ѵ�. ���� ��û�� �����Ͽ� ó���� �ؾ� �ϴ� ���� ���������� ���� �ƴ�
	 * ����̴�.
	 * 
	 * @param messageID :
	 *            Pump A �κ��� ���� ������ messageID
	 * @param heWorkMsg :
	 *            Pump A �κ��� ���� ����
	 * @param khproc_no :
	 *            KH ó����ȣ
	 */
	private void processHE(String uniqueKey, String messageID, HE_WorkingMessage heWorkMsg, String khproc_no) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/" + "processHE()");

		sendLockingInfoToPOS(heWorkMsg.getConnectNozzleNo(), khproc_no,	IConstant.PUMP_SALE_LOCKING);

		UPOSMessage uPos = null;
		
		/**
		 * 2016.06.28 WooChul Jung. Bug Fix.
		 * 	���� �ڵ��� ���, ����+���ݰŷ�ó �ε�, 0�� ������ ��� DG ������ POS �� ���۾ȵ�. �� ������ ����+���ݰŷ�ó�� ���
		 * 	paid_ind �� 1 �� ���� ���� ����.
		 * 	���� �������� ������ ���̶�� �ϸ�, paid_ind �� ������ 1 �� ������.
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
		 * �پ��� ���� �ܻ�ŷ�ó ���� �߰� HE_WorkingMsg���� ������ �߰� 2009.04.28 edited by
		 * ykjang
		 */

		if (!GlobalUtility.isNullOrEmptyString(heWorkMsg.getCashCount())	&& GlobalUtility.isNullOrEmptyString(heWorkMsg.getCustCardNo())) {

			// ���� ���� ���
			QM_WoringMessage qmWorkMsg = new QM_WoringMessage();

			qmWorkMsg.setNozzleNo(heWorkMsg.getNozzleNo());
			qmWorkMsg.setConnectNozzleNo(heWorkMsg.getConnectNozzleNo());
			qmWorkMsg.setMode("1");
			qmWorkMsg.setBasePrice(PumpMUtil.convertNumberFormatFromPOSToPump(
					PumpMUtil.getBasePriceFromNozzleNo(heWorkMsg.getConnectNozzleNo()), 4, 2)); // ����ܰ�
			qmWorkMsg.setPrice(heWorkMsg.getPrice());	// ���αݾ�
			qmWorkMsg.setCertiInfo(khproc_no); 				// ���ι�ȣ
			qmWorkMsg.setCertiTime(GlobalUtility.getDateYYYYMMDDHHMMSS()); // ���νð�

			Preamble preamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_PUMP_ADAPTER, qmWorkMsg, "");
			LogUtility.getPumpMLogger().info("[Pump M] Send WorkMsg to Pump A");

			DasNoSelfPumpingManager.getInstance().setWorkingMessage(heWorkMsg.getConnectNozzleNo(), heWorkMsg);
			// ���� ���� ��� fullpumping option�� ī���ȣ Ȯ�� �� �����Ϸ� �� ������ �����Ѵ�.

			sendMessage(preamble);

			/*LogUtility.getPumpMLogger().info("[T_KH_PUMP_TRHandler] Update for payed info= khproc_no=" + khproc_no);
			try {
				T_KH_PUMP_TRHandler.getHandler().updatePumpPaidInfo_BY_khproc_no(khproc_no, "1");
			} catch (Exception e) {
				LogUtility.getPumpMLogger().error(e.getMessage(), e);
			}*/

			/**
			 * ���� ���� ��쿡�� ���Աݾ��� pos�� �����Ѵ�.
			 */
			posSendKK(messageID, uniqueKey, heWorkMsg, khproc_no);

			return;
		} else {
			// ���� ���� �ƴ� ���
			if (IPumpConstant.CB_CUSTOMER_TYPE_CUS_CASH.equals(heWorkMsg.getCustomerType())
					|| IPumpConstant.CB_CUSTOMER_TYPE_GENERAL.equals(heWorkMsg.getCustomerType())
					// 2012.09.21 ksm �����پ����ΰ�� ""���� �ѱ�.
					|| "".equals(heWorkMsg.getCustomerType())) {
				
				//�̰�ȣ 2014-07 ����ī�庸�ʽ���������
				//�Ե�ī��: ��ǰ�ڵ� üũ, ����: ī��� �����Ƚ� üũ
				//ODT�ŷ��� kixxhub���� ó����. ������ VAN�������� ó�� 
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
					
					//�Ե� ��ǰ�ڵ� && ī���
					if("0008".equals(track2Check) && "94092000".equals(binChekCardNo8)){
						LogUtility.getPumpMLogger().debug("[Pump M] LOTTE BIN8=" + binChekCardNo8 + 
								"#LOTTE CODE" + track2Check + "#BounsCardNo=" + BounsCardNo + "#khproc_no="+khproc_no);
						
						PumpMTransactionManager.getInstance().setCorporate(heWorkMsg.getConnectNozzleNo(), true);
						PumpMTransactionManager.getInstance().setCorporateBonus(heWorkMsg.getConnectNozzleNo(), BounsCardNo);
					}
					//���� ī���
					else if("943416".equals(binChekCardNo6)|| "943417".equals(binChekCardNo6)){
						LogUtility.getPumpMLogger().debug("[Pump M] NH BIN6=" + binChekCardNo6 + "#BounsCardNo="+khproc_no +
								"#khproc_no="+khproc_no);
						
						PumpMTransactionManager.getInstance().setCorporate(heWorkMsg.getConnectNozzleNo(), true);
						PumpMTransactionManager.getInstance().setCorporateBonus(heWorkMsg.getConnectNozzleNo(), BounsCardNo);
					}else{
						LogUtility.getPumpMLogger().debug("[Pump M] ����ī����ƴ�. "+" khproc_no="+khproc_no);
					}
									
				
				} catch (Exception e) {
					LogUtility.getPumpMLogger().error(e.getMessage(), e);
				}
			
				////�̰�ȣ 2014-07 ����ī�庸�ʽ���������
				//����ī�� ������ ��� HE ����(�پ���)���� ���ʽ�ī���ȣ�� ���� ��, ���ʽ�ī�� �������� ��ó�� ���� ���μ��� ����.
				if( PumpMTransactionManager.getInstance().isCorporate(heWorkMsg.getConnectNozzleNo())){
					heWorkMsg.setBonusCard("");
					LogUtility.getPumpMLogger().debug("[Pump M] BonusCard Null ó�� �Ϸ�."+ " khproc_no="+khproc_no);
					
				}
				
				// ���ݰŷ�ó or �Ϲ� �ſ�ī�� �ŷ�
				POSHeader posMsg = PumpMODTSaleManager.getCustPOSPumpM(heWorkMsg.getConnectNozzleNo());
				WorkingMessage workMsg = PumpMODTSaleManager.getCustPumpAPumpM(heWorkMsg.getConnectNozzleNo());
				POS_DW dwPumpM = null;
				boolean isDiscountCargo = false;

				if (IPumpConstant.CB_CUSTOMER_TYPE_CUS_CASH.equals(heWorkMsg.getCustomerType())) {
					// ���ݰŷ�ó�� ��� �ܰ��� �����Ų��.

					if ((posMsg != null) && (posMsg instanceof POS_DW)) {
						dwPumpM = (POS_DW) posMsg;
						LogUtility.getPumpMLogger().info(dwPumpM.toString());

						// �ŷ�ó ���� (05: ����ȭ��) && ��ǰ�ߺз� ����
						if (ICode.CUST_CD_ITEM_05.equals(dwPumpM.getCust_cd_item())	
								&& "12".equals(dwPumpM.getGoods_code().substring(0, 2)))
							isDiscountCargo = CustUtil.isDiscountCargo(heWorkMsg.getBonusCard(), heWorkMsg.getCardNumber());

						if (isDiscountCargo)
							if (Double.parseDouble(PumpMUtil.convertNumberFormatFromPOSToPump(dwPumpM.getBasePrice(), 4, 2)) != 0)
								heWorkMsg.setBasePrice(PumpMUtil.convertNumberFormatFromPOSToPump(dwPumpM.getBasePrice(), 4, 2));

						// ���ݰŷ�ó�̸鼭 ���������, ����ȭ��, ��������, �����, �����ýð� �ƴ� �ŷ�ó�� PL�ܰ�����
						// ����
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

					// ��û ������ �����Ѵ�. �̴� ���� ���� ������ ���� �����, ������� ���ؼ� ���Ǿ� ����.
					DasNoSelfPumpingManager.getInstance().setWorkingMessage(heWorkMsg.getConnectNozzleNo(), heWorkMsg);

					if (!GlobalUtility.isNullOrEmptyString(heWorkMsg.getCashCount())) {
						// ���� �ŷ�ó�� �������� �����ϴ� ���
						QM_WoringMessage qmWorkMsg = new QM_WoringMessage();

						qmWorkMsg.setNozzleNo(heWorkMsg.getNozzleNo());
						qmWorkMsg.setConnectNozzleNo(heWorkMsg.getConnectNozzleNo());
						qmWorkMsg.setMode("1");
						qmWorkMsg.setBasePrice(heWorkMsg.getBasePrice()); // ����ܰ�						
						qmWorkMsg.setLiter(heWorkMsg.getLiter()); // ���θ���
						qmWorkMsg.setPrice(heWorkMsg.getPrice()); // ���αݾ�
						
						//	2012.10.10 ksm �����پ��� ���ݰŷ�ó ó���� ���ͽ��� ����
						// ���Ͱ��� ������ ODT���� ���͸� �����ߴٴ� ����.
						// ���Ͱ��� ���� ��� �ŷ�ó�ܰ��� ����Ͽ�  
						// 1. "(����*�ŷ��ܰ�) >= ���Աݾ�" �̸� ���Աݾ����� ����
						// 2. "(����*�ŷ��ܰ�) < ���Աݾ�" �̸� ���ͷ� ����
						if(!"0".equals(PumpMUtil.convertNumberFormatFromUPOSToStandardFormat(heWorkMsg.getLiter()))){
							//1. ���Ϳ� �ŷ��ܰ��� �����ݾ� ����.
							String calcAmt = GlobalUtility.multiple(PumpMUtil.convertLiterFromPumpToPOS(heWorkMsg.getLiter()), 
									PumpMUtil.convertBasePriceFromPumpToPOS(heWorkMsg.getBasePrice()));
							
							// 2. �����ݾװ� ���Աݾ� ��
							if ( (int)Double.parseDouble(calcAmt) >= Integer.parseInt(heWorkMsg.getCashCount()) ){
								qmWorkMsg.setLiter("0000000"); 									// ���θ���
								qmWorkMsg.setPrice(heWorkMsg.getCashCount()); 	// ���αݾ�(���Աݾ�)
								heWorkMsg.setPrice(heWorkMsg.getCashCount());	// �Ž����� ó���� ���� ���αݾ׿� ���Աݾ��� ����.
							}else{
								heWorkMsg.setPrice(heWorkMsg.getCashCount());	// �Ž����� ó���� ���� ���αݾ׿� ���Աݾ��� ����.
							}
						}
						
						qmWorkMsg.setCertiInfo(khproc_no); // ���ι�ȣ
						qmWorkMsg.setCertiTime(GlobalUtility.getDateYYYYMMDDHHMMSS()); // ���νð�

						Preamble preamble = PumpMUtil.createWorkingMessagePreamble(null, SyncManager.DISE_PUMP_ADAPTER, qmWorkMsg, "");
						LogUtility.getPumpMLogger().info("[Pump M] Send WorkMsg to Pump A");
						sendMessage(preamble);

						/**
						 * ���� ���� ��쿡�� ���Աݾ��� pos�� �����Ѵ�.
						 */
						posSendKK(messageID, uniqueKey, heWorkMsg, khproc_no);

						return;
					}
				}

				if (DasNoSelfPumpingManager.getInstance().isFullPumping(heWorkMsg.getConnectNozzleNo(),
						GlobalUtility.getStringValue(heWorkMsg.getPrice()))) {
					// ���� ������ ���
					// ��û ������ �����Ѵ�. �̴� ���� ���� ������ ���� �����, ������� ���ؼ� ���Ǿ� ����.
					DasNoSelfPumpingManager.getInstance().setWorkingMessage( heWorkMsg.getConnectNozzleNo(), heWorkMsg);
					uPos = DasNoSelfPumpingManager.getInstance().getUPOSMessageBeforePumping(heWorkMsg, khproc_no);
					/**
					 * 2016.03.31 WooChul Jung.
					 * 	move filler1 to selfpayment_type
					 */
					uPos.setSelfPayment_type(IConstant.UPOSMESSAGE_PAID_TYPE_20);
				} else {
					// ���� ������ �ƴ� ���
					// ��û ������ �����Ѵ�. �̴� ���� ����� �ּ� �ݾ׺��� Ŭ ��� �����, ������� ���ؼ� ���Ǿ�
					// ����.
					DasNoSelfPumpingManager.getInstance().setWorkingMessage(heWorkMsg.getConnectNozzleNo(), heWorkMsg);

					uPos = ODTUtility_Common.createUPOSMessageFromWorkingMessage(heWorkMsg,	khproc_no);
					/**
					 * 2016.03.31 WooChul Jung.
					 * 	move filler1 to selfpayment_type
					 */
					uPos.setSelfPayment_type(IConstant.UPOSMESSAGE_PAID_TYPE_10);}

				if (isDiscountCargo)
					uPos.setFiller2("U");

				// ���� �ŷ�ó�� ���
				// DasNoSelfPumpingManager.getInstance().isFullPumping �� method��
				// ����
				// PumpMODTSaleManager �� class�� �ʱ�ȭ�ϱ� ������ �ٽ� �־� �ش�.
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
				// �ܻ��� ��� �Ǵ� ���� ���� ���
				// ���� �ŷ�ó�� �������� ó������ ����. 2009.04.29 by ������
				QM_WoringMessage qmWorkMsg = new QM_WoringMessage();

				qmWorkMsg.setNozzleNo(heWorkMsg.getNozzleNo());
				qmWorkMsg.setConnectNozzleNo(heWorkMsg.getConnectNozzleNo());

				// �ܻ�ŷ�ó�� ������ ���� ����(DW)�� ���� �ѵ��� üũ�Ѵ�.
				POSHeader posMsg = PumpMODTSaleManager.getCustPOSPumpM(heWorkMsg.getConnectNozzleNo());
				POS_DW dwPumpM = null;
				
				if ((posMsg != null) && (posMsg instanceof POS_DW))
					dwPumpM = (POS_DW) posMsg;

				qmWorkMsg.setBasePrice(PumpMUtil.convertNumberFormatFromPOSToPump(dwPumpM.getBasePrice(), 4, 2)); // �ǸŴܰ�

				// tatsuno_hs okdhp7 (2013.02) -> �پ��뼿��(����) �ܻ�ó�� �������� 
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
						// ī�� ���� ������ �������� ���
						qmWorkMsg.setMode("1");
					} else {
						Double accPrice = Double.parseDouble(heWorkMsg.getPrice());
						Double accLiter = GlobalUtility.getValueByCertainDecimal(	
								accPrice / Double.parseDouble(PumpMUtil.convertBasePriceFromPumpToPOS(heWorkMsg.getBasePrice())),3);

						if (ICode.ADJBASE_CODE_LIMIT_01.equals(dwPumpM.getAdjbase_code_limit())) {
							// �ѵ�������� = ����
							// �ܷ� = �ѵ��ܷ� - ������뷮

							LogUtility.getPumpMLogger().debug("PumpMathUtil.convertLiterFromPumpToPOS(heWorkMsg.getLiter()) : "
									+ PumpMUtil.convertLiterFromPumpToPOS(heWorkMsg.getLiter()));

							if (CustUtil.isCustRecipt(remainAmount, accLiter, dwPumpM.getRentlimit_proc_ind_overlimit()))
								qmWorkMsg.setMode("1");
							else {
								qmWorkMsg.setMode("2");
								errMessage = "�ѵ� �ʰ�";
							}

						} else {
							// �ѵ�������� = �ݾ�
							// �ѵ����� ������ �ݾ��� ��� ODT���� ���õ� �ݾ׿� �ŷ��ܰ��� ���� ������ �ѵ��ܷ���
							// ���Ͽ� �ŷ�����,�źθ� �Ǵ��Ѵ�.
							double setLitter = Double.parseDouble(heWorkMsg.getPrice()) / Double.parseDouble(dwPumpM.getBasePrice());

							LogUtility.getPumpMLogger().debug("[Pump M] setLitter=" + setLitter);
							
							remainAmount = remainAmount / Double.parseDouble(dwPumpM.getBasePrice());

							if (CustUtil.isCustRecipt(remainAmount, setLitter,dwPumpM.getRentlimit_proc_ind_overlimit()))
								qmWorkMsg.setMode("1");
							else {
								qmWorkMsg.setMode("2");
								errMessage = "�ѵ� �ʰ�";
							}
						}
					}
				} else {
					// ��ǰ�ڵ尡 Ʋ�� ��� ���� ����
					qmWorkMsg.setMode("2");
					errMessage = "��ǰ�ڵ� ����ġ";
				}

				// �ܰ��� ���� ���� pl�� �ִ��� ������ ������ ���õ� ������ ��ġ���� �ʴ� ��� �ܰ��� 0���� �����´�.
				if ("0".equals(dwPumpM.getBasePrice()))
					qmWorkMsg.setMode("2");

				DasNoSelfPumpingManager.getInstance().setWorkingMessage( heWorkMsg.getConnectNozzleNo(), heWorkMsg);

				qmWorkMsg.setLiter(heWorkMsg.getLiter()); // ���θ���
				qmWorkMsg.setPrice(heWorkMsg.getPrice()); // ���αݾ�
				qmWorkMsg.setCertiInfo(khproc_no); // ���ι�ȣ
				qmWorkMsg.setCertiTime(GlobalUtility.getDateYYYYMMDDHHMMSS()); // ���νð�

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
					// �ŷ�ó ���� ���н� �ŷ�ó ���� clear
					PumpMODTSaleManager.initSaleContent(heWorkMsg.getConnectNozzleNo());

					QL_WorkingMessage qlWrkMsg = new QL_WorkingMessage(heWorkMsg.getNozzleNo(), 
							heWorkMsg.getConnectNozzleNo(), 
							String.valueOf(receipt.length()), 
							receipt, 
							"0",
							"", 
							"");

					preamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey, SyncManager.DISE_PUMP_ADAPTER, qlWrkMsg, "");
					// ���� ���� ����
					sendMessage(preamble);
					qmWorkMsg = null;
					dwPumpM = null;
				}
			}
		}
	}

	/**
	 * ������� ���� Preset ������ �޾Ƽ� ������ �����Ѵ�. 1. T_KH_PUMP_TR Table update 2. POS ��
	 * Preset ���� ����
	 * 
	 * @param uniqueKey :
	 *            Preamble Object �� Unique Key
	 * @param messageID :
	 *            ������� ���� �� ���� Message ID
	 * @param hfWorkMsg :
	 *            ������� ���� �� ����
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
			// 2009�� 4�� 21�� �߿��� �߰�.
			GlobalUtility.printAnalysisLog("pumpM", khproc_no, hfWorkMsg.getNozzleNo(), "", "������", "", dqPumpMMsg.convertPOSContent(), "", "", "");

		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(	"[Pump M] !!!!!!!!!!AnalysisLOG ��� ����!!!!!!!!!!!", e);
		} // end try

		sendMessage(dqPreamble);
	}

	/**
	 * ������� ���� �� ������ODT���� �� ���� ���� ó���� �Ѵ�.
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

	// 2012.06.07 ksm �Ҹ��� �������ڵ� POS����
	// OPT�� ���� ���� ���ڵ带 DB�� ���������� �ӽ÷� ����ϴ� �������̹Ƿ� DB���� ������.
	// 2016. 4. 14. ���� 15:48:31, PI2, twsongkis ���ο� ���ڵ� ���� ���� / ������ DB ����
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
			String qlBarcodeStr = carWashBarCode; // �ý���(X)��ȿ����(YMMDD)�������αݾ�(XX)SEQ(XXXX)��������ڵ�(XXXX)
			
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
					//�μ�Ʈ 
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
					//�μ�Ʈ 
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
			String qlBarcodeStr = ql_WMsg.getBarCode(); // �ý���(X)��ȿ����(YMMDD)�������αݾ�(XX)SEQ(XXXX)��������ڵ�(XXXX)
			
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
					//�μ�Ʈ 
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
					//�μ�Ʈ 
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

	
	/** PI2, �ű� ODT �߰��� ���� ���μ��� �߰�, 2016-03-23 - CWI
     * ksm 2011.12.13 PumpAController�� EN���� ���� �� SEQ DB Insert/Update ������.
     * twsongkis 2016. 4. 14. ���� 15:48:31, PI2, songkis,  DB Insert/Update ���̺� ����.
     * ���ϼ������� �̸������� POS�� EN���� ������ �ȵȴٰ� ���� ��û��. (�븮�� ����1�� ������ ���� ��û CSR)
     * @param workMsg
     */
	private void processOPTGSCSelfBarcode(WorkingMessage workMsg, String khproc_no) {

		BS_WorkingMessage bsMsg = (BS_WorkingMessage) workMsg;

		if("".equals(bsMsg.getBarcode()) == false) {
			
			// POS�� EN ���� �۽� ó��
			String getMessageID = GlobalUtility.getUniqueMessageID();
			
			try {
				POS_EN enPumpMMsg = new POS_EN(getMessageID, khproc_no, bsMsg, T_KH_STOREHandler.getHandler().getWorkingDate());

				Preamble preambleM = PumpMUtil.createPOSMessagePreamble(GlobalUtility.getUniqueMessageID(), SyncManager.DISE_SALE_MODULE, enPumpMMsg, "");
				LogUtility.getPumpMLogger().info("[Pump M] Send EN Content to Sale M");
				sendMessage(preambleM);
			} catch(Exception e) {
				LogUtility.getPumpMLogger().error(e.getMessage(), e);
			}

			// POS�� Insert ������ KixxHUB���� Insert ó�� ����
			/* 
			T_KH_CARWASH_COUPON_LISTData carwash_coupon_listSLT = new T_KH_CARWASH_COUPON_LISTData();
			T_KH_CARWASH_COUPON_LISTData carwash_coupon_list = new T_KH_CARWASH_COUPON_LISTData();
			String bsBarcodeStr = bsMsg.getBarcode(); // �ý���(X)��ȿ����(YMMDD)�������αݾ�(XX)SEQ(XXXX)��������ڵ�(XXXX)

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
					// �μ�Ʈ
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
					// �μ�Ʈ
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
	 * ������� ���� �� ���� �Ϸ� ������ ó���Ѵ�. ���� �Ϸ� �ݾ��� 0���� ��� �����Ϸ������ �����ϴ� ��찡 �ִ�.
	 * (PumpMTransactionManager.needToSendToPOS_IFPumpCompleted_ZeroOne() �Լ� ����)
	 * �����Ϸ� �ݾ��� 0���� �ƴ� ���, T_KH_PUMP_TR ���̺��� update �ϰ�, POS �� �����Ϸ� ����(DG����)��
	 * �����Ѵ�. �׸��� �����Ϸ� ����, ���ܰ��� �������� �ܰ��� ���Ͽ� �ٸ� ��� �����ܰ� ������ �����Ѵ�.
	 * 
	 * @param uniqueKey :
	 *            Preamble Object �� Unique Key
	 * @param messageID :
	 *            ������� ���� �� ���� Message ID
	 * @param s4WorkMsg :
	 *            ������� ���� �� ����
	 * @param khproc_no :
	 *            KH ó����ȣ
	 */
	private void processS4(String uniqueKey, String messageID, S4_WorkingMessage s4WorkMsg, String khproc_no) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/" + "processS4()");

		int nozzle_noInt = Integer.parseInt(s4WorkMsg.getNozzleNo());

		String price = PumpMPriceManager.getRightPumpPrice(nozzle_noInt, s4WorkMsg.getPrice());

		// // �����⿡�� �ö�� �ܰ��� KIXXHUB�� �˰� �ִ� �ܰ��� Ʋ�����, KIXXHUB�� �˰� �ִ� �ܰ��� �����Ѵ�.
		// 09.03.27 �ǰ� �ۼ� -- ����ȣ
		// ������ �ܰ��� �������� �ƴϰ�, �ܰ������� ����� �̷����� �ʾ��� ��쿡�� ������ ǥ�� �ݾװ� ���� ���� �ݾ��� ���̰�
		// �߻��ϴ� ���� ��, ������ �ܰ��� �ٸ� ��� �ܰ� ������ ���ημ����� �۵����� �ʴ� ���� �� �Ҹ��� �ܰ� ���� �޴� ����
		// ��쿡 �ܰ� ���������� �ʴ� ������ ���̵� ����Ʈ�� �߻��Ͽ� �� ���μ����� �������� �ʴ´�.
		// �� ������ �߰��� ���ؼ� ����ö ����� ����������, �������� �ʴ� ���� �´ٴ� �ǰ��� ���� ���� ����. ���� �� ������ ����
		// �Ǿ� ���� ��ġ�� �̷� �� ���� ����.
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

			// ���� �Ϸ� �ڷ��� ���� * �ܰ��� ���� �ݾ׺��� 100 �� �̻��� ���̰� �߻� �Ͽ��� ���, ���� * �ܰ��� ������
			// �����Ѵ�.
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
				// 2009�� 4�� 21�� �߿��� �ۼ�.
				GlobalUtility.printAnalysisLog("pumpM", khproc_no, s4WorkMsg
						.getNozzleNo(), String
						.valueOf(IConstant.STATE_PUMP_STATECODE_654), "�����Ϸ�", "",
						dgPumpMMsg.convertPOSContent(), "", "", "");

			} catch (Exception e) {
				LogUtility.getPumpMLogger().error(	"[Pump M] !!!!!!!!!!AnalysisLOG ��� ����!!!!!!!!!!!", e);
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
				// �����Ϸ� ���� (DG) �� Sale M ���� ����
				Preamble preamble = PumpMUtil.createPOSMessagePreamble(null,	SyncManager.DISE_SALE_MODULE, dgPumpMMsg, "");
				LogUtility.getPumpMLogger().info("[Pump M] Send DG Content to Sale M");
				sendMessage(preamble);

				// Thread.sleep(100) ; // ksm 2012.03.06 �����Ϸ� �� �����ܰ� ������ �����̵�.
				// �����ܰ��� �� ���� (���� : Preset �����̸鼭, Preset ���� �ܰ��� ���ܰ��� �ٸ� ���.)
				notifyBasePriceToPumpAfterPumpCompletion(khproc_no, s4WorkMsg.getNozzleNo());
				
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		}
	}

	/**
	 * ������κ��� ��ī�� ���� ��û����(S9����) �� �޾Ƽ� �����Ѵ�. ������ ������ S9 ������ mode �� ���� ������ ����
	 * ���еȴ�. Mode 0 : �� ī�忡�� ���� �Ϸù�ȣ�� ������ ��û��. (���� ������ ī������ ���θ� üũ �� ���� �ƴϸ�
	 * ��ī��� �ν��Ѵ�.) 1 : ���۾����� �Էµ� ���������� ������ ��û��. 2 : ������ ��ȸ�� ��û��. Mode 2 �� �����ϰ��
	 * POS ���۽� POS ���� ��û�Ͽ� �� ���� ������ Pump A �� �����ϰ�, POS �� �������� ���� ��� KixxHub ��
	 * ���̺��� ������ �̿��Ͽ� Pump A ���� �����Ѵ�.
	 * 
	 * @param uniqueKey :
	 *            Preamble Object �� Unique Key
	 * @param receiving_pumpa :
	 *            ������� ���� ��û���� ����
	 * @param messageID :
	 *            ������� ���� �� ���� Message ID
	 * @param s9WorkMsg :
	 *            ������� ���� �� ����
	 */
	private void processS9(String uniqueKey, Object receiving_pumpa,
			String messageID, S9_WorkingMessage s9WorkMsg) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info(
					"SSDC/" + "PUMP_M/" + "PupmAController/" + "processS9()");

		int s9Mode = Integer.parseInt(s9WorkMsg.getMode());
		switch (s9Mode) {
		case 0: {
			// �� ī�忡�� ���� �Ϸù�ȣ�� ������ ��û�մϴ�.
			LogUtility.getPumpMLogger().info(
					"[Pump M] �� ī��(or ������ ī��)���� ���� �Ϸù�ȣ�� ������ ��û�� �޾ҽ��ϴ�");
			// ���� ������ ī������ �˻��ϰ� ������ ī�尡 �ƴ� ��� �� ī�带 �����մϴ�.
			WorkingMessage workMsg = S9Util
					.getPGWorkingMessageForChargingPerson(s9WorkMsg);
			if (((PG_WorkingMessage) workMsg).getTransType().equals("0")) {
				// ������ ī���� ���
				LogUtility.getPumpMLogger().info("[Pump M] ������ ī���Դϴ�.");
				LogUtility.getPumpMLogger().info(
						"[Pump M] �������� ���� �մϴ�. ���ο� KH ó����ȣ�� �����մϴ�."
								+ uniqueKey);

				resetCustDataNewKHProcNo(workMsg);
				Preamble pgPreamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey,
						SyncManager.DISE_PUMP_ADAPTER, workMsg, "");

				LogUtility.getPumpMLogger().info(
						"[Pump M] ������ ī�������� �����մϴ�." + uniqueKey);

				sendMessage(pgPreamble);
			} else {
				String khproc_no = ""; // ������� ����.

				// ������ ī�尡 �ƴ� ��� �� ī�� ����.
				LogUtility.getPumpMLogger().info(
						"[Pump M] ������ ī�尡 �ƴմϴ�. �� ī������ �����մϴ�.");
				LogUtility.getPumpMLogger().info("[Pump M] POS ���� �� ī������ ��û�� �մϴ�.");

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
			// ���۾����� �Էµ� ���������� ������ ��û�մϴ�.
			// �ߺ� ������ ���� ��� ��� ���������� �����մϴ�.
			LogUtility.getPumpMLogger().info("[Pump M] POS ���� ���������� ��û�մϴ�.");
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
			// ������ ����� ��û�մϴ�.
			LogUtility.getPumpMLogger().info("[Pump M] ������ ī�� ��ȸ ��û�� �޾ҽ��ϴ�");
			WorkingMessage workMsg = S9Util
					.getPGWorkingMessageForChargingPerson(s9WorkMsg);
			Preamble pgPreamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey,
					SyncManager.DISE_PUMP_ADAPTER, workMsg, "");
			LogUtility.getPumpMLogger().info("[Pump M] ������ ī�� ��ȸ ��û�� ���� ������ �����մϴ�.");
			sendMessage(pgPreamble);
			break;
		}
		}
	}

	/**
	 * ������� ���� ������ ��ȣ �ο� ��û�� �����մϴ�.
	 * 
	 * @param uniqueKey :
	 *            Preamble Object �� Unique Key
	 * @param messageID :
	 *            ������� ���� �� ���� Message ID
	 * @param sfWorkMsg :
	 *            ������� ���� �� ����
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
	 * ������ �Ǹ� �Ϸ� ������ ������ ������ �����Ѵ�. 1. �����⿡ ACK ������ �۽��Ѵ�. 2. ����/��ǰ��/�ܻ� ������ SH ������
	 * ���Ե� ��� UPOSMessage ������ ���� �����Ѵ�. �ܻ� - 0082 ��ǰ�� - 0022 ���� - 0012 3. POS ��
	 * ���� �Ϸ� ���� (MessageType = 0001) �� �����Ѵ�. (lastpayment_yn=1 �� ����) 4.
	 * SaleContent �� ����� ������ �ʱ�ȭ�Ѵ�.(�����Ѵ�)
	 * 
	 * @param uniqueKey :
	 *            Unique Key
	 * @param shWorkMsg :
	 *            SH WorkingMessage
	 * @param khproc_no :
	 *            KH ó����ȣ
	 */
	private void processSH(String uniqueKey, SH_WorkingMessage shWorkMsg,
			String khproc_no) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/" + "processSH()");

		// �������濡 ���� �׸� �߰�(2015.11.23)
		String term_Res_Code = shWorkMsg.getTerm_Res_Code(); 			// �ܸ��� ���� �ڵ�
		String txt_Direction = shWorkMsg.getTxt_Direction(); 			// ��������
		    
		ACK_WorkingMessage ackWorkMsg = new ACK_WorkingMessage(shWorkMsg.getMessageID(), shWorkMsg.getNozzleNo());

		// 1. �����⿡ ACK ������ �۽��Ѵ�.
		Preamble ackPreamble = PumpMUtil.createWorkingMessagePreamble(uniqueKey, SyncManager.DISE_PUMP_ADAPTER, ackWorkMsg, "");
		
		LogUtility.getPumpMLogger().info("[Pump M] Receive SH from Recharge, so Send ACK to Pump A");
		sendMessage(ackPreamble);

		// 2. ����/��ǰ��/�ܻ� ������ SH ������ ���Ե� ��� UPOSMessage ������ ���� �����Ѵ�.
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

		// 3. POS �� ���� �Ϸ� ���� (MessageType = 0001) �� �����Ѵ�. (lastpayment_yn=1 �� ����)
		if (PumpMODTSaleManager.shouldSend0001UPOSMessageToSaleM(shWorkMsg.getNozzleNo(), khproc_no, shWorkMsg)) {
			
			UPOSMessage uPosMsg = CreateUPOSMessage.createUPOSMessage_0001(IUPOSConstant.DEVICE_TYPE_3O, khproc_no, "1", term_Res_Code, txt_Direction);
			
			Preamble posPreamble = PumpMUtil.createUPOSMessagePreamble(	SyncManager.getUniqueKey(), 
					SyncManager.DISE_PUMP_MODULE,
					SyncManager.DISE_SALE_MODULE, 
					uPosMsg, "");

			LogUtility.getPumpMLogger().info("[Pump M] Send 0001 UPOSMessage to SaleM.");

			sendMessage(posPreamble);
		}

		// 4. SaleContent �� ����� ������ �ʱ�ȭ�Ѵ�.(�����Ѵ�)
		PumpMODTSaleManager.initSaleContent(shWorkMsg.getNozzleNo());
	}

	/**
	 * 
	 * �Ҹ��� �ǸſϷ����� (ST ����) �� ���� ������ �ǽ��Ѵ�. ST �������� ���� �� �ܻ� �׸��� POS �� ������ ���� ���� ��
	 * ��������� ����, ����� ���� ������� ����� �ִ�. �̷��� ������� �� �����Ͽ��� POS �� �����ϵ��� �Ѵ�.
	 * 
	 * @param uniqueKey :
	 *            Unique Key
	 * @param messageID :
	 *            Message ID
	 * @param commandID :
	 *            Command ID
	 * @param stWorkMsg :
	 *            �Ǹ� �Ϸ� ���� (ST ����)
	 * @param khproc_no :
	 *            KH ó����ȣ
	 */
	private void processST(String uniqueKey, String messageID, String commandID, ST_WorkingMessage stWorkMsg, String khproc_no) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/" + "processST()");

		// �ܻ� �ŷ�ó�� ��� ST ���� �� ���� DW(POS Protocol) HD(Pump A Spec) ������
		// �̿��Ͽ� �ܻ� ������ �����ؼ� POS �� �����Ѵ�.
		String nozzleNo = stWorkMsg.getConnectNozzleNo();
		// String cardNo = stWorkMsg.getCardNo(); // (SOMO SELF) ī�� ��ȣ
		// String bonusCardNo = stWorkMsg.getBonusCardNo(); // (SOMO SELF) ���ʽ�
		// ī�� ��ȣ
		Vector<PB_ST_TrInfo> trInfoVector = stWorkMsg.getTrInfoVector(); // TR_TYPE,
																			// MODE,
																			// ����
																			// ����,
																			// ����
																			// �ܰ�,
																			// ����
																			// �ݾ�

		int size = 0;
		if (trInfoVector != null) {
			size = trInfoVector.size();
		}

		// 2009�� 5�� 8�� �߿��� �߰�.
		// LOG ���
		StringBuffer dataLog = new StringBuffer();
		for (int i = 0; i < size; i++) {
			PB_ST_TrInfo trInfo = trInfoVector.get(i);

			dataLog.append("Mode : " + trInfo.getMode() + ", TRTYPE : " + trInfo.getTrType() + " / ");

		} // end for

		try {
			GlobalUtility.printAnalysisLog("pumpM", khproc_no, stWorkMsg.getNozzleNo(), "", "�ǸſϷ�", "", dataLog.toString().getBytes(), "", "", "");

		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(	"[Pump M] !!!!!!!!!!AnalysisLOG ��� ����!!!!!!!!!!!", e);
		} // end try

		for (int i = 0; i < size; i++) {
			PB_ST_TrInfo trInfo = trInfoVector.get(i);
			String mode = trInfo.getMode(); // 1 : ����
			// 2 : �����
			// 3 : ����� ( ����, POS ������ Preset �� ���� �� �����ݾ�)
			// 4 : ����� ����
			// 5 : ����� ����

			String trType = trInfo.getTrType(); // 0 : POS �� ������ Preset
			// 1 : �ſ�ī�� ����
			// 2 : �ſ�ī�� ���� ���
			// 5 : ���� ����
			// 7 : �ܻ� ���� ��û
			// A : GS ���ʽ�/GS & ���ʽ� �̿� ����
			// B : GS ���ʽ�/GS & ���ʽ� �̿� ���
			// C : ��Ÿ(myLG ����) ���ʽ� ī�� �̿� ����
			// D : ��Ÿ(myLG ����) ���ʽ� ī�� �̿� ���
			// F : ���ݿ����� ����

			/*
			 * TrType ( 5: ����, 7: �ܻ� ), Mode (1:����, 2:�����, 3:�����, 4:����ҽ���,
			 * 5:����ν���, 6: �������� ��ҹ�ư Ŭ��) �ܻ� �ŷ��� ���� ������ ��� �ܻ� ������ �����ؼ� POS ��
			 * �����Ѵ�.
			 */
			if ("7".equals(trType) && "1".equals(mode)) {
				POSHeader posMsg = PumpMODTSaleManager.getCustPOSPumpM(nozzleNo);
				POS_DW dwPumpM = null;
				if ((posMsg != null) && (posMsg instanceof POS_DW)) {
					dwPumpM = (POS_DW) posMsg;
				}

				// ??? ksm �� array�� �޾Ƽ� ó������?
				ArrayList<UPOSMessage> uPosMsgArray = ODTUtility_SoMo	.createUPOSMessage(nozzleNo, dwPumpM, trInfo, khproc_no);
				if ((uPosMsgArray != null) && (uPosMsgArray.size() > 0)) {
					LogUtility.getPumpMLogger().info(	"[Pump M] Send Created UPOSMessage(0082) to SaleM. size=" + uPosMsgArray.size());

					// 2012.06.28 ksm �ܻ� ������ ��� uposHash�� �������� �ʰ� ����.
					// �Ҹ��� ������ ���ڵ� ��� �����Ͽ� �߰���.
					PumpMODTSaleManager.addUPOSMessage(nozzleNo, uPosMsgArray.get(0));

					for (int j = 0; j < uPosMsgArray.size(); j++) {
						UPOSMessage uPosMsg = uPosMsgArray.get(j);

						Preamble posPreamble = PumpMUtil.createUPOSMessagePreamble(SyncManager.getUniqueKey(),
								SyncManager.DISE_PUMP_MODULE,
								SyncManager.DISE_SALE_MODULE, 
								uPosMsg, "");
						sendMessage(posPreamble);
						/*
						 * 2012.06.28 ksm �ܻ��� ��� 0082 �� �ι� POS�� ���۵�. Ȯ���ʿ�. 0001
						 * ���۽� ������ ��ȸ�ϸ鼭 �ɸ��°����� ����. 0.06 sec ������.
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
				// ���� �̸������� ���(mode =3) POS�� 0012 ������ �������� �ʾ���. �����ϵ��� ����.
				// 92�� �ǸſϷ� ������ ���� ���Աݾ� �Ѿ��.
				// 0��0���� ������ ������ �ǹǷ� �����ݾ� üũ�Ͽ� �б��Ŵ.
				
				// ���� ���� ������ ���� POS �� �����ؾ� �Ѵ�.
				
				String paymentAmt = trInfo.getPrice();				
				
				if (!"0".equals(GlobalUtility.getStringValue(paymentAmt))) {
					String emp_no 				= ""; // Default
					String custCard_No 		= ""; // Default
					String ss_crStNum 			= ""; // Default
					String ss_carNum 			= ""; // Default
					String lastPayment_yn 	= "";
					String term_id 				= ""; // Default
					String led_code 				= IUPOSConstant.RESPOND_LEDCODE_1; // Default 1 : ����

					String salesBasePrice = PumpMUtil.convertBasePriceFromPumpToPOS(trInfo.getBasePrice());// �Ǹ� �ܰ�
					String paymentLiter 	= PumpMUtil.convertTotalLiterFromPumpTOPOS(trInfo.getLiter()); 		// ���� ����

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
					LogUtility.getPumpMLogger().info("[Pump M] ���ݰ��� �� 0�� ������ ��� ������ ������ �ʴ´�.");
				}
			} else if ("5".equals(trType) && "6".equals(mode)) {
				// ���� ���� ��, ������ ���� ��/�ٿ� �߻����� ���� ��Ȳ���� ��� ��ư�� ������ ���
				// DG�� ������ POS �� ����
				LogUtility.getPumpMLogger().info("[Pump M] Send DG Content to Sale M : SOMO SELF CASH 0Liter 0Won - ��ҹ�ư Ŭ��");
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
				// ���� ������ ����� ���� ������ ���
				// ���ݿ����� HF �� �̹� POS �� ���� �Ǿ������� ���� HF�� ������ �ʴ´�
			}
		}

		// 2012.06.07 ksm
		// �ǸſϷ� �޾��� ��� ������ ������� Ȯ�� �� POS�� EN���� ����
		// ��, 0��0���� ������ ��� EN���� ��������.
		// 1. ��������� ���� Ȯ�� - (����) ���� ���ڵ� ��� ����
		String printBarCode = "0";

		try {
			printBarCode = T_KH_CODEMASTERHandler.getHandler().getT_KH_CODEMASTERDataValueByCode(IConstant.POSPORTOCOL_CODEMASTER_0440);
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error("[CarWash BarCode]���� ���ڵ� ��� ���� ��ȸ�� ���� �߻�");
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		}
		// LogUtility.getPumpMLogger().info("[CarWash BarCode] (����) ���� ���ڵ� ��� ���� : "
		// + printBarCode);

		if ("1".equals(printBarCode)) {
			// 2. ���� ���� �������� Ȯ��
			// ���ο� ���� ��� Ȥ�� ����� ���� ������ �ִ� ��� ���ڵ� ���� ����.
			// BLüũ ���� ����
			// ���ν������/������ҽ���
			if (!ODTUtility_SoMo.containFailContent(stWorkMsg)) {

				Integer sumAmt = 0;
				// String mode = "";

				String messageType = "";
				String ledCode = "";
				String pymtAmt = "0";

				ArrayList<UPOSMessage> uposArray = PumpMODTSaleManager.getUPOSMessageArray(nozzleNo, khproc_no);

				UPOSMessage uposM = null;

				if (uposArray == null) {
					// POS���������� ���� Ȯ��. �������̸� EN���� ������ ���� �ݾ� ������.
					if (PumpMTransactionManager.getInstance().isPresetFromPOS(nozzleNo)) {
						sumAmt = 1;
					}
				} else {
					for (int i = 0; i < uposArray.size(); i++) {
						uposM = uposArray.get(i);
						messageType = uposM.getMessageType();
						ledCode = uposM.getLed_code();
						pymtAmt = uposM.getPayment_amt();

						LogUtility.getPumpMLogger().info("[CarWash BarCode] MessageType : ("	+ messageType + ") �ݾ� : (" + pymtAmt + ") LedCode : (" + ledCode + ")");

						// ���ʽ������� ��� �ݾ� ��� ���� �� �ʿ�� ���׿�. �������� ��츸 ���ʽ� ���� ��û�ϹǷ�.
						// ���ʽ� ������ ��� 2�� �ݾ� ��.
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
					LogUtility.getPumpMLogger().error(	"[CarWash BarCode] �����ڷ� ��ȸ �� Exception �߻� : " + e.toString());
				}

				String pymtSum = sumAmt.toString();

				// �ǸſϷ��� �����ݾ� ���� 0 �ʰ��� ��쿡�� ���ڵ� ����Ͽ� ó����.
				if (sumAmt > 0) {
					// ���� ���ڵ� ����
					// twsongkis 2015-01-28 ���ο� Barcode Ŭ������ barcode�������� ����
					String barCode = Barcode.getBarcodeNumber("3", pymtSum, nozzleNo, khproc_no, messageType, ledCode, null);			// �������ڵ�
					LogUtility.getPumpMLogger().info("[CarWash BarCode] ���ڵ� ��   :  " + barCode);
					
					// ���� ���ڵ� POS ����
					processOPTBarcode(barCode, nozzleNo, khproc_no);
				} else {
					LogUtility.getPumpMLogger().info("[CarWash BarCode] �����ݾ��� 0���̰ų� �ǸſϷ��� �ݾ� ���� 0 ���� �̹Ƿ� ������ ������ �ƴ�. "+ pymtAmt);
				}
			} else {
				LogUtility.getPumpMLogger().info("[CarWash BarCode] ���ν��� ���� �����Ƿ� ������ ��¾���.");
			}
		}
		// ///////////////////////////////////////////////////////////////////////////////////////////////////

		if (PumpMODTSaleManager.shouldSend0001UPOSMessageToSaleM(stWorkMsg.getConnectNozzleNo(), khproc_no, stWorkMsg)) {
			UPOSMessage uPosMsg = CreateUPOSMessage.createUPOSMessage_0001(IUPOSConstant.DEVICE_TYPE_3S, khproc_no, "1", "", "");
			Preamble posPreamble = PumpMUtil.createUPOSMessagePreamble(SyncManager.getUniqueKey(), 
					SyncManager.DISE_PUMP_MODULE,
					SyncManager.DISE_SALE_MODULE, 
					uPosMsg, "");
			LogUtility.getPumpMLogger().info("[Pump M] Send Self 0001 UPOSMessage to SaleM. 0001 ������ �ٸ� ���� �������� ���� ���� �� ��" 
					+ " �ֱ� ������, 0001 ������ 1�� ��� ���� ����");
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
	 * �پ��� ������ ���� �� �����Ϸ� ���� �����Ѵ�.
	 * 
	 * 1) UPOSMessage ���� ���� (1) �������� ���� ��� (POS �� ���� Preset ���� ���� ������ ����.) a)
	 * Preset �����ݾװ� ���� �����ݾ� ��ġ ���� (a) ��ġ�� ��� (b) ��ġ���� ���� ��� (2) �������� ���� ��� (ODT
	 * �� ���� ���� ��û�� ���� ������ ����.) a) Preset �����ݾװ� ���� �����ݾ� ��ġ ���� (a) ��ġ�� ��� (b) ��ġ����
	 * ���� ���
	 * 
	 * @param uniqueKey :
	 *            Preamble Object �� Unique Key
	 * @param messageID :
	 *            ������� ���� �� ���� Message ID
	 * @param commandID :
	 *            ������� ���� �� ���� CommandID
	 * @param trWorkMsg :
	 *            ������� ���� �� �ǸſϷ� ����
	 * @param khproc_no :
	 *            KH ó����ȣ
	 */
	private void processTR(String uniqueKey, String messageID, String commandID, TR_WorkingMessage trWorkMsg, String khproc_no) {
		if (logSSDC == true)
			LogUtility.getCATLogger().info("SSDC/" + "PUMP_M/" + "PupmAController/" + "processTR()");

		handleWorkingMessageForAdditionalWork(uniqueKey, trWorkMsg
				.getConnectNozzleNo(), commandID, trWorkMsg, khproc_no);

		try {
			// 2009�� 5�� 8�� �߿��� �߰�.
			GlobalUtility.printAnalysisLog("pumpM", khproc_no, trWorkMsg.getNozzleNo(), "", "�ǸſϷ�", "", 
					new String("LITER : "	+ trWorkMsg.getLiter() + ", PRICE : " + trWorkMsg.getPrice()).getBytes(), "", "", "");

		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(	"[Pump M] !!!!!!!!!!AnalysisLOG ��� ����!!!!!!!!!!!", e);
		} // end try

		/**
		 * �پ��� ������ ��� ���� ������ ������ 0001 ������ POS �� �����ϵ��� �Ѵ�. ���� ���ٸ� ������ �ʴ´�. (�̴�
		 * POS �� ���� Preset �� ���� �������θ� �����Ѵ�. (lastpayment_yn=1 �� ����)
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
	 * �پ��� ���� ������ ���
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

		// ������ ���� ������ Pos A�� �����Ѵ�.
		processOPTBarcode(qlMsg, khproc_no);
	}

	/**
	 * ��Ȳ�� ���� ������ �� ���� �ϰ� ű����� ó�� ��ȣ�� ������ �����Ѵ�.
	 * 
	 * @param commandID
	 * @param workMsg
	 */
	private void resetCustDataNewKHProcNo(WorkingMessage workMsg) {

		PumpMTransactionManager.getInstance().setNozzleState(	workMsg.getNozzleNo(), IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED);
		PumpMODTSaleManager.initSaleContent(workMsg.getNozzleNo());
	}

	/**
	 * �����⿡ �ܰ��� �����մϴ�.
	 * 
	 * @param nozzle_no :
	 *            ���� ��ȣ
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

			// ������ Type �� ���� ����
			T_NZ_NOZZLEData tNozData = T_NZ_NOZZLEHandler.getHandler()
					.getT_NZ_NOZZLEDataByNozzleNo(nozzle_no);
			String self_ind_exist = tNozData.getSelf_ind_exist();

			if (self_ind_exist.equals(ICode.SELF_IND_EXIST_01_PUMP)
					|| self_ind_exist
							.equals(ICode.SELF_IND_EXIST_03_SEMI_SELF)) {
				// ������ / Semi ������ �� ��� P3
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
					// LogUtility.getPumpMLogger().info("[Pump M] P3 ������ ������ �ʽ��ϴ�.")
					// ;
				}
			} else if (self_ind_exist.equals(ICode.SELF_IND_EXIST_02_SELF_PUMP)) {
				// ���� ������ �� ��� P5 (ODT ID) �� ��û
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
					// LogUtility.getPumpMLogger().info("[Pump M] ���� ODT�� ���, P5 ������
					// �����⿡ ������ �ʽ��ϴ�.") ;
				}

			} else if (self_ind_exist
					.equals(ICode.SELF_IND_EXIST_04_ODT_RECHARGE)) {
				// �������� ��� D1
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
					// LogUtility.getPumpMLogger().info("[Pump M] D1 ������ �����⿡ ������
					// �ʽ��ϴ�.") ;
				}
			}
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(), e);
		}
	}

	/**
	 * POS �� ���� �ʱ�ȭ ������ �� ���� ��� ���������� POS A ���� �����Ѵ�.
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
	 * �������� �Ұ��� �ð� ������ odt�� ����.
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

					// P5 ������ ���� ODT �� ����
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
	 * Locking , unLocking ������ POS �� �����Ѵ�.
	 * 
	 * @param nozID :
	 *            ������ �����ϴ� ���� ��ȣ
	 * @param khproc_no :
	 *            KH ó�� ��ȣ
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
	 * Pump M ���� �� Controller �� Pump A ���� ������ �۽��Ѵ�. ���� ������ Class Type �� ����
	 * Command ID �� ������ ����.
	 * 
	 * Output POS Adapter : Preamble byte[] Pump Adapter : PumpPreamble
	 * WorkingMessage CAT Module : Preamble byte[] State Module : Preamble
	 * WorkingMessage Sale Module : Preamble byte[]
	 * 
	 * Send Where - PosAController Command ID List from where - POS Protocol DE :
	 * ���� �� ���� ���� : PumpAController DM : ������ �߱� ��ȸ : PumpAController DQ : Preset
	 * �ڷ� ���� : PumpAController
	 *  - Pump Adapter Command ID List - PumpA Protocol PA : ���� ����/���� ��� :
	 * PosAController QF : Preset �ڷ� ��û : PosAController PB : ���� / ���� ���� ��û :
	 * PosAController PQ : ������ ��û ���� : PosAController
	 * 
	 * P2 : �Ϲ� ȯ�� ���� ���� : PumpAController P3 : ������ ȯ������ ���� : PumpAController P5 :
	 * ODT ȯ������ ���� (����,������): PumpAController P6 : ������ �� �ð� ���� : PumpAController
	 * PT : ������ ODT POS ȯ���������� : PumpAController
	 * 
	 * HD : �Ҹ��� - �ܻ�� ���� ���� : CATMController HC : �Ҹ��� - ī����� ���� ���� :
	 * CATMController QM : �پ��뼿�� - ī������������� : CATMController
	 * 
	 * PG : ������ - ��ī�� ���� ��û ���� - �ߺ������� �ƴ� ��� : PumpAController PF : ������ - ��ī��
	 * ���� ��û ���� - �ߺ����� �����ϴ� ��� : PumpAController NAK : ������ - ��ī�� ���� ��û ���� -
	 * �������� �ʴ� ��� : PumpAController PI : ������ - �ſ�ī�� ���� ��û ���� : CATMController PK :
	 * ������ - ���ʽ� ī�� ó�� ��û ���� : CATMController PL : ������ - ���ڻ�ǰ�� ó�� ���� :
	 * CATMController BB : ������ - ���ʽ� ���� ���� ���� : CATMController XA : ������ - ���� ������
	 * ��û ���� : CATMController PP : ������ - ���� ������ ��ȣ ���� : PumpAController PQ : ������ -
	 * ������ ��ȸ ��û ���� : PumpAController
	 *  - CatMController Command ID List from where - POS Protocol including SMT
	 * ���� HB : �Ҹ��� - ī����� ���� ��û : PumpAController HE : �پ��뼿�� - ī����� ���� ��û :
	 * PumpAController SB : ������ - ī����� ���� ��û : PumpAController BA : ������ - ���ʽ� ����
	 * ���� ��û : PumpAController TJ : ������ - ���� ������ ��û : PumpAController
	 *  - StateMController Command ID List from where - PumpA Protocol SE : ����
	 * ����̽� �̻� ���� ���� : PumpAController S8 : ������/������ ���� ���� : PumpAController
	 *  - SaleMController Command ID List from where - POS Protocol DG : ���� �Ϸ�
	 * ���� ���� : PumpAController
	 * 
	 * @param preambleData :
	 *            ������ ����
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
		// PI2, CWI, 2016-03-25, ķ���� ���������� �������� ����  ���ο� ������� ����(PumpAController -> CatMController)
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
	 * Pump A �� ���ؼ� ������ �ʱ�ȭ ������ �����Ͽ� �����Ѵ�.
	 * 
	 */
	private void startPumpAdapter() {
		if (logSSDC == true)
			LogUtility.getCATLogger().info(
					"SSDC/" + "PUMP_M/" + "PupmAController/"
							+ "startPumpAdapter()");

		if (isAlreadyInitialize() == true) {
			LogUtility.getPumpMLogger().info("[Pump M] �̹� Pump A �� �ʱ�ȭ ��û ������ ���۵Ǿ����ϴ�. ���� �� �̻� �ʱ�ȭ ��û�� �������� �ʽ��ϴ�.");
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

					// ������ �ʱ�ȭ �Ϸ�Ǿ����� POS A �� ����
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
				//�ֹ����� ��û
				process0221(messageID, upos);
			} else if(IUPOSConstant.MESSAGETYPE_0223.equals(upos.getMessageType())){
				//preset ��û 
				process0223(messageID, upos);
			} else if(IUPOSConstant.MESSAGETYPE_0228.equals(upos.getMessageType())){
				//���� ��Ż
				process0228(messageID, upos);
			} else if (IUPOSConstant.MESSAGETYPE_0229.equals(upos.getMessageType())){
				//preset ��� ��û
				process0229(messageID, upos);
			} else {
				//���� ��û ������ ó���Ѵ�.
				
				/*
	        	 * BEACON ���� :
	        	 * 	1. PumpA -> PumpA : ���ݿ�û(0011),���ݺ��ʽ���û(0013), �ſ��������(0031), �ſ�+���ʽ���������(0033),
	        	 *                      GS���ʽ� �������(0061), ���� �������(0063),
	        	 * 		- DeviceType   : 3M
	        	 * 2017.06.30
	        	 */
				
				//pos�� ������ �� ��û
				sendLockingInfoToPOS(upos.getNozzle_no(), upos.getPosReceipt_no(), IConstant.PUMP_SALE_LOCKING) ;
				//���� ��û ������ CATM�� �����Ѵ�.
				Preamble preamble = PumpMUtil.createUPOSMessagePreamble( messageID,
						SyncManager.DISE_BEACON_MODULE,
						SyncManager.DISE_CAT_MODULE, 
						upos, "");

				LogUtility.getLogger().info("[Pump M] <Beacon Process> ������û ���� CAT M ����");
				sendMessage(preamble);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ARK ���� ���� �ֹ�������û�� ó���Ѵ�.
	 * 1. display ARK�� ����Ǿ��ִ� ������ ���� üũ
	 * 
	 * 2. ������ ��� ���ɽ� �ֹ����� ��ִ����� ����
	 * 2.1 ������ ���� ���� �߰�
	 * 
	 * 3. ������ ��� �Ұ��� ������ ��� �Ұ� ������ ARK�� ����
	 * 
	 * @param messageID :
	 *            �޼��� ID
	 * @param upos :
	 *            �ֹ����� UPOSMessage
	 */
	private void process0221(String messageID, UPOSMessage upos) throws CloneNotSupportedException {
		BeaconMessage bMsg = ByteArrayToBeaconMessage.createBeaconMessage(upos.getFiller2().getBytes());
		String dArkNo = bMsg.getDisplayArkId() ;
		
		LogUtility.getLogger().info("[Pump M] <Beacon Process> 0221(�ֹ����� ��û) ó�� - Display Ark ���� Hashmap ����  dArk No : " + dArkNo) ;
		
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
		
		//1. display ark�� ���� �Ǿ� �ִ� ������ ���� üũ
		if(state){
			/* 2. ������ ��� ���ɽ�
			 * ���� ���� ���� �� 
			 * �ֹ� ���� ��ִ��� ����
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
			
			//�ֹ����� ��û ��ִ����� ����
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
			/* 2. ������ ��� �Ұ��ɽ�
			 �Ұ��� ���� ARK�� ����
			*/
			
			UPOSMessage upos0222 = upos.clone();
			
			BeaconMessage bMsg0222 = ByteArrayToBeaconMessage.createBeaconMessage(upos.getFiller2().getBytes());
			bMsg0222.setCommand(IPumpConstant.COMMANDID_JB);
			bMsg0222.setDirection("K");
			//���� ����
			bMsg0222.setNozStats("1");
			
			byte[] bm_jbFiller2 = BeaconMessageToByteArray.createBeaconMessageByteArray(bMsg0222);
			
			upos0222.setMessageType(IUPOSConstant.MESSAGETYPE_0222);
			upos0222.setFiller2(new String(bm_jbFiller2));
			
			LogUtility.getLogger().info("[Pump M] <Beacon Process> Send UPOSMessage 0222 to pumpA");
			
			sendBeaconMessageToBeaconM(messageID, upos0222);
		}
	}
	
	/**
	 * ARK ���� ���� ������Ż�� ó���Ѵ�.
	 * 
	 * 1. AE ������ POS�� �����Ѵ�.
	 * 2. ACK ������ ARK�� �����Ѵ�. 
	 * 
	 * @param messageID :
	 *            �޼��� ID
	 * @param upos :
	 *            �ֹ����� UPOSMessage
	 */
	private void process0228(String messageID, UPOSMessage upos) throws CloneNotSupportedException {
		LogUtility.getLogger().info("[Pump M] <Beacon Process>  0228(������Ż) ó��");
		UPOSMessage upos0220 = upos.clone();
		
		String filler2 = upos.getFiller2();
		BeaconMessage bMsg = ByteArrayToBeaconMessage.createBeaconMessage(filler2.getBytes());
		String msgId = bMsg.getMessageId();
		
		//	1. AE ���� POS ���� (���°� : 292)
		LogUtility.getLogger().info("[Pump M] <Beacon Process> 0228(������Ż)  - AE POS ����");
		SE_WorkingMessage seWokringMessage = new SE_WorkingMessage() ;
		seWokringMessage.setNozzleNo(bMsg.getNozNo()); // �����ȣ 
		seWokringMessage.setDeviceType(ICode.EXIST_06_BEACON);
		seWokringMessage.setStatus(IPumpConstant.PUMP_STATECODE_ERROR);
		seWokringMessage.setStatusCode(String.valueOf(IConstant.STATE_ARK_STATECODE_292));
		POS_AE posMessage = new POS_AE(messageID,IConstant.POSPROTOCOL_TYPE_POS,seWokringMessage) ;
		LogUtility.getLogger().info("POS AE �Ϸ�");
		Preamble preambleData = PumpMUtil.createPreamble(messageID
				, SyncManager.DISE_POS_ADAPTER, posMessage, "") ;
		LogUtility.getLogger().info("POS AE ������ �Ϸ�");
		/*Preamble preambleData = new Preamble() ;
		preambleData.setKey(messageID) ;
		preambleData.setFrom(SyncManager.DISE_PUMP_MODULE) ;
		preambleData.setDest(SyncManager.DISE_POS_ADAPTER) ;
		preambleData.setPreamble(posMessage) ;
		preambleData.setLog("") */;
		
		sendMessage(preambleData) ;
		
		//  2. ARK(0220 uPosMessage) ����
		BeaconMessage ackBMsg = new BeaconMessage() ;
		ackBMsg.setCommand(IPumpConstant.COMMANDID_JZ);
		ackBMsg.setDirection("K");
		ackBMsg.setValue("0");	//0:���� ACK
		ackBMsg.setMessageId(msgId);
		
		upos0220.setMessageType(IUPOSConstant.MESSAGETYPE_0220);
		upos0220.setFiller2(new String(BeaconMessageToByteArray.createBeaconMessageByteArray(ackBMsg)));
		
		sendBeaconMessageToBeaconM(messageID, upos0220);
	}
	
	/**
	 * ARK ���� ���� preset ��� ��û�� ó���Ѵ�.
	 * 
	 * 1. ������ ���¸� üũ�Ѵ�.
	 * 2. ������ ���°� ����� �̰ų�, 
	 *    ���� ���� ��� ARK�� ACK������ �����Ѵ�.
	 * 3. ������ ���°� ����� �̰ų�, ���� ���� �ƴ� ���   
	 * 3.1. ������ �������
	 * 3.2. ������ ������� ����
	 * 3.3. ������ �ܰ� ������
	 * 3.4. POS�� AE���� ����
	 * 3.5. ARK�� ACK���� ����(JZ)
	 * 3.6. ���� �����ϰ� �ִ� �ֹ�������û ���� ����
	 * 
	 * @param messageID :
	 *            �޼��� ID
	 * @param upos :
	 *            �ֹ����� UPOSMessage
	 */
	private void process0229(String messageID, UPOSMessage upos) throws Exception {
		LogUtility.getLogger().info("[Pump M] <Beacon Process> 0229(��� ��û) ó��") ;
		String filler2 = upos.getFiller2();						
		BeaconMessage bMsg = ByteArrayToBeaconMessage.createBeaconMessage(filler2.getBytes());						
		String nozzleNo = bMsg.getNozNo();
		String khproc_no = bMsg.getKhProcs_no();
		String msgId = bMsg.getMessageId();
		UPOSMessage upos0220 = upos.clone();
		
		/*
		 * �����, ���� ���� ���� preset ��� �Ұ�
		 */
		int pumpstateCode = StateMController.getPumpStateCode(nozzleNo);
		
		if(pumpstateCode == IConstant.STATE_PUMP_STATECODE_652 
				|| pumpstateCode == IConstant.STATE_PUMP_STATECODE_653 ){	
			
			LogUtility.getLogger().info("[Pump M] <Beacon Process> ��ҿ�û - ���������� ��� ó�� �Ұ� ") ;
			// 1. NAK(0220 uPosMessage) ����
			BeaconMessage ackBMsg = ByteArrayToBeaconMessage.createBeaconMessage(filler2.getBytes());
			ackBMsg.setCommand(IPumpConstant.COMMANDID_JZ);
			ackBMsg.setDirection("K");
			ackBMsg.setValue("1");	// 1:NAK
			ackBMsg.setMessageId(msgId);
			
			upos0220.setMessageType(IUPOSConstant.MESSAGETYPE_0220);
			upos0220.setFiller2(new String(BeaconMessageToByteArray.createBeaconMessageByteArray(ackBMsg)));
			
			sendBeaconMessageToBeaconM(messageID, upos0220);
			
		} else {
			
			// 1. ������ Preset ����
			String dIcommand =""; //������ �������/���� ���
			POS_DI diMsg = null;
			
			dIcommand ="0"; //������ ������� ���
			LogUtility.getLogger().info("[Pump M] <Beacon Process> Preset ���� - �������");
			diMsg = new POS_DI(messageID, nozzleNo, dIcommand);
			LogUtility.getLogger().info(diMsg.toString());
			processDI(messageID, diMsg) ;

			Thread.sleep(500);
			
			dIcommand ="1"; //������ ������� ���� ���
			LogUtility.getLogger().info("[Pump M] <Beacon Process> Preset ���� - ������� ����");
			diMsg = new POS_DI(messageID, nozzleNo, dIcommand);
			LogUtility.getLogger().info(diMsg.toString());
			processDI(messageID, diMsg) ;
			
			//���� ���� �ʱ�ȭ
			//������ Type �� ���� ����
			T_NZ_NOZZLEData tNozData = T_NZ_NOZZLEHandler.getHandler().getT_NZ_NOZZLEDataByNozzleNo(nozzleNo);
			String self_ind_exist = tNozData.getSelf_ind_exist();
			
			if(ICode.SELF_IND_EXIST_02_SELF_PUMP.equals(self_ind_exist)){
				PumpMTransactionManager.getInstance().setNozzleState(nozzleNo, IPumpConstant.KH_ODT_TR_SH_ST_COMPLETED);
			}else{
				PumpMTransactionManager.getInstance().setNozzleState(nozzleNo, IPumpConstant.KH_PUMP_COMPLETED);
			}
			
			Thread.sleep(500);
			
			// �����ܰ��� �� ���� (���� : Preset �����̸鼭, Preset ���� �ܰ��� ���ܰ��� �ٸ� ���.)
			//notifyBasePriceToPumpAfterPumpCompletion(khproc_no, nozzleNo);
			sendBasePrice(nozzleNo);
			
			// �Ϲ� ��� : 293 
			// �ڵ������� ��� : 294
			// AE ���� POS ���� (���°� : 293)
			LogUtility.getLogger().info("[Pump M] <Beacon Process> Preset ����  - AE(294) POS ����");
			SE_WorkingMessage seWokringMessage = new SE_WorkingMessage() ;
			seWokringMessage.setNozzleNo(bMsg.getNozNo()); // �����ȣ 
			seWokringMessage.setDeviceType(ICode.EXIST_06_BEACON);
			seWokringMessage.setStatus(IPumpConstant.PUMP_STATECODE_ERROR);
			//������ ��� 
			seWokringMessage.setStatusCode(String.valueOf(IConstant.STATE_ARK_STATECODE_294));
			POS_AE posMessage = new POS_AE(messageID, IConstant.POSPROTOCOL_TYPE_POS, seWokringMessage) ;
			Preamble preambleData = PumpMUtil.createPreamble(messageID
					, SyncManager.DISE_POS_ADAPTER, posMessage, "") ;
			
			sendMessage(preambleData) ;
			
			// ARK(0220 uPosMessage) ����
			BeaconMessage ackBMsg = ByteArrayToBeaconMessage.createBeaconMessage(filler2.getBytes());
			ackBMsg.setCommand(IPumpConstant.COMMANDID_JZ);
			ackBMsg.setDirection("K");
			ackBMsg.setValue("0");	//0:���� ACK
			ackBMsg.setMessageId(msgId);

			upos0220.setMessageType(IUPOSConstant.MESSAGETYPE_0220);
			upos0220.setPosReceipt_no(khproc_no);
			upos0220.setFiller2(new String(BeaconMessageToByteArray.createBeaconMessageByteArray(ackBMsg)));
		
			sendBeaconMessageToBeaconM(messageID, upos0220);
			
			//�ֹ� ���� Hashmap ���� (remove(kh��ȣ), exception ó��)
	    	IBeaconConstant.beaconPumpData.remove(bMsg.getKhProcs_no()) ;
		}
	}
	
	private void process0223(String messageID, UPOSMessage upos) throws Exception {
		LogUtility.getLogger().info("[Pump M] <Beacon Process> 0223(Preset ��û) ó��") ;
		String filler2 = upos.getFiller2();
		
		BeaconMessage bMsg = ByteArrayToBeaconMessage.createBeaconMessage(filler2.getBytes());
		
		//������ üũ�Ѵ�.
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
			
			//���� ���� üũ
			boolean state = checkNozState(bMsg.getDisplayArkId());
			/*
			 * ������ ���°� ��� ������ ���
			 * ���� ������ �� ���� ODT���� �ڵ带 �ѹ� �� Ȯ�� �Ѵ�.
			 * ���� �����⿡�� ���� �Ϸ��� ������ ��� �� preset ������
			 * preset ���� �� �Ǵ� ������ ����ϱ� ���� �߰� 
			 * 699(ȭ���̵�) ���� �ΰ�� preset ���� 2017.06.22
			 */
		
			//���������� ����
			String self_ind_exist = "";
			
			if(state){
				T_NZ_NOZZLEData tNozData = T_NZ_NOZZLEHandler.getHandler().getT_NZ_NOZZLEDataByNozzleNo(bMsg.getNozNo());
				self_ind_exist = tNozData.getSelf_ind_exist();
				
				LogUtility.getLogger().info("[beacon] pumpacontroller ������Ÿ�� : " + self_ind_exist);
				
				if(self_ind_exist.equals(ICode.SELF_IND_EXIST_02_SELF_PUMP)) {
					LogUtility.getLogger().info("1");
					
					//�������
					S8_WorkingMessage wm235 = (S8_WorkingMessage) IBeaconConstant.beaconODTstate.get(bMsg.getNozNo() + "235");
					LogUtility.getLogger().info("2");
					//�����Ϸ�
					S8_WorkingMessage wm654 = (S8_WorkingMessage) IBeaconConstant.beaconODTstate.get(bMsg.getNozNo() + "654");
					LogUtility.getLogger().info("3");
					//����ȭ�� �̵�
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
						LogUtility.getLogger().debug("[Pump M] <Beacon Process> Preset ��� ����") ;
						state = true;
					}else{
						LogUtility.getLogger().info("9");
						LogUtility.getLogger().debug("[Pump M] <Beacon Process> Preset ��� �Ұ�") ;
						state = false;
					}
				}
			}
			
			LogUtility.getLogger().info("10");
			
			//������ ���°� ��� �Ұ��� ���
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
				
				//1. ������� ����
				LogUtility.getLogger().info("[Pump M] <Beacon Process> Preset Process - �������");
				String dIcommand =""; //������ �������/���� ���
				POS_DI diMsg = null;
				
				dIcommand ="0"; //������ ������� ���
				diMsg = new POS_DI(messageID, nozzleNo, dIcommand);
				LogUtility.getLogger().info(diMsg.toString());
				processDI(messageID, diMsg) ;

				Thread.sleep(500);
				
				//2. ������� ����
				LogUtility.getLogger().info("[Pump M] <Beacon Process> Preset Process - ������� ����");
				dIcommand ="1"; //������ ������� ���� ���
				diMsg = new POS_DI(messageID, nozzleNo, dIcommand);
				LogUtility.getLogger().info(diMsg.toString());
				processDI(messageID, diMsg) ;
				
				Thread.sleep(500);
				
				//3. preset ����
				LogUtility.getLogger().info("[Pump M] <Beacon Process> Preset Process - Preset ��û");
				//����/���� ���� ��û	
				String udtYn = PropertyManager.getSingleton().getProperty(PropertyManager.OWIN_PRESET_CODE_UPDATE_YN, PropertyManager.OWIN_PRESET_CODE_UPDATE_YN_DEFALUT);
				
				/*
				 * Preset ��û �⺻ ��(0: ���� / 1: ����)
				 * 
				 * kixxhub.properties ���Ͽ� kh.beacon.update_yn �������� ���� 0�ϰ�� ������ ����, 1�ϰ�� (2: ���� / 3: ����) ���� ����
				 * 2 �Ǵ� 3���� ������� Owin ���������� �ν��Ͽ� �̸����� �߻��� ������ �� �Ž��� �� ǥ�õ��� �ʰ� ó�� ��
				 * 
				 * */
				if(!PropertyManager.OWIN_PRESET_CODE_UPDATE_YN_DEFALUT.equals(udtYn)){
					//==>> soon 20211104 ���� ������ �� ��쿡�� 0->2 �Ǵ� 1->3���� ����
					if(self_ind_exist.equals(ICode.SELF_IND_EXIST_02_SELF_PUMP)){
						
						if(command.equals(ICode.PRESET_QTY_PRC_IND_0_PRICE)) {
							command = ICode.PRESET_QTY_PRC_IND_OWIN_PRICE;	   //OWIN ���� ����
						} else {
							command = ICode.PRESET_QTY_PRC_IND_OWIN_LITER;	;  //OWIN ���� ����						
						}
					}
					
					LogUtility.getLogger().info("[Pump M] <Beacon Process> Preset Process - Preset ����(0,2)/����(1,3) : ["+command+"]");
				} 
				
				POS_DK dkMsg = new POS_DK(source, messageID, deviceType, nozzleNo, command, liter, price, presetBaseprice);
				
				PB_WorkingMessage pbWorkMsg = PumpMUtil.createPB_WorkMsg(dkMsg) ;
				
				Preamble pumpPreamble = null ;
				String nozID = dkMsg.getDeviceID() ;
				
				LogUtility.getLogger().info("[Pump M] <Beacon Process> Receive Fixed Price/Liter Pump from Beacon. NozID="+nozID) ;
				
				String khproc_no = 
					PumpMTransactionManager.getInstance().getKHTransactionID(pbWorkMsg.getNozzleNo(), IPumpConstant.KH_PUMP_PRESET) ;
				PumpMTransactionManager.getInstance().setPresetInfo(pbWorkMsg.getNozzleNo(), IPumpConstant.PRESET_FROM_POS) ;
		
				// ���ڵ� ����
				// ���ο� ���ڵ� �������� barcode ����
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
				
				// 3. �ֹ� ���� Hashmap ����
				// ARK�� �ֹ�����, �ֹ��Ϸ� ������ �����ϱ� ���� preset ��û ������ HashMap�� �����Ѵ�. 
				LogUtility.getLogger().info("[Pump M] <Beacon Process> Preset Process - �ֹ���ȣ ����");
				
				BeaconPumpInfo beaconPumpInfo = new BeaconPumpInfo();
				beaconPumpInfo.setUpos(upos);
				IBeaconConstant.beaconPumpData.put(khproc_no,beaconPumpInfo) ;

				processHF(messageID, pbWorkMsg.getMessageID(), khproc_no, pbWorkMsg);
				LogUtility.getLogger().info("[Pump M] <Beacon Process> Respond Preset to POS");
				
				//	4. 0224 uPosMessage BeaconM ���� 
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
     * POS �� ���� �������� ����� �޾Ƽ� �̸� Pump A ���� �����Ѵ�.
     * �������� ����� ������ ���� ���еȴ�.
     * 	Command
     * 		0 : ����(����) ����
     * 		1 : ����(����) ���� ����
     * 		2 : ��ü ����(����) ����
     * 		3 : ��ü ����(����) ���� ����
     * 
     * @param uniqueKey	: Unique Key
     * @param diMsg		: �������� �� ���� (DI ����)
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
	 * ���� UPOS���� filler2�� ���� ���� ������ �����Ѵ�.
	 * beaconMessage command JF�� �����Ѵ�.
	 * 
	 * ������ �� ��� �����Ⱑ ���� ���� ������ Kixxhub���� ������ ���� 
	 * �ʾƼ� ���� �� ������ �����ϸ� ���� ���� ������ ARK�� ���´��� Ȯ�� ��
	 * ���� ���� ������ �����ؼ� ARK�� �����Ѵ�.
	 * 
	 * @param messageID :
	 *            �޼��� ID
	 * @return khproc_no :
	 *            KH ó����ȣ
	 */
	private void processBeaconS3(String messageID, String khproc_no) throws CloneNotSupportedException {
		//kixxhub ó����ȣ�� �����ϸ� Beacon�� ����
		if(IBeaconConstant.beaconPumpData.containsKey(khproc_no)){
			BeaconPumpInfo beaconPumpInfo = (BeaconPumpInfo) IBeaconConstant.beaconPumpData.get(khproc_no);
			
			//���� ���������� �̹� �����ߴ��� Ȯ���Ѵ�.
			if(!beaconPumpInfo.isPumpingSJalreadySent()){
				LogUtility.getLogger().info("[Pump M] <Beacon Process> preset �ڷ� ���� " + khproc_no) ;
			
				//�̹� ���� �����͸� ���� ��� ������ ����
				UPOSMessage uposTemp = (UPOSMessage)beaconPumpInfo.getUpos();
				
				//���� ���� upos ����
				UPOSMessage upos = makeBeaconPumpingStart(uposTemp);
				
				//���� ���� ���� �������� ����
				beaconPumpInfo.setPumpingSJalreadySent(true);
				
				LogUtility.getLogger().info("[Pump M] <Beacon Process> �������� �ڷ� ProxyArk ���� " + khproc_no) ;

				sendBeaconMessageToBeaconM(messageID, upos);
			}
		}
	}
	
	/**
	 * Beacon ����� �������� ������ ������ �� ARK�� ������ �����Ѵ�.
	 * 
	 * @param messageID :
	 *            �޼��� ID
	 * @param khproc_no :
	 *            KH ó����ȣ
	 */
	private void processBeaconPumpingStart(String messageID, String khproc_no) throws CloneNotSupportedException {
		//kixxhub ó����ȣ�� �����ϸ� Beacon�� ����
		if(IBeaconConstant.beaconPumpData.containsKey(khproc_no)){
			LogUtility.getLogger().info("[Pump M] <Beacon Process> Ȯ�� - ���������ڷ� 0225 ���� : K/Hó����ȣ : " + khproc_no) ;
			
			BeaconPumpInfo beaconPumpInfo = (BeaconPumpInfo) IBeaconConstant.beaconPumpData.get(khproc_no);
			UPOSMessage uposTemp = (UPOSMessage)beaconPumpInfo.getUpos();
			
			uposTemp.setPosReceipt_no(khproc_no);
			//���� ���� upos ����
			UPOSMessage upos = makeBeaconPumpingStart(uposTemp);

			beaconPumpInfo.setPumpingSJalreadySent(true);
			
			sendBeaconMessageToBeaconM(messageID, upos);
			
		}
	}
	
	/**
	 *
	 * ���� �Ϸ� ������ ARK�� �����Ѵ�. 
	 * 
	 * @param messageID :
	 *            �޼��� ID
	 *        khproc_no :
	 *            KH ó����ȣ
	 *        s4WorkMsg :
	 *            �����⿡�� ���� ���� �Ϸ� ���� 
	 */
	private void sendS4BeaconMessage(String messageID, String khproc_no, S4_WorkingMessage s4WorkMsg) throws Exception {
		//kixxhub ó����ȣ�� �����ϸ� Beacon�� ����
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
			
			//���� �Ϸ� �ڷḦ �ҷ��´�.
			SqlSession session = null;
			session = SqlSessionFactoryManager.openSqlSession();
			
			T_KH_PUMP_TRData[] rlt = T_KH_PUMP_TRHandler.getHandler().getT_KH_PUMP_TRData(session, khproc_no) ;
			
			SqlSessionFactoryManager.closeSqlSession(session);
			
			//���� �Ϸ� �ڷḦ Ȯ���Ѵ�.
			if(rlt == null || rlt.length < 1){
				LogUtility.getLogger().info("[Pump M] <Beacon Process> No T_KH_PUMP_TRData was found. khproc_no : " + khproc_no) ;
				return;
			}
			pumpTrData = rlt[0];
			
			//��ǰ ������ �����.
			itemInfoItem = UPOSUtil.getUPOSMessage_ItemInfo_Item(pumpTrData, isCatPreset);

			//�����Ϸ� ������ �����.
			sendUPOSMsg = UPOSUtil.process4201_4291(upos, itemInfoItem, completed);
			
			//beacon �����Ϸ� �������� �����Ѵ�.
			sendUPOSMsg.setMessageType(IUPOSConstant.MESSAGETYPE_0227);
			
			//Beacon�޼����� �����.
			bm_jg = ByteArrayToBeaconMessage.createBeaconMessage(upos.getFiller2().getBytes());

			bm_jg.setCommand("JG");
			bm_jg.setDirection("K");
			
			//�����ȣ -> preset ���� ���
			//OTI�� -> preset ���� ��� 
			//�ֹ���ȣ -> preset ���� ���
			//������ȣ -> preset ���� ���
			
			//�����Ϸ�ð�
			bm_jg.setPmTime(pumpTrData.getOil_datetime_to());
			//����
			bm_jg.setGdsCode(pumpTrData.getGoods_code());
			//�ܰ�
			bm_jg.setGdsPrice(pumpTrData.getBaseprice());
			//���� ����
			bm_jg.setPmLiter(pumpTrData.getEqpm_qty());
			//���� �ݾ�
			bm_jg.setPmPrice(pumpTrData.getEqpm_amt_prc());
				
			byte[] bm_jgFiller2 = BeaconMessageToByteArray.createBeaconMessageByteArray(bm_jg);
		
			sendUPOSMsg.setFiller2(new String(bm_jgFiller2));
		    
			sendBeaconMessageToBeaconM(messageID, sendUPOSMsg);
		    
			IBeaconConstant.beaconPumpData.remove(khproc_no);
		    
		    //1�� ���� �ֹ� ��û ����
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
	 * ���� UPOS���� filler2�� ���� ���� ������ �����Ѵ�.
	 * beaconMessage command JF�� �����Ѵ�.
	 * 
	 * @param uposTemp :
	 *            �ֹ� ���� ��û ����
	 * @return UPOSMessage :
	 *            ���� ���� ����
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
		//���� ��ȣ -> preset �ڷ� ���
		//OTI�� -> preset �ڷ� ���
		//�ֹ���ȣ -> preset �ڷ� ��� 
		//������ȣ -> preset �ڷ� ���
		//�������۽ð� 
		bm_jf.setPmTime(GlobalUtility.getDateYYYYMMDDHHMMSS());					
		
		byte[] bm_jfFiller2 = BeaconMessageToByteArray.createBeaconMessageByteArray(bm_jf);

		upos.setFiller2(new String(bm_jfFiller2));
		
		return upos;
	}
	
	/**
	 * beacon, Nozzle state ������  
	 * 
	 * @param arkNo : display ARK ��ȣ
	 * @return boolean : ������ ��� ���� ����
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
				LogUtility.getLogger().info("[Pump M] <Beacon Process> ���ܰ��� ���� ���� Ȯ�� - �����ȣ : " + nozNo + ", ����Ÿ�� : " + self_ind_exist) ;
				
				if(StateMController.ckPumpState(nozNo) && PumpMTransactionManager.getInstance().isPresetState(nozNo, self_ind_exist)){
					LogUtility.getLogger().info("[Pump M] <Beacon Process> " + nozNo + "�� ���� ��� ����") ;
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
	 * Beacon���� ���� Preset ������ �޾Ƽ� ������ �����Ѵ�. 
	 * 1. T_KH_PUMP_TR Table update 
	 * 2. POS �� Preset ���� ����
	 * 
	 * @param uniqueKey :
	 *            Preamble Object �� Unique Key
	 * @param messageID :
	 *            CAT���� ���� �� ���� Message ID
	 * @param pbWorkMsg :
	 *            CAT���η� ���� �� ����
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
			// 2009�� 4�� 21�� �߿��� �߰�.
			GlobalUtility.printAnalysisLog("pumpM", khproc_no, pbWorkMsg.getNozzleNo(), "", "������", "", dqPumpMMsg.convertPOSContent(), "", "", "");

		} catch (Exception e) {
			LogUtility.getLogger().error(	"[Pump M] !!!!!!!!!!AnalysisLOG ��� ����!!!!!!!!!!!", e);
		} // end try

		sendMessage(dqPreamble);
	}
	
	 /**
     * BEACON �κ����� ���� ��û�� ���� ���� ������ Pump A �� ������.
     * 
     * @param syncUnique	: Unique Key
     * @param uPosMsg		: ���� ����
     */
    private void sendBeaconMessageToBeaconM(String syncUnique, UPOSMessage uPosMsg) {
    	
    	BeaconMessage beaconMsg = BeaconUtility.createBeaconMessageFromUPOSMessage(syncUnique,uPosMsg);
		
    	// Pump A �� WorkingMessage �� �����Ѵ�.
		Preamble pumpPreamble = PumpMUtil.createBeaconMessagePreamble(syncUnique,
				SyncManager.DISE_BEACON_MODULE, beaconMsg , "") ;
		LogUtility.getLogger().info("[Pump M] <Beacon Process> PumpM -> BeaconM ���� : " + uPosMsg.getMessageType()) ;
		sendMessage(pumpPreamble) ;
    }
    
	/**
    * 
    * <pre>
    * 1. �޼ҵ�� : insertTRData
    * 2. �ۼ��� : 2021. 4. 07. ���� 15:48:31, PI2.
    * 3. �ۼ��� : ������
    * 4. ���� :   TR�ڷḦ DB �� �����Ѵ�.
    * 5. �����̷�:
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
    		
    		LogUtility.getSCMLogger().debug("[Pump A Controller] TR �����͸� �����Ѵ�. khManageNo=" + khManageNo + "#messageID=" + messageID + 
    				"#seqNo=" + seqNo  + "#reponseType=" + reponseType );
    		isInsert = T_KH_SALES_INFOHandler.getHandler().insertT_KH_SALES_INFOData(session, data);
		} catch (Exception e) {
			LogUtility.getSCMLogger().error(e.getMessage(),e) ;
		} 
		return isInsert;
    }

}