package br.tec.dev2b.app.financeiro.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class AtualizarMovimentoDto {

    private String titulo;
    private String status;
    private BigDecimal valorPago;
    private LocalDate dataPagamento;
    private String metodoPagamento;
    private String observacao;
}
