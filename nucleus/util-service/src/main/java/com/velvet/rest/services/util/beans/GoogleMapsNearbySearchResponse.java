package com.velvet.rest.services.util.beans;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleMapsNearbySearchResponse {

	private List<Place> places;

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Place {
		private String id;
		private DisplayName displayName;
		private String formattedAddress;
		private Location location;
		private Double rating;
		private Integer userRatingCount;
		private String googleMapsUri;
		private List<String> types;
		private List<Photo> photos;
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class DisplayName {
		private String text;
		private String languageCode;
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Photo {
		private String name;
		private Integer widthPx;
		private Integer heightPx;
	}
}
