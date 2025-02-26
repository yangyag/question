//------------------------------------------------------//
//----- ��ȭ������(Protocol=WooJoo, ROMVer=101 & 102) --//
//----- ���� : ��������(õ����) ------------------------//
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

public class CommWooJoo_101 extends CommWooJoo {

	protected int	s0_cnt = 0;
	
	/**
	 * @param nozNum
	 * @param romVerStr
	 */
	public CommWooJoo_101(int nozNum, String romVerStr) {
		
		super(nozNum, romVerStr);
		
		readStartInterval  =10+4; // ����Ʈ������ PJT ����
		writeStartInterval =10+4; // ����Ʈ������ PJT ����
		readBuffInterval   =30+6; // ����Ʈ������ PJT ����
		minErrCnt		   =8;

		readCmdInterval    =30; // 20 -> 30 ���� ����(08/12/05)
		
		MAX_StateReq_Cnt   =20; // �������� ��û�ֱ�(0=nothing)
	}
	
	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.pump.driver.CommWooJoo#processRecvSTX()
	 */
	@Override
	public void processRecvSTX () throws SerialConnectException, Exception {
		
		//############# State check ##############//
		if (RxBuf[4]=='s' && RxBuf[5]=='0') { // ������ �������� ����

			short state = (short) (RxBuf[6] - 0x30);
			
			switch (state) {
				case 0 : // ����ٿ�(�㰡��)
				//case 1 : // �����(�㰡��) // ����(09/03/12)
				//case 2 : // ����ٿ�(�㰡��)
					if (nozState==4 || (nozState==5 && pumpingStart==true))
						nozState=5;
					else
						nozState=state;
										
					break;
				case 2 : // ����ٿ�(�㰡��) - ��� ���¿�û�� s04 > s02 ���ŵǾ� �̻����� ����-�߰�(09/03/19)		
				case 3 : // �����(�㰡��) - ���������� nozState=3���� ���°��� ���� ����
					if (nozState==4 && pumpingStart==true)
						nozState=4;
					else
						nozState=state;
					
					break;
				default :
					nozState=state;
			}
		} 
		else if (RxBuf[4]=='p' && RxBuf[5]=='0') { // ������ ���� ����
			
			String type; 
			String szLiter = "", szPrice="", szBasePrice="";

			// ������ �ڷ� ����ó���� �߰�(08/10/10)
			waitPumpingData=false;
			waitPumpingData_Cnt=0;
			
			//--- Preset-data processing of quick-win nozzle ---//
			p0r_ds.setByteStream(RxBuf);
			szLiter 	= (String) p0r_ds.getValue("liter");
			szBasePrice = (String) p0r_ds.getValue("basePrice");
			szPrice 	= (String) p0r_ds.getValue("price");
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

			if (type.equals("0") || type.equals("1")) { // ���� �Ǵ� ���� ��������

				if (sentPresetData==false) {
					HF_wm = new HF_WorkingMessage();
					HF_wm.setNozzleNo(Change.toString("%02d", nozNo));
					HF_wm.setType(type);
					HF_wm.setLiter(szLiter);
					szBasePrice = Change.toValue(szBasePrice) == 0 ? 
							(String) e0_ds.getValue("basePrice") : szBasePrice;
					HF_wm.setBasePrice(szBasePrice + "00");
					HF_wm.setPrice(szPrice);
				
					insertRecvQueue(HF_wm);
					
					presetDataFlag=true;
					sentPresetData=true;
					presetType=type;
					presetLiter=szLiter;
					presetBasePrice=szBasePrice;
					presetPrice=szPrice;
				}
			}
		}
		else if (RxBuf[4]=='t' && RxBuf[5]=='0') { // ���а����� ���� ����
			
			if (nozState==1 || nozState==3 || nozState==4) { // ��������¿��� ����
				
				SJ_wm = (SJ_WorkingMessage) generateWorkingMessage(RxBuf, "SJ");
				//last_SJ_TotalGauge = SJ_wm.getTotalGauge();
				if(sentPumpingStartInfo==false) // AAAAA-�߰�(2008/09/06)
					insertRecvQueue(SJ_wm); // �������� �ڷ� �۽�	

				//--- ���� �����ڷ� ���Ź��� �߻��� �������� 
				TxQue.enQueueNewer(p0_Buf); // �����ڷ� ��û-�������ڷ��(08/08/12)
				
				sentPumpingStartInfo=true;
				waitTotalGuageFor_SJ=false;
				waitGaugeForSJ_Cnt = 0;
			}
			else if (progressStep==4) { // �����Ϸ��� ����
				progressStep = 5;
			} 
			else 
				insertRecvQueue(generateWorkingMessage(RxBuf, "S5")); // ���а����� �ڷ� �۽�
		}
		
		//############# STX Process ##############//
		switch (progressStep) {
		case 3 :
			if (nozState==0 || nozState==2) { // ����ٿ�

				flushBuffer(lastPumpingData);
				sentPumpingStartInfo=false;
				waitTotalGuageFor_SJ=false; // �߰�->��������(SJ) �ݺ����� �����ذ�(08/08/11)
				waitGaugeForSJ_Cnt=0;

				if (sentNozDownInfo==false) { // �߰� (08/12/19)
					makeStatusInfo(nozState); // �������� ����
					sentNozDownInfo=true;
				}
				
				setNozzleBasePrice(e0_Buf);
				
				// �߰� (09/01/13)
				if (m_nozLock==false && nozType==1 && nozState==0 && pumpingStart==false &&
						romVer.equals("104")==false) { // �Ϲ�������
					if (Change.toValue(m_basePrice) > 0) {
						TxQue.enQueueNewer(e0_Buf); // �����㰡(����/���� ����)
						LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> ����ٿ�, �����㰡(e0) : progStep=" + 
								progressStep + " nozState=" + nozState + " m_basePrice=" + m_basePrice);
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
				if (nozState==0 && beforeNozzleUp==true && realPumpingStart==false && // ���� �����Ϸ��ڷᰡ �ƴϸ�
						sentPumpingEndInfo==false) { // nozState==0 �߰�(09/06/29)-���������͹̳�
					
					S4_wm = new S4_WorkingMessage();
					S4_wm.setNozzleNo(Change.toString("%02d",nozNo));
					S4_wm.setLiter("0000000");
					S4_wm.setBasePrice(m_basePrice);
					S4_wm.setPrice("00000000");
					S4_wm.setTotalGauge("0000000000");
					
					insertRecvQueue(S4_wm); // �����Ϸ� �ڷ�

					sentPresetData=false;
					sentPumpingEndInfo=true;
					waitTotalGuageFor_SJ=false; // �߰�->��ȭ ������ٿ�� SJ�� S4 �������� ó��(09/01/07)

					LogUtility.getPumpALogger().info("nozzle=" + nozNo + 
						" -> ������ٿ�, ������ ������ҿ� �����Ϸ�����(S4) ����");
				}
				
				beforeNozzleUp=false;
			}
			else if (nozState==1 || nozState==3) { // �����

				flushBuffer(lastPumpingData);
				//makeStatusInfo(nozState); // �������� ����
				beforeNozzleUp=true;
				realPumpingStart=false;
				sentPumpingEndInfo=false;
				sentNozDownInfo=false;
				
				if (sentPumpingStartInfo==false) {
					TxQue.enQueue(t0_Buf); // ���а����� ��û(�������� ���� ���ſ�)
					makeStatusInfo(nozState); // �������� ����
					
					waitTotalGuageFor_SJ=true;
				}
				
				// Appended for ��ȭ������-õ��(�������� #1,2,3,4)-2008.04.16
				if (m_nozLock==false && nozType==1 && nozState==1) { // �Ϲ�������
					if (Change.toValue(m_basePrice) > 0) {
						TxQue.enQueueNewer(e0_Buf); // �����㰡(����/���� ����)
						LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> �����, �����㰡(e0) : progStep=" + 
								progressStep + " nozState=" + nozState + " m_basePrice=" + m_basePrice);
						//Log.datas(e0_Buf, e0_Buf.length, 20);
					}
				}
			}
			else if (nozState == 4) { // ������

				if (pumpingStart==false) {
					flushBuffer(lastPumpingData);
					pumpingStart=true;
				}
				
				if (RxBuf[4]=='p' && RxBuf[5]=='0') {

					S3_wm = (S3_WorkingMessage) generateWorkingMessage(RxBuf, "S3");
					int nPrice=0, nLiter=0;
					if (S3_wm != null) {
						nPrice = Change.toValue(S3_wm.getPrice());
						nLiter = Change.toValue(S3_wm.getLiter());
					}
					
					// ���� ������ ���Ž� Skip�� ���� �߰�(08/08/29)
					if (nPrice > 0 && nLiter > 0) {
						if (newPumpingData==false && nLiter==m_nLiter) {
							LogUtility.getPumpALogger().info("nozzle=" + nozNo + 
									" -> ������(p0)�� ���� �������� �����մϴ�. : ������=" + 
									nLiter + " ���� ������=" + m_nLiter);
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
				
				if (romVer.equals("102") || romVer.equals("103") || romVer.equals("104")) {
					// ���������� (version 102)
					if (s0_cnt%3==0) {
						TxQue.enQueueNewer(s0_Buf); // �������� ��û
						s0_cnt=0;
					}
					s0_cnt++;
				}
				
				TxQue.enQueueNewer(p0_Buf); // �����ڷ� ��û
				waitPumpingData=true; // ������ �ڷ� ����ó���� �߰�(08/10/10)
				sentNozDownInfo=false;
				
			}
			else if (nozState == 5) { // �����Ϸ�

				if (pumpingStart==false) {
					flushBuffer(lastPumpingData);
					pumpingStart=true;
				}

				if ((RxBuf[4]=='p' && RxBuf[5]=='0') || waitLastPData_Cnt>=MAX_LAST_PDATA) {
					
					if (RxBuf[4]=='p' && RxBuf[5]=='0') {
						S3_wm = (S3_WorkingMessage) generateWorkingMessage(RxBuf, "S3");
						int nPrice=0, nLiter=0;
						if (S3_wm != null) {
							nPrice = Change.toValue(S3_wm.getPrice());
							nLiter = Change.toValue(S3_wm.getLiter());
						}
						if (nPrice > 0 || nLiter > 0) // ����(08/11/24) : && -> ||
							lastPumpingData = RxBuf.clone(); // ���� �������� ����
						
						if (romVer.equals("103")) {
							lastPumpingData = RxBuf.clone(); // ���� �������� ����
						}

						LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> �����Ϸ�, ���������� ����(p0) : progStep=" + 
								progressStep + " waitLastPData_Cnt=" + waitLastPData_Cnt + 
								" liter=" + S3_wm.getLiter() + " price=" + S3_wm.getPrice());
					} else
						LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> �����Ϸ�, ���������� �̼���(p0), ���������� ���� : progStep=" + 
								progressStep + " waitLastPData_Cnt=" + waitLastPData_Cnt);
	
					TxQue.enQueue(t0_Buf); // ���а����� ��û

					LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> �����Ϸ�, ���а����� ��û(t0) : progStep=" + 
							progressStep + " nozState=" + nozState + " pumpingStart=" + pumpingStart);
	
					progressStep=4;
					makeStatusInfo(nozState); // �������� ����
					
					// Clear preset-data
					presetType="2";
					presetLiter="0000000";
					presetBasePrice="000000";
					presetPrice="000000";
					sentPresetData=false;

					waitLastPumpData=false;
					waitLastPData_Cnt=0;
					sentNozDownInfo=false;
				}			
				else {
					waitLastPumpData=true;
					TxQue.enQueueNewer(p0_Buf); // �߰�(09/02/03)
					
					LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> �����Ϸ�, ���������� ���Ŵ�� : nozState=" +
							nozState + " waitLastPData_Cnt=" + waitLastPData_Cnt);
				}
			}
			break;
			
		case 4 :
			
			break;
			
		case 5 : // �����Ϸ��� ���а����� ����

			byte[][] tBuf = new byte [2][buffSize];
			
			tBuf[0] = lastPumpingData.clone(); // ���� ��������
			flushBuffer(lastPumpingData);
			tBuf[1] = RxBuf.clone(); // Total guage
			
			progressStep = 3;
			nozState = 0;
			progressStep4Cnt=0;

			pumpingStart=false;
			sentPumpingStartInfo=false;
			firstPumpingData=true;
			newPumpingData=false;

			LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> �����Ϸ�, ���а����� ����(t0) : progStep=" + 
					progressStep + " nozState=" + nozState + " pumpingStart=" + pumpingStart);

