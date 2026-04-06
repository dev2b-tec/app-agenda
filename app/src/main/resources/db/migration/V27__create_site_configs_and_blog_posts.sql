CREATE TABLE IF NOT EXISTS site_configs (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id      UUID        NOT NULL UNIQUE REFERENCES usuarios(id) ON DELETE CASCADE,
    link_slug       VARCHAR(100),
    cor_principal   VARCHAR(20),
    disponibilidade VARCHAR(20) NOT NULL DEFAULT 'ativo',
    logo_url        VARCHAR(500),
    perfil_url      VARCHAR(500),
    banner_url      VARCHAR(500),
    sobre_mim       TEXT,
    servicos        TEXT,
    slogan          VARCHAR(255),
    titulo_pagina   VARCHAR(255),
    whatsapp        VARCHAR(100),
    email_contato   VARCHAR(255),
    telefone        VARCHAR(50),
    website_link    VARCHAR(500),
    instagram       VARCHAR(255),
    facebook        VARCHAR(255),
    linkedin        VARCHAR(255),
    youtube         VARCHAR(255),
    created_at      TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS blog_posts (
    id               UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    site_config_id   UUID        NOT NULL REFERENCES site_configs(id) ON DELETE CASCADE,
    titulo           VARCHAR(500) NOT NULL,
    data_publicacao  DATE,
    status           VARCHAR(20) NOT NULL DEFAULT 'RASCUNHO',
    conteudo         TEXT,
    imagem_url       VARCHAR(500),
    created_at       TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP   NOT NULL DEFAULT NOW()
);
