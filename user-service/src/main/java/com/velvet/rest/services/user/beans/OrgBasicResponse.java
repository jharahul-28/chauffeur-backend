package com.velvet.rest.services.user.beans;

import java.io.Serializable;
import java.util.UUID;

import lombok.Data;

@Data
public class OrgBasicResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private UUID id;

	private String name;

	private String code;

	private String logoUrl;

	private String whatsappGroupLink;
}
