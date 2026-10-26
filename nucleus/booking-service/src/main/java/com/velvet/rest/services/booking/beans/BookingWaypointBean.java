package com.velvet.rest.services.booking.beans;

import java.io.Serializable;
import java.math.BigDecimal;

import com.velvet.rest.services.booking.entity.BookingWaypointConstants.WaypointType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "Booking Waypoint Bean")
@Data
public class BookingWaypointBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "ID of the waypoint")
	private Long id;

	@ApiModelProperty(value = "Sequence order in the route")
	private Integer waypointSequence;

	@ApiModelProperty(value = "Type of waypoint: PICKUP or DROP")
	private WaypointType waypointType;

	@ApiModelProperty(value = "Latitude")
	private BigDecimal latitude;

	@ApiModelProperty(value = "Longitude")
	private BigDecimal longitude;

	@ApiModelProperty(value = "Human-readable address")
	private String address;

	public static BookingWaypointBean newInstance() {
		return new BookingWaypointBean();
	}
}
