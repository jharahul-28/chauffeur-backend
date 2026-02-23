package com.velvet.rest.services.common.upload.csv;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.velvet.rest.common.beans.ErrorBean;
import com.velvet.rest.common.exceptions.ValidationException;
import com.velvet.rest.common.logging.AppLogger;
import com.velvet.rest.common.logging.LoggingManager;
import com.velvet.rest.common.utils.CollectionUtils;
import com.velvet.rest.common.utils.ParamsUtils;

/**
 * <p>
 * CsvUtils class.
 * </p>
 *
 * @author nitin
 * @version $Id: $Id
 */
public class CsvUtils {

	/** The Constant _LOGGER. */
	private static final AppLogger _LOGGER = LoggingManager.getLogger(CsvUtils.class);

	/**
	 * Instantiates a new csv utils.
	 */
	private CsvUtils() {

	}

	/**
	 * Gets the beans.
	 *
	 * @param <T>            the generic type
	 * @param moduleName     the module name
	 * @param file           the file
	 * @param maxAllowedRows the max allowed rows
	 * @param csvBean        the csv bean
	 * @return the beans
	 */
	public static <T extends CSVMapping> List<T> getBeans(String moduleName, MultipartFile file, Integer maxAllowedRows, T csvBean) {

		final List<ErrorBean> errors = new ArrayList<>();
		try {

			if (file == null || file.getSize() == 0) {
				_LOGGER.error(String.format("File Received for %s module is having no data.", moduleName));
				errors.add(ErrorBean.withError("CSV_FILE_ERROR", "File size is zero or no data found in file", "file"));
			} else {

				final String allowedExtn = ParamsUtils.getParam("BULK_UPLOAD_ALLOWED_FILE_EXTENSIONS", "csv");

				final String originalFileName = file.getOriginalFilename();
				final String extension = getFileExt(file.getOriginalFilename());

				_LOGGER.info(String.format("File Name = %s and File Extension = %s", originalFileName, extension));

				if (StringUtils.isEmpty(allowedExtn) || !allowedExtn.contains(extension.toLowerCase())) {
					errors.add(ErrorBean.withError("CSV_FILE_ERROR", String.format("Only [%s] files allowed for upload.", allowedExtn), "file"));
				} else if (UploadDataUtils.isUploadInProcess(moduleName)) {
					errors.add(ErrorBean.withError("CSV_UPLOAD_IN_PROCESS", String.format("Upload for '%s' is already in process.", moduleName), "file"));
				} else {
					final byte[] fileContents = file.getBytes();
					return getBeans(fileContents, maxAllowedRows, csvBean);
				}
			}
		} catch (final ValidationException ve) {
			_LOGGER.error(String.format("Validation Error is Processing File Received for %s module. Error is %s.", moduleName, ve.getMessage()));
			throw ve;
		} catch (final Exception e) {
			_LOGGER.error(String.format("Exception is Processing File Received for %s module. Error is %s.", moduleName, e.getMessage()), e);
			errors.add(ErrorBean.withError("CSV_FILE_PARSING_ERROR", "Error in Parsing CSV File", null));
		}
		if (CollectionUtils.isNotEmpty(errors)) {
			_LOGGER.error(String.format("Validation Error is Processing File Received for %s module. Error is %s.", moduleName, errors.toString()));
			throw new ValidationException("CSV_FILE_PARSING_ERROR", errors);
		}
		return new ArrayList<>();
	}

