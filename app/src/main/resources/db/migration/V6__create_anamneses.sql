CREATE TABLE anamneses (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    titulo              VARCHAR(255) NOT NULL,
    empresa_id          UUID NOT NULL REFERENCES empresas(id) ON DELETE CASCADE,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP
);

CREATE TABLE anamnese_perguntas (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    anamnese_id         UUID NOT NULL REFERENCES anamneses(id) ON DELETE CASCADE,
    texto               TEXT NOT NULL,
    tipo_resposta       VARCHAR(50) NOT NULL DEFAULT 'AMBOS',
    ordem               INTEGER NOT NULL DEFAULT 0,
    ativa               BOOLEAN NOT NULL DEFAULT true,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP
);

CREATE INDEX idx_anamneses_empresa_id ON anamneses(empresa_id);
CREATE INDEX idx_anamnese_perguntas_anamnese_id ON anamnese_perguntas(anamnese_id);
CREATE INDEX idx_anamnese_perguntas_ordem ON anamnese_perguntas(anamnese_id, ordem);
