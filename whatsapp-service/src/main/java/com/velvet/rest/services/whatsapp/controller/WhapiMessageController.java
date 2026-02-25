package com.velvet.rest.services.whatsapp.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.velvet.rest.common.websupport.base.BaseController;
import com.velvet.rest.services.common.mapper.BaseMapper;
import com.velvet.rest.services.whatsapp.beans.WhapiSendTextRequest;
import com.velvet.rest.services.whatsapp.services.WhapiMessageService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class WhapiMessageController implements BaseController {

	@Autowired
	private BaseMapper<?, ?> mapper;

	@Autowired
	private WhapiMessageService messageService;

	@PostMapping("/whatsapp/messages/text")
	public ResponseEntity<Void> sendTextMessage(@Valid @RequestBody WhapiSendTextRequest request) {
		messageService.sendTextMessage(request);
		return ResponseEntity.ok().build();
	}

	@Override
	public BaseMapper<?, ?> getMapper() {
		return mapper;
	}
}
