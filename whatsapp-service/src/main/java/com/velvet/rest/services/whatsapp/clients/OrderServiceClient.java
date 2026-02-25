package com.velvet.rest.services.whatsapp.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.velvet.rest.common.openfeign.FeignInternalConfiguration;
import com.velvet.rest.services.whatsapp.beans.CartRequest;

@FeignClient(value = "WhapiOrderServiceClient", url = "${client.order.url}", configuration = { FeignInternalConfiguration.class })
public interface OrderServiceClient {

	@PostMapping(value = "/orders/cart")
	ResponseEntity<Void> addToCart(@RequestBody CartRequest request);
}
