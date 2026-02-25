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
public class GoogleMapsNearbySearchRequest {

	private List<String> includedTypes;
	private Integer maxResultCount;
	private LocationRestriction locationRestriction;
	private String rankPreference;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class LocationRestriction {
		private Circle circle;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Circle {
		private Location center;
		private Double radius;
	}
}
