
package com.velvet.rest.services.util.beans;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class GoogleMapsPlaceResponse {

	private String formattedAddress;

	private Location location;

	private double rating;

	private String googleMapsUri;

	private DisplayName displayName;

	private ArrayList<Photo> photos;

	private ArrayList<String> types;

	private String priceLevel;

	private Integer userRatingCount;

	private String internationalPhoneNumber;

	private String websiteUri;

	private Boolean servesVegetarianFood;

	@Data
	public static class AuthorAttribution {

		private String displayName;

		private String uri;

		private String photoUri;
	}

	@Data
	public static class DisplayName {

		private String text;

		private String languageCode;
	}

	@Data
	public static class Photo {

		private String name;

		private int widthPx;

		private int heightPx;

		private ArrayList<AuthorAttribution> authorAttributions;

		private String flagContentUri;

		private String googleMapsUri;
	}
}
