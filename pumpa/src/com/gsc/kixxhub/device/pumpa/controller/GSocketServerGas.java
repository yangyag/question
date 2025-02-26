package com.gsc.kixxhub.device.pumpa.controller;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;

import com.gsc.kixxhub.common.utility.ClearUtil;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.BytesQue2;
import com.gsc.kixxhub.device.pumpa.common.Change;
import com.gsc.kixxhub.device.pumpa.common.Command;
import com.gsc.kixxhub.device.pumpa.common.DataStruct;
import com.gsc.kixxhub.device.pumpa.common.GaLog;
import com.gsc.kixxhub.device.pumpa.driver.CommGSGasODTMr;

/*
 * 프로젝트 : PI2
 * 생성일자 : 2016.12.07 양일준
 * 내용 : TCP/IP 통신방식 충전기 ODT용 Socket 클래스 
 *
 */
public class GSocketServerGas {

	public GServerService service;
	
	protected int BUFFER_SIZE = 4096;
	protected int chkchk = 0;
	//protected long currTime;
	protected Thread m_thread = null;
	protected boolean m_isRunning = false;
	protected boolean m_isStartedServer = false;
	protected String host;
	protected int port;
	public Selector selector = null;
	protected ServerSocketChannel serverSocketChannel;
	protected ServerSocket serverSocket = null;
	protected SocketChannel m_sockChannel = null;
	protected SelectionKey m_key = null;
	protected boolean odtInitCompleted = false;
	protected boolean isConnected = false;
	protected SocketChannel m_sc = null;
	protected String clientIP = "";
	protected int clientPort = 0;
	protected String ODTNo = "";
    protected DataStruct verREQ_ds = new DataStruct(); // Version REQ
    protected DataStruct verACK_ds = new DataStruct(); // Version ACK
    protected DataStruct inputReady_ds = new DataStruct(); // InputReady_ds
    protected byte[] dataACK_Buf;
	public BytesQue2 TxQue = new BytesQue2(30);
	public BytesQue2 RxQue = new BytesQue2(30);
	protected String source = "SC01";
	protected String dest = "OD02";
	protected int tickCnt = 0;  
	
	public boolean canSendC1 = true;  // C1전송여부 플래그 (전문 송신 후 일정시간동안 C1을 보내지 않고 대기함)
	
	private Hashtable<String, Integer> logCount = new Hashtable<String, Integer>();

	public GSocketServerGas(int headerSize, int bufferSize) {
		try {

			// 초기화 명령 패킷 
			// Version REQ
			verREQ_ds.addByte  ("SOH", (byte) 0x01);
			verREQ_ds.addString("Source", source, 4);
			verREQ_ds.addString("Destination", "0000", 4);
			verREQ_ds.addString("PacketGroup", "01", 2);
			verREQ_ds.addString("Length", "00000024", 8); // Length = STX ~ ETX	
			verREQ_ds.addByte  ("STX", (byte) 0x02);
			verREQ_ds.addString("ODTNo", "", 2);
			verREQ_ds.addString("Code", "0001", 4);
			verREQ_ds.addString("VerData", "", 16);
			verREQ_ds.addByte  ("ETX", (byte) 0x03);
			verREQ_ds.addString("CRC", "", 4); // 4 bytes

			// Version ACK
			verACK_ds.addByte  ("SOH", (byte) 0x01);
			verACK_ds.addString("Source", source, 4);
			verACK_ds.addString("Destination", "0000", 4);
			verACK_ds.addString("PacketGroup", "01", 2);
			verACK_ds.addString("Length", "00000008", 8); // Length = STX ~ ETX
			verACK_ds.addByte  ("STX", (byte) 0x02);
			verACK_ds.addString("ODTNo", "", 2);
			verACK_ds.addString("Code", "0002", 4);
			verACK_ds.addByte  ("ETX", (byte) 0x03);
			verACK_ds.addString("CRC", "", 4); // 4 bytes

			// Input Ready
			inputReady_ds.addByte  ("SOH", (byte) 0x01);
			inputReady_ds.addString("Source", source, 4);
			inputReady_ds.addString("Destination", "0000", 4);
			inputReady_ds.addString("PacketGroup", "01", 2);
			inputReady_ds.addString("Length", "00000008", 8); // Length = STX ~ ETX
			inputReady_ds.addByte  ("STX", (byte) 0x02);
			inputReady_ds.addString("ODTNo", "", 2);
			inputReady_ds.addString("Code", "0003", 4);
			inputReady_ds.addByte  ("ETX", (byte) 0x03);
			inputReady_ds.addString("CRC", "", 4); // 4 bytes
			
		} catch (Exception e) {
			LogUtility.getLogger().debug(e.getMessage());
		} 
	}


