package com.velvet.rest.services.booking.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.velvet.rest.common.dbsupport.pagination.PageAndSortRequest.SortDirection;
import com.velvet.rest.common.dbsupport.pagination.PageAndSortResult;
import java.util.List;

import com.velvet.rest.common.websupport.base.BaseController;
import com.velvet.rest.services.booking.beans.BookingBean;
import com.velvet.rest.services.booking.beans.BookingRequest;
import com.velvet.rest.services.booking.beans.BookingWaypointBean;
import com.velvet.rest.services.booking.beans.WaypointRequest;
import com.velvet.rest.services.booking.entity.BookingWaypointEntity;
import com.velvet.rest.services.booking.entity.BookingEntity;
import com.velvet.rest.services.booking.services.BookingService;
import com.velvet.rest.services.common.mapper.BaseMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * The Class BookingController.
 */
@RestController
@Api(tags = "Booking Services", description = "Manage bookings.")
public class BookingController implements BaseController {

	private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

	@Autowired
	private BookingService bookingService;

	@Autowired
	private BaseMapper<?, ?> bookingMapper;

	@ApiOperation(value = "Get booking by ID", nickname = "getBookingById")
	@GetMapping("/bookings/{id}")
	public ResponseEntity<BookingBean> getBookingById(@PathVariable Long id) {
		BookingEntity booking = bookingService.getById(id);
		return ResponseEntity.ok(getMapper().mapSrcToDest(booking, BookingBean.newInstance()));
	}

	@ApiOperation(value = "Get all requested bookings paginated", nickname = "getRequestedBookings")
	@GetMapping("/bookings/requested")
	public PageAndSortResult<BookingBean> getRequestedBookings(@RequestParam(defaultValue = "1") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize, @RequestParam(required = false) String sortBy,
			@RequestParam(required = false) SortDirection sortDirection) {
		java.util.Map<String, SortDirection> sort = null;
		if (sortBy != null) {
			sort = buildSortMap(sortBy, sortDirection);
		}
		final PageAndSortResult<BookingEntity> results = bookingService.getRequestedBookings(pageSize, pageNo, sort);
		PageAndSortResult<BookingBean> response = new PageAndSortResult<>();
		if (results != null && results.getData() != null) {
			response = prepareResponsePageData(results, BookingBean.class);
		}
		return response;
	}

	@ApiOperation(value = "Create or update a booking (multi-phase)", nickname = "addBooking")
	@PostMapping("/bookings/createOrUpdate")
	public ResponseEntity<BookingBean> addBooking(@RequestBody BookingRequest request) {
		logger.info("BookingController.addBooking: bookingId={}, customerId={}", request.getBookingId(), request.getCustomerId());
		BookingEntity booking = bookingService.addBooking(request);
		return ResponseEntity.ok(getMapper().mapSrcToDest(booking, BookingBean.newInstance()));
	}

	@ApiOperation(value = "Get all active waypoints for a booking", nickname = "getWaypoints")
	@GetMapping("/bookings/{bookingId}/waypoints")
	public List<BookingWaypointBean> getWaypoints(@PathVariable Long bookingId) {
		List<BookingWaypointEntity> waypoints = bookingService.getActiveWaypoints(bookingId);
		return waypoints.stream()
				.map(w -> getMapper().mapSrcToDest(w, BookingWaypointBean.newInstance()))
				.collect(java.util.stream.Collectors.toList());
	}

	@ApiOperation(value = "Add a waypoint to a booking", nickname = "addWaypoint")
	@PostMapping("/bookings/{bookingId}/waypoints")
	public ResponseEntity<BookingBean> addWaypoint(@PathVariable Long bookingId, @Valid @RequestBody WaypointRequest request) {
		logger.info("BookingController.addWaypoint: bookingId={}, type={}, sequence={}", bookingId, request.getWaypointType(), request.getWaypointSequence());
		BookingEntity booking = bookingService.addWaypoint(bookingId, request);
		return ResponseEntity.ok(getMapper().mapSrcToDest(booking, BookingBean.newInstance()));
	}

	@ApiOperation(value = "Remove a waypoint from a booking", nickname = "removeWaypoint")
	@DeleteMapping("/bookings/{bookingId}/waypoints/{waypointId}")
	public ResponseEntity<BookingBean> removeWaypoint(@PathVariable Long bookingId, @PathVariable Long waypointId) {
		logger.info("BookingController.removeWaypoint: bookingId={}, waypointId={}", bookingId, waypointId);
		BookingEntity booking = bookingService.removeWaypoint(bookingId, waypointId);
		return ResponseEntity.ok(getMapper().mapSrcToDest(booking, BookingBean.newInstance()));
	}

	@Override
	public BaseMapper<?, ?> getMapper() {
		return bookingMapper;
	}
}
