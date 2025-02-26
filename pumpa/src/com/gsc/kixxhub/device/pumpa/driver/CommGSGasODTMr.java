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
 * 변경일자 : 2016.03.28 양일준
 * 변경내용 : TCP/IP 통신방식 충전기 ODT 드라이버 
 * 1. 기존 전문에서 CRC, SUM 삭제
 * 2. C1 전문에서 CRC, SUM, 현재시각 삭제 
 * 3. 수신 및 송신데이터 로그 생성은 GSocketServerGas.java 에서 처리
 * 4. 전문 정상수신 시 ACK전송 로직 삭제 => ACK는 GSocketServerGas.java 에서 송신
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

	//	2012.06.12 ksm    'T2' 주유중정보 로그 기록 주기 변경
	public static int intervalInt = 1 ;
	
	/**
	 * 생성자
	 * 
	 * @param nozINum : 노즐번호
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

		super.interface_method = 1; // 통신방식 (1: TCP/IP)

		try {
			INetStart();
		} catch (Exception e) {
			LogUtility.getLogger().error(e.getMessage(), e);
		}
	}

	/**
	 * C1전문 생성
	 * 변경내용 : 현재시각 삭제 
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
					
					// CN 명령어가전달되면,환경설정정 모두 전송된 것임으로, 환경설정정보 요청을 송신할 수 있는 상태로 바꿔준다.
					firstOn = false;
					odtEnOn = true;
				}
			return byteStreamM;
		} else {
			return null;
		}
	}
	
	/**
	 * 변경내용:
	 * 1. 정상수신 시 ACK전송 로직 삭제 => GSocketServerGas.java 에서 ACK 송신
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
			// 초기환경정보 요청이 올라올 경우, 이전 주유중 자료가 존재하고 주유완료 정보가 전송되지 않았을 경우 (중간에 ODT가 작동이 멈추어 재구동하는 경우)
			// 가상 주유완료 정보를 PumpM 으로 전송한다.
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
				// kixxhub가 기동되고 최초의 명령이 초기환경정보 요청일 경우 명령어를 전송하지 않는다.
				LogUtility.getLogger().info("[Pump A] kixxhub가 기동되고 최초의 명령이 초기환경정보 요청일 경우");
				requChk = false;
			} else {
				// kixxhub 기동 후 최초 명령이 초기환경정보 요청이 아닐 경우
				if (odtEnOn) // 초기환경정보 요청이 올 경우 1회만 전송한다.
				{
					String returnNozzleNo = null;
					returnNozzleNo = new String(this.nozNum);
					LogUtility.getLogger().info("[Pump A] returnNozzleNo : " + returnNozzleNo);

					// 초기환경정보를 요청하기 위해 현재 노즐아이디로 기동되어 있는 프로세서를 강제종료
					ProcessSelector.removeProcess(returnNozzleNo);
					//	로그용 변수
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
   
				// 상태정보 변경시만 내용 전송
			if (tempByte[4] == 'T' && tempByte[5] == '1') {
				if (statusFirst) {
					requChk = true;
					statusFirst = false;
				} else {
					if (LastBuf[1] == tempByte[1]
							&& LastBuf[2] == tempByte[2]
							&& LastBuf[6] != tempByte[6]) {
						requChk = true;
                        // 로그용 변수
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
				
				// 주유중 정보가 올라오면, 주유시작정보전문을 만들기 위해 토탈게이지요청 전문을 충전기로 전송한다.
				// 이때 처음 올라온 주유정보는 버린다. 주유중정보를 최초전송받았을 때, 1회만 시도.
				// ODT 켜진 후 처음 CC 전문에는 토털게이지가 '0000000000'으로 올라오며 이 후 토털게이지는 정상적으로 올라옴
				String returnNozzleNo = null;
				returnNozzleNo = new String(this.nozNum);
				P8_WorkingMessage wmCC = new P8_WorkingMessage();
				wmCC.setNozzleNo(returnNozzleNo);
				sndQue.enQueue(wmCC);
				sjChk = true;
				requChk = false;
				
			} else {
				
				// 주유중 자료 3회에 한번씩만 전송
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
			
			// 최종판매정보가 올라오면, 주유완료자료전문을 만들어 모듈로 전송하고, 이후 최종판매정보를 모듈로 전송한다.
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
				// 토탈게이지 정보가 전송되었을 때 주유시작 정보로 판단할 것인지를 구별한다.
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
	
	
	// 수신전문 처리 
	public void recvMessage(byte[] RxBuf) {
		try {
            // 수신된 전문 처리 
			recvText(RxBuf, RxBuf.length); 	
				
			// 메모리 삭제 
			ClearUtil.setClearString(RxBuf);
			
			} catch (Exception e) {
				
			LogUtility.getLogger().error(e.getMessage(), e);
			}
		}

	// 데이터 폴링 호출 (스케즐러에 의해 호출되는 메서드로 큐에 쌓이 데이타를 읽어 들인다.)
	public void requestData() throws Exception, SerialConnectException {
		try {
			// ODT 초기화 확인 및 설정
			if (m_sockServer != null && m_sockServer.isOdtInitCompleted() == true) { // ODT 초기화 완료
				if (isODTInitCompleted == false) {  
					isODTInitCompleted = true;
				}
			}
			
            // ODT와 KixxHub간 초기화 작업 후 소켓서버 연결 끊길 시 S8전문 생성
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
				wmS8.setErrMsg("회선 불량 (공통-주유기, 셀프 ODT, 충전기 ODT) - Off");
				currentTime = new Date ( );
				dTime = getSystemTimeCM(12);
				wmS8.setDetectTime(dTime);
				//recvChk = true; // 수신을 포기 하지 않고 무한정 수신을 기다리게 하려면 recvChk = false;
				insertRecvQueue(wmS8);  //충전기 통신 장애 상태를 보고 함.
				LastBuf[6]='X';
				TxQue.flushQueue();
			}
			
			Sleep.sleep(30);
			
			// 송신전문 처리 
			while (sndQue.getItemCount() > 0) {
				WorkingMessage wm = (WorkingMessage)sndQue.deQueue();
				byte[] wmByte = generateByteStream(wm);
				if (wmByte != null) {
					TxQue.enQueue(wmByte); 
				}
				wm = null;
			}
			
			// GSOcketServer 상태 및 ODT 초기화 여부 확인 후 전문 송신 
			if (m_sockServer != null && m_sockServer.isOdtInitCompleted() == true) { 
				
				// 데이터 송신 처리부
                // 큐에 내용이 있으면 큐의 전문을 전송
				if (TxQue.isEmpty() == false) { 
					for (int i=0; i<TxQue.getItemCount(); i++) {
						TxBuf = makeSendTxt((TxQue.getFirstItem()));
						TxQue.deQueue();
						sendMessage(TxBuf);
					}
                // 큐에 내용이 없으면 C1전문을 전송
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
	 * 시스템 시간 가져오기
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
	 * ODT번호 별 서버 시작 
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
	 * 소켓서버로 송신전문 전송 
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