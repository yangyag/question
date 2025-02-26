package com.gsc.kixxhub.module.pumpm.pump.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Encoder;


import com.gsc.kixxhub.common.utility.property.PropertyManager;

/**
	PI2 20160324 twlee
	��й�ȣ ��ȣȭ ��� TripleDES �߰�
*/

public class TripleDES {
	/*
	��ȣȭ ���� : CBC
	������ �е���� : PKCS5Pading  
	 PKCS5 and PKCS7 use the same padding
	�ʱ�ȭ Vector   : [0x01, 0x23, 0x45, 0x67, 0x89, 0xab, 0xcd, 0xef]
	��ȣȭ Key����  : �� ������ 16Byte Key���� �� 8Byte�� �ڿ� �ٿ� 24Byte Key ����
	===================================>>
	- �� ���޵� 16Byte Key
	FDK : FDKRGS&PT2015DEV
	KICC : VAN1GS&PT2015DEV
	SMARTRO : VAN2GS&PT2015DEV
	
	- ��ȣȭKey(24Byte)
	FDK : FDKRGS&PT2015DEVFDKRGS&P
	KICC : VAN1GS&PT2015DEVVAN1GS&P
	SMARTRO : VAN2GS&PT2015DEVVAN2GS&P
	<<===================================
	*/
	private final static byte[] initializationVector =  {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef};
	
	 /**
	 * ����Ű ����
	 * @return
	 */
	public static String key()
	{
		String key = PropertyManager.getSingleton().getProperty(PropertyManager.TRIPLEDES_KEY, PropertyManager.TRIPLEDES_KEY_DEFAULT);
	    return key;
	}
	
	
	
	public static String encryptText(String plainText) throws Exception {
		//----  Use specified 3DES key and IV from other source --------------
		byte[] plaintext = plainText.getBytes();
		byte[] tdesKeyData = key().getBytes();
		byte[] iv = initializationVector;
	
		Cipher c3des = Cipher.getInstance("DESede/CBC/PKCS5Padding");
		SecretKeySpec myKey = new SecretKeySpec(tdesKeyData, "DESede");
		IvParameterSpec ivspec = new IvParameterSpec(iv);
		c3des.init(Cipher.ENCRYPT_MODE, myKey, ivspec);
		byte[] cipherText = c3des.doFinal(plaintext);
		BASE64Encoder obj64 = new BASE64Encoder();
		
		return obj64.encode(cipherText);
	}
	
	public static String decryptText(String encryptText) throws Exception {
	
		byte[] encData = new sun.misc.BASE64Decoder().decodeBuffer(encryptText);
		Cipher decipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
		byte[] tdesKeyData =  key().getBytes();
		SecretKeySpec myKey = new SecretKeySpec(tdesKeyData, "DESede");
		IvParameterSpec ivspec = new IvParameterSpec(initializationVector);
		decipher.init(Cipher.DECRYPT_MODE, myKey, ivspec);
		byte[] plainText = decipher.doFinal(encData);
		
		return new String(plainText);
	
	}
}
