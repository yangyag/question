/*
 * GS ǥ�ؼ��� ODT �� TCP/IP ��� ����
 * PI2-�ڵ�ȭ, 2016-01-07 �ۼ�
 * <<< GSocketServer�� ODT�� �񵿱� ����� >>>
 */
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
import java.util.Iterator;
import java.util.Vector;

import com.gsc.kixxhub.common.utility.ClearUtil;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.BytesQue2;
import com.gsc.kixxhub.device.pumpa.common.Change;
import com.gsc.kixxhub.device.pumpa.common.DataStruct;
import com.gsc.kixxhub.device.pumpa.common.GaLog;
import com.gsc.kixxhub.device.pumpa.common.Sleep;


public class GSocketServerSelf {

	public static void main(String[] args) throws IOException, Exception {

	}
	//protected int HEADER_SIZE = 16;
	protected int BUFFER_SIZE = 4096;
	
	protected String clientIP = "";
	protected int clientPort = 0;
	protected byte[] 	connectACK_Buf;
	protected DataStruct connectACK_ds = new DataStruct(); // Connection ACK
	protected byte[] 	connectREQ_Buf;
	protected DataStruct connectREQ_ds = new DataStruct(); // Connection REQ
	protected byte[] 	dataACK_Buf;
	protected DataStruct dataACK_ds = new DataStruct(); // Data ACK
	protected byte[] 	dataENQ_Buf;
	protected DataStruct dataENQ_ds = new DataStruct(); // Data ENQ
	protected byte[] 	dataEOT_Buf;
	protected DataStruct dataEOT_ds = new DataStruct(); // Data EOT
	protected byte[] 	dataNAK_Buf;
	protected DataStruct dataNAK_ds = new DataStruct(); // Data NAK
	public int 		dispLevel=0;
	GSocketTimer gSockTimer = new GSocketTimer();
	//protected byte[] 	 m_byHeader = new byte[HEADER_SIZE];
    //protected byte[] 	 m_sndBuff = new byte[BUFFER_SIZE];
    //protected byte[] 	 m_rcvBuff = new byte[BUFFER_SIZE];
    protected DataStruct header_ds = new DataStruct(); // Header
	protected String host;

    protected DataStruct inputReady_ds = new DataStruct(); // InputReady_ds
    protected boolean isConnACKRecved=false;
    protected boolean isConnected = false;
    protected boolean  isRcvEnvDataOK =false;
    protected boolean m_isRunning = false;
    protected boolean m_isStartedServer = false;
    protected SocketChannel m_sc = null;
    protected Thread m_thread = null;
    protected boolean odtInitCompleted = false;
    protected String ODTNo = "";

    protected int port;
    public BytesQue2 RxQue = new BytesQue2(30);
    public Selector selector = null;
    protected ServerSocket serverSocket = null;
    protected ServerSocketChannel serverSocketChannel;
    //protected ArrayList<GServerService> serviceList = new ArrayList<GServerService>()
	public GServerService service;
    
	//ByteBuffer m_rcvByteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);	// ���ۿ�
	//ByteBuffer m_sndByteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);	// ���ſ�
	
	//Charset charset = Charset.forName("UTF-8");
	//CharsetEncoder encoder = charset.newEncoder();
	//protected PacketUtil PacketUtil = new PacketUtil();

	protected String source = "SC01";
	protected int connectCount = 0;
	//	 kixxhub ���ʱ⵿�� CommGSSelfODTi ���� ������ ������ ���� 
	public boolean startInitCompletedFlag = odtInitCompleted;
	
	public BytesQue2 TxQue = new BytesQue2(30);	
	protected DataStruct verACK_ds = new DataStruct(); // Version ACK	

	protected DataStruct verREQ_ds = new DataStruct(); // Version REQ


