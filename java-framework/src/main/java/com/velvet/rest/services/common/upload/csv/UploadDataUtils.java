package com.velvet.rest.services.common.upload.csv;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * The Class UploadDataUtils.
 */
public class UploadDataUtils {

	private UploadDataUtils() {
	}

	public static <T> T get(String key, Class<T> clazz) {
		return UploadDataDbReader.get(key, clazz);
	}

	public static String get(String key) {
		return UploadDataDbReader.get(key);
	}

	public static List<Map<String, Object>> getModuleData(String module) {
		return UploadDataDbReader.getModuleData(module);
	}

	public static String save(Long userId, String module, Object data) {
		return UploadDataDbReader.save(userId, module, data);
	}

	public static int updateStatusAndModifiedTime(String key, Integer status) {
		return UploadDataDbReader.updateStatusAndModifiedTime(key, status);
	}

	public static String saveAsync(Long userId, String module, Object data) {
		final String accessKey = UploadDataDbReader.generateRandomKey();
		CompletableFuture.runAsync(() -> save(userId, module, accessKey, data));
		return accessKey;
	}

	public static String save(Long userId, String module, String key, Object data) {
		return UploadDataDbReader.save(userId, module, key, data);
	}

	public static int processed(String key) {
		return UploadDataDbReader.updateStatusAndModifiedTime(key, 1);
	}

	public static int delete(String key) {
		return UploadDataDbReader.delete(key);
	}

	public static boolean isUploadExists(String key) {
		final Integer count = UploadDataDbReader.checkCount(key);
		return count > 0;
	}

	public static boolean isUploadInProcess(String module) {
		final Integer count = UploadDataDbReader.getCount(module);
		return count > 0;
	}

	public static Integer isFirstUploadOfTheDay(String module, Date date) {
		final Integer count = UploadDataDbReader.getCount(module, date);
		return count;
	}

	public static List<Map<String, Object>> getLatestUpload(String module) {
		return UploadDataDbReader.getLatestUploadByModule(module);
	}

}
