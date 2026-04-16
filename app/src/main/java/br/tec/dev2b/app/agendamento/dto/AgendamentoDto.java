package br.tec.dev2b.app.agendamento.dto;

import br.tec.dev2b.app.agendamento.model.Agendamento;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class AgendamentoDto {
    private UUID id;
    private UUID empresaId;
    private String tipo;
    private String titulo;
    private UUID pacienteId;
    private String pacienteNome;
    private UUID usuarioId;
    private String usuarioNome;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private String status;
    private String sala;
    private Boolean recorrente;
    private String observacoes;
    private String cor;
    private List<AgendamentoServicoDto> servicos;
    private BigDecimal valorTotal;
    private BigDecimal valorRecebido;
    private LocalDate dataPagamento;
    private String metodoPagamento;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String wherebyMeetingId;
    private String wherebyHostUrl;
    private String wherebyViewerUrl;
    private Boolean atendimentoRemoto;
    private String pacientePlano;
    private Integer pacienteSessoes;
    private java.time.LocalDate pacienteDataNascimento;

    public static AgendamentoDto from(Agendamento a) {
        AgendamentoDto dto = new AgendamentoDto();
        dto.id           = a.getId();
        dto.empresaId    = a.getEmpresa() != null ? a.getEmpresa().getId() : null;
        dto.tipo         = a.getTipo();
        dto.titulo       = a.getTitulo();
        dto.pacienteId   = a.getPaciente() != null ? a.getPaciente().getId() : null;
        dto.pacienteNome = a.getPacienteNome();
        dto.usuarioId    = a.getUsuario() != null ? a.getUsuario().getId() : null;
        dto.usuarioNome  = a.getUsuarioNome();
        dto.inicio       = a.getInicio();
        dto.fim          = a.getFim();
        dto.status       = a.getStatus();
        dto.sala         = a.getSala();
        dto.recorrente   = a.getRecorrente();
        dto.observacoes  = a.getObservacoes();
        dto.cor          = a.getCor();
        dto.servicos      = a.getServicos() != null
                ? a.getServicos().stream().map(AgendamentoServicoDto::from).collect(Collectors.toList())
                : new ArrayList<>();
        dto.valorTotal      = a.getValorTotal();
        dto.valorRecebido   = a.getValorRecebido();
        dto.dataPagamento   = a.getDataPagamento();
        dto.metodoPagamento = a.getMetodoPagamento();
        dto.createdAt         = a.getCreatedAt();
        dto.updatedAt         = a.getUpdatedAt();
        dto.wherebyMeetingId  = a.getWherebyMeetingId();
        dto.wherebyHostUrl    = a.getWherebyHostUrl();
        dto.wherebyViewerUrl  = a.getWherebyViewerUrl();
        dto.atendimentoRemoto = a.getAtendimentoRemoto();
        dto.pacientePlano     = a.getPaciente() != null ? a.getPaciente().getPlano() : null;
        dto.pacienteSessoes   = a.getPaciente() != null ? a.getPaciente().getSessoes() : null;
        dto.pacienteDataNascimento = a.getPaciente() != null ? a.getPaciente().getDataNascimento() : null;
        return dto;
    }
}
