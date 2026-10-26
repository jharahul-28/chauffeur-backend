package com.velvet.rest.common.constants;

/**
 * The Class CommonConstants.
 *
 * @author Mohit
 * @version $Id: $Id
 */
public class CommonConstants {

	/**
	 * The Enum Protocol.
	 */
	public enum Protocol {
		/** The http. */
		HTTP,
		/** The https. */
		HTTPS
	}

	/**
	 * The Enum Protocol.
	 */
	public interface UserTypes {
		String CUSTOMER = "customer";

		String ADMIN = "admin";

		String CHAUFFEUR = "chauffeur";
	}

	/**
	 * The Enum Protocol.
	 */
	public interface AuthTypes {
		String FIREBASE = "FIREBASE";
		String SYSTEM = "SYSTEM";

	}

	/** Constant <code>exposeHeaders</code> */
	public static String[] exposeHeaders = new String[] { CommonConstants.AUTH_HEADER, CommonConstants.ATTACHMENT_HEADER, "Content-Disposition", "Content-Type",
			"Accept", "Accept-Encoding", "Accept-Language", "Cache-Control", "Content-Length", "Connection", "Host", "Origin", "Pragma", "User-Agent",
			"Referer" };

	public interface PoolDataSourceBean {
		String PRIMARY = "primaryDataSource";

		String REPLICA = "replicaDataSource";

		String DYNAMIC = "dynamicDataSource";
	}

	/** The Constant BASE_PACKAGE. */
	public static final String BASE_PACKAGE = "com.velvet.rest";

	/** The Constant BASE_PACKAGE. */
	public static final String CATALOG_REPO_PACKAGE = "com.velvet.rest.services.catalog.repository";

	/** The Constant BASE_PACKAGE. */
	public static final String USER_REPO_PACKAGE = "com.velvet.rest.services.user.repository";

	/** The Constant EXTERNAL_CONFIG_PROPERTY. */
	public static final String EXTERNAL_CONFIG_PROPERTY = "wms.external.config";

	/** The Constant REDIS_TEMPLATE_QUALIFIER. */
	public static final String CACHE_TEMPLATE_QUALIFIER = "cacheByteArrayTemplate";

	/** The Constant ENABLE_CACHE_QUALIFIED_NAME. */
	public static final String ENABLE_CACHE_QUALIFIED_NAME = "com.velvet.rest.services.entity.base.annotation.EnableEntityCache";

	/** The Constant TABLE_ANNOTATION. */
	public static final String TABLE_ANNOTATION = "javax.persistence.Table";

	/** The Constant SPRNG_APPLICATION_NAME_KEY. */
	public static final String SPRNG_APPLICATION_NAME_KEY = "spring.application.name";

	/** The Constant SPRING_PROFILE_ACTIVE_KEY. */
	public static final String SPRING_PROFILE_ACTIVE_KEY = "spring.profiles.active";

	/** The Constant LOG4J2_FILE_NAME_PROP. */
	public static final String LOG4J2_FILE_NAME_PROP = "log4j.configurationFile";

	/** Constant <code>DEPLOYMENT_TYPE_KEY="milkbakset.deployment.type"</code> */
	public static final String DEPLOYMENT_TYPE_KEY = "wms.deployment.type";

	/** The Constant REPOSITORY_ASPECT. */
	public static final String REPOSITORY_ASPECT = "execution(* com.velvet.rest.services..*.repository.*.*(..))";

	/**
	 * The Constant REDIS_REPOSITORY.
	 */
	public static final String REDIS_REPOSITORY = "execution(* com.velvet.core.framework.caching.repository.CacheRepository.*(..))";

	/** The Constant DEFAULT_PAGE_NO. */
	public static final String DEFAULT_PAGE_NO = "DEFAULT_PAGE_NO";

	/** The Constant DEFAULT_PAGE_SIZE. */
	public static final String DEFAULT_PAGE_SIZE = "DEFAULT_PAGE_SIZE";

	/** The Constant MAX_PAGE_SIZE. */
	public static final String MAX_PAGE_SIZE = "MAX_PAGE_SIZE";

	/** The Constant GET_MAX_RECORDS_LIMIT. */
	public static final Integer GET_MAX_RECORDS_LIMIT = 50;

	/** The Constant AUTOCOMPLETE_MAX_RECORDS_LIMIT. */
	public static final String AUTOCOMPLETE_MAX_RECORDS_LIMIT = "AC_MAX_RECORDS_LIMIT";

	/** The Constant UTILITY_TOKEN. */
	public static final String UTILITY_TOKEN = "UTILITY_TOKEN";

	/** The Constant AUTH_TOKEN_VALIDITY. */
	public static final String AUTH_TOKEN_VALIDITY = "AUTH_TOKEN_VALIDITY_";

	/** The Constant DEFAULT_COUNTRY_CODE. */
	public static final String DEFAULT_COUNTRY_CODE = "+91";

	/** The Constant AUTH_HEADER. */
	public static final String AUTH_HEADER = "auth-key";

	public static final String WH_ID = "wh-id";

	/** Constant <code>AUTH_SOCIAL_HEADER="social-key"</code> */
	public static final String AUTH_SOCIAL_HEADER = "rz-social-key";

	/** Constant <code>SYSTEM_AUTH_HEADER="b2b-auth-key"</code> */
	public static final String SYSTEM_AUTH_HEADER = "b2b-auth-key";

