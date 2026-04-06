package br.tec.dev2b.app.financeiro.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CriarMovimentoDto {

    private UUID empresaId;
    private UUID pacienteId;
    private UUID usuarioId;
    private String tipo;        // MENSALIDADE | OUTROS
    private String titulo;
    private Integer totalParcelas;
    private BigDecimal valorTotal;
    private LocalDate dataPrimeiroVencimento;
    private String observacao;
}
