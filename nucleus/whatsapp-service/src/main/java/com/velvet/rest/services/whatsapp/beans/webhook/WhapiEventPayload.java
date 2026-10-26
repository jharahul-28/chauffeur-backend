package com.velvet.rest.services.whatsapp.beans.webhook;

import java.io.Serializable;

import lombok.Data;

/**
 * Event metadata from Whapi webhook.
 *
 * @author mohit
 */
@Data
public class WhapiEventPayload implements Serializable {

	private static final long serialVersionUID = 1L;

	private String type;

	private String event;
}
