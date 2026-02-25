package com.velvet.rest.services.whatsapp.beans;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppUserRequest {

	private String phone;

	private UUID orgId;
}
