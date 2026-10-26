package com.velvet.rest.services.chat.clients;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.velvet.rest.common.logging.AppLogger;
import com.velvet.rest.common.logging.LoggingManager;
import com.velvet.rest.common.utils.ParamsUtils;
import com.velvet.rest.services.chat.beans.OpenAIResponsesRequest;
import com.velvet.rest.services.chat.beans.OpenAIResponsesRequest.Reasoning;
import com.velvet.rest.services.chat.beans.OpenAIResponsesResponse;
import com.velvet.rest.services.chat.config.OpenAIConfig;

@Service
public class ChatClientService {
	private static final AppLogger _LOGGER = LoggingManager.getLogger(ChatClientService.class);

	@Autowired
	private OpenAIClient openAIClient;

	@Autowired
	private OpenAIConfig openAIConfig;
	
    public OpenAIResponsesResponse generateResponse(String phone, String instructions, List<Map<String,String>> messages) {
    	try {
    		_LOGGER.info(String.format("sending message to openai for user: %s \nmessages: %s", phone, messages));

    		Reasoning reasoning = null;
    		if (ParamsUtils.getBooleanParam("OPENAI_REASONING_ENABLED", false)) {
    			reasoning = Reasoning.builder()
    					.effort("low")
    					.summary("auto")
    					.build();
    		}

    		OpenAIResponsesRequest request = OpenAIResponsesRequest.builder()
    				.model(openAIConfig.getModel())
    				.input(messages)
    				.instructions(instructions)
    				.reasoning(reasoning)
    				.build();

    		return openAIClient.sendMessage(request);
    	} catch (Exception e) {
    		_LOGGER.error(String.format("Failed to fetch OpenAi Responses Response user for phone: %s", phone), e);
    	}
    	return null;
    }
	

}
