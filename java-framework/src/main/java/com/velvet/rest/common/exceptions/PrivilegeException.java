package com.velvet.rest.common.exceptions;

import com.velvet.rest.common.beans.ErrorBean;

/**
 * The Class PrivilegeException.
 *
 * @author Mohit
 * @version $Id: $Id
 */
public class PrivilegeException extends RuntimeException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5318591489806278624L;

	/** The error. */
	private ErrorBean error;

	/**
	 * Instantiates a new privilege exception.
	 *
	 * @param error the error
	 */
	public PrivilegeException(ErrorBean error) {
		super(error.getMessage());
		this.error = error;
	}

	/**
	 * Instantiates a new privilege exception.
	 *
	 * @param message the message
	 * @param error   the error
	 */
	public PrivilegeException(String message, ErrorBean error) {
		super(message);
		this.error = error;
	}

	/**
	 * Instantiates a new privilege exception.
	 *
	 * @param message the message
	 * @param error   the error
	 * @param cause   the cause
	 */
	public PrivilegeException(String message, ErrorBean error, Throwable cause) {
		super(message, cause);
		this.error = error;
	}

	/**
	 * Gets the error.
	 *
	 * @return the error
	 */
	public ErrorBean getError() {
		return error;
	}

}
