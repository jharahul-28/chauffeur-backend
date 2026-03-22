package com.velvet.rest.services.chat.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.velvet.rest.common.websupport.base.BaseController;
import com.velvet.rest.services.chat.beans.ChatRequest;
import com.velvet.rest.services.chat.beans.ChatResponse;
import com.velvet.rest.services.chat.services.ChatService;
import com.velvet.rest.services.common.mapper.BaseMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Api(tags = "Chat Services", description = "AI-powered Lead chatbot")
public class ChatController implements BaseController {

	@Autowired
	private ChatService chatService;

	@Autowired
	private BaseMapper<?, ?> mapper;

	@Override
	public BaseMapper<?, ?> getMapper() {
		return mapper;
	}

	@PostMapping("/chat/message")
	@ApiOperation(value = "Send a chat message", notes = "Send a message to the trip chatbot and get an AI-generated response")
	public ResponseEntity<ChatResponse> sendMessage(@Valid @RequestBody ChatRequest request) {
		log.info("Received chat message: {}", request);
		ChatResponse response = chatService.chat(request);
		return ResponseEntity.ok(response);
	}

}
