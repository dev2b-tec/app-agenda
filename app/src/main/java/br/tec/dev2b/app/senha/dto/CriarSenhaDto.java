package br.tec.dev2b.app.senha.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CriarSenhaDto {
    private String tipo;
    private String descricao;
    private String valor;
    private UUID usuarioId;
}
