package com.example.RateLimiter.configs;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Bandwidth;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

//@Configuration
//public class RateLimiterConfig {
//
//    private final ConcurrentMap<String, Bucket> bucketMap = new ConcurrentHashMap<>();
//
//    // Use a combination of clientName and methodName as the key
//    public void registerRateLimit(String key, int requestsPerMinute) {
//        Bandwidth limit = Bandwidth.simple(requestsPerMinute, Duration.ofMinutes(1));
//        Bucket bucket = Bucket4j.builder().addLimit(limit).build();
//        bucketMap.put(key, bucket);
//    }
//
//    public Bucket getBucket(String key) {
//        return bucketMap.get(key);
//    }
//}

@Configuration
public class RateLimiterConfig {

    private final ConcurrentMap<String, Bucket> bucketMap = new ConcurrentHashMap<>();

    public void registerRateLimit(String key, int requestsPerMinute) {
        Bandwidth limit = Bandwidth.simple(requestsPerMinute, Duration.ofMinutes(1));
        Bucket bucket = Bucket4j.builder().addLimit(limit).build();
        bucketMap.put(key, bucket);
        System.out.println("Bucket registered: " + key + " | Requests per Minute: " + bucket.getAvailableTokens());
    }

    public Bucket getBucket(String key) {
        return bucketMap.get(key);
    }
}
