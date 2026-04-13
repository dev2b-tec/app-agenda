-- V30: Cria tabela de controle de menus ativos (Telas Principais)
-- Cada linha representa uma tela do sistema com flag ativo/inativo.
-- Para habilitar ou desabilitar uma tela, atualize a coluna `ativo`.

CREATE TABLE menus_ativos (
    id         SERIAL PRIMARY KEY,
    chave      VARCHAR(50)  NOT NULL UNIQUE,
    label      VARCHAR(100) NOT NULL,
    ativo      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ─── Dados iniciais ───────────────────────────────────────────────────────────
-- Menus desligados: Cadastros, Finanças, Vendas, Suprimentos, Serviços
-- (Logística fica implicitamente desligada pois Suprimentos e Serviços = false)

INSERT INTO menus_ativos (chave, label, ativo) VALUES
    ('dashboard',     'Dashboard',     TRUE),
    ('calendario',    'Calendário',    TRUE),
    ('cadastros',     'Cadastros',     FALSE),
    ('clientes',      'Clientes',      TRUE),
    ('usuarios',      'Usuários',      TRUE),
    ('avisos',        'Avisos',        TRUE),
    ('chat',          'Chat',          TRUE),
    ('marketing',     'Marketing',     TRUE),
    ('financeiro',    'Financeiro',    TRUE),
    ('financas',      'Finanças',      FALSE),
    ('vendas',        'Vendas',        FALSE),
    ('suprimentos',   'Suprimentos',   FALSE),
    ('servicos',      'Serviços',      FALSE),
    ('configuracoes', 'Configurações', TRUE);