	private static String getFileExt(String fname) {
		String ext = "";
		int i = fname.lastIndexOf('#');
		if (i > 0) {
			fname = fname.substring(0, i - 1);
		}
		i = fname.lastIndexOf('.');
		// REMIND: OS specific delimters appear here
		i = Math.max(i, fname.lastIndexOf('/'));
		i = Math.max(i, fname.lastIndexOf('?'));
		if (i != -1 && fname.charAt(i) == '.') {
			ext = fname.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	/**
	 * Gets the beans.
	 *
	 * @param <T>            the generic type
	 * @param bytes          the bytes
	 * @param maxAllowedRows the max allowed rows
	 * @param clazz          the clazz
	 * @return the beans
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private static <T extends CSVMapping> List<T> getBeans(byte[] bytes, Integer maxAllowedRows, T clazz) throws IOException {

		final long startTime = System.nanoTime();

		final Reader reader = new StringReader(new String(bytes));
		final CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

		final List<CSVRecord> records = csvParser.getRecords();

		final int recCount = records.size();

		if (recCount > maxAllowedRows) {
			csvParser.close();
			throw new ValidationException(ErrorBean.withError("MAX_ROW_UPLOAD_ERROR",
					String.format("Uploaded sheet is having %s row. Max allowed rows is %s", recCount, maxAllowedRows)));
		} else if (recCount == 0) {
			csvParser.close();
			throw new ValidationException(ErrorBean.withError("CSV_DATA_ERROR", "No rows were found in the uploaded csv", "file"));
		}
		final Map<String, String> fieldMapping = clazz.getHeaderFieldMapping();
		final Map<String, Field> fieldMap = getBeanFields(clazz.getClass());

		final List<T> data = records.stream().map(record -> parseBean(fieldMapping, fieldMap, clazz, record)).collect(Collectors.toList());
		csvParser.close();

		final Long timeDiff = System.nanoTime() - startTime;
		final Long diffInMs = TimeUnit.NANOSECONDS.toMillis(timeDiff);
		_LOGGER.info(String.format("Total Parsed Row = %s. Total Parsing Time =%s (ms)", data.size(), diffInMs));

		return data;
	}

	/**
	 * Parses the bean.
	 *
	 * @param <T>          the generic type
	 * @param fieldMapping the field mapping
	 * @param fieldMap     the field map
	 * @param clazz        the clazz
	 * @param record       the record
	 * @return the t
	 */
	private static <T extends CSVMapping> T parseBean(final Map<String, String> fieldMapping, final Map<String, Field> fieldMap, T clazz, CSVRecord record) {
		final T newMapping = clazz.newBean();
		final Map<String, String> dataMap = new LinkedHashMap<>();
		fieldMapping.entrySet().stream().forEach(e -> {
			final String column = e.getKey().toLowerCase();
			final String field = e.getValue();
			String fieldValue = "";
			if (record.isSet(column)) {
				fieldValue = record.get(column);
			}
			dataMap.put(field, fieldValue);
		});
		newMapping.fill(dataMap, fieldMap);
		return newMapping;
	}

	/**
	 * Gets the bean fields.
	 *
	 * @param clazz the clazz
	 * @return the bean fields
	 */
	private static Map<String, Field> getBeanFields(Class<?> clazz) {
		final Map<String, Field> fieldMap = new HashMap<>();
		fillFields(fieldMap, Arrays.asList(clazz.getSuperclass().getDeclaredFields()));
		fillFields(fieldMap, Arrays.asList(clazz.getDeclaredFields()));
		return fieldMap;
	}

	/**
	 * Fill fields.
	 *
	 * @param fieldMap  the field map
	 * @param fieldList the field list
	 */
	private static void fillFields(final Map<String, Field> fieldMap, List<Field> fieldList) {
		fieldList.stream().forEach(field -> {
			try {
				field.setAccessible(true);
				fieldMap.put(field.getName(), field);
			} catch (final Exception e) {
				// do nothing
			}
		});
	}

	/**
	 * Save bulk request data.
	 *
	 * @param <T>        the generic type
	 * @param authUserId the auth user id
	 * @param module     the upload module
	 * @param result     the result
	 */
	public static <T extends CSVMapping> void saveBulkRequestData(Long authUserId, String module, CsvUploadResult<T> result) {
		if (result.getSuccess() > 0 && result.getFailed() == 0) {
			_LOGGER.info("Going to save uploaded objects");
			final CSVBulkRequest<T> bulkRequest = new CSVBulkRequest<>();
			bulkRequest.setData(result.getSuccessRows());
			final String key = UploadDataUtils.save(authUserId, module, bulkRequest);
			if (!StringUtils.isEmpty(key)) {
				result.setKey(key);
			}
			_LOGGER.info("Key = " + key);
		}
	}

	/**
	 * Gets the bulk request data.
	 *
	 * @param <T>          the generic type
	 * @param key          the key
	 * @param contentClass the content class
	 * @return the bulk request data
	 */
	public static <T extends CSVMapping> CSVBulkRequest<T> getBulkRequestData(String key, Class<T> contentClass) {
		try {
			final String data = UploadDataUtils.get(key);
			if (!StringUtils.isEmpty(data)) {
				return getRequestData(data, contentClass);
			}
		} catch (final Exception e) {
			// do nothing
		}
		return null;
	}

	public static <T extends CSVMapping> CSVBulkRequest<T> getRequestData(final String data, Class<T> contentClass)
			throws IOException, JsonParseException, JsonMappingException {
		final InputStream json = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
		final ObjectMapper mapper = new ObjectMapper();
		final JavaType type = mapper.getTypeFactory().constructParametricType(CSVBulkRequest.class, contentClass);
		return mapper.readValue(json, type);
	}

	public static int cancelUpload(String key) {
		return UploadDataUtils.delete(key);
	}

	public static int markUploadProcessed(String key) {
		return UploadDataUtils.processed(key);
	}

}
