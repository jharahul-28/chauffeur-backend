package com.velvet.rest.services.fleet.beans;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "Vehicle Category Bean")
@Data
public class VehicleCategoryBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "ID of the vehicle category")
	private Long id;

	@ApiModelProperty(value = "Name of the vehicle category")
	private String categoryName;

	@ApiModelProperty(value = "Base price of the vehicle category")
	private BigDecimal basePrice;

	@ApiModelProperty(value = "Active status of the vehicle category")
	private Integer active;

	public static VehicleCategoryBean newInstance() {
		return new VehicleCategoryBean();
	}
}
