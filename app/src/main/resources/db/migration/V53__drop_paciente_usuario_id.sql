-- Remove coluna usuario_id de pacientes.
-- O controle de acesso por usuário passou a ser feito
-- exclusivamente pela tabela paciente_acessos.
ALTER TABLE pacientes DROP COLUMN IF EXISTS usuario_id;
