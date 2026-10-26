
package com.velvet.rest.services.util.beans;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class PlaceResponse {

	private String formattedAddress;

	private Location location;

	private double rating;

	private String googleMapsUri;

	private DisplayName displayName;

	private String photo;

	private List<String> types;

	private String priceLevel;

	private Integer userRatingCount;

	private String internationalPhoneNumber;

	private String websiteUri;

	private Boolean servesVegetarianFood;

	@Data
	public static class DisplayName {

		private String text;

		private String languageCode;
	}

}
