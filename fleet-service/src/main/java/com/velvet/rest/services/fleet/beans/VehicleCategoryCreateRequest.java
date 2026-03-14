package com.velvet.rest.services.fleet.beans;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "Request body for creating a vehicle category")
@Data
public class VehicleCategoryCreateRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "Name of the vehicle category", required = true)
	@NotBlank
	private String categoryName;

	@ApiModelProperty(value = "Base price of the vehicle category", required = true)
	@NotNull
	private BigDecimal basePrice;
}
