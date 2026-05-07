package com.berijalan.user_wallet_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Table(name="mst_wallet")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletEntity {
    @Id
    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private long balance;
}
