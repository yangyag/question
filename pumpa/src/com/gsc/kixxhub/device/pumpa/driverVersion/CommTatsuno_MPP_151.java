/* 다쓰노 MPP형 일반 주유기 */
package com.gsc.kixxhub.device.pumpa.driverVersion;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

import com.gsc.kixxhub.common.data.pump.HF_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P3_1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P7_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PA_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.PB_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S3_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S4_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S5_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S8_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SE_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SJ_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.Change;
import com.gsc.kixxhub.device.pumpa.common.SerialConnectException;
import com.gsc.kixxhub.device.pumpa.common.Sleep;
import com.gsc.kixxhub.device.pumpa.controller.TatsunoMPPNoz;
import com.gsc.kixxhub.device.pumpa.driver.CommTatsuno_MPP6;

public class CommTatsuno_MPP_151 extends CommTatsuno_MPP6 {
	
	protected static boolean[][] m_nozLocks = new boolean[100][3];
	protected static boolean[][] m_pumpGrant = new boolean[100][3];
	protected static String[][]  m_sBasePrice = new String[100][3];
	protected static boolean[][] m_setNozzle = new boolean[100][3];
	protected int		baseMinErrCnt=8, baseMaxSkipCnt=100;
	protected int		baseReadBuffInterval   = 30;
	protected int		baseReadStartInterval  = 20;
	
    protected int		baseWriteStartInterval = 20;
    
	protected int		dispLevel=0;
	protected HF_WorkingMessage HF_wm;
    protected S3_WorkingMessage S3_wm;
	protected S4_WorkingMessage S4_wm;
	
	protected S5_WorkingMessage S5_wm;
	protected S8_WorkingMessage S8_wm;
	protected SE_WorkingMessage SE_wm;
	protected SJ_WorkingMessage SJ_wm;
	
    public CommTatsuno_MPP_151 (int nozNum, String romVerStr, Vector<TatsunoMPPNoz>tatsunoMPPNozVec) {
    	
		super(nozNum, romVerStr, tatsunoMPPNozVec);	
		
		try { // $$$$$$$$$$$$$$$$$$$$$$
			//--- pumping grant(Only MPP) ---//
			t11_ds.addByte  ("STX", STX);
			t11_ds.addByte  ("SA", nozID);
			t11_ds.addByte  ("UA", SEL);
			t11_ds.addString("command", "11", 2);
			t11_ds.addByte  ("pumpingType", (byte) '0');
			t11_ds.addByte  ("presetType", (byte) '0');
			t11_ds.addString("price_liter", "000000", 6);
			t11_ds.addByte  ("bPriceFlag1", (byte) '2');
			t11_ds.addString("basePrice1", "0000", 4);
			t11_ds.addByte  ("bPriceFlag2", (byte) '2');
			t11_ds.addString("basePrice2", "0000", 4);
			t11_ds.addByte  ("bPriceFlag3", (byte) '2');
			t11_ds.addString("basePrice3", "0000", 4);
			t11_ds.addByte  ("bPriceFlag4", (byte) '2');
			t11_ds.addString("basePrice4", "0000", 4);
			t11_ds.addByte  ("bPriceFlag5", (byte) '2');
			t11_ds.addString("basePrice5", "0000", 4);
			t11_ds.addByte  ("bPriceFlag6", (byte) '2');
			t11_ds.addString("basePrice6", "0000", 4);
			t11_ds.addByte  ("ETX", ETX);
			t11_ds.addByte  ("BCC", (byte) ' ');
			t11_Buf = t11_ds.getByteStream();
		}
		catch (Exception e) {
			// TODO: handle exception
		}
    }
       
