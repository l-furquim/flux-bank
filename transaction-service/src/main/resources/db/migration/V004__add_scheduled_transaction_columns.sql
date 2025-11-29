ALTER TABLE transactions
ADD COLUMN IF NOT EXISTS pix_key VARCHAR(255),
ADD COLUMN IF NOT EXISTS scheduled_date DATE,
ADD COLUMN IF NOT EXISTS scheduled_time TIMESTAMP,
ADD COLUMN IF NOT EXISTS recurrence VARCHAR(50);

-- Índice para buscar transações agendadas pendentes
CREATE INDEX IF NOT EXISTS idx_transactions_scheduled
ON transactions(scheduled_date, scheduled_time)
WHERE type = 'SCHEDULED_PIX' AND status = 'SCHEDULED';

-- Índice para buscar por usuário
CREATE INDEX IF NOT EXISTS idx_transactions_payer_type
ON transactions(payer_id, type, scheduled_date)
WHERE type = 'SCHEDULED_PIX';

