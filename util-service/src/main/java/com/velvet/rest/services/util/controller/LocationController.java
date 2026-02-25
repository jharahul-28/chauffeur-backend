package com.velvet.rest.services.util.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.velvet.rest.common.websupport.base.BaseController;
import com.velvet.rest.services.common.mapper.BaseMapper;
import com.velvet.rest.services.util.beans.CityResponse;
import com.velvet.rest.services.util.entity.CityEntity;
import com.velvet.rest.services.util.services.CityService;

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
@Api(tags = "Location Services", description = "Manage location related services.")
public class LocationController implements BaseController {

	private static final Logger logger = LoggerFactory.getLogger(LocationController.class);

	@Autowired
	private BaseMapper<?, ?> locationMapper;

	@Autowired
	private CityService cityService;

	@ApiOperation(value = "Search cities by name", nickname = "searchCities")
	@GetMapping("/utils/locations/cities/search")
	public ResponseEntity<List<CityResponse>> searchCities(@ApiParam(value = "Search query", required = true) @RequestParam String query,
			@ApiParam(value = "Country ID (optional)") @RequestParam(required = false) Long countryId) {

		List<CityEntity> cities = cityService.searchCities(query);
		List<CityResponse> response = locationMapper.mapAsList(cities, CityResponse.class);

		return ResponseEntity.ok(response);
	}

	@ApiOperation(value = "Fetch cities by IDs", nickname = "fetchCitiesById")
	@GetMapping("/utils/cities")
	public ResponseEntity<List<CityResponse>> getCitiesByIds(@ApiParam(value = "Comma-separated city IDs", required = true) @RequestParam String ids) {
		try {
			List<Long> cityIds = Arrays.stream(ids.split(",")).map(String::trim).map(Long::valueOf).collect(Collectors.toList());
			List<CityEntity> cities = cityService.findRecordByIds(cityIds);
			List<CityResponse> response = locationMapper.mapAsList(cities, CityResponse.class);

			return ResponseEntity.ok(response);
		} catch (NumberFormatException e) {
			logger.error("Invalid city IDs format: " + ids, e);
			return ResponseEntity.badRequest().build();
		}
	}

	@ApiOperation(value = "Get city by ID", nickname = "getCityById")
	@GetMapping("/utils/cities/{id}")
	public ResponseEntity<CityResponse> getCityById(@ApiParam(value = "City ID", required = true) @PathVariable Long id) {
		CityEntity city = cityService.findRecordById(id);
		if (city == null) {
			return ResponseEntity.notFound().build();
		}
		CityResponse response = locationMapper.mapSrcToDest(city, new CityResponse());
		return ResponseEntity.ok(response);
	}

	@Override
	public BaseMapper<?, ?> getMapper() {
		return locationMapper;
	}
}
