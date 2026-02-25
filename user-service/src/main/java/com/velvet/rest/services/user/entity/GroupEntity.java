package com.velvet.rest.services.user.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.velvet.rest.common.websupport.base.BaseEntity;

/**
 * EntityAsset_change_log related information
 *
 * @author mohit
 */
@Entity
@Table(name = GroupConstants.TABLE_NAME)
@DynamicUpdate
public class GroupEntity extends BaseEntity implements Serializable, GroupConstants {

	private static final long serialVersionUID = -7260801691151129451L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = ID, nullable = false)
	private Long id;

	@Column(name = NAME)
	private String name;

	@Column(name = LEVEL)
	private Integer level;

	@ManyToMany(mappedBy = "groups")
	private Set<AuthUserEntity> users;

	@OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<GroupPrivilegeEntity> privileges;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Set<AuthUserEntity> getUsers() {
		return users;
	}

	public void setUsers(Set<AuthUserEntity> users) {
		this.users = users;
	}

	public Set<GroupPrivilegeEntity> getPrivileges() {
		return privileges;
	}

	public void setPrivileges(Set<GroupPrivilegeEntity> privileges) {
		this.privileges = privileges;
	}

	public static GroupEntity newInstance() {
		GroupEntity entity = new GroupEntity();
		return entity;
	}
}