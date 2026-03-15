package com.velvet.rest.services.chat.config;

import lombok.Data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class OpenAIConfig {

	@Value("${agent.openai.api-key}")
	private String apiKey;

	@Value("${agent.openai.base-url}")
	private String baseUrl = "https://api.openai.com/v1";

	@Value("${agent.openai.model}")
	private String model = "gpt-4";

	@Value("${agent.openai.max-tokens}")
	private Integer maxTokens = 5000;

	@Value("${agent.openai.temperature}")
	private Double temperature = 0.7;

	@Value("${agent.openai.timeout}")
	private Integer timeout = 60;
}