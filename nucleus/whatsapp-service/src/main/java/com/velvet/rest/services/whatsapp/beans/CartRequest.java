package com.velvet.rest.services.whatsapp.beans;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long customerId;

	private Date deliveryDate;

	private String orderType;

	private String skuCode;

	private Double quantity;

	private String channel;
}
