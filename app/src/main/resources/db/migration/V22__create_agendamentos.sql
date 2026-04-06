CREATE TABLE agendamentos (
    id              UUID         NOT NULL DEFAULT gen_random_uuid(),
    empresa_id      UUID         NOT NULL,
    paciente_id     UUID,
    usuario_id      UUID,
    paciente_nome   VARCHAR(255),
    usuario_nome    VARCHAR(255),
    inicio          TIMESTAMP    NOT NULL,
    fim             TIMESTAMP    NOT NULL,
    status          VARCHAR(50)  NOT NULL DEFAULT 'Aguardando',
    sala            VARCHAR(100),
    recorrente      BOOLEAN      NOT NULL DEFAULT FALSE,
    observacoes     TEXT,
    cor             VARCHAR(20)  DEFAULT 'purple',
    created_at      TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT now(),

    CONSTRAINT pk_agendamentos            PRIMARY KEY (id),
    CONSTRAINT fk_agendamentos_empresa    FOREIGN KEY (empresa_id)  REFERENCES empresas(id)  ON DELETE CASCADE,
    CONSTRAINT fk_agendamentos_paciente   FOREIGN KEY (paciente_id) REFERENCES pacientes(id) ON DELETE SET NULL,
    CONSTRAINT fk_agendamentos_usuario    FOREIGN KEY (usuario_id)  REFERENCES usuarios(id)  ON DELETE SET NULL
);

CREATE INDEX idx_agendamentos_empresa_id ON agendamentos(empresa_id);
CREATE INDEX idx_agendamentos_inicio     ON agendamentos(inicio);
