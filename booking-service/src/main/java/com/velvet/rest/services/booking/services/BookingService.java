package com.velvet.rest.services.booking.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.velvet.rest.common.beans.ErrorBean;
import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.common.dbsupport.pagination.PageAndSortRequest.SortDirection;
import com.velvet.rest.common.dbsupport.pagination.PageAndSortResult;
import com.velvet.rest.common.utils.CollectionUtils;
import com.velvet.rest.common.exceptions.ValidationException;
import com.velvet.rest.common.websupport.base.BaseService;
import com.velvet.rest.services.booking.beans.BookingRequest;
import com.velvet.rest.services.booking.beans.WaypointRequest;
import com.velvet.rest.services.booking.entity.BookingWaypointConstants.WaypointType;
import com.velvet.rest.services.booking.entity.BookingConstants.Status;
import com.velvet.rest.services.booking.entity.BookingEntity;
import com.velvet.rest.services.booking.entity.BookingWaypointEntity;
import com.velvet.rest.services.booking.errors.AppErrors;
import com.velvet.rest.services.booking.repository.BookingRepository;
import com.velvet.rest.services.booking.repository.BookingWaypointRepository;

/**
 * Service for Booking operations.
 */
@Service
public class BookingService implements BaseService<BookingEntity> {

	private static final DateTimeFormatter REFERENCE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private BookingWaypointRepository bookingWaypointRepository;

	@Transactional(propagation = Propagation.REQUIRED)
	public BookingEntity addBooking(BookingRequest request) {
		if (request.getBookingId() == null) {
			return createBooking(request);
		} else {
			return updateBooking(request);
		}
	}

	private BookingEntity createBooking(BookingRequest request) {
		if (request.getCustomerId() == null) {
			throw new ValidationException(ErrorBean.withError(AppErrors.INVALID_REQUEST, "customerId is required to create a booking", "customerId"));
		}
		if (CollectionUtils.isEmpty(request.getWaypoints())) {
			throw new ValidationException(ErrorBean.withError(AppErrors.INVALID_REQUEST, "At least one waypoint is required", "waypoints"));
		}

		BookingEntity booking = BookingEntity.newInstance();
		booking.setBookingReference(generateBookingReference());
		booking.setCustomerId(request.getCustomerId());
		booking.setStatus(Status.PENDING);
		bookingRepository.save(booking);

		List<BookingWaypointEntity> waypoints = buildWaypointEntities(booking, request.getWaypoints());
		bookingWaypointRepository.saveAll(waypoints);
		booking.setWaypoints(waypoints);
		return booking;
	}

	private BookingEntity updateBooking(BookingRequest request) {
		BookingEntity booking = bookingRepository.findById(request.getBookingId())
				.orElseThrow(() -> new ValidationException(ErrorBean.withError(AppErrors.BOOKING_NOT_FOUND, "Booking not found", "bookingId")));

		if (!Status.PENDING.equals(booking.getStatus())) {
			throw new ValidationException(ErrorBean.withError(AppErrors.BOOKING_NOT_PENDING, "Booking is no longer in PENDING state", "bookingId"));
		}

		if (request.getRideDate() != null) {
			booking.setRideDate(request.getRideDate());
		}
		if (request.getRideTime() != null) {
			booking.setRideTime(request.getRideTime());
		}
		if (request.getVehicleCategoryId() != null) {
			booking.setVehicleCategoryId(request.getVehicleCategoryId());
		}
		if (request.getBeverageType() != null) {
			booking.setBeverageType(request.getBeverageType());
		}
		if (request.getSpecialRequest() != null) {
			booking.setSpecialRequest(request.getSpecialRequest());
		}

		return bookingRepository.save(booking);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public BookingEntity addWaypoint(Long bookingId, WaypointRequest request) {
		BookingEntity booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new ValidationException(ErrorBean.withError(AppErrors.BOOKING_NOT_FOUND, "Booking not found", "bookingId")));

		if (!Status.PENDING.equals(booking.getStatus())) {
			throw new ValidationException(ErrorBean.withError(AppErrors.BOOKING_NOT_PENDING, "Booking is no longer in PENDING state", "bookingId"));
		}

		int sequence;
		if (request.getWaypointSequence() != null) {
			sequence = request.getWaypointSequence();
			bookingWaypointRepository.shiftSequencesUp(bookingId, sequence);
		} else {
			Integer maxSeq = bookingWaypointRepository.findMaxSequenceByBookingId(bookingId);
			sequence = (maxSeq != null ? maxSeq : 0) + 1;
		}

		bookingWaypointRepository.save(BookingWaypointEntity.newInstance().buildWaypoint(booking, sequence, request));

		validateWaypointOrder(bookingId);

		if (WaypointType.PICKUP.equals(request.getWaypointType())) {
			booking.setPickupCount(booking.getPickupCount() + 1);
		} else {
			booking.setDropCount(booking.getDropCount() + 1);
		}
		bookingRepository.save(booking);

		return booking;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public BookingEntity removeWaypoint(Long bookingId, Long waypointId) {
		BookingEntity booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new ValidationException(ErrorBean.withError(AppErrors.BOOKING_NOT_FOUND, "Booking not found", "bookingId")));

		if (!Status.PENDING.equals(booking.getStatus())) {
			throw new ValidationException(ErrorBean.withError(AppErrors.BOOKING_NOT_PENDING, "Booking is no longer in PENDING state", "bookingId"));
		}

		BookingWaypointEntity waypoint = bookingWaypointRepository.findById(waypointId)
				.filter(w -> bookingId.equals(w.getBookingId()))
				.orElseThrow(() -> new ValidationException(ErrorBean.withError(AppErrors.WAYPOINT_NOT_FOUND, "Waypoint not found for this booking", "waypointId")));

		WaypointType type = waypoint.getWaypointType();
		long remaining = bookingWaypointRepository.countByBookingIdAndWaypointType(bookingId, type);
		if (remaining <= 1) {
			throw new ValidationException(ErrorBean.withError(AppErrors.WAYPOINT_REQUIRED,
					"Booking must have at least one " + type.name() + " waypoint", "waypointId"));
		}

		waypoint.setActive(0);
		bookingWaypointRepository.save(waypoint);

		validateWaypointOrder(bookingId);

		if (WaypointType.PICKUP.equals(type)) {
			booking.setPickupCount(booking.getPickupCount() - 1);
		} else {
			booking.setDropCount(booking.getDropCount() - 1);
		}
		bookingRepository.save(booking);

		return booking;
	}

