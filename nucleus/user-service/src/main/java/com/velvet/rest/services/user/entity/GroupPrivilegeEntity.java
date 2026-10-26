package com.velvet.rest.services.user.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;


import com.velvet.rest.common.websupport.base.BaseEntity;

/**
 * The Class GroupPrivilegeEntity.
 *
 * @author Mohit
 * @version $Id: $Id
 */
@Entity
@Table(name = GroupPrivilegeConstants.TABLE_NAME)
@DynamicUpdate
public class GroupPrivilegeEntity extends BaseEntity implements Serializable, GroupPrivilegeConstants {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1697793867887294950L;

	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = ID, nullable = false)
	private Long id;

	/** The group id. */
	@ManyToOne
	@JoinColumn(name = GROUP_ID, nullable = false)
	private GroupEntity group;

	/** The privilege id. */
	@Column(name = PRIVILEGE_SLUG)
	private String privilegeSlug;

	/**
	 * Instantiates a new group privilege entity.
	 */
	public GroupPrivilegeEntity() {}

	/**
	 * Instantiates a new group privilege entity.
	 *
	 * @param group       the group id
	 * @param privilegeId the privilege id
	 */
	public GroupPrivilegeEntity(GroupEntity group, String privilegeSlug) {
		super();
		this.group = group;
		this.privilegeSlug = privilegeSlug;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Gets the group id.
	 *
	 * @return the group id
	 */
	public GroupEntity getGroup() {
		return group;
	}

	/**
	 * Sets the group id.
	 *
	 * @param group the new group id
	 */
	public void setGroup(GroupEntity group) {
		this.group = group;
	}

	/**
	 * Gets the privilege id.
	 *
	 * @return the privilege id
	 */
	public String getPrivilegeSlug() {
		return privilegeSlug;
	}

	/**
	 * Sets the privilege id.
	 *
	 * @param privilegeId the new privilege id
	 */
	public void setPrivilegeSlug(String privilegeSlug) {
		this.privilegeSlug = privilegeSlug;
	}

	public static GroupPrivilegeEntity newInstance() {
		return new GroupPrivilegeEntity();
	}

}
