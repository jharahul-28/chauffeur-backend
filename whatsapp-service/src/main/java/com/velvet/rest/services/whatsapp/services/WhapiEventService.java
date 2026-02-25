package com.velvet.rest.services.whatsapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.velvet.rest.services.whatsapp.beans.webhook.WhapiEventPayload;
import com.velvet.rest.services.whatsapp.beans.webhook.WhapiWebhookRequest;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WhapiEventService {

	@Autowired
	private WhapiGroupEventHandler groupEventHandler;

	@Autowired
	private WhapiMessageEventHandler messageEventHandler;

	public void processEvent(WhapiWebhookRequest payload) {

		WhapiEventPayload event = payload.getEvent();

		if (event == null) {
			log.warn("No event block found");
			return;
		}

		String eventType = event.getType();

		switch (eventType) {
		case "groups":
			groupEventHandler.handle(payload);
			break;

		case "messages":
			messageEventHandler.handle(payload);
			break;

		default:
			log.warn("Unhandled event type: {}", eventType);
		}
	}
}
