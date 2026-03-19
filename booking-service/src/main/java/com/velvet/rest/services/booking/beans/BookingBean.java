package com.velvet.rest.services.booking.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.velvet.rest.services.booking.entity.BookingConstants.BeverageType;
import com.velvet.rest.services.booking.entity.BookingConstants.Status;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "Booking Bean")
@Data
public class BookingBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "ID of the booking")
	private Long id;

	@ApiModelProperty(value = "Unique booking reference")
	private String bookingReference;

	@ApiModelProperty(value = "Customer ID")
	private Long customerId;

	@ApiModelProperty(value = "Date of the ride")
	private LocalDate rideDate;

	@ApiModelProperty(value = "Time of the ride")
	private LocalTime rideTime;

	@ApiModelProperty(value = "Vehicle category ID")
	private Long vehicleCategoryId;

	@ApiModelProperty(value = "Assigned vehicle ID")
	private Long vehicleId;

	@ApiModelProperty(value = "Assigned chauffeur ID")
	private Long chauffeurId;

	@ApiModelProperty(value = "Booking status")
	private Status status;

	@ApiModelProperty(value = "Beverage preference")
	private BeverageType beverageType;

	@ApiModelProperty(value = "Special request")
	private String specialRequest;

	@ApiModelProperty(value = "Estimated price")
	private BigDecimal estimatedPrice;

	@ApiModelProperty(value = "Estimated distance in km")
	private BigDecimal estimatedDistanceKm;

	@ApiModelProperty(value = "Waypoints (pickup and drop locations)")
	private List<BookingWaypointBean> waypoints;

	public static BookingBean newInstance() {
		return new BookingBean();
	}
}
