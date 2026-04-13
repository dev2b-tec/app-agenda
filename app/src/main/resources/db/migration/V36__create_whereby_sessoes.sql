CREATE TABLE whereby_sessoes (
    id                 UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    agendamento_id     UUID         REFERENCES agendamentos(id) ON DELETE SET NULL,
    meeting_id         VARCHAR(255) NOT NULL,
    participant_id     VARCHAR(255),
    participant_name   VARCHAR(255),
    started_at         TIMESTAMP    NOT NULL,
    ended_at           TIMESTAMP,
    duracao_minutos    INT,
    created_at         TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE INDEX idx_whereby_sessoes_meeting_id     ON whereby_sessoes (meeting_id);
CREATE INDEX idx_whereby_sessoes_agendamento_id ON whereby_sessoes (agendamento_id);
CREATE INDEX idx_whereby_sessoes_open           ON whereby_sessoes (started_at) WHERE ended_at IS NULL;
