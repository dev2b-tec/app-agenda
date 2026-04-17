-- Rastreamento de notificações enviadas por agendamento.
-- Garante que cada tipo de mensagem automatica seja enviado apenas uma vez por agendamento.
CREATE TABLE IF NOT EXISTS notificacoes_agendamento (
    id            UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    agendamento_id UUID        NOT NULL REFERENCES agendamentos(id) ON DELETE CASCADE,
    tipo          VARCHAR(40)  NOT NULL,
    enviado_em    TIMESTAMP    NOT NULL DEFAULT now(),
    numero_destino VARCHAR(30),
    UNIQUE (agendamento_id, tipo)
);

CREATE INDEX IF NOT EXISTS idx_notif_agend_agendamento_id ON notificacoes_agendamento(agendamento_id);
