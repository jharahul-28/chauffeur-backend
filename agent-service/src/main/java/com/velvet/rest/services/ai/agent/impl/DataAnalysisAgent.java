package com.velvet.rest.services.ai.agent.impl;

import org.springframework.stereotype.Component;

import com.velvet.rest.services.ai.agent.OpenAIAgentStrategy;
import com.velvet.rest.services.ai.beans.TaskRequest;

@Component
public abstract class DataAnalysisAgent extends OpenAIAgentStrategy {
    
    @Override
    public String getTaskType() {
        return "DATA_ANALYSIS";
    }
    
    @Override
    protected String buildPrompt(TaskRequest request) {
        String data = (String) request.getParameters().get("data");
        String analysisType = (String) request.getParameters().get("analysisType");
        String format = (String) request.getParameters().getOrDefault("format", "JSON");
        
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an expert data analyst. Analyze the following ");
        prompt.append(format).append(" data and provide ");
        prompt.append(analysisType).append(" insights.\n\n");
        
        prompt.append("Data to analyze:\n");
        prompt.append(data).append("\n\n");
        
        prompt.append("Please provide:\n");
        prompt.append("1. Summary of the data\n");
        prompt.append("2. Key patterns and trends\n");
        prompt.append("3. Statistical insights\n");
        prompt.append("4. Recommendations based on the analysis\n");
        prompt.append("5. Potential data quality issues (if any)\n");
        
        if ("trend".equalsIgnoreCase(analysisType)) {
            prompt.append("6. Trend analysis and forecasting\n");
        } else if ("comparison".equalsIgnoreCase(analysisType)) {
            prompt.append("6. Comparative analysis\n");
        } else if ("correlation".equalsIgnoreCase(analysisType)) {
            prompt.append("6. Correlation analysis\n");
        }
        
        return prompt.toString();
    }
    
    @Override
    protected String getTaskContext() {
        return "Data analysis and insights";
    }

	@Override
	public Class getResponse() {
		// TODO Auto-generated method stub
		return null;
	}
} 