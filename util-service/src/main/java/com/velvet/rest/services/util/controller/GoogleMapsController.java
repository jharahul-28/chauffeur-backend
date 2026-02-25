package com.velvet.rest.services.util.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.velvet.rest.common.beans.ErrorBean;
import com.velvet.rest.common.exceptions.ValidationException;
import com.velvet.rest.common.utils.CollectionUtils;
import com.velvet.rest.common.websupport.base.BaseController;
import com.velvet.rest.services.common.mapper.BaseMapper;
import com.velvet.rest.services.util.beans.AutoCompleteResponse;
import com.velvet.rest.services.util.beans.GoogleMapsAutoCompleteResponse;
import com.velvet.rest.services.util.beans.GoogleMapsPlaceResponse;
import com.velvet.rest.services.util.beans.Location;
import com.velvet.rest.services.util.beans.NearbyPlacesResponse;
import com.velvet.rest.services.util.beans.PlaceResponse;
import com.velvet.rest.services.util.entity.CityEntity;
import com.velvet.rest.services.util.services.CityService;
import com.velvet.rest.services.util.services.GoogleMapsService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * The Class LocationController.
 *
 * @author Mohit
 * @version $Id: $Id
 */
@RestController
@Api(tags = "Google Maps Services", description = "Manage Google Maps related services.")
public class GoogleMapsController implements BaseController {

	private static final Logger logger = LoggerFactory.getLogger(GoogleMapsController.class);

	@Autowired
	private BaseMapper<?, ?> mapper;

	@Autowired
	private CityService cityService;

	@Autowired
	private GoogleMapsService googleMapsService;

	@ApiOperation(value = "Search places using Google Maps API", nickname = "searchPlaces")
	@GetMapping("/utils/google/places/search")
	public ResponseEntity<AutoCompleteResponse> searchPlaces(@ApiParam(value = "Search query", required = true) @RequestParam String query,
			@ApiParam(value = "City id") @RequestParam(required = false) Long cityId,
			@ApiParam(value = "Location bias (lat,lng)") @RequestParam(required = false) Location location,
			@ApiParam(value = "Search radius in meters") @RequestParam(required = false, defaultValue = "50000") Integer radius,
			@ApiParam(value = "Place types (comma-separated)") @RequestParam(required = false) String types) {
		try {
			CityEntity city = null;
			if (cityId != null) {
				city = cityService.findRecordById(cityId);
			}
			GoogleMapsAutoCompleteResponse places = googleMapsService.searchGooglePlaces(query, city, location, radius, types);
			// Build AutoCompleteResponse from GoogleMapsAutoCompleteResponse
			AutoCompleteResponse response = new AutoCompleteResponse();
			if (CollectionUtils.isNotEmpty(places.getSuggestions())) {
				List<AutoCompleteResponse.Suggestion> suggestions = new ArrayList<>();
				places.getSuggestions().stream().forEach(p -> {
					AutoCompleteResponse.Suggestion suggestion = new AutoCompleteResponse.Suggestion();
					suggestion.setPlaceId(p.getPlacePrediction().getPlaceId());
					suggestion.setMainText(p.getPlacePrediction().getStructuredFormat().getMainText().getText());
					suggestion.setSecondaryText(p.getPlacePrediction().getStructuredFormat().getSecondaryText().getText());
					suggestions.add(suggestion);
				});
				response.setSuggestions(suggestions);
			}
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			logger.error("Error searching Google Places: ", e);
			throw new ValidationException(new ErrorBean("Something went wrong while searching Places", e.getMessage()));
		}
	}

	@ApiOperation(value = "Get place details from Google Maps API", nickname = "getPlaceDetails")
	@GetMapping("/utils/google/places/{placeId}")
	public ResponseEntity<?> getPlaceDetails(@ApiParam(value = "Google Place ID", required = true) @PathVariable String placeId,
			@ApiParam(value = "Fields to return (comma-separated)") @RequestParam(required = false) String fields) {
		try {
			GoogleMapsPlaceResponse placeDetails = googleMapsService.getGooglePlaceDetails(placeId);
			PlaceResponse placeResponse = mapper.mapSrcToDest(placeDetails, new PlaceResponse());

			// Explicitly set fields (mapper might not copy ArrayList to List)
			if (placeDetails.getTypes() != null) {
				placeResponse.setTypes(placeDetails.getTypes());
			}

			// Explicitly set new fields
			placeResponse.setPriceLevel(placeDetails.getPriceLevel());
			placeResponse.setUserRatingCount(placeDetails.getUserRatingCount());
			placeResponse.setInternationalPhoneNumber(placeDetails.getInternationalPhoneNumber());
			placeResponse.setWebsiteUri(placeDetails.getWebsiteUri());
			placeResponse.setServesVegetarianFood(placeDetails.getServesVegetarianFood());

			// Get photo as byte array and convert to base64 for JSON response
			if (placeDetails.getPhotos() != null && !placeDetails.getPhotos().isEmpty()) {
				String photo = googleMapsService.getGoogleMapsPhoto(placeDetails.getPhotos().get(0).getName());
				placeResponse.setPhoto(photo);
			}
			return ResponseEntity.ok(placeResponse);
		} catch (Exception e) {
			logger.error("Error getting place details: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error getting place details: " + e.getMessage());
		}
	}

