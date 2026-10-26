package com.velvet.rest.common.utils;

import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @author Pradeep Kumar
 */
public class DateUtilsV2 {

	public static final String SHORT_DATE_FMT = "yyyy-MM-dd";

	public static final String SHORT_DATE_FMT_IST = "dd-MM-yyyy";

	public static final String DATE_FMT_WITH_TIME = "yyyy-MM-dd HH:mm:ss";

	public static final String DATE_FMT_WITH_TIME_IST = "dd-MM-yyyy HH:mm:ss";

	public static final String TIME_FORMAT_WITHOUT_COLON = "HHmm";

	public static final String TIME_FORMAT_WITH_COLON = "HH:mm";

	public static final String TIME_FORMAT = "HH:mm:ss";

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private static final DateTimeFormatter DATE_TIME_FORMATTER_IST = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private static final ZoneId IST = ZoneId.of("Asia/Kolkata");

	/**
	 * Get the current date and time
	 *
	 * @return
	 */
	public static LocalDateTime getCurrentDateTime() {
		return LocalDateTime.now();
	}

	/**
	 * Get the current date in "yyyy-MM-dd" format
	 *
	 * @return
	 */
	public static String getCurrentDate() {
		return LocalDate.now().format(DATE_FORMATTER);
	}

	/**
	 * Get the current time in "HH:mm:ss" format
	 *
	 * @return
	 */
	public static String getCurrentTime() {
		return LocalTime.now().format(DateTimeFormatter.ofPattern(TIME_FORMAT));
	}

	public static String getCurrentTimeIST() {
		return getDateTimeIST().toLocalTime().format(DateTimeFormatter.ofPattern(TIME_FORMAT));
	}

	/**
	 * Get the current date and time in "yyyy-MM-dd HH:mm:ss" format
	 *
	 * @return
	 */
	public static String getCurrentDateTimeString() {
		return getCurrentDateTime().format(DATE_TIME_FORMATTER);
	}

	/**
	 * Parse a date string in "yyyy-MM-dd" format to LocalDate
	 *
	 * @param dateString
	 * @return
	 */
	public static LocalDate parseDate(String dateString) {
		return LocalDate.parse(dateString, DATE_FORMATTER);
	}

