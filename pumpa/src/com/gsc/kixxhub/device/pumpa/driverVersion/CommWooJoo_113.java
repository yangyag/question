//------------------------------------------------------//
//----- ��ȭ������(Protocol=WooJoo, ROMVer=113)---------//
//----- ���� : ���а����� ����ó�� ���������� ���� ---//
//----- * ������ -> �׽�Ʈ �̿Ϸ�, ���뿩�� �̰��� -----//
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
		
		baseReadStartInterval  =10+4; // ����Ʈ������ PJT ���� 
		baseWriteStartInterval =10+4; // ����Ʈ������ PJT ���� 
		baseReadBuffInterval   =20+6; // ����Ʈ������ PJT ���� 
		baseMinErrCnt		   =8;

		readCmdInterval    =20; // ����
	}
	

	@Override
	public void ownershipChange(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void processRecvSTX () throws SerialConnectException, Exception {
	
		//############# State check ##############//
		if (RxBuf[4]=='s' && RxBuf[5]=='0') { // ������ �������� ����

			short state = (short) (RxBuf[6] - 0x30);
			nozState =  state;
		
			//pumpingStart = (nozState==4 ? true : pumpingStart); // ��������
			//nozState = (pumpingStart==true && nozState==0 ? 5 : nozState); // S5(�����Ϸ�) ������ ó��
			pumpingStart = (nozState >= 4 ? true : pumpingStart); // ��������
			nozState = (pumpingStart==true && nozState < 2 ? 5 : nozState); // S4(�����Ϸ�) ������ ó��
			/*
			if (beforeNozzleUp==false && nozState==4) { // ���δ��������� �߰�(08/08/12)
				beforeNozzleUp=true;
				nozState=3;
				pumpingStart=false;
			}
			*/
			//System.out.printf ("1. ===> m_nozLock=%s progState=%d nozState=%d\n", 
					//nozLock, progressStep, nozState);
			if (nozState==2 && nozState==3) // �߰� (08/12/19) for ���ѱ�������
				nozState = (pumpingStart==true? 4 : nozState); 
		} 
		else if (RxBuf[4]=='p' && RxBuf[5]=='0') { // ������ ���� ����
			
			String type; 
			String szLiter = "", szPrice="";

			// ������ �ڷ� ����ó���� �߰�(08/10/10)
			waitPumpingData=false;
			waitPumpingData_Cnt=0;
			
			//--- Preset-data processing of quick-win nozzle ---//
			p0r_ds.setByteStream(RxBuf);
			szLiter = (String) p0r_ds.getValue("liter");
			szPrice = (String) p0r_ds.getValue("price");
			int liter=Change.toValue (szLiter);
			int price=Change.toValue (szPrice);
			
			if (liter==0 && price>0) {
				type = "0"; // ���׼���
				szLiter = "0000000";
			} else 
			if (liter>0 && price==0 && liter!=4000000) {
				type = "1"; // ��������
				szPrice = "000000";
			} else {
				type = "2"; // ��������
				szLiter = "0000000";
				szPrice = "000000";
			}

			if (type.equals("0") || type.equals("1")) { // Preset Data(����/���� ��������)

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
			else { // Quick-win Preset �ڷᰡ �ƴϸ� ���������� ���º���
				//if (liter>0 && price>0) // �߰����� ����
				nozState = (nozState==3 ? 4 : nozState);
				//pumpingStart = (nozState==4 ? true : pumpingStart); // ��������
				pumpingStart = (nozState >= 4 ? true : pumpingStart); // ��������
			}
		}
		else if (RxBuf[4]=='t' && RxBuf[5]=='0') { // ���а����� ��������
			/*
			if (nozState==1 || nozState==3 || nozState==4) { // �����/������ ���¿��� ����

				SJ_wm = (SJ_WorkingMessage) generateWorkingMessage(RxBuf, "SJ");
				//last_SJ_TotalGauge = SJ_wm.getTotalGauge(); // ����(2008/09/06)
				if(sentPumpingStartInfo==false) // AAAAA-�߰�(2008/09/06)
					insertRecvQueue(SJ_wm); // �������� �ڷ� �۽�
				
				//---- ���� �����ڷ� ���Ź��� �߻��� �������� ----//
				TxQue.enQueueNewer(p0_Buf); // �����ڷ� ��û-���δ��������� �߰�(08/08/12)
				
				sentPumpingStartInfo=true;
				waitTotalGuageFor_SJ=false;
				waitGaugeForSJ_Cnt = 0;
			}
			else if (progressStep==4) { // �����Ϸ��� ����
				progressStep = 5;
			} 
			else 
				insertRecvQueue(generateWorkingMessage(RxBuf, "S5")); // ���а����� �ڷ� �۽�
			*/

			insertRecvQueue(generateWorkingMessage(RxBuf, "S5")); // ���а����� �ڷ� �۽�
		}
		else if (RxBuf[4]=='q') { // �������̻����� �߰�(08/12/07 for SomoSelf)
			insertRecvQueue(generateWorkingMessage(RxBuf, "Q0")); 
		}

		//LogUtility.getPumpALogger().debug("@STX Start : nozzle=" + nozStr + " ProgStep=" +
				//progressStep + " nozState=" + nozState + " pumpingStart=" + pumpingStart);
		
		
		//############# STX Process ##############//
		switch (progressStep) {
		case 3 :
			if (nozState==0 || nozState==2) { // ����ٿ�

				sentPumpingStartInfo=false;
				waitTotalGuageFor_SJ=false; // �߰�->��������(SJ) �ݺ����� �����ذ�(08/08/11)
				waitGaugeForSJ_Cnt=0; // �߰�(2008/09/06)
				
				if (sentNozDownInfo==false) { // �߰� (08/12/19)
					makeStatusInfo(nozState); // �������� ����
					sentNozDownInfo=true;
				}
				
				setNozzleBasePrice(e0_Buf);
				
				if (m_nozLock==false && nozType==1 && nozState==0 && pumpingStart==false) {
					
					if (Change.toValue(m_basePrice) > 0) {
						TxQue.enQueueNewer(e0_Buf); // �����㰡(����/���� ����)
						LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> ����ٿ�, �����㰡(e0) : progStep=" + progressStep + 
								" nozState=" + nozState + " m_basePrice=" + m_basePrice);
						//Log.datas(e0_Buf, e0_Buf.length, 20);
					}
				}
				
				// �������/���� �������� ó��
				if (m_nozLock==true)
					makeStatusInfo(656); // �������
				else if (m_recvedNozLock==true) {
					makeStatusInfo(657); // �������
					m_recvedNozLock=false;
				}

				// ������ٿ�� ��=0 �����Ϸ� �ڷ� ���� -> POS���� ������ ������ ���ó����
				// POS ������ ���δ� ��⿡�� �Ǵ�
				if (beforeNozzleUp==true && realPumpingStart==false && // ���� �����Ϸ��ڷᰡ �ƴϸ�
						sentPumpingEndInfo==false && nozState==2) { // nozState==2 �߰�(08/12/03) for SomoSelf
					
					S4_wm = new S4_WorkingMessage();
					S4_wm.setNozzleNo(Change.toString("%02d",nozNo));
					S4_wm.setLiter("0000000");
					S4_wm.setBasePrice(m_basePrice);
					S4_wm.setLiter("00000000");
					//S4_wm.setTotalGauge(last_SJ_TotalGauge);
					S4_wm.setTotalGauge("0000000000"); // ����(2008/09/06)
					
					insertRecvQueue(S4_wm); // �����Ϸ� �ڷ�
					sentPumpingEndInfo=true;
					
					LogUtility.getPumpALogger().debug("nozzle=" + nozNo + 
							" -> ������ٿ�, ������ ������ҿ� �����Ϸ�����(S4) ����");
				}
				
				beforeNozzleUp=false;
				
			}
			else if (nozState==1 || nozState==3) { // �����

				//makeStatusInfo(nozState); // �������� ����->�Ʒ��� �̵�(2008/07/22)
				beforeNozzleUp=true;
				realPumpingStart=false;
				sentPumpingEndInfo=false;
				sentNozDownInfo=false;

				if (nozState==3 && sentPumpingStartInfo==false) {
					//TxQue.enQueueNewer(t0_Buf); // ���а����� ��û(�������� ���� ���ſ�)					
					makeStatusInfo(nozState); // �������� ����, ��ġ�̵�(08/07/22)��
					
					
					
					byte[] byTotalGauge = new byte[18];
					processTotalGauge(byTotalGauge);
					
					SJ_wm = (SJ_WorkingMessage) generateWorkingMessage(byTotalGauge, "SJ");
					//last_SJ_TotalGauge = SJ_wm.getTotalGauge(); // ����(2008/09/06)
					if(sentPumpingStartInfo==false) // AAAAA-�߰�(2008/09/06)
						insertRecvQueue(SJ_wm); // �������� �ڷ� �۽�
					
					//---- ���� �����ڷ� ���Ź��� �߻��� �������� ----//
					TxQue.enQueueNewer(p0_Buf); // �����ڷ� ��û-���δ��������� �߰�(08/08/12)
					
					
					
					
					sentPumpingStartInfo=true;
					waitTotalGuageFor_SJ=false;
					waitGaugeForSJ_Cnt = 0;
					
					waitTotalGuageFor_SJ=true;
				}
				// �߰�(08/11/29 for SomoSelf)
				else if (nozType==2)
					makeStatusInfo(nozState); // �������� ����(�����)

				if (nozType!=2) // �߰�(08/11/29 for SomoSelf) - ���������� �ƴϸ�
					TxQue.enQueueNewer(s0_Buf); // �Ҹ��� s04�� �����ڷ� ����ó��, �׽�Ʈ�� #6 (2008/07/22)
				
			}
			else if (nozState == 4) { // ������

				if (RxBuf[4]=='p' && RxBuf[5]=='0') {

					S3_wm = (S3_WorkingMessage) generateWorkingMessage(RxBuf, "S3");
					int nPrice=0, nLiter=0;
					if (S3_wm != null) {
						nPrice = Change.toValue(S3_wm.getPrice());
						nLiter = Change.toValue(S3_wm.getLiter());
					}
					
					// ���� ������ ���Ž� Skip �� ���� �߰�(08/08/29)
					if (nPrice > 0 && nLiter > 0) {			
						if (newPumpingData==false && nLiter==m_nLiter) {
							LogUtility.getPumpALogger().debug("nozzle=" + nozNo + 
									" -> ������(p0)�� ���� �������� �����մϴ�. : ������(x.3)=" + 
									nLiter + " ���� ������(x.3)=" + m_nLiter);
						}
						else {
							lastPumpingData = RxBuf.clone(); // ���� �������� ����
							realPumpingStart=true;
							newPumpingData=true;
						}
					}

					if (presetDataFlag==true) 
						presetDataFlag=false; // Preset data �̸� skip
					else {
						if (nPrice > 0 && nLiter > 0) {
							if (firstPumpingData==true) {
								makeStatusInfo(nozState); // �������� ����(������)
								firstPumpingData=false;
							}
							if (newPumpingData==true) // �߰�(08/08/29)
								insertRecvQueue(S3_wm); // ������ �ڷ� ���� �۽�
						}
					}
				}
				sentPumpingEndInfo=false; 
				
				if (romVer.equals("002")) { // �Ҹ� ��Ʈ�κ���
					
					if (p0_cnt%2==0) {
						TxQue.enQueueNewer(p0_Buf); // �����ڷ� ��û
						//waitPumpingData=true; // ������ �ڷ� ����ó���� �߰�(08/10/10)->AAA��?
						p0_cnt=0;
					}
					else {
						TxQue.enQueueNewer(s0_Buf); // �������� ��û
					}
						
					p0_cnt++;
				} 
				else {
					TxQue.enQueueNewer(p0_Buf); // �����ڷ� ��û
					//waitPumpingData=true; // ������ �ڷ� ����ó���� �߰�(08/10/10)->AAA��?
				}
				
				waitPumpingData=true; // AAA (08/10/18)
				sentNozDownInfo=false;
				
			}
			else if (nozState == 5) { // �����Ϸ�
							
				if ((RxBuf[4]=='p' && RxBuf[5]=='0') || waitLastPData_Cnt>=MAX_LAST_PDATA) {
					
					if ((RxBuf[4]=='p' && RxBuf[5]=='0')) {
						S3_wm = (S3_WorkingMessage) generateWorkingMessage(RxBuf, "S3");
						int nPrice=0, nLiter=0;
						if (S3_wm != null) {
							nPrice = Change.toValue(S3_wm.getPrice());
							nLiter = Change.toValue(S3_wm.getLiter());
						}
						if (nPrice > 0 || nLiter > 0) // ����(08/11/24) : && -> ||
							lastPumpingData = RxBuf.clone(); // ���� �������� ����

						LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> �����Ϸ�, ���������� ����(p0) : progStep=" + 
									progressStep + " waitLastPData_Cnt=" + waitLastPData_Cnt + 
									" liter=" + S3_wm.getLiter() + " price=" + S3_wm.getPrice());
					} else
						LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> �����Ϸ�, ���������� �̼���(p0), ���������� ���� : progStep=" + 
								progressStep + " waitLastPData_Cnt=" + waitLastPData_Cnt);
					
					//TxQue.enQueueNewer(t0_Buf); // ���а����� ��û
					
					
					
					
					
					byte[] byTotalGauge = new byte[18];
					processTotalGauge(byTotalGauge);
					
					byte[][] tBuf = new byte [2][buffSize];

					tBuf[0] = lastPumpingData.clone(); // ���� ��������
					flushBuffer(lastPumpingData);
					tBuf[1] = byTotalGauge.clone(); // Total gauge
					
					progressStep = 3;
					nozState = 0;
					progressStep4Cnt=0;

					pumpingStart=false;
					sentPumpingStartInfo=false;
					firstPumpingData=true;
					newPumpingData=false;
					
					LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> �����Ϸ�, ���а����� ����(t0) : progStep=" + 
							progressStep + " nozState=" + nozState + " pumpingStart=" + pumpingStart);

					setNozzleBasePrice(e0_Buf);
					
					if (m_nozLock==false && nozType==1) { // �Ϲ�������
						if (Change.toValue(m_basePrice) > 0) {
							TxQue.enQueueNewer(e0_Buf); // �����㰡(����/���� ����)				
							LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> ���а����� ����, �����㰡(e0) : progStep=" + 
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
						LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> ���а��������� 0 �̰ų�, �̼����Ͽ� 0 ���� ����");
					
					/* ---����ó�� ���뿩�� �̰��� -> ����
					int nGauge = Change.toValue(S4_wm.getTotalGauge());
					if (nGauge==0) {
						S4_wm.setTotalGauge(addTotalGauge(last_SJ_TotalGauge, S4_wm.getLiter()));
						LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> �����Ϸ�(S4) ���а�������=0, �������� ����ó��");
					}
					else
						last_S4_TotalGauge = S4_wm.getTotalGauge();
					*/
					
					 // ����(08/09/10)
					if (nLiter <= 0 || nPrice <= 0) {
						S4_wm.setBasePrice(m_basePrice);
						LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> �����Ϸ�(S4) �ڷᰪ=0 : Liter=" + 
								nLiter + " Price=" + nPrice);
					}
					if (sentPumpingEndInfo==false) {
						insertRecvQueue(S4_wm); // �����Ϸ� �ڷ�
						sentPumpingEndInfo=true;
					}
					
					if (nozType==2) // �߰�(2008/11/19 for SomoSelf)
						TxQue.enQueueNewer(s0_Buf);
					
					
					
					
					
					
					//LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> �����Ϸ�, ���а����� ��û(t0) : progStep=" + 
							//progressStep + " nozState=" + nozState + " pumpingStart=" + pumpingStart);
					
					//progressStep=4;
					makeStatusInfo(nozState); // �������� ����(�����Ϸ�)
					
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
					
					LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> �����Ϸ�, ���������� ���Ŵ�� : nozState=" +
							nozState + " waitLastPData_Cnt=" + waitLastPData_Cnt);
				}
			}
			break;

		case 4 :
			// skip
			break;

		case 5 : // �����Ϸ��� ���а����� ����		
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
		
		// ȸ���ҷ� ó��
		boolean	occurLineErr = (lineErrCnt >= minErrCnt);
			
		if (occurLineErr) { // Check line error

			if (issueLineErr==true) {
				makeLineError(); // ȸ���ҷ�
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
		
		// ȸ���ҷ� ������ ó��
		if (firstRequest==true) {
			firstRequest=false;
			TxQue.enQueueNewer(s0_Buf); // ����������û
		}
		if (m_statusCode==601) { // ȸ���ҷ�
			if (lineCommCheckCnt%(minErrCnt+5)==0) {
				lineCommCheckCnt=0;
				TxQue.enQueueNewer(s0_Buf); // ����������û
			}
			lineCommCheckCnt++;
		}
		/*
		// ��������(SJ) ���а����� �̼��� ó��
		if (waitTotalGuageFor_SJ==true) {
			if (waitGaugeForSJ_Cnt >= MAX_GAUGE_SJ) {
				SJ_wm = new SJ_WorkingMessage();
				SJ_wm.setNozzleNo(Change.toString("%02d",nozNo));
				//SJ_wm.setTotalGauge(last_S4_TotalGauge);
				SJ_wm.setTotalGauge("0000000000");
				insertRecvQueue(SJ_wm); // �������� �ڷ� �۽�

				waitGaugeForSJ_Cnt=0;
				sentPumpingStartInfo=true;
				waitTotalGuageFor_SJ=false; // ����(08/08/11)

				LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> �������� ���а�����(t0) �̼���, ��������(SJ) ���� ����");
			}
			else
				waitGaugeForSJ_Cnt++;
			
			LogUtility.getPumpALogger().debug("waitGaugeForSJ_Cnt="+waitGaugeForSJ_Cnt);
		}*/
		
		// ������ �ڷ�(S3) �̼��� ó��
		if (nozState==4 && waitPumpingData==true) {
			//LogUtility.getPumpALogger().debug("waitPumpingData_Cnt=" + waitPumpingData_Cnt);
			if (waitPumpingData_Cnt >= MAX_WAIT_PDATA) {
				TxQue.enQueueNewer(p0_Buf);				
				
				LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> ������ �ڷ�(S3)�� �̼���, �����ڷ� ��û(p0), waitPumpingData_Cnt="+waitPumpingData_Cnt);
				waitPumpingData_Cnt=0;
			}
			else
				waitPumpingData_Cnt++;
		}
		
		// �����Ϸ�(S4) ���������� �̼��� ó��
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
		// �����Ϸ�(S4) ���а����� �̼��� ó��
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

					processRecvSTX(); // ���� ���ŵ����� ó��
					recvTail_proc(ACK);
					
					retryRcvData_Cnt=0;
				}
			} 
			else if (RxBuf[1] == ACK) { // recv : ACK

				if (dispLevel>=3)
					LogUtility.getPumpALogger().debug("2.Recv ACK (Noz="+nozNo+")");

				if (compareNozID(RxBuf)==false) { // �߰�(08/12/03) for SomoSelf
					LogUtility.getPumpALogger().debug ("2.Recv ACK NozID mismatch-2.0! (Noz="+nozNo+")");
					Log.datas(RxBuf, 40, 20);
					
					trimInputStream("routine : 2");
					return;
				}

				if (TxQue.isEmpty()==false) { // �۽� ������ ������

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

						if (compareNozID(RxBuf)==false) { // �߰�(08/12/03 for SomoSelf)
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
							//--- �۽ſϷ��� �۽ŵ����� ���� ---//
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
				else { // �۽� ������ ������ 
					sendTail_proc();
					
					// �������� ��û : �߰�(08/12/19)
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
