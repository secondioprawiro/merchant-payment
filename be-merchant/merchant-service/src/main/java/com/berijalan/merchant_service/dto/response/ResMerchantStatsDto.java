package com.berijalan.merchant_service.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResMerchantStatsDto {
    private long totalTransaksiHariIni;
    private long totalBerhasilHariIni;
    private long totalGagalHariIni;

    private long totalTransaksiKeseluruhan;
    private long totalBerhasilKeseluruhan;
    private long totalGagalKeseluruhan;
}
