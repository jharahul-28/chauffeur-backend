package com.velvet.rest.services.whatsapp.beans;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WhapiSendPollResponse implements Serializable {

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

		private PollWrapper poll;
	}

	@Data
	@NoArgsConstructor
	public static class PollWrapper implements Serializable {

		private static final long serialVersionUID = 1L;

		private String title;

		private List<String> options;

		private List<PollOptionResult> results;
	}

	@Data
	@NoArgsConstructor
	public static class PollOptionResult implements Serializable {

		private static final long serialVersionUID = 1L;

		private String name;

		private String id;

		private Integer count;
	}
}
