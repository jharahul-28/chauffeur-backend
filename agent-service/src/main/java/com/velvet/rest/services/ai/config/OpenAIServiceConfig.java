package com.velvet.rest.services.ai.config;

import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OpenAIServiceConfig {
    
    @Autowired
    private OpenAIConfig openAIConfig;
    
    @Bean
    public OpenAiService openAiService() {
        return new OpenAiService(openAIConfig.getApiKey(), 
                                Duration.ofSeconds(openAIConfig.getTimeout()));
    }
} 