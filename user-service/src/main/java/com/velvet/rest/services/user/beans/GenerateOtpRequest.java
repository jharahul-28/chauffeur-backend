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
public class GenerateOtpRequest implements Serializable {

	private static final long serialVersionUID = 2102504245219017738L;

	@ApiModelProperty(value = "phone/email of the user", allowEmptyValue = false, required = true)
	@NotEmpty
	private String referenceId;

	@ApiModelProperty(value = "firebase verificationId of the mobile no ", allowEmptyValue = false, required = true)
	@NotEmpty
	private String verificationId;

	public static GenerateOtpRequest newInstance() {
		return new GenerateOtpRequest();
	}
}
