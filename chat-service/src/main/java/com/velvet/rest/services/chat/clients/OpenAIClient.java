package com.velvet.rest.services.chat.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.velvet.rest.services.chat.beans.OpenAIResponsesRequest;
import com.velvet.rest.services.chat.beans.OpenAIResponsesResponse;
import com.velvet.rest.services.chat.config.FeignOpenAIConfiguration;

@FeignClient(value = "OpenAIClient", url = "${agent.openai.base-url}", configuration = { FeignOpenAIConfiguration.class })
public interface OpenAIClient {

	@PostMapping(value = "/responses")
	OpenAIResponsesResponse sendMessage(@RequestBody OpenAIResponsesRequest request);

}