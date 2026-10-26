package com.velvet.rest.common.logging;

/**
 * A factory for creating WrappedLogger objects.
 *
 * @author Mohit
 * @version $Id: $Id
 */
public class WrappedLoggerFactory {

    /**
     * Instantiates a new wrapped logger factory.
     */
    private WrappedLoggerFactory() {}

    /**
     * Gets the wrapped logger.
     *
     * @param aClass the a class
     * @return the wrapped logger
     */
    public static AppLogger getWrappedLogger(Class<?> aClass) {
        return getWrappedLogger(aClass, AppLogger.LOG4J2_WRAPPER);
    }

    /**
     * Gets the wrapped logger.
     *
     * @param aClass the a class
     * @param wrapper the wrapper
     * @return the wrapped logger
     */
    public static AppLogger getWrappedLogger(Class<?> aClass, String wrapper) {
        AppLogger logger = null;
        if (AppLogger.LOG4J2_WRAPPER.equalsIgnoreCase(wrapper)) {
            logger = Log4j2WrappedLogger.getLogger(aClass);
        }
        return logger;
    }
}
