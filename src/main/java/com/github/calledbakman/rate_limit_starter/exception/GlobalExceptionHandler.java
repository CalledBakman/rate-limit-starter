package com.github.calledbakman.rate_limit_starter.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RateLimitExceededForClientException.class)
    public ResponseEntity<ErrorDetail> handleRateLimitExceededForClient(RateLimitExceededForClientException e, WebRequest webRequest){
        ErrorDetail errorDetail = new ErrorDetail(
                LocalDateTime.now(),
                webRequest.getContextPath(),
                "429 TOO MANY REQUESTS",
                e.getRetryAfter(),
                e.getMessage()
        );

        logger.warn("CLIENT RATE LIMIT EXCEEDED | TimeStamp: {} | ErrorCode: {} | Path: {} | Retry: After {} seconds | Details: {}",
                errorDetail.getTimeStamp(),
                errorDetail.getErrorCode(),
                errorDetail.getRequestPath(),
                errorDetail.getRetryAfter(),
                errorDetail.getMessage());

        HttpHeaders header = new HttpHeaders();
        header.set("RETRY-AFTER", String.valueOf(e.getRetryAfter()));

        return new ResponseEntity<>(errorDetail, header, HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(ServiceRateLimitExceededException.class)
    public ResponseEntity<ErrorDetail> handleServiceRateLimitExceeded(ServiceRateLimitExceededException e, WebRequest webRequest){
        ErrorDetail errorDetail = new ErrorDetail(
                LocalDateTime.now(),
                webRequest.getContextPath(),
                "429 TOO MANY REQUESTS",
                e.getRetryAfter(),
                e.getMessage()
        );

        logger.warn("SERVICE RATE LIMIT EXCEEDED | TimeStamp: {} | ErrorCode: {} | Path: {} | Retry: After {} seconds | Details: {}",
                errorDetail.getTimeStamp(),
                errorDetail.getErrorCode(),
                errorDetail.getRequestPath(),
                errorDetail.getRetryAfter(),
                errorDetail.getMessage());

        HttpHeaders header = new HttpHeaders();
        header.set("RETRY-AFTER", String.valueOf(e.getRetryAfter()));

        return new ResponseEntity<>(errorDetail, header, HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(ServiceNotAvailableException.class)
    public ResponseEntity<ErrorDetail> handleServiceNotAvailable(ServiceNotAvailableException e, WebRequest webRequest){
        ErrorDetail errorDetail = new ErrorDetail(
                LocalDateTime.now(),
                webRequest.getContextPath(),
                "503 SERVICE UNAVAILABLE",
                null,
                e.getMessage()
        );

        logger.warn("SERVICE UNAVAILABLE | TimeStamp: {} | ErrorCode: {} | Path: {} | Retry: At {} until {} | Details: {}",
                errorDetail.getTimeStamp(),
                errorDetail.getErrorCode(),
                errorDetail.getRequestPath(),
                e.getRetryAt(),
                e.getUntil(),
                errorDetail.getMessage());

        HttpHeaders header = new HttpHeaders();
        header.set("RETRY-AT", String.valueOf(e.getRetryAt()));

        return new ResponseEntity<>(errorDetail, header, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(NoHttpRequestException.class)
    public ResponseEntity<ErrorDetail> noHttpRequestExceptionHandler(NoHttpRequestException e, WebRequest webRequest){
        ErrorDetail errorDetail = new ErrorDetail(
                LocalDateTime.now(),
                webRequest.getContextPath(),
                "500 INTERNAL SERVER ERROR",
                null,
                e.getMessage()
        );

        logger.warn("SERVICE UNAVAILABLE | TimeStamp: {} | ErrorCode: {} | Path: {} | Details: {}",
                errorDetail.getTimeStamp(),
                errorDetail.getErrorCode(),
                errorDetail.getRequestPath(),
                errorDetail.getMessage());

        return new ResponseEntity<>(errorDetail, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
