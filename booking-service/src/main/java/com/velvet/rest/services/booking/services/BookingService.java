package com.velvet.rest.services.booking.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.velvet.rest.services.booking.beans.AdditionalCharges;
import com.velvet.rest.services.booking.beans.BookingMetadata;
import com.velvet.rest.services.booking.beans.BookingVehicleRequest;
import com.velvet.rest.services.booking.beans.BookingVehicleMetadata;

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
import com.velvet.rest.services.booking.entity.BookingVehicleEntity;
import com.velvet.rest.services.booking.entity.BookingWaypointEntity;
import com.velvet.rest.services.booking.errors.AppErrors;
import com.velvet.rest.services.booking.repository.BookingRepository;
import com.velvet.rest.services.booking.repository.BookingVehicleRepository;
import com.velvet.rest.services.booking.repository.BookingWaypointRepository;
import com.velvet.rest.services.fleet.entity.VehicleCategoryEntity;
import com.velvet.rest.services.fleet.entity.VehicleEntity;
import com.velvet.rest.services.fleet.repository.VehicleCategoryRepository;
import com.velvet.rest.services.fleet.repository.VehicleRepository;
import com.velvet.rest.services.user.entity.ChauffeurEntity;
import com.velvet.rest.services.user.repository.ChauffeurRepository;

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

	@Autowired
	private BookingVehicleRepository bookingVehicleRepository;

	@Autowired
	private VehicleCategoryRepository vehicleCategoryRepository;

	@Autowired
	private VehicleRepository vehicleRepository;

	@Autowired
	private ChauffeurRepository chauffeurRepository;

	@Transactional(propagation = Propagation.REQUIRED)
	public BookingEntity addBooking(BookingRequest request) {
		if (request.getBookingId() == null) {
			return createBooking(request);
		} else {
			return updateBooking(request);
		}
	}

	private BookingEntity createBooking(BookingRequest request) {
		if (request.getCustomerName() == null || request.getCustomerName().isBlank()) {
			throw new ValidationException(ErrorBean.withError(AppErrors.INVALID_REQUEST, "customerName is required", "customerName"));
		}
		if (request.getCustomerContact() == null || request.getCustomerContact().isBlank()) {
			throw new ValidationException(ErrorBean.withError(AppErrors.INVALID_REQUEST, "customerContact is required", "customerContact"));
		}
		if (request.getTripType() == null) {
			throw new ValidationException(ErrorBean.withError(AppErrors.INVALID_REQUEST, "tripType is required", "tripType"));
		}
		if (request.getRideDate() == null) {
			throw new ValidationException(ErrorBean.withError(AppErrors.INVALID_REQUEST, "rideDate is required", "rideDate"));
		}
		if (request.getPickupTime() == null) {
			throw new ValidationException(ErrorBean.withError(AppErrors.INVALID_REQUEST, "pickupTime is required", "pickupTime"));
		}
		if (CollectionUtils.isEmpty(request.getWaypoints())) {
			throw new ValidationException(ErrorBean.withError(AppErrors.INVALID_REQUEST, "At least one waypoint is required", "waypoints"));
		}

		BookingMetadata metadata = new BookingMetadata();
		metadata.setCustomerName(request.getCustomerName());
		metadata.setCustomerContact(request.getCustomerContact());

		BookingEntity booking = BookingEntity.newInstance();
		booking.setBookingReference(generateBookingReference());
		booking.setBookingDate(LocalDate.now());
		booking.setStatus(Status.PENDING);
		booking.setCustomerId(request.getCustomerId());
		booking.setMetadata(metadata);
		booking.setTripType(request.getTripType());
		booking.setRideDate(request.getRideDate());
		booking.setPickupTime(request.getPickupTime());
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

		if (request.getTripType() != null) booking.setTripType(request.getTripType());
		if (request.getBookingSource() != null) booking.setBookingSource(request.getBookingSource());
		if (request.getRideDate() != null) {
			booking.setRideDate(request.getRideDate());
		}
		if (request.getPickupTime() != null) {
			booking.setPickupTime(request.getPickupTime());
		}
		if (request.getKmRide() != null) {
			booking.setKmRide(request.getKmRide());
		}
		if (request.getEstimatedFare() != null) {
			booking.setEstimatedFare(request.getEstimatedFare());
		}
		if (request.getActualFare() != null) {
			booking.setActualFare(request.getActualFare());
		}
		if (request.getAdditionalCharges() != null) {
			booking.setAdditionalCharges(request.getAdditionalCharges());
		}
		if (request.getAdvanceAmount() != null) {
			booking.setAdvanceAmount(request.getAdvanceAmount());
		}
		computeDerivedFareFields(booking);
		if (request.getPaymentMode() != null) {
			booking.setPaymentMode(request.getPaymentMode());
		}
		if (request.getPaymentStatus() != null) {
			booking.setPaymentStatus(request.getPaymentStatus());
		}
		BookingMetadata metadata = booking.getMetadata() != null ? booking.getMetadata() : new BookingMetadata();
		if (request.getCustomerName() != null) metadata.setCustomerName(request.getCustomerName());
		if (request.getCustomerContact() != null) metadata.setCustomerContact(request.getCustomerContact());
		if (request.getCustomerType() != null) metadata.setCustomerType(request.getCustomerType());
		if (request.getSpecialInstructions() != null) metadata.setSpecialInstructions(request.getSpecialInstructions());
		if (request.getRemarks() != null) metadata.setRemarks(request.getRemarks());
		booking.setMetadata(metadata);

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

	@Transactional(propagation = Propagation.REQUIRED)
	public BookingVehicleEntity assignVehicle(BookingVehicleRequest request) {
		if (request.getId() == null) {
			return createVehicleAssignment(request);
		} else {
			return updateVehicleAssignment(request);
		}
	}

	private BookingVehicleEntity createVehicleAssignment(BookingVehicleRequest request) {
		if (request.getBookingId() == null) {
			throw new ValidationException(ErrorBean.withError(AppErrors.INVALID_REQUEST, "bookingId is required", "bookingId"));
		}
		if (request.getVehicleCategoryId() == null) {
			throw new ValidationException(ErrorBean.withError(AppErrors.INVALID_REQUEST, "vehicleCategoryId is required", "vehicleCategoryId"));
		}

		BookingEntity booking = bookingRepository.findById(request.getBookingId())
				.orElseThrow(() -> new ValidationException(ErrorBean.withError(AppErrors.BOOKING_NOT_FOUND, "Booking not found", "bookingId")));

		VehicleCategoryEntity category = vehicleCategoryRepository.findById(request.getVehicleCategoryId())
				.orElseThrow(() -> new ValidationException(ErrorBean.withError(AppErrors.INVALID_REQUEST, "Vehicle category not found", "vehicleCategoryId")));

		BookingVehicleMetadata metadata = new BookingVehicleMetadata();
		metadata.setCategoryName(category.getCategoryName());

		BookingVehicleEntity assignment = BookingVehicleEntity.newInstance();
		assignment.setBooking(booking);
		assignment.setVehicleCategoryId(category.getId());
		assignment.setVehicleCategory(category);
		assignment.setMetadata(metadata);

		return bookingVehicleRepository.save(assignment);
	}

	private BookingVehicleEntity updateVehicleAssignment(BookingVehicleRequest request) {
		BookingVehicleEntity assignment = bookingVehicleRepository.findById(request.getId())
				.orElseThrow(() -> new ValidationException(ErrorBean.withError(AppErrors.INVALID_REQUEST, "Vehicle assignment not found", "id")));

		BookingVehicleMetadata metadata = assignment.getMetadata() != null ? assignment.getMetadata() : new BookingVehicleMetadata();

		if (request.getVehicleCategoryId() != null) {
			VehicleCategoryEntity category = vehicleCategoryRepository.findById(request.getVehicleCategoryId())
					.orElseThrow(() -> new ValidationException(ErrorBean.withError(AppErrors.INVALID_REQUEST, "Vehicle category not found", "vehicleCategoryId")));
			assignment.setVehicleCategoryId(category.getId());
			assignment.setVehicleCategory(category);
			metadata.setCategoryName(category.getCategoryName());
		}

		if (request.getVehicleId() != null) {
			VehicleEntity vehicle = vehicleRepository.findById(request.getVehicleId())
					.orElseThrow(() -> new ValidationException(ErrorBean.withError(AppErrors.INVALID_REQUEST, "Vehicle not found", "vehicleId")));
			assignment.setVehicleId(vehicle.getId());
			assignment.setVehicle(vehicle);
			assignment.setVehicleNumber(vehicle.getVehicleNumber());
			metadata.setVehicleBrand(vehicle.getBrand());
			metadata.setVehicleModel(vehicle.getModel());
		}

		if (request.getChauffeurId() != null) {
			ChauffeurEntity chauffeur = chauffeurRepository.findById(request.getChauffeurId())
					.orElseThrow(() -> new ValidationException(ErrorBean.withError(AppErrors.INVALID_REQUEST, "Chauffeur not found", "chauffeurId")));
			assignment.setChauffeurId(chauffeur.getId());
			assignment.setChauffeur(chauffeur);
			metadata.setChauffeurName(chauffeur.getFirstName() + " " + chauffeur.getLastName());
			metadata.setChauffeurContact(chauffeur.getPhone());
		}

		assignment.setMetadata(metadata);
		return bookingVehicleRepository.save(assignment);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void removeVehicleAssignment(Long id) {
		BookingVehicleEntity assignment = bookingVehicleRepository.findById(id)
				.orElseThrow(() -> new ValidationException(ErrorBean.withError(AppErrors.INVALID_REQUEST, "Vehicle assignment not found", "id")));
		assignment.setActive(0);
		bookingVehicleRepository.save(assignment);
	}

	public List<BookingVehicleEntity> getVehicleAssignments(Long bookingId) {
		if (!bookingRepository.existsById(bookingId)) {
			throw new ValidationException(ErrorBean.withError(AppErrors.BOOKING_NOT_FOUND, "Booking not found", "bookingId"));
		}
		return bookingVehicleRepository.findByBookingIdAndActive(bookingId, 1);
	}

	private void computeDerivedFareFields(BookingEntity booking) {
		AdditionalCharges charges = booking.getAdditionalCharges();
		BigDecimal totalAdditional = BigDecimal.ZERO;
		if (charges != null) {
			if (charges.getMcFare() != null) totalAdditional = totalAdditional.add(charges.getMcFare());
			if (charges.getWaitingCharge() != null) totalAdditional = totalAdditional.add(charges.getWaitingCharge());
			if (charges.getParkingCharge() != null) totalAdditional = totalAdditional.add(charges.getParkingCharge());
		}
		booking.setTotalAdditionalAmount(totalAdditional);

		BigDecimal actualFare = booking.getActualFare();
		BigDecimal totalFare = actualFare != null ? actualFare.add(totalAdditional) : null;
		booking.setTotalFare(totalFare);

		BigDecimal advance = booking.getAdvanceAmount();
		booking.setBalanceAmount(totalFare != null ? totalFare.subtract(advance != null ? advance : BigDecimal.ZERO) : null);
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
