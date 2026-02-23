package com.velvet.rest.common.monitor.handler;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.velvet.rest.common.beans.ErrorBean;
import com.velvet.rest.common.beans.SessionBean;
import com.velvet.rest.common.constants.CommonConstants;
import com.velvet.rest.common.eligibility.EligibilityUtils;
import com.velvet.rest.common.exceptions.AuthenticationException;
import com.velvet.rest.common.exceptions.PrivilegeException;
import com.velvet.rest.common.logging.AppLogger;
import com.velvet.rest.common.logging.LoggingManager;
import com.velvet.rest.common.properties.Errors;
import com.velvet.rest.common.state.ApplicationState;
import com.velvet.rest.common.utils.SessionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

/**
 * The Class SessionInterceptor.
 *
 * @author Mohit
 * @version $Id: $Id
 */
public class SessionInterceptor implements HandlerInterceptor {

	private static final AppLogger _LOGGER = LoggingManager.getLogger(SessionInterceptor.class);

	/** {@inheritDoc} */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		final long millis = System.currentTimeMillis();

		final String method = request.getMethod();
		final String url = request.getRequestURI();
		if (url.equalsIgnoreCase("/")) {
			return false;
		}

		int appControlFlag = ApplicationState.getAppControlFlag();
		if (appControlFlag == 1 && !isSystemEndpoint(url)) {
			throw new AuthenticationException("Application is under maintenance. Please try again later.", ErrorBean.withError(Errors.AUTHORIZATION_EXCEPTION));
		} else if (appControlFlag == 2 && !method.equalsIgnoreCase("GET") && !isSystemEndpoint(url)) {
			throw new AuthenticationException("Application is in read-only mode. Only read operations are allowed.",
					ErrorBean.withError(Errors.AUTHORIZATION_EXCEPTION));
		}
		// Increment request counter for monitoring
		ApplicationState.incrementRequestCounter();
		if (isSystemEndpoint(url)) {
			return true;
		}
		// Log headers for non-system endpoints
		Collections.list(request.getHeaderNames()).forEach(header -> {
			_LOGGER.debug(header + " : " + request.getHeader(header));
		});

		/**
		 * Allow Options Call
		 */
		if (isOptions(method, url)) {
			return true;
		}

		logCallInfo(method, url);

		/**
		 * User Authorization Token
		 */
		String authHeader = request.getHeader(CommonConstants.AUTH_HEADER);
		boolean isBypass = isBypassCall(method, url, handler);
		if (isBypass && StringUtils.isEmpty(authHeader)) {
			return true;
		} else if (!isBypass && StringUtils.isEmpty(authHeader)) {
			_LOGGER.warn("Auth Header Found Empty");
			throw new AuthenticationException("User Session Required. Please login to continue.", ErrorBean.withError(Errors.AUTHORIZATION_EXCEPTION));
		} else {
			try {
				final SessionBean sessionBean = getSessionBean(authHeader, method, url, handler, !isBypass);

				if (sessionBean != null) {
					SessionUtils.setSessionBean(sessionBean);

					_LOGGER.debug("*********************************************************************");
					_LOGGER.info(String.format("User Check Success - UserType = %s and User ID = %s Session Interceptor Time = %s ", sessionBean.getUserType(),
							sessionBean.getUserId(), System.currentTimeMillis() - millis));
					_LOGGER.debug("*********************************************************************");
				} else if (!isBypass) {
					_LOGGER.warn("User Session Not Found");
					throw new AuthenticationException("User Session Expired. Please login to continue.", Errors.error(Errors.AUTHORIZATION_EXCEPTION));
				}
			} catch (final Exception e) {
				_LOGGER.error(e.getMessage(), e);
				throw e;
			}
		}
		return true;
	}

	private boolean swaggerEndpoints(String url) {
		return url.contains("swagger");
	}

	private boolean isSystemEndpoint(String url) {
		return url.equalsIgnoreCase("/status") || url.equalsIgnoreCase("/swag/apps") || url.equalsIgnoreCase("/error") || swaggerEndpoints(url);
	}

	private boolean isBypassCall(String method, String uri, Object handler) {
		if (EligibilityUtils.isBypassSessionHandler(method, handler)) {
			if (!"/status".equalsIgnoreCase(uri)) {
				_LOGGER.debug(String.format("By Pass Call having method =%s and uri =%s", method, uri));
			}
			return true;
		}
		return false;
	}

	private SessionBean getSessionBean(String authHeader, String method, String uri, Object handler, boolean ischeckingEligibility) {
		final String[] keyComponents = SessionUtils.getAuthKeyParts(authHeader);
		SessionBean sessionBean = null;
		if (keyComponents != null && keyComponents.length >= 2) {
			String userType = keyComponents[0];
			Long userId = Long.valueOf(keyComponents[1]);
			boolean isAuthKeyValid = EligibilityUtils.isAuthKeyValid(authHeader, userId);
			if (isAuthKeyValid) {
				if (ischeckingEligibility) {
					// check Eligibility
					isEligible(method, uri, handler, userId);
				}
				sessionBean = new SessionBean();
				sessionBean.setUserType(userType);
				sessionBean.setUserId(userId);
				sessionBean.setAuthToken(authHeader);
			}
		}
		return sessionBean;
	}

	private void isEligible(String method, String uri, Object handler, Long userId) {
		final boolean isPrivileged = EligibilityUtils.isEligibleHandler(method.toUpperCase(), uri.toLowerCase(), handler, userId);
		if (!isPrivileged) {
			_LOGGER.error("Unauthorized Access : " + userId);
			throw new PrivilegeException("You do not have sufficient privileges. Please contact system administrator.",
					ErrorBean.withError(Errors.PRIVILEGE_EXCEPTION));
		}
	}

	/** {@inheritDoc} */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
		if (!StringUtils.isEmpty(SessionUtils.getAuthToken())) {
			response.setHeader(CommonConstants.AUTH_HEADER, SessionUtils.getAuthToken());
		}

		String theadName = Thread.currentThread().getName();
		if (theadName.contains(":::")) {
			theadName = theadName.split(":::")[0];
			Thread.currentThread().setName(theadName);
		}

		SessionUtils.clearRequest();
	}

	private boolean isOptions(String method, String url) {
		if (method.equalsIgnoreCase(RequestMethod.OPTIONS.toString())) {
			if (!"/status".equalsIgnoreCase(url)) {
				_LOGGER.info("By Pass Call");
			}
			return true;
		}
		return false;
	}

	private void logCallInfo(String method, String url) {
		/**
		 * Disable Log for Status Calls
		 */
		if (!"/status".equalsIgnoreCase(url)) {
			_LOGGER.debug("Method Called : " + method);
			_LOGGER.info(method + "::" + url);
		}
	}

}
