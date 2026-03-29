package com.velvet.rest.services.booking.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.velvet.rest.common.websupport.base.BaseController;
import com.velvet.rest.services.booking.beans.RideJourneyBean;
import com.velvet.rest.services.booking.entity.RideJourneyEntity;
import com.velvet.rest.services.booking.services.ChauffeurRideService;
import com.velvet.rest.services.common.mapper.BaseMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "Chauffeur Ride Services")
public class ChauffeurRideController implements BaseController {

	private static final Logger logger = LoggerFactory.getLogger(ChauffeurRideController.class);

	@Autowired
	private ChauffeurRideService chauffeurRideService;

	@Autowired
	private BaseMapper<?, ?> bookingMapper;

	@ApiOperation(value = "Chauffeur marks himself as on-the-way to pickup", nickname = "driverOnTheWay")
	@PostMapping("/chauffeur/rides/{bookingVehicleId}/driver-on-the-way")
	public ResponseEntity<RideJourneyBean> driverOnTheWay(@PathVariable Long bookingVehicleId) {
		RideJourneyEntity journey = chauffeurRideService.markDriverOnTheWay(bookingVehicleId);
		return ResponseEntity.ok(getMapper().mapSrcToDest(journey, RideJourneyBean.newInstance()));
	}

	@ApiOperation(value = "Get full ride journey (status history) for a booking", nickname = "getRideJourney")
	@GetMapping("/bookings/{bookingId}/ride-journey")
	public List<RideJourneyBean> getRideJourney(@PathVariable Long bookingId) {
		List<RideJourneyEntity> journey = chauffeurRideService.getRideJourney(bookingId);
		return journey.stream()
				.map(e -> getMapper().mapSrcToDest(e, RideJourneyBean.newInstance()))
				.collect(Collectors.toList());
	}

	@Override
	public BaseMapper<?, ?> getMapper() {
		return bookingMapper;
	}
}
