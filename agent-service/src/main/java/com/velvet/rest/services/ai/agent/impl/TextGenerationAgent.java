package com.velvet.rest.services.ai.agent.impl;

import org.springframework.stereotype.Component;

import com.velvet.rest.services.ai.agent.OpenAIAgentStrategy;
import com.velvet.rest.services.ai.beans.TaskRequest;

@Component
public abstract class TextGenerationAgent extends OpenAIAgentStrategy {
    
    @Override
    public String getTaskType() {
        return "TEXT_GENERATION";
    }
    
    @Override
    protected String buildPrompt(TaskRequest request) {
        String contentType = (String) request.getParameters().get("contentType");
        String topic = (String) request.getParameters().get("topic");
        String tone = (String) request.getParameters().getOrDefault("tone", "professional");
        Integer wordCount = (Integer) request.getParameters().getOrDefault("wordCount", 500);
        
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an expert content writer. Generate ");
        prompt.append(contentType).append(" content about '").append(topic).append("'.\n\n");
        
        prompt.append("Requirements:\n");
        prompt.append("- Content type: ").append(contentType).append("\n");
        prompt.append("- Topic: ").append(topic).append("\n");
        prompt.append("- Tone: ").append(tone).append("\n");
        prompt.append("- Target word count: ").append(wordCount).append("\n\n");
        
        prompt.append("Please ensure the content is:\n");
        prompt.append("1. Engaging and well-structured\n");
        prompt.append("2. Relevant to the topic\n");
        prompt.append("3. Written in the specified tone\n");
        prompt.append("4. Appropriate length\n");
        prompt.append("5. Original and creative\n");
        
        return prompt.toString();
    }
    
    @Override
    protected String getTaskContext() {
        return "Content generation and writing";
    }

	@Override
	public Class getResponse() {
		// TODO Auto-generated method stub
		return null;
	}
} 