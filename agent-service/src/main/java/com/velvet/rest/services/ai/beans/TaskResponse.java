package com.velvet.rest.services.ai.beans;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class TaskResponse {
    
    private String taskId;
    private String taskType;
    private String status;
    private Object result;
    private String errorMessage;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long executionTimeMs;
    private Map<String, Object> metadata;
} 