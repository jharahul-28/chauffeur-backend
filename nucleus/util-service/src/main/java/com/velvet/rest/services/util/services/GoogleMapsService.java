package com.velvet.rest.services.util.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.velvet.rest.common.utils.CollectionUtils;
import com.velvet.rest.services.util.beans.GoogleMapsAutoCompleteRequest;
import com.velvet.rest.services.util.beans.GoogleMapsAutoCompleteRequest.GoogleMapsAutoCompleteRequestBuilder;
import com.velvet.rest.services.util.beans.GoogleMapsAutoCompleteResponse;
import com.velvet.rest.services.util.beans.GoogleMapsNearbySearchRequest;
import com.velvet.rest.services.util.beans.GoogleMapsNearbySearchResponse;
import com.velvet.rest.services.util.beans.GoogleMapsPlaceResponse;
import com.velvet.rest.services.util.beans.Location;
import com.velvet.rest.services.util.beans.NearbyPlacesResponse;
import com.velvet.rest.services.util.clients.UtilClientService;
import com.velvet.rest.services.util.controller.GoogleMapsController;
import com.velvet.rest.services.util.entity.CityEntity;

@Service
public class GoogleMapsService {

	private static final Logger logger = LoggerFactory.getLogger(GoogleMapsController.class);

	@Autowired
	private UtilClientService clientService;

	public GoogleMapsAutoCompleteResponse searchGooglePlaces(String query, CityEntity city, Location location, Integer radius, String types) {
		Assert.notNull(query, "Search query must not be empty");

		GoogleMapsAutoCompleteRequestBuilder builder = GoogleMapsAutoCompleteRequest.builder();
		builder.input(query);
		GoogleMapsAutoCompleteResponse response = clientService.googleMapsAutoComplete(builder.build());
		return response;
	}

	public GoogleMapsPlaceResponse getGooglePlaceDetails(String placeId) {
		GoogleMapsPlaceResponse response = clientService.googleMapsPlaceInfo(placeId);
		return response;
	}

	public Object geocodeAddress(String address, String region) {
		return null;
	}

	public Object reverseGeocode(Double lat, Double lng, String resultTypes) {
		return null;

	}

	public String getGoogleMapsPhoto(String name) {
		try {
			byte[] photoBytes = clientService.googleMapsPhoto(name);
			String base64Photo = java.util.Base64.getEncoder().encodeToString(photoBytes);
			return "data:image/jpeg;base64," + base64Photo;
		} catch (Exception e) {
			logger.error(String.format("Error fetching photo for placeId: %s", name), e);
		}
		return null;
	}

	public String getGoogleMapsPhotoWithSize(String name, Integer maxWidthPx) {
		return getGoogleMapsPhotoWithSize(name, maxWidthPx, null);
	}

	public String getGoogleMapsPhotoWithSize(String name, Integer maxWidthPx, Integer maxHeightPx) {
		try {
			byte[] photoBytes = clientService.googleMapsPhotoWithSize(name, maxWidthPx, maxHeightPx);
			String base64Photo = java.util.Base64.getEncoder().encodeToString(photoBytes);
			return "data:image/jpeg;base64," + base64Photo;
		} catch (Exception e) {
			logger.error(String.format("Error fetching photo for placeId: %s with size: %dx%d", name, maxWidthPx, maxHeightPx), e);
		}
		return null;
	}

	/**
	 * Get city image URL from Google Maps
	 * @param cityName The name of the city
	 * @return Image URL or null if not found
	 */
	public String getCityImage(String cityName) {
		return getCityImage(cityName, null);
	}

	/**
	 * Get city image URL from Google Maps with specific size
	 * @param cityName The name of the city
	 * @param maxWidthPx Maximum width in pixels (optional, defaults to 400)
	 * @return Image URL or null if not found
	 */
	public String getCityImage(String cityName, Integer maxWidthPx) {
		return getCityImage(cityName, maxWidthPx, null);
	}

