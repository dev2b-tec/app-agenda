ALTER TABLE pacientes
    ADD COLUMN IF NOT EXISTS usuario_id UUID,
    ADD CONSTRAINT fk_pacientes_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL;
