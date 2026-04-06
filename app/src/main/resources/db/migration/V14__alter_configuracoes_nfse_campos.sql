-- Aumentar tamanho dos campos que podem receber valores maiores da API ReceitaWS
ALTER TABLE configuracoes_nfse 
    ALTER COLUMN telefone TYPE VARCHAR(50),
    ALTER COLUMN data_abertura TYPE VARCHAR(50),
    ALTER COLUMN numero TYPE VARCHAR(50);
