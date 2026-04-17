package br.tec.dev2b.app.servico.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AtualizarServicoDto {
    private String nome;
    private String tipo;
    private String categoria;
    private String descricao;
    private String tipoComissao;
    private Integer duracaoMinutos;
    private BigDecimal valor;
    private BigDecimal valorCusto;
    private BigDecimal valorNaoComissionavel;
    private Boolean ativo;
}
