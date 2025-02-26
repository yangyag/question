//------------------------------------------------------//
//----- 동화프라임(Protocol=WooJoo, ROMVer=101 & 102) --//
//----- 적용 : 서초제원(천정식) ------------------------//
//------------------------------------------------------//
package com.gsc.kixxhub.device.pumpa.driverVersion;

import com.gsc.kixxhub.common.data.pump.HF_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S3_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S4_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SJ_WorkingMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.Change;
import com.gsc.kixxhub.device.pumpa.common.SerialConnectException;
import com.gsc.kixxhub.device.pumpa.driver.CommWooJoo;

public class CommWooJoo_801 extends CommWooJoo {

	protected int	s0_cnt = 0;
	
	/**
	 * @param nozNum
	 * @param romVerStr
	 */
	public CommWooJoo_801(int nozNum, String romVerStr) {
		
		super(nozNum, romVerStr);
		
		baseReadStartInterval  =10+4; // 스마트주유소 PJT 보정 
		baseWriteStartInterval =10+4; // 스마트주유소 PJT 보정 
		baseReadBuffInterval   =20+6; // 스마트주유소 PJT 보정 
		baseMinErrCnt		   =8;

		readCmdInterval    =20; // 고정
	}
	

	@Override
	public void processRecvSTX () throws SerialConnectException, Exception {
	
		//############# State check ##############//
		if (RxBuf[4]=='s' && RxBuf[5]=='0') { // 주유기 상태정보 수신

			short state = (short) (RxBuf[6] - 0x30);
			nozState =  state;
		
			pumpingStart = (nozState >= 4 ? true : pumpingStart); // 주유시작
			// nozState==0 를 nozState < 2 로 수정(2008/12/04)
			nozState = (pumpingStart==true && nozState < 2 ? 5 : nozState); // S4(주유완료) 누락시 처리
			
			/* // $$$$$$$$$$$
			if (beforeNozzleUp==false && nozState==4) { // 용인대한주유소 추가(08/08/12)
				beforeNozzleUp=true;
				nozState=3;
				pumpingStart=false;
			}*/
			
			//System.out.printf ("1. ===> m_nozLock=%s progState=%d nozState=%d\n", 
					//nozLock, progressStep, nozState);
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
				pumpingStart = (nozState >= 4 ? true : pumpingStart); // 주유시작
			}
		}
		else if (RxBuf[4]=='t' && RxBuf[5]=='0') { // 토털게이지 정보수신(주유시작 용)
		
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
				
				makeStatusInfo(nozState); // 상태정보 전송
				setNozzleBasePrice(e0_Buf);
				
				if (m_nozLock==false && nozType==1 && nozState==0 && pumpingStart==false) {
					
					if (Change.toValue(m_basePrice) > 0) {
						TxQue.enQueue(e0_Buf); // 주유허가(정액/정량 설정)
						LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 노즐다운, 주유허가(e0) : progStep=" + progressStep + 
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
					
					LogUtility.getPumpALogger().info("nozzle=" + nozNo + 
							" -> 노즐업다운, 선결제 주유취소용 주유완료전문(S4) 생성");
				}
				beforeNozzleUp=false;
				
			}
			else if (nozState==1 || nozState==3) { // 노즐업

				//makeStatusInfo(nozState); // 상태정보 전송->아래로 이동(2008/07/22)
				beforeNozzleUp=true;
				realPumpingStart=false;
				sentPumpingEndInfo=false;
							
				if (nozState==3 && sentPumpingStartInfo==false) {
					TxQue.enQueue(t0_Buf); // 토털게이지 요청(주유시작 정보 수신용)					
					makeStatusInfo(nozState); // 상태정보 전송, 위치이동(08/07/22)됨
					
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
							LogUtility.getPumpALogger().info("nozzle=" + nozNo + 
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

						LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 주유완료, 최종주유값 수신(p0) : progStep=" + 
									progressStep + " waitLastPData_Cnt=" + waitLastPData_Cnt + 
									" liter=" + S3_wm.getLiter() + " price=" + S3_wm.getPrice());
					} else
						LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 주유완료, 최종주유값 미수신(p0), 이전주유값 적용 : progStep=" + 
								progressStep + " waitLastPData_Cnt=" + waitLastPData_Cnt);
					
					TxQue.enQueue(t0_Buf); // 토털게이지 요청
					
					LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 주유완료, 토털게이지 요청(t0) : progStep=" + 
							progressStep + " nozState=" + nozState + " pumpingStart=" + pumpingStart);
					
					progressStep=4;
					makeStatusInfo(nozState); // 상태정보 전송(주유완료)
					
					// Clear preset-data
					presetType="2";
					presetLiter="0000000";
					presetBasePrice="000000";
					presetPrice="000000";

					waitLastPumpData=false;
					waitLastPData_Cnt=0;
				}
				else {
					waitLastPumpData=true;
					
					LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 주유완료, 최종주유값 수신대기 : nozState=" +
							nozState + " waitLastPData_Cnt=" + waitLastPData_Cnt);
				}
			}
			break;

		case 4 :
			// skip
			break;

		case 5 : // 주유완료후 토털게이지 수신

			byte[][] tBuf = new byte [2][buffSize];

			tBuf[0] = lastPumpingData.clone(); // 최종 주유정보
			flushBuffer(lastPumpingData);
			tBuf[1] = RxBuf.clone(); // Total guage
			
			progressStep = 3;
			nozState = 0;
			progressStep4Cnt=0;

			pumpingStart=false;
			sentPumpingStartInfo=false;
			firstPumpingData=true;
			newPumpingData=false;
			
			LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 주유완료, 토털게이지 수신(t0) : progStep=" + 
					progressStep + " nozState=" + nozState + " pumpingStart=" + pumpingStart);

			setNozzleBasePrice(e0_Buf);
			
			if (m_nozLock==false && nozType==1) { // 일반주유기
				if (Change.toValue(m_basePrice) > 0) {
					TxQue.enQueue(e0_Buf); // 주유허가(정액/정량 설정)				
					LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 토털게이지 수신, 주유허가(e0) : progStep=" + 
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
				LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 토털게이지값이 0 이거나, 미수신하여 0 으로 생성");
			
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
				LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> 주유완료(S4) 자료값=0 : Liter=" + 
						nLiter + " Price=" + nPrice);
			}
			if (sentPumpingEndInfo==false) {
				insertRecvQueue(S4_wm); // 주유완료 자료
				sentPumpingEndInfo=true;
			}
			
			if (nozType==2) // 추가(2008/11/19 for SomoSelf)
				TxQue.enQueueNewer(s0_Buf);
			
			break;
		}
	}

}
