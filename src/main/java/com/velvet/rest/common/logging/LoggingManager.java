package com.velvet.rest.common.logging;

/**
 * The Class LoggingManager.
 *
 * @author Mohit
 * @version $Id: $Id
 */
public class LoggingManager {

    /**
     * Gets the logger.
     *
     * @param aClass the a class
     * @return the logger
     */
    public static AppLogger getLogger(Class<?> aClass) {
        return WrappedLoggerFactory.getWrappedLogger(aClass);
    }
}
