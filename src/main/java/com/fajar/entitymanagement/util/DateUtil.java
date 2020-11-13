package com.fajar.entitymanagement.util;

import static com.fajar.entitymanagement.util.CollectionUtil.reverse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fajar.entitymanagement.dto.KeyValue;

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
	
	public static int getCurrentYear() {
		return getCalendarYear(new Date());
	}
	
	/**
	 * month starts at 0
	 * @return
	 */
	public static int getCurrentMonth() {
		return getCalendarMonth(new Date());
	}
	
	public static int getCalendarDayOfMonth(Date date) {
		return getCalendarItem(date, Calendar.DAY_OF_MONTH);
	}
	
	static Integer[] kabisatMonths = new Integer[] { 31, (  29  ), 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
	static Integer[] regularMonths = new Integer[] { 31, (  28  ), 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

	public static Integer[] getMonthsDay(int year) {
		boolean kabisat = year % 4 == 0;
		return  kabisat ? kabisatMonths : regularMonths;
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
	
	public static List<KeyValue<String, Integer>> months(){
		
		return new ArrayList<KeyValue<String, Integer>>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				for (int i = 1; i <=12; i++) {
					add(new KeyValue<String, Integer>(MONTH_NAMES[i - 1] + "("+i+")", i, true));
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

	public static List<KeyValue<Integer, Integer>> yearArray(int minYear, int i) {
		
		List<KeyValue<Integer, Integer>> years = new ArrayList<>();
		for (int j = minYear; j <= i; j++) {
			years.add(new KeyValue<Integer, Integer>(j, j, true));
		}
		return years ;
	}

	/**
	 * 
	 * @param m0 from month
	 * @param y0 from year
	 * @param m1 to month
	 * @param y1 to year
	 * @return
	 */
	public static int getDiffMonth(int m0, int y0, int m1, int y1) {
		int diff = 0;
		for (int i = y0; i <= y1; i++) {

			int beginMonth = 1; 
			if (i == y0) { 
				beginMonth = m0;
			}

			for (int j = beginMonth; j <= 12; j++) { 
				if (i == y1 && j == m1) { 
					return diff;
				}
				diff++;
			}

		}
		return diff;
	}
	
	/**
	 * get difference day from now
	 * @param date
	 * @return
	 */
	public static long getDiffDaysFromNow(Date date) {
		long diff = new Date().getTime() - date.getTime();
		long diffDays = diff / (24 * 60 * 60 * 1000);
		return diffDays;
	}
	
	/**
	 * get list of months with length: diff
	 * @param calendar
	 * @param diff
	 * @return
	 */
	public static List<int[]> getMonths(Calendar calendar, int diff) {

		Integer currentMonth = calendar.get(Calendar.MONTH) + 1;
		Integer currentYear = calendar.get(Calendar.YEAR);
		List<int[]> periods = new ArrayList<>();
		String monthString = currentMonth >= 10 ? currentMonth.toString() : "0" + currentMonth;

		periods.add(new int[] { currentYear, Integer.parseInt(monthString) });

		for (int i = 1; i <= diff  ; i++) {
			currentMonth--;
			if (currentMonth <= 0) {
				currentMonth = 12;
				currentYear--;
			}
			monthString = currentMonth >= 10 ? currentMonth.toString() : "0" + currentMonth;
			periods.add(new int[] { currentYear, Integer.parseInt(monthString) });
		}
		return reverse(periods);
	}
	
	/**
	 * get day of month count
	 * @param year
	 * @param month
	 * @return
	 */
	public static int getMonthDayCount(int year, int month) {
		
		int day = 30;
		
		if(month == 2 && year % 4 == 0) {
			return 29;
		}else if(month == 2) {
			return 28;
		}
		
		if(month < 8 && month % 2 != 0) {
			return 30;
		}else if(month >= 8 && month % 2 == 0) {
			return 31;
		}
		
		return day;
	}
}
