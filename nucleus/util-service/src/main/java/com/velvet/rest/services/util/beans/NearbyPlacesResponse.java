package com.velvet.rest.services.util.beans;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NearbyPlacesResponse {

	private List<NearbyPlace> restaurants;
	private List<NearbyPlace> activities;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class NearbyPlace {
		private String id;
		private String name;
		private String address;
		private Double latitude;
		private Double longitude;
		private Double rating;
		private Integer userRatingCount;
		private String googleMapsUri;
		private List<String> types;
		private String photoUrl;
	}
}
