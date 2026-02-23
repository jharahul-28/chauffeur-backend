package com.velvet.rest.common.properties;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.velvet.rest.common.beans.ErrorBean;
import com.velvet.rest.common.utils.ObjectUtils;

/**
 * The Class Errors.
 *
 * @author Mohit
 * @version $Id: $Id
 */
public class Errors {

	/** The Constant AUTHORIZATION_EXCEPTION. */
	public static final String AUTHORIZATION_EXCEPTION = "AUTHORIZATION_EXCEPTION";

	/** The Constant AUTHENTICATION_EXCEPTION. */
	public static final String AUTHENTICATION_EXCEPTION = "AUTHENTICATION_EXCEPTION";

	/** Constant <code>PRIVILEGE_EXCEPTION="PRIVILEGE_EXCEPTION"</code> */
	public static final String PRIVILEGE_EXCEPTION = "PRIVILEGE_EXCEPTION";

	/** The Constant SERVER_EXCEPTION. */
	public static final String SERVER_EXCEPTION = "SERVER_EXCEPTION";

	/** The Constant AUTOCOMPLETE_EXCEPTION. */
	public static final String AUTOCOMPLETE_EXCEPTION = "AUTOCOMPLETE_EXCEPTION";

	/** The Constant FORBIDDEN_OPERATION. */
	public static final String FORBIDDEN_OPERATION = "FORBIDDEN_OPERATION";

	/** The Constant UNSUPPORTED_DB_OPERATION. */
	public static final String UNSUPPORTED_DB_OPERATION = "UNSUPPORTED_DB_OPERATION";

	/** The Constant SAVE_FAILED. */
	public static final String SAVE_FAILED = "SAVE_FAILED";

	/** The Constant UPDATE_FAILED. */
	public static final String UPDATE_FAILED = "UPDATE_FAILED";

	/** The Constant DELETE_FAILED. */
	public static final String DELETE_FAILED = "DELETE_FAILED";

	/** The Constant NO_DATA_FOUND. */
	public static final String NO_DATA_FOUND = "NO_DATA_FOUND";

	/** The Constant NULL_REQUEST_PARAMS. */
	public static final String NULL_REQUEST_PARAMS = "NULL_REQUEST_PARAMS";

	/** The Constant ILLEGAL_PARAM. */
	public static final String ILLEGAL_PARAM = "ILLEGAL_PARAM";

	/** The Constant INVALID_REQUEST_PAYLOAD. */
	public static final String INVALID_REQUEST_PAYLOAD = "INVALID_REQUEST_PAYLOAD";

	public static final String INVALID_STORE_RETURNS_UPLOAD = "INVALID_STORE_RETURNS_UPLOAD";

	/** The Constant INVALID_REQUEST. */
	public static final String INVALID_REQUEST = "INVALID_REQUEST";

	/** The Constant JDBC_CONNECTION_ERROR. */
	public static final String JDBC_CONNECTION_ERROR = "JDBC_CONNECTION_ERROR";

	/** The Constant JDBC_GRAMMER_ERROR. */
	public static final String JDBC_GRAMMER_ERROR = "JDBC_GRAMMER_ERROR";

	/** Constant <code>JDBC_DATA_ACCESS_ERROR="JDBC_DATA_ACCESS_ERROR"</code> */
	public static final String JDBC_DATA_ACCESS_ERROR = "JDBC_DATA_ACCESS_ERROR";

	/** The Constant JDBC_CONSTRAINT_ERROR. */
	public static final String JDBC_CONSTRAINT_ERROR = "JDBC_CONSTRAINT_ERROR";

	/** The Constant INVALID_NUMBER. */
	public static final String INVALID_NUMBER = "INVALID_NUMBER";

	/** The Constant HANDLER_NOT_FOUND. */
	public static final String HANDLER_NOT_FOUND = "HANDLER_NOT_FOUND";

	/** The Constant UNIQUE_VALUE. */
	public static final String UNIQUE_VALUE = "UNIQUE_VALUE";

	/** The Constant API_RESPONSE_ERROR. */
	public static final String API_RESPONSE_ERROR = "API_RESPONSE_ERROR";

	/** The Constant EMAIL_INVALID_DESTINATION. */
	/**
	 * The invalid destination.
	 */
	public static final String EMAIL_INVALID_DESTINATION = "EMAIL_INVALID_DESTINATION";

	/** The Constant EMAIL_SUBJECT_BODY_EMPTY. */
	public static final String EMAIL_SUBJECT_BODY_EMPTY = "EMAIL_SUBJECT_BODY_EMPTY";

