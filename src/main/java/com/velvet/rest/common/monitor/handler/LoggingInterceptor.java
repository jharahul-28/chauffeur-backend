package com.velvet.rest.common.monitor.handler;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import com.velvet.rest.common.beans.RequestLog;
import com.velvet.rest.common.constants.CommonConstants;
import com.velvet.rest.common.logging.AppLogger;
import com.velvet.rest.common.logging.LoggingManager;
import com.velvet.rest.common.utils.LoggingUtils;
import com.velvet.rest.common.utils.SessionUtils;

/**
 * The Class PerformanceInterceptor.
 *
 * @author Mohit
 * @version $Id: $Id
 */
public class LoggingInterceptor implements HandlerInterceptor {

	private static final AppLogger _LOGGER = LoggingManager.getLogger(LoggingInterceptor.class);

	/** {@inheritDoc} */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		final String url = request.getRequestURI();
		if (url.equals("/") && url.contains("swagger")) {
			return false;
		}
		String appVersion = request.getHeader("app-version");
		String platform = request.getHeader("platform");
		if (StringUtils.isEmpty(appVersion)) {
			appVersion = "NOT_SPECIFIED";
		}
		if (StringUtils.isEmpty(platform)) {
			platform = "NOT_SPECIFIED";
		}
		prepareLoggerContext(getClientIpAddr(request), request.getMethod(), request.getRequestURL().toString(), getUserAgent(request), getReferer(request),
				getRequestId(request), appVersion, platform, url);

		return true;
	}

	/**
	 * <p>
	 * prepareLoggerContext.
	 * </p>
	 *
	 * @param ip        a {@link java.lang.String} object.
	 * @param method    a {@link java.lang.String} object.
	 * @param url       a {@link java.lang.String} object.
	 * @param userAgent a {@link java.lang.String} object.
	 * @param referer   a {@link java.lang.String} object.
	 * @param requestId a {@link java.lang.String} object.
	 * @param platform
	 * @param uri 
	 * @param logGroup  a
	 *                  {@link com.milkbasket.core.framework.common.bean.log.LogGroup}
	 *                  object.
	 */
	private void prepareLoggerContext(String ip, String method, String url, String userAgent, String referer, String requestId, String appVersion,
			String platform, String uri) {
		final RequestLog requestLog = new RequestLog();

		requestLog.setStartTime(System.currentTimeMillis());
		requestLog.setIp(ip);
		requestLog.setRequestMethod(method);
		requestLog.setUrl(url);
		requestLog.setUserAgent(userAgent);
		requestLog.setUserId(SessionUtils.getAuthUserId());
		requestLog.setReferer(referer);
		requestLog.setThreadName(Thread.currentThread().getName());
		requestLog.setRequestId(requestId);
		if (SessionUtils.getAuthUser() != null)
			requestLog.setUserType(SessionUtils.getAuthUser().getUserType());

		LoggingUtils.setContext(requestLog);

		if (!isSystemEndpoint(uri)) {
			final StringBuilder sb = new StringBuilder(method).append(":").append(url).append(" | app-version:").append(appVersion).append(" | platform:")
					.append(platform).append(" | userAgent:").append(userAgent).append(" | referer:").append(referer).append(" | requestId:").append(requestId)
					.append(" | ip:").append(ip);
			_LOGGER.info(sb);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		final long millis = System.currentTimeMillis();

		final StringBuilder sb = new StringBuilder(request.getRequestURI()).append(" Handler Processing Time : ")
				.append(millis - LoggingUtils.getContext().getStartTime());
		LoggingUtils.clearContext();

		if (!isSystemEndpoint(request.getRequestURI())) {
			_LOGGER.info(sb.toString());
		}
	}

	private boolean isSystemEndpoint(String url) {
		return url.equalsIgnoreCase("/swag/apps") || "/status".equalsIgnoreCase(url);
	}

	private static String getClientIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_FORWARDED");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_VIA");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("REMOTE_ADDR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}

		try {
			if (!"/status".equalsIgnoreCase(request.getRequestURI())) {
				_LOGGER.debug("Caller IP : " + ip);
			}
			if (!StringUtils.isEmpty(ip) && ip.contains(",")) {
				ip = ip.split(",")[0];
			}
		} catch (Exception e) {
			_LOGGER.warn("Error in obtaining client ip address." + e.getMessage());
		}

		if (!StringUtils.isEmpty(ip) && ip.equalsIgnoreCase("0:0:0:0:0:0:0:1")) {
			ip = "127.0.0.1";
		}
		return ip;
	}

	private String getReferer(HttpServletRequest request) {
		String referer = request.getHeader("referer");
		if (StringUtils.isEmpty(referer)) {
			referer = request.getHeader("Referer");
		}
		return referer;
	}

	private String getUserAgent(HttpServletRequest request) {
		String userAgent = request.getHeader("user-agent");
		if (StringUtils.isEmpty(userAgent)) {
			userAgent = request.getHeader("User-Agent");
		}
		return userAgent;
	}

	private String getRequestId(HttpServletRequest request) {
		if (StringUtils.isEmpty(request.getHeader(CommonConstants.X_REQUEST_ID))) {
			return UUID.randomUUID().toString();
		}
		return request.getHeader(CommonConstants.X_REQUEST_ID);
	}
}
