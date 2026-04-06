package br.tec.dev2b.app.servico.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AtualizarServicoDto {
    private String nome;
    private String tipo;
    private BigDecimal valor;
    private Boolean ativo;
}
