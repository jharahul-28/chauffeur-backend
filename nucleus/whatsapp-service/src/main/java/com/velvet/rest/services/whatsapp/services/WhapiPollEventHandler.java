package com.velvet.rest.services.whatsapp.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velvet.rest.services.whatsapp.beans.webhook.WhapiActionPayload;
import com.velvet.rest.services.whatsapp.beans.webhook.WhapiMessagePayload;
import com.velvet.rest.services.whatsapp.beans.webhook.WhapiPollPayload;
import com.velvet.rest.services.whatsapp.beans.webhook.WhapiPollResultPayload;
import com.velvet.rest.services.whatsapp.entity.WhapiChatEntity;
import com.velvet.rest.services.whatsapp.entity.WhapiMessageEntity;
import com.velvet.rest.services.whatsapp.entity.WhapiPollEntity;
import com.velvet.rest.services.whatsapp.entity.WhapiPollOptionEntity;
import com.velvet.rest.services.whatsapp.entity.WhapiPollVoteEntity;
import com.velvet.rest.services.whatsapp.repository.WhapiChatRepository;
import com.velvet.rest.services.whatsapp.repository.WhapiMessageRepository;
import com.velvet.rest.services.whatsapp.repository.WhapiPollOptionRepository;
import com.velvet.rest.services.whatsapp.repository.WhapiPollRepository;
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

	@Transactional
	public void handlePoll(WhapiMessagePayload msg) {

		try {
			// Introducing a delay to allow for transaction completion before processing
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			log.error("Error while waiting to process poll", e);
			Thread.currentThread().interrupt();
		}

		String messageId = msg.getId();
		String chatId = msg.getChatId();

		// Get or create chat
		WhapiChatEntity chat = chatRepository.findByWaChatId(chatId).orElseGet(() -> {
			WhapiChatEntity newChat = WhapiChatEntity.builder()
					.waChatId(chatId)
					.chatType(chatId.endsWith("@g.us") ? "GROUP" : "PRIVATE")
					.name(msg.getChatName())
					.build();
			return chatRepository.save(newChat);
		});

		// Save message if not exists
		if (messageRepository.findByWaMessageId(messageId).isEmpty()) {
			WhapiMessageEntity message = WhapiMessageEntity.builder()
					.waMessageId(messageId)
					.chat(chat)
					.senderWaId(msg.getFrom())
					.messageType("poll")
					.fromMe(msg.getFromMe())
					.messageTimestamp(msg.getTimestamp())
					.build();
			messageRepository.save(message);
		}

		WhapiPollPayload poll = msg.getPoll();

		// Create poll if not exists
		WhapiPollEntity pollEntity = pollRepository.findByWaPollMessageId(messageId).orElseGet(() -> {
			WhapiPollEntity newPoll = WhapiPollEntity.builder()
					.waPollMessageId(messageId)
					.chat(chat)
					.title(poll.getTitle())
					.createdAtTs(msg.getTimestamp())
					.createdByWaId(msg.getFrom())
					.totalVotes(0)
					.build();
			return pollRepository.save(newPoll);
		});

		// Create poll options
		List<WhapiPollResultPayload> results = poll.getResults();
		if (results != null) {
			for (WhapiPollResultPayload r : results) {
				String optionId = r.getId();
				if (pollOptionRepository.findByPoll_IdAndWaOptionId(pollEntity.getId(), optionId).isEmpty()) {
					WhapiPollOptionEntity option = WhapiPollOptionEntity.builder()
							.poll(pollEntity)
							.waOptionId(optionId)
							.optionName(r.getName())
							.voteCount(0)
							.build();
					pollOptionRepository.save(option);
				}
			}
		}

		log.info("Poll created: {}", messageId);
	}

	@Transactional
	public void handleAction(WhapiMessagePayload msg) {

		WhapiActionPayload action = msg.getAction();

		if (!"vote".equals(action.getType()))
			return;

		String pollMessageId = action.getTarget();
		String voter = msg.getFrom();

		Long pollId = pollRepository.findIdByWaMessageId(pollMessageId);
		if (pollId == null) {
			log.warn("Poll not found for message: {}", pollMessageId);
			return;
		}

		Set<String> currentVotes = new HashSet<>(action.getVotes());
		Long votedAtTs = msg.getTimestamp();

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
	}

}
