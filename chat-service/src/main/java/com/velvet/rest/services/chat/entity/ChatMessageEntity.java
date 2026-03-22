package com.velvet.rest.services.chat.entity;

import java.util.Map;

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
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import com.velvet.rest.common.websupport.base.BaseEntity;

import lombok.Data;

@Entity
@Table(name = "chat_messages")
@DynamicUpdate
@Where(clause = "active = 1")
@Data
public class ChatMessageEntity extends BaseEntity {

	private static final long serialVersionUID = -7676800333335976437L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "session_id", nullable = false)
	private Long sessionId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "session_id", updatable = false, insertable = false)
	private ChatSessionEntity session;

	@Column(nullable = false, length = 20)
	private String role;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@Column(length = 50)
	private String intent;

	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb")
	private Map<String, String> metadata;

}
