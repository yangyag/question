package com.gsc.kixxhub.device.pumpa.common;

public class Sleep {

	/**
	 * @param time
	 */
	public static void sleep (int time) {
		try {
			Thread.sleep(time);
		} catch (Exception e) {}
	}
}