package com.velvet.rest.services.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.velvet.rest.services.ai.agent.AgentStrategy;
import com.velvet.rest.services.ai.beans.TaskRequest;
import com.velvet.rest.services.ai.beans.TaskResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class AgentFactory {

	private final Map<String, AgentStrategy> agentStrategies = new ConcurrentHashMap<>();

	@Autowired
	public AgentFactory(List<AgentStrategy> strategies) {
		for (AgentStrategy strategy : strategies) {
			agentStrategies.put(strategy.getTaskType().toUpperCase(), strategy);
			log.info("Registered agent strategy: {}", strategy.getTaskType());
		}
	}

	/**
	 * Execute a task using the appropriate agent strategy
	 */
	public TaskResponse executeTask(TaskRequest request) {
		String taskType = request.getTaskType().toUpperCase();
		AgentStrategy strategy = agentStrategies.get(taskType);

		if (strategy == null) {
			log.error("No agent strategy found for task type: {}", taskType);
			return TaskResponse.builder().taskType(taskType).status("ERROR").errorMessage("Unsupported task type: " + taskType).build();
		}

		log.info("Executing task with strategy: {}", strategy.getClass().getSimpleName());
		return strategy.execute(request);
	}

	/**
	 * Get all available task types
	 */
	public List<String> getAvailableTaskTypes() {
		return List.copyOf(agentStrategies.keySet());
	}

	/**
	 * Check if a task type is supported
	 */
	public boolean isTaskTypeSupported(String taskType) {
		return agentStrategies.containsKey(taskType.toUpperCase());
	}
}