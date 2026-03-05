package com.velvet.rest.services.ai.agent;

import com.velvet.rest.services.ai.beans.TaskRequest;
import com.velvet.rest.services.ai.beans.TaskResponse;

public interface AgentStrategy {

	/**
	 * Get the task type this strategy handles
	 */
	String getTaskType();

	Class<?> getResponse();

	/**
	 * Execute the task using this agent strategy
	 */
	TaskResponse execute(TaskRequest request);

	/**
	 * Check if this strategy can handle the given task type
	 */
	default boolean canHandle(String taskType) {
		return getTaskType().equalsIgnoreCase(taskType);
	}
}