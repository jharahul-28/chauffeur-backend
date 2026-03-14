package com.velvet.rest.services.fleet.beans;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.velvet.rest.services.fleet.entity.VehicleConstants.FuelType;
import com.velvet.rest.services.fleet.entity.VehicleConstants.Transmission;
import com.velvet.rest.services.fleet.entity.VehicleMetadata;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "Request body for updating a vehicle")
@Data
public class VehicleUpdateRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "Vehicle registration number", required = true)
	@NotBlank
	private String vehicleNumber;

	@ApiModelProperty(value = "ID of the vehicle category")
	private Long categoryId;

	@ApiModelProperty(value = "Vehicle brand", required = true)
	@NotBlank
	private String brand;

	@ApiModelProperty(value = "Vehicle model", required = true)
	@NotBlank
	private String model;

	@ApiModelProperty(value = "Seating capacity", required = true)
	@NotNull
	private Integer seatingCapacity;

	@ApiModelProperty(value = "Fuel type", required = true)
	@NotNull
	private FuelType fuelType;

	@ApiModelProperty(value = "Transmission type", required = true)
	@NotNull
	private Transmission transmission;

	@ApiModelProperty(value = "Whether the vehicle is available for assignment")
	private Boolean available;

	@ApiModelProperty(value = "Vehicle metadata (registration, insurance, PUC expiry dates)")
	private VehicleMetadata metadata;

	@ApiModelProperty(value = "URL of the vehicle image")
	private String imageUrl;
}
