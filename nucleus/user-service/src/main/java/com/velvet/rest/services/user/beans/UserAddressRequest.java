package com.velvet.rest.services.user.beans;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Bean to be returned with Contains Application Level Addresses
 *
 * @author Mohit
 * @version $Id: $Id
 */
@Data
@ApiModel(description = "Address Bean")
public class UserAddressRequest implements Serializable {

	private static final long serialVersionUID = 2102504245219017738L;

	@ApiModelProperty(value = "tower")
	private String tower;

	@ApiModelProperty(value = "floor")
	private String floor;

	@ApiModelProperty(value = "Org Address id")
	@NotNull
	private Long orgAddressId;
}
