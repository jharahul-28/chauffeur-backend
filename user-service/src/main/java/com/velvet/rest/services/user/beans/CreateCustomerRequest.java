package com.velvet.rest.services.user.beans;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.Data;

@Data
public class CreateCustomerRequest {

	private String countryCode;

	@NotBlank(message = "Phone number is required")
	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Not a valid phone number format")
	private String phone;

}