    @Override
	protected boolean isSkipWorkingMessage (WorkingMessage wm) throws Exception {
		
		boolean skip=false;
				
		if (wm.getCommand().equals("PB")) { // 정액정량 설정
			
			// 단가를 t11_ds에 저장한다.
			PB_WorkingMessage PB_wm = (PB_WorkingMessage) wm;
			
			m_basePrice = PB_wm.getBasePrice();	
			m_pumpGrant[baseNozNo][fixedSubNozNo-1] = true;

			LogUtility.getPumpALogger().info("PB 전문 수신하였습니다. nozzle=" + PB_wm.getNozzleNo() + 
					" ODT=" + PB_wm.getConnectNozzleNo()+ " baseNoz=" + baseNozNo+" fixedSubNozNo="+fixedSubNozNo+
					" liter=" + PB_wm.getLiter() + " bPrice=" + PB_wm.getBasePrice() + " price=" + PB_wm.getPrice());
						
			skip = false;
		}
		else if (wm.getCommand().equals("P3_1")) { // 단가설정
			
			// 단가를 t11_ds에 저장한다.
			P3_1_WorkingMessage P3_wm = (P3_1_WorkingMessage) wm;
			String bPrice = P3_wm.getBasePrice().substring(0, 4);
			
			m_sBasePrice[baseNozNo][fixedSubNozNo-1] = bPrice;
			m_setNozzle[baseNozNo][fixedSubNozNo-1] = true;
			
			LogUtility.getPumpALogger().info("P3 전문(단가) 수신하였습니다. nozzle=" + P3_wm.getNozzleNo());
			//LogUtility.getPumpALogger().info("nozNo=" + nozNo + " baseNoz=" + baseNozNo + " fixedSubNozNo="+fixedSubNozNo);
			LogUtility.getPumpALogger().info("m_setNozzle-> [0]=" + m_setNozzle[baseNozNo][0] + 
					" [1]=" + m_setNozzle[baseNozNo][1] + " [2]=" + m_setNozzle[baseNozNo][2]);
			
//			LogUtility.getPumpALogger().info("P3 전문 수신하였습니다. nozzle=" + P3_wm.getNozzleNo() + 
//					" ODT=" + P3_wm.getConnectNozzleNo()+ " baseNoz=" + baseNozNo+" fixedSubNozNo="+fixedSubNozNo+
//					" bPrice=" + P3_wm.getBasePrice());
			//PB_wm.print();
			
			TxQue.enQueue(t15_Buf); // 상태정보요청(P3 수신이전에는 m_setNozzle의 값이 초기상태여서 상태정보 생성을 못함)
			
			skip = true;
		}
		else if (wm.getCommand().equals("PA")) { // 노즐제어 요청(비상정지/해제)

			PA_WorkingMessage PA_wm = (PA_WorkingMessage) wm;
			int nNozState = Change.toValue(PA_wm.getNozzleState());

			m_nozLock = (nNozState==0 ? true : false);
			m_nozLocks[baseNozNo][fixedSubNozNo-1] = m_nozLock;
			
			if (m_nozLock==true) { // 비상정지
				makeStatusInfo_direct(656);
				skip = false;
			}
			else { // 비상해제
				makeStatusInfo_direct(657);
				skip = true;
			}

			LogUtility.getPumpALogger().info("PA 전문 수신하였습니다. nozzle=" + PA_wm.getNozzleNo() + 
					" mode=" + PA_wm.getNozzleState());
					
		}
		else if (wm.getCommand().equals("P7")) { // 주유기 파라미터 설정

			P7_WorkingMessage P7_wm = (P7_WorkingMessage) wm;
			readBuffInterval   = baseReadBuffInterval + Change.toValue(P7_wm.getReadBuffInterval());
			readStartInterval  = baseReadStartInterval + Change.toValue(P7_wm.getReadStartInterval());
			writeStartInterval = baseWriteStartInterval + Change.toValue(P7_wm.getWriteStartInterval());
			minErrCnt 		   = baseMinErrCnt + Change.toValue(P7_wm.getLineErrorCount());

			// init values
			dispLevel=0;
			m_isSaveSTX=false;
			
			int lineErrSkipCnt = Change.toValue(P7_wm.getLineErrorSkipCount());
			if (lineErrSkipCnt >= 9000 && lineErrSkipCnt <= 9003)
				dispLevel = lineErrSkipCnt - 9000; // dispLevel : 0 ~ 3
			else
				maxSkipCnt = baseMaxSkipCnt + lineErrSkipCnt;

			skip = true;
		}
		
		return skip;
	}
    	
