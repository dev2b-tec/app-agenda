CREATE TABLE salas (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome                VARCHAR(255) NOT NULL,
    unidade_id          UUID REFERENCES unidades(id) ON DELETE SET NULL,
    ativa               BOOLEAN NOT NULL DEFAULT true,
    permitir_overbooking BOOLEAN NOT NULL DEFAULT false,
    empresa_id          UUID NOT NULL REFERENCES empresas(id) ON DELETE CASCADE,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP
);

CREATE INDEX idx_salas_empresa_id ON salas(empresa_id);
CREATE INDEX idx_salas_unidade_id ON salas(unidade_id);
CREATE INDEX idx_salas_ativa ON salas(ativa);
