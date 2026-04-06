package br.tec.dev2b.app.agendamento.dto;

import br.tec.dev2b.app.agendamento.model.AgendamentoServico;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class AgendamentoServicoDto {
    private UUID id;
    private UUID servicoId;
    private String servicoNome;
    private Integer quantidade;
    private BigDecimal valorUnitario;

    public static AgendamentoServicoDto from(AgendamentoServico s) {
        AgendamentoServicoDto dto = new AgendamentoServicoDto();
        dto.id            = s.getId();
        dto.servicoId     = s.getServico() != null ? s.getServico().getId() : null;
        dto.servicoNome   = s.getServicoNome();
        dto.quantidade    = s.getQuantidade();
        dto.valorUnitario = s.getValorUnitario();
        return dto;
    }
}
