package com.github.calledbakman.rate_limit_starter.exception;

import lombok.Getter;

@Getter
public class ServiceRateLimitExceededException extends RuntimeException {
    private final long retryAfter;

    public ServiceRateLimitExceededException(long retryAfter) {
        super(String.format("service exceeded the limit! please try %d seconds later!", retryAfter));
        this.retryAfter = retryAfter;
    }
}
