package com.berijalan.auth_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqCreateMerchantDto {
    private String accountId;
    private String namaMerchant;
}
