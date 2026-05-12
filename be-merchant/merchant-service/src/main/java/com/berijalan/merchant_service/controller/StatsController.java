package com.berijalan.merchant_service.controller;

import com.berijalan.merchant_service.dto.response.BaseResponse;
import com.berijalan.merchant_service.exception.BadRequestException;
import com.berijalan.merchant_service.service.MerchantService;
import com.berijalan.merchant_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class StatsController {
    private final TransactionService transactionService;

    @GetMapping("/admin")
    public ResponseEntity<BaseResponse<?>> getAdminStats(@RequestHeader("X-User-Role") String role){
        if (!role.equals("ADMIN")) {
            throw new BadRequestException("Tidak memiliki akses");
        }
        return ResponseEntity.ok(BaseResponse.success("Success", transactionService.getAdminStats()));
    }

    @GetMapping("/merchant")
    public ResponseEntity<BaseResponse<?>> getMerchantStats(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @RequestParam(required = false) UUID merchantId
    ){
        return ResponseEntity.ok(
                BaseResponse.success("Success",
                        transactionService.getMerchantStats(userId, role, merchantId))
        );
    }


}
