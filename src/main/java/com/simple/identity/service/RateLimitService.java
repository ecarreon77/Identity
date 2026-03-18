package com.simple.identity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    // Concurrent map to store user login attempts
    private final Map<String, List<Instant>> userAttemptsMap = new ConcurrentHashMap<>();

    // Max attempts per minute
    private static final int MAX_ATTEMPTS = 10;
    private static final long TIME_WINDOW = TimeUnit.MINUTES.toMillis(1);  // 1 minute time window

    public boolean isRateLimited(String userEmail) {
        // Get the list of timestamps for the user's previous login attempts
        List<Instant> attempts = userAttemptsMap.getOrDefault(userEmail, new ArrayList<>());

        // Remove attempts that are older than 1 minute
        long currentTime = System.currentTimeMillis();
        attempts.removeIf(attempt -> currentTime - attempt.toEpochMilli() > TIME_WINDOW);

        // Check if the number of attempts exceeds the limit
        return attempts.size() >= MAX_ATTEMPTS;
    }

    public void recordLoginAttempt(String userEmail) {
        List<Instant> attempts = userAttemptsMap.getOrDefault(userEmail, new ArrayList<>());

        // Add the current timestamp to the list of attempts
        attempts.add(Instant.now());

        // Store the updated list back in the map
        userAttemptsMap.put(userEmail, attempts);
    }
}
