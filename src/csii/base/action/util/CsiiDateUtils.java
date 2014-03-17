package csii.base.action.util;
import java.lang.reflect.Constructor;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 */
public class CsiiDateUtils {

	private static final Log log = LogFactory.getLog(CsiiDateUtils.class);

	public static final int MILLIS_IN_SECOND = 1000;
	public static final int MILLIS_IN_MINUTE = 60 * 1000;
	public static final int MILLIS_IN_HOUR = 60 * 60 * 1000;
	public static final int MILLIS_IN_DAY = 24 * 60 * 60 * 1000;

	private CsiiDateUtils() {
	}

	public static int diffYear(Date d1, Date d2) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(d1);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(d2);
		return c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR);
	}

	public static int diffMonth(Date d1, Date d2) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(d1);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(d2);
		int m = c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH);
		int y = c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR);
		return y * 12 + m;
	}

	public static int diffWeek(Date d1, Date d2) {
		int d = diffDay(d1, d2);
		if (d == 0)
			return 0;
		Calendar c2 = Calendar.getInstance();
		c2.setTime(d2);
		int w2 = c2.get(Calendar.DAY_OF_WEEK);
		if (c2.getFirstDayOfWeek() != Calendar.SUNDAY)
			w2 = (w2 == Calendar.SUNDAY) ? 7 : w2 - 1;
		int w = d / 7;
		int dw = (w2 + d % 7);
		if (dw < 1)
			return w - 1;
		if (dw > 7)
			return w + 1;
		return w;
	}

	public static int diffDay(Date d1, Date d2) {
		long offset =
			TimeZone.getDefault().getRawOffset()
				+ TimeZone.getDefault().getDSTSavings();
		return (int)
			((d1.getTime() + offset) / MILLIS_IN_DAY
				- (d2.getTime() + offset) / MILLIS_IN_DAY);
	}

	public static int diffHour(Date d1, Date d2) {
		return (int)
			(d1.getTime() / MILLIS_IN_HOUR - d2.getTime() / MILLIS_IN_HOUR);
	}

	public static int diffMinute(Date d1, Date d2) {
		return (int)
			(d1.getTime() / MILLIS_IN_MINUTE - d2.getTime() / MILLIS_IN_MINUTE);
	}

	public static int diffSecond(Date d1, Date d2) {
		return (int)
			(d1.getTime() / MILLIS_IN_SECOND - d2.getTime() / MILLIS_IN_SECOND);
	}

	public static Date roll(
		Date now,
		int year,
		int month,
		int day,
		int hour,
		int minute,
		int second) {
		Calendar c = Calendar.getInstance();
		c.setTime(now);
		c.add(Calendar.YEAR, year);
		c.add(Calendar.MONTH, month);
		c.add(Calendar.DATE, day);
		c.add(Calendar.HOUR_OF_DAY, hour);
		c.add(Calendar.MINUTE, minute);
		c.add(Calendar.SECOND, second);
		if (Date.class == now.getClass())
			return c.getTime();
		return newInstance(now.getClass(), c.getTimeInMillis());
	}

	public static Date rollTime(Date now, int hour, int minute, int second) {
		return roll(now, 0, 0, 0, hour, minute, second);
	}

	public static Date rollDate(Date now, int year, int month, int date) {
		return roll(now, year, month, date, 0, 0, 0);
	}
    
	public static Date rollDate (Date now,int day){
		Calendar c = Calendar.getInstance();
		c.setTime(now);
		c.add(Calendar.DATE, day);
		if (Date.class == now.getClass())
			return c.getTime();
		return newInstance(now.getClass(), c.getTimeInMillis());
	}
	public static Date rollWeek(Date now, int week, int day) {
		Calendar c = Calendar.getInstance();
		c.setTime(now);
		c.add(Calendar.WEEK_OF_YEAR, week);
		c.add(Calendar.DAY_OF_WEEK, day);
		if (Date.class == now.getClass())
			return c.getTime();
		return newInstance(now.getClass(), c.getTimeInMillis());
	}

	private static Date newInstance(Class clazz, long newMillis) {
		try {
			Constructor c = clazz.getConstructor(new Class[] { long.class });
			Date d = (Date)c.newInstance(new Object[] { new Long(newMillis)});
			return d;
		} catch (Exception e) {
			return null;
		}
	}

	// ---------- general format and parse method -------------

	public static String format(Date date, String pattern) {
		String ret = null;
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		ret = format.format(date);
		return ret;
	}
	public static String formatDate(Date date, String pattern) {
		if (date == null)
			return CsiiStringUtils.formatString("", pattern.length());
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}
	public static String formatDatetime(Date date, String pattern) {
		if (date == null)
			return CsiiStringUtils.formatString("", pattern.length());
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}
	public static Date parse(String dateStr, String pattern) {
		return parse(dateStr, pattern, null);
	}

	public static Date parse(String dateStr, String pattern, Class clazz) {
		Date ret = null;

		try {
			SimpleDateFormat format = new SimpleDateFormat(pattern);
			ret = format.parse(dateStr);
		} catch (ParseException e) {
			log.warn(
				"date format error! format:" + pattern + ",input:" + dateStr,
				e);
			// slient skip exception, just return null;
			return null;
		}

		if (clazz == null || clazz == Date.class)
			return ret;
		return newInstance(clazz, ret.getTime());
	}

	// --------- special format and parse method --------------

	public static final String DATE_ISO_FORMAT = "yyyy-MM-dd";
	public static final String TIME_ISO_FORMAT = "HH:mm:ss";
	public static final String DATETIME_ISO_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final DateFormat DATE_ISO_FORMAT_OBJECT =
		new SimpleDateFormat(DATE_ISO_FORMAT);
	public static final DateFormat TIME_ISO_FORMAT_OBJECT =
		new SimpleDateFormat(TIME_ISO_FORMAT);
	public static final DateFormat DATETIME_ISO_FORMAT_OBJECT =
		new SimpleDateFormat(DATETIME_ISO_FORMAT);

	public static final String DATE_COMPACT_FORMAT = "yyyyMMdd";
	public static final String TIME_COMPACT_FORMAT = "HHmmss";
	public static final String DATETIME_COMPACT_FORMAT = "yyyyMMddHHmmss";
	public static final DateFormat DATE_COMPACT_FORMAT_OBJECT =
		new SimpleDateFormat(DATE_COMPACT_FORMAT);
	public static final DateFormat TIME_COMPACT_FORMAT_OBJECT =
		new SimpleDateFormat(TIME_COMPACT_FORMAT);
	public static final DateFormat DATETIME_COMPACT_FORMAT_OBJECT =
		new SimpleDateFormat(DATETIME_COMPACT_FORMAT);

	public static String formatISODate(java.sql.Date date) {
		return DATE_ISO_FORMAT_OBJECT.format(date);
	}

	public static String formatISOTime(java.sql.Time time) {
		return TIME_ISO_FORMAT_OBJECT.format(time);
	}

	public static String formatISODatetime(java.sql.Timestamp timestamp) {
		return DATETIME_ISO_FORMAT_OBJECT.format(timestamp);
	}

	public static java.sql.Date parseISODate(String dateStr) {
		return (java.sql.Date)parse(
			dateStr,
			DATE_ISO_FORMAT_OBJECT,
			java.sql.Date.class);
	}

	public static java.sql.Time parseISOTime(String timeStr) {
		return (java.sql.Time)parse(
			timeStr,
			TIME_ISO_FORMAT_OBJECT,
			java.sql.Time.class);
	}

	public static java.sql.Timestamp parseISODatetime(String dateStr) {
		return (java.sql.Timestamp)parse(
			dateStr,
			DATETIME_ISO_FORMAT_OBJECT,
			java.sql.Timestamp.class);
	}

	public static String formatCompactDate(java.sql.Date date) {
		return DATE_COMPACT_FORMAT_OBJECT.format(date);
	}

	public static String formatCompactTime(java.sql.Time time) {
		return TIME_COMPACT_FORMAT_OBJECT.format(time);
	}

	public static String formatCompactDatetime(java.sql.Timestamp timestamp) {
		return DATETIME_COMPACT_FORMAT_OBJECT.format(timestamp);
	}
	
	/**
	 * @author taphoon
	 * @param date
	 * @return
	 */
	public static String formatCompactDatetime(Date date) {
		return DATETIME_COMPACT_FORMAT_OBJECT.format(date);
	}

	public static java.sql.Date parseCompactDate(String dateStr) {
		return (java.sql.Date)parse(
			dateStr,
			DATE_COMPACT_FORMAT_OBJECT,
			java.sql.Date.class);
	}

	public static java.sql.Time parseCompactTime(String timeStr) {
		return (java.sql.Time)parse(
			timeStr,
			TIME_COMPACT_FORMAT_OBJECT,
			java.sql.Time.class);
	}

	public static java.sql.Timestamp parseCompactDatetime(String dateStr) {
		return (java.sql.Timestamp)parse(
			dateStr,
			DATETIME_COMPACT_FORMAT_OBJECT,
			java.sql.Timestamp.class);
	}

	private static Date parse(String dateStr, DateFormat format, Class clazz) {
		Date ret = null;

		try {
			ret = format.parse(dateStr);
			return newInstance(clazz, ret.getTime());
		} catch (ParseException e) {
			log.warn(
				"date format error! format:"
					+ ((SimpleDateFormat)format).toPattern()
					+ ",input:"
					+ dateStr,
				e);
			// slient skip exception, just return null;
			return null;
		}

	}
	
	private static Calendar getCalendar(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c;
	}

	public static int getYear(Date date) {
		return getCalendar(date).get(Calendar.YEAR);
	}

	public static int getMonth(Date date) {
		return getCalendar(date).get(Calendar.MONTH) + 1;
	}

	public static int getDay(Date date) {
		return getCalendar(date).get(Calendar.DAY_OF_MONTH);
	}
	public static final Calendar date2Calendar(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c;
	}

	public static boolean isMonth(Date batchDate) {
		if (batchDate == null) {
			return false;
		}
		Calendar c = date2Calendar(batchDate);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DATE);
		if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8
				|| month == 10 || month == 12) {
			return day == 31;
		}
		if (month == 4 || month == 6 || month == 9 || month == 11) {
			return day == 30;
		}
		if (month == 2) {
			return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0) ? day == 29
					: day == 28;
		}
		return false;
	}

	public static boolean isQuarter(Date batchDate) {
		if (batchDate == null) {
			return false;
		}
		Calendar c = date2Calendar(batchDate);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DATE);
		return (month == 3 && day == 31) || (month == 6 && day == 30)
				|| (month == 9 && day == 30) || (month == 12 && day == 31);
	}

	public static boolean isHalfyear(Date batchDate) {
		if (batchDate == null) {
			return false;
		}
		Calendar c = date2Calendar(batchDate);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DATE);
		return (month == 6 && day == 30) || (month == 12 && day == 31);
	}

	public static boolean isYear(Date batchDate) {
		if (batchDate == null) {
			return false;
		}
		Calendar c = date2Calendar(batchDate);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DATE);
		return (month == 12 && day == 31);
	}

	public static void main(String[] args) {
		TimeZone z = TimeZone.getDefault();
		Date d1 = new Date();
		java.sql.Date d2 =
			new java.sql.Date(
				System.currentTimeMillis() + 60000 * 60 * 43 + 34000);
		d2 = (java.sql.Date)rollDate(d2, 3, 4, 1);
		java.sql.Time t = new java.sql.Time(System.currentTimeMillis());
		java.sql.Timestamp t1 =
			new java.sql.Timestamp(System.currentTimeMillis());
		System.err.println("compare:" + diffDay(d1, d2));
		System.err.println("compare:" + diffHour(d1, d2));
		System.err.println("compare:" + diffMinute(d1, d2));
		System.err.println("compare:" + diffSecond(d1, d2));
		System.err.println("compare:" + diffWeek(d1, d2));
		System.err.println("compare:" + diffMonth(d1, d2));
		System.err.println("compare:" + diffYear(d1, d2));

		System.err.println("format:" + format(d2, "yyyy-MM-dd HH:mm:ss"));
		System.err.println("format:" + formatCompactDate(d2));
		System.err.println("format:" + formatCompactDatetime(t1));
		System.err.println("format:" + formatCompactTime(t));
		System.err.println("format:" + formatISODate(d2));
		System.err.println("format:" + formatISODatetime(t1));
		System.err.println("format:" + formatISOTime(t));

		System.err.println(parse("2002-01-01 13:22:11", "yyyy-MM-dd HH:mm:ss"));
		System.err.println(
			parse("2002-01-01 13:22:11", "yyyy-MM-dd HH:mm:ss", Date.class));
		System.err.println(parseISODate("2001-01-01"));
		System.err.println(parseISOTime("23:46:11"));
		System.err.println(parseISODatetime("2001-01-01 23:46:11"));

		System.err.println(parseCompactDate("20010101"));
		System.err.println(parseCompactTime("234611"));
		System.err.println(parseCompactDatetime("20000101234611"));

		System.err.println(rollDate(t1, 3, 0, 1));
		System.err.println(rollTime(t1, -3, 4, 30));
		System.err.println(rollWeek(t1, -3, 4));
		
		System.err.println(formatCompactDatetime(new Date()));

	}
	public static final java.sql.Date toDate(String str, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			return new java.sql.Date(sdf.parse(str).getTime());
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	public static final Timestamp toTimestamp(String str, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			return new Timestamp(sdf.parse(str).getTime());
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	final public static  java.sql.Date getTime(String time) {
		if (time == null) {
			return null;
		}
		int pos = time.indexOf(":");
		String strhour = time;
		String strminute = "0";
		if (pos != -1) {
			strhour = time.substring(0, pos);
			strminute = time.substring(pos + 1);
		}
		int hour = 0;
		int minute = 0;
		try {
			hour = Integer.parseInt(strhour);
			minute = Integer.parseInt(strminute);
		} catch (NumberFormatException e) {
			return null;
		}
		Calendar cd = Calendar.getInstance();
		cd.set(Calendar.HOUR_OF_DAY, hour);
		cd.set(Calendar.MINUTE, minute);
		return new java.sql.Date(cd.getTime().getTime());
	}
}
