package com.velvet.rest.services.user.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Request body for social login (Google, Apple)")
public class SocialAuth {

	@ApiModelProperty(value = "OAuth/identity token from the provider", required = true)
	private String authToken;

	@ApiModelProperty(value = "Full name. For Apple: send from first-time auth response (credential.fullName)", example = "John Doe")
	private String name;

	@ApiModelProperty(value = "First name. For Apple: send from credential.fullName.givenName on first sign-in", example = "John")
	private String firstName;

	@ApiModelProperty(value = "Last name. For Apple: send from credential.fullName.familyName on first sign-in", example = "Doe")
	private String lastName;

}
