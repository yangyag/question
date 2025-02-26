//------------------------------------------------------//
//----- 동화프라임(Protocol=WooJoo, ROMVer=113)---------//
//----- 내용 : 토털게이지 수신처리 동기방식으로 변경 ---//
//----- * 수정중 -> 테스트 미완료, 적용여부 미결정 -----//
//------------------------------------------------------//
package com.gsc.kixxhub.device.pumpa.driverVersion;

import gnu.io.SerialPortEvent;

import com.gsc.kixxhub.common.data.pump.HF_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S3_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S4_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SJ_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.Change;
import com.gsc.kixxhub.device.pumpa.common.Log;
import com.gsc.kixxhub.device.pumpa.common.SerialConnectException;
import com.gsc.kixxhub.device.pumpa.common.Sleep;
import com.gsc.kixxhub.device.pumpa.driver.CommWooJoo;

public class CommWooJoo_113 extends CommWooJoo {

	protected int	s0_cnt = 0;
	
	/**
	 * @param nozNum
	 * @param romVerStr
	 */
	public CommWooJoo_113(int nozNum, String romVerStr) {
		
		super(nozNum, romVerStr);
		
		baseReadStartInterval  =10+4; // 스마트주유소 PJT 보정 
		baseWriteStartInterval =10+4; // 스마트주유소 PJT 보정 
		baseReadBuffInterval   =20+6; // 스마트주유소 PJT 보정 
		baseMinErrCnt		   =8;

		readCmdInterval    =20; // 고정
	}
	

