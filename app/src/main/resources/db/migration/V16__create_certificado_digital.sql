CREATE TABLE certificado_digital (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id              UUID NOT NULL REFERENCES empresas(id) ON DELETE CASCADE,
    
    -- Dados do certificado
    nome_arquivo            VARCHAR(255) NOT NULL,
    url_minio               VARCHAR(500) NOT NULL,
    senha_hash              VARCHAR(255) NOT NULL,
    
    -- Metadados
    tamanho_bytes           BIGINT,
    data_validade           DATE,
    ativo                   BOOLEAN DEFAULT true,
    
    created_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP
);

CREATE INDEX idx_certificado_empresa_id ON certificado_digital(empresa_id);
CREATE INDEX idx_certificado_ativo ON certificado_digital(ativo);
