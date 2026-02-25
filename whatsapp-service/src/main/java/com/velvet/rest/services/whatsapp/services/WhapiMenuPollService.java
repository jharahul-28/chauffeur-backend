package com.velvet.rest.services.whatsapp.services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.velvet.rest.common.utils.CollectionUtils;
import com.velvet.rest.common.utils.DateUtils;
import com.velvet.rest.common.utils.DateUtilsV2;
import com.velvet.rest.common.utils.DeliveryDateUtils;
import com.velvet.rest.services.whatsapp.beans.OrgMenuItemResponse;
import com.velvet.rest.services.whatsapp.beans.OrgResponse;
import com.velvet.rest.services.whatsapp.beans.WhapiSendPollRequest;
import com.velvet.rest.services.whatsapp.beans.WhapiSendPollResponse;
import com.velvet.rest.services.whatsapp.beans.WhapiSendTextRequest;
import com.velvet.rest.services.whatsapp.clients.WhapiClient;
import com.velvet.rest.services.whatsapp.clients.WhatsappClientService;
import com.velvet.rest.services.whatsapp.entity.WhapiChatEntity;
import com.velvet.rest.services.whatsapp.entity.WhapiGroupEntity;
import com.velvet.rest.services.whatsapp.entity.WhapiPollEntity;
import com.velvet.rest.services.whatsapp.entity.WhapiPollOptionEntity;
import com.velvet.rest.services.whatsapp.repository.WhapiChatRepository;
import com.velvet.rest.services.whatsapp.repository.WhapiGroupRepository;
import com.velvet.rest.services.whatsapp.repository.WhapiPollOptionRepository;
import com.velvet.rest.services.whatsapp.repository.WhapiPollRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WhapiMenuPollService {

	private static final int MAX_POLL_OPTIONS = 12;
	private static final long GROUP_DELAY_MS = 7000;
	private static final long CATEGORY_DELAY_MS = 2000;
	private static final int MAX_POLL_RETRIES = 2;
	private static final long POLL_RETRY_DELAY_MS = 3000L;

	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	@Autowired
	private WhatsappClientService clientService;

	@Autowired
	private WhapiClient whapiClient;

	@Autowired
	private WhapiGroupRepository groupRepository;

	@Autowired
	private WhapiChatRepository chatRepository;

	@Autowired
	private WhapiPollRepository pollRepository;

	@Autowired
	private WhapiPollOptionRepository pollOptionRepository;

	@Autowired
	private WhatsappClientService whapiMessageService;

	public void sendMenuPoll() {
		sendMenuPoll(null, null);
	}

	/**
	 * Send menu polls to all WhatsApp groups that have an org mapped. For each
	 * group, fetches org details to determine working days and org name, then sends
	 * one poll per category for the next working day's menu.
	 */
	@Async
	public void sendMenuPoll(String reqDeliveryDate, Long groupId) {
		Date requestedDeliveryDate = null;
		if (reqDeliveryDate != null) {
			requestedDeliveryDate = new Date(DateUtilsV2.getDate(DateUtils.SHORT_DATE_FMT, reqDeliveryDate).getTime());
		}

		List<WhapiGroupEntity> groups = groupRepository.findByOrgIdNotNull();
		if (groups.isEmpty()) {
			log.warn("No WhatsApp groups with org mapping found");
			return;
		}

		CompletableFuture<Void> chain = CompletableFuture.completedFuture(null);

		for (WhapiGroupEntity group : groups) {
			if (groupId != null && !group.getId().equals(groupId)) {
				continue;
			}
			final Date finalDeliveryDate = requestedDeliveryDate;
			chain = chain.thenCompose(v -> {
				try {
					OrgResponse org = clientService.getOrg(group.getOrgId());
					List<DayOfWeek> workingDays = org != null ? org.getWorkingDays() : null;
					Date deliveryDate = finalDeliveryDate != null ? finalDeliveryDate : DeliveryDateUtils.getNextDayDeliveryDate(workingDays);
					return sendMenuPollForGroup(group, org, deliveryDate);
				} catch (Exception e) {
					log.error("Failed to send menu poll for group {} (org {}): {}", group.getWaGroupId(), group.getOrgId(), e.getMessage(), e);
					return CompletableFuture.completedFuture(null);
				}
			}).thenCompose(v -> asyncDelay(GROUP_DELAY_MS));
		}

		chain.exceptionally(e -> {
			log.error("Error in menu poll chain: {}", e.getMessage(), e);
			return null;
		});
	}

	private CompletableFuture<Void> sendMenuPollForGroup(WhapiGroupEntity group, OrgResponse org, Date deliveryDate) {
		UUID orgId = group.getOrgId();

		List<OrgMenuItemResponse> menuItems = clientService.getOrgMenuItems(orgId, deliveryDate);
		if (CollectionUtils.isEmpty(menuItems)) {
			log.warn("No menu items found for org {} on {}", orgId, deliveryDate);
			return CompletableFuture.completedFuture(null);
		}

		// Filter out sample items
		menuItems = menuItems.stream().filter(item -> !Boolean.TRUE.equals(item.getIsSample())).collect(Collectors.toList());
		if (CollectionUtils.isEmpty(menuItems)) {
			log.warn("No non-sample menu items found for org {} on {}", orgId, deliveryDate);
			return CompletableFuture.completedFuture(null);
		}

		String mealType = menuItems.get(0).getMenuType();

		// Send greeting message before polls
		String orgName = org != null && org.getName() != null ? org.getName() : "team";
		sendGreetingMessage(group, orgName, deliveryDate);

		// Group items by category
		Map<String, List<OrgMenuItemResponse>> itemsByCategory = menuItems.stream().collect(
				Collectors.groupingBy(item -> item.getCategoryName() != null ? item.getCategoryName() : "Other", LinkedHashMap::new, Collectors.toList()));

		CompletableFuture<Void> chain = CompletableFuture.completedFuture(null);

		for (Map.Entry<String, List<OrgMenuItemResponse>> entry : itemsByCategory.entrySet()) {
			String categoryName = entry.getKey();
			List<OrgMenuItemResponse> categoryItems = entry.getValue().stream().limit(MAX_POLL_OPTIONS).collect(Collectors.toList());

			List<String> optionStrings = categoryItems.stream().map(item -> item.getProductName() + " - \u20B9" + item.getSalePrice().intValue())
					.collect(Collectors.toList());

			Map<String, String> optionToSkuCode = new LinkedHashMap<>();
			for (int i = 0; i < categoryItems.size(); i++) {
				optionToSkuCode.put(optionStrings.get(i), categoryItems.get(i).getSkuCode());
			}

			chain = chain.thenCompose(v -> {
				try {
					sendPollToGroup(group, categoryName, optionStrings, optionToSkuCode, mealType, deliveryDate);
				} catch (Exception e) {
					log.error("Failed to send poll '{}' to group {}: {}", categoryName, group.getWaGroupId(), e.getMessage(), e);
				}
				return asyncDelay(CATEGORY_DELAY_MS);
			});
		}

		return chain;
	}

	private void sendGreetingMessage(WhapiGroupEntity group, String orgName, Date deliveryDate) {
		LocalDate deliveryLocalDate = deliveryDate.toLocalDate();
		LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));
		String dayLabel;
		if (deliveryLocalDate.equals(today.plusDays(1))) {
			dayLabel = "tomorrow";
		} else {
			dayLabel = deliveryLocalDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
		}
		String message = String.format("Hey team %s!\n\nHope you enjoyed your meal, here's a new poll for your lunches for %s. Vote away!", orgName, dayLabel);
		WhapiSendTextRequest request = WhapiSendTextRequest.builder()
				.to(group.getWaGroupId())
				.body(message)
				.build();
		whapiMessageService.sendTextMessage(request);
	}

	private WhapiSendPollResponse sendPollWithRetry(WhapiSendPollRequest request) {
		Exception lastException = null;
		for (int attempt = 1; attempt <= MAX_POLL_RETRIES; attempt++) {
			try {
				return whapiClient.sendPoll(request);
			} catch (Exception e) {
				lastException = e;
				log.warn("sendPollWithRetry :: attempt {}/{} failed for group {}: {}", attempt, MAX_POLL_RETRIES, request.getTo(), e.getMessage());
				if (attempt < MAX_POLL_RETRIES) {
					try {
						Thread.sleep(POLL_RETRY_DELAY_MS);
					} catch (InterruptedException ie) {
						log.warn("sendPollWithRetry :: interrupted during retry sleep for group {}", request.getTo());
						Thread.currentThread().interrupt();
						break;
					}
				}
			}
		}
		throw new RuntimeException("All " + MAX_POLL_RETRIES + " poll send attempts failed for group " + request.getTo(), lastException);
	}

	private void sendPollToGroup(WhapiGroupEntity group, String pollTitle, List<String> optionStrings, Map<String, String> optionToSkuCode, String mealType,
			Date deliveryDate) {

		WhapiSendPollRequest request = WhapiSendPollRequest.builder().to(group.getWaGroupId()).title(pollTitle).options(optionStrings).build();

		WhapiSendPollResponse apiResponse = sendPollWithRetry(request);

		if (apiResponse == null || !Boolean.TRUE.equals(apiResponse.getSent())) {
			log.error("Whapi API returned failure for group {}", group.getWaGroupId());
			return;
		}

		String messageId = apiResponse.getMessage().getId();

		// Ensure chat entity exists
		WhapiChatEntity chat = group.getChat();
		if (chat == null) {
			chat = chatRepository.findByWaChatId(group.getWaGroupId()).orElseGet(() -> {
				WhapiChatEntity newChat = WhapiChatEntity.builder().waChatId(group.getWaGroupId()).chatType("GROUP").name(group.getName()).build();
				return chatRepository.save(newChat);
			});
		}

		// Create poll entity
		WhapiPollEntity pollEntity = WhapiPollEntity.builder()
				.waPollMessageId(messageId)
				.chat(chat)
				.title(apiResponse.getMessage().getPoll().getTitle())
				.createdAtTs(apiResponse.getMessage().getTimestamp() != null ? apiResponse.getMessage().getTimestamp() : System.currentTimeMillis() / 1000)
				.createdByWaId(apiResponse.getMessage().getFrom())
				.totalVotes(0)
				.mealType(mealType)
				.deliveryDate(deliveryDate)
				.build();
		pollEntity = pollRepository.save(pollEntity);

		// Create poll option entities with skuCode mapping
		List<WhapiSendPollResponse.PollOptionResult> apiOptions = apiResponse.getMessage().getPoll().getResults();

		if (apiOptions != null) {
			for (WhapiSendPollResponse.PollOptionResult apiOption : apiOptions) {
				String skuCode = optionToSkuCode.get(apiOption.getName());
				WhapiPollOptionEntity optionEntity = WhapiPollOptionEntity.builder()
						.poll(pollEntity)
						.waOptionId(apiOption.getId())
						.optionName(apiOption.getName())
						.skuCode(skuCode)
						.voteCount(0)
						.build();
				pollOptionRepository.save(optionEntity);
			}
		}
		log.info("Poll sent to group {} with messageId {}", group.getWaGroupId(), messageId);
	}

	private CompletableFuture<Void> asyncDelay(long millis) {
		CompletableFuture<Void> future = new CompletableFuture<>();
		scheduler.schedule(() -> future.complete(null), millis, TimeUnit.MILLISECONDS);
		return future;
	}
}
