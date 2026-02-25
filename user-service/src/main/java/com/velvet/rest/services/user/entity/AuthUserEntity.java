package com.velvet.rest.services.user.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.velvet.rest.common.websupport.base.BaseEntity;

import lombok.Data;

/**
 * AuthUser related information
 *
 * @author mohit
 */
@Entity
@Table(name = AuthUserConstants.TABLE_NAME)
@DynamicUpdate
@Data
public class AuthUserEntity extends BaseEntity implements Serializable, AuthUserConstants {

	private static final long serialVersionUID = -3905536261674974519L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = ID, nullable = false)
	private Long id;

	@Column(name = USER_TYPE)
	private String userType;

	@ManyToMany
	@JoinTable(name = USERS_GROUPS, joinColumns = { @JoinColumn(name = USER_ID) }, inverseJoinColumns = { @JoinColumn(name = GROUP_ID) })
	Set<GroupEntity> groups = new HashSet<>();

	public static AuthUserEntity newInstance(String userType) {
		AuthUserEntity entity = new AuthUserEntity();
		entity.setUserType(userType);
		return entity;
	}

	public void addGroup(GroupEntity group) {
		this.groups.add(group);
	}
}