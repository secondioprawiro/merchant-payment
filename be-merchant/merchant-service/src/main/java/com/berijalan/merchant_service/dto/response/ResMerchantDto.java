package com.berijalan.merchant_service.dto.response;

import com.berijalan.merchant_service.entity.MerchantEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResMerchantDto {
    private UUID merchantId;
    private String kodeMerchant;
    private String namaMerchant;
    private MerchantEntity.Status status;
    private Boolean isDeleted;
}
