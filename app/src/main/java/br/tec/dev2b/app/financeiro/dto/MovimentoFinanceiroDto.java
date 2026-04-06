package br.tec.dev2b.app.financeiro.dto;

import br.tec.dev2b.app.financeiro.model.MovimentoFinanceiro;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MovimentoFinanceiroDto {

    private UUID id;
    private UUID empresaId;
    private UUID pacienteId;
    private String pacienteNome;
    private UUID usuarioId;
    private String usuarioNome;
    private String tipo;
    private String titulo;
    private UUID grupoId;
    private Integer numeroParcela;
    private Integer totalParcelas;
    private BigDecimal valorParcela;
    private BigDecimal valorPago;
    private LocalDate dataVencimento;
    private LocalDate dataPagamento;
    private String status;
    private String metodoPagamento;
    private String observacao;
    private UUID referenciaId;
    private LocalDateTime createdAt;

    public static MovimentoFinanceiroDto from(MovimentoFinanceiro m) {
        MovimentoFinanceiroDto dto = new MovimentoFinanceiroDto();
        dto.id = m.getId();
        dto.empresaId = m.getEmpresa() != null ? m.getEmpresa().getId() : null;
        if (m.getPaciente() != null) {
            dto.pacienteId = m.getPaciente().getId();
            dto.pacienteNome = m.getPaciente().getNome();
        }
        if (m.getUsuario() != null) {
            dto.usuarioId = m.getUsuario().getId();
            dto.usuarioNome = m.getUsuario().getNome();
        }
        dto.tipo = m.getTipo();
        dto.titulo = m.getTitulo();
        dto.grupoId = m.getGrupoId();
        dto.numeroParcela = m.getNumeroParcela();
        dto.totalParcelas = m.getTotalParcelas();
        dto.valorParcela = m.getValorParcela();
        dto.valorPago = m.getValorPago();
        dto.dataVencimento = m.getDataVencimento();
        dto.dataPagamento = m.getDataPagamento();
        dto.status = m.getStatus();
        dto.metodoPagamento = m.getMetodoPagamento();
        dto.observacao = m.getObservacao();
        dto.referenciaId = m.getReferenciaId();
        dto.createdAt = m.getCreatedAt();
        return dto;
    }
}
