package com.velvet.rest.services.whatsapp.beans;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrgMenuItemResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private String skuCode;

	private String productName;

	private BigDecimal salePrice;

	private String categoryName;

	private Boolean isSample;

	private String menuType;
}
