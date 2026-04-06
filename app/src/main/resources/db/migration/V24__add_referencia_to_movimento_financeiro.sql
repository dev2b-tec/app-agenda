ALTER TABLE movimento_financeiro
    ADD COLUMN IF NOT EXISTS referencia_id UUID;

CREATE INDEX IF NOT EXISTS idx_movimento_referencia ON movimento_financeiro(referencia_id);
