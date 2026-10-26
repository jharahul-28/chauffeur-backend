package com.velvet.rest.services.util.beans;

import lombok.Data;

@Data
public class FeedbackRequest {

	private Integer userId;
	
	private String feedbackText;
	
	private String status;  // Optional: OPEN, RESOLVED (defaults to OPEN if not provided)
}

