package com.velvet.rest.services.whatsapp.services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.velvet.rest.services.whatsapp.beans.WhapiGroupResponse;
import com.velvet.rest.services.whatsapp.clients.WhapiClient;
import com.velvet.rest.services.whatsapp.clients.WhatsappClientService;
import com.velvet.rest.services.whatsapp.beans.CreateAppUserRequest;
import com.velvet.rest.services.whatsapp.entity.WhapiChatEntity;
import com.velvet.rest.services.whatsapp.entity.WhapiGroupEntity;
import com.velvet.rest.services.whatsapp.entity.WhapiGroupParticipantEntity;
import com.velvet.rest.services.whatsapp.repository.WhapiChatRepository;
import com.velvet.rest.services.whatsapp.repository.WhapiGroupParticipantRepository;
import com.velvet.rest.services.whatsapp.repository.WhapiGroupRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WhapiGroupSyncService {

	@Autowired
	private WhapiClient whapiClient;

	@Autowired
	private WhapiGroupRepository groupRepository;

	@Autowired
	private WhapiChatRepository chatRepository;

	@Autowired
	private WhapiGroupParticipantRepository participantRepository;

	@Autowired
	private WhatsappClientService clientService;

	public void syncAllGroups() {
		List<WhapiGroupEntity> groups = groupRepository.findAll();
		log.info("Starting group sync for {} groups", groups.size());

		for (WhapiGroupEntity group : groups) {
			try {
				syncGroup(group);
			} catch (Exception e) {
				log.error("Failed to sync group {}: {}", group.getWaGroupId(), e.getMessage(), e);
			}
		}

		log.info("Group sync completed");
	}

	private void syncGroup(WhapiGroupEntity group) {
		WhapiGroupResponse response = whapiClient.getGroup(group.getWaGroupId());
		if (response == null) {
			log.warn("No response from Whapi for group {}", group.getWaGroupId());
			return;
		}

		// Update chat
		WhapiChatEntity chat = chatRepository.findByWaChatId(group.getWaGroupId())
				.orElseGet(() -> WhapiChatEntity.builder().waChatId(group.getWaGroupId()).chatType("GROUP").build());
		chat.setName(response.getName());
		chatRepository.save(chat);

		// Update group
		group.setName(response.getName());
		group.setCreatedAtTs(response.getCreatedAt());
		group.setCreatedByWaId(response.getCreatedBy());
		group.setParticipantCount(response.getParticipantsCount());
		groupRepository.save(group);

		// Sync participants
		if (response.getParticipants() != null) {
			syncParticipants(group, response.getParticipants());
		}

		log.info("Group synced: {} ({})", group.getWaGroupId(), response.getName());
	}

	private void syncParticipants(WhapiGroupEntity group, List<WhapiGroupResponse.Participant> apiParticipants) {
		List<WhapiGroupParticipantEntity> existingParticipants = participantRepository.findByGroupId(group.getId());

		Set<String> apiWaUserIds = apiParticipants.stream().map(WhapiGroupResponse.Participant::getId).collect(Collectors.toSet());

		// Mark removed participants
		for (WhapiGroupParticipantEntity existing : existingParticipants) {
			if (existing.getLeftAtTs() == null && !apiWaUserIds.contains(existing.getWaUserId())) {
				existing.setLeftAtTs(System.currentTimeMillis() / 1000);
				existing.setActive(0);
				participantRepository.save(existing);
			}
		}

		// Add or update participants from API
		for (WhapiGroupResponse.Participant p : apiParticipants) {
			WhapiGroupParticipantEntity existing = participantRepository.findByGroupIdAndWaUserId(group.getId(), p.getId()).orElse(null);
			if (existing == null) {
				WhapiGroupParticipantEntity participant = WhapiGroupParticipantEntity.builder().group(group).waUserId(p.getId())
						.role(p.getRank()).joinedAtTs(System.currentTimeMillis() / 1000).build();
				participantRepository.save(participant);
				createAppUser(p.getId(), group.getOrgId());
			} else {
				existing.setRole(p.getRank());
				if (existing.getLeftAtTs() != null) {
					existing.setLeftAtTs(null);
					existing.setActive(1);
					existing.setJoinedAtTs(System.currentTimeMillis() / 1000);
					createAppUser(p.getId(), group.getOrgId());
				}
				participantRepository.save(existing);
			}
		}
	}

	private void createAppUser(String waUserId, java.util.UUID orgId) {
		try {
			String phone = waUserId.contains("@") ? waUserId.substring(0, waUserId.indexOf("@")) : waUserId;
			clientService.createAppUser(new CreateAppUserRequest(phone, orgId));
		} catch (Exception e) {
			log.error("Failed to ensure user for waUserId: {}", waUserId, e);
		}
	}
}
