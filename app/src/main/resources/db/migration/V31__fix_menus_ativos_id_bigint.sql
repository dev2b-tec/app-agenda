-- V31: Corrige o tipo da coluna id em menus_ativos
-- SERIAL cria INTEGER, mas a entidade JPA usa Long (BIGINT).
-- Convertemos para BIGINT mantendo a sequência existente.

ALTER TABLE menus_ativos ALTER COLUMN id TYPE BIGINT;
