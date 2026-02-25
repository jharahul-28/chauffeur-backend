package com.velvet.rest.services.user.clients;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.velvet.rest.common.openfeign.FeignInternalConfiguration;
import com.velvet.rest.services.user.beans.OrgAddressResponse;
import com.velvet.rest.services.user.beans.OrgBasicResponse;

/**
 * Feign client for Organisation Service.
 *
 * @author mohit
 */
@FeignClient(value = "OrgServiceClient", url = "${client.organisation.url}", configuration = { FeignInternalConfiguration.class })
public interface OrgServiceClient {

	@GetMapping(value = "/organisations/addresses/{id}")
	ResponseEntity<OrgAddressResponse> getOrgAddress(@PathVariable Long id);

	@GetMapping(value = "/organisations/domain/{domain}")
	ResponseEntity<OrgBasicResponse> getOrgByDomain(@PathVariable String domain);

	@GetMapping(value = "/organisations/{id}/internal")
	ResponseEntity<OrgBasicResponse> getOrgById(@PathVariable UUID id);

}
