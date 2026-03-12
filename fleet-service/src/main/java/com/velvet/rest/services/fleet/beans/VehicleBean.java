package com.velvet.rest.services.fleet.beans;

import java.io.Serializable;

import com.velvet.rest.services.fleet.entity.VehicleConstants.FuelType;
import com.velvet.rest.services.fleet.entity.VehicleConstants.Transmission;
import com.velvet.rest.services.fleet.entity.VehicleMetadata;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "Vehicle Bean")
@Data
public class VehicleBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "ID of the vehicle")
	private Long id;

	@ApiModelProperty(value = "Vehicle registration number")
	private String vehicleNumber;

	@ApiModelProperty(value = "ID of the vehicle category")
	private Long categoryId;

	@ApiModelProperty(value = "Vehicle brand")
	private String brand;

	@ApiModelProperty(value = "Vehicle model")
	private String model;

	@ApiModelProperty(value = "Seating capacity")
	private Integer seatingCapacity;

	@ApiModelProperty(value = "Fuel type")
	private FuelType fuelType;

	@ApiModelProperty(value = "Transmission type")
	private Transmission transmission;

	@ApiModelProperty(value = "Whether the vehicle is available for assignment")
	private Boolean available;

	@ApiModelProperty(value = "Vehicle metadata")
	private VehicleMetadata metadata;

	public static VehicleBean newInstance() {
		return new VehicleBean();
	}
}
