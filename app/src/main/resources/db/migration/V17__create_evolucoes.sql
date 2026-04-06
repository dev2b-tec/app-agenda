CREATE TABLE evolucoes (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    paciente_id         UUID NOT NULL REFERENCES pacientes(id) ON DELETE CASCADE,

    titulo              VARCHAR(255),
    profissional        VARCHAR(255) NOT NULL,
    data                DATE NOT NULL,
    assinado            BOOLEAN NOT NULL DEFAULT FALSE,

    resumo_ai           TEXT,
    comentarios_gerais  TEXT,
    conduta             TEXT,
    exames_realizados   TEXT,
    prescricao          TEXT,

    created_at          TIMESTAMP NOT NULL DEFAULT now(),
    updated_at          TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_evolucoes_paciente_id ON evolucoes(paciente_id);
