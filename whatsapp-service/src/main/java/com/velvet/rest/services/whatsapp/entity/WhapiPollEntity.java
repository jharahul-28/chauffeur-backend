package com.velvet.rest.services.whatsapp.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.velvet.rest.common.websupport.base.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "whapi_polls")
@DynamicUpdate
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WhapiPollEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "wa_poll_message_id", nullable = false, unique = true)
	private String waPollMessageId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chat_id", nullable = false)
	private WhapiChatEntity chat;

	@Column(name = "title")
	private String title;

	@Column(name = "total_votes")
	private Integer totalVotes = 0;

	@Column(name = "created_by_wa_id")
	private String createdByWaId;

	@Column(name = "created_at_ts")
	private Long createdAtTs;

	@Column(name = "meal_type", length = 50)
	private String mealType;

	@Column(name = "delivery_date")
	private Date deliveryDate;
}
