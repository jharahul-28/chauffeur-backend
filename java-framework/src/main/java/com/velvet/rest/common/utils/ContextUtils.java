package com.velvet.rest.common.utils;

import java.io.File;
import java.net.URL;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.velvet.rest.common.constants.ApplicationProfiles;
import com.velvet.rest.common.constants.CommonConstants;
import com.velvet.rest.common.logging.AppLogger;
import com.velvet.rest.common.logging.LoggingManager;

/**
 * The Class ContextUtils.
 *
 * @author Mohit
 * @version $Id: $Id
 */
@Configuration
@SuppressWarnings("unused")
public class ContextUtils implements ApplicationContextAware {

	/** The Constant _LOGGER. */
	private static final AppLogger _LOGGER = LoggingManager.getLogger(ContextUtils.class);

	/** The context. */
	@Autowired
	private static ApplicationContext context;

	/** The application name. */
	private static String applicationName;

	/** The release name. */
	private static String releaseName;

	/** The release number. */
	private static String releaseNumber;

	/** The local cache identifier */
	private static String localCacheIdentifier;

	/** The active profile. */
	private static String activeProfile;

	/** The api host. */
	private static String apiHost;

	/**
	 * Gets the bean.
	 *
	 * @param              <T> the generic type
	 * @param requiredType the required type
	 * @return the bean
	 */
	public static <T> T getBean(Class<T> requiredType) {
		return getContext().getBean(requiredType);
	}

	/**
	 * Gets the bean.
	 *
	 * @param name the name
	 * @return the bean
	 */
	public static Object getBean(String name) {
		return getContext().getBean(name);
	}

	/**
	 * Gets the bean.
	 *
	 * @param name the name
	 * @param args the args
	 * @return the bean
	 */
	public static Object getBean(String name, Object... args) {
		return getContext().getBean(name, args);
	}

	/**
	 * Gets the bean.
	 *
	 * @param              <T> the generic type
	 * @param requiredType the required type
	 * @param args         the args
	 * @return the bean
	 */
	public static <T> T getBean(Class<T> requiredType, Object... args) {
		return getContext().getBean(requiredType, args);
	}

	/**
	 * Gets the context.
	 *
	 * @return the context
	 */
	public static ApplicationContext getContext() {
		return context;
	}

	/**
	 * Gets the boot name.
	 *
	 * @return the boot name
	 */
	public static String getBootName() {
		if (applicationName == null) {
			applicationName = getEnvironment().getProperty(CommonConstants.SPRNG_APPLICATION_NAME_KEY);
		}
		if (StringUtils.isEmpty(applicationName)) {
			applicationName = System.getenv("lambda_name");
		}

		return applicationName;
	}

	/**
	 * <p>
	 * getLocalCacheId.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public static String getLocalCacheId() {
		if (StringUtils.isEmpty(localCacheIdentifier)) {
			localCacheIdentifier = System.getProperty(CommonConstants.LAMBDA_ID);
		}
		if (StringUtils.isEmpty(localCacheIdentifier) && getEnvironment() != null) {
			localCacheIdentifier = getEnvironment().getProperty(CommonConstants.SPRNG_APPLICATION_NAME_KEY);
		}

		return localCacheIdentifier;
	}

	/**
	 * Gets the active profile.
	 *
	 * @return the active profile
	 */
	public static String getActiveProfile() {
		return getEnvironment().getActiveProfiles()[0];
	}

	public static boolean isProfileProd() {
		return isProfileActive(ApplicationProfiles.PRODUCTION);
	}

	public static boolean isProfileActive(String profile) {
		Assert.notNull(profile, "Profile Can not be null");
		return getActiveProfile().equalsIgnoreCase(profile);
	}

	/**
	 * Gets the environment.
	 *
	 * @return the environment
	 */
	public static Environment getEnvironment() {
		return context.getBean(Environment.class);
	}

	/**
	 * Gets the defaultbase.
	 *
	 * @param profile  the profile
	 * @param filePath the file path
	 * @return the defaultbase
	 */
	public static String getDefaultbase() {
		final URL classUrl = ContextUtils.class.getProtectionDomain().getCodeSource().getLocation();
		final String base = classUrl.getPath();
		final String configbase = base + "config";
		final String filePath = configbase;
		return File.separator.equals("/") ? "/" + filePath : filePath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext (org
	 * .springframework.context.ApplicationContext)
	 */
	/** {@inheritDoc} */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ContextUtils.context = applicationContext;
	}

	public static String getRelease() {
		if (releaseName == null) {
			releaseName = getEnvironment().getProperty(CommonConstants.RELEASE_PROPERTY);
		}
		if (StringUtils.isEmpty(releaseName)) {
			releaseName = System.getenv("lambda_release");
		}
		if (StringUtils.isEmpty(releaseName)) {
			releaseName = "current";
		}
		return releaseName;
	}

	public static String getReleaseNumber() {
		if (releaseNumber == null) {
			releaseNumber = getEnvironment().getProperty(CommonConstants.RELEASE_NUMBER_PROPERTY);
		}
		if (StringUtils.isEmpty(releaseNumber)) {
			releaseNumber = System.getenv("lambda_release_number");
		}
		return releaseNumber;
	}
}