	public void start(final String HOST, final int PORT, String ODTNO) {
		
		// 서버 초기화
		this.host = HOST;
		this.port = PORT;
		this.ODTNo = ODTNO;
		
		if (!m_isRunning) {
			m_isRunning = true;
			m_isStartedServer = true;
			
            // 스레드 생성
			createThread();		
		}
	}


	private void createThread() {
		
		m_thread = new Thread(new Runnable() {
			public void run() {				
				if (m_isRunning) {
					if(! initServer() ) { // 초기화
						LogUtility.getLogger().error("[Pump A] ### TCP/IP >>> Init GSocketServer FAIL!!!");						
						closeServer(); // 서버 클로징			
						return;
					} else {
						LogUtility.getLogger().error("[Pump A] ### TCP/IP >>> Init GSocketServer SUCCESS!");
					}
					
					startServer(); // 서버 시작
				}
			}
		});
		
		m_thread.setName("GSocketServer");
		m_thread.start();
	}
	
	
	private boolean initServer() {
		
		InetSocketAddress isa = null;
		
		try {
			// 셀렉터 열기
			selector = Selector.open(); 
			
            // 서버 소켓 채널 생성
			serverSocketChannel = ServerSocketChannel.open();  
			
            // 비블록킹 모드로 설정
			serverSocketChannel.configureBlocking(false);  
			
            // 서버 소켓 채널과 연결된 서버 소켓 가져 옴
			serverSocket = serverSocketChannel.socket();  
			
            // 주어진 파라미터에 해당하는 주소임. 포트로 서버 소켓일 바인드함
			isa = new InetSocketAddress(host,port);  
			serverSocket.bind(isa);
			
            // 서버 소켓 채널을 셀렉터에 등록함
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);  
			
		} catch (Exception e) {
			LogUtility.getLogger().error(e.getMessage(), e);
			return false;
		}

