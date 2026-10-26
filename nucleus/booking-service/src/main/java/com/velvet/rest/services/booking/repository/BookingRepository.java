package com.velvet.rest.services.booking.repository;

import org.springframework.stereotype.Repository;

import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.services.booking.entity.BookingEntity;

/**
 * The Interface BookingRepository.
 */
@Repository
public interface BookingRepository extends BaseCrudRepository<BookingEntity, Long> {

}
