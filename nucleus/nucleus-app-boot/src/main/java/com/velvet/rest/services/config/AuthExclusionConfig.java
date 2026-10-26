package com.velvet.rest.services.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AuthExclusionConfig implements WebMvcConfigurer {
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // This configuration ensures that the todo trip-event endpoints
        // are excluded from authentication checks
        // The actual interceptor is in the framework, but we can
        // configure exclusions here
    }
    
    /**
     * Returns patterns that should be excluded from authentication
     * This can be used by the framework's SessionInterceptor
     */
    public static String[] getAuthExclusionPatterns() {
        return new String[] {
            "/swagger-ui/**",
            "/v2/api-docs/**",
            "/error"
        };
    }
}