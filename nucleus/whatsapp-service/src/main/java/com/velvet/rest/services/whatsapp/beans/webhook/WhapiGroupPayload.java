package com.velvet.rest.services.whatsapp.beans.webhook;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Group payload from Whapi webhook.
 *
 * @author mohit
 */
@Data
public class WhapiGroupPayload implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	private String name;

	private String type;

	private Long timestamp;

	private List<WhapiParticipantPayload> participants;

	@JsonProperty("participants_count")
	private Integer participantsCount;

	@JsonProperty("name_at")
	private Long nameAt;

	@JsonProperty("created_at")
	private Long createdAt;

	@JsonProperty("created_by")
	private String createdBy;

	@JsonProperty("adminAddMemberMode")
	private Boolean adminAddMemberMode;

	@JsonProperty("performed_by")
	private String performedBy;
}
