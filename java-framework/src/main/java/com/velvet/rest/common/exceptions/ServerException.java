package com.velvet.rest.common.exceptions;

import com.velvet.rest.common.beans.ErrorBean;

/**
 * The Class ServerException.
 *
 * @author Mohit
 * @version $Id: $Id
 */
public class ServerException extends RuntimeException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5318591489806278624L;
    
    /** The error. */
    private ErrorBean error;

    /**
     * Instantiates a new server exception.
     *
     * @param error the error
     */
    public ServerException(ErrorBean error) {
        super(error.getMessage());
        this.error = error;
    }

    /**
     * Instantiates a new server exception.
     *
     * @param message the message
     * @param error the error
     */
    public ServerException(String message, ErrorBean error) {
        super(message);
        this.error = error;
    }

    /**
     * Instantiates a new server exception.
     *
     * @param message the message
     * @param error the error
     * @param throwable the throwable
     */
    public ServerException(String message, ErrorBean error, Throwable throwable) {
        super(message, throwable);
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
