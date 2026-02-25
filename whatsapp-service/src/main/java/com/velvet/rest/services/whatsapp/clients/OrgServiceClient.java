package com.velvet.rest.services.whatsapp.clients;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.velvet.rest.common.openfeign.FeignInternalConfiguration;
import com.velvet.rest.services.whatsapp.beans.OrgMenuItemResponse;
import com.velvet.rest.services.whatsapp.beans.OrgResponse;

@FeignClient(value = "WhapiOrgServiceClient", url = "${client.organisation.url}", configuration = { FeignInternalConfiguration.class })
public interface OrgServiceClient {

	@GetMapping(value = "/organisations/{orgId}/menu-items")
	ResponseEntity<List<OrgMenuItemResponse>> getOrgMenuItems(@PathVariable UUID orgId, @RequestParam String deliveryDate);

	@GetMapping(value = "/organisations/{orgId}/internal")
	ResponseEntity<OrgResponse> getOrgById(@PathVariable UUID orgId);
}
