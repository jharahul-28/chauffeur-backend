package com.velvet.rest.services.common.upload.csv;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.velvet.rest.common.logging.AppLogger;
import com.velvet.rest.common.logging.LoggingManager;
import com.velvet.rest.common.utils.CollectionUtils;
import com.velvet.rest.common.utils.ContextUtils;
import com.velvet.rest.common.utils.DateUtils;
import com.velvet.rest.common.utils.JSONUtils;
import com.velvet.rest.common.utils.ParamsUtils;

/**
 * The Class UploadDataDbReader.
 */
public class UploadDataDbReader {

	private static JdbcTemplate getJdbcTemplate() {
		JdbcTemplate jdbcTemplate = ContextUtils.getContext().getBean(JdbcTemplate.class);
		return jdbcTemplate;
	}

	/** The Constant _LOGGER. */
	private static final AppLogger _LOGGER = LoggingManager.getLogger(UploadDataDbReader.class);

	private static final int EXPIRY_MIN = ParamsUtils.getIntegerParam("UPLOAD_EXPIRY_MIN", -15);

	private static final int EXPIRY_MIN_ALL = EXPIRY_MIN - 1;

	private static final String INS_QUERY = "INSERT INTO bulk_upload(access_key, module, json_data, created_on, created_by, modified_on, modified_by) VALUES (?, ?, ?, ?, ?, ?, ?)";

	private static final String SEL_QUERY = "SELECT json_data FROM bulk_upload WHERE access_key = ? AND modified_on >=? AND status is null";

	private static final String SEL_ON_MODULE = "SELECT * FROM bulk_upload WHERE module = ? AND status is null";

	private static final String UPD_QUERY = "UPDATE bulk_upload set modified_on =? where  access_key =?";

	private static final String UPD_WITH_STATUS_QUERY = "UPDATE bulk_upload SET modified_on =?, status=? WHERE  access_key =?";

	private static final String CHECK_QUERY = "SELECT count(id) FROM bulk_upload WHERE access_key =? AND status is null";

	private static final String SEL_COUNT_QUERY = "SELECT count(id) FROM bulk_upload WHERE module =? AND modified_on >=? AND status is null";

	private static final String SEL_COUNT_QUERY_FOR_DAY = "SELECT count(id) FROM bulk_upload WHERE module =? AND date(created_on) >=? AND status != 0";

	private static final String DEL_EXPIRED_QUERY = "UPDATE bulk_upload SET status =0 WHERE modified_on <=? AND status is null";

	private static final String SELECT_ON_LATEST_MODULE = "SELECT *  FROM bulk_upload WHERE module = ? AND status is null order by id desc limit 1";

	public static String generateRandomKey() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	/**
	 * Instantiates a new master data db reader.
	 */
	private UploadDataDbReader() {
	}

	public static String save(Long userId, final String module, final String key, Object data) {
		deleteExpired();
		if (data != null) {
			try {
				final String json = JSONUtils.objectToJson(data);
				if (!StringUtils.isEmpty(json)) {
					// _LOGGER.info("JSON = " + json);
					final JdbcTemplate template = getJdbcTemplate();
					final Date currentDate = new Date();
					final String accessKey = StringUtils.isEmpty(key) ? generateRandomKey() : key;
					template.update(INS_QUERY, accessKey, module, json, currentDate, userId, currentDate, userId);
					_LOGGER.info(String.format("Bulkupload Module =%s - Returning Key = %s", module, accessKey));
					return accessKey;
				}

			} catch (final Exception e) {
				_LOGGER.error(e.getMessage(), e);
				return null;
			}
		}
		return null;
	}

	public static String save(Long userId, final String module, Object data) {
		return save(userId, module, null, data);
	}

	private static void updateModifiedTime(String key) {
		try {
			if (!StringUtils.isEmpty(key)) {
				final JdbcTemplate template = getJdbcTemplate();
				template.update(UPD_QUERY, new Date(), key);
			}
		} catch (final Exception e) {
			_LOGGER.error(e.getMessage(), e);
		}
	}

	public static int updateStatusAndModifiedTime(String key, Integer status) {
		try {
			if (!StringUtils.isEmpty(key)) {
				final JdbcTemplate template = getJdbcTemplate();
				return template.update(UPD_WITH_STATUS_QUERY, new Date(), status, key);
			}
		} catch (final Exception e) {
			_LOGGER.error(e.getMessage(), e);
		}
		return -1;
	}

	public static int delete(String key) {
		return updateStatusAndModifiedTime(key, 0);
	}

	private static void deleteExpired() {
		try {
			final JdbcTemplate template = getJdbcTemplate();
			final Date modifiedOn = DateUtils.addMinutes(new Date(), EXPIRY_MIN_ALL);
			template.update(DEL_EXPIRED_QUERY, modifiedOn);
		} catch (final Exception e) {
			_LOGGER.error(e.getMessage(), e);
		}
	}

	public static Integer checkCount(String key) {
		try {
			final JdbcTemplate template = getJdbcTemplate();
			final Object[] args = new Object[] { key };
			return template.queryForObject(CHECK_QUERY, args, Integer.class);
		} catch (final Exception e) {
			return 0;
		}
	}

	public static Integer getCount(String module) {
		try {
			final JdbcTemplate template = getJdbcTemplate();
			final Date modifiedOn = DateUtils.addMinutes(new Date(), EXPIRY_MIN);
			final Object[] args = new Object[] { module, modifiedOn };
			return template.queryForObject(SEL_COUNT_QUERY, args, Integer.class);
		} catch (final Exception e) {
			return 0;
		}
	}

	public static Integer getCount(String module, java.sql.Date date) {
		try {
			final JdbcTemplate template = getJdbcTemplate();
			final Object[] args = new Object[] { module, date };
			return template.queryForObject(SEL_COUNT_QUERY_FOR_DAY, args, Integer.class);
		} catch (final Exception e) {
			return 0;
		}
	}

	public static String get(String key) {
		try {
			return getJSONData(key);
		} catch (final Exception e) {
			return null;
		}
	}

	public static <T> T get(String key, Class<T> clazz) {
		try {
			final String data = getJSONData(key);
			if (!StringUtils.isEmpty(data)) {
				return JSONUtils.streamToObject(new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8)), clazz);
			}
		} catch (final Exception e) {
			_LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	public static List<Map<String, Object>> getModuleData(String module) {
		final JdbcTemplate template = getJdbcTemplate();
		final Object[] args = new Object[] { module };
		final List<Map<String, Object>> dataList = template.queryForList(SEL_ON_MODULE, args);
		if (CollectionUtils.isNotEmpty(dataList)) {
			return dataList;
		} else {
			return new ArrayList<>();
		}
	}

	private static String getJSONData(String key) {
		updateModifiedTime(key);
		final JdbcTemplate template = getJdbcTemplate();
		final Date createdOn = DateUtils.addMinutes(new Date(), EXPIRY_MIN);
		final Object[] args = new Object[] { key, createdOn };
		final String data = template.queryForObject(SEL_QUERY, args, String.class);
		return data;
	}

	public static List<Map<String, Object>> getLatestUploadByModule(String module) {
		final JdbcTemplate template = getJdbcTemplate();
		final Object[] args = new Object[] { module };
		final List<Map<String, Object>> dataList = template.queryForList(SELECT_ON_LATEST_MODULE, args);
		if (CollectionUtils.isNotEmpty(dataList)) {
			return dataList;
		} else {
			return new ArrayList<>();
		}
	}

}
