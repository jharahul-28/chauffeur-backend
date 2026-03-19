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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OrderBy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.velvet.rest.common.websupport.base.BaseEntity;
import com.velvet.rest.services.fleet.entity.VehicleCategoryEntity;
import com.velvet.rest.services.fleet.entity.VehicleEntity;
import com.velvet.rest.services.user.entity.ChauffeurEntity;

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

	@Column(name = "customer_id", nullable = false)
	private Long customerId;

	@Column(name = "ride_date")
	private LocalDate rideDate;

	@Column(name = "ride_time")
	private LocalTime rideTime;

	@Column(name = "vehicle_category_id", insertable = false, updatable = false)
	private Long vehicleCategoryId;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vehicle_category_id")
	private VehicleCategoryEntity vehicleCategory;

	@Column(name = "vehicle_id", insertable = false, updatable = false)
	private Long vehicleId;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vehicle_id")
	private VehicleEntity vehicle;

	@Column(name = "chauffeur_id", insertable = false, updatable = false)
	private Long chauffeurId;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chauffeur_id")
	private ChauffeurEntity chauffeur;

	@Column(name = "pickup_count", nullable = false)
	private Integer pickupCount = 1;

	@Column(name = "drop_count", nullable = false)
	private Integer dropCount = 1;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private Status status = Status.PENDING;

	@Enumerated(EnumType.STRING)
	@Column(name = "beverage_type")
	private BeverageType beverageType;

	@Column(name = "special_request", columnDefinition = "text")
	private String specialRequest;

	@Column(name = "estimated_price", precision = 10, scale = 2)
	private BigDecimal estimatedPrice;

	@Column(name = "estimated_distance_km", precision = 8, scale = 3)
	private BigDecimal estimatedDistanceKm;

	@Column(name = "start_otp", length = 6)
	private String startOtp;

	@Column(name = "otp_verified", nullable = false)
	private Boolean otpVerified = false;

	@OrderBy(clause = "waypoint_sequence ASC")
	@OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonManagedReference
	private List<BookingWaypointEntity> waypoints;

	public static BookingEntity newInstance() {
		return new BookingEntity();
	}
}
