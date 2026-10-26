package com.velvet.rest.common.exceptions;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.Errors;

import com.velvet.rest.common.beans.ErrorBean;

/**
 * The Class ValidationException.
 *
 * @author Mohit
 * @version $Id: $Id
 */
public class ValidationException extends RuntimeException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5318591489806278624L;

	/** The field. */
	private String field;

	/** The error. */
	private ErrorBean error;

	/** The errors. */
	private List<ErrorBean> errors = new ArrayList<>();

	/** The validation errors. */
	private Errors validationErrors = null;

	/**
	 * Instantiates a new validation exception.
	 */
	public ValidationException() {
	}

	/**
	 * Instantiates a new validation exception.
	 *
	 * @param field
	 *            the field
	 */
	public ValidationException(String field) {
		this.field = field;
	}

	/**
	 * Instantiates a new validation exception.
	 *
	 * @param error
	 *            the error
	 */
	public ValidationException(ErrorBean error) {
		super(error.getMessage());
		this.error = error;
		this.errors.add(this.error);
	}

	/**
	 * Instantiates a new validation exception.
	 *
	 * @param message
	 *            the message
	 * @param fieldErrors
	 *            the field errors
	 */
	public ValidationException(String message, Errors fieldErrors) {
		super(message);
		this.validationErrors = fieldErrors;
	}

	/**
	 * Instantiates a new validation exception.
	 *
	 * @param message
	 *            the message
	 * @param error
	 *            the error
	 */
	public ValidationException(String message, ErrorBean error) {
		super(message);
		this.error = error;
		this.errors.add(this.error);
	}

	/**
	 * Instantiates a new validation exception.
	 *
	 * @param message
	 *            the message
	 * @param error
	 *            the error
	 * @param errors
	 *            the errors
	 */
	public ValidationException(String message, ErrorBean error, List<ErrorBean> errors) {
		super(message);
		this.error = error;
		this.errors = errors;
	}

	/**
	 * Instantiates a new validation exception.
	 *
	 * @param message
	 *            the message
	 * @param errors
	 *            the errors
	 */
	public ValidationException(String message, List<ErrorBean> errors) {
		super(message);
		this.errors = errors;
	}

	/**
	 * Instantiates a new validation exception.
	 *
	 * @param message
	 *            the message
	 * @param error
	 *            the error
	 * @param errors
	 *            the errors
	 * @param throwable
	 *            the throwable
	 */
	public ValidationException(String message, ErrorBean error, List<ErrorBean> errors, Throwable throwable) {
		super(message, throwable);
		this.error = error;
		this.errors = errors;
	}

	/**
	 * Gets the error.
	 *
	 * @return the error
	 */
	public ErrorBean getError() {
		return error;
	}

	/**
	 * Gets the errors.
	 *
	 * @return the errors
	 */
	public List<ErrorBean> getErrors() {
		return errors;
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
	 * @param field
	 *            the new field
	 */
	public void setField(String field) {
		this.field = field;
	}

	/**
	 * Gets the validation errors.
	 *
	 * @return the validation errors
	 */
	public Errors getValidationErrors() {
		return this.validationErrors;
	}

}
