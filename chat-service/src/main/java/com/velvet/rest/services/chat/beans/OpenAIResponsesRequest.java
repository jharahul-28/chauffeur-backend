package com.velvet.rest.services.chat.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenAIResponsesRequest {

	private String model;

	private String instructions;

	private Object input;

	private boolean store;

	private Reasoning reasoning;

	@Data
	@Builder
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Reasoning {

		@JsonProperty("effort")
		private String effort;

		@JsonProperty("summary")
		private String summary;
	}
}
