package com.velvet.rest.services.user.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.velvet.rest.common.openfeign.FeignInternalConfiguration;
import com.velvet.rest.services.user.beans.WhapiSendTextRequest;

@FeignClient(value = "WhatsappServiceUserClient", url = "${client.whatsapp.url}", configuration = { FeignInternalConfiguration.class })
public interface WhatsappServiceClient {

	@PostMapping(value = "/whatsapp/messages/text", consumes = "application/json")
	void sendWhatsappText(@RequestBody WhapiSendTextRequest request);
}
