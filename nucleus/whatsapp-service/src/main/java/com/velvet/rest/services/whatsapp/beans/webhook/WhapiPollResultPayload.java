package com.velvet.rest.services.whatsapp.beans.webhook;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * Poll result payload from Whapi webhook.
 *
 * @author mohit
 */
@Data
public class WhapiPollResultPayload implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;

	private List<String> voters;

	private Integer count;

	private String id;
}
