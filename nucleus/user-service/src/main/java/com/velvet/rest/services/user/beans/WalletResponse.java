package com.velvet.rest.services.user.beans;

import lombok.Data;

@Data
public class WalletResponse {

	private Long userId;

	private Double amount;

	private Double creditLimit;

}