	/** The Constant EMAIL_INVALID_FROM_ADDRESS. */
	public static final String EMAIL_INVALID_FROM_ADDRESS = "EMAIL_INVALID_FROM_ADDRESS";

	/** The Constant EMAIL_INVALID_SCHEDULE. */
	public static final String EMAIL_INVALID_SCHEDULE = "EMAIL_INVALID_SCHEDULE";

	/** The Constant PUSH_INVALID_DESTINATION. */
	/**
	 * The invalid destination.
	 */
	public static final String PUSH_INVALID_DESTINATION = "PUSH_INVALID_DESTINATION";

	/** The Constant PUSH_TITLE_BODY_EMPTY. */
	public static final String PUSH_TITLE_BODY_EMPTY = "PUSH_TITLE_BODY_EMPTY";

	/** The Constant INVALID_VALUE. */
	public static final String INVALID_VALUE = "INVALID_VALUE";

	/** The Constant MAX_LENGTH. */
	public static final String MAX_LENGTH = "MaxLength";

	/** The Constant MIN_LENGTH. */
	public static final String MIN_LENGTH = "MinLength";

	/** The Constant MAX. */
	public static final String MAX = "Max";

	/** The Constant MIN. */
	public static final String MIN = "Min";

	/** The Constant LENGTH_EQUALS. */
	public static final String LENGTH_EQUALS = "LengthEquals";

	/** The Constant DATE_FORMAT. */
	public static final String DATE_FORMAT = "DateFormat";

	/** The Constant FLOAT_MAX_LENGTH. */
	public static final String FLOAT_MAX_LENGTH = "FloatMaxLength";

	/** The Constant FLOAT_MIN_LENGTH. */
	public static final String FLOAT_MIN_LENGTH = "FloatMinLength";

	/** The Constant DECIMAL_MAX. */
	public static final String DECIMAL_MAX = "DecimalMax";

	/** The Constant DECIMAL_MIN. */
	public static final String DECIMAL_MIN = "DecimalMin";

	/** The Constant DECIMAL_FORMAT. */
	public static final String DECIMAL_FORMAT = "DecimalFormat";

	/** The Constant MANDATORY. */
	public static final String MANDATORY = "Mandatory";

	/** The Constant ALLOWED_VALUES. */
	public static final String ALLOWED_VALUES = "AllowedValues";

	/** The Constant ALPHA_NUMERIC. */
	public static final String ALPHA_NUMERIC = "AlphaNumeric";

	/** The Constant EMAIL_COLLECTION. */
	public static final String EMAIL_COLLECTION = "EmailCollection";

	/** The Constant FILE_EXTENSION. */
	public static final String FILE_EXTENSION = "FileExtension";

	/** The Constant PATTERN. */
	public static final String PATTERN = "Pattern";

	/** The Constant IS_BOOLEAN. */
	public static final String IS_BOOLEAN = "IsBoolean";

	/** The Constant IS_ACTIVE. */
	public static final String IS_ACTIVE = "IsActive";

	/** The Constant IS_NUMERIC. */
	public static final String IS_NUMERIC = "IsNumeric";

	/** The Constant IS_PHONE. */
	public static final String IS_PHONE = "IsPhone";

	/** The Constant IS_PHONE_COLLECTION. */
	public static final String IS_PHONE_COLLECTION = "IsPhoneCollection";

	/** The Constant NO_SPECIAL. */
	public static final String NO_SPECIAL = "NoSpecial";

	/** The Constant OPTIONS. */
	public static final String OPTIONS = "Options";

	/** The Constant SOME_SPECIAL. */
	public static final String SOME_SPECIAL = "SomeSpecial";

	/** The Constant TIME_FORMAT. */
	public static final String TIME_FORMAT = "TimeFormat";

	/** The Constant VALID_COLUMN_VALUE. */
	public static final String VALID_COLUMN_VALUE = "ValidColumnValue";

	/** The Constant FILE_SIZE. */
	public static final String FILE_SIZE = "FileSize";

	/** The Constant FILE_TYPE. */
	public static final String FILE_TYPE = "FileType";

	/** The Constant FILE_DIMENSION. */
	public static final String FILE_DIMENSION = "FileDimension";

	/** Constant <code>UNSUPPORTED_MEDIA_TYPE="UNSUPPORTED_MEDIA_TYPE"</code> */
	public static final String UNSUPPORTED_MEDIA_TYPE = "UNSUPPORTED_MEDIA_TYPE";

