package com.velvet.rest.common.monitor.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.velvet.rest.common.constants.CommonConstants;
import com.velvet.rest.common.monitor.handler.LoggingInterceptor;
import com.velvet.rest.common.monitor.handler.SessionInterceptor;

/**
 * The Class RestHandlerInterceptorConfig.
 *
 * @author Mohit
 * @version $Id: $Id
 */
@Configuration
public class RestHandlerInterceptorConfig implements WebMvcConfigurer {

	/** {@inheritDoc} */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LoggingInterceptor());
		registry.addInterceptor(new SessionInterceptor());
	}

	/** {@inheritDoc} */
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry
			.addMapping("/**")
			.allowedOrigins("*")
			.allowedHeaders("*")
			.allowedMethods("*")
			.exposedHeaders(CommonConstants.exposeHeaders);
	}

}
