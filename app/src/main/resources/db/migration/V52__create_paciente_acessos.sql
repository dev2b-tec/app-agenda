CREATE TABLE paciente_acessos (
    paciente_id UUID NOT NULL REFERENCES pacientes(id) ON DELETE CASCADE,
    usuario_id  UUID NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (paciente_id, usuario_id)
);

CREATE INDEX idx_paciente_acessos_usuario ON paciente_acessos(usuario_id);
