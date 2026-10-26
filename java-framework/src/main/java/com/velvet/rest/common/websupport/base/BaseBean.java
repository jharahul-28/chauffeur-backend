/*
 * @author Mohit
 */
package com.velvet.rest.common.websupport.base;

import java.io.Serializable;

import javax.validation.constraints.Null;

import io.swagger.annotations.ApiModelProperty;

/**
 * The Class BaseBean.
 *
 * @author Mohit
 * @version $Id: $Id
 */
public class BaseBean implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7111344873483109021L;

	/** The created on. */
	@ApiModelProperty(value = " Created On ", readOnly = true, allowEmptyValue = true, notes = "Not Required in input field.")
	@Null
	private String createdOn;

	/** The created by. */
	@ApiModelProperty(value = " Created By ", readOnly = true, allowEmptyValue = true, notes = "Not Required in input field.")
	@Null
	private Long createdBy;

	/** The modified on. */
	@ApiModelProperty(value = " Modified On ", readOnly = true, allowEmptyValue = true, notes = "Not Required in input field.")
	@Null
	private String modifiedOn;

	/** The modified by. */
	@ApiModelProperty(value = " Modified By ", readOnly = true, allowEmptyValue = true, notes = "Not Required in input field.")
	@Null
	private Long modifiedBy;

	/**
	 * Gets the created on.
	 *
	 * @return the created on
	 */
	public String getCreatedOn() {
		return createdOn;
	}

	/**
	 * Sets the created on.
	 *
	 * @param createdOn
	 *            the new created on
	 */
	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	/**
	 * Gets the created by.
	 *
	 * @return the created by
	 */
	public Long getCreatedBy() {
		return createdBy;
	}

	/**
	 * Sets the created by.
	 *
	 * @param createdBy
	 *            the new created by
	 */
	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * Gets the modified on.
	 *
	 * @return the modified on
	 */
	public String getModifiedOn() {
		return modifiedOn;
	}

	/**
	 * Sets the modified on.
	 *
	 * @param modifiedOn
	 *            the new modified on
	 */
	public void setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	/**
	 * Gets the modified by.
	 *
	 * @return the modified by
	 */
	public Long getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * Sets the modified by.
	 *
	 * @param modifiedBy
	 *            the new modified by
	 */
	public void setModifiedBy(Long modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
}
