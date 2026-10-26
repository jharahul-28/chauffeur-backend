package com.velvet.rest.services.util.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.velvet.rest.common.websupport.base.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "cities")
@DynamicUpdate
@Data
@EqualsAndHashCode(callSuper = false)
public class CityEntity extends BaseEntity {

	private static final long serialVersionUID = -5218860644043344651L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column
	private String state;

	@Column(nullable = false)
	private String country;

	@Column(nullable = false)
	private String phoneCode;

}