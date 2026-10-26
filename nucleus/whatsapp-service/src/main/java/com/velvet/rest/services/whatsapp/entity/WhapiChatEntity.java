package com.velvet.rest.services.whatsapp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.velvet.rest.common.websupport.base.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "whapi_chats")
@DynamicUpdate
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhapiChatEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "wa_chat_id", nullable = false, unique = true, length = 100)
	private String waChatId;

	@Column(name = "chat_type", nullable = false, length = 20)
	private String chatType; // PRIVATE | GROUP

	@Column(name = "name")
	private String name;

	@Column(name = "is_archived")
	private Boolean archived = false;

	@Column(name = "is_muted")
	private Boolean muted = false;
}
