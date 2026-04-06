package br.tec.dev2b.app.servico.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CriarServicoDto {
    private String nome;
    private String tipo;
    private BigDecimal valor;
    private UUID empresaId;
}
