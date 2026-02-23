package com.velvet.rest.common.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;

import com.velvet.rest.common.constants.CommonConstants;
import com.velvet.rest.common.logging.AppLogger;
import com.velvet.rest.common.logging.LoggingManager;
import com.velvet.rest.common.utils.ContextUtils;

/**
 * The Class ExternalConfigReader.
 *
 * @author Mohit
 * @version $Id: $Id
 */
@Configuration
public class ExternalConfigReader {

	/** The Constant _LOGGER. */
	private static final AppLogger _LOGGER = LoggingManager.getLogger(ExternalConfigReader.class);

	/** The env. */
	@Autowired
	private Environment env;

	/** The context utils. */
	@Autowired
	@SuppressWarnings("unused")
	private ContextUtils contextUtils;

	/**
	 * Application config.
	 *
	 * @return the external application config
	 */
	@Bean
	public ExternalApplicationConfig applicationConfig() {
		long millis = System.currentTimeMillis();
		// ExternalApplicationConfig config = loadFromClassPath();
//		if (config == null) {
//			config = loadFromExternalPath();
//		}
//		if (config == null) {
//			throw new ConfigurationException("filePath is null or blank. Check if following argument has been passed to the program :"
//					+ CommonConstants.EXTERNAL_CONFIG_PROPERTY
//					+ " and the path has required file. If not check if class path has required file in config folder.");
//		}
		_LOGGER.debug("External Configuration Loaded in : " + (System.currentTimeMillis() - millis));
		_LOGGER.debug("**************************************************************************************************");
		return null;
	}

	private ExternalApplicationConfig loadFromExternalPath() {
		try {
			_LOGGER.debug("Loading database configuration from external file");
			Yaml yml = new Yaml();
			String filePath = env.getProperty(CommonConstants.EXTERNAL_CONFIG_PROPERTY);
			String profile = env.getActiveProfiles()[0];

			// filePath = ContextUtils.getDefaultbase(profile, filePath);

			_LOGGER.debug("filepath : " + filePath);
			if (filePath != null && !filePath.endsWith(".yml")) {
				String fileName = "appconfig-" + profile + ".yml";

				_LOGGER.debug("final filename : " + fileName);

				char fileSeparator = File.separatorChar;
				filePath = new StringBuilder().append(filePath).append(fileSeparator).append("ymls").append(fileSeparator).append(fileName).toString();
				_LOGGER.debug("Loading file as java bean");
				logFileContent(filePath);
				ExternalApplicationConfig externalConfig = yml.loadAs(new FileReader(new File(filePath)), ExternalApplicationConfig.class);
				externalConfig.setActiveProfile(profile);

				setPropertyFromSystemVariable(externalConfig);

				return externalConfig;
			}
		} catch (Exception e) {
			_LOGGER.debug("Loading file as java bean: FAILED due to " + e.getMessage() + "\n\n");
		}
		return null;
	}

	private void setPropertyFromSystemVariable(ExternalApplicationConfig externalConfig) {
		String primaryPassword = System.getProperty(CommonConstants.PRIMARY_DBPASS_PROPERTY);
		if (!StringUtils.isEmpty(primaryPassword)) {
			if (externalConfig != null && externalConfig.getDbserver() != null && externalConfig.getDbserver().getPrimary() != null) {
				externalConfig.getDbserver().getPrimary().setPassword(primaryPassword);
			}
		}
		String replicaPassword = System.getProperty(CommonConstants.REPLICA_DBPASS_PROPERTY);
		if (!StringUtils.isEmpty(replicaPassword)) {
			if (externalConfig != null && externalConfig.getDbserver() != null && externalConfig.getDbserver().getReplica() != null) {
				externalConfig.getDbserver().getReplica().setPassword(replicaPassword);
			}
		}
		primaryPassword = System.getenv(CommonConstants.PRIMARY_DBPASS_PROPERTY);
		if (!StringUtils.isEmpty(replicaPassword)) {
			if (externalConfig != null && externalConfig.getDbserver() != null && externalConfig.getDbserver().getPrimary() != null) {
				externalConfig.getDbserver().getPrimary().setPassword(primaryPassword);
			}
		}
		replicaPassword = System.getenv(CommonConstants.REPLICA_DBPASS_PROPERTY);
		if (!StringUtils.isEmpty(replicaPassword)) {
			if (externalConfig != null && externalConfig.getDbserver() != null && externalConfig.getDbserver().getReplica() != null) {
				externalConfig.getDbserver().getReplica().setPassword(replicaPassword);
			}
		}
	}

	/**
	 * <p>
	 * loadFromClassPath.
	 * </p>
	 *
	 * @return a ExternalApplicationConfig object.
	 */
	public ExternalApplicationConfig loadFromClassPath() {
		try {
			_LOGGER.debug("Loading configuration from classpath file");
			Yaml yml = new Yaml();
			ClassPathResource resource = new ClassPathResource("ymls/appconfig-" + env.getActiveProfiles()[0] + ".yml");
			_LOGGER.debug("Loading file as java bean");
			ExternalApplicationConfig externalConfig = yml.loadAs(resource.getInputStream(), ExternalApplicationConfig.class);
			externalConfig.setActiveProfile(env.getActiveProfiles()[0]);

			setPropertyFromSystemVariable(externalConfig);

			_LOGGER.debug("Loading configuration from classpath file : COMPLETE ");
			return externalConfig;
		} catch (Exception e) {
			_LOGGER.debug("Loading file as java bean: FAILED due to " + e.getMessage() + "\n\n");
			return null;
		}
	}

	/**
	 * Log file content.
	 *
	 * @param filePath the file path
	 */
	public static void logFileContent(String filePath) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File(filePath)));
			String line = null;
			StringBuilder builder = new StringBuilder();
			builder.append("File Content : ").append("\n");
			while ((line = reader.readLine()) != null) {
				builder.append("\t\t > \t").append(line).append("\n");
			}
			reader.close();
			_LOGGER.debug(builder.toString());
		} catch (IOException e) {
			_LOGGER.debug("Could not write file content due to : " + e.getMessage() + "\n\n");
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					_LOGGER.warn("Could not close reader : " + e.getMessage());
				}
		}
	}

}
