
package com.velvet.rest.services.util.beans;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AutoCompleteResponse {

	@JsonProperty("suggestions")
	private List<Suggestion> suggestions;

	@Data
	public static class Suggestion {

		private String mainText;

		private String secondaryText;

		private String placeId;

	}
}
