package com.gsc.kixxhub.device.pumpa.driver;

import gnu.io.CommPortOwnershipListener;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.gsc.kixxhub.common.data.pump.P8_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S4_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.S8_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SH_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.Command;
import com.gsc.kixxhub.device.pumpa.common.SerialConnectException;
import com.gsc.kixxhub.device.pumpa.common.Sleep;
import com.gsc.kixxhub.device.pumpa.controller.ProcessSelector;
import com.gsc.kixxhub.device.pumpa.define.CCommDriver;
import com.gsc.kixxhub.device.pumpa.translation.TransGSGasOdt;

public class CommMiraeHitRatio extends CCommDriver implements SerialPortEventListener, CommPortOwnershipListener, 
	Runnable {
	
	TransGSGasOdt trans = new TransGSGasOdt();
	
	private byte ACK = 0x06;
	
	private boolean ackOn   = false;
	private int		buffSize = 1000;
	Date currentTime = null;
	
	String dTime = null;
	private byte ENQ = 0x05;
	private byte EOT = 0x04;
	private int		erTotal = 0;
	private byte ETX = 0x03;
	private boolean firstOn = true;
	SimpleDateFormat formatter = new SimpleDateFormat ( "yyMMddHHmmss");
	
	private int gasCount = 0;
	private boolean hitStart   = false;
	private byte NAK = 0x15;
	private byte[] nozNum = new byte[2];
	private boolean odtEnOn = true;
	private int payCoutn = 0;
	private byte[]	readBuffer = new byte[buffSize];
	private boolean recvChk = true;
	private int		recvCnt = 0;
	private byte REQ = 0x05;
	private boolean requChk = true;
	private byte[] 	RxBuf = new byte[buffSize];
	private byte[] sendBufAK = new byte[4];
	private byte[] sendBufNK = new byte[4];
	private boolean sjChk	= false;
	
	private byte STX = 0x02;
	
	private int tempCheck = 0;
	private byte[] 	testBufT2 = new byte[28];
	private byte[]	testBufTA = new byte[23];
	String tmpNoz = "";
	
	private byte[] 	TxBuf = new byte[buffSize];
	
	public CommMiraeHitRatio (int nozINum) {
		
		tmpNoz = String.valueOf(nozINum);
		
		nozNo = nozINum;
		
		if (tmpNoz.length() == 1)
		{
			tmpNoz = "0" + tmpNoz;
		}
		this.nozNum = tmpNoz.getBytes();
		
		this.sendBufAK[0] = Command.SOH;
		this.sendBufAK[1] = this.nozNum[0];
		this.sendBufAK[2] = this.nozNum[1];
		this.sendBufAK[3] = Command.ACK;
		
		this.sendBufNK[0] = Command.SOH;
		this.sendBufNK[1] = this.nozNum[0];
		this.sendBufNK[2] = this.nozNum[1];
		this.sendBufNK[3] = Command.NAK;
		
		
	}

	
	private byte[] generateByteStream (WorkingMessage wm) throws Exception {	
		return trans.generateByteStream(wm);
	}
	private WorkingMessage generateWorkingMessage (byte[] buf, String command) throws Exception {
		return trans.generateWorkingMessage(buf, command);
	}
	
	public String getSystemTimeCM(int length){
		String returnData = null; 
		Calendar calendar = Calendar.getInstance();
		
		String year = Integer.toString(calendar.get(Calendar.YEAR));
		
		String month = Integer.toString(calendar.get(Calendar.MONTH) + 1);
		if(month.length() != 2){
			month = "0" + month;
		}
		
		String day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
		if(day.length() != 2){
			day = "0" + day;
		}
		
		String hour = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
		if(hour.length() != 2){
			hour = "0" + hour;
		}
		
		String minute = Integer.toString(calendar.get(Calendar.MINUTE));
		if(minute.length() != 2){
			minute = "0" + minute;
		}
		
		String second = Integer.toString(calendar.get(Calendar.SECOND));
		if(second.length() != 2){
			second = "0" + second;
		}
		
		if (length == 12) {
			// YYMMDDhhmmss
			returnData = year.substring(2) + month + day + 
										hour + minute + second;
		
		} else if (length == 6) {
			//YYMMDD 
			returnData = year.substring(2) + month + day;
		
		} else {
			// YYMMDDhhmmss
			returnData = year.substring(2) + month + day + 
										hour + minute + second;
		
		}
		
		return returnData;
	
	}	// end getSystemTime
	
	public byte[] makeC1M() throws Exception
	{		
		currentTime = new Date ( );
		dTime = formatter.format ( currentTime );
		
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

	public byte[] makeC1M2(byte[] nozID) throws Exception
	{		
		currentTime = new Date ( );
		dTime = formatter.format ( currentTime );
		
		byte[] c1M = new byte[25];
		byte[] crcCheckVl = null;
		
		c1M[0] = Command.SOH;
		c1M[1] = nozID[0];
		c1M[2] = nozID[1];
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
	public byte[] makeCCM() throws Exception
	{
		byte[] crcCheckVl = null;
		byte[] ccM = new byte[25];
		ccM[0] = Command.SOH;
		ccM[1] = nozNum[0];
		ccM[2] = nozNum[1];
		ccM[3] = Command.STX;
		ccM[4] = 'C';
		ccM[5] = 'C';
		ccM[6] = Command.ETB;
		crcCheckVl = makeCRC(ccM);
		ccM[7] = crcCheckVl[0];
		ccM[8] = crcCheckVl[1];
		ccM[9] = crcCheckVl[2];
		ccM[10] = crcCheckVl[3];
		ccM[11] = Command.ETX;
		ccM[12] = makeSum(ccM);
		
		return 	ccM;	
	}
	public byte[] makeCRC(byte[] byteStreamM)  throws Exception//CRC ���� ��� ��
	{
		byte[] crcCheckVl = new byte[4];
		int bsLength = byteStreamM.length;
		int numBytes = 0;
		String CRCtoInt = "";
		
		CRCtoInt = Integer.toHexString(CRCCheck.crc16(byteStreamM, bsLength-6)).toUpperCase();

		for (int i=0; i<4; i++)
		{
			if (3-i < CRCtoInt.length())
			{
				crcCheckVl[i] = (byte) CRCtoInt.charAt(numBytes);
				numBytes = numBytes + 1;
			}
			else
			{
				crcCheckVl[i] = '0';
			}
		}
		
		return crcCheckVl; 
	}
	public byte[] makeSendTxt(byte[] byteStreamM) throws Exception	//��ŷ�޼������� ��ȯ�� ������ CRC�� SUM�� �߰�
	{
		int txtLength = byteStreamM.length;		
		byte[] crcCheckTXT = null;
		if (byteStreamM[3] != Command.ACK) //������ ������ ACK �� �ƴѰ�츸 CRC/SUM �� �߰�
		{
			if (byteStreamM[4] == 'C' && byteStreamM[5] == 'N')
			{   //CN ��ɾ ���޵Ǹ�, ȯ�漳�������� ��� ���۵� ��������, ȯ�漳������ ��û�� �۽��Ҽ� �ִ� ���·� �ٲ��ش�.
				firstOn = false;
				odtEnOn = true;
				
				
				
				hitStart = true;
			}
					
			crcCheckTXT = makeCRC(byteStreamM); //������ CRC�� �߰�
			byteStreamM[txtLength-6] = crcCheckTXT[0];
			byteStreamM[txtLength-5] = crcCheckTXT[1];
			byteStreamM[txtLength-4] = crcCheckTXT[2];
			byteStreamM[txtLength-3] = crcCheckTXT[3];
			byteStreamM[txtLength-2] = Command.ETX;
			byteStreamM[txtLength-1] = makeSum(byteStreamM); //������ SUM�� �߰�
		}
		else //ACK�� ���� ��� ������ ��ٸ��� �ʵ��� ���� 
		{
			recvCnt = 2;
			recvChk = true;
		}
		return byteStreamM;
	}
	
	public byte makeSum(byte[] byteStreamM) throws Exception //SUM(BCC) ���� ��� ��
	{
		byte sumcheckVl = 0;
		byte teACK = 0x20;
		int bsLength = byteStreamM.length;
		
		for (int i = 1; i < bsLength-1; i++)
		{
			sumcheckVl = (byte) (sumcheckVl ^ byteStreamM[i]);
		}
		
		sumcheckVl = (byte) (sumcheckVl | teACK);
		
		return sumcheckVl;
	}
	public void ownershipChange(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public int recvText(byte[] RxBuf) throws SerialConnectException {
		
		int		numBytes=0, c=0, i=0, loop=0;
		boolean	STXFlag=false;
		byte[]	RxByt = new byte[1];

		try {

			flushBuffer (RxBuf);
			
			//if (is.available()==0)
				//return 0;
			
			for (c=0; c<RxBuf.length-1; c++) {
			
				RxByt[0] = 0x00;
				
				if (is.read(RxByt, 0, 1) < 1) { // no recv data
					//---check timeout
					if (loop > 2000) break;
					else loop++;
					
					c--;
					continue;
				} else
					loop=0;
				
				RxBuf[i] = RxByt[0];
				
				if (RxBuf[i]==STX)
					STXFlag=true;

				if (STXFlag==false)
					if (RxBuf[i]==EOT || RxBuf[i]==ACK || RxBuf[i]==NAK) {  // recv EOT/ACK/NAK
						numBytes=i+1;
						break;
					}
				
				if (RxBuf[i]==ETX) { // recv ETX next STX
					if (STXFlag==true) {
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

	public int recvText(byte[] readBuffer, int numBytes) throws Exception, SerialConnectException {
		
		byte[] crcCheckVl = null;
		byte[] tempByte = new byte[numBytes];
		int rtnCheck = 1;
		for (int i=0; i<numBytes; i++)
		{
			tempByte[i] = readBuffer[i];
		}
		//Show.datas(tempByte, tempByte.length, 30);
		if (tempByte[3] == ACK) //ACK�� ���μ����� �������� ����
		{
			rtnCheck = 0;
		} else if (tempByte[3] == EOT) {
			rtnCheck = 0;
			
		} else if (numBytes > 6){ // ACK or EOT �� �ƴϸ�, ���̰� 7 �ڸ� �̻� 
			crcCheckVl = makeCRC(tempByte);
			if (crcCheckVl[0] == tempByte[numBytes-6] &&
					crcCheckVl[1] == tempByte[numBytes-5] &&
					crcCheckVl[2] == tempByte[numBytes-4] &&
					crcCheckVl[3] == tempByte[numBytes-3] &&
					makeSum(tempByte) == tempByte[numBytes-1]) //CRC �� SUM�� �ǹٸ��� üũ
			{
				rtnCheck = 0;
				

//				private boolean recvChk = true;
//				private boolean requChk = true;
//				private boolean odtEnOn = true;
//				private boolean firstOn = true;
//				private boolean ackOn   = false;
				if (tempByte[4] == 'T' && tempByte[5] =='1' && tempByte[6] =='2')
				{
					if (firstOn == true)
					{	//kixxhub�� �⵿�ǰ� ������ ����� �ʱ�ȯ������ ��û�� ��� ��ɾ �������� �ʴ´�.
						requChk = false;
					}
					else
					{   //kixxhub �⵿ �� ���� ����� �ʱ�ȯ������ ��û�� �ƴ� ���
						if (odtEnOn == true) //�ʱ�ȯ������ ��û�� �� ��� 1ȸ�� �����Ѵ�.
						{
							String returnNozzleNo = null;
							returnNozzleNo = new String(this.nozNum);
							//�ʱ�ȯ�������� ��û�ϱ� ���� ���� ������̵�� �⵿�Ǿ� �ִ� ���μ����� ��������
							ProcessSelector.removeProcess(returnNozzleNo);
							
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
				}
				if (tempByte[4] == 'T' && tempByte[5] =='2')
				{
					if ( sjChk == false )
					{   //������ ������ �ö����, ������������������ ����� ���� ��Ż��������û ������ ������� �����Ѵ�.
						//�̶� ó�� �ö�� ���������� ������. ������������ �������۹޾��� ��, 1ȸ�� �õ�.
						String returnNozzleNo = null;
						returnNozzleNo = new String(this.nozNum);
						P8_WorkingMessage wmCC = new P8_WorkingMessage();
						wmCC.setNozzleNo(returnNozzleNo);
						//sendText(makeCCM());
						sndQue.enQueue(wmCC);
						//LogUtility.getPumpALogger().debug("\n\nSend Text CC Message");
						sjChk = true;
						requChk = false;
					}
					else
					{
						
					}
				}
				if (tempByte[4] == 'T' && tempByte[5] =='4')
				{  //�����Ǹ������� �ö����, �����Ϸ��ڷ������� ����� ���� �����ϰ�, ���� �����Ǹ������� ���� �����Ѵ�.
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
//					LogUtility.getPumpALogger().debug("SEND WORKING MASSAGE : S4, SUCCESS.");
				}
				
				//�α׿� ����
				String Tempstr ="";
				String Tempstr2 ="";
				byte[] newbyteAr = new byte[2];
				newbyteAr[0] = tempByte[1];
				newbyteAr[1] = tempByte[2];
				Tempstr = new String(newbyteAr);
				newbyteAr[0] = tempByte[4];
				newbyteAr[1] = tempByte[5];
				Tempstr2 = new String(newbyteAr);
				//�α׿� ���� End
				if (requChk == true)
				{
//					String Tempstr ="";
//					Tempstr = new String(nozNum)+" "+new String(tempByte);
//					LogUtility.getPumpALogger().debug ("send TX : "+Tempstr);
//					LogUtility.getPumpALogger().debug("\n\nWorkingMessage Command : " + tempByte[1]+tempByte[2]+"  "+ Tempstr);
					if (tempByte[4] == 'T' && tempByte[5] =='A' && sjChk == true) //��Ż������ ������ ���۵Ǿ��� �� �������� ������ �Ǵ��� �������� �����Ѵ�.
					{
						WorkingMessage wmT = generateWorkingMessage(tempByte, "SJ");					
						insertRecvQueue(wmT);
						//sjChk = false;
//						LogUtility.getPumpALogger().debug("\n\nWorkingMessage Command : " + Tempstr +"  "+ wmT.getCommand());
					}
					else
					{
						WorkingMessage wmT = generateWorkingMessage(tempByte, null);					
						insertRecvQueue(wmT);
//						LogUtility.getPumpALogger().debug("\n\nWorkingMessage Command : " + Tempstr +"  "+ wmT.getCommand());
					}
					
					sendText(sendBufAK); //���� �����϶� ���� ����(ACK)�� ODT�� ����
				}
				else
				{
					LogUtility.getPumpALogger().debug("\n\nDo Not Request WorkingMessage Command : "+ Tempstr2 +", Noz No. : "  + Tempstr);
				}
			}
			else
			{
				LogUtility.getPumpALogger().debug("CheckSum Error");
			}
		}

		if (rtnCheck == 0)
		{
			//String Tempstr = "";
			//Tempstr = new String(nozNum)+" "+new String(tempByte);
			//LogUtility.getPumpALogger().debug ("send TX : "+Tempstr);
			//Show.datas(tempByte, tempByte.length, 20);
			
			//ACK ���� ����
//			WorkingMessage wmT = generateWorkingMessage(tempByte, null);
//			LogUtility.getPumpALogger().debug("\n\nWorkingMessage Command : " + wmT.getCommand());
//			insertRecvQueue(wmT);
			
		}
		return rtnCheck;
	}

	@Override
	public void requestData() throws Exception, SerialConnectException {
		//Sleep.sleep(500);
		//flushBuffer(RxBuf);
		String Tempstr = "";
		int 	numBytes=0;
		
//		PE_WorkingMessage pe = new PE_WorkingMessage();
//		pe.setNozzleNo("01");
//		sndQue.enQueue(pe);
//		LogUtility.getPumpALogger().debug ("enQueue : "+new String(generateByteStream(pe)));
		
//		System.out.print(sndQue.getItemCount());
		while (sndQue.getItemCount() > 0) {
			WorkingMessage wm = (WorkingMessage)sndQue.deQueue();
			byte[] wmByte = generateByteStream(wm);
			
//			LogUtility.getPumpALogger().debug("sendQue Insert");
//			for (int i = 0; i < wmByte.length; i++) {
//				System.out.print("B[" + i +"] = " + wmByte[i] + " ");
//			}
			
			
//			Tempstr = "TEST BYTE MESSAGE : "+new String(wmByte);
//			LogUtility.getPumpALogger().debug(Tempstr);
			  
			if (wmByte != null) 
				TxQue.enQueue(wmByte);
		}
		
		if (recvChk == true) //���� ���� ��ɿ� ���� ������  �޾��� ��쿡 true
		{
			flushBuffer(TxBuf);
			if (TxQue.isEmpty()==true) //ť�� ������ ������ C1 ���� ����
			{
//				sendText(makeC1M());
				TxBuf = makeC1M();
				
//				LogUtility.getPumpALogger().debug ("\n-----SEND-----SEND-----SEND-----");
				
				Tempstr = "Noz No : " + new String(nozNum)+",  "+new String(TxBuf);
				LogUtility.getPumpALogger().debug ("Send C1 : "+Tempstr);
			} 
			else //ť�� ������ ������ ť�� ������ ����
			{
				//flushBuffer(TxBuf);
				//LogUtility.getPumpALogger().debug("WARNNING MESSAGE : !!!!! : "+new String(TxQue.getFirstItem()));
				TxBuf = makeSendTxt (TxQue.getFirstItem()); // write CRC, SUM
//				LogUtility.getPumpALogger().debug ("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
//				sendText(TxBuf);
				TxQue.deQueue();
				
//				LogUtility.getPumpALogger().debug ("\n-----SEND-----SEND-----SEND-----");
				
				Tempstr = "Noz No : " + new String(nozNum)+" "+new String(TxBuf);
				LogUtility.getPumpALogger().debug ("Send TX : "+Tempstr);
			}
//			LogUtility.getPumpALogger().debug ("\n^^^^^SEND^^^^^SEND^^^^^SEND^^^^^");
//			Tempstr = new String(nozNum)+" "+new String(TxBuf);
//			LogUtility.getPumpALogger().debug ("send TX : "+Tempstr);
//
			sendText(TxBuf);
		}
		else //���� ���� ��ɿ� ���� ������ �� �޾��� ��쿡 false
		{
			LogUtility.getPumpALogger().debug ("erTotal!!!! : " + erTotal);
			if ((erTotal%3) == 0) { //3ȸ ���� ���� �� ���� 1ȸ�� ��� �� ����
				sendText(TxBuf);
//				LogUtility.getPumpALogger().debug ("\n-----RETRY-----RETRY-----RETRY-----");
				
				Tempstr = "Noz No : " + new String(nozNum)+" "+new String(TxBuf);
				LogUtility.getPumpALogger().debug ("send TX ReTry : "+Tempstr);

			} else { //���� ���� ��ɿ� ���� (2ȸ)������ �� �޾��� ��쿡 NAK ����
				sendText(sendBufNK);
				LogUtility.getPumpALogger().debug ("send NAK!!!! to Noz No : " + new String(nozNum));
			}
		}
		
		while (recvCnt < 2) //������ ������ ���, �����ѵ������� CRC/SUM�� �̻� �߻��� : 1ȸ�� ���� �ֱ⿡ 2ȸ�� �ݺ���
		{
			Sleep.sleep(200);
			try {
				
				flushBuffer(readBuffer);
				//LogUtility.getPumpALogger().debug(	" is.available()      :" + is.available()) ;				
				numBytes = is.read(readBuffer);
				//LogUtility.getPumpALogger().debug(	" is.read(readBuffer) :" + numBytes) ;

				
				
				//// ������ �����׽�Ʈ �� �ڵ� == �׽�Ʈ �Ϸ��� ���� ���� == 
				String tempgasCount = "";
				String temppayCoutn = "";
				String stGasCount = "";
				String stPayCoutn = "";
				byte[] crcCheckVltemp = null;
				
				testBufT2[0] = Command.SOH;
				testBufT2[1] = nozNum[0];
				testBufT2[2] = nozNum[1];
				testBufT2[3] = Command.STX;
				testBufT2[4] = 'T';
				testBufT2[5] = '2';
				
				gasCount = gasCount + 1;
				payCoutn = payCoutn + 2;
				tempgasCount = Integer.toString(gasCount);
				temppayCoutn = Integer.toString(payCoutn);
				
				for (int k=0; k<6; k++)
				{
					if (7-k > tempgasCount.length())
					{
						stGasCount = stGasCount + "0";
					}
				}
				stGasCount = stGasCount + tempgasCount;
				for (int i=0; i<7; i++)
				{
					testBufT2[i+6] = (byte) stGasCount.charAt(i);
				}
		
				for (int j=0; j<7; j++)
				{
					if (8-j > temppayCoutn.length())
					{
						stPayCoutn = stPayCoutn + "0";
					}
				}
				stPayCoutn = stPayCoutn + payCoutn;
				for (int i=0; i<8; i++)
				{
					testBufT2[i+13] = (byte) stPayCoutn.charAt(i);
				}
				testBufT2[21] = Command.ETB;
				crcCheckVltemp = makeCRC(testBufT2);
				testBufT2[22] = crcCheckVltemp[0];
				testBufT2[23] = crcCheckVltemp[1];
				testBufT2[24] = crcCheckVltemp[2];
				testBufT2[25] = crcCheckVltemp[3];
				testBufT2[26] = Command.ETX;
				testBufT2[27] = makeSum(testBufT2);
				
				testBufTA[0] = Command.SOH;
				testBufTA[1] = nozNum[0];
				testBufTA[2] = nozNum[1];
				testBufTA[3] = Command.STX;
				testBufTA[4] = 'T';
				testBufTA[5] = 'A';
				
				testBufTA[6] = '0';
				testBufTA[7] = '0';
				testBufTA[8] = '0';
				testBufTA[9] = '0';
				testBufTA[10] = '0';
				testBufTA[11] = '0';
				testBufTA[12] = '0';
				testBufTA[13] = '0';
				testBufTA[14] = '0';
				testBufTA[15] = '0';
				
				testBufTA[16] = Command.ETB;
				crcCheckVltemp = makeCRC(testBufTA);
				testBufTA[17] = crcCheckVltemp[0];
				testBufTA[18] = crcCheckVltemp[1];
				testBufTA[19] = crcCheckVltemp[2];
				testBufTA[20] = crcCheckVltemp[3];
				testBufTA[21] = Command.ETX;
				testBufTA[22] = makeSum(testBufTA);
//				LogUtility.getPumpALogger().debug(new String(readBuffer));
//				Show.datas(testBuf, 28, 9) ;
				
//				numBytes = 29;
//				readBuffer = testBufT2;
				if (hitStart == true)
				{
					if (tempCheck < 1)
					{
						readBuffer = testBufTA;
						numBytes = 23;
						tempCheck++;
						sjChk = true;
	//					Show.datas(readBuffer, 23, 9) ;
					} else {
						readBuffer = testBufT2;
						numBytes = 28;
	//					Show.datas(readBuffer, 28, 9) ;
					}
				} else {
					numBytes = 4;
					
					readBuffer = this.sendBufAK.clone();
//					Show.datas(sendBufAK, 4, 4) ;
				}
				////������ �����׽�Ʈ �� �ڵ� == �׽�Ʈ �Ϸ��� ���� ���� == 
				
//				LogUtility.getPumpALogger().debug (numBytes);
				
				
				if (numBytes > 3) //���ŵ� ����Ʈ ���� 4����Ʈ �̸��̸� ���������� ���� �������� ó���Ѵ�. 
				{
					
//					Tempstr = new String(readBuffer);
//					LogUtility.getPumpALogger().debug (nozNum[0]+nozNum[1]+" request : "+Tempstr+", Num Bytes : "+numBytes+"\n\n");
					
					if (recvText(readBuffer, numBytes) != 0)
					{
						recvChk = false;
						recvCnt++;
					}
					else
					{
						recvChk = true;
						recvCnt = 2; //���� ���� ��ɿ� ���� ������ ���ŵǾ������� recvChk�� true, commCnt�� 2�� �����Ͽ� ���� ����
						Tempstr = new String(readBuffer);
//						
//						LogUtility.getPumpALogger().debug ("----RECV----RECV----RECV----RECV----");
						LogUtility.getPumpALogger().debug ("receive : "+Tempstr.trim() +", Num Bytes : "+numBytes+"\n\n");

					}
					
//					numBytes = recvText(RxBuf);
//					Tempstr = new String(RxBuf);
//					LogUtility.getPumpALogger().debug (nozNum[0]+nozNum[1]+" request : "+Tempstr);
					
				}
				else
				{
					recvChk = false;
					recvCnt++;
					LogUtility.getPumpALogger().debug("dosen't buffer : "+ new String(nozNum)+ ", Count : "+recvCnt+", Error count : "+erTotal);
					erTotal++;
					if (erTotal == 12) // �ѽõ� ȸ�� 12ȸ �̸� ������ �����ϰ� ���� ����� ����ǵ��� ��.
					{
						S8_WorkingMessage wmS8 = new S8_WorkingMessage();
						wmS8.setDeviceType("04");
						wmS8.setConnectNozzleNo(tmpNoz);
						wmS8.setNozzleNo(tmpNoz);
						wmS8.setStatus("1");
						wmS8.setStatusCode("303");
						wmS8.setErrMsg("                    ");
						currentTime = new Date ( );
						dTime = formatter.format ( currentTime );
						wmS8.setDetectTime(dTime);
						insertRecvQueue(wmS8);  //������ ��� ��� ���¸� ���� ��.
						
						recvChk = true;
						recvCnt = 2;
						erTotal = 0;
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//commCnt = commCnt + 1;
			//LogUtility.getPumpALogger().debug ("commCnt : "+commCnt);
		}
		
//		recvText(RxBuf);
//		Tempstr = new String(RxBuf);
//		LogUtility.getPumpALogger().debug (nozNum[0]+nozNum[1]+" request : "+Tempstr);
		
		recvCnt = 0; //�ݺ�ȸ�� �ʱ�ȭ
	}
	
	public void run() {
		// TODO Auto-generated method stub
		
	}

	
	
	
	
	//Kixxhub -> ODT
//	DataStruct msC1 = OdtDS.getDS("C1");	//ODT ȣ��
//	DataStruct msC2 = OdtDS.getDS("C2");	//��ī�� ����, �ǸŽ��� ����
//	DataStruct msC3 = OdtDS.getDS("C3");	//�ߺ�������ȣ ����
//	DataStruct msC4 = OdtDS.getDS("C4");	//�����Ǹŵ����� ��û
//	DataStruct msC5 = OdtDS.getDS("C5");	//��ȯ���Աݰ�� ���� -- �̻��
//	DataStruct msC6 = OdtDS.getDS("C6");	//�ſ�ī�� ��� ����
//	DataStruct msC7 = OdtDS.getDS("C7");	//���ʽ�ī�� ����������� ����
//	DataStruct msC8 = OdtDS.getDS("C8");	//���ڻ�ǰ�� ���ΰ�� ����
//	DataStruct msCB = OdtDS.getDS("CB");	//ODT ����
//	DataStruct msCC = OdtDS.getDS("CC");	//���漭 ��Ż������ ��û
//	DataStruct msCD = OdtDS.getDS("CD");	//ȯ�漳�� - ������ ����
//	DataStruct msCE = OdtDS.getDS("CE");	//ȯ�漳�� - �⺻������ �μ⳻��
//	DataStruct msCF = OdtDS.getDS("CF");	//ȯ�漳�� - ������ �μ⳻��
//	DataStruct msCG = OdtDS.getDS("CG");	//ȯ�漳�� - ��ī�� �μ⳻��
//	DataStruct msCH = OdtDS.getDS("CH");	//ȯ�漳�� - ���ʽ�ī�� �μ⳻��
//	DataStruct msCI = OdtDS.getDS("CI");	//ȯ�漳�� - ������� �μ⳻��
//	DataStruct msCJ = OdtDS.getDS("CJ");	//ȯ�漳�� - ������������(�������� ������)
//	DataStruct msCK = OdtDS.getDS("CK");	//������ ������ ����
//	DataStruct msCN = OdtDS.getDS("CN");	//POS �������� ����
//	DataStruct msCO = OdtDS.getDS("CO");	//������ ��ȣ �߱�
//	DataStruct msCP = OdtDS.getDS("CP");	//���ʽ� ī�� ����Ʈ ��
//	DataStruct msBB = OdtDS.getDS("BB");	//���ʽ� ��������/���ݿ����� ���� (���)
	
	//ODT -> Kixxhub
	
	
	public boolean sendText(byte[] buf) throws Exception, SerialConnectException {
		
		try {
			os.write(buf);
			
		} catch (IOException e) {
			throw new SerialConnectException("error - Send stream");
		}
		return true;
	}
	
	public void serialEvent(SerialPortEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	
//	byte[]	sendBufCD = new byte[197];
//	byte[] 	TempBuf;
//	byte 	teACK = 0x20;
//	byte	sumcheck = 0;
//	String	CRCtoInt = "";
//	sendBufCD[0] = Command.SOH;
//	sendBufCD[1] = nozNum[0];
//	sendBufCD[2] = nozNum[1];
//	sendBufCD[3] = Command.STX;
//	sendBufCD[4] = 'C';
//	sendBufCD[5] = 'D';
//	Tempstr = "Test Charger2";
//	TempBuf = Tempstr.getBytes();
//	
//	for (int i=0; i<TempBuf.length; i++)
//	{
//		sendBufCD[i+6] =  TempBuf[i];
//	}
//	for (int i=6+TempBuf.length; i<46; i++)
//	{
//		sendBufCD[i] =  ' ';
//	}
//
//	sendBufCD[46] = '1';
//	sendBufCD[47] = '2';
//	sendBufCD[48] = '3';
//	sendBufCD[49] = '-';
//	sendBufCD[50] = '1';
//	sendBufCD[51] = '2';
//	sendBufCD[52] = '-';
//	sendBufCD[53] = '1';
//	sendBufCD[54] = '2';
//	sendBufCD[55] = '3';
//	sendBufCD[56] = '4';
//	sendBufCD[57] = '5';
//	
////	try {
////		Tempstr = new String("����ȣ".getBytes(), "ISO-8859-1");
////	} catch (UnsupportedEncodingException e1) {
////		// TODO Auto-generated catch block
////		e1.printStackTrace();
////	}
//	Tempstr = "����ȣ";
//	TempBuf = Tempstr.getBytes();
//	for (int i=0; i<TempBuf.length; i++)
//	{
//		sendBufCD[i+58] =  TempBuf[i];
//	}
//	for (int i=58+TempBuf.length; i<88; i++)
//	{
//		sendBufCD[i] =  ' ';
//	}
//	
////	try {
////		Tempstr = new String("ű����� �ּ� ����� ���ʱ�".getBytes(), "");
////	} catch (UnsupportedEncodingException e1) {
////		// TODO Auto-generated catch block
////		e1.printStackTrace();
////	}
//	Tempstr = "ű����� �ּ� ����� ���ʱ�";
//	TempBuf = Tempstr.getBytes();
//	for (int i=0; i<TempBuf.length; i++)
//	{
//		sendBufCD[i+88] =  TempBuf[i];
//	}
//	for (int i=88+TempBuf.length; i<138; i++)
//	{
//		sendBufCD[i] =  ' ';
//	}
//	
//	Tempstr = "02-2189-6793";
//	TempBuf = Tempstr.getBytes();
//	for (int i=0; i<TempBuf.length; i++)
//	{
//		sendBufCD[i+138] =  TempBuf[i];
//	}
//	for (int i=138+TempBuf.length; i<154; i++)
//	{
//		sendBufCD[i] =  ' ';
//	}
//
////	try {
////		Tempstr = new String("���� ��ź".getBytes(), "");
////	} catch (UnsupportedEncodingException e1) {
////		// TODO Auto-generated catch block
////		e1.printStackTrace();
////	}
//	Tempstr = "���� ��ź";
//	TempBuf = Tempstr.getBytes();
//	for (int i=0; i<TempBuf.length; i++)
//	{
//		sendBufCD[i+154] =  TempBuf[i];
//	}
//	for (int i=154+TempBuf.length; i<184; i++)
//	{
//		sendBufCD[i] =  ' ';
//	}
//	sendBufCD[184] = '1';
//	sendBufCD[185] = '2';
//	sendBufCD[186] = '3';
//	sendBufCD[187] = '0';
//	sendBufCD[188] = '1';
//	sendBufCD[189] = '2';
//	
//	sendBufCD[190] = Command.ETB;
//	CRCtoInt = Integer.toHexString(CRCCheck.crc16(sendBufCD, 191)).toUpperCase();
//	numBytes = 0;
//	for (int i=0; i<4; i++)
//	{
//		if (3-i < CRCtoInt.length())
//		{
//			sendBufCD[i+191] = (byte) CRCtoInt.charAt(numBytes);
//			numBytes = numBytes + 1;
//		}
//		else
//		{
//			sendBufCD[i+191] = '0';
//		}
//	}
//	sendBufCD[195] = Command.ETX;
//	sumcheck = 0;
//	for (int i = 1; i < 196; i++)
//	{
//		sumcheck = (byte) (sumcheck ^ sendBufCD[i]);
//	}
//	teACK = 0x20;
//	sumcheck = (byte) (sumcheck | teACK);
//
//	sendBufCD[196] = sumcheck;
//	
//	sendText(sendBufCD);
}


