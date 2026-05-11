package com.berijalan.merchant_service.dto.response;

import com.berijalan.merchant_service.entity.MerchantTransactionEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResTransactionDto {
    private String refId;
    private String productId;
    private String nomorTujuan;
    private Long price;
    private String status;
    private String failureReason;
}