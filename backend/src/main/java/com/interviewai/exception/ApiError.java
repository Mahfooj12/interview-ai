package com.interviewai.exception;

import java.time.Instant;

/**
 * Standard error body used across the API.
 */
public record ApiError(
        boolean success,
        String message,
        String errorCode,
        Instant timestamp,
        String path
) {
    public static ApiError of(String message, String errorCode, String path) {
        return new ApiError(false, message, errorCode, Instant.now(), path);
    }
}