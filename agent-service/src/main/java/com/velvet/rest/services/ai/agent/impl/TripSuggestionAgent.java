package com.velvet.rest.services.ai.agent.impl;

import org.springframework.stereotype.Component;

import com.velvet.rest.services.ai.agent.OpenAIAgentStrategy;
import com.velvet.rest.services.ai.beans.TaskRequest;
import com.velvet.rest.services.ai.beans.TripNameSuggestions;

@Component
public class TripSuggestionAgent extends OpenAIAgentStrategy {

	@Override
	public String getTaskType() {
		return "TRIP_SUGGESTION";
	}

	@Override
	public Class<TripNameSuggestions> getResponse() {
		return TripNameSuggestions.class;
	}

	@Override
	protected String buildPrompt(TaskRequest request) {
		String destinations = (String) request.getParameters().get("destinations");
		String tripType = (String) request.getParameters().getOrDefault("tripType", "general");
		Integer numberOfSuggestions = (Integer) request.getParameters().getOrDefault("numberOfSuggestions", 1);

		StringBuilder prompt = new StringBuilder();
		prompt.append("You are an expert travel consultant and creative naming specialist. ");
		prompt.append("Based on the destination and trip details provided, suggest creative and appealing trip names and an image url of the first destination.\n\n");

		prompt.append("Destinations: ").append(destinations).append("\n");
		prompt.append("Trip Type: ").append(tripType).append("\n");

		prompt.append("Please provide ").append(numberOfSuggestions).append(" creative trip name suggestions, upto 25 characters, that:\n");
		prompt.append("1. Capture the essence and unique characteristics of ").append(destinations).append("\n");
		prompt.append("2. Reflect the trip type (adventure, relaxation, cultural, etc.)\n");
		prompt.append("3. Are memorable and appealing\n");
		prompt.append("4. Consider the duration and budget level\n");
		prompt.append("5. Include a brief explanation of the destinations, upto 250 chars\n");
		prompt.append("6. Provide an image URL of the destinations\n\n");

		prompt.append("Format your response as json object of:\n");
		prompt.append("- tripName: [Name, upto 25 characters]\n");
		prompt.append("  explanation: [Brief explanation of the locations, upto 250 chars]\n");
		prompt.append("  imageUrl: [ iconic pic of any destination in landscape]\n\n");

		prompt.append("Make the names creative, catchy, and relevant to the destination's culture, landmarks, or unique features.");

		return prompt.toString();
	}

	@Override
	protected String getTaskContext() {
		return "Travel trip name suggestions based on destination";
	}
}