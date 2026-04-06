package br.tec.dev2b.app.senha.dto;

import lombok.Data;

@Data
public class AtualizarSenhaDto {
    private String tipo;
    private String descricao;
    private String valor;
}
