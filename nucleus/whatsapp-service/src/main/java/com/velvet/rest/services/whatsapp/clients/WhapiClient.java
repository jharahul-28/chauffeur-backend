package com.velvet.rest.services.whatsapp.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.velvet.rest.services.whatsapp.beans.WhapiGroupResponse;
import com.velvet.rest.services.whatsapp.beans.WhapiSendPollRequest;
import com.velvet.rest.services.whatsapp.beans.WhapiSendPollResponse;
import com.velvet.rest.services.whatsapp.beans.WhapiSendTextRequest;
import com.velvet.rest.services.whatsapp.beans.WhapiSendTextResponse;

@FeignClient(value = "whapiClient", url = "${client.whapi.url}", configuration = { FeignWhapiConfiguration.class })
public interface WhapiClient {

	@PostMapping(value = "/messages/poll", consumes = "application/json")
	WhapiSendPollResponse sendPoll(@RequestBody WhapiSendPollRequest request);

	@PostMapping(value = "/messages/text", consumes = "application/json")
	WhapiSendTextResponse sendTextMessage(@RequestBody WhapiSendTextRequest request);

	@GetMapping(value = "/groups/{groupId}")
	WhapiGroupResponse getGroup(@PathVariable String groupId);
}
