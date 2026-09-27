package com.fluxbank.ledger_service.domain;


import com.fluxbank.commons_core.domain.Currency;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "entry_accounts")
public class EntryAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private AccountOwnerType ownerType;

    @Enumerated(EnumType.STRING)
    private AccountClass accountClass;
    private String code;

    @Enumerated(EnumType.STRING)
    private LedgerCategory category;
    private UUID ownerReferenceId;

    @Enumerated(EnumType.STRING)
    private Currency currency;

}
