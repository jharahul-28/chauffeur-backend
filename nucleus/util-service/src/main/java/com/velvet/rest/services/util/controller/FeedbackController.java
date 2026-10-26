package com.velvet.rest.services.util.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.velvet.rest.common.websupport.base.BaseController;
import com.velvet.rest.services.common.mapper.BaseMapper;
import com.velvet.rest.services.util.beans.FeedbackRequest;
import com.velvet.rest.services.util.beans.FeedbackResponse;
import com.velvet.rest.services.util.entity.FeedbackEntity;
import com.velvet.rest.services.util.services.FeedbackService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * The Class FeedbackController.
 *
 * @author Mohit
 * @version 1.0
 */
@RestController
@Api(tags = "Feedback Services", description = "Manage user feedback.")
public class FeedbackController implements BaseController {

	private static final Logger logger = LoggerFactory.getLogger(FeedbackController.class);

	@Autowired
	private BaseMapper<?, ?> feedbackMapper;

	@Autowired
	private FeedbackService feedbackService;

	@ApiOperation(value = "Submit user feedback", nickname = "submitFeedback")
	@PostMapping("/utils/feedback")
	public ResponseEntity<FeedbackResponse> submitFeedback(
			@ApiParam(value = "Feedback details", required = true) @RequestBody FeedbackRequest request) {
		
		try {
			if (request.getUserId() == null || request.getFeedbackText() == null || request.getFeedbackText().trim().isEmpty()) {
				logger.error("Invalid feedback request: userId or feedbackText is missing");
				return ResponseEntity.badRequest().build();
			}

			// Set default status to OPEN if not provided
			if (request.getStatus() == null || request.getStatus().trim().isEmpty()) {
				request.setStatus("OPEN");
			}

			FeedbackEntity feedbackEntity = feedbackMapper.mapSrcToDest(request, new FeedbackEntity());
			FeedbackEntity savedFeedback = feedbackService.saveFeedback(feedbackEntity);
			FeedbackResponse response = feedbackMapper.mapSrcToDest(savedFeedback, new FeedbackResponse());

			logger.info("Feedback submitted successfully for user: {}", request.getUserId());
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (Exception e) {
			logger.error("Error submitting feedback", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@ApiOperation(value = "Get feedback by user ID", nickname = "getFeedbackByUserId")
	@GetMapping("/utils/feedback/user/{userId}")
	public ResponseEntity<List<FeedbackResponse>> getFeedbackByUserId(
			@ApiParam(value = "User ID", required = true) @PathVariable Integer userId) {
		
		try {
			List<FeedbackEntity> feedbackList = feedbackService.getFeedbackByUserId(userId);
			List<FeedbackResponse> response = feedbackMapper.mapAsList(feedbackList, FeedbackResponse.class);
			
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			logger.error("Error fetching feedback for user: {}", userId, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@ApiOperation(value = "Get all feedback", nickname = "getAllFeedback")
	@GetMapping("/utils/feedback")
	public ResponseEntity<List<FeedbackResponse>> getAllFeedback() {
		
		try {
			List<FeedbackEntity> feedbackList = feedbackService.getAllFeedback();
			List<FeedbackResponse> response = feedbackMapper.mapAsList(feedbackList, FeedbackResponse.class);
			
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			logger.error("Error fetching all feedback", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@ApiOperation(value = "Get feedback by ID", nickname = "getFeedbackById")
	@GetMapping("/utils/feedback/{id}")
	public ResponseEntity<FeedbackResponse> getFeedbackById(
			@ApiParam(value = "Feedback ID", required = true) @PathVariable Long id) {
		
		try {
			FeedbackEntity feedback = feedbackService.findRecordById(id);
			if (feedback == null) {
				return ResponseEntity.notFound().build();
			}
			FeedbackResponse response = feedbackMapper.mapSrcToDest(feedback, new FeedbackResponse());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			logger.error("Error fetching feedback by ID: {}", id, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@ApiOperation(value = "Get feedback by status", nickname = "getFeedbackByStatus")
	@GetMapping("/utils/feedback/status/{status}")
	public ResponseEntity<List<FeedbackResponse>> getFeedbackByStatus(
			@ApiParam(value = "Status: OPEN or RESOLVED", required = true) @PathVariable String status) {
		
		try {
			List<FeedbackEntity> feedbackList = feedbackService.getFeedbackByStatus(status.toUpperCase());
			List<FeedbackResponse> response = feedbackMapper.mapAsList(feedbackList, FeedbackResponse.class);
			
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			logger.error("Error fetching feedback by status: {}", status, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@ApiOperation(value = "Update feedback status", nickname = "updateFeedbackStatus")
	@PatchMapping("/utils/feedback/{id}/status")
	public ResponseEntity<FeedbackResponse> updateFeedbackStatus(
			@ApiParam(value = "Feedback ID", required = true) @PathVariable Long id,
			@ApiParam(value = "New status: OPEN or RESOLVED", required = true) @RequestParam String status) {
		
		try {
			FeedbackEntity feedback = feedbackService.findRecordById(id);
			if (feedback == null) {
				return ResponseEntity.notFound().build();
			}

			feedback.setStatus(status.toUpperCase());
			FeedbackEntity updatedFeedback = feedbackService.saveFeedback(feedback);
			FeedbackResponse response = feedbackMapper.mapSrcToDest(updatedFeedback, new FeedbackResponse());

			logger.info("Feedback status updated successfully for ID: {}", id);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			logger.error("Error updating feedback status for ID: {}", id, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@Override
	public BaseMapper<?, ?> getMapper() {
		return feedbackMapper;
	}
}

