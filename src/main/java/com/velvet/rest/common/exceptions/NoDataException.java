package com.velvet.rest.common.exceptions;

import com.velvet.rest.common.beans.ErrorBean;
import com.velvet.rest.common.logging.AppLogger;
import com.velvet.rest.common.logging.LoggingManager;

/**
 * The Class NoDataException.
 *
 * @author Mohit
 * @version $Id: $Id
 */
public class NoDataException extends RuntimeException {

    /** The Constant _LOGGER. */
    private static final AppLogger _LOGGER = LoggingManager.getLogger(NoDataException.class);

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5318591489806278624L;
    
    /** The error. */
    private ErrorBean error; // NOSONAR


    /**
     * Instantiates a new no data exception.
     *
     * @param error the error
     */
    public NoDataException(ErrorBean error) {
        super(error.getMessage());
        this.error = error;
        _LOGGER.error(error.getMessage(), this);
    }

    /**
     * Instantiates a new no data exception.
     *
     * @param message the message
     * @param error the error
     */
    public NoDataException(String message, ErrorBean error) {
        super(message);
        this.error = error;
        _LOGGER.error(message, this);
    }

    /**
     * Instantiates a new no data exception.
     *
     * @param message the message
     * @param error the error
     * @param cause the cause
     */
    public NoDataException(String message, ErrorBean error, Throwable cause) {
        super(message, cause);
        this.error = error;
        _LOGGER.error(message, cause);
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
