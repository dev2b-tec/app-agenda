CREATE TABLE pacientes (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id              UUID NOT NULL REFERENCES empresas(id) ON DELETE CASCADE,
    
    -- Dados Básicos
    nome                    VARCHAR(255) NOT NULL,
    data_nascimento         DATE,
    telefone                VARCHAR(20),
    
    -- Gênero e Plano
    genero                  VARCHAR(20),
    plano                   VARCHAR(100),
    
    -- Carteirinha e Grupo
    numero_carteirinha      VARCHAR(50),
    grupo                   VARCHAR(100),
    
    -- Como conheceu
    como_conheceu           VARCHAR(100),
    
    -- Informações Pessoais
    rg                      VARCHAR(20),
    cpf                     VARCHAR(14),
    cep                     VARCHAR(10),
    email                   VARCHAR(255),
    logradouro              VARCHAR(255),
    numero                  VARCHAR(20),
    complemento             VARCHAR(100),
    bairro                  VARCHAR(100),
    cidade                  VARCHAR(100),
    outras_informacoes      TEXT,
    
    -- Menor de Idade
    nome_responsavel        VARCHAR(255),
    data_nascimento_resp    DATE,
    cpf_responsavel         VARCHAR(14),
    telefone_responsavel    VARCHAR(20),
    
    -- Status
    status_pagamento        VARCHAR(50) DEFAULT 'EM_ABERTO',
    sessoes                 INTEGER DEFAULT 0,
    
    created_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP
);

CREATE INDEX idx_pacientes_empresa_id ON pacientes(empresa_id);
CREATE INDEX idx_pacientes_nome ON pacientes(nome);
CREATE INDEX idx_pacientes_cpf ON pacientes(cpf);
CREATE INDEX idx_pacientes_telefone ON pacientes(telefone);
CREATE INDEX idx_pacientes_status_pagamento ON pacientes(status_pagamento);
