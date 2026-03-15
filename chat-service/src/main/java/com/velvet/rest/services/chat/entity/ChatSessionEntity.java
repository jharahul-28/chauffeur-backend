package com.velvet.rest.services.chat.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;

import com.velvet.rest.common.websupport.base.BaseEntity;

import lombok.Data;

@Entity
@Table(name = "chat_sessions")
@DynamicUpdate
@Where(clause = "active = 1")
@Data
public class ChatSessionEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String phone;

	private String stage;

	@Column(name = "reasoning_summary", columnDefinition = "TEXT")
	private String reasoningSummary;
}
