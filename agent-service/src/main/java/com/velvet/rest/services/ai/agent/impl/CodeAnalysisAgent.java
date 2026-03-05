package com.velvet.rest.services.ai.agent.impl;

import org.springframework.stereotype.Component;

import com.velvet.rest.services.ai.agent.OpenAIAgentStrategy;
import com.velvet.rest.services.ai.beans.TaskRequest;

@Component
public abstract class CodeAnalysisAgent extends OpenAIAgentStrategy {
    
    @Override
    public String getTaskType() {
        return "CODE_ANALYSIS";
    }
    
    @Override
    protected String buildPrompt(TaskRequest request) {
        String code = (String) request.getParameters().get("code");
        String language = (String) request.getParameters().getOrDefault("language", "Java");
        String analysisType = (String) request.getParameters().getOrDefault("analysisType", "general");
        
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an expert code analyst. Analyze the following ");
        prompt.append(language).append(" code and provide ");
        prompt.append(analysisType).append(" analysis.\n\n");
        
        prompt.append("Code to analyze:\n");
        prompt.append("```").append(language.toLowerCase()).append("\n");
        prompt.append(code);
        prompt.append("\n```\n\n");
        
        prompt.append("Please provide:\n");
        prompt.append("1. Code quality assessment\n");
        prompt.append("2. Potential issues and improvements\n");
        prompt.append("3. Best practices recommendations\n");
        prompt.append("4. Security considerations (if applicable)\n");
        prompt.append("5. Performance suggestions (if applicable)\n");
        
        return prompt.toString();
    }
    
    @Override
    protected String getTaskContext() {
        return "Code analysis and review";
    }

	@Override
	public Class getResponse() {
		// TODO Auto-generated method stub
		return null;
	}
} 