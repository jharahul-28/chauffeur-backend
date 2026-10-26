/**
 * 
 */
package com.velvet.rest.common.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.util.StringUtils;

/**
 * @author Mohit
 *
 */
public class DateUtils {

	/** The Constant INVALID. */
	public static final long INVALID = -99999999; // NOSONAR

	public static final String SHORT_DATE_FMT = "yyyy-MM-dd"; // NOSONAR

	public static final String YEAR_MONTH_FMT = "yyyy-MM";

	public static final String DATE_FMT_WITH_TIME = "yyyy-MM-dd HH:mm:ss"; // NOSONAR

	public static final String TIME_FORMAT_WITHOUT_COLON = "HHmm"; // NOSONAR

	public static final String TIME_FORMAT_WITH_COLON = "HH:mm"; // NOSONAR

	public static final String DATE_MONTH_FMT = "dd MMMM yyyy"; // NOSONAR

	public static final DateTimeZone APPL_TZ = DateTimeZone.forID(TimeZone.getDefault().getID());

	private static final ZoneId IST = ZoneId.of("Asia/Kolkata");

	public static Date getDate(String format, String dateStr) {
		Date date = null;
		final DateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setLenient(false);
		try {
			date = dateFormat.parse(dateStr);
		} catch (final Exception e) {
			// do nothing
		}
		return date;
	}

	public static Date getShortDate() {
		return getDate(SHORT_DATE_FMT, toString(new Date()));
	}

	public static String toString(Date dt) {
		return toString(SHORT_DATE_FMT, dt);
	}

	public static Date getDate() {
		return getDate(toString(new Date()));
	}

	public static Date getDate(String dateStr) {
		return getDate(SHORT_DATE_FMT, dateStr);
	}

	public static String toString(String format, Date dt) {
		final DateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setLenient(false);
		return dateFormat.format(dt);
	}

	public static Date addDays(Date firstDate, int days) {
		Date addedDate = null;
		if (firstDate != null) {
			final DateTime addedDateTime = new DateTime(firstDate, APPL_TZ).plusDays(days);
			addedDate = addedDateTime.toDate();
		}
		return addedDate;
	}

	public static Date addMinutes(Date firstDate, int minutes) {
		Date addedDate = null;
		if (firstDate != null) {
			final DateTime addedDateTime = new DateTime(firstDate, APPL_TZ).plusMinutes(minutes);
			addedDate = addedDateTime.toDate();
		}
		return addedDate;
	}

	/**
	 * Get system date with time.
	 *
	 * @return the date
	 */
	public static Date getDateTime() {
		return new Date();
	}

	public static String getTime(String format, Date date) {
		if (StringUtils.isEmpty(format)) {
			format = TIME_FORMAT_WITHOUT_COLON;
		}
		if (date == null) {
			date = new Date();
		}
		return new SimpleDateFormat(format).format(date);
	}

	public static String getCurrentTime(String format) {
		if (StringUtils.isEmpty(format)) {
			format = TIME_FORMAT_WITH_COLON;
		}
		Date date = new Date();
		return new SimpleDateFormat(format).format(date);
	}

	public static String getCurrentTime() {
		return getCurrentTime(null);
	}

	public static Integer getDayofWeek(LocalDateTime currentdate) {
		return currentdate.getDayOfWeek().getValue();
	}

	public static Date convertDateUtcToIst(Date date) {
		LocalDateTime l = LocalDateTime.ofInstant(date.toInstant(), IST);
		return Date.from(l.toInstant(ZoneOffset.UTC));
	}

	public static LocalDateTime getLocalDateTimeIST() {
		return LocalDateTime.ofInstant(new Date().toInstant(), IST);
	}
}
