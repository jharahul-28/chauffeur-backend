package com.velvet.rest.services.booking.beans;

import java.io.Serializable;

import com.velvet.rest.services.booking.entity.BookingVehicleConstants.VehicleAssignmentStatus;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "Booking Vehicle Bean")
@Data
public class BookingVehicleBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "ID")
	private Long id;

	@ApiModelProperty(value = "Vehicle category ID")
	private Long vehicleCategoryId;

	@ApiModelProperty(value = "Vehicle ID")
	private Long vehicleId;

	@ApiModelProperty(value = "Chauffeur ID")
	private Long chauffeurId;

	@ApiModelProperty(value = "Vehicle license plate number")
	private String vehicleNumber;

	@ApiModelProperty(value = "Vehicle assignment status")
	private VehicleAssignmentStatus status;

	@ApiModelProperty(value = "Vehicle and chauffeur metadata")
	private BookingVehicleMetadata metadata;

	public static BookingVehicleBean newInstance() {
		return new BookingVehicleBean();
	}
}
