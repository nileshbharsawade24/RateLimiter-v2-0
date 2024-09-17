package com.example.RateLimiter.controllers;

import com.example.RateLimiter.annotations.RateLimit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RateLimitedController {

    @GetMapping("/methodA")
    @RateLimit()
    public String methodA() {
        return "Response from Method A";
    }

    @GetMapping("/methodB")
    @RateLimit(requestsPerMinute = 100)
    public String methodB() {
        return "Response from Method B";
    }

    @GetMapping("/methodC")
    @RateLimit(value = @RateLimit.RateLimitMapping({
            @RateLimit.RateLimitEntry(key = "clientX", requestsPerMinute = 20),
            @RateLimit.RateLimitEntry(key = "clientY", requestsPerMinute = 30)
    }))
    public String methodC(@RequestParam String clientName) {
        return "Response from Method C";
    }
}
