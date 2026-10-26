package com.velvet.rest.services.util.beans;

import java.util.Date;

import lombok.Data;

@Data
public class FeedbackResponse {

	private Long id;
	
	private Integer userId;
	
	private String feedbackText;
	
	private String status;
	
	private Date createdOn;
	
	private Integer createdBy;
	
	private Date modifiedOn;
	
	private Integer modifiedBy;
	
	private Integer active;
}