		return true;
		
	}

	private void startServer() {

		LogUtility.getLogger().debug("[Pump A] TCP/IP - Server starting...");
		boolean isExitLoop;

		try {
			while (m_isStartedServer) {
				isExitLoop = false;

				if (isConnected() == false) {
					LogUtility.getLogger().info("[Pump A] TCP/IP - Open server socket : Server IP=" + host + ", port=" + port);
					LogUtility.getLogger().info("[Pump A] TCP/IP - Client connection WAITING.........\n");
				}

				while (true) {
					Thread.sleep(1);
					
					int rtn = selector.select();
					LogUtility.getLogger().info("[Pump A] ODTNo=" + ODTNo + ", Selector return=" + rtn);
					
					Iterator it = selector.selectedKeys().iterator();
					
					while (it.hasNext()) {
						
						SelectionKey key = (SelectionKey) it.next();
						
						if (key.isAcceptable()) {
							
							// 서버 소켓 채널에 클라이언트가 접속을 시도한 경우
							accept(key);
							
						} else if (isConnected() && key.isReadable()) {
							try{
								dataProcessing(key);
								isExitLoop = true;
								break;
							} catch (Exception e){
								closeConnection(key, (SocketChannel)key.channel()); //Exception 처리 시 해당 Key정보 삭제 후, 다른 Key 수신 가능하게 
								LogUtility.getLogger().error(e.getMessage(), e);
							}
						}
						
						// 이미 처리한 이벤트 이므로 반드시 삭제함
						it.remove();
					}					
					if (isExitLoop == true) break;
				}
			}
		} catch (Exception e) {
			LogUtility.getLogger().error(e.getMessage(), e);
		}		

		LogUtility.getLogger().debug("[Pump A] Exit waiting loop...");
	}
    /*
     * 데이터 송수신 및 데이터 처리 
     * 
     * 1. 해당 메소드는 '송신부', '송신 후 미응답 시 처리부', '수신부' 로 나누어져 있음
     * 
     *     - 송신부 
     *       a. CommGSGasODTMr 에서 que에 담은 송신할 전문을 가져와 패킷화 및 ByteBuffer 형태로 바꾸어 전송함
     *       b. 송신 후 canSendC1 를 fasle로 바꾸어 KixxHub로 부터 ODT로 C1을 보내지 않고 응답을 기다림 
     *          (단, 송신할 전문이 ACK일 경우, canSendC1을 true로 하여 C1전송 및 응답을 기다리지 않음)
     *          
     *     - 송신 후 미응답 시 처리부
     *       a. dataProcessing 폴링 시 tickCnt를 이용하여, 응답대기시간이 2초, 4초,6초 시마다 송신했던 전문을 재전송함
     *       b. 응답대기시간이 8초 경과 시 송신했던 전문에 대한 응답 대기를 포기하고, C1을 재전송하도록 함
     *          (폴링 시 응답대기시간이 10초 지날 경우 해당 소켓의 커넥션을 끊음)
     *          
     *     - 수신부
     *       a. 초기화 전문(그룹코드: 01)과 데이터 전문(그룹코드:04)를 처리함
     *       
     */
	private void dataProcessing (SelectionKey key) throws InterruptedException, Exception {
		
		GSocketTimer gSockTimer = new GSocketTimer();	
		SocketChannel sc = (SocketChannel) key.channel();
		ByteBuffer sndByteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);	// 수신용
		ByteBuffer rcvByteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);  // 송신용

		boolean isRcvVerREQ = false; 
		boolean isRcvInputReady = false;
		byte[] rcvBytes;
		byte[] tempSndBytes = null;
		byte[] sndBuf = null;
		boolean isLineErr = false;
		gSockTimer.resetTime();  // 소켓타이머를 리셋 함 

		while (true) {
			
            // 연결확인 처리용				
			long timeGap = getCurrentDeciSeconds(gSockTimer);
			
			// 10초간 어떤 데이터도 미 수신할 경우 소켓을 끊음 
			isLineErr = timeGap > 100 ? true : false;
			
			if (isLineErr) { 
				LogUtility.getLogger().info("[Pump A] TCP/IP - Client disconnected! : Client IP=" + clientIP + 
						", port=" + clientPort + ", ODTNo=" + ODTNo + "\n");
				
				setConnected(false);
				closeConnection(key, sc);
				tickCnt = 0;
				return;
			}

			/*
			 * 전문 송신 처리 (송신부)
			 */ 	
			if (TxQue.getItemCount() > 0 && isOdtInitCompleted() == true) { 
				
                // 에러카운트 초기화 
				tickCnt = 0;  
				
                // 큐에서 전문을 가져옴 
				sndBuf = TxQue.getFirstItem();  
				
				// 송신할 전문 패킷화 함 
				tempSndBytes = makePacket("SC01", "OD02", "04", sndBuf); 

                // ByteBuffer 형태로 변환 
				sndByteBuffer = transBytesToByteBuffer(tempSndBytes);  
				
                // 데이터 전송	
				writeBuffer(sc, sndByteBuffer, tempSndBytes.length);
				
                /*
                 * 송신 전문의 로그 처리  
                 * (C1은 로그 찍지 않음)
                 */
				if(!(sndBuf[4] == 'C' && sndBuf[5] == '1')){  
					
					LogUtility.getLogger().info("[Pump A] Send STX(" + ODTNo + ") : [" + new String(tempSndBytes) + "]");
					//Log.datas(sndBuf, sndBuf.length, 20);
					
				}
				
				// 송신큐에서 삭제
				TxQue.deQueue();
				
				Thread.sleep(20);
				
				/*
				 * 송신전문의 Command에 따라 응답 대기 및 C1전송여부를 결정 
				 */
				if(sndBuf[4] == Command.ACK){ 
					
                    // 송신할 전문이 ACK일 경우, ODT로부터 응답을 기다리지 않으며 C1을 전송
					setCanSendC1(true) ;
					
				} else {
					
                    // 송신할 전문이 ACK가 아닐 경우, ODT로부터 응답을 기다리는 동안 C1을 미전송 
					setCanSendC1(false) ;
					
				}
			}  // 데이터 송신 처리 완료 
			
			/*
			 * 전문 전송 후 응답 미수신 시 처리 
			 */			
			if(!isCanSendC1() && tempSndBytes != null){  
				
				tickCnt++;
				
                /*
                 *  전문 송신 후 2초, 4초, 6초 경과 시 전문 재전송
                 */
				if(tickCnt == 100 || tickCnt == 200 || tickCnt == 300){ 
					
					// 데이터 전송
					writeBuffer(sc, sndByteBuffer, tempSndBytes.length);
					LogUtility.getLogger().info("[Pump A] Send STX Again(" + ODTNo + ") : [" + new String(sndBuf) + "]");
					//Log.datas(sndBuf, sndBuf.length, 20);
					
                //  전문 수신 후 8초 경과 시 tempSndBytes에 저장해두었던 기존전문 삭제 및 C1전문 전송 재시작  	
				} else if (tickCnt > 400){
					
					LogUtility.getLogger().info("[Pump A] 전문 송신 후 미응답");
					
                    // C1전송 재시작
					setCanSendC1(true) ;
					
                    // 에러카운트 초기화 
					tickCnt = 0;
					
                    // 임시 저장해두었던 송신 전문을 초기화 
					tempSndBytes = null;
					
				}
			}
								
			// 데이터 읽기
			boolean Recvd = readBuffer(sc, rcvByteBuffer);
			
			/*
			 * 수신 데이터 처리 (수신부)
			 */
			if (Recvd) {
				rcvBytes = transByteBufferToBytes(rcvByteBuffer);
				
				/*
				 * 초기화 전문 수신 처리 
				 */
				if (PacketUtil.getPacketGroup(rcvBytes).equals("01")) { 
					
                    // Version REQ 수신
					if (PacketUtil.getPacketCode(rcvBytes).equals("0001")) {
						
						LogUtility.getLogger().info("");
						LogUtility.getLogger().info("[Pump A] ---------------------TCP/IP 초기화 시작---------------------");
						LogUtility.getLogger().info("[Pump A] RECV <<< Version REQ : ODTNo=" + PacketUtil.getPacketODTNo(rcvBytes));
						LogUtility.getLogger().info("[Pump A] recv data = " + new String(rcvBytes));				
						isRcvVerREQ = true;					
						verREQ_ds.setByteStream(rcvBytes);
													
						// 데이터 송신 
						verACK_ds.editString("ODTNo", (String) verREQ_ds.getValue("ODTNo"), 2);
						verACK_ds.editString("Source", source, 4);
						verACK_ds.editString("Destination", (String) verREQ_ds.getValue("Source"), 4);
						byte[] sndReACK = verACK_ds.getByteStream();
											
						sndByteBuffer = transBytesToByteBuffer(sndReACK);
						writeBuffer(sc, sndReACK);
											
						LogUtility.getLogger().info("");
						LogUtility.getLogger().info("[Pump A] SEND >>> Version ACK : ODTNo=" + PacketUtil.getPacketODTNo(sndReACK));	
						LogUtility.getLogger().info("[Pump A] send data = " + new String(sndReACK));
						
					} else if (PacketUtil.getPacketCode(rcvBytes).equals("0003")) { //-- Input ready 수신
														
						LogUtility.getLogger().info("");
						LogUtility.getLogger().info("[Pump A] RECV <<< Input Ready : ODTNo=" + PacketUtil.getPacketODTNo(rcvBytes));
						LogUtility.getLogger().info("[Pump A] recv data = " + new String(rcvBytes));			
										
						if (isRcvVerREQ==true) {
							
							// ODT 초기화 완료
							isRcvInputReady = true;
							setOdtInitCompleted(true);
													
							LogUtility.getLogger().info("[Pump A] ---------------------TCP/IP 초기화 완료---------------------\n");
							LogUtility.getLogger().info("[Pump A] TCP/IP - Client init completed... : Client IP=" + clientIP + 
														", port=" + clientPort + ", ODTNo=" + ODTNo + "\n");
							setCanSendC1(true) ;						
							tickCnt = 0;
							
						} else {
							
							LogUtility.getLogger().info("[Pump A] ODTNo=" + ODTNo + " TCP/IP 초기화 미완료 : VerREQ 수신=" + isRcvVerREQ + ", InputReady 수신=" + isRcvInputReady);
							
						}
					}	
					
			    /*
			     * 데이터 전문 수신 처리 
			     */
				} else if (PacketUtil.getPacketGroup(rcvBytes).equals("04")) {
					
					// C1 전송 가능하도록 변경 
					setCanSendC1(true) ;
					
					tickCnt = 0;
					
                    // 임시 저장해두었던 송신 전문을 초기화 
					tempSndBytes = null;    
					
                    // 패킷화 된 전문 내 body를 가져옴
					byte[] RxBuf = getBodyBytes(rcvBytes); 
					
                    // 전문 Command 가져옴
					String msgCmd = getCommand(RxBuf[4], RxBuf[5]);
					
                    // 전문을 로그 변수에 저장
					String Tempstr = "";
					
					// SK전문의 경우 민감한정보를 가지고 있으므로 GaLog를 이용하여 처리함 
					if(msgCmd.equals("SK")){
						
						Tempstr =  new String(GaLog.gaLog(rcvBytes));
						
					// 일반 전문의 경우 로그 처리 
					} else {
						
						Tempstr = new String(rcvBytes);
						
					}
						
					/*
					 * 수신전문 로그 처리
					 * 1. 2012.06.12 kms T2의경우( 주유중 정보) 로그 기록을 조절함. intervalInt 사용. with 박종호k 
					 * 2. T1 수신 시 로그 찍지 않음
					 */
					
					// T1 수신 시 로그 기록하지 않음   
					if(!(msgCmd.equals("T1"))){   
						
						// T2 수신 시 로그 기록 조절 
						if(msgCmd.equals("T2")){  
							int logCnt = 1;
							
							if (logCount.containsKey(ODTNo)) {
								
								logCnt = logCount.get(ODTNo) + 1;
								logCount.put(ODTNo, logCnt);
								
							} else {
								
								logCount.put(ODTNo, logCnt);
								
							}
							
							// 로그를 3번에 1회 찍는다. 너무 많음.
							if ( logCount.get(ODTNo) == CommGSGasODTMr.intervalInt) {
								
								LogUtility.getLogger().info("[Pump A] Recv STX(" + ODTNo + ") : [" + Tempstr + "]");
								logCount.put(ODTNo, 0);
								
							}
							
					    // 승인요청전문 로그 처리  
						} else if(msgCmd.equals("ACK")){ 
							
							LogUtility.getLogger().info("[Pump A] Recv ACK(" + ODTNo + ")");
							
                        // T1, T2, ACK를 제외한 전문 로그 처리   
						} else { 
							
							LogUtility.getLogger().info("[Pump A] Recv STX(" + ODTNo + ") : [" + Tempstr + "]");
							//Log.datas(RxBuf, RxBuf.length, 20);
							
						}
					}  
					
					/*
					 * 전문 수신 후 ACK 송신 처리
					 * 1. ODT서 수신된 T1, T2, T4, ACK의 경우 ACK를 보내지 않음 (미래전자 요청)
					 */
					if(shouldSendACK(msgCmd)){ 
						byte[] ACKBuf = makeACK(ODTNo);
						byte[] sndPacket = makePacket("SC01", "OD02", "04", ACKBuf);
						sndByteBuffer = transBytesToByteBuffer(sndPacket);
						writeBuffer(sc, sndByteBuffer, sndByteBuffer.limit());
						LogUtility.getLogger().info("[Pump A] Send ACK(" + ODTNo + ")");
						//Log.datas(ACKBuf, ACKBuf.length, 20);
					}
					
					/* KixxHub서 T4전문 수신 후 S4, SH workingMessage를 PumpM으로 보낸 후 C1을 보내도록 변경 함
					 * 변경 사유 : T4, SK(0003)전문이 연달아 수신 될 경우, S4, SH전문을 처리 중 SK 전문이 processSelector.startProcess()에서 무시될 수 있음
					 * 변경 내역 : T4전문 처리 후 pumpM에서 내려오는 ACK를 송신 후 C1을 전송하여 SK(0003)전문을 순차적으로 받도록 변경  
					 */
					if((msgCmd.equals("T4"))){  
						
						setCanSendC1(false) ;
					} 
					
					/*
					 * 수신 전문 처리
					 * (ACK를 제외한 나머지 Command의 경우 수신 처리)
					 */  
					if(!(msgCmd.equals("ACK"))){
						
						recvProcess(RxBuf);
					} 
					
                    // 메모리 삭제 
					ClearUtil.setClearString(RxBuf); 
					ClearUtil.setClearString(Tempstr); 
					
				/*
				 * 초기화 전문, 데이터 전문이 아닌 전문 수신 시 처리 
				 * 1. ODT서 승인응답전문(LED포함)을 수신 후 데이터가 없는 전문을 송신 할 수 있음 (충전기 ODT미래전자 요청)
				 * (충전기 ODT의 경우, 민감데이터 삭제를 위해서는 빈데이터 전문을 송신해야 정상적으로 처리된다고 함)  
				 */
				} else {  
					
					LogUtility.getLogger().info("[Pump A] Recv STX(" + ODTNo + ") : [" + new String(rcvBytes) + "]");
				}
				
				// 소켓타이머를 리셋 함 
				gSockTimer.resetTime(); 
				
                // 메모리 삭제 
				ClearUtil.setClearString(rcvBytes);
				ClearUtil.setClearString(rcvByteBuffer);
				
			} // 데이터 수신 처리 완료 
			
            // 메모리 삭제 
			ClearUtil.setClearString(rcvByteBuffer);
			
            // 폴링 간격 : 20ms
			Thread.sleep(20); 
			
		} //  데이터 송수신 처리 종료 
	
	}

	private void recvProcess(byte[] byBuf) {

		service.recvMessage(byBuf);
	}

	private void closeConnection (SelectionKey key, SocketChannel sc) {													
		try {
			m_sc.close(); // accept channel
			
			key.cancel();
			sc.close(); // read channel
			
			setOdtInitCompleted(false);			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/*
	 * 수신부에서 응답 전문에 따라 ACK 송신여부 결정
	 * 1. T1, T2, T4, ACK 전문을 수신 할 경우, 수신부에서 ACK를 ODT로 보내지 않음
	 * 2. T4의 경우 pumpA가 아닌 pumpM에서 ACK를 송신하도록 결정 (pumpAcontroller.processSH() 참고)
	 */	
	private boolean shouldSendACK(String command){
		
		boolean sendACK = true;
		
		if(command.equals("T1")
			||command.equals("T2")
			||command.equals("T4")
			||command.equals("ACK")){
			
			sendACK = false;
			}
		return sendACK;
	}
	
	public long getCurrentTimeMillis() {
		
		try {	
			Calendar cal = new GregorianCalendar();
			return  cal.getTimeInMillis();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

	private void accept(SelectionKey key) {
		// TODO Auto-generated method stub
		ServerSocketChannel server = (ServerSocketChannel)key.channel();
		SocketChannel sc;
		
		try {
			
			// 서버 소켓 채널의 accept() 로 서버 소켓을 생성함
			sc = server.accept();
			m_sc = sc;
			
			clientIP = sc.socket().getInetAddress().getHostName();
			clientPort = sc.socket().getPort();
			
			setConnected(true);
			
			// 생성된 소켓채널을 비블록킹과 읽기 모드로 셀렉터에 등록함
			registerChannel(selector, sc, SelectionKey.OP_READ /*| SelectionKey.OP_WRITE*/);
		
		} catch (ClosedChannelException e) {
			LogUtility.getLogger().error(e.getMessage(), e);
		} catch (IOException e) {
			LogUtility.getLogger().error(e.getMessage(), e);
		}
	}

	private void registerChannel(Selector selector, SocketChannel sc,int ops) throws ClosedChannelException, IOException {
		// TODO Auto-generated method stub
		if(sc == null){
			LogUtility.getLogger().info("[Pump A] Invalid  Connection");
			return;
		}
		
		sc.configureBlocking(false);
		sc.register(selector, ops);
	}
	
	public boolean readBuffer(SocketChannel sc, ByteBuffer byteBuffer) {

		try {
			byteBuffer.clear();				
			int len = sc.read(byteBuffer);	
			if (len > 0){
				byteBuffer.flip();		
				return true;
				
            // 데이터 미수신
			} else {
				//byteBuffer.flip();
				return false;
			}
			
		} catch (Exception e) {
			LogUtility.getLogger().error(e.getMessage(), e);
			return false;
		}
	}
	
	public void writeBuffer(SocketChannel sockChannel, ByteBuffer byteBuffer, int size) {

		try {
			byteBuffer.clear();
			byteBuffer.limit(size);
			
			sockChannel.write(byteBuffer);
			
		} catch (Exception e) {
			//LogUtility.getLogger().error(e.getMessage(), e);
		}
	}
	
	public void writeBuffer(SocketChannel sockChannel, byte[] data) {

		try {
			
			ByteBuffer byteBuffer = ByteBuffer.wrap(data);
			sockChannel.write(byteBuffer);
			
		} catch (Exception e) {
			//LogUtility.getLogger().error(e.getMessage(), e);
		}
	}
	
	public byte[] transByteBufferToBytes(ByteBuffer bytebuffer) {
		
		try {
			byte[] byt = new byte[bytebuffer.limit()];
			bytebuffer.get(byt);
			return byt;		
		} catch (Exception e) {
			LogUtility.getLogger().error(e.getMessage(), e);
			return null;
		}
	}
	
	public ByteBuffer transBytesToByteBuffer(byte[] bytes) {
		
		try {       
			//return encoder.encode(CharBuffer.wrap(new String (bytes) + ""));	
			ByteBuffer byteBuf = ByteBuffer.allocate(bytes.length);
			byteBuf.put(bytes);
			return byteBuf;
		} catch (Exception e) {
			LogUtility.getLogger().error(e.getMessage(), e);
			return null;
		}
	}
	public synchronized SocketChannel getSocketChannel() {
		
		return m_sockChannel ;
	}

	public void stop() {

		m_isRunning = false;
		m_isStartedServer = false;
		if (m_thread != null) {
			// socket close..
			closeServer();
			m_thread.interrupt();
			try {
				m_thread.join();
			} catch (InterruptedException e) {
				LogUtility.getLogger().debug(e.getMessage());
			}
			m_thread = null;
		}
	}

	private void closeServer() {
		try {
			this.serverSocket.close();
			this.serverSocket.bind(null);
			this.serverSocketChannel.close();
		} catch (Exception e) {
			LogUtility.getLogger().error(e.getMessage(), e);
		}
	}	


	public synchronized boolean isConnected() {
		return isConnected;
	}


	public synchronized void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}


	public synchronized boolean isOdtInitCompleted() {
		return odtInitCompleted;
	}


	public synchronized void setOdtInitCompleted(boolean odtInitCompleted) {
		this.odtInitCompleted = odtInitCompleted;
	}
	
	
	//	 패킷그룹 조회
	public String getPacketGroup(byte[] bytes) {

		try {
			byte[] by = new byte[2];
			by[0] = bytes[9];
			by[1] = bytes[10];
			
			return new String (by);
		} 
		catch (Exception e) {
			return null;
		}
	}
	
	
	// 패킷 코드 조회
	public String getPacketCode(byte[] bytes) {

		try {
			byte[] by = new byte[4];
			by[0] = bytes[22];
			by[1] = bytes[23];
			by[2] = bytes[24];
			by[3] = bytes[25];
			
			return new String (by);
		} 
		catch (Exception e) {
			return null;
		}
	}
	
	// 패킷 길이 조회
	public int getPacketLength(byte[] bytes) {

		try {
			byte[] by = new byte[8];
			by[0] = bytes[11];
			by[1] = bytes[12];
			by[2] = bytes[13];
			by[3] = bytes[14];
			by[4] = bytes[15];
			by[5] = bytes[16];
			by[6] = bytes[17];
			by[7] = bytes[18];
			
			return Integer.parseInt(new String (by));
		} 
		catch (Exception e) {
			return -1;
		}
	}
	
	// 패킷 소스 ODT번호 조회
	public String getPacketODTNo(byte[] bytes) {

		try {
			byte[] by = new byte[2];
			by[0] = bytes[20];
			by[1] = bytes[21];
			
			return new String (by);
		} 
		catch (Exception e) {
			return "";
		}
	}
	
	// 패킷 소스 ODT번호 설정
	public void setPacketODTNo(byte[] bytes, String ODTNo) {

		try {
			byte[] by = ODTNo.getBytes();
			bytes[20] = by[0];
			bytes[21] = by[1];
		} 
		catch (Exception e) {
		}
	}	
	
	/**
	 * ack전문 생성
	 * @param no 
	 * 
	 * @return
	 * @throws Exception
	 */
	private byte[] makeACK(String nozNum) throws Exception {
		byte[] no = ODTNo.getBytes();
		byte[] ackM = new byte[6];
		ackM[0] = Command.SOH;
		ackM[1] = no[0];
		ackM[2] = no[1];
		ackM[3] = Command.STX;
		ackM[4] = Command.ACK;
		ackM[5] = Command.ETX;
		
		return ackM;
	}
	
	public byte[] makePacket(String src, String dest, String packGrp,
			byte[] bodyBytes) {
		DataStruct head_ds = new DataStruct();
		DataStruct tail_ds = new DataStruct();

		try {
			String length = Change.toString("%08d", bodyBytes.length + 2);

			head_ds.addByte("SOH", (byte) 0x01);
			head_ds.addString("Source", src, 4);
			head_ds.addString("Destination", dest, 4);
			head_ds.addString("PacketGroup", packGrp, 2);
			head_ds.addString("Length", length, 8); // Length = STX ~ ETX
			head_ds.addByte("STX", (byte) 0x02);
			byte[] byHead = head_ds.getByteStream();

			tail_ds.addByte("ETX", (byte) 0x03);
			tail_ds.addString("CRC", "", 4); // 4 bytes
			byte[] byTail = tail_ds.getByteStream();

			return (new String(byHead) + new String(bodyBytes) + new String(
					byTail)).getBytes();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private String getCommand(byte byteA, byte byteB) {
		
		try {
			if(byteA == Command.ACK) {
				return "ACK";
			} else {
				byte[] byBuf = {byteA, byteB};
				return new String(byBuf);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
    public long getCurrentDeciSeconds(GSocketTimer timer) {
		
		try {	
			return timer.getDeciSeconds();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

    
    public byte[] getBodyBytes(byte[] rcvPacket) {
		
		try {	
			byte[] bySize = new byte[8];
			System.arraycopy(rcvPacket, 11, bySize, 0, 8);
			
			int size = Change.toValue(new String(bySize));
			
			byte[] byBody = new byte[size - 2]; // STX, ETX 는 제외
			
			System.arraycopy(rcvPacket, 20, byBody, 0, size - 2);

			return byBody;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
    public boolean isCanSendC1() {
		return canSendC1;
	}

	public void setCanSendC1(boolean canSendC1) {
		this.canSendC1 = canSendC1;
	}

	public static void main(String[] args) throws IOException, Exception {
	
	}
}