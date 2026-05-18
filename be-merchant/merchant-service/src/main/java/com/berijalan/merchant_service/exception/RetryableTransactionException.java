package com.berijalan.merchant_service.exception;

public class RetryableTransactionException extends RuntimeException {
    public RetryableTransactionException(String message) {
        super(message);
    }
}
