package br.tec.dev2b.app.credito.dto;

import br.tec.dev2b.app.credito.model.HistoricoCredito;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class HistoricoCreditoDto {
    private UUID id;
    private UUID usuarioId;
    private UUID dadosPagamentoId;
    private Integer quantidade;
    private BigDecimal valorPago;
    private String status;
    private LocalDateTime createdAt;

    public static HistoricoCreditoDto from(HistoricoCredito h) {
        HistoricoCreditoDto dto = new HistoricoCreditoDto();
        dto.id = h.getId();
        dto.usuarioId = h.getUsuario() != null ? h.getUsuario().getId() : null;
        dto.dadosPagamentoId = h.getDadosPagamento() != null ? h.getDadosPagamento().getId() : null;
        dto.quantidade = h.getQuantidade();
        dto.valorPago = h.getValorPago();
        dto.status = h.getStatus();
        dto.createdAt = h.getCreatedAt();
        return dto;
    }
}
