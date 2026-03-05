package com.velvet.rest.services.ai.beans;

import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class TaskRequest {
    
    @NotBlank(message = "Task type is required")
    private String taskType;
    
    @NotBlank(message = "Task description is required")
    private String taskDescription;
    
    @NotNull(message = "Task parameters are required")
    private Map<String, Object> parameters;
    
    private String userId;
    private String sessionId;
    private Integer priority = 1;
} 