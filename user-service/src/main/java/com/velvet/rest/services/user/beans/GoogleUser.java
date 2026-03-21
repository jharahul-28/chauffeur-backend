package com.velvet.rest.services.user.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class GoogleUser {

	@JsonProperty("sub")
	private String id;

	@JsonProperty("given_name")
	private String firstName;

	@JsonProperty("family_name")
	private String lastName;

	private String email;

	private String picture;

}
