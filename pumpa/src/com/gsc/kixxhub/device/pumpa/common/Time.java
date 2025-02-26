package com.gsc.kixxhub.device.pumpa.common;

import java.util.Calendar;
import java.util.Formatter;
import java.util.GregorianCalendar;

import com.gsc.kixxhub.common.utility.log.LogUtility;

public class Time {

	/**
	 * @return
	 */
	public static String currentTime () {
		
		Calendar cal = new GregorianCalendar();
		Formatter form = new Formatter();
		
		try {
			String year = Change.toString("%04d", cal.get(Calendar.YEAR));
			form.format("%s%02d%02d%02d%02d%02d", year.substring(2, 4),
					cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH), 
					cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), 
					cal.get(Calendar.SECOND));
		} catch (Exception e) {
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
		
		return form.toString();
	}
}
