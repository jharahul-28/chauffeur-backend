package com.velvet.rest.services.whatsapp.beans.webhook;

import java.io.Serializable;

import lombok.Data;

/**
 * Participant payload from Whapi webhook.
 *
 * @author mohit
 */
@Data
public class WhapiParticipantPayload implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	private String rank;
}
