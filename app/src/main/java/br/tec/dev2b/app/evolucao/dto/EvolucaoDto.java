package br.tec.dev2b.app.evolucao.dto;

import br.tec.dev2b.app.evolucao.model.Evolucao;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class EvolucaoDto {
    private UUID id;
    private UUID pacienteId;
    private String titulo;
    private String profissional;
    private LocalDate data;
    private Boolean assinado;
    private String resumoAi;
    private String comentariosGerais;
    private String conduta;
    private String examesRealizados;
    private String prescricao;

    public static EvolucaoDto from(Evolucao e) {
        EvolucaoDto dto = new EvolucaoDto();
        dto.id = e.getId();
        dto.pacienteId = e.getPaciente() != null ? e.getPaciente().getId() : null;
        dto.titulo = e.getTitulo();
        dto.profissional = e.getProfissional();
        dto.data = e.getData();
        dto.assinado = e.getAssinado();
        dto.resumoAi = e.getResumoAi();
        dto.comentariosGerais = e.getComentariosGerais();
        dto.conduta = e.getConduta();
        dto.examesRealizados = e.getExamesRealizados();
        dto.prescricao = e.getPrescricao();
        return dto;
    }
}
