package com.gsc.kixxhub.device.pumpa.driver;

import gnu.io.CommPortOwnershipListener;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;

import com.gsc.kixxhub.common.data.pump.P8_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S3_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S4_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S8_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SH_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.Base64Util;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.common.utility.property.PropertyManager;
import com.gsc.kixxhub.device.pumpa.common.Command;
import com.gsc.kixxhub.device.pumpa.common.SerialConnectException;
import com.gsc.kixxhub.device.pumpa.common.Sleep;
import com.gsc.kixxhub.device.pumpa.controller.ProcessSelector;
import com.gsc.kixxhub.device.pumpa.define.CCommDriver;
import com.gsc.kixxhub.device.pumpa.translation.TransOdt;


public class CommMirae extends CCommDriver implements SerialPortEventListener, CommPortOwnershipListener, 
	Runnable {
	
	//2012.06.12 ksm    'T2' 주유중정보 로그 기록 주기 변경
	private static int intervalInt = 2;
	
	private static String toHexString(byte[] b){
        int count = 1;
        String hex = "";
        int beginidx = 0;
        int endidx = b.length - 1;
        StringBuffer sb = new StringBuffer(b.length * 2 + 1);
        int lastIdx = (b.length - 1 < endidx) ? b.length - 1 : endidx;
        for (int i = beginidx; i <= lastIdx; i++) {
            hex = Integer.toHexString(b[i]);
            if (hex.length() == 1)
                hex = "0" + hex;
            else if (hex.length() == 8)
                hex = hex.substring(6);

            sb.append(hex.toUpperCase() + " ");

            if ((count % 16) == 0)
                sb.append("\n");
            else if ((count % 8) == 0)
                sb.append(" ");

            count++;
        }
        return sb.toString();

    }
	private byte ACK = 0x06;
	private int		buffSize = 1000;	
	private byte[] 	ACKBuf = new byte[buffSize];
	Date currentTime = null;
	String dTime = null;
	private byte EOT = 0x04;
	
	private int		erTotal = 1;
	private byte ETX = 0x03;
	private boolean firstOn = true;
	private byte[] 	LastBuf = new byte[buffSize];
	private byte[]	LastChgInfoBuf = new byte[28];
	private Hashtable<String, Integer> logCount = new Hashtable<String, Integer>();
	private byte NAK = 0x15;
	byte[] nozNum = new byte[2];
	private boolean odtEnOn = true;
	private byte[]	readBuffer = new byte[buffSize];
	
	protected int	readBuffInterval   = 20;
	protected int	readStartInterval  = 5;
	
	private boolean recvChk = true;
	private int		recvCnt = 0;
	private boolean requChk = true;
	
	byte[] sendBufAK = new byte[4];
	byte[] sendBufNK = new byte[4];
	
	private boolean sjChk	= false;
	private boolean statusFirst	= true;
	
	private byte STX = 0x02;
	private int 	t2count = 0;
	
	String tmpNoz = "";
	
	TransOdt trans = new TransOdt();
	
	private byte[] 	TxBuf = new byte[buffSize];

	/**
	 * 생성자
	 * @param nozINum : 노즐번호
	 * @throws Exception
	 */
	public CommMirae (int nozINum) throws Exception {
		
		tmpNoz = String.valueOf(nozINum);
		
		nozNo = nozINum;
		
		if (tmpNoz.length() == 1)
		{
			tmpNoz = "0" + tmpNoz;
		}
		this.nozNum = tmpNoz.getBytes();
		
		this.sendBufAK[0] = Command.SOH;
		this.sendBufAK[1] = nozNum[0];
		this.sendBufAK[2] = nozNum[1];
		this.sendBufAK[3] = Command.ACK;
		
		this.sendBufNK[0] = Command.SOH;
		this.sendBufNK[1] = nozNum[0];
		this.sendBufNK[2] = nozNum[1];
		this.sendBufNK[3] = Command.NAK;
				
		super.interface_method = 0; // 통신방식 0=Serial
		
		String interval = PropertyManager.getSingleton().getProperty(PropertyManager.PUMP_PUMPING_INTERVAL, PropertyManager.PUMP_PUMPING_INTERVAL_DEFAULT) ;
		intervalInt = Integer.parseInt(interval) ;
		if (intervalInt < 3) {
			intervalInt = 3 ;
		}
		
	}
		
	private byte[] generateByteStream (WorkingMessage wm) throws Exception {	
		return trans.generateByteStream(wm);
	}

	private WorkingMessage generateWorkingMessage (byte[] buf, String command)  throws Exception{
		return trans.generateWorkingMessage(buf, command);
	}
	/**
	 * 시스템 시간 가져오기
	 * @param length
	 * @return
	 */
	private String getSystemTimeCM(int length){
		String returnData = "";
		
		if (length == 6) {
			//YYMMDD 
			returnData = GlobalUtility.getDateYYYYMMDD().substring(2);
		} else {
			// YYMMDDhhmmss
			returnData = GlobalUtility.getDateYYYYMMDDHHMMSS().substring(2);
		} 
		
		return returnData;
	
	}

	/**
	 * ack전문 생성
	 * @return
	 * @throws Exception
	 */
	private byte[] makeACK() throws Exception
	{
		byte[] ackM = new byte[4];
		ackM[0] = Command.SOH;
		ackM[1] = nozNum[0];
		ackM[2] = nozNum[1];
		ackM[3] = Command.ACK;
		return 	ackM;	
	}
	
	/**
	 * C1전문 생성
	 * @return
	 * @throws Exception
	 */
	private byte[] makeC1M() throws Exception
	{		
		currentTime = new Date ( );
		dTime = getSystemTimeCM(12);
		
		byte[] c1M = new byte[25];
		byte[] crcCheckVl = null;
		
		c1M[0] = Command.SOH;
		c1M[1] = nozNum[0];
		c1M[2] = nozNum[1];
		c1M[3] = Command.STX;
		c1M[4] = 'C';
		c1M[5] = '1';
		
		for (int i=0; i<12; i++)
		{
			c1M[i+6] =  (byte) dTime.charAt(i);
		}
		c1M[18] = Command.ETB;
		crcCheckVl = makeCRC(c1M);
		c1M[19] = crcCheckVl[0];
		c1M[20] = crcCheckVl[1];
		c1M[21] = crcCheckVl[2];
		c1M[22] = crcCheckVl[3];
		c1M[23] = Command.ETX;
		c1M[24] = makeSum(c1M);
		
		return 	c1M;	
	}
	
	/**
	 * CRC 값을 얻어 냄
	 * @return
	 * @throws Exception
	 */
	private byte[] makeCRC(byte[] byteStreamM) throws Exception
	{
		byte[] crcCheckVl = new byte[4];
		int bsLength = byteStreamM.length;
		int numBytes = 0;
		String CRCtoInt = "";
		
		CRCtoInt = Integer.toHexString(CRCCheck.crc16(byteStreamM, bsLength-6)).toUpperCase();

		for (int i=0; i<4; i++)
		{
			if (3-i < CRCtoInt.length()){
				crcCheckVl[i] = (byte) CRCtoInt.charAt(numBytes);
				numBytes = numBytes + 1;
			} else
				crcCheckVl[i] = '0';
		}
		
		return crcCheckVl; 
	}
	
	/**
	 * 워킹메세지에서 변환된 전문에 CRC와 SUM을 추가
	 * @param byteStreamM
	 * @return
	 * @throws Exception
	 */
	private byte[] makeSendTxt(byte[] byteStreamM) throws Exception
	{
		int txtLength = byteStreamM.length;		
		byte[] crcCheckTXT = null;
		if (byteStreamM != null)
		{
			if (byteStreamM[3] != Command.ACK && byteStreamM[3] != Command.NAK) //보내는 전문이 ACK, NAK 가 아닌경우만 CRC/SUM 을 추가
			{
				if (byteStreamM[4] == 'C' && byteStreamM[5] == 'N')
				{   //CN 명령어가 전달되면, 환경설정정보다 모두 전송된 것임으로, 환경설정정보 요청을 송신할수 있는 상태로 바꿔준다.
					firstOn = false;
					odtEnOn = true;
				}
						
				crcCheckTXT = makeCRC(byteStreamM); //전문에 CRC를 추가
				byteStreamM[txtLength-6] = crcCheckTXT[0];
				byteStreamM[txtLength-5] = crcCheckTXT[1];
				byteStreamM[txtLength-4] = crcCheckTXT[2];
				byteStreamM[txtLength-3] = crcCheckTXT[3];
				byteStreamM[txtLength-2] = Command.ETX;
				byteStreamM[txtLength-1] = makeSum(byteStreamM); //전문에 SUM을 추가
			}
			else //ACK, NAK 를 보낼 경우 응답을 기다리지 않도록 설정 
			{
				if (byteStreamM[3] != Command.NAK)
					LogUtility.getPumpALogger().debug("SEND ACK");
				
				recvCnt = 2;
				recvChk = true;
			}
			return byteStreamM;
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * SUM(BCC) 값을 얻어 냄
	 * @param byteStreamM
	 * @return
	 * @throws Exception
	 */
	private byte makeSum(byte[] byteStreamM) throws Exception	
	{
		byte sumcheckVl = 0;
		byte teACK = 0x20;
		int bsLength = byteStreamM.length;
		
		for (int i = 1; i < bsLength-1; i++) {
			sumcheckVl = (byte) (sumcheckVl ^ byteStreamM[i]);
		}
		
		sumcheckVl = (byte) (sumcheckVl | teACK);
		return sumcheckVl;
	}
	public void ownershipChange(int arg0) {
	}
	
	/**
	 * 수신된 버퍼의 Length체크
	 * @param RxBuf
	 * @return
	 * @throws SerialConnectException
	 */
	private int recvText(byte[] RxBuf) throws SerialConnectException {
		
		int		numBytes=0, c=0, i=0, loop=0;
		boolean	STXFlag=false;
		byte[]	RxByt = new byte[1];

		int		recvLoopCnt=readBuffInterval/10;
		
		try {

			flushBuffer (RxBuf);
			
			for (c=0; c<RxBuf.length-1; c++) {
			
				RxByt[0] = 0x00;
				
				if (is.read(RxByt, 0, 1) < 1) { // no recv data
					//---check timeout
					if (loop > recvLoopCnt) break;
					else loop++;
					
					c--;
					Sleep.sleep(2); // wait
					continue;
				} else
					loop=0;
				
				RxBuf[i] = RxByt[0];
				
				if (RxBuf[i]==STX)
					STXFlag=true;

				if (!STXFlag)
					if (RxBuf[i]==EOT || RxBuf[i]==ACK || RxBuf[i]==NAK) {  // recv EOT/ACK/NAK
						numBytes=i+1;
						break;
					}
				
				if (RxBuf[i]==ETX) { // recv ETX next STX
					if (STXFlag) {
						RxByt[0] = 0x00;
						is.read(RxByt, 0, 1); // read BCC
						RxBuf[i+1] = RxByt[0];
						numBytes=i+2;
						break;
					}
					else {  // recv ETX none STX
						flushBuffer (RxBuf);
						numBytes=0;
						break;
					}
				}
				i++;
			}
			return numBytes;
			
		} catch (IOException e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
			return 0;
		} catch (Exception e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
			return 0;
		}
	}

	/**
	 * 	ODT로 전문 수
	 * @param buf
	 * @return
	 * @throws Exception
	 * @throws SerialConnectException
	 */
	private int recvText(byte[] readBuffer, int numBytes) throws Exception, SerialConnectException {
		
		byte[] crcCheckVl = null;
		byte[] tempByte = new byte[numBytes];

		int rtnCheck = 1;
		for (int i=0; i<numBytes; i++)
		{
			tempByte[i] = readBuffer[i];
		}
		
		if (tempByte[3] == ACK) //ACK는 프로세스로 전달하지 않음
		{
			rtnCheck = 0;
			LogUtility.getPumpALogger().debug("Receive ACK");
			return rtnCheck;
		} 

		if (tempByte[3] == EOT) {
			rtnCheck = 0;
			LogUtility.getPumpALogger().debug("Receive EOT");
			return rtnCheck;
			
		} 
		
		if (numBytes > 6){ // ACK or EOT 가 아니면, 길이가 7 자리 이상 
			
//			로그용 변수
			String Tempstr ="";
			String Tempstr2 ="";
			byte[] newbyteAr = new byte[2];
			newbyteAr[0] = tempByte[1];
			newbyteAr[1] = tempByte[2];
			Tempstr = new String(newbyteAr);
			Tempstr2 = new String(tempByte);
			//로그용 변수 End
			
			crcCheckVl = makeCRC(tempByte);
			rtnCheck = 0;
			
			if (!(crcCheckVl[0] == tempByte[numBytes-6] &&
					crcCheckVl[1] == tempByte[numBytes-5] &&
					crcCheckVl[2] == tempByte[numBytes-4] &&
					crcCheckVl[3] == tempByte[numBytes-3] &&
					makeSum(tempByte) == tempByte[numBytes-1])) //CRC 와 SUM이 옳바른가 체크
			{
				LogUtility.getPumpALogger().debug("CheckSum and BCC ERROR, Command : "+ toHexString(tempByte) +", Noz No. : "  + Tempstr);
				rtnCheck = 1;
				return rtnCheck;
			}
			
			if (tempByte[4] == 'T' && tempByte[5] =='1' && tempByte[6] =='2'
				&& LastBuf[1] == tempByte[1] && LastBuf[2] == tempByte[2])
			{
				//초기환경정보 요청이 올라올 경우, 이전 주유중 자료가 존재하고 주유완료 정보가 전송되지 않았을 경우
				//가상 주유완료 정보를 PumpM 으로 전송한다.
				if (!firstOn && sjChk  ) {
					LogUtility.getPumpALogger().debug("SEND WORKING MASSAGE : KJ, START SUCCESS.");
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
					Sleep.sleep(300);
				}
				
				if (firstOn)
					//kixxhub가 기동되고 최초의 명령이 초기환경정보 요청일 경우 명령어를 전송하지 않는다.
					requChk = false;
				else {   
					//kixxhub 기동 후 최초 명령이 초기환경정보 요청이 아닐 경우
					if (odtEnOn) //초기환경정보 요청이 올 경우 1회만 전송한다.
					{
						String returnNozzleNo = null;
						returnNozzleNo = new String(this.nozNum);
						//초기환경정보를 요청하기 위해 현재 노즐아이디로 기동되어 있는 프로세서를 강제종료
						ProcessSelector.removeProcess(returnNozzleNo);
						
						LogUtility.getPumpALogger().debug("Status Change : " + Tempstr2);
						
						sjChk = false;
						odtEnOn = false;
						requChk = true;
					}
					else
					{
						requChk = false;
					}
				}//private boolean odtEnOn = true;
			}
			else
			{
				firstOn = false;
				odtEnOn = true;
				requChk = true;
				
				//상태정보 변경시만 내용 전송
				if (tempByte[4] == 'T' && tempByte[5] =='1')
				{
					if (statusFirst) {
						requChk = true;
						statusFirst = false;
					} 
					else {
						if (LastBuf[1] == tempByte[1] && 
								LastBuf[2] == tempByte[2] && 
								LastBuf[6] != tempByte[6])
						{
							requChk = true;
							LogUtility.getPumpALogger().debug("Status Change : " + Tempstr2);
							
						} else {
							requChk = false;
						}
						
						for (int i=0; i<numBytes; i++) {
							LastBuf[i] = tempByte[i];
						}
					}
				}
				
			}
			
			if (tempByte[4] == 'T' && tempByte[5] =='2') {
//					LogUtility.getPumpALogger().debug("T2 numBytes" + numBytes);
				if ( !sjChk) {   
					//주유중 정보가 올라오면, 주유시작정보전문을 만들기 위해 토탈게이지요청 전문을 충전기로 전송한다.
					//이때 처음 올라온 주유정보는 버린다. 주유중정보를 최초전송받았을 때, 1회만 시도.
					String returnNozzleNo = null;
					returnNozzleNo = new String(this.nozNum);
					P8_WorkingMessage wmCC = new P8_WorkingMessage();
					wmCC.setNozzleNo(returnNozzleNo);
					//sendText(makeCCM());
					sndQue.enQueue(wmCC);
					sjChk = true;
					requChk = false;
				}
				else {	
					//주유중 자료 3회에 한번씩만 전송
					if (t2count == 3) {
						t2count = 0;
						requChk = true;
					} else {
						t2count = t2count + 1;
						requChk = false;
					}
				}
				
				for (int i=0; i<numBytes; i++)
				{
					LastChgInfoBuf[i] = tempByte[i];
				}
			}
			if (tempByte[4] == 'T' && tempByte[5] =='4'){  
				//최종판매정보가 올라오면, 주유완료자료전문을 만들어 모듈로 전송하고, 이후 최종판매정보를 모듈로 전송한다.
				//LogUtility.getPumpALogger().debug("SEND WORKING MASSAGE : S4, START SUCCESS.");
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
				Sleep.sleep(300);

			}
			
			if (requChk){
				if (tempByte[4] == 'T' && tempByte[5] =='A' && sjChk ) {
//						토탈게이지 정보가 전송되었을 때 주유시작 정보로 판단할 것인지를 구별한다.
					WorkingMessage wmT = generateWorkingMessage(tempByte, "SJ");					
					insertRecvQueue(wmT);
				}
				else					{
					WorkingMessage wmT = generateWorkingMessage(tempByte, null);					
					insertRecvQueue(wmT);
				}
				
				ACKBuf = makeACK();
				sendText(ACKBuf); //정상 수신일때 수신 여부(ACK)를 ODT로 보냄
			}
			/*else if (statusChk == false) {
					LogUtility.getPumpALogger().debug("Do Not Request WorkingMessage Command : "+ Tempstr2 +", Noz No. : "  + Tempstr);
			}
			*/
		} else {
			String Tempstr3 ="";
			Tempstr3 = new String(tempByte);
			LogUtility.getPumpALogger().debug("ERROR Syntax : "  + Tempstr3);
			rtnCheck = 1;
		}

		return rtnCheck;
	}

	/**
	 * 스케즐러에 의해 호출되는 메서드로 큐에 쌓이 데이타를 읽어 들인다.
	 */
	@Override
	public void requestData() throws Exception, SerialConnectException {

		String Tempstr = "";
		int 	numBytes=0;
		
		while (sndQue.getItemCount() > 0) {
			WorkingMessage wm = (WorkingMessage)sndQue.deQueue();
			byte[] wmByte = generateByteStream(wm);
			
			if (wmByte != null) 
				TxQue.enQueue(wmByte);
		}
		
		if (recvChk) {
			//이전 보낸 명령에 대해 응답을  받았을 경우에 true
			flushBuffer(TxBuf);
			if (TxQue.isEmpty()) {
				//큐에 내용이 없으면 C1 전문 전송
				TxBuf = makeC1M();				
				Tempstr = "Noz No : " + new String(nozNum)+",  "+new String(TxBuf);
			} 
			else {
				//큐에 내용이 있으면 큐의 전문을 전송
				TxBuf = makeSendTxt (TxQue.getFirstItem()); // write CRC, SUM
				TxQue.deQueue();
				
				if (TxBuf == null) {
					TxBuf = makeC1M();
				}
				
				Tempstr = "Noz No : " + new String(nozNum)+" "+new String(TxBuf);
				LogUtility.getPumpALogger().info ("Send TX : "+Tempstr);
			}

			sendText(TxBuf);
		}
		else {
			//이전 보낸 명령에 대해 응답을 못 받았을 경우에 false
			Tempstr = "Noz No : " + new String(nozNum)+" "+new String(TxBuf) + ", erTotal!!!! : " + erTotal;
			LogUtility.getPumpALogger().debug ("send TX ReTry : "+Tempstr);
			sendText(TxBuf);
		}
		
		while (recvCnt < 2) //수신을 못했을 경우, 수신한데이터의 CRC/SUM에 이상 발생시 : 1회의 폴링 주기에 2회만 반복함
		{
			Sleep.sleep(50);
			try {
				
				flushBuffer(readBuffer);
				numBytes = recvText(readBuffer);

				Tempstr = new String(readBuffer);
				if (numBytes > 2){
					//수신된 바이트 수가 4바이트 미만이면 정상적이지 않은 전문으로 처리한다.
					if (recvText(readBuffer, numBytes) != 0) {
						recvChk = false;
						erTotal++;
						LogUtility.getPumpALogger().debug("dosen't buffer : "+ new String(nozNum)+ ", Count : "+recvCnt+", Error count : "+erTotal);
						recvCnt++;

						if (erTotal == 11) 
						{	// 전송명령에 대해서 응답이 총시도 회수 30회 이면 수신을 포기하고 다음 명령이 진행되도록 함.
							// 회선불량  인식시간이 느릴경우 이 숫자를 줄인다.
							S8_WorkingMessage wmS8 = new S8_WorkingMessage();
							wmS8.setDeviceType("04");
							wmS8.setConnectNozzleNo(tmpNoz);
							wmS8.setNozzleNo(tmpNoz);
							wmS8.setStatus("1");
							wmS8.setStatusCode("601");
							wmS8.setErrMsg("                    ");
							currentTime = new Date ( );
							dTime = getSystemTimeCM(12);
							wmS8.setDetectTime(dTime);
							insertRecvQueue(wmS8);  //충전기 통신 장애 상태를 보고 함.
							LogUtility.getPumpALogger().info("GAS ODT connection Fail");
							recvChk = true; // 수신을 포기 하지 않고 무한정 수신을 기다리게 하려면 recvChk = false;
							recvCnt = 2;
							erTotal = 1;
							LastBuf[6]='X';
						}
						
					}
					else
					{
						recvChk = true;
						recvCnt = 2; //이전 보낸 명령에 대해 응답이 수신되었음으로 recvChk를 true, commCnt를 2로 설정하여 수신 종료
						Tempstr = new String(readBuffer);
						//정상 데이타 수신시 erTotal를 초기화 한다.
						erTotal = 1;
						if (readBuffer[4] == 'T' && readBuffer[5] !='1')	{
							
							// 신용카드 및 보너스카드 승인요청 시 카드번호 암호화 적용.
							if (readBuffer[4] == 'T' && readBuffer[5] =='6') {
								byte[] bt_Tempstr =  Tempstr.getBytes();								
								byte[] bt_credit_no = new byte[40];
								
								System.arraycopy(bt_Tempstr, 8, bt_credit_no, 0, 40);
								
								String credit_no = Base64Util.encode(bt_credit_no);

								byte[] bt_print_Tempstr = new byte[bt_Tempstr.length - 40 + credit_no.length()];
								
								System.arraycopy(bt_Tempstr, 0, bt_print_Tempstr, 0, 8);
								System.arraycopy(credit_no.getBytes(), 0, bt_print_Tempstr, 8, credit_no.length());
								System.arraycopy(bt_Tempstr, 48, bt_print_Tempstr, 8 + credit_no.length() , bt_Tempstr.length - 48);
								
								LogUtility.getPumpALogger().info (new String(bt_print_Tempstr).trim() +",Bt:"+numBytes);
								
							// 2012.06.12 ksm  T2 의 경우( 주유중 정보) 로그 기록을 조절함. intervalInt 사용. with 박종호K
							} else if (readBuffer[4] == 'T' && readBuffer[5] == '2') {	
								String nozzleNo = (new String(Tempstr)).substring(1, 3);
								int logCnt = 1;
								
								if (logCount.containsKey(nozzleNo)) {
									logCnt = logCount.get(nozzleNo) + 1;
									logCount.put(nozzleNo, logCnt);
								} else {
									logCount.put(nozzleNo, logCnt);
								}
								
								// 로그를 3번에 1회 찍는다. 너무 많음.
								if ( logCount.get(nozzleNo) == intervalInt) {
									LogUtility.getPumpALogger().debug (Tempstr.trim());
									logCount.put(nozzleNo, 0);
								} 
							} else {
								LogUtility.getPumpALogger().debug (Tempstr.trim());
							}
						}
					}
				} else {
					recvChk = false;
					recvCnt++;
					LogUtility.getPumpALogger().debug("dosen't buffer : "+ new String(nozNum)+ ", Count : "+recvCnt+", Error count : "+erTotal);
					erTotal++;
					if (erTotal == 11) 
					{	// 전송명령에 대해서 응답이 총시도 회수 30회 이면 수신을 포기하고 다음 명령이 진행되도록 함.
						// 회선불량  인식시간이 느릴경우 이 숫자를 줄인다.
						S8_WorkingMessage wmS8 = new S8_WorkingMessage();
						wmS8.setDeviceType("04");
						wmS8.setConnectNozzleNo(tmpNoz);
						wmS8.setNozzleNo(tmpNoz);
						wmS8.setStatus("1");
						wmS8.setStatusCode("601");
						wmS8.setErrMsg("                    ");
						currentTime = new Date ( );
						dTime = getSystemTimeCM(12);
						wmS8.setDetectTime(dTime);
						insertRecvQueue(wmS8);  //충전기 통신 장애 상태를 보고 함.
						LogUtility.getPumpALogger().info("GAS ODT connection Fail");
						recvChk = true; // 수신을 포기 하지 않고 무한정 수신을 기다리게 하려면 recvChk = false;
						recvCnt = 2;
						erTotal = 1;
						LastBuf[6]='X';
					}
				}
			} catch (IOException e) {
				LogUtility.getPumpALogger().error(e.getMessage(), e);
			} catch (SerialConnectException e) {
				LogUtility.getPumpALogger().error(e.getMessage(), e);
			} catch (Exception e) {
				LogUtility.getPumpALogger().error(e.getMessage(), e);
			} 			
		}
		
		recvCnt = 0; //반복회수 초기화
	}
	
	public void run() {
		
	}	
	
	/**
	 * 	ODT로 전문 전송
	 * @param buf
	 * @return
	 * @throws Exception
	 * @throws SerialConnectException
	 */
	private boolean sendText(byte[] buf) throws Exception, SerialConnectException {
		
		try {
			os.write(buf);
			
		} catch (IOException e) {
			LogUtility.getPumpALogger().error(e.getMessage(),e);
			return false;
		}
		return true;
	}
	
	public void serialEvent(SerialPortEvent arg0) {

		
	}
	
}