package com.velvet.rest.services.whatsapp.clients;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.velvet.rest.services.whatsapp.beans.CreateCustomerRequest;
import com.velvet.rest.services.whatsapp.beans.CustomerResponse;
import com.velvet.rest.services.whatsapp.beans.WhapiSendTextRequest;
import com.velvet.rest.services.whatsapp.beans.WhapiSendTextResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * Client service wrapper for external service calls.
 *
 * @author mohit
 */
@Service
@Slf4j
public class WhatsappClientService {

	@Autowired
	private UserServiceClient userServiceClient;

	@Autowired
	private WhapiClient whapiClient;

	public void createCustomer(CreateCustomerRequest request) {
		try {
			userServiceClient.createCustomer(request);
		} catch (Exception e) {
			log.error("Failed to ensure user for waUserId: {}", request.getPhone(), e);
		}
	}

	public Long getOrCreateCustomerId(String phone, UUID orgId) {
		try {
			ResponseEntity<CustomerResponse> response = userServiceClient.createCustomer(new CreateCustomerRequest(phone));
			if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
				return response.getBody().getId();
			}
		} catch (Exception e) {
			log.error("Failed to get customerId for phone: {}", phone, e);
		}
		return null;
	}

	public WhapiSendTextResponse sendTextMessage(WhapiSendTextRequest request) {
		try {
			return whapiClient.sendTextMessage(request);
		} catch (Exception e) {
			log.error("Failed to send text message", e);
			return null;
		}
	}
}
