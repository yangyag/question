package com.gsc.kixxhub.device.pumpa.driver;

import java.util.Date;

import com.gsc.kixxhub.common.data.pump.P8_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S3_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S4_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S8_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SH_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.ClearUtil;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.common.utility.property.PropertyManager;
import com.gsc.kixxhub.device.pumpa.common.Change;
import com.gsc.kixxhub.device.pumpa.common.Command;
import com.gsc.kixxhub.device.pumpa.common.SerialConnectException;
import com.gsc.kixxhub.device.pumpa.common.Sleep;
import com.gsc.kixxhub.device.pumpa.controller.GServerService;
import com.gsc.kixxhub.device.pumpa.controller.GSocketServerGas;
import com.gsc.kixxhub.device.pumpa.controller.ProcessSelector;
import com.gsc.kixxhub.device.pumpa.define.CCommDriver;
import com.gsc.kixxhub.device.pumpa.translation.TransGSGasOdt;


/**
 * �������� : 2016.03.28 ������
 * ���泻�� : TCP/IP ��Ź�� ������ ODT ����̹� 
 * 1. ���� �������� CRC, SUM ����
 * 2. C1 �������� CRC, SUM, ����ð� ���� 
 * 3. ���� �� �۽ŵ����� �α� ������ GSocketServerGas.java ���� ó��
 * 4. ���� ������� �� ACK���� ���� ���� => ACK�� GSocketServerGas.java ���� �۽�
 */

public class CommGSGasODTMr extends CCommDriver implements GServerService  {
	TransGSGasOdt trans = new TransGSGasOdt();

	Date currentTime = null;
	
	String dTime = null;
	
	protected int BUFFER_SIZE = 4096;

	protected byte[] TxBuf = new byte[BUFFER_SIZE];
	
	protected byte[] RxBuf = new byte[BUFFER_SIZE];

	private boolean requChk = true;

	private boolean odtEnOn = true;

	private boolean firstOn = true;

	private boolean sjChk = false;

	private boolean statusFirst = true;

	private int buffSize = 1000;

	byte[] nozNum = new byte[2];

	private byte[] LastBuf = new byte[buffSize];

	private byte[] LastChgInfoBuf = new byte[28];

	protected int readBuffInterval = 20;

	protected int readStartInterval = 5;

	protected int HEADER_SIZE = 8;

	protected int m_statusCode = 601;

	private int t2count = 0;

	String tmpNoz = "";

	private String host = "";
	
	private int port = 55000;

	private GSocketServerGas m_sockServer = new GSocketServerGas(HEADER_SIZE,	BUFFER_SIZE);
	
	protected boolean	isODTInitCompleted = false;

	//	2012.06.12 ksm    'T2' ���������� �α� ��� �ֱ� ����
	public static int intervalInt = 1 ;
	
	/**
	 * ������
	 * 
	 * @param nozINum : �����ȣ
	 * @param ipAddress
	 * @param ver
	 * @throws Exception
	 */

	public CommGSGasODTMr(int nozINum, String romVerStr, String ipAddress)throws Exception {
		tmpNoz = String.valueOf(nozINum);
		
		nozNo = nozINum;
		super.romVer = romVerStr;
		super.ipAddress = ipAddress;

		String interval = PropertyManager.getSingleton().getProperty("pump.pumping.interval", "3") ;
		intervalInt = Integer.parseInt(interval) ;
		if (intervalInt < 3) {
			intervalInt = 3 ;
		}
		
		if (tmpNoz.length() == 1) {
			tmpNoz = "0" + tmpNoz;
		}
		this.nozNum = tmpNoz.getBytes();

		super.interface_method = 1; // ��Ź�� (1: TCP/IP)

		try {
			INetStart();
		} catch (Exception e) {
			LogUtility.getLogger().error(e.getMessage(), e);
		}
	}

	/**
	 * C1���� ����
	 * ���泻�� : ����ð� ���� 
	 * @return
	 * @throws Exception
	 */
	private byte[] makeC1M() throws Exception {
		
		byte[] c1M = new byte[7];
		c1M[0] = Command.SOH;
		c1M[1] = nozNum[0];
		c1M[2] = nozNum[1];
		c1M[3] = Command.STX;
		c1M[4] = 'C';
		c1M[5] = '1';
		c1M[6] = Command.ETX;

		return c1M;
	}

