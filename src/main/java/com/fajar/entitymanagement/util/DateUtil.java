package com.fajar.entitymanagement.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fajar.entitymanagement.dto.KeyPair;

public class DateUtil {

	static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat();

	static final Calendar cal() {
		return Calendar.getInstance();
	}

	/**
	 * 
	 * @param year
	 * @param month starts at 0
	 * @param day
	 * @return
	 */
	public static Date getDate(int year, int month, int day) {

		Calendar cal = cal();
		cal.set(year, month, day);

		return cal.getTime();

	}

	public static Calendar cal(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}
	
	/**
	 * 
	 * @param date
	 * @param type
	 * @return
	 */
	public static int getCalendarItem(Date date, int type) {
		Calendar cal = cal(date);
		return cal.get(type);
	}
	
	public static int getCalendarYear(Date date) {
		return getCalendarItem(date, Calendar.YEAR);
	}
	
	/**
	 * month starts at 0
	 * @param date
	 * @return
	 */
	public static int getCalendarMonth(Date date) {
		return getCalendarItem(date, Calendar.MONTH);
	}
	
	public static int getCalendarDayOfMonth(Date date) {
		return getCalendarItem(date, Calendar.DAY_OF_MONTH);
	}

	public static Integer[] getMonthsDay(int year) {
		boolean kabisat = year % 4 == 0;
		return new Integer[] { 31, (kabisat ? 29 : 28), 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
	}
	
	public static final String[] MONTH_NAMES = new String[] {
		"Januari",
		"Februari",
		"Maret",
		"April",
		"Mei",
		"Juni",
		"Juli",
		"Agustus",
		"September",
		"Oktober",
		"November",
		"Desember"
	};
	
	public static List<KeyPair> months(){
		return new ArrayList<KeyPair>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				for (int i = 1; i <=12; i++) {
					add(new KeyPair(MONTH_NAMES[i - 1] + "("+i+")", i, true));
				}
			}
		};
	}
	
	public static <K, V> Map<K, V> map(final K key, final V value) {
		return new HashMap<K, V>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -1426364237618468152L;

			{
				put(key, value);
			}
		};
	}

	public static String formatDate(Date date, String pattern) {
		SIMPLE_DATE_FORMAT.applyPattern(pattern);
		return SIMPLE_DATE_FORMAT.format(date);
	}

	public static String getTimeGreeting() {
		int hour = cal().get(Calendar.HOUR_OF_DAY);
		String time = "Morning";
		if (hour >= 3 && hour < 11) {
			time = "Morning";
		} else if (hour >= 11 && hour < 18) {
			time = "Afternoon";
		} else {
			time = "Evening";
		}

		return time;
	}

	public static String getFullFirstDate(int month, int year) {
		return year + "-" + StringUtil.addZeroBefore(month) + "-01";
	}

	public static String getFullLastDate(int month, int year) {
		String date = "";
		Integer day = getMonthsDay(year)[month - 1];
		boolean kabisat = year % 4 == 0;
		if (kabisat && month == 2) {
			day = 29;
		}
		date = year + "-" + StringUtil.addZeroBefore(month) + "-" + day;
		return date;
	}

	/**
	 * 
	 * @param month starts at 0
	 * @param year
	 * @return
	 */
	public static int getMonthsDay(int month, int year) {
		// TODO Auto-generated method stub
		return getMonthsDay(year)[month ];
	}
 
	/**
	 * 
	 * @param dayIndex starts at 1
	 * @param month starts at 0
	 * @param year
	 * @return
	 */
	public static Date[] getDaysInOneMonth(int dayIndex, int month, int year) {
		
		Date[] result = new Date[5]; 
		int monthDay = DateUtil.getMonthsDay(month, year);
		int arrayIndex = 0;
		
		for(int i = 1; i <= monthDay; i++) {
			
			Date currentDate = DateUtil.getDate(year, month, i);
			int dayOfWeek = DateUtil.getCalendarItem(currentDate, Calendar.DAY_OF_WEEK);
			
			if(dayOfWeek == dayIndex && arrayIndex <= 5) {
				result[arrayIndex] = currentDate;
				arrayIndex++;
			}
		}
		
		return result ;
	}

}
