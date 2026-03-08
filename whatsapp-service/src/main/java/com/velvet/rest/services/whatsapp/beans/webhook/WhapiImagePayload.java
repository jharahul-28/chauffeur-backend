package com.velvet.rest.services.whatsapp.beans.webhook;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Image message payload from Whapi webhook.
 *
 * @author mohit
 */
@Data
public class WhapiImagePayload implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	@JsonProperty("mime_type")
	private String mimeType;

	@JsonProperty("file_size")
	private Long fileSize;

	private String sha256;

	private Integer width;

	private Integer height;
}