	/**
	 * 
	 * @param byteStreamM
	 * @return
	 * @throws Exception
	 */
	private byte[] makeSendTxt(byte[] byteStreamM) throws Exception {
		if (byteStreamM != null) {
				if (byteStreamM[4] == 'C' && byteStreamM[5] == 'N') { 
					
					// CN ��ɾ���޵Ǹ�,ȯ�漳���� ��� ���۵� ��������, ȯ�漳������ ��û�� �۽��� �� �ִ� ���·� �ٲ��ش�.
					firstOn = false;
					odtEnOn = true;
				}
			return byteStreamM;
		} else {
			return null;
		}
	}
	
	/**
	 * ���泻��:
	 * 1. ������� �� ACK���� ���� ���� => GSocketServerGas.java ���� ACK �۽�
	 * @return
	 * @throws Exception
	 */
	private void recvText(byte[] bodyBuffer, int numBytes) throws Exception, SerialConnectException {
		
		byte[] tempByte = new byte[numBytes];

		for (int i = 0; i < numBytes; i++) {
			tempByte[i] = bodyBuffer[i];
		}
		
		if (tempByte[4] == 'T' && tempByte[5] == '1' && tempByte[6] == '2' 
			&& LastBuf[1] == tempByte[1] && LastBuf[2] == tempByte[2]) {
			// �ʱ�ȯ������ ��û�� �ö�� ���, ���� ������ �ڷᰡ �����ϰ� �����Ϸ� ������ ���۵��� �ʾ��� ��� (�߰��� ODT�� �۵��� ���߾� �籸���ϴ� ���)
			// ���� �����Ϸ� ������ PumpM ���� �����Ѵ�.
			if (!firstOn && sjChk) {
				LogUtility.getLogger().debug("[Pump A] SEND WORKING MASSAGE : KJ, START SUCCESS.");
				String returnNozzleNo = null;
				returnNozzleNo = new String(this.nozNum);
				S4_WorkingMessage wmS4 = new S4_WorkingMessage();
				WorkingMessage wmTemp = generateWorkingMessage(LastChgInfoBuf, null);
				S3_WorkingMessage s3Message = (S3_WorkingMessage) wmTemp;

				wmS4.setNozzleNo(returnNozzleNo);
				wmS4.setFlag("0");
				wmS4.setLiter(s3Message.getLiter());
				wmS4.setBasePrice("000000");
				wmS4.setPrice(s3Message.getPrice());
				wmS4.setWDate(getSystemTimeCM(6));
				wmS4.setSystemTime(getSystemTimeCM(12));
				wmS4.setTotalGauge("0000000000");
				wmS4.setStatusFlag("9");

				wmS4.print();

				insertRecvQueue(wmS4);
				ProcessSelector.removeProcess(returnNozzleNo);
				sjChk = false;
				//Sleep.sleep(300);
			}

			if (firstOn){
				// kixxhub�� �⵿�ǰ� ������ ����� �ʱ�ȯ������ ��û�� ��� ��ɾ �������� �ʴ´�.
				LogUtility.getLogger().info("[Pump A] kixxhub�� �⵿�ǰ� ������ ����� �ʱ�ȯ������ ��û�� ���");
				requChk = false;
			} else {
				// kixxhub �⵿ �� ���� ����� �ʱ�ȯ������ ��û�� �ƴ� ���
				if (odtEnOn) // �ʱ�ȯ������ ��û�� �� ��� 1ȸ�� �����Ѵ�.
				{
					String returnNozzleNo = null;
					returnNozzleNo = new String(this.nozNum);
					LogUtility.getLogger().info("[Pump A] returnNozzleNo : " + returnNozzleNo);

					// �ʱ�ȯ�������� ��û�ϱ� ���� ���� ������̵�� �⵿�Ǿ� �ִ� ���μ����� ��������
					ProcessSelector.removeProcess(returnNozzleNo);
					//	�α׿� ����
					String Tempstr = "";
					Tempstr = new String(tempByte);
					LogUtility.getLogger().debug("[Pump A] Status Change :" + Tempstr);

					sjChk = false;
					odtEnOn = false;
					requChk = true;
				} else {
					requChk = false;
				}
			}// private boolean odtEnOn = true;
			} else {
				firstOn = false;
				odtEnOn = true;
				requChk = true;
   
				// �������� ����ø� ���� ����
			if (tempByte[4] == 'T' && tempByte[5] == '1') {
				if (statusFirst) {
					requChk = true;
					statusFirst = false;
				} else {
					if (LastBuf[1] == tempByte[1]
							&& LastBuf[2] == tempByte[2]
							&& LastBuf[6] != tempByte[6]) {
						requChk = true;
                        // �α׿� ����
						String Tempstr = "";
						Tempstr = new String(tempByte);
						LogUtility.getLogger().debug("[Pump A] Status Change :" + Tempstr);

					} else {
						requChk = false;
					}

					for (int i = 0; i < numBytes; i++) {
						LastBuf[i] = tempByte[i];
					}
				}
			}
		}
		
		if (tempByte[4] == 'T' && tempByte[5] == '2') {
			if (!sjChk) {
				
				// ������ ������ �ö����, ������������������ ����� ���� ��Ż��������û ������ ������� �����Ѵ�.
				// �̶� ó�� �ö�� ���������� ������. ������������ �������۹޾��� ��, 1ȸ�� �õ�.
				// ODT ���� �� ó�� CC �������� ���а������� '0000000000'���� �ö���� �� �� ���а������� ���������� �ö��
				String returnNozzleNo = null;
				returnNozzleNo = new String(this.nozNum);
				P8_WorkingMessage wmCC = new P8_WorkingMessage();
				wmCC.setNozzleNo(returnNozzleNo);
				sndQue.enQueue(wmCC);
				sjChk = true;
				requChk = false;
				
			} else {
				
				// ������ �ڷ� 3ȸ�� �ѹ����� ����
				if (t2count == 3) {
					t2count = 0;
					requChk = true;
				} else {
					t2count = t2count + 1;
					requChk = false;
				}
			}

			for (int i = 0; i < numBytes; i++) {
				LastChgInfoBuf[i] = tempByte[i];
			}
		}
		
		if (tempByte[4] == 'T' && tempByte[5] == '4') {
			
			// �����Ǹ������� �ö����, �����Ϸ��ڷ������� ����� ���� �����ϰ�, ���� �����Ǹ������� ���� �����Ѵ�.
			LogUtility.getLogger().debug("[Pump A] SEND WORKING MASSAGE : S4,START SUCCESS.");
			String returnNozzleNo = null;
			returnNozzleNo = new String(this.nozNum);
			S4_WorkingMessage wmS4 = new S4_WorkingMessage();
			WorkingMessage wmTemp = generateWorkingMessage(tempByte, null);
			SH_WorkingMessage shMessage = (SH_WorkingMessage) wmTemp;
			wmS4.setNozzleNo(returnNozzleNo);
			wmS4.setFlag("0");
			wmS4.setLiter(shMessage.getTotalLiter());
			wmS4.setBasePrice(shMessage.getUp1());
			wmS4.setPrice(shMessage.getTotalAMT1());
			wmS4.setWDate(getSystemTimeCM(6));
			wmS4.setSystemTime(getSystemTimeCM(12));
			wmS4.setTotalGauge(shMessage.getTotalGauge());
			wmS4.setStatusFlag("0");

			insertRecvQueue(wmS4);
			sjChk = false;
			requChk = true;
			//Sleep.sleep(300);
		}

		if (requChk) {
			if (tempByte[4] == 'T' && tempByte[5] == 'A' && sjChk) {
				// ��Ż������ ������ ���۵Ǿ��� �� �������� ������ �Ǵ��� �������� �����Ѵ�.
				WorkingMessage wmT = generateWorkingMessage(tempByte, "SJ");
				insertRecvQueue(wmT);
			} else {
				WorkingMessage wmT = generateWorkingMessage(tempByte, null);
				insertRecvQueue(wmT);
			}
		}
		ClearUtil.setClearString(bodyBuffer);
		ClearUtil.setClearString(tempByte);
	}
	
	
	// �������� ó�� 
	public void recvMessage(byte[] RxBuf) {
		try {
            // ���ŵ� ���� ó�� 
			recvText(RxBuf, RxBuf.length); 	
				
			// �޸� ���� 
			ClearUtil.setClearString(RxBuf);
			
			} catch (Exception e) {
				
			LogUtility.getLogger().error(e.getMessage(), e);
			}
		}

