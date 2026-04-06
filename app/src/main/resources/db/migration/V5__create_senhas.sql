CREATE TABLE senhas (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tipo                VARCHAR(50) NOT NULL,
    descricao           TEXT,
    valor               TEXT NOT NULL,
    usuario_id          UUID NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP
);

CREATE INDEX idx_senhas_usuario_id ON senhas(usuario_id);
CREATE INDEX idx_senhas_tipo ON senhas(tipo);
