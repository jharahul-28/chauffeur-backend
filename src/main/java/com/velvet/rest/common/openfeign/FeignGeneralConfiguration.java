package com.velvet.rest.common.openfeign;

import org.springframework.context.annotation.Bean;

import com.velvet.rest.common.logging.AppLogger;
import com.velvet.rest.common.logging.LoggingManager;

import feign.RequestInterceptor;

/**
 * The Class FeignGeneralConfiguration.
 *
 * @author Mohit
 * @version $Id: $Id
 */
public class FeignGeneralConfiguration {

	private final AppLogger logger = LoggingManager.getLogger(FeignGeneralConfiguration.class);

	@Bean
	public RequestInterceptor requestInterceptor() {
		return requestTemplate -> {
			// Log the complete request details
			logger.info("=== Feign Request Debug ===");
			logger.info("Method: " + requestTemplate.method());
			logger.info("URL: " + requestTemplate.url());
			logger.info("Headers: " + requestTemplate.headers());
			logger.info("Body: " + (requestTemplate.body() != null ? new String(requestTemplate.body()) : "null"));
			logger.info("========================");

			// Build curl command for debugging
			StringBuilder curl = new StringBuilder("curl -X " + requestTemplate.method());
			curl.append(" '").append(requestTemplate.url()).append("'");

			requestTemplate.headers().forEach((key, values) -> {
				values.forEach(value -> {
					curl.append(" -H '").append(key).append(": ").append(value).append("'");
				});
			});

			if (requestTemplate.body() != null) {
				curl.append(" -d '").append(new String(requestTemplate.body())).append("'");
			}

			logger.info("=== Equivalent CURL Command ===");
			logger.info(curl.toString());
			logger.info("================================");
		};
	}

}
