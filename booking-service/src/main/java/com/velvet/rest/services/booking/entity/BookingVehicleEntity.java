package com.velvet.rest.services.booking.entity;

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
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.velvet.rest.common.websupport.base.BaseEntity;
import com.velvet.rest.services.booking.beans.BookingVehicleMetadata;
import com.velvet.rest.services.fleet.entity.VehicleCategoryEntity;
import com.velvet.rest.services.fleet.entity.VehicleEntity;
import com.velvet.rest.services.user.entity.ChauffeurEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = BookingVehicleConstants.TABLE_NAME)
@DynamicUpdate
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class BookingVehicleEntity extends BaseEntity implements BookingVehicleConstants {

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

	@Column(name = "vehicle_category_id", nullable = false, insertable = false, updatable = false)
	private Long vehicleCategoryId;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vehicle_category_id", nullable = false)
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

	@Column(name = "vehicle_number", length = 20)
	private String vehicleNumber;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private VehicleAssignmentStatus status = VehicleAssignmentStatus.ASSIGNED;

	@Type(type = "jsonb")
	@Column(name = "metadata", columnDefinition = "jsonb")
	private BookingVehicleMetadata metadata;

	public static BookingVehicleEntity newInstance() {
		return new BookingVehicleEntity();
	}
}
