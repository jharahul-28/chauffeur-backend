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
	public BookingEntity addStop(Long bookingId, WaypointRequest request) {
		BookingEntity booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new ValidationException(ErrorBean.withError(AppErrors.BOOKING_NOT_FOUND, "Booking not found", "bookingId")));

		if (!Status.PENDING.equals(booking.getStatus())) {
			throw new ValidationException(ErrorBean.withError(AppErrors.BOOKING_NOT_PENDING, "Booking is no longer in PENDING state", "bookingId"));
		}

		Integer maxSeq = bookingWaypointRepository.findMaxSequenceByBookingId(bookingId);
		int dropSeq = maxSeq != null ? maxSeq : 2;

		// insert before the DROP (last position)
		int sequence = request.getWaypointSequence() != null ? request.getWaypointSequence() : dropSeq;

		if (sequence <= 1 || sequence > dropSeq) {
			throw new ValidationException(ErrorBean.withError(AppErrors.INVALID_WAYPOINT_ORDER, "Stop must be inserted between PICKUP and DROP", "waypointSequence"));
		}

		bookingWaypointRepository.shiftSequencesUp(bookingId, sequence);
		bookingWaypointRepository.save(BookingWaypointEntity.newInstance().buildWaypoint(booking, sequence, WaypointType.STOP, request));

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

		if (!WaypointType.STOP.equals(waypoint.getWaypointType())) {
			throw new ValidationException(ErrorBean.withError(AppErrors.INVALID_WAYPOINT_ORDER, "Only STOP waypoints can be removed", "waypointId"));
		}

		waypoint.setActive(0);
		bookingWaypointRepository.save(waypoint);

		return booking;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public BookingEntity updatePickup(Long bookingId, WaypointRequest request) {
		BookingEntity booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new ValidationException(ErrorBean.withError(AppErrors.BOOKING_NOT_FOUND, "Booking not found", "bookingId")));

		if (!Status.PENDING.equals(booking.getStatus())) {
			throw new ValidationException(ErrorBean.withError(AppErrors.BOOKING_NOT_PENDING, "Booking is no longer in PENDING state", "bookingId"));
		}

		List<BookingWaypointEntity> waypoints = bookingWaypointRepository.findActiveByBookingIdOrdered(bookingId);
		BookingWaypointEntity pickup = waypoints.get(0);
		pickup.setLatitude(request.getLatitude());
		pickup.setLongitude(request.getLongitude());
		pickup.setAddress(request.getAddress());
		bookingWaypointRepository.save(pickup);

		return booking;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public BookingEntity updateDrop(Long bookingId, WaypointRequest request) {
		BookingEntity booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new ValidationException(ErrorBean.withError(AppErrors.BOOKING_NOT_FOUND, "Booking not found", "bookingId")));

		if (!Status.PENDING.equals(booking.getStatus())) {
			throw new ValidationException(ErrorBean.withError(AppErrors.BOOKING_NOT_PENDING, "Booking is no longer in PENDING state", "bookingId"));
		}

		List<BookingWaypointEntity> waypoints = bookingWaypointRepository.findActiveByBookingIdOrdered(bookingId);
		BookingWaypointEntity drop = waypoints.get(waypoints.size() - 1);
		drop.setLatitude(request.getLatitude());
		drop.setLongitude(request.getLongitude());
		drop.setAddress(request.getAddress());
		bookingWaypointRepository.save(drop);

		return booking;
	}

	private List<BookingWaypointEntity> buildWaypointEntities(BookingEntity booking, List<WaypointRequest> waypointRequests) {
		List<BookingWaypointEntity> waypoints = new ArrayList<>();
		int total = waypointRequests.size();
		for (int i = 0; i < total; i++) {
			WaypointRequest req = waypointRequests.get(i);
			WaypointType type = (i == 0) ? WaypointType.PICKUP : (i == total - 1) ? WaypointType.DROP : WaypointType.STOP;
			waypoints.add(BookingWaypointEntity.newInstance().buildWaypoint(booking, i + 1, type, req));
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

	@Transactional(propagation = Propagation.REQUIRED)
	public BookingEntity requestBooking(Long id) {
		BookingEntity booking = bookingRepository.findById(id)
				.orElseThrow(() -> new ValidationException(ErrorBean.withError(AppErrors.BOOKING_NOT_FOUND, "Booking not found", "id")));
		if (Status.REQUESTED.equals(booking.getStatus())) {
			throw new ValidationException(ErrorBean.withError(AppErrors.BOOKING_ALREADY_REQUESTED, "Booking has already been submitted", "id"));
		}
		if (!Status.PENDING.equals(booking.getStatus())) {
			throw new ValidationException(ErrorBean.withError(AppErrors.BOOKING_NOT_PENDING, "Booking is not in PENDING state", "id"));
		}
		booking.setStatus(Status.REQUESTED);
		return bookingRepository.save(booking);
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
