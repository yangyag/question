package com.gsc.kixxhub.device.pumpa.common;

import com.gsc.kixxhub.common.data.upos.MessageLogUtil;
import com.gsc.kixxhub.common.utility.ClearUtil;
import com.gsc.kixxhub.common.utility.log.LogUtility;

/**
 * ������Ʈ�� : PI2
 * �ۼ����� : 2016.03.28 
 * ���泻�� : ga���� �α� 
 * �ۼ��� : ������
 */

public class GaLog {
	
public static byte[] gaLog(byte[] message) throws Exception
	{
		int idx = 0;
		int fsCount = 0;
		int lastSize = 0;
		int uposLen = 0;
		
		if(message[0] == 0x01)
		{
			lastSize = 7;
		}
		else
		{
			lastSize = 2;
		}
		
		
		for(int i=0; i<message.length; i++)
		{
			if(message[i] == 0x1C)
			{
				fsCount++;
				if(fsCount == 10)
				{
					for(int j=i; j<message.length; j++)
					{
						if(message[j] == 0x02)
						{
							idx = j;
							break;
						}
					}
				}
			}
		}
		
//		������������ ��ȸ
		uposLen = Integer.parseInt(new String(new byte[]{message[idx+1],message[idx+2],message[idx+3],message[idx+4]}));
		byte[] content = new byte[idx];
		byte[] uPosBody = new byte[uposLen];
		byte[] lastByte = new byte[lastSize];
		
		System.arraycopy(message, 0, content , 0, idx);
		System.arraycopy(message, idx, uPosBody , 0, uposLen);
		System.arraycopy(message, message.length-lastSize, lastByte , 0, lastSize);
		
		byte[] maskingUpos = MessageLogUtil.getProtectedBytes(uPosBody,0);
		
		byte[] gaContent = new byte[content.length + maskingUpos.length + lastSize];
		System.arraycopy(content, 0, gaContent , 0, content.length);
		System.arraycopy(maskingUpos, 0, gaContent , idx, maskingUpos.length);
		System.arraycopy(lastByte, 0, gaContent, content.length + maskingUpos.length, lastByte.length);
		
		ClearUtil.setClearString(content);
		ClearUtil.setClearString(uPosBody);
		ClearUtil.setClearString(maskingUpos);
		ClearUtil.setClearString(lastByte);
		
		return gaContent;
		
	}
	
	//	mode : 	"s"�̸� ga������ String���� ���
//			"b"�̸� ga������ byte �迭�� 20�ھ� �߶� ����ϸ� mode�� ��ҹ���
	public static void gaLog(byte[] message, String mode) throws Exception
	{
		int idx = 0;
		int fsCount = 0;
		int lastSize = 0;
		int uposLen = 0;
		
		if(message[0] == 0x01)
		{
			lastSize = 7;
		}
		else
		{
			lastSize = 2;
		}
		
		
		for(int i=0; i<message.length; i++)
		{
			if(message[i] == 0x1C)
			{
				fsCount++;
				if(fsCount == 10)
				{
					for(int j=i; j<message.length; j++)
					{
						if(message[j] == 0x02)
						{
							idx = j;
							break;
						}
					}
				}
			}
		}
		
//		������������ ��ȸ
		uposLen = Integer.parseInt(new String(new byte[]{message[idx+1],message[idx+2],message[idx+3],message[idx+4]}));
		
		byte[] content = new byte[idx];
		byte[] uPosBody = new byte[uposLen];
		byte[] lastByte = new byte[lastSize];
		
		
		System.arraycopy(message, 0, content , 0, idx);
		System.arraycopy(message, idx, uPosBody , 0, uposLen);
		System.arraycopy(message, message.length-lastSize, lastByte , 0, lastSize);
		
		
		byte[] maskingUpos = MessageLogUtil.getProtectedBytes(uPosBody,0);
		
		// String ���·� ���
		if("S".equals(mode.toUpperCase()))
		{
			StringBuffer ga = new StringBuffer();
			ga.append(new String(content));
			ga.append(new String(maskingUpos));
			ga.append(new String(lastByte));
			LogUtility.getPumpALogger().info(ga.toString());
		}
//		byteStream ���·� ����
		else if("B".equals(mode.toUpperCase()))
		{
			byte[] gaContent = new byte[content.length + maskingUpos.length + lastSize];
			System.arraycopy(content, 0, gaContent , 0, content.length);
			System.arraycopy(maskingUpos, 0, gaContent , idx, maskingUpos.length);
			System.arraycopy(lastByte, 0, gaContent, content.length + maskingUpos.length, lastByte.length);
			Log.datas(gaContent, gaContent.length, 20);
			ClearUtil.setClearString(gaContent);
			
		}
		else
		{
			LogUtility.getPumpALogger().error("gaLog() �Ű����� \"mode = " + mode + "\" �� �߸��Ǿ����ϴ�.");
		}
		
		ClearUtil.setClearString(content);
		ClearUtil.setClearString(uPosBody);
		ClearUtil.setClearString(maskingUpos);
		ClearUtil.setClearString(lastByte);
		
	}
}