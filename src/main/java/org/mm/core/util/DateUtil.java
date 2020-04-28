package org.mm.core.util;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 日期工具类
 * 
 */
public class DateUtil {

	// 默认日期格式
	public static final String DATE_DEFAULT_FORMAT = "yyyy-MM-dd";

	// 默认时间格式
	public static final String DATETIME_DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public static final String TIME_DEFAULT_FORMAT = "HH:mm:ss";
	
	public static final String DATE_NO_SYMBOL_FORMAT = "yyyyMMdd";

	//UTC
	public static final String DATE_UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

	// 日期格式化
	private static DateFormat dateFormat = null;

	// 时间格式化
	private static DateFormat dateTimeFormat = null;

	private static DateFormat timeFormat = null;

	private static Calendar gregorianCalendar = null;
	
	private static DateFormat dateNoSymbolFormat = null;

	static {
		dateFormat = new SimpleDateFormat(DATE_DEFAULT_FORMAT);
		dateTimeFormat = new SimpleDateFormat(DATETIME_DEFAULT_FORMAT);
		timeFormat = new SimpleDateFormat(TIME_DEFAULT_FORMAT);
		gregorianCalendar = new GregorianCalendar();
		dateNoSymbolFormat = new SimpleDateFormat(DATE_NO_SYMBOL_FORMAT);
	}

