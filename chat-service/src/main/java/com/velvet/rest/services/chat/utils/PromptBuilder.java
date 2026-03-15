
package com.velvet.rest.services.chat.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.velvet.rest.common.utils.ParamsUtils;
import com.velvet.rest.services.chat.entity.ChatMessageEntity;

public class PromptBuilder {

	public static List<Map<String, String>> buildPrompt(List<ChatMessageEntity> history, String newMessage) {

		List<Map<String, String>> messages = new ArrayList<>();

		for (ChatMessageEntity msg : history) {
			Map<String, String> m = new HashMap<>();
			m.put("role", msg.getRole());
			m.put("content", msg.getContent());

			messages.add(m);
		}
		Map<String, String> user = new HashMap<>();
		user.put("role", "user");
		user.put("content", newMessage);

		messages.add(user);

		return messages;
	}

	public static String getInstructions() {
		return ParamsUtils.getParam("WHATSAPP_CHATBOT_INSTRUCTIONS");
	}
}