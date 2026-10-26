package com.velvet.rest.common.utils;

import java.io.InputStream;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.velvet.rest.common.logging.AppLogger;
import com.velvet.rest.common.logging.LoggingManager;

/**
 * <p>
 * JSONUtils class.
 * </p>
 *
 * @author Mohit
 * @version $Id: $Id
 */
public class JSONUtils {

	private static final AppLogger _LOGGER = LoggingManager.getLogger(JSONUtils.class);
	private static final String LOG_TAG = "[ OBJECT MAPPING ERROR ] ";

	/**
	 * <p>
	 * jsonToObject.
	 * </p>
	 *
	 * @param payload a {@link java.lang.String} object.
	 * @param clazz   a {@link java.lang.Class} object.
	 * @param         <T> a T object.
	 * @return a T object.
	 */
	public static <T> T jsonToObject(String payload, Class<T> clazz) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			return mapper.readValue(payload, clazz);
		} catch (Exception e) {
			_LOGGER.error(LOG_TAG + e.getMessage());
			return null;
		}
	}

	/**
	 * <p>
	 * streamToObject.
	 * </p>
	 *
	 * @param stream a {@link java.io.InputStream} object.
	 * @param clazz  a {@link java.lang.Class} object.
	 * @param        <T> a T object.
	 * @return a T object.
	 */
	public static <T> T streamToObject(InputStream stream, Class<T> clazz) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			return mapper.readValue(stream, clazz);
		} catch (Exception e) {
			_LOGGER.error(LOG_TAG + e.getMessage());
			return null;
		}
	}

	/**
	 * 
	 * @param object
	 * @param clazz
	 * @return a T object
	 */
	public static <T> T objectToClass(Object object, Class<T> clazz) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			return mapper.convertValue(object, clazz);
		} catch (Exception e) {
			_LOGGER.error(LOG_TAG + e.getMessage());
			return null;
		}
	}

	/**
	 * <p>
	 * objectToJson.
	 * </p>
	 *
	 * @param object a {@link java.lang.Object} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String objectToJson(Object object) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			String value = mapper.writeValueAsString(object);
			return value;
		} catch (Exception e) {
			_LOGGER.error(LOG_TAG + e.getMessage());
			return null;
		}
	}

}