	/**
	 * 日期格式化yyyy-MM-dd
	 * 
	 * @param date
	 * @return
	 */
	public static Date formatDate(String date, String format) {
		try {
			return new SimpleDateFormat(format).parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 日期格式化yyyy-MM-dd
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateFormat(Date date) {
		return dateFormat.format(date);
	}

	/**
	 * 日期格式化yyyy-MM-dd HH:mm:ss
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateTimeFormat(Date date) {
		return dateTimeFormat.format(date);
	}

	/**
	 * 时间格式化
	 * 
	 * @param date
	 * @return HH:mm:ss
	 */
	public static String getTimeFormat(Date date) {
		return timeFormat.format(date);
	}
	
	/**
	 * 日期格式化 yyyyMMdd
	 * @param date
	 * @return
	 */
	public static String getNoSymbolDateFormat(Date date) {
		return dateNoSymbolFormat.format(date);
	}

	/**
	 * 日期格式化
	 * 
	 * @param date
	 * @param formatStr //格式类型
	 * @return
	 */
	public static String getDateFormat(Date date, String formatStr) {
		if (StringUtils.isNotBlank(formatStr)) {
			return new SimpleDateFormat(formatStr).format(date);
		} else {
			return getDateFormat(date);
		}
	}

	/**
	 * 日期格式化
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDateFormat(String date) {
		try {
			return dateFormat.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 时间格式化
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDateTimeFormat(String date) {
		try {
			return dateTimeFormat.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取当前日期(yyyy-MM-dd)
	 * @return
	 */
	public static Date getNowDate() {
		return DateUtil.getDateFormat(dateFormat.format(new Date()));
	}
	
	/**
	 * 获取一年得第一天
	 * @return
	 */
	public static Date getFirstDayOfYear() {
		gregorianCalendar.setTime(new Date());
		gregorianCalendar.set(Calendar.DAY_OF_YEAR, 1);
		return gregorianCalendar.getTime();
	}
	
	/**
	 * 获取指定日期一年得第一天
	 * @param date
	 * @return
	 */
	public static Date getFirstDayOfYear(Date date) {
		gregorianCalendar.setTime(date);
		gregorianCalendar.set(Calendar.DAY_OF_YEAR, 1);
		return gregorianCalendar.getTime();
	}
	
	/**
	 * 获取一年得第一天
	 * @return
	 */
	public static Date getLastDayOfYear() {
		gregorianCalendar.setTime(new Date());
		gregorianCalendar.set(Calendar.DAY_OF_YEAR, 1);
		gregorianCalendar.add(Calendar.YEAR, 1);
		gregorianCalendar.add(Calendar.DAY_OF_YEAR, -1);
		return gregorianCalendar.getTime();
	}
	
	/**
	 * 获取指定日期一年得第一天
	 * @param date
	 * @return
	 */
	public static Date getLastDayOfYear(Date date) {
		gregorianCalendar.setTime(date);
		gregorianCalendar.set(Calendar.DAY_OF_YEAR, 1);
		gregorianCalendar.add(Calendar.YEAR, 1);
		gregorianCalendar.add(Calendar.DAY_OF_YEAR, -1);
		return gregorianCalendar.getTime();
	}

	/**
	 * 获取当前日期星期一日期
	 * 
	 * @return date
	 */
	public static Date getFirstDayOfWeek() {
		gregorianCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		gregorianCalendar.setTime(new Date());
		gregorianCalendar.set(Calendar.DAY_OF_WEEK, gregorianCalendar.getFirstDayOfWeek()); // Monday
		return gregorianCalendar.getTime();
	}

	/**
	 * 获取当前日期星期日日期
	 * 
	 * @return date
	 */
	public static Date getLastDayOfWeek() {
		gregorianCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		gregorianCalendar.setTime(new Date());
		gregorianCalendar.set(Calendar.DAY_OF_WEEK, gregorianCalendar.getFirstDayOfWeek() + 6); // Monday
		return gregorianCalendar.getTime();
	}

	/**
	 * 获取日期星期一日期
	 * 
	 * @date 指定日期
	 * @return date
	 */
	public static Date getFirstDayOfWeek(Date date) {
		if (date == null) {
			return null;
		}
		gregorianCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		gregorianCalendar.setTime(date);
		gregorianCalendar.set(Calendar.DAY_OF_WEEK, gregorianCalendar.getFirstDayOfWeek()); // Monday
		return gregorianCalendar.getTime();
	}

	/**
	 * 获取日期星期一日期
	 * 
	 * @date 指定日期
	 * @return date
	 */
	public static Date getLastDayOfWeek(Date date) {
		if (date == null) {
			return null;
		}
		gregorianCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		gregorianCalendar.setTime(date);
		gregorianCalendar.set(Calendar.DAY_OF_WEEK, gregorianCalendar.getFirstDayOfWeek() + 6); // Monday
		return gregorianCalendar.getTime();
	}

	/**
	 * 获取当前月的第一天
	 * 
	 * @return date
	 */
	public static Date getFirstDayOfMonth() {
		gregorianCalendar.setTime(new Date());
		gregorianCalendar.set(Calendar.DAY_OF_MONTH, 1);
		return gregorianCalendar.getTime();
	}

	/**
	 * 获取当前月的最后一天
	 * 
	 * @return
	 */
	public static Date getLastDayOfMonth() {
		gregorianCalendar.setTime(new Date());
		gregorianCalendar.set(Calendar.DAY_OF_MONTH, 1);
		gregorianCalendar.add(Calendar.MONTH, 1);
		gregorianCalendar.add(Calendar.DAY_OF_MONTH, -1);
		return gregorianCalendar.getTime();
	}

	/**
	 * 获取指定月的第一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getFirstDayOfMonth(Date date) {
		gregorianCalendar.setTime(date);
		gregorianCalendar.set(Calendar.DAY_OF_MONTH, 1);
		return gregorianCalendar.getTime();
	}

	/**
	 * 获取指定月的最后一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getLastDayOfMonth(Date date) {
		gregorianCalendar.setTime(date);
		gregorianCalendar.set(Calendar.DAY_OF_MONTH, 1);
		gregorianCalendar.add(Calendar.MONTH, 1);
		gregorianCalendar.add(Calendar.DAY_OF_MONTH, -1);
		return gregorianCalendar.getTime();
	}
	
	/**
	 * 一天的开始
	 * @param date
	 * @return
	 */
	public static Date getFirstTimeOfDay(Date date) {
		gregorianCalendar.setTime(date);
		gregorianCalendar.set(Calendar.SECOND, 0);
		gregorianCalendar.set(Calendar.MINUTE, 0);
		gregorianCalendar.set(Calendar.HOUR_OF_DAY, 0);
		return gregorianCalendar.getTime();
	}
	
	/**
	 * 一天的结束
	 * @param date
	 * @return
	 */
	public static Date getLastTimeOfDay(Date date) {
		gregorianCalendar.setTime(date);
		gregorianCalendar.set(Calendar.SECOND, 59);
		gregorianCalendar.set(Calendar.MINUTE, 59);
		gregorianCalendar.set(Calendar.HOUR_OF_DAY, 23);
		return gregorianCalendar.getTime();
	}
	
	public static Date getFirstTimeOfDayHalf(Date date) {
		Integer DayHalf = getHour(date) / 12;
		gregorianCalendar.setTime(date);
		gregorianCalendar.set(Calendar.SECOND, 0);
		gregorianCalendar.set(Calendar.MINUTE, 0);
		gregorianCalendar.set(Calendar.HOUR_OF_DAY, DayHalf * 12);
		return gregorianCalendar.getTime();
	}
	
	public static Date getLastTimeOfDayHalf(Date date) {
		Integer DayHalf = getHour(date) / 12;
		gregorianCalendar.setTime(date);
		gregorianCalendar.set(Calendar.SECOND, 59);
		gregorianCalendar.set(Calendar.MINUTE, 59);
		gregorianCalendar.set(Calendar.HOUR_OF_DAY, DayHalf * 12 + 11);
		return gregorianCalendar.getTime();
	}
	
	public static Date getFirstTimeOfHour(Date date) {
		gregorianCalendar.setTime(date);
		gregorianCalendar.set(Calendar.SECOND, 0);
		gregorianCalendar.set(Calendar.MINUTE, 0);
		return gregorianCalendar.getTime();
	}
	
	public static Date getLastTimeOfHour(Date date) {
		gregorianCalendar.setTime(date);
		gregorianCalendar.set(Calendar.SECOND, 59);
		gregorianCalendar.set(Calendar.MINUTE, 59);
		return gregorianCalendar.getTime();
	}
	
	public static Date getFirstTimeOfHourQuarter(Date date) {
		Integer hourQuarter = getMinute(date) / 15;
		gregorianCalendar.setTime(date);
		gregorianCalendar.set(Calendar.SECOND, 0);
		gregorianCalendar.set(Calendar.MINUTE, 15 * hourQuarter);
		return gregorianCalendar.getTime();
	}
	
	public static Date getLastTimeOfHourQuarter(Date date) {
		Integer hourQuarter = getMinute(date) / 15;
		gregorianCalendar.setTime(date);
		gregorianCalendar.set(Calendar.SECOND, 59);
		gregorianCalendar.set(Calendar.MINUTE, 15 * hourQuarter + 14);
		return gregorianCalendar.getTime();
	}
	
	public static Date getYearBefore(Date date) {
		return getYearBefore(date, 1);
	}
	
	public static Date getYearBefore(Date date, int yearBefore) {
		gregorianCalendar.setTime(date);
		int year = gregorianCalendar.get(Calendar.YEAR);
		gregorianCalendar.set(Calendar.YEAR, year - yearBefore);
		return gregorianCalendar.getTime();
	}

	public static Date getYearAfter(Date date) {
		return getYearAfter(date, 1);
	}
	
	public static Date getYearAfter(Date date, int yearAfter) {
		gregorianCalendar.setTime(date);
		int year = gregorianCalendar.get(Calendar.YEAR);
		gregorianCalendar.set(Calendar.YEAR, year + yearAfter);
		return gregorianCalendar.getTime();
	}
	
	public static Date getMonthBefore(Date date) {
		return getMonthBefore(date, 1);
	}
	
	public static Date getMonthBefore(Date date, int monthBefore) {
		gregorianCalendar.setTime(date);
		int month = gregorianCalendar.get(Calendar.MONTH);
		gregorianCalendar.set(Calendar.MONTH, month - monthBefore);
		return gregorianCalendar.getTime();
	}

	public static Date getMonthAfter(Date date) {
		return getMonthAfter(date, 1);
	}
	
	public static Date getMonthAfter(Date date, int monthAfter) {
		gregorianCalendar.setTime(date);
		int month = gregorianCalendar.get(Calendar.MONTH);
		gregorianCalendar.set(Calendar.MONTH, month + monthAfter);
		return gregorianCalendar.getTime();
	}

	/**
	 * 获取日期前一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDayBefore(Date date) {
		return getDayBefore(date, 1);
	}

	/**
	 * 获取日期前n天
	 * 
	 * @param date
	 * @param beforeDay
	 * @return
	 */
	public static Date getDayBefore(Date date, int beforeDay) {
		gregorianCalendar.setTime(date);
		int day = gregorianCalendar.get(Calendar.DATE);
		gregorianCalendar.set(Calendar.DATE, day - beforeDay);
		return gregorianCalendar.getTime();
	}

	/**
	 * 获取日期后一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDayAfter(Date date) {
		return getDayAfter(date, 1);
	}

	/**
	 * 获取日期前n天
	 * 
	 * @param date
	 * @param dayAfter
	 * @return
	 */
	public static Date getDayAfter(Date date, int dayAfter) {
		gregorianCalendar.setTime(date);
		int day = gregorianCalendar.get(Calendar.DATE);
		gregorianCalendar.set(Calendar.DATE, day + dayAfter);
		return gregorianCalendar.getTime();
	}
	
	/**
	 * 获取时间前n小时
	 * @param date
	 * @param beforeHour
	 * @return
	 */
	public static Date getHourBefore(Date date, int beforeHour) {
		gregorianCalendar.setTime(date);
		int hour = gregorianCalendar.get(Calendar.HOUR);
		gregorianCalendar.set(Calendar.HOUR, hour - beforeHour);
		return gregorianCalendar.getTime();
	}
	
	/**
	 * 获取时间前n小时
	 * @param date
	 * @param afterHour
	 * @return
	 */
	public static Date getHourAfter(Date date, int afterHour) {
		gregorianCalendar.setTime(date);
		int hour = gregorianCalendar.get(Calendar.HOUR);
		gregorianCalendar.set(Calendar.HOUR, hour + afterHour);
		return gregorianCalendar.getTime();
	}
	
	/**
	 * 获取时间前n秒
	 * @param date
	 * @param seconds
	 * @return
	 */
	public static Date getSecondBefore(Date date, int seconds) {
		gregorianCalendar.setTime(date);
		int second = gregorianCalendar.get(Calendar.SECOND);
		gregorianCalendar.set(Calendar.SECOND, second - seconds);
		return gregorianCalendar.getTime();
	}
	
	/**
	 * 获取时间后n秒
	 * @param date
	 * @param seconds
	 * @return
	 */
	public static Date getSecondAfter(Date date, int seconds) {
		gregorianCalendar.setTime(date);
		int second = gregorianCalendar.get(Calendar.SECOND);
		gregorianCalendar.set(Calendar.SECOND, second + seconds);
		return gregorianCalendar.getTime();
	}
	
	public static int getYear(Date date) {
		gregorianCalendar.setTime(date);
		return gregorianCalendar.get(Calendar.YEAR);
	}

	/**
	 * 获取当前年
	 * 
	 * @return
	 */
	public static int getNowYear() {
		Calendar d = Calendar.getInstance();
		return d.get(Calendar.YEAR);
	}
	
	public static int getMonth(Date date) {
		gregorianCalendar.setTime(date);
		return gregorianCalendar.get(Calendar.MONTH) + 1;
	}
	
	public static int getDay(Date date) {
		gregorianCalendar.setTime(date);
		return gregorianCalendar.get(Calendar.DAY_OF_MONTH);
	}
	
	public static int getMonthLastDay(Date date) {
		gregorianCalendar.setTime(date);
		return gregorianCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
	
	public static int getHour(Date date) {
		gregorianCalendar.setTime(date);
		return gregorianCalendar.get(Calendar.HOUR_OF_DAY);
	}
	
	public static int getMinute(Date date) {
		gregorianCalendar.setTime(date);
		return gregorianCalendar.get(Calendar.MINUTE);
	}

	/**
	 * 获取当前月份
	 * 
	 * @return
	 */
	public static int getNowMonth() {
		Calendar d = Calendar.getInstance();
		return d.get(Calendar.MONTH) + 1;
	}

	/**
	 * 获取当月天数
	 * 
	 * @return
	 */
	public static int getNowMonthDay() {
		Calendar d = Calendar.getInstance();
		return d.getActualMaximum(Calendar.DATE);
	}

	/**
	 * 获取时间段的每一天
	 * 
	 * @startDate 开始日期
	 * @endDate 结算日期
	 * @return 日期列表
	 */
	public static List<Date> getEveryDay(Date startDate, Date endDate) {
		if (startDate == null || endDate == null) {
			return null;
		}
		// 格式化日期(yy-MM-dd)
		startDate = DateUtil.getDateFormat(DateUtil.getDateFormat(startDate));
		endDate = DateUtil.getDateFormat(DateUtil.getDateFormat(endDate));
		List<Date> dates = new ArrayList<Date>();
		gregorianCalendar.setTime(startDate);
		dates.add(gregorianCalendar.getTime());
		while (gregorianCalendar.getTime().compareTo(endDate) < 0) {
			// 加1天
			gregorianCalendar.add(Calendar.DAY_OF_MONTH, 1);
			dates.add(gregorianCalendar.getTime());
		}
		return dates;
	}

	/**
	 * 获取提前多少个月
	 * 
	 * @param monty
	 * @return
	 */
	public static Date getFirstMonth(int monty) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -monty);
		return c.getTime();
	}
	
	/**
	 * 格式化日期为 时分秒
	 * @param 
	 * @return
	 */
	public static String formatTime(Long mss) {
//		long days = mss / (1000 * 60 * 60 * 24);
		long hours = (mss / (1000 * 60 * 60));
		long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
		long seconds = (mss % (1000 * 60)) / 1000;
		return hours + "小时" + minutes + "分钟" + seconds + "秒";
	}
	
}