	/** Constant <code>ATTACHMENT_HEADER="filename"</code> */
	public static final String ATTACHMENT_HEADER = "filename";

	/** The Constant X_SOURCE_APP. */
	public static final String X_SOURCE_APP_TOKEN = "x-rzsource-app-token";

	/** Constant <code>X_SOURCE_APP="x-sksource-app"</code> */
	public static final String X_SOURCE_APP = "x-rzsource-app";

	/** Constant <code>X_REQUEST_ID="x-skrequest-id"</code> */
	public static final String X_REQUEST_ID = "x-rzrequest-id";

	/** The Constant LOAD_BALANCER_PROPERTY. */
	public static final String LOAD_BALANCER_PROPERTY = "service.loadbalancer.url";

	/** The Constant APPLICATION_TIME_ZONE. */
	public static final String APPLICATION_TIME_ZONE = "velvet.application.timezone";

	/** The Constant TZ_ASIA_CALCUTTA. */
	public static final String TZ_ASIA_CALCUTTA = "Asia/Calcutta";

	/** The Constant PRIMARY_DBPASS_PROPERTY. */
	public static final String PRIMARY_DBPASS_PROPERTY = "velvet.dbserver.primary.password";

	/** The Constant REPLICA_DBPASS_PROPERTY. */
	public static final String REPLICA_DBPASS_PROPERTY = "velvet.dbserver.replica.password";

	/** Constant <code>VALIDATION_ERROR_MSG="Validation Error(s)"</code> */
	public static final String VALIDATION_ERROR_MSG = "Validation Error(s)";

	/** Constant <code>APP_REALM="APP_REALM"</code> */
	public static final String APP_REALM = "APP_REALM";

	/** Constant <code>ADMIN_GROUP="ADMIN_GROUP"</code> */
	public static final String ADMIN_GROUP = "ADMIN_GROUP";

	/** Constant <code>CUSTOMER_GROUP="CUSTOMER_GROUP"</code> */
	public static final String CUSTOMER_GROUP = "CUSTOMER_GROUP";

	/** Constant <code>RECAPTCHA_VERIFY_URL="RECAPTCHA_VERIFY_URL"</code> */
	public static final String RECAPTCHA_VERIFY_URL = "RECAPTCHA_VERIFY_URL";

	/** Constant <code>RECAPTCHA_PASSKEY="RECAPTCHA_PASSKEY"</code> */
	public static final String RECAPTCHA_PASSKEY = "RECAPTCHA_PASSKEY";

	/** Constant <code>LAMBDA_ID="lambda_id"</code> */
	public static final String LAMBDA_ID = "lambda_id";

	/** The Constant RELEASE_PROPERTY. */
	public static final String RELEASE_PROPERTY = "service.release";

	/** The Constant RELEASE_NUMBER_PROPERTY. */
	public static final String RELEASE_NUMBER_PROPERTY = "service.release.number";

	/**
	 * Constant
	 * <code>DB_CONNECTION_RESET_TIME_MIN="DB_CONNECTION_RESET_TIME_MIN"</code>
	 */
	public static final String DB_CONNECTION_RESET_TIME_MIN = "DB_CONNECTION_RESET_TIME_MIN";

	/**
	 * Constant
	 * <code>KEEP_ALIVE_POOL_INTERVAL_SEC="KEEP_ALIVE_POOL_INTERVAL_SEC"</code>
	 */
	public static final String KEEP_ALIVE_POOL_INTERVAL_SEC = "KEEP_ALIVE_POOL_INTERVAL_SEC";

	/** Constant <code>STATIC_BASE_URL="STATIC_BASE_URL"</code> */
	public static final String STATIC_BASE_URL = "STATIC_BASE_URL";

	/** Constant <code>APP_VERSION="APP_VERSION"</code> */
	public static final String APP_VERSION = "APP_VERSION";

	/** Constant <code>MIN_APP_VERSION="MIN_APP_VERSION"</code> */
	public static final String MIN_APP_VERSION = "MIN_APP_VERSION";

	/** Constant <code>CONTACT_PHONE="CONTACT_PHONE"</code> */
	public static final String CONTACT_PHONE = "CONTACT_PHONE";

	/** Constant <code>TOPUP_URL="TOPUP_URL"</code> */
	public static final String TOPUP_URL = "TOPUP_URL";

	/** Constant <code>DEFAULT_HUB="DEFAULT_HUB"</code> */
	public static final String DEFAULT_HUB = "DEFAULT_HUB";

	/** Constant <code>APPLICATION_BASE_URL="APPLICATION_BASE_URL"</code> */
	public static final String APPLICATION_BASE_URL = "APPLICATION_BASE_URL";

	/** Constant <code>MAX_RECORD_LIMIT_EXPORT="MAX_RECORD_LIMIT_EXPORT"</code> */
	public static final String MAX_RECORD_LIMIT_EXPORT = "MAX_RECORD_LIMIT_EXPORT";

	/** Constant for application control flag */
	public static final String APP_CONTROL_FLAG = "velvet.app.control.flag";

	public static final String NO_SESSION_ENDPOINTS_QUALIFIER = "noSessionEndpoints";
	public static final String IMAGE_SUFFIX = "file_";
	public static final String IMAGE_EXTENSION = ".jpeg";
	public static final String IMAGE = "image";
	public static final String DOC = "doc";

	public static final String PHONE_REGEX = "^[5-9][0-9]{9}$";
	public static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z" + "A-Z]{2,7}$";

}
