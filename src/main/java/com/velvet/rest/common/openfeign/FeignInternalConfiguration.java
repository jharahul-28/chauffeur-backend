package com.velvet.rest.common.openfeign;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import com.velvet.rest.common.constants.CommonConstants;

import feign.RequestInterceptor;

/**
 * The Class FeignCustomConfiguration.
 *
 * @author Mohit
 * @version $Id: $Id
 */
public class FeignInternalConfiguration {

	@Value("${auth.header}")
	private String authHeader;

	@Bean
	public RequestInterceptor requestInterceptor() {
		return requestTemplate -> {
			requestTemplate.header(CommonConstants.AUTH_HEADER, authHeader);
		};
	}
}
