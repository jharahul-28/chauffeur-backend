package com.velvet.rest.services.user.utils;

import java.util.regex.Pattern;

public class UserServiceUtil {

	public static boolean patternMatches(String emailAddress, String regexPattern) {
	    return Pattern.compile(regexPattern)
	      .matcher(emailAddress)
	      .matches();
	}
}
