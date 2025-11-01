package com.example.config;

import com.example.service.JwtService;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    private JwtService jwtService;

    @Autowired
    public void setJwtService(final JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Bean
    public RequestInterceptor feignJwtAuthFilter() {
        return template -> {
            String token=jwtService.generateJwtToken("monolith-service");
            template.header("Authorization", "Bearer "+token);
        };
    }
}
