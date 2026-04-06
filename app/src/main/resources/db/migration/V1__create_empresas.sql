CREATE TABLE empresas (
    id                       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome_comercial           VARCHAR(255)  NOT NULL,
    logo_url                 TEXT,
    telefone_comercial       VARCHAR(20),
    cep                      VARCHAR(10),
    logradouro               VARCHAR(255),
    numero                   VARCHAR(20),
    complemento              VARCHAR(100),
    bairro                   VARCHAR(100),
    cidade                   VARCHAR(100),
    duracao_sessao_minutos   INT,
    created_at               TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at               TIMESTAMP
);
