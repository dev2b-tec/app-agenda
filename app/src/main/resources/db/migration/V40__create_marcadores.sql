CREATE TABLE marcadores (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tipo       VARCHAR(50)  NOT NULL,
    cor        VARCHAR(20)  NOT NULL,
    empresa_id UUID NOT NULL REFERENCES empresas(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE UNIQUE INDEX idx_marcadores_empresa_tipo ON marcadores(empresa_id, tipo);
CREATE INDEX idx_marcadores_empresa_id ON marcadores(empresa_id);
