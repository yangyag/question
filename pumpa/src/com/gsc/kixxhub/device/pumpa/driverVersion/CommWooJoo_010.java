//------------------������---------------------------//
//----- �ѱ�ENE-������(Protocol=WooJoo, ROMVer=061) ---//
//----- ���� ������ ROMVer : 3.0, 4.0 -----------------//
//-----------------------------------------------------//
package com.gsc.kixxhub.device.pumpa.driverVersion;

import java.io.IOException;

import com.gsc.kixxhub.common.data.pump.HF_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S3_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S4_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SJ_WorkingMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.Change;
import com.gsc.kixxhub.device.pumpa.common.SerialConnectException;
import com.gsc.kixxhub.device.pumpa.common.Show;
import com.gsc.kixxhub.device.pumpa.driver.CommWooJoo;

public class CommWooJoo_010 extends CommWooJoo {
	
	/**
	 * @param nozNum
	 * @param romVerStr
	 */
	public CommWooJoo_010(int nozNum, String romVerStr) {
		super(nozNum, romVerStr);
	}
	
	
	/* (non-Javadoc)
	 * @see com.gsc.kixxhub.adaptor.pump.driver.CommWooJoo#processRecvSTX()
	 */
	@Override
	public void processRecvSTX () throws SerialConnectException, Exception {
		
		//############# State check ##############//
		if (RxBuf[4]=='s' && RxBuf[5]=='0') { // ������ �������� ����

			short state = (short) (RxBuf[6] - 0x30);
			nozState =  state;
		
			pumpingStart = (nozState==4 ? true : pumpingStart); // ��������
			nozState = (pumpingStart==true && nozState==0 ? 5 : nozState); // S5(�����Ϸ�) ������ ó��

			//System.out.printf ("1. ===> m_nozLock=%s progState=%d nozState=%d\n", 
					//nozLock, progressStep, nozState);
		} 
		else if (RxBuf[4]=='p' && RxBuf[5]=='0') { // ������ ���� ����
			
			String type; 
			String szLiter = "", szPrice="";

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
			else {
				nozState = (nozState==3 ? 4 : nozState);
				pumpingStart = (nozState==4 ? true : pumpingStart); // ��������
			}
		}
		else if (RxBuf[4]=='t' && RxBuf[5]=='0') { // ���а����� ��������(�������� ��)
			
			if (nozState==1 || nozState==3 || nozState==4) { // ��������¿��� ����

				SJ_wm = (SJ_WorkingMessage) generateWorkingMessage(RxBuf, "SJ");
				//last_SJ_TotalGauge = SJ_wm.getTotalGauge();
				insertRecvQueue(SJ_wm); // �������� �ڷ� �۽�
				
				sentPumpingStartInfo=true;
				
				//TxQue.enQueue(p0_Buf);// append
			}
			else if (progressStep==4) { // �����Ϸ��� ����
				progressStep = 5;
			} 
			else 
				insertRecvQueue(generateWorkingMessage(RxBuf, "S5")); // ���а����� �ڷ� �۽�
		}

		//LogUtility.getPumpALogger().debug("@STX Start : nozzle=" + nozStr + " ProgStep=" +
				//progressStep + " nozState=" + nozState + " pumpingStart=" + pumpingStart);
		
		
		//############# STX Process ##############//
		switch (progressStep) {
		case 3 :
			if (nozState==0 || nozState==2) { // ����ٿ�

				sentPumpingStartInfo=false;
				makeStatusInfo(nozState); // �������� ����
				
				if (m_nozLock==false && nozType==1 && nozState==0 && pumpingStart==false) {
					if (Change.toValue(m_basePrice) > 0) {
						TxQue.enQueue(e0_Buf); // �����㰡(����/���� ����)
						LogUtility.getPumpALogger().debug("����ٿ�, �����㰡(e0) : progStep=" + progressStep + 
								" nozState=" + nozState + " pumpingStart=" + pumpingStart);
					}
				}
				
				// �������/���� �������� ó��
				if (m_nozLock==true)
					makeStatusInfo(656); // �������
				else if (m_recvedNozLock==true) {
					makeStatusInfo(657); // �������
					m_recvedNozLock=false;
				}

				// POS���� ������ ������ ���ó��(�������� 0 �� �����Ϸ� �ڷ�)
				if (beforeNozzleUp==true && realPumpingStart==false) { // ���� �����Ϸ��ڷᰡ �ƴϸ�
					S4_wm = new S4_WorkingMessage();
					S4_wm.setNozzleNo(Change.toString("%02d",nozNo));
					S4_wm.setLiter("0000000");
					S4_wm.setBasePrice(m_basePrice);
					S4_wm.setLiter("00000000");
					//S4_wm.setTotalGauge(last_SJ_TotalGauge);
					insertRecvQueue(S4_wm); // �����Ϸ� �ڷ�
				}
				beforeNozzleUp=false;
				
			}
			else if (nozState==1 || nozState==3) { // �����

				//makeStatusInfo(nozState); // �������� ����->�Ʒ��� �̵�(2008/07/22)
				beforeNozzleUp=true;
				realPumpingStart=false;
				
				if (nozState==3 && sentPumpingStartInfo==false) {
					TxQue.enQueue(t0_Buf); // ���а����� ��û(�������� ���� ���ſ�)
					
					makeStatusInfo(nozState); // �������� ����->��ġ�̵�(2008/07/22)
					sentPumpingStartInfo=true;
				}

				// �Ҹ��� s04�� �����ڷ� ����ó��, �׽�Ʈ�� #6 (2008/07/22)
				TxQue.enQueueNewer(s0_Buf); 
				
			}
			else if (nozState == 4) { // ������
	
				if (RxBuf[4]=='p' && RxBuf[5]=='0') {

					S3_wm = (S3_WorkingMessage) generateWorkingMessage(RxBuf, "S3");
					int nPrice = Change.toValue(S3_wm.getPrice());
					int nLiter = Change.toValue(S3_wm.getLiter());

					if (nPrice > 0 && nLiter > 0) {
						lastPumpingData = RxBuf.clone(); // ���� �������� ����
						realPumpingStart=true;
					}

					if (presetDataFlag==true) 
						presetDataFlag=false; // skip
					else {
						if (nPrice > 0 && nLiter > 0) {
							if (firstPumpingData==true) {
								makeStatusInfo(nozState); // �������� ����(������)
								firstPumpingData=false;
							}
							insertRecvQueue(S3_wm); // ������ �ڷ� ���� �۽�
						}
					}
				}
				
				TxQue.enQueueNewer(p0_Buf); // �����ڷ� ��û
				
			}
			else if (nozState == 5) { // �����Ϸ�

				if (RxBuf[4]=='p' && RxBuf[5]=='0') {
					
					S3_wm = (S3_WorkingMessage) generateWorkingMessage(RxBuf, "S3");
					int nPrice = Change.toValue(S3_wm.getPrice());
					int nLiter = Change.toValue(S3_wm.getLiter());
					if (nPrice > 0 && nLiter > 0)
						lastPumpingData = RxBuf.clone(); // ���� �������� ����
				}

				TxQue.enQueue(c0_Buf); // ��������
				TxQue.enQueue(t0_Buf); // ���а����� ��û
				
				LogUtility.getPumpALogger().debug("�����Ϸ�, ���а����� ��û(t0) : progStep=" + 
						progressStep + " nozState=" + nozState + " pumpingStart=" + pumpingStart);
				
				progressStep=4;
				makeStatusInfo(nozState); // �������� ����(�����Ϸ�)
				
				// Clear preset-data
				presetType="2";
				presetLiter="0000000";
				presetBasePrice="000000";
				presetPrice="000000";
			}
			break;

		case 4 :
			// skip
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
			
			//sendedPumpingEndInfo=true; // POS���� ������ ������ ���ó��

			LogUtility.getPumpALogger().debug("�����Ϸ�, ���а����� ����(t0) : progStep=" + 
					progressStep + " nozState=" + nozState + " pumpingStart=" + pumpingStart);
			
			if (m_nozLock==false && nozType==1) { // �Ϲ�������
				if (Change.toValue(m_basePrice) > 0) {
					TxQue.enQueue(e0_Buf); // �����㰡(����/���� ����)				
					LogUtility.getPumpALogger().debug("���а����� ����, �����㰡(e0) : progStep=" + 
							progressStep + " nozState=" + nozState + " pumpingStart=" + pumpingStart);
				}
			}

			S4_wm = (S4_WorkingMessage) generateWorkingMessage(tBuf, null);
			S4_wm.setNozzleNo(Change.toString("%02d",nozNo));
			int nLiter = Change.toValue(S4_wm.getLiter());
			int nPrice = Change.toValue(S4_wm.getPrice());
			
			if (nLiter > 0 && nPrice > 0) 
				insertRecvQueue(S4_wm); // �����Ϸ� �ڷ�
			else 
				LogUtility.getPumpALogger().debug("�����Ϸ��ڷ� ���� '0'�ΰ�� �߻� : Liter=" + nLiter + 
						" Price=" + nPrice);
			
			break;
		}
	}
	
	

