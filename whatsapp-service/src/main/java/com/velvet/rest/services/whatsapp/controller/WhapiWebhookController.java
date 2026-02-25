package com.velvet.rest.services.whatsapp.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.velvet.rest.services.whatsapp.beans.webhook.WhapiWebhookRequest;
import com.velvet.rest.services.whatsapp.services.WhapiEventService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class WhapiWebhookController {

	@Autowired
	private WhapiEventService eventService;

	@PostMapping("/whatsapp/whapi/webhook")
	public ResponseEntity<Void> receiveEvent(@RequestBody WhapiWebhookRequest payload, @RequestHeader Map<String, String> headers) {
		try {
			log.info("WHAPI webhook payload: {}", payload);
			log.info("WHAPI webhook headers: {}", headers);

			// OPTIONAL: verify signature here
			eventService.processEvent(payload);

			// IMPORTANT: always return 200 fast
			return ResponseEntity.ok().build();

		} catch (Exception e) {
			log.error("Webhook processing failed", e);

			// Still return 200 to avoid retries explosion
			return ResponseEntity.ok().build();
		}
	}
}
