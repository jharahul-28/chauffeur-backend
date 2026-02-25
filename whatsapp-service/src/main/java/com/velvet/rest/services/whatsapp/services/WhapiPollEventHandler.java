package com.velvet.rest.services.whatsapp.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velvet.rest.services.whatsapp.beans.CartRequest;
import com.velvet.rest.services.whatsapp.clients.WhatsappClientService;
import com.velvet.rest.services.whatsapp.entity.WhapiChatEntity;
import com.velvet.rest.services.whatsapp.entity.WhapiGroupEntity;
import com.velvet.rest.services.whatsapp.entity.WhapiMessageEntity;
import com.velvet.rest.services.whatsapp.entity.WhapiPollEntity;
import com.velvet.rest.services.whatsapp.entity.WhapiPollOptionEntity;
import com.velvet.rest.services.whatsapp.entity.WhapiPollVoteEntity;
import com.velvet.rest.services.whatsapp.repository.WhapiChatRepository;
import com.velvet.rest.services.whatsapp.repository.WhapiGroupRepository;
import com.velvet.rest.services.whatsapp.repository.WhapiMessageRepository;
import com.velvet.rest.services.whatsapp.repository.WhapiPollRepository;
import com.velvet.rest.services.whatsapp.repository.WhapiPollOptionRepository;
import com.velvet.rest.services.whatsapp.repository.WhapiPollVoteRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WhapiPollEventHandler {

	@Autowired
	private WhapiMessageRepository messageRepository;

	@Autowired
	private WhapiChatRepository chatRepository;

	@Autowired
	private WhapiPollRepository pollRepository;

	@Autowired
	private WhapiPollOptionRepository pollOptionRepository;

	@Autowired
	private WhapiPollVoteRepository pollVoteRepository;

	@Autowired
	private WhapiGroupRepository groupRepository;

	@Autowired
	private WhatsappClientService clientService;

	@SuppressWarnings("unchecked")
	@Transactional
	public void handlePoll(Map<String, Object> msg) {

		try {
			// Introducing a delay to allow for transaction completion before processing
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			log.error("Error while waiting to process poll", e);
			Thread.currentThread().interrupt();
		}

		String messageId = (String) msg.get("id");
		String chatId = (String) msg.get("chat_id");

		// Get or create chat
		WhapiChatEntity chat = chatRepository.findByWaChatId(chatId).orElseGet(() -> {
			WhapiChatEntity newChat = WhapiChatEntity.builder()
					.waChatId(chatId)
					.chatType(chatId.endsWith("@g.us") ? "GROUP" : "PRIVATE")
					.name((String) msg.get("chat_name"))
					.build();
			return chatRepository.save(newChat);
		});

		// Save message if not exists
		if (messageRepository.findByWaMessageId(messageId).isEmpty()) {
			WhapiMessageEntity message = WhapiMessageEntity.builder()
					.waMessageId(messageId)
					.chat(chat)
					.senderWaId((String) msg.get("from"))
					.messageType("poll")
					.fromMe((Boolean) msg.get("from_me"))
					.messageTimestamp(msg.get("timestamp") != null ? ((Number) msg.get("timestamp")).longValue() : null)
					.build();
			messageRepository.save(message);
		}

		Map<String, Object> poll = (Map<String, Object>) msg.get("poll");

		// Create poll if not exists
		WhapiPollEntity pollEntity = pollRepository.findByWaPollMessageId(messageId).orElseGet(() -> {
			WhapiPollEntity newPoll = WhapiPollEntity.builder()
					.waPollMessageId(messageId)
					.chat(chat)
					.title(poll.get("title").toString())
					.createdAtTs(msg.get("timestamp") != null ? ((Number) msg.get("timestamp")).longValue() : null)
					.createdByWaId((String) msg.get("from"))
					.totalVotes(0)
					.build();
			return pollRepository.save(newPoll);
		});

		// Create poll options
		List<Map<String, Object>> results = (List<Map<String, Object>>) poll.get("results");
		if (results != null) {
			for (Map<String, Object> r : results) {
				String optionId = (String) r.get("id");
				if (pollOptionRepository.findByPoll_IdAndWaOptionId(pollEntity.getId(), optionId).isEmpty()) {
					WhapiPollOptionEntity option = WhapiPollOptionEntity.builder()
							.poll(pollEntity)
							.waOptionId(optionId)
							.optionName((String) r.get("name"))
							.voteCount(0)
							.build();
					pollOptionRepository.save(option);
				}
			}
		}

		log.info("Poll created: {}", messageId);
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public void handleAction(Map<String, Object> msg) {

		Map<String, Object> action = (Map<String, Object>) msg.get("action");

		if (!"vote".equals(action.get("type")))
			return;

		String pollMessageId = (String) action.get("target");
		String voter = (String) msg.get("from");

		Long pollId = pollRepository.findIdByWaMessageId(pollMessageId);
		if (pollId == null) {
			log.warn("Poll not found for message: {}", pollMessageId);
			return;
		}

		Set<String> currentVotes = new HashSet<>((List<String>) action.get("votes"));
		Long votedAtTs = msg.get("timestamp") != null ? ((Number) msg.get("timestamp")).longValue() : null;

		// Index existing votes by option ID for O(1) lookup
		Map<String, WhapiPollVoteEntity> existingVoteMap = pollVoteRepository.findByPollIdAndVoterWaId(pollId, voter)
				.stream()
				.collect(Collectors.toMap(WhapiPollVoteEntity::getWaOptionId, v -> v));

		int totalVoteDelta = 0;
		List<String> newlyVotedOptionIds = new ArrayList<>();
		List<String> removedOptionIds = new ArrayList<>();

		// Inactivate votes that are no longer in the current votes list
		for (WhapiPollVoteEntity existingVote : existingVoteMap.values()) {
			if (existingVote.getActive() == 1 && !currentVotes.contains(existingVote.getWaOptionId())) {
				existingVote.setActive(0);
				pollVoteRepository.save(existingVote);
				pollOptionRepository.decrementVote(pollId, existingVote.getWaOptionId());
				totalVoteDelta--;
				removedOptionIds.add(existingVote.getWaOptionId());
			}
		}

		// Add or reactivate votes
		for (String optionWaId : currentVotes) {
			try {
				WhapiPollVoteEntity existingVote = existingVoteMap.get(optionWaId);

				if (existingVote == null) {
				WhapiPollVoteEntity vote = WhapiPollVoteEntity.builder()
						.poll(WhapiPollEntity.builder().id(pollId).build())
						.waOptionId(optionWaId)
						.voterWaId(voter)
						.votedAtTs(votedAtTs)
						.build();
					pollVoteRepository.save(vote);
					pollOptionRepository.incrementVote(pollId, optionWaId);
					totalVoteDelta++;
					newlyVotedOptionIds.add(optionWaId);

				} else if (existingVote.getActive() == 0) {
					existingVote.setActive(1);
					existingVote.setVotedAtTs(votedAtTs);
					pollVoteRepository.save(existingVote);
					pollOptionRepository.incrementVote(pollId, optionWaId);
					totalVoteDelta++;
					newlyVotedOptionIds.add(optionWaId);
				}
			} catch (Exception e) {
				log.error("Failed to save PollVoteEntity {}", e.getMessage());
			}
		}

		// Single DB call for total votes instead of N individual increment/decrements
		if (totalVoteDelta != 0) {
			pollRepository.updateTotalVotesByDelta(pollId, totalVoteDelta);
		}

		log.info("Vote registered for poll {}", pollMessageId);

		// Add newly voted items to cart / remove unvoted items from cart
		if (!newlyVotedOptionIds.isEmpty() || !removedOptionIds.isEmpty()) {
			updateCart(pollId, voter, (String) msg.get("chat_id"), newlyVotedOptionIds, removedOptionIds);
		}
	}

	private void updateCart(Long pollId, String voterWaId, String chatId, List<String> addedOptionIds, List<String> removedOptionIds) {
		try {
			// Get orgId from group
			Optional<WhapiGroupEntity> groupOpt = groupRepository.findByWaGroupId(chatId);
			if (groupOpt.isEmpty()) {
				log.warn("Group not found for chatId: {}, skipping cart", chatId);
				return;
			}
			UUID orgId = groupOpt.get().getOrgId();

			// Get customerId from voter phone
			String phone = voterWaId.contains("@") ? voterWaId.substring(0, voterWaId.indexOf("@")) : voterWaId;
			Long customerId = clientService.getOrCreateCustomerId(phone, orgId);
			if (customerId == null) {
				log.warn("Could not resolve customerId for phone: {}, skipping cart", phone);
				return;
			}

			// Load poll for deliveryDate and mealType
			WhapiPollEntity poll = pollRepository.findById(pollId).orElse(null);
			if (poll == null) {
				log.warn("Poll not found for id: {}, skipping cart", pollId);
				return;
			}

			// Add each voted option to cart
			for (String optionWaId : addedOptionIds) {
				try {
					addOrRemoveCartItem(pollId, customerId, poll, optionWaId, 1.0);
				} catch (Exception e) {
					log.error("Failed to update addedOptionIds cart for poll {}: {}", pollId, e.getMessage(), e);
				}
			}

			// Remove each unvoted option from cart
			for (String optionWaId : removedOptionIds) {
				try {
					addOrRemoveCartItem(pollId, customerId, poll, optionWaId, 0.0);
				} catch (Exception e) {
					log.error("Failed to update removedOptionIds cart for poll {}: {}", pollId, e.getMessage(), e);
				}
			}
		} catch (Exception e) {
			log.error("Failed to update cart for poll {}: {}", pollId, e.getMessage(), e);
		}
	}

	private void addOrRemoveCartItem(Long pollId, Long customerId, WhapiPollEntity poll, String optionWaId, Double quantity) {
		pollOptionRepository.findByPoll_IdAndWaOptionId(pollId, optionWaId).ifPresent(option -> {
			if (option.getSkuCode() == null) {
				log.warn("No skuCode mapped for option: {}, skipping cart", option.getOptionName());
				return;
			}
			CartRequest cartRequest = CartRequest.builder()
					.customerId(customerId)
					.skuCode(option.getSkuCode())
					.quantity(quantity)
					.channel("WHATSAPP")
					.deliveryDate(poll.getDeliveryDate())
					.orderType(poll.getMealType())
					.build();
			clientService.addToCart(cartRequest);
			log.info("Cart updated: customerId={}, skuCode={}, quantity={}", customerId, option.getSkuCode(), quantity);
		});
	}
}
