package com.velvet.rest.common.utils;

import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.velvet.rest.common.beans.SessionBean;
import com.velvet.rest.common.constants.CommonConstants;

import java.util.Calendar;
import java.util.Date;

/**
 * <p>
 * SessionUtils class.
 * </p>
 *
 * @author Mohit
 * @version $Id: $Id
 */
public class SessionUtils {

	private SessionUtils() {
	}

	private static ThreadLocal<SessionBean> SESSION_THREAD_LOCAL = new ThreadLocal<>();

	/** Constant <code>SESSION_KEY_SEP="######"</code> */
	public static final String SESSION_KEY_SEP = "######";

	private static final String SESSION_KEY_FORMAT = new StringBuilder().append("%s").append(SESSION_KEY_SEP).append("%s").append(SESSION_KEY_SEP).append("%s")
			.append(SESSION_KEY_SEP).append("%s").toString();

	/** Constant <code>UNKNOWN="Unknown"</code> */
	public static final String UNKNOWN = "Unknown";

	public enum ApplicationId {
		ADMIN_APP, CUSTOMER_APP, CHAUFFEUR_APP
	}

	/**
	 * <p>
	 * setSessionBean.
	 * </p>
	 *
	 * @param bean a {@link com.velvet.rest.common.beans.SessionBean} object.
	 */
	public static void setSessionBean(SessionBean bean) {
		SESSION_THREAD_LOCAL.set(bean);
	}

	private static SessionBean getSessionBean() {
		return SESSION_THREAD_LOCAL.get();
	}

	/**
	 * <p>
	 * clearRequest.
	 * </p>
	 */
	public static void clearRequest() {
		SESSION_THREAD_LOCAL.remove();
	}

	/**
	 * <p>
	 * isAuthUserLoggedIn.
	 * </p>
	 *
	 * @return a boolean.
	 */
	public static boolean isAuthUserLoggedIn() {
		return getAuthUser() != null;
	}

	/**
	 * <p>
	 * getAuthUser.
	 * </p>
	 *
	 * @return a {@link com.velvet.rest.common.beans.SessionBean} object.
	 */
	public static SessionBean getAuthUser() {
		return getSessionBean();
	}

	/**
	 * <p>
	 * getAuthUserId.
	 * </p>
	 *
	 * @return a {@link java.lang.Long} object.
	 */
	public static Long getAuthUserId() {
		return getAuthUser() != null ? getAuthUser().getUserId() : 0;
	}

	/**
	 * <p>
	 * getAuthToken.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public static String getAuthToken() {
		return getAuthUser() != null ? getAuthUser().getAuthToken() : "";
	}

	/**
	 * <p>
	 * isCustomer.
	 * </p>
	 *
	 * @return a boolean.
	 */
	public static boolean isCustomer() {
		return getAuthUser() != null ? getAuthUser().getUserType().equals(CommonConstants.UserTypes.CUSTOMER) : false;
	}

	/**
	 * <p>
	 * isAuthUserAdmin.
	 * </p>
	 *
	 * @return a boolean.
	 */
	public static boolean isAdmin() {
		return getAuthUser() != null ? getAuthUser().getUserType().equals(CommonConstants.UserTypes.ADMIN) : false;
	}

	/**
	 * <p>
	 * isChauffeur.
	 * </p>
	 *
	 * @return a boolean.
	 */
	public static boolean isChauffeur() {
		return getAuthUser() != null ? getAuthUser().getUserType().equals(CommonConstants.UserTypes.CHAUFFEUR) : false;
	}

	/**
	 * <p>
	 * createAuthToken.
	 * </p>
	 *
	 * @param groupIdId a {@link java.lang.Long} object.
	 * @param userId    a {@link java.lang.Long} object.
	 * @param appId     a
	 *                  {@link com.velvet.rest.common.utils.SessionUtils.ApplicationId}
	 *                  object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String createAuthToken(@NonNull String userType, @NonNull Long userId, @NonNull ApplicationId appId) {
		Assert.notNull(userType, "UserType Identifier is required");
		Assert.notNull(userId, "User Identifier is required");
		Assert.notNull(appId, "Application Identifier is required");
		return SecurityUtils.encrypt(String.format(SESSION_KEY_FORMAT, userType, userId, System.currentTimeMillis(), appId));
	}

	/**
	 * <p>
	 * getTimeUnitForSessionInMinutes.
	 * </p>
	 *
	 * @param authTokenValidity a {@link java.lang.String} object.
	 * @return a {@link java.lang.Long} object.
	 */
	public static Date getTimeUnitForSessionInMinutes(String authTokenValidity) {
		Date validity = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(validity);
		if (authTokenValidity.endsWith(":MINUTE")) {
			calendar.add(Calendar.MINUTE, Integer.valueOf(authTokenValidity.split(":")[0]));
		} else if (authTokenValidity.endsWith(":HOUR")) {
			calendar.add(Calendar.HOUR_OF_DAY, Integer.valueOf(authTokenValidity.split(":")[0]));
		} else if (authTokenValidity.endsWith(":DAY")) {
			calendar.add(Calendar.DATE, Integer.valueOf(authTokenValidity.split(":")[0]));
		} else if (authTokenValidity.endsWith(":MONTH")) {
			calendar.add(Calendar.MONTH, Integer.valueOf(authTokenValidity.split(":")[0]));
		} else if (authTokenValidity.endsWith(":YEAR")) {
			calendar.add(Calendar.YEAR, Integer.valueOf(authTokenValidity.split(":")[0]));
		}
		return calendar.getTime();
	}

	public static String[] getAuthKeyParts(String authHeader) {
		final String decryptedToken = SecurityUtils.decrypt(authHeader);
		if (!StringUtils.isEmpty(decryptedToken)) {
			// _LOGGER.info(decryptedToken);
			final String[] keyComponents = decryptedToken.split(SessionUtils.SESSION_KEY_SEP);
			if (!StringUtils.isEmpty(keyComponents) && keyComponents.length > 2) {
				return keyComponents;
			}
		}
		return null;
	}
}
