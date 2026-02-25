package com.velvet.rest.services.user.utils;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import com.velvet.rest.common.utils.ContextUtils;
import com.velvet.rest.common.utils.ParamsUtils;
import com.velvet.rest.common.utils.SecurityUtils;

/**
 * <p>
 * AuthUtils class.
 * </p>
 *
 * @author Mohit
 * @version $Id: $Id
 */
public class AuthUtils {

	private static final String APP_REALM = "velvet.com";

	public static String generateUserOtp() {
		Integer otpLength = ParamsUtils.getIntegerParam("USER_OTP_LENGTH", 6);
		return generateOtp(otpLength);
	}

	public static String generateAdminOtp() {
		Integer otpLength = ParamsUtils.getIntegerParam("ADMIN_OTP_LENGTH", 6);
		return generateOtp(otpLength);
	}

	public static Date getUserOtpExpiry() {
		final Integer validity = ParamsUtils.getIntegerParam("OTP_EXPIRY", 600);
		return getOtpExpiry(validity);
	}

	public static Date getAdminOtpExpiry() {
		final Integer validity = ParamsUtils.getIntegerParam("OTP_EXPIRY", 600);
		return getOtpExpiry(validity);
	}

	public static Date getOtpExpiry(final Integer validity) {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, validity);
		return cal.getTime();
	}

	public static String generateTestUserOtp() {
		Integer otpLength = ParamsUtils.getIntegerParam("USER_OTP_LENGTH", 6);
		return StringUtils.leftPad(ParamsUtils.getParam("DEFAULT_OTP", "123456"), otpLength, '0');
	}

	private static String generateOtp(Integer length) {
		if (ContextUtils.isProfileProd()) {
			return StringUtils.leftPad(RandomStringUtils.randomNumeric(length), length, '0');
		} else {
			return StringUtils.leftPad(ParamsUtils.getParam("DEFAULT_OTP", "123456"), length, '0');
		}
	}

	/**
	 * <p>
	 * getHashPassword.
	 * </p>
	 *
	 * @param identifier a {@link java.lang.String} object.
	 * @param password   a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String getHashPassword(@NonNull String identifier, @NonNull String password) {
		Assert.isTrue(StringUtils.isNotBlank(identifier), "Identifier can not be blank");
		Assert.isTrue(StringUtils.isNotBlank(password), "Password can not be blank");
		final StringBuilder builder = new StringBuilder().append(identifier).append(":").append(APP_REALM).append(":").append(password);
		final String hash = SecurityUtils.toMd5(builder.toString());
		return hash;
	}

	/**
	 * This method will return 6-digit alphanumeric referrer code.
	 * 
	 * @return String
	 */
	public static String generateReferralCode() {
		Integer otpLength = ParamsUtils.getIntegerParam("USER_OTP_LENGTH", 6);
		return RandomStringUtils.randomAlphanumeric(otpLength);
	}
}