	@Override
	public void processRecvSTX () throws SerialConnectException, Exception {

		Calendar cal = new GregorianCalendar();
		
		//############# State check ##############//
		if (RxBuf[3]=='0' && RxBuf[4]=='0') { // 주유기 초기화
						
			rcvInitBuf[0] = RxBuf[3];
			rcvInitBuf[1] = RxBuf[4];
			rcvInitBuf[2] = RxBuf[5];
			rcvInitBuf[3] = RxBuf[6];
			rcvInitBuf[4] = RxBuf[7];
			rcvInitBuf[5] = RxBuf[8];

			LogUtility.getPumpALogger().info("####### Completed Initialization : baseNozNo="+baseNozNo+"#######");
			
			byte[] sndInitBufTmp = getInitData (rcvInitBuf);
			byte[] sndInitBuf = new byte[9];
			sndInitBuf[0] = STX;
			sndInitBuf[1] = nozID;
			sndInitBuf[2] = SEL;
			sndInitBuf[3] = sndInitBufTmp[0];
			sndInitBuf[4] = sndInitBufTmp[1];
			sndInitBuf[5] = sndInitBufTmp[2];
			sndInitBuf[6] = sndInitBufTmp[3];
			sndInitBuf[7] = ETX;
			sndInitBuf[8] = getBCC (sndInitBuf);

			TxQue.enQueue(sndInitBuf);
			progressStep=1;
		}
		else if (RxBuf[3]=='6' && RxBuf[4]=='0') { // 주유기 상황보고

			progressStep = 2;		
		} 
		else if (RxBuf[3]=='6' && RxBuf[4]=='1') { // 주유기 Status 보고(주유중/완료 정보)

			// 주유중 자료 누락처리용 추가(08/10/10)
			waitPumpingData=false;
			waitPumpingData_Cnt=0;
			progressStep = 3;
			
			short state = (short) (RxBuf[5] - 0x30);			
			long currTime = cal.getTimeInMillis();
									
			// 수정 (09/01/16)
			if ((nozState>=5 || (nozState==0 && realPumpingStart==true)) && state==1)
				return; // 무시
						
			switch (state) {
				
				case 0 :  // 노즐다운
					if ((currTime - m_nNozUpTime) > 3000) { // 3 sec
						m_nNozDownTime=currTime;
						//LogUtility.getPumpALogger().debug("Nozzle-Down Time=====>>>>" + m_nNozDownTime + " (Noz="+(baseNozNo+subNozNo-1)+")");
						nozState=0;
					} else { // 무시함
						Sleep.sleep(500);
						TxQue.enQueueNewer(t15_Buf); // 상태정보요청
						nozState=1;
						return;
					}
					break;
				case 1 :  // 노즐업
					if ((currTime - m_nNozDownTime) > 3000) { // 3 sec
						m_nNozUpTime=currTime;
						//LogUtility.getPumpALogger().debug("Nozzle-Up Time=====>>>>" + m_nNozUpTime + " (Noz="+(baseNozNo+subNozNo-1)+")");
						nozState=1;
					} else { // 무시함
						Sleep.sleep(500);
						TxQue.enQueueNewer(t15_Buf); // 상태정보요청
						nozState=0;
						return;
					}
					break;
					
				case 3 :  // 주유중
					nozState = 4;
					break;
					
				case 4 :  //주유완료
					nozState = 5;
					recvedPumpingEnd=true;
					break;
					
				default :
					nozState = state;
					break;
			}
			
			nozState = (pumpingStart==true && nozState==0 && 
					    recvedPumpingEnd==false ? 5 : nozState); //주유완료자료 미수신 처리 -> 삭제(09/01/08)                    
			
			// 다쓰노6복식(셀프) 가상노즐번호 처리(데이터 수신한 subNozNo 값을 구함)
			int subNoz = RxBuf[23] - 0x30;
			if (subNoz >= 1) {
				subNozNo = subNoz;
				//subNozNo = (subNoz==5? 3 : subNoz);
				m_recvedSubNozID=true; // 61번 전문의 nozzle status 값 1~3 수신시 true
			} 
			
			//LogUtility.getPumpALogger().debug("ProcessRcvSTX=====> nozState="+nozState+" subNoz="+subNoz+" subNozNo="+subNozNo+" m_recvedSubNozID="+m_recvedSubNozID);			
		}
		else if (RxBuf[3]=='6' && RxBuf[4]=='5') { // 주유기 적산치 보고

			switch (progressStep) {
				
				case 3  : 
					if (nozState==1 && m_nozLocks[baseNozNo][subNozNo-1]==false) { // 노즐업상태에서 수신				
						SJ_wm = (SJ_WorkingMessage) generateWorkingMessage(RxBuf, "SJ", subNozNo);
						insertRecvQueue(SJ_wm); // 주유시작 자료 송신
						sentPumpingStartInfo=true;
						return; // 추가(09/02/02)
					}
					else if (nozState==4) { // 주유중 수신
						// Skip
					}
					else if (nozState==5 || (nozState==0 && realPumpingStart==true)) { // 주유완료후 수신
						progressStep = 4;
						nozState = 5;
					} 
					/*else if (progressStep==3 && (nozState==0 && realPumpingStart==false)) {
						//System.out.printf ("(2-1) : nozNo=%d subNozNo=%d\n", nozNo, subNozNo);	
						insertRecvQueue(generateWorkingMessage(RxBuf, "S5", subNozNo)); // 토털게이지 자료 송신
					}*/
					
					break;
				
				case 4 :
					break; // skip
			}
		}


		//############# STX Process ##############//
		switch (progressStep) {
		case 3 :

			if (nozState==0) { // 노즐다운

				if (m_setNozzle[baseNozNo][subNozNo-1]==true) {

					pumpingStart=false; // 초기화
					sentPumpingStartInfo=false;				
					beforeNozzleUp=false;
					makeStatusInfo(nozState); // 상태정보 전송
	
					LogUtility.getPumpALogger().info("nozzle=" + (baseNozNo+subNozNo-1) + " -> 노즐다운 : subNozNo=" + subNozNo + 
							" nozLocks[0]=" + m_nozLocks[0] + " nozLocks[1]=" + m_nozLocks[1] + " nozLocks[2]=" + m_nozLocks[2]);
				}
			}
			else if (nozState==1) { // 노즐업 & 일반주유기	

				if (m_setNozzle[baseNozNo][subNozNo-1]==true) {

					flushBuffer(lastPumpingData);
					pumpingStart=true; // Apppend 2008.04.19 (동탄주유소)
					beforeNozzleUp=true;
					realPumpingStart=false; // 초기화
					recvedPumpingEnd=false;
					
					makeStatusInfo(nozState); // 상태정보 전송
					
					if (sentPumpingStartInfo==false) {
						TxQue.enQueue(t20_Buf); // 토털게이지 요청(주유시작 정보 수신용)
						sentPumpingStartInfo=true;
					}	
	
//					LogUtility.getPumpALogger().info("\n#######노즐업>>>> nozzle=" + nozNo + " subNozNo=" + subNozNo +  " pumpGrant=" + m_pumpGrant +
//							" m_bSetNozzle-> [0]=" + m_setNozzle[baseNozNo][0] + " [1]=" + m_setNozzle[baseNozNo][1] + " [2]=" + m_setNozzle[baseNozNo][2] +"\n");
					
					// $$$$$$$$$$$$$$$$$$$$$$
					if (nozType==1 && m_pumpGrant[baseNozNo][subNozNo-1]==false && m_nozLocks[baseNozNo][subNozNo-1]==false) { // 일반주유기
						
						m_nozLocks[baseNozNo][subNozNo-1] = false;
						m_pumpGrant[baseNozNo][subNozNo-1]=true;
						
						switch(subNozNo) {
						case 1 :
							t11_ds.editString("basePrice1", m_sBasePrice[baseNozNo][0], 4);
							break;
						case 2 :
							t11_ds.editString("basePrice2", m_sBasePrice[baseNozNo][1], 4);
							break;
						case 3 :
							t11_ds.editString("basePrice3", m_sBasePrice[baseNozNo][2], 4);
							break;
						}
						
						t11_Buf = t11_ds.getByteStream();
						TxQue.enQueue(t11_Buf); // 주유허가(정액/정량 설정)
	
						LogUtility.getPumpALogger().info("nozzle=" + (baseNozNo+subNozNo-1) + " -> 노즐업, 주유허가 : subNozNo=" + subNozNo + 
							" nozLocks[0]=" + m_nozLocks[0] + " nozLocks[1]=" + m_nozLocks[1] + " nozLocks[2]=" + m_nozLocks[2]);
					}
					else
						LogUtility.getPumpALogger().info("nozzle=" + (baseNozNo+subNozNo-1) + " -> 노즐업 : subNozNo=" + subNozNo + 
							" nozLocks[0]=" + m_nozLocks[0] + " nozLocks[1]=" + m_nozLocks[1] + " nozLocks[2]=" + m_nozLocks[2]);
				}
			}
			else if (nozState == 4) { // 주유중
					
				lastPumpingData = RxBuf.clone(); // 최종 주유정보 저장
				pumpingStart=true;
				realPumpingStart=true;
				
				if (firstPumpingData==true) {
					makeStatusInfo(nozState); // 상태정보 전송
					firstPumpingData=false;
				}
					
				TxQue.enQueueNewer(t15_Buf);
				waitPumpingData=true; // 주유중 자료 누락처리용 추가(08/10/10)

				S3_wm = (S3_WorkingMessage) generateWorkingMessage(RxBuf, "S3");
										
				insertRecvQueue(S3_wm);

			}
			else if (nozState == 5) { // 주유완료
							
				lastPumpingData = RxBuf.clone(); // 최종 주유정보 저장
	
				TxQue.enQueue(t20_Buf); // 토털게이지 요청
				TxQue.enQueue(t13_Buf); // 허가취소
				
				waitTotalGuageFor_S4=true;
				progressStep = 4;
				
				if (m_nozLocks[baseNozNo][subNozNo-1]==false)
					makeStatusInfo(nozState); // 상태정보 전송(주유완료)
				
				LogUtility.getPumpALogger().info("nozzle=" + (baseNozNo+subNozNo-1) + " -> 주유완료, 토털게이지 요청 : subNozNo=" + subNozNo + 
						" nozLocks[0]=" + m_nozLocks[0] + " nozLocks[1]=" + m_nozLocks[1] + " nozLocks[2]=" + m_nozLocks[2]);
			}
			break;

		case 4 :
			if (nozState == 5) { // 주유완료후 토털게이지 수신
				
				byte[][] tBuf = new byte [2][80];
				tBuf[0] = lastPumpingData.clone(); // 최종 주유정보
				flushBuffer(lastPumpingData);
				tBuf[1] = RxBuf.clone(); // Total guage

				progressStep = 3;
				nozState = 0;

				TxQue.enQueue(t13_Buf); // 허가취소

				//if (m_nozLocks[subNozNo-1]==false) {
				if (m_pumpGrant[baseNozNo][subNozNo-1]==true) {
					
					if (m_recvedSubNozID==true && tBuf[0][23] > 0) { // 추가(09/01/08) - tBuf[0][23]=subNoz number
						S4_wm = (S4_WorkingMessage) generateWorkingMessage(tBuf, "S4", subNozNo);

						int nPrice=0, nLiter=0; 
						if (S4_wm != null) {
							nPrice = Change.toValue(S4_wm.getPrice());
							nLiter = Change.toValue(S4_wm.getLiter());
						}
						
						if (realPumpingStart==true) {
							if (nPrice>0 && nLiter>0)
								insertRecvQueue(S4_wm); // 주유완료 자료
						} 
						else if (realPumpingStart==false) {
							S4_wm.setLiter("0000000");
							S4_wm.setPrice("00000000");
							insertRecvQueue(S4_wm); // 주유완료 자료
						}
						
						LogUtility.getPumpALogger().info("nozzle=" + (baseNozNo+subNozNo-1) + " -> 주유완료, 토털게이지 수신. 완료전문(S4) 전송 루틴: m_pumpGrant=" +m_pumpGrant[subNozNo-1]);
						LogUtility.getPumpALogger().info("nozzle=" + (baseNozNo+subNozNo-1) + " -> realPumpingStart="+realPumpingStart+" nPrice="+nPrice+" nLiter="+nLiter);
					}
					
					m_recvedSubNozID=false;
				}
				else {
					LogUtility.getPumpALogger().info("nozzle=" + (baseNozNo+subNozNo-1) + " -> 주유완료, 토털게이지 수신. 완료전문(S4) 전송않음 : m_pumpGrant=" + m_pumpGrant[subNozNo-1]);
				}
				
				makeStatusInfo(nozState); // 상태정보 전송(노즐다운)

				//LogUtility.getPumpALogger().debug("####>>>> 노즐다운. m_nozLocks= " + m_nozLocks[subNozNo-1] +
						//" beforeNozzleUp="+beforeNozzleUp+" realPumpingStart="+realPumpingStart);
						
				// 수정(09/01/13)
				pumpingStart=false; 
				sentPumpingStartInfo=false;
				firstPumpingData=true;

				realPumpingStart=false;
				waitTotalGuageFor_S4=false;
				waitGaugeForS4_Cnt=0;
				sendPumpingData_Cnt=0;
				m_pumpGrant[baseNozNo][subNozNo-1]=false;
				
				//m_nozLocks[subNozNo-1] = false; // true -> false로 수정(09/06/14)
			}
			break;
		}
	}
}
