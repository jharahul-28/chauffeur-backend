package com.velvet.rest.services.user.beans;

import java.io.Serializable;
import java.util.UUID;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.velvet.rest.common.websupport.base.BaseBean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Bean/DTO for UserAddress entity.
 *
 * @author mohit
 */
@Data
@ApiModel(description = "User Address details")
public class UserAddressBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "Address ID")
	private Integer id;

	@ApiModelProperty(value = "User ID")
	private Long userId;

	@ApiModelProperty(value = "Organisation ID (UUID)", required = true)
	@NotNull(message = "Organisation ID is required")
	private UUID orgId;

	@ApiModelProperty(value = "Organisation Address ID", required = true)
	@NotNull(message = "Organisation Address ID is required")
	private Integer orgAddressId;

	@ApiModelProperty(value = "Organisation Address", required = true)
	@NotEmpty(message = "Organisation Address is required")
	private String orgAddress;

	@ApiModelProperty(value = "Tower name", required = true)
	@NotEmpty(message = "Tower is required")
	private String tower;

	@ApiModelProperty(value = "Floor", required = true)
	@NotEmpty(message = "Floor is required")
	private String floor;

	@ApiModelProperty(value = "Active status (1 = active, 0 = inactive)")
	private Integer active;

	public static UserAddressBean newInstance() {
		return new UserAddressBean();
	}
}
