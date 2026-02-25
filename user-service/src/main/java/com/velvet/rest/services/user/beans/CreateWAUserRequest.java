package com.velvet.rest.services.user.beans;

import java.util.UUID;

import lombok.Data;

@Data
public class CreateWAUserRequest {

	private String phone;

	private UUID orgId;
}
