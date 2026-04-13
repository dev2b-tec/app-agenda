ALTER TABLE usuarios
    ADD COLUMN IF NOT EXISTS descricao_atuacao   TEXT,
    ADD COLUMN IF NOT EXISTS cursos_certificacoes TEXT,
    ADD COLUMN IF NOT EXISTS faixa_preco         VARCHAR(100),
    ADD COLUMN IF NOT EXISTS modo_atendimento    VARCHAR(50),
    ADD COLUMN IF NOT EXISTS aceita_convenio     BOOLEAN,
    ADD COLUMN IF NOT EXISTS instagram           VARCHAR(255),
    ADD COLUMN IF NOT EXISTS facebook            VARCHAR(255),
    ADD COLUMN IF NOT EXISTS linkedin            VARCHAR(255),
    ADD COLUMN IF NOT EXISTS youtube             VARCHAR(255),
    ADD COLUMN IF NOT EXISTS website_link        VARCHAR(255),
    ADD COLUMN IF NOT EXISTS whatsapp            VARCHAR(50),
    ADD COLUMN IF NOT EXISTS publicado_buscador  BOOLEAN NOT NULL DEFAULT FALSE;
