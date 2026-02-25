package com.velvet.rest.services.whatsapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.velvet.rest.services.whatsapp.beans.WhapiSendTextRequest;
import com.velvet.rest.services.whatsapp.beans.WhapiSendTextResponse;
import com.velvet.rest.services.whatsapp.clients.WhatsappClientService;
import com.velvet.rest.services.whatsapp.entity.WhapiChatEntity;
import com.velvet.rest.services.whatsapp.entity.WhapiMessageEntity;
import com.velvet.rest.services.whatsapp.repository.WhapiChatRepository;
import com.velvet.rest.services.whatsapp.repository.WhapiMessageRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WhapiMessageService {

	@Autowired
	private WhapiChatRepository chatRepository;

	@Autowired
	private WhapiMessageRepository messageRepository;

	@Autowired
	private WhatsappClientService clientService;

	public void sendTextMessage(WhapiSendTextRequest request) {
		log.info("WhapiMessageService:: Sending message");
		WhapiSendTextResponse response = clientService.sendTextMessage(request);
		if (response != null && Boolean.TRUE.equals(response.getSent()) && response.getMessage() != null) {
			saveSentMessage(response.getMessage());
		}
	}

	public void saveSentMessage(WhapiSendTextResponse.MessageWrapper msg) {
		log.info("Saving Sent message: {}", msg.getId());
		try {
			String chatId = msg.getChatId();
			WhapiChatEntity chat = chatRepository.findByWaChatId(chatId).orElseGet(() -> {
				WhapiChatEntity newChat = WhapiChatEntity.builder()
						.waChatId(chatId)
						.chatType(chatId.endsWith("@g.us") ? "GROUP" : "PRIVATE")
						.build();
				return chatRepository.save(newChat);
			});

			WhapiMessageEntity message = WhapiMessageEntity.builder()
					.waMessageId(msg.getId())
					.chat(chat)
					.senderWaId(msg.getFrom())
					.messageType(msg.getType())
					.textBody(msg.getText() != null ? msg.getText().getBody() : null)
					.fromMe(msg.getFromMe())
					.status(msg.getStatus())
					.messageTimestamp(msg.getTimestamp())
					.build();

			messageRepository.save(message);
			log.info("Sent message saved");
		} catch (Exception e) {
			log.error("Failed to save sent message: {}", msg.getId(), e);
		}
	}
}