			setNozzleBasePrice(e0_Buf);
			
			if (m_nozLock==false && nozType==1 && romVer.equals("104")==false) { // �Ϲ�������
				
				if (Change.toValue(m_basePrice) > 0) {
					TxQue.enQueueNewer(e0_Buf); // �����㰡(����/���� ����)
					LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> ���а����� ����, �����㰡(e0) : progStep=" + 
							progressStep + " nozState=" + nozState + " m_basePrice=" + m_basePrice);
					//Log.datas(e0_Buf, e0_Buf.length, 20);
				}
			}

			S4_wm = (S4_WorkingMessage) generateWorkingMessage(tBuf, "S4");
			S4_wm.setNozzleNo(Change.toString("%02d",nozNo)); // append
			int nPrice=0, nLiter=0; long nGauge=0;
			if (S4_wm != null) {
				nPrice = Change.toValue(S4_wm.getPrice());
				nLiter = Change.toValue(S4_wm.getLiter());
				nGauge = Change.toLongValue(S4_wm.getTotalGauge());
			}
			m_nPrice = nPrice;
			m_nLiter = nLiter;
			
			if (nGauge==0)
				LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> ���а��������� 0 �̰ų�, �̼����Ͽ� 0 ���� ����");
	
			/* ---����ó�� ���뿩�� �̰��� -> ����
			long nGauge = Change.toValue(S4_wm.getTotalGauge());
			if (nGauge==0) {
				S4_wm.setTotalGauge(addTotalGauge(last_SJ_TotalGauge, S4_wm.getLiter()));
				LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> �����Ϸ�(S4) ���а�������=0, �������� ����ó��");
			}
			else
				last_S4_TotalGauge = S4_wm.getTotalGauge();
			*/
			/*
			// �����ݾ� ���̽� ����ó��(2009/07/20)
			int nRespectPrice = (int) (Change.toValue(m_basePrice.substring(0,4)) * (nLiter/1000.0));
			if (nozType==1 && Math.abs(nRespectPrice - nPrice) >= (nRespectPrice / 1000.0)) { // 0.1% �̻� ���̽� ������
				String sRespectPrice = Change.toString("%08d", nRespectPrice);
				m_nPrice = Change.toValue(sRespectPrice);
				S4_wm.setPrice(sRespectPrice);
				LogUtility.getPumpALogger().debug("nozzle=" + nozNo + " -> �����Ϸ�(S4) �ݾ׺���. ���Ű�=" + 
						nPrice + " ������=" + m_nPrice);
			}
			*/
			 // ����(08/09/10)
			if (nLiter <= 0 || nPrice <= 0) {
				S4_wm.setBasePrice(m_basePrice);
				LogUtility.getPumpALogger().info("nozzle=" + nozNo + " -> �����Ϸ�(S4) �ڷᰪ=0 : Liter=" + 
						nLiter + " Price=" + nPrice);
			}
			if (sentPumpingEndInfo==false) {
				insertRecvQueue(S4_wm); // �����Ϸ� �ڷ�
				sentPumpingEndInfo=true;
				waitTotalGuageFor_SJ=false; // �߰�->��ȭ ������ٿ�� SJ�� S4 �������� ó��(09/01/07)
			}

			break;
		}
	}
}
