package com.velvet.rest.services.whatsapp.beans.webhook;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Payload for group participants update events from Whapi webhook.
 *
 * @author mohit
 */
@Data
public class WhapiGroupParticipantsPayload implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("group_id")
	private String groupId;

	private List<String> participants;

	private String action; // add, remove, promote, demote

	@JsonProperty("performed_by")
	private String performedBy;
}
