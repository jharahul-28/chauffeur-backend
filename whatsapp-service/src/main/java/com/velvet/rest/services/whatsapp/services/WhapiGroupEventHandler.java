package com.velvet.rest.services.whatsapp.services;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.velvet.rest.services.whatsapp.beans.CreateAppUserRequest;
import com.velvet.rest.services.whatsapp.clients.WhatsappClientService;
import com.velvet.rest.services.whatsapp.entity.WhapiChatEntity;
import com.velvet.rest.services.whatsapp.entity.WhapiGroupEntity;
import com.velvet.rest.services.whatsapp.entity.WhapiGroupParticipantEntity;
import com.velvet.rest.services.whatsapp.repository.WhapiChatRepository;
import com.velvet.rest.services.whatsapp.repository.WhapiGroupParticipantRepository;
import com.velvet.rest.services.whatsapp.repository.WhapiGroupRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WhapiGroupEventHandler {

	@Autowired
	private WhapiChatRepository chatRepository;

	@Autowired
	private WhapiGroupRepository groupRepository;

	@Autowired
	private WhapiGroupParticipantRepository participantRepository;

	@Autowired
	private WhatsappClientService clientService;

	@SuppressWarnings("unchecked")
	public void handle(Map<String, Object> payload) {

		// Handle full group creation/update events
		List<Map<String, Object>> groups = (List<Map<String, Object>>) payload.get("groups");
		if (groups != null && !groups.isEmpty()) {
			handleGroupsPayload(groups);
		}

		// Handle participant update events (add, remove, promote, demote)
		List<Map<String, Object>> groupsParticipants = (List<Map<String, Object>>) payload.get("groups_participants");
		if (groupsParticipants != null && !groupsParticipants.isEmpty()) {
			handleParticipantsUpdate(groupsParticipants);
		}
	}

	@SuppressWarnings("unchecked")
	private void handleGroupsPayload(List<Map<String, Object>> groups) {
		for (Map<String, Object> g : groups) {

			String waGroupId = (String) g.get("id");
			String groupName = (String) g.get("name");

			// Upsert chat
			WhapiChatEntity chat = chatRepository.findByWaChatId(waGroupId)
					.orElseGet(() -> WhapiChatEntity.builder().waChatId(waGroupId).chatType("GROUP").name(groupName).build());
			chat.setName(groupName);
			chat = chatRepository.save(chat);

			// Upsert group
			final WhapiChatEntity savedChat = chat;
			WhapiGroupEntity group = groupRepository.findByWaGroupId(waGroupId)
					.orElseGet(() -> WhapiGroupEntity.builder().chat(savedChat).waGroupId(waGroupId).build());
			group.setName(groupName);
			group.setCreatedAtTs(g.get("created_at") != null ? ((Number) g.get("created_at")).longValue() : null);
			group.setCreatedByWaId((String) g.get("created_by"));
			group.setAdminAddMemberMode((Boolean) g.get("adminAddMemberMode"));
			group.setParticipantCount(g.get("participants_count") != null ? ((Number) g.get("participants_count")).intValue() : null);
			group = groupRepository.save(group);

			// Reset participants (group creation event)
			participantRepository.deleteByGroupId(group.getId());

			List<Map<String, Object>> participants = (List<Map<String, Object>>) g.get("participants");
			if (participants != null) {
				for (Map<String, Object> p : participants) {
					WhapiGroupParticipantEntity participant = WhapiGroupParticipantEntity.builder().group(group).waUserId((String) p.get("id"))
							.role((String) p.get("rank")).build();
					participantRepository.save(participant);
				}
			}

			log.info("Group synced: {}", waGroupId);
		}
	}

	@SuppressWarnings("unchecked")
	private void handleParticipantsUpdate(List<Map<String, Object>> groupsParticipants) {
		for (Map<String, Object> gp : groupsParticipants) {

			String groupId = (String) gp.get("group_id");
			String action = (String) gp.get("action");
			List<String> participants = (List<String>) gp.get("participants");

			if (groupId == null || action == null || participants == null) {
				log.warn("Invalid groups_participants payload: missing required fields");
				continue;
			}

			// Find the group
			WhapiGroupEntity group = groupRepository.findByWaGroupId(groupId).orElse(null);
			if (group == null) {
				log.warn("Group not found for participant update: {}", groupId);
				continue;
			}

			switch (action.toLowerCase()) {
			case "add":
				handleParticipantAdd(group, participants);
				break;
			case "remove":
				handleParticipantRemove(group, participants);
				break;
			case "promote":
				handleParticipantPromote(group, participants);
				break;
			case "demote":
				handleParticipantDemote(group, participants);
				break;
			default:
				log.warn("Unknown participant action: {}", action);
			}

			log.info("Participant {} for group {}: {}", action, groupId, participants);
		}
	}

	private void handleParticipantAdd(WhapiGroupEntity group, List<String> participants) {
		for (String waUserId : participants) {
			// Check if participant already exists
			WhapiGroupParticipantEntity existing = participantRepository.findByGroupIdAndWaUserId(group.getId(), waUserId).orElse(null);
			if (existing == null) {
				WhapiGroupParticipantEntity participant = WhapiGroupParticipantEntity.builder().group(group).waUserId(waUserId).role("member")
						.joinedAtTs(System.currentTimeMillis() / 1000).build();
				participantRepository.save(participant);
			} else if (existing.getLeftAtTs() != null) {
				// Participant was removed and is being re-added
				existing.setLeftAtTs(null);
				existing.setActive(1);
				existing.setJoinedAtTs(System.currentTimeMillis() / 1000);
				participantRepository.save(existing);
			}

			// Ensure user exists in user-service and map orgId
			createAppUser(waUserId, group.getOrgId());
		}
		// Update participant count
		updateParticipantCount(group);
	}

	private void createAppUser(String waUserId, UUID orgId) {
		try {
			String phone = waUserId.contains("@") ? waUserId.substring(0, waUserId.indexOf("@")) : waUserId;
			clientService.createAppUser(new CreateAppUserRequest(phone, orgId));
			log.info("User ensured for phone: {}", phone);
		} catch (Exception e) {
			log.error("Failed to ensure user for waUserId: {}", waUserId, e);
		}
	}

	private void handleParticipantRemove(WhapiGroupEntity group, List<String> participants) {
		for (String waUserId : participants) {
			WhapiGroupParticipantEntity existing = participantRepository.findByGroupIdAndWaUserId(group.getId(), waUserId).orElse(null);
			if (existing != null) {
				existing.setLeftAtTs(System.currentTimeMillis() / 1000);
				existing.setActive(0);
				participantRepository.save(existing);
			}
		}
		// Update participant count
		updateParticipantCount(group);
	}

	private void handleParticipantPromote(WhapiGroupEntity group, List<String> participants) {
		for (String waUserId : participants) {
			WhapiGroupParticipantEntity existing = participantRepository.findByGroupIdAndWaUserId(group.getId(), waUserId).orElse(null);
			if (existing != null) {
				existing.setRole("admin");
				participantRepository.save(existing);
			}
		}
	}

	private void handleParticipantDemote(WhapiGroupEntity group, List<String> participants) {
		for (String waUserId : participants) {
			WhapiGroupParticipantEntity existing = participantRepository.findByGroupIdAndWaUserId(group.getId(), waUserId).orElse(null);
			if (existing != null) {
				existing.setRole("member");
				participantRepository.save(existing);
			}
		}
	}

	private void updateParticipantCount(WhapiGroupEntity group) {
		long activeCount = participantRepository.countByGroupIdAndLeftAtTsIsNull(group.getId());
		group.setParticipantCount((int) activeCount);
		groupRepository.save(group);
	}
}
