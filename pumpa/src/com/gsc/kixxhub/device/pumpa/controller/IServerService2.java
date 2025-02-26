package com.gsc.kixxhub.device.pumpa.controller;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public interface IServerService2 {

	//public SocketChannel getSocketChannel();

	/**
	 * @param sc
	 * Ŭ���̾�Ʈ���� ������ �������� �� ó���ϴ� �Լ�
	 */
	public void closingRequest(SocketChannel sc);
		
	/**
	 * @param sc
	 * ���� ��û�� ���ͼ� ���� �����Ǿ��� �� ó���ϴ� �Լ�
	 */
	public void connectionRequest(SocketChannel sc);
		
	/**
	 * @param sc
	 * @param bytebuffer
	 * @param size
	 * 
	 * Ŭ���̾�Ʈ�κ����� �޼����� �д� �Լ�
	 */
	public void recvMessage(SocketChannel sc, ByteBuffer bytebuffer, int size);
	
	/**
	 * @param sc
	 * @param bytebuffer
	 * @param size
	 * 
	 * Ŭ���̾�Ʈ�� �޼����� ���� �Լ�
	 */
	public void sendMessage(SocketChannel sc, ByteBuffer bytebuffer, int size);
		
}
