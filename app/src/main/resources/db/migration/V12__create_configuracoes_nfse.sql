CREATE TABLE configuracoes_nfse (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id              UUID NOT NULL REFERENCES empresas(id) ON DELETE CASCADE,
    
    -- Dados da Receita Federal
    cnpj                    VARCHAR(18) NOT NULL,
    razao_social            VARCHAR(255),
    nome_fantasia           VARCHAR(255),
    data_abertura           VARCHAR(20),
    situacao                VARCHAR(100),
    
    -- Endereço
    uf                      VARCHAR(2),
    municipio               VARCHAR(100),
    logradouro              VARCHAR(255),
    numero                  VARCHAR(20),
    complemento             VARCHAR(100),
    bairro                  VARCHAR(100),
    cep                     VARCHAR(10),
    
    -- Contato
    email                   VARCHAR(255),
    telefone                VARCHAR(20),
    
    -- Flags
    validado_receita        BOOLEAN NOT NULL DEFAULT false,
    
    created_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP
);

CREATE INDEX idx_configuracoes_nfse_empresa_id ON configuracoes_nfse(empresa_id);
CREATE INDEX idx_configuracoes_nfse_cnpj ON configuracoes_nfse(cnpj);
