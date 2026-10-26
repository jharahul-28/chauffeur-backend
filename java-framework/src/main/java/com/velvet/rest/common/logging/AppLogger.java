package com.velvet.rest.common.logging;

/**
 * The Interface AppLogger.
 *
 * @author Mohit
 * @version $Id: $Id
 */
public interface AppLogger {

    /**
     * The Enum LOG_WRAPPER.
     */
    public static enum LOG_WRAPPER {
        
        /** The log4j2. */
        LOG4J2
    }

    /** The log4j2 wrapper. */
    public static String LOG4J2_WRAPPER = LOG_WRAPPER.LOG4J2.toString();

    /**
     * Debug.
     *
     * @param message the message
     * @param arg1 the arg 1
     */
    public void debug(Object message, Throwable arg1);

    /**
     * Debug.
     *
     * @param message the message
     */
    public void debug(Object message);

    /**
     * Error.
     *
     * @param message the message
     * @param arg1 the arg 1
     */
    public void error(Object message, Throwable arg1);

    /**
     * Error.
     *
     * @param message the message
     */
    public void error(Object message);

    /**
     * Fatal.
     *
     * @param message the message
     * @param arg1 the arg 1
     */
    public void fatal(Object message, Throwable arg1);

    /**
     * Fatal.
     *
     * @param message the message
     */
    public void fatal(Object message);

    /**
     * Info.
     *
     * @param message the message
     * @param arg1 the arg 1
     */
    public void info(Object message, Throwable arg1);

    /**
     * Info.
     *
     * @param message the message
     */
    public void info(Object message);

    /**
     * Warn.
     *
     * @param message the message
     * @param arg1 the arg 1
     */
    public void warn(Object message, Throwable arg1);

    /**
     * Warn.
     *
     * @param message the message
     */
    public void warn(Object message);

    /**
     * Event.
     *
     * @param logMessage the log message
     */
    public void event(Object logMessage);

}
