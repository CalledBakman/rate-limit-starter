package com.github.calledbakman.rate_limit_starter.exception;

public class NoHttpRequestException extends RuntimeException {
    public NoHttpRequestException(String resolver, String keyName) {
        super(String.format("No Http Request found : %s needs a HTTP Request Header named %s!", resolver, keyName));
    }
}
