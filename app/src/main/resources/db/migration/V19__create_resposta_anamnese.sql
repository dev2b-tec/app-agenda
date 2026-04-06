CREATE TABLE resposta_anamnese (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    paciente_id     UUID NOT NULL REFERENCES pacientes(id) ON DELETE CASCADE,
    anamnese_id     UUID NOT NULL REFERENCES anamneses(id) ON DELETE CASCADE,
    profissional    VARCHAR(255),
    data            DATE NOT NULL DEFAULT CURRENT_DATE,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now(),
    UNIQUE (paciente_id, anamnese_id)
);

CREATE TABLE resposta_anamnese_itens (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    resposta_anamnese_id    UUID NOT NULL REFERENCES resposta_anamnese(id) ON DELETE CASCADE,
    pergunta_id             UUID NOT NULL REFERENCES anamnese_perguntas(id) ON DELETE CASCADE,
    opcao                   VARCHAR(20) NOT NULL DEFAULT 'NENHUM',
    texto                   TEXT,
    UNIQUE (resposta_anamnese_id, pergunta_id)
);

CREATE INDEX idx_resposta_anamnese_paciente ON resposta_anamnese(paciente_id);
