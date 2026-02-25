package com.velvet.rest.services.whatsapp.clients;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.velvet.rest.services.whatsapp.beans.AppUserResponse;
import com.velvet.rest.services.whatsapp.beans.CartRequest;
import com.velvet.rest.services.whatsapp.beans.CreateAppUserRequest;
import com.velvet.rest.services.whatsapp.beans.OrgMenuItemResponse;
import com.velvet.rest.services.whatsapp.beans.OrgResponse;
import com.velvet.rest.services.whatsapp.beans.WhapiSendTextRequest;
import com.velvet.rest.services.whatsapp.beans.WhapiSendTextResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * Client service wrapper for external service calls.
 *
 * @author mohit
 */
@Service
@Slf4j
public class WhatsappClientService {

	@Autowired
	private UserServiceClient userServiceClient;

	@Autowired
	private OrgServiceClient orgServiceClient;

	@Autowired
	private OrderServiceClient orderServiceClient;

	@Autowired
	private WhapiClient whapiClient;

	public void createAppUser(CreateAppUserRequest request) {
		try {
			userServiceClient.createAppUser(request);
		} catch (Exception e) {
			log.error("Failed to ensure user for waUserId: {}", request.getPhone(), e);
		}
	}

	public Long getOrCreateCustomerId(String phone, UUID orgId) {
		try {
			ResponseEntity<AppUserResponse> response = userServiceClient.createAppUser(new CreateAppUserRequest(phone, orgId));
			if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
				return response.getBody().getId();
			}
		} catch (Exception e) {
			log.error("Failed to get customerId for phone: {}", phone, e);
		}
		return null;
	}

	public void addToCart(CartRequest request) {
		try {
			orderServiceClient.addToCart(request);
		} catch (Exception e) {
			log.error("Failed to add to cart for customerId: {}, skuCode: {}", request.getCustomerId(), request.getSkuCode(), e);
		}
	}

	public List<OrgMenuItemResponse> getOrgMenuItems(UUID orgId, Date deliveryDate) {
		try {
			String deliveryDateStr = new SimpleDateFormat("yyyy-MM-dd").format(deliveryDate);

			ResponseEntity<List<OrgMenuItemResponse>> response = orgServiceClient.getOrgMenuItems(orgId, deliveryDateStr);
			if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
				return response.getBody();
			}
		} catch (Exception e) {
			log.error("Failed to fetch org menu items for orgId: {}, date: {}", orgId, deliveryDate, e);
		}
		return null;
	}

	public OrgResponse getOrg(UUID orgId) {
		try {
			ResponseEntity<OrgResponse> response = orgServiceClient.getOrgById(orgId);
			if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
				return response.getBody();
			}
		} catch (Exception e) {
			log.error("Failed to fetch org for orgId: {}", orgId, e);
		}
		return null;
	}

	public WhapiSendTextResponse sendTextMessage(WhapiSendTextRequest request) {
		try {
			return whapiClient.sendTextMessage(request);
		} catch (Exception e) {
			log.error("Failed to send text message", e);
			return null;
		}
	}
}
