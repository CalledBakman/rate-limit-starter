package com.github.calledbakman.rate_limit_starter.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RateLimitExceededForClientException extends RuntimeException {

    private final long retryAfter;

    public RateLimitExceededForClientException(long retryAfter) {
        super(String.format("You reached your limit! please try %d seconds later!: %s", retryAfter));
        this.retryAfter = retryAfter;
    }
}