	// ������ ���� ȣ�� (�����񷯿� ���� ȣ��Ǵ� �޼���� ť�� ���� ����Ÿ�� �о� ���δ�.)
	public void requestData() throws Exception, SerialConnectException {
		try {
			// ODT �ʱ�ȭ Ȯ�� �� ����
			if (m_sockServer != null && m_sockServer.isOdtInitCompleted() == true) { // ODT �ʱ�ȭ �Ϸ�
				if (isODTInitCompleted == false) {  
					isODTInitCompleted = true;
				}
			}
			
            // ODT�� KixxHub�� �ʱ�ȭ �۾� �� ���ϼ��� ���� ���� �� S8���� ����
			if (m_sockServer.isConnected() == false && isODTInitCompleted == true) 
			{	
				LogUtility.getLogger().info("[Pump A] *******************GAS ODT No." + tmpNoz + " connection Fail*******************");
				isODTInitCompleted = false;
				S8_WorkingMessage wmS8 = new S8_WorkingMessage();
				wmS8.setDeviceType("04");
				wmS8.setConnectNozzleNo(tmpNoz);
				wmS8.setNozzleNo(tmpNoz);
				wmS8.setStatus("1");
				wmS8.setStatusCode("601");
				wmS8.setErrMsg("ȸ�� �ҷ� (����-������, ���� ODT, ������ ODT) - Off");
				currentTime = new Date ( );
				dTime = getSystemTimeCM(12);
				wmS8.setDetectTime(dTime);
				//recvChk = true; // ������ ���� ���� �ʰ� ������ ������ ��ٸ��� �Ϸ��� recvChk = false;
				insertRecvQueue(wmS8);  //������ ��� ��� ���¸� ���� ��.
				LastBuf[6]='X';
				TxQue.flushQueue();
			}
			
			Sleep.sleep(30);
			
			// �۽����� ó�� 
			while (sndQue.getItemCount() > 0) {
				WorkingMessage wm = (WorkingMessage)sndQue.deQueue();
				byte[] wmByte = generateByteStream(wm);
				if (wmByte != null) {
					TxQue.enQueue(wmByte); 
				}
				wm = null;
			}
			
			// GSOcketServer ���� �� ODT �ʱ�ȭ ���� Ȯ�� �� ���� �۽� 
			if (m_sockServer != null && m_sockServer.isOdtInitCompleted() == true) { 
				
				// ������ �۽� ó����
                // ť�� ������ ������ ť�� ������ ����
				if (TxQue.isEmpty() == false) { 
					for (int i=0; i<TxQue.getItemCount(); i++) {
						TxBuf = makeSendTxt((TxQue.getFirstItem()));
						TxQue.deQueue();
						sendMessage(TxBuf);
					}
                // ť�� ������ ������ C1������ ����
				} else if (TxQue.isEmpty() &&  m_sockServer.isCanSendC1()) { 
					TxBuf = makeC1M();
					sendMessage(TxBuf);
				}				
			}
		} catch (Exception e) {
			LogUtility.getLogger().error("[Pump A] Exception occurr! (Noz=" + nozNo + ")");
			LogUtility.getLogger().error(e.getMessage(), e);
		}
	}

