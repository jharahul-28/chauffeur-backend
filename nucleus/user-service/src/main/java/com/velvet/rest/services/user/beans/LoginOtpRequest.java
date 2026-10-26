package com.velvet.rest.services.user.beans;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * LoginOtpRequest class.
 * </p>
 *
 * @author Mohit
 * @version $Id: $Id
 */
@Data
@ApiModel(description = "Login with OTP Request. Includes Mobile No, Otp and Retry Options")
public class LoginOtpRequest implements Serializable {

	private static final long serialVersionUID = 777038794575602211L;

	private String countryCode;

	@ApiModelProperty(value = "Phone/Email cannot be empty", allowEmptyValue = false)
	@NotNull
	private String phone;

	@Length(min = 4, max = 6)
	@NotNull
	private String otp;

}
