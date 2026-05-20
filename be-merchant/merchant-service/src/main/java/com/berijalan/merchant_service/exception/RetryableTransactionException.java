package com.berijalan.merchant_service.exception;

import com.berijalan.merchant_service.dto.response.ResTransactionDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RetryableTransactionException extends RuntimeException {
    private final ResTransactionDto lastResponse;

    public RetryableTransactionException(String message, ResTransactionDto lastResponse) {
        super(message);
        this.lastResponse = lastResponse;
    }
}
