package com.velvet.rest.services.whatsapp.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import feign.RequestInterceptor;
import feign.Retryer;

/**
 * Feign configuration for external Whapi API calls.
 * Adds Bearer token from application.yml.
 * NOT annotated with @Configuration — scoped to WhapiApiClient only.
 */
public class FeignWhapiConfiguration {

	@Value("${client.whapi.token}")
	private String whapiToken;

	@Bean
	public RequestInterceptor whapiAuthInterceptor() {
		return requestTemplate -> {
			requestTemplate.header("Authorization", "Bearer " + whapiToken);
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
