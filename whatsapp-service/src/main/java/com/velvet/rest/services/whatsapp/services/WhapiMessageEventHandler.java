package com.velvet.rest.services.whatsapp.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.velvet.rest.services.whatsapp.entity.WhapiChatEntity;
import com.velvet.rest.services.whatsapp.entity.WhapiMessageEntity;
import com.velvet.rest.services.whatsapp.repository.WhapiChatRepository;
import com.velvet.rest.services.whatsapp.repository.WhapiMessageRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WhapiMessageEventHandler {

	@Autowired
	private WhapiMessageRepository messageRepository;

	@Autowired
	private WhapiChatRepository chatRepository;

	@Autowired
	private WhapiPollEventHandler pollEventHandler;

	@SuppressWarnings("unchecked")
	public void handle(Map<String, Object> payload) {

		List<Map<String, Object>> messages = (List<Map<String, Object>>) payload.get("messages");

		if (messages == null)
			return;

		for (Map<String, Object> msg : messages) {

			String type = (String) msg.get("type");

			if ("poll".equals(type)) {
				pollEventHandler.handlePoll(msg);
			} else if ("action".equals(type)) {
				pollEventHandler.handleAction(msg);
			} else {
				handleRegularMessage(msg);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void handleRegularMessage(Map<String, Object> msg) {
		String waMessageId = (String) msg.get("id");

		// Skip if message already exists
		if (messageRepository.findByWaMessageId(waMessageId).isPresent()) {
			return;
		}

		String chatId = (String) msg.get("chat_id");
		WhapiChatEntity chat = chatRepository.findByWaChatId(chatId).orElseGet(() -> {
			WhapiChatEntity newChat = WhapiChatEntity.builder()
					.waChatId(chatId)
					.chatType(chatId.endsWith("@g.us") ? "GROUP" : "PRIVATE")
					.name((String) msg.get("chat_name"))
					.build();
			return chatRepository.save(newChat);
		});

		// Extract text body if present
		String textBody = null;
		Map<String, Object> text = (Map<String, Object>) msg.get("text");
		if (text != null) {
			textBody = (String) text.get("body");
		}

		WhapiMessageEntity message = WhapiMessageEntity.builder()
				.waMessageId(waMessageId)
				.chat(chat)
				.senderWaId((String) msg.get("from"))
				.senderName((String) msg.get("from_name"))
				.messageType((String) msg.get("type"))
				.textBody(textBody)
				.fromMe((Boolean) msg.get("from_me"))
				.messageTimestamp(msg.get("timestamp") != null ? ((Number) msg.get("timestamp")).longValue() : null)
				.build();

		messageRepository.save(message);
		log.info("Message saved: {}", waMessageId);
	}
}
