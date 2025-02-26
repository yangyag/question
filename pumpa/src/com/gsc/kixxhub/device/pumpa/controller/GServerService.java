package com.gsc.kixxhub.device.pumpa.controller;

public interface GServerService {

	//public SocketChannel getSocketChannel();

	/**
	 * @param sc
	 * @param bytebuffer
	 * @param size
	 * 
	 * 클라이언트로 메세지를 송신하는 함수
	 */
	public void recvMessage(byte[] buf);
	
	/**
	 * @param sc
	 * @param bytebuffer
	 * @param size
	 * 
	 * 클라이언트로부터의 메세지를 수신하는 함수
	 */
	public void sendMessage(byte[] buf);
	
	/**
	 * @param sc
	 * @param bytebuffer
	 * @param size
	 * 
	 * 데이터 검증하는 함수
	 */
	public boolean verifyRecvData(byte[] rcvBytes);

	//public boolean isRcvEnvDataOK();
		
}
