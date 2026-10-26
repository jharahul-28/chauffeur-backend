package com.velvet.rest.services.common.swagger;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.yaml.snakeyaml.Yaml;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.velvet.rest.common.constants.CommonConstants;
import com.velvet.rest.common.logging.AppLogger;
import com.velvet.rest.common.logging.LoggingManager;

import springfox.documentation.RequestHandler;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * <p>
 * Abstract SwaggerConfig class.
 * </p>
 *
 * @author Mohit
 *
 */
@Configuration
@EnableSwagger2
public abstract class SwaggerConfig {

	/** The Constant _LOGGER. */
	private static final AppLogger _LOGGER = LoggingManager.getLogger(SwaggerConfig.class);

	/** Constant <code>SWAGGER_BASE_PACKAGE="swagger.base.package"</code> */
	public static final String SWAGGER_BASE_PACKAGE = "swagger.base.package";

	/**
	 * Gets the api info.
	 *
	 * @return the api info
	 */
	public abstract SwaggerApiInfo getApiInfo();

	/** The env. */
	@Autowired
	private Environment env;

	/**
	 * The Class ApiCodeMessage.
	 */
	public static class ApiCodeMessage {

		/**
		 * Instantiates a new api code message.
		 */
		private ApiCodeMessage() {}

		/** The Constant _200. */
		public static final String _200 = "Success";

		/** The Constant _400. */
		public static final String _400 = "Validation Error/Business Error";

		/** The Constant _401. */
		public static final String _401 = "Unauthorized Error";

		/** The Constant _403. */
		public static final String _403 = "Privilege Error";

		/** The Constant _404. */
		public static final String _404 = "Not Found Error";

		/** The Constant _500. */
		public static final String _500 = "Server Error [NetworkFailure, Server Side Exception, Permission Error]";

	}

	/**
	 * Product api.
	 *
	 * @return the docket
	 */
	@Bean
	public Docket productApi() {

		_LOGGER.info("Starting Swagger Docket Initialization");

		String basePackage = System.getProperty(SWAGGER_BASE_PACKAGE);
		if (StringUtils.isEmpty(basePackage)) {
			basePackage = CommonConstants.BASE_PACKAGE;
		}

		List<Predicate<RequestHandler>> predicates = new ArrayList<>();
		for (String pkg : basePackage.split(",")) {
			predicates.add(RequestHandlerSelectors.basePackage(pkg));
		}

		final SwaggerApiInfo swaggerInfo = getApiInfo();
		ApiInfo apiInfo = ApiInfo.DEFAULT;
		if (swaggerInfo != null) {
			apiInfo = new ApiInfo(swaggerInfo.getTitle(), swaggerInfo.getDescription(), swaggerInfo.getVersion(), swaggerInfo.getTermsOfServiceUrl(),
					new Contact(swaggerInfo.getContact().getName(), swaggerInfo.getContact().getUrl(), swaggerInfo.getContact().getEmail()),
					swaggerInfo.getLicense(), swaggerInfo.getLicenseUrl(), new ArrayList<>());
		}

		_LOGGER.info("External Swagger Config : " + swaggerInfo);

		final List<ResponseMessage> response = new ArrayList<>();
		response.add(new ResponseMessageBuilder().code(401).message(ApiCodeMessage._401).build());
		response.add(new ResponseMessageBuilder().code(403).message(ApiCodeMessage._403).build());
		response.add(new ResponseMessageBuilder().code(404).message(ApiCodeMessage._404).build());
		response.add(new ResponseMessageBuilder().code(400).message(ApiCodeMessage._400).build());
		response.add(new ResponseMessageBuilder().code(500).message(ApiCodeMessage._500).build());

		return new Docket(DocumentationType.SWAGGER_2).forCodeGeneration(true).ignoredParameterTypes(ApiIgnore.class).useDefaultResponseMessages(false)
				.globalResponseMessage(RequestMethod.GET, response).globalResponseMessage(RequestMethod.POST, response)
				.globalResponseMessage(RequestMethod.PUT, response).globalResponseMessage(RequestMethod.DELETE, response)
				.globalResponseMessage(RequestMethod.PATCH, response).globalResponseMessage(RequestMethod.HEAD, response).host(getHost(swaggerInfo))
				.securitySchemes(apiKeys()).securityContexts(securityContexts()).select().apis(Predicates.or(predicates)).paths(PathSelectors.any()).build()
				.apiInfo(apiInfo);
	}

	/**
	 * Api keys.
	 *
	 * @return the list
	 */
	private List<ApiKey> apiKeys() {
		final List<ApiKey> apiKeys = new ArrayList<>();
		apiKeys.add(new ApiKey("WMS Auth Header", CommonConstants.AUTH_HEADER, CommonConstants.AUTH_HEADER));
		return apiKeys;
	}

	/**
	 * Security contexts.
	 *
	 * @return the list
	 */
	private List<SecurityContext> securityContexts() {
		final List<SecurityContext> securityContext = new ArrayList<>();
		securityContext.add(SecurityContext.builder().securityReferences(defaultAuth()).forPaths(PathSelectors.any()).build());
		return securityContext;
	}

	/**
	 * Default auth.
	 *
	 * @return the list
	 */
	public List<SecurityReference> defaultAuth() {
		final AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		final AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;

		final List<SecurityReference> securityRefereence = new ArrayList<>();
		securityRefereence.add(new SecurityReference("WMS Auth Header", authorizationScopes));
		return securityRefereence;
	}

	/**
	 * Security.
	 *
	 * @return the security configuration
	 */
	@Bean
	public SecurityConfiguration security() {
		return SecurityConfigurationBuilder.builder().clientId(null).clientSecret(null).realm(null).appName("WMS Application").scopeSeparator(",")
				.additionalQueryStringParams(null).useBasicAuthenticationWithAccessCodeGrant(false).build();
	}

	/**
	 * Gets the swagger file info.
	 *
	 * @param module the module
	 * @return the swagger file info
	 */
	public SwaggerApiInfo getSwaggerFileInfo(String module) {
		try {
			final Yaml yml = new Yaml();
			final char fileSeparator = File.separatorChar;

			final String filePath = new StringBuilder().append("config").append(fileSeparator).append("swagger").append(fileSeparator).append("swagger-")
					.append(module).append(".yml").toString();
			Resource resource = new ClassPathResource(filePath);

			_LOGGER.info("Load Swagger Config From File : " + filePath);

			InputStream inputStream = resource.getInputStream();
			Reader reader = new InputStreamReader(inputStream, "UTF-8");

			return yml.loadAs(reader, SwaggerApiInfo.class);
		} catch (final Exception e) {
			_LOGGER.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Gets the host.
	 *
	 * @param swaggerInfo the swagger info
	 * @return the host
	 */
	private String getHost(SwaggerApiInfo swaggerInfo) {
		String host = swaggerInfo.getHost();
//		if (swaggerInfo != null) {
//			if (ContextUtils.getExternalConfig().getSwaggerHost() != null) {
//				host = ContextUtils.getExternalConfig().getSwaggerHost() + ":" + ContextUtils.getExternalConfig().getAppPorts().get(ContextUtils.getBootName());
//			} else if (swaggerInfo.getContact() != null && swaggerInfo.getContact().getUrl() != null) {
//				host = swaggerInfo.getContact().getUrl();
//			}
//		}
		return host;
	}

}
