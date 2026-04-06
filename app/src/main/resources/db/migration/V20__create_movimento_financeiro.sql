CREATE TABLE movimento_financeiro (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id        UUID NOT NULL REFERENCES empresas(id),
    paciente_id       UUID REFERENCES pacientes(id),
    usuario_id        UUID REFERENCES usuarios(id),
    tipo              VARCHAR(30) NOT NULL DEFAULT 'MENSALIDADE',
    titulo            VARCHAR(255) NOT NULL,
    grupo_id          UUID NOT NULL,
    numero_parcela    INT NOT NULL DEFAULT 1,
    total_parcelas    INT NOT NULL DEFAULT 1,
    valor_parcela     NUMERIC(12, 2) NOT NULL DEFAULT 0,
    valor_pago        NUMERIC(12, 2) NOT NULL DEFAULT 0,
    data_vencimento   DATE NOT NULL,
    data_pagamento    DATE,
    status            VARCHAR(20) NOT NULL DEFAULT 'EM_ABERTO',
    metodo_pagamento  VARCHAR(50),
    observacao        TEXT,
    created_at        TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_movimento_empresa ON movimento_financeiro(empresa_id);
CREATE INDEX idx_movimento_paciente ON movimento_financeiro(paciente_id);
CREATE INDEX idx_movimento_grupo ON movimento_financeiro(grupo_id);
CREATE INDEX idx_movimento_status ON movimento_financeiro(status);