	public GSocketServerSelf(int headerSize, int bufferSize) {
		
//		this.HEADER_SIZE = headerSize;
//		this.BUFFER_SIZE = bufferSize;
//		
		try {

			//--- �ʱ�ȭ ��� ��Ŷ ---//
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
			

			//--- ���� Ȯ�� ��Ŷ ---//
			// Connection REQ
			connectREQ_ds.addByte  ("SOH", (byte) 0x01);
			connectREQ_ds.addString("Source", source, 4);
			connectREQ_ds.addString("Destination", "0000", 4);
			connectREQ_ds.addString("PacketGroup", "02", 2);
			connectREQ_ds.addString("Length", "00000020", 8); // Length = STX ~ ETX	
			connectREQ_ds.addByte  ("STX", (byte) 0x02);
			connectREQ_ds.addString("ODTNo", "", 2);
			connectREQ_ds.addString("Code", "0001", 4);
			connectREQ_ds.addString("DataName", "Connect REQ", 12);
			connectREQ_ds.addByte  ("ETX", (byte) 0x03);
			connectREQ_ds.addString("CRC", "", 4); // 4 bytes
			connectREQ_Buf = connectREQ_ds.getByteStream();

			// Connection ACK
			connectACK_ds.addByte  ("SOH", (byte) 0x01);
			connectACK_ds.addString("Source", source, 4);
			connectACK_ds.addString("Destination", "0000", 4);
			connectACK_ds.addString("PacketGroup", "02", 2);
			connectACK_ds.addString("Length", "00000020", 8); // Length = STX ~ ETX	
			connectACK_ds.addByte  ("STX", (byte) 0x02);
			connectACK_ds.addString("ODTNo", "", 2);
			connectACK_ds.addString("Code", "0002", 4);
			connectACK_ds.addString("DataName", "Connect ACK", 12);
			connectACK_ds.addByte  ("ETX", (byte) 0x03);
			connectACK_ds.addString("CRC", "", 4); // 4 bytes

			//-------- ���� ��Ŷ --------//	
			// Data ENQ
			dataENQ_ds.addByte  ("SOH", (byte) 0x01);
			dataENQ_ds.addString("Source", "SC01", 4);
			dataENQ_ds.addString("Destination", "OD01", 4);
			dataENQ_ds.addString("PacketGroup", "03", 2);
			dataENQ_ds.addString("Length", "00000020", 8); // Length = STX ~ ETX	
			dataENQ_ds.addByte  ("STX", (byte) 0x02);
			dataENQ_ds.addString("ODTNo", ODTNo, 2);
			dataENQ_ds.addString("Code", "0001", 4);
			dataENQ_ds.addString("DataName", "Data ENQ", 12);
			dataENQ_ds.addByte  ("ETX", (byte) 0x03);
			dataENQ_ds.addString("CRC", "", 4); // 4 bytes
			dataENQ_Buf = dataENQ_ds.getByteStream();
			
			// Data ACK
			dataACK_ds.addByte  ("SOH", (byte) 0x01);
			dataACK_ds.addString("Source", "SC01", 4);
			dataACK_ds.addString("Destination", "OD01", 4);
			dataACK_ds.addString("PacketGroup", "03", 2);
			dataACK_ds.addString("Length", "00000020", 8); // Length = STX ~ ETX	
			dataACK_ds.addByte  ("STX", (byte) 0x02);
			dataACK_ds.addString("ODTNo", "", 2);
			dataACK_ds.addString("Code", "0002", 4);
			dataACK_ds.addString("DataName", "Data ACK", 8);
			dataACK_ds.addString("Echo", "", 4);
			dataACK_ds.addByte  ("ETX", (byte) 0x03);
			dataACK_ds.addString("CRC", "", 4); // 4 bytes
			dataACK_Buf = dataACK_ds.getByteStream();
			
			// Data NAK
			dataNAK_ds.addByte  ("SOH", (byte) 0x01);
			dataNAK_ds.addString("Source", "SC01", 4);
			dataNAK_ds.addString("Destination", "OD01", 4);
			dataNAK_ds.addString("PacketGroup", "03", 2);
			dataNAK_ds.addString("Length", "00000020", 8); // Length = STX ~ ETX	
			dataNAK_ds.addByte  ("STX", (byte) 0x02);
			dataNAK_ds.addString("ODTNo", "", 2);
			dataNAK_ds.addString("Code", "0003", 4);
			dataNAK_ds.addString("DataName", "Data NAK", 12);
			dataNAK_ds.addByte  ("ETX", (byte) 0x03);
			dataNAK_ds.addString("CRC", "", 4); // 4 bytes
			dataNAK_Buf = dataNAK_ds.getByteStream();

			// Data EOT
			dataEOT_ds.addByte  ("SOH", (byte) 0x01);
			dataEOT_ds.addString("Source", "SC01", 4);
			dataEOT_ds.addString("Destination", "OD01", 4);
			dataEOT_ds.addString("PacketGroup", "03", 2);
			dataEOT_ds.addString("Length", "00000020", 8); // Length = STX ~ ETX	
			dataEOT_ds.addByte  ("STX", (byte) 0x02);
			dataEOT_ds.addString("ODTNo", "", 2);
			dataEOT_ds.addString("Code", "0004", 4);
			dataEOT_ds.addString("DataName", "Data EOT", 12);
			dataEOT_ds.addByte  ("ETX", (byte) 0x03);
			dataEOT_ds.addString("CRC", "", 4); // 4 bytes
			dataEOT_Buf = dataEOT_ds.getByteStream();
	
		} catch (Exception e) {
			LogUtility.getLogger().debug(e.getMessage());
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
			registerChannel(selector, sc, SelectionKey.OP_READ /*| SelectionKey.OP_WRITE*/); // OP_WRITE ������
		
		} catch (ClosedChannelException e) {
			LogUtility.getLogger().error(e.getMessage(), e);
		} catch (IOException e) {
			LogUtility.getLogger().error(e.getMessage(), e);
		}
	}
	
	
	private void businessMessageProc(SocketChannel sc, byte[] rcvBytes, ByteBuffer rcvByteBuffer, ByteBuffer sndByteBuffer) {
		
		String msgCmd = PacketUtil.getDataCommand(rcvBytes);
		
		try {
			if (dispLevel >= 2) {
				byte[] logBytes;
				if (msgCmd.equals("GA"))
					logBytes = GaLog.gaLog(PacketUtil.pack(rcvBytes));
				else
					logBytes = PacketUtil.pack(rcvBytes);					
				LogUtility.getLogger().info("Recv STX("+ ODTNo + ") : [" + new String(logBytes)+"]");
				//Log.datas(logBytes, logBytes.length, 20);
				ClearUtil.setClearString(logBytes);
				logBytes=null;
			}
						
			if (isOdtInitCompleted()==true) {
				boolean dataOK = service.verifyRecvData (rcvBytes); // ������ ����
				
				if (dataOK == true) { //-- ���� ������ ���
					if (!msgCmd.equals("S3") && !msgCmd.equals("S8")) {
						//PacketUtil.setPacketODTNo(dataACK_Buf, ODTNo);
						dataACK_ds.editString("Source", PacketUtil.getPacketDst(rcvBytes), 4);
						dataACK_ds.editString("Destination", PacketUtil.getPacketSrc(rcvBytes), 4);
						dataACK_ds.editString("ODTNo", PacketUtil.getBodyODTNo(rcvBytes), 2);
						dataACK_ds.editString("Echo", PacketUtil.getPacketCRC(rcvBytes), 4);
						dataACK_Buf = dataACK_ds.getByteStream();
						
						//Thread.sleep(10);						
						sndByteBuffer = transBytesToByteBuffer(dataACK_Buf);
						writeBuffer(sc, sndByteBuffer, dataACK_Buf.length);
						if (dispLevel >= 2)
							LogUtility.getLogger().info("Send STX("+ ODTNo + ") : [" + new String(dataACK_Buf)+"]");
					}
					
					//-- ���ŵ� ���� ó�� --//
					//Thread.sleep(10);
					uploadMessage(rcvBytes);
				}
				else { //-- �̻� ���� ����
					PacketUtil.setPacketODTNo(dataNAK_Buf, ODTNo);
					sndByteBuffer = transBytesToByteBuffer(dataNAK_Buf);
					writeBuffer(sc, sndByteBuffer, dataNAK_Buf.length);
					if (dispLevel >= 2)
						LogUtility.getLogger().info("Send STX("+ ODTNo + ") : Data NAK : [" + new String(dataNAK_Buf)+"]");
					//Log.datas(dataNAK_Buf, dataNAK_Buf.length, 20);
					
				}
			}
			else
				LogUtility.getLogger().info("STX("+ ODTNo + "), TCP/IP �ʱ�ȭ �̿Ϸ��̹Ƿ� �����͸� ó���� �� �����ϴ�.");
		}
		catch (Exception e) {
			LogUtility.getLogger().info("STX("+ ODTNo + "), ������ ���� ����!");
			LogUtility.getLogger().error(e.getMessage(), e);
			//Log.datas(rcvBytes, PacketUtil.pack(rcvBytes).length, 20);
			ClearUtil.setClearString(rcvBytes);
			ClearUtil.setClearString(rcvByteBuffer);
			rcvBytes=null;
		}					
	}

