ALTER TABLE agendamentos
    ADD COLUMN IF NOT EXISTS valor_total      NUMERIC(10, 2) DEFAULT 0,
    ADD COLUMN IF NOT EXISTS valor_recebido   NUMERIC(10, 2) DEFAULT 0,
    ADD COLUMN IF NOT EXISTS data_pagamento   DATE,
    ADD COLUMN IF NOT EXISTS metodo_pagamento VARCHAR(50);

CREATE TABLE agendamento_servicos (
    id               UUID          NOT NULL DEFAULT gen_random_uuid(),
    agendamento_id   UUID          NOT NULL,
    servico_id       UUID,
    servico_nome     VARCHAR(255)  NOT NULL,
    quantidade       INTEGER       NOT NULL DEFAULT 1,
    valor_unitario   NUMERIC(10, 2) NOT NULL DEFAULT 0,

    CONSTRAINT pk_agendamento_servicos           PRIMARY KEY (id),
    CONSTRAINT fk_agendamento_servicos_agenda    FOREIGN KEY (agendamento_id) REFERENCES agendamentos(id)  ON DELETE CASCADE,
    CONSTRAINT fk_agendamento_servicos_servico   FOREIGN KEY (servico_id)     REFERENCES servicos(id)      ON DELETE SET NULL
);

CREATE INDEX idx_agendamento_servicos_agendamento_id ON agendamento_servicos(agendamento_id);
