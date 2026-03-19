package com.velvet.rest.services.booking.beans;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.velvet.rest.services.booking.entity.BookingConstants.BeverageType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "Booking Request")
@Data
public class BookingRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "Existing booking ID — null on Phase 1 (create), required on Phase 2 and 3 (update)")
	private Long bookingId;

	@ApiModelProperty(value = "Customer ID — required when bookingId is null")
	private Long customerId;

	@ApiModelProperty(value = "List of waypoints (PICKUP and DROP) — required when bookingId is null")
	private List<WaypointRequest> waypoints;

	@ApiModelProperty(value = "Date of ride")
	private LocalDate rideDate;

	@ApiModelProperty(value = "Time of ride")
	private LocalTime rideTime;

	@ApiModelProperty(value = "Vehicle category ID")
	private Long vehicleCategoryId;

	@ApiModelProperty(value = "Beverage preference")
	private BeverageType beverageType;

	@ApiModelProperty(value = "Any special request")
	private String specialRequest;
}
