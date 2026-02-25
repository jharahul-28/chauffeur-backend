package com.velvet.rest.services.whatsapp.beans.webhook;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * Action payload from Whapi webhook (for votes, reactions, etc.).
 *
 * @author mohit
 */
@Data
public class WhapiActionPayload implements Serializable {

	private static final long serialVersionUID = 1L;

	private String target;

	private String type;

	private List<String> votes;
}
