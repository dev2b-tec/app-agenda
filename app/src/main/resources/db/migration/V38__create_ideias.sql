-- Tabela principal de ideias
CREATE TABLE ideias (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    numero SERIAL UNIQUE,
    titulo VARCHAR(200) NOT NULL,
    descricao TEXT,
    situacao VARCHAR(50) NOT NULL DEFAULT 'MODERACAO',
    empresa_id UUID NOT NULL,
    nome_empresa VARCHAR(255),
    ideia_legal BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Categorias da ideia (até 3)
CREATE TABLE ideia_categorias (
    ideia_id UUID NOT NULL REFERENCES ideias(id) ON DELETE CASCADE,
    categoria VARCHAR(100) NOT NULL,
    PRIMARY KEY (ideia_id, categoria)
);

-- Imagens da ideia
CREATE TABLE ideia_imagens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ideia_id UUID NOT NULL REFERENCES ideias(id) ON DELETE CASCADE,
    url VARCHAR(500) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Votos (uma empresa vota uma vez por ideia)
CREATE TABLE ideia_votos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ideia_id UUID NOT NULL REFERENCES ideias(id) ON DELETE CASCADE,
    empresa_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (ideia_id, empresa_id)
);

-- Comentários
CREATE TABLE ideia_comentarios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ideia_id UUID NOT NULL REFERENCES ideias(id) ON DELETE CASCADE,
    empresa_id UUID NOT NULL,
    nome_empresa VARCHAR(255),
    texto TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices
CREATE INDEX idx_ideias_empresa ON ideias(empresa_id);
CREATE INDEX idx_ideias_situacao ON ideias(situacao);
CREATE INDEX idx_ideias_numero ON ideias(numero);
CREATE INDEX idx_ideia_votos_ideia ON ideia_votos(ideia_id);
CREATE INDEX idx_ideia_comentarios_ideia ON ideia_comentarios(ideia_id);
