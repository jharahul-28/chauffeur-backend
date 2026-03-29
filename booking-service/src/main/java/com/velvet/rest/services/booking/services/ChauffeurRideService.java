package com.velvet.rest.services.booking.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.velvet.rest.common.beans.ErrorBean;
import com.velvet.rest.common.exceptions.ValidationException;
import com.velvet.rest.services.booking.entity.BookingConstants.Status;
import com.velvet.rest.services.booking.entity.BookingEntity;
import com.velvet.rest.services.booking.entity.BookingVehicleConstants.VehicleAssignmentStatus;
import com.velvet.rest.services.booking.entity.BookingVehicleEntity;
import com.velvet.rest.services.booking.entity.RideJourneyEntity;
import com.velvet.rest.services.booking.errors.AppErrors;
import com.velvet.rest.services.booking.repository.BookingRepository;
import com.velvet.rest.services.booking.repository.BookingVehicleRepository;
import com.velvet.rest.services.booking.repository.RideJourneyRepository;

@Service
public class ChauffeurRideService {

	@Autowired
	private BookingVehicleRepository bookingVehicleRepository;

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private RideJourneyRepository rideJourneyRepository;

	private BookingVehicleEntity getAssignedVehicle(Long bookingVehicleId) {
		return bookingVehicleRepository.findById(bookingVehicleId)
				.orElseThrow(() -> new ValidationException(ErrorBean.withError(AppErrors.VEHICLE_ASSIGNMENT_NOT_FOUND, "Vehicle assignment not found", "bookingVehicleId")));
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public RideJourneyEntity markDriverOnTheWay(Long bookingVehicleId) {
		BookingVehicleEntity assignment = getAssignedVehicle(bookingVehicleId);

		if (!VehicleAssignmentStatus.ASSIGNED.equals(assignment.getStatus())) {
			throw new ValidationException(ErrorBean.withError(AppErrors.INVALID_RIDE_STATUS_TRANSITION,
					"Driver can only be marked on-the-way from ASSIGNED status. Current status: " + assignment.getStatus(), "bookingVehicleId"));
		}

		assignment.setStatus(VehicleAssignmentStatus.DRIVER_ON_THE_WAY);
		bookingVehicleRepository.save(assignment);

		BookingEntity booking = bookingRepository.findById(assignment.getBookingId())
				.orElseThrow(() -> new ValidationException(ErrorBean.withError(AppErrors.BOOKING_NOT_FOUND, "Booking not found", "bookingId")));
		booking.setStatus(Status.DRIVER_ON_THE_WAY);
		bookingRepository.save(booking);

		RideJourneyEntity journey = RideJourneyEntity.newInstance();
		journey.setBookingId(assignment.getBookingId());
		journey.setBookingVehicleId(bookingVehicleId);
		journey.setChauffeurId(assignment.getChauffeurId());
		journey.setStatus(Status.DRIVER_ON_THE_WAY);
		return rideJourneyRepository.save(journey);
	}

	public List<RideJourneyEntity> getRideJourney(Long bookingId) {
		if (!bookingRepository.existsById(bookingId)) {
			throw new ValidationException(ErrorBean.withError(AppErrors.BOOKING_NOT_FOUND, "Booking not found", "bookingId"));
		}
		return rideJourneyRepository.findByBookingIdOrderByCreatedOnAsc(bookingId);
	}
}
