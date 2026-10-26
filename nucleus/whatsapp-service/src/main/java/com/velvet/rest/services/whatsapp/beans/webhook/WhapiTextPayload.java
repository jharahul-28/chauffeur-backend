package com.velvet.rest.services.whatsapp.beans.webhook;

import java.io.Serializable;

import lombok.Data;

/**
 * Text message payload from Whapi webhook.
 *
 * @author mohit
 */
@Data
public class WhapiTextPayload implements Serializable {

	private static final long serialVersionUID = 1L;

	private String body;
}
