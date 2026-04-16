CREATE TABLE IF NOT EXISTS integracoes (
    id          UUID         NOT NULL DEFAULT gen_random_uuid(),
    empresa_id  UUID         NOT NULL,
    tipo        VARCHAR(50)  NOT NULL,
    ativo       BOOLEAN      NOT NULL DEFAULT false,
    configuracao JSONB        NOT NULL DEFAULT '{}',
    criado_em   TIMESTAMP    NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMP  NOT NULL DEFAULT now(),
    CONSTRAINT pk_integracoes PRIMARY KEY (id),
    CONSTRAINT uq_integracoes_empresa_tipo UNIQUE (empresa_id, tipo),
    CONSTRAINT fk_integracoes_empresa FOREIGN KEY (empresa_id) REFERENCES empresas(id) ON DELETE CASCADE
);

CREATE INDEX idx_integracoes_empresa_id ON integracoes(empresa_id);
