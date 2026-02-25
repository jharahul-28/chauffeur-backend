package com.velvet.rest.services.user.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * AuthResponse class.
 * </p>
 *
 * @author Mohit
 * @version $Id: $Id
 */
@Data
public class AuthResponse {

	@ApiModelProperty(value = "Auth Token generated. The token is also sent in header")
	@JsonInclude(Include.NON_NULL)
	private String authToken;

	@ApiModelProperty("User Details")
	@JsonInclude(Include.NON_NULL)
	private UserBean user;
}
