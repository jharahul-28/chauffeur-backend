package com.velvet.rest.services.chat.services;

import org.springframework.stereotype.Service;

import com.velvet.rest.services.chat.beans.OpenAIResponsesResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OpenAIResponsesService {

	public String getReply(OpenAIResponsesResponse response) {
		if (response == null) {
			log.warn("OpenAI response is null");
			return null;
		}
		if (response.getError() != null) {
			log.error("OpenAI returned error: [{}] {}", response.getError().getCode(), response.getError().getMessage());
			return null;
		}
		String text = response.getFirstTextContent();
		if (text == null || text.isBlank()) {
			log.warn("OpenAI response contained no text content, status={}", response.getStatus());
		}
		return text;
	}

	public String getReasoningSummary(OpenAIResponsesResponse response) {
		if (response == null) return null;
		return response.getFirstReasoningSummaryText();
	}
}
