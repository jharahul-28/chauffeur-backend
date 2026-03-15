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

@ApiModel(description = "Vehicle Bean")
@Data
public class VehicleBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "ID of the vehicle")
	private Long id;

	@NotBlank
	@ApiModelProperty(value = "Vehicle registration number")
	private String vehicleNumber;

	@ApiModelProperty(value = "ID of the vehicle category")
	private Long categoryId;

	@NotBlank
	@ApiModelProperty(value = "Vehicle brand")
	private String brand;

	@NotBlank
	@ApiModelProperty(value = "Vehicle model")
	private String model;

	@NotNull
	@ApiModelProperty(value = "Seating capacity")
	private Integer seatingCapacity;

	@NotNull
	@ApiModelProperty(value = "Fuel type")
	private FuelType fuelType;

	@NotNull
	@ApiModelProperty(value = "Transmission type")
	private Transmission transmission;

	@ApiModelProperty(value = "Whether the vehicle is available for assignment")
	private Boolean available;

	@ApiModelProperty(value = "Vehicle metadata")
	private VehicleMetadata metadata;

	@ApiModelProperty(value = "URL of the vehicle image")
	private String imageUrl;

	public static VehicleBean newInstance() {
		return new VehicleBean();
	}
}
