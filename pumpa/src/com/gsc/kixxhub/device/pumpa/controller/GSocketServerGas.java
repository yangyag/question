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
 * ������Ʈ : PI2
 * �������� : 2016.12.07 ������
 * ���� : TCP/IP ��Ź�� ������ ODT�� Socket Ŭ���� 
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
	
	public boolean canSendC1 = true;  // C1���ۿ��� �÷��� (���� �۽� �� �����ð����� C1�� ������ �ʰ� �����)
	
	private Hashtable<String, Integer> logCount = new Hashtable<String, Integer>();

	public GSocketServerGas(int headerSize, int bufferSize) {
		try {

			// �ʱ�ȭ ��� ��Ŷ 
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
		
		// ���� �ʱ�ȭ
		this.host = HOST;
		this.port = PORT;
		this.ODTNo = ODTNO;
		
		if (!m_isRunning) {
			m_isRunning = true;
			m_isStartedServer = true;
			
            // ������ ����
			createThread();		
		}
	}


	private void createThread() {
		
		m_thread = new Thread(new Runnable() {
			public void run() {				
				if (m_isRunning) {
					if(! initServer() ) { // �ʱ�ȭ
						LogUtility.getLogger().error("[Pump A] ### TCP/IP >>> Init GSocketServer FAIL!!!");						
						closeServer(); // ���� Ŭ��¡			
						return;
					} else {
						LogUtility.getLogger().error("[Pump A] ### TCP/IP >>> Init GSocketServer SUCCESS!");
					}
					
					startServer(); // ���� ����
				}
			}
		});
		
		m_thread.setName("GSocketServer");
		m_thread.start();
	}
	
	
	private boolean initServer() {
		
		InetSocketAddress isa = null;
		
		try {
			// ������ ����
			selector = Selector.open(); 
			
            // ���� ���� ä�� ����
			serverSocketChannel = ServerSocketChannel.open();  
			
            // ����ŷ ���� ����
			serverSocketChannel.configureBlocking(false);  
			
            // ���� ���� ä�ΰ� ����� ���� ���� ���� ��
			serverSocket = serverSocketChannel.socket();  
			
            // �־��� �Ķ���Ϳ� �ش��ϴ� �ּ���. ��Ʈ�� ���� ������ ���ε���
			isa = new InetSocketAddress(host,port);  
			serverSocket.bind(isa);
			
            // ���� ���� ä���� �����Ϳ� �����
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
							
							// ���� ���� ä�ο� Ŭ���̾�Ʈ�� ������ �õ��� ���
							accept(key);
							
						} else if (isConnected() && key.isReadable()) {
							try{
								dataProcessing(key);
								isExitLoop = true;
								break;
							} catch (Exception e){
								closeConnection(key, (SocketChannel)key.channel()); //Exception ó�� �� �ش� Key���� ���� ��, �ٸ� Key ���� �����ϰ� 
								LogUtility.getLogger().error(e.getMessage(), e);
							}
						}
						
						// �̹� ó���� �̺�Ʈ �̹Ƿ� �ݵ�� ������
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
     * ������ �ۼ��� �� ������ ó�� 
     * 
     * 1. �ش� �޼ҵ�� '�۽ź�', '�۽� �� ������ �� ó����', '���ź�' �� �������� ����
     * 
     *     - �۽ź� 
     *       a. CommGSGasODTMr ���� que�� ���� �۽��� ������ ������ ��Ŷȭ �� ByteBuffer ���·� �ٲپ� ������
     *       b. �۽� �� canSendC1 �� fasle�� �ٲپ� KixxHub�� ���� ODT�� C1�� ������ �ʰ� ������ ��ٸ� 
     *          (��, �۽��� ������ ACK�� ���, canSendC1�� true�� �Ͽ� C1���� �� ������ ��ٸ��� ����)
     *          
     *     - �۽� �� ������ �� ó����
     *       a. dataProcessing ���� �� tickCnt�� �̿��Ͽ�, ������ð��� 2��, 4��,6�� �ø��� �۽��ߴ� ������ ��������
     *       b. ������ð��� 8�� ��� �� �۽��ߴ� ������ ���� ���� ��⸦ �����ϰ�, C1�� �������ϵ��� ��
     *          (���� �� ������ð��� 10�� ���� ��� �ش� ������ Ŀ�ؼ��� ����)
     *          
     *     - ���ź�
     *       a. �ʱ�ȭ ����(�׷��ڵ�: 01)�� ������ ����(�׷��ڵ�:04)�� ó����
     *       
     */
	private void dataProcessing (SelectionKey key) throws InterruptedException, Exception {
		
		GSocketTimer gSockTimer = new GSocketTimer();	
		SocketChannel sc = (SocketChannel) key.channel();
		ByteBuffer sndByteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);	// ���ſ�
		ByteBuffer rcvByteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);  // �۽ſ�

		boolean isRcvVerREQ = false; 
		boolean isRcvInputReady = false;
		byte[] rcvBytes;
		byte[] tempSndBytes = null;
		byte[] sndBuf = null;
		boolean isLineErr = false;
		gSockTimer.resetTime();  // ����Ÿ�̸Ӹ� ���� �� 

		while (true) {
			
            // ����Ȯ�� ó����				
			long timeGap = getCurrentDeciSeconds(gSockTimer);
			
			// 10�ʰ� � �����͵� �� ������ ��� ������ ���� 
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
			 * ���� �۽� ó�� (�۽ź�)
			 */ 	
			if (TxQue.getItemCount() > 0 && isOdtInitCompleted() == true) { 
				
                // ����ī��Ʈ �ʱ�ȭ 
				tickCnt = 0;  
				
                // ť���� ������ ������ 
				sndBuf = TxQue.getFirstItem();  
				
				// �۽��� ���� ��Ŷȭ �� 
				tempSndBytes = makePacket("SC01", "OD02", "04", sndBuf); 

                // ByteBuffer ���·� ��ȯ 
				sndByteBuffer = transBytesToByteBuffer(tempSndBytes);  
				
                // ������ ����	
				writeBuffer(sc, sndByteBuffer, tempSndBytes.length);
				
                /*
                 * �۽� ������ �α� ó��  
                 * (C1�� �α� ���� ����)
                 */
				if(!(sndBuf[4] == 'C' && sndBuf[5] == '1')){  
					
					LogUtility.getLogger().info("[Pump A] Send STX(" + ODTNo + ") : [" + new String(tempSndBytes) + "]");
					//Log.datas(sndBuf, sndBuf.length, 20);
					
				}
				
				// �۽�ť���� ����
				TxQue.deQueue();
				
				Thread.sleep(20);
				
				/*
				 * �۽������� Command�� ���� ���� ��� �� C1���ۿ��θ� ���� 
				 */
				if(sndBuf[4] == Command.ACK){ 
					
                    // �۽��� ������ ACK�� ���, ODT�κ��� ������ ��ٸ��� ������ C1�� ����
					setCanSendC1(true) ;
					
				} else {
					
                    // �۽��� ������ ACK�� �ƴ� ���, ODT�κ��� ������ ��ٸ��� ���� C1�� ������ 
					setCanSendC1(false) ;
					
				}
			}  // ������ �۽� ó�� �Ϸ� 
			
			/*
			 * ���� ���� �� ���� �̼��� �� ó�� 
			 */			
			if(!isCanSendC1() && tempSndBytes != null){  
				
				tickCnt++;
				
                /*
                 *  ���� �۽� �� 2��, 4��, 6�� ��� �� ���� ������
                 */
				if(tickCnt == 100 || tickCnt == 200 || tickCnt == 300){ 
					
					// ������ ����
					writeBuffer(sc, sndByteBuffer, tempSndBytes.length);
					LogUtility.getLogger().info("[Pump A] Send STX Again(" + ODTNo + ") : [" + new String(sndBuf) + "]");
					//Log.datas(sndBuf, sndBuf.length, 20);
					
                //  ���� ���� �� 8�� ��� �� tempSndBytes�� �����صξ��� �������� ���� �� C1���� ���� �����  	
				} else if (tickCnt > 400){
					
					LogUtility.getLogger().info("[Pump A] ���� �۽� �� ������");
					
                    // C1���� �����
					setCanSendC1(true) ;
					
                    // ����ī��Ʈ �ʱ�ȭ 
					tickCnt = 0;
					
                    // �ӽ� �����صξ��� �۽� ������ �ʱ�ȭ 
					tempSndBytes = null;
					
				}
			}
								
			// ������ �б�
			boolean Recvd = readBuffer(sc, rcvByteBuffer);
			
			/*
			 * ���� ������ ó�� (���ź�)
			 */
			if (Recvd) {
				rcvBytes = transByteBufferToBytes(rcvByteBuffer);
				
				/*
				 * �ʱ�ȭ ���� ���� ó�� 
				 */
				if (PacketUtil.getPacketGroup(rcvBytes).equals("01")) { 
					
                    // Version REQ ����
					if (PacketUtil.getPacketCode(rcvBytes).equals("0001")) {
						
						LogUtility.getLogger().info("");
						LogUtility.getLogger().info("[Pump A] ---------------------TCP/IP �ʱ�ȭ ����---------------------");
						LogUtility.getLogger().info("[Pump A] RECV <<< Version REQ : ODTNo=" + PacketUtil.getPacketODTNo(rcvBytes));
						LogUtility.getLogger().info("[Pump A] recv data = " + new String(rcvBytes));				
						isRcvVerREQ = true;					
						verREQ_ds.setByteStream(rcvBytes);
													
						// ������ �۽� 
						verACK_ds.editString("ODTNo", (String) verREQ_ds.getValue("ODTNo"), 2);
						verACK_ds.editString("Source", source, 4);
						verACK_ds.editString("Destination", (String) verREQ_ds.getValue("Source"), 4);
						byte[] sndReACK = verACK_ds.getByteStream();
											
						sndByteBuffer = transBytesToByteBuffer(sndReACK);
						writeBuffer(sc, sndReACK);
											
						LogUtility.getLogger().info("");
						LogUtility.getLogger().info("[Pump A] SEND >>> Version ACK : ODTNo=" + PacketUtil.getPacketODTNo(sndReACK));	
						LogUtility.getLogger().info("[Pump A] send data = " + new String(sndReACK));
						
					} else if (PacketUtil.getPacketCode(rcvBytes).equals("0003")) { //-- Input ready ����
														
						LogUtility.getLogger().info("");
						LogUtility.getLogger().info("[Pump A] RECV <<< Input Ready : ODTNo=" + PacketUtil.getPacketODTNo(rcvBytes));
						LogUtility.getLogger().info("[Pump A] recv data = " + new String(rcvBytes));			
										
						if (isRcvVerREQ==true) {
							
							// ODT �ʱ�ȭ �Ϸ�
							isRcvInputReady = true;
							setOdtInitCompleted(true);
													
							LogUtility.getLogger().info("[Pump A] ---------------------TCP/IP �ʱ�ȭ �Ϸ�---------------------\n");
							LogUtility.getLogger().info("[Pump A] TCP/IP - Client init completed... : Client IP=" + clientIP + 
														", port=" + clientPort + ", ODTNo=" + ODTNo + "\n");
							setCanSendC1(true) ;						
							tickCnt = 0;
							
						} else {
							
							LogUtility.getLogger().info("[Pump A] ODTNo=" + ODTNo + " TCP/IP �ʱ�ȭ �̿Ϸ� : VerREQ ����=" + isRcvVerREQ + ", InputReady ����=" + isRcvInputReady);
							
						}
					}	
					
			    /*
			     * ������ ���� ���� ó�� 
			     */
				} else if (PacketUtil.getPacketGroup(rcvBytes).equals("04")) {
					
					// C1 ���� �����ϵ��� ���� 
					setCanSendC1(true) ;
					
					tickCnt = 0;
					
                    // �ӽ� �����صξ��� �۽� ������ �ʱ�ȭ 
					tempSndBytes = null;    
					
                    // ��Ŷȭ �� ���� �� body�� ������
					byte[] RxBuf = getBodyBytes(rcvBytes); 
					
                    // ���� Command ������
					String msgCmd = getCommand(RxBuf[4], RxBuf[5]);
					
                    // ������ �α� ������ ����
					String Tempstr = "";
					
					// SK������ ��� �ΰ��������� ������ �����Ƿ� GaLog�� �̿��Ͽ� ó���� 
					if(msgCmd.equals("SK")){
						
						Tempstr =  new String(GaLog.gaLog(rcvBytes));
						
					// �Ϲ� ������ ��� �α� ó�� 
					} else {
						
						Tempstr = new String(rcvBytes);
						
					}
						
					/*
					 * �������� �α� ó��
					 * 1. 2012.06.12 kms T2�ǰ��( ������ ����) �α� ����� ������. intervalInt ���. with ����ȣk 
					 * 2. T1 ���� �� �α� ���� ����
					 */
					
					// T1 ���� �� �α� ������� ����   
					if(!(msgCmd.equals("T1"))){   
						
						// T2 ���� �� �α� ��� ���� 
						if(msgCmd.equals("T2")){  
							int logCnt = 1;
							
							if (logCount.containsKey(ODTNo)) {
								
								logCnt = logCount.get(ODTNo) + 1;
								logCount.put(ODTNo, logCnt);
								
							} else {
								
								logCount.put(ODTNo, logCnt);
								
							}
							
							// �α׸� 3���� 1ȸ ��´�. �ʹ� ����.
							if ( logCount.get(ODTNo) == CommGSGasODTMr.intervalInt) {
								
								LogUtility.getLogger().info("[Pump A] Recv STX(" + ODTNo + ") : [" + Tempstr + "]");
								logCount.put(ODTNo, 0);
								
							}
							
					    // ���ο�û���� �α� ó��  
						} else if(msgCmd.equals("ACK")){ 
							
							LogUtility.getLogger().info("[Pump A] Recv ACK(" + ODTNo + ")");
							
                        // T1, T2, ACK�� ������ ���� �α� ó��   
						} else { 
							
							LogUtility.getLogger().info("[Pump A] Recv STX(" + ODTNo + ") : [" + Tempstr + "]");
							//Log.datas(RxBuf, RxBuf.length, 20);
							
						}
					}  
					
					/*
					 * ���� ���� �� ACK �۽� ó��
					 * 1. ODT�� ���ŵ� T1, T2, T4, ACK�� ��� ACK�� ������ ���� (�̷����� ��û)
					 */
					if(shouldSendACK(msgCmd)){ 
						byte[] ACKBuf = makeACK(ODTNo);
						byte[] sndPacket = makePacket("SC01", "OD02", "04", ACKBuf);
						sndByteBuffer = transBytesToByteBuffer(sndPacket);
						writeBuffer(sc, sndByteBuffer, sndByteBuffer.limit());
						LogUtility.getLogger().info("[Pump A] Send ACK(" + ODTNo + ")");
						//Log.datas(ACKBuf, ACKBuf.length, 20);
					}
					
					/* KixxHub�� T4���� ���� �� S4, SH workingMessage�� PumpM���� ���� �� C1�� �������� ���� ��
					 * ���� ���� : T4, SK(0003)������ ���޾� ���� �� ���, S4, SH������ ó�� �� SK ������ processSelector.startProcess()���� ���õ� �� ����
					 * ���� ���� : T4���� ó�� �� pumpM���� �������� ACK�� �۽� �� C1�� �����Ͽ� SK(0003)������ ���������� �޵��� ����  
					 */
					if((msgCmd.equals("T4"))){  
						
						setCanSendC1(false) ;
					} 
					
					/*
					 * ���� ���� ó��
					 * (ACK�� ������ ������ Command�� ��� ���� ó��)
					 */  
					if(!(msgCmd.equals("ACK"))){
						
						recvProcess(RxBuf);
					} 
					
                    // �޸� ���� 
					ClearUtil.setClearString(RxBuf); 
					ClearUtil.setClearString(Tempstr); 
					
				/*
				 * �ʱ�ȭ ����, ������ ������ �ƴ� ���� ���� �� ó�� 
				 * 1. ODT�� ������������(LED����)�� ���� �� �����Ͱ� ���� ������ �۽� �� �� ���� (������ ODT�̷����� ��û)
				 * (������ ODT�� ���, �ΰ������� ������ ���ؼ��� ������ ������ �۽��ؾ� ���������� ó���ȴٰ� ��)  
				 */
				} else {  
					
					LogUtility.getLogger().info("[Pump A] Recv STX(" + ODTNo + ") : [" + new String(rcvBytes) + "]");
				}
				
				// ����Ÿ�̸Ӹ� ���� �� 
				gSockTimer.resetTime(); 
				
                // �޸� ���� 
				ClearUtil.setClearString(rcvBytes);
				ClearUtil.setClearString(rcvByteBuffer);
				
			} // ������ ���� ó�� �Ϸ� 
			
            // �޸� ���� 
			ClearUtil.setClearString(rcvByteBuffer);
			
            // ���� ���� : 20ms
			Thread.sleep(20); 
			
		} //  ������ �ۼ��� ó�� ���� 
	
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
	 * ���źο��� ���� ������ ���� ACK �۽ſ��� ����
	 * 1. T1, T2, T4, ACK ������ ���� �� ���, ���źο��� ACK�� ODT�� ������ ����
	 * 2. T4�� ��� pumpA�� �ƴ� pumpM���� ACK�� �۽��ϵ��� ���� (pumpAcontroller.processSH() ����)
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
			
			// ���� ���� ä���� accept() �� ���� ������ ������
			sc = server.accept();
			m_sc = sc;
			
			clientIP = sc.socket().getInetAddress().getHostName();
			clientPort = sc.socket().getPort();
			
			setConnected(true);
			
			// ������ ����ä���� ����ŷ�� �б� ���� �����Ϳ� �����
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
				
            // ������ �̼���
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
	
	
	//	 ��Ŷ�׷� ��ȸ
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
	
	
	// ��Ŷ �ڵ� ��ȸ
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
	
	// ��Ŷ ���� ��ȸ
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
	
	// ��Ŷ �ҽ� ODT��ȣ ��ȸ
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
	
	// ��Ŷ �ҽ� ODT��ȣ ����
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
	 * ack���� ����
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
			
			byte[] byBody = new byte[size - 2]; // STX, ETX �� ����
			
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