package com.gsc.kixxhub.device.pumpa.controller;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public interface IServerService2 {

	//public SocketChannel getSocketChannel();

	/**
	 * @param sc
	 * 클라이언트에서 연결이 끊어졌을 때 처리하는 함수
	 */
	public void closingRequest(SocketChannel sc);
		
	/**
	 * @param sc
	 * 연결 요청이 들어와서 연결 설정되었을 때 처리하는 함수
	 */
	public void connectionRequest(SocketChannel sc);
		
	/**
	 * @param sc
	 * @param bytebuffer
	 * @param size
	 * 
	 * 클라이언트로부터의 메세지를 읽는 함수
	 */
	public void recvMessage(SocketChannel sc, ByteBuffer bytebuffer, int size);
	
	/**
	 * @param sc
	 * @param bytebuffer
	 * @param size
	 * 
	 * 클라이언트로 메세지를 쓰는 함수
	 */
	public void sendMessage(SocketChannel sc, ByteBuffer bytebuffer, int size);
		
}
