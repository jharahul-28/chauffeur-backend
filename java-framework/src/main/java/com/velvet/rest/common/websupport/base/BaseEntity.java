/*
 * @author Mohit
 */
package com.velvet.rest.common.websupport.base;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import lombok.Data;

/**
 * The Class BaseEntity.
 *
 * @author Mohit
 * @version $Id: $Id
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data
@TypeDefs({ @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class) })
public class BaseEntity implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3303023715208714284L;

	/** The Constant CREATED_ON. */
	public static final String CREATED_ON = "created_on";

	/** The Constant CREATED_BY. */
	public static final String CREATED_BY = "created_by";

	/** The Constant MODIFIED_ON. */
	public static final String MODIFIED_ON = "modified_on";

	/** The Constant MODIFIED_BY. */
	public static final String MODIFIED_BY = "modified_by";

	/** The Constant ACTIVE. */
	public static final String ACTIVE = "active";

	/** The created on. */
	@Column(name = CREATED_ON, nullable = false, updatable = false)
	@CreatedDate
	private Date createdOn;

	/** The modified on. */
	@Column(name = MODIFIED_ON, nullable = false, updatable = true)
	@LastModifiedDate
	private Date modifiedOn;

	/** The created by. */
	@Column(name = CREATED_BY, nullable = false, updatable = false)
	@CreatedBy
	private Long createdBy;

	/** The modified by. */
	@Column(name = MODIFIED_BY, nullable = false, updatable = true)
	@LastModifiedBy
	private Long modifiedBy;

	@Column(name = ACTIVE, nullable = false, updatable = true)
	private Integer active = 1;


	/**
	 * <p>
	 * toSafeDate.
	 * </p>
	 *
	 * @param date a {@link java.util.Date} object.
	 * @return a {@link java.util.Date} object.
	 */
	protected Date toSafeDate(Date date) {
		return date != null ? new Date(date.getTime()) : null;
	}

}
