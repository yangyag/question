package com.gsc.kixxhub.device.pumpa.common;

import java.util.Formatter;

import com.gsc.kixxhub.common.utility.log.LogUtility;

public class Log {

	/**
	 * @param dat
	 * @param size
	 * @param widthCnt
	 */
	public static void datas(byte[] dat, int size, int widthCnt) throws Exception {
		
		int	i=0, j, cnt=0;
		String str = new String();
		Formatter form = new Formatter();
		
		while (cnt<size) {

			for (j=0; cnt<size && j<widthCnt; j++) {

				if (j!=0 && j%10==0) {
					form.format("  ");
					str=form.toString();
				}

				if (dat[cnt] <= 0x20) { // Hex(Command) 
					form.format("x%02X ", dat[cnt]);
					str=form.toString();
				}
				else { // Ascii
					form.format("'%c' ", dat[cnt]);
					str=form.toString();
				}

				cnt++;
			}
			
			form.format("\n");
			str=form.toString();
			
			i = i + j;
		}

		LogUtility.getPumpALogger().info(str);
	}
}