	@Override
	public void ownershipChange(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void processRecvSTX () throws SerialConnectException, Exception {
	
		//############# State check ##############//
		if (RxBuf[4]=='s' && RxBuf[5]=='0') { // 주유기 상태정보 수신

			short state = (short) (RxBuf[6] - 0x30);
			nozState =  state;
		
			//pumpingStart = (nozState==4 ? true : pumpingStart); // 주유시작
			//nozState = (pumpingStart==true && nozState==0 ? 5 : nozState); // S5(주유완료) 누락시 처리
			pumpingStart = (nozState >= 4 ? true : pumpingStart); // 주유시작
			nozState = (pumpingStart==true && nozState < 2 ? 5 : nozState); // S4(주유완료) 누락시 처리
			/*
			if (beforeNozzleUp==false && nozState==4) { // 용인대한주유소 추가(08/08/12)
				beforeNozzleUp=true;
				nozState=3;
				pumpingStart=false;
			}
			*/
			//System.out.printf ("1. ===> m_nozLock=%s progState=%d nozState=%d\n", 
					//nozLock, progressStep, nozState);
			if (nozState==2 && nozState==3) // 추가 (08/12/19) for 신한국주유기
				nozState = (pumpingStart==true? 4 : nozState); 
		} 
		else if (RxBuf[4]=='p' && RxBuf[5]=='0') { // 주유중 정보 수신
			
			String type; 
			String szLiter = "", szPrice="";

			// 주유중 자료 누락처리용 추가(08/10/10)
			waitPumpingData=false;
			waitPumpingData_Cnt=0;
			
			//--- Preset-data processing of quick-win nozzle ---//
			p0r_ds.setByteStream(RxBuf);
			szLiter = (String) p0r_ds.getValue("liter");
			szPrice = (String) p0r_ds.getValue("price");
			int liter=Change.toValue (szLiter);
			int price=Change.toValue (szPrice);
			
			if (liter==0 && price>0) {
				type = "0"; // 정액설정
				szLiter = "0000000";
			} else 
			if (liter>0 && price==0 && liter!=4000000) {
				type = "1"; // 정량설정
				szPrice = "000000";
			} else {
				type = "2"; // 설정없음
				szLiter = "0000000";
				szPrice = "000000";
			}

			if (type.equals("0") || type.equals("1")) { // Preset Data(정액/정량 설정상태)

				HF_wm = new HF_WorkingMessage();
				HF_wm.setNozzleNo(Change.toString("%02d", nozNo));
				HF_wm.setType(type);
				HF_wm.setLiter(szLiter);
				String szBasePrice = (String) e0_ds.getValue("basePrice") + "00";
				HF_wm.setBasePrice(szBasePrice);
				HF_wm.setPrice(szPrice);
			
				insertRecvQueue(HF_wm);
				
				presetDataFlag=true;
				presetType=type;
				presetLiter=szLiter;
				presetBasePrice=szBasePrice;
				presetPrice=szPrice;
			}
			else { // Quick-win Preset 자료가 아니면 주유중으로 상태변경
				//if (liter>0 && price>0) // 추가여부 검토
				nozState = (nozState==3 ? 4 : nozState);
				//pumpingStart = (nozState==4 ? true : pumpingStart); // 주유시작
				pumpingStart = (nozState >= 4 ? true : pumpingStart); // 주유시작
			}
		}
		else if (RxBuf[4]=='t' && RxBuf[5]=='0') { // 토털게이지 정보수신
			/*
			if (nozState==1 || nozState==3 || nozState==4) { // 노즐업/주유중 상태에서 수신

				SJ_wm = (SJ_WorkingMessage) generateWorkingMessage(RxBuf, "SJ");
				//last_SJ_TotalGauge = SJ_wm.getTotalGauge(); // 삭제(2008/09/06)
				if(sentPumpingStartInfo==false) // AAAAA-추가(2008/09/06)
					insertRecvQueue(SJ_wm); // 주유시작 자료 송신
				
				//---- 이전 주유자료 수신문제 발생시 삭제검토 ----//
				TxQue.enQueueNewer(p0_Buf); // 주유자료 요청-용인대한주유소 추가(08/08/12)
				
				sentPumpingStartInfo=true;
				waitTotalGuageFor_SJ=false;
				waitGaugeForSJ_Cnt = 0;
			}
			else if (progressStep==4) { // 주유완료후 수신
				progressStep = 5;
			} 
			else 
				insertRecvQueue(generateWorkingMessage(RxBuf, "S5")); // 토털게이지 자료 송신
			*/

			insertRecvQueue(generateWorkingMessage(RxBuf, "S5")); // 토털게이지 자료 송신
		}
		else if (RxBuf[4]=='q') { // 주유기이상정보 추가(08/12/07 for SomoSelf)
			insertRecvQueue(generateWorkingMessage(RxBuf, "Q0")); 
		}

		//LogUtility.getPumpALogger().debug("@STX Start : nozzle=" + nozStr + " ProgStep=" +
				//progressStep + " nozState=" + nozState + " pumpingStart=" + pumpingStart);
		
		
		//############# STX Process ##############//
		switch (progressStep) {
		case 3 :
			if (nozState==0 || nozState==2) { // 노즐다운

				sentPumpingStartInfo=false;
				waitTotalGuageFor_SJ=false; // 추가->주유시작(SJ) 반복생성 문제해결(08/08/11)
				waitGaugeForSJ_Cnt=0; // 추가(2008/09/06)
				
				if (sentNozDownInfo==false) { // 추가 (08/12/19)
					makeStatusInfo(nozState); // 상태정보 전송
					sentNozDownInfo=true;
				}
				
				setNozzleBasePrice(e0_Buf);
				
				if (m_nozLock==false && nozType==1 && nozState==0 && pumpingStart==false) {
					
					if (Change.toValue(m_basePrice) > 0) {
						TxQue.enQueueNewer(e0_Buf); // 주유허가(정액/정량 설정)
						LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> 노즐다운, 주유허가(e0) : progStep=" + progressStep + 
								" nozState=" + nozState + " m_basePrice=" + m_basePrice);
						//Log.datas(e0_Buf, e0_Buf.length, 20);
					}
				}
				
				// 비상정지/해제 상태정보 처리
				if (m_nozLock==true)
					makeStatusInfo(656); // 비상정지
				else if (m_recvedNozLock==true) {
					makeStatusInfo(657); // 비상해제
					m_recvedNozLock=false;
				}

				// 노즐업다운시 값=0 주유완료 자료 전송 -> POS에서 프리셋 설정후 취소처리용
				// POS 프리셋 여부는 모듈에서 판단
				if (beforeNozzleUp==true && realPumpingStart==false && // 정상 주유완료자료가 아니면
						sentPumpingEndInfo==false && nozState==2) { // nozState==2 추가(08/12/03) for SomoSelf
					
					S4_wm = new S4_WorkingMessage();
					S4_wm.setNozzleNo(Change.toString("%02d",nozNo));
					S4_wm.setLiter("0000000");
					S4_wm.setBasePrice(m_basePrice);
					S4_wm.setLiter("00000000");
					//S4_wm.setTotalGauge(last_SJ_TotalGauge);
					S4_wm.setTotalGauge("0000000000"); // 수정(2008/09/06)
					
					insertRecvQueue(S4_wm); // 주유완료 자료
					sentPumpingEndInfo=true;
					
					LogUtility.getPumpALogger().debug("nozzle=" + nozNo + 
							" -> 노즐업다운, 선결제 주유취소용 주유완료전문(S4) 생성");
				}
				
				beforeNozzleUp=false;
				
			}
			else if (nozState==1 || nozState==3) { // 노즐업

				//makeStatusInfo(nozState); // 상태정보 전송->아래로 이동(2008/07/22)
				beforeNozzleUp=true;
				realPumpingStart=false;
				sentPumpingEndInfo=false;
				sentNozDownInfo=false;

				if (nozState==3 && sentPumpingStartInfo==false) {
					//TxQue.enQueueNewer(t0_Buf); // 토털게이지 요청(주유시작 정보 수신용)					
					makeStatusInfo(nozState); // 상태정보 전송, 위치이동(08/07/22)됨
					
					
					
					byte[] byTotalGauge = new byte[18];
					processTotalGauge(byTotalGauge);
					
					SJ_wm = (SJ_WorkingMessage) generateWorkingMessage(byTotalGauge, "SJ");
					//last_SJ_TotalGauge = SJ_wm.getTotalGauge(); // 삭제(2008/09/06)
					if(sentPumpingStartInfo==false) // AAAAA-추가(2008/09/06)
						insertRecvQueue(SJ_wm); // 주유시작 자료 송신
					
					//---- 이전 주유자료 수신문제 발생시 삭제검토 ----//
					TxQue.enQueueNewer(p0_Buf); // 주유자료 요청-용인대한주유소 추가(08/08/12)
					
					
					
					
					sentPumpingStartInfo=true;
					waitTotalGuageFor_SJ=false;
					waitGaugeForSJ_Cnt = 0;
					
					waitTotalGuageFor_SJ=true;
				}
				// 추가(08/11/29 for SomoSelf)
				else if (nozType==2)
					makeStatusInfo(nozState); // 상태정보 전송(노즐업)

				if (nozType!=2) // 추가(08/11/29 for SomoSelf) - 셀프주유기 아니면
					TxQue.enQueueNewer(s0_Buf); // 소모구형 s04와 주유자료 누락처리, 테스트룸 #6 (2008/07/22)
				
			}
			else if (nozState == 4) { // 주유중

				if (RxBuf[4]=='p' && RxBuf[5]=='0') {

					S3_wm = (S3_WorkingMessage) generateWorkingMessage(RxBuf, "S3");
					int nPrice=0, nLiter=0;
					if (S3_wm != null) {
						nPrice = Change.toValue(S3_wm.getPrice());
						nLiter = Change.toValue(S3_wm.getLiter());
					}
					
					// 이전 주유량 수신시 Skip 을 위한 추가(08/08/29)
					if (nPrice > 0 && nLiter > 0) {			
						if (newPumpingData==false && nLiter==m_nLiter) {
							LogUtility.getPumpALogger().debug("nozzle=" + nozNo + 
									" -> 주유량(p0)이 이전 주유량과 동일합니다. : 주유량(x.3)=" + 
									nLiter + " 이전 주유량(x.3)=" + m_nLiter);
						}
						else {
							lastPumpingData = RxBuf.clone(); // 최종 주유정보 저장
							realPumpingStart=true;
							newPumpingData=true;
						}
					}

					if (presetDataFlag==true) 
						presetDataFlag=false; // Preset data 이면 skip
					else {
						if (nPrice > 0 && nLiter > 0) {
							if (firstPumpingData==true) {
								makeStatusInfo(nozState); // 상태정보 전송(주유중)
								firstPumpingData=false;
							}
							if (newPumpingData==true) // 추가(08/08/29)
								insertRecvQueue(S3_wm); // 주유중 자료 모듈로 송신
						}
					}
				}
				sentPumpingEndInfo=false; 
				
				if (romVer.equals("002")) { // 소모 페트로비즈
					
					if (p0_cnt%2==0) {
						TxQue.enQueueNewer(p0_Buf); // 주유자료 요청
						//waitPumpingData=true; // 주유중 자료 누락처리용 추가(08/10/10)->AAA로?
						p0_cnt=0;
					}
					else {
						TxQue.enQueueNewer(s0_Buf); // 상태정보 요청
					}
						
					p0_cnt++;
				} 
				else {
					TxQue.enQueueNewer(p0_Buf); // 주유자료 요청
					//waitPumpingData=true; // 주유중 자료 누락처리용 추가(08/10/10)->AAA로?
				}
				
				waitPumpingData=true; // AAA (08/10/18)
				sentNozDownInfo=false;
				
			}
			else if (nozState == 5) { // 주유완료
							
				if ((RxBuf[4]=='p' && RxBuf[5]=='0') || waitLastPData_Cnt>=MAX_LAST_PDATA) {
					
					if ((RxBuf[4]=='p' && RxBuf[5]=='0')) {
						S3_wm = (S3_WorkingMessage) generateWorkingMessage(RxBuf, "S3");
						int nPrice=0, nLiter=0;
						if (S3_wm != null) {
							nPrice = Change.toValue(S3_wm.getPrice());
							nLiter = Change.toValue(S3_wm.getLiter());
						}
						if (nPrice > 0 || nLiter > 0) // 수정(08/11/24) : && -> ||
							lastPumpingData = RxBuf.clone(); // 최종 주유정보 저장

						LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> 주유완료, 최종주유값 수신(p0) : progStep=" + 
									progressStep + " waitLastPData_Cnt=" + waitLastPData_Cnt + 
									" liter=" + S3_wm.getLiter() + " price=" + S3_wm.getPrice());
					} else
						LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> 주유완료, 최종주유값 미수신(p0), 이전주유값 적용 : progStep=" + 
								progressStep + " waitLastPData_Cnt=" + waitLastPData_Cnt);
					
					//TxQue.enQueueNewer(t0_Buf); // 토털게이지 요청
					
					
					
					
					
					byte[] byTotalGauge = new byte[18];
					processTotalGauge(byTotalGauge);
					
					byte[][] tBuf = new byte [2][buffSize];

					tBuf[0] = lastPumpingData.clone(); // 최종 주유정보
					flushBuffer(lastPumpingData);
					tBuf[1] = byTotalGauge.clone(); // Total gauge
					
					progressStep = 3;
					nozState = 0;
					progressStep4Cnt=0;

					pumpingStart=false;
					sentPumpingStartInfo=false;
					firstPumpingData=true;
					newPumpingData=false;
					
					LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> 주유완료, 토털게이지 수신(t0) : progStep=" + 
							progressStep + " nozState=" + nozState + " pumpingStart=" + pumpingStart);

					setNozzleBasePrice(e0_Buf);
					
					if (m_nozLock==false && nozType==1) { // 일반주유기
						if (Change.toValue(m_basePrice) > 0) {
							TxQue.enQueueNewer(e0_Buf); // 주유허가(정액/정량 설정)				
							LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> 토털게이지 수신, 주유허가(e0) : progStep=" + 
									progressStep + " nozState=" + nozState + " m_basePrice=" + m_basePrice);
							//Log.datas(e0_Buf, e0_Buf.length, 20);
						}
					}

