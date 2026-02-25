package com.velvet.rest.services.whatsapp.services;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.velvet.rest.common.utils.ParamsUtils;
import com.velvet.rest.services.whatsapp.beans.WhapiSendTextRequest;
import com.velvet.rest.services.whatsapp.beans.webhook.WhapiMessagePayload;
import com.velvet.rest.services.whatsapp.beans.webhook.WhapiTextPayload;
import com.velvet.rest.services.whatsapp.beans.webhook.WhapiWebhookRequest;
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

	@Autowired
	private WhapiMessageService messageService;

	@Autowired
	@Qualifier("whatsappTaskScheduler")
	private TaskScheduler taskScheduler;

	public void handle(WhapiWebhookRequest payload) {

		List<WhapiMessagePayload> messages = payload.getMessages();

		if (messages == null)
			return;

		for (WhapiMessagePayload msg : messages) {

			String type = msg.getType();

			if ("poll".equals(type)) {
				pollEventHandler.handlePoll(msg);
			} else if ("action".equals(type)) {
				pollEventHandler.handleAction(msg);
			} else {
				handleRegularMessage(msg);
			}
		}
	}

	private void handleRegularMessage(WhapiMessagePayload msg) {
		String waMessageId = msg.getId();

		// Skip if message already exists
		if (messageRepository.findByWaMessageId(waMessageId).isPresent()) {
			return;
		}

		String chatId = msg.getChatId();
		WhapiChatEntity chat = chatRepository.findByWaChatId(chatId).orElseGet(() -> {
			WhapiChatEntity newChat = WhapiChatEntity.builder()
					.waChatId(chatId)
					.chatType(chatId.endsWith("@g.us") ? "GROUP" : "PRIVATE")
					.name(msg.getChatName())
					.build();
			return chatRepository.save(newChat);
		});

		// Extract text body if present
		String textBody = null;
		WhapiTextPayload text = msg.getText();
		if (text != null) {
			textBody = text.getBody();
		}

		WhapiMessageEntity message = WhapiMessageEntity.builder()
				.waMessageId(waMessageId)
				.chat(chat)
				.senderWaId(msg.getFrom())
				.senderName(msg.getFromName())
				.messageType(msg.getType())
				.textBody(textBody)
				.fromMe(msg.getFromMe())
				.messageTimestamp(msg.getTimestamp())
				.build();

		messageRepository.save(message);
		log.info("handleRegularMessage :: Message saved: {}", waMessageId);

		if (textBody != null && matchesInfoTrigger(textBody)) {
			long triggerTimestamp = System.currentTimeMillis() / 1000;

			// 1st message — immediate
			sendText(chatId, ParamsUtils.getParam("WA_INFO_REPLY_MESSAGE"));
			log.info("Auto-replied (msg1) to info query from chatId: {}", chatId);

			// 2nd message — after configured delay
			int msg2DelaySeconds = ParamsUtils.getIntegerParam("WA_INFO_MSG2_DELAY_SECONDS", 11);
			taskScheduler.schedule(
					() -> sendSecondMessage(chatId, triggerTimestamp),
					Instant.now().plusSeconds(msg2DelaySeconds));
		}
	}

	private void sendSecondMessage(String chatId, long triggerTimestamp) {
		sendText(chatId, ParamsUtils.getParam("WA_INFO_MSG2_MESSAGE"));
		log.info("Auto-replied (msg2) to chatId: {}", chatId);

		// 3rd message — if customer hasn't replied after configured delay
		int msg3DelaySeconds = ParamsUtils.getIntegerParam("WA_INFO_MSG3_NO_REPLY_DELAY_SECONDS", 300);
		taskScheduler.schedule(
				() -> sendThirdMessageIfNoReply(chatId, triggerTimestamp),
				Instant.now().plusSeconds(msg3DelaySeconds));
	}

	private void sendThirdMessageIfNoReply(String chatId, long triggerTimestamp) {
		boolean customerReplied = messageRepository
				.existsByChat_WaChatIdAndFromMeFalseAndMessageTimestampGreaterThan(chatId, triggerTimestamp);
		if (!customerReplied) {
			sendText(chatId, ParamsUtils.getParam("WA_INFO_MSG3_MESSAGE"));
			log.info("Auto-replied (msg3 - no reply follow-up) to chatId: {}", chatId);
		}
	}

	private void sendText(String chatId, String body) {
		String phone = chatId.contains("@") ? chatId.substring(0, chatId.indexOf("@")) : chatId;

		messageService.sendTextMessage(WhapiSendTextRequest.builder()
				.to(phone)
				.body(body)
				.build());
	}

	private boolean matchesInfoTrigger(String textBody) {
		String param = ParamsUtils.getParam("WA_INFO_TRIGGER_MESSAGE");
		if (param == null) return false;
		try {
			List<String> triggers = new ObjectMapper().readValue(param, new TypeReference<List<String>>() {});
			return triggers.stream().anyMatch(t -> t.equalsIgnoreCase(textBody));
		} catch (Exception e) {
			log.warn("Failed to parse WA_INFO_TRIGGER_MESSAGE as JSON list: {}", param);
			return false;
		}
	}
}