	/**
	 * @param format
	 * @param dateStr
	 * @return
	 */
	public static Date getUtilDate(String format, String dateStr) {
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

	/**
	 * Format a LocalDate to a date string in "yyyy-MM-dd" format
	 *
	 * @param date
	 * @return
	 */
	public static String formatDate(LocalDate date) {
		return date.format(DATE_FORMATTER);
	}

	/**
	 * Format a LocalDateTime to a date-time string in "yyyy-MM-dd HH:mm:ss" format
	 *
	 * @param dateTime
	 * @return
	 */
	public static String formatDateTime(LocalDateTime dateTime) {
		return dateTime.format(DATE_TIME_FORMATTER);
	}

	public static String formatDateTimeIST(LocalDateTime dateTime) {
		return dateTime.format(DATE_TIME_FORMATTER_IST);
	}

	public static LocalDateTime formatDateTimeStringToISTLocalTime(String dateTime) {
		return LocalDateTime.parse(dateTime, DATE_TIME_FORMATTER_IST);
	}

	/**
	 * Calculate the difference in days between two dates
	 *
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static long daysBetween(LocalDate startDate, LocalDate endDate) {
		return ChronoUnit.DAYS.between(startDate, endDate);
	}

	/**
	 * Add days to a given date
	 *
	 * @param date
	 * @param daysToAdd
	 * @return
	 */
	public static LocalDate addDays(LocalDate date, long daysToAdd) {
		return date.plusDays(daysToAdd);
	}

	/**
	 * Add hours to a given date
	 *
	 * @param date
	 * @param hoursToAdd
	 * @return
	 */
	public static LocalDateTime addHours(LocalDateTime date, long hoursToAdd) {
		return date.plusHours(hoursToAdd);
	}

	/**
	 * @param format
	 * @param date
	 * @return
	 */
	public static String getTime(String format, LocalDateTime date) {
		if (StringUtils.isEmpty(format)) {
			format = TIME_FORMAT_WITHOUT_COLON;
		}
		if (date == null) {
			date = LocalDateTime.now();
		}
		return new SimpleDateFormat(format).format(date);
	}

	/**
	 * @param format
	 * @return
	 */
	public static String getCurrentTime(String format) {
		if (StringUtils.isEmpty(format)) {
			format = TIME_FORMAT_WITH_COLON;
		}
		LocalDateTime date = LocalDateTime.now();
		return new SimpleDateFormat(format).format(date);
	}

	/**
	 * @return
	 */
	public static String getCurrentUtcDateTime() {
		ZonedDateTime utcDateTime = ZonedDateTime.now(ZoneId.of("UTC"));
		return utcDateTime.format(DATE_TIME_FORMATTER);
	}

	/**
	 * @param currentdate
	 * @return
	 */
	public static Integer getDayOfWeek(LocalDateTime currentdate) {
		return currentdate.getDayOfWeek().getValue();
	}

	/**
	 * @param localDateTime
	 * @return
	 */
	public static Date convertLocalDateTimeToDate(LocalDateTime localDateTime) {
		Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
		return Date.from(instant);
	}

	/**
	 * @param localDateTime
	 * @return
	 */
	public static java.sql.Date convertLocalDateTimeToSqlDate(LocalDateTime localDateTime) {
		java.util.Date utilDate = java.util.Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
		return new java.sql.Date(utilDate.getTime());
	}

	/**
	 *
	 * @return
	 */
	public static java.sql.Date getCurrentSqlDate() {
		return convertLocalDateTimeToSqlDate(LocalDateTime.now());
	}

	/**
	 * convert java.util.Date to IST time zone
	 *
	 * @param date
	 * @return
	 */
	public static Date convertDateUtcToIst(Date date) {
		LocalDateTime l = LocalDateTime.ofInstant(date.toInstant(), IST);
		return Date.from(l.toInstant(ZoneOffset.UTC));
	}

	/**
	 *
	 * @param date
	 * @return
	 */
	public static LocalDate convertDateUtcToIstLocalDate(Date date) {
		return convertDateUtcToIst(date).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	/**
	 *
	 * @return
	 */
	public static Date getISTDate() {
		Date date = new Date();
		return convertDateUtcToIst(date);
	}

	/**
	 * return Local date time in IST Timezone
	 *
	 * @return
	 */
	public static LocalDateTime getDateTimeIST() {
		LocalDateTime localDate = getCurrentDateTime().plusHours(5).plusMinutes(30);
		return localDate;
	}

	/**
	 *
	 * @return
	 */
	public static LocalTime getTimeIST() {
		LocalDateTime localDate = getCurrentDateTime().plusHours(5).plusMinutes(30);
		return localDate.toLocalTime();
	}

	/**
	 * @param format
	 * @param dt
	 * @return
	 */
	public static String toString(String format, Date dt) {
		final DateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setLenient(false);
		return dateFormat.format(dt);
	}

	/**
	 *
	 * @param format
	 * @param dateStr
	 * @return
	 */
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

	/**
	 *
	 * @return
	 */
	public static java.sql.Date getSqlDateIST() {
		LocalDateTime localDate = getCurrentDateTime().plusHours(5).plusMinutes(30);
		java.sql.Date date = java.sql.Date.valueOf(localDate.toLocalDate());
		return date;
	}

	/**
	 *
	 * @return
	 */
	public static String getLocalDateTimeIST() {
		LocalDateTime localDate = getCurrentDateTime().plusHours(5).plusMinutes(30);
		String formattedDate = formatDateTimeIST(localDate);
		return formattedDate;
	}

	public static List<Date> getStartAndEndDate(java.sql.Date sqlDate) {
		List<Date> dates = new ArrayList<>();

		TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");

		Date utilDate = new Date(sqlDate.getTime());

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(utcTimeZone);

		calendar.setTime(utilDate);
		calendar.add(Calendar.DATE, -1);
		calendar.set(Calendar.HOUR_OF_DAY, 22);
		calendar.set(Calendar.MINUTE, 30);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		Date startTime = calendar.getTime();
		dates.add(startTime);

		calendar.setTime(utilDate);
		calendar.set(Calendar.HOUR_OF_DAY, 11);
		calendar.set(Calendar.MINUTE, 30);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		Date endTime = calendar.getTime();
		dates.add(endTime);

		return dates;
	}
}
