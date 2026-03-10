package com.velvet.rest.services.fleet.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

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
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
	private UUID id;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false, unique = true)
	private CategoryType type;

	@Column(name = "category_name", length = 100, nullable = false, unique = true)
	private String categoryName;

	//order of display in app, lower number means higher priority
	@Column(name = "display_order", nullable = false)
	private Integer displayOrder;

	@Column(name = "base_price", nullable = false, precision = 10, scale = 2)
	private BigDecimal basePrice;

	public static VehicleCategoryEntity newInstance() {
		return new VehicleCategoryEntity();
	}
}
