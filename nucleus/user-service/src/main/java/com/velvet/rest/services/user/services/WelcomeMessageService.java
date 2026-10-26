package com.velvet.rest.services.user.services;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.velvet.rest.common.utils.CollectionUtils;
import com.velvet.rest.common.utils.ParamsUtils;
import com.velvet.rest.services.user.beans.OrgBasicResponse;
import com.velvet.rest.services.user.beans.WalletResponse;
import com.velvet.rest.services.user.beans.WhapiSendTextRequest;
import com.velvet.rest.services.user.clients.UserClientService;
import com.velvet.rest.services.user.entity.CustomerEntity;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WelcomeMessageService {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private UserClientService clientService;

	public void sendPendingWelcomeMessages() {
		List<CustomerEntity> users = customerService.findPendingWelcomeUsers();
		if (CollectionUtils.isEmpty(users)) {
			return;
		}

		log.info("Found {} users pending welcome message", users.size());

		List<Long> customerIds = users.stream().map(CustomerEntity::getId).distinct().collect(Collectors.toList());

		Map<Long, WalletResponse> walletMap = clientService.getUserWallets(customerIds).stream()
				.collect(Collectors.toMap(WalletResponse::getUserId, w -> w, (a, b) -> a));

		String welcomeMessage = ParamsUtils.getParam("NEW_CUSTOMER_WELCOME_MESSAGE");
		Set<UUID> noCommsOrgs = Arrays.stream(ParamsUtils.getParam("ORG_IDS_NO_COMMUNICATION", "").split(","))
				.map(String::trim).filter(s -> !s.isEmpty()).map(UUID::fromString).collect(Collectors.toSet());

		for (CustomerEntity user : users) {
			try {
				if(user.getOrgId()!=null && !noCommsOrgs.contains(user.getOrgId())) {
					sendWelcomeMessage(user, walletMap, welcomeMessage);
				}
				customerService.markWelcomeMessageSent(user.getId());
			} catch (Exception e) {
				log.error("Failed to send welcome message to user {}: {}", user.getId(), e.getMessage(), e);
			}
		}
	}

	private void sendWelcomeMessage(CustomerEntity user, Map<Long, WalletResponse> walletMap, String welcomeMessage) {
		String phone = user.getPhone();
		if (phone == null) {
			log.warn("No phone number for user {}", user.getId());
			return;
		}

		Double walletBalance = 0d;
		WalletResponse wallet = walletMap.get(user.getId());
		if (wallet != null) {
			walletBalance = wallet.getAmount();
		}

		if (walletBalance.compareTo(0d) > 0) {
			log.info("Wallet already recharged for the user {}, phone: {}", user.getId(), phone);
			return;
		}

		String orgName = null;
		String whatsappGroupLink = null;
		if (user.getOrgId() != null) {
			OrgBasicResponse org = clientService.getOrg(user.getOrgId());
			if (org != null) {
				orgName = org.getName();
				whatsappGroupLink = org.getWhatsappGroupLink();
			}
		}

		String userName = user.getName() != null ? user.getName() : "Customer";
		String message = String.format(welcomeMessage, userName, orgName, whatsappGroupLink);

		WhapiSendTextRequest request = WhapiSendTextRequest.builder().to(phone).body(message).build();

		clientService.sendWhatsappText(request);
		log.info("Welcome message sent to user {}, phone: {}", user.getId(), phone);
	}

}