					S4_wm = (S4_WorkingMessage) generateWorkingMessage(tBuf, "S4");
					S4_wm.setNozzleNo(Change.toString("%02d",nozNo));
					int nPrice=0, nLiter=0; long nGauge=0;
					if (S4_wm != null) {
						nPrice = Change.toValue(S4_wm.getPrice());
						nLiter = Change.toValue(S4_wm.getLiter());
						nGauge = Change.toLongValue(S4_wm.getTotalGauge());
					}
					m_nPrice = nPrice;
					m_nLiter = nLiter;
					
					if (nGauge==0)
						LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> 토털게이지값이 0 이거나, 미수신하여 0 으로 생성");
					
					/* ---보정처리 적용여부 미결정 -> 보류
					int nGauge = Change.toValue(S4_wm.getTotalGauge());
					if (nGauge==0) {
						S4_wm.setTotalGauge(addTotalGauge(last_SJ_TotalGauge, S4_wm.getLiter()));
						LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> 주유완료(S4) 토털게이지값=0, 게이지값 보정처리");
					}
					else
						last_S4_TotalGauge = S4_wm.getTotalGauge();
					*/
					
					 // 수정(08/09/10)
					if (nLiter <= 0 || nPrice <= 0) {
						S4_wm.setBasePrice(m_basePrice);
						LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> 주유완료(S4) 자료값=0 : Liter=" + 
								nLiter + " Price=" + nPrice);
					}
					if (sentPumpingEndInfo==false) {
						insertRecvQueue(S4_wm); // 주유완료 자료
						sentPumpingEndInfo=true;
					}
					
					if (nozType==2) // 추가(2008/11/19 for SomoSelf)
						TxQue.enQueueNewer(s0_Buf);
					
					
					
					
					
					
					//LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> 주유완료, 토털게이지 요청(t0) : progStep=" + 
							//progressStep + " nozState=" + nozState + " pumpingStart=" + pumpingStart);
					
					//progressStep=4;
					makeStatusInfo(nozState); // 상태정보 전송(주유완료)
					
					// Clear preset-data
					presetType="2";
					presetLiter="0000000";
					presetBasePrice="000000";
					presetPrice="000000";

					waitLastPumpData=false;
					waitLastPData_Cnt=0;
					sentNozDownInfo=false;
				}
				else {
					waitLastPumpData=true;
					
					LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> 주유완료, 최종주유값 수신대기 : nozState=" +
							nozState + " waitLastPData_Cnt=" + waitLastPData_Cnt);
				}
			}
			break;

		case 4 :
			// skip
			break;

		case 5 : // 주유완료후 토털게이지 수신		
			break;
		}
	}

	public void processTotalGauge (byte[] byGauge) throws Exception {
		byte[] buf = new byte[18];

		recvTail_proc(ACK);
		
		for (int i=0; i<3; i++) {

			sendText(ENQ);
			Sleep.sleep(30);
			recvText(buf);
			
			if (buf[1]==ACK) {
				sendText(t0_Buf);
				Sleep.sleep(30);
				recvText(buf);
				
				if (buf[1]==ACK) {
					sendTail_proc();

					sendText(ENQ);
					Sleep.sleep(30);
					recvText(byGauge);

					if (byGauge[1]==STX) {
						
						if (compareNozID(byGauge)==false) { 
							//recvTail_proc(NAK);
							flushBuffer(byGauge);
							trimInputStream("routine : processTotalGauge 4");
							continue;
						}
						if (compareBCC(byGauge)==false) {
							//recvTail_proc(NAK);
							flushBuffer(byGauge);
							continue;
						}
						if (verifyData(byGauge)==false) { 
							//recvTail_proc(NAK);
							flushBuffer(byGauge);
							continue;
						}
						
						//recvTail_proc(ACK); // OK
						LogUtility.getPumpALogger().debug("####### Recv totalGauge......" + new String(byGauge));
						break;
					}
					else {
						trimInputStream("routine : processTotalGauge 3");
					}
				}
				else {
					trimInputStream("routine : processTotalGauge 2");
				}	
			}
			else {
				trimInputStream("routine : processTotalGauge 1");
			}
		} // end of for()
	}
		
	@Override
	public void requestData() throws SerialConnectException, Exception {
	
		byte[] 	TempBuf = new byte[buffSize];
		
		// 회선불량 처리
		boolean	occurLineErr = (lineErrCnt >= minErrCnt);
			
		if (occurLineErr) { // Check line error

			if (issueLineErr==true) {
				makeLineError(); // 회선불량
				TxQue.flushQueue();
				issueLineErr=false;
				sentNozDownInfo=false;
			}
			
			if (lineErrCnt < maxSkipCnt) {
				lineErrCnt++;
				return; // Skip
			} 
			else {
				lineErrCnt=0;
				issueLineErr=true;
			}
		}
		
		flushBuffer(RxBuf);
		
		// 회선불량 복구시 처리
		if (firstRequest==true) {
			firstRequest=false;
			TxQue.enQueueNewer(s0_Buf); // 상태정보요청
		}
		if (m_statusCode==601) { // 회선불량
			if (lineCommCheckCnt%(minErrCnt+5)==0) {
				lineCommCheckCnt=0;
				TxQue.enQueueNewer(s0_Buf); // 상태정보요청
			}
			lineCommCheckCnt++;
		}
		/*
		// 주유시작(SJ) 토털게이지 미수신 처리
		if (waitTotalGuageFor_SJ==true) {
			if (waitGaugeForSJ_Cnt >= MAX_GAUGE_SJ) {
				SJ_wm = new SJ_WorkingMessage();
				SJ_wm.setNozzleNo(Change.toString("%02d",nozNo));
				//SJ_wm.setTotalGauge(last_S4_TotalGauge);
				SJ_wm.setTotalGauge("0000000000");
				insertRecvQueue(SJ_wm); // 주유시작 자료 송신

				waitGaugeForSJ_Cnt=0;
				sentPumpingStartInfo=true;
				waitTotalGuageFor_SJ=false; // 수정(08/08/11)

				LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> 주유시작 토털게이지(t0) 미수신, 주유시작(SJ) 전문 생성");
			}
			else
				waitGaugeForSJ_Cnt++;
			
			LogUtility.getPumpALogger().debug("waitGaugeForSJ_Cnt="+waitGaugeForSJ_Cnt);
		}*/
		
		// 주유중 자료(S3) 미수신 처리
		if (nozState==4 && waitPumpingData==true) {
			//LogUtility.getPumpALogger().debug("waitPumpingData_Cnt=" + waitPumpingData_Cnt);
			if (waitPumpingData_Cnt >= MAX_WAIT_PDATA) {
				TxQue.enQueueNewer(p0_Buf);				
				
				LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> 주유중 자료(S3)값 미수신, 주유자료 요청(p0), waitPumpingData_Cnt="+waitPumpingData_Cnt);
				waitPumpingData_Cnt=0;
			}
			else
				waitPumpingData_Cnt++;
		}
		
		// 주유완료(S4) 최종주유값 미수신 처리
		if (waitLastPumpData==true) {
			if (waitLastPData_Cnt >= MAX_LAST_PDATA) {
				try {
					processRecvSTX();
				} catch (SerialConnectException e) {
					LogUtility.getPumpALogger().error(e.getMessage(), e);
				} catch (Exception e) {
					LogUtility.getPumpALogger().error(e.getMessage(), e);
				}
			}
			else
				waitLastPData_Cnt++;
			
			LogUtility.getPumpALogger().debug("waitLastPData_Cnt="+waitLastPData_Cnt+" progStep="+
					progressStep+" nozState="+nozState);
		}
		/*
		// 주유완료(S4) 토털게이지 미수신 처리
		if (progressStep==4) {
			if (progressStep4Cnt >= MAX_PROG4STEP) {
				
				progressStep4Cnt = 0;
				progressStep = 5;
				try {
					processRecvSTX();
				} catch (SerialConnectException e) {
					LogUtility.getPumpALogger().error(e.getMessage(), e);
				} catch (Exception e) {
					LogUtility.getPumpALogger().error(e.getMessage(), e);
				}
			}
			else
				progressStep4Cnt++;
			
			LogUtility.getPumpALogger().debug("progressStep4Cnt="+progressStep4Cnt+" progStep="+
					progressStep+" nozState="+nozState);
		}*/
		

		//--- Transfer from WorkingMessage to BytesStream
		while (sndQue.getItemCount() > 0) {
			WorkingMessage wm = (WorkingMessage)sndQue.deQueue();

			LogUtility.getPumpALogger().debug("\n>>>>> Received  Down data : " +
								"nozNo=" + nozStr + " command=" + wm.getCommand());

			if (isSkipWorkingMessage(wm)==true) {
				wm=null;
				continue;
			}
						
			byte[] wmByte = generateByteStream(wm);
			if (wmByte != null) 
				TxQue.enQueue(wmByte);
			
			if (wm.getCommand().equals("PB")) {
				LogUtility.getPumpALogger().debug(":::::::::>>> ByteStrems of workingMessage ["+ wm.getCommand() + "] in requestData().");
				Log.datas(wmByte, wmByte.length, 20);
			}
			
			wm=null;
		}
		if (dispLevel>=3)
			LogUtility.getPumpALogger().debug("\nStart request ============> WooJoo, nozNo=" + nozNo);

		try {
			//##### Send ENQ and Recv ACK/STX #####//
			if (dispLevel>=3)
				LogUtility.getPumpALogger().debug("----POLLING----(progStep="+progressStep+", nozState="+nozState+")");
			
			//--- Send ENQ ---//
			if(sendText (ENQ) != true) { // fail
				lineErrCnt++;
				return;
			}

			if (dispLevel>=3)
				LogUtility.getPumpALogger().debug("0.Send ENQ (Noz="+nozNo+")");
						
			//--- recv data---//
			if (recvText(RxBuf) < 1) {
				if (dispLevel>=2)
					LogUtility.getPumpALogger().debug("0.Recv ACK/STX fail!(No response from nozzle) (Noz="+nozNo+")");
				lineErrCnt++;
				return;
			}
			
			//###### Recv data : STX ######//
			if (RxBuf[1] == STX) {
				
				if (dispLevel>=3) {
					LogUtility.getPumpALogger().debug ("1.Recv STX (Noz="+nozNo+")");
					Log.datas(RxBuf, 40, 20);
				}
				
				if (compareNozID(RxBuf)==false) { 
					LogUtility.getPumpALogger().debug ("1.Recv STX NozID mismatch-1.0! (Noz="+nozNo+")");
					Log.datas(RxBuf, 40, 20);

					trimInputStream("routine : 1");
					return;
				}
				if (compareBCC(RxBuf)==false) {
					recvTail_proc(NAK);
					if (retryRcvData_Cnt < 3) {
						retryRecvData(RxBuf);
						retryRcvData_Cnt++;
					}
					LogUtility.getPumpALogger().debug ("1.Recv STX BCC fail! (Noz="+nozNo+")");
					Log.datas(RxBuf, 40, 20);
					lineErrCnt++;
					return;
				}
				if (verifyData(RxBuf)==false) { 
					recvTail_proc(NAK);
					if (retryRcvData_Cnt < 3) {
						retryRecvData(RxBuf);
						retryRcvData_Cnt++;
					}
					LogUtility.getPumpALogger().debug ("1.Recv STX Data verify fail! (Noz="+nozNo+")");
					Log.datas(RxBuf, 40, 20);
					lineErrCnt++;
					return;
				}
				else {
					if (dispLevel==1 || dispLevel==2) {
						LogUtility.getPumpALogger().debug ("1.Recv STX with normal (Noz="+nozNo+")");
						Log.datas(RxBuf, 40, 20);
					}

					processRecvSTX(); // 노즐 수신데이터 처리
					recvTail_proc(ACK);
					
					retryRcvData_Cnt=0;
				}
			} 
			else if (RxBuf[1] == ACK) { // recv : ACK

				if (dispLevel>=3)
					LogUtility.getPumpALogger().debug("2.Recv ACK (Noz="+nozNo+")");

				if (compareNozID(RxBuf)==false) { // 추가(08/12/03) for SomoSelf
					LogUtility.getPumpALogger().debug ("2.Recv ACK NozID mismatch-2.0! (Noz="+nozNo+")");
					Log.datas(RxBuf, 40, 20);
					
					trimInputStream("routine : 2");
					return;
				}

				if (TxQue.isEmpty()==false) { // 송신 데이터 있으면

					//###### Send data : send working-data ######//
					TxBuf = TxQue.getFirstItem();
					TxBuf[0] = nozID;
					TxBuf[1] = STX;
					setBCC (TxBuf); // write BCC
					if(sendText(TxBuf) != true) {
						if (dispLevel>=3) {
							LogUtility.getPumpALogger().debug("2.Send STX fail! (Noz="+nozNo+")");
							Log.datas(TxBuf, TxBuf.length, 20);
						}
						lineErrCnt++;
						return;
					}

					if (dispLevel>=3) {
						LogUtility.getPumpALogger().debug("2.Send STX(TxBuf) (Noz="+nozNo+")");
						Log.datas(TxBuf, TxBuf.length, 20);
					}
					
					if (recvText(RxBuf) < 1) {
						if (dispLevel>=3)
							LogUtility.getPumpALogger().debug("2.Recv ACK fail! (Noz="+nozNo+")");
						lineErrCnt++;
						return;
					}
					
					if (RxBuf[1] == ACK) {	// recv : ACK
						if (dispLevel>=3)
							LogUtility.getPumpALogger().debug("2.Recv ACK (Noz="+nozNo+")");

						if (compareNozID(RxBuf)==false) { // 추가(08/12/03 for SomoSelf)
							LogUtility.getPumpALogger().debug("2.Recv ACK NozID mismatch-2.1! (Noz="+nozNo+")");
							Log.datas(RxBuf, 40, 20);
							
							trimInputStream("routine : 3");
							return;
						}
						
						if (sendTail_proc()==true) {
							if (dispLevel==2) {
								LogUtility.getPumpALogger().debug("2.Send STX with normal (Noz="+nozNo+")");
								Log.datas(TxBuf, TxBuf.length, 20);
							}
							//--- 송신완료후 송신데이터 제거 ---//
							TxQue.deQueue();
						} 
						else {
							LogUtility.getPumpALogger().debug("2.Send STX fail!(Fail send/recv EOT) (Noz="+nozNo+")");
							Log.datas(TxBuf, TxBuf.length, 20);
						}
					}
					else if (RxBuf[1] == NAK) {	// recv : NAK
						sendTail_proc();
						
						LogUtility.getPumpALogger().debug("2.Send STX fail!(Returned NAK) (Noz="+nozNo+")");
						Log.datas(TxBuf, TxBuf.length, 20);
					}
					else {
						LogUtility.getPumpALogger().debug("2.Send STX fail!(No returned ACK/NAK) (Noz="+nozNo+")");
						Log.datas(TxBuf, TxBuf.length, 20);
						
						trimInputStream("routine : 4");
						return;
					}
				} 
				else { // 송신 데이터 없으면 
					sendTail_proc();
					
					// 상태정보 요청 : 추가(08/12/19)
					if((nozState==0 || nozState==2) && stateReq_Cnt >= 0) {
						TxQue.enQueueNewer(s0_Buf);
						stateReq_Cnt=0;
					}
					else
						stateReq_Cnt++;
					
				}
			}
			else {
				LogUtility.getPumpALogger().debug("2.Recv ACK/STX fail!(Received unknown data) (Noz="+nozNo+")");
				Log.datas(RxBuf, 40, 20);
				
				trimInputStream("routine : 5");
				return;
			}
				
		} catch (Exception e) {
			LogUtility.getPumpALogger().debug(":::::::Exception occurr! (Noz="+nozNo+")");
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
		
		lineErrCnt=0; // Normal terminated
	}

	@Override
	public void run() {

	}

	// To be invoked when InputStream(is) has a receiving data
	@Override
	public void serialEvent(SerialPortEvent event) {


	}
}
