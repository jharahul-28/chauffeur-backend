package com.velvet.rest.services.whatsapp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.DynamicUpdate;

import com.velvet.rest.common.websupport.base.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "whapi_group_participants", uniqueConstraints = @UniqueConstraint(columnNames = { "group_id", "wa_user_id" }))
@DynamicUpdate
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WhapiGroupParticipantEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "group_id", updatable = false, insertable = false)
	private Long groupId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id", nullable = false)
	private WhapiGroupEntity group;

	@Column(name = "wa_user_id", nullable = false)
	private String waUserId;

	@Column(name = "role", length = 20)
	private String role; // creator | admin | member

	@Column(name = "joined_at_ts")
	private Long joinedAtTs;

	@Column(name = "left_at_ts")
	private Long leftAtTs;
}
