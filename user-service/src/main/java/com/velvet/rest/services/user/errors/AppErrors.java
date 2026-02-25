package com.velvet.rest.services.user.errors;

import com.velvet.rest.common.properties.Errors;

/**
 * <p>
 * AppErrors class.
 * </p>
 *
 * @author Mohit
 * @version $Id: $Id
 */
public class AppErrors extends Errors {

	public static final String OTP_ERROR = "otp_error";

	public static final String MOBILE_ERROR = "mobile_error";

	public static final String EMAIL_ERROR = "email_error";

	public static final String TRUECALLER_ERROR = "truecaller_error";

	public static final String OTP_MOBILE_ERROR = "otp_incorrect";

	public static final String OTP_EXPIRED_ERROR = "otp_expired_error";

	public static final String OTP_MATCH_ERROR = "otp_match_error";

	public static final String USER_NOT_FOUND = "user_not_found";

	public static final String USER_FOUND = "user_found";

	public static final String INVALID_USERNAME = "invalid_username";

	public static final String USER_BLOCKED = "user_not_active";

	public static final String AREA_INACTIVE = "area_not_active";

	public static final String AREA_CITY_INVALID = "area_city_invalid";

	public static final String CITY_INACTIVE = "city_not_active";

	public static final String GENERATE_LOGIN_OTP_ERROR = "generate_login_otp_error";

	public static final String OTP_MAX_ATTEMPT = "otp_max_attempt";

	public static final String OTP_INCORRECT = "otp_incorrect";

	public static final String INCORRECT_PASSWORD = "incorrect_password";

	public static final String GROUP_NOT_FOUND = "group_not_found";

	public static final String INCORRECT_REFERRAL_CODE = "incorrect_referral_Code";

	public static final String ORG_ADDRESS_NOT_FOUND = "org_address_not_found";

	public static final String ORG_NOT_FOUND = "org_not_found";

	public static final String ONBOARDING_STEP_COMPLETED = "onboarding_step_completed";
}
