package com.velvet.rest.services.user.beans;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * LoginPasswordRequest class.
 * </p>
 *
 * @author Mohit
 * @version $Id: $Id
 */
@Data
@ApiModel(description = "Login with Password Request.")
public class LoginPasswordRequest implements Serializable {

	private static final long serialVersionUID = -5255885521766125895L;

	@ApiModelProperty(value = "Username Email Id/Mobile No, Max length is set to 100 to support both email and mobile")
	@NotNull
	@Length(max = 100)
	private String username;

	@ApiModelProperty(value = "Password")
	@NotNull
	private String password;
}
