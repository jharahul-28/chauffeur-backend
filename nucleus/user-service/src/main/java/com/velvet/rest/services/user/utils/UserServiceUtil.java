package com.velvet.rest.services.user.utils;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.velvet.rest.common.beans.ErrorBean;
import com.velvet.rest.common.exceptions.ValidationException;

public class UserServiceUtil {

	public static boolean patternMatches(String emailAddress, String regexPattern) {
	    return Pattern.compile(regexPattern)
	      .matcher(emailAddress)
	      .matches();
	}

	public static String formatPhoneNumber(String countryCode, String phone) {
		if (StringUtils.isBlank(phone)) {
			throw new ValidationException(ErrorBean.withError("INVALID_PHONE", "Phone number is required", "phone"));
		}
		String combinedPhoneNumber = countryCode != null ? countryCode + phone : phone;
		return combinedPhoneNumber.replaceAll("\\+", "");
	}
}
