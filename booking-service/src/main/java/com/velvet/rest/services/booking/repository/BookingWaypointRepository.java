package com.velvet.rest.services.booking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.services.booking.entity.BookingWaypointConstants.WaypointType;
import com.velvet.rest.services.booking.entity.BookingWaypointEntity;

/**
 * The Interface BookingWaypointRepository.
 */
@Repository
public interface BookingWaypointRepository extends BaseCrudRepository<BookingWaypointEntity, Long> {

	List<BookingWaypointEntity> findByBookingId(Long bookingId);

	void deleteByBookingId(Long bookingId);

	@Modifying
	@Query("UPDATE BookingWaypointEntity w SET w.waypointSequence = w.waypointSequence + 1 WHERE w.booking.id = :bookingId AND w.waypointSequence >= :sequence AND w.active = 1")
	void shiftSequencesUp(@Param("bookingId") Long bookingId, @Param("sequence") Integer sequence);

	@Query("SELECT MAX(w.waypointSequence) FROM BookingWaypointEntity w WHERE w.booking.id = :bookingId AND w.active = 1")
	Integer findMaxSequenceByBookingId(@Param("bookingId") Long bookingId);

	@Query("SELECT COUNT(w) FROM BookingWaypointEntity w WHERE w.booking.id = :bookingId AND w.waypointType = :type AND w.active = 1")
	long countByBookingIdAndWaypointType(@Param("bookingId") Long bookingId, @Param("type") WaypointType type);

	@Query("SELECT w FROM BookingWaypointEntity w WHERE w.booking.id = :bookingId AND w.active = 1 ORDER BY w.waypointSequence ASC")
	List<BookingWaypointEntity> findActiveByBookingIdOrdered(@Param("bookingId") Long bookingId);
}
