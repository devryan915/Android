package com.langlang.utils;

import android.annotation.SuppressLint;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Create by Xuhui on 2006-10-20 All rights reserved by Vxichina ESBU
 */
public class DateUtil {

	/*
	 * 代码code 目前考虑D,W,M三种方式 如：000D， 000W00D， 000M00D
	 * 
	 * 算法：判断code是否为合法，如果合法，先取第四位，再取相应值进行计算
	 */
	public static Date computeDate(Date date, String code) {
		if (code.length() == 4) {
			String flag = code.substring(3, 4);
			if ("D".equalsIgnoreCase(flag)) {
				date = getDateByType(date, Calendar.DATE,
						Integer.parseInt(code.substring(0, 3)));
			} else if ("W".equalsIgnoreCase(flag)) {
				date = getDateByType(date, Calendar.WEEK_OF_YEAR,
						Integer.parseInt(code.substring(0, 3)));
			} else if ("M".equalsIgnoreCase(flag)) {
				date = getDateByType(date, Calendar.MONTH,
						Integer.parseInt(code.substring(0, 3)));
			}

		} else {
			String flag = code.substring(3, 4);
			if ("W".equalsIgnoreCase(flag)) {
				date = setDateByWeek(date,
						Integer.parseInt(code.substring(0, 3)),
						code.substring(4, 6));
			} else {
				date = setDateByMonth(date,
						Integer.parseInt(code.substring(0, 3)),
						code.substring(4, 6));
			}
		}
		return date;

	}

	/*
	 * 代码code 目前考虑两种方式 分别为：XXXXX & XX:XX
	 * 
	 * 算法：判断code是否为合法，如果合法，先取第四位，再取相应值进行计算
	 */
	public static Date computeTime(Date date, String code) {
		if (":".equals(code.substring(2, 3))) {
			date = setDateByTime(date, Calendar.HOUR_OF_DAY,
					Integer.parseInt(code.substring(0, 2)));
			date = setDateByTime(date, Calendar.MINUTE,
					Integer.parseInt(code.substring(3, 5)));
		} else {
			date = getDateByType(date, Calendar.MINUTE, Integer.parseInt(code));
		}
		return date;
	}