	@ApiOperation(value = "Geocode address using Google Maps API", nickname = "geocodeAddress")
	@GetMapping("/utils/google/geocode")
	public ResponseEntity<?> geocodeAddress(@ApiParam(value = "Address to geocode", required = true) @RequestParam String address,
			@ApiParam(value = "Region bias (country code)") @RequestParam(required = false) String region) {
		try {
			Object geocodeResult = googleMapsService.geocodeAddress(address, region);
			return ResponseEntity.ok(geocodeResult);
		} catch (Exception e) {
			logger.error("Error geocoding address: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error geocoding address: " + e.getMessage());
		}
	}

	@ApiOperation(value = "Reverse geocode coordinates using Google Maps API", nickname = "reverseGeocode")
	@GetMapping("/utils/google/reverse-geocode")
	public ResponseEntity<?> reverseGeocode(@ApiParam(value = "Latitude", required = true) @RequestParam Double lat,
			@ApiParam(value = "Longitude", required = true) @RequestParam Double lng,
			@ApiParam(value = "Result types (comma-separated)") @RequestParam(required = false) String resultTypes) {
		try {
			Object reverseGeocodeResult = googleMapsService.reverseGeocode(lat, lng, resultTypes);
			return ResponseEntity.ok(reverseGeocodeResult);
		} catch (Exception e) {
			logger.error("Error reverse geocoding: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reverse geocoding: " + e.getMessage());
		}
	}

	@ApiOperation(value = "Search for nearby restaurants and activity centers", nickname = "searchRestaurantsAndActivities")
	@GetMapping("/utils/google/places/nearby/recommendations")
	public ResponseEntity<NearbyPlacesResponse> searchRestaurantsAndActivities(
			@ApiParam(value = "Latitude (optional if cityName provided)") @RequestParam(required = false) Double lat,
			@ApiParam(value = "Longitude (optional if cityName provided)") @RequestParam(required = false) Double lng,
			@ApiParam(value = "City name (optional if lat/lng provided)") @RequestParam(required = false) String cityName,
			@ApiParam(value = "Search radius in meters (default: 5000)") @RequestParam(required = false, defaultValue = "5000") Integer radius) {
		try {
			// If lat/lng not provided, fetch from city name
			if ((lat == null || lng == null) && cityName != null && !cityName.trim().isEmpty()) {
				CityEntity city = cityService.findByName(cityName.trim());
				if (city == null) {
					logger.error("City not found with name: {}", cityName);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
				}
				logger.info("Using coordinates from city: {} - lat: {}, lng: {}", city.getName(), lat, lng);
			}

			// Validate that we have coordinates
			if (lat == null || lng == null) {
				logger.error("Either lat/lng or cityName must be provided");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
			}

			NearbyPlacesResponse response = googleMapsService.searchRestaurantsAndActivities(lat, lng, radius);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			logger.error("Error searching nearby restaurants and activities: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@ApiOperation(value = "Get city image from Google Maps", nickname = "getCityImage")
	@GetMapping("/utils/google/cities/image")
	public ResponseEntity<String> getCityImage(@ApiParam(value = "City name", required = true) @RequestParam String cityName,
			@ApiParam(value = "Maximum width in pixels (optional, default: 400)") @RequestParam(required = false) Integer maxWidthPx,
			@ApiParam(value = "Maximum height in pixels (optional)") @RequestParam(required = false) Integer maxHeightPx) {
		try {
			String imageUrl = googleMapsService.getCityImage(cityName, maxWidthPx, maxHeightPx);
			// Return default image if no image found
			if (imageUrl == null) {
				imageUrl = "https://images.unsplash.com/photo-1528728329032-2972f65dfb3f";
			}
			return ResponseEntity.ok(imageUrl);
		} catch (Exception e) {
			logger.error("Error getting city image: ", e);
			// Return default image on error
			return ResponseEntity.ok("https://images.unsplash.com/photo-1528728329032-2972f65dfb3f");
		}
	}

	@Override
	public BaseMapper<?, ?> getMapper() {
		return mapper;
	}
}
