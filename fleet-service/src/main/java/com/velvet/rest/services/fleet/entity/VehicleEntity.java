package com.velvet.rest.services.fleet.entity;

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

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.velvet.rest.common.websupport.base.BaseEntity;
import com.velvet.rest.services.fleet.beans.VehicleBean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * VehicleEntity - represents a vehicle in the fleet
 */
@Entity
@Table(name = VehicleConstants.VEHICLE_TABLE_NAME)
@DynamicUpdate
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class VehicleEntity extends BaseEntity implements VehicleConstants {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;

	@Column(name = "vehicle_number", nullable = false, unique = true)
	private String vehicleNumber;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false)
	private VehicleCategoryEntity category;

	@Column(name = "brand", nullable = false)
	private String brand;

	@Column(name = "model", nullable = false)
	private String model;

	@Column(name = "seating_capacity", nullable = false)
	private Integer seatingCapacity;

	@Enumerated(EnumType.STRING)
	@Column(name = "fuel_type", nullable = false)
	private FuelType fuelType;

	@Enumerated(EnumType.STRING)
	@Column(name = "transmission", nullable = false)
	private Transmission transmission;

	@Column(name = "available", nullable = false)
	private Boolean available = true;
	
	@Type(type = "jsonb")
	@Column(name = "metadata", columnDefinition = "jsonb")
	private VehicleMetadata metadata = new VehicleMetadata();

	@Column(name = "image_url")
	private String imageUrl;

	public static VehicleEntity newInstance() {
		return new VehicleEntity();
	}

	public VehicleEntity createNewVehicle(VehicleBean bean, VehicleCategoryEntity category) {
		this.setVehicleNumber(bean.getVehicleNumber());
		this.setCategory(category);
		this.setBrand(bean.getBrand());
		this.setModel(bean.getModel());
		this.setSeatingCapacity(bean.getSeatingCapacity());
		this.setFuelType(bean.getFuelType());
		this.setTransmission(bean.getTransmission());
		this.setAvailable(bean.getAvailable() != null ? bean.getAvailable() : true);
		this.setMetadata(bean.getMetadata());
		return this;
	}

	public Long getCategoryId() {
		return category != null ? category.getId() : null;
	}
}
