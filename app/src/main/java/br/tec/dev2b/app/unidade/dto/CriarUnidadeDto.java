package br.tec.dev2b.app.unidade.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CriarUnidadeDto {
    private String nome;
    private String descricao;
    private UUID empresaId;
}
