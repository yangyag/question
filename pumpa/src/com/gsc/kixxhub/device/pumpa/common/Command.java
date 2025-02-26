package com.gsc.kixxhub.device.pumpa.common;

public class Command { 
	
	/**
	 * 
	 */
	public static byte ACK = 0x06;
	/**
	 * 
	 */
	public static byte ENQ = 0x05;
	/**
	 * 
	 */
	public static byte EOT = 0x04;
	/**
	 * 
	 */
	public static byte ETB = 0x17; // ODT
	/**
	 * 
	 */
	public static byte ETX = 0x03;
	/**
	 * 
	 */
	public static byte NAK = 0x15; // 0x15 ??? at WooJoo
	/**
	 * 
	 */
	public static byte REQ = 0x05;
	/**
	 * 
	 */
	public static byte SOH = 0x01;
	/**
	 * 
	 */
	public static byte STX = 0x02;
	/**
	 * 
	 */
	public static byte STX_T = (byte) 0x82;	// Old Tatsuno
	
}
