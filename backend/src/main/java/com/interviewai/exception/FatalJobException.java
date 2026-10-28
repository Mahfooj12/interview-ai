package com.interviewai.exception;

/**
 * Thrown when a job fails for a reason that will never succeed on retry
 * (e.g. referenced interview/resume does not exist, malformed payload).
 * The Kafka error handler routes these straight to the DLQ without retries.
 */
public class FatalJobException extends RuntimeException {

    public FatalJobException(String message) {
        super(message);
    }

    public FatalJobException(String message, Throwable cause) {
        super(message, cause);
    }
}