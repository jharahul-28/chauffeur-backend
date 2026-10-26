package com.velvet.rest.common.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.velvet.rest.common.properties.Errors;

/**
 * The Class ErrorBean.
 *
 * @author Mohit
 * @version $Id: $Id
 */
public class ErrorBean implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3174610163626734324L;

	/** The code. */
	private String code;

	/** The message. */
	private String message;

	/** The field. */
	private String field;

	/**
	 * Instantiates a new error bean.
	 */
	public ErrorBean() {
	}

	/**
	 * With error.
	 *
	 * @param code the code
	 * @return the error bean
	 */
	public static ErrorBean withError(String code) {
		return new ErrorBean(code, Errors.error(code).getMessage());
	}

	/**
	 * With error.
	 *
	 * @param code  the code
	 * @param field the field
	 * @return the error bean
	 */
	public static ErrorBean withError(String code, String field) {
		return new ErrorBean(code, Errors.error(code).getMessage(), field);
	}

	/**
	 * With error.
	 *
	 * @param code    the code
	 * @param message the message
	 * @param field   the field
	 * @return the error bean
	 */
	public static ErrorBean withError(String code, String message, String field) {
		return new ErrorBean(code, message, field);
	}

	/**
	 * Instantiates a new error bean.
	 *
	 * @param code the code
	 */
	public ErrorBean(String code) {
		super();
		this.code = code;
		this.message = Errors.error(code).getMessage();
	}

	/**
	 * Instantiates a new error bean.
	 *
	 * @param code    the code
	 * @param message the message
	 */
	public ErrorBean(String code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	/**
	 * Instantiates a new error bean.
	 *
	 * @param code    the code
	 * @param message the message
	 * @param field   the field
	 */
	@JsonCreator
	public ErrorBean(@JsonProperty("code") String code, @JsonProperty("message") String message, @JsonProperty("field") String field) {
		super();
		this.code = code;
		this.field = field;
		this.message = message;
	}

	/**
	 * Builder.
	 *
	 * @return the error builder
	 */
	public static ErrorBuilder builder() {
		return new ErrorBuilder();
	}

	/**
	 * Gets the code.
	 *
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Sets the code.
	 *
	 * @param code the new code
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message.
	 *
	 * @param message the new message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Gets the field.
	 *
	 * @return the field
	 */
	public String getField() {
		return field;
	}

	/**
	 * Sets the field.
	 *
	 * @param field the new field
	 */
	public void setField(String field) {
		this.field = field;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "Error [code=" + code + ", message=" + message + ", field=" + field + "]";
	}

	/**
	 * The Class ErrorBuilder.
	 */
	public static class ErrorBuilder {

		/** The errors. */
		List<ErrorBean> errors = new ArrayList<>();

		/**
		 * Adds the.
		 *
		 * @param error the error
		 * @return the error builder
		 */
		public ErrorBuilder add(ErrorBean error) {
			errors.add(error);
			return this;
		}

		/**
		 * Builds the.
		 *
		 * @return the list
		 */
		public List<ErrorBean> build() {
			return this.errors;
		}
	}
}
