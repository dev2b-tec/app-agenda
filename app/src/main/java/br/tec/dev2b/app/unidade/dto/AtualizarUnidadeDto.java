package br.tec.dev2b.app.unidade.dto;

import lombok.Data;

@Data
public class AtualizarUnidadeDto {
    private String nome;
    private String descricao;
    private Boolean ativa;
}
