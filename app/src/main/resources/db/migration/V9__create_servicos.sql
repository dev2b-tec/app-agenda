CREATE TABLE servicos (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome                VARCHAR(255) NOT NULL,
    tipo                VARCHAR(50) NOT NULL DEFAULT 'GERAL',
    valor               DECIMAL(10,2),
    ativo               BOOLEAN NOT NULL DEFAULT true,
    empresa_id          UUID NOT NULL REFERENCES empresas(id) ON DELETE CASCADE,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP
);

CREATE INDEX idx_servicos_empresa_id ON servicos(empresa_id);
CREATE INDEX idx_servicos_tipo ON servicos(tipo);
CREATE INDEX idx_servicos_ativo ON servicos(ativo);
