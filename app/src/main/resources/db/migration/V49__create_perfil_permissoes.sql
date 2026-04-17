CREATE TABLE perfil_permissoes (
    id        BIGSERIAL    PRIMARY KEY,
    perfil_id BIGINT       NOT NULL REFERENCES perfis(id) ON DELETE CASCADE,
    permissao VARCHAR(100) NOT NULL,
    UNIQUE (perfil_id, permissao)
);

-- PROFISSIONAL_ADM (id=1)
INSERT INTO perfil_permissoes (perfil_id, permissao) VALUES
    (1, 'pacientes.read'),   (1, 'pacientes.create'),  (1, 'pacientes.update'),
    (1, 'agenda.read'),      (1, 'agenda.create'),     (1, 'agenda.update'),     (1, 'agenda.delete'),
    (1, 'financeiro.read'),  (1, 'financeiro.create'), (1, 'financeiro.update'),
    (1, 'usuarios.read'),    (1, 'usuarios.create'),   (1, 'usuarios.update'),
    (1, 'evolucoes.read'),   (1, 'evolucoes.create'),  (1, 'evolucoes.update'),
    (1, 'documentos.read'),  (1, 'documentos.create'),
    (1, 'configuracoes.read'), (1, 'configuracoes.update'),
    (1, 'relatorios.read');

-- ASSISTENTE (id=2)
INSERT INTO perfil_permissoes (perfil_id, permissao) VALUES
    (2, 'pacientes.read'),  (2, 'pacientes.create'),
    (2, 'agenda.read'),     (2, 'agenda.create'),    (2, 'agenda.update'),
    (2, 'financeiro.read');

-- PROFISSIONAL_SIMPLES (id=3)
INSERT INTO perfil_permissoes (perfil_id, permissao) VALUES
    (3, 'pacientes.read'),  (3, 'pacientes.create'),  (3, 'pacientes.update'),
    (3, 'agenda.read'),     (3, 'agenda.create'),     (3, 'agenda.update'),
    (3, 'evolucoes.read'),  (3, 'evolucoes.create'),  (3, 'evolucoes.update'),
    (3, 'documentos.read'), (3, 'documentos.create'),
    (3, 'financeiro.read');

-- GERENTE (id=4)
INSERT INTO perfil_permissoes (perfil_id, permissao) VALUES
    (4, 'pacientes.read'),   (4, 'pacientes.create'),  (4, 'pacientes.update'),  (4, 'pacientes.delete'),
    (4, 'agenda.read'),      (4, 'agenda.create'),     (4, 'agenda.update'),     (4, 'agenda.delete'),
    (4, 'financeiro.read'),  (4, 'financeiro.create'), (4, 'financeiro.update'),
    (4, 'usuarios.read'),    (4, 'usuarios.create'),   (4, 'usuarios.update'),
    (4, 'evolucoes.read'),   (4, 'evolucoes.create'),  (4, 'evolucoes.update'),
    (4, 'documentos.read'),  (4, 'documentos.create'),
    (4, 'configuracoes.read'), (4, 'configuracoes.update'),
    (4, 'relatorios.read');

-- GERENTE_GERAL (id=5)
INSERT INTO perfil_permissoes (perfil_id, permissao) VALUES
    (5, 'pacientes.read'),   (5, 'pacientes.create'),  (5, 'pacientes.update'),  (5, 'pacientes.delete'),
    (5, 'agenda.read'),      (5, 'agenda.create'),     (5, 'agenda.update'),     (5, 'agenda.delete'),
    (5, 'financeiro.read'),  (5, 'financeiro.create'), (5, 'financeiro.update'), (5, 'financeiro.delete'),
    (5, 'usuarios.read'),    (5, 'usuarios.create'),   (5, 'usuarios.update'),   (5, 'usuarios.delete'),
    (5, 'evolucoes.read'),   (5, 'evolucoes.create'),  (5, 'evolucoes.update'),  (5, 'evolucoes.delete'),
    (5, 'documentos.read'),  (5, 'documentos.create'), (5, 'documentos.delete'),
    (5, 'configuracoes.read'), (5, 'configuracoes.update'),
    (5, 'relatorios.read'),
    (5, 'empresa.update');
