/* �پ��� MPP�� �Ϲ� ������ */
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
				
		if (wm.getCommand().equals("PB")) { // �������� ����
			
			// �ܰ��� t11_ds�� �����Ѵ�.
			PB_WorkingMessage PB_wm = (PB_WorkingMessage) wm;
			
			m_basePrice = PB_wm.getBasePrice();	
			m_pumpGrant[baseNozNo][fixedSubNozNo-1] = true;

			LogUtility.getPumpALogger().info("PB ���� �����Ͽ����ϴ�. nozzle=" + PB_wm.getNozzleNo() + 
					" ODT=" + PB_wm.getConnectNozzleNo()+ " baseNoz=" + baseNozNo+" fixedSubNozNo="+fixedSubNozNo+
					" liter=" + PB_wm.getLiter() + " bPrice=" + PB_wm.getBasePrice() + " price=" + PB_wm.getPrice());
						
			skip = false;
		}
		else if (wm.getCommand().equals("P3_1")) { // �ܰ�����
			
			// �ܰ��� t11_ds�� �����Ѵ�.
			P3_1_WorkingMessage P3_wm = (P3_1_WorkingMessage) wm;
			String bPrice = P3_wm.getBasePrice().substring(0, 4);
			
			m_sBasePrice[baseNozNo][fixedSubNozNo-1] = bPrice;
			m_setNozzle[baseNozNo][fixedSubNozNo-1] = true;
			
			LogUtility.getPumpALogger().info("P3 ����(�ܰ�) �����Ͽ����ϴ�. nozzle=" + P3_wm.getNozzleNo());
			//LogUtility.getPumpALogger().info("nozNo=" + nozNo + " baseNoz=" + baseNozNo + " fixedSubNozNo="+fixedSubNozNo);
			LogUtility.getPumpALogger().info("m_setNozzle-> [0]=" + m_setNozzle[baseNozNo][0] + 
					" [1]=" + m_setNozzle[baseNozNo][1] + " [2]=" + m_setNozzle[baseNozNo][2]);
			
//			LogUtility.getPumpALogger().info("P3 ���� �����Ͽ����ϴ�. nozzle=" + P3_wm.getNozzleNo() + 
//					" ODT=" + P3_wm.getConnectNozzleNo()+ " baseNoz=" + baseNozNo+" fixedSubNozNo="+fixedSubNozNo+
//					" bPrice=" + P3_wm.getBasePrice());
			//PB_wm.print();
			
			TxQue.enQueue(t15_Buf); // ����������û(P3 ������������ m_setNozzle�� ���� �ʱ���¿��� �������� ������ ����)
			
			skip = true;
		}
		else if (wm.getCommand().equals("PA")) { // �������� ��û(�������/����)

			PA_WorkingMessage PA_wm = (PA_WorkingMessage) wm;
			int nNozState = Change.toValue(PA_wm.getNozzleState());

			m_nozLock = (nNozState==0 ? true : false);
			m_nozLocks[baseNozNo][fixedSubNozNo-1] = m_nozLock;
			
			if (m_nozLock==true) { // �������
				makeStatusInfo_direct(656);
				skip = false;
			}
			else { // �������
				makeStatusInfo_direct(657);
				skip = true;
			}

			LogUtility.getPumpALogger().info("PA ���� �����Ͽ����ϴ�. nozzle=" + PA_wm.getNozzleNo() + 
					" mode=" + PA_wm.getNozzleState());
					
		}
		else if (wm.getCommand().equals("P7")) { // ������ �Ķ���� ����

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
		if (RxBuf[3]=='0' && RxBuf[4]=='0') { // ������ �ʱ�ȭ
						
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
		else if (RxBuf[3]=='6' && RxBuf[4]=='0') { // ������ ��Ȳ����

			progressStep = 2;		
		} 
		else if (RxBuf[3]=='6' && RxBuf[4]=='1') { // ������ Status ����(������/�Ϸ� ����)

			// ������ �ڷ� ����ó���� �߰�(08/10/10)
			waitPumpingData=false;
			waitPumpingData_Cnt=0;
			progressStep = 3;
			
			short state = (short) (RxBuf[5] - 0x30);			
			long currTime = cal.getTimeInMillis();
									
			// ���� (09/01/16)
			if ((nozState>=5 || (nozState==0 && realPumpingStart==true)) && state==1)
				return; // ����
						
			switch (state) {
				
				case 0 :  // ����ٿ�
					if ((currTime - m_nNozUpTime) > 3000) { // 3 sec
						m_nNozDownTime=currTime;
						//LogUtility.getPumpALogger().debug("Nozzle-Down Time=====>>>>" + m_nNozDownTime + " (Noz="+(baseNozNo+subNozNo-1)+")");
						nozState=0;
					} else { // ������
						Sleep.sleep(500);
						TxQue.enQueueNewer(t15_Buf); // ����������û
						nozState=1;
						return;
					}
					break;
				case 1 :  // �����
					if ((currTime - m_nNozDownTime) > 3000) { // 3 sec
						m_nNozUpTime=currTime;
						//LogUtility.getPumpALogger().debug("Nozzle-Up Time=====>>>>" + m_nNozUpTime + " (Noz="+(baseNozNo+subNozNo-1)+")");
						nozState=1;
					} else { // ������
						Sleep.sleep(500);
						TxQue.enQueueNewer(t15_Buf); // ����������û
						nozState=0;
						return;
					}
					break;
					
				case 3 :  // ������
					nozState = 4;
					break;
					
				case 4 :  //�����Ϸ�
					nozState = 5;
					recvedPumpingEnd=true;
					break;
					
				default :
					nozState = state;
					break;
			}
			
			nozState = (pumpingStart==true && nozState==0 && 
					    recvedPumpingEnd==false ? 5 : nozState); //�����Ϸ��ڷ� �̼��� ó�� -> ����(09/01/08)                    
			
			// �پ���6����(����) ��������ȣ ó��(������ ������ subNozNo ���� ����)
			int subNoz = RxBuf[23] - 0x30;
			if (subNoz >= 1) {
				subNozNo = subNoz;
				//subNozNo = (subNoz==5? 3 : subNoz);
				m_recvedSubNozID=true; // 61�� ������ nozzle status �� 1~3 ���Ž� true
			} 
			
			//LogUtility.getPumpALogger().debug("ProcessRcvSTX=====> nozState="+nozState+" subNoz="+subNoz+" subNozNo="+subNozNo+" m_recvedSubNozID="+m_recvedSubNozID);			
		}
		else if (RxBuf[3]=='6' && RxBuf[4]=='5') { // ������ ����ġ ����

			switch (progressStep) {
				
				case 3  : 
					if (nozState==1 && m_nozLocks[baseNozNo][subNozNo-1]==false) { // ��������¿��� ����				
						SJ_wm = (SJ_WorkingMessage) generateWorkingMessage(RxBuf, "SJ", subNozNo);
						insertRecvQueue(SJ_wm); // �������� �ڷ� �۽�
						sentPumpingStartInfo=true;
						return; // �߰�(09/02/02)
					}
					else if (nozState==4) { // ������ ����
						// Skip
					}
					else if (nozState==5 || (nozState==0 && realPumpingStart==true)) { // �����Ϸ��� ����
						progressStep = 4;
						nozState = 5;
					} 
					/*else if (progressStep==3 && (nozState==0 && realPumpingStart==false)) {
						//System.out.printf ("(2-1) : nozNo=%d subNozNo=%d\n", nozNo, subNozNo);	
						insertRecvQueue(generateWorkingMessage(RxBuf, "S5", subNozNo)); // ���а����� �ڷ� �۽�
					}*/
					
					break;
				
				case 4 :
					break; // skip
			}
		}


		//############# STX Process ##############//
		switch (progressStep) {
		case 3 :

			if (nozState==0) { // ����ٿ�

				if (m_setNozzle[baseNozNo][subNozNo-1]==true) {

					pumpingStart=false; // �ʱ�ȭ
					sentPumpingStartInfo=false;				
					beforeNozzleUp=false;
					makeStatusInfo(nozState); // �������� ����
	
					LogUtility.getPumpALogger().info("nozzle=" + (baseNozNo+subNozNo-1) + " -> ����ٿ� : subNozNo=" + subNozNo + 
							" nozLocks[0]=" + m_nozLocks[0] + " nozLocks[1]=" + m_nozLocks[1] + " nozLocks[2]=" + m_nozLocks[2]);
				}
			}
			else if (nozState==1) { // ����� & �Ϲ�������	

				if (m_setNozzle[baseNozNo][subNozNo-1]==true) {

					flushBuffer(lastPumpingData);
					pumpingStart=true; // Apppend 2008.04.19 (��ź������)
					beforeNozzleUp=true;
					realPumpingStart=false; // �ʱ�ȭ
					recvedPumpingEnd=false;
					
					makeStatusInfo(nozState); // �������� ����
					
					if (sentPumpingStartInfo==false) {
						TxQue.enQueue(t20_Buf); // ���а����� ��û(�������� ���� ���ſ�)
						sentPumpingStartInfo=true;
					}	
	
//					LogUtility.getPumpALogger().info("\n#######�����>>>> nozzle=" + nozNo + " subNozNo=" + subNozNo +  " pumpGrant=" + m_pumpGrant +
//							" m_bSetNozzle-> [0]=" + m_setNozzle[baseNozNo][0] + " [1]=" + m_setNozzle[baseNozNo][1] + " [2]=" + m_setNozzle[baseNozNo][2] +"\n");
					
					// $$$$$$$$$$$$$$$$$$$$$$
					if (nozType==1 && m_pumpGrant[baseNozNo][subNozNo-1]==false && m_nozLocks[baseNozNo][subNozNo-1]==false) { // �Ϲ�������
						
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
						TxQue.enQueue(t11_Buf); // �����㰡(����/���� ����)
	
						LogUtility.getPumpALogger().info("nozzle=" + (baseNozNo+subNozNo-1) + " -> �����, �����㰡 : subNozNo=" + subNozNo + 
							" nozLocks[0]=" + m_nozLocks[0] + " nozLocks[1]=" + m_nozLocks[1] + " nozLocks[2]=" + m_nozLocks[2]);
					}
					else
						LogUtility.getPumpALogger().info("nozzle=" + (baseNozNo+subNozNo-1) + " -> ����� : subNozNo=" + subNozNo + 
							" nozLocks[0]=" + m_nozLocks[0] + " nozLocks[1]=" + m_nozLocks[1] + " nozLocks[2]=" + m_nozLocks[2]);
				}
			}
			else if (nozState == 4) { // ������
					
				lastPumpingData = RxBuf.clone(); // ���� �������� ����
				pumpingStart=true;
				realPumpingStart=true;
				
				if (firstPumpingData==true) {
					makeStatusInfo(nozState); // �������� ����
					firstPumpingData=false;
				}
					
				TxQue.enQueueNewer(t15_Buf);
				waitPumpingData=true; // ������ �ڷ� ����ó���� �߰�(08/10/10)

				S3_wm = (S3_WorkingMessage) generateWorkingMessage(RxBuf, "S3");
										
				insertRecvQueue(S3_wm);

			}
			else if (nozState == 5) { // �����Ϸ�
							
				lastPumpingData = RxBuf.clone(); // ���� �������� ����
	
				TxQue.enQueue(t20_Buf); // ���а����� ��û
				TxQue.enQueue(t13_Buf); // �㰡���
				
				waitTotalGuageFor_S4=true;
				progressStep = 4;
				
				if (m_nozLocks[baseNozNo][subNozNo-1]==false)
					makeStatusInfo(nozState); // �������� ����(�����Ϸ�)
				
				LogUtility.getPumpALogger().info("nozzle=" + (baseNozNo+subNozNo-1) + " -> �����Ϸ�, ���а����� ��û : subNozNo=" + subNozNo + 
						" nozLocks[0]=" + m_nozLocks[0] + " nozLocks[1]=" + m_nozLocks[1] + " nozLocks[2]=" + m_nozLocks[2]);
			}
			break;

		case 4 :
			if (nozState == 5) { // �����Ϸ��� ���а����� ����
				
				byte[][] tBuf = new byte [2][80];
				tBuf[0] = lastPumpingData.clone(); // ���� ��������
				flushBuffer(lastPumpingData);
				tBuf[1] = RxBuf.clone(); // Total guage

				progressStep = 3;
				nozState = 0;

				TxQue.enQueue(t13_Buf); // �㰡���

				//if (m_nozLocks[subNozNo-1]==false) {
				if (m_pumpGrant[baseNozNo][subNozNo-1]==true) {
					
					if (m_recvedSubNozID==true && tBuf[0][23] > 0) { // �߰�(09/01/08) - tBuf[0][23]=subNoz number
						S4_wm = (S4_WorkingMessage) generateWorkingMessage(tBuf, "S4", subNozNo);

						int nPrice=0, nLiter=0; 
						if (S4_wm != null) {
							nPrice = Change.toValue(S4_wm.getPrice());
							nLiter = Change.toValue(S4_wm.getLiter());
						}
						
						if (realPumpingStart==true) {
							if (nPrice>0 && nLiter>0)
								insertRecvQueue(S4_wm); // �����Ϸ� �ڷ�
						} 
						else if (realPumpingStart==false) {
							S4_wm.setLiter("0000000");
							S4_wm.setPrice("00000000");
							insertRecvQueue(S4_wm); // �����Ϸ� �ڷ�
						}
						
						LogUtility.getPumpALogger().info("nozzle=" + (baseNozNo+subNozNo-1) + " -> �����Ϸ�, ���а����� ����. �Ϸ�����(S4) ���� ��ƾ: m_pumpGrant=" +m_pumpGrant[subNozNo-1]);
						LogUtility.getPumpALogger().info("nozzle=" + (baseNozNo+subNozNo-1) + " -> realPumpingStart="+realPumpingStart+" nPrice="+nPrice+" nLiter="+nLiter);
					}
					
					m_recvedSubNozID=false;
				}
				else {
					LogUtility.getPumpALogger().info("nozzle=" + (baseNozNo+subNozNo-1) + " -> �����Ϸ�, ���а����� ����. �Ϸ�����(S4) ���۾��� : m_pumpGrant=" + m_pumpGrant[subNozNo-1]);
				}
				
				makeStatusInfo(nozState); // �������� ����(����ٿ�)

				//LogUtility.getPumpALogger().debug("####>>>> ����ٿ�. m_nozLocks= " + m_nozLocks[subNozNo-1] +
						//" beforeNozzleUp="+beforeNozzleUp+" realPumpingStart="+realPumpingStart);
						
				// ����(09/01/13)
				pumpingStart=false; 
				sentPumpingStartInfo=false;
				firstPumpingData=true;

				realPumpingStart=false;
				waitTotalGuageFor_S4=false;
				waitGaugeForS4_Cnt=0;
				sendPumpingData_Cnt=0;
				m_pumpGrant[baseNozNo][subNozNo-1]=false;
				
				//m_nozLocks[subNozNo-1] = false; // true -> false�� ����(09/06/14)
			}
			break;
		}
	}
}
