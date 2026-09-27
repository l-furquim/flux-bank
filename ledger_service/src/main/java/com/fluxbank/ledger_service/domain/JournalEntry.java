package com.fluxbank.ledger_service.domain;

import com.fluxbank.commons_core.domain.Metadata;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "journal_entries")
public class JournalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;
    private UUID sourceEventId;
    private UUID sourceTxId;
    private UUID sourceEntryAccountId;
    private UUID destinationEntryAccountId;

    @Enumerated(EnumType.STRING)
    private EntryType type;
    private String description;
    private LocalDateTime postedAt;

    @Enumerated(EnumType.STRING)
    private EntryStatus status;

    private UUID reversedByJournalEntryId;
    private LocalDateTime reversedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Metadata metadata;

}
