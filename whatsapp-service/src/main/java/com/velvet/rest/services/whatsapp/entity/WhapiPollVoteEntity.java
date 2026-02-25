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
@Table(name = "whapi_poll_votes", uniqueConstraints = @UniqueConstraint(columnNames = { "poll_id", "wa_option_id", "voter_wa_id" }))
@DynamicUpdate
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhapiPollVoteEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "poll_id", updatable = false, insertable = false)
	private Long pollId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "poll_id", nullable = false)
	private WhapiPollEntity poll;

	@Column(name = "wa_option_id", nullable = false)
	private String waOptionId;

	@Column(name = "voter_wa_id", nullable = false)
	private String voterWaId;

	@Column(name = "voted_at_ts")
	private Long votedAtTs;
}
