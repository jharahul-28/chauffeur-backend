package com.velvet.rest.services.booking.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.velvet.rest.common.websupport.base.BaseEntity;
import com.velvet.rest.services.booking.entity.BookingConstants.Status;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "ride_journeys")
@DynamicUpdate
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class RideJourneyEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;

	@Column(name = "booking_id", nullable = false)
	private Long bookingId;

	@Column(name = "booking_vehicle_id", nullable = false)
	private Long bookingVehicleId;

	@Column(name = "chauffeur_id")
	private Long chauffeurId;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private Status status;

	public static RideJourneyEntity newInstance() {
		return new RideJourneyEntity();
	}
}
