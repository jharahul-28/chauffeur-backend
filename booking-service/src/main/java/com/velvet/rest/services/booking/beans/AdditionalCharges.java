package com.velvet.rest.services.booking.beans;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdditionalCharges implements Serializable {

	private static final long serialVersionUID = 1L;

	private BigDecimal mcFare;

	private BigDecimal waitingCharge;

	private BigDecimal parkingCharge;

}
