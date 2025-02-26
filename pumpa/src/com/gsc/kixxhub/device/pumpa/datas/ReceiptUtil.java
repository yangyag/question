package com.gsc.kixxhub.device.pumpa.datas;

import java.util.Vector;

public class ReceiptUtil {
	
	/**
	 * 주어진 byte[]에서 split을 기준으로 maxLength 길이를 최대치로 갖는 byte[]들로 분리한다.
	 * ==== Example ====
	 * data : "abcdef"
	 * split : 'c'
	 * maxLength : 3
	 * result : {"abc", "def"}
	 * 
	 * 
	 * @param data
	 * @param maxLength
	 * @return
	 */
	public static Vector<byte[]> splitBs(byte[] data, int maxLength) {
		Vector<byte[]> returnData = new Vector<byte[]>();
		
		byte split = 0x0A;
		
		// 시작 위치
		int startPosition = 0;
		// maxLength와 비교할 읽은 데이터 길이
		int readLength = 0;
		// 끝 위치
		int endPosition = 0;
		byte[] tempBytes = null;
		
		for (int i = 0; i < data.length; i++) {
			readLength++;
			
			if (data[i] == split) {
				endPosition = i;
				
			}	// end if
			
			if (readLength == maxLength) {
				int byteLength = 0;
				
				if (startPosition == endPosition) {
					// split을 만나지 못했을 때
					byteLength = readLength;
					endPosition = i;
					
				} else {
					byteLength = endPosition - startPosition + 1;
					
				}	// end inner if
				
				tempBytes = new byte[byteLength];
				
				System.arraycopy(data, startPosition, tempBytes, 0, byteLength);
				returnData.add(tempBytes);
				
				readLength = readLength - byteLength;
				startPosition = endPosition + 1;
				endPosition = startPosition;
				
			}	// end if
			
		}	// end for
		
		if (readLength > 0) {
			// 남은 데이터 처리
			tempBytes = new byte[readLength];
			System.arraycopy(data, startPosition, tempBytes, 0, readLength);
			returnData.add(tempBytes);
			
		}	// end if
		
		
		return returnData;
		
	}	// end splitBs
	
}
