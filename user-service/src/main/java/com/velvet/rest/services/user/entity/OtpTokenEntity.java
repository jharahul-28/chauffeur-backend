package com.velvet.rest.services.user.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;

import com.velvet.rest.common.websupport.base.BaseEntity;
import com.velvet.rest.services.user.constants.UserConstants.OtpType;

import lombok.Data;

@Entity
@Data
@Table(name = "otp_tokens")
@DynamicUpdate
@Where(clause = "active = 1")
public class OtpTokenEntity extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 7586054634244740782L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@Column
	private Long userId;

	@Column(nullable = false)
	private String referenceId;

	@Column(nullable = false)
	private String otp;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private OtpType otpType;

	@Column
	private String provider;

	@Column
	private String verificationId;

	@Column(nullable = false)
	private Date expiry;

	@Column(nullable = false)
	private Integer attempts = 0;

	@Column(nullable = false)
	private boolean verified = false;

	public static OtpTokenEntity newInstance() {
		OtpTokenEntity entity = new OtpTokenEntity();
		return entity;
	}
}
