CREATE TABLE configuracoes_mensagens (
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id                  UUID NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    
    -- Número WhatsApp
    numero_whatsapp             VARCHAR(20),
    
    -- Permissões
    permitir_profissionais      BOOLEAN NOT NULL DEFAULT false,
    
    -- SMS
    envio_sms_automatico        BOOLEAN NOT NULL DEFAULT false,
    enviar_com_risco_falta      BOOLEAN NOT NULL DEFAULT false,
    horario_disparo             VARCHAR(20),
    
    -- Mensagem de Lembrete
    mensagem_lembrete           TEXT,
    
    created_at                  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at                  TIMESTAMP
);

CREATE INDEX idx_configuracoes_mensagens_usuario_id ON configuracoes_mensagens(usuario_id);
