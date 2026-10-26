package com.velvet.rest.services.chat.beans;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ChatRequest {

	@NotNull(message = "phone is required")
	@NotBlank(message = "phone cannot be blank")
	private String phone;

	@NotNull(message = "Message is required")
	@NotBlank(message = "Message cannot be blank")
	private String message;

	// Optional. Session ID (frontend sends this)
	private Long sessionId;

	// Optional location coordinates for distance calculations
	private Double latitude;

	private Double longitude;
}
