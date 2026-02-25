package com.velvet.rest.services.user.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;

/**
 * CustomerEntity related information
 * 
 * @author mohit
 */
@Entity
@Table(name = AdminUserConstants.TABLE_NAME)
@DynamicUpdate
@Data
public class AdminUserEntity extends UserEntity implements Serializable, AdminUserConstants {

	private static final long serialVersionUID = -4068472413640202344L;

	@Column(name = FIRSTNAME)
	private String firstname;

	@Column(name = LASTNAME)
	private String lastname;

	public static AdminUserEntity newInstance() {
		AdminUserEntity entity = new AdminUserEntity();
		return entity;
	}
}