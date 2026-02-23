package com.velvet.rest.common.exceptions;

/**
 * The Class JSONMappingException.
 *
 * @author Mohit
 * @version $Id: $Id
 */
public class JSONMappingException extends RuntimeException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5318591489806278624L;

	/**
	 * Instantiates a new JSON mapping exception.
	 *
	 * @param message the message
	 */
	public JSONMappingException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new JSON mapping exception.
	 *
	 * @param cause the cause
	 */
	public JSONMappingException(Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates a new JSON mapping exception.
	 *
	 * @param message the message
	 * @param cause   the cause
	 */
	public JSONMappingException(String message, Throwable cause) {
		super(message, cause);
	}

}
