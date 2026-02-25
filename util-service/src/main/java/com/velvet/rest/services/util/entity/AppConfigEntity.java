package com.velvet.rest.services.util.entity;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import com.velvet.rest.common.websupport.base.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "app_config")
@DynamicUpdate
@Data
@EqualsAndHashCode(callSuper = false)
public class AppConfigEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 20)
	private String platform;

	@Column(name = "min_supported_version", length = 20)
	private String minSupportedVersion;

	@Column(name = "latest_version", length = 20)
	private String latestVersion;

	@Column(name = "soft_update_version", length = 20)
	private String softUpdateVersion;

	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb")
	private Map<String, String> labels;

	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb")
	private Map<String, String> phones;

	@Type(type = "jsonb")
	@Column(name = "dynamic_fields", columnDefinition = "jsonb")
	private Map<String, Object> dynamicFields;
}
