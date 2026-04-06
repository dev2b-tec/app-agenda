-- Tabela de dados de pagamento (reutilizável)
CREATE TABLE dados_pagamento (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id          UUID NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    
    -- Informações do Pagador
    nome_completo       VARCHAR(255) NOT NULL,
    email               VARCHAR(255) NOT NULL,
    pais                VARCHAR(3) NOT NULL DEFAULT 'BRA',
    numero_telefone     VARCHAR(20),
    tipo_documento      VARCHAR(10) NOT NULL,
    numero_documento    VARCHAR(20) NOT NULL,
    
    -- Endereço de Cobrança
    logradouro          VARCHAR(255),
    complemento         VARCHAR(100),
    bairro              VARCHAR(100),
    cep                 VARCHAR(10),
    
    -- Método de Pagamento
    metodo_pagamento    VARCHAR(50) NOT NULL,
    nome_cartao         VARCHAR(255),
    numero_cartao       VARCHAR(20),
    cvv                 VARCHAR(4),
    expiracao           VARCHAR(7),
    
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP
);

-- Tabela de histórico de compras de créditos
CREATE TABLE historico_creditos (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id          UUID NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    dados_pagamento_id  UUID REFERENCES dados_pagamento(id) ON DELETE SET NULL,
    
    quantidade          INTEGER NOT NULL,
    valor_pago          DECIMAL(10,2) NOT NULL,
    status              VARCHAR(50) NOT NULL DEFAULT 'PENDENTE',
    
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP
);

CREATE INDEX idx_dados_pagamento_usuario_id ON dados_pagamento(usuario_id);
CREATE INDEX idx_historico_creditos_usuario_id ON historico_creditos(usuario_id);
CREATE INDEX idx_historico_creditos_status ON historico_creditos(status);
CREATE INDEX idx_historico_creditos_created_at ON historico_creditos(created_at DESC);
