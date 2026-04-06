ALTER TABLE usuarios
    ADD COLUMN IF NOT EXISTS telefone_comercial VARCHAR(50),
    ADD COLUMN IF NOT EXISTS observacoes        TEXT;