	/** The Constant TEMPLATE_SUBJECT_NOT_REQUIRED. */

	/**********************************
	 * T E M P L A T E V A L I D A T I O N : S T A R T
	 *******************************/
	/**
	 * The invalid destination.
	 */
	public static final String TEMPLATE_SUBJECT_NOT_REQUIRED = "TEMPLATE_SUBJECT_NOT_REQUIRED";

	/** The Constant TEMPLATE_SMS_MESSAGE_MAX_LENGTH. */
	public static final String TEMPLATE_SMS_MESSAGE_MAX_LENGTH = "TEMPLATE_SMS_MESSAGE_MAX_LENGTH";

	/** Constant <code>INVALID_DATE="invalid_date"</code> */
	public static final String INVALID_DATE = "invalid_date";

	/** The error mappings. */

	private static Map<String, ErrorBean> errorMappings = new HashMap<>();

	/**
	 * Sets the errors.
	 *
	 * @param errors
	 *            the errors
	 */
	protected static void setErrors(Map<String, ErrorBean> errors) {
		errorMappings = errors;
	}

	/**
	 * Error.
	 *
	 * @param code
	 *            the code
	 * @return the error bean
	 */
	public static ErrorBean error(String code) {
		return (ErrorBean) ObjectUtils.deepCopy(errorMappings.get(code) != null ? errorMappings.get(code) : new ErrorBean(code, code));
	}

	/**
	 * Error.
	 *
	 * @param code
	 *            the code
	 * @param args
	 *            the args
	 * @return the error bean
	 */
	public static ErrorBean error(String code, Object[] args) {
		ErrorBean bean = (ErrorBean) ObjectUtils.deepCopy(errorMappings.get(code) != null ? errorMappings.get(code) : new ErrorBean(code, code));
		bean.setMessage(resolveMessage(bean.getMessage(), args));
		return bean;
	}

	/**
	 * Error.
	 *
	 * @param code
	 *            the code
	 * @param field
	 *            the field
	 * @return the error bean
	 */
	public static ErrorBean error(String code, String field) {
		ErrorBean bean = (ErrorBean) ObjectUtils.deepCopy(errorMappings.get(code) != null ? errorMappings.get(code) : new ErrorBean(code, code));
		bean.setField(field);
		return bean;
	}

	/**
	 * Error.
	 *
	 * @param code
	 *            the code
	 * @param field
	 *            the field
	 * @param args
	 *            the args
	 * @return the error bean
	 */
	public static ErrorBean error(String code, String field, Object[] args) {
		ErrorBean bean = (ErrorBean) ObjectUtils.deepCopy(errorMappings.get(code) != null ? errorMappings.get(code) : new ErrorBean(code, code));
		bean.setField(field);
		bean.setMessage(resolveMessage(bean.getMessage(), args));
		return bean;
	}

	/**
	 * Message.
	 *
	 * @param code
	 *            the code
	 * @return the string
	 */
	public static String message(String code) {
		return (String) ObjectUtils
				.deepCopy(errorMappings.get(code) != null ? errorMappings.get(code).getMessage() : "#" + new ErrorBean(code, code).getMessage());
	}

	/**
	 * Message.
	 *
	 * @param code
	 *            the code
	 * @param args
	 *            the args
	 * @return the string
	 */
	public static String message(String code, Object[] args) {
		String message = (String) ObjectUtils
				.deepCopy(errorMappings.get(code) != null ? errorMappings.get(code).getMessage() : "#" + new ErrorBean(code, code).getMessage());
		message = resolveMessage(message, args);
		return message;
	}

	/**
	 * Gets the errors.
	 *
	 * @return the errors
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, ErrorBean> getErrors() {
		return (Map<String, ErrorBean>) ObjectUtils.deepCopy(errorMappings);
	}

	/**
	 * <p>
	 * messageArgs.
	 * </p>
	 *
	 * @param args
	 *            a {@link java.lang.Object} object.
	 * @return an array of {@link java.lang.Object} objects.
	 */
	public static Object[] messageArgs(Object... args) {
		return args;
	}

	/**
	 * Resolve message.
	 *
	 * @param message
	 *            the message
	 * @param args
	 *            the args
	 * @return the string
	 */
	private static String resolveMessage(String message, Object[] args) {
		if (!StringUtils.isEmpty(message) && args != null) {
			message = MessageFormat.format(message, args);
		}
		return message;
	}
}
