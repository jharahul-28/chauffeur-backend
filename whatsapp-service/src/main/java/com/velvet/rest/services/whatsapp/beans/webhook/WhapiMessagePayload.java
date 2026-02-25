package com.velvet.rest.services.whatsapp.beans.webhook;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Message payload from Whapi webhook.
 *
 * @author mohit
 */
@Data
public class WhapiMessagePayload implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	@JsonProperty("from_me")
	private Boolean fromMe;

	private String type;

	@JsonProperty("chat_id")
	private String chatId;

	private Long timestamp;

	private String source;

	@JsonProperty("device_id")
	private Integer deviceId;

	@JsonProperty("chat_name")
	private String chatName;

	private String status;

	private String from;

	@JsonProperty("from_name")
	private String fromName;

	private WhapiPollPayload poll;

	private WhapiActionPayload action;

	private WhapiTextPayload text;
}
