package com.velvet.rest.services.user.beans;

import java.io.Serializable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class OrgCodeEmailRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotEmpty(message = "Email is required")
	@Email(message = "Invalid email format")
	private String email;
}