	private void clearBuffer(ByteBuffer buffer) {
		if (buffer != null) {
			buffer.clear();
			buffer = null;
		}
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
	

	private void closeServer() {
		try {
			this.serverSocket.close();
			this.serverSocket.bind(null);
			this.serverSocketChannel.close();
		} catch (Exception e) {
			LogUtility.getLogger().error(e.getMessage(), e);
		}
	}
	
	private void createThread() {
		
		m_thread = new Thread(new Runnable() {
			public void run() {		
				
				//-- ȯ������ �ٿ�ε� Ȯ��
				for (int i=0; i<5; i++) {
					if (isRcvEnvDataOK()==false) {
						try {
							//LogUtility.getLogger().info("STX("+ ODTNo + "), ȯ������ �̼������� GSocketServer �������� ����! i=" + i);
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}				
				
				if (m_isRunning) {
					if(! initServer() ) { // �ʱ�ȭ
						LogUtility.getLogger().error("TCP/IP - Init GSocketServer FAIL!!! ODTNo=" + ODTNo);						
						closeServer(); // ���� Ŭ��¡			
						return;
					} else {
						LogUtility.getLogger().error("TCP/IP - Init GSocketServer SUCCESS!");
					}
					
					startServer(); // ���� ����
				}
			}
		});
		
		m_thread.setName("GSocketServer_" + ODTNo);
		//Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		m_thread.start();
	}
	

/*	private boolean ODTInitProcess (SelectionKey key, SocketChannel sc, byte[] rcvBytes) throws Exception {
		
		LogUtility.getLogger().info("");
		LogUtility.getLogger().info("---------------------TCP/IP �ʱ�ȭ ����---------------------");
		LogUtility.getLogger().info("RECV <<< Version REQ : ODTNo=" + PacketUtil.getPacketODTNo(rcvBytes));
		LogUtility.getLogger().info("recv data = " + new String(PacketUtil.pack(rcvBytes)));
		//Log.datas(rcvBytes, 60, 20);
		
		verREQ_ds.setByteStream(rcvBytes);
					
		//---- ������ �۽� ----//	
		verACK_ds.editString("ODTNo", (String) verREQ_ds.getValue("ODTNo"), 2);
		verACK_ds.editString("Source", source, 4);
		verACK_ds.editString("Destination", (String) verREQ_ds.getValue("Source"), 4);
		byte[] sndBytes = verACK_ds.getByteStream();
			
		m_sndByteBuffer = transBytesToByteBuffer(sndBytes);
		writeBuffer(sc, m_sndByteBuffer, sndBytes.length);	
			
		LogUtility.getLogger().info("");
		LogUtility.getLogger().info("SEND >>> Version ACK : ODTNo=" + PacketUtil.getPacketODTNo(sndBytes));	
		LogUtility.getLogger().info("send data = " + new String(sndBytes));												
		//Log.datas(sndBytes, sndBytes.length, 20);
		
		//---- ������ ���� (Input Ready) ----//		
		boolean rcvInputReady = false;
		for (int cnt=0; cnt<60; cnt++) {
			boolean rcvOk = readBuffer(sc, m_rcvByteBuffer, m_rcvByteBuffer.limit());
			
			if (rcvOk == true) {
				rcvInputReady = true;
				break;
			}
			else
				Thread.sleep(50);
		}

		if (rcvInputReady == false) {														
			LogUtility.getLogger().info("Input Ready none received!!!");
			closeConnection(key, sc);
			return false;
		}
		
		if (m_rcvByteBuffer.position() != 0) { // �����Ͱ� ������
			rcvBytes = transByteBufferToBytes(m_rcvByteBuffer);
			
			if (PacketUtil.getPacketCode(rcvBytes).equals("0003")) {				
				LogUtility.getLogger().info("");
				LogUtility.getLogger().info("RECV <<< Input Ready : ODTNo=" + PacketUtil.getPacketODTNo(rcvBytes));
				LogUtility.getLogger().info("recv data = " + new String(PacketUtil.pack(rcvBytes)));
				//Log.datas(rcvBytes, 60, 20);
																	
				// ODT �ʱ�ȭ �Ϸ�
				setOdtInitCompleted(true);
				
				LogUtility.getLogger().info("---------------------TCP/IP �ʱ�ȭ �Ϸ�---------------------\n");
				LogUtility.getLogger().info("TCP/IP - Client init completed... : Client IP=" + clientIP + 
						", port=" + clientPort + ", ODTNo=" + ODTNo + "\n");

				return true;
			}
			else 
				return false;
		}
		else 		
			return false;
	}*/
	
	
	//--- ������ �ۼ��� �� ������ ó�� ---//
	private void dataProcessing (SelectionKey key) {

		SocketChannel sc = (SocketChannel) key.channel();
		ByteBuffer rcvByteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);  // ���ſ�
		ByteBuffer sndByteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);	// �۽ſ�

		boolean isRcvVerREQ = false;
		boolean isRcvInputReady = false;
		byte[] rcvBytes = null;
		boolean isLineErr = false;
		gSockTimer.resetTime();
		
		while (true) {
			
			try {				
				// ����Ȯ�� ó����				
				long timeGap = getCurrentDeciSeconds();
				isLineErr = timeGap > 100 ? true : false; // 10�ʰ� � �����͵� �� ����

				//LogUtility.getLogger().info("ODTNo=" + ODTNo + "-> currTime=" + currTime + ", lastRcvedDataTime=" + lastRcvedDataTime  + ", diff=" + diff);
				
				if (isLineErr) { 
					LogUtility.getLogger().info("TCP/IP - Client disconnected! : Client IP=" + clientIP + 
							", port=" + clientPort + ", ODTNo=" + ODTNo + "\n");
										
					setConnected(false);
					closeConnection(key, sc);
					return;
				}
				
	//			if (TxQue.getItemCount() > 0 && isOdtInitCompleted()==false) {
	//				LogUtility.getLogger().info("STX("+ ODTNo + "), TCP/IP �ʱ�ȭ �̿Ϸ��̹Ƿ� �����͸� ó���� �� �����ϴ�.");
	//				TxQue.flushQueue();
	//				return;
	//			}
	
				if (TxQue.getItemCount() > 0 && isOdtInitCompleted()==true) { // ���� ������ ������
	
					byte[] sndBytes = TxQue.getFirstItem();
					sndByteBuffer = transBytesToByteBuffer(sndBytes);	
									
					for (int cnt=0; cnt<3; cnt++) {
						writeBuffer(sc, sndByteBuffer, sndBytes.length); // ������ ����
	
						if (dispLevel >= 2) {
							LogUtility.getLogger().info("Send STX("+ ODTNo + ") : [" + new String(PacketUtil.pack(sndBytes))+"]");
						}
	
						//---- ACK ���Ŵ�� �� �̼��� �� ������ ó��
						boolean isRcvACK = false;
						for (int i=0; i<100; i++) { // ACK �߰� ���ð� : �ִ� 2 sec	
	
							boolean isRecvd = readBuffer(sc, rcvByteBuffer);							
	
							if (isRecvd) {	
								
								Vector<byte[]> datas = splitData (transByteBufferToBytes(rcvByteBuffer));
								
								for (int j=0; j<datas.size(); j++) 
								{
									rcvBytes = datas.get(j);
							
									if (PacketUtil.getPacketGroup(rcvBytes).equals("03")) { //-- Data ACK ����ó��
										if (PacketUtil.getPacketCode(rcvBytes).equals("0002")) { // ACK ������ ���
											isRcvACK = true;
											gSockTimer.resetTime();
											
											if (dispLevel >= 2)
												LogUtility.getLogger().info("Recv STX("+ ODTNo + ") : [" + new String(PacketUtil.pack(rcvBytes))+"]");
											continue; // ��������
										}
										else if (PacketUtil.getPacketCode(rcvBytes).equals("0003")) { // NAK ������ ���									
											writeBuffer(sc, sndByteBuffer, sndBytes.length);
											i=0;	
											gSockTimer.resetTime();
											continue; // ACK �� ��������
										}
										else { // �ٸ� �����ʹ� ����
											gSockTimer.resetTime();
											//LogUtility.getLogger().info("RECV <<< Data ??? : " + new String(pack(rcvBuf)));
										}
									}
									else if (PacketUtil.getPacketGroup(rcvBytes).equals("04")) { //-- ������ ���� ����ó��
										gSockTimer.resetTime();	
										businessMessageProc(sc, rcvBytes, rcvByteBuffer, sndByteBuffer);
										
										ClearUtil.setClearString(rcvBytes);	
										rcvBytes=null;
									}
								}
								
								if (isRcvACK==true) { // ACK ����
									break;
								}
								else { // ACK �̼���
									continue;
								}
							}
	
							ClearUtil.setClearString(rcvByteBuffer);
							
							Thread.sleep(20);
						} // End of ACK ���
											
						if (isRcvACK==true) { // ACK ����
							break;
						}
						else { // ACK �̼���
							continue;
						}
					}
	
					//ClearUtil.setClearString(sndByteBuffer);
					//ClearUtil.setClearString(sndBytes);				
					//LogUtility.getLogger().info("STX("+ ODTNo + "), ###### �۽� �޸� Ŭ���� ����.................");
					
					TxQue.deQueue(); // �۽�ť���� ����
				}
									
				//---- ������ �б� ----//
				selector.selectNow();
				boolean isRecvd = readBuffer(sc, rcvByteBuffer);
	
				// ���� �����Ͱ� ������
				if (isRecvd) {														
					Vector<byte[]> datas = splitData (transByteBufferToBytes(rcvByteBuffer));
					
					for (int i=0; i<datas.size(); i++) {	
						
						rcvBytes = datas.get(i);
										
						//---- ������ Ÿ�� Ȯ�� �� ó��----//
						if (PacketUtil.getPacketGroup(rcvBytes).equals("01")) { //== �ʱ�ȭ ���� ����
		
							if (PacketUtil.getPacketCode(rcvBytes).equals("0001")) { //-- Version REQ ����
		
								LogUtility.getLogger().info("");
								LogUtility.getLogger().info("---------------------TCP/IP �ʱ�ȭ ����---------------------");
								LogUtility.getLogger().info("RECV <<< Version REQ : ODTNo=" + PacketUtil.getPacketODTNo(rcvBytes));
								LogUtility.getLogger().info("recv data = " + new String(PacketUtil.pack(rcvBytes)));
								//Log.datas(rcvBytes, 60, 20);
												
								isRcvVerREQ = true;					
								verREQ_ds.setByteStream(rcvBytes);
															
								//---- ������ �۽� ----//	
								verACK_ds.editString("ODTNo", (String) verREQ_ds.getValue("ODTNo"), 2);
								verACK_ds.editString("Source", source, 4);
								verACK_ds.editString("Destination", (String) verREQ_ds.getValue("Source"), 4);
								byte[] sndBytes = verACK_ds.getByteStream();
													
								sndByteBuffer = transBytesToByteBuffer(sndBytes);
								writeBuffer(sc, sndByteBuffer, sndBytes.length);
													
								LogUtility.getLogger().info("");
								LogUtility.getLogger().info("Send STX("+ PacketUtil.getPacketODTNo(sndBytes) + "), Version ACK");	
								LogUtility.getLogger().info("send data = " + new String(sndBytes));
								//Log.datas(sndBytes, sndBytes.length, 20);
							}
							else if (PacketUtil.getPacketCode(rcvBytes).equals("0003")) { //-- Input ready ����
																
								LogUtility.getLogger().info("");
								LogUtility.getLogger().info("Recv STX("+ PacketUtil.getPacketODTNo(rcvBytes) + "), Input Ready");	
								LogUtility.getLogger().info("recv data = " + new String(PacketUtil.pack(rcvBytes)));
								//Log.datas(rcvBytes, 60, 20);															
												
								if (isRcvVerREQ==true) {
									// ODT �ʱ�ȭ �Ϸ�
									isRcvInputReady = true;
									setOdtInitCompleted(true);
									setStartInitCompletedFlag(isOdtInitCompleted());
															
									LogUtility.getLogger().info("---------------------TCP/IP �ʱ�ȭ �Ϸ�---------------------\n");
									LogUtility.getLogger().info("TCP/IP - Client init completed... : Client IP=" + clientIP + 
																", port=" + clientPort + ", ODTNo=" + ODTNo + "\n");
								}
								else 
									LogUtility.getLogger().info("STX("+ ODTNo + ") TCP/IP �ʱ�ȭ �̿Ϸ� : VerREQ ����=" + isRcvVerREQ + ", InputReady ����=" + isRcvInputReady);
							}
						}
						else if (PacketUtil.getPacketGroup(rcvBytes).equals("02")) { //== ����Ȯ�� ���� ����
		
							if (PacketUtil.getPacketCode(rcvBytes).equals("0001")) { //-- Connection REQ ����
								
								if (dispLevel >= 3)
								{
									LogUtility.getLogger().info("Recv STX(" + ODTNo + ") : [" + new String(PacketUtil.pack(rcvBytes))+"]");
								}	
								else if(dispLevel >= 2)
								{
									if(connectCount % 3 == 0)
									{
										LogUtility.getLogger().info("Recv STX(" + ODTNo + ") : [" + new String(PacketUtil.pack(rcvBytes))+"]");
									}
								}
								connectREQ_ds.setByteStream(rcvBytes); // �������� ���� ������
								
								//if (isOdtInitCompleted()) {
									//---- ����Ȯ�� ���� �۽� ----//
									connectACK_ds.editString("ODTNo", (String) connectREQ_ds.getValue("ODTNo"), 2);
									connectACK_ds.editString("Source", source, 4);
									connectACK_ds.editString("Destination", (String) connectREQ_ds.getValue("Source"), 4);
									byte[] sndBytes = connectACK_ds.getByteStream();
									sndByteBuffer = transBytesToByteBuffer(sndBytes);
									writeBuffer(sc, sndByteBuffer, sndBytes.length);
			
									gSockTimer.resetTime();
									
									if (dispLevel >= 3)
									{
										LogUtility.getLogger().info("Send STX(" + ODTNo + ") : [" + new String(PacketUtil.pack(sndBytes))+"]");
									}	
									else if(dispLevel >= 2)
									{
										if(connectCount % 3 == 0)
										{
											LogUtility.getLogger().info("Send STX(" + ODTNo + ") : [" + new String(PacketUtil.pack(sndBytes))+"]");
											connectCount = 0;
										}
										
										connectCount++;
									}	
								//}
							}
						}
						else if (PacketUtil.getPacketGroup(rcvBytes).equals("04")) { //== ������ ���� ����
	
							gSockTimer.resetTime();					
							businessMessageProc(sc, rcvBytes, rcvByteBuffer, sndByteBuffer);
							
							ClearUtil.setClearString(rcvBytes);
							rcvBytes=null;
						}
						else { //== �ٸ� �����ʹ� ����
							gSockTimer.resetTime();
							//LogUtility.getLogger().info("RECV <<< Data ??? : " + new String(rcvBuf));
						}
					}
					
					ClearUtil.setClearString(rcvByteBuffer);
				}
	
				Thread.sleep(50);
			
			} catch (Exception e) {
				LogUtility.getLogger().error(e.getMessage(), e);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		} //--- End of ������ �ۼ��� ó�� ---//
	
	}
	
	
	public long getCurrentDeciSeconds() {
		
		try {	
			return gSockTimer.getDeciSeconds();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
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
		
	public synchronized SocketChannel getSocketChannel() {
		
		return m_sc ;
	}
	
	private boolean initServer() {
		
		InetSocketAddress isa = null;
		
		// �����͸� ����.
		try {
			//������ ����
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
	
	public synchronized boolean isConnected() {
		return isConnected;
	}
	
	public synchronized boolean isOdtInitCompleted() {
		return odtInitCompleted;
	}
	

//	public void addListener(GServerService service) {
//		serviceList.add(service);
//	}
//
//	public void removeListener(GServerService service) {
//		serviceList.remove(service);
//	}


	public synchronized boolean isRcvEnvDataOK() {
		return isRcvEnvDataOK;
	}

	
	public synchronized boolean isStartInitCompletedFlag() {
		return startInitCompletedFlag;
	}

	public boolean readBuffer(SocketChannel sc, ByteBuffer byteBuffer) {
		
		//ByteBuffer byteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

/*		try {
			byteBuffer.clear();				
			sc.read(byteBuffer);	
			
			if (byteBuffer.position() > 0) { // ������ ����
				while (byteBuffer.hasRemaining() && byteBuffer.position() < size)
					byteBuffer.get();
				
				byteBuffer.flip();		
				return true;
			} 
			else {// ������ �̼���
				//byteBuffer.flip();
				return false;
			}
			
		} catch (Exception e) {
			//LogUtility.getLogger().error(e.getMessage(), e);
			//byteBuffer.flip();
			return false;
		}*/
		/**
		 * socket �дºκ��� ���� (��ű��̰� ������鼭 ������ �κ��� �����ϱ����ؼ� ???)
		 * 2021.03.16 
		 * by Soonkwan
		 */

		int read = 0;
		int totalRead = 0;
		boolean isRead = true;
		int readCount = 0;
		int iSize = 0;
		byte[] bySize = new byte[4];
		
		
		try {
			
			sc.socket().setTcpNoDelay(true);
			byteBuffer.clear();
			
			do{
				read = sc.read(byteBuffer);
								
				// Socket read �ϱ� ��������  300�и��ʸ� ��ٸ���.
				if(read == 0) {					
					if (totalRead == 0) {
						isRead = false;
						continue;
					}
					
					if(readCount++ > 30) {
						isRead = false;
					}
					else {
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					continue;	
				}
				
				//Socket disconnected
				if (read == -1) {
					isRead = false;
					continue;
				}

				totalRead += read;
				
				if(totalRead < 20) continue;
				
				//Size�� �о�´�
				for(int i= 0;i<4;i++)
					bySize[i] = byteBuffer.get(i+15);
				
				iSize = Integer.parseInt(new String(bySize)) + 23;
				
				if(totalRead >= iSize) 
					isRead = false;
				
				//LogUtility.getSOCKETLogger().info(" totalRead = ["+ totalRead + "]  numRead = ["+ read+"] Size = [" + iSize +"] readCount = ["+readCount+"]");
				
			}while(isRead);
						
			if(read == -1) {
				LogUtility.getSOCKETLogger().info("disconnect . port=" + port);
				return false;
			}
			
			if(read == 0) return false;
			
			byteBuffer.flip();		
			return true;
			
		} catch (IOException e1) {
			LogUtility.getSOCKETLogger().info("[GSSocketServer] disconnect . port=" + port) ;
			return false;
		}
		
		/*****
		try {
			byteBuffer.clear();				
			int size = sc.read(byteBuffer);	
			
			if (size > 0) { // ������ ����
				//while (byteBuffer.hasRemaining())
				//	byteBuffer.get();
				
				byteBuffer.flip();		
				return true;
			} 
			else {// ������ �̼���
				return false;
			}
			
		} catch (Exception e) {
			return false;
		}
		****/
	}	
	
	// ������
	private int readn(SocketChannel sc, ByteBuffer byteBuffer, int length) throws Exception {
		
		int nLeft, nRead=0;
		nLeft = length;
		
		while (nLeft > 0) {
			if ( (nRead = sc.read(byteBuffer)) < 0) { // fail
				return 0;
			}
			else if (nRead==0)
				break;
			
			nLeft -= nRead;
			Sleep.sleep(20);
		}
		
		return (length - nLeft);
	}

	private void registerChannel(Selector selector, SocketChannel sc,int ops) throws ClosedChannelException, IOException {
		// TODO Auto-generated method stub
		if(sc == null) {
			LogUtility.getLogger().info("Invalid Connection");
			return;
		}
		
		sc.configureBlocking(false);
		sc.register(selector, ops);
	}


	public synchronized void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}


	public synchronized void setOdtInitCompleted(boolean odtInitCompleted) {
		this.odtInitCompleted = odtInitCompleted;
	}


	public synchronized void setRcvEnvDataOK(boolean isRcvEnvDataOK) {
		this.isRcvEnvDataOK = isRcvEnvDataOK;
	}


	public void setStartInitCompletedFlag(boolean startInitCompletedFlag) {
		this.startInitCompletedFlag = startInitCompletedFlag;
	}

	public void start(final String HOST, final int PORT, String ODTNO) {
		// ���� �ʱ�ȭ
		this.host = HOST;
		this.port = PORT;
		this.ODTNo = ODTNO;
		
		if (!m_isRunning) {
			m_isRunning = true;
			m_isStartedServer = true;
			
			createThread();	// ������ ����	
		}
	}

	private void startServer() {

		LogUtility.getLogger().debug("TCP/IP - Server starting...");
		boolean isExitLoop;
		//SelectionKey m_key = null;

		while (m_isStartedServer) {
			
			try {
				isExitLoop = false;

				if (isConnected() == false) {
					LogUtility.getLogger().info("TCP/IP - Open server socket : Server IP=" + host + ", port=" + port + " - is received env. datas=" + isRcvEnvDataOK());
					LogUtility.getLogger().info("TCP/IP - Client connection WAITING.........\n");
				}

				while (true) {
					Thread.sleep(1);
					
					int rtn = selector.select();
					//LogUtility.getLogger().info("ODTNo="+ ODTNo + ", Selector return=" + rtn);
					
					Iterator it = selector.selectedKeys().iterator();
					
					while (it.hasNext()) {
						
						SelectionKey key = (SelectionKey) it.next();
						//m_key = key;
						
						if (key.isAcceptable()) {
							// ���� ���� ä�ο� Ŭ���̾�Ʈ�� ������ �õ��� ���
							accept(key);
						} 
						else if (isConnected() && key.isReadable()) {
							
							dataProcessing(key);
							
							isExitLoop = true;
							break;
						}
						// �̹� ó���� �̺�Ʈ �̹Ƿ� �ݵ�� ������
						it.remove();
					}					
					if (isExitLoop == true) break;
				}
				
			} catch (Exception e) {
				LogUtility.getLogger().error(e.getMessage(), e);
			}		
		}
		
		LogUtility.getLogger().debug("ODTNo="+ ODTNo + ", Exit waiting loop...");

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
	
	public byte[] transByteBufferToBytes(ByteBuffer bytebuffer) {
		
		try {
			//String str = new String(bytebuffer.array());
			//byte[] byt = bytebuffer.array();
			byte[] byt = new byte[bytebuffer.limit()];
			bytebuffer.get(byt);
			//ClearUtil.setClearString(bytebuffer);
			return byt;		
		} catch (Exception e) {
			LogUtility.getLogger().error(e.getMessage(), e);
			return null;
		}
	}


	public ByteBuffer transBytesToByteBuffer(byte[] bytes) {
		
		try {       
			ByteBuffer byteBuf = ByteBuffer.allocate(bytes.length);
			byteBuf.put(bytes);
			//ClearUtil.setClearString(bytes);
			return byteBuf;
		} catch (Exception e) {
			LogUtility.getLogger().error(e.getMessage(), e);
			return null;
		}
	}

	/* head ������ ��������  ��Ŷ�� �и��Ͽ� �����Ѵ�.
	 * 
	 * */
	
	private Vector<byte[]> splitData (byte[] bytesAll) {

		Vector vec = new Vector<byte[]>();
		byte[] stx = new byte[1];
		byte[] byLen = new byte[8];
		int len = 0;
		int totalLen = 0;
		
		try 
		{	
			// ������ ����� ����			
			for (int i=0; i<bytesAll.length-19; i++) 
			{ 
//				 SOH �� ������ ����
				if (bytesAll[i] == 0x01) 
				{
//					������ 20��°�� STX ���� üũ�Ѵ�.
					if(bytesAll[i+19] == 0x02)
					{
						System.arraycopy(bytesAll, i + 11, byLen, 0, 8);
						len = Change.toValue(new String(byLen));
						
						totalLen = len + 23;
						if(totalLen < bytesAll.length+1)
						{
//						 body ������ ������ ETX�� �����ϴ��� �˻��Ѵ�.
							if(bytesAll[i+totalLen-5] == 0x03)
							{
								byte[] buf = new byte[totalLen];
								
								System.arraycopy(bytesAll, i, buf, 0, totalLen);
								vec.add(buf);
								i = i + totalLen-1;
							}
						}
					}
				}
			}
		} 
		catch (Exception e) 
		{		
			e.printStackTrace();	
		}

		return vec;	
	}
	
	
	private void uploadMessage (byte[] byBuf) {

		service.recvMessage(byBuf);
	}


	public void writeBuffer(SocketChannel sc, ByteBuffer byteBuffer, int size) {

		try {
			byteBuffer.clear();
			byteBuffer.limit(size);
			
			sc.write(byteBuffer);
			while (byteBuffer.hasRemaining())
				sc.write(byteBuffer);
			
			//ClearUtil.setClearString(byteBuffer);
						
		} catch (Exception e) {
			//LogUtility.getLogger().error(e.getMessage(), e);
		}
	}
		
}
