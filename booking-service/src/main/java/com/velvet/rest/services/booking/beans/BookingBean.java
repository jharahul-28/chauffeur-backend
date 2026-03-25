package com.velvet.rest.services.booking.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.velvet.rest.services.booking.entity.BookingConstants.BookingSource;
import com.velvet.rest.services.booking.entity.BookingConstants.PaymentMode;
import com.velvet.rest.services.booking.entity.BookingConstants.PaymentStatus;
import com.velvet.rest.services.booking.entity.BookingConstants.RideType;
import com.velvet.rest.services.booking.entity.BookingConstants.Status;
import com.velvet.rest.services.booking.entity.BookingConstants.TripType;

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

	@ApiModelProperty(value = "Date the booking was created")
	private LocalDate bookingDate;

	@ApiModelProperty(value = "Booking status")
	private Status status;

	// --- Customer ---

	@ApiModelProperty(value = "Customer ID")
	private Long customerId;

	@ApiModelProperty(value = "Customer metadata (name, phone, customerType)")
	private BookingMetadata metadata;

	// --- Ride details ---

	@ApiModelProperty(value = "Date of the ride")
	private LocalDate rideDate;

	@ApiModelProperty(value = "Pickup time")
	private LocalTime pickupTime;

	@ApiModelProperty(value = "Booking source")
	private BookingSource bookingSource;

	@ApiModelProperty(value = "Ride type")
	private RideType rideType;

	@ApiModelProperty(value = "Trip type")
	private TripType tripType;

	@ApiModelProperty(value = "Distance in km")
	private BigDecimal kmRide;

	// --- Fare ---

	@ApiModelProperty(value = "Total fare")
	private BigDecimal totalFare;

	@ApiModelProperty(value = "Advance / payment received")
	private BigDecimal advanceAmount;

	@ApiModelProperty(value = "Balance amount (totalFare - advanceAmount - parkingFee - customerTip)")
	private BigDecimal balanceAmount;

	@ApiModelProperty(value = "Parking fee")
	private BigDecimal parkingFee;

	@ApiModelProperty(value = "Payment mode")
	private PaymentMode paymentMode;

	@ApiModelProperty(value = "Payment status")
	private PaymentStatus paymentStatus;

	// --- Waypoints ---

	@ApiModelProperty(value = "Waypoints (pickup, stops, and drop locations)")
	private List<BookingWaypointBean> waypoints;

	public static BookingBean newInstance() {
		return new BookingBean();
	}
}
