package com.berijalan.product_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResTransactionDto {
    private String refId;
    private String productId;
    private String productName;
    private String nomorTujuan;
    private Long price;
    private String status;
    private String failureReason;
}
