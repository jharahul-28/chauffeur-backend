package com.velvet.rest.services.booking.beans;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.velvet.rest.services.booking.entity.BookingConstants.CustomerType;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingMetadata implements Serializable {

	private static final long serialVersionUID = 1L;

	private String customerName;
	private String customerContact;
	private CustomerType customerType = CustomerType.NORMAL;
	private BigDecimal customerTip;
	private String specialInstructions;
	private String remarks;
}
