package com.velvet.rest.common.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.velvet.rest.services.params.entity.ParamEntity;
import com.velvet.rest.services.params.service.ParamService;

/**
 * <p>
 * ParamsUtils class.
 * </p>
 *
 * @author Mohit
 * @version $Id: $Id
 */
public class ParamsUtils {

	private ParamsUtils() {

	}

	/**
	 * <p>
	 * get all params.
	 * </p>
	 *
	 * @return a {@link java.util.Map} object.
	 */
	public static Map<String, String> getParams() {
		Map<String, String> params = new HashMap<>();
		List<ParamEntity> entityList = getParamService().findAll();
		if (entityList != null && !entityList.isEmpty()) {
			params = entityList.stream().collect(Collectors.toMap(ParamEntity::getParamKey, ParamEntity::getParamValue));
		}
		return params;
	}

	/**
	 * <p>
	 * getParam.
	 * </p>
	 *
	 * @param key a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String getParam(String key) {
		return getParamService().getParam(key, null);
	}

	/**
	 * <p>
	 * getParam.
	 * </p>
	 *
	 * @param key          a {@link java.lang.String} object.
	 * @param defaultValue a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String getParam(String key, String defaultValue) {
		return getParamService().getParam(key, defaultValue);
	}

	/**
	 * <p>
	 * get param as a integer
	 * </p>
	 *
	 * @param key a {@link java.lang.String} object.
	 * @return a {@link java.lang.Integer} object.
	 */
	public static Integer getIntegerParam(String key) {
		return getIntegerParam(key, null);
	}

	/**
	 * <p>
	 * get param as a integer
	 * </p>
	 *
	 * @param key          a {@link java.lang.String} object.
	 * @param defaultValue a {@link java.lang.Integer} object.
	 * @return a {@link java.lang.Integer} object.
	 */
	public static Integer getIntegerParam(String key, Integer defaultValue) {
		String paramValue = getParamService().getParam(key, null);
		return paramValue != null ? Integer.valueOf(paramValue) : defaultValue;

	}

	/**
	 * <p>
	 * get param as a boolean
	 * </p>
	 *
	 * @param key a {@link java.lang.String} object.
	 * @return a boolean.
	 */
	public static boolean getBooleanParam(String key) {
		return getBooleanParam(key, null);
	}

	/**
	 * <p>
	 * get param as a boolean
	 * </p>
	 *
	 * @param key          a {@link java.lang.String} object.
	 * @param defaultValue a {@link java.lang.Boolean} object.
	 * @return a boolean.
	 */
	public static boolean getBooleanParam(String key, Boolean defaultValue) {
		String paramValue = getParamService().getParam(key, null);
		return paramValue != null ? Boolean.valueOf(paramValue) : defaultValue;

	}

	/**
	 * <p>
	 * reload.
	 * </p>
	 *
	 * @return a boolean.
	 */
	private static ParamService getParamService() {
		ParamService paramService = ContextUtils.getContext().getBean(ParamService.class);
		return paramService;
	}

	public static void updateParam(String key, String value) {
		getParamService().updateParamValue(key, value);
	}

}
