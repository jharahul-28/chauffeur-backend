package com.velvet.rest.services.ai.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.velvet.rest.services.ai.beans.TaskRequest;
import com.velvet.rest.services.ai.beans.TaskResponse;
import com.velvet.rest.services.ai.service.AgentFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

@Slf4j
@RestController
public class AgentController {

	@Autowired
	private AgentFactory agentFactory;

	/**
	 * Execute a task using the appropriate agent
	 */
	@PostMapping("/agents/execute")
	public ResponseEntity<TaskResponse> executeTask(@Valid @RequestBody TaskRequest request) {
		log.info("Received task execution request for type: {}", request.getTaskType());

		TaskResponse response = agentFactory.executeTask(request);

		if ("ERROR".equals(response.getStatus())) {
			return ResponseEntity.badRequest().body(response);
		}

		return ResponseEntity.ok(response);
	}

	/**
	 * Get all available task types
	 */
	@GetMapping("/agents/task-types")
	public ResponseEntity<List<String>> getAvailableTaskTypes() {
		List<String> taskTypes = agentFactory.getAvailableTaskTypes();
		return ResponseEntity.ok(taskTypes);
	}

	/**
	 * Check if a task type is supported
	 */
	@GetMapping("/task-types/{taskType}/supported")
	public ResponseEntity<Map<String, Boolean>> isTaskTypeSupported(@PathVariable String taskType) {
		boolean supported = agentFactory.isTaskTypeSupported(taskType);
		return ResponseEntity.ok(Map.of("supported", supported));
	}

	/**
	 * Parse booking confirmation file
	 */
	@PostMapping("/agents/parse-booking")
	public ResponseEntity<TaskResponse> parseBookingConfirmation(
			@RequestParam("file") MultipartFile file,
			@RequestParam(value = "description", required = false, defaultValue = "Parse booking confirmation") String description) {
		
		if (file.isEmpty()) {
			return ResponseEntity.badRequest().body(
				TaskResponse.builder()
					.taskType("BOOKING_CONFIRMATION")
					.status("ERROR")
					.errorMessage("File is required")
					.build()
			);
		}

		try {
			String fileContent;
			
			// Check if it's a PDF file
			if (file.getOriginalFilename() != null && file.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
				// Extract text from PDF
				try (InputStream inputStream = file.getInputStream()) {
					org.apache.pdfbox.pdmodel.PDDocument document = org.apache.pdfbox.pdmodel.PDDocument.load(inputStream);
					org.apache.pdfbox.text.PDFTextStripper stripper = new org.apache.pdfbox.text.PDFTextStripper();
					fileContent = stripper.getText(document);
					document.close();
					
					// Limit to first 4000 characters to avoid token limits
					if (fileContent.length() > 4000) {
						fileContent = fileContent.substring(0, 4000);
					}
				}
			} else {
				// For non-PDF files, read as text
				fileContent = new String(file.getBytes(), StandardCharsets.UTF_8);
				// Limit content length
				if (fileContent.length() > 4000) {
					fileContent = fileContent.substring(0, 4000);
				}
			}
			
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("fileContent", fileContent);
			parameters.put("fileName", file.getOriginalFilename());
			
			TaskRequest request = new TaskRequest();
			request.setTaskType("BOOKING_CONFIRMATION");
			request.setTaskDescription(description);
			request.setParameters(parameters);
			
			log.info("Processing booking confirmation file: {}", file.getOriginalFilename());
			
			TaskResponse response = agentFactory.executeTask(request);
			
			if ("ERROR".equals(response.getStatus())) {
				return ResponseEntity.badRequest().body(response);
			}
			
			return ResponseEntity.ok(response);
			
		} catch (IOException e) {
			log.error("Error reading file: {}", e.getMessage());
			return ResponseEntity.badRequest().body(
				TaskResponse.builder()
					.taskType("BOOKING_CONFIRMATION")
					.status("ERROR")
					.errorMessage("Error reading file: " + e.getMessage())
					.build()
			);
		}
	}

	/**
	 * Parse booking confirmation text
	 */
	@PostMapping("/agents/parse-booking-text")
	public ResponseEntity<TaskResponse> parseBookingConfirmationText(
			@RequestBody Map<String, String> requestBody) {
		
		String text = requestBody.get("text");
		String description = requestBody.getOrDefault("description", "Parse booking confirmation text");
		
		if (text == null || text.trim().isEmpty()) {
			return ResponseEntity.badRequest().body(
				TaskResponse.builder()
					.taskType("BOOKING_CONFIRMATION")
					.status("ERROR")
					.errorMessage("Text content is required")
					.build()
			);
		}
		
		// Limit to first 4000 characters to avoid token limits
		String fileContent = text;
		if (fileContent.length() > 4000) {
			fileContent = fileContent.substring(0, 4000);
		}
		
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("fileContent", fileContent);
		parameters.put("fileName", "text_input");
		
		TaskRequest request = new TaskRequest();
		request.setTaskType("BOOKING_CONFIRMATION");
		request.setTaskDescription(description);
		request.setParameters(parameters);
		
		log.info("Processing booking confirmation text input");
		
		TaskResponse response = agentFactory.executeTask(request);
		
		if ("ERROR".equals(response.getStatus())) {
			return ResponseEntity.badRequest().body(response);
		}
		
		return ResponseEntity.ok(response);
	}

}