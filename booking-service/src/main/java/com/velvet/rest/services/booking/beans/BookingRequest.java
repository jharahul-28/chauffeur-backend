package com.velvet.rest.services.booking.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.velvet.rest.services.booking.entity.BookingConstants.BookingSource;
import com.velvet.rest.services.booking.entity.BookingConstants.CustomerType;
import com.velvet.rest.services.booking.entity.BookingConstants.PaymentMode;
import com.velvet.rest.services.booking.entity.BookingConstants.PaymentStatus;
import com.velvet.rest.services.booking.entity.BookingConstants.RideType;
import com.velvet.rest.services.booking.entity.BookingConstants.TripType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "Booking Request")
@Data
public class BookingRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "Existing booking ID — null on create, required on update")
	private Long bookingId;

	// --- Customer (mandatory on create) ---

	@ApiModelProperty(value = "Customer ID")
	private Long customerId;

	@ApiModelProperty(value = "Customer type — required on create")
	private CustomerType customerType;

	@ApiModelProperty(value = "Customer name — required on create")
	private String customerName;

	@ApiModelProperty(value = "Customer contact number — required on create")
	private String customerContact;

	// --- Ride details (mandatory on create) ---

	@ApiModelProperty(value = "List of waypoints (PICKUP and DROP) — required on create")
	private List<WaypointRequest> waypoints;

	@ApiModelProperty(value = "Booking source — required on create")
	private BookingSource bookingSource;

	@ApiModelProperty(value = "Ride type — required on create")
	private RideType rideType;

	@ApiModelProperty(value = "Trip type — required on create")
	private TripType tripType;

	@ApiModelProperty(value = "Date of ride")
	private LocalDate rideDate;

	@ApiModelProperty(value = "Pickup time")
	private LocalTime pickupTime;

	@ApiModelProperty(value = "Distance in km")
	private BigDecimal kmRide;

	// --- Fare ---

	@ApiModelProperty(value = "Total fare")
	private BigDecimal totalFare;

	@ApiModelProperty(value = "Advance / payment received")
	private BigDecimal advanceAmount;

	@ApiModelProperty(value = "Parking fee")
	private BigDecimal parkingFee;

	@ApiModelProperty(value = "Payment mode")
	private PaymentMode paymentMode;

	@ApiModelProperty(value = "Payment status")
	private PaymentStatus paymentStatus;

	// --- Notes ---

	@ApiModelProperty(value = "Special instructions")
	private String specialInstructions;

	@ApiModelProperty(value = "Remarks")
	private String remarks;
}
