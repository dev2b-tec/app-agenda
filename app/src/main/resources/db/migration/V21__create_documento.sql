CREATE TABLE documentos (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id      UUID NOT NULL REFERENCES empresas(id) ON DELETE CASCADE,
    paciente_id     UUID NOT NULL REFERENCES pacientes(id) ON DELETE CASCADE,
    usuario_id      UUID REFERENCES usuarios(id) ON DELETE SET NULL,
    titulo          VARCHAR(255) NOT NULL,
    conteudo        TEXT,
    tipo            VARCHAR(50) NOT NULL DEFAULT 'branco',
    perm_prof       BOOLEAN NOT NULL DEFAULT TRUE,
    perm_assist     BOOLEAN NOT NULL DEFAULT TRUE,
    arquivo_url     TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP
);
