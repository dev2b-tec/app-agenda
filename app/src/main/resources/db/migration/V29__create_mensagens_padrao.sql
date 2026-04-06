-- Tabela de mensagens padrão (globais e por empresa)
CREATE TABLE mensagens_padrao (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id  UUID REFERENCES empresas(id) ON DELETE CASCADE,
    tipo        VARCHAR(40)  NOT NULL,
    texto       TEXT         NOT NULL,
    is_default  BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP,
    CONSTRAINT uq_mensagem_empresa_tipo UNIQUE (empresa_id, tipo)
);

-- Índice parcial para busca de defaults globais
CREATE INDEX idx_mensagens_padrao_default ON mensagens_padrao (tipo) WHERE empresa_id IS NULL;

-- ─── Carga dos textos padrão globais (empresa_id = NULL) ─────────────────────
INSERT INTO mensagens_padrao (tipo, texto, is_default) VALUES
(
    'CONFIRMAR_AGENDAMENTO',
    'Sua consulta com o(a) #nome_profissional# no dia #data_e_hora_agendamento# está próxima. Por favor, confirme a sua presença pelo link abaixo.' || chr(10) || chr(10) ||
    'Caso você não consiga comparecer à consulta, responda a essa mensagem nos informando. Obrigado(a)' || chr(10) || chr(10) ||
    'Link para confirmação: #link_de_confirmacao#',
    TRUE
),
(
    'REMARCACAO',
    'Você gostaria de reagendar uma consulta com #nome_profissional#, já que não compareceu em seu último agendamento?',
    TRUE
),
(
    'COBRANCA',
    'Olá #nome_paciente#! Identificamos que você possui um débito pendente. Entre em contato conosco para regularizar sua situação.',
    TRUE
),
(
    'AGRADECIMENTO',
    'Olá #nome_paciente#! Agradecemos pela sua visita. Esperamos ter atendido suas expectativas. Até a próxima!',
    TRUE
),
(
    'ANIVERSARIO',
    'Olá #nome_paciente#! Em nome da nossa clínica venho desejar um Feliz Aniversário e muitas felicidades. Que esse novo ano seja repleto de coisas positivas na sua vida! =]',
    TRUE
);
