package com.velvet.rest.services.user.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.velvet.rest.common.websupport.base.BaseEntity;

import lombok.Data;

/**
 * Entity App Session Logs
 *
 * @author Mohit
 * @version $Id: $Id
 */
@Entity
@Table(name = AppSessionConstants.TABLE_NAME)
@DynamicUpdate
@Data
public class AppSessionEntity extends BaseEntity implements Serializable, AppSessionConstants {

	private static final long serialVersionUID = 201806141755170277L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = ID, nullable = false)
	private Long id;

	@Column(name = USER_ID)
	private Long userId;

	@Column(name = KEY)
	private String sessionKey;

	@Column(name = EXPIRY)
	private Date expiry;

	@Column(name = LAST_ACCESSED)
	private Date lastAccessed;

	public static AppSessionEntity newInstance(Long userId, String key) {
		AppSessionEntity entity = new AppSessionEntity();
		entity.setUserId(userId);
		entity.setSessionKey(key);
		entity.setLastAccessed(new Date());
		return entity;
	}

}
