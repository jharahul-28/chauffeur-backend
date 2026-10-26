package com.velvet.rest.services.whatsapp.beans.webhook;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Base webhook request from Whapi.
 *
 * @author mohit
 */
@Data
public class WhapiWebhookRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<WhapiGroupPayload> groups;

	@JsonProperty("groups_participants")
	private List<WhapiGroupParticipantsPayload> groupsParticipants;

	private List<WhapiMessagePayload> messages;

	private WhapiEventPayload event;

	@JsonProperty("channel_id")
	private String channelId;
}
