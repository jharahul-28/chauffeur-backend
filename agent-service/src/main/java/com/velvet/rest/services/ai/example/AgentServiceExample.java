package com.velvet.rest.services.ai.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.velvet.rest.services.ai.beans.TaskRequest;
import com.velvet.rest.services.ai.beans.TaskResponse;
import com.velvet.rest.services.ai.service.AgentFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Example class demonstrating how to use the Agent Service
 * This will run when the application starts (if enabled)
 */
@Component
public class AgentServiceExample implements CommandLineRunner {
    
    @Autowired
    private AgentFactory agentFactory;
    
    @Override
    public void run(String... args) throws Exception {
        // Uncomment the examples below to test the service
        
        // exampleCodeAnalysis();
        // exampleTextGeneration();
        // exampleDataAnalysis();
        // exampleTripSuggestion();
    }
    
    private void exampleCodeAnalysis() {
        System.out.println("=== Code Analysis Example ===");
        
        TaskRequest request = new TaskRequest();
        request.setTaskType("CODE_ANALYSIS");
        request.setTaskDescription("Analyze this Java code for best practices");
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("code", 
            "public class UserService {\n" +
            "    public String getUser(String id) {\n" +
            "        return \"SELECT * FROM users WHERE id = \" + id;\n" +
            "    }\n" +
            "}");
        parameters.put("language", "Java");
        parameters.put("analysisType", "security");
        
        request.setParameters(parameters);
        request.setUserId("example-user");
        
        TaskResponse response = agentFactory.executeTask(request);
        System.out.println("Status: " + response.getStatus());
        System.out.println("Result: " + response.getResult());
    }
    
    private void exampleTextGeneration() {
        System.out.println("=== Text Generation Example ===");
        
        TaskRequest request = new TaskRequest();
        request.setTaskType("TEXT_GENERATION");
        request.setTaskDescription("Generate a blog post about AI");
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("contentType", "blog post");
        parameters.put("topic", "The Future of Artificial Intelligence");
        parameters.put("tone", "informative");
        parameters.put("wordCount", 300);
        
        request.setParameters(parameters);
        request.setUserId("example-user");
        
        TaskResponse response = agentFactory.executeTask(request);
        System.out.println("Status: " + response.getStatus());
        System.out.println("Result: " + response.getResult());
    }
    
    private void exampleDataAnalysis() {
        System.out.println("=== Data Analysis Example ===");
        
        TaskRequest request = new TaskRequest();
        request.setTaskType("DATA_ANALYSIS");
        request.setTaskDescription("Analyze sales data for trends");
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("data", 
            "{\"sales\": [100, 150, 200, 180, 250], " +
            "\"months\": [\"Jan\", \"Feb\", \"Mar\", \"Apr\", \"May\"]}");
        parameters.put("analysisType", "trend");
        parameters.put("format", "JSON");
        
        request.setParameters(parameters);
        request.setUserId("example-user");
        
        TaskResponse response = agentFactory.executeTask(request);
        System.out.println("Status: " + response.getStatus());
        System.out.println("Result: " + response.getResult());
    }
    
    private void exampleTripSuggestion() {
        System.out.println("=== Trip Suggestion Example ===");
        
        TaskRequest request = new TaskRequest();
        request.setTaskType("TRIP_SUGGESTION");
        request.setTaskDescription("Suggest creative trip names for a destination");
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("destination", "Bali, Indonesia");
        parameters.put("tripType", "adventure");
        parameters.put("duration", "2 weeks");
        parameters.put("budget", "luxury");
        parameters.put("numberOfSuggestions", 3);
        
        request.setParameters(parameters);
        request.setUserId("example-user");
        
        TaskResponse response = agentFactory.executeTask(request);
        System.out.println("Status: " + response.getStatus());
        System.out.println("Result: " + response.getResult());
    }
} 