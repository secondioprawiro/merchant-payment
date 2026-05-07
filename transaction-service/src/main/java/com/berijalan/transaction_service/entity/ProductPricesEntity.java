package com.berijalan.transaction_service.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductPricesEntity {

    @Id
    private Long id;

    @Column(name = "selling_price", nullable = false)
    private long sellingPrice;

    @Column(name = "admin_fee", nullable = false)
    private long adminFee;

    @Column(nullable = false)
    private boolean is_active;

    @CreationTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
