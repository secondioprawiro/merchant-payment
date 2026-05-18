package com.berijalan.merchant_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReqTransactionDto {
    @NotBlank(message = "Product Id Tidak Boleh Kosong")
    private String productId;

    @NotBlank(message = "Nomor Tujuan Tidak Boleh Kosong")
    private String nomorTujuan;

    @NotBlank
    private String idempotencyKey;
}