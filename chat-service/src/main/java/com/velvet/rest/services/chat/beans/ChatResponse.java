package com.velvet.rest.services.chat.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {

	private String message;

	// session ID from chat messages
	private String sessionId;

	private String intent;
}
