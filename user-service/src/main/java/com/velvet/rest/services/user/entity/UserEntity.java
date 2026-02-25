package com.velvet.rest.services.user.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.velvet.rest.common.websupport.base.BaseEntity;
import com.velvet.rest.services.user.constants.UserConstants.Gender;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * StoreEntity related information
 * 
 * @author mohit
 */
@MappedSuperclass
@Getter
@Setter
@ToString
public abstract class UserEntity extends BaseEntity implements Serializable, UserConstants {

	private static final long serialVersionUID = -3859831095490774210L;

	@Id
	@Column(name = ID, nullable = false)
	private Long id;

	@Column(name = PHONE)
	private String phone;

	@Column
	private String countryCode;

	@Column(name = EMAIL)
	private String email;

	@Column(name = GENDER)
	private Gender gender;

	@Column(name = PASSWORD)
	private String password;

	@Column(name = PASSWORD_KEY)
	private String passwordKey;

	@Column(name = LAST_LOGIN)
	private String lastLogin;

	@Column
	private Integer allowedSessions = 1;

	@Column(nullable = false, updatable = true)
	private Boolean blocked = false;
}