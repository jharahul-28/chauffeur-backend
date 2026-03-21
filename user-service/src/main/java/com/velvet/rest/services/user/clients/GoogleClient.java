package com.velvet.rest.services.user.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.velvet.rest.services.user.beans.GoogleUser;

@FeignClient(value = "googleClient", url = "${client.google.url}")
public interface GoogleClient {

	@GetMapping(value = "/tokeninfo")
	ResponseEntity<GoogleUser> oauth(@RequestParam(name = "id_token") String token);

}