	/**
	 * 通过星期获取日期
	 * 
	 * @param date
	 *            日期
	 * @param numOfWeek
	 *            一年中第几周
	 * 
	 * @param dayOfWeek
	 *            星期几
	 * 
	 * @return
	 */
	public static Date setDateByWeek(Date date, int numOfWeek, String dayOfWeek) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.WEEK_OF_YEAR, numOfWeek);
		cal.set(Calendar.DAY_OF_WEEK, Integer.parseInt(dayOfWeek) + 1);
		return cal.getTime();
	}

	/**
	 * 通过月份获取日期
	 * 
	 * @param date
	 *            日期
	 * @param numOfWeek
	 * @param dayOfWeek
	 * @return
	 */
	public static Date setDateByMonth(Date date, int numOfMonth,
			String dayOfMonth) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, numOfMonth);
		cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dayOfMonth));
		return cal.getTime();
	}

	/**
	 * @param date
	 *            -- 当前日期
	 * @param type
	 *            -- 类型
	 * @param num
	 *            -- 该类型添加数量
	 * 
	 * @return --添加后的日期
	 */
	public static Date getDateByType(Date date, int type, int num) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(type, num);
		return cal.getTime();
	}

	/**
	 * @param date
	 *            -- 当前日期
	 * @param type
	 *            -- 类型
	 * @param num
	 *            -- 该类型添加数量
	 * 
	 * @return --添加后的日期的前一天
	 */
	public static Date getDateByTypeBefore(Date date, int type, int num) {
		Calendar cal = Calendar.getInstance();
		Calendar calStart = Calendar.getInstance();
		cal.setTime(date);
		calStart.setTime(date);
		cal.add(type, num);
		if (calStart.get(Calendar.DAY_OF_MONTH) <= cal
				.get(Calendar.DAY_OF_MONTH)) {
			cal.add(Calendar.DATE, -1);
		}
		return cal.getTime();
	}

	/**
	 * @param date
	 *            -- 当前日期
	 * @param type
	 *            -- 类型
	 * @param num
	 *            -- 该类型设置数量
	 * 
	 * @return -- 设置后的日期
	 */
	public static Date setDateByTime(Date date, int type, int num) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(type, num);

		return cal.getTime();
	}

	/**
	 * 判断指定日期是否位于两个日期之间
	 * 
	 * @param curDay
	 * @param from
	 * @param to
	 * @return
	 */
	public static boolean isBetween(Date curDay, Date from, Date to) {
		if (curDay == null || from == null || to == null) {
			return false;
		}
		if (curDay.compareTo(from) >= 0 && curDay.compareTo(to) <= 0) {
			return true;
		}
		return false;
	}

	public static boolean isBetweenNotEqule(Date curDay, Date from, Date to) {
		if (curDay == null || from == null || to == null) {
			return false;
		}
		if (from.compareTo(to) == 0) {
			return false;
		}
		if (curDay.compareTo(from) >= 0 && curDay.compareTo(to) <= 0) {
			return true;
		}
		return false;
	}

	public static int compare(Date curDay, Date oldDate) {
		if (curDay == null || oldDate == null) {
			return -1;
		}

		return curDay.compareTo(oldDate);
	}

	/**
	 * 将日期转化成特定类型的字符串
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String parseDateToInput(Date date, String format) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	/**
	 * 
	 * @param date
	 * @param unit
	 * @param field
	 * @return
	 */
	public static Date getDate(Date date, int unit, int field) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		if (unit == 1) {
			c.add(Calendar.YEAR, field);
		}
		if (unit == 2) {
			c.add(Calendar.MONTH, field);
		}

		if (unit == 3) {
			c.add(Calendar.WEEK_OF_MONTH, field);
		}

		if (unit == 4) {
			c.add(Calendar.DATE, field);
		}

		if (field > 0) {
			c.add(Calendar.DATE, -1);
		}
		return c.getTime();
	}

	/**
	 * 将指定日期类型转化为当天初始时间 如 2010-04-03 00:00:00.0
	 * 
	 * @param dateStr
	 * @return
	 */
	public static Date getStartTimeOfDate(String dateStr) {
		return Timestamp.valueOf(dateStr + " 00:00:00.0");
	}

	/**
	 * 将指定日期类型转化为当天末端时间 如 2010-04-03 23:59:59.0
	 * 
	 * @param dateStr
	 * @return
	 */
	public static Date getEndTimeOfDate(String dateStr) {
		return Timestamp.valueOf(dateStr + " 23:59:59.0");
	}

	public static Date getDelayDate(Date date, int field, int input) {
		// System.out.println(date.toString());
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		// c.set(field, c.get(field)+input);
		c.set(2, c.get(2) + 3);
		return c.getTime();
	}

	/**
	 * 取得系统当前时间前n个月的相对应的一天
	 * 
	 * 
	 * @param n
	 *            int
	 * @return String yyyy-mm-dd
	 */
	public static String getNMonthBeforeCurrentDay(int n) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -n);
		String year = String.valueOf(c.get(Calendar.YEAR));
		String month = String.valueOf(c.get(Calendar.MONTH) + 1);
		String date = String.valueOf(c.get(Calendar.DATE));
		if (month.length() < 2) {
			month = "0" + month;
		}
		if (date.length() < 2) {
			date = "0" + date;
		}
		return year + "-" + month + "-" + date;
	}

	/**
	 * 获取某年第一天日期
	 * 
	 * @param year
	 *            年份
	 * @return Date
	 */
	public static Date getCurrYearFirst(int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.YEAR, year);
		Date currYearFirst = calendar.getTime();
		return currYearFirst;
	}

	/**
	 * 获取某年最后一天日期
	 * 
	 * @param year
	 *            年份
	 * @return Date
	 */
	public static Date getCurrYearLast(int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.YEAR, year);
		calendar.roll(Calendar.DAY_OF_YEAR, -1);
		Date currYearLast = calendar.getTime();

		return currYearLast;
	}

	/**
	 * 获取上一分钟数
	 * 
	 * @return
	 */
	public static String getLastMinute() {
		SimpleDateFormat sdf = new SimpleDateFormat(Program.MINUTE_FORMAT);
		Calendar can = Calendar.getInstance();
		can.add(Calendar.MINUTE, -1);
		return sdf.format(can.getTime());
	}
	/**
	 * 获取当前分钟数
	 * 
	 * @return
	 */
	public static String getCurrentMinute() {
		SimpleDateFormat sdf = new SimpleDateFormat(Program.MINUTE_FORMAT);
		Calendar can = Calendar.getInstance();
		return sdf.format(can.getTime());
	}
	
	/**
	 *  判断date是否在now的days以前
	 **/
	public static boolean isDateBeforDays(Date now, Date date, int days) {
		if (days < 0) return false;
		if (now == null || date == null) {
			return false;
		}
		
		if ((now.getTime() - date.getTime()) > (days * 24 * 60 * 60 * 1000)) {
			return true;
		}
		
		return false;
	}
	
	public static boolean isInTheSameDay(Date now, Date date) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(now);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date);
        
        return (cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH))
        	&& (cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH))
        	&& (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR));
	}
}