	private byte[] generateByteStream(WorkingMessage wm) throws Exception {
		return trans.generateByteStream(wm);
	}

	private WorkingMessage generateWorkingMessage(byte[] buf, String command)
			throws Exception {
		return trans.generateWorkingMessage(buf, command);
	}

	/**
	 * �ý��� �ð� ��������
	 * 
	 * @param length
	 * @return
	 */
	private String getSystemTimeCM(int length) {
		String returnData = "";

		if (length == 6) {
			// YYMMDD
			returnData = GlobalUtility.getDateYYYYMMDD().substring(2);
		} else {
			// YYMMDDhhmmss
			returnData = GlobalUtility.getDateYYYYMMDDHHMMSS().substring(2);
		}
		return returnData;
	}

	
	/**
	 * ODT��ȣ �� ���� ���� 
	 */
	public void INetStart() {
		try {
			host = super.ipAddress;
			String nozStr = Change.toString("%02d", super.nozNo);
			port = 55000 + nozNo;
			
			m_sockServer.service=this;
			m_sockServer.start(host, port, nozStr);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/**
	 * ���ϼ����� �۽����� ���� 
	 * @param sndBuf
	 */
	public void sendMessage(byte[] sndData) {
		// TODO Auto-generated method stub
		try {
			//m_sockServer.TxQue.enQueueNewer(sndData);
			m_sockServer.TxQue.enQueue(sndData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public boolean verifyRecvData(byte[] rcvBytes) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isRcvEnvDataOK() {
		// TODO Auto-generated method stub
		return false;
	}		
}