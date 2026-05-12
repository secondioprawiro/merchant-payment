package com.berijalan.merchant_service.controller;

import com.berijalan.merchant_service.service.TransactionService;
import com.berijalan.merchant_service.dto.request.ReqTransactionDto;
import com.berijalan.merchant_service.dto.response.BaseResponse;
import com.berijalan.merchant_service.entity.MerchantTransactionEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping()
    public ResponseEntity<BaseResponse<?>> buyProduct(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody ReqTransactionDto request
    ) {
        MerchantTransactionEntity transaction = transactionService.buyProduct(userId, request);
        return ResponseEntity.ok(
                transaction.getStatus().equals(MerchantTransactionEntity.Status.FAILED)
                        ? BaseResponse.failed("Transaksi gagal", transaction)
                        : BaseResponse.success("Transaksi berhasil", transaction)
        );
    }

    @GetMapping()
    public ResponseEntity<BaseResponse<?>> getAllTransactions(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role
    ){
        List<MerchantTransactionEntity> transaction = transactionService.getAllTransactions(userId, role);
        return ResponseEntity.ok(BaseResponse.success("Success", transaction));
    }

    @GetMapping("{transactionId}")
    public ResponseEntity<BaseResponse<?>> getTransactionById(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @PathVariable UUID transactionId
    ){
        MerchantTransactionEntity transaction = transactionService.getTransactionById(userId, role, transactionId);
        return ResponseEntity.ok(BaseResponse.success("Success", transaction));
    }




}
