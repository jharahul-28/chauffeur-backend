package com.velvet.rest.services.user.entity;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.ToString;

/**
 * CustomerEntity related information
 * 
 * @author mohit
 */
@Entity
@Table(name = CustomerConstants.TABLE_NAME)
@DynamicUpdate
@Data
@ToString
public class CustomerEntity extends UserEntity implements Serializable, CustomerConstants {

	private static final long serialVersionUID = -3859831095490774210L;

	@Column
	private String name;

	@Column(name = PROFILE_PIC)
	private String profilePic;

	@Column(name = EMAIL_VERIFIED)
	private Boolean emailVerified = false;

	@Column
	private Boolean phoneVerified = false;

	@Column(name = REFERRED_BY)
	private String referredBy;

	@Column(name = REFERRER_CODE)
	private String referralCode;

	@Column(name = STATUS, nullable = false, updatable = true)
	private Integer status = 0;

	@Column(name = IS_DELETED, nullable = false, updatable = true)
	private Boolean isDeleted = false;

	@Column
	private UUID orgId;

	@Column(name = WELCOME_MESSAGE_SENT)
	private Boolean welcomeMessageSent = false;

	public static CustomerEntity newInstance() {
		CustomerEntity entity = new CustomerEntity();
		return entity;
	}
}