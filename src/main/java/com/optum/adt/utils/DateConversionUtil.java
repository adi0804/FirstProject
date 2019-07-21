package com.optum.adt.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class DateConversionUtil {

	public String getDate(String dateString) {

		DateFormat format = new SimpleDateFormat("yyyyMMdd");
		Date date = null;
		try {
			date = format.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar cal = Calendar.getInstance();

		cal.setTime(date);
		return cal.get(Calendar.YEAR) + "" + (cal.get(Calendar.MONTH) + 1) + cal.get(Calendar.DATE);

	}

	public String gettime(String time) {

		DateFormat format = new SimpleDateFormat("yyyyMMdd HHmm");
		Date date = null;
		try {
			date = format.parse(time);
		} catch (ParseException e) {

			e.printStackTrace();
		}
		Calendar cal = Calendar.getInstance();

		cal.setTime(date);
		return cal.get(Calendar.HOUR_OF_DAY) + "" + cal.get(Calendar.MINUTE);

	}

}
