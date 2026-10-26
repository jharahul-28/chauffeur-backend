package com.velvet.rest.services.user.beans;

import java.io.Serializable;

import com.velvet.rest.services.user.constants.UserConstants.Gender;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Request containing Customer Info
 *
 * @author Mohit
 * @version $Id: $Id
 */
@Data
@ApiModel(description = "User Request For update info")
public class CustomerInfoRequest implements Serializable {

	private static final long serialVersionUID = 1632130020507453176L;

	@ApiModelProperty(value = "name of user")
	private String name;

	@ApiModelProperty(value = "Email of user")
	private String email;

	@ApiModelProperty(value = "Gender of user")
	private Gender gender;
}
