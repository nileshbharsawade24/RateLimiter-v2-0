package com.example.RateLimiter.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimit {
    int requestsPerMinute() default 10; // Default rate limit if no mappings are provided

    RateLimitMapping[] value() default {};

    @Retention(RetentionPolicy.RUNTIME)
    @Target({})
    @interface RateLimitMapping {
        RateLimitEntry[] value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({})
    @interface RateLimitEntry {
        String key(); // Can be used to specify client-specific limits
        int requestsPerMinute();
    }
}
