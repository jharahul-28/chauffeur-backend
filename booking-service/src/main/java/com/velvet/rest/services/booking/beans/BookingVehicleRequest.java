package com.velvet.rest.services.booking.beans;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "Booking Vehicle Assignment Request")
@Data
public class BookingVehicleRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "Existing assignment ID — null on create, required on update")
	private Long id;

	@ApiModelProperty(value = "Booking ID — required on create")
	private Long bookingId;

	@ApiModelProperty(value = "Vehicle category ID — required on create")
	private Long vehicleCategoryId;

	@ApiModelProperty(value = "Vehicle ID — optional, assigned in phase 2")
	private Long vehicleId;

	@ApiModelProperty(value = "Chauffeur ID — optional, assigned in phase 3")
	private Long chauffeurId;
}
