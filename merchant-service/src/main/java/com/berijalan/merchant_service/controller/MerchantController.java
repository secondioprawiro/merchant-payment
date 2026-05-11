package com.berijalan.merchant_service.controller;

import com.berijalan.merchant_service.dto.request.ReqInternalCreateMerchantDto;
import com.berijalan.merchant_service.dto.request.ReqTransactionDto;
import com.berijalan.merchant_service.dto.request.ReqUpdateMerchantDto;
import com.berijalan.merchant_service.dto.response.BaseResponse;
import com.berijalan.merchant_service.dto.response.ResDetailMerchantDto;
import com.berijalan.merchant_service.dto.response.ResMerchantDto;
import com.berijalan.merchant_service.entity.MerchantTransactionEntity;
import com.berijalan.merchant_service.exception.ForbiddenException;
import com.berijalan.merchant_service.service.MerchantService;
import com.berijalan.merchant_service.service.ProductService;
import com.berijalan.merchant_service.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/merchant")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;
    private final ProductService productService;
    private final TransactionService transactionService;

    @PostMapping("/internal/create")
    public ResponseEntity<BaseResponse<Void>> createMerchant(@RequestBody ReqInternalCreateMerchantDto request) {
        merchantService.createMerchant(request);
        return ResponseEntity.ok(BaseResponse.success("Merchant created", null));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<ResMerchantDto>>> getAllMerchants(
            @RequestHeader("X-User-Role") String role) {
        if (!"ADMIN".equals(role)) {
            throw new ForbiddenException("Access denied");
        }
        List<ResMerchantDto> merchants = merchantService.getAllMerchants();
        return ResponseEntity.ok(BaseResponse.success("Success", merchants));
    }

    @GetMapping("/{merchantId}")
    public ResponseEntity<BaseResponse<ResDetailMerchantDto>> getDetailMerchant(
            @PathVariable UUID merchantId,
            @RequestHeader("X-User-Role") String role) {
        ResDetailMerchantDto detail = merchantService.getDetailMerchant(merchantId);
        return ResponseEntity.ok(BaseResponse.success("Success", detail));
    }

    @PutMapping("/{merchantId}")
    public ResponseEntity<BaseResponse<ResDetailMerchantDto>> updateMerchant(
            @PathVariable UUID merchantId,
            @RequestBody ReqUpdateMerchantDto request,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        ResDetailMerchantDto updated = merchantService.updateMerchant(merchantId, request, userId, role);
        return ResponseEntity.ok(BaseResponse.success("Merchant updated", updated));
    }

    @DeleteMapping("/{merchantId}")
    public ResponseEntity<BaseResponse<Void>> deleteMerchant(
            @PathVariable UUID merchantId,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        merchantService.deleteMerchant(merchantId, userId, role);
        return ResponseEntity.ok(BaseResponse.success("Merchant deleted", null));
    }

    @GetMapping("/product/all")
    public ResponseEntity getAllProduct(
            @RequestParam(required = false) String type
    ) {
        return ResponseEntity.ok(productService.getAllProduct(type));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity getProductById(@PathVariable String productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @PostMapping("/product/transaction")
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
}
