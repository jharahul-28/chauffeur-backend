package com.velvet.rest.services.ai.beans;

import lombok.Data;

@Data
public class TripNameSuggestions implements java.io.Serializable {

	private static final long serialVersionUID = 8029440894618785163L;

	private String tripName;

	private String explanation;

	private String imageUrl;

}
