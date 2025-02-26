package com.gsc.kixxhub.device.pumpa;

import com.gsc.kixxhub.common.data.pump.BC_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.BI_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.CA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.CL_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.GA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.GT_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HE_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HF_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S3_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S4_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S5_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S8_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SE_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SJ_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.ST_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.TR_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.Base64Util;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.Change;
import com.gsc.kixxhub.device.pumpa.common.Time;
import com.gsc.kixxhub.device.pumpa.controller.DeviceSelector;
import com.gsc.kixxhub.device.pumpa.controller.ProcessSelector;
import com.gsc.kixxhub.device.pumpa.service.AdaptorService;
import com.gsc.kixxhub.device.pumpa.service.listener.AdaptorListener;
 
public class AdaptorServiceImp implements AdaptorService {

	public static 	short 	dispMode=0;
	private static 	AdaptorListener listener = null;
	private static 	byte[] S3_cnt = new byte[101];
	private static 	byte[] SE_cnt = new byte[101];
	private static  String version, issueDate;
	/**
	 * @param wm
	 * @return
	 */
	public static boolean sendDeviceMsg (WorkingMessage wm) { // Send data to module
		
		try {
			
			listener.sendDeviceMsg (wm);
			
			if (wm.getCommand().equals("SJ")) {
				SJ_WorkingMessage rcvWm = (SJ_WorkingMessage) wm;

				S3_cnt[Change.toValue(rcvWm.getNozzleNo())]=0;
				
				if (dispMode==0) {
					LogUtility.getPumpALogger().info("[Pump A][주유시작 자료]" + " nozzle=" + rcvWm.getNozzleNo() + 
							"("+rcvWm.getCommand()+")" + " | totGauge=" + rcvWm.getTotalGauge());
				} else if (dispMode==1) {
					System.out.printf ("\n[주유시작 자료]\n");
					System.out.printf (" nozzle   =%s\n", rcvWm.getNozzleNo());
					System.out.printf (" command  =%s\n", rcvWm.getCommand());
					System.out.printf (" totGauge =%s\n", rcvWm.getTotalGauge());
				} else if (dispMode==2) {
					LogUtility.getPumpALogger().debug("\n[주유시작 자료]");
					LogUtility.getPumpALogger().debug(" nozzle   =" + rcvWm.getNozzleNo() + 
							"("+rcvWm.getCommand()+")");
					LogUtility.getPumpALogger().debug(" totGauge =" + rcvWm.getTotalGauge());
				}
				
			}
			else if (wm.getCommand().equals("S3")) {
				S3_WorkingMessage rcvWm = (S3_WorkingMessage) wm;
				
//				if (dispMode==0) {
//					if (rcvWm.isWriteLog()==true)
//						LogUtility.getPumpALogger().info("[Pump A][주유중 자료]" + " nozzle=" + rcvWm.getNozzleNo() + 
//							"("+rcvWm.getCommand()+")" + " | liter=" + rcvWm.getLiter() + 
//							" | basePrice=" + rcvWm.getBasePrice() + " | price=" + rcvWm.getPrice());
//				} 
				if (dispMode==0) {
					if (S3_cnt[Change.toValue(rcvWm.getNozzleNo())] % 20 == 0) {
						LogUtility.getPumpALogger().info("[Pump A][주유중 자료]" + " nozzle=" + rcvWm.getNozzleNo() + 
							"("+rcvWm.getCommand()+")" + " | liter=" + rcvWm.getLiter() + 
							" | basePrice=" + rcvWm.getBasePrice() + " | price=" + rcvWm.getPrice());
						
						S3_cnt[Change.toValue(rcvWm.getNozzleNo())]=0;
					}
					S3_cnt[Change.toValue(rcvWm.getNozzleNo())]++;
				} 
				else if (dispMode==1) {
					System.out.printf ("\n[주유중 자료]\n");
					System.out.printf (" nozzle   =%s\n", rcvWm.getNozzleNo());
					System.out.printf (" command  =%s\n", rcvWm.getCommand());
					System.out.printf (" liter    =%s\n", rcvWm.getLiter());
					System.out.printf (" basePrice=%s\n", rcvWm.getBasePrice());
					System.out.printf (" price    =%s\n", rcvWm.getPrice());
				} 
				else if (dispMode==2) {
					LogUtility.getPumpALogger().debug("\n[주유중 자료]");
					LogUtility.getPumpALogger().debug(" nozzle   =" + rcvWm.getNozzleNo() + 
							"("+rcvWm.getCommand()+")");
					LogUtility.getPumpALogger().debug(" liter    =" + rcvWm.getLiter());
					LogUtility.getPumpALogger().debug(" basePrice=" + rcvWm.getBasePrice());
					LogUtility.getPumpALogger().debug(" price    =" + rcvWm.getPrice());
				}
			}
			else if (wm.getCommand().equals("S4")) {
				S4_WorkingMessage rcvWm = (S4_WorkingMessage) wm;
				
				S3_cnt[Change.toValue(rcvWm.getNozzleNo())]=0;
				
				if (dispMode==0) {
					LogUtility.getPumpALogger().info("[Pump A][주유완료 자료]" + " nozzle=" + rcvWm.getNozzleNo() + 
							"("+rcvWm.getCommand()+")" + " | liter=" + rcvWm.getLiter() + 
							" | basePrice=" + rcvWm.getBasePrice() + " | price=" + 
							rcvWm.getPrice() + " | totGauge=" + rcvWm.getTotalGauge());
				} 
				else if (dispMode==1) {
					System.out.printf ("\n[주유완료 자료]\n");
					System.out.printf (" nozzle   =%s", rcvWm.getNozzleNo() + 
							"("+rcvWm.getCommand()+")\n");
					System.out.printf (" liter    =%s\n", rcvWm.getLiter());
					System.out.printf (" basePrice=%s\n", rcvWm.getBasePrice());
					System.out.printf (" price    =%s\n", rcvWm.getPrice());
					System.out.printf (" totGauge =%s\n", rcvWm.getTotalGauge());
				} 
				else if (dispMode==2) {
					LogUtility.getPumpALogger().debug("\n[주유완료 자료]");
					LogUtility.getPumpALogger().debug(" nozzle   =" + rcvWm.getNozzleNo() + 
							"("+rcvWm.getCommand()+")");
					LogUtility.getPumpALogger().debug(" liter    =" + rcvWm.getLiter());
					LogUtility.getPumpALogger().debug(" basePrice=" + rcvWm.getBasePrice());
					LogUtility.getPumpALogger().debug(" price    =" + rcvWm.getPrice());
					LogUtility.getPumpALogger().debug(" totGauge =" + rcvWm.getTotalGauge());
				}
				LogUtility.getPumpALogger().info("[Version Info] : " + version + "(" + issueDate + ")");
			}
			else if (wm.getCommand().equals("S5")) {
				S5_WorkingMessage rcvWm = (S5_WorkingMessage) wm;
				
				if (dispMode==0) {
					LogUtility.getPumpALogger().info("[Pump A][토털게이지 자료]" + " nozzle=" + rcvWm.getNozzleNo() + 
							"("+rcvWm.getCommand()+")" + " | totGauge=" + rcvWm.getTotalGauge());
				} 
				else if (dispMode==1) {
					System.out.printf ("\n[토털게이지 자료]\n");
					System.out.printf (" nozzle   =%s", rcvWm.getNozzleNo() + 
							"("+rcvWm.getCommand()+")\n");
					System.out.printf (" totGauge =%s\n", rcvWm.getTotalGauge());
				} 
				else if (dispMode==2) {
					LogUtility.getPumpALogger().debug("\n[토털게이지 자료]");
					LogUtility.getPumpALogger().debug(" nozzle   =" + rcvWm.getNozzleNo() + 
							"("+rcvWm.getCommand()+")");
					LogUtility.getPumpALogger().debug(" totGauge =" + rcvWm.getTotalGauge());
				}
			}
			else if (wm.getCommand().equals("SE")) {
				SE_WorkingMessage rcvWm = (SE_WorkingMessage) wm;
				
				if (dispMode==0) {
					String statusCode = rcvWm.getStatusCode();
					if (!statusCode.equals("601") || SE_cnt[Change.toValue(rcvWm.getNozzleNo())] % 50 == 0) {
						LogUtility.getPumpALogger().info("[Pump A][디바이스 에러]" + " nozzle=" + rcvWm.getNozzleNo() + 
							"("+rcvWm.getCommand()+")" + " | devType=" + rcvWm.getDeviceType() + 
							" | status=" + rcvWm.getStatus() + " | stateCode=" + rcvWm.getStatusCode() + 
							" | errMsg=" + rcvWm.getErrMsg() + " | time=" + rcvWm.getDetectTime());
						
						if (statusCode.equals("601"))
							SE_cnt[Change.toValue(rcvWm.getNozzleNo())]=0;
					}
					if (statusCode.equals("601"))
						SE_cnt[Change.toValue(rcvWm.getNozzleNo())]++;
				} 
				else if (dispMode==1) {
					System.out.printf ("\n[디바이스 에러]\n");
					System.out.printf (" nozzle   =%s", rcvWm.getNozzleNo() + 
							"("+rcvWm.getCommand()+")\n");	
					System.out.printf (" devType  =%s\n", rcvWm.getDeviceType());
					System.out.printf (" status   =%s\n", rcvWm.getStatus());
					System.out.printf (" stateCode=%s\n", rcvWm.getStatusCode()); 
					System.out.printf (" errMsg   =%s\n", rcvWm.getErrMsg());
					System.out.printf (" time     =%s\n", rcvWm.getDetectTime());
				} 
				else if (dispMode==2) {
					LogUtility.getPumpALogger().debug("\n[디바이스 에러]");
					LogUtility.getPumpALogger().debug(" nozzle   =" + rcvWm.getNozzleNo() + 
							"("+rcvWm.getCommand()+")");
					LogUtility.getPumpALogger().debug(" devType  =" + rcvWm.getDeviceType());
					LogUtility.getPumpALogger().debug(" status   =" + rcvWm.getStatus());
					LogUtility.getPumpALogger().debug(" stateCode=" + rcvWm.getStatusCode()); 
					LogUtility.getPumpALogger().debug(" errMsg   =" + rcvWm.getErrMsg());
					LogUtility.getPumpALogger().debug(" time     =" + rcvWm.getDetectTime());
				}
			}
			else if (wm.getCommand().equals("HF")) {
				HF_WorkingMessage rcvWm = (HF_WorkingMessage) wm;
				
				if (dispMode==0) {
					LogUtility.getPumpALogger().info("[Pump A][프리셋 자료]" + " nozzle=" + rcvWm.getNozzleNo() + 
							"("+rcvWm.getCommand()+")" + " | type=" + rcvWm.getType() + 
							" | liter=" + rcvWm.getLiter() + " | basePrice=" + rcvWm.getBasePrice() +
							" | price=" + rcvWm.getPrice());
				} 
				else if (dispMode==1) {
					System.out.printf ("\n[프리셋 자료]\n");
					System.out.printf (" nozzle   =%s", rcvWm.getNozzleNo() + 
							"("+rcvWm.getCommand()+")\n");
					System.out.printf (" type     =%s\n", rcvWm.getType());
					System.out.printf (" liter    =%s\n", rcvWm.getLiter());
					System.out.printf (" basePrice=%s\n", rcvWm.getBasePrice());
					System.out.printf (" price    =%s\n", rcvWm.getPrice());
				} 
				else if (dispMode==2) {
					LogUtility.getPumpALogger().debug("\n[프리셋 자료]");
					LogUtility.getPumpALogger().debug(" nozzle   =" + rcvWm.getNozzleNo() + 
							"("+rcvWm.getCommand()+")");	
					LogUtility.getPumpALogger().debug(" type     =" + rcvWm.getType());
					LogUtility.getPumpALogger().debug(" liter    =" + rcvWm.getLiter());
					LogUtility.getPumpALogger().debug(" basePrice=" + rcvWm.getBasePrice());
					LogUtility.getPumpALogger().debug(" price    =" + rcvWm.getPrice());
				}
			}
			else if (wm.getCommand().equals("S8")) {
				S8_WorkingMessage rcvWm = (S8_WorkingMessage) wm;
				
				SE_cnt[Change.toValue(rcvWm.getNozzleNo())]=0;
				
				if (dispMode==0) {
					LogUtility.getPumpALogger().info("[Pump A][디바이스 상태정보]" + " nozzle=" + rcvWm.getNozzleNo() + 
							"("+rcvWm.getCommand()+")" + " | type=" + rcvWm.getDeviceType() +
							" | status=" + rcvWm.getStatus() + " | statusCod=" + rcvWm.getStatusCode() +
							" | statusMsg=" + rcvWm.getErrMsg() + " | nozState=" + rcvWm.getNozzleState() + 
							" | time=" + rcvWm.getDetectTime());
				}
				else if (dispMode==1) {
					System.out.printf ("\n[디바이스 상태정보]\n");
					System.out.printf (" nozzle   =%s", rcvWm.getNozzleNo() + 
							"("+rcvWm.getCommand()+")\n");
					System.out.printf (" type     =%s\n", rcvWm.getDeviceType());
					System.out.printf (" status   =%s\n", rcvWm.getStatus());
					System.out.printf (" statusCod=%s\n", rcvWm.getStatusCode());
					System.out.printf (" statusMsg=%s\n", rcvWm.getErrMsg());
					System.out.printf (" nozState =%s\n", rcvWm.getNozzleState());
					System.out.printf (" time     =%s\n", rcvWm.getDetectTime());
				} 
				else if (dispMode==2) {
					LogUtility.getPumpALogger().debug("\n[디바이스 상태정보]");
					LogUtility.getPumpALogger().debug(" nozzle   =" + rcvWm.getNozzleNo() + 
							"("+rcvWm.getCommand()+")");
					LogUtility.getPumpALogger().debug(" type     =" + rcvWm.getDeviceType());
					LogUtility.getPumpALogger().debug(" status   =" + rcvWm.getStatus());
					LogUtility.getPumpALogger().debug(" statusCod=" + rcvWm.getStatusCode());
					LogUtility.getPumpALogger().debug(" statusMsg=" + rcvWm.getErrMsg());
					LogUtility.getPumpALogger().debug(" nozState =" + rcvWm.getNozzleState());
					LogUtility.getPumpALogger().debug(" time     =" + rcvWm.getDetectTime());
				}
			}
			else if (wm.getCommand().equals("HE")) {
				HE_WorkingMessage rcvWm = (HE_WorkingMessage) wm;
				
				if (dispMode==0) {
					LogUtility.getPumpALogger().info("[Pump A][다쓰노셀프-승인요청]" + " nozzle=" + rcvWm.getConnectNozzleNo() + 
							"("+rcvWm.getCommand()+")" + " | ODT_No=" + rcvWm.getNozzleNo() +
							" | type=" + rcvWm.getCardType() + " | liter=" + rcvWm.getLiter() + 
							" | basePrice=" + rcvWm.getBasePrice() + " | price=" + rcvWm.getPrice() +
							//" | creditNo=" + Base64Util.encode(rcvWm.getCardNumber()) + 
							" | creditNo=" + Base64Util.encode(rcvWm.getCardNumber().split("=")[0]) +
							" | bonusNo=" + Base64Util.encode(rcvWm.getBonusCard().split("=")[0]) + 
							" | custNo=" + Base64Util.encode(rcvWm.getCustCardNo()) +
							" | cashCount=" + rcvWm.getCashCount() + 
							" | cashRctNo=" + Base64Util.encode(rcvWm.getCashReceiptNo()) +
							" | isFullPmp=" + rcvWm.getIsFullPumping() + 
							" | custType=" + rcvWm.getCustomerType());
				} 
				else if (dispMode==1) {
					System.out.printf ("\n[다쓰노셀프-승인요청]\n");
					System.out.printf (" nozzle   =%s", rcvWm.getConnectNozzleNo() + 
							"("+rcvWm.getCommand()+")\n");
					System.out.printf (" ODT_No   =%s\n", rcvWm.getNozzleNo());
					System.out.printf (" type     =%s\n", rcvWm.getCardType());	
					System.out.printf (" liter    =%s\n", rcvWm.getLiter());	
					System.out.printf (" basePrice=%s\n", rcvWm.getBasePrice());
					System.out.printf (" price    =%s\n", rcvWm.getPrice());	
					System.out.printf (" creditNo =%s\n", Base64Util.encode(rcvWm.getCardNumber().split("=")[0]));
					System.out.printf (" bonusNo  =%s\n", Base64Util.encode(rcvWm.getBonusCard().split("=")[0]));
					System.out.printf (" custNo   =%s\n", Base64Util.encode(rcvWm.getCustCardNo()));
					System.out.printf (" cashCount=%s\n", rcvWm.getCashCount());
					System.out.printf (" cashRctNo=%s\n", Base64Util.encode(rcvWm.getCashReceiptNo()));
					System.out.printf (" isFullPmp=%s\n", rcvWm.getIsFullPumping());
					System.out.printf (" custType =%s\n", rcvWm.getCustomerType());
				} 
				else if (dispMode==2) {
					LogUtility.getPumpALogger().debug("\n[다쓰노셀프-승인요청]");
					LogUtility.getPumpALogger().debug(" nozzle   =" + rcvWm.getConnectNozzleNo() + 
							"("+rcvWm.getCommand()+")");
					LogUtility.getPumpALogger().debug(" ODT_No   =" + rcvWm.getNozzleNo());
					LogUtility.getPumpALogger().debug(" type     =" + rcvWm.getCardType());	
					LogUtility.getPumpALogger().debug(" liter    =" + rcvWm.getLiter());	
					LogUtility.getPumpALogger().debug(" basePrice=" + rcvWm.getBasePrice());
					LogUtility.getPumpALogger().debug(" price    =" + rcvWm.getPrice());	
					LogUtility.getPumpALogger().debug(" creditNo =" + Base64Util.encode(rcvWm.getCardNumber().split("=")[0]));
					LogUtility.getPumpALogger().debug(" bonusNo  =" + Base64Util.encode(rcvWm.getBonusCard().split("=")[0]));	
					LogUtility.getPumpALogger().debug(" custNo   =" + Base64Util.encode(rcvWm.getCustCardNo()));	
					LogUtility.getPumpALogger().debug(" cashCount=" + rcvWm.getCashCount());	
					LogUtility.getPumpALogger().debug(" cashRctNo=" + Base64Util.encode(rcvWm.getCashReceiptNo()));	
					LogUtility.getPumpALogger().debug(" isFullPmp=" + rcvWm.getIsFullPumping());	
					LogUtility.getPumpALogger().debug(" custType =" + rcvWm.getCustomerType());	
				}
			}
			else if (wm.getCommand().equals("TR")) {
				TR_WorkingMessage rcvWm = (TR_WorkingMessage) wm;
				
				if (dispMode==0) {
					LogUtility.getPumpALogger().info("[Pump A][다쓰노셀프-판매자료]" + 
							" nozzle=" + rcvWm.getConnectNozzleNo() + "("+rcvWm.getCommand()+")" + 
							" ODT_No=" + rcvWm.getNozzleNo() + " | liter=" + rcvWm.getLiter() + 
							" | basePrice=" + rcvWm.getBasePrice() + " | price=" + rcvWm.getPrice());
				}
				else if (dispMode==1) {
					System.out.printf ("\n[다쓰노셀프-판매자료]\n");
					System.out.printf (" nozzle   =%s", rcvWm.getConnectNozzleNo() + 
							"("+rcvWm.getCommand()+")\n");
					System.out.printf (" ODT_No   =%s\n", rcvWm.getNozzleNo());
					System.out.printf (" liter    =%s\n", rcvWm.getLiter());
					System.out.printf (" basePrice=%s\n", rcvWm.getBasePrice());
					System.out.printf (" price    =%s\n", rcvWm.getPrice());
				} 
				else if (dispMode==2) {
					LogUtility.getPumpALogger().debug("\n[다쓰노셀프-판매자료]");
					LogUtility.getPumpALogger().debug(" nozzle   =" + rcvWm.getConnectNozzleNo() + 
							"("+rcvWm.getCommand()+")");	
					LogUtility.getPumpALogger().debug(" ODT_No   =" + rcvWm.getNozzleNo());
					LogUtility.getPumpALogger().debug(" liter    =" + rcvWm.getLiter());
					LogUtility.getPumpALogger().debug(" basePrice=" + rcvWm.getBasePrice());
					LogUtility.getPumpALogger().debug(" price    =" + rcvWm.getPrice());
				}
			}
			else if (wm.getCommand().equals("HA")) {
				HA_WorkingMessage rcvWm = (HA_WorkingMessage) wm;
				
				if (dispMode==0) {
					LogUtility.getPumpALogger().info("[Pump A][소모/동화셀프-현금/신용 승인요청]" + 
							" | nozzle=" + rcvWm.getConnectNozzleNo() + "("+rcvWm.getCommand()+")" + " ODT_No=" + rcvWm.getNozzleNo() +  
							" | cmdIdx=" + rcvWm.getCommandIndex() + 
							" | trType=" + rcvWm.getTrType() + 
							" | authType=" + rcvWm.getAuthType() + 
							//" | creditNo=" + Base64Util.encode(rcvWm.getCardNumber()) +
							" | creditNo=" + Base64Util.encode(rcvWm.getCardNumber().split("=")[0]) +
							" | bonusNo=" + Base64Util.encode(rcvWm.getBonusCard().split("=")[0]) +
							" | liter=" + rcvWm.getLiter() + " | basePrice=" + rcvWm.getBasePrice() + 
							" | price=" + rcvWm.getPrice());	
				} 
				else if (dispMode==1) {
					System.out.printf ("\n[소모/동화셀프-현금/신용 승인요청]\n");
					System.out.printf (" nozzle   =%s", rcvWm.getConnectNozzleNo() + 
							"("+rcvWm.getCommand()+")\n");
					System.out.printf (" ODT_No   =%s", rcvWm.getNozzleNo());
					System.out.printf (" cmdIdx   =%s\n", rcvWm.getCommandIndex());	
					System.out.printf (" trType   =%s\n", rcvWm.getTrType());	
					System.out.printf (" authType =%s\n", rcvWm.getAuthType());	
					System.out.printf (" creditNo =%s\n", Base64Util.encode(rcvWm.getCardNumber().split("=")[0]));
					System.out.printf (" bonusNo  =%s\n", Base64Util.encode(rcvWm.getBonusCard().split("=")[0]));	
					System.out.printf (" liter    =%s\n", rcvWm.getLiter());	
					System.out.printf (" basePrice=%s\n", rcvWm.getBasePrice());
					System.out.printf (" price    =%s\n", rcvWm.getPrice());	
				} 
				else if (dispMode==2) {
					LogUtility.getPumpALogger().debug("\n[소모/동화셀프-현금/신용 승인요청]");
					LogUtility.getPumpALogger().debug(" nozzle   =" + rcvWm.getConnectNozzleNo() + 
							"("+rcvWm.getCommand()+")");
					LogUtility.getPumpALogger().debug(" ODT_No   =" + rcvWm.getNozzleNo());	
					LogUtility.getPumpALogger().debug(" cmdIdx   =" + rcvWm.getCommandIndex());
					LogUtility.getPumpALogger().debug(" trType   =" + rcvWm.getTrType());	
					LogUtility.getPumpALogger().debug(" authType =" + rcvWm.getAuthType());
					LogUtility.getPumpALogger().debug(" creditNo =" + Base64Util.encode(rcvWm.getCardNumber().split("=")[0]));
					LogUtility.getPumpALogger().debug(" bonusNo  =" + Base64Util.encode(rcvWm.getBonusCard().split("=")[0]));	
					LogUtility.getPumpALogger().debug(" liter    =" + rcvWm.getLiter());	
					LogUtility.getPumpALogger().debug(" basePrice=" + rcvWm.getBasePrice());
					LogUtility.getPumpALogger().debug(" price    =" + rcvWm.getPrice());	
				}
			}
			else if (wm.getCommand().equals("HB")) {
				HB_WorkingMessage rcvWm = (HB_WorkingMessage) wm;
				
				if (dispMode==0) {
					LogUtility.getPumpALogger().info("[Pump A][소모/동화셀프-외상 승인요청]" +  
							" | nozzle=" + rcvWm.getConnectNozzleNo() + "("+rcvWm.getCommand()+")" + " ODT_No=" + rcvWm.getNozzleNo() +
							" | cmdIdx=" + rcvWm.getCommandIndex() + 
							" | trType=" + rcvWm.getTrType() + 
							" | authType=" + rcvWm.getAuthType() + 
							" | creditNo=" + Base64Util.encode(rcvWm.getCardNumber().split("=")[0]) +
							" | bonusNo=" + Base64Util.encode(rcvWm.getBonusCard().split("=")[0]) + 
							" | liter=" + rcvWm.getLiter() + " | basePrice=" + rcvWm.getBasePrice() + 
							" | price=" + rcvWm.getPrice());	
				} 
				else if (dispMode==1) {
					System.out.printf ("\n[소모/동화셀프-외상 승인요청]\n");
					System.out.printf (" nozzle   =%s", rcvWm.getConnectNozzleNo() + 
							"("+rcvWm.getCommand()+")\n");
					System.out.printf (" ODT_No   =%s\n", rcvWm.getNozzleNo());
					System.out.printf (" cmdIdx   =%s\n", rcvWm.getCommandIndex());	
					System.out.printf (" trType   =%s\n", rcvWm.getTrType());	
					System.out.printf (" authType =%s\n", rcvWm.getAuthType());	
					System.out.printf (" creditNo =%s\n", Base64Util.encode(rcvWm.getCardNumber().split("=")[0]));
					System.out.printf (" bonusNo  =%s\n", Base64Util.encode(rcvWm.getBonusCard().split("=")[0]));	
					System.out.printf (" liter    =%s\n", rcvWm.getLiter());	
					System.out.printf (" basePrice=%s\n", rcvWm.getBasePrice());
					System.out.printf (" price    =%s\n", rcvWm.getPrice());	
				} 
				else if (dispMode==2) {
					LogUtility.getPumpALogger().debug("\n[소모/동화셀프-외상 승인요청]");
					LogUtility.getPumpALogger().debug(" nozzle   =" + rcvWm.getConnectNozzleNo() + 
							"("+rcvWm.getCommand()+")");
					LogUtility.getPumpALogger().debug(" ODT_No   =" + rcvWm.getNozzleNo());
					LogUtility.getPumpALogger().debug(" cmdIdx   =" + rcvWm.getCommandIndex());	
					LogUtility.getPumpALogger().debug(" trType   =" + rcvWm.getTrType());	
					LogUtility.getPumpALogger().debug(" authType =" + rcvWm.getAuthType());	
					LogUtility.getPumpALogger().debug(" creditNo =" + Base64Util.encode(rcvWm.getCardNumber().split("=")[0]));
					LogUtility.getPumpALogger().debug(" bonusNo  =" + Base64Util.encode(rcvWm.getBonusCard().split("=")[0]));	
					LogUtility.getPumpALogger().debug(" liter    =" + rcvWm.getLiter());	
					LogUtility.getPumpALogger().debug(" basePrice=" + rcvWm.getBasePrice());
					LogUtility.getPumpALogger().debug(" price    =" + rcvWm.getPrice());	
				}
			}
			else if (wm.getCommand().equals("ST")) {
				ST_WorkingMessage rcvWm = (ST_WorkingMessage) wm;
				
				if (dispMode==0) {
					LogUtility.getPumpALogger().info("[Pump A][소모/동화셀프-판매자료]" + 
							" nozzle=" + rcvWm.getConnectNozzleNo() + "("+rcvWm.getCommand()+")" + 
							" ODT_No=" + rcvWm.getNozzleNo() + 
							//" | creditNo=" + Base64Util.encode(rcvWm.getCardNo()) + 
							" | creditNo=" + Base64Util.encode(rcvWm.getCardNo().split("=")[0]) +
							" | bonusNo=" + Base64Util.encode(rcvWm.getBonusCardNo().split("=")[0]));
				}
				else if (dispMode==1) {
					System.out.printf ("\n[소모/동화셀프-판매자료]\n");
					System.out.printf (" nozzle   =%s", rcvWm.getConnectNozzleNo() + 
							"("+rcvWm.getCommand()+")\n");
					System.out.printf (" ODT_No   =%s\n", rcvWm.getNozzleNo());
					System.out.printf (" creditNo =%s\n", Base64Util.encode(rcvWm.getCardNo().split("=")[0]));
					System.out.printf (" bonusNo  =%s\n", Base64Util.encode(rcvWm.getBonusCardNo().split("=")[0]));
				}
				else if (dispMode==2) {
					LogUtility.getPumpALogger().debug("\n[소모/동화셀프-판매자료]");
					LogUtility.getPumpALogger().debug(" nozzle   =" + rcvWm.getConnectNozzleNo() + 
							"("+rcvWm.getCommand()+")");
					LogUtility.getPumpALogger().debug(" ODT_No   =" + rcvWm.getNozzleNo());
					LogUtility.getPumpALogger().debug(" creditNo =" + Base64Util.encode(rcvWm.getCardNo().split("=")[0]));
					LogUtility.getPumpALogger().debug(" bonusNo  =" + Base64Util.encode(rcvWm.getBonusCardNo().split("=")[0]));
				}
			}
			else if (wm.getCommand().equals("GA")) { // PI2-박동화, 2015-12-04 추가
				GA_WorkingMessage rcvWm = (GA_WorkingMessage) wm;
				
				if (dispMode==0) {
					LogUtility.getLogger().info("[Pump A][GS표준셀프-승인요청]" + " nozzle=" + rcvWm.getConnectNozzleNo() + 
							"("+rcvWm.getCommand()+")" + " | ODT_No=" + rcvWm.getNozzleNo() +
							" | messageType=" + rcvWm.getMessageType() + " | liter=" + rcvWm.getLiter() + 
							" | basePrice=" + rcvWm.getBasePrice() + " | price=" + rcvWm.getPrice() +
							" | creditNo=" + Base64Util.encode(rcvWm.getCardNumber()) + 
							//" | creditNo=" + Base64Util.encode(rcvWm.getCardNumber().split("=")[0]) +
							" | bonusNo=" + Base64Util.encode(rcvWm.getBonusCard().split("=")[0]) + 
							" | custNo=" + Base64Util.encode(rcvWm.getCustCardNo()) +
							" | cashRctNo=" + Base64Util.encode(rcvWm.getCashReceiptNo()) +
							//" | isFullPmp=" + rcvWm.getIsFullPumping() + 
							//" | custType=" + rcvWm.getCustomerType() +
							"");
				} 
				else if (dispMode==1) {
					System.out.printf ("[Pump A][GS표준셀프-승인요청]\n");
					System.out.printf (" nozzle   =%s", rcvWm.getConnectNozzleNo() + "("+rcvWm.getCommand()+")\n");
					System.out.printf (" ODT_No     =%s\n", rcvWm.getNozzleNo());
					System.out.printf (" messageType=%s\n", rcvWm.getMessageType());	
					System.out.printf (" liter      =%s\n", rcvWm.getLiter());	
					System.out.printf (" basePrice  =%s\n", rcvWm.getBasePrice());
					System.out.printf (" price      =%s\n", rcvWm.getPrice());	
					//System.out.printf (" creditNo   =%s\n", rcvWm.getCardNumber().split("=")[0]);
					System.out.printf (" bonusNo    =%s\n", Base64Util.encode(rcvWm.getBonusCard().split("=")[0]));
					System.out.printf (" custNo     =%s\n", Base64Util.encode(rcvWm.getCustCardNo()));
					System.out.printf (" cashRctNo  =%s\n", Base64Util.encode(rcvWm.getCashReceiptNo()));
					//System.out.printf (" isFullPmp=%s\n", rcvWm.getIsFullPumping());
					//System.out.printf (" custType =%s\n", rcvWm.getCustomerType());
				} 
				else if (dispMode==2) {
					LogUtility.getLogger().debug("[Pump A][GS표준셀프-승인요청]");
					LogUtility.getLogger().debug(" nozzle     =" + rcvWm.getConnectNozzleNo() + "("+rcvWm.getCommand()+")");
					LogUtility.getLogger().debug(" ODT_No     =" + rcvWm.getNozzleNo());
					LogUtility.getLogger().debug(" messageType=" + rcvWm.getMessageType());	
					LogUtility.getLogger().debug(" liter      =" + rcvWm.getLiter());	
					LogUtility.getLogger().debug(" basePrice  =" + rcvWm.getBasePrice());
					LogUtility.getLogger().debug(" price      =" + rcvWm.getPrice());	
					//LogUtility.getLogger().debug(" creditNo   =" + rcvWm.getCardNumber().split("=")[0]);
					LogUtility.getLogger().debug(" bonusNo    =" + Base64Util.encode(rcvWm.getBonusCard().split("=")[0]));	
					LogUtility.getLogger().debug(" custNo     =" + Base64Util.encode(rcvWm.getCustCardNo()));	
					//LogUtility.getLogger().debug(" cashRctNo=" + Base64Util.encode(rcvWm.getCashReceiptNo()));	
					//LogUtility.getLogger().debug(" isFullPmp=" + rcvWm.getIsFullPumping());	
					//LogUtility.getLogger().debug(" custType =" + rcvWm.getCustomerType());	
				}
				
				// PI2-박동화, 2015-12-04 추가
				//ClearUtil.setClearString(rcvWm.getEncryptCreditNo());
				//ClearUtil.setClearString(rcvWm.getCreditPassCode());
			}
			else if (wm.getCommand().equals("GT")) { // PI2-박동화, 2015-12-04 추가
				GT_WorkingMessage rcvWm = (GT_WorkingMessage) wm;
				
				if (dispMode==0) {
					LogUtility.getLogger().info("[Pump A][GS표준셀프-판매자료]" + 
							" nozzle=" + rcvWm.getConnectNozzleNo() + "("+rcvWm.getCommand()+")" + 
							" ODT_No=" + rcvWm.getNozzleNo() + " | liter=" + rcvWm.getLiter() + 
							" | basePrice=" + rcvWm.getBasePrice() + " | price=" + rcvWm.getPrice());
				}
				else if (dispMode==1) {
					System.out.printf ("[Pump A][GS표준셀프-판매자료]\n");
					System.out.printf (" nozzle   =%s", rcvWm.getConnectNozzleNo() + "("+rcvWm.getCommand()+")\n");
					System.out.printf (" ODT_No   =%s\n", rcvWm.getNozzleNo());
					System.out.printf (" liter    =%s\n", rcvWm.getLiter());
					System.out.printf (" basePrice=%s\n", rcvWm.getBasePrice());
					System.out.printf (" price    =%s\n", rcvWm.getPrice());
				} 
				else if (dispMode==2) {
					LogUtility.getLogger().debug("[Pump A][GS표준셀프-판매자료]");
					LogUtility.getLogger().debug(" nozzle   =" + rcvWm.getConnectNozzleNo() + 
							"("+rcvWm.getCommand()+")");	
					LogUtility.getLogger().debug(" ODT_No   =" + rcvWm.getNozzleNo());
					LogUtility.getLogger().debug(" liter    =" + rcvWm.getLiter());
					LogUtility.getLogger().debug(" basePrice=" + rcvWm.getBasePrice());
					LogUtility.getLogger().debug(" price    =" + rcvWm.getPrice());
				}
			}
			else if (wm.getCommand().equals("CA")) {
				CA_WorkingMessage rcvWm = (CA_WorkingMessage) wm;
				
				if (dispMode==0) {
					LogUtility.getPumpALogger().info("[Pump A][다쓰노셀프-고객유형 확인요청]" + 
							" nozzle=" + rcvWm.getConnectNozzleNo() + "("+rcvWm.getCommand()+")" + 
							" ODT_No=" + rcvWm.getNozzleNo() + " | creditNo=" + Base64Util.encode(rcvWm.getCardNo()));
				}
				else if (dispMode==1) {
					System.out.printf ("\n[다쓰노셀프-고객유형 확인요청]\n");
					System.out.printf (" nozzle   =%s", rcvWm.getConnectNozzleNo() + 
							"("+rcvWm.getCommand()+")\n");
					System.out.printf (" ODT_No   =%s\n", rcvWm.getNozzleNo());
					System.out.printf (" creditNo =%s\n", Base64Util.encode(rcvWm.getCardNo()));
				}
				else if (dispMode==2) {
					LogUtility.getPumpALogger().debug("\n[다쓰노셀프-고객유형 확인요청]");
					LogUtility.getPumpALogger().debug(" nozzle   =" + rcvWm.getConnectNozzleNo() + 
							"("+rcvWm.getCommand()+")");	
					LogUtility.getPumpALogger().debug(" ODT_No   =" + rcvWm.getNozzleNo());
					LogUtility.getPumpALogger().debug(" creditNo =" + Base64Util.encode(rcvWm.getCardNo()));
				}
			}
			else if (wm.getCommand().equals("BI")) {
				BI_WorkingMessage rcvWm = (BI_WorkingMessage) wm;
				
				if (dispMode==0) {
					LogUtility.getPumpALogger().info("[Pump A][셀프-지폐투입 정보]" + 
							" nozzle=" + rcvWm.getConnectNozzleNo() + "("+rcvWm.getCommand()+")" + 
							" ODT_No=" + rcvWm.getNozzleNo() + " | cash=" + rcvWm.getCash() +
							" | cashCount=" + rcvWm.getCashCount());
				}
				else if (dispMode==1) {
					System.out.printf ("\n[셀프-지폐투입 정보]\n");
					System.out.printf (" nozzle   =%s", rcvWm.getConnectNozzleNo() + 
							"("+rcvWm.getCommand()+")\n");
					System.out.printf (" ODT_No   =%s\n", rcvWm.getNozzleNo());
					System.out.printf (" cash     =%s\n", rcvWm.getCash());
					System.out.printf (" cashCount=%s\n", rcvWm.getCashCount());
				}
				else if (dispMode==2) {
					LogUtility.getPumpALogger().debug("\n[셀프-지폐투입 정보]");
					LogUtility.getPumpALogger().debug(" nozzle   =" + rcvWm.getConnectNozzleNo() + 
							"("+rcvWm.getCommand()+")");	
					LogUtility.getPumpALogger().debug(" ODT_No   =" + rcvWm.getNozzleNo());
					LogUtility.getPumpALogger().debug(" cash     =" + rcvWm.getCash());
					LogUtility.getPumpALogger().debug(" cashCount=" + rcvWm.getCashCount());
				}
			}
			else if (wm.getCommand().equals("BC")) {
				BC_WorkingMessage rcvWm = (BC_WorkingMessage) wm;
				
				if (dispMode==0) {
					LogUtility.getPumpALogger().info("[Pump A][셀프-지폐투입 취소요청]" + 
							" nozzle=" + rcvWm.getConnectNozzleNo() + "("+rcvWm.getCommand()+")" + 
							" ODT_No=" + rcvWm.getNozzleNo() + " | cashCount=" + rcvWm.getCashCount());
				}
				else if (dispMode==1) {
					System.out.printf ("\n[셀프-지폐투입 취소요청]\n");
					System.out.printf (" nozzle   =%s", rcvWm.getConnectNozzleNo() + 
							"("+rcvWm.getCommand()+")\n");
					System.out.printf (" ODT_No   =%s\n", rcvWm.getNozzleNo());
					System.out.printf (" cashCount=%s\n", rcvWm.getCashCount());
				}
				else if (dispMode==2) {
					LogUtility.getPumpALogger().debug("\n[셀프-지폐투입 취소요청]");
					LogUtility.getPumpALogger().debug(" nozzle   =" + rcvWm.getConnectNozzleNo() + 
							"("+rcvWm.getCommand()+")");	
					LogUtility.getPumpALogger().debug(" ODT_No   =" + rcvWm.getNozzleNo());
					LogUtility.getPumpALogger().debug(" cashCount=" + rcvWm.getCashCount());
				}
			}
			else if (wm.getCommand().equals("CL")) {
				CL_WorkingMessage rcvWm = (CL_WorkingMessage) wm;
				
				if (dispMode==0) {
					LogUtility.getPumpALogger().info("[Pump A][취소처리 자료]" + " nozzle=" + rcvWm.getNozzleNo() + 
							"("+rcvWm.getCommand()+")");
				} 
				else if (dispMode==1) {
					System.out.printf ("\n[취소처리 자료]\n");
					System.out.printf (" nozzle   =%s", rcvWm.getNozzleNo() + 
							"("+rcvWm.getCommand()+")\n");
				} 
				else if (dispMode==2) {
					LogUtility.getPumpALogger().debug("\n[취소처리 자료]");
					LogUtility.getPumpALogger().debug(" nozzle   =" + rcvWm.getNozzleNo() + 
							"("+rcvWm.getCommand()+")");
				}
			}
			
	
			if (dispMode==1)
				LogUtility.getPumpALogger().debug("\n<<<<< Transmitted Up data[" + Time.currentTime() +
					      "] : " + "nozNo=" + wm.getNozzleNo() + " command=" + wm.getCommand());
			else if (dispMode==2)
				LogUtility.getPumpALogger().info("\n<<<<< Transmitted Up data[" + Time.currentTime() +
					      "] : " + "nozNo=" + wm.getNozzleNo() + " command=" + wm.getCommand());
			
			//System.out.printf ("\n<<<<<<<<<< Up Message(in AdaptorServiceImp) noz=%s cmd=%s\n\n", wm.getNozzleNo(), wm.getCommand());
			
			wm=null;
			
		} catch (Exception e) {
			// TODO 자동 생성된 catch 블록
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
		
		return true;
	}
  
	DeviceSelector 	devSel = new DeviceSelector();
   
	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.service.AdaptorService#init(com.gsc.kixxhub.data.pump.WorkingMessage)
	 */
	public void init(WorkingMessage wm) { 

		version   = "4.0.0";
		issueDate = "2016/04/21";
		
		LogUtility.getPumpALogger().info("<<< KIXXHUB Pump Adaptor starting.... >>>");
		LogUtility.getPumpALogger().info("-Version : " + version + ", Issue-date : " + issueDate);
		
		devSel.init(wm);
		devSel.setDriverScheduler();
		devSel.startDataGatherer();
		
		try {
			ProcessSelector.setSelector(devSel);
		} catch (Exception e) {
			// TODO 자동 생성된 catch 블록
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	}

	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.service.AdaptorService#removeListener(com.gsc.kixxhub.adaptor.service.listener.AdaptorListener)
	 */
	public void removeListener(AdaptorListener listener) {
		if (listener != null) listener = null;
	}

	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.service.AdaptorService#sendModuleMsg(com.gsc.kixxhub.data.pump.WorkingMessage)
	 */
	public boolean sendModuleMsg(WorkingMessage orgWm) { // Invoked by pump-module
		
		try {
			WorkingMessage wm = (WorkingMessage) orgWm.clone();
	
			//LogUtility.getPumpALogger().debug("1. 모듈로 부터 전문수신(AdaptorServiceImp) : devNo=" + 
					//wm.getNozzleNo() + " cmd=" + wm.getCommand() + "\n");
			
			ProcessSelector.selectProcess(wm);
		} catch (Exception e) {
			// TODO 자동 생성된 catch 블록
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.service.AdaptorService#setListener(com.gsc.kixxhub.adaptor.service.listener.AdaptorListener)
	 */
	public void setListener(AdaptorListener listener) {
		AdaptorServiceImp.listener = listener ;
	}

	/*
	public void sendAsynMessage(Object obj) {
		// Auto-generated method stub
		
	}*/
}
