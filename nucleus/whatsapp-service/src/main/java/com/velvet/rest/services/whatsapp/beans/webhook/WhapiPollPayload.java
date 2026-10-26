package com.velvet.rest.services.whatsapp.beans.webhook;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * Poll payload from Whapi webhook.
 *
 * @author mohit
 */
@Data
public class WhapiPollPayload implements Serializable {

	private static final long serialVersionUID = 1L;

	private String title;

	private List<String> options;

	private Integer total;

	private List<WhapiPollResultPayload> results;
}
