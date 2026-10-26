package com.velvet.rest.services.whatsapp.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.velvet.rest.common.openfeign.FeignInternalConfiguration;
import com.velvet.rest.services.whatsapp.beans.CreateCustomerRequest;
import com.velvet.rest.services.whatsapp.beans.CustomerResponse;

/**
 * Feign client for User Service.
 */
@FeignClient(value = "WhapiUserServiceClient", url = "${client.user.url}", configuration = { FeignInternalConfiguration.class })
public interface UserServiceClient {

	@PostMapping(value = "/users/customers/internal/whatsapp")
	ResponseEntity<CustomerResponse> createCustomer(@RequestBody CreateCustomerRequest request);
}
