package com.velvet.rest.services.booking.beans;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingVehicleMetadata implements Serializable {

	private static final long serialVersionUID = 1L;

	private String categoryName;
	private String chauffeurName;
	private String chauffeurContact;
	private String vehicleBrand;
	private String vehicleModel;
}
