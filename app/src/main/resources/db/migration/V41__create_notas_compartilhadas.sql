CREATE TABLE IF NOT EXISTS notas_compartilhadas (
    id          UUID         NOT NULL DEFAULT gen_random_uuid(),
    paciente_id UUID         NOT NULL,
    autor_id    UUID         NOT NULL,
    autor_nome  VARCHAR(255) NOT NULL,
    titulo      VARCHAR(255),
    texto       TEXT         NOT NULL,
    cor         VARCHAR(50)  NOT NULL DEFAULT '#FEF9C3',
    criado_em   TIMESTAMP    NOT NULL DEFAULT now(),
    CONSTRAINT pk_notas_compartilhadas PRIMARY KEY (id),
    CONSTRAINT fk_nc_paciente FOREIGN KEY (paciente_id) REFERENCES pacientes(id) ON DELETE CASCADE,
    CONSTRAINT fk_nc_autor    FOREIGN KEY (autor_id)    REFERENCES usuarios(id)  ON DELETE CASCADE
);
