package com.velvet.rest.services.user.utils;

import java.util.regex.Pattern;

public class UserServiceUtil {

	public static boolean patternMatches(String emailAddress, String regexPattern) {
	    return Pattern.compile(regexPattern)
	      .matcher(emailAddress)
	      .matches();
	}

	public static String formatPhoneNumber(String countryCode, String phone) {
		if (phone == null) {
			throw new IllegalArgumentException("Phone number cannot be null");
		}
		String combinedPhoneNumber = countryCode != null ? countryCode + phone : phone;
		return combinedPhoneNumber.replaceAll("\\+", "");
	}
}
