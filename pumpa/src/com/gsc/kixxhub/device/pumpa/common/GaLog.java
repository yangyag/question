package com.gsc.kixxhub.device.pumpa.common;

import com.gsc.kixxhub.common.data.upos.MessageLogUtil;
import com.gsc.kixxhub.common.utility.ClearUtil;
import com.gsc.kixxhub.common.utility.log.LogUtility;

/**
 * 프로젝트명 : PI2
 * 작성일자 : 2016.03.28 
 * 변경내용 : ga전문 로그 
 * 작성자 : 정혜정
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
		
//		통합전문길이 조회
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
	
	//	mode : 	"s"이면 ga전문을 String으로 출력
//			"b"이면 ga전문을 byte 배열로 20자씩 잘라서 출력하며 mode는 대소문자
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
		
//		통합전문길이 조회
		uposLen = Integer.parseInt(new String(new byte[]{message[idx+1],message[idx+2],message[idx+3],message[idx+4]}));
		
		byte[] content = new byte[idx];
		byte[] uPosBody = new byte[uposLen];
		byte[] lastByte = new byte[lastSize];
		
		
		System.arraycopy(message, 0, content , 0, idx);
		System.arraycopy(message, idx, uPosBody , 0, uposLen);
		System.arraycopy(message, message.length-lastSize, lastByte , 0, lastSize);
		
		
		byte[] maskingUpos = MessageLogUtil.getProtectedBytes(uPosBody,0);
		
		// String 형태로 출력
		if("S".equals(mode.toUpperCase()))
		{
			StringBuffer ga = new StringBuffer();
			ga.append(new String(content));
			ga.append(new String(maskingUpos));
			ga.append(new String(lastByte));
			LogUtility.getPumpALogger().info(ga.toString());
		}
//		byteStream 형태로 출혁
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
			LogUtility.getPumpALogger().error("gaLog() 매개변수 \"mode = " + mode + "\" 로 잘못되었습니다.");
		}
		
		ClearUtil.setClearString(content);
		ClearUtil.setClearString(uPosBody);
		ClearUtil.setClearString(maskingUpos);
		ClearUtil.setClearString(lastByte);
		
	}
}