	/**
	 * Get city image URL from Google Maps with specific dimensions
	 * @param cityName The name of the city
	 * @param maxWidthPx Maximum width in pixels (optional)
	 * @param maxHeightPx Maximum height in pixels (optional)
	 * @return Image URL or null if not found
	 */
	public String getCityImage(String cityName, Integer maxWidthPx, Integer maxHeightPx) {
		try {
			// Search for the city using autocomplete
			GoogleMapsAutoCompleteRequestBuilder builder = GoogleMapsAutoCompleteRequest.builder();
			builder.input(cityName);
			ArrayList<String> types = new ArrayList<>();
			types.add("locality");
			types.add("administrative_area_level_1");
			builder.includedPrimaryTypes(types);
			
			GoogleMapsAutoCompleteResponse response = clientService.googleMapsAutoComplete(builder.build());
			
			// Get the first suggestion (most relevant city)
			if (response != null && CollectionUtils.isNotEmpty(response.getSuggestions())) {
				String placeId = response.getSuggestions().get(0).getPlacePrediction().getPlaceId();
				
				// Get place details to fetch photos
				GoogleMapsPlaceResponse placeDetails = clientService.googleMapsPlaceInfo(placeId);
				
				// Get photo URL if available
				if (placeDetails != null && placeDetails.getPhotos() != null && !placeDetails.getPhotos().isEmpty()) {
					String photoName = placeDetails.getPhotos().get(0).getName();
					// Use custom size if provided, otherwise use default
					if (maxWidthPx != null && maxWidthPx > 0) {
						return getGoogleMapsPhotoWithSize(photoName, maxWidthPx, maxHeightPx);
					} else {
						return getGoogleMapsPhoto(photoName);
					}
				}
			}
			
			logger.warn(String.format("No image found for city: %s", cityName));
		} catch (Exception e) {
			logger.error(String.format("Error fetching city image for: %s", cityName), e);
		}
		
		return null;
	}

	public NearbyPlacesResponse searchRestaurantsAndActivities(Double lat, Double lng, Integer radius) {
		// Validation is now done in controller, but keep these for direct service calls
		if (lat == null || lng == null) {
			throw new IllegalArgumentException("Latitude and longitude must not be null");
		}

		Location location = new Location(lat, lng);
		Double searchRadius = radius != null ? radius.doubleValue() : 50000.0;

		// Search for restaurants
		List<NearbyPlacesResponse.NearbyPlace> restaurants = searchNearbyByType(
			location, searchRadius, Arrays.asList("restaurant"), 5
		);

		// Search for activity centers
		List<String> activityTypes = Arrays.asList(
			"amusement_center", "museum", "tourist_attraction",
			"aquarium", "zoo", "amusement_park"
		);
		List<NearbyPlacesResponse.NearbyPlace> activities = searchNearbyByType(
			location, searchRadius, activityTypes, 5
		);

		return NearbyPlacesResponse.builder()
			.restaurants(restaurants)
			.activities(activities)
			.build();
	}

	private List<NearbyPlacesResponse.NearbyPlace> searchNearbyByType(
		Location location, Double radius, List<String> types, int maxResults
	) {
		GoogleMapsNearbySearchRequest request = GoogleMapsNearbySearchRequest.builder()
			.includedTypes(types)
			.maxResultCount(maxResults)
			.locationRestriction(
				GoogleMapsNearbySearchRequest.LocationRestriction.builder()
					.circle(
						GoogleMapsNearbySearchRequest.Circle.builder()
							.center(location)
							.radius(radius)
							.build()
					)
					.build()
			)
			.rankPreference("DISTANCE")
			.build();

		GoogleMapsNearbySearchResponse response = clientService.searchNearby(request);

		if (response == null || response.getPlaces() == null) {
			return new ArrayList<>();
		}

		return response.getPlaces().stream()
			.map(place -> {
				NearbyPlacesResponse.NearbyPlace nearbyPlace = NearbyPlacesResponse.NearbyPlace.builder()
					.id(place.getId())
					.name(place.getDisplayName() != null ? place.getDisplayName().getText() : null)
					.address(place.getFormattedAddress())
					.latitude(place.getLocation() != null ? place.getLocation().getLatitude() : null)
					.longitude(place.getLocation() != null ? place.getLocation().getLongitude() : null)
					.rating(place.getRating())
					.userRatingCount(place.getUserRatingCount())
					.googleMapsUri(place.getGoogleMapsUri())
					.types(place.getTypes())
					.build();

				// Get photo URL if available
				if (place.getPhotos() != null && !place.getPhotos().isEmpty()) {
					String photoName = place.getPhotos().get(0).getName();
					nearbyPlace.setPhotoUrl(getGoogleMapsPhoto(photoName));
				}

				return nearbyPlace;
			})
			.filter(place -> place.getPhotoUrl() != null && !place.getPhotoUrl().isEmpty()) // Filter out places without images
			.limit(maxResults)
			.collect(Collectors.toList());
	}
}