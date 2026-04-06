ALTER TABLE usuarios
    ADD COLUMN IF NOT EXISTS genero              VARCHAR(50),
    ADD COLUMN IF NOT EXISTS duracao_sessao      INTEGER,
    ADD COLUMN IF NOT EXISTS periodo_minimo      VARCHAR(100),
    ADD COLUMN IF NOT EXISTS periodo_maximo      VARCHAR(100),
    ADD COLUMN IF NOT EXISTS tempo_antecedencia  VARCHAR(100),
    ADD COLUMN IF NOT EXISTS disponivel          BOOLEAN NOT NULL DEFAULT TRUE;
