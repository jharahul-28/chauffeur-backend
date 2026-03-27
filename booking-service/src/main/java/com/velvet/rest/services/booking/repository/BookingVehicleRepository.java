package com.velvet.rest.services.booking.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.services.booking.entity.BookingVehicleEntity;

@Repository
public interface BookingVehicleRepository extends BaseCrudRepository<BookingVehicleEntity, Long> {

	List<BookingVehicleEntity> findByBookingIdAndActive(Long bookingId, Integer active);
}
