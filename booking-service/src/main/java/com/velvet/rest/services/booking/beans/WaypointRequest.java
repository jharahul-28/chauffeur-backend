package com.velvet.rest.services.booking.beans;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "Waypoint Request")
@Data
public class WaypointRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull
	@ApiModelProperty(value = "Latitude", required = true)
	private BigDecimal latitude;

	@NotNull
	@ApiModelProperty(value = "Longitude", required = true)
	private BigDecimal longitude;

	@NotBlank
	@ApiModelProperty(value = "Human-readable address", required = true)
	private String address;

	@ApiModelProperty(value = "Position to insert at — if not provided, appends to end")
	private Integer waypointSequence;
}
