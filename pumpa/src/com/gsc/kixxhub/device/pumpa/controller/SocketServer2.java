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
import java.util.ArrayList;
import java.util.Iterator;

import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.DataStruct;
import com.gsc.kixxhub.device.pumpa.common.Log;
import com.gsc.kixxhub.device.pumpa.common.Sleep;


public class SocketServer2 {
	
	protected int BUFFER_SIZE = 4096;
	protected DataStruct header_ds = new DataStruct(); // Header
	protected int HEADER_SIZE = 8;
	protected String host;
	protected boolean isRunning = false;
	protected boolean isStartedServer = false;
	protected byte[] 	 m_byData = new byte[BUFFER_SIZE];
	protected byte[] 	 m_byHeader = new byte[HEADER_SIZE];
	protected SocketChannel m_sc;
	protected int port;
	ByteBuffer recvBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

	protected Selector selector = null;

    protected ServerSocket serverSocket = null;
    protected ServerSocketChannel serverSocketChannel;
    protected ArrayList<IServerService2> serviceList = new ArrayList<IServerService2>();

	protected Thread thread = null;

	public SocketServer2(int headerSize, int bufferSize) {
		
		this.HEADER_SIZE = headerSize;
		this.BUFFER_SIZE = bufferSize;
		
		//--- header---//
		try {
			header_ds.addByte  ("srcUnitID", (byte) 0x10); // Address
			header_ds.addByte  ("srcSvrID", (byte) 0x01);
			header_ds.addByte  ("destUnitID", (byte) 0x20);
			header_ds.addByte  ("destSvrID", (byte) 0x00);
			header_ds.addByte  ("packetGroup", (byte) 0x00); // Control
			header_ds.addByte  ("packetID", (byte) 0x00);
			header_ds.addShort ("length", (short) 0);
			m_byHeader = header_ds.getByteStream();
		} catch (Exception e) {
			LogUtility.getPumpALogger().debug(e.getMessage());
		} 
	}

