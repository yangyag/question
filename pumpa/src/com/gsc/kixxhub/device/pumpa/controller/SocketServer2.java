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

		// ��������ä���� accept() �޼ҵ�� �������� ����.
		try {
			sc = server.accept();
			m_sc = sc;
			// ������ ����ä���� ����ŷ �� �б� ���� �����Ϳ� ���.
			registerChannel(selector, sc, SelectionKey.OP_READ);
			// // ó�� ���� �Ǿ��� �� ó��
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
		// �����͸� ����.
		try {
			selector = Selector.open();
			// ��������ä�� ����.
			serverSocketChannel = ServerSocketChannel.open();
			// ����ŷ ���� ����.
			serverSocketChannel.configureBlocking(false);
			// ��������ä�ΰ� ����� �������� ������.
			serverSocket = serverSocketChannel.socket();
			// �־��� �Ķ���Ϳ� �ش��ϴ� �ּ�, ��Ʈ�� ���������� ���ε�.
			if(host == null) {
				isa = new InetSocketAddress(port);
			} else {
				isa = new InetSocketAddress(host, port);
			}
			
			serverSocket.bind(isa);

			// ��������ä���� �����Ϳ� ���.
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
			//numRead = sc.read(recvBuffer); // Header ������ �б�
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
				//numRead = sc.read(recvBuffer); // ���� ������ �б�
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
				readMessage(sc, recvBuffer, length); // �б� �޼ҵ� ȣ��
			} catch (Exception e) {
				LogUtility.getPumpALogger().error(e.getMessage(), e);
			}
			
		}
		// ���� �޸𸮸� ��������.
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
		// ���� �ʱ�ȭ
		this.host = null;
		this.port = PORT;
		if (!isRunning) {
			isRunning = true;
			isStartedServer = true;
			createThread();
			
		}
	}

	public void start(final String HOST, final int PORT) {
		// ���� �ʱ�ȭ
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
				// �������� select() �޼ҵ�� �غ�� �̺�Ʈ�� �ִ��� üũ.
				selector.select();
				// �������� SelectedSet �� ����� �غ�� �̺�Ʈ��(SelectionKey��)�� �ϳ��� ó��.
				Iterator it = selector.selectedKeys().iterator();
				while (it.hasNext()) {
					SelectionKey key = (SelectionKey) it.next();
					if (key.isAcceptable()) {
						// ��������ä�ο� Ŭ���̾�Ʈ�� ������ �õ��� ���.
						accept(key);
					} else if (key.isReadable()) {
						// �̹� ����� Ŭ���̾�Ʈ�� �޼����� ���� ���.
						read(key);
					}
					// �̹� ó���� �̺�Ʈ�̹Ƿ� �ݵ�� ��������.
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
