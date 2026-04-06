CREATE TABLE cnae (
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id                  UUID NOT NULL REFERENCES empresas(id) ON DELETE CASCADE,
    
    -- Dados do CNAE
    codigo_cnae                 VARCHAR(20) NOT NULL,
    tipo_tributacao             VARCHAR(50) NOT NULL DEFAULT 'MUNICIPIO',
    discriminacao_servicos      TEXT,
    codigo_federal              VARCHAR(20),
    codigo_municipal            VARCHAR(20),
    
    -- Retenções
    iss_retido                  BOOLEAN DEFAULT false,
    ir_retido                   BOOLEAN DEFAULT false,
    inss_retido                 BOOLEAN DEFAULT false,
    csll_retido                 BOOLEAN DEFAULT false,
    pis_retido                  BOOLEAN DEFAULT false,
    cofins_retido               BOOLEAN DEFAULT false,
    
    -- Alíquotas
    aliquota_iss                DECIMAL(5,2) DEFAULT 0,
    aliquota_inss               DECIMAL(5,2) DEFAULT 0,
    aliquota_ir                 DECIMAL(5,2) DEFAULT 0,
    aliquota_csll               DECIMAL(5,2) DEFAULT 0,
    aliquota_pis                DECIMAL(5,2) DEFAULT 0,
    aliquota_cofins             DECIMAL(5,2) DEFAULT 0,
    
    -- Configurações
    padrao                      BOOLEAN DEFAULT false,
    ativo                       BOOLEAN DEFAULT true,
    
    created_at                  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at                  TIMESTAMP
);

CREATE INDEX idx_cnae_empresa_id ON cnae(empresa_id);
CREATE INDEX idx_cnae_codigo ON cnae(codigo_cnae);
CREATE INDEX idx_cnae_ativo ON cnae(ativo);
