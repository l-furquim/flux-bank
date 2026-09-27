-- V1__create_ledger_tables.sql

CREATE TABLE entry_accounts (
                                id                  UUID PRIMARY KEY DEFAULT uuidv7(),

                                owner_type          VARCHAR(20) NOT NULL,
                                account_class       VARCHAR(20) NOT NULL,
                                code                VARCHAR(100) NOT NULL,
                                category            VARCHAR(50) NOT NULL,
                                owner_reference_id  UUID,
                                currency            VARCHAR(3) NOT NULL,

                                CONSTRAINT uk_entry_accounts_code
                                    UNIQUE (code),

                                CONSTRAINT chk_entry_accounts_owner_type
                                    CHECK (owner_type IN (
                                                          'CUSTOMER',
                                                          'MERCHANT',
                                                          'INTERNAL'
                                        )),

                                CONSTRAINT chk_entry_accounts_account_class
                                    CHECK (account_class IN (
                                                             'REVENUE',
                                                             'ASSET',
                                                             'LIABILITY',
                                                             'EQUITY',
                                                             'EXPENSE'
                                        )),

                                CONSTRAINT chk_entry_accounts_category
                                    CHECK (category IN (
                                                        'CUSTOMER_DEPOSITS',
                                                        'CREDIT_RECEIVABLE',
                                                        'MERCHANT_PAYABLE'
                                        )),

                                CONSTRAINT chk_entry_accounts_currency
                                    CHECK (currency ~ '^[A-Z]{3}$')
    );


CREATE TABLE journal_entries (
                                 id                              UUID PRIMARY KEY DEFAULT uuidv7(),

                                 source_event_id                 UUID,
                                 source_tx_id                    UUID,
                                 source_entry_account_id         UUID,
                                 destination_entry_account_id    UUID,

                                 type                            VARCHAR(30) NOT NULL,
                                 description                     VARCHAR(500),

                                 posted_at                       TIMESTAMP,
                                 status                          VARCHAR(20) NOT NULL DEFAULT 'POSTED',

                                 reversed_by_journal_entry_id    UUID,
                                 reversed_at                     TIMESTAMP,

                                 metadata                        JSONB NOT NULL DEFAULT '{}'::jsonb,

                                 CONSTRAINT fk_journal_entries_source_account
                                     FOREIGN KEY (source_entry_account_id)
                                         REFERENCES entry_accounts (id),

                                 CONSTRAINT fk_journal_entries_destination_account
                                     FOREIGN KEY (destination_entry_account_id)
                                         REFERENCES entry_accounts (id),

                                 CONSTRAINT fk_journal_entries_reversed_by
                                     FOREIGN KEY (reversed_by_journal_entry_id)
                                         REFERENCES journal_entries (id),

                                 CONSTRAINT chk_journal_entries_type
                                     CHECK (type IN (
                                                     'PIX',
                                                     'INSTALLMENT',
                                                     'REVERSAL',
                                                     'CHARGEBACK',
                                                     'MED_REFUND',
                                                     'FEE',
                                                     'ACQUIRING_SETTLEMENT',
                                                     'INTEREST'
                                         )),

                                 CONSTRAINT chk_journal_entries_status
                                     CHECK (status IN (
                                                       'POSTED',
                                                       'REVERSED'
                                         )),

                                 CONSTRAINT chk_journal_entries_reversed_at
                                     CHECK (
                                         (status = 'POSTED' AND reversed_at IS NULL)
                                             OR
                                         (status = 'REVERSED')
                                         )
);


CREATE TABLE entries (
                         id                  UUID PRIMARY KEY DEFAULT uuidv7(),

                         journal_entry_id    UUID NOT NULL,
                         entry_account_id    UUID NOT NULL,

                         entry_direction     VARCHAR(10) NOT NULL,
                         account_owner_type  VARCHAR(20) NOT NULL,

                         source_entry_id     UUID,
                         previous_entry_id   UUID,

                         balance_before      NUMERIC(19, 4) NOT NULL,
                         balance_after       NUMERIC(19, 4) NOT NULL,

                         metadata            JSONB NOT NULL DEFAULT '{}'::jsonb,

                         reverses_entry_id   UUID,

                         amount              NUMERIC(19, 4) NOT NULL,

                         created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                         CONSTRAINT fk_entries_journal_entry
                             FOREIGN KEY (journal_entry_id)
                                 REFERENCES journal_entries (id),

                         CONSTRAINT fk_entries_account
                             FOREIGN KEY (entry_account_id)
                                 REFERENCES entry_accounts (id),

                         CONSTRAINT fk_entries_source_entry
                             FOREIGN KEY (source_entry_id)
                                 REFERENCES entries (id),

                         CONSTRAINT fk_entries_previous_entry
                             FOREIGN KEY (previous_entry_id)
                                 REFERENCES entries (id),

                         CONSTRAINT fk_entries_reverses_entry
                             FOREIGN KEY (reverses_entry_id)
                                 REFERENCES entries (id),

                         CONSTRAINT chk_entries_direction
                             CHECK (entry_direction IN (
                                                        'CREDIT',
                                                        'DEBIT'
                                 )),

                         CONSTRAINT chk_entries_owner_type
                             CHECK (account_owner_type IN (
                                                           'CUSTOMER',
                                                           'MERCHANT',
                                                           'INTERNAL'
                                 )),

                         CONSTRAINT chk_entries_amount
                             CHECK (amount > 0),

                         CONSTRAINT chk_entries_balance_before
                             CHECK (balance_before >= 0),

                         CONSTRAINT chk_entries_balance_after
                             CHECK (balance_after >= 0)
);


-- ============================================================
-- INDEXES
-- ============================================================

CREATE INDEX idx_journal_entries_source_event_id
    ON journal_entries (source_event_id);

CREATE INDEX idx_journal_entries_source_tx_id
    ON journal_entries (source_tx_id);

CREATE INDEX idx_journal_entries_source_account
    ON journal_entries (source_entry_account_id);

CREATE INDEX idx_journal_entries_destination_account
    ON journal_entries (destination_entry_account_id);

CREATE INDEX idx_journal_entries_status
    ON journal_entries (status);

CREATE INDEX idx_entries_journal_entry_id
    ON entries (journal_entry_id);

CREATE INDEX idx_entries_entry_account_id
    ON entries (entry_account_id);

CREATE INDEX idx_entries_source_entry_id
    ON entries (source_entry_id);

CREATE INDEX idx_entries_previous_entry_id
    ON entries (previous_entry_id);

CREATE INDEX idx_entries_reverses_entry_id
    ON entries (reverses_entry_id);

CREATE INDEX idx_entries_account_created_at
    ON entries (entry_account_id, created_at);

CREATE INDEX idx_entry_accounts_owner_reference
    ON entry_accounts (owner_reference_id);

CREATE INDEX idx_entry_accounts_category
    ON entry_accounts (category);


CREATE OR REPLACE FUNCTION update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER trg_entries_updated_at
    BEFORE UPDATE ON entries
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at();