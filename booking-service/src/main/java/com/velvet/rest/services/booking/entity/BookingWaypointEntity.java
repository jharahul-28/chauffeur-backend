package com.velvet.rest.services.booking.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.velvet.rest.common.websupport.base.BaseEntity;
import com.velvet.rest.services.booking.beans.WaypointRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = BookingWaypointConstants.TABLE_NAME)
@DynamicUpdate
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class BookingWaypointEntity extends BaseEntity implements BookingWaypointConstants {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;

	@Column(name = "booking_id", nullable = false, insertable = false, updatable = false)
	private Long bookingId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "booking_id", nullable = false)
	@JsonBackReference
	private BookingEntity booking;

	@Column(name = "waypoint_sequence", nullable = false)
	private Integer waypointSequence;

	@Enumerated(EnumType.STRING)
	@Column(name = "waypoint_type", nullable = false)
	private WaypointType waypointType;

	@Column(name = "latitude", nullable = false, precision = 10, scale = 7)
	private BigDecimal latitude;

	@Column(name = "longitude", nullable = false, precision = 10, scale = 7)
	private BigDecimal longitude;

	@Column(name = "address", nullable = false, columnDefinition = "text")
	private String address;

	public static BookingWaypointEntity newInstance() {
		return new BookingWaypointEntity();
	}

	public BookingWaypointEntity buildWaypoint(BookingEntity booking, Integer sequence, WaypointRequest request) {
		this.setBooking(booking);
		this.setWaypointSequence(sequence);
		this.setWaypointType(request.getWaypointType());
		this.setLatitude(request.getLatitude());
		this.setLongitude(request.getLongitude());
		this.setAddress(request.getAddress());
		return this;
	}
}
