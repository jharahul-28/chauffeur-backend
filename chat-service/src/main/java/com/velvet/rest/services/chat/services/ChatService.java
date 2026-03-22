package com.velvet.rest.services.chat.services;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.velvet.rest.services.chat.beans.ChatRequest;
import com.velvet.rest.services.chat.beans.ChatResponse;
import com.velvet.rest.services.chat.beans.OpenAIResponsesResponse;
import com.velvet.rest.services.chat.clients.ChatClientService;
import com.velvet.rest.services.chat.entity.ChatMessageEntity;
import com.velvet.rest.services.chat.entity.ChatSessionEntity;
import com.velvet.rest.services.chat.repository.ChatMessageRepository;
import com.velvet.rest.services.chat.repository.ChatSessionRepository;
import com.velvet.rest.services.chat.utils.PromptBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ChatService {

	private static final long TWENTY_FOUR_HOURS_MS = 24L * 60 * 60 * 1000;

	@Autowired
	private ChatMessageRepository messageRepository;

	@Autowired
	private ChatSessionRepository sessionRepository;

	@Autowired
	private ChatClientService clientService;

	@Autowired
	private OpenAIResponsesService openAIResponsesService;

	@Autowired
	private IntentService intentService;

	public ChatResponse chat(ChatRequest request) {
		String phone = request.getPhone();
		String userMessage = request.getMessage();

		// Resolve or create a session for this phone
		ChatSessionEntity session = resolveSession(phone, request.getSessionId());
		Long sessionId = session.getId();

		// Fetch recent message history for context
		List<ChatMessageEntity> history = messageRepository.findTop20BySessionIdOrderByCreatedOnDesc(sessionId);

		// Build instructions — append prior reasoning summary so the model has continuity
		String instructions = PromptBuilder.getInstructions();
		if (session.getReasoningSummary() != null && !session.getReasoningSummary().isBlank()) {
			instructions = instructions + "\n\nPrevious reasoning context:\n" + session.getReasoningSummary();
		}

		List<Map<String, String>> prompt = PromptBuilder.buildPrompt(history, userMessage);
		// Classify user intent from the message using in-code keyword matching
		String intent = intentService.classify(userMessage).name();

		OpenAIResponsesResponse aiResponse = clientService.generateResponse(phone, instructions, prompt);

		String aiReply = openAIResponsesService.getReply(aiResponse);

		// Persist the latest reasoning summary back onto the session
		String newSummary = openAIResponsesService.getReasoningSummary(aiResponse);
		if (newSummary != null) {
			session.setReasoningSummary(newSummary);
			sessionRepository.save(session);
		}

		// Persist user message (with intent) and AI reply
		ChatMessageEntity userMsg = new ChatMessageEntity();
		userMsg.setSessionId(sessionId);
		userMsg.setRole("user");
		userMsg.setContent(userMessage);
		userMsg.setIntent(intent);

		ChatMessageEntity assistantMsg = new ChatMessageEntity();
		assistantMsg.setSessionId(sessionId);
		assistantMsg.setRole("assistant");
		assistantMsg.setContent(aiReply);

		messageRepository.save(userMsg);
		messageRepository.save(assistantMsg);

		return ChatResponse.builder()
				.message(aiReply)
				.sessionId(String.valueOf(sessionId))
				.intent(intent)
				.build();
	}

	/**
	 * Returns the session entity to use for this request.
	 * If the frontend sent a sessionId, load it directly.
	 * Otherwise look for an active session created in the last 24 hours for this phone.
	 * If none found, create a new session.
	 */
	private ChatSessionEntity resolveSession(String phone, Long requestedSessionId) {
		if (requestedSessionId != null) {
			log.debug("Using client-provided sessionId={} for phone={}", requestedSessionId, phone);
			return sessionRepository.findById(requestedSessionId)
					.orElseThrow(() -> new IllegalArgumentException("Session not found: " + requestedSessionId));
		}

		Date since = new Date(System.currentTimeMillis() - TWENTY_FOUR_HOURS_MS);
		List<ChatSessionEntity> recentSessions = sessionRepository.findRecentByPhone(phone, since);

		if (!recentSessions.isEmpty()) {
			ChatSessionEntity session = recentSessions.get(0);
			log.info("Found existing session id={} for phone={}", session.getId(), phone);
			return session;
		}

		// No recent session — create a new one
		ChatSessionEntity newSession = new ChatSessionEntity();
		newSession.setPhone(phone);
		newSession.setStage("active");
		ChatSessionEntity saved = sessionRepository.save(newSession);
		log.info("Created new session id={} for phone={}", saved.getId(), phone);
		return saved;
	}
}
