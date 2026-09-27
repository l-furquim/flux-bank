package com.fluxbank.ledger_service.domain;

import com.fluxbank.commons_core.domain.Metadata;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "entries")
public class Entry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    private UUID journalEntryId;
    private UUID entryAccountId;

    @Enumerated(EnumType.STRING)
    private EntryDirection entryDirection;

    @Enumerated(EnumType.STRING)
    private AccountOwnerType accountOwnerType;

    private UUID sourceEntryId;
    private UUID previousEntryId;

    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Metadata metadata;
    private UUID reversesEntryId;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
