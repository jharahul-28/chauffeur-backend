
package com.velvet.rest.services.util.beans;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class GoogleMapsAutoCompleteResponse {

	@JsonProperty("suggestions")
	public List<Suggestion> suggestions;

	@Data
	public static class Suggestion {

		@JsonProperty("placePrediction")
		public PlacePrediction placePrediction;

	}

	@Data
	public static class PlacePrediction {

		@JsonProperty("place")
		public String place;

		@JsonProperty("placeId")
		public String placeId;

		@JsonProperty("text")
		public Text text;

		@JsonProperty("structuredFormat")
		public StructuredFormat structuredFormat;

		@JsonProperty("types")
		public List<String> types;

		@JsonProperty("distanceMeters")
		public Integer distanceMeters;

	}

	@Data
	public static class StructuredFormat {

		@JsonProperty("mainText")
		public MainText mainText;

		@JsonProperty("secondaryText")
		public SecondaryText secondaryText;

	}

	@Data
	public static class MainText {

		@JsonProperty("text")
		public String text;

		@JsonProperty("matches")
		public List<Match> matches;

	}

	@Data
	public static class Match {

		@JsonProperty("endOffset")
		public Integer endOffset;

	}

	@Data
	public static class SecondaryText {

		@JsonProperty("text")
		public String text;

	}

	@Data
	public static class Text {

		@JsonProperty("text")
		public String text;

		@JsonProperty("matches")
		public List<Match> matches;

	}

}
