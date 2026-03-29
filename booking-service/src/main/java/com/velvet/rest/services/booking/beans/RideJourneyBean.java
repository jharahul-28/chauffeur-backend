package com.velvet.rest.services.booking.beans;

import java.io.Serializable;
import com.velvet.rest.services.booking.entity.BookingConstants.Status;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "Ride Journey Event Bean")
@Data
public class RideJourneyBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "ID of the journey event")
	private Long id;

	@ApiModelProperty(value = "Booking ID")
	private Long bookingId;

	@ApiModelProperty(value = "Booking vehicle assignment ID")
	private Long bookingVehicleId;

	@ApiModelProperty(value = "Chauffeur ID")
	private Long chauffeurId;

	@ApiModelProperty(value = "Ride status at this event")
	private Status status;

	public static RideJourneyBean newInstance() {
		return new RideJourneyBean();
	}
}
