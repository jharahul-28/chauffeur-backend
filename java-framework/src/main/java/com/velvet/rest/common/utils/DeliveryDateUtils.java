package com.velvet.rest.common.utils;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class DeliveryDateUtils {

	public static final Integer CONSUMER_ORDER_DAYS_FACTOR = 1;

	private static final ZoneId IST = ZoneId.of("Asia/Kolkata");

	public static final List<DayOfWeek> DEFAULT_WORKING_DAYS = List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY,
			DayOfWeek.FRIDAY);

	public static Date getDeliveryDateForCurrentOrder() {
		LocalDateTime now = LocalDateTime.now(IST);
		LocalTime localTime = now.toLocalTime();
		String start = ParamsUtils.getParam("CURRENT_ORDER_VISIBILITY_END_TIME", "16:00:00");
		if (localTime.isAfter(LocalTime.parse(start))) {
			return Date.valueOf(LocalDate.ofInstant(now.plusDays(1).toInstant(ZoneOffset.UTC), ZoneId.systemDefault()));
		} else {
			return Date.valueOf(LocalDate.ofInstant(now.toInstant(ZoneOffset.UTC), ZoneId.systemDefault()));
		}
	}

	public static Date getDeliveryDateForPreviousOrder(Integer sinceDays) {
		return new Date(DateUtils.addDays(getDeliveryDateForCurrentOrder(), -sinceDays).getTime());
	}

	public static Date getCustomDeliveryDate() {
		LocalDateTime now = LocalDateTime.now(IST);
		LocalTime localTime = now.toLocalTime();
		String start = "12:00:00";
		if (localTime.isAfter(LocalTime.parse(start))) {
			return Date.valueOf(LocalDate.ofInstant(now.plusDays(1).toInstant(ZoneOffset.UTC), ZoneId.systemDefault()));
		} else {
			return Date.valueOf(LocalDate.ofInstant(now.toInstant(ZoneOffset.UTC), ZoneId.systemDefault()));
		}
	}

	public static java.util.Date getConsumerCronDeliveryDate(boolean checkForHoliday) {
		LocalTime localTime = DateUtils.getLocalDateTimeIST().toLocalTime();
		String last = ParamsUtils.getParam("PROCESS_ORDER_CRON_LAST_TIME", "10:00:00");
		if (localTime.isBefore(LocalTime.parse(last))) {
			return getConsumerDeliveryDate(checkForHoliday, 0);
		} else {
			return getConsumerDeliveryDate(checkForHoliday);
		}
	}

	public static java.util.Date getConsumerDeliveryDate(boolean checkForHoliday) {
		LocalTime localTime = DateUtils.getLocalDateTimeIST().toLocalTime();
		String nextDayOrderingTime = ParamsUtils.getParam("NEXT_DAY_ORDERING_START_TIME", "12:00:00");

		int dayFactor = localTime.isAfter(LocalTime.parse(nextDayOrderingTime)) ? CONSUMER_ORDER_DAYS_FACTOR : 0;
		return getConsumerDeliveryDate(checkForHoliday, dayFactor);
	}

	public static java.util.Date getConsumerDeliveryDate(boolean checkForHoliday, Integer dayFactor) {
		java.util.Date deliveryDate = DateUtils.convertDateUtcToIst(DateUtils.addDays(new java.util.Date(), dayFactor));
		if (checkForHoliday) {
			int orderDaysFactor = dayFactor;
			List<LocalDate> holidays = getPublicHolidayDates();
			while (isHoliday(deliveryDate, holidays)) {
				deliveryDate = DateUtils.convertDateUtcToIst(DateUtils.addDays(new java.util.Date(), ++orderDaysFactor));
			}
		}
		return deliveryDate;
	}

	public static List<LocalDate> getPublicHolidayDates() {
		String holidayParam = ParamsUtils.getParam("CONSUMER_ORDER_HOLIDAYS");
		List<String> holidayDates = List.of(holidayParam.split(","));
		List<LocalDate> dates = new ArrayList<>();
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		for (String holidayDate : holidayDates) {
			LocalDate localDate = LocalDate.parse(holidayDate.trim(), dateFormat);
			dates.add(localDate);
		}
		return dates;
	}

	public static boolean isHoliday(java.util.Date deliveryDate, List<LocalDate> holidays) {
		LocalDate deliveryLocalDate = convertToLocalDate(deliveryDate);
		if (CollectionUtils.isEmpty(holidays)) {
			holidays = getPublicHolidayDates();
		}
		return holidays.contains(deliveryLocalDate);
	}

	public static String addDayOfMonthSuffix(String formattedDate) {
		String[] parts = formattedDate.split(", ");
		int day = Integer.parseInt(parts[1]);
		String suffix;
		if (day >= 11 && day <= 13) {
			suffix = "th";
		} else {
			switch (day % 10) {
			case 1:
				suffix = "st";
				break;
			case 2:
				suffix = "nd";
				break;
			case 3:
				suffix = "rd";
				break;
			default:
				suffix = "th";
				break;
			}
		}
		return parts[0] + ", " + day + suffix;
	}

	public static LocalDate convertToLocalDate(java.util.Date date) {
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public static DayOfWeek getA2CDayOfWeek() {
		java.util.Date deliveryDate = DeliveryDateUtils.getConsumerDeliveryDate(false);
		Calendar cal = Calendar.getInstance();
		cal.setTime(deliveryDate);
		cal.add(Calendar.DATE, -1);
		return DayOfWeek.valueOf(cal.getTime().toInstant().atZone(ZoneId.systemDefault()).getDayOfWeek().name());
	}

	public static Date getNextDayDeliveryDate(List<DayOfWeek> orgWorkingDays) {
		if (CollectionUtils.isEmpty(orgWorkingDays)) {
			orgWorkingDays = DEFAULT_WORKING_DAYS;
		}
		LocalDateTime now = LocalDateTime.now(IST);

		String nextDayOrderingTime = ParamsUtils.getParam("NEXT_DAY_ORDERING_START_TIME", "12:00:00");
		int dayFactor = now.toLocalTime().isAfter(LocalTime.parse(nextDayOrderingTime)) ? CONSUMER_ORDER_DAYS_FACTOR : 0;

		LocalDate nextDay = now.toLocalDate().plusDays(dayFactor);
		return findNextWorkingDay(nextDay, orgWorkingDays);
	}

	public static Date getCurrentDeliveryDate(List<DayOfWeek> orgWorkingDays) {
		if (CollectionUtils.isEmpty(orgWorkingDays)) {
			orgWorkingDays = DEFAULT_WORKING_DAYS;
		}
		LocalDate today = getDeliveryDateForCurrentOrder().toLocalDate();
		return findNextWorkingDay(today, orgWorkingDays);
	}

	public static List<DayOfWeek> getDefaultWorkingDays() {
		return DEFAULT_WORKING_DAYS;
	}

	public static Date findNextWorkingDay(LocalDate from, List<DayOfWeek> workingDays) {
		List<LocalDate> holidays = getPublicHolidayDates();
		LocalDate candidate = from;
		for (int i = 0; i < 14; i++) {
			if (workingDays.contains(candidate.getDayOfWeek()) && !holidays.contains(candidate)) {
				return Date.valueOf(candidate);
			}
			candidate = candidate.plusDays(1);
		}
		return Date.valueOf(from);
	}

}
