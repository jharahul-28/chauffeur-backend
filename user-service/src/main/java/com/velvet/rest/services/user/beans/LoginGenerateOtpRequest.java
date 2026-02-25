package com.velvet.rest.services.user.beans;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Bean to be returned with Contains Application Level Auth User
 *
 * @author Mohit
 * @version $Id: $Id
 */
@Data
@ApiModel(description = "Generate Otp Request ")
public class LoginGenerateOtpRequest implements Serializable {

	private static final long serialVersionUID = 2102504245219017738L;

	private String countryCode;

	@ApiModelProperty(value = "phone of the user", allowEmptyValue = false, required = true)
	@NotEmpty
	private String phone;

	@ApiModelProperty(value = "firebase verificationId of the mobile no ", allowEmptyValue = false, required = true)
	@NotEmpty
	private String verificationId;

	public static LoginGenerateOtpRequest newInstance() {
		return new LoginGenerateOtpRequest();
	}
}