	/**
	 * @param RxBuf
	 * @return
	 * @throws SerialConnectException
	 */
	public boolean recvTotalGauge(byte[] RxBuf) throws Exception, SerialConnectException {

		byte[] byTotalGauge =  new byte[100];
		byte[] byCmd = new byte[2];
		
		try {
			
			// Data send (t0 �۽�)
			t0_Buf[0] = nozID;
			t0_Buf[1] = STX;
			setBCC (t0_Buf); // write BCC
			if(sendText(t0_Buf) != true) {
				if (dispLevel>=1) {
					System.out.printf ("2.Send 't0' fail! (Noz=%02d)\n", nozNo);
					Show.datas(TxBuf, TxBuf.length, 20);
				}
				lineErrCnt++;
				return false;
			}
			if (dispLevel>=2) 
				System.out.printf ("2.Send 't0' (Noz=%02d)\n", nozNo);
	
			if (recvCmd(byCmd) < 1) {
				if (dispLevel>=1)
					System.out.printf ("2.Recv ACK fail! (Noz=%02d)\n", nozNo);
				lineErrCnt++;
				return false;
			}
			
			if (byCmd[1]==ACK) { // recv : ACK
				if (dispLevel>=2)
					System.out.printf ("2.Recv ACK : 0x%02X 0x%02X\n", byCmd[0], byCmd[1]);
				if (sendTail_proc()==false) {
					return false;
				}
				else {
					return true;
				}
			}
			

			// Data receive (t0 ����)
			
			
			

			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogUtility.getPumpALogger().error(e.getMessage(), e);
			return false;
		}
		
		return true;

	}
	
}
