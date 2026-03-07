package com.velvet.rest.services.user.entity;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.ToString;

/**
 * ChauffeurEntity related information
 */
@Entity
@Table(name = ChauffeurConstants.TABLE_NAME)
@DynamicUpdate
@Data
@ToString
public class ChauffeurEntity extends UserEntity implements Serializable, ChauffeurConstants {

	private static final long serialVersionUID = -4821931095490774310L;

	@Column
	private String name;

	@Column(name = DOB)
	private Date dob;

	@Column(name = STATUS, nullable = false, updatable = true)
	private Integer status = 0;

	@Column(name = IS_DELETED, nullable = false, updatable = true)
	private Boolean isDeleted = false;

	@Column(name = RATING)
	private Double rating;

	@Column(name = PERFORMANCE_SCORE)
	private Double performanceScore;

	@Column(name = WORKING_HOURS)
	private Double workingHours;

	public static ChauffeurEntity newInstance() {
		return new ChauffeurEntity();
	}
}
