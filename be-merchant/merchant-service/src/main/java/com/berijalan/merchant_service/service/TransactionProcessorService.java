package com.berijalan.merchant_service.service;

import com.berijalan.merchant_service.client.ProductFeignClient;
import com.berijalan.merchant_service.dto.request.ReqTransactionDto;
import com.berijalan.merchant_service.dto.response.ResTransactionDto;
import com.berijalan.merchant_service.exception.RetryableTransactionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionProcessorService {

    private final ProductFeignClient productClient;

    private static final List<String> RETRYABLE_REASONS = List.of(
            "PROVIDER_TIMEOUT",
            "PROVIDER_NETWORK_ERROR"
    );

    @Retryable(
            retryFor = { RetryableTransactionException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public ResTransactionDto process(ReqTransactionDto request) {
        log.info("Processing transaction: productId={}", request.getProductId());

        ResTransactionDto response = productClient.processTransaction(request).getData();

        if (response.getStatus().equals("FAILED") &&
                RETRYABLE_REASONS.contains(response.getFailureReason())) {
            log.warn("Retryable failure: {}", response.getFailureReason());
            throw new RetryableTransactionException(response.getFailureReason());
        }

        return response;
    }

    @Recover
    public ResTransactionDto recover(RetryableTransactionException e, ReqTransactionDto request) {
        log.error("All retries exhausted for productId={}, reason={}",
                request.getProductId(), e.getMessage());

        ResTransactionDto failed = new ResTransactionDto();
        failed.setStatus("FAILED");
        failed.setFailureReason(e.getMessage());
        return failed;
    }
}
