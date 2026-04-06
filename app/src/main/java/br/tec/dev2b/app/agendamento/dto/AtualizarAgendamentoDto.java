package br.tec.dev2b.app.agendamento.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class AtualizarAgendamentoDto {
    private UUID pacienteId;
    private UUID usuarioId;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private String status;
    private String sala;
    private Boolean recorrente;
    private String observacoes;
    private String cor;
    private List<AgendamentoServicoItemDto> servicos;
    private BigDecimal valorTotal;
    private BigDecimal valorRecebido;
    private LocalDate dataPagamento;
    private String metodoPagamento;
}
