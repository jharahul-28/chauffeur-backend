package com.velvet.rest.services.booking.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.services.booking.entity.RideJourneyEntity;

@Repository
public interface RideJourneyRepository extends BaseCrudRepository<RideJourneyEntity, Long> {

	List<RideJourneyEntity> findByBookingIdOrderByCreatedOnAsc(Long bookingId);

	List<RideJourneyEntity> findByBookingVehicleIdOrderByCreatedOnAsc(Long bookingVehicleId);
}
