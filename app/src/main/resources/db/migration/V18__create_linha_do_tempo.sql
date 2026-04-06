CREATE TABLE linha_do_tempo (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    paciente_id     UUID NOT NULL REFERENCES pacientes(id) ON DELETE CASCADE,

    tipo            VARCHAR(50)  NOT NULL,
    titulo          VARCHAR(255),
    profissional    VARCHAR(255),
    referencia_id   UUID,

    data            DATE NOT NULL,
    hora            TIME,
    assinado        BOOLEAN NOT NULL DEFAULT FALSE,

    created_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_linha_do_tempo_paciente_id ON linha_do_tempo(paciente_id);
