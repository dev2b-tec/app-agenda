ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS perfil_id BIGINT REFERENCES perfis(id);

-- Migrar registros existentes usando tipo_acesso
UPDATE usuarios u
SET perfil_id = p.id
FROM perfis p
WHERE p.nome = CASE u.tipo_acesso
    WHEN 'Profissional / ADM'    THEN 'PROFISSIONAL_ADM'
    WHEN 'Assistente'            THEN 'ASSISTENTE'
    WHEN 'Profissional Simples'  THEN 'PROFISSIONAL_SIMPLES'
    WHEN 'Gerente'               THEN 'GERENTE'
    WHEN 'Gerente Geral'         THEN 'GERENTE_GERAL'
    ELSE NULL
END;
