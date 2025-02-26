package com.gsc.kixxhub.device.pumpa.driver;

import gnu.io.CommPortOwnershipListener;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Vector;

import com.gsc.kixxhub.common.data.pump.EX_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.HF_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P3_1_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.P7_WorkingMessage;
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
import com.gsc.kixxhub.device.pumpa.common.DataStruct;
import com.gsc.kixxhub.device.pumpa.common.Log;
import com.gsc.kixxhub.device.pumpa.common.SerialConnectException;
import com.gsc.kixxhub.device.pumpa.common.Sleep;
import com.gsc.kixxhub.device.pumpa.controller.DevInfo;
import com.gsc.kixxhub.device.pumpa.define.CCommDriver;
import com.gsc.kixxhub.device.pumpa.translation.TransTokico;

public class CommTokico extends CCommDriver implements SerialPortEventListener, CommPortOwnershipListener, 
	Runnable {

	protected class LubrStatus	{
		boolean	beforeNozzleUp=false;
		boolean firstPumpingData=true;
		byte m_byLastPrice[] = new byte[5];
		byte m_byNozzleID;
		byte m_byNozzleReadState;
		byte[] m_byTotGauge = new byte[10]; // AAAAAAA
		int	m_iNozzle;
		int m_nAmount;
		int m_nBasePrice;
		int m_nLastPrice=0;		
		int m_nNozzleState;
		int m_nPlusPrice=0;
		int m_nQty;

	    int m_nTGauge;
	    String m_sBasePrice="000000";
	    boolean presetDataFlag=false;
	    boolean	pumpingStart=false;
	    boolean sentNozzleDownInfo=false;
	    boolean sentPumpingEndInfo=false;
	    boolean sentPumpingStartInfo=false;
	}
	protected static boolean m_isSaveSTX=false;
	protected byte ACK = 0x06;
	protected int	AN_LN=2;
	protected int	baseMinErrCnt=8, baseMaxSkipCnt=100;
	protected int	baseReadBuffInterval   = 30+8; // 스마트주유소 PJT 보정
	protected int	baseReadStartInterval  = 20+5; // 스마트주유소 PJT 보정
	
    protected int	baseWriteStartInterval = 20+5; // 스마트주유소 PJT 보정
    protected int		buffSize = 500;
    protected int	dispLevel=0;
    protected byte[] 	e0_Buf = new byte[26];
    protected DataStruct e0_ds = new DataStruct();
    protected byte ENQ = 0x05;
    protected byte EOT = 0x04;
    
	protected byte ETX = 0x03;
	protected boolean firstPumpingData=true;
	protected int	  g_nRealPumpCnt=6;  // 캡처보드당 최대 지원 노즐수 = 6개
	protected int	HD_LN=1;
	
	protected HF_WorkingMessage HF_wm;
	protected boolean	issueLineErr=true;
	protected byte[] 	lastPumpingData=new byte[buffSize];
	protected byte[] 	lastStateData=new byte[10];

	protected int		lineErrCnt=0;
	protected int	 m_baseNozNo;
    protected boolean m_bQueryState;
	protected byte m_byACKENQ[] = new byte[2];
	protected byte m_byACKEOT[] = new byte[2];
	protected byte m_byACKNUL[] = new byte[2];
	protected byte m_byACKSOH[] = new byte[2];
	//protected byte BASE_NOZZLE_ID = 0x41;
	protected byte m_byENQ[] = new byte[1];
	protected byte m_bytSOH[] = new byte[2];
	protected Vector<LubrStatus> m_pLubrStatus = new Vector<LubrStatus>();
	
    protected String 	m_sRcvStatus="";
    protected byte NAK = 0x15;
	
	protected byte		nozID;
	protected short		nozState=0;
	protected String 	nozStr = "";
	protected byte NUL = 0x00;
	protected int	NZ_LN=2;
	protected String presetBasePrice="000000";
	protected boolean presetDataFlag=false;
	
	protected String presetLiter="0000000";
	
	protected String presetPrice="000000";
	// Preset data keeping
	protected String presetType="2";
	//progressStep : 0=초기화전, 1=초기화후, 2=상태정보송신후, 3=주유중, 4=주유완료, 
    //               5=토털게이지수신/주유완료자료 송신
    protected int 	progressStep=3;
	protected boolean pumpingStart=false;
	
	protected boolean rtn;
	protected byte[] 	RxBuf = new byte[buffSize];
		
	//protected S3_WorkingMessage S3_wm;
	protected S4_WorkingMessage S4_wm;;
	
	protected S5_WorkingMessage S5_wm;

	protected S8_WorkingMessage S8_wm;
	protected SE_WorkingMessage SE_wm;
	protected boolean sentPumpingStartInfo=false;
	protected SJ_WorkingMessage SJ_wm;
	protected byte SOH = 0x01;
	protected byte STX = 0x02;
	protected int	TG_LN=2;
	
	protected TransTokico trans;
    
	protected byte[] 	TxBuf = new byte[buffSize];
	
    public CommTokico (int nozNum, String romVerStr, Hashtable<String, DevInfo> devTbl) { //생성자
    	
    	byte	bcc;
		Formatter form = new Formatter();
		byte[] 	nozByt = new byte[2];
		
		nozNo = nozNum;
		romVer = romVerStr;

		form.format("%02d", nozNo);
		nozStr = form.toString();
		nozByt = nozStr.getBytes();
    	nozID = (byte) (nozNo + 0x40);
    	
		readBuffInterval   = baseReadBuffInterval;
		readStartInterval  = baseReadStartInterval;
		writeStartInterval = baseWriteStartInterval;
		minErrCnt 		   = baseMinErrCnt;
		maxSkipCnt 		   = baseMaxSkipCnt;
    	
    	// 도끼꼬구형 Base Nozzle No(=lowerNozNo) 계산
		try {
			m_baseNozNo = getLowerNozNo(devTbl);
			g_nRealPumpCnt = devTbl.size();
			setSize(g_nRealPumpCnt);
		} catch (Exception e) {
			// TODO 자동 생성된 catch 블록
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}

    	trans = new TransTokico(m_baseNozNo);
    	
    	try {
			//--- e0 (주유허가, 정액/정량설정)---//
			e0_ds.addByte("nozNo", nozID);
			e0_ds.addByte("STX", STX);
			e0_ds.addString("nozStr", nozStr, 2);
			e0_ds.addString("command", "e0", 2);
			e0_ds.addByte("mode", (byte) '0');
			e0_ds.addString("liter", "0000000", 7);
			e0_ds.addString("basePrice", "1111", 4);
			e0_ds.addString("price", "000000", 6);
			e0_ds.addByte("ETX", ETX);
			e0_ds.addByte("bcc", (byte) ' ');
			e0_Buf = e0_ds.getByteStream();	
    	} catch (Exception e) {
    		LogUtility.getPumpALogger().error(e.getMessage(), e);
    	}

    	m_byENQ[0] = ENQ;

    	m_byACKNUL[0] = ACK; 
    	m_byACKNUL[1] = NUL;

    	m_byACKEOT[0] = ACK;
    	m_byACKEOT[1] = EOT;

    	m_byACKENQ[0] = ACK; 
    	m_byACKENQ[1] = ENQ;

    	m_byACKSOH[0] = ACK; 
    	m_byACKSOH[1] = SOH;

    	m_bytSOH[0] = 't';
    	m_bytSOH[1] = SOH;
    	
    	try {
			setSize(6);
		} catch (Exception e) {
			// TODO 자동 생성된 catch 블록
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
    	
    }
    
	protected int calculateBasePrice(int i , int price, int liter) throws Exception {
		
		int   basePrice=0;
		
		// price=읽은값, m_nLastPrice=이전값
		if (price - m_pLubrStatus.get(i).m_nLastPrice <= -90000)
			m_pLubrStatus.get(i).m_nPlusPrice += 100000;
		
		if (liter>0)
			basePrice = (price+m_pLubrStatus.get(i).m_nPlusPrice) / liter;
		
		//LogUtility.getPumpALogger().debug("\ni="+i+" Price="+price+" Liter="+liter+ " diff="+(price-m_pLubrStatus.get(i).m_nLastPrice));
		//LogUtility.getPumpALogger().debug("m_nLastPrice="+m_pLubrStatus.get(i).m_nLastPrice+" m_nPlusPrice="+m_pLubrStatus.get(i).m_nPlusPrice+
		//		" nBasePrice=" + basePrice);

		m_pLubrStatus.get(i).m_nLastPrice = price;

		return basePrice;
	}

	protected boolean compareBCC(byte[] buf) throws Exception {
		int	i;
	
		for (i=0; i<buf.length-2; i++) {
			if (buf[i]==ETX) 
				break;
		}
		
		if (getBCC (buf) == buf[i+1])		
			return true;
		else
			return false;
	}
	
	public byte[] generateByteStream (WorkingMessage wm) throws Exception {		
		return trans.generateByteStream(wm);
	}
	
	public WorkingMessage generateWorkingMessage (byte[] state, byte[] data, 
			String command, int i) throws Exception {

		WorkingMessage wm = new WorkingMessage();
		wm = trans.generateWorkingMessage(state, data, command, i);

		//System.out.printf ("Command=%s, i=%d\n", command, i);
		return wm;
	}
	
	public WorkingMessage generateWorkingMessage (int price, int liter, byte[] totGauge, 
			String command, int i) throws Exception {

		WorkingMessage wm = new WorkingMessage();
		wm = trans.generateWorkingMessage(price, liter, totGauge, command, i);

		//System.out.printf ("Command=%s, i=%d\n", command, i);
				
		return wm;
	}

	protected byte getBCC(byte[] dat) throws Exception {
		
		byte 	bcc=0;
		int 	stxIdx=0, etxIdx=0, i, j;
		
		for (i=0; i<dat.length-1; i++) {
			if (dat[i]==STX) 
				break;
		}
		stxIdx = i + 1; // start from STX + 3
		
		for(j=i; j<dat.length-1; j++) {
			if (dat[j]==ETX)
				break;
		}
		etxIdx = j;
		
		//LogUtility.getPumpALogger().debug("length="+dat.length+", stxIdx="+stxIdx+", etxIdx="+etxIdx);
		//Show.datas(dat, dat.length, 20);

		if (stxIdx<=0 || etxIdx<=0) // No exist STX, ETX
			return -1;
		
		//--- Calculate BCC
		for (i=stxIdx; i<=etxIdx; i++) {
			bcc = (byte) (bcc ^ dat[i]); // XOR
		}
		
		return bcc;
	}

	protected int getLowerNozNo (Hashtable<String, DevInfo> devTbl) throws Exception {
		
		int lowerNozNo=999;
		Enumeration<String> nozNoEnum = devTbl.keys();
		
		while (nozNoEnum.hasMoreElements()) {
			String szNozNo = nozNoEnum.nextElement();
			int		nNozNo = Change.toValue(szNozNo);
			if (nNozNo < lowerNozNo)
				lowerNozNo = nNozNo;
		}
		return lowerNozNo;
	}
	
	protected int getPumpingNozzleCnt(byte flag) throws Exception {
		int cnt=0;
		byte b=0x01;
		
		for (int i=0; i<6; i++) {
			if ((flag & b) != 0)
				cnt++;
			b = (byte) (b << 1);
		}
		
		return cnt;
	}
	
	protected boolean isPumpingNow(byte flag, int index) throws Exception {
		
		byte b=0x01;

		b = (byte) (b << index);
		
		return ((flag & b) != 0? true: false);
	}
	
	protected boolean isSkipWorkingMessage (WorkingMessage wm) throws Exception {
		
		boolean skip=false;
		String bPrice;
				
		if (wm.getCommand().equals("QF")) { // 프리셋 자료 요청
			
			skip = true;
		}
		else if (wm.getCommand().equals("PB")) { // 정액정량 설정
			
			// 단가를 e0_ds에 저장한다.
			PB_WorkingMessage PB_wm = (PB_WorkingMessage) wm;
			e0_ds.editString("liter", "0000000" , 7);
			bPrice = PB_wm.getBasePrice();
			e0_ds.editString("basePrice", bPrice.substring(0, 4), 4);
			e0_ds.editString("price", "000000" , 6);
			e0_Buf = e0_ds.getByteStream();
						
			LogUtility.getPumpALogger().info("[Pump A][수신-주유허가]" + " nozzle=" + 
					PB_wm.getNozzleNo() + "("+PB_wm.getCommand()+")" +
					" liter =" + PB_wm.getLiter() +
					" | bPrice=" + PB_wm.getBasePrice() +
					" | price =" + PB_wm.getPrice());
			
			skip = false;
		}
		else if (wm.getCommand().equals("P3_1")) { // 주유기 환경설정

			// 단가를 e0_ds에 저장한다.
			
			P3_1_WorkingMessage P3_wm = (P3_1_WorkingMessage) wm;

			if (nozNo != m_baseNozNo) {
				EX_WorkingMessage EX_wm = new EX_WorkingMessage();
				EX_wm.setCommand("EX");
				EX_wm.setPassThrough(false);
				EX_wm.setNozzleNo(P3_wm.getNozzleNo());
				EX_wm.setConnectNozzleNo(P3_wm.getNozzleNo());
				EX_wm.setTargetNozzleNo(Change.toString("%02d", m_baseNozNo));
				EX_wm.setSubCommand("P3"); // 주유허가
				EX_wm.setBasePrice(P3_wm.getBasePrice()); 
				insertRecvQueue(EX_wm);
				
				//LogUtility.getPumpALogger().debug("EX 전문 송신하였습니다. nozNo=" + EX_wm.getNozzleNo()
						//+ " tarNoz="+EX_wm.getTargetNozzleNo()+" baseNoz=" + EX_wm.getTargetNozzleNo()+
						//" BasePrice="+EX_wm.getBasePrice());
			}
			else {
				int i = Change.toValue(P3_wm.getNozzleNo()) - m_baseNozNo;
				m_pLubrStatus.get(i).m_sBasePrice = P3_wm.getBasePrice();
	
				//LogUtility.getPumpALogger().debug("Recv P3_wm i="+i+" noz="+P3_wm.getNozzleNo()+
						//" basePrice="+P3_wm.getBasePrice());
				//LogUtility.getPumpALogger().debug("m_szBasePrice="+m_pLubrStatus.get(i).m_sBasePrice);
			}
										
			skip = true;
		}
		else if (wm.getCommand().equals("EX")) { // 정보교환 전문

			EX_WorkingMessage EX_wm = (EX_WorkingMessage) wm;
			
			if (EX_wm.getSubCommand().equals("P3")) {
				int i = Change.toValue(EX_wm.getConnectNozzleNo()) - m_baseNozNo;
				m_pLubrStatus.get(i).m_sBasePrice = EX_wm.getBasePrice();
	
				//LogUtility.getPumpALogger().debug("Recv EX_wm i="+i+" noz="+EX_wm.getNozzleNo()+" conNoz="+EX_wm.getConnectNozzleNo()+
						//" basePrice="+EX_wm.getBasePrice());
				//LogUtility.getPumpALogger().debug("m_szBasePrice="+m_pLubrStatus.get(i).m_sBasePrice);
			}
			
			skip = true; // 추가(2010/04/20)
		}
		else if (wm.getCommand().equals("P7")) { // 주유기 파라미터 설정

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
			
			if (lineErrSkipCnt >= 9005)
				m_isSaveSTX = true;
			
			skip = true;
		}
		
		return skip;
	}
	
	protected void makeLineError () throws Exception {
		
		SE_wm = new SE_WorkingMessage();
		
		SE_wm.setNozzleNo(Change.toString("%02d", nozNo));
		SE_wm.setDeviceType(Change.toString("%02d", nozType));
		SE_wm.setStatus("1");
		SE_wm.setStatusCode("601"); // 회선불량
		SE_wm.setErrMsg("주유기 회선불량");

		Calendar cal = new GregorianCalendar();
		Formatter form = new Formatter();
		String year = Change.toString("%04d", cal.get(Calendar.YEAR));
		form.format("%s%02d%02d%02d%02d%02d", year.substring(2, 4),
				cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH), 
				cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), 
				cal.get(Calendar.SECOND));
		String timeStr = form.toString();
		SE_wm.setDetectTime(timeStr);
		
		insertRecvQueue(SE_wm);
	}
	
	protected void makeStatusInfo(int nozState, int i) throws Exception {

		S8_wm = new S8_WorkingMessage();
		int	statusCode=0;
		String errorMsg="";
		
		switch (nozState) {
			case 0:
			case 2:
				statusCode = 651; // 노즐다운
				errorMsg = "노즐다운";
				break;
			case 1:
			case 3:
				statusCode = 652; // 노즐업
				errorMsg = "노즐업";
				break;
			case 4:
				statusCode = 653; // 주유중
				errorMsg = "주유중";
				break;
			case 5:
				statusCode = 654; // 주유완료
				errorMsg = "주유완료";
				break;
		}
		
		S8_wm.setNozzleNo(Change.toString("%02d", m_baseNozNo + i));
		S8_wm.setDeviceType(Change.toString("%02d", nozType));
		S8_wm.setStatus("1");
		S8_wm.setStatusCode(Change.toString("%03d", statusCode)); 
		S8_wm.setErrMsg(errorMsg);
		
		Calendar cal = new GregorianCalendar();
		Formatter form = new Formatter();
		String year = Change.toString("%04d", cal.get(Calendar.YEAR));
		form.format("%s%02d%02d%02d%02d%02d", year.substring(2, 4),
				cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH), 
				cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), 
				cal.get(Calendar.SECOND));
		String timeStr = form.toString();
		S8_wm.setDetectTime(timeStr);

		insertRecvQueue(S8_wm);
	}
	
	public void ownershipChange(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
	protected void processRecvSTX (byte[] lpbyStatus, byte[][] byPumpData, int nLen) throws Exception {
		
		int n2Cnt=0, n3Cnt=0, n4Cnt=0;
		
		try	{
			
			for (int i=0; i<g_nRealPumpCnt; i++) {

				switch (lpbyStatus[i]) {

				case '2':

					if (m_pLubrStatus.get(i).sentNozzleDownInfo==false) {
						
						makeStatusInfo(0, i); // 상태정보 전송(노즐다운)
											
						if (m_pLubrStatus.get(i).beforeNozzleUp==true &&
							m_pLubrStatus.get(i).pumpingStart==false &&  
							m_pLubrStatus.get(i).sentPumpingEndInfo==false) { // 정상 주유완료자료가 아니면
							
							S4_wm = new S4_WorkingMessage();
							S4_wm.setNozzleNo(Change.toString("%02d", m_baseNozNo + i));
							S4_wm.setLiter("0000000");
							//S4_wm.setBasePrice(m_basePrice);
							S4_wm.setPrice("00000000");
							S4_wm.setTotalGauge("0000000000"); // 수정(2008/09/06)
							
							insertRecvQueue(S4_wm); // 주유완료 자료
							
							//m_pLubrStatus.get(i).sentPumpingEndInfo=false;
							m_pLubrStatus.get(i).sentPumpingEndInfo=true; // 수정(2010/04/20)
							m_pLubrStatus.get(i).sentPumpingStartInfo=true; // BBBBB
							m_pLubrStatus.get(i).m_nLastPrice=0;
							m_pLubrStatus.get(i).m_nPlusPrice=0;
							
							LogUtility.getPumpALogger().info("nozzle=" + (m_baseNozNo+i) + 
									" -> 노즐업다운, 선결제 주유취소용 주유완료전문(S4) 생성");
						}
						m_pLubrStatus.get(i).beforeNozzleUp=false;
						
						m_pLubrStatus.get(i).sentNozzleDownInfo=true;
						m_pLubrStatus.get(i).sentPumpingStartInfo=false;
					}
					
					n2Cnt++;
					m_pLubrStatus.get(i).m_byNozzleReadState = 2;
					
					if (m_pLubrStatus.get(i).m_nNozzleState==3) { // 3 -> 2 
						
						m_pLubrStatus.get(i).m_nNozzleState = 4; // 주유중
						
						byte[] byTemp = new byte[10];
						flushBuffer(byTemp);
						
					}
					else if (m_pLubrStatus.get(i).m_nNozzleState==4) { // 4 -> 2 
						
						m_pLubrStatus.get(i).m_nNozzleState = 5; // 주유완료
											
						if (m_pLubrStatus.get(i).sentPumpingEndInfo == false) {
						
							makeStatusInfo(5, i); // 상태정보 전송(주유완료)
							
							byte[] byTotGauge;
							byTotGauge = recvTotalGuage();	// 토털게이지 수신처리
							
							m_pLubrStatus.get(i).m_byTotGauge = byTotGauge.clone();
	
							if (dispLevel==1 || dispLevel==2) {
								LogUtility.getPumpALogger().info("nozzle=" + (m_baseNozNo+i) +
										" -> 주유완료 자료(4) total-gauge 수신");
								Log.datas (byTotGauge, byTotGauge.length, 20);
							}
	
							S4_WorkingMessage S4_wm = new S4_WorkingMessage();
							S4_wm = (S4_WorkingMessage) generateWorkingMessage(m_pLubrStatus.get(i).m_nAmount, 
									m_pLubrStatus.get(i).m_nQty, byTotGauge, "S4", i);

//							int price = Change.toValue(S4_wm.getPrice());
//							int liter = Change.toValue(S4_wm.getLiter().substring(0,4));
//							int basePrice = calculateBasePrice(i, price, liter);
//							S4_wm.setBasePrice(Change.toString("%04d00", basePrice));
				
							S4_wm.setBasePrice(m_pLubrStatus.get(i).m_sBasePrice);
							insertRecvQueue(S4_wm);
	
							m_pLubrStatus.get(i).pumpingStart=false;
							m_pLubrStatus.get(i).sentPumpingStartInfo=false;
							m_pLubrStatus.get(i).sentPumpingEndInfo=true;
							m_pLubrStatus.get(i).firstPumpingData=true;
							m_pLubrStatus.get(i).m_nLastPrice=0;
							m_pLubrStatus.get(i).m_nPlusPrice=0;
						}
						
					}
					else if (m_pLubrStatus.get(i).m_nNozzleState==5) { 
						
						m_pLubrStatus.get(i).m_nNozzleState = 1; // 노즐다운
						m_pLubrStatus.get(i).sentPumpingStartInfo=false;
	
					}
					else if (m_pLubrStatus.get(i).m_nNozzleState==2) {
						m_pLubrStatus.get(i).m_nNozzleState = 1;
					}
					else
						m_pLubrStatus.get(i).m_nNozzleState = 1;

					break;

				case '3':
					if (m_pLubrStatus.get(i).sentPumpingStartInfo==false) {	
						
						//if (m_pLubrStatus.get(i).sentPumpingStartInfo==false) {

							makeStatusInfo(3, i); // 상태정보 전송(노즐업)
						
							byte[] byTotGague;
							byTotGague=m_pLubrStatus.get(i).m_byTotGauge.clone();
							byTotGague = recvTotalGuage ();	
	
							if (dispLevel==1 || dispLevel==2) {
								LogUtility.getPumpALogger().info("nozzle=" + (m_baseNozNo+i) + 
															" -> 주유시작 자료(3) total-gauge 수신");
								Log.datas (byTotGague, byTotGague.length, 20);
							}
					
							insertRecvQueue(generateWorkingMessage(null, byTotGague, "SJ", i));
							
							m_pLubrStatus.get(i).beforeNozzleUp=true;
							m_pLubrStatus.get(i).sentPumpingStartInfo=true;
							m_pLubrStatus.get(i).sentNozzleDownInfo=false;
							m_pLubrStatus.get(i).sentPumpingEndInfo=false;
						//}
					}
					
					n3Cnt++;
					m_pLubrStatus.get(i).m_byNozzleReadState = 3;
					if (m_pLubrStatus.get(i).m_nNozzleState != 2)	{
						m_pLubrStatus.get(i).m_nNozzleState = 2;
					}
					break;

				case '4': // 주유중 -> 주유자료 읽기로 감
					n4Cnt++;
					m_pLubrStatus.get(i).m_byNozzleReadState = 4;
					
					if (m_pLubrStatus.get(i).m_nNozzleState != 3)
						m_pLubrStatus.get(i).m_nNozzleState = 3;
					
					break;

				default:
					break;
				}	
			}

			
			if (lpbyStatus[6] != NUL) {
				
				int j=0;
				
				for (int i=0; i<g_nRealPumpCnt; i++) {

					if (m_pLubrStatus.get(i).m_byNozzleReadState==4) { // 주유 중
												
						//m_pLubrStatus.get(i).pumpingStart=true;
						
						if (isPumpingNow(lpbyStatus[6], i)) { // 주유중(토출) 확인

							m_pLubrStatus.get(i).pumpingStart=true;
									
							byte[] byNumber=new byte[5];
							flushBuffer(byNumber);
							System.arraycopy(byPumpData[j], 0, byNumber, 0, 5);
							m_pLubrStatus.get(i).m_nAmount = Change.toValue (new String(byNumber)); // price
							
							flushBuffer(byNumber);
							System.arraycopy(byPumpData[j], 5, byNumber, 0, 5);
							m_pLubrStatus.get(i).m_nQty = Change.toValue (new String(byNumber)); // liter

							if (m_pLubrStatus.get(i).m_nNozzleState==3) { 
								
								if (m_pLubrStatus.get(i).firstPumpingData==true) {
									
									m_pLubrStatus.get(i).firstPumpingData=false;
									makeStatusInfo(4, i); // 상태정보 전송(주유중)
								}
								
								S3_WorkingMessage S3_wm = new S3_WorkingMessage();
								S3_wm = (S3_WorkingMessage) generateWorkingMessage(lpbyStatus, 
										byPumpData[j], "S3", i);
								
								S3_wm.setNozzleNo(Change.toString("%02d",m_baseNozNo + i));
/*
								int price = Change.toValue(S3_wm.getPrice());
								int liter = Change.toValue(S3_wm.getLiter().substring(0,4));
								int basePrice = calculateBasePrice(i, price, liter);
								S3_wm.setBasePrice(Change.toString("%04d00", basePrice));
*/	
								S3_wm.setBasePrice(m_pLubrStatus.get(i).m_sBasePrice);
								insertRecvQueue(S3_wm); 
								
								//LogUtility.getPumpALogger().debug("주유중 전문(S3) 전송. nozNo="+S3_wm.getNozzleNo());
							}
							
							j++;
						}
					}
					else if (m_pLubrStatus.get(i).m_byNozzleReadState==2) { // 노즐다운
						
					}
				}
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}

	}

	public int recvText(byte[] RxBuf) throws Exception, SerialConnectException {

		int		numBytes=0, c=0, i=0, loop=0;
		boolean	STXFlag=false;
		byte[]	RxByt = new byte[1];
		int		recvLoopCnt=readBuffInterval/10;
		
		flushBuffer (RxBuf);
		Sleep.sleep(readStartInterval);

		try {
			
			for (c=0; c<RxBuf.length-1; c++) {
			
				RxByt[0] = 0x00;
				
				if (is.read(RxByt, 0, 1) < 1) { // no recv data
					//---check timeout
					if (loop > recvLoopCnt) break;
					else loop++;
					
					c--;
					Sleep.sleep(10);
					continue;
				} else
					loop=0;

				RxBuf[i] = RxByt[0];
				
				if (i>0 && RxBuf[i]==ETX) { // recv ETX next STX
					RxByt[0] = 0x00;
					is.read(RxByt, 0, 1); // read BCC
					RxBuf[i+1] = RxByt[0];
					numBytes=i+2;
					break;
				}
				i++;
			}
			return numBytes;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogUtility.getPumpALogger().error(e.getMessage(), e);
			return 0;
		}
	}
	
	//다쓰노구형에서 적용한것임 -> 사용권장
	public int recvText(byte[] RxBuf, int len) throws Exception, SerialConnectException {

		int		c=0, i=0, loop=0;
		byte[]	RxByt = new byte[1];
		int		recvLoopCnt=10;
		
		flushBuffer (RxBuf);
		Sleep.sleep(readStartInterval);

		try {
			
			for (c=0; c<RxBuf.length; c++) {
				
				RxByt[0] = 0x00;
				
				if (is.read(RxByt, 0, 1) < 1) { // no recv data
					//---check timeout
					if (loop > recvLoopCnt) break;
					else loop++;
					
					c--;
					Sleep.sleep(10);
					continue;
				} else
					loop=0;

				RxBuf[i] = RxByt[0];
				
				i++;			
				if (i >= len)
					break;
			}

			return i;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogUtility.getPumpALogger().error(e.getMessage(), e);
			return 0;
		}
	}
	
	protected byte[] recvTotalGuage() throws Exception, SerialConnectException {
		
		byte[] byTotGauge = new byte[100];
		
		try {
			
			int rtn;
			byte[] byDummy = new byte[2];
			
			for (int i=0; i<3; i++) {
						    	
				sendText(m_bytSOH);
	
				if (m_isSaveSTX==true) 
					LogUtility.getPumpALogger().info ("Send STX("+m_baseNozNo+"/base) : ["+new String(m_bytSOH)+"]");
				else 
					LogUtility.getPumpALogger().debug("Send STX("+m_baseNozNo+"/base) : ["+new String(m_bytSOH)+"]");
				
				Log.datas(m_bytSOH, m_bytSOH.length, 20);
				
				int timeDelay = readStartInterval * 3; // 추가(2010/10/07)$
				Sleep.sleep(30 + timeDelay); // 추가(2010/04/20)
				//Sleep.sleep(30); // 추가(2010/04/20)
				
				int numBytes = recvText(byDummy, 2); // Header -> SOH + 't'
				
				if (numBytes < 1) { // 추가 2010/11/29
					LogUtility.getPumpALogger().info("0.Recv totalGauge fail(no response)! retry...");
					lineErrCnt++;
					continue;
				}
				
				if (byDummy[1] != 't') { // fail
					LogUtility.getPumpALogger().info("0.Recv totalGauge fail(incorrect)! retry...");
					Log.datas(byDummy, byDummy.length, 10);

					byte[] trimBuf = new byte[100]; // 40 -> 60
					is.read(trimBuf); // trim inputStream // 변경 2010/11/29
					lineErrCnt++;
					continue;
				}
				else { // success
					rtn = recvText(byTotGauge); // Gauge Data
					
					if (m_isSaveSTX==true) 
						LogUtility.getPumpALogger().info ("Recv STX("+m_baseNozNo+"/base) : ["+pack(byTotGauge, ETX)+"]");
					else 
						LogUtility.getPumpALogger().debug("Recv STX("+m_baseNozNo+"/base) : ["+pack(byTotGauge, ETX)+"]");
	
					if (rtn < 62) { // 비정상값 수신
						LogUtility.getPumpALogger().info("1.Recv totalGauge fail!");
						Log.datas(byDummy, byDummy.length, 10);
						Log.datas(byTotGauge, byTotGauge.length, 20);
						
						byte[] trimBuf = new byte[100];
						is.read(trimBuf); // trim inputStream
						lineErrCnt++;
					} 
					else if (dispLevel>=2) { // 정상값 수신
						LogUtility.getPumpALogger().info("1.Recv totalGauge success.");
						Log.datas(byDummy, byDummy.length, 10);
						Log.datas (byTotGauge, byTotGauge.length, 20);
					}
					
					break;
				}
			}
			
		} catch (SerialConnectException e) {
			// TODO 자동 생성된 catch 블록
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		} // total gauge 요청
		
		byte byRtnTotGauge[] = new byte[60];
		System.arraycopy(byTotGauge, 0, byRtnTotGauge, 0, 60);
		
		return byRtnTotGauge;
	}
	
	@Override
	public void requestData() throws Exception, SerialConnectException {
	
		int 	numBytes=0;
		boolean	occurLineErr = (lineErrCnt >= minErrCnt);
		
		if (occurLineErr) { // Check line error

			if (issueLineErr==true) {
				makeLineError();
				issueLineErr=false;
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
		
		//--- Transfer from WorkingMessage to BytesStream
		while (sndQue.getItemCount() > 0) {
			WorkingMessage wm = (WorkingMessage)sndQue.deQueue();

			LogUtility.getPumpALogger().debug("\n>>>>> Received  Down data : " +
								"nozNo=" + nozNo + " command=" + wm.getCommand());
			
			if (isSkipWorkingMessage(wm)==true) {
				wm=null;
				continue;
			}
			
			byte[] wmByte = generateByteStream(wm);
			if (wmByte != null) 
				TxQue.enQueue(wmByte);
			/*
			if (wm.getCommand().equals("P8"))
				Show.datas(wmByte, wmByte.length, 20);
			*/
			wm=null;
		}
		
		if (dispLevel>=3)
			LogUtility.getPumpALogger().debug("\nStart request ============> Tokico, baseNozNo=" + m_baseNozNo);
		
		try {
			//##### Send ENQ and Recv ACK/STX #####//
			if (dispLevel>=3)
				LogUtility.getPumpALogger().debug("----POLLING----(progStep="+progressStep+", nozState="+nozState+")");
			
			/*
			// 삭제(2010/04/20)
			if (TxQue.isEmpty()==false) { // 송신 데이터 있으면(Total gauge요청 -> 't'+SOH)
				
				TxBuf = TxQue.getFirstItem();
				
				if(sendText(TxBuf) != true) {
					if (dispLevel>=3) {
						LogUtility.getPumpALogger().info("2.Send STX fail! baseNozNo=" + m_baseNozNo);
					}
					lineErrCnt++;
					TxBuf = TxQue.deQueue();
					return;
				}
								
				//--- recv data---//
				byte byData[] = new byte[2];
				if (recvText(byData, 2) < 1) {
					LogUtility.getPumpALogger().info("0.Recv DAT fail! baseNozNo=" + m_baseNozNo);
					Log.datas(byData, byData.length, 20);
					lineErrCnt++;
					TxBuf = TxQue.deQueue();
					return;
				}
				
				if (dispLevel==3) {
					LogUtility.getPumpALogger().info("0.Recv DAT. baseNozNo=" + m_baseNozNo);
					Log.datas(byData, byData.length, 20);
				}

				//for (int i=0; i<g_nRealPumpCnt; i++) {
				for (int i=0; i<6; i++) { // change ???? -> 6개의 노즐데이터 동시에 처리하게
					
					byte byPumpData[] = new byte[10]; // -> [60] ????
					
					if (recvText(byPumpData, 10) < 1) {
						LogUtility.getPumpALogger().info("1.Recv totalGauge fail! baseNozNo=" + m_baseNozNo);
						Log.datas(byPumpData, byPumpData.length, 20);
						lineErrCnt++;
						TxBuf = TxQue.deQueue();
						return;
					}

					if (dispLevel==1 || dispLevel==2) {
						LogUtility.getPumpALogger().info("1.Recv totalGuage. baseNozNo=" + m_baseNozNo);
						Log.datas(byPumpData, byPumpData.length, 20);
					}

					//ProcessTotalGauge (byPumpData, i);
					insertRecvQueue(generateWorkingMessage(null, byPumpData, "S5", i)); // 토털게이지 전송
				}
				
				TxBuf = TxQue.deQueue();

				if (recvText(byData, 2) < 1) {
					LogUtility.getPumpALogger().info("2.Recv DAT fail! baseNozNo=" + m_baseNozNo);
					Log.datas(byData, byData.length, 20);
					lineErrCnt++;
					return;
				}
				
				if (dispLevel==3) {
					LogUtility.getPumpALogger().info("2.Recv DAT. baseNozNo=" + m_baseNozNo);
					Log.datas(byData, byData.length, 20);
				}
			} */
			
			// 도끼꼬 구형 가상노즐번호 처리용 - m_baseNozzle만 데이터 수신함
			if (nozNo != m_baseNozNo) {
				Sleep.sleep(30); // 추가(2010/05/12)
				return;
			}
			
			//######## Polling start ########//
			if(sendText (m_byENQ) != true) { // fail
				//lineErrCnt++;
				return;
			}
		
			byte[] bySOH = new byte[1];
			
			do {
				if (recvText(bySOH, 1) < 1) {
					LogUtility.getPumpALogger().info("3.Recv SOH fail! baseNozNo=" + m_baseNozNo);
					Log.datas(bySOH, bySOH.length, 20);
					lineErrCnt++;
					return;
				}
			} while (bySOH[0] != SOH);

			if (dispLevel==3) {
				LogUtility.getPumpALogger().info("3.Recv SOH. baseNozNo=" + m_baseNozNo);
				Log.datas(bySOH, bySOH.length, 20);
			}
			
			byte[] byPumpStatus = new byte[7]; // status(6) + terminate(1)
			
			if (recvText(byPumpStatus, 7) < 1) {
				LogUtility.getPumpALogger().info("4.Recv statusData fail! baseNozNo=" + m_baseNozNo);
				Log.datas(byPumpStatus, byPumpStatus.length, 20);
				lineErrCnt++;
				return;
			}
			
			String sRcvStatus = new String(byPumpStatus).substring(0, 6);
			
			if (!sRcvStatus.equals(m_sRcvStatus)) {
				if (m_isSaveSTX==true) 
					LogUtility.getPumpALogger().info ("Recv STX("+m_baseNozNo+"/base) : ["+sRcvStatus+"]");
				else 
					LogUtility.getPumpALogger().debug("Recv STX("+m_baseNozNo+"/base) : ["+sRcvStatus+"]");
			
				m_sRcvStatus = sRcvStatus;
			}
			
			if (dispLevel==2) {
				LogUtility.getPumpALogger().info("4.Recv statusData. baseNozNo=" + m_baseNozNo);
				Log.datas(byPumpStatus, byPumpStatus.length, 20);
			}
					
			byte byFlag = byPumpStatus[6];
			byte[][] byPumpData = new byte[6][10];
			
			
			//boolean isCorrect = verifyPumpingData (); // 추가 2010/11/29
			//-----
			if (byFlag != NUL) {

				int nPumpingNozCnt = getPumpingNozzleCnt(byFlag);

				for (int i=0; i<nPumpingNozCnt; i++) {
					
					if (recvText(byPumpData[i], 10) < 1) {
						LogUtility.getPumpALogger().info("Recv pumpingData fail! i=" + i + "  baseNozNo=" + m_baseNozNo);
						Log.datas(byPumpData[i], byPumpData[i].length, 20);
						return;
					}
					
					Formatter form = new Formatter ();
					form.format("0x%02X", byFlag);
					
					if (m_isSaveSTX==true) 
						LogUtility.getPumpALogger().info ("Recv STX("+m_baseNozNo+"/base) : flag="+form.toString()+
								" i=" + i + " ["+new String(byPumpData[i])+"]");
					else
						LogUtility.getPumpALogger().debug("Recv STX("+m_baseNozNo+"/base) : flag="+form.toString()+
								" i=" + i + " ["+new String(byPumpData[i])+"]");
	
					if (dispLevel==1 || dispLevel==2) {
						LogUtility.getPumpALogger().info("Recv pumpingData. i=" + i + "  baseNozNo=" + m_baseNozNo);
						Log.datas(byPumpData[i], byPumpData[i].length, 20);
					}
				}
			}

			byte[] byRxData = new byte[2];
			
			if (recvText(byRxData, 2) < 1) { // dummy data(ETX+BCC)
				LogUtility.getPumpALogger().info("5.Recv ETX+BCC fail! baseNozNo=" + m_baseNozNo);
				Log.datas(byRxData, byRxData.length, 20);
				return;
			}
					
			byte byEcho[] = new byte[2];
			byEcho[0]=ACK;
			byEcho[1]=byFlag;
			sendText (byEcho);
			//------
			
			
				
			processRecvSTX (byPumpStatus, byPumpData, g_nRealPumpCnt);
				
		} catch (Exception e) {
			LogUtility.getPumpALogger().error("Exception occurr!");
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
		
		lineErrCnt=0; // Normal terminated
	}
	
	public void run() {

	}
	
	public boolean sendText(byte[] buf) throws Exception, SerialConnectException {

		Sleep.sleep(writeStartInterval);
		
	    try {
			os.write(buf);
		} catch (IOException e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
		
		return true;
	}

	public boolean sendText(String txt) throws Exception, SerialConnectException {

		Sleep.sleep(writeStartInterval);
		
	    try {
			os.write(txt.getBytes());
		} catch (IOException e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	
		return true;
	}
		
	// To be invoked when InputStream(is) has a receiving data
	public void serialEvent(SerialPortEvent event) {

	}

	protected void setBCC(byte[] buf) throws Exception {
		int	i;
		
		for (i=0; i<buf.length-2; i++) {
			if (buf[i]==ETX) 
				break;
		}
		
		buf[i+1] = getBCC(buf);
	}

	protected void setSize(int nCnt) throws Exception {
		
		try {			
			for (int i=0; i<nCnt; i++) {

				LubrStatus lubrStatus = new LubrStatus();
				
				lubrStatus.m_iNozzle			= i;
				lubrStatus.m_byNozzleID			= (byte) (m_baseNozNo + i);
				lubrStatus.m_nNozzleState		= 0;
				lubrStatus.m_byNozzleReadState	= 2;
				lubrStatus.m_nQty				= 0;
				lubrStatus.m_nAmount			= 0;
				flushBuffer(lubrStatus.m_byLastPrice);
				lubrStatus.m_nTGauge			= 0;
				
				m_pLubrStatus.add(lubrStatus);
			}

		} catch (SerialConnectException e) {
			// TODO 자동 생성된 catch 블록
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	}
}
