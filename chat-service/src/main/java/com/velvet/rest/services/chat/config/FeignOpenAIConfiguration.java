package com.velvet.rest.services.chat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import feign.RequestInterceptor;
import feign.Retryer;

/**
 * Feign configuration for external OpenAI API calls.
 * Adds Bearer token from application.yml.
 * NOT annotated with @Configuration — scoped to OpenAIClient only.
 */
public class FeignOpenAIConfiguration {

	@Autowired
	private OpenAIConfig openAIConfig;

	@Bean
	public RequestInterceptor whapiAuthInterceptor() {
		return requestTemplate -> {
			requestTemplate.header("Authorization", "Bearer " + openAIConfig.getApiKey());
			requestTemplate.header("Content-Type", "application/json");
		};
	}

	@Bean
	public Retryer retryer() {
	    return new Retryer.Default(
	        1000,   // initial interval
	        2000,   // max interval
	        2       // max attempts
	    );
	}
}
