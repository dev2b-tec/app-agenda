ALTER TABLE servicos
    ADD COLUMN IF NOT EXISTS categoria              VARCHAR(255),
    ADD COLUMN IF NOT EXISTS descricao              TEXT,
    ADD COLUMN IF NOT EXISTS tipo_comissao          VARCHAR(50) NOT NULL DEFAULT 'NAO_GERAR',
    ADD COLUMN IF NOT EXISTS duracao_minutos        INTEGER,
    ADD COLUMN IF NOT EXISTS valor_custo            DECIMAL(10,2),
    ADD COLUMN IF NOT EXISTS valor_nao_comissionavel DECIMAL(10,2);
