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
@Table(name = "user_feedback")
@DynamicUpdate
@Data
@EqualsAndHashCode(callSuper = false)
public class FeedbackEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Integer userId;

	@Column(name = "feedback_text", nullable = false, columnDefinition = "TEXT")
	private String feedbackText;

	@Column(name = "status", nullable = false, length = 20)
	private String status;
}
