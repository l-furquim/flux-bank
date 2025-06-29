package com.fluxbank.wallet_service.domain.models;

import com.fluxbank.wallet_service.domain.enums.MethodStatus;
import com.fluxbank.wallet_service.domain.enums.MethodType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "payment_methods")
public class PaymentMethods {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @NotNull
    @Enumerated(EnumType.STRING)
    private MethodType methodType;

    @NotNull
    private String displayName;

    private String lastFourDigits;

    private LocalDateTime expiryDate;

    @Enumerated(EnumType.STRING)
    private MethodStatus status;

    @Builder.Default
    private Boolean isDefault = false;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDateTime.now());
    }

    public boolean isUsable() {
        return status == MethodStatus.ACTIVE && !isExpired();
    }

}
