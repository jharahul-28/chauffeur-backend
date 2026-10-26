package com.velvet.rest.services.util.clients;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.velvet.rest.services.util.beans.GoogleMapsAutoCompleteRequest;
import com.velvet.rest.services.util.beans.GoogleMapsAutoCompleteResponse;
import com.velvet.rest.services.util.beans.GoogleMapsNearbySearchRequest;
import com.velvet.rest.services.util.beans.GoogleMapsNearbySearchResponse;
import com.velvet.rest.services.util.beans.GoogleMapsPlaceResponse;

@FeignClient(value = "googleMapsClient", url = "${client.google.maps.url}")
public interface GoogleMapsClient {

	@PostMapping(value = "/places:autocomplete")
	GoogleMapsAutoCompleteResponse autoComplete(@RequestHeader Map<String, Object> headers, @RequestBody GoogleMapsAutoCompleteRequest token);

	@GetMapping(value = "/places/{placeId}")
	GoogleMapsPlaceResponse placeInfo(@RequestHeader Map<String, Object> headers, @PathVariable String placeId, @RequestParam String fields);

	@GetMapping(value = "/{photoName}/media?maxWidthPx=400")
	byte[] photo(@RequestHeader Map<String, Object> headers, @PathVariable String photoName);

	@GetMapping(value = "/{photoName}/media")
	byte[] photoWithSize(@RequestHeader Map<String, Object> headers, @PathVariable String photoName, @RequestParam(value = "maxWidthPx", required = false) Integer maxWidthPx, @RequestParam(value = "maxHeightPx", required = false) Integer maxHeightPx);

	@PostMapping(value = "/places:searchNearby")
	GoogleMapsNearbySearchResponse searchNearby(@RequestHeader Map<String, Object> headers, @RequestBody GoogleMapsNearbySearchRequest request, @RequestParam("fields") String fields);

}