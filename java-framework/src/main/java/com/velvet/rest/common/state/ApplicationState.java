package com.velvet.rest.common.state;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

/**
 * Application-level state holder for runtime configuration and flags.
 * This class provides thread-safe access to application-wide variables.
 *
 * @author Mohit
 * @version $Id: $Id
 */
@Component
public class ApplicationState {

	/**
	 * Application control flag that can be used to enable/disable request processing.
	 * Values:
	 * 0 = Normal operation (default)
	 * 1 = Maintenance mode (reject all non-system requests)
	 * 2 = Read-only mode (allow only GET requests)
	 * 3+ = Custom application-specific states
	 */
	private static final AtomicInteger appControlFlag = new AtomicInteger(0);

	/**
	 * Request counter for monitoring purposes
	 */
	private static final AtomicInteger requestCounter = new AtomicInteger(0);

	/**
	 * Get the current application control flag value.
	 *
	 * @return the current flag value
	 */
	public static int getAppControlFlag() {
		return appControlFlag.get();
	}

	/**
	 * Set the application control flag value.
	 *
	 * @param value the new flag value
	 */
	public static void setAppControlFlag(int value) {
		appControlFlag.set(value);
	}

	/**
	 * Get the current request counter value.
	 *
	 * @return the current counter value
	 */
	public static int getRequestCounter() {
		return requestCounter.get();
	}

	/**
	 * Increment and get the request counter.
	 *
	 * @return the incremented counter value
	 */
	public static int incrementRequestCounter() {
		return requestCounter.incrementAndGet();
	}

	/**
	 * Reset the request counter to zero.
	 */
	public static void resetRequestCounter() {
		requestCounter.set(0);
	}

	/**
	 * Check if the application is in normal operation mode.
	 *
	 * @return true if in normal mode (flag = 0)
	 */
	public static boolean isNormalMode() {
		return appControlFlag.get() == 0;
	}

	/**
	 * Check if the application is in maintenance mode.
	 *
	 * @return true if in maintenance mode (flag = 1)
	 */
	public static boolean isMaintenanceMode() {
		return appControlFlag.get() == 1;
	}

	/**
	 * Check if the application is in read-only mode.
	 *
	 * @return true if in read-only mode (flag = 2)
	 */
	public static boolean isReadOnlyMode() {
		return appControlFlag.get() == 2;
	}
}
