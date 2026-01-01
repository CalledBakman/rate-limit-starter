package com.github.calledbakman.rate_limit_starter.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class ErrorDetail {
    private LocalDateTime timeStamp;
    private String requestPath;
    private String errorCode;
    private Long retryAfter;
    private String message;
}
