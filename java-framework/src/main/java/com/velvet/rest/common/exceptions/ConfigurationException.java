package com.velvet.rest.common.exceptions;

/**
 * The Class ConfigurationException.
 *
 * @author Mohit
 * @version $Id: $Id
 */
public class ConfigurationException extends RuntimeException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5318591489806278624L;

    /**
     * Instantiates a new configuration exception.
     *
     * @param message the message
     */
    public ConfigurationException(String message) {
        super(message);
    }

    /**
     * Instantiates a new configuration exception.
     *
     * @param message the message
     * @param cause the cause
     */
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

}
