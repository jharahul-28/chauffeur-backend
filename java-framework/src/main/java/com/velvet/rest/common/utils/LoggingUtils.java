package com.velvet.rest.common.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import com.velvet.rest.common.beans.RequestLog;

/**
 * <p>
 * LoggingUtils class.
 * </p>
 *
 * @author mohit
 * @version $Id: $Id
 */
public class LoggingUtils {
	private static ThreadLocal<RequestLog> loggerContext = new ThreadLocal<>();

	private LoggingUtils() {}

	/**
	 * <p>
	 * getContext.
	 * </p>
	 *
	 * @return a {@link com.milkbasket.core.framework.common.bean.LoggerContext} object.
	 */
	public static RequestLog getContext() {
		return loggerContext.get();
	}

	/**
	 * <p>
	 * setContext.
	 * </p>
	 *
	 * @param ctx a {@link com.milkbasket.core.framework.common.bean.LoggerContext} object.
	 */
	public static void setContext(RequestLog ctx) {
		loggerContext.set(ctx);
	}

	/**
	 * <p>
	 * clearContext.
	 * </p>
	 */
	public static void clearContext() {
		if (loggerContext != null) {
			loggerContext.remove();
		}
	}

	private static Map<Object, Object> getLogData(Object... objects) {
		final Map<Object, Object> dataMap = new LinkedHashMap<>();
		for (int i = 0; i < objects.length; i = i + 2) {
			dataMap.put(objects[i], objects[i + 1]);
		}
		return dataMap;
	}
}
