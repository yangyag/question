package com.gsc.kixxhub.device.pumpa.common;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Formatter;

import com.gsc.kixxhub.common.utility.log.LogUtility;

public class Change { 
	
	/**
	 * Integer를 16진수 문자열로 변환
	 */
	public static String binToHex (int bin) {
		String hex="";
		
		try {
			Formatter form = new Formatter();
			form.format("%X", bin);
			hex = form.toString();
		} catch (Exception e) {
			/*LogUtility.getPumpALogger().error(e.getMessage(), e);*/
			e.getStackTrace();
		}
		
		return hex;
	}
	
	// 다쓰노 구형에서 unsigned byte를 지원하는 메소드
	/**
	 * @param data
	 * @return
	 */
	public static byte getUnsignedByte (byte data) {
		byte returnData = 0;

		try {
			if (data > 0x80) {
				int tempData = data;
				
				tempData = (0xFF - tempData + 1) * -1;
				
				returnData = (byte) tempData;
				
			} else {
				returnData = data;
			}
		} catch (Exception e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
		
		return returnData;
	}

	/**
	 * 16진수 문자열을 Integer로 변환
	 */
	public static int hexToBin (String hexStr) {
		int val=0;
		
		try {
			Formatter form = new Formatter();
			form.format("%d", Integer.parseInt(hexStr, 16));
			val = toValue(form.toString());
		} catch (Exception e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
		
		return val;
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		LogUtility.getPumpALogger().debug("val======" + hexToBin("ff"));

		LogUtility.getPumpALogger().debug("hex======" + binToHex(15));
	}
	
	/**
	 * @param str
	 * @return
	 */
	public static long toLongValue (String str) {
		long val=0;
		try {
			Long longInt = new Long(str);
			val = longInt.longValue();
		} catch (Exception e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
		
		return val;
	}
		
	/**
	 * @param formStr = "#,##0.00"
	 * @param val
	 * @return
	 */
	public static String toMoneyString (String formStr, double val) {

		DecimalFormat df=null;
		String moneyString=null;
		
		try {
	        moneyString = new Double(val).toString(); 
	
	        df = new DecimalFormat(formStr); 
	        DecimalFormatSymbols dfs = new DecimalFormatSymbols(); 
	
	        dfs.setGroupingSeparator(','); // 구분자를 ,로 
	        df.setGroupingSize(3); //3자리 단위마다 구분자처리 한다. 
	        df.setDecimalFormatSymbols(dfs); 
		} catch (Exception e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}

        return (df.format(Double.parseDouble(moneyString))).toString(); 
	}	
	
	/**
	 * @param val
	 * @return
	 */
	public static String toString (int val) {
		String str="";
		try {
			Integer integer = new Integer(val);
			str = integer.toString();
		} catch (Exception e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
		
		return str;
	}
	
	
	/**
	 * @param formStr
	 * @param val
	 * @return
	 */
	public static String toString (String formStr, int val) {
		String str="";
		try {
			Formatter form = new Formatter();
			form.format(formStr, val);
			str = form.toString();
		} catch (Exception e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
		
		return str;
	}
	
	/**
	 * @param str
	 * @return
	 */
	public static int toValue (String str) {
		int val=0;
		try {
			Integer integer = new Integer(str);
			val = integer.intValue();
		} catch (Exception e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
		
		return val;
	}
}
