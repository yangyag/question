package com.gsc.kixxhub.device.pumpa.controller;

public class GSocketTimer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	private long deciSeconds = 0; //-- 1/10 second
	
	public GSocketTimer() {

		Thread m_thread = new Thread(new Runnable() {
			public void run() {	

				while (true) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					deciSeconds++;
				}
			}
		});
		
		m_thread.setName("GSockTimer");
		m_thread.start();	

	}
	
	public synchronized long getDeciSeconds() {
		
		return deciSeconds;
	}

	public synchronized void resetTime() {
		deciSeconds = 0;
	}

}
