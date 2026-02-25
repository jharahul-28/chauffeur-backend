package com.velvet.rest.services.whatsapp.services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WhapiEventService {

	@Autowired
	private WhapiGroupEventHandler groupEventHandler;

	@Autowired
	private WhapiMessageEventHandler messageEventHandler;

	@SuppressWarnings("unchecked")
	public void processEvent(Map<String, Object> payload) {

		Map<String, Object> event = (Map<String, Object>) payload.get("event");

		if (event == null) {
			log.warn("No event block found");
			return;
		}

		String eventType = (String) event.get("type");

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
