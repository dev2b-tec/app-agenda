package br.tec.dev2b.app.agendamento.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class AgendamentoServicoItemDto {
    private UUID servicoId;
    private String servicoNome;
    private Integer quantidade;
    private BigDecimal valorUnitario;
}
