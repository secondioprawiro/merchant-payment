package com.berijalan.merchant_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResAdminStatsDto {

    private long totalMerchant;
    private long totalMerchantAktif;
    private long totalMerchantNonAktif;

    private long totalTransaksiHariIni;
    private long totalBerhasilHariIni;
    private long totalGagalHariIni;

    private long totalTransaksiKeseluruhan;
    private long totalBerhasilKeseluruhan;
    private long totalGagalKeseluruhan;
}