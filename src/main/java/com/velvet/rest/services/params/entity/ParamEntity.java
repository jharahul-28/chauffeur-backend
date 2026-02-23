package com.velvet.rest.services.params.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicUpdate;

import lombok.Getter;
import lombok.Setter;

/**
 * ParamEntity related information
 * 
 * @author mohit
 */
@Entity
@Table(name = ParamConstants.TABLE_NAME)
@DynamicUpdate
@Getter
@Setter
public class ParamEntity implements Serializable, ParamConstants {

	private static final long serialVersionUID = -8815213389547240590L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = ID, nullable = false)
	private Long id;

	@Column(name = PARAM_KEY)
	@NotNull
	private String paramKey;

	@Column(name = PARAM_NAME)
	private String paramName;

	@Column(name = PARAM_VALUE)
	@NotNull
	private String paramValue;

	@Column(name = ACTIVE)
	private int active = 1;

	@Column(name = IS_EDITABLE)
	private String isEditable;

	@Column(name = IS_PUBLIC)
	private String isPublic;

	public static ParamEntity newInstance() {
		ParamEntity entity = new ParamEntity();
		return entity;
	}

	public static ParamEntity create(String paramKey, String paramValue, String isEditable, String isPublic) {
		ParamEntity entity = new ParamEntity();
		entity.setParamKey(paramKey);
		entity.setParamName(paramKey);
		entity.setParamValue(paramValue);
		entity.setIsEditable(isEditable != null ? isEditable : "1");
		entity.setIsPublic(isPublic != null ? isPublic : "1");
		return entity;
	}
}