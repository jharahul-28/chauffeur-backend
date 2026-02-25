package com.velvet.rest.services.whatsapp.beans;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WhapiSendTextResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private Boolean sent;

	private MessageWrapper message;

	@Data
	@NoArgsConstructor
	public static class MessageWrapper implements Serializable {

		private static final long serialVersionUID = 1L;

		private String id;

		@JsonProperty("from_me")
		private Boolean fromMe;

		private String from;

		private String type;

		@JsonProperty("chat_id")
		private String chatId;

		private Long timestamp;

		private String source;

		private String status;

		private TextWrapper text;
	}

	@Data
	@NoArgsConstructor
	public static class TextWrapper implements Serializable {

		private static final long serialVersionUID = 1L;

		private String body;
	}
}
