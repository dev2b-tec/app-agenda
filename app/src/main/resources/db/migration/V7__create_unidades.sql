CREATE TABLE unidades (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome                VARCHAR(255) NOT NULL,
    descricao           TEXT,
    ativa               BOOLEAN NOT NULL DEFAULT true,
    empresa_id          UUID NOT NULL REFERENCES empresas(id) ON DELETE CASCADE,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP
);

CREATE INDEX idx_unidades_empresa_id ON unidades(empresa_id);
CREATE INDEX idx_unidades_ativa ON unidades(ativa);
