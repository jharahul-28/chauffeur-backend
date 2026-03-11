package com.velvet.rest.services.fleet.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.velvet.rest.common.websupport.base.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * VehicleCategoryEntity - represents a vehicle category (e.g. Sedan, SUV, Limousine)
 */
@Entity
@Table(name = VehicleCategoryConstants.VEHICLE_CATEGORY_TABLE_NAME)
@DynamicUpdate
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class VehicleCategoryEntity extends BaseEntity implements Serializable, VehicleCategoryConstants {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;

	@Column(name = "category_name", length = 100, nullable = false, unique = true)
	private String categoryName;

	@Column(name = "base_price", nullable = false, precision = 10, scale = 2)
	private BigDecimal basePrice;

	public static VehicleCategoryEntity newInstance() {
		return new VehicleCategoryEntity();
	}
}
