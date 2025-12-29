package com.github.calledbakman.rate_limit_starter.exception;

import lombok.Getter;

@Getter
public class ServiceNotAvailableException extends RuntimeException {
    private final String retryAt;
    private final String until;

    public ServiceNotAvailableException(String retryAt, String until) {
        super(String.format("The service is not available right now! please try at %s to %s!", retryAt, until));
        this.retryAt = retryAt;
        this.until = until;
    }
}
