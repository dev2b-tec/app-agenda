package br.tec.dev2b.app.servico.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CriarServicoDto {
    private String nome;
    private String tipo;
    private String categoria;
    private String descricao;
    private String tipoComissao;
    private Integer duracaoMinutos;
    private BigDecimal valor;
    private BigDecimal valorCusto;
    private BigDecimal valorNaoComissionavel;
    private UUID empresaId;
}
