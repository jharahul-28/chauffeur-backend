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
@Table(name = "whapi_poll_options", uniqueConstraints = @UniqueConstraint(columnNames = { "poll_id", "wa_option_id" }))
@DynamicUpdate
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WhapiPollOptionEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "poll_id", nullable = false)
	private WhapiPollEntity poll;

	@Column(name = "wa_option_id", nullable = false)
	private String waOptionId;

	@Column(name = "option_name", nullable = false)
	private String optionName;

	@Column(name = "sku_code", length = 50)
	private String skuCode;

	@Column(name = "vote_count")
	private Integer voteCount = 0;
}
