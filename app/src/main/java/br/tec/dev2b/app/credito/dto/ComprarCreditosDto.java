package br.tec.dev2b.app.credito.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ComprarCreditosDto {
    private UUID usuarioId;
    private Integer quantidade;
    private BigDecimal valorPago;
    private CriarDadosPagamentoDto dadosPagamento;
}
