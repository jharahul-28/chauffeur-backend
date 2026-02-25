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

import org.hibernate.annotations.DynamicUpdate;

import com.velvet.rest.common.websupport.base.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "whapi_messages")
@DynamicUpdate
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhapiMessageEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "wa_message_id", nullable = false, unique = true)
	private String waMessageId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chat_id", nullable = false)
	private WhapiChatEntity chat;

	@Column(name = "sender_wa_id")
	private String senderWaId;

	@Column(name = "sender_name")
	private String senderName;

	@Column(name = "message_type", length = 30)
	private String messageType; // text, poll, action

	@Column(name = "text_body")
	private String textBody;

	@Column(name = "is_from_me")
	private Boolean fromMe;

	@Column(name = "status", length = 20)
	private String status;

	@Column(name = "message_timestamp")
	private Long messageTimestamp;
}
