package com.velvet.rest.services.user.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;

import com.velvet.rest.common.websupport.base.BaseEntity;

import lombok.Data;

/**
 * UserAddress entity for managing user addresses.
 *
 * @author mohit
 */
@Entity
@Table(name = "user_addresses")
@DynamicUpdate
@Data
@Where(clause = "active = 1")
public class UserAddressEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false, nullable = false)
	private Integer id;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	private UUID orgId;

	@Column(nullable = false)
	private Integer orgAddressId;

	@Column(nullable = false)
	private String orgAddress;

	@Column
	private String tower;

	@Column
	private String floor;

	public static UserAddressEntity newInstance() {
		return new UserAddressEntity();
	}
}
