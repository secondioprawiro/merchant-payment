package com.berijalan.user_wallet_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="mst_wallet_mutation")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MutationEntity {

    public enum Type {
        CREDIT,
        DEBIT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private WalletEntity walletEntity;

    @Column(nullable = false)
    private long amount;

    @Column(nullable = false)
    private Type type;

    private String desc;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
