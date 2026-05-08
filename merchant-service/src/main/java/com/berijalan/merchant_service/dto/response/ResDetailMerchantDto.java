package com.berijalan.merchant_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResDetailMerchantDto {
    private String namaMerchant;
    private String email;
}
