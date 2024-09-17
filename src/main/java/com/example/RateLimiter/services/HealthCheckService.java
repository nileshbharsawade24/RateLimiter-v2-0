package com.example.RateLimiter.services;

import org.springframework.stereotype.Service;

@Service
public class HealthCheckService {

    public String checkHealth() {
        return "Health check successful!";
    }
}
