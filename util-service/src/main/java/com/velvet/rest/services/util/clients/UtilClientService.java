package com.velvet.rest.services.util.clients;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.velvet.rest.common.beans.ErrorBean;
import com.velvet.rest.common.exceptions.ServerException;
import com.velvet.rest.common.logging.AppLogger;
import com.velvet.rest.common.logging.LoggingManager;
import com.velvet.rest.common.properties.Errors;
import com.velvet.rest.services.util.beans.GoogleMapsAutoCompleteRequest;
import com.velvet.rest.services.util.beans.GoogleMapsAutoCompleteResponse;
import com.velvet.rest.services.util.beans.GoogleMapsNearbySearchRequest;
import com.velvet.rest.services.util.beans.GoogleMapsNearbySearchResponse;
import com.velvet.rest.services.util.beans.GoogleMapsPlaceResponse;
import com.velvet.rest.services.util.beans.UnsplashPhotoResponse;

import feign.FeignException;
import lombok.Getter;

/**
 * @author mohitaggarwal
 */
@Service
public class UtilClientService {

	private final AppLogger _LOGGER = LoggingManager.getLogger(UtilClientService.class);

	@Autowired
	private GoogleMapsClient googleMapsClient;

	@Value("${client.google.maps.key}")
	@Getter
	private String googleMapKey;

	private Map<String, Object> getGoogleMapHeader() {
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("X-Goog-Api-Key", googleMapKey);
		return headerMap;
	}

	public GoogleMapsAutoCompleteResponse googleMapsAutoComplete(GoogleMapsAutoCompleteRequest request) {
		try {
			return googleMapsClient.autoComplete(getGoogleMapHeader(), request);
		} catch (Exception e) {
			_LOGGER.error("Error while autoComplete", e);
			throw new ServerException(new ErrorBean(Errors.SERVER_EXCEPTION, "Something went wrong. Please try again."));
		}
	}

	public GoogleMapsPlaceResponse googleMapsPlaceInfo(String placeId) {
		try {
			String fields = "displayName,formattedAddress,location,photos,rating,googleMapsUri,types,priceLevel,userRatingCount,internationalPhoneNumber,websiteUri,servesVegetarianFood";
			return googleMapsClient.placeInfo(getGoogleMapHeader(), placeId, fields);
		} catch (Exception e) {
			_LOGGER.error("Error while fetching placeInfo", e);
			throw new ServerException(new ErrorBean(Errors.SERVER_EXCEPTION, "Something went wrong. Please try again."));
		}
	}

	public byte[] googleMapsPhoto(String photoName) {
		try {
			Map<String, Object> headerMap = getGoogleMapHeader();
			return googleMapsClient.photo(headerMap, photoName);
		} catch (Exception e) {
			_LOGGER.error("Error while fetching photo", e);
			throw new ServerException(new ErrorBean(Errors.SERVER_EXCEPTION, "Something went wrong. Please try again."));
		}
	}

	public byte[] googleMapsPhotoWithSize(String photoName, Integer maxWidthPx) {
		return googleMapsPhotoWithSize(photoName, maxWidthPx, null);
	}

	public byte[] googleMapsPhotoWithSize(String photoName, Integer maxWidthPx, Integer maxHeightPx) {
		try {
			Map<String, Object> headerMap = getGoogleMapHeader();
			return googleMapsClient.photoWithSize(headerMap, photoName, maxWidthPx, maxHeightPx);
		} catch (Exception e) {
			_LOGGER.error("Error while fetching photo with size", e);
			throw new ServerException(new ErrorBean(Errors.SERVER_EXCEPTION, "Something went wrong. Please try again."));
		}
	}

	public GoogleMapsNearbySearchResponse searchNearby(GoogleMapsNearbySearchRequest request) {
		try {
			// Google Places API requires field mask to specify which fields to return
			String fieldMask = "places.id,places.displayName,places.formattedAddress,places.location,"
					+ "places.rating,places.userRatingCount,places.googleMapsUri,places.types,places.photos";
			return googleMapsClient.searchNearby(getGoogleMapHeader(), request, fieldMask);
		} catch (Exception e) {
			_LOGGER.error("Error while searching nearby places", e);
			throw new ServerException(new ErrorBean(Errors.SERVER_EXCEPTION, "Something went wrong. Please try again."));
		}
	}
}