	private void accept(SelectionKey key) {
		ServerSocketChannel server = (ServerSocketChannel) key.channel();
		SocketChannel sc;

		// 서버소켓채널의 accept() 메소드로 서버소켓 생성.
		try {
			sc = server.accept();
			m_sc = sc;
			// 생성된 소켓채널을 비블록킹 및 읽기 모드로 셀렉터에 등록.
			registerChannel(selector, sc, SelectionKey.OP_READ);
			// // 처음 연결 되었을 시 처리
			readConnectionRequest(sc);
		} catch (Exception e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	}

	public void addListener(IServerService2 service) {
		serviceList.add(service);
	}
	
	private void clearBuffer(ByteBuffer buffer) {
		if (buffer != null) {
			buffer.clear();
			buffer = null;
		}
	}

	private void closeServer() {
		try {
			this.serverSocket.close();
			this.serverSocketChannel.close();
		} catch (Exception e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	}

	private void createThread() {
		thread = new Thread(new Runnable() {
			public void run() {
				if (isRunning) {
					if(! initServer() ) {
						LogUtility.getPumpALogger().error("INIT SERVER FAIL...");
						closeServer();
						return;
					} else {
						LogUtility.getPumpALogger().error("INIT SERVER OK..!!");
					}
					startServer();
				}
			}
		});
		thread.setName("PumpA NIO SocketServer2");
		thread.start();
	}

	public SocketChannel getM_sc() {
		return m_sc;
	}

	public Selector getSelector() {
		return selector;
	}

	private boolean initServer() {
		InetSocketAddress isa = null;
		// 셀렉터를 연다.
		try {
			selector = Selector.open();
			// 서버소켓채널 생성.
			serverSocketChannel = ServerSocketChannel.open();
			// 비블록킹 모드로 설정.
			serverSocketChannel.configureBlocking(false);
			// 서버소켓채널과 연결된 서버소켓 가져옴.
			serverSocket = serverSocketChannel.socket();
			// 주어진 파라미터에 해당하는 주소, 포트로 서버소켓을 바인드.
			if(host == null) {
				isa = new InetSocketAddress(port);
			} else {
				isa = new InetSocketAddress(host, port);
			}
			
			serverSocket.bind(isa);

			// 서버소켓채널을 셀렉터에 등록.
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (Exception e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
			return false;
		}

		return true;
		
	}

	private void read(SelectionKey key) {
		
		SocketChannel sc = (SocketChannel) key.channel();
		int numRead=0, length=0;
		
		//---- Header Data read ----//
		try {
			recvBuffer.clear();
			recvBuffer.limit(HEADER_SIZE);
			//numRead = sc.read(recvBuffer); // Header 데이터 읽기
			numRead = readn (sc, recvBuffer, HEADER_SIZE);
			//LogUtility.getPumpALogger().debug("Recv header : " + recvBuffer);
			
			if (numRead == -1) {
				readClosingRequest(sc);
				key.cancel();
				return;
			}
			
		} catch (Exception e) {
			readClosingRequest(sc);
			key.cancel();
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
		
		try {
			recvBuffer.rewind();
			recvBuffer.get(m_byHeader, 0, HEADER_SIZE);
			header_ds.setByteStream(m_byHeader);
			length = (Short) header_ds.getValue("length");
			
			LogUtility.getPumpALogger().debug("@@@@ read header :" + recvBuffer);
			LogUtility.getPumpALogger().debug("payload length=" + length);
			Log.datas (m_byHeader, HEADER_SIZE, 20);
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		if (length > 0) {
			
			//Sleep.sleep(100);
			
			//---- Payload Data read ----//
			try {
				recvBuffer.limit(length + HEADER_SIZE);
				recvBuffer.position(HEADER_SIZE);
				//numRead = sc.read(recvBuffer); // 실제 데이터 읽기
				numRead = readn (sc, recvBuffer, length);

				recvBuffer.rewind();
				recvBuffer.get(m_byData, 0, length + HEADER_SIZE);
				LogUtility.getPumpALogger().debug("read data : " + recvBuffer);
				LogUtility.getPumpALogger().debug("numRead=" + numRead);
				Log.datas (m_byData, numRead, 20);
				
				if (numRead == -1) {
					readClosingRequest(sc);
					key.cancel();
					return;
				}
				
			} catch (Exception e) {
				readClosingRequest(sc);
				key.cancel();
				LogUtility.getPumpALogger().error(e.getMessage(), e);
			}
		}
		
		if (numRead > 0) {
			try {
//				recvBuffer.flip();
//				recvBuffer.rewind();
				readMessage(sc, recvBuffer, length); // 읽기 메소드 호출
			} catch (Exception e) {
				LogUtility.getPumpALogger().error(e.getMessage(), e);
			}
			
		}
		// 버퍼 메모리를 해제해줌.
		//clearBuffer(recvBuffer);
	}

	private void readClosingRequest(SocketChannel sc) {
		
		for (Iterator<IServerService2> iterator = serviceList.iterator(); iterator.hasNext();) {
			try {
				IServerService2 service = iterator.next();
				service.closingRequest(sc);
			} catch (Exception e) {
				LogUtility.getPumpALogger().error(e.getMessage(), e);
			}
		}
	}
	
	private void readConnectionRequest(SocketChannel sc) {
		
		for (Iterator<IServerService2> iterator = serviceList.iterator(); iterator.hasNext();) {
			try {
				IServerService2 service = iterator.next();
				service.connectionRequest(sc);
			} catch (Exception e) {
				LogUtility.getPumpALogger().error(e.getMessage(), e);
			}
		}
	}

	private void readMessage(SocketChannel sc, ByteBuffer bytebuffer, int size) {

		for (Iterator<IServerService2> iterator = serviceList.iterator(); iterator.hasNext();) {
			try {
				IServerService2 service = iterator.next();
				service.recvMessage(sc, bytebuffer, size);
			} catch (Exception e) {
				LogUtility.getPumpALogger().error(e.getMessage(), e);
			}
		}
	}
	
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

	private void registerChannel(Selector selector, SocketChannel sc, int ops)
			throws ClosedChannelException, IOException {
		if (sc == null) {
			new Exception("Invalid Connection");
			// log
			LogUtility.getPumpALogger().error("Invalid Connection");
			return;
		}
		sc.configureBlocking(false);
		sc.register(selector, ops);
	}

	public void removeListener(IServerService2 service) {
		serviceList.remove(service);
	}

	public void setSelector(Selector selector) {
		this.selector = selector;
	}

	public void start( final int PORT) {
		// 서버 초기화
		this.host = null;
		this.port = PORT;
		if (!isRunning) {
			isRunning = true;
			isStartedServer = true;
			createThread();
			
		}
	}

	public void start(final String HOST, final int PORT) {
		// 서버 초기화
		this.host = HOST;
		this.port = PORT;
		if (!isRunning) {
			isRunning = true;
			isStartedServer = true;
			createThread();
			
		}
	}

	public void startServer() {

		LogUtility.getPumpALogger().debug("Server is started..");

		try {
			while (isStartedServer) {
				// 셀렉터의 select() 메소드로 준비된 이벤트가 있는지 체크.
				selector.select();
				// 셀렉터의 SelectedSet 에 저장된 준비된 이벤트들(SelectionKey들)을 하나씩 처리.
				Iterator it = selector.selectedKeys().iterator();
				while (it.hasNext()) {
					SelectionKey key = (SelectionKey) it.next();
					if (key.isAcceptable()) {
						// 서버소켓채널에 클라이언트가 접속을 시도한 경우.
						accept(key);
					} else if (key.isReadable()) {
						// 이미 연결된 클라이언트가 메세지를 보낸 경우.
						read(key);
					}
					// 이미 처리한 이벤트이므로 반드시 삭제해줌.
					it.remove();
				}
			}
		} catch (Exception e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	}

	public void stop() {

		isRunning = false;
		isStartedServer = false;
		if (thread != null) {
			// socket close..
			closeServer();
			thread.interrupt();
			try {
				thread.join();
			} catch (InterruptedException e) {
				LogUtility.getPumpALogger().debug(e.getMessage());
			}
			thread = null;
		}
	}

	public void writeMessage(SocketChannel sc, ByteBuffer bytebuffer, int size) {

		try {
			sc.write(bytebuffer);
		} catch (Exception e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	}
}
