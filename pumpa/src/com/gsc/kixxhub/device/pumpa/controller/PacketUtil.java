package com.gsc.kixxhub.device.pumpa.controller;

import com.gsc.kixxhub.common.utility.ClearUtil;
import com.gsc.kixxhub.device.pumpa.common.Change;

public class PacketUtil {


	// 패킷 Body 데이터 추출
	public static byte[] getBodyBytes(byte[] rcvPacket) {
		
		try {	
			byte[] bySize = new byte[8];
			System.arraycopy(rcvPacket, 11, bySize, 0, 8);
			
			int size = Change.toValue(new String(bySize));
			//LogUtility.getPumpALogger().debug("######### Packet size = " + size);
			
			byte[] byBody = new byte[size - 2]; // STX, ETX 는 제외
			System.arraycopy(rcvPacket, 20, byBody, 0, size - 2);

			ClearUtil.setClearString(rcvPacket);			
			return byBody;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	// Body ODTNo 조회
	public static String getBodyODTNo(byte[] bytes) {
		
		try {
			byte[] by = new byte[2];
			by[0] = bytes[22];
			by[1] = bytes[23];
			
			return new String (by);
		} 
		catch (Exception e) {
			return "";
		}
	}
		
	public static String getDataCommand(byte[] rcvPacket) {
		
		try {	
			byte[] byBuf = {rcvPacket[20], rcvPacket[21]};
			
			return new String(byBuf);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	
	// 패킷 코드 조회
	public static String getPacketCode(byte[] bytes) {

		try {
			byte[] by = new byte[4];
			by[0] = bytes[22];
			by[1] = bytes[23];
			by[2] = bytes[24];
			by[3] = bytes[25];
			
			return new String (by);
		} 
		catch (Exception e) {
			return null;
		}
	}
	
	// 패킷 CRC 조회
	public static String getPacketCRC(byte[] bytes) {

		try {
			byte[] byCRC = new byte[4];
			System.arraycopy(bytes, 19 + getPacketLength(bytes), byCRC, 0, 4);
			
			return new String (byCRC);
		} 
		catch (Exception e) {
			return "";
		}
	}
	
	//	 패킷타겟 조회
	public static String getPacketDst(byte[] bytes) {

		try {
			byte[] by = new byte[4];
			by[0] = bytes[5];
			by[1] = bytes[6];
			by[2] = bytes[7];
			by[3] = bytes[8];
			
			return new String (by);
		} 
		catch (Exception e) {
			return null;
		}
	}
	
	
	//	 패킷그룹 조회
	public static String getPacketGroup(byte[] bytes) {

		try {
			byte[] by = new byte[2];
			by[0] = bytes[9];
			by[1] = bytes[10];
			
			return new String (by);
		} 
		catch (Exception e) {
			return null;
		}
	}
	
	
	
	// 패킷 길이 조회
	public static int getPacketLength(byte[] bytes) {

		try {
			byte[] by = new byte[8];
			by[0] = bytes[11];
			by[1] = bytes[12];
			by[2] = bytes[13];
			by[3] = bytes[14];
			by[4] = bytes[15];
			by[5] = bytes[16];
			by[6] = bytes[17];
			by[7] = bytes[18];
			
			return Integer.parseInt(new String (by));
		} 
		catch (Exception e) {
			return -1;
		}
	}
	
	
	// 패킷 소스 ODT번호 조회
	public static String getPacketODTNo(byte[] bytes) {

		try {
			byte[] by = new byte[2];
			by[0] = bytes[20];
			by[1] = bytes[21];
			
			return new String (by);
		} 
		catch (Exception e) {
			return "";
		}
	}
	
	
	//	 패킷소스 조회
	public static String getPacketSrc(byte[] bytes) {

		try {
			byte[] by = new byte[4];
			by[0] = bytes[1];
			by[1] = bytes[2];
			by[2] = bytes[3];
			by[3] = bytes[4];
			
			return new String (by);
		} 
		catch (Exception e) {
			return null;
		}
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
		
	// 데이터 크기만큼 packing
	public static byte[] pack (byte[] bytes) {
		
		int len = getPacketLength(bytes);
		byte[] pBytes = new byte[len + 23];
		System.arraycopy(bytes, 0, pBytes, 0, len + 23);
		
		return pBytes;
	}
	
	// 패킷 소스 ODT번호 설정
	public static void setPacketODTNo(byte[] bytes, String ODTNo) {

		try {
			byte[] by = ODTNo.getBytes();
			bytes[20] = by[0];
			bytes[21] = by[1];
		} 
		catch (Exception e) {
		}
	}

}
