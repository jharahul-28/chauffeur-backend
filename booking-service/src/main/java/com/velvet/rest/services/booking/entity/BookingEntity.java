package com.velvet.rest.services.booking.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.velvet.rest.common.websupport.base.BaseEntity;
import com.velvet.rest.services.booking.beans.AdditionalCharges;
import com.velvet.rest.services.booking.beans.BookingMetadata;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = BookingConstants.TABLE_NAME)
@DynamicUpdate
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class BookingEntity extends BaseEntity implements BookingConstants {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;

	@Column(name = "booking_reference", nullable = false, unique = true, length = 20)
	private String bookingReference;

	@Column(name = "booking_date", nullable = false)
	private LocalDate bookingDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private Status status = Status.PENDING;

	// --- Customer fields ---

	@Column(name = "customer_id")
	private Long customerId;

	@Type(type = "jsonb")
	@Column(name = "metadata", columnDefinition = "jsonb")
	private BookingMetadata metadata;

	// --- Ride details ---

	@Column(name = "ride_date")
	private LocalDate rideDate;

	@Column(name = "pickup_time")
	private LocalTime pickupTime;

	@Enumerated(EnumType.STRING)
	@Column(name = "booking_source", nullable = false)
	private BookingSource bookingSource = BookingSource.WEB;

	@Enumerated(EnumType.STRING)
	@Column(name = "trip_type", nullable = false)
	private TripType tripType;

	@Column(name = "km_ride", precision = 8, scale = 3)
	private BigDecimal kmRide;

	// --- Fare fields ---

	@Column(name = "estimated_fare", precision = 10, scale = 2)
	private BigDecimal estimatedFare;

	@Column(name = "actual_fare", precision = 10, scale = 2)
	private BigDecimal actualFare;

	@Type(type = "jsonb")
	@Column(name = "additional_charges", columnDefinition = "jsonb")
	private AdditionalCharges additionalCharges;

	@Column(name = "total_additional_amount", precision = 10, scale = 2)
	private BigDecimal totalAdditionalAmount;

	@Column(name = "total_fare", precision = 10, scale = 2)
	private BigDecimal totalFare;

	@Column(name = "advance_amount", precision = 10, scale = 2)
	private BigDecimal advanceAmount;

	@Column(name = "balance_amount", precision = 10, scale = 2)
	private BigDecimal balanceAmount;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_mode")
	private PaymentMode paymentMode;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_status", nullable = false)
	private PaymentStatus paymentStatus = PaymentStatus.PENDING;

	// --- OTP ---

	@Column(name = "start_otp", length = 6)
	private String startOtp;

	@Column(name = "otp_verified", nullable = false)
	private Boolean otpVerified = false;

	// --- Waypoints ---

	@OrderBy(clause = "waypoint_sequence ASC")
	@OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonManagedReference
	private List<BookingWaypointEntity> waypoints;

	// --- Vehicles ---

	@OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<BookingVehicleEntity> vehicles;


	public static BookingEntity newInstance() {
		return new BookingEntity();
	}
}
