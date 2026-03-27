package com.velvet.rest.services.booking.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.velvet.rest.common.websupport.base.BaseController;
import com.velvet.rest.services.booking.beans.BookingVehicleBean;
import com.velvet.rest.services.booking.beans.BookingVehicleRequest;
import com.velvet.rest.services.booking.entity.BookingVehicleEntity;
import com.velvet.rest.services.booking.services.BookingService;
import com.velvet.rest.services.common.mapper.BaseMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "Vehicle Assignment", description = "Assign vehicles to bookings.")
public class VehicleAssignmentController implements BaseController {

	@Autowired
	private BookingService bookingService;

	@Autowired
	private BaseMapper<?, ?> bookingMapper;

	@ApiOperation(value = "Create or update a vehicle assignment", nickname = "assignVehicle")
	@PostMapping("/vehicle-assignments/createOrUpdate")
	public ResponseEntity<BookingVehicleBean> assignVehicle(@RequestBody BookingVehicleRequest request) {
		BookingVehicleEntity assignedVehicle = bookingService.assignVehicle(request);
		return ResponseEntity.ok(getMapper().mapSrcToDest(assignedVehicle, BookingVehicleBean.newInstance()));
	}

	@ApiOperation(value = "Get all vehicle assignments for a booking", nickname = "getVehicleAssignments")
	@GetMapping("/bookings/{bookingId}/vehicle-assignments")
	public List<BookingVehicleBean> getVehicleAssignments(@PathVariable Long bookingId) {
		return bookingService.getVehicleAssignments(bookingId).stream()
				.map(v -> getMapper().mapSrcToDest(v, BookingVehicleBean.newInstance()))
				.collect(Collectors.toList());
	}

	@ApiOperation(value = "Remove a vehicle assignment", nickname = "removeVehicleAssignment")
	@DeleteMapping("/vehicle-assignments/{id}")
	public ResponseEntity<Void> removeVehicleAssignment(@PathVariable Long id) {
		bookingService.removeVehicleAssignment(id);
		return ResponseEntity.ok().build();
	}

	@Override
	public BaseMapper<?, ?> getMapper() {
		return bookingMapper;
	}
}