	private void validateWaypointOrder(Long bookingId) {
		List<BookingWaypointEntity> activeWaypoints = bookingWaypointRepository.findActiveByBookingIdOrdered(bookingId);
		if (activeWaypoints.isEmpty()) return;
		if (!WaypointType.PICKUP.equals(activeWaypoints.get(0).getWaypointType())) {
			throw new ValidationException(ErrorBean.withError(AppErrors.INVALID_WAYPOINT_ORDER, "First waypoint must be a PICKUP", "waypointType"));
		}
		if (!WaypointType.DROP.equals(activeWaypoints.get(activeWaypoints.size() - 1).getWaypointType())) {
			throw new ValidationException(ErrorBean.withError(AppErrors.INVALID_WAYPOINT_ORDER, "Last waypoint must be a DROP", "waypointType"));
		}
	}

	private List<BookingWaypointEntity> buildWaypointEntities(BookingEntity booking, List<WaypointRequest> waypointRequests) {
		List<BookingWaypointEntity> waypoints = new ArrayList<>();
		for (int i = 0; i < waypointRequests.size(); i++) {
			WaypointRequest req = waypointRequests.get(i);
			BookingWaypointEntity waypoint = BookingWaypointEntity.newInstance();
			waypoint.setBooking(booking);
			waypoint.setWaypointSequence(i + 1);
			waypoint.setWaypointType(req.getWaypointType());
			waypoint.setLatitude(req.getLatitude());
			waypoint.setLongitude(req.getLongitude());
			waypoint.setAddress(req.getAddress());
			waypoints.add(waypoint);
		}
		return waypoints;
	}

	public BookingEntity getById(Long id) {
		BookingEntity booking = bookingRepository.findById(id)
				.orElseThrow(() -> new ValidationException(ErrorBean.withError(AppErrors.BOOKING_NOT_FOUND, "Booking not found", "id")));
		if (booking.getActive() != 1) {
			throw new ValidationException(ErrorBean.withError(AppErrors.BOOKING_NOT_FOUND, "Booking not found", "id"));
		}
		return booking;
	}

	public List<BookingWaypointEntity> getActiveWaypoints(Long bookingId) {
		if (!bookingRepository.existsById(bookingId)) {
			throw new ValidationException(ErrorBean.withError(AppErrors.BOOKING_NOT_FOUND, "Booking not found", "bookingId"));
		}
		return bookingWaypointRepository.findActiveByBookingIdOrdered(bookingId);
	}

	public PageAndSortResult<BookingEntity> getRequestedBookings(Integer pageSize, Integer pageNo, Map<String, SortDirection> sort) {
		Map<String, Object> filters = new HashMap<>();
		filters.put("status", Status.REQUESTED);
		filters.put("active", 1);
		try {
			return findPagedRecords(filters, sort, pageSize, pageNo);
		} catch (Exception e) {
			throw new ValidationException(ErrorBean.withError("FETCH_ERROR", e.getMessage(), null));
		}
	}

	private String generateBookingReference() {
		String datePart = LocalDate.now().format(REFERENCE_DATE_FORMAT);
		String randomPart = UUID.randomUUID().toString().replace("-", "").substring(0, 5).toUpperCase();
		return "VLV-" + datePart + "-" + randomPart;
	}

	@Override
	public Class<BookingEntity> getEntity() {
		return BookingEntity.class;
	}

	@Override
	public BaseCrudRepository<BookingEntity, ?> getRepository() {
		return bookingRepository;
	}
}
