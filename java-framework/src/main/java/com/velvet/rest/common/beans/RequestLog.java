package com.velvet.rest.common.beans;

import java.io.Serializable;

import org.springframework.util.StringUtils;

import com.velvet.rest.common.utils.JSONUtils;

/**
 * <p>
 * RequestLog class.
 * </p>
 *
 * @author mohit
 * @version $Id: $Id
 */
public class RequestLog implements Serializable {

	private static String EXCEPTION_LOG_TAG = "[ ** EXCEPTION IN LOGGING ** ] ";

	/**
	 * 
	 */
	private static final long serialVersionUID = -4189515021206217295L;

	private Long startTime;

	private Long userId;

	private String userType;

	private String url;

	private String ip;

	private String userAgent;

	private String referer;

	private String requestMethod;

	private Long timeTaken;

	private String threadName;

	private String requestId;

	private String source = "WEB";

	/**
	 * <p>
	 * Getter for the field <code>requestMethod</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getRequestMethod() {
		return requestMethod;
	}

	/**
	 * <p>
	 * Setter for the field <code>requestMethod</code>.
	 * </p>
	 *
	 * @param requestMethod a {@link java.lang.String} object.
	 */
	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	/**
	 * <p>
	 * Getter for the field <code>url</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * <p>
	 * Setter for the field <code>url</code>.
	 * </p>
	 *
	 * @param url a {@link java.lang.String} object.
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * <p>
	 * Getter for the field <code>userId</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.Long} object.
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * <p>
	 * Setter for the field <code>userId</code>.
	 * </p>
	 *
	 * @param userId a {@link java.lang.Long} object.
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * <p>
	 * Getter for the field <code>ip</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * <p>
	 * Setter for the field <code>ip</code>.
	 * </p>
	 *
	 * @param ip a {@link java.lang.String} object.
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * <p>
	 * Getter for the field <code>userAgent</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getUserAgent() {
		return userAgent;
	}

	/**
	 * <p>
	 * Setter for the field <code>userAgent</code>.
	 * </p>
	 *
	 * @param userAgent a {@link java.lang.String} object.
	 */
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	/**
	 * <p>
	 * Getter for the field <code>referer</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getReferer() {
		return StringUtils.isEmpty(referer) ? "" : referer;
	}

	/**
	 * <p>
	 * Setter for the field <code>referer</code>.
	 * </p>
	 *
	 * @param referer a {@link java.lang.String} object.
	 */
	public void setReferer(String referer) {
		this.referer = referer;
	}

	/**
	 * <p>
	 * setRequestInfo.
	 * </p>
	 *
	 * @param requestInfo a {@link com.milkbasket.core.framework.common.bean.log.RequestLog} object.
	 */
	public void setRequestInfo(RequestLog requestInfo) {
		this.setIp(requestInfo.getIp());
		this.setReferer(requestInfo.getReferer());
		this.setUrl(requestInfo.getUrl());
		this.setUserAgent(requestInfo.getUserAgent());
		this.setUserId(requestInfo.getUserId());
		this.setRequestMethod(requestInfo.getRequestMethod());
		this.setThreadName(requestInfo.getThreadName());
		this.setRequestId(requestInfo.getRequestId());
	}

	/**
	 * <p>
	 * Getter for the field <code>timeTaken</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.Long} object.
	 */
	public Long getTimeTaken() {
		return timeTaken;
	}

	/**
	 * <p>
	 * Setter for the field <code>timeTaken</code>.
	 * </p>
	 *
	 * @param timeTaken a {@link java.lang.Long} object.
	 */
	public void setTimeTaken(Long timeTaken) {
		this.timeTaken = timeTaken;
	}

	/**
	 * <p>
	 * Getter for the field <code>threadName</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getThreadName() {
		return threadName;
	}

	/**
	 * <p>
	 * Setter for the field <code>threadName</code>.
	 * </p>
	 *
	 * @param threadName a {@link java.lang.String} object.
	 */
	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	/**
	 * <p>
	 * Getter for the field <code>requestId</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getRequestId() {
		return requestId;
	}

	/**
	 * <p>
	 * Setter for the field <code>requestId</code>.
	 * </p>
	 *
	 * @param requestId a {@link java.lang.String} object.
	 */
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	/**
	 * <p>
	 * Getter for the field <code>source</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getSource() {
		return StringUtils.isEmpty(source) ? "WEB" : source;
	}

	/**
	 * <p>
	 * Setter for the field <code>source</code>.
	 * </p>
	 *
	 * @param source a {@link java.lang.String} object.
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		try {
			return JSONUtils.objectToJson(this);
		} catch (Exception e) {
			return EXCEPTION_LOG_TAG + e.getMessage();
		}
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

}
