package com.example.RateLimiter.aspects;

import com.example.RateLimiter.annotations.RateLimit;
import com.example.RateLimiter.configs.RateLimiterConfig;
import com.example.RateLimiter.exceptions.RateLimitExceededException;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
//@Aspect
//@Component
//@AllArgsConstructor
//public class RateLimitingAspect {
//
//    private final RateLimiterConfig rateLimiterConfig;
//
//    @Pointcut("@annotation(rateLimit)")
//    public void rateLimitPointcut(RateLimit rateLimit) {}
//
//    @Around("rateLimitPointcut(rateLimit)")
//    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
//        String methodName = joinPoint.getSignature().getName();
//
//        // Extract clientName from request parameters
//        String clientName = extractClientName(joinPoint);
//        if (clientName == null) {
//            clientName = "default"; // Use default key if clientName is not present
//        }
//
//        // Determine the rate limit based on annotation
//        RateLimit.RateLimitMapping[] mappings = rateLimit.value();
//        int requestsPerMinute = getRequestsPerMinuteForClient(clientName, mappings, rateLimit.requestsPerMinute());
//
//        if (requestsPerMinute == -1) {
//            throw new IllegalArgumentException("No rate limit found for client: " + clientName);
//        }
//
//        String key = clientName + ":" + methodName;
//        Bucket bucket = rateLimiterConfig.getBucket(key);
//
//        if (bucket == null) {
//            // Register the rate limit if not already present
//            rateLimiterConfig.registerRateLimit(key, requestsPerMinute);
//            bucket = rateLimiterConfig.getBucket(key);
//        }
//
//        if (bucket.tryConsume(1)) {
//            return joinPoint.proceed();
//        } else {
//            throw new RateLimitExceededException("Rate limit exceeded for client: " + clientName + " and method: " + methodName);
//        }
//    }
//
//    private String extractClientName(ProceedingJoinPoint joinPoint) {
//        // Logic to extract clientName from request parameters
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        return request.getParameter("clientName");
//    }
//
//    private int getRequestsPerMinuteForClient(String clientName, RateLimit.RateLimitMapping[] mappings, int defaultRequestsPerMinute) {
//        for (RateLimit.RateLimitMapping mapping : mappings) {
//            for (RateLimit.RateLimitEntry entry : mapping.value()) {
//                if (entry.key().equals(clientName)) {
//                    return entry.requestsPerMinute();
//                }
//            }
//        }
//        return defaultRequestsPerMinute; // Return default rate limit if no specific entry found
//    }
//}
@Aspect
@Component
@AllArgsConstructor
public class RateLimitingAspect {

    private final RateLimiterConfig rateLimiterConfig;

    @Pointcut("@annotation(rateLimit)")
    public void rateLimitPointcut(RateLimit rateLimit) {}

    @Around("rateLimitPointcut(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String clientName = extractClientName(joinPoint);
        if (clientName == null) {
            clientName = "default";
        }

        RateLimit.RateLimitMapping[] mappings = rateLimit.value();
        int requestsPerMinute = getRequestsPerMinuteForClient(clientName, mappings, rateLimit.requestsPerMinute());

        String key = clientName + ":" + methodName;
        Bucket bucket = rateLimiterConfig.getBucket(key);

        if (bucket == null) {
            rateLimiterConfig.registerRateLimit(key, requestsPerMinute);
            bucket = rateLimiterConfig.getBucket(key);
        }

        // Debugging output
        System.out.println("Bucket Key: " + key);
        System.out.println("Available Tokens before request: " + bucket.getAvailableTokens());

        if (bucket.tryConsume(1)) {
            System.out.println("Available Tokens after request: " + bucket.getAvailableTokens());
            return joinPoint.proceed();
        } else {
            System.out.println("Available Tokens after request: " + bucket.getAvailableTokens());
            throw new RateLimitExceededException("Rate limit exceeded for client: " + clientName + " and method: " + methodName);
        }
    }

    private String extractClientName(ProceedingJoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request.getParameter("clientName");
    }

    private int getRequestsPerMinuteForClient(String clientName, RateLimit.RateLimitMapping[] mappings, int defaultRequestsPerMinute) {
        for (RateLimit.RateLimitMapping mapping : mappings) {
            for (RateLimit.RateLimitEntry entry : mapping.value()) {
                if (entry.key().equals(clientName)) {
                    return entry.requestsPerMinute();
                }
            }
        }
        return defaultRequestsPerMinute;
    }
}
