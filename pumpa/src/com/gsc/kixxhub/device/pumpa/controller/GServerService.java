package com.gsc.kixxhub.device.pumpa.controller;

public interface GServerService {

	//public SocketChannel getSocketChannel();

	/**
	 * @param sc
	 * @param bytebuffer
	 * @param size
	 * 
	 * Ŭ���̾�Ʈ�� �޼����� �۽��ϴ� �Լ�
	 */
	public void recvMessage(byte[] buf);
	
	/**
	 * @param sc
	 * @param bytebuffer
	 * @param size
	 * 
	 * Ŭ���̾�Ʈ�κ����� �޼����� �����ϴ� �Լ�
	 */
	public void sendMessage(byte[] buf);
	
	/**
	 * @param sc
	 * @param bytebuffer
	 * @param size
	 * 
	 * ������ �����ϴ� �Լ�
	 */
	public boolean verifyRecvData(byte[] rcvBytes);

	//public boolean isRcvEnvDataOK();
		
}
