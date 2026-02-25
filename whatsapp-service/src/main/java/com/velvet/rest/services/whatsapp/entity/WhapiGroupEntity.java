package com.velvet.rest.services.whatsapp.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.velvet.rest.common.websupport.base.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "whapi_groups")
@DynamicUpdate
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhapiGroupEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chat_id", nullable = false)
	private WhapiChatEntity chat;

	@Column(name = "wa_group_id", nullable = false, unique = true)
	private String waGroupId;

	@Column(name = "name")
	private String name;

	@Column(name = "created_at_ts")
	private Long createdAtTs;

	@Column(name = "created_by_wa_id")
	private String createdByWaId;

	@Column(name = "participant_count")
	private Integer participantCount;

	@Column(name = "admin_add_member_mode")
	private Boolean adminAddMemberMode;

	@Column(name = "last_performed_by")
	private String lastPerformedBy;

	@Column(name = "org_id")
	private UUID orgId;
}
