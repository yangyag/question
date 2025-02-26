package com.gsc.kixxhub.device.pumpa.datas;

import java.util.Vector;

public class ReceiptUtil {
	
	/**
	 * �־��� byte[]���� split�� �������� maxLength ���̸� �ִ�ġ�� ���� byte[]��� �и��Ѵ�.
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
		
		// ���� ��ġ
		int startPosition = 0;
		// maxLength�� ���� ���� ������ ����
		int readLength = 0;
		// �� ��ġ
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
					// split�� ������ ������ ��
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
			// ���� ������ ó��
			tempBytes = new byte[readLength];
			System.arraycopy(data, startPosition, tempBytes, 0, readLength);
			returnData.add(tempBytes);
			
		}	// end if
		
		
		return returnData;
		
	}	// end splitBs
	
}
