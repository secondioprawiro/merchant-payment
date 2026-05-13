package com.berijalan.merchant_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResDetailMerchantDto {
    private UUID merchantId;
    private String kodeMerchant;
    private String namaMerchant;
    private String email;
}
