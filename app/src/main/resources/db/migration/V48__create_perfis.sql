CREATE TABLE perfis (
    id        BIGSERIAL    PRIMARY KEY,
    nome      VARCHAR(60)  NOT NULL UNIQUE,
    descricao VARCHAR(255)
);

INSERT INTO perfis (nome, descricao) VALUES
    ('PROFISSIONAL_ADM',    'Profissional com acesso administrativo completo'),
    ('ASSISTENTE',          'Assistente com acesso básico de agenda e pacientes'),
    ('PROFISSIONAL_SIMPLES','Profissional com acesso restrito aos próprios atendimentos'),
    ('GERENTE',             'Gerente com acesso amplo exceto configurações de empresa'),
    ('GERENTE_GERAL',       'Gerente geral com acesso total ao sistema');
