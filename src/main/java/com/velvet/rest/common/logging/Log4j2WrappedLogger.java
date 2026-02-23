package com.velvet.rest.common.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.ExtendedLoggerWrapper;

/**
 * The Class Log4j2WrappedLogger.
 *
 * @author Mohit
 * @version $Id: $Id
 */
public class Log4j2WrappedLogger implements AppLogger {

	/** The Constant FQCN. */
	private static final String FQCN = Log4j2WrappedLogger.class.getName();

	/** The log. */
	private ExtendedLoggerWrapper log;

	/**
	 * Gets the logger.
	 *
	 * @param aClass the a class
	 * @return the logger
	 */
	public static AppLogger getLogger(Class<?> aClass) {
		return getLogger(aClass.getName());
	}

	/**
	 * Gets the logger.
	 *
	 * @param name the name
	 * @return the logger
	 */
	public static AppLogger getLogger(String name) {
		Log4j2WrappedLogger logger = new Log4j2WrappedLogger();
		logger.initLog(name);
		return logger;
	}

	/**
	 * Inits the log.
	 *
	 * @param name the name
	 * @return the log 4 j 2 wrapped logger
	 */
	public Log4j2WrappedLogger initLog(String name) {
		org.apache.logging.log4j.Logger logger = LogManager.getLogger(name);
		log = new ExtendedLoggerWrapper((ExtendedLogger) logger, logger.getName(), logger.getMessageFactory());
		return new Log4j2WrappedLogger();
	}

	/**
	 * Checks if is info enabled.
	 *
	 * @return a boolean.
	 */
	public boolean isInfoEnabled() {
		return log.isInfoEnabled();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.velvet.rest.common.logging.AppLogger#debug(java.lang.Object, java.lang.Throwable)
	 */
	/** {@inheritDoc} */
	@Override
	public void debug(Object message, Throwable arg1) {
		log.logIfEnabled(FQCN, Level.DEBUG, null, message, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.velvet.rest.common.logging.AppLogger#debug(java.lang.Object)
	 */
	/** {@inheritDoc} */
	@Override
	public void debug(Object message) {
		log.logIfEnabled(FQCN, Level.DEBUG, null, message, null);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.velvet.rest.common.logging.AppLogger#error(java.lang.Object, java.lang.Throwable)
	 */
	/** {@inheritDoc} */
	@Override
	public void error(Object message, Throwable arg1) {
		log.logIfEnabled(FQCN, Level.ERROR, null, message, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.velvet.rest.common.logging.AppLogger#error(java.lang.Object)
	 */
	/** {@inheritDoc} */
	@Override
	public void error(Object message) {
		log.logIfEnabled(FQCN, Level.ERROR, null, message, null);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.velvet.rest.common.logging.AppLogger#fatal(java.lang.Object, java.lang.Throwable)
	 */
	/** {@inheritDoc} */
	@Override
	public void fatal(Object message, Throwable arg1) {
		log.logIfEnabled(FQCN, Level.FATAL, null, message, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.velvet.rest.common.logging.AppLogger#fatal(java.lang.Object)
	 */
	/** {@inheritDoc} */
	@Override
	public void fatal(Object message) {
		log.logIfEnabled(FQCN, Level.FATAL, null, message, null);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.velvet.rest.common.logging.AppLogger#info(java.lang.Object, java.lang.Throwable)
	 */
	/** {@inheritDoc} */
	@Override
	public void info(Object message, Throwable arg1) {
		log.logIfEnabled(FQCN, Level.INFO, null, message, null);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.velvet.rest.common.logging.AppLogger#info(java.lang.Object)
	 */
	/** {@inheritDoc} */
	@Override
	public void info(Object message) {
		log.logIfEnabled(FQCN, Level.INFO, null, message, null);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.velvet.rest.common.logging.AppLogger#warn(java.lang.Object, java.lang.Throwable)
	 */
	/** {@inheritDoc} */
	@Override
	public void warn(Object message, Throwable arg1) {
		log.logIfEnabled(FQCN, Level.WARN, null, message, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.velvet.rest.common.logging.AppLogger#warn(java.lang.Object)
	 */
	/** {@inheritDoc} */
	@Override
	public void warn(Object message) {
		log.logIfEnabled(FQCN, Level.WARN, null, message, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.velvet.rest.common.logging.AppLogger#event(java.lang.Object)
	 */
	/** {@inheritDoc} */
	@Override
	public void event(Object message) {
		log.logIfEnabled(FQCN, Level.forName("EVENT", 99), null, message, null);
	}

}
