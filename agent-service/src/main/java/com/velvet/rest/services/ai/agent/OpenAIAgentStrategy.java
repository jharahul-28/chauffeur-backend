package com.velvet.rest.services.ai.agent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.velvet.rest.services.ai.config.OpenAIConfig;
import com.velvet.rest.services.ai.beans.TaskRequest;
import com.velvet.rest.services.ai.beans.TaskResponse;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public abstract class OpenAIAgentStrategy implements AgentStrategy {

	@Autowired
	private ObjectMapper jacksonMapper;

	@Autowired
	protected OpenAIConfig openAIConfig;

	@Autowired
	protected OpenAiService openAiService;

	@Override
	public TaskResponse execute(TaskRequest request) {
		LocalDateTime startTime = LocalDateTime.now();
		String taskId = UUID.randomUUID().toString();

		try {
			log.info("Executing task {} with type: {}", taskId, request.getTaskType());

			// Build the prompt based on task type
			String prompt = buildPrompt(request);

			// Create chat completion request
			ChatCompletionRequest chatRequest = ChatCompletionRequest.builder().model(openAIConfig.getModel())
					.messages(List.of(new ChatMessage("user", prompt))).maxTokens(openAIConfig.getMaxTokens()).temperature(openAIConfig.getTemperature())
					.build();

			// Execute the request
			String result = openAiService.createChatCompletion(chatRequest).getChoices().get(0).getMessage().getContent();
			
			log.info("Task {} executed successfully. Result: {}", taskId, result);

			LocalDateTime endTime = LocalDateTime.now();
			long executionTime = java.time.Duration.between(startTime, endTime).toMillis();

			return TaskResponse.builder().taskId(taskId).taskType(request.getTaskType()).status("SUCCESS").result(mapJsonAsObject(result, getResponse()))
					.startTime(startTime).endTime(endTime).executionTimeMs(executionTime).build();

		} catch (Exception e) {
			log.error("Error executing task {}: {}", taskId, e.getMessage(), e);

			LocalDateTime endTime = LocalDateTime.now();
			long executionTime = java.time.Duration.between(startTime, endTime).toMillis();

			return TaskResponse.builder().taskId(taskId).taskType(request.getTaskType()).status("ERROR").errorMessage(e.getMessage()).startTime(startTime)
					.endTime(endTime).executionTimeMs(executionTime).build();
		}
	}

	public <T> T mapJsonAsObject(String json, Class<T> clazz) {
		T t = null;
		try {
			// Clean the JSON response by removing markdown code blocks
			String cleanedJson = json;
			if (json != null) {
				// Remove ```json at the beginning and ``` at the end
				cleanedJson = json.replaceAll("^```json\\s*", "").replaceAll("\\s*```$", "");
				// Also handle generic code blocks
				cleanedJson = cleanedJson.replaceAll("^```\\s*", "").replaceAll("\\s*```$", "");
				
				// Check if the cleaned response looks like JSON
				cleanedJson = cleanedJson.trim();
				if (!cleanedJson.startsWith("{") && !cleanedJson.startsWith("[")) {
					log.warn("AI response is not JSON format: {}", cleanedJson.substring(0, Math.min(200, cleanedJson.length())));
					return null; // Return null instead of trying to parse non-JSON
				}
			}
			t = jacksonMapper.readValue(cleanedJson, clazz);
		} catch (IOException e) {
			log.error("Failed to parse JSON response: {}", json.substring(0, Math.min(200, json.length())), e);
		}
		return t;
	}

	public <T> T mapJsonAsList(String json, TypeReference<T> typeRef) {
		T t = null;
		try {
			// Clean the JSON response by removing markdown code blocks
			String cleanedJson = json;
			if (json != null) {
				// Remove ```json at the beginning and ``` at the end
				cleanedJson = json.replaceAll("^```json\\s*", "").replaceAll("\\s*```$", "");
				// Also handle generic code blocks
				cleanedJson = cleanedJson.replaceAll("^```\\s*", "").replaceAll("\\s*```$", "");
			}
			t = jacksonMapper.readValue(cleanedJson, typeRef);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return t;
	}

	/**
	 * Build the prompt for the specific task type
	 */
	protected abstract String buildPrompt(TaskRequest request);

	/**
	 * Get additional context for the task type
	 */
	protected String getTaskContext() {
		return "";
	}
}