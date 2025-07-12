package com.fluxbank.user_service.infrastructure.persistence.entity;

import com.fluxbank.user_service.domain.enums.KeyType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "pix_keys")
public class PixKeyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    private UUID ownerId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private KeyType type;

    @CreationTimestamp
    private LocalDateTime issuedAt;

    @NotBlank
    private String value;

}
