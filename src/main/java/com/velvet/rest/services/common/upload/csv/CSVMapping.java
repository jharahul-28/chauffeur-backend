package com.velvet.rest.services.common.upload.csv;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.velvet.rest.common.beans.ErrorBean;

import org.springframework.util.StringUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The Interface CSVMapping to be used by csv upload bean.
 *
 * @author Sanjeev Jha
 */
public interface CSVMapping {

	/**
	 * New bean.
	 *
	 * @param <T> the generic type
	 * @return the t
	 */
	<T extends CSVMapping> T newBean();

	/**
	 * Gets the mapping string.
	 *
	 * @return the mapping string
	 */
	@JsonIgnore
	String getHeaderMapping();

	/**
	 * Gets the header field mapping.
	 *
	 * @return the header field mapping
	 */
	@JsonIgnore
	default Map<String, String> getHeaderFieldMapping() {
		final String str = getHeaderMapping();
		return Arrays.asList(str.split(",")).stream().map(s -> s.split(":")).collect(Collectors.toMap(e -> e[1], e -> e[0]));
	}

	/**
	 * Gets the errors.
	 *
	 * @return the errors
	 */
	List<ErrorBean> getErrors();

	/**
	 * Adds the error.
	 *
	 * @param field the field
	 * @param value the value
	 */
	default void addError(String field, String value) {
		if (getErrors() != null) {
			getErrors().add(ErrorBean.withError("FIELD_ERROR", String.format("Invalid Value Received as '%s'", value), field));
		}
	}

	/**
	 * Adds the errors.
	 *
	 * @param errors the errors
	 */
	default void addErrors(Collection<ErrorBean> errors) {
		if (getErrors() != null) {
			getErrors().addAll(errors);
		}
	}

	/**
	 * Fill.
	 *
	 * @param data     the data
	 * @param fieldMap the field map
	 */
	default void fill(Map<String, String> data, final Map<String, Field> fieldMap) {
		data.entrySet().stream().forEach(entry -> {
			final String key = entry.getKey();
			final String value = entry.getValue();
			final Field field = fieldMap.get(key);
			fillBeanField(key, value, field);
		});
	}

	/**
	 * Fill bean field.
	 *
	 * @param key   the key
	 * @param value the value
	 * @param field the field
	 */
	default void fillBeanField(final String key, final String value, final Field field) {
		if (field != null) {
			boolean setDefault = false;
			if (StringUtils.isEmpty(value)) {
				setDefault = true;
				if (field.getAnnotation(NotNull.class) != null || field.getAnnotation(NotEmpty.class) != null) {
					setDefault = false;
				}
			}
			try {
				if (setDefault) {
					field.set(this, null);
				} else {
					final Class<?> type = field.getType();
					if (type.equals(Boolean.class) || type.equals(Boolean.TYPE)) {
						field.set(this, Boolean.parseBoolean(value));
					} else if (type.equals(Integer.class) || type.equals(Integer.TYPE)) {
						field.set(this, Integer.valueOf(value));
					} else if (type.equals(Long.class) || type.equals(Long.TYPE)) {
						field.set(this, Long.valueOf(value));
					} else if (type.equals(Double.class) || type.equals(Double.TYPE)) {
						field.set(this, Double.valueOf(value));
					} else if (type.equals(BigInteger.class)) {
						field.set(this, new BigInteger(value));
					} else if (type.equals(BigDecimal.class)) {
						field.set(this, new BigDecimal(value));
					} else {
						field.set(this, value);
					}
				}
			} catch (final Exception e) {
				// addError(key, value);
				addError(field.getName(), value);
			}
		}
	}